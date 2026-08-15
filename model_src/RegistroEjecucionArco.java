// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Un movimiento fisico completado de la red (ADR-064): una fila por arco recorrido.
 *
 * La fila se cierra al salir del arco, no se emite al entrar: recien ahi existen la
 * duracion real y el estado final. La duracion esperada es la que el modelo prometio
 * para ese arco, asi que la comparacion ex ante contra ex post es la resta.
 *
 * Las esperas son arcos: sin ESPERA_PORTACONTENEDOR y ESPERA_POSICION la suma de las
 * etapas no reconstruye el tiempo real de un envio (C-10), y son justamente las dos
 * esperas que explican el atraso.
 *
 * El almacenaje, el ingreso y el egreso de deposito no son arcos: son cargos y ya viven
 * en el registro de costos. Emitirlos aca duplicaria volumen y haria que el costo por
 * arco y el costo de campania no puedan ser el mismo numero.
 *
 * No es un tipo de agente: PLE admite 10 y el modelo ya los usa (ADR-030).
 */
public class RegistroEjecucionArco implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final String PLANTA_DEPOSITO = "PLANTA_DEPOSITO";
	public static final String DEPOSITO_DEPOSITO = "DEPOSITO_DEPOSITO";
	public static final String ESPERA_PORTACONTENEDOR = "ESPERA_PORTACONTENEDOR";
	public static final String TERMINAL_ORIGEN_VACIO = "TERMINAL_ORIGEN_CONTENEDOR_VACIO";
	public static final String CARGA_CONSOLIDACION = "CARGA_CONSOLIDACION";
	public static final String ORIGEN_TERMINAL_CARGADO = "ORIGEN_TERMINAL_CONTENEDOR_CARGADO";
	public static final String DESCARGA_TERMINAL = "DESCARGA_TERMINAL";
	public static final String CONSOLIDACION_TERMINAL = "CONSOLIDACION_TERMINAL";
	public static final String CROSS_DOCK = "CROSS_DOCK";
	public static final String ESPERA_POSICION = "ESPERA_POSICION";

	public String runId = "";
	public String escenario = "";
	public int replica = 0;

	public long idEventoArco = 0;

	public String idDecision = "";
	public String idAlternativa = "";
	public String idAsignacion = "";
	public String idEnvio = "";
	public String idContenedor = "";
	public String codigoPedido = "";
	public String idLote = "";
	public String producto = "";

	public String tipoArco = "";
	public String origen = "";
	public String destino = "";
	public String circuito = "";
	public boolean esCrossDock = false;

	public double toneladas = 0;
	public int contenedores = 0;
	public int viajes = 0;
	public double distanciaKm = 0;

	public double diaProgramacion = -1;
	public double diaInicio = -1;
	public double diaFin = -1;
	public double duracionRealHoras = 0;
	public double duracionEsperadaHoras = -1;

	public String recursoUtilizado = "";
	public String idRecurso = "";
	public String estadoFinal = "";

	/**
	 * El costo del arco no se copia aca: los cargos son de costos_eventos y se unen por
	 * id_envio, id_contenedor, id_lote o codigo_pedido. Un importe repetido en dos tablas es
	 * un importe que puede diferir.
	 */

	public static String encabezadoCsv() {
		return "run_id,escenario,replica,id_evento_arco,id_decision,id_alternativa,"
			+ "id_asignacion,id_envio,id_contenedor,codigo_pedido,id_lote,producto,"
			+ "tipo_arco,origen,destino,circuito,es_cross_dock,"
			+ "toneladas,contenedores,viajes,distancia_km,"
			+ "dia_programacion,dia_inicio,dia_fin,duracion_real_horas,duracion_esperada_horas,"
			+ "recurso_utilizado,id_recurso,estado_final,fecha_inicio,fecha_fin";
	}

	public String toCsv() {
		StringBuilder f = new StringBuilder(320);

		f.append(AuditoriaRed.txt(runId)).append(',');
		f.append(AuditoriaRed.txt(escenario)).append(',');
		f.append(AuditoriaRed.ent(replica)).append(',');
		f.append(AuditoriaRed.ent(idEventoArco)).append(',');
		f.append(AuditoriaRed.txt(idDecision)).append(',');
		f.append(AuditoriaRed.txt(idAlternativa)).append(',');
		f.append(AuditoriaRed.txt(idAsignacion)).append(',');
		f.append(AuditoriaRed.txt(idEnvio)).append(',');
		f.append(AuditoriaRed.txt(idContenedor)).append(',');
		f.append(AuditoriaRed.txt(codigoPedido)).append(',');
		f.append(AuditoriaRed.txt(idLote)).append(',');
		f.append(AuditoriaRed.txt(producto)).append(',');
		f.append(AuditoriaRed.txt(tipoArco)).append(',');
		f.append(AuditoriaRed.txt(origen)).append(',');
		f.append(AuditoriaRed.txt(destino)).append(',');
		f.append(AuditoriaRed.txt(circuito)).append(',');
		f.append(AuditoriaRed.si(esCrossDock)).append(',');
		f.append(AuditoriaRed.num(toneladas)).append(',');
		f.append(AuditoriaRed.ent(contenedores)).append(',');
		f.append(AuditoriaRed.ent(viajes)).append(',');
		f.append(AuditoriaRed.num(distanciaKm)).append(',');
		f.append(AuditoriaRed.num(diaProgramacion)).append(',');
		f.append(AuditoriaRed.num(diaInicio)).append(',');
		f.append(AuditoriaRed.num(diaFin)).append(',');
		f.append(AuditoriaRed.num(duracionRealHoras)).append(',');
		f.append(AuditoriaRed.num(duracionEsperadaHoras)).append(',');
		f.append(AuditoriaRed.txt(recursoUtilizado)).append(',');
		f.append(AuditoriaRed.txt(idRecurso)).append(',');
		f.append(AuditoriaRed.txt(estadoFinal)).append(',');

		f.append(AuditoriaRed.txt(AuditoriaRed.fecha(diaInicio))).append(',');
		f.append(AuditoriaRed.txt(AuditoriaRed.fecha(diaFin)));

		return f.toString();
	}
}
