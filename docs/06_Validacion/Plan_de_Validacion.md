# Plan de validación

[← Volver al índice](../README.md)

## 1. Objetivo

Verificar que cada módulo represente correctamente las reglas de negocio y que la migración no rompa el modelo funcional existente.

## 2. Estrategia

Cada fase debe superar cuatro niveles:

1. compilación;
2. prueba unitaria funcional;
3. reconciliación de saldos y costos;
4. prueba integrada end-to-end.

## 3. Principios

- usar casos pequeños y determinísticos antes de escenarios largos;
- controlar semilla aleatoria cuando corresponda;
- validar balances antes de indicadores;
- comparar lógica anterior y nueva durante la transición;
- no aceptar resultados solo porque el modelo termina sin error.

## 4. Casos mínimos

### V-001 Producción sin restricciones

**Datos:** capacidad superior a la producción de cinco días.

**Esperado:**

- producido acumulado correcto;
- stock igual a producido;
- ocupación por debajo del nivel nominal y sobrecarga cero;
- saldo físico reconciliado.

### V-002 Producción por encima de la capacidad nominal (ADR-048)

**Esperado:**

- la producción del plan ingresa **completa**: no hay descarte ni merma por capacidad;
- el stock puede superar el 100 % de la capacidad nominal;
- se acumulan `tonDiaSobreNominalPlanta`, `diasSobrecargaPlanta` y el pico de ocupación;
- por encima del umbral crítico se acumula además `tonDiaSobreCriticoPlanta`;
- el lote registra toda la producción del día.

**Ejecutado:** 2026-07-27, modelo `fase-19`, barrido de 390 corridas. **Resultado: cumple.** Ningún escenario pierde producto y E-01, el de flota mínima, acumula 452 tn-día sobre el nivel nominal con un pico de 104,3 % de ocupación; el resto queda en 100 %.

### V-002b Costo de caja y costo económico (ADR-049)

**Esperado:**

- con `oportunidad_usd_tn_dia = 0` y `penalidad_sobrecarga_usd_tn_dia = 0`, el costo económico es exactamente igual al de caja;
- con tarifas positivas, `costo_total_economico_usd >= costo_total_usd` en toda corrida;
- el costo de caja no incluye ningún devengo de oportunidad.

**Ejecutado:** 2026-07-27, modelo `fase-19`. **Resultado: cumple.** En E-00, caja USD 1,57 M contra económico USD 2,20 M, con la diferencia igual al devengo de frío propio reportado en `costo_oportunidad_frio_usd`.

### V-003 Lote comercial acumulativo

**Datos:** lote objetivo 500 tn, producción 100 tn/día.

**Esperado al día 5:**

- producido 500 tn;
- un solo lote comercial;
- cinco registros diarios no deben crear cinco identidades comerciales.

**Ejecutado:** 2026-07-27, modelo `fase-17`, determinístico (una sola serie de ingresos, sin variabilidad). **Resultado: cumple.**

| Comprobación | Esperado | Obtenido |
|---|---|---|
| identidades comerciales creadas | 1 | 1 |
| producción acumulada (`toneladasIniciales`) | 500 tn | 500 tn |
| saldo físico del lote (`Inventario.stockLote`) | 500 tn | 500 tn |
| capas de inventario | 5 (una por día) | 5 |
| estado comercial al día 5 | `CERRADO` | `CERRADO` |
| `Inventario.validar()` | sin errores | sin errores |

Las cinco capas comparten `idLote` y se diferencian por `diaIngreso`: la identidad comercial es una y los registros diarios son cinco, que es exactamente lo que el caso pide distinguir.

### V-004 Despacho parcial antes del cierre

**Datos:** producido 150 tn, despacho 25 tn.

**Esperado:**

- despachado 25;
- disponible 125;
- lote continúa abierto;
- producción posterior sigue sumando al mismo lote.

**Ejecutado:** 2026-07-27, modelo `fase-17`, determinístico. **Resultado: cumple.**

| Comprobación | Esperado | Obtenido |
|---|---|---|
| mismo lote tras dos días de producción | sí | sí (`idLote` 1 y 1) |
| producción acumulada antes del despacho | 150 tn | 150 tn |
| despachado | 25 tn | 25 tn |
| disponible tras el despacho | 125 tn | 125 tn |
| estado comercial tras el despacho | `ABIERTO` | `ABIERTO` |
| producción posterior (100 tn) en el mismo lote | sí | sí (`idLote` 1) |
| producción acumulada final | 250 tn | 250 tn |
| disponible final | 225 tn | 225 tn |
| `Inventario.validar()` | sin errores | sin errores |

El despacho reduce las capas y no toca `toneladasIniciales`: por eso la producción acumulada llega a 250 tn mientras el saldo físico queda en 225 tn. Es la separación que hace que el despacho parcial no cierre el lote (ADR-047).

**Cómo se ejecutan V-003 y V-004:** el cuerpo de `Main.crearLoteEnPlanta()` vive en un agente y no compila fuera de AnyLogic, así que los dos casos se corren sobre las clases reales `Inventario`, `Capa` y `DatosEntrada` del espejo `model_src/`, con una copia fiel de ese cuerpo y de `buscarLoteComercialAbierto()`. Es la única forma de fijar producción y despacho exactos sin depender del sorteo de pedidos de la campaña. Si el cuerpo de la función cambia, el caso hay que volver a correrlo.

### V-005 Múltiples ubicaciones

**Datos:** 75 tn planta, 50 tn depósito, 25 tn contenedor.

