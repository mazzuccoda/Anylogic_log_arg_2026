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

## ADR-020 — El proyecto se desarrolla dentro de los límites de PLE

**Estado:** aceptada.  
**Contexto:** no hay licencia paga disponible. Los límites de PLE fueron verificados contra la documentación oficial y contra el modelo (ver [Inventario del modelo](../03_Logica/Inventario_del_Modelo.md) §2).  
**Hallazgo que corrige un supuesto previo:** Parameter Variation, Monte Carlo, Compare Runs, Sensitivity Analysis y Calibration **sí** están disponibles en PLE. El barrido de escenarios con réplicas, que es el uso primario (ADR-018), no está bloqueado. Tampoco lo está el horizonte de 183 días: el límite de 5 horas de tiempo de modelo no aplica a la Process Modeling Library, la única librería que el modelo usa.  
**Límites que sí condicionan el diseño:**

| Límite | Valor | Regla que impone |
|---|---:|---|
| Tipos de agente | 10, **ya agotados** | ninguna estructura nueva puede ser un tipo de agente (ADR-030) |
| Agentes dinámicos por corrida | 50 000 | presupuestar creación de lotes, reservas y contenedores; no crear agentes para representar datos |
| Custom Experiment | no disponible | los barridos se construyen con Parameter Variation, no con código de experimento propio (ADR-032) |
| OptQuest | 500 iteraciones, 7 variables | la optimización queda fuera del alcance comprometido |

**Consecuencias:** el proyecto es viable en PLE tal como está definido. Queda una restricción no técnica: la licencia PLE autoriza únicamente aprendizaje personal e instrucción, de modo que un uso comercial del modelo requiere licencia Professional. Es una decisión del responsable del proyecto, no un impedimento para construirlo.

## ADR-021 — Capas de inventario como unidad atómica

**Estado:** aceptada.  
**Reemplaza:** ADR-007.  
**Contexto:** el inventario debe soportar un lote distribuido en varias ubicaciones, con ingresos en fechas distintas y retiros parciales. La estructura documentada de cuatro listas paralelas nunca llegó a existir en el modelo (hallazgo H-03).  
**Decisión:** la unidad atómica del inventario físico es la capa `(lote, ubicacion, diaIngreso, toneladas, toneladasReservadas)`. Un lote tiene N capas.  
**Precisión sobre el almacenaje:** el devengo diario de storage no necesita capas — acumular `toneladas × tarifa` cada día funciona igual. Lo que sí exige capas es imputar IN y OUT por movimiento parcial, ordenar el consumo por antigüedad (ADR-022) y evitar partir el lote en agentes nuevos para reservar (hallazgo H-05).  
**Consecuencias:** desaparece la desalineación de índices de las listas paralelas; se requiere una política de consumo explícita (ADR-022); las capas no consumen slots de tipo de agente porque son clases Java (ADR-030).

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

## ADR-030 — Las estructuras de datos son clases Java, no tipos de agente

**Estado:** aceptada.  
**Contexto:** los 10 tipos de agente que permite PLE están ocupados (H-01), y el diseño necesita al menos tres estructuras nuevas: capa de inventario, reserva y registro de costo.  
**Decisión:** toda estructura que sea sólo datos y comportamiento propio se implementa como **clase Java** dentro del modelo (`New > Java Class`). Se reserva el tipo de agente para lo que necesita capacidades de agente: participar en un flowchart, tener statechart, moverse o animarse.  
**Alternativas:** liberar slots retirando `Envio` y `Camion` antes de tiempo (obliga a migrar el camino legado antes de tener reemplazo, contra ADR-009); usar listas paralelas (ADR-007, ya reemplazada).  
**Consecuencias:** el límite de PLE deja de condicionar el modelo de dominio; menor consumo de memoria y ningún consumo del cupo de 50 000 agentes dinámicos; se pierde la posibilidad de animar esas entidades, que no se necesita. AnyLogic permite convertir una clase Java en tipo de agente más adelante si hiciera falta.

## ADR-031 — Presupuesto de tipos de agente

**Estado:** aceptada.  
**Decisión:** los 10 slots quedan asignados así, y agregar un tipo exige retirar otro en el mismo cambio:

| Slot | Tipo | Situación |
|---|---|---|
| 1-6 | `Main`, `Planta`, `Deposito`, `Terminal`, `Pedido`, `LoteProducto` | permanentes |
| 7 | `ContenedorExportacion` | permanente; ejecuta el flowchart de despacho |
| 8 | `PlanLogistico` | permanente hasta que el planificador se convierta en función pura |
| 9 | `Envio` | legado, se retira al validar el reemplazo (fase 12) |
| 10 | `Camion` | legado, se retira junto con `Envio` |

**Consecuencias:** el proyecto sabe de antemano que tiene exactamente dos slots recuperables y no puede gastarlos en estructuras de datos.

## ADR-032 — El barrido se construye con Parameter Variation sobre parámetros de `Main`

**Estado:** aceptada.  
**Contexto:** PLE no ofrece Custom Experiment, que sería la forma natural de recorrer una tabla de escenarios por código.  
**Decisión:** cada escenario se identifica con un parámetro `idEscenario` de `Main`; el experimento de Parameter Variation recorre `idEscenario × replica`, y `Main` carga de la tabla `Escenario` todos los valores correspondientes al arrancar. La semilla es `semillaBase + replica`, fijada en el modelo y no en el experimento.  
**Alternativas:** enumerar cada parámetro barrido como dimensión del experimento (no escala y desacopla el barrido del contrato de datos).  
**Consecuencias:** agregar un escenario es agregar una fila, no tocar el experimento; el experimento queda con dos dimensiones sin importar cuántos parámetros cambien; exige que los parámetros de escenario se lean en el arranque y no en la definición del agente.

