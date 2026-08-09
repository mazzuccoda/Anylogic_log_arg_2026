# 2. Lógica de entrega — cómo elige el modelo el circuito de cada pedido

> Funciones fuente: `model_src/Main.java` (evaluador de circuitos, C6),
> `model_src/AlternativaCircuito.java`, `model_src/AsignacionPedido.java`,
> `model_src/Pedido.java`.
> Documentación técnica: [`docs/03_Logica/Funciones.md`](../docs/03_Logica/Funciones.md) §4
> ("Evaluador de circuitos"), ADR-054, ADR-055, ADR-056, ADR-059, ADR-060, ADR-061.

## 2.1 Lo primero que hay que soltar: "entregar un pedido" no es una decisión, son varias

En el modelo de juguete más simple uno elegiría un depósito y listo. Acá un
`Pedido` se sirve con **una o varias `AsignacionPedido`**, cada una desde un
origen distinto (planta o un depósito), por un circuito distinto
(consolidación en planta, en depósito, en terminal, o cross dock), y puede
tardar varios días en completarse (ADR-055). La razón de diseño está en el
glosario del propio proyecto:

> **Reserva**: bloqueo de toneladas físicas existentes en una capa, a favor
> de un pedido y contenedor.

Reservar **no es todo o nada**: un pedido de 500 tn puede reservar 300 hoy
desde un depósito y las 200 restantes recién mañana desde otro (o desde la
planta), y cada fracción tiene su propia `AsignacionPedido` con su propia
clave (`codigoPedido + "|" + idAsignacion`) para que dos asignaciones del
mismo pedido en el mismo sitio no se pisen entre sí.

```java
// Pedido.java
double toneladasPendientesAsignar() {
    return max(0, toneladasSolicitadas - toneladasAsignadasAcumuladas());
}
```

Todo el evaluador de circuitos trabaja sobre este **saldo pendiente**, no
sobre el pedido entero — eso es lo que permite que una alternativa que sólo
puede resolver una parte compita igual, en vez de quedar descartada por "no
alcanza para todo".

## 2.2 El fork inicial: política fija vs. evaluador económico

```java
double asignarParcialPedido(Pedido pedido) {
    return usaEvaluador()
        ? asignarConEvaluador(pedido)
        : asignarConPoliticaFija(pedido);
}

boolean usaEvaluador() {
    return politicaSeleccion == PRIORIDAD_FRIO_PROPIO
        || politicaSeleccion == MENOR_COSTO_INCREMENTAL_FACTIBLE
        || politicaSeleccion == MENOR_COSTO_END_TO_END_FACTIBLE;
}
```

El escenario declara una de estas ocho políticas (`DatosEntrada.PoliticaSeleccion`):

| Familia | Valores | Cómo decide |
|---|---|---|
| **Fija** | `FIJA_PLANTA`, `FIJA_DEPOSITO`, `FIJA_CROSS_DOCK_DEPOSITO`, `FIJA_CROSS_DOCK_TERMINAL`, `MANUAL` | Orden de candidatos cableado (§2.3) — es la conducta *anterior* al evaluador, mantenida como regresión |
| **Económica** (evaluador) | `PRIORIDAD_FRIO_PROPIO`, `MENOR_COSTO_INCREMENTAL_FACTIBLE`, `MENOR_COSTO_END_TO_END_FACTIBLE` | Genera y compara **todos** los circuitos posibles cada vez (§2.4) |

Este documento pide "cómo elige la entrega según costo/capacidad operativa"
— eso es exactamente lo que hace la familia económica, así que el resto se
enfoca ahí. La política fija se explica en §2.3 porque sigue siendo el
*fallback* que usan las económicas cuando conviene, y porque entenderla
ayuda a apreciar por qué se construyó el evaluador.

## 2.3 Política fija — orden cableado + reserva parcial (ADR-054, mantenida)