**Esperado:** suma física 150 tn y una sola identidad de lote.

### V-006 Transferencia parcial

**Datos:** lote con 100 tn en planta, mover 30 tn.

**Esperado:**

- planta 70;
- depósito 30;
- costo aplicado sobre 30;
- identidad sin cambio.

### V-007 Transferencia fallida

**Datos:** depósito sin capacidad.

**Esperado:**

- no cambia ningún saldo;
- no se registra costo;
- retorno `false`;
- motivo visible.

### V-008 Reserva completa

**Datos:** pedido 80 tn y saldo libre 100 tn.

**Esperado:** reservado 80, libre 20, pedido `RESERVADO`.

### V-009 Reserva insuficiente (reserva incremental, ADR-024)

**Datos:** pedido 120 tn, saldo libre 100 tn, capacidad de contenedor 25 tn.

**Esperado:**

- se reservan 100 tn;
- 4 contenedores quedan ejecutables y el quinto espera producción;
- el pedido queda `PARCIALMENTE_RESERVADO`, no revertido;
- al producirse 20 tn más, la reserva se completa sin intervención manual.

### V-010 Cantidad de contenedores

- 50 tn de jugo a 25 tn/cont → 2.
- 51 tn → 3.
- 0 tn → error de validación del pedido.

### V-011 Último contenedor parcial

**Datos:** 52 tn, capacidad 25.

**Esperado:** 25 + 25 + 2 tn.

### V-012 Consolidación en depósito

**Esperado:** OUT, consolidación, ciclo, terminal, THC y despachante aplicados una vez.

### V-013 Cross docking

**Esperado:**

- operación empieza cuando ambos camiones están presentes;
- primero puede esperar;
- debe ocurrir el mismo día;
- no se aplica IN, storage ni OUT.

### V-014 Mismo portacontenedor

Verificar que el recurso permanezca ocupado desde retiro vacío hasta ingreso cargado.

### V-015 Fecha límite (ADR-027)

Un plan que finaliza después del día límite se ejecuta igual y registra el atraso en días. Verificar que el pedido no quede bloqueado y que el atraso aparezca en el KPI de nivel de servicio.

### V-016 Tarifa faltante

El plan debe quedar no factible. Nunca costo cero silencioso.

### V-017 Reconciliación de costos

```text
Costo pedido = suma de costos reales de sus contenedores y movimientos asociados
```

### V-018 Reconciliación de inventario

```text
Producido = físico + reservado/despachado
```

Desde ADR-048 no hay término de merma: la planta no descarta producto, así que todo lo producido tiene que estar en la red o exportado.

### V-019 Imputación de almacenaje por capas (ADR-021, ADR-022)

**Datos:** un lote ingresa 30 tn al día 4 y 50 tn al día 9 al mismo depósito; se retiran 40 tn al día 20; tarifa 0,10 USD/tn/día.

**Esperado:**

- el retiro consume FIFO: 30 tn de la capa del día 4 y 10 tn de la del día 9;
- el storage devengado se calcula por capa, no con una fecha única de ubicación;
- saldo remanente 40 tn en la capa del día 9.

### V-020 Stock derivado (ADR-023)

**Esperado:** en cualquier instante, `stock de la ubicación = suma de las capas de todos los lotes en esa ubicación`, sin excepción y sin necesidad de una rutina de reconciliación.

### V-021 Recursos y esperas (ADR-019)

**Datos:** demanda de 5 viajes en un día con 3 camiones disponibles.

**Esperado:**

- 3 viajes ejecutados, 2 pospuestos al día siguiente;
- 2 registros en `esperas_recursos.csv` con causa `CAMION_PRODUCTO`;
- ningún viaje se pierde ni se duplica.

### V-022 Reproducibilidad

**Esperado:** dos corridas del mismo escenario con la misma semilla producen salidas idénticas byte a byte; con variabilidades en cero, el resultado es independiente de la semilla.

### V-023 Almacenaje sin doble conteo (H-04)

**Esperado:** el almacenaje total del día calculado por depósito coincide con la suma del imputado a cada lote, incluidos los lotes reservados. Hoy divergen porque el cálculo por lote excluye el estado `RESERVADO` y el agregado por depósito no.

### V-024 Independencia del orden de eventos (H-06, H-07)

**Esperado:** ningún evento tiene cadencia fraccionaria y la corrida completa produce los mismos resultados tras reordenar la creación de los eventos en el modelo. Verifica que la secuencia diaria sea la de ADR-034 y no el orden interno del motor.

### V-025 Presupuesto de agentes de PLE (ADR-020)

**Esperado:** el escenario más cargado termina sin alcanzar los 50 000 agentes creados dinámicamente. El total creado se registra en el resumen de cada corrida.

### V-026 Capacidad diaria de flota (ADR-044)

**Esperado:** en ningún día `flotaProductoUsadaHoy` supera `flotaProductoOfrecidaHoy`; `tomarFlotaProducto()` aborta la corrida si ocurre. Verificado en el barrido completo: 360 corridas sin abortos. La utilización de las dos flotas queda entre 0 y 1: medido 0,067..0,411 (producto) y 0,057..0,310 (portacontenedores).

### V-027 Circuito físico por envío (ADR-050)

**Esperado:** cada envío recorre los bloques del circuito que le corresponde y ninguno los de otro. Verificado en el barrido completo de `fase-21` (14 escenarios × 30 réplicas = 420 corridas, todas `Finished`, sin abortos ni excepciones), leyendo los contadores por circuito del CSV como medias de 30 réplicas:

