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
- [x] Reserva incremental sobre capas (ADR-024); retirado `crearLoteReservadoDesdeDivision` (H-05).
- [x] **Reserva parcial y multi-origen (ADR-055):** `reservarParcialPedido()` conserva lo que consiguió y `asignarParcialPedido()` recorre orígenes hasta cubrir el pedido. Falta el compromiso sobre producción futura.
- [x] Asociar reservas al pedido: cada reserva de capa guarda la clave `pedido|asignación`, el `codigoPedido` y `diaReserva`.
- [x] Liberar reservas canceladas (`Inventario.liberarReserva(codigoPedido)`).
- [ ] Validar V-008 y V-009.

## 9. Fase 6 — Contenedores

- [x] Crear tipo de agente.
- [x] Definir variables iniciales.
- [x] Crear prueba técnica de un contenedor. Reemplazada por la lógica real: se eliminaron `Main.pruebaCrearContenedor()`, `Pedido.probarCalculoContenedores()` y el botón de prueba.
- [x] Crear N por asignación (`Main.crearContenedoresParaAsignacion()`, ADR-055; antes `crearContenedoresParaPedido()`).
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
- [x] Cross dock parcial: `esCrossDock` pasa de ser un atributo del pedido a serlo de la asignación y del envío, así que una fracción puede cruzarse y otra almacenarse (ADR-055).
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

## 11h. C5 y C6 — Escenarios económicos y evaluador de circuitos

Implementado en el modelo y verificado en PLE (ADR-054), versión `fase-23`.

- [x] `politica_seleccion` en el contrato: las `FIJA_*` y `MANUAL` reproducen la conducta previa y quedan como regresión; `PRIORIDAD_FRIO_PROPIO`, `MENOR_COSTO_INCREMENTAL_FACTIBLE` y `MENOR_COSTO_END_TO_END_FACTIBLE` activan el evaluador.
- [x] `servicio_minimo_proyectado`, cuatro factores de sensibilidad tarifaria y tres de capacidad (`factor_consolidacion_planta`, `factor_cupo_cross_dock`, `factor_capacidad_terminal`), por generador sintético, validación, importador de Excel, `tools/VolcarDatos.java` y plantilla.
- [x] `AlternativaCircuito` (clase Java plana) y `PlanLogistico` poblado: origen, sitio de estiba, circuito, factibilidad con motivo, fecha estimada, servicio proyectado y costo por componente, con las vistas incremental, histórica y end-to-end.
- [x] Evaluador en `Main`: genera alternativas desde el stock real, verifica factibilidad **sin mutar inventario**, costea, ordena por servicio y después por costo, y ejecuta la elegida con el flujo físico que ya existía.
- [x] Cinco KPIs de decisión en el CSV (`planes_emitidos`, `planes_tardios`, `alternativas_evaluadas`, `alternativas_descartadas`, `pedidos_sin_alternativa_factible`), en 0 cuando la política es fija.
- [x] 22 escenarios nuevos (E-14 a E-35): estrategia, sensibilidad tarifaria, permanencia y capacidad; el barrido pasa a 36 × 30 = 1 080 corridas.
- [x] Verificado en PLE: build limpio, 1 080 corridas `Finished` con CSV `fase-23`, los 14 escenarios de política fija idénticos a `fase-22` fila por fila, y la corrida desde Excel idéntica a la sintética.
- [ ] Pendiente en C7: la alternativa depósito → depósito se genera siempre descartada, porque el movimiento físico no existe.

## 11i. MOD — Pedidos parciales y transferencia preventiva

Implementado en el modelo y verificado en PLE (ADR-055/056), versión `fase-24`.

