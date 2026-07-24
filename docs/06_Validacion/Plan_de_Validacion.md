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

### V-009 Reserva insuficiente

**Datos:** pedido 120 tn y saldo libre 100 tn.

**Esperado inicial recomendado:** reserva atómica revertida y pedido pendiente.

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

### V-015 Fecha límite

Un plan que finaliza después del día límite debe quedar no factible o penalizado según la política vigente.

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

## 5. Matriz de aceptación por fase

| Fase | Compila | Prueba funcional | Balance | Integración | Aprobación |
|---|---:|---:|---:|---:|---:|
| Lote acumulativo | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente |
| Ubicaciones múltiples | Parcial | Parcial | Pendiente | Pendiente | Pendiente |
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
