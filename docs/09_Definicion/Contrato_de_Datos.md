# Contrato de datos de entrada

[← Volver al índice](../README.md)

**Estado:** propuesta para revisión.

## 1. Objetivo

Definir las tablas de entrada del modelo antes de implementar, para que la fase sintética y la fase Excel usen **exactamente el mismo esquema**. El modelo nunca lee valores hardcodeados: lee estas tablas.

## 2. Modo de carga

| Fase | Origen | Mecanismo |
|---|---|---|
| 1 | Generador sintético | `GeneradorSintetico.generar(...)` puebla las mismas tablas internas |
| 2 | Excel | Archivo `.xlsx`, una hoja por tabla, leído por `ImportadorExcel` a las mismas tablas internas |

Regla: si la fase 2 requiere cambiar alguna función de lógica de negocio, el contrato estaba mal definido.

El origen se elige con dos parámetros de `Main`, sin tocar código:

| Parámetro | Valores | Significado |
|---|---|---|
| `origenDatos` | `OrigenDatos.SINTETICO` (default), `OrigenDatos.EXCEL` | de dónde salen las tablas |
| `rutaExcel` | ruta absoluta o relativa al directorio del modelo | libro a leer cuando el origen es Excel |

Con `EXCEL`, los parámetros del generador (`semillaBase`, `variabilidadProduccion`, `pedidosPorCampania`, ...) se ignoran: el escenario sale de las hojas `Escenario`, `ProduccionPlan` y `PedidoPlan`, filtradas por `idEscenario`, así que un mismo libro puede contener varios escenarios.

`datos/entrada_ejemplo.xlsx` es la plantilla: la genera `tools/generar_excel_ejemplo.py` corriendo el propio `GeneradorSintetico` del modelo, de modo que ambos orígenes producen el mismo escenario y la corrida es comparable. Para cargar datos reales alcanza con reemplazar los valores respetando hojas y encabezados. Los libros con tarifas reales no se versionan (`.gitignore`).

## 3. Convenciones

- Una hoja de Excel por tabla, con el nombre exacto de la tabla.
- Primera fila = encabezados, con los nombres de columna exactos de este documento.
- Sin celdas combinadas, sin totales, sin filas en blanco intermedias.
- Decimal con punto. Fechas `YYYY-MM-DD`. Texto sin espacios al inicio o final.
- Enumerados en MAYÚSCULAS, exactamente como las Option Lists (`JUGO`, `CASCARA`, `ACEITE`, `REEFER_40`, ...).
- Unidades en el nombre de la columna cuando pueda haber ambigüedad (`_tn`, `_usd`, `_dias`, `_usd_tn_dia`).
- Toda tarifa tiene vigencia. **Una tarifa faltante no es cero**: produce error explícito y detiene la corrida.

---

## 4. Tablas maestras

### 4.1 `Producto`

| Columna | Tipo | Regla |
|---|---|---|
| `producto` | enum | `JUGO`, `CASCARA`, `ACEITE` |
| `tipo_contenedor` | enum | `REEFER_40`, `DRY_HC_40`, `IMO_DRY_20` |
| `capacidad_contenedor_tn` | double | > 0 |
| `toneladas_objetivo_lote_tn` | double | ≥ 0. Tamaño del lote comercial acumulativo. `0` = el lote no cierra por tamaño (ADR-047) |
| `descripcion` | texto | Libre |

Reemplaza `obtenerTipoContenedor()` y `obtenerCapacidadContenedorTon()` hardcodeados.

`toneladas_objetivo_lote_tn` es la **única** fuente del tamaño objetivo del lote comercial: el lote acumula la producción diaria del producto y se cierra cuando la producción acumulada lo alcanza. Vive acá porque el tamaño comercial describe al producto; no hay una tabla `LoteComercial` que lo duplique (ADR-047).

### 4.2 `Ubicacion`

| Columna | Tipo | Regla |
|---|---|---|
| `id_ubicacion` | texto | Único |
| `nombre` | texto | |
| `tipo` | enum | `PLANTA`, `DEPOSITO`, `TERMINAL` |
| `habilita_consolidacion` | bool | |
| `habilita_cross_dock` | bool | |
| `contenedores_por_dia` | double | >= 0. Capacidad de consolidación del sitio, en contenedores por día (ADR-048). Reemplaza a `posiciones_consolidacion × contenedores_por_posicion_dia` |
| `posiciones_cross_dock` | int | >= 0 |

