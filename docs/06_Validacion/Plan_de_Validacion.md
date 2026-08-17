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

### V-COST-11 Crédito de holding futuro no es un cargo (ADR-065)

**Esperado:** `costoHoldingEvitado` cambia qué alternativa gana el ranking de `ordenarAlternativas()` bajo `MENOR_COSTO_INCREMENTAL_FACTIBLE` / `MENOR_COSTO_END_TO_END_FACTIBLE`, pero no aparece en ningún cargo registrado: `costoIncremental`, `costoEndToEnd`, `RegistroCostos.total()` y `costoEsperadoCircuito()` deben coincidir exactamente con los de una corrida sin el Mod. Con `horizonteHoldingEvitado()` forzado a 0 (o `diasEstimadosAlmacenamiento = 0`), el comportamiento tiene que ser **idéntico byte a byte** al de antes del Mod — mismas asignaciones, mismos costos, mismo nivel de servicio.

**Medido:** corrida pareada E-14/E-15 (política de costo) con el Mod activo vs. `diasEstimadosAlmacenamiento = 0`, y `reconciliarCostos()` sin abortos en las dos. Si `costoTotalCampania()` difiere entre ambas sólo por qué alternativa se ejecutó (no por cómo se calculó cada una), y las 420+ corridas del barrido terminan `Finished`, el caso pasa. Cualquier corrida en la que `costoIncremental` de la alternativa ejecutada no coincida con lo que después registra `RegistroCostos` para ese envío es una falla de este caso, no de C3.

### V-COST-12 Afinidad pedido-depósito no altera nada si no se usa (ADR-066)

**Esperado:** con `deposito_comprometido` vacío en todos los pedidos (el caso de `datos/entrada_ejemplo.xlsx` sin modificar), el comportamiento de `ordenarAlternativas()` es **idéntico** al de antes de ADR-066 — el nuevo criterio nunca decide nada porque `depositoComprometido.isEmpty()` es siempre verdadero.

**Medido:** corrida pareada con y sin el Mod (o con la columna presente pero vacía) debe dar el mismo `costoTotalCampania()`, las mismas asignaciones y el mismo `nivelServicio()`. Con al menos un pedido con `deposito_comprometido` cargado, esa alternativa debe ganar el ranking mientras sea factible, incluso si otra alternativa resulta más barata — comparar contra `decisiones_alternativas.csv` (columna `costo_incremental_usd_tn` de la alternativa elegida vs. la más barata disponible) para confirmar que ganó por compromiso y no por costo.

### V-COST-13 Rebalanceo entre depósitos no mueve nada sin datos, y no rompe balance con datos (ADR-066)

**Esperado:** sin filas depósito→depósito en `TarifaFleteProducto`/`Distancia`, `revisarRebalanceoEntreDepositos()` corre todos los días sin mover una tonelada (`toneladasRebalanceadasEntreDepositos` queda en 0) y sin abortar la corrida. Con esas filas cargadas, el mecanismo mueve stock de depósitos con `capacidadCrossDockDia <= 0` y antigüedad ≥ `diasEstimadosAlmacenamiento` hacia el destino de `mejorDestinoRebalanceo()`, y el balance físico se mantiene: lo que sale de un depósito aparece en el otro, sin producto perdido ni duplicado.

**Medido:** `validarInventario()` y `validarBalanceProducido()` (C-01/C-02) sin abortos en una corrida con datos depósito-depósito cargados. `reconciliarCostos()` sin abortos: el total de `FLETE_PRODUCTO` en el registro debe coincidir con `costoFletePlantaDeposito + costoFleteGranelTerminal + costoFleteEntreDepositos`, y el de `OUT_DEPOSITO` con `costoOutDeposito` (que ahora incluye tanto los egresos por despacho a pedido como los egresos por rebalanceo). Un escenario con `BOREAS` (sin cross dock) y stock viejo debería mostrar, al cierre, menos stock remanente en Boreas que una corrida sin las filas de tarifa depósito-depósito cargadas — con las mismas toneladas exportadas o más, nunca menos.

**Confirmado (corrida real, `E-00-R0`, con `BOREAS`/`NORRY` en `posiciones_cross_dock = 0` y filas `BOREAS→RUTA9`/`NORRY→RUTA9` cargadas):** 408 filas con `id_operacion` que empieza en `REB-` en `costos_eventos.csv` (136 de `FLETE_PRODUCTO`, 136 de `OUT_DEPOSITO`, 136 de `IN_DEPOSITO` — un trío por movimiento, 136 movimientos), entre los días 30 y 346. 8.060,1 tn rebalanceadas en 386 viajes (5.283,3 tn Boreas→Ruta9, 2.776,8 tn Norry→Ruta9), costo USD 688.576. Stock de cierre en Boreas+Norry: de 2.446,7 tn (corrida sin rebalanceo) a 21,8 tn (−99 %). Costo total de campaña (`tipo_contable = CAJA` sumado del registro): USD 6.242.209, **menor** que los ~USD 6.397.129 de la corrida sin rebalanceo — el costo del rebalanceo se compensó de sobra con lo que se dejó de perder por no exportar ese stock.

### V-COST-14 Material no se mezcla y no altera nada si es uniforme (ADR-067)

**Esperado:** con `material = ""` en todo el libro (el generador sintético, o un Excel sin la columna, o con un solo material por producto en `Producto`), el comportamiento es **idéntico** al de antes de ADR-067 — `materialesDe(producto)` devuelve `[""]`, y todas las comparaciones de material coinciden trivialmente. Con al menos dos materiales reales por producto en `Producto` (p. ej. `JUGO`/`JCL` y `JUGO`/`JCCL`) y pedidos de cada uno, ningún pedido de un material puede reservar ni despachar capas del otro material del mismo producto, aunque estén en el mismo depósito — `asignaciones_elegidas.csv`/`ejecucion_arcos.csv` no deben mostrar ninguna asignación cuyo material difiera del material del pedido que la generó.

**Medido:** corrida pareada con y sin el Mod (o con material uniforme) debe dar el mismo `costoTotalCampania()`, las mismas asignaciones y el mismo `nivelServicio()` — es el caso de regresión obligatorio. Con materiales reales distintos por producto, `validarInventario()` (C-01) sin abortos, y un chequeo cruzado entre `snapshot_inventario.csv` (o el CSV de auditoría que registre `id_lote`) y `decisiones_alternativas.csv`: ningún lote reservado para un pedido tiene un material distinto al de `Pedido.material`. Un escenario donde un producto tenga producción de un material pero pedidos de otro debería mostrar servicio degradado para esos pedidos específicos (déficit real, no oculto prestando entre materiales) — comparable contra `deficitEstructuralTn()` recalculado por material en vez de por producto agregado.

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

### C-02 Nada de lo disponible se pierde

**Esperado:** `stock inicial + producido = stock en planta + stock en depósitos + en proceso + entregado` (ADR-057).

**Medido:** `validarBalanceProducido()` corre todos los días sobre el stock inicial cargado más la producción acumulada de la planta. Las 1 080 corridas del barrido `fase-25` pasan, y también la corrida con 3 509 tn de stock inicial. Con la identidad anterior —sólo producción— la corrida abortaba el día 0 en cuanto había stock inicial, porque el inventario era mayor que lo producido.

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

## 4.5 Stock inicial (ADR-057)

Los casos SI se verifican con libros de prueba derivados del volcado sintético de E-00 (`tools/generar_excel_ejemplo.py` produce la plantilla; los libros de prueba sólo cambian la hoja `StockInicial`), más el barrido completo para la regresión.

### SI-01 Stock inicial en planta

**Esperado:** el producto está disponible el día 0 sin haberse producido, y no se devenga flete ni IN.

**Medido:** con 800 tn de JUGO en `PLANTA` (`dia_produccion = -90`, `dia_ingreso = -30`), el tablero informa `Stock inicial: 3509 tn` en el día 0 y el stock de planta arranca en el nivel cargado. La carga sólo llama a `inventario.ingresar(...)`: no hay ningún cargo con fecha anterior al día 0 en el registro de costos.

### SI-02 Stock inicial en depósito y costos futuros

**Esperado:** no paga flete planta→depósito ni IN histórico; sí paga almacenaje desde el día 0, OUT, consolidación, round trip, THC, terminal y despachante cuando se despacha.

**Medido:** con 1 200 tn de JUGO en FRINOA, 1 500 tn de CASCARA en RUTA9 y 9 tn de ACEITE en FRINOA, la campaña completa cierra con IN + OUT de 85 677 USD contra 85 115 USD de la misma corrida sin stock inicial: la diferencia es OUT de despacho, no IN de ingreso. Si el IN histórico se hubiera devengado, las 2 709 tn iniciales en depósito habrían agregado del orden de 6 800 USD. El almacenaje **baja** de 1 393 959 a 864 097 USD porque el producto ya posicionado se despacha antes y la producción nueva queda más tiempo en el frío propio, que no cobra almacenaje.

### SI-03 Un lote en dos ubicaciones

**Esperado:** un mismo `codigo_lote` puede estar en planta y en depósito, y sigue siendo un solo lote.

**Medido:** `JUG-2025-001` con 1 200 tn en FRINOA y 800 tn en PLANTA crea **un** `LoteProducto` con dos capas, `toneladasIniciales = 2000` y `ubicacionActual` en la ubicación de mayor saldo (`Inventario.ubicacionPrincipalDeLote`).

### SI-04 Stock inicial más producción nueva

**Esperado:** la disponibilidad de campaña es la suma, y los lotes iniciales no reciben producción nueva.