```java
double asignarConPoliticaFija(Pedido pedido) {
    List<String> candidatos = new ArrayList<>();
    if (consolidaEnPlanta()) candidatos.add("PLANTA");
    for (Deposito d : depositosOrdenadosParaPedido(pedido)) candidatos.add(d.idUbicacion);

    for (String idSitio : candidatos) {
        if (pedido.toneladasPendientesAsignar() <= 0.0001) break;
        asignadas += reservarParcialPedido(pedido, idSitio,
            pedido.toneladasPendientesAsignar(), circuitoDe(idSitio, false), false, ...);
    }

    // Si no alcanzó y el escenario lo permite, cae al evaluador (ADR-060)
    if (pedido.toneladasPendientesAsignar() > 0.0001 && datos.escenario.permiteFallbackPoliticaFija) {
        asignadas += asignarConEvaluador(pedido);
    }
    return asignadas;
}
```

Es la lógica "de siempre": un orden fijo de candidatos (planta primero si la
estrategia consolida ahí, después los depósitos ordenados por costo
estimado — no por nombre), y se les va pidiendo reserva en ese orden hasta
cubrir el pedido o agotar candidatos. Lo único que cambió con ADR-055 es que
**acepta lo que cada uno pueda dar** en vez de exigir todo o nada. Si con
capacidad finita (posiciones de consolidación, ADR-060) el circuito fijo no
puede procesar el saldo y el escenario lo habilita, cae al evaluador como
**fallback** (contado en el KPI `fallbacksPoliticaFija`) — no se pierde
demanda por rigidez de la política.

## 2.4 El evaluador económico — el corazón de la pregunta (ADR-054, "C6")

Esta es la secuencia que corre, **pedido por pedido, ronda por ronda**, cada
vez que la política del escenario es económica:

```
generarAlternativas(pedido)
        │  (enumera TODOS los circuitos físicamente posibles)
        ▼
evaluarAlternativa(pedido, cada alternativa)
        │  (factibilidad: stock, cupo, flota, capacidad — ANTES del costo)
        ▼
costearAlternativa + costearHundidoAlternativa
        │  (incremental, histórico y end-to-end, por componente)
        ▼
ordenarAlternativas(pedido, factibles)
        │  (servicio → política → costo unitario → clave, para desempatar)
        ▼
ejecutarAlternativa(pedido, la primera del ranking)
        │  (reserva contra el origen real; puede tomar menos de lo evaluado)
        ▼
registrarPlan(...)   →  PlanLogistico con TODO lo evaluado y descartado
```

y esto se repite en `asignarConEvaluador()` **mientras quede saldo pendiente**
(hasta `2 + 2 × cantidadDepositos` vueltas), porque tomar stock en una vuelta
cambia lo que las siguientes alternativas pueden ofrecer.

### 2.4.1 `generarAlternativas` — enumerar todo lo que el flujo sabe ejecutar

```java
List<AlternativaCircuito> generarAlternativas(Pedido pedido) {
    List<AlternativaCircuito> alternativas = new ArrayList<>();
    double pendiente = pedido.toneladasPendientesAsignar();

    alternativas.add(alternativaPara(pedido, pendiente, "PLANTA", "PLANTA", CONSOLIDACION_PLANTA, false));
    alternativas.add(alternativaPara(pedido, pendiente, "PLANTA", terminal, CONSOLIDACION_TERMINAL, false));

    for (Deposito deposito : depositos) {
        alternativas.add(alternativaPara(pedido, pendiente, deposito.idUbicacion, deposito.idUbicacion,
            CONSOLIDACION_DEPOSITO, false));
        alternativas.add(alternativaPara(pedido, pendiente, deposito.idUbicacion, deposito.idUbicacion,
            CROSS_DOCK_DEPOSITO, true));
        alternativas.add(alternativaPara(pedido, pendiente, deposito.idUbicacion, terminal,
            CONSOLIDACION_TERMINAL, false));
    }
    // + una alternativa "transferencia depósito→depósito", agregada YA DESCARTADA
    //   porque ese movimiento no existe físicamente en el modelo (C7).
    return alternativas;
}
```

Con 5 depósitos, esto genera **17 alternativas** por pedido y por vuelta: 2
desde planta + 3 por depósito × 5 depósitos + la transferencia descartada.
No hay atajos ni preselección — se generan todas, y es `evaluarAlternativa`
la que decide cuáles sobreviven.

