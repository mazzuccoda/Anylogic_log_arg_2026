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

### `transferirProductoADepositos(TipoProducto producto, double toneladasObjetivo)`

**Estado:** implementada; transfiere por toneladas (fase 4).

**Regla:** toma el lote más antiguo **con saldo libre en planta** —no el más antiguo "en estado EN_PLANTA", para que un lote transferido a medias pueda terminar de salir— y le manda `min(saldo libre, pendiente)`. Si en el depósito elegido no entra todo, se manda lo que entra y el resto sale en la vuelta siguiente, posiblemente a otro depósito.

**Problema resuelto:** antes, un lote más grande que el pendiente cortaba el ciclo (`if (toneladasLote > pendiente) break;`), así que la planta se sobrellenaba y generaba excedente con depósitos vacíos.

### `seleccionarDeposito(TipoProducto producto, double toneladas)`

**Estado:** implementada; criterio por costo unitario (fase 4).

**Regla:** entre los depósitos habilitados con espacio, elige el de menor costo estimado **por tonelada** de `min(toneladas, espacio disponible)`. Antes descartaba todo depósito donde no entrara la carga completa, lo que dejaba producto en planta con capacidad libre repartida.

### Antes: `transferirLoteCompleto(LoteProducto lote, Deposito destino)`

Eliminada en la fase 4. Movía todo el saldo libre del lote y fijaba `ubicacionActual` como si el lote estuviera entero en un solo lugar.

### `revisarTransferencias()`

Evalúa umbrales de planta y solicita mover el exceso respecto del stock objetivo.

### `reservarLotesParaPedido(Pedido pedido, Deposito deposito)`

**Estado:** en revisión.

**Problema:** la lógica actual depende de lotes diarios y de una ubicación única.

**Objetivo:** reservar toneladas de un lote comercial, potencialmente en varias ubicaciones, manteniendo trazabilidad.

### `intentarAsignarPedido(Pedido pedido)`

Valida estado, localiza depósito, ejecuta reserva y actualiza el pedido.

**Cambio requerido:** el pedido ya llega con lote específico; la búsqueda debe comenzar desde ese lote y luego localizar sus saldos.

### `intentarAsignarPedidos()`

Recorre pedidos pendientes o atrasados.

### `obtenerTipoContenedor(TipoProducto producto)`

Mapea producto a tipo de contenedor.

### `obtenerCapacidadContenedorTon(TipoContenedor tipo)`

Retorna la capacidad de la tabla `Producto`; una capacidad faltante aborta el arranque.

### `crearContenedoresParaPedido(Pedido pedido)`

**Estado:** implementada (fase 6).

**Retorno:** `int`, la cantidad de contenedores creados. Es idempotente: si el pedido ya tiene contenedores, devuelve los que hay.

**Regla:** divide las toneladas **reservadas** en contenedores de la capacidad del tipo que corresponde al producto, y el último va parcial. Cada contenedor queda asociado al lote que más aporta a su carga: se recorren en paralelo los contenedores y las capas reservadas del pedido en el depósito, en el mismo orden FIFO en que se van a despachar, así que la trazabilidad contenedor–lote coincide con la salida física real.

**Nota:** se dimensiona sobre lo reservado y no sobre lo solicitado, que es lo que hace `Pedido.calcularCantidadContenedores()`. Un pedido reservado a medias despacha los contenedores que su reserva permite.

### `contarContenedores(EstadoContenedor estadoBuscado)`

Cuenta contenedores por estado, para los indicadores de pantalla.

### `consolidaEnDeposito()` y `sitioConsolidacion(ContenedorExportacion contenedor)`

**Estado:** implementadas (fase 7).

La estrategia de la corrida la fija el parámetro `Main.estrategiaConsolidacion` (ADR-040): con `CONSOLIDACION_DEPOSITO` el contenedor se estiba en el depósito que tiene la reserva y llega consolidado a la terminal; con `CONSOLIDACION_TERMINAL` el producto viaja a granel y se consolida en la terminal. `sitioConsolidacion()` devuelve la ubicación que corresponde, y es la que paga la tarifa y consume la posición.

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

### KPIs de cierre de corrida

**Estado:** implementadas (fase 13).