Nota: hoy la habilitación se deriva de `capacidad > 0` (ADR-009). El contrato la separa porque un depósito puede almacenar sin poder consolidar. Si se confirma que en la práctica coinciden, se cargan iguales; el modelo no cambia.

**La planta es una ubicación como cualquier otra (ADR-050).** Desde que el circuito de consolidación en planta existe físicamente, la fila `PLANTA` necesita los mismos datos operativos que un depósito: `contenedores_por_dia`, `velocidad_carga_tn_hora`, `velocidad_consolidacion_tn_hora`, tarifa de `TarifaServicioCarga`, y filas de `Distancia` y `TarifaFlete` hacia cada terminal. Hasta `fase-19` esa fila venía en cero y el circuito de planta habría salido gratis e instantáneo.

**Supuesto vigente:** los valores sintéticos de la planta son equivalentes a los del depósito de referencia (estiba 9 / 7 / 14 USD/tn por producto, flete 12 y 13 USD/tn a las dos terminales, 130 y 145 km, 50 tn/h de carga, 30 tn/h de estiba, 4 contenedores/día). Están marcados como supuesto porque no son datos medidos: al cargar el Excel real hay que reemplazarlos, y el nivel de servicio de E-13 depende directamente de `contenedores_por_dia`.

### 4.3 `CapacidadUbicacion`

| Columna | Tipo | Regla |
|---|---|---|
| `id_ubicacion` | texto | FK `Ubicacion` |
| `producto` | enum | |
| `capacidad_tn` | double | >= 0. Cero = no almacena ese producto |

### 4.4 `Distancia`

| Columna | Tipo | Regla |
|---|---|---|
| `origen` | texto | FK `Ubicacion` |
| `destino` | texto | FK `Ubicacion` |
| `distancia_km` | double | > 0 |
| `transito_dias_min` | double | > 0 |
| `transito_dias_moda` | double | >= min |
| `transito_dias_max` | double | >= moda |

### 4.5 `TiemposOperativos`

| Columna | Tipo | Regla |
|---|---|---|
| `operacion` | enum | `CARGA_PRODUCTO`, `DESCARGA_PRODUCTO`, `CONSOLIDACION`, `CROSS_DOCK`, `RETIRO_VACIO`, `INGRESO_TERMINAL` |
| `id_ubicacion` | texto | Vacío = aplica a todas |
| `producto` | enum | Vacío = aplica a todos |
| `dias_min` / `dias_moda` / `dias_max` | double | En días o fracción |

---

## 5. Tablas de tarifas

Cuatro tablas cubren todos los conceptos facturables (ADR-051). **Toda** tarifa lleva `proveedor`, `vigencia_desde`, `vigencia_hasta` (en días de campaña; el generador sintético usa tramos de 31 días) y `habilitada`, porque las tarifas reales se negocian por mes: la consulta resuelve por día de campaña y **falla** si no hay fila vigente o si hay dos vigentes para la misma clave. Un cero silencioso por falta de cobertura sería un error de datos disfrazado de resultado.

Cada tarifa lleva además su `unidad`, y la unidad decide la base de cálculo: `USD_VIAJE`, `USD_TN`, `USD_CONTENEDOR`, `USD_TN_DIA`, `USD_HORA`, `USD_OPERACION` o `USD_PEDIDO`. El último contenedor parcial paga **contenedor completo** en consolidación, cross dock, THC, costo terminal, despachante y round trip; sólo el flete de producto y el almacenaje se cobran por tonelada.

### 5.1 `TarifaFleteProducto`

Flete a granel del producto (planta → depósito, depósito → terminal, planta → terminal).

| Columna | Tipo |
|---|---|
| `origen`, `destino` | texto (`id_ubicacion`) |
| `producto` | enum |
| `tipo_camion` | texto |
| `capacidad_camion_tn` | double > 0 |
| `unidad` | enum: `USD_VIAJE` o `USD_TN` |
| `tarifa` | double ≥ 0, en la unidad declarada |
| `variable_usd_tn` | double ≥ 0; componente por tonelada que se suma a la tarifa por viaje |

### 5.2 `TarifaRoundTrip`

Ciclo del portacontenedor terminal → sitio de consolidación → terminal. Se devenga **al completar** el ciclo: un circuito truncado al cierre de campaña no genera cargo.

