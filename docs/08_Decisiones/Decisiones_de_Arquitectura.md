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

**Estado:** implementada en el modelo (clases Java `Capa` e `Inventario`, variable `Main.inventario`).  
**Reemplaza:** ADR-007.  
**Contexto:** el inventario debe soportar un lote distribuido en varias ubicaciones, con ingresos en fechas distintas y retiros parciales. La estructura documentada de cuatro listas paralelas nunca llegó a existir en el modelo (hallazgo H-03).  
**Decisión:** la unidad atómica del inventario físico es la capa `(lote, ubicacion, diaIngreso, toneladas, toneladasReservadas)`. Un lote tiene N capas.  
**Precisión sobre el almacenaje:** el devengo diario de storage no necesita capas — acumular `toneladas × tarifa` cada día funciona igual. Lo que sí exige capas es imputar IN y OUT por movimiento parcial, ordenar el consumo por antigüedad (ADR-022) y evitar partir el lote en agentes nuevos para reservar (hallazgo H-05).  
**Consecuencias:** desaparece la desalineación de índices de las listas paralelas; se requiere una política de consumo explícita (ADR-022); las capas no consumen slots de tipo de agente porque son clases Java (ADR-030).

## ADR-022 — Consumo FIFO por día de ingreso

**Estado:** implementada. `Inventario` ordena las capas por `diaIngreso`, desempatando por `diaProduccion` y `idLote` para que el consumo no dependa del orden de inserción.  
**Decisión:** transferencias, reservas y despachos consumen capas en orden FIFO por `diaIngreso`. La imputación de costos de almacenaje sigue la misma regla.  
**Alternativas:** LIFO, costo promedio.  
**Consecuencias:** el costo de almacenaje deja de depender del orden de ejecución del código; permite reproducir el criterio contable habitual del depósito.

## ADR-023 — Stock de ubicación derivado de las capas

**Contexto:** hoy coexisten `Planta.stock*` y `Deposito.stock*` con los saldos por ubicación del lote, lo que el propio documento identifica como riesgo de doble contabilidad.  
**Decisión:** el stock de una ubicación es una función derivada de la suma de sus capas. Las variables de stock se conservan sólo como series para gráficos, nunca como fuente de verdad.  
**Estado:** implementada. `Planta.stock*` y `Deposito.stock*`/`reservado*` se eliminaron; `getStock()` y `getReservado()` consultan `Main.inventario`. Desaparecen también las funciones mutadoras que mantenían esos saldos (`agregarStock`, `retirarStock`, `recibirProducto`, `retirarProducto`, `reservarProducto`, `liberarReserva`, `despacharReservado`): mover stock es mover capas.  
**Consecuencias:** el riesgo de doble contabilidad se elimina por diseño en lugar de vigilarse; la reconciliación de inventario se vuelve trivial. `Inventario.validar()` corre cada día y aborta la corrida si una capa tiene toneladas negativas o más reservado que saldo físico.

## ADR-024 — Compromiso y reserva incremental

**Estado:** implementada parcialmente. La reserva bloquea toneladas físicas sobre las capas y el despacho las consume, pero sigue siendo todo o nada por pedido: si no hay saldo libre suficiente se libera lo tomado y el pedido espera. Falta el compromiso sobre producción futura y el disparo por contenedor completo.  
**Reemplaza:** ADR-016.  
**Decisión:** se distinguen tres conceptos. *Compromiso*: asociación del pedido a un lote y su producción futura, sin bloquear stock. *Reserva*: bloqueo incremental de toneladas físicas existentes. *Despacho*: consumo de la reserva al cargar el contenedor. Un pedido pasa a ejecutable cuando su reserva alcanza al menos un contenedor completo.  
**Consecuencias:** compatible con lote acumulativo y con despacho parcial; exige una regla de prioridad entre pedidos (ADR-026) y trazabilidad de reservas (ADR-025).

## ADR-025 — Reserva como entidad trazable

