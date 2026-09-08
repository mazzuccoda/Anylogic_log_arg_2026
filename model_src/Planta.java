// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Planta extends Agent {

    // ----- Variables -----
    double produccionAcumuladaJugo = 0;
    double produccionAcumuladaCascara = 0;
    double produccionAcumuladaAceite = 0;
    double toneladasConsolidadas = 0;
    int contenedoresConsolidados = 0;
    double costoConsolidacionAcumulado = 0;

    // ----- Funciones -----

    void producir() {
        // La produccion del dia es un dato de entrada (tabla ProduccionPlan) y entra
        // completa: la capacidad de la planta es un umbral, no un tope, porque el
        // producto ya esta cosechado y no se puede descartar (ADR-048). Se produce por
        // material (ADR-067): materialesDe() lee la lista valida de la tabla Producto, asi
        // que un material nuevo en el maestro entra sin tocar este codigo.
        Main modelo = (Main) getRootAgent();

        int dia = (int) floor(time());

        for (TipoProducto producto : TipoProducto.values()) {
            for (String material : modelo.datos.materialesDe(producto)) {
                ingresarProduccion(
                    producto,
                    material,
                    modelo.datos.produccionDelDia(dia, producto, material)
                );
            }
        }
    }

    double getStock(TipoProducto producto) {
        // El stock de la planta se deriva de sus capas (ADR-023): no hay un saldo propio
        // que pueda quedar desalineado con los lotes.
        Main modelo = (Main) getRootAgent();

        return modelo.inventario.stock("PLANTA", producto);
    }

    double getCapacidad(TipoProducto producto) {
        // La capacidad es dato de la tabla y varia por tramo de dias (ADR-074): se lee con el
        // dia de campania en vez de copiarse al agente. Una baja con stock adentro no destruye
        // ni mueve producto: el espacio libre queda en cero hasta que el stock salga.
        Main modelo = (Main) getRootAgent();

        return modelo.datos.capacidadDeclaradaTn("PLANTA", producto, modelo.diaCampania());
    }

    double getEspacioDisponible(TipoProducto producto) {
        // Lo que falta para llegar a la capacidad nominal. Puede ser cero sin que la
        // produccion se detenga: el excedente queda en sobrecarga (ADR-048).
        return max(0, getCapacidad(producto) - getStock(producto));
    }

    void ingresarProduccion(TipoProducto producto, String material, double toneladas) {
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

        modelo.crearLoteEnPlanta(producto, material, toneladas, this);
    }

    double getOcupacionPct(TipoProducto producto) {
        double capacidad = getCapacidad(producto);

        return capacidad <= 0
            ? 0
            : 100 * getStock(producto) / capacidad;
    }
}
