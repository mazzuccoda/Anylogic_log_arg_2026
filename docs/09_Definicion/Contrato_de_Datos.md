# Contrato de datos de entrada

[← Volver al índice](../README.md)

**Estado:** propuesta para revisión.

## 1. Objetivo

Definir las tablas de entrada del modelo antes de implementar, para que la fase sintética y la fase Excel usen **exactamente el mismo esquema**. El modelo nunca lee valores hardcodeados: lee estas tablas.

## 2. Modo de carga

| Fase | Origen | Mecanismo |
|---|---|---|
| 1 | Generador sintético | Función `generarDatosSinteticos()` que puebla las mismas tablas internas |
| 2 | Excel | Archivo `datos/entrada_<escenario>.xlsx`, una hoja por tabla, importado a la base de datos interna de AnyLogic |

Regla: si la fase 2 requiere cambiar alguna función de lógica de negocio, el contrato estaba mal definido.

## 3. Convenciones

- Una hoja de Excel por tabla, con el nombre exacto de la tabla.
- Primera fila = encabezados, con los nombres de columna exactos de este documento.
- Sin celdas combinadas, sin totales, sin filas en blanco intermedias.
- Decimal con punto. Fechas `YYYY-MM-DD`. Texto sin espacios al inicio o final.
- Enumerados en MAYÚSCULAS, exactamente como las Option Lists (`JUGO`, `CASCARA`, `ACEITE`, `REEFER_40`, ...).
- Unidades en el nombre de la columna cuando pueda haber ambigüedad (`_tn`, `_usd`, `_dias`, `_usd_tn_dia`).
- Toda tarifa tiene vigencia. **Una tarifa faltante no es cero**: produce error explícito y detiene la corrida.

---

## 4. Tablas maestras

### 4.1 `Producto`

| Columna | Tipo | Regla |
|---|---|---|
| `producto` | enum | `JUGO`, `CASCARA`, `ACEITE` |
| `tipo_contenedor` | enum | `REEFER_40`, `DRY_HC_40`, `IMO_DRY_20` |
| `capacidad_contenedor_tn` | double | > 0 |
| `descripcion` | texto | Libre |

Reemplaza `obtenerTipoContenedor()` y `obtenerCapacidadContenedorTon()` hardcodeados.

### 4.2 `Ubicacion`

| Columna | Tipo | Regla |
|---|---|---|
| `id_ubicacion` | texto | Único |
| `nombre` | texto | |
| `tipo` | enum | `PLANTA`, `DEPOSITO`, `TERMINAL` |
| `habilita_consolidacion` | bool | |
| `habilita_cross_dock` | bool | |
| `posiciones_consolidacion` | int | >= 0 |
| `posiciones_cross_dock` | int | >= 0 |
| `contenedores_por_posicion_dia` | double | > 0 si consolida |

Nota: hoy la habilitación se deriva de `capacidad > 0` (ADR-009). El contrato la separa porque un depósito puede almacenar sin poder consolidar. Si se confirma que en la práctica coinciden, se cargan iguales; el modelo no cambia.

### 4.3 `CapacidadUbicacion`

| Columna | Tipo | Regla |
|---|---|---|
| `id_ubicacion` | texto | FK `Ubicacion` |
| `producto` | enum | |
| `capacidad_tn` | double | >= 0. Cero = no almacena ese producto |

### 4.4 `Distancia`

| Columna | Tipo | Regla |
|---|---|---|
| `origen` | texto | FK `Ubicacion` |
| `destino` | texto | FK `Ubicacion` |
| `distancia_km` | double | > 0 |
| `transito_dias_min` | double | > 0 |
| `transito_dias_moda` | double | >= min |
| `transito_dias_max` | double | >= moda |

### 4.5 `TiemposOperativos`

| Columna | Tipo | Regla |
|---|---|---|
| `operacion` | enum | `CARGA_PRODUCTO`, `DESCARGA_PRODUCTO`, `CONSOLIDACION`, `CROSS_DOCK`, `RETIRO_VACIO`, `INGRESO_TERMINAL` |
| `id_ubicacion` | texto | Vacío = aplica a todas |
| `producto` | enum | Vacío = aplica a todos |
| `dias_min` / `dias_moda` / `dias_max` | double | En días o fracción |

---

## 5. Tablas de tarifas

Todas incluyen `vigencia_desde` y `vigencia_hasta` (fecha; `vigencia_hasta` vacío = sin límite) y `moneda` (`USD` o `ARS`).

### 5.1 `TarifaFleteProducto`

| Columna | Tipo |
|---|---|
| `origen`, `destino` | texto |
| `producto` | enum |
| `tipo_movimiento` | enum: `REUBICACION_PRODUCTO`, `TRANSPORTE_CROSS_DOCK` |
| `unidad_tarifaria` | enum: `USD_VIAJE`, `USD_TN` |
| `tarifa` | double > 0 |
| `capacidad_camion_tn` | double > 0 |

### 5.2 `TarifaCicloContenedor`

`terminal`, `lugar_carga`, `producto`, `tipo_contenedor`, `tarifa_usd_contenedor`, `duracion_ciclo_dias`.

### 5.3 `TarifaAlmacenamiento`

