# Funciones del modelo

[← Volver al índice](../README.md)

## 1. Convención documental

Cada función debe registrar:

- agente propietario;
- firma;
- objetivo;
- argumentos;
- retorno;
- precondiciones;
- variables modificadas;
- dependencias;
- estado;
- problemas conocidos;
- reemplazo objetivo.

## 2. Funciones de Planta

### `producir()`

**Estado:** implementada.

**Objetivo:** ingresar la producción del día del plan, **completa**: la capacidad de la planta es un umbral de lectura y no un tope, porque el producto ya está cosechado y no se descarta (ADR-048).

**Modifica:** producción acumulada y población `lotes`. El stock de la planta ya no es una variable: cada ingreso agrega una capa al lote comercial abierto (ADR-023, ADR-047).

### `getStock(TipoProducto producto)`

**Retorno:** `double`.

**Regla:** deriva el saldo de las capas de la planta (`Main.inventario.stock("PLANTA", producto)`).

`agregarStock` y `retirarStock` se eliminaron: la planta no tiene un saldo propio que mantener. Ingresar es crear una capa; retirar es moverla (ADR-023).

## 3. Funciones de LoteProducto

### Implementadas hoy

El lote ya no guarda saldos propios. Sus tres funciones derivan del inventario (ADR-023):

- `getToneladasDisponibles()`: suma de las capas del lote, en cualquier ubicación.
- `getToneladasReservadas()`: suma de las reservas de esas capas.
- `getToneladasLibres()`: la diferencia.

`toneladasIniciales` es lo producido y no vuelve a modificarse.

### Funciones sobre capas

Viven en las clases Java `Capa` e `Inventario`, no en el agente (ADR-021, ADR-030), porque el presupuesto de tipos de agente de PLE está agotado. `Main.inventario` es la única instancia:

- `ingresar(idLote, producto, idUbicacion, toneladas, diaIngreso, diaProduccion)`: acumula sobre la capa del mismo lote, ubicación y día, o crea una nueva.
- `stock / reservado / libre (idUbicacion, producto)`: saldos derivados por ubicación.
- `stockLote / reservadoLote (idLote)`: saldos derivados por lote.
- `retirarLibre(idUbicacion, producto, toneladas)`: consume capas en FIFO por `diaIngreso`, sólo toneladas libres, y elimina las capas que quedan en cero.
- `mover(origen, destino, producto, toneladas, dia)` y `moverLote(idLote, ...)`: retiro parcial en el origen e ingreso en el destino con el día de ingreso nuevo.
- `reservar(idUbicacion, producto, toneladas, codigoPedido, dia)`, `liberarReserva(codigoPedido)` y `despachar(idUbicacion, producto, toneladas, codigoPedido)`.
- `validar()`: invariantes de las capas; `Main.validarInventario()` la corre cada día.

Todas devuelven las toneladas efectivamente movidas, reservadas o despachadas, de modo que quien llama puede detectar un cumplimiento parcial en lugar de asumirlo.

### Funciones objetivo pendientes

- reserva contra producción futura (compromiso, ADR-024);
- asociación de la reserva al contenedor (ADR-025);
- `getToneladasFisicasTotales()`;
- `getToneladasReservadasTotales()`;
- `validarIntegridadUbicaciones()`;
- `estaAbiertoParaProduccion(...)`;
- `registrarProduccion(...)`.

## 4. Funciones de Main

### `crearLoteEnPlanta(TipoProducto producto, double toneladas, Agent origen)`

**Estado:** implementada (fase 2 del rediseño comercial, ADR-047).

**Regla:** busca el lote comercial abierto compatible con `buscarLoteComercialAbierto(producto, cliente, calidad)` y le agrega una capa nueva, acumulando en `toneladasIniciales`. Sólo crea una identidad nueva cuando el lote compatible ya está cerrado.

### `transferirToneladasLote(LoteProducto lote, Deposito destino, double toneladas)`

**Estado:** implementada (fase 4). Reemplaza a `transferirLoteCompleto()`, que se eliminó.

**Retorno:** `double`, las toneladas efectivamente movidas. Puede ser menos que las pedidas, y quien llama debe usar ese valor —no el pedido— para descontar su pendiente.

**Comportamiento:** acota el movimiento a lo que el lote tiene libre en `PLANTA` y a lo que entra en el depósito, mueve las capas con `Inventario.moverLote(...)`, imputa el flete sobre lo movido y actualiza la ubicación del lote.

**Contrato transaccional:** no hay reversión porque no hay estado intermedio. El movimiento se acota *antes* de tocar el inventario, y `moverLote` retira e ingresa exactamente la misma cantidad dentro de la misma lista de capas. El esquema anterior —retirar de un saldo, agregar a otro y reponer si el segundo falla— sólo era necesario cuando había dos saldos independientes.

**Precondiciones:** lote y destino no nulos; toneladas positivas; depósito habilitado. Un depósito lleno o un lote sin saldo libre no son errores: devuelven 0.

### `actualizarUbicacionLote(LoteProducto lote)`

**Estado:** implementada (fase 4).

**Regla:** `ubicacionActual` pasa a ser la ubicación donde el lote tiene más saldo (`Inventario.ubicacionPrincipalDeLote`), porque con transferencia parcial el lote está en varios lugares a la vez. `estado` acompaña: `EN_PLANTA` mientras el grueso siga en planta, `EN_DEPOSITO` cuando ya no. Ninguno de los dos es un saldo: el saldo se le pregunta siempre al inventario.

### `transferirProductoADepositos(TipoProducto producto, double toneladasObjetivo, boolean priorizarEspacio)`

**Estado:** implementada; devuelve las toneladas realmente movidas (ADR-056).

**Regla:** toma el lote más antiguo **con saldo libre en planta** —no el más antiguo "en estado EN_PLANTA", para que un lote transferido a medias pueda terminar de salir— y le manda `min(saldo libre, pendiente)`. El objetivo se reparte entre **todos** los depósitos que puedan recibir: que en el primero entren 100 de 300 no termina la transferencia. Un destino que no pudo recibir (o que recibió menos de lo pedido) queda descartado por hoy y no se reintenta, así el recorrido siempre termina. Si al agotar los candidatos queda saldo, incrementa `transferencias_incompletas` y, con `debugPlanificacion`, imprime el diagnóstico de destinos.

**Problema resuelto:** antes, cualquier depósito que recibiera menos de lo pedido cortaba el ciclo con `break`, así que el resto del objetivo se quedaba en planta aunque hubiera espacio en otros sitios. Con las capacidades concentradas del Excel real (CASCARA sólo en RUTA9) eso dejaba producto retenido con espacio libre en la red.

### `seleccionarDeposito(TipoProducto producto, double toneladas, boolean priorizarEspacio, Set<String> excluidos)`

**Estado:** implementada; criterio por costo unitario, o por espacio en sobrecarga crítica (ADR-056).

**Regla:** entre los depósitos que pasan `motivoDescarteDeposito()` y no están en `excluidos`, elige el de menor costo estimado **por tonelada** de `min(toneladas, espacio disponible)`. Con `priorizarEspacio` —la planta sobre el umbral de sobrecarga— el criterio se invierte: gana el destino donde entra **más**, y el costo sólo desempata. El volumen a transferir no cambia por estar en sobrecarga; cambia el orden de los destinos.

### `motivoDescarteDeposito(Deposito deposito, TipoProducto producto, double toneladas)`

