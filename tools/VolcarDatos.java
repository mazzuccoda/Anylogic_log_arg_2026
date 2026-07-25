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
				+ "\testrategia_consolidacion");
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
					+ "\t" + e.estrategiaConsolidacion);
		}

		hoja("Producto", "producto\ttipo_contenedor\tcapacidad_contenedor_tn");
		for (DatosEntrada.Producto p : d.productos) {
			System.out.println(p.producto + "\t" + p.tipoContenedor + "\t" + p.capacidadContenedorTn);
		}

		hoja("Ubicacion", "id_ubicacion\ttipo\thabilitada\tvelocidad_carga_tn_hora"
				+ "\tvelocidad_descarga_tn_hora\tvelocidad_consolidacion_tn_hora\tcapacidad_diaria_tn"
				+ "\tposiciones_consolidacion\tcontenedores_por_posicion_dia\tposiciones_cross_dock");
		for (DatosEntrada.Ubicacion u : d.ubicaciones) {
			System.out.println(u.idUbicacion + "\t" + u.tipo + "\t" + u.habilitada
					+ "\t" + u.velocidadCargaTnHora + "\t" + u.velocidadDescargaTnHora
					+ "\t" + u.velocidadConsolidacionTnHora + "\t" + u.capacidadDiariaTn
					+ "\t" + u.posicionesConsolidacion + "\t" + u.contenedoresPorPosicionDia
					+ "\t" + u.posicionesCrossDock);
		}

		hoja("CapacidadUbicacion", "id_ubicacion\tproducto\tcapacidad_tn");
		for (DatosEntrada.Capacidad c : d.capacidades) {
			System.out.println(c.idUbicacion + "\t" + c.producto + "\t" + c.capacidadTn);
		}

		hoja("Distancia", "origen\tdestino\tdistancia_km");
		for (DatosEntrada.Distancia x : d.distancias) {
			System.out.println(x.origen + "\t" + x.destino + "\t" + x.distanciaKm);
		}

		hoja("TarifaAlmacenamiento", "id_ubicacion\tproducto\tstorage_usd_tn_dia");
		for (DatosEntrada.TarifaAlmacenamiento t : d.tarifasAlmacenamiento) {
			System.out.println(t.idUbicacion + "\t" + t.producto + "\t" + t.storageUsdTnDia);
		}

		hoja("TarifaFleteProducto", "origen\tdestino\tproducto\ttarifa_usd_tn");
		for (DatosEntrada.TarifaFlete t : d.tarifasFlete) {
			System.out.println(t.origen + "\t" + t.destino + "\t" + t.producto + "\t" + t.tarifaUsdTn);
		}

		hoja("TarifaServicioCarga", "id_ubicacion\tproducto\ttipo_servicio\ttarifa_usd_tn");
		for (DatosEntrada.TarifaServicioCarga t : d.tarifasServicioCarga) {
			System.out.println(t.idUbicacion + "\t" + t.producto + "\t" + t.tipoServicio
					+ "\t" + t.tarifaUsdTn);
		}

		hoja("ProduccionPlan", "id_escenario\tdia\tproducto\tproduccion_tn");
		for (DatosEntrada.ProduccionPlan p : d.produccionPlan) {
			System.out.println(idEscenario + "\t" + p.dia + "\t" + p.producto + "\t" + p.produccionTn);
		}

		hoja("PedidoPlan", "id_escenario\tcodigo_pedido\tdia_llegada\tdia_limite"
				+ "\tproducto\ttoneladas_solicitadas\tterminal");
		for (DatosEntrada.PedidoPlan p : d.pedidoPlan) {
			System.out.println(idEscenario + "\t" + p.codigoPedido + "\t" + p.diaLlegada
					+ "\t" + p.diaLimite + "\t" + p.producto + "\t" + p.toneladasSolicitadas
					+ "\t" + p.terminal);
		}
	}

	private static void hoja(String nombre, String encabezados) {
		System.out.println("#HOJA\t" + nombre);
		System.out.println(encabezados);
	}
}
