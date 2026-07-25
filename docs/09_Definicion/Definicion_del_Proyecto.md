# Definición del proyecto — modelo de dimensionamiento de campaña

[← Volver al índice](../README.md)

**Estado:** propuesta para revisión.
**Fecha:** 2026-07-24.
**Reemplaza en caso de conflicto:** el alcance implícito de la Especificación Técnica Maestra, que mezclaba uso táctico y estratégico.

---

## 1. Uso primario decidido

El modelo se construye para **dimensionar y costear la campaña**: capacidad de depósitos, flota de camiones, posiciones de consolidación y cross docking, y costo logístico total de exportación.

No se construye (todavía) para decidir la operación de un pedido individual en tiempo real. Esa decisión se toma explícitamente porque define el nivel de detalle de todo el resto.

### 1.1 Preguntas que el modelo debe responder

| # | Pregunta | Salida que la responde |
|---|---|---|
| P1 | ¿Cuánta capacidad de depósito de terceros se necesita por producto para no frenar la producción? | Ocupación máxima y percentil 95 por depósito y producto |
| P2 | ¿Cuántos camiones de producto y cuántos portacontenedores se necesitan? | Utilización, tiempo de espera por falta de camión, pedidos atrasados por recurso |
| P3 | ¿Cuánto cuesta la campaña completa, y cómo se compone? | Costo total y desglose por categoría, por producto y por estrategia |
| P4 | ¿Cuál es el costo por tonelada exportada y por contenedor? | USD/tn y USD/contenedor por producto |
| P5 | ¿Habilitar cross docking reduce el costo total y en cuánto? | Comparación de escenarios con y sin cross docking |
| P6 | ¿Qué recurso es el cuello de botella de la campaña? | Ranking de utilización y de causas de atraso |
| P7 | ¿Qué pasa en una campaña de producción alta o con menos capacidad de depósito? | Barrido de escenarios |
| P8 | ¿Cuánto producto no se puede almacenar (excedente) y qué política lo evita? | Excedente acumulado por producto y escenario |

### 1.2 Preguntas explícitamente fuera del uso primario

- Elegir la alternativa logística óptima de un pedido puntual en el día.
- Cumplir un cut-off documental por buque.
- Secuenciar turnos y ventanillas horarias de terminal.

El modelo debe quedar **estructuralmente preparado** para responderlas más adelante (el planificador y las estrategias se mantienen), pero no se calibran a ese nivel ahora.

---

## 2. Resolución temporal

| Aspecto | Definición |
|---|---|
| Unidad base del reloj | **Día** (`time()` en días, `TimeUnit = day`) |
| Horizonte por defecto | Campaña de 6 meses (183 días), parametrizable `diaInicio`, `duracionCampaniaDias` |
| Fecha calendario | Parámetro `fechaInicioCampania`; toda salida reporta día simulado y fecha |
| Jornada operativa | **No se modela.** Los recursos se cuentan por día disponible |
| Fines de semana y feriados | Parámetro `diasOperativosPorSemana` (default 6). Afecta capacidad de transporte y de consolidación, no la producción |
| Duraciones | En días o fracción de día (`0,5` = medio día). Todas en la tabla `TiemposOperativos` |

Consecuencia sobre reglas ya acordadas:

- **"Mismo día operativo" del cross docking** queda definido como: el camión de producto y el portacontenedor están asignados al **mismo día simulado entero** (`floor(time())`). Si uno no llega ese día, la operación se reprograma al día siguiente y el camión que esperó consume un día de recurso (costo de espera parametrizable).
- **Fecha límite del pedido** es un plazo en días desde su llegada (`diaLimite`). Incumplirla **no hace el plan no factible**: se registra como atraso y alimenta el KPI de nivel de servicio. Motivo: en un modelo de dimensionamiento el atraso es el síntoma que se quiere medir, no algo que deba prohibirse.

---

## 3. Recursos: modelo de conteo diario

Con paso diario no se modelan colas hora a hora. Cada recurso se representa como **capacidad diaria consumible**:

| Recurso | Unidad de capacidad | Consumo |
|---|---|---|
| Camión de producto | camión-día | 1 camión-día por viaje planta→depósito o →cross dock (o `N` días si el tránsito dura `N`) |
| Portacontenedor | camión-día | `duracionCicloContenedorDias` por contenedor (retiro vacío → carga → ingreso terminal) |
| Posición de consolidación | posición-día por sitio | `contenedoresPorPosicionDia` contenedores por posición y día |
| Posición de cross dock | posición-día por sitio | 1 operación de cross dock por posición y día |
| Capacidad de depósito | toneladas | Stock instantáneo, no consumible |

Si la demanda del día excede la capacidad, la operación **se posterga al día siguiente** y se registra `esperaPorRecurso` por tipo de recurso. Ese registro es la respuesta a P2 y P6.

Ventaja frente a statecharts y colas horarias: menos código, sin transiciones ilegales posibles, y suficiente para dimensionar. Si más adelante se necesita detalle horario, se reemplaza esta capa sin tocar inventario ni costos.

