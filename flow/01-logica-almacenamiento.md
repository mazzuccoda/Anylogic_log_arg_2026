# 1. Lógica de almacenamiento — planta, depósitos e inventario por capas

> Funciones fuente: `model_src/Main.java`, `model_src/Planta.java`,
> `model_src/Deposito.java`, `model_src/Inventario.java`, `model_src/Capa.java`.
> Documentación técnica: [`docs/04_Flujos/Flujos_Operativos.md`](../docs/04_Flujos/Flujos_Operativos.md) §2-3,
> [`docs/03_Logica/Funciones.md`](../docs/03_Logica/Funciones.md) §2-5.

## 1.1 La idea de fondo: nadie tiene "un saldo", todos consultan las mismas capas

Antes de entrar en la lógica de decisión hay una idea de diseño que conviene
tener clara, porque cambia cómo se lee todo el resto: **ni la planta, ni un
depósito, ni un lote tienen una variable "stock" propia**. El único lugar
donde vive el inventario es `Main.inventario`, una lista de `Capa` (ADR-021,
ADR-023, ADR-030). Cada `Capa` es:

```java
// Capa.java (simplificado)
String idLote;
String idUbicacion;     // "PLANTA" o el id de un Deposito
double toneladas;
double reservado;       // cuánto de esa capa está comprometido con algún pedido
double diaIngreso;      // para el FIFO y para el costo de almacenaje
```

Cuando alguien pregunta "¿cuánto jugo hay en la planta?", en realidad se está
preguntando "¿cuánto suman las capas con `idUbicacion == "PLANTA"` y
`producto == JUGO`?". Por eso `Planta.getStock()` y `Deposito.getStock()` son
una sola línea cada una:

```java
// Planta.java
double getStock(TipoProducto producto) {
    Main modelo = (Main) getRootAgent();
    return modelo.inventario.stock("PLANTA", producto);
}
```

Esto importa para la lógica de almacenamiento porque significa que **"hay
lugar" y "está reservado" son preguntas independientes**: `stock` es lo que
físicamente está ahí, `reservado` es lo que ya tiene dueño (un pedido) aunque
no se haya movido todavía, y `libre = stock − reservado` es lo único que
puede ofrecerse a un lote nuevo o a un pedido nuevo.

## 1.2 Etapa 1 — la planta: capacidad como umbral, no como tope

```java
// Planta.java
void producir() {
    // La produccion del dia es un dato de entrada (tabla ProduccionPlan) y entra
    // completa: la capacidad de la planta es un umbral, no un tope, porque el
    // producto ya esta cosechado y no se puede descartar (ADR-048).
    ...
    ingresarProduccion(TipoProducto.JUGO, datos.produccionDelDia(dia, TipoProducto.JUGO));
    ...
}
```

Esta es una decisión de diseño explícita (**ADR-048**) y distinta de lo que
uno esperaría de un depósito: la producción del día **entra siempre entera**,
aunque supere `capacidadJugo` (5.000 tn en el escenario base). La razón es
física, no de sistemas — el jugo ya está cosechado, no se puede "rechazar".
Lo que pasa cuando se supera la capacidad no es un bloqueo, es una
**sobrecarga medida**: `Main.registrarOcupacionPlanta()` acumula
tonelada-día por encima del nivel nominal y del crítico, cuenta los días en
sobrecarga y guarda el pico de ocupación — son los KPIs
`tonDiaSobreNominalPlanta`, `diasSobrecargaPlanta`, `picoOcupacionPlantaPct`.
La planta nunca le dice "no" a la cosecha; lo que hace es empujar producto
hacia los depósitos cada vez con más urgencia cuanto peor esté la sobrecarga
(ver §1.4).

## 1.3 Etapa 2 — el depósito de terceros: ahí sí hay un tope duro

