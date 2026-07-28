// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Terminal extends Agent {

    // ----- Parámetros -----
    String idUbicacion = "";
    int idTerminal = 0;
    String nombreTerminal = "Terminal";
    double capacidadDiariaTn = 0;
    boolean habilitada = true;
    double velocidadDescargaTnHora = 60;
    double velocidadConsolidacionTnHora = 40;

    // ----- Variables -----
    double toneladasRecibidas = 0;
    double toneladasConsolidadas = 0;
    double cantidadEnviosRecibidos = 0;
    double costoConsolidacionAcumulado = 0;

    // ----- Funciones -----

    double getImporteConsolidacion(TipoProducto producto, double toneladas, int contenedores) {
        // Consolidar en la terminal lo cobra la terminal, con la unidad de su tarifa
        // (ADR-051). El agente ya no guarda una copia de la tarifa por producto.
        Main modelo = (Main) getRootAgent();

        return modelo.datos.importeConsolidacion(
            modelo.diaCampania(),
            idUbicacion,
            producto,
            toneladas,
            contenedores
        );
    }
}
