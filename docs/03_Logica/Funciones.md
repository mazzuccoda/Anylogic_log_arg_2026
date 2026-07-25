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

**Estado:** implementada; conceptualmente en transición.

**Objetivo actual:** generar producción diaria, almacenar hasta capacidad y crear lotes por lo efectivamente ingresado.

**Modifica:** producción acumulada, excedentes y población `lotes`. El stock de la planta ya no es una variable: cada ingreso crea el lote y su capa (ADR-023).

**Problema:** crea un lote nuevo por día y producto.

**Objetivo futuro:** acumular la producción en un lote comercial abierto.

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

**Estado:** implementada; será reemplazada.

**Problema:** crea un lote nuevo en cada llamada.

**Objetivo:** encontrar el lote comercial abierto correspondiente y registrar producción incremental.

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
- `getCapacidad(producto)`, `getEspacioDisponible(producto)`, `puedeRecibir(producto, toneladas)`, `puedeReservar(producto, toneladas)`.

Las mutadoras `recibirProducto`, `retirarProducto`, `reservarProducto`, `liberarReserva` y `despacharReservado` se eliminaron: el depósito no tiene saldo propio, y el movimiento lo hace `Main.inventario`.

### Pendientes

- `calcularCostoIn(...)`;
- `calcularCostoStorage(...)`;
- `calcularCostoOut(...)`;
- `puedeConsolidar(...)`;
- `puedeOperarCrossDock(...)`;
- `solicitarPosicionConsolidacion(...)`;
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
