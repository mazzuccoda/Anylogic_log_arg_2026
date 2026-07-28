# Roadmap y estado de avance

[← Volver al índice](../README.md)

## 1. Criterios

Los porcentajes son estimaciones técnicas y deben actualizarse con evidencia de prueba.

**Este archivo es la única fuente de estado de avance del proyecto.** Ningún otro documento debe replicar tablas de avance.

El orden de ejecución vigente está en §16, derivado de la [Definición del proyecto](../09_Definicion/Definicion_del_Proyecto.md). Las fases 0 a 14 se conservan como inventario de trabajo, no como secuencia.

## 2. Resumen

| Fase | Estado | Avance |
|---|---|---:|
| 0. Respaldo y documentación inicial | Modelo versionado y `version_modelo` en las salidas del barrido | 100% |
| 1. Normalización del dominio | En curso | 75% |
| 2. Lote comercial acumulativo | Diseño validado | 15% |
| 3. Existencias físicas múltiples | Capas de inventario implementadas y verificadas en PLE | 90% |
| 4. Transferencias parciales | Planta → depósito parcial, verificada en PLE; falta depósito → depósito | 80% |
| 5. Reserva profesional | Reservas trazables por capa y pedido; falta compromiso y reserva parcial | 60% |
| 6. Contenedores individuales | Contenedores reales por pedido y tramo vacío terminal → origen con round trip, verificado en PLE (ADR-050); falta el resto del ciclo del vacío y su tarifa | 85% |
| 7. Consolidación directa | Posiciones finitas por día, circuito por pedido y consolidación en planta con flujo físico, verificado en PLE (ADR-050) | 95% |
| 8. Cross docking | Cupo diario por sitio, tarifa propia, sin almacenaje y con paso físico por el depósito; cross dock en terminal es el circuito 4 (ADR-050); falta cross dock parcial | 85% |
| 9. Terminal | Parcial | 20% |
| 10. Registro de costos | Parcial; tarifas ya vienen de tablas | 35% |
| 11. Planificador | Parcial | 25% |
| 12. Reemplazo de Envio | Pendiente | 0% |
| 13. KPIs y experimentos | Barrido de 14 escenarios × 30 réplicas verificado en PLE, con las dos flotas consumiéndose de verdad y KPIs por circuito (ADR-044, ADR-050) | 90% |
| 14. Optimización avanzada | Futuro | 0% |
| 18. Contrato de datos del rediseño logístico | Umbrales porcentuales, forecast, política de frío propio, `contenedores_por_dia`, oportunidad y penalidad, en generador, importador y plantilla | 100% |
| 19. Frío propio y sobrecarga sin pérdida | Planta sin descarte, métricas de sobrecarga, forecast perfecto, caja vs. económico, verificado en PLE (ADR-048, ADR-049) | 100% |
| 21. Flujo físico con los cuatro circuitos | `SelectOutput`, tramo vacío, estiba en el sitio real, circuito por pedido, terminal sin pool, 420 corridas en PLE (ADR-050) | 100% |

## 3. Fase 0 — Respaldo

- [x] Mantener modelo funcional.
- [x] Restaurar función accidentalmente vaciada.
- [x] Crear repositorio.
- [x] Crear especificación técnica maestra.
- [x] Crear documentación navegable inicial.
- [x] Versionar el `.alp` en el repositorio.
- [x] Versionar el código exportado por agente en `model_src/` (`tools/exportar_modelo.py`, ADR-035).
- [x] Inventariar el modelo real: [Inventario del modelo](../03_Logica/Inventario_del_Modelo.md).
- [x] Crear `CHANGELOG.md`.
- [x] Volcar la versión del modelo en las salidas: `resultados/kpis_por_corrida.csv` lleva `version_modelo` en cada fila.

La fase había sido declarada completada al 100% sin que el modelo estuviera versionado. Ya lo está, y las salidas del barrido dicen con qué versión se produjeron.

## 4. Fase 1 — Dominio

