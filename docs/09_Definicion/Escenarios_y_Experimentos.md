# Escenarios y experimentos

[← Volver al índice](../README.md)

**Estado:** propuesta para revisión.

## 1. Por qué se define antes de programar

En un modelo de dimensionamiento, el valor no está en la corrida sino en la **comparación entre escenarios**. La lista de escenarios determina qué variables deben ser parámetros de entrada y qué puede quedar fijo. Definirla al final obliga a reabrir el modelo para parametrizar.

Regla derivada: **toda variable que aparezca en la columna "qué varía" de esta tabla debe ser parámetro de escenario, nunca un valor en código.**

## 2. Caso base

| Parámetro | Valor inicial (sintético) |
|---|---|
| Duración | 183 días |
| Producción | Media diaria por producto, variabilidad ±20% triangular |
| Capacidad de planta | Suficiente para ~7 días de producción |
| Depósitos | 2, con capacidad por producto |
| Camiones de producto | Cantidad "razonable" a determinar por corrida piloto |
| Portacontenedores | Ídem |
| Cross docking | Deshabilitado |
| Réplicas | 30 |

El caso base no pretende ser realista todavía: es la referencia contra la que se miden los deltas. Se recalibra cuando llegue el Excel.

## 3. Escenarios

| ID | Nombre | Qué varía | Pregunta que responde |
|---|---|---|---|
| E-00 | Caso base | — | Referencia |
| E-01 | Barrido de flota | `camiones_producto`, `camiones_portacontenedor` (±50% en 5 pasos) | P2, P6: flota mínima sin degradar servicio |
| E-02 | Barrido de capacidad de depósito | `capacidad_tn` por depósito (0,5× a 2×) | P1, P8: capacidad mínima sin excedente |
| E-03 | Cross docking on/off | `habilita_cross_dock` | P5: ahorro real del cross docking |
| E-04 | Campaña alta | Producción +30% | P7: robustez |
| E-05 | Campaña baja | Producción −30% | P7: costo fijo sobredimensionado |
| E-06 | Sin capacidad de planta | Capacidad de planta a la mitad | Sensibilidad al cuello de botella inicial |
| E-07 | Demanda concentrada | Mismos pedidos totales en la mitad de días | Pico de recursos |
| E-08 | Política de prioridad | `politica_prioridad` en sus 3 valores | Impacto de la regla de asignación |
| E-09 | Determinístico | Todas las variabilidades en 0 | Verificación y comparación con el caso estocástico |
| E-10 | Tarifas de almacenaje altas | `storage_usd_tn_dia` ×2 | Cuándo conviene mover producto vs guardarlo |

Prioridad de implementación: E-00, E-09, E-01, E-02, E-03. El resto son barridos del mismo mecanismo.

## 4. Diseño de experimento

- **Réplicas:** 30 por escenario, semilla `semilla_base + replica`.
- **Estadísticos reportados:** media, desvío, mínimo, máximo y P95 de cada KPI.
- **Comparación:** cada escenario se reporta como delta absoluto y porcentual contra E-00, con solapamiento de intervalos indicado (si los intervalos se solapan, la diferencia no se declara).
- **Barridos:** experimento de variación de parámetros; con paso diario y 183 días el costo computacional es bajo, así que conviene barrer grillas completas antes de refinar.
- **Trazabilidad:** cada corrida escribe `id_escenario`, `replica`, `semilla` y `version_modelo` en toda salida. Un resultado sin esas cuatro columnas no es evidencia.

## 5. Cómo se implementa el barrido en PLE

PLE incluye Parameter Variation y Monte Carlo, así que el barrido con réplicas es viable sin licencia paga (ADR-020). Lo que PLE no ofrece es Custom Experiment, es decir, escribir código de experimento que recorra una tabla de escenarios. La forma de trabajar es la siguiente (ADR-032):

- El experimento tiene **dos dimensiones y sólo dos**: `idEscenario` y `replica`. No importa cuántos parámetros cambie un escenario.
- Al arrancar, `Main` lee la fila de la tabla `Escenario` que corresponde a `idEscenario` y carga de ahí todos sus valores.
- La semilla se fija dentro del modelo como `semillaBase + replica`, no en la configuración aleatoria del experimento, para que la réplica sea reproducible de forma independiente.
- Agregar un escenario es agregar una fila y ampliar el rango de `idEscenario`. Nunca es tocar el experimento.

Costo computacional estimado: 11 escenarios × 30 réplicas = 330 corridas de 183 días con paso diario. Con `Ejecutar en modo virtual` y animación desactivada esto es minutos, no horas.

Límite a vigilar: PLE admite 50 000 agentes creados dinámicamente **por corrida**. Con lotes, contenedores y pedidos de una campaña completa el margen es amplio, pero conviene registrar el conteo en el resumen del escenario para detectar el día en que deje de serlo.

Queda una restricción no técnica: la licencia PLE cubre aprendizaje personal e instrucción, no uso comercial. Es una decisión del responsable del proyecto y no afecta al diseño.

## 6. Presentación de resultados

Para cada pregunta P1..P8, una tabla y un gráfico:

- P1 y P8: ocupación de depósito y excedente vs capacidad (curva de saturación).
- P2 y P6: nivel de servicio y utilización vs tamaño de flota (curva de rendimientos decrecientes, para leer el punto de quiebre).
- P3 y P4: costo total y USD/tn con desglose apilado por categoría.
- P5: costo total con y sin cross docking, con desglose del delta por categoría.
- P7: costo y servicio en campaña alta/baja.

La salida final del proyecto es un informe con esas curvas, no el modelo en sí.
