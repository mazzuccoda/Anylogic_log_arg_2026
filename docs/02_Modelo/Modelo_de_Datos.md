# Modelo de datos

[← Volver al índice](../README.md)

## 1. Objetivo

Definir variables, relaciones, unidades y reglas de integridad. Los nombres se basan en lo implementado y en la arquitectura objetivo acordada.

## 2. Pedido

| Campo | Tipo | Unidad | Estado | Regla |
|---|---|---|---|---|
| `idPedido` | int | — | Actual | Identificador interno |
| `codigoPedido` | String | — | Actual | Único |
| `producto` | TipoProducto | — | Actual | Obligatorio |
| `cliente` | String/Agent | — | Objetivo | Obligatorio futuro |
| `calidad` | String/OptionList | — | Objetivo | Obligatoria futuro |
| `loteSolicitado` | LoteProducto | — | Actual | Obligatorio para planificar |
| `toneladasSolicitadas` | double | tn | Actual | > 0 |
| `toneladasReservadas` | double | tn | Actual | 0..solicitadas |
| `toneladasDespachadas` | double | tn | Actual | 0..solicitadas |
| `toneladasEntregadas` | double | tn | Actual | 0..solicitadas |
| `diaLlegada` | double | día simulado | Actual | >= 0 |
| `diaLimite` | double | día simulado | Actual | >= llegada |
| `diaReserva` | double | día simulado | Actual | -1 si no existe |
| `diaEntrega` | double | día simulado | Actual | -1 si no existe |
| `terminalDestino` | Terminal | — | Actual | Obligatoria |
| `naviera` | Naviera | — | Actual | Obligatoria |
| `incoterm` | OptionList/String | — | Actual | Informativo por ahora |
| `estado` | EstadoPedido | — | Actual | Control de ciclo |
| `tipoContenedor` | TipoContenedor | — | Actual | Derivado del producto |
| `capacidadContenedorTon` | double | tn/cont | Actual | > 0 |
| `cantidadContenedores` | int | cont | Actual | ceil(tn/capacidad) |
| `contenedores` | List | — | Actual | Entidades individuales |
| `planesEvaluados` | List | — | Actual | Alternativas |
| `planSeleccionado` | PlanLogistico | — | Actual | Nulo hasta selección |
| `asignaciones` | List\<AsignacionPedido\> | — | Actual | **Fuente de verdad** del compromiso (ADR-055) |
| `tuvoReservaParcial` | boolean | — | Actual | Marca histórica para KPIs |
| `tuvoEntregaParcial` | boolean | — | Actual | Marca histórica para KPIs |

`toneladasReservadas`, `idSitioOrigen` y `esCrossDock` quedan como campos **legacy**: sirven para pantalla y compatibilidad, no como saldo. Lo que vale se calcula sobre las asignaciones: `toneladasAsignadasAcumuladas()`, `toneladasReservadasActivas()`, `toneladasEnProceso()`, `toneladasPendientesAsignar()`, `toneladasPendientesEntregar()` y `estaCompleto()`.

### 2.1 AsignacionPedido (ADR-055)

Clase Java plana serializable, no un tipo de agente: el modelo está en 10 de 10 de PLE. Es la unidad de compromiso: un pedido puede tener varias, de distintos orígenes y con distinto circuito.

| Campo | Tipo | Unidad | Regla |
|---|---|---|---|
| `idAsignacion` | String | — | Único; `ASG-0001`, `ASG-0002`… |
| `codigoPedido` | String | — | Pedido comercial al que pertenece |
| `idSitioOrigen` | String | — | `"PLANTA"` o `idUbicacion` del depósito |
| `producto` | TipoProducto | — | El del pedido |
| `circuito` | EstrategiaLogistica | — | Circuito físico de esta fracción |
| `esCrossDock` | boolean | — | Por fracción, no por pedido |
| `toneladasAsignadas` | double | tn | > 0 al crearse |
| `toneladasReservadasActivas` | double | tn | Reserva viva sobre las capas |
| `toneladasContenerizadas` | double | tn | <= asignadas |
| `toneladasDespachadas` | double | tn | <= asignadas |
| `toneladasEntregadas` | double | tn | <= despachadas |
| `diaAsignacion` / `diaPrimerDespacho` / `diaUltimaEntrega` | double | día simulado | -1 si no ocurrió |
| `cerrada` / `cancelada` | boolean | — | Excluyentes |
| `motivoAsignacion` | String | — | Por qué se eligió este origen |
| `costoIncrementalEstimado` / `costoEndToEndEstimado` | double | USD | Del evaluador (ADR-054) |

