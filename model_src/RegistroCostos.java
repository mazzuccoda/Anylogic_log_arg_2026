// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Registro auditable de los cargos economicos de la campania (ADR-052).
 *
 * Cada evento economico se anota una sola vez, con su clave de negocio, la
 * tarifa que lo genero y su unidad. Los acumuladores de los agentes pasan a ser
 * vistas derivadas: si un total no coincide con el registro, la corrida falla en
 * vez de reportar un numero que nadie puede explicar.
 *
 * No es un tipo de agente: PLE admite 10 y el modelo ya los usa (ADR-030).
 */
public class RegistroCostos implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	/** Tolerancia de la reconciliacion: redondeo de punto flotante, no de negocio. */
	public static final double EPS = 0.01;

	/** Concepto facturable. Cada categoria tiene un solo devengo y un solo dueno. */
	public enum Categoria {
		FLETE_PRODUCTO,
		ROUND_TRIP,
		CONSOLIDACION,
		CROSS_DOCK,
		ALMACENAMIENTO,
		IN_DEPOSITO,
		OUT_DEPOSITO,
		THC,
		COSTO_TERMINAL,
		DESPACHANTE,
		ESPERA,
		OPORTUNIDAD_FRIO,
		PENALIDAD_SOBRECARGA
	}

	/**
	 * CAJA es lo que se paga y es comparable contra una cotizacion; ECONOMICO es
	 * lo que cuesta usar un recurso propio y no se factura (ADR-049).
	 */
	public enum Tipo {
		CAJA,
		ECONOMICO
	}

	/** Un cargo devengado: quien, cuando, por que concepto y contra que tarifa. */
	public static class Cargo implements java.io.Serializable {

		private static final long serialVersionUID = 1L;

		public final long id;
		public final double dia;
		public final Categoria categoria;
		public final Tipo tipo;

		public final String codigoPedido;
		public final String codigoContenedor;
		public final String idLote;
		public final TipoProducto producto;

		public final String origen;
		public final String destino;
		public final String sitio;
		public final EstrategiaLogistica estrategia;
		public final String proveedor;

		public final DatosEntrada.Unidad unidad;
		public final double cantidad;
		public final double tarifa;
		public final double importe;

		public final String idOperacion;
		public final String motivo;

		public Cargo(long id, double dia, Categoria categoria, Tipo tipo,
				String codigoPedido, String codigoContenedor, String idLote, TipoProducto producto,
				String origen, String destino, String sitio, EstrategiaLogistica estrategia,
				String proveedor, DatosEntrada.Unidad unidad, double cantidad, double tarifa,
				String idOperacion, String motivo) {
			this.id = id;
			this.dia = dia;
			this.categoria = categoria;
			this.tipo = tipo;
			this.codigoPedido = codigoPedido;
			this.codigoContenedor = codigoContenedor;
			this.idLote = idLote;
			this.producto = producto;
			this.origen = origen;
			this.destino = destino;
			this.sitio = sitio;
			this.estrategia = estrategia;
			this.proveedor = proveedor;
			this.unidad = unidad;
			this.cantidad = cantidad;
			this.tarifa = tarifa;
			this.importe = cantidad * tarifa;
			this.idOperacion = idOperacion;
			this.motivo = motivo;
		}

		public String clave() {
			// El motivo entra en la clave porque una tarifa puede tener dos componentes
			// con la misma unidad (por ejemplo el flete por viaje mas su parte variable).
			return idOperacion + "|" + categoria + "|" + unidad + "|" + motivo;
		}

		public String toString() {
			return "Cargo[" + id + " dia=" + dia + " " + categoria + " " + tipo
					+ " " + cantidad + " x " + tarifa + " " + unidad + " = " + importe
					+ " op=" + idOperacion + "]";
		}
	}

	private final java.util.List<Cargo> cargos = new java.util.ArrayList<Cargo>();

	/** Clave de negocio de cada cargo ya anotado: garantiza el devengo unico. */
	private final java.util.Set<String> claves = new java.util.HashSet<String>();

	private long secuencia = 0;

	/** Totales por categoria y por tipo, para no recorrer la lista en cada consulta. */
	private final java.util.Map<Categoria, Double> porCategoria =
			new java.util.EnumMap<Categoria, Double>(Categoria.class);

	private final java.util.Map<Tipo, Double> porTipo =
			new java.util.EnumMap<Tipo, Double>(Tipo.class);

	/** Si esta en false los cargos se totalizan pero no se guardan uno por uno. */
	public boolean guardarDetalle = true;

	/**
	 * Anota un cargo y devuelve su importe. Repetir la misma operacion, categoria
	 * y unidad devuelve cero: el devengo doble es un error de modelo, no una
	 * suma mas.
	 */
	public double registrar(double dia, Categoria categoria, Tipo tipo,
			String codigoPedido, String codigoContenedor, String idLote, TipoProducto producto,
			String origen, String destino, String sitio, EstrategiaLogistica estrategia,
			String proveedor, DatosEntrada.Unidad unidad, double cantidad, double tarifa,
			String idOperacion, String motivo) {

		if (idOperacion == null || idOperacion.isEmpty()) {
			throw new RuntimeException("Cargo sin idOperacion: " + categoria
					+ " dia " + dia + ". Sin identificador no hay devengo unico.");
		}

		if (cantidad < 0 || tarifa < 0) {
			throw new RuntimeException("Cargo negativo en " + categoria + " (" + idOperacion
					+ "): cantidad " + cantidad + ", tarifa " + tarifa + ".");
		}

		Cargo cargo = new Cargo(secuencia + 1, dia, categoria, tipo,
				codigoPedido, codigoContenedor, idLote, producto,
				origen, destino, sitio, estrategia, proveedor,
				unidad, cantidad, tarifa, idOperacion, motivo);

		if (!claves.add(cargo.clave())) {
			return 0;
		}

		secuencia++;

		if (guardarDetalle) {
			cargos.add(cargo);
		}

		porCategoria.put(categoria, total(categoria) + cargo.importe);
		porTipo.put(tipo, total(tipo) + cargo.importe);

		return cargo.importe;
	}

	public double total(Categoria categoria) {
		Double valor = porCategoria.get(categoria);
		return valor == null ? 0 : valor;
	}

	public double total(Tipo tipo) {
		Double valor = porTipo.get(tipo);
		return valor == null ? 0 : valor;
	}

	public double total() {
		return total(Tipo.CAJA) + total(Tipo.ECONOMICO);
	}

	public long cantidadCargos() {
		return secuencia;
	}

	public java.util.List<Cargo> detalle() {
		return cargos;
	}

	/**
	 * Suma el detalle por una clave de negocio. Solo sirve con detalle guardado:
	 * es la vista de auditoria, no el total de la corrida.
	 */
	public double totalDe(Categoria categoria, String codigoPedido) {
		exigirDetalle();

		double total = 0;

		for (Cargo cargo : cargos) {
			if (
				(categoria == null || cargo.categoria == categoria)
				&& (codigoPedido == null || codigoPedido.equals(cargo.codigoPedido))
			) {
				total += cargo.importe;
			}
		}

		return total;
	}

	public double totalDeEstrategia(EstrategiaLogistica estrategia, Tipo tipo) {
		exigirDetalle();

		double total = 0;

		for (Cargo cargo : cargos) {
			if (cargo.estrategia == estrategia && (tipo == null || cargo.tipo == tipo)) {
				total += cargo.importe;
			}
		}

		return total;
	}

	public double totalDeSitio(String sitio, Tipo tipo) {
		exigirDetalle();

		double total = 0;

		for (Cargo cargo : cargos) {
			if (sitio != null && sitio.equals(cargo.sitio) && (tipo == null || cargo.tipo == tipo)) {
				total += cargo.importe;
			}
		}

		return total;
	}

	public double totalDeDia(int dia) {
		exigirDetalle();

		double total = 0;

		for (Cargo cargo : cargos) {
			if ((int) Math.floor(cargo.dia) == dia) {
				total += cargo.importe;
			}
		}

		return total;
	}

	public double totalDeProducto(TipoProducto producto, Tipo tipo) {
		exigirDetalle();

		double total = 0;

		for (Cargo cargo : cargos) {
			if (cargo.producto == producto && (tipo == null || cargo.tipo == tipo)) {
				total += cargo.importe;
			}
		}

		return total;
	}

	public double totalDeContenedor(String codigoContenedor) {
		exigirDetalle();

		double total = 0;

		for (Cargo cargo : cargos) {
			if (codigoContenedor != null && codigoContenedor.equals(cargo.codigoContenedor)) {
				total += cargo.importe;
			}
		}

		return total;
	}

	/**
	 * Verifica que un acumulador coincida con el registro. Se llama todos los dias
	 * y al cierre: un total que no se puede explicar cargo por cargo es un error.
	 */
	public void reconciliar(Categoria categoria, double acumulado, double dia) {
		double registrado = total(categoria);

		if (Math.abs(registrado - acumulado) > EPS) {
			throw new RuntimeException("Reconciliacion fallida el dia " + dia + " en "
					+ categoria + ": el registro tiene " + registrado
					+ " y el acumulador " + acumulado
					+ ". Los totales son vistas del registro (ADR-052).");
		}
	}

	/** Suma de las categorias indicadas, para reconciliar totales agregados. */
	public double totalDe(Categoria[] categorias) {
		double total = 0;

		for (Categoria categoria : categorias) {
			total += total(categoria);
		}

		return total;
	}

	public String resumen() {
		String texto = "Cargos: " + secuencia
				+ " | caja " + Math.round(total(Tipo.CAJA))
				+ " | economico " + Math.round(total(Tipo.ECONOMICO));

		for (Categoria categoria : Categoria.values()) {
			if (total(categoria) != 0) {
				texto += "\n  " + categoria + " " + Math.round(total(categoria));
			}
		}

		return texto;
	}

	/**
	 * Escribe el detalle en un csv. No se llama en el barrido: una campania son cientos
	 * de miles de cargos y el archivo solo hace falta cuando hay que auditar un numero.
	 */
	public void exportarCsv(String ruta) {
		exigirDetalle();

		java.io.PrintWriter salida = null;

		try {
			salida = new java.io.PrintWriter(ruta, "UTF-8");

			salida.println("id,dia,categoria,tipo,pedido,contenedor,lote,producto,origen,destino,"
					+ "sitio,estrategia,proveedor,unidad,cantidad,tarifa,importe,operacion,motivo");

			for (Cargo c : cargos) {
				salida.println(c.id + "," + c.dia + "," + c.categoria + "," + c.tipo + ","
						+ c.codigoPedido + "," + c.codigoContenedor + "," + c.idLote + ","
						+ c.producto + "," + c.origen + "," + c.destino + "," + c.sitio + ","
						+ c.estrategia + "," + c.proveedor + "," + c.unidad + ","
						+ c.cantidad + "," + c.tarifa + "," + c.importe + ","
						+ c.idOperacion + "," + c.motivo);
			}

		} catch (java.io.IOException e) {
			throw new RuntimeException("No se pudo escribir " + ruta + ": " + e.getMessage());

		} finally {
			if (salida != null) {
				salida.close();
			}
		}
	}

	private void exigirDetalle() {
		if (!guardarDetalle) {
			throw new RuntimeException("El detalle de cargos esta apagado: las vistas por"
					+ " pedido, contenedor, sitio o dia necesitan guardarDetalle en true.");
		}
	}
}