Devuelve el string vacío si el depósito puede recibir hoy y, si no, el motivo: `NO_HABILITADO`, `SIN_CAPACIDAD_PRODUCTO`, `SIN_ESPACIO`, `TARIFA_INEXISTENTE`, `TARIFA_SITIO_INEXISTENTE` o `SIN_FLOTA`. Es la única fuente del filtro: la selección y el diagnóstico usan la misma función, así que el diagnóstico no puede mentir sobre por qué se descartó un destino.

### `diagnosticoDepositos(TipoProducto producto, double toneladas)`

Una línea por depósito con capacidad, stock, espacio, existencia de tarifa de flete, costo estimado, si es elegible y el motivo de descarte. Se imprime con `debugPlanificacion` activo. Responde "por qué no se usó RUTA9" sin tener que instrumentar el modelo.

### Antes: `transferirLoteCompleto(LoteProducto lote, Deposito destino)`

Eliminada en la fase 4. Movía todo el saldo libre del lote y fijaba `ubicacionActual` como si el lote estuviera entero en un solo lugar.

### `revisarTransferencias()`

Evalúa umbrales de planta y solicita mover el exceso respecto del stock objetivo.

### `reservarParcialPedido(Pedido pedido, String idSitio, double toneladasObjetivo, EstrategiaLogistica circuito, boolean cruza, String motivo)`

**Estado:** implementada (ADR-055). Reemplaza a `reservarLotesParaPedido()`, que era todo o nada.

**Retorno:** `double`, las toneladas **realmente** reservadas.

**Regla:** reserva `min(objetivo, saldo pendiente del pedido, stock libre del sitio)` y **conserva** lo reservado aunque no alcance para el pedido completo. Crea una `AsignacionPedido` con identidad propia y reserva las capas con la clave `codigoPedido + "|" + idAsignacion`, no con el código del pedido: dos asignaciones del mismo pedido en el mismo sitio no se pisan. Si el inventario no entregó nada, la asignación se descarta y no queda rastro. El sitio es un `id_ubicacion` (`"PLANTA"` o el id de un depósito), y el despacho usa exactamente la misma clave, así que reserva y consumo no pueden desalinearse.

### `asignarParcialPedido(Pedido pedido)` y `asignarConPoliticaFija(Pedido pedido)`

**Regla:** un pedido se cubre con los orígenes que hagan falta, en uno o varios días, y el saldo sigue siendo demanda para los días siguientes. Con política económica los candidatos salen del evaluador (ADR-054); con política fija el orden de candidatos es el de siempre —planta primero si consolida en planta, después los depósitos ordenados por costo— y lo único que cambia es que se acepta lo que cada uno pueda dar. Las dos rutas comparten `reservarParcialPedido()`: no hay dos formas de comprometer stock.

### `intentarAsignarPedido(Pedido pedido)`

Valida estado, localiza depósito, ejecuta reserva y actualiza el pedido.

**Cambio requerido:** el pedido ya llega con lote específico; la búsqueda debe comenzar desde ese lote y luego localizar sus saldos.

### `intentarAsignarPedidos()`

Recorre pedidos pendientes o atrasados.

### `obtenerTipoContenedor(TipoProducto producto)`

Mapea producto a tipo de contenedor.

### `obtenerCapacidadContenedorTon(TipoContenedor tipo)`

Retorna la capacidad de la tabla `Producto`; una capacidad faltante aborta el arranque.

### `crearContenedoresParaAsignacion(Pedido pedido, AsignacionPedido asignacion)`

**Estado:** implementada (ADR-055). Reemplaza a `crearContenedoresParaPedido()`, que dependía de un único origen.

**Retorno:** `int`, la cantidad de contenedores creados en esta pasada. No es "todo o nada": se llama todos los días y va creando contenedores a medida que la asignación consigue volumen.

**Regla:** el volumen disponible es `min(reserva activa, asignado − ya contenerizado)`. Se crean contenedores **completos** mientras el disponible alcance para uno. El último contenedor **parcial** sólo se crea si `permitirUltimoParcial(pedido)`: el pedido está completamente asignado, venció su fecha límite o terminó la campaña. Que una asignación tenga un resto no basta para despachar un contenedor a medio llenar, porque el resto puede venir de otro origen mañana y en el contrato el parcial paga contenedor completo (ADR-053). Cada contenedor referencia la asignación y la clave de reserva con las que se va a despachar.

**Regla anterior (fase 6):** dividía las toneladas **reservadas** del pedido en contenedores de la capacidad del tipo que corresponde al producto, y el último iba parcial siempre. Cada contenedor queda asociado al lote que más aporta a su carga: se recorren en paralelo los contenedores y las capas reservadas del pedido en el depósito, en el mismo orden FIFO en que se van a despachar, así que la trazabilidad contenedor–lote coincide con la salida física real.

**Nota:** se dimensiona sobre lo reservado y no sobre lo solicitado, que es lo que hace `Pedido.calcularCantidadContenedores()`. Un pedido reservado a medias despacha los contenedores que su reserva permite.

### `contarContenedores(EstadoContenedor estadoBuscado)`

Cuenta contenedores por estado, para los indicadores de pantalla.

### `consolidaEnDeposito()` y `sitioConsolidacion(ContenedorExportacion contenedor)`

**Estado:** implementadas (fase 7).

`Main.estrategiaConsolidacion` es la **política por defecto** de la corrida (ADR-040, ADR-050); el circuito efectivo se resuelve por pedido en `circuitoDe(idSitioOrigen, esCrossDock)` y queda guardado en `Pedido.estrategiaSeleccionada` y `Envio.circuito`. `sitioConsolidacion()` devuelve la ubicación donde el contenedor se estiba —planta, depósito o terminal—, que es la que paga la tarifa, consume el cupo diario y acumula el costo. `usaPortacontenedor(envio)` es la condición del `SelectOutput` `seleccionarCircuito`: los circuitos 1 a 3 toman el pool y hacen el round trip terminal → origen → terminal; el circuito 4 manda el producto a granel y no toca el pool.

### `abrirPosicionesConsolidacionDelDia()` y `tomarPosicionConsolidacion(String idUbicacion)`

**Estado:** implementadas (fase 7).

La posición de consolidación es un recurso contado por día (ADR-039): cada sitio ofrece `contenedores_por_dia` contenedores por día (ADR-048). La primera función abre el cupo del día y acumula la capacidad ofrecida; la segunda lo consume y devuelve `false` cuando el sitio ya no tiene lugar.

### `despacharContenedoresPendientes()`

**Estado:** implementada (fase 7).

Fase 7 de la secuencia diaria. Recorre los contenedores en `ESPERANDO_PROGRAMACION`, ordenados por fecha límite del pedido, y por cada uno que consigue posición crea el `Envio` que lo mueve y lo pasa a `ESPERANDO_CARGA`. El que no consigue posición suma un día en `diasEsperaPosicion` y vuelve a competir al día siguiente; mientras espera, su producto sigue reservado y sigue devengando almacenaje, que es el costo que hace visible la falta de posiciones.

Reemplaza a `generarEnviosParaPedido()`, que creaba de una vez todos los envíos del pedido: con capacidad finita, un pedido puede despachar sus contenedores a lo largo de varios días.

### `abrirPosicionesCrossDockDelDia()`, `capacidadCrossDockLibre(String idUbicacion)` y `tomarPosicionesCrossDock(String idUbicacion, int cantidad)`

**Estado:** implementadas (fase 8).

El cupo de cross dock es un recurso contado por día y por sitio (ADR-041): cada depósito habilitado ofrece `posiciones_cross_dock` operaciones diarias, donde una operación es un contenedor cruzado. La primera función abre el cupo del día y acumula la capacidad ofrecida; las otras dos consultan y consumen. Con `habilitaCrossDock = false` no se abre capacidad y ningún pedido se cruza.