### 2.4.2 `evaluarAlternativa` — factibilidad antes que costo

El orden de los chequeos importa (es la misma jerarquía que en el
almacenamiento, §1.5 del documento anterior, pero aplicada a un pedido):

| Orden | Chequeo | Motivo de descarte si falla |
|---|---|---|
| 1 | Capacidad de posiciones (consolidación o cross dock) antes del cut-off | `SIN_CAPACIDAD_ANTES_CUTOFF` |
| 2 | Flota de producto disponible antes del cut-off | `SIN_FLOTA_ANTES_CUTOFF` / `SIN_FLOTA_PLANTA_DEPOSITO` / `SIN_FLOTA_GRANEL_TERMINAL` |
| 3 | Stock libre en el origen (con asignación parcial, si ya llegó acotado a 0, no hay nada que ofrecer) | `SIN_STOCK` |
| 4 | (sólo cross dock) cross dock habilitado, depósito habilitado, cupo de cross dock, espacio de paso | `CROSS_DOCK_DESHABILITADO`, `ORIGEN_NO_HABILITADO`, `SIN_CUPO_CROSS_DOCK`, `SIN_ESPACIO_DE_PASO` |
| 5 | Capacidad de estiba declarada (¿el sitio arma contenedores alguna vez?) | `CAPACIDAD_ESTIBA_CERO` |

Nótese el detalle del punto 1: **aunque no haya capacidad**, si había stock
disponible sin esa restricción, el modelo **costea igual** ese volumen
hipotético (`toneladasSinRestriccionCapacidad`) sólo para poder medir cuánto
costaría la saturación — y después la descarta. Es lo que alimenta el KPI
`sobrecostoSaturacionUsd`: la falta de capacidad no es invisible, tiene un
precio medible aunque la alternativa no se ejecute.

Sólo la alternativa que pasa los cinco filtros llega a `costearAlternativa`.

### 2.4.3 El costo — tres vistas, nunca mezcladas (ADR-053)

```java
void totalizar() {
    costoIncremental = costoFleteProducto + costoRoundTrip + costoEstiba + costoOut
        + costoTHC + costoTerminal + costoDespachante;
    costoHistorico   = costoInHundido + costoAlmacenajeHundido + costoFleteHundido;
    costoEndToEnd    = costoHistorico + costoIncremental;
}
```

| Vista | Qué incluye | Para qué se usa |
|---|---|---|
| **Incremental** | Sólo lo que la decisión de *hoy* agrega: OUT, flete a cross dock, ciclo del contenedor, consolidación, cross dock, terminal, THC, despachante | Comparación **táctica** — el almacenaje y el flete ya pagados son costo hundido y no pueden decidir dónde consolidar |
| **Histórico** | Lo que el stock ya pagó por estar donde está: IN, flete de guarda, almacenaje acumulado hasta hoy (`toneladaDiaEnStock`, FIFO sobre las capas reales) | Se reporta aparte, para poder explicarlo sin que contamine la comparación |
| **End-to-end** | Histórico + incremental | Comparación **estratégica**, cuando el escenario lo pide (`MENOR_COSTO_END_TO_END_FACTIBLE`) |

Y el costo por tonelada, no el total, es lo que se compara —porque dos
alternativas con asignación parcial pueden ofrecer volúmenes distintos, y
comparar totales premiaría a la que resuelve menos tonelaje:

```java
double costoUnitarioSegun(boolean endToEnd) {
    return toneladas <= 0.0001 ? POSITIVE_INFINITY : costoSegun(endToEnd) / toneladas;
}
```

### 2.4.4 `ordenarAlternativas` — la política, en una sola función

