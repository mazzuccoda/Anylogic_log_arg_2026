// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Deposito extends Agent {

    // ----- Parámetros -----
    String idUbicacion = "";
    String nombreDeposito = "Deposito";
    int idDeposito = 0;
    boolean habilitado = true;
    double capacidadJugo = 0;
    double capacidadCascara = 0;
    double capacidadAceite = 0;
    double costoJugoTnDia = 0;
    double costoCascaraTnDia = 0;
    double costoAceiteTnDia;
    double velocidadCargaTnHora = 50;

    // ----- Variables -----
    double costoAlmacenamientoAcumulado = 0;
    double toneladasRecibidasAcumuladas = 0;
    double cantidadRecepciones = 0;

    // ----- Funciones -----

    double getStock(TipoProducto producto) {
        // El stock del deposito se deriva de sus capas (ADR-023).
        Main modelo = (Main) getRootAgent();

        return modelo.inventario.stock(idUbicacion, producto);
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
        return max(
            0,
            getCapacidad(producto) - getStock(producto)
        );
    }

    double getTarifaAlmacenamiento(TipoProducto producto) {
        switch (producto) {

            case JUGO:
                return costoJugoTnDia;

            case CASCARA:
                return costoCascaraTnDia;

            case ACEITE:
                return costoAceiteTnDia;

            default:
                return 0;
        }
    }

    boolean puedeRecibir(TipoProducto producto, double toneladas) {
        if (!habilitado) {
            return false;
        }

        if (toneladas <= 0) {
            return false;
        }

        return getEspacioDisponible(producto) >= toneladas;
    }

    double getCostoFletePuerto(Terminal terminal, TipoProducto producto) {
        if (terminal == null) {
            return Double.POSITIVE_INFINITY;
        }

        Main modelo = (Main) getRootAgent();

        return modelo.datos.fleteUsdTn(
            idUbicacion,
            terminal.idUbicacion,
            producto
        );
    }

    double getReservado(TipoProducto producto) {
        // Lo reservado es la suma de las reservas anotadas en las capas (ADR-024).
        Main modelo = (Main) getRootAgent();

        return modelo.inventario.reservado(idUbicacion, producto);
    }

    double getDisponible(TipoProducto producto) {
        return max(
            0,
            getStock(producto) - getReservado(producto)
        );
    }

    boolean puedeReservar(TipoProducto producto, double toneladas) {
        if (!habilitado) {
            return false;
        }

        if (toneladas <= 0) {
            return false;
        }

        return getDisponible(producto) >= toneladas;
    }

    double getDistanciaTerminal(Terminal terminal) {
        if (terminal == null) {
            return Double.POSITIVE_INFINITY;
        }

        Main modelo = (Main) getRootAgent();

        return modelo.datos.distanciaKm(
            idUbicacion,
            terminal.idUbicacion
        );
    }
}
