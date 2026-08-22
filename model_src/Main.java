// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Main extends Agent {

    // ----- Parámetros -----
    AuditoriaRed.Nivel nivelAuditoriaRed = AuditoriaRed.Nivel.DESACTIVADA;
    OrigenDatos origenDatos = OrigenDatos.SINTETICO;
    boolean animacionRed = true;
    double pasoAnimacionDias = 0.05;
    int maximoFigurasRedVisual = 150;
    String fechaInicioCampania = "2026-04-01";
    String rutaExcel = "datos/Maestro_Simulacion.xlsx";
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
    int contenedoresEnEspera = 0;
    double toleranciaRetencionEnvioDias = 1.0;
    AuditoriaRed auditoria = new AuditoriaRed();
    long secuenciaArcoAuditoria = 0;
    String idDecisionActual = "";
    String idAlternativaActual = "";
    java.util.LinkedHashMap<String, Double> stockInicialDelDia = new java.util.LinkedHashMap<String, Double>();
    java.util.LinkedHashMap<String, Long> motivosAuditoria = new java.util.LinkedHashMap<String, Long>();
    java.util.LinkedHashMap<String, double[]> arcosAuditoria = new java.util.LinkedHashMap<String, double[]>();
    java.util.LinkedHashMap<String, Double> costoRealPorAsignacion = new java.util.LinkedHashMap<String, Double>();
    int descuadresInventarioAuditoria = 0;
    long decisionesAuditadas = 0;
    long alternativasElegidasAuditadas = 0;
    double sobrecostoVsMasBarataAuditoria = 0;
    int enviosEnCurso = 0;
    int enviosRetenidos = 0;
    int enviosRetenidosPico = 0;
    double toneladasEnviosEnCurso = 0;
    String detalleEnviosEnCurso = "";
    double esperaConsolidacionContenedorDia = 0;
    int consolidacionesRealizadas = 0;
    double capacidadConsolidacionOfrecida = 0;
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
    double toneladasRebalanceadasEntreDepositos = 0;
    int rebalanceosSinDestino = 0;
    double costoFleteEntreDepositos = 0;
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
    java.util.ArrayList<ReservaCapacidad> reservasCapacidad = new java.util.ArrayList<ReservaCapacidad>();
    LinkedHashMap<String, ReservaCapacidad> reservaPorClave = new LinkedHashMap<String, ReservaCapacidad>();
    LinkedHashMap<String, java.util.ArrayList<ReservaCapacidad>> reservasPorAsignacion = new LinkedHashMap<String, java.util.ArrayList<ReservaCapacidad>>();
    LinkedHashMap<String, AsignacionPedido> asignacionPorId = new LinkedHashMap<String, AsignacionPedido>();
    LinkedHashMap<String, LinkedHashMap<Integer, Integer>> ocupacionCapacidad = new LinkedHashMap<String, LinkedHashMap<Integer, Integer>>();
    int siguienteIdReservaCapacidad = 1;
    int capacidadReservadaTotal = 0;
    int capacidadConsumidaTotal = 0;
    int capacidadLiberadaTotal = 0;
    int reservasReprogramadas = 0;
    double toneladasReasignadasPorCapacidad = 0;
    int fallbacksPoliticaFija = 0;
    double costoAdicionalSaturacion = 0;
    int pedidosMultiCircuito = 0;
    LinkedHashMap<String, Integer> contenedoresPorCircuito = new LinkedHashMap<String, Integer>();
    java.util.ArrayList<String> diagnosticoAsignaciones = new java.util.ArrayList<String>();
    boolean exportarDiagnosticoCapacidad = false;
    LinkedHashMap<String, LinkedHashMap<Integer, Integer>> colaCapacidad = new LinkedHashMap<String, LinkedHashMap<Integer, Integer>>();
    java.util.ArrayList<UnidadFlotaProducto> unidadesFlotaProducto = new java.util.ArrayList<UnidadFlotaProducto>();
    java.util.ArrayList<ViajeProducto> viajesProducto = new java.util.ArrayList<ViajeProducto>();
    LinkedHashMap<String, ViajeProducto> viajeProductoPorId = new LinkedHashMap<String, ViajeProducto>();
    int siguienteIdViajeProducto = 1;
    double toneladasProductoEnTransito = 0;
    double toneladasReservadasParaTransporte = 0;
    int viajesProductoProgramados = 0;
    int viajesProductoIniciados = 0;
    int viajesProductoCompletados = 0;
    int viajesProductoCancelados = 0;
    double esperaFlotaProductoDiasAcumulada = 0;
    double esperaFlotaProductoDiasMaxima = 0;
    int movimientosConEsperaFlota = 0;
    double toneladasNoProgramadasPorFlota = 0;
    double toneladasProgramadasParcialmente = 0;
    int movimientosParcialesPorFlota = 0;
    double picoCamionesProductoEnRuta = 0;
    double camionesEnRutaDiaAcumulado = 0;
    int diasFlotaMedidos = 0;
    double toneladasTransferidasProgramadas = 0;
    double toneladasTransferidasSalidas = 0;
    LinkedHashMap<String, Integer> descartesFlotaPorMotivo = new LinkedHashMap<String, Integer>();
    java.util.HashSet<String> pedidosConDescartePorFlota = new java.util.HashSet<String>();
    int pedidosPerdieronCutoffPorFlota = 0;
    java.util.LinkedHashMap<String, double[]> posicionRedVisual = new java.util.LinkedHashMap<String, double[]>();
    java.util.LinkedHashMap<String, com.anylogic.engine.presentation.ShapeOval> nodoRedVisual = new java.util.LinkedHashMap<String, com.anylogic.engine.presentation.ShapeOval>();
    java.util.LinkedHashMap<String, com.anylogic.engine.presentation.ShapeText> etiquetaRedVisual = new java.util.LinkedHashMap<String, com.anylogic.engine.presentation.ShapeText>();
    java.util.LinkedHashMap<String, com.anylogic.engine.presentation.ShapeLine> arcoRedVisual = new java.util.LinkedHashMap<String, com.anylogic.engine.presentation.ShapeLine>();
    java.util.LinkedHashMap<String, double[]> flujoRedVisual = new java.util.LinkedHashMap<String, double[]>();
    com.anylogic.engine.presentation.ShapeText rotuloRedVisual = null;
    com.anylogic.engine.presentation.ShapeText resumenRedVisual = null;
    java.util.ArrayList<com.anylogic.engine.presentation.ShapeOval> figurasRedVisual = new java.util.ArrayList<com.anylogic.engine.presentation.ShapeOval>();
    com.anylogic.engine.presentation.ShapeText estadoRedVisual = null;
    int figurasRedVisualPico = 0;
    boolean redVisualDibujada = false;
    boolean redVisualGeografica = false;
    String fechaInicioCampaniaEfectiva = "";
    java.util.ArrayList<String> diagnosticoFlota = new java.util.ArrayList<String>();

    // ----- Colecciones -----
    ArrayList<Terminal> terminales = new ArrayList<Terminal>();
    ArrayList<Deposito> depositos = new ArrayList<Deposito>();

    // ----- Objetos embebidos (poblaciones y bloques de flowchart) -----
    //  planta
    //  lotes
    //  planes
    //  depFrinoa
    //  depGrupoPaz
    //  depControlUnion
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
    //  liberarCamion
    //  salidaEnvios
    //  entradaEnvios
    //  contenedoresExportacion

    // ----- Codigo de arranque (StartupCode) -----
    void onStartup() {
        depositos.clear();

        depositos.add(depFrinoa);
        depositos.add(depNorry);
        depositos.add(depBoreas);
        depositos.add(depRuta9);
        depositos.add(depDodero);
        depositos.add(depGrupoPaz);
        depositos.add(depControlUnion);

        terminales.clear();

        terminales.add(terminalZarate);
        terminales.add(terminalT4);

        cargarDatosEntrada();
        inicializarFlotaProducto();
        cargarStockInicial();
        abrirAuditoriaRed();
        dibujarRedVisual();          // vista de red (ADR-072): presentacion, no decide nada
    }

    // ----- Funciones -----

    LoteProducto crearLoteEnPlanta(TipoProducto producto, String material, double toneladas, Planta origen) {
        if (toneladas <= 0) {
            return null;
        }

        // Lote comercial acumulativo (ADR-047): la produccion diaria entra como una capa
        // nueva del mismo lote abierto; solo se abre una identidad comercial nueva cuando
        // el lote compatible ya esta cerrado por haber alcanzado su objetivo. El material
        // entra a la comparacion igual que cliente y calidad (ADR-067): dos materiales del
        // mismo producto no son el mismo lote comercial.
        String cliente = datos.escenario.clienteDefault;
        String calidad = datos.escenario.calidadDefault;

        LoteProducto lote = buscarLoteComercialAbierto(producto, material, cliente, calidad);

        if (lote == null) {
            lote = add_lotes();

            lote.idLote = siguienteIdLote;
            siguienteIdLote++;

            lote.producto = producto;
            lote.material = material;
            lote.diaProduccion = time();
            lote.estado = EstadoLote.EN_PLANTA;
            lote.ubicacionActual = origen;
            lote.costoAcumulado = 0;
            lote.pedidoAsignado = null;

            lote.cliente = cliente;
            lote.calidad = calidad;
            lote.toneladasObjetivo = datos.producto(producto, material).toneladasObjetivoLoteTn;
            lote.estadoComercial = EstadoComercialLote.ABIERTO;
            lote.toneladasIniciales = 0;
        }

        // La produccion del dia es una capa nueva con el mismo idLote. El saldo fisico vive
        // en las capas (ADR-023); la identidad comercial vive en el lote.
        inventario.ingresar(
            lote.idLote,
            producto,
            material,
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
            // El espacio se descuenta por lo que ya viene en camino: con viajes de varios dias
            // el volumen en ruta todavia no esta en el stock del deposito (ADR-061).
            double posible = min(
                toneladas,
                espacioDisponibleEfectivo(deposito, producto)
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

    Deposito mejorDestinoRebalanceo(Deposito origen, TipoProducto producto, double toneladas) {
        // ADR-066: entre los depositos que pueden recibir esto (habilitados, con tarifa
        // de flete, de sitio y distancia cargadas), elige el mas barato en total: flete
        // de reubicacion mas el holding que va a seguir devengando en el destino,
        // proyectado con el mismo horizonte que usa el resto del modelo (ADR-065, ADR-056).
        int dia = diaCampania();
        Deposito mejor = null;
        double menorCosto = Double.POSITIVE_INFINITY;

        for (Deposito destino : depositos) {

            if (destino == origen || !destino.habilitado) {
                continue;
            }

            if (!datos.hayTarifaFlete(dia, origen.idUbicacion, destino.idUbicacion, producto)) {
                continue;
            }

            if (!datos.hayTarifaSitio(dia, destino.idUbicacion, producto)) {
                continue;
            }

            if (datos.distanciaKmSimetrica(origen.idUbicacion, destino.idUbicacion) < 0) {
                continue;
            }

            double posible = min(toneladas, espacioDisponibleEfectivo(destino, producto));

            if (posible <= 0.0001) {
                continue;
            }

            double costoEstimado =
                datos.importeFlete(
                    dia, origen.idUbicacion, destino.idUbicacion, producto,
                    posible, viajesNecesariosCamion(posible))
                + posible * destino.getTarifaAlmacenamiento(producto) * horizonteHoldingEvitado();

            double costoPorTonelada = costoEstimado / posible;

            if (costoPorTonelada < menorCosto) {
                menorCosto = costoPorTonelada;
                mejor = destino;
            }
        }

        return mejor;
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

    LoteProducto buscarLoteMasAntiguoEnPlanta(TipoProducto producto, String material) {
        // Igual que buscarLoteMasAntiguoEnPlanta(producto), pero acotado a un
        // material (ADR-067): lo usa transferirLotesADeposito() para el cross dock de un
        // pedido puntual, donde el material que se mueve tiene que ser el que el pedido
        // pide. El heuristico agregado de transferirProductoADepositos() sigue usando el
        // overload de un solo parametro: ese no le presta a ningun pedido en particular.
        LoteProducto seleccionado = null;
        double menorDia = Double.POSITIVE_INFINITY;

        for (LoteProducto lote : lotes) {

            if (
                lote.producto == producto
                && lote.material.equals(material)
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

    LoteProducto buscarLoteMasAntiguoEnDeposito(String idUbicacion, TipoProducto producto) {
        // ADR-066: mismo criterio que buscarLoteMasAntiguoEnPlanta, para el lado deposito.
        LoteProducto seleccionado = null;
        double menorDia = Double.POSITIVE_INFINITY;

        for (LoteProducto lote : lotes) {

            if (
                lote.producto == producto
                && inventario.libreDeLoteEn(lote.idLote, idUbicacion) > 0.0001
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

    double transferirEntreDepositos(LoteProducto lote, Deposito origen, Deposito destino, double toneladas) {
        // ADR-066: movimiento fisico entre depositos de terceros, para stock que quedo
        // sin capacidad de salida en origen (sin cross dock) y que otro deposito puede
        // sacar mejor. Formula de costo de V-COST-06: OUT del origen, flete entre
        // depositos e IN en el destino, sin cargos de contenedor -eso lo paga despues
        // quien arme el contenedor en destino-.
        //
        // Simplificacion declarada: no usa la agenda de flota multidiaria (ADR-061), usa
        // la misma capacidad diaria agregada que el resto de las transferencias con esa
        // agenda desactivada. Queda como extension pendiente si hace falta.
        if (lote == null || origen == null || destino == null || toneladas <= 0.0001) {
            return 0;
        }

        if (!destino.habilitado) {
            return 0;
        }

        int dia = diaCampania();

        if (!datos.hayTarifaFlete(dia, origen.idUbicacion, destino.idUbicacion, lote.producto)) {
            return 0;
        }

        if (!datos.hayTarifaSitio(dia, destino.idUbicacion, lote.producto)) {
            return 0;
        }

        double distanciaKm = datos.distanciaKmSimetrica(origen.idUbicacion, destino.idUbicacion);

        if (distanciaKm < 0) {
            return 0;
        }

        double aMover = min(
            min(toneladas, inventario.libreDeLoteEn(lote.idLote, origen.idUbicacion)),
            destino.getEspacioDisponible(lote.producto)
        );

        if (aMover <= 0.0001) {
            return 0;
        }

        double camionDiaPorViaje =
            2 * distanciaKm
            / datos.escenario.velocidadCamionKmh
            / datos.escenario.horasOperativasDia;

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
            origen.idUbicacion,
            destino.idUbicacion,
            aMover,
            time()
        );

        if (movidas <= 0.0001) {
            return 0;
        }

        tomarFlotaProducto(
            origen.idUbicacion,
            destino.idUbicacion,
            viajesNecesariosCamion(movidas)
        );

        lote.depositoActual = destino;
        lote.diaIngresoDeposito = time();

        actualizarUbicacionLote(lote);

        double costoViaje = registrarFleteProducto(
            origen.idUbicacion,
            destino.idUbicacion,
            lote.producto,
            movidas,
            viajesNecesariosCamion(movidas),
            "" + lote.idLote,
            "",
            EstrategiaLogistica.SIN_DEFINIR,
            "REB-" + dia + "-" + lote.idLote + "-" + origen.idUbicacion + "-" + destino.idUbicacion
        );

        lote.costoAcumulado += costoViaje;
        costoFleteEntreDepositos += costoViaje;

        double costoOut = registrarOutDepositoTransferencia(
            origen.idUbicacion,
            lote.producto,
            movidas,
            "" + lote.idLote,
            "REB-OUT-" + dia + "-" + lote.idLote + "-" + origen.idUbicacion + "-" + destino.idUbicacion
        );

        lote.costoAcumulado += costoOut;

        lote.costoAcumulado += registrarInDeposito(
            destino.idUbicacion,
            lote.producto,
            movidas,
            "" + lote.idLote,
            "",
            "REB-IN-" + dia + "-" + lote.idLote + "-" + origen.idUbicacion + "-" + destino.idUbicacion
        );

        toneladasRebalanceadasEntreDepositos += movidas;

        return movidas;
    }

    void revisarRebalanceoEntreDepositos() {
        // ADR-066: un deposito sin cross dock puede terminar con stock que nunca gana
        // la competencia por un pedido -no porque salga caro, sino porque no puede
        // despacharlo a tiempo-. Si ademas ese stock ya lleva un tiempo sin moverse, se
        // reubica hacia el mejor destino disponible (mejorDestinoRebalanceo).
        for (TipoProducto producto : TipoProducto.values()) {

            for (Deposito origen : depositos) {

                if (datos.capacidadCrossDockDia(origen.idUbicacion) > 0.0001) {
                    continue;
                }

                double libre = inventario.libre(origen.idUbicacion, producto);

                if (libre <= 0.0001) {
                    continue;
                }

                java.util.List<Capa> capas = inventario.fifo(origen.idUbicacion, producto);

                if (capas.isEmpty()) {
                    continue;
                }

                double diaMasAntiguo = max(0, capas.get(0).diaIngreso);

                if (time() - diaMasAntiguo < diasEstimadosAlmacenamiento) {
                    continue;
                }

                LoteProducto lote = buscarLoteMasAntiguoEnDeposito(origen.idUbicacion, producto);

                if (lote == null) {
                    continue;
                }

                Deposito destino = mejorDestinoRebalanceo(origen, producto, libre);

                if (destino == null) {
                    rebalanceosSinDestino++;

                    // ADR-066: sin debugPlanificacion esto es invisible -no hay tabla de
                    // auditoria para el rebalanceo, a diferencia del resto del modelo-. Con la
                    // traza activa, dice si el bloqueo es de datos (falta tarifa/distancia
                    // deposito-deposito) o de verdad no hay ningun destino con lugar.
                    if (debugPlanificacion) {
                        traceln(
                            "[dia " + (int) floor(time()) + "] " + producto
                            + " rebalanceo sin destino desde " + origen.idUbicacion
                            + " (" + Math.round(libre) + " tn libres, "
                            + "revisar TarifaFleteProducto/Distancia " + origen.idUbicacion + "->*)"
                        );
                    }

                    continue;
                }

                transferirEntreDepositos(lote, origen, destino, libre);
            }
        }
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

            // Lo reservado para un viaje que todavia no salio ya esta saliendo: volver a
            // pedirlo programaria dos veces el mismo desborde (ADR-061). La politica no
            // cambia; cambia que ahora la salida tarda.
            if (usaFlotaMultidiaria()) {
                toneladas =
                    max(0, toneladas - toneladasComprometidasParaViajesDe(producto));
            }

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
        pedido.material = plan.material;

        // Recien aca producto y material son los del pedido real, asi que el contenedor
        // se resuelve aca y no en el startup del agente (ADR-069).
        pedido.tipoContenedor = obtenerTipoContenedor(producto, plan.material);
        pedido.capacidadContenedorTon = obtenerCapacidadContenedorTon(producto, plan.material);
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
        pedido.depositoComprometido = plan.depositoComprometido;
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

        // El round trip del portacontenedor empieza y termina en la terminal: el tramo vacio y
        // el cargado ya son el ciclo completo. El retorno a origen era un cuarto tramo que no
        // existe en la operacion (ADR-062). El campo queda por compatibilidad, en cero.
        envio.tiempoRetornoHoras = 0;

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
                contenedoresNecesarios(pedido.producto, pedido.material, toneladas)
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

    TipoContenedor obtenerTipoContenedor(TipoProducto producto, String material) {
        return datos.producto(producto, material).tipoContenedor;
    }

    double obtenerCapacidadContenedorTon(TipoProducto producto, String material) {
        return datos.producto(producto, material).capacidadContenedorTn;
    }

    double thcUsdContenedorPedido(int dia, Pedido pedido, String terminal) {
        // THC por naviera (ADR-068 seguimiento): confirmado por el usuario que el
        // costo de THC lo factura la naviera (la maritima), no el sitio. Si el libro
        // trajo Gastos_THC (datos.tarifasThc no vacia) se resuelve por naviera y tipo
        // de contenedor, calculado fresco via obtenerTipoContenedor() y no leido de
        // pedido.tipoContenedor: ese campo lo resuelve el StartupCode con los valores
        // default del agente antes de crearPedido(), y recien queda correcto mucho
        // mas tarde en crearContenedoresParaAsignacion() (no confiable en este punto,
        // que corre antes de elegir circuito). Sin Gastos_THC (contrato original) cae
        // al thc_usd_contenedor de siempre, por terminal y producto (TarifaSitio).
        if (!datos.tarifasThc.isEmpty()) {
            TipoContenedor tipoContenedor = obtenerTipoContenedor(pedido.producto, pedido.material);
            return datos.thcUsdContenedor(dia, pedido.naviera, tipoContenedor);
        }
        return datos.thcUsdContenedor(dia, terminal, pedido.producto);
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

        // La red del modelo es un superconjunto de la del libro: un escenario puede no
        // declarar un deposito que existe en el canvas. Se lo saca de la coleccion en vez de
        // dejarlo pidiendo capacidades y tarifas que el libro no tiene por que traer (ADR-069).
        for (int i = depositos.size() - 1; i >= 0; i--) {
            Deposito deposito = depositos.get(i);

            if (!datos.existeUbicacion(deposito.idUbicacion)) {
                deposito.habilitado = false;
                depositos.remove(i);

                traceln(
                    "Advertencia de datos: el libro no declara el deposito "
                    + deposito.idUbicacion
                    + ", que queda fuera de la red de esta corrida."
                );
            }
        }

        // El caso inverso si es un error: la red fisica son agentes del canvas (limitacion
        // declarada en ADR-069) y un deposito del libro sin agente no almacena nada. Sin este
        // aviso el sintoma aparece mucho despues como "la ubicacion no tiene capacidad".
        for (DatosEntrada.Ubicacion u : datos.ubicacionesDeTipo("DEPOSITO")) {
            if (u.habilitada && buscarDeposito(u.idUbicacion) == null) {
                error(
                    "El libro declara el deposito " + u.idUbicacion
                    + " pero la red del modelo no tiene un agente Deposito para el:"
                    + " hay que agregarlo en Main (ADR-069)."
                );
            }
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

        // Con la agenda de camiones el movimiento deja de ser instantaneo: se programan viajes
        // fisicos, el producto sale al salir el viaje y entra al llegar (ADR-061). El flete y el
        // ingreso al deposito se devengan en esos dos momentos, no aca.
        if (usaFlotaMultidiaria()) {
            return programarTransferenciaLote(lote, destino, toneladas, cruza);
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

    boolean tomarPosicionConsolidacion(ContenedorExportacion contenedor) {
        // Ejecutar la estiba de un contenedor. Con reserva se consume la posicion ya
        // comprometida y la ocupacion del dia no se incrementa dos veces; sin reserva -con la
        // agenda apagada, o despues de perder la posicion- solo puede usar lo que quedo libre.
        String idUbicacion = sitioConsolidacion(contenedor);

        int hoy = diaCampania();

        ReservaCapacidad reserva = reservaDeContenedor(contenedor);

        if (
            reserva != null
            && reserva.activa
            && !reserva.consumida
            && !reserva.liberada
        ) {

            // Todavia no es su dia: la posicion esta comprometida mas adelante en la ventana.
            if (reserva.diaPlanificado > hoy) {
                return false;
            }

            if (!consumirReservaCapacidad(reserva, hoy)) {
                return false;
            }

            contenedor.diaPlanificadoOperacion = hoy;

            consolidacionesRealizadas++;

            return true;
        }

        if (
            capacidadDisponibleDia(
                ReservaCapacidad.CONSOLIDACION, idUbicacion, hoy) < 1
        ) {
            return false;
        }

        ocuparCapacidad(ReservaCapacidad.CONSOLIDACION, idUbicacion, hoy, 1);

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

            if (granel && !flotaProductoDisponibleParaGranel(pedido, contenedor)) {
                contenedor.diasEsperaPosicion++;
                esperaConsolidacionContenedorDia++;
                contenedoresEnEspera++;
                continue;
            }

            if (
                !contenedor.esCrossDock
                && !tomarPosicionConsolidacion(contenedor)
            ) {
                // La cola mide falta de lugar, no espera planificada: el contenedor que todavia
                // no llego al dia de su posicion no esta haciendo cola, esta esperando su turno.
                ReservaCapacidad reservaEnEspera = reservaDeContenedor(contenedor);

                if (
                    reservaEnEspera == null
                    || reservaEnEspera.diaPlanificado <= diaCampania()
                ) {
                    registrarColaCapacidad(
                        ReservaCapacidad.CONSOLIDACION,
                        sitioConsolidacion(contenedor)
                    );
                }

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

            // La espera por una posicion es un arco de espera: sin ella la suma de las etapas no
            // reconstruye el tiempo del envio (ADR-064).
            registrarArcoEsperaPosicion(contenedor, contenedor.diasEsperaPosicion);

            if (granel) {
                ocuparFlotaParaGranel(pedido, contenedor);
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
        }
    }

    double capacidadCrossDockLibre(String idUbicacion) {
        // Cupo de cross dock libre hoy en ese deposito. Sale de la misma agenda que la
        // consolidacion pero con recurso propio: cruzar no consume posiciones de estiba y la
        // estiba no consume cupo de cruce (ADR-041, ADR-060).
        return capacidadDisponibleDia(
            ReservaCapacidad.CROSS_DOCK,
            idUbicacion,
            diaCampania());
    }

    int contenedoresNecesarios(TipoProducto producto, String material, double toneladas) {
        double capacidad =
            obtenerCapacidadContenedorTon(producto, material);

        return (int) ceil(toneladas / capacidad - 0.0001);
    }

    Deposito seleccionarSitioCrossDock(Pedido pedido) {
        // Deposito de cruce mas barato entre los que pueden recibir hoy aunque sea una parte
        // del saldo pendiente: con reserva parcial ya no hace falta que entre todo (ADR-055).
        Deposito mejorSitio = null;
        double menorCostoPorTonelada = Double.POSITIVE_INFINITY;

        double capacidadContenedor =
            obtenerCapacidadContenedorTon(pedido.producto, pedido.material);

        double pendiente =
            min(
                pedido.toneladasPendientesAsignar(),
                inventario.libre("PLANTA", pedido.producto, pedido.material)
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
                contenedoresNecesarios(pedido.producto, pedido.material, posible);

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

    double transferirLotesADeposito(TipoProducto producto, String material, Deposito destino, double toneladas, boolean cruza) {
        double pendiente = toneladas;

        while (pendiente > 0.0001) {

            // ADR-067: el lote movido tiene que ser del material del pedido, sino la
            // reserva posterior en destino no encuentra nada que reservar.
            LoteProducto lote =
                buscarLoteMasAntiguoEnPlanta(producto, material);

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
            obtenerCapacidadContenedorTon(pedido.producto, pedido.material);

        double toneladas =
            min(
                min(toneladasObjetivo, pedido.toneladasPendientesAsignar()),
                min(
                    inventario.libre("PLANTA", pedido.producto, pedido.material),
                    min(
                        espacioDisponibleEfectivo(sitio, pedido.producto),
                        capacidadCrossDockLibre(sitio.idUbicacion) * capacidadContenedor
                    )
                )
            );

        if (toneladas <= 0.0001) {
            return 0;
        }

        if (usaFlotaMultidiaria()) {

            // Cruzar exige que el camion llegue dentro de la jornada: si no, el producto tendria
            // que dormir en el sitio y eso ya no es cross dock (ADR-010, ADR-061).
            if (!cruceLlegaEnElDia("PLANTA", sitio.idUbicacion, toneladas)) {
                contarDescarteFlota(
                    ViajeProducto.CRUCE_SIN_LLEGADA_EN_EL_DIA, pedido.codigoPedido);
                crossDockReprogramados++;
                return 0;
            }

            ResultadoDisponibilidadFlota disponibilidad =
                evaluarDisponibilidadFlotaProducto(
                    "PLANTA", sitio.idUbicacion, toneladas, time(), floor(time()) + 1);

            if (!disponibilidad.puedeProgramarAlgo()) {
                contarDescarteFlota(
                    disponibilidad.motivo.isEmpty()
                    ? ViajeProducto.SIN_FLOTA_ANTES_CUTOFF
                    : disponibilidad.motivo,
                    pedido.codigoPedido);
                crossDockReprogramados++;
                return 0;
            }

            // Se cruza lo que la flota puede llevar hoy: el resto vuelve a competir manana.
            toneladas = min(toneladas, disponibilidad.toneladasProgramables);

        } else if (
            !flotaProductoAlcanza(
                "PLANTA",
                sitio.idUbicacion,
                toneladas
            )
        ) {
            crossDockReprogramados++;
            return 0;
        }

        // El cupo de cross dock se toma como reserva de la asignacion y no como un conteo
        // aparte: asi el contenedor que cruza tiene su posicion y la agenda no cuenta dos veces
        // la misma operacion (ADR-060). El volumen ya vino acotado por el cupo libre de hoy.
        if (
            capacidadCrossDockLibre(sitio.idUbicacion)
            < contenedoresNecesarios(pedido.producto, pedido.material, toneladas)
        ) {
            crossDockReprogramados++;
            return 0;
        }

        double movidas =
            transferirLotesADeposito(
                pedido.producto,
                pedido.material,
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
        anclarCalendarioDeCampania();
        habilitaCrossDock = escenario.habilitaCrossDock;
        exportarDiagnosticoCapacidad = escenario.exportarDiagnosticoCapacidad;
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
        // Producto que la campania no logro exportar: quedo en planta, en un deposito o arriba
        // de un camion que todavia no llego a destino al cierre (ADR-061).
        double total = toneladasProductoEnTransito;

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
        // Con la agenda de camiones (ADR-061) la restriccion es la fecha en que cada camion
        // vuelve a estar libre, no un balde diario: aca la capacidad diaria queda solo como
        // medida. Sin agenda sigue vigente ADR-044 y el balde es la restriccion.
        flotaProductoOfrecidaHoy = datos.escenario.camionesProducto;

        flotaProductoUsadaHoy =
            usaFlotaMultidiaria()
            ? camionesProductoEnRuta(time())
            : 0;

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

    LoteProducto buscarLoteComercialAbierto(TipoProducto producto, String material, String cliente, String calidad) {
        // Lote comercial abierto compatible por producto, material, cliente y calidad
        // (ADR-047, ADR-067). Hay a lo sumo uno abierto por combinacion: al cerrarse, el
        // siguiente ingreso abre una identidad comercial nueva.
        for (LoteProducto lote : lotes) {
            if (
                lote.producto == producto
                && lote.material.equals(material)
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

        // Circuito 4: el servicio no queda cumplido cuando llega el granel sino cuando el
        // contenedor esta armado, que es lo que el evaluador prometio (ADR-062).
        envio.diaListoEnTerminal = time();
    }

    void abrirAuditoriaRed() {
        // ADR-064: abre las tablas de auditoria de la corrida. El nivel es del escenario: en el
        // barrido son mil corridas y la auditoria completa escribe decenas de miles de filas por
        // corrida, asi que el default es DESACTIVADA y el modelo decide igual con auditoria o sin
        // ella (V-AUD-10).
        auditoria.nivel = nivelAuditoriaRed;

        if (!auditoria.activa()) {
            return;
        }

        // run_id es la clave que hace que dos corridas puedan convivir en la misma tabla. Es
        // deterministico a proposito: dos corridas iguales tienen que dar tablas iguales.
        auditoria.runId = idEscenario + "-R" + replica;

        auditoria.directorio = "resultados";

        new java.io.File(auditoria.directorio).mkdirs();

        auditoria.abrir(AuditoriaRed.DECISIONES, RegistroDecisionAlternativa.encabezadoCsv());
        auditoria.abrir(AuditoriaRed.ASIGNACIONES, AsignacionPedido.encabezadoCsv());
        auditoria.abrir(AuditoriaRed.ARCOS, RegistroEjecucionArco.encabezadoCsv());
        auditoria.abrir(AuditoriaRed.COSTOS, encabezadoCostosEventos());
        auditoria.abrir(AuditoriaRed.INVENTARIO, SnapshotInventario.encabezadoCsv());
    }

    String encabezadoCostosEventos() {
        // Encabezado de costos_eventos. La tabla es una vista de RegistroCostos y no una segunda
        // lista de cargos: si fueran dos fuentes, reconciliarlas solo probaria que dos
        // exportaciones coinciden, no que el modelo cobro una sola vez (ADR-064).
        return "run_id,escenario,replica,id_costo,dia,dia_campania,tipo_contable,categoria,"
            + "codigo_pedido,id_asignacion,id_decision,id_contenedor,id_lote,producto,material,circuito,"
            + "origen,destino,sitio,proveedor,unidad,cantidad,tarifa,importe_usd,id_operacion,"
            + "alcance,es_incremental,motivo,fecha";
    }

    void exportarCostosEventos() {
        // costos_eventos sale del mismo registro que paga la campania (ADR-052). La asignacion y la
        // decision no se guardan en el cargo: se resuelven aca por el contenedor, que ya lleva
        // idAsignacionPedido. Un cargo del lote (almacenaje, IN, flete de guarda) no pertenece a
        // ninguna asignacion y su alcance queda declarado como LOTE en vez de repartirse.
        if (!auditoria.activa()) {
            return;
        }

        costoRealPorAsignacion.clear();

        if (!registro.guardarDetalle) {
            traceln(
                "Auditoria de red: el registro de costos corre sin detalle, costos_eventos"
                + " queda vacio y asignaciones_elegidas informa costo real cero.");
            return;
        }

        java.util.LinkedHashMap<String, String> asignacionDeContenedor =
            new java.util.LinkedHashMap<String, String>();

        java.util.LinkedHashMap<String, String> materialDeContenedor =
            new java.util.LinkedHashMap<String, String>();

        java.util.LinkedHashMap<String, String> decisionDeAsignacion =
            new java.util.LinkedHashMap<String, String>();

        java.util.LinkedHashMap<String, Double> diaDecisionDePedido =
            new java.util.LinkedHashMap<String, Double>();

        // Material por lote (ADR-067/071): el cargo solo lleva idLote, asi que el material se
        // resuelve aca contra el agente, igual que la asignacion se resuelve contra el contenedor.
        java.util.LinkedHashMap<String, String> materialDeLote =
            new java.util.LinkedHashMap<String, String>();

        for (LoteProducto lote : lotes) {
            materialDeLote.put("" + lote.idLote, lote.material);
        }

        for (Pedido pedido : pedidos) {

            for (AsignacionPedido asignacion : pedido.asignaciones) {

                decisionDeAsignacion.put(asignacion.idAsignacion, asignacion.idDecision);

                Double primera = diaDecisionDePedido.get(pedido.codigoPedido);

                if (primera == null || asignacion.diaAsignacion < primera) {
                    diaDecisionDePedido.put(pedido.codigoPedido, asignacion.diaAsignacion);
                }
            }

            for (ContenedorExportacion contenedor : pedido.contenedores) {
                asignacionDeContenedor.put(
                    contenedor.idContenedor, contenedor.idAsignacionPedido);
                materialDeContenedor.put(
                    contenedor.idContenedor, contenedor.material);
            }
        }

        for (RegistroCostos.Cargo cargo : registro.detalle()) {

            String idAsignacion =
                cargo.codigoContenedor == null || cargo.codigoContenedor.isEmpty()
                ? ""
                : asignacionDeContenedor.get(cargo.codigoContenedor);

            if (idAsignacion == null) {
                idAsignacion = "";
            }

            // Material del cargo (ADR-067/071): por contenedor si es un cargo de contenedor, si no
            // por lote (almacenaje, IN, flete de guarda). Un cargo de alcance RED (oportunidad del
            // frio propio, penalidad de sobrecarga) no tiene material: es de toda la red.
            String material =
                cargo.codigoContenedor != null && !cargo.codigoContenedor.isEmpty()
                ? materialDeContenedor.get(cargo.codigoContenedor)
                : (cargo.idLote != null && !cargo.idLote.isEmpty()
                    ? materialDeLote.get(cargo.idLote)
                    : null);

            if (material == null) {
                material = "";
            }

            String idDecision =
                idAsignacion.isEmpty() ? "" : decisionDeAsignacion.get(idAsignacion);

            if (idDecision == null) {
                idDecision = "";
            }

            String alcance =
                !idAsignacion.isEmpty()
                ? "CONTENEDOR"
                : (cargo.codigoPedido != null && !cargo.codigoPedido.isEmpty()
                    ? "PEDIDO"
                    : (cargo.idLote != null && !cargo.idLote.isEmpty() ? "LOTE" : "RED"));

            // Incremental es el cargo devengado desde que el pedido comprometio producto: antes de
            // esa fecha el costo existia igual y el evaluador lo trata como hundido (ADR-054).
            Double diaDecision =
                cargo.codigoPedido == null ? null : diaDecisionDePedido.get(cargo.codigoPedido);

            boolean incremental =
                diaDecision != null && cargo.dia >= diaDecision.doubleValue() - 0.0001;

            if (!idAsignacion.isEmpty()) {

                Double acumulado = costoRealPorAsignacion.get(idAsignacion);

                costoRealPorAsignacion.put(
                    idAsignacion,
                    (acumulado == null ? 0 : acumulado.doubleValue()) + cargo.importe);
            }

            auditoria.escribir(
                AuditoriaRed.COSTOS,
                AuditoriaRed.txt(auditoria.runId) + "," + AuditoriaRed.txt(idEscenario) + ","
                + AuditoriaRed.ent(replica) + "," + AuditoriaRed.ent(cargo.id) + ","
                + AuditoriaRed.num(cargo.dia) + "," + AuditoriaRed.ent((long) Math.floor(cargo.dia))
                + "," + AuditoriaRed.txt("" + cargo.tipo) + ","
                + AuditoriaRed.txt("" + cargo.categoria) + ","
                + AuditoriaRed.txt(cargo.codigoPedido) + "," + AuditoriaRed.txt(idAsignacion) + ","
                + AuditoriaRed.txt(idDecision) + "," + AuditoriaRed.txt(cargo.codigoContenedor) + ","
                + AuditoriaRed.txt(cargo.idLote) + "," + AuditoriaRed.txt("" + cargo.producto) + ","
                + AuditoriaRed.txt(material) + ","
                + AuditoriaRed.txt("" + cargo.estrategia) + "," + AuditoriaRed.txt(cargo.origen) + ","
                + AuditoriaRed.txt(cargo.destino) + "," + AuditoriaRed.txt(cargo.sitio) + ","
                + AuditoriaRed.txt(cargo.proveedor) + "," + AuditoriaRed.txt("" + cargo.unidad) + ","
                + AuditoriaRed.num(cargo.cantidad) + "," + AuditoriaRed.num(cargo.tarifa) + ","
                + AuditoriaRed.num(cargo.importe) + "," + AuditoriaRed.txt(cargo.idOperacion) + ","
                + AuditoriaRed.txt(alcance) + "," + AuditoriaRed.si(incremental) + ","
                + AuditoriaRed.txt(cargo.motivo) + ","
                + AuditoriaRed.txt(AuditoriaRed.fecha(Math.floor(cargo.dia))));
        }
    }

    void exportarAsignacionesElegidas() {
        // asignaciones_elegidas sale de AsignacionPedido, que ya es la unidad de compromiso del
        // pedido (ADR-055): no hay una lista paralela de asignaciones auditadas que pueda quedar
        // desincronizada. Se escribe al cierre porque el ciclo real y el costo recien existen ahi.
        if (!auditoria.activa()) {
            return;
        }

        for (Pedido pedido : pedidos) {

            for (AsignacionPedido asignacion : pedido.asignaciones) {

                int creados = 0;
                int entregados = 0;

                for (ContenedorExportacion contenedor : pedido.contenedores) {

                    if (!asignacion.idAsignacion.equals(contenedor.idAsignacionPedido)) {
                        continue;
                    }

                    creados++;

                    if (contenedor.estado == EstadoContenedor.EXPORTADO) {
                        entregados++;
                    }
                }

                Double costoReal = costoRealPorAsignacion.get(asignacion.idAsignacion);

                auditoria.escribir(
                    AuditoriaRed.ASIGNACIONES,
                    asignacion.toCsv(
                        auditoria.runId, idEscenario, replica,
                        costoReal == null ? 0 : costoReal.doubleValue(),
                        creados, entregados));
            }
        }
    }

    void cerrarAuditoriaRed() {
        // Cierre de la auditoria: las tablas que solo existen al final (asignaciones y costos), las
        // reconciliaciones y el manifiesto que el tablero web va a leer para saber que hay. Corre al
        // terminar la corrida y no el ultimo dia de campania: el flujo sigue moviendo envios despues
        // del ultimo paso diario y esos arcos tambien son hechos de la corrida.
        if (!auditoria.activa() || auditoria.cerrada) {
            return;
        }

        exportarCostosEventos();
        exportarAsignacionesElegidas();
        reconciliarAuditoriaRed();
        escribirManifiestoAuditoria();
        escribirEsquemaAuditoria();

        auditoria.cerrar();

        traceln(resumenAuditoriaRed());
        traceln(auditoria.resumen());
    }

    void escribirManifiestoAuditoria() {
        // Manifiesto de la corrida: que tablas hay, cuantas filas y con que claves se unen. Lo
        // escribe el modelo y no un documento aparte para que el esquema no pueda quedar viejo: el
        // tablero web lee esto, no una convencion.
        java.io.PrintWriter salida = null;

        try {
            salida =
                new java.io.PrintWriter(
                    auditoria.directorio + "/manifiesto_auditoria_" + auditoria.runId + ".json",
                    "UTF-8");

            salida.println("{");
            salida.println("  \"run_id\": \"" + auditoria.runId + "\",");
            salida.println("  \"version_esquema\": \"" + AuditoriaRed.VERSION_ESQUEMA + "\",");
            salida.println("  \"escenario\": \"" + idEscenario + "\",");
            salida.println("  \"replica\": " + replica + ",");
            salida.println("  \"nivel_auditoria\": \"" + auditoria.nivel + "\",");
            salida.println("  \"duracion_campania_dias\": " + duracionCampaniaDias + ",");
            salida.println(
                "  \"fecha_inicio_campania\": \"" + fechaInicioCampaniaEfectiva + "\",");
            salida.println("  \"generado\": \"" + new java.util.Date() + "\",");
            salida.println("  \"tablas\": {");

            String[] tablas = {
                AuditoriaRed.DECISIONES, AuditoriaRed.ASIGNACIONES, AuditoriaRed.ARCOS,
                AuditoriaRed.COSTOS, AuditoriaRed.INVENTARIO
            };

            for (int i = 0; i < tablas.length; i++) {
                salida.println(
                    "    \"" + tablas[i] + "\": {\"archivo\": \"" + tablas[i] + ".csv\","
                    + " \"filas\": " + auditoria.filasDe(tablas[i]) + "},");
            }

            salida.println(
                "    \"" + AuditoriaRed.CAPACIDAD + "\": {\"archivo\":"
                + " \"capacidad_por_dia.csv\", \"filas\": -1}");

            salida.println("  },");
            salida.println("  \"claves\": {");
            salida.println("    \"decisiones_alternativas\": [\"run_id\", \"id_alternativa\"],");
            salida.println("    \"asignaciones_elegidas\": [\"run_id\", \"id_asignacion\"],");
            salida.println("    \"ejecucion_arcos\": [\"run_id\", \"id_evento_arco\"],");
            salida.println("    \"costos_eventos\": [\"run_id\", \"id_costo\"],");
            salida.println(
                "    \"snapshot_inventario\": [\"run_id\", \"dia\", \"ubicacion\","
                + " \"producto\"],");
            salida.println(
                "    \"snapshot_capacidad_recursos\": [\"run_id\", \"dia\","
                + " \"tipo_recurso\", \"ubicacion\"]");
            salida.println("  }");
            salida.println("}");

        } catch (java.io.IOException e) {
            traceln("No se pudo escribir el manifiesto de auditoria: " + e.getMessage());

        } finally {

            if (salida != null) {
                salida.close();
            }
        }
    }

    void fotografiarStockInicialDelDia() {
        // Stock de cada nodo antes de que el dia haga nada. Sin esta foto el balance diario se
        // derivaria del propio stock final y C-12 no podria fallar nunca (ADR-064).
        if (!auditoria.activa()) {
            return;
        }

        stockInicialDelDia.clear();

        inventario.reiniciarFlujoDia();

        for (TipoProducto producto : TipoProducto.values()) {

            stockInicialDelDia.put(
                "PLANTA|" + producto, inventario.stock("PLANTA", producto));

            for (Deposito deposito : depositos) {
                stockInicialDelDia.put(
                    deposito.idUbicacion + "|" + producto,
                    inventario.stock(deposito.idUbicacion, producto));
            }

            for (Terminal terminal : terminales) {
                stockInicialDelDia.put(
                    terminal.idUbicacion + "|" + producto,
                    inventario.stock(terminal.idUbicacion, producto));
            }
        }
    }

    void tomarSnapshotInventarioDelDia() {
        // Foto diaria del inventario por nodo y producto, con el balance del dia (C-12):
        //
        //     stock inicial + ingresos - egresos = stock final
        //
        // validarInventario() ya verificaba el total de la red; esto lo verifica por nodo, que es
        // donde se ve un deposito lleno o un nodo que pierde producto.
        if (!auditoria.activa()) {
            return;
        }

        int dia = diaCampania();

        for (TipoProducto producto : TipoProducto.values()) {

            for (int i = 0; i <= depositos.size() + terminales.size(); i++) {

                String idUbicacion;
                String tipoUbicacion;
                double capacidad;
                double costoTnDia;

                if (i == 0) {
                    idUbicacion = "PLANTA";
                    tipoUbicacion = "PLANTA";
                    capacidad = planta.getCapacidad(producto);
                    costoTnDia = 0;

                } else if (i <= depositos.size()) {
                    Deposito deposito = depositos.get(i - 1);
                    idUbicacion = deposito.idUbicacion;
                    tipoUbicacion = "DEPOSITO";
                    capacidad = deposito.getCapacidad(producto);
                    costoTnDia = deposito.getTarifaAlmacenamiento(producto);

                } else {
                    Terminal terminal = terminales.get(i - 1 - depositos.size());
                    idUbicacion = terminal.idUbicacion;
                    tipoUbicacion = "TERMINAL";
                    capacidad = 0;
                    costoTnDia = 0;
                }

                double stock = inventario.stock(idUbicacion, producto);
                double ingresos = inventario.ingresosDia(idUbicacion, producto);
                double egresos = inventario.egresosDia(idUbicacion, producto);

                Double inicial = stockInicialDelDia.get(idUbicacion + "|" + producto);

                if (stock <= 0.0001 && ingresos <= 0.0001 && egresos <= 0.0001) {
                    continue;
                }

                SnapshotInventario fila = new SnapshotInventario();

                fila.runId = auditoria.runId;
                fila.escenario = idEscenario;
                fila.replica = replica;
                fila.dia = dia;
                fila.ubicacion = idUbicacion;
                fila.tipoUbicacion = tipoUbicacion;
                fila.producto = "" + producto;
                fila.capacidadTn = capacidad;
                fila.stockInicialDiaTn = inicial == null ? 0 : inicial.doubleValue();
                fila.stockFisicoTn = stock;
                fila.stockLibreTn = inventario.libre(idUbicacion, producto);
                fila.stockReservadoPedidosTn = inventario.reservado(idUbicacion, producto);
                fila.stockEnTransitoEntradaTn = toneladasEnTransitoHacia(idUbicacion, producto);
                fila.ingresosDiaTn = ingresos;
                fila.egresosDiaTn = egresos;
                fila.produccionDiaTn =
                    "PLANTA".equals(idUbicacion) ? datos.produccionDelDia(dia, producto) : 0;
                fila.ocupacionPct = capacidad <= 0 ? 0 : 100.0 * stock / capacidad;
                fila.costoAlmacenajeDiaUsd = stock * costoTnDia;
                fila.lotesAbiertos = inventario.cantidadLotes(idUbicacion, producto);

                double masAntiguo = 0;
                double sumaDias = 0;
                double sumaTn = 0;

                for (Capa capa : inventario.fifo(idUbicacion, producto)) {
                    double antiguedad = Math.max(0, time() - capa.diaIngreso);
                    masAntiguo = Math.max(masAntiguo, antiguedad);
                    sumaDias += antiguedad * capa.toneladas;
                    sumaTn += capa.toneladas;
                }

                fila.loteMasAntiguoDias = masAntiguo;
                fila.diasStockPromedio = sumaTn <= 0.0001 ? 0 : sumaDias / sumaTn;

                // El viaje reserva su carga en el origen con clave VIAJE| (ADR-061): lo que ya esta
                // comprometido para salir no es stock libre del nodo.
                fila.stockReservadoViajesTn = 0;
                fila.stockEnTransitoSalidaTn = 0;

                for (ViajeProducto viaje : viajesProducto) {

                    if (viaje.producto != producto) {
                        continue;
                    }

                    if (viaje.origen.equals(idUbicacion) && !viaje.stockRetiradoOrigen && viaje.vivo()) {
                        fila.stockReservadoViajesTn += viaje.toneladas;
                    }

                    if (viaje.origen.equals(idUbicacion) && viaje.enTransito()) {
                        fila.stockEnTransitoSalidaTn += viaje.toneladas;
                    }
                }

                if (Math.abs(fila.descuadre()) > 0.001) {
                    descuadresInventarioAuditoria++;
                    error(
                        "C-12: el balance de " + idUbicacion + " " + producto + " el dia " + dia
                        + " no cierra: inicial " + fila.stockInicialDiaTn
                        + " + ingresos " + fila.ingresosDiaTn
                        + " - egresos " + fila.egresosDiaTn
                        + " != final " + fila.stockFisicoTn);
                }

                auditoria.escribir(AuditoriaRed.INVENTARIO, fila.toCsv());
            }
        }
    }

    void etiquetarAlternativas(String idDecision, java.util.List<AlternativaCircuito> alternativas) {
        // Identidad de cada alternativa de la ronda (ADR-064). Se etiqueta despues de generarlas y
        // antes de ejecutar, asi la asignacion puede apuntar a la alternativa exacta que la creo.
        for (int i = 0; i < alternativas.size(); i++) {
            alternativas.get(i).idAlternativa = idDecision + "-A" + (i + 1);
        }
    }

    double[] componentesCicloFisico(Pedido pedido, String idOrigen, EstrategiaLogistica circuito, boolean esCrossDock, double toneladas) {
        // Las mismas componentes que suma horasCicloFisico(), separadas para poder auditar el
        // tiempo por etapa. No se refactoriza horasCicloFisico() para que las use: cambiar el orden
        // de una suma de doubles cambia el ultimo decimal, y el evaluador decide con ese numero.
        // La suma se verifica contra el original antes de escribirla (C-13).
        boolean granel = circuito == EstrategiaLogistica.CONSOLIDACION_TERMINAL;

        String terminal = pedido.puertoSalida.idUbicacion;

        double velocidad = datos.escenario.velocidadCamionKmh;

        DatosEntrada.Ubicacion origen = datos.ubicacion(idOrigen);

        DatosEntrada.Ubicacion puerto = datos.ubicacion(terminal);

        double distancia = datos.distanciaKm(idOrigen, terminal);

        double[] componentes = new double[6];

        componentes[0] = esCrossDock ? datos.distanciaKm("PLANTA", idOrigen) / velocidad : 0;

        componentes[1] =
            granel
            ? toneladas / origen.velocidadCargaTnHora
            : toneladas / origen.velocidadConsolidacionTnHora;

        componentes[2] = granel ? 0 : distancia / velocidad;

        componentes[3] = distancia / velocidad;

        componentes[4] = toneladas / puerto.velocidadDescargaTnHora;

        componentes[5] = granel ? toneladas / puerto.velocidadConsolidacionTnHora : 0;

        return componentes;
    }

    RegistroDecisionAlternativa filaDecision(Pedido pedido, AlternativaCircuito alternativa) {
        // La parte de la fila que describe al pedido, la alternativa y lo que el modelo veia
        // cuando la evaluo. Nada de esto se calcula: se lee de donde ya estaba.
        RegistroDecisionAlternativa fila = new RegistroDecisionAlternativa();

        fila.runId = auditoria.runId;
        fila.escenario = idEscenario;
        fila.replica = replica;
        fila.diaSimulacion = time();
        fila.diaCampania = diaCampania();
        fila.politicaSeleccion = "" + politicaSeleccion;
        fila.criterioOrden =
            (datos.escenario.servicioMinimoProyectado > 0 ? "servicio_y_" : "")
            + (decideEndToEnd() ? "menor_costo_unitario_end_to_end" : "menor_costo_unitario_incremental");

        fila.codigoPedido = pedido.codigoPedido;
        fila.producto = "" + pedido.producto;
        fila.tipoContenedor = "" + pedido.tipoContenedor;
        fila.terminal = pedido.puertoSalida.idUbicacion;
        fila.estadoPedidoAntes = "" + pedido.estado;
        fila.toneladasSolicitadas = pedido.toneladasSolicitadas;
        fila.toneladasEntregadasPrevias = pedido.toneladasEntregadas;
        fila.toneladasEnProcesoPrevias = pedido.toneladasDespachadas - pedido.toneladasEntregadas;
        fila.toneladasReservadasPrevias = pedido.toneladasReservadas;
        fila.toneladasPendientesAsignar = pedido.toneladasPendientesAsignar();
        fila.contenedoresPendientesEstimados =
            contenedoresNecesarios(pedido.producto, pedido.material, pedido.toneladasPendientesAsignar());
        fila.diaConocimiento = pedido.diaConocimiento;
        fila.diaAperturaRetiro = pedido.diaAperturaRetiroVacio;
        fila.diaCutoff = pedido.fechaLimiteTerminal;
        fila.diasHastaCutoff = pedido.fechaLimiteTerminal - time();
        fila.cantidadOrigenesPrevios = pedido.asignaciones.size();

        fila.circuito = "" + alternativa.circuito;
        fila.esCrossDock = alternativa.esCrossDock;
        fila.origenStock = alternativa.idOrigen;
        fila.sitioEstiba = alternativa.sitioEstiba;
        fila.destinoFinal = pedido.puertoSalida.idUbicacion;
        fila.requiereFlotaProducto = alternativa.requiereFlotaProducto;
        fila.requierePortacontenedor =
            alternativa.circuito != EstrategiaLogistica.CONSOLIDACION_TERMINAL;
        fila.requierePosicion = !alternativa.esCrossDock;
        fila.tipoRecursoCapacidad = alternativa.tipoRecursoCapacidad;
        fila.ubicacionRecursoCapacidad = alternativa.idUbicacionCapacidad;

        fila.stockFisicoOrigenTn = inventario.stock(alternativa.idOrigen, pedido.producto, pedido.material);
        fila.stockLibreOrigenTn = inventario.libre(alternativa.idOrigen, pedido.producto, pedido.material);
        fila.stockReservadoOrigenTn = inventario.reservado(alternativa.idOrigen, pedido.producto, pedido.material);
        fila.stockEnTransitoHaciaOrigenTn =
            toneladasEnTransitoHacia(alternativa.idOrigen, pedido.producto);

        Deposito sitio = buscarDeposito(alternativa.sitioEstiba);

        if (sitio != null) {
            fila.espacioFisicoSitioTn = sitio.getEspacioDisponible(pedido.producto);
            fila.espacioEfectivoSitioTn = espacioDisponibleEfectivo(sitio, pedido.producto);
            fila.ocupacionSitioPct =
                sitio.getCapacidad(pedido.producto) <= 0
                ? 0
                : 100.0 * inventario.stock(sitio.idUbicacion, pedido.producto)
                    / sitio.getCapacidad(pedido.producto);
        }

        fila.cupoCrossdockLibreCont =
            alternativa.esCrossDock
            ? (int) Math.round(capacidadCrossDockLibre(alternativa.idOrigen))
            : 0;
        fila.posicionesDisponiblesAntesCutoff = alternativa.contenedoresConCapacidad;
        fila.flotaProductoDisponible = flotaProductoLibreHoy();
        fila.primeraSalidaFlota = alternativa.primeraSalidaProducto;
        fila.ultimaLlegadaFlota = alternativa.ultimaLlegadaProducto;
        fila.esperaFlotaDias = alternativa.esperaFlotaDias;
        fila.portacontenedoresLibres = (int) Math.round(flotaPortacontenedores.idle());
        fila.portacontenedoresOcupados = (int) Math.round(flotaPortacontenedores.busy());

        fila.toneladasSinRestriccionCapacidad = alternativa.toneladasSinRestriccionCapacidad;
        fila.toneladasFactibles = alternativa.toneladas;
        fila.contenedoresFactibles = alternativa.contenedores;
        fila.contenedoresConCapacidad = alternativa.contenedoresConCapacidad;
        fila.viajesProductoRequeridos = viajesNecesariosCamion(alternativa.toneladas);
        fila.viajesProductoFactibles = alternativa.viajesFactiblesPorFlota;
        fila.esAsignacionParcial =
            alternativa.toneladas + 0.0001 < pedido.toneladasPendientesAsignar();
        fila.porcentajePedidoCubierto =
            pedido.toneladasSolicitadas <= 0.0001
            ? 0
            : 100.0 * alternativa.toneladas / pedido.toneladasSolicitadas;

        // generarAlternativas() agrega una alternativa de transferencia deposito-deposito cuyo
        // origen no es un nodo de la red: existe solo para dejar escrito que el modelo no la
        // mueve. Esa no tiene ciclo fisico que descomponer.
        if (datos.existeUbicacion(alternativa.idOrigen)) {

            double[] etapas =
                componentesCicloFisico(
                    pedido, alternativa.idOrigen, alternativa.circuito, alternativa.esCrossDock,
                    alternativa.toneladas);

            double suma = 0;

            for (int i = 0; i < etapas.length; i++) {
                suma += etapas[i];
            }

            double total = horasCicloAlternativa(pedido, alternativa);

            // C-13: el tiempo por etapa tiene que sumar el ciclo con el que se decidio.
            if (Math.abs(suma - total) > 0.0001) {
                error(
                    "C-13: las etapas de " + alternativa.clave() + " suman " + suma
                    + " y el ciclo del evaluador es " + total);
            }

            fila.horasFleteProducto = etapas[0];
            fila.horasCargaEstiba = etapas[1];
            fila.horasViajeVacio = etapas[2];
            fila.horasViajeCargado = etapas[3];
            fila.horasDescargaTerminal = etapas[4];
            fila.horasConsolidacionTerminal = etapas[5];
            fila.horasCicloFisicoTotal = total;
        }

        fila.diaEntregaEstimado = alternativa.diaEntregaEstimado;
        fila.holguraEstimadaDias = pedido.fechaLimiteTerminal - alternativa.diaEntregaEstimado;
        fila.llegaATiempoEstimado = alternativa.llegaATiempo;

        fila.costoFleteProducto = alternativa.costoFleteProducto;
        fila.costoRoundTrip = alternativa.costoRoundTrip;
        fila.costoEstiba = alternativa.costoEstiba;
        fila.costoOut = alternativa.costoOut;
        fila.costoThc = alternativa.costoTHC;
        fila.costoTerminal = alternativa.costoTerminal;
        fila.costoDespachante = alternativa.costoDespachante;
        fila.costoInHundido = alternativa.costoInHundido;
        fila.costoAlmacenajeHundido = alternativa.costoAlmacenajeHundido;
        fila.costoFleteHundido = alternativa.costoFleteHundido;
        fila.costoHistorico = alternativa.costoHistorico;
        fila.costoIncremental = alternativa.costoIncremental;
        fila.costoEndToEnd = alternativa.costoEndToEnd;
        fila.costoIncrementalUsdTn = alternativa.costoUnitarioSegun(false);
        fila.costoEndToEndUsdTn = alternativa.costoUnitarioSegun(true);
        fila.costoIncrementalUsdCont =
            alternativa.contenedores <= 0
            ? 0
            : alternativa.costoIncremental / alternativa.contenedores;
        fila.costoUnitarioSinRestriccion = alternativa.costoUnitarioSinRestriccion;

        fila.factible = alternativa.factible;
        fila.codigoMotivo = alternativa.codigoMotivo;
        fila.detalleMotivo = alternativa.motivoNoFactible;
        fila.idAlternativa = alternativa.idAlternativa;

        return fila;
    }

    void registrarDecisionRonda(Pedido pedido, String idDecision, int ronda, java.util.List<AlternativaCircuito> alternativas, java.util.List<AlternativaCircuito> ranking, AlternativaCircuito ejecutada, double tomadas, double saldoAntes) {
        // Una fila por alternativa de la ronda. La ronda es parte de la identidad porque
        // asignarConEvaluador() vuelve a generar las alternativas en cada vuelta: la misma
        // alternativa evaluada dos veces en el mismo dia son dos hechos distintos (ADR-055).
        //
        // El nivel RESUMIDA escribe solo la elegida: las descartadas son el grueso del volumen y
        // solo hacen falta para explicar por que no gano otra.
        if (!auditoria.activa()) {
            return;
        }

        decisionesAuditadas++;

        double costoElegida =
            ejecutada == null ? 0 : ejecutada.costoUnitarioSegun(decideEndToEnd());

        for (AlternativaCircuito alternativa : alternativas) {

            boolean elegida = alternativa == ejecutada;

            String resultado =
                elegida
                ? RegistroDecisionAlternativa.ELEGIDA
                : (AlternativaCircuito.NO_TOMADA_AL_EJECUTAR.equals(alternativa.codigoMotivo)
                    ? RegistroDecisionAlternativa.INTENTADA_FALLIDA
                    : (alternativa.factible
                        ? RegistroDecisionAlternativa.NO_INTENTADA
                        : RegistroDecisionAlternativa.NO_FACTIBLE));

            if (!alternativa.codigoMotivo.isEmpty()) {

                Long cantidad = motivosAuditoria.get(alternativa.codigoMotivo);

                motivosAuditoria.put(
                    alternativa.codigoMotivo,
                    (cantidad == null ? 0L : cantidad.longValue()) + 1);
            }

            if (elegida) {
                alternativasElegidasAuditadas++;
            }

            if (!elegida && !auditoria.registraDescartadas()) {
                continue;
            }

            RegistroDecisionAlternativa fila = filaDecision(pedido, alternativa);

            fila.idDecision = idDecision;
            fila.ronda = ronda;
            fila.ordenRanking = ranking.indexOf(alternativa) + 1;
            fila.resultadoEjecucion = resultado;
            fila.toneladasTomadas = elegida ? tomadas : 0;
            fila.costoElegidaUsdTn = costoElegida;
            fila.saldoPedidoAntes = saldoAntes;
            fila.saldoPedidoDespues = pedido.toneladasPendientesAsignar();

            double unitario = fila.costoIncrementalUsdTn;

            if (decideEndToEnd()) {
                unitario = fila.costoEndToEndUsdTn;
            }

            fila.diferenciaVsElegidaUsdTn =
                ejecutada == null || Double.isInfinite(unitario) ? 0 : unitario - costoElegida;

            // La alternativa mas barata que no se pudo usar es el precio de la restriccion: es el
            // numero que explica cuanto cuesta la capacidad que falta (ADR-060).
            fila.esMasBarataNoFactible =
                !alternativa.factible
                && ejecutada != null
                && !Double.isInfinite(alternativa.costoUnitarioSinRestriccion)
                && alternativa.costoUnitarioSinRestriccion < costoElegida - 0.0001;

            if (fila.esMasBarataNoFactible) {
                sobrecostoVsMasBarataAuditoria +=
                    (costoElegida - alternativa.costoUnitarioSinRestriccion) * tomadas;
            }

            auditoria.escribir(AuditoriaRed.DECISIONES, fila.toCsv());
        }
    }

    void emitirArco(RegistroEjecucionArco arco) {
        // Una fila de ejecucion_arcos, con el agregado que alimenta la vista de lectura.
        if (!auditoria.activa()) {
            return;
        }

        arco.runId = auditoria.runId;
        arco.escenario = idEscenario;
        arco.replica = replica;
        arco.idEventoArco = ++secuenciaArcoAuditoria;

        double[] agregado = arcosAuditoria.get(arco.tipoArco);

        if (agregado == null) {
            agregado = new double[4];
            arcosAuditoria.put(arco.tipoArco, agregado);
        }

        agregado[0]++;
        agregado[1] += arco.toneladas;
        agregado[2] += arco.duracionRealHoras;
        agregado[3] += arco.duracionEsperadaHoras < 0 ? 0 : arco.duracionEsperadaHoras;

        auditoria.escribir(AuditoriaRed.ARCOS, arco.toCsv());
    }

    String tipoArcoDeBloque(String bloque) {
        // Traduccion del bloque del flowchart al arco fisico que representa. Un bloque que no es un
        // movimiento ni una espera fisica no es un arco: el almacenaje y los cargos de deposito ya
        // viven en costos_eventos y duplicarlos aca haria que el costo por arco no reconcilie.
        if ("colaCamiones".equals(bloque)) {
            return RegistroEjecucionArco.ESPERA_PORTACONTENEDOR;
        }

        if ("viajarVacioAlOrigen".equals(bloque)) {
            return RegistroEjecucionArco.TERMINAL_ORIGEN_VACIO;
        }

        if ("cargarCamion".equals(bloque) || "cargarGranel".equals(bloque)) {
            return RegistroEjecucionArco.CARGA_CONSOLIDACION;
        }

        if ("viajarPuerto".equals(bloque) || "viajarTerminalGranel".equals(bloque)) {
            return RegistroEjecucionArco.ORIGEN_TERMINAL_CARGADO;
        }

        if ("descargarPuerto".equals(bloque) || "descargarTerminal".equals(bloque)) {
            return RegistroEjecucionArco.DESCARGA_TERMINAL;
        }

        if ("consolidarCarga".equals(bloque)) {
            return RegistroEjecucionArco.CONSOLIDACION_TERMINAL;
        }

        return "";
    }

    void cerrarArcoEnvio(Envio envio) {
        // Cierra el arco en el que estaba el envio. Se cierra al salir y no al entrar porque recien
        // al salir existen la duracion real y el estado final; los campos de etapa que ADR-063 ya
        // puso para C-05 son los que dicen donde estaba y cuanto deberia haber durado.
        if (!auditoria.activa() || envio == null) {
            return;
        }

        if (envio.bloqueActual == null || envio.bloqueActual.isEmpty() || envio.diaEntradaBloque < 0) {
            return;
        }

        String tipo = tipoArcoDeBloque(envio.bloqueActual);

        if (tipo.isEmpty()) {
            return;
        }

        RegistroEjecucionArco arco = new RegistroEjecucionArco();

        arco.tipoArco = tipo;
        arco.codigoPedido = envio.pedido == null ? "" : envio.pedido.codigoPedido;
        arco.idEnvio = envio.codigoEnvio;
        arco.idContenedor = envio.contenedor == null ? "" : envio.contenedor.idContenedor;
        arco.idAsignacion = envio.idAsignacionPedido;

        AsignacionPedido asignacion = asignacionDeEnvio(envio);

        if (asignacion != null) {
            arco.idDecision = asignacion.idDecision;
            arco.idAlternativa = asignacion.idAlternativa;
        }

        arco.idLote =
            envio.contenedor == null || envio.contenedor.lote == null
            ? ""
            : "" + envio.contenedor.lote.idLote;

        arco.producto = "" + envio.producto;
        arco.circuito = "" + envio.circuito;
        arco.esCrossDock = envio.esCrossDock;
        arco.toneladas = envio.toneladas;
        arco.contenedores = envio.contenedor == null ? 0 : 1;
        arco.viajes = 1;

        String[] extremos = extremosArcoEnvio(envio, tipo);

        arco.origen = extremos[0];
        arco.destino = extremos[1];

        // La tabla Distancia declara un solo sentido por tramo (ADR-061): el arco del vacio va
        // terminal -> origen y su fila es la de la ida.
        arco.distanciaKm =
            arco.origen.equals(arco.destino)
            ? 0
            : Math.max(0, datos.distanciaKmSimetrica(arco.origen, arco.destino));

        arco.diaProgramacion = envio.diaCreacion;
        arco.diaInicio = envio.diaEntradaBloque;
        arco.diaFin = time();
        arco.duracionRealHoras = Math.max(0, (time() - envio.diaEntradaBloque) * 24.0);
        arco.duracionEsperadaHoras = envio.horasEsperadasBloque;

        arco.recursoUtilizado =
            RegistroEjecucionArco.CARGA_CONSOLIDACION.equals(tipo)
                || RegistroEjecucionArco.CONSOLIDACION_TERMINAL.equals(tipo)
            ? "POSICION_CONSOLIDACION"
            : (envio.circuito == EstrategiaLogistica.CONSOLIDACION_TERMINAL
                ? "FLOTA_PRODUCTO"
                : "PORTACONTENEDOR");

        arco.idRecurso = envio.bloqueActual;
        arco.estadoFinal = "COMPLETADO";

        emitirArco(arco);
    }

    void registrarArcoViajeProducto(ViajeProducto viaje) {
        // El viaje de producto es un arco: el producto sale del origen al salir el camion y entra al
        // destino al llegar (ADR-061). Se emite al llegar, cuando la duracion real existe. Un viaje
        // cancelado no genera arco: no hubo movimiento, y el motivo ya queda en la decision.
        if (!auditoria.activa() || viaje == null || viaje.ocupaSoloFlota) {
            return;
        }

        RegistroEjecucionArco arco = new RegistroEjecucionArco();

        arco.tipoArco =
            "PLANTA".equals(viaje.origen)
            ? RegistroEjecucionArco.PLANTA_DEPOSITO
            : RegistroEjecucionArco.DEPOSITO_DEPOSITO;

        if (viaje.crossDock) {
            arco.tipoArco = RegistroEjecucionArco.CROSS_DOCK;
        }

        arco.codigoPedido = viaje.codigoPedido;
        arco.idLote = viaje.idLote > 0 ? "" + viaje.idLote : "";
        arco.producto = "" + viaje.producto;
        arco.circuito = "" + viaje.estrategia;
        arco.esCrossDock = viaje.crossDock;
        arco.origen = viaje.origen;
        arco.destino = viaje.destino;
        arco.toneladas = viaje.toneladas;
        arco.viajes = 1;
        arco.distanciaKm = viaje.distanciaKmIda;
        arco.diaProgramacion = viaje.diaProgramacion;
        arco.diaInicio = viaje.diaSalida;
        arco.diaFin = viaje.diaLlegadaDestino;
        arco.duracionRealHoras = Math.max(0, (viaje.diaLlegadaDestino - viaje.diaSalida) * 24.0);
        arco.duracionEsperadaHoras = viaje.duracionIdaDias * 24.0;
        arco.recursoUtilizado = "FLOTA_PRODUCTO";
        arco.idRecurso = "CAMION-" + viaje.idCamion;
        arco.estadoFinal = "COMPLETADO";
        arco.idAsignacion = viaje.idOperacion;

        emitirArco(arco);
    }

    void registrarArcoEsperaPosicion(ContenedorExportacion contenedor, int diasEspera) {
        // La espera por una posicion de consolidacion es un arco de espera: sin ella la suma de las
        // etapas no reconstruye el tiempo del envio, y es una de las dos esperas que explican el
        // atraso (ADR-060).
        if (!auditoria.activa() || contenedor == null || diasEspera <= 0) {
            return;
        }

        RegistroEjecucionArco arco = new RegistroEjecucionArco();

        arco.tipoArco = RegistroEjecucionArco.ESPERA_POSICION;
        arco.codigoPedido = contenedor.Pedido == null ? "" : contenedor.Pedido.codigoPedido;
        arco.idContenedor = contenedor.idContenedor;
        arco.idAsignacion = contenedor.idAsignacionPedido;
        arco.producto = "" + contenedor.producto;
        arco.circuito = "" + contenedor.circuito;
        arco.esCrossDock = contenedor.esCrossDock;
        arco.origen = contenedor.idUbicacionOperacion;
        arco.destino = contenedor.idUbicacionOperacion;
        arco.toneladas = contenedor.cantidadAsignadaTon;
        arco.contenedores = 1;
        arco.diaInicio = time() - diasEspera;
        arco.diaFin = time();
        arco.duracionRealHoras = diasEspera * 24.0;

        // Esperar una posicion no tiene techo fisico, igual que esperar un portacontenedor: la
        // duracion esperada negativa es "no aplica", no cero (ADR-063).
        arco.duracionEsperadaHoras = -1;
        arco.recursoUtilizado = contenedor.tipoRecursoOperacion;
        arco.idRecurso = contenedor.idUbicacionOperacion;
        arco.estadoFinal = "COMPLETADO";

        emitirArco(arco);
    }

    void reconciliarAuditoriaRed() {
        // Reconciliaciones de la auditoria. Ninguna compara dos exportaciones entre si: comparan la
        // tabla contra el estado del modelo, que es la unica forma de que puedan fallar.
        if (!auditoria.activa()) {
            return;
        }

        // C-06: toda asignacion viva tiene decision y alternativa. Una asignacion sin decision es
        // producto comprometido que nadie puede explicar.
        long sinDecision = 0;

        long asignaciones = 0;

        for (Pedido pedido : pedidos) {

            for (AsignacionPedido asignacion : pedido.asignaciones) {

                asignaciones++;

                if (asignacion.idDecision.isEmpty() || asignacion.idAlternativa.isEmpty()) {
                    sinDecision++;
                }
            }
        }

        // La politica fija no evalua alternativas: sus asignaciones no tienen decision porque no
        // hubo eleccion, y ahi la falta de identidad es el dato correcto (ADR-054).
        if (sinDecision > 0 && usaEvaluador()) {
            error(
                "C-06: " + sinDecision + " de " + asignaciones + " asignaciones decididas por el"
                + " evaluador no tienen decision de origen. La trazabilidad esta cortada.");
        }

        if (sinDecision > 0) {
            traceln(
                "Auditoria de red: " + sinDecision + " de " + asignaciones + " asignaciones sin"
                + " decision (politica fija, sin eleccion de alternativa).");
        }

        if (auditoria.filasDe(AuditoriaRed.ASIGNACIONES) != asignaciones) {
            error(
                "C-07: asignaciones_elegidas tiene "
                + auditoria.filasDe(AuditoriaRed.ASIGNACIONES)
                + " filas y el modelo tiene " + asignaciones + " asignaciones.");
        }

        // C-09: el importe de costos_eventos es el total de la campania, no una parte.
        if (registro.guardarDetalle) {

            double sumaEventos = 0;

            for (RegistroCostos.Cargo cargo : registro.detalle()) {
                sumaEventos += cargo.importe;
            }

            if (Math.abs(sumaEventos - registro.total()) > 0.01) {
                error(
                    "C-09: costos_eventos suma " + sumaEventos + " y el registro de la campania"
                    + " dice " + registro.total());
            }

            if (auditoria.filasDe(AuditoriaRed.COSTOS) != registro.detalle().size()) {
                error(
                    "C-09: costos_eventos tiene " + auditoria.filasDe(AuditoriaRed.COSTOS)
                    + " filas y el registro tiene " + registro.detalle().size() + " cargos.");
            }
        }

        // C-10: todo envio entregado dejo la secuencia completa de sus arcos. Sin la ultima etapa
        // el tiempo del envio no se puede reconstruir sumando arcos.
        long arcosEsperados = 0;

        for (String tipo : arcosAuditoria.keySet()) {
            arcosEsperados += (long) arcosAuditoria.get(tipo)[0];
        }

        if (arcosEsperados != auditoria.filasDe(AuditoriaRed.ARCOS)) {
            error(
                "C-10: el agregado de arcos cuenta " + arcosEsperados + " y la tabla tiene "
                + auditoria.filasDe(AuditoriaRed.ARCOS) + " filas.");
        }

        if (descuadresInventarioAuditoria > 0) {
            error(
                "C-12: " + descuadresInventarioAuditoria + " balances diarios de inventario no"
                + " cerraron.");
        }
    }

    String encabezadoCapacidadRecursos() {
        // snapshot_capacidad_recursos de ADR-064 es la tabla de capacidad de ADR-060: la ocupacion
        // por (recurso, sitio, dia) ya vive en una sola agenda y duplicarla en otra tabla obligaria
        // a reconciliar dos calendarios que pueden diferir. El encabezado es una funcion para que el
        // esquema publicado y el csv no puedan divergir.
        return "run_id,escenario,replica,dia,tipo_recurso,ubicacion,capacidad_nominal,"
            + "reservada,consumida,liberada,ocupada,libre,cola,fecha";
    }

    void escribirEsquemaAuditoria() {
        // Esquema de las tablas de auditoria: archivo, columnas y clave primaria de cada una. Se
        // genera desde los mismos encabezados que escriben los csv, asi que no puede quedar viejo:
        // si alguien agrega una columna, aparece aca sin tocar nada (ADR-064).
        String[] tablas = {
            AuditoriaRed.DECISIONES, AuditoriaRed.ASIGNACIONES, AuditoriaRed.ARCOS,
            AuditoriaRed.COSTOS, AuditoriaRed.INVENTARIO, AuditoriaRed.CAPACIDAD
        };

        String[] archivos = {
            AuditoriaRed.DECISIONES + ".csv", AuditoriaRed.ASIGNACIONES + ".csv",
            AuditoriaRed.ARCOS + ".csv", AuditoriaRed.COSTOS + ".csv",
            AuditoriaRed.INVENTARIO + ".csv", "capacidad_por_dia.csv"
        };

        String[] encabezados = {
            RegistroDecisionAlternativa.encabezadoCsv(), AsignacionPedido.encabezadoCsv(),
            RegistroEjecucionArco.encabezadoCsv(), encabezadoCostosEventos(),
            SnapshotInventario.encabezadoCsv(), encabezadoCapacidadRecursos()
        };

        String[] claves = {
            "run_id|id_alternativa", "run_id|id_asignacion", "run_id|id_evento_arco",
            "run_id|id_costo", "run_id|dia|ubicacion|producto",
            "run_id|dia|tipo_recurso|ubicacion"
        };

        java.io.PrintWriter salida = null;

        try {
            salida =
                new java.io.PrintWriter(auditoria.directorio + "/esquema_auditoria.json", "UTF-8");

            salida.println("{");
            salida.println("  \"version_esquema\": \"" + AuditoriaRed.VERSION_ESQUEMA + "\",");
            salida.println("  \"tablas\": [");

            for (int i = 0; i < tablas.length; i++) {

                salida.println("    {");
                salida.println("      \"tabla\": \"" + tablas[i] + "\",");
                salida.println("      \"archivo\": \"" + archivos[i] + "\",");

                salida.print("      \"clave\": [");

                String[] partes = claves[i].split("\\|");

                for (int k = 0; k < partes.length; k++) {
                    salida.print((k == 0 ? "" : ", ") + "\"" + partes[k] + "\"");
                }

                salida.println("],");

                String[] columnas = encabezados[i].split(",");

                salida.println("      \"columnas\": [");

                for (int j = 0; j < columnas.length; j++) {
                    salida.println(
                        "        \"" + columnas[j] + "\"" + (j == columnas.length - 1 ? "" : ","));
                }

                salida.println("      ]");
                salida.println("    }" + (i == tablas.length - 1 ? "" : ","));
            }

            salida.println("  ]");
            salida.println("}");

        } catch (java.io.IOException e) {
            traceln("No se pudo escribir el esquema de auditoria: " + e.getMessage());

        } finally {

            if (salida != null) {
                salida.close();
            }
        }
    }

    String resumenAuditoriaRed() {
        // Vista de lectura de la auditoria (ADR-064). Es un resumen, no un tablero con filtros: el
        // tablero se construye sobre los csv en otro proyecto, y PLE no exporta vistas.
        if (!auditoria.activa()) {
            return "Auditoria de red: DESACTIVADA";
        }

        String texto =
            "AUDITORIA DE RED · " + auditoria.nivel + " · run " + auditoria.runId
            + "\n\nDECISIONES\n"
            + "  rondas auditadas " + decisionesAuditadas
            + " · alternativas elegidas " + alternativasElegidasAuditadas
            + " · filas " + auditoria.filasDe(AuditoriaRed.DECISIONES)
            + "\n  costo de la restriccion (mas barata no factible) USD "
            + String.format("%,.0f", sobrecostoVsMasBarataAuditoria);

        texto += "\n\nMOTIVOS DE DESCARTE";

        for (String motivo : motivosAuditoria.keySet()) {
            texto += "\n  " + motivo + " " + motivosAuditoria.get(motivo);
        }

        texto += "\n\nARCOS EJECUTADOS (real vs esperado, horas)";

        for (String tipo : arcosAuditoria.keySet()) {

            double[] agregado = arcosAuditoria.get(tipo);

            texto +=
                "\n  " + tipo + " · " + (long) agregado[0] + " arcos · "
                + String.format("%,.0f", agregado[1]) + " tn · "
                + String.format("%,.1f", agregado[0] <= 0 ? 0 : agregado[2] / agregado[0]) + " h"
                + " (esperado "
                + String.format("%,.1f", agregado[0] <= 0 ? 0 : agregado[3] / agregado[0]) + " h)";
        }

        texto +=
            "\n\nTABLAS\n  " + AuditoriaRed.INVENTARIO + " "
            + auditoria.filasDe(AuditoriaRed.INVENTARIO)
            + " filas · descuadres " + descuadresInventarioAuditoria
            + "\n  " + AuditoriaRed.COSTOS + " " + auditoria.filasDe(AuditoriaRed.COSTOS) + " filas"
            + "\n  " + AuditoriaRed.ARCOS + " " + auditoria.filasDe(AuditoriaRed.ARCOS) + " filas";

        return texto;
    }

    void registrarEtapaEnvio(Envio envio, String bloque, double horasEsperadas) {
        // C-05: cada envio declara en que bloque del flujo esta y cuanto deberia durar. Una
        // espera de recurso no tiene techo (horasEsperadas < 0); un Delay si (ADR-063).
        if (envio == null) {
            return;
        }

        // El arco anterior se cierra antes de declarar el nuevo: recien al salir del bloque
        // existen la duracion real y el estado final (ADR-064). Los tres campos que ADR-063 puso
        // para C-05 son los mismos que describen el arco, asi que no hay una segunda etapa que
        // pueda quedar desincronizada de la que vigila C-05.
        cerrarArcoEnvio(envio);

        // El tramo cargado hacia la terminal es el unico movimiento del envio que la vista dibuja:
        // el resto de los bloques son operaciones dentro de un sitio o el vacio de vuelta.
        if (
            "viajarPuerto".equals(envio.bloqueActual)
            || "viajarTerminalGranel".equals(envio.bloqueActual)
        ) {
            registrarFlujoVisual(
                envio.idSitioOrigen,
                envio.pedido == null ? "" : envio.pedido.puertoSalida.idUbicacion,
                envio.toneladas);
        }

        envio.bloqueActual = bloque;
        envio.diaEntradaBloque = time();
        envio.horasEsperadasBloque = horasEsperadas;
    }

    String claveTramoVisual(String origen, String destino) {
        // El tramo dibujado es la infraestructura, no el sentido del viaje: la tabla Distancia
        // declara un solo sentido (ADR-061) y los dos usan la misma linea.
        return origen.compareTo(destino) <= 0 ? origen + "|" + destino : destino + "|" + origen;
    }

    void registrarFlujoVisual(String origen, String destino, double toneladas) {
        // Acumulador de presentacion (ADR-072): da el grosor del arco. No es una fuente de verdad
        // del movimiento fisico -esa es ejecucion_arcos- y con la animacion apagada no se llena.
        if (!animacionRed || origen == null || destino == null || origen.equals(destino) || toneladas <= 0) {
            return;
        }

        String clave = claveTramoVisual(origen, destino);
        double[] acumulado = flujoRedVisual.get(clave);

        if (acumulado == null) {
            acumulado = new double[2];
            flujoRedVisual.put(clave, acumulado);
        }

        acumulado[0] += toneladas;
        acumulado[1]++;
    }

    double capacidadNodoRedVisual(String idUbicacion) {
        // Capacidad de almacenamiento declarada del nodo, sumada sobre los productos. La terminal no
        // almacena: devuelve 0 y el nodo se dibuja sin semaforo de ocupacion.
        double total = 0;

        for (TipoProducto producto : TipoProducto.values()) {
            total +=
                "PLANTA".equals(idUbicacion)
                ? planta.getCapacidad(producto)
                : datos.capacidadDeclaradaTn(idUbicacion, producto);
        }

        return total;
    }

    double stockNodoRedVisual(String idUbicacion) {
        double total = 0;

        for (TipoProducto producto : TipoProducto.values()) {
            total += inventario.stock(idUbicacion, producto);
        }

        return total;
    }

    java.awt.Color colorOcupacionRedVisual(double ocupacionPct) {
        // Semaforo de ocupacion del nodo. Sin capacidad declarada no hay semaforo: gris.
        if (ocupacionPct < 0) {
            return new java.awt.Color(148, 163, 184);
        }

        if (ocupacionPct < 70) {
            return new java.awt.Color(34, 150, 83);
        }

        if (ocupacionPct < 90) {
            return new java.awt.Color(235, 160, 20);
        }

        return new java.awt.Color(200, 50, 50);
    }

    java.awt.Color colorTipoNodoRedVisual(String tipo) {
        if ("PLANTA".equals(tipo)) {
            return new java.awt.Color(30, 58, 138);
        }

        return "TERMINAL".equals(tipo)
            ? new java.awt.Color(109, 40, 217)
            : new java.awt.Color(100, 116, 139);
    }

    int columnaRedVisual(String tipo) {
        if ("PLANTA".equals(tipo)) {
            return 0;
        }

        return "TERMINAL".equals(tipo) ? 2 : 1;
    }

    void calcularPosicionesRedVisual() {
        // Posicion de cada nodo en el lienzo. Con coordenadas en la tabla Ubicacion el mapa es
        // geografico (ADR-072); sin ellas se dibuja el esquema por tipo de nodo, que no necesita
        // ningun dato nuevo. Las coordenadas no entran en ninguna decision: el tiempo y el costo del
        // tramo siguen saliendo de la tabla Distancia.
        posicionRedVisual.clear();

        double izquierda = 1960;
        double arriba = -840;
        double ancho = 660;
        double alto = 620;

        redVisualGeografica = !datos.ubicaciones.isEmpty();

        for (DatosEntrada.Ubicacion u : datos.ubicaciones) {
            if (!u.tieneCoordenadas()) {
                redVisualGeografica = false;
            }
        }

        if (redVisualGeografica) {

            double latMin = Double.MAX_VALUE;
            double latMax = -Double.MAX_VALUE;
            double lonMin = Double.MAX_VALUE;
            double lonMax = -Double.MAX_VALUE;

            for (DatosEntrada.Ubicacion u : datos.ubicaciones) {
                latMin = Math.min(latMin, u.latitud);
                latMax = Math.max(latMax, u.latitud);
                lonMin = Math.min(lonMin, u.longitud);
                lonMax = Math.max(lonMax, u.longitud);
            }

            // Proyeccion equirectangular con el meridiano corregido por la latitud media: a -30 grados
            // un grado de longitud mide poco mas de la mitad que uno de latitud, y sin el factor el
            // pais sale estirado.
            double latMedia = (latMin + latMax) / 2;
            double lonMedia = (lonMin + lonMax) / 2;
            double factorLon = Math.cos(Math.toRadians(latMedia));

            double anchoGrados = Math.max(0.001, (lonMax - lonMin) * factorLon);
            double altoGrados = Math.max(0.001, latMax - latMin);
            double escala = Math.min(ancho / anchoGrados, alto / altoGrados);

            for (DatosEntrada.Ubicacion u : datos.ubicaciones) {
                double x = izquierda + ancho / 2 + (u.longitud - lonMedia) * factorLon * escala;
                double y = arriba + alto / 2 - (u.latitud - latMedia) * escala;
                posicionRedVisual.put(u.idUbicacion, new double[] {x, y});
            }

            separarNodosRedVisual(izquierda, arriba, ancho, alto);

        } else {

            int[] porTipo = new int[3];
            int[] dibujados = new int[3];

            for (DatosEntrada.Ubicacion u : datos.ubicaciones) {
                porTipo[columnaRedVisual(u.tipo)]++;
            }

            for (DatosEntrada.Ubicacion u : datos.ubicaciones) {
                int columna = columnaRedVisual(u.tipo);
                int cantidad = Math.max(1, porTipo[columna]);

                double x = izquierda + columna * ancho / 2;
                double y = arriba + alto * (dibujados[columna] + 1.0) / (cantidad + 1.0);

                dibujados[columna]++;
                posicionRedVisual.put(u.idUbicacion, new double[] {x, y});
            }
        }
    }

    void recortarNodosRedVisual(double izquierda, double arriba, double ancho, double alto) {
        // Deja los nodos dentro del area de la vista, con margen para el titulo de arriba y para la
        // etiqueta de dos lineas que cuelga del nodo de abajo.
        for (double[] p : posicionRedVisual.values()) {
            p[0] = Math.max(izquierda + 40, Math.min(izquierda + ancho - 40, p[0]));
            p[1] = Math.max(arriba + 50, Math.min(arriba + alto - 40, p[1]));
        }
    }

    void separarNodosRedVisual(double izquierda, double arriba, double ancho, double alto) {
        // Seis de los diez sitios estan en un radio de 25 km: en el mapa geografico quedarian
        // superpuestos y el nodo no se podria leer. Se separan lo minimo para distinguirlos, asi que
        // la posicion dibujada es aproximada y la geografia se conserva solo en grande. La distancia
        // que se costea es siempre la de la tabla, nunca la del dibujo.
        // La separacion minima se mide en pixeles de pantalla y tiene que dar lugar a la etiqueta de dos
        // lineas que va debajo del nodo, no solo al circulo.
        double minima = 112;
        java.util.ArrayList<String> nodos = new java.util.ArrayList<String>(posicionRedVisual.keySet());

        for (int iteracion = 0; iteracion < 600; iteracion++) {

            boolean movido = false;

            for (int i = 0; i < nodos.size(); i++) {
                for (int j = i + 1; j < nodos.size(); j++) {

                    double[] a = posicionRedVisual.get(nodos.get(i));
                    double[] b = posicionRedVisual.get(nodos.get(j));

                    double dx = b[0] - a[0];
                    double dy = b[1] - a[1];
                    double distancia = Math.sqrt(dx * dx + dy * dy);

                    if (distancia >= minima) {
                        continue;
                    }

                    // Dos nodos exactamente encima: se los separa en una direccion fija para que el
                    // resultado sea el mismo en cada corrida (la vista no puede depender del azar).
                    if (distancia < 0.0001) {
                        dx = 1;
                        dy = 0;
                        distancia = 1;
                    }

                    double empuje = (minima - distancia) / 2;

                    a[0] -= dx / distancia * empuje;
                    a[1] -= dy / distancia * empuje;
                    b[0] += dx / distancia * empuje;
                    b[1] += dy / distancia * empuje;

                    movido = true;
                }
            }

            // El recorte al area se hace dentro del ciclo: si se hiciera solo al final, los nodos empujados
            // contra el borde volverian a superponerse justo donde el cumulo es mas denso.
            recortarNodosRedVisual(izquierda, arriba, ancho, alto);

            if (!movido) {
                break;
            }
        }

        recortarNodosRedVisual(izquierda, arriba, ancho, alto);
    }

    void dibujarRedVisual() {
        // Vista de red (ADR-072). Capa de presentacion: lee estado ya calculado y no escribe nada del
        // modelo. Con animacionRed = false no se crea ninguna figura y la corrida decide igual.
        if (!animacionRed || redVisualDibujada) {
            return;
        }

        redVisualDibujada = true;

        calcularPosicionesRedVisual();

        // Los arcos se agregan primero para que queden por debajo de los nodos.
        for (DatosEntrada.Distancia d : datos.distancias) {

            String clave = claveTramoVisual(d.origen, d.destino);

            if (arcoRedVisual.containsKey(clave)) {
                continue;
            }

            double[] desde = posicionRedVisual.get(d.origen);
            double[] hasta = posicionRedVisual.get(d.destino);

            if (desde == null || hasta == null) {
                continue;
            }

            com.anylogic.engine.presentation.ShapeLine arco =
                new com.anylogic.engine.presentation.ShapeLine(
                    true, desde[0], desde[1], new java.awt.Color(203, 213, 225),
                    hasta[0] - desde[0], hasta[1] - desde[1], 0, com.anylogic.engine.presentation.LineStyle.LINE_STYLE_SOLID);

            arco.setX(desde[0]);
            arco.setY(desde[1]);
            arco.setDx(hasta[0] - desde[0]);
            arco.setDy(hasta[1] - desde[1]);
            arco.setLineWidth(1);

            presentation.add(arco);
            arcoRedVisual.put(clave, arco);
        }

        double capacidadMaxima = 0;

        for (DatosEntrada.Ubicacion u : datos.ubicaciones) {
            capacidadMaxima = Math.max(capacidadMaxima, capacidadNodoRedVisual(u.idUbicacion));
        }

        for (DatosEntrada.Ubicacion u : datos.ubicaciones) {

            double[] p = posicionRedVisual.get(u.idUbicacion);

            if (p == null) {
                continue;
            }

            // El radio dice capacidad declarada; por area, no por radio, para no exagerar el nodo
            // grande. La terminal no almacena y va con el radio minimo.
            double capacidad = capacidadNodoRedVisual(u.idUbicacion);
            double radio =
                capacidadMaxima <= 0
                ? 13
                : 11 + 13 * Math.sqrt(Math.max(0, capacidad) / capacidadMaxima);

            com.anylogic.engine.presentation.ShapeOval nodo =
                new com.anylogic.engine.presentation.ShapeOval(
                    true, p[0], p[1], 0, new java.awt.Color(148, 163, 184),
                    colorTipoNodoRedVisual(u.tipo), radio, radio, 0, com.anylogic.engine.presentation.LineStyle.LINE_STYLE_SOLID);

            nodo.setX(p[0]);
            nodo.setY(p[1]);
            nodo.setRadiusX(radio);
            nodo.setRadiusY(radio);
            nodo.setLineColor(colorTipoNodoRedVisual(u.tipo));
            nodo.setLineWidth("PLANTA".equals(u.tipo) ? 3 : 2);

            presentation.add(nodo);
            nodoRedVisual.put(u.idUbicacion, nodo);

            com.anylogic.engine.presentation.ShapeText etiqueta =
                new com.anylogic.engine.presentation.ShapeText(
                    true, p[0], p[1] + radio + 4, 0, new java.awt.Color(30, 41, 59),
                    u.idUbicacion, new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 10),
                    com.anylogic.engine.presentation.TextAlignment.ALIGNMENT_CENTER);

            etiqueta.setX(p[0]);
            etiqueta.setY(p[1] + radio + 4);

            presentation.add(etiqueta);
            etiquetaRedVisual.put(u.idUbicacion, etiqueta);
        }

        dibujarLeyendaRedVisual();
        actualizarRedVisual();
    }

    void dibujarLeyendaRedVisual() {
        // Titulo, identidad de la corrida y leyenda. La identidad va en pantalla para que una captura
        // del mapa se pueda reproducir: sin escenario, replica y run_id, no se sabe que se esta viendo.
        com.anylogic.engine.presentation.ShapeText titulo =
            new com.anylogic.engine.presentation.ShapeText(
                true, 1960, -880, 0, new java.awt.Color(30, 58, 95),
                "Red logistica de exportacion", new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16),
                com.anylogic.engine.presentation.TextAlignment.ALIGNMENT_LEFT);

        titulo.setX(1960);
        titulo.setY(-880);
        presentation.add(titulo);

        rotuloRedVisual =
            new com.anylogic.engine.presentation.ShapeText(
                true, 1960, -858, 0, new java.awt.Color(71, 85, 105),
                "", new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 11), com.anylogic.engine.presentation.TextAlignment.ALIGNMENT_LEFT);

        rotuloRedVisual.setX(1960);
        rotuloRedVisual.setY(-858);
        presentation.add(rotuloRedVisual);

        com.anylogic.engine.presentation.ShapeText leyenda =
            new com.anylogic.engine.presentation.ShapeText(
                true, 2700, -800, 0, new java.awt.Color(71, 85, 105),
                "Nodo: tamano = capacidad declarada, color = ocupacion"
                + "\n   verde < 70%   ambar 70-90%   rojo > 90%   gris sin capacidad declarada"
                + "\nBorde: azul planta, gris deposito, violeta terminal"
                + "\nArco: grosor = toneladas movidas en la campania"
                + "\nFigura sobre el arco: un movimiento en curso, avanzando con el reloj del modelo"
                + "\n   azul contenedor hacia la terminal   gris portacontenedor vacio al origen"
                + "\n   naranja camion de producto cargado   naranja claro camion de regreso"
                + "\n"
                + (redVisualGeografica
                    ? "\nMapa geografico. Los sitios a menos de 25 km se separan para poder leerlos:\nla posicion dibujada es aproximada y la distancia que se costea es la de la tabla."
                    : "\nEsquema por tipo de nodo: la tabla Ubicacion no trae latitud y longitud."),
                new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 11), com.anylogic.engine.presentation.TextAlignment.ALIGNMENT_LEFT);

        leyenda.setX(2700);
        leyenda.setY(-800);
        presentation.add(leyenda);

        resumenRedVisual =
            new com.anylogic.engine.presentation.ShapeText(
                true, 2700, -640, 0, new java.awt.Color(30, 41, 59),
                "", new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 11), com.anylogic.engine.presentation.TextAlignment.ALIGNMENT_LEFT);

        resumenRedVisual.setX(2700);
        resumenRedVisual.setY(-640);
        presentation.add(resumenRedVisual);

        // Panel "ahora mismo" (ADR-073). Estado instantaneo del flujo, que es lo que ningun CSV puede
        // dar: el bug de los envios congelados de ADR-063 se lee aca sin abrir una tabla.
        estadoRedVisual =
            new com.anylogic.engine.presentation.ShapeText(
                true, 2700, -540, 0, new java.awt.Color(30, 41, 59),
                "", new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 11), com.anylogic.engine.presentation.TextAlignment.ALIGNMENT_LEFT);

        estadoRedVisual.setX(2700);
        estadoRedVisual.setY(-540);
        presentation.add(estadoRedVisual);
    }

    void actualizarRedVisual() {
        // Refresco diario de la vista. Solo cambia color, grosor y texto de figuras que ya existen:
        // no crea objetos por evento, que es lo que haria caer el rendimiento en una campania larga.
        if (!animacionRed || !redVisualDibujada) {
            return;
        }

        for (String idUbicacion : nodoRedVisual.keySet()) {

            com.anylogic.engine.presentation.ShapeOval nodo = nodoRedVisual.get(idUbicacion);

            double capacidad = capacidadNodoRedVisual(idUbicacion);
            double stock = stockNodoRedVisual(idUbicacion);
            double ocupacion = capacidad <= 0 ? -1 : 100.0 * stock / capacidad;

            nodo.setFillColor(colorOcupacionRedVisual(ocupacion));

            com.anylogic.engine.presentation.ShapeText etiqueta = etiquetaRedVisual.get(idUbicacion);

            // Nombre y ocupacion en dos lineas: en el cumulo de Tucuman una sola linea se solapa con la
            // etiqueta del sitio vecino y no se puede leer ninguna de las dos.
            etiqueta.setText(
                idUbicacion
                + (capacidad > 0
                    ? "\n" + Math.round(stock) + " / " + Math.round(capacidad) + " tn"
                    : "\n" + Math.round(stock) + " tn"));
        }

        double toneladasMaximas = 0;

        for (double[] flujo : flujoRedVisual.values()) {
            toneladasMaximas = Math.max(toneladasMaximas, flujo[0]);
        }

        for (String clave : arcoRedVisual.keySet()) {

            com.anylogic.engine.presentation.ShapeLine arco = arcoRedVisual.get(clave);
            double[] flujo = flujoRedVisual.get(clave);
            double toneladas = flujo == null ? 0 : flujo[0];

            if (toneladas <= 0 || toneladasMaximas <= 0) {
                arco.setLineWidth(1);
                arco.setColor(new java.awt.Color(214, 221, 231));
                continue;
            }

            arco.setLineWidth(1 + 7 * Math.sqrt(toneladas / toneladasMaximas));
            arco.setColor(new java.awt.Color(37, 99, 235));
        }

        // Al terminar la campania el reloj marca el instante de cierre, un dia despues del ultimo dia con
        // agenda: el rotulo muestra el ultimo dia de campania y no un "dia 365 de 364" que no existe.
        int diaRotulo = Math.min(diaCampania(), datos.escenario.duracionCampaniaDias);

        rotuloRedVisual.setText(
            "dia " + diaRotulo + " de " + datos.escenario.duracionCampaniaDias
            + (AuditoriaRed.fecha(diaRotulo).isEmpty()
                ? "" : "  ·  " + AuditoriaRed.fecha(diaRotulo))
            + "  ·  escenario " + idEscenario + "  ·  replica " + replica
            + "  ·  run " + auditoria.runId);

        resumenRedVisual.setText(textoTramosRedVisual());

        // El panel instantaneo se refresca tambien al cierre del dia: asi la parte de C-05 que
        // publica -envios en curso y retenidos por bloque- se ve recien calculada y no con el
        // valor del dia anterior.
        moverFigurasRedVisual();
    }

    String[] extremosArcoEnvio(Envio envio, String tipo) {
        // Los dos extremos fisicos del bloque en el que esta el envio. La fila de ejecucion_arcos
        // (ADR-064) y la figura que se mueve (ADR-073) los piden a la misma funcion: escrita dos
        // veces, la animacion podria mostrar un sentido y la tabla auditar el otro.
        String terminal =
            envio == null || envio.pedido == null ? "" : envio.pedido.puertoSalida.idUbicacion;

        String origen = envio == null ? "" : envio.idSitioOrigen;

        if (
            RegistroEjecucionArco.TERMINAL_ORIGEN_VACIO.equals(tipo)
            || RegistroEjecucionArco.ESPERA_PORTACONTENEDOR.equals(tipo)
        ) {
            return new String[] {terminal, origen};
        }

        if (RegistroEjecucionArco.ORIGEN_TERMINAL_CARGADO.equals(tipo)) {
            return new String[] {origen, terminal};
        }

        if (
            RegistroEjecucionArco.DESCARGA_TERMINAL.equals(tipo)
            || RegistroEjecucionArco.CONSOLIDACION_TERMINAL.equals(tipo)
        ) {
            return new String[] {terminal, terminal};
        }

        return new String[] {origen, origen};
    }

    com.anylogic.engine.presentation.ShapeOval figuraRedVisual(int indice) {
        // Pool de figuras: se crean una vez y se reusan viaje tras viaje. Crear una figura por
        // movimiento y descartarla al llegar haria crecer el arbol de presentacion durante toda la
        // campania, que es lo que degrada la corrida larga.
        while (figurasRedVisual.size() <= indice) {

            com.anylogic.engine.presentation.ShapeOval figura =
                new com.anylogic.engine.presentation.ShapeOval(
                    true, 0, 0, 0, new java.awt.Color(255, 255, 255),
                    new java.awt.Color(37, 99, 235), 5, 5, 0,
                    com.anylogic.engine.presentation.LineStyle.LINE_STYLE_SOLID);

            figura.setRadiusX(5);
            figura.setRadiusY(5);
            figura.setLineWidth(1);
            figura.setVisible(false);

            presentation.add(figura);
            figurasRedVisual.add(figura);
        }

        return figurasRedVisual.get(indice);
    }

    boolean ubicarFiguraRedVisual(int indice, String origen, String destino, double progreso, java.awt.Color color, double radio) {
        // Interpolacion lineal sobre el arco dibujado. El avance sale del reloj del motor y de la
        // duracion que el modelo ya fijo para ese bloque: la figura no tiene una velocidad propia
        // ni usa la distancia geometrica de la pantalla.
        double[] desde = posicionRedVisual.get(origen);
        double[] hasta = posicionRedVisual.get(destino);

        if (desde == null || hasta == null || origen.equals(destino)) {
            return false;
        }

        double avance = Math.max(0, Math.min(1, progreso));

        double dx = hasta[0] - desde[0];
        double dy = hasta[1] - desde[1];
        double largo = Math.sqrt(dx * dx + dy * dy);

        // Corrimiento perpendicular al tramo: la ida y la vuelta comparten la linea -la tabla
        // Distancia declara un solo sentido- y sin separarlas las figuras se pisan y no se ve
        // para donde va cada una. El signo lo da el sentido del viaje, no un sorteo.
        double desvioX = largo <= 0 ? 0 : -dy / largo * 7;
        double desvioY = largo <= 0 ? 0 : dx / largo * 7;

        com.anylogic.engine.presentation.ShapeOval figura = figuraRedVisual(indice);

        figura.setX(desde[0] + dx * avance + desvioX);
        figura.setY(desde[1] + dy * avance + desvioY);
        figura.setRadiusX(radio);
        figura.setRadiusY(radio);
        figura.setFillColor(color);
        figura.setVisible(true);

        return true;
    }

    void moverFigurasRedVisual() {
        // Movimiento sobre el tramo y panel instantaneo (ADR-073). Presentacion pura: recorre estado
        // ya calculado, interpola posiciones y no escribe ni una variable del modelo. Con
        // animacionRed = false no hace nada.
        if (!animacionRed || !redVisualDibujada || estadoRedVisual == null) {
            return;
        }

        int usadas = 0;

        int contenedoresCargados = 0;
        int portacontenedoresVacios = 0;
        double toneladasHaciaTerminal = 0;

        for (Envio envio : envios) {

            if (
                envio.estado == EstadoEnvio.ENTREGADO
                || envio.bloqueActual == null
                || envio.bloqueActual.isEmpty()
                || envio.diaEntradaBloque < 0
                || envio.horasEsperadasBloque <= 0
            ) {
                continue;
            }

            String tipo = tipoArcoDeBloque(envio.bloqueActual);

            boolean cargado = RegistroEjecucionArco.ORIGEN_TERMINAL_CARGADO.equals(tipo);
            boolean vacio = RegistroEjecucionArco.TERMINAL_ORIGEN_VACIO.equals(tipo);

            // Solo los dos bloques que son movimiento entre sitios. Cargar, descargar y consolidar
            // pasan dentro de un sitio -la figura quedaria quieta sobre el nodo- y la cola de
            // portacontenedor es espera de un recurso finito, no un viaje.
            if (!cargado && !vacio) {
                continue;
            }

            if (cargado) {
                contenedoresCargados++;
                toneladasHaciaTerminal += envio.toneladas;
            } else {
                portacontenedoresVacios++;
            }

            String[] extremos = extremosArcoEnvio(envio, tipo);

            double progreso =
                (time() - envio.diaEntradaBloque) / (envio.horasEsperadasBloque / 24.0);

            if (
                usadas < maximoFigurasRedVisual
                && ubicarFiguraRedVisual(
                    usadas, extremos[0], extremos[1], progreso,
                    cargado ? new java.awt.Color(37, 99, 235) : new java.awt.Color(148, 163, 184),
                    cargado ? 5 : 4)
            ) {
                usadas++;
            }
        }

        int camionesIda = 0;
        int camionesRegreso = 0;
        double toneladasDeProducto = 0;

        for (ViajeProducto viaje : viajesProducto) {

            // El viaje que solo ocupa flota no mueve producto por si mismo: el movimiento fisico es el
            // del envio a granel, que ya se dibuja arriba. Dibujarlo tambien seria mostrar dos
            // camiones donde el modelo tiene uno.
            if (viaje.ocupaSoloFlota) {
                continue;
            }

            String origen = "";
            String destino = "";
            double progreso = 0;
            boolean ida = false;

            if (
                viaje.estado == EstadoViajeProducto.EN_TRANSITO_DESTINO
                && viaje.diaSalida >= 0 && viaje.duracionIdaDias > 0
            ) {
                ida = true;
                origen = viaje.origen;
                destino = viaje.destino;
                progreso = (time() - viaje.diaSalida) / viaje.duracionIdaDias;

                camionesIda++;
                toneladasDeProducto += viaje.toneladas;

            } else if (
                viaje.estado == EstadoViajeProducto.RETORNANDO
                && viaje.diaInicioRetorno >= 0 && viaje.duracionRetornoDias > 0
            ) {
                origen = viaje.destino;
                destino = viaje.origen;
                progreso = (time() - viaje.diaInicioRetorno) / viaje.duracionRetornoDias;

                camionesRegreso++;

            } else {
                continue;
            }

            if (
                usadas < maximoFigurasRedVisual
                && ubicarFiguraRedVisual(
                    usadas, origen, destino, progreso,
                    ida ? new java.awt.Color(234, 88, 12) : new java.awt.Color(253, 186, 116),
                    4)
            ) {
                usadas++;
            }
        }

        for (int i = usadas; i < figurasRedVisual.size(); i++) {
            figurasRedVisual.get(i).setVisible(false);
        }

        figurasRedVisualPico = Math.max(figurasRedVisualPico, usadas);

        estadoRedVisual.setText(textoEstadoRedVisual(
            contenedoresCargados, toneladasHaciaTerminal, portacontenedoresVacios,
            camionesIda, camionesRegreso, toneladasDeProducto, usadas));
    }

    String textoEstadoRedVisual(int contenedoresCargados, double toneladasHaciaTerminal, int portacontenedoresVacios, int camionesIda, int camionesRegreso, double toneladasDeProducto, int figuras) {
        // Panel "ahora mismo": el instante, que es lo unico que ninguna tabla exportada puede dar.
        // La parte de bloques la publica C-05 al cierre del dia y se muestra tal cual: contarla otra
        // vez aca seria una segunda cuenta del mismo hecho.
        int enMovimiento =
            contenedoresCargados + portacontenedoresVacios + camionesIda + camionesRegreso;

        StringBuilder texto = new StringBuilder("Ahora mismo   dia ");

        texto.append(Math.round(time() * 100) / 100.0)
            .append("\n   contenedores hacia la terminal: ").append(contenedoresCargados)
            .append("   ").append(Math.round(toneladasHaciaTerminal)).append(" tn")
            .append("\n   portacontenedores vacios al origen: ").append(portacontenedoresVacios)
            .append("\n   camiones de producto: ").append(camionesIda).append(" cargados   ")
            .append(camionesRegreso).append(" de regreso   ")
            .append(Math.round(toneladasDeProducto)).append(" tn");

        if (figuras < enMovimiento) {
            texto.append("\n   figuras dibujadas: ").append(figuras).append(" de ")
                .append(enMovimiento).append(" (tope maximoFigurasRedVisual)");
        }

        int diaCierre = Math.min(diaCampania(), datos.escenario.duracionCampaniaDias);

        texto.append("\n\nEnvios en curso por bloque, cierre del dia ").append(diaCierre)
            .append(" (C-05)")
            .append("\n   en curso: ").append(enviosEnCurso)
            .append("   ").append(Math.round(toneladasEnviosEnCurso)).append(" tn")
            .append("   retenidos: ").append(enviosRetenidos);

        if (detalleEnviosEnCurso.isEmpty()) {
            texto.append("\n   ningun bloque retiene envios");
        } else {
            texto.append("\n   ").append(detalleEnviosEnCurso.replace(" · ", "\n   "));
        }

        return texto.toString();
    }

    String textoTramosRedVisual() {
        // Los cinco tramos que mas movieron, que es lo que el grosor del arco no alcanza a decir con
        // precision. Sale del mismo acumulador que dibuja los arcos, no de una cuenta paralela.
        java.util.ArrayList<String> claves = new java.util.ArrayList<String>(flujoRedVisual.keySet());

        java.util.Collections.sort(claves, new java.util.Comparator<String>() {
            public int compare(String a, String b) {
                return Double.compare(flujoRedVisual.get(b)[0], flujoRedVisual.get(a)[0]);
            }
        });

        StringBuilder texto = new StringBuilder("Tramos con mas movimiento (tn acumuladas)");

        for (int i = 0; i < Math.min(5, claves.size()); i++) {
            double[] flujo = flujoRedVisual.get(claves.get(i));
            texto.append("\n   ")
                .append(claves.get(i).replace("|", " - "))
                .append("   ")
                .append(Math.round(flujo[0]))
                .append(" tn en ")
                .append(Math.round(flujo[1]))
                .append(" movimientos");
        }

        if (claves.isEmpty()) {
            texto.append("\n   todavia no se movio producto");
        }

        return texto.toString();
    }

    void reconciliarEnviosEnCurso() {
        // C-05: ningun bloque del flujo puede retener un envio mas alla de su duracion fisica.
        // La concurrencia la limitan el pool de portacontenedores, las posiciones de
        // consolidacion (ADR-060) y la flota de producto (ADR-061), nunca la capacidad de un
        // Delay. Un envio retenido es un error del modelo, no un dato (ADR-063).
        enviosEnCurso = 0;
        enviosRetenidos = 0;
        toneladasEnviosEnCurso = 0;

        java.util.LinkedHashMap<String, Integer> porBloque =
            new java.util.LinkedHashMap<String, Integer>();

        java.util.LinkedHashMap<String, Integer> retenidosPorBloque =
            new java.util.LinkedHashMap<String, Integer>();

        for (Envio envio : envios) {

            if (
                envio.estado == EstadoEnvio.ENTREGADO
                || envio.bloqueActual == null
                || envio.bloqueActual.isEmpty()
                || envio.diaEntradaBloque < 0
            ) {
                continue;
            }

            enviosEnCurso++;
            toneladasEnviosEnCurso += envio.toneladas;

            String bloque = envio.bloqueActual;

            porBloque.put(
                bloque,
                (porBloque.containsKey(bloque) ? porBloque.get(bloque) : 0) + 1);

            // La cola que espera portacontenedor no tiene techo: es lista de espera de un
            // recurso finito y esperar ahi es la conducta correcta.
            if (envio.horasEsperadasBloque < 0) {
                continue;
            }

            double permanencia = time() - envio.diaEntradaBloque;

            double techo =
                envio.horasEsperadasBloque / 24.0
                + toleranciaRetencionEnvioDias;

            if (permanencia > techo) {

                enviosRetenidos++;

                retenidosPorBloque.put(
                    bloque,
                    (retenidosPorBloque.containsKey(bloque) ? retenidosPorBloque.get(bloque) : 0) + 1);
            }
        }

        enviosRetenidosPico = max(enviosRetenidosPico, enviosRetenidos);

        StringBuilder detalle = new StringBuilder();

        for (String bloque : porBloque.keySet()) {

            if (detalle.length() > 0) {
                detalle.append(" · ");
            }

            detalle.append(bloque).append(" ").append(porBloque.get(bloque));

            if (retenidosPorBloque.containsKey(bloque)) {
                detalle.append(" (retenidos ").append(retenidosPorBloque.get(bloque)).append(")");
            }
        }

        detalleEnviosEnCurso = detalle.toString();

        if (enviosRetenidos > 0 && !retenidosPorBloque.isEmpty()) {

            String peor = "";

            int cuantos = 0;

            for (String bloque : retenidosPorBloque.keySet()) {

                if (retenidosPorBloque.get(bloque) > cuantos) {
                    cuantos = retenidosPorBloque.get(bloque);
                    peor = bloque;
                }
            }

            error(
                "C-05: el dia " + diaCampania() + " hay " + enviosRetenidos
                + " envios retenidos en el flujo mas alla de su duracion fisica; el bloque que mas"
                + " retiene es " + peor + " con " + cuantos
                + ". Un bloque del flujo no puede limitar la concurrencia (ADR-063).");
        }
    }

    void finalizarEnvio(Envio envio) {
        // Cierre del envio, comun a los cuatro circuitos: el contenedor de terminal sale
        // por su propia salida y no puede tener otra contabilidad que el resto.
        envio.estado =
            EstadoEnvio.ENTREGADO;

        // El servicio se mide en el instante fisico en que el envio queda listo en la terminal
        // (fin de descarga en los circuitos 1 a 3, fin de consolidacion en el 4) y no cuando
        // corre el cierre administrativo (ADR-062).
        double fechaEntrega =
            envio.diaListoEnTerminal >= 0
            ? envio.diaListoEnTerminal
            : time();

        envio.diaEntrega = fechaEntrega;

        // Los cargos, en cambio, se devengan el dia en que se registran: la auditoria por envio
        // tiene que reconstruirlos con la tarifa de ese dia y no con la del servicio (ADR-062).
        envio.diaCargosCierre = diaCampania();

        // El envio ya no esta en ningun bloque del flujo (C-05, ADR-063).
        cerrarArcoEnvio(envio);

        envio.bloqueActual = "";
        envio.diaEntradaBloque = -1;

        Pedido pedido =
            envio.pedido;

        pedido.toneladasEntregadas +=
            envio.toneladas;

        pedido.enviosEntregados++;

        AsignacionPedido asignacion = asignacionDeEnvio(envio);

        if (asignacion != null) {

            asignacion.toneladasEntregadas += envio.toneladas;
            asignacion.diaUltimaEntrega = fechaEntrega;
            asignacion.cerrarSiCompleta();
        }

        // El servicio se mide contra el cut-off fisico y por tonelada: media entrega a
        // tiempo no es medio buque perdido, pero tampoco es servicio completo (ADR-059).
        if (fechaEntrega <= pedido.diaLimite + 0.0001) {
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
                fechaEntrega;

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
                contenedoresNecesarios(pedido.producto, pedido.material, toneladas)
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

            // Un deposito sin capacidad declarada para el producto no lo almacena nunca, asi que
            // no necesita tarifa de almacenaje: exigirla abortaria la carga por una combinacion
            // fisicamente imposible (ADR-069).
            deposito.costoJugoTnDia =
                datos.almacenaProducto(deposito.idUbicacion, TipoProducto.JUGO)
                ? datos.storageUsdTnDia(dia, deposito.idUbicacion, TipoProducto.JUGO)
                : 0;

            deposito.costoCascaraTnDia =
                datos.almacenaProducto(deposito.idUbicacion, TipoProducto.CASCARA)
                ? datos.storageUsdTnDia(dia, deposito.idUbicacion, TipoProducto.CASCARA)
                : 0;

            deposito.costoAceiteTnDia =
                datos.almacenaProducto(deposito.idUbicacion, TipoProducto.ACEITE)
                ? datos.storageUsdTnDia(dia, deposito.idUbicacion, TipoProducto.ACEITE)
                : 0;
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
            ? contenedoresNecesarios(envio.producto, pedido.material, envio.toneladas)
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
            costoFletePlantaDeposito + costoFleteGranelTerminal + costoFleteEntreDepositos, dia);

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

    double registrarOutDepositoTransferencia(String idSitio, TipoProducto producto, double toneladas, String idLote, String idOperacion) {
        // ADR-066: egreso de un deposito de terceros por un rebalanceo hacia otro
        // deposito, no por un despacho a pedido -no hay Envio-. Mismo cargo que
        // registrarOutDeposito(Envio).
        DatosEntrada.TarifaSitio tarifa =
            datos.tarifaSitio(diaCampania(), idSitio, producto);

        double importe = registro.registrar(
            time(),
            RegistroCostos.Categoria.OUT_DEPOSITO,
            RegistroCostos.Tipo.CAJA,
            "", "", idLote, producto,
            idSitio, idSitio, idSitio,
            EstrategiaLogistica.SIN_DEFINIR, tarifa.proveedor,
            DatosEntrada.Unidad.USD_TN, toneladas, tarifa.outUsdTn,
            idOperacion, "egreso por rebalanceo entre depositos (ADR-066)"
        );

        costoOutDeposito += importe;

        return importe;
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
            contenedoresNecesarios(envio.producto, envio.pedido.material, envio.toneladas);

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
            thcUsdContenedorPedido(dia, envio.pedido, terminal),
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
            contenedoresNecesarios(envio.producto, pedido.material, envio.toneladas);

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

        // Dia del devengo del cierre, no el del servicio: el envio puede quedar listo en la
        // terminal un dia y cerrarse en otro, y la tarifa cobrada es la del registro (ADR-062).
        int diaCierre =
            envio.diaCargosCierre >= 0
            ? (int) envio.diaCargosCierre
            : (int) Math.floor(envio.diaEntrega);

        int contenedores =
            contenedoresNecesarios(envio.producto, pedido.material, envio.toneladas);

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

        esperado += thcUsdContenedorPedido(diaTerminal, pedido, terminal) * contenedores;

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
            AlternativaCircuito.TRANSFERENCIA_DEPOSITO_DEPOSITO,
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

        // Sin capacidad no hay alternativa (ADR-060): habia producto, lo que falta es donde
        // procesarlo antes del cut-off. Se costea igual, con el volumen que el stock permitia,
        // para poder medir despues cuanto cuesta la saturacion.
        if (
            toneladas <= 0.0001
            && usaAgendaCapacidad()
            && alternativa.toneladasSinRestriccionCapacidad > 0.0001
        ) {

            alternativa.toneladas = alternativa.toneladasSinRestriccionCapacidad;

            alternativa.contenedores =
                contenedoresNecesarios(pedido.producto, pedido.material, alternativa.toneladas);

            costearAlternativa(pedido, alternativa);

            alternativa.costoUnitarioSinRestriccion =
                alternativa.costoUnitarioSegun(decideEndToEnd());

            alternativa.toneladas = 0;
            alternativa.contenedores = 0;

            alternativa.descartar(
                AlternativaCircuito.SIN_CAPACIDAD_ANTES_CUTOFF,
                "sin capacidad antes del cutoff en " + alternativa.idUbicacionCapacidad);

            if (exportarDiagnosticoCapacidad) {
                diagnosticoAsignaciones.add(
                    diaCampania() + "," + pedido.codigoPedido + ","
                    + alternativa.clave() + "," + alternativa.tipoRecursoCapacidad + ","
                    + alternativa.idUbicacionCapacidad + ","
                    + Math.round(alternativa.toneladasSinRestriccionCapacidad * 100) / 100.0
                    + ",0,0,SIN_CAPACIDAD_ANTES_CUTOFF");
            }

            return;
        }

        // Habia producto y habia capacidad, lo que no hay es camion: el motivo tiene que
        // decirlo, porque el remedio es otro (comprar viajes, no alquilar posiciones). El
        // volumen sin restriccion se costea igual para medir cuanto cuesta la falta de flota.
        if (
            toneladas <= 0.0001
            && alternativa.requiereFlotaProducto
            && alternativa.toneladasSinRestriccionCapacidad > 0.0001
        ) {

            String motivoFlota =
                alternativa.diagnosticoFlota.isEmpty()
                ? ViajeProducto.SIN_FLOTA_ANTES_CUTOFF
                : alternativa.diagnosticoFlota;

            alternativa.descartar(
                motivoFlota,
                motivoFlota + " "
                + (alternativa.esCrossDock ? "PLANTA" : alternativa.idOrigen)
                + "->"
                + (alternativa.esCrossDock ? alternativa.idOrigen : terminal));

            contarDescarteFlota(motivoFlota, pedido.codigoPedido);

            return;
        }

        // Con asignacion parcial el volumen ya viene acotado a lo posible: si quedo en cero, el
        // origen no tiene nada que ofrecer hoy (ADR-055).
        if (toneladas <= 0.0001) {
            alternativa.descartar(
                alternativa.esCrossDock
                ? AlternativaCircuito.SIN_STOCK_ESPACIO_O_CUPO
                : AlternativaCircuito.SIN_STOCK,
                alternativa.esCrossDock
                ? "sin stock, espacio o cupo para cruzar por " + alternativa.idOrigen
                : "sin stock libre en " + alternativa.idOrigen);
            return;
        }

        if (alternativa.esCrossDock) {

            if (!habilitaCrossDock) {
                alternativa.descartar(
            AlternativaCircuito.CROSS_DOCK_DESHABILITADO, "cross dock deshabilitado en el escenario");
                return;
            }

            // El cross dock cruza producto que todavia esta en planta: lo que ya entro a un
            // deposito no vuelve a cruzar (ADR-010).
            if (
                inventario.libre("PLANTA", pedido.producto, pedido.material) + 0.0001
                < toneladas
            ) {
                alternativa.descartar(
            AlternativaCircuito.SIN_STOCK_PLANTA_PARA_CRUZAR, "sin stock libre en planta para cruzar");
                return;
            }

            Deposito sitio = buscarDeposito(alternativa.idOrigen);

            if (sitio == null || !sitio.habilitado) {
                alternativa.descartar(
            AlternativaCircuito.ORIGEN_NO_HABILITADO, "deposito no habilitado");
                return;
            }

            if (
                capacidadCrossDockLibre(alternativa.idOrigen)
                < alternativa.contenedores
            ) {
                alternativa.descartar(
            AlternativaCircuito.SIN_CUPO_CROSS_DOCK, "sin cupo de cross dock hoy");
                return;
            }

            if (
                espacioDisponibleEfectivo(sitio, pedido.producto) + 0.0001
                < toneladas
            ) {
                alternativa.descartar(
            AlternativaCircuito.SIN_ESPACIO_DE_PASO, "sin espacio de paso en el deposito");
                return;
            }

            // Con la agenda esto ya se resolvio en acotarAlternativaPorFlota(): el volumen
            // viene acotado a los viajes que llegan hoy (ADR-061).
            if (
                !usaFlotaMultidiaria()
                && !flotaProductoAlcanza("PLANTA", alternativa.idOrigen, toneladas)
            ) {
                alternativa.descartar(
            AlternativaCircuito.SIN_FLOTA_PLANTA_DEPOSITO, "sin flota de producto planta-deposito");
                return;
            }

        } else {

            if (
                inventario.libre(alternativa.idOrigen, pedido.producto, pedido.material) + 0.0001
                < toneladas
            ) {
                alternativa.descartar(
            AlternativaCircuito.SIN_STOCK, "sin stock libre en " + alternativa.idOrigen);
                return;
            }

            if (
                alternativa.circuito == EstrategiaLogistica.CONSOLIDACION_TERMINAL
                && !usaFlotaMultidiaria()
                && !flotaProductoAlcanza(alternativa.idOrigen, terminal, toneladas)
            ) {
                alternativa.descartar(
            AlternativaCircuito.SIN_FLOTA_GRANEL_TERMINAL, "sin flota de producto para el granel a terminal");
                return;
            }
        }

        // El contenedor se arma en algun lado: sin capacidad declarada el circuito no puede
        // ejecutarse ningun dia, no solo hoy.
        if (
            !alternativa.esCrossDock
            && datos.ubicacion(alternativa.sitioEstiba).contenedoresPorDia <= 0
        ) {
            alternativa.descartar(
            AlternativaCircuito.CAPACIDAD_ESTIBA_CERO, "sin capacidad de estiba en " + alternativa.sitioEstiba);
            return;
        }

        costearAlternativa(pedido, alternativa);

        alternativa.costoUnitarioSinRestriccion =
            alternativa.costoUnitarioSegun(decideEndToEnd());

        // Un dia para armar y programar el contenedor mas el ciclo fisico del circuito,
        // contados desde que la ventana de retiro abre: antes de esa fecha el circuito
        // no puede empezar por mas barato que sea (ADR-059).
        // La espera por un camion libre es tiempo que el ciclo no conoce: horasCicloAlternativa()
        // ya cuenta el viaje, asi que se suma solo lo que el pedido espera para arrancar
        // (ADR-061). Sumar tambien el viaje seria contarlo dos veces.
        alternativa.diaEntregaEstimado =
            max(time(), pedido.diaAperturaRetiroVacio)
            + alternativa.esperaFlotaDias
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
            thcUsdContenedorPedido(dia, pedido, terminal) * contenedores;

        alternativa.costoTerminal =
            datos.costoTerminalUsdContenedor(dia, terminal, pedido.producto) * contenedores;

        DatosEntrada.TarifaSitio tarifa =
            datos.tarifaSitio(dia, terminal, pedido.producto);

        alternativa.costoDespachante =
            tarifa.despachanteUnidad == DatosEntrada.Unidad.USD_PEDIDO
            ? datos.despachanteTarifa(dia, terminal, pedido.producto)
            : datos.despachanteTarifa(dia, terminal, pedido.producto) * contenedores;

        costearHundidoAlternativa(pedido, alternativa);

        // Credito de holding futuro para el ranking (ADR-065): no es un cargo, no entra a
        // totalizar().
        alternativa.costoHoldingEvitado =
            tarifaHoldingOrigen(alternativa.idOrigen, pedido.producto)
            * horizonteHoldingEvitado()
            * alternativa.toneladas;

        alternativa.totalizar();
    }

    double tarifaHoldingOrigen(String idOrigen, TipoProducto producto) {
        // Tarifa de "costo de tener 1 tn parada" en el origen: oportunidad de frio propio
        // si es PLANTA, storage del deposito en cualquier otro caso (ADR-065). Unifica el
        // criterio que seleccionarDeposito() ya usa para Planta->Deposito (ADR-056) con el
        // que le faltaba al evaluador de circuitos para Deposito/Planta->Pedido (ADR-054).
        if ("PLANTA".equals(idOrigen)) {
            return datos.tarifaSitio(diaCampania(), "PLANTA", producto).oportunidadUsdTnDia;
        }

        Deposito deposito = buscarDeposito(idOrigen);

        return deposito == null ? 0 : deposito.getTarifaAlmacenamiento(producto);
    }

    double horizonteHoldingEvitado() {
        // Dias de holding futuro que se dan por evitados al despachar hoy en vez de dejarlo
        // donde esta (ADR-065). Reusa diasEstimadosAlmacenamiento (ADR-056) como unica fuente
        // de verdad del horizonte, acotado por lo que queda de campania: proyectar 30 dias de
        // storage evitado a 3 dias del cierre exageraria el credito.
        return min(diasEstimadosAlmacenamiento, max(0, duracionCampaniaDias - diaCampania()));
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
            * toneladaDiaEnStock(alternativa.idOrigen, pedido.producto, pedido.material, toneladas);
    }

    double toneladaDiaEnStock(String idUbicacion, TipoProducto producto, String material, double toneladas) {
        // Tonelada-dia acumulada por las capas FIFO que serviria la alternativa: es el
        // almacenaje que ese stock ya devengo, y depende de cual se consume, no del promedio.
        double pendiente = toneladas;

        double acumulado = 0;

        for (Capa capa : inventario.fifo(idUbicacion, producto, material)) {

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

        // ADR-066: un pedido puede traer un deposito con el que ya cuenta en la realidad
        // (stock ya posicionado para el, aunque el costo no lo haga ganar). Si lo trae,
        // esa alternativa gana mientras sea factible, antes de mirar costo.
        final String depositoComprometido = pedido.depositoComprometido;

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

                    if (depositoComprometido != null && !depositoComprometido.isEmpty()) {

                        boolean ca = depositoComprometido.equals(a.idOrigen);
                        boolean cb = depositoComprometido.equals(b.idOrigen);

                        if (ca != cb) {
                            return ca ? -1 : 1;
                        }
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
                                    a.costoUnitarioRankingSegun(endToEnd),
                                    b.costoUnitarioRankingSegun(endToEnd));

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

            // Identidad de la ronda (ADR-064). La vuelta es parte de la identidad: la misma
            // alternativa evaluada en dos vueltas del mismo dia son dos hechos distintos, porque
            // entre una y otra el pedido ya se llevo stock y capacidad.
            idDecisionActual = pedido.codigoPedido + "-D" + vueltas;

            double saldoAntes = pedido.toneladasPendientesAsignar();

            java.util.List<AlternativaCircuito> alternativas =
                generarAlternativas(pedido);

            etiquetarAlternativas(idDecisionActual, alternativas);

            java.util.List<AlternativaCircuito> ranking =
                ordenarAlternativas(pedido, alternativas);

            double tomadas = 0;

            AlternativaCircuito ejecutada = null;

            for (AlternativaCircuito elegida : ranking) {

                idAlternativaActual = elegida.idAlternativa;

                tomadas = ejecutarAlternativa(pedido, elegida);

                if (tomadas <= 0.0001) {
                    // No es una restriccion del sitio: la alternativa era factible y otro pedido del
                    // mismo dia se llevo lo que faltaba. Se distingue para poder medirlo (ADR-064).
                    elegida.descartar(
                        AlternativaCircuito.NO_TOMADA_AL_EJECUTAR,
                        "el flujo no pudo tomarla al ejecutar");
                    continue;
                }

                ejecutada = elegida;

                registrarPlan(pedido, alternativas, elegida, tomadas);

                registrarSaturacion(pedido, alternativas, elegida, tomadas);

                break;
            }

            idAlternativaActual = "";

            registrarDecisionRonda(
                pedido, idDecisionActual, vueltas, alternativas, ranking, ejecutada, tomadas,
                saldoAntes);

            idDecisionActual = "";

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
        asignacion.material = pedido.material;
        asignacion.prioridad = pedido.asignaciones.size() + 1;

        // Decision que creo esta asignacion (ADR-064). Se toma del contexto y no por parametro para
        // no cambiar la firma de las cinco funciones que crean asignaciones; queda vacio cuando la
        // asignacion no viene del evaluador (cross dock heuristico), y eso es un dato, no una falla.
        asignacion.idDecision = idDecisionActual;
        asignacion.idAlternativa = idAlternativaActual;

        pedido.asignaciones.add(asignacion);

        asignacionPorId.put(asignacion.idAsignacion, asignacion);

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
            min(pendiente, inventario.libre(idSitio, pedido.producto, pedido.material));

        if (aReservar <= 0.0001) {
            return 0;
        }

        // Capacidad antes que compromiso (ADR-060): solo se reserva inventario para lo que el
        // sitio puede procesar dentro de la ventana del pedido. El cross dock cruza hoy, con su
        // cupo propio; la estiba usa las posiciones del sitio donde se arma el contenedor.
        String tipoRecurso =
            cruza
            ? ReservaCapacidad.CROSS_DOCK
            : ReservaCapacidad.CONSOLIDACION;

        String sitioRecurso =
            cruza
            ? idSitio
            : sitioEstiba(idSitio, circuito, pedido.puertoSalida);

        int desdeDia = cruza ? diaCampania() : primerDiaVentanaCapacidad(pedido);
        int hastaDia = cruza ? diaCampania() : ultimoDiaVentanaCapacidad(pedido);

        boolean reservaPosiciones = usaAgendaCapacidad() || cruza;

        if (reservaPosiciones) {

            double capacidadContenedor =
                obtenerCapacidadContenedorTon(pedido.producto, pedido.material);

            aReservar =
                min(
                    aReservar,
                    capacidadDisponibleEnVentana(
                        tipoRecurso, sitioRecurso, desdeDia, hastaDia)
                    * capacidadContenedor);

            if (aReservar <= 0.0001) {
                return 0;
            }
        }

        AsignacionPedido asignacion =
            crearAsignacion(pedido, idSitio, circuito, cruza, motivo);

        double reservadas =
            inventario.reservar(
                idSitio,
                pedido.producto,
                pedido.material,
                aReservar,
                asignacion.claveReserva(),
                pedido.codigoPedido,
                time()
            );

        if (reservadas <= 0.0001) {

            // Nada que anotar: la asignacion no existe si no reservo.
            pedido.asignaciones.remove(asignacion);
            asignacionPorId.remove(asignacion.idAsignacion);
            asignacionesCreadas--;

            return 0;
        }

        asignacion.toneladasAsignadas = reservadas;
        asignacion.toneladasReservadasActivas = reservadas;

        if (reservaPosiciones) {

            int necesarias =
                contenedoresNecesarios(pedido.producto, pedido.material, reservadas);

            java.util.List<ReservaCapacidad> posiciones =
                reservarCapacidad(
                    asignacion.idAsignacion,
                    pedido.codigoPedido,
                    tipoRecurso,
                    sitioRecurso,
                    desdeDia,
                    hastaDia,
                    necesarias);

            if (posiciones.size() < necesarias) {

                // No deberia pasar: la capacidad se verifico contra la misma agenda un
                // instante antes. Si pasa, la agenda dejo de ser una restriccion.
                error(
                    "La asignacion " + asignacion.idAsignacion
                    + " reservo " + reservadas + " tn en " + sitioRecurso
                    + " y solo consiguio " + posiciones.size()
                    + " de " + necesarias + " posiciones.");
            }

            if (cruza) {
                operacionesCrossDock += posiciones.size();
            }
        }

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

        int circuitosAntes = circuitosDePedido(pedido);

        double asignadas =
            usaEvaluador()
            ? asignarConEvaluador(pedido)
            : asignarConPoliticaFija(pedido);

        if (antes <= 1 && pedido.cantidadOrigenes() > 1) {
            pedidosMultiOrigen++;
        }

        if (circuitosAntes <= 1 && circuitosDePedido(pedido) > 1) {
            pedidosMultiCircuito++;
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

        // Politica fija con capacidad finita (ADR-060): el circuito fijo se usa mientras sea
        // factible. Si el saldo no entra y el escenario habilita el fallback, se evaluan las
        // demas alternativas en vez de dejarlo sin cubrir; sin permiso, el saldo no es factible.
        if (
            pedido.toneladasPendientesAsignar() > 0.0001
            && datos.escenario.permiteFallbackPoliticaFija
        ) {

            double porFallback = asignarConEvaluador(pedido);

            if (porFallback > 0.0001) {
                fallbacksPoliticaFija++;
                asignadas += porFallback;
            }
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
                && inventario.libre(deposito.idUbicacion, pedido.producto, pedido.material) > 0.0001
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
                inventario.libre(deposito.idUbicacion, pedido.producto, pedido.material)
            );

        if (toneladas <= 0.0001) {
            return Double.POSITIVE_INFINITY;
        }

        int contenedores =
            contenedoresNecesarios(pedido.producto, pedido.material, toneladas);

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

    double toneladasDisponiblesParaAlternativa(String idOrigen, TipoProducto producto, String material, boolean cruza) {
        // Cuanto puede prometer hoy un candidato: stock libre en el origen, o para el cross
        // dock lo que hay libre en planta acotado por el espacio de paso y el cupo del dia.
        if (!cruza) {
            return inventario.libre(idOrigen, producto, material);
        }

        Deposito sitio = buscarDeposito(idOrigen);

        if (sitio == null || !sitio.habilitado) {
            return 0;
        }

        double capacidadContenedor =
            obtenerCapacidadContenedorTon(producto, material);

        return min(
            inventario.libre("PLANTA", producto, material),
            min(
                sitio.getEspacioDisponible(producto),
                capacidadCrossDockLibre(idOrigen) * capacidadContenedor
            )
        );
    }

    AlternativaCircuito alternativaPara(Pedido pedido, double pendiente, String idOrigen, String sitioEstiba, EstrategiaLogistica circuito, boolean cruza) {
        // Una alternativa por el volumen que su origen puede resolver, acotado por el saldo
        // pendiente y por la capacidad del sitio dentro de la ventana del pedido. Cero toneladas
        // es una alternativa que existe y se descarta con motivo, no una que desaparece.
        double stock =
            min(
                pendiente,
                toneladasDisponiblesParaAlternativa(idOrigen, pedido.producto, pedido.material, cruza)
            );

        stock = max(0, stock);

        AlternativaCircuito alternativa =
            new AlternativaCircuito(
                idOrigen,
                sitioEstiba,
                circuito,
                cruza,
                stock,
                contenedoresNecesarios(pedido.producto, pedido.material, stock)
            );

        alternativa.toneladasSinRestriccionCapacidad = stock;

        // El recurso que consume la alternativa: cruzar usa el cupo de cross dock del deposito
        // de paso y estibar usa las posiciones del sitio donde se arma el contenedor.
        alternativa.tipoRecursoCapacidad =
            cruza
            ? ReservaCapacidad.CROSS_DOCK
            : ReservaCapacidad.CONSOLIDACION;

        alternativa.idUbicacionCapacidad = cruza ? idOrigen : sitioEstiba;

        if (!usaAgendaCapacidad()) {
            acotarAlternativaPorFlota(pedido, alternativa);
            return alternativa;
        }

        // Capacidad antes que costo (ADR-060): cuantos contenedores puede procesar el sitio
        // entre la apertura del retiro y el cut-off. El cross dock se ejecuta el mismo dia en
        // que el producto cruza, asi que su ventana es hoy.
        int desde = cruza ? diaCampania() : primerDiaVentanaCapacidad(pedido);
        int hasta = cruza ? diaCampania() : ultimoDiaVentanaCapacidad(pedido);

        alternativa.contenedoresConCapacidad =
            capacidadDisponibleEnVentana(
                alternativa.tipoRecursoCapacidad,
                alternativa.idUbicacionCapacidad,
                desde,
                hasta);

        alternativa.diasCapacidadDisponibles =
            diasDisponiblesEnVentana(
                alternativa.tipoRecursoCapacidad,
                alternativa.idUbicacionCapacidad,
                desde,
                hasta);

        alternativa.capacidadReservable =
            alternativa.contenedoresConCapacidad > 0;

        alternativa.toneladasCapacidadDisponible =
            alternativa.contenedoresConCapacidad
            * obtenerCapacidadContenedorTon(pedido.producto, pedido.material);

        alternativa.toneladas =
            max(0, min(stock, alternativa.toneladasCapacidadDisponible));

        alternativa.contenedores =
            contenedoresNecesarios(pedido.producto, pedido.material, alternativa.toneladas);

        acotarAlternativaPorFlota(pedido, alternativa);

        return alternativa;
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
            obtenerTipoContenedor(pedido.producto, pedido.material);

        pedido.capacidadContenedorTon =
            obtenerCapacidadContenedorTon(pedido.producto, pedido.material);

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

        // Un contenedor por posicion reservada (ADR-060): no se arma un contenedor que el sitio
        // no puede procesar dentro de la ventana, por mas stock reservado que haya.
        int posicionesLibres =
            (usaAgendaCapacidad() || asignacion.esCrossDock)
            ? reservasLibresDe(asignacion.idAsignacion)
            : Integer.MAX_VALUE;

        int creados = 0;

        while (disponible > 0.0001 && creados < posicionesLibres) {

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
            contenedor.material = pedido.material;
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

            ReservaCapacidad posicion =
                reservaLibreDe(asignacion.idAsignacion);

            if (posicion != null) {

                posicion.idContenedor = contenedor.idContenedor;

                contenedor.claveReservaCapacidad = posicion.claveReserva;
                contenedor.diaPlanificadoOperacion = posicion.diaPlanificado;
                contenedor.idUbicacionOperacion = posicion.idUbicacion;
                contenedor.tipoRecursoOperacion = posicion.tipoRecurso;

                // El cruce ocurre el dia en que el producto sale de planta: la posicion de cross
                // dock se consume al armar el contenedor y no espera al despacho.
                if (asignacion.esCrossDock) {
                    consumirReservaCapacidad(posicion, diaCampania());
                }
            }

            registrarContenedorPorCircuito(asignacion.circuito, asignacion.esCrossDock);

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

        if (espacioDisponibleEfectivo(deposito, producto) <= 0.0001) {
            return "SIN_ESPACIO";
        }

        int dia = diaCampania();

        if (!datos.hayTarifaFlete(dia, "PLANTA", deposito.idUbicacion, producto)) {
            return "TARIFA_INEXISTENTE";
        }

        if (!datos.hayTarifaSitio(dia, deposito.idUbicacion, producto)) {
            return "TARIFA_SITIO_INEXISTENTE";
        }

        if (toneladas > 0.0001) {

            if (usaFlotaMultidiaria()) {

                // Con la agenda el motivo deja de ser "no entra en la jornada": es sin camiones,
                // sin camion antes de la fecha o solo una parte (ADR-061).
                ResultadoDisponibilidadFlota disponibilidad =
                    evaluarDisponibilidadFlotaProducto(
                        "PLANTA", deposito.idUbicacion, toneladas, time(),
                        limiteLlegadaTransferencia("PLANTA", deposito.idUbicacion, toneladas));

                if (!disponibilidad.puedeProgramarAlgo()) {
                    return disponibilidad.motivo.isEmpty()
                        ? ViajeProducto.SIN_FLOTA_ANTES_CUTOFF
                        : disponibilidad.motivo;
                }

            } else if (!flotaProductoAlcanza("PLANTA", deposito.idUbicacion, toneladas)) {
                return "SIN_FLOTA";
            }
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

        // El producto arriba de un camion no esta en ninguna ubicacion y todavia no se
        // entrego: es una cuarta ubicacion del balance, no una perdida (ADR-061).
        double enTransito = toneladasProductoEnTransito;

        if (abs(producido - (enStock + enProceso + entregado + enTransito)) > 0.001) {
            error(
                "Balance de producto el dia " + (int) floor(time())
                + ": stock inicial + producido " + producido
                + " contra stock " + enStock
                + " + en proceso " + enProceso
                + " + entregado " + entregado
                + " + en transito " + enTransito
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
                lote.material = fila.material;
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
                fila.material,
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

            // Si a este pedido se le descarto o se le acoto una alternativa por falta de
            // camiones, la perdida del buque tiene otro responsable que la capacidad de
            // estiba: el remedio es flota, no posiciones (ADR-061).
            if (pedidosConDescartePorFlota.contains(pedido.codigoPedido)) {
                pedidosPerdieronCutoffPorFlota++;
            }

            if ("CANCELAR".equals(datos.escenario.politicaReprogramacionBuque)) {

                // Politica dura: lo que no llega al buque no viaja. El saldo se cancela y
                // deja de competir por recursos, incluidas las posiciones que tenia tomadas:
                // si no se liberan, un pedido cancelado sigue bloqueando el sitio (CAP-05).
                pedido.estado = EstadoPedido.CANCELADO;

                for (AsignacionPedido asignacion : pedido.asignaciones) {

                    liberarCapacidadPorAsignacion(
                        asignacion.idAsignacion, "pedido cancelado por cut-off");
                }

                // Un viaje que todavia no salio ya no tiene a quien llevarle el producto:
                // libera el stock reservado y el camion. El que ya salio no se cancela,
                // porque el producto esta arriba del camion (ADR-061).
                for (
                    ViajeProducto viaje
                    : new java.util.ArrayList<ViajeProducto>(viajesProducto)
                ) {

                    if (
                        viaje.estado == EstadoViajeProducto.PROGRAMADO
                        && pedido.codigoPedido.equals(viaje.codigoPedido)
                    ) {
                        cancelarViajeProducto(viaje, "pedido cancelado por cut-off");
                    }
                }

            } else {

                // Politica por defecto: el saldo se rolea y sigue hasta entregarse.
                pedido.reprogramado = true;
                pedido.cantidadReprogramaciones++;
            }
        }
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

    boolean usaAgendaCapacidad() {
        // Interruptor maestro de la capacidad finita (ADR-060). Con el permiso apagado la
        // agenda no reserva nada y el modelo vuelve a la conducta anterior: la capacidad se
        // verifica el dia de la operacion y la futura es solo diagnostico.
        return datos != null
            && datos.escenario != null
            && datos.escenario.permiteReservaCapacidadFutura;
    }

    String claveRecurso(String tipoRecurso, String idUbicacion) {
        // La unidad de capacidad es el par recurso-sitio: la consolidacion y el cross dock de
        // un mismo deposito son cupos distintos y no se prestan posiciones (ADR-041).
        return tipoRecurso + "|" + idUbicacion;
    }

    int capacidadNominalDia(String tipoRecurso, String idUbicacion, int dia) {
        // Capacidad declarada del sitio para ese dia. contenedores_por_dia es capacidad diaria
        // total y no posiciones simultaneas (ADR-060, seccion 4.1); los factores del escenario
        // ya vienen aplicados en el dato de Ubicacion, aplicarlos aca los contaria dos veces.
        if (idUbicacion == null || idUbicacion.isEmpty()) {
            return 0;
        }

        if (dia < 0 || dia > duracionCampaniaDias) {
            return 0;
        }

        if (!datos.existeUbicacion(idUbicacion)) {
            return 0;
        }

        DatosEntrada.Ubicacion sitio = datos.ubicacion(idUbicacion);

        if (!sitio.habilitada) {
            return 0;
        }

        double nominal =
            ReservaCapacidad.CROSS_DOCK.equals(tipoRecurso)
            ? (habilitaCrossDock ? datos.capacidadCrossDockDia(idUbicacion) : 0)
            : datos.capacidadConsolidacionDia(idUbicacion);

        return (int) Math.floor(nominal + 0.0001);
    }

    int capacidadOcupadaDia(String tipoRecurso, String idUbicacion, int dia) {
        // Ocupacion unica del dia: reservas vivas mas operaciones ejecutadas sin reserva. Que
        // sea un solo contador es lo que evita el doble conteo entre planificar y ejecutar.
        LinkedHashMap<Integer, Integer> porDia =
            ocupacionCapacidad.get(claveRecurso(tipoRecurso, idUbicacion));

        if (porDia == null) {
            return 0;
        }

        Integer ocupadas = porDia.get(dia);

        return ocupadas == null ? 0 : ocupadas.intValue();
    }

    void ocuparCapacidad(String tipoRecurso, String idUbicacion, int dia, int delta) {
        String clave = claveRecurso(tipoRecurso, idUbicacion);

        LinkedHashMap<Integer, Integer> porDia = ocupacionCapacidad.get(clave);

        if (porDia == null) {
            porDia = new LinkedHashMap<Integer, Integer>();
            ocupacionCapacidad.put(clave, porDia);
        }

        Integer ocupadas = porDia.get(dia);

        int total = (ocupadas == null ? 0 : ocupadas.intValue()) + delta;

        if (total < 0) {
            error("La ocupacion de " + clave + " el dia " + dia + " quedo negativa.");
        }

        porDia.put(dia, total);
    }

    int capacidadDisponibleDia(String tipoRecurso, String idUbicacion, int dia) {
        return Math.max(
            0,
            capacidadNominalDia(tipoRecurso, idUbicacion, dia)
            - capacidadOcupadaDia(tipoRecurso, idUbicacion, dia));
    }

    int capacidadDisponibleEnVentana(String tipoRecurso, String idUbicacion, int desde, int hasta) {
        // Cuantos contenedores puede procesar el sitio entre dos dias inclusive. Es la
        // pregunta que el evaluador tiene que hacerse antes de comparar costos: una alternativa
        // barata sin dias disponibles no es una alternativa (ADR-060).
        int total = 0;

        for (int dia = Math.max(0, desde); dia <= hasta; dia++) {
            total += capacidadDisponibleDia(tipoRecurso, idUbicacion, dia);
        }

        return total;
    }

    java.util.List<Integer> diasDisponiblesEnVentana(String tipoRecurso, String idUbicacion, int desde, int hasta) {
        // Los dias con lugar, del mas temprano al mas tardio: reservar temprano es lo que baja
        // el riesgo de perder el cut-off.
        java.util.List<Integer> dias = new java.util.ArrayList<Integer>();

        for (int dia = Math.max(0, desde); dia <= hasta; dia++) {

            if (capacidadDisponibleDia(tipoRecurso, idUbicacion, dia) > 0) {
                dias.add(Integer.valueOf(dia));
            }
        }

        return dias;
    }

    int primerDiaVentanaCapacidad(Pedido pedido) {
        // El circuito fisico no puede empezar antes de que abra el retiro del vacio (ADR-059).
        return (int) Math.max(
            diaCampania(),
            Math.ceil(pedido.diaAperturaRetiroVacio - 0.0001));
    }

    int ultimoDiaVentanaCapacidad(Pedido pedido) {
        // No se reserva despues del cut-off (CAP-15). La unica excepcion es el pedido que ya lo
        // perdio y sigue viajando por politica CONTINUAR: si tampoco pudiera reservar, su saldo
        // quedaria inmovilizado con el inventario tomado y nadie lo despacharia nunca.
        int cutoff = (int) Math.floor(pedido.diaLimite + 0.0001);

        return cutoff >= primerDiaVentanaCapacidad(pedido)
            ? cutoff
            : duracionCampaniaDias;
    }

    java.util.List<ReservaCapacidad> reservarCapacidad(String claveAsignacion, String codigoPedido, String tipoRecurso, String idUbicacion, int desde, int hasta, int cantidad) {
        // Reserva hasta la capacidad disponible y devuelve lo que consiguio: la asignacion
        // parcial necesita poder reservar menos de lo pedido, y nunca mas que el cupo del dia.
        java.util.List<ReservaCapacidad> creadas =
            new java.util.ArrayList<ReservaCapacidad>();

        if (cantidad <= 0 || idUbicacion == null || idUbicacion.isEmpty()) {
            return creadas;
        }

        for (
            int dia = Math.max(0, desde);
            dia <= hasta && creadas.size() < cantidad;
            dia++
        ) {

            int libres = capacidadDisponibleDia(tipoRecurso, idUbicacion, dia);

            for (int i = 0; i < libres && creadas.size() < cantidad; i++) {

                ReservaCapacidad reserva =
                    new ReservaCapacidad(
                        "RES-" + siguienteIdReservaCapacidad,
                        codigoPedido,
                        claveAsignacion,
                        tipoRecurso,
                        idUbicacion,
                        dia,
                        hasta);

                siguienteIdReservaCapacidad++;

                reservasCapacidad.add(reserva);
                reservaPorClave.put(reserva.claveReserva, reserva);

                java.util.ArrayList<ReservaCapacidad> deLaAsignacion =
                    reservasPorAsignacion.get(claveAsignacion);

                if (deLaAsignacion == null) {
                    deLaAsignacion = new java.util.ArrayList<ReservaCapacidad>();
                    reservasPorAsignacion.put(claveAsignacion, deLaAsignacion);
                }

                deLaAsignacion.add(reserva);

                ocuparCapacidad(tipoRecurso, idUbicacion, dia, 1);

                capacidadReservadaTotal++;

                creadas.add(reserva);
            }
        }

        return creadas;
    }

    void liberarReserva(ReservaCapacidad reserva, String motivo) {
        // Liberar devuelve la posicion al dia: la capacidad no se pierde en silencio.
        if (reserva == null || reserva.liberada || reserva.consumida) {
            return;
        }

        reserva.activa = false;
        reserva.liberada = true;
        reserva.motivoBaja = motivo == null ? "" : motivo;
        reserva.idContenedor = "";

        ocuparCapacidad(
            reserva.tipoRecurso,
            reserva.idUbicacion,
            reserva.diaPlanificado,
            -1);

        capacidadLiberadaTotal++;
    }

    int liberarCapacidadPorAsignacion(String claveAsignacion, String motivo) {
        // Se usa al cancelar una asignacion, al cambiar de circuito y al cerrar la asignacion
        // con posiciones de mas: lo que no se va a usar vuelve al cupo del sitio.
        java.util.ArrayList<ReservaCapacidad> reservas =
            reservasPorAsignacion.get(claveAsignacion);

        if (reservas == null) {
            return 0;
        }

        int liberadas = 0;

        for (ReservaCapacidad reserva : reservas) {

            if (reserva.activa && !reserva.consumida && !reserva.liberada) {
                liberarReserva(reserva, motivo);
                liberadas++;
            }
        }

        return liberadas;
    }

    ReservaCapacidad reservaLibreDe(String claveAsignacion) {
        // La primera posicion libre de la asignacion, la mas temprana: es la que va a tomar el
        // proximo contenedor.
        java.util.ArrayList<ReservaCapacidad> reservas =
            reservasPorAsignacion.get(claveAsignacion);

        if (reservas == null) {
            return null;
        }

        ReservaCapacidad mejor = null;

        for (ReservaCapacidad reserva : reservas) {

            if (
                reserva.disponible()
                && (mejor == null || reserva.diaPlanificado < mejor.diaPlanificado)
            ) {
                mejor = reserva;
            }
        }

        return mejor;
    }

    int reservasLibresDe(String claveAsignacion) {
        java.util.ArrayList<ReservaCapacidad> reservas =
            reservasPorAsignacion.get(claveAsignacion);

        if (reservas == null) {
            return 0;
        }

        int libres = 0;

        for (ReservaCapacidad reserva : reservas) {

            if (reserva.disponible()) {
                libres++;
            }
        }

        return libres;
    }

    ReservaCapacidad reservaDeContenedor(ContenedorExportacion contenedor) {
        if (
            contenedor == null
            || contenedor.claveReservaCapacidad == null
            || contenedor.claveReservaCapacidad.isEmpty()
        ) {
            return null;
        }

        return reservaPorClave.get(contenedor.claveReservaCapacidad);
    }

    boolean consumirReservaCapacidad(ReservaCapacidad reserva, int dia) {
        // Ejecutar lo comprometido: la reserva pasa a consumida y la ocupacion del dia no se
        // vuelve a incrementar. Si la operacion cae mas tarde que el dia reservado, la ocupacion
        // se mueve al dia real para que ninguna ubicacion supere su capacidad diaria.
        if (
            reserva == null
            || !reserva.activa
            || reserva.consumida
            || reserva.liberada
            || reserva.diaPlanificado > dia
        ) {
            return false;
        }

        if (reserva.diaPlanificado < dia) {

            if (
                capacidadDisponibleDia(reserva.tipoRecurso, reserva.idUbicacion, dia) < 1
            ) {
                return false;
            }

            ocuparCapacidad(
                reserva.tipoRecurso, reserva.idUbicacion, reserva.diaPlanificado, -1);

            ocuparCapacidad(reserva.tipoRecurso, reserva.idUbicacion, dia, 1);

            reserva.diaPlanificado = dia;
            reserva.reprogramaciones++;
            reservasReprogramadas++;
        }

        reserva.consumida = true;
        reserva.activa = false;

        capacidadConsumidaTotal++;

        return true;
    }

    boolean asignacionNecesitaReserva(String claveAsignacion) {
        AsignacionPedido asignacion = asignacionPorId.get(claveAsignacion);

        if (asignacion == null || asignacion.cancelada) {
            return false;
        }

        return asignacion.toneladasPorContenerizar() > 0.0001;
    }

    void reprogramarReservasCapacidad() {
        // Paso diario: una posicion que no se uso el dia comprometido no se pierde y tampoco
        // bloquea el sitio. Se mueve al proximo dia con lugar dentro de la ventana; si no queda
        // ninguno antes del cut-off, se libera con motivo y el contenedor queda sin posicion.
        if (!usaAgendaCapacidad()) {
            return;
        }

        int hoy = diaCampania();

        for (ReservaCapacidad reserva : reservasCapacidad) {

            if (!reserva.activa || reserva.consumida || reserva.liberada) {
                continue;
            }

            // Posicion de mas: la asignacion ya tiene contenedor para todo lo comprometido.
            if (
                reserva.idContenedor.isEmpty()
                && !asignacionNecesitaReserva(reserva.claveAsignacion)
            ) {
                liberarReserva(reserva, "sobrante de la asignacion");
                continue;
            }

            if (reserva.diaPlanificado >= hoy) {
                continue;
            }

            int destino = -1;

            for (int dia = hoy; dia <= reserva.diaLimiteVentana; dia++) {

                if (
                    capacidadDisponibleDia(reserva.tipoRecurso, reserva.idUbicacion, dia) > 0
                ) {
                    destino = dia;
                    break;
                }
            }

            if (destino < 0) {
                liberarReserva(reserva, "SIN_CAPACIDAD_ANTES_CUTOFF");
                contenedoresSinPosicionFutura++;
                continue;
            }

            ocuparCapacidad(
                reserva.tipoRecurso, reserva.idUbicacion, reserva.diaPlanificado, -1);

            ocuparCapacidad(reserva.tipoRecurso, reserva.idUbicacion, destino, 1);

            reserva.diaPlanificado = destino;
            reserva.reprogramaciones++;
            reservasReprogramadas++;
        }
    }

    void reconciliarCapacidad() {
        // C-03 (CAP-12): lo reservado se explica siempre por lo consumido, lo activo y lo
        // liberado, y ninguna ubicacion supera su capacidad diaria. Si alguna de las dos cosas
        // falla, la agenda dejo de ser una restriccion y la corrida no sirve. Se controla
        // tambien con la agenda apagada: ahi el cross dock sigue reservando y la consolidacion
        // ocupa al ejecutar, y el techo diario tiene que valer igual.
        int activas = 0;
        int consumidas = 0;
        int liberadas = 0;

        for (ReservaCapacidad reserva : reservasCapacidad) {

            if (reserva.consumida) {
                consumidas++;
            } else if (reserva.liberada) {
                liberadas++;
            } else {
                activas++;
            }
        }

        if (
            activas + consumidas + liberadas != capacidadReservadaTotal
            || consumidas != capacidadConsumidaTotal
            || liberadas != capacidadLiberadaTotal
        ) {
            error(
                "C-03: la capacidad no reconcilia el dia " + diaCampania()
                + " (reservada " + capacidadReservadaTotal
                + ", activa " + activas
                + ", consumida " + consumidas + "/" + capacidadConsumidaTotal
                + ", liberada " + liberadas + "/" + capacidadLiberadaTotal + ").");
        }

        for (String clave : ocupacionCapacidad.keySet()) {

            int corte = clave.indexOf('|');

            String tipoRecurso = clave.substring(0, corte);
            String idUbicacion = clave.substring(corte + 1);

            LinkedHashMap<Integer, Integer> porDia = ocupacionCapacidad.get(clave);

            for (Integer dia : porDia.keySet()) {

                int nominal = capacidadNominalDia(tipoRecurso, idUbicacion, dia.intValue());

                if (porDia.get(dia).intValue() > nominal) {
                    error(
                        "C-03: " + clave + " tiene " + porDia.get(dia)
                        + " posiciones ocupadas el dia " + dia
                        + " y su capacidad es " + nominal + ".");
                }
            }
        }
    }

    void registrarContenedorPorCircuito(EstrategiaLogistica circuito, boolean cruza) {
        // Contenedores planificados por circuito (ADR-060): con capacidad finita el mismo
        // pedido puede repartirse entre circuitos y el reparto es el resultado a leer.
        String clave = cruza ? "CROSS_DOCK_DEPOSITO" : ("" + circuito);

        Integer previos = contenedoresPorCircuito.get(clave);

        contenedoresPorCircuito.put(
            clave,
            Integer.valueOf((previos == null ? 0 : previos.intValue()) + 1));
    }

    int circuitosDePedido(Pedido pedido) {
        java.util.List<String> vistos = new java.util.ArrayList<String>();

        for (AsignacionPedido asignacion : pedido.asignaciones) {

            String clave =
                asignacion.esCrossDock
                ? "CROSS_DOCK_DEPOSITO"
                : ("" + asignacion.circuito);

            if (!vistos.contains(clave)) {
                vistos.add(clave);
            }
        }

        return vistos.size();
    }

    void registrarSaturacion(Pedido pedido, java.util.List<AlternativaCircuito> alternativas, AlternativaCircuito elegida, double toneladas) {
        // Cuanto cuesta la saturacion (ADR-060): la diferencia entre lo que se pago y lo que
        // habria costado la alternativa mas barata si hubiera tenido capacidad. Es cero cuando la
        // capacidad no ata: mide la restriccion, no el costo.
        boolean endToEnd = decideEndToEnd();

        double costoElegido = elegida.costoUnitarioSegun(endToEnd);

        double mejorSinRestriccion = costoElegido;

        for (AlternativaCircuito alternativa : alternativas) {

            if (
                alternativa.factible
                || alternativa.costoUnitarioSinRestriccion == Double.POSITIVE_INFINITY
            ) {
                continue;
            }

            if (alternativa.costoUnitarioSinRestriccion < mejorSinRestriccion) {
                mejorSinRestriccion = alternativa.costoUnitarioSinRestriccion;
            }
        }

        if (mejorSinRestriccion + 0.0001 < costoElegido) {
            costoAdicionalSaturacion +=
                (costoElegido - mejorSinRestriccion) * toneladas;

            toneladasReasignadasPorCapacidad += toneladas;
        }

        if (exportarDiagnosticoCapacidad) {
            diagnosticoAsignaciones.add(
                diaCampania() + "," + pedido.codigoPedido + ","
                + elegida.clave() + "," + elegida.tipoRecursoCapacidad + ","
                + elegida.idUbicacionCapacidad + ","
                + Math.round(toneladas * 100) / 100.0 + ","
                + elegida.contenedoresConCapacidad + ","
                + Math.round((costoElegido - mejorSinRestriccion) * toneladas * 100) / 100.0
                + ",SELECCIONADA");
        }
    }

    void registrarColaCapacidad(String tipoRecurso, String idUbicacion) {
        // Cola del dia por recurso y sitio: contenedores que estaban listos y no encontraron
        // posicion. Es el numero que dimensiona posiciones, porque la ocupacion sola no dice si
        // falto lugar o falto trabajo.
        String clave = claveRecurso(tipoRecurso, idUbicacion);

        LinkedHashMap<Integer, Integer> porDia = colaCapacidad.get(clave);

        if (porDia == null) {
            porDia = new LinkedHashMap<Integer, Integer>();
            colaCapacidad.put(clave, porDia);
        }

        Integer previos = porDia.get(Integer.valueOf(diaCampania()));

        porDia.put(
            Integer.valueOf(diaCampania()),
            Integer.valueOf((previos == null ? 0 : previos.intValue()) + 1));
    }

    int colaCapacidadDia(String tipoRecurso, String idUbicacion, int dia) {
        LinkedHashMap<Integer, Integer> porDia =
            colaCapacidad.get(claveRecurso(tipoRecurso, idUbicacion));

        if (porDia == null) {
            return 0;
        }

        Integer cola = porDia.get(Integer.valueOf(dia));

        return cola == null ? 0 : cola.intValue();
    }

    int ultimoDiaConCapacidad() {
        // Ultimo dia que la agenda de capacidad tiene que publicar: la campania entera y, si una
        // reserva quedo planificada mas alla del ultimo dia de campania, tambien ese dia. El
        // horizonte no puede salir de una constante porque la reserva se programa contra el
        // cut-off del pedido, que es un dato del libro (ADR-060).
        int ultimo = duracionCampaniaDias;

        for (java.util.Map<Integer, Integer> porDia : ocupacionCapacidad.values()) {

            for (Integer dia : porDia.keySet()) {

                if (dia.intValue() > ultimo) {
                    ultimo = dia.intValue();
                }
            }
        }

        for (ReservaCapacidad reserva : reservasCapacidad) {

            if (reserva.diaPlanificado > ultimo) {
                ultimo = reserva.diaPlanificado;
            }
        }

        return ultimo;
    }

    void anclarCalendarioDeCampania() {
        // Fecha calendario del dia 1 (ADR-071). Manda la del escenario, porque el calendario es
        // un atributo de la campania igual que su duracion; el parametro de la corrida es el
        // default para el libro que no la declara y para los datos sinteticos.
        String delEscenario =
            datos.escenario.fechaInicioCampania == null
            ? ""
            : datos.escenario.fechaInicioCampania.trim();

        fechaInicioCampaniaEfectiva =
            delEscenario.isEmpty()
            ? (fechaInicioCampania == null ? "" : fechaInicioCampania.trim())
            : delEscenario;

        if (
            !fechaInicioCampaniaEfectiva.isEmpty()
            && !AuditoriaRed.esFechaIso(fechaInicioCampaniaEfectiva)
        ) {
            // Una fecha mal escrita no puede degradarse a columna vacia: el tablero la usa para
            // fechar la campania entera.
            throw new RuntimeException(
                "fecha_inicio_campania invalida: '" + fechaInicioCampaniaEfectiva
                + "'. Se espera una fecha real en formato YYYY-MM-DD.");
        }

        AuditoriaRed.anclarCalendario(fechaInicioCampaniaEfectiva);
    }

    int reservasDelDia(String tipoRecurso, String idUbicacion, int dia, String estado) {
        // Reservas de ese recurso, sitio y dia en un estado: ACTIVA, CONSUMIDA o LIBERADA.
        int total = 0;

        for (ReservaCapacidad reserva : reservasCapacidad) {

            if (
                !reserva.tipoRecurso.equals(tipoRecurso)
                || !reserva.idUbicacion.equals(idUbicacion)
                || reserva.diaPlanificado != dia
            ) {
                continue;
            }

            if ("CONSUMIDA".equals(estado) ? reserva.consumida
                : ("LIBERADA".equals(estado) ? reserva.liberada
                    : (!reserva.consumida && !reserva.liberada))) {
                total++;
            }
        }

        return total;
    }

    String resumenCapacidad() {
        // Linea del tablero: la capacidad como restriccion, no como dato de catalogo.
        return "Capacidad · reservadas " + capacidadReservadaTotal
            + " · consumidas " + capacidadConsumidaTotal
            + " · liberadas " + capacidadLiberadaTotal
            + " · reprogramadas " + reservasReprogramadas
            + " · sin posición antes del cut-off " + contenedoresSinPosicionFutura
            + " | multi-circuito " + pedidosMultiCircuito
            + " · fallback política fija " + fallbacksPoliticaFija
            + " · sobrecosto por saturación USD "
            + String.format("%,.0f", costoAdicionalSaturacion);
    }

    void exportarCapacidadSiCorresponde() {
        // El diagnostico se escribe una vez, el ultimo dia, y solo si el escenario lo pide: en
        // el barrido son millones de filas que nadie lee.
        if (
            (!exportarDiagnosticoCapacidad && !auditoria.activa())
            || diaCampania() < duracionCampaniaDias - 1
        ) {
            return;
        }

        exportarCapacidadPorDia("resultados/capacidad_por_dia.csv");
        exportarAsignacionesCapacidad("resultados/asignaciones_capacidad.csv");
    }

    void exportarCapacidadPorDia(String ruta) {
        java.io.PrintWriter salida = null;

        try {
            salida = new java.io.PrintWriter(ruta, "UTF-8");

            salida.println(encabezadoCapacidadRecursos());

            int ultimoDia = ultimoDiaConCapacidad();

            for (String clave : ocupacionCapacidad.keySet()) {

                int corte = clave.indexOf('|');

                String tipoRecurso = clave.substring(0, corte);
                String idUbicacion = clave.substring(corte + 1);

                // Los dias del modelo van del 1 al ultimo paso diario: arrancar en 0 publicaba un dia
                // que nunca existio y cortar en la duracion dejaba afuera la capacidad reservada para
                // el cierre, que es justo cuando la agenda esta mas apretada (ADR-071).
                for (int dia = 1; dia <= ultimoDia; dia++) {

                    int nominal = capacidadNominalDia(tipoRecurso, idUbicacion, dia);
                    int ocupada = capacidadOcupadaDia(tipoRecurso, idUbicacion, dia);
                    int cola = colaCapacidadDia(tipoRecurso, idUbicacion, dia);

                    if (nominal <= 0 && ocupada <= 0 && cola <= 0) {
                        continue;
                    }

                    salida.println(
                        AuditoriaRed.txt(auditoria.runId) + ","
                        + idEscenario + "," + replica + "," + dia + ","
                        + tipoRecurso + "," + idUbicacion + "," + nominal + ","
                        + reservasDelDia(tipoRecurso, idUbicacion, dia, "ACTIVA") + ","
                        + reservasDelDia(tipoRecurso, idUbicacion, dia, "CONSUMIDA") + ","
                        + reservasDelDia(tipoRecurso, idUbicacion, dia, "LIBERADA") + ","
                        + ocupada + "," + Math.max(0, nominal - ocupada) + "," + cola + ","
                        + AuditoriaRed.txt(AuditoriaRed.fecha(dia)));
                }
            }

        } catch (java.io.IOException e) {
            traceln("No se pudo escribir " + ruta + ": " + e.getMessage());

        } finally {
            if (salida != null) {
                salida.close();
            }
        }
    }

    void exportarAsignacionesCapacidad(String ruta) {
        // Dos bloques en un archivo no sirven, asi que van dos archivos: este es la vida de
        // cada posicion reservada -quien la pidio, para cuando, que contenedor la uso y por que
        // se dio de baja- y el de decisiones queda al lado con el sufijo _decisiones.
        java.io.PrintWriter salida = null;

        try {
            salida = new java.io.PrintWriter(ruta, "UTF-8");

            salida.println(
                "escenario,replica,reserva,pedido,asignacion,tipo_recurso,ubicacion,"
                + "dia_planificado,dia_original,dia_limite,reprogramaciones,contenedor,"
                + "estado,motivo_baja");

            for (ReservaCapacidad reserva : reservasCapacidad) {

                salida.println(
                    idEscenario + "," + replica + "," + reserva.claveReserva + ","
                    + reserva.codigoPedido + "," + reserva.claveAsignacion + ","
                    + reserva.tipoRecurso + "," + reserva.idUbicacion + ","
                    + reserva.diaPlanificado + "," + reserva.diaOriginal + ","
                    + reserva.diaLimiteVentana + "," + reserva.reprogramaciones + ","
                    + reserva.idContenedor + ","
                    + (reserva.consumida
                        ? "CONSUMIDA"
                        : (reserva.liberada ? "LIBERADA" : "ACTIVA"))
                    + "," + reserva.motivoBaja);
            }

        } catch (java.io.IOException e) {
            traceln("No se pudo escribir " + ruta + ": " + e.getMessage());

        } finally {
            if (salida != null) {
                salida.close();
            }
        }

        // Deprecado por ADR-064: decisiones_alternativas registra la misma decision con la
        // identidad de la ronda y el resultado de ejecucion, asi que con la auditoria activa este
        // archivo seria una segunda version incompleta del mismo hecho.
        if (diagnosticoAsignaciones.isEmpty() || auditoria.activa()) {
            return;
        }

        String rutaDecisiones = ruta.replace(".csv", "_decisiones.csv");

        salida = null;

        try {
            salida = new java.io.PrintWriter(rutaDecisiones, "UTF-8");

            salida.println(
                "escenario,replica,dia,pedido,alternativa,tipo_recurso,ubicacion,"
                + "toneladas,contenedores_con_capacidad,sobrecosto_saturacion_usd,resultado");

            for (String linea : diagnosticoAsignaciones) {
                salida.println(idEscenario + "," + replica + "," + linea);
            }

        } catch (java.io.IOException e) {
            traceln("No se pudo escribir " + rutaDecisiones + ": " + e.getMessage());

        } finally {
            if (salida != null) {
                salida.close();
            }
        }
    }

    boolean usaFlotaMultidiaria() {
        // Interruptor de regresion (ADR-061): en false corre la capacidad diaria agregada de
        // ADR-044, con el movimiento instantaneo y sin transito.
        return datos != null
            && datos.escenario != null
            && datos.escenario.habilitaFlotaProductoMultidiaria;
    }

    void inicializarFlotaProducto() {
        // Los camiones se crean una sola vez por corrida, despues de leer el escenario. No se
        // recrean cada dia: un camion que salio sigue ocupado manana, y eso es todo el cambio
        // (ADR-061).
        unidadesFlotaProducto.clear();
        viajesProducto.clear();
        viajeProductoPorId.clear();
        siguienteIdViajeProducto = 1;

        if (!usaFlotaMultidiaria()) {
            return;
        }

        int cantidad = datos.escenario.camionesProducto;

        for (int i = 1; i <= cantidad; i++) {
            unidadesFlotaProducto.add(
                new UnidadFlotaProducto(i, "PLANTA")
            );
        }
    }

    double duracionIdaProductoDias(String origen, String destino) {
        // Solo la ida: el retorno se cuenta aparte porque el producto llega a destino antes de
        // que el camion se libere (ADR-061). La jornada es la del escenario, no un supuesto.
        double distancia = datos.distanciaKmSimetrica(origen, destino);

        double velocidad = datos.escenario.velocidadCamionKmh;

        double jornada = datos.escenario.horasOperativasDia;

        return distancia <= 0 || velocidad <= 0 || jornada <= 0
            ? 0
            : distancia / velocidad / jornada;
    }

    double duracionRetornoProductoDias(String origen, String destino) {
        // Si no hay distancia inversa declarada, datos.distanciaKm() devuelve la misma: el
        // retorno vale lo que la ida.
        return duracionIdaProductoDias(destino, origen);
    }

    double horasManipuleoViajeProducto(String origen, String destino, double toneladas) {
        // Carga en el origen y descarga en el destino con las velocidades declaradas de cada
        // sitio: son los mismos campos que usa crearEnvio(), asi que el modelo tiene una sola
        // duracion para el mismo viaje y nada cableado.
        double horas = 0;

        DatosEntrada.Ubicacion salida = datos.ubicacion(origen);

        if (salida != null && salida.velocidadCargaTnHora > 0) {
            horas += toneladas / salida.velocidadCargaTnHora;
        }

        DatosEntrada.Ubicacion llegada = datos.ubicacion(destino);

        if (llegada != null && llegada.velocidadDescargaTnHora > 0) {
            horas += toneladas / llegada.velocidadDescargaTnHora;
        }

        return horas;
    }

    double duracionTotalViajeProductoDias(String origen, String destino, double toneladas) {
        // Lo que el camion queda ocupado: ida, manipuleo y retorno. Es el mismo numero que
        // camionDiaViaje() calculaba para la capacidad diaria (ADR-044), mas el manipuleo; lo
        // que cambia es que ya no tiene que entrar en una sola jornada.
        double jornada = datos.escenario.horasOperativasDia;

        return duracionIdaProductoDias(origen, destino)
            + duracionRetornoProductoDias(origen, destino)
            + (jornada <= 0
                ? 0
                : horasManipuleoViajeProducto(origen, destino, toneladas) / jornada);
    }

    UnidadFlotaProducto buscarCamionDisponibleMasTemprano(double noAntesDe) {
        // El camion que puede salir antes. El empate se rompe por id para que la corrida sea
        // determinista: dos corridas iguales asignan los mismos camiones a los mismos viajes.
        UnidadFlotaProducto elegido = null;

        double mejor = Double.POSITIVE_INFINITY;

        for (UnidadFlotaProducto camion : unidadesFlotaProducto) {

            if (!camion.activo) {
                continue;
            }

            double salida = camion.salidaMasTemprana(noAntesDe);

            if (salida < mejor - 0.0001) {
                mejor = salida;
                elegido = camion;
            }
        }

        return elegido;
    }

    double fechaSalidaMasTempranaProducto(double noAntesDe) {
        UnidadFlotaProducto camion = buscarCamionDisponibleMasTemprano(noAntesDe);

        return camion == null ? -1 : camion.salidaMasTemprana(noAntesDe);
    }

    int camionesDisponiblesEn(double dia) {
        int cantidad = 0;

        for (UnidadFlotaProducto camion : unidadesFlotaProducto) {

            if (camion.disponibleEn(dia)) {
                cantidad++;
            }
        }

        return cantidad;
    }

    int camionesProductoEnRuta(double dia) {
        int cantidad = 0;

        for (ViajeProducto viaje : viajesProducto) {

            if (
                viaje.vivo()
                && viaje.diaSalida >= 0
                && viaje.diaSalida <= dia + 0.0001
                && viaje.diaRegreso > dia + 0.0001
            ) {
                cantidad++;
            }
        }

        return cantidad;
    }

    ResultadoDisponibilidadFlota evaluarDisponibilidadFlotaProducto(String origen, String destino, double toneladas, double noAntesDe, double fechaLimite) {
        // Que puede prometer la agenda sin tocarla: simula sobre una copia de las fechas de
        // disponibilidad y no crea viajes (ADR-061). Reemplaza el si/no de flotaProductoAlcanza():
        // si solo entra una parte del volumen, la alternativa compite por esa parte.
        ResultadoDisponibilidadFlota resultado = new ResultadoDisponibilidadFlota();

        resultado.toneladasSolicitadas = max(0, toneladas);

        if (toneladas <= 0.0001) {
            return resultado;
        }

        double capacidad = datos.escenario.capacidadCamionTn;

        resultado.viajesRequeridos = viajesNecesariosCamion(toneladas);

        if (unidadesFlotaProducto.isEmpty()) {
            resultado.motivo = ViajeProducto.SIN_CAMIONES_CONFIGURADOS;
            return resultado;
        }

        double ida = duracionIdaProductoDias(origen, destino);

        double retorno = duracionRetornoProductoDias(origen, destino);

        if (ida <= 0 && retorno <= 0) {
            // Sin distancia el viaje no existe como movimiento fisico: es un dato faltante y no
            // una ruta gratis.
            resultado.motivo = ViajeProducto.RUTA_SIN_DISTANCIA;
            return resultado;
        }

        double[] libres = new double[unidadesFlotaProducto.size()];

        for (int i = 0; i < libres.length; i++) {
            libres[i] = unidadesFlotaProducto.get(i).activo
                ? unidadesFlotaProducto.get(i).disponibleDesde
                : Double.POSITIVE_INFINITY;
        }

        double pendiente = toneladas;

        int guarda = 0;

        while (pendiente > 0.0001 && guarda <= resultado.viajesRequeridos) {

            guarda++;

            double carga = min(pendiente, capacidad);

            int mejor = -1;

            for (int i = 0; i < libres.length; i++) {

                if (mejor < 0 || libres[i] < libres[mejor] - 0.0001) {
                    mejor = i;
                }
            }

            if (mejor < 0 || Double.isInfinite(libres[mejor])) {
                resultado.motivo = ViajeProducto.SIN_CAMIONES_CONFIGURADOS;
                break;
            }

            double salida = max(noAntesDe, libres[mejor]);

            double llegada =
                salida + ida
                + horasManipuleoViajeProducto(origen, destino, carga)
                    / max(0.0001, datos.escenario.horasOperativasDia);

            // El limite es del movimiento, no del camion: si el producto no puede llegar antes de
            // la fecha, ese viaje no sirve y los siguientes tampoco, porque salen mas tarde.
            if (llegada > fechaLimite + 0.0001) {
                resultado.motivo = resultado.toneladasProgramables > 0.0001
                    ? ViajeProducto.FLOTA_PARCIAL
                    : ViajeProducto.SIN_FLOTA_ANTES_CUTOFF;
                break;
            }

            resultado.toneladasProgramables += carga;
            resultado.viajesProgramables++;

            if (resultado.primeraSalida < 0) {
                resultado.primeraSalida = salida;
            }

            resultado.ultimaSalida = max(resultado.ultimaSalida, salida);
            resultado.ultimaLlegada = max(resultado.ultimaLlegada, llegada);
            resultado.ultimoRegreso = max(resultado.ultimoRegreso, llegada + retorno);

            resultado.esperaMaximaDias =
                max(resultado.esperaMaximaDias, salida - noAntesDe);

            libres[mejor] = llegada + retorno;

            pendiente -= carga;
        }

        if (resultado.motivo.isEmpty() && !resultado.puedeProgramarTodo()) {
            resultado.motivo = resultado.puedeProgramarAlgo()
                ? ViajeProducto.FLOTA_PARCIAL
                : ViajeProducto.SIN_FLOTA_ANTES_CUTOFF;
        }

        if (resultado.motivo.isEmpty() && resultado.esperaMaximaDias > 0.0001) {
            resultado.motivo = ViajeProducto.ESPERA_FLOTA;
        }

        return resultado;
    }

    double diaProduccionDeLoteEn(int idLote, String idUbicacion, TipoProducto producto) {
        // La antiguedad del producto no se pierde en ruta: el viaje se lleva el dia de produccion
        // de la capa que carga y la capa que se crea en destino lo conserva (ADR-047).
        java.util.List<Capa> capas =
            idLote > 0
            ? inventario.fifoDeLote(idLote, idUbicacion)
            : inventario.fifo(idUbicacion, producto);

        for (Capa capa : capas) {

            if (capa.libres() > 0.0001) {
                return capa.diaProduccion;
            }
        }

        return time();
    }

    ViajeProducto programarViajeProducto(String origen, String destino, TipoProducto producto, double toneladas, int idLote, String codigoPedido, EstrategiaLogistica estrategia, boolean crossDock, String idOperacion, double noAntesDe, double fechaLimiteSalida, boolean ocupaSoloFlota) {
        // Un viaje de un camion (ADR-061). Programar no mueve producto: reserva el stock, ocupa
        // el camion hasta su regreso y deja escritas las fechas de salida, llegada y regreso. El
        // producto sale del origen al salir el viaje y aparece en destino al llegar.
        if (toneladas <= 0.0001 || producto == null) {
            return null;
        }

        UnidadFlotaProducto camion = buscarCamionDisponibleMasTemprano(noAntesDe);

        if (camion == null) {
            return null;
        }

        double carga = min(toneladas, datos.escenario.capacidadCamionTn);

        double salida = camion.salidaMasTemprana(noAntesDe);

        if (salida > fechaLimiteSalida + 0.0001) {
            return null;
        }

        ViajeProducto viaje = new ViajeProducto();

        viaje.idViaje = "V-" + siguienteIdViajeProducto;
        siguienteIdViajeProducto++;

        viaje.idCamion = camion.idCamion;
        viaje.idOperacion = idOperacion;
        viaje.idLote = idLote;
        viaje.codigoPedido = codigoPedido;
        viaje.producto = producto;
        viaje.origen = origen;
        viaje.destino = destino;
        viaje.estrategia = estrategia;
        viaje.crossDock = crossDock;
        viaje.ocupaSoloFlota = ocupaSoloFlota;

        // El stock se compromete con las reservas de capa que ya existen (ADR-023), con la clave
        // del viaje: asi lo comprometido deja de estar libre para otro pedido sin que aparezca una
        // segunda fuente de verdad de reservas.
        if (!ocupaSoloFlota) {

            viaje.diaProduccionLote =
                diaProduccionDeLoteEn(idLote, origen, producto);

            double reservadas =
                idLote > 0
                ? inventario.reservarDeLote(
                    idLote, origen, carga, viaje.claveReservaStock(), codigoPedido, time())
                : inventario.reservar(
                    origen, producto, carga, viaje.claveReservaStock(), codigoPedido, time());

            if (reservadas <= 0.0001) {
                contarDescarteFlota(ViajeProducto.STOCK_NO_RESERVABLE, codigoPedido);
                return null;
            }

            carga = reservadas;
            toneladasReservadasParaTransporte += reservadas;
        }

        viaje.toneladas = carga;
        viaje.distanciaKmIda = datos.distanciaKmSimetrica(origen, destino);
        viaje.duracionIdaDias = duracionIdaProductoDias(origen, destino);
        viaje.duracionRetornoDias = duracionRetornoProductoDias(origen, destino);

        viaje.diaProgramacion = time();
        viaje.diaSolicitud = noAntesDe;
        viaje.diaSalida = salida;

        viaje.diaLlegadaDestino =
            salida
            + viaje.duracionIdaDias
            + horasManipuleoViajeProducto(origen, destino, carga)
                / max(0.0001, datos.escenario.horasOperativasDia);

        viaje.diaInicioRetorno = viaje.diaLlegadaDestino;
        viaje.diaRegreso = viaje.diaLlegadaDestino + viaje.duracionRetornoDias;

        viaje.estado = EstadoViajeProducto.PROGRAMADO;

        camion.disponibleDesde = viaje.diaRegreso;
        camion.idViajeActual = viaje.idViaje;
        camion.ubicacionActual = origen;

        viajesProducto.add(viaje);
        viajeProductoPorId.put(viaje.idViaje, viaje);
        viajesProductoProgramados++;

        double espera = viaje.esperaFlotaDias();

        if (espera > 0.0001) {
            esperaFlotaProductoDiasAcumulada += espera;
            esperaFlotaProductoDiasMaxima = max(esperaFlotaProductoDiasMaxima, espera);
            movimientosConEsperaFlota++;
        }

        // Un viaje que sale hoy sale hoy: el paso diario ya corrio sus salidas cuando la
        // planificacion lo pide, y esperar al dia siguiente atrasaria un dia todas las rutas que
        // entran en una jornada, sin razon fisica.
        procesarViajeProductoInmediato(viaje);

        return viaje;
    }

    double programarMovimientoProducto(String origen, String destino, TipoProducto producto, double toneladasObjetivo, int idLote, String codigoPedido, EstrategiaLogistica estrategia, boolean crossDock, String idOperacion, double noAntesDe, double fechaLimiteSalida, boolean ocupaSoloFlota) {
        // Programa el volumen en viajes de hasta un camion y devuelve lo programado, que puede
        // ser una parte (ADR-061). Un movimiento parcial no se revierte porque no entre el total:
        // mover cinco de ocho viajes es mejor que mover cero.
        if (toneladasObjetivo <= 0.0001) {
            return 0;
        }

        double capacidad = datos.escenario.capacidadCamionTn;

        int maximoViajes = viajesNecesariosCamion(toneladasObjetivo);

        double programadas = 0;

        double pendiente = toneladasObjetivo;

        int guarda = 0;

        while (pendiente > 0.0001 && guarda < maximoViajes + 1) {

            guarda++;

            ViajeProducto viaje =
                programarViajeProducto(
                    origen, destino, producto, min(pendiente, capacidad), idLote, codigoPedido,
                    estrategia, crossDock, idOperacion, noAntesDe, fechaLimiteSalida,
                    ocupaSoloFlota);

            if (viaje == null) {
                break;
            }

            programadas += viaje.toneladas;
            pendiente -= viaje.toneladas;
        }

        double faltante = max(0, toneladasObjetivo - programadas);

        if (faltante > 0.0001) {

            toneladasNoProgramadasPorFlota += faltante;

            if (programadas > 0.0001) {
                toneladasProgramadasParcialmente += programadas;
                movimientosParcialesPorFlota++;
                contarDescarteFlota(ViajeProducto.FLOTA_PARCIAL, codigoPedido);

            } else {
                contarDescarteFlota(ViajeProducto.SIN_FLOTA_ANTES_CUTOFF, codigoPedido);
            }
        }

        return programadas;
    }

    void contarDescarteFlota(String motivo, String codigoPedido) {
        // Un solo texto por concepto para poder agrupar el diagnostico (seccion 27 del MOD).
        Integer previo = descartesFlotaPorMotivo.get(motivo);

        descartesFlotaPorMotivo.put(motivo, previo == null ? 1 : previo + 1);

        if (codigoPedido != null && !codigoPedido.isEmpty()) {
            pedidosConDescartePorFlota.add(codigoPedido);
        }
    }

    void iniciarViajeProducto(ViajeProducto viaje) {
        // Salida del viaje: aca el producto deja el origen y pasa a estar en transito, y aca se
        // devenga el flete, una sola vez por viaje fisico (ADR-061).
        if (viaje == null || viaje.estado != EstadoViajeProducto.PROGRAMADO) {
            return;
        }

        if (!viaje.ocupaSoloFlota) {

            double sacadas =
                viaje.idLote > 0
                ? inventario.despacharDeLote(
                    viaje.idLote, viaje.origen, viaje.toneladas, viaje.claveReservaStock())
                : inventario.despachar(
                    viaje.origen, viaje.producto, viaje.toneladas, viaje.claveReservaStock());

            if (abs(sacadas - viaje.toneladas) > 0.001) {
                error(
                    "El viaje " + viaje.idViaje + " no pudo retirar su carga: "
                    + sacadas + " de " + viaje.toneladas + " tn en " + viaje.origen);
            }

            viaje.stockRetiradoOrigen = true;
            toneladasReservadasParaTransporte -= sacadas;
            toneladasProductoEnTransito += sacadas;
            toneladasTransferidasSalidas += sacadas;

            // El flete del tramo lo paga el viaje que efectivamente sale: un viaje cancelado antes
            // de salir no paga (seccion 25.1 del MOD). El circuito 4 lo cobra el envio, que es el
            // que tiene el movimiento fisico del granel.
            double costo =
                registrarFleteProducto(
                    viaje.origen, viaje.destino, viaje.producto, viaje.toneladas, 1,
                    viaje.idLote > 0 ? "" + viaje.idLote : "", viaje.codigoPedido,
                    viaje.estrategia, "FLV-" + viaje.idViaje);

            viaje.fleteRegistrado = true;

            LoteProducto lote = buscarLote(viaje.idLote);

            if (lote != null) {
                lote.costoAcumulado += costo;
            }

            if (buscarTerminal(viaje.destino) == null) {
                costoFletePlantaDeposito += costo;
            }
        }

        viaje.estado = EstadoViajeProducto.EN_TRANSITO_DESTINO;
        viajesProductoIniciados++;

        if (buscarTerminal(viaje.destino) != null) {
            viajesGranelTerminal++;
        } else {
            viajesPlantaDeposito++;
        }
    }

    void recibirViajeProducto(ViajeProducto viaje) {
        // Llegada a destino: el producto entra al inventario del sitio recien ahora, con la fecha
        // de llegada como dia de ingreso y conservando lote y produccion (ADR-061).
        if (viaje == null || viaje.estado != EstadoViajeProducto.EN_TRANSITO_DESTINO) {
            return;
        }

        viaje.estado = EstadoViajeProducto.DESCARGANDO;

        if (viaje.ocupaSoloFlota) {
            // El movimiento fisico del granel lo ejecuta el flujo del envio: el viaje solo tiene
            // ocupado el camion.
            viaje.estado = EstadoViajeProducto.RETORNANDO;
            return;
        }

        if (!viaje.stockRetiradoOrigen) {
            error("El viaje " + viaje.idViaje + " llego sin haber retirado la carga del origen");
        }

        // La capa nueva hereda el material del lote (ADR-067); un viaje sin lote (idLote <= 0)
        // no tiene material propio, y "" es el mismo sentinel que usa el resto del modelo.
        LoteProducto loteViaje = buscarLote(viaje.idLote);
        String materialViaje = loteViaje != null ? loteViaje.material : "";

        inventario.ingresar(
            viaje.idLote,
            viaje.producto,
            materialViaje,
            viaje.destino,
            viaje.toneladas,
            viaje.diaLlegadaDestino,
            viaje.diaProduccionLote);

        toneladasProductoEnTransito -= viaje.toneladas;
        viaje.stockIngresadoDestino = true;

        // El viaje ya recorrio el arco: sale del origen al salir y entra al destino al llegar
        // (ADR-061), asi que la duracion real existe recien aca (ADR-064).
        registrarArcoViajeProducto(viaje);
        registrarFlujoVisual(viaje.origen, viaje.destino, viaje.toneladas);

        Deposito destino = buscarDeposito(viaje.destino);

        LoteProducto lote = buscarLote(viaje.idLote);

        if (destino != null) {

            destino.toneladasRecibidasAcumuladas += viaje.toneladas;
            destino.cantidadRecepciones++;

            // El ingreso al almacenamiento lo paga el producto que se queda: el que cruza no entra
            // al stock y no lo paga (ADR-053).
            if (!viaje.crossDock) {

                double costoIn =
                    registrarInDeposito(
                        viaje.destino, viaje.producto, viaje.toneladas,
                        viaje.idLote > 0 ? "" + viaje.idLote : "", viaje.codigoPedido,
                        "INV-" + viaje.idViaje);

                if (lote != null) {
                    lote.costoAcumulado += costoIn;
                }
            }

            if (lote != null) {
                lote.depositoActual = destino;
                lote.diaIngresoDeposito = viaje.diaLlegadaDestino;
            }

            toneladasTransferidasDepositos += viaje.toneladas;
            cantidadTransferenciasDepositos++;
        }

        if (lote != null) {
            actualizarUbicacionLote(lote);
        }

        viaje.estado = EstadoViajeProducto.RETORNANDO;
    }

    void completarViajeProducto(ViajeProducto viaje) {
        // Regreso del camion: recien aca vuelve a estar disponible. Un camion no se libera al
        // llegar a destino si el viaje incluye retorno (ADR-061).
        if (viaje == null || viaje.estado != EstadoViajeProducto.RETORNANDO) {
            return;
        }

        if (!viaje.ocupaSoloFlota && !viaje.stockIngresadoDestino) {
            error("El viaje " + viaje.idViaje + " se completa sin haber ingresado la carga");
        }

        viaje.estado = EstadoViajeProducto.COMPLETADO;
        viajesProductoCompletados++;

        double camionDia = viaje.camionDiaOcupado();

        camionDiaOcupado += camionDia;

        for (UnidadFlotaProducto camion : unidadesFlotaProducto) {

            if (camion.idCamion != viaje.idCamion) {
                continue;
            }

            camion.camionDiaAcumulado += camionDia;
            camion.viajesCompletados++;
            camion.ubicacionActual = camion.ubicacionBase;

            if (camion.idViajeActual.equals(viaje.idViaje)) {
                camion.idViajeActual = "";
            }
        }
    }

    void procesarViajeProductoInmediato(ViajeProducto viaje) {
        // Un evento fechado hoy ocurre hoy: el reloj es diario pero las fechas del viaje son
        // fraccionarias, asi que un viaje que sale, llega o vuelve dentro de la jornada de hoy se
        // procesa en el mismo paso. Sin esto las rutas cortas atrasarian un dia por el redondeo.
        if (viaje == null) {
            return;
        }

        double finDelDia = floor(time()) + 1;

        if (viaje.diaSalida < finDelDia) {
            iniciarViajeProducto(viaje);
        }

        if (viaje.diaLlegadaDestino < finDelDia) {
            recibirViajeProducto(viaje);
        }

        if (viaje.diaRegreso < finDelDia) {
            completarViajeProducto(viaje);
        }
    }

    void iniciarViajesProductoDelDia() {
        if (!usaFlotaMultidiaria()) {
            return;
        }

        double finDelDia = floor(time()) + 1;

        for (ViajeProducto viaje : new java.util.ArrayList<ViajeProducto>(viajesProducto)) {

            if (
                viaje.estado == EstadoViajeProducto.PROGRAMADO
                && viaje.diaSalida < finDelDia
            ) {
                iniciarViajeProducto(viaje);

                // Una ruta que entra en la jornada llega el mismo dia en que sale.
                if (viaje.diaLlegadaDestino < finDelDia) {
                    recibirViajeProducto(viaje);
                }

                if (viaje.diaRegreso < finDelDia) {
                    completarViajeProducto(viaje);
                }
            }
        }
    }

    void recibirViajesProductoDelDia() {
        if (!usaFlotaMultidiaria()) {
            return;
        }

        double finDelDia = floor(time()) + 1;

        for (ViajeProducto viaje : new java.util.ArrayList<ViajeProducto>(viajesProducto)) {

            if (
                viaje.estado == EstadoViajeProducto.EN_TRANSITO_DESTINO
                && viaje.diaLlegadaDestino < finDelDia
            ) {
                recibirViajeProducto(viaje);
            }
        }
    }

    void completarViajesProductoDelDia() {
        if (!usaFlotaMultidiaria()) {
            return;
        }

        double finDelDia = floor(time()) + 1;

        for (ViajeProducto viaje : new java.util.ArrayList<ViajeProducto>(viajesProducto)) {

            if (
                viaje.estado == EstadoViajeProducto.RETORNANDO
                && viaje.diaRegreso < finDelDia
            ) {
                completarViajeProducto(viaje);
            }
        }
    }

    void cancelarViajeProducto(ViajeProducto viaje, String motivo) {
        // Cancelar antes de salir libera el stock comprometido y el camion; no cobra flete
        // porque el viaje no ocurrio (seccion 25.1 del MOD). Un viaje ya salido no se cancela: el
        // producto esta arriba del camion.
        if (viaje == null || viaje.estado != EstadoViajeProducto.PROGRAMADO) {
            return;
        }

        if (!viaje.ocupaSoloFlota) {

            double liberadas =
                inventario.liberarReserva(viaje.claveReservaStock());

            toneladasReservadasParaTransporte -= liberadas;
        }

        viaje.estado = EstadoViajeProducto.CANCELADO;
        viaje.motivoBaja = motivo;
        viajesProductoCancelados++;

        for (UnidadFlotaProducto camion : unidadesFlotaProducto) {

            if (camion.idCamion != viaje.idCamion) {
                continue;
            }

            if (camion.idViajeActual.equals(viaje.idViaje)) {
                camion.idViajeActual = "";
            }

            // El camion no hizo este viaje, pero puede tener otros ya programados: queda
            // libre cuando vuelve del ultimo que le sigue vivo, nunca antes de hoy.
            double libre = time();

            for (ViajeProducto otro : viajesProducto) {

                if (otro.idCamion == camion.idCamion && otro.vivo()) {
                    libre = max(libre, otro.diaRegreso);
                }
            }

            camion.disponibleDesde = libre;
        }
    }

    double toneladasEnTransitoHacia(String destino, TipoProducto producto) {
        // Producto que ya viene en camino a un sitio. Sin esto el espacio del deposito se
        // comprometeria dos veces: el volumen en ruta todavia no esta en el stock.
        double total = 0;

        for (ViajeProducto viaje : viajesProducto) {

            if (
                viaje.vivo()
                && !viaje.ocupaSoloFlota
                && !viaje.stockIngresadoDestino
                && viaje.producto == producto
                && viaje.destino.equals(destino)
            ) {
                total += viaje.toneladas;
            }
        }

        return total;
    }

    double toneladasComprometidasParaViajesDe(TipoProducto producto) {
        // Reservado para un viaje que todavia no salio: sigue en el stock del origen, pero ya
        // esta saliendo. La transferencia del dia siguiente no tiene que volver a pedirlo.
        double total = 0;

        for (ViajeProducto viaje : viajesProducto) {

            if (
                viaje.estado == EstadoViajeProducto.PROGRAMADO
                && !viaje.ocupaSoloFlota
                && viaje.producto == producto
            ) {
                total += viaje.toneladas;
            }
        }

        return total;
    }

    double espacioDisponibleEfectivo(Deposito deposito, TipoProducto producto) {
        if (deposito == null) {
            return 0;
        }

        return max(
            0,
            deposito.getEspacioDisponible(producto)
            - toneladasEnTransitoHacia(deposito.idUbicacion, producto));
    }

    double toneladasProductoEnTransitoDe(TipoProducto producto) {
        double total = 0;

        for (ViajeProducto viaje : viajesProducto) {

            if (
                viaje.enTransito()
                && !viaje.ocupaSoloFlota
                && viaje.producto == producto
            ) {
                total += viaje.toneladas;
            }
        }

        return total;
    }

    int viajesProductoEnCurso() {
        int cantidad = 0;

        for (ViajeProducto viaje : viajesProducto) {

            if (viaje.vivo() && viaje.estado != EstadoViajeProducto.PROGRAMADO) {
                cantidad++;
            }
        }

        return cantidad;
    }

    void medirFlotaDelDia() {
        if (!usaFlotaMultidiaria()) {
            return;
        }

        int enRuta = camionesProductoEnRuta(time());

        picoCamionesProductoEnRuta = max(picoCamionesProductoEnRuta, enRuta);
        camionesEnRutaDiaAcumulado += enRuta;
        diasFlotaMedidos++;
    }

    double camionesProductoPromedioEnRuta() {
        return diasFlotaMedidos <= 0
            ? 0
            : camionesEnRutaDiaAcumulado / diasFlotaMedidos;
    }

    double esperaMediaFlotaProductoDias() {
        return movimientosConEsperaFlota <= 0
            ? 0
            : esperaFlotaProductoDiasAcumulada / movimientosConEsperaFlota;
    }

    void reconciliarFlotaProducto() {
        // C-04: la agenda de camiones tiene que ser fisicamente posible. Un camion con dos
        // viajes al mismo tiempo o un viaje que ingresa carga que nunca retiro son errores del
        // modelo, no datos (ADR-061).
        if (!usaFlotaMultidiaria()) {
            return;
        }

        java.util.List<String> errores = new java.util.ArrayList<String>();

        for (UnidadFlotaProducto camion : unidadesFlotaProducto) {

            java.util.List<ViajeProducto> propios = new java.util.ArrayList<ViajeProducto>();

            double ultimoRegreso = 0;

            for (ViajeProducto viaje : viajesProducto) {

                if (viaje.idCamion != camion.idCamion || !viaje.vivo()) {
                    continue;
                }

                propios.add(viaje);
                ultimoRegreso = max(ultimoRegreso, viaje.diaRegreso);
            }

            // Un camion puede tener el viaje en curso y el siguiente ya programado para cuando
            // vuelve: eso es una agenda, no un error. Lo imposible es estar en dos viajes al mismo
            // tiempo, y eso es lo que se audita (ADR-061).
            for (int i = 0; i < propios.size(); i++) {

                for (int j = i + 1; j < propios.size(); j++) {

                    ViajeProducto a = propios.get(i);
                    ViajeProducto b = propios.get(j);

                    if (
                        a.diaSalida < b.diaRegreso - 0.0001
                        && b.diaSalida < a.diaRegreso - 0.0001
                    ) {
                        errores.add(
                            "el camion " + camion.idCamion + " superpone los viajes " + a.idViaje
                            + " (" + a.diaSalida + " a " + a.diaRegreso + ") y " + b.idViaje
                            + " (" + b.diaSalida + " a " + b.diaRegreso + ")");
                    }
                }
            }

            if (camion.disponibleDesde + 0.0001 < ultimoRegreso) {
                errores.add(
                    "el camion " + camion.idCamion + " esta libre desde " + camion.disponibleDesde
                    + " y su ultimo viaje regresa " + ultimoRegreso);
            }
        }

        double enTransito = 0;

        double reservadas = 0;

        for (ViajeProducto viaje : viajesProducto) {

            if (viaje.vivo() && viaje.toneladas <= 0.0001 && !viaje.ocupaSoloFlota) {
                errores.add("el viaje " + viaje.idViaje + " no lleva carga");
            }

            if (viaje.toneladas > datos.escenario.capacidadCamionTn + 0.0001) {
                errores.add(
                    "el viaje " + viaje.idViaje + " lleva " + viaje.toneladas
                    + " tn, mas que un camion");
            }

            if (
                viaje.diaSalida > viaje.diaLlegadaDestino + 0.0001
                || viaje.diaLlegadaDestino > viaje.diaRegreso + 0.0001
            ) {
                errores.add("el viaje " + viaje.idViaje + " tiene fechas incoherentes");
            }

            if (viaje.stockIngresadoDestino && !viaje.stockRetiradoOrigen) {
                errores.add("el viaje " + viaje.idViaje + " ingreso carga que no retiro");
            }

            if (viaje.enTransito() && !viaje.ocupaSoloFlota) {
                enTransito += viaje.toneladas;
            }

            if (viaje.estado == EstadoViajeProducto.PROGRAMADO && !viaje.ocupaSoloFlota) {
                reservadas += inventario.reservadoDeClave(viaje.claveReservaStock());
            }
        }

        if (abs(enTransito - toneladasProductoEnTransito) > 0.001) {
            errores.add(
                "el transito de los viajes es " + enTransito
                + " y el contador dice " + toneladasProductoEnTransito);
        }

        if (abs(reservadas - toneladasReservadasParaTransporte) > 0.001) {
            errores.add(
                "lo reservado para viajes es " + reservadas
                + " y el contador dice " + toneladasReservadasParaTransporte);
        }

        if (!errores.isEmpty()) {

            StringBuilder detalle = new StringBuilder();

            for (String linea : errores) {
                detalle.append("\n - ").append(linea);
            }

            error(
                "Flota de producto inconsistente el dia " + (int) floor(time())
                + " (" + errores.size() + "):" + detalle);
        }
    }

    double limiteLlegadaTransferencia(String origen, String destino, double toneladas) {
        // Hasta cuando sirve programar una transferencia: el horizonte de compromiso de la agenda
        // mas lo que tarda el viaje. Un viaje que llega despues no ayuda a nadie y deja el stock
        // inmovilizado (ADR-061).
        return time()
            + datos.escenario.diasMaxProgramacionFlota
            + duracionIdaProductoDias(origen, destino)
            + horasManipuleoViajeProducto(
                origen, destino, min(toneladas, datos.escenario.capacidadCamionTn))
                / max(0.0001, datos.escenario.horasOperativasDia);
    }

    boolean cruceLlegaEnElDia(String origen, String destino, double toneladas) {
        // El cross dock es, por definicion, producto que llega y sale el mismo dia sin ingresar al
        // stock (ADR-010). Con viajes fisicos eso deja de ser gratis: si el camion no llega dentro
        // de la jornada, ese sitio no puede cruzar y el pedido tiene que usar otro circuito. Antes
        // el producto se teletransportaba y cualquier distancia cruzaba.
        if (!usaFlotaMultidiaria()) {
            return true;
        }

        double salida = fechaSalidaMasTempranaProducto(time());

        if (salida < 0) {
            return false;
        }

        double llegada =
            salida
            + duracionIdaProductoDias(origen, destino)
            + horasManipuleoViajeProducto(
                origen, destino, min(toneladas, datos.escenario.capacidadCamionTn))
                / max(0.0001, datos.escenario.horasOperativasDia);

        return llegada < floor(time()) + 1;
    }

    double programarTransferenciaLote(LoteProducto lote, Deposito destino, double toneladas, boolean cruza) {
        // Transferencia planta-deposito con viajes fisicos (ADR-061). Aca solo se programa: el
        // producto sigue en planta, reservado, hasta que el viaje sale. El flete se devenga al
        // salir y el ingreso al deposito al llegar.
        double aMover =
            min(
                min(toneladas, inventario.libreDeLoteEn(lote.idLote, "PLANTA")),
                espacioDisponibleEfectivo(destino, lote.producto));

        if (aMover <= 0.0001) {
            return 0;
        }

        double limiteSalida =
            cruza
            ? floor(time()) + 1 - 0.001
            : time() + datos.escenario.diasMaxProgramacionFlota;

        if (cruza) {

            // Cruzar es llegar y salir el mismo dia: se acota el volumen a los viajes que llegan
            // hoy, y si no llega ninguno el sitio no puede cruzar (ADR-010).
            ResultadoDisponibilidadFlota disponibilidad =
                evaluarDisponibilidadFlotaProducto(
                    "PLANTA", destino.idUbicacion, aMover, time(), floor(time()) + 1);

            if (!disponibilidad.puedeProgramarAlgo()) {
                contarDescarteFlota(ViajeProducto.CRUCE_SIN_LLEGADA_EN_EL_DIA, "");
                return 0;
            }

            aMover = min(aMover, disponibilidad.toneladasProgramables);
        }

        double programadas =
            programarMovimientoProducto(
                "PLANTA",
                destino.idUbicacion,
                lote.producto,
                aMover,
                lote.idLote,
                "",
                cruza
                ? EstrategiaLogistica.CROSS_DOCK_DEPOSITO
                : EstrategiaLogistica.SIN_DEFINIR,
                cruza,
                "TRA-" + diaCampania() + "-" + lote.idLote + "-" + destino.idUbicacion
                    + "-" + cantidadTransferenciasDepositos,
                time(),
                limiteSalida,
                false);

        toneladasTransferidasProgramadas += programadas;

        return programadas;
    }

    boolean flotaProductoDisponibleParaGranel(Pedido pedido, ContenedorExportacion contenedor) {
        // Circuito 4: el producto va a granel en camion propio hasta la terminal. El contenedor es
        // una unidad fisica indivisible, asi que o hay camiones para toda su carga hoy o espera:
        // media carga en la terminal no arma un contenedor (ADR-050, ADR-061).
        String terminal = pedido.puertoSalida.idUbicacion;

        double toneladas = contenedor.cantidadAsignadaTon;

        if (!usaFlotaMultidiaria()) {
            return flotaProductoAlcanza(contenedor.idSitioOrigen, terminal, toneladas);
        }

        ResultadoDisponibilidadFlota disponibilidad =
            evaluarDisponibilidadFlotaProducto(
                contenedor.idSitioOrigen, terminal, toneladas, time(),
                limiteLlegadaTransferencia(contenedor.idSitioOrigen, terminal, toneladas));

        boolean salenHoy =
            disponibilidad.ultimaSalida >= 0
            && disponibilidad.ultimaSalida < floor(time()) + 1;

        if (!disponibilidad.puedeProgramarTodo() || !salenHoy) {

            contarDescarteFlota(
                disponibilidad.motivo.isEmpty()
                ? ViajeProducto.ESPERA_FLOTA
                : disponibilidad.motivo,
                pedido.codigoPedido);

            return false;
        }

        return true;
    }

    void ocuparFlotaParaGranel(Pedido pedido, ContenedorExportacion contenedor) {
        // El viaje de granel solo ocupa el camion: el movimiento fisico del producto y su flete
        // los hace el envio, que ya modela el ciclo con sus delays. Dos duraciones para el mismo
        // viaje serian dos verdades (ADR-050, ADR-061).
        String terminal = pedido.puertoSalida.idUbicacion;

        if (!usaFlotaMultidiaria()) {

            tomarFlotaProducto(
                contenedor.idSitioOrigen,
                terminal,
                viajesNecesariosCamion(contenedor.cantidadAsignadaTon));

            return;
        }

        double programadas =
            programarMovimientoProducto(
                contenedor.idSitioOrigen,
                terminal,
                pedido.producto,
                contenedor.cantidadAsignadaTon,
                0,
                pedido.codigoPedido,
                EstrategiaLogistica.CONSOLIDACION_TERMINAL,
                false,
                "GRA-" + contenedor.idContenedor,
                time(),
                floor(time()) + 1 - 0.001,
                true);

        if (programadas + 0.0001 < contenedor.cantidadAsignadaTon) {
            error(
                "El contenedor " + contenedor.idContenedor + " salio a granel con "
                + programadas + " de " + contenedor.cantidadAsignadaTon
                + " tn de flota: la disponibilidad se verifico antes de crear el envio");
        }
    }

    void acotarAlternativaPorFlota(Pedido pedido, AlternativaCircuito alternativa) {
        // Flota antes de costo (ADR-060 dejo el orden capacidad -> factibilidad -> costo -> reserva;
        // ADR-061 agrega la flota al primer paso). La alternativa vale por lo que la agenda de
        // camiones puede mover dentro de la ventana, no por lo que hay en stock. Esta consulta no
        // muta la agenda: simula sobre una copia de las fechas de disponibilidad.
        if (!usaFlotaMultidiaria() || alternativa == null || pedido == null) {
            return;
        }

        boolean granel =
            alternativa.circuito == EstrategiaLogistica.CONSOLIDACION_TERMINAL;

        // Los circuitos que no mueven producto con la flota propia no dependen de ella: el
        // contenedor se arma donde el producto ya esta y despues viaja en portacontenedor.
        if (!alternativa.esCrossDock && !granel) {
            return;
        }

        String origen = alternativa.esCrossDock ? "PLANTA" : alternativa.idOrigen;

        String destino =
            alternativa.esCrossDock
            ? alternativa.idOrigen
            : pedido.puertoSalida.idUbicacion;

        alternativa.requiereFlotaProducto = true;

        if (alternativa.toneladas <= 0.0001) {
            return;
        }

        // Cruzar es hoy; el granel puede esperar hasta el cut-off, y si el pedido ya lo perdio
        // igual conviene medirlo contra el horizonte de compromiso de la agenda.
        double fechaLimite =
            alternativa.esCrossDock
            ? floor(time()) + 1
            : max(
                pedido.diaLimite,
                limiteLlegadaTransferencia(origen, destino, alternativa.toneladas));

        ResultadoDisponibilidadFlota disponibilidad =
            evaluarDisponibilidadFlotaProducto(
                origen, destino, alternativa.toneladas, time(), fechaLimite);

        alternativa.toneladasFactiblesPorFlota = disponibilidad.toneladasProgramables;
        alternativa.viajesFactiblesPorFlota = disponibilidad.viajesProgramables;
        alternativa.primeraSalidaProducto = disponibilidad.primeraSalida;
        alternativa.ultimaSalidaProducto = disponibilidad.ultimaSalida;
        alternativa.ultimaLlegadaProducto = disponibilidad.ultimaLlegada;
        alternativa.ultimoRegresoProducto = disponibilidad.ultimoRegreso;
        alternativa.esperaFlotaDias = disponibilidad.esperaMaximaDias;
        alternativa.flotaCompleta = disponibilidad.puedeProgramarTodo();
        alternativa.flotaParcial =
            disponibilidad.puedeProgramarAlgo() && !disponibilidad.puedeProgramarTodo();
        alternativa.diagnosticoFlota = disponibilidad.motivo;

        // Parcial no es cero: la alternativa entra por lo que la flota puede mover y el saldo
        // vuelve a competir manana con los demas pedidos (ADR-055).
        alternativa.toneladas =
            max(0, min(alternativa.toneladas, disponibilidad.toneladasProgramables));

        if (
            alternativa.esCrossDock
            && alternativa.toneladas > 0.0001
            && !cruceLlegaEnElDia(origen, destino, alternativa.toneladas)
        ) {

            alternativa.toneladas = 0;
            alternativa.flotaCompleta = false;
            alternativa.flotaParcial = false;
            alternativa.diagnosticoFlota = ViajeProducto.CRUCE_SIN_LLEGADA_EN_EL_DIA;
        }

        alternativa.contenedores =
            contenedoresNecesarios(pedido.producto, pedido.material, alternativa.toneladas);
    }

    // ----- Eventos -----

    // evento pasoDiario [timeout cyclic] cada 1 day
    void pasoDiario_accion() {
        // Secuencia diaria del modelo (ADR-034). El orden es parte de la
        // definicion: cambiarlo cambia el costo y el servicio del dia.
        fotografiarStockInicialDelDia();         // 0a. foto del inventario antes del dia (ADR-064)
        refrescarTarifasDelDia();                // 0. tarifa vigente de hoy (ADR-051)
        abrirFlotaDelDia();                      // 0b. abrir la capacidad de flota del dia

        // Flota multidiaria (ADR-061). Los tres pasos van antes de producir y de planificar:
        // primero vuelven los camiones, despues llega el producto que estaba en ruta y solo
        // entonces salen los viajes del dia. Al reves, un pedido podria usar producto que
        // todavia esta arriba de un camion.
        completarViajesProductoDelDia();    // 0c. regresos: el camion vuelve a estar libre
        recibirViajesProductoDelDia();      // 0d. llegadas: recien aca el producto esta
        iniciarViajesProductoDelDia();      // 0e. salidas del dia

        producirEnPlantas();                     // 1. producir
        registrarPedidosDelDia();                // 2. planificar y comprometer
        actualizarVentanasRetiroDelDia();        // 2b. abrir la ventana de retiro del vacio
        abrirPosicionesConsolidacionDelDia();    // 3. abrir la capacidad de estiba del dia
        abrirPosicionesCrossDockDelDia();        // 4. abrir la capacidad de cross dock del dia
        reprogramarReservasCapacidad();          // 4b. mover las posiciones no usadas (ADR-060)
        programarCrossDockDelDia();              // 5. cruzar lo que no necesita guardarse
        revisarTransferenciasPlanta();           // 6. sacar de planta solo lo necesario
        revisarRebalanceoEntreDepositos();       // 6b. mover entre depositos lo que quedo sin salida (ADR-066)
        revisarPedidosPendientes();              // 7. reservar contra stock
        prepararPedidosReservados();             // 8. armar los contenedores del pedido
        despacharContenedoresPendientes();       // 9. consolidar y despachar lo que entra
        devengarAlmacenamientoDiario();          // 10. devengar almacenaje
        devengarOportunidadFrioPropio();         // 10b. devengar el uso del frio propio
        registrarOcupacionPlanta();              // 10c. medir la sobrecarga del dia
        registrarAtrasos();                      // 11. registrar indicadores del dia
        registrarPerdidaDeCutoff();              // 11b. que pedidos perdieron su buque
        medirFlotaDelDia();                      // 11c. camiones en ruta y pico (ADR-061)
        validarInventario();              // invariantes de las capas (ADR-023)
        validarBalancePedidos();          // C-01: el pedido cierra por partes (ADR-055)
        validarBalanceProducido();        // C-02: nada de lo producido se pierde (ADR-048)
        reconciliarCapacidad();           // C-03: la capacidad reservada reconcilia (ADR-060)
        reconciliarFlotaProducto();       // C-04: la agenda de camiones es posible (ADR-061)
        reconciliarEnviosEnCurso();       // C-05: ningun bloque del flujo retiene envios (ADR-063)
        reconciliarCostos();              // los totales explican cargo por cargo (ADR-052)
        tomarSnapshotInventarioDelDia();  // C-12: el balance de cada nodo cierra (ADR-064)
        exportarCapacidadSiCorresponde(); // diagnostico de capacidad al cierre (ADR-060)
        actualizarRedVisual();            // vista de red (ADR-072): refresco de presentacion
    }

    // evento pasoAnimacion [timeout cyclic] cada pasoAnimacionDias day
    void pasoAnimacion_accion() {
        // Paso de animacion (ADR-073). Es el unico evento del modelo que no decide nada: mueve las
        // figuras del tramo con el reloj del motor y refresca el panel de estado. Con animacionRed
        // = false no hace nada y la corrida decide igual.
        moverFigurasRedVisual();
    }
}
