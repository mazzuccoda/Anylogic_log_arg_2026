# Roadmap y estado de avance

[← Volver al índice](../README.md)

## 1. Criterios

Los porcentajes son estimaciones técnicas y deben actualizarse con evidencia de prueba.

**Este archivo es la única fuente de estado de avance del proyecto.** Ningún otro documento debe replicar tablas de avance.

El orden de ejecución vigente está en §16, derivado de la [Definición del proyecto](../09_Definicion/Definicion_del_Proyecto.md). Las fases 0 a 14 se conservan como inventario de trabajo, no como secuencia.

## 2. Resumen

| Fase | Estado | Avance |
|---|---|---:|
| 0. Respaldo y documentación inicial | Reabierta: falta versionar el modelo | 60% |
| 1. Normalización del dominio | En curso | 65% |
| 2. Lote comercial acumulativo | Diseño validado | 15% |
| 3. Existencias físicas múltiples | En transición | 30% |
| 4. Transferencias parciales | Pendiente | 5% |
| 5. Reserva profesional | Pendiente | 10% |
| 6. Contenedores individuales | Parcial | 25% |
| 7. Consolidación directa | Pendiente | 10% |
| 8. Cross docking | Diseño validado | 10% |
| 9. Terminal | Parcial | 20% |
| 10. Registro de costos | Parcial | 20% |
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
- [ ] **Versionar el `.alp` en el repositorio.**
- [ ] **Versionar el código exportado por agente en `model_src/`.**
- [ ] **Crear `CHANGELOG.md` y parámetro `versionModelo` visible en las salidas.**

La fase estaba declarada como completada al 100%, pero el modelo no está versionado: hoy el activo crítico vive sólo en una máquina. Queda reabierta.

## 4. Fase 1 — Dominio

- [x] Definir productos y contenedores.
- [x] Definir estrategias.
- [x] Crear `ContenedorExportacion`.
- [x] Crear `PlanLogistico`.
- [x] Ampliar `Pedido`.
- [ ] Auditar todas las Option Lists reales.
- [ ] Auditar variables reales de agentes.
- [ ] Eliminar nombres duplicados o ambiguos.

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

- [x] Crear listas de ubicaciones.
- [x] Crear búsqueda de ubicación.
- [x] Agregar saldo por ubicación.
- [x] Consultar saldo y saldo libre.
- [x] Retirar saldo libre.
- [ ] Reservar por ubicación.
- [ ] Liberar reserva.
- [ ] Despachar reserva.
- [ ] Validar integridad de listas.
- [ ] Reconciliar con stock de agentes.

## 7. Fase 4 — Transferencia parcial

- [ ] Diseñar contrato transaccional.
- [ ] Crear `transferirToneladasLote()`.
- [ ] Aplicar retiro parcial en planta.
- [ ] Aplicar recepción parcial en depósito.
- [ ] Registrar flete e IN.
- [ ] Revertir ante fallo.
- [ ] Reemplazar uso de `transferirLoteCompleto()`.
- [ ] Validar V-006 y V-007.

## 8. Fase 5 — Reserva

- [ ] Comenzar por lote solicitado.
- [ ] Localizar saldos físicos.
- [ ] Definir orden de consumo.
- [ ] Reserva atómica inicial.
- [ ] Asociar reservas al pedido.
- [ ] Liberar reservas canceladas.
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
| 1 | Cerrar definición y ADR nuevos; resolver licencia (ADR-020) | — |
| 2 | Datos maestros y tarifas como tablas + generador sintético (ADR-029) | parte de 1 y 10 |
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
