// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Una alternativa evaluada en una ronda de decision de un pedido (ADR-064).
 *
 * La granularidad es dia + pedido + ronda + alternativa. La ronda es parte de la
 * identidad porque asignarConEvaluador() vuelve a generar las alternativas en cada
 * vuelta: tomar stock cambia lo que el resto puede prometer (ADR-055), asi que la
 * misma alternativa evaluada dos veces en el mismo dia son dos hechos distintos.
 *
 * No es un tipo de agente: PLE admite 10 y el modelo ya los usa (ADR-030).
 */
public class RegistroDecisionAlternativa implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------ corrida
	public String runId = "";
	public String escenario = "";
	public int replica = 0;
	public double diaSimulacion = 0;
	public int diaCampania = 0;
	public String politicaSeleccion = "";
	public String criterioOrden = "";

	// ----------------------------------------------------------------- decision
	public String idDecision = "";
	public int ronda = 0;
	public String idAlternativa = "";

	// ------------------------------------------------------------------- pedido
	public String codigoPedido = "";
	public String producto = "";
	public String tipoContenedor = "";
	public String terminal = "";
	public String estadoPedidoAntes = "";
	public double toneladasSolicitadas = 0;
	public double toneladasEntregadasPrevias = 0;
	public double toneladasEnProcesoPrevias = 0;
	public double toneladasReservadasPrevias = 0;
	public double toneladasPendientesAsignar = 0;
	public int contenedoresPendientesEstimados = 0;
	public double diaConocimiento = -1;
	public double diaAperturaRetiro = -1;
	public double diaCutoff = -1;
	public double diasHastaCutoff = 0;
	public int cantidadOrigenesPrevios = 0;

	// -------------------------------------------------------------- alternativa
	public String circuito = "";
	public boolean esCrossDock = false;
	public String origenStock = "";
	public String sitioEstiba = "";
	public String destinoFinal = "";
	public boolean requiereFlotaProducto = false;
	public boolean requierePortacontenedor = false;
	public boolean requierePosicion = false;
	public String tipoRecursoCapacidad = "";
	public String ubicacionRecursoCapacidad = "";

	// ------------------------------------------------------ stock y capacidad al decidir
	public double stockFisicoOrigenTn = 0;
	public double stockLibreOrigenTn = 0;
	public double stockReservadoOrigenTn = 0;
	public double stockEnTransitoHaciaOrigenTn = 0;
	public double espacioFisicoSitioTn = 0;
	public double espacioEfectivoSitioTn = 0;
	public double ocupacionSitioPct = 0;
	public int cupoCrossdockLibreCont = 0;
	public int posicionesDisponiblesAntesCutoff = 0;
	public double flotaProductoDisponible = 0;
	public double primeraSalidaFlota = -1;
	public double ultimaLlegadaFlota = -1;
	public double esperaFlotaDias = 0;
	public int portacontenedoresLibres = 0;
	public int portacontenedoresOcupados = 0;

	// --------------------------------------------------------- volumen factible
	public double toneladasSinRestriccionCapacidad = 0;
	public double toneladasFactibles = 0;
	public int contenedoresFactibles = 0;
	public int contenedoresConCapacidad = 0;
	public int viajesProductoRequeridos = 0;
	public int viajesProductoFactibles = 0;
	public boolean esAsignacionParcial = false;
	public double porcentajePedidoCubierto = 0;

	// -------------------------------------------------------- tiempos estimados
	public double horasFleteProducto = 0;
	public double horasCargaEstiba = 0;
	public double horasViajeVacio = 0;
	public double horasViajeCargado = 0;
	public double horasDescargaTerminal = 0;
	public double horasConsolidacionTerminal = 0;
	public double horasCicloFisicoTotal = 0;
	public double diaEntregaEstimado = -1;
	public double holguraEstimadaDias = 0;
	public boolean llegaATiempoEstimado = false;

	// --------------------------------------------------------- costos estimados
	public double costoFleteProducto = 0;
	public double costoRoundTrip = 0;
	public double costoEstiba = 0;
	public double costoOut = 0;
	public double costoThc = 0;
	public double costoTerminal = 0;
	public double costoDespachante = 0;
	public double costoInHundido = 0;
	public double costoAlmacenajeHundido = 0;
	public double costoFleteHundido = 0;
	public double costoHistorico = 0;
	public double costoIncremental = 0;
	public double costoEndToEnd = 0;
	public double costoIncrementalUsdTn = 0;
	public double costoEndToEndUsdTn = 0;
	public double costoIncrementalUsdCont = 0;
	public double costoUnitarioSinRestriccion = 0;

	// ---------------------------------------------------------------- resultado
	public boolean factible = false;
	public int ordenRanking = -1;
	public String resultadoEjecucion = "";
	public String codigoMotivo = "";
	public String detalleMotivo = "";
	public double toneladasTomadas = 0;
	public double costoElegidaUsdTn = 0;
	public double diferenciaVsElegidaUsdTn = 0;
	public double saldoPedidoAntes = 0;
	public double saldoPedidoDespues = 0;
	public boolean esMasBarataNoFactible = false;

	/**
	 * Que paso con la alternativa en la ronda. No alcanza con un booleano "elegida":
	 * la primera del ranking puede fallar al ejecutar porque otro pedido del mismo dia
	 * se llevo la capacidad, y entonces se ejecuta la segunda (ADR-060).
	 */
	public static final String ELEGIDA = "ELEGIDA";
	public static final String INTENTADA_FALLIDA = "INTENTADA_FALLIDA";
	public static final String NO_INTENTADA = "NO_INTENTADA";
	public static final String NO_FACTIBLE = "NO_FACTIBLE";

	public static String encabezadoCsv() {
		return "run_id,escenario,replica,dia_simulacion,dia_campania,politica_seleccion,"
			+ "criterio_orden,id_decision,ronda,id_alternativa,"
			+ "codigo_pedido,producto,tipo_contenedor,terminal,estado_pedido_antes,"
			+ "toneladas_solicitadas,toneladas_entregadas_previas,toneladas_en_proceso_previas,"
			+ "toneladas_reservadas_previas,toneladas_pendientes_asignar,"
			+ "contenedores_pendientes_estimados,dia_conocimiento,dia_apertura_retiro,dia_cutoff,"
			+ "dias_hasta_cutoff,cantidad_origenes_previos,"
			+ "circuito,es_cross_dock,origen_stock,sitio_estiba,destino_final,"
			+ "requiere_flota_producto,requiere_portacontenedor,requiere_posicion,"
			+ "tipo_recurso_capacidad,ubicacion_recurso_capacidad,"
			+ "stock_fisico_origen_tn,stock_libre_origen_tn,stock_reservado_origen_tn,"
			+ "stock_en_transito_hacia_origen_tn,espacio_fisico_sitio_tn,espacio_efectivo_sitio_tn,"
			+ "ocupacion_sitio_pct,cupo_crossdock_libre_cont,posiciones_disponibles_antes_cutoff,"
			+ "flota_producto_disponible,primera_salida_flota,ultima_llegada_flota,"
			+ "espera_flota_dias,portacontenedores_libres,portacontenedores_ocupados,"
			+ "toneladas_sin_restriccion_capacidad,toneladas_factibles,contenedores_factibles,"
			+ "contenedores_con_capacidad,viajes_producto_requeridos,viajes_producto_factibles,"
			+ "es_asignacion_parcial,porcentaje_pedido_cubierto,"
			+ "horas_flete_producto,horas_carga_estiba,horas_viaje_vacio,horas_viaje_cargado,"
			+ "horas_descarga_terminal,horas_consolidacion_terminal,horas_ciclo_fisico_total,"
			+ "dia_entrega_estimado,holgura_estimada_dias,llega_a_tiempo_estimado,"
			+ "costo_flete_producto,costo_roundtrip,costo_estiba,costo_out,costo_thc,"
			+ "costo_terminal,costo_despachante,costo_in_hundido,costo_almacenaje_hundido,"
			+ "costo_flete_hundido,costo_historico,costo_incremental,costo_end_to_end,"
			+ "costo_incremental_usd_tn,costo_end_to_end_usd_tn,costo_incremental_usd_cont,"
			+ "costo_unitario_sin_restriccion,"
			+ "factible,orden_ranking,resultado_ejecucion,codigo_motivo,detalle_motivo,"
			+ "toneladas_tomadas,costo_elegida_usd_tn,diferencia_vs_elegida_usd_tn,"
			+ "saldo_pedido_antes,saldo_pedido_despues,es_mas_barata_no_factible,"
			+ "fecha,fecha_cutoff";
	}

	public String toCsv() {
		StringBuilder f = new StringBuilder(1024);

		f.append(AuditoriaRed.txt(runId)).append(',');
		f.append(AuditoriaRed.txt(escenario)).append(',');
		f.append(AuditoriaRed.ent(replica)).append(',');
		f.append(AuditoriaRed.num(diaSimulacion)).append(',');
		f.append(AuditoriaRed.ent(diaCampania)).append(',');
		f.append(AuditoriaRed.txt(politicaSeleccion)).append(',');
		f.append(AuditoriaRed.txt(criterioOrden)).append(',');
		f.append(AuditoriaRed.txt(idDecision)).append(',');
		f.append(AuditoriaRed.ent(ronda)).append(',');
		f.append(AuditoriaRed.txt(idAlternativa)).append(',');

		f.append(AuditoriaRed.txt(codigoPedido)).append(',');
		f.append(AuditoriaRed.txt(producto)).append(',');
		f.append(AuditoriaRed.txt(tipoContenedor)).append(',');
		f.append(AuditoriaRed.txt(terminal)).append(',');
		f.append(AuditoriaRed.txt(estadoPedidoAntes)).append(',');
		f.append(AuditoriaRed.num(toneladasSolicitadas)).append(',');
		f.append(AuditoriaRed.num(toneladasEntregadasPrevias)).append(',');
		f.append(AuditoriaRed.num(toneladasEnProcesoPrevias)).append(',');
		f.append(AuditoriaRed.num(toneladasReservadasPrevias)).append(',');
		f.append(AuditoriaRed.num(toneladasPendientesAsignar)).append(',');
		f.append(AuditoriaRed.ent(contenedoresPendientesEstimados)).append(',');
		f.append(AuditoriaRed.num(diaConocimiento)).append(',');
		f.append(AuditoriaRed.num(diaAperturaRetiro)).append(',');
		f.append(AuditoriaRed.num(diaCutoff)).append(',');
		f.append(AuditoriaRed.num(diasHastaCutoff)).append(',');
		f.append(AuditoriaRed.ent(cantidadOrigenesPrevios)).append(',');

		f.append(AuditoriaRed.txt(circuito)).append(',');
		f.append(AuditoriaRed.si(esCrossDock)).append(',');
		f.append(AuditoriaRed.txt(origenStock)).append(',');
		f.append(AuditoriaRed.txt(sitioEstiba)).append(',');
		f.append(AuditoriaRed.txt(destinoFinal)).append(',');
		f.append(AuditoriaRed.si(requiereFlotaProducto)).append(',');
		f.append(AuditoriaRed.si(requierePortacontenedor)).append(',');
		f.append(AuditoriaRed.si(requierePosicion)).append(',');
		f.append(AuditoriaRed.txt(tipoRecursoCapacidad)).append(',');
		f.append(AuditoriaRed.txt(ubicacionRecursoCapacidad)).append(',');

		f.append(AuditoriaRed.num(stockFisicoOrigenTn)).append(',');
		f.append(AuditoriaRed.num(stockLibreOrigenTn)).append(',');
		f.append(AuditoriaRed.num(stockReservadoOrigenTn)).append(',');
		f.append(AuditoriaRed.num(stockEnTransitoHaciaOrigenTn)).append(',');
		f.append(AuditoriaRed.num(espacioFisicoSitioTn)).append(',');
		f.append(AuditoriaRed.num(espacioEfectivoSitioTn)).append(',');
		f.append(AuditoriaRed.num(ocupacionSitioPct)).append(',');
		f.append(AuditoriaRed.ent(cupoCrossdockLibreCont)).append(',');
		f.append(AuditoriaRed.ent(posicionesDisponiblesAntesCutoff)).append(',');
		f.append(AuditoriaRed.num(flotaProductoDisponible)).append(',');
		f.append(AuditoriaRed.num(primeraSalidaFlota)).append(',');
		f.append(AuditoriaRed.num(ultimaLlegadaFlota)).append(',');
		f.append(AuditoriaRed.num(esperaFlotaDias)).append(',');
		f.append(AuditoriaRed.ent(portacontenedoresLibres)).append(',');
		f.append(AuditoriaRed.ent(portacontenedoresOcupados)).append(',');

		f.append(AuditoriaRed.num(toneladasSinRestriccionCapacidad)).append(',');
		f.append(AuditoriaRed.num(toneladasFactibles)).append(',');
		f.append(AuditoriaRed.ent(contenedoresFactibles)).append(',');
		f.append(AuditoriaRed.ent(contenedoresConCapacidad)).append(',');
		f.append(AuditoriaRed.ent(viajesProductoRequeridos)).append(',');
		f.append(AuditoriaRed.ent(viajesProductoFactibles)).append(',');
		f.append(AuditoriaRed.si(esAsignacionParcial)).append(',');
		f.append(AuditoriaRed.num(porcentajePedidoCubierto)).append(',');

		f.append(AuditoriaRed.num(horasFleteProducto)).append(',');
		f.append(AuditoriaRed.num(horasCargaEstiba)).append(',');
		f.append(AuditoriaRed.num(horasViajeVacio)).append(',');
		f.append(AuditoriaRed.num(horasViajeCargado)).append(',');
		f.append(AuditoriaRed.num(horasDescargaTerminal)).append(',');
		f.append(AuditoriaRed.num(horasConsolidacionTerminal)).append(',');
		f.append(AuditoriaRed.num(horasCicloFisicoTotal)).append(',');
		f.append(AuditoriaRed.num(diaEntregaEstimado)).append(',');
		f.append(AuditoriaRed.num(holguraEstimadaDias)).append(',');
		f.append(AuditoriaRed.si(llegaATiempoEstimado)).append(',');

		f.append(AuditoriaRed.num(costoFleteProducto)).append(',');
		f.append(AuditoriaRed.num(costoRoundTrip)).append(',');
		f.append(AuditoriaRed.num(costoEstiba)).append(',');
		f.append(AuditoriaRed.num(costoOut)).append(',');
		f.append(AuditoriaRed.num(costoThc)).append(',');
		f.append(AuditoriaRed.num(costoTerminal)).append(',');
		f.append(AuditoriaRed.num(costoDespachante)).append(',');
		f.append(AuditoriaRed.num(costoInHundido)).append(',');
		f.append(AuditoriaRed.num(costoAlmacenajeHundido)).append(',');
		f.append(AuditoriaRed.num(costoFleteHundido)).append(',');
		f.append(AuditoriaRed.num(costoHistorico)).append(',');
		f.append(AuditoriaRed.num(costoIncremental)).append(',');
		f.append(AuditoriaRed.num(costoEndToEnd)).append(',');
		f.append(AuditoriaRed.num(costoIncrementalUsdTn)).append(',');
		f.append(AuditoriaRed.num(costoEndToEndUsdTn)).append(',');
		f.append(AuditoriaRed.num(costoIncrementalUsdCont)).append(',');
		f.append(AuditoriaRed.num(costoUnitarioSinRestriccion)).append(',');

		f.append(AuditoriaRed.si(factible)).append(',');
		f.append(AuditoriaRed.ent(ordenRanking)).append(',');
		f.append(AuditoriaRed.txt(resultadoEjecucion)).append(',');
		f.append(AuditoriaRed.txt(codigoMotivo)).append(',');
		f.append(AuditoriaRed.txt(detalleMotivo)).append(',');
		f.append(AuditoriaRed.num(toneladasTomadas)).append(',');
		f.append(AuditoriaRed.num(costoElegidaUsdTn)).append(',');
		f.append(AuditoriaRed.num(diferenciaVsElegidaUsdTn)).append(',');
		f.append(AuditoriaRed.num(saldoPedidoAntes)).append(',');
		f.append(AuditoriaRed.num(saldoPedidoDespues)).append(',');
		f.append(AuditoriaRed.si(esMasBarataNoFactible)).append(',');

		f.append(AuditoriaRed.txt(AuditoriaRed.fecha(diaCampania))).append(',');
		f.append(AuditoriaRed.txt(AuditoriaRed.fecha(diaCutoff)));

		return f.toString();
	}
}
