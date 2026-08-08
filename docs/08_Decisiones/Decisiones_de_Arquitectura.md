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

## ADR-050 — El circuito logístico es físico y se decide por pedido

**Estado:** aceptada.  
**Fecha:** 2026-07-28  
**Contexto:** hasta `fase-19` la cadena de bloques era única y lineal (`entradaEnvios → colaCamiones → tomarCamion → cargarCamion → viajarPuerto → descargarPuerto → consolidarCarga → retornarDeposito → liberarCamion`), sin ningún `SelectOutput`. Todos los envíos hacían el mismo recorrido y `estrategiaConsolidacion` sólo cambiaba qué tarifa se cobraba y qué cupo se consumía. Dos consecuencias concretas: `consolidarCarga` estaba **después** de `descargarPuerto`, así que "consolidación en depósito" cobraba tarifa de depósito sobre una estiba que el flujo ejecutaba en el puerto; y el portacontenedor aparecía en el origen sin haber viajado vacío desde la terminal, de modo que la utilización del pool medía medio ciclo.

**Decisión:** el flujo se bifurca y cada envío lleva su propio circuito.

1. `seleccionarCircuito` (`SelectOutput`, condición `usaPortacontenedor(envio)`) separa los circuitos que usan portacontenedor de los que no.
2. Circuitos 1 a 3 (consolidación en planta, consolidación en depósito, cross docking en depósito): `colaCamiones → tomarCamion → viajarVacioAlOrigen → cargarCamion → viajarPuerto → descargarPuerto → retornarDeposito → liberarCamion`. El portacontenedor hace el **round trip** terminal → origen → terminal → origen y la estiba ocurre en `cargarCamion`, es decir en el sitio real, no en el puerto. *(El cuarto tramo `retornarDeposito` se elimina en ADR-062: el ciclo termina en la terminal.)*
3. Circuito 4 (consolidación / cross docking en terminal, que son el mismo servicio): `cargarGranel → viajarTerminalGranel → descargarTerminal → consolidarCarga`. El producto viaja **a granel** consumiendo jornada de la flota de producto, el contenedor ya está en la terminal y el pool de portacontenedores **no se toca**.
4. El circuito deja de ser un parámetro global de la corrida: se resuelve por pedido en `circuitoDe(idSitioOrigen, esCrossDock)`, se guarda en `Pedido.estrategiaSeleccionada` y `Envio.circuito`, y `estrategiaConsolidacion` queda como **política por defecto**. Es lo que permite enchufar después el evaluador `PlanLogistico` sin volver a tocar el flujo.
5. El origen se generaliza: `Pedido.idSitioOrigen` y `Envio.idSitioOrigen` son el `id_ubicacion` del sitio (`"PLANTA"` o el id del depósito), y reserva, despacho, distancias, fletes y tarifa de estiba se resuelven contra ese id. `depositoAsignado`/`depositoOrigen` se conservan como referencia derivada (`null` si el origen es la planta).

**Alternativas:** dejar la estrategia global y sólo mover `consolidarCarga` antes del puerto (arregla el cobro pero no representa los cuatro circuitos ni el tramo vacío); modelar cada circuito con su propia cadena completa de bloques (duplica nueve bloques por circuito y en PLE se vuelve ilegible); crear un tipo de agente por circuito (no hay presupuesto: el modelo está en 10 de 10 tipos); hacer que el circuito de terminal también tome el pool (mide una ocupación que no existe, porque ahí no hay portacontenedor haciendo el viaje).

**Consecuencias:** el barrido pasa a `fase-21` y suma cinco KPIs por circuito (`contenedores_circuito_planta`, `_deposito`, `_cross_dock`, `_terminal` y `viajes_granel_terminal`) más el escenario E-13 (consolidación en planta), con 14 escenarios × 30 réplicas = 420 corridas. Medias de 30 réplicas contra `fase-19`:

- **El circuito de depósito no cambia de costo** (E-00: USD 1.573.338 y 124,37 USD/tn en las dos fases): la estiba ya se cobraba en el sitio, ahora además ocurre ahí. Lo que cambia es la ocupación del pool, que sube porque el tramo vacío es real (E-00 de 11 % a 13 %).
- **Donde el pool es escaso, el round trip se paga en servicio**: E-01 (1 camión de producto, 1 portacontenedor) cae de 0,98 a 0,77 de nivel de servicio y el atraso sube de 0,49 a 2,06 días. Antes el mismo portacontenedor rendía el doble porque no viajaba vacío.
- **El circuito de terminal deja de consumir el pool**: E-11 pasa de 11 % a 0 % de utilización de portacontenedor, con 530 viajes a granel, y el costo sube 1,1 % (USD 1.599.602 → 1.617.454) porque esas toneladas ahora pagan flete de producto hasta la terminal.
- **E-13 (nuevo, consolidación en planta)** entrega 121,29 USD/tn contra 124,37 de E-00, pero con 0,90 de nivel de servicio: el cuello es `contenedores_por_dia` de la planta, que es un dato sintético hasta que se cargue el real.

Queda un hueco declarado: el tramo vacío consume tiempo y pool pero **no** cuesta, porque la tarifa del ciclo de contenedor todavía no existe (fase 10). Los datos operativos de la planta (velocidades, `contenedores_por_dia`, distancias y tarifas planta → terminal) son sintéticos equivalentes al depósito de referencia y están marcados como supuesto en el contrato de datos.

## ADR-051 — Toda tarifa tiene unidad, proveedor y vigencia, y se resuelve por día de campaña

**Estado:** aceptada.  
**Fecha:** 2026-07-28  
**Contexto:** hasta `fase-21` había dos problemas superpuestos. Primero, **la tarifa de flete estaba en las tablas y el modelo no la usaba para cobrar**: lo que se devengaba eran tres parámetros cableados en `Main` (`envio.costoFleteReal = costoFijoViajePD + distancia * 2 * costoKmPD`), y `TarifaFleteProducto` se leía sólo para *estimar* y elegir depósito. Cambiar esa tarifa cambiaba qué depósito se elegía pero no el costo de campaña; los costos del circuito de depósito que en F21 no se movieron un centavo no eran estables, eran insensibles. Segundo, cada concepto vivía en su propia tabla con su unidad implícita, todas por tonelada, así que el último contenedor parcial de 5 tn pagaba la quinta parte de la consolidación de uno de 25, y faltaban seis conceptos (IN, OUT, THC, costo terminal, despachante, espera) más la tarifa de round trip por contenedor. Además las tarifas reales se negocian **por mes**, y el contrato tenía una vigencia por campaña.

**Decisión:** un solo contrato de costos con cuatro tablas —`TarifaFleteProducto`, `TarifaRoundTrip`, `TarifaSitio` y `TarifaEspera`— y estas reglas:

1. Toda tarifa lleva `proveedor`, `vigencia_desde`, `vigencia_hasta` y `habilitada`. Las consultas (`tarifaFlete`, `tarifaRoundTrip`, `tarifaSitio`, `tarifaEspera`) reciben el **día de campaña** y devuelven la fila vigente ese día. Si no hay fila vigente, o si hay dos vigentes para la misma clave, la corrida aborta con la clave y el día en el mensaje: un cero silencioso por falta de cobertura sería un error de datos disfrazado de resultado.
2. Toda tarifa lleva su `unidad` (`USD_VIAJE`, `USD_TN`, `USD_CONTENEDOR`, `USD_TN_DIA`, `USD_HORA`, `USD_OPERACION`, `USD_PEDIDO`) y la unidad decide la base: `importe(unidad, tarifa, toneladas, contenedores, motivo)` elige entre toneladas y contenedores y rechaza una unidad que no corresponda al concepto.
3. El flete de producto soporta las **dos** unidades en la misma tabla (`USD_VIAJE` con `variable_usd_tn` opcional, o `USD_TN`), porque hoy se contrata por viaje y la comparación contra una cotización por tonelada tiene que poder hacerse sin cambiar el modelo.
4. El round trip es una tarifa por contenedor y se devenga **al completar** el ciclo terminal → origen → terminal: un circuito truncado al cierre de campaña no genera cargo.
5. El último contenedor parcial paga **contenedor completo** en consolidación, cross dock, THC, costo terminal, despachante y round trip; sólo el flete de producto y el almacenaje se cobran por tonelada.
6. `GeneradorSintetico` e `ImportadorExcel` llenan las mismas listas, y los valores sintéticos están calibrados para que el barrido reproduzca `fase-21` fila por fila: la migración del contrato **no** es una recalibración disfrazada.

**Alternativas:** dejar la fórmula cableada y usar las tarifas sólo para estimar (es el estado que se está corrigiendo: el barrido no responde a los datos); una tabla por concepto (seis búsquedas para la misma clave `sitio`/`producto`/día, y seis lugares donde olvidarse de la vigencia); una vigencia por campaña (más simple, pero no es lo que existe: hay tarifas por mes); devolver 0 cuando no hay cobertura (convierte un dato faltante en un resultado barato, que es el peor error posible en un modelo de dimensionamiento).

**Consecuencias:** cambiar una tarifa en el Excel ahora cambia el costo de campaña, que era el objetivo. El barrido de `fase-21` se reproduce exactamente: 420 corridas comparadas fila por fila, 26 columnas idénticas y una sola diferencia de 1 × 10⁻⁴ USD en `costo_total_economico_usd` de una réplica, que es orden de suma de punto flotante. Quedan con estructura y consulta pero sin devengo hasta C3: IN, OUT, THC, costo terminal, despachante y espera (todos en 0, así que no mueven ningún número). El costo por tonelada **todavía no** es comparable contra una cotización real, y no lo será hasta que esos conceptos se cobren.

## ADR-052 — El registro de cargos es la fuente de verdad del costo; los acumuladores son vistas

**Estado:** aceptada.  
**Fecha:** 2026-07-28  
**Contexto:** el costo de campaña se calculaba sumando once acumuladores repartidos entre `Main`, `Deposito` y `Terminal` (`costoAlmacenamientoTotal`, `costoFletePlantaDeposito`, `costoConsolidacion`, …). Con ese diseño no hay nada contra qué reconciliar: si un devengo se registraba dos veces, o no se registraba, el total seguía cerrando consigo mismo. Tampoco había forma de responder "por qué costó esto" ni de totalizar por pedido, contenedor, sitio o día.

**Decisión:** existe un registro plano de cargos, `RegistroCostos` (clase Java, no un tipo de agente: el modelo está en 10 de 10 de PLE), y el total sale de ahí.