### `programarCrossDockDelDia()`, `intentarCrossDockPedido(Pedido pedido)` y `seleccionarSitioCrossDock(Pedido pedido)`

**Estado:** implementadas (fase 8).

Fase 5 de la secuencia diaria, antes de las transferencias normales. Recorre los pedidos `PENDIENTE` o `ATRASADO` en orden de fecha límite e intenta servirlos con producto que todavía está en planta: elige el sitio de menor costo (flete planta–depósito + flete depósito–puerto + tarifa de cross dock, sin almacenaje) entre los que tienen cupo y espacio, exige que la flota de producto del día alcance para el pedido entero (ADR-011, ADR-044), toma las posiciones, mueve el producto y lo reserva para el pedido, que queda marcado `esCrossDock`. Si falta stock, cupo o camión, el pedido no se cruza, suma en `crossDockReprogramados` y compite de nuevo al día siguiente por el camino normal.

### `costoServicioEstibaUsdTn(Pedido pedido, Deposito deposito)` y `estibaEnDeposito(Pedido pedido)`

**Estado:** implementadas (fase 8).

Resuelven quién cobra la estiba y dónde se arma el contenedor: la tarifa `CROSS_DOCK` del depósito si el pedido se cruzó, la de `CONSOLIDACION` del depósito si la estrategia consolida ahí, y la de la terminal en el caso contrario. Se usan tanto para el costo estimado del contenedor como para el costo real del envío, de modo que no puedan divergir.

### Consultas de tarifas por día de campaña: `DatosEntrada.tarifaFlete`, `tarifaRoundTrip`, `tarifaSitio`, `tarifaEspera` e `importe`

**Estado:** implementadas (C1, ADR-051).

Todas reciben el día de campaña y la clave del concepto (origen/destino/producto, terminal/sitio/tipo de contenedor, sitio/producto, recurso/sitio) y devuelven la **única** fila habilitada y vigente ese día. Dos filas vigentes para la misma clave, o ninguna, abortan la corrida indicando clave y día: la alternativa —devolver 0— convertiría un dato faltante en un resultado barato. `importe(unidad, tarifa, toneladas, contenedores, motivo)` es la que traduce unidad a base de cálculo y rechaza una unidad que no aplique al concepto; `importeFlete`, `importeConsolidacion`, `importeCrossDock` y `roundTripUsdContenedor` son los envoltorios que usa el modelo.

### Registro de cargos: `RegistroCostos.registrar()`, `total()`, `totalDe*()`, `reconciliar()` y `exportarCsv()`

**Estado:** implementadas (C2, ADR-052).

`registrar(...)` crea un `Cargo` inmutable, calcula el importe como `cantidad × tarifa`, rechaza cantidades o tarifas negativas y devuelve el importe registrado, o 0 si el cargo ya estaba (idempotencia por operación, categoría, unidad y motivo). Los totales por categoría, tipo, pedido, contenedor, producto, sitio, estrategia y día son consultas sobre la misma lista, así que no hay un segundo saldo que sincronizar. `Main.reconciliarCostos()` compara cada acumulador de agente contra el total de su categoría, todos los días y al cierre, y aborta si difieren en más de 0,01 USD. `exportarCsv(ruta)` vuelca el detalle a pedido y **no** se llama en el barrido.

### Devengos por circuito: `registrarInDeposito`, `registrarOutDeposito`, `registrarCargosTerminal`, `registrarDespachante`, `registrarEspera` y `registrarFleteProducto`

**Estado:** implementadas (C3, ADR-053).

Cada una registra un cargo en el evento físico que lo genera y devuelve el importe efectivamente registrado, que los acumuladores suman. `registrarInDeposito(sitio, producto, toneladas, lote, pedido, operacion)` corre cuando la capa entra al almacenamiento, y no corre cuando el producto cruza en cross dock. `registrarOutDeposito(envio)` corre al despachar, y `pagaOutDeposito(envio)` decide si corresponde: sólo paga egreso lo que estuvo almacenado en un depósito de terceros, así que el frío propio y el cross dock no lo pagan. `registrarCargosTerminal(envio)` devenga THC y costo de terminal por contenedor completo y guarda el día en `Envio.diaCargosTerminal`, porque en el circuito de terminal el contenedor recién existe al consolidar. `registrarDespachante(envio)` respeta la unidad de la tarifa: por contenedor, o una sola vez por pedido si es `USD_PEDIDO`. `registrarEspera(envio, recurso, sitio, horas, motivo)` cobra sólo el excedente sobre la franquicia, con `DatosEntrada.horasEsperaFacturables` como única regla.

### `costoEsperadoCircuito(Envio envio)` y `exigirIgual(...)`

**Estado:** implementadas (C3, ADR-053).

Auditoría de costeo por circuito. `costoEsperadoCircuito` reconstruye lo que el envío **debe** pagar leyendo las tarifas del día correspondiente a cada devengo —sin mirar el registro—: ciclo del portacontenedor o flete a granel, nunca los dos; armado del contenedor en el sitio donde ocurre; THC, costo de terminal y despachante por contenedor completo; egreso del depósito si corresponde; y espera sobre la franquicia. `finalizarEnvio()` compara ese número contra lo devengado con `exigirIgual` y aborta la corrida si difieren más de 0,01 USD. Es lo que hace que V-COST-01 a V-COST-05 y V-COST-07 se verifiquen en cada envío de cada corrida en lugar de una vez a mano.

### Vistas de costo del pedido: `costoEndToEndPedido`, `costoIncrementalPedido` y `costoHistoricoPedido`

**Estado:** implementadas (C3, ADR-053).

Las tres son consultas sobre el registro. La **end-to-end** es todo lo devengado contra el pedido. La **incremental** es lo posterior al día en que el pedido eligió origen y circuito: es la vista con la que se comparan alternativas, porque el almacenaje y el flete ya incurridos son costo hundido y no pueden decidir dónde consolidar. La **histórica** los reporta aparte, justamente para poder explicarlos sin meterlos en la comparación.

### KPIs de cierre de corrida

**Estado:** implementadas (fase 13).

`costoTotalCampania()`, `toneladasExportadas()`, `costoPorToneladaExportada()`, `nivelServicio()`, `atrasoPromedioDias()`, `utilizacionFlota()`, `excedenteFinalTn()` y `usoPosicionesConsolidacion()`. Desde la fase 19 se suman `costoTotalEconomico()` y `costoEconomicoPorTonelada()`, que agregan el costo de oportunidad del frío propio y la penalidad de sobrecarga al costo de caja sin mezclarse con él (ADR-049), y las métricas de ocupación de planta `tonDiaSobreNominalPlanta`, `diasSobrecargaPlanta` y `picoOcupacionPlantaPct` (ADR-048). `excedenteFinalTn()` sigue existiendo pero ya no es producto perdido: es el stock que queda en la red al cierre. Son funciones puras sobre el estado final: el experimento `Escenarios` las lee al terminar cada corrida y las escribe en `resultados/kpis_por_corrida.csv`. Que sean funciones y no acumuladores del experimento es lo que permite mirarlas también en una corrida suelta.

La utilización se informa por flota (ADR-044): `utilizacionFlota()` divide el camión-día de producto consumido por el ofrecido, y `utilizacionPortacontenedor()` devuelve la estadística de ocupación del pool `flotaPortacontenedores`. `viajesPlantaDeposito` cuenta los viajes efectivamente hechos y viaja al CSV como evidencia de que la flota se consumió.

### Frío propio: `forecastProduccion()`, `demandaProyectada()`, `toneladasASacarDePlanta()`, `toneladasASacarPreventivamente()`, `toneladasASacarReactiva()`, `plantaEnSobrecargaCritica()`, `registrarOcupacionPlanta()` y `devengarOportunidadFrioPropio()`