| Columna | Tipo |
|---|---|
| `terminal`, `sitio` | texto (`id_ubicacion`) |
| `tipo_contenedor` | enum |
| `tarifa_usd_contenedor` | double ≥ 0 |
| `horas_espera_incluidas` | double ≥ 0; franquicia del ciclo |
| `tarifa_espera_usd_hora` | double ≥ 0; espera por encima de la franquicia |

### 5.3 `TarifaSitio`

Una fila por sitio (planta, depósito o terminal) y producto, con todos los conceptos que cobra ese sitio. Unificarlos evita que la misma clave (`sitio`, `producto`, día) se busque en seis tablas distintas.

| Columna | Tipo | Aplica a |
|---|---|---|
| `id_ubicacion`, `producto` | texto, enum | clave |
| `in_usd_tn`, `out_usd_tn` | double ≥ 0 | depósito |
| `storage_usd_tn_dia` | double ≥ 0 | depósito (la planta cobra 0: el frío es propio) |
| `oportunidad_usd_tn_dia` | double ≥ 0 | planta; **no** es caja (ADR-049) |
| `penalidad_sobrecarga_usd_tn_dia` | double ≥ 0 | planta; **no** es caja (ADR-049) |
| `consolidacion_tarifa` + `consolidacion_unidad` | double, enum | planta, depósito, terminal |
| `cross_dock_tarifa` + `cross_dock_unidad` | double, enum | depósito, terminal |
| `thc_usd_contenedor`, `costo_terminal_usd_contenedor` | double ≥ 0 | terminal |
| `despachante_tarifa` + `despachante_unidad` | double, enum | terminal (`USD_CONTENEDOR` o `USD_PEDIDO`) |

THC y costo terminal entran por contenedor y por producto, que es cómo se facturan hoy; la estructura acepta `USD_TN` para cuando cambie el contrato.

### 5.4 `TarifaEspera`

| Columna | Tipo |
|---|---|
| `tipo_recurso` | texto: `CAMION_PRODUCTO` o `PORTACONTENEDOR` |
| `id_ubicacion` | texto |
| `franquicia_horas` | double ≥ 0 |
| `usd_hora` | double ≥ 0 |

Desde C3 la espera se devenga: `Main.registrarEspera()` cobra las horas que superan `franquicia_horas` en la carga, en la descarga y en la terminal. Con los valores sintéticos (franquicia 3 h, 25 USD/h, proveedor `SUPUESTO_C3`) el cargo es 0, porque un contenedor se carga en menos de una hora; con `franquicia_horas` alta o `usd_hora` en 0 tampoco cambia ningún número.

### 5.5 Valores marcados como supuesto (`SUPUESTO_C3`)

Las tarifas que el usuario todavía no aportó entran con proveedor `SUPUESTO_C3`, para que el barrido mida algo en lugar de cobrar 0. Reemplazarlas en el Excel no requiere tocar código; conviene además cambiar el proveedor por el real, que es lo que permite distinguir en el registro de cargos qué números son supuestos.

| Concepto | Unidad | Jugo | Cáscara | Aceite |
|---|---|---:|---:|---:|
| `in_usd_tn` / `out_usd_tn` (depósito) | USD/tn | 2,5 | 2,0 | 3,0 |
| `thc_usd_contenedor` (terminal) | USD/contenedor | 220 | 150 | 190 |
| `costo_terminal_usd_contenedor` | USD/contenedor | 90 | 70 | 80 |
| `despachante_tarifa` | USD/contenedor | 120 | 120 | 120 |
| `consolidacion_tarifa` (depósito) | USD/contenedor | 225 | 175 | 280 |
| `cross_dock_tarifa` (depósito) | USD/contenedor | 175 | 150 | 240 |
| `consolidacion_tarifa` (terminal) | USD/contenedor | 300 | 250 | 360 |
| `franquicia_horas` / `usd_hora` | h, USD/h | 3 / 25 | 3 / 25 | 3 / 25 |

Los datos operativos de la planta (velocidades, `contenedores_por_dia`, tarifa de consolidación) siguen siendo los del depósito de referencia, también como supuesto (ADR-050).

---

## 6. Tablas de escenario

### 6.1 `Escenario`

