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

	/**
	 * Decision y alternativa que crearon esta asignacion (ADR-064). Es la unica copia de la
	 * identidad de la decision en el mundo fisico: el contenedor y el envio ya llevan
	 * idAsignacionPedido, asi que repetirla en ellos seria una copia que puede quedar vieja.
	 */
	public String idDecision = "";
	public String idAlternativa = "";

	public TipoProducto producto = TipoProducto.JUGO;

	/** Subdivision del producto (ADR-067), heredada del pedido que origina la asignacion. */
	public String material = "";

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

	public static String encabezadoCsv() {
		return "run_id,escenario,replica,id_asignacion,id_decision,id_alternativa,"
				+ "codigo_pedido,producto,material,origen,circuito,es_cross_dock,prioridad,"
				+ "dia_asignacion,dia_primer_despacho,dia_ultima_entrega,"
				+ "toneladas_asignadas,toneladas_reservadas_activas,toneladas_contenerizadas,"
				+ "toneladas_despachadas,toneladas_entregadas,"
				+ "contenedores_creados,contenedores_entregados,"
				+ "costo_incremental_estimado,costo_end_to_end_estimado,"
				+ "costo_real_contenedores_usd,desvio_costo_usd,"
				+ "dias_ciclo_real,cerrada,cancelada,motivo_asignacion";
	}

	/**
	 * Fila de asignaciones_elegidas. El costo real es el de los cargos de sus contenedores:
	 * el almacenaje y el flete de guarda se devengan contra el lote y no contra una
	 * asignacion, asi que repartirlos aca seria inventar una atribucion.
	 */
	public String toCsv(String runId, String escenario, int replica, double costoRealContenedores,
			int contenedoresCreados, int contenedoresEntregados) {
		double ciclo =
				diaUltimaEntrega < 0 || diaAsignacion < 0
				? -1
				: diaUltimaEntrega - diaAsignacion;

		return AuditoriaRed.txt(runId) + "," + AuditoriaRed.txt(escenario) + ","
				+ AuditoriaRed.ent(replica) + "," + AuditoriaRed.txt(idAsignacion) + ","
				+ AuditoriaRed.txt(idDecision) + "," + AuditoriaRed.txt(idAlternativa) + ","
				+ AuditoriaRed.txt(codigoPedido) + "," + AuditoriaRed.txt("" + producto) + ","
				+ AuditoriaRed.txt(material) + ","
				+ AuditoriaRed.txt(idSitioOrigen) + "," + AuditoriaRed.txt("" + circuito) + ","
				+ AuditoriaRed.si(esCrossDock) + "," + AuditoriaRed.ent(prioridad) + ","
				+ AuditoriaRed.num(diaAsignacion) + "," + AuditoriaRed.num(diaPrimerDespacho) + ","
				+ AuditoriaRed.num(diaUltimaEntrega) + ","
				+ AuditoriaRed.num(toneladasAsignadas) + ","
				+ AuditoriaRed.num(toneladasReservadasActivas) + ","
				+ AuditoriaRed.num(toneladasContenerizadas) + ","
				+ AuditoriaRed.num(toneladasDespachadas) + ","
				+ AuditoriaRed.num(toneladasEntregadas) + ","
				+ AuditoriaRed.ent(contenedoresCreados) + ","
				+ AuditoriaRed.ent(contenedoresEntregados) + ","
				+ AuditoriaRed.num(costoIncrementalEstimado) + ","
				+ AuditoriaRed.num(costoEndToEndEstimado) + ","
				+ AuditoriaRed.num(costoRealContenedores) + ","
				+ AuditoriaRed.num(costoRealContenedores - costoIncrementalEstimado) + ","
				+ AuditoriaRed.num(ciclo) + "," + AuditoriaRed.si(cerrada) + ","
				+ AuditoriaRed.si(cancelada) + "," + AuditoriaRed.txt(motivoAsignacion);
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