1. Cada evento económico se registra como un `Cargo` inmutable con identidad propia, día, categoría, tipo (`CAJA` o `ECONOMICO`), pedido, contenedor, lote, producto, origen, destino, sitio, estrategia, proveedor, unidad, cantidad, tarifa, importe, operación y motivo.
2. El importe se **calcula** en el registro (`importe = cantidad × tarifa`): no se puede registrar un importe que no se corresponda con su cantidad y su tarifa. Cantidad o tarifa negativa aborta la corrida.
3. `registrar()` es idempotente por operación, categoría, unidad y motivo, y devuelve el importe registrado —0 si el cargo ya estaba—, de modo que los acumuladores existentes suman exactamente lo que entró al registro.
4. Totales por categoría, tipo, pedido, contenedor, producto, sitio, estrategia y día son consultas sobre la misma lista. `costoTotalCampania()` devuelve `registro.total(CAJA)` y el económico `registro.total()`.
5. `reconciliarCostos()` compara, todos los días y al cierre, cada acumulador de agente contra el total de su categoría en el registro y aborta la corrida si difieren en más de 0,01 USD. El barrido lo ejecuta antes de escribir los KPIs de cada corrida.
6. El detalle se puede volcar a csv con `registro.exportarCsv(ruta)`, que **no** se llama en el barrido: una campaña son cientos de miles de cargos y el archivo sólo hace falta cuando hay que auditar un número.

**Alternativas:** dejar los acumuladores como fuente y agregar un registro paralelo sólo para auditar (dos verdades que se van a separar en la primera distracción); registrar sin idempotencia y confiar en que cada devengo se llame una vez (es justo lo que no se puede verificar); hacer del registro un tipo de agente (no hay presupuesto en PLE); escribir el csv siempre (multiplica por cien el peso de una corrida del barrido para un dato que casi nunca se lee).

**Consecuencias:** el total dejó de poder cerrar consigo mismo, y eso encontró un error real en el primer intento: la clave de idempotencia del almacenaje era `día|lote|ubicación|producto`, que no distingue dos capas del mismo lote en el mismo depósito, así que sólo la primera capa de cada lote pagaba y el costo caía a un tercio. La capa pasó a tener identidad propia (`Capa.idCapa`, asignada por `Inventario`) y la reconciliación volvió a cerrar. La contrapartida es costo de memoria: los cargos se guardan en una lista por corrida.

## ADR-053 — Cada circuito paga lo que físicamente ocurre, y el modelo lo audita envío por envío

**Estado:** aceptada.  
**Fecha:** 2026-07-28  
**Contexto:** con C1 y C2 las tarifas ya tenían unidad, proveedor y vigencia, y el total salía del registro, pero seis conceptos estaban en 0 y no se devengaban: IN, OUT, THC, costo terminal, despachante y espera. El costo por tonelada no era comparable contra una cotización real, y no había forma de verificar que un circuito no pagara cargos de otro: los cuatro circuitos existen físicamente desde ADR-050, pero nada impedía cobrarle un round trip al circuito de terminal, que no usa portacontenedor.

**Decisión:** el cargo se devenga en el evento físico que lo genera, y el modelo verifica en cada envío que lo devengado sea exactamente lo que el circuito debe pagar.

1. **Devengo en el evento físico:** IN cuando el producto entra al almacenamiento, almacenaje una vez por día sobre el stock físico, OUT cuando sale, flete cuando el viaje se ejecuta, consolidación o cross dock en el sitio donde se arma el contenedor, THC y costo terminal cuando el contenedor cargado entra a la terminal —en el circuito de terminal, cuando se arma ahí, con el día guardado en `Envio.diaCargosTerminal`—, despachante por contenedor o por pedido según la unidad, espera sólo por las horas que superan la franquicia y round trip al completar el ciclo.
2. **Auditoría por circuito:** `Main.costoEsperadoCircuito(envio)` reconstruye el importe desde las tarifas, sin mirar el registro, y `finalizarEnvio()` aborta la corrida si difiere de lo devengado. Es la única forma de garantizar que un circuito no cobre lo que no le corresponde, y es lo que ejecuta los casos V-COST-01 a V-COST-05 y V-COST-07 en cada corrida.
3. **Lo que un circuito no paga, no se registra:** el de terminal no paga round trip (`usaPortacontenedor()` es falso) y paga el flete a granel; el que sale del frío propio no paga IN, almacenaje de terceros ni OUT (`pagaOutDeposito()` exige que el origen sea un depósito); lo que cruza en cross dock no paga IN ni OUT ni almacenaje en el sitio de cross dock, y sí los paga si el cross dock se degrada y la mercadería queda como stock.
4. **Vistas separadas:** `costoEndToEndPedido`, `costoIncrementalPedido` y `costoHistoricoPedido` sobre el registro. La comparación entre alternativas usa la **incremental**: el almacenaje y el flete ya incurridos son costo hundido y no pueden decidir dónde consolidar.
5. **Valores de referencia marcados como supuesto:** IN y OUT por tonelada (2,0–3,0 USD/tn según producto), THC (150–220 USD/contenedor), costo terminal (70–90) y despachante (120) entran con proveedor `SUPUESTO_C3` en lugar de quedar en 0, para que el barrido y los casos V-COST midan algo. Se reemplazan en el Excel sin tocar código.

**Alternativas:** dejar los seis conceptos en 0 con la estructura lista (el modelo compila y no cambia nada, pero el USD/tn sigue sin ser comparable y los casos V-COST no miden); devengar todo al cierre del envío (más simple, pero entonces el almacenaje de un lote no despachado no existiría y la vista incremental no podría separar el costo hundido); confiar en la reconciliación por categoría sin auditar por envío (detecta un total mal sumado, no un cargo puesto en el circuito equivocado); cobrar THC y costo terminal por tonelada (no es el contrato: hoy son por contenedor).

**Consecuencias:** el costo de campaña **sube** entre 11,8 % y 37,2 % según escenario y `fase-21` deja de ser comparable en costo, aunque sigue siendo la línea de base física: los KPIs físicos y de servicio quedaron idénticos fila por fila en las 420 corridas. Las utilizaciones de flota y pool se mueven sólo en E-05 (≤ 0,0007) porque la elección del sitio de cross dock se hace por costo estimado y ese estimado ahora incluye los cargos nuevos. `costo_espera_usd` es 0 en las 420 corridas: con las velocidades sintéticas la carga tarda menos de una hora y la franquicia de 3 h no se supera nunca; el concepto está devengado y se activa con los tiempos reales. La transferencia depósito→depósito queda pendiente por decisión del usuario, así que V-COST-06 se documenta pero no se ejercita.

## ADR-054 — El circuito de cada pedido lo decide un evaluador de alternativas, y la comparación es por costo incremental

**Estado:** aceptada.  
**Fecha:** 2026-07-29  
**Contexto:** `PlanLogistico` existía completo desde el inicio del proyecto —estrategia, origen, factibilidad, costo histórico, incremental y end-to-end— y tenía **cero referencias** en el modelo. El circuito de cada pedido salía de `estrategia_consolidacion`, un parámetro global del escenario: todos los pedidos de una corrida hacían lo mismo, aunque el stock estuviera en la planta y el depósito asignado quedara del otro lado. Con ADR-050 los cuatro circuitos existen físicamente y con ADR-053 cada uno paga lo que ocurre, así que por primera vez hay con qué discriminar.

**Decisión:** cuando la política del escenario es económica, cada pedido se asigna con un plan que compara alternativas reales y ejecuta la elegida con el flujo físico que ya existe.

1. **La política es un dato del escenario**, `politica_seleccion`. Las `FIJA_*` y `MANUAL` reproducen la conducta anterior al evaluador y quedan como regresión; `PRIORIDAD_FRIO_PROPIO`, `MENOR_COSTO_INCREMENTAL_FACTIBLE` y `MENOR_COSTO_END_TO_END_FACTIBLE` generan y comparan alternativas.
2. **Las alternativas salen del stock real**, no de una lista fija: por cada ubicación que tiene stock libre del producto se genera el circuito de consolidación en el sitio, el cross dock por cada depósito habilitado y el circuito de terminal. La transferencia depósito→depósito se genera **descartada**, con el motivo escrito (`sin movimiento fisico en el modelo (C7)`), para que la decisión muestre que se consideró.
3. **Factibilidad antes que costo, y con motivo.** Stock libre, habilitación del depósito, cupo de cross dock, espacio de paso, flota de producto y capacidad de estiba se verifican **sin mutar inventario**: evaluar no reserva. Una alternativa no factible no compite y su motivo queda en el plan.
4. **La comparación es por costo incremental** (`MENOR_COSTO_INCREMENTAL_FACTIBLE`): flete, round trip, estiba, OUT, THC, terminal y despachante. El IN, el almacenaje ya devengado y el flete con el que el producto llegó a donde está son **costo hundido** y se reportan aparte, en la vista end-to-end. La política end-to-end existe para poder medir qué pasa cuando el hundido decide, no porque sea la correcta.
5. **El servicio manda sobre el costo:** ninguna diferencia de precio compra una entrega tarde mientras exista una alternativa que llega a tiempo. Si ninguna llega, se elige la mejor tardía y el plan queda marcado (`planes_tardios`).
6. **El evaluador no mueve producto.** `ejecutarAlternativa()` reserva contra el origen elegido o llama al cross dock que ya existía: si el flujo no puede tomarla, la alternativa se marca `el flujo no pudo tomarla al ejecutar` y el pedido queda pendiente. No hay un segundo camino físico que pueda prometer algo que la cadena no hace.
7. **El orden es determinístico:** servicio, criterio de costo y desempate por clave de alternativa, para que dos corridas con la misma semilla decidan igual.
8. **Los factores de sensibilidad tarifaria valen donde se devenga**, no sólo donde se cotiza: el devengo pasa a leer los mismos accesores que la auditoría (`storageUsdTnDia`, `fleteTarifaUnitaria`, `roundTripUsdContenedor`, `crossDockTarifa`, `thcUsdContenedor`, `costoTerminalUsdContenedor`, `despachanteTarifa`).

**Alternativas:** dejar `PlanLogistico` muerto y elegir por una heurística cableada (es el estado que se corrige: el escenario no puede comparar estrategias); decidir por costo end-to-end (el hundido no es evitable y contamina la decisión: se implementa como escenario, no como default); reservar el inventario durante la evaluación para simplificar la factibilidad (una alternativa descartada se llevaría stock que otra necesita); elegir por costo y verificar servicio después (deja entregas tarde que eran evitables); hacer del evaluador un tipo de agente (no hay presupuesto en PLE: el modelo está en 10 de 10, y `AlternativaCircuito` va como clase Java plana).