```java
// Deposito.java
double getEspacioDisponible(TipoProducto producto) {
    return max(0, getCapacidad(producto) - getStock(producto));
}

boolean puedeRecibir(TipoProducto producto, double toneladas) {
    if (!habilitado) return false;
    if (toneladas <= 0) return false;
    return getEspacioDisponible(producto) >= toneladas;
}
```

A diferencia de la planta, el depósito de terceros **sí tiene un tope real**:
`getEspacioDisponible` nunca es negativo, y `puedeRecibir` es el semáforo
binario que corta cualquier intento de meter más producto del que entra.
Tiene sentido — es un depósito ajeno, alquilado, no la cámara propia de la
planta.

Pero hay un matiz importante que el modelo ya resuelve y que conviene
entender: el espacio "disponible" de la foto de hoy **no** es lo mismo que el
espacio realmente comprometible, porque puede haber camiones ya en camino
hacia ese depósito con varios días de viaje (ADR-061):

```java
// Main.java
double espacioDisponibleEfectivo(Deposito deposito, TipoProducto producto) {
    return max(
        0,
        deposito.getEspacioDisponible(producto)
        - toneladasEnTransitoHacia(deposito.idUbicacion, producto)
    );
}
```

`espacioDisponibleEfectivo()` —no `getEspacioDisponible()`— es la función que
usa la asignación de destino (§1.4) y el filtro de descarte (§1.5). La
diferencia entre las dos es exactamente lo que un viaje de varios días puede
esconder: un depósito puede *verse* con 500 tn libres hoy y en realidad tener
sólo 200, porque 300 ya están viajando hacia él y van a ocupar ese lugar en
cuanto lleguen.

## 1.4 La decisión activa: ¿cuánto sacar de planta, y a qué depósito?

Esta es la parte que responde a "cómo decide el modelo dónde y cuánto
guardar". Ocurre todos los días, en el paso 6 de la secuencia diaria
(`revisarTransferenciasPlanta()`, ver `pasoDiario_accion()`), y tiene dos
preguntas separadas: **cuánto** sacar de la planta, y **a cuál** depósito.

### 1.4.1 Cuánto sacar — tres motivos combinados con `max`, no con suma (ADR-056)

```java
double toneladasASacarDePlanta(TipoProducto producto) {
    // 1. desborde:   proyectado - capacidad nominal de planta
    // 2. servicio:   demanda proyectada de pedidos - libre en depósitos
    // 3. preventivo: si el proyectado toca el umbral de alerta, bajar al objetivo
    return max(componentePorDesborde, max(componentePorServicio, componentePreventivo));
}
```

La razón de usar `max` y no suma es que **los tres motivos leen el mismo
stock**: si ya cuenta como "hay que sacar 200 tn porque nos desbordamos", no
tiene sentido sumarle otras 150 tn por "servicio" sobre las mismas 200 —
serían las mismas toneladas contadas dos veces. Gana el motivo más exigente,
no la suma de todos.

| Componente | Se activa cuando… | Ejemplo ilustrativo |
|---|---|---|
| **Desborde** | `stock + producción proyectada > capacidad nominal` | Planta con 5.000 tn de capacidad, stock 4.200 tn, pronóstico de 3 días = 900 tn → proyectado 5.100 tn → desborde = 100 tn |
| **Servicio** | Los pedidos con fecha límite próxima necesitan más de lo que ya está libre en depósitos | Demanda proyectada 800 tn, libre en depósitos 500 tn → componente servicio = 300 tn |
| **Preventivo** | El proyectado toca el umbral de alerta (% de la capacidad) aunque todavía no desborde | Capacidad 5.000 tn, umbral alerta 90 % = 4.500 tn, umbral objetivo 70 % = 3.500 tn, proyectado 4.700 tn → bajar hasta el objetivo = 1.200 tn |

En el ejemplo, el motivo que manda es el **preventivo** (1.200 tn), porque es
el más alto de los tres — no se suman.

