// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Deposito extends Agent {

    // ----- Parámetros -----
    String idUbicacion = "";
    String nombreDeposito = "Deposito";
    int idDeposito = 0;
    boolean habilitado = true;
    double costoJugoTnDia = 0;
    double costoCascaraTnDia = 0;
    double costoAceiteTnDia;
    double velocidadCargaTnHora = 50;
    double velocidadConsolidacionTnHora = 30;

    // ----- Variables -----
    double costoAlmacenamientoAcumulado = 0;
    double toneladasRecibidasAcumuladas = 0;
    double cantidadRecepciones = 0;
    double toneladasConsolidadas = 0;
    int contenedoresConsolidados = 0;
    double costoConsolidacionAcumulado = 0;
    double toneladasCrossDock = 0;
    int contenedoresCrossDock = 0;
    double costoCrossDockAcumulado = 0;

    // ----- Funciones -----

    double getStock(TipoProducto producto) {
        // El stock del deposito se deriva de sus capas (ADR-023).
        Main modelo = (Main) getRootAgent();

        return modelo.inventario.stock(idUbicacion, producto);
    }

    double getCapacidad(TipoProducto producto) {
        // La capacidad es dato de la tabla y varia por tramo de dias (ADR-074): se lee con el
        // dia de campania en vez de copiarse al agente. Una baja con stock adentro no destruye
        // ni mueve producto: el espacio libre queda en cero hasta que el stock salga.
        Main modelo = (Main) getRootAgent();

        return modelo.datos.capacidadDeclaradaTn(idUbicacion, producto, modelo.diaCampania());
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

    double getImporteFletePuerto(Terminal terminal, TipoProducto producto, double toneladas) {
        if (terminal == null) {
            return Double.POSITIVE_INFINITY;
        }

        Main modelo = (Main) getRootAgent();

        return modelo.datos.importeFlete(
            modelo.diaCampania(),
            idUbicacion,
            terminal.idUbicacion,
            producto,
            toneladas,
            modelo.viajesNecesariosCamion(toneladas)
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

    double getImporteConsolidacion(TipoProducto producto, double toneladas, int contenedores) {
        // La tarifa de estiba vive en la tabla y no en el agente (ADR-036), y con ella su
        // unidad: por tonelada o por contenedor completo (ADR-051).
        Main modelo = (Main) getRootAgent();

        return modelo.datos.importeConsolidacion(
            modelo.diaCampania(),
            idUbicacion,
            producto,
            toneladas,
            contenedores
        );
    }

    double getImporteCrossDock(TipoProducto producto, double toneladas, int contenedores) {
        // El cross dock es otro servicio que la estiba desde stock: tiene su propia columna
        // en TarifaSitio (ADR-041).
        Main modelo = (Main) getRootAgent();

        return modelo.datos.importeCrossDock(
            modelo.diaCampania(),
            idUbicacion,
            producto,
            toneladas,
            contenedores
        );
    }
}
