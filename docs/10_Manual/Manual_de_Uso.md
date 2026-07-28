# Manual de uso

Paso a paso para abrir el modelo, correr una campaña, cargar datos propios desde Excel, correr el barrido de escenarios y leer los resultados.

Este documento es operativo: qué hacer, en qué orden y qué esperar. Lo que significan los números está en [Tablero e indicadores](Tablero_e_Indicadores.md); lo que significan las columnas de entrada está en el [contrato de datos](../09_Definicion/Contrato_de_Datos.md).

---

## 1. Qué necesitás

| Requisito | Detalle |
|---|---|
| AnyLogic | 8.9.9 Personal Learning Edition o superior. PLE alcanza para todo lo que hace hoy el modelo (ADR-020). |
| Repositorio | Clon local de este repositorio. El modelo es `RedLogistica_Exportacion.alp` en la raíz. |
| Excel (opcional) | Sólo si vas a cargar datos propios. El libro de entrada es `.xlsx`. |
| Python 3 (opcional) | Sólo para regenerar la plantilla de Excel y el espejo `model_src/`. |

No hay que instalar bibliotecas dentro de AnyLogic: el lector de Excel que usa el modelo es el que trae AnyLogic.

---

## 2. Abrir el modelo

1. Abrir AnyLogic.
2. `File → Open…` y elegir `RedLogistica_Exportacion.alp`.
3. En el panel `Projects` (izquierda) aparece el árbol del modelo: los agentes (`Main`, `Planta`, `Deposito`, `Terminal`, `Pedido`, `LoteProducto`, `Camion`, `Envio`, `ContenedorExportacion`, `PlanLogistico`), las clases Java (`DatosEntrada`, `GeneradorSintetico`, `ImportadorExcel`, `Inventario`, `Capa`), las listas de opciones y dos experimentos: `Simulation` y `Escenarios`.
4. `Model → Build` (o `Ctrl+B`). Tiene que decir **Build completed successfully**.

Si el build falla, el error aparece en el panel `Problems` con el agente y la función; no sigas hasta resolverlo.

---

## 3. Correr una campaña (experimento `Simulation`)

Es la corrida de un escenario, con animación y tablero en vivo. Sirve para entender el comportamiento; **no** para dimensionar (para eso está el barrido, paso 6).

1. Seleccionar `Simulation: Main` en el árbol.
2. En `Properties`, revisar los parámetros de la corrida (sección `Parameters`):

   | Parámetro | Qué hace | Valor típico |
   |---|---|---|
   | `origenDatos` | De dónde salen las tablas de entrada | `SINTETICO` |
   | `rutaExcel` | Libro `.xlsx` a leer si `origenDatos = EXCEL` | `"datos/entrada_ejemplo.xlsx"` |
   | `idEscenario` | Qué fila de la tabla `Escenario` se aplica | `"E-00"` |
   | `replica` | Número de réplica; cambia la semilla | `0` |
   | `semillaBase` | Semilla base; la corrida usa `semillaBase + replica` | `1` |

   El resto de las palancas (camiones, capacidades, cross dock, estrategia de consolidación, duración) **no** son parámetros de la corrida: salen de la fila del escenario. Para cambiarlas, cambiá el escenario o su fila (paso 5).

3. Correr con `Run` (`F5`).
4. Se abre la ventana de la simulación con el tablero. Controles útiles:
   - `⏸` pausa, `⏹` detiene;
   - la caja `x1` es la velocidad; subila para que la campaña de 183 días termine en segundos;
   - `Ctrl` + rueda del mouse hace zoom sobre el tablero, y arrastrando con el botón derecho se desplaza.
5. La corrida termina sola al día 365 (tiempo de parada del experimento). La **campaña** —producción y pedidos nuevos— dura `duracion_campania_dias` (183 por defecto); los días siguientes existen para que termine de despacharse lo que quedó pendiente, así que los KPIs finales se leen al final de la corrida, no al día 183.