- [x] `AsignacionPedido` (clase Java plana): el compromiso deja de ser el pedido y pasa a ser la asignación, con su propio ciclo de vida en toneladas y su clave de reserva `codigoPedido|idAsignacion`.
- [x] Reserva parcial que **conserva** lo conseguido, y cobertura del pedido desde varios orígenes en uno o varios días, tanto con política económica como con política fija.
- [x] Contenedorización progresiva con último parcial condicionado (pedido completamente asignado, vencido o fin de campaña), para no despachar a medio llenar pagando contenedor completo.
- [x] Despacho y entrega por clave de reserva; `ATRASADO` conserva lo entregado y el origen de cada fracción.
- [x] Almacenaje por clave de reserva: un pedido con una fracción que cruza y otra almacenada paga por la segunda.
- [x] Componente preventivo alerta/objetivo dentro de `FLEXIBLE`, combinado con `max` y no con suma; `toneladasASacarReactiva()` sin cambios.
- [x] Reparto de la transferencia entre todos los depósitos factibles, con prioridad de espacio cuando la planta está en sobrecarga crítica y sin agregar volumen.
- [x] Diagnóstico de descartes con una única función compartida con la selección, y `debugPlanificacion` para pedidos y depósitos.
- [x] Trece KPIs nuevos en el CSV y las validaciones diarias C-01 (identidad del pedido) y C-02 (nada de lo producido se pierde).
- [x] Verificado en PLE: build limpio, campaña completa sin excepciones y 1 080 corridas `Finished` con CSV `fase-24`.
- [ ] Pendiente: no hay reoptimización ni cancelación de asignaciones vivas; una reserva parcial retiene stock que otro pedido podría usar mejor.
- [x] **Resuelto (ADR-061):** la flota de producto son camiones físicos con viajes que pueden durar varios días. Queda pendiente sólo el barrido de escenarios de flota.
- [ ] Pendiente, y bloqueante para los datos reales: un viaje que no cabe en una jornada no puede empezar. La flota de producto se consume como capacidad diaria (ADR-044) y un viaje redondo de 1 200 km cuesta 3,43 camión-día, así que con 3 camiones el destino es inalcanzable y el diagnóstico responde `SIN_FLOTA` siempre. En `datos/entrada_ejemplo.xlsx` eso deja las 12 031 tn de cáscara encerradas en planta, porque su única capacidad de depósito está a 1 200 km. Hay que permitir que un viaje ocupe un camión durante varios días.

## 11j. MOD — Stock inicial de campaña

Implementado en el modelo y verificado en PLE (ADR-057), versión `fase-25`.

- [x] Hoja `StockInicial` opcional en el contrato de datos, con identidad de lote histórico `codigo_lote + producto + cliente + calidad` y fechas negativas.
- [x] `cargarStockInicial()` crea lotes con `esStockInicial` y **capas reales** de `Inventario`; sin producción ficticia, transferencia ficticia ni saldos paralelos. Invocado desde el arranque del agente, antes del primer paso diario.
- [x] `validarStockInicial()`: capacidad dura en depósito, advertencia en planta (ADR-048). Identidad, fechas y ubicación se validan en el contrato con el criterio de ADR-037.
- [x] C-02 pasa a `stock inicial + producción = stock + en proceso + entregado`, y `toneladaDiaEnStock()` acota la antigüedad al horizonte para no imputar almacenaje anterior al día 0.
- [x] Siete KPIs nuevos: stock inicial cargado, consumido y remanente, producción de campaña, disponibilidad total, demanda planificada y déficit estructural.
- [x] Verificado en PLE: build limpio, barrido de 1 080 corridas **idéntico** a `fase-24` en las 55 columnas comunes, y campaña completa con 3 509 tn de stock inicial.
- [ ] Pendiente: reservas de stock inicial por cliente y calidad como restricción y no sólo como identidad (fase 5), y una tabla de compromisos previos —producto ya vendido antes del día 0— que hoy no existe.

## 11k. MOD — Crédito de holding futuro en el evaluador (ADR-065)

Implementado en `RedLogistica_Exportacion.alp` y `model_src/` (ADR-065). **Pendiente de compilar y correr en el IDE de AnyLogic** — se editó y regeneró el espejo fuera de AnyLogic, sin motor de simulación disponible en ese entorno, así que falta la compilación real y las corridas de verificación antes de marcar la fase como terminada (§17). Origina en el hallazgo de que `seleccionarDeposito()` (planta→depósito) proyecta 30 días de storage futuro con `diasEstimadosAlmacenamiento`, pero `costearAlternativa()` (evaluador de circuitos, depósito/planta→pedido) no tenía el equivalente: comparaba sólo el flete/estiba de despachar hoy, nunca lo que le va a costar a esa tonelada seguir parada.