`costoTotalCampania()`, `toneladasExportadas()`, `costoPorToneladaExportada()`, `nivelServicio()`, `atrasoPromedioDias()`, `utilizacionFlota()`, `excedenteFinalTn()` y `usoPosicionesConsolidacion()`. Desde la fase 19 se suman `costoTotalEconomico()` y `costoEconomicoPorTonelada()`, que agregan el costo de oportunidad del frío propio y la penalidad de sobrecarga al costo de caja sin mezclarse con él (ADR-049), y las métricas de ocupación de planta `tonDiaSobreNominalPlanta`, `diasSobrecargaPlanta` y `picoOcupacionPlantaPct` (ADR-048). `excedenteFinalTn()` sigue existiendo pero ya no es producto perdido: es el stock que queda en la red al cierre. Son funciones puras sobre el estado final: el experimento `Escenarios` las lee al terminar cada corrida y las escribe en `resultados/kpis_por_corrida.csv`. Que sean funciones y no acumuladores del experimento es lo que permite mirarlas también en una corrida suelta.

La utilización se informa por flota (ADR-044): `utilizacionFlota()` divide el camión-día de producto consumido por el ofrecido, y `utilizacionPortacontenedor()` devuelve la estadística de ocupación del pool `flotaPortacontenedores`. `viajesPlantaDeposito` cuenta los viajes efectivamente hechos y viaja al CSV como evidencia de que la flota se consumió.

### Frío propio: `forecastProduccion()`, `demandaProyectada()`, `toneladasASacarDePlanta()`, `toneladasASacarReactiva()`, `registrarOcupacionPlanta()` y `devengarOportunidadFrioPropio()`

**Estado:** implementadas (fase 19).

`revisarTransferenciasPlanta()` ya no compara el stock contra toneladas cableadas del agente: pregunta a la política del escenario cuánto sacar por producto (ADR-048). Con `FLEXIBLE`, `toneladasASacarDePlanta()` retiene todo lo que quepa bajo el nivel objetivo considerando el forecast del horizonte (`forecastProduccion()`, perfecto sobre el plan de producción) y saca además lo que exijan las obligaciones pendientes (`demandaProyectada()`, que cuenta los pedidos `PENDIENTE` y `ATRASADO` sin reserva). Con `REACTIVA`, `toneladasASacarReactiva()` reproduce el vaciado anterior con los umbrales expresados en porcentaje.

`registrarOcupacionPlanta()` corre una vez por día y acumula tonelada-día sobre el nivel nominal y sobre el crítico, los días en sobrecarga y el pico de ocupación. `devengarOportunidadFrioPropio()` devenga la tarifa interna del frío propio y la penalidad de sobrecarga, que sólo entran al costo económico.

### Flota de producto: `abrirFlotaDelDia()`, `camionDiaViaje()`, `viajesNecesariosCamion()`, `flotaProductoLibreHoy()`, `tomarFlotaProducto()` y `flotaProductoAlcanza()`

**Estado:** implementadas (ADR-044).

La flota planta→depósito es capacidad diaria, igual que las posiciones de consolidación. `abrirFlotaDelDia()` abre la secuencia diaria ofreciendo `camiones_producto` camión-día y fija la capacidad del pool de portacontenedores desde el escenario (en el arranque el pool todavía no está inicializado y `set_capacity` se perdería). `camionDiaViaje(origen, destino)` cuesta un viaje redondo como fracción de jornada y `viajesNecesariosCamion(tn)` lo convierte en viajes según `capacidad_camion_tn`.

`transferirToneladasLote()` acota lo que mueve a los viajes que todavía entran hoy y llama a `tomarFlotaProducto()`, que descuenta la capacidad y aborta la corrida si se sobregira. El cross dock, que es todo o nada, pregunta antes con `flotaProductoAlcanza()`: si la flota del día no da para el pedido entero, el pedido no se cruza y se cuenta en `crossDockReprogramados`.

### `aplicarEscenario()`

**Estado:** implementada (fase 13).

Traduce la fila del escenario a estado del modelo: duración de campaña, cross dock y estrategia de consolidación. Las dos flotas no se fijan acá sino en `abrirFlotaDelDia()` (ADR-044). Corre una sola vez, después de cargar y validar las tablas y antes del día 1. Todo lo demás que el escenario cambia (producción, capacidades, tarifas, ventana de demanda) ya viene aplicado en las tablas, no acá.

### Funciones objetivo de planificación

- `localizarExistenciasPedido(Pedido pedido)`;
- `generarPlanesLogisticos(Pedido pedido)`;
- `evaluarPlan(PlanLogistico plan)`;
- `seleccionarMejorPlan(Pedido pedido)`;
- `ejecutarPlan(PlanLogistico plan)`.

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

Actualmente valida referencias obligatorias. Debe incorporar inventario, capacidad, recursos, fecha límite y tarifas.

### Pendientes

- `estimarTiempo()`;
- `validarFechaLimite()`;
- `validarRecursos()`;
- `calcularScore()`.

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