| Escenario | Circuito | planta | depósito | cross dock | terminal | viajes a granel | `utilizacion_portacontenedor` |
|---|---|---:|---:|---:|---:|---:|---:|
| E-00 | consolidación en depósito | 0 | 529,9 | 0 | 0 | 0 | 0,13 |
| E-05 | cross docking habilitado | 0 | 235,2 | 294,8 | 0 | 0 | 0,11 |
| E-11 | consolidación en terminal | 0 | 0 | 0 | 530,0 | 529,9 | **0,00** |
| E-13 | consolidación en planta | 528,9 | 1,1 | 0 | 0 | 0 | 0,15 |

Las dos verificaciones que exige ADR-050 se leen directamente de esa tabla: el circuito de terminal **no** consume el pool de portacontenedores (0 % de utilización con 530 contenedores exportados) y los circuitos que sí lo usan pagan el tramo vacío en ocupación (E-00 sube de 0,11 en `fase-19` a 0,13 en `fase-21`, con los mismos 494 contenedores y el mismo costo). En E-13 el 0,2 % que sale por depósito son los pedidos cuyo producto ya había sido transferido: el circuito se resuelve por el sitio real del stock, no por la política.

### V-028 El round trip se paga cuando el pool es escaso (ADR-050)

**Esperado:** al hacer real el tramo vacío, la capacidad efectiva del pool baja y eso se ve en servicio, no en costo. Medido sobre 30 réplicas, E-01 (1 camión de producto, 1 portacontenedor) pasa de 0,98 a 0,77 de nivel de servicio y de 0,49 a 2,06 días de atraso, con costo idéntico; los escenarios con pool holgado no se mueven. Es la evidencia de que el tramo vacío es un consumo de recurso y no un delay decorativo.

### V-029 La migración del contrato de costos no mueve ningún número (ADR-051)

**Esperado:** reemplazar las fórmulas cableadas por tarifas con unidad, proveedor y vigencia no cambia ningún resultado, porque los valores sintéticos están calibrados para reproducir la línea base. **Medido:** barrido completo de 420 corridas (14 escenarios × 30 réplicas, todas `Finished`) comparado fila por fila contra `fase-21`; las 26 columnas de KPI coinciden en las 420 filas, con una única diferencia de 1 × 10⁻⁴ USD en `costo_total_economico_usd` de E-00 réplica 29, que es orden de suma de punto flotante. Si alguna otra columna se hubiera movido, la migración habría perdido o duplicado un devengo.

**Esperado (falta de cobertura):** una tarifa que no cubra algún día de la campaña aborta la corrida indicando clave y día, en lugar de devolver 0. Verificado por construcción en `DatosEntrada.tarifaFlete/tarifaRoundTrip/tarifaSitio/tarifaEspera`, que también abortan si dos filas están vigentes a la vez para la misma clave.

### V-030 El registro de cargos reconcilia con los acumuladores (ADR-052)

**Esperado:** el total de cada categoría en `RegistroCostos` coincide con el acumulador del agente que la devenga, todos los días y al cierre, con tolerancia de 0,01 USD; si no, la corrida aborta. **Medido:** `reconciliarCostos()` corre en la secuencia diaria y antes de escribir los KPIs de cada corrida del barrido; las 420 corridas terminaron `Finished`, es decir sin una sola diferencia. Además `costoTotalCampania()` sale del registro (`total(CAJA)`) y el económico de `total()`, así que pantalla, CSV y registro no pueden discrepar.

**Encontrado con este caso:** la primera versión usaba `día|lote|ubicación|producto` como clave de idempotencia del almacenaje, que no distingue dos capas del mismo lote en el mismo depósito; sólo la primera capa pagaba y el costo de almacenaje caía a un tercio. La comparación contra `fase-21` lo detectó en la primera corrida y se corrigió dándole identidad propia a la capa (`Capa.idCapa`).

## 4.2 Costeo por circuito (C3/C4, ADR-053)

**Cómo se ejecutan.** Los diez casos no son corridas aparte: son propiedades que el modelo verifica solo, en cada envío y en cada corrida. `Main.costoEsperadoCircuito(envio)` reconstruye lo que el circuito **debe** pagar leyendo las tarifas —sin mirar el registro— y `finalizarEnvio()` aborta la corrida si lo devengado no coincide con eso. La evidencia que se cita abajo es el barrido completo de `fase-22`: 14 escenarios × 30 réplicas = 420 corridas, todas `Finished`, con unos 530 envíos auditados por corrida (≈ 222 000 auditorías) y `reconciliarCostos()` corriendo todos los días.

La descomposición por categoría sale del CSV (medias de 30 réplicas, USD):

| Escenario | circuito | flete prod. | round trip | consol. | cross dock | almacenaje | IN | OUT | THC | terminal | despachante | espera |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| E-00 | depósito | 257 264 | 232 000 | 118 795 | 0 | 969 700 | 51 965 | 30 055 | 105 727 | 44 583 | 63 584 | 0 |
| E-05 | cross dock | 251 730 | 226 078 | 52 744 | 56 768 | 959 527 | 35 971 | 14 065 | 105 740 | 44 588 | 63 592 | 0 |
| E-11 | terminal | 377 956 | **0** | 167 358 | 0 | 967 331 | 51 965 | 30 058 | 105 740 | 44 588 | 63 592 | 0 |
| E-13 | planta | 126 227 | 254 286 | 111 874 | 0 | 1 047 654 | 23 646 | 54 | 105 740 | 44 588 | 63 592 | 0 |