- [x] `tarifaHoldingOrigen(idOrigen, producto)`: `oportunidadUsdTnDia` si `idOrigen == "PLANTA"`, `storageUsdTnDia` del depósito en cualquier otro caso.
- [x] `horizonteHoldingEvitado()`: reusa `diasEstimadosAlmacenamiento`, acotado por `duracionCampaniaDias - diaCampania()`.
- [x] `AlternativaCircuito.costoHoldingEvitado` (no auditado, no entra a `costoIncremental`/`costoEndToEnd`) y `costoUnitarioRankingSegun(endToEnd)`, que reemplaza a `costoUnitarioSegun(endToEnd)` **sólo** dentro del comparador de `ordenarAlternativas()` — verificado por diff que los otros seis usos de `costoUnitarioSegun()` (reportes/KPI) quedan intactos.
- [x] No toca el desempate de `PRIORIDAD_FRIO_PROPIO` (sigue decidiendo por origen antes de llegar al costo) ni ninguna política `FIJA_*` — no se tocó ninguna otra función.
- [ ] Caso de regresión obligatorio, **pendiente de correr**: con `diasEstimadosAlmacenamiento = 0`, comportamiento idéntico byte a byte al actual (`V-COST-11`, `docs/06_Validacion/Plan_de_Validacion.md`).
- [ ] Pendiente de compilar en el IDE de AnyLogic — este entorno no tiene el compilador ni el motor de simulación; sólo se validó que el `.alp` sigue siendo XML bien formado y que el espejo `model_src/` se regenera sin errores.
- [ ] Pendiente de discusión aparte, fuera de alcance de este Mod: si el crédito debería también poder ganarle al desempate de `PRIORIDAD_FRIO_PROPIO` — cambiaría la semántica ya aceptada de esa política.

## 11l. MOD — Afinidad pedido-depósito y rebalanceo entre depósitos (ADR-066)

Implementado en `RedLogistica_Exportacion.alp` y `model_src/`. **Pendiente de compilar y correr en el IDE de AnyLogic** (mismo estado que 11k — sin motor de simulación en el entorno donde se implementó).

- [x] `Pedido.depositoComprometido` / `PedidoPlan.depositoComprometido`, columna opcional `deposito_comprometido` en `PedidoPlan` (vacía por defecto, no rompe libros existentes).
- [x] `ordenarAlternativas()`: nuevo criterio de desempate por depósito comprometido, entre servicio y frío propio — orden final: servicio → compromiso → frío propio → costo.
- [x] `revisarRebalanceoEntreDepositos()` (paso 6b de la secuencia diaria): mueve stock de depósitos sin cross dock (`capacidadCrossDockDia <= 0`) y con antigüedad ≥ `diasEstimadosAlmacenamiento` hacia el mejor destino disponible.
- [x] `mejorDestinoRebalanceo()`, `transferirEntreDepositos()`, `buscarLoteMasAntiguoEnDeposito()`, `registrarOutDepositoTransferencia()` — costo según V-COST-06 (OUT + flete + IN, sin contenedor), sin tocar `costoIncremental`/`costoEndToEnd` del evaluador.
- [x] Nuevo acumulador `costoFleteEntreDepositos`, sumado a la reconciliación de `FLETE_PRODUCTO` sin mezclarse con `costoFletePlantaDeposito`.
- [x] Sin datos de tarifa/distancia depósito-depósito, el mecanismo no hace nada (no crashea, no asume costo cero) — verificado que `transferirEntreDepositos`/`mejorDestinoRebalanceo` chequean `hayTarifaFlete`/`hayTarifaSitio`/`distanciaKmSimetrica` antes de mover.
- [x] **Confirmado empíricamente que sin esos datos no hace nada:** primera corrida post-implementación, cero operaciones `REB-` en las cuatro tablas de auditoría y stock por depósito idéntico al de antes del Mod.
- [x] Traza de diagnóstico bajo `debugPlanificacion` cuando `mejorDestinoRebalanceo()` no encuentra destino — visible en la consola de AnyLogic sin exportar CSV.
- [x] **Filas depósito→depósito cargadas** (`BOREAS→RUTA9`, `NORRY→RUTA9` en `TarifaFleteProducto`/`Distancia`) y **confirmado con una corrida real:** 8.060 tn movidas en 386 viajes, stock de cierre en Boreas+Norry cayó de 2.446,7 a 21,8 tn (−99 %), costo total de campaña bajó ~USD 155.000 pese al costo del rebalanceo (USD 688.576) — detalle completo en la nota de implementación de ADR-066.
- [x] Caso de regresión (`V-COST-13`, rebalanceo) confirmado por la corrida real de arriba.
- [ ] **Pendiente:** correr `V-COST-12` (afinidad pedido-depósito, D1) con un pedido que traiga `deposito_comprometido` cargado — todavía no se probó esa mitad del Mod.
- [ ] Pendiente de compilar en el IDE de AnyLogic con verificación explícita de build limpio — las corridas hechas hasta ahora ya prueban que el modelo compila y corre, pero no hubo un chequeo formal documentado.
- [ ] Fuera de alcance de este Mod: integrar el rebalanceo con la agenda de flota multidiaria (ADR-061) — usa capacidad diaria agregada como simplificación declarada.