| Columna | Tipo | Descripción |
|---|---|---|
| `id_escenario` | texto | Único |
| `descripcion` | texto | |
| `fecha_inicio_campania` | fecha | |
| `duracion_campania_dias` | int | Default 183 |
| `dias_operativos_semana` | int | Default 6 |
| `cantidad_replicas` | int | Default 30 |
| `semilla_base` | int | |
| `camiones_producto` | int | Flota planta→depósito, contada en camión-día (ADR-044) |
| `camiones_portacontenedor` | int | Flota depósito→terminal, capacidad del pool `flotaPortacontenedores` |
| `capacidad_camion_tn` | double | Toneladas por viaje: mover más son más viajes |
| `velocidad_camion_kmh` | double | Define cuánta jornada consume un viaje |
| `horas_operativas_dia` | double | Jornada del camión, 0 < x ≤ 24 |
| `indisponibilidad_flota` | double | 0..1 |
| `variabilidad_produccion` | double | Ruido de la producción diaria |
| `variabilidad_demanda` | double | Ruido del tamaño del pedido |
| `deterministico` | bool | Además de anular el ruido, elimina los sorteos del plan de pedidos (ADR-043) |
| `factor_produccion` | double | Multiplica la producción media |
| `factor_capacidad_planta` | double | Multiplica la capacidad de la planta |
| `factor_capacidad_deposito` | double | Multiplica la capacidad de cada depósito |
| `factor_storage` | double | Multiplica las tarifas de almacenaje |
| `ventana_demanda` | double | 0..1: fracción del horizonte en la que llegan los pedidos |
| `estrategia_consolidacion` | enum | `CONSOLIDACION_DEPOSITO` o `CONSOLIDACION_TERMINAL` |
| `cliente_default` | texto | No vacío. Cliente del lote comercial mientras haya un solo cliente (ADR-019, ADR-047) |
| `calidad_default` | texto | No vacío. Calidad del lote comercial mientras haya una sola calidad |
| `umbral_alerta_pct` | double | 0..200. Ocupación de planta a partir de la cual se avisa (default 85) |
| `umbral_objetivo_pct` | double | 0..200. Nivel nominal de referencia (default 100) |
| `umbral_sobrecarga_pct` | double | 0..200, ≥ `umbral_objetivo_pct`. Sobrecarga crítica (default 105) |
| `dias_forecast` | int | >= 0. Horizonte del forecast de producción, default 7 (ADR-048) |
| `politica_frio_propio` | texto | `FLEXIBLE` o `REACTIVA` (ADR-048) |
| `habilita_cross_dock` | bool | |
| `politica_seleccion` | enum | Quién elige el circuito de cada pedido (ADR-054): `FIJA_PLANTA`, `FIJA_DEPOSITO`, `FIJA_CROSS_DOCK_DEPOSITO`, `FIJA_CROSS_DOCK_TERMINAL`, `MANUAL` reproducen la conducta previa al evaluador; `PRIORIDAD_FRIO_PROPIO`, `MENOR_COSTO_INCREMENTAL_FACTIBLE` y `MENOR_COSTO_END_TO_END_FACTIBLE` lo activan |
| `servicio_minimo_proyectado` | double | 0..1. Por encima de 0, ninguna alternativa tardía se elige mientras haya una que llega a tiempo (default 0,95) |
| `factor_tarifa_flete` | double | > 0. Sensibilidad sobre la tarifa de flete de producto, en la cotización **y** en el devengo |
| `factor_tarifa_round_trip` | double | > 0. Ídem sobre el ciclo del portacontenedor |
| `factor_tarifa_cross_dock` | double | > 0. Ídem sobre el servicio de cross dock |
| `factor_tarifa_terminal` | double | > 0. Ídem sobre THC, costo de terminal y despachante |
| `factor_consolidacion_planta` | double | > 0. Multiplica `contenedores_por_dia` de la planta |
| `factor_cupo_cross_dock` | double | > 0. Multiplica el cupo diario de cross dock de cada depósito |
| `factor_capacidad_terminal` | double | > 0. Multiplica `contenedores_por_dia` de la terminal |
| `dias_anticipacion_planificacion_default` | int | >= 0. Sólo para derivar `dia_conocimiento` cuando el libro no lo trae (default 14, ADR-059) |
| `dias_anticipacion_retiro_default` | int | >= 0. Ídem para `dia_apertura_retiro_vacio` (default 7) |
| `dias_entre_cutoff_y_etd_default` | int | >= 0. Ídem para `dia_etd` (default 1) |
| `permite_reserva_antes_retiro` | bool | Si el pedido puede reservar inventario antes de que abra el retiro del vacío (default `true`) |
| `permite_transferencia_antes_retiro` | bool | Si su demanda cuenta para las transferencias preventivas planta→depósito (default `true`) |
| `permite_reserva_capacidad_futura` | bool | Si el contenedor comprometido reserva una posición de consolidación dentro de su ventana (default `true`) |
| `politica_reprogramacion_buque` | texto | Qué pasa con el saldo que perdió el cut-off: `CONTINUAR` lo rolea y lo entrega tarde (default), `CANCELAR` lo da de baja |
| `politica_prioridad` | enum | `FECHA_LIMITE`, `FIFO`, `MAYOR_VOLUMEN` |
| `tipo_cambio_ars_usd` | double | Para tarifas en ARS |

