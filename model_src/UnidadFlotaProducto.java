// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Un camion de producto (ADR-061). La flota deja de ser capacidad diaria agregada y pasa a
 * ser un conjunto de camiones con una fecha de disponibilidad propia: un viaje mas largo que
 * la jornada ocupa un camion varios dias en lugar de ser imposible.
 *
 * Es un dato y no un agente: PLE admite 10 tipos y el modelo los usa todos (ADR-030), igual
 * que Capa, ReservaCapacidad y AlternativaCircuito.
 */
public class UnidadFlotaProducto implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final double EPS = 0.0001;

	public int idCamion = 0;

	/** Base del camion: vuelve a ella al terminar el viaje, y el ciclo cobrado la incluye. */
	public String ubicacionBase = "PLANTA";
	public String ubicacionActual = "PLANTA";

	/** Viaje que lo tiene ocupado. Vacio cuando esta en base. */
	public String idViajeActual = "";

	/**
	 * Unica fuente de verdad de la disponibilidad: el dia (fraccionario) desde el que el
	 * camion puede volver a salir. Mientras esta en ruta es la fecha de regreso.
	 */
	public double disponibleDesde = 0;

	public boolean activo = true;

	/** Camion-dia efectivamente ocupado: se acumula al completar cada viaje. */
	public double camionDiaAcumulado = 0;
	public int viajesCompletados = 0;

	public UnidadFlotaProducto(int idCamion, String ubicacionBase) {
		this.idCamion = idCamion;
		this.ubicacionBase = ubicacionBase;
		this.ubicacionActual = ubicacionBase;
	}

	public boolean disponibleEn(double dia) {
		return activo && disponibleDesde <= dia + EPS;
	}

	/** Cuando podria salir si se lo pide para una fecha: nunca antes de estar libre. */
	public double salidaMasTemprana(double noAntesDe) {
		return Math.max(noAntesDe, disponibleDesde);
	}

	public boolean enRuta(double dia) {
		return activo && !idViajeActual.isEmpty() && disponibleDesde > dia + EPS;
	}

	public String descripcion() {
		return "camion " + idCamion + " en " + ubicacionActual
				+ " libre desde " + Math.round(disponibleDesde * 100) / 100.0
				+ (idViajeActual.isEmpty() ? "" : " | " + idViajeActual);
	}
}