**Estado:** implementadas (fase 19; componente preventivo en ADR-056).

`revisarTransferenciasPlanta()` ya no compara el stock contra toneladas cableadas del agente: pregunta a la política del escenario cuánto sacar por producto (ADR-048). Con `FLEXIBLE`, `toneladasASacarDePlanta()` combina **tres** motivos con un máximo, no con una suma:

1. **desborde** — `proyectado − capacidad nominal`, con `proyectado = stock físico + forecastProduccion(diasForecast)`;
2. **servicio** — `demandaProyectada() − libre en depósitos`, donde la demanda es el **saldo pendiente de asignar** de cada pedido abierto (ADR-055) y no el pedido entero;
3. **prevención** — `toneladasASacarPreventivamente()`: si el proyectado toca el umbral de alerta, bajar hasta el objetivo; debajo de la alerta, cero (ADR-056).

El resultado se limita al stock **libre** en planta. Los tres se combinan con `max` porque son lecturas del mismo stock: sumarlos transferiría dos veces las mismas toneladas. Los tres componentes quedan en `componentePorDesborde`, `componentePorServicio` y `componentePreventivo` para el diagnóstico y para imputar el motivo del KPI.

`toneladasASacarPreventivamente()` no es la política REACTIVA: REACTIVA mira el stock de **hoy** y aborta si está bajo la alerta; el componente preventivo mira el **proyectado** con el forecast y convive con los otros dos motivos. Con `REACTIVA`, `toneladasASacarReactiva()` **no cambia**: sigue reproduciendo el vaciado anterior con los umbrales en porcentaje.

`plantaEnSobrecargaCritica()` no agrega volumen: sobre el umbral de sobrecarga, la distribución entre depósitos prioriza factibilidad y espacio antes que costo (ADR-056). La producción nunca se bloquea y la penalidad del día se sigue devengando.

`registrarOcupacionPlanta()` corre una vez por día y acumula tonelada-día sobre el nivel nominal y sobre el crítico, los días en sobrecarga y el pico de ocupación. `devengarOportunidadFrioPropio()` devenga la tarifa interna del frío propio y la penalidad de sobrecarga, que sólo entran al costo económico.

### Flota de producto: `abrirFlotaDelDia()`, `camionDiaViaje()`, `viajesNecesariosCamion()`, `flotaProductoLibreHoy()`, `tomarFlotaProducto()` y `flotaProductoAlcanza()`

**Estado:** implementadas (ADR-044).

La flota planta→depósito es capacidad diaria, igual que las posiciones de consolidación. `abrirFlotaDelDia()` abre la secuencia diaria ofreciendo `camiones_producto` camión-día y fija la capacidad del pool de portacontenedores desde el escenario (en el arranque el pool todavía no está inicializado y `set_capacity` se perdería). `camionDiaViaje(origen, destino)` cuesta un viaje redondo como fracción de jornada y `viajesNecesariosCamion(tn)` lo convierte en viajes según `capacidad_camion_tn`.

`transferirToneladasLote()` acota lo que mueve a los viajes que todavía entran hoy y llama a `tomarFlotaProducto()`, que descuenta la capacidad y aborta la corrida si se sobregira. El cross dock, que es todo o nada, pregunta antes con `flotaProductoAlcanza()`: si la flota del día no da para el pedido entero, el pedido no se cruza y se cuenta en `crossDockReprogramados`.

### Stock inicial: `cargarStockInicial()` y `validarStockInicial()`

**Estado:** implementadas (ADR-057).

`cargarStockInicial()` corre en el arranque del agente `Main`, inmediatamente después de `cargarDatosEntrada()` y antes del primer `pasoDiario_accion()`, así que el inventario preexistente está disponible para los pedidos del día 0. Recorre `datos.stockInicial`, crea un `LoteProducto` por identidad histórica (`codigo_lote + producto + cliente + calidad`) con `add_lotes()` y una **capa real** por fila con `inventario.ingresar(...)`, la misma función que usa la producción. No pasa por `crearLoteEnPlanta()`, que busca lote comercial abierto, fecha con `time()` y fuerza `PLANTA`.

Los lotes históricos quedan con `esStockInicial = true`, `codigoLoteExterno`, `estadoComercial = CERRADO` y `toneladasObjetivo = 0`, así que no reciben producción de campaña ni se cierran por objetivo; `toneladasIniciales` acumula el total histórico cargado y `diaProduccion` retiene el mínimo de sus capas. La carga **no devenga ningún costo pasado**: ni flete, ni IN, ni almacenaje anterior al día 0. Cierra con `actualizarUbicacionLote()` por lote y con `validarInventario()`.

`validarStockInicial()` valida la **capacidad efectiva** —lo que las validaciones del contrato no pueden hacer, porque los factores del escenario recién están aplicados a los agentes en este punto—: en un depósito exceder la capacidad por producto es error de datos y aborta el arranque con la lista completa (ADR-037); en la planta queda como advertencia por consola, porque la capacidad nominal del frío propio es un umbral de lectura y no un tope (ADR-048). Identidad, fechas, cantidad y ubicación se validan antes, en `DatosEntrada.validar()`.

KPIs asociados: `stockInicialCargadoTn`, `stockInicialRemanenteTn()` (lo que queda físicamente de los lotes con `esStockInicial`), `stockInicialConsumidoTn()`, `produccionCampaniaTn()`, `disponibilidadTotalTn()`, `demandaPlanificadaTn()` y `deficitEstructuralTn()`.

### Ventana marítima: `actualizarVentanasRetiroDelDia()`, `registrarPerdidaDeCutoff()`, `planificarPosicionFutura()`, `tiempoLogisticoEstimadoDias()`, `tiempoLogisticoMinimoDias()`, `horasCicloFisico()` y `holguraContenedor()`

**Estado:** implementadas (ADR-059).

`actualizarVentanasRetiroDelDia()` es el **paso 2b** del día, inmediatamente después de `registrarPedidosDelDia()`. Abre la ejecución física del pedido cuando llega `dia_apertura_retiro_vacio`: marca `ventanaRetiroAbierta`, mide la holgura una sola vez (`dia_cutoff_fisico − hoy − tiempoLogisticoMinimoDias(pedido)`), informa por traza las ventanas inviables y pasa a `ESPERANDO_PROGRAMACION` los contenedores que estaban en `CREADO`. Antes de ese día el pedido ya existe y ya puede reservar: lo que no puede es retirar el vacío.

`registrarPerdidaDeCutoff()` es el **paso 11b**, después de `registrarAtrasos()`. Marca una sola vez el pedido que pasó su cut-off sin completarse y aplica `politica_reprogramacion_buque`: `CONTINUAR` (default) lo cuenta como reprogramado y deja que el saldo siga hasta entregarse tarde; `CANCELAR` da de baja el saldo. No hay estado de contenedor nuevo ni calendario de buques.

`planificarPosicionFutura(pedido, sitio)` corre al comprometer un contenedor: busca dentro de la ventana el primer día con posición de consolidación libre contra `capacidadConsolidacionDia(sitio)` y lo anota en `posicionesPlanificadas`. Si ningún día de la ventana tiene lugar incrementa `contenedoresSinPosicionFutura`. Es una reserva de aviso: la ejecución sigue decidiéndose día a día con la capacidad real, así que planificar no consume capacidad de hoy.

