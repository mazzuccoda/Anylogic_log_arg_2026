# Tablero e indicadores

Qué muestra la vista de `Main` durante una corrida, cómo se calcula cada número y cómo se lee. Para el paso a paso operativo, ver el [manual de uso](Manual_de_Uso.md).

El tablero es la lectura **de una corrida**. Las decisiones de dimensionamiento se toman sobre el barrido (`resultados/kpis_por_corrida.csv`), donde cada escenario tiene 30 réplicas: un número del tablero es una realización, no un resultado.

---

## 1. Disposición

![Tablero al final de una corrida del escenario E-00](img/tablero_campania.png)

```
  Campaña        Producción y stock    Transporte y flota   Pedidos y servicio
  Inventario     Contenedores          Consolidación y      Costos
  y reservas     y envíos              cross dock

  [Stock en planta]  [Stock por depósito]  [Utilización de flota]
  [Costos acumulados]  [Estado de los pedidos]
```

Ocho paneles de estado y cinco gráficos de evolución. Debajo del tablero, en el mismo lienzo, están el diagrama de flujo del transporte depósito–terminal y las poblaciones de agentes; se ven alejando el zoom.

---

## 2. Paneles

### Campaña

| Línea | Origen |
|---|---|
| Día actual y horizonte | `time()` y `duracionCampaniaDias` |
| Escenario y réplica | `idEscenario`, `replica` |
| Origen de datos | `origenDatos` |
| Cross dock habilitado | `habilitaCrossDock` |
| Estrategia de consolidación | `estrategiaConsolidacion` |

Es la identidad de la corrida: sin esto, una captura del tablero no se puede reproducir.

### Producción y stock (tn)

Por producto, toneladas **en planta** y **en depósito**, más los lotes comerciales y el excedente.

- Stock: `planta.getStock(producto)` y `stockTotalDepositos(producto)`, ambos derivados de las capas de inventario (ADR-023). No son saldos que se mantengan aparte.
- Lotes comerciales: `lotes.size()` en total y `lotesComercialesAbiertos()` abiertos. Un lote acumula la producción de varios días y se cierra al alcanzar su objetivo (ADR-047), así que el total crece de a poco: una identidad por cada objetivo completado, no una por día de producción. Si los abiertos son más que la cantidad de productos, hay lotes de distinto cliente o calidad conviviendo.
- Excedente: producción que no encontró lugar (`Planta.excedente*`). Si crece, falta capacidad **o** falta transporte; el panel de flota dice cuál de las dos.

### Transporte y flota

| Línea | Cálculo | Cómo se lee |
|---|---|---|
| Camión-día de hoy | `flotaProductoUsadaHoy` sobre `flotaProductoOfrecidaHoy` | Si toca el techo todos los días, la flota de producto es el cuello de botella |
| Utilización de flota de producto | `camionDiaOcupado / camionDiaOfrecido` acumulado | Baja con flota sobrante; alta con flota ajustada |
| Viajes planta-depósito | `viajesPlantaDeposito` | Cada viaje mueve a lo sumo `capacidad_camion_tn` |
| Toneladas transferidas | `toneladasTransferidasDepositos` | Lo efectivamente movido, ya con transferencia parcial |
| Portacontenedores ocupados | `flotaPortacontenedores.busy()` / `.size()` | Es el pool del flujo depósito–terminal |
| Utilización de portacontenedores | `flotaPortacontenedores.utilization()` | Tiempo continuo, no muestreo diario |

Las dos flotas son distintas y se miden distinto (ADR-044): la de producto es capacidad diaria en camión-día; la de portacontenedores es un `ResourcePool` que se toma y se libera dentro del flujo.

### Pedidos y servicio

| Línea | Cálculo |
|---|---|
| Recibidos | `pedidosRecibidos` y `toneladasSolicitadasAcumuladas` |
| Pendientes / reservados | `contarPedidos(PENDIENTE)`, `contarPedidos(RESERVADO)` |
| Entregados / atrasados | `contarPedidos(ENTREGADO)`, `contarPedidos(ATRASADO)` |
| Nivel de servicio | Pedidos entregados con `diasAtraso <= 0` sobre pedidos recibidos |
| Atraso promedio | Suma de `diasAtraso` sobre pedidos recibidos, **incluyendo los no entregados** |
| Exportado | Toneladas entregadas en terminal |

