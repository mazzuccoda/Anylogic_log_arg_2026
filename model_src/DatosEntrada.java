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
		public int camionesProducto;
		public double factorProduccion;
		public double factorCapacidadPlanta;
		public double factorCapacidadDeposito;
		public double factorStorage;
		public double ventanaDemanda;              // fraccion del horizonte con pedidos
		public boolean habilitaCrossDock;
		public boolean deterministico;             // sin sorteos: la replica no cambia nada
		public String estrategiaConsolidacion;     // CONSOLIDACION_DEPOSITO | CONSOLIDACION_TERMINAL
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
		public double posicionesConsolidacion;       // posiciones de estiba del sitio
		public double contenedoresPorPosicionDia;    // contenedores por posicion y dia
		public double posicionesCrossDock;           // operaciones de cross dock por dia

		public Ubicacion(String idUbicacion, String tipo, boolean habilitada,
				double velocidadCargaTnHora, double velocidadDescargaTnHora,
				double velocidadConsolidacionTnHora, double capacidadDiariaTn,
				double posicionesConsolidacion, double contenedoresPorPosicionDia,
				double posicionesCrossDock) {
			this.idUbicacion = idUbicacion;
			this.tipo = tipo;
			this.habilitada = habilitada;
			this.velocidadCargaTnHora = velocidadCargaTnHora;
			this.velocidadDescargaTnHora = velocidadDescargaTnHora;
			this.velocidadConsolidacionTnHora = velocidadConsolidacionTnHora;
			this.capacidadDiariaTn = capacidadDiariaTn;
			this.posicionesConsolidacion = posicionesConsolidacion;
			this.contenedoresPorPosicionDia = contenedoresPorPosicionDia;
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
		public TipoContenedor tipoContenedor;
		public double capacidadContenedorTn;

		public Producto(TipoProducto producto, TipoContenedor tipoContenedor, double capacidadContenedorTn) {
			this.producto = producto;
			this.tipoContenedor = tipoContenedor;
			this.capacidadContenedorTn = capacidadContenedorTn;
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

	public static class TarifaAlmacenamiento implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		public String idUbicacion;
		public TipoProducto producto;
		public double storageUsdTnDia;

		public TarifaAlmacenamiento(String idUbicacion, TipoProducto producto, double storageUsdTnDia) {
			this.idUbicacion = idUbicacion;
			this.producto = producto;
			this.storageUsdTnDia = storageUsdTnDia;
		}
	}

	public static class TarifaFlete implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		public String origen;
		public String destino;
		public TipoProducto producto;
		public double tarifaUsdTn;

		public TarifaFlete(String origen, String destino, TipoProducto producto, double tarifaUsdTn) {
			this.origen = origen;
			this.destino = destino;
			this.producto = producto;
			this.tarifaUsdTn = tarifaUsdTn;
		}
	}

	public static class TarifaServicioCarga implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		public String idUbicacion;
		public TipoProducto producto;
		public String tipoServicio;                  // CONSOLIDACION o CROSS_DOCK
		public double tarifaUsdTn;

		public TarifaServicioCarga(String idUbicacion, TipoProducto producto, String tipoServicio,
				double tarifaUsdTn) {
			this.idUbicacion = idUbicacion;
			this.producto = producto;
			this.tipoServicio = tipoServicio;
			this.tarifaUsdTn = tarifaUsdTn;
		}
	}

	public static class ProduccionPlan implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		public int dia;
		public TipoProducto producto;
		public double produccionTn;

		public ProduccionPlan(int dia, TipoProducto producto, double produccionTn) {
			this.dia = dia;
			this.producto = producto;
			this.produccionTn = produccionTn;
		}
	}

	public static class PedidoPlan implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		public String codigoPedido;
		public int diaLlegada;
		public int diaLimite;
		public TipoProducto producto;
		public double toneladasSolicitadas;
		public String terminal;

		public PedidoPlan(String codigoPedido, int diaLlegada, int diaLimite,
				TipoProducto producto, double toneladasSolicitadas, String terminal) {
			this.codigoPedido = codigoPedido;
			this.diaLlegada = diaLlegada;
			this.diaLimite = diaLimite;
			this.producto = producto;
			this.toneladasSolicitadas = toneladasSolicitadas;
			this.terminal = terminal;
		}
	}

	public Escenario escenario;
	public java.util.List<Ubicacion> ubicaciones = new java.util.ArrayList<Ubicacion>();
	public java.util.List<Capacidad> capacidades = new java.util.ArrayList<Capacidad>();
	public java.util.List<Producto> productos = new java.util.ArrayList<Producto>();
	public java.util.List<Distancia> distancias = new java.util.ArrayList<Distancia>();
	public java.util.List<TarifaAlmacenamiento> tarifasAlmacenamiento = new java.util.ArrayList<TarifaAlmacenamiento>();
	public java.util.List<TarifaFlete> tarifasFlete = new java.util.ArrayList<TarifaFlete>();
	public java.util.List<TarifaServicioCarga> tarifasServicioCarga = new java.util.ArrayList<TarifaServicioCarga>();
	public java.util.List<ProduccionPlan> produccionPlan = new java.util.ArrayList<ProduccionPlan>();
	public java.util.List<PedidoPlan> pedidoPlan = new java.util.ArrayList<PedidoPlan>();

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

	public double capacidadTn(String idUbicacion, TipoProducto producto) {
		for (Capacidad c : capacidades) {
			if (c.idUbicacion.equals(idUbicacion) && c.producto == producto) {
				return c.capacidadTn;
			}
		}
		throw new RuntimeException("Falta la capacidad de " + producto + " en " + idUbicacion
				+ " (tabla CapacidadUbicacion). Cero se carga explicitamente.");
	}

	public Producto producto(TipoProducto producto) {
		for (Producto p : productos) {
			if (p.producto == producto) {
				return p;
			}
		}
		throw new RuntimeException("Falta la fila de " + producto + " en la tabla Producto.");
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
		Ubicacion u = ubicacion(idUbicacion);
		return u.posicionesConsolidacion * u.contenedoresPorPosicionDia;
	}

	public double storageUsdTnDia(String idUbicacion, TipoProducto producto) {
		for (TarifaAlmacenamiento t : tarifasAlmacenamiento) {
			if (t.idUbicacion.equals(idUbicacion) && t.producto == producto) {
				return t.storageUsdTnDia;
			}
		}
		throw new RuntimeException("Falta la tarifa de almacenamiento de " + producto + " en " + idUbicacion
				+ " (tabla TarifaAlmacenamiento).");
	}

	public double fleteUsdTn(String origen, String destino, TipoProducto producto) {
		for (TarifaFlete t : tarifasFlete) {
			if (t.origen.equals(origen) && t.destino.equals(destino) && t.producto == producto) {
				return t.tarifaUsdTn;
			}
		}
		throw new RuntimeException("Falta la tarifa de flete " + origen + " -> " + destino + " de " + producto
				+ " (tabla TarifaFleteProducto).");
	}

	public double servicioCargaUsdTn(String idUbicacion, TipoProducto producto) {
		return servicioCargaUsdTn(idUbicacion, producto, "CONSOLIDACION");
	}

	public double servicioCargaUsdTn(String idUbicacion, TipoProducto producto, String tipoServicio) {
		for (TarifaServicioCarga t : tarifasServicioCarga) {
			if (t.idUbicacion.equals(idUbicacion) && t.producto == producto
					&& t.tipoServicio.equals(tipoServicio)) {
				return t.tarifaUsdTn;
			}
		}
		throw new RuntimeException("Falta la tarifa de " + tipoServicio + " de " + producto + " en "
				+ idUbicacion + " (tabla TarifaServicioCarga).");
	}

	/**
	 * Capacidad de cross dock del sitio en operaciones por dia: una operacion es un
	 * contenedor armado el mismo dia en que el producto llega (ADR-010, ADR-041).
	 */
	public double capacidadCrossDockDia(String idUbicacion) {
		return ubicacion(idUbicacion).posicionesCrossDock;
	}

	public double produccionDelDia(int dia, TipoProducto producto) {
		for (ProduccionPlan p : produccionPlan) {
			if (p.dia == dia && p.producto == producto) {
				return p.produccionTn;
			}
		}
		return 0;
	}

	public java.util.List<PedidoPlan> pedidosDelDia(int dia) {
		java.util.List<PedidoPlan> resultado = new java.util.ArrayList<PedidoPlan>();
		for (PedidoPlan p : pedidoPlan) {
			if (p.diaLlegada == dia) {
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

		if (escenario.factorProduccion <= 0 || escenario.factorCapacidadPlanta <= 0
				|| escenario.factorCapacidadDeposito <= 0 || escenario.factorStorage <= 0) {
			errores.add("Los factores del escenario deben ser > 0.");
		}

		if (escenario.ventanaDemanda <= 0 || escenario.ventanaDemanda > 1) {
			errores.add("ventana_demanda debe estar en (0, 1].");
		}

		if (!"CONSOLIDACION_DEPOSITO".equals(escenario.estrategiaConsolidacion)
				&& !"CONSOLIDACION_TERMINAL".equals(escenario.estrategiaConsolidacion)) {
			errores.add("estrategia_consolidacion invalida: " + escenario.estrategiaConsolidacion);
		}

		for (Producto p : productos) {
			if (p.capacidadContenedorTn <= 0) {
				errores.add("capacidad_contenedor_tn de " + p.producto + " debe ser > 0.");
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

		for (TarifaAlmacenamiento t : tarifasAlmacenamiento) {
			if (!existeUbicacion(t.idUbicacion)) {
				errores.add("TarifaAlmacenamiento referencia la ubicacion inexistente " + t.idUbicacion + ".");
			}
			if (t.storageUsdTnDia < 0) {
				errores.add("storage_usd_tn_dia negativa en " + t.idUbicacion + " / " + t.producto + ".");
			}
		}

		for (TarifaFlete t : tarifasFlete) {
			if (!existeUbicacion(t.origen) || !existeUbicacion(t.destino)) {
				errores.add("TarifaFleteProducto referencia una ubicacion inexistente: "
						+ t.origen + " -> " + t.destino + ".");
			}
			if (t.tarifaUsdTn <= 0) {
				errores.add("tarifa debe ser > 0 en " + t.origen + " -> " + t.destino + " / " + t.producto + ".");
			}
		}

		// Toda combinacion alcanzable deposito x terminal x producto necesita
		// almacenamiento, flete y consolidacion: si falta, la corrida abortaria a
		// mitad de campania en vez de al validar.
		for (Ubicacion deposito : ubicacionesDeTipo("DEPOSITO")) {
			for (TipoProducto producto : TipoProducto.values()) {
				errores.addAll(faltante(deposito.idUbicacion, producto));
				// Consolidar en el deposito necesita su propia tarifa de estiba, y
				// hacer cross dock ahi tiene su propia tarifa: no es el mismo servicio.
				try {
					servicioCargaUsdTn(deposito.idUbicacion, producto);
				} catch (RuntimeException e) {
					errores.add(e.getMessage());
				}
				if (deposito.posicionesCrossDock > 0) {
					try {
						servicioCargaUsdTn(deposito.idUbicacion, producto, "CROSS_DOCK");
					} catch (RuntimeException e) {
						errores.add(e.getMessage());
					}
				}
				for (Ubicacion terminal : ubicacionesDeTipo("TERMINAL")) {
					try {
						fleteUsdTn(deposito.idUbicacion, terminal.idUbicacion, producto);
						distanciaKm(deposito.idUbicacion, terminal.idUbicacion);
						servicioCargaUsdTn(terminal.idUbicacion, producto);
					} catch (RuntimeException e) {
						errores.add(e.getMessage());
					}
				}
			}
		}

		// Un sitio de consolidacion sin capacidad no retrasa el despacho: lo detiene
		// para siempre. Es un error de datos, no un escenario.
		for (Ubicacion u : ubicaciones) {
			if (u.tipo.equals("PLANTA")) {
				continue;
			}
			if (u.posicionesCrossDock < 0) {
				errores.add("posiciones_cross_dock no puede ser negativo en " + u.idUbicacion + ".");
			}

			if (u.posicionesConsolidacion < 0 || u.contenedoresPorPosicionDia < 0) {
				errores.add("posiciones_consolidacion y contenedores_por_posicion_dia no pueden ser"
						+ " negativos en " + u.idUbicacion + ".");
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
			if (p.diaLimite < p.diaLlegada) {
				errores.add("dia_limite < dia_llegada en el pedido " + p.codigoPedido + ".");
			}
			if (p.toneladasSolicitadas <= 0) {
				errores.add("toneladas_solicitadas <= 0 en el pedido " + p.codigoPedido + ".");
			}
			if (!existeUbicacion(p.terminal)) {
				errores.add("El pedido " + p.codigoPedido + " referencia la terminal inexistente " + p.terminal + ".");
			}
		}

		for (ProduccionPlan p : produccionPlan) {
			if (p.produccionTn < 0) {
				errores.add("produccion_tn negativa el dia " + p.dia + " para " + p.producto + ".");
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
			storageUsdTnDia(idDeposito, producto);
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
}
