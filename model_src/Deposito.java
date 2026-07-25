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
    double stockJugo = 0;
    double stockCascara = 0;
    double stockAceite = 0;
    double costoAlmacenamientoAcumulado = 0;
    double toneladasRecibidasAcumuladas = 0;
    double cantidadRecepciones = 0;
    double reservadoJugo = 0;
    double reservadoCascara = 0;
    double reservadoAceite = 0;

    // ----- Funciones -----

    double getStock(TipoProducto producto) {
        switch (producto) {

            case JUGO:
                return stockJugo;

            case CASCARA:
                return stockCascara;

            case ACEITE:
                return stockAceite;

            default:
                return 0;
        }
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

    boolean recibirProducto(TipoProducto producto, double toneladas) {
        if (!puedeRecibir(producto, toneladas)) {
            return false;
        }

        switch (producto) {

            case JUGO:
                stockJugo += toneladas;
                break;

            case CASCARA:
                stockCascara += toneladas;
                break;

            case ACEITE:
                stockAceite += toneladas;
                break;
        }

        toneladasRecibidasAcumuladas += toneladas;
        cantidadRecepciones++;

        return true;
    }

    boolean retirarProducto(TipoProducto producto, double toneladas) {
        if (toneladas <= 0) {
            return false;
        }

        switch (producto) {

            case JUGO:
                if (stockJugo >= toneladas) {
                    stockJugo -= toneladas;
                    return true;
                }
                return false;

            case CASCARA:
                if (stockCascara >= toneladas) {
                    stockCascara -= toneladas;
                    return true;
                }
                return false;

            case ACEITE:
                if (stockAceite >= toneladas) {
                    stockAceite -= toneladas;
                    return true;
                }
                return false;

            default:
                return false;
        }
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
        switch (producto) {

            case JUGO:
                return reservadoJugo;

            case CASCARA:
                return reservadoCascara;

            case ACEITE:
                return reservadoAceite;

            default:
                return 0;
        }
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

    boolean reservarProducto(TipoProducto producto, double toneladas) {
        if (!puedeReservar(producto, toneladas)) {
            traceln(
                nombreDeposito
                + " no puede reservar "
                + toneladas
                + " tn de "
                + producto
                + " | disponible="
                + getDisponible(producto)
            );

            return false;
        }

        switch (producto) {

            case JUGO:
                reservadoJugo += toneladas;
                break;

            case CASCARA:
                reservadoCascara += toneladas;
                break;

            case ACEITE:
                reservadoAceite += toneladas;
                break;

            default:
                return false;
        }

        traceln(
            nombreDeposito
            + " reservó "
            + toneladas
            + " tn de "
            + producto
            + " | reservado total="
            + getReservado(producto)
        );

        return true;
    }

    boolean liberarReserva(TipoProducto producto, double toneladas) {
        if (toneladas <= 0) {
            return false;
        }

        switch (producto) {
            case JUGO:
                if (reservadoJugo < toneladas) {
                    return false;
                }
                reservadoJugo -= toneladas;
                return true;

            case CASCARA:
                if (reservadoCascara < toneladas) {
                    return false;
                }
                reservadoCascara -= toneladas;
                return true;

            case ACEITE:
                if (reservadoAceite < toneladas) {
                    return false;
                }
                reservadoAceite -= toneladas;
                return true;

            default:
                return false;
        }
    }

    boolean despacharReservado(TipoProducto producto, double toneladas) {
        if (toneladas <= 0) {
            return false;
        }

        if (getReservado(producto) < toneladas) {
            return false;
        }

        if (getStock(producto) < toneladas) {
            return false;
        }

        if (!retirarProducto(producto, toneladas)) {
            return false;
        }

        if (!liberarReserva(producto, toneladas)) {
            recibirProducto(producto, toneladas);
            return false;
        }

        return true;
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