### V-COST-01 Consolidación en planta

**Esperado:** el envío paga round trip terminal–planta–terminal, consolidación en planta, THC, costo terminal, despachante y espera; **no** paga flete planta→depósito, IN, almacenaje de terceros ni OUT.

**Medido:** los 528,9 envíos de circuito planta de E-13 pasan la auditoría en las 30 réplicas. `pagaOutDeposito()` devuelve `false` cuando el origen no es un depósito, así que el egreso no entra en el esperado ni en el devengado; los 54 USD de OUT y el almacenaje de E-13 son del producto que **no** salió por planta: el frío propio desborda y esas toneladas se transfieren a depósitos, donde pagan IN y almacenaje aunque nadie las despache. Es física de `fase-21`, no un cargo del circuito: los KPIs físicos de E-13 no se movieron.

### V-COST-02 Consolidación en depósito

**Esperado:** flete de producto al depósito, IN, almacenaje diario, OUT, round trip terminal–depósito–terminal, consolidación en el depósito, THC, costo terminal, despachante y espera.

**Medido:** E-00 devenga las once categorías (fila de la tabla). El flete planta→depósito y el almacenaje se devengan cuando ocurren, contra el **lote**, no contra el envío: por eso no entran en `costoEsperadoCircuito()`, que audita lo que el envío paga desde que se decide su circuito. La vista incremental (`costoIncrementalPedido`) es la que compara alternativas y no vuelve a incluir esos costos ya incurridos; la histórica (`costoHistoricoPedido`) los reporta aparte para poder explicarlos.

### V-COST-03 Cross docking en depósito

**Esperado:** flete al punto de cross dock, round trip, tarifa de cross dock por contenedor, THC, costo terminal, despachante y espera. En el sitio de cross dock, dentro de la ventana de operación, no se cobra IN, ni almacenaje, ni OUT.

**Medido:** en E-05 el cargo se registra en la categoría `CROSS_DOCK` (56 768 USD) y no en `CONSOLIDACION`, y las toneladas que cruzan no pagan ingreso ni egreso: IN cae de 51 965 a 35 971 USD (−31 %) y OUT de 30 055 a 14 065 (−53 %) contra E-00, con la misma producción y las mismas toneladas exportadas. El almacenaje baja apenas (969 700 → 959 527) porque lo que cruza es una fracción del stock que igual se acumula.

### V-COST-04 Consolidación/cross dock en terminal

**Esperado:** flete planta→terminal o depósito→terminal, consolidación en terminal por contenedor, THC, costo terminal y despachante; **sin** round trip de portacontenedor y sin almacenaje temporal en terminal.

**Medido:** E-11 da `costo_round_trip_usd` = 0,00 exacto en las 30 réplicas, con 530 contenedores exportados y 530 viajes a granel, y es el escenario con más flete de producto (377 956 USD contra 257 264 de E-00): el producto viaja a granel y paga ese flete, no un ciclo que no ocurre. La consolidación es la más cara por contenedor (315,8 USD contra 224,2 en E-00) porque la tarifa de terminal es mayor que la de depósito. En este circuito el contenedor recién existe en la terminal, así que THC y costo terminal se devengan al consolidar y no al ingresar: el día del devengo queda guardado en `Envio.diaCargosTerminal` y la auditoría usa ese día para elegir la tarifa vigente.

### V-COST-05 Último contenedor parcial

**Esperado:** un contenedor que carga menos que su capacidad paga consolidación, cross dock, THC, costo terminal, despachante y round trip como contenedor **completo**; sólo el flete de producto y el almacenaje se cobran por tonelada.

**Medido:** todos los cargos por contenedor usan `contenedoresNecesarios()`, que redondea para arriba, y la unidad registrada es `USD_CONTENEDOR` con `cantidad` = contenedores. En E-00 el THC medio es 199,51 USD por contenedor y el despachante 119,98, contra tarifas de 220/150/190 y 120 por contenedor según producto: la mezcla, no la tonelada. El efecto del contenedor parcial se lee en la consolidación: 118 795 USD sobre 12 737 tn son 9,33 USD/tn efectivos contra 9,00 USD/tn de tarifa equivalente (225 USD ÷ 25 tn), es decir un 3,6 % de sobrecosto que es exactamente el hueco de los contenedores que salieron con 24,0 tn en lugar de 25.

### V-COST-06 Transferencia entre depósitos

**Estado:** pendiente por decisión del usuario ("hoy no existe el movimiento"). La fórmula queda documentada —OUT del depósito de origen, flete entre depósitos e IN en el destino, sin cargos de contenedor— y se puede evaluar a mano con `DatosEntrada.outUsdTn`, `importeFlete` e `inUsdTn`, que ya resuelven por día y sitio. No se implementa el movimiento físico, así que el modelo **no** devenga esta combinación y ninguna corrida la ejercita.

### V-COST-07 No duplicación

**Esperado:** ningún concepto se cobra dos veces por el mismo evento; en particular THC, costo terminal y despachante, una vez por contenedor (o por pedido, si la unidad es `USD_PEDIDO`).

**Medido:** tres candados encadenados. (1) `RegistroCostos.registrar()` es idempotente por operación, categoría, unidad y motivo, y devuelve 0 si el cargo ya estaba. (2) `costoEsperadoCircuito()` cuenta cada concepto una sola vez, así que un devengo repetido rompe la igualdad y aborta la corrida. (3) El despachante con unidad `USD_PEDIDO` lo paga sólo el primer envío entregado del pedido, y la auditoría replica esa regla. En el barrido, THC/contenedor es 199,51 en los cuatro circuitos y despachante/contenedor 119,98 en todos: si un circuito cobrara de más, la relación se movería. Las 420 corridas terminaron sin abortos.

