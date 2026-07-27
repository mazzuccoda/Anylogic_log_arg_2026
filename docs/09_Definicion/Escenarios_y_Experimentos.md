# Escenarios y experimentos

[← Volver al índice](../README.md)

**Estado:** implementado (fase 13). Lo que sigue describe el barrido que existe en el modelo, no una propuesta.

## 1. Por qué se define antes de programar

En un modelo de dimensionamiento, el valor no está en la corrida sino en la **comparación entre escenarios**. La lista de escenarios determina qué variables deben ser parámetros de entrada y qué puede quedar fijo. Definirla al final obliga a reabrir el modelo para parametrizar.

Regla derivada: **toda variable que aparezca en la columna "qué varía" de esta tabla debe ser parámetro de escenario, nunca un valor en código.**

## 2. Caso base

| Palanca | Valor de E-00 |
|---|---|
| Duración de campaña | 183 días (la corrida sigue hasta el día 365 para drenar entregas) |
| Producción | Media diaria por producto, ruido gaussiano ±15% |
| Demanda | 40 pedidos, 400 tn medias, ruido ±20%, plazo 15 días |
| Capacidad de planta | `factor_capacidad_planta` = 1 |
| Depósitos | 5, con capacidad por producto y `factor_capacidad_deposito` = 1 |
| Camiones de producto | 8 |
| Cross docking | Deshabilitado |
| Consolidación | En depósito |
| Réplicas | 30 |

El caso base no pretende ser realista todavía: es la referencia contra la que se miden los deltas. Se recalibra cuando llegue el Excel.

## 3. Escenarios

Cada escenario es **una fila**: `GeneradorSintetico.escenario(id, semilla)` parte del caso base y aplica su palanca. Agregar un escenario es agregar un caso ahí (o una fila en la hoja `Escenario` del Excel), nunca tocar el experimento.

| ID | Nombre | Qué varía | Pregunta que responde |
|---|---|---|---|
| E-00 | Caso base | — | Referencia |
| E-01 | Flota reducida | `camiones_producto` 3 → 1, `camiones_portacontenedor` 4 → 1 | P2, P6: flota mínima sin degradar servicio |
| E-02 | Flota ampliada | `camiones_producto` 3 → 6, `camiones_portacontenedor` 4 → 8 | P2, P6: rendimientos decrecientes |
| E-03 | Depósitos chicos | `factor_capacidad_deposito` 0,5× | P1, P8: capacidad mínima sin sobrecargar la planta |
| E-04 | Depósitos grandes | `factor_capacidad_deposito` 2× | P1, P8: saturación |
| E-05 | Cross docking | `habilita_cross_dock` | P5: ahorro real del cross docking |
| E-06 | Campaña alta | `factor_produccion` +30% | P7: robustez |
| E-07 | Campaña baja | `factor_produccion` −30% | P7: costo fijo sobredimensionado |
| E-08 | Demanda concentrada | `ventana_demanda` 0,5 | Pico de recursos |
| E-09 | Determinístico | Variabilidades en 0 y plan de pedidos regular | Verificación: la réplica no puede cambiar nada |
| E-10 | Almacenaje caro | `factor_storage` ×2 | Cuándo conviene mover producto vs guardarlo |
| E-11 | Consolidación en terminal | `estrategia_consolidacion` | Dónde conviene estibar |
| E-12 | Frío propio reactivo | `politica_frio_propio` `FLEXIBLE` → `REACTIVA` | ADR-048: cuánto compra retener en frío propio |

Un escenario de la propuesta original sigue sin estar en la tabla:

- **Política de prioridad**: la regla de asignación es hoy única (fecha límite). El escenario se agrega cuando existan las tres políticas.

## 4. Diseño de experimento

- **Corridas:** 13 escenarios × 30 réplicas = 390.
- **Réplicas:** 30 por escenario (`REPLICAS` en el experimento), semilla `semilla_base + replica`.
- **Estadísticos reportados:** media, desvío, mínimo, máximo y P95 de cada KPI, impresos por escenario al terminar el barrido.
- **Comparación:** cada escenario se reporta como delta absoluto y porcentual contra E-00.
- **Trazabilidad:** `resultados/kpis_por_corrida.csv` lleva una fila por corrida con `version_modelo`, `id_escenario`, `replica` y `semilla`. Un resultado sin esas cuatro columnas no es evidencia.

KPIs de cierre de corrida (funciones de `Main`):

