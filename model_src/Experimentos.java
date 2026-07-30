// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

// Experimentos del modelo

class Simulation extends SimulationExperiment {
}

class Escenarios extends ParamVariationExperiment {
    // corridas: 1080
    // parámetro #1786000001 = (getCurrentIteration() - 1) % REPLICAS
    // parámetro #1791000000003 = GeneradorSintetico.ESCENARIOS[(getCurrentIteration() - 1) / REPLICAS]

    // código de clase adicional
    void additionalClassCode() {
        // Version del modelo con la que se corrio el barrido: sin esto un csv de
        // resultados no se puede volver a atar al codigo que lo produjo.
        static final String VERSION_MODELO = "fase-25";

        static final int REPLICAS = 30;

        /** Una corrida del barrido: su identidad y sus KPIs de cierre. */
        static class Corrida {
        	String idEscenario;
        	String config;
        	int replica;
        	long semilla;
        	double[] kpis;
        }

        static final String[] KPIS = {
        	"costo_total_usd", "costo_usd_tn", "nivel_servicio", "atraso_promedio_dias",
        	"utilizacion_flota", "utilizacion_portacontenedor", "viajes_planta_deposito",
        	"uso_posiciones_consolidacion", "toneladas_exportadas",
        	"excedente_final_tn", "toneladas_cross_dock", "contenedores_exportados",
        	"costo_oportunidad_frio_usd", "costo_total_economico_usd", "costo_economico_usd_tn",
        	"ton_dia_sobre_nominal", "dias_sobrecarga", "pico_ocupacion_planta_pct",
        	"contenedores_circuito_planta", "contenedores_circuito_deposito",
        	"contenedores_circuito_cross_dock", "contenedores_circuito_terminal",
        	"viajes_granel_terminal",
        	"costo_flete_producto_usd", "costo_round_trip_usd", "costo_consolidacion_usd",
        	"costo_cross_dock_usd", "costo_almacenamiento_usd", "costo_in_usd",
        	"costo_out_usd", "costo_thc_usd", "costo_terminal_usd", "costo_despachante_usd",
        	"costo_espera_usd",
        	"planes_emitidos", "planes_tardios", "alternativas_evaluadas",
        	"alternativas_descartadas", "pedidos_sin_alternativa_factible",
        	"pedidos_parcialmente_reservados", "pedidos_multi_origen",
        	"pedidos_parcialmente_entregados", "pedidos_atrasados_entrega_parcial",
        	"asignaciones_creadas", "asignaciones_parciales",
        	"toneladas_pendientes_asignar", "toneladas_pendientes_entregar",
        	"toneladas_transferidas_preventivas", "toneladas_transferidas_desborde",
        	"toneladas_transferidas_servicio", "toneladas_transferidas_criticas",
        	"transferencias_incompletas",
        	"stock_inicial_tn", "stock_inicial_consumido_tn", "stock_inicial_remanente_tn",
        	"produccion_campania_tn", "disponibilidad_total_tn", "demanda_planificada_tn",
        	"deficit_estructural_tn"
        };

        // Las corridas se evaluan en serie (con evaluacion paralela el agente raiz no
        // esta disponible al cerrar la corrida), pero la coleccion se sincroniza igual
        // y el orden se impone al escribir, no al llegar.
        final java.util.List<Corrida> corridas =
        	java.util.Collections.synchronizedList(new java.util.ArrayList<Corrida>());

        /** Media, desvio muestral, minimo, maximo y P95 de una muestra. */
        static double[] estadisticos(double[] muestra) {
        	double[] x = muestra.clone();
        	java.util.Arrays.sort(x);

        	double suma = 0;
        	for (double v : x) {
        		suma += v;
        	}
        	double media = suma / x.length;

        	double sc = 0;
        	for (double v : x) {
        		sc += (v - media) * (v - media);
        	}
        	double desvio = x.length > 1 ? Math.sqrt(sc / (x.length - 1)) : 0;

        	int p95 = (int) Math.ceil(0.95 * x.length) - 1;
        	p95 = Math.max(0, Math.min(x.length - 1, p95));

        	return new double[] { media, desvio, x[0], x[x.length - 1], x[p95] };
        }

        /** Muestra de un KPI dentro de un escenario. */
        double[] muestra(String idEscenario, int kpi) {
        	java.util.List<Double> valores = new java.util.ArrayList<Double>();

        	for (Corrida c : corridas) {
        		if (c.idEscenario.equals(idEscenario)) {
        			valores.add(c.kpis[kpi]);
        		}
        	}

        	double[] x = new double[valores.size()];
        	for (int i = 0; i < x.length; i++) {
        		x[i] = valores.get(i);
        	}
        	return x;
        }

        // ------------------------------------------------------------- tablero

        /** Cuantas corridas terminadas tiene un escenario. */
        int corridasDe(String idEscenario) {
        	int n = 0;
        	for (Corrida c : corridas) {
        		if (c.idEscenario.equals(idEscenario)) {
        			n++;
        		}
        	}
        	return n;
        }

        /** Configuracion con la que corrio el escenario, tomada de su primera corrida. */
        String configDe(String idEscenario) {
        	for (Corrida c : corridas) {
        		if (c.idEscenario.equals(idEscenario)) {
        			return c.config;
        		}
        	}
        	return "";
        }

        /** Media de un KPI en un escenario; NaN si todavia no hay corridas. */
        double mediaKpi(String idEscenario, int kpi) {
        	double[] x = muestra(idEscenario, kpi);
        	return x.length == 0 ? Double.NaN : estadisticos(x)[0];
        }

        /** Desvio muestral de un KPI en un escenario. */
        double desvioKpi(String idEscenario, int kpi) {
        	double[] x = muestra(idEscenario, kpi);
        	return x.length == 0 ? Double.NaN : estadisticos(x)[1];
        }

        /** Escenarios que ya tienen al menos una corrida, en el orden de la tabla. */
        java.util.List<String> escenariosConDatos() {
        	java.util.List<String> ids = new java.util.ArrayList<String>();
        	for (String id : GeneradorSintetico.ESCENARIOS) {
        		if (corridasDe(id) > 0) {
        			ids.add(id);
        		}
        	}
        	return ids;
        }

        /** Barra de bloques para comparar magnitudes sin depender de un grafico. */
        String barra(double fraccion, int ancho) {
        	if (Double.isNaN(fraccion)) {
        		return repetir('.', ancho);
        	}
        	int llenos = (int) Math.round(Math.max(0, Math.min(1, fraccion)) * ancho);
        	return repetir('#', llenos) + repetir('.', ancho - llenos);
        }

        String repetir(char c, int n) {
        	StringBuilder s = new StringBuilder();
        	for (int i = 0; i < n; i++) {
        		s.append(c);
        	}
        	return s.toString();
        }

        /** Avance del barrido: iteracion en curso sobre el total planificado. */
        double fraccionCompletada() {
        	int total = REPLICAS * GeneradorSintetico.ESCENARIOS.length;
        	return total == 0 ? 0 : Math.min(1.0, (double) corridas.size() / total);
        }

        String escenarioEnCurso() {
        	int i = (getCurrentIteration() - 1) / REPLICAS;
        	if (i < 0 || i >= GeneradorSintetico.ESCENARIOS.length) {
        		return "-";
        	}
        	return GeneradorSintetico.ESCENARIOS[i];
        }

        /** Tabla comparativa: una fila por escenario, con el delta contra E-00. */
        String tablaEscenarios() {
        	StringBuilder s = new StringBuilder();

        	s.append(String.format(java.util.Locale.US,
        		"%-5s %3s %-24s %12s %9s %8s %7s %7s %7s %7s %9s %8s\n",
        		"esc", "n", "cam  dep   prod  xd cons", "costo USD", "+-costo",
        		"USD/tn", "serv", "atraso", "utFlo", "utPor", "exced tn", "vs E-00"));

        	double costoBase = mediaKpi("E-00", 0);

        	for (String id : escenariosConDatos()) {

        		double costo = mediaKpi(id, 0);
        		double delta = Double.isNaN(costoBase) || costoBase == 0
        			? Double.NaN
        			: 100 * (costo - costoBase) / costoBase;

        		s.append(String.format(java.util.Locale.US,
        			"%-5s %3d %-24s %12.0f %9.0f %8.1f %6.1f%% %7.2f %6.0f%% %6.0f%% %9.0f %7s\n",
        			id,
        			corridasDe(id),
        			configDe(id),
        			costo,
        			desvioKpi(id, 0),
        			mediaKpi(id, 1),
        			100 * mediaKpi(id, 2),
        			mediaKpi(id, 3),
        			100 * mediaKpi(id, 4),
        			100 * mediaKpi(id, 5),
        			mediaKpi(id, 9),
        			Double.isNaN(delta) ? "-" : String.format(java.util.Locale.US, "%+.1f%%", delta)));
        	}

        	if (corridas.isEmpty()) {
        		s.append("sin corridas terminadas todavia");
        	}
        	return s.toString();
        }

        /**
         * Frente de decision: escenario por escenario, barra de nivel de servicio y de
         * costo por tonelada, y si alguna otra configuracion lo domina (mejor servicio
         * y menor costo por tonelada a la vez).
         */
        String frenteDecision() {
        	java.util.List<String> ids = escenariosConDatos();

        	if (ids.isEmpty()) {
        		return "sin corridas terminadas todavia";
        	}

        	double servMin = Double.MAX_VALUE, servMax = -Double.MAX_VALUE;
        	double costoMin = Double.MAX_VALUE, costoMax = -Double.MAX_VALUE;

        	for (String id : ids) {
        		servMin = Math.min(servMin, mediaKpi(id, 2));
        		servMax = Math.max(servMax, mediaKpi(id, 2));
        		costoMin = Math.min(costoMin, mediaKpi(id, 1));
        		costoMax = Math.max(costoMax, mediaKpi(id, 1));
        	}

        	StringBuilder s = new StringBuilder();
        	s.append(String.format("%-5s %-14s %-14s %s\n",
        		"esc", "servicio", "costo por tn", "estado"));

        	for (String id : ids) {

        		double serv = mediaKpi(id, 2);
        		double costoTn = mediaKpi(id, 1);

        		String dominador = "";
        		for (String otro : ids) {
        			if (otro.equals(id)) {
        				continue;
        			}
        			boolean mejorServicio = mediaKpi(otro, 2) >= serv;
        			boolean mejorCosto = mediaKpi(otro, 1) <= costoTn;
        			boolean estricto = mediaKpi(otro, 2) > serv || mediaKpi(otro, 1) < costoTn;

        			if (mejorServicio && mejorCosto && estricto) {
        				dominador = otro;
        				break;
        			}
        		}

        		s.append(String.format(java.util.Locale.US, "%-5s %-14s %-14s %s\n",
        			id,
        			barra(escalar(serv, servMin, servMax), 12),
        			barra(escalar(costoMax + costoMin - costoTn, costoMin, costoMax), 12),
        			dominador.isEmpty() ? "eficiente" : "dominado por " + dominador));
        	}

        	return s.toString();
        }

        /** Posicion de un valor dentro del rango observado en el barrido. */
        double escalar(double valor, double minimo, double maximo) {
        	if (Double.isNaN(valor) || maximo <= minimo) {
        		return 1;
        	}
        	return (valor - minimo) / (maximo - minimo);
        }

        /** Lectura corta: quien gana en servicio, en costo y con la restriccion de servicio. */
        String recomendaciones() {
        	java.util.List<String> ids = escenariosConDatos();

        	if (ids.isEmpty()) {
        		return "";
        	}

        	String mejorServicio = null;
        	String menorCostoTn = null;
        	String menorCostoConServicio = null;

        	for (String id : ids) {

        		if (mejorServicio == null || mediaKpi(id, 2) > mediaKpi(mejorServicio, 2)) {
        			mejorServicio = id;
        		}
        		if (menorCostoTn == null || mediaKpi(id, 1) < mediaKpi(menorCostoTn, 1)) {
        			menorCostoTn = id;
        		}
        		if (mediaKpi(id, 2) >= 0.95
        			&& (menorCostoConServicio == null
        				|| mediaKpi(id, 1) < mediaKpi(menorCostoConServicio, 1))) {
        			menorCostoConServicio = id;
        		}
        	}

        	StringBuilder s = new StringBuilder();

        	s.append(String.format(java.util.Locale.US, "Mejor nivel de servicio: %s (%.1f%%, atraso %.2f dias)\n",
        		mejorServicio, 100 * mediaKpi(mejorServicio, 2), mediaKpi(mejorServicio, 3)));

        	s.append(String.format(java.util.Locale.US, "Menor costo por tonelada: %s (%.1f USD/tn, servicio %.1f%%)\n",
        		menorCostoTn, mediaKpi(menorCostoTn, 1), 100 * mediaKpi(menorCostoTn, 2)));

        	if (menorCostoConServicio == null) {
        		s.append("Ningun escenario alcanza 95% de nivel de servicio");
        	} else {
        		s.append(String.format(java.util.Locale.US,
        			"Mas barato con servicio >= 95%%: %s (%.1f USD/tn)",
        			menorCostoConServicio, mediaKpi(menorCostoConServicio, 1)));
        	}

        	return s.toString();
        }
    }

