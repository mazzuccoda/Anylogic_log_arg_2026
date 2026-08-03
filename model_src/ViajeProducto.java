// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Un viaje fisico de un camion de producto (ADR-061): una instancia por camion y por viaje,
 * nunca una que represente varios camiones a la vez.
 *
 * El viaje separa cinco momentos que antes ocurrian juntos: reserva del stock, salida,
 * transito, llegada a destino y regreso del camion. El producto sale del origen al salir el
 * viaje y aparece en destino al llegar, no al programarlo.
 */
public class ViajeProducto implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final double EPS = 0.0001;

	// Motivos de descarte y de diagnostico. Un solo texto por concepto para que el
	// diagnostico se pueda agrupar (seccion 27 del MOD).
	public static final String SIN_CAMIONES_CONFIGURADOS = "SIN_CAMIONES_CONFIGURADOS";
	public static final String SIN_FLOTA_ANTES_CUTOFF = "SIN_FLOTA_ANTES_CUTOFF";
	public static final String FLOTA_PARCIAL = "FLOTA_PARCIAL";
	public static final String ESPERA_FLOTA = "ESPERA_FLOTA";
	public static final String VIAJE_FUERA_HORIZONTE = "VIAJE_FUERA_HORIZONTE";
	public static final String STOCK_NO_RESERVABLE = "STOCK_NO_RESERVABLE";
	public static final String RUTA_SIN_DISTANCIA = "RUTA_SIN_DISTANCIA";
	public static final String DURACION_RUTA_INVALIDA = "DURACION_RUTA_INVALIDA";
	public static final String CRUCE_SIN_LLEGADA_EN_EL_DIA = "CRUCE_SIN_LLEGADA_EN_EL_DIA";

	public String idViaje = "";
	public int idCamion = 0;

	/** Operacion que lo pidio: transferencia, cruce o granel. Sirve para la trazabilidad. */
	public String idOperacion = "";

	public int idLote = 0;
	public String codigoPedido = "";
	public TipoProducto producto = null;
	public String origen = "";
	public String destino = "";
	public double toneladas = 0;

	/** Dia de produccion de la capa que viaja: la antiguedad del lote no se pierde en ruta. */
	public double diaProduccionLote = 0;

	public double diaProgramacion = -1;
	public double diaSolicitud = -1;
	public double diaSalida = -1;
	public double diaLlegadaDestino = -1;
	public double diaInicioRetorno = -1;
	public double diaRegreso = -1;

	public double duracionIdaDias = 0;
	public double duracionRetornoDias = 0;
	public double distanciaKmIda = 0;

	public EstadoViajeProducto estado = EstadoViajeProducto.PROGRAMADO;
	public EstrategiaLogistica estrategia = EstrategiaLogistica.SIN_DEFINIR;
	public boolean crossDock = false;

	/**
	 * El viaje solo ocupa el camion: el producto lo mueve el flujo del envio, que ya modela
	 * el ciclo fisico con sus delays (circuito 4, granel a terminal). Sin esto el modelo
	 * tendria dos duraciones y dos movimientos para el mismo viaje.
	 */
	public boolean ocupaSoloFlota = false;

	public boolean stockRetiradoOrigen = false;
	public boolean stockIngresadoDestino = false;
	public boolean fleteRegistrado = false;
	public String motivoBaja = "";

	/** Clave con la que el viaje reserva su stock en las capas del origen (ADR-023). */
	public String claveReservaStock() {
		return "VIAJE|" + idViaje;
	}

	public double esperaFlotaDias() {
		return diaSolicitud < 0 || diaSalida < 0
				? 0
				: Math.max(0, diaSalida - diaSolicitud);
	}

	public double camionDiaOcupado() {
		return diaSalida < 0 || diaRegreso < 0
				? 0
				: Math.max(0, diaRegreso - diaSalida);
	}

	public boolean enTransito() {
		return estado == EstadoViajeProducto.EN_TRANSITO_DESTINO;
	}

	public boolean vivo() {
		return estado != EstadoViajeProducto.COMPLETADO
				&& estado != EstadoViajeProducto.CANCELADO;
	}

	public String descripcion() {
		return idViaje + " camion " + idCamion + " " + origen + "->" + destino
				+ " " + Math.round(toneladas * 100) / 100.0 + " tn"
				+ " salida " + Math.round(diaSalida * 100) / 100.0
				+ " llegada " + Math.round(diaLlegadaDestino * 100) / 100.0
				+ " regreso " + Math.round(diaRegreso * 100) / 100.0
				+ " | " + estado;
	}
}
