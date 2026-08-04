# Auditoría de red: tablas de salida (ADR-064)

[← Volver al índice](../README.md)

## 1. Objetivo

Dejar escrito, por corrida, **qué alternativas existían, cuál se eligió, por qué se descartaron las demás, qué pasó físicamente después y cuánto costó**. Las tablas son la fuente del análisis fuera de AnyLogic y del tablero web que se construirá en otro proyecto.

La auditoría **observa** el modelo: no cambia la factibilidad, ni el orden del ranking, ni la política económica, ni la capacidad. Con `nivelAuditoriaRed = DESACTIVADA` el modelo decide exactamente lo mismo (V-AUD-10).

## 2. Nivel de auditoría

Parámetro `nivelAuditoriaRed` de `Main`:

| Nivel | Qué escribe | Cuándo usarlo |
|---|---|---|
| `DESACTIVADA` | nada | barridos (1 080 corridas en la misma JVM) |
| `RESUMIDA` | las mismas tablas sin las alternativas no intentadas | campaña normal |
| `COMPLETA` | todo, incluidas las alternativas que nunca se intentaron | corrida puntual auditada |

Default del parámetro: `DESACTIVADA`. El experimento `Simulation` (corrida puntual) lo fija en `COMPLETA`; el barrido lo deja apagado.

Las filas se escriben **en streaming**, no al cierre: una corrida interrumpida ya dejó su evidencia en disco.

## 3. Identidad de la corrida y de la decisión

- `run_id = <id_escenario>-R<replica>`. Es la primera columna de las seis tablas: sin ella no se pueden cargar 1 080 corridas en la misma tabla sin colisión de claves.
- `id_decision = <codigo_pedido>-D<n>`: una decisión por pedido y por vuelta del asignador. `asignarConEvaluador()` **regenera** las alternativas después de cada asignación parcial (ADR-055), así que un pedido multiorigen tiene varias decisiones y la columna `ronda` las ordena.
- `id_alternativa = <id_decision>-A<n>`: la alternativa concreta dentro de esa decisión. Es la clave que une la decisión con la asignación, con los arcos y con los cargos.

## 4. Las seis tablas

| Tabla | Archivo | Grano | Clave |
|---|---|---|---|
| `decisiones_alternativas` | `decisiones_alternativas.csv` | una fila por alternativa evaluada en cada ronda | `run_id`, `id_alternativa` |
| `asignaciones_elegidas` | `asignaciones_elegidas.csv` | una fila por asignación ejecutada | `run_id`, `id_asignacion` |
| `ejecucion_arcos` | `ejecucion_arcos.csv` | una fila por movimiento o espera física terminada | `run_id`, `id_evento_arco` |
| `costos_eventos` | `costos_eventos.csv` | una fila por cargo devengado | `run_id`, `id_costo` |
| `snapshot_inventario` | `snapshot_inventario.csv` | una fila por día, ubicación y producto | `run_id`, `dia`, `ubicacion`, `producto` |
| `snapshot_capacidad_recursos` | `capacidad_por_dia.csv` | una fila por día, recurso y ubicación | `run_id`, `dia`, `tipo_recurso`, `ubicacion` |

Las dos últimas **no son tablas nuevas**: `costos_eventos` es `RegistroCostos.Cargo` (ADR-052) y `snapshot_capacidad_recursos` es la agenda de capacidad de ADR-060, ambas extendidas con `run_id` y con la identidad de la decisión. Copiarlas a una tabla propia crearía una segunda fuente de verdad de costos y un segundo calendario de capacidad.

`asignaciones_capacidad_decisiones.csv` queda **deprecado**: era el antecesor parcial de `decisiones_alternativas` y se sigue escribiendo sólo cuando la auditoría está apagada.

### 4.1 Esquema publicado

`resultados/esquema_auditoria.json` se genera en la misma corrida, desde los mismos métodos `encabezadoCsv()` que escriben los CSV: archivo, columnas y clave primaria de cada tabla. No puede quedar viejo. `version_esquema` (hoy `ADR-064.1`) cambia cuando cambia una columna o una clave, y aparece también en `resultados/manifiesto_auditoria_<run_id>.json` junto con el conteo de filas de la corrida.

## 5. Diccionario de campos

Convenciones: los importes son USD, las toneladas `tn`, las duraciones en horas salvo que el nombre diga `_dias`, los días son días de campaña (`dia_simulacion` es el reloj del modelo, con fracción). Un campo vacío es un dato que el modelo **no produce**; nunca es un cero implícito.

### 5.1 `decisiones_alternativas` (97 columnas)

