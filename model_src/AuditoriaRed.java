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

	/**
	 * Version del esquema de las tablas. Cambia cuando cambia una columna o una clave: el
	 * tablero que consume los csv necesita saber contra que version fue generado.
	 */
	public static final String VERSION_ESQUEMA = "ADR-064.2";

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

	/** El cierre es del final de la corrida y ocurre una sola vez. */
	public boolean cerrada = false;

	/**
	 * Filas emitidas despues del cierre. Al terminar la corrida pueden quedar envios en el
	 * flujo (C-05): su arco es un hecho incompleto y no se escribe, pero se cuenta para que
	 * el resumen no lo tape.
	 */
	public long filasDespuesDelCierre = 0;

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

		if (cerrada) {
			filasDespuesDelCierre++;
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
		cerrada = true;

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

		if (filasDespuesDelCierre > 0) {
			texto +=
				"\n  filas posteriores al cierre (envios todavia en el flujo) "
				+ filasDespuesDelCierre;
		}

		return texto;
	}

	// ------------------------------------------------------------------ calendario

	/**
	 * Ancla del calendario de la corrida (ADR-071): que fecha es el dia 1 de campania.
	 * Vacia significa que el paquete no la declara y las columnas de fecha salen vacias.
	 *
	 * Es estatica porque la escribe el arranque de la corrida y la leen las filas de las
	 * seis tablas, que no conocen al agente. El barrido corre las corridas una despues de
	 * otra y cada una vuelve a anclar antes de escribir su primera fila.
	 */
	private static String inicioCampania = "";

	private static long inicioCampaniaMs = 0;

	public static void anclarCalendario(String fechaIso) {
		inicioCampania = fechaIso == null || !esFechaIso(fechaIso) ? "" : fechaIso.trim();
		inicioCampaniaMs = inicioCampania.isEmpty() ? 0 : medianocheUtc(inicioCampania);
	}

	public static String inicioCampania() {
		return inicioCampania;
	}

	/** Una fecha del contrato es YYYY-MM-DD y ademas existe: 2026-02-30 no es fecha. */
	public static boolean esFechaIso(String texto) {
		if (texto == null) {
			return false;
		}

		String t = texto.trim();

		if (!t.matches("\\d{4}-\\d{2}-\\d{2}")) {
			return false;
		}

		try {
			medianocheUtc(t);
			return true;

		} catch (RuntimeException e) {
			return false;
		}
	}

	/**
	 * Fecha calendario del dia de campania de una fila: el dia 1 es el inicio de campania.
	 *
	 * El dia se pisa al entero porque el reloj del modelo es continuo y la fila publica una
	 * fecha, no un instante. Un dia negativo es el centinela de "no aplica" (un despacho que
	 * nunca ocurrio) y no tiene fecha: publicar una la inventaria.
	 */
	public static String fecha(double dia) {
		if (inicioCampania.isEmpty() || dia < 0) {
			return "";
		}

		java.util.GregorianCalendar c = calendarioUtc();
		c.setTimeInMillis(inicioCampaniaMs + (long) (Math.floor(dia) - 1) * 86400000L);

		return String.format(
			java.util.Locale.US, "%04d-%02d-%02d",
			c.get(java.util.Calendar.YEAR),
			c.get(java.util.Calendar.MONTH) + 1,
			c.get(java.util.Calendar.DAY_OF_MONTH));
	}

	/**
	 * Medianoche UTC de una fecha del contrato. El calendario es UTC y no el del sistema:
	 * con el huso local la misma corrida daria una fecha distinta segun donde se abra.
	 */
	private static long medianocheUtc(String fechaIso) {
		java.util.GregorianCalendar c = calendarioUtc();
		c.setLenient(false);

		c.set(
			Integer.parseInt(fechaIso.substring(0, 4)),
			Integer.parseInt(fechaIso.substring(5, 7)) - 1,
			Integer.parseInt(fechaIso.substring(8, 10)));

		return c.getTimeInMillis();
	}

	private static java.util.GregorianCalendar calendarioUtc() {
		java.util.GregorianCalendar c =
			new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("UTC"));

		c.clear();
		return c;
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