> Con la política `REACTIVA` (el escenario de comparación histórico) este
> cálculo no corre: en su lugar, `toneladasASacarReactiva()` sólo mira el
> stock de **hoy** contra un umbral en porcentaje, sin mirar el pronóstico.

### 1.4.2 A qué depósito — costo por tonelada, salvo en sobrecarga crítica (ADR-056)

```java
Deposito seleccionarDeposito(TipoProducto producto, double toneladas,
        boolean priorizarEspacio, Set<String> excluidos) {

    for (Deposito deposito : depositos) {
        if (excluidos.contains(deposito.idUbicacion)) continue;
        if (!motivoDescarteDeposito(deposito, producto, 0).isEmpty()) continue;

        double posible = min(toneladas, espacioDisponibleEfectivo(deposito, producto));
        if (posible <= 0.0001) continue;

        double costoEstimado =
            calcularCostoPlantaDeposito(deposito, producto, posible)          // flete
            + posible * deposito.getTarifaAlmacenamiento(producto)            // storage
                * diasEstimadosAlmacenamiento;

        double costoPorTonelada = costoEstimado / posible;

        boolean mejor = priorizarEspacio
            ? (posible > mayorEspacio || (posible == mayorEspacio && costoPorTonelada < menorCostoPorTonelada))
            : costoPorTonelada < menorCostoPorTonelada;

        if (mejor) { mejorDeposito = deposito; ... }
    }
    return mejorDeposito;
}
```

Traducido: para cada depósito que **pasa el filtro de capacidad y tarifas**
(§1.5), se estima "si le mando `posible` toneladas hoy, ¿cuánto me cuesta en
total? (flete planta→depósito + storage proyectado sobre
`diasEstimadosAlmacenamiento`)", y se **divide por tonelada** — no se compara
el total, porque dos depósitos pueden aceptar volúmenes distintos y comparar
totales premiaría al que menos acepta. Gana el menor costo por tonelada.

**La excepción — `priorizarEspacio` (sobrecarga crítica, ADR-056):** cuando
`plantaEnSobrecargaCritica(producto)` es verdadero (el stock de planta ya
pasó el umbral de sobrecarga, no sólo el de alerta), el criterio **se
invierte**: gana el depósito donde **entra más** producto, y el costo sólo
desempata entre depósitos que aceptan lo mismo. La lógica es de urgencia: con
la planta en riesgo real de desborde, lo prioritario es que el producto
*salga*, no que salga lo más barato posible — el volumen a mover no cambia
por estar en sobrecarga (eso ya lo decidió §1.4.1), lo que cambia es el
**orden** de los destinos candidatos.

### 1.4.3 El reparto no es todo-o-nada (ADR-056)

```java
double transferirProductoADepositos(TipoProducto producto, double toneladasObjetivo, boolean priorizarEspacio) {
    double pendiente = toneladasObjetivo;
    Set<String> agotados = new HashSet<>();

    while (pendiente > 0.0001) {
        LoteProducto lote = buscarLoteMasAntiguoEnPlanta(producto);   // FIFO
        if (lote == null) break;

        double aMover = min(inventario.libreDeLoteEn(lote.idLote, "PLANTA"), pendiente);
        Deposito destino = seleccionarDeposito(producto, aMover, priorizarEspacio, agotados);
        if (destino == null) break;

        double movidas = transferirToneladasLote(lote, destino, aMover, false);
        if (movidas <= 0.0001 || movidas + 0.0001 < aMover) agotados.add(destino.idUbicacion);

        pendiente -= movidas;
    }
    if (pendiente > 0.0001) transferenciasIncompletas++;   // se queda en planta, no se pierde
}
```

Esta función resuelve un problema muy concreto: si el objetivo es mover 300
tn y el depósito más barato sólo tiene lugar para 100, **no se corta ahí**.
Se mueven esas 100, ese depósito se marca "agotado por hoy" (no se vuelve a
intentar en la misma corrida del día, para que el bucle siempre termine), y
se repite la búsqueda del mejor destino entre los que quedan para las 200
restantes. Sólo cuando ya no queda ningún depósito con lugar (o tarifa, o
flota — ver §1.5) el remanente se cuenta en `transferenciasIncompletas` y
**se queda en planta**, sumando a la sobrecarga del día siguiente. Nunca se
descarta producto.

