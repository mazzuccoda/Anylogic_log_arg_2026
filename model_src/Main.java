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
                calcularCostoPlantaDeposito(deposito, producto, posible)
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
                transferirToneladasLote(lote, destino, aMover, false);

            if (movidas <= 0.0001) {
                break;
            }

            pendiente -= movidas;
        }
    }

    void revisarTransferenciasPlanta() {
        // Politica de frio propio (ADR-048). El frio de la planta es propio y no se
        // factura, asi que el default es retener: el producto sale solo si el forecast
        // proyecta pasar la capacidad nominal o si un pedido no se puede servir desde
        // deposito. La politica REACTIVA conserva la regla anterior (vaciar la planta
        // al llegar al umbral de alerta) y queda como escenario de comparacion.
        boolean flexible =
            "FLEXIBLE".equals(datos.escenario.politicaFrioPropio);

        for (TipoProducto producto : TipoProducto.values()) {

            double toneladas = flexible
                ? toneladasASacarDePlanta(producto)
                : toneladasASacarReactiva(producto);

            if (toneladas <= 0.0001) {
                continue;
            }

            transferirProductoADepositos(producto, toneladas);
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

            int contenedores =
                contenedoresNecesarios(
                    pedido.producto,
                    pedido.toneladasSolicitadas
                );

            double costoFlete =
                deposito.getImporteFletePuerto(
                    pedido.puertoSalida,
                    pedido.producto,
                    pedido.toneladasSolicitadas
                );

            double costoConsolidado =
                consolidaEnTerminal()
                ? pedido.puertoSalida.getImporteConsolidacion(
                    pedido.producto,
                    pedido.toneladasSolicitadas,
                    contenedores
                )
                : deposito.getImporteConsolidacion(
                    pedido.producto,
                    pedido.toneladasSolicitadas,
                    contenedores
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

        String idSitio =
            seleccionarSitioParaPedido(pedido);

        if (idSitio == null) {
            return false;
        }

        boolean reservado =
            reservarLotesParaPedido(
                pedido,
                idSitio
            );

        if (!reservado) {
            return false;
        }

        confirmarAsignacion(pedido, idSitio);

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
            || pedido.idSitioOrigen.isEmpty()
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

        envio.idSitioOrigen =
            pedido.idSitioOrigen;

        // Queda nulo cuando el pedido sale del frio propio: el deposito es un caso del
        // origen y no el origen (ADR-050).
        envio.depositoOrigen =
            pedido.depositoAsignado;

        envio.circuito =
            pedido.estrategiaSeleccionada;

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
                pedido,
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
        if (
            envio == null
            || envio.pedido == null
            || envio.idSitioOrigen.isEmpty()
            || envio.toneladas <= 0
        ) {
            return false;
        }

        Pedido pedido = envio.pedido;

        double reservado =
            inventario.reservadoPedidoEn(
                envio.idSitioOrigen,
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
                envio.idSitioOrigen,
                envio.producto,
                envio.toneladas,
                pedido.codigoPedido
            );

        if (despachadas + 0.0001 < envio.toneladas) {
            return false;
        }

        actualizarEstadoLotesVacios();

        // El egreso se devenga cuando el producto sale fisicamente del almacenamiento
        // (ADR-053).
        envio.costoCargosReal +=
            registrarOutDeposito(envio);

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

            DatosEntrada.TarifaSitio tarifa =
                datos.tarifaSitio(diaCampania(), capa.idUbicacion, capa.producto);

            double costoDia = registro.registrar(
                time(),
                RegistroCostos.Categoria.ALMACENAMIENTO,
                RegistroCostos.Tipo.CAJA,
                "", "", "" + capa.idLote, capa.producto,
                capa.idUbicacion, capa.idUbicacion, capa.idUbicacion,
                EstrategiaLogistica.SIN_DEFINIR, tarifa.proveedor,
                DatosEntrada.Unidad.USD_TN_DIA, facturables, tarifa.storageUsdTnDia,
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

    int crearContenedoresParaPedido(Pedido pedido) {
        // Fase 6: la unidad de exportacion pasa a ser el contenedor. Se crean tantos como haga
        // falta para lo reservado, con la capacidad del tipo de contenedor del producto, y el
        // ultimo va parcial. Cada uno queda asociado al lote que mas aporta a su carga.
        if (
            pedido == null
            || pedido.idSitioOrigen.isEmpty()
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

        String origen = pedido.idSitioOrigen;

        String sitioDeEstiba = sitioEstiba(pedido);

        // Capas que este pedido tiene reservadas, en el mismo orden FIFO en que se van a
        // despachar: recorrerlas en paralelo a los contenedores da el lote de cada uno.
        java.util.List<Capa> reservadas =
            inventario.capasReservadasDe(
                origen,
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
                sitioDeEstiba.equals("PLANTA")
                ? (Agent) planta
                : (
                    buscarDeposito(sitioDeEstiba) != null
                    ? (Agent) buscarDeposito(sitioDeEstiba)
                    : (Agent) pedido.puertoSalida
                );
            contenedor.estado = EstadoContenedor.ESPERANDO_PROGRAMACION;

            contenedor.costoEstimado =
                datos.roundTripUsdContenedor(
                    diaCampania(),
                    pedido.puertoSalida.idUbicacion,
                    origen,
                    pedido.tipoContenedor
                )
                + importeServicioEstiba(pedido, carga, 1);

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
        // Donde se estiba el contenedor: el sitio de origen del pedido o la terminal de
        // salida, segun el circuito (ADR-050).
        return contenedor == null
            ? ""
            : sitioEstiba(contenedor.Pedido);
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
            Pedido pedido = contenedor.Pedido;

            // Circuito 4: el producto viaja a granel, asi que le consume jornada a la
            // flota de producto y no al pool de portacontenedores (ADR-050).
            boolean granel =
                pedido.estrategiaSeleccionada
                == EstrategiaLogistica.CONSOLIDACION_TERMINAL;

            if (
                granel
                && !flotaProductoAlcanza(
                    pedido.idSitioOrigen,
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
                    pedido.idSitioOrigen,
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
                    pedido.producto,
                    pedido.toneladasSolicitadas
                )
                + deposito.getImporteFletePuerto(
                    pedido.puertoSalida,
                    pedido.producto,
                    pedido.toneladasSolicitadas
                )
                + deposito.getImporteCrossDock(
                    pedido.producto,
                    pedido.toneladasSolicitadas,
                    contenedoresNecesarios(
                        pedido.producto,
                        pedido.toneladasSolicitadas
                    )
                );

            if (costo < menorCosto) {
                menorCosto = costo;
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
                "PLANTA",
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
                pedido.toneladasSolicitadas,
                true
            );

        // Si el producto se movio pero el pedido no se puede armar, lo movido queda en
        // el deposito como stock normal y devenga almacenaje: es una operacion de cross
        // dock que se degrado, y se cuenta como tal.
        if (
            movidas + 0.0001 < pedido.toneladasSolicitadas
            || !reservarLotesParaPedido(pedido, sitio.idUbicacion)
        ) {
            crossDockDegradados++;

            // Lo movido queda como stock normal: desde aca paga ingreso y almacenaje. El
            // cargo va sin lote porque puede venir de varios (ADR-053).
            registrarInDeposito(
                sitio.idUbicacion,
                pedido.producto,
                movidas,
                "",
                pedido.codigoPedido,
                "INX-" + diaCampania() + "-" + pedido.codigoPedido
            );

            return false;
        }

        pedido.esCrossDock = true;

        confirmarAsignacion(pedido, sitio.idUbicacion);

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

    void aplicarEscenario() {
        // La fila del escenario describe la corrida entera (ADR-032): lo que el
        // barrido enumera es el id, no cada palanca por separado.
        DatosEntrada.Escenario escenario = datos.escenario;

        duracionCampaniaDias = escenario.duracionCampaniaDias;
        habilitaCrossDock = escenario.habilitaCrossDock;

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
        // Toneladas comprometidas y todavia no reservadas. Un pedido ya recibido es una
        // obligacion: se cuenta completo aunque su fecha limite caiga despues del
        // horizonte, porque sacarlo de planta toma dias de flota y esperar al ultimo
        // momento es lo que hace perder el pedido.
        double total = 0;

        for (Pedido pedido : pedidos) {

            if (
                pedido.producto == producto
                && (
                    pedido.estado == EstadoPedido.PENDIENTE
                    || pedido.estado == EstadoPedido.ATRASADO
                )
            ) {
                total += pedido.toneladasSolicitadas;
            }
        }

        return total;
    }

    double toneladasASacarDePlanta(TipoProducto producto) {
        // Dos motivos, y solo dos, para gastar frio de terceros:
        //
        //   1. desborde proyectado: con la produccion de los proximos dias la planta
        //      pasa su capacidad nominal;
        //   2. servicio: los pedidos con fecha limite dentro del horizonte no se pueden
        //      cubrir con lo que ya esta libre en deposito.
        //
        // Si no se cumple ninguno, el producto se queda en el frio propio.
        int horizonte = datos.escenario.diasForecast;

        double stockPlanta =
            inventario.libre("PLANTA", producto);

        if (stockPlanta <= 0.0001) {
            return 0;
        }

        double proyectado =
            planta.getStock(producto)
            + forecastProduccion(producto, horizonte);

        double porDesborde =
            max(0, proyectado - planta.getCapacidad(producto));

        double libreEnDepositos = 0;

        for (Deposito deposito : depositos) {
            libreEnDepositos +=
                inventario.libre(deposito.idUbicacion, producto);
        }

        // Con consolidacion en planta la demanda se sirve del frio propio: sacarla al
        // deposito seria pagar almacenaje para despachar desde el mismo producto.
        double porServicio =
            consolidaEnPlanta()
            ? 0
            : max(
                0,
                demandaProyectada(producto) - libreEnDepositos
            );

        return min(stockPlanta, max(porDesborde, porServicio));
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

    String sitioEstiba(Pedido pedido) {
        // Donde se arma fisicamente el contenedor: el sitio de origen, salvo en el
        // circuito de terminal, donde el producto viaja a granel y se estiba en el puerto.
        if (pedido == null) {
            return "";
        }

        return pedido.estrategiaSeleccionada
            == EstrategiaLogistica.CONSOLIDACION_TERMINAL
            ? pedido.puertoSalida.idUbicacion
            : pedido.idSitioOrigen;
    }

    boolean usaPortacontenedor(Envio envio) {
        // Los circuitos de planta, deposito y cross dock mandan el contenedor vacio al
        // origen y lo traen cargado: ocupan un portacontenedor de punta a punta. El de
        // terminal manda el producto a granel y no toca el pool.
        return envio != null
            && envio.circuito
                != EstrategiaLogistica.CONSOLIDACION_TERMINAL;
    }

    String seleccionarSitioParaPedido(Pedido pedido) {
        if (
            pedido == null
            || pedido.puertoSalida == null
            || pedido.toneladasSolicitadas <= 0
        ) {
            return null;
        }

        // Con consolidacion en planta el pedido se sirve del frio propio siempre que
        // alcance: es el circuito que ahorra el tramo planta-deposito y el almacenaje.
        if (
            consolidaEnPlanta()
            && !pedido.esCrossDock
            && inventario.libre("PLANTA", pedido.producto)
                + 0.0001
                >= pedido.toneladasSolicitadas
        ) {
            return "PLANTA";
        }

        Deposito deposito =
            seleccionarDepositoParaPedido(pedido);

        return deposito == null
            ? null
            : deposito.idUbicacion;
    }

    void registrarEstibaEnOrigen(Envio envio) {
        // El servicio se devenga donde se arma el contenedor (ADR-050): planta, deposito
        // o cross dock en deposito. Cada sitio lleva su propia estadistica.
        if (
            envio == null
            || envio.contenedor == null
            || !estibaEnOrigen(envio.pedido)
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

        if (
            pedido.toneladasEntregadas
            >= pedido.toneladasSolicitadas - 0.0001
        ) {
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

        if (envio.pedido.esCrossDock) {
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

    boolean reservarLotesParaPedido(Pedido pedido, String idSitio) {
        if (
            pedido == null
            || idSitio == null
            || pedido.toneladasSolicitadas <= 0
        ) {
            return false;
        }

        // La reserva se anota sobre las capas del sitio de origen, tomando primero las
        // mas antiguas (ADR-022). El lote no se parte en un segundo agente (ADR-024).
        double reservadas =
            inventario.reservar(
                idSitio,
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

    void confirmarAsignacion(Pedido pedido, String idSitio) {
        pedido.idSitioOrigen = idSitio;

        // Sigue habiendo deposito asignado cuando el origen es un deposito; en el
        // circuito de planta queda nulo y el origen es el propio frio propio.
        pedido.depositoAsignado = buscarDeposito(idSitio);

        pedido.estrategiaSeleccionada =
            circuitoDe(idSitio, pedido.esCrossDock);

        pedido.toneladasReservadas =
            pedido.toneladasSolicitadas;

        pedido.diaReserva = time();
        pedido.estado = EstadoPedido.RESERVADO;

        pedido.costoFleteEstimado =
            datos.importeFlete(
                diaCampania(),
                idSitio,
                pedido.puertoSalida.idUbicacion,
                pedido.producto,
                pedido.toneladasSolicitadas,
                viajesNecesariosCamion(pedido.toneladasSolicitadas)
            );

        pedido.costoConsolidadoEstimado =
            importeServicioEstiba(
                pedido,
                pedido.toneladasSolicitadas,
                contenedoresNecesarios(
                    pedido.producto,
                    pedido.toneladasSolicitadas
                )
            );

        pedido.costoTotalEstimado =
            pedido.costoFleteEstimado
            + pedido.costoConsolidadoEstimado;

        pedidosReservados++;
        pedidosPendientes--;

        toneladasReservadasAcumuladas +=
            pedido.toneladasSolicitadas;
    }

    double importeServicioEstiba(Pedido pedido, double toneladas, int contenedores) {
        // Quien cobra la estiba es el sitio donde se arma el contenedor, y el cross dock es
        // otro servicio que la estiba desde stock (ADR-041, ADR-050). La unidad de la tarifa
        // decide si se cobra por tonelada o por contenedor completo (ADR-051).
        String sitio = sitioEstiba(pedido);

        int dia = diaCampania();

        return pedido.esCrossDock
            ? datos.importeCrossDock(dia, sitio, pedido.producto, toneladas, contenedores)
            : datos.importeConsolidacion(dia, sitio, pedido.producto, toneladas, contenedores);
    }

    boolean estibaEnOrigen(Pedido pedido) {
        // Tres de los cuatro circuitos estiban en el origen; solo el de terminal manda el
        // producto a granel y arma el contenedor en el puerto.
        return pedido != null
            && pedido.estrategiaSeleccionada
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
                DatosEntrada.Unidad.USD_VIAJE, viajes, tarifa.tarifa,
                idOperacion, "flete por viaje"
            );

        } else {

            importe += registro.registrar(
                time(),
                RegistroCostos.Categoria.FLETE_PRODUCTO,
                RegistroCostos.Tipo.CAJA,
                codigoPedido, "", idLote, producto,
                origen, destino, origen, estrategia, tarifa.proveedor,
                DatosEntrada.Unidad.USD_TN, toneladas, tarifa.tarifa,
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
                DatosEntrada.Unidad.USD_TN, toneladas, tarifa.variableUsdTn,
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
            DatosEntrada.Unidad.USD_CONTENEDOR, 1, tarifa.tarifaUsdContenedor,
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

        String sitio = sitioEstiba(pedido);

        DatosEntrada.TarifaSitio tarifa =
            datos.tarifaSitio(diaTarifa, sitio, envio.producto);

        boolean cruza = pedido.esCrossDock;

        DatosEntrada.Unidad unidad =
            cruza ? tarifa.crossDockUnidad : tarifa.consolidacionUnidad;

        double usd =
            cruza ? tarifa.crossDockTarifa : tarifa.consolidacionTarifa;

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
        // lo paga lo que sale del frio propio ni lo que cruza en cross dock (ADR-053).
        return envio != null
            && envio.pedido != null
            && !envio.pedido.esCrossDock
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
            DatosEntrada.Unidad.USD_CONTENEDOR, contenedores, tarifa.thcUsdContenedor,
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
            tarifa.costoTerminalUsdContenedor,
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
            tarifa.despachanteTarifa,
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

        String sitio = sitioEstiba(pedido);

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
        esperado += pedido.esCrossDock
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
                esperado += tarifaCierre.despachanteTarifa;
            }

        } else {
            esperado += tarifaCierre.despachanteTarifa * contenedores;
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

    // ----- Eventos -----

    // evento pasoDiario [timeout cyclic] cada 1 day
    void pasoDiario_accion() {
        // Secuencia diaria del modelo (ADR-034). El orden es parte de la
        // definicion: cambiarlo cambia el costo y el servicio del dia.
        refrescarTarifasDelDia();                // 0. tarifa vigente de hoy (ADR-051)
        abrirFlotaDelDia();                      // 0b. abrir la capacidad de flota del dia
        producirEnPlantas();                     // 1. producir
        registrarPedidosDelDia();                // 2. planificar y comprometer
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
        validarInventario();              // invariantes de las capas (ADR-023)
        reconciliarCostos();              // los totales explican cargo por cargo (ADR-052)
    }
}
