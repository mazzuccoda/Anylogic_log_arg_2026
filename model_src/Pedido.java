// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.


class Pedido extends Agent {

    // ----- Parámetros -----
    int idPedido = 0;
    String codigoPedido = "";
    TipoProducto producto = TipoProducto.JUGO;
    double toneladasSolicitadas = 0;
    double toneladasReservadas = 0;
    double toneladasEntregadas = 0;
    Terminal puertoSalida = null;
    Deposito depositoAsignado = null;
    String depositoComprometido = "";
    double diaLlegada = 0;
    double diaLimite = 0;
    double diaReserva = -1;
    double diaEntrega = -1;
    EstadoPedido estado = EstadoPedido.PENDIENTE;
    double costoFleteEstimado = 0;
    double costoConsolidadoEstimado = 0;
    double costoTotalEstimado = 0;
    double diasAtraso = 0;
    double toneladasDespachadas = 0;
    double cantidadEnvios = 0;
    double enviosEntregados = 0;
    double costoFleteReal = 0;
    double costoConsolidacionReal = 0;
    double costoLogisticoReal = 0;
    boolean enviosGenerados = false;
    LoteProducto loteSolicitado = null;
    Terminal terminalDestino = null;
    Naviera naviera = Naviera.SIN_DEFINIR;
    String incoterm = "";
    double fechaLimiteTerminal = -1;
    EstrategiaLogistica estrategiaSeleccionada = EstrategiaLogistica.SIN_DEFINIR;
    TipoContenedor tipoContenedor = TipoContenedor.REEFER_40;
    double capacidadContenedorTon = 0;
    double cantidadContenedores = 0;
    ArrayList<ContenedorExportacion> contenedores = new ArrayList<ContenedorExportacion>();
    double costoEstimado = 0;
    double costoReal = 0;
    boolean planificacionNuevaActiva = false;
    ArrayList<AsignacionPedido> asignaciones = new ArrayList<AsignacionPedido>();

    // ----- Variables -----
    boolean esCrossDock = false;
    String idSitioOrigen = "";
    boolean tuvoReservaParcial = false;
    boolean tuvoEntregaParcial = false;
    double diaConocimiento = -1;
    double diaAperturaRetiroVacio = -1;
    double diaETD = -1;
    String buque = "";
    String viajeBuque = "";
    boolean ventanaRetiroAbierta = false;
    boolean perdioCutoff = false;
    boolean reprogramado = false;
    int cantidadReprogramaciones = 0;

    // ----- Funciones -----

    int calcularCantidadContenedores() {
        if (capacidadContenedorTon <= 0) {
            error(
                "Capacidad de contenedor inválida para pedido: "
                + this
            );
            return 0;
        }

        return (int) Math.ceil(
            toneladasSolicitadas / capacidadContenedorTon
        );
    }

    double toneladasAsignadasAcumuladas() {
        // Todo lo que el pedido llego a comprometer, en todos sus origenes (ADR-055). Las
        // asignaciones canceladas no cuentan: lo suyo volvio a estar por asignar.
        double total = 0;

        for (AsignacionPedido asignacion : asignaciones) {

            if (!asignacion.cancelada) {
                total += asignacion.toneladasAsignadas;
            }
        }

        return total;
    }

    double toneladasReservadasActivas() {
        // Reserva viva sobre las capas: lo comprometido que todavia no salio fisicamente.
        double total = 0;

        for (AsignacionPedido asignacion : asignaciones) {

            if (!asignacion.cancelada) {
                total += asignacion.toneladasReservadasActivas;
            }
        }

        return total;
    }

    double toneladasContenerizadas() {
        double total = 0;

        for (AsignacionPedido asignacion : asignaciones) {

            if (!asignacion.cancelada) {
                total += asignacion.toneladasContenerizadas;
            }
        }

        return total;
    }

    double toneladasEnProceso() {
        // Despachado y no entregado: ya consumio la reserva y todavia no llego. Es la
        // definicion que hace cerrar la identidad del pedido (C-01).
        double total = 0;

        for (AsignacionPedido asignacion : asignaciones) {

            if (!asignacion.cancelada) {
                total += asignacion.toneladasEnProceso();
            }
        }

        return total;
    }

    double toneladasPendientesAsignar() {
        // Lo que falta comprometer. Es el saldo con el que trabaja el evaluador: un pedido
        // parcialmente reservado sigue siendo demanda por la diferencia (ADR-055).
        return max(
            0,
            toneladasSolicitadas - toneladasAsignadasAcumuladas()
        );
    }

    double toneladasPendientesEntregar() {
        return max(0, toneladasSolicitadas - toneladasEntregadas);
    }

    boolean estaCompleto() {
        return toneladasEntregadas >= toneladasSolicitadas - 0.0001;
    }

    int cantidadOrigenes() {
        // Cuantos sitios distintos sirvieron al pedido: uno es el caso de siempre y mas de
        // uno es lo que este MOD hace posible.
        java.util.List<String> sitios = new java.util.ArrayList<String>();

        for (AsignacionPedido asignacion : asignaciones) {

            if (
                !asignacion.cancelada
                && !sitios.contains(asignacion.idSitioOrigen)
            ) {
                sitios.add(asignacion.idSitioOrigen);
            }
        }

        return sitios.size();
    }

    java.util.List<AsignacionPedido> asignacionesActivas() {
        java.util.List<AsignacionPedido> activas =
            new java.util.ArrayList<AsignacionPedido>();

        for (AsignacionPedido asignacion : asignaciones) {

            if (asignacion.activa()) {
                activas.add(asignacion);
            }
        }

        return activas;
    }

    AsignacionPedido buscarAsignacion(String idAsignacion) {
        for (AsignacionPedido asignacion : asignaciones) {

            if (asignacion.idAsignacion.equals(idAsignacion)) {
                return asignacion;
            }
        }

        return null;
    }
}
