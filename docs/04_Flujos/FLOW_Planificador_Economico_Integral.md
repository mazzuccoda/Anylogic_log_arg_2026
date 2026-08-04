# Flow detallado — Situación actual y cambios propuestos

## Leyenda

- **AZUL**: lógica actual existente.
- **ROJO**: problema detectado.
- **VERDE**: cambio propuesto.
- **NARANJA**: decisión económica nueva.
- **GRIS**: ejecución física ya existente.

---

## 1. Flow general: lógica actual

```mermaid
flowchart TD

    A[Inicio del día] --> B[Abrir flota y capacidades]
    B --> C[Registrar pedidos conocidos]
    C --> D[Actualizar ventanas marítimas]
    D --> E[Calcular demanda proyectada por producto]

    E --> F[toneladasASacarDePlanta producto]

    F --> F1[Componente por desborde]
    F --> F2[Componente por servicio]
    F --> F3[Componente preventivo]

    F1 --> G1[Stock proyectado - capacidad planta]
    F2 --> G2[Demanda proyectada - stock libre en depósitos]
    F3 --> G3[Si ocupación alcanza alerta, bajar al objetivo]

    G1 --> H[Tomar máximo de los tres componentes]
    G2 --> H
    G3 --> H

    H --> I{¿Toneladas a transferir > 0?}

    I -- No --> J[No transferir desde planta]
    I -- Sí --> K[Seleccionar depósito]

    K --> K1[Comparar depósitos]
    K1 --> K2[Flete planta-depósito]
    K1 --> K3[IN]
    K1 --> K4[Almacenamiento estimado]
    K2 --> L[Elegir depósito más barato]
    K3 --> L
    K4 --> L

    L --> M[Transferir producto desde planta]
    M --> N[Registrar flete e IN]
    N --> O[Producto queda en depósito]

    J --> P[Evaluar pedidos]
    O --> P

    P --> Q[Generar alternativas de circuito]
    Q --> R[Validar stock]
    R --> S[Validar capacidad finita]
    S --> T[Calcular costo de alternativas]
    T --> U[Elegir menor costo factible]
    U --> V[Crear asignaciones parciales]
    V --> W[Reservar capacidad futura]
    W --> X[Crear contenedores]
    X --> Y[Crear envíos]

    Y --> Z{Circuito}

    Z -- Planta / depósito --> ZA[Tomar portacontenedor]
    ZA --> ZB[Viajar vacío al origen]
    ZB --> ZC[Cargar contenedor]
    ZC --> ZD[Viajar a puerto]
    ZD --> ZE[Descargar]
    ZE --> ZF[Retornar]
    ZF --> ZG[Liberar portacontenedor]

    Z -- Terminal --> ZH[Cargar producto a granel]
    ZH --> ZI[Viajar a terminal]
    ZI --> ZJ[Descargar producto]
    ZJ --> ZK[Consolidar en terminal]

    ZG --> AA[Entregar / exportar]
    ZK --> AA
```

---

## 2. Problema actual de inventario

```mermaid
flowchart TD

    A[Pedido conocido] --> B[demandaProyectada]
    B --> C[Saldo pendiente de todos los pedidos conocidos]

    C --> D{¿Consolidación en planta?}

    D -- Sí --> E[Componente por servicio = 0]
    D -- No --> F[Componente por servicio = demanda proyectada menos stock libre en depósitos]

    F --> G[Se interpreta que la demanda debe estar en depósitos]

    G --> H[Se ordena transferir desde planta]

    H --> I[Después se selecciona el depósito más barato]

    I --> J[Stock sale de Famaillá]
    J --> K[Se paga flete + IN + almacenamiento]
    K --> L[El depósito puede quedar con stock libre sin pedido]

    style G fill:#f8d7da,stroke:#b02a37
    style H fill:#f8d7da,stroke:#b02a37
    style L fill:#f8d7da,stroke:#b02a37
```

### Problema central

La lógica actual compara:

```text
¿A qué depósito conviene enviar?
```

pero no compara antes:

```text
¿Conviene mover o conviene mantener en planta?
```

---