### V-COST-08 Almacenaje de reservas de cross dock

**Esperado:** el producto reservado para cruzar no paga almacenaje en el sitio de cross dock durante la ventana de operación; si el cross dock se degrada y la mercadería queda como stock normal, ahí sí paga IN y almacenaje.

**Medido:** `transferirToneladasLote(..., cruza)` no devenga IN cuando el producto cruza, y el degradado sí lo devenga cuando la mercadería termina como stock. E-05 es el único escenario con cross dock y es el único con IN y OUT por debajo de E-00, con las mismas toneladas exportadas y el mismo nivel de servicio.

### V-COST-09 Reconciliación

**Esperado:** el total de cada categoría en el registro coincide con el acumulador que la devenga, y el costo del pedido con la suma de sus envíos.

**Medido:** `reconciliarCostos()` corre todos los días y antes de escribir los KPIs de cada corrida, con tolerancia de 0,01 USD, y abarca las ocho categorías nuevas de C3. Las 420 corridas terminaron `Finished`. Además `costoTotalCampania()` sale de `registro.total(CAJA)`, así que pantalla, CSV y registro no pueden discrepar: el panel de costos de `Main` muestra la descomposición completa y suma al total.

### V-COST-10 Comparación manual

**Esperado:** los circuitos se pueden comparar categoría por categoría y la diferencia se explica.

**Medido:** la tabla de arriba es esa comparación. Contra `fase-21`, el costo de campaña sube entre 11,8 % y 37,2 % según escenario, y **todos** los KPIs físicos y de servicio quedan idénticos fila por fila en las 420 corridas: nivel de servicio, atraso, toneladas exportadas, contenedores, viajes, contadores por circuito y sobrecarga, con diferencia máxima 0. Las dos únicas columnas físicas que se mueven son las utilizaciones de flota y de pool, y sólo en E-05 (máximo 0,0007), porque la elección del sitio de cross dock se hace por costo estimado y ahora ese estimado incluye IN, OUT y los cargos por contenedor: el modelo elige otro depósito en algunas réplicas, con las mismas toneladas y el mismo servicio.

| Escenario | `fase-21` USD/tn | `fase-22` USD/tn | Δ costo total |
|---|---:|---:|---:|
| E-00 depósito | 124,4 | 148,0 | +19,1 % |
| E-05 cross dock | 122,0 | 143,0 | +17,4 % |
| E-07 sin capacidad | 59,8 | 82,0 | +37,2 % |
| E-11 terminal | 127,8 | 142,8 | +11,8 % |
| E-13 planta | 121,3 | 140,3 | +15,7 % |

El aumento es explicable en su totalidad: los cargos nuevos son IN + OUT + THC + costo terminal + despachante (≈ 296 000 USD por corrida en E-00) más el sobrecosto del contenedor parcial en consolidación. `fase-21` deja de ser comparable en costo y sigue siendo la línea de base física.

**Espera en 0.** Las tarifas de espera están cargadas como supuesto (franquicia 3 h, 25 USD/h), y `costo_espera_usd` es 0 en las 420 corridas: con las velocidades sintéticas un contenedor se carga en menos de una hora, así que nunca se supera la franquicia. El concepto se devenga —`registrarEspera()` corre en la carga, en la descarga y en la terminal— y se activa solo cuando los tiempos reales o la franquicia real lo pidan.

## 4.3 Decisión de circuito (C5/C6, ADR-054)

Los casos que siguen se verificaron sobre el barrido `fase-23`: 36 escenarios × 30 réplicas = 1 080 corridas, todas `Finished`.

### V-DEC-01 Las políticas fijas no cambian

**Esperado:** activar el evaluador no puede mover un solo número de los escenarios que no lo usan.

**Medido:** las 420 corridas de E-00 a E-13 de `fase-23` se compararon fila por fila contra `fase-22`, por `id_escenario` y `réplica`, en las 37 columnas comunes: **idéntico, sin una sola diferencia**. En los 32 escenarios de política fija los cinco KPIs de decisión son 0, que es la comprobación de que la asignación no pasa por el evaluador.

### V-DEC-02 Nunca se ejecuta una alternativa no factible

**Esperado:** una alternativa descartada no puede llegar al flujo físico.

**Medido:** `ordenarAlternativas()` construye la lista de candidatas filtrando `factible`, y es la única fuente de la elegida. La ejecución es la que ya existía —reservar contra el origen o cruzar por el depósito—, así que una alternativa que el flujo no puede tomar falla ahí y se marca `el flujo no pudo tomarla al ejecutar` en lugar de mover producto. En las 120 corridas con evaluador, `pedidos_sin_alternativa_factible` es 0 y `Inventario.validar()` cierra todos los días.

### V-DEC-03 El servicio manda sobre el costo

**Esperado:** mientras exista una alternativa que llega a tiempo, no se elige una tardía por barata.

**Medido:** el comparador ordena por `llegaATiempo` antes que por costo cuando `servicio_minimo_proyectado > 0`. E-14 entrega el 100 % de los pedidos en fecha (30/30 réplicas) contra 98,7 % de E-00, con `planes_tardios` en 0.

### V-DEC-04 Selección por menor costo incremental

