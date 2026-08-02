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
	private final java.util.List<String> advertencias = new java.util.ArrayList<String>();

	/**
	 * Lo que el libro no trae y se derivo con los defaults del contrato. No aborta
	 * la corrida, pero el modelo lo informa: un dato derivado no es un dato cargado.
	 */
	public static java.util.List<String> ultimasAdvertencias = new java.util.ArrayList<String>();

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
		ultimasAdvertencias = advertencias;

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
			e.umbralAlertaPct = f.numero("umbral_alerta_pct");
			e.umbralSobrecargaPct = f.numero("umbral_sobrecarga_pct");
			e.umbralObjetivoPct = f.numero("umbral_objetivo_pct");
			e.diasForecast = f.entero("dias_forecast");
			e.politicaFrioPropio = f.texto("politica_frio_propio");
			e.politicaSeleccion = f.texto("politica_seleccion");
			e.servicioMinimoProyectado = f.numero("servicio_minimo_proyectado");
			e.factorTarifaFlete = f.numero("factor_tarifa_flete");
			e.factorTarifaRoundTrip = f.numero("factor_tarifa_round_trip");
			e.factorTarifaCrossDock = f.numero("factor_tarifa_cross_dock");
			e.factorTarifaTerminal = f.numero("factor_tarifa_terminal");
			e.factorConsolidacionPlanta = f.numero("factor_consolidacion_planta");
			e.factorCupoCrossDock = f.numero("factor_cupo_cross_dock");
			e.factorCapacidadTerminal = f.numero("factor_capacidad_terminal");
			// Ventana maritima (ADR-059): opcionales, para que un libro anterior al
			// MOD siga corriendo con los defaults del contrato.
			e.diasAnticipacionPlanificacionDefault =
					f.enteroOpcional("dias_anticipacion_planificacion_default", 14);
			e.diasAnticipacionRetiroDefault =
					f.enteroOpcional("dias_anticipacion_retiro_default", 7);
			e.diasEntreCutoffYEtdDefault =
					f.enteroOpcional("dias_entre_cutoff_y_etd_default", 1);
			e.permiteReservaAntesRetiro =
					f.booleanoOpcional("permite_reserva_antes_retiro", true);
			e.permiteTransferenciaAntesRetiro =
					f.booleanoOpcional("permite_transferencia_antes_retiro", true);
			e.permiteReservaCapacidadFutura =
					f.booleanoOpcional("permite_reserva_capacidad_futura", true);
			e.politicaReprogramacionBuque =
					f.textoOpcional("politica_reprogramacion_buque", "CONTINUAR").toUpperCase();
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
					f.numero("contenedores_por_dia"),
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

		// Una fila por sitio, producto y vigencia: todo lo que cobra ese sitio. Los
		// conceptos que no aplican van en cero y no como columna ausente, para que la
		// planilla muestre que la decision de no cobrarlos fue explicita (ADR-051).
		for (Fila f : filas("TarifaSitio", null, null)) {
			datos.tarifasSitio.add(new DatosEntrada.TarifaSitio(
					f.texto("id_ubicacion"), f.producto("producto"),
					f.numero("in_usd_tn"), f.numero("storage_usd_tn_dia"), f.numero("out_usd_tn"),
					f.numero("oportunidad_usd_tn_dia"), f.numero("penalidad_sobrecarga_usd_tn_dia"),
					f.numero("consolidacion_tarifa"), f.unidad("consolidacion_unidad"),
					f.numero("cross_dock_tarifa"), f.unidad("cross_dock_unidad"),
					f.numero("thc_usd_contenedor"), f.numero("costo_terminal_usd_contenedor"),
					f.numero("despachante_tarifa"), f.unidad("despachante_unidad"),
					f.texto("proveedor"), f.entero("vigencia_desde"), f.entero("vigencia_hasta"),
					f.booleano("habilitada")));
		}

		for (Fila f : filas("TarifaFleteProducto", null, null)) {
			datos.tarifasFlete.add(new DatosEntrada.TarifaFlete(
					f.texto("origen"), f.texto("destino"), f.producto("producto"),
					f.texto("tipo_camion"), f.numero("capacidad_camion_tn"), f.unidad("unidad"),
					f.numero("tarifa"), f.numero("variable_usd_tn"),
					f.texto("proveedor"), f.entero("vigencia_desde"), f.entero("vigencia_hasta"),
					f.booleano("habilitada")));
		}

		for (Fila f : filas("TarifaRoundTrip", null, null)) {
			datos.tarifasRoundTrip.add(new DatosEntrada.TarifaRoundTrip(
					f.texto("terminal"), f.texto("sitio"), f.contenedor("tipo_contenedor"),
					f.numero("tarifa_usd_contenedor"), f.numero("horas_espera_incluidas"),
					f.numero("tarifa_espera_usd_hora"),
					f.texto("proveedor"), f.entero("vigencia_desde"), f.entero("vigencia_hasta"),
					f.booleano("habilitada")));
		}

		for (Fila f : filas("TarifaEspera", null, null)) {
			datos.tarifasEspera.add(new DatosEntrada.TarifaEspera(
					f.texto("tipo_recurso"), f.texto("id_ubicacion"),
					f.numero("franquicia_horas"), f.numero("usd_hora"),
					f.texto("proveedor"), f.entero("vigencia_desde"), f.entero("vigencia_hasta"),
					f.booleano("habilitada")));
		}

		for (Fila f : filas("ProduccionPlan", "id_escenario", idEscenario)) {
			datos.produccionPlan.add(new DatosEntrada.ProduccionPlan(
					f.entero("dia"), f.producto("producto"), f.numero("produccion_tn")));
		}

		for (Fila f : filas("PedidoPlan", "id_escenario", idEscenario)) {
			datos.pedidoPlan.add(leerPedidoPlan(f, datos.escenario));
		}

		// La hoja es opcional: un libro sin stock inicial corre con inventario
		// inicial cero, para no invalidar los libros anteriores a ADR-057.
		leerStockInicial(datos, idEscenario);

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
	 * Arma el pedido con su ventana maritima (ADR-059). El contrato pide las cuatro
	 * fechas; un libro anterior al MOD solo trae dia_llegada y dia_limite y la
	 * ventana se deriva de los defaults del escenario, sin inventar conocimiento
	 * mas temprano que el que el libro declara.
	 */
	private DatosEntrada.PedidoPlan leerPedidoPlan(Fila f, DatosEntrada.Escenario escenario) {

		String codigo = f.texto("codigo_pedido");
		TipoProducto producto = f.producto("producto");
		double toneladas = f.numero("toneladas_solicitadas");
		String terminal = f.texto("terminal");

		DatosEntrada.PedidoPlan plan;

		if (f.tiene("dia_cutoff_fisico")) {
			int cutoff = f.entero("dia_cutoff_fisico");
			int retiro = escenario == null ? 7 : escenario.diasAnticipacionRetiroDefault;
			int planificacion = escenario == null
					? 14 : escenario.diasAnticipacionPlanificacionDefault;
			int hastaEtd = escenario == null ? 1 : escenario.diasEntreCutoffYEtdDefault;

			plan = new DatosEntrada.PedidoPlan(
					codigo,
					f.enteroOpcional("dia_conocimiento", cutoff - planificacion),
					f.enteroOpcional("dia_apertura_retiro_vacio", cutoff - retiro),
					cutoff,
					f.enteroOpcional("dia_etd", cutoff + hastaEtd),
					producto, toneladas, terminal);

		} else if (f.tiene("dia_limite")) {
			plan = DatosEntrada.PedidoPlan.desdeLegacy(codigo, f.entero("dia_llegada"),
					f.entero("dia_limite"), producto, toneladas, terminal, escenario);

			String aviso = "La hoja PedidoPlan no trae dia_cutoff_fisico: la ventana"
					+ " maritima se deriva de dia_llegada/dia_limite con los defaults del"
					+ " escenario (ADR-059).";
			if (!advertencias.contains(aviso)) {
				advertencias.add(aviso);
			}

			if (plan.diaConocimiento == plan.diaAperturaRetiroVacio) {
				String sinMargen = "Con la ventana derivada hay pedidos sin margen entre el"
						+ " conocimiento y la apertura del retiro (ADR-059).";
				if (!advertencias.contains(sinMargen)) {
					advertencias.add(sinMargen);
				}
			}

		} else {
			errores.add("La hoja PedidoPlan necesita dia_cutoff_fisico (contrato) o"
					+ " dia_llegada/dia_limite (forma anterior a ADR-059).");
			plan = new DatosEntrada.PedidoPlan(codigo, 0, 0, 0, 0, producto, toneladas, terminal);
		}

		plan.naviera = f.naviera("naviera");
		plan.incoterm = f.textoOpcional("incoterm", "").toUpperCase();
		plan.buque = f.textoOpcional("buque", "");
		plan.viajeBuque = f.textoOpcional("viaje_buque", "");

		return plan;
	}

	/**
	 * Stock inicial (ADR-057). Es la unica hoja que se arma a mano fuera del
	 * volcado del modelo, asi que se lee de forma tolerante: nombre de hoja con
	 * erratas, encabezados alternativos, ubicaciones escritas como en el negocio
	 * ("RUTA 9", "DODERO BARRACAS") y columnas de identidad y fechas opcionales.
	 * Lo que no se puede resolver se informa como error de datos: nada se adivina.
	 */
	private void leerStockInicial(DatosEntrada datos, String idEscenario) {
		String hoja = hojaStockInicial();

		if (hoja == null) {
			return;
		}

		java.util.Map<String, Integer> encabezados = new java.util.HashMap<String, Integer>();
		int columnas = libro.getLastCellNum(hoja, 1);

		for (int c = 1; c <= columnas; c++) {
			if (libro.cellExists(hoja, 1, c)) {
				String nombre = normalizar(libro.getCellStringValue(hoja, 1, c));
				if (nombre.length() > 0 && !encabezados.containsKey(nombre)) {
					encabezados.put(nombre, Integer.valueOf(c));
				}
			}
		}

		int cEscenario = columnaAlias(encabezados, new String[] {"id_escenario", "escenario"});
		int cProducto = columnaAlias(encabezados, new String[] {"producto", "tipo_producto"});
		int cUbicacion = columnaAlias(encabezados,
				new String[] {"id_ubicacion", "ubicacion", "deposito", "sitio", "lugar"});
		int cToneladas = columnaAlias(encabezados, new String[] {"toneladas", "toneladas_tn",
				"inicial", "stock_inicial", "stock_inicial_tn", "stock", "stock_tn", "tn"});
		int cIdStock = columnaAlias(encabezados, new String[] {"id_stock"});
		int cLote = columnaAlias(encabezados, new String[] {"codigo_lote", "lote"});
		int cDiaProduccion = columnaAlias(encabezados,
				new String[] {"dia_produccion", "dia_elaboracion"});
		int cDiaIngreso = columnaAlias(encabezados, new String[] {"dia_ingreso"});
		int cCliente = columnaAlias(encabezados, new String[] {"cliente"});
		int cCalidad = columnaAlias(encabezados, new String[] {"calidad"});

		// Producto, ubicacion y toneladas son el minimo irreducible: lo demas tiene
		// un default explicito y auditable.
		if (cProducto < 0 || cUbicacion < 0 || cToneladas < 0) {
			errores.add("La hoja " + hoja + " de stock inicial necesita al menos las columnas de"
					+ " producto, ubicacion (id_ubicacion, ubicacion o deposito) y toneladas"
					+ " (toneladas o inicial). Encabezados encontrados: " + encabezados.keySet()
					+ ".");
			return;
		}

		int ultima = libro.getLastRowNum(hoja);

		for (int f = 2; f <= ultima; f++) {
			String textoProducto = celda(hoja, f, cProducto);
			String textoUbicacion = celda(hoja, f, cUbicacion);

			if (textoProducto.length() == 0 && textoUbicacion.length() == 0) {
				continue;
			}

			if (cEscenario >= 0 && !idEscenario.equals(celda(hoja, f, cEscenario))) {
				continue;
			}

			// La hoja lista la grilla completa producto x deposito, asi que la
			// mayoria de las celdas esta en cero: cero no es stock, es ausencia.
			double toneladas = celdaNumero(hoja, f, cToneladas,
					"La celda de toneladas de la fila " + f + " de la hoja " + hoja);

			if (toneladas <= 0) {
				continue;
			}

			TipoProducto producto = productoDe(textoProducto, hoja, f);
			String idUbicacion = ubicacionDe(datos, textoUbicacion, hoja, f);

			if (producto == null || idUbicacion == null) {
				continue;
			}

			String idStock = cIdStock >= 0 ? celda(hoja, f, cIdStock) : "";

			if (idStock.length() == 0) {
				idStock = "SI-" + f;
			}

			// Sin codigo de lote, la fila ES el lote: dos filas del mismo producto y
			// deposito son dos partidas distintas, no una sola.
			String codigoLote = cLote >= 0 ? celda(hoja, f, cLote) : "";

			if (codigoLote.length() == 0) {
				codigoLote = "SI-" + producto + "-" + idUbicacion + "-" + f;
			}

			String cliente = cCliente >= 0 ? celda(hoja, f, cCliente) : "";

			if (cliente.length() == 0) {
				cliente = datos.escenario.clienteDefault;
			}

			String calidad = cCalidad >= 0 ? celda(hoja, f, cCalidad) : "";

			if (calidad.length() == 0) {
				calidad = datos.escenario.calidadDefault;
			}

			// Sin fechas historicas el stock se fecha en el dia 0: no se inventa
			// antiguedad, y el FIFO igual lo saca antes que la produccion de campania
			// porque los lotes iniciales se crean antes y desempatan por idLote.
			double diaProduccion = cDiaProduccion < 0 ? 0
					: celdaNumero(hoja, f, cDiaProduccion,
							"La celda dia_produccion de la fila " + f + " de la hoja " + hoja);
			double diaIngreso = cDiaIngreso < 0 ? 0
					: celdaNumero(hoja, f, cDiaIngreso,
							"La celda dia_ingreso de la fila " + f + " de la hoja " + hoja);

			datos.stockInicial.add(new DatosEntrada.StockInicial(idStock, codigoLote, producto,
					idUbicacion, toneladas, diaProduccion, diaIngreso, cliente, calidad));
		}
	}

	/**
	 * Nombre real de la hoja de stock inicial. Se acepta cualquier hoja cuyo
	 * nombre normalizado empiece con STOCK y contenga INICIAL, porque la hoja la
	 * escribe una persona y una errata de tipeo no deberia leerse como "no hay
	 * stock inicial", que es el silencio mas caro posible.
	 */
	private String hojaStockInicial() {
		for (int i = 1; i <= libro.getNumberOfSheets(); i++) {
			String nombre = libro.getSheetName(i);
			String n = normalizar(nombre);

			if (n.startsWith("STOCK") && n.indexOf("INICIAL") >= 0) {
				return nombre;
			}
		}

		return null;
	}

	/** Texto comparable: mayusculas, sin acentos y solo letras y digitos. */
	private String normalizar(String texto) {
		if (texto == null) {
			return "";
		}

		String limpio = texto.toUpperCase().replace("\u00c1", "A").replace("\u00c9", "E")
				.replace("\u00cd", "I").replace("\u00d3", "O").replace("\u00da", "U")
				.replace("\u00d1", "N");
		StringBuilder comparable = new StringBuilder();

		for (int i = 0; i < limpio.length(); i++) {
			char c = limpio.charAt(i);
			if (Character.isLetterOrDigit(c)) {
				comparable.append(c);
			}
		}

		return comparable.toString();
	}

	/** Primera columna presente entre varios nombres posibles; -1 si no hay ninguna. */
	private int columnaAlias(java.util.Map<String, Integer> encabezados, String[] alias) {
		for (String nombre : alias) {
			Integer c = encabezados.get(normalizar(nombre));
			if (c != null) {
				return c.intValue();
			}
		}

		return -1;
	}

	private TipoProducto productoDe(String texto, String hoja, int fila) {
		String n = normalizar(texto);

		for (TipoProducto producto : TipoProducto.values()) {
			if (normalizar(producto.name()).equals(n)) {
				return producto;
			}
		}

		errores.add("Producto desconocido '" + texto + "' en la fila " + fila + " de la hoja "
				+ hoja + ".");
		return null;
	}

	/**
	 * Ubicacion escrita como en el negocio. Se acepta el id exacto y, si no hay
	 * coincidencia, un unico id que sea prefijo del nombre comercial: "RUTA 9" es
	 * RUTA9 y "DODERO BARRACAS" es DODERO. Si el nombre es ambiguo se informa con
	 * los candidatos en vez de elegir uno.
	 */
	private String ubicacionDe(DatosEntrada datos, String texto, String hoja, int fila) {
		String n = normalizar(texto);
		String donde = " en la fila " + fila + " de la hoja " + hoja;

		if (n.length() == 0) {
			errores.add("Falta la ubicacion" + donde + ".");
			return null;
		}

		for (DatosEntrada.Ubicacion u : datos.ubicaciones) {
			if (normalizar(u.idUbicacion).equals(n)) {
				return u.idUbicacion;
			}
		}

		java.util.List<String> candidatos = new java.util.ArrayList<String>();

		for (DatosEntrada.Ubicacion u : datos.ubicaciones) {
			String id = normalizar(u.idUbicacion);
			if (id.length() >= 3 && n.startsWith(id)) {
				candidatos.add(u.idUbicacion);
			}
		}

		if (candidatos.size() == 1) {
			return candidatos.get(0);
		}

		if (candidatos.size() > 1) {
			errores.add("La ubicacion '" + texto + "'" + donde + " es ambigua: coincide con "
					+ candidatos + ".");
			return null;
		}

		java.util.List<String> ids = new java.util.ArrayList<String>();

		for (DatosEntrada.Ubicacion u : datos.ubicaciones) {
			ids.add(u.idUbicacion);
		}

		errores.add("La ubicacion '" + texto + "'" + donde + " no coincide con ninguna de " + ids
				+ ".");
		return null;
	}

	/** Celda como texto, con el mismo criterio de tipos que Fila.comoTexto. */
	private String celda(String hoja, int fila, int columna) {
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

	/** Celda como numero. Una celda vacia es cero; un texto que no es numero, error. */
	private double celdaNumero(String hoja, int fila, int columna, String donde) {
		if (columna < 0 || !libro.cellExists(hoja, fila, columna)) {
			return 0;
		}

		if (libro.getCellType(hoja, fila, columna)
				== com.anylogic.engine.connectivity.ExcelFile.CELL_TYPE_NUMERIC) {
			return libro.getCellNumericValue(hoja, fila, columna);
		}

		String v = celda(hoja, fila, columna);

		if (v.length() == 0) {
			return 0;
		}

		try {
			return Double.parseDouble(v.replace(",", "."));
		} catch (NumberFormatException e) {
			errores.add(donde + " no es un numero: '" + v + "'.");
			return 0;
		}
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

		/** Columna presente en la hoja. Una columna opcional ausente no es un error. */
		private boolean tiene(String nombre) {
			return encabezados.containsKey(nombre);
		}

		private String textoOpcional(String nombre, String porDefecto) {
			if (!tiene(nombre)) {
				return porDefecto;
			}
			String v = comoTexto(columna(nombre));
			return v.length() == 0 ? porDefecto : v;
		}

		private int enteroOpcional(String nombre, int porDefecto) {
			return tiene(nombre) && comoTexto(columna(nombre)).length() > 0
					? entero(nombre) : porDefecto;
		}

		private boolean booleanoOpcional(String nombre, boolean porDefecto) {
			return tiene(nombre) && comoTexto(columna(nombre)).length() > 0
					? booleano(nombre) : porDefecto;
		}

		private Naviera naviera(String nombre) {
			String v = textoOpcional(nombre, "").toUpperCase().replace(" ", "_").replace("-", "_");
			if (v.length() == 0) {
				return Naviera.SIN_DEFINIR;
			}
			try {
				return Naviera.valueOf(v);
			} catch (IllegalArgumentException e) {
				errores.add("Naviera desconocida '" + v + "' en la fila " + fila
						+ " de la hoja " + hoja + ".");
				return Naviera.SIN_DEFINIR;
			}
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

		private DatosEntrada.Unidad unidad(String nombre) {
			String v = texto(nombre).toUpperCase().replace("/", "_").replace(" ", "");
			try {
				return DatosEntrada.Unidad.valueOf(v);
			} catch (IllegalArgumentException e) {
				errores.add("Unidad desconocida '" + v + "' en la columna " + nombre + " de la fila "
						+ fila + " de la hoja " + hoja + ".");
				return DatosEntrada.Unidad.USD_TN;
			}
		}

		private TipoContenedor contenedor(String nombre) {
			String v = texto(nombre).toUpperCase();
			try {
				return TipoContenedor.valueOf(v);
			} catch (IllegalArgumentException e) {
				errores.add("Tipo de contenedor desconocido '" + v + "' en la fila " + fila
						+ " de la hoja " + hoja + ".");
				return TipoContenedor.REEFER_40;
			}
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