**Corrida y decisión.** `run_id`, `escenario`, `replica`, `dia_simulacion`, `dia_campania`, `politica_seleccion` y `criterio_orden` (la política económica vigente, para poder comparar corridas), `id_decision`, `ronda`, `id_alternativa`.

**Pedido.** `codigo_pedido`, `producto`, `tipo_contenedor`, `terminal`, `estado_pedido_antes`, `toneladas_solicitadas`, `toneladas_entregadas_previas`, `toneladas_en_proceso_previas`, `toneladas_reservadas_previas`, `toneladas_pendientes_asignar`, `contenedores_pendientes_estimados`, `cantidad_origenes_previos`.

**Ventana marítima (ADR-059).** `dia_conocimiento`, `dia_apertura_retiro`, `dia_cutoff`, `dias_hasta_cutoff`.

**Alternativa.** `circuito`, `es_cross_dock`, `origen_stock`, `sitio_estiba`, `destino_final`, `requiere_flota_producto`, `requiere_portacontenedor`, `requiere_posicion`, `tipo_recurso_capacidad`, `ubicacion_recurso_capacidad`.

**Estado del mundo cuando se decidió.** `stock_fisico_origen_tn`, `stock_libre_origen_tn`, `stock_reservado_origen_tn`, `stock_en_transito_hacia_origen_tn`, `espacio_fisico_sitio_tn`, `espacio_efectivo_sitio_tn` (descuenta lo que ya viene en camino), `ocupacion_sitio_pct`, `cupo_crossdock_libre_cont`, `posiciones_disponibles_antes_cutoff`, `flota_producto_disponible`, `primera_salida_flota`, `ultima_llegada_flota`, `espera_flota_dias`, `portacontenedores_libres`, `portacontenedores_ocupados`.

**Lo que la alternativa podía cubrir.** `toneladas_sin_restriccion_capacidad` (el volumen que se habría tomado sin capacidad finita, para medir el costo de la restricción), `toneladas_factibles`, `contenedores_factibles`, `contenedores_con_capacidad`, `viajes_producto_requeridos`, `viajes_producto_factibles`, `es_asignacion_parcial`, `porcentaje_pedido_cubierto`.

**Tiempo estimado.** `horas_flete_producto`, `horas_carga_estiba`, `horas_viaje_vacio`, `horas_viaje_cargado`, `horas_descarga_terminal`, `horas_consolidacion_terminal`, `horas_ciclo_fisico_total`, `dia_entrega_estimado`, `holgura_estimada_dias`, `llega_a_tiempo_estimado`. C-13 verifica que las seis etapas sumen el ciclo con el que decidió el evaluador. La alternativa sintética de transferencia depósito–depósito no tiene ciclo físico: sus etapas quedan en cero.

**Costo estimado (el que se usó para decidir).** `costo_flete_producto`, `costo_roundtrip`, `costo_estiba`, `costo_out`, `costo_thc`, `costo_terminal`, `costo_despachante`; hundidos `costo_in_hundido`, `costo_almacenaje_hundido`, `costo_flete_hundido`; agregados `costo_historico`, `costo_incremental`, `costo_end_to_end` y sus unitarios `costo_incremental_usd_tn`, `costo_end_to_end_usd_tn`, `costo_incremental_usd_cont`; y `costo_unitario_sin_restriccion`.

**Resultado.** `factible`, `orden_ranking` (el único ranking que produce el modelo), `resultado_ejecucion`, `codigo_motivo`, `detalle_motivo`, `toneladas_tomadas`, `costo_elegida_usd_tn`, `diferencia_vs_elegida_usd_tn`, `saldo_pedido_antes`, `saldo_pedido_despues`, `es_mas_barata_no_factible`.

`resultado_ejecucion` toma cuatro valores:

| Valor | Significado |
|---|---|
| `ELEGIDA` | se ejecutó y hay una asignación |
| `INTENTADA_FALLIDA` | era factible al evaluar y falló al ejecutar (otro pedido del mismo día se llevó el recurso) |
| `NO_INTENTADA` | factible, pero el pedido se completó antes de llegar a ella en el ranking |
| `NO_FACTIBLE` | descartada por el evaluador; `codigo_motivo` dice por qué |

`codigo_motivo` está vacío cuando no hubo descarte. Los motivos normalizados son los que el código realmente produce: `TRANSFERENCIA_DEPOSITO_DEPOSITO`, `CROSS_DOCK_DESHABILITADO`, `SIN_STOCK_PLANTA_PARA_CRUZAR`, `ORIGEN_NO_HABILITADO`, `SIN_CUPO_CROSS_DOCK`, `SIN_ESPACIO_DE_PASO`, `SIN_FLOTA_PLANTA_DEPOSITO`, `SIN_STOCK`, `SIN_STOCK_ESPACIO_O_CUPO`, `SIN_FLOTA_GRANEL_TERMINAL`, `CAPACIDAD_ESTIBA_CERO`, `SIN_CAPACIDAD_ANTES_CUTOFF`, `SIN_FLOTA_ANTES_CUTOFF` y `NO_TOMADA_AL_EJECUTAR`.

