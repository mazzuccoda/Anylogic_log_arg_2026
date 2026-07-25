# Inventario del modelo real

[← Volver al índice](../README.md)

Este documento describe **lo que el archivo `RedLogistica_Exportacion.alp` contiene hoy**, no lo que el diseño pretende. Cuando la documentación y el modelo discrepan, manda el modelo y el desvío se anota aquí como hallazgo.

Generado a partir de `model_src/`, el espejo legible que produce `tools/exportar_modelo.py`. Cada hallazgo cita archivo y línea de ese espejo.

Los hallazgos marcados **resuelto** ya se corrigieron en el `.alp`; el resto describe el estado actual.

## 1. Composición del modelo

| Elemento | Cantidad | Detalle |
|---|---:|---|
| Tipos de agente | 10 | `Main`, `Planta`, `Deposito`, `Terminal`, `Pedido`, `LoteProducto`, `Camion`, `Envio`, `ContenedorExportacion`, `PlanLogistico` |
| Option Lists | 12 | ver `model_src/OptionLists.java` |
| Experimentos | 1 | `Simulation` |
| Librerías requeridas | 1 | Process Modeling Library |
| Unidad de tiempo del modelo | — | `Day` |
| Funciones | 63 | 35 en `Main` |
| Eventos | 1 | `Main.pasoDiario`, cada 1 día |
| Objetos embebidos en `Main` | 23 | 7 agentes de ubicación, 3 poblaciones, 1 `ResourcePool`, 11 bloques de flowchart |
| Variables de estado | 40 | 39 escalares (`Main` 17, `Planta` 9, `Deposito` 9, `Terminal` 4) más la colección `Main.depositos` |

## 2. Límites verificados de la edición PLE

