# Glosario

[← Volver al índice](README.md)

Cada término define el hecho operativo que representa y, cuando corresponde, el costo que dispara y su unidad.

| Término | Definición operativa | Costo asociado | Unidad |
|---|---|---|---|
| **Lote comercial** | Identidad comercial y productiva de un conjunto de producto (producto + cliente + calidad + número). Se produce durante varios días y puede despacharse parcialmente | — | tn |
| **Capa de inventario** | Saldo de un lote en una ubicación con una fecha de ingreso determinada. Unidad atómica del inventario | Base del almacenaje | tn |
| **Guarda** | Movimiento de producto desde planta hacia un depósito de terceros para liberar capacidad de planta | Flete de guarda | USD/viaje o USD/tn |
| **IN** | Ingreso formal de producto a un depósito de terceros, con registro y recepción | Almacenaje IN | USD/tn |
| **Storage** | Permanencia de producto en depósito, devengada por día (o por período mínimo) | Almacenaje diario | USD/tn/día |
| **OUT** | Egreso formal de producto de un depósito | Almacenaje OUT | USD/tn |
| **Consolidación** | Carga de producto dentro de un contenedor, en planta, depósito o terminal | Consolidación | USD/contenedor |
| **Cross docking** | Transferencia directa de producto desde un camión a un contenedor sin ingreso formal a almacenamiento. Requiere camión de producto, portacontenedor con vacío y posición disponible el mismo día | Cross dock | USD/contenedor |
| **Ciclo del contenedor** | Recorrido completo del portacontenedor: retiro del vacío en terminal, viaje al lugar de carga, espera de carga, retorno e ingreso del contenedor cargado | Ciclo contenedor | USD/contenedor |
| **Portacontenedor** | Camión que transporta el contenedor. Queda asignado desde el retiro del vacío hasta el ingreso cargado a terminal | Incluido en ciclo | camión-día |
| **Camión de producto** | Camión que transporta producto a granel o embalado entre planta, depósito y punto de cross docking | Flete de producto | USD/viaje o USD/tn |
| **THC** | *Terminal Handling Charge*: costo local cobrado por la naviera por la manipulación del contenedor en terminal | THC | USD/contenedor |
| **Costo terminal** | Cargo de la terminal portuaria por el ingreso del contenedor cargado | Terminal | USD/contenedor |
| **Despachante** | Honorario del despachante de aduana, dependiente del lugar de consolidación | Despachante | USD/contenedor |
| **Costo histórico** | Costo ya incurrido antes de la decisión que se está evaluando (guarda, IN, storage acumulado). No cambia por la decisión actual | — | USD |
| **Costo incremental** | Costo que se genera a partir de la decisión evaluada (OUT, fletes nuevos, ciclo, consolidación, cross dock, terminal, THC, despachante) | — | USD |
| **Costo end-to-end** | Histórico + incremental | — | USD |
| **Compromiso** | Asociación de un pedido a un lote y a su producción futura esperada. No bloquea stock físico | — | tn |
| **Depósito comprometido** | Dato de entrada (`Pedido.depositoComprometido`, ADR-066), distinto de "Compromiso": indica que el pedido ya cuenta, en la realidad, con stock posicionado en un depósito específico. Hace ganar esa alternativa en `ordenarAlternativas()` mientras sea factible, sin mirar costo | — | — |
| **Reserva** | Bloqueo de toneladas físicas existentes en una capa, a favor de un pedido y contenedor | — | tn |
| **Despacho** | Consumo de una reserva al cargar el contenedor. Descuenta la capa | — | tn |
| **Excedente** | Producción que no pudo almacenarse por falta de capacidad. Indicador de dimensionamiento, no se recupera | Pendiente de definir | tn |
| **Merma** | Pérdida física de producto durante el manejo. No modelada en el alcance actual | — | tn |
| **Estrategia logística** | Combinación de origen del producto y lugar de carga: consolidación en planta, consolidación en depósito, cross dock en depósito, cross dock en terminal | — | — |
| **Plan logístico** | Alternativa evaluada pero no ejecutada, con factibilidad, tiempo y costos estimados | — | — |
| **Día operativo** | Día simulado entero. La sincronización de cross docking exige coincidencia dentro del mismo día operativo | — | día |
| **Camión-día** | Unidad de capacidad de transporte: un camión disponible durante un día | — | camión-día |
| **Réplica** | Corrida del mismo escenario con distinta semilla aleatoria. Los resultados de dimensionamiento se reportan sobre el conjunto de réplicas | — | — |
| **Escenario** | Conjunto completo de parámetros y datos de entrada que define una configuración a comparar | — | — |
| **PLE** | *Personal Learning Edition* de AnyLogic. Edición gratuita de uso no comercial y con límites de modelo | — | — |