**Consecuencias:** en el barrido de 1 080 corridas, decidir por costo incremental (E-14) sale **9,11 USD/tn más barato que E-00 con la misma demanda servida, y lo hace en las 30 réplicas** (−6,2 %, desvío del pareado 1,47) subiendo el nivel de servicio de 0,987 a 1,000. El reparto lo explica: el circuito deja de ser único y quedan 267 contenedores desde planta, 214 desde depósito y 49 armados en terminal, con el flete de producto un 24 % abajo y el OUT un 51 %. Decidir por end-to-end (E-15) es **peor**: sale 7,44 USD/tn más barato que E-00 pero pierde 7,8 puntos de servicio, porque el almacenaje ya pagado empuja a despachar desde donde está el stock aunque el circuito no llegue a tiempo; coincide con `PRIORIDAD_FRIO_PROPIO` (E-16) en 29 de 30 réplicas, que es la lectura de que el hundido, en la práctica, es una regla de "no muevas nada". El cross dock **nunca gana**: E-14 y E-17 (mismo escenario sin cross dock) dan idéntico en las 30 réplicas. Los 14 escenarios de política fija reproducen `fase-22` fila por fila en las 420 corridas y las 37 columnas comunes, y en todos ellos los cinco KPIs de decisión son 0. El punto 8 encontró un error real: con cualquier factor distinto de 1 la corrida abortaba en la auditoría por envío (E-20: round trip devengado 390 contra 312 cotizado), es decir que los ocho escenarios de sensibilidad tarifaria no habrían medido nada.

## ADR-055 — Un pedido se cubre con varias asignaciones de origen, y el saldo sigue siendo demanda

**Estado:** aceptada.  
**Fecha:** 2026-07-30  
**Contexto:** la reserva era todo o nada y contra un solo sitio. `reservarLotesParaPedido()` recorría las capas de una ubicación y, si el stock libre no alcanzaba para el pedido completo, **liberaba lo que había tomado** y dejaba el pedido `PENDIENTE`. La clave de reserva era el `codigoPedido`, así que dos intentos del mismo pedido en el mismo sitio no eran distinguibles y `esCrossDock` era un atributo del pedido: un pedido se cruzaba entero o no se cruzaba. Con el Excel real eso deja de ser una limitación teórica: en `CapacidadUbicacion` la CASCARA tiene capacidad concentrada en un solo depósito y el ACEITE en dos con pocas toneladas, así que hay pedidos globalmente factibles —hay stock en la red— que se rechazaban todos los días por no caber en ningún sitio individual.

**Decisión:** la unidad de compromiso pasa a ser la **asignación**, no el pedido.

1. **`AsignacionPedido` es una clase Java plana serializable**, no un tipo de agente: el modelo está en 10 de 10 de PLE. Tiene identidad (`idAsignacion`), origen, circuito, si cruza, y el ciclo de vida en toneladas: asignadas, reserva activa, contenerizadas, despachadas y entregadas, más los días de asignación, primer despacho y última entrega.
2. **La clave de reserva es `codigoPedido + "|" + idAsignacion`.** Es lo que permite dos fracciones del mismo pedido en el mismo sitio sin pisarse, y lo que hace que el despacho consuma exactamente la reserva que le corresponde. El `codigoPedido` **se conserva** en la reserva de la capa, porque hay reglas que son del pedido y no de la fracción: el cross dock que no paga almacenaje es una de ellas.
3. **`reservarParcialPedido()` conserva lo que consiguió.** Reserva `min(objetivo, pendiente del pedido, stock libre del sitio)` y no vuelve atrás porque falte el resto. Devuelve las toneladas realmente reservadas.
4. **`asignarParcialPedido()` recorre orígenes hasta cubrir el pedido o agotar candidatos**, y el saldo queda como demanda de los días siguientes. Las políticas `FIJA_*` conservan su orden de candidatos: lo único que cambia es que aceptan lo que cada uno pueda dar. No se convierten en políticas económicas.
5. **La fuente de verdad son las asignaciones.** Los campos legacy del pedido (`toneladasReservadas`, `idSitioOrigen`, `esCrossDock`) se mantienen para compatibilidad de pantalla, pero las funciones derivadas —`toneladasAsignadasAcumuladas()`, `toneladasReservadasActivas()`, `toneladasEnProceso()`, `toneladasPendientesAsignar()`, `toneladasPendientesEntregar()`— se calculan sobre la colección.
6. **La contenedorización es progresiva y el último parcial es condicionado.** `crearContenedoresParaAsignacion()` crea contenedores **completos** con el volumen disponible de la asignación, y el último parcial sólo cuando el pedido está completamente asignado, venció su fecha límite o terminó la campaña. Que una asignación tenga un resto no alcanza: el resto puede venir de otro origen mañana, y en el contrato un contenedor parcial paga como uno lleno (ADR-053).
7. **`ENTREGADO` sigue exigiendo el total.** `EN_PREPARACION` pasa a ser un estado que admite fracciones, `ATRASADO` puede venir de `PENDIENTE`, `RESERVADO` o `EN_PREPARACION`, y un pedido atrasado **conserva** lo entregado, sus asignaciones activas y el origen de cada fracción.
8. **La identidad del pedido se valida todos los días (C-01):** `solicitado = pendiente de asignar + reserva activa + despachado no entregado + entregado`, con tolerancia 0,0001 tn. `validarBalanceProducido()` agrega C-02 sobre la producción acumulada de la planta.

**Alternativas:** hacer de `AsignacionPedido` un tipo de agente (no hay presupuesto en PLE); mantener el `codigoPedido` como única clave de reserva y distinguir las fracciones por sitio (dos asignaciones en el mismo sitio, que es exactamente el caso del pedido que se completa en dos días, quedarían indistinguibles); fragmentar los pedidos del Excel en pedidos chicos que sí quepan (esconde el problema en los datos y rompe la trazabilidad comercial y la regla de contenedor completo); crear el último contenedor parcial en cuanto una asignación tiene un resto (despacharía a medio llenar pagando contenedor completo, y el resto que llegaba mañana viajaría en otro contenedor igual de caro); dejar la reserva parcial sólo para las políticas económicas (las políticas fijas seguirían rechazando pedidos factibles, y la comparación entre políticas mediría dos cosas distintas).

**Consecuencias:** los pedidos que antes se rechazaban por concentración de capacidad ahora se sirven desde varios orígenes, así que **los KPIs se mueven en todos los escenarios** y `fase-23` deja de ser línea de base: sirve para explicar los movimientos, no para exigir igualdad. Aparece trazabilidad que antes no existía —origen de cada fracción, día de primer despacho y de última entrega por asignación— y con ella los KPIs de pedidos parciales y multi-origen. El almacenaje pasa a descontarse por clave de reserva y no por pedido: un pedido con una fracción que cruza y otra almacenada paga por la segunda, que antes quedaba excluida por completo. Riesgo asumido: la reserva parcial retiene stock que otro pedido podría usar mejor; no hay reoptimización ni cancelación de asignaciones vivas.

## ADR-056 — La política FLEXIBLE incluye un componente preventivo alerta/objetivo, y la sobrecarga sólo cambia la prioridad del destino

**Estado:** aceptada.  
**Fecha:** 2026-07-30  
**Contexto:** con `FLEXIBLE`, la planta sacaba producto por dos motivos: desborde proyectado sobre la capacidad nominal y demanda pendiente que los depósitos no podían cubrir. Los umbrales de alerta y objetivo del escenario existían pero sólo alimentaban indicadores: la planta llegaba al 100 % antes de mover nada preventivamente. Y `transferirProductoADepositos()` cortaba el reparto con `break` en cuanto un destino recibía menos de lo pedido, así que un objetivo de 300 tn se detenía en 100 aunque hubiera espacio en otros depósitos. La pregunta abierta del requerimiento era si la sobrecarga crítica, además de reordenar destinos, debía aumentar el volumen transferido.

**Decisión:**

1. **Componente preventivo en FLEXIBLE:** si `stock físico + forecastProduccion(diasForecast) >= capacidad × umbral_alerta_pct/100`, se transfiere hasta bajar al `umbral_objetivo_pct`; debajo de la alerta, cero.
2. **Los tres motivos se combinan con `max`, no con suma**, y el total se limita al stock libre en planta: son tres lecturas del mismo stock y sumarlas movería dos veces las mismas toneladas.
3. **`toneladasASacarReactiva()` no se toca.** REACTIVA mira el stock de hoy, no el proyectado, y sigue siendo la línea de comparación.
4. **La sobrecarga crítica no agrega volumen; cambia la prioridad del destino.** Es demostrable con las validaciones del contrato: `umbral_sobrecarga_pct >= 100`, así que `proyectado − umbralSobrecarga <= proyectado − capacidad`, que ya es el componente de desborde. Lo que cambia es a dónde va: con la planta sobre el umbral, `seleccionarDeposito()` ordena por espacio disponible y usa el costo sólo como desempate.
5. **La producción no se bloquea nunca y no se pierde producto** (ADR-048 sigue vigente). La penalidad por tonelada-día sobre el nominal se sigue devengando en el costo económico: transferir con prioridad de espacio la reduce, no la elimina.
6. **El objetivo se reparte entre todos los destinos factibles.** Un destino que recibió menos de lo pedido queda excluido por hoy y el recorrido sigue con el siguiente, así que el ciclo termina siempre. Si al agotar los candidatos queda saldo, se cuenta en `transferencias_incompletas`.
7. **El descarte de un destino tiene motivo escrito**, con una única función (`motivoDescarteDeposito()`) que usan tanto la selección como el diagnóstico: `NO_HABILITADO`, `SIN_CAPACIDAD_PRODUCTO`, `SIN_ESPACIO`, `TARIFA_INEXISTENTE`, `TARIFA_SITIO_INEXISTENTE`, `SIN_FLOTA`.

**Alternativas:** sumar el volumen de sobrecarga al de desborde (contaría dos veces las mismas toneladas, con `umbral_sobrecarga_pct >= 100` validado); bloquear la producción o descartar el excedente sobre el umbral (contradice ADR-048 y esconde el faltante de frío, que es lo que el modelo tiene que dimensionar); modificar `toneladasASacarReactiva()` para que también prevenga (perdería la línea de comparación entre políticas, que es el punto del escenario E-12); mantener el reparto a un solo destino por día (con las capacidades concentradas del Excel real deja producto retenido en planta con espacio libre en la red); dejar el componente preventivo como una política nueva en vez de dentro de FLEXIBLE (multiplica los escenarios sin agregar una decisión: los umbrales ya son parámetros del escenario y con `umbral_alerta_pct = 100` el componente no actúa).

**Consecuencias:** con el componente preventivo la planta trabaja más vacía y el almacenaje de terceros sube, que es el intercambio explícito de la decisión: se paga depósito para no llegar al desborde. Los KPIs de transferencia se abren por motivo —preventiva, desborde, servicio, crítica— así que el costo de almacenaje adicional es atribuible. `fase-23` no es comparable en costo. Con `umbral_alerta_pct = 100` el componente preventivo se apaga y la conducta anterior se recupera sin tocar código, lo que hace que la decisión sea un parámetro del escenario y no una regla del modelo.