Al pie del tablero hay cinco gráficos; los paneles superiores muestran el estado del día y los acumulados de la campaña. Cada panel está explicado en [Tablero e indicadores](Tablero_e_Indicadores.md).

---

## 4. Correr con datos propios desde Excel

El modelo lee exactamente las mismas tablas, vengan del generador sintético o del libro. Cambiar el origen no cambia la lógica (ADR-038).

1. Partir de la plantilla `datos/entrada_ejemplo.xlsx`, que ya tiene las hojas y los encabezados correctos. **No** empieces un libro desde cero.
2. Reemplazar los valores. Reglas:
   - una hoja por tabla, con el nombre exacto (`Escenario`, `Ubicacion`, `Distancia`, `Producto`, `CapacidadUbicacion`, `TarifaAlmacenamiento`, `TarifaFleteProducto`, `TarifaServicioCarga`, `ProduccionPlan`, `PedidoPlan`);
   - las columnas se buscan **por nombre**, así que podés reordenarlas o agregar columnas propias, pero no renombrar las que el modelo pide;
   - las hojas con `id_escenario` se filtran por el escenario de la corrida: un mismo libro puede tener varios escenarios;
   - las filas vacías se saltean.
3. En `Simulation → Properties`, poner `origenDatos = OrigenDatos.EXCEL` y `rutaExcel` con la ruta al libro (relativa a la carpeta del modelo o absoluta).
4. Correr. Si falta algo, la corrida se detiene **antes del día 1** e informa todos los problemas juntos: hojas faltantes, columnas faltantes, celdas no numéricas con hoja/fila/columna, y después las validaciones de negocio (tarifa faltante, capacidad cero, etc.).

Un libro anterior a `fase-17` **no importa**: la hoja `Producto` pide ahora `toneladas_objetivo_lote_tn` y la hoja `Escenario` pide `cliente_default` y `calidad_default` (ADR-047). Si cambiaste de versión del modelo y tu libro es viejo, la forma rápida de actualizarlo es regenerar la plantilla y volver a cargar tus valores:

```bash
python3 tools/generar_excel_ejemplo.py
```

La plantilla se genera corriendo el propio `GeneradorSintetico` del modelo, así que la plantilla y el contrato no pueden divergir.

---

## 5. Definir o cambiar un escenario

Un escenario es **una fila**: cambiarlo no toca la lógica ni el experimento.

- **Con datos sintéticos:** la fila vive en `GeneradorSintetico.ESCENARIOS` dentro del modelo (clase Java `GeneradorSintetico`). Los escenarios existentes son E-00 a E-11 y están descritos en [Escenarios y experimentos](../09_Definicion/Escenarios_y_Experimentos.md).
- **Con Excel:** la fila vive en la hoja `Escenario` del libro. Agregar un escenario es agregar una fila con un `id_escenario` nuevo.

Palancas disponibles en la fila: `duracion_campania_dias`, `semilla_base`, `variabilidad_produccion`, `variabilidad_demanda`, `pedidos_por_campania`, `toneladas_medias_pedido`, `plazo_pedido_dias`, `camiones_producto`, `camiones_portacontenedor`, `capacidad_camion_tn`, `velocidad_camion_kmh`, `horas_operativas_dia`, `factor_produccion`, `factor_capacidad_planta`, `factor_capacidad_deposito`, `factor_storage`, `ventana_demanda`, `habilita_cross_dock`, `deterministico`, `estrategia_consolidacion`, `cliente_default` y `calidad_default`. El significado de cada una está en el [contrato de datos](../09_Definicion/Contrato_de_Datos.md). El tamaño del lote comercial no es una palanca del escenario: vive en `toneladas_objetivo_lote_tn` de la hoja `Producto`, porque describe al producto (ADR-047).

Para correr un escenario suelto alcanza con poner su `id_escenario` en `Simulation`. Para compararlo con los demás, entra solo al barrido.

---

## 6. Correr el barrido de escenarios (experimento `Escenarios`)

