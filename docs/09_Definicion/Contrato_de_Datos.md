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
| `material` | texto | Opcional (ADR-067). Subdivisión productiva del producto (p. ej. `JCL`, `JCCL`, `PULPA` de `JUGO`). Vacío = el producto no distingue material. Junto con `producto` forma la clave de la tabla: puede haber varias filas por producto, una por material |
| `tipo_contenedor` | enum | `REEFER_40`, `DRY_HC_40`, `IMO_DRY_20` |
| `capacidad_contenedor_tn` | double | > 0. Puede variar por material del mismo producto (p. ej. `PULPA` entra 20 tn por `REEFER_40` contra 24 tn de `JCL`/`JCCL`) |
| `toneladas_objetivo_lote_tn` | double | ≥ 0. Tamaño del lote comercial acumulativo. `0` = el lote no cierra por tamaño (ADR-047) |
| `descripcion` | texto | Libre |

Reemplaza `obtenerTipoContenedor()` y `obtenerCapacidadContenedorTon()` hardcodeados; ambos resuelven hoy por `(producto, material)` (ADR-067), no sólo por producto.

**Material (ADR-067).** No es sólo trazabilidad: un pedido de un material no puede satisfacerse con stock de otro material del mismo producto (dos materiales del mismo producto no son sustituibles). Participa en la identidad física del lote y de la capa, igual que `cliente`/`calidad` (ADR-047), y en la resolución de contenedor de exportación. La capacidad física de almacenamiento (`CapacidadUbicacion`) sigue agregada por `producto` solamente: no hay dato de capacidad por material, y un tanque de jugo no separa físicamente sus materiales en compartimentos. `material = ""` es el sentinel de "producto que no distingue" (o de una corrida sintética/anterior a este ADR); `materialesDe(producto)` deriva la lista de materiales válidos de esta misma tabla.

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
| `latitud` | double | Opcional (ADR-072). Grados decimales, entre −56 y −21. Alias aceptado: `lat` |
| `longitud` | double | Opcional (ADR-072). Grados decimales, entre −74 y −53. Alias aceptados: `long`, `lon` |

Nota: hoy la habilitación se deriva de `capacidad > 0` (ADR-009). El contrato la separa porque un depósito puede almacenar sin poder consolidar. Si se confirma que en la práctica coinciden, se cargan iguales; el modelo no cambia.

**La planta es una ubicación como cualquier otra (ADR-050).** Desde que el circuito de consolidación en planta existe físicamente, la fila `PLANTA` necesita los mismos datos operativos que un depósito: `contenedores_por_dia`, `velocidad_carga_tn_hora`, `velocidad_consolidacion_tn_hora`, tarifa de `TarifaServicioCarga`, y filas de `Distancia` y `TarifaFlete` hacia cada terminal. Hasta `fase-19` esa fila venía en cero y el circuito de planta habría salido gratis e instantáneo.

**La red física del modelo es un superconjunto de la del libro (ADR-069).** Los depósitos y terminales son agentes del canvas de `Main` (limitación de PLE: no se crean en tiempo de ejecución), hoy `FRINOA`, `NORRY`, `BOREAS`, `RUTA9`, `DODERO`, `GRUPO_PAZ` y `CONTROL_UNION` más las terminales `ZARATE` y `T4`. Un escenario puede declarar **menos**: el agente que el libro no nombra queda fuera de la red de esa corrida, con advertencia, y no exige capacidades ni tarifas. Al revés no: un depósito habilitado en `Ubicacion` **sin** agente en `Main` es un error de datos que aborta el arranque nombrándolo, porque no podría almacenar nada.

**Un depósito sin capacidad declarada de un producto no necesita tarifas de ese producto (ADR-069).** `capacidad_tn = 0` en `CapacidadUbicacion` es la forma de declarar "no lo almacena": la validación de cobertura y el refresco diario de tarifas omiten esa combinación en vez de exigir almacenaje, flete y round trip de algo que la corrida nunca va a leer. En el maestro real `GRUPO_PAZ` y `CONTROL_UNION` son depósitos de cáscara y declaran cero de jugo y de aceite.

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