| KPI | Función |
|---|---|
| `costo_total_usd` | `costoTotalCampania()` |
| `costo_usd_tn` | `costoPorToneladaExportada()` |
| `nivel_servicio` | `nivelServicio()` — pedidos entregados sin atraso sobre pedidos recibidos |
| `atraso_promedio_dias` | `atrasoPromedioDias()` |
| `utilizacion_flota` | `utilizacionFlota()` — camión-día de producto ocupado sobre ofrecido (ADR-044) |
| `utilizacion_portacontenedor` | `utilizacionPortacontenedor()` — estadística de ocupación del pool `flotaPortacontenedores` |
| `viajes_planta_deposito` | `viajesPlantaDeposito` — viajes de camión efectivamente hechos en la campaña |
| `uso_posiciones_consolidacion` | `usoPosicionesConsolidacion()` |
| `toneladas_exportadas` | `toneladasExportadas()` |
| `excedente_final_tn` | `excedenteFinalTn()` — stock que queda en la red al cierre, **no** producto perdido (ADR-048) |
| `toneladas_cross_dock` | `toneladasCrossDock` |
| `contenedores_exportados` | `contarContenedores(EXPORTADO)` |
| `costo_oportunidad_frio_usd` | `costoOportunidadFrio` — frío propio devengado, fuera de caja (ADR-049) |
| `costo_total_economico_usd` | `costoTotalEconomico()` — caja + oportunidad + penalidad de sobrecarga |
| `costo_economico_usd_tn` | `costoEconomicoPorTonelada()` |
| `ton_dia_sobre_nominal` | `tonDiaSobreNominalPlanta` — tonelada-día por encima del nivel nominal de planta |
| `dias_sobrecarga` | `diasSobrecargaPlanta` |
| `pico_ocupacion_planta_pct` | `picoOcupacionPlantaPct` |

Los tres últimos son la respuesta a "cuánto frío falta": desde la fase 19 la planta no descarta producto, así que el faltante de capacidad se lee ahí y no en el excedente.

## 5. Cómo se implementa el barrido en PLE

PLE incluye Parameter Variation, así que el barrido con réplicas es viable sin licencia paga (ADR-020). Lo que PLE no ofrece es Custom Experiment, es decir, escribir código de experimento que recorra una tabla de escenarios. El experimento `Escenarios` lo resuelve así (ADR-032, ADR-042):

- Modo **freeform** con `13 × REPLICAS` corridas y **dos dimensiones y sólo dos**:
  - `idEscenario = GeneradorSintetico.ESCENARIOS[(getCurrentIteration() - 1) / REPLICAS]`
  - `replica = (getCurrentIteration() - 1) % REPLICAS`
- Al arrancar, `Main.cargarDatosEntrada()` obtiene la fila del escenario y `aplicarEscenario()` fija duración, flota, cross dock y estrategia.
- La semilla se calcula dentro del modelo como `semillaBase + replica`, no en la configuración aleatoria del experimento, para que la réplica sea reproducible de forma independiente.
- Las corridas se evalúan **en serie**: con evaluación paralela el agente raíz no está disponible al cerrar cada corrida y no se pueden leer los KPIs.

Costo computacional medido: 360 corridas de 365 días tardan alrededor de un minuto.

Límite a vigilar: PLE admite 50 000 agentes creados dinámicamente **por corrida**. Con lotes, contenedores y pedidos de una campaña completa el margen es amplio.

Queda una restricción no técnica: la licencia PLE cubre aprendizaje personal e instrucción, no uso comercial. Es una decisión del responsable del proyecto y no afecta al diseño.

## 6. Cómo leer el barrido de flota

Desde ADR-044 la flota se consume: los viajes planta→depósito toman camión-día y los contenedores toman un portacontenedor del pool. Medias de 30 réplicas, escenarios de flota:

| Escenario | Camiones (producto / portacontenedor) | `utilizacion_flota` | `utilizacion_portacontenedor` | `viajes_planta_deposito` | `nivel_servicio` | `atraso_promedio_dias` | `costo_total_usd` |
|---|---|---|---|---|---|---|---|
| E-01 | 1 / 1 | 0,399 | 0,278 | 1 156 | 0,908 | 3,17 | 1 759 588 |
| E-00 | 3 / 4 | 0,138 | 0,108 | 1 196 | 0,940 | 1,93 | 1 830 480 |
| E-02 | 6 / 8 | 0,070 | 0,065 | 1 218 | 0,939 | 1,88 | 1 884 110 |

Dos lecturas importantes:

- **El punto de quiebre se lee en servicio y atraso, no en costo.** De 1 a 3 camiones el nivel de servicio sube de 0,908 a 0,940 y el atraso baja de 3,17 a 1,93 días; de 3 a 6 no cambia nada (0,939 y 1,88). Con estos datos la respuesta a P2/P6 es 3 camiones de producto y 4 portacontenedores, y agregar más no compra nada.
- **Más camiones cuestan más.** El costo total sube monótonamente con la flota porque el producto llega antes al depósito y paga más almacenaje, mientras lo que espera en planta no paga nada. Es una consecuencia del contrato de datos (la planta no tiene tarifa de almacenaje), no una ventaja de tener menos camiones: E-01 es más barato *y* peor servido.

La utilización media es baja incluso en E-01 porque la demanda de transporte es a ráfagas: en los días de transferencia la flota se satura y el resto del año está ociosa. Por eso el dimensionamiento mira el servicio, y la utilización sirve para saber cuánto margen queda.

## 7. Presentación de resultados

Para cada pregunta P1..P8, una tabla y un gráfico:

- P1 y P8: ocupación de depósito y excedente vs capacidad (curva de saturación).
- P2 y P6: nivel de servicio y utilización vs tamaño de flota (curva de rendimientos decrecientes, para leer el punto de quiebre).
- P3 y P4: costo total y USD/tn con desglose apilado por categoría.
- P5: costo total con y sin cross docking, con desglose del delta por categoría.
- P7: costo y servicio en campaña alta/baja.

La salida final del proyecto es un informe con esas curvas, no el modelo en sí.