Tres aclaraciones que importan al leer la tabla: **ser más cara no es un descarte** (`es_mas_barata_no_factible` mide justamente el costo de la restricción: la alternativa más barata que no se pudo usar); **llegar tarde tampoco lo es** (una alternativa tarde sigue compitiendo, y eso se ve en `llega_a_tiempo_estimado` con `factible = true`); y **una tarifa faltante no descarta**, aborta la corrida por contrato de datos.

**Campos que el modelo no produce y por eso no están:** costo de espera, costo de oportunidad por alternativa, y un segundo ranking. `ordenarAlternativas()` produce **un solo** orden; inventar un ranking alternativo sería inventar una política.

### 5.2 `asignaciones_elegidas` (30 columnas)

`run_id`, `escenario`, `replica`, `id_asignacion`, `id_decision`, `id_alternativa`, `codigo_pedido`, `producto`, `origen`, `circuito`, `es_cross_dock`, `prioridad`, `dia_asignacion`, `dia_primer_despacho`, `dia_ultima_entrega`, `toneladas_asignadas`, `toneladas_reservadas_activas`, `toneladas_contenerizadas`, `toneladas_despachadas`, `toneladas_entregadas`, `contenedores_creados`, `contenedores_entregados`, `costo_incremental_estimado`, `costo_end_to_end_estimado`, `costo_real_contenedores_usd`, `desvio_costo_usd`, `dias_ciclo_real`, `cerrada`, `cancelada`, `motivo_asignacion`.

`costo_real_contenedores_usd` es la suma de los cargos **atribuibles al contenedor** de esa asignación, no el costo total: el almacenaje se devenga contra el lote y no contra la asignación. `desvio_costo_usd` compara ese real con el estimado y hay que leerlo con ese alcance.

### 5.3 `ejecucion_arcos` (29 columnas)

Un arco es un **hecho físico terminado**: se emite al salir del bloque, cuando ya existen la duración real y el estado final.

Claves e identidad: `run_id`, `escenario`, `replica`, `id_evento_arco`, `id_decision`, `id_alternativa`, `id_asignacion`, `id_envio`, `id_contenedor`, `codigo_pedido`, `id_lote`, `producto`.
Hecho: `tipo_arco`, `origen`, `destino`, `circuito`, `es_cross_dock`, `toneladas`, `contenedores`, `viajes`, `distancia_km`.
Tiempo: `dia_programacion`, `dia_inicio`, `dia_fin`, `duracion_real_horas`, `duracion_esperada_horas`.
Recurso: `recurso_utilizado`, `id_recurso`, `estado_final`.

`duracion_esperada_horas` **negativa** significa "no aplica": esperar un portacontenedor o una posición no tiene techo físico, y así lo trata C-05 (ADR-063). Los diez tipos de arco:

| `tipo_arco` | Qué es |
|---|---|
| `PLANTA_DEPOSITO` | viaje de producto planta → depósito (ADR-061) |
| `DEPOSITO_DEPOSITO` | viaje de producto entre depósitos |
| `CROSS_DOCK` | viaje de producto que cruza sin almacenar |
| `ESPERA_PORTACONTENEDOR` | el envío espera equipo en `colaCamiones` |
| `TERMINAL_ORIGEN_CONTENEDOR_VACIO` | retiro del vacío desde la terminal |
| `CARGA_CONSOLIDACION` | carga del contenedor en el sitio de estiba |
| `ORIGEN_TERMINAL_CONTENEDOR_CARGADO` | viaje cargado hacia la terminal |
| `DESCARGA_TERMINAL` | descarga en terminal |
| `CONSOLIDACION_TERMINAL` | consolidación en terminal (circuito 4) |
| `ESPERA_POSICION` | el contenedor espera una posición de consolidación (ADR-060) |

**No son arcos** y por eso no están acá: el almacenaje, el ingreso y el egreso contable de depósito. Son cargos, y viven en `costos_eventos`. Por la misma razón el arco **no lleva importe**: el costo se une por `id_envio`, `id_contenedor`, `id_lote` o `codigo_pedido` contra `costos_eventos`. Un importe repetido en dos tablas es un importe que puede diferir.

`id_decision` está vacío en los arcos que no nacen de una decisión de circuito: los viajes de producto planta–depósito preventivos, el cross dock y la espera de posición.

