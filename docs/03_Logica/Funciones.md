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

### `transferirLoteCompleto(LoteProducto lote, Deposito destino)`

**Estado:** restaurada y funcional.

**Comportamiento:** mueve todo el saldo libre del lote con `Inventario.moverLote(...)`, que retira las capas de la planta e ingresa las mismas toneladas en el depósito, cambia la ubicación comercial del lote y carga el flete sobre lo efectivamente movido.

**Problemas:**

- no permite transferencia parcial (el motor de capas sí la soporta; falta usarla desde la lógica);
- cambia la ubicación comercial del lote como si fuera única;
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
