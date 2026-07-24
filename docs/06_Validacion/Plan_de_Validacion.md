# Plan de validación

[← Volver al índice](../README.md)

## 1. Objetivo

Verificar que cada módulo represente correctamente las reglas de negocio y que la migración no rompa el modelo funcional existente.

## 2. Estrategia

Cada fase debe superar cuatro niveles:

1. compilación;
2. prueba unitaria funcional;
3. reconciliación de saldos y costos;
4. prueba integrada end-to-end.

## 3. Principios

- usar casos pequeños y determinísticos antes de escenarios largos;
- controlar semilla aleatoria cuando corresponda;
- validar balances antes de indicadores;
- comparar lógica anterior y nueva durante la transición;
- no aceptar resultados solo porque el modelo termina sin error.

## 4. Casos mínimos

### V-001 Producción sin restricciones

**Datos:** capacidad superior a la producción de cinco días.

**Esperado:**

- producido acumulado correcto;
- stock igual a producido;
- excedente cero;
- saldo físico reconciliado.

### V-002 Producción con capacidad insuficiente

**Esperado:**

- stock limitado a capacidad;
- excedente igual a producción no ingresada;
- lote registra solo toneladas efectivamente almacenadas.

### V-003 Lote comercial acumulativo

**Datos:** lote objetivo 500 tn, producción 100 tn/día.

**Esperado al día 5:**

- producido 500 tn;
- un solo lote comercial;
- cinco registros diarios no deben crear cinco identidades comerciales.

### V-004 Despacho parcial antes del cierre

**Datos:** producido 150 tn, despacho 25 tn.

**Esperado:**

- despachado 25;
- disponible 125;
- lote continúa abierto;
- producción posterior sigue sumando al mismo lote.

### V-005 Múltiples ubicaciones

**Datos:** 75 tn planta, 50 tn depósito, 25 tn contenedor.

**Esperado:** suma física 150 tn y una sola identidad de lote.

### V-006 Transferencia parcial

**Datos:** lote con 100 tn en planta, mover 30 tn.

**Esperado:**

- planta 70;
- depósito 30;
- costo aplicado sobre 30;
- identidad sin cambio.

### V-007 Transferencia fallida

**Datos:** depósito sin capacidad.

**Esperado:**

- no cambia ningún saldo;
- no se registra costo;
- retorno `false`;
- motivo visible.

### V-008 Reserva completa

**Datos:** pedido 80 tn y saldo libre 100 tn.

**Esperado:** reservado 80, libre 20, pedido `RESERVADO`.

### V-009 Reserva insuficiente (reserva incremental, ADR-024)

**Datos:** pedido 120 tn, saldo libre 100 tn, capacidad de contenedor 25 tn.

**Esperado:**

- se reservan 100 tn;
- 4 contenedores quedan ejecutables y el quinto espera producción;
- el pedido queda `PARCIALMENTE_RESERVADO`, no revertido;
- al producirse 20 tn más, la reserva se completa sin intervención manual.

### V-010 Cantidad de contenedores

- 50 tn de jugo a 25 tn/cont → 2.
- 51 tn → 3.
- 0 tn → error de validación del pedido.

### V-011 Último contenedor parcial

**Datos:** 52 tn, capacidad 25.

**Esperado:** 25 + 25 + 2 tn.

### V-012 Consolidación en depósito

**Esperado:** OUT, consolidación, ciclo, terminal, THC y despachante aplicados una vez.

### V-013 Cross docking

**Esperado:**

- operación empieza cuando ambos camiones están presentes;
- primero puede esperar;
- debe ocurrir el mismo día;
- no se aplica IN, storage ni OUT.

### V-014 Mismo portacontenedor

Verificar que el recurso permanezca ocupado desde retiro vacío hasta ingreso cargado.

### V-015 Fecha límite (ADR-027)