**Esperado:** con `MENOR_COSTO_INCREMENTAL_FACTIBLE` la campaña no puede salir más cara que con el circuito fijo, a igual demanda servida.

**Medido:** E-14 sale 9,11 USD/tn más barato que E-00 (−6,2 %) y lo hace **en las 30 réplicas**, con desvío del pareado 1,47 y las mismas toneladas exportadas. El reparto muestra que la decisión es por pedido y no global: 267 contenedores desde planta, 214 desde depósito y 49 armados en terminal, contra 530 todos desde depósito en E-00.

### V-DEC-05 Selección por menor costo end-to-end

**Esperado:** la vista end-to-end existe y decide distinto que la incremental, porque suma el costo hundido.

**Medido:** E-15 elige 519 contenedores desde planta y prácticamente ninguno desde depósito: el almacenaje ya pagado empuja a despachar desde donde está el stock. Sale 7,44 USD/tn más barato que E-00 pero **pierde 7,8 puntos de nivel de servicio**, que es la evidencia de por qué la decisión táctica se toma con la incremental y no con esta.

### V-DEC-06 Prioridad del frío propio

**Esperado:** `PRIORIDAD_FRIO_PROPIO` antepone el despacho desde planta y el costo sólo desempata.

**Medido:** E-16 coincide con E-15 en 29 de las 30 réplicas en todas las columnas. No es que compartan código —el comparador de E-16 ordena por origen y el de E-15 por costo end-to-end— sino que con estas tarifas el costo hundido lleva casi siempre a la misma elección que la regla de frío propio.

### V-DEC-07 Las alternativas descartadas quedan registradas

**Esperado:** el plan conserva lo que se evaluó y por qué se descartó.

**Medido:** `PlanLogistico` guarda la lista completa de `AlternativaCircuito` con `factible` y `motivoNoFactible`, y el barrido publica los agregados: en E-14, 18 alternativas evaluadas por pedido y 574 descartadas por campaña (80 %). Los motivos son los ocho de factibilidad —stock libre, depósito no habilitado, cupo de cross dock, espacio de paso, flota de producto, flota de granel y capacidad de estiba— más la transferencia depósito→depósito, que se genera siempre descartada con `sin movimiento fisico en el modelo (C7)`.

### V-DEC-08 El plan elegido es el que se ejecuta

**Esperado:** el circuito que el plan promete es el que la cadena hace y el que se cobra.

**Medido:** `confirmarAsignacion()` escribe `Pedido.estrategiaSeleccionada`, que es lo que lee `seleccionarCircuito` en el flujo, y los contadores por circuito del CSV se corresponden con el reparto de planes. Además la auditoría por envío de ADR-053 sigue activa en las 120 corridas con evaluador: si el plan mandara un pedido por un circuito y se cobrara otro, `costoEsperadoCircuito()` abortaría la corrida.

### V-DEC-09 Los factores de sensibilidad se cobran

**Esperado:** cambiar `factor_tarifa_*` cambia el costo de campaña en la proporción esperada, y no sólo la cotización del evaluador.

**Medido:** el primer barrido abortó en E-20 con `round trip del envio 1: registro 390,0 contra 312,0`, porque el devengo leía el campo crudo de la tarifa y la auditoría el accesor con el factor. Desde ADR-054 punto 8 el devengo usa los mismos accesores. Resultado en `fase-23`: E-18/E-19 (flete ±20 %) dan 142,8 y 152,0 USD/tn contra 148,0 de E-00, E-20/E-21 (round trip) 144,3 y 151,6, E-22/E-23 (cross dock, sobre E-05) 142,1 y 143,9, y E-24/E-25 (terminal) 144,6 y 151,3.

### V-DEC-10 Excel y sintético siguen dando lo mismo

**Esperado:** las columnas nuevas del contrato no separan las dos rutas de carga.

**Medido:** E-00 corrido desde `datos/entrada_ejemplo.xlsx` da 1 970 450 USD de costo de campaña, 11 921 tn exportadas, 494 contenedores, 1 048 viajes y almacenaje 1 109 754 USD, exactamente los mismos valores que la corrida sintética de la misma réplica.

## 4.4 Pedidos parciales y transferencia preventiva (ADR-055/056)

Los casos P, T, E y C se verifican **dentro de la corrida**, no a mano: C-01 y C-02 son invariantes diarios que abortan la campaña si se rompen, y los demás se leen en los KPIs por corrida del barrido `fase-24` (1 080 corridas, 36 escenarios × 30 réplicas).

### P-01 a P-04 Un pedido se cubre con varios orígenes

**Esperado:** un pedido que no cabe en ningún sitio individual se sirve desde varios, con una asignación por origen, y se entrega completo.

**Medido:** `pedidos_multi_origen` y `asignaciones_parciales` son > 0 en los 36 escenarios; en E-05 (cross dock) hay 16,2 pedidos multi-origen y 53,4 asignaciones parciales por corrida, y en E-34 78,5 asignaciones para 40 pedidos. Las toneladas exportadas y `excedente_final_tn` no cambian respecto de `fase-23` en E-00, E-05, E-12 y E-14: el producto no se duplica ni se pierde, cambia de dónde sale.

### P-02 El saldo no cubierto sigue siendo demanda

**Esperado:** reservar lo que hay y conservarlo, no liberar la reserva porque falte el resto.