- [x] Auditar todas las Option Lists y variables reales de los agentes: [Inventario del modelo](../03_Logica/Inventario_del_Modelo.md).
- [x] Definir productos y contenedores.
- [x] Definir estrategias.
- [x] Crear `ContenedorExportacion`.
- [x] Crear `PlanLogistico`.
- [x] Ampliar `Pedido`.
- [x] Separar parámetros de estado en `Main`, `Planta`, `Deposito` y `Terminal` (H-02, ADR-033). Pendiente en los agentes de entidad, junto con el rediseño de inventario.
- [x] Unificar los eventos diarios en `pasoDiario` con orden fijo (H-06, H-07, ADR-034).
- [x] Eliminar el doble conteo de almacenaje (H-04).
- [x] Sacar del código los datos maestros, las tarifas, las distancias, la producción y la demanda: hoy están en las tablas de `DatosEntrada`, generadas por `GeneradorSintetico` y validadas al arrancar (H-01, ADR-036, ADR-037).
- [ ] Eliminar nombres duplicados o ambiguos.
- [x] Importador de Excel que llena las mismas tablas (fase 2 del contrato de datos, ADR-038): `ImportadorExcel`, el parámetro `origenDatos` y la plantilla `datos/entrada_ejemplo.xlsx`.
- [ ] Tablas del contrato todavía sin implementar: `TiemposOperativos`, tarifas por contenedor y `LoteInicial`.

## 5. Fase 2 — Lote comercial

Implementado en el modelo y verificado en PLE (ADR-047).

- [x] Validar que el lote no es producción diaria.
- [x] Validar producción durante varios días.
- [x] Validar despacho antes del cierre.
- [x] Agregar cliente: `LoteProducto.cliente`, desde `Escenario.cliente_default`.
- [x] Agregar calidad: `LoteProducto.calidad`, desde `Escenario.calidad_default`.
- [x] Agregar toneladas objetivo: `LoteProducto.toneladasObjetivo`, desde `Producto.toneladas_objetivo_lote_tn`.
- [x] Agregar estado comercial: `LoteProducto.estadoComercial` y la lista de opciones `EstadoComercialLote` (`ABIERTO`/`CERRADO`).
- [x] Implementar lote abierto: `Main.buscarLoteComercialAbierto(producto, cliente, calidad)`, a lo sumo uno abierto por combinación.
- [x] Modificar `crearLoteEnPlanta()`: la producción diaria entra como una capa nueva del mismo `idLote` y acumula `toneladasIniciales`; el lote se cierra al alcanzar el objetivo y sólo entonces la producción siguiente abre una identidad nueva.
- [x] Validar casos V-003 y V-004: ambos cumplen, con la evidencia en el [plan de validación](../06_Validacion/Plan_de_Validacion.md).
- [x] Verificado en PLE: build limpio, campaña completa sin excepciones, 360 corridas `Finished` y CSV etiquetado `fase-17`.
- [ ] Pendiente: reserva y despacho **por lote comercial**. Hoy los pedidos siguen reservando por producto contra las capas del depósito en FIFO, así que cliente y calidad son trazabilidad y todavía no restringen a quién se le puede asignar un lote (fase 5).

## 6. Fase 3 — Existencias físicas

Corregido tras el inventario (hallazgo H-03): las listas por ubicación figuraban como implementadas, pero no existen en el `.alp`. Se reemplazaron por capas de inventario (ADR-021) implementadas como clases Java (ADR-030). Implementado en el modelo y verificado en PLE.

- [x] Crear la clase `Capa` y su colección por lote (`Capa`, `Inventario`).
- [x] Derivar el stock de cada ubicación de sus capas (ADR-023): `Planta.getStock()` y `Deposito.getStock()` consultan `Main.inventario`.
- [x] Consultar saldo y saldo libre por ubicación (`stock`, `reservado`, `libre`).
- [x] Retirar saldo libre con consumo FIFO (ADR-022): `retirarLibre`, `mover`, `moverLote`.
- [x] Reservar por ubicación (`reservar`).
- [x] Liberar reserva (`liberarReserva`).
- [x] Despachar reserva (`despachar`).
- [x] Validar integridad de listas (`Inventario.validar()`, invocada cada día en `pasoDiario`).
- [x] Reconciliar con stock de agentes: los agentes ya no tienen saldo propio que reconciliar.

## 7. Fase 4 — Transferencia parcial

- [x] Diseñar contrato transaccional: el movimiento se acota antes de tocar el inventario, así que no hay estado intermedio que revertir.
- [x] Crear `transferirToneladasLote(lote, destino, toneladas)`, que devuelve las toneladas efectivamente movidas.
- [x] Aplicar retiro parcial en planta (FIFO sobre las capas del lote en `PLANTA`).
- [x] Aplicar recepción parcial en depósito, acotada por `getEspacioDisponible()`.
- [x] Registrar flete sobre lo movido, no sobre lo pedido.
- [x] Revertir ante fallo: innecesario, ver contrato.
- [x] Reemplazar uso de `transferirLoteCompleto()`, que se eliminó.
- [ ] Validar V-006 y V-007.
- [ ] Falta el movimiento entre depósitos: hoy sólo se transfiere planta → depósito.

