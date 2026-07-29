// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Una parte de un pedido servida desde un origen por un circuito (ADR-055). Un pedido
 * puede tener varias, creadas en dias distintos, y cada una lleva su propia reserva:
 * la fraccion que ya se pudo comprometer no espera al resto del pedido.
 *
 * Es un dato y no un agente: PLE admite 10 tipos y el modelo ya los usa (ADR-030),
 * igual que Capa y AlternativaCircuito.
 */
public class AsignacionPedido implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final double EPS = 0.0001;

	public String idAsignacion = "";
	public String codigoPedido = "";
	public String idSitioOrigen = "";

	public TipoProducto producto = TipoProducto.JUGO;
	public EstrategiaLogistica circuito = EstrategiaLogistica.SIN_DEFINIR;
	public boolean esCrossDock = false;

	/** Lo comprometido por esta asignacion: reserva viva mas lo ya despachado. */
	public double toneladasAsignadas = 0;

	/** Lo que sigue reservado en las capas del origen y todavia no salio. */
	public double toneladasReservadasActivas = 0;

	/** Lo que ya tiene contenedor creado, despachado o no. */
	public double toneladasContenerizadas = 0;

	public double toneladasDespachadas = 0;
	public double toneladasEntregadas = 0;

	public double diaAsignacion = -1;
	public double diaPrimerDespacho = -1;
	public double diaUltimaEntrega = -1;

	public boolean cerrada = false;
	public boolean cancelada = false;

	public String motivoAsignacion = "";

	public double costoIncrementalEstimado = 0;
	public double costoEndToEndEstimado = 0;

	/** Orden en que se fue armando el pedido: 1 es el primer origen que se consiguio. */
	public int prioridad = 0;

	public AsignacionPedido(String idAsignacion, String codigoPedido, String idSitioOrigen,
			TipoProducto producto, EstrategiaLogistica circuito, boolean esCrossDock,
			double diaAsignacion) {
		this.idAsignacion = idAsignacion;
		this.codigoPedido = codigoPedido;
		this.idSitioOrigen = idSitioOrigen;
		this.producto = producto;
		this.circuito = circuito;
		this.esCrossDock = esCrossDock;
		this.diaAsignacion = diaAsignacion;
	}

	/**
	 * Identidad de la reserva en el inventario. Es la clave que hace posible el despacho
	 * parcial: dos asignaciones del mismo pedido en el mismo sitio no se pisan.
	 */
	public String claveReserva() {
		return codigoPedido + "|" + idAsignacion;
	}

	/** Lo asignado que todavia no tiene contenedor. */
	public double toneladasPorContenerizar() {
		return Math.max(0, toneladasAsignadas - toneladasContenerizadas);
	}

	/** Despachado y no entregado: viajando, sin reserva y sin estar en destino. */
	public double toneladasEnProceso() {
		return Math.max(0, toneladasDespachadas - toneladasEntregadas);
	}

	public boolean activa() {
		return !cancelada && !cerrada;
	}

	/** Se cierra cuando entrego todo lo que habia comprometido. */
	public void cerrarSiCompleta() {
		if (!cancelada && toneladasEntregadas >= toneladasAsignadas - EPS) {
			cerrada = true;
		}
	}

	public String descripcion() {
		return idAsignacion + " " + codigoPedido + " desde " + idSitioOrigen
				+ " por " + circuito
				+ " | asignadas " + toneladasAsignadas
				+ " reserva " + toneladasReservadasActivas
				+ " despachadas " + toneladasDespachadas
				+ " entregadas " + toneladasEntregadas
				+ (cancelada ? " | cancelada" : (cerrada ? " | cerrada" : " | activa"));
	}
}
