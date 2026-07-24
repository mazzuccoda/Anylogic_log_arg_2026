# Decisiones de arquitectura

[← Volver al índice](../README.md)

Este archivo registra decisiones que afectan el diseño del modelo. Cada decisión debe conservarse aunque luego sea reemplazada.

## ADR-001 — Contenedor como entidad independiente

**Estado:** aceptada.  
**Contexto:** un pedido puede requerir varios contenedores con tiempos, recursos y costos diferentes.  
**Decisión:** crear un agente `ContenedorExportacion` por unidad física.  
**Consecuencias:** mayor detalle, más agentes y necesidad de reconciliar contenedores con pedido.

## ADR-002 — Mismo portacontenedor durante todo el ciclo

**Estado:** aceptada.  
**Decisión:** el camión que retira el vacío permanece asignado hasta devolver el contenedor cargado.  
**Consecuencia:** la espera de consolidación consume recurso.

## ADR-003 — El pedido parte de un lote específico

**Estado:** aceptada.  
**Decisión:** la planificación no selecciona cualquier lote; primero localiza el lote solicitado y luego sus existencias.  
**Consecuencia:** la reserva debe respetar identidad de lote.

## ADR-004 — Lote comercial acumulativo

**Estado:** aceptada.  
**Contexto:** el lote puede producirse durante varios días.  
**Decisión:** no crear una identidad comercial nueva por cada producción diaria.  
**Consecuencia:** `crearLoteEnPlanta()` debe localizar un lote abierto y acumular.

## ADR-005 — Despacho parcial antes del cierre

**Estado:** aceptada.  
**Decisión:** el estado productivo del lote no bloquea el despacho si existe saldo físico libre.  
**Consecuencia:** separar estado comercial, producción acumulada y disponibilidad.

## ADR-006 — Múltiples ubicaciones por lote

**Estado:** aceptada.  
**Decisión:** un lote puede coexistir en planta, depósitos y contenedores.  
**Consecuencia:** `ubicacionActual` deja de ser una fuente válida de verdad.

## ADR-007 — Solución transitoria compatible con PLE

**Estado:** aceptada como transición.  
**Contexto:** PLE impidió crear `ExistenciaLote`.  
**Decisión:** usar listas paralelas dentro de `LoteProducto`.  
**Riesgo:** desalineación de índices.  
**Salida futura:** clase dedicada de existencia física.

## ADR-008 — Sin movimientos depósito-depósito

**Estado:** aceptada para el alcance actual.  
**Decisión:** permitir planta→depósito, planta/depósito→puerto y movimientos a cross dock.  
**Consecuencia:** simplifica búsqueda de rutas y costos.

## ADR-009 — Compatibilidad de depósito por capacidad

**Estado:** aceptada temporalmente.  
**Decisión:** capacidad mayor que cero implica habilitación para el producto.  
**Consecuencia:** recepción, almacenamiento y operación comparten una regla simplificada.  
**Evolución:** separar habilitaciones si aparecen excepciones.

## ADR-010 — Cross docking sin IN, storage ni OUT

**Estado:** aceptada.  
**Decisión:** la mercadería no ingresa formalmente a almacenamiento.  
**Consecuencia:** solo se aplican fletes, cross dock, ciclo, terminal, THC y despachante.

## ADR-011 — Sincronización de cross docking por día operativo

**Estado:** aceptada.  
**Decisión:** ambos camiones deben coincidir el mismo día, no necesariamente a la misma hora.  
**Consecuencia:** el primero espera y la operación puede requerir reprogramación al cambiar de día.

## ADR-012 — Prioridad por ubicación actual del lote

**Estado:** aceptada para la primera versión.  
**Decisión:** si el lote está almacenado en un depósito habilitado, la primera alternativa es consolidar allí.  
**Consecuencia:** no se busca otro depósito más barato inicialmente.

## ADR-013 — Separación de costos

**Estado:** aceptada.  
**Decisión:** todo plan debe mostrar costo histórico, incremental y end-to-end.  
**Consecuencia:** evita considerar como evitables costos ya incurridos.

## ADR-014 — Tarifa faltante no equivale a cero

**Estado:** aceptada.  
**Decisión:** una tarifa ausente hace el plan no factible o genera error explícito.  
**Consecuencia:** se evitan alternativas artificialmente baratas.

## ADR-015 — Migración sin eliminación inmediata

**Estado:** aceptada.  
**Decisión:** `Envio`, campos de ubicación única y funciones antiguas permanecen hasta que el reemplazo complete regresión.  
**Consecuencia:** coexistencia temporal de estructuras y riesgo de doble conteo.

## ADR-016 — Reserva atómica inicial

**Estado:** propuesta recomendada, pendiente de validación final.  
**Decisión:** si no se cubre el pedido completo, revertir la reserva.  
**Motivo:** reduce inconsistencias durante la primera migración.  
**Evolución:** permitir reserva parcial controlada.

## ADR-017 — Criterio inicial del planificador

**Estado:** aceptada como primera regla.  
**Decisión:** seleccionar menor costo incremental entre planes factibles que cumplen la fecha límite.  
**Evolución:** incluir riesgo, congestión, servicio y robustez.

## Plantilla para nuevas decisiones

```markdown
## ADR-XXX — Título

**Estado:** propuesta | aceptada | reemplazada | descartada  
**Fecha:** YYYY-MM-DD  
**Contexto:** problema que origina la decisión.  
**Decisión:** solución adoptada.  
**Alternativas:** opciones evaluadas.  
**Consecuencias:** efectos positivos, negativos y riesgos.  
**Reemplaza:** ADR anterior, si aplica.
```
