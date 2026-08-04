// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Capa de auditoria de la red (ADR-064): escribe los hechos de la corrida a csv.
 *
 * Escribe en streaming y no acumula filas en memoria: una campania genera decenas de
 * miles de decisiones y cientos de miles de cargos, y el barrido corre mil corridas en
 * la misma JVM. Guardarlas para exportarlas al cierre no entra en memoria y una corrida
 * interrumpida no dejaria evidencia.
 *
 * No es un tipo de agente: PLE admite 10 y el modelo ya los usa (ADR-030).
 */
public class AuditoriaRed implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Cuanto se registra. La auditoria completa multiplica por decenas el tamano de la
	 * salida de una corrida, asi que el default del barrido es DESACTIVADA y COMPLETA es
	 * para la corrida puntual que se audita.
	 */
	public enum Nivel {
		DESACTIVADA,
		RESUMIDA,
		COMPLETA
	}

	public static final String DECISIONES = "decisiones_alternativas";
	public static final String ASIGNACIONES = "asignaciones_elegidas";
	public static final String ARCOS = "ejecucion_arcos";
	public static final String COSTOS = "costos_eventos";
	public static final String INVENTARIO = "snapshot_inventario";
	public static final String CAPACIDAD = "snapshot_capacidad_recursos";

	/**
	 * Tablas ya abiertas por esta JVM. La primera corrida escribe el encabezado y las
	 * siguientes anexan: asi el barrido deja un solo archivo por tabla con todas las
	 * corridas identificadas por run_id, sin que nadie tenga que declarar si es barrido.
	 */
	private static final java.util.Set<String> abiertas =
		new java.util.HashSet<String>();

	public Nivel nivel = Nivel.DESACTIVADA;

	/** Identidad de la corrida: sin esto dos corridas colisionan en la misma tabla. */
	public String runId = "";

	public String directorio = "resultados";

	/** Filas escritas por tabla: es el contador que reconcilian C-06 y C-09. */
	public final java.util.LinkedHashMap<String, Long> filas =
		new java.util.LinkedHashMap<String, Long>();

	private transient java.util.LinkedHashMap<String, java.io.PrintWriter> salidas =
		new java.util.LinkedHashMap<String, java.io.PrintWriter>();

	public boolean activa() {
		return nivel != Nivel.DESACTIVADA;
	}

	/** Las alternativas descartadas son el grueso del volumen: solo en COMPLETA. */
	public boolean registraDescartadas() {
		return nivel == Nivel.COMPLETA;
	}

	public void abrir(String tabla, String encabezado) {
		if (!activa()) {
			return;
		}

		if (salidas == null) {
			salidas = new java.util.LinkedHashMap<String, java.io.PrintWriter>();
		}

		if (salidas.containsKey(tabla)) {
			return;
		}

		String ruta = directorio + "/" + tabla + ".csv";

		boolean anexa = abiertas.contains(ruta);

		try {
			java.io.PrintWriter salida =
				new java.io.PrintWriter(
					new java.io.BufferedWriter(
						new java.io.OutputStreamWriter(
							new java.io.FileOutputStream(ruta, anexa), "UTF-8"),
						1 << 16));

			if (!anexa) {
				salida.println(encabezado);
				abiertas.add(ruta);
			}

			salidas.put(tabla, salida);

			if (!filas.containsKey(tabla)) {
				filas.put(tabla, 0L);
			}

		} catch (java.io.IOException e) {
			throw new RuntimeException(
				"No se pudo abrir " + ruta + " para la auditoria de red: " + e.getMessage());
		}
	}

	public void escribir(String tabla, String fila) {
		if (!activa()) {
			return;
		}

		java.io.PrintWriter salida = salidas == null ? null : salidas.get(tabla);

		if (salida == null) {
			throw new RuntimeException(
				"La tabla de auditoria " + tabla + " no fue abierta: el encabezado y las filas"
				+ " se escriben desde el mismo lugar (ADR-064).");
		}

		salida.println(fila);

		filas.put(tabla, filasDe(tabla) + 1);
	}

	public long filasDe(String tabla) {
		Long cantidad = filas.get(tabla);

		return cantidad == null ? 0L : cantidad.longValue();
	}

	public void cerrar() {
		if (salidas == null) {
			return;
		}

		for (java.io.PrintWriter salida : salidas.values()) {
			salida.flush();
			salida.close();
		}

		salidas.clear();
	}

	public String resumen() {
		String texto = "Auditoria de red " + nivel + " | run " + runId;

		for (String tabla : filas.keySet()) {
			texto += "\n  " + tabla + " " + filasDe(tabla);
		}

		return texto;
	}

	// ------------------------------------------------------------------ formato csv

	/** Texto escapado: una coma, una comilla o un salto de linea no rompen la tabla. */
	public static String txt(String valor) {
		if (valor == null || valor.isEmpty()) {
			return "";
		}

		String limpio = valor.replace("\r", " ").replace("\n", " ");

		return limpio.indexOf(',') >= 0 || limpio.indexOf('"') >= 0
			? "\"" + limpio.replace("\"", "\"\"") + "\""
			: limpio;
	}

	/** Numero con punto decimal y cuatro decimales; vacio si no es un numero finito. */
	public static String num(double valor) {
		if (Double.isNaN(valor) || Double.isInfinite(valor)) {
			return "";
		}

		return String.format(java.util.Locale.US, "%.4f", valor);
	}

	public static String ent(long valor) {
		return Long.toString(valor);
	}

	public static String si(boolean valor) {
		return valor ? "true" : "false";
	}
}
