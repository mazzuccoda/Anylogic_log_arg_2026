# Decisiones de arquitectura

[← Volver al índice](../README.md)

Este archivo registra decisiones que afectan el diseño del modelo. Cada decisión debe conservarse aunque luego sea reemplazada.

**Este archivo es la única fuente de numeración de ADR del proyecto.** La Especificación Técnica Maestra solamente enlaza aquí; no debe volver a numerar decisiones.

## ADR-001 — Contenedor como entidad independiente

**Estado:** aceptada.  
**Contexto:** un pedido puede requerir varios contenedores con tiempos, recursos y costos diferentes.  
**Decisión:** crear un agente `ContenedorExportacion` por unidad física.  
**Consecuencias:** mayor detalle, más agentes y necesidad de reconciliar contenedores con pedido.

## ADR-002 — Mismo portacontenedor durante todo el ciclo

**Estado:** aceptada.  
**Decisión:** el camión que retira el vacío permanece asignado hasta devolver el contenedor cargado.  
**Consecuencia:** la espera de consolidación consume recurso.

## ADR-003 — El pedido parte de un lote específico

**Estado:** aceptada.  
**Decisión:** la planificación no selecciona cualquier lote; primero localiza el lote solicitado y luego sus existencias.  
**Consecuencia:** la reserva debe respetar identidad de lote.

## ADR-004 — Lote comercial acumulativo

**Estado:** aceptada.  
**Contexto:** el lote puede producirse durante varios días.  
**Decisión:** no crear una identidad comercial nueva por cada producción diaria.  
**Consecuencia:** `crearLoteEnPlanta()` debe localizar un lote abierto y acumular.

## ADR-005 — Despacho parcial antes del cierre

**Estado:** aceptada.  
**Decisión:** el estado productivo del lote no bloquea el despacho si existe saldo físico libre.  
**Consecuencia:** separar estado comercial, producción acumulada y disponibilidad.

## ADR-006 — Múltiples ubicaciones por lote

**Estado:** aceptada.  
**Decisión:** un lote puede coexistir en planta, depósitos y contenedores.  
**Consecuencia:** `ubicacionActual` deja de ser una fuente válida de verdad.

## ADR-007 — Solución transitoria compatible con PLE

**Estado:** reemplazada por ADR-021.  
**Contexto:** PLE impidió crear `ExistenciaLote`.  
**Decisión:** usar listas paralelas dentro de `LoteProducto`.  
**Riesgo:** desalineación de índices.  
**Motivo del reemplazo:** un único saldo y una única fecha de ingreso por ubicación no permiten imputar el almacenaje cuando el mismo lote ingresa a la misma ubicación en fechas distintas y se retira parcialmente.

## ADR-008 — Sin movimientos depósito-depósito

**Estado:** aceptada para el alcance actual.  
**Decisión:** permitir planta→depósito, planta/depósito→puerto y movimientos a cross dock.  
**Consecuencia:** simplifica búsqueda de rutas y costos.

## ADR-009 — Compatibilidad de depósito por capacidad

**Estado:** aceptada temporalmente.  
**Decisión:** capacidad mayor que cero implica habilitación para el producto.  
**Consecuencia:** recepción, almacenamiento y operación comparten una regla simplificada.  
**Evolución:** separar habilitaciones si aparecen excepciones.

## ADR-010 — Cross docking sin IN, storage ni OUT

**Estado:** aceptada.  
**Decisión:** la mercadería no ingresa formalmente a almacenamiento.  
**Consecuencia:** solo se aplican fletes, cross dock, ciclo, terminal, THC y despachante.

## ADR-011 — Sincronización de cross docking por día operativo

**Estado:** aceptada.  
**Decisión:** ambos camiones deben coincidir el mismo día, no necesariamente a la misma hora.  
**Consecuencia:** el primero espera y la operación puede requerir reprogramación al cambiar de día.

## ADR-012 — Prioridad por ubicación actual del lote

**Estado:** aceptada para la primera versión.  
**Decisión:** si el lote está almacenado en un depósito habilitado, la primera alternativa es consolidar allí.  
**Consecuencia:** no se busca otro depósito más barato inicialmente.

## ADR-013 — Separación de costos

**Estado:** aceptada.  
**Decisión:** todo plan debe mostrar costo histórico, incremental y end-to-end.  
**Consecuencia:** evita considerar como evitables costos ya incurridos.