El atraso incluye a los no entregados a propósito: si no, un escenario que no entrega nada tendría atraso cero.

### Inventario y reservas (tn)

Capas vivas, toneladas reservadas y libres en depósito, transferencias y reservas acumuladas. Es la vista del motor de inventario: una capa es `(lote, producto, ubicación, día de ingreso, toneladas, reservas)` y el stock siempre se deriva de ellas. Los invariantes se verifican todos los días y abortan la corrida si se rompen.

### Contenedores y envíos

Contenedores creados, esperando posición de consolidación, exportados; envíos totales, en tránsito y entregados. Un contenedor es una entidad real con la capacidad del tipo que corresponde a su producto; los envíos son los movimientos depósito–terminal.

### Consolidación y cross dock

| Línea | Cómo se lee |
|---|---|
| Consolidaciones | Contenedores estibados |
| Uso de posiciones | `consolidacionesRealizadas / capacidadConsolidacionOfrecida`; cerca de 1 la estiba es el cuello de botella |
| Espera de posición | Contenedor-día esperando cupo; es almacenaje que se paga sin avanzar |
| Cross dock | Operaciones y toneladas que cruzaron sin almacenarse |
| Cross dock reprogramados | Pedidos que no pudieron cruzar ese día (hoy el cruce es todo o nada) |

### Costos (USD)

Almacenaje, flete planta–depósito, flete depósito–puerto, consolidación, cross dock, total de campaña y costo por tonelada exportada. El total es `costoTotalCampania()`, la misma función que alimenta el CSV del barrido: el tablero y el barrido no pueden discrepar.

---

## 3. Gráficos

| Gráfico | Series | Para qué |
|---|---|---|
| Stock en planta | Jugo, cáscara, aceite | Ver si el producto se acumula en planta (falta transporte o falta depósito) |
| Stock por depósito | Los cinco depósitos, todos los productos | Ver si la carga se reparte o se concentra en el más barato |
| Utilización de flota | Flota de producto (%), portacontenedores (%), flota usada hoy (%) | Ver saturación y estacionalidad de la flota |
| Costos acumulados | Almacenaje, ambos fletes, consolidación, cross dock | Ver qué componente domina y desde cuándo |
| Estado de los pedidos | Entregados, atrasados, pendientes, reservados | Ver cuándo se degrada el servicio |

La ventana temporal de los gráficos es `duracionCampaniaDias`: la campaña entera queda a la vista al terminar.

---

## 4. Definición formal de los KPIs del barrido

Los mismos que escribe `resultados/kpis_por_corrida.csv`, todos calculados al cierre de la corrida.

| KPI | Definición | Unidad | Rango |
|---|---|---|---|
| `costo_total_usd` | Almacenaje + flete planta–depósito + flete depósito–puerto + consolidación + cross dock | USD | ≥ 0 |
| `costo_usd_tn` | `costo_total_usd / toneladas_exportadas` | USD/tn | ≥ 0 |
| `nivel_servicio` | Pedidos entregados sin atraso / pedidos recibidos | fracción | 0 a 1 |
| `atraso_promedio_dias` | Suma de días de atraso / pedidos recibidos | días | ≥ 0 |
| `utilizacion_flota` | Camión-día consumidos / camión-día ofrecidos (flota de producto) | fracción | 0 a 1 |
| `utilizacion_portacontenedor` | Utilización del `ResourcePool` de portacontenedores | fracción | 0 a 1 |
| `viajes_planta_deposito` | Viajes de camión de producto | viajes | ≥ 0 |
| `uso_posiciones_consolidacion` | Consolidaciones / posiciones ofrecidas | fracción | 0 a 1 |
| `toneladas_exportadas` | Toneladas entregadas en terminal | tn | ≥ 0 |
| `excedente_final_tn` | Producción que nunca encontró lugar | tn | ≥ 0 |
| `toneladas_cross_dock` | Toneladas que cruzaron sin almacenarse | tn | ≥ 0 |
| `contenedores_exportados` | Contenedores exportados | unidades | ≥ 0 |

---

## 5. Cómo leer el tablero sin equivocarse