**Medido:** `disponibilidad_total_tn` = `stock_inicial_tn` + `produccion_campania_tn` en las corridas. Los lotes iniciales quedan con `estadoComercial = CERRADO` y `toneladasObjetivo = 0`, así que `crearLoteEnPlanta()` nunca los reutiliza: la producción abre sus propios lotes comerciales.

### SI-05 FIFO histórico

**Esperado:** el stock inicial sale antes que la producción nueva.

**Medido:** en el día 37 de la corrida con stock inicial ya hay 548 tn de stock inicial consumidas, y al cierre `stock_inicial_consumido_tn` = 3 509 con remanente 0, con producción disponible sin despachar. El orden lo da el FIFO existente (`dia_ingreso`, después `dia_produccion`, después `idLote`) sin ninguna regla nueva: las fechas negativas lo garantizan.

### SI-06 Capacidad excedida

**Esperado:** exceder la capacidad de un depósito es error de datos y aborta el arranque.

**Medido:** con 99 000 tn de JUGO en FRINOA (capacidad 7 000) la corrida se detiene en el día 0: *"El stock inicial no cumple el contrato de datos (1): - StockInicial en FRINOA / JUGO: 99000 tn superan la capacidad efectiva de 7000 tn"*. En **planta** el mismo exceso es una advertencia de consola y la corrida sigue, porque la capacidad nominal del frío propio es un umbral de lectura (ADR-048) y arrancar en sobrecarga es un caso real que el modelo mide.

### SI-07 Ubicación incompatible

**Esperado:** una terminal no puede tener stock inicial.

**Medido:** con `id_ubicacion = ZARATE` la corrida aborta en la validación del contrato: *"StockInicial SI-201: el stock inicial solo puede estar en PLANTA o DEPOSITO, no en ZARATE (TERMINAL)"*. Lo mismo con una ubicación inexistente, deshabilitada o sin capacidad para el producto.

### SI-08 Hoja vacía y regresión

**Esperado:** sin stock inicial, todo da igual que antes del cambio.

**Medido:** el barrido `fase-25` (1 080 corridas) es **idéntico fila por fila** al de `fase-24` en las 55 columnas comunes —sin una sola diferencia— y agrega las siete columnas nuevas en 0. Un libro **sin** la hoja `StockInicial` (los anteriores a esta versión, incluido `datos/entrada_ejemplo.xlsx`) corre la campaña completa y el tablero informa `Stock inicial: sin carga`. Un libro **con** la hoja y sin filas se comporta igual.

### SI-09 Almacenamiento desde el día 0

**Esperado:** el stock inicial en depósito paga almacenaje del día 0 en adelante y nada de la antigüedad anterior.

**Medido:** `devengarAlmacenamientoDiario()` cobra por capa y por día simulado, así que la primera imputación de una capa inicial es la del día 0. Y `toneladaDiaEnStock()` pasa a acotar la antigüedad al horizonte (`time() − max(0, diaIngreso)`): con `dia_ingreso = -60` el evaluador arrancaba imputando 60 tn-día de almacenaje anterior a la campaña, un costo hundido que empujaba a la política end-to-end a evitar el stock inicial. Con `dia_ingreso >= 0` el cambio es la identidad, y por eso el barrido sin stock inicial no se mueve.

### SI-10 Balance físico

**Esperado:** el stock inicial entra en la conciliación y el remanente es trazable.

**Medido:** C-02 pasa a ser `stock inicial + producción = stock + en proceso + entregado` y corre todos los días. `stock_inicial_remanente_tn` se calcula sobre las capas de los lotes con `esStockInicial`, y `stock_inicial_consumido_tn = stock_inicial_tn − remanente`, así que el retiro de los lotes históricos es atribuible sin acumuladores nuevos.

### SI-11 La carga está conectada al arranque

**Esperado:** `cargarStockInicial()` se ejecuta de verdad, no queda como función huérfana.

**Medido:** el `StartupCode` del `.alp` es `cargarDatosEntrada(); cargarStockInicial();`, y la traza del aborto de SI-06 muestra la cadena real: `Main.validarStockInicial → Main.cargarStockInicial → Main.onStartup`. El punto de invocación vive en el `.alp`, no en el espejo `model_src/`.

### SI-12 Déficit estructural sobre los datos reales

**Esperado:** el modelo dice antes de correr si la demanda es inalcanzable.

**Medido:** con `datos/entrada_ejemplo.xlsx` el tablero informa `déficit estructural 3924 tn` en el día 0. Es la lectura de X-24 punto 1, ahora cuantificada por producto por `DatosEntrada.deficitEstructuralTn()` = `max(0, demanda − stock inicial − producción planificada)`.

## 4.6 Ventana marítima y cut-off (ADR-059)

Los casos VM se verifican con dos libros de prueba (`vm_ventana.xlsx`, con las cuatro fechas explícitas y pedidos armados caso por caso, y `vm_legacy.xlsx`, con el contrato anterior), más el barrido completo `fase-26` para el efecto agregado. Cada caso dice **cómo** se midió: corrida directa, barrido o lectura del código. Un caso medido por lectura de código no es un caso corrido, y así queda anotado.

### VM-01 Planificación anticipada (corrida directa)

**Esperado:** con conocimiento 10, apertura 17, cut-off 24 y ETD 25, el pedido existe y reserva desde el día 10, ningún viaje físico ocurre antes del día 17 y la entrega puede ocurrir hasta el día 24.

**Medido:** la traza de `vm_ventana.xlsx` registra `[dia 10] VM01 JUGO estado=RESERVADO solicitado=400 reserva_activa=400`, siete días antes de la apertura. En el corte del día 16 el pedido tiene sus contenedores en `CREADO`, con 0 envíos en el flujo físico y 0 viajes; el tablero informa `Planificados sin ejecutar` distinto de cero y `con retiro abierto` en cero para ese buque.

### VM-02 Varios pedidos en el mismo buque (corrida directa)

**Esperado:** 30 pedidos con el mismo cut-off y la misma terminal se distribuyen dentro de la ventana en vez de liberarse todos el día del cut-off.

**Medido:** los 30 pedidos `VM02-xx` se reservan el día 46 (conocimiento) y no el 53 (apertura): la traza los muestra `RESERVADO` con `pend_asignar = 0` en el mismo día en que nacen. La ejecución arranca el día 53 y se secuencia por holgura, no por orden de llegada. **Límite de esta evidencia:** con la capacidad del libro de prueba el cluster no entra completo en la ventana y 28 de los 30 pierden el cut-off, así que el caso demuestra la planificación anticipada y la secuenciación, no un cluster holgado.

### VM-03 Ventana inviable (corrida directa)

**Esperado:** una ventana más corta que el tiempo logístico mínimo se detecta y se avisa.

**Medido:** *"Dia 20: la ventana del pedido VM03 no alcanza para el cut-off (holgura -1.37 dias, buque B-VM03)"*. El tablero lo cuenta en `ventanas inviables`, y el KPI `pedidos_ventana_inviable` lo expone en el barrido.

### VM-04 Servicio por tonelada (corrida directa)

**Esperado:** el incumplimiento por pedido es binario y el servicio por tonelada mide la fracción entregada antes del cut-off.

**Medido:** las dos métricas se separan de verdad. En la campaña completa con `datos/entrada_ejemplo.xlsx`: `Toneladas al cut-off: 7666 · fuera: 6936`, con `Nivel de servicio 26% · por tonelada al cut-off 25%`. El acumulador se parte en `toneladasEntregadasAntesCutoff` / `toneladasEntregadasFueraCutoff` en el momento de la entrega, contra `dia_cutoff_fisico` y no contra el ETD.

### VM-05 Pérdida de buque (corrida directa)

**Esperado:** una entrega posterior al cut-off marca `perdioCutoff` y el pedido sigue vivo hasta entregarse o reprogramarse.

**Medido:** `VM05` (3 000 tn, ventana de 2 días) queda avisado el día 77 por ventana inviable y después aparece como `estado=ATRASADO` con reserva que sigue creciendo hasta el final de la corrida: el pedido no se cancela ni se congela. Con `politica_reprogramacion_buque = CONTINUAR` (default) se marca `reprogramado`; con `CANCELAR` el pedido pasa a `CANCELADO`.

### VM-06 Capacidad futura insuficiente (corrida directa)

**Esperado:** los contenedores que no encuentran posición dentro de la ventana quedan identificados.

**Medido:** el tablero informa `sin posición` distinto de cero en las dos corridas (213 en el libro VM, 150 en la campaña completa) y `contenedores_sin_posicion_futura` lo expone por corrida en el barrido. `posicionesPlanificadas` es un registro de planificación: no consume capacidad operativa del día, sólo dice cuándo la ventana no alcanza para el sitio elegido.

### VM-07 Cross docking dentro de la ventana (barrido)

**Esperado:** el cross dock se planifica anticipadamente y se ejecuta dentro de la ventana.

**Medido:** en `fase-26`, E-05 (escenario de cross dock) mantiene sus operaciones de cross dock con `pedidos_perdieron_cutoff = 0,2` de media en 30 réplicas y servicio 0,988: el cruce ocurre dentro de la ventana. **Límite:** no hay un caso unitario de cross dock con ventana corta; el respaldo es el escenario del barrido.

### VM-08 Compatibilidad legacy (corrida directa)

**Esperado:** un libro sin las columnas nuevas corre, deriva las fechas y avisa.

**Medido:** `vm_legacy.xlsx` (1 399 pedidos con `dia_llegada`/`dia_limite`) corre sin excepción y la consola abre con *"Advertencia de datos: La hoja PedidoPlan no trae dia_cutoff_fisico: la ventana maritima se deriva de dia_llegada/dia_limite con los defaults del escenario (ADR-059)"*. La derivación no inventa conocimiento más temprano que el declarado: `dia_conocimiento = dia_llegada`.