`horasCicloFisico(pedido, idOrigen, circuito, esCrossDock, toneladas)` es el ciclo físico del circuito calculado sin depender de que exista la alternativa o el envío: carga o consolidación en origen, tramo vacío terminal→origen en los circuitos 1 a 3, viaje cargado, descarga y consolidación en terminal en el circuito 4. `horasCicloAlternativa()` pasó a delegar en ella, así que el evaluador y la ventana miden el mismo ciclo. `tiempoLogisticoEstimadoDias(pedido, asignacion)` le suma el día de programación y lo pasa a días; `tiempoLogisticoMinimoDias(pedido)` toma el mejor circuito ya comprometido y, si el pedido todavía no tiene asignaciones, estima con el depósito habilitado más rápido. `holguraContenedor(contenedor)` es la misma cuenta a nivel de contenedor y ordena el despacho: primero el cut-off más cercano y, con el mismo cut-off, el que menos margen tiene.

KPIs asociados: `servicioPorToneladaCutoff()`, `buquesCumplidos()`, `buquesPerdidos()`, `holguraPromedioDias()`, `contenedoresPlanificadosSinEjecutar()`, más los contadores `toneladasEntregadasAntesCutoff`, `toneladasEntregadasFueraCutoff`, `pedidosPerdieronCutoff`, `pedidosVentanaInviable` y `contenedoresSinPosicionFutura`.

### Capacidad finita: `capacidadNominalDia()`, `capacidadDisponibleDia()`, `capacidadDisponibleEnVentana()`, `reservarCapacidad()`, `consumirReservaCapacidad()`, `liberarCapacidadPorAsignacion()`, `reprogramarReservasCapacidad()` y `reconciliarCapacidad()`

**Estado:** implementadas (ADR-060).

La agenda es `ocupacionCapacidad`, una ocupacion por `(recurso, sitio, dia)`, y `reservasCapacidad`, la lista de `ReservaCapacidad`. Los recursos son `CONSOLIDACION` y `CROSS_DOCK` y no comparten cupo.

`capacidadNominalDia(recurso, sitio, dia)` es el techo declarado del sitio para ese recurso: `contenedores_por_dia` —capacidad diaria total procesable, no posiciones simultaneas— para consolidacion y el cupo de cross dock para el otro, cero si el sitio no existe, no esta habilitado o el dia esta fuera del horizonte. `capacidadDisponibleDia()` le resta la ocupacion; `capacidadDisponibleEnVentana(recurso, sitio, desde, hasta)` la suma dia por dia y `diasDisponiblesEnVentana()` devuelve los dias concretos, que es lo que el evaluador usa para acotar la alternativa antes de costearla.

`reservarCapacidad(asignacion, pedido, recurso, sitio, desde, hasta, cuantas)` toma las posiciones mas tempranas de la ventana y devuelve las reservas creadas; se llama al crear la asignacion, con `ceil(tn / capacidad del contenedor)`. `consumirReservaCapacidad(contenedor)` es lo que ejecuta el contenedor el dia comprometido: valida la reserva, la marca consumida y **no vuelve a ocupar** el dia —la posicion ya estaba ocupada desde que se reservo—. `liberarCapacidadPorAsignacion(asignacion, motivo)` devuelve al cupo lo que la asignacion ya no va a usar, con el motivo escrito, y deja la reserva en el registro: liberada no bloquea, pero sigue siendo auditable.

`reprogramarReservasCapacidad()` es el **paso diario** que corre antes del despacho: libera las posiciones que sobran de una asignacion ya contenerizada, mueve al proximo dia con lugar de la ventana las que quedaron vencidas y libera con `SIN_CAPACIDAD_ANTES_CUTOFF` las que ya no tienen dia antes del cut-off.

`reconciliarCapacidad()` es **C-03** y corre todos los dias: `activas + consumidas + liberadas` tiene que explicar lo reservado, los contadores de consumo y liberacion tienen que coincidir con las reservas, y ninguna ocupacion diaria puede superar el nominal del sitio. Corre tambien con la agenda apagada, porque la consolidacion sigue ocupando capacidad al ejecutar.

KPIs asociados: `capacidadReservadaTotal`, `capacidadConsumidaTotal`, `capacidadLiberadaTotal`, `reservasReprogramadas`, `contenedoresSinPosicionFutura`, `sobrecostoSaturacionUsd`, `pedidosMultiCircuito` y `fallbacksPoliticaFija`, mas los tres CSV de diagnostico.

### `aplicarEscenario()`

**Estado:** implementada (fase 13).

Traduce la fila del escenario a estado del modelo: duración de campaña, cross dock y estrategia de consolidación. Las dos flotas no se fijan acá sino en `abrirFlotaDelDia()` (ADR-044). Corre una sola vez, después de cargar y validar las tablas y antes del día 1. Todo lo demás que el escenario cambia (producción, capacidades, tarifas, ventana de demanda) ya viene aplicado en las tablas, no acá.

### Evaluador de circuitos: `usaEvaluador()`, `asignarConEvaluador(Pedido)`, `generarAlternativas(Pedido)`, `evaluarAlternativa`, `costearAlternativa`, `costearHundidoAlternativa`, `horasCicloAlternativa`, `ordenarAlternativas`, `ejecutarAlternativa` y `registrarPlan`

**Estado:** implementadas (C6, ADR-054).

`usaEvaluador()` decide si la política del escenario pasa por acá: las `FIJA_*` y `MANUAL` no, y conservan la asignación anterior. Cuando sí, `asignarConEvaluador()` corre por pedido, en orden de fecha límite y código.

`generarAlternativas()` parte del stock real: por cada ubicación con stock libre del producto genera el circuito de consolidación en el sitio, por cada depósito habilitado el cross dock, y el circuito de terminal; agrega además la transferencia depósito→depósito **ya descartada**, con el motivo de que no existe el movimiento físico (C7). `evaluarAlternativa()` verifica stock libre, habilitación, cupo de cross dock, espacio de paso, flota de producto, flota de granel y capacidad de estiba **sin mutar inventario**, y escribe el motivo cuando descarta.

`costearAlternativa()` arma el costo incremental —flete, round trip, estiba, OUT, THC, terminal y despachante— con las mismas tarifas y accesores que después devengan el envío; `costearHundidoAlternativa()` arma el IN, el almacenaje y el flete ya incurridos. `horasCicloAlternativa()` estima el ciclo y con él la fecha de entrega y si llega a tiempo.

`ordenarAlternativas()` es la política: primero servicio, después el criterio de costo (incremental o end-to-end según el escenario) y desempate por clave, para que dos corridas con la misma semilla decidan igual. `ejecutarAlternativa()` no mueve producto por su cuenta: reserva contra el origen elegido o llama a `ejecutarCrossDockPedido()`, es decir el flujo que ya existía. `registrarPlan()` guarda el `PlanLogistico` con todas las alternativas, factibles y descartadas.

### Afinidad pedido-depósito: `Pedido.depositoComprometido` (ADR-066)

**Estado:** implementada, pendiente de compilar en AnyLogic.

Columna opcional `deposito_comprometido` en `PedidoPlan`, copiada al pedido en `crearPedido()`. Distinto de `depositoAsignado` (que sólo se **escribe** después de asignar, como registro histórico): `depositoComprometido` es una entrada que representa que el pedido ya cuenta, en la realidad, con stock posicionado en un depósito específico. `ordenarAlternativas()` lo usa como criterio de desempate — entre servicio y frío propio — para que esa alternativa gane mientras sea factible, sin mirar costo. Vacío por defecto: no cambia nada si no se completa (`V-COST-12`).

### Rebalanceo entre depósitos: `revisarRebalanceoEntreDepositos`, `mejorDestinoRebalanceo`, `transferirEntreDepositos`, `buscarLoteMasAntiguoEnDeposito` y `registrarOutDepositoTransferencia` (ADR-066)