## ADR-014 — Tarifa faltante no equivale a cero

**Estado:** aceptada.  
**Decisión:** una tarifa ausente hace el plan no factible o genera error explícito.  
**Consecuencia:** se evitan alternativas artificialmente baratas.

## ADR-015 — Migración sin eliminación inmediata

**Estado:** aceptada.  
**Decisión:** `Envio`, campos de ubicación única y funciones antiguas permanecen hasta que el reemplazo complete regresión.  
**Consecuencia:** coexistencia temporal de estructuras y riesgo de doble conteo.

## ADR-016 — Reserva atómica inicial

**Estado:** reemplazada por ADR-024.  
**Decisión:** si no se cubre el pedido completo, revertir la reserva.  
**Motivo del reemplazo:** es incompatible con el lote comercial acumulativo (ADR-004). Un pedido cuyo lote se produce durante varios días nunca alcanzaría el saldo total y quedaría permanentemente no factible.

## ADR-017 — Criterio inicial del planificador

**Estado:** aceptada como primera regla, modificada por ADR-027.  
**Decisión:** seleccionar menor costo incremental entre planes factibles. El incumplimiento de la fecha límite no vuelve al plan no factible: se selecciona igual y el atraso se registra como indicador de servicio.  
**Evolución:** incluir riesgo, congestión, servicio y robustez.

## ADR-018 — Uso primario del modelo: dimensionamiento de campaña

**Estado:** aceptada.  
**Fecha:** 2026-07-24.  
**Contexto:** el borrador mezclaba dos usos con requisitos de fidelidad muy distintos: decisión táctica por pedido y dimensionamiento estratégico.  
**Decisión:** el uso primario es dimensionar depósitos, flota, posiciones y costo total de campaña.  
**Consecuencias:** el detalle horario deja de ser necesario; la variabilidad y la comparación de escenarios pasan a ser parte del alcance mínimo; el planificador se mantiene estructuralmente pero no se calibra a nivel de pedido individual.

## ADR-019 — Paso de tiempo diario y recursos por conteo diario

**Estado:** aceptada.  
**Contexto:** ADR-018.  
**Decisión:** la unidad base del reloj es el día. Los recursos finitos se modelan como capacidad diaria consumible (camión-día, posición-día) y las operaciones que no consiguen recurso se posponen al día siguiente registrando la espera.  
**Alternativas:** statecharts y colas horarias con la Process Modeling Library.  
**Consecuencias:** menos código y sin transiciones ilegales posibles; no se detecta congestión intradiaria; la regla de cross docking "mismo día" queda definida como coincidencia en el mismo día simulado entero.

## ADR-020 — Licencia de AnyLogic

**Estado:** propuesta, pendiente de decisión del responsable del proyecto.  
**Contexto:** el desarrollo se hace sobre PLE 8.9.9. PLE es de uso no comercial y limita el tamaño del modelo y los experimentos.  
**Problema:** el uso primario definido (ADR-018) requiere barridos de parámetros con réplicas, que es justamente lo que PLE limita; y un uso empresarial no está cubierto por la licencia.  
**Decisión propuesta:** resolver la licencia antes de la fase de escenarios. Mientras tanto, mantener el diseño libre de estructuras que dependan de límites de PLE y verificar empíricamente qué límite bloqueó la creación de `ExistenciaLote`.

## ADR-021 — Capas de inventario como unidad atómica

**Estado:** aceptada.  
**Reemplaza:** ADR-007.  
**Contexto:** el almacenaje debe imputarse por toneladas y días de permanencia, con retiros parciales.  
**Decisión:** la unidad atómica del inventario físico es la capa `(lote, ubicacion, diaIngreso, toneladas, toneladasReservadas)`. Un lote tiene N capas.  
**Consecuencias:** el storage se calcula exacto por capa; desaparecen las cuatro listas paralelas y su riesgo de desalineación de índices; se requiere una política de consumo explícita (ADR-022).

## ADR-022 — Consumo FIFO por día de ingreso

**Estado:** aceptada.  
**Decisión:** transferencias, reservas y despachos consumen capas en orden FIFO por `diaIngreso`. La imputación de costos de almacenaje sigue la misma regla.  
**Alternativas:** LIFO, costo promedio.  
**Consecuencias:** el costo de almacenaje deja de depender del orden de ejecución del código; permite reproducir el criterio contable habitual del depósito.