---

## 4. Variabilidad y réplicas

Un modelo determinístico subdimensiona sistemáticamente: la flota que alcanza "en promedio" no alcanza en los picos. Por eso la variabilidad es parte del alcance mínimo.

| Fuente | Representación por defecto | Parámetro |
|---|---|---|
| Producción diaria por producto | `produccionMedia × triangular(1-a, 1, 1+b)`, truncada en 0 | `variabilidadProduccion` |
| Tránsito (planta↔depósito, →terminal) | `triangular(min, moda, max)` en días | `TiemposOperativos` |
| Duración de consolidación | `triangular` en fracción de día | `TiemposOperativos` |
| Disponibilidad de camión | Fracción de flota fuera de servicio por día | `indisponibilidadFlota` |
| Llegada de pedidos | Volumen y espaciamiento con variabilidad | `variabilidadDemanda` |

Reglas:

- Todo parámetro de variabilidad debe poder ponerse en cero para obtener una corrida determinística reproducible (necesario para los casos V-001..V-018).
- Los resultados de dimensionamiento se reportan sobre **réplicas** (default 30, `cantidadReplicas`), con media, desvío y percentil 95. Un único run no es evidencia.
- Semilla fija por réplica y registrada en la salida.

---

## 5. Alcance funcional revisado

### 5.1 Incluido

Se mantiene todo el alcance del borrador, con estas precisiones:

- Producción diaria estocástica por producto, con capacidad de planta y excedente.
- Lote comercial acumulativo con existencias distribuidas (ver §6).
- Transferencia parcial planta→depósito con costo de flete e IN.
- Pedidos de exportación con lote solicitado, terminal, naviera y plazo.
- Reserva de producto trazable por pedido y contenedor.
- Contenedores individuales con las cuatro estrategias (consolidación planta/depósito, cross dock depósito/terminal).
- Costeo histórico, incremental y end-to-end, auditable por registro.
- Recursos finitos con conteo diario y registro de esperas.
- Escenarios y barridos de parámetros con réplicas.

### 5.2 Fuera de alcance (confirmado)

- Ventas locales.
- Transferencias depósito→depósito y depósito→planta.
- Optimización matemática (MILP).
- Cut-off por buque, slots navieros, demurrage y detention.
- Aduana y documentación.
- Detalle horario, turnos y ventanillas de terminal.
- Integración con ERP/TMS/WMS.
- Unidad de carga a nivel bulto o pallet (se costea por tonelada y contenedor).

### 5.3 Simplificaciones aceptadas y su impacto

| Simplificación | Impacto si la realidad difiere |
|---|---|
| Capacidad de contenedor en tn fija por tipo | Subestima cantidad de contenedores si la carga es volumétrica |
| Vacíos siempre disponibles en terminal | Subestima tiempo de ciclo del portacontenedor |
| Sin cut-off por buque | El atraso medido es optimista |
| Paso diario | No detecta congestión intradiaria |
| Un cliente y una calidad | No captura competencia entre pedidos por calidad |

Cada simplificación queda como riesgo documentado, no como supuesto oculto.

---

## 6. Estructura de inventario: capas

Unidad atómica del inventario físico:

```text
Capa = (lote, ubicacion, diaIngreso, toneladas, toneladasReservadas)
```

Reglas:

1. Un lote comercial tiene una identidad única y `N` capas distribuidas en planta, depósitos y contenedores.
2. El stock de una ubicación **se deriva** de la suma de sus capas. No existe una variable de stock independiente que pueda desincronizarse.
3. El consumo (transferencia, reserva, despacho) es **FIFO por `diaIngreso`**.
4. El almacenaje se devenga cada día sobre el saldo de cierre de cada capa: `toneladas × tarifa`, una sola vez y en un solo lugar (ADR-034, fase 6). La capa es lo que permite imputar ese costo al lote y a la ubicación correctos cuando hubo retiros parciales.
5. Toda operación sobre capas es transaccional: si falla un paso, se revierte todo.

Esto reemplaza las cuatro listas paralelas (`ubicacionesFisicas`, `toneladasPorUbicacion`, `reservadasPorUbicacion`, `diasIngresoPorUbicacion`), que no permitían costear storage con múltiples ingresos a la misma ubicación y eran frágiles por índices.

### 6.1 Reservas

```text
Reserva = (idReserva, pedido, contenedor, lote, capa, toneladas, estado)
```

`toneladasReservadas` de la capa se deriva de las reservas activas. Permite responder "qué pedido reservó estas toneladas" y liberar la reserva correcta al cancelar.

### 6.2 Política de reserva

La reserva atómica pura es incompatible con el lote acumulativo: un pedido de 500 tn sobre un lote que se produce en 15 días nunca sería factible. Política definida:

| Concepto | Definición |
|---|---|
| **Compromiso** | El pedido se asocia al lote y a la producción futura esperada. No bloquea stock físico. Se crea al aceptar el pedido |
| **Reserva** | Sobre stock físico existente. Se crea incrementalmente a medida que hay saldo libre |
| **Despacho** | Consume reserva y descuenta la capa |