**Contexto:** con las reservas representadas como escalares no se puede saber qué pedido reservó qué toneladas, ni liberar la reserva correcta al cancelar, ni asociar reserva a contenedor.  
**Decisión:** la reserva es un registro `(idReserva, pedido, contenedor, lote, capa, toneladas, estado)`. Las toneladas reservadas de la capa se derivan de las reservas activas.  
**Estado:** implementada parcialmente. Cada `Capa` guarda su lista de `Capa.Reserva` `(codigoPedido, toneladas, diaReserva)`, y `capa.reservadas()` es la suma de esas reservas, no un escalar aparte. Todavía no hay `idReserva` ni contenedor asociado: la clave es el `codigoPedido`, que basta para reservar, liberar y despachar por pedido. El contenedor se agrega en la fase 6.  
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
**Implementación:** `Main.pasoDiario` es el único evento del modelo. Cada fase es una función de `Main`: `producirEnPlantas`, `registrarPedidosDelDia`, `abrirPosicionesConsolidacionDelDia`, `abrirPosicionesCrossDockDelDia`, `programarCrossDockDelDia`, `revisarTransferenciasPlanta`, `revisarPedidosPendientes`, `prepararPedidosReservados`, `despacharContenedoresPendientes`, `devengarAlmacenamientoDiario` y `registrarAtrasos`. Desde la fase 8 el cross dock se resuelve antes de las transferencias normales: lo que cruza sale de planta directo contra un pedido, y sólo el excedente se guarda (ADR-041). Desde la fase 7 la fase 5 del esquema se desdobla: primero se abre la capacidad de estiba del día, después se arman los contenedores del pedido y por último se despacha lo que entra en esa capacidad (ADR-039). La última fase hoy sólo registra atrasos; los demás KPIs se agregan ahí.

## ADR-035 — El modelo se versiona junto a su espejo legible

**Estado:** aceptada.  
**Contexto:** el `.alp` es un XML con el código Java embebido: un cambio de una línea produce un diff irrevisable, y hasta ahora el modelo no estaba versionado.  
**Decisión:** el `.alp` es la fuente de verdad y se versiona. Además, `tools/exportar_modelo.py` genera `model_src/`, un espejo de sólo lectura con parámetros, variables, funciones y eventos por agente, que se regenera y se commitea en el mismo cambio.  
**Consecuencias:** los pull requests muestran el cambio real de lógica; `model_src/` puede quedar desactualizado si alguien olvida regenerarlo, por lo que la regeneración es parte de la definición de terminado.

## ADR-036 — Las tablas de entrada son una clase Java, no la base de datos de AnyLogic

**Estado:** aceptada.  
**Contexto:** el contrato de datos exige que el generador sintético y el importador de Excel produzcan el mismo esquema, y que la lógica no dependa del origen. AnyLogic ofrece su base de datos interna, pero el modelo ya usa los 10 tipos de agente de PLE (ADR-030) y la base interna obliga a que exista un archivo para poder correr.  
**Decisión:** las tablas viven en la clase Java `DatosEntrada` (una lista por tabla, más las consultas). Las llenan `GeneradorSintetico` e `ImportadorExcel`, cada uno sobre las mismas listas. La lógica de negocio consulta siempre `Main.datos`.  
**Alternativas:** base de datos interna de AnyLogic como fuente directa (ata el modelo a un archivo y a la sintaxis de consultas de AnyLogic en cada punto de uso); parámetros por agente (es lo que estamos sacando).  
**Consecuencias:** un escenario se define por parámetros de `Main` y una semilla, sin archivos; cambiar de sintético a Excel es cambiar quién llena las listas. El costo es que la carga desde Excel deberá escribirse a mano, fila por tabla.

## ADR-037 — Un dato faltante aborta el arranque, no vale cero

**Estado:** aceptada.  
**Contexto:** `Deposito.getTarifaAlmacenamiento()` devolvía el campo correspondiente al producto, y un campo sin cargar vale 0 en Java: un depósito sin tarifa de aceite almacenaba aceite gratis y el modelo devolvía un costo de campaña plausible pero falso.  
**Decisión:** las consultas de `DatosEntrada` lanzan una excepción con la clave que falta, y `DatosEntrada.validar()` recorre todas las combinaciones alcanzables (depósito × producto × terminal) al arrancar, junta todos los errores y aborta con la lista completa.  
**Alternativas:** validar sólo lo que se usa (el error aparece a mitad de campaña y depende del escenario); devolver un valor por defecto (es el problema que se está corrigiendo).  
**Consecuencias:** un dato mal cargado se ve antes de simular y de una sola vez; los agentes conservan sus campos de tarifa y capacidad, pero como copia que `Main.aplicarDatosAAgentes()` escribe desde las tablas, no como fuente.

## ADR-038 — El Excel se lee con `ExcelFile` del motor y por nombre de columna

**Estado:** aceptada.  
**Contexto:** la fase 2 del contrato necesita cargar las tablas desde un libro que mantiene una persona, no un programa: las columnas se reordenan, se agregan columnas propias, y un número termina tipeado como texto. Además el modelo corre en PLE, así que la solución no puede depender de librerías externas.  
**Decisión:** `ImportadorExcel` usa `com.anylogic.engine.connectivity.ExcelFile`, que ya viene con el motor (Apache POI por debajo), lee la primera fila como encabezados y busca cada columna por nombre. Las hojas y columnas faltantes se informan todas juntas antes de validar; las filas vacías se saltan; los números tipeados como texto se aceptan y los que no son números indican hoja, fila y columna. Las hojas con `id_escenario` se filtran por el parámetro `idEscenario`, de modo que un libro puede contener varios escenarios.  
**Alternativas:** Apache POI directo (misma dependencia, sin la garantía de que el motor la exponga en el futuro); la base de datos interna de AnyLogic (obliga a mantener el esquema en dos lugares y a importar a mano en cada cambio del libro); leer por posición de columna (rompe con cualquier edición del archivo).  
**Consecuencias:** el libro de entrada tolera edición humana y los errores de carga se corrigen de una sola vez. `datos/entrada_ejemplo.xlsx` no se escribe a mano: `tools/generar_excel_ejemplo.py` lo genera corriendo el propio `GeneradorSintetico`, así que la plantilla y la fase sintética no pueden divergir, y ambos orígenes producen el mismo resultado sobre el mismo escenario.

