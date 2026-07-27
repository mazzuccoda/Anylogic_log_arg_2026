// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Main extends Agent {

    // ----- Parámetros -----
    OrigenDatos origenDatos = OrigenDatos.SINTETICO;
    String rutaExcel = "datos/entrada_ejemplo.xlsx";
    String idEscenario = "E-00";
    long semillaBase = 1;
    double costoFijoViajePD = 150;
    double costoKmPD = 1.2;
    double costoTnPD = 2.0;
    double diasEstimadosAlmacenamiento = 30;
    int replica = 0;

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
    //  flotaPortacontenedores
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

    double calcularCostoPlantaDeposito(Deposito deposito, double toneladas) {
        if (deposito == null || toneladas <= 0) {
            return Double.POSITIVE_INFINITY;
        }

        // El costo fijo y el kilometraje son por viaje, y un viaje mueve a lo sumo un
        // camion cargado: mover el doble de toneladas cuesta dos veces el viaje.
        return viajesNecesariosCamion(toneladas)
            * (
                costoFijoViajePD
                + datos.distanciaKm("PLANTA", deposito.idUbicacion) * costoKmPD
            )
            + toneladas * costoTnPD;
    }

    Deposito seleccionarDeposito(TipoProducto producto, double toneladas) {
        Deposito mejorDeposito = null;
        double menorCostoPorTonelada = Double.POSITIVE_INFINITY;

        for (Deposito deposito : depositos) {

            if (!deposito.habilitado) {
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
                calcularCostoPlantaDeposito(deposito, posible)
                + posible
                * deposito.getTarifaAlmacenamiento(producto)
                * diasEstimadosAlmacenamiento;

            double costoPorTonelada = costoEstimado / posible;

            if (costoPorTonelada < menorCostoPorTonelada) {
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

    void transferirProductoADepositos(TipoProducto producto, double toneladasObjetivo) {
        double pendiente = toneladasObjetivo;

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
                seleccionarDeposito(producto, aMover);

            if (destino == null) {
                break;
            }

            // Si en el deposito elegido no entra todo, se manda lo que entra y el resto sale
            // en la vuelta siguiente, posiblemente a otro deposito.
            double movidas =
                transferirToneladasLote(lote, destino, aMover);

            if (movidas <= 0.0001) {
                break;
            }

            pendiente -= movidas;
        }
    }

    void revisarTransferenciasPlanta() {
        // JUGO
        if (planta.getStock(TipoProducto.JUGO) >= planta.nivelActivacionJugo) {

            double toneladas =
                planta.getStock(TipoProducto.JUGO) - planta.stockObjetivoJugo;

            transferirProductoADepositos(
                TipoProducto.JUGO,
                toneladas
            );
        }


        // CASCARA
        if (planta.getStock(TipoProducto.CASCARA) >= planta.nivelActivacionCascara) {

            double toneladas =
                planta.getStock(TipoProducto.CASCARA) - planta.stockObjetivoCascara;

            transferirProductoADepositos(
                TipoProducto.CASCARA,
                toneladas
            );
        }


        // ACEITE
        if (planta.getStock(TipoProducto.ACEITE) >= planta.nivelActivacionAceite) {

            double toneladas =
                planta.getStock(TipoProducto.ACEITE) - planta.stockObjetivoAceite;

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
                * (
                    consolidaEnDeposito()
                    ? deposito.getCostoConsolidado(pedido.producto)
                    : pedido.puertoSalida
                        .getCostoConsolidado(pedido.producto)
                );

            double costoTotal =
                costoFlete + costoConsolidado;

            if (costoTotal < menorCosto) {
                menorCosto = costoTotal;
                mejorDeposito = deposito;
            }
        }

        return mejorDeposito;
    }

    boolean reservarLotesParaPedido(Pedido pedido, Deposito deposito) {
        if (
            pedido == null
            || deposito == null
            || pedido.toneladasSolicitadas <= 0
        ) {
            return false;
        }

        // La reserva se anota sobre las capas del deposito, tomando primero las mas
        // antiguas (ADR-022). El lote no se parte en un segundo agente (ADR-024).
        double reservadas =
            inventario.reservar(
                deposito.idUbicacion,
                pedido.producto,
                pedido.toneladasSolicitadas,
                pedido.codigoPedido,
                time()
            );

        if (reservadas + 0.0001 < pedido.toneladasSolicitadas) {

            // Reserva completa o nada: se devuelve lo que se habia tomado.
            inventario.liberarReserva(pedido.codigoPedido);

            return false;
        }

        marcarLotesReservados(pedido);

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

        confirmarAsignacion(pedido, deposito);

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
        return inventario.reservadoProducto(producto);
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

        double velocidadCamion = datos.escenario.velocidadCamionKmh;

        // Fase 7: si el contenedor se estiba en el deposito, la carga es la
        // consolidacion y en la terminal solo queda el ingreso.
        envio.tiempoCargaHoras =
            estibaEnDeposito(pedido)
            ? toneladas
                / envio.depositoOrigen
                    .velocidadConsolidacionTnHora
            : toneladas
                / envio.depositoOrigen
                    .velocidadCargaTnHora;

        envio.tiempoViajeIdaHoras =
            distancia / velocidadCamion;

        envio.tiempoDescargaHoras =
            toneladas
            / envio.terminalDestino
                .velocidadDescargaTnHora;

        envio.tiempoConsolidacionHoras =
            estibaEnDeposito(pedido)
            ? 0
            : toneladas
                / envio.terminalDestino
                    .velocidadConsolidacionTnHora;

        envio.tiempoRetornoHoras =
            distancia / velocidadCamion;

        envio.costoFleteReal =
            150
            + distancia * 2 * 1.20;

        envio.costoConsolidacionReal =
            toneladas
            * costoServicioEstibaUsdTn(
                pedido,
                envio.depositoOrigen
            );

        envio.costoTotalReal =
            envio.costoFleteReal
            + envio.costoConsolidacionReal;

        pedido.cantidadEnvios++;

        entradaEnvios.take(envio);

        return envio;
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

        Pedido pedido = envio.pedido;

        Deposito deposito = envio.depositoOrigen;

        double reservado =
            inventario.reservadoPedidoEn(
                deposito.idUbicacion,
                envio.producto,
                pedido.codigoPedido
            );

        if (reservado + 0.0001 < envio.toneladas) {
            return false;
        }

        // Sale exactamente lo que este pedido tenia reservado, de las capas mas
        // antiguas primero.
        double despachadas =
            inventario.despachar(
                deposito.idUbicacion,
                envio.producto,
                envio.toneladas,
                pedido.codigoPedido
            );

        if (despachadas + 0.0001 < envio.toneladas) {
            return false;
        }

        actualizarEstadoLotesVacios();

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
                && pedido.contenedores.isEmpty()
            ) {

                if (crearContenedoresParaPedido(pedido) > 0) {
                    pedido.estado =
                        EstadoPedido.EN_PREPARACION;
                }
            }
        }
    }

    void devengarAlmacenamientoDiario() {
        // Fuente unica del costo de almacenaje del dia (H-04). Se devenga por capa, que
        // es lo que tiene ubicacion y dia de ingreso propios, y se imputa al lote y al
        // deposito en el mismo recorrido, sin doble conteo.
        //
        // Lo comprometido por un pedido de cross dock no paga almacenaje: cruza el sitio
        // sin ingresar al stock (ADR-010).
        java.util.HashSet<String> cruzados =
            new java.util.HashSet<String>();

        for (Pedido pedido : pedidos) {

            if (pedido.esCrossDock) {
                cruzados.add(pedido.codigoPedido);
            }
        }

        for (Capa capa : inventario.capas) {

            Deposito deposito = buscarDeposito(capa.idUbicacion);

            if (deposito == null || capa.toneladas <= 0) {
                continue;
            }

            double facturables = capa.toneladas;

            for (Capa.Reserva reserva : capa.reservas) {

                if (cruzados.contains(reserva.codigoPedido)) {
                    facturables -= reserva.toneladas;
                }
            }

            if (facturables <= Capa.EPS) {
                continue;
            }

            double costoDia =
                facturables
                * deposito.getTarifaAlmacenamiento(capa.producto);

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

    double transferirToneladasLote(LoteProducto lote, Deposito destino, double toneladas) {
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
            destino.idUbicacion,
            viajesNecesariosCamion(movidas)
        );

        lote.depositoActual = destino;
        lote.diaIngresoDeposito = time();

        actualizarUbicacionLote(lote);

        double costoViaje =
            calcularCostoPlantaDeposito(destino, movidas);

        lote.costoAcumulado += costoViaje;
        costoFletePlantaDeposito += costoViaje;

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

    int crearContenedoresParaPedido(Pedido pedido) {
        // Fase 6: la unidad de exportacion pasa a ser el contenedor. Se crean tantos como haga
        // falta para lo reservado, con la capacidad del tipo de contenedor del producto, y el
        // ultimo va parcial. Cada uno queda asociado al lote que mas aporta a su carga.
        if (
            pedido == null
            || pedido.depositoAsignado == null
            || pedido.puertoSalida == null
            || pedido.toneladasReservadas <= 0.0001
        ) {
            return 0;
        }

        if (!pedido.contenedores.isEmpty()) {
            return pedido.contenedores.size();
        }

        pedido.tipoContenedor =
            obtenerTipoContenedor(pedido.producto);

        pedido.capacidadContenedorTon =
            obtenerCapacidadContenedorTon(pedido.tipoContenedor);

        Deposito deposito = pedido.depositoAsignado;

        double distancia =
            deposito.getDistanciaTerminal(pedido.puertoSalida);

        // Capas que este pedido tiene reservadas, en el mismo orden FIFO en que se van a
        // despachar: recorrerlas en paralelo a los contenedores da el lote de cada uno.
        java.util.List<Capa> reservadas =
            inventario.capasReservadasDe(
                deposito.idUbicacion,
                pedido.producto,
                pedido.codigoPedido
            );

        int indiceCapa = 0;

        double saldoCapa =
            reservadas.isEmpty()
            ? 0
            : reservadas.get(0).reservadasDe(pedido.codigoPedido);

        double pendiente = pedido.toneladasReservadas;
        int numero = 0;

        while (pendiente > 0.0001) {

            double carga =
                min(pedido.capacidadContenedorTon, pendiente);

            numero++;

            ContenedorExportacion contenedor =
                add_contenedoresExportacion();

            contenedor.idContenedor =
                pedido.codigoPedido + "-C" + numero;

            contenedor.Pedido = pedido;
            contenedor.producto = pedido.producto;
            contenedor.tipoContenedor = pedido.tipoContenedor;
            contenedor.capacidadTon = pedido.capacidadContenedorTon;
            contenedor.cantidadAsignadaTon = carga;
            contenedor.terminalDestino = pedido.puertoSalida;

            contenedor.esCrossDock = pedido.esCrossDock;

            contenedor.diaProgramadoCrossDock =
                pedido.esCrossDock ? time() : -1;

            contenedor.lugarConsolidacion =
                pedido.esCrossDock || consolidaEnDeposito()
                ? (Agent) deposito
                : (Agent) pedido.puertoSalida;
            contenedor.estado = EstadoContenedor.ESPERANDO_PROGRAMACION;

            contenedor.costoEstimado =
                150
                + distancia * 2 * 1.20
                + carga
                * costoServicioEstibaUsdTn(pedido, deposito);

            double resto = carga;
            double mayorAporte = 0;

            while (resto > 0.0001 && indiceCapa < reservadas.size()) {

                double toma = min(resto, saldoCapa);

                if (toma > mayorAporte) {
                    mayorAporte = toma;
                    contenedor.lote =
                        buscarLote(
                            reservadas.get(indiceCapa).idLote
                        );
                }

                resto -= toma;
                saldoCapa -= toma;

                if (saldoCapa <= 0.0001) {
                    indiceCapa++;

                    saldoCapa =
                        indiceCapa < reservadas.size()
                        ? reservadas.get(indiceCapa)
                            .reservadasDe(pedido.codigoPedido)
                        : 0;
                }
            }

            pedido.contenedores.add(contenedor);

            pendiente -= carga;
        }

        pedido.cantidadContenedores = numero;

        return numero;
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
        // Donde se estiba el contenedor: el deposito que lo tiene reservado o la
        // terminal de salida.
        if (contenedor == null) {
            return "";
        }

        if (
            consolidaEnDeposito()
            && contenedor.Pedido != null
            && contenedor.Pedido.depositoAsignado != null
        ) {
            return contenedor.Pedido
                .depositoAsignado.idUbicacion;
        }

        return contenedor.terminalDestino.idUbicacion;
    }

    void abrirPosicionesConsolidacionDelDia() {
        // Las posiciones son un recurso de conteo diario (definicion, seccion 3): cada
        // dia se abre la capacidad del sitio y lo que no entra espera al dia siguiente.
        consolidacionesDelDia.clear();

        if (consolidaEnDeposito()) {

            for (Deposito deposito : depositos) {
                capacidadConsolidacionOfrecida +=
                    datos.capacidadConsolidacionDia(
                        deposito.idUbicacion
                    );
            }

        } else {

            for (Terminal terminal : terminales) {
                capacidadConsolidacionOfrecida +=
                    datos.capacidadConsolidacionDia(
                        terminal.idUbicacion
                    );
            }
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

                    return Double.compare(
                        a.Pedido.diaLimite,
                        b.Pedido.diaLimite
                    );
                }
            }
        );

        contenedoresEnEspera = 0;

        for (
            ContenedorExportacion contenedor
            : pendientes
        ) {
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

            Pedido pedido = contenedor.Pedido;

            Envio envio =
                crearEnvio(
                    pedido,
                    contenedor.cantidadAsignadaTon
                );

            if (envio == null) {
                error(
                    "No se pudo crear el envio del contenedor "
                    + contenedor.idContenedor
                );
            }

            envio.contenedor = contenedor;

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
        Deposito mejorSitio = null;
        double menorCosto = Double.POSITIVE_INFINITY;

        int necesarios =
            contenedoresNecesarios(
                pedido.producto,
                pedido.toneladasSolicitadas
            );

        for (Deposito deposito : depositos) {

            if (!deposito.habilitado) {
                continue;
            }

            if (
                capacidadCrossDockLibre(deposito.idUbicacion)
                < necesarios
            ) {
                continue;
            }

            // El producto pasa por el deposito, asi que tiene que entrar aunque no se
            // quede: la capacidad se libera el mismo dia, al salir el contenedor.
            if (
                deposito.getEspacioDisponible(pedido.producto)
                + 0.0001
                < pedido.toneladasSolicitadas
            ) {
                continue;
            }

            // Sin almacenaje en el costo: no guardar es justamente el punto del
            // cross dock (ADR-010).
            double costo =
                calcularCostoPlantaDeposito(
                    deposito,
                    pedido.toneladasSolicitadas
                )
                + pedido.toneladasSolicitadas
                * (
                    deposito.getCostoFletePuerto(
                        pedido.puertoSalida,
                        pedido.producto
                    )
                    + deposito.getCostoCrossDock(pedido.producto)
                );

            if (costo < menorCosto) {
                menorCosto = costo;
                mejorSitio = deposito;
            }
        }

        return mejorSitio;
    }

    double transferirLotesADeposito(TipoProducto producto, Deposito destino, double toneladas) {
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
                    )
                );

            if (movidas <= 0.0001) {
                break;
            }

            pendiente -= movidas;
        }

        return toneladas - pendiente;
    }

    boolean intentarCrossDockPedido(Pedido pedido) {
        // El pedido se sirve con producto que todavia esta en planta: sale hoy, se
        // estiba hoy y no entra a almacenamiento (ADR-010). Si algo no da, no se mueve
        // nada y el pedido sigue el camino normal.
        if (
            inventario.libre("PLANTA", pedido.producto)
            + 0.0001
            < pedido.toneladasSolicitadas
        ) {
            return false;
        }

        Deposito sitio = seleccionarSitioCrossDock(pedido);

        if (sitio == null) {
            crossDockReprogramados++;
            return false;
        }

        if (
            !flotaProductoAlcanza(
                sitio.idUbicacion,
                pedido.toneladasSolicitadas
            )
        ) {
            crossDockReprogramados++;
            return false;
        }

        int necesarios =
            contenedoresNecesarios(
                pedido.producto,
                pedido.toneladasSolicitadas
            );

        if (
            !tomarPosicionesCrossDock(
                sitio.idUbicacion,
                necesarios
            )
        ) {
            crossDockReprogramados++;
            return false;
        }

        double movidas =
            transferirLotesADeposito(
                pedido.producto,
                sitio,
                pedido.toneladasSolicitadas
            );

        // Si el producto se movio pero el pedido no se puede armar, lo movido queda en
        // el deposito como stock normal y devenga almacenaje: es una operacion de cross
        // dock que se degrado, y se cuenta como tal.
        if (
            movidas + 0.0001 < pedido.toneladasSolicitadas
            || !reservarLotesParaPedido(pedido, sitio)
        ) {
            crossDockDegradados++;
            return false;
        }

        pedido.esCrossDock = true;

        confirmarAsignacion(pedido, sitio);

        toneladasCrossDock += movidas;

        return true;
    }

    void programarCrossDockDelDia() {
        if (!habilitaCrossDock) {
            return;
        }

        java.util.List<Pedido> candidatos =
            new java.util.ArrayList<Pedido>();

        for (Pedido pedido : pedidos) {

            if (
                pedido.estado == EstadoPedido.PENDIENTE
                || pedido.estado == EstadoPedido.ATRASADO
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

    double costoServicioEstibaUsdTn(Pedido pedido, Deposito deposito) {
        // Quien cobra la estiba depende de donde se arma el contenedor: el deposito si
        // es cross dock (ADR-041) o si la estrategia consolida ahi (ADR-040), y la
        // terminal en el caso contrario.
        if (pedido.esCrossDock) {
            return deposito.getCostoCrossDock(pedido.producto);
        }

        return consolidaEnDeposito()
            ? deposito.getCostoConsolidado(pedido.producto)
            : pedido.puertoSalida.getCostoConsolidado(
                pedido.producto
            );
    }

    void confirmarAsignacion(Pedido pedido, Deposito deposito) {
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
            * costoServicioEstibaUsdTn(pedido, deposito);

        pedido.costoTotalEstimado =
            pedido.costoFleteEstimado
            + pedido.costoConsolidadoEstimado;

        pedidosReservados++;
        pedidosPendientes--;

        toneladasReservadasAcumuladas +=
            pedido.toneladasSolicitadas;
    }

    boolean estibaEnDeposito(Pedido pedido) {
        return pedido != null
            && (pedido.esCrossDock || consolidaEnDeposito());
    }

    void aplicarEscenario() {
        // La fila del escenario describe la corrida entera (ADR-032): lo que el
        // barrido enumera es el id, no cada palanca por separado.
        DatosEntrada.Escenario escenario = datos.escenario;

        duracionCampaniaDias = escenario.duracionCampaniaDias;
        habilitaCrossDock = escenario.habilitaCrossDock;

        estrategiaConsolidacion =
            escenario.estrategiaConsolidacion.equals("CONSOLIDACION_TERMINAL")
            ? EstrategiaLogistica.CONSOLIDACION_TERMINAL
            : EstrategiaLogistica.CONSOLIDACION_DEPOSITO;

        // La flota de portacontenedores se fija al abrir el dia: en el arranque el pool
        // todavia no leyo su capacidad y set_capacity aca se pierde.
    }

    double costoTotalCampania() {
        return costoFletePlantaDeposito
            + costoFleteDepositoPuertoReal
            + costoConsolidacionReal
            + costoCrossDockReal
            + getCostoAlmacenamientoTotal();
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

    void tomarFlotaProducto(String idDestino, int viajes) {
        double camionDia = viajes * camionDiaViaje("PLANTA", idDestino);

        flotaProductoUsadaHoy += camionDia;
        camionDiaOcupado += camionDia;
        viajesPlantaDeposito += viajes;

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

    boolean flotaProductoAlcanza(String idDestino, double toneladas) {
        // El cross dock es todo o nada: si la flota del dia no alcanza para el pedido
        // entero, no se mueve nada (ADR-010).
        return flotaProductoLibreHoy() + 0.0001
            >= viajesNecesariosCamion(toneladas)
            * camionDiaViaje("PLANTA", idDestino);
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

    // ----- Eventos -----

    // evento pasoDiario [timeout cyclic] cada 1 day
    void pasoDiario_accion() {
        // Secuencia diaria del modelo (ADR-034). El orden es parte de la
        // definicion: cambiarlo cambia el costo y el servicio del dia.
        abrirFlotaDelDia();                      // 0. abrir la capacidad de flota del dia
        producirEnPlantas();                     // 1. producir
        registrarPedidosDelDia();                // 2. planificar y comprometer
        abrirPosicionesConsolidacionDelDia();    // 3. abrir la capacidad de estiba del dia
        abrirPosicionesCrossDockDelDia();        // 4. abrir la capacidad de cross dock del dia
        programarCrossDockDelDia();              // 5. cruzar lo que no necesita guardarse
        revisarTransferenciasPlanta();           // 6. recibir e ingresar el excedente
        revisarPedidosPendientes();              // 7. reservar contra stock
        prepararPedidosReservados();             // 8. armar los contenedores del pedido
        despacharContenedoresPendientes();       // 9. consolidar y despachar lo que entra
        devengarAlmacenamientoDiario();          // 10. devengar almacenaje
        registrarAtrasos();                      // 11. registrar indicadores del dia
        validarInventario();              // invariantes de las capas (ADR-023)
    }
}
