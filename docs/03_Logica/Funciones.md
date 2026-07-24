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

**Modifica:** producción acumulada, stocks, excedentes y población `lotes`.

**Problema:** crea un lote nuevo por día y producto.

**Objetivo futuro:** acumular la producción en un lote comercial abierto.

### `agregarStock(TipoProducto producto, double toneladas)`

**Retorno:** `void`.

**Precondiciones:** producto válido; toneladas positivas.

**Regla:** almacena hasta la capacidad disponible y registra el sobrante como excedente.

### `retirarStock(TipoProducto producto, double toneladas)`

**Retorno:** `boolean`.

**Regla objetivo:** solo retirar si existe stock suficiente; nunca permitir saldo negativo.

## 3. Funciones de LoteProducto

### Implementadas hoy

El agente tiene **una sola función**: `getToneladasLibres()`, que devuelve `toneladasDisponibles - toneladasReservadas` sobre escalares del lote, sin desagregar por ubicación.

Las funciones por ubicación que este documento describía como existentes (`buscarIndiceUbicacion`, `agregarToneladasEnUbicacion`, `getToneladasEnUbicacion`, `getToneladasLibresEnUbicacion`, `retirarToneladasDeUbicacion`) **no existen en el modelo** (hallazgo H-03).

### Funciones objetivo sobre capas

Reemplazan a las anteriores y operan sobre `List<Capa>` (ADR-021, ADR-030):

- `agregarCapa(Agent ubicacion, double toneladas)`: acumula sobre la capa del mismo día y ubicación, o crea una nueva.
- `getToneladasEnUbicacion(Agent ubicacion)`: suma de las capas de esa ubicación.
- `getToneladasLibresEnUbicacion(Agent ubicacion)`: idem, descontando lo reservado.
- `retirarToneladas(Agent ubicacion, double toneladas)`: consume capas en orden FIFO por `diaIngreso`, retira sólo toneladas libres y elimina las capas que quedan en cero.

### Funciones objetivo pendientes

- `reservarToneladasEnUbicacion(...)`;
- `liberarReservaEnUbicacion(...)`;
- `despacharToneladasReservadas(...)`;
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

### `transferirLoteCompleto(LoteProducto lote, Deposito destino)`

**Estado:** restaurada y funcional.

**Comportamiento:** mueve todo el saldo disponible, retira de planta, recibe en depósito, cambia ubicación única y carga flete.

**Problemas:**

- no permite transferencia parcial;
- cambia la ubicación total del lote;
- no utiliza la nueva estructura de saldos;
- mezcla movimiento físico y estado comercial.

**Reemplazo:** `transferirToneladasLote(...)`.

### Firma objetivo

```java
boolean transferirToneladasLote(
    LoteProducto lote,
    Agent origen,
    Agent destino,
    double toneladas,
    TipoMovimiento movimiento
)
```

### Precondiciones objetivo

- lote no nulo;
- origen y destino válidos;
- toneladas positivas;
- saldo libre suficiente;
- destino compatible;
- capacidad suficiente;
- tarifa definida.

### Transacción objetivo

1. validar;
2. reservar temporalmente capacidad destino;
3. retirar saldo origen;
4. actualizar stock local;
5. agregar saldo destino;
6. aplicar costos;
7. registrar movimiento;
8. revertir completamente ante error.

### `transferirProductoADepositos(TipoProducto producto, double toneladasObjetivo)`

**Estado:** implementada.

**Problema:** solo mueve lotes completos menores que el pendiente; si el lote excede el objetivo, detiene el ciclo.

**Objetivo:** permitir transferencias parciales y distribuir según reglas de capacidad.

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

Retorna capacidad parametrizada. Actualmente existen valores provisionales escritos en función.

### Funciones objetivo de planificación

- `localizarExistenciasPedido(Pedido pedido)`;
- `generarPlanesLogisticos(Pedido pedido)`;
- `evaluarPlan(PlanLogistico plan)`;
- `seleccionarMejorPlan(Pedido pedido)`;
- `ejecutarPlan(PlanLogistico plan)`.

### Funciones objetivo de contenedores

- `crearContenedoresParaPedido(Pedido pedido)`;
- `asignarToneladasAContenedores(Pedido pedido)`;
- `programarRetiroVacio(ContenedorExportacion contenedor)`;
- `cerrarIngresoTerminal(ContenedorExportacion contenedor)`.

## 5. Funciones de Deposito

### Implementadas o conocidas

- `puedeRecibir(producto, toneladas)`;
- `recibirProducto(producto, toneladas)`;
- `puedeReservar(producto, toneladas)`;
- `reservarProducto(producto, toneladas)`;
- `liberarReserva(producto, toneladas)`.

### Pendientes

- `retirarProductoReservado(...)`;
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

Debe validar capacidad mayor que cero.

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