Un plan que finaliza después del día límite se ejecuta igual y registra el atraso en días. Verificar que el pedido no quede bloqueado y que el atraso aparezca en el KPI de nivel de servicio.

### V-016 Tarifa faltante

El plan debe quedar no factible. Nunca costo cero silencioso.

### V-017 Reconciliación de costos

```text
Costo pedido = suma de costos reales de sus contenedores y movimientos asociados
```

### V-018 Reconciliación de inventario

```text
Producido = físico + reservado/despachado + excedente/merma según definición
```

### V-019 Imputación de almacenaje por capas (ADR-021, ADR-022)

**Datos:** un lote ingresa 30 tn al día 4 y 50 tn al día 9 al mismo depósito; se retiran 40 tn al día 20; tarifa 0,10 USD/tn/día.

**Esperado:**

- el retiro consume FIFO: 30 tn de la capa del día 4 y 10 tn de la del día 9;
- el storage devengado se calcula por capa, no con una fecha única de ubicación;
- saldo remanente 40 tn en la capa del día 9.

### V-020 Stock derivado (ADR-023)

**Esperado:** en cualquier instante, `stock de la ubicación = suma de las capas de todos los lotes en esa ubicación`, sin excepción y sin necesidad de una rutina de reconciliación.

### V-021 Recursos y esperas (ADR-019)

**Datos:** demanda de 5 viajes en un día con 3 camiones disponibles.

**Esperado:**

- 3 viajes ejecutados, 2 pospuestos al día siguiente;
- 2 registros en `esperas_recursos.csv` con causa `CAMION_PRODUCTO`;
- ningún viaje se pierde ni se duplica.

### V-022 Reproducibilidad

**Esperado:** dos corridas del mismo escenario con la misma semilla producen salidas idénticas byte a byte; con variabilidades en cero, el resultado es independiente de la semilla.

### V-023 Almacenaje sin doble conteo (H-04)

**Esperado:** el almacenaje total del día calculado por depósito coincide con la suma del imputado a cada lote, incluidos los lotes reservados. Hoy divergen porque el cálculo por lote excluye el estado `RESERVADO` y el agregado por depósito no.

### V-024 Independencia del orden de eventos (H-06, H-07)

**Esperado:** ningún evento tiene cadencia fraccionaria y la corrida completa produce los mismos resultados tras reordenar la creación de los eventos en el modelo. Verifica que la secuencia diaria sea la de ADR-034 y no el orden interno del motor.

### V-025 Presupuesto de agentes de PLE (ADR-020)

**Esperado:** el escenario más cargado termina sin alcanzar los 50 000 agentes creados dinámicamente. El total creado se registra en el resumen de cada corrida.

## 4.1 Validación de datos de entrada

Antes de cualquier caso funcional, el escenario debe pasar `validarDatosEntrada()` (ver [Contrato de datos](../09_Definicion/Contrato_de_Datos.md) §7). Una corrida con `errores_entrada.csv` no vacío no se considera evidencia válida.

## 5. Matriz de aceptación por fase

| Fase | Compila | Prueba funcional | Balance | Integración | Aprobación |
|---|---:|---:|---:|---:|---:|
| Lote acumulativo | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente |
| Ubicaciones múltiples | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente |
| Transferencia parcial | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente |
| Reserva nueva | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente |
| Contenedores | Parcial | Parcial | Pendiente | Pendiente | Pendiente |
| Cross dock | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente |
| Costeo | Parcial | Pendiente | Pendiente | Pendiente | Pendiente |

## 6. Evidencia requerida

Para considerar una fase validada, guardar:

- parámetros del escenario;
- captura o log de resultados;
- saldos iniciales y finales;
- costos esperados y obtenidos;
- versión del modelo;
- fecha;
- observaciones.

## 7. Regresión

Después de cada cambio ejecutar al menos:

- producción base;
- transferencia actual;
- creación de pedido;
- reserva;
- generación de plan;
- creación de contenedor;
- corrida corta sin excepciones.
