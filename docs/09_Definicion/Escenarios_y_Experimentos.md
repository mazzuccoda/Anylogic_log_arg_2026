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

## 5. Restricción de licencia

Los barridos con réplicas son exactamente lo que la edición PLE limita, y PLE es de uso no comercial. Antes de E-01 hay que resolver la licencia (ver ADR-020). Mitigación transitoria: reducir réplicas y correr los barridos en varias sesiones, aceptando pérdida de precisión estadística.

## 6. Presentación de resultados

Para cada pregunta P1..P8, una tabla y un gráfico:

- P1 y P8: ocupación de depósito y excedente vs capacidad (curva de saturación).
- P2 y P6: nivel de servicio y utilización vs tamaño de flota (curva de rendimientos decrecientes, para leer el punto de quiebre).
- P3 y P4: costo total y USD/tn con desglose apilado por categoría.
- P5: costo total con y sin cross docking, con desglose del delta por categoría.
- P7: costo y servicio en campaña alta/baja.

La salida final del proyecto es un informe con esas curvas, no el modelo en sí.
