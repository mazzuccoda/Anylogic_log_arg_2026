// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Planta extends Agent {

    // ----- Parámetros -----
    double capacidadJugo = 5000;
    double capacidadCascara = 1800;
    double capacidadAceite = 1500;
    double nivelActivacionJugo = 4000;
    double stockObjetivoJugo = 2500;
    double nivelActivacionCascara = 1400;
    double stockObjetivoCascara = 900;
    double nivelActivacionAceite = 1200;
    double stockObjetivoAceite = 700;

    // ----- Variables -----
    double produccionAcumuladaJugo = 0;
    double produccionAcumuladaCascara = 0;
    double produccionAcumuladaAceite = 0;
    double excedenteJugo = 0;
    double excedenteCascara = 0;
    double excedenteAceite = 0;

    // ----- Funciones -----

    void producir() {
        // La produccion del dia es un dato de entrada (tabla ProduccionPlan).
        Main modelo = (Main) getRootAgent();

        int dia = (int) floor(time());

        double produccionJugo =
            modelo.datos.produccionDelDia(dia, TipoProducto.JUGO);

        double produccionCascara =
            modelo.datos.produccionDelDia(dia, TipoProducto.CASCARA);

        double produccionAceite =
            modelo.datos.produccionDelDia(dia, TipoProducto.ACEITE);


        // Registrar toda la produccion generada
        produccionAcumuladaJugo += produccionJugo;
        produccionAcumuladaCascara += produccionCascara;
        produccionAcumuladaAceite += produccionAceite;


        // Entra lo que quepa; lo que no entra es excedente y no se produce fisicamente
        double ingresoJugo =
            min(produccionJugo, getEspacioDisponible(TipoProducto.JUGO));

        double ingresoCascara =
            min(produccionCascara, getEspacioDisponible(TipoProducto.CASCARA));

        double ingresoAceite =
            min(produccionAceite, getEspacioDisponible(TipoProducto.ACEITE));

        excedenteJugo += produccionJugo - ingresoJugo;
        excedenteCascara += produccionCascara - ingresoCascara;
        excedenteAceite += produccionAceite - ingresoAceite;


        // Cada ingreso crea el lote y su capa en planta
        if (ingresoJugo > 0) {
            modelo.crearLoteEnPlanta(
                TipoProducto.JUGO,
                ingresoJugo,
                this
            );
        }

        if (ingresoCascara > 0) {
            modelo.crearLoteEnPlanta(
                TipoProducto.CASCARA,
                ingresoCascara,
                this
            );
        }

        if (ingresoAceite > 0) {
            modelo.crearLoteEnPlanta(
                TipoProducto.ACEITE,
                ingresoAceite,
                this
            );
        }
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
        return max(0, getCapacidad(producto) - getStock(producto));
    }
}