## ADR-057 — Stock inicial como capas de inventario preexistentes

**Estado:** aceptada  
**Fecha:** 2026-08-04  
**Contexto:** la campaña arrancaba siempre con inventario cero, así que la disponibilidad era únicamente la producción de los días simulados. En la realidad la campaña arranca con producto ya elaborado y ya ubicado, en la cámara propia y en depósitos de terceros, y ese stock es el que atiende los primeros pedidos: sin él el dimensionamiento sobreestima el faltante inicial y no se puede reproducir un estado real ni arrancar una corrida a mitad de campaña. El contrato de datos ya proyectaba una tabla `LoteInicial` que nunca se implementó, y el requerimiento pedía explícitamente que el stock inicial **no** se cargue como producción ficticia, transferencia ficticia, pedido especial ni ajuste directo de las variables de `Planta` o `Deposito`.

**Decisión:**

1. **El stock inicial existe como capas reales de `Inventario`.** Se carga con `inventario.ingresar(...)`, la misma función que usa la producción, así que el FIFO, las reservas, las reservas parciales, el cross dock, el almacenaje y las conciliaciones lo ven sin ninguna rama especial. No hay saldo paralelo ni agente nuevo: el modelo sigue en 10 de 10 tipos de agente de PLE.
2. **Contrato de datos: hoja `StockInicial` opcional**, con una fila por lote-ubicación (`id_stock`, `codigo_lote`, `producto`, `id_ubicacion`, `toneladas`, `dia_produccion`, `dia_ingreso`, `cliente`, `calidad`). Reemplaza a la `LoteInicial` proyectada. Es opcional porque una hoja faltante aborta el arranque (ADR-037) y hacerla obligatoria dejaría fuera de servicio a todos los libros anteriores; ausente equivale a inventario inicial cero, y la plantilla generada la trae con encabezados.
3. **Identidad del lote histórico:** `codigo_lote + producto + cliente + calidad`. Un mismo `codigo_lote` puede estar en varias ubicaciones, y esas filas son capas del mismo lote. Los lotes iniciales **no** pasan por `buscarLoteComercialAbierto()` ni por `crearLoteEnPlanta()`: quedan con `esStockInicial = true`, `codigoLoteExterno`, `estadoComercial = CERRADO` y `toneladasObjetivo = 0`, así que no reciben producción de campaña ni se cierran por objetivo.
4. **`toneladasIniciales` de un lote inicial es el total histórico cargado**, no producción de campaña. Sigue siendo el acumulador monótono de ADR-047; el origen se distingue con `esStockInicial`.
5. **Fechas negativas:** `dia_produccion <= 0`, `dia_ingreso <= 0` y `dia_ingreso >= dia_produccion`. El FIFO ordena por `dia_ingreso`, después `dia_produccion` y después `idLote`, así que el stock histórico sale antes que la producción nueva sin ninguna regla adicional.
6. **Ningún costo pasado se devenga ni se imputa.** La carga no registra flete, IN, almacenaje anterior al día 0, round trip, OUT ni consolidación. Y la antigüedad de una capa se cuenta **dentro del horizonte**: `toneladaDiaEnStock()` pasa a usar `time() − max(0, capa.diaIngreso)`, porque con `dia_ingreso = -60` el evaluador arrancaba imputando 60 tn-día de almacenaje anterior a la campaña, un costo hundido que hacía que la política end-to-end evitara el stock inicial. Los costos futuros sí se cobran normalmente: almacenaje desde el día 0, OUT, consolidación, round trip, THC, terminal, despachante y esperas; oportunidad de frío propio y penalidad de sobrecarga para el stock que arranca en planta.
7. **Validación temprana con el criterio de ADR-037:** identidad (`id_stock` no vacío y único, `codigo_lote` no vacío, producto/cliente/calidad consistentes por lote), ubicación (existe, habilitada, `PLANTA` o `DEPOSITO`, nunca `TERMINAL`, con capacidad para el producto), cantidad (`> 0`) y fechas se validan en `DatosEntrada.validar()`; la **capacidad efectiva** se valida en `Main.validarStockInicial()`, que corre con los factores del escenario ya aplicados a los agentes. Se reúnen todos los errores y el arranque se detiene; no hay defaults silenciosos.
8. **Capacidad: dura en depósito, advertencia en planta.** El depósito es de terceros y su capacidad es una restricción contractual. La planta es frío propio y su capacidad nominal es un umbral de lectura, no un tope (ADR-048): arrancar por encima del nominal es un dato real, se avisa por consola y la sobrecarga se mide con los indicadores que ya existen. Abortar la escondería.
9. **C-02 pasa a ser la identidad de disponibilidad:** `stock inicial + producción = stock físico + en proceso + entregado`, con tolerancia 0,001 tn. Sin este cambio la conciliación abortaba el día 0 en cuanto había stock inicial, porque comparaba el inventario contra la producción de planta.
10. **La carga se invoca en el arranque del agente `Main`**, inmediatamente después de `cargarDatosEntrada()` y antes del primer `pasoDiario_accion()`, así que el stock está disponible para los pedidos del día 0. El punto de invocación vive en el `StartupCode` del `.alp`, no en el espejo `model_src/`.

**Alternativas:** cargar el stock como producción del día 0 (contamina la producción de campaña, los KPIs de planta y C-02, y le da fecha 0 a producto viejo, así que el FIFO lo sacaría después de la producción nueva); cargarlo como transferencias ficticias planta→depósito (devenga flete e IN de un movimiento que ya ocurrió y se pagó); mantener un saldo inicial paralelo en `Planta`/`Deposito` (dos fuentes de verdad, contra ADR-021/023, y el FIFO y las reservas no lo verían); hacer obligatoria la hoja `StockInicial` (rompe los libros existentes); tratar la capacidad de planta como error duro (contradice ADR-048 y esconde un caso real); dejar `toneladaDiaEnStock()` con antigüedad absoluta (imputa almacenaje anterior al horizonte y sesga la política end-to-end contra el stock inicial).

**Consecuencias:** la disponibilidad de campaña pasa a ser stock inicial más producción, y siete KPIs nuevos la hacen legible (`stock_inicial_tn`, `stock_inicial_consumido_tn`, `stock_inicial_remanente_tn`, `produccion_campania_tn`, `disponibilidad_total_tn`, `demanda_planificada_tn`, `deficit_estructural_tn`), con el déficit estructural calculado sobre los datos de entrada: si la demanda supera stock inicial más producción planificada, ningún dimensionamiento llega al 100 % de servicio y el modelo lo dice antes de correr. Con la hoja ausente o vacía el barrido sintético da idéntico a `fase-24` en las 55 columnas anteriores, así que la regresión es verificable. El único cambio que afecta a las corridas sin stock inicial es el acotamiento de la antigüedad, que con `dia_ingreso >= 0` es la identidad. El CSV pasa a `fase-25`.

## ADR-058 — La hoja de stock inicial se lee tolerante al formato, no al contenido

**Estado:** aceptada  
**Fecha:** 2026-07-24  
**Contexto:** todas las hojas del libro de entrada salen del volcado del propio modelo (`tools/generar_excel_ejemplo.py` corre `GeneradorSintetico`), así que sus nombres de hoja y de columna son exactos por construcción. La hoja de stock inicial es la excepción: es un relevamiento de existencias que escribe una persona, y en la práctica llega con la grilla `producto × depósito` que se usa para contar —tres columnas, sin fechas ni identidad de lote—, con el nombre de hoja tipeado con una errata (`StockcInicial`), con encabezados del negocio (`DEPOSITO`, `Inicial`) y con los depósitos escritos como se los nombra (`RUTA 9`, `DODERO BARRACAS`, `NORRY `). Con la lectura estricta de ADR-038 eso equivale a "no hay stock inicial", y como la hoja es opcional (ADR-057) el modelo arranca en cero **sin avisar nada**: el silencio más caro posible, porque el dimensionamiento sale mal y nada falla.

**Decisión:** sólo para esta hoja, `ImportadorExcel.leerStockInicial()` tolera el **formato** y sigue siendo estricto con el **contenido**.

1. **Nombre de hoja** por coincidencia normalizada: cualquier hoja cuyo nombre en mayúsculas, sin acentos ni separadores, empiece con `STOCK` y contenga `INICIAL`.
2. **Encabezados con alias**, comparados normalizados: la ubicación acepta `id_ubicacion`, `ubicacion`, `deposito`, `sitio` o `lugar`; las toneladas aceptan `toneladas`, `inicial`, `stock`, `stock_inicial`, `stock_tn` o `tn`.
3. **Columnas opcionales con default explícito**: sin `id_escenario` todas las filas aplican al escenario que se importa; sin `id_stock` la fila se identifica por su número; sin `codigo_lote` **la fila es el lote** (dos filas del mismo producto y depósito son dos partidas, no una); sin `cliente` o `calidad` se usan los defaults del escenario; sin fechas el stock se fecha en el día 0.
4. **Ubicaciones escritas como en el negocio**: id exacto y, si no coincide, el único id que sea prefijo del nombre comercial. Si hay más de un candidato se informa la ambigüedad con la lista.
5. **Una fila en cero se ignora**: la grilla lista todas las combinaciones y la mayoría está vacía; cero es ausencia de stock, no un dato inválido.
6. **Nada se adivina**: producto, ubicación y toneladas siguen siendo obligatorios, y un encabezado mínimo faltante, un producto desconocido, una ubicación que no resuelve o un texto que no es número son errores de datos que abortan el arranque con la hoja y la fila (ADR-037).

**Alternativas:** exigir el formato canónico y documentarlo (es lo que hacía la primera versión: el libro real cargaba cero en silencio, y pedirle a una persona que reescriba un relevamiento a otro formato en cada actualización garantiza que alguna vez se cargue mal); convertir el libro con un script externo (agrega un paso manual entre el dato y el modelo, y el `.xlsx` del repositorio deja de ser la fuente); extender la tolerancia a todas las hojas (las demás las escribe el modelo, así que la tolerancia sólo taparía errores de las herramientas); inferir fechas históricas para dar antigüedad (inventa datos que después se leen como reales).

**Consecuencias:** el libro que mantiene el negocio se puede cargar sin reformatear y sin tocar el modelo, y la forma agregada `producto × depósito` es un caso soportado del contrato (§6.4). Sin `dia_ingreso` no hay antigüedad que imputar y el stock queda fechado el día 0; el FIFO igual lo saca antes que la producción de campaña, porque los lotes iniciales se crean primero y el desempate final es por `idLote`. Como contrapartida, un depósito nuevo cuyo nombre comercial no empiece con su id no resuelve por prefijo y hay que escribir el id o agregar la columna `id_ubicacion`: el error lo dice con la lista de ids válidos. La tolerancia queda acotada a esta hoja para no debilitar ADR-038.