**Formato alternativo de vigencia (ADR-068, ADR-070).** `ImportadorExcel` acepta, sheet por sheet, este formato original (`vigencia_desde`/`vigencia_hasta` + una columna de importe) **o** una grilla de columnas cuyo encabezado es un rango de días (`'0-31'`, `'31-59'`, `'59-90'`, … `'334-999'`) + una columna `Moneda` — se detecta por el nombre de la hoja (`TarifaFleteProducto` vs. `TarifaFleteCamionproducto`) o por si existe la columna del formato original (`TarifaRoundTrip.tarifa_usd_contenedor`). Con la grilla, cada fila se expande a una fila interna por tramo antes de llegar a `DatosEntrada`, así que la resolución por día de campaña no cambia.

Reglas de la grilla de vigencia (ADR-070):

- **La vigencia la declara el encabezado del libro, no el código.** No hay una lista fija de nombres de columna ni una cantidad fija de tramos: se leen todas las columnas cuyo encabezado tenga la forma `entero-entero`, en el orden en que aparecen. Una ronda del relevamiento que renombre los cortes (`'60-89'` → `'59-90'`) se lee igual de bien.
- **El tope de cada tramo es el día anterior al inicio del siguiente**, no el número que dice el encabezado: el maestro escribe el mismo corte de dos formas (`'59-90'` y `'60-89'`) y tomar el declarado deja huecos o solapes de un día. `'0-31'` rige los días 0–30, `'31-59'` los 31–58, `'59-90'` los 59–89.
- **El último tramo queda abierto** (hasta 9999): el flujo cierra envíos después del último día de campaña y esos cargos se cotizan con la tarifa del día de devengo — es el mismo criterio con el que el maestro cierra `TarifaEspera`.
- **Todas las hojas de tarifa del libro tienen que compartir los cortes.** `TarifaSitio` se acumula por posición de tramo entre siete hojas; si una declara otra grilla, la carga **falla nombrando la hoja y su primer tramo** en vez de mezclar vigencias.
- **Una hoja de tarifas sin ninguna columna de rango de días es un error de datos**, no una tarifa cero.

**Conversión de moneda (ADR-070).** La hoja `Tipo_cambio` trae el tipo de cambio ARS/USD en la **misma grilla de rangos de días** (`id_escenario` + una columna por tramo). Toda tarifa cuya `Moneda` no sea `USD` —incluido `$`, que es como el maestro marca los pesos argentinos— se divide por el tipo de cambio **vigente el día en que empieza su tramo**, al leer: la tarifa que llega a `DatosEntrada` ya está en USD. `Moneda` vacía se toma como USD. La hoja es **opcional** mientras el libro cotice todo en USD (el contrato original, `entrada_ejemplo.xlsx`); pero si hay una tarifa en pesos y no hay tipo de cambio positivo para ese día, la carga **falla nombrando la moneda y el día**: cotizar en cero abarata la alternativa y cambia la decisión del evaluador.

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

**Formato alternativo dividido por hoja (ADR-068).** El maestro nuevo no trae `TarifaSitio` como una sola hoja: la parte en `Tarifa_almacenaje` (`in`/`storage`/`out`, clave `Proveedor` + `Tipo` = sitio + producto), `Gastos_terminal` (`costo_terminal_usd_contenedor`, clave `Puerto de Salida` + `Tipo Contenor`), `Despachante` (`despachante_tarifa`, clave `Lugar de consolidado` + `Tipo Contenor`), `TarifaConsolidado` y `TarifaCross_docking` (`consolidacion_tarifa`/`cross_dock_tarifa`, clave `Lugar Consolidado` + `Tipo de Contenedor` — **sin** columna de terminal: confirmado que no dependen de ella, igual que el formato original). `ImportadorExcel.leerTarifaSitioMaestroNuevo()` las acumula todas por `(id_ubicacion, producto)` — resolviendo producto desde el tipo de contenedor cuando la hoja no lo trae directo — y emite filas `TarifaSitio` internas idénticas a las del formato original. `TarifaConsolidado`/`TarifaCross_docking` toleran también los nombres sin el prefijo `Tarifa` (`Consolidado`/`Cross_docking`).

