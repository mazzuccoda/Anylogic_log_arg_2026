// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Tablas de entrada del modelo (contrato: docs/09_Definicion/Contrato_de_Datos.md).
 *
 * El generador sintetico y el futuro importador de Excel llenan estas mismas
 * tablas. La logica de negocio no lee valores cableados: lee esta clase.
 *
 * Una tarifa, capacidad o distancia faltante nunca vale cero: la consulta falla
 * con el dato que falta.
 */
public class DatosEntrada implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static class Escenario implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		public String idEscenario;
		public int duracionCampaniaDias;
		public long semilla;
		public double variabilidadProduccion;
		public double variabilidadDemanda;
		public int pedidosPorCampania;
		public double toneladasMediasPedido;
		public int plazoPedidoDias;

		// Palancas del barrido (ADR-032): la fila del escenario define la corrida
		// entera, asi que agregar un escenario es agregar una fila.
		public int camionesProducto;               // planta -> deposito (granel)
		public int camionesPortacontenedor;        // deposito -> terminal
		public double capacidadCamionTn;
		public double velocidadCamionKmh;
		public double horasOperativasDia;          // jornada del camion, define viajes por dia
		public double factorProduccion;
		public double factorCapacidadPlanta;
		public double factorCapacidadDeposito;
		public double factorStorage;
		public double ventanaDemanda;              // fraccion del horizonte con pedidos
		public boolean habilitaCrossDock;
		public boolean deterministico;             // sin sorteos: la replica no cambia nada
		public String estrategiaConsolidacion;     // CONSOLIDACION_PLANTA | CONSOLIDACION_DEPOSITO | CONSOLIDACION_TERMINAL
		public String clienteDefault;              // identidad comercial del lote (un solo valor por ahora)
		public String calidadDefault;              // calidad comercial del lote acumulativo

		// Politica de frio propio (ADR-048). La planta es el almacenamiento mas
		// barato, asi que la regla es retenerla llena y sacar solo lo que la
		// demanda pide o lo que el forecast dice que va a desbordar.
		public double umbralAlertaPct;             // % de la capacidad nominal que enciende la alerta
		public double umbralSobrecargaPct;         // % hasta el que la sobrecarga se considera aceptable
		public double umbralObjetivoPct;           // % al que la politica REACTIVA vacia la planta
		public int diasForecast;                   // horizonte de la transferencia preventiva
		public String politicaFrioPropio;          // FLEXIBLE | REACTIVA

		// Politica de seleccion de circuito (ADR-054). Las FIJA_* reproducen la
		// decision por escenario; las MENOR_COSTO_* dejan decidir al evaluador
		// pedido por pedido, que es lo que produce una estrategia mixta.
		public String politicaSeleccion;           // ver PoliticaSeleccion
		public double servicioMinimoProyectado;    // fraccion: alternativa que llega tarde se descarta si hay otra

		// Sensibilidad tarifaria (seccion 11 de la especificacion de costos). Se
		// aplican al resolver la tarifa y no al generarla, asi que valen igual
		// desde el Excel que desde el generador sintetico.
		public double factorTarifaFlete;
		public double factorTarifaRoundTrip;
		public double factorTarifaCrossDock;
		public double factorTarifaTerminal;        // thc, costo terminal y despachante
		public double factorConsolidacionPlanta;   // contenedores por dia del frio propio
		public double factorCupoCrossDock;         // operaciones de cross dock por dia
		public double factorCapacidadTerminal;     // contenedores por dia de la terminal

		// Ventana maritima (ADR-059). El pedido se conoce antes de poder ejecutarlo:
		// estos defaults solo se usan para derivar las fechas de un libro que todavia
		// trae la forma vieja (dia_llegada / dia_limite).
		public int diasAnticipacionPlanificacionDefault = 14;
		public int diasAnticipacionRetiroDefault = 7;
		public int diasEntreCutoffYEtdDefault = 1;
		public boolean permiteReservaAntesRetiro = true;
		public boolean permiteTransferenciaAntesRetiro = true;
		public boolean permiteReservaCapacidadFutura = true;

		/**
		 * Con politica fija y sin capacidad, que hacer con el saldo (ADR-060). Sin
		 * fallback el saldo queda sin cubrir, que es lo que mide la saturacion del
		 * circuito fijo; con fallback se evaluan las demas alternativas factibles.
		 */
		public boolean permiteFallbackPoliticaFija = false;

		/**
		 * Escribir el diagnostico de capacidad al cierre de la corrida (ADR-060). Apagado
		 * en el barrido: son millones de filas que nadie lee.
		 */
		public boolean exportarDiagnosticoCapacidad = false;
		public String politicaReprogramacionBuque = "CONTINUAR";   // CONTINUAR | CANCELAR

		/**
		 * Flota de producto como camiones discretos con viajes multidiarios (ADR-061). En
		 * false corre la logica anterior de capacidad diaria agregada (ADR-044) y es la
		 * regresion exacta: sin agenda, sin transito y con el movimiento instantaneo.
		 */
		public boolean habilitaFlotaProductoMultidiaria = true;

		/**
		 * Cuantos dias hacia adelante puede comprometer la agenda de camiones. Programar
		 * un viaje inmoviliza el stock que lleva: sin techo, el producto queda reservado
		 * para un viaje lejano y no sirve al pedido de manana.
		 */
		public double diasMaxProgramacionFlota = 2;
	}

	public static class Ubicacion implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		public String idUbicacion;
		public String tipo;                          // PLANTA, DEPOSITO o TERMINAL
		public boolean habilitada;
		public double velocidadCargaTnHora;
		public double velocidadDescargaTnHora;
		public double velocidadConsolidacionTnHora;
		public double capacidadDiariaTn;
		public double contenedoresPorDia;            // capacidad de consolidacion del sitio (ADR-048)
		public double posicionesCrossDock;           // operaciones de cross dock por dia

		public Ubicacion(String idUbicacion, String tipo, boolean habilitada,
				double velocidadCargaTnHora, double velocidadDescargaTnHora,
				double velocidadConsolidacionTnHora, double capacidadDiariaTn,
				double contenedoresPorDia, double posicionesCrossDock) {
			this.idUbicacion = idUbicacion;
			this.tipo = tipo;
			this.habilitada = habilitada;
			this.velocidadCargaTnHora = velocidadCargaTnHora;
			this.velocidadDescargaTnHora = velocidadDescargaTnHora;
			this.velocidadConsolidacionTnHora = velocidadConsolidacionTnHora;
			this.capacidadDiariaTn = capacidadDiariaTn;
			this.contenedoresPorDia = contenedoresPorDia;
			this.posicionesCrossDock = posicionesCrossDock;
		}
	}

	public static class Capacidad implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		public String idUbicacion;
		public TipoProducto producto;
		public double capacidadTn;

		public Capacidad(String idUbicacion, TipoProducto producto, double capacidadTn) {
			this.idUbicacion = idUbicacion;
			this.producto = producto;
			this.capacidadTn = capacidadTn;
		}
	}

	public static class Producto implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		public TipoProducto producto;
		public String material;                      // subdivision del producto (ADR-067); "" = sin distincion
		public TipoContenedor tipoContenedor;
		public double capacidadContenedorTn;
		public double toneladasObjetivoLoteTn;       // tamano comercial del lote acumulativo (0 = sin objetivo)

		public Producto(TipoProducto producto, String material, TipoContenedor tipoContenedor,
				double capacidadContenedorTn, double toneladasObjetivoLoteTn) {
			this.producto = producto;
			this.material = material;
			this.tipoContenedor = tipoContenedor;
			this.capacidadContenedorTn = capacidadContenedorTn;
			this.toneladasObjetivoLoteTn = toneladasObjetivoLoteTn;
		}
	}

	public static class Distancia implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		public String origen;
		public String destino;
		public double distanciaKm;

		public Distancia(String origen, String destino, double distanciaKm) {
			this.origen = origen;
			this.destino = destino;
			this.distanciaKm = distanciaKm;
		}
	}

	/**
	 * Unidad contractual de una tarifa. El importe no se deduce del nombre del
	 * campo: la unidad es un dato, porque el mismo concepto se contrata por viaje,
	 * por tonelada o por contenedor segun el proveedor (ADR-051).
	 */
	public enum Unidad {
		USD_VIAJE, USD_TN, USD_CONTENEDOR, USD_TN_DIA, USD_HORA, USD_OPERACION, USD_PEDIDO
	}

	/**
	 * Como se elige el circuito de cada pedido (seccion 10 de la especificacion de
	 * costos). Las FIJA_* son la politica por escenario que existia antes del
	 * evaluador; MANUAL queda declarada y sin uso porque el modelo corre sin
	 * intervencion durante la campania.
	 */
	public enum PoliticaSeleccion {
		FIJA_PLANTA, FIJA_DEPOSITO, FIJA_CROSS_DOCK_DEPOSITO, FIJA_CROSS_DOCK_TERMINAL,
		PRIORIDAD_FRIO_PROPIO, MENOR_COSTO_INCREMENTAL_FACTIBLE,
		MENOR_COSTO_END_TO_END_FACTIBLE, MANUAL
	}

	/** La politica del escenario, con el nombre de la columna en el mensaje de error. */
	public PoliticaSeleccion politicaSeleccion() {
		try {
			return PoliticaSeleccion.valueOf(escenario.politicaSeleccion);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("politica_seleccion invalida: " + escenario.politicaSeleccion);
		}
	}

	/**
	 * Lo que toda tarifa tiene ademas del importe: quien la cobra y desde cuando.
	 * Las tarifas reales cambian por mes, asi que una clave no tiene un valor unico
	 * y toda consulta resuelve por dia de campania (ADR-051).
	 */
	public static abstract class Tarifa implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		public String proveedor;
		public int vigenciaDesde;                    // dia de campania, inclusive
		public int vigenciaHasta;                    // dia de campania, inclusive
		public boolean habilitada;

		protected Tarifa(String proveedor, int vigenciaDesde, int vigenciaHasta, boolean habilitada) {
			this.proveedor = proveedor;
			this.vigenciaDesde = vigenciaDesde;
			this.vigenciaHasta = vigenciaHasta;
			this.habilitada = habilitada;
		}

		public boolean vigente(int dia) {
			return habilitada && dia >= vigenciaDesde && dia <= vigenciaHasta;
		}

		public String vigenciaTexto() {
			return "dias " + vigenciaDesde + "-" + vigenciaHasta;
		}
	}

	/**
	 * Transporte de producto a granel. El contrato real se cobra por viaje; la
	 * unidad queda explicita para poder cargar tambien tarifas por tonelada, y
	 * variableUsdTn cubre la parte variable de los contratos que tienen las dos.
	 */
	public static class TarifaFlete extends Tarifa {
		private static final long serialVersionUID = 1L;
		public String origen;
		public String destino;
		public TipoProducto producto;
		public String tipoCamion;
		public double capacidadCamionTn;
		public Unidad unidad;                        // USD_VIAJE o USD_TN
		public double tarifa;
		public double variableUsdTn;                 // componente por tonelada (0 si no aplica)

		public TarifaFlete(String origen, String destino, TipoProducto producto, String tipoCamion,
				double capacidadCamionTn, Unidad unidad, double tarifa, double variableUsdTn,
				String proveedor, int vigenciaDesde, int vigenciaHasta, boolean habilitada) {
			super(proveedor, vigenciaDesde, vigenciaHasta, habilitada);
			this.origen = origen;
			this.destino = destino;
			this.producto = producto;
			this.tipoCamion = tipoCamion;
			this.capacidadCamionTn = capacidadCamionTn;
			this.unidad = unidad;
			this.tarifa = tarifa;
			this.variableUsdTn = variableUsdTn;
		}
	}

	/**
	 * Ciclo del portacontenedor terminal -> sitio -> terminal. Es una tarifa por
	 * contenedor y no por kilometro: el proveedor cotiza el circuito completo, con
	 * horas de espera incluidas y un adicional por hora cuando se pasa (ADR-051).
	 */
	public static class TarifaRoundTrip extends Tarifa {
		private static final long serialVersionUID = 1L;
		public String terminal;
		public String sitio;
		public TipoContenedor tipoContenedor;
		public double tarifaUsdContenedor;
		public double horasEsperaIncluidas;
		public double tarifaEsperaUsdHora;

		public TarifaRoundTrip(String terminal, String sitio, TipoContenedor tipoContenedor,
				double tarifaUsdContenedor, double horasEsperaIncluidas, double tarifaEsperaUsdHora,
				String proveedor, int vigenciaDesde, int vigenciaHasta, boolean habilitada) {
			super(proveedor, vigenciaDesde, vigenciaHasta, habilitada);
			this.terminal = terminal;
			this.sitio = sitio;
			this.tipoContenedor = tipoContenedor;
			this.tarifaUsdContenedor = tarifaUsdContenedor;
			this.horasEsperaIncluidas = horasEsperaIncluidas;
			this.tarifaEsperaUsdHora = tarifaEsperaUsdHora;
		}
	}

	/**
	 * Todo lo que cobra un sitio por un producto: una fila por sitio, producto y
	 * vigencia. Planta, deposito y terminal comparten la tabla porque comparten los
	 * conceptos; lo que cambia son los que valen cero (la planta no factura
	 * almacenaje, el deposito no cobra THC).
	 */
	public static class TarifaSitio extends Tarifa {
		private static final long serialVersionUID = 1L;
		public String idUbicacion;
		public TipoProducto producto;
		public double inUsdTn;                       // ingreso al almacenamiento
		public double storageUsdTnDia;               // lo que se factura: entra al costo de caja
		public double outUsdTn;                      // egreso del almacenamiento
		public double oportunidadUsdTnDia;           // costo de oportunidad del frio propio (ADR-049)
		public double penalidadSobrecargaUsdTnDia;   // por tonelada y dia sobre la capacidad nominal
		public double consolidacionTarifa;
		public Unidad consolidacionUnidad;           // USD_CONTENEDOR o USD_TN
		public double crossDockTarifa;
		public Unidad crossDockUnidad;
		public double thcUsdContenedor;              // solo terminal
		public double costoTerminalUsdContenedor;    // solo terminal
		public double despachanteTarifa;             // solo terminal
		public Unidad despachanteUnidad;             // USD_CONTENEDOR o USD_PEDIDO

		public TarifaSitio(String idUbicacion, TipoProducto producto, double inUsdTn,
				double storageUsdTnDia, double outUsdTn, double oportunidadUsdTnDia,
				double penalidadSobrecargaUsdTnDia, double consolidacionTarifa,
				Unidad consolidacionUnidad, double crossDockTarifa, Unidad crossDockUnidad,
				double thcUsdContenedor, double costoTerminalUsdContenedor, double despachanteTarifa,
				Unidad despachanteUnidad, String proveedor, int vigenciaDesde, int vigenciaHasta,
				boolean habilitada) {
			super(proveedor, vigenciaDesde, vigenciaHasta, habilitada);
			this.idUbicacion = idUbicacion;
			this.producto = producto;
			this.inUsdTn = inUsdTn;
			this.storageUsdTnDia = storageUsdTnDia;
			this.outUsdTn = outUsdTn;
			this.oportunidadUsdTnDia = oportunidadUsdTnDia;
			this.penalidadSobrecargaUsdTnDia = penalidadSobrecargaUsdTnDia;
			this.consolidacionTarifa = consolidacionTarifa;
			this.consolidacionUnidad = consolidacionUnidad;
			this.crossDockTarifa = crossDockTarifa;
			this.crossDockUnidad = crossDockUnidad;
			this.thcUsdContenedor = thcUsdContenedor;
			this.costoTerminalUsdContenedor = costoTerminalUsdContenedor;
			this.despachanteTarifa = despachanteTarifa;
			this.despachanteUnidad = despachanteUnidad;
		}
	}

	/**
	 * Espera de un camion o de un portacontenedor por encima de la franquicia. En
	 * cero no cambia ningun numero: la estructura queda lista para cuando el dato
	 * exista (C1, seccion 4.2 de la especificacion de costos).
	 */
	public static class TarifaEspera extends Tarifa {
		private static final long serialVersionUID = 1L;
		public String tipoRecurso;                   // CAMION_PRODUCTO o PORTACONTENEDOR
		public String idUbicacion;
		public double franquiciaHoras;
		public double usdHora;

		public TarifaEspera(String tipoRecurso, String idUbicacion, double franquiciaHoras,
				double usdHora, String proveedor, int vigenciaDesde, int vigenciaHasta,
				boolean habilitada) {
			super(proveedor, vigenciaDesde, vigenciaHasta, habilitada);
			this.tipoRecurso = tipoRecurso;
			this.idUbicacion = idUbicacion;
			this.franquiciaHoras = franquiciaHoras;
			this.usdHora = usdHora;
		}
	}

	/**
	 * THC por naviera (ADR-068 seguimiento): el costo de manipuleo portuario que
	 * factura Gastos_THC no depende del sitio como TarifaSitio, sino del proveedor
	 * maritimo (la naviera) y del tipo de contenedor. Confirmado por el usuario:
	 * "gastos THC si depende de la naviera, porque es el costo de THC de la
	 * maritima".
	 */
	public static class TarifaThc extends Tarifa {
		private static final long serialVersionUID = 1L;
		public Naviera naviera;
		public TipoContenedor tipoContenedor;
		public double usdContenedor;

		public TarifaThc(Naviera naviera, TipoContenedor tipoContenedor, double usdContenedor,
				String proveedor, int vigenciaDesde, int vigenciaHasta, boolean habilitada) {
			super(proveedor, vigenciaDesde, vigenciaHasta, habilitada);
			this.naviera = naviera;
			this.tipoContenedor = tipoContenedor;
			this.usdContenedor = usdContenedor;
		}
	}

	public static class ProduccionPlan implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		public int dia;
		public TipoProducto producto;
		public String material = "";           // subdivision del producto (ADR-067)
		public double produccionTn;

		public ProduccionPlan(int dia, TipoProducto producto, String material, double produccionTn) {
			this.dia = dia;
			this.producto = producto;
			this.material = material;
			this.produccionTn = produccionTn;
		}
	}

	/**
	 * Pedido de exportacion con su ventana maritima (ADR-059). La fecha comercial
	 * de siempre es el cut-off fisico: el ultimo dia en que el contenedor cargado
	 * puede entrar a la terminal. El pedido se conoce antes (diaConocimiento) y el
	 * vacio recien puede retirarse desde diaAperturaRetiroVacio.
	 */
	public static class PedidoPlan implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		public String codigoPedido;
		public TipoProducto producto;
		public String material = "";           // subdivision del producto (ADR-067)
		public double toneladasSolicitadas;
		public String terminal;

		public int diaConocimiento;          // desde aqui el pedido existe y puede reservar
		public int diaAperturaRetiroVacio;   // desde aqui puede empezar el movimiento fisico
		public int diaCutoffFisico;          // contra esta fecha se mide el servicio
		public int diaETD;                   // salida estimada del buque, no es fecha de entrega

		public Naviera naviera = Naviera.SIN_DEFINIR;
		public String incoterm = "";
		public String buque = "";
		public String viajeBuque = "";

		// Deposito con el que el pedido ya cuenta en la realidad, aunque el costo no lo
		// haga ganar la comparacion (ADR-066). Vacio si no hay compromiso previo.
		public String depositoComprometido = "";

		// Alias legacy: el resto del modelo sigue leyendo estos dos nombres. No son
		// datos independientes, son vistas de las fechas de la ventana.
		public int diaLlegada;
		public int diaLimite;

		public PedidoPlan(String codigoPedido, int diaConocimiento, int diaAperturaRetiroVacio,
				int diaCutoffFisico, int diaETD, TipoProducto producto,
				double toneladasSolicitadas, String terminal) {
			this.codigoPedido = codigoPedido;
			this.producto = producto;
			this.toneladasSolicitadas = toneladasSolicitadas;
			this.terminal = terminal;
			this.diaConocimiento = diaConocimiento;
			this.diaAperturaRetiroVacio = diaAperturaRetiroVacio;
			this.diaCutoffFisico = diaCutoffFisico;
			this.diaETD = diaETD;
			this.diaLlegada = diaConocimiento;
			this.diaLimite = diaCutoffFisico;
		}

		/**
		 * Deriva la ventana de un libro que solo trae dia_llegada y dia_limite
		 * (VM-08). La derivacion nunca inventa conocimiento mas temprano que el que
		 * el libro declara: dia_llegada sigue siendo el dia en que el pedido existe,
		 * y la apertura se atrasa hasta la anticipacion de retiro del escenario.
		 */
		public static PedidoPlan desdeLegacy(String codigoPedido, int diaLlegada, int diaLimite,
				TipoProducto producto, double toneladasSolicitadas, String terminal,
				Escenario escenario) {
			int retiro = escenario == null ? 7 : escenario.diasAnticipacionRetiroDefault;
			int etd = escenario == null ? 1 : escenario.diasEntreCutoffYEtdDefault;
			int apertura = Math.max(diaLlegada, diaLimite - retiro);
			return new PedidoPlan(codigoPedido, diaLlegada, apertura, diaLimite,
					diaLimite + etd, producto, toneladasSolicitadas, terminal);
		}
	}

	/**
	 * Inventario que ya existe cuando arranca la campania. No es produccion ni
	 * transferencia: es una capa de inventario preexistente (ADR-057). Los dias son
	 * relativos al arranque y normalmente negativos, para que el FIFO por diaIngreso
	 * saque primero el producto historico.
	 */
	public static class StockInicial implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		public String idStock;
		public String codigoLote;
		public TipoProducto producto;
		public String material = "";           // subdivision del producto (ADR-067)
		public String idUbicacion;
		public double toneladas;
		public double diaProduccion;
		public double diaIngreso;
		public String cliente;
		public String calidad;

		public StockInicial(String idStock, String codigoLote, TipoProducto producto, String material,
				String idUbicacion, double toneladas, double diaProduccion, double diaIngreso,
				String cliente, String calidad) {
			this.idStock = idStock;
			this.codigoLote = codigoLote;
			this.producto = producto;
			this.material = material;
			this.idUbicacion = idUbicacion;
			this.toneladas = toneladas;
			this.diaProduccion = diaProduccion;
			this.diaIngreso = diaIngreso;
			this.cliente = cliente;
			this.calidad = calidad;
		}

		/** Identifica el lote fisico (ADR-057, ADR-067): dos materiales del mismo
		 * producto nunca son el mismo lote, igual que dos clientes o calidades. */
		public String claveLote() {
			return codigoLote + "|" + producto + "|" + material + "|" + cliente + "|" + calidad;
		}
	}

	public Escenario escenario;
	public java.util.List<Ubicacion> ubicaciones = new java.util.ArrayList<Ubicacion>();
	public java.util.List<Capacidad> capacidades = new java.util.ArrayList<Capacidad>();
	public java.util.List<Producto> productos = new java.util.ArrayList<Producto>();
	public java.util.List<Distancia> distancias = new java.util.ArrayList<Distancia>();
	public java.util.List<TarifaSitio> tarifasSitio = new java.util.ArrayList<TarifaSitio>();
	public java.util.List<TarifaFlete> tarifasFlete = new java.util.ArrayList<TarifaFlete>();
	public java.util.List<TarifaRoundTrip> tarifasRoundTrip = new java.util.ArrayList<TarifaRoundTrip>();
	public java.util.List<TarifaEspera> tarifasEspera = new java.util.ArrayList<TarifaEspera>();
	public java.util.List<TarifaThc> tarifasThc = new java.util.ArrayList<TarifaThc>();
	public java.util.List<ProduccionPlan> produccionPlan = new java.util.ArrayList<ProduccionPlan>();
	public java.util.List<PedidoPlan> pedidoPlan = new java.util.ArrayList<PedidoPlan>();
	public java.util.List<StockInicial> stockInicial = new java.util.ArrayList<StockInicial>();

	// ---------- consultas ----------

	public Ubicacion ubicacion(String idUbicacion) {
		for (Ubicacion u : ubicaciones) {
			if (u.idUbicacion.equals(idUbicacion)) {
				return u;
			}
		}
		throw new RuntimeException("Falta la ubicacion " + idUbicacion + " en la tabla Ubicacion.");
	}

	public boolean existeUbicacion(String idUbicacion) {
		for (Ubicacion u : ubicaciones) {
			if (u.idUbicacion.equals(idUbicacion)) {
				return true;
			}
		}
		return false;
	}

	public java.util.List<Ubicacion> ubicacionesDeTipo(String tipo) {
		java.util.List<Ubicacion> resultado = new java.util.ArrayList<Ubicacion>();
		for (Ubicacion u : ubicaciones) {
			if (u.tipo.equals(tipo)) {
				resultado.add(u);
			}
		}
		return resultado;
	}

	/**
	 * Capacidad declarada sin exigir la fila: cero cuando la tabla no la trae. Sirve
	 * para saber si un deposito puede almacenar un producto sin abortar por el dato
	 * que justamente dice que no puede (ADR-069).
	 */
	public double capacidadDeclaradaTn(String idUbicacion, TipoProducto producto) {
		for (Capacidad c : capacidades) {
			if (c.idUbicacion.equals(idUbicacion) && c.producto == producto) {
				return c.capacidadTn;
			}
		}
		return 0;
	}

	/**
	 * Un deposito con capacidad cero para un producto no puede almacenarlo nunca, asi
	 * que exigirle flete, round trip o almacenaje de ese producto es pedir un dato que
	 * la corrida no va a leer jamas (ADR-069). El maestro real usa esto: GRUPO_PAZ y
	 * CONTROL_UNION son depositos de CASCARA y no declaran capacidad de JUGO ni ACEITE.
	 */
	public boolean almacenaProducto(String idUbicacion, TipoProducto producto) {
		return capacidadDeclaradaTn(idUbicacion, producto) > 0;
	}

	public double capacidadTn(String idUbicacion, TipoProducto producto) {
		for (Capacidad c : capacidades) {
			if (c.idUbicacion.equals(idUbicacion) && c.producto == producto) {
				return c.capacidadTn;
			}
		}
		throw new RuntimeException("Falta la capacidad de " + producto + " en " + idUbicacion
				+ " (tabla CapacidadUbicacion). Cero se carga explicitamente.");
	}

	/** Fila de la tabla Producto por producto y material (ADR-067): dos materiales del
	 * mismo producto pueden tener contenedor y objetivo de lote distintos. */
	public Producto producto(TipoProducto producto, String material) {
		for (Producto p : productos) {
			if (p.producto == producto && p.material.equals(material)) {
				return p;
			}
		}
		throw new RuntimeException("Falta la fila de " + producto + "/" + material
				+ " en la tabla Producto.");
	}

	/** Materiales validos de un producto, en el orden en que aparecen en la tabla
	 * Producto. Lo usa Planta.producir() para no asumir un unico material por dia. */
	public java.util.List<String> materialesDe(TipoProducto producto) {
		java.util.List<String> resultado = new java.util.ArrayList<String>();
		for (Producto p : productos) {
			if (p.producto == producto && !resultado.contains(p.material)) {
				resultado.add(p.material);
			}
		}
		return resultado;
	}

	/**
	 * Tipo de contenedor "representativo" de un producto entero, para las
	 * validaciones de cobertura que todavia razonan a nivel producto (no
	 * material): TarifaRoundTrip y TarifaThc se resuelven por tipo de
	 * contenedor, y en los datos reales todos los materiales de un mismo
	 * producto comparten el mismo tipo de contenedor (ADR-067; solo cambia la
	 * capacidad, no el tipo). Si algun producto dejara de cumplirlo, esto lo
	 * dice explicitamente en vez de resolver un material al azar.
	 */
	public TipoContenedor tipoContenedorDe(TipoProducto producto) {
		java.util.List<String> materiales = materialesDe(producto);
		if (materiales.isEmpty()) {
			throw new RuntimeException("Falta la fila de " + producto + " en la tabla Producto.");
		}
		TipoContenedor tipo = producto(producto, materiales.get(0)).tipoContenedor;
		for (String material : materiales) {
			if (producto(producto, material).tipoContenedor != tipo) {
				throw new RuntimeException("El producto " + producto + " tiene mas de un tipo de"
						+ " contenedor segun el material (" + materiales + "); las validaciones de"
						+ " cobertura por producto necesitan uno solo.");
			}
		}
		return tipo;
	}

	/**
	 * Distancia del tramo en cualquiera de los dos sentidos, o -1 si el tramo no esta en la
	 * tabla. La tabla Distancia declara un solo sentido por par (PLANTA -> ZARATE, no la
	 * vuelta), asi que el retorno del camion tiene que poder leer la fila de la ida sin que
	 * un tramo faltante aborte la corrida (ADR-061).
	 */
	public double distanciaKmSimetrica(String origen, String destino) {
		for (Distancia d : distancias) {
			if (d.origen.equals(origen) && d.destino.equals(destino)) {
				return d.distanciaKm;
			}
		}
		for (Distancia d : distancias) {
			if (d.origen.equals(destino) && d.destino.equals(origen)) {
				return d.distanciaKm;
			}
		}
		return -1;
	}

	public double distanciaKm(String origen, String destino) {
		for (Distancia d : distancias) {
			if (d.origen.equals(origen) && d.destino.equals(destino)) {
				return d.distanciaKm;
			}
		}
		throw new RuntimeException("Falta la distancia " + origen + " -> " + destino + " en la tabla Distancia.");
	}

	/**
	 * Capacidad de consolidacion del sitio en contenedores por dia. El recurso se
	 * cuenta por dia, no por hora (definicion, seccion 3).
	 */
	public double capacidadConsolidacionDia(String idUbicacion) {
		return ubicacion(idUbicacion).contenedoresPorDia;
	}

	/**
	 * Resuelve la fila de tarifa del sitio vigente ese dia. Dos filas vigentes para
	 * la misma clave es un error de datos y no un empate a resolver por orden de
	 * carga: el modelo no elige tarifa.
	 */
	public TarifaSitio tarifaSitio(int dia, String idUbicacion, TipoProducto producto) {
		TarifaSitio encontrada = null;
		for (TarifaSitio t : tarifasSitio) {
			if (t.idUbicacion.equals(idUbicacion) && t.producto == producto && t.vigente(dia)) {
				if (encontrada != null) {
					throw new RuntimeException("Dos tarifas vigentes el dia " + dia + " para " + producto
							+ " en " + idUbicacion + " (" + encontrada.vigenciaTexto() + " y "
							+ t.vigenciaTexto() + ", tabla TarifaSitio).");
				}
				encontrada = t;
			}
		}
		if (encontrada == null) {
			throw new RuntimeException("Falta la tarifa de " + producto + " en " + idUbicacion
					+ " vigente el dia " + dia + " (tabla TarifaSitio).");
		}
		return encontrada;
	}

	public double storageUsdTnDia(int dia, String idUbicacion, TipoProducto producto) {
		return tarifaSitio(dia, idUbicacion, producto).storageUsdTnDia * escenario.factorStorage;
	}

	public double inUsdTn(int dia, String idUbicacion, TipoProducto producto) {
		return tarifaSitio(dia, idUbicacion, producto).inUsdTn;
	}

	public double outUsdTn(int dia, String idUbicacion, TipoProducto producto) {
		return tarifaSitio(dia, idUbicacion, producto).outUsdTn;
	}

	/**
	 * Costo de oportunidad del frio propio: no se factura, pero sin el la planta
	 * es gratis y toda comparacion de estrategias la elige siempre (ADR-049).
	 */
	public double oportunidadUsdTnDia(int dia, String idUbicacion, TipoProducto producto) {
		return tarifaSitio(dia, idUbicacion, producto).oportunidadUsdTnDia;
	}

	public double penalidadSobrecargaUsdTnDia(int dia, String idUbicacion, TipoProducto producto) {
		return tarifaSitio(dia, idUbicacion, producto).penalidadSobrecargaUsdTnDia;
	}

	/** Tarifa unitaria del flete, con el factor de sensibilidad del escenario. */
	public double fleteTarifaUnitaria(int dia, String origen, String destino,
			TipoProducto producto) {
		return tarifaFlete(dia, origen, destino, producto).tarifa * escenario.factorTarifaFlete;
	}

	public double fleteVariableUsdTn(int dia, String origen, String destino,
			TipoProducto producto) {
		return tarifaFlete(dia, origen, destino, producto).variableUsdTn
			* escenario.factorTarifaFlete;
	}

	public double consolidacionTarifa(int dia, String idUbicacion, TipoProducto producto) {
		return tarifaSitio(dia, idUbicacion, producto).consolidacionTarifa;
	}

	public double crossDockTarifa(int dia, String idUbicacion, TipoProducto producto) {
		return tarifaSitio(dia, idUbicacion, producto).crossDockTarifa
			* escenario.factorTarifaCrossDock;
	}

	public double despachanteTarifa(int dia, String idTerminal, TipoProducto producto) {
		return tarifaSitio(dia, idTerminal, producto).despachanteTarifa
			* escenario.factorTarifaTerminal;
	}

	public double thcUsdContenedor(int dia, String idTerminal, TipoProducto producto) {
		return tarifaSitio(dia, idTerminal, producto).thcUsdContenedor * escenario.factorTarifaTerminal;
	}

	/**
	 * THC por naviera (ADR-068 seguimiento). Tabla vacia (libro sin Gastos_THC, o
	 * el contrato original con thc_usd_contenedor directo en TarifaSitio) es un
	 * error de uso del llamador: quien invoque esto primero debe confirmar que
	 * datos.tarifasThc no esta vacia, y si no usar el thcUsdContenedor por sitio.
	 */
	public TarifaThc tarifaThc(int dia, Naviera naviera, TipoContenedor tipoContenedor) {
		TarifaThc encontrada = null;
		for (TarifaThc t : tarifasThc) {
			if (t.naviera == naviera && t.tipoContenedor == tipoContenedor && t.vigente(dia)) {
				if (encontrada != null) {
					throw new RuntimeException("Dos tarifas de THC vigentes el dia " + dia + " para "
							+ naviera + " en " + tipoContenedor + " (" + encontrada.vigenciaTexto() + " y "
							+ t.vigenciaTexto() + ", tabla TarifaThc).");
				}
				encontrada = t;
			}
		}
		if (encontrada == null) {
			throw new RuntimeException("Falta la tarifa de THC de " + naviera + " en " + tipoContenedor
					+ " vigente el dia " + dia + " (tabla TarifaThc).");
		}
		return encontrada;
	}

	public double thcUsdContenedor(int dia, Naviera naviera, TipoContenedor tipoContenedor) {
		return tarifaThc(dia, naviera, tipoContenedor).usdContenedor * escenario.factorTarifaTerminal;
	}

	public double costoTerminalUsdContenedor(int dia, String idTerminal, TipoProducto producto) {
		return tarifaSitio(dia, idTerminal, producto).costoTerminalUsdContenedor
			* escenario.factorTarifaTerminal;
	}

	/**
	 * Importe del servicio de estiba o de cross dock. La unidad decide si se cobra
	 * por tonelada o por contenedor, asi que cambiar de una a otra es cambiar el
	 * dato y no la formula (correccion 3 de la especificacion de costos).
	 */
	public double importeConsolidacion(int dia, String idUbicacion, TipoProducto producto,
			double toneladas, int contenedores) {
		TarifaSitio t = tarifaSitio(dia, idUbicacion, producto);
		return importe(t.consolidacionUnidad, t.consolidacionTarifa, toneladas, contenedores,
				"consolidacion de " + producto + " en " + idUbicacion);
	}

	public double importeCrossDock(int dia, String idUbicacion, TipoProducto producto,
			double toneladas, int contenedores) {
		TarifaSitio t = tarifaSitio(dia, idUbicacion, producto);
		return importe(t.crossDockUnidad, t.crossDockTarifa * escenario.factorTarifaCrossDock,
				toneladas, contenedores, "cross dock de " + producto + " en " + idUbicacion);
	}

	public double importeDespachante(int dia, String idTerminal, TipoProducto producto,
			int contenedores, int pedidos) {
		TarifaSitio t = tarifaSitio(dia, idTerminal, producto);
		if (t.despachanteUnidad == Unidad.USD_PEDIDO) {
			return t.despachanteTarifa * escenario.factorTarifaTerminal * pedidos;
		}
		return t.despachanteTarifa * escenario.factorTarifaTerminal * contenedores;
	}

	private double importe(Unidad unidad, double tarifa, double toneladas, int contenedores,
			String concepto) {
		if (unidad == Unidad.USD_CONTENEDOR) {
			return tarifa * contenedores;
		}
		if (unidad == Unidad.USD_TN) {
			return tarifa * toneladas;
		}
		throw new RuntimeException("Unidad " + unidad + " no valida para la " + concepto + ".");
	}

	/**
	 * Si existe una tarifa de flete vigente, sin abortar la corrida. Sirve para el
	 * diagnostico de descartes: querer saber si un destino es alcanzable no es lo mismo
	 * que cobrarle un flete que no esta en las tablas (ADR-056).
	 */
	public boolean hayTarifaFlete(int dia, String origen, String destino, TipoProducto producto) {
		try {
			tarifaFlete(dia, origen, destino, producto);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	/** Si existe una tarifa de sitio vigente, sin abortar la corrida. */
	public boolean hayTarifaSitio(int dia, String idUbicacion, TipoProducto producto) {
		try {
			tarifaSitio(dia, idUbicacion, producto);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	public TarifaFlete tarifaFlete(int dia, String origen, String destino, TipoProducto producto) {
		TarifaFlete encontrada = null;
		for (TarifaFlete t : tarifasFlete) {
			if (t.origen.equals(origen) && t.destino.equals(destino) && t.producto == producto
					&& t.vigente(dia)) {
				if (encontrada != null) {
					throw new RuntimeException("Dos tarifas de flete vigentes el dia " + dia + ": "
							+ origen + " -> " + destino + " de " + producto + " ("
							+ encontrada.vigenciaTexto() + " y " + t.vigenciaTexto()
							+ ", tabla TarifaFleteProducto).");
				}
				encontrada = t;
			}
		}
		if (encontrada == null) {
			throw new RuntimeException("Falta la tarifa de flete " + origen + " -> " + destino + " de "
					+ producto + " vigente el dia " + dia + " (tabla TarifaFleteProducto).");
		}
		return encontrada;
	}

	/**
	 * Importe del transporte de producto. Por viaje se cobra el viaje completo
	 * aunque el camion vaya a medio cargar, que es el contrato real; por tonelada se
	 * cobra lo que se movio.
	 */
	public double importeFlete(int dia, String origen, String destino, TipoProducto producto,
			double toneladas, int viajes) {
		TarifaFlete t = tarifaFlete(dia, origen, destino, producto);
		double base = t.unidad == Unidad.USD_VIAJE ? t.tarifa * viajes : t.tarifa * toneladas;
		return (base + t.variableUsdTn * toneladas) * escenario.factorTarifaFlete;
	}

	public TarifaRoundTrip tarifaRoundTrip(int dia, String idTerminal, String sitio,
			TipoContenedor tipoContenedor) {
		TarifaRoundTrip encontrada = null;
		for (TarifaRoundTrip t : tarifasRoundTrip) {
			if (t.terminal.equals(idTerminal) && t.sitio.equals(sitio)
					&& t.tipoContenedor == tipoContenedor && t.vigente(dia)) {
				if (encontrada != null) {
					throw new RuntimeException("Dos tarifas de round trip vigentes el dia " + dia + ": "
							+ idTerminal + " <-> " + sitio + " en " + tipoContenedor + " ("
							+ encontrada.vigenciaTexto() + " y " + t.vigenciaTexto()
							+ ", tabla TarifaRoundTrip).");
				}
				encontrada = t;
			}
		}
		if (encontrada == null) {
			throw new RuntimeException("Falta la tarifa de round trip " + idTerminal + " <-> " + sitio
					+ " en " + tipoContenedor + " vigente el dia " + dia + " (tabla TarifaRoundTrip).");
		}
		return encontrada;
	}

	public double roundTripUsdContenedor(int dia, String idTerminal, String sitio,
			TipoContenedor tipoContenedor) {
		return tarifaRoundTrip(dia, idTerminal, sitio, tipoContenedor).tarifaUsdContenedor
			* escenario.factorTarifaRoundTrip;
	}

	/** Fila de espera vigente del recurso en el sitio. Sin fila es error de datos. */
	public TarifaEspera tarifaEspera(int dia, String tipoRecurso, String idUbicacion) {
		TarifaEspera encontrada = null;
		for (TarifaEspera t : tarifasEspera) {
			if (t.tipoRecurso.equals(tipoRecurso) && t.idUbicacion.equals(idUbicacion)
					&& t.vigente(dia)) {
				if (encontrada != null) {
					throw new RuntimeException("Dos tarifas de espera vigentes el dia " + dia + " para "
							+ tipoRecurso + " en " + idUbicacion + " (tabla TarifaEspera).");
				}
				encontrada = t;
			}
		}
		if (encontrada == null) {
			throw new RuntimeException("Falta la tarifa de espera de " + tipoRecurso + " en "
					+ idUbicacion + " vigente el dia " + dia + " (tabla TarifaEspera).");
		}
		return encontrada;
	}

	/**
	 * Horas por encima de la franquicia del sitio: las unicas que se facturan. Se
	 * expone aparte del importe porque es la cantidad del cargo (ADR-052).
	 */
	public double horasEsperaFacturables(int dia, String tipoRecurso, String idUbicacion,
			double horas) {
		double excedente = horas - tarifaEspera(dia, tipoRecurso, idUbicacion).franquiciaHoras;
		return excedente <= 0 ? 0 : excedente;
	}

	/** Espera del recurso sobre la franquicia del sitio, en usd. */
	public double importeEspera(int dia, String tipoRecurso, String idUbicacion, double horas) {
		return horasEsperaFacturables(dia, tipoRecurso, idUbicacion, horas)
			* tarifaEspera(dia, tipoRecurso, idUbicacion).usdHora;
	}

	/**
	 * Capacidad de cross dock del sitio en operaciones por dia: una operacion es un
	 * contenedor armado el mismo dia en que el producto llega (ADR-010, ADR-041).
	 */
	public double capacidadCrossDockDia(String idUbicacion) {
		return ubicacion(idUbicacion).posicionesCrossDock;
	}

	/** Agregado entre materiales: lo usan los reportes y KPIs que no distinguen. */
	public double produccionDelDia(int dia, TipoProducto producto) {
		double total = 0;
		for (ProduccionPlan p : produccionPlan) {
			if (p.dia == dia && p.producto == producto) {
				total += p.produccionTn;
			}
		}
		return total;
	}

	/** Produccion de un material puntual (ADR-067): Planta.producir() la usa para no
	 * mezclar materiales del mismo producto en un mismo lote. */
	public double produccionDelDia(int dia, TipoProducto producto, String material) {
		for (ProduccionPlan p : produccionPlan) {
			if (p.dia == dia && p.producto == producto && p.material.equals(material)) {
				return p.produccionTn;
			}
		}
		return 0;
	}

	public double stockInicialTn() {
		double total = 0;
		for (StockInicial s : stockInicial) {
			total += s.toneladas;
		}
		return total;
	}

	public double stockInicialTn(TipoProducto producto) {
		double total = 0;
		for (StockInicial s : stockInicial) {
			if (s.producto == producto) {
				total += s.toneladas;
			}
		}
		return total;
	}

	public double stockInicialTn(String idUbicacion, TipoProducto producto) {
		double total = 0;
		for (StockInicial s : stockInicial) {
			if (s.producto == producto && s.idUbicacion.equals(idUbicacion)) {
				total += s.toneladas;
			}
		}
		return total;
	}

	public double produccionPlanificadaTn(TipoProducto producto) {
		double total = 0;
		for (ProduccionPlan p : produccionPlan) {
			if (p.producto == producto) {
				total += p.produccionTn;
			}
		}
		return total;
	}

	public double demandaPlanificadaTn(TipoProducto producto) {
		double total = 0;
		for (PedidoPlan p : pedidoPlan) {
			if (p.producto == producto) {
				total += p.toneladasSolicitadas;
			}
		}
		return total;
	}

	/** Lo que la campania no puede cubrir ni con stock inicial ni con produccion planificada. */
	public double deficitEstructuralTn(TipoProducto producto) {
		return Math.max(0,
				demandaPlanificadaTn(producto)
				- stockInicialTn(producto)
				- produccionPlanificadaTn(producto));
	}

	public java.util.List<PedidoPlan> pedidosDelDia(int dia) {
		java.util.List<PedidoPlan> resultado = new java.util.ArrayList<PedidoPlan>();
		for (PedidoPlan p : pedidoPlan) {
			// El pedido nace el dia en que se conoce, no el dia del cut-off (ADR-059).
			if (p.diaConocimiento == dia) {
				resultado.add(p);
			}
		}
		return resultado;
	}

	// ---------- validacion ----------

	/**
	 * Devuelve todos los errores juntos: corregir la entrada de a un error por
	 * corrida es inviable (contrato, seccion 7).
	 */
	public java.util.List<String> validar() {
		java.util.List<String> errores = new java.util.ArrayList<String>();

		if (escenario == null) {
			errores.add("Falta la fila de Escenario.");
			return errores;
		}

		if (escenario.duracionCampaniaDias <= 0) {
			errores.add("duracion_campania_dias debe ser > 0.");
		}

		if (escenario.camionesProducto <= 0) {
			errores.add("camiones_producto debe ser > 0.");
		}

		if (escenario.camionesPortacontenedor <= 0) {
			errores.add("camiones_portacontenedor debe ser > 0.");
		}

		if (escenario.capacidadCamionTn <= 0) {
			errores.add("capacidad_camion_tn debe ser > 0.");
		}

		if (escenario.velocidadCamionKmh <= 0) {
			errores.add("velocidad_camion_kmh debe ser > 0.");
		}

		if (escenario.horasOperativasDia <= 0 || escenario.horasOperativasDia > 24) {
			errores.add("horas_operativas_dia debe estar en (0, 24].");
		}

		if (escenario.factorProduccion <= 0 || escenario.factorCapacidadPlanta <= 0
				|| escenario.factorCapacidadDeposito <= 0 || escenario.factorStorage <= 0) {
			errores.add("Los factores del escenario deben ser > 0.");
		}

		if (escenario.ventanaDemanda <= 0 || escenario.ventanaDemanda > 1) {
			errores.add("ventana_demanda debe estar en (0, 1].");
		}

		if (!"CONSOLIDACION_PLANTA".equals(escenario.estrategiaConsolidacion)
				&& !"CONSOLIDACION_DEPOSITO".equals(escenario.estrategiaConsolidacion)
				&& !"CONSOLIDACION_TERMINAL".equals(escenario.estrategiaConsolidacion)) {
			errores.add("estrategia_consolidacion invalida: " + escenario.estrategiaConsolidacion);
		}

		if (escenario.clienteDefault == null || escenario.clienteDefault.trim().isEmpty()) {
			errores.add("cliente_default no puede estar vacio.");
		}

		if (escenario.calidadDefault == null || escenario.calidadDefault.trim().isEmpty()) {
			errores.add("calidad_default no puede estar vacio.");
		}

		if (escenario.umbralAlertaPct <= 0 || escenario.umbralAlertaPct > 100) {
			errores.add("umbral_alerta_pct debe estar en (0, 100].");
		}

		if (escenario.umbralSobrecargaPct < 100) {
			errores.add("umbral_sobrecarga_pct no puede ser menor que la capacidad nominal (100).");
		}

		if (escenario.umbralObjetivoPct < 0 || escenario.umbralObjetivoPct > escenario.umbralAlertaPct) {
			errores.add("umbral_objetivo_pct debe estar en [0, umbral_alerta_pct].");
		}

		if (escenario.diasForecast < 0) {
			errores.add("dias_forecast no puede ser negativo.");
		}

		if (escenario.diasAnticipacionRetiroDefault < 0
				|| escenario.diasAnticipacionPlanificacionDefault < 0
				|| escenario.diasEntreCutoffYEtdDefault < 0) {
			errores.add("Las anticipaciones de la ventana maritima no pueden ser negativas.");
		}

		if (escenario.diasAnticipacionPlanificacionDefault < escenario.diasAnticipacionRetiroDefault) {
			errores.add("dias_anticipacion_planificacion_default ("
					+ escenario.diasAnticipacionPlanificacionDefault
					+ ") no puede ser menor que dias_anticipacion_retiro_default ("
					+ escenario.diasAnticipacionRetiroDefault
					+ "): el pedido se conoce antes de poder retirar el vacio.");
		}

		if (!"CONTINUAR".equals(escenario.politicaReprogramacionBuque)
				&& !"CANCELAR".equals(escenario.politicaReprogramacionBuque)) {
			errores.add("politica_reprogramacion_buque invalida: "
					+ escenario.politicaReprogramacionBuque);
		}

		if (!"FLEXIBLE".equals(escenario.politicaFrioPropio)
				&& !"REACTIVA".equals(escenario.politicaFrioPropio)) {
			errores.add("politica_frio_propio invalida: " + escenario.politicaFrioPropio);
		}

		boolean politicaValida = false;
		for (PoliticaSeleccion p : PoliticaSeleccion.values()) {
			politicaValida = politicaValida || p.name().equals(escenario.politicaSeleccion);
		}
		if (!politicaValida) {
			errores.add("politica_seleccion invalida: " + escenario.politicaSeleccion);
		}

		if (escenario.factorTarifaFlete <= 0 || escenario.factorTarifaRoundTrip <= 0
				|| escenario.factorTarifaCrossDock <= 0 || escenario.factorTarifaTerminal <= 0
				|| escenario.factorConsolidacionPlanta <= 0
				|| escenario.factorCupoCrossDock <= 0
				|| escenario.factorCapacidadTerminal <= 0) {
			errores.add("Los factores de sensibilidad tarifaria deben ser > 0.");
		}

		if (escenario.servicioMinimoProyectado < 0 || escenario.servicioMinimoProyectado > 1) {
			errores.add("servicio_minimo_proyectado debe estar en [0, 1].");
		}

		for (Producto p : productos) {
			if (p.capacidadContenedorTn <= 0) {
				errores.add("capacidad_contenedor_tn de " + p.producto + " debe ser > 0.");
			}
			if (p.toneladasObjetivoLoteTn < 0) {
				errores.add("toneladas_objetivo_lote_tn de " + p.producto + " no puede ser negativa.");
			}
		}

		for (TipoProducto p : TipoProducto.values()) {
			boolean encontrado = false;
			for (Producto fila : productos) {
				encontrado = encontrado || fila.producto == p;
			}
			if (!encontrado) {
				errores.add("Falta la fila de " + p + " en la tabla Producto.");
			}
		}

		for (Capacidad c : capacidades) {
			if (!existeUbicacion(c.idUbicacion)) {
				errores.add("CapacidadUbicacion referencia la ubicacion inexistente " + c.idUbicacion + ".");
			}
			if (c.capacidadTn < 0) {
				errores.add("capacidad_tn negativa en " + c.idUbicacion + " / " + c.producto + ".");
			}
		}

		for (Distancia d : distancias) {
			if (!existeUbicacion(d.origen) || !existeUbicacion(d.destino)) {
				errores.add("Distancia referencia una ubicacion inexistente: " + d.origen + " -> " + d.destino + ".");
			}
			if (d.distanciaKm <= 0) {
				errores.add("distancia_km debe ser > 0 en " + d.origen + " -> " + d.destino + ".");
			}
		}

		for (TarifaSitio t : tarifasSitio) {
			if (!existeUbicacion(t.idUbicacion)) {
				errores.add("TarifaSitio referencia la ubicacion inexistente " + t.idUbicacion + ".");
			}
			String donde = t.idUbicacion + " / " + t.producto + " (" + t.vigenciaTexto() + ")";
			if (t.inUsdTn < 0 || t.storageUsdTnDia < 0 || t.outUsdTn < 0) {
				errores.add("in/storage/out no pueden ser negativos en " + donde + ".");
			}
			if (t.oportunidadUsdTnDia < 0 || t.penalidadSobrecargaUsdTnDia < 0) {
				errores.add("oportunidad y penalidad no pueden ser negativas en " + donde + ".");
			}
			if (t.consolidacionTarifa < 0 || t.crossDockTarifa < 0) {
				errores.add("consolidacion y cross dock no pueden ser negativas en " + donde + ".");
			}
			if (t.thcUsdContenedor < 0 || t.costoTerminalUsdContenedor < 0 || t.despachanteTarifa < 0) {
				errores.add("thc, costo terminal y despachante no pueden ser negativos en " + donde + ".");
			}
			// Una unidad que el modelo no sabe cobrar es un error de carga, no un cero.
			if (t.consolidacionUnidad != Unidad.USD_TN && t.consolidacionUnidad != Unidad.USD_CONTENEDOR) {
				errores.add("consolidacion_unidad debe ser USD_TN o USD_CONTENEDOR en " + donde + ".");
			}
			if (t.crossDockUnidad != Unidad.USD_TN && t.crossDockUnidad != Unidad.USD_CONTENEDOR) {
				errores.add("cross_dock_unidad debe ser USD_TN o USD_CONTENEDOR en " + donde + ".");
			}
			if (t.despachanteUnidad != Unidad.USD_CONTENEDOR && t.despachanteUnidad != Unidad.USD_PEDIDO) {
				errores.add("despachante_unidad debe ser USD_CONTENEDOR o USD_PEDIDO en " + donde + ".");
			}
			errores.addAll(vigenciaInvalida("TarifaSitio", donde, t));
		}

		for (TarifaFlete t : tarifasFlete) {
			String donde = t.origen + " -> " + t.destino + " / " + t.producto
					+ " (" + t.vigenciaTexto() + ")";
			if (!existeUbicacion(t.origen) || !existeUbicacion(t.destino)) {
				errores.add("TarifaFleteProducto referencia una ubicacion inexistente: " + donde + ".");
			}
			if (t.tarifa <= 0) {
				errores.add("La tarifa de flete debe ser > 0 en " + donde + ".");
			}
			if (t.variableUsdTn < 0) {
				errores.add("variable_usd_tn no puede ser negativa en " + donde + ".");
			}
			if (t.unidad != Unidad.USD_VIAJE && t.unidad != Unidad.USD_TN) {
				errores.add("unidad debe ser USD_VIAJE o USD_TN en " + donde + ".");
			}
			// Por viaje sin capacidad no se puede saber cuantos viajes hacen falta.
			if (t.unidad == Unidad.USD_VIAJE && t.capacidadCamionTn <= 0) {
				errores.add("capacidad_camion_tn debe ser > 0 para una tarifa por viaje en " + donde + ".");
			}
			errores.addAll(vigenciaInvalida("TarifaFleteProducto", donde, t));
		}

		for (TarifaRoundTrip t : tarifasRoundTrip) {
			String donde = t.terminal + " <-> " + t.sitio + " / " + t.tipoContenedor
					+ " (" + t.vigenciaTexto() + ")";
			if (!existeUbicacion(t.terminal) || !existeUbicacion(t.sitio)) {
				errores.add("TarifaRoundTrip referencia una ubicacion inexistente: " + donde + ".");
			}
			if (t.tarifaUsdContenedor <= 0) {
				errores.add("tarifa_usd_contenedor debe ser > 0 en " + donde + ".");
			}
			if (t.horasEsperaIncluidas < 0 || t.tarifaEsperaUsdHora < 0) {
				errores.add("La espera del round trip no puede ser negativa en " + donde + ".");
			}
			errores.addAll(vigenciaInvalida("TarifaRoundTrip", donde, t));
		}

		for (TarifaEspera t : tarifasEspera) {
			String donde = t.tipoRecurso + " en " + t.idUbicacion + " (" + t.vigenciaTexto() + ")";
			if (!existeUbicacion(t.idUbicacion)) {
				errores.add("TarifaEspera referencia la ubicacion inexistente " + t.idUbicacion + ".");
			}
			if (!t.tipoRecurso.equals("CAMION_PRODUCTO") && !t.tipoRecurso.equals("PORTACONTENEDOR")) {
				errores.add("tipo_recurso invalido en " + donde + ".");
			}
			if (t.franquiciaHoras < 0 || t.usdHora < 0) {
				errores.add("La espera no puede ser negativa en " + donde + ".");
			}
			errores.addAll(vigenciaInvalida("TarifaEspera", donde, t));
		}

		for (TarifaThc t : tarifasThc) {
			String donde = t.naviera + " en " + t.tipoContenedor + " (" + t.vigenciaTexto() + ")";
			if (t.usdContenedor < 0) {
				errores.add("usd_contenedor de THC no puede ser negativo en " + donde + ".");
			}
			errores.addAll(vigenciaInvalida("TarifaThc", donde, t));
		}

		// Stock inicial: identidad, ubicacion y fechas. La capacidad efectiva se valida
		// en Main.validarStockInicial(), que es donde los factores del escenario ya
		// estan aplicados a los agentes (ADR-057).
		java.util.Set<String> idsStock = new java.util.HashSet<String>();
		java.util.Map<String, StockInicial> loteInicial = new java.util.HashMap<String, StockInicial>();

		for (StockInicial s : stockInicial) {
			String donde = "StockInicial " + s.idStock;

			if (s.idStock == null || s.idStock.trim().isEmpty()) {
				errores.add("id_stock no puede estar vacio en StockInicial.");
			} else if (!idsStock.add(s.idStock)) {
				errores.add("id_stock duplicado en StockInicial: " + s.idStock + ".");
			}

			if (s.codigoLote == null || s.codigoLote.trim().isEmpty()) {
				errores.add("codigo_lote no puede estar vacio en " + donde + ".");
				continue;
			}

			StockInicial previa = loteInicial.get(s.codigoLote);

			if (previa == null) {
				loteInicial.put(s.codigoLote, s);
			} else if (previa.producto != s.producto) {
				errores.add("El lote " + s.codigoLote + " aparece con productos distintos ("
						+ previa.producto + " y " + s.producto + ") en StockInicial.");
			} else if (!previa.material.equals(s.material)) {
				errores.add("El lote " + s.codigoLote + " aparece con materiales distintos ("
						+ previa.material + " y " + s.material + ") en StockInicial (ADR-067).");
			} else if (!previa.cliente.equals(s.cliente) || !previa.calidad.equals(s.calidad)) {
				errores.add("El lote " + s.codigoLote + " aparece con cliente o calidad"
						+ " contradictorios en StockInicial.");
			}

			if (s.toneladas <= 0) {
				errores.add("toneladas debe ser > 0 en " + donde + ".");
			}

			if (!materialesDe(s.producto).contains(s.material)) {
				errores.add("El material '" + s.material + "' no existe en la tabla Producto"
						+ " para " + s.producto + " en " + donde + " (ADR-067).");
			}

			if (!existeUbicacion(s.idUbicacion)) {
				errores.add(donde + " referencia la ubicacion inexistente " + s.idUbicacion + ".");
			} else {
				Ubicacion u = ubicacion(s.idUbicacion);
				if (!u.habilitada) {
					errores.add(donde + " usa la ubicacion deshabilitada " + s.idUbicacion + ".");
				}
				if (!u.tipo.equals("PLANTA") && !u.tipo.equals("DEPOSITO")) {
					errores.add(donde + ": el stock inicial solo puede estar en PLANTA o DEPOSITO,"
							+ " no en " + s.idUbicacion + " (" + u.tipo + ").");
				}
			}

			if (s.diaProduccion > 0) {
				errores.add("dia_produccion debe ser <= 0 en " + donde + ".");
			}
			if (s.diaIngreso > 0) {
				errores.add("dia_ingreso debe ser <= 0 en " + donde + ".");
			}
			if (s.diaIngreso < s.diaProduccion) {
				errores.add("dia_ingreso no puede ser anterior a dia_produccion en " + donde + ".");
			}
		}

		errores.addAll(coberturaTarifas());

		// Toda combinacion alcanzable deposito x terminal x producto necesita
		// almacenamiento, flete y consolidacion: si falta, la corrida abortaria a
		// mitad de campania en vez de al validar.
		for (Ubicacion deposito : ubicacionesDeTipo("DEPOSITO")) {
			for (TipoProducto producto : TipoProducto.values()) {
				errores.addAll(faltante(deposito.idUbicacion, producto));
				for (Ubicacion terminal : ubicacionesDeTipo("TERMINAL")) {
					try {
						distanciaKm(deposito.idUbicacion, terminal.idUbicacion);
					} catch (RuntimeException e) {
						errores.add(e.getMessage());
					}
				}
			}
		}

		// Un sitio de consolidacion sin capacidad no retrasa el despacho: lo detiene
		// para siempre. Es un error de datos, no un escenario.
		for (TipoProducto producto : TipoProducto.values()) {
			try {
				capacidadTn("PLANTA", producto);
			} catch (RuntimeException e) {
				errores.add(e.getMessage());
			}
			for (Ubicacion terminal : ubicacionesDeTipo("TERMINAL")) {
				try {
					distanciaKm("PLANTA", terminal.idUbicacion);
				} catch (RuntimeException e) {
					errores.add(e.getMessage());
				}
			}
		}

		for (Ubicacion u : ubicaciones) {
			if (u.tipo.equals("TERMINAL") && u.velocidadDescargaTnHora <= 0) {
				errores.add("velocidad_descarga_tn_hora debe ser > 0 en " + u.idUbicacion + ".");
			}
			if (u.tipo.equals("PLANTA") && u.velocidadCargaTnHora <= 0) {
				errores.add("velocidad_carga_tn_hora debe ser > 0 en " + u.idUbicacion + ".");
			}
			if (u.posicionesCrossDock < 0) {
				errores.add("posiciones_cross_dock no puede ser negativo en " + u.idUbicacion + ".");
			}

			if (u.contenedoresPorDia < 0) {
				errores.add("contenedores_por_dia no puede ser negativo en " + u.idUbicacion + ".");
			}
			if (capacidadConsolidacionDia(u.idUbicacion) <= 0) {
				errores.add("La capacidad de consolidacion de " + u.idUbicacion
						+ " es cero: ningun contenedor podria salir de ahi.");
			}
			if (u.velocidadConsolidacionTnHora <= 0) {
				errores.add("velocidad_consolidacion_tn_hora debe ser > 0 en " + u.idUbicacion + ".");
			}
		}

		for (PedidoPlan p : pedidoPlan) {
			// La ventana maritima es una cadena ordenada (ADR-059, seccion 21 del MOD):
			// romperla no es un caso raro, es un dato mal cargado.
			if (!(p.diaConocimiento <= p.diaAperturaRetiroVacio
					&& p.diaAperturaRetiroVacio <= p.diaCutoffFisico
					&& p.diaCutoffFisico <= p.diaETD)) {
				errores.add("La ventana maritima del pedido " + p.codigoPedido
						+ " no respeta dia_conocimiento <= dia_apertura_retiro_vacio"
						+ " <= dia_cutoff_fisico <= dia_etd: "
						+ p.diaConocimiento + ", " + p.diaAperturaRetiroVacio + ", "
						+ p.diaCutoffFisico + ", " + p.diaETD + ".");
			}
			if (p.toneladasSolicitadas <= 0) {
				errores.add("toneladas_solicitadas <= 0 en el pedido " + p.codigoPedido + ".");
			}
			if (!existeUbicacion(p.terminal)) {
				errores.add("El pedido " + p.codigoPedido + " referencia la terminal inexistente " + p.terminal + ".");
			}
			if (!materialesDe(p.producto).contains(p.material)) {
				errores.add("El material '" + p.material + "' del pedido " + p.codigoPedido
						+ " no existe en la tabla Producto para " + p.producto + " (ADR-067).");
			}
		}

		for (ProduccionPlan p : produccionPlan) {
			if (p.produccionTn < 0) {
				errores.add("produccion_tn negativa el dia " + p.dia + " para " + p.producto + ".");
			}
			// ADR-067: si Producto tiene mas de un material para este producto, la fila
			// tiene que decir cual - "" deja de ser un material valido y produccionDelDia()
			// con material especifico devolveria 0 en silencio, no un error explicito.
			if (!materialesDe(p.producto).contains(p.material)) {
				errores.add("El material '" + p.material + "' en ProduccionPlan no existe en la"
						+ " tabla Producto para " + p.producto + ".");
			}
		}

		return errores;
	}

	private java.util.List<String> faltante(String idDeposito, TipoProducto producto) {
		java.util.List<String> errores = new java.util.ArrayList<String>();
		try {
			capacidadTn(idDeposito, producto);
		} catch (RuntimeException e) {
			errores.add(e.getMessage());
		}
		try {
			distanciaKm("PLANTA", idDeposito);
		} catch (RuntimeException e) {
			errores.add(e.getMessage());
		}
		return errores;
	}

	private java.util.List<String> vigenciaInvalida(String tabla, String donde, Tarifa t) {
		java.util.List<String> errores = new java.util.ArrayList<String>();
		if (t.vigenciaHasta < t.vigenciaDesde) {
			errores.add(tabla + ": vigencia_hasta < vigencia_desde en " + donde + ".");
		}
		if (t.proveedor == null || t.proveedor.trim().isEmpty()) {
			errores.add(tabla + ": proveedor vacio en " + donde + ".");
		}
		return errores;
	}

	/**
	 * Toda tarifa que la campania va a necesitar tiene que estar cubierta todos los
	 * dias por exactamente una fila habilitada. Con tarifas mensuales, un hueco de
	 * un dia aborta la corrida a mitad de campania y una superposicion la hace
	 * depender del orden de carga; las dos cosas son errores de datos.
	 */
	private java.util.List<String> coberturaTarifas() {
		java.util.List<String> errores = new java.util.ArrayList<String>();

		int dias = escenario.duracionCampaniaDias;
		java.util.List<Ubicacion> depositos = ubicacionesDeTipo("DEPOSITO");
		java.util.List<Ubicacion> terminales = ubicacionesDeTipo("TERMINAL");

		// THC por naviera (ADR-068 seguimiento) solo aplica si el libro trae
		// Gastos_THC; el contrato original sigue resolviendo por sitio via
		// TarifaSitio.thcUsdContenedor, ya cubierto por unaSola() de arriba.
		java.util.Set<Naviera> navierasUsadas = new java.util.LinkedHashSet<Naviera>();
		if (!tarifasThc.isEmpty()) {
			for (PedidoPlan p : pedidoPlan) {
				navierasUsadas.add(p.naviera);
			}
		}

		for (int dia = 0; dia <= dias; dia++) {
			for (TipoProducto producto : TipoProducto.values()) {

				// La planta es origen posible de despacho y sitio de estiba (ADR-050).
				errores.addAll(unaSola(dia, "PLANTA", producto));

				for (Naviera naviera : navierasUsadas) {
					errores.addAll(unaSolaThc(dia, naviera, tipoContenedorDe(producto)));
				}

				for (Ubicacion deposito : depositos) {
					if (!almacenaProducto(deposito.idUbicacion, producto)) {
						continue;
					}

					errores.addAll(unaSola(dia, deposito.idUbicacion, producto));
					errores.addAll(unaSolaFlete(dia, "PLANTA", deposito.idUbicacion, producto));
				}

				for (Ubicacion terminal : terminales) {
					errores.addAll(unaSola(dia, terminal.idUbicacion, producto));
					errores.addAll(unaSolaFlete(dia, "PLANTA", terminal.idUbicacion, producto));
					errores.addAll(unaSolaRoundTrip(dia, terminal.idUbicacion, "PLANTA", producto));

					for (Ubicacion deposito : depositos) {
						if (!almacenaProducto(deposito.idUbicacion, producto)) {
							continue;
						}

						errores.addAll(unaSolaFlete(dia, deposito.idUbicacion, terminal.idUbicacion,
								producto));
						errores.addAll(unaSolaRoundTrip(dia, terminal.idUbicacion, deposito.idUbicacion,
								producto));
					}
				}
			}

			// La espera es por recurso y sitio, no por producto.
			for (Ubicacion deposito : depositos) {
				errores.addAll(unaSolaEspera(dia, "PORTACONTENEDOR", deposito.idUbicacion));
				errores.addAll(unaSolaEspera(dia, "CAMION_PRODUCTO", deposito.idUbicacion));
			}
			errores.addAll(unaSolaEspera(dia, "PORTACONTENEDOR", "PLANTA"));
			errores.addAll(unaSolaEspera(dia, "CAMION_PRODUCTO", "PLANTA"));
			for (Ubicacion terminal : terminales) {
				errores.addAll(unaSolaEspera(dia, "PORTACONTENEDOR", terminal.idUbicacion));
				errores.addAll(unaSolaEspera(dia, "CAMION_PRODUCTO", terminal.idUbicacion));
			}

			// La coherencia se chequea recien cuando hay cobertura: sin fila vigente no
			// hay nada que comparar.
			if (errores.isEmpty()) {
				for (Ubicacion terminal : terminales) {
					for (TipoProducto producto : TipoProducto.values()) {
						TipoContenedor tipo = tipoContenedorDe(producto);
						errores.addAll(esperaCoherente(dia, terminal.idUbicacion, "PLANTA", tipo));
						for (Ubicacion deposito : depositos) {
							if (!almacenaProducto(deposito.idUbicacion, producto)) {
								continue;
							}

							errores.addAll(esperaCoherente(dia, terminal.idUbicacion,
									deposito.idUbicacion, tipo));
						}
					}
				}
			}

			// Los errores de cobertura se repiten todos los dias del hueco: con el
			// primer dia alcanza para corregir la fila.
			if (!errores.isEmpty()) {
				return errores;
			}
		}

		return errores;
	}

	private java.util.List<String> unaSola(int dia, String idUbicacion, TipoProducto producto) {
		java.util.List<String> errores = new java.util.ArrayList<String>();
		try {
			tarifaSitio(dia, idUbicacion, producto);
		} catch (RuntimeException e) {
			errores.add(e.getMessage());
		}
		return errores;
	}

	private java.util.List<String> unaSolaThc(int dia, Naviera naviera, TipoContenedor tipoContenedor) {
		java.util.List<String> errores = new java.util.ArrayList<String>();
		try {
			tarifaThc(dia, naviera, tipoContenedor);
		} catch (RuntimeException e) {
			errores.add(e.getMessage());
		}
		return errores;
	}

	private java.util.List<String> unaSolaFlete(int dia, String origen, String destino,
			TipoProducto producto) {
		java.util.List<String> errores = new java.util.ArrayList<String>();
		try {
			tarifaFlete(dia, origen, destino, producto);
		} catch (RuntimeException e) {
			errores.add(e.getMessage());
		}
		return errores;
	}

	private java.util.List<String> unaSolaRoundTrip(int dia, String idTerminal, String sitio,
			TipoProducto producto) {
		java.util.List<String> errores = new java.util.ArrayList<String>();
		try {
			tarifaRoundTrip(dia, idTerminal, sitio, tipoContenedorDe(producto));
		} catch (RuntimeException e) {
			errores.add(e.getMessage());
		}
		return errores;
	}

	private java.util.List<String> esperaCoherente(int dia, String idTerminal, String sitio,
			TipoContenedor tipoContenedor) {
		java.util.List<String> errores = new java.util.ArrayList<String>();
		TarifaRoundTrip ciclo = tarifaRoundTrip(dia, idTerminal, sitio, tipoContenedor);
		TarifaEspera espera = tarifaEspera(dia, "PORTACONTENEDOR", sitio);
		String donde = idTerminal + " - " + sitio + " - " + tipoContenedor + " el dia " + dia;
		if (Math.abs(ciclo.horasEsperaIncluidas - espera.franquiciaHoras) > 0.0001) {
			errores.add("La franquicia de espera del ciclo (" + ciclo.horasEsperaIncluidas
					+ " h) no coincide con la del sitio (" + espera.franquiciaHoras + " h) en "
					+ donde + ".");
		}
		if (Math.abs(ciclo.tarifaEsperaUsdHora - espera.usdHora) > 0.0001) {
			errores.add("La tarifa de espera del ciclo (" + ciclo.tarifaEsperaUsdHora
					+ " usd/h) no coincide con la del sitio (" + espera.usdHora + " usd/h) en "
					+ donde + ".");
		}
		return errores;
	}

	private java.util.List<String> unaSolaEspera(int dia, String tipoRecurso, String idUbicacion) {
		java.util.List<String> errores = new java.util.ArrayList<String>();
		try {
			tarifaEspera(dia, tipoRecurso, idUbicacion);
		} catch (RuntimeException e) {
			errores.add(e.getMessage());
		}
		return errores;
	}
}