**Medido:** `pedidos_parcialmente_reservados` cuenta los pedidos que estuvieron comprometidos a medias en algún momento (marca histórica: al cierre de campaña todos los pedidos cerraron, así que el estado final no lo mide). `toneladas_pendientes_asignar` es 0 al cierre en todos los escenarios: ningún saldo queda huérfano.

### E-01 El último contenedor parcial es condicionado

**Esperado:** un pedido de 62 tn con contenedores de 25 tn arma 25 + 25 y el parcial de 12 sólo cuando el pedido está completamente asignado, venció o terminó la campaña.

**Medido:** `contenedores_exportados` no cambia respecto de `fase-23` en los escenarios de política fija, es decir que la contenedorización progresiva no genera contenedores extra a medio llenar. Es la regla que hace que el cambio no encarezca el despacho: en el contrato un parcial paga como uno lleno (ADR-053).

### E-02 Vencimiento con entrega parcial

**Esperado:** un pedido con parte entregada al llegar la fecha límite queda `ATRASADO` con el saldo pendiente, conservando lo entregado y el origen de cada fracción.

**Medido:** `pedidos_atrasados_entrega_parcial` es 0,4 por corrida en E-00 y 10,2 en E-31 (capacidad de frío reducida), donde antes esos pedidos figuraban atrasados sin ninguna entrega. El atraso promedio de E-31 **baja** de 33,5 a 30,0 días: la entrega parcial adelanta parte del pedido.

### C-01 Identidad del pedido

**Esperado:** `solicitado = pendiente de asignar + reserva activa + despachado no entregado + entregado`, tolerancia 0,0001 tn.

**Medido:** `validarBalancePedidos()` corre todos los días de las 1 080 corridas y aborta si difiere. Las 1 080 terminaron `Finished`.

### C-02 Nada de lo producido se pierde

**Esperado:** `producido = stock en planta + stock en depósitos + en proceso + entregado`.

**Medido:** `validarBalanceProducido()` corre todos los días sobre la producción acumulada de la planta. Las 1 080 corridas pasan.

### T-01 y T-02 Componente preventivo

**Esperado:** con el proyectado sobre el umbral de alerta, transferir hasta el objetivo; debajo de la alerta, cero.

**Medido:** `toneladas_transferidas_preventivas` es > 0 en los 35 escenarios con política `FLEXIBLE` (23 181 tn por corrida en E-00) y exactamente **0 en E-12**, el escenario `REACTIVA`, que no tiene componente preventivo.

### T-03 El objetivo se reparte entre depósitos

**Esperado:** una transferencia de 300 tn no se detiene porque en el primer depósito entren 100.

**Medido:** el stock final se reparte entre varios depósitos en la misma corrida (E-00: FRINOA 7 000, RUTA9 5 456, DODERO 625 tn), lo que antes no ocurría en un mismo día. `transferencias_incompletas` cuenta los días en que, agotados los candidatos, quedó saldo sin mover: 32,1 por corrida en E-00 y 161 en E-01, que es la lectura de que la red no tiene espacio, no de que el reparto se cortó solo.

### T-04 y T-05 Diagnóstico de destinos

**Esperado:** poder responder por qué no se usó un depósito.

**Medido:** con `debugPlanificacion` activo, `diagnosticoDepositos()` imprime una línea por depósito con capacidad, stock, espacio, existencia de tarifa, costo estimado, elegibilidad y motivo de descarte (`NO_HABILITADO`, `SIN_CAPACIDAD_PRODUCTO`, `SIN_ESPACIO`, `TARIFA_INEXISTENTE`, `TARIFA_SITIO_INEXISTENTE`, `SIN_FLOTA`). La selección usa **la misma** función que el diagnóstico, así que el motivo informado es el motivo real.

### T-06 REACTIVA no cambia

**Esperado:** la política REACTIVA se comporta igual que antes del MOD.

**Medido, y con una salvedad honesta:** `toneladasASacarReactiva()` es **idéntica carácter por carácter** a la de `fase-23`, y E-12 no transfiere una sola tonelada por el componente preventivo. Su costo por tonelada se mueve **+0,1 %** (162,90 → 163,05 USD/tn) y el servicio de 0,922 a 0,927: la diferencia no viene de la política de transferencia sino del otro cambio del mismo MOD, los pedidos parciales, que afectan a todos los escenarios. La igualdad bit a bit que pedía el criterio original no es alcanzable con los dos cambios en la misma versión; lo que sí se puede afirmar es que la política de transferencia reactiva no fue modificada.

### T-07 Sobrecarga crítica

**Esperado:** no bloquea la producción, no duplica volumen y no borra la penalidad.

**Medido:** `toneladas_transferidas_criticas` no se suma a `toneladas_transferidas_desborde` —los tres componentes se combinan con `max`—, `ton_dia_sobre_nominal` y `dias_sobrecarga` siguen devengando la penalidad en el costo económico, y las toneladas exportadas no bajan en ningún escenario.

### X-24 Corrida desde el Excel actualizado

**Esperado:** la campaña completa desde `datos/entrada_ejemplo.xlsx` termina sin excepciones y los balances cierran.

**Medido:** E-00 desde Excel corre los 364 días de campaña (365 de reloj) sin abortos, con C-01 y C-02 en verde todos los días. Antes hacía falta corregir la capacidad de `colaCamiones`: con la capacidad por default de la librería (100 agentes) la corrida moría en el día 162 con "An agent was not able to leave the port root.entradaEnvios.out". Esa cola es la lista de espera de envíos por portacontenedor, no una restricción física, así que pasa a capacidad ilimitada; la restricción sigue siendo la flota.