## 8. Fase 5 — Reserva

- [ ] Comenzar por lote solicitado.
- [ ] Localizar saldos físicos.
- [x] Definir orden de consumo: FIFO por `diaIngreso` (ADR-022).
- [x] Reserva incremental sobre capas (ADR-024); retirado `crearLoteReservadoDesdeDivision` (H-05). Falta el compromiso sobre producción futura y la aceptación de reserva parcial: hoy la reserva sigue siendo todo o nada.
- [x] Asociar reservas al pedido: cada reserva de capa guarda `codigoPedido` y `diaReserva`.
- [x] Liberar reservas canceladas (`Inventario.liberarReserva(codigoPedido)`).
- [ ] Validar V-008 y V-009.

## 9. Fase 6 — Contenedores

- [x] Crear tipo de agente.
- [x] Definir variables iniciales.
- [x] Crear prueba técnica de un contenedor. Reemplazada por la lógica real: se eliminaron `Main.pruebaCrearContenedor()`, `Pedido.probarCalculoContenedores()` y el botón de prueba.
- [x] Crear N por pedido (`Main.crearContenedoresParaPedido()`).
- [x] Distribuir toneladas con la capacidad del tipo de contenedor del producto.
- [x] Manejar último parcial.
- [x] Asociar lote y reserva: cada contenedor guarda el lote que más aporta a su carga, tomado de las capas reservadas del pedido en orden FIFO.
- [x] Implementar estados a lo largo del flujo: `ESPERANDO_PROGRAMACION` → `ESPERANDO_CARGA` → `CONSOLIDANDO` → `EN_TRANSITO_CARGADO` → `INGRESADO_TERMINAL` → `EXPORTADO`.
- [ ] Ciclo del contenedor vacío (`ESPERANDO_RETIRO_VACIO`, `EN_TRANSITO_VACIO`): sin modelar, y por eso `horaRetiroVacio` y `horaLlegadaLugarCarga` quedan en `-1`.
- [ ] Validar V-010 y V-011.

## 10. Fase 7 — Consolidación

- [x] Recursos en depósito: capacidad de consolidación diaria por sitio (`contenedores_por_dia` desde la fase 19; antes `posiciones_consolidacion × contenedores_por_posicion_dia`), leída de la tabla `Ubicacion`.
- [x] Tiempos y colas: `Main.despacharContenedoresPendientes()` despacha por día lo que la capacidad del sitio permite, con prioridad por fecha límite del pedido; el resto espera y acumula `diasEsperaPosicion`.
- [x] Costos reales: el servicio de consolidación se cobra con la tarifa del sitio donde se estiba el contenedor, y se acumula en ese sitio.
- [x] Cambios de estado del contenedor: `ESPERANDO_PROGRAMACION` mientras espera posición y `CONSOLIDANDO` cuando la toma.
- [x] Estrategia: parámetro `Main.estrategiaConsolidacion` (`CONSOLIDACION_DEPOSITO` por defecto, `CONSOLIDACION_TERMINAL` para el comportamiento anterior).
- [ ] Recursos en planta: la consolidación en planta (`CONSOLIDACION_PLANTA`) todavía no está implementada; el pedido se reserva y se estiba desde un depósito.
- [ ] Descuento de reserva en el momento de la estiba: la reserva se sigue descontando al entrar el envío al flujo, que con consolidación en depósito es el mismo día pero no el mismo instante.

## 11. Fase 8 — Cross docking

- [x] Validar concepto de sincronización.
- [x] Validar regla del mismo día.
- [x] Validar exclusión de IN/storage/OUT.
- [x] Crear recursos (`posiciones_cross_dock`, contadas por día y por sitio).
- [x] Sincronizar camiones: la operación necesita que la flota de producto del día alcance para el pedido entero (ADR-044).
- [x] Gestionar reprogramación (sin cupo, sin camión o sin stock en planta el pedido no se cruza y compite de nuevo al día siguiente).
- [x] Exención de almacenaje para lo que cruza (ADR-010).
- [ ] Colas independientes: el contenedor de cross dock usa el mismo flujo que el resto, con prioridad de despacho.
- [ ] Cross dock parcial: requiere reserva parcial (fase 5), hoy el pedido se cruza entero o no se cruza.
- [ ] Cross dock en terminal (`CROSS_DOCK_TERMINAL`).
- [ ] Validar V-013.