1. **El costo no ordena las decisiones por sí solo.** Con el contrato de datos actual la planta no cobra almacenaje y el depósito sí, así que una configuración que deja producto varado en planta parece más barata. El criterio es servicio y atraso; el costo desempata entre configuraciones que sirven igual (ADR-044).
2. **Una corrida no es un resultado.** Salvo E-09 (determinístico), dos réplicas del mismo escenario dan números distintos. Comparar escenarios exige el barrido.
3. **Utilización baja no es una buena noticia.** Es capacidad ociosa; sólo importa junto con el nivel de servicio.
4. **Excedente y espera de posición son diagnóstico, no costo.** Señalan dónde está el cuello de botella: excedente en planta apunta a transporte o depósito; espera de posición apunta a la estiba.
5. **El día importa.** Nivel de servicio y atraso a mitad de campaña todavía no significan nada: hay pedidos con fecha límite por delante.

---

## 6. Limitaciones vigentes del tablero

- Los indicadores del panel son de la corrida en curso; no hay intervalos de confianza en pantalla (están en la salida del barrido).
- No hay costo de almacenaje en planta, así que el excedente en planta no aparece en ningún costo.
- La carga y la descarga no consumen jornada de camión (faltan velocidades operativas en las tablas), así que la utilización de la flota de producto está subestimada.
- La terminal todavía no tiene cola propia ni THC, así que no hay panel de terminal.

---

## 7. Tablero del barrido (experimento `Escenarios`)

El tablero de `Main` responde "qué pasó en esta corrida". El del barrido responde "qué configuración conviene", que es la pregunta del proyecto. Se ve al correr `Escenarios` y se actualiza mientras las 360 corridas avanzan.

![Tablero del barrido al terminar las 360 corridas](img/tablero_barrido.png)

```
  Avance del barrido            Lectura del barrido

  Medias por escenario                        Frente de decisión
```

### Avance del barrido

Corridas terminadas sobre las planificadas (escenarios × `REPLICAS`), barra de progreso, escenario y réplica en curso, escenarios con datos y versión del modelo. Sirve para saber si el barrido está a mitad de camino y de qué escenario son todavía los números en pantalla.

### Lectura del barrido

Tres líneas calculadas sobre las medias: mejor nivel de servicio, menor costo por tonelada y **el más barato entre los que alcanzan 95 % de nivel de servicio**, que es la forma de leer el barrido que evita elegir por costo una configuración que no sirve.

### Medias por escenario

Una fila por escenario con la configuración que lo define y las medias de sus réplicas:

| Columna | Qué es |
|---|---|
| `n` | Réplicas terminadas del escenario |
| `cam` | Camiones de producto / portacontenedores |
| `dep`, `prod` | Factores de capacidad de depósito y de producción |
| `xd`, `cons` | Cross dock habilitado; sitio de consolidación (`dep` o `term`) |
| `costo USD`, `+-costo` | Media y desvío muestral del costo total de campaña |
| `USD/tn`, `serv`, `atraso` | Costo por tonelada exportada, nivel de servicio y atraso medio |
| `utFlo`, `utPor` | Utilización de la flota de producto y del pool de portacontenedores |
| `exced tn` | Excedente final |
| `vs E-00` | Diferencia porcentual de costo total contra el caso base |

El desvío está al lado de la media a propósito: **una diferencia de costo menor que el desvío entre réplicas no es una diferencia**. En E-09 (determinístico) el desvío es exactamente 0, lo que hace de ese escenario la prueba de regresión del barrido.

### Frente de decisión

Para cada escenario, dos barras relativas al rango observado en el barrido —más llena es mejor en las dos columnas— y el veredicto: un escenario está **dominado** si otro da a la vez mejor nivel de servicio y menor costo por tonelada, y es **eficiente** si nadie lo domina. Las barras son relativas y no absolutas porque con escala 0–100 % todas las configuraciones se ven iguales.

Dos advertencias que el panel repite en pantalla:

- sólo son comparables escenarios con la misma producción y la misma demanda; E-06 (+30 %) y E-07 (−30 %) cambian la escala del problema y aparecen como eficientes por eso, no por ser mejores decisiones;
- el costo por tonelada hereda la limitación del contrato de datos: la planta no cobra almacenaje.

Todos los números del tablero salen de las mismas corridas que el CSV: son medias de `corridas`, la misma colección que se escribe en `resultados/kpis_por_corrida.csv` (ADR-046).
