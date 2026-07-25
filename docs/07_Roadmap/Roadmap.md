# Roadmap y estado de avance

[← Volver al índice](../README.md)

## 1. Criterios

Los porcentajes son estimaciones técnicas y deben actualizarse con evidencia de prueba.

**Este archivo es la única fuente de estado de avance del proyecto.** Ningún otro documento debe replicar tablas de avance.

El orden de ejecución vigente está en §16, derivado de la [Definición del proyecto](../09_Definicion/Definicion_del_Proyecto.md). Las fases 0 a 14 se conservan como inventario de trabajo, no como secuencia.

## 2. Resumen

| Fase | Estado | Avance |
|---|---|---:|
| 0. Respaldo y documentación inicial | Modelo versionado; falta `versionModelo` en salidas | 90% |
| 1. Normalización del dominio | En curso | 75% |
| 2. Lote comercial acumulativo | Diseño validado | 15% |
| 3. Existencias físicas múltiples | Capas de inventario implementadas y verificadas en PLE | 90% |
| 4. Transferencias parciales | Planta → depósito parcial, verificada en PLE; falta depósito → depósito | 80% |
| 5. Reserva profesional | Reservas trazables por capa y pedido; falta compromiso y reserva parcial | 60% |
| 6. Contenedores individuales | Parcial | 25% |
| 7. Consolidación directa | Pendiente | 10% |
| 8. Cross docking | Diseño validado | 10% |
| 9. Terminal | Parcial | 20% |
| 10. Registro de costos | Parcial; tarifas ya vienen de tablas | 35% |
| 11. Planificador | Parcial | 25% |
| 12. Reemplazo de Envio | Pendiente | 0% |
| 13. KPIs y experimentos | Pendiente | 15% |
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
- [ ] Agregar el parámetro `versionModelo` y volcarlo en todas las salidas.

La fase había sido declarada completada al 100% sin que el modelo estuviera versionado. Ya lo está; queda pendiente sólo la trazabilidad de versión en las salidas.

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
- [x] Crear prueba técnica de un contenedor.
- [ ] Crear N por pedido.
- [ ] Distribuir toneladas.
- [ ] Manejar último parcial.
- [ ] Asociar lote y reserva.
- [ ] Implementar estados.
- [ ] Validar V-010 y V-011.

## 10. Fase 7 — Consolidación

- [ ] Recursos en planta.
- [ ] Recursos en depósito.
- [ ] Tiempos y colas.
- [ ] Descuento de reserva.
- [ ] Costos reales.
- [ ] Cambios de estado del contenedor.

## 11. Fase 8 — Cross docking

- [x] Validar concepto de sincronización.
- [x] Validar regla del mismo día.
- [x] Validar exclusión de IN/storage/OUT.
- [ ] Crear recursos.
- [ ] Crear colas independientes.
- [ ] Sincronizar camiones.
- [ ] Gestionar reprogramación.
- [ ] Validar V-013.

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