La corrida deja tres lecturas que son de los **datos**, no del modelo, y que conviene revisar antes de usar sus KPIs como respuesta de negocio:

1. **La demanda de JUGO excede la producción**: `PedidoPlan` pide 20 870 tn y `ProduccionPlan` produce 17 175 tn en la campaña. El faltante estructural es 3 694 tn, así que ningún dimensionamiento de flota o de depósito puede dar 100 % de servicio.
2. **La producción arranca el día 88** y hay pedidos desde el día 1 con plazo de 31 días: los primeros pedidos vencen antes de que exista producto. De ahí los atrasos tempranos.
3. **CASCARA queda encerrada en planta.** Su única capacidad de depósito está en RUTA9, a 1 200 km, y un viaje redondo consume `2 × 1200 / 70 / 10 = 3,43` camión-día mientras el escenario ofrece 3 camión-día por día. `flotaProductoAlcanza()` nunca da verdadero y el diagnóstico responde `SIN_FLOTA` todos los días: las 12 031 tn de cáscara se producen y no salen nunca (pico de ocupación de planta 668 %). Es una limitación conocida del modelo de flota como capacidad diaria (ADR-044): un viaje que no cabe en una jornada no puede empezar. Queda declarada como pendiente; para representar distancias de 1 200 km hay que permitir que un viaje ocupe un camión durante varios días.

## 4.1 Validación de datos de entrada

Antes de cualquier caso funcional, el escenario debe pasar `validarDatosEntrada()` (ver [Contrato de datos](../09_Definicion/Contrato_de_Datos.md) §7). Una corrida con `errores_entrada.csv` no vacío no se considera evidencia válida.

## 5. Matriz de aceptación por fase

| Fase | Compila | Prueba funcional | Balance | Integración | Aprobación |
|---|---:|---:|---:|---:|---:|
| Lote acumulativo | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente |
| Ubicaciones múltiples | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente |
| Transferencia parcial | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente |
| Reserva nueva | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente |
| Contenedores | Parcial | Parcial | Pendiente | Pendiente | Pendiente |
| Cross dock | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente |
| Costeo | Parcial | Pendiente | Pendiente | Pendiente | Pendiente |

## 6. Evidencia requerida

Para considerar una fase validada, guardar:

- parámetros del escenario;
- captura o log de resultados;
- saldos iniciales y finales;
- costos esperados y obtenidos;
- versión del modelo;
- fecha;
- observaciones.

## 7. Regresión

Después de cada cambio ejecutar al menos:

- producción base;
- transferencia actual;
- creación de pedido;
- reserva;
- generación de plan;
- creación de contenedor;
- corrida corta sin excepciones.

### Regresión de la fase 24

**Ejecutado:** 2026-07-31 en AnyLogic PLE 8.9.9, modelo `fase-24`.

| Comprobación | Resultado |
|---|---|
| Build | `Build completed successfully`, 0 errores |
| Campaña completa sintética | 365 días de reloj sin excepciones, C-01 y C-02 diarios en verde, servicio 100 %, 0 atrasados |
| Barrido | 1 080 corridas (36 escenarios × 30 réplicas) `Finished`, CSV etiquetado `fase-24` |
| Excel | E-00 desde `datos/entrada_ejemplo.xlsx` corre la campaña completa sin abortos (ver X-24) |
| Corrección de la cola | El barrido posterior al cambio de `colaCamiones` da idéntico al anterior en las 1 080 filas y todas las columnas: la corrección sólo destraba la carga desde Excel |
| REACTIVA | `toneladas_transferidas_preventivas` = 0 en E-12 y función sin modificar |
| Saldos huérfanos | `toneladas_pendientes_asignar` y `toneladas_pendientes_entregar` = 0 al cierre en los 36 escenarios |

### Regresión de la fase 23

**Ejecutado:** 2026-07-29 en AnyLogic PLE 8.9.9, modelo `fase-23`.

| Comprobación | Resultado |
|---|---|
| Build | `Build completed successfully`, 0 errores |
| Campaña completa | 365 días de reloj (183 de campaña más el drenaje) sin excepciones |
| Barrido | 1 080 corridas (36 escenarios × 30 réplicas) `Finished`, CSV etiquetado `fase-23` |
| Regresión de política fija | E-00 a E-13 idénticos a `fase-22` en las 420 filas y las 37 columnas comunes |
| KPIs de decisión | 0 en los 32 escenarios de política fija; 40 planes y 720 alternativas por corrida en los 4 con evaluador |
| Excel vs. sintético | E-00 da los mismos KPIs por los dos caminos |
| Auditoría por envío | Sin abortos en las 1 080 corridas, incluidos los 8 escenarios de sensibilidad tarifaria |

### Regresión de la fase 19

**Ejecutado:** 2026-07-27 en AnyLogic PLE 8.9.9, modelo `fase-19`.

| Comprobación | Resultado |
|---|---|
| Build | `Build completed successfully`, 0 errores |
| Campaña completa sintética | 183 días sin excepciones, `Inventario.validar()` en verde todos los días |
| Barrido | 390 corridas (13 escenarios × 30 réplicas) `Finished`, CSV etiquetado `fase-19` |
| Excel vs. sintético | Las 743 filas de `datos/entrada_ejemplo.xlsx` coinciden celda por celda con el generador (0 diferencias numéricas) y E-00 da los mismos KPIs por los dos caminos |
| Pérdida de producto | Cero en los 13 escenarios |
| E-09 determinístico | Desvío 0 en las 30 réplicas |