## 3. Flow de decisión económica actual

```mermaid
flowchart TD

    A[Se determina que el producto debe salir] --> B[Buscar depósitos elegibles]
    B --> C[Calcular flete planta-depósito]
    C --> D[Calcular IN]
    D --> E[Calcular almacenamiento estimado]
    E --> F[Elegir depósito con menor costo]

    F --> G[Transferir]

    style A fill:#f8d7da,stroke:#b02a37
    style G fill:#f8d7da,stroke:#b02a37
```

### Limitación

La alternativa:

```text
MANTENER EN PLANTA
```

no participa en la comparación.

---

## 4. Flow objetivo con planificador económico integral

```mermaid
flowchart TD

    A[Pedido conocido] --> B[Generar alternativas de ubicación y circuito]

    B --> C1[Mantener en planta]
    B --> C2[Transferir a depósito]
    B --> C3[Consolidar en planta]
    B --> C4[Consolidar en depósito]
    B --> C5[Cross docking]
    B --> C6[Consolidar en terminal]

    C1 --> D[Validar factibilidad física]
    C2 --> D
    C3 --> D
    C4 --> D
    C5 --> D
    C6 --> D

    D --> D1[Stock disponible]
    D --> D2[Capacidad de almacenamiento]
    D --> D3[Capacidad de consolidación]
    D --> D4[Capacidad cross dock]
    D --> D5[Capacidad transporte]
    D --> D6[Ventana retiro]
    D --> D7[Cut-off físico]
    D --> D8[Capacidad futura reservable]

    D1 --> E[Estimar día de consumo]
    D2 --> E
    D3 --> E
    D4 --> E
    D5 --> E
    D6 --> E
    D7 --> E
    D8 --> E

    E --> F[Calcular costo incremental futuro]

    F --> F1[Flete futuro]
    F --> F2[IN futuro]
    F --> F3[Almacenamiento futuro]
    F --> F4[OUT futuro]
    F --> F5[Round trip]
    F --> F6[Consolidación / cross dock]
    F --> F7[Terminal / THC / despachante]
    F --> F8[Frío propio futuro]

    F1 --> G[Agregar penalizaciones]
    F2 --> G
    F3 --> G
    F4 --> G
    F5 --> G
    F6 --> G
    F7 --> G
    F8 --> G

    G --> G1[Riesgo de perder cut-off]
    G --> G2[Sobrecarga planta]
    G --> G3[Saturación operativa]

    G1 --> H[Calcular costo objetivo]
    G2 --> H
    G3 --> H

    H --> I[Ordenar alternativas factibles]
    I --> J[Elegir menor costo objetivo]
    J --> K{¿La alternativa exige mover stock?}

    K -- No --> L[Mantener producto en planta]
    K -- Sí --> M[Crear movimiento planificado]

    M --> N[Vincular a pedido + asignación]
    N --> O[Reservar capacidad]
    O --> P[Ejecutar transferencia en fecha necesaria]

    L --> Q[Esperar hasta fecha de ejecución]
    P --> Q

    Q --> R[Ejecutar circuito logístico]
```

---

## 5. Cambio clave: transferencia desde planta

### Situación actual

```mermaid
flowchart LR

    A[Demanda proyectada] --> B[Componente por servicio]
    B --> C[Transferencia genérica]
    C --> D[Depósito]

    style C fill:#f8d7da,stroke:#b02a37
```

### Nueva situación

```mermaid
flowchart LR

    A[Asignación confirmada] --> B{¿Necesita stock almacenado en depósito?}

    B -- No --> C[Mantener en planta]
    B -- Sí --> D[Calcular costo de preposicionar]

    D --> E{¿Es menor costo y mejora servicio?}

    E -- No --> C
    E -- Sí --> F[Crear movimiento planificado]

    F --> G[Transferir solo toneladas necesarias]
```

---

## 6. Nueva lógica para desborde

