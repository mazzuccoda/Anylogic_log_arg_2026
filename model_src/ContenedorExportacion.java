// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class ContenedorExportacion extends Agent {

    // ----- Parámetros -----
    String idContenedor = "";
    Pedido Pedido = null;
    LoteProducto lote = null;
    TipoProducto producto = TipoProducto.ACEITE;
    String material = "";
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
    String claveReservaCapacidad = "";
    int diaPlanificadoOperacion = -1;
    String idUbicacionOperacion = "";
    String tipoRecursoOperacion = "";

    // ----- Codigo de arranque (StartupCode) -----
    void onStartup() {
        // Red de seguridad para un contenedor creado sin capacidad. El material es parte
        // de la identidad del producto (ADR-067) y en el startup todavia es el default,
        // asi que solo se puede cotizar cuando ya vino con material (ADR-069): quien crea
        // el contenedor le pasa la capacidad del pedido inmediatamente despues.
        if (capacidadTon <= 0 && main != null && material != null && !material.isEmpty()) {
            capacidadTon = main.obtenerCapacidadContenedorTon(producto, material);
        }
    }
}