### VM-09 Reservar y transferir antes de abrir la ventana (corrida directa)

**Esperado:** la reserva y la transferencia funcionan con la ventana cerrada; el contenedor existe en `CREADO` y nada entra al flujo físico hasta la apertura.

**Medido:** es la otra mitad de VM-01. `crearContenedoresParaAsignacion()` crea el contenedor en `CREADO` cuando `pedido.ventanaRetiroAbierta` es falso y en `ESPERANDO_PROGRAMACION` cuando ya abrió, así que un pedido que nace con la ventana abierta no pierde un día. `actualizarVentanasRetiroDelDia()` (paso 2b) es el único punto que hace `CREADO → ESPERANDO_PROGRAMACION`. No se agregó ningún estado nuevo de contenedor.

### VM-10 Regresión de `demandaProyectada()` (lectura de código + barrido)

**Esperado:** la demanda conocida se cuenta completa aunque el retiro no haya abierto, y la transferencia preventiva no cambia sólo por la existencia de la ventana.

**Medido:** el filtro es `permiteTransferenciaAntesRetiro || pedido.ventanaRetiroAbierta`, con el permiso en `true` por default, así que con la configuración del contrato la proyección es la de antes. Lo mismo con `permiteReservaAntesRetiro` en `reservarParcialPedido()`. **Límite:** no hay corrida específica con los permisos apagados; el caso está cubierto por lectura del código y por el barrido con los defaults.

### VM-11 Efecto agregado de la ventana (barrido y corrida completa)

La ventana **no** es neutra, y ese es el punto del MOD: hoy un pedido con 31 días de plazo se ejecuta desde el día 1; con el gate arranca a lo sumo 7 días antes del cut-off.

- Barrido sintético `fase-24` → `fase-26`: E-00 baja de 0,975 a 0,696 de servicio con las mismas 12 738 tn exportadas, y aparecen 10,7 pedidos que pierden el cut-off, 5,24 días de holgura media y 60 contenedores sin posición futura. E-05 (0,988) y E-22 (0,990) casi no se mueven: el margen ya estaba.
- Campaña completa con `datos/entrada_ejemplo.xlsx`: servicio 26 %, 14 602 tn exportadas, 943 pedidos que pierden el cut-off, 93 buques cumplidos contra 313 perdidos.
- **Cuánto de eso es la ventana:** corriendo el mismo libro con `dia_apertura_retiro_vacio = dia_conocimiento` (que reproduce la regla anterior, ejecutar desde `dia_llegada`) el servicio sube de 26 % a 32 % y las toneladas exportadas de 14 602 a 15 218. La ventana explica unos 6 puntos; el resto es estructural y ya estaba declarado en X-24: 520 de los 1 399 pedidos son de CASCARA, que no puede salir de planta por la limitación de flota de ADR-044 (RUTA9 a 1 200 km).

## 4.7 Capacidad finita y menor costo factible (ADR-060)

Los casos CAP se corren **uno por uno, sin barrido**, con libros minimos generados a partir de `datos/entrada_ejemplo.xlsx`: 24 pedidos vivos, las cuatro fechas de la ventana concentradas en un mismo buque y la capacidad de los sitios como unica variable. Cada caso dice **como** se midio. Los tres CSV de diagnostico (`capacidad_por_dia.csv`, `asignaciones_capacidad.csv`, `asignaciones_capacidad_decisiones.csv`) son la evidencia; el tablero es la lectura.

El caso base es **CAP-00** (capacidad de red sin tocar): 32 posiciones reservadas y las 32 consumidas, 25 alternativas seleccionadas, ningun descarte por capacidad, ocupacion DODERO 26 / RUTA9 4 / T4 1 / ZARATE 1 y ningun dia por encima del nominal.

### CAP-01 y CAP-07 La alternativa barata con capacidad parcial se usa hasta donde llega (corrida directa)

**Esperado:** si el sitio mas barato solo tiene una parte de las posiciones que el pedido necesita, se usa por esa parte y el resto va a otro circuito, en vez de comprometer el pedido entero y hacer backlog.

**Medido:** libro `CAP-07-sitio-barato-saturado.xlsx`, identico a CAP-00 salvo DODERO en 1 posicion/dia. El reparto pasa de 26/4/1/1 a **DODERO 17, T4 7, ZARATE 4, RUTA9 4**, aparecen 7 alternativas descartadas con `SIN_CAPACIDAD_ANTES_CUTOFF` y el sobrecosto de saturacion queda medido en **5 098,66 USD**. Ningun dia supera el nominal y las 32 posiciones se consumen.

### CAP-02 La alternativa barata sin posiciones en la ventana no gana el ranking (corrida directa)

**Esperado:** la alternativa mas barata sin capacidad en la ventana se descarta **antes** de compararse por costo, con motivo escrito.

**Medido:** libro `CAP-02-sitio-barato-sin-capacidad.xlsx` (ventana de 5 dias, RUTA9 en 1 posicion/dia): 2 alternativas descartadas con `SIN_CAPACIDAD_ANTES_CUTOFF` y 1 236,86 USD de sobrecosto. Poner la capacidad del sitio en **cero** no es la forma de probar esto: el modelo lo rechaza como error de datos al arrancar (“la capacidad de consolidacion de RUTA9 es cero”), y esta bien que lo haga.

### CAP-03 Capacidad total insuficiente (corrida directa)

**Esperado:** cuando la ventana completa no alcanza para todos los contenedores, el modelo no sobre-vende posiciones: reparte lo que hay, deja el resto sin cubrir y lo muestra.

**Medido:** libro `CAP-03-capacidad-insuficiente.xlsx` (ventana de 5 dias y **todos** los sitios en 1 posicion/dia). Cada sitio de consolidacion queda exactamente en su techo —BOREAS 5, DODERO 5, PLANTA 5, RUTA9 5 en 5 dias—, se usan 12 posiciones de cross dock, **68 alternativas** se descartan por falta de posicion antes del cut-off y el servicio cae a 25 %. El sobrecosto de saturacion sube a 15 455,54 USD. **Ningun dia por encima del nominal.**

### CAP-04 No sobreasignar un dia (corrida directa, todos los casos)

**Esperado:** la ocupacion diaria de un recurso nunca supera su capacidad nominal.

**Medido:** `dias sobre nominal = 0` en los diez libros CAP y en la campana completa (3 640 filas de `capacidad_por_dia.csv`). Es ademas invariante diario: `reconciliarCapacidad()` aborta la corrida si se rompe.

### CAP-05 Liberacion de posiciones (implementado; no observado en las corridas)

**Esperado:** una posicion que ya no se va a usar vuelve al cupo del sitio y queda registrada con motivo.

**Medido:** las tres rutas de liberacion estan implementadas y auditadas por C-03 —sobrante de una asignacion ya contenerizada, `SIN_CAPACIDAD_ANTES_CUTOFF` al quedarse sin dia, y cancelacion del pedido que perdio el cut-off con politica `CANCELAR`, que antes de este cambio dejaba las posiciones tomadas—, pero **ninguna se disparo** en los libros CAP: la reserva se crea junto con los contenedores de la asignacion y se consume el dia planificado, asi que en estas corridas nunca queda una reserva viva sin contenedor. Se corrio el caso dirigido (`CAP-05-cancelacion-libera.xlsx`, capacidad estrangulada + `politica_reprogramacion_buque = CANCELAR`) y dio 32 reservas consumidas y 0 liberadas. **Queda como caso no observado**, no como caso verde.

### CAP-06 Reprogramacion dentro de la ventana (implementado; no observado en las corridas)

**Esperado:** una posicion no usada el dia comprometido se mueve al proximo dia con lugar de la ventana y nunca despues del cut-off.

**Medido:** `reservasReprogramadas = 0` en los diez libros y en la campana completa. La reprogramacion se dispara cuando un contenedor **pierde** su dia comprometido, y en estos libros el contenedor siempre esta listo: se intento forzarlo con un solo camion de producto y sin stock en los depositos (`CAP-06-flota-escasa.xlsx`), y el evaluador evita la demora eligiendo planta y cross dock en vez de llegar tarde —que es la conducta correcta—. La cota dura si esta verificada: **ninguna reserva quedo con `dia_planificado > dia_limite`** en ningun caso.

### CAP-08 Pedido cubierto por mas de un circuito (corrida directa)

**Esperado:** un pedido que no entra en un solo sitio se reparte entre circuitos, con trazabilidad por asignacion.

**Medido:** en CAP-07 y CAP-03 el mismo pedido aparece con reservas en sitios distintos de `asignaciones_capacidad.csv`, una por asignacion, y el tablero cuenta los pedidos multi-circuito. Es el mismo mecanismo de ADR-055; lo que agrega ADR-060 es que el reparto lo dispara la capacidad y no solo el stock.

### CAP-09 El cross dock consume su propio recurso (corrida directa)

**Esperado:** una operacion de cross dock ocupa `CROSS_DOCK` y no gasta posiciones de consolidacion.

**Medido:** en CAP-03 las reservas se separan en 20 de `CONSOLIDACION` y 12 de `CROSS_DOCK`, con techos independientes (`CROSS_DOCK|DODERO` nominal 320, `CROSS_DOCK|RUTA9` 640) y sin que el cruce reduzca las posiciones de consolidacion del mismo sitio. En la campana completa: 1 870 de consolidacion y 113 de cross dock.

### CAP-10 Sin posiciones de cross dock (corrida directa)

**Esperado:** con el cross dock sin cupo, el flujo degrada a consolidacion normal y no rompe.