### 1.4.4 Ejemplo numérico completo

Depósitos ilustrativos (no son los del Excel real):

| Depósito | Capacidad JUGO (tn) | Stock actual (tn) | En tránsito hacia él (tn) | Tarifa storage (USD/tn/día) |
|---|---|---|---|---|
| DEP_RUTA9 | 3.000 | 2.850 | 100 | 0,90 |
| DEP_NORTE | 2.000 | 1.200 | 0 | 1,40 |
| DEP_SUR | 2.500 | 900 | 0 | 1,10 |

Objetivo de hoy: mover **300 tn de JUGO**, flete planta→depósito ilustrativo
120 USD/tn para todos, `diasEstimadosAlmacenamiento = 40`.

1. **Espacio efectivo** (`espacioDisponibleEfectivo`):
   - DEP_RUTA9: `(3.000 − 2.850) − 100 = −50 → 0` (los 100 en tránsito ya se comen el poco lugar que quedaba) → **descartado, `SIN_ESPACIO`**.
   - DEP_NORTE: `2.000 − 1.200 − 0 = 800` tn.
   - DEP_SUR: `2.500 − 900 − 0 = 1.600` tn.
2. **Costo por tonelada** (con las 300 tn completas entrando en ambos):
   - DEP_NORTE: `(300×120 + 300×1,40×40) / 300 = 120 + 56 = 176`.
   - DEP_SUR: `(300×120 + 300×1,10×40) / 300 = 120 + 44 = 164` ← **mínimo**.
3. → **Gana DEP_SUR.** Con la planta en sobrecarga normal (no crítica), decide el
   costo por tonelada, y DEP_SUR gana por tener la tarifa de storage más baja
   de los dos que tienen lugar — aunque DEP_NORTE tuviera más espacio libre
   en términos absolutos, eso no pesa mientras no haya sobrecarga crítica.
4. **Variante — sobrecarga crítica:** si la planta estuviera sobre el umbral
   crítico (`priorizarEspacio = true`), el criterio cambiaría a "quién tiene
   más espacio": DEP_SUR (1.600 tn libres) le gana a DEP_NORTE (800 tn) igual
   que antes, pero por una razón distinta — no importaría cuál es más barato,
   sólo dónde entra más producto para sacarlo de la planta cuanto antes.

### 1.4.5 REACTIVA vs FLEXIBLE — la misma pregunta, dos reglas muy distintas

`toneladasASacarDePlanta()` (§1.4.1) **no siempre corre**. `revisarTransferenciasPlanta()`
elige entre dos reglas según el escenario, y cuál está activa cambia por
completo el comportamiento observable de la planta:

```java
void revisarTransferenciasPlanta() {
    boolean flexible = "FLEXIBLE".equals(datos.escenario.politicaFrioPropio);
    double toneladas = flexible
        ? toneladasASacarDePlanta(producto)      // desborde + servicio + preventivo, con forecast
        : toneladasASacarReactiva(producto);      // solo mira el % de stock de HOY
    ...
}
```

| | `FLEXIBLE` | `REACTIVA` |
|---|---|---|
| Mira el pronóstico (`forecastProduccion`, `dias_forecast` días) | Sí — desborde y preventivo se calculan sobre el stock **proyectado** | **No.** Sólo compara el stock de hoy contra un % |
| Mira la demanda de pedidos (`demandaProyectada`) | Sí — componente de servicio | No |
| Regla | `max(desborde, servicio, preventivo)` | `if (stock/capacidad < umbralAlerta%) no hacer nada; si no, bajar hasta umbralObjetivo%` |
| Banda resultante | Variable, según lo que se venga (producción + demanda) | Fija: entre `umbral_objetivo_pct` y `umbral_alerta_pct` de la capacidad nominal |