## ADR-039 — La posición de consolidación es un recurso contado por día, no un bloque de la biblioteca

**Estado:** aceptada.  
**Contexto:** la fase 7 necesita que consolidar tenga capacidad finita para poder dimensionar posiciones, pero el modelo avanza en pasos de un día (ADR-034) y el `Envio` recién entra al flujo cuando el contenedor está despachado. Un `ResourcePool` con `Seize`/`Release` mediría ocupación en tiempo continuo dentro de un día que el modelo no representa, y obligaría a meter en el diagrama la espera previa a la creación del envío.  
**Decisión:** cada sitio publica su capacidad de consolidación diaria (`posiciones_consolidacion × contenedores_por_posicion_dia`, hoy una sola columna `contenedores_por_dia`, ADR-048) y `Main.despacharContenedoresPendientes()` consume ese cupo una vez por día, en orden de fecha límite del pedido. El contenedor que no consigue posición queda en `ESPERANDO_PROGRAMACION`, suma un día en `diasEsperaPosicion` y compite de nuevo al día siguiente.  
**Alternativas:** `ResourcePool` por sitio (precisión intradiaria que el resto del modelo no tiene, y un tipo de recurso por sitio); capacidad del bloque `Delay` (limita simultaneidad, no contenedores por día, y no distingue sitios).  
**Consecuencias:** la utilización se mide como contenedores consolidados sobre capacidad ofrecida, y la escasez aparece como espera y como almacenaje adicional, que es lo que el dimensionamiento tiene que ver. Un sitio con capacidad cero detiene el despacho para siempre, así que `DatosEntrada.validar()` lo rechaza como error de datos.

## ADR-040 — El lugar de consolidación es una estrategia del escenario, no un dato del pedido

**Estado:** aceptada.  
**Contexto:** el contenedor se puede estibar en el depósito (y llegar consolidado a la terminal) o llevarse el producto a granel y consolidar en la terminal. Las dos opciones cambian costos, tiempos y qué sitio necesita posiciones, y el proyecto existe para compararlas.  
**Decisión:** el parámetro `Main.estrategiaConsolidacion` fija la estrategia de la corrida. Con `CONSOLIDACION_DEPOSITO` la carga en el depósito **es** la consolidación (tiempo por `velocidad_consolidacion_tn_hora` del depósito, tarifa de `TarifaServicioCarga` del depósito) y el bloque `consolidarCarga` queda de paso; con `CONSOLIDACION_TERMINAL` rige el comportamiento anterior. La tarifa y la velocidad salen siempre de las tablas del sitio, nunca de una constante por tipo de agente.  
**Alternativas:** decidir por pedido según costo (mezcla la política con la comparación de escenarios y hace irreproducible el dimensionamiento); dos diagramas de proceso paralelos (duplica el flujo y consume bloques sin agregar información).  
**Consecuencias:** la estrategia es una entrada del barrido, como la cantidad de camiones o la capacidad de depósito. También cambia la elección de depósito, porque el costo estimado del pedido incluye la tarifa de estiba del depósito candidato.

## ADR-041 — El cross docking es una modalidad del pedido con recurso, tarifa y exención de almacenaje propios

