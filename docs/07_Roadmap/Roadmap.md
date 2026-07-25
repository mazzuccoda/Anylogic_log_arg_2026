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
| 6. Contenedores individuales | Contenedores reales por pedido, verificados en PLE; falta el ciclo del vacío | 70% |
| 7. Consolidación directa | Posiciones finitas por día y estrategia del escenario, verificadas en PLE; falta consolidación en planta | 80% |
| 8. Cross docking | Operación con cupo diario por sitio, tarifa propia y sin almacenaje, verificada en PLE; falta cross dock parcial y en terminal | 70% |
| 9. Terminal | Parcial | 20% |
| 10. Registro de costos | Parcial; tarifas ya vienen de tablas | 35% |
| 11. Planificador | Parcial | 25% |
| 12. Reemplazo de Envio | Pendiente | 0% |
| 13. KPIs y experimentos | Barrido de 12 escenarios × 30 réplicas verificado en PLE, con las dos flotas consumiéndose de verdad (ADR-044) | 85% |
| 14. Optimización avanzada | Futuro | 0% |

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

- [x] Validar que el lote no es producción diaria.
- [x] Validar producción durante varios días.
- [x] Validar despacho antes del cierre.
- [ ] Agregar cliente.
- [ ] Agregar calidad.
- [ ] Agregar toneladas objetivo.
- [ ] Agregar estado comercial.
- [ ] Implementar lote abierto.
- [ ] Modificar `crearLoteEnPlanta()`.
- [ ] Validar casos V-003 y V-004.

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

- [x] Recursos en depósito: posiciones de consolidación con capacidad diaria (`posiciones_consolidacion × contenedores_por_posicion_dia`), leídas de la tabla `Ubicacion`.
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

- [x] Escenario como fila: `GeneradorSintetico.escenario(id, semilla)` y la hoja `Escenario` del Excel gobiernan las 12 corridas; el experimento no conoce ningún escenario en particular.
- [x] Experimento `Escenarios` (Parameter Variation freeform) con dos dimensiones: `idEscenario` y `replica`.
- [x] Réplicas reproducibles: `semilla = semillaBase + replica`, calculada dentro del modelo.
- [x] KPIs de cierre de corrida como funciones de `Main`.
- [x] `resultados/kpis_por_corrida.csv` con `version_modelo`, `id_escenario`, `replica` y `semilla`.
- [x] Media, desvío, mínimo, máximo y P95 por escenario, con delta absoluto y porcentual contra E-00.
- [x] E-09 determinístico verificado: desvío exactamente 0 en 30 réplicas.
- [x] Barrido de flota útil (ADR-044): la flota de producto se consume en camión-día y cada contenedor toma un portacontenedor del pool. E-01 (1/1) baja el nivel de servicio a 0,908 y sube el atraso a 3,17 días; E-02 (6/8) no mejora a E-00, que es la respuesta a P2 y P6.
- [ ] Cargar y descargar no le consumen jornada al camión: la planta no tiene velocidades de carga en las tablas.
- [ ] Almacenaje en planta: como la planta no tiene tarifa, el excedente que espera camión no cuesta nada y por eso achicar la flota baja el costo total.
- [ ] Solapamiento de intervalos en la comparación contra E-00.
- [ ] Escenario de capacidad de planta y de política de prioridad (la política es hoy única).

## 12. Fase 9 — Terminal

- [ ] Entrega de vacíos.
- [ ] Cola de ingreso cargado.
- [ ] Costo terminal.
- [ ] THC.
- [ ] Cierre de ciclo.
- [ ] Liberación del camión.

## 13. Fase 10 — Costos

- [x] Separar histórico, incremental y end-to-end.
- [x] Definir categorías.
- [ ] Crear tablas tarifarias.
- [ ] Crear registro auditable.
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
