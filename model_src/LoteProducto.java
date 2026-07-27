// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class LoteProducto extends Agent {

    // ----- Parámetros -----
    int idLote = 0;
    TipoProducto producto = TipoProducto.JUGO;
    double toneladasIniciales = 0;
    double diaProduccion = 0;
    EstadoLote estado = EstadoLote.EN_PLANTA;
    Agent ubicacionActual = null;
    double costoAcumulado = 0;
    Pedido pedidoAsignado = null;
    Deposito depositoActual = null;
    double diaIngresoDeposito = -1;
    double costoAlmacenamientoLote = 0;
    double diaReserva = -1;
    String cliente = "";
    String calidad = "";
    double toneladasObjetivo = 0;
    EstadoComercialLote estadoComercial = EstadoComercialLote.ABIERTO;

    // ----- Funciones -----

    double getToneladasLibres() {
        return max(
            0,
            getToneladasDisponibles() - getToneladasReservadas()
        );
    }

    double getToneladasDisponibles() {
        // El saldo fisico del lote es la suma de sus capas (ADR-023): el lote es la
        // identidad productiva, la capa es la existencia fisica.
        Main modelo = (Main) getRootAgent();

        return modelo.inventario.stockLote(idLote);
    }

    double getToneladasReservadas() {
        Main modelo = (Main) getRootAgent();

        return modelo.inventario.reservadoLote(idLote);
    }
}
