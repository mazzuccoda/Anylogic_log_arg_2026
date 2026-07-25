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
| E-01 | Flota reducida | `camiones_producto` 8 → 4 | P2, P6: flota mínima sin degradar servicio |
| E-02 | Flota ampliada | `camiones_producto` 8 → 12 | P2, P6: rendimientos decrecientes |
| E-03 | Depósitos chicos | `factor_capacidad_deposito` 0,5× | P1, P8: capacidad mínima sin excedente |
| E-04 | Depósitos grandes | `factor_capacidad_deposito` 2× | P1, P8: saturación |
| E-05 | Cross docking | `habilita_cross_dock` | P5: ahorro real del cross docking |
| E-06 | Campaña alta | `factor_produccion` +30% | P7: robustez |
| E-07 | Campaña baja | `factor_produccion` −30% | P7: costo fijo sobredimensionado |
| E-08 | Demanda concentrada | `ventana_demanda` 0,5 | Pico de recursos |
| E-09 | Determinístico | Variabilidades en 0 y plan de pedidos regular | Verificación: la réplica no puede cambiar nada |
| E-10 | Almacenaje caro | `factor_storage` ×2 | Cuándo conviene mover producto vs guardarlo |
| E-11 | Consolidación en terminal | `estrategia_consolidacion` | Dónde conviene estibar |

Dos escenarios de la propuesta original no están en la tabla:

- **Capacidad de planta a la mitad**: la palanca existe (`factor_capacidad_planta`) pero no tiene escenario asignado, porque el excedente de la campaña se acumula en planta y el escenario sólo mediría el descarte.
- **Política de prioridad**: la regla de asignación es hoy única (fecha límite). El escenario se agrega cuando existan las tres políticas.

## 4. Diseño de experimento

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
| `utilizacion_flota` | `utilizacionFlota()` — camión-día ocupado sobre camión-día ofrecido |
| `uso_posiciones_consolidacion` | `usoPosicionesConsolidacion()` |
| `toneladas_exportadas` | `toneladasExportadas()` |
| `excedente_final_tn` | `excedenteFinalTn()` |
| `toneladas_cross_dock` | `toneladasCrossDock` |
| `contenedores_exportados` | `contarContenedores(EXPORTADO)` |

## 5. Cómo se implementa el barrido en PLE

PLE incluye Parameter Variation, así que el barrido con réplicas es viable sin licencia paga (ADR-020). Lo que PLE no ofrece es Custom Experiment, es decir, escribir código de experimento que recorra una tabla de escenarios. El experimento `Escenarios` lo resuelve así (ADR-032, ADR-042):

- Modo **freeform** con `12 × REPLICAS` corridas y **dos dimensiones y sólo dos**:
  - `idEscenario = GeneradorSintetico.ESCENARIOS[(getCurrentIteration() - 1) / REPLICAS]`
  - `replica = (getCurrentIteration() - 1) % REPLICAS`
- Al arrancar, `Main.cargarDatosEntrada()` obtiene la fila del escenario y `aplicarEscenario()` fija duración, flota, cross dock y estrategia.
- La semilla se calcula dentro del modelo como `semillaBase + replica`, no en la configuración aleatoria del experimento, para que la réplica sea reproducible de forma independiente.
- Las corridas se evalúan **en serie**: con evaluación paralela el agente raíz no está disponible al cerrar cada corrida y no se pueden leer los KPIs.

Costo computacional medido: 360 corridas de 365 días tardan alrededor de un minuto.

Límite a vigilar: PLE admite 50 000 agentes creados dinámicamente **por corrida**. Con lotes, contenedores y pedidos de una campaña completa el margen es amplio.

Queda una restricción no técnica: la licencia PLE cubre aprendizaje personal e instrucción, no uso comercial. Es una decisión del responsable del proyecto y no afecta al diseño.

## 6. Limitación conocida del barrido de flota

E-01 y E-02 dan **exactamente** el mismo resultado que E-00. No es un problema del experimento: la transferencia planta→depósito mueve todas las toneladas del día sin consumir camiones, y `camionDisponibleHoy()` sólo pregunta si queda alguno libre en un pool que casi nunca se ocupa. Por eso `utilizacion_flota` da ~0 salvo en los escenarios que despachan por otro camino.

Hasta que la flota se consuma como capacidad diaria (viajes por camión y día, igual que las posiciones de consolidación), el modelo **no puede responder P2 ni P6**. Es el primer pendiente del roadmap.

## 7. Presentación de resultados

Para cada pregunta P1..P8, una tabla y un gráfico:

- P1 y P8: ocupación de depósito y excedente vs capacidad (curva de saturación).
- P2 y P6: nivel de servicio y utilización vs tamaño de flota (curva de rendimientos decrecientes, para leer el punto de quiebre).
- P3 y P4: costo total y USD/tn con desglose apilado por categoría.
- P5: costo total con y sin cross docking, con desglose del delta por categoría.
- P7: costo y servicio en campaña alta/baja.

La salida final del proyecto es un informe con esas curvas, no el modelo en sí.