```mermaid
flowchart TD

    A[Stock actual + forecast] --> B{¿Supera capacidad nominal?}

    B -- No --> C[No transferencia obligatoria]
    B -- Sí --> D[Calcular exceso real]

    D --> E[Transferir solo exceso necesario]

    E --> F{¿Supera 105%?}

    F -- Sí --> G[Transferencia crítica obligatoria]
    F -- No --> H[Transferencia preventiva mínima]

    style G fill:#fff3cd,stroke:#b8860b
```

### Cambio

El umbral del 85% deja de significar:

```text
vaciar hasta el objetivo
```

y pasa a significar:

```text
activar evaluación anticipada
```

---

## 7. Nueva comparación de costos

```mermaid
flowchart TD

    A[Alternativa] --> B[Costos futuros]
    B --> B1[Flete]
    B --> B2[IN]
    B --> B3[Almacenamiento futuro]
    B --> B4[OUT]
    B --> B5[Consolidación]
    B --> B6[Cross dock]
    B --> B7[Round trip]
    B --> B8[Terminal]
    B --> B9[Frío propio]

    B1 --> C[Penalidades]
    B2 --> C
    B3 --> C
    B4 --> C
    B5 --> C
    B6 --> C
    B7 --> C
    B8 --> C
    B9 --> C

    C --> C1[Riesgo de cut-off]
    C --> C2[Sobrecarga]
    C --> C3[Saturación]

    C1 --> D[Costo objetivo]
    C2 --> D
    C3 --> D

    D --> E{¿Alternativa factible y de menor costo?}

    E -- Sí --> F[Seleccionar]
    E -- No --> G[Descartar]
```

---

## 8. Diferencia entre costo esperado y costo real

```mermaid
flowchart LR

    A[Planificador] --> B[Costo esperado]
    B --> C[Decisión]

    C --> D[Ejecución real]
    D --> E[RegistroCostos]

    E --> F[Costo real]

    F --> G[Comparar esperado vs real]
```

### Regla

Los costos esperados:

```text
sirven para decidir
```

Los costos reales:

```text
se registran cuando la operación ocurre
```

Nunca registrar el costo esperado como caja.

---

## 9. Cambios a marcar en el código

| Área | Situación actual | Cambio |
|---|---|---|
| `toneladasASacarDePlanta()` | Combina desborde, servicio y prevención | Dividir en transferencia obligatoria y movimientos planificados |
| `componentePorServicio` | Demanda proyectada menos stock en depósitos | Eliminar como transferencia genérica |
| `demandaProyectada()` | Suma saldos pendientes | Mantener solo para forecast y riesgo |
| `seleccionarDeposito()` | Elige depósito luego de decidir mover | Integrar “mantener en planta” en el comparador |
| `AlternativaCircuito` | No modela almacenamiento futuro explícito | Agregar costos futuros y penalidades |
| `PlanLogistico` | Mezcla almacenamiento diario con histórico | Separar histórico vs futuro |
| `revisarTransferenciasPlanta()` | Transfiere antes de que el plan económico lo justifique | Ejecutar solo movimientos obligatorios o planificados |
| Capacidad finita | Ya restringe posiciones | Mantener antes del costo |
| Costos reales | Se devengan en ejecución | Mantener sin cambios |
| Flowcharts | Ejecutan circuitos físicos | Mantener sin rediseño inicial |

---

## 10. Flow final resumido

```mermaid
flowchart TD

    A[Pedido conocido] --> B[Generar alternativas]
    B --> C[Validar capacidad y ventana]
    C --> D[Estimar fecha de consumo]
    D --> E[Calcular costo futuro]
    E --> F[Agregar riesgo y sobrecarga]
    F --> G[Elegir menor costo factible]

    G --> H{¿Mover producto ahora?}

    H -- No --> I[Mantener en planta]
    H -- Sí --> J[Crear movimiento vinculado]

    I --> K[Reservar capacidad]
    J --> K

    K --> L[Ejecutar en fecha planificada]
    L --> M[Registrar costo real]
    M --> N[Medir servicio y costo]
```

---

# Criterio rector

```text
No mover por regla.
Mover solo porque:

1. es obligatorio por capacidad;
2. mejora el servicio;
3. reduce el costo incremental esperado;
4. forma parte de un plan confirmado.
```
