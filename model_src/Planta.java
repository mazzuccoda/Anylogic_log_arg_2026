// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Planta extends Agent {

    // ----- Parámetros -----
    double capacidadJugo = 5000;
    double capacidadCascara = 1800;
    double capacidadAceite = 1500;

    // ----- Variables -----
    double produccionAcumuladaJugo = 0;
    double produccionAcumuladaCascara = 0;
    double produccionAcumuladaAceite = 0;

    // ----- Funciones -----

    void producir() {
        // La produccion del dia es un dato de entrada (tabla ProduccionPlan) y entra
        // completa: la capacidad de la planta es un umbral, no un tope, porque el
        // producto ya esta cosechado y no se puede descartar (ADR-048).
        Main modelo = (Main) getRootAgent();

        int dia = (int) floor(time());

        ingresarProduccion(
            TipoProducto.JUGO,
            modelo.datos.produccionDelDia(dia, TipoProducto.JUGO)
        );

        ingresarProduccion(
            TipoProducto.CASCARA,
            modelo.datos.produccionDelDia(dia, TipoProducto.CASCARA)
        );

        ingresarProduccion(
            TipoProducto.ACEITE,
            modelo.datos.produccionDelDia(dia, TipoProducto.ACEITE)
        );
    }

    double getStock(TipoProducto producto) {
        // El stock de la planta se deriva de sus capas (ADR-023): no hay un saldo propio
        // que pueda quedar desalineado con los lotes.
        Main modelo = (Main) getRootAgent();

        return modelo.inventario.stock("PLANTA", producto);
    }

    double getCapacidad(TipoProducto producto) {
        switch (producto) {
            case JUGO:
                return capacidadJugo;

            case CASCARA:
                return capacidadCascara;

            case ACEITE:
                return capacidadAceite;

            default:
                return 0;
        }
    }

    double getEspacioDisponible(TipoProducto producto) {
        // Lo que falta para llegar a la capacidad nominal. Puede ser cero sin que la
        // produccion se detenga: el excedente queda en sobrecarga (ADR-048).
        return max(0, getCapacidad(producto) - getStock(producto));
    }

    void ingresarProduccion(TipoProducto producto, double toneladas) {
        if (toneladas <= 0) {
            return;
        }

        switch (producto) {

            case JUGO:
                produccionAcumuladaJugo += toneladas;
                break;

            case CASCARA:
                produccionAcumuladaCascara += toneladas;
                break;

            default:
                produccionAcumuladaAceite += toneladas;
                break;
        }

        Main modelo = (Main) getRootAgent();

        modelo.crearLoteEnPlanta(producto, toneladas, this);
    }

    double getOcupacionPct(TipoProducto producto) {
        double capacidad = getCapacidad(producto);

        return capacidad <= 0
            ? 0
            : 100 * getStock(producto) / capacidad;
    }
}