**Medido:** libro `CAP-10-sin-posiciones-cross-dock.xlsx` (`posiciones_cross_dock = 0` en todos los sitios): 32 reservas, **todas de `CONSOLIDACION`**, ninguna de cross dock, y el resultado coincide con CAP-00 sitio por sitio (26/4/1/1). La degradacion es completa y silenciosa.

### CAP-11 Regresion con la agenda apagada (corrida directa)

**Esperado:** con `permite_reserva_capacidad_futura = false` el modelo reproduce exactamente la conducta anterior a ADR-060.

**Medido:** libro `CAP-11-sin-agenda.xlsx` contra CAP-00: **0 reservas creadas**, las mismas 160 filas de `capacidad_por_dia.csv` con **0 diferencias de ocupacion**, y las decisiones del evaluador **identicas** pedido por pedido. La unica diferencia es la que tiene que estar: sin agenda, DODERO acumula 55 contenedor-dia de cola esperando posicion; con agenda, esa espera es planificada y la cola es 0.

### CAP-12 Reconciliacion (invariante diario)

**Esperado:** lo reservado se explica siempre por lo activo, lo consumido y lo liberado, y ninguna ocupacion supera el nominal.

**Medido:** `reconciliarCapacidad()` (C-03) corre todos los dias de todas las corridas —incluida la campana completa de 365 dias con `datos/entrada_ejemplo.xlsx`— y ninguna aborto. Con la agenda apagada tambien corre, porque la consolidacion sigue ocupando capacidad al ejecutar.

### CAP-13 Terminal saturada (corrida directa)

**Esperado:** con T4 en una posicion por dia, T4 recibe como maximo un contenedor por dia y el resto se reparte.

**Medido:** libro `CAP-13-t4-saturada.xlsx`: T4 opera los dias 2 a 8, **exactamente 1 contenedor por dia**, maximo 1, y las 7 alternativas que no entran se descartan con motivo. Ninguna reserva quedo despues del cut-off.

### CAP-14 Politica fija con y sin fallback (corrida directa)

**Esperado:** una politica `FIJA_*` respeta la capacidad; sin fallback el saldo que no entra queda sin cubrir y se ve, y con fallback pasa al evaluador.

**Medido:** dos libros identicos con `politica_seleccion = FIJA_DEPOSITO`, cross dock apagado, ventana de 5 dias y los depositos en 1 posicion/dia. Sin fallback (`CAP-14a`) el modelo usa **solo depositos** —BOREAS 18, DODERO 9, RUTA9 5— y el saldo que no entra se pierde. Con fallback (`CAP-14b`) aparecen PLANTA 3, T4 10 y ZARATE 4, es decir circuitos que la politica fija no habilita, con 41 alternativas descartadas por capacidad y 12 958,32 USD de sobrecosto de saturacion. En los dos casos ningun dia supera el nominal.

### CAP-15 Horizonte y cut-off (corrida directa, todos los casos)

**Esperado:** no se reserva ni se opera despues del cut-off del pedido.

**Medido:** `dia_planificado > dia_limite` en **0 reservas** de todos los libros CAP y de la campana completa (1 983 reservas). La unica excepcion prevista —el pedido que ya perdio el cut-off y sigue por politica `CONTINUAR`— esta documentada en ADR-060 punto 5 y no se activo en estos casos.

### CAP-16 Campana completa con el libro real (corrida directa, integracion)

**Esperado:** la campana completa desde `datos/entrada_ejemplo.xlsx` termina sin excepciones y con la agenda consistente.

**Medido:** 365 dias, **1 983 posiciones reservadas y las 1 983 consumidas** (1 870 de consolidacion, 113 de cross dock), 0 reprogramadas, 0 liberadas, **0 dias por encima del nominal** en 3 640 filas de agenda, 521 alternativas descartadas por falta de posicion antes del cut-off y 103 359 USD de sobrecosto de saturacion. Servicio y toneladas quedan en el mismo orden que `fase-26` (14 611 tn exportadas), como corresponde: capacidad finita **reparte, no crea**, y el techo estructural de la CASCARA sigue siendo flota (ADR-044).

**No se corrio barrido**, por pedido explicito. El efecto agregado de ADR-060 sobre los 36 escenarios queda pendiente y no se afirma.

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

### Regresión de la fase 25

**Ejecutado:** 2026-08-04 en AnyLogic PLE 8.9.9, modelo `fase-25`.

| Comprobación | Resultado |
|---|---|
| Build | `Build completed successfully`, 0 errores |
| Campaña completa sintética | 183 días de campaña sin excepciones, C-01 y C-02 diarios en verde, servicio 100 % |
| Barrido | 1 080 corridas (36 escenarios × 30 réplicas) `Finished`, CSV etiquetado `fase-25` |
| Regresión contra `fase-24` | **Idéntico**: las 1 080 filas coinciden en las 55 columnas comunes, sin una sola diferencia. Las siete columnas nuevas dan 0 porque los escenarios sintéticos no tienen stock inicial |
| Excel sin la hoja | `datos/entrada_ejemplo.xlsx` (364 días, sin `StockInicial`) corre completo; el tablero informa `Stock inicial: sin carga · déficit estructural 3924 tn` |
| Excel con stock inicial | 3 509 tn en planta, FRINOA y RUTA9: campaña completa, servicio 100 %, stock inicial consumido 3 509 y remanente 0 |
| Errores de datos | Capacidad de depósito excedida y ubicación `TERMINAL` abortan el arranque con la lista completa de errores (SI-06, SI-07) |

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

## V-FLOTA-MD. Flota de producto multidiaria (ADR-061)

Doce casos. El barrido **no** se corrio en esta tanda por pedido explicito: la evidencia es build, campana completa y corridas dirigidas con libros derivados del libro real (`/home/ubuntu/flota_tests/`). Cada caso dice **como se midio**, y los que no se observaron directamente quedan como *no observados*, no como verdes.

| Caso | Qué verifica | Resultado |
|---|---|---|
| V-FLOTA-MD-01 | Un viaje mas largo que una jornada se puede programar y ocupa el camion hasta que vuelve | **Verificado** por corrida. Libro con 3 camiones y 10 h: el tramo de 1 200 km cuesta 3,43 camion-dia y con la agenda **se hace**; la utilizacion de la flota pasa de 7 % (agenda apagada) a **91 %** y aparece espera de flota de 1,4 dias |
| V-FLOTA-MD-02 | El producto no esta disponible en destino antes de la llegada | **Verificado** por construccion auditada: `recibirViajeProducto()` es el unico que llama a `inventario.ingresar()` para un viaje, con `dia_ingreso = diaLlegadaDestino`, y C-04 aborta si un viaje se completa sin haber ingresado o ingresa sin haber retirado. Ningun aborto en las corridas |
| V-FLOTA-MD-03 | El camion no se libera al llegar, sino al regresar | **Verificado** por C-04: `disponibleDesde` nunca puede ser anterior al ultimo regreso de sus viajes vivos, y la reconciliacion corre todos los dias de las cuatro campanas |
| V-FLOTA-MD-04 | Un movimiento se puede programar parcialmente | **Verificado** por corrida: con 3 camiones el modelo programa 566 de 578 viajes y contabiliza 681 951 tn sin flota, en vez de rechazar el movimiento entero |
| V-FLOTA-MD-05 | La CASCARA hacia RUTA9 (1 200 km) deja de ser imposible | **Verificado, con una correccion del diagnostico previo:** con el libro vigente (500 camiones, 20 h) el tramo **ya era posible** bajo ADR-044, porque la regla era camion-dia agregado y no "entrar en la jornada": la corrida con la agenda apagada tambien mueve 506 tn de cascara a deposito. Lo que era imposible es el caso de flota chica, y ahi la agenda es la diferencia |
| V-FLOTA-MD-06 | Inventario en transito reconciliado | **Verificado** por C-04 y C-02: el contador `toneladasProductoEnTransito` tiene que ser igual a la suma de los viajes en transito todos los dias, y el balance de la campana incluye el transito. Sin abortos |
| V-FLOTA-MD-07 | El flete se cobra una sola vez por viaje fisico, y el viaje cancelado no paga | **Verificado** por construccion auditada: `iniciarViajeProducto()` es el unico punto que llama a `registrarFleteProducto()` para un viaje y marca `fleteRegistrado`; `cancelarViajeProducto()` no cobra. La auditoria por envio de ADR-053 no aborto en ninguna corrida |
| V-FLOTA-MD-08 | El espacio del deposito no se compromete dos veces | **Verificado** por construccion auditada: `espacioDisponibleEfectivo()` descuenta `toneladasEnTransitoHacia()`, y ninguna corrida excedio capacidad de deposito (la capacidad dura de ADR-057 aborta si se excede) |
| V-FLOTA-MD-09 | El cross dock solo cruza si el viaje llega dentro de la jornada | **Verificado** por lectura de codigo y corrida: `cruceLlegaEnElDia()` exige llegada antes del fin del dia y, si no, la alternativa se descarta con `CRUCE_SIN_LLEGADA_EN_EL_DIA`. Con los libros usados el cross dock sigue sin ganar el ranking, igual que en ADR-054 |
| V-FLOTA-MD-10 | La evaluacion no muta la agenda | **Verificado** por construccion auditada: `evaluarDisponibilidadFlotaProducto()` trabaja sobre una copia `double[]` de las fechas de disponibilidad y no crea viajes; el unico punto que escribe `disponibleDesde` es `programarViajeProducto()` (y la liberacion de `cancelarViajeProducto()`) |
| V-FLOTA-MD-11 | Un tramo sin distancia declarada no aborta la corrida | **Verificado** por corrida: antes de la correccion la campana abortaba con `Falta la distancia ZARATE -> PLANTA`; con la lectura simetrica corre y el tramo faltante daria `RUTA_SIN_DISTANCIA` |
| V-FLOTA-MD-12 | Interruptor de regresion | **Verificado** por corrida pareada: con `habilita_flota_producto_multidiaria = false` el libro vigente da 988 viajes planta→deposito, 23 417 tn transferidas, 27 % de servicio y 14 611 tn exportadas, y con la agenda encendida 989 viajes y los mismos 23 417 tn, 27 % y 14 611 tn. El caso donde la flota no restringe **no se distorsiona** |