`thc_usd_contenedor` es la excepción: **no** entra a este acumulador. Ver `TarifaThc` (§5.3.bis).

### 5.3.bis `TarifaThc` (ADR-068, sólo en el maestro nuevo)

Confirmado por el usuario: el THC lo factura la naviera, no el sitio ("es el costo de THC de la marítima"). La hoja `Gastos_THC` no encaja en la clave `(id_ubicacion, producto)` de `TarifaSitio` — tiene su propia tabla en `DatosEntrada` con clave `(naviera, tipo_contenedor)`.

| Columna (hoja `Gastos_THC`) | Tipo | Nota |
|---|---|---|
| `Proveedor` | texto | naviera; valores fuera del enum `Naviera` (p. ej. `FORWARDER`, `SILVERFREIGHT`: forwarders/alternativas, no líneas marítimas) se descartan con `advertencias`, no abortan el import |
| `Tipo Contenor` | texto | `'40 HC'`/`'20 dry'`/`'40 RF'` o el nombre del enum `TipoContenedor` directo |
| `Moneda` + 12 buckets | texto, double | igual que el resto de las hojas de tarifa (§5, nota de vigencia) |

Resolución en tiempo de corrida: `datos.thcUsdContenedor(dia, naviera, tipoContenedor)` (nuevo overload; el original `thcUsdContenedor(dia, idTerminal, producto)` sigue existiendo y es lo que se usa si el libro no trae `Gastos_THC`). `Main.thcUsdContenedorPedido(dia, pedido, terminal)` decide cuál overload usar según si `datos.tarifasThc` está vacía, y calcula el tipo de contenedor fresco vía `producto`+`material` del pedido en vez de leer `pedido.tipoContenedor` (ver ADR-068 para por qué ese campo no es confiable en los puntos donde se cobra THC).

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
| `camiones_producto` | int | Flota planta→depósito. Con ADR-061 son camiones **físicos** con agenda propia; con `habilita_flota_producto_multidiaria = false`, camión-día agregado (ADR-044) |
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
| `permite_fallback_politica_fija` | bool | Con una politica `FIJA_*`, si el saldo que el circuito fijo no puede tomar por capacidad pasa al evaluador (default `false`, ADR-060) |
| `exportar_diagnostico_capacidad` | bool | Si la corrida escribe `resultados/capacidad_por_dia.csv`, `asignaciones_capacidad.csv` y `asignaciones_capacidad_decisiones.csv` (default `false`) |
| `fecha_inicio_campania` | texto o fecha | Opcional. Fecha calendario del **día 1** de la campaña, en `YYYY-MM-DD` (ADR-071). Vacía, manda el parámetro de corrida `fechaInicioCampania`; vacíos los dos, las columnas de fecha salen vacías |
| `politica_prioridad` | enum | `FECHA_LIMITE`, `FIFO`, `MAYOR_VOLUMEN` |
| `tipo_cambio_ars_usd` | double | Para tarifas en ARS |

### 6.2 `ProduccionPlan`

Producción diaria esperada. Con datos sintéticos la genera el modelo; con Excel se carga la serie real o estimada.

| Columna | Tipo | Regla |
|---|---|---|
| `id_escenario` | texto | |
| `dia` | int | 0..duración. Alternativamente `fecha` |
| `producto` | enum | |
| `material` | texto | Opcional (ADR-067). Si `Producto` tiene más de un material para este producto, cada material se produce en una fila separada — `Planta.producir()` itera `materialesDe(producto)` y busca la fila exacta. Vacío sólo es válido si `Producto` tampoco distingue material para ese producto (si no, `DatosEntrada.validar()` lo rechaza explícitamente en vez de producir 0 en silencio) |
| `produccion_tn` | double | >= 0 |

