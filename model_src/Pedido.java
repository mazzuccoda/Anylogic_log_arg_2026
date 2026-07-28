// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Pedido extends Agent {

    // ----- Parámetros -----
    int idPedido = 0;
    String codigoPedido = "";
    TipoProducto producto = TipoProducto.JUGO;
    double toneladasSolicitadas = 0;
    double toneladasReservadas = 0;
    double toneladasEntregadas = 0;
    Terminal puertoSalida = null;
    Deposito depositoAsignado = null;
    double diaLlegada = 0;
    double diaLimite = 0;
    double diaReserva = -1;
    double diaEntrega = -1;
    EstadoPedido estado = EstadoPedido.PENDIENTE;
    double costoFleteEstimado = 0;
    double costoConsolidadoEstimado = 0;
    double costoTotalEstimado = 0;
    double diasAtraso = 0;
    double toneladasDespachadas = 0;
    double cantidadEnvios = 0;
    double enviosEntregados = 0;
    double costoFleteReal = 0;
    double costoConsolidacionReal = 0;
    double costoLogisticoReal = 0;
    boolean enviosGenerados = false;
    LoteProducto loteSolicitado = null;
    Terminal terminalDestino = null;
    Naviera naviera = Naviera.SIN_DEFINIR;
    String incoterm = "";
    double fechaLimiteTerminal = -1;
    EstrategiaLogistica estrategiaSeleccionada = EstrategiaLogistica.SIN_DEFINIR;
    TipoContenedor tipoContenedor = TipoContenedor.REEFER_40;
    double capacidadContenedorTon = 0;
    double cantidadContenedores = 0;
    ArrayList<ContenedorExportacion> contenedores = new ArrayList<ContenedorExportacion>();
    double costoEstimado = 0;
    double costoReal = 0;
    boolean planificacionNuevaActiva = false;

    // ----- Variables -----
    boolean esCrossDock = false;
    String idSitioOrigen = "";

    // ----- Funciones -----

    int calcularCantidadContenedores() {
        if (capacidadContenedorTon <= 0) {
            error(
                "Capacidad de contenedor inválida para pedido: "
                + this
            );
            return 0;
        }

        return (int) Math.ceil(
            toneladasSolicitadas / capacidadContenedorTon
        );
    }
}