    // al preparar el experimento
    void initialSetupCode() {
        corridas.clear();
    }

    // después de la corrida
    void afterSimulationRunCode() {
        // Antes de leer un KPI de costo, el registro tiene que cerrar contra los
        // acumuladores del modelo (ADR-052).
        root.reconciliarCostos();

        // Una fila por corrida, con la identidad que permite reproducirla.
        Corrida c = new Corrida();

        c.idEscenario = root.idEscenario;
        c.config = String.format(java.util.Locale.US, "%d/%d  x%.2f  x%.2f  %-3s %s  %s",
        	root.datos.escenario.camionesProducto,
        	root.datos.escenario.camionesPortacontenedor,
        	root.datos.escenario.factorCapacidadDeposito,
        	root.datos.escenario.factorProduccion,
        	root.datos.escenario.habilitaCrossDock ? "si" : "no",
        	"CONSOLIDACION_TERMINAL".equals(root.datos.escenario.estrategiaConsolidacion)
        		? "term"
        		: ("CONSOLIDACION_PLANTA".equals(root.datos.escenario.estrategiaConsolidacion)
        			? "planta" : "dep"),
        	root.datos.escenario.politicaSeleccion);
        c.replica = root.replica;
        c.semilla = root.semillaBase + root.replica;

        c.kpis = new double[] {
        	root.costoTotalCampania(),
        	root.costoPorToneladaExportada(),
        	root.nivelServicio(),
        	root.atrasoPromedioDias(),
        	root.utilizacionFlota(),
        	root.utilizacionPortacontenedor(),
        	root.viajesPlantaDeposito,
        	root.usoPosicionesConsolidacion(),
        	root.toneladasExportadas(),
        	root.excedenteFinalTn(),
        	root.toneladasCrossDock,
        	root.contarContenedores(EstadoContenedor.EXPORTADO),
        	root.costoOportunidadFrio,
        	root.costoTotalEconomico(),
        	root.costoEconomicoPorTonelada(),
        	root.tonDiaSobreNominalPlanta,
        	root.diasSobrecargaPlanta,
        	root.picoOcupacionPlantaPct,
        	root.contenedoresCircuitoPlanta,
        	root.contenedoresCircuitoDeposito,
        	root.contenedoresCircuitoCrossDock,
        	root.contenedoresCircuitoTerminal,
        	root.viajesGranelTerminal,
        	root.registro.total(RegistroCostos.Categoria.FLETE_PRODUCTO),
        	root.registro.total(RegistroCostos.Categoria.ROUND_TRIP),
        	root.registro.total(RegistroCostos.Categoria.CONSOLIDACION),
        	root.registro.total(RegistroCostos.Categoria.CROSS_DOCK),
        	root.registro.total(RegistroCostos.Categoria.ALMACENAMIENTO),
        	root.registro.total(RegistroCostos.Categoria.IN_DEPOSITO),
        	root.registro.total(RegistroCostos.Categoria.OUT_DEPOSITO),
        	root.registro.total(RegistroCostos.Categoria.THC),
        	root.registro.total(RegistroCostos.Categoria.COSTO_TERMINAL),
        	root.registro.total(RegistroCostos.Categoria.DESPACHANTE),
        	root.registro.total(RegistroCostos.Categoria.ESPERA_CAMION_PRODUCTO)
        		+ root.registro.total(RegistroCostos.Categoria.ESPERA_PORTACONTENEDOR),
        	root.planesEmitidos,
        	root.planesTardios,
        	root.alternativasEvaluadasTotal,
        	root.alternativasDescartadasTotal,
        	root.pedidosSinAlternativaFactible,
        	root.pedidosParcialmenteReservados(),
        	root.pedidosMultiOrigen,
        	root.pedidosParcialmenteEntregados(),
        	root.pedidosAtrasadosConEntregaParcial,
        	root.asignacionesCreadas,
        	root.asignacionesParciales,
        	root.toneladasPendientesAsignarTotal(),
        	root.toneladasPendientesEntregarTotal(),
        	root.toneladasTransferidasPreventivas,
        	root.toneladasTransferidasDesborde,
        	root.toneladasTransferidasServicio,
        	root.toneladasTransferidasCriticas,
        	root.transferenciasIncompletas,
        	root.stockInicialCargadoTn,
        	root.stockInicialConsumidoTn(),
        	root.stockInicialRemanenteTn(),
        	root.produccionCampaniaTn(),
        	root.disponibilidadTotalTn(),
        	root.demandaPlanificadaTn(),
        	root.deficitEstructuralTn()
        };

        corridas.add(c);
    }