## ADR-033 — Parámetros para entradas, variables para estado

**Estado:** aceptada.  
**Contexto:** hoy todo el estado del modelo está declarado como parámetro (H-02): `stockJugo`, `excedenteJugo`, `costoFletePlantaDeposito` y `siguienteIdLote` conviven con `capacidadJugo` sin distinción.  
**Decisión:** un parámetro es una entrada que el escenario fija y nunca se modifica durante la corrida. Todo lo que cambia durante la corrida es variable. Todo lo que se reporta es variable o dataset.  
**Consecuencias:** la lista de parámetros de cada agente pasa a ser exactamente la interfaz de configuración que el barrido manipula; el reset entre réplicas queda bien definido; requiere una migración de las declaraciones existentes.  
**Implementación:** aplicada en `Main`, `Planta`, `Deposito` y `Terminal`. Los agentes de entidad (`Pedido`, `LoteProducto`, `Envio`, `ContenedorExportacion`, `PlanLogistico`, `Camion`) conservan sus campos como parámetros porque se crean en tiempo de corrida y no forman parte de la interfaz del experimento; se migran junto con el rediseño de inventario.

## ADR-034 — Secuencia diaria explícita

**Estado:** aceptada.  
**Contexto:** cinco eventos ocurren cada día y su orden lo decide el motor, no el modelo (H-07); además dos eventos usan cadencias de 1,2 y 1,9 días (H-06).  
**Decisión:** un único evento diario `pasoDiario` invoca las fases en orden fijo:

```text
1. producir
2. recibir e ingresar (IN)
3. planificar y comprometer pedidos
4. reservar
5. ejecutar movimientos y consolidar (consume recursos del día)
6. devengar almacenaje (storage)
7. registrar KPIs del día
```

**Alternativas:** prioridades por evento (funciona, pero deja la secuencia implícita y dispersa).  
**Consecuencias:** el resultado deja de depender del orden interno de creación de los eventos; el devengo de almacenaje queda definido sobre el saldo de cierre del día; cualquier operación nueva debe declarar en qué fase entra.  
**Implementación:** `Main.pasoDiario` es el único evento del modelo. Cada fase es una función de `Main`: `producirEnPlantas`, `revisarTransferenciasPlanta`, `registrarPedidosDelDia`, `revisarPedidosPendientes`, `prepararPedidosReservados`, `devengarAlmacenamientoDiario` y `registrarAtrasos`. La fase 7 hoy sólo registra atrasos; los demás KPIs se agregan ahí.

## ADR-035 — El modelo se versiona junto a su espejo legible

**Estado:** aceptada.  
**Contexto:** el `.alp` es un XML con el código Java embebido: un cambio de una línea produce un diff irrevisable, y hasta ahora el modelo no estaba versionado.  
**Decisión:** el `.alp` es la fuente de verdad y se versiona. Además, `tools/exportar_modelo.py` genera `model_src/`, un espejo de sólo lectura con parámetros, variables, funciones y eventos por agente, que se regenera y se commitea en el mismo cambio.  
**Consecuencias:** los pull requests muestran el cambio real de lógica; `model_src/` puede quedar desactualizado si alguien olvida regenerarlo, por lo que la regeneración es parte de la definición de terminado.

## ADR-036 — Las tablas de entrada son una clase Java, no la base de datos de AnyLogic

**Estado:** aceptada.  
**Contexto:** el contrato de datos exige que el generador sintético y el importador de Excel produzcan el mismo esquema, y que la lógica no dependa del origen. AnyLogic ofrece su base de datos interna, pero el modelo ya usa los 10 tipos de agente de PLE (ADR-030) y la base interna obliga a que exista un archivo para poder correr.  
**Decisión:** las tablas viven en la clase Java `DatosEntrada` (una lista por tabla, más las consultas). `GeneradorSintetico` las llena hoy; el importador de Excel las llenará mañana leyendo la base interna y traduciendo cada fila a la misma lista. La lógica de negocio consulta siempre `Main.datos`.  
**Alternativas:** base de datos interna de AnyLogic como fuente directa (ata el modelo a un archivo y a la sintaxis de consultas de AnyLogic en cada punto de uso); parámetros por agente (es lo que estamos sacando).  
**Consecuencias:** un escenario se define por parámetros de `Main` y una semilla, sin archivos; cambiar de sintético a Excel es cambiar quién llena las listas. El costo es que la carga desde Excel deberá escribirse a mano, fila por tabla.

## ADR-037 — Un dato faltante aborta el arranque, no vale cero

**Estado:** aceptada.  
**Contexto:** `Deposito.getTarifaAlmacenamiento()` devolvía el campo correspondiente al producto, y un campo sin cargar vale 0 en Java: un depósito sin tarifa de aceite almacenaba aceite gratis y el modelo devolvía un costo de campaña plausible pero falso.  
**Decisión:** las consultas de `DatosEntrada` lanzan una excepción con la clave que falta, y `DatosEntrada.validar()` recorre todas las combinaciones alcanzables (depósito × producto × terminal) al arrancar, junta todos los errores y aborta con la lista completa.  
**Alternativas:** validar sólo lo que se usa (el error aparece a mitad de campaña y depende del escenario); devolver un valor por defecto (es el problema que se está corrigiendo).  
**Consecuencias:** un dato mal cargado se ve antes de simular y de una sola vez; los agentes conservan sus campos de tarifa y capacidad, pero como copia que `Main.aplicarDatosAAgentes()` escribe desde las tablas, no como fuente.

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
