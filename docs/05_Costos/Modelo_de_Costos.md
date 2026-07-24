# Modelo de costos

[← Volver al índice](../README.md)

## 1. Objetivo

Definir un costeo auditable por pedido, lote, contenedor, producto, ubicación y estrategia.

## 2. Vistas de costo

### Histórico

Costos incurridos antes de la decisión actual:

- flete planta-depósito;
- IN;
- almacenamiento acumulado.

### Incremental

Costos necesarios desde el momento de planificar:

- OUT;
- flete de producto a cross dock;
- ciclo del contenedor;
- consolidación;
- cross docking;
- terminal;
- THC;
- despachante.

### End-to-end

```text
Costo end-to-end = costo histórico + costo incremental
```

## 3. Componentes

| Componente | Unidad | Generador | Histórico/Incremental |
|---|---|---|---|
| Flete de guarda | USD/viaje o USD/tn | Planta→depósito | Histórico si ya ocurrió |
| IN | USD/tn | Ingreso formal a depósito | Histórico |
| Storage | USD/tn/día | Permanencia | Histórico acumulativo |
| OUT | USD/tn | Egreso formal | Incremental |
| Flete cross dock | USD/viaje o USD/tn | Producto→cross dock | Incremental |
| Ciclo contenedor | USD/contenedor | Terminal→carga→terminal | Incremental |
| Consolidación | USD/contenedor | Carga formal | Incremental |
| Cross docking | USD/contenedor | Transferencia directa | Incremental |
| Terminal | USD/contenedor | Ingreso cargado | Incremental |
| THC | USD/contenedor | Naviera/terminal | Incremental |
| Despachante | USD/contenedor | Lugar de consolidación | Incremental |

## 4. Aplicabilidad por estrategia

| Costo | Planta | Depósito | Cross dock depósito | Cross dock terminal |
|---|---:|---:|---:|---:|
| Flete guarda previo | Opcional histórico | Sí histórico | Posible histórico | Posible histórico |
| IN | No | Sí | No durante cross dock | No |
| Storage | No | Sí | No | No |
| OUT | No | Sí | No | No |
| Flete producto | No | No si ya está allí | Sí | Sí |
| Ciclo contenedor | Sí | Sí | Sí | Sí |
| Consolidación | Sí | Sí | No o tarifa específica | No o tarifa específica |
| Cross dock | No | No | Sí | Sí |
| Terminal | Sí | Sí | Sí | Sí |
| THC | Sí | Sí | Sí | Sí |
| Despachante | Sí | Sí | Sí | Sí |

## 5. Fórmulas

### Almacenamiento

```text
Costo storage = toneladas almacenadas × días × tarifa USD/tn/día
```

Para retiros parciales debe calcularse sobre las toneladas y permanencia correspondientes, evitando cobrar el saldo ya retirado.

### Cantidad de contenedores

```text
N = ceil(toneladas solicitadas / capacidad del contenedor)
```

### Costo por tonelada

```text
Costo USD/tn = costo total / toneladas efectivamente entregadas
```

No calcular si las toneladas son cero.

## 6. Dimensiones tarifarias

Las tarifas pueden depender de:

- origen;
- destino;
- producto;
- depósito;
- terminal;
- naviera;
- tipo de contenedor;
- lugar de consolidación;
- vigencia;
- unidad tarifaria.

## 7. Tablas maestras recomendadas

- `TarifaFleteProducto`.
- `TarifaCicloContenedor`.
- `TarifaAlmacenamiento`.
- `TarifaServicioCarga`.
- `TarifaTerminal`.
- `TarifaTHC`.
- `TarifaDespachante`.

## 8. Registro de costo objetivo

Cada registro debería incluir:

| Campo | Descripción |
|---|---|
| id | Identificador único |
| tiempo | Momento simulado |
| categoría | Tipo de costo |
| pedido | Pedido asociado |
| lote | Lote asociado |
| contenedor | Contenedor asociado |
| ubicación | Agente generador |
| cantidadBase | tn, contenedores, días o viajes |
| tarifa | Valor unitario |
| importe | Cantidad × tarifa |
| histórico | Indicador |
| descripción | Trazabilidad |

Por restricción PLE puede implementarse inicialmente mediante listas o colecciones en `Main`.

## 9. Reglas de control

- tarifa ausente produce plan no factible o error explícito;
- no usar cero como valor por defecto de una tarifa desconocida;
- el mismo evento no puede registrar dos veces el mismo costo;
- los costos estimados no se suman a los reales;
- costo real se registra cuando ocurre el evento;
- costo histórico se conserva al comparar alternativas, pero no cambia por la decisión actual;
- cada contenedor debe reconciliar sus costos con el pedido.

## 10. KPIs de costo

- USD/pedido;
- USD/contenedor;
- USD/tn;
- USD por producto;
- USD por estrategia;
- costo histórico promedio;
- costo incremental promedio;
- desviación estimado-real;
- storage por depósito;
- costo por espera de recursos, cuando se implemente.