## 11b. Fase 13 — Escenarios, réplicas y KPIs

- [x] Escenario como fila: `GeneradorSintetico.escenario(id, semilla)` y la hoja `Escenario` del Excel gobiernan las 13 configuraciones; el experimento no conoce ningún escenario en particular.
- [x] Experimento `Escenarios` (Parameter Variation freeform) con dos dimensiones: `idEscenario` y `replica`.
- [x] Réplicas reproducibles: `semilla = semillaBase + replica`, calculada dentro del modelo.
- [x] KPIs de cierre de corrida como funciones de `Main`.
- [x] `resultados/kpis_por_corrida.csv` con `version_modelo`, `id_escenario`, `replica` y `semilla`.
- [x] Media, desvío, mínimo, máximo y P95 por escenario, con delta absoluto y porcentual contra E-00.
- [x] E-09 determinístico verificado: desvío exactamente 0 en 30 réplicas.
- [x] Barrido de flota útil (ADR-044): la flota de producto se consume en camión-día y cada contenedor toma un portacontenedor del pool. E-01 (1/1) baja el nivel de servicio a 0,908 y sube el atraso a 3,17 días; E-02 (6/8) no mejora a E-00, que es la respuesta a P2 y P6.
- [ ] Cargar y descargar no le consumen jornada al camión: la planta no tiene velocidades de carga en las tablas.
- [x] Almacenaje en planta: resuelto como costo de oportunidad separado del costo de caja (ADR-049); el de caja sigue sin tarifa de planta a propósito.
- [ ] Solapamiento de intervalos en la comparación contra E-00.
- [ ] Escenario de capacidad de planta y de política de prioridad (la política es hoy única).

## 11c. Fase 15 — Tablero, indicadores y manual

- [x] Tablero en la vista de `Main`: ocho paneles (campaña, producción y stock, transporte y flota, pedidos y servicio, inventario y reservas, contenedores y envíos, consolidación y cross dock, costos) en lugar de los 54 textos sueltos anteriores (ADR-045).
- [x] Cinco gráficos de evolución: stock en planta, stock por depósito, utilización de las dos flotas, costos acumulados y pedidos por estado, con la ventana temporal igual al horizonte de la corrida.
- [x] Los indicadores del tablero usan las mismas funciones que el CSV del barrido, así que tablero y barrido no pueden discrepar. Verificado en PLE: E-00 réplica 0 da 1.922.761 USD, 95% de servicio y 1.173 viajes en los dos lados.
- [x] Variables y parámetros de `Main` ocultos en la corrida (`PresentationFlag = false`): antes se dibujaban sobre el tablero.
- [x] [Manual de uso](../10_Manual/Manual_de_Uso.md) y [Tablero e indicadores](../10_Manual/Tablero_e_Indicadores.md).
- [ ] Intervalos de confianza y comparación entre escenarios en pantalla: hoy sólo están en la salida del barrido.
- [ ] Panel de terminal: la terminal todavía no tiene cola ni costo propio (fase 9).

## 11d. Fase 16 — Tablero del barrido

- [x] Pantalla propia del experimento `Escenarios` (antes estaba vacía): avance de las corridas, lectura del barrido, medias por escenario y frente de decisión (ADR-046).
- [x] La tabla muestra la configuración de cada escenario (camiones, factor de depósito, factor de producción, cross dock, sitio de consolidación) tomada de la corrida, no de una lista escrita a mano en el tablero.
- [x] Media y desvío del costo lado a lado, para que no se lea como diferencia lo que no supera la variabilidad entre réplicas.
- [x] Frente de decisión: barras relativas al rango del barrido y marca de escenario dominado (otro da a la vez mejor servicio y menor costo por tonelada).
- [x] Recomendación con restricción de servicio: el más barato entre los que alcanzan 95%.
- [x] Verificado en PLE: 360 corridas `Finished`, medias sin cambios respecto de la fase 15 y CSV etiquetado `fase-16`.
- [ ] Intervalos de confianza y prueba estadística contra E-00 en el tablero (hoy media, desvío y delta).
- [ ] Marcar automáticamente los escenarios no comparables (E-06 y E-07 cambian producción); hoy es una advertencia escrita en el panel.

## 11e. Fases 18 y 19 — Contrato del rediseño logístico y frío propio