## 11m. MOD — Material como dimensión física del inventario (ADR-067)

Implementado en `RedLogistica_Exportacion.alp` y `model_src/`. **Pendiente de compilar y correr en el IDE de AnyLogic** (mismo estado que 11k/11l — sin motor de simulación en el entorno donde se implementó). Origina en el nuevo maestro de datos (`Maestro_Simulacion.xlsx`), que separa `Producto` de `Material` (subdivisión productiva, p. ej. `JCL`/`JCCL`/`PULPA` de `JUGO`) y ya trae `Stock_inicial` separado por material — el usuario confirma que un pedido de un material no puede satisfacerse con stock de otro (no son sustituibles).

- [x] `Capa.material`, `LoteProducto.material`, `Pedido.material`, `ContenedorExportacion.material` — mismo patrón que `cliente`/`calidad` (ADR-047).
- [x] `Inventario`: overloads con material de `stock`, `libre`, `reservado`, `fifo`, `reservar`, `despachar`; `ingresar()` pasa a pedir material siempre. Las firmas sin material se conservan intactas para capacidad, KPIs y las transferencias agregadas (ADR-048, ADR-066), que no dependen de qué material es.
- [x] Camino de reserva/evaluación de un pedido puntual (`reservarParcialPedido`, `alternativaPara`/`toneladasDisponiblesParaAlternativa`, `seleccionarSitioCrossDock`, `ejecutarCrossDockPedido`/`transferirLotesADeposito`, `depositosOrdenadosParaPedido`/`costoEstimadoDesde`, `evaluarAlternativa`, `toneladaDiaEnStock` de ADR-065) filtra por `pedido.material`.
- [x] `obtenerTipoContenedor`/`obtenerCapacidadContenedorTon` resuelven directo por `(producto, material)` — elimina la ambigüedad de la búsqueda inversa por tipo de contenedor cuando dos materiales comparten tipo de contenedor con capacidad distinta (`PULPA` 20 tn vs. `JCL`/`JCCL` 24 tn, ambos `REEFER_40`).
- [x] `buscarLoteComercialAbierto`/`crearLoteEnPlanta` agregan material a la identidad del lote; `Planta.producir()` itera `materialesDe(producto)` en vez de asumir un solo total agregado por día.
- [x] `DatosEntrada.materialesDe()`, `producto(producto, material)`, `produccionDelDia(dia, producto, material)` — derivados de la tabla `Producto`, sin tablas nuevas.
- [x] Contrato: columna opcional `material` en `Producto`, `ProduccionPlan`, `PedidoPlan`, `StockInicial`; `claveLote()` y `DatosEntrada.validar()` la incorporan (un `codigo_lote` no puede tener dos materiales, y un material vacío se rechaza explícitamente cuando `Producto` sí distingue materiales para ese producto — no se resuelve 0 en silencio).
- [x] Verificación exhaustiva de aridad (script que compara cada *call site* contra la definición de cada función tocada) — encontró y corrigió dos llamadores que se habían escapado del primer paso.
- [x] Compatibilidad: con `material = ""` en todo el libro (generador sintético, o un Excel sin la columna), el comportamiento no cambia — es el caso de regresión obligatorio.
- [ ] **Pendiente:** correr el caso de regresión (`material = ""` en todo el libro, `V-COST-14`) y una corrida con materiales reales distintos por producto para confirmar que un pedido de un material no consume stock de otro.
- [ ] Pendiente de compilar en el IDE de AnyLogic — este entorno no tiene el compilador ni el motor de simulación.
- [ ] Fuera de alcance de este Mod: propagar `material` a las tarifas (`TarifaSitio`, `TarifaFleteProducto`, `TarifaRoundTrip`, `TarifaEspera`) — ninguna hoja del maestro nuevo las varía por material; y capacidad física por material (`CapacidadUbicacion`) — no hay dato de capacidad por material en el maestro nuevo.
- [ ] Fuera de alcance, explícitamente diferido por el usuario (punto 4 del análisis del maestro nuevo): `oportunidad_usd_tn_dia`/`penalidad_sobrecarga_usd_tn_dia` (ADR-049) no tienen columna equivalente en el maestro nuevo — el crédito de holding de ADR-065 queda estructuralmente en 0 para la planta mientras se cargue ese maestro, sin que sea un bug de este Mod.