**Clave de reserva:** `claveReserva() = codigoPedido + "|" + idAsignacion`. Es la clave con la que se reservan y se consumen las capas, y la que llevan el contenedor y el envío. Dos asignaciones del mismo pedido en el mismo sitio no se pisan.

## 3. LoteProducto

### Identidad comercial

| Campo | Tipo | Unidad | Estado |
|---|---|---|---|
| `idLote` | int/String | — | Actual |
| `producto` | TipoProducto | — | Actual |
| `cliente` | String | — | Actual, de `Escenario.cliente_default` (ADR-047) |
| `calidad` | String | — | Actual, de `Escenario.calidad_default` |
| `toneladasObjetivo` | double | tn | Actual, de `Producto.toneladas_objetivo_lote_tn`; 0 = sin cierre por tamaño |
| `estadoComercial` | EstadoComercialLote | — | Actual, `ABIERTO` o `CERRADO` |
| `toneladasIniciales` | double | tn | Actual, **producción acumulada del lote**; sólo crece |
| `getToneladasDisponibles()` | double | tn | Actual, derivado de las capas |
| `getToneladasReservadas()` | double | tn | Actual, derivado de las reservas de las capas |
| `toneladasDespachadas` | double | tn | Objetivo |
| `diaProduccion` | double | día | Actual, primer día de producción del lote |
| `diaApertura` | double | día | Objetivo (hoy lo cubre `diaProduccion`) |
| `diaCierre` | double | día | Objetivo |
| `estado` | EstadoLote | — | Actual |
| `pedidoAsignado` | Pedido | — | Actual, demasiado restrictivo |
| `costoAcumulado` | double | USD | Actual |
| `codigoLoteExterno` | String | — | Actual, código del lote histórico del Excel; vacío en lotes de campaña (ADR-057) |
| `esStockInicial` | boolean | — | Actual, marca el lote de inventario preexistente al día 0 (ADR-057) |

Un lote acumula la producción de varios días: `crearLoteEnPlanta()` reutiliza el lote abierto compatible por producto, cliente y calidad, y cada día de producción agrega una capa nueva con el mismo `idLote` en lugar de una identidad nueva. El lote se cierra al alcanzar `toneladasObjetivo` en producción acumulada; el despacho y la transferencia parcial no lo cierran, porque descuentan capas y no producción histórica (ADR-047). De ahí que `toneladasIniciales` y el saldo físico puedan diferir: el primero responde "cuánto se produjo", las capas responden "cuánto queda y dónde".

Un lote de **stock inicial** (ADR-057) es la excepción: nace cerrado, con `toneladasObjetivo = 0` y `esStockInicial = true`, y nunca recibe producción de campaña. En esos lotes `toneladasIniciales` es el total histórico cargado, no producción del horizonte simulado, y sus capas tienen `diaIngreso` y `diaProduccion` negativos, así que el FIFO las consume antes que las de la campaña.

### Ubicación física: capas

Implementado como capas (ADR-021) en las clases Java `Capa` e `Inventario` (ADR-030). Las listas paralelas que este documento describía nunca existieron en el modelo (hallazgo H-03).

La colección es única y vive en `Main.inventario`, no en cada lote: `Inventario.capas : List<Capa>`. Las capas de un lote se obtienen por `idLote`.

| Campo de `Capa` | Tipo | Regla |
|---|---|---|
| `idLote` | int | lote de origen; inmutable |
| `producto` | TipoProducto | inmutable |
| `diaProduccion` | double | del lote; desempata el FIFO |
| `idUbicacion` | String | `"PLANTA"` o `idUbicacion` del depósito; no vacío |
| `diaIngreso` | double | día simulado del ingreso a esa ubicación; define el orden FIFO |
| `toneladas` | double | > 0; la capa se elimina al llegar a 0 |
| `reservas` | List\<Reserva\> | `(clave, codigoPedido, toneladas, diaReserva)`; su suma no puede superar `toneladas` |
| `costoAlmacenamiento` | double | almacenaje devengado por esa capa |

