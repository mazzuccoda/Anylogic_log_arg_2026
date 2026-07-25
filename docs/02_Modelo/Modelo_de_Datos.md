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

## 3. LoteProducto

### Identidad comercial

| Campo | Tipo | Unidad | Estado |
|---|---|---|---|
| `idLote` | int/String | — | Actual |
| `producto` | TipoProducto | — | Actual |
| `cliente` | String/Agent | — | Objetivo |
| `calidad` | String/OptionList | — | Objetivo |
| `toneladasObjetivo` | double | tn | Objetivo |
| `toneladasIniciales` | double | tn | Actual, producido; ya no se reescribe |
| `getToneladasDisponibles()` | double | tn | Actual, derivado de las capas |
| `getToneladasReservadas()` | double | tn | Actual, derivado de las reservas de las capas |
| `toneladasDespachadas` | double | tn | Objetivo |
| `diaProduccion` | double | día | Actual |
| `diaApertura` | double | día | Objetivo |
| `diaCierre` | double | día | Objetivo |
| `estado` | EstadoLote | — | Actual |
| `pedidoAsignado` | Pedido | — | Actual, demasiado restrictivo |
| `costoAcumulado` | double | USD | Actual |

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
| `reservas` | List\<Reserva\> | `(codigoPedido, toneladas, diaReserva)`; su suma no puede superar `toneladas` |
| `costoAlmacenamiento` | double | almacenaje devengado por esa capa |

Invariantes, verificados cada día por `Inventario.validar()`: no puede haber dos capas del mismo lote con la misma ubicación y el mismo `diaIngreso` (se acumulan en una sola); `toneladas` nunca es negativa; lo reservado nunca supera el saldo físico.

### Campos heredados

- `ubicacionActual`.
- `depositoActual`.
- `diaIngresoDeposito`.

Se mantienen como referencia comercial y de presentación; no son saldos y no deben utilizarse en nuevas reglas definitivas. Desde la fase 4 un lote transferido parcialmente tiene capas en varias ubicaciones a la vez, así que `ubicacionActual` es únicamente la ubicación de mayor saldo (`Inventario.ubicacionPrincipalDeLote`); la lista completa es `Inventario.ubicacionesDeLote(idLote)`.

## 4. ContenedorExportacion

Desde la fase 6 los contenedores son reales: `Main.crearContenedoresParaPedido()` crea uno por cada carga en que se divide lo reservado del pedido, y `Envio.contenedor` los vincula con el flujo de transporte. Los campos `hora*` guardan tiempo del modelo, cuya unidad es el día.

Desde la fase 7 `lugarConsolidacion` es el sitio donde efectivamente se estiba —el depósito o la terminal, según `Main.estrategiaConsolidacion` (ADR-040)— y es el que cobra la tarifa y consume la posición del día.

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
entregado <= despachado <= reservado <= solicitado
```

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