Si la tabla trae una sola fila por producto sin `dia`, se interpreta como media diaria constante para toda la campaña.

### 6.3 `PedidoPlan`

| Columna | Tipo | Regla |
|---|---|---|
| `id_escenario` | texto | |
| `codigo_pedido` | texto | Único por escenario |
| `cliente` | texto | |
| `producto` | enum | |
| `material` | texto | Opcional (ADR-067). Si vacío y `Producto` tiene más de un material para ese producto, `DatosEntrada.validar()` rechaza la fila explícitamente: un pedido sin material no puede resolver contenedor ni stock una vez que el producto distingue materiales. El pedido nunca se satisface con stock de otro material del mismo producto |
| `calidad` | texto | |
| `lote_solicitado` | texto | Vacío = cualquier lote del producto/calidad |
| `toneladas_solicitadas` | double | > 0 |
| `terminal` | texto | FK `Ubicacion` tipo `TERMINAL` |
| `naviera` | enum | |
| `incoterm` | texto | Informativo |
| `buque` | texto | Informativo. Agrupa los pedidos que comparten cut-off |
| `viaje_buque` | texto | Informativo |
| `deposito_comprometido` | texto | Opcional (ADR-066). FK `Ubicacion` tipo `DEPOSITO`. Vacío = sin compromiso previo, el pedido compite normalmente por costo |
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
| `material` | texto | no | Opcional (ADR-067). Un `codigo_lote` no puede tener dos materiales, igual que no puede tener dos productos. Si `Producto` tiene más de un material para el producto de la fila, vacío es rechazado explícitamente por `DatosEntrada.validar()` |
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
- **Columna de toneladas como rango de días (ADR-069)**: si ninguna columna es un alias de `toneladas` y hay **una sola** columna cuyo encabezado es un rango de días (`1-365`, `0-364`), se lee como el stock inicial en toneladas y se avisa en la consola. El relevamiento nombra así la columna porque es la tenencia al inicio del año de campaña; leerla en silencio sería una conversión que después nadie puede auditar, y no leerla deja el stock afuera.
- **Encabezado `producto` repetido para el material (ADR-069)**: la segunda aparición de la misma columna se lee como `material`, con aviso. Sin esto el stock entra con `material = ""` y desde ADR-067 ningún pedido lo puede consumir: son toneladas inertes, que es peor que no cargarlas.
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
| `Escenario` | `id_escenario`, `duracion_campania_dias`, `semilla_base`, `variabilidad_produccion`, `variabilidad_demanda`, `pedidos_por_campania`, `toneladas_medias_pedido`, `plazo_pedido_dias`, `camiones_producto`, `camiones_portacontenedor`, `capacidad_camion_tn`, `velocidad_camion_kmh`, `horas_operativas_dia`, `factor_produccion`, `factor_capacidad_planta`, `factor_capacidad_deposito`, `factor_storage`, `ventana_demanda`, `habilita_cross_dock`, `deterministico`, `estrategia_consolidacion`, `cliente_default`, `calidad_default`, `umbral_alerta_pct`, `umbral_sobrecarga_pct`, `umbral_objetivo_pct`, `dias_forecast`, `politica_frio_propio`, `politica_seleccion`, `servicio_minimo_proyectado`, `factor_tarifa_flete`, `factor_tarifa_round_trip`, `factor_tarifa_cross_dock`, `factor_tarifa_terminal`, `factor_consolidacion_planta`, `factor_cupo_cross_dock`, `factor_capacidad_terminal`, `dias_anticipacion_planificacion_default`, `dias_anticipacion_retiro_default`, `dias_entre_cutoff_y_etd_default`, `permite_reserva_antes_retiro`, `permite_transferencia_antes_retiro`, `permite_reserva_capacidad_futura`, `politica_reprogramacion_buque`, `permite_fallback_politica_fija`, `exportar_diagnostico_capacidad`, `habilita_flota_producto_multidiaria`, `dias_max_programacion_flota`, `fecha_inicio_campania` |
| `Producto` | `producto`, `tipo_contenedor`, `capacidad_contenedor_tn`, `toneladas_objetivo_lote_tn` |
| `Ubicacion` | `id_ubicacion`, `tipo`, `habilitada`, `velocidad_carga_tn_hora`, `velocidad_descarga_tn_hora`, `velocidad_consolidacion_tn_hora`, `capacidad_diaria_tn`, `contenedores_por_dia`, `posiciones_cross_dock`, `latitud` (opcional, ADR-072), `longitud` (opcional, ADR-072) |
| `CapacidadUbicacion` | `id_ubicacion`, `producto`, `capacidad_tn` |
| `Distancia` | `origen`, `destino`, `distancia_km` |
| `TarifaSitio` | `id_ubicacion`, `producto`, `in_usd_tn`, `storage_usd_tn_dia`, `out_usd_tn`, `oportunidad_usd_tn_dia`, `penalidad_sobrecarga_usd_tn_dia`, `consolidacion_tarifa`, `consolidacion_unidad`, `cross_dock_tarifa`, `cross_dock_unidad`, `thc_usd_contenedor`, `costo_terminal_usd_contenedor`, `despachante_tarifa`, `despachante_unidad`, `proveedor`, `vigencia_desde`, `vigencia_hasta`, `habilitada` |
| `TarifaFleteProducto` | `origen`, `destino`, `producto`, `tipo_camion`, `capacidad_camion_tn`, `unidad`, `tarifa`, `variable_usd_tn`, `proveedor`, `vigencia_desde`, `vigencia_hasta`, `habilitada` |
| `TarifaRoundTrip` | `terminal`, `sitio`, `tipo_contenedor`, `tarifa_usd_contenedor`, `horas_espera_incluidas`, `tarifa_espera_usd_hora`, `proveedor`, `vigencia_desde`, `vigencia_hasta`, `habilitada` |
| `TarifaEspera` | `tipo_recurso`, `id_ubicacion`, `franquicia_horas`, `usd_hora`, `proveedor`, `vigencia_desde`, `vigencia_hasta`, `habilitada` |
| `ProduccionPlan` | `id_escenario`, `dia`, `producto`, `produccion_tn` |
| `PedidoPlan` | `id_escenario`, `codigo_pedido`, `producto`, `toneladas_solicitadas`, `terminal`, `naviera`, `incoterm`, `buque`, `viaje_buque`, `deposito_comprometido` (opcional, ADR-066), `dia_conocimiento`, `dia_apertura_retiro_vacio`, `dia_cutoff_fisico`, `dia_etd`. Acepta también la forma anterior a ADR-059 con `dia_llegada` y `dia_limite` |
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
| `PedidoPlan` | sí | sin cliente, calidad, lote solicitado, naviera ni incoterm: el modelo aún no los usa. `deposito_comprometido` es opcional (ADR-066): vacío en todos los pedidos existentes, sin efecto si no se completa |
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