Invariantes, verificados cada día por `Inventario.validar()`: no puede haber dos capas del mismo lote con la misma ubicación y el mismo `diaIngreso` (se acumulan en una sola); `toneladas` nunca es negativa; lo reservado nunca supera el saldo físico.

### Campos heredados

- `ubicacionActual`.
- `depositoActual`.
- `diaIngresoDeposito`.

Se mantienen como referencia comercial y de presentación; no son saldos y no deben utilizarse en nuevas reglas definitivas. Desde la fase 4 un lote transferido parcialmente tiene capas en varias ubicaciones a la vez, así que `ubicacionActual` es únicamente la ubicación de mayor saldo (`Inventario.ubicacionPrincipalDeLote`); la lista completa es `Inventario.ubicacionesDeLote(idLote)`.

## 4. ContenedorExportacion

Desde la fase 6 los contenedores son reales: `Main.crearContenedoresParaPedido()` crea uno por cada carga en que se divide lo reservado del pedido, y `Envio.contenedor` los vincula con el flujo de transporte. Los campos `hora*` guardan tiempo del modelo, cuya unidad es el día.

Desde la fase 7 `lugarConsolidacion` es el sitio donde efectivamente se estiba —el depósito o la terminal, según `Main.estrategiaConsolidacion` (ADR-040)— y es el que cobra la tarifa y consume la posición del día. Desde la fase 8, un contenedor de un pedido cruzado se estiba siempre en el depósito de cross dock, cobra la tarifa `CROSS_DOCK` y tiene prioridad de despacho, porque su producto no puede quedarse guardado (ADR-041).

| Campo | Tipo | Unidad |
|---|---|---|
| `idContenedor` | String/int | — |
| `pedido` | Pedido | — |
| `lote` | LoteProducto | — |
| `producto` | TipoProducto | — |
| `tipoContenedor` | TipoContenedor | — |
| `capacidadTon` | double | tn |
| `cantidadAsignadaTon` | double | tn |
| `terminalDestino` | Terminal | — |
| `lugarConsolidacion` | Agent | — |
| `camionPortacontenedor` | Camion | — |
| `estado` | EstadoContenedor | — |
| `diasEsperaPosicion` | int | días que el contenedor esperó una posición de consolidación (fase 7) |
| `esCrossDock` | boolean | el contenedor se arma con producto que cruza el depósito sin almacenarse (fase 8) |
| `diaProgramadoCrossDock` | double | día en que se programó el cruce; `-1` si no es cross dock |
| `horaRetiroVacio` | double | día simulado; sin usar (ciclo del vacío no modelado) |
| `horaLlegadaLugarCarga` | double | día simulado; sin usar |
| `horaInicioCarga` | double | día simulado |
| `horaFinCarga` | double | día simulado |
| `horaIngresoTerminal` | double | día simulado |
| `costoEstimado` | double | USD |
| `costoReal` | double | USD |
| `diaProgramadoCrossDock` | double | día |
| `esCrossDock` | boolean | — |

## 5. PlanLogistico

| Campo | Tipo | Unidad |
|---|---|---|
| `idPlan` | String/int | — |
| `pedido` | Pedido | — |
| `estrategia` | EstrategiaLogistica | — |
| `origenProducto` | Agent | — |
| `lugarConsolidacion` | Agent | — |
| `terminal` | Terminal | — |
| `estado` | EstadoPlanLogistico | — |
| `factible` | boolean | — |
| `motivoNoFactible` | String | — |
| `cantidadContenedores` | int | cont |
| `tiempoEstimado` | double | días/horas |
| `costoHistorico` | double | USD |
| `costoIncremental` | double | USD |
| `costoTotalEndToEnd` | double | USD |

Componentes de costo:

- `costoFleteGuarda`;
- `costoAlmacenajeIn`;
- `costoAlmacenajeDiario`;
- `costoAlmacenajeOut`;
- `costoFleteCrossDock`;
- `costoCicloContenedor`;
- `costoConsolidacion`;
- `costoCrossDock`;
- `costoTerminal`;
- `costoTHC`;
- `costoDespachante`.

## 6. Depósito

Campos mínimos por producto:

- capacidad;
- stock;
- reservado;
- tarifa IN;
- tarifa almacenamiento diario;
- tarifa OUT;
- tiempo de recepción;
- tiempo de consolidación;
- posiciones de consolidación;
- posiciones de cross dock.