**Por qué importa para leer un resultado real:** si tu escenario tiene
`politica_frio_propio = REACTIVA` (es el caso del escenario de ejemplo
`E-00` en `datos/entrada_ejemplo.xlsx`, con `umbral_alerta_pct = 95` y
`umbral_objetivo_pct = 90`), esta función **nunca mira el futuro** — sólo
empuja producto a depósito cuando la planta ya pasó el 95 % de su capacidad
nominal (5.000 tn de JUGO), y sólo hasta bajar al 90 %. Es una banda muy
angosta pensada para que la planta esté casi siempre llena mientras hay
cosecha activa, no para anticipar nada. El `forecastProduccion(producto,
dias_forecast)` existe en el código y se usa en el rastro de diagnóstico
(`debugPlanificacion`), pero con `REACTIVA` activo **esos días de pronóstico
no alimentan ninguna decisión real** — sólo se activan bajo `FLEXIBLE`.

## 1.5 Por qué un depósito queda afuera — el filtro único (`motivoDescarteDeposito`)

```java
String motivoDescarteDeposito(Deposito deposito, TipoProducto producto, double toneladas) {
    if (deposito == null) return "INEXISTENTE";
    if (!deposito.habilitado) return "NO_HABILITADO";
    if (deposito.getCapacidad(producto) <= 0.0001) return "SIN_CAPACIDAD_PRODUCTO";
    if (espacioDisponibleEfectivo(deposito, producto) <= 0.0001) return "SIN_ESPACIO";
    if (!datos.hayTarifaFlete(dia, "PLANTA", deposito.idUbicacion, producto)) return "TARIFA_INEXISTENTE";
    if (!datos.hayTarifaSitio(dia, deposito.idUbicacion, producto)) return "TARIFA_SITIO_INEXISTENTE";
    if (toneladas > 0.0001 && !flotaProductoAlcanza("PLANTA", deposito.idUbicacion, toneladas)) return "SIN_FLOTA";
    return "";   // "" significa "puede recibir"
}
```

Es una función pequeña pero clave de diseño: **es la única fuente del
filtro**. `seleccionarDeposito()` la usa para decidir quién compite, y
`diagnosticoDepositos()` (la que imprime la traza con `debugPlanificacion`)
la usa para explicar por qué cada depósito quedó afuera. Al ser la misma
función en los dos lugares, el diagnóstico **no puede mentir** sobre el
motivo real del descarte — no hay dos copias de la regla que puedan
desalinearse.

| Motivo | Qué significa |
|---|---|
| `NO_HABILITADO` | El depósito está apagado en el escenario |
| `SIN_CAPACIDAD_PRODUCTO` | Ese depósito no maneja ese producto (capacidad declarada = 0) |
| `SIN_ESPACIO` | No entra ni una tonelada, contando lo que ya viene en camino |
| `TARIFA_INEXISTENTE` / `TARIFA_SITIO_INEXISTENTE` | Falta la tarifa de flete o de storage para hoy — **el modelo nunca asume costo cero**, así que sin tarifa el depósito no es candidato |
| `SIN_FLOTA` | No hay camiones disponibles para llevar el volumen hoy (o, con flota multidiaria activada, ningún camión que llegue antes del corte) |

## 1.6 El costo de almacenamiento, día por día

```java
// Main.java, paso 10 de la secuencia diaria
void devengarAlmacenamientoDiario() {
    for (Capa capa : inventario.capas) {
        // el producto cruzado (cross dock) no pasa por storage: nunca ingresó al depósito
        if (cruzados.contains(capa.claveReserva())) continue;
        ...
        registro.registrar(..., capa.toneladas, tarifaStorage, ...);
    }
}
```