Las salidas de **auditoría de red** (ADR-064) tienen contrato propio, con diccionario de campos, claves y uniones: [Auditoría de red](Auditoria_de_Red.md). Son seis tablas por corrida, todas con `run_id`, y su esquema se publica en `resultados/esquema_auditoria.json` generado desde el propio modelo.

## 12. Columnas de flota multidiaria (ADR-061)

Dos columnas nuevas, opcionales, en la hoja `Escenario`. Un libro que no las trae corre igual con los valores por defecto.

| Columna | Tipo | Default | Significado |
|---|---|---|---|
| `habilita_flota_producto_multidiaria` | bool | `true` | Con `true` la flota de producto son camiones físicos con viajes que pueden durar varios días. Con `false` el modelo vuelve exactamente a la capacidad diaria agregada de ADR-044, con movimiento instantáneo y sin tránsito |
| `dias_max_programacion_flota` | double | `2` | Horizonte de compromiso de la agenda: hasta cuántos días adelante se programa un viaje que no puede salir hoy. Más días adelantan producto que todavía no tiene pedido; menos días dejan camiones ociosos |

Las duraciones **no** se cargan: se derivan de `Distancia`, de `velocidad_camion_kmh`, de `horas_operativas_dia` y de las velocidades de carga y descarga de cada ubicación. La tabla `Distancia` declara **un solo sentido por tramo** y el modelo la lee de forma simétrica; un tramo que no está en la tabla no aborta la corrida, se descarta el movimiento con `RUTA_SIN_DISTANCIA`.