## ADR-059 — La ventana marítima separa conocer el pedido de poder ejecutarlo

**Estado:** aceptada  
**Fecha:** 2026-07-24  
**Contexto:** el pedido tenía dos fechas, `dia_llegada` y `dia_limite`, y con ellas el modelo hacía todo: el pedido nacía el día de llegada y desde ese mismo día podía reservar, transferir, retirar el vacío y consolidar. En la operación real esas son cuatro fechas distintas y el orden importa: el exportador **conoce** el pedido semanas antes, la naviera **abre el retiro del vacío** unos días antes del buque, la terminal tiene un **cut-off físico** para recibir el contenedor cargado y el buque tiene un **ETD**. Con una sola fecha el modelo permitía empezar la ejecución física apenas se conocía el pedido, que es justo lo que la operación no puede hacer, y medía el servicio contra una fecha que no es la que compromete a nadie.

**Decisión:** el pedido lleva cuatro fechas —`dia_conocimiento`, `dia_apertura_retiro_vacio`, `dia_cutoff_fisico`, `dia_etd`— con la invariante `dia_conocimiento <= dia_apertura_retiro_vacio <= dia_cutoff_fisico <= dia_etd`, validada como error de datos.

1. **El pedido nace en `dia_conocimiento`.** `pedidosDelDia()` compara contra esa fecha, así que desde ese día el pedido reserva inventario, se asigna, cuenta para las transferencias preventivas y planifica posiciones. Conocer no es ejecutar.
2. **La ejecución física se abre en `dia_apertura_retiro_vacio`.** El contenedor comprometido antes de la apertura existe en estado `CREADO` y no entra al flujo: `actualizarVentanasRetiroDelDia()` —paso 2b del día, inmediatamente después de registrar los pedidos— lo pasa a `ESPERANDO_PROGRAMACION` el día que la ventana abre. **No se agrega ningún estado de contenedor**: `CREADO` ya existía y significaba exactamente eso.
3. **El servicio se mide contra `dia_cutoff_fisico`**, no contra el ETD. `fechaLimiteTerminal` y `diaLimite` del pedido son el cut-off, así que `registrarAtrasos()` no cambia una línea. `dia_etd` queda como dato marítimo y como agrupador junto con `buque` y `viaje_buque`.
4. **Perder el cut-off se registra, no cancela.** `registrarPerdidaDeCutoff()` —paso 11b, después de `registrarAtrasos()`— marca el pedido, cuenta la pérdida y, con la política por defecto `CONTINUAR`, deja que el saldo siga hasta entregarse tarde: es el roll-over al buque siguiente, sin implementar todavía el calendario real de buques. `CANCELAR` es la política dura y da de baja el saldo.
5. **La holgura se mide una sola vez**, el día en que la ventana abre: `dia_cutoff_fisico − hoy − tiempoLogisticoMinimoDias(pedido)`. Es la pregunta de dimensionamiento —¿la ventana alcanza para el circuito que este pedido puede usar?— y no un indicador que cambia con el reloj. Una holgura negativa es una ventana inviable y se informa antes del cut-off.
6. **La capacidad futura se reserva sin restringir el presente.** `planificarPosicionFutura()` busca un día con posición libre dentro de la ventana contra la capacidad declarada del sitio; si ningún día de la ventana tiene lugar, lo cuenta como contenedor sin posición futura. La ejecución sigue decidiéndose día a día con la capacidad real: la reserva **avisa**, no bloquea.
7. **Los permisos son del escenario**: `permite_reserva_antes_retiro` y `permite_transferencia_antes_retiro` gobiernan si el pedido conocido puede comprometer inventario y si su demanda cuenta para la transferencia preventiva; `permite_reserva_capacidad_futura` habilita el punto 6. Los tres vienen en `true`, que es la conducta que el MOD pide; en `false` reproducen el modelo estrecho de "no existe hasta que abre".

**Alternativas:** agregar estados de contenedor (`PLANIFICADO`, `PERDIO_CUTOFF`): duplican información que ya está en `CREADO` y en el pedido, y obligan a tocar todas las transiciones; medir el servicio contra el ETD (el compromiso operativo es entrar a la terminal antes del cut-off; el ETD lo mueve la naviera y contaminaría el indicador con algo que el dimensionamiento no controla); filtrar `demandaProyectada()` por la apertura del retiro (posicionar producto lleva días de flota; esperar a la apertura para transferir es exactamente lo que hace perder el cut-off); cancelar el pedido al perder el cut-off (esconde la tonelada en vez de mostrarla tarde, y borra la demanda que el buque siguiente sí va a tener); derivar las cuatro fechas dentro del modelo en vez de pedirlas en el libro (las vuelve un supuesto invisible: la ventana es un dato comercial de la naviera).

**Consecuencias:** el libro de entrada declara la ventana completa y el modelo deja de suponer que un pedido conocido puede ejecutarse hoy. Nueve KPIs nuevos hacen legible la ventana (`servicio_toneladas_cutoff`, `toneladas_dentro_cutoff`, `toneladas_fuera_cutoff`, `pedidos_perdieron_cutoff`, `buques_cumplidos`, `buques_perdidos`, `holgura_promedio_dias`, `pedidos_ventana_inviable`, `contenedores_sin_posicion_futura`) y el tablero suma el panel *Ventana marítima y cut-off*. El servicio se mide además **por tonelada**, porque un pedido entregado a medias no es un pedido servido pero las toneladas que llegaron al buque tampoco desaparecen. Los KPIs se mueven en **todos** los escenarios por diseño: la ejecución que antes empezaba el día de llegada ahora arranca cuando abre el retiro, y la campaña se comprime contra el cut-off. Los libros con `dia_llegada`/`dia_limite` siguen cargando con la derivación documentada en el contrato §6.3, que nunca inventa un conocimiento anterior al que el libro declara. El CSV pasa a `fase-26`. Queda **fuera de alcance**, declarado: calendario real de buques, roll-over automático a un viaje concreto, demurrage y detention, cut-off documental, VGM, aduana y pre-gate.

## ADR-060 — La capacidad de posiciones se verifica antes de costear, y se reserva

**Estado:** aceptada  
**Fecha:** 2026-07-24  
**Contexto:** el evaluador de circuitos (ADR-054) descartaba una alternativa por stock, por flota y por cupo de cross dock **de hoy**, pero nunca miraba las posiciones de consolidacion de la ventana. La capacidad de consolidacion era diagnostica en dos sentidos: `planificarPosicionFutura()` (ADR-059) buscaba un dia libre **despues** de crear el contenedor y, si no lo encontraba, solo incrementaba un contador; y `tomarPosicionConsolidacion()` recien resolvia el cupo el dia de la operacion. El resultado es el que describe el MOD: el modelo elegia el sitio mas barato, comprometia el pedido entero y despues el sitio acumulaba backlog. Con T4 en 2 contenedores/dia eso es un plan que no existe.

**Decision:** la capacidad es una restriccion del plan y se resuelve **antes** del costo, en este orden: `capacidad → factibilidad → costo → reserva → asignacion`. No se elige primero por costo para validar capacidad despues.

1. **Una agenda por `(recurso, sitio, dia)`.** `ReservaCapacidad` es la posicion comprometida y `ocupacionCapacidad` la ocupacion diaria. Los recursos son dos y no se mezclan: `CONSOLIDACION` y `CROSS_DOCK`. `contenedores_por_dia` se interpreta como **capacidad diaria total procesable** del sitio, no como posiciones simultaneas, y asi queda documentado en el contrato.
2. **Una sola ocupacion por posicion.** Es el riesgo real de este cambio: `tomarPosicionConsolidacion()` ya consumia cupo al ejecutar, asi que si conviven reserva y conteo cada sitio pierde la mitad de su capacidad. Reservar ocupa; **consumir una reserva convierte reserva→consumo sin volver a ocupar**; liberar devuelve la posicion al cupo y deja la reserva en el registro con su motivo. `reconciliarCapacidad()` (C-03) verifica todos los dias que `activas + consumidas + liberadas` explique lo reservado y que ninguna ocupacion diaria supere el nominal del sitio.
3. **La alternativa se acota por capacidad antes de compararse.** `AlternativaCircuito` —y no `PlanLogistico`, que es la constancia de una decision ya tomada— lleva `capacidadReservable`, `contenedoresConCapacidad`, `toneladasCapacidadDisponible`, `diasCapacidadDisponibles` y el costo unitario sin restriccion. Las toneladas de una alternativa son `min(stock utilizable, contenedores con posicion x capacidad del contenedor, capacidad de transporte, saldo del pedido)`, de modo que la alternativa barata sin posiciones no gana el ranking: entra acotada o se descarta con `SIN_CAPACIDAD_ANTES_CUTOFF`.
4. **La reserva se toma al crear la asignacion, no al crear el contenedor.** Los contenedores se arman progresivamente y el ultimo parcial depende de `permitirUltimoParcial()` (ADR-055): reservando por contenedor sobra o falta reserva. Se reservan `ceil(tn / capacidad)` posiciones para la asignacion y cada contenedor consume una. **El contenedor parcial consume una posicion completa**, igual que paga contenedor completo (ADR-053).
5. **Nunca se reserva despues del cut-off.** La ventana de reserva es `[max(hoy, apertura del retiro), cut-off]` (ADR-059). La unica excepcion es el pedido que **ya** perdio el cut-off y sigue por politica `CONTINUAR`: si tampoco pudiera reservar, su saldo quedaria inmovilizado con el inventario tomado y nadie lo despacharia nunca.
6. **La prioridad del dia es la agenda.** Primero los contenedores con reserva para hoy, despues los que la perdieron y se pueden reprogramar, y por ultimo los que no tienen reserva, solo si sobra capacidad. Una posicion que no se uso el dia comprometido no se pierde ni bloquea el sitio: `reprogramarReservasCapacidad()` la mueve al proximo dia con lugar dentro de la ventana, y si no queda ninguno antes del cut-off la libera con motivo.
7. **Las politicas fijas tambien respetan la capacidad.** `FIJA_*` y `MANUAL` siguen siendo la conducta previa al evaluador, pero el circuito fijo se usa mientras sea factible. Si el saldo no entra, el escenario decide con `permite_fallback_politica_fija`: en `false` (default) el saldo queda sin cubrir y se ve; en `true` el saldo pasa al evaluador en vez de perderse.
8. **El interruptor de regresion ya existia.** Con `permite_reserva_capacidad_futura = false` no se crea ninguna reserva y el modelo vuelve exactamente a la conducta anterior. La reconciliacion sigue corriendo igual, porque la consolidacion sigue ocupando capacidad al ejecutar.