**No observados:** la cancelacion de viajes por perder el cut-off (`politica_reprogramacion_buque = CANCELAR`) esta implementada y auditada por C-04, pero los libros usados corren con la politica `CONTINUAR`, asi que la ruta no se ejercito. Tampoco se ejercito `SIN_CAMIONES_CONFIGURADOS` (ningun libro declara flota cero).

### Corridas de esta tanda

**Ejecutado:** 2026-07-24 en AnyLogic PLE 8.9.9, modelo `fase-27`.

| Corrida | Libro | Resultado |
|---|---|---|
| Build | — | `Build completed successfully`, 0 errores |
| Campana completa, agenda encendida | `datos/entrada_ejemplo.xlsx` | 365 dias `Finished` sin excepciones; 1 553 viajes programados y los 1 553 iniciados, espera 0,0 d, 989 viajes planta→deposito, 23 417 tn transferidas, pico de 94 camiones en ruta de 500, servicio 27 %, exportado 14 611 tn |
| Campana completa, agenda apagada | libro vigente con `habilita_flota_producto_multidiaria = false` | 365 dias `Finished`; 988 viajes, 23 417 tn, servicio 27 %, exportado 14 611 tn |
| Flota chica, agenda encendida | 3 camiones, 10 h | 365 dias `Finished`; utilizacion 91 %, 566 de 578 viajes programados, espera 1,4 d, 25 tn en transito al cierre, servicio 6 %, exportado 7 536 tn |
| Flota chica, agenda apagada | 3 camiones, 10 h | 365 dias `Finished`; utilizacion 7 %, 548 viajes, servicio 4 %, exportado 7 436 tn |

Dos errores reales aparecieron en estas corridas y estan corregidos: el retorno pedia la fila inversa de `Distancia` (que la tabla no trae) y C-04 contaba como superposicion tener el viaje en curso con el siguiente ya programado para cuando el camion vuelve, que es una agenda legitima.

## V-RT-S. El round trip del portacontenedor termina en la terminal (ADR-062)

Siete casos. El barrido **no** se corrio, por pedido explicito. La evidencia son corridas pareadas **antes/despues** con tres libros minimos deterministas (`/home/ubuntu/rt_tests/`, 20 dias, sin produccion, stock inicial en FRINOA, `politica_seleccion = FIJA_DEPOSITO`, cross dock apagado) mas la campana completa desde el libro real. "Antes" es el modelo de `main` en ADR-061 y "despues" es este cambio; los dos se corrieron con la misma instrumentacion temporal, que solo vuelca tiempos y fechas y no toca ninguna decision.

| Caso | Qué verifica | Resultado |
|---|---|---|
| V-RT-S-01 | El ciclo fisico no tiene un cuarto tramo | **Verificado** por corrida pareada (RT-01: 100 km, 50 km/h, carga 2 h, descarga 1 h). Antes: llegada a terminal en 6 h y cierre en **9 h** (0,375 d). Despues: cierre en **7 h** (0,2916667 d) = 2 + 2 + 2 + 1. El viaje de 2 h de mas desaparecio y `ret=0.0` en las seis trazas |
| V-RT-S-02 | Los Delay estan en horas, no en dias | **Verificado** por corrida (RT-02: 1 200 km, 70 km/h). Cada tramo dura `17,142857` **horas** (0,714286 d), no 17,14 dias: la llegada a terminal cae en el dia 3,5119 para un envio creado el dia 2. El ciclo pasa de **54,4 h** (antes) a **37,3 h** (despues) |
| V-RT-S-03 | El servicio se mide con el instante fisico, no con el cierre administrativo | **Verificado** por corrida pareada (RT-03, cut-off dos dias despues del conocimiento). El primer contenedor de cada pedido llega a terminal el dia 3,5119 y queda listo el 3,5536, **dentro** del cut-off del dia 4; antes cerraba el dia 4,2679 y lo perdia. Ademas la fecha de servicio y la de devengo quedan separadas y visibles: `entrega = 4,2679` con `cargos = 4` |
| V-RT-S-04 | El pedido de dos contenedores se entrega con el segundo | **Verificado** por corrida (RT-03, 48 tn = 2 contenedores). Al cerrar el primero el pedido sigue abierto (`pedido.diaEntrega = -1`); al cerrar el segundo toma **su** fecha de servicio (`4,267857`), que es `diaListoEnTerminal` del segundo contenedor |
| V-RT-S-05 | El portacontenedor se libera al terminar la descarga | **Verificado** por corrida: en las 24 trazas de cierre de las tres corridas `cierre == diaListoEnTerminal` con diferencia 0, es decir el envio sale del flujo en el mismo instante en que termina `descargarPuerto`, y el tablero cierra con 0 portacontenedores ocupados |
| V-RT-S-06 | Un solo round trip por contenedor | **Verificado** por corrida: 12 contenedores, 12 registros de round trip, `rt = 2 700 USD` en cada uno y ninguno repetido. El importe es **identico** antes y despues: el cambio es fisico, no tarifario |
| V-RT-S-07 | Las toneladas dentro y fuera del cut-off reconcilian | **Verificado** por corrida (RT-03): `144 + 144 = 288 tn`, exactamente las toneladas de los 12 envios entregados y las 288 tn que informa el tablero como exportadas |

### Corridas de esta tanda

**Ejecutado:** 2026-07-24 en AnyLogic PLE 8.9.9, modelo `fase-28`.

| Corrida | Libro | Antes (ADR-061) | Despues (ADR-062) |
|---|---|---|---|
| RT-01 — 100 km, 50 km/h | `rt_tests/RT-01.xlsx` | ciclo 9,0 h; atraso medio 0,4 d | ciclo **7,0 h**; atraso medio **0,3 d** |
| RT-02 — 1 200 km, 70 km/h | `rt_tests/RT-02.xlsx` | ciclo 54,4 h; atraso medio 2,3 d | ciclo **37,3 h**; atraso medio **1,6 d** |
| RT-03 — 2 contenedores por pedido, cut-off a 2 dias | `rt_tests/RT-03.xlsx` | 0 de 288 tn dentro del cut-off (servicio 0 %); atraso medio 1,0 d | **144 de 288 tn** dentro del cut-off (servicio 50 %); atraso medio **0,3 d** |

Las tres corridas mueven las mismas 288 tn (RT-03) o 144 tn (RT-01/02) y devengan el mismo round trip: lo unico que cambia es el tiempo.

| Corrida | Libro | Resultado |
|---|---|---|
| Build | — | `Build completed successfully`, 0 errores |
| Campana completa | `datos/entrada_ejemplo.xlsx` | 365 dias `Finished` sin excepciones; C-01 a C-04 en verde; 1 983 contenedores, 979 envios entregados, 989 viajes planta→deposito y 23 417 tn transferidas —los mismos volumenes fisicos que ADR-061—, servicio 28 % (contra 27 %) y exportado 14 612 tn (contra 14 611). El ciclo del portacontenedor se sigue devengando una vez por contenedor |

El **barrido no se corrio** en esta tanda, por pedido explicito. La comparacion entre escenarios con estos tiempos queda pendiente para la proxima tanda de `fase-28`.

## Capacidad de los bloques del flujo (ADR-063)

**Que se valida:** que ningun bloque del flowchart limite la concurrencia, que la limiten los recursos que se dimensionan, y que C-05 detecte el dia en que un bloque empieza a retener envios.

| Caso | Que verifica | Como se midio |
|---|---|---|
| V-BLQ-01 | Los nueve bloques del flujo corren con capacidad ilimitada | **Verificado** por lectura del `.alp`: `colaCamiones`, `viajarVacioAlOrigen`, `cargarCamion`, `viajarPuerto`, `descargarPuerto`, `cargarGranel`, `viajarTerminalGranel`, `descargarTerminal` y `consolidarCarga` tienen `maximumCapacity = true` |
| V-BLQ-02 | El techo artificial existia y era el de `viajarPuerto` | **Verificado** por aritmetica y corrida: 1 200 km a 70 km/h son 17,14 h por tramo, y con capacidad 1 el techo es `365 × 24 / 17,14 = 511` contenedores por campania. La corrida anterior al cambio entrego **417** por esa rama, con **1 066 envios congelados** en `viajarVacioAlOrigen` y 1 069 de los 1 500 portacontenedores tomados |
| V-BLQ-03 | Quitar la capacidad de bloque no relaja ninguna restriccion real | **Verificado** por corrida: al cierre de la campania corregida hay **21 portacontenedores ocupados de 1 500** (contra 1 069), uso de posiciones de consolidacion 20 %, `Esperando posicion: 0` y utilizacion de flota de producto 1 %. Ningun recurso queda saturado, o sea que la red nunca lo habia estado |
| V-BLQ-04 | C-05 detecta la retencion y nombra el bloque | **Verificado** por corrida dirigida: se devolvio `viajarPuerto` a la capacidad por default y la corrida aborto el **dia 10** con `C-05: el dia 10 hay 5 envios retenidos en el flujo mas alla de su duracion fisica; el bloque que mas retiene es cargarCamion con 5`. Es la regresion del error que estuvo silencioso hasta ahora |
| V-BLQ-05 | La espera de un recurso finito no cuenta como retencion | **Verificado** por construccion auditada: `colaCamiones` se registra con `horasEsperadas = -1` y C-05 la cuenta como envio en curso pero nunca como retenido. En los 365 dias de la campania corregida no hubo un solo envio retenido |
| V-BLQ-06 | El tablero informa donde estan los envios en curso | **Verificado** por corrida: al cierre muestra `Envios: 1 983 · en curso: 13` y `viajarPuerto 13`, que son los envios legitimamente en viaje el ultimo dia. El KPI anterior (`en transito`) mostraba 0 mientras 1 066 envios estaban congelados, porque contaba transito de flota de producto |