**Estado:** aceptada.  
**Contexto:** ADR-010 define el cross docking como una operación sin IN, storage ni OUT, y ADR-011 la ata al día operativo. Faltaba decidir con qué se cuenta la capacidad, quién cobra la estiba y cómo se evita que el producto que sólo cruza el sitio pague almacenaje, sabiendo que el inventario por capas (ADR-021) es la única fuente física y que el producto tiene que estar en el depósito para armar el contenedor.  
**Decisión:** cada sitio publica `posiciones_cross_dock` operaciones por día (una operación = un contenedor cruzado), contadas igual que las posiciones de consolidación (ADR-039). `Main.programarCrossDockDelDia()` recorre los pedidos pendientes por fecha límite y, si hay stock libre en planta, cupo en el sitio y camión disponible ese día, mueve el producto de planta al sitio, lo reserva para el pedido y lo marca `esCrossDock`. La estiba la cobra el sitio con la tarifa `CROSS_DOCK` de `TarifaServicioCarga` — más barata que `CONSOLIDACION` porque el producto no entra ni sale del almacenamiento — y el devengo diario excluye las toneladas reservadas por pedidos de cross dock, de modo que cruzar no genera storage. Si no hay cupo o camión, el pedido no se cruza ese día y sigue el camino normal.  
**Alternativas:** un sitio de cross dock fuera del inventario (rompe la fuente física única y la validación de capas); un `ResourcePool` por sitio (misma objeción que ADR-039); cobrar el almacenaje del día del cruce (contradice ADR-010 por un artefacto del paso diario).  
**Consecuencias:** el cross docking es una opción del escenario (`habilitaCrossDock`) que se compara contra la misma corrida sin cross dock, y su beneficio aparece como menos consolidación, menos espera de posición y menos almacenaje. Como la reserva sigue siendo todo-o-nada (ADR-016 pendiente de reserva parcial), un pedido se cruza entero o no se cruza: los pedidos más grandes que el cupo diario del sitio nunca usan cross dock y se cuentan en `crossDockReprogramados`.

## ADR-042 — El barrido tiene dos dimensiones y la semilla se calcula dentro del modelo

**Estado:** aceptada.  
**Contexto:** PLE no ofrece Custom Experiment, así que no se puede escribir un experimento que recorra una tabla de escenarios. Un escenario cambia varias palancas a la vez (flota, capacidad, tarifas, estrategia), y un Parameter Variation que enumere cada palanca genera el producto cartesiano de combinaciones que nadie pidió. Además el dimensionamiento necesita réplicas reproducibles: hay que poder volver a correr exactamente la réplica 17 de E-05.  
**Decisión:** el experimento `Escenarios` varía **`idEscenario` y `replica`, y nada más**. En modo freeform, `idEscenario = ESCENARIOS[(getCurrentIteration() - 1) / REPLICAS]` y `replica = (getCurrentIteration() - 1) % REPLICAS`. La semilla no se configura en la sección de aleatoriedad del experimento: se calcula dentro del modelo como `semillaBase + replica` y se pasa al generador, de modo que una réplica se reproduce corriendo el experimento `Simulation` con ese par de valores. Las corridas se evalúan en serie porque con evaluación paralela el agente raíz no está disponible al cerrar la corrida y los KPIs no se pueden leer.  
**Alternativas:** enumerar cada palanca como dimensión (producto cartesiano y escenarios que no existen); semilla aleatoria por corrida (no reproducible); una corrida por escenario lanzada a mano (no escala a 360).  
**Consecuencias:** agregar un escenario es agregar una fila y ampliar el rango; el experimento no se toca nunca. El precio es que la cantidad de réplicas vive en el experimento (`REPLICAS`) y tiene que coincidir con la cantidad de corridas.

## ADR-043 — Un escenario determinístico no puede tener sorteos, aunque tenga variabilidad cero

**Estado:** aceptada.  
**Contexto:** E-09 existe para verificar el modelo: si las variabilidades son cero, las 30 réplicas deberían dar el mismo resultado y la réplica no debería aportar nada. No pasaba: con variabilidad cero el desvío del costo total seguía siendo del 4%, porque el día de llegada del pedido, el plazo, el producto y la terminal se sorteaban igual y sólo la magnitud dejaba de tener ruido.  
**Decisión:** el escenario lleva un campo propio `deterministico`. Cuando está activo, el plan de pedidos se construye sin ningún sorteo: llegadas repartidas parejo en la ventana, plazo fijo, terminal por rotación y producto por posición dentro de la mezcla acumulada (que conserva la proporción de la producción media). La variabilidad cero sigue siendo un dato aparte, porque son dos cosas distintas: cuánto ruido tiene una magnitud y si la estructura del plan se sortea.  
**Alternativas:** tratar variabilidad cero como determinístico implícito (acopla dos conceptos y esconde los sorteos que quedan); correr E-09 con una sola réplica (no verifica nada, sólo evita mirar el desvío).  
**Consecuencias:** E-09 da desvío exactamente 0 en 30 réplicas y sirve como prueba de regresión del barrido. Sus resultados no son comparables con E-00 corrida a corrida, porque el plan de pedidos es otro; lo comparable es el comportamiento agregado.

## ADR-044 — Dos flotas: la de producto se cuenta en camión-día, la de portacontenedores se toma en el flujo