### 6.2 `ProduccionPlan`

Producción diaria esperada. Con datos sintéticos la genera el modelo; con Excel se carga la serie real o estimada.

| Columna | Tipo | Regla |
|---|---|---|
| `id_escenario` | texto | |
| `dia` | int | 0..duración. Alternativamente `fecha` |
| `producto` | enum | |
| `produccion_tn` | double | >= 0 |

Si la tabla trae una sola fila por producto sin `dia`, se interpreta como media diaria constante para toda la campaña.

### 6.3 `PedidoPlan`

| Columna | Tipo | Regla |
|---|---|---|
| `id_escenario` | texto | |
| `codigo_pedido` | texto | Único por escenario |
| `cliente` | texto | |
| `producto` | enum | |
| `calidad` | texto | |
| `lote_solicitado` | texto | Vacío = cualquier lote del producto/calidad |
| `toneladas_solicitadas` | double | > 0 |
| `terminal` | texto | FK `Ubicacion` tipo `TERMINAL` |
| `naviera` | enum | |
| `incoterm` | texto | Informativo |
| `buque` | texto | Informativo. Agrupa los pedidos que comparten cut-off |
| `viaje_buque` | texto | Informativo |
| `dia_conocimiento` | int | >= 0. Desde cuándo el pedido existe para planificar |
| `dia_apertura_retiro_vacio` | int | >= `dia_conocimiento`. Primer día de retiro físico del vacío |
| `dia_cutoff_fisico` | int | >= `dia_apertura_retiro_vacio`. Último día de ingreso a terminal |
| `dia_etd` | int | >= `dia_cutoff_fisico`. Salida estimada del buque |

`cliente` y `calidad` se incluyen desde el inicio aunque el alcance actual use un solo valor: son necesarios para identificar el lote comercial abierto y agregarlos después implicaría migrar datos.

Las cuatro fechas son la ventana marítima (ADR-059) y tienen que respetar `dia_conocimiento <= dia_apertura_retiro_vacio <= dia_cutoff_fisico <= dia_etd`. El servicio se mide contra `dia_cutoff_fisico`, no contra `dia_etd`: el compromiso operativo es que el contenedor esté en la terminal, y lo que pasa después es del buque.

**Forma anterior a ADR-059.** Un libro con `dia_llegada` y `dia_limite` sigue cargando: `dia_cutoff_fisico = dia_limite`, `dia_conocimiento = dia_llegada` (no se inventa conocimiento anterior al que el libro declara), `dia_apertura_retiro_vacio = max(dia_llegada, cut-off − dias_anticipacion_retiro_default)` y `dia_etd = cut-off + dias_entre_cutoff_y_etd_default`.

### 6.4 `StockInicial` (opcional, ADR-057)

Inventario físico preexistente al día 0. Reemplaza a la tabla `LoteInicial` que este documento proyectaba: no lleva `toneladas_objetivo` porque un lote histórico está cerrado y no recibe producción de campaña.

| Columna | Tipo | Obligatoria | Regla |
|---|---|---|---|
| `id_escenario` | texto | no | Si falta, todas las filas aplican al escenario que se importa |
| `id_stock` | texto | no | Único por escenario. Identifica la fila, no el lote. Si falta: `SI-<fila>` |
| `codigo_lote` | texto | no | Puede repetirse: el mismo lote puede estar en varias ubicaciones. Si falta: `SI-<producto>-<ubicacion>-<fila>`, o sea una partida por fila |
| `producto` | enum | **sí** | Un `codigo_lote` no puede tener dos productos |
| `id_ubicacion` | texto | **sí** | FK `Ubicacion`, habilitada, tipo `PLANTA` o `DEPOSITO`. Nunca `TERMINAL` |
| `toneladas` | double | **sí** | > 0. Una fila en cero se ignora: cero no es stock, es ausencia |
| `dia_produccion` | double | no | <= 0, día relativo al inicio de campaña. Si falta: 0 |
| `dia_ingreso` | double | no | <= 0 y >= `dia_produccion`. Si falta: 0 |
| `cliente` | texto | no | Un `codigo_lote` no puede tener dos clientes. Si falta: `cliente_default` del escenario |
| `calidad` | texto | no | Un `codigo_lote` no puede tener dos calidades. Si falta: `calidad_default` del escenario |

