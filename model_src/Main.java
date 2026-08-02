// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Main extends Agent {

    // ----- Parámetros -----
    OrigenDatos origenDatos = OrigenDatos.SINTETICO;
    String rutaExcel = "datos/entrada_ejemplo.xlsx";
    String idEscenario = "E-00";
    long semillaBase = 1;
    double diasEstimadosAlmacenamiento = 30;
    int replica = 0;
    boolean debugPlanificacion = false;

    // ----- Variables -----
    DatosEntrada datos = null;
    int siguienteIdLote = 1;
    double costoFletePlantaDeposito = 0;
    double toneladasTransferidasDepositos = 0;
    double cantidadTransferenciasDepositos = 0;
    int siguienteIdPedido = 1;
    int pedidosRecibidos = 0;
    int pedidosReservados = 0;
    double pedidosPendientes = 0;
    double toneladasSolicitadasAcumuladas = 0;
    double toneladasReservadasAcumuladas = 0;
    int siguienteIdEnvio = 1;
    boolean enviosGenerados = false;
    double costoFleteDepositoPuertoReal = 0;
    double costoConsolidacionReal = 0;
    Inventario inventario = new Inventario();
    java.util.HashMap<String, Integer> consolidacionesDelDia = new java.util.HashMap<String, Integer>();
    int contenedoresEnEspera = 0;
    double esperaConsolidacionContenedorDia = 0;
    int consolidacionesRealizadas = 0;
    double capacidadConsolidacionOfrecida = 0;
    java.util.HashMap<String, Integer> crossDockDelDia = new java.util.HashMap<String, Integer>();
    double capacidadCrossDockOfrecida = 0;
    int operacionesCrossDock = 0;
    double toneladasCrossDock = 0;
    double costoCrossDockReal = 0;
    int crossDockReprogramados = 0;
    int crossDockDegradados = 0;
    int duracionCampaniaDias = 183;
    EstrategiaLogistica estrategiaConsolidacion = EstrategiaLogistica.CONSOLIDACION_DEPOSITO;
    boolean habilitaCrossDock = false;
    double camionDiaOcupado = 0;
    double camionDiaOfrecido = 0;
    double flotaProductoOfrecidaHoy = 0;
    double flotaProductoUsadaHoy = 0;
    int viajesPlantaDeposito = 0;
    double tonDiaSobreNominalPlanta = 0;
    double tonDiaSobreCriticoPlanta = 0;
    int diasSobrecargaPlanta = 0;
    double picoOcupacionPlantaPct = 0;
    double costoOportunidadFrio = 0;
    double costoPenalidadSobrecarga = 0;
    double toneladasTransferidasPreventivas = 0;
    int viajesGranelTerminal = 0;
    int contenedoresCircuitoPlanta = 0;
    int contenedoresCircuitoDeposito = 0;
    int contenedoresCircuitoCrossDock = 0;
    int contenedoresCircuitoTerminal = 0;
    RegistroCostos registro = new RegistroCostos();
    double costoInDeposito = 0;
    double costoOutDeposito = 0;
    double costoThcReal = 0;
    double costoTerminalReal = 0;
    double costoDespachanteReal = 0;
    double costoEsperaCamionProducto = 0;
    double costoEsperaPortacontenedor = 0;
    double costoFleteGranelTerminal = 0;
    DatosEntrada.PoliticaSeleccion politicaSeleccion = DatosEntrada.PoliticaSeleccion.FIJA_DEPOSITO;
    int siguienteIdPlan = 1;
    int planesEmitidos = 0;
    int planesTardios = 0;
    int alternativasEvaluadasTotal = 0;
    int alternativasDescartadasTotal = 0;
    int pedidosSinAlternativaFactible = 0;
    int siguienteIdAsignacion = 1;
    AsignacionPedido ultimaAsignacionCreada = null;
    int asignacionesCreadas = 0;
    int asignacionesParciales = 0;
    int pedidosMultiOrigen = 0;
    int pedidosAtrasadosConEntregaParcial = 0;
    double toneladasTransferidasDesborde = 0;
    double toneladasTransferidasServicio = 0;
    double toneladasTransferidasCriticas = 0;
    int transferenciasIncompletas = 0;
    double componentePorDesborde = 0;
    double componentePorServicio = 0;
    double componentePreventivo = 0;
    double stockInicialCargadoTn = 0;
    double toneladasEntregadasAntesCutoff = 0;
    double toneladasEntregadasFueraCutoff = 0;
    int pedidosPerdieronCutoff = 0;
    int pedidosVentanaInviable = 0;
    int contenedoresSinPosicionFutura = 0;
    double holguraAcumuladaDias = 0;
    int pedidosConHolguraMedida = 0;
    LinkedHashMap<String, LinkedHashMap<Integer, Integer>> posicionesPlanificadas = new LinkedHashMap<String, LinkedHashMap<Integer, Integer>>();

    // ----- Colecciones -----
    ArrayList<Terminal> terminales = new ArrayList<Terminal>();
    ArrayList<Deposito> depositos = new ArrayList<Deposito>();

    // ----- Objetos embebidos (poblaciones y bloques de flowchart) -----
    //  planta
    //  lotes
    //  planes
    //  depFrinoa
    //  depNorry
    //  depBoreas
    //  depRuta9
    //  depDodero
    //  terminalZarate
    //  terminalT4
    //  pedidos
    //  envios
    //  flotaPortacontenedores
    //  colaCamiones
    //  tomarCamion
    //  seleccionarCircuito
    //  viajarVacioAlOrigen
    //  cargarGranel
    //  viajarTerminalGranel
    //  descargarTerminal
    //  cargarCamion
    //  viajarPuerto
    //  descargarPuerto
    //  consolidarCarga
    //  retornarDeposito
    //  liberarCamion
    //  salidaEnvios
    //  entradaEnvios
    //  contenedoresExportacion

    // ----- Funciones -----

    LoteProducto crearLoteEnPlanta(TipoProducto producto, double toneladas, Planta origen) {
        if (toneladas <= 0) {
            return null;
        }

        // Lote comercial acumulativo (ADR-047): la produccion diaria entra como una capa
        // nueva del mismo lote abierto; solo se abre una identidad comercial nueva cuando
        // el lote compatible ya esta cerrado por haber alcanzado su objetivo.
        String cliente = datos.escenario.clienteDefault;
        String calidad = datos.escenario.calidadDefault;

        LoteProducto lote = buscarLoteComercialAbierto(producto, cliente, calidad);

        if (lote == null) {
            lote = add_lotes();

            lote.idLote = siguienteIdLote;
            siguienteIdLote++;

            lote.producto = producto;
            lote.diaProduccion = time();
            lote.estado = EstadoLote.EN_PLANTA;
            lote.ubicacionActual = origen;
            lote.costoAcumulado = 0;
            lote.pedidoAsignado = null;

            lote.cliente = cliente;
            lote.calidad = calidad;
            lote.toneladasObjetivo = datos.producto(producto).toneladasObjetivoLoteTn;
            lote.estadoComercial = EstadoComercialLote.ABIERTO;
            lote.toneladasIniciales = 0;
        }

        // La produccion del dia es una capa nueva con el mismo idLote. El saldo fisico vive
        // en las capas (ADR-023); la identidad comercial vive en el lote.
        inventario.ingresar(
            lote.idLote,
            producto,
            "PLANTA",
            toneladas,
            time(),
            time()
        );

        // 'toneladasIniciales' pasa a ser lo producido acumulado del lote comercial (no del
        // dia): es la base de la regla de cierre, no un saldo fisico.
        lote.toneladasIniciales += toneladas;

        // Regla de cierre (ADR-047): el lote se cierra al alcanzar sus toneladas objetivo.
        // El despacho parcial no lo cierra; la produccion posterior sigue sumando mientras
        // este abierto. Objetivo 0 = sin cierre por tamano (solo cierra al fin de campania).
        if (
            lote.toneladasObjetivo > 0
            && lote.toneladasIniciales >= lote.toneladasObjetivo - 0.0001
        ) {
            lote.estadoComercial = EstadoComercialLote.CERRADO;
        }

        return lote;
    }

    double toneladasLotesEnPlanta(TipoProducto producto) {
        return inventario.stock("PLANTA", producto);
    }

    int cantidadLotesEnPlanta(TipoProducto producto) {
        return inventario.cantidadLotes("PLANTA", producto);
    }

    double calcularCostoPlantaDeposito(Deposito deposito, TipoProducto producto, double toneladas) {
        if (deposito == null || toneladas <= 0) {
            return Double.POSITIVE_INFINITY;
        }

        // El flete a granel se cobra por viaje: mover el doble de toneladas cuesta dos veces
        // el viaje aunque el ultimo camion vaya a medio cargar. La tarifa, su unidad y su
        // vigencia son dato de la tabla y no una formula cableada (ADR-051).
        return datos.importeFlete(
            diaCampania(),
            "PLANTA",
            deposito.idUbicacion,
            producto,
            toneladas,
            viajesNecesariosCamion(toneladas)
        );
    }

    Deposito seleccionarDeposito(TipoProducto producto, double toneladas, boolean priorizarEspacio, java.util.Set<String> excluidos) {
        // Con la planta en sobrecarga critica el criterio deja de ser el costo y pasa a ser que
        // el producto salga: gana el destino donde entra mas, y el costo desempata (ADR-056). El
        // volumen a transferir no cambia por estar en sobrecarga; cambia el orden de los destinos.
        Deposito mejorDeposito = null;
        double menorCostoPorTonelada = Double.POSITIVE_INFINITY;
        double mayorEspacio = 0;

        for (Deposito deposito : depositos) {

            if (excluidos != null && excluidos.contains(deposito.idUbicacion)) {
                continue;
            }

            if (!motivoDescarteDeposito(deposito, producto, 0).isEmpty()) {
                continue;
            }

            // Ya no hace falta que entre todo: alcanza con que entre una parte, y se compara
            // por costo unitario para que el criterio no dependa de cuanto entra en cada uno.
            double posible = min(
                toneladas,
                deposito.getEspacioDisponible(producto)
            );

            if (posible <= 0.0001) {
                continue;
            }

            double costoEstimado =
                calcularCostoPlantaDeposito(deposito, producto, posible)
                + posible
                * deposito.getTarifaAlmacenamiento(producto)
                * diasEstimadosAlmacenamiento;

            double costoPorTonelada = costoEstimado / posible;

            boolean mejor =
                priorizarEspacio
                ? (
                    posible > mayorEspacio + 0.0001
                    || (
                        abs(posible - mayorEspacio) <= 0.0001
                        && costoPorTonelada < menorCostoPorTonelada
                    )
                )
                : costoPorTonelada < menorCostoPorTonelada;

            if (mejor) {
                mayorEspacio = posible;
                menorCostoPorTonelada = costoPorTonelada;
                mejorDeposito = deposito;
            }
        }

        return mejorDeposito;
    }

    LoteProducto buscarLoteMasAntiguoEnPlanta(TipoProducto producto) {
        LoteProducto seleccionado = null;
        double menorDia = Double.POSITIVE_INFINITY;

        for (LoteProducto lote : lotes) {

            // El criterio es el saldo real en planta: un lote transferido a medias sigue
            // teniendo capas ahi y tiene que poder terminar de salir.
            if (
                lote.producto == producto
                && inventario.libreDeLoteEn(lote.idLote, "PLANTA") > 0.0001
            ) {
                if (lote.diaProduccion < menorDia) {
                    menorDia = lote.diaProduccion;
                    seleccionado = lote;
                }
            }
        }

        return seleccionado;
    }

    double transferirProductoADepositos(TipoProducto producto, double toneladasObjetivo, boolean priorizarEspacio) {
        // El objetivo se reparte entre todos los depositos que puedan recibir: que en el primero
        // entren 100 de 300 no termina la transferencia, sigue con el siguiente (ADR-056). Un
        // destino que no pudo recibir queda descartado por hoy y no vuelve a intentarse, asi el
        // recorrido termina siempre.
        double pendiente = toneladasObjetivo;

        java.util.Set<String> agotados =
            new java.util.HashSet<String>();

        double movidasTotal = 0;

        while (pendiente > 0.0001) {

            LoteProducto lote =
                buscarLoteMasAntiguoEnPlanta(producto);

            if (lote == null) {
                break;
            }

            double aMover = min(
                inventario.libreDeLoteEn(lote.idLote, "PLANTA"),
                pendiente
            );

            Deposito destino =
                seleccionarDeposito(producto, aMover, priorizarEspacio, agotados);

            if (destino == null) {
                break;
            }

            // Si en el deposito elegido no entra todo, se manda lo que entra y el resto sale
            // en la vuelta siguiente, a otro deposito.
            double movidas =
                transferirToneladasLote(lote, destino, aMover, false);

            if (movidas <= 0.0001) {
                agotados.add(destino.idUbicacion);
                continue;
            }

            if (movidas + 0.0001 < aMover) {
                agotados.add(destino.idUbicacion);
            }

            pendiente -= movidas;
            movidasTotal += movidas;
        }

        if (pendiente > 0.0001) {

            transferenciasIncompletas++;

            if (debugPlanificacion) {
                traceln(
                    "[dia " + (int) floor(time()) + "] " + producto
                    + " quedaron sin destino " + Math.round(pendiente) + " tn"
                );
                traceln(diagnosticoDepositos(producto, pendiente));
            }
        }

        return movidasTotal;
    }

    void revisarTransferenciasPlanta() {
        // Politica de frio propio (ADR-048, ADR-056). El frio de la planta es propio y no se
        // factura, asi que el default es retener: el producto sale por desborde proyectado, por
        // servicio o por prevencion. La politica REACTIVA conserva la regla anterior (vaciar la
        // planta al llegar al umbral de alerta) y queda como escenario de comparacion.
        boolean flexible =
            "FLEXIBLE".equals(datos.escenario.politicaFrioPropio);

        for (TipoProducto producto : TipoProducto.values()) {

            double toneladas = flexible
                ? toneladasASacarDePlanta(producto)
                : toneladasASacarReactiva(producto);

            boolean critica = plantaEnSobrecargaCritica(producto);

            if (debugPlanificacion && (toneladas > 0.0001 || critica)) {

                traceln(
                    "[dia " + (int) floor(time()) + "] " + producto
                    + " politica=" + datos.escenario.politicaFrioPropio
                    + " stock=" + Math.round(planta.getStock(producto))
                    + " libre=" + Math.round(inventario.libre("PLANTA", producto))
                    + " forecast="
                    + Math.round(forecastProduccion(producto, datos.escenario.diasForecast))
                    + " capacidad=" + Math.round(planta.getCapacidad(producto))
                    + " desborde=" + Math.round(componentePorDesborde)
                    + " servicio=" + Math.round(componentePorServicio)
                    + " preventiva=" + Math.round(componentePreventivo)
                    + " critica=" + (critica ? "si" : "no")
                    + " objetivo=" + Math.round(toneladas)
                );

                traceln(diagnosticoDepositos(producto, toneladas));
            }

            if (toneladas <= 0.0001) {
                continue;
            }

            double movidas =
                transferirProductoADepositos(producto, toneladas, critica);

            if (debugPlanificacion) {
                traceln(
                    "[dia " + (int) floor(time()) + "] " + producto
                    + " transferido=" + Math.round(movidas)
                    + " de " + Math.round(toneladas)
                );
            }

            if (critica) {
                toneladasTransferidasCriticas += movidas;
            } else if (componentePorDesborde >= max(componentePorServicio, componentePreventivo)) {
                toneladasTransferidasDesborde += movidas;
            } else if (componentePorServicio >= componentePreventivo) {
                toneladasTransferidasServicio += movidas;
            } else {
                toneladasTransferidasPreventivas += movidas;
            }
        }
    }

    double stockTotalDepositos(TipoProducto producto) {
        double total = 0;

        for (Deposito deposito : depositos) {
            total += deposito.getStock(producto);
        }

        return total;
    }

    double toneladasLotesEnDepositos(TipoProducto producto) {
        double total = 0;

        for (Deposito deposito : depositos) {
            total += inventario.stock(deposito.idUbicacion, producto);
        }

        return total;
    }

    double getCostoAlmacenamientoTotal() {
        double total = 0;

        for (Deposito deposito : depositos) {
            total += deposito.costoAlmacenamientoAcumulado;
        }

        return total;
    }

    Pedido crearPedido(DatosEntrada.PedidoPlan plan, Terminal puerto) {
        String codigo = plan.codigoPedido;
        TipoProducto producto = plan.producto;
        double toneladas = plan.toneladasSolicitadas;

        if (
            toneladas <= 0
            || puerto == null
            || !puerto.habilitada
        ) {
            return null;
        }

        Pedido pedido = add_pedidos();

        pedido.idPedido = siguienteIdPedido;
        siguienteIdPedido++;

        pedido.codigoPedido = codigo;
        pedido.producto = producto;
        pedido.toneladasSolicitadas = toneladas;
        pedido.toneladasReservadas = 0;
        pedido.toneladasEntregadas = 0;

        pedido.diaLlegada = time();
        pedido.diaReserva = -1;
        pedido.diaEntrega = -1;

        // Ventana maritima (ADR-059). El cut-off fisico es la fuente de verdad de la
        // fecha limite: diaLimite y fechaLimiteTerminal son la misma fecha vista con
        // dos nombres, para no partir en dos lo que el modelo ya compara.
        pedido.diaConocimiento = plan.diaConocimiento;
        pedido.diaAperturaRetiroVacio = plan.diaAperturaRetiroVacio;
        pedido.diaETD = plan.diaETD;
        pedido.fechaLimiteTerminal = plan.diaCutoffFisico;
        pedido.diaLimite = plan.diaCutoffFisico;

        pedido.naviera = plan.naviera;
        pedido.incoterm = plan.incoterm;
        pedido.buque = plan.buque;
        pedido.viajeBuque = plan.viajeBuque;

        // La ventana abre sola el dia que corresponde; un pedido que nace con la
        // ventana ya abierta no espera un dia de mas.
        pedido.ventanaRetiroAbierta = time() >= plan.diaAperturaRetiroVacio;
        pedido.perdioCutoff = false;

        pedido.puertoSalida = puerto;

        // El campo estaba declarado y sin usar: la terminal de destino es la misma
        // que la de salida del circuito, no un dato aparte (ADR-059).
        pedido.terminalDestino = puerto;

        pedido.depositoAsignado = null;
        pedido.idSitioOrigen = "";

        pedido.estado = EstadoPedido.PENDIENTE;

        pedido.costoFleteEstimado = 0;
        pedido.costoConsolidadoEstimado = 0;
        pedido.costoTotalEstimado = 0;

        pedido.diasAtraso = 0;

        pedidosRecibidos++;
        pedidosPendientes++;
        toneladasSolicitadasAcumuladas += toneladas;

        return pedido;
    }

    boolean intentarAsignarPedido(Pedido pedido) {
        // Un pedido sigue siendo asignable mientras le quede saldo: parcialmente reservado o
        // parcialmente en preparacion, la diferencia sigue siendo demanda (ADR-055).
        if (pedido == null || pedido.toneladasPendientesAsignar() <= 0.0001) {
            return false;
        }

        if (
            pedido.estado == EstadoPedido.ENTREGADO
            || pedido.estado == EstadoPedido.CANCELADO
        ) {
            return false;
        }

        return asignarParcialPedido(pedido) > 0.0001;
    }

    int contarPedidos(EstadoPedido estadoBuscado) {
        int cantidad = 0;

        for (Pedido pedido : pedidos) {

            if (pedido.estado == estadoBuscado) {
                cantidad++;
            }
        }

        return cantidad;
    }

    double stockReservadoDepositos(TipoProducto producto) {
        double total = 0;

        for (Deposito deposito : depositos) {

            double reservado =
                deposito.getReservado(producto);

            total += reservado;
        }

        return total;
    }

    double toneladasReservadasEnLotes(TipoProducto producto) {
        return inventario.reservadoProducto(producto);
    }

    Envio crearEnvio(Pedido pedido, AsignacionPedido asignacion, double toneladas) {
        // El envio es de una asignacion, no del pedido: de ahi salen el origen, el circuito y la
        // clave de reserva contra la que se va a despachar (ADR-055).
        if (
            pedido == null
            || asignacion == null
            || asignacion.idSitioOrigen.isEmpty()
            || pedido.puertoSalida == null
            || toneladas <= 0
        ) {
            return null;
        }

        Envio envio = add_envios();

        envio.idEnvio = siguienteIdEnvio;
        siguienteIdEnvio++;

        envio.codigoEnvio =
            pedido.codigoPedido
            + "-E"
            + envio.idEnvio;

        envio.pedido = pedido;

        envio.idAsignacionPedido = asignacion.idAsignacion;
        envio.claveReserva = asignacion.claveReserva();
        envio.esCrossDock = asignacion.esCrossDock;

        envio.idSitioOrigen =
            asignacion.idSitioOrigen;

        // Queda nulo cuando el pedido sale del frio propio: el deposito es un caso del
        // origen y no el origen (ADR-050).
        envio.depositoOrigen =
            buscarDeposito(asignacion.idSitioOrigen);

        envio.circuito =
            asignacion.circuito;

        envio.terminalDestino =
            pedido.puertoSalida;

        envio.producto =
            pedido.producto;

        envio.toneladas =
            toneladas;

        envio.estado =
            EstadoEnvio.ESPERANDO_CAMION;

        envio.diaCreacion =
            time();

        DatosEntrada.Ubicacion origen =
            datos.ubicacion(envio.idSitioOrigen);

        double distancia =
            datos.distanciaKm(
                envio.idSitioOrigen,
                envio.terminalDestino.idUbicacion
            );

        double velocidadCamion = datos.escenario.velocidadCamionKmh;

        boolean conPortacontenedor =
            usaPortacontenedor(envio);

        // Circuitos 1 a 3: el portacontenedor sale vacio de la terminal, se estiba en el
        // origen y vuelve cargado. Circuito 4: el producto viaja a granel en un camion de
        // la flota de producto y el contenedor se arma en la terminal.
        envio.tiempoViajeVacioHoras =
            conPortacontenedor
            ? distancia / velocidadCamion
            : 0;

        envio.tiempoCargaHoras =
            conPortacontenedor
            ? toneladas
                / origen.velocidadConsolidacionTnHora
            : toneladas
                / origen.velocidadCargaTnHora;

        envio.tiempoViajeIdaHoras =
            distancia / velocidadCamion;

        envio.tiempoDescargaHoras =
            toneladas
            / envio.terminalDestino
                .velocidadDescargaTnHora;

        envio.tiempoConsolidacionHoras =
            conPortacontenedor
            ? 0
            : toneladas
                / envio.terminalDestino
                    .velocidadConsolidacionTnHora;

        envio.tiempoRetornoHoras =
            conPortacontenedor
            ? distancia / velocidadCamion
            : 0;

        // El ciclo del portacontenedor se cotiza por contenedor y por circuito, no por
        // kilometro: la tarifa cubre terminal -> origen -> terminal (ADR-051). El circuito de
        // terminal no usa portacontenedor: el producto viaja a granel y lo que paga es ese
        // flete, no un ciclo que nunca ocurre (ADR-053).
        envio.costoFleteReal =
            conPortacontenedor
            ? datos.roundTripUsdContenedor(
                diaCampania(),
                envio.terminalDestino.idUbicacion,
                envio.idSitioOrigen,
                pedido.tipoContenedor
            )
            : datos.importeFlete(
                diaCampania(),
                envio.idSitioOrigen,
                envio.terminalDestino.idUbicacion,
                envio.producto,
                toneladas,
                viajesNecesariosCamion(toneladas)
            );

        envio.costoConsolidacionReal =
            importeServicioEstiba(
                sitioEstiba(
                    envio.idSitioOrigen,
                    envio.circuito,
                    envio.terminalDestino
                ),
                envio.esCrossDock,
                envio.producto,
                toneladas,
                contenedoresNecesarios(pedido.producto, toneladas)
            );

        envio.costoTotalReal =
            envio.costoFleteReal
            + envio.costoConsolidacionReal;

        pedido.cantidadEnvios++;

        entradaEnvios.take(envio);

        return envio;
    }

    boolean retirarReservaParaEnvio(Envio envio) {
        // El producto sale contra la reserva de su asignacion y no contra el codigo del pedido:
        // dos asignaciones del mismo pedido en el mismo sitio no se pisan (ADR-055).
        if (
            envio == null
            || envio.pedido == null
            || envio.idSitioOrigen.isEmpty()
            || envio.claveReserva.isEmpty()
            || envio.toneladas <= 0
        ) {
            return false;
        }

        Pedido pedido = envio.pedido;

        AsignacionPedido asignacion = asignacionDeEnvio(envio);

        if (asignacion == null) {
            return false;
        }

        double reservado =
            inventario.reservadoClaveEn(
                envio.idSitioOrigen,
                envio.producto,
                envio.claveReserva
            );

        if (reservado + 0.0001 < envio.toneladas) {
            return false;
        }

        // Sale exactamente lo que esta asignacion tenia reservado, de las capas mas
        // antiguas primero.
        double despachadas =
            inventario.despachar(
                envio.idSitioOrigen,
                envio.producto,
                envio.toneladas,
                envio.claveReserva
            );

        if (despachadas + 0.0001 < envio.toneladas) {
            return false;
        }

        actualizarEstadoLotesVacios();

        // El egreso se devenga cuando el producto sale fisicamente del almacenamiento
        // (ADR-053).
        envio.costoCargosReal +=
            registrarOutDeposito(envio);

        asignacion.toneladasReservadasActivas =
            max(0, asignacion.toneladasReservadasActivas - envio.toneladas);

        asignacion.toneladasDespachadas += envio.toneladas;

        if (asignacion.diaPrimerDespacho < 0) {
            asignacion.diaPrimerDespacho = time();
        }

        pedido.toneladasDespachadas +=
            envio.toneladas;

        envio.estado =
            EstadoEnvio.CARGANDO;

        envio.diaInicioCarga =
            time();

        return true;
    }

    int contarEnvios(EstadoEnvio estadoBuscado) {
        int cantidad = 0;

        for (Envio envio : envios) {

            if (envio.estado == estadoBuscado) {
                cantidad++;
            }
        }

        return cantidad;
    }

    double toneladasEnProceso(TipoProducto producto) {
        double total = 0;

        for (Envio envio : envios) {

            if (
                envio.producto == producto
                && envio.estado != EstadoEnvio.CREADO
                && envio.estado != EstadoEnvio.ESPERANDO_CAMION
                && envio.estado != EstadoEnvio.ENTREGADO
                && envio.estado != EstadoEnvio.CANCELADO
            ) {
                total += envio.toneladas;
            }
        }

        return total;
    }

    double toneladasEntregadas(TipoProducto producto) {
        double total = 0;

        for (Envio envio : envios) {

            if (
                envio.producto == producto
                && envio.estado == EstadoEnvio.ENTREGADO
            ) {
                total += envio.toneladas;
            }
        }

        return total;
    }

    TipoContenedor obtenerTipoContenedor(TipoProducto producto) {
        return datos.producto(producto).tipoContenedor;
    }

    double obtenerCapacidadContenedorTon(TipoContenedor tipo) {
        for (DatosEntrada.Producto fila : datos.productos) {

            if (fila.tipoContenedor == tipo) {
                return fila.capacidadContenedorTn;
            }
        }

        error("Capacidad no definida para el contenedor: " + tipo);

        return 0;
    }

    void cargarDatosEntrada() {
        // El origen de los datos no cambia la logica: ambas ramas llenan las mismas
        // tablas y todo el modelo lee 'datos'.
        if (origenDatos == OrigenDatos.EXCEL) {

            datos = ImportadorExcel.importar(
                this,
                rutaExcel,
                idEscenario
            );

        } else {

            // La replica solo mueve la semilla (ADR-042): mismo escenario, otro sorteo.
            datos = GeneradorSintetico.generar(
                idEscenario,
                semillaBase + replica
            );
        }

        // Un dato derivado no es un dato cargado (ADR-059): si el libro no trae las
        // cuatro fechas de la ventana, la corrida sigue pero lo dice.
        for (String aviso : ImportadorExcel.ultimasAdvertencias) {
            traceln("Advertencia de datos: " + aviso);
        }

        List<String> errores = datos.validar();

        if (!errores.isEmpty()) {

            String detalle = "";

            for (String e : errores) {
                detalle += "\n  - " + e;
            }

            error(
                "Datos de entrada invalidos ("
                + errores.size()
                + "):"
                + detalle
            );
        }

        aplicarEscenario();
        aplicarDatosAAgentes();
    }

    void aplicarDatosAAgentes() {
        // Los agentes no guardan datos propios: son una vista de las tablas.
        planta.capacidadJugo =
            datos.capacidadTn("PLANTA", TipoProducto.JUGO);

        planta.capacidadCascara =
            datos.capacidadTn("PLANTA", TipoProducto.CASCARA);

        planta.capacidadAceite =
            datos.capacidadTn("PLANTA", TipoProducto.ACEITE);

        for (Deposito deposito : depositos) {

            DatosEntrada.Ubicacion ubicacion =
                datos.ubicacion(deposito.idUbicacion);

            deposito.habilitado = ubicacion.habilitada;
            deposito.velocidadCargaTnHora = ubicacion.velocidadCargaTnHora;
            deposito.velocidadConsolidacionTnHora = ubicacion.velocidadConsolidacionTnHora;

            deposito.capacidadJugo =
                datos.capacidadTn(deposito.idUbicacion, TipoProducto.JUGO);

            deposito.capacidadCascara =
                datos.capacidadTn(deposito.idUbicacion, TipoProducto.CASCARA);

            deposito.capacidadAceite =
                datos.capacidadTn(deposito.idUbicacion, TipoProducto.ACEITE);

        }

        for (Terminal terminal : terminales) {

            DatosEntrada.Ubicacion ubicacion =
                datos.ubicacion(terminal.idUbicacion);

            terminal.habilitada = ubicacion.habilitada;
            terminal.capacidadDiariaTn = ubicacion.capacidadDiariaTn;
            terminal.velocidadDescargaTnHora = ubicacion.velocidadDescargaTnHora;
            terminal.velocidadConsolidacionTnHora = ubicacion.velocidadConsolidacionTnHora;
        }

        refrescarTarifasDelDia();
    }

    Terminal buscarTerminal(String idUbicacion) {
        for (Terminal terminal : terminales) {

            if (terminal.idUbicacion.equals(idUbicacion)) {
                return terminal;
            }
        }

        return null;
    }

    void producirEnPlantas() {
        // Fase 1 de la secuencia diaria (ADR-034).
        planta.producir();
    }

    void registrarPedidosDelDia() {
        int diaActual = (int) floor(time());

        // La demanda es un dato de entrada (tabla PedidoPlan), no una regla del modelo.
        for (DatosEntrada.PedidoPlan plan : datos.pedidosDelDia(diaActual)) {

            Terminal terminal = buscarTerminal(plan.terminal);

            if (terminal == null) {
                error(
                    "El pedido "
                    + plan.codigoPedido
                    + " referencia la terminal "
                    + plan.terminal
                    + ", que no existe en el modelo."
                );
            }

            crearPedido(plan, terminal);
        }
    }

    void revisarPedidosPendientes() {
        java.util.List<Pedido> pendientes =
            new java.util.ArrayList<Pedido>();

        // Con asignacion parcial la cola no son los pedidos sin reserva sino los que tienen
        // saldo: uno reservado a medias vuelve todos los dias hasta completarse (ADR-055).
        for (Pedido pedido : pedidos) {

            // Conocer el pedido alcanza para reservar: comprometer inventario no es
            // mover un contenedor (ADR-059). Con el permiso apagado, el pedido no
            // toca el inventario hasta que abre su retiro.
            boolean puedeReservar =
                datos.escenario.permiteReservaAntesRetiro
                || pedido.ventanaRetiroAbierta;

            if (
                puedeReservar
                && pedido.estado != EstadoPedido.ENTREGADO
                && pedido.estado != EstadoPedido.CANCELADO
                && pedido.toneladasPendientesAsignar() > 0.0001
            ) {
                pendientes.add(pedido);
            }
        }

        // Con evaluador los pedidos compiten por la misma capacidad, asi que el orden decide:
        // primero el que vence antes, y el empate se rompe por codigo para que la corrida sea
        // reproducible. Sin evaluador se conserva el orden de la poblacion (regresion).
        if (usaEvaluador()) {

            java.util.Collections.sort(
                pendientes,
                new java.util.Comparator<Pedido>() {

                    public int compare(Pedido a, Pedido b) {

                        int orden = Double.compare(a.diaLimite, b.diaLimite);

                        return orden != 0
                            ? orden
                            : a.codigoPedido.compareTo(b.codigoPedido);
                    }
                });
        }

        for (Pedido pedido : pendientes) {
            intentarAsignarPedido(pedido);
        }
    }

    void prepararPedidosReservados() {
        // Se arman los contenedores de cada asignacion activa, no del pedido: una fraccion ya
        // reservada no espera a que el pedido este completo para empezar a viajar (ADR-055).
        for (Pedido pedido : pedidos) {

            if (
                pedido.estado == EstadoPedido.ENTREGADO
                || pedido.estado == EstadoPedido.CANCELADO
                || pedido.asignaciones.isEmpty()
            ) {
                continue;
            }

            int creados = 0;

            for (AsignacionPedido asignacion : pedido.asignaciones) {
                creados += crearContenedoresParaAsignacion(pedido, asignacion);
            }

            if (
                creados > 0
                && pedido.estado != EstadoPedido.ATRASADO
            ) {
                pedido.estado = EstadoPedido.EN_PREPARACION;
            }
        }
    }

    void devengarAlmacenamientoDiario() {
        // Fuente unica del costo de almacenaje del dia (H-04). Se devenga por capa, que
        // es lo que tiene ubicacion y dia de ingreso propios, y se imputa al lote y al
        // deposito en el mismo recorrido, sin doble conteo.
        //
        // Lo comprometido por una asignacion de cross dock no paga almacenaje: cruza el sitio
        // sin ingresar al stock (ADR-010). Se identifica por la clave de la reserva y no por el
        // codigo del pedido, porque el mismo pedido puede cruzar una fraccion y guardar otra
        // (ADR-055).
        java.util.HashSet<String> cruzados =
            new java.util.HashSet<String>();

        for (Pedido pedido : pedidos) {

            for (AsignacionPedido asignacion : pedido.asignaciones) {

                if (asignacion.esCrossDock) {
                    cruzados.add(asignacion.claveReserva());
                }
            }
        }

        for (Capa capa : inventario.capas) {

            Deposito deposito = buscarDeposito(capa.idUbicacion);

            if (deposito == null || capa.toneladas <= 0) {
                continue;
            }

            double facturables = capa.toneladas;

            for (Capa.Reserva reserva : capa.reservas) {

                if (cruzados.contains(reserva.clave)) {
                    facturables -= reserva.toneladas;
                }
            }

            if (facturables <= Capa.EPS) {
                continue;
            }

            DatosEntrada.TarifaSitio tarifa =
                datos.tarifaSitio(diaCampania(), capa.idUbicacion, capa.producto);

            double costoDia = registro.registrar(
                time(),
                RegistroCostos.Categoria.ALMACENAMIENTO,
                RegistroCostos.Tipo.CAJA,
                "", "", "" + capa.idLote, capa.producto,
                capa.idUbicacion, capa.idUbicacion, capa.idUbicacion,
                EstrategiaLogistica.SIN_DEFINIR, tarifa.proveedor,
                DatosEntrada.Unidad.USD_TN_DIA, facturables,
                datos.storageUsdTnDia(diaCampania(), capa.idUbicacion, capa.producto),
                "STO-" + diaCampania() + "-" + capa.idLote + "-" + capa.idUbicacion
                    + "-" + capa.producto + "-" + capa.idCapa,
                "almacenaje del dia"
            );

            capa.costoAlmacenamiento += costoDia;
            deposito.costoAlmacenamientoAcumulado += costoDia;

            LoteProducto lote = buscarLote(capa.idLote);

            if (lote != null) {
                lote.costoAlmacenamientoLote += costoDia;
                lote.costoAcumulado += costoDia;
            }
        }
    }

    void registrarAtrasos() {
        // Un pedido vencido queda atrasado aunque ya tenga parte reservada, contenerizada o
        // entregada: lo que decide es el saldo sin entregar (ADR-055).
        for (Pedido pedido : pedidos) {

            // Marca historica de entrega parcial: se evalua al cierre del dia y no al llegar cada
            // envio, porque un pedido se despacha en varios contenedores y el saldo de media
            // jornada no es un pedido servido a medias.
            if (
                pedido.toneladasEntregadas > 0.0001
                && !pedido.estaCompleto()
                && pedido.estado != EstadoPedido.CANCELADO
            ) {
                pedido.tuvoEntregaParcial = true;
            }

            if (
                time() > pedido.diaLimite
                && pedido.estado != EstadoPedido.ENTREGADO
                && pedido.estado != EstadoPedido.CANCELADO
            ) {
                pedido.diasAtraso =
                    time() - pedido.diaLimite;

                if (pedido.estado != EstadoPedido.ATRASADO) {

                    pedido.estado = EstadoPedido.ATRASADO;

                    if (pedido.toneladasEntregadas > 0.0001) {
                        pedidosAtrasadosConEntregaParcial++;
                    }
                }
            }
        }
    }

    Deposito buscarDeposito(String idUbicacion) {
        for (Deposito deposito : depositos) {

            if (deposito.idUbicacion.equals(idUbicacion)) {
                return deposito;
            }
        }

        return null;
    }

    LoteProducto buscarLote(int idLote) {
        for (LoteProducto lote : lotes) {

            if (lote.idLote == idLote) {
                return lote;
            }
        }

        return null;
    }

    void marcarLotesReservados(Pedido pedido) {
        // El lote sigue siendo uno solo: se marca como reservado cuando ya no le queda
        // saldo libre. La trazabilidad fina esta en las reservas de cada capa.
        for (LoteProducto lote : lotes) {

            if (
                inventario.reservadoDeLotePorPedido(
                    lote.idLote,
                    pedido.codigoPedido
                ) <= 0
            ) {
                continue;
            }

            lote.pedidoAsignado = pedido;
            lote.diaReserva = time();

            if (lote.getToneladasLibres() <= 0.0001) {
                lote.estado = EstadoLote.RESERVADO;
            }
        }
    }

    void actualizarEstadoLotesVacios() {
        for (LoteProducto lote : lotes) {

            if (
                lote.getToneladasDisponibles() > 0.0001
                || (
                    lote.estado != EstadoLote.EN_DEPOSITO
                    && lote.estado != EstadoLote.RESERVADO
                )
            ) {
                continue;
            }

            lote.estado = EstadoLote.EN_TRANSITO_PUERTO;
        }
    }

    void validarInventario() {
        List<String> errores = inventario.validar();

        if (errores.isEmpty()) {
            return;
        }

        String detalle = "";

        for (String e : errores) {
            detalle += "\n  - " + e;
        }

        error(
            "Inventario inconsistente el dia "
            + (int) floor(time())
            + " ("
            + errores.size()
            + "):"
            + detalle
        );
    }

    double transferirToneladasLote(LoteProducto lote, Deposito destino, double toneladas, boolean cruza) {
        // Fase 4: el lote deja de viajar entero. Se mueve lo que se pide, acotado por lo que
        // el lote tiene libre en planta y por el lugar que queda en el deposito.
        // El movimiento no necesita reversion: al acotar antes, mover() retira e ingresa
        // exactamente lo mismo dentro de la misma estructura (ADR-021).
        if (lote == null || destino == null || toneladas <= 0.0001) {
            return 0;
        }

        if (!destino.habilitado) {
            return 0;
        }

        double aMover = min(
            min(toneladas, inventario.libreDeLoteEn(lote.idLote, "PLANTA")),
            destino.getEspacioDisponible(lote.producto)
        );

        if (aMover <= 0.0001) {
            return 0;
        }

        // La flota del dia acota el movimiento: se mueve lo que entra en los viajes que
        // todavia se pueden hacer hoy, y el resto queda en planta (ADR-044).
        double camionDiaPorViaje =
            camionDiaViaje("PLANTA", destino.idUbicacion);

        int viajesPosibles = (int) floor(
            flotaProductoLibreHoy() / camionDiaPorViaje + 0.0001
        );

        if (viajesPosibles <= 0) {
            return 0;
        }

        aMover = min(
            aMover,
            viajesPosibles * datos.escenario.capacidadCamionTn
        );

        double movidas = inventario.moverLote(
            lote.idLote,
            "PLANTA",
            destino.idUbicacion,
            aMover,
            time()
        );

        if (movidas <= 0.0001) {
            return 0;
        }

        tomarFlotaProducto(
            "PLANTA",
            destino.idUbicacion,
            viajesNecesariosCamion(movidas)
        );

        lote.depositoActual = destino;
        lote.diaIngresoDeposito = time();

        actualizarUbicacionLote(lote);

        double costoViaje = registrarFleteProducto(
            "PLANTA",
            destino.idUbicacion,
            lote.producto,
            movidas,
            viajesNecesariosCamion(movidas),
            "" + lote.idLote,
            "",
            EstrategiaLogistica.SIN_DEFINIR,
            "TRA-" + diaCampania() + "-" + lote.idLote + "-" + destino.idUbicacion
                + "-" + cantidadTransferenciasDepositos
        );

        lote.costoAcumulado += costoViaje;
        costoFletePlantaDeposito += costoViaje;

        // El ingreso al almacenamiento lo paga el producto que se queda; el que cruza en cross
        // dock no entra al stock y no lo paga (ADR-053).
        if (!cruza) {

            lote.costoAcumulado += registrarInDeposito(
                destino.idUbicacion,
                lote.producto,
                movidas,
                "" + lote.idLote,
                "",
                "IN-" + diaCampania() + "-" + lote.idLote + "-" + destino.idUbicacion
                    + "-" + cantidadTransferenciasDepositos
            );
        }

        destino.toneladasRecibidasAcumuladas += movidas;
        destino.cantidadRecepciones++;

        toneladasTransferidasDepositos += movidas;
        cantidadTransferenciasDepositos++;

        return movidas;
    }

    void actualizarUbicacionLote(LoteProducto lote) {
        // Un lote transferido a medias esta en dos lugares a la vez, asi que ubicacionActual
        // pasa a ser donde tiene mas saldo. El saldo real siempre se consulta al inventario.
        if (lote == null) {
            return;
        }

        String idUbicacion =
            inventario.ubicacionPrincipalDeLote(lote.idLote);

        if (idUbicacion == null) {
            return;
        }

        if (idUbicacion.equals("PLANTA")) {
            lote.ubicacionActual = planta;

            if (lote.estado == EstadoLote.EN_DEPOSITO) {
                lote.estado = EstadoLote.EN_PLANTA;
            }

            return;
        }

        Deposito deposito = buscarDeposito(idUbicacion);

        if (deposito == null) {
            return;
        }

        lote.ubicacionActual = deposito;

        if (lote.estado == EstadoLote.EN_PLANTA) {
            lote.estado = EstadoLote.EN_DEPOSITO;
        }
    }

    int contarContenedores(EstadoContenedor estadoBuscado) {
        int cantidad = 0;

        for (
            ContenedorExportacion contenedor
            : contenedoresExportacion
        ) {

            if (contenedor.estado == estadoBuscado) {
                cantidad++;
            }
        }

        return cantidad;
    }

    boolean consolidaEnDeposito() {
        return estrategiaConsolidacion
            == EstrategiaLogistica.CONSOLIDACION_DEPOSITO;
    }

    String sitioConsolidacion(ContenedorExportacion contenedor) {
        // Donde se estiba el contenedor: su sitio de origen o la terminal de salida, segun su
        // circuito (ADR-050). Es del contenedor y no del pedido, que puede tener varios (ADR-055).
        return contenedor == null
            ? ""
            : sitioEstiba(
                contenedor.idSitioOrigen,
                contenedor.circuito,
                contenedor.terminalDestino
            );
    }

    void abrirPosicionesConsolidacionDelDia() {
        // Las posiciones son un recurso de conteo diario (definicion, seccion 3): cada
        // dia se abre la capacidad del sitio y lo que no entra espera al dia siguiente.
        consolidacionesDelDia.clear();

        // La capacidad se ofrece donde el escenario arma contenedores: en el circuito de
        // terminal es el puerto y en los demas el sitio de origen. Con consolidacion en
        // planta los depositos siguen ofreciendo capacidad porque el pedido que no entra
        // en el frio propio se sirve desde deposito.
        if (consolidaEnTerminal()) {

            for (Terminal terminal : terminales) {
                capacidadConsolidacionOfrecida +=
                    datos.capacidadConsolidacionDia(
                        terminal.idUbicacion
                    );
            }

            return;
        }

        if (consolidaEnPlanta()) {
            capacidadConsolidacionOfrecida +=
                datos.capacidadConsolidacionDia("PLANTA");
        }

        for (Deposito deposito : depositos) {
            capacidadConsolidacionOfrecida +=
                datos.capacidadConsolidacionDia(
                    deposito.idUbicacion
                );
        }
    }

    boolean tomarPosicionConsolidacion(String idUbicacion) {
        int usadas =
            consolidacionesDelDia.containsKey(idUbicacion)
            ? consolidacionesDelDia.get(idUbicacion)
            : 0;

        if (
            usadas + 1
            > datos.capacidadConsolidacionDia(idUbicacion)
        ) {
            return false;
        }

        consolidacionesDelDia.put(
            idUbicacion,
            usadas + 1
        );

        consolidacionesRealizadas++;

        return true;
    }

    void despacharContenedoresPendientes() {
        java.util.List<ContenedorExportacion> pendientes =
            new java.util.ArrayList<ContenedorExportacion>();

        for (
            ContenedorExportacion contenedor
            : contenedoresExportacion
        ) {
            if (
                contenedor.estado
                == EstadoContenedor.ESPERANDO_PROGRAMACION
            ) {
                pendientes.add(contenedor);
            }
        }

        java.util.Collections.sort(
            pendientes,
            new java.util.Comparator<ContenedorExportacion>() {
                public int compare(
                    ContenedorExportacion a,
                    ContenedorExportacion b
                ) {
                    if (a.esCrossDock != b.esCrossDock) {
                        return a.esCrossDock ? -1 : 1;
                    }

                    int orden =
                        Double.compare(
                            a.Pedido.diaLimite,
                            b.Pedido.diaLimite
                        );

                    if (orden != 0) {
                        return orden;
                    }

                    // Mismo cut-off: primero el que tiene menos holgura, que es el que
                    // deja de llegar si el dia se queda sin recursos (ADR-059).
                    orden =
                        Double.compare(
                            holguraContenedor(a),
                            holguraContenedor(b)
                        );

                    return orden != 0
                        ? orden
                        : a.idContenedor.compareTo(b.idContenedor);
                }
            }
        );

        contenedoresEnEspera = 0;

        for (
            ContenedorExportacion contenedor
            : pendientes
        ) {
            Pedido pedido = contenedor.Pedido;

            // El origen y el circuito son del contenedor, que los hereda de su asignacion: el
            // mismo pedido puede tener contenedores de dos circuitos distintos (ADR-055).
            AsignacionPedido asignacion =
                asignacionDe(pedido, contenedor.idAsignacionPedido);

            if (asignacion == null) {
                error(
                    "El contenedor "
                    + contenedor.idContenedor
                    + " no tiene asignacion viva"
                );
            }

            // Circuito 4: el producto viaja a granel, asi que le consume jornada a la
            // flota de producto y no al pool de portacontenedores (ADR-050).
            boolean granel =
                contenedor.circuito
                == EstrategiaLogistica.CONSOLIDACION_TERMINAL;

            if (
                granel
                && !flotaProductoAlcanza(
                    contenedor.idSitioOrigen,
                    pedido.puertoSalida.idUbicacion,
                    contenedor.cantidadAsignadaTon
                )
            ) {
                contenedor.diasEsperaPosicion++;
                esperaConsolidacionContenedorDia++;
                contenedoresEnEspera++;
                continue;
            }

            if (
                !contenedor.esCrossDock
                && !tomarPosicionConsolidacion(
                    sitioConsolidacion(contenedor)
                )
            ) {
                contenedor.diasEsperaPosicion++;
                esperaConsolidacionContenedorDia++;
                contenedoresEnEspera++;
                continue;
            }

            Envio envio =
                crearEnvio(
                    pedido,
                    asignacion,
                    contenedor.cantidadAsignadaTon
                );

            if (envio == null) {
                error(
                    "No se pudo crear el envio del contenedor "
                    + contenedor.idContenedor
                );
            }

            envio.contenedor = contenedor;

            if (granel) {
                tomarFlotaProducto(
                    contenedor.idSitioOrigen,
                    pedido.puertoSalida.idUbicacion,
                    viajesNecesariosCamion(
                        contenedor.cantidadAsignadaTon
                    )
                );
            }

            contenedor.estado =
                EstadoContenedor.ESPERANDO_CARGA;

            pedido.enviosGenerados =
                pedidoTotalmenteDespachado(pedido);
        }
    }

    boolean pedidoTotalmenteDespachado(Pedido pedido) {
        for (
            ContenedorExportacion contenedor
            : pedido.contenedores
        ) {

            if (
                contenedor.estado
                == EstadoContenedor.ESPERANDO_PROGRAMACION
            ) {
                return false;
            }
        }

        return true;
    }

    void abrirPosicionesCrossDockDelDia() {
        // Las posiciones de cross dock se cuentan por dia, igual que las de
        // consolidacion (ADR-039): una operacion es un contenedor armado con producto
        // que llega y sale el mismo dia.
        crossDockDelDia.clear();

        capacidadCrossDockOfrecida = 0;

        if (!habilitaCrossDock) {
            return;
        }

        for (Deposito deposito : depositos) {

            if (!deposito.habilitado) {
                continue;
            }

            capacidadCrossDockOfrecida +=
                datos.capacidadCrossDockDia(
                    deposito.idUbicacion
                );

            crossDockDelDia.put(
                deposito.idUbicacion,
                0
            );
        }
    }

    double capacidadCrossDockLibre(String idUbicacion) {
        if (!crossDockDelDia.containsKey(idUbicacion)) {
            return 0;
        }

        return datos.capacidadCrossDockDia(idUbicacion)
            - crossDockDelDia.get(idUbicacion);
    }

    boolean tomarPosicionesCrossDock(String idUbicacion, int cantidad) {
        if (capacidadCrossDockLibre(idUbicacion) < cantidad) {
            return false;
        }

        crossDockDelDia.put(
            idUbicacion,
            crossDockDelDia.get(idUbicacion) + cantidad
        );

        operacionesCrossDock += cantidad;

        return true;
    }

    int contenedoresNecesarios(TipoProducto producto, double toneladas) {
        double capacidad =
            obtenerCapacidadContenedorTon(
                obtenerTipoContenedor(producto)
            );

        return (int) ceil(toneladas / capacidad - 0.0001);
    }

    Deposito seleccionarSitioCrossDock(Pedido pedido) {
        // Deposito de cruce mas barato entre los que pueden recibir hoy aunque sea una parte
        // del saldo pendiente: con reserva parcial ya no hace falta que entre todo (ADR-055).
        Deposito mejorSitio = null;
        double menorCostoPorTonelada = Double.POSITIVE_INFINITY;

        double capacidadContenedor =
            obtenerCapacidadContenedorTon(obtenerTipoContenedor(pedido.producto));

        double pendiente =
            min(
                pedido.toneladasPendientesAsignar(),
                inventario.libre("PLANTA", pedido.producto)
            );

        if (pendiente <= 0.0001) {
            return null;
        }

        for (Deposito deposito : depositos) {

            if (!deposito.habilitado) {
                continue;
            }

            double posible =
                min(
                    pendiente,
                    min(
                        deposito.getEspacioDisponible(pedido.producto),
                        capacidadCrossDockLibre(deposito.idUbicacion) * capacidadContenedor
                    )
                );

            if (posible <= 0.0001) {
                continue;
            }

            int contenedores =
                contenedoresNecesarios(pedido.producto, posible);

            // Sin almacenaje en el costo: no guardar es justamente el punto del
            // cross dock (ADR-010).
            double costo =
                calcularCostoPlantaDeposito(
                    deposito,
                    pedido.producto,
                    posible
                )
                + deposito.getImporteFletePuerto(
                    pedido.puertoSalida,
                    pedido.producto,
                    posible
                )
                + deposito.getImporteCrossDock(
                    pedido.producto,
                    posible,
                    contenedores
                );

            double costoPorTonelada = costo / posible;

            if (costoPorTonelada < menorCostoPorTonelada) {
                menorCostoPorTonelada = costoPorTonelada;
                mejorSitio = deposito;
            }
        }

        return mejorSitio;
    }

    double transferirLotesADeposito(TipoProducto producto, Deposito destino, double toneladas, boolean cruza) {
        double pendiente = toneladas;

        while (pendiente > 0.0001) {

            LoteProducto lote =
                buscarLoteMasAntiguoEnPlanta(producto);

            if (lote == null) {
                break;
            }

            double movidas =
                transferirToneladasLote(
                    lote,
                    destino,
                    min(
                        inventario.libreDeLoteEn(
                            lote.idLote,
                            "PLANTA"
                        ),
                        pendiente
                    ),
                    cruza
                );

            if (movidas <= 0.0001) {
                break;
            }

            pendiente -= movidas;
        }

        return toneladas - pendiente;
    }

    double ejecutarCrossDockPedido(Pedido pedido, Deposito sitio, double toneladasObjetivo) {
        // El pedido se sirve con producto que todavia esta en planta: sale hoy, se estiba hoy y
        // no entra a almacenamiento (ADR-010). Devuelve las toneladas cruzadas, que pueden ser
        // una fraccion del saldo pendiente (ADR-055).
        if (pedido == null) {
            return 0;
        }

        if (sitio == null) {
            crossDockReprogramados++;
            return 0;
        }

        double capacidadContenedor =
            obtenerCapacidadContenedorTon(obtenerTipoContenedor(pedido.producto));

        double toneladas =
            min(
                min(toneladasObjetivo, pedido.toneladasPendientesAsignar()),
                min(
                    inventario.libre("PLANTA", pedido.producto),
                    min(
                        sitio.getEspacioDisponible(pedido.producto),
                        capacidadCrossDockLibre(sitio.idUbicacion) * capacidadContenedor
                    )
                )
            );

        if (toneladas <= 0.0001) {
            return 0;
        }

        if (
            !flotaProductoAlcanza(
                "PLANTA",
                sitio.idUbicacion,
                toneladas
            )
        ) {
            crossDockReprogramados++;
            return 0;
        }

        int necesarios =
            contenedoresNecesarios(pedido.producto, toneladas);

        if (
            !tomarPosicionesCrossDock(
                sitio.idUbicacion,
                necesarios
            )
        ) {
            crossDockReprogramados++;
            return 0;
        }

        double movidas =
            transferirLotesADeposito(
                pedido.producto,
                sitio,
                toneladas,
                true
            );

        if (movidas <= 0.0001) {
            crossDockReprogramados++;
            return 0;
        }

        double reservadas =
            reservarParcialPedido(
                pedido,
                sitio.idUbicacion,
                movidas,
                EstrategiaLogistica.CROSS_DOCK_DEPOSITO,
                true,
                "cross dock por " + sitio.idUbicacion
            );

        // Lo que se movio y no se pudo comprometer queda en el deposito como stock normal y
        // devenga ingreso y almacenaje: es una operacion de cross dock que se degrado, y se
        // cuenta como tal. El cargo va sin lote porque puede venir de varios (ADR-053).
        double degradadas = max(0, movidas - reservadas);

        if (degradadas > 0.0001) {

            crossDockDegradados++;

            registrarInDeposito(
                sitio.idUbicacion,
                pedido.producto,
                degradadas,
                "",
                pedido.codigoPedido,
                "INX-" + diaCampania() + "-" + pedido.codigoPedido
            );
        }

        toneladasCrossDock += reservadas;

        return reservadas;
    }

    void programarCrossDockDelDia() {
        // Con evaluador el cross dock es una alternativa mas del pedido y se decide junto
        // con las demas: adelantarlo aca seria decidir dos veces (ADR-054).
        if (!habilitaCrossDock || usaEvaluador()) {
            return;
        }

        java.util.List<Pedido> candidatos =
            new java.util.ArrayList<Pedido>();

        // Cruza el saldo sin asignar, en cualquier estado abierto: un pedido reservado a medias
        // puede cruzar el resto (ADR-055).
        for (Pedido pedido : pedidos) {

            if (
                pedido.estado != EstadoPedido.ENTREGADO
                && pedido.estado != EstadoPedido.CANCELADO
                && pedido.toneladasPendientesAsignar() > 0.0001
            ) {
                candidatos.add(pedido);
            }
        }

        java.util.Collections.sort(
            candidatos,
            new java.util.Comparator<Pedido>() {
                public int compare(Pedido a, Pedido b) {
                    return Double.compare(
                        a.diaLimite,
                        b.diaLimite
                    );
                }
            }
        );

        for (Pedido pedido : candidatos) {
            intentarCrossDockPedido(pedido);
        }
    }

    void aplicarEscenario() {
        // La fila del escenario describe la corrida entera (ADR-032): lo que el
        // barrido enumera es el id, no cada palanca por separado.
        DatosEntrada.Escenario escenario = datos.escenario;

        duracionCampaniaDias = escenario.duracionCampaniaDias;
        habilitaCrossDock = escenario.habilitaCrossDock;
        politicaSeleccion = datos.politicaSeleccion();

        if (
            escenario.estrategiaConsolidacion.equals("CONSOLIDACION_PLANTA")
        ) {
            estrategiaConsolidacion =
                EstrategiaLogistica.CONSOLIDACION_PLANTA;

        } else if (
            escenario.estrategiaConsolidacion.equals("CONSOLIDACION_TERMINAL")
        ) {
            estrategiaConsolidacion =
                EstrategiaLogistica.CONSOLIDACION_TERMINAL;

        } else {
            estrategiaConsolidacion =
                EstrategiaLogistica.CONSOLIDACION_DEPOSITO;
        }

        // La flota de portacontenedores se fija al abrir el dia: en el arranque el pool
        // todavia no leyo su capacidad y set_capacity aca se pierde.
    }

    double costoTotalCampania() {
        // El total no suma acumuladores: es el saldo de caja del registro de cargos, y los
        // acumuladores de los agentes se reconcilian contra el todos los dias (ADR-052).
        return registro.total(RegistroCostos.Tipo.CAJA);
    }

    double toneladasExportadas() {
        double total = 0;

        for (TipoProducto producto : TipoProducto.values()) {
            total += toneladasEntregadas(producto);
        }

        return total;
    }

    double costoPorToneladaExportada() {
        double toneladas = toneladasExportadas();

        return toneladas <= 0
            ? 0
            : costoTotalCampania() / toneladas;
    }

    double nivelServicio() {
        // Pedido servido: entregado y no despues de su fecha limite.
        int servidos = 0;

        for (Pedido pedido : pedidos) {

            if (
                pedido.estado == EstadoPedido.ENTREGADO
                && pedido.diasAtraso <= 0
            ) {
                servidos++;
            }
        }

        return pedidosRecibidos <= 0
            ? 0
            : (double) servidos / pedidosRecibidos;
    }

    double atrasoPromedioDias() {
        // Incluye los pedidos sin entregar, que son los que mas atraso acumulan.
        double total = 0;

        for (Pedido pedido : pedidos) {
            total += pedido.diasAtraso;
        }

        return pedidosRecibidos <= 0
            ? 0
            : total / pedidosRecibidos;
    }

    double utilizacionFlota() {
        return camionDiaOfrecido <= 0
            ? 0
            : camionDiaOcupado / camionDiaOfrecido;
    }

    double excedenteFinalTn() {
        // Producto que la campania no logro exportar: se quedo en planta o en deposito.
        double total = 0;

        for (TipoProducto producto : TipoProducto.values()) {

            total += inventario.stock("PLANTA", producto);

            for (Deposito deposito : depositos) {
                total += inventario.stock(deposito.idUbicacion, producto);
            }
        }

        return total;
    }

    double usoPosicionesConsolidacion() {
        return capacidadConsolidacionOfrecida <= 0
            ? 0
            : consolidacionesRealizadas / capacidadConsolidacionOfrecida;
    }

    void abrirFlotaDelDia() {
        // La flota de producto es capacidad diaria, igual que las posiciones (ADR-039):
        // cada dia se ofrece un camion-dia por camion y lo que no entra espera al dia
        // siguiente. Los portacontenedores, en cambio, se toman y se liberan en el flujo.
        flotaProductoUsadaHoy = 0;
        flotaProductoOfrecidaHoy = datos.escenario.camionesProducto;

        flotaPortacontenedores.set_capacity(
            datos.escenario.camionesPortacontenedor
        );

        camionDiaOfrecido += flotaProductoOfrecidaHoy;
    }

    double camionDiaViaje(String origen, String destino) {
        // Un camion-dia es un camion durante una jornada, asi que un viaje consume la
        // fraccion de jornada que tarda el viaje redondo. La carga y la descarga todavia
        // no se le cobran al camion: PLANTA no tiene velocidades cargadas.
        double distancia = datos.distanciaKm(origen, destino);

        return 2 * distancia
            / datos.escenario.velocidadCamionKmh
            / datos.escenario.horasOperativasDia;
    }

    int viajesNecesariosCamion(double toneladas) {
        // Un camion lleva a lo sumo su capacidad: mover mas toneladas es hacer mas viajes,
        // no un viaje mas grande.
        return (int) ceil(
            toneladas / datos.escenario.capacidadCamionTn - 0.0001
        );
    }

    double flotaProductoLibreHoy() {
        return flotaProductoOfrecidaHoy - flotaProductoUsadaHoy;
    }

    double utilizacionPortacontenedor() {
        // El pool lleva la estadistica de ocupacion en tiempo continuo; muestrearlo una
        // vez por dia no veia los viajes que empiezan y terminan dentro del mismo dia.
        return flotaPortacontenedores.utilization();
    }

    LoteProducto buscarLoteComercialAbierto(TipoProducto producto, String cliente, String calidad) {
        // Lote comercial abierto compatible por producto, cliente y calidad (ADR-047).
        // Hay a lo sumo uno abierto por combinacion: al cerrarse, el siguiente ingreso
        // abre una identidad comercial nueva.
        for (LoteProducto lote : lotes) {
            if (
                lote.producto == producto
                && lote.estadoComercial == EstadoComercialLote.ABIERTO
                && lote.cliente.equals(cliente)
                && lote.calidad.equals(calidad)
            ) {
                return lote;
            }
        }

        return null;
    }

    int lotesComercialesAbiertos() {
        int abiertos = 0;

        for (LoteProducto lote : lotes) {
            if (lote.estadoComercial == EstadoComercialLote.ABIERTO) {
                abiertos++;
            }
        }

        return abiertos;
    }

    double forecastProduccion(TipoProducto producto, int dias) {
        // Forecast perfecto (ADR-048): se leen los proximos dias del plan de produccion,
        // que es un dato de entrada. Para dimensionar es la cota optimista, y deja el
        // error de pronostico como una extension explicita y no como un supuesto oculto.
        double total = 0;

        int hoy = (int) floor(time());

        for (int d = hoy + 1; d <= hoy + dias; d++) {
            total += datos.produccionDelDia(d, producto);
        }

        return total;
    }

    double demandaProyectada(TipoProducto producto) {
        // Toneladas comprometidas y todavia no asignadas. Un pedido ya recibido es una
        // obligacion: se cuenta completo aunque su fecha limite caiga despues del horizonte,
        // porque sacarlo de planta toma dias de flota y esperar al ultimo momento es lo que hace
        // perder el pedido. Con asignacion parcial lo que falta es el saldo, no el pedido entero.
        double total = 0;

        for (Pedido pedido : pedidos) {

            // La demanda conocida se cuenta completa aunque el retiro todavia no abra:
            // posicionar producto lleva dias de flota y esperar a la apertura es lo que
            // hace perder el cut-off (ADR-059). El permiso apagado es el caso contrario.
            boolean cuenta =
                datos.escenario.permiteTransferenciaAntesRetiro
                || pedido.ventanaRetiroAbierta;

            if (
                cuenta
                && pedido.producto == producto
                && pedido.estado != EstadoPedido.ENTREGADO
                && pedido.estado != EstadoPedido.CANCELADO
            ) {
                total += pedido.toneladasPendientesAsignar();
            }
        }

        return total;
    }

    double toneladasASacarDePlanta(TipoProducto producto) {
        // Tres motivos, y solo tres, para gastar frio de terceros:
        //
        //   1. desborde proyectado: con la produccion de los proximos dias la planta
        //      pasa su capacidad nominal;
        //   2. servicio: los pedidos con fecha limite dentro del horizonte no se pueden
        //      cubrir con lo que ya esta libre en deposito;
        //   3. prevencion: el stock proyectado toca el umbral de alerta y conviene bajar al
        //      objetivo antes de estar contra la capacidad (ADR-056).
        //
        // Los tres se combinan con un maximo y no con una suma: son lecturas distintas del
        // mismo stock, y sumarlas transferiria dos veces las mismas toneladas.
        int horizonte = datos.escenario.diasForecast;

        double stockPlanta =
            inventario.libre("PLANTA", producto);

        componentePorDesborde = 0;
        componentePorServicio = 0;
        componentePreventivo = 0;

        if (stockPlanta <= 0.0001) {
            return 0;
        }

        double proyectado =
            planta.getStock(producto)
            + forecastProduccion(producto, horizonte);

        componentePorDesborde =
            max(0, proyectado - planta.getCapacidad(producto));

        double libreEnDepositos = 0;

        for (Deposito deposito : depositos) {
            libreEnDepositos +=
                inventario.libre(deposito.idUbicacion, producto);
        }

        // Con consolidacion en planta la demanda se sirve del frio propio: sacarla al
        // deposito seria pagar almacenaje para despachar desde el mismo producto.
        componentePorServicio =
            consolidaEnPlanta()
            ? 0
            : max(
                0,
                demandaProyectada(producto) - libreEnDepositos
            );

        componentePreventivo =
            toneladasASacarPreventivamente(producto);

        return min(
            stockPlanta,
            max(componentePorDesborde, max(componentePorServicio, componentePreventivo))
        );
    }

    double toneladasASacarReactiva(TipoProducto producto) {
        // Regla anterior a la politica de frio propio, expresada en porcentaje de la
        // capacidad: al tocar el umbral de alerta se vacia la planta hasta el objetivo.
        double capacidad = planta.getCapacidad(producto);

        if (capacidad <= 0) {
            return 0;
        }

        double stock = planta.getStock(producto);

        if (100 * stock / capacidad < datos.escenario.umbralAlertaPct) {
            return 0;
        }

        double objetivo =
            capacidad * datos.escenario.umbralObjetivoPct / 100;

        return min(
            inventario.libre("PLANTA", producto),
            max(0, stock - objetivo)
        );
    }

    void registrarOcupacionPlanta() {
        // La capacidad de la planta es un indicador, no un tope (ADR-048): lo que se
        // mide es cuanto y cuantos dias se estuvo por encima, que es la respuesta a
        // "cuanto frio propio falta".
        boolean diaEnSobrecarga = false;

        for (TipoProducto producto : TipoProducto.values()) {

            double capacidad = planta.getCapacidad(producto);

            if (capacidad <= 0) {
                continue;
            }

            double stock = planta.getStock(producto);

            double ocupacion = 100 * stock / capacidad;

            picoOcupacionPlantaPct =
                max(picoOcupacionPlantaPct, ocupacion);

            double sobreNominal = max(0, stock - capacidad);

            if (sobreNominal <= 0.0001) {
                continue;
            }

            diaEnSobrecarga = true;

            tonDiaSobreNominalPlanta += sobreNominal;

            double critico =
                capacidad * datos.escenario.umbralSobrecargaPct / 100;

            tonDiaSobreCriticoPlanta += max(0, stock - critico);

            DatosEntrada.TarifaSitio tarifa =
                datos.tarifaSitio(diaCampania(), "PLANTA", producto);

            costoPenalidadSobrecarga += registro.registrar(
                time(),
                RegistroCostos.Categoria.PENALIDAD_SOBRECARGA,
                RegistroCostos.Tipo.ECONOMICO,
                "", "", "", producto,
                "PLANTA", "PLANTA", "PLANTA",
                EstrategiaLogistica.SIN_DEFINIR, tarifa.proveedor,
                DatosEntrada.Unidad.USD_TN_DIA, sobreNominal,
                tarifa.penalidadSobrecargaUsdTnDia,
                "PEN-" + diaCampania() + "-" + producto,
                "toneladas sobre la capacidad nominal"
            );
        }

        if (diaEnSobrecarga) {
            diasSobrecargaPlanta++;
        }
    }

    void devengarOportunidadFrioPropio() {
        // El frio propio no se factura, pero ocupa un recurso que tiene alternativa.
        // Se devenga aparte para que el costo de caja siga siendo comparable contra la
        // cotizacion de un tercero (ADR-049).
        for (TipoProducto producto : TipoProducto.values()) {

            DatosEntrada.TarifaSitio tarifa =
                datos.tarifaSitio(diaCampania(), "PLANTA", producto);

            costoOportunidadFrio += registro.registrar(
                time(),
                RegistroCostos.Categoria.OPORTUNIDAD_FRIO,
                RegistroCostos.Tipo.ECONOMICO,
                "", "", "", producto,
                "PLANTA", "PLANTA", "PLANTA",
                EstrategiaLogistica.SIN_DEFINIR, tarifa.proveedor,
                DatosEntrada.Unidad.USD_TN_DIA,
                inventario.stock("PLANTA", producto),
                tarifa.oportunidadUsdTnDia,
                "OPO-" + diaCampania() + "-" + producto,
                "ocupacion del frio propio"
            );
        }
    }

    double costoTotalEconomico() {
        // Costo de caja mas lo que cuesta ocupar el frio propio y la penalidad por operar
        // sobre la capacidad nominal (ADR-049). Con las tarifas en cero coincide con el costo
        // de caja.
        return registro.total();
    }

    double costoEconomicoPorTonelada() {
        double toneladas = toneladasExportadas();

        return toneladas <= 0
            ? 0
            : costoTotalEconomico() / toneladas;
    }

    boolean consolidaEnPlanta() {
        return estrategiaConsolidacion
            == EstrategiaLogistica.CONSOLIDACION_PLANTA;
    }

    boolean consolidaEnTerminal() {
        return estrategiaConsolidacion
            == EstrategiaLogistica.CONSOLIDACION_TERMINAL;
    }

    EstrategiaLogistica circuitoDe(String idSitioOrigen, boolean crossDock) {
        // El circuito es del pedido y no del escenario: la estrategia es la politica por
        // defecto y el origen efectivo termina de decidirlo (ADR-050).
        if (crossDock) {
            return EstrategiaLogistica.CROSS_DOCK_DEPOSITO;
        }

        if ("PLANTA".equals(idSitioOrigen)) {
            return EstrategiaLogistica.CONSOLIDACION_PLANTA;
        }

        return consolidaEnTerminal()
            ? EstrategiaLogistica.CONSOLIDACION_TERMINAL
            : EstrategiaLogistica.CONSOLIDACION_DEPOSITO;
    }

    String sitioEstiba(String idSitioOrigen, EstrategiaLogistica circuito, Terminal puerto) {
        // Donde se arma fisicamente el contenedor: el sitio de origen, salvo en el circuito de
        // terminal, donde el producto viaja a granel y se estiba en el puerto. Es por asignacion
        // y no por pedido: un pedido puede armar contenedores en dos sitios distintos (ADR-055).
        return circuito == EstrategiaLogistica.CONSOLIDACION_TERMINAL
            ? (puerto == null ? "" : puerto.idUbicacion)
            : idSitioOrigen;
    }

    boolean usaPortacontenedor(Envio envio) {
        // Los circuitos de planta, deposito y cross dock mandan el contenedor vacio al
        // origen y lo traen cargado: ocupan un portacontenedor de punta a punta. El de
        // terminal manda el producto a granel y no toca el pool.
        return envio != null
            && envio.circuito
                != EstrategiaLogistica.CONSOLIDACION_TERMINAL;
    }

    void registrarEstibaEnOrigen(Envio envio) {
        // El servicio se devenga donde se arma el contenedor (ADR-050): planta, deposito
        // o cross dock en deposito. Cada sitio lleva su propia estadistica.
        if (
            envio == null
            || envio.contenedor == null
            || !estibaEnOrigen(envio)
        ) {
            return;
        }

        if (envio.circuito == EstrategiaLogistica.CONSOLIDACION_PLANTA) {

            planta.toneladasConsolidadas +=
                envio.toneladas;

            planta.contenedoresConsolidados++;

            planta.costoConsolidacionAcumulado +=
                envio.costoConsolidacionReal;

            return;
        }

        Deposito deposito = envio.depositoOrigen;

        if (deposito == null) {
            return;
        }

        if (envio.contenedor.esCrossDock) {

            deposito.toneladasCrossDock +=
                envio.toneladas;

            deposito.contenedoresCrossDock++;

            deposito.costoCrossDockAcumulado +=
                envio.costoConsolidacionReal;

        } else {

            deposito.toneladasConsolidadas +=
                envio.toneladas;

            deposito.contenedoresConsolidados++;

            deposito.costoConsolidacionAcumulado +=
                envio.costoConsolidacionReal;
        }
    }

    void registrarIngresoTerminal(Envio envio) {
        envio.estado =
            EstadoEnvio.DESCARGANDO;

        envio.diaLlegadaTerminal =
            time();

        envio.terminalDestino
            .toneladasRecibidas +=
                envio.toneladas;

        envio.terminalDestino
            .cantidadEnviosRecibidos++;

        if (envio.contenedor != null) {
            envio.contenedor.estado =
                EstadoContenedor.INGRESADO_TERMINAL;

            envio.contenedor.horaIngresoTerminal = time();
        }

        if (usaPortacontenedor(envio)) {

            // Circuitos 1 a 3: entra un contenedor ya cargado, y ahi se devengan THC y costo
            // de terminal. El portacontenedor cobra la espera del sitio de estiba (ADR-053).
            envio.costoCargosReal +=
                registrarCargosTerminal(envio);

            envio.costoCargosReal += registrarEspera(
                envio,
                "PORTACONTENEDOR",
                envio.idSitioOrigen,
                envio.tiempoCargaHoras,
                "espera en el sitio de estiba"
            );

            return;
        }

        // Circuito 4: el viaje a granel termina aca, asi que aca se devenga el flete: el
        // transporte se cobra cuando el viaje se ejecuta (ADR-053).
        double flete = registrarFleteProducto(
            envio.idSitioOrigen,
            envio.terminalDestino.idUbicacion,
            envio.producto,
            envio.toneladas,
            viajesNecesariosCamion(envio.toneladas),
            "",
            envio.pedido.codigoPedido,
            envio.circuito,
            "ENV-" + envio.idEnvio
        );

        exigirIgual(
            flete,
            envio.costoFleteReal,
            "flete a granel del envio " + envio.idEnvio
        );

        envio.costoCargosReal += flete;
        costoFleteGranelTerminal += flete;

        envio.costoCargosReal += registrarEspera(
            envio,
            "CAMION_PRODUCTO",
            envio.idSitioOrigen,
            envio.tiempoCargaHoras,
            "espera en la carga a granel"
        );

        envio.costoCargosReal += registrarEspera(
            envio,
            "CAMION_PRODUCTO",
            envio.terminalDestino.idUbicacion,
            envio.tiempoDescargaHoras,
            "espera en la descarga a granel"
        );
    }

    void registrarConsolidacionEnTerminal(Envio envio) {
        // Circuito 4: el producto llego a granel y el contenedor se arma en la terminal.
        // Cross docking y consolidacion en terminal son el mismo servicio (ADR-050).
        envio.terminalDestino
            .toneladasConsolidadas +=
                envio.toneladas;

        envio.terminalDestino
            .costoConsolidacionAcumulado +=
                envio.costoConsolidacionReal;

        if (envio.contenedor != null) {
            envio.contenedor.estado =
                EstadoContenedor.CONSOLIDANDO;
        }

        // El contenedor recien existe aca, asi que este es el evento que devenga THC y costo
        // de terminal en el circuito de terminal (ADR-053).
        envio.costoCargosReal +=
            registrarCargosTerminal(envio);
    }

    void finalizarEnvio(Envio envio) {
        // Cierre del envio, comun a los cuatro circuitos: el contenedor de terminal sale
        // por su propia salida y no puede tener otra contabilidad que el resto.
        envio.estado =
            EstadoEnvio.ENTREGADO;

        envio.diaEntrega =
            time();

        Pedido pedido =
            envio.pedido;

        pedido.toneladasEntregadas +=
            envio.toneladas;

        pedido.enviosEntregados++;

        AsignacionPedido asignacion = asignacionDeEnvio(envio);

        if (asignacion != null) {

            asignacion.toneladasEntregadas += envio.toneladas;
            asignacion.diaUltimaEntrega = time();
            asignacion.cerrarSiCompleta();
        }

        // El servicio se mide contra el cut-off fisico y por tonelada: media entrega a
        // tiempo no es medio buque perdido, pero tampoco es servicio completo (ADR-059).
        if (time() <= pedido.diaLimite + 0.0001) {
            toneladasEntregadasAntesCutoff += envio.toneladas;
        } else {
            toneladasEntregadasFueraCutoff += envio.toneladas;
        }

        // El pedido se entrega cuando se completa, no cuando llega un envio: una entrega parcial
        // deja el pedido abierto con su saldo (ADR-055).
        if (pedido.estaCompleto()) {

            pedido.estado =
                EstadoPedido.ENTREGADO;

            pedido.diaEntrega =
                time();

            pedido.diasAtraso =
                max(
                    0,
                    pedido.diaEntrega
                    - pedido.diaLimite
                );
        }

        // El ciclo del portacontenedor se devenga recien cuando se completa (ADR-051); el
        // circuito de terminal no lo paga porque no usa portacontenedor (ADR-053).
        double roundTrip = registrarRoundTrip(envio);

        costoFleteDepositoPuertoReal += roundTrip;
        envio.costoCargosReal += roundTrip;

        double estiba = registrarServicioEstiba(envio);

        if (envio.esCrossDock) {
            costoCrossDockReal += estiba;
        } else {
            costoConsolidacionReal += estiba;
        }

        envio.costoCargosReal += estiba;

        envio.costoCargosReal +=
            registrarDespachante(envio);

        if (usaPortacontenedor(envio)) {

            // El portacontenedor tambien espera en la terminal mientras se descarga.
            envio.costoCargosReal += registrarEspera(
                envio,
                "PORTACONTENEDOR",
                envio.terminalDestino.idUbicacion,
                envio.tiempoDescargaHoras,
                "espera en la terminal"
            );
        }

        // Lo devengado tiene que ser exactamente lo que el circuito debe pagar segun las
        // tarifas: es la auditoria de costeo por circuito (V-COST-01 a V-COST-05).
        exigirIgual(
            envio.costoCargosReal,
            costoEsperadoCircuito(envio),
            "circuito " + envio.circuito + " del envio " + envio.idEnvio
        );

        envio.costoTotalReal =
            envio.costoCargosReal;

        pedido.costoFleteReal +=
            envio.costoFleteReal;

        pedido.costoConsolidacionReal +=
            envio.costoConsolidacionReal;

        pedido.costoLogisticoReal +=
            envio.costoTotalReal;

        switch (envio.circuito) {

            case CONSOLIDACION_PLANTA:
                contenedoresCircuitoPlanta++;
                break;

            case CROSS_DOCK_DEPOSITO:
                contenedoresCircuitoCrossDock++;
                break;

            case CONSOLIDACION_TERMINAL:
                contenedoresCircuitoTerminal++;
                break;

            default:
                contenedoresCircuitoDeposito++;
                break;
        }

        if (envio.contenedor != null) {
            envio.contenedor.estado =
                EstadoContenedor.EXPORTADO;

            envio.contenedor.costoReal =
                envio.costoTotalReal;
        }
    }

    void confirmarAsignacion(Pedido pedido, AsignacionPedido asignacion) {
        // Anota una asignacion nueva en el pedido. Los campos de un solo origen se conservan
        // como vista del primero conseguido (tablero y compatibilidad); la fuente de verdad de
        // origen y circuito son las asignaciones y, por envio, el contenedor (ADR-055).
        if (pedido.idSitioOrigen.isEmpty()) {
            pedido.idSitioOrigen = asignacion.idSitioOrigen;
            pedido.depositoAsignado = buscarDeposito(asignacion.idSitioOrigen);
            pedido.estrategiaSeleccionada = asignacion.circuito;
            pedido.esCrossDock = asignacion.esCrossDock;
        }

        boolean primera = pedido.diaReserva < 0;

        if (primera) {
            pedido.diaReserva = time();
            pedidosReservados++;
            pedidosPendientes--;
        }

        pedido.toneladasReservadas =
            pedido.toneladasAsignadasAcumuladas();

        // Un pedido vencido sigue vencido aunque hoy consiga stock: el estado lo maneja
        // registrarAtrasos() y no la asignacion.
        if (pedido.estado == EstadoPedido.PENDIENTE) {
            pedido.estado = EstadoPedido.RESERVADO;
        }

        double toneladas = asignacion.toneladasAsignadas;

        pedido.costoFleteEstimado +=
            datos.importeFlete(
                diaCampania(),
                asignacion.idSitioOrigen,
                pedido.puertoSalida.idUbicacion,
                pedido.producto,
                toneladas,
                viajesNecesariosCamion(toneladas)
            );

        pedido.costoConsolidadoEstimado +=
            importeServicioEstiba(
                sitioEstiba(
                    asignacion.idSitioOrigen,
                    asignacion.circuito,
                    pedido.puertoSalida
                ),
                asignacion.esCrossDock,
                pedido.producto,
                toneladas,
                contenedoresNecesarios(pedido.producto, toneladas)
            );

        pedido.costoTotalEstimado =
            pedido.costoFleteEstimado
            + pedido.costoConsolidadoEstimado;

        toneladasReservadasAcumuladas += toneladas;
    }

    double importeServicioEstiba(String sitio, boolean cruza, TipoProducto producto, double toneladas, int contenedores) {
        // Quien cobra la estiba es el sitio donde se arma el contenedor, y el cross dock es
        // otro servicio que la estiba desde stock (ADR-041, ADR-050). La unidad de la tarifa
        // decide si se cobra por tonelada o por contenedor completo (ADR-051).
        int dia = diaCampania();

        return cruza
            ? datos.importeCrossDock(dia, sitio, producto, toneladas, contenedores)
            : datos.importeConsolidacion(dia, sitio, producto, toneladas, contenedores);
    }

    boolean estibaEnOrigen(Envio envio) {
        // Tres de los cuatro circuitos estiban en el origen; solo el de terminal manda el
        // producto a granel y arma el contenedor en el puerto. Se lee del envio, que lleva el
        // circuito de su asignacion (ADR-055).
        return envio != null
            && envio.circuito
                != EstrategiaLogistica.CONSOLIDACION_TERMINAL;
    }

    void tomarFlotaProducto(String idOrigen, String idDestino, int viajes) {
        double camionDia =
            viajes * camionDiaViaje(idOrigen, idDestino);

        flotaProductoUsadaHoy += camionDia;
        camionDiaOcupado += camionDia;

        if (buscarTerminal(idDestino) != null) {
            viajesGranelTerminal += viajes;
        } else {
            viajesPlantaDeposito += viajes;
        }

        // La capacidad del dia es un limite fisico, no una preferencia: si se sobregira
        // es que alguien movio producto sin pedir camion (V-026).
        if (flotaProductoUsadaHoy > flotaProductoOfrecidaHoy + 0.0001) {
            error(
                "Dia "
                + time()
                + ": la flota de producto se sobregiro ("
                + flotaProductoUsadaHoy
                + " de "
                + flotaProductoOfrecidaHoy
                + " camion-dia)."
            );
        }
    }

    boolean flotaProductoAlcanza(String idOrigen, String idDestino, double toneladas) {
        // El movimiento es todo o nada: si la flota del dia no alcanza para el pedido
        // entero, no se mueve nada (ADR-010).
        return flotaProductoLibreHoy() + 0.0001
            >= viajesNecesariosCamion(toneladas)
            * camionDiaViaje(idOrigen, idDestino);
    }

    int diaCampania() {
        // Las tarifas reales cambian por mes: el dia de campania es la clave con la que
        // se resuelve cual esta vigente (ADR-051).
        return (int) Math.floor(time());
    }

    void refrescarTarifasDelDia() {
        // El almacenaje se devenga por dia y su tarifa puede cambiar de mes: el deposito
        // no guarda la tarifa de la campania, guarda la de hoy (ADR-051).
        int dia = diaCampania();

        for (Deposito deposito : depositos) {

            deposito.costoJugoTnDia =
                datos.storageUsdTnDia(dia, deposito.idUbicacion, TipoProducto.JUGO);

            deposito.costoCascaraTnDia =
                datos.storageUsdTnDia(dia, deposito.idUbicacion, TipoProducto.CASCARA);

            deposito.costoAceiteTnDia =
                datos.storageUsdTnDia(dia, deposito.idUbicacion, TipoProducto.ACEITE);
        }
    }

    double registrarFleteProducto(String origen, String destino, TipoProducto producto, double toneladas, int viajes, String idLote, String codigoPedido, EstrategiaLogistica estrategia, String idOperacion) {
        // El flete a granel puede venir por viaje o por tonelada, y ademas tener una parte
        // variable: cada componente es un cargo con su propia unidad, para que el importe
        // siempre sea cantidad por tarifa (ADR-052).
        int dia = diaCampania();

        DatosEntrada.TarifaFlete tarifa =
            datos.tarifaFlete(dia, origen, destino, producto);

        double importe = 0;

        if (tarifa.unidad == DatosEntrada.Unidad.USD_VIAJE) {

            importe += registro.registrar(
                time(),
                RegistroCostos.Categoria.FLETE_PRODUCTO,
                RegistroCostos.Tipo.CAJA,
                codigoPedido, "", idLote, producto,
                origen, destino, origen, estrategia, tarifa.proveedor,
                DatosEntrada.Unidad.USD_VIAJE, viajes,
                datos.fleteTarifaUnitaria(dia, origen, destino, producto),
                idOperacion, "flete por viaje"
            );

        } else {

            importe += registro.registrar(
                time(),
                RegistroCostos.Categoria.FLETE_PRODUCTO,
                RegistroCostos.Tipo.CAJA,
                codigoPedido, "", idLote, producto,
                origen, destino, origen, estrategia, tarifa.proveedor,
                DatosEntrada.Unidad.USD_TN, toneladas,
                datos.fleteTarifaUnitaria(dia, origen, destino, producto),
                idOperacion, "flete por tonelada"
            );
        }

        if (tarifa.variableUsdTn > 0) {

            importe += registro.registrar(
                time(),
                RegistroCostos.Categoria.FLETE_PRODUCTO,
                RegistroCostos.Tipo.CAJA,
                codigoPedido, "", idLote, producto,
                origen, destino, origen, estrategia, tarifa.proveedor,
                DatosEntrada.Unidad.USD_TN, toneladas,
                datos.fleteVariableUsdTn(dia, origen, destino, producto),
                idOperacion, "componente variable del flete"
            );
        }

        return importe;
    }

    double registrarRoundTrip(Envio envio) {
        // El ciclo se devenga recien cuando se completa: un circuito truncado al cierre de
        // la campania no genera cargo (ADR-051). La tarifa es la del dia en que salio.
        if (!usaPortacontenedor(envio)) {

            // El circuito de terminal manda el producto a granel: no hay ciclo que cobrar, y
            // su transporte ya se devengo como flete de producto (ADR-053).
            return 0;
        }

        int diaTarifa = (int) Math.floor(envio.diaCreacion);

        Pedido pedido = envio.pedido;

        DatosEntrada.TarifaRoundTrip tarifa =
            datos.tarifaRoundTrip(
                diaTarifa,
                envio.terminalDestino.idUbicacion,
                envio.idSitioOrigen,
                pedido.tipoContenedor
            );

        double importe = registro.registrar(
            time(),
            RegistroCostos.Categoria.ROUND_TRIP,
            RegistroCostos.Tipo.CAJA,
            pedido.codigoPedido,
            envio.contenedor == null ? "" : envio.contenedor.idContenedor,
            "", envio.producto,
            envio.idSitioOrigen, envio.terminalDestino.idUbicacion, envio.idSitioOrigen,
            envio.circuito, tarifa.proveedor,
            DatosEntrada.Unidad.USD_CONTENEDOR, 1,
            datos.roundTripUsdContenedor(
                diaTarifa,
                envio.terminalDestino.idUbicacion,
                envio.idSitioOrigen,
                pedido.tipoContenedor
            ),
            "ENV-" + envio.idEnvio, "ciclo terminal -> origen -> terminal"
        );

        exigirIgual(importe, envio.costoFleteReal, "round trip del envio " + envio.idEnvio);

        return importe;
    }

    double registrarServicioEstiba(Envio envio) {
        // Consolidar y cruzar son dos servicios distintos del mismo sitio, y la unidad de
        // la tarifa decide si se cobran por tonelada o por contenedor completo (ADR-051).
        int diaTarifa = (int) Math.floor(envio.diaCreacion);

        Pedido pedido = envio.pedido;

        String sitio =
            sitioEstiba(envio.idSitioOrigen, envio.circuito, envio.terminalDestino);

        DatosEntrada.TarifaSitio tarifa =
            datos.tarifaSitio(diaTarifa, sitio, envio.producto);

        boolean cruza = envio.esCrossDock;

        DatosEntrada.Unidad unidad =
            cruza ? tarifa.crossDockUnidad : tarifa.consolidacionUnidad;

        double usd =
            cruza
            ? datos.crossDockTarifa(diaTarifa, sitio, envio.producto)
            : datos.consolidacionTarifa(diaTarifa, sitio, envio.producto);

        double cantidad =
            unidad == DatosEntrada.Unidad.USD_CONTENEDOR
            ? contenedoresNecesarios(envio.producto, envio.toneladas)
            : envio.toneladas;

        double importe = registro.registrar(
            time(),
            cruza
                ? RegistroCostos.Categoria.CROSS_DOCK
                : RegistroCostos.Categoria.CONSOLIDACION,
            RegistroCostos.Tipo.CAJA,
            pedido.codigoPedido,
            envio.contenedor == null ? "" : envio.contenedor.idContenedor,
            "", envio.producto,
            envio.idSitioOrigen, envio.terminalDestino.idUbicacion, sitio,
            envio.circuito, tarifa.proveedor,
            unidad, cantidad, usd,
            "ENV-" + envio.idEnvio,
            cruza ? "cross dock del envio" : "estiba del envio"
        );

        exigirIgual(importe, envio.costoConsolidacionReal, "estiba del envio " + envio.idEnvio);

        return importe;
    }

    void exigirIgual(double registrado, double cotizado, String concepto) {
        // El registro es la fuente de verdad: si un devengo no coincide con lo que el envio
        // tenia cotizado, la tarifa cambio entre la salida y la entrega y el numero deja de
        // ser explicable (ADR-052).
        if (Math.abs(registrado - cotizado) > RegistroCostos.EPS) {
            throw new RuntimeException("Devengo distinto de lo cotizado en " + concepto
                + ": registro " + registrado + " contra " + cotizado + ".");
        }
    }

    void reconciliarCostos() {
        // Todo total del modelo tiene que poder explicarse cargo por cargo (ADR-052). Se
        // llama todos los dias y al cierre de la corrida: un desvio es un error de modelo.
        double dia = time();

        registro.reconciliar(
            RegistroCostos.Categoria.ALMACENAMIENTO, getCostoAlmacenamientoTotal(), dia);

        // El flete de producto tiene dos destinos: el deposito y la terminal del circuito 4.
        registro.reconciliar(
            RegistroCostos.Categoria.FLETE_PRODUCTO,
            costoFletePlantaDeposito + costoFleteGranelTerminal, dia);

        registro.reconciliar(
            RegistroCostos.Categoria.ROUND_TRIP, costoFleteDepositoPuertoReal, dia);

        registro.reconciliar(
            RegistroCostos.Categoria.CONSOLIDACION, costoConsolidacionReal, dia);

        registro.reconciliar(
            RegistroCostos.Categoria.CROSS_DOCK, costoCrossDockReal, dia);

        registro.reconciliar(
            RegistroCostos.Categoria.IN_DEPOSITO, costoInDeposito, dia);

        registro.reconciliar(
            RegistroCostos.Categoria.OUT_DEPOSITO, costoOutDeposito, dia);

        registro.reconciliar(
            RegistroCostos.Categoria.THC, costoThcReal, dia);

        registro.reconciliar(
            RegistroCostos.Categoria.COSTO_TERMINAL, costoTerminalReal, dia);

        registro.reconciliar(
            RegistroCostos.Categoria.DESPACHANTE, costoDespachanteReal, dia);

        registro.reconciliar(
            RegistroCostos.Categoria.ESPERA_CAMION_PRODUCTO, costoEsperaCamionProducto, dia);

        registro.reconciliar(
            RegistroCostos.Categoria.ESPERA_PORTACONTENEDOR, costoEsperaPortacontenedor, dia);

        registro.reconciliar(
            RegistroCostos.Categoria.OPORTUNIDAD_FRIO, costoOportunidadFrio, dia);

        registro.reconciliar(
            RegistroCostos.Categoria.PENALIDAD_SOBRECARGA, costoPenalidadSobrecarga, dia);
    }

    double registrarInDeposito(String idSitio, TipoProducto producto, double toneladas, String idLote, String codigoPedido, String idOperacion) {
        // El ingreso al almacenamiento se devenga cuando el producto entra fisicamente al
        // deposito. El cross dock no lo paga: cruza el sitio sin ingresar al stock (ADR-053).
        DatosEntrada.TarifaSitio tarifa =
            datos.tarifaSitio(diaCampania(), idSitio, producto);

        double importe = registro.registrar(
            time(),
            RegistroCostos.Categoria.IN_DEPOSITO,
            RegistroCostos.Tipo.CAJA,
            codigoPedido, "", idLote, producto,
            idSitio, idSitio, idSitio,
            EstrategiaLogistica.SIN_DEFINIR, tarifa.proveedor,
            DatosEntrada.Unidad.USD_TN, toneladas, tarifa.inUsdTn,
            idOperacion, "ingreso al almacenamiento"
        );

        costoInDeposito += importe;

        return importe;
    }

    boolean pagaOutDeposito(Envio envio) {
        // Solo paga egreso el producto que estuvo almacenado en un deposito de terceros: no
        // lo paga lo que sale del frio propio ni lo que cruza en cross dock (ADR-053). Se lee
        // del envio: en el mismo pedido puede haber una fraccion que cruza y otra que no.
        return envio != null
            && envio.pedido != null
            && !envio.esCrossDock
            && buscarDeposito(envio.idSitioOrigen) != null;
    }

    double registrarOutDeposito(Envio envio) {
        if (!pagaOutDeposito(envio)) {
            return 0;
        }

        DatosEntrada.TarifaSitio tarifa =
            datos.tarifaSitio(diaCampania(), envio.idSitioOrigen, envio.producto);

        double importe = registro.registrar(
            time(),
            RegistroCostos.Categoria.OUT_DEPOSITO,
            RegistroCostos.Tipo.CAJA,
            envio.pedido.codigoPedido,
            envio.contenedor == null ? "" : envio.contenedor.idContenedor,
            "", envio.producto,
            envio.idSitioOrigen, envio.terminalDestino.idUbicacion, envio.idSitioOrigen,
            envio.circuito, tarifa.proveedor,
            DatosEntrada.Unidad.USD_TN, envio.toneladas, tarifa.outUsdTn,
            "ENV-" + envio.idEnvio, "egreso del almacenamiento"
        );

        costoOutDeposito += importe;

        return importe;
    }

    double registrarCargosTerminal(Envio envio) {
        // THC y costo de terminal se cobran por contenedor y no por tonelada: el ultimo
        // contenedor parcial paga completo (respuesta del negocio, C1). Se devengan cuando el
        // contenedor cargado entra a la terminal, una sola vez por envio (ADR-053).
        int dia = diaCampania();

        String terminal = envio.terminalDestino.idUbicacion;

        DatosEntrada.TarifaSitio tarifa =
            datos.tarifaSitio(dia, terminal, envio.producto);

        int contenedores =
            contenedoresNecesarios(envio.producto, envio.toneladas);

        // El dia del devengo queda anotado: con tarifas mensuales, auditar con otro dia seria
        // comparar contra una tarifa distinta de la que se cobro.
        envio.diaCargosTerminal = dia;

        double thc = registro.registrar(
            time(),
            RegistroCostos.Categoria.THC,
            RegistroCostos.Tipo.CAJA,
            envio.pedido.codigoPedido,
            envio.contenedor == null ? "" : envio.contenedor.idContenedor,
            "", envio.producto,
            envio.idSitioOrigen, terminal, terminal,
            envio.circuito, tarifa.proveedor,
            DatosEntrada.Unidad.USD_CONTENEDOR, contenedores,
            datos.thcUsdContenedor(dia, terminal, envio.producto),
            "ENV-" + envio.idEnvio, "thc del contenedor"
        );

        double terminalCargo = registro.registrar(
            time(),
            RegistroCostos.Categoria.COSTO_TERMINAL,
            RegistroCostos.Tipo.CAJA,
            envio.pedido.codigoPedido,
            envio.contenedor == null ? "" : envio.contenedor.idContenedor,
            "", envio.producto,
            envio.idSitioOrigen, terminal, terminal,
            envio.circuito, tarifa.proveedor,
            DatosEntrada.Unidad.USD_CONTENEDOR, contenedores,
            datos.costoTerminalUsdContenedor(dia, terminal, envio.producto),
            "ENV-" + envio.idEnvio, "costo de terminal del contenedor"
        );

        costoThcReal += thc;
        costoTerminalReal += terminalCargo;

        return thc + terminalCargo;
    }

    double registrarDespachante(Envio envio) {
        // El despachante se cobra por contenedor o por pedido segun el contrato: con la
        // unidad por pedido el cargo es uno por pedido y el registro descarta el segundo
        // envio del mismo pedido por clave repetida (ADR-052).
        int dia = diaCampania();

        String terminal = envio.terminalDestino.idUbicacion;

        Pedido pedido = envio.pedido;

        DatosEntrada.TarifaSitio tarifa =
            datos.tarifaSitio(dia, terminal, envio.producto);

        boolean porPedido =
            tarifa.despachanteUnidad == DatosEntrada.Unidad.USD_PEDIDO;

        int contenedores =
            contenedoresNecesarios(envio.producto, envio.toneladas);

        double importe = registro.registrar(
            time(),
            RegistroCostos.Categoria.DESPACHANTE,
            RegistroCostos.Tipo.CAJA,
            pedido.codigoPedido,
            porPedido
                ? ""
                : (envio.contenedor == null ? "" : envio.contenedor.idContenedor),
            "", envio.producto,
            envio.idSitioOrigen, terminal, terminal,
            envio.circuito, tarifa.proveedor,
            tarifa.despachanteUnidad,
            porPedido ? 1 : contenedores,
            datos.despachanteTarifa(dia, terminal, envio.producto),
            porPedido ? "PED-" + pedido.codigoPedido : "ENV-" + envio.idEnvio,
            "despacho de exportacion"
        );

        costoDespachanteReal += importe;

        return importe;
    }

    double registrarEspera(Envio envio, String tipoRecurso, String idSitio, double horas, String motivo) {
        // Se paga solo lo que pasa la franquicia del sitio, y el recurso decide el contrato:
        // el camion de producto y el portacontenedor no esperan al mismo precio (ADR-053).
        int dia = diaCampania();

        DatosEntrada.TarifaEspera tarifa =
            datos.tarifaEspera(dia, tipoRecurso, idSitio);

        double facturables =
            datos.horasEsperaFacturables(dia, tipoRecurso, idSitio, horas);

        if (facturables <= 0) {
            return 0;
        }

        RegistroCostos.Categoria categoria =
            tipoRecurso.equals("PORTACONTENEDOR")
            ? RegistroCostos.Categoria.ESPERA_PORTACONTENEDOR
            : RegistroCostos.Categoria.ESPERA_CAMION_PRODUCTO;

        double importe = registro.registrar(
            time(),
            categoria,
            RegistroCostos.Tipo.CAJA,
            envio.pedido.codigoPedido,
            envio.contenedor == null ? "" : envio.contenedor.idContenedor,
            "", envio.producto,
            envio.idSitioOrigen, envio.terminalDestino.idUbicacion, idSitio,
            envio.circuito, tarifa.proveedor,
            DatosEntrada.Unidad.USD_HORA, facturables, tarifa.usdHora,
            "ENV-" + envio.idEnvio, motivo
        );

        if (categoria == RegistroCostos.Categoria.ESPERA_PORTACONTENEDOR) {
            costoEsperaPortacontenedor += importe;
        } else {
            costoEsperaCamionProducto += importe;
        }

        return importe;
    }

    double costoEsperadoCircuito(Envio envio) {
        // Auditoria de costeo por circuito (V-COST-01 a V-COST-05): reconstruye lo que el
        // circuito tiene que pagar leyendo las tarifas y sin mirar el registro. Si no coincide
        // con lo devengado, falta un cargo o hay uno de mas.
        Pedido pedido = envio.pedido;

        String terminal = envio.terminalDestino.idUbicacion;

        String sitio =
            sitioEstiba(envio.idSitioOrigen, envio.circuito, envio.terminalDestino);

        int diaSalida = (int) Math.floor(envio.diaCreacion);

        int diaLlegada = (int) Math.floor(envio.diaLlegadaTerminal);

        int diaCierre = (int) Math.floor(envio.diaEntrega);

        int contenedores =
            contenedoresNecesarios(envio.producto, envio.toneladas);

        double esperado = 0;

        // Transporte: el ciclo del portacontenedor o el flete a granel, nunca los dos.
        if (usaPortacontenedor(envio)) {

            esperado += datos.roundTripUsdContenedor(
                diaSalida, terminal, envio.idSitioOrigen, pedido.tipoContenedor);

            esperado += datos.importeEspera(
                diaLlegada, "PORTACONTENEDOR", envio.idSitioOrigen, envio.tiempoCargaHoras);

            esperado += datos.importeEspera(
                diaCierre, "PORTACONTENEDOR", terminal, envio.tiempoDescargaHoras);

        } else {

            esperado += datos.importeFlete(
                diaLlegada, envio.idSitioOrigen, terminal, envio.producto,
                envio.toneladas, viajesNecesariosCamion(envio.toneladas));

            esperado += datos.importeEspera(
                diaLlegada, "CAMION_PRODUCTO", envio.idSitioOrigen, envio.tiempoCargaHoras);

            esperado += datos.importeEspera(
                diaLlegada, "CAMION_PRODUCTO", terminal, envio.tiempoDescargaHoras);
        }

        // Armado del contenedor, en el sitio donde ocurre.
        esperado += envio.esCrossDock
            ? datos.importeCrossDock(
                diaSalida, sitio, envio.producto, envio.toneladas, contenedores)
            : datos.importeConsolidacion(
                diaSalida, sitio, envio.producto, envio.toneladas, contenedores);

        // Cargos de la terminal, por contenedor completo y con la tarifa del dia en que se
        // devengaron: en el circuito de terminal el contenedor se arma despues de llegar.
        int diaTerminal = (int) envio.diaCargosTerminal;

        esperado += datos.thcUsdContenedor(diaTerminal, terminal, envio.producto) * contenedores;

        esperado +=
            datos.costoTerminalUsdContenedor(diaTerminal, terminal, envio.producto) * contenedores;

        DatosEntrada.TarifaSitio tarifaCierre =
            datos.tarifaSitio(diaCierre, terminal, envio.producto);

        if (tarifaCierre.despachanteUnidad == DatosEntrada.Unidad.USD_PEDIDO) {

            // Un solo cargo por pedido: lo paga el primer envio que se entrega.
            if (pedido.enviosEntregados <= 1) {
                esperado += datos.despachanteTarifa(diaCierre, terminal, envio.producto);
            }

        } else {
            esperado +=
                datos.despachanteTarifa(diaCierre, terminal, envio.producto) * contenedores;
        }

        // Egreso del deposito de origen, cuando el producto salio de almacenamiento.
        if (pagaOutDeposito(envio)) {
            esperado +=
                datos.outUsdTn(diaSalida, envio.idSitioOrigen, envio.producto) * envio.toneladas;
        }

        return esperado;
    }

    double costoEndToEndPedido(Pedido pedido) {
        // Todo lo devengado contra el pedido. El almacenaje del stock del que se sirvio es
        // del lote y ya estaba incurrido cuando el pedido eligio su origen (ADR-053).
        return pedido == null
            ? 0
            : registro.totalDePedido(pedido.codigoPedido, RegistroCostos.Tipo.CAJA);
    }

    double costoIncrementalPedido(Pedido pedido) {
        // Lo que agrega la decision: los cargos posteriores al dia en que el pedido eligio
        // origen y circuito. Es la vista con la que se comparan alternativas.
        return pedido == null
            ? 0
            : registro.totalIncrementalDePedido(
                pedido.codigoPedido, RegistroCostos.Tipo.CAJA, pedido.diaReserva);
    }

    double costoHistoricoPedido(Pedido pedido) {
        // Costo hundido: lo devengado contra el pedido antes de la decision mas el
        // almacenaje y el flete del stock que va a consumir. No debe entrar en la comparacion
        // entre alternativas, y se reporta para poder explicarlo.
        if (pedido == null) {
            return 0;
        }

        double historico = registro.totalHistoricoDePedido(
            pedido.codigoPedido, RegistroCostos.Tipo.CAJA, pedido.diaReserva);

        for (LoteProducto lote : lotes) {

            if (
                lote.producto == pedido.producto
                && inventario.reservadoDeLotePorPedido(lote.idLote, pedido.codigoPedido) > 0
            ) {
                historico += registro.totalDeLote("" + lote.idLote, RegistroCostos.Tipo.CAJA);
            }
        }

        return historico;
    }

    boolean usaEvaluador() {
        // Las politicas FIJA_* y MANUAL son la conducta previa al evaluador y quedan como
        // regresion: solo las de costo y la de frio propio generan y comparan alternativas.
        return politicaSeleccion == DatosEntrada.PoliticaSeleccion.PRIORIDAD_FRIO_PROPIO
            || politicaSeleccion
                == DatosEntrada.PoliticaSeleccion.MENOR_COSTO_INCREMENTAL_FACTIBLE
            || politicaSeleccion
                == DatosEntrada.PoliticaSeleccion.MENOR_COSTO_END_TO_END_FACTIBLE;
    }

    boolean decideEndToEnd() {
        // La decision tactica compara incremental; la estrategica, end-to-end (seccion 7.4).
        return politicaSeleccion
            == DatosEntrada.PoliticaSeleccion.MENOR_COSTO_END_TO_END_FACTIBLE;
    }

    java.util.List<AlternativaCircuito> generarAlternativas(Pedido pedido) {
        // Un pedido puede servirse de varios origenes por varios circuitos. Se enumeran todos
        // los que el flujo fisico sabe ejecutar y se agrega, descartada, la transferencia entre
        // depositos: no existe como movimiento (C7) y no se aproxima con otro circuito.
        //
        // Lo que se evalua es el saldo pendiente y no el pedido entero, y cada alternativa se
        // acota a lo que su origen puede dar hoy: asi una que resuelve una parte compite en vez
        // de quedar descartada por no alcanzar para todo (ADR-055).
        java.util.List<AlternativaCircuito> alternativas =
            new java.util.ArrayList<AlternativaCircuito>();

        double pendiente = pedido.toneladasPendientesAsignar();

        String terminal = pedido.puertoSalida.idUbicacion;

        alternativas.add(
            alternativaPara(
                pedido, pendiente, "PLANTA", "PLANTA",
                EstrategiaLogistica.CONSOLIDACION_PLANTA, false));

        alternativas.add(
            alternativaPara(
                pedido, pendiente, "PLANTA", terminal,
                EstrategiaLogistica.CONSOLIDACION_TERMINAL, false));

        for (Deposito deposito : depositos) {

            alternativas.add(
                alternativaPara(
                    pedido, pendiente, deposito.idUbicacion, deposito.idUbicacion,
                    EstrategiaLogistica.CONSOLIDACION_DEPOSITO, false));

            alternativas.add(
                alternativaPara(
                    pedido, pendiente, deposito.idUbicacion, deposito.idUbicacion,
                    EstrategiaLogistica.CROSS_DOCK_DEPOSITO, true));

            alternativas.add(
                alternativaPara(
                    pedido, pendiente, deposito.idUbicacion, terminal,
                    EstrategiaLogistica.CONSOLIDACION_TERMINAL, false));
        }

        for (AlternativaCircuito alternativa : alternativas) {
            evaluarAlternativa(pedido, alternativa);
        }

        AlternativaCircuito transferencia =
            alternativaPara(
                pedido, pendiente, "DEPOSITO", "DEPOSITO",
                EstrategiaLogistica.CONSOLIDACION_DEPOSITO, false);

        transferencia.descartar(
            "transferencia deposito-deposito sin movimiento fisico en el modelo (C7)");

        alternativas.add(transferencia);

        return alternativas;
    }

    void evaluarAlternativa(Pedido pedido, AlternativaCircuito alternativa) {
        // Factibilidad antes que costo: una alternativa que el flujo no puede ejecutar hoy no
        // compite, y el motivo queda escrito para poder auditar la decision (seccion 6.3).
        alternativa.factible = true;
        alternativa.motivoNoFactible = "";

        String terminal = pedido.puertoSalida.idUbicacion;

        double toneladas = alternativa.toneladas;

        // Con asignacion parcial el volumen ya viene acotado a lo posible: si quedo en cero, el
        // origen no tiene nada que ofrecer hoy (ADR-055).
        if (toneladas <= 0.0001) {
            alternativa.descartar(
                alternativa.esCrossDock
                ? "sin stock, espacio o cupo para cruzar por " + alternativa.idOrigen
                : "sin stock libre en " + alternativa.idOrigen);
            return;
        }

        if (alternativa.esCrossDock) {

            if (!habilitaCrossDock) {
                alternativa.descartar("cross dock deshabilitado en el escenario");
                return;
            }

            // El cross dock cruza producto que todavia esta en planta: lo que ya entro a un
            // deposito no vuelve a cruzar (ADR-010).
            if (
                inventario.libre("PLANTA", pedido.producto) + 0.0001
                < toneladas
            ) {
                alternativa.descartar("sin stock libre en planta para cruzar");
                return;
            }

            Deposito sitio = buscarDeposito(alternativa.idOrigen);

            if (sitio == null || !sitio.habilitado) {
                alternativa.descartar("deposito no habilitado");
                return;
            }

            if (
                capacidadCrossDockLibre(alternativa.idOrigen)
                < alternativa.contenedores
            ) {
                alternativa.descartar("sin cupo de cross dock hoy");
                return;
            }

            if (
                sitio.getEspacioDisponible(pedido.producto) + 0.0001
                < toneladas
            ) {
                alternativa.descartar("sin espacio de paso en el deposito");
                return;
            }

            if (
                !flotaProductoAlcanza("PLANTA", alternativa.idOrigen, toneladas)
            ) {
                alternativa.descartar("sin flota de producto planta-deposito");
                return;
            }

        } else {

            if (
                inventario.libre(alternativa.idOrigen, pedido.producto) + 0.0001
                < toneladas
            ) {
                alternativa.descartar("sin stock libre en " + alternativa.idOrigen);
                return;
            }

            if (
                alternativa.circuito == EstrategiaLogistica.CONSOLIDACION_TERMINAL
                && !flotaProductoAlcanza(alternativa.idOrigen, terminal, toneladas)
            ) {
                alternativa.descartar("sin flota de producto para el granel a terminal");
                return;
            }
        }

        // El contenedor se arma en algun lado: sin capacidad declarada el circuito no puede
        // ejecutarse ningun dia, no solo hoy.
        if (
            !alternativa.esCrossDock
            && datos.ubicacion(alternativa.sitioEstiba).contenedoresPorDia <= 0
        ) {
            alternativa.descartar("sin capacidad de estiba en " + alternativa.sitioEstiba);
            return;
        }

        costearAlternativa(pedido, alternativa);

        // Un dia para armar y programar el contenedor mas el ciclo fisico del circuito,
        // contados desde que la ventana de retiro abre: antes de esa fecha el circuito
        // no puede empezar por mas barato que sea (ADR-059).
        alternativa.diaEntregaEstimado =
            max(time(), pedido.diaAperturaRetiroVacio)
            + 1 + horasCicloAlternativa(pedido, alternativa) / 24.0;

        alternativa.llegaATiempo =
            alternativa.diaEntregaEstimado <= pedido.diaLimite + 0.0001;
    }

    void costearAlternativa(Pedido pedido, AlternativaCircuito alternativa) {
        // Gemelo ex ante de costoEsperadoCircuito(): las mismas tarifas, unidades y reglas de
        // contenedor completo, con la diferencia de que aca todavia no existe el envio. Si los
        // dos difieren, el evaluador esta decidiendo con un costo que despues no se cobra.
        int dia = diaCampania();

        String terminal = pedido.puertoSalida.idUbicacion;

        double toneladas = alternativa.toneladas;

        int contenedores = alternativa.contenedores;

        boolean granel =
            alternativa.circuito == EstrategiaLogistica.CONSOLIDACION_TERMINAL;

        alternativa.costoFleteProducto = 0;

        if (alternativa.esCrossDock) {

            // Tramo previo: el producto sale hoy de planta y cruza sin guardarse.
            alternativa.costoFleteProducto +=
                datos.importeFlete(
                    dia, "PLANTA", alternativa.idOrigen, pedido.producto,
                    toneladas, viajesNecesariosCamion(toneladas));
        }

        if (granel) {

            alternativa.costoFleteProducto +=
                datos.importeFlete(
                    dia, alternativa.idOrigen, terminal, pedido.producto,
                    toneladas, viajesNecesariosCamion(toneladas));

            alternativa.costoRoundTrip = 0;

        } else {

            alternativa.costoRoundTrip =
                datos.roundTripUsdContenedor(
                    dia, terminal, alternativa.idOrigen, pedido.tipoContenedor)
                * contenedores;
        }

        alternativa.costoEstiba =
            alternativa.esCrossDock
            ? datos.importeCrossDock(
                dia, alternativa.sitioEstiba, pedido.producto, toneladas, contenedores)
            : datos.importeConsolidacion(
                dia, alternativa.sitioEstiba, pedido.producto, toneladas, contenedores);

        // El egreso lo paga lo que estaba almacenado: el cross dock no entra ni sale, y la
        // planta no factura deposito (ADR-053).
        alternativa.costoOut =
            alternativa.esCrossDock || "PLANTA".equals(alternativa.idOrigen)
            ? 0
            : datos.outUsdTn(dia, alternativa.idOrigen, pedido.producto) * toneladas;

        alternativa.costoTHC =
            datos.thcUsdContenedor(dia, terminal, pedido.producto) * contenedores;

        alternativa.costoTerminal =
            datos.costoTerminalUsdContenedor(dia, terminal, pedido.producto) * contenedores;

        DatosEntrada.TarifaSitio tarifa =
            datos.tarifaSitio(dia, terminal, pedido.producto);

        alternativa.costoDespachante =
            tarifa.despachanteUnidad == DatosEntrada.Unidad.USD_PEDIDO
            ? datos.despachanteTarifa(dia, terminal, pedido.producto)
            : datos.despachanteTarifa(dia, terminal, pedido.producto) * contenedores;

        costearHundidoAlternativa(pedido, alternativa);

        alternativa.totalizar();
    }

    void costearHundidoAlternativa(Pedido pedido, AlternativaCircuito alternativa) {
        // Lo que el stock ya pago por estar donde esta. No entra en la vista incremental
        // (seccion 7.1) y por eso se guarda aparte: sirve para explicar la comparacion
        // estrategica, no para decidir la tactica.
        alternativa.costoInHundido = 0;
        alternativa.costoAlmacenajeHundido = 0;
        alternativa.costoFleteHundido = 0;

        if (alternativa.esCrossDock || "PLANTA".equals(alternativa.idOrigen)) {
            return;
        }

        int dia = diaCampania();

        double toneladas = alternativa.toneladas;

        alternativa.costoInHundido =
            datos.inUsdTn(dia, alternativa.idOrigen, pedido.producto) * toneladas;

        alternativa.costoFleteHundido =
            datos.importeFlete(
                dia, "PLANTA", alternativa.idOrigen, pedido.producto,
                toneladas, viajesNecesariosCamion(toneladas));

        alternativa.costoAlmacenajeHundido =
            datos.storageUsdTnDia(dia, alternativa.idOrigen, pedido.producto)
            * toneladaDiaEnStock(alternativa.idOrigen, pedido.producto, toneladas);
    }

    double toneladaDiaEnStock(String idUbicacion, TipoProducto producto, double toneladas) {
        // Tonelada-dia acumulada por las capas FIFO que serviria la alternativa: es el
        // almacenaje que ese stock ya devengo, y depende de cual se consume, no del promedio.
        double pendiente = toneladas;

        double acumulado = 0;

        for (Capa capa : inventario.fifo(idUbicacion, producto)) {

            if (pendiente <= 0.0001) {
                break;
            }

            double toma = Math.min(capa.libres(), pendiente);

            if (toma <= 0) {
                continue;
            }

            // La antiguedad se cuenta dentro del horizonte: una capa de stock inicial ingreso
            // antes del dia 0 y su almacenaje historico no se devenga ni se imputa (ADR-057).
            acumulado += toma * Math.max(0, time() - Math.max(0, capa.diaIngreso));

            pendiente -= toma;
        }

        return acumulado;
    }

    double horasCicloAlternativa(Pedido pedido, AlternativaCircuito alternativa) {
        // Mismo ciclo fisico que arma crearEnvio(), estimado antes de que el envio exista.
        return horasCicloFisico(
            pedido,
            alternativa.idOrigen,
            alternativa.circuito,
            alternativa.esCrossDock,
            alternativa.toneladas
        );
    }

    java.util.List<AlternativaCircuito> ordenarAlternativas(Pedido pedido, java.util.List<AlternativaCircuito> alternativas) {
        // Ranking de lo factible. El orden es la politica: primero servicio, despues el
        // criterio de costo, y el desempate por clave para que dos corridas iguales decidan
        // igual (seccion 6.8).
        final boolean endToEnd = decideEndToEnd();

        final boolean frioPropio =
            politicaSeleccion == DatosEntrada.PoliticaSeleccion.PRIORIDAD_FRIO_PROPIO;

        final boolean exigeServicio =
            datos.escenario.servicioMinimoProyectado > 0;

        java.util.List<AlternativaCircuito> factibles =
            new java.util.ArrayList<AlternativaCircuito>();

        for (AlternativaCircuito alternativa : alternativas) {

            if (alternativa.factible) {
                factibles.add(alternativa);
            }
        }

        java.util.Collections.sort(
            factibles,
            new java.util.Comparator<AlternativaCircuito>() {

                public int compare(AlternativaCircuito a, AlternativaCircuito b) {

                    // Ninguna diferencia de costo compra una entrega tarde mientras exista una
                    // alternativa que llega a tiempo.
                    if (exigeServicio && a.llegaATiempo != b.llegaATiempo) {
                        return a.llegaATiempo ? -1 : 1;
                    }

                    if (frioPropio) {

                        boolean pa = "PLANTA".equals(a.idOrigen) && !a.esCrossDock;
                        boolean pb = "PLANTA".equals(b.idOrigen) && !b.esCrossDock;

                        if (pa != pb) {
                            return pa ? -1 : 1;
                        }
                    }

                    int orden =
                        Double.compare(
                                    a.costoUnitarioSegun(endToEnd),
                                    b.costoUnitarioSegun(endToEnd));

                    return orden != 0 ? orden : a.clave().compareTo(b.clave());
                }
            });

        return factibles;
    }

    double ejecutarAlternativa(Pedido pedido, AlternativaCircuito alternativa) {
        // El plan se ejecuta con el flujo fisico que ya existe: reservar contra el origen
        // elegido, o cruzar por el deposito elegido. El evaluador no mueve producto por su
        // cuenta, asi no puede prometer algo que la cadena no hace. Devuelve las toneladas
        // efectivamente tomadas, que pueden ser menos de las evaluadas (ADR-055).
        if (!alternativa.factible) {
            return 0;
        }

        if (alternativa.esCrossDock) {

            Deposito sitio = buscarDeposito(alternativa.idOrigen);

            return sitio == null
                ? 0
                : ejecutarCrossDockPedido(pedido, sitio, alternativa.toneladas);
        }

        return reservarParcialPedido(
            pedido,
            alternativa.idOrigen,
            alternativa.toneladas,
            alternativa.circuito,
            false,
            "evaluador: " + alternativa.clave());
    }

    double asignarConEvaluador(Pedido pedido) {
        // C6: generar, descartar, costear, ordenar, ejecutar y dejar constancia. Se recorre el
        // ranking porque tomar capacidad puede fallar contra otro pedido del mismo dia; lo que
        // no se pudo tomar queda descartado con su motivo, no elegido en silencio.
        //
        // Con asignacion parcial la vuelta se repite mientras quede saldo: cada iteracion vuelve
        // a generar alternativas porque tomar stock cambia lo que el resto puede prometer. El
        // tope de vueltas es la cantidad de origenes posibles (ADR-055).
        double asignadas = 0;

        int vueltas = 0;

        int maxVueltas = 2 + 2 * depositos.size();

        while (
            pedido.toneladasPendientesAsignar() > 0.0001
            && vueltas < maxVueltas
        ) {
            vueltas++;

            java.util.List<AlternativaCircuito> alternativas =
                generarAlternativas(pedido);

            java.util.List<AlternativaCircuito> ranking =
                ordenarAlternativas(pedido, alternativas);

            double tomadas = 0;

            for (AlternativaCircuito elegida : ranking) {

                tomadas = ejecutarAlternativa(pedido, elegida);

                if (tomadas <= 0.0001) {
                    elegida.descartar("el flujo no pudo tomarla al ejecutar");
                    continue;
                }

                registrarPlan(pedido, alternativas, elegida, tomadas);

                break;
            }

            if (tomadas <= 0.0001) {
                break;
            }

            asignadas += tomadas;
        }

        if (asignadas <= 0.0001) {
            pedidosSinAlternativaFactible++;
        }

        return asignadas;
    }

    void registrarPlan(Pedido pedido, java.util.List<AlternativaCircuito> alternativas, AlternativaCircuito elegida, double toneladasAsignadas) {
        // El plan es la constancia de la decision: que se eligio, contra que se lo comparo y
        // con que numeros. PlanLogistico existia desde el primer dia del proyecto sin ninguna
        // referencia; esta es la funcion que lo pone a vivir (ADR-054).
        PlanLogistico plan = add_planes();

        plan.idPlan = "PL-" + siguienteIdPlan;
        siguienteIdPlan++;

        plan.pedido = pedido;
        plan.terminal = pedido.puertoSalida;
        plan.politica = "" + politicaSeleccion;
        plan.diaDecision = time();
        plan.alternativas = alternativas;
        plan.alternativasEvaluadas = alternativas.size();

        int descartadas = 0;

        for (AlternativaCircuito alternativa : alternativas) {

            if (!alternativa.factible) {
                descartadas++;
            }
        }

        plan.alternativasDescartadas = descartadas;

        alternativasEvaluadasTotal += alternativas.size();
        alternativasDescartadasTotal += descartadas;

        plan.estrategia = elegida.circuito;
        plan.idOrigen = elegida.idOrigen;
        plan.idSitioEstiba = elegida.sitioEstiba;
        plan.toneladas = toneladasAsignadas;
        plan.cantidadContenedores = elegida.contenedores;
        plan.diaEntregaEstimado = elegida.diaEntregaEstimado;
        plan.llegaATiempo = elegida.llegaATiempo;
        plan.tiempoEstimado = horasCicloAlternativa(pedido, elegida);
        plan.factible = true;
        plan.estado = EstadoPlanLogistico.SELECCIONADO;

        plan.origenProducto =
            "PLANTA".equals(elegida.idOrigen)
            ? (Agent) planta
            : (Agent) buscarDeposito(elegida.idOrigen);

        plan.lugarConsolidacion =
            elegida.sitioEstiba.equals(elegida.idOrigen)
            ? plan.origenProducto
            : (Agent) pedido.puertoSalida;

        // La descomposicion viaja al plan concepto por concepto: el total no se copia, lo
        // recalcula el propio plan y despues se exige que coincida.
        plan.costoFleteGuarda = elegida.costoFleteHundido;
        plan.costoAlmacenajeIn = elegida.costoInHundido;
        plan.costoAlmacenajeDiario = elegida.costoAlmacenajeHundido;
        plan.costoAlmacenajeOut = elegida.costoOut;
        plan.costoFleteCrossDock = elegida.costoFleteProducto;
        plan.costoCicloContenedor = elegida.costoRoundTrip;
        plan.costoConsolidacion = elegida.esCrossDock ? 0 : elegida.costoEstiba;
        plan.costoCrossDock = elegida.esCrossDock ? elegida.costoEstiba : 0;
        plan.costoTerminal = elegida.costoTerminal;
        plan.costoTHC = elegida.costoTHC;
        plan.costoDespachante = elegida.costoDespachante;

        plan.recalcularCostos();

        exigirIgual(
            plan.costoTotalEndToEnd,
            elegida.costoEndToEnd,
            "plan " + plan.idPlan + " contra la alternativa elegida");

        plan.motivoSeleccion =
            (decideEndToEnd() ? "menor costo end-to-end factible" : "menor costo incremental factible")
            + " USD " + Math.round(elegida.costoSegun(decideEndToEnd()))
            + " entre " + (alternativas.size() - descartadas) + " factibles"
            + (elegida.llegaATiempo ? "" : " (ninguna llega a tiempo)");

        planesEmitidos++;

        if (!elegida.llegaATiempo) {
            planesTardios++;
        }
    }

    double intentarCrossDockPedido(Pedido pedido) {
        // Sin evaluador, el sitio de cruce lo elige la heuristica de costo de siempre.
        return ejecutarCrossDockPedido(
            pedido,
            seleccionarSitioCrossDock(pedido),
            pedido.toneladasPendientesAsignar()
        );
    }

    AsignacionPedido crearAsignacion(Pedido pedido, String idSitio, EstrategiaLogistica circuito, boolean cruza, String motivo) {
        // Una asignacion es una parte del pedido servida desde un origen (ADR-055). El pedido
        // deja de tener un origen y pasa a tener una lista.
        AsignacionPedido asignacion =
            new AsignacionPedido(
                "ASG-" + siguienteIdAsignacion,
                pedido.codigoPedido,
                idSitio,
                pedido.producto,
                circuito,
                cruza,
                time()
            );

        siguienteIdAsignacion++;

        asignacion.motivoAsignacion = motivo == null ? "" : motivo;
        asignacion.prioridad = pedido.asignaciones.size() + 1;

        pedido.asignaciones.add(asignacion);

        asignacionesCreadas++;

        return asignacion;
    }

    double reservarParcialPedido(Pedido pedido, String idSitio, double toneladasObjetivo, EstrategiaLogistica circuito, boolean cruza, String motivo) {
        // Reserva lo que el sitio pueda dar y conserva lo reservado aunque no alcance para el
        // pedido completo (ADR-055). Es la operacion que reemplaza al todo-o-nada: antes, un
        // pedido de 500 tn con 300 disponibles no reservaba nada y devolvia las 300.
        ultimaAsignacionCreada = null;

        if (
            pedido == null
            || idSitio == null
            || idSitio.isEmpty()
            || toneladasObjetivo <= 0.0001
        ) {
            return 0;
        }

        double pendiente =
            min(toneladasObjetivo, pedido.toneladasPendientesAsignar());

        if (pendiente <= 0.0001) {
            return 0;
        }

        double aReservar =
            min(pendiente, inventario.libre(idSitio, pedido.producto));

        if (aReservar <= 0.0001) {
            return 0;
        }

        AsignacionPedido asignacion =
            crearAsignacion(pedido, idSitio, circuito, cruza, motivo);

        double reservadas =
            inventario.reservar(
                idSitio,
                pedido.producto,
                aReservar,
                asignacion.claveReserva(),
                pedido.codigoPedido,
                time()
            );

        if (reservadas <= 0.0001) {

            // Nada que anotar: la asignacion no existe si no reservo.
            pedido.asignaciones.remove(asignacion);
            asignacionesCreadas--;

            return 0;
        }

        asignacion.toneladasAsignadas = reservadas;
        asignacion.toneladasReservadasActivas = reservadas;

        if (reservadas + 0.0001 < pedido.toneladasSolicitadas) {
            asignacionesParciales++;
        }

        ultimaAsignacionCreada = asignacion;

        confirmarAsignacion(pedido, asignacion);

        marcarLotesReservados(pedido);

        return reservadas;
    }

    double asignarParcialPedido(Pedido pedido) {
        // Un pedido se cubre con los origenes que hagan falta, en uno o varios dias. Devuelve
        // las toneladas asignadas hoy; el saldo sigue siendo demanda para los dias siguientes.
        if (pedido == null || pedido.toneladasPendientesAsignar() <= 0.0001) {
            return 0;
        }

        double antes = pedido.cantidadOrigenes();

        double asignadas =
            usaEvaluador()
            ? asignarConEvaluador(pedido)
            : asignarConPoliticaFija(pedido);

        if (antes <= 1 && pedido.cantidadOrigenes() > 1) {
            pedidosMultiOrigen++;
        }

        // Marca historica: el estado final no alcanza para medir por donde paso el pedido, porque
        // al cierre de campania un pedido completo no se distingue de uno que nunca fue parcial.
        if (
            !pedido.asignaciones.isEmpty()
            && pedido.toneladasPendientesAsignar() > 0.0001
        ) {
            pedido.tuvoReservaParcial = true;
        }

        if (debugPlanificacion && asignadas > 0.0001) {
            traceln(diagnosticoPedido(pedido));
        }

        return asignadas;
    }

    double asignarConPoliticaFija(Pedido pedido) {
        // Conducta previa al evaluador (ADR-054), ahora con reserva parcial: el orden de los
        // candidatos es el de siempre y lo unico que cambia es que se acepta lo que cada uno
        // pueda dar en vez de exigir el pedido completo.
        double asignadas = 0;

        java.util.List<String> candidatos =
            new java.util.ArrayList<String>();

        if (consolidaEnPlanta()) {
            candidatos.add("PLANTA");
        }

        for (Deposito deposito : depositosOrdenadosParaPedido(pedido)) {
            candidatos.add(deposito.idUbicacion);
        }

        for (String idSitio : candidatos) {

            if (pedido.toneladasPendientesAsignar() <= 0.0001) {
                break;
            }

            asignadas +=
                reservarParcialPedido(
                    pedido,
                    idSitio,
                    pedido.toneladasPendientesAsignar(),
                    circuitoDe(idSitio, false),
                    false,
                    "politica " + politicaSeleccion
                );
        }

        return asignadas;
    }

    java.util.List<Deposito> depositosOrdenadosParaPedido(Pedido pedido) {
        // Los depositos con stock libre del producto, del mas barato al mas caro para este
        // pedido. Es el mismo criterio de seleccionarDepositoParaPedido(), pero devuelve la lista
        // completa: con reserva parcial el segundo candidato tambien sirve.
        java.util.List<Deposito> candidatos =
            new java.util.ArrayList<Deposito>();

        for (Deposito deposito : depositos) {

            if (
                deposito.habilitado
                && inventario.libre(deposito.idUbicacion, pedido.producto) > 0.0001
            ) {
                candidatos.add(deposito);
            }
        }

        final Pedido delPedido = pedido;

        java.util.Collections.sort(
            candidatos,
            new java.util.Comparator<Deposito>() {

                public int compare(Deposito a, Deposito b) {

                    int orden =
                        Double.compare(
                            costoEstimadoDesde(delPedido, a),
                            costoEstimadoDesde(delPedido, b)
                        );

                    return orden != 0
                        ? orden
                        : a.idUbicacion.compareTo(b.idUbicacion);
                }
            }
        );

        return candidatos;
    }

    double costoEstimadoDesde(Pedido pedido, Deposito deposito) {
        // Costo estimado de servir el saldo pendiente del pedido desde un deposito: flete al
        // puerto mas estiba, con la unidad de la tarifa. Solo se usa para ordenar candidatos.
        double toneladas =
            min(
                pedido.toneladasPendientesAsignar(),
                inventario.libre(deposito.idUbicacion, pedido.producto)
            );

        if (toneladas <= 0.0001) {
            return Double.POSITIVE_INFINITY;
        }

        int contenedores =
            contenedoresNecesarios(pedido.producto, toneladas);

        double flete =
            deposito.getImporteFletePuerto(
                pedido.puertoSalida,
                pedido.producto,
                toneladas
            );

        double estiba =
            consolidaEnTerminal()
            ? pedido.puertoSalida.getImporteConsolidacion(
                pedido.producto,
                toneladas,
                contenedores
            )
            : deposito.getImporteConsolidacion(
                pedido.producto,
                toneladas,
                contenedores
            );

        return (flete + estiba) / toneladas;
    }

    double toneladasDisponiblesParaAlternativa(String idOrigen, TipoProducto producto, boolean cruza) {
        // Cuanto puede prometer hoy un candidato: stock libre en el origen, o para el cross
        // dock lo que hay libre en planta acotado por el espacio de paso y el cupo del dia.
        if (!cruza) {
            return inventario.libre(idOrigen, producto);
        }

        Deposito sitio = buscarDeposito(idOrigen);

        if (sitio == null || !sitio.habilitado) {
            return 0;
        }

        double capacidadContenedor =
            obtenerCapacidadContenedorTon(obtenerTipoContenedor(producto));

        return min(
            inventario.libre("PLANTA", producto),
            min(
                sitio.getEspacioDisponible(producto),
                capacidadCrossDockLibre(idOrigen) * capacidadContenedor
            )
        );
    }

    AlternativaCircuito alternativaPara(Pedido pedido, double pendiente, String idOrigen, String sitioEstiba, EstrategiaLogistica circuito, boolean cruza) {
        // Una alternativa por el volumen que su origen puede resolver hoy, acotado por el saldo
        // pendiente del pedido. Cero toneladas es una alternativa que existe y se descarta con
        // motivo, no una que desaparece.
        double toneladas =
            min(
                pendiente,
                toneladasDisponiblesParaAlternativa(idOrigen, pedido.producto, cruza)
            );

        toneladas = max(0, toneladas);

        return new AlternativaCircuito(
            idOrigen,
            sitioEstiba,
            circuito,
            cruza,
            toneladas,
            contenedoresNecesarios(pedido.producto, toneladas)
        );
    }

    AsignacionPedido asignacionDe(Pedido pedido, String idAsignacion) {
        // La asignacion de un envio o contenedor. Es la que dice de donde salio el producto y
        // por que circuito, que con varios origenes ya no puede leerse del pedido (ADR-055).
        if (pedido == null || idAsignacion == null || idAsignacion.isEmpty()) {
            return null;
        }

        for (AsignacionPedido asignacion : pedido.asignaciones) {

            if (asignacion.idAsignacion.equals(idAsignacion)) {
                return asignacion;
            }
        }

        return null;
    }

    AsignacionPedido asignacionDeEnvio(Envio envio) {
        return envio == null
            ? null
            : asignacionDe(envio.pedido, envio.idAsignacionPedido);
    }

    boolean permitirUltimoParcial(Pedido pedido) {
        // El contenedor que no se llena se arma solo cuando ya no hay nada que esperar: el
        // pedido esta completamente asignado, vencio, o la campania se termina. Armarlo apenas
        // aparece una asignacion parcial seria despachar medio contenedor por una demora de un
        // dia y pagarlo como si estuviera lleno (ADR-055).
        if (pedido == null) {
            return false;
        }

        return pedido.toneladasPendientesAsignar() <= 0.0001
            || time() >= pedido.diaLimite
            || time() >= duracionCampaniaDias - 1;
    }

    int crearContenedoresParaAsignacion(Pedido pedido, AsignacionPedido asignacion) {
        // Contenerizacion progresiva (ADR-055): se arman contenedores completos con lo que la
        // asignacion tiene reservado y todavia no contenerizado, y el parcial solo cuando ya no
        // puede completarse. Cada contenedor pertenece a una asignacion y a una reserva.
        if (
            pedido == null
            || asignacion == null
            || !asignacion.activa()
            || pedido.puertoSalida == null
        ) {
            return 0;
        }

        pedido.tipoContenedor =
            obtenerTipoContenedor(pedido.producto);

        pedido.capacidadContenedorTon =
            obtenerCapacidadContenedorTon(pedido.tipoContenedor);

        double disponible =
            min(
                asignacion.toneladasReservadasActivas,
                asignacion.toneladasPorContenerizar()
            );

        if (disponible <= 0.0001) {
            return 0;
        }

        String origen = asignacion.idSitioOrigen;

        String sitioDeEstiba =
            sitioEstiba(origen, asignacion.circuito, pedido.puertoSalida);

        // Capas que esta asignacion tiene reservadas, en el mismo orden FIFO en que se van a
        // despachar: recorrerlas en paralelo a los contenedores da el lote de cada uno.
        java.util.List<Capa> reservadas =
            inventario.capasReservadasDe(
                origen,
                pedido.producto,
                asignacion.claveReserva()
            );

        int indiceCapa = 0;

        double saldoCapa =
            reservadas.isEmpty()
            ? 0
            : reservadas.get(0).reservadasDe(asignacion.claveReserva());

        boolean ultimoParcial = permitirUltimoParcial(pedido);

        int creados = 0;

        while (disponible > 0.0001) {

            boolean completo =
                disponible + 0.0001 >= pedido.capacidadContenedorTon;

            if (!completo && !ultimoParcial) {
                break;
            }

            double carga =
                min(pedido.capacidadContenedorTon, disponible);

            pedido.cantidadContenedores++;

            ContenedorExportacion contenedor =
                add_contenedoresExportacion();

            contenedor.idContenedor =
                pedido.codigoPedido + "-C" + pedido.cantidadContenedores;

            contenedor.Pedido = pedido;
            contenedor.producto = pedido.producto;
            contenedor.tipoContenedor = pedido.tipoContenedor;
            contenedor.capacidadTon = pedido.capacidadContenedorTon;
            contenedor.cantidadAsignadaTon = carga;
            contenedor.terminalDestino = pedido.puertoSalida;

            contenedor.idAsignacionPedido = asignacion.idAsignacion;
            contenedor.claveReserva = asignacion.claveReserva();
            contenedor.idSitioOrigen = origen;
            contenedor.circuito = asignacion.circuito;
            contenedor.esCrossDock = asignacion.esCrossDock;

            contenedor.diaProgramadoCrossDock =
                asignacion.esCrossDock ? time() : -1;

            contenedor.lugarConsolidacion =
                sitioDeEstiba.equals("PLANTA")
                ? (Agent) planta
                : (
                    buscarDeposito(sitioDeEstiba) != null
                    ? (Agent) buscarDeposito(sitioDeEstiba)
                    : (Agent) pedido.puertoSalida
                );

            // Ventana maritima (ADR-059): comprometer no es ejecutar. Con la ventana
            // cerrada el contenedor existe planificado y no entra al flujo fisico
            // hasta que abre el retiro del vacio.
            contenedor.estado =
                pedido.ventanaRetiroAbierta
                ? EstadoContenedor.ESPERANDO_PROGRAMACION
                : EstadoContenedor.CREADO;

            planificarPosicionFutura(pedido, sitioDeEstiba);

            contenedor.costoEstimado =
                datos.roundTripUsdContenedor(
                    diaCampania(),
                    pedido.puertoSalida.idUbicacion,
                    origen,
                    pedido.tipoContenedor
                )
                + importeServicioEstiba(
                    sitioDeEstiba,
                    asignacion.esCrossDock,
                    pedido.producto,
                    carga,
                    1
                );

            double resto = carga;
            double mayorAporte = 0;

            while (resto > 0.0001 && indiceCapa < reservadas.size()) {

                double toma = min(resto, saldoCapa);

                if (toma > mayorAporte) {
                    mayorAporte = toma;
                    contenedor.lote =
                        buscarLote(reservadas.get(indiceCapa).idLote);
                }

                resto -= toma;
                saldoCapa -= toma;

                if (saldoCapa <= 0.0001) {
                    indiceCapa++;

                    saldoCapa =
                        indiceCapa < reservadas.size()
                        ? reservadas.get(indiceCapa)
                            .reservadasDe(asignacion.claveReserva())
                        : 0;
                }
            }

            pedido.contenedores.add(contenedor);

            asignacion.toneladasContenerizadas += carga;

            disponible -= carga;
            creados++;
        }

        return creados;
    }

    String diagnosticoPedido(Pedido pedido) {
        // Estado del pedido en una linea, para poder auditar la asignacion parcial (seccion 13).
        if (pedido == null) {
            return "";
        }

        return "[dia " + (int) floor(time()) + "] " + pedido.codigoPedido
            + " " + pedido.producto
            + " estado=" + pedido.estado
            + " solicitado=" + Math.round(pedido.toneladasSolicitadas)
            + " entregado=" + Math.round(pedido.toneladasEntregadas)
            + " en_proceso=" + Math.round(pedido.toneladasEnProceso())
            + " reserva_activa=" + Math.round(pedido.toneladasReservadasActivas())
            + " pend_asignar=" + Math.round(pedido.toneladasPendientesAsignar())
            + " pend_entregar=" + Math.round(pedido.toneladasPendientesEntregar())
            + " origenes=" + pedido.cantidadOrigenes();
    }

    void validarBalancePedidos() {
        // C-01: lo solicitado se explica siempre por lo entregado, lo que viaja, lo reservado y
        // lo que falta asignar. Si no cierra, alguna transicion perdio toneladas (seccion 16).
        for (Pedido pedido : pedidos) {

            if (pedido.estado == EstadoPedido.CANCELADO) {
                continue;
            }

            double suma =
                pedido.toneladasEntregadas
                + pedido.toneladasEnProceso()
                + pedido.toneladasReservadasActivas()
                + pedido.toneladasPendientesAsignar();

            if (abs(suma - pedido.toneladasSolicitadas) > 0.0001) {
                error(
                    "Balance del pedido " + pedido.codigoPedido
                    + " el dia " + (int) floor(time())
                    + ": solicitado " + pedido.toneladasSolicitadas
                    + " contra " + suma
                    + " (" + diagnosticoPedido(pedido) + ")"
                );
            }
        }
    }

    double toneladasASacarPreventivamente(TipoProducto producto) {
        // Componente preventivo de la politica FLEXIBLE (ADR-056): con el stock proyectado a
        // la vista, si se va a tocar el umbral de alerta conviene bajar hasta el objetivo antes
        // de estar contra la capacidad, porque sacar producto toma dias de flota.
        //
        // No es la politica REACTIVA: REACTIVA mira el stock de hoy y aca se mira el proyectado
        // con el forecast, y sigue conviviendo con los motivos de desborde y de servicio.
        double capacidad = planta.getCapacidad(producto);

        if (capacidad <= 0) {
            return 0;
        }

        double alerta =
            capacidad * datos.escenario.umbralAlertaPct / 100;

        double objetivo =
            capacidad * datos.escenario.umbralObjetivoPct / 100;

        double proyectado =
            planta.getStock(producto)
            + forecastProduccion(producto, datos.escenario.diasForecast);

        // Debajo de la alerta el frio propio no cuesta nada: no hay motivo para gastar frio
        // de terceros.
        if (proyectado < alerta) {
            return 0;
        }

        return max(0, proyectado - objetivo);
    }

    boolean plantaEnSobrecargaCritica(TipoProducto producto) {
        // Sobre el umbral de sobrecarga la planta esta en riesgo: no se transfiere mas volumen
        // (ya lo cubre el componente de desborde, que compara contra el 100 %), pero la eleccion
        // del destino deja de priorizar el costo y prioriza que el producto salga (ADR-056).
        double capacidad = planta.getCapacidad(producto);

        return capacidad > 0
            && planta.getStock(producto)
                > capacidad * datos.escenario.umbralSobrecargaPct / 100 + 0.0001;
    }

    String motivoDescarteDeposito(Deposito deposito, TipoProducto producto, double toneladas) {
        // Por que un deposito no puede recibir hoy. Vacio significa que puede: es el motivo que
        // necesita el diagnostico para no tener que adivinar cual filtro lo descarto (seccion 13.4).
        if (deposito == null) {
            return "INEXISTENTE";
        }

        if (!deposito.habilitado) {
            return "NO_HABILITADO";
        }

        if (deposito.getCapacidad(producto) <= 0.0001) {
            return "SIN_CAPACIDAD_PRODUCTO";
        }

        if (deposito.getEspacioDisponible(producto) <= 0.0001) {
            return "SIN_ESPACIO";
        }

        int dia = diaCampania();

        if (!datos.hayTarifaFlete(dia, "PLANTA", deposito.idUbicacion, producto)) {
            return "TARIFA_INEXISTENTE";
        }

        if (!datos.hayTarifaSitio(dia, deposito.idUbicacion, producto)) {
            return "TARIFA_SITIO_INEXISTENTE";
        }

        if (toneladas > 0.0001 && !flotaProductoAlcanza("PLANTA", deposito.idUbicacion, toneladas)) {
            return "SIN_FLOTA";
        }

        return "";
    }

    String diagnosticoDepositos(TipoProducto producto, double toneladas) {
        // Una linea por deposito con lo que decide la distribucion: capacidad, stock, espacio,
        // tarifas, costo estimado y motivo de descarte (seccion 13.4). Es la unica forma de
        // explicar por que un destino con lugar no se usa.
        StringBuilder texto = new StringBuilder();

        texto.append(
            "[dia " + (int) floor(time()) + "] destinos para " + producto
            + " objetivo " + Math.round(toneladas) + " tn"
            + (plantaEnSobrecargaCritica(producto) ? " (planta en sobrecarga critica)" : "")
        );

        for (Deposito deposito : depositos) {

            String motivo =
                motivoDescarteDeposito(deposito, producto, 0);

            double posible =
                min(toneladas, deposito.getEspacioDisponible(producto));

            double costo =
                motivo.isEmpty() && posible > 0.0001
                ? calcularCostoPlantaDeposito(deposito, producto, posible)
                    + posible
                        * deposito.getTarifaAlmacenamiento(producto)
                        * diasEstimadosAlmacenamiento
                : Double.NaN;

            texto.append(
                "\n  " + deposito.idUbicacion
                + " capacidad=" + Math.round(deposito.getCapacidad(producto))
                + " stock=" + Math.round(inventario.stock(deposito.idUbicacion, producto))
                + " espacio=" + Math.round(deposito.getEspacioDisponible(producto))
                + " tarifa_flete="
                + (datos.hayTarifaFlete(diaCampania(), "PLANTA", deposito.idUbicacion, producto)
                    ? "si" : "no")
                + " costo_estimado=" + (Double.isNaN(costo) ? "-" : "" + Math.round(costo))
                + " elegible=" + (motivo.isEmpty() ? "si" : "no")
                + " motivo=" + (motivo.isEmpty() ? "-" : motivo)
            );
        }

        return texto.toString();
    }

    int pedidosParcialmenteReservados() {
        // Pedidos que en algun momento quedaron comprometidos a medias: parte reservada y parte
        // todavia sin asignar (ADR-055). Se cuenta la marca historica, no el estado final.
        int cantidad = 0;

        for (Pedido pedido : pedidos) {

            if (pedido.tuvoReservaParcial) {
                cantidad++;
            }
        }

        return cantidad;
    }

    int pedidosParcialmenteEntregados() {
        // Idem: pedidos que recibieron una entrega que no los completo, aunque despues cerraran.
        int cantidad = 0;

        for (Pedido pedido : pedidos) {

            if (pedido.tuvoEntregaParcial) {
                cantidad++;
            }
        }

        return cantidad;
    }

    double toneladasPendientesAsignarTotal() {
        double total = 0;

        for (Pedido pedido : pedidos) {

            if (pedido.estado != EstadoPedido.CANCELADO) {
                total += pedido.toneladasPendientesAsignar();
            }
        }

        return total;
    }

    double toneladasPendientesEntregarTotal() {
        double total = 0;

        for (Pedido pedido : pedidos) {

            if (pedido.estado != EstadoPedido.CANCELADO) {
                total += pedido.toneladasPendientesEntregar();
            }
        }

        return total;
    }

    void validarBalanceProducido() {
        // C-02: todo lo disponible esta en algun lado. Sin perdida de producto (ADR-048), el
        // stock inicial mas lo producido es lo que hay en planta, en depositos, viajando y ya
        // entregado (ADR-057).
        double producido =
            stockInicialCargadoTn
            + planta.produccionAcumuladaJugo
            + planta.produccionAcumuladaCascara
            + planta.produccionAcumuladaAceite;

        double enStock = 0;

        for (TipoProducto producto : TipoProducto.values()) {
            enStock += inventario.stockProducto(producto);
        }

        double enProceso = 0;

        for (Pedido pedido : pedidos) {
            enProceso += pedido.toneladasEnProceso();
        }

        double entregado = 0;

        for (Pedido pedido : pedidos) {
            entregado += pedido.toneladasEntregadas;
        }

        if (abs(producido - (enStock + enProceso + entregado)) > 0.001) {
            error(
                "Balance de producto el dia " + (int) floor(time())
                + ": stock inicial + producido " + producido
                + " contra stock " + enStock
                + " + en proceso " + enProceso
                + " + entregado " + entregado
            );
        }
    }

    void cargarStockInicial() {
        // Inventario preexistente al arranque (ADR-057). No es produccion ni transferencia:
        // crea el lote historico y sus capas directamente en el inventario, sin devengar
        // ningun costo pasado. Se llama desde el arranque del agente, despues de
        // cargarDatosEntrada() y antes del primer pasoDiario_accion().
        validarStockInicial();

        java.util.Map<String, LoteProducto> porCodigo =
            new java.util.HashMap<String, LoteProducto>();

        for (DatosEntrada.StockInicial fila : datos.stockInicial) {

            LoteProducto lote = porCodigo.get(fila.claveLote());

            if (lote == null) {
                lote = add_lotes();

                lote.idLote = siguienteIdLote;
                siguienteIdLote++;

                lote.codigoLoteExterno = fila.codigoLote;
                lote.esStockInicial = true;
                lote.producto = fila.producto;
                lote.cliente = fila.cliente;
                lote.calidad = fila.calidad;
                lote.diaProduccion = fila.diaProduccion;
                lote.costoAcumulado = 0;
                lote.pedidoAsignado = null;

                // Un lote historico no recibe produccion de campania: queda cerrado y sin
                // objetivo, asi que la regla de cierre de crearLoteEnPlanta() no lo alcanza.
                lote.estadoComercial = EstadoComercialLote.CERRADO;
                lote.toneladasObjetivo = 0;

                // Para un lote inicial 'toneladasIniciales' es el total historico cargado, no
                // produccion de campania (ADR-047 y ADR-057). Sigue siendo un acumulador
                // monotono; el origen se distingue con esStockInicial.
                lote.toneladasIniciales = 0;

                porCodigo.put(fila.claveLote(), lote);
            }

            inventario.ingresar(
                lote.idLote,
                fila.producto,
                fila.idUbicacion,
                fila.toneladas,
                fila.diaIngreso,
                fila.diaProduccion
            );

            lote.toneladasIniciales += fila.toneladas;
            lote.diaProduccion = min(lote.diaProduccion, fila.diaProduccion);

            // Ningun costo lee estos dos campos hoy, pero la presentacion del modelo si.
            if (!fila.idUbicacion.equals("PLANTA")) {
                lote.depositoActual = buscarDeposito(fila.idUbicacion);
                lote.diaIngresoDeposito = fila.diaIngreso;
            }
        }

        for (LoteProducto lote : porCodigo.values()) {
            actualizarUbicacionLote(lote);
        }

        stockInicialCargadoTn = datos.stockInicialTn();

        validarInventario();

        if (stockInicialCargadoTn > 0.0001) {
            traceln(
                "Stock inicial cargado: " + Math.round(stockInicialCargadoTn)
                + " tn en " + porCodigo.size() + " lotes historicos y "
                + datos.stockInicial.size() + " capas."
            );
        }
    }

    void validarStockInicial() {
        // Mismo criterio que DatosEntrada.validar() (ADR-037): la lista completa de errores
        // y el arranque se detiene. La identidad, las fechas y la ubicacion ya se validaron
        // con los datos; aca se valida la capacidad efectiva, que necesita los agentes con
        // los factores del escenario ya aplicados.
        java.util.List<String> errores = new java.util.ArrayList<String>();

        java.util.Set<String> vistos = new java.util.HashSet<String>();

        for (DatosEntrada.StockInicial fila : datos.stockInicial) {

            String clave = fila.idUbicacion + "|" + fila.producto;

            if (!vistos.add(clave)) {
                continue;
            }

            double inicial = datos.stockInicialTn(fila.idUbicacion, fila.producto);

            double capacidad = fila.idUbicacion.equals("PLANTA")
                ? planta.getCapacidad(fila.producto)
                : (buscarDeposito(fila.idUbicacion) == null
                    ? 0
                    : buscarDeposito(fila.idUbicacion).getCapacidad(fila.producto));

            String donde = fila.idUbicacion + " / " + fila.producto;

            if (capacidad <= 0) {
                errores.add(
                    "StockInicial en " + donde + ": la ubicacion no tiene capacidad para el producto."
                );
                continue;
            }

            if (inicial <= capacidad + 0.0001) {
                continue;
            }

            // La planta es frio propio y su capacidad nominal es un umbral de lectura, no un
            // tope (ADR-048): arrancar por encima del nominal es un dato valido y se mide con
            // los indicadores de sobrecarga. El deposito es de terceros y su capacidad es dura.
            if (fila.idUbicacion.equals("PLANTA")) {
                traceln(
                    "Aviso: el stock inicial de " + donde + " (" + Math.round(inicial)
                    + " tn) supera la capacidad nominal (" + Math.round(capacidad)
                    + " tn). La campania arranca en sobrecarga (ADR-048)."
                );
            } else {
                errores.add(
                    "StockInicial en " + donde + ": " + Math.round(inicial)
                    + " tn superan la capacidad efectiva de " + Math.round(capacidad) + " tn."
                );
            }
        }

        if (errores.isEmpty()) {
            return;
        }

        String detalle = "";

        for (String e : errores) {
            detalle += "\n  - " + e;
        }

        error("El stock inicial no cumple el contrato de datos (" + errores.size() + "):" + detalle);
    }

    double stockInicialRemanenteTn() {
        // Lo que queda fisicamente de los lotes historicos. La capa guarda idLote, asi que
        // el remanente no necesita un acumulador propio.
        double total = 0;

        for (LoteProducto lote : lotes) {

            if (lote.esStockInicial) {
                total += inventario.stockLote(lote.idLote);
            }
        }

        return total;
    }

    double stockInicialConsumidoTn() {
        return max(0, stockInicialCargadoTn - stockInicialRemanenteTn());
    }

    double produccionCampaniaTn() {
        return planta.produccionAcumuladaJugo
            + planta.produccionAcumuladaCascara
            + planta.produccionAcumuladaAceite;
    }

    double disponibilidadTotalTn() {
        // La disponibilidad de la campania es el stock que ya estaba mas lo producido.
        return stockInicialCargadoTn + produccionCampaniaTn();
    }

    double deficitEstructuralTn() {
        // Lo que la demanda pide y la campania no puede cubrir ni con stock inicial ni
        // con produccion planificada. Es un dato de entrada, no un resultado del modelo.
        double total = 0;

        for (TipoProducto producto : TipoProducto.values()) {
            total += datos.deficitEstructuralTn(producto);
        }

        return total;
    }

    double demandaPlanificadaTn() {
        double total = 0;

        for (TipoProducto producto : TipoProducto.values()) {
            total += datos.demandaPlanificadaTn(producto);
        }

        return total;
    }

    double horasCicloFisico(Pedido pedido, String idOrigen, EstrategiaLogistica circuito, boolean esCrossDock, double toneladas) {
        // Ciclo fisico de un circuito, sin depender de que exista la alternativa o el envio:
        // lo usan el evaluador (ex ante) y la ventana maritima (holgura contra el cut-off).
        boolean granel = circuito == EstrategiaLogistica.CONSOLIDACION_TERMINAL;

        String terminal = pedido.puertoSalida.idUbicacion;

        double velocidad = datos.escenario.velocidadCamionKmh;

        DatosEntrada.Ubicacion origen = datos.ubicacion(idOrigen);

        DatosEntrada.Ubicacion puerto = datos.ubicacion(terminal);

        double distancia = datos.distanciaKm(idOrigen, terminal);

        double horas = 0;

        if (esCrossDock) {
            horas += datos.distanciaKm("PLANTA", idOrigen) / velocidad;
        }

        horas +=
            granel
            ? toneladas / origen.velocidadCargaTnHora
            : toneladas / origen.velocidadConsolidacionTnHora;

        // Circuitos 1 a 3: el portacontenedor sale vacio de la terminal antes de cargar.
        horas += granel ? 0 : distancia / velocidad;

        horas += distancia / velocidad;

        horas += toneladas / puerto.velocidadDescargaTnHora;

        horas += granel ? toneladas / puerto.velocidadConsolidacionTnHora : 0;

        return horas;
    }

    double tiempoLogisticoEstimadoDias(Pedido pedido, AsignacionPedido asignacion) {
        // Dias entre que el circuito puede empezar y el contenedor entra a la terminal
        // (ADR-059): un dia para programar y armar, mas el ciclo fisico del circuito.
        if (asignacion == null) {
            return 1;
        }

        return 1
            + horasCicloFisico(
                pedido,
                asignacion.idSitioOrigen,
                asignacion.circuito,
                asignacion.esCrossDock,
                asignacion.toneladasAsignadas
            ) / 24.0;
    }

    double tiempoLogisticoMinimoDias(Pedido pedido) {
        // El circuito mas rapido que el pedido tiene comprometido hoy. Sin asignaciones
        // todavia no hay circuito elegido y se estima con el deposito por defecto, que es
        // lo que el pedido usaria si tuviera que salir ahora.
        double mejor = Double.POSITIVE_INFINITY;

        for (AsignacionPedido asignacion : pedido.asignaciones) {

            if (!asignacion.cancelada) {
                mejor = min(mejor, tiempoLogisticoEstimadoDias(pedido, asignacion));
            }
        }

        if (mejor < Double.POSITIVE_INFINITY) {
            return mejor;
        }

        double toneladas =
            min(pedido.toneladasSolicitadas, max(1, pedido.capacidadContenedorTon));

        double horas = Double.POSITIVE_INFINITY;

        for (Deposito deposito : depositos) {

            if (deposito.habilitado) {
                horas =
                    min(
                        horas,
                        horasCicloFisico(
                            pedido,
                            deposito.idUbicacion,
                            EstrategiaLogistica.CONSOLIDACION_DEPOSITO,
                            false,
                            toneladas
                        )
                    );
            }
        }

        if (horas == Double.POSITIVE_INFINITY) {
            horas =
                horasCicloFisico(
                    pedido, "PLANTA", EstrategiaLogistica.CONSOLIDACION_PLANTA, false, toneladas);
        }

        return 1 + horas / 24.0;
    }

    void actualizarVentanasRetiroDelDia() {
        // Paso 2b del dia (ADR-059). Separa conocer de poder ejecutar: hasta hoy el pedido
        // pudo reservar, asignar y hacer transferir producto, pero el vacio no podia salir de
        // la terminal. Al abrir la ventana, lo que estaba planificado entra al flujo fisico.
        for (Pedido pedido : pedidos) {

            if (
                pedido.ventanaRetiroAbierta
                || pedido.estado == EstadoPedido.CANCELADO
                || time() + 0.0001 < pedido.diaAperturaRetiroVacio
            ) {
                continue;
            }

            pedido.ventanaRetiroAbierta = true;

            // La holgura del pedido se mide una sola vez, el dia en que su ejecucion puede
            // empezar: es la pregunta de dimensionamiento (¿la ventana alcanza?), no un
            // indicador que cambia con el reloj.
            double holgura =
                pedido.diaLimite - time() - tiempoLogisticoMinimoDias(pedido);

            holguraAcumuladaDias += holgura;
            pedidosConHolguraMedida++;

            if (holgura < 0) {

                pedidosVentanaInviable++;

                if (pedidosVentanaInviable <= 10) {
                    traceln(
                        "Dia " + diaCampania()
                        + ": la ventana del pedido " + pedido.codigoPedido
                        + " no alcanza para el cut-off (holgura "
                        + String.format("%.2f", holgura) + " dias, buque "
                        + (pedido.buque.isEmpty() ? "sin identificar" : pedido.buque) + ")."
                    );
                }
            }

            // Lo planificado mientras la ventana estaba cerrada arranca hoy.
            for (ContenedorExportacion contenedor : pedido.contenedores) {

                if (contenedor.estado == EstadoContenedor.CREADO) {
                    contenedor.estado = EstadoContenedor.ESPERANDO_PROGRAMACION;
                }
            }
        }
    }

    void registrarPerdidaDeCutoff() {
        // Paso 11b del dia (ADR-059). El cut-off no es el fin del pedido: el saldo sigue
        // buscando salir y se entrega tarde, que es lo que en la realidad rolea al buque
        // siguiente. Lo que se registra aca es que ese buque se perdio.
        for (Pedido pedido : pedidos) {

            if (
                pedido.perdioCutoff
                || pedido.estado == EstadoPedido.CANCELADO
                || time() <= pedido.diaLimite + 0.0001
            ) {
                continue;
            }

            if (pedido.estaCompleto()) {
                continue;
            }

            pedido.perdioCutoff = true;
            pedidosPerdieronCutoff++;

            if ("CANCELAR".equals(datos.escenario.politicaReprogramacionBuque)) {

                // Politica dura: lo que no llega al buque no viaja. El saldo se cancela y
                // deja de competir por recursos.
                pedido.estado = EstadoPedido.CANCELADO;

            } else {

                // Politica por defecto: el saldo se rolea y sigue hasta entregarse.
                pedido.reprogramado = true;
                pedido.cantidadReprogramaciones++;
            }
        }
    }

    void planificarPosicionFutura(Pedido pedido, String sitioEstiba) {
        // Reserva de capacidad futura (ADR-059): al comprometer un contenedor se le busca un
        // dia de estiba dentro de su ventana, contra la capacidad declarada del sitio. No
        // restringe la ejecucion, que sigue decidiendose dia a dia con la capacidad real: lo
        // que hace es avisar antes del cut-off que la capacidad no da.
        if (!datos.escenario.permiteReservaCapacidadFutura) {
            return;
        }

        int capacidad = (int) Math.floor(datos.capacidadConsolidacionDia(sitioEstiba));

        if (capacidad <= 0) {
            return;
        }

        LinkedHashMap<Integer, Integer> porDia = posicionesPlanificadas.get(sitioEstiba);

        if (porDia == null) {
            porDia = new LinkedHashMap<Integer, Integer>();
            posicionesPlanificadas.put(sitioEstiba, porDia);
        }

        int desde = (int) Math.max(diaCampania(), Math.floor(pedido.diaAperturaRetiroVacio));
        int hasta = (int) Math.floor(pedido.diaLimite);

        for (int dia = desde; dia <= hasta; dia++) {

            Integer usadas = porDia.get(dia);
            int ocupadas = usadas == null ? 0 : usadas.intValue();

            if (ocupadas < capacidad) {
                porDia.put(dia, ocupadas + 1);
                return;
            }
        }

        // Ningun dia de la ventana tiene posicion libre: el contenedor va a existir igual,
        // pero se sabe hoy que no tiene donde armarse antes del cut-off.
        contenedoresSinPosicionFutura++;
    }

    double holguraContenedor(ContenedorExportacion contenedor) {
        // Dias que le sobran al contenedor para entrar a la terminal antes del cut-off.
        // Negativa significa que ya no llega ni empezando ahora.
        Pedido pedido = contenedor.Pedido;

        if (pedido == null) {
            return Double.POSITIVE_INFINITY;
        }

        AsignacionPedido asignacion = null;

        for (AsignacionPedido a : pedido.asignaciones) {

            if (a.idAsignacion.equals(contenedor.idAsignacionPedido)) {
                asignacion = a;
            }
        }

        return pedido.diaLimite
            - time()
            - tiempoLogisticoEstimadoDias(pedido, asignacion);
    }

    double servicioPorToneladaCutoff() {
        // Servicio por tonelada contra el cut-off fisico (ADR-059): un pedido entregado a
        // medias no es un pedido servido, pero las toneladas que si llegaron al buque no
        // desaparecen del indicador.
        double solicitadas = 0;

        for (Pedido pedido : pedidos) {

            if (pedido.estado != EstadoPedido.CANCELADO) {
                solicitadas += pedido.toneladasSolicitadas;
            }
        }

        return solicitadas <= 0 ? 0 : toneladasEntregadasAntesCutoff / solicitadas;
    }

    int buquesCumplidos() {
        // Un buque se cumple cuando ningun pedido suyo perdio el cut-off. Los pedidos sin
        // buque identificado no cuentan: no hay nada que cumplir o perder.
        LinkedHashMap<String, Boolean> porBuque = new LinkedHashMap<String, Boolean>();

        for (Pedido pedido : pedidos) {

            if (pedido.buque == null || pedido.buque.isEmpty()) {
                continue;
            }

            Boolean ok = porBuque.get(pedido.buque);
            boolean cumple = !pedido.perdioCutoff;

            porBuque.put(pedido.buque, (ok == null ? true : ok.booleanValue()) && cumple);
        }

        int cumplidos = 0;

        for (Boolean ok : porBuque.values()) {
            cumplidos += ok.booleanValue() ? 1 : 0;
        }

        return cumplidos;
    }

    int buquesPerdidos() {
        LinkedHashSet<String> buques = new LinkedHashSet<String>();

        for (Pedido pedido : pedidos) {

            if (pedido.buque != null && !pedido.buque.isEmpty()) {
                buques.add(pedido.buque);
            }
        }

        return buques.size() - buquesCumplidos();
    }

    double holguraPromedioDias() {
        // Promedio de la holgura medida el dia en que cada ventana abrio (ADR-059).
        return pedidosConHolguraMedida == 0
            ? 0
            : holguraAcumuladaDias / pedidosConHolguraMedida;
    }

    int contenedoresPlanificadosSinEjecutar() {
        // Contenedores comprometidos que todavia esperan que abra su ventana de retiro.
        return contarContenedores(EstadoContenedor.CREADO);
    }

    // ----- Eventos -----

    // evento pasoDiario [timeout cyclic] cada 1 day
    void pasoDiario_accion() {
        // Secuencia diaria del modelo (ADR-034). El orden es parte de la
        // definicion: cambiarlo cambia el costo y el servicio del dia.
        refrescarTarifasDelDia();                // 0. tarifa vigente de hoy (ADR-051)
        abrirFlotaDelDia();                      // 0b. abrir la capacidad de flota del dia
        producirEnPlantas();                     // 1. producir
        registrarPedidosDelDia();                // 2. planificar y comprometer
        actualizarVentanasRetiroDelDia();        // 2b. abrir la ventana de retiro del vacio
        abrirPosicionesConsolidacionDelDia();    // 3. abrir la capacidad de estiba del dia
        abrirPosicionesCrossDockDelDia();        // 4. abrir la capacidad de cross dock del dia
        programarCrossDockDelDia();              // 5. cruzar lo que no necesita guardarse
        revisarTransferenciasPlanta();           // 6. sacar de planta solo lo necesario
        revisarPedidosPendientes();              // 7. reservar contra stock
        prepararPedidosReservados();             // 8. armar los contenedores del pedido
        despacharContenedoresPendientes();       // 9. consolidar y despachar lo que entra
        devengarAlmacenamientoDiario();          // 10. devengar almacenaje
        devengarOportunidadFrioPropio();         // 10b. devengar el uso del frio propio
        registrarOcupacionPlanta();              // 10c. medir la sobrecarga del dia
        registrarAtrasos();                      // 11. registrar indicadores del dia
        registrarPerdidaDeCutoff();              // 11b. que pedidos perdieron su buque
        validarInventario();              // invariantes de las capas (ADR-023)
        validarBalancePedidos();          // C-01: el pedido cierra por partes (ADR-055)
        validarBalanceProducido();        // C-02: nada de lo producido se pierde (ADR-048)
        reconciliarCostos();              // los totales explican cargo por cargo (ADR-052)
    }
}