## 11n. MOD — El importador acepta el formato de tarifas del maestro nuevo (ADR-068)

Implementado en `RedLogistica_Exportacion.alp` y `model_src/` (contenido enteramente en `ImportadorExcel.java`). **Pendiente de compilar y correr en el IDE de AnyLogic** (mismo estado que 11k/11l/11m). Origina en que `Maestro_Simulacion.xlsx` renombra/parte las hojas de tarifa y reemplaza vigencia+tarifa por 12 columnas de mes + moneda — sin este Mod, ese libro no carga en absoluto (falta la hoja `TarifaSitio`, falta la hoja `TarifaFleteProducto`).

- [x] `filas()` filtra por `id_escenario` sólo si la columna existe — mismo método sirve para el contrato original (sin esa columna en los maestros) y el nuevo (con ella).
- [x] Buckets mensuales resueltos por posición de columna, no por el texto del encabezado (los 12 nombres traen imprecisiones de ±1 día entre hojas) — se expanden a filas `Tarifa` de siempre (una por mes), sin tocar `DatosEntrada`.
- [x] Conversión de moneda (`Tipo_cambio`, `aUsd()`) aplicada al leer: todo lo que no declare `Moneda = 'USD'` (incluido `'$'`) se convierte antes de llegar a `DatosEntrada`.
- [x] `TarifaFleteCamionproducto` (rename de `TarifaFleteProducto`) y `TarifaRoundTrip` con buckets — detectados por nombre de hoja o por columna presente, sin romper el formato original.
- [x] `TarifaSitio` ausente ⇒ se arma desde `Tarifa_almacenaje` (in/storage/out) + `Gastos_terminal` (costo terminal) + `Despachante` (despachante), acumulados por `(idUbicacion, producto)` vía `productoDeContenedor()` (asume un tipo de contenedor por producto).
- [x] Sitios que el maestro nombra distinto a `Ubicacion.id_ubicacion` (`EXOLGAN`, `TPROSARIO`, `TRPLATA`) se descartan con `advertencias`, no con un error que aborte todo el libro.
- [x] `Consolidado`, `Cross_docking` y `Gastos_THC` deliberadamente **no** se leen — varían por terminal o por naviera, una dimensión que `TarifaSitio` no tiene, y la pregunta hecha al usuario sobre cómo incorporarla no se respondió. `consolidacion_tarifa`/`cross_dock_tarifa`/`thc_usd_contenedor` quedan en 0 con una advertencia explícita, no una tarifa adivinada.
- [ ] **Pendiente:** correr `datos/entrada_ejemplo.xlsx` (formato original) y confirmar resultado idéntico a antes de este Mod — caso de regresión obligatorio, dado que casi todo el cambio es una rama `if/else` sobre el nombre/columna de la hoja.
- [ ] **Pendiente, del lado del dato, no del código:** `GRUPO_PAZ` y `CONTROL_UNION` no tienen ninguna fila en `TarifaFleteCamionproducto` ni en `TarifaRoundTrip` en el libro real — `coberturaTarifas()` va a abortar pidiendo esos datos.
- [ ] **Pendiente, decisión de negocio sin responder:** si `Consolidado`/`Cross_docking` varían genuinamente por terminal (cambiaría `getImporteConsolidacion()`/`getImporteCrossDock()` para depender también del destino) y si `thc_usd_contenedor` pasa a resolver por `pedido.naviera` en vez de por sitio.
- [ ] Pendiente de compilar en el IDE de AnyLogic.

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