La identidad del lote histórico es `codigo_lote + producto + cliente + calidad`: las filas que la comparten son capas del mismo lote en distintos sitios.

Las fechas son negativas porque el FIFO ordena por `dia_ingreso` y después por `dia_produccion`: el stock histórico sale antes que la producción de la campaña. La antigüedad anterior al día 0 **no** se factura ni se imputa en el costeo (ADR-057).

La hoja es **opcional**: un libro sin stock inicial se interpreta como inventario inicial cero, para que los libros anteriores a esta versión sigan corriendo. La plantilla que genera `tools/generar_excel_ejemplo.py` la incluye con encabezados aunque el escenario sintético no tenga stock previo.

Es también la única hoja que se escribe a mano fuera del volcado del modelo, así que se lee de forma tolerante. Es la excepción a la regla general de nombres exactos y sólo aplica a esta hoja:

- **Nombre de la hoja**: cualquiera que normalizado empiece con `STOCK` y contenga `INICIAL` (`StockInicial`, `Stock Inicial`, `StockcInicial`). Una errata de tipeo no puede leerse como "no hay stock inicial", que es el silencio más caro posible.
- **Encabezados alternativos**: `id_ubicacion` acepta `ubicacion`, `deposito`, `sitio` o `lugar`; `toneladas` acepta `inicial`, `stock`, `stock_inicial`, `stock_tn` o `tn`. La comparación ignora mayúsculas, acentos, espacios y guiones bajos.
- **Ubicaciones escritas como en el negocio**: se acepta el id exacto y, si no coincide, un único id que sea prefijo del nombre comercial (`RUTA 9` → `RUTA9`, `DODERO BARRACAS` → `DODERO`, `NORRY ` → `NORRY`). Un nombre ambiguo se informa con los candidatos en vez de elegir uno.
- Faltar producto, ubicación o toneladas, un producto desconocido, una ubicación que no resuelve o un texto que no es número siguen siendo **errores de datos** que abortan el arranque con la fila. La tolerancia es de formato, no de contenido.

Esta lectura permite la forma agregada que se usa en la práctica —una grilla `producto × depósito` con la tenencia inicial, sin fechas ni identidad de lote— donde cada fila con toneladas es una partida y el stock queda fechado el día 0. Sin `dia_ingreso` no hay antigüedad que imputar, y el FIFO igual saca el stock inicial antes que la producción de campaña porque sus lotes se crean primero y el desempate final es por `idLote`.

Capacidad: la suma del stock inicial por ubicación y producto se compara contra la capacidad efectiva del escenario. En un **depósito** excederla es error de datos y aborta el arranque; en la **planta** queda como advertencia en la consola, porque la capacidad nominal del frío propio es un umbral de lectura y no un tope (ADR-048), y arrancar la campaña en sobrecarga es un caso real que el modelo mide (`ton_dia_sobre_nominal`, `pico_ocupacion_planta_pct`, penalidad).

---

## 7. Validación de entrada

Antes de simular, el modelo valida las tablas y **aborta con mensaje explícito** si:

1. falta una tabla obligatoria o una columna;
2. hay una FK inexistente (ubicación, producto, terminal, naviera);
3. una capacidad, tarifa o duración es negativa, o una capacidad de contenedor es cero;
4. falta una tarifa requerida por alguna combinación alcanzable (producto × ubicación × terminal × naviera presente en el escenario);
5. `vigencia` no cubre todo el horizonte de campaña, o hay tarifas superpuestas para la misma clave;
6. la ventana marítima no respeta `dia_conocimiento <= dia_apertura_retiro_vacio <= dia_cutoff_fisico <= dia_etd`, o `toneladas_solicitadas <= 0`;
7. un `lote_solicitado` referenciado no existe ni puede producirse;
8. `transito_dias_min > moda > max` está mal ordenado.

Los errores se listan **todos juntos** en un CSV (`errores_entrada.csv`) en lugar de abortar en el primero: corregir un Excel de a un error por corrida es inviable.