    // al terminar el experimento
    void afterExperimentCode() {
        // Detalle por corrida: es la evidencia, y sin escenario, replica, semilla y
        // version no se puede volver a producir.
        try {

        	java.io.File carpeta = new java.io.File("resultados");
        	carpeta.mkdirs();

        	java.io.PrintWriter csv = new java.io.PrintWriter(
        		new java.io.File(carpeta, "kpis_por_corrida.csv"), "UTF-8");

        	StringBuilder cabecera = new StringBuilder(
        		"version_modelo,id_escenario,replica,semilla");
        	for (String kpi : KPIS) {
        		cabecera.append(",").append(kpi);
        	}
        	csv.println(cabecera);

        	for (String id : GeneradorSintetico.ESCENARIOS) {
        		for (Corrida c : corridas) {

        			if (!c.idEscenario.equals(id)) {
        				continue;
        			}

        			StringBuilder fila = new StringBuilder();
        			fila.append(VERSION_MODELO).append(",")
        				.append(c.idEscenario).append(",")
        				.append(c.replica).append(",")
        				.append(c.semilla);

        			for (double v : c.kpis) {
        				fila.append(",").append(String.format(java.util.Locale.US, "%.4f", v));
        			}
        			csv.println(fila);
        		}
        	}

        	csv.close();

        } catch (java.io.IOException e) {
        	traceln("No se pudo escribir resultados/kpis_por_corrida.csv: " + e.getMessage());
        }

        // Resumen por escenario y comparacion contra el caso base: la pregunta del
        // dimensionamiento no es cuanto dio una corrida sino cuanto cambia el escenario.
        traceln("version_modelo=" + VERSION_MODELO + "  replicas=" + REPLICAS
        	+ "  corridas=" + corridas.size());

        for (int k = 0; k < KPIS.length; k++) {

        	traceln("");
        	traceln(KPIS[k]);
        	traceln(String.format(java.util.Locale.US,
        		"%-6s %12s %12s %12s %12s %12s %12s %9s",
        		"esc", "media", "desvio", "min", "max", "p95", "delta", "delta_%"));

        	double[] base = estadisticos(muestra("E-00", k));

        	for (String id : GeneradorSintetico.ESCENARIOS) {

        		double[] x = muestra(id, k);

        		if (x.length == 0) {
        			continue;
        		}

        		double[] e = estadisticos(x);
        		double delta = e[0] - base[0];
        		double relativo = base[0] == 0 ? 0 : 100 * delta / base[0];

        		traceln(String.format(java.util.Locale.US,
        			"%-6s %12.3f %12.3f %12.3f %12.3f %12.3f %12.3f %8.1f%%",
        			id, e[0], e[1], e[2], e[3], e[4], delta, relativo));
        	}
        }
    }
}
