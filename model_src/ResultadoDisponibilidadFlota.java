// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Lo que la agenda de camiones puede prometer para un movimiento, sin mutarla (ADR-061).
 * Reemplaza el si/no de flotaProductoAlcanza(): una alternativa que solo puede mover una
 * parte del volumen sigue compitiendo por esa parte en lugar de descartarse entera.
 */
public class ResultadoDisponibilidadFlota implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public double toneladasSolicitadas = 0;
	public double toneladasProgramables = 0;
	public int viajesRequeridos = 0;
	public int viajesProgramables = 0;

	public double primeraSalida = -1;
	public double ultimaSalida = -1;
	public double ultimaLlegada = -1;
	public double ultimoRegreso = -1;
	public double esperaMaximaDias = 0;

	public String motivo = "";

	public boolean puedeProgramarAlgo() {
		return toneladasProgramables > 0.0001;
	}

	public boolean puedeProgramarTodo() {
		return toneladasProgramables + 0.0001 >= toneladasSolicitadas;
	}

	public String descripcion() {
		return Math.round(toneladasProgramables * 100) / 100.0
				+ " de " + Math.round(toneladasSolicitadas * 100) / 100.0 + " tn"
				+ " en " + viajesProgramables + " de " + viajesRequeridos + " viajes"
				+ " | espera " + Math.round(esperaMaximaDias * 100) / 100.0 + " d"
				+ (motivo.isEmpty() ? "" : " | " + motivo);
	}
}
