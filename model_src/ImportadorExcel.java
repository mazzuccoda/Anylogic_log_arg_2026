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
			e.permiteFallbackPoliticaFija =
					f.booleanoOpcional("permite_fallback_politica_fija", false);
			e.exportarDiagnosticoCapacidad =
					f.booleanoOpcional("exportar_diagnostico_capacidad", false);
			e.habilitaFlotaProductoMultidiaria =
					f.booleanoOpcional("habilita_flota_producto_multidiaria", true);
			e.diasMaxProgramacionFlota =
					f.numeroOpcional("dias_max_programacion_flota", 2);
			e.politicaReprogramacionBuque =
					f.textoOpcional("politica_reprogramacion_buque", "CONTINUAR").toUpperCase();
			e.fechaInicioCampania = f.fechaOpcional("fecha_inicio_campania");
			datos.escenario = e;
		}

		if (datos.escenario == null) {
			errores.add("La hoja Escenario no tiene ninguna fila con id_escenario = " + idEscenario + ".");
		}

		// La hoja solo existe en el maestro nuevo, donde hay tarifas en pesos. El contrato
		// original cotiza todo en USD: sin la hoja no hay conversion que hacer, y una tarifa
		// en pesos sin esta hoja falla al convertir, con la hoja y el dia en el mensaje
		// (ADR-069, ADR-070).
		Tramos tipoCambio = null;

		for (Fila f : filas("Tipo_cambio", "id_escenario", idEscenario, false)) {
			tipoCambio = leerTramos("Tipo_cambio", f);
		}

		for (Fila f : filas("Producto", "id_escenario", idEscenario)) {
			datos.productos.add(new DatosEntrada.Producto(
					f.producto("producto"),
					f.textoOpcional("material", ""),
					TipoContenedor.valueOf(f.texto("tipo_contenedor")),
					f.numero("capacidad_contenedor_tn"),
					f.numero("toneladas_objetivo_lote_tn")));
		}

		for (Fila f : filas("Ubicacion", "id_escenario", idEscenario)) {
			DatosEntrada.Ubicacion ubicacion = new DatosEntrada.Ubicacion(
					f.texto("id_ubicacion"),
					f.texto("tipo"),
					f.booleano("habilitada"),
					f.numero("velocidad_carga_tn_hora"),
					f.numero("velocidad_descarga_tn_hora"),
					f.numero("velocidad_consolidacion_tn_hora"),
					f.numero("capacidad_diaria_tn"),
					f.numero("contenedores_por_dia"),
					f.numero("posiciones_cross_dock"));

			// ADR-072: las coordenadas son opcionales. Una sola de las dos no alcanza para ubicar
			// el nodo, asi que se toman las dos o ninguna.
			double latitudCelda = f.numeroOpcionalAlias(new String[] {"latitud", "lat"}, Double.NaN);
			double longitudCelda = f.numeroOpcionalAlias(new String[] {"longitud", "long", "lon"}, Double.NaN);

			double latitud = coordenada(latitudCelda,
					LATITUD_MINIMA, LATITUD_MAXIMA, ubicacion.idUbicacion, "latitud");

			double longitud = coordenada(longitudCelda,
					LONGITUD_MINIMA, LONGITUD_MAXIMA, ubicacion.idUbicacion, "longitud");

			if (!Double.isNaN(latitud) && !Double.isNaN(longitud)) {
				ubicacion.latitud = latitud;
				ubicacion.longitud = longitud;

			// Se mira la celda y no el valor validado: una coordenada rechazada por rango ya dio su
			// propio error, y repetirla como "declara una sola" es el mismo problema contado dos veces.
			} else if (Double.isNaN(latitudCelda) != Double.isNaN(longitudCelda)) {
				errores.add("La ubicacion " + ubicacion.idUbicacion + " declara una sola"
						+ " coordenada: latitud y longitud van juntas o ninguna.");
			}

			datos.ubicaciones.add(ubicacion);
		}

		for (Fila f : filas("CapacidadUbicacion", "id_escenario", idEscenario)) {
			datos.capacidades.add(new DatosEntrada.Capacidad(
					f.texto("id_ubicacion"), f.producto("producto"), f.numero("capacidad_tn")));
		}

		for (Fila f : filas("Distancia", "id_escenario", idEscenario)) {
			datos.distancias.add(new DatosEntrada.Distancia(
					f.texto("origen"), f.texto("destino"), f.numero("distancia_km")));
		}

		// Una fila por sitio, producto y vigencia: todo lo que cobra ese sitio. Los
		// conceptos que no aplican van en cero y no como columna ausente, para que la
		// planilla muestre que la decision de no cobrarlos fue explicita (ADR-051).
		// El maestro nuevo (ADR-068) partio esta hoja en varias: sin TarifaSitio, se
		// arma desde Tarifa_almacenaje + Gastos_terminal + Despachante.
		if (hojas().contains("TarifaSitio")) {
			for (Fila f : filas("TarifaSitio", "id_escenario", idEscenario)) {
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
		} else {
			leerTarifaSitioMaestroNuevo(datos, idEscenario, tipoCambio);
		}
		leerGastosThc(datos, idEscenario, tipoCambio);

		if (hojas().contains("TarifaFleteProducto")) {
			for (Fila f : filas("TarifaFleteProducto", "id_escenario", idEscenario)) {
				datos.tarifasFlete.add(new DatosEntrada.TarifaFlete(
						f.texto("origen"), f.texto("destino"), f.producto("producto"),
						f.texto("tipo_camion"), f.numero("capacidad_camion_tn"), f.unidad("unidad"),
						f.numero("tarifa"), f.numero("variable_usd_tn"),
						f.texto("proveedor"), f.entero("vigencia_desde"), f.entero("vigencia_hasta"),
						f.booleano("habilitada")));
			}
		} else if (hojas().contains("TarifaFleteCamionproducto")) {
			// Renombrada en el maestro nuevo, con buckets mensuales en vez de vigencia +
			// una columna tarifa, y sin variable_usd_tn (se asume 0, ADR-068).
			for (Fila f : filas("TarifaFleteCamionproducto", "id_escenario", idEscenario)) {
				Tramos tramos = verificarGrilla("TarifaFleteCamionproducto",
						leerTramos("TarifaFleteCamionproducto", f));
				String moneda = f.textoOpcional("Moneda", "USD");
				String proveedor = f.textoOpcional("proveedor", "MAESTRO_2026");
				boolean habilitada = f.booleanoOpcional("habilitada", true);
				DatosEntrada.Unidad unidad = unidadDeViaje(f.texto("unidad"));
				double variableUsdTn = f.numeroOpcional("variable_usd_tn", 0);
				for (int m = 0; m < tramos.cantidad(); m++) {
					datos.tarifasFlete.add(new DatosEntrada.TarifaFlete(
							f.texto("origen"), f.texto("destino"), f.producto("producto"),
							f.texto("tipo_camion"), f.numero("capacidad_camion_tn"), unidad,
							aUsd(tramos.valor[m], moneda, tramos.desde[m], tipoCambio), variableUsdTn,
							proveedor, tramos.desde[m], tramos.hasta[m], habilitada));
				}
			}
		} else {
			errores.add("Falta la hoja TarifaFleteProducto (o su reemplazo, TarifaFleteCamionproducto).");
		}

		for (Fila f : filas("TarifaRoundTrip", "id_escenario", idEscenario)) {
			if (f.tiene("tarifa_usd_contenedor")) {
				datos.tarifasRoundTrip.add(new DatosEntrada.TarifaRoundTrip(
						f.texto("terminal"), f.texto("sitio"), f.contenedor("tipo_contenedor"),
						f.numero("tarifa_usd_contenedor"), f.numero("horas_espera_incluidas"),
						f.numero("tarifa_espera_usd_hora"),
						f.texto("proveedor"), f.entero("vigencia_desde"), f.entero("vigencia_hasta"),
						f.booleano("habilitada")));
			} else {
				// Buckets mensuales en vez de vigencia + tarifa_usd_contenedor (ADR-068);
				// horas_espera_incluidas y tarifa_espera_usd_hora siguen siendo columnas
				// planas, no varian por mes.
				Tramos tramos = verificarGrilla("TarifaRoundTrip",
						leerTramos("TarifaRoundTrip", f));
				String moneda = f.textoOpcional("Moneda", "USD");
				String proveedor = f.textoOpcional("proveedor", "MAESTRO_2026");
				boolean habilitada = f.booleanoOpcional("habilitada", true);
				for (int m = 0; m < tramos.cantidad(); m++) {
					datos.tarifasRoundTrip.add(new DatosEntrada.TarifaRoundTrip(
							f.texto("terminal"), f.texto("sitio"), f.contenedor("tipo_contenedor"),
							aUsd(tramos.valor[m], moneda, tramos.desde[m], tipoCambio),
							f.numero("horas_espera_incluidas"), f.numero("tarifa_espera_usd_hora"),
							proveedor, tramos.desde[m], tramos.hasta[m], habilitada));
				}
			}
		}

		for (Fila f : filas("TarifaEspera", "id_escenario", idEscenario)) {
			datos.tarifasEspera.add(new DatosEntrada.TarifaEspera(
					f.texto("tipo_recurso"), f.texto("id_ubicacion"),
					f.numero("franquicia_horas"), f.numero("usd_hora"),
					f.texto("proveedor"), f.entero("vigencia_desde"), f.entero("vigencia_hasta"),
					f.booleano("habilitada")));
		}

		for (Fila f : filas("ProduccionPlan", "id_escenario", idEscenario)) {
			datos.produccionPlan.add(new DatosEntrada.ProduccionPlan(
					f.entero("dia"), f.producto("producto"), f.textoOpcional("material", ""),
					f.numero("produccion_tn")));
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
		plan.depositoComprometido = f.textoOpcional("deposito_comprometido", "");
		plan.material = f.textoOpcional("material", "");

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
		int cMaterial = columnaAlias(encabezados, new String[] {"material"});

		// El maestro 2026 nombra la columna de toneladas con el bucket de campania
		// ("1-365") y repite "Producto" para el material: dos encabezados que el
		// contrato no conoce y que, sin tolerarlos, dejan la hoja ilegible. Lo que se
		// interpreta se informa, para que la traza diga que columna se leyo como que.
		if (cToneladas < 0) {
			cToneladas = columnaBucketUnica(hoja, columnas);

			if (cToneladas > 0) {
				advertencias.add("La hoja " + hoja + " no trae columna de toneladas del contrato:"
						+ " se lee la columna de rango de dias '"
						+ libro.getCellStringValue(hoja, 1, cToneladas)
						+ "' como stock inicial en toneladas.");
			}
		}

		if (cMaterial < 0) {
			cMaterial = columnaRepetida(hoja, columnas, cProducto);

			if (cMaterial > 0) {
				advertencias.add("La hoja " + hoja + " repite el encabezado '"
						+ libro.getCellStringValue(hoja, 1, cProducto) + "': la segunda columna se lee"
						+ " como material del stock inicial (ADR-067).");
			}
		}

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

			String material = cMaterial >= 0 ? celda(hoja, f, cMaterial) : "";

			// Sin fechas historicas el stock se fecha en el dia 0: no se inventa
			// antiguedad, y el FIFO igual lo saca antes que la produccion de campania
			// porque los lotes iniciales se crean antes y desempatan por idLote.
			double diaProduccion = cDiaProduccion < 0 ? 0
					: celdaNumero(hoja, f, cDiaProduccion,
							"La celda dia_produccion de la fila " + f + " de la hoja " + hoja);
			double diaIngreso = cDiaIngreso < 0 ? 0
					: celdaNumero(hoja, f, cDiaIngreso,
							"La celda dia_ingreso de la fila " + f + " de la hoja " + hoja);

			datos.stockInicial.add(new DatosEntrada.StockInicial(idStock, codigoLote, producto, material,
					idUbicacion, toneladas, diaProduccion, diaIngreso, cliente, calidad));
		}
	}

	/**
	 * Columna cuyo encabezado es un rango de dias ("1-365", "0-364"): asi nombra el
	 * maestro 2026 la columna de valor de una hoja que no varia en el tiempo.
	 * Devuelve -1 si no hay ninguna o si hay mas de una, porque con dos rangos no
	 * se puede saber cual es el valor y adivinar seria peor que fallar.
	 */
	private int columnaBucketUnica(String hoja, int columnas) {
		int encontrada = -1;

		for (int c = 1; c <= columnas; c++) {
			if (!libro.cellExists(hoja, 1, c)) {
				continue;
			}

			if (!esRangoDeDias(libro.getCellStringValue(hoja, 1, c))) {
				continue;
			}

			if (encontrada > 0) {
				return -1;
			}

			encontrada = c;
		}

		return encontrada;
	}

	/** Encabezado con forma "entero - entero", el bucket de dias del maestro 2026. */
	private boolean esRangoDeDias(String texto) {
		if (texto == null) {
			return false;
		}

		String limpio = texto.trim();
		int guion = limpio.indexOf('-');

		if (guion <= 0 || guion == limpio.length() - 1) {
			return false;
		}

		try {
			Integer.parseInt(limpio.substring(0, guion).trim());
			Integer.parseInt(limpio.substring(guion + 1).trim());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Segunda aparicion del mismo encabezado que la columna dada. El maestro 2026
	 * escribe el material del stock inicial en una segunda columna llamada tambien
	 * "Producto", y el mapa de encabezados solo guarda la primera.
	 */
	private int columnaRepetida(String hoja, int columnas, int columna) {
		if (columna < 1 || !libro.cellExists(hoja, 1, columna)) {
			return -1;
		}

		String buscado = normalizar(libro.getCellStringValue(hoja, 1, columna));

		for (int c = columna + 1; c <= columnas; c++) {
			if (libro.cellExists(hoja, 1, c)
					&& normalizar(libro.getCellStringValue(hoja, 1, c)).equals(buscado)) {
				return c;
			}
		}

		return -1;
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

	/**
	 * Buckets mensuales del maestro nuevo (ADR-068): 12 columnas fijas en vez de
	 * vigencia_desde/vigencia_hasta + una columna tarifa. Los limites de dia son los
	 * de un anio calendario no bisiesto (0-364), no lo que dice el encabezado de cada
	 * columna -- las 8 hojas del maestro repiten el mismo texto con imprecisiones de
	 * +-1 dia ("60-89" en vez de "59-90"), asi que la columna manda por posicion, no
	 * por el numero que dice.
	 */
	/**
	 * Un tramo de vigencia leido del encabezado de la hoja: el maestro tarifa por
	 * rangos de dias en columnas ("0-31", "31-59", ...) y los rangos cambian entre
	 * rondas del relevamiento. Antes los nombres estaban escritos en el codigo y una
	 * hoja con otros rangos se leia como cero -sin error de lectura- y recien fallaba
	 * mucho despues como "la tarifa de flete debe ser > 0" (ADR-070).
	 */
	private class Tramos {

		private final int[] desde;
		private final int[] hasta;
		private final double[] valor;

		private Tramos(int[] desde, int[] hasta, double[] valor) {
			this.desde = desde;
			this.hasta = hasta;
			this.valor = valor;
		}

		private int cantidad() {
			return desde.length;
		}

		/** El valor vigente el dia dado, o 0 si ningun tramo lo cubre. */
		private double valorDelDia(int dia) {
			for (int i = 0; i < desde.length; i++) {
				if (dia >= desde[i] && dia <= hasta[i]) {
					return valor[i];
				}
			}
			return 0;
		}

		private boolean mismosLimites(Tramos otro) {
			if (otro == null || otro.desde.length != desde.length) {
				return false;
			}
			for (int i = 0; i < desde.length; i++) {
				if (otro.desde[i] != desde[i] || otro.hasta[i] != hasta[i]) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Los tramos que declara la fila en sus encabezados de rango de dias, en el orden
	 * de las columnas. El tope de cada tramo es el dia anterior al inicio del
	 * siguiente, no el numero que dice el encabezado: las hojas del maestro escriben
	 * el mismo corte de dos formas ("59-90" y "60-89"), y tomar el declarado deja
	 * huecos o solapes de un dia. El ultimo tramo queda abierto porque el flujo cierra
	 * envios despues del ultimo dia de campania y esos cargos se cotizan con la tarifa
	 * del dia de devengo (ADR-064, ADR-069).
	 */
	private Tramos leerTramos(String hoja, Fila f) {
		java.util.List<int[]> columnas = f.columnasDeRango();

		if (columnas.isEmpty()) {
			String falta = "La hoja " + hoja + " no tiene ninguna columna con rango de dias"
					+ " ('0-31', '31-59', ...): no se puede saber que vigencia tiene la tarifa.";
			if (!errores.contains(falta)) {
				errores.add(falta);
			}
			return new Tramos(new int[0], new int[0], new double[0]);
		}

		int[] desde = new int[columnas.size()];
		int[] hasta = new int[columnas.size()];
		double[] valor = new double[columnas.size()];

		for (int i = 0; i < columnas.size(); i++) {
			desde[i] = columnas.get(i)[1];
			valor[i] = f.numeroDeColumna(columnas.get(i)[0]);
		}

		for (int i = 0; i < columnas.size(); i++) {
			hasta[i] = i + 1 < columnas.size() ? desde[i + 1] - 1 : 9999;
		}

		return new Tramos(desde, hasta, valor);
	}

	/**
	 * Grilla de tramos de la primera hoja de tarifas que se leyo. Las hojas del
	 * maestro comparten los mismos cortes, y si una difiere hay que verlo al cargar:
	 * TarifaSitio se acumula por posicion de tramo entre siete hojas distintas.
	 */
	private Tramos grillaTramos = null;

	private Tramos verificarGrilla(String hoja, Tramos tramos) {
		if (tramos.cantidad() == 0) {
			return tramos;
		}

		if (grillaTramos == null) {
			grillaTramos = tramos;
			return tramos;
		}

		if (!tramos.mismosLimites(grillaTramos)) {
			String detalle = "La hoja " + hoja + " declara " + tramos.cantidad() + " tramos de dias"
					+ " (el primero " + tramos.desde[0] + "-" + tramos.hasta[0] + ") y el resto del"
					+ " libro declara " + grillaTramos.cantidad() + " (el primero "
					+ grillaTramos.desde[0] + "-" + grillaTramos.hasta[0] + "): las tarifas del"
					+ " maestro tienen que compartir los cortes de vigencia.";
			if (!errores.contains(detalle)) {
				errores.add(detalle);
			}
		}

		return tramos;
	}

	/** Convierte a USD un valor tarifado en la moneda de la fila (ADR-068). Solo
	 * 'USD' se deja pasar sin convertir; cualquier otro valor -incluido '$', que es
	 * como el maestro marca los pesos argentinos- se divide por el tipo de cambio
	 * vigente el dia en que empieza el tramo. Un peso sin tipo de cambio es un error
	 * de datos, no un cero: cotizar en cero abarata la alternativa y cambia la
	 * decision (ADR-070). */
	private double aUsd(double valor, String moneda, int dia, Tramos tipoCambio) {
		if (moneda == null || moneda.trim().length() == 0
				|| moneda.trim().equalsIgnoreCase("USD")) {
			return valor;
		}

		double tc = tipoCambio == null ? 0 : tipoCambio.valorDelDia(dia);

		if (tc <= 0) {
			String falta = "Hay tarifas en " + moneda.trim() + " y la hoja Tipo_cambio no declara"
					+ " un tipo de cambio positivo para el dia " + dia + ".";
			if (!errores.contains(falta)) {
				errores.add(falta);
			}
			return valor;
		}

		return valor / tc;
	}

	// Rango continental del pais. Sirve para dos cosas: detectar la coordenada mal escalada y
	// rechazar la que quedo en otro continente por un signo o un pegado en la columna equivocada.
	private static final double LATITUD_MINIMA = -56;
	private static final double LATITUD_MAXIMA = -21;
	private static final double LONGITUD_MINIMA = -74;
	private static final double LONGITUD_MAXIMA = -53;

	/**
	 * Coordenada del relevamiento, normalizada de escala (ADR-072).
	 *
	 * El relevamiento las trae sin separador decimal (-269032792 por -26,9032792), asi que un
	 * valor fuera de rango se divide por diez hasta que entra. Si aun asi no entra, la carga
	 * aborta con el sitio y el valor a la vista: un nodo dibujado en otro continente es un
	 * error silencioso, y la vista de red se leeria como si el dato estuviera bien.
	 */
	private double coordenada(double valor, double minimo, double maximo, String sitio, String columna) {
		if (Double.isNaN(valor) || valor == 0) {
			return Double.NaN;
		}

		double normalizado = valor;

		for (int i = 0; i < 12 && Math.abs(normalizado) > Math.abs(minimo); i++) {
			normalizado = normalizado / 10.0;
		}

		if (normalizado < minimo || normalizado > maximo) {
			errores.add("La " + columna + " de la ubicacion " + sitio + " (" + valor + ") no queda"
					+ " dentro del pais ni normalizando la escala: " + normalizado
					+ " esta fuera de [" + minimo + ", " + maximo + "].");
			return Double.NaN;
		}

		return normalizado;
	}

	/** Igual criterio de nombre comercial que ubicacionDe(), pero sin agregar error:
	 * las hojas de tarifa del maestro nuevo traen sitios que Ubicacion todavia no
	 * tiene (EXOLGAN, TPROSARIO, TRPLATA), y esas filas se descartan explicitamente
	 * en vez de inventarles una ubicacion. */
	private String resolverUbicacionOpcional(DatosEntrada datos, String texto) {
		String n = normalizar(texto);
		for (DatosEntrada.Ubicacion u : datos.ubicaciones) {
			if (normalizar(u.idUbicacion).equals(n)) {
				return u.idUbicacion;
			}
		}
		return null;
	}

	/** Igual que Fila.producto(), pero devuelve null en vez de forzar JUGO cuando el
	 * texto no matchea: estas hojas son de lectura tolerante (ADR-068). */
	private TipoProducto productoOpcional(String texto) {
		String n = normalizar(texto);
		for (TipoProducto p : TipoProducto.values()) {
			if (normalizar(p.name()).equals(n)) {
				return p;
			}
		}
		return null;
	}

	/** "40 HC"/"20 dry"/"40 RF" (Gastos_terminal, Gastos_THC) o el nombre del enum
	 * directo (Despachante). Null si no matchea ninguno de los dos. */
	private TipoContenedor contenedorOpcional(String texto) {
		String n = normalizar(texto).replace("_", "");
		if (n.equals("40HC") || n.equals("DRYHC40")) {
			return TipoContenedor.DRY_HC_40;
		}
		if (n.equals("40RF") || n.equals("REEFER40")) {
			return TipoContenedor.REEFER_40;
		}
		if (n.equals("20DRY") || n.equals("IMODRY20")) {
			return TipoContenedor.IMO_DRY_20;
		}
		return null;
	}

	/** Producto duenio de un tipo de contenedor, segun la tabla Producto ya cargada.
	 * Asume un tipo de contenedor por producto (vale para JUGO/CASCARA/ACEITE con
	 * REEFER_40/DRY_HC_40/IMO_DRY_20); si dos productos compartieran tipo de
	 * contenedor esto devolveria el primero que encuentre. */
	private TipoProducto productoDeContenedor(DatosEntrada datos, TipoContenedor tipo) {
		for (DatosEntrada.Producto p : datos.productos) {
			if (p.tipoContenedor == tipo) {
				return p.producto;
			}
		}
		return null;
	}

	/** Como Fila.naviera(), pero null en vez de error para proveedores que no son
	 * navieras reales (p.ej. FORWARDER/SILVERFREIGHT en Gastos_THC): esas filas se
	 * descartan con una advertencia en vez de abortar el import entero (ADR-068). */
	private Naviera navieraOpcional(String texto) {
		String v = texto == null ? "" : texto.trim().toUpperCase().replace(" ", "_").replace("-", "_");
		if (v.length() == 0) {
			return null;
		}
		try {
			return Naviera.valueOf(v);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/** "VIAJE"/"TN" (TarifaFleteCamionproducto) ademas de los nombres USD_* de
	 * siempre. */
	private DatosEntrada.Unidad unidadDeViaje(String texto) {
		String t = texto == null ? "" : texto.trim().toUpperCase();
		if (t.equals("VIAJE")) {
			return DatosEntrada.Unidad.USD_VIAJE;
		}
		if (t.equals("TN")) {
			return DatosEntrada.Unidad.USD_TN;
		}
		try {
			return DatosEntrada.Unidad.valueOf(t.replace("/", "_").replace(" ", "_"));
		} catch (IllegalArgumentException e) {
			errores.add("Unidad desconocida '" + texto + "' en TarifaFleteCamionproducto.");
			return DatosEntrada.Unidad.USD_TN;
		}
	}

	/**
	 * Maestro nuevo (ADR-068): TarifaSitio se partio en varias hojas por concepto,
	 * cada una con su propia grilla y sus propios buckets mensuales. Se acumula por
	 * (idUbicacion, producto) y se emiten 12 filas de TarifaSitio por combinacion,
	 * una por mes, para que datos.tarifaSitio() siga resolviendo exactamente igual
	 * que con el contrato original.
	 *
	 * Consolidado y Cross_docking se confirmaron con el usuario sin dependencia de
	 * terminal (igual que el contrato original: por idUbicacion + producto), asi
	 * que entran en este mismo acumulador. Gastos_THC se confirmo que si depende de
	 * la naviera (la maritima, no el sitio) y por eso no encaja en TarifaSitio: se
	 * lee aparte en leerGastosThc() hacia datos.tarifasThc, una tabla nueva con su
	 * propia clave.
	 */
	private void leerTarifaSitioMaestroNuevo(DatosEntrada datos, String idEscenario, Tramos tipoCambio) {
		// Los siete conceptos se acumulan por posicion de tramo, asi que los tramos los
		// define la primera hoja con datos y las demas se verifican contra ella (ADR-070).
		Tramos grilla = grillaDeHojas(new String[] {"Tarifa_almacenaje", "Gastos_terminal",
				"Despachante", "TarifaConsolidado", "Consolidado", "TarifaCross_docking",
				"Cross_docking"}, idEscenario);

		if (grilla == null) {
			errores.add("El libro no trae TarifaSitio ni ninguna hoja de concepto"
					+ " (Tarifa_almacenaje, Gastos_terminal, Despachante, Consolidado,"
					+ " Cross_docking) con tramos de dias.");
			return;
		}

		// acumulado[idUbicacion|producto][concepto][tramo]; concepto: 0=in, 1=storage,
		// 2=out, 3=costoTerminal, 4=despachante, 5=consolidacion, 6=crossdock.
		java.util.Map<String, double[][]> acumulado = new java.util.LinkedHashMap<String, double[][]>();

		for (DatosEntrada.Ubicacion u : datos.ubicaciones) {
			for (TipoProducto p : TipoProducto.values()) {
				acumulado.put(u.idUbicacion + "|" + p, new double[7][grilla.cantidad()]);
			}
		}

		for (Fila f : filas("Tarifa_almacenaje", "id_escenario", idEscenario)) {
			String idUbicacion = resolverUbicacionOpcional(datos, f.texto("Proveedor"));
			TipoProducto producto = productoOpcional(f.texto("Tipo"));
			if (idUbicacion == null || producto == null) {
				advertencias.add("Tarifa_almacenaje: fila descartada, no matchea ubicacion/producto ("
						+ f.texto("Proveedor") + " / " + f.texto("Tipo") + ").");
				continue;
			}
			double[][] fila = acumulado.get(idUbicacion + "|" + producto);
			if (fila == null) {
				continue;
			}
			String concepto = f.texto("Concepto").trim().toUpperCase();
			int indice = concepto.equals("IN") ? 0 : concepto.equals("STORAGE") ? 1
					: concepto.equals("OUT") ? 2 : -1;
			if (indice < 0) {
				errores.add("Concepto desconocido '" + f.texto("Concepto") + "' en Tarifa_almacenaje.");
				continue;
			}
			Tramos tramos = verificarGrilla("Tarifa_almacenaje", leerTramos("Tarifa_almacenaje", f));
			String moneda = f.textoOpcional("Moneda", "USD");
			for (int m = 0; m < tramos.cantidad() && m < grilla.cantidad(); m++) {
				fila[indice][m] = aUsd(tramos.valor[m], moneda, tramos.desde[m], tipoCambio);
			}
		}

		for (Fila f : filas("Gastos_terminal", "id_escenario", idEscenario)) {
			String idUbicacion = resolverUbicacionOpcional(datos, f.texto("Puerto de Salida"));
			TipoContenedor tipo = contenedorOpcional(f.texto("Tipo Contenor"));
			if (idUbicacion == null || tipo == null) {
				advertencias.add("Gastos_terminal: fila descartada, no matchea ubicacion/contenedor ("
						+ f.texto("Puerto de Salida") + " / " + f.texto("Tipo Contenor") + ").");
				continue;
			}
			TipoProducto producto = productoDeContenedor(datos, tipo);
			if (producto == null) {
				continue;
			}
			double[][] fila = acumulado.get(idUbicacion + "|" + producto);
			if (fila == null) {
				continue;
			}
			Tramos tramos = verificarGrilla("Gastos_terminal", leerTramos("Gastos_terminal", f));
			String moneda = f.textoOpcional("Moneda", "USD");
			for (int m = 0; m < tramos.cantidad() && m < grilla.cantidad(); m++) {
				fila[3][m] = aUsd(tramos.valor[m], moneda, tramos.desde[m], tipoCambio);
			}
		}

		for (Fila f : filas("Despachante", "id_escenario", idEscenario)) {
			String idUbicacion = resolverUbicacionOpcional(datos, f.texto("Lugar de consolidado"));
			TipoContenedor tipo = contenedorOpcional(f.texto("Tipo Contenor"));
			if (idUbicacion == null || tipo == null) {
				advertencias.add("Despachante: fila descartada, no matchea ubicacion/contenedor ("
						+ f.texto("Lugar de consolidado") + " / " + f.texto("Tipo Contenor") + ").");
				continue;
			}
			TipoProducto producto = productoDeContenedor(datos, tipo);
			if (producto == null) {
				continue;
			}
			double[][] fila = acumulado.get(idUbicacion + "|" + producto);
			if (fila == null) {
				continue;
			}
			Tramos tramos = verificarGrilla("Despachante", leerTramos("Despachante", f));
			String moneda = f.textoOpcional("Moneda", "USD");
			for (int m = 0; m < tramos.cantidad() && m < grilla.cantidad(); m++) {
				fila[4][m] = aUsd(tramos.valor[m], moneda, tramos.desde[m], tipoCambio);
			}
		}

		// Consolidado y Cross_docking (ADR-068 seguimiento): confirmado por el
		// usuario que no dependen de la terminal, solo de idUbicacion + producto,
		// igual que el resto de esta hoja. El nombre de hoja tolera el prefijo
		// "Tarifa" (formato del maestro 2026) o su ausencia (nombre original).
		String hojaConsolidado = hojas().contains("TarifaConsolidado") ? "TarifaConsolidado"
				: hojas().contains("Consolidado") ? "Consolidado" : null;
		if (hojaConsolidado != null) {
			for (Fila f : filas(hojaConsolidado, "id_escenario", idEscenario)) {
				String idUbicacion = resolverUbicacionOpcional(datos, f.texto("Lugar Consolidado"));
				TipoContenedor tipo = contenedorOpcional(f.texto("Tipo de Contenedor"));
				if (idUbicacion == null || tipo == null) {
					advertencias.add(hojaConsolidado + ": fila descartada, no matchea"
							+ " ubicacion/contenedor (" + f.texto("Lugar Consolidado") + " / "
							+ f.texto("Tipo de Contenedor") + ").");
					continue;
				}
				TipoProducto producto = productoDeContenedor(datos, tipo);
				if (producto == null) {
					continue;
				}
				double[][] fila = acumulado.get(idUbicacion + "|" + producto);
				if (fila == null) {
					continue;
				}
				Tramos tramos = verificarGrilla(hojaConsolidado, leerTramos(hojaConsolidado, f));
				String moneda = f.textoOpcional("Moneda", "USD");
				for (int m = 0; m < tramos.cantidad() && m < grilla.cantidad(); m++) {
					fila[5][m] = aUsd(tramos.valor[m], moneda, tramos.desde[m], tipoCambio);
				}
			}
		}

		String hojaCrossDock = hojas().contains("TarifaCross_docking") ? "TarifaCross_docking"
				: hojas().contains("Cross_docking") ? "Cross_docking" : null;
		if (hojaCrossDock != null) {
			for (Fila f : filas(hojaCrossDock, "id_escenario", idEscenario)) {
				String idUbicacion = resolverUbicacionOpcional(datos, f.texto("Lugar Consolidado"));
				TipoContenedor tipo = contenedorOpcional(f.texto("Tipo de Contenedor"));
				if (idUbicacion == null || tipo == null) {
					advertencias.add(hojaCrossDock + ": fila descartada, no matchea"
							+ " ubicacion/contenedor (" + f.texto("Lugar Consolidado") + " / "
							+ f.texto("Tipo de Contenedor") + ").");
					continue;
				}
				TipoProducto producto = productoDeContenedor(datos, tipo);
				if (producto == null) {
					continue;
				}
				double[][] fila = acumulado.get(idUbicacion + "|" + producto);
				if (fila == null) {
					continue;
				}
				Tramos tramos = verificarGrilla(hojaCrossDock, leerTramos(hojaCrossDock, f));
				String moneda = f.textoOpcional("Moneda", "USD");
				for (int m = 0; m < tramos.cantidad() && m < grilla.cantidad(); m++) {
					fila[6][m] = aUsd(tramos.valor[m], moneda, tramos.desde[m], tipoCambio);
				}
			}
		}

		for (java.util.Map.Entry<String, double[][]> entrada : acumulado.entrySet()) {
			int corte = entrada.getKey().indexOf('|');
			String idUbicacion = entrada.getKey().substring(0, corte);
			TipoProducto producto = TipoProducto.valueOf(entrada.getKey().substring(corte + 1));
			double[][] valores = entrada.getValue();
			for (int m = 0; m < grilla.cantidad(); m++) {
				datos.tarifasSitio.add(new DatosEntrada.TarifaSitio(
						idUbicacion, producto,
						valores[0][m], valores[1][m], valores[2][m],
						0, 0,
						valores[5][m], DatosEntrada.Unidad.USD_CONTENEDOR,
						valores[6][m], DatosEntrada.Unidad.USD_CONTENEDOR,
						0, valores[3][m],
						valores[4][m], DatosEntrada.Unidad.USD_CONTENEDOR,
						"MAESTRO_2026", grilla.desde[m], grilla.hasta[m], true));
			}
		}
	}

	/**
	 * THC por naviera (ADR-068 seguimiento): confirmado por el usuario que el
	 * costo de THC de Gastos_THC lo factura la naviera (la maritima), no el sitio,
	 * asi que no encaja en el acumulador de TarifaSitio de arriba. Se lee aparte,
	 * hacia datos.tarifasThc, con su propia clave (naviera + tipo de contenedor).
	 * Proveedores de Gastos_THC que no son una naviera real del enum (p.ej.
	 * FORWARDER, SILVERFREIGHT: forwarders/alternativas, no lineas maritimas) se
	 * descartan con una advertencia en vez de abortar el import: en los datos
	 * reales todos los pedidos declaran naviera MAERSK, que si esta en el enum.
	 */
	/** Los tramos de dias de la primera de esas hojas que tenga datos del escenario. */
	private Tramos grillaDeHojas(String[] candidatas, String idEscenario) {
		for (String hoja : candidatas) {
			if (!hojas().contains(hoja)) {
				continue;
			}

			for (Fila f : filas(hoja, "id_escenario", idEscenario, false)) {
				Tramos tramos = leerTramos(hoja, f);

				if (tramos.cantidad() > 0) {
					return verificarGrilla(hoja, tramos);
				}
			}
		}

		return null;
	}

	private void leerGastosThc(DatosEntrada datos, String idEscenario, Tramos tipoCambio) {
		if (!hojas().contains("Gastos_THC")) {
			return;
		}
		for (Fila f : filas("Gastos_THC", "id_escenario", idEscenario)) {
			Naviera naviera = navieraOpcional(f.texto("Proveedor"));
			TipoContenedor tipo = contenedorOpcional(f.texto("Tipo Contenor"));
			if (naviera == null || tipo == null) {
				advertencias.add("Gastos_THC: fila descartada, proveedor no es una naviera"
						+ " conocida o tipo de contenedor invalido (" + f.texto("Proveedor") + " / "
						+ f.texto("Tipo Contenor") + ").");
				continue;
			}
			Tramos tramos = verificarGrilla("Gastos_THC", leerTramos("Gastos_THC", f));
			String moneda = f.textoOpcional("Moneda", "USD");
			for (int m = 0; m < tramos.cantidad(); m++) {
				datos.tarifasThc.add(new DatosEntrada.TarifaThc(naviera, tipo,
						aUsd(tramos.valor[m], moneda, tramos.desde[m], tipoCambio),
						"MAESTRO_2026", tramos.desde[m], tramos.hasta[m], true));
			}
		}
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
		return filas(hoja, columnaFiltro, valorFiltro, true);
	}

	/** @param obligatoria si la hoja ausente es un error de contrato o simplemente no hay datos. */
	private java.util.List<Fila> filas(String hoja, String columnaFiltro, String valorFiltro,
			boolean obligatoria) {
		java.util.List<Fila> resultado = new java.util.ArrayList<Fila>();

		if (!hojas().contains(hoja)) {
			if (obligatoria) {
				errores.add("Falta la hoja " + hoja + ".");
			}
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
			// El filtro solo aplica si la columna existe: una hoja del formato anterior a
			// ADR-068, sin id_escenario, se sigue leyendo entera en vez de fallar por una
			// columna que ese libro nunca tuvo.
			if (columnaFiltro != null && encabezados.containsKey(columnaFiltro)
					&& !valorFiltro.equals(fila.texto(columnaFiltro))) {
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

		/**
		 * Las columnas cuyo encabezado es un rango de dias, ordenadas por posicion, como
		 * pares {columna, primer dia}. Es lo que define la vigencia de las tarifas del
		 * maestro, que cambia de ronda en ronda (ADR-070).
		 */
		private java.util.List<int[]> columnasDeRango() {
			java.util.List<int[]> encontradas = new java.util.ArrayList<int[]>();

			for (java.util.Map.Entry<String, Integer> e : encabezados.entrySet()) {
				if (!esRangoDeDias(e.getKey())) {
					continue;
				}

				int guion = e.getKey().indexOf('-');
				int primerDia = Integer.parseInt(e.getKey().substring(0, guion).trim());
				encontradas.add(new int[] {e.getValue().intValue(), primerDia});
			}

			java.util.Collections.sort(encontradas, new java.util.Comparator<int[]>() {
				public int compare(int[] a, int[] b) {
					return a[0] - b[0];
				}
			});

			return encontradas;
		}

		/** Valor numerico de una columna por posicion. Celda vacia o no numerica es 0: en
		 * las grillas de tarifa por rango de dias, un mes en blanco es "no cobra". */
		private double numeroDeColumna(int columna) {
			String v = comoTexto(columna);

			if (v.length() == 0) {
				return 0;
			}

			try {
				return Double.parseDouble(v.replace(",", "."));
			} catch (NumberFormatException e) {
				errores.add("El valor '" + v + "' de la hoja " + hoja + " fila " + fila
						+ " no es un numero.");
				return 0;
			}
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

		/**
		 * Fecha del contrato, en YYYY-MM-DD. Una celda con formato de fecha llega como numero
		 * de serie de Excel: se convierte para que el libro pueda tipearla como fecha y no
		 * obligue a escribirla como texto. Lo que no sea ni una cosa ni la otra vuelve tal
		 * cual y lo rechaza la validacion del contrato, con el valor a la vista.
		 */
		private String fechaOpcional(String nombre) {
			String v = textoOpcional(nombre, "");

			if (v.length() == 0 || v.indexOf('-') >= 0) {
				return v;
			}

			double serie;

			try {
				serie = Double.parseDouble(v.replace(",", "."));

			} catch (NumberFormatException e) {
				return v;
			}

			java.util.GregorianCalendar c =
					new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("UTC"));

			c.clear();
			c.set(1899, java.util.Calendar.DECEMBER, 30);
			c.add(java.util.Calendar.DAY_OF_MONTH, (int) Math.floor(serie));

			return String.format(java.util.Locale.US, "%04d-%02d-%02d",
					c.get(java.util.Calendar.YEAR),
					c.get(java.util.Calendar.MONTH) + 1,
					c.get(java.util.Calendar.DAY_OF_MONTH));
		}

		private int enteroOpcional(String nombre, int porDefecto) {
			return tiene(nombre) && comoTexto(columna(nombre)).length() > 0
					? entero(nombre) : porDefecto;
		}

		private double numeroOpcional(String nombre, double porDefecto) {
			return tiene(nombre) && comoTexto(columna(nombre)).length() > 0
					? numero(nombre) : porDefecto;
		}

		/** El primero de varios encabezados posibles que la hoja traiga. Las columnas nuevas
		 * del relevamiento llegan con el nombre del negocio ('Lat'), no con el del contrato. */
		private double numeroOpcionalAlias(String[] nombres, double porDefecto) {
			for (int i = 0; i < nombres.length; i++) {
				for (java.util.Map.Entry<String, Integer> e : encabezados.entrySet()) {
					if (e.getKey().trim().equalsIgnoreCase(nombres[i])) {
						String v = comoTexto(e.getValue().intValue());
						if (v.length() > 0) {
							return numeroDeColumna(e.getValue().intValue());
						}
					}
				}
			}
			return porDefecto;
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
