/**
 * Vuelca las tablas de un escenario sintetico en bloques TSV, para que
 * tools/generar_excel_ejemplo.py arme el libro de ejemplo con los mismos datos
 * que produce el modelo. No es parte del modelo: se compila con el espejo de
 * model_src/.
 *
 * Los nombres de hoja y de columna deben coincidir con los que lee
 * ImportadorExcel; si se desincronizan, la importacion del libro de ejemplo
 * falla y el error aparece al validar.
 */
public class VolcarDatos {

	public static void main(String[] args) {
		String idEscenario = args.length > 0 ? args[0] : "E-00";
		long semilla = args.length > 1 ? Long.parseLong(args[1]) : 1L;

		DatosEntrada d = GeneradorSintetico.generar(idEscenario, semilla);

		// El libro trae la fila de todos los escenarios del barrido, para que sirva
		// de plantilla completa; el resto de las hojas es el maestro de E-00.
		hoja("Escenario", "id_escenario\tduracion_campania_dias\tsemilla_base"
				+ "\tvariabilidad_produccion\tvariabilidad_demanda\tpedidos_por_campania"
				+ "\ttoneladas_medias_pedido\tplazo_pedido_dias\tcamiones_producto"
				+ "\tcamiones_portacontenedor\tcapacidad_camion_tn\tvelocidad_camion_kmh"
				+ "\thoras_operativas_dia"
				+ "\tfactor_produccion\tfactor_capacidad_planta\tfactor_capacidad_deposito"
				+ "\tfactor_storage\tventana_demanda\thabilita_cross_dock\tdeterministico"
				+ "\testrategia_consolidacion\tcliente_default\tcalidad_default"
				+ "\tumbral_alerta_pct\tumbral_sobrecarga_pct\tumbral_objetivo_pct\tdias_forecast"
				+ "\tpolitica_frio_propio\tpolitica_seleccion\tservicio_minimo_proyectado"
				+ "\tfactor_tarifa_flete\tfactor_tarifa_round_trip\tfactor_tarifa_cross_dock"
				+ "\tfactor_tarifa_terminal\tfactor_consolidacion_planta"
				+ "\tfactor_cupo_cross_dock\tfactor_capacidad_terminal"
				+ "\tdias_anticipacion_planificacion_default\tdias_anticipacion_retiro_default"
				+ "\tdias_entre_cutoff_y_etd_default\tpermite_reserva_antes_retiro"
				+ "\tpermite_transferencia_antes_retiro\tpermite_reserva_capacidad_futura\tpermite_fallback_politica_fija\texportar_diagnostico_capacidad"
				+ "\tpolitica_reprogramacion_buque"
				+ "\thabilita_flota_producto_multidiaria\tdias_max_programacion_flota");
		for (String id : GeneradorSintetico.ESCENARIOS) {
			DatosEntrada.Escenario e = GeneradorSintetico.escenario(id, semilla);
			System.out.println(e.idEscenario
					+ "\t" + e.duracionCampaniaDias
					+ "\t" + e.semilla
					+ "\t" + e.variabilidadProduccion
					+ "\t" + e.variabilidadDemanda
					+ "\t" + e.pedidosPorCampania
					+ "\t" + e.toneladasMediasPedido
					+ "\t" + e.plazoPedidoDias
					+ "\t" + e.camionesProducto
					+ "\t" + e.camionesPortacontenedor
					+ "\t" + e.capacidadCamionTn
					+ "\t" + e.velocidadCamionKmh
					+ "\t" + e.horasOperativasDia
					+ "\t" + e.factorProduccion
					+ "\t" + e.factorCapacidadPlanta
					+ "\t" + e.factorCapacidadDeposito
					+ "\t" + e.factorStorage
					+ "\t" + e.ventanaDemanda
					+ "\t" + e.habilitaCrossDock
					+ "\t" + e.deterministico
					+ "\t" + e.estrategiaConsolidacion
					+ "\t" + e.clienteDefault
					+ "\t" + e.calidadDefault
					+ "\t" + e.umbralAlertaPct
					+ "\t" + e.umbralSobrecargaPct
					+ "\t" + e.umbralObjetivoPct
					+ "\t" + e.diasForecast
					+ "\t" + e.politicaFrioPropio
					+ "\t" + e.politicaSeleccion
					+ "\t" + e.servicioMinimoProyectado
					+ "\t" + e.factorTarifaFlete
					+ "\t" + e.factorTarifaRoundTrip
					+ "\t" + e.factorTarifaCrossDock
					+ "\t" + e.factorTarifaTerminal
					+ "\t" + e.factorConsolidacionPlanta
					+ "\t" + e.factorCupoCrossDock
					+ "\t" + e.factorCapacidadTerminal
					+ "\t" + e.diasAnticipacionPlanificacionDefault
					+ "\t" + e.diasAnticipacionRetiroDefault
					+ "\t" + e.diasEntreCutoffYEtdDefault
					+ "\t" + e.permiteReservaAntesRetiro
					+ "\t" + e.permiteTransferenciaAntesRetiro
					+ "\t" + e.permiteReservaCapacidadFutura
					+ "\t" + e.permiteFallbackPoliticaFija
					+ "\t" + e.exportarDiagnosticoCapacidad
					+ "\t" + e.politicaReprogramacionBuque
					+ "\t" + e.habilitaFlotaProductoMultidiaria
					+ "\t" + e.diasMaxProgramacionFlota);
		}

		hoja("Producto", "producto\ttipo_contenedor\tcapacidad_contenedor_tn\ttoneladas_objetivo_lote_tn");
		for (DatosEntrada.Producto p : d.productos) {
			System.out.println(p.producto + "\t" + p.tipoContenedor + "\t" + p.capacidadContenedorTn
					+ "\t" + p.toneladasObjetivoLoteTn);
		}

		hoja("Ubicacion", "id_ubicacion\ttipo\thabilitada\tvelocidad_carga_tn_hora"
				+ "\tvelocidad_descarga_tn_hora\tvelocidad_consolidacion_tn_hora\tcapacidad_diaria_tn"
				+ "\tcontenedores_por_dia\tposiciones_cross_dock");
		for (DatosEntrada.Ubicacion u : d.ubicaciones) {
			System.out.println(u.idUbicacion + "\t" + u.tipo + "\t" + u.habilitada
					+ "\t" + u.velocidadCargaTnHora + "\t" + u.velocidadDescargaTnHora
					+ "\t" + u.velocidadConsolidacionTnHora + "\t" + u.capacidadDiariaTn
					+ "\t" + u.contenedoresPorDia + "\t" + u.posicionesCrossDock);
		}

		hoja("CapacidadUbicacion", "id_ubicacion\tproducto\tcapacidad_tn");
		for (DatosEntrada.Capacidad c : d.capacidades) {
			System.out.println(c.idUbicacion + "\t" + c.producto + "\t" + c.capacidadTn);
		}

		hoja("Distancia", "origen\tdestino\tdistancia_km");
		for (DatosEntrada.Distancia x : d.distancias) {
			System.out.println(x.origen + "\t" + x.destino + "\t" + x.distanciaKm);
		}

		hoja("TarifaSitio", "id_ubicacion\tproducto\tin_usd_tn\tstorage_usd_tn_dia\tout_usd_tn"
				+ "\toportunidad_usd_tn_dia\tpenalidad_sobrecarga_usd_tn_dia"
				+ "\tconsolidacion_tarifa\tconsolidacion_unidad"
				+ "\tcross_dock_tarifa\tcross_dock_unidad"
				+ "\tthc_usd_contenedor\tcosto_terminal_usd_contenedor"
				+ "\tdespachante_tarifa\tdespachante_unidad"
				+ "\tproveedor\tvigencia_desde\tvigencia_hasta\thabilitada");
		for (DatosEntrada.TarifaSitio t : d.tarifasSitio) {
			System.out.println(t.idUbicacion + "\t" + t.producto
					+ "\t" + t.inUsdTn + "\t" + t.storageUsdTnDia + "\t" + t.outUsdTn
					+ "\t" + t.oportunidadUsdTnDia + "\t" + t.penalidadSobrecargaUsdTnDia
					+ "\t" + t.consolidacionTarifa + "\t" + t.consolidacionUnidad
					+ "\t" + t.crossDockTarifa + "\t" + t.crossDockUnidad
					+ "\t" + t.thcUsdContenedor + "\t" + t.costoTerminalUsdContenedor
					+ "\t" + t.despachanteTarifa + "\t" + t.despachanteUnidad
					+ "\t" + t.proveedor + "\t" + t.vigenciaDesde + "\t" + t.vigenciaHasta
					+ "\t" + t.habilitada);
		}

		hoja("TarifaFleteProducto", "origen\tdestino\tproducto\ttipo_camion\tcapacidad_camion_tn"
				+ "\tunidad\ttarifa\tvariable_usd_tn"
				+ "\tproveedor\tvigencia_desde\tvigencia_hasta\thabilitada");
		for (DatosEntrada.TarifaFlete t : d.tarifasFlete) {
			System.out.println(t.origen + "\t" + t.destino + "\t" + t.producto
					+ "\t" + t.tipoCamion + "\t" + t.capacidadCamionTn
					+ "\t" + t.unidad + "\t" + t.tarifa + "\t" + t.variableUsdTn
					+ "\t" + t.proveedor + "\t" + t.vigenciaDesde + "\t" + t.vigenciaHasta
					+ "\t" + t.habilitada);
		}

		hoja("TarifaRoundTrip", "terminal\tsitio\ttipo_contenedor\ttarifa_usd_contenedor"
				+ "\thoras_espera_incluidas\ttarifa_espera_usd_hora"
				+ "\tproveedor\tvigencia_desde\tvigencia_hasta\thabilitada");
		for (DatosEntrada.TarifaRoundTrip t : d.tarifasRoundTrip) {
			System.out.println(t.terminal + "\t" + t.sitio + "\t" + t.tipoContenedor
					+ "\t" + t.tarifaUsdContenedor
					+ "\t" + t.horasEsperaIncluidas + "\t" + t.tarifaEsperaUsdHora
					+ "\t" + t.proveedor + "\t" + t.vigenciaDesde + "\t" + t.vigenciaHasta
					+ "\t" + t.habilitada);
		}

		hoja("TarifaEspera", "tipo_recurso\tid_ubicacion\tfranquicia_horas\tusd_hora"
				+ "\tproveedor\tvigencia_desde\tvigencia_hasta\thabilitada");
		for (DatosEntrada.TarifaEspera t : d.tarifasEspera) {
			System.out.println(t.tipoRecurso + "\t" + t.idUbicacion
					+ "\t" + t.franquiciaHoras + "\t" + t.usdHora
					+ "\t" + t.proveedor + "\t" + t.vigenciaDesde + "\t" + t.vigenciaHasta
					+ "\t" + t.habilitada);
		}

		hoja("ProduccionPlan", "id_escenario\tdia\tproducto\tproduccion_tn");
		for (DatosEntrada.ProduccionPlan p : d.produccionPlan) {
			System.out.println(idEscenario + "\t" + p.dia + "\t" + p.producto + "\t" + p.produccionTn);
		}

		// Ventana maritima (ADR-059): la fecha comercial de siempre es el cut-off fisico
		// y las otras tres fechas van explicitas, no derivadas.
		hoja("PedidoPlan", "id_escenario\tcodigo_pedido\tproducto\ttoneladas_solicitadas"
				+ "\tterminal\tnaviera\tincoterm\tbuque\tviaje_buque"
				+ "\tdia_conocimiento\tdia_apertura_retiro_vacio\tdia_cutoff_fisico\tdia_etd");
		for (DatosEntrada.PedidoPlan p : d.pedidoPlan) {
			System.out.println(idEscenario + "\t" + p.codigoPedido
					+ "\t" + p.producto + "\t" + p.toneladasSolicitadas + "\t" + p.terminal
					+ "\t" + p.naviera + "\t" + p.incoterm
					+ "\t" + p.buque + "\t" + p.viajeBuque
					+ "\t" + p.diaConocimiento + "\t" + p.diaAperturaRetiroVacio
					+ "\t" + p.diaCutoffFisico + "\t" + p.diaETD);
		}

		// Stock inicial (ADR-057): la plantilla trae la hoja aunque el escenario sintetico
		// no tenga inventario previo, para que cargarlo sea llenar filas y no crear la hoja.
		hoja("StockInicial", "id_escenario\tid_stock\tcodigo_lote\tproducto\tid_ubicacion"
				+ "\ttoneladas\tdia_produccion\tdia_ingreso\tcliente\tcalidad");
		for (DatosEntrada.StockInicial s : d.stockInicial) {
			System.out.println(idEscenario + "\t" + s.idStock + "\t" + s.codigoLote
					+ "\t" + s.producto + "\t" + s.idUbicacion + "\t" + s.toneladas
					+ "\t" + s.diaProduccion + "\t" + s.diaIngreso
					+ "\t" + s.cliente + "\t" + s.calidad);
		}
	}

	private static void hoja(String nombre, String encabezados) {
		System.out.println("#HOJA\t" + nombre);
		System.out.println(encabezados);
	}
}
