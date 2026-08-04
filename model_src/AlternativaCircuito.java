// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Una alternativa de circuito para un pedido: de donde sale el producto, donde
 * se arma el contenedor, si llega a tiempo y cuanto cuesta (ADR-054).
 *
 * Es un dato y no un agente: PLE admite 10 tipos y el modelo ya los usa
 * (ADR-030). El plan seleccionado si se materializa como PlanLogistico, que es
 * el agente que el proyecto ya tenia reservado para eso.
 */
public class AlternativaCircuito implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public String idOrigen = "";              // sitio del que sale el producto
	public String sitioEstiba = "";           // donde se arma el contenedor
	public EstrategiaLogistica circuito = EstrategiaLogistica.SIN_DEFINIR;
	public boolean esCrossDock = false;

	public double toneladas = 0;
	public int contenedores = 0;

	public boolean factible = false;

	// Capacidad finita (ADR-060): una alternativa vale por lo que puede procesar antes
	// del cut-off, no por su costo. El recurso es la unidad de capacidad que consume.
	/** Identidad de la alternativa dentro de la ronda de decision (ADR-064). */
	public String idAlternativa = "";

	/**
	 * Motivo normalizado del descarte. El texto libre queda en motivoNoFactible: el codigo
	 * es para agrupar y el detalle para leer, y ninguno de los dos reemplaza al otro.
	 */
	public String codigoMotivo = "";

	public static final String TRANSFERENCIA_DEPOSITO_DEPOSITO = "TRANSFERENCIA_DEPOSITO_DEPOSITO";
	public static final String SIN_CAPACIDAD_ANTES_CUTOFF = "SIN_CAPACIDAD_ANTES_CUTOFF";
	public static final String CAPACIDAD_ESTIBA_CERO = "CAPACIDAD_ESTIBA_CERO";
	public static final String SIN_FLOTA_ANTES_CUTOFF = "SIN_FLOTA_ANTES_CUTOFF";
	public static final String SIN_FLOTA_PLANTA_DEPOSITO = "SIN_FLOTA_PLANTA_DEPOSITO";
	public static final String SIN_FLOTA_GRANEL_TERMINAL = "SIN_FLOTA_GRANEL_TERMINAL";
	public static final String SIN_STOCK = "SIN_STOCK";
	public static final String SIN_STOCK_PLANTA_PARA_CRUZAR = "SIN_STOCK_PLANTA_PARA_CRUZAR";
	public static final String SIN_STOCK_ESPACIO_O_CUPO = "SIN_STOCK_ESPACIO_O_CUPO";
	public static final String CROSS_DOCK_DESHABILITADO = "CROSS_DOCK_DESHABILITADO";
	public static final String ORIGEN_NO_HABILITADO = "ORIGEN_NO_HABILITADO";
	public static final String SIN_CUPO_CROSS_DOCK = "SIN_CUPO_CROSS_DOCK";
	public static final String SIN_ESPACIO_DE_PASO = "SIN_ESPACIO_DE_PASO";

	/** No es una restriccion del sitio: otro pedido del mismo dia se llevo lo que faltaba. */
	public static final String NO_TOMADA_AL_EJECUTAR = "NO_TOMADA_AL_EJECUTAR";

	public String tipoRecursoCapacidad = ReservaCapacidad.CONSOLIDACION;
	public String idUbicacionCapacidad = "";
	public int contenedoresConCapacidad = 0;

	/** Si el sitio tiene al menos una posicion libre en la ventana del pedido. */
	public boolean capacidadReservable = true;
	public double toneladasCapacidadDisponible = 0;

	/** Lo que el origen podria dar si la capacidad no existiera: mide la saturacion. */
	public double toneladasSinRestriccionCapacidad = 0;

	/** Costo unitario que tendria sin la restriccion de capacidad (KPI de saturacion). */
	public double costoUnitarioSinRestriccion = Double.POSITIVE_INFINITY;

	public java.util.List<Integer> diasCapacidadDisponibles = new java.util.ArrayList<Integer>();

	// Flota multidiaria (ADR-061): el camion de producto se compromete por la duracion
	// real del viaje, asi que la alternativa vale por lo que la agenda puede mover antes
	// del cut-off, no por lo que entra en una jornada.
	public boolean requiereFlotaProducto = false;
	public double toneladasFactiblesPorFlota = 0;
	public int viajesFactiblesPorFlota = 0;
	public double primeraSalidaProducto = -1;
	public double ultimaSalidaProducto = -1;
	public double ultimaLlegadaProducto = -1;
	public double ultimoRegresoProducto = -1;
	public double esperaFlotaDias = 0;
	public boolean flotaCompleta = true;
	public boolean flotaParcial = false;
	public String diagnosticoFlota = "";
	public String motivoNoFactible = "";

	public double diaEntregaEstimado = 0;
	public boolean llegaATiempo = false;

	// Descomposicion del incremental: el evaluador no compara un numero opaco, y
	// cada componente es el mismo concepto que despues se devenga en el registro.
	public double costoFleteProducto = 0;
	public double costoRoundTrip = 0;
	public double costoEstiba = 0;            // consolidacion o cross dock, segun el circuito
	public double costoOut = 0;
	public double costoTHC = 0;
	public double costoTerminal = 0;
	public double costoDespachante = 0;

	// Descomposicion del hundido: lo que el stock ya pago para estar donde esta.
	public double costoInHundido = 0;
	public double costoAlmacenajeHundido = 0;
	public double costoFleteHundido = 0;

	/** Costo hundido del stock que consumiria: no debe decidir nada (seccion 7.1). */
	public double costoHistorico = 0;

	/** Lo que agrega la decision desde hoy: la vista tactica (seccion 7.2). */
	public double costoIncremental = 0;

	/** Historico mas incremental: la vista estrategica (seccion 7.3). */
	public double costoEndToEnd = 0;

	public AlternativaCircuito(String idOrigen, String sitioEstiba, EstrategiaLogistica circuito,
			boolean esCrossDock, double toneladas, int contenedores) {
		this.idOrigen = idOrigen;
		this.sitioEstiba = sitioEstiba;
		this.circuito = circuito;
		this.esCrossDock = esCrossDock;
		this.toneladas = toneladas;
		this.contenedores = contenedores;
	}

	/** Suma las componentes: el total no se carga aparte para que no puedan diferir. */
	public void totalizar() {
		costoIncremental = costoFleteProducto + costoRoundTrip + costoEstiba + costoOut
			+ costoTHC + costoTerminal + costoDespachante;
		costoHistorico = costoInHundido + costoAlmacenajeHundido + costoFleteHundido;
		costoEndToEnd = costoHistorico + costoIncremental;
	}

	/**
	 * Descarta con codigo y detalle. No hay version de un solo argumento a proposito: un
	 * descarte sin codigo no se puede agrupar y la tabla de motivos deja de servir.
	 */
	public void descartar(String codigo, String detalle) {
		factible = false;
		codigoMotivo = codigo;
		motivoNoFactible = detalle;
	}

	public double costoSegun(boolean endToEnd) {
		return endToEnd ? costoEndToEnd : costoIncremental;
	}

	/**
	 * Costo por tonelada. Con asignacion parcial dos alternativas pueden ofrecer volumenes
	 * distintos, y comparar totales premiaria a la que menos toneladas resuelve (ADR-055).
	 */
	public double costoUnitarioSegun(boolean endToEnd) {
		return toneladas <= 0.0001
			? Double.POSITIVE_INFINITY
			: costoSegun(endToEnd) / toneladas;
	}

	/** Clave estable para desempatar: dos alternativas iguales no dependen del orden. */
	public String clave() {
		return circuito + "|" + idOrigen + "|" + sitioEstiba;
	}

	public String descripcion() {
		return circuito + " desde " + idOrigen
			+ " (estiba en " + sitioEstiba + ")"
			+ " | incremental USD " + Math.round(costoIncremental)
			+ " | end-to-end USD " + Math.round(costoEndToEnd)
			+ (factible ? (llegaATiempo ? " | a tiempo" : " | tarde") : " | descartada: " + motivoNoFactible);
	}
}