**Alternativas:** dejar la capacidad como criterio de desempate dentro del ranking (es lo que pide el MOD en su §7 y es justamente lo que hace que una alternativa infactible pueda ganar por costo); reservar al crear el contenedor (sobra o falta reserva por el parcial progresivo); un unico recurso para consolidacion y cross dock (el cross dock dejaria de tener techo propio y competiria por posiciones que no usa); resolver el reparto con un MILP (fuera de alcance: el modelo es de dimensionamiento y la decision es diaria); capacidad estocastica o negociacion dinamica de cupos (no hay dato que lo sostenga).

**Consecuencias:** la decision economica pasa a ser **factible por construccion** y el sobrecosto de la restriccion queda medido en vez de aparecer como backlog: `sobrecosto_saturacion_usd` es la diferencia entre lo que se pago y lo que habria costado la alternativa mas barata sin restriccion de capacidad. Tres CSV nuevos hacen auditable el reparto (`resultados/capacidad_por_dia.csv`, `resultados/asignaciones_capacidad.csv` y `resultados/asignaciones_capacidad_decisiones.csv`, este ultimo solo con `exportar_diagnostico_capacidad = true`) y el tablero suma el panel *Capacidad finita*. Capacidad finita **reparte, no crea**: no destraba la CASCARA hacia RUTA9, que es flota (ADR-044). No se agrego ningun estado de contenedor. Queda **fuera de alcance**, declarado: barrido de escenarios de capacidad, solver, movimiento deposito→deposito, pooling de terminales no equivalentes, capacidad estocastica, API de terminales y negociacion de tarifas.

## ADR-061 — La flota de producto son camiones discretos con viajes que pueden durar varios dias

**Estado:** aceptada
**Fecha:** 2026-07-24
**Contexto:** ADR-044 modelaba la flota de producto como **capacidad diaria agregada**: el dia ofrecia `camiones_producto` camion-dia, cada viaje consumia `2 x distancia / velocidad / horas_operativas_dia` y el movimiento planta→deposito era **instantaneo**. Eso tiene dos consecuencias que el MOD describe bien. La primera es que un viaje que no entra en una jornada **no puede empezar** cuando la flota es chica: con 3 camiones y 10 horas operativas, el tramo de 1 200 km hacia RUTA9 cuesta 3,43 camion-dia contra 3 ofrecidos por dia, asi que la CASCARA nunca sale de planta y el diagnostico contesta `SIN_FLOTA` todos los dias. La segunda es que, incluso cuando la flota alcanza, el producto **aparece en el deposito el mismo dia** en que se decide moverlo: no hay tiempo de viaje, no hay inventario en transito y no hay camion ocupado manana por lo que salio hoy.

Cinco supuestos del MOD no coinciden con el codigo y hay que decirlo antes de leer los KPIs: la transferencia planta→deposito **ya era parcial** (el todo-o-nada estaba en `flotaProductoAlcanza()`, que gobernaba el evaluador, el despacho a granel y la transferencia por servicio); el tramo hacia la terminal **ya tenia duracion fisica** en el flujo del envio; la agenda **no cambia el caudal** de la flota (con N camiones y ciclo T la capacidad diaria ya daba N/T viajes por dia), cambia el **tiempo** y la **indivisibilidad**; con el libro vigente (`camiones_producto = 500`, `horas_operativas_dia = 20`) la flota **no es el cuello de botella**, y el bloqueo de CASCARA que se habia reportado venia del libro anterior y del escenario sintetico de 3 camiones y 10 horas; y la flota sigue siendo **homogenea y con base en planta**, que queda declarado como supuesto y no como propiedad del mundo.

**Decision:** la flota de producto pasa a ser una **agenda de camiones fisicos**. Cada camion es una `UnidadFlotaProducto` con una unica fecha `disponibleDesde`, cada movimiento es uno o varios `ViajeProducto` de hasta `capacidad_camion_tn`, y el ciclo tiene tres momentos separados: **sale** (el producto deja el origen), **llega** (el producto entra al inventario del destino) y **regresa** (el camion vuelve a estar disponible). El reloj del modelo sigue siendo diario y no se agrego ningun agente de AnyLogic: los camiones y los viajes son clases Java embebidas.

1. **Una sola fuente de disponibilidad.** `disponibleDesde` por camion. No hay un segundo contador de flota ni una capacidad diaria paralela: `camionDiaViaje()` y `flotaProductoAlcanza()` quedan solo para el modo de regresion.
2. **Una sola fuente de compromiso de stock.** El viaje reserva con las **reservas de capa que ya existen** (ADR-023), con `clave = VIAJE|<id>` y el `codigo_pedido` del pedido cuando el viaje tiene dueno comercial. Una transferencia a deposito reserva sin pedido, porque mueve inventario propio: `Inventario.validar()` acepta la reserva de viaje sin pedido y sigue exigiendo que toda reserva tenga dueno.
3. **Una sola duracion por tramo.** `duracionIdaProductoDias()` + manipuleo + `duracionRetornoProductoDias()`, con la distancia de la tabla `Distancia`, la velocidad y la jornada del escenario, y las velocidades de carga y descarga declaradas de cada sitio —los mismos campos que usa `crearEnvio()`—. La tabla trae **un solo sentido por tramo**, asi que la lectura es simetrica (`distanciaKmSimetrica()`) y un tramo faltante da `RUTA_SIN_DISTANCIA` en vez de abortar la corrida.
4. **Un evento fechado hoy ocurre hoy.** Las fechas del viaje son fraccionarias aunque el paso sea diario: si un viaje sale, llega o vuelve dentro de la jornada, se procesa en el mismo paso. Sin esto las rutas de 50 km empeorarian un dia entero por redondeo y la regresion se volveria ilegible.
5. **La evaluacion no muta la agenda.** `evaluarDisponibilidadFlotaProducto()` simula sobre una **copia** de las fechas de disponibilidad y devuelve un `ResultadoDisponibilidadFlota` con toneladas y viajes programables, primera y ultima salida, ultima llegada, ultimo regreso, espera y motivo. `acotarAlternativaPorFlota()` acota las toneladas de la alternativa con eso, en lugar del si/no anterior: una alternativa que solo puede mover una parte **compite por esa parte**.
6. **Parcial antes que nada.** `programarMovimientoProducto()` parte el volumen en viajes de hasta un camion y programa los que puede. Mover cinco de ocho viajes es mejor que mover cero, y el saldo no programado queda contado (`toneladas_no_programadas_por_flota`) en vez de desaparecer.
7. **El deposito no se compromete dos veces.** El espacio disponible se lee con `espacioDisponibleEfectivo()`, que descuenta lo que ya viene en camino: el producto en transito todavia no esta en el stock, pero el lugar esta tomado.
8. **El flete se devenga al salir, una sola vez por viaje fisico.** Un viaje cancelado antes de salir no paga y libera stock y camion. El IN del deposito se devenga al **llegar**, y el que cruza no lo paga (ADR-053). El circuito 4 no se cobra dos veces: el viaje a terminal ocupa camion (`ocupaSoloFlota`) y el movimiento fisico y su costo siguen siendo del envio, que ya los tenia.
9. **C-04, reconciliacion de flota.** Todos los dias: ningun camion con dos viajes **superpuestos en el tiempo** (tener el viaje en curso y el siguiente ya programado para cuando vuelve es una agenda, no un error), `disponibleDesde` nunca anterior al ultimo regreso, ningun viaje sin carga o con mas de un camion, fechas coherentes (`salida <= llegada <= regreso`), nada que ingrese sin haber salido, nada que se complete sin haber ingresado, y los contadores de transito y de reservado iguales a la suma de los viajes.
10. **Interruptor de regresion.** `habilita_flota_producto_multidiaria = false` vuelve exactamente a ADR-044: capacidad diaria agregada, movimiento instantaneo y sin transito.

**Alternativas:** dividir `camionDiaViaje()` entre varios dias (no arregla la indivisibilidad ni da fechas de llegada); un `ResourcePool` de camiones de producto en el flujo (mezcla las dos flotas, que se miden distinto, y obliga a convertir la transferencia en un proceso de flowchart); un mapa Java de compromisos de stock aparte de las capas (segunda fuente de verdad frente a las reservas); pasar el modelo a paso horario (cambia todo el resto por un tramo); reservar flota al evaluar y confirmarla despues (deja al evaluador prometiendo camiones que otro pedido ya tomo); flota heterogenea o con bases distintas (no hay dato que lo sostenga).

**Consecuencias:** aparece **inventario en transito** como estado real del producto, la llegada al deposito deja de ser instantanea y la fecha estimada de entrega incluye la espera de flota, asi que los KPIs se mueven en todos los escenarios **por diseno**; el CSV pasa a `fase-27`. Con flota chica el efecto es **positivo**, porque los viajes que antes eran imposibles ahora se hacen: con 3 camiones y 10 horas la utilizacion de la flota pasa de 7 % a 91 % y el exportado de 7 436 a 7 536 tn. Con el libro vigente, donde la flota no es restrictiva, el resultado fisico es practicamente el mismo con la agenda prendida o apagada (989 contra 988 viajes planta→deposito y las mismas 23 417 tn transferidas), que es la regresion que se buscaba. Quedan **fuera de alcance**, declarados: flota heterogenea, bases distintas de planta, viaje con paradas multiples, retorno con carga, ventanas horarias de los sitios, mantenimiento y turnos de conductor, y el barrido de escenarios de flota.

## ADR-062 — El round trip del portacontenedor termina en la terminal

**Estado:** aceptada
**Fecha:** 2026-07-24
**Contexto:** ADR-050 dejó la cadena `descargarPuerto → retornarDeposito → liberarCamion`, es decir un **cuarto tramo** físico: el portacontenedor volvía vacío al origen después de haber entregado en terminal, y recién ahí se liberaba. El ciclo real es terminal → sitio de consolidación → terminal: el equipo se retira vacío en la terminal, va a cargar, vuelve cargado y queda disponible **en la terminal**, que es donde empieza el ciclo siguiente. El tramo de más alargaba el envío, la ocupación del pool y el atraso, y no correspondía a ningún movimiento del mundo. Con 1 200 km a 70 km/h eran 17,14 horas de más por contenedor.

Dos supuestos del MOD que originó el cambio no coinciden con el código y quedan aclarados: los ocho Delay del flujo **ya estaban en horas** (`HOUR`) y ninguno dividía por 24, así que no había nada que corregir ahí; y usar `diaLlegadaTerminal` como fecha de entrega —lo que el MOD pedía— haría que el ex post prometa **menos** que el evaluador, porque `horasCicloFisico()` ya cuenta la descarga en los circuitos 1 a 3 y además la consolidación en el circuito 4, donde el contenedor **todavía no existe** cuando llega el granel.

**Decisión:** el ciclo físico tiene tres tramos y termina en la terminal.

