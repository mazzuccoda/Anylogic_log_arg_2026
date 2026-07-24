// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class LoteProducto extends Agent {

    // ----- Parámetros -----
    int idLote = 0;
    TipoProducto producto = TipoProducto.JUGO;
    double toneladasIniciales = 0;
    double toneladasDisponibles = 0;
    double diaProduccion = 0;
    EstadoLote estado = EstadoLote.EN_PLANTA;
    Agent ubicacionActual = null;
    double costoAcumulado = 0;
    Pedido pedidoAsignado = null;
    Deposito depositoActual = null;
    double diaIngresoDeposito = -1;
    double costoAlmacenamientoLote = 0;
    LoteProducto loteOrigen = null;
    double toneladasReservadas = 0;
    double diaReserva = -1;

    // ----- Funciones -----

    double getToneladasLibres() {
        return max(
            0,
            toneladasDisponibles - toneladasReservadas
        );
    }
}