La fórmula, documentada en [`Modelo_de_Costos.md`](../docs/05_Costos/Modelo_de_Costos.md#5-fórmulas):

```
Costo storage = toneladas almacenadas × días × tarifa USD/tn/día
```

pero la implementación real no calcula "toneladas promedio × días totales":
recorre **cada capa** todos los días y le cobra un día de tarifa sobre lo que
tiene en ese momento. Esto es lo que permite que un retiro parcial a mitad de
camino no siga pagando storage sobre lo que ya se fue, y que el cross dock
—que nunca entra formalmente al depósito— no pague storage en absoluto
(§1.8).

## 1.7 El costo de oportunidad del frío propio — un cargo de HOY, no una proyección

El tablero de campaña muestra una línea "Costo oportunidad frío" (KPI
`costoOportunidadFrio`). Es fácil leerla como si el modelo estuviera
*proyectando* cuánto va a costar el frío propio hacia adelante — pero **no
es una proyección**: es un cargo que se registra todos los días,
proporcional al stock de **ese mismo día**, en el mismo paso de la secuencia
diaria que devenga el storage de depósito (paso 10b, justo después de 10):

```java
void devengarOportunidadFrioPropio() {
    // El frio propio no se factura, pero ocupa un recurso que tiene alternativa.
    // Se devenga aparte para que el costo de caja siga siendo comparable contra la
    // cotizacion de un tercero (ADR-049).
    for (TipoProducto producto : TipoProducto.values()) {
        DatosEntrada.TarifaSitio tarifa = datos.tarifaSitio(diaCampania(), "PLANTA", producto);

        costoOportunidadFrio += registro.registrar(
            ..., inventario.stock("PLANTA", producto), tarifa.oportunidadUsdTnDia, ...
        );
    }
}
```

La cuenta es literalmente:

```
costo_oportunidad_del_día = stock_en_planta_HOY × oportunidad_usd_tn_dia
```

En `datos/entrada_ejemplo.xlsx` (hoja `TarifaSitio`), `oportunidad_usd_tn_dia`
para JUGO en PLANTA es **0,25 USD/tn/día**. Con eso:

| Día | Stock en planta (JUGO) | Costo de oportunidad de ese día |
|---|---|---|
| día con 4.200 tn en planta | 4.200 tn | `4.200 × 0,25 = 1.050 USD` |
| día con 0 tn en planta | 0 tn | `0 × 0,25 = 0 USD` |

**Si el stock de hoy es 0, el cargo de hoy es exactamente 0.** No hay ningún
cálculo "hacia adelante" que lo compense ni lo suavice — es, a propósito
(ADR-049), un costo **retrospectivo**, para poder comparar el costo de caja
real contra "cuánto valdría ese mismo lugar si fuera un depósito de
terceros" sin mezclar los dos. La única función del modelo que sí mira
adelante es `forecastProduccion()` (§1.4.5), y esa alimenta la decisión de
**cuánto sacar de planta**, no el costo de oportunidad — son dos cálculos
distintos que conviene no confundir.

### Por qué la planta puede llegar a 0 al cierre de una campaña — no es un bug

Con el escenario de ejemplo `E-00` (`REACTIVA` + política de selección
`PRIORIDAD_FRIO_PROPIO`), que la planta termine en 0 tn de JUGO es el
resultado esperable de tres cosas actuando juntas, no de una sola:

1. **Ya no entra más cosecha.** `ProduccionPlan` para JUGO en `E-00` está en
   0 tn/día desde bastante antes del cierre de la campaña (364 días) — no es
   que el modelo "vacíe" la planta, es que no hay más fruta.
2. **La demanda sigue sirviéndose preferentemente desde ahí.** Con
   `politica_seleccion = PRIORIDAD_FRIO_PROPIO`, `ordenarAlternativas()`
   (documento 02, §2.4.4) prefiere consolidar directo desde planta antes que
   desde depósito cuando el servicio está empatado — así que mientras quede
   algo en planta, los últimos pedidos de la campaña lo van a consumir ahí
   primero, en vez de esperar a que salga.
3. **La regla `REACTIVA` (§1.4.5) no está diseñada para reponer nada.** Sólo
   sabe empujar excedente hacia depósito cuando se pasa del 95 %; no tiene
   ninguna regla simétrica para "traer de vuelta" stock a la planta cuando
   está bajando. Una vez que deja de entrar producción, no hay ningún
   mecanismo que evite que el stock siga bajando hasta 0.

Es el comportamiento correcto de una campaña estacional que se agota: el
frío propio se vacía solo al final, junto con la cosecha.

## 1.8 La salida rápida: cross dock (no pasa por storage)

No toda tonelada que sale de planta hacia una zona de depósito pasa por
"guardar". El **cross dock** (`programarCrossDockDelDia()`, ver
`docs/04_Flujos/Flujos_Operativos.md` §8) transfiere producto directo de un
camión a un contenedor, sin que el producto quede registrado como stock del
depósito. Requiere que coincidan el mismo día: camión de producto, portacon-
tenedor con vacío y una posición de cross dock libre. A cambio, no paga IN ni
storage ni OUT — sólo flete de producto, ciclo del contenedor y la tarifa de
cross dock (ver tabla de aplicabilidad en `Modelo_de_Costos.md` §4). Es la
otra cara de la misma decisión de almacenamiento: a veces la respuesta a
"¿dónde guardo esto?" es "en ningún lado, va directo al contenedor".

## 1.9 Rebalanceo entre depósitos — cuando el problema no es el costo, es la salida (ADR-066)

Todo lo anterior en este documento asume que, una vez que el producto llega a un depósito, el único camino de salida es que algún pedido lo elija (doc `flow/02`). En la práctica aparece un caso que ese esquema no cubre: un depósito puede tener stock perfectamente vendible y aun así no tener **cómo** sacarlo a tiempo, porque no tiene capacidad de cross dock ni mucha capacidad de consolidación propia. Ese stock no pierde la competencia por caro — la pierde porque, aunque gane, el depósito no puede armar el contenedor lo bastante rápido.

Con los datos de `datos/entrada_ejemplo.xlsx`, `BOREAS` es exactamente ese caso: `posiciones_cross_dock = 0` y sólo 3 contenedores/día, contra 16 posiciones y 10 contenedores/día de `RUTA9`.

**La solución no es ajustar el costo — es mover el producto a un depósito que sí pueda despacharlo:**

```java
void revisarRebalanceoEntreDepositos() {
    for (TipoProducto producto : TipoProducto.values()) {
        for (Deposito origen : depositos) {

            if (datos.capacidadCrossDockDia(origen.idUbicacion) > 0.0001) {
                continue;   // ya puede despachar cross dock, no hace falta mover nada
            }

            double libre = inventario.libre(origen.idUbicacion, producto);
            if (libre <= 0.0001) continue;

            // solo lo que ya lleva el horizonte de holding sin moverse (mismo que ADR-065)
            double diaMasAntiguo = max(0, inventario.fifo(origen.idUbicacion, producto).get(0).diaIngreso);
            if (time() - diaMasAntiguo < diasEstimadosAlmacenamiento) continue;

            LoteProducto lote = buscarLoteMasAntiguoEnDeposito(origen.idUbicacion, producto);
            Deposito destino = mejorDestinoRebalanceo(origen, producto, libre);

            if (destino == null) { rebalanceosSinDestino++; continue; }

            transferirEntreDepositos(lote, origen, destino, libre);
        }
    }
}
```

Corre como paso 6b de la secuencia diaria, justo después de `revisarTransferenciasPlanta()`. `mejorDestinoRebalanceo()` elige destino con el mismo criterio que ya usa `seleccionarDeposito()` (§1.4.2): flete de reubicación más el holding que el destino va a seguir devengando, proyectado con `horizonteHoldingEvitado()` — la misma función que ADR-065 usa para el crédito del evaluador de pedidos. `transferirEntreDepositos()` cobra la fórmula que `docs/06_Validacion/Plan_de_Validacion.md` (V-COST-06) ya tenía documentada y sin implementar: OUT del origen + flete entre depósitos + IN en el destino, **sin** cargos de contenedor — eso lo paga después quien arme el contenedor en destino, por el camino que ya existe.

**Qué NO hace, a propósito:**

- **No compite por costo dentro de `generarAlternativas()`.** El problema de Boreas no es que salga caro — es que no puede despachar a tiempo. Meterlo a competir por costo contra las demás alternativas de un pedido puntual podría perder igual contra una opción más barata que tampoco puede cumplir el plazo.
- **No usa la agenda de flota multidiaria (ADR-061).** Calcula el camión-día con `distanciaKmSimetrica()` inline, no con `camionDiaViaje()` — que usa `distanciaKm()` sin dirección alternativa y **aborta la corrida** si falta la fila exacta origen→destino. Como hoy no existe ninguna fila depósito→depósito en `Distancia`, usar la versión que revienta habría sido peligroso; la versión simétrica simplemente no mueve nada si falta el dato. Es una simplificación declarada, no la versión final.
- **No asume tarifa ni distancia si faltan.** Antes de mover un gramo, chequea `hayTarifaFlete`, `hayTarifaSitio` y `distanciaKmSimetrica() >= 0`. Sin esas filas cargadas —el caso de `datos/entrada_ejemplo.xlsx` hoy— el mecanismo corre todos los días y no mueve nada. Hace falta cargar esos datos para que tenga algún efecto.

## 1.10 Resumen — criterios de almacenamiento

| # | Criterio | Función | ¿Duro o de costo? |
|---|---|---|---|
| 1 | Cuánto sacar de planta | `toneladasASacarDePlanta()` (FLEXIBLE, con forecast) **o** `toneladasASacarReactiva()` (REACTIVA, sin forecast) según `politica_frio_propio` | Determina volumen, no destino |
| 2 | Capacidad física del depósito (neta de tránsito) | `espacioDisponibleEfectivo()` / `puedeRecibir()` | **Duro** |
| 3 | Tarifa de flete y de storage existentes | `datos.hayTarifaFlete()` / `hayTarifaSitio()` | **Duro** — sin tarifa, no hay candidato |
| 4 | Flota de camiones disponible hoy | `flotaProductoAlcanza()` / agenda multidiaria | **Duro** |
| 5 | Costo por tonelada (flete + storage proyectado) | `seleccionarDeposito()` | De costo — decide *entre* los que pasan 2, 3 y 4 |
| 6 | Espacio disponible (en vez de costo) | `seleccionarDeposito(priorizarEspacio=true)` | Sólo si la planta está en sobrecarga crítica (ADR-056) |
| 7 | Producción de la planta | `producir()` | **Nunca se bloquea** (ADR-048) — la capacidad de planta es lectura, no filtro |
| 8 | Costo de oportunidad del frío propio | `devengarOportunidadFrioPropio()` = stock de **hoy** × tarifa | **No es proyección** — con stock 0 el cargo del día es 0 (ADR-049) |
| 9 | Capacidad de salida del depósito (no de costo) | `revisarRebalanceoEntreDepositos()` — sólo depósitos sin cross dock (ADR-066) | **Duro por capacidad de despacho**, no por precio — reubica hacia quien sí puede sacarlo |

La planta jamás dice "no" a la cosecha; el depósito sí dice "no" cuando no
hay lugar, tarifa o camión. Entre los depósitos que dicen "sí", gana el más
barato por tonelada — salvo que la planta esté en emergencia, en cuyo caso
gana el que tiene más lugar. Y el costo de haber elegido mal (o bien) se ve
todos los días en `devengarAlmacenamientoDiario()`, tonelada por tonelada, no
como un promedio al final de la campaña.