### 5.4 `costos_eventos` (27 columnas)

Es `RegistroCostos.Cargo`: `run_id`, `escenario`, `replica`, `id_costo`, `dia`, `dia_campania`, `tipo_contable` (`CAJA` o `ECONOMICO`), `categoria`, `codigo_pedido`, `id_asignacion`, `id_decision`, `id_contenedor`, `id_lote`, `producto`, `circuito`, `origen`, `destino`, `sitio`, `proveedor`, `unidad`, `cantidad`, `tarifa`, `importe_usd`, `id_operacion`, `alcance` (`RED`, `PEDIDO`, `LOTE`, `CONTENEDOR`), `es_incremental`, `motivo`.

`tipo_contable = ECONOMICO` es costo de oportunidad, no caja: **no se suma** al total de campaña. `alcance` dice contra qué se devengó el cargo y es lo que hace que el desvío por asignación tenga un alcance declarado y no un número inventado.

### 5.5 `snapshot_inventario` (24 columnas)

`run_id`, `escenario`, `replica`, `dia`, `ubicacion`, `tipo_ubicacion`, `producto`, `capacidad_tn`, `stock_inicial_dia_tn`, `stock_fisico_tn`, `stock_libre_tn`, `stock_reservado_pedidos_tn`, `stock_reservado_viajes_tn`, `stock_en_transito_entrada_tn`, `stock_en_transito_salida_tn`, `ingresos_dia_tn`, `egresos_dia_tn`, `produccion_dia_tn`, `ocupacion_pct`, `costo_almacenaje_dia_usd`, `dias_stock_promedio`, `lotes_abiertos`, `lote_mas_antiguo_dias`, `descuadre_tn`.

El balance de cada fila cierra por construcción y C-12 lo verifica todos los días:

```
stock_fisico_tn = stock_inicial_dia_tn + ingresos_dia_tn − egresos_dia_tn
```

`descuadre_tn` es esa diferencia y tiene que ser cero. Las filas en cero no se escriben: un nodo sin stock, sin movimiento y sin capacidad no aporta información.

### 5.6 `snapshot_capacidad_recursos` (13 columnas)

`run_id`, `escenario`, `replica`, `dia`, `tipo_recurso`, `ubicacion`, `capacidad_nominal`, `reservada`, `consumida`, `liberada`, `ocupada`, `libre`, `cola`.

Regla contra el doble conteo (ADR-060): hay **una sola ocupación por (recurso, sitio, día)**. `ocupada` es la ocupación física; ejecutar un contenedor ya reservado convierte reserva en consumo **sin volver a ocupar**, así que `reservada + consumida` no es la ocupación. `libre = capacidad_nominal − ocupada`. La invariante `ocupada ≤ capacidad_nominal` la vigila C-03.

Cuando la auditoría está apagada esta tabla se sigue exportando como diagnóstico de capacidad y el `run_id` queda vacío.

## 6. Reconciliaciones

| Id | Qué verifica |
|---|---|
| C-03 | ningún día por encima de la capacidad nominal |
| C-05 | ningún bloque del flujo retiene un envío más allá de su duración física |
| C-06 | las alternativas `ELEGIDA` son exactamente las asignaciones ejecutadas |
| C-07 | las toneladas tomadas por decisión cierran contra el saldo del pedido, ronda a ronda |
| C-09 | las asignaciones exportadas son las del modelo, y sus contenedores y envíos también |
| C-10 | el agregado de arcos por tipo coincide con las filas escritas |
| C-12 | el balance diario de cada nodo cierra (`descuadre_tn = 0`) |
| C-13 | las etapas de tiempo suman el ciclo con el que decidió el evaluador |
| C-14 | los cargos exportados son los del registro de costos, sin duplicar ni perder ninguno |

Ninguna compara dos exportaciones entre sí: todas comparan la tabla contra el estado del modelo, que es la única forma de que puedan fallar.

## 7. Para el tablero web

- Cargar por `run_id`: es la primera columna de las seis tablas y la única forma de mezclar corridas sin colisión.
- Leer `esquema_auditoria.json` para las columnas y `manifiesto_auditoria_<run_id>.json` para el conteo de filas esperado; comparar `version_esquema` antes de asumir un campo.
- Uniones estables: `decisiones_alternativas.id_alternativa` → `asignaciones_elegidas.id_alternativa` → `ejecucion_arcos.id_asignacion` → `costos_eventos.id_asignacion`; y por `id_contenedor` / `id_lote` para el costo que no es de la asignación.
- Los importes **sólo** se suman de `costos_eventos`, filtrando `tipo_contable = CAJA`. Ninguna otra tabla trae dinero.