1. **Se elimina el tramo físico de regreso.** El objeto `retornarDeposito` y su conector salen del flowchart; la cadena queda `... → viajarPuerto → descargarPuerto → liberarCamion → salidaEnvios`. `envio.tiempoRetornoHoras = 0`: el campo sobrevive por compatibilidad de datos, no como Delay.
2. **El portacontenedor se libera al terminar la descarga.** El `ResourcePool` y los estados no cambian: lo que cambia es que la liberación ocurre en el instante en que el contenedor ingresó a terminal, no un viaje después.
3. **El round trip comercial no se toca.** `registrarRoundTrip()` sigue cobrando **una** tarifa por contenedor, que cubre terminal → origen → terminal. El cambio es físico, no tarifario, y por eso los costos de ciclo no se mueven.
4. **La fecha de servicio es física y única: `diaListoEnTerminal`.** Fin de `descargarPuerto` en los circuitos 1 a 3, fin de `consolidarCarga` en el circuito 4 —que es exactamente lo que el evaluador prometió ex ante—. Con ella se fijan `envio.diaEntrega`, `asignacion.diaUltimaEntrega`, el reparto de toneladas dentro y fuera del cut-off, `pedido.diaEntrega` y `pedido.diasAtraso`. `finalizarEnvio()` sigue siendo el único punto de cierre.
5. **El devengo de los cargos de cierre se separa del servicio.** `costoEsperadoCircuito()` derivaba la tarifa del cierre con `floor(envio.diaEntrega)` mientras el devengo usaba `diaCampania()`: al adelantar la fecha de servicio, cualquier cruce de medianoche hacía cortar la corrida en la auditoría por envío (`exigirIgual`). `envio.diaCargosCierre` guarda el día de campaña del registro —mismo patrón que `diaCargosTerminal`— y la auditoría cotiza contra él. El servicio es físico; el cargo es contable.

**Alternativas:** dejar `retornarDeposito` con duración cero (deja un bloque muerto en el flujo y sigue mostrando un tramo que no existe); liberar el portacontenedor en `descargarPuerto` pero conservar el viaje de regreso como ocupación de flota (no hay flota de portacontenedores por ruta: el pool es el recurso); usar `diaLlegadaTerminal` como fecha de entrega (contradice al evaluador y acredita el circuito 4 antes de que el contenedor exista); dividir `finalizarEnvio()` en cierre físico y cierre contable (dos puntos de cierre para una sola transición).

**Consecuencias:** el ciclo físico se acorta en un tramo completo y con él el atraso y la ocupación del pool; los KPIs de tiempo y servicio **mejoran por diseño** y no son comparables con corridas anteriores. Medido con libros mínimos deterministas: a 100 km y 50 km/h el envío pasa de 9 h a 7 h; a 1 200 km y 70 km/h, de 54,4 h a 37,3 h; y en el caso de dos contenedores por pedido con cut-off a dos días, las toneladas entregadas dentro del cut-off pasan de 0 a 144 de 288 (0 % → 50 % de servicio). Los costos no cambian: el round trip se sigue devengando una vez por contenedor. Queda **fuera de alcance**, declarado: el reposicionamiento de equipos vacíos entre terminales, el pool de contenedores por naviera y cualquier modelo de disponibilidad de vacíos.

## ADR-063 — La concurrencia del flujo la fijan los recursos, no la capacidad de un bloque

**Estado:** aceptada
**Fecha:** 2026-07-24
**Contexto:** con `datos/entrada_ejemplo.xlsx` la campana exportaba **13 749 de 30 656 tn** (servicio 25 %, atraso medio 46,2 dias) y el diagnostico por dato descartaba el techo estructural: cruzando la demanda contra la oferta acumulada **a la fecha de cada cut-off**, los tres productos dan 100 % entregable (JUGO 17 237 tn pedidas contra 20 770 disponibles, CASCARA 12 261 contra 13 920, ACEITE 1 250 contra 1 595) y el tablero informaba deficit estructural 0.

El flowchart mostraba la causa: **1 066 envios congelados dentro de `viajarVacioAlOrigen`** al cierre, con 1 069 de los 1 500 portacontenedores tomados. No estaban viajando: estaban bloqueados esperando entrar al bloque siguiente. Cuatro `Delay` habian quedado con la **capacidad por default de la libreria, que es 1**: `cargarCamion`, `viajarPuerto`, `descargarPuerto` y `consolidarCarga`. El que mordia era `viajarPuerto`: con capacidad 1 solo puede haber **un contenedor viajando a terminal en toda la red**, y como el tramo dura 17,14 h el techo es de **511 contenedores por campania**. La corrida entregaba 417 por esa rama mas 502 a granel: los 919 envios y las 13 749 tn del tablero.

Es la misma clase de error que la cola de envios que esperaban portacontenedor con la capacidad por default de 100, con una diferencia importante: aquella abortaba la corrida con un mensaje, esta **estrangula en silencio** y el resultado parece un problema de dimensionamiento.

**Decision:** ningun bloque del flujo puede limitar la concurrencia.

1. **`maximumCapacity = true` en los bloques fisicos.** `cargarCamion`, `viajarPuerto`, `descargarPuerto` y `consolidarCarga` pasan a capacidad ilimitada, igual que `colaCamiones`, `viajarVacioAlOrigen` y los tres bloques de granel, que ya la tenian.
2. **La concurrencia fisica la fijan los recursos, que ya estan modelados**: el `ResourcePool` de portacontenedores, las posiciones de consolidacion por sitio y dia (ADR-060), el cupo de cross dock, `contenedores_por_dia` de cada sitio y la flota de producto (ADR-061). Un `Delay` representa **duracion**, no capacidad; el que quiera limitar cuantas operaciones simultaneas hay tiene que hacerlo con un recurso, que es lo que se dimensiona y lo que se cobra.
3. **C-05, reconciliacion de envios en curso.** Cada envio declara en que bloque esta (`bloqueActual`), desde cuando (`diaEntradaBloque`) y cuanto deberia durar (`horasEsperadasBloque`). Todos los dias `reconciliarEnviosEnCurso()` verifica que ninguno lleve mas de su duracion fisica mas `toleranciaRetencionEnvioDias`, y aborta nombrando **el bloque que mas retiene**. La espera de un recurso finito no tiene techo (`horasEsperadas = -1`, caso de `colaCamiones`): esperar ahi es la conducta correcta.
4. **El tablero informa los envios en curso y donde estan.** `Envios: N · en curso: M` y el detalle por bloque. El KPI anterior (`en transito`) contaba transito de flota de producto y por eso mostraba 0 mientras 1 066 envios estaban congelados en el flowchart.

**Alternativas:** poner una capacidad grande y fija en cada bloque (vuelve a ser un numero magico y el dia que se supere estrangula igual); dejar la capacidad de 1 y compensar dimensionando mas portacontenedores (dimensiona contra un cuello ficticio); auditar solo al cierre de la campania (el diagnostico llega tarde y no dice desde cuando); medir la retencion sin abortar (un envio retenido es un error del modelo, no un dato, y el resto de las reconciliaciones aborta).

**Consecuencias:** con `datos/entrada_ejemplo.xlsx` el exportado pasa de **13 749 a 30 343 tn** de las 30 656 recibidas, el servicio de **25 % a 95 %**, los pedidos atrasados de **708 a 4**, el atraso medio de **46,2 a 0,8 dias** y los envios entregados de 919 a 1 962. No se relajo ninguna restriccion real: los portacontenedores ocupados al cierre pasan de 1 069 a **21 de 1 500** y el uso de posiciones de consolidacion queda en 20 %, o sea que la red **nunca habia estado saturada**. Todos los KPIs anteriores a este cambio —incluidos los de ADR-059 a ADR-062 y el barrido `fase-26`/`fase-27`— se midieron contra un techo artificial de 511 contenedores y **no son comparables**; el CSV pasa a `fase-28`. Queda **fuera de alcance**, declarado: cualquier limite de operaciones simultaneas por sitio que se quiera modelar tiene que entrar como recurso con dato propio, no como capacidad de bloque.

## ADR-064 — Tabla maestra de auditoria de red: identidad de la decision y streaming a CSV

**Estado:** aceptada
**Fecha:** 2026-07-24
**Contexto:** el modelo decide bien pero no deja escrito **por que**. `asignarConEvaluador()` genera hasta 18 alternativas por pedido, las ordena por costo y ejecuta la primera factible; de todo eso solo sobrevivia la asignacion elegida. Sin las alternativas descartadas no se puede responder la pregunta que motiva el proyecto —cuanto cuesta la restriccion, que deposito o que posicion conviene agrandar— y sin la duracion real de cada tramo no se puede separar un atraso de transporte de una espera de recurso. El requerimiento pedia seis tablas nuevas; tres de ellas ya existian en el modelo.

**Decision:** una auditoria que **observa** y no participa. No toca `generarAlternativas()`, `evaluarAlternativa()`, `ordenarAlternativas()` ni `costearAlternativa()` salvo para clasificar el motivo del descarte, que ya se escribia como texto libre.

