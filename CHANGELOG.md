# Registro de cambios

Formato: una entrada por cambio relevante del modelo o de las definiciones. Las entradas del modelo indican qué versión del `.alp` las contiene.

## [Sin publicar]

### Agregado

- Modelo `RedLogistica_Exportacion.alp` versionado en el repositorio.
- `tools/exportar_modelo.py` y `model_src/`: espejo legible del modelo para poder revisar cambios de lógica en un pull request (ADR-035).
- [Inventario del modelo real](docs/03_Logica/Inventario_del_Modelo.md) con los hallazgos H-01 a H-14 y los límites de PLE verificados contra el modelo.
- ADR-030 a ADR-035: estructuras de datos como clases Java, presupuesto de tipos de agente, barrido con Parameter Variation, separación entre parámetros y estado, secuencia diaria explícita y versionado del espejo del modelo.
- Casos de validación V-019 a V-022.

### Cambiado

- Modelo, higiene previa a la migración de datos e inventario:
  - 39 parámetros que en realidad eran estado de corrida pasaron a variables en `Main`, `Planta`, `Deposito` y `Terminal` (H-02, ADR-033). Los parámetros que quedan son las entradas del modelo.
  - Los seis eventos diarios de `Main` y `Planta.produccionDiaria` se reemplazan por un único evento `pasoDiario` que invoca las siete fases en orden fijo (H-06, H-07, ADR-034). Desaparecen las cadencias de 1,2 y 1,9 días.
  - El almacenaje se devenga una sola vez, en `devengarAlmacenamientoDiario()`: se imputa por lote y se agrega al depósito en el mismo recorrido, e incluye los lotes reservados (H-04).
- ADR-020: la licencia queda resuelta. El proyecto se desarrolla dentro de PLE. Se corrige el supuesto anterior de que PLE impedía los barridos con réplicas: Parameter Variation y Monte Carlo están disponibles, y el límite de 5 horas de tiempo de modelo no aplica a la Process Modeling Library.
- ADR-021: se precisa que el devengo diario de almacenaje no requiere capas; lo que sí las requiere es la imputación de IN/OUT parciales, el consumo por antigüedad y evitar partir el lote en agentes.
- Roadmap: fase 3 corregida de 30% a 0% tras verificar que las listas por ubicación no existen en el modelo (H-03).

### Definiciones previas

- Definición del proyecto como modelo de dimensionamiento de campaña, contrato de datos de entrada, escenarios E-00 a E-10, glosario y ADR-018 a ADR-029 ([PR #1](https://github.com/mazzuccoda/Anylogic_log_arg_2026/pull/1)).