## 13. Fecha de inicio de campaña (ADR-071)

La columna `fecha_inicio_campania` de la hoja `Escenario` es opcional y fecha el **día 1** de la campaña. Un libro que no la trae corre igual: las columnas de fecha de la auditoría quedan vacías.

| Origen | Prioridad |
|---|---|
| `Escenario.fecha_inicio_campania` | 1 — es un atributo de la campaña, igual que `duracion_campania_dias`, y así un barrido que compara campañas de años distintos fecha cada una como corresponde |
| Parámetro de corrida `fechaInicioCampania` | 2 — default `2026-04-01` |
| ninguno de los dos | fecha vacía en todas las tablas |

Formato **`YYYY-MM-DD`**, y sólo ése. La celda puede venir como texto (`2026-04-01`) o como fecha de Excel: el importador convierte el serial (base 1899-12-30) al mismo formato. Una fecha inexistente o mal escrita (`2026-02-30`, `01/04/2026`) **aborta la carga** nombrando el valor; nunca degrada a fecha vacía, porque una fecha vacía se lee como "el libro no la declara" y no como "el libro la declara mal".

La fecha no entra en ninguna decisión: no cambia la vigencia de las tarifas, ni el cut-off, ni la estacionalidad, ni el reloj del motor, que siguen razonando en días de campaña. Es una etiqueta de salida. Dónde aparece, en `docs/09_Definicion/Auditoria_de_Red.md` §5.0.

## 14. Coordenadas de los sitios (ADR-072)

Las columnas `latitud` y `longitud` de la hoja `Ubicacion` son **opcionales** y sirven sólo para dibujar: un libro que no las trae corre igual y la vista de red cae al esquema por tipo de nodo.

| Regla | Detalle |
|---|---|
| Unidad | grados decimales, con signo. `-26.9032792`, `-65.341295970` |
| Alias | `latitud` o `lat`; `longitud`, `long` o `lon` |
| Rango | latitud entre **−56 y −21**, longitud entre **−74 y −53** (Argentina continental). Fuera de rango **aborta la carga** nombrando el sitio y la columna |
| Escala | un relevamiento pegado con el separador corrido (`-269032792`) se **normaliza** dividiendo por 10 hasta caer en rango. Es la forma en que salen de varias planillas de campo y no hay ambigüedad posible: ninguna coordenada argentina válida es un múltiplo de 10 de otra |
| Una sola de las dos | error de datos: latitud y longitud van juntas o ninguna |
| Cobertura parcial | el mapa geográfico se dibuja sólo si **todos** los sitios de la red las traen; si falta uno, la vista cae al esquema por tipo de nodo y lo dice en pantalla. No es error de carga: media red geográfica y media inventada sería peor que el esquema |

**La coordenada no se usa para calcular nada.** La distancia que se costea, el tiempo de viaje y la elección de circuito salen de la tabla `Distancia`, igual que antes; la coordenada sólo posiciona el nodo en el dibujo (ADR-072). Dos consecuencias buscadas: una coordenada mal cargada deforma el mapa pero **no** puede cambiar un costo ni una decisión, y la distancia real por ruta puede diferir de la distancia en línea recta del dibujo sin que eso sea una inconsistencia.

El **libro canónico `datos/entrada_ejemplo.xlsx` no declara coordenadas** a propósito: el generador sintético no las inventa, y así el camino "libro sin coordenadas" queda ejercitado en cada regresión. El maestro real sí las trae.