## 7.bis Estado de implementación

El paso 2 del [roadmap](../07_Roadmap/Roadmap.md) implementa la primera mitad del contrato. Las tablas viven en la clase Java `DatosEntrada` (ADR-030: no consumen tipos de agente) y las llenan indistintamente `GeneradorSintetico` e `ImportadorExcel`, que **no tocan la lógica de negocio**.

Hojas y encabezados que lee hoy el importador (los que faltan corresponden a tablas todavía no implementadas):

| Hoja | Columnas |
|---|---|
| `Escenario` | `id_escenario`, `duracion_campania_dias`, `semilla_base`, `variabilidad_produccion`, `variabilidad_demanda`, `pedidos_por_campania`, `toneladas_medias_pedido`, `plazo_pedido_dias`, `camiones_producto`, `camiones_portacontenedor`, `capacidad_camion_tn`, `velocidad_camion_kmh`, `horas_operativas_dia`, `factor_produccion`, `factor_capacidad_planta`, `factor_capacidad_deposito`, `factor_storage`, `ventana_demanda`, `habilita_cross_dock`, `deterministico`, `estrategia_consolidacion`, `cliente_default`, `calidad_default`, `umbral_alerta_pct`, `umbral_sobrecarga_pct`, `umbral_objetivo_pct`, `dias_forecast`, `politica_frio_propio`, `politica_seleccion`, `servicio_minimo_proyectado`, `factor_tarifa_flete`, `factor_tarifa_round_trip`, `factor_tarifa_cross_dock`, `factor_tarifa_terminal`, `factor_consolidacion_planta`, `factor_cupo_cross_dock`, `factor_capacidad_terminal`, `dias_anticipacion_planificacion_default`, `dias_anticipacion_retiro_default`, `dias_entre_cutoff_y_etd_default`, `permite_reserva_antes_retiro`, `permite_transferencia_antes_retiro`, `permite_reserva_capacidad_futura`, `politica_reprogramacion_buque` |
| `Producto` | `producto`, `tipo_contenedor`, `capacidad_contenedor_tn`, `toneladas_objetivo_lote_tn` |
| `Ubicacion` | `id_ubicacion`, `tipo`, `habilitada`, `velocidad_carga_tn_hora`, `velocidad_descarga_tn_hora`, `velocidad_consolidacion_tn_hora`, `capacidad_diaria_tn`, `contenedores_por_dia`, `posiciones_cross_dock` |
| `CapacidadUbicacion` | `id_ubicacion`, `producto`, `capacidad_tn` |
| `Distancia` | `origen`, `destino`, `distancia_km` |
| `TarifaSitio` | `id_ubicacion`, `producto`, `in_usd_tn`, `storage_usd_tn_dia`, `out_usd_tn`, `oportunidad_usd_tn_dia`, `penalidad_sobrecarga_usd_tn_dia`, `consolidacion_tarifa`, `consolidacion_unidad`, `cross_dock_tarifa`, `cross_dock_unidad`, `thc_usd_contenedor`, `costo_terminal_usd_contenedor`, `despachante_tarifa`, `despachante_unidad`, `proveedor`, `vigencia_desde`, `vigencia_hasta`, `habilitada` |
| `TarifaFleteProducto` | `origen`, `destino`, `producto`, `tipo_camion`, `capacidad_camion_tn`, `unidad`, `tarifa`, `variable_usd_tn`, `proveedor`, `vigencia_desde`, `vigencia_hasta`, `habilitada` |
| `TarifaRoundTrip` | `terminal`, `sitio`, `tipo_contenedor`, `tarifa_usd_contenedor`, `horas_espera_incluidas`, `tarifa_espera_usd_hora`, `proveedor`, `vigencia_desde`, `vigencia_hasta`, `habilitada` |
| `TarifaEspera` | `tipo_recurso`, `id_ubicacion`, `franquicia_horas`, `usd_hora`, `proveedor`, `vigencia_desde`, `vigencia_hasta`, `habilitada` |
| `ProduccionPlan` | `id_escenario`, `dia`, `producto`, `produccion_tn` |
| `PedidoPlan` | `id_escenario`, `codigo_pedido`, `producto`, `toneladas_solicitadas`, `terminal`, `naviera`, `incoterm`, `buque`, `viaje_buque`, `dia_conocimiento`, `dia_apertura_retiro_vacio`, `dia_cutoff_fisico`, `dia_etd`. Acepta también la forma anterior a ADR-059 con `dia_llegada` y `dia_limite` |
| `StockInicial` (opcional) | `id_escenario`, `id_stock`, `codigo_lote`, `producto`, `id_ubicacion`, `toneladas`, `dia_produccion`, `dia_ingreso`, `cliente`, `calidad` |

