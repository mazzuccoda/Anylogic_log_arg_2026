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

	public Capa ingresar(int idLote, TipoProducto producto, String idUbicacion,
			double toneladas, double dia, double diaProduccion) {
		if (toneladas <= EPS) {
			return null;
		}
		Capa capa = new Capa(idLote, producto, idUbicacion, toneladas, dia, diaProduccion);
		capas.add(capa);
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
				total += c.reservadasDe(codigoPedido);
			}
		}
		return total;
	}

	public double reservadoPedido(String codigoPedido) {
		double total = 0;
		for (Capa c : capas) {
			total += c.reservadasDe(codigoPedido);
		}
		return total;
	}

	public double reservadoPedidoEn(String idUbicacion, TipoProducto producto,
			String codigoPedido) {
		double total = 0;
		for (Capa c : capas) {
			if (c.producto == producto && c.idUbicacion.equals(idUbicacion)) {
				total += c.reservadasDe(codigoPedido);
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
			nuevas.add(new Capa(c.idLote, c.producto, destino, toma, dia, c.diaProduccion));
			pendiente -= toma;
		}
		capas.addAll(nuevas);
		limpiar();
		return toneladas - pendiente;
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
			nuevas.add(new Capa(c.idLote, c.producto, destino, toma, dia, c.diaProduccion));
			pendiente -= toma;
		}
		capas.addAll(nuevas);
		limpiar();
		return toneladas - pendiente;
	}

	// ---------------------------------------------------------------- reservas

	/** Reserva para un pedido, FIFO. Devuelve lo reservado, que puede ser menos. */
	public double reservar(String idUbicacion, TipoProducto producto, double toneladas,
			String codigoPedido, double dia) {
		double pendiente = toneladas;
		for (Capa c : fifo(idUbicacion, producto)) {
			if (pendiente <= EPS) {
				break;
			}
			double toma = Math.min(pendiente, c.libres());
			if (toma <= EPS) {
				continue;
			}
			c.reservar(codigoPedido, toma, dia);
			pendiente -= toma;
		}
		return toneladas - pendiente;
	}

	public double liberarReserva(String codigoPedido) {
		double total = 0;
		for (Capa c : capas) {
			total += c.quitarReserva(codigoPedido, c.reservadasDe(codigoPedido));
		}
		return total;
	}

	public double liberarReserva(String codigoPedido, double toneladas) {
		double pendiente = toneladas;
		for (Capa c : capas) {
			if (pendiente <= EPS) {
				break;
			}
			pendiente -= c.quitarReserva(codigoPedido, pendiente);
		}
		return toneladas - pendiente;
	}

	/** Saca fisicamente toneladas ya reservadas por un pedido, FIFO. Devuelve lo despachado. */
	public double despachar(String idUbicacion, TipoProducto producto, double toneladas,
			String codigoPedido) {
		double pendiente = toneladas;
		for (Capa c : fifo(idUbicacion, producto)) {
			if (pendiente <= EPS) {
				break;
			}
			double toma = Math.min(pendiente, Math.min(c.reservadasDe(codigoPedido), c.toneladas));
			if (toma <= EPS) {
				continue;
			}
			c.quitarReserva(codigoPedido, toma);
			c.toneladas -= toma;
			pendiente -= toma;
		}
		limpiar();
		return toneladas - pendiente;
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
				if (r.codigoPedido == null || r.codigoPedido.length() == 0) {
					errores.add("Reserva sin pedido en " + c + ".");
				}
			}
		}
		return errores;
	}
}
