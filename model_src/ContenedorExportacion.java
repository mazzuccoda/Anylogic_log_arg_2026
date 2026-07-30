// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class ContenedorExportacion extends Agent {

    // ----- Parámetros -----
    String idContenedor = "";
    Pedido Pedido = null;
    LoteProducto lote = null;
    TipoProducto producto = TipoProducto.ACEITE;
    TipoContenedor tipoContenedor = TipoContenedor.REEFER_40;
    double cantidadAsignadaTon = 0;
    double capacidadTon = 0;
    Terminal terminalDestino = null;
    EstadoContenedor estado = EstadoContenedor.CREADO;
    Agent lugarConsolidacion = null;
    Camion camionPortacontenedor = null;
    double horaRetiroVacio = -1;
    double horaLlegadaLugarCarga = -1;
    double horaInicioCarga = -1;
    double horaFinCarga = -1;
    double horaIngresoTerminal = -1;
    double costoEstimado = 0;
    double diaProgramadoCrossDock = -1;
    boolean esCrossDock = false;
    double costoReal = 0;

    // ----- Variables -----
    int diasEsperaPosicion = 0;
    String idAsignacionPedido = "";
    String claveReserva = "";
    String idSitioOrigen = "";
    EstrategiaLogistica circuito = EstrategiaLogistica.SIN_DEFINIR;
}