```java
int compare(AlternativaCircuito a, AlternativaCircuito b) {
    if (exigeServicio && a.llegaATiempo != b.llegaATiempo)
        return a.llegaATiempo ? -1 : 1;               // 1. servicio, si el escenario lo exige

    if (frioPropio) {                                  // 2. si la política es PRIORIDAD_FRIO_PROPIO
        boolean pa = "PLANTA".equals(a.idOrigen) && !a.esCrossDock;
        boolean pb = "PLANTA".equals(b.idOrigen) && !b.esCrossDock;
        if (pa != pb) return pa ? -1 : 1;
    }

    int orden = compare(a.costoUnitarioSegun(endToEnd), b.costoUnitarioSegun(endToEnd));
    return orden != 0 ? orden : a.clave().compareTo(b.clave());   // 3. costo, 4. clave (desempate estable)
}
```

Este es exactamente el "según costo / capacidad operativa" del pedido de este
documento, expresado como cuatro criterios en cascada, cada uno desempatando
sólo si el anterior queda parejo:

1. **Servicio primero, si el escenario lo exige** (`servicioMinimoProyectado > 0`):
   ninguna diferencia de costo compra una entrega tarde mientras exista una
   alternativa que llega a tiempo.
2. **Frío propio, si la política es `PRIORIDAD_FRIO_PROPIO`**: entre
   alternativas empatadas en servicio, se prefiere consolidar directo desde
   planta (sin cross dock) antes que usar un depósito de terceros —tiene
   sentido leerlo junto con el documento 01: si ya está en la planta, sacarlo
   sólo para volver a guardarlo en un depósito sería pagar storage dos veces
   por el mismo jugo.
3. **Costo unitario** (incremental o end-to-end, según la política del
   escenario) — el criterio de fondo.
4. **Clave estable** (`circuito|origen|sitioEstiba`) como desempate final,
   para que dos corridas con la misma semilla decidan siempre igual.

### 2.4.5 `ejecutarAlternativa` — el plan se ejecuta con el flujo físico real

```java
double ejecutarAlternativa(Pedido pedido, AlternativaCircuito alternativa) {
    if (!alternativa.factible) return 0;

    if (alternativa.esCrossDock) {
        return ejecutarCrossDockPedido(pedido, buscarDeposito(alternativa.idOrigen), alternativa.toneladas);
    }
    return reservarParcialPedido(pedido, alternativa.idOrigen, alternativa.toneladas,
        alternativa.circuito, false, "evaluador: " + alternativa.clave());
}
```

El evaluador **no mueve producto por su cuenta** — llama a la misma
`reservarParcialPedido()` que usa la política fija. Esto importa: puede pasar
que la alternativa ganadora, al ir a ejecutarse, tome **menos** de lo
evaluado (otro pedido del mismo día se llevó parte de esa capacidad o ese
stock mientras tanto). Cuando eso pasa, la alternativa se marca
`NO_TOMADA_AL_EJECUTAR` —no es que el sitio tuviera una restricción que el
evaluador no vio, es una carrera entre pedidos del mismo día— y
`asignarConEvaluador()` sigue con la siguiente alternativa del ranking en la
misma vuelta.

### 2.4.6 `registrarPlan` — la decisión queda escrita, no sólo ejecutada

Cada vuelta que efectivamente asigna algo crea un `PlanLogistico`: guarda
**todas** las alternativas evaluadas (factibles y descartadas, con su motivo),
cuál se eligió y con qué costo. Es lo que permite, después de correr la
campaña, responder "¿por qué el pedido P-045 no fue al depósito más barato?"
mirando el plan en vez de reconstruir la corrida.

## 2.5 Capacidad operativa: los tres cupos que compiten por el mismo pedido

"Costo" es una de las dos palabras del título de este documento; la otra es
**capacidad operativa**. En este modelo no es una sola cosa — son tres
recursos finitos y separados, cada uno con su propia agenda:

| Recurso | Qué limita | Función clave | ADR |
|---|---|---|---|
| **Posiciones de consolidación / cross dock** | Cuántos contenedores puede armar un sitio por día | `reservarCapacidad()`, `capacidadDisponibleEnVentana()` | ADR-060 |
| **Flota de camiones de producto** | Cuánto puede moverse planta↔depósito↔terminal, y cuándo, dado que un viaje puede durar varios días | `evaluarDisponibilidadFlotaProducto()`, `flotaProductoAlcanza()` | ADR-061 |
| **Cupo de cross dock por sitio y día** | Cuántas operaciones de cruce directo admite un depósito hoy | `capacidadCrossDockLibre()` | ADR-041 |

