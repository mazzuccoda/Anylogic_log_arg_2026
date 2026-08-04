// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Envio extends Agent {

    // ----- Parámetros -----
    EstadoEnvio estado = EstadoEnvio.CREADO;
    double diaCreacion = 0;
    double diaInicioCarga = -1;
    double diaSalidaDeposito = -1;
    double diaLlegadaTerminal = -1;
    double diaListoEnTerminal = -1;
    String bloqueActual = "";
    double diaEntradaBloque = -1;
    double horasEsperadasBloque = -1;
    double diaCargosCierre = -1;
    double tiempoRetornoHoras = 0;
    double diaEntrega = -1;
    double tiempoCargaHoras = 0;
    double tiempoViajeIdaHoras = 0;
    double tiempoDescargaHoras = 0;
    double tiempoConsolidacionHoras = 0;
    double costoFleteReal = 0;
    double costoConsolidacionReal = 0;
    double costoTotalReal;
    int idEnvio = 0;
    String codigoEnvio = "";
    Pedido pedido = null;
    Deposito depositoOrigen = null;
    Terminal terminalDestino = null;
    TipoProducto producto = TipoProducto.JUGO;
    double toneladas = 0;
    ContenedorExportacion contenedor = null;

    // ----- Variables -----
    String idSitioOrigen = "";
    EstrategiaLogistica circuito = EstrategiaLogistica.SIN_DEFINIR;
    double tiempoViajeVacioHoras = 0;
    double costoCargosReal = 0;
    double diaCargosTerminal = -1;
    String idAsignacionPedido = "";
    String claveReserva = "";
    boolean esCrossDock = false;
}
