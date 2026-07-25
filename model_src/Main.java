// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Main extends Agent {

    // ----- Parámetros -----
    String idEscenario = "E-00";
    int duracionCampaniaDias = 183;
    long semillaBase = 1;
    double variabilidadProduccion = 0.15;
    double variabilidadDemanda = 0.20;
    int pedidosPorCampania = 40;
    double toneladasMediasPedido = 400;
    int plazoPedidoDias = 15;
    double costoFijoViajePD = 150;
    double costoKmPD = 1.2;
    double costoTnPD = 2.0;
    double diasEstimadosAlmacenamiento = 30;

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

    // ----- Colecciones -----
    ArrayList<Terminal> terminales = new ArrayList<Terminal>();
    ArrayList<Deposito> depositos = new ArrayList<Deposito>();

    // ----- Objetos embebidos (poblaciones y bloques de flowchart) -----
    //  planta
    //  lotes
    //  depFrinoa
    //  depNorry
    //  depBoreas
    //  depRuta9
    //  depDodero
    //  terminalZarate
    //  terminalT4
    //  pedidos
    //  envios
    //  flotaCamiones
    //  colaCamiones
    //  tomarCamion
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

        LoteProducto lote = add_lotes();

        lote.idLote = siguienteIdLote;
        siguienteIdLote++;

        lote.producto = producto;
        lote.toneladasIniciales = toneladas;
        lote.toneladasDisponibles = toneladas;
        lote.diaProduccion = time();
        lote.estado = EstadoLote.EN_PLANTA;
        lote.ubicacionActual = origen;
        lote.costoAcumulado = 0;
        lote.pedidoAsignado = null;

        return lote;
    }

    double toneladasLotesEnPlanta(TipoProducto producto) {
        double total = 0;

        for (LoteProducto lote : lotes) {

            if (
                lote.producto == producto
                && lote.estado == EstadoLote.EN_PLANTA
                && lote.ubicacionActual == planta
            ) {
                total += lote.toneladasDisponibles;
            }
        }

        return total;
    }

    int cantidadLotesEnPlanta(TipoProducto producto) {
        int cantidad = 0;

        for (LoteProducto lote : lotes) {

            if (
                lote.producto == producto
                && lote.estado == EstadoLote.EN_PLANTA
                && lote.ubicacionActual == planta
            ) {
                cantidad++;
            }
        }

        return cantidad;
    }

    double calcularCostoPlantaDeposito(Deposito deposito, double toneladas) {
        if (deposito == null || toneladas <= 0) {
            return Double.POSITIVE_INFINITY;
        }

        return costoFijoViajePD
            + datos.distanciaKm("PLANTA", deposito.idUbicacion) * costoKmPD
            + toneladas * costoTnPD;
    }

    Deposito seleccionarDeposito(TipoProducto producto, double toneladas) {
        Deposito mejorDeposito = null;
        double menorCosto = Double.POSITIVE_INFINITY;

        for (Deposito deposito : depositos) {

            if (!deposito.puedeRecibir(producto, toneladas)) {
                continue;
            }

            double costoFlete =
                calcularCostoPlantaDeposito(deposito, toneladas);

            double costoAlmacenamientoEstimado =
                toneladas
                * deposito.getTarifaAlmacenamiento(producto)
                * diasEstimadosAlmacenamiento;

            double costoEstimado =
                costoFlete + costoAlmacenamientoEstimado;

            if (costoEstimado < menorCosto) {
                menorCosto = costoEstimado;
                mejorDeposito = deposito;
            }
        }

        return mejorDeposito;
    }

    LoteProducto buscarLoteMasAntiguoEnPlanta(TipoProducto producto) {
        LoteProducto seleccionado = null;
        double menorDia = Double.POSITIVE_INFINITY;

        for (LoteProducto lote : lotes) {

            if (
                lote.producto == producto
                && lote.estado == EstadoLote.EN_PLANTA
                && lote.ubicacionActual == planta
                && lote.toneladasDisponibles > 0
            ) {
                if (lote.diaProduccion < menorDia) {
                    menorDia = lote.diaProduccion;
                    seleccionado = lote;
                }
            }
        }

        return seleccionado;
    }

    boolean transferirLoteCompleto(LoteProducto lote, Deposito destino) {
        if (lote == null || destino == null) {
            return false;
        }

        if (
            lote.estado != EstadoLote.EN_PLANTA
            || lote.ubicacionActual != planta
            || lote.toneladasDisponibles <= 0
        ) {
            return false;
        }

        double toneladas = lote.toneladasDisponibles;

        if (!destino.puedeRecibir(lote.producto, toneladas)) {
            return false;
        }

        boolean retirado =
            planta.retirarStock(lote.producto, toneladas);

        if (!retirado) {
            return false;
        }

        boolean recibido =
            destino.recibirProducto(lote.producto, toneladas);

        if (!recibido) {
            // Reponer stock en planta si la recepción falla
            planta.agregarStock(lote.producto, toneladas);
            return false;
        }

        lote.estado = EstadoLote.EN_DEPOSITO;
        lote.ubicacionActual = destino;
        lote.depositoActual = destino;
        lote.diaIngresoDeposito = time();

        double costoViaje =
            calcularCostoPlantaDeposito(destino, toneladas);

        lote.costoAcumulado += costoViaje;
        costoFletePlantaDeposito += costoViaje;

        toneladasTransferidasDepositos += toneladas;
        cantidadTransferenciasDepositos++;

        return true;
    }

    void transferirProductoADepositos(TipoProducto producto, double toneladasObjetivo) {
        double pendiente = toneladasObjetivo;

        while (pendiente > 0.0001) {

            LoteProducto lote =
                buscarLoteMasAntiguoEnPlanta(producto);

            if (lote == null) {
                break;
            }

            // En esta primera versión se transfieren lotes completos
            double toneladasLote = lote.toneladasDisponibles;

            if (toneladasLote > pendiente) {
                break;
            }

            Deposito destino =
                seleccionarDeposito(producto, toneladasLote);

            if (destino == null) {
                break;
            }

            boolean transferido =
                transferirLoteCompleto(lote, destino);

            if (!transferido) {
                break;
            }

            pendiente -= toneladasLote;
        }
    }

    void revisarTransferenciasPlanta() {
        // JUGO
        if (planta.stockJugo >= planta.nivelActivacionJugo) {

            double toneladas =
                planta.stockJugo - planta.stockObjetivoJugo;

            transferirProductoADepositos(
                TipoProducto.JUGO,
                toneladas
            );
        }


        // CASCARA
        if (planta.stockCascara >= planta.nivelActivacionCascara) {

            double toneladas =
                planta.stockCascara - planta.stockObjetivoCascara;

            transferirProductoADepositos(
                TipoProducto.CASCARA,
                toneladas
            );
        }


        // ACEITE
        if (planta.stockAceite >= planta.nivelActivacionAceite) {

            double toneladas =
                planta.stockAceite - planta.stockObjetivoAceite;

            transferirProductoADepositos(
                TipoProducto.ACEITE,
                toneladas
            );
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

        for (LoteProducto lote : lotes) {

            if (
                lote.producto == producto
                && lote.estado == EstadoLote.EN_DEPOSITO
                && lote.depositoActual != null
            ) {
                total += lote.toneladasDisponibles;
            }
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

    Pedido crearPedido(String codigo, TipoProducto producto, double toneladas, Terminal puerto, double diaLimite) {
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
        pedido.diaLimite = diaLimite;
        pedido.diaReserva = -1;
        pedido.diaEntrega = -1;

        pedido.puertoSalida = puerto;
        pedido.depositoAsignado = null;

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

    Deposito seleccionarDepositoParaPedido(Pedido pedido) {
        if (
            pedido == null
            || pedido.puertoSalida == null
            || pedido.toneladasSolicitadas <= 0
        ) {
            return null;
        }

        Deposito mejorDeposito = null;
        double menorCosto = Double.POSITIVE_INFINITY;

        for (Deposito deposito : depositos) {

            if (
                !deposito.puedeReservar(
                    pedido.producto,
                    pedido.toneladasSolicitadas
                )
            ) {
                continue;
            }

            double costoFlete =
                pedido.toneladasSolicitadas
                * deposito.getCostoFletePuerto(
                    pedido.puertoSalida,
                    pedido.producto
                );

            double costoConsolidado =
                pedido.toneladasSolicitadas
                * pedido.puertoSalida
                    .getCostoConsolidado(pedido.producto);

            double costoTotal =
                costoFlete + costoConsolidado;

            if (costoTotal < menorCosto) {
                menorCosto = costoTotal;
                mejorDeposito = deposito;
            }
        }

        return mejorDeposito;
    }

    LoteProducto crearLoteReservadoDesdeDivision(LoteProducto loteOriginal, double toneladasReserva, Pedido pedido) {
        if (
            loteOriginal == null
            || pedido == null
            || toneladasReserva <= 0
            || toneladasReserva >= loteOriginal.getToneladasLibres()
        ) {
            return null;
        }

        double toneladasAntes =
            loteOriginal.toneladasDisponibles;

        double proporcion =
            toneladasReserva / toneladasAntes;

        LoteProducto nuevoLote = add_lotes();

        nuevoLote.idLote = siguienteIdLote;
        siguienteIdLote++;

        nuevoLote.producto = loteOriginal.producto;
        nuevoLote.toneladasIniciales = toneladasReserva;
        nuevoLote.toneladasDisponibles = toneladasReserva;
        nuevoLote.toneladasReservadas = toneladasReserva;

        nuevoLote.diaProduccion =
            loteOriginal.diaProduccion;

        nuevoLote.estado =
            EstadoLote.RESERVADO;

        nuevoLote.ubicacionActual =
            loteOriginal.ubicacionActual;

        nuevoLote.depositoActual =
            loteOriginal.depositoActual;

        nuevoLote.diaIngresoDeposito =
            loteOriginal.diaIngresoDeposito;

        nuevoLote.diaReserva = time();
        nuevoLote.pedidoAsignado = pedido;
        nuevoLote.loteOrigen = loteOriginal;


        // Distribuir proporcionalmente los costos históricos
        nuevoLote.costoAcumulado =
            loteOriginal.costoAcumulado * proporcion;

        nuevoLote.costoAlmacenamientoLote =
            loteOriginal.costoAlmacenamientoLote
            * proporcion;


        // Reducir el lote original
        loteOriginal.toneladasIniciales -= toneladasReserva;
        loteOriginal.toneladasDisponibles -= toneladasReserva;

        loteOriginal.costoAcumulado -=
            nuevoLote.costoAcumulado;

        loteOriginal.costoAlmacenamientoLote -=
            nuevoLote.costoAlmacenamientoLote;

        return nuevoLote;
    }

    boolean reservarLotesParaPedido(Pedido pedido, Deposito deposito) {
        if (
            pedido == null
            || deposito == null
            || pedido.toneladasSolicitadas <= 0
        ) {
            return false;
        }

        if (
            !deposito.puedeReservar(
                pedido.producto,
                pedido.toneladasSolicitadas
            )
        ) {
            return false;
        }

        // Registrar primero la reserva agregada
        boolean reservaRegistrada =
            deposito.reservarProducto(
                pedido.producto,
                pedido.toneladasSolicitadas
            );

        if (!reservaRegistrada) {
            return false;
        }

        double pendiente =
            pedido.toneladasSolicitadas;

        while (pendiente > 0.0001) {

            LoteProducto loteSeleccionado = null;
            double diaMasAntiguo =
                Double.POSITIVE_INFINITY;

            for (LoteProducto lote : lotes) {

                if (
                    lote.producto == pedido.producto
                    && lote.estado == EstadoLote.EN_DEPOSITO
                    && lote.depositoActual == deposito
                    && lote.getToneladasLibres() > 0
                    && lote.diaProduccion < diaMasAntiguo
                ) {
                    loteSeleccionado = lote;
                    diaMasAntiguo = lote.diaProduccion;
                }
            }

            if (loteSeleccionado == null) {

                deposito.liberarReserva(
                    pedido.producto,
                    pedido.toneladasSolicitadas
                );

                return false;
            }

            double libre =
                loteSeleccionado.getToneladasLibres();

            if (libre <= pendiente + 0.0001) {

                loteSeleccionado.toneladasReservadas =
                    libre;

                loteSeleccionado.estado =
                    EstadoLote.RESERVADO;

                loteSeleccionado.pedidoAsignado =
                    pedido;

                loteSeleccionado.diaReserva =
                    time();

                pendiente -= libre;

            } else {

                LoteProducto loteDividido =
                    crearLoteReservadoDesdeDivision(
                        loteSeleccionado,
                        pendiente,
                        pedido
                    );

                if (loteDividido == null) {

                    deposito.liberarReserva(
                        pedido.producto,
                        pedido.toneladasSolicitadas
                    );

                    return false;
                }

                pendiente = 0;
            }
        }

        return true;
    }

    boolean intentarAsignarPedido(Pedido pedido) {
        if (pedido == null) {
            return false;
        }

        if (
            pedido.estado != EstadoPedido.PENDIENTE
            && pedido.estado != EstadoPedido.ATRASADO
        ) {
            return false;
        }

        Deposito deposito =
            seleccionarDepositoParaPedido(pedido);

        if (deposito == null) {
            return false;
        }

        boolean reservado =
            reservarLotesParaPedido(
                pedido,
                deposito
            );

        if (!reservado) {
            return false;
        }

        pedido.depositoAsignado = deposito;
        pedido.toneladasReservadas =
            pedido.toneladasSolicitadas;

        pedido.diaReserva = time();
        pedido.estado = EstadoPedido.RESERVADO;

        pedido.costoFleteEstimado =
            pedido.toneladasSolicitadas
            * deposito.getCostoFletePuerto(
                pedido.puertoSalida,
                pedido.producto
            );

        pedido.costoConsolidadoEstimado =
            pedido.toneladasSolicitadas
            * pedido.puertoSalida
                .getCostoConsolidado(pedido.producto);

        pedido.costoTotalEstimado =
            pedido.costoFleteEstimado
            + pedido.costoConsolidadoEstimado;

        pedidosReservados++;
        pedidosPendientes--;

        toneladasReservadasAcumuladas +=
            pedido.toneladasSolicitadas;

        return true;
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
        double total = 0;

        for (LoteProducto lote : lotes) {

            if (
                lote.producto == producto
                && lote.estado == EstadoLote.RESERVADO
                && lote.pedidoAsignado != null
            ) {
                total += lote.toneladasReservadas;
            }
        }

        return total;
    }

    Envio crearEnvio(Pedido pedido, double toneladas) {
        if (
            pedido == null
            || pedido.depositoAsignado == null
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
        envio.depositoOrigen =
            pedido.depositoAsignado;

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

        double distancia =
            envio.depositoOrigen
                .getDistanciaTerminal(
                    envio.terminalDestino
                );

        double velocidadCamion = 70;

        envio.tiempoCargaHoras =
            toneladas
            / envio.depositoOrigen
                .velocidadCargaTnHora;

        envio.tiempoViajeIdaHoras =
            distancia / velocidadCamion;

        envio.tiempoDescargaHoras =
            toneladas
            / envio.terminalDestino
                .velocidadDescargaTnHora;

        envio.tiempoConsolidacionHoras =
            toneladas
            / envio.terminalDestino
                .velocidadConsolidacionTnHora;

        envio.tiempoRetornoHoras =
            distancia / velocidadCamion;

        envio.costoFleteReal =
            150
            + distancia * 2 * 1.20;

        envio.costoConsolidacionReal =
            toneladas
            * envio.terminalDestino
                .getCostoConsolidado(
                    envio.producto
                );

        envio.costoTotalReal =
            envio.costoFleteReal
            + envio.costoConsolidacionReal;

        pedido.cantidadEnvios++;

        entradaEnvios.take(envio);

        return envio;
    }

    boolean generarEnviosParaPedido(Pedido pedido) {
        if (
            pedido == null
            || pedido.estado != EstadoPedido.RESERVADO
            || pedido.toneladasReservadas <= 0
            || pedido.enviosGenerados
        ) {
            return false;
        }

        double capacidadCamion = 25;
        double pendiente =
            pedido.toneladasReservadas;

        while (pendiente > 0.0001) {

            double toneladasEnvio =
                min(capacidadCamion, pendiente);

            Envio envio =
                crearEnvio(
                    pedido,
                    toneladasEnvio
                );

            if (envio == null) {
                return false;
            }

            pendiente -= toneladasEnvio;
        }

        pedido.estado =
            EstadoPedido.EN_PREPARACION;

        return true;
    }

    boolean retirarReservaParaEnvio(Envio envio) {
        if (
            envio == null
            || envio.pedido == null
            || envio.depositoOrigen == null
            || envio.toneladas <= 0
        ) {
            return false;
        }

        Pedido pedido =
            envio.pedido;

        Deposito deposito =
            envio.depositoOrigen;

        double pendiente =
            envio.toneladas;

        // Reducir cantidades en lotes reservados
        for (LoteProducto lote : lotes) {

            if (pendiente <= 0.0001) {
                break;
            }

            if (
                lote.pedidoAsignado == pedido
                && lote.depositoActual == deposito
                && lote.estado == EstadoLote.RESERVADO
                && lote.toneladasReservadas > 0
            ) {
                double retirar =
                    min(
                        pendiente,
                        lote.toneladasReservadas
                    );

                lote.toneladasReservadas -= retirar;
                lote.toneladasDisponibles -= retirar;

                pendiente -= retirar;

                if (
                    lote.toneladasDisponibles <= 0.0001
                    && lote.toneladasReservadas <= 0.0001
                ) {
                    lote.toneladasDisponibles = 0;
                    lote.toneladasReservadas = 0;
                    lote.estado =
                        EstadoLote.EN_TRANSITO_PUERTO;
                }
            }
        }

        if (pendiente > 0.0001) {
            return false;
        }

        boolean despachado =
            deposito.despacharReservado(
                envio.producto,
                envio.toneladas
            );

        if (!despachado) {
            return false;
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

    void pruebaCrearContenedor() {
        ContenedorExportacion c = add_contenedoresExportacion();

        c.idContenedor = "CONT-TEST-001";
        c.tipoContenedor = TipoContenedor.REEFER_40;
        c.capacidadTon = obtenerCapacidadContenedorTon(c.tipoContenedor);
        c.cantidadAsignadaTon = 24.5;
        c.estado = EstadoContenedor.CREADO;

        traceln(
            "Creado: " + c.idContenedor
            + " | Tipo: " + c.tipoContenedor
            + " | Capacidad: " + c.capacidadTon
            + " tn"
        );
    }

    void cargarDatosEntrada() {
        // Fase 1 del contrato de datos: las tablas se generan; la fase 2 las va a
        // importar desde Excel. Nada mas cambia: la logica lee siempre 'datos'.
        datos = GeneradorSintetico.generar(
            idEscenario,
            duracionCampaniaDias,
            semillaBase,
            variabilidadProduccion,
            variabilidadDemanda,
            pedidosPorCampania,
            toneladasMediasPedido,
            plazoPedidoDias
        );

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

            deposito.capacidadJugo =
                datos.capacidadTn(deposito.idUbicacion, TipoProducto.JUGO);

            deposito.capacidadCascara =
                datos.capacidadTn(deposito.idUbicacion, TipoProducto.CASCARA);

            deposito.capacidadAceite =
                datos.capacidadTn(deposito.idUbicacion, TipoProducto.ACEITE);

            deposito.costoJugoTnDia =
                datos.storageUsdTnDia(deposito.idUbicacion, TipoProducto.JUGO);

            deposito.costoCascaraTnDia =
                datos.storageUsdTnDia(deposito.idUbicacion, TipoProducto.CASCARA);

            deposito.costoAceiteTnDia =
                datos.storageUsdTnDia(deposito.idUbicacion, TipoProducto.ACEITE);
        }

        for (Terminal terminal : terminales) {

            DatosEntrada.Ubicacion ubicacion =
                datos.ubicacion(terminal.idUbicacion);

            terminal.habilitada = ubicacion.habilitada;
            terminal.capacidadDiariaTn = ubicacion.capacidadDiariaTn;
            terminal.velocidadDescargaTnHora = ubicacion.velocidadDescargaTnHora;
            terminal.velocidadConsolidacionTnHora = ubicacion.velocidadConsolidacionTnHora;

            terminal.costoConsolidadoJugo =
                datos.servicioCargaUsdTn(terminal.idUbicacion, TipoProducto.JUGO);

            terminal.costoConsolidadoCascara =
                datos.servicioCargaUsdTn(terminal.idUbicacion, TipoProducto.CASCARA);

            terminal.costoConsolidadoAceite =
                datos.servicioCargaUsdTn(terminal.idUbicacion, TipoProducto.ACEITE);
        }
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

            crearPedido(
                plan.codigoPedido,
                plan.producto,
                plan.toneladasSolicitadas,
                terminal,
                plan.diaLimite
            );
        }
    }

    void revisarPedidosPendientes() {
        for (Pedido pedido : pedidos) {

            if (
                pedido.estado == EstadoPedido.PENDIENTE
                || pedido.estado == EstadoPedido.ATRASADO
            ) {
                intentarAsignarPedido(pedido);
            }
        }
    }

    void prepararPedidosReservados() {
        for (Pedido pedido : pedidos) {

            if (
                pedido.estado == EstadoPedido.RESERVADO
                && !pedido.enviosGenerados
            ) {
                generarEnviosParaPedido(pedido);
            }
        }
    }

    void devengarAlmacenamientoDiario() {
        // Fuente unica del costo de almacenaje del dia (H-04).
        // Se imputa por lote y se agrega al deposito en el mismo recorrido, de modo
        // que el total del deposito sea por construccion la suma de sus lotes.
        for (LoteProducto lote : lotes) {

            Deposito deposito = lote.depositoActual;

            if (
                deposito == null
                || lote.toneladasDisponibles <= 0
            ) {
                continue;
            }

            double costoDia =
                lote.toneladasDisponibles
                * deposito.getTarifaAlmacenamiento(lote.producto);

            lote.costoAlmacenamientoLote += costoDia;
            lote.costoAcumulado += costoDia;
            deposito.costoAlmacenamientoAcumulado += costoDia;
        }
    }

    void registrarAtrasos() {
        for (Pedido pedido : pedidos) {

            if (
                time() > pedido.diaLimite
                && pedido.estado != EstadoPedido.ENTREGADO
                && pedido.estado != EstadoPedido.CANCELADO
            ) {
                pedido.diasAtraso =
                    time() - pedido.diaLimite;

                if (pedido.estado == EstadoPedido.PENDIENTE) {
                    pedido.estado = EstadoPedido.ATRASADO;
                }
            }
        }
    }

    // ----- Eventos -----

    // evento pasoDiario [timeout cyclic] cada 1 day
    void pasoDiario_accion() {
        // Secuencia diaria del modelo (ADR-034). El orden es parte de la
        // definicion: cambiarlo cambia el costo y el servicio del dia.
        producirEnPlantas();              // 1. producir
        revisarTransferenciasPlanta();    // 2. recibir e ingresar
        registrarPedidosDelDia();         // 3. planificar y comprometer
        revisarPedidosPendientes();       // 4. reservar
        prepararPedidosReservados();      // 5. ejecutar movimientos
        devengarAlmacenamientoDiario();   // 6. devengar almacenaje
        registrarAtrasos();               // 7. registrar indicadores del dia
    }
}