**Estado:** implementada, pendiente de compilar en AnyLogic y de datos depósito-depósito para poder ejercitarse.

Activa la transferencia depósito-depósito que `generarAlternativas()` traía descartada a propósito (`TRANSFERENCIA_DEPOSITO_DEPOSITO`, C7) y que `V-COST-06` dejó documentada sin implementar. No vive en el evaluador de pedidos — vive junto a `revisarTransferenciasPlanta()`, como paso 6b de la secuencia diaria, porque resuelve un problema de **capacidad de salida** (un depósito sin cross dock que no puede despachar a tiempo), no de costo.

`revisarRebalanceoEntreDepositos()` recorre los depósitos con `datos.capacidadCrossDockDia(idUbicacion) <= 0` que tengan stock libre con antigüedad ≥ `diasEstimadosAlmacenamiento` (reusa el mismo horizonte de ADR-065/056), y para el lote más antiguo (`buscarLoteMasAntiguoEnDeposito`) busca el mejor destino con `mejorDestinoRebalanceo` — mismo criterio de costo que `seleccionarDeposito()`: flete de reubicación más el holding proyectado en destino (`horizonteHoldingEvitado()`, reusada de ADR-065). `transferirEntreDepositos()` ejecuta el movimiento con la fórmula de `V-COST-06` (OUT del origen vía `registrarOutDepositoTransferencia` —nueva, porque `registrarOutDeposito(Envio)` depende de un envío que acá no existe—, flete vía `registrarFleteProducto` reusada tal cual, IN en destino vía `registrarInDeposito` reusada tal cual), sin cargos de contenedor.

Dos guardas explícitas para no asumir dato inexistente: si falta la tarifa de flete o de sitio (`hayTarifaFlete`/`hayTarifaSitio`), o la distancia entre los dos depósitos (`distanciaKmSimetrica() < 0`), la función devuelve 0 sin mover nada — no crashea, no asume costo cero. Con `datos/entrada_ejemplo.xlsx` sin filas depósito-depósito en `TarifaFleteProducto`/`Distancia`, el mecanismo corre todos los días y no mueve nada, que es el comportamiento correcto (`V-COST-13`).

**Simplificación declarada:** no usa la agenda de flota multidiaria (ADR-061) — calcula el camión-día inline con `distanciaKmSimetrica()` (no con `camionDiaViaje()`, que usa `distanciaKm()` y aborta la corrida si falta la fila exacta origen→destino). Es la única transferencia del modelo que queda fuera de esa agenda; extenderla es trabajo pendiente, no un descuido.

### Crédito de holding futuro en el ranking: `tarifaHoldingOrigen`, `horizonteHoldingEvitado` y `AlternativaCircuito.costoUnitarioRankingSegun` (ADR-065)

**Estado:** propuesta.

**Objetivo:** que `ordenarAlternativas()` compare no sólo el costo de despachar hoy desde cada origen, sino también el storage/oportunidad que ese origen deja de devengar al despacharse ahora en vez de más adelante — sin tocar los campos auditados (`costoIncremental`, `costoEndToEnd`).

`tarifaHoldingOrigen(String idOrigen, TipoProducto producto)` unifica la tarifa de "costo de tener 1 tn parada": `oportunidadUsdTnDia` si `idOrigen == "PLANTA"`, o `storageUsdTnDia` del depósito en cualquier otro caso. `horizonteHoldingEvitado()` es `min(diasEstimadosAlmacenamiento, duracionCampaniaDias - diaCampania())` — reusa el parámetro que ya existe para `seleccionarDeposito()` (ADR-056), acotado por lo que queda de campaña. `costearAlternativa()` calcula `alternativa.costoHoldingEvitado = tarifaHoldingOrigen(...) * horizonteHoldingEvitado() * toneladas`, y `AlternativaCircuito.costoUnitarioRankingSegun(endToEnd) = (costoSegun(endToEnd) - costoHoldingEvitado) / toneladas` es lo único que reemplaza a `costoUnitarioSegun(endToEnd)` dentro de `ordenarAlternativas()`. Ver el detalle completo en ADR-065 y el ejemplo numérico en `flow/02-logica-entrega-pedidos.md`.

### Material como dimensión física del inventario (ADR-067)

**Estado:** implementada, pendiente de compilar en AnyLogic.

`material` es una subdivisión del producto (`JCL`, `JCCL`, `PULPA` de `JUGO`, por ejemplo) que participa en la identidad física del lote y de la capa — no es sólo trazabilidad, como sí lo eran `cliente`/`calidad` hasta este ADR. Un pedido de un material nunca reserva ni despacha capas de otro material del mismo producto.

- **`Capa.material`, `LoteProducto.material`, `Pedido.material`, `ContenedorExportacion.material`**: campos nuevos, mismo patrón que `cliente`/`calidad` (ADR-047). `crearPedido()` copia `plan.material` al pedido; `crearContenedoresParaAsignacion()` copia `pedido.material` al contenedor.
- **`Inventario`** gana overloads con `material` de `stock`, `libre`, `reservado`, `fifo`, `reservar`, `despachar`, además del `ingresar()` existente (que ahora *siempre* pide material). Las firmas sin material **no se tocaron**: siguen agregando entre materiales, y las usan capacidad, KPIs, `revisarTransferenciasPlanta()` y `revisarRebalanceoEntreDepositos()` (ADR-066) — ninguna de esas decisiones depende de qué material es, sólo de cuánto producto hay.
- **Todo lo que resuelve stock físico para un pedido puntual pasa a filtrar por `pedido.material`**: `reservarParcialPedido()`, `alternativaPara()`/`toneladasDisponiblesParaAlternativa()`, `seleccionarSitioCrossDock()`, `ejecutarCrossDockPedido()`/`transferirLotesADeposito()` (que usa el nuevo overload `buscarLoteMasAntiguoEnPlanta(producto, material)` — el de un solo parámetro sigue sirviendo al heurístico agregado de `transferirProductoADepositos()`), `depositosOrdenadosParaPedido()`/`costoEstimadoDesde()`, `evaluarAlternativa()` y `toneladaDiaEnStock()` (histórico de almacenaje de ADR-065, que sin este filtro estimaría el costo hundido sobre capas del material equivocado).
- **`obtenerTipoContenedor(producto, material)` y `obtenerCapacidadContenedorTon(producto, material)`** resuelven directo contra `datos.producto(producto, material)` — reemplaza la búsqueda inversa por tipo de contenedor, que era ambigua en cuanto dos materiales del mismo producto compartían tipo de contenedor con capacidades distintas (el caso real: `JUGO`/`PULPA` es `REEFER_40` de 20 tn, `JUGO`/`JCL` y `JCCL` son `REEFER_40` de 24 tn).
- **`buscarLoteComercialAbierto()` y `crearLoteEnPlanta()`** agregan `material` a la comparación de identidad del lote, en el mismo lugar donde ya comparan `cliente`/`calidad`. **`Planta.producir()`** deja de asumir un solo total agregado por producto y por día: itera `datos.materialesDe(producto)` y llama `ingresarProduccion(producto, material, toneladas)` por cada uno, usando `DatosEntrada.produccionDelDia(dia, producto, material)`.
- **`DatosEntrada.materialesDe(TipoProducto)`** deriva la lista de materiales válidos de la propia tabla `Producto` — no hay una tabla nueva que la duplique. `DatosEntrada.validar()` rechaza explícitamente un material vacío en `ProduccionPlan`/`PedidoPlan`/`StockInicial` cuando `Producto` sí distingue materiales para ese producto, para no resolver 0 en silencio.