1. **Identidad de la decision.** `id_decision = <codigo_pedido>-D<n>` por pedido y **por ronda** del asignador —`asignarConEvaluador()` regenera las alternativas despues de cada asignacion parcial (ADR-055), asi que sin la ronda C-07 no cierra en un pedido multiorigen—, e `id_alternativa = <id_decision>-A<n>`. El par se propaga a la asignacion, al contenedor, al envio, a los arcos y al `Cargo`: es la clave que une las cinco tablas.
2. **Tres tablas se extienden, no se crean.** `costos_eventos` es `RegistroCostos.Cargo` (ADR-052) y `snapshot_capacidad_recursos` es la agenda de capacidad de ADR-060, ambas con `run_id` y la identidad de la decision agregadas. Crear clases nuevas armaria una **segunda fuente de verdad** de costos y un segundo calendario de capacidad, y volveria la reconciliacion de costos una comparacion entre dos listas que pueden diferir; extendiendo las existentes es tautologica. `asignaciones_capacidad_decisiones.csv`, el antecesor parcial, queda deprecado.
3. **Streaming a CSV, no acumulacion en memoria.** El requerimiento preferia juntar los registros y escribir al cierre: con `datos/entrada_ejemplo.xlsx` son 25 524 filas de 97 columnas de decisiones y 108 007 cargos por corrida, y el barrido son 1 080 corridas en la misma JVM. Cada tabla se abre una vez, se escribe fila por fila y se cierra al terminar la corrida; una corrida interrumpida ya dejo su evidencia en disco.
4. **El nivel es un parametro y el default es apagado.** `nivelAuditoriaRed` vale `DESACTIVADA` (barrido), `RESUMIDA` (campania normal) o `COMPLETA` (corrida puntual auditada). El experimento `Simulation` corre en `COMPLETA`; el barrido, apagado.
5. **El cierre es del final de la corrida, no del ultimo dia de campania.** El flujo sigue moviendo envios despues del ultimo paso diario y esos arcos tambien son hechos de la corrida: `cerrarAuditoriaRed()` se llama desde `AfterSimulationRunCode` de los dos experimentos, es idempotente, y `AuditoriaRed` distingue "activa" de "ya cerrada" para que una fila emitida despues del cierre se cuente en el resumen en vez de romper la corrida o desaparecer sin dejar rastro.
6. **Un arco es un hecho fisico terminado.** Se emite al salir del bloque, cuando existen la duracion real y el estado final, reusando los tres campos que ADR-063 ya puso para C-05 (`bloqueActual`, `diaEntradaBloque`, `horasEsperadasBloque`), de modo que no hay una segunda etapa que pueda quedar desincronizada de la que vigila C-05. Se agregan las **dos esperas** que explican el atraso y que el requerimiento no tenia —portacontenedor y posicion de consolidacion— y se excluyen los tres "arcos" que en realidad son cargos: almacenaje, ingreso y egreso contable. Por la misma razon el arco **no lleva importe**: el costo se une contra `costos_eventos`.
7. **Los motivos de descarte son los que el codigo produce.** Catorce codigos normalizados, entre ellos `NO_TOMADA_AL_EJECUTAR` —una alternativa factible al evaluar que fallo al ejecutar porque otro pedido del mismo dia se llevo el recurso—, que el requerimiento no distinguia de una no factible. Ser mas cara no es un descarte y llegar tarde tampoco: una alternativa tarde sigue compitiendo. Una tarifa faltante no descarta, **aborta** la corrida por contrato de datos.
8. **Lo que el modelo no produce va vacio y documentado.** El evaluador no estima costo de espera ni costo de oportunidad por alternativa, y `ordenarAlternativas()` produce **un solo** ranking, no dos. Inventar un segundo ranking seria inventar una politica, que es justo lo que el requerimiento prohibe. `costo_real_contenedores_usd` por asignacion es la suma de los cargos **atribuibles al contenedor**, con el alcance declarado: el almacenaje se devenga contra el lote.
9. **Esquema publicado y generado.** `resultados/esquema_auditoria.json` sale de los mismos `encabezadoCsv()` que escriben los CSV, con archivo, columnas y clave de cada tabla, y `version_esquema` (`ADR-064.1`) viaja tambien en el manifiesto por corrida. El encabezado y el esquema no pueden divergir. `run_id = <id_escenario>-R<replica>` es la primera columna de las seis tablas: sin el no se pueden cargar 1 080 corridas en una tabla sin colision de claves.
10. **Reconciliaciones nuevas** C-06 (elegidas = asignaciones), C-07 (toneladas tomadas contra el saldo del pedido por ronda), C-09 (asignaciones, contenedores y envios), C-10 (agregado de arcos contra filas escritas), C-12 (balance diario de cada nodo) y las aserciones C-13 (las etapas suman el ciclo del evaluador) y C-14 (los cargos exportados son los del registro). Ninguna compara dos exportaciones entre si: todas comparan la tabla contra el estado del modelo, que es la unica forma de que puedan fallar.

**Alternativas:** crear las seis tablas desde cero como pedia el requerimiento (dos fuentes de verdad, una de ellas de dinero); acumular en memoria y exportar al cierre (2 M de registros en el barrido, y una corrida interrumpida no deja nada); auditar siempre (el barrido paga el costo sin usar el resultado); construir en AnyLogic la vista con filtros del requerimiento (la mitad del esfuerzo, solo la ve quien abre el modelo, PLE no exporta vistas y es exactamente lo que se va a rehacer en el tablero web); atribuir el costo a cada arco (duplica importes que pueden diferir); estimar la espera y la oportunidad por alternativa (cambia la politica que se dice auditar).

**Consecuencias:** una corrida en `COMPLETA` con `datos/entrada_ejemplo.xlsx` deja 45 MB en seis tablas: 25 524 alternativas evaluadas en 1 418 decisiones de hasta 3 rondas, 1 418 asignaciones, 11 020 arcos de 9 tipos, 108 007 cargos, 3 385 snapshots de inventario con **descuadre 0** y 3 276 filas de capacidad. Con eso se puede responder, por ejemplo, que 398 alternativas mas baratas no se pudieron usar, o que la espera de posicion promedio fue de 65,7 h. La corrida pareada con la auditoria apagada decide **identico**: las 1 987 asignaciones son iguales byte a byte y las 3 276 filas de capacidad tambien, salvo el `run_id`, que solo existe con la auditoria activa. Queda **fuera de alcance**, declarado: el tablero web —que se construye en otro proyecto sobre estos CSV—, el barrido con auditoria activada y cualquier campo que exija estimar algo que el evaluador no estima.

## ADR-065 — El evaluador de circuitos compara costo de despacho, nunca costo de dejarlo donde está

**Estado:** propuesta
**Fecha:** 2026-08-08
**Contexto:** `seleccionarDeposito()` (transferencia planta→depósito, ADR-056) ya proyecta un costo futuro: `posible * tarifaAlmacenamiento * diasEstimadosAlmacenamiento`, con `diasEstimadosAlmacenamiento = 30` (parámetro de `Main`, sin fuente en `Escenario`). El evaluador de circuitos (`costearAlternativa()`, ADR-054) no tiene el equivalente: compara sólo el costo de despachar **hoy** desde cada origen (round trip + estiba + OUT + THC + terminal + despachante), nunca "cuánto le va a costar a esa tonelada seguir parada si no la elijo". Con `politica_seleccion = MENOR_COSTO_INCREMENTAL_FACTIBLE` esto puede hacer que, para un pedido con stock disponible en planta y en un depósito, gane sistemáticamente el origen con el flete/estiba más barato *de hoy*, aunque el otro origen tenga una tarifa de holding (`oportunidadUsdTnDia` en planta, `storageUsdTnDia` en depósito) mucho más cara y ese stock termine acumulando costo día tras día sin que ninguna decisión lo saque de ahí — es una de las causas de que `excedenteFinalTn()` cierre con tonelaje atrapado en depósito (ver `flow/02-logica-entrega-pedidos.md`). Se descarta explícitamente usar `MENOR_COSTO_END_TO_END_FACTIBLE` para esto: esa vista le suma a cada alternativa el histórico ya devengado (`costoAlmacenajeHundido`, que crece cada día que el stock sigue en depósito), así que cuanto más tiempo lleva parado, **más caro** se ve frente a planta (que nunca tiene histórico) — empeora el problema en vez de resolverlo, y por diseño (evitar la falacia del costo hundido) es la vista estratégica, no la táctica.

**Decisión:** agregar, sólo para el **ranking** de `ordenarAlternativas()`, un crédito por el holding futuro que cada alternativa evita al despacharse hoy — sin tocar `costoIncremental` ni `costoEndToEnd`, que son los campos auditados contra el cargo real (ADR-052, ADR-053).

1. **Reutilizar `diasEstimadosAlmacenamiento`** (ya existe, default 30) como horizonte de proyección para las dos decisiones — una sola fuente de verdad para "cuántos días de holding futuro se proyectan", en vez de un segundo parámetro paralelo.
2. **Tarifa de holding unificada por origen:** `tarifaHoldingOrigen(idOrigen, producto)` devuelve `oportunidadUsdTnDia` si el origen es `"PLANTA"`, o `storageUsdTnDia` del depósito en cualquier otro caso — es la misma dualidad que ya resuelve `seleccionarDeposito()`, expuesta como función para que el evaluador la reuse.
3. **Horizonte acotado por lo que queda de campaña:** `horizonteHoldingEvitado() = min(diasEstimadosAlmacenamiento, max(0, duracionCampaniaDias - diaCampania()))` — proyectar 30 días de storage evitado a 3 días del cierre exagera el crédito.
4. **Nuevo campo en `AlternativaCircuito`, `costoHoldingEvitado`**, calculado en `costearAlternativa()` como `tarifaHoldingOrigen(idOrigen, producto) * horizonteHoldingEvitado() * toneladas`. Y un nuevo método de lectura, `costoUnitarioRankingSegun(endToEnd) = (costoSegun(endToEnd) - costoHoldingEvitado) / toneladas`, que es lo único que cambia en `ordenarAlternativas()`: reemplaza a `costoUnitarioSegun(endToEnd)` en la comparación de costo, sin tocar los criterios de servicio ni el desempate de `PRIORIDAD_FRIO_PROPIO`, que siguen decidiendo **antes** de llegar al costo, exactamente como hoy.
5. **No es un cargo.** `costoHoldingEvitado` no entra a `totalizar()`, no se registra en `RegistroCostos`, no lo ve `reconciliarCostos()` ni `costoEsperadoCircuito()`. Es una corrección de orden, no de contabilidad — mezclarlo en `costoIncremental` rompería la reconciliación de costos que exige que el evaluador decida con el mismo número que después se cobra (ADR-053).

**Alternativas:** sumarlo directo a `costoIncremental` (descartada — invalida la reconciliación de C3/ADR-053, un costo hipotético terminaría auditado como si fuera real); usar `MENOR_COSTO_END_TO_END_FACTIBLE` (descartada, ver contexto — empeora el sesgo contra los depósitos con más antigüedad de stock); crear un parámetro nuevo en vez de reusar `diasEstimadosAlmacenamiento` (descartada — dos fuentes de verdad para el mismo horizonte, contra el principio de diseño del proyecto); aplicar el crédito también dentro del desempate de `PRIORIDAD_FRIO_PROPIO` para que el costo pueda ganarle al origen (fuera de alcance de este ADR — cambia la semántica ya aceptada de esa política, se trataría en un ADR separado si se decide).

**Consecuencias:** bajo `MENOR_COSTO_INCREMENTAL_FACTIBLE` / `MENOR_COSTO_END_TO_END_FACTIBLE`, el ranking puede elegir un origen con costo de despacho de hoy más alto si su tarifa de holding es suficientemente más cara que la del resto — en la práctica, tiende a vaciar primero los depósitos más caros de mantener. Bajo `PRIORIDAD_FRIO_PROPIO` no cambia nada (el desempate de origen sigue decidiendo antes de llegar al costo). El costo de campaña real (`costoTotalCampania()`) puede moverse porque **cambia qué alternativa se ejecuta**, no porque cambie cómo se calcula el costo de cada una — con `diasEstimadosAlmacenamiento = 0` (o el horizonte forzado a 0), el comportamiento debe ser **idéntico, byte a byte**, al actual: es el caso de prueba de regresión obligatorio (ver `V-COST-11`, `docs/06_Validacion/Plan_de_Validacion.md`).

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