### Campania completa antes y despues

| Indicador | Antes (ADR-062) | Despues (ADR-063) |
|---|---|---|
| Exportado | 13 749 tn | **30 343 tn** de 30 656 recibidas |
| Nivel de servicio | 25 % | **95 %** |
| Pedidos entregados / atrasados | 629 / 708 | **1 378 / 4** |
| Atraso promedio | 46,2 dias | **0,8 dias** |
| Envios entregados | 919 | **1 962** |
| Portacontenedores ocupados al cierre | 1 069 de 1 500 | **21 de 1 500** |
| Envios retenidos | 1 066 (sin deteccion) | **0**, C-05 verde los 365 dias |

Ambas corridas son 365 dias `Finished` con `datos/entrada_ejemplo.xlsx`, con C-01 a C-04 en verde. El **barrido no se corrio**: todos los barridos anteriores (`fase-26`, `fase-27`) se midieron contra el techo artificial de 511 contenedores y **no son comparables**; la comparacion entre escenarios queda pendiente para `fase-28`.

## V-AUD. Auditoria de red (ADR-064)

**Que se valida:** que las tablas digan la verdad sobre lo que el modelo hizo, que no dupliquen ninguna fuente de verdad y que activar la auditoria **no cambie ninguna decision**.

Corrida de referencia: `datos/entrada_ejemplo.xlsx`, escenario `E-00`, replica 0, 365 dias `Finished`, `nivelAuditoriaRed = COMPLETA`.

| Caso | Que verifica | Como se midio |
|---|---|---|
| V-AUD-01 | Las seis tablas se escriben y cierran completas | **Verificado** por corrida: 25 524 filas de decisiones (97 columnas), 1 418 asignaciones (30), 11 020 arcos (29), 108 007 cargos (27), 3 385 snapshots de inventario (24) y 3 276 filas de capacidad (13), 45 MB en total. El resumen al cierre no informa ninguna fila posterior al cierre, o sea que no se perdio ni una |
| V-AUD-02 | Las claves primarias son unicas | **Verificado** por lectura de los csv: 0 duplicados en `(run_id, id_alternativa)`, `(run_id, id_asignacion)`, `(run_id, id_evento_arco)`, `(run_id, id_costo)`, `(run_id, dia, ubicacion, producto)` y `(run_id, dia, tipo_recurso, ubicacion)`. `run_id` es unico y no vacio en las seis tablas (`E-00-R0`) |
| V-AUD-03 | El esquema publicado no puede divergir del csv | **Verificado** por comparacion: para las seis tablas, la lista de columnas de `esquema_auditoria.json` es **igual** al encabezado real del archivo, y cada clave declarada existe entre las columnas. `version_esquema = ADR-064.1` aparece tambien en el manifiesto |
| V-AUD-04 | C-06: las alternativas `ELEGIDA` son las asignaciones ejecutadas | **Verificado** por corrida y por lectura: 1 418 `ELEGIDA` y 1 418 asignaciones, todas con `id_alternativa`, y **0** asignaciones cuyo `id_alternativa` no este en la tabla de decisiones. C-06 corre como reconciliacion al cierre y no aborto |
| V-AUD-05 | La identidad de la decision llega hasta el costo | **Verificado** por lectura: 9 375 de 11 020 arcos traen `id_decision` —los 1 645 que no son los viajes preventivos planta-deposito, el cross dock y la espera de posicion, que no nacen de una decision de circuito— y los 10 698 cargos de alcance `CONTENEDOR` traen `id_asignacion`. La ronda existe: 1 418 decisiones con hasta 3 rondas por pedido |
| V-AUD-06 | C-12: el balance diario de cada nodo cierra | **Verificado** por corrida: 3 385 filas, `descuadre_tn` maximo **0,0001 tn** y **0** filas por encima de la tolerancia, en los 365 dias y las 6 ubicaciones con movimiento. C-12 aborta si algun dia no cierra |
| V-AUD-07 | Los motivos de descarte son los que el codigo produce | **Verificado** por lectura: `SIN_STOCK` 6 652, `SIN_STOCK_ESPACIO_O_CUPO` 5 220, `TRANSFERENCIA_DEPOSITO_DEPOSITO` 1 418, `SIN_CAPACIDAD_ANTES_CUTOFF` 501, y vacio en las 11 733 filas que no son descartes. `resultado_ejecucion` reparte 1 418 `ELEGIDA`, 10 315 `NO_INTENTADA` y 13 791 `NO_FACTIBLE` |
| V-AUD-08 | La tabla mide el costo de la restriccion | **Verificado** por lectura: **398 alternativas mas baratas no factibles** (`es_mas_barata_no_factible`), y la espera de posicion de consolidacion promedia **65,7 h** sobre 585 arcos. Son las dos preguntas que antes no se podian responder |
| V-AUD-09 | Ninguna tabla duplica una fuente de verdad | **Verificado** por construccion auditada y por lectura: los importes salen solo de `costos_eventos` (`CAJA` 5 870 256 USD, que es el total del tablero, y `ECONOMICO` 411 140 aparte), el arco **no** tiene columna de importe, y la capacidad sale de la agenda de ADR-060 con `run_id` agregado, sin un segundo calendario |
| V-AUD-10 | La auditoria no cambia ninguna decision (corrida pareada) | **Verificado** por corrida pareada `COMPLETA` / `DESACTIVADA` con el mismo libro y la misma semilla: `asignaciones_capacidad.csv` (1 987 filas) sale **identico byte a byte** y las 3 276 filas de `capacidad_por_dia.csv` son identicas salvo el `run_id`, que solo existe con la auditoria activa. Los KPIs coinciden en todo el tablero: 30 343 tn exportadas, servicio 95 %, 1 987 contenedores, 1 060 viajes planta-deposito, 1 887 consolidaciones, 4 atrasados, atraso 0,8 dias y 5 870 256 USD de caja |
| V-AUD-11 | Costo de rendimiento de la auditoria | **Medido, sin diferencia material**: las dos campanias de 365 dias tardaron aproximadamente lo mismo (unos 8 minutos de reloj en PLE); la corrida auditada escribe 45 MB en streaming y no acumula registros en memoria. No se probo el barrido con auditoria activada: el barrido corre en `DESACTIVADA` |

Dos errores reales aparecieron corriendo estos casos y quedaron arreglados: la alternativa sintetica de transferencia deposito-deposito usa `DEPOSITO` como marcador y no es un nodo de la red (la auditoria de etapas abortaba con `Falta la ubicacion DEPOSITO`), y el arco del contenedor vacio va terminal -> origen mientras la tabla `Distancia` declara un solo sentido por tramo (abortaba con `Falta la distancia T4 -> DODERO`). La instrumentacion usa la lectura simetrica; la logica fisica original no cambio.

El **barrido no se corrio** en esta tanda, por pedido explicito.


## V-MAESTRO. El maestro real corre entero (ADR-069)

Corrida de referencia: `datos/Maestro_Simulacion.xlsx`, escenario `E-00`, replica 0, 365 dias `Finished` en AnyLogic PLE 8.9.9, `nivelAuditoriaRed = COMPLETA`. Es la **primera** corrida real de ADR-065 a ADR-068, que estaban declarados "pendiente de compilar y correr en el IDE".