## 7. Reglas de integridad

### Balance del lote

```text
Producido acumulado =
  disponible físico
+ reservado aún no despachado
+ despachado
+ merma registrada
```

### Balance físico

```text
Disponible físico total = suma(toneladasPorUbicacion)
```

### Reserva

```text
0 <= reservadasPorUbicacion[i] <= toneladasPorUbicacion[i]
```

### Contenedor

```text
0 < cantidadAsignadaTon <= capacidadTon
```

### Pedido

```text
entregado <= despachado <= asignado <= solicitado
```

**C-01, verificado todos los días** (`validarBalancePedidos()`, tolerancia 0,0001 tn):

```text
solicitado = pendiente de asignar + reserva activa + despachado no entregado + entregado
```

**C-02, verificado todos los días** (`validarBalanceProducido()`):

```text
producido = stock en planta + stock en depósitos + en proceso + entregado
```

La reserva de la capa lleva **dos** identificadores y no uno: la `clave` (`pedido|asignación`) define qué reserva consume un despacho, y el `codigoPedido` sigue existiendo porque hay reglas que son del pedido y no de la fracción —el cross dock que no paga almacenaje es la principal—.

### Costos

- una tarifa faltante no equivale a cero;
- un costo histórico no debe volver a contabilizarse como incremental;
- cada costo real debe tener pedido, contenedor o lote asociado.

## 8. Unidades estándar

| Magnitud | Unidad estándar |
|---|---|
| Producto | toneladas |
| Capacidad | toneladas |
| Tiempo operativo | horas o días simulados, documentado por variable |
| Distancia | km |
| Costos | USD |
| Flete | USD/viaje, USD/tn o USD/contenedor |
| Storage | USD/tn/día |

## 9. Tolerancia numérica

Para comparaciones de toneladas:

```java
final double EPS = 0.0001;
```

No comparar `double` mediante igualdad exacta en saldos y capacidades.

## 10. Flota de producto y viajes (ADR-061)

### 10.1 `UnidadFlotaProducto`

| Campo | Tipo | Significado |
|---|---|---|
| `idCamion` | int | Identidad del camión, 1..`camiones_producto` |
| `disponibleDesde` | double | **Única** fuente de disponibilidad: día a partir del cual puede salir |
| `ubicacionBase` | String | Base del camión (hoy siempre `PLANTA`, supuesto declarado) |
| `ubicacionActual` | String | Dónde está o hacia dónde salió |
| `idViajeActual` | String | Último viaje asignado, vacío si está libre |
| `activo` | boolean | Camión disponible en la campaña |
| `camionDiaAcumulado` | double | Camión-día consumido, base de la utilización |
| `viajesCompletados` | int | Viajes que volvieron |

### 10.2 `ViajeProducto`

Un viaje es **un camión y hasta `capacidad_camion_tn` toneladas**. Lleva origen, destino, lote, código de pedido, producto, toneladas, estrategia y si cruza; las fechas de programación, solicitud, salida, llegada, inicio de retorno y regreso; las duraciones de ida y retorno y la distancia; y tres banderas que hacen auditable el ciclo: `stockRetiradoOrigen`, `stockIngresadoDestino` y `fleteRegistrado`. `ocupaSoloFlota` distingue el viaje que solo ocupa camión (granel a terminal, cuyo movimiento físico y costo son del envío) del que además mueve inventario.

`EstadoViajeProducto`: `PROGRAMADO → EN_TRANSITO_DESTINO → DESCARGANDO → RETORNANDO → COMPLETADO`, con `CANCELADO` para el viaje que nunca salió.

### 10.3 Reglas de integridad

- `0 < toneladas <= capacidad_camion_tn`.
- `diaSalida <= diaLlegadaDestino <= diaRegreso`.
- Ningún camión con dos viajes **superpuestos en el tiempo**; tener el viaje en curso y el siguiente programado para cuando vuelve es válido.
- `disponibleDesde` nunca anterior al último regreso de sus viajes vivos.
- No se ingresa carga que no se retiró, ni se completa un viaje que no ingresó.
- El flete se devenga una sola vez por viaje, al salir.
- La reserva del viaje vive en las capas de `Inventario` con `clave = VIAJE|<id>`; no hay un segundo registro de compromisos.

Todo esto lo verifica **C-04** (`reconciliarFlotaProducto()`) todos los días.