**Estado:** aceptada.  
**Contexto:** el modelo declaraba una sola flota (`flotaCamiones`, un `ResourcePool`) que sólo usaba el tramo depósito→terminal, y la transferencia planta→depósito movía todas las toneladas del día sin pedir camión: `camionDisponibleHoy()` preguntaba si el pool tenía alguna unidad libre y nada más. Por eso E-01 y E-02 daban exactamente lo mismo que E-00 y `utilizacion_flota` daba ~0 (ADR-042, fase 13). Son además dos flotas distintas en la realidad: el granel planta→depósito lo mueve un camión de carga, y el contenedor lo mueve un portacontenedor.  
**Decisión:** se modelan por separado y con el mecanismo que corresponde a cada paso de tiempo.

- **Producto (planta→depósito, incluido el cross dock):** capacidad diaria contada en **camión-día**, igual que las posiciones de consolidación (ADR-039). Cada día `abrirFlotaDelDia()` ofrece `camiones_producto` camión-día. Un viaje consume `2 × distancia / velocidad_camion_kmh / horas_operativas_dia`, es decir la fracción de jornada que tarda ida y vuelta, y mueve a lo sumo `capacidad_camion_tn`: mover más toneladas son más viajes, no un viaje más grande. `transferirToneladasLote()` acota lo que mueve a los viajes que todavía entran hoy y lo que sobra queda en planta para el día siguiente. `tomarFlotaProducto()` aborta la corrida si la capacidad del día se sobregira.
- **Portacontenedores (depósito→terminal):** siguen siendo el `ResourcePool` del flujo, renombrado `flotaPortacontenedores`, con capacidad `camiones_portacontenedor` fijada desde el escenario. El contenedor que no consigue camión espera en `colaCamiones`, que es lo que un pool sabe hacer y una capacidad diaria no.

El costo del flete planta→depósito pasa a cobrarse **por viaje** (fijo y kilometraje por viaje, tarifa por tonelada sobre lo movido), coherente con la cantidad de viajes que la flota efectivamente hizo.  
**Alternativas:** hacer que la transferencia diaria pase por los bloques `Seize`/`Release` del flujo (obliga a modelar cada viaje como entidad en un modelo de paso diario, sin ganar información); una sola flota para los dos tramos (mezcla camión de granel con portacontenedor y hace que el barrido no se pueda leer); dejar la utilización como muestreo diario del pool (medía 0,002 con la flota trabajando, porque no ve los viajes que empiezan y terminan el mismo día — ahora `utilizacionPortacontenedor()` usa la estadística del propio pool).  
**Consecuencias:** E-01 (1 camión de cada flota) y E-02 (6 y 8) ya no son iguales a E-00: cambian utilización, viajes, atraso y nivel de servicio. Aparece un efecto contraintuitivo que es real y conviene tener presente al leer el barrido: **más camiones cuestan más**, porque el producto llega antes al depósito y paga más almacenaje, mientras el excedente que espera en planta no paga nada (la planta no tiene tarifa de almacenaje en el contrato de datos). El punto de quiebre de la flota hay que leerlo en nivel de servicio y atraso, no en el costo total. La carga y la descarga todavía no le consumen tiempo al camión, porque la planta no tiene velocidades cargadas.

## ADR-045 — El tablero de la corrida se arma con las mismas funciones que el CSV del barrido

**Estado:** aceptada.  
**Contexto:** la vista de `Main` mostraba 54 textos sueltos, sin agrupar y superpuestos con el diagrama de flujo, y varios calculaban su valor con una expresión propia escrita en el texto. Dos riesgos: la pantalla se volvía ilegible a medida que crecía el modelo, y un indicador de pantalla podía decir algo distinto del KPI homónimo del barrido sin que nada lo detectara.  
**Decisión:** el tablero es una composición de ocho paneles temáticos y cinco gráficos, y **ningún indicador recalcula nada**: cada línea invoca la función de `Main` que ya usa el experimento (`costoTotalCampania()`, `nivelServicio()`, `utilizacionFlota()`, `utilizacionPortacontenedor()`, `usoPosicionesConsolidacion()`, etc.) o lee la variable de estado directamente. Las variables y los parámetros de `Main` dejan de dibujarse en la corrida (`PresentationFlag = false`), que es lo que ensuciaba la pantalla. La ventana temporal de los gráficos es el horizonte de la corrida, para que la campaña entera quede a la vista al terminar.  
**Alternativas:** dejar los textos sueltos (ilegible y con riesgo de divergencia); armar el tablero en un agente aparte (gasta uno de los diez tipos de agente que PLE permite, ADR-031); publicar los KPIs sólo por CSV (pierde el diagnóstico temporal: cuándo se satura la flota, cuándo empiezan los atrasos).  
**Consecuencias:** el tablero es una lectura **de una corrida**, no un resultado: salvo E-09 los números cambian entre réplicas, y la comparación entre escenarios se sigue haciendo sobre `resultados/kpis_por_corrida.csv`. El diagrama de flujo y las poblaciones quedan debajo del tablero en el mismo lienzo. Agregar un KPI ahora obliga a agregar primero la función en `Main`, que es lo que mantiene alineados tablero y barrido.

