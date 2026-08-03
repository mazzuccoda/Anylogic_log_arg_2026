// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Una posicion comprometida en un sitio para un dia (ADR-060). La capacidad futura deja
 * de ser un diagnostico y pasa a ser un compromiso: se reserva al asignar, la toma un
 * contenedor al crearse y se consume al ejecutar la operacion.
 *
 * Es un dato y no un agente: PLE admite 10 tipos y el modelo ya los usa (ADR-030),
 * igual que Capa, AlternativaCircuito y AsignacionPedido.
 */
public class ReservaCapacidad implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	/** Estiba de un contenedor: planta, deposito o terminal. */
	public static final String CONSOLIDACION = "CONSOLIDACION";

	/** Cruce en deposito: cupo propio, no consume consolidacion (ADR-041). */
	public static final String CROSS_DOCK = "CROSS_DOCK";

	public String claveReserva = "";
	public String codigoPedido = "";
	public String claveAsignacion = "";
	public String tipoRecurso = CONSOLIDACION;
	public String idUbicacion = "";

	/** Dia en el que la posicion esta comprometida. Puede moverse dentro de la ventana. */
	public int diaPlanificado = 0;

	/** Dia en el que se reservo por primera vez: sirve para medir reprogramaciones. */
	public int diaOriginal = 0;

	/** Ultimo dia util de la ventana del pedido: mas alla no se reprograma. */
	public int diaLimiteVentana = 0;

	public int reprogramaciones = 0;

	/** Contenedor que tomo la posicion. Vacio mientras la reserva esta libre. */
	public String idContenedor = "";

	public boolean activa = true;
	public boolean consumida = false;
	public boolean liberada = false;
	public String motivoBaja = "";

	public ReservaCapacidad(String claveReserva, String codigoPedido, String claveAsignacion,
			String tipoRecurso, String idUbicacion, int diaPlanificado, int diaLimiteVentana) {
		this.claveReserva = claveReserva;
		this.codigoPedido = codigoPedido;
		this.claveAsignacion = claveAsignacion;
		this.tipoRecurso = tipoRecurso;
		this.idUbicacion = idUbicacion;
		this.diaPlanificado = diaPlanificado;
		this.diaOriginal = diaPlanificado;
		this.diaLimiteVentana = diaLimiteVentana;
	}

	/** Clave del recurso: es la unidad de capacidad, tipo mas sitio. */
	public String claveRecurso() {
		return tipoRecurso + "|" + idUbicacion;
	}

	/** Ocupa el dia mientras no se libere: consumir no libera, ejecuta lo comprometido. */
	public boolean ocupa() {
		return !liberada;
	}

	/** Libre para que un contenedor la tome. */
	public boolean disponible() {
		return activa && !consumida && !liberada && idContenedor.isEmpty();
	}

	public String descripcion() {
		return claveReserva + " " + codigoPedido + " " + claveAsignacion
				+ " " + tipoRecurso + " en " + idUbicacion
				+ " dia " + diaPlanificado
				+ (consumida ? " | consumida" : (liberada ? " | liberada: " + motivoBaja : " | activa"))
				+ (idContenedor.isEmpty() ? "" : " | " + idContenedor);
	}
}