| Caso | Que verifica | Como se midio |
|---|---|---|
| V-MAESTRO-01 | El libro carga sin errores de contrato | **Verificado** por corrida: la carga no aborta y deja 23 advertencias en consola, todas explicadas en V-MAESTRO-08. Antes de este ADR abortaba con `La hoja Stock inicial ... necesita al menos las columnas de producto, ubicacion y toneladas` |
| V-MAESTRO-02 | El stock inicial entra completo y con material | **Verificado** por corrida: `Stock inicial cargado: 6589 tn en 14 lotes historicos y 14 capas`, leyendo las toneladas de la columna `1-365` y el material de la segunda columna `Producto`, las dos con aviso explicito. El tablero cierra la campania con `stock inicial 6589 tn - consumido 5265` |
| V-MAESTRO-03 | Un pedido de un material no consume stock de otro (ADR-067) | **Verificado** por lectura de `asignaciones_elegidas.csv` unida al libro por `codigo_pedido`: `JUGO/JCL` asigna **15 842 tn de 16 961 pedidas** —exactamente su disponibilidad de campania (13 847 de produccion + 1 995 de stock inicial)— mientras `JCCL` (142) y `PCL` (133) asignan su demanda **completa** y no se usan para tapar el deficit de `JCL`. `CASCARA/CDL` 12 261 y `ACEITE/AEL` 1 250, completos |
| V-MAESTRO-04 | La campania completa termina sin excepciones | **Verificado** por corrida: 365 dias `Finished`. 30 656 tn pedidas, **29 439 tn exportadas**, servicio 72 % por tonelada al cut-off, 1 338 pedidos entregados, 16 atrasados con atraso medio 1,0 dia, 1 926 contenedores, 8 187 074 USD de caja |
| V-MAESTRO-05 | Los depositos nuevos del maestro existen en la red y reciben sus datos del libro | **Verificado** por corrida: `GRUPO_PAZ` y `CONTROL_UNION` se inicializan, aparecen en el grafico de stock por deposito y usan las cuatro distancias de 1 200 km. Su uso real es marginal (4,25 tn y 0 tn de pico) porque el evaluador elige depositos mas baratos, no porque falten datos |
| V-MAESTRO-06 | La auditoria de red escribe las seis tablas del maestro | **Verificado** por corrida y manifiesto `E-00-R0`: 50 496 alternativas evaluadas en 2 104 rondas, 1 400 asignaciones elegidas, 10 775 arcos, 112 593 cargos y 3 780 snapshots de inventario con **descuadre 0**. Costo de la restriccion (mas barata no factible) 1 228 227 USD; motivos: `SIN_STOCK` 26 778, `SIN_STOCK_ESPACIO_O_CUPO` 12 627, `TRANSFERENCIA_DEPOSITO_DEPOSITO` 2 104, `SIN_CAPACIDAD_ANTES_CUTOFF` 1 544 |
| V-MAESTRO-07 | Las etapas fisicas duran lo que el evaluador promete (C-13) | **Verificado** por el resumen de arcos: `ORIGEN_TERMINAL_CONTENEDOR_CARGADO` 17,1 h real contra 17,1 h esperada, `PLANTA_DEPOSITO` 14,7 contra 13,8, `CROSS_DOCK` 20,6 contra 19,8, `DESCARGA_TERMINAL` 0,2 contra 0,2. La unica etapa sin techo es `ESPERA_POSICION`, 126,5 h de promedio sobre 920 arcos: es la espera que explica el atraso, no un error de estimacion |
| V-MAESTRO-08 | Las filas descartadas son datos que la corrida no puede usar, no huecos de cobertura | **Verificado** por lectura de las 21 advertencias: 9 de `Gastos_terminal` y 6 de `Despachante` son terminales que el maestro nombra distinto a `Ubicacion` (`EXOLGAN`, `TPROSARIO`, `TRPLATA`, `TRP`) y 6 de `Gastos_THC` son proveedores que no son navieras (`FORWARDER`, `SILVERFREIGHT`). Ninguna corresponde a un tramo que la campania recorra: la cobertura de tarifas de `ZARATE`/`T4` con `MAERSK` esta completa, o la carga habria abortado |
| V-MAESTRO-09 | Regresion del contrato original con `datos/entrada_ejemplo.xlsx` | **Verificado** por corrida: 365 dias `Finished`, 30 364 tn exportadas de 30 656, servicio 97 %, 4 atrasados, 1 986 contenedores, 1 421 asignaciones y 109 878 cargos. **En `main` este libro no cargaba**: ADR-068 exigia la hoja `Tipo_cambio`, que solo existe en el maestro nuevo, y despues los depositos nuevos del canvas pedian `Ubicacion`, `CapacidadUbicacion` y `TarifaSitio` que el libro canonico no tiene. La comparacion contra los KPIs de ADR-064 (30 343 tn, 95 %) **no es una regresion limpia**: entre medio entraron ADR-065 (holding), ADR-066 (rebalanceo) y ADR-068, ninguno corrido hasta ahora |
| V-MAESTRO-10 | La campania no se cae en el cierre por vigencia de tarifa | **Verificado** por corrida: con los 12 buckets cerrados en el dia 364 la campania abortaba en el dia 365 (`Falta la tarifa de JUGO en FRINOA vigente el dia 365`); con el ultimo tramo abierto cierra los 1 915 envios entregados y los 7 en curso sin excepcion |

El **barrido no se corrio**, por pedido explicito. Los KPIs de esta tanda no son comparables con los de ADR-059 a ADR-063: el techo artificial de ADR-063 los afectaba, y este es el primer libro con material.

## V-TAR. Vigencia de tarifas por encabezado y conversion de moneda (ADR-070)

Corrida de referencia: `datos/Maestro_Simulacion.xlsx` con los cortes de vigencia de la ronda nueva (`0-31`, `31-59`, `59-90`, ... `334-999`) y la hoja `Tipo_cambio`, escenario `E-00`, replica 0, 365 dias `Finished` en AnyLogic PLE 8.9.9, `nivelAuditoriaRed = COMPLETA`.

| Caso | Que verifica | Como se midio |
|---|---|---|
| V-TAR-01 | Los tramos se leen del encabezado del libro, cualquiera sea el nombre de los cortes | **Verificado** por corrida: el libro con los cortes nuevos carga sin un solo error de contrato. En `main` abortaba con **723** errores `La tarifa de flete debe ser > 0 en PLANTA -> FRINOA / JUGO (dias 59-89)`, porque `COLUMNAS_MES` buscaba `'60-89'`/`'89-120'` y el libro dice `'59-90'`/`'90-120'`: no matcheaba ninguna columna y leia 0 |
| V-TAR-02 | Las tarifas en USD llegan sin convertir | **Verificado** por lectura de `costos_eventos.csv`: `FLETE_PRODUCTO` cotiza exactamente `300` y `1700` USD/viaje (los dos unicos valores del libro, en USD) en 2 308 cargos, y `ROUND_TRIP` `500` y `2700` en 883. `THC`, `COSTO_TERMINAL` y `DESPACHANTE`, tambien en USD, coinciden celda por celda con el libro en 1 915 cargos cada uno |
| V-TAR-03 | Las tarifas en `$` se dividen por el tipo de cambio de su tramo | **Verificado** por reconciliacion de los 716 cargos de `CONSOLIDACION` y `CROSS_DOCK` con valor contra el libro: la consolidacion de `RUTA9`/`DRY_HC_40` cotiza 848,226 USD en el tramo `181-212` (1 224 838,63 / 1 444), 829,275 en `212-243` (/1 477), 810,078 en `243-273` (/1 512) y 798,312 en `334-999` (1 286 080,56 / 1 611). Los 13 cargos que a primera vista no coinciden son cargos de cierre cuya **fecha de servicio** cae el dia anterior al **devengo** (ADR-062): el contenedor `P00900-C1.0` consolida el dia 242,71 y su cargo se registra el 243,47, cotizado con la tarifa del tramo del dia 242 |
| V-TAR-04 | El tope de cada tramo lo fija el inicio del siguiente, sin huecos ni solapes | **Verificado** por corrida: la resolucion por dia de campania **falla** si no hay fila vigente o si hay dos (contrato de costos, ADR-051), asi que 115 282 cargos cotizados sobre los 365 dias sin un solo aborto prueban que la grilla es una particion. Con los topes declarados por el encabezado (`'59-90'` y `'60-89'` para el mismo corte) uno de los dos casos aparece siempre |
| V-TAR-05 | Un peso sin tipo de cambio aborta la carga en vez de valer cero | **Verificado** por prueba negativa: el maestro sin la hoja `Tipo_cambio` (sus tarifas de consolidacion, cross dock y almacenaje estan en `$`) falla con `El libro de entrada no cumple el contrato de datos (12)` y 12 mensajes `Hay tarifas en $ y la hoja Tipo_cambio no declara un tipo de cambio positivo para el dia <n>`, uno por tramo. En `main` esas tarifas quedaban en **0** y la corrida seguia, abaratando la alternativa |
| V-TAR-06 | Dos hojas con grillas distintas abortan nombrando la hoja | **Verificado** por prueba negativa: renombrando los dos primeros cortes de `TarifaConsolidado` a `'0-15'`/`'15-59'`, la carga falla con `La hoja TarifaConsolidado declara 12 tramos de dias (el primero 0-14) y el resto del libro declara 12 (el primero 0-30): las tarifas del maestro tienen que compartir los cortes de vigencia`. Importa porque `TarifaSitio` acumula siete hojas **por posicion** de tramo |
| V-TAR-07 | Los dos fletes de cascara autorizados quedan disponibles | **Verificado** por corrida: `PLANTA -> GRUPO_PAZ` y `PLANTA -> CONTROL_UNION` para `CASCARA` a **1 700 USD/viaje** (valor autorizado por el usuario, el mismo que el resto de los tramos de 1 200 km del libro) cargan en los 12 tramos y la cobertura de tarifas no aborta. Su uso sigue siendo marginal (`CONTROL_UNION` 0,51 tn de pico, `GRUPO_PAZ` 0) porque el evaluador elige depositos mas baratos, no porque falte el dato |
| V-TAR-08 | No se inventaron tarifas para los tramos que el usuario no autorizo | **Verificado** por lectura del libro: `BOREAS -> RUTA9` y `NORRY -> RUTA9` tienen distancia y **no** tienen fila en `TarifaFleteCamionproducto`; siguen siendo el dato que falta para ejercitar el rebalanceo deposito-deposito de ADR-066 (`V-COST-12`, `V-COST-13`), que en esta corrida descarta sus 2 103 alternativas con `TRANSFERENCIA_DEPOSITO_DEPOSITO` |
| V-TAR-09 | La campania completa desde el maestro termina sin excepciones | **Verificado** por corrida: 365 dias `Finished`. 30 656 tn pedidas, **29 439 tn exportadas**, servicio 72 % al cut-off, 16 atrasados con atraso medio 1,0 dia, 1 926 contenedores, **8 453 167 USD** de caja, `snapshot_inventario` con **descuadre 0**. Mismos volumenes fisicos que ADR-069 (el fix es de lectura de tarifas, no de politica) y 266 093 USD mas de caja, por los valores nuevos del libro y el tipo de cambio por tramo aplicado a cada concepto en pesos |
| V-TAR-10 | Regresion del contrato original con `datos/entrada_ejemplo.xlsx` | **Verificado** por corrida: 365 dias `Finished`, 30 364 tn exportadas, servicio 97 %, 4 atrasados, 1 986 contenedores, 1 421 asignaciones, 109 878 cargos y descuadre 0 — **identico** a V-MAESTRO-09. El libro legacy no tiene `Tipo_cambio` ni tarifas en pesos, y sus encabezados de vigencia son los del contrato original: leer los tramos del encabezado no lo cambia |

