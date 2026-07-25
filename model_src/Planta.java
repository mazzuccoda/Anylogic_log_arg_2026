// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Planta extends Agent {

    // ----- Parámetros -----
    double capacidadJugo = 5000;
    double capacidadCascara = 1800;
    double capacidadAceite = 1500;
    double produccionDiariaJugo = 100;
    double produccionDiariaCascara = 60;
    double produccionDiariaAceite = 8;
    double nivelActivacionJugo = 4000;
    double stockObjetivoJugo = 2500;
    double nivelActivacionCascara = 1400;
    double stockObjetivoCascara = 900;
    double nivelActivacionAceite = 1200;
    double stockObjetivoAceite = 700;

    // ----- Variables -----
    double stockJugo = 0;
    double stockCascara = 0;
    double stockAceite = 0;
    double produccionAcumuladaJugo = 0;
    double produccionAcumuladaCascara = 0;
    double produccionAcumuladaAceite = 0;
    double excedenteJugo = 0;
    double excedenteCascara = 0;
    double excedenteAceite = 0;

    // ----- Funciones -----

    void agregarStock(TipoProducto producto, double toneladas) {
        if (toneladas <= 0) {
            return;
        }

        switch (producto) {

            case JUGO:
                double espacioJugo = capacidadJugo - stockJugo;
                double ingresadoJugo = min(toneladas, espacioJugo);
                double sobranteJugo = toneladas - ingresadoJugo;

                stockJugo += ingresadoJugo;
                excedenteJugo += sobranteJugo;
                break;

            case CASCARA:
                double espacioCascara = capacidadCascara - stockCascara;
                double ingresadoCascara = min(toneladas, espacioCascara);
                double sobranteCascara = toneladas - ingresadoCascara;

                stockCascara += ingresadoCascara;
                excedenteCascara += sobranteCascara;
                break;

            case ACEITE:
                double espacioAceite = capacidadAceite - stockAceite;
                double ingresadoAceite = min(toneladas, espacioAceite);
                double sobranteAceite = toneladas - ingresadoAceite;

                stockAceite += ingresadoAceite;
                excedenteAceite += sobranteAceite;
                break;
        }
    }

    void producir() {
        // Registrar toda la producción generada
        produccionAcumuladaJugo += produccionDiariaJugo;
        produccionAcumuladaCascara += produccionDiariaCascara;
        produccionAcumuladaAceite += produccionDiariaAceite;


        // Guardar stock anterior
        double stockAnteriorJugo = stockJugo;
        double stockAnteriorCascara = stockCascara;
        double stockAnteriorAceite = stockAceite;


        // Intentar almacenar la producción
        agregarStock(TipoProducto.JUGO, produccionDiariaJugo);
        agregarStock(TipoProducto.CASCARA, produccionDiariaCascara);
        agregarStock(TipoProducto.ACEITE, produccionDiariaAceite);


        // Calcular cuánto ingresó realmente
        double ingresoJugo = stockJugo - stockAnteriorJugo;
        double ingresoCascara = stockCascara - stockAnteriorCascara;
        double ingresoAceite = stockAceite - stockAnteriorAceite;


        // Acceder al agente Main
        Main modelo = (Main) getRootAgent();


        // Crear lotes solamente por las toneladas almacenadas
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
        return max(0, getCapacidad(producto) - getStock(producto));
    }

    boolean retirarStock(TipoProducto producto, double toneladas) {
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
}