### 2.5.1 Posiciones de consolidación — se reserva, no se pide y ya (ADR-060)

La regla de orden es explícita en el propio ADR: **capacidad → factibilidad →
costo → reserva → asignación** — nunca se elige primero por costo para
recién ahí fijarse si hay lugar para armar el contenedor. Cada sitio ofrece
`contenedores_por_dia` como techo diario; `reservarParcialPedido()` toma las
posiciones más tempranas dentro de la ventana del pedido, y si el sitio se
queda sin cupo esa alternativa deja de ser factible **antes** de competir por
costo con las demás.

### 2.5.2 Flota de camiones — un viaje puede durar más de un día (ADR-061)

Este es el matiz que más cambia la intuición "de manual": la transferencia
planta→depósito **no es instantánea**. `programarMovimientoProducto()`
reparte el volumen en viajes de hasta `capacidad_camion_tn`, cada viaje toma
el camión disponible más temprano, y ese camión queda ocupado desde la
salida hasta el **regreso** a su base — un viaje de 1.200 km con jornada de
10 horas ocupa el camión 3,43 días. Mientras el camión no vuelve, no está
disponible para otro viaje, así que la "capacidad operativa" de este recurso
no es un número fijo por día: depende de qué tan lejos está el destino que se
eligió.

`evaluarDisponibilidadFlotaProducto()` simula la asignación **sobre una copia**
de la agenda (sin tocar la real) y devuelve cuánto se puede programar antes
del corte, con qué espera máxima — es lo que reemplaza al simple sí/no de
versiones anteriores del modelo, y lo que permite que `motivoDescarteDeposito`
(documento 01, §1.5) diga *"sin flota antes del cutoff"* en vez de *"sin
flota"* sin más precisión.

### 2.5.3 Por qué esto importa para la elección de circuito

Un depósito puede ser el más barato por tonelada y aun así perder la
comparación si:
- no tiene posiciones de consolidación libres en la ventana del pedido
  (`SIN_CAPACIDAD_ANTES_CUTOFF` — pero igual se **costea** el volumen
  hipotético para medir el sobrecosto de saturación, §2.4.2), o
- no hay camiones que puedan completar el viaje antes del cut-off físico del
  pedido (`SIN_FLOTA_ANTES_CUTOFF` / `SIN_FLOTA_PLANTA_DEPOSITO`).

Es la misma idea del documento 01 (capacidad antes que costo) pero ahora
aplicada no a "¿entra este lote?" sino a "¿puede este circuito completo
—origen, transporte, armado de contenedor— ejecutarse a tiempo?".

## 2.6 Una limitación real — el costo comparado es "hoy", nunca "cuánto sale dejarlo donde está" (ADR-065)

Los §2.4.3-2.4.4 muestran que el evaluador **sí** compara costo entre orígenes — pero ese costo es siempre "cuánto sale despachar *hoy* desde acá", nunca "cuánto le va a costar a esta tonelada seguir parada si elijo el otro origen". Es una asimetría real frente a la lógica de almacenamiento (doc 01, §1.4.2): `seleccionarDeposito()` ya proyecta `tarifaAlmacenamiento × diasEstimadosAlmacenamiento` (30 días, parámetro `Main.diasEstimadosAlmacenamiento`) para decidir a qué depósito transferir desde planta; `costearAlternativa()` no tiene el equivalente para decidir desde qué origen despachar un pedido.

**Ejemplo con datos reales de `datos/entrada_ejemplo.xlsx`** — un pedido con stock disponible en PLANTA y en NORRY, 1 contenedor REEFER_40 (24 tn), política `MENOR_COSTO_INCREMENTAL_FACTIBLE`:

| | Planta | Norry |
|---|---|---|
| Round trip (`TarifaRoundTrip`, Zárate) | 2.700 | 2.700 |
| Consolidación (`TarifaSitio.consolidacion_tarifa`) | 225 | 250 |
| OUT (`TarifaSitio.out_usd_tn` × 24 tn) | 0 | 60 |
| **Costo incremental (hoy)** | **2.925** | **3.010** |