## ADR-046 — El tablero del barrido se calcula sobre la misma colección de corridas que el CSV

**Estado:** aceptada.  
**Fecha:** 2026-07-26  
**Contexto:** el experimento `Escenarios` no tenía pantalla: las 360 corridas avanzaban sin mostrar nada y la comparación entre escenarios sólo aparecía al final, en la consola y en el CSV. Para dimensionar hace falta ver antes qué configuración conviene, y el riesgo evidente era resolverlo con un tablero que recalculara sus propias medias y terminara diciendo algo distinto del CSV.  
**Decisión:** la pantalla del experimento lee `corridas`, la misma colección que `After simulation run` llena y que `After experiment` escribe en `resultados/kpis_por_corrida.csv`, a través de `mediaKpi`, `desvioKpi` y `muestra`, que ya existían para el resumen de consola. Cada corrida guarda además su configuración (`Corrida.config`, tomada de `root.datos.escenario`), así que la tabla describe el escenario con lo que el modelo efectivamente corrió y no con una leyenda escrita en el tablero. La comparación se publica como frente de decisión —dominado si otro escenario da a la vez mejor nivel de servicio y menor costo por tonelada— con barras relativas al rango del barrido, y no como un ranking por costo.  
**Alternativas:** gráficos de barras de AnyLogic en la pantalla del experimento (no hay tiempo de modelo que dispare su actualización, y las series por escenario habría que llenarlas a mano igual); un agente de tablero (gasta uno de los diez tipos que PLE permite, ADR-031); dejar la comparación sólo en el CSV (obliga a esperar el final del barrido y a salir del modelo para decidir).  
**Consecuencias:** agregar un KPI al tablero del barrido es agregarlo a `KPIS` y al arreglo de `Corrida.kpis`, con lo que pantalla, consola y CSV se mueven juntos. El frente de decisión sólo es válido entre escenarios con la misma producción y demanda: E-06 y E-07 cambian la escala del problema y aparecen como eficientes por eso; el panel lo advierte en pantalla, pero el modelo no lo impide. La dominancia usa costo por tonelada, que hereda la limitación de ADR-044 (la planta no cobra almacenaje).

## ADR-047 — El lote comercial acumula producción y las capas siguen siendo la existencia física

**Estado:** aceptada.  
**Fecha:** 2026-07-27  
**Contexto:** `Main.crearLoteEnPlanta()` creaba un `LoteProducto` nuevo por cada ingreso, y el ciclo diario la llamaba una vez por producto y por día. En una campaña de 183 días eso son ~549 identidades de lote, una por día de producción, con `toneladasIniciales` igual a lo producido *ese día*. El lote comercial —la unidad que se le vende a un cliente, con una calidad y un tamaño objetivo— no existía: no había cliente, ni calidad, ni toneladas objetivo, ni estado comercial, y no había forma de decir "este lote de 2.000 tn se completó en veinte días".

**Decisión:** se separan tres cosas que antes estaban colapsadas en el mismo objeto.

- **Identidad comercial:** `LoteProducto` suma `cliente`, `calidad`, `toneladasObjetivo` y `estadoComercial` (`ABIERTO`/`CERRADO`, lista de opciones nueva `EstadoComercialLote`). No es un tipo de agente nuevo: PLE admite diez y el modelo ya los usa (ADR-031).
- **Producción acumulada:** `toneladasIniciales` pasa a significar *lo producido acumulado del lote comercial*, no lo producido del día. Es la base de la regla de cierre y nunca se reduce.
- **Existencia física:** sigue viviendo en las capas del `Inventario` (ADR-021, ADR-023). Cada día de producción agrega **una capa nueva con el mismo `idLote`**, no un lote nuevo.

`crearLoteEnPlanta()` busca con `buscarLoteComercialAbierto(producto, cliente, calidad)` el lote abierto compatible; si lo encuentra acumula sobre él y conserva el `idLote`, y sólo crea una identidad nueva cuando el compatible ya está cerrado. Hay a lo sumo un lote abierto por combinación producto × cliente × calidad.

**Regla de cierre:** el lote se cierra cuando la **producción acumulada** alcanza `toneladasObjetivo`. El despacho y la transferencia parcial **no** lo cierran: reducen capas, no producción histórica, y mientras el lote esté abierto la producción posterior compatible sigue sumándose a la misma identidad. `toneladasObjetivo = 0` significa sin cierre por tamaño. Al terminar la campaña no hay más producción, así que el lote abierto queda cerrado de hecho sin necesidad de una acción de cierre.