Las columnas se buscan por nombre, no por posición: se pueden reordenar o agregar columnas propias. Una hoja o una columna faltante se informa junto con todas las demás antes de validar; un número tipeado como texto se acepta, y un texto que no sea número indica hoja, fila y columna.

| Tabla | Implementada | Diferencias con este documento |
|---|---|---|
| `Producto` | sí | — |
| `Ubicacion` | sí | sin `habilita_consolidacion` ni `habilita_cross_dock`: la habilitación se deriva de la capacidad. La capacidad diaria de consolidación es `contenedores_por_dia` (fase 19, ADR-048) y la de cross dock es `posiciones_cross_dock`, en operaciones por día (fase 8). Agrega las velocidades operativas que hoy son parámetros de los agentes |
| `CapacidadUbicacion` | sí | — |
| `Distancia` | sí | sin tránsito min/moda/max: el tránsito todavía se deriva de la distancia y la velocidad del camión |
| `TiemposOperativos` | no | los tiempos siguen calculándose como toneladas ÷ velocidad |
| `TarifaFleteProducto` | sí, todos los tramos | reemplaza a la fórmula cableada `costoFijoViajePD + km × costoKmPD`: desde C1 el flete se cobra con la tarifa de la tabla, en `USD_VIAJE` o `USD_TN` según la unidad de la fila (ADR-051) |
| `TarifaRoundTrip` | sí | reemplaza al `× 2` de la fórmula cableada. Se devenga al completar el ciclo, y sólo en los circuitos que usan portacontenedor. `horas_espera_incluidas` y `tarifa_espera_usd_hora` tienen que coincidir con la fila de `TarifaEspera` del mismo sitio: `validarDatosEntrada()` aborta si difieren |
| `TarifaSitio` | sí, completa | desde C3 se devengan las once categorías: consolidación, cross dock, almacenaje, oportunidad, penalidad, IN, OUT, THC, costo terminal y despachante (ADR-053). Los valores de IN, OUT, THC, costo terminal y despachante son supuestos (§5.5) |
| `TarifaEspera` | sí | `registrarEspera()` cobra las horas que superan la franquicia en carga, descarga y terminal. Con los tiempos sintéticos el cargo es 0 |
| `Escenario` | sí, parcial | implementa `id_escenario`, `duracion_campania_dias`, `semilla_base`, `variabilidad_produccion` y `variabilidad_demanda`, y agrega `pedidos_por_campania`, `toneladas_medias_pedido` y `plazo_pedido_dias`, que son los que gobiernan la demanda sintética |
| `ProduccionPlan` | sí | serie diaria completa por producto |
| `PedidoPlan` | sí | sin cliente, calidad, lote solicitado, naviera ni incoterm: el modelo aún no los usa |
| `StockInicial` | sí, opcional (ADR-057) | reemplaza a `LoteInicial`. El stock inicial entra como capas reales de `Inventario` con lotes `esStockInicial = true`; sin `toneladas_objetivo`, porque el lote histórico queda cerrado |

La validación existe y corre en el arranque: `Main.cargarDatosEntrada()` obtiene las tablas del origen elegido, ejecuta `DatosEntrada.validar()` y aborta con la lista completa de errores. Todavía no escribe `errores_entrada.csv`. Cubre los puntos 2, 3, 4 y 6 de la sección anterior; los puntos 1, 5, 7 y 8 corresponden a columnas o tablas no implementadas.

## 8. Salidas

Simétricas a la entrada, siempre con `id_escenario`, `replica`, `semilla` y `version_modelo`:

| Archivo | Contenido |
|---|---|
| `resumen_escenario.csv` | Una fila por escenario y réplica con todos los KPIs |
| `registro_costos.csv` | Un registro por costo devengado (categoría, pedido, lote, contenedor, ubicación, base, tarifa, importe, histórico) |
| `series_diarias.csv` | Stock, reservado, producción, excedente y utilización por día, producto y ubicación |
| `esperas_recursos.csv` | Operaciones postergadas por día, recurso y causa |
| `errores_entrada.csv` | Validaciones fallidas |