Con el evaluador actual, gana Planta — es realmente más barata *hoy*, y así debería ser en principio. El problema aparece cuando se agrega la otra mitad de la cuenta: si esas 24 tn se quedan en Norry (tarifa de storage 0,48 USD/tn/día) en vez de las de Planta (tarifa de oportunidad 0,25 USD/tn/día), ¿a cuál le sale más caro seguir parada?

```
crédito por 30 días evitados = tarifaHolding(origen) × 30 × toneladas

Planta: 0,25 × 30 × 24 = 180
Norry:  0,48 × 30 × 24 = 345,6

costo ajustado = costo incremental − crédito

Planta: 2.925 − 180    = 2.745
Norry:  3.010 − 345,6  = 2.664,4   ← ahora gana Norry
```

El ranking se da vuelta: aunque despachar desde Norry sale 85 USD más caro hoy, dejarlo ahí acumula bastante más storage futuro (345,6 vs. 180) que dejarlo en Planta — la diferencia de holding termina pesando más que la diferencia de flete/estiba. Es el mecanismo de **ADR-065** (`docs/08_Decisiones/Decisiones_de_Arquitectura.md`), ya implementado en `RedLogistica_Exportacion.alp`/`model_src/` (pendiente de compilar y correr en el IDE de AnyLogic para validarlo empíricamente — ver la nota de implementación al final del ADR): un campo `costoHoldingEvitado` que ajusta sólo el **ranking** de `ordenarAlternativas()`, sin tocar `costoIncremental`/`costoEndToEnd` (que siguen siendo, a propósito, el costo real que se termina cobrando — ADR-052/053).

**Por qué `MENOR_COSTO_END_TO_END_FACTIBLE` no resuelve esto:** esa vista le suma a cada alternativa el histórico ya devengado — para Norry, `costoAlmacenajeHundido` crece cada día que el stock sigue ahí sin despacharse; para Planta, el histórico es siempre 0. Cuanto más tiempo lleva ese jugo en Norry, **más caro** se ve frente a Planta bajo END_TO_END — el efecto es el contrario al que se busca. Por diseño, esa vista es para comparar **estrategias completas**, no para decidir todos los días desde qué origen despachar (ver glosario, "Costo incremental" vs. "Costo end-to-end").

**Con `PRIORIDAD_FRIO_PROPIO` (la política de tu escenario de ejemplo) nada de esto importa:** `ordenarAlternativas()` decide por origen —gana Planta si está disponible— antes de llegar a comparar ningún costo (ver §2.4.4). El crédito de holding sólo tiene efecto bajo las políticas de costo puro.

## 2.7 Afinidad pedido-depósito — cuando el stock ya tiene dueño (ADR-066)

El resto de este documento asume que todos los orígenes con stock compiten en igualdad de condiciones por cada pedido. En la operación real eso no siempre es cierto: parte del stock que está en un depósito lejano ya está ahí porque un pedido o cliente específico lo va a retirar de ese lugar — no está "compitiendo", está reservado en la práctica aunque el modelo no lo sepa.

Sin esa información, el evaluador puede hacerle perder esa competencia contra un origen más barato para otro pedido — y ese stock queda contado como "atrapado" en la contabilidad del modelo (doc `flow/01`, `excedenteFinalTn()`) cuando en la realidad no lo está en absoluto.

**La solución no es un crédito de costo — es un dato que hoy falta.** `PedidoPlan` gana una columna opcional, `deposito_comprometido`, que se copia al pedido en `crearPedido()`:

```java
pedido.depositoComprometido = plan.depositoComprometido;   // vacío por defecto
```

Y `ordenarAlternativas()` gana un criterio de desempate nuevo, entre servicio y frío propio:

```java
if (depositoComprometido != null && !depositoComprometido.isEmpty()) {

    boolean ca = depositoComprometido.equals(a.idOrigen);
    boolean cb = depositoComprometido.equals(b.idOrigen);

    if (ca != cb) {
        return ca ? -1 : 1;
    }
}

if (frioPropio) {
    ...
}
```