**Fuente de los datos comerciales:** una sola, para no crear una segunda fuente de verdad. `toneladas_objetivo_lote_tn` es una columna nueva de la tabla `Producto` (el tamaño comercial es una característica del producto), y `cliente_default`/`calidad_default` son dos columnas nuevas de `Escenario` (hoy hay un solo cliente y una sola calidad, ADR-019). Las tres se generan sintéticamente y se leen del Excel por el mismo camino que el resto del contrato; nada queda cableado en código.

**Alternativas:** una tabla `LoteComercial` propia (duplica el tamaño objetivo que ya describe al producto, y obliga a decidir a qué lote entra cada día de producción con datos que todavía no existen); un tipo de agente `LoteComercial` con los `LoteProducto` como hijos (gasta uno de los diez tipos de PLE); dejar `toneladasIniciales` como producción del día y agregar un segundo campo acumulado (dos números que significan casi lo mismo y se desincronizan); cerrar el lote al despachar (haría que un pedido chico partiera un lote de 2.000 tn en decenas de identidades, que es el problema que se quiere resolver).

**Consecuencias:** hay una identidad comercial estable y trazable: `toneladasIniciales` responde "cuánto se produjo de este lote" y las capas responden "cuánto queda y dónde", que es lo que pedía ADR-023. La cantidad de agentes `LoteProducto` baja de ~549 a ~30 por campaña.

El consumo de stock **no** cambia: los pedidos siguen reservando por producto contra las capas del depósito en FIFO, no contra un lote comercial (la reserva por lote es fase 5 y sigue pendiente). Pero el barrido **sí** se mueve, y conviene entender por qué antes de leerlo como una mejora del negocio:

`transferirToneladasLote()` cobra y consume flota por viajes, y los viajes son `ceil(toneladas / capacidad_camion_tn)` **por llamada** (ADR-044). Con un lote por día, cada llamada movía como máximo la producción de un día, así que el redondeo del último camión parcial se pagaba **una vez por día y por producto**: con camión de 25 tn, la cáscara (60 tn/día) gastaba 3 viajes donde necesitaba 2,4 y el aceite (8 tn/día) gastaba 1 viaje entero para 0,32. Al acumular, el redondeo ocurre una vez por tanda de transferencia y no una vez por día de producción, que es el cálculo correcto. El efecto medido sobre 30 réplicas es consistente en los doce escenarios: **12% a 16% menos viajes planta–depósito**, 0,3% a 2,6% menos costo total, y toneladas y contenedores exportados prácticamente iguales (+0,05%). Donde más se nota es en los escenarios con flota escasa, porque eran los que más pagaban el desperdicio: E-01 baja el atraso medio de 3,17 a 2,20 días. La respuesta de dimensionamiento no cambia (E-01 sigue siendo el peor en servicio y atraso, y E-02 sigue sin comprar nada sobre E-00), pero las series anteriores a `fase-17` no son comparables con las nuevas.

## ADR-048 — La capacidad de la planta es un umbral, no un tope: el producto no se descarta

**Estado:** aceptada.  
**Fecha:** 2026-07-27  
**Contexto:** `Planta.producir()` comparaba la producción del día contra la capacidad nominal y lo que no entraba se acumulaba en `excedenteJugo`/`excedenteCascara`/`excedenteAceite`, es decir **se descartaba**. Con producto ya cosechado eso no existe en la operación real: la fruta se procesa y el subproducto ocupa lugar, aunque el frío esté lleno. Además el descarte **ocultaba el faltante de frío** justamente en el escenario que debía mostrarlo: E-06 (producción +30 %) tiraba el excedente y terminaba pareciéndose al caso base. Del otro lado, la transferencia planta→depósito se disparaba con `nivelActivacion*` y `stockObjetivo*` en toneladas absolutas por planta, así que la planta se vaciaba apenas superaba el nivel y no había forma de expresar "aprovechar el frío propio primero".

**Decisión:** la capacidad nominal de la planta pasa a ser un **nivel de referencia** y no un bloqueo.

- **Sin pérdida.** `producir()` ingresa la producción completa del plan. `excedente*` desaparece de `Planta`.
- **Umbrales en porcentaje, en el escenario:** `umbral_alerta_pct` (85), `umbral_objetivo_pct` (100) y `umbral_sobrecarga_pct` (105). Ninguno bloquea: son niveles de lectura, y reemplazan a `nivelActivacion*`/`stockObjetivo*`, que eran toneladas cableadas por agente.
- **Se mide lo que antes se tiraba:** `tonDiaSobreNominalPlanta`, `tonDiaSobreCriticoPlanta`, `diasSobrecargaPlanta` y `picoOcupacionPlantaPct`, registrados una vez por día en `registrarOcupacionPlanta()`.
- **Forecast perfecto** de `dias_forecast` días (default 7): `forecastProduccion()` lee el plan de producción futuro. Para dimensionar, la cota optimista es la lectura honesta; un forecast con error es un dato de entrada más y se agrega después sin tocar la política.
- **Dos políticas de frío propio** (`politica_frio_propio`): `FLEXIBLE` retiene en planta y transfiere sólo lo necesario para no pasar el nivel objetivo dentro del horizonte y para cubrir las obligaciones pendientes; `REACTIVA` conserva el comportamiento anterior, con los umbrales derivados de los porcentajes en lugar de las toneladas cableadas.
- **La capacidad de consolidación se expresa en `contenedores_por_dia`** por sitio, una sola columna en lugar de `posiciones_consolidacion × contenedores_por_posicion_dia`. Es el indicador que se usa en la operación y da el mismo número.