Implementado en el modelo y verificado en PLE (ADR-048, ADR-049).

- [x] Contrato de datos: `umbral_alerta_pct`, `umbral_objetivo_pct`, `umbral_sobrecarga_pct`, `dias_forecast` y `politica_frio_propio` en `Escenario`; `contenedores_por_dia` en `Ubicacion` reemplazando a `posiciones_consolidacion × contenedores_por_posicion_dia`; `oportunidad_usd_tn_dia` y `penalidad_sobrecarga_usd_tn_dia` en `TarifaAlmacenamiento`. Generador sintético, validaciones, importador de Excel, `tools/VolcarDatos.java` y plantilla, todo por el mismo camino.
- [x] La planta no descarta producto: `producir()` ingresa la producción completa del plan y `excedente*`, `nivelActivacion*` y `stockObjetivo*` desaparecen de `Planta`.
- [x] Métricas de ocupación: `tonDiaSobreNominalPlanta`, `tonDiaSobreCriticoPlanta`, `diasSobrecargaPlanta` y `picoOcupacionPlantaPct`, registradas una vez por día.
- [x] Forecast perfecto de `dias_forecast` días sobre el plan de producción, y demanda proyectada sobre las obligaciones pendientes.
- [x] Políticas `FLEXIBLE` (retener en frío propio y transferir sólo lo necesario) y `REACTIVA` (vaciado anterior con umbrales en porcentaje), elegidas por escenario; E-12 compara las dos.
- [x] Costo de caja y costo económico separados, con seis KPIs nuevos en el CSV etiquetado `fase-19`.
- [x] Verificado en PLE: build limpio, campaña completa sin excepciones, 390 corridas `Finished`, corrida desde Excel idéntica a la sintética y cero pérdida de producto.
- [ ] Pendiente: forecast con error, penalidad de sobrecarga calibrada con datos reales y capacidad de frío propio como variable de decisión del barrido.

## 11f. C1 y C2 — Contrato de costos y registro auditable

Implementado en el modelo y verificado en PLE (ADR-051, ADR-052).

- [x] Cuatro tablas de tarifas con `unidad`, `proveedor`, `vigencia_desde`, `vigencia_hasta` y `habilitada`: `TarifaFleteProducto`, `TarifaRoundTrip`, `TarifaSitio` y `TarifaEspera`. Generador sintético, validaciones, importador de Excel, `tools/VolcarDatos.java` y plantilla, todo por el mismo camino.
- [x] Consultas que resuelven por día de campaña y abortan si no hay cobertura o si hay dos filas vigentes para la misma clave.
- [x] El flete de producto y el round trip dejan de ser fórmulas cableadas: se cobran con la tarifa de la tabla, en `USD_VIAJE` o `USD_TN` según la fila, y el round trip se devenga al completar el ciclo.
- [x] `RegistroCostos`: cargo inmutable con identidad, categoría, tipo (caja/económico), pedido, contenedor, lote, producto, origen, destino, sitio, estrategia, proveedor, unidad, cantidad, tarifa e importe calculado; idempotencia por operación; totales por ocho dimensiones; volcado opcional a csv.
- [x] `costoTotalCampania()` y el económico salen del registro; los acumuladores de los agentes quedan como vistas y `reconciliarCostos()` los compara contra el registro todos los días y al cierre.
- [x] Verificado en PLE: build limpio, 420 corridas `Finished` comparadas fila por fila contra `fase-21` (idénticas salvo 1 × 10⁻⁴ USD en una réplica) y corrida desde Excel idéntica a la sintética.
- [ ] Pendiente en C7: movimiento depósito → depósito y ciclo completo del vacío.

## 11g. C3 y C4 — Costeo por circuito y casos V-COST

Implementado en el modelo y verificado en PLE (ADR-053), versión `fase-22`.

