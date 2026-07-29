// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class PlanLogistico extends Agent {

    // ----- Parámetros -----
    String idPlan = "";
    Pedido pedido = null;
    EstrategiaLogistica estrategia = EstrategiaLogistica.SIN_DEFINIR;
    Agent origenProducto = null;
    Agent lugarConsolidacion = null;
    Terminal terminal = null;
    EstadoPlanLogistico estado = EstadoPlanLogistico.BORRADOR;
    boolean factible = false;
    String motivoNoFactible = "";
    double cantidadContenedores = 0;
    double tiempoEstimado = 0;
    double parameter5;
    double costoFleteGuarda = 0;
    double costoAlmacenajeIn = 0;
    double costoAlmacenajeDiario = 0;
    double costoAlmacenajeOut = 0;
    double costoFleteCrossDock = 0;
    double costoCicloContenedor = 0;
    double costoConsolidacion = 0;
    double costoCrossDock = 0;
    double costoTerminal = 0;
    double costoTHC = 0;
    double costoDespachante = 0;
    double costoHistorico = 0;
    double costoIncremental = 0;
    double costoTotalEndToEnd = 0;

    // ----- Variables -----
    String idOrigen = "";
    String idSitioEstiba = "";
    double toneladas = 0;
    double diaDecision = -1;
    double diaEntregaEstimado = -1;
    boolean llegaATiempo = false;
    String politica = "";
    String motivoSeleccion = "";
    int alternativasEvaluadas = 0;
    int alternativasDescartadas = 0;
    java.util.List<AlternativaCircuito> alternativas = new java.util.ArrayList<AlternativaCircuito>();

    // ----- Funciones -----

    void recalcularCostos() {
        costoHistorico =
              costoFleteGuarda
            + costoAlmacenajeIn
            + costoAlmacenajeDiario;

        costoIncremental =
              costoAlmacenajeOut
            + costoFleteCrossDock
            + costoCicloContenedor
            + costoConsolidacion
            + costoCrossDock
            + costoTerminal
            + costoTHC
            + costoDespachante;

        costoTotalEndToEnd =
            costoHistorico + costoIncremental;
    }

    void validarPlan() {
        factible = true;
        motivoNoFactible = "";

        if (pedido == null) {
            factible = false;
            motivoNoFactible = "El plan no tiene pedido.";
        }

        else if (pedido.loteSolicitado == null) {
            factible = false;
            motivoNoFactible = "El pedido no tiene lote solicitado.";
        }

        else if (origenProducto == null) {
            factible = false;
            motivoNoFactible = "No se definió el origen del producto.";
        }

        else if (lugarConsolidacion == null) {
            factible = false;
            motivoNoFactible = "No se definió el lugar de consolidación.";
        }

        else if (terminal == null) {
            factible = false;
            motivoNoFactible = "No se definió la terminal.";
        }

        else if (cantidadContenedores <= 0) {
            factible = false;
            motivoNoFactible = "La cantidad de contenedores es inválida.";
        }

        estado = factible
            ? EstadoPlanLogistico.FACTIBLE
            : EstadoPlanLogistico.NO_FACTIBLE;
    }

    void imprimirResumen() {
        traceln(
            "PLAN: " + idPlan
            + " | Pedido: "
            + (pedido != null ? pedido.codigoPedido : "SIN PEDIDO")
            + " | Estrategia: " + estrategia
            + " | Factible: " + factible
            + " | Contenedores: " + cantidadContenedores
            + " | Histórico: USD " + costoHistorico
            + " | Incremental: USD " + costoIncremental
            + " | Total: USD " + costoTotalEndToEnd
            + (
                factible
                ? ""
                : " | Motivo: " + motivoNoFactible
              )
        );
    }
}
