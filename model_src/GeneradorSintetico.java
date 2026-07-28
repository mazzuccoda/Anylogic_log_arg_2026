// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Fase 1 del contrato de datos: genera las tablas de entrada sin leer ningun
 * archivo. La fase 2 (Excel) reemplaza esta clase por un importador que llena
 * exactamente las mismas tablas; la logica de negocio no cambia.
 *
 * Reproducible: todo el azar sale de la semilla del escenario.
 */
public class GeneradorSintetico implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private static final String PLANTA = "PLANTA";

	private static final String[] DEPOSITOS = { "FRINOA", "NORRY", "BOREAS", "RUTA9", "DODERO" };

	// Por deposito: capacidad jugo, cascara, aceite (tn)
	private static final double[][] CAPACIDAD = {
		{ 7000, 3500, 1000 },
		{ 6000, 4000, 1200 },
		{ 5000, 3000,  900 },
		{ 8000, 5000, 1500 },
		{ 4000, 2500,  700 }
	};

	// Por deposito: usd/tn/dia de jugo, cascara, aceite
	private static final double[][] STORAGE = {
		{ 0.45, 0.30, 0.65 },
		{ 0.48, 0.28, 0.62 },
		{ 0.40, 0.35, 0.70 },
		{ 0.52, 0.32, 0.60 },
		{ 0.43, 0.29, 0.68 }
	};

	// Por deposito: km a planta, a ZARATE y a T4
	private static final double[][] DISTANCIA = {
		{  50, 100, 140 },
		{  80, 120, 110 },
		{ 110, 150,  90 },
		{  35,  80, 160 },
		{  65, 130,  70 }
	};

	// Por deposito: usd/tn de flete a ZARATE y a T4
	private static final double[][] FLETE = {
		{  8, 11 },
		{  9,  9 },
		{ 11,  8 },
		{  7, 12 },
		{ 10,  7 }
	};

	private static final String[] TERMINALES = { "ZARATE", "T4" };

	// Km de la planta a ZARATE y a T4. Consolidar en planta ahorra el tramo
	// planta-deposito pero paga el tramo largo hasta el puerto.
	private static final double[] DISTANCIA_PLANTA_TERMINAL = { 130, 145 };

	// Usd/tn de flete de la planta a ZARATE y a T4.
	private static final double[] FLETE_PLANTA = { 12, 13 };

	// Operacion de la planta como sitio de estiba: velocidad de carga a granel,
	// velocidad de estiba y contenedores consolidados por dia. Supuesto temporal:
	// los valores del deposito de referencia hasta tener los reales de la planta.
	private static final double VELOCIDAD_CARGA_PLANTA = 50;
	private static final double VELOCIDAD_ESTIBA_PLANTA = 30;
	private static final double CONTENEDORES_DIA_PLANTA = 4;

	// Usd/tn de estiba en planta de jugo, cascara y aceite, con el mismo criterio.
	private static final double[] ESTIBA_PLANTA = { 9, 7, 14 };

	// Por terminal: capacidad diaria tn, velocidad descarga tn/h, velocidad consolidacion tn/h
	private static final double[][] TERMINAL_OPERACION = {
		{ 500, 60, 50 },
		{ 600, 70, 60 }
	};

	// Por deposito: velocidad de estiba tn/h, contenedores consolidados por dia y
	// operaciones de cross dock por dia
	private static final double[][] CONSOLIDACION_DEPOSITO = {
		{ 30, 4, 12 },
		{ 28, 4, 10 },
		{ 25, 3, 0 },
		{ 32, 6, 16 },
		{ 26, 3, 8 }
	};

	// Por deposito: usd/tn de cross dock de jugo, cascara, aceite. Es mas barato que
	// la estiba desde stock porque el producto no entra ni sale del almacenamiento.
	private static final double[][] CROSS_DOCK_DEPOSITO = {
		{ 7, 6, 12 },
		{ 8, 6, 11 },
		{ 7, 6, 13 },
		{ 9, 7, 10 },
		{ 7, 6, 12 }
	};

	// Por deposito: usd/tn de estiba de jugo, cascara, aceite. Consolidar en el
	// deposito es mas barato que en la terminal y ahorra la manipulacion a granel.
	private static final double[][] ESTIBA_DEPOSITO = {
		{ 9, 7, 14 },
		{ 10, 8, 13 },
		{ 8, 7, 15 },
		{ 11, 9, 12 },
		{ 9, 8, 14 }
	};

	// Por terminal: contenedores consolidados por dia
	private static final double[] CONSOLIDACION_TERMINAL = { 16, 24 };

	// Por terminal: usd/tn de consolidacion de jugo, cascara, aceite
	private static final double[][] CONSOLIDACION = {
		{ 12, 10, 18 },
		{ 15, 11, 16 }
	};

	// Capacidad de la planta y produccion diaria media por producto
	private static final double[] CAPACIDAD_PLANTA = { 5000, 1800, 1500 };
	private static final double[] PRODUCCION_MEDIA = { 100, 60, 8 };

	// Costo de oportunidad del frio propio por producto (usd/tn/dia). No se
	// factura, pero sin el la planta sale gratis y siempre gana la comparacion
	// contra cualquier deposito (ADR-049). Debajo del deposito mas barato.
	private static final double[] OPORTUNIDAD_PLANTA = { 0.25, 0.18, 0.35 };

	// Penalidad por tonelada y dia sobre la capacidad nominal. Cero por defecto:
	// la sobrecarga se mide en tn-dia y no se disfraza de plata (ADR-048).
	private static final double PENALIDAD_SOBRECARGA = 0;

	// Flete de producto planta -> deposito. Estaban cableados en Main como
	// costo_fijo_viaje, costo_km y costo_tn; ahora son la tarifa por viaje de la
	// tabla, con el mismo valor, para que la migracion no mueva ningun numero
	// (ADR-051). El componente por tonelada es la parte variable del contrato.
	private static final double COSTO_FIJO_VIAJE = 150;
	private static final double COSTO_KM = 1.2;
	private static final double COSTO_VARIABLE_TN = 2.0;

	private static final String TIPO_CAMION_GRANEL = "GRANEL_25";
	private static final String PROVEEDOR = "SINTETICO";

	// Tarifas reales cambian por mes (respuesta del negocio, C1): el generador
	// emite tramos mensuales con el mismo valor, de modo que la resolucion por dia
	// queda ejercitada sin cambiar resultados. Seis tramos cubren la campania.
	private static final int DIAS_TRAMO = 31;
	private static final int TRAMOS = 6;

	// El ultimo tramo queda abierto: una campania mas larga no deja huecos.
	private static final int SIN_FIN = 9999;

	// Espera de camion y portacontenedor: la estructura queda cargada y en cero
	// hasta que exista la franquicia real (C1, seccion 4.2).
	private static final double FRANQUICIA_HORAS = 0;
	private static final double ESPERA_USD_HORA = 0;

	private static final TipoProducto[] PRODUCTOS = { TipoProducto.JUGO, TipoProducto.CASCARA, TipoProducto.ACEITE };

	private static final TipoContenedor[] CONTENEDOR = {
		TipoContenedor.REEFER_40, TipoContenedor.DRY_HC_40, TipoContenedor.IMO_DRY_20
	};

	private static final double[] CAPACIDAD_CONTENEDOR = { 25, 25, 20 };

	// Tamano comercial del lote acumulativo por producto (tn): jugo, cascara, aceite.
	// El lote acumula produccion diaria hasta el objetivo y ahi se cierra (ADR-047).
	private static final double[] TON_OBJETIVO_LOTE = { 2000, 1200, 200 };

	/** Escenarios del barrido. Agregar uno es agregar un caso aca, no tocar el experimento. */
	public static final String[] ESCENARIOS = {
		"E-00", "E-01", "E-02", "E-03", "E-04", "E-05",
		"E-06", "E-07", "E-08", "E-09", "E-10", "E-11", "E-12", "E-13"
	};

	/**
	 * Caso base con la palanca del escenario aplicada encima. Todo lo que un
	 * escenario cambia esta aca, de modo que la corrida queda descripta por una fila.
	 */
	public static DatosEntrada.Escenario escenario(String idEscenario, long semilla) {

		DatosEntrada.Escenario e = new DatosEntrada.Escenario();
		e.idEscenario = idEscenario;
		e.semilla = semilla;
		e.duracionCampaniaDias = 183;
		e.variabilidadProduccion = 0.15;
		e.variabilidadDemanda = 0.20;
		e.pedidosPorCampania = 40;
		e.toneladasMediasPedido = 400;
		e.plazoPedidoDias = 15;
		e.camionesProducto = 3;
		e.camionesPortacontenedor = 4;
		e.capacidadCamionTn = 25;
		e.velocidadCamionKmh = 70;
		e.horasOperativasDia = 10;
		e.factorProduccion = 1;
		e.factorCapacidadPlanta = 1;
		e.factorCapacidadDeposito = 1;
		e.factorStorage = 1;
		e.ventanaDemanda = 1;
		e.habilitaCrossDock = false;
		e.deterministico = false;
		e.estrategiaConsolidacion = "CONSOLIDACION_DEPOSITO";
		e.clienteDefault = "GENERICO";
		e.calidadDefault = "ESTANDAR";
		e.umbralAlertaPct = 85;
		e.umbralSobrecargaPct = 105;
		e.umbralObjetivoPct = 50;
		e.diasForecast = 7;
		e.politicaFrioPropio = "FLEXIBLE";

		if (idEscenario.equals("E-00")) {
			return e;                                    // caso base
		} else if (idEscenario.equals("E-01")) {
			e.camionesProducto = 1;                      // flota reducida
			e.camionesPortacontenedor = 1;
		} else if (idEscenario.equals("E-02")) {
			e.camionesProducto = 6;                      // flota ampliada
			e.camionesPortacontenedor = 8;
		} else if (idEscenario.equals("E-03")) {
			e.factorCapacidadDeposito = 0.5;             // depositos a la mitad
		} else if (idEscenario.equals("E-04")) {
			e.factorCapacidadDeposito = 2;               // depositos al doble
		} else if (idEscenario.equals("E-05")) {
			e.habilitaCrossDock = true;                  // cross dock
		} else if (idEscenario.equals("E-06")) {
			e.factorProduccion = 1.3;                    // campania alta
		} else if (idEscenario.equals("E-07")) {
			e.factorProduccion = 0.7;                    // campania baja
		} else if (idEscenario.equals("E-08")) {
			e.ventanaDemanda = 0.5;                      // demanda concentrada
		} else if (idEscenario.equals("E-09")) {
			e.variabilidadProduccion = 0;                // deterministico
			e.variabilidadDemanda = 0;
			e.deterministico = true;
		} else if (idEscenario.equals("E-10")) {
			e.factorStorage = 2;                         // almacenaje caro
		} else if (idEscenario.equals("E-11")) {
			e.estrategiaConsolidacion = "CONSOLIDACION_TERMINAL";
		} else if (idEscenario.equals("E-12")) {
			e.politicaFrioPropio = "REACTIVA";           // vaciar la planta apenas se activa
		} else if (idEscenario.equals("E-13")) {
			e.estrategiaConsolidacion = "CONSOLIDACION_PLANTA";
		} else {
			throw new RuntimeException("Escenario no definido: " + idEscenario);
		}

		return e;
	}

	public static DatosEntrada generar(String idEscenario, long semilla) {

		DatosEntrada datos = new DatosEntrada();

		DatosEntrada.Escenario escenario = escenario(idEscenario, semilla);
		datos.escenario = escenario;

		cargarMaestros(datos);

		java.util.Random rnd = new java.util.Random(semilla);
		generarProduccion(datos, rnd);
		generarPedidos(datos, rnd);

		return datos;
	}

	private static void cargarMaestros(DatosEntrada datos) {

		DatosEntrada.Escenario escenario = datos.escenario;

		datos.ubicaciones.add(new DatosEntrada.Ubicacion(PLANTA, "PLANTA", true,
			VELOCIDAD_CARGA_PLANTA, 0, VELOCIDAD_ESTIBA_PLANTA, 0, CONTENEDORES_DIA_PLANTA, 0));

		for (int i = 0; i < PRODUCTOS.length; i++) {
			datos.productos.add(new DatosEntrada.Producto(
				PRODUCTOS[i], CONTENEDOR[i], CAPACIDAD_CONTENEDOR[i], TON_OBJETIVO_LOTE[i]));
			datos.capacidades.add(new DatosEntrada.Capacidad(
				PLANTA, PRODUCTOS[i], CAPACIDAD_PLANTA[i] * escenario.factorCapacidadPlanta));

			// El frio propio no se factura (storage, in y out en cero) pero cuesta
			// ocuparlo. El cross dock no es un servicio de la planta: se carga con la
			// tarifa de estiba para que una consulta indebida no salga gratis.
			tarifaSitio(datos, PLANTA, PRODUCTOS[i], 0, 0, 0, OPORTUNIDAD_PLANTA[i],
				PENALIDAD_SOBRECARGA, ESTIBA_PLANTA[i], ESTIBA_PLANTA[i], 0, 0, 0);
		}

		for (int d = 0; d < DEPOSITOS.length; d++) {

			datos.ubicaciones.add(new DatosEntrada.Ubicacion(
				DEPOSITOS[d], "DEPOSITO", true, 50, 0, CONSOLIDACION_DEPOSITO[d][0], 0,
				CONSOLIDACION_DEPOSITO[d][1], CONSOLIDACION_DEPOSITO[d][2]));
			datos.distancias.add(new DatosEntrada.Distancia(PLANTA, DEPOSITOS[d], DISTANCIA[d][0]));

			for (int p = 0; p < PRODUCTOS.length; p++) {
				datos.capacidades.add(new DatosEntrada.Capacidad(
					DEPOSITOS[d], PRODUCTOS[p], CAPACIDAD[d][p] * escenario.factorCapacidadDeposito));

				// IN y OUT en cero: el deposito de hoy solo cobra almacenaje y estiba.
				// La columna existe para poder cargar el contrato real sin tocar codigo.
				tarifaSitio(datos, DEPOSITOS[d], PRODUCTOS[p], 0,
					STORAGE[d][p] * escenario.factorStorage, 0, 0, PENALIDAD_SOBRECARGA,
					ESTIBA_DEPOSITO[d][p], CROSS_DOCK_DEPOSITO[d][p], 0, 0, 0);
			}

			// Flete a granel planta -> deposito, cobrado por viaje.
			fleteViaje(datos, PLANTA, DEPOSITOS[d], DISTANCIA[d][0], escenario.capacidadCamionTn);
		}

		for (int t = 0; t < TERMINALES.length; t++) {

			datos.ubicaciones.add(new DatosEntrada.Ubicacion(
				TERMINALES[t], "TERMINAL", true, 0,
				TERMINAL_OPERACION[t][1], TERMINAL_OPERACION[t][2], TERMINAL_OPERACION[t][0],
				CONSOLIDACION_TERMINAL[t], 0));

			datos.distancias.add(
				new DatosEntrada.Distancia(PLANTA, TERMINALES[t], DISTANCIA_PLANTA_TERMINAL[t]));

			for (int p = 0; p < PRODUCTOS.length; p++) {
				// THC, costo terminal y despachante quedan en cero: son tarifas por
				// contenedor que todavia no tenemos y que se cobran en C3 (ADR-051).
				tarifaSitio(datos, TERMINALES[t], PRODUCTOS[p], 0, 0, 0, 0, 0,
					CONSOLIDACION[t][p], CONSOLIDACION[t][p], 0, 0, 0);

				// Flete de producto a granel hasta la terminal, en usd/tn.
				fleteTonelada(datos, PLANTA, TERMINALES[t], PRODUCTOS[p], FLETE_PLANTA[t],
					escenario.capacidadCamionTn);
			}

			// Round trip del portacontenedor terminal -> sitio -> terminal. El valor
			// heredado es el que Main cobraba con la formula cableada; C3 lo reemplaza
			// por la tarifa del proveedor.
			roundTrip(datos, TERMINALES[t], PLANTA, DISTANCIA_PLANTA_TERMINAL[t]);

			for (int d = 0; d < DEPOSITOS.length; d++) {

				datos.distancias.add(
					new DatosEntrada.Distancia(DEPOSITOS[d], TERMINALES[t], DISTANCIA[d][t + 1]));

				roundTrip(datos, TERMINALES[t], DEPOSITOS[d], DISTANCIA[d][t + 1]);

				for (int p = 0; p < PRODUCTOS.length; p++) {
					// El flete producto no depende del producto en los datos de
					// hoy; la tabla igual lo desagrega porque el Excel real si lo hace.
					fleteTonelada(datos, DEPOSITOS[d], TERMINALES[t], PRODUCTOS[p], FLETE[d][t],
						escenario.capacidadCamionTn);
				}
			}
		}

		for (DatosEntrada.Ubicacion u : datos.ubicaciones) {
			espera(datos, "CAMION_PRODUCTO", u.idUbicacion);
			espera(datos, "PORTACONTENEDOR", u.idUbicacion);
		}
	}

	/** Primer dia del tramo mensual. El ultimo queda abierto para cubrir la campania. */
	private static int desde(int tramo) {
		return tramo * DIAS_TRAMO;
	}

	private static int hasta(int tramo) {
		return tramo == TRAMOS - 1 ? SIN_FIN : (tramo + 1) * DIAS_TRAMO - 1;
	}

	private static void tarifaSitio(DatosEntrada datos, String idUbicacion, TipoProducto producto,
			double inUsdTn, double storageUsdTnDia, double outUsdTn, double oportunidadUsdTnDia,
			double penalidadUsdTnDia, double consolidacionUsdTn, double crossDockUsdTn,
			double thcUsdContenedor, double costoTerminalUsdContenedor, double despachanteUsdContenedor) {

		for (int tramo = 0; tramo < TRAMOS; tramo++) {
			datos.tarifasSitio.add(new DatosEntrada.TarifaSitio(idUbicacion, producto, inUsdTn,
				storageUsdTnDia, outUsdTn, oportunidadUsdTnDia, penalidadUsdTnDia,
				consolidacionUsdTn, DatosEntrada.Unidad.USD_TN,
				crossDockUsdTn, DatosEntrada.Unidad.USD_TN,
				thcUsdContenedor, costoTerminalUsdContenedor,
				despachanteUsdContenedor, DatosEntrada.Unidad.USD_CONTENEDOR,
				PROVEEDOR, desde(tramo), hasta(tramo), true));
		}
	}

	private static void fleteViaje(DatosEntrada datos, String origen, String destino, double km,
			double capacidadCamionTn) {

		// Mismo valor que la formula cableada: fijo mas kilometraje por viaje, mas la
		// parte variable por tonelada.
		double porViaje = COSTO_FIJO_VIAJE + km * COSTO_KM;

		for (int p = 0; p < PRODUCTOS.length; p++) {
			for (int tramo = 0; tramo < TRAMOS; tramo++) {
				datos.tarifasFlete.add(new DatosEntrada.TarifaFlete(origen, destino, PRODUCTOS[p],
					TIPO_CAMION_GRANEL, capacidadCamionTn, DatosEntrada.Unidad.USD_VIAJE, porViaje,
					COSTO_VARIABLE_TN, PROVEEDOR, desde(tramo), hasta(tramo), true));
			}
		}
	}

	private static void fleteTonelada(DatosEntrada datos, String origen, String destino,
			TipoProducto producto, double usdTn, double capacidadCamionTn) {

		for (int tramo = 0; tramo < TRAMOS; tramo++) {
			datos.tarifasFlete.add(new DatosEntrada.TarifaFlete(origen, destino, producto,
				TIPO_CAMION_GRANEL, capacidadCamionTn, DatosEntrada.Unidad.USD_TN, usdTn, 0,
				PROVEEDOR, desde(tramo), hasta(tramo), true));
		}
	}

	private static void roundTrip(DatosEntrada datos, String terminal, String sitio, double km) {

		// Valor heredado de la formula cableada de Main: ida y vuelta del tramo.
		double porContenedor = COSTO_FIJO_VIAJE + km * 2 * COSTO_KM;

		for (int c = 0; c < CONTENEDOR.length; c++) {
			for (int tramo = 0; tramo < TRAMOS; tramo++) {
				datos.tarifasRoundTrip.add(new DatosEntrada.TarifaRoundTrip(terminal, sitio,
					CONTENEDOR[c], porContenedor, FRANQUICIA_HORAS, ESPERA_USD_HORA,
					PROVEEDOR, desde(tramo), hasta(tramo), true));
			}
		}
	}

	private static void espera(DatosEntrada datos, String tipoRecurso, String idUbicacion) {
		for (int tramo = 0; tramo < TRAMOS; tramo++) {
			datos.tarifasEspera.add(new DatosEntrada.TarifaEspera(tipoRecurso, idUbicacion,
				FRANQUICIA_HORAS, ESPERA_USD_HORA, PROVEEDOR, desde(tramo), hasta(tramo), true));
		}
	}

	private static void generarProduccion(DatosEntrada datos, java.util.Random rnd) {

		double variabilidad = datos.escenario.variabilidadProduccion;

		for (int dia = 0; dia <= datos.escenario.duracionCampaniaDias; dia++) {
			for (int p = 0; p < PRODUCTOS.length; p++) {

				double media = PRODUCCION_MEDIA[p] * datos.escenario.factorProduccion;
				double ruido = datos.escenario.deterministico ? 0 : rnd.nextGaussian();
				double toneladas = media * (1 + variabilidad * ruido);

				datos.produccionPlan.add(
					new DatosEntrada.ProduccionPlan(dia, PRODUCTOS[p], redondear(Math.max(0, toneladas))));
			}
		}
	}

	private static void generarPedidos(DatosEntrada datos, java.util.Random rnd) {

		DatosEntrada.Escenario escenario = datos.escenario;

		int primerDia = 30;
		int ultimoDia = Math.max(primerDia, escenario.duracionCampaniaDias - escenario.plazoPedidoDias);

		// Demanda concentrada: los mismos pedidos llegan en una fraccion del horizonte.
		ultimoDia = primerDia + (int) Math.round((ultimoDia - primerDia) * escenario.ventanaDemanda);

		int cantidad = escenario.pedidosPorCampania;
		boolean fijo = escenario.deterministico;

		for (int i = 1; i <= cantidad; i++) {

			// En el caso deterministico los pedidos se reparten parejos en la ventana
			// y la mezcla sigue la misma proporcion, pero sin sortearla.
			double posicion = (i - 0.5) / cantidad;

			int diaLlegada = fijo
					? primerDia + (int) Math.round(posicion * (ultimoDia - primerDia))
					: primerDia + rnd.nextInt(Math.max(1, ultimoDia - primerDia + 1));

			int plazo = fijo
					? escenario.plazoPedidoDias + escenario.plazoPedidoDias / 2
					: escenario.plazoPedidoDias + rnd.nextInt(escenario.plazoPedidoDias + 1);

			TipoProducto producto = PRODUCTOS[elegirProducto(fijo ? posicion : rnd.nextDouble())];

			double proporcion = PRODUCCION_MEDIA[indice(producto)] / PRODUCCION_MEDIA[0];
			double toneladas = escenario.toneladasMediasPedido * proporcion
					* (1 + escenario.variabilidadDemanda * (fijo ? 0 : rnd.nextGaussian()));

			String terminal = TERMINALES[fijo ? (i - 1) % TERMINALES.length
					: rnd.nextInt(TERMINALES.length)];

			datos.pedidoPlan.add(new DatosEntrada.PedidoPlan(
				String.format("P%03d", i),
				diaLlegada,
				diaLlegada + plazo,
				producto,
				redondear(Math.max(10, toneladas)),
				terminal));
		}
	}

	/**
	 * Redondea al gramo. El libro de Excel guarda 15-16 cifras significativas:
	 * sin esto, la misma campania corrida desde Excel y desde el generador se
	 * separa por el ultimo bit del double y deja de ser comparable.
	 */
	private static double redondear(double toneladas) {
		return Math.round(toneladas * 1e6) / 1e6;
	}

	/** Elige producto en proporcion a la produccion media, no uniformemente. */
	private static int elegirProducto(double posicion) {

		double total = 0;
		for (int p = 0; p < PRODUCTOS.length; p++) {
			total += PRODUCCION_MEDIA[p];
		}

		double corte = posicion * total;
		double acumulado = 0;

		for (int p = 0; p < PRODUCTOS.length; p++) {
			acumulado += PRODUCCION_MEDIA[p];
			if (corte <= acumulado) {
				return p;
			}
		}

		return PRODUCTOS.length - 1;
	}

	private static int indice(TipoProducto producto) {
		for (int p = 0; p < PRODUCTOS.length; p++) {
			if (PRODUCTOS[p] == producto) {
				return p;
			}
		}
		throw new RuntimeException("Producto desconocido: " + producto);
	}
}