- [x] IN y OUT de depósito devengados en el evento físico: IN cuando la capa entra al almacenamiento, OUT cuando el envío se despacha, y ninguno de los dos cuando el producto sale del frío propio o cruza en cross dock.
- [x] THC, costo terminal y despachante por contenedor completo, al ingresar el contenedor cargado a la terminal —o al armarlo ahí, en el circuito de terminal—, con el día del devengo guardado en `Envio.diaCargosTerminal`.
- [x] Consolidación y cross dock por contenedor (`USD_CONTENEDOR`) en el sitio donde ocurren, con el contenedor parcial cobrado como completo.
- [x] Espera de camión de producto y de portacontenedor sobre la franquicia, en carga, descarga y terminal. Con los tiempos sintéticos el cargo es 0.
- [x] El circuito de terminal no paga round trip y sí paga el flete a granel hasta el puerto.
- [x] Vistas de costo end-to-end, incremental e histórica sobre el registro, y once columnas de descomposición por categoría en el CSV, más la descomposición en el panel de costos de `Main`.
- [x] Auditoría por circuito: `costoEsperadoCircuito()` reconstruye el importe desde las tarifas y la corrida aborta si no coincide con lo devengado. Ejecuta V-COST-01 a V-COST-05 y V-COST-07 en cada envío.
- [x] Verificado en PLE: build limpio, campaña completa de 183 días sin excepciones, 420 corridas `Finished` con CSV etiquetado `fase-22`, y los KPIs físicos y de servicio idénticos a `fase-21` fila por fila. El costo sube entre 11,8 % y 37,2 % según escenario.
- [ ] Pendiente: V-COST-06 (transferencia depósito → depósito) queda documentado sin movimiento físico, por decisión del usuario. Los valores de IN, OUT, THC, costo terminal y despachante son supuestos (`SUPUESTO_C3`) hasta que se carguen los reales.

## 12. Fase 9 — Terminal

- [ ] Entrega de vacíos.
- [ ] Cola de ingreso cargado.
- [x] Costo terminal (C3, ADR-053).
- [x] THC (C3, ADR-053).
- [x] Cierre de ciclo: el round trip se devenga al completarse (C1, ADR-051).
- [ ] Liberación del camión.

## 13. Fase 10 — Costos

- [x] Separar histórico, incremental y end-to-end.
- [x] Definir categorías.
- [x] Crear tablas tarifarias (C1, ADR-051).
- [x] Crear registro auditable (C2, ADR-052).
- [ ] Evitar duplicación.
- [ ] Reconciliar estimado y real.

## 14. Fase 11 — Planificador

- [x] Crear agente `PlanLogistico`.
- [x] Crear costeo básico.
- [x] Crear validación básica.
- [ ] Generar todas las alternativas aplicables.
- [ ] Validar capacidad, recursos y fecha.
- [ ] Seleccionar menor costo incremental factible.
- [ ] Ejecutar plan seleccionado.

## 15. Próximo bloque recomendado

Orden estricto:

1. inventariar el código real actual;
2. cerrar diseño de lote comercial;
3. implementar reserva por ubicación;
4. implementar transferencia parcial;
5. recién después conectar contenedores a la ejecución.

## 16. Orden de ejecución vigente

Reordenamiento derivado del uso estratégico del modelo (ADR-018). Cierra el dominio y los datos antes de implementar, y deja el planificador después del ejecutor porque estimar costo y tiempo exige que exista quién los produzca.

| Paso | Contenido | Fases originales que absorbe |
|---|---|---|
| 0 | Versionar `.alp`, código exportado, CHANGELOG y `versionModelo` | 0 |
| 1 | Cerrar definición y ADR nuevos (licencia resuelta: se trabaja en PLE, ADR-020) | — |
| 1b | Separar parámetros de estado (ADR-033) y unificar la secuencia diaria (ADR-034) | — |
| 2 | Datos maestros y tarifas como tablas, generador sintético e importador de Excel (ADR-029, ADR-036, ADR-037, ADR-038) — **hecho para el alcance del [estado de implementación](../09_Definicion/Contrato_de_Datos.md)** | parte de 1 y 10 |
| 3 | Capas de inventario y stock derivado (ADR-021, 022, 023) | 3 |
| 4 | Lote comercial acumulativo sobre capas | 2 |
| 5 | Reservas trazables, compromiso y prioridad (ADR-024, 025, 026) | 5 |
| 6 | Transferencia parcial transaccional | 4 |
| 7 | Registro de costos idempotente y suite de verificación ejecutable | 10 |
| 8 | Ejecución con recursos de conteo diario (ADR-019): consolidación, terminal, cross dock | 6, 7, 8, 9, 12 |
| 9 | Planificador de alternativas y selección | 11 |
| 10 | Escenarios, réplicas y KPIs de dimensionamiento (ADR-028) | 13 |

La fase 14 (optimización avanzada) queda fuera del alcance comprometido.

## 17. Definición de terminado

Una fase está terminada cuando:

- compila;
- cumple casos de prueba;
- reconcilia saldos;
- reconcilia costos;
- está documentada;
- tiene respaldo versionado;
- no rompe regresión.
