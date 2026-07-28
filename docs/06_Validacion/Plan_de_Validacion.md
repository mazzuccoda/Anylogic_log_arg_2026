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