Es el mismo mecanismo que ya usa `PRIORIDAD_FRIO_PROPIO` para preferir planta (§2.4.4) — sólo que acá el origen "ganador" no es fijo (siempre planta), es el que cada pedido trae consigo. El orden final de criterios queda: **servicio → compromiso → frío propio → costo**.

**Por qué no distorsiona nada cuando no se usa:** con `deposito_comprometido` vacío (el caso de todo pedido en un libro que no complete esa columna), `depositoComprometido.isEmpty()` es siempre verdadero y el criterio nunca decide nada — el comportamiento es exactamente el de antes de ADR-066. Y si el depósito comprometido no tiene stock ese día (no es factible), tampoco pasa nada especial: simplemente no aparece entre las alternativas factibles, y el pedido compite normalmente por las demás — no hace falta ningún manejo de "fallback", es una consecuencia de cómo ya funciona el filtro de factibilidad (§2.4.2).

### 2.7.1 Material — la afinidad que sí bloquea, no sólo desempata (ADR-067)

`deposito_comprometido` (§2.7) es un desempate: si el origen comprometido no tiene stock ese día, el pedido compite normalmente por los demás, sin que nada se lo impida. El material es distinto — **no desempata, filtra.** Un pedido de `JUGO`/`JCCL` nunca puede reservar ni despachar una capa de `JUGO`/`JCL`, sea cual sea el costo o el desempate, porque no son el mismo material y en la realidad no son intercambiables.

Esto significa que `alternativaPara()` (§2.4.1) ya no calcula "cuánto de este producto hay libre en este origen" — calcula cuánto **de este material** hay libre: si Ruta9 tiene 500 tn de `JCL` y el pedido pide `JCCL`, la alternativa por Ruta9 tiene 0 tn disponibles, aunque el depósito esté lleno de producto del mismo tipo. `reservarParcialPedido()`, `evaluarAlternativa()` y el costo hundido de ADR-065 (`toneladaDiaEnStock()`) siguen la misma regla: todos filtran por `pedido.material`, no sólo por `pedido.producto`.

**Consecuencia directa: el nivel de servicio proyectado puede bajar** en un escenario donde la producción de un material va por detrás de sus pedidos, aunque sobre stock de otro material del mismo producto — antes de ADR-067 el modelo prestaba entre materiales sin que la operación real lo permitiera, y eso ocultaba un déficit genuino. No es una regresión: es la corrección de un préstamo que nunca debió existir.

## 2.8 La ventana marítima — el reloj que corre en paralelo (ADR-059)

El pedido no vive en una fecha sino en cuatro: `dia_conocimiento` →
`dia_apertura_retiro_vacio` → `dia_cutoff_fisico` → `dia_etd`. Esto afecta la
elección de circuito de dos formas concretas:

- Entre **conocimiento** y **apertura**, el pedido ya puede reservar
  inventario (cuenta para el componente de servicio del documento 01, §1.4.1)
  pero sus contenedores quedan en `CREADO` — no hay movimiento físico
  todavía.
- `alternativa.diaEntregaEstimado` se calcula sumando la espera hasta la
  apertura, la espera de flota y el ciclo físico del circuito
  (`horasCicloAlternativa`), y `llegaATiempo` compara eso contra
  `dia_cutoff_fisico`. Si el escenario exige servicio mínimo
  (`servicioMinimoProyectado > 0`), este booleano es el **primer** criterio
  de `ordenarAlternativas` (§2.4.4) — antes que cualquier costo.

## 2.9 Ejemplo numérico — un pedido, tres alternativas, una elegida

Pedido ilustrativo: **P-104**, JUGO, 200 tn pendientes, política
`MENOR_COSTO_INCREMENTAL_FACTIBLE`, sin exigencia de servicio mínimo.
`generarAlternativas` devuelve (entre otras) estas tres ya evaluadas como
factibles:

