# Registro de cambios

Formato: una entrada por cambio relevante del modelo o de las definiciones. Las entradas del modelo indican qué versión del `.alp` las contiene.

## [Sin publicar]

### Agregado

- Modelo `RedLogistica_Exportacion.alp` versionado en el repositorio.
- `tools/exportar_modelo.py` y `model_src/`: espejo legible del modelo para poder revisar cambios de lógica en un pull request (ADR-035).
- [Inventario del modelo real](docs/03_Logica/Inventario_del_Modelo.md) con los hallazgos H-01 a H-14 y los límites de PLE verificados contra el modelo.
- ADR-030 a ADR-035: estructuras de datos como clases Java, presupuesto de tipos de agente, barrido con Parameter Variation, separación entre parámetros y estado, secuencia diaria explícita y versionado del espejo del modelo.
- Casos de validación V-019 a V-022.
- Clases Java `DatosEntrada` y `GeneradorSintetico` en el modelo (ADR-036): las tablas de entrada del contrato de datos y el generador que las puebla con semilla reproducible.
- `Main.cargarDatosEntrada()`, `Main.aplicarDatosAAgentes()` y `DatosEntrada.validar()`: el modelo genera, valida y distribuye las tablas antes del día 1 (ADR-037).
- Parámetros de escenario en `Main`: `idEscenario`, `duracionCampaniaDias`, `semillaBase`, `variabilidadProduccion`, `variabilidadDemanda`, `pedidosPorCampania`, `toneladasMediasPedido` y `plazoPedidoDias`.
- ADR-036 y ADR-037.
- `tools/exportar_modelo.py` exporta también las clases Java del modelo a `model_src/`.
- Clase Java `ImportadorExcel` (ADR-038): carga las tablas de `DatosEntrada` desde un libro `.xlsx` con una hoja por tabla, buscando las columnas por nombre. Informa juntas las hojas y columnas faltantes, saltea filas vacías y filtra por `id_escenario` las hojas que lo tienen, de modo que un libro puede contener varios escenarios.
- Parámetros `origenDatos` (`SINTETICO` o `EXCEL`) y `rutaExcel` en `Main`, y lista de opciones `OrigenDatos`: el origen de los datos se cambia sin tocar código.
- `datos/entrada_ejemplo.xlsx` y `tools/generar_excel_ejemplo.py`: plantilla del libro de entrada, generada corriendo el propio `GeneradorSintetico` del modelo para que la plantilla y la fase sintética no puedan divergir. Verificado en PLE: el mismo escenario cargado desde Excel y generado sintéticamente produce los mismos 494 envíos, 40 pedidos entregados y los mismos costos.
- El [contrato de datos](docs/09_Definicion/Contrato_de_Datos.md) documenta las hojas y encabezados que lee el importador, y las tablas del esquema objetivo que todavía no consume el modelo (`TiemposOperativos`, tarifas por contenedor, `LoteInicial` y los campos completos de `Ubicacion`, `Distancia` y `PedidoPlan`).

### Cambiado

- Modelo, datos de entrada como tablas (paso 2, H-08 parcial, H-09, H-10, H-11):
  - la demanda deja de ser tres pedidos cableados y se lee de la tabla `PedidoPlan`;
  - la producción diaria de la planta se lee de `ProduccionPlan` en lugar de tres parámetros fijos;
  - distancias, capacidades, tarifas de almacenaje, fletes a terminal, consolidación, velocidades y el tipo y la capacidad de contenedor por producto se leen de las tablas; los depósitos y terminales pasan a identificarse por `idUbicacion`;
  - una tarifa o una distancia faltante aborta el arranque con la clave que falta, en lugar de valer cero.
  - El [contrato de datos](docs/09_Definicion/Contrato_de_Datos.md) documenta qué tablas están implementadas y en qué se apartan del esquema objetivo. Falta el importador de Excel, que llenará las mismas listas.
- Modelo, higiene previa a la migración de datos e inventario:
  - 39 parámetros que en realidad eran estado de corrida pasaron a variables en `Main`, `Planta`, `Deposito` y `Terminal` (H-02, ADR-033). Los parámetros que quedan son las entradas del modelo.
  - Los seis eventos diarios de `Main` y `Planta.produccionDiaria` se reemplazan por un único evento `pasoDiario` que invoca las siete fases en orden fijo (H-06, H-07, ADR-034). Desaparecen las cadencias de 1,2 y 1,9 días.
  - El almacenaje se devenga una sola vez, en `devengarAlmacenamientoDiario()`: se imputa por lote y se agrega al depósito en el mismo recorrido, e incluye los lotes reservados (H-04).
- ADR-020: la licencia queda resuelta. El proyecto se desarrolla dentro de PLE. Se corrige el supuesto anterior de que PLE impedía los barridos con réplicas: Parameter Variation y Monte Carlo están disponibles, y el límite de 5 horas de tiempo de modelo no aplica a la Process Modeling Library.
- ADR-021: se precisa que el devengo diario de almacenaje no requiere capas; lo que sí las requiere es la imputación de IN/OUT parciales, el consumo por antigüedad y evitar partir el lote en agentes.
- Roadmap: fase 3 corregida de 30% a 0% tras verificar que las listas por ubicación no existen en el modelo (H-03).

### Definiciones previas

- Definición del proyecto como modelo de dimensionamiento de campaña, contrato de datos de entrada, escenarios E-00 a E-10, glosario y ADR-018 a ADR-029 ([PR #1](https://github.com/mazzuccoda/Anylogic_log_arg_2026/pull/1)).