Ver el detalle completo en ADR-067 y el impacto en `flow/01-logica-almacenamiento.md` §1.11.

### Funciones objetivo de contenedores

- `programarRetiroVacio(ContenedorExportacion contenedor)`: el ciclo del contenedor vacío todavía no se modela, y por eso `horaRetiroVacio` y `horaLlegadaLugarCarga` siguen en `-1`.

## 5. Funciones de Deposito

### Implementadas o conocidas

- `getStock(producto)`, `getReservado(producto)`, `getDisponible(producto)`: derivadas de las capas del depósito (ADR-023);
- `getCapacidad(producto)`, `getEspacioDisponible(producto)`, `puedeRecibir(producto, toneladas)`, `puedeReservar(producto, toneladas)`;
- `getCostoConsolidado(producto)`: tarifa de estiba del depósito, leída de `TarifaServicioCarga` con `tipo_servicio = CONSOLIDACION`. Desde la fase 7 es la que se cobra cuando se consolida en depósito. Las posiciones no viven en el depósito: las resuelven `Main.tomarPosicionConsolidacion()` y `DatosEntrada.capacidadConsolidacionDia()` (ADR-039).
- `getCostoCrossDock(producto)`: la misma tabla con `tipo_servicio = CROSS_DOCK`, que es la que se cobra cuando el producto cruza el depósito sin almacenarse (fase 8). El depósito acumula `toneladasCrossDock`, `contenedoresCrossDock` y `costoCrossDockAcumulado`.

Las mutadoras `recibirProducto`, `retirarProducto`, `reservarProducto`, `liberarReserva` y `despacharReservado` se eliminaron: el depósito no tiene saldo propio, y el movimiento lo hace `Main.inventario`.

### Pendientes

- `calcularCostoIn(...)`;
- `calcularCostoStorage(...)`;
- `calcularCostoOut(...)`;
- `puedeOperarCrossDock(...)`;
- `solicitarPosicionCrossDock(...)`.

## 6. Funciones de Pedido

### `calcularCantidadContenedores()`

```java
return (int) Math.ceil(
    toneladasSolicitadas / capacidadContenedorTon
);
```

Estimación comercial sobre lo **solicitado**. Los contenedores que efectivamente se crean salen de `Main.crearContenedoresParaPedido()`, que divide lo **reservado**; los dos números coinciden sólo si el pedido se reservó completo.

### Pendientes

- `getToneladasPendientesReserva()`;
- `getToneladasPendientesDespacho()`;
- `estaCompleto()`;
- `actualizarEstado()`;
- `validarConsistencia()`.

## 7. Funciones de PlanLogistico

### `recalcularCostos()`

Separa costo histórico, incremental y end-to-end.

### `validarPlan()`

Valida referencias obligatorias.

Desde ADR-054 el plan es un registro de la decisión y no la calcula: la factibilidad, la estimación de tiempo y el costo de cada alternativa los resuelve el evaluador de `Main` sobre `AlternativaCircuito`, que es donde están las tarifas, el inventario y los cupos. `estimarTiempo()`, `validarFechaLimite()`, `validarRecursos()` y `calcularScore()` quedan sin implementar por esa razón, no por falta de alcance.

## 8. Funciones de ContenedorExportacion

Pendientes:

- `asignarCamion(...)`;
- `registrarRetiroVacio()`;
- `registrarLlegadaCarga()`;
- `iniciarConsolidacion()`;
- `finalizarConsolidacion()`;
- `registrarIngresoTerminal()`;
- `calcularTiempoCiclo()`;
- `validarTransicionEstado(...)`.

## 9. Reglas de implementación

- entregar siempre la función completa cuando se modifique;
- no usar fragmentos sin contexto para reemplazos;
- validar `null` y cantidades no positivas;
- devolver `boolean` cuando la operación pueda fallar;
- revertir cambios parciales;
- usar `traceln` temporalmente y un registro estructurado en la versión final;
- no tratar tarifa inexistente como cero;
- documentar todas las variables modificadas.

## 10. Funciones de flota de producto multidiaria (ADR-061)

**Estado:** implementadas.

### 10.1 Agenda de camiones

`inicializarFlotaProducto()` crea `camiones_producto` unidades una sola vez por corrida, con base en planta. No se recrean cada dia: un camion que salio sigue ocupado manana, y ese es todo el cambio respecto de ADR-044.

`buscarCamionDisponibleMasTemprano(noAntesDe)` devuelve el camion que puede salir antes, con `max(noAntesDe, disponibleDesde)` como criterio y el menor `idCamion` como desempate, para que dos corridas iguales asignen los mismos camiones a los mismos viajes. `fechaSalidaMasTempranaProducto()`, `camionesDisponiblesEn()` y `camionesProductoEnRuta()` son consultas sin efecto.

### 10.2 Duraciones

`duracionIdaProductoDias(origen, destino)` es `distancia / velocidad / jornada`, con la distancia leida de forma simetrica (`DatosEntrada.distanciaKmSimetrica()`, porque la tabla declara un solo sentido por tramo) y la velocidad y la jornada del escenario. `duracionRetornoProductoDias()` es la ida al revés. `horasManipuleoViajeProducto()` suma carga en el origen y descarga en el destino con las velocidades declaradas de cada sitio —los mismos campos que usa `crearEnvio()`—, y `duracionTotalViajeProductoDias()` es lo que el camion queda ocupado. No hay ninguna duracion cableada ni una segunda duracion para el mismo tramo.

### 10.3 Disponibilidad sin mutar la agenda

`evaluarDisponibilidadFlotaProducto(origen, destino, toneladas, noAntesDe, fechaLimite)` simula la asignacion sobre una **copia** de las fechas de disponibilidad y devuelve un `ResultadoDisponibilidadFlota`: toneladas y viajes programables, primera y ultima salida, ultima llegada, ultimo regreso, espera maxima y motivo. Es lo que reemplaza al si/no de `flotaProductoAlcanza()`.

### 10.4 Programacion

`programarViajeProducto(...)` crea **un** viaje de hasta `capacidad_camion_tn`: elige el camion mas temprano, calcula salida, llegada y regreso, ocupa el camion hasta el regreso, reserva el stock con `clave = VIAJE|<id>` y deja el viaje en `PROGRAMADO`. Programar **no** mueve producto.

`programarMovimientoProducto(...)` parte el volumen en viajes y programa los que puede, devolviendo lo programado. La parcialidad es valida y no se revierte; el saldo se cuenta en `toneladas_no_programadas_por_flota`.

### 10.5 Ciclo del viaje

`iniciarViajeProducto()` saca la carga del origen (consumiendo la reserva), la pasa a transito y devenga el flete, una sola vez por viaje. `recibirViajeProducto()` ingresa la carga al inventario del destino con la fecha de llegada, conservando lote y dia de produccion, y devenga el IN del deposito salvo que el producto cruce. `completarViajeProducto()` devuelve el camion a su base y lo libera. `cancelarViajeProducto()` solo actua antes de la salida: libera la reserva y el camion, y no cobra flete.

Los tres pasos diarios corren en este orden, antes de producir: `completarViajesProductoDelDia()`, `recibirViajesProductoDelDia()`, `iniciarViajesProductoDelDia()`. Un viaje fechado dentro de la jornada de hoy se procesa hoy, incluida la llegada y el regreso si entran en el dia.

### 10.6 Transito y espacio

`toneladasEnTransitoHacia()`, `toneladasComprometidasParaViajesDe()`, `toneladasProductoEnTransitoDe()` y `espacioDisponibleEfectivo()` evitan comprometer dos veces el mismo lugar: el producto en ruta no esta en el stock del destino, pero su lugar ya esta tomado.