| Alternativa | Origen | Circuito | Costo incremental total (USD) | Toneladas que puede tomar | Costo unitario (USD/tn) |
|---|---|---|---|---|---|
| A | PLANTA | `CONSOLIDACION_PLANTA` | 14.000 | 200 | **70,0** |
| B | DEP_SUR | `CONSOLIDACION_DEPOSITO` | 12.800 | 150 (le queda espacio de estiba sólo para 150 hoy) | 85,3 |
| C | DEP_NORTE | `CROSS_DOCK_DEPOSITO` | 15.600 | 200 | 78,0 |

`ordenarAlternativas`:
1. Sin exigencia de servicio, el paso 1 no desempata nada.
2. La política no es `PRIORIDAD_FRIO_PROPIO`, el paso 2 tampoco aplica.
3. Se compara **costo unitario**: A (70,0) < C (78,0) < B (85,3).
4. → **Gana A** (consolidar directo en planta).

`ejecutarAlternativa(A)` llama a `reservarParcialPedido(pedido, "PLANTA", 200, ...)`.
Si en el instante de ejecutar sólo quedan 180 tn libres en planta (otro
pedido se llevó 20 tn en la misma vuelta), la reserva real es 180 —no las
200 evaluadas—, el plan registra "elegida: A, tomadas: 180", y las 20 tn
restantes vuelven al bucle de `asignarConEvaluador` para una **segunda
vuelta**, donde `generarAlternativas` se llama de nuevo con el pedido ya
parcialmente cubierto: ahí compiten de nuevo A (con lo que le quede a
planta), B y C, y el ranking puede cambiar.

Nótese que **B**, aunque más caro por tonelada, seguía siendo una alternativa
viable — de hecho, si A hubiera tenido lugar sólo para 50 tn, B habría sido
la que cubre el resto en la misma vuelta o en la siguiente, porque el
evaluador no elige "la más barata en general", elige la más barata **entre
las que pueden ejecutarse hoy con lo que queda de saldo**.

## 2.10 Resumen — árbol de decisión de la entrega

```
¿Política del escenario?
   │
   ├─ FIJA_* / MANUAL ──────────────────────────► asignarConPoliticaFija()
   │                                                 orden cableado + reserva parcial
   │                                                 └─ si no alcanza y hay permiso → cae al evaluador
   │
   └─ económica (evaluador) ────────────────────► asignarConEvaluador()  [se repite por vueltas]
            │
            ▼
      generarAlternativas(pedido)   → enumera TODOS los circuitos posibles
            │
            ▼
      evaluarAlternativa()   → factibilidad ANTES que costo:
            │                   posiciones → flota → stock → (cross dock: cupo/espacio) → estiba
            ▼
      costearAlternativa() + costearHundidoAlternativa()
            │                   incremental / histórico / end-to-end, por componente
            ▼
      ordenarAlternativas()   → servicio (si se exige) → compromiso (si el pedido trae uno,
            │                   ADR-066) → frío propio (si aplica) → costo unitario ajustado
            │                   por holding evitado (ADR-065) → clave
            │
            ▼
      ejecutarAlternativa()   → reserva real (puede ser menos que lo evaluado)
            │
            ▼
      registrarPlan()   → PlanLogistico con todo lo evaluado y descartado
            │
            ▼
      ¿queda saldo pendiente?  → sí: nueva vuelta (hasta 2+2×depósitos)
                                → no: pedido cubierto (total o parcialmente hoy)
```

**En una frase:** la entrega de un pedido no es "elegir un depósito", es
generar el universo completo de circuitos físicos posibles, descartar por
capacidad operativa real (posiciones, flota, cupos, stock) antes de mirar un
solo número de costo, comparar lo que sobrevive por costo por tonelada
ajustado por el holding que evita —con servicio, compromiso y frío propio
como criterios previos cuando corresponden— y ejecutar aceptando que la
realidad del día puede dar menos de lo que el cálculo prometió, sin que eso
se pierda: el saldo compite de nuevo. Y lo que ninguna de estas reglas
resuelve —stock atascado en un depósito sin capacidad de despacho— lo cubre
un mecanismo aparte, del lado de la oferta, no del pedido (`flow/01`, §1.9).
