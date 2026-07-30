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

### `aplicarEscenario()`

**Estado:** implementada (fase 13).

Traduce la fila del escenario a estado del modelo: duración de campaña, cross dock y estrategia de consolidación. Las dos flotas no se fijan acá sino en `abrirFlotaDelDia()` (ADR-044). Corre una sola vez, después de cargar y validar las tablas y antes del día 1. Todo lo demás que el escenario cambia (producción, capacidades, tarifas, ventana de demanda) ya viene aplicado en las tablas, no acá.

### Evaluador de circuitos: `usaEvaluador()`, `asignarConEvaluador(Pedido)`, `generarAlternativas(Pedido)`, `evaluarAlternativa`, `costearAlternativa`, `costearHundidoAlternativa`, `horasCicloAlternativa`, `ordenarAlternativas`, `ejecutarAlternativa` y `registrarPlan`

**Estado:** implementadas (C6, ADR-054).

`usaEvaluador()` decide si la política del escenario pasa por acá: las `FIJA_*` y `MANUAL` no, y conservan la asignación anterior. Cuando sí, `asignarConEvaluador()` corre por pedido, en orden de fecha límite y código.

`generarAlternativas()` parte del stock real: por cada ubicación con stock libre del producto genera el circuito de consolidación en el sitio, por cada depósito habilitado el cross dock, y el circuito de terminal; agrega además la transferencia depósito→depósito **ya descartada**, con el motivo de que no existe el movimiento físico (C7). `evaluarAlternativa()` verifica stock libre, habilitación, cupo de cross dock, espacio de paso, flota de producto, flota de granel y capacidad de estiba **sin mutar inventario**, y escribe el motivo cuando descarta.

`costearAlternativa()` arma el costo incremental —flete, round trip, estiba, OUT, THC, terminal y despachante— con las mismas tarifas y accesores que después devengan el envío; `costearHundidoAlternativa()` arma el IN, el almacenaje y el flete ya incurridos. `horasCicloAlternativa()` estima el ciclo y con él la fecha de entrega y si llega a tiempo.

`ordenarAlternativas()` es la política: primero servicio, después el criterio de costo (incremental o end-to-end según el escenario) y desempate por clave, para que dos corridas con la misma semilla decidan igual. `ejecutarAlternativa()` no mueve producto por su cuenta: reserva contra el origen elegido o llama a `ejecutarCrossDockPedido()`, es decir el flujo que ya existía. `registrarPlan()` guarda el `PlanLogistico` con todas las alternativas, factibles y descartadas.

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
