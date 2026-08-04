// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Inventario fisico del modelo. La lista de capas es la unica fuente del stock
 * (ADR-023): ni la planta ni los depositos guardan saldos propios, los consultan
 * aca. Asi el stock no puede quedar desalineado con los lotes, porque es el
 * mismo dato leido de dos maneras.
 *
 * El consumo es FIFO por dia de ingreso (ADR-022) y la reserva se anota sobre la
 * capa, sin partir el lote en dos agentes (ADR-024).
 */
public class Inventario implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final double EPS = 0.0001;

	public final java.util.List<Capa> capas = new java.util.ArrayList<Capa>();

	/** Numerador de capas: dos capas pueden coincidir en todo menos en su identidad. */
	private long secuenciaCapa = 0;

	/** Orden FIFO: primero la capa que ingreso antes; los empates se rompen igual siempre. */
	private static class PorAntiguedad
			implements java.util.Comparator<Capa>, java.io.Serializable {

		private static final long serialVersionUID = 1L;

		public int compare(Capa a, Capa b) {
			int r = Double.compare(a.diaIngreso, b.diaIngreso);
			if (r != 0) {
				return r;
			}
			r = Double.compare(a.diaProduccion, b.diaProduccion);
			if (r != 0) {
				return r;
			}
			return a.idLote - b.idLote;
		}
	}

	private static final PorAntiguedad FIFO = new PorAntiguedad();

	/**
	 * Ingresos y egresos fisicos del dia por ubicacion y producto (ADR-064): [ingreso, egreso].
	 *
	 * Se cuenta aca y no en quien llama porque aca estan todas las mutaciones: contarlo en el
	 * modelo obligaria a acordarse en cada punto nuevo, y un movimiento no contado hace que el
	 * balance diario del nodo cierre por casualidad.
	 */
	public final java.util.LinkedHashMap<String, double[]> flujoDia =
			new java.util.LinkedHashMap<String, double[]>();

	private void anotarFlujo(String idUbicacion, TipoProducto producto, double toneladas,
			boolean ingreso) {
		if (toneladas <= EPS) {
			return;
		}
		String clave = idUbicacion + "|" + producto;
		double[] flujo = flujoDia.get(clave);
		if (flujo == null) {
			flujo = new double[2];
			flujoDia.put(clave, flujo);
		}
		flujo[ingreso ? 0 : 1] += toneladas;
	}

	public double ingresosDia(String idUbicacion, TipoProducto producto) {
		double[] flujo = flujoDia.get(idUbicacion + "|" + producto);
		return flujo == null ? 0 : flujo[0];
	}

	public double egresosDia(String idUbicacion, TipoProducto producto) {
		double[] flujo = flujoDia.get(idUbicacion + "|" + producto);
		return flujo == null ? 0 : flujo[1];
	}

	public void reiniciarFlujoDia() {
		flujoDia.clear();
	}

	public Capa ingresar(int idLote, TipoProducto producto, String idUbicacion,
			double toneladas, double dia, double diaProduccion) {
		if (toneladas <= EPS) {
			return null;
		}
		Capa capa = new Capa(idLote, producto, idUbicacion, toneladas, dia, diaProduccion);
		capa.idCapa = ++secuenciaCapa;
		capas.add(capa);
		anotarFlujo(idUbicacion, producto, toneladas, true);
		return capa;
	}

	// ---------------------------------------------------------------- consultas

	public double stock(String idUbicacion, TipoProducto producto) {
		double total = 0;
		for (Capa c : capas) {
			if (c.producto == producto && c.idUbicacion.equals(idUbicacion)) {
				total += c.toneladas;
			}
		}
		return total;
	}

	public double reservado(String idUbicacion, TipoProducto producto) {
		double total = 0;
		for (Capa c : capas) {
			if (c.producto == producto && c.idUbicacion.equals(idUbicacion)) {
				total += c.reservadas();
			}
		}
		return total;
	}

	public double libre(String idUbicacion, TipoProducto producto) {
		return Math.max(0, stock(idUbicacion, producto) - reservado(idUbicacion, producto));
	}

	public double stockProducto(TipoProducto producto) {
		double total = 0;
		for (Capa c : capas) {
			if (c.producto == producto) {
				total += c.toneladas;
			}
		}
		return total;
	}

	public double reservadoProducto(TipoProducto producto) {
		double total = 0;
		for (Capa c : capas) {
			if (c.producto == producto) {
				total += c.reservadas();
			}
		}
		return total;
	}

	public double stockLote(int idLote) {
		double total = 0;
		for (Capa c : capas) {
			if (c.idLote == idLote) {
				total += c.toneladas;
			}
		}
		return total;
	}

	public double reservadoLote(int idLote) {
		double total = 0;
		for (Capa c : capas) {
			if (c.idLote == idLote) {
				total += c.reservadas();
			}
		}
		return total;
	}

	public double reservadoDeLotePorPedido(int idLote, String codigoPedido) {
		double total = 0;
		for (Capa c : capas) {
			if (c.idLote == idLote) {
				total += c.reservadasDePedido(codigoPedido);
			}
		}
		return total;
	}

	public double reservadoPedido(String codigoPedido) {
		double total = 0;
		for (Capa c : capas) {
			total += c.reservadasDePedido(codigoPedido);
		}
		return total;
	}

	public double reservadoPedidoEn(String idUbicacion, TipoProducto producto,
			String codigoPedido) {
		double total = 0;
		for (Capa c : capas) {
			if (c.producto == producto && c.idUbicacion.equals(idUbicacion)) {
				total += c.reservadasDePedido(codigoPedido);
			}
		}
		return total;
	}

	/** Reservado por una asignacion concreta: es contra esto que se despacha (ADR-055). */
	public double reservadoClaveEn(String idUbicacion, TipoProducto producto, String clave) {
		double total = 0;
		for (Capa c : capas) {
			if (c.producto == producto && c.idUbicacion.equals(idUbicacion)) {
				total += c.reservadasDe(clave);
			}
		}
		return total;
	}

	public int cantidadLotes(String idUbicacion, TipoProducto producto) {
		java.util.Set<Integer> ids = new java.util.HashSet<Integer>();
		for (Capa c : capas) {
			if (c.producto == producto && c.idUbicacion.equals(idUbicacion) && c.toneladas > EPS) {
				ids.add(Integer.valueOf(c.idLote));
			}
		}
		return ids.size();
	}

	/** Capas de una ubicacion y producto, de la mas antigua a la mas nueva. */
	public java.util.List<Capa> fifo(String idUbicacion, TipoProducto producto) {
		java.util.List<Capa> sel = new java.util.ArrayList<Capa>();
		for (Capa c : capas) {
			if (c.producto == producto && c.idUbicacion.equals(idUbicacion)) {
				sel.add(c);
			}
		}
		java.util.Collections.sort(sel, FIFO);
		return sel;
	}

	public java.util.List<Capa> fifoDeLote(int idLote, String idUbicacion) {
		java.util.List<Capa> sel = new java.util.ArrayList<Capa>();
		for (Capa c : capas) {
			if (c.idLote == idLote && c.idUbicacion.equals(idUbicacion)) {
				sel.add(c);
			}
		}
		java.util.Collections.sort(sel, FIFO);
		return sel;
	}

	public double stockLoteEn(int idLote, String idUbicacion) {
		double total = 0;
		for (Capa c : capas) {
			if (c.idLote == idLote && c.idUbicacion.equals(idUbicacion)) {
				total += c.toneladas;
			}
		}
		return total;
	}

	public double libreDeLoteEn(int idLote, String idUbicacion) {
		double total = 0;
		for (Capa c : capas) {
			if (c.idLote == idLote && c.idUbicacion.equals(idUbicacion)) {
				total += c.libres();
			}
		}
		return total;
	}

	/** Ubicaciones donde el lote tiene saldo. Con transferencia parcial pueden ser varias. */
	public java.util.List<String> ubicacionesDeLote(int idLote) {
		java.util.List<String> sel = new java.util.ArrayList<String>();
		for (Capa c : capas) {
			if (c.idLote == idLote && c.toneladas > EPS && !sel.contains(c.idUbicacion)) {
				sel.add(c.idUbicacion);
			}
		}
		return sel;
	}

	/**
	 * Ubicacion donde el lote tiene mas saldo, o null si ya no tiene. Es lo unico que
	 * puede significar "donde esta el lote" cuando el lote esta en varias partes.
	 */
	public String ubicacionPrincipalDeLote(int idLote) {
		String mejor = null;
		double mayor = 0;
		for (String u : ubicacionesDeLote(idLote)) {
			double tn = stockLoteEn(idLote, u);
			if (tn > mayor + EPS) {
				mayor = tn;
				mejor = u;
			}
		}
		return mejor;
	}

	/** Capas de una ubicacion con reserva viva de una asignacion, de la mas antigua a la mas nueva. */
	public java.util.List<Capa> capasReservadasDe(String idUbicacion, TipoProducto producto,
			String clave) {
		java.util.List<Capa> sel = new java.util.ArrayList<Capa>();
		for (Capa c : fifo(idUbicacion, producto)) {
			if (c.reservadasDe(clave) > EPS) {
				sel.add(c);
			}
		}
		return sel;
	}

	// ---------------------------------------------------- movimientos (FIFO)

	/** Retira saldo libre consumiendo primero lo mas antiguo. Devuelve lo retirado. */
	public double retirarLibre(String idUbicacion, TipoProducto producto, double toneladas) {
		double pendiente = toneladas;
		for (Capa c : fifo(idUbicacion, producto)) {
			if (pendiente <= EPS) {
				break;
			}
			double toma = Math.min(pendiente, c.libres());
			if (toma <= EPS) {
				continue;
			}
			c.toneladas -= toma;
			pendiente -= toma;
		}
		limpiar();
		anotarFlujo(idUbicacion, producto, toneladas - pendiente, false);
		return toneladas - pendiente;
	}

	/** Mueve saldo libre entre ubicaciones, FIFO. La capa destino arranca su propia antiguedad. */
	public double mover(String origen, String destino, TipoProducto producto,
			double toneladas, double dia) {
		double pendiente = toneladas;
		java.util.List<Capa> nuevas = new java.util.ArrayList<Capa>();
		for (Capa c : fifo(origen, producto)) {
			if (pendiente <= EPS) {
				break;
			}
			double toma = Math.min(pendiente, c.libres());
			if (toma <= EPS) {
				continue;
			}
			c.toneladas -= toma;
			nuevas.add(nuevaCapa(c, destino, toma, dia));
			pendiente -= toma;
		}
		capas.addAll(nuevas);
		limpiar();
		anotarFlujo(origen, producto, toneladas - pendiente, false);
		anotarFlujo(destino, producto, toneladas - pendiente, true);
		return toneladas - pendiente;
	}

	/** Parte de una capa que se muda: mismo lote y misma produccion, otra identidad. */
	private Capa nuevaCapa(Capa origen, String destino, double toneladas, double dia) {
		Capa capa = new Capa(origen.idLote, origen.producto, destino, toneladas, dia,
				origen.diaProduccion);
		capa.idCapa = ++secuenciaCapa;
		return capa;
	}

	/** Igual que mover(), pero limitado a las capas de un lote. */
	public double moverLote(int idLote, String origen, String destino,
			double toneladas, double dia) {
		double pendiente = toneladas;
		java.util.List<Capa> nuevas = new java.util.ArrayList<Capa>();
		for (Capa c : fifoDeLote(idLote, origen)) {
			if (pendiente <= EPS) {
				break;
			}
			double toma = Math.min(pendiente, c.libres());
			if (toma <= EPS) {
				continue;
			}
			c.toneladas -= toma;
			nuevas.add(nuevaCapa(c, destino, toma, dia));
			pendiente -= toma;
		}
		capas.addAll(nuevas);
		limpiar();
		if (!nuevas.isEmpty()) {
			anotarFlujo(origen, nuevas.get(0).producto, toneladas - pendiente, false);
			anotarFlujo(destino, nuevas.get(0).producto, toneladas - pendiente, true);
		}
		return toneladas - pendiente;
	}

	// ---------------------------------------------------------------- reservas

	/**
	 * Reserva para una asignacion de un pedido, FIFO. Devuelve lo reservado, que puede ser
	 * menos: la reserva parcial es valida y se conserva (ADR-055).
	 */
	public double reservar(String idUbicacion, TipoProducto producto, double toneladas,
			String clave, String codigoPedido, double dia) {
		double pendiente = toneladas;
		for (Capa c : fifo(idUbicacion, producto)) {
			if (pendiente <= EPS) {
				break;
			}
			double toma = Math.min(pendiente, c.libres());
			if (toma <= EPS) {
				continue;
			}
			c.reservar(clave, codigoPedido, toma, dia);
			pendiente -= toma;
		}
		return toneladas - pendiente;
	}

	public double liberarReserva(String clave) {
		double total = 0;
		for (Capa c : capas) {
			total += c.quitarReserva(clave, c.reservadasDe(clave));
		}
		return total;
	}

	public double liberarReserva(String clave, double toneladas) {
		double pendiente = toneladas;
		for (Capa c : capas) {
			if (pendiente <= EPS) {
				break;
			}
			pendiente -= c.quitarReserva(clave, pendiente);
		}
		return toneladas - pendiente;
	}

	/** Saca fisicamente toneladas ya reservadas por una asignacion, FIFO. Devuelve lo despachado. */
	public double despachar(String idUbicacion, TipoProducto producto, double toneladas,
			String clave) {
		double pendiente = toneladas;
		for (Capa c : fifo(idUbicacion, producto)) {
			if (pendiente <= EPS) {
				break;
			}
			double toma = Math.min(pendiente, Math.min(c.reservadasDe(clave), c.toneladas));
			if (toma <= EPS) {
				continue;
			}
			c.quitarReserva(clave, toma);
			c.toneladas -= toma;
			pendiente -= toma;
		}
		limpiar();
		anotarFlujo(idUbicacion, producto, toneladas - pendiente, false);
		return toneladas - pendiente;
	}

	/** Igual que reservar(), pero limitado a las capas de un lote en una ubicacion. */
	public double reservarDeLote(int idLote, String idUbicacion, double toneladas,
			String clave, String codigoPedido, double dia) {
		double pendiente = toneladas;
		for (Capa c : fifoDeLote(idLote, idUbicacion)) {
			if (pendiente <= EPS) {
				break;
			}
			double toma = Math.min(pendiente, c.libres());
			if (toma <= EPS) {
				continue;
			}
			c.reservar(clave, codigoPedido, toma, dia);
			pendiente -= toma;
		}
		return toneladas - pendiente;
	}

	/** Saca fisicamente toneladas ya reservadas por una clave, dentro de un lote. */
	public double despacharDeLote(int idLote, String idUbicacion, double toneladas, String clave) {
		double pendiente = toneladas;
		for (Capa c : fifoDeLote(idLote, idUbicacion)) {
			if (pendiente <= EPS) {
				break;
			}
			double toma = Math.min(pendiente, Math.min(c.reservadasDe(clave), c.toneladas));
			if (toma <= EPS) {
				continue;
			}
			c.quitarReserva(clave, toma);
			c.toneladas -= toma;
			pendiente -= toma;
			anotarFlujo(idUbicacion, c.producto, toma, false);
		}
		limpiar();
		return toneladas - pendiente;
	}

	/** Total reservado con una clave, en cualquier ubicacion: lo comprometido por un viaje. */
	public double reservadoDeClave(String clave) {
		double total = 0;
		for (Capa c : capas) {
			total += c.reservadasDe(clave);
		}
		return total;
	}

	// -------------------------------------------------------------- integridad

	private void limpiar() {
		java.util.Iterator<Capa> it = capas.iterator();
		while (it.hasNext()) {
			if (it.next().vacia()) {
				it.remove();
			}
		}
	}

	/** Invariantes del inventario. Una capa incoherente es un error del modelo, no un dato. */
	public java.util.List<String> validar() {
		java.util.List<String> errores = new java.util.ArrayList<String>();
		for (Capa c : capas) {
			if (c.toneladas < -EPS) {
				errores.add("Toneladas negativas en " + c + ".");
			}
			if (c.reservadas() > c.toneladas + EPS) {
				errores.add("Reservado mayor que el saldo fisico en " + c + ".");
			}
			if (c.idUbicacion == null || c.idUbicacion.length() == 0) {
				errores.add("Capa sin ubicacion en el lote " + c.idLote + ".");
			}
			for (Capa.Reserva r : c.reservas) {
				if (r.toneladas < -EPS) {
					errores.add("Reserva negativa de " + r.codigoPedido + " en " + c + ".");
				}
				// Un viaje de producto reserva stock sin pedido: una transferencia a deposito
				// mueve inventario propio y no tiene comprador todavia (ADR-061). Lo que no
				// puede faltar nunca es el dueno de la reserva, y ahi es el viaje.
				boolean deViaje = r.clave != null && r.clave.startsWith("VIAJE|");
				if (!deViaje && (r.codigoPedido == null || r.codigoPedido.length() == 0)) {
					errores.add("Reserva sin pedido en " + c + ".");
				}
				if (r.clave == null || r.clave.length() == 0) {
					errores.add("Reserva sin clave de asignacion en " + c + ".");
				}
			}
		}
		return errores;
	}
}
