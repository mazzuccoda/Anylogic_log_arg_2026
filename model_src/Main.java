// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Main extends Agent {

    // ----- Parámetros -----
    OrigenDatos origenDatos = OrigenDatos.SINTETICO;
    String rutaExcel = "datos/entrada_ejemplo.xlsx";
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
    Inventario inventario = new Inventario();

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
        lote.diaProduccion = time();
        lote.estado = EstadoLote.EN_PLANTA;
        lote.ubicacionActual = origen;
        lote.costoAcumulado = 0;
        lote.pedidoAsignado = null;

        // El saldo fisico vive en la capa, no en el lote (ADR-023). 'toneladasIniciales'
        // queda como lo producido, que ya no se toca nunca mas.
        inventario.ingresar(
            lote.idLote,
            producto,
            "PLANTA",
            toneladas,
            time(),
            time()
        );

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

        return costoFijoViajePD
            + datos.distanciaKm("PLANTA", deposito.idUbicacion) * costoKmPD
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

        // Fase 6: la carga se divide en contenedores y cada envio mueve uno. La capacidad ya
        // no es un camion generico de 25 tn: es la del tipo de contenedor del producto.
        int cantidad = crearContenedoresParaPedido(pedido);

        if (cantidad <= 0) {
            return false;
        }

        for (
            ContenedorExportacion contenedor
            : pedido.contenedores
        ) {

            Envio envio =
                crearEnvio(
                    pedido,
                    contenedor.cantidadAsignadaTon
                );

            if (envio == null) {
                return false;
            }

            envio.contenedor = contenedor;

            contenedor.estado =
                EstadoContenedor.ESPERANDO_CARGA;
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
        // Fuente unica del costo de almacenaje del dia (H-04). Se devenga por capa, que
        // es lo que tiene ubicacion y dia de ingreso propios, y se imputa al lote y al
        // deposito en el mismo recorrido, sin doble conteo.
        for (Capa capa : inventario.capas) {

            Deposito deposito = buscarDeposito(capa.idUbicacion);

            if (deposito == null || capa.toneladas <= 0) {
                continue;
            }

            double costoDia =
                capa.toneladas
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
            contenedor.lugarConsolidacion = deposito;
            contenedor.estado = EstadoContenedor.ESPERANDO_PROGRAMACION;

            contenedor.costoEstimado =
                150
                + distancia * 2 * 1.20
                + carga
                * pedido.puertoSalida.getCostoConsolidado(
                    pedido.producto
                );

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
        validarInventario();              // invariantes de las capas (ADR-023)
    }
}