## ADR-023 — Stock de ubicación derivado de las capas

**Estado:** aceptada.  
**Contexto:** hoy coexisten `Planta.stock*` y `Deposito.stock*` con los saldos por ubicación del lote, lo que el propio documento identifica como riesgo de doble contabilidad.  
**Decisión:** el stock de una ubicación es una función derivada de la suma de sus capas. Las variables de stock se conservan sólo como series para gráficos, nunca como fuente de verdad.  
**Consecuencias:** el riesgo de doble contabilidad se elimina por diseño en lugar de vigilarse; la reconciliación de inventario se vuelve trivial.

## ADR-024 — Compromiso y reserva incremental

**Estado:** aceptada.  
**Reemplaza:** ADR-016.  
**Decisión:** se distinguen tres conceptos. *Compromiso*: asociación del pedido a un lote y su producción futura, sin bloquear stock. *Reserva*: bloqueo incremental de toneladas físicas existentes. *Despacho*: consumo de la reserva al cargar el contenedor. Un pedido pasa a ejecutable cuando su reserva alcanza al menos un contenedor completo.  
**Consecuencias:** compatible con lote acumulativo y con despacho parcial; exige una regla de prioridad entre pedidos (ADR-026) y trazabilidad de reservas (ADR-025).

## ADR-025 — Reserva como entidad trazable

**Estado:** aceptada.  
**Contexto:** con las reservas representadas como escalares no se puede saber qué pedido reservó qué toneladas, ni liberar la reserva correcta al cancelar, ni asociar reserva a contenedor.  
**Decisión:** la reserva es un registro `(idReserva, pedido, contenedor, lote, capa, toneladas, estado)`. Las toneladas reservadas de la capa se derivan de las reservas activas.  
**Consecuencias:** trazabilidad completa y liberación correcta; un registro más a mantener.

## ADR-026 — Prioridad entre pedidos

**Estado:** aceptada como regla inicial.  
**Decisión:** ante competencia por el mismo lote o recurso, prioridad por menor `diaLimite`, luego por antigüedad de llegada, luego por mayor volumen pendiente.  
**Consecuencias:** el resultado se vuelve determinístico ante empates; la política queda parametrizada para poder compararla como escenario.

## ADR-027 — La fecha límite es un indicador de servicio, no una restricción de factibilidad

**Estado:** aceptada.  
**Contexto:** el borrador dejaba abierto si un plan que incumple la fecha límite es no factible o penalizado.  
**Decisión:** en el uso de dimensionamiento el atraso se mide, no se prohíbe. Un plan que termina después del límite se ejecuta y alimenta el KPI de nivel de servicio.  
**Consecuencias:** el modelo no queda bloqueado por pedidos imposibles; el cut-off por buque queda fuera de alcance hasta que se necesite uso táctico.

## ADR-028 — Variabilidad y réplicas obligatorias

**Estado:** aceptada.  
**Contexto:** un modelo determinístico dimensiona sobre promedios y subestima los picos.  
**Decisión:** producción, tránsito, duración de operaciones, disponibilidad de flota y demanda tienen variabilidad parametrizable; los resultados se reportan sobre réplicas (default 30) con media, desvío y P95. Toda variabilidad debe poder anularse para obtener corridas determinísticas de verificación.  
**Consecuencias:** dimensionamiento defendible; mayor costo computacional; interacción con ADR-020.

## ADR-029 — Datos de entrada en tablas, con contrato único

**Estado:** aceptada.  
**Decisión:** ningún parámetro operativo, tarifa, capacidad o duración vive en código. Todo se lee de las tablas definidas en el contrato de datos, y el generador sintético produce exactamente las mismas tablas que el Excel.  
**Consecuencias:** el paso de datos sintéticos a datos reales no requiere cambios de lógica; una tarifa faltante produce error explícito (ADR-014) y no cero.

## Plantilla para nuevas decisiones

```markdown
## ADR-XXX — Título

**Estado:** propuesta | aceptada | reemplazada | descartada  
**Fecha:** YYYY-MM-DD  
**Contexto:** problema que origina la decisión.  
**Decisión:** solución adoptada.  
**Alternativas:** opciones evaluadas.  
**Consecuencias:** efectos positivos, negativos y riesgos.  
**Reemplaza:** ADR anterior, si aplica.
```