La regla de "no perder producto" aplica **sólo a la planta**. Los depósitos de terceros conservan capacidad dura, porque se contratan y se facturan: si no hay lugar afuera, el producto se queda en planta por encima del 100 %. Así el desborde de planta mide el faltante de frío de **toda la red**, que es la pregunta de dimensionamiento.

**Alternativas:** mantener el descarte (mide mal el faltante y no representa la operación); bloquear la producción al llegar al 105 % (equivale a parar la planta, que es una decisión de negocio que el modelo no debe tomar solo); modelar el turno como unidad de tiempo (`turnos_por_dia × contenedores_por_turno`) en vez de `contenedores_por_dia` (más parámetros, mismo resultado); forecast con error desde el principio (agrega un parámetro de ruido sin cambiar la conclusión de dimensionamiento).

**Consecuencias:** el barrido se mueve por diseño y las series anteriores a `fase-19` no son comparables. Sobre 30 réplicas, con el frío propio flexible el producto sale más tarde de la planta y paga menos almacenaje de terceros: el costo de caja baja entre 6,9 % y 14,3 % según el escenario, y el nivel de servicio sube (E-00 de 0,941 a 0,987, E-07 de 0,606 a 0,972) porque el stock queda cerca del origen y disponible. La sobrecarga aparece donde tiene que aparecer: E-01 (flota mínima) acumula 452 tn-día sobre el nivel nominal con un pico de 104,3 % de ocupación, y el resto de los escenarios queda en 100 %. `excedente_final_tn` sigue en el CSV pero **cambia de significado**: es el stock que queda en la red al cierre, no producto perdido; el faltante de frío se lee en `ton_dia_sobre_nominal`, `dias_sobrecarga` y `pico_ocupacion_planta_pct`. Se agrega el escenario E-12 para comparar `REACTIVA` contra el default `FLEXIBLE`.

## ADR-049 — El costo de oportunidad del frío propio se reporta separado del costo de caja

**Estado:** aceptada.  
**Fecha:** 2026-07-27  
**Contexto:** la planta no tiene tarifa de almacenaje, así que guardar en frío propio cuesta cero en el modelo. Eso produce el efecto contraintuitivo de ADR-044 (más camiones cuestan más, porque sacar el producto de la planta lo hace pagar depósito) y deja al evaluador sin nada que comparar: siempre le conviene la planta. La corrección obvia es ponerle una tarifa interna, pero si esa tarifa entra al costo total, `costo_usd_tn` deja de ser comparable contra la cotización de un tercero y el frente de decisión del tablero se ordena por un número que no es plata.

**Decisión:** dos cifras en paralelo, nunca una sola.

- `costo_total_caja` (`costoTotalCampania()`): lo que efectivamente se paga. Es la serie comparable con las corridas anteriores y con una cotización real.
- `costo_total_economico` (`costoTotalEconomico()`): caja **más** el costo de oportunidad del frío propio (`oportunidad_usd_tn_dia`, devengado por `devengarOportunidadFrioPropio()`) **más** la penalidad por tonelada-día sobre el nivel nominal (`penalidad_sobrecarga_usd_tn_dia`).

Las decisiones de política se toman con el económico; los KPIs reportan los dos, con `costo_economico_usd_tn` al lado de `costo_usd_tn`. Con las dos tarifas nuevas en 0 el económico es idéntico al de caja, así que el default no cambia ningún resultado.

**Alternativas:** sumar la oportunidad al costo total (rompe la comparabilidad y contamina el KPI que se lleva a una negociación); dejar la planta en cero (el evaluador no discrimina); cobrar la oportunidad como si fuera una tarifa de depósito más (mismo problema, además de mezclar caja con economía en la misma cuenta).

**Consecuencias:** en el barrido actual el económico corre por encima del de caja de forma consistente (E-00: USD 1,57 M de caja contra USD 2,20 M económico), y esa brecha **es** el valor del frío propio que hoy no se ve en la contabilidad. Al leer el tablero hay que decir cuál de los dos se está mirando: son dos ordenamientos distintos y el de caja sigue favoreciendo retener en planta.

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
