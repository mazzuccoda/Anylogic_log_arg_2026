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

	public void descartar(String motivo) {
		factible = false;
		motivoNoFactible = motivo;
	}

	public double costoSegun(boolean endToEnd) {
		return endToEnd ? costoEndToEnd : costoIncremental;
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