Es lo que responde las preguntas de dimensionamiento, porque corre cada escenario muchas veces y muestra la variabilidad.

1. Seleccionar `Escenarios: Main` en el árbol, botón derecho → `Run` (con `F5` corre el último experimento usado, que suele ser `Simulation`).
2. Se abre el **tablero del barrido**; apretar `Run` (▶) para arrancar las corridas.
3. Corre **14 escenarios × 30 réplicas = 420 corridas** sin animación; tarda alrededor de diez minutos. El estado final tiene que decir `Finished`.
4. El tablero se actualiza mientras corre: avance, medias por escenario y frente de decisión ([cómo se lee](Tablero_e_Indicadores.md#7-tablero-del-barrido-experimento-escenarios)).
5. Al terminar imprime en la consola, por escenario y por KPI: media, desvío, mínimo, máximo, P95 y el delta contra E-00 (absoluto y porcentual).
6. Escribe `resultados/kpis_por_corrida.csv`, una fila por corrida.

Notas importantes:

- **No habilites la evaluación paralela** del experimento: el modelo escribe el CSV desde `After simulation run` y con corridas en paralelo el agente raíz puede no estar disponible.
- La cantidad de réplicas está en la constante `REPLICAS` del experimento; si la cambiás, ajustá también `Number of runs` (= escenarios × réplicas).
- La semilla se calcula dentro del modelo (`semillaBase + replica`), así que cualquier fila del CSV se reproduce corriendo `Simulation` con ese `idEscenario` y esa `replica`.

---

## 7. Leer los resultados

### 7.1 En vivo

El tablero de `Main` durante la corrida (paso 3). Sirve para entender **cómo** se llegó al resultado: cuándo se llena el depósito, cuándo se satura la flota, cuándo empiezan los atrasos.

### 7.2 En el tablero del barrido

La pantalla del experimento `Escenarios` muestra las medias de las réplicas ya terminadas: la tabla por escenario con la configuración que le corresponde (camiones, factor de capacidad de depósito, factor de producción, cross dock, sitio de consolidación) y el frente de decisión, que marca qué escenarios están dominados. Es la lectura rápida; la evidencia sigue siendo el CSV.

### 7.3 El CSV del barrido

`resultados/kpis_por_corrida.csv`, una fila por corrida:

| Columna | Qué es |
|---|---|
| `version_modelo` | Versión del modelo que produjo la fila |
| `id_escenario`, `replica`, `semilla` | Identidad de la corrida; permite reproducirla |
| `costo_total_usd` | Costo de campaña: almacenaje + fletes + consolidación + cross dock |
| `costo_usd_tn` | Costo total sobre toneladas exportadas |
| `nivel_servicio` | Fracción de pedidos entregados en fecha |
| `atraso_promedio_dias` | Atraso medio por pedido, incluyendo los no entregados |
| `utilizacion_flota` | Camión-día consumidos sobre ofrecidos (flota de producto) |
| `utilizacion_portacontenedor` | Utilización del pool de portacontenedores |
| `viajes_planta_deposito` | Viajes de camión de producto en la campaña |
| `uso_posiciones_consolidacion` | Consolidaciones hechas sobre posiciones ofrecidas |
| `toneladas_exportadas` | Toneladas entregadas en terminal |
| `excedente_final_tn` | Stock que queda en la red al cierre; **no** es producto perdido (ADR-048) |
| `toneladas_cross_dock` | Toneladas que cruzaron sin almacenarse |
| `contenedores_exportados` | Contenedores exportados |
| `costo_oportunidad_frio_usd` | Costo de oportunidad del frío propio devengado (fuera de caja) |
| `costo_total_economico_usd` | Caja + oportunidad + penalidad de sobrecarga (ADR-049) |
| `costo_economico_usd_tn` | Costo económico sobre toneladas exportadas |
| `ton_dia_sobre_nominal` | Tonelada-día de planta por encima del nivel nominal |
| `dias_sobrecarga` | Días con la planta por encima del nivel nominal |
| `pico_ocupacion_planta_pct` | Pico de ocupación de la planta en la campaña |

Análisis típico en una planilla o en Python: promediar por `id_escenario`, mirar el desvío entre réplicas y comparar contra E-00. Con 30 réplicas, una diferencia menor al desvío entre réplicas **no** es una diferencia.

---

## 8. Recetas

### Dimensionar la flota

1. Definir escenarios que sólo cambien `camiones_producto` y `camiones_portacontenedor` (hoy: E-01, E-00, E-02).
2. Correr el barrido.
3. Mirar **`nivel_servicio` y `atraso_promedio_dias`**, no el costo: el punto de quiebre es donde agregar camiones deja de mejorar el servicio.
4. Cruzarlo con `utilizacion_flota`: una utilización que baja sin que mejore el servicio es flota de más.

> Con los datos sintéticos actuales, más camiones **aumentan** el costo de caja: el producto llega antes al depósito y paga más almacenaje, mientras lo que espera camión en planta no paga tarifa de caja (ADR-044). Para comparar retener contra tercerizar hay que mirar `costo_total_economico_usd`, que sí le pone precio al frío propio (ADR-049).

### Dimensionar depósitos

1. Usar `factor_capacidad_deposito` (E-03 = 0,5 y E-04 = 2,0) o cambiar `CapacidadUbicacion` en el Excel para dimensionar depósito por depósito.
2. Mirar `ton_dia_sobre_nominal`, `dias_sobrecarga` y `pico_ocupacion_planta_pct`: como la planta ya no descarta producto (ADR-048), el faltante de capacidad de la red se lee ahí, junto con `nivel_servicio`.

### Evaluar el cross docking

1. Comparar E-00 (`habilita_cross_dock = false`) contra E-05 (`true`).
2. Mirar `toneladas_cross_dock`, `costo_total_usd` y `uso_posiciones_consolidacion`.

### Reproducir una corrida rara del CSV

Poner en `Simulation` el `idEscenario` y la `replica` de esa fila y correr con animación.

---

## 9. Herramientas del repositorio

| Comando | Para qué |
|---|---|
| `python3 tools/exportar_modelo.py` | Regenera `model_src/`, el espejo Java legible del `.alp`. Hay que correrlo después de cada cambio del modelo (ADR-035). |
| `python3 tools/generar_excel_ejemplo.py` | Regenera `datos/entrada_ejemplo.xlsx` con el contrato vigente. |
| `tools/VolcarDatos.java` | Vuelca las tablas del generador sintético en texto; lo usa el generador de la plantilla. |

`model_src/*.java` es **generado**: se lee, no se edita. La fuente de verdad es siempre `RedLogistica_Exportacion.alp`.

---

## 10. Problemas frecuentes

| Síntoma | Causa habitual | Qué hacer |
|---|---|---|
| La corrida se detiene antes del día 1 con una lista de errores | Datos de entrada incompletos o inválidos | Corregir el libro o la fila del escenario; el mensaje trae hoja, fila y columna |
| `Falta la tarifa …` | Una tarifa que el modelo necesita no está en las tablas | Agregar la fila; una tarifa faltante no vale cero, aborta a propósito |
| El barrido termina con `root is null` | Evaluación paralela habilitada | Deshabilitarla en las propiedades del experimento |
| Cambié un escenario y no pasó nada | El cambio se hizo en `Simulation` y no en la fila del escenario | Las palancas viven en la tabla `Escenario`, no en la corrida |
| Mi Excel dejó de importar tras actualizar el modelo | El contrato sumó columnas | Regenerar la plantilla y volver a cargar los valores |
| La corrida aborta con "la flota de producto se sobregiró" | Invariante de capacidad diaria roto (V-026) | Es un error del modelo, no de los datos: reportarlo con el escenario y la réplica |
| El tablero se ve vacío al abrir la ventana | La vista quedó fuera del área del tablero | `Ctrl` + rueda para alejar, o correr de nuevo |