`id_deposito`, `producto`, `in_usd_tn`, `storage_usd_tn_dia`, `out_usd_tn`, `periodo_minimo_dias` (0 si no hay mínimo).

`periodo_minimo_dias` se agrega porque los depósitos suelen cobrar por período mínimo (quincena o mes) y no por día exacto. Si la tarifa real es día a día, se carga 0.

### 5.4 `TarifaServicioCarga`

`id_ubicacion`, `producto`, `tipo_servicio` (`CONSOLIDACION` o `CROSS_DOCK`), `tarifa_usd_contenedor`.

### 5.5 `TarifaTerminal`

`terminal`, `producto`, `tipo_contenedor`, `tarifa_usd_contenedor`.

### 5.6 `TarifaTHC`

`naviera`, `terminal`, `producto`, `tipo_contenedor`, `tarifa_usd_contenedor`.

### 5.7 `TarifaDespachante`

`lugar_consolidacion`, `producto`, `tarifa_usd_contenedor`.

---

## 6. Tablas de escenario

### 6.1 `Escenario`

| Columna | Tipo | Descripción |
|---|---|---|
| `id_escenario` | texto | Único |
| `descripcion` | texto | |
| `fecha_inicio_campania` | fecha | |
| `duracion_campania_dias` | int | Default 183 |
| `dias_operativos_semana` | int | Default 6 |
| `cantidad_replicas` | int | Default 30 |
| `semilla_base` | int | |
| `camiones_producto` | int | |
| `camiones_portacontenedor` | int | |
| `indisponibilidad_flota` | double | 0..1 |
| `variabilidad_produccion` | double | 0 = determinístico |
| `variabilidad_demanda` | double | 0 = determinístico |
| `habilita_cross_dock` | bool | |
| `politica_prioridad` | enum | `FECHA_LIMITE`, `FIFO`, `MAYOR_VOLUMEN` |
| `tipo_cambio_ars_usd` | double | Para tarifas en ARS |

### 6.2 `ProduccionPlan`

Producción diaria esperada. Con datos sintéticos la genera el modelo; con Excel se carga la serie real o estimada.

| Columna | Tipo | Regla |
|---|---|---|
| `id_escenario` | texto | |
| `dia` | int | 0..duración. Alternativamente `fecha` |
| `producto` | enum | |
| `produccion_tn` | double | >= 0 |

Si la tabla trae una sola fila por producto sin `dia`, se interpreta como media diaria constante para toda la campaña.

### 6.3 `PedidoPlan`

| Columna | Tipo | Regla |
|---|---|---|
| `id_escenario` | texto | |
| `codigo_pedido` | texto | Único por escenario |
| `dia_llegada` | int | >= 0 |
| `dia_limite` | int | >= `dia_llegada` |
| `cliente` | texto | |
| `producto` | enum | |
| `calidad` | texto | |
| `lote_solicitado` | texto | Vacío = cualquier lote del producto/calidad |
| `toneladas_solicitadas` | double | > 0 |
| `terminal` | texto | FK `Ubicacion` tipo `TERMINAL` |
| `naviera` | enum | |
| `incoterm` | texto | Informativo |

`cliente` y `calidad` se incluyen desde el inicio aunque el alcance actual use un solo valor: son necesarios para identificar el lote comercial abierto y agregarlos después implicaría migrar datos.

### 6.4 `LoteInicial` (opcional)

Stock preexistente al inicio de la campaña: `id_escenario`, `id_lote`, `producto`, `calidad`, `cliente`, `id_ubicacion`, `toneladas_tn`, `dia_ingreso`, `toneladas_objetivo`.

Necesaria para arrancar una corrida a mitad de campaña o para reproducir un estado real.

---

## 7. Validación de entrada

Antes de simular, el modelo ejecuta `validarDatosEntrada()` y **aborta con mensaje explícito** si:

1. falta una tabla obligatoria o una columna;
2. hay una FK inexistente (ubicación, producto, terminal, naviera);
3. una capacidad, tarifa o duración es negativa, o una capacidad de contenedor es cero;
4. falta una tarifa requerida por alguna combinación alcanzable (producto × ubicación × terminal × naviera presente en el escenario);
5. `vigencia` no cubre todo el horizonte de campaña, o hay tarifas superpuestas para la misma clave;
6. `dia_limite < dia_llegada`, o `toneladas_solicitadas <= 0`;
7. un `lote_solicitado` referenciado no existe ni puede producirse;
8. `transito_dias_min > moda > max` está mal ordenado.

Los errores se listan **todos juntos** en un CSV (`errores_entrada.csv`) en lugar de abortar en el primero: corregir un Excel de a un error por corrida es inviable.

## 8. Salidas

Simétricas a la entrada, siempre con `id_escenario`, `replica`, `semilla` y `version_modelo`:

| Archivo | Contenido |
|---|---|
| `resumen_escenario.csv` | Una fila por escenario y réplica con todos los KPIs |
| `registro_costos.csv` | Un registro por costo devengado (categoría, pedido, lote, contenedor, ubicación, base, tarifa, importe, histórico) |
| `series_diarias.csv` | Stock, reservado, producción, excedente y utilización por día, producto y ubicación |
| `esperas_recursos.csv` | Operaciones postergadas por día, recurso y causa |
| `errores_entrada.csv` | Validaciones fallidas |
