// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Unidad fisica del inventario (ADR-021): un lote en una ubicacion con su propio
 * dia de ingreso. Un lote puede tener varias capas si se mueve por partes.
 *
 * No es un tipo de agente: PLE admite 10 y el modelo ya los usa (ADR-030).
 */
public class Capa implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final double EPS = 0.0001;

	/**
	 * Reserva trazable sobre la capa. Lleva dos identidades y no una: la clave es la
	 * unidad de despacho (una asignacion de un pedido, ADR-055) y el codigo de pedido es
	 * la identidad comercial, que es la que consultan el almacenaje y los KPIs.
	 */
	public static class Reserva implements java.io.Serializable {

		private static final long serialVersionUID = 1L;

		public final String clave;
		public final String codigoPedido;
		public final double diaReserva;
		public double toneladas;

		public Reserva(String clave, String codigoPedido, double toneladas, double diaReserva) {
			this.clave = clave;
			this.codigoPedido = codigoPedido;
			this.toneladas = toneladas;
			this.diaReserva = diaReserva;
		}
	}

	/** Identidad de la capa, para que un cargo pueda apuntar a una y solo una. */
	public long idCapa = 0;

	public final int idLote;
	public final TipoProducto producto;
	public final String material;
	public final double diaProduccion;

	public String idUbicacion;
	public double diaIngreso;
	public double toneladas;
	public double costoAlmacenamiento;

	public final java.util.List<Reserva> reservas = new java.util.ArrayList<Reserva>();

	public Capa(int idLote, TipoProducto producto, String material, String idUbicacion,
			double toneladas, double diaIngreso, double diaProduccion) {
		this.idLote = idLote;
		this.producto = producto;
		this.material = material;
		this.idUbicacion = idUbicacion;
		this.toneladas = toneladas;
		this.diaIngreso = diaIngreso;
		this.diaProduccion = diaProduccion;
	}

	/** El reservado es la suma de las reservas: no hay un segundo saldo que sincronizar. */
	public double reservadas() {
		double total = 0;
		for (Reserva r : reservas) {
			total += r.toneladas;
		}
		return total;
	}

	/** Reservado por una clave, que es una asignacion concreta de un pedido. */
	public double reservadasDe(String clave) {
		double total = 0;
		for (Reserva r : reservas) {
			if (r.clave.equals(clave)) {
				total += r.toneladas;
			}
		}
		return total;
	}

	/** Reservado por un pedido, sumando todas sus asignaciones. */
	public double reservadasDePedido(String codigoPedido) {
		double total = 0;
		for (Reserva r : reservas) {
			if (r.codigoPedido.equals(codigoPedido)) {
				total += r.toneladas;
			}
		}
		return total;
	}

	public double libres() {
		return Math.max(0, toneladas - reservadas());
	}

	public void reservar(String clave, String codigoPedido, double toneladas, double dia) {
		for (Reserva r : reservas) {
			if (r.clave.equals(clave)) {
				r.toneladas += toneladas;
				return;
			}
		}
		reservas.add(new Reserva(clave, codigoPedido, toneladas, dia));
	}

	/** Quita toneladas reservadas por una clave; devuelve lo efectivamente quitado. */
	public double quitarReserva(String clave, double toneladas) {
		double pendiente = toneladas;
		java.util.Iterator<Reserva> it = reservas.iterator();
		while (it.hasNext() && pendiente > EPS) {
			Reserva r = it.next();
			if (!r.clave.equals(clave)) {
				continue;
			}
			double quita = Math.min(pendiente, r.toneladas);
			r.toneladas -= quita;
			pendiente -= quita;
			if (r.toneladas <= EPS) {
				it.remove();
			}
		}
		return toneladas - pendiente;
	}

	public boolean vacia() {
		return toneladas <= EPS && reservas.isEmpty();
	}

	public String toString() {
		return "Capa[lote=" + idLote + " " + producto + "/" + material + " " + idUbicacion
				+ " dia=" + diaIngreso + " tn=" + toneladas
				+ " reservadas=" + reservadas() + "]";
	}
}