Valores oficiales de la [comparación de ediciones](https://www.anylogic.help/anylogic/ui/editions.html), contrastados contra el modelo:

| Límite PLE | Valor | Uso actual | Margen |
|---|---:|---:|---|
| Tipos de agente por modelo | 10 | **10** | **agotado** |
| Agentes creados dinámicamente por corrida | 50 000 | ~600 en la corrida de prueba | amplio, pero crece con lotes y contenedores |
| Poblaciones y bloques de flowchart por agente | 200 | 23 en `Main` | amplio |
| Variables de dinámica de sistemas por agente | 200 | 0 | no aplica |
| Tiempo de modelo | 5 h, **excepto** Process Modeling Library | el modelo sólo usa PML | **no aplica**: la campaña de 183 días es válida |

Experimentos disponibles en PLE: Simulation, **Parameter Variation**, **Monte Carlo**, Compare Runs, Sensitivity Analysis, Calibration, Optimization con motor genético. No disponibles: Custom Experiment y aplicación autónoma. OptQuest limitado a 500 iteraciones y 7 variables de decisión.

Consecuencia práctica: **el barrido de escenarios con réplicas es viable en PLE.** El único límite que el proyecto ya está tocando es el de 10 tipos de agente.

## 3. Contenido por agente

| Agente | Parámetros | Funciones | Eventos | Rol real en el código |
|---|---:|---:|---:|---|
| `Main` | 4 | 35 | 1 | coordina todo: crea lotes, transfiere, reserva, crea envíos, costea |
| `Planta` | 12 | 6 | 0 | produce por día y mantiene stock y excedente por producto |
| `Deposito` | 15 | 15 | 0 | stock, reserva y tarifas por producto |
| `Terminal` | 9 | 1 | 0 | costos de consolidación |
| `Pedido` | 37 | 2 | 0 | demanda, estado comercial y contenedores |
| `LoteProducto` | 15 | 1 | 0 | identidad del lote y saldos |
| `Camion` | 9 | 0 | 0 | recurso del flowchart legado |
| `Envio` | 21 | 0 | 0 | mecanismo de despacho legado |
| `ContenedorExportacion` | 20 | 0 | 0 | creado, sin lógica de ejecución |
| `PlanLogistico` | 26 | 3 | 0 | estima costos y valida |

Eventos y su cadencia:

| Agente | Evento | Cadencia |
|---|---|---|
| `Main` | `pasoDiario` | 1 día |

`pasoDiario` invoca las siete fases en orden fijo (ADR-034); cada fase es una función de `Main`. Los seis eventos diarios anteriores y `Planta.produccionDiaria` ya no existen: su cuerpo pasó a la función de la fase correspondiente.

## 4. Hallazgos

### H-01 — El límite de tipos de agente de PLE está agotado

10 de 10. Confirma por qué no pudo crearse `ExistenciaLote` (ADR-007) y convierte la restricción en una regla de diseño permanente: **ninguna estructura nueva puede ser un tipo de agente** mientras no se libere un slot.

Slots liberables: `Envio` y `Camion` pertenecen al camino legado (§H-12). Retirarlos deja 2 disponibles.

### H-02 — Todo el estado está declarado como parámetro — **resuelto**

Corregido: 39 parámetros de `Main`, `Planta`, `Deposito` y `Terminal` pasaron a variables. Quedan como parámetros sólo las entradas (capacidades, tarifas, distancias, niveles de activación, costos unitarios de flete). Los agentes de entidad (`Pedido`, `LoteProducto`, `Envio`, `ContenedorExportacion`, `PlanLogistico`, `Camion`) siguen con sus campos como parámetros: se crean en tiempo de corrida, no aparecen en la interfaz del experimento, y su migración se hace junto con el rediseño de inventario.

Diagnóstico original:

`model_src/Planta.java:7-27`, `model_src/Main.java:7-28`. `stockJugo`, `excedenteJugo`, `costoFletePlantaDeposito`, `siguienteIdLote` y `pedidosRecibidos` son `Parameter`. No hay una sola variable `Class="Variable"` fuera de `Main.depositos`.

Los parámetros son la **interfaz de entrada** del agente: son lo que un experimento de Parameter Variation enumera y asigna. Con el estado mezclado ahí, la lista de parámetros del barrido se vuelve ilegible y nada distingue "capacidad del depósito" (entrada a dimensionar) de "stock actual" (resultado). Es el obstáculo más concreto entre el modelo de hoy y el uso definido en ADR-018.

Regla: **entrada → parámetro; estado → variable; resultado → variable o dataset.**

### H-03 — Las listas por ubicación documentadas no existen

`ubicacionesFisicas` y `toneladasPorUbicacion` aparecen en la especificación y en ADR-007, pero tienen **cero ocurrencias** en el `.alp`. La fase 3 figuraba con ítems tildados y 30% de avance; su avance real es 0%.

Un lote sigue teniendo una única `ubicacionActual` y un único `diaIngresoDeposito` (`model_src/LoteProducto.java`). El modelo no soporta un lote distribuido en varias ubicaciones.

### H-04 — El almacenaje diario se contabiliza dos veces con criterios distintos — **resuelto**

Corregido: `devengarAlmacenamientoDiario()` es la única fuente del costo de almacenaje. Recorre los lotes con depósito y toneladas disponibles, imputa el costo al lote y **en el mismo recorrido** lo agrega al depósito, de modo que el total del depósito es por construcción la suma de sus lotes. Desaparece el filtro por `EN_DEPOSITO`, así que los lotes reservados —que siguen ocupando el depósito— también devengan.

Diagnóstico original:

`model_src/Main.java:1030-1057`. El mismo evento acumulaba:

```java
// (a) agregado por depósito
deposito.costoAlmacenamientoAcumulado += stockJugo * costoJugoTnDia + ...;

// (b) por lote, sólo si estado == EN_DEPOSITO
lote.costoAlmacenamientoLote += lote.toneladasDisponibles * tarifa;
lote.costoAcumulado          += ...;
```

Dos fuentes de verdad del mismo costo. Y no son equivalentes: (b) excluye los lotes en estado `RESERVADO`, que (a) sí cuenta porque siguen en el stock del depósito. Apenas existe una reserva, los totales divergen, y cualquier KPI que sume ambos duplica el almacenaje.

### H-05 — Reservar parte un lote en dos agentes y reescribe lo producido

`model_src/Main.java:433-499`. `crearLoteReservadoDesdeDivision` crea un `LoteProducto` nuevo con las toneladas reservadas y **resta esas toneladas a `toneladasIniciales` del original**.

Dos consecuencias:

1. `toneladasIniciales` deja de significar "lo que se produjo de este lote", con lo que el invariante de reconciliación del modelo de datos (`producido = disponible + reservado + despachado + merma`) no se puede evaluar.
2. La identidad comercial se fragmenta: un pedido con tres reservas parciales genera tres lotes con `idLote` distintos, unidos sólo por el puntero `loteOrigen`.

Es exactamente el problema que resuelven las capas (ADR-021): la capa cambia de saldo, el lote no se parte.

### H-06 — Dos eventos tienen cadencia fraccionaria — **resuelto**

Corregido: no quedan cadencias fraccionarias; el único evento del modelo es diario.

Diagnóstico original:

`revisarPedidosAtrasados` cada 1,9 días y `generarPedidosPrueba` cada 1,2 días. Con un reloj diario (ADR-019) esto produce deriva de fase: los eventos van cayendo en momentos distintos dentro del día y la regla de cross docking "mismo día" se vuelve ambigua. Ambos deben pasar a 1 día.

### H-07 — El orden de los eventos diarios no está definido — **resuelto**

Corregido: `pasoDiario` fija el orden en el modelo y ya no depende del desempate del motor.

Diagnóstico original:

Cinco eventos ocurren cada 1 día. AnyLogic desempata por orden interno de programación, no por una regla del modelo. Que la producción ocurra antes o después del devengo de almacenaje cambia el costo del día. La secuencia diaria debe ser explícita (ADR-034).

### H-08 — Ubicaciones cableadas como agentes embebidos

Cinco depósitos (`depFrinoa`, `depNorry`, `depBoreas`, `depRuta9`, `depDodero`) y dos terminales (`terminalZarate`, `terminalT4`) son objetos embebidos individuales, y la lista se arma a mano en el código de arranque de `Main`:

```java
depositos.add(depFrinoa);
depositos.add(depNorry);
// ...
```

Agregar o quitar un depósito exige editar el modelo. Con esto no se puede barrer "cuántos depósitos hacen falta", que es la pregunta P1 del proyecto. Deben ser una población cargada desde datos.

**Parcialmente resuelto.** Los cinco depósitos y las dos terminales siguen siendo objetos embebidos, pero dejaron de ser la fuente de sus datos: cada uno declara un `idUbicacion` y `Main.aplicarDatosAAgentes()` les escribe capacidades, tarifas y velocidades desde las tablas. Falta convertirlos en población para poder variar la cantidad, lo que exige antes resolver el vínculo con la vista de `Main`.

### H-09 — La demanda es un caso de prueba cableado

`model_src/Main.java:1093-1142`. Tres pedidos fijos (P001 500 tn día 60, P002 300 tn día 60, P003 80 tn día 180) con banderas booleanas de "ya creado". Sirve para probar, no para dimensionar.

**Resuelto.** `registrarPedidosDelDia()` recorre `datos.pedidosDelDia(dia)`: la demanda es la tabla `PedidoPlan`, que hoy genera `GeneradorSintetico` con semilla y variabilidad, y mañana llenará el Excel.

### H-10 — Tarifas, distancias y capacidades viven en el código

- Costos de flete planta-depósito como parámetros de `Main`: `costoFijoViajePD = 150`, `costoKmPD = 1.2`, `costoTnPD = 2.0`.
- Tarifas de almacenaje y fletes a puerto como parámetros de cada `Deposito`.
- Capacidades de contenedor en un `switch`: `REEFER_40 → 25.0`, `DRY_HC_40 → 25.0`, `IMO_DRY_20 → 20.0` (`model_src/Main.java:987`).
- `velocidadCargaTnHora = 50` como parámetro de depósito.

Contradice el contrato de datos (ADR-029) y hace que cada cambio de tarifa sea un cambio de modelo.

**Resuelto para el alcance del paso 2.** Distancias, capacidades, tarifas de almacenaje, fletes a terminal, consolidación, tipo y capacidad de contenedor y velocidades se leen de `DatosEntrada`. Siguen como parámetros de `Main` los tres coeficientes del flete planta → depósito (`costoFijoViajePD`, `costoKmPD`, `costoTnPD`), que son una fórmula y no una tarifa por par origen-destino.

### H-11 — Una tarifa sin valor equivale hoy a cero

`double costoAceiteTnDia;` es el único parámetro de `Deposito` sin valor inicial explícito. Java lo inicializa en 0, así que almacenar aceite en un depósito mal cargado **no cuesta nada** y el modelo no avisa. Viola la regla de costos "una tarifa faltante no equivale a cero".

**Resuelto.** Las consultas de `DatosEntrada` lanzan una excepción con la clave faltante y `validar()` recorre todas las combinaciones alcanzables antes del día 1 (ADR-037).

### H-12 — Conviven dos caminos de despacho

`Envio` más el flowchart de `Main` (`colaCamiones → tomarCamion → cargarCamion → viajarPuerto → descargarPuerto → consolidarCarga → retornarDeposito → liberarCamion`) con el `ResourcePool flotaCamiones`, frente a `ContenedorExportacion`, que existe pero no tiene lógica de ejecución (0 funciones, 0 eventos).

El camino nuevo todavía no reemplaza nada. La migración segura exige mantener ambos hasta la comparación, pero conviene fijar el momento del retiro: retirar `Envio` y `Camion` es lo que libera los dos slots de agente que el proyecto va a necesitar.

### H-13 — No hay experimento de barrido

Sólo existe el experimento `Simulation`. No hay Parameter Variation ni control de semilla, y sin eso no hay réplicas ni resultados reproducibles.

### H-14 — La consolidación y el cross docking no están implementados

Ni `ContenedorExportacion` ni `Terminal` tienen lógica; `Terminal` sólo calcula un costo. Los flujos documentados de consolidación, cross docking y ciclo de portacontenedor son diseño, no código.

## 5. Prioridad de corrección

| Prioridad | Hallazgos | Motivo |
|---|---|---|
| Bloquea el uso definido | ~~H-02~~, H-08 (parcial), ~~H-09~~, ~~H-10~~, H-13 | sin esto no se puede barrer ni un solo escenario |
| Corrompe resultados | ~~H-04~~, H-05, ~~H-11~~ | producen números que parecen válidos y no lo son |
| Corrompe la semántica temporal | ~~H-06~~, ~~H-07~~ | el reloj diario no es consistente |
| Deuda estructural | H-01, H-03, H-12, H-14 | condicionan el diseño de todo lo que sigue |

Tachados: resueltos en el `.alp`.

## 6. Cómo regenerar este espejo

```bash
python3 tools/exportar_modelo.py
```

Escribe `model_src/` a partir del `.alp`. Debe ejecutarse después de cada cambio del modelo y versionarse en el mismo commit, para que un pull request muestre el cambio de código y no un diff de XML ilegible.
