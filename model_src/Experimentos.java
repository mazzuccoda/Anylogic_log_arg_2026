// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

// Experimentos del modelo

class Simulation extends SimulationExperiment {
}

class Escenarios extends ParamVariationExperiment {
    // corridas: 360
    // parámetro #1786000001 = (getCurrentIteration() - 1) % REPLICAS
    // parámetro #1791000000003 = GeneradorSintetico.ESCENARIOS[(getCurrentIteration() - 1) / REPLICAS]

    // código de clase adicional
    void additionalClassCode() {
        // Version del modelo con la que se corrio el barrido: sin esto un csv de
        // resultados no se puede volver a atar al codigo que lo produjo.
        static final String VERSION_MODELO = "fase-15";

        static final int REPLICAS = 30;

        /** Una corrida del barrido: su identidad y sus KPIs de cierre. */
        static class Corrida {
        	String idEscenario;
        	int replica;
        	long semilla;
        	double[] kpis;
        }

        static final String[] KPIS = {
        	"costo_total_usd", "costo_usd_tn", "nivel_servicio", "atraso_promedio_dias",
        	"utilizacion_flota", "utilizacion_portacontenedor", "viajes_planta_deposito",
        	"uso_posiciones_consolidacion", "toneladas_exportadas",
        	"excedente_final_tn", "toneladas_cross_dock", "contenedores_exportados"
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
    }

    // al preparar el experimento
    void initialSetupCode() {
        corridas.clear();
    }

    // después de la corrida
    void afterSimulationRunCode() {
        // Una fila por corrida, con la identidad que permite reproducirla.
        Corrida c = new Corrida();

        c.idEscenario = root.idEscenario;
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
        	root.contarContenedores(EstadoContenedor.EXPORTADO)
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
