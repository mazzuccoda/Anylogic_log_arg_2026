# `flow/` — Cómo piensa el modelo (guía didáctica)

Este repo ya tiene documentación técnica muy completa en [`docs/`](../docs/README.md)
(arquitectura, agentes, funciones, costos, ADRs). Esa es la **fuente de verdad**.
Esta carpeta es un **complemento didáctico**: explica, con menos jerga y con
ejemplos numéricos paso a paso, las dos decisiones que más preguntas generan:

| Documento | Pregunta que responde | Docs técnicos relacionados |
|---|---|---|
| [`01-logica-almacenamiento.md`](./01-logica-almacenamiento.md) | ¿Cómo decide el modelo qué producto sacar de la planta, a qué depósito mandarlo, y cuánto cuesta tenerlo guardado? | [`Flujos_Operativos.md`](../docs/04_Flujos/Flujos_Operativos.md) §2-3, [`Funciones.md`](../docs/03_Logica/Funciones.md) §2-5, [`Modelo_de_Costos.md`](../docs/05_Costos/Modelo_de_Costos.md) |
| [`02-logica-entrega-pedidos.md`](./02-logica-entrega-pedidos.md) | De todos los circuitos posibles para cubrir un pedido, ¿cuál elige el modelo, y cómo entran el costo y la capacidad operativa (posiciones, cross dock, flota de camiones) en esa elección? | [`Flujos_Operativos.md`](../docs/04_Flujos/Flujos_Operativos.md) §4-9, [`Funciones.md`](../docs/03_Logica/Funciones.md) §4 (Evaluador), ADR-054/055/056/059/060/061 |

Si algo de acá contradice el código o `docs/`, **gana el código** — esa es la
regla del propio proyecto (ver [`docs/README.md`](../docs/README.md), "Jerarquía
de documentos"). Este material simplifica para explicar, no reemplaza la
especificación.

## Mapa mental rápido

```
                    ┌───────────────────────────┐
                    │  PLANTA                    │  produce jugo/cáscara/aceite
                    │  capacidad = umbral, no tope│  cada día (ADR-048)
                    └──────────────┬─────────────┘
                                   │ revisarTransferenciasPlanta()
                                   ▼                                doc 01
                    ┌───────────────────────────┐
                    │  DEPÓSITOS DE TERCEROS     │  capacidad dura, cuesta
                    │  seleccionarDeposito()     │  storage USD/tn/día
                    └──────────────┬─────────────┘
                                   │
                    ┌──────────────┴─────────────┐
                    │   PEDIDO (demanda de export)│                doc 02
                    │   asignarConEvaluador() /   │
                    │   asignarConPoliticaFija()  │
                    └──────────────┬─────────────┘
                                   ▼
                    ┌───────────────────────────┐
                    │  CONTENEDOR → TERMINAL      │  consolidación, cross dock,
                    │                             │  THC, despachante
                    └───────────────────────────┘
```

La planta y los depósitos son el **lado de la oferta** (dónde vive el
producto y qué cuesta tenerlo ahí — doc 01). Los pedidos son el **lado de la
demanda**: cada uno dispara una comparación de circuitos completos —origen +
transporte + consolidación— que compite por costo y por la capacidad
operativa real de la red (doc 02). Las dos lógicas comparten el mismo
inventario por capas (`Main.inventario`, ver `docs/02_Modelo/Modelo_de_Datos.md`),
así que una tonelada reservada para un pedido deja de estar "libre" para la
decisión de almacenamiento, y viceversa.