### 10.7 Cota del evaluador y reconciliacion

`acotarAlternativaPorFlota()` acota las toneladas de la alternativa por lo que la agenda puede prometer y escribe el diagnostico. `reconciliarFlotaProducto()` es C-04 y corre todos los dias.

`usaFlotaMultidiaria()` es el interruptor: en `false` todo lo anterior queda inactivo y el modelo vuelve a la capacidad diaria agregada de ADR-044.

## 11. THC por naviera y cobertura de tarifas del maestro nuevo (ADR-068, seguimiento)

**Estado:** implementadas.

### 11.1 `DatosEntrada.TarifaThc` / `tarifaThc()` / `thcUsdContenedor(dia, naviera, tipoContenedor)`

Tabla nueva, paralela a `TarifaSitio` pero con clave `(naviera, tipoContenedor)` en vez de `(idUbicacion, producto)`: el THC de `Gastos_THC` lo factura la naviera, confirmado por el usuario, y forzarlo en `TarifaSitio` hubiera significado una fila por naviera aunque el resto de esa tabla no varíe por naviera. `tarifaThc(dia, naviera, tipoContenedor)` resuelve la fila vigente con el mismo criterio que `tarifaSitio()` (dos vigentes es error de datos, ninguna vigente también). El overload `thcUsdContenedor(dia, idTerminal, producto)` de siempre no cambia — sigue siendo lo que se usa cuando el libro no trae `Gastos_THC`.

### 11.2 `DatosEntrada.tipoContenedorDe(producto)`

Resuelve el tipo de contenedor de un producto entero (no de un material puntual), para las validaciones de cobertura que todavía razonan a nivel producto: recorre `materialesDe(producto)` y devuelve el tipo de contenedor común, o falla explícitamente si dos materiales del mismo producto declaran tipos distintos. Reemplaza tres llamadas a `producto(producto).tipoContenedor` que no compilaban desde ADR-067 (ese método sólo existe de a dos argumentos, `producto` + `material`) — bug preexistente encontrado y corregido durante este seguimiento, no introducido por él.

### 11.3 `Main.thcUsdContenedorPedido(dia, pedido, terminal)`

Punto único de entrada para cobrar THC en los tres lugares que lo hacen (`registrarCargosTerminal()`, la auditoría de `costoEsperadoCircuito()` y `costearAlternativa()`). Si `datos.tarifasThc` no está vacía (el libro trajo `Gastos_THC`), resuelve por naviera y tipo de contenedor — calculado *fresco* vía `obtenerTipoContenedor(pedido.producto, pedido.material)`, no leído de `pedido.tipoContenedor`: ese campo lo deja resuelto el `StartupCode` con los valores default del agente antes de `crearPedido()`, y sólo queda correcto mucho más tarde, en `crearContenedoresParaAsignacion()` — no confiable en ninguno de los tres puntos donde se cobra THC, que corren antes de que se elija circuito. Si la tabla está vacía, cae al `thcUsdContenedor(dia, idTerminal, producto)` de siempre.

### 11.4 `ImportadorExcel.leerGastosThc()` / lectura de `TarifaConsolidado` y `TarifaCross_docking`

`leerGastosThc()` lee `Gastos_THC` hacia `datos.tarifasThc`, descartando con `advertencias` (no con error que aborte el import) las filas cuyo `Proveedor` no es una naviera real del enum `Naviera` (`FORWARDER`, `SILVERFREIGHT`). `leerTarifaSitioMaestroNuevo()` ganó dos conceptos más en su acumulador (`consolidacion`, `crossdock`) leídos de `TarifaConsolidado`/`TarifaCross_docking` (o `Consolidado`/`Cross_docking`, sin el prefijo) con la misma técnica que `Gastos_terminal`/`Despachante`: `Lugar Consolidado` resuelve el sitio, `Tipo de Contenedor` resuelve el producto vía `productoDeContenedor()`.

## 12. Vista de red en vivo (ADR-072)

**Estado:** implementadas. Son funciones de **presentación**: leen estado ya calculado y no escriben nada del modelo. Con `animacionRed = false` ninguna hace trabajo (todas cortan en la primera línea) y la corrida decide y cuesta igual.

### 12.1 Coordenadas: `DatosEntrada.Ubicacion.latitud` / `.longitud` / `tieneCoordenadas()`

Dos campos `double` que arrancan en `Double.NaN` —"el libro no las declara"— más el predicado que pregunta por las dos juntas. `ImportadorExcel.coordenada(valor, minimo, maximo, idUbicacion, columna)` normaliza la escala dividiendo por 10 hasta caer en el rango declarado y falla si no cae; `numeroOpcionalAlias()` acepta `latitud`/`lat` y `longitud`/`long`/`lon`. Declarar una sola de las dos es error de datos. **Ninguna función de costo, de distancia ni de tiempo las lee.**

### 12.2 Posiciones: `calcularPosicionesRedVisual`, `separarNodosRedVisual`, `recortarNodosRedVisual`, `columnaRedVisual`

`calcularPosicionesRedVisual()` proyecta latitud y longitud sobre el área de la vista si **todos** los sitios de la red las traen: corrige la longitud por el coseno de la latitud media, conserva la relación de aspecto y da vuelta el eje Y (la pantalla crece hacia abajo, la latitud hacia arriba). Si el libro no las trae, cae al esquema de tres columnas por tipo de nodo (`columnaRedVisual()`: planta, depósitos, terminales). `separarNodosRedVisual()` empuja los nodos que quedan a menos de 112 px —seis de los diez sitios están dentro de 25 km— con dirección fija cuando coinciden exactamente, así la vista es la misma en cada corrida; `recortarNodosRedVisual()` los deja dentro del área con margen para el título y la etiqueta, y se llama **dentro** del ciclo para que el recorte no vuelva a superponer lo que ya se separó.

### 12.3 Estado del nodo: `capacidadNodoRedVisual`, `stockNodoRedVisual`, `colorOcupacionRedVisual`, `colorTipoNodoRedVisual`

Capacidad y stock salen de las mismas dos funciones que alimentan los paneles (`datos.capacidadDeclaradaTn()` y el inventario del sitio), no de un acumulador paralelo: el semáforo del mapa y el número del panel no pueden divergir. La terminal no almacena y va con ocupación `-1`, que es el gris de "sin capacidad declarada" y no un 0 % engañoso.

### 12.4 Flujo por tramo: `claveTramoVisual` y `registrarFlujoVisual`

`claveTramoVisual(origen, destino)` ordena los extremos alfabéticamente: el arco es la **infraestructura**, así que acumula los dos sentidos —lo que se dimensiona es el tramo—. `registrarFlujoVisual(origen, destino, toneladas)` suma toneladas y cantidad de movimientos de cada movimiento **ya ejecutado**, en el mismo punto donde el modelo cierra la etapa física del envío; con la animación apagada no acumula nada.

### 12.5 Dibujo y refresco: `dibujarRedVisual`, `dibujarLeyendaRedVisual`, `actualizarRedVisual`, `textoTramosRedVisual`

`dibujarRedVisual()` crea las figuras **una sola vez** al arrancar (arcos primero, para que queden debajo de los nodos) y `actualizarRedVisual()` corre al final de cada paso diario cambiando sólo color, grosor y texto de figuras que ya existen: no se crea un objeto por evento, que es lo que haría caer el rendimiento en una campaña de 365 días. `textoTramosRedVisual()` lista los cinco tramos que más movieron, del mismo acumulador que dibuja los arcos. Sin tipos de agente nuevos: PLE está en 10 de 10 y las figuras son `ShapeOval`, `ShapeLine` y `ShapeText` colgadas de `presentation`.