Un pedido pasa a ejecutable cuando la reserva acumulada alcanza al menos un contenedor completo, y se despacha por contenedor. Esto permite despachos parciales, que ya está aceptado como regla de negocio.

### 6.3 Prioridad entre pedidos

Cuando varios pedidos compiten por el mismo lote o por el mismo recurso:

1. menor `diaLimite`;
2. si empatan, mayor antigüedad de llegada;
3. si empatan, mayor cantidad de toneladas pendientes.

Parametrizable como `politicaPrioridad` para poder comparar políticas como escenario.

---

## 7. KPIs de dimensionamiento

Se agregan a los KPIs ya definidos y son los que cierran las preguntas P1..P8. Todos reportados como media, desvío y P95 sobre réplicas.

### Capacidad

- ocupación media y máxima por depósito y producto (tn y % de capacidad);
- días con ocupación > 90%;
- excedente de producción acumulado (tn) y días con excedente > 0.

### Flota y posiciones

- utilización de camiones de producto y de portacontenedores (%);
- camión-días utilizados vs disponibles;
- operaciones postergadas por falta de recurso, por tipo de recurso;
- utilización de posiciones de consolidación y cross dock.

### Servicio

- pedidos entregados dentro del plazo (%);
- atraso medio y P95 (días);
- toneladas pendientes al cierre de campaña.

### Costo

- costo total de campaña y desglose por las 11 categorías;
- USD/tn exportada y USD/contenedor, por producto;
- costo por estrategia logística;
- participación del almacenaje en el costo total (indicador directo de sobredimensionamiento de depósito).

### Salidas

Cada corrida escribe CSV versionado: `resumen_escenario.csv`, `registro_costos.csv`, `series_diarias.csv`, `esperas_recursos.csv`, más `versionModelo`, escenario, réplica y semilla.

---

## 8. Datos de entrada

Fase 1: datos sintéticos generados por parámetros.
Fase 2: los mismos datos leídos de Excel, sin cambiar la lógica del modelo.

Para que ese cambio no requiera reescribir nada, el contrato de datos se define desde ahora: ver [Contrato de datos](Contrato_de_Datos.md). El generador sintético debe producir exactamente las mismas tablas que el Excel.

---

## 9. Impacto sobre el roadmap

Orden propuesto (reordena el roadmap actual, que arranca por implementación antes de cerrar el dominio):

| Paso | Contenido |
|---|---|
| 0 | Versionar el `.alp` y el código exportado; CHANGELOG y `versionModelo` |
| 1 | Cerrar esta definición y los ADR nuevos |
| 1b | Separar parámetros de estado (ADR-033) y unificar la secuencia diaria (ADR-034) |
| 2 | Tablas de datos maestros y tarifas como datos (§8), con generador sintético |
| 3 | Capas de inventario y stock derivado (§6) |
| 4 | Lote comercial acumulativo sobre capas |
| 5 | Reservas trazables, compromiso y prioridad (§6.1–6.3) |
| 6 | Transferencia parcial transaccional |
| 7 | Registro de costos idempotente y suite de verificación ejecutable |
| 8 | Ejecución con recursos de conteo diario (§3): consolidación, terminal, cross dock |
| 9 | Planificador de alternativas y selección |
| 10 | Escenarios, réplicas y KPIs de dimensionamiento (§7) |

El planificador va después de la ejecución a propósito: estimar costo y tiempo de una alternativa exige que exista un ejecutor que los produzca.

---

## 9.1 Restricciones de la edición PLE

El proyecto se desarrolla íntegramente sobre AnyLogic PLE (ADR-020). Los límites fueron verificados contra el modelo real y **ninguno impide el alcance definido**:

- Los barridos con réplicas son posibles: Parameter Variation y Monte Carlo están incluidos en PLE.
- El horizonte de 183 días es válido: el límite de 5 horas de tiempo de modelo no aplica a la Process Modeling Library, la única que el modelo usa.
- El único límite ya alcanzado es el de **10 tipos de agente**, y está agotado. Por eso las capas, reservas y registros de costo se implementan como clases Java y no como agentes (ADR-030, ADR-031).

Detalle y evidencia en el [Inventario del modelo](../03_Logica/Inventario_del_Modelo.md).

---

## 10. Criterios de aceptación del proyecto

El proyecto se considera cumplido cuando:

1. corre una campaña completa de 183 días con 30 réplicas sin excepciones;
2. reconcilia inventario (producido = físico + despachado + excedente) y costos (costo de pedido = suma de sus registros) en toda corrida;
3. los casos V-001..V-025 pasan en modo determinístico y quedan registrados en CSV;
4. responde P1..P8 con evidencia de al menos 4 escenarios comparados;
5. los datos de entrada provienen de Excel sin modificar la lógica;
6. la documentación refleja el modelo implementado y el `.alp` está versionado.