El **barrido no se corrio**, por pedido explicito.

## V-FECHA. Fecha de calendario por corrida (ADR-071)

Dos corridas de referencia en AnyLogic PLE 8.9.9, `nivelAuditoriaRed = COMPLETA`, las dos `Finished`: `datos/Maestro_Simulacion.xlsx` con la fecha del parametro de corrida (`2026-04-01`) y una copia de `datos/entrada_ejemplo.xlsx` con la columna `fecha_inicio_campania` agregada a la hoja `Escenario` **como fecha de Excel** (`2027-01-15`) — el libro canonico del repositorio no la trae, porque es opcional. La verificacion no es por muestra: un script externo recalcula `fecha_inicio + piso(dia) - 1` para **cada fila de cada tabla** y compara contra la columna publicada.

| Caso | Que verifica | Como se midio |
|---|---|---|
| V-FECHA-01 | La formula vale en las seis tablas, fila por fila | **Verificado** por reconciliacion externa de las dos corridas: **0 discrepancias** en 50 472 + 25 578 alternativas (`fecha`, `fecha_cutoff`), 115 282 + 109 878 cargos, 3 593 + 3 377 snapshots, 3 640 + 3 276 filas de capacidad, 10 754 + 11 308 arcos (`fecha_inicio`, `fecha_fin`) y 1 399 + 1 421 asignaciones |
| V-FECHA-02 | El dia 1 es la fecha de inicio, no el dia siguiente | **Verificado** por corrida: la primera fila de `capacidad_por_dia.csv` del maestro es `dia = 1`, `fecha = 2026-04-01`, y la del dia 2 es `2026-04-02` |
| V-FECHA-03 | La hoja `Escenario` gana al parametro de corrida | **Verificado** por corrida sobre la copia del libro canonico: con el parametro en `2026-04-01` y la hoja declarando `2027-01-15`, las seis tablas y el manifiesto de la regresion fechan `2027-01-15`. La celda vino como **fecha de Excel** (serial), no como texto: `fechaOpcional()` la convierte con la base 1899-12-30 |
| V-FECHA-04 | Una fecha inexistente aborta la carga en vez de degradar a vacio | **Verificado** por prueba negativa: con `2026-02-30` en la hoja `Escenario`, la corrida falla al arrancar con `Datos de entrada invalidos (1): - fecha_inicio_campania debe ser una fecha real en formato YYYY-MM-DD`. El calendario **no** la rueda a 2 de marzo ni la toma como vacia |
| V-FECHA-05 | El formato aceptado es solo `YYYY-MM-DD`, y el ano bisiesto se respeta | **Verificado** por prueba unitaria de `AuditoriaRed.esFechaIso()` compilada aparte: rechaza `2026-02-30`, `2026-13-01`, `01/04/2026`, `2026-4-1`, `abc`, vacio y `2026-02-29`; acepta `2026-04-01`, `2028-02-29`, `2000-02-29` y `1999-12-31` |
| V-FECHA-06 | Un dia fraccionario se fecha con el piso y un dia negativo queda vacio | **Verificado** por prueba unitaria: `fecha(1.99) = 2026-04-01` y `fecha(-1) = ""`. En las dos corridas los 14 702 + 15 082 dias fraccionarios de `ejecucion_arcos` cierran con el piso (V-FECHA-01) y **ninguna** fila de las columnas fechadas trajo un dia negativo: el centinela esta implementado y **no observado** en estos libros |
| V-FECHA-07 | Sin fecha declarada, las columnas quedan vacias y la corrida no cambia | **Verificado** por prueba unitaria (`anclarCalendario("")` -> `fecha(5) = ""`). La aritmetica es UTC: `fecha()` usa un `GregorianCalendar` en `UTC`, sin huso ni horario de verano |
| V-FECHA-08 | `capacidad_por_dia` no publica dia 0 y llega al ultimo dia de campania | **Verificado** por corrida: dia minimo **1** y maximo **364** en las dos corridas. En `main` el bucle era `0 .. duracion - 1`: publicaba un dia 0 que el modelo no tiene y cortaba dos dias antes del cierre de los costos |
| V-FECHA-09 | El esquema y el manifiesto declaran las columnas nuevas y la fecha de la corrida | **Verificado** por lectura de `resultados/esquema_auditoria.json`: `version_esquema = ADR-064.2` y las columnas `fecha`/`fecha_cutoff`/`fecha_asignacion`/`fecha_inicio`/`fecha_fin` en las seis tablas (99, 31, 31, 28, 25 y 14 columnas). El manifiesto trae `fecha_inicio_campania`, y `kpis_por_corrida.csv` la agrega por corrida |
| V-FECHA-10 | Fechar no cambia ninguna decision | **Verificado** por regresion: `entrada_ejemplo.xlsx` da 30 364 tn exportadas, servicio 97 %, 4 atrasados, 1 421 asignaciones, 109 878 cargos, 25 578 alternativas y descuadre 0 — **identico** a V-TAR-10. La campania del maestro exporta las mismas 29 439 tn de V-TAR-09 con descuadre 0 |

El **barrido no se corrio**, por pedido explicito.

## V-RED. Vista de red en vivo (ADR-072)

Corridas de referencia en AnyLogic PLE 8.9.9: campaña completa desde `datos/Maestro_Simulacion.xlsx` con `animacionRed = true`, la misma con `animacionRed = false`, el libro canónico `datos/entrada_ejemplo.xlsx` (sin coordenadas), una copia del maestro con las coordenadas multiplicadas por `1e7` y otra con una latitud fuera del país.

| Caso | Qué verifica | Cómo se midió |
|---|---|---|
| V-RED-01 | La animación no cambia ninguna decisión ni ningún costo | **Verificado** por corrida pareada: las siete tablas de la corrida con `animacionRed = true` y de la corrida con `animacionRed = false` son **idénticas byte a byte** (`asignaciones_capacidad`, `asignaciones_elegidas`, `capacidad_por_dia`, `costos_eventos`, `decisiones_alternativas`, `ejecucion_arcos`, `snapshot_inventario`), comparadas con `cmp -s` |
| V-RED-02 | Dibujar no cuesta tiempo de corrida significativo | **Verificado** por reloj: campaña completa **369 s** con animación contra **379 s** sin ella, las dos `Finished`. La diferencia está dentro del ruido entre corridas, así que el default queda en `true` |
| V-RED-03 | El mapa geográfico se dibuja desde la hoja `Ubicacion` | **Verificado** por corrida del maestro: los diez sitios en su posición relativa correcta (cúmulo de Tucumán arriba a la izquierda, ZARATE/T4/DODERO/RUTA9 en el litoral) y la leyenda declarando `Mapa geografico` |
| V-RED-04 | Un libro sin coordenadas cae al esquema y lo declara | **Verificado** por corrida de `entrada_ejemplo.xlsx`: `Finished`, nodos por columna según tipo (planta, depósitos, terminales) y la leyenda dice `Esquema por tipo de nodo: la tabla Ubicacion no trae latitud y longitud` |
| V-RED-05 | Las coordenadas sin separador decimal se normalizan | **Verificado** por corrida de una copia del maestro con lat/lon multiplicadas por `1e7`: la carga no falla y el mapa sale igual que con las coordenadas en grados |
| V-RED-06 | Una coordenada fuera del país aborta la carga | **Verificado** por prueba negativa: con `latitud = -12,5` en PLANTA la corrida falla con `La latitud de la ubicacion PLANTA (-12.5) no queda dentro del pais ni normalizando la escala`, y **no** se emite además el mensaje de "declara una sola coordenada": el problema se cuenta una vez |
| V-RED-07 | Los sitios del cúmulo son legibles | **Verificado** por lectura de pantalla: los seis sitios de Tucumán, a menos de 40 km entre sí, quedan separados con su etiqueta de dos líneas (nombre y `stock / capacidad`) sin superponerse, y ningún nodo queda fuera del área de la vista |
| V-RED-08 | El semáforo de ocupación y el grosor del arco siguen el estado | **Verificado** por corrida: NORRY/BOREAS/FRINOA pasan de verde a **rojo** al llegar a su capacidad declarada (`1200/1200`, `1800/1800`, `1009/1009`) y los tramos engrosan con las toneladas acumuladas; al cierre los cinco tramos que más movieron son `PLANTA-RUTA9` 19 433 tn, `RUTA9-T4` 17 096 tn, `PLANTA-ZARATE` 3 356 tn, `RUTA9-ZARATE` 3 212 tn y `DODERO-PLANTA` 2 797 tn |
| V-RED-09 | El encabezado identifica la corrida y no inventa un día que no existe | **Verificado** por corrida completa: al terminar muestra `dia 364 de 364 · 2027-03-30 · escenario E-00 · replica 0 · run E-00-R0`. Antes del ajuste el reloj marcaba el instante de cierre y el rótulo podía decir `dia 365 de 364` |
| V-RED-10 | La navegación entre las dos vistas funciona en las dos direcciones | **Verificado** por lectura de pantalla: *Ver red en vivo* desde el tablero y *Ver tablero* desde la red, con la corrida andando y también al terminar |

El **barrido no se corrió**, por pedido explícito.
