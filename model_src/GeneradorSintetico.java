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

	// Por terminal: capacidad diaria tn, velocidad descarga tn/h, velocidad consolidacion tn/h
	private static final double[][] TERMINAL_OPERACION = {
		{ 500, 60, 50 },
		{ 600, 70, 60 }
	};

	// Por deposito: velocidad de estiba tn/h, posiciones de consolidacion,
	// contenedores por posicion y dia, y operaciones de cross dock por dia
	private static final double[][] CONSOLIDACION_DEPOSITO = {
		{ 30, 1, 4, 12 },
		{ 28, 1, 4, 10 },
		{ 25, 1, 3, 0 },
		{ 32, 2, 3, 16 },
		{ 26, 1, 3, 8 }
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

	// Por terminal: posiciones de consolidacion y contenedores por posicion y dia
	private static final double[][] CONSOLIDACION_TERMINAL = {
		{ 2, 8 },
		{ 3, 8 }
	};

	// Por terminal: usd/tn de consolidacion de jugo, cascara, aceite
	private static final double[][] CONSOLIDACION = {
		{ 12, 10, 18 },
		{ 15, 11, 16 }
	};

	// Capacidad de la planta y produccion diaria media por producto
	private static final double[] CAPACIDAD_PLANTA = { 5000, 1800, 1500 };
	private static final double[] PRODUCCION_MEDIA = { 100, 60, 8 };

	private static final TipoProducto[] PRODUCTOS = { TipoProducto.JUGO, TipoProducto.CASCARA, TipoProducto.ACEITE };

	private static final TipoContenedor[] CONTENEDOR = {
		TipoContenedor.REEFER_40, TipoContenedor.DRY_HC_40, TipoContenedor.IMO_DRY_20
	};

	private static final double[] CAPACIDAD_CONTENEDOR = { 25, 25, 20 };

	public static DatosEntrada generar(String idEscenario, int duracionCampaniaDias, long semilla,
			double variabilidadProduccion, double variabilidadDemanda,
			int pedidosPorCampania, double toneladasMediasPedido, int plazoPedidoDias) {

		DatosEntrada datos = new DatosEntrada();

		DatosEntrada.Escenario escenario = new DatosEntrada.Escenario();
		escenario.idEscenario = idEscenario;
		escenario.duracionCampaniaDias = duracionCampaniaDias;
		escenario.semilla = semilla;
		escenario.variabilidadProduccion = variabilidadProduccion;
		escenario.variabilidadDemanda = variabilidadDemanda;
		escenario.pedidosPorCampania = pedidosPorCampania;
		escenario.toneladasMediasPedido = toneladasMediasPedido;
		escenario.plazoPedidoDias = plazoPedidoDias;
		datos.escenario = escenario;

		cargarMaestros(datos);

		java.util.Random rnd = new java.util.Random(semilla);
		generarProduccion(datos, rnd);
		generarPedidos(datos, rnd);

		return datos;
	}

	private static void cargarMaestros(DatosEntrada datos) {

		datos.ubicaciones.add(new DatosEntrada.Ubicacion(PLANTA, "PLANTA", true, 0, 0, 0, 0, 0, 0, 0));

		for (int i = 0; i < PRODUCTOS.length; i++) {
			datos.productos.add(new DatosEntrada.Producto(PRODUCTOS[i], CONTENEDOR[i], CAPACIDAD_CONTENEDOR[i]));
			datos.capacidades.add(new DatosEntrada.Capacidad(PLANTA, PRODUCTOS[i], CAPACIDAD_PLANTA[i]));
		}

		for (int d = 0; d < DEPOSITOS.length; d++) {

			datos.ubicaciones.add(new DatosEntrada.Ubicacion(
				DEPOSITOS[d], "DEPOSITO", true, 50, 0, CONSOLIDACION_DEPOSITO[d][0], 0,
				CONSOLIDACION_DEPOSITO[d][1], CONSOLIDACION_DEPOSITO[d][2],
				CONSOLIDACION_DEPOSITO[d][3]));
			datos.distancias.add(new DatosEntrada.Distancia(PLANTA, DEPOSITOS[d], DISTANCIA[d][0]));

			for (int p = 0; p < PRODUCTOS.length; p++) {
				datos.capacidades.add(
					new DatosEntrada.Capacidad(DEPOSITOS[d], PRODUCTOS[p], CAPACIDAD[d][p]));
				datos.tarifasAlmacenamiento.add(
					new DatosEntrada.TarifaAlmacenamiento(DEPOSITOS[d], PRODUCTOS[p], STORAGE[d][p]));
				datos.tarifasServicioCarga.add(new DatosEntrada.TarifaServicioCarga(
					DEPOSITOS[d], PRODUCTOS[p], "CONSOLIDACION", ESTIBA_DEPOSITO[d][p]));
				datos.tarifasServicioCarga.add(new DatosEntrada.TarifaServicioCarga(
					DEPOSITOS[d], PRODUCTOS[p], "CROSS_DOCK", CROSS_DOCK_DEPOSITO[d][p]));
			}
		}

		for (int t = 0; t < TERMINALES.length; t++) {

			datos.ubicaciones.add(new DatosEntrada.Ubicacion(
				TERMINALES[t], "TERMINAL", true, 0,
				TERMINAL_OPERACION[t][1], TERMINAL_OPERACION[t][2], TERMINAL_OPERACION[t][0],
				CONSOLIDACION_TERMINAL[t][0], CONSOLIDACION_TERMINAL[t][1], 0));

			for (int p = 0; p < PRODUCTOS.length; p++) {
				datos.tarifasServicioCarga.add(new DatosEntrada.TarifaServicioCarga(
					TERMINALES[t], PRODUCTOS[p], "CONSOLIDACION", CONSOLIDACION[t][p]));
			}

			for (int d = 0; d < DEPOSITOS.length; d++) {

				datos.distancias.add(
					new DatosEntrada.Distancia(DEPOSITOS[d], TERMINALES[t], DISTANCIA[d][t + 1]));

				for (int p = 0; p < PRODUCTOS.length; p++) {
					// El flete producto no depende del producto en los datos de
					// hoy; la tabla igual lo desagrega porque el Excel real si lo hace.
					datos.tarifasFlete.add(
						new DatosEntrada.TarifaFlete(DEPOSITOS[d], TERMINALES[t], PRODUCTOS[p], FLETE[d][t]));
				}
			}
		}
	}

	private static void generarProduccion(DatosEntrada datos, java.util.Random rnd) {

		double variabilidad = datos.escenario.variabilidadProduccion;

		for (int dia = 0; dia <= datos.escenario.duracionCampaniaDias; dia++) {
			for (int p = 0; p < PRODUCTOS.length; p++) {

				double media = PRODUCCION_MEDIA[p];
				double toneladas = media * (1 + variabilidad * rnd.nextGaussian());

				datos.produccionPlan.add(
					new DatosEntrada.ProduccionPlan(dia, PRODUCTOS[p], Math.max(0, toneladas)));
			}
		}
	}

	private static void generarPedidos(DatosEntrada datos, java.util.Random rnd) {

		DatosEntrada.Escenario escenario = datos.escenario;

		int primerDia = 30;
		int ultimoDia = Math.max(primerDia, escenario.duracionCampaniaDias - escenario.plazoPedidoDias);

		for (int i = 1; i <= escenario.pedidosPorCampania; i++) {

			int diaLlegada = primerDia + rnd.nextInt(Math.max(1, ultimoDia - primerDia + 1));
			int plazo = escenario.plazoPedidoDias + rnd.nextInt(escenario.plazoPedidoDias + 1);

			TipoProducto producto = PRODUCTOS[elegirProducto(rnd)];

			double proporcion = PRODUCCION_MEDIA[indice(producto)] / PRODUCCION_MEDIA[0];
			double toneladas = escenario.toneladasMediasPedido * proporcion
					* (1 + escenario.variabilidadDemanda * rnd.nextGaussian());

			String terminal = TERMINALES[rnd.nextInt(TERMINALES.length)];

			datos.pedidoPlan.add(new DatosEntrada.PedidoPlan(
				String.format("P%03d", i),
				diaLlegada,
				diaLlegada + plazo,
				producto,
				Math.max(10, toneladas),
				terminal));
		}
	}

	/** Elige producto en proporcion a la produccion media, no uniformemente. */
	private static int elegirProducto(java.util.Random rnd) {

		double total = 0;
		for (int p = 0; p < PRODUCTOS.length; p++) {
			total += PRODUCCION_MEDIA[p];
		}

		double corte = rnd.nextDouble() * total;
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
