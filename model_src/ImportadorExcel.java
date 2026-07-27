// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Fase 2 del contrato de datos: llena las mismas tablas que GeneradorSintetico,
 * leyendo un libro de Excel con una hoja por tabla (contrato, seccion 3).
 *
 * La logica de negocio no cambia al cambiar de origen: sigue leyendo
 * DatosEntrada. Lo unico que decide el origen es Main.cargarDatosEntrada().
 *
 * Las hojas y columnas que falten se informan todas juntas, no de a una.
 */
public class ImportadorExcel implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private final com.anylogic.engine.connectivity.ExcelFile libro;
	private final java.util.List<String> errores = new java.util.ArrayList<String>();

	private ImportadorExcel(com.anylogic.engine.connectivity.ExcelFile libro) {
		this.libro = libro;
	}

	/**
	 * @param duenio       agente que abre el archivo (normalmente Main)
	 * @param ruta         ruta del libro, absoluta o relativa al directorio del modelo
	 * @param idEscenario  filtra las hojas que tienen columna id_escenario
	 */
	public static DatosEntrada importar(com.anylogic.engine.Presentable duenio, String ruta,
			String idEscenario) {

		com.anylogic.engine.connectivity.ExcelFile libro =
				new com.anylogic.engine.connectivity.ExcelFile(duenio, "/", ruta, false);
		libro.readFile();

		try {
			return new ImportadorExcel(libro).leer(idEscenario);
		} finally {
			libro.close();
		}
	}

	private DatosEntrada leer(String idEscenario) {
		DatosEntrada datos = new DatosEntrada();

		for (Fila f : filas("Escenario", "id_escenario", idEscenario)) {
			DatosEntrada.Escenario e = new DatosEntrada.Escenario();
			e.idEscenario = f.texto("id_escenario");
			e.duracionCampaniaDias = f.entero("duracion_campania_dias");
			e.semilla = (long) f.numero("semilla_base");
			e.variabilidadProduccion = f.numero("variabilidad_produccion");
			e.variabilidadDemanda = f.numero("variabilidad_demanda");
			e.pedidosPorCampania = f.entero("pedidos_por_campania");
			e.toneladasMediasPedido = f.numero("toneladas_medias_pedido");
			e.plazoPedidoDias = f.entero("plazo_pedido_dias");
			e.camionesProducto = f.entero("camiones_producto");
			e.camionesPortacontenedor = f.entero("camiones_portacontenedor");
			e.capacidadCamionTn = f.numero("capacidad_camion_tn");
			e.velocidadCamionKmh = f.numero("velocidad_camion_kmh");
			e.horasOperativasDia = f.numero("horas_operativas_dia");
			e.factorProduccion = f.numero("factor_produccion");
			e.factorCapacidadPlanta = f.numero("factor_capacidad_planta");
			e.factorCapacidadDeposito = f.numero("factor_capacidad_deposito");
			e.factorStorage = f.numero("factor_storage");
			e.ventanaDemanda = f.numero("ventana_demanda");
			e.habilitaCrossDock = f.texto("habilita_cross_dock").equalsIgnoreCase("true");
			e.deterministico = f.texto("deterministico").equalsIgnoreCase("true");
			e.estrategiaConsolidacion = f.texto("estrategia_consolidacion");
			e.clienteDefault = f.texto("cliente_default");
			e.calidadDefault = f.texto("calidad_default");
			datos.escenario = e;
		}

		if (datos.escenario == null) {
			errores.add("La hoja Escenario no tiene ninguna fila con id_escenario = " + idEscenario + ".");
		}

		for (Fila f : filas("Producto", null, null)) {
			datos.productos.add(new DatosEntrada.Producto(
					f.producto("producto"),
					TipoContenedor.valueOf(f.texto("tipo_contenedor")),
					f.numero("capacidad_contenedor_tn"),
					f.numero("toneladas_objetivo_lote_tn")));
		}

		for (Fila f : filas("Ubicacion", null, null)) {
			datos.ubicaciones.add(new DatosEntrada.Ubicacion(
					f.texto("id_ubicacion"),
					f.texto("tipo"),
					f.booleano("habilitada"),
					f.numero("velocidad_carga_tn_hora"),
					f.numero("velocidad_descarga_tn_hora"),
					f.numero("velocidad_consolidacion_tn_hora"),
					f.numero("capacidad_diaria_tn"),
					f.numero("posiciones_consolidacion"),
					f.numero("contenedores_por_posicion_dia"),
					f.numero("posiciones_cross_dock")));
		}

		for (Fila f : filas("CapacidadUbicacion", null, null)) {
			datos.capacidades.add(new DatosEntrada.Capacidad(
					f.texto("id_ubicacion"), f.producto("producto"), f.numero("capacidad_tn")));
		}

		for (Fila f : filas("Distancia", null, null)) {
			datos.distancias.add(new DatosEntrada.Distancia(
					f.texto("origen"), f.texto("destino"), f.numero("distancia_km")));
		}

		for (Fila f : filas("TarifaAlmacenamiento", null, null)) {
			datos.tarifasAlmacenamiento.add(new DatosEntrada.TarifaAlmacenamiento(
					f.texto("id_ubicacion"), f.producto("producto"), f.numero("storage_usd_tn_dia")));
		}

		for (Fila f : filas("TarifaFleteProducto", null, null)) {
			datos.tarifasFlete.add(new DatosEntrada.TarifaFlete(
					f.texto("origen"), f.texto("destino"), f.producto("producto"),
					f.numero("tarifa_usd_tn")));
		}

		for (Fila f : filas("TarifaServicioCarga", null, null)) {
			datos.tarifasServicioCarga.add(new DatosEntrada.TarifaServicioCarga(
					f.texto("id_ubicacion"), f.producto("producto"), f.texto("tipo_servicio"),
					f.numero("tarifa_usd_tn")));
		}

		for (Fila f : filas("ProduccionPlan", "id_escenario", idEscenario)) {
			datos.produccionPlan.add(new DatosEntrada.ProduccionPlan(
					f.entero("dia"), f.producto("producto"), f.numero("produccion_tn")));
		}

		for (Fila f : filas("PedidoPlan", "id_escenario", idEscenario)) {
			datos.pedidoPlan.add(new DatosEntrada.PedidoPlan(
					f.texto("codigo_pedido"), f.entero("dia_llegada"), f.entero("dia_limite"),
					f.producto("producto"), f.numero("toneladas_solicitadas"), f.texto("terminal")));
		}

		if (!errores.isEmpty()) {
			String detalle = "";
			for (String e : errores) {
				detalle += "\n  - " + e;
			}
			throw new RuntimeException("El libro de entrada no cumple el contrato de datos ("
					+ errores.size() + "):" + detalle);
		}

		return datos;
	}

	/**
	 * Nombres de las hojas del libro. Se listan en vez de preguntar por cada
	 * hoja: pedir una hoja inexistente aborta la corrida y la idea es informar
	 * todas las que falten.
	 */
	private java.util.Set<String> hojas() {
		java.util.Set<String> nombres = new java.util.HashSet<String>();
		for (int i = 1; i <= libro.getNumberOfSheets(); i++) {
			nombres.add(libro.getSheetName(i));
		}
		return nombres;
	}

	/**
	 * Devuelve las filas de la hoja indexadas por encabezado. Si la hoja no
	 * existe, anota el error y sigue: el objetivo es listar todo lo que falta.
	 */
	private java.util.List<Fila> filas(String hoja, String columnaFiltro, String valorFiltro) {
		java.util.List<Fila> resultado = new java.util.ArrayList<Fila>();

		if (!hojas().contains(hoja)) {
			errores.add("Falta la hoja " + hoja + ".");
			return resultado;
		}

		java.util.Map<String, Integer> encabezados = new java.util.HashMap<String, Integer>();
		int columnas = libro.getLastCellNum(hoja, 1);

		for (int c = 1; c <= columnas; c++) {
			if (libro.cellExists(hoja, 1, c)) {
				String nombre = libro.getCellStringValue(hoja, 1, c);
				if (nombre != null && nombre.trim().length() > 0) {
					encabezados.put(nombre.trim(), c);
				}
			}
		}

		int ultima = libro.getLastRowNum(hoja);

		for (int f = 2; f <= ultima; f++) {
			Fila fila = new Fila(hoja, f, encabezados);
			if (fila.vacia()) {
				continue;
			}
			if (columnaFiltro != null && !valorFiltro.equals(fila.texto(columnaFiltro))) {
				continue;
			}
			resultado.add(fila);
		}

		return resultado;
	}

	/** Una fila de una hoja, leída por nombre de columna y no por posición. */
	private class Fila {

		private final String hoja;
		private final int fila;
		private final java.util.Map<String, Integer> encabezados;

		private Fila(String hoja, int fila, java.util.Map<String, Integer> encabezados) {
			this.hoja = hoja;
			this.fila = fila;
			this.encabezados = encabezados;
		}

		private boolean vacia() {
			for (Integer c : encabezados.values()) {
				if (comoTexto(c.intValue()).length() > 0) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Excel no garantiza el tipo de la celda: un mismo encabezado puede venir
		 * como texto en una fila y como numero en otra.
		 */
		private String comoTexto(int columna) {
			if (columna < 0 || !libro.cellExists(hoja, fila, columna)) {
				return "";
			}
			org.apache.poi.ss.usermodel.CellType tipo = libro.getCellType(hoja, fila, columna);
			if (tipo == com.anylogic.engine.connectivity.ExcelFile.CELL_TYPE_NUMERIC) {
				double v = libro.getCellNumericValue(hoja, fila, columna);
				return v == Math.rint(v) ? String.valueOf((long) v) : String.valueOf(v);
			}
			if (tipo == com.anylogic.engine.connectivity.ExcelFile.CELL_TYPE_BOOLEAN) {
				return String.valueOf(libro.getCellBooleanValue(hoja, fila, columna));
			}
			if (tipo == com.anylogic.engine.connectivity.ExcelFile.CELL_TYPE_BLANK) {
				return "";
			}
			String v = libro.getCellStringValue(hoja, fila, columna);
			return v == null ? "" : v.trim();
		}

		private int columna(String nombre) {
			Integer c = encabezados.get(nombre);
			if (c == null) {
				String falta = "Falta la columna " + nombre + " en la hoja " + hoja + ".";
				if (!errores.contains(falta)) {
					errores.add(falta);
				}
				return -1;
			}
			return c.intValue();
		}

		private String texto(String nombre) {
			return comoTexto(columna(nombre));
		}

		private double numero(String nombre) {
			int c = columna(nombre);
			if (c < 0) {
				return 0;
			}
			// Un número tipeado como texto es el error de carga más común: se
			// acepta, pero un texto que no sea número se informa.
			if (libro.getCellType(hoja, fila, c)
					== com.anylogic.engine.connectivity.ExcelFile.CELL_TYPE_NUMERIC) {
				return libro.getCellNumericValue(hoja, fila, c);
			}
			String v = texto(nombre);
			try {
				return Double.parseDouble(v.replace(",", "."));
			} catch (NumberFormatException e) {
				errores.add("La celda " + nombre + " de la fila " + fila + " de la hoja " + hoja
						+ " no es un numero: '" + v + "'.");
				return 0;
			}
		}

		private int entero(String nombre) {
			return (int) Math.round(numero(nombre));
		}

		private boolean booleano(String nombre) {
			String v = texto(nombre).toUpperCase();
			return v.equals("TRUE") || v.equals("SI") || v.equals("SÍ") || v.equals("1");
		}

		private TipoProducto producto(String nombre) {
			String v = texto(nombre).toUpperCase();
			try {
				return TipoProducto.valueOf(v);
			} catch (IllegalArgumentException e) {
				errores.add("Producto desconocido '" + v + "' en la fila " + fila
						+ " de la hoja " + hoja + ".");
				return TipoProducto.JUGO;
			}
		}
	}
}
