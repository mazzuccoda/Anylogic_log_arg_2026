// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Terminal extends Agent {

    // ----- Parámetros -----
    int idTerminal = 0;
    String nombreTerminal = "Terminal";
    double capacidadDiariaTn = 0;
    boolean habilitada = true;
    double costoConsolidadoJugo = 0;
    double costoConsolidadoCascara = 0;
    double costoConsolidadoAceite = 0;
    double velocidadDescargaTnHora = 60;
    double velocidadConsolidacionTnHora = 40;

    // ----- Variables -----
    double toneladasRecibidas = 0;
    double toneladasConsolidadas = 0;
    double cantidadEnviosRecibidos = 0;
    double costoConsolidacionAcumulado = 0;

    // ----- Funciones -----

    double getCostoConsolidado(TipoProducto producto) {
        switch (producto) {

            case JUGO:
                return costoConsolidadoJugo;

            case CASCARA:
                return costoConsolidadoCascara;

            case ACEITE:
                return costoConsolidadoAceite;

            default:
                return 0;
        }
    }
}
