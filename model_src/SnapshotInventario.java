// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.
// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.

/**
 * Foto diaria del inventario de una ubicacion y un producto (ADR-064).
 *
 * Es la tabla que permite reconciliar el balance por nodo:
 *
 *     stock inicial del dia + ingresos - egresos = stock final del dia
 *
 * validarInventario() ya verificaba el total de la red; esto lo verifica por ubicacion y
 * producto, que es donde se ve si un deposito se llena o si un nodo pierde producto.
 *
 * No es un tipo de agente: PLE admite 10 y el modelo ya los usa (ADR-030).
 */
public class SnapshotInventario implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public String runId = "";
	public String escenario = "";
	public int replica = 0;

	public int dia = 0;
	public String ubicacion = "";
	public String tipoUbicacion = "";
	public String producto = "";

	public double capacidadTn = 0;
	public double stockInicialDiaTn = 0;
	public double stockFisicoTn = 0;
	public double stockLibreTn = 0;
	public double stockReservadoPedidosTn = 0;
	public double stockReservadoViajesTn = 0;
	public double stockEnTransitoEntradaTn = 0;
	public double stockEnTransitoSalidaTn = 0;
	public double ingresosDiaTn = 0;
	public double egresosDiaTn = 0;
	public double produccionDiaTn = 0;
	public double ocupacionPct = 0;
	public double costoAlmacenajeDiaUsd = 0;
	public double diasStockPromedio = 0;
	public int lotesAbiertos = 0;
	public double loteMasAntiguoDias = 0;

	public static String encabezadoCsv() {
		return "run_id,escenario,replica,dia,ubicacion,tipo_ubicacion,producto,capacidad_tn,"
			+ "stock_inicial_dia_tn,stock_fisico_tn,stock_libre_tn,stock_reservado_pedidos_tn,"
			+ "stock_reservado_viajes_tn,stock_en_transito_entrada_tn,stock_en_transito_salida_tn,"
			+ "ingresos_dia_tn,egresos_dia_tn,produccion_dia_tn,ocupacion_pct,"
			+ "costo_almacenaje_dia_usd,dias_stock_promedio,lotes_abiertos,lote_mas_antiguo_dias,"
			+ "descuadre_tn,fecha";
	}

	/** Lo que C-12 exige que sea cero: la identidad del balance diario del nodo. */
	public double descuadre() {
		return stockFisicoTn - (stockInicialDiaTn + ingresosDiaTn - egresosDiaTn);
	}

	public String toCsv() {
		StringBuilder f = new StringBuilder(256);

		f.append(AuditoriaRed.txt(runId)).append(',');
		f.append(AuditoriaRed.txt(escenario)).append(',');
		f.append(AuditoriaRed.ent(replica)).append(',');
		f.append(AuditoriaRed.ent(dia)).append(',');
		f.append(AuditoriaRed.txt(ubicacion)).append(',');
		f.append(AuditoriaRed.txt(tipoUbicacion)).append(',');
		f.append(AuditoriaRed.txt(producto)).append(',');
		f.append(AuditoriaRed.num(capacidadTn)).append(',');
		f.append(AuditoriaRed.num(stockInicialDiaTn)).append(',');
		f.append(AuditoriaRed.num(stockFisicoTn)).append(',');
		f.append(AuditoriaRed.num(stockLibreTn)).append(',');
		f.append(AuditoriaRed.num(stockReservadoPedidosTn)).append(',');
		f.append(AuditoriaRed.num(stockReservadoViajesTn)).append(',');
		f.append(AuditoriaRed.num(stockEnTransitoEntradaTn)).append(',');
		f.append(AuditoriaRed.num(stockEnTransitoSalidaTn)).append(',');
		f.append(AuditoriaRed.num(ingresosDiaTn)).append(',');
		f.append(AuditoriaRed.num(egresosDiaTn)).append(',');
		f.append(AuditoriaRed.num(produccionDiaTn)).append(',');
		f.append(AuditoriaRed.num(ocupacionPct)).append(',');
		f.append(AuditoriaRed.num(costoAlmacenajeDiaUsd)).append(',');
		f.append(AuditoriaRed.num(diasStockPromedio)).append(',');
		f.append(AuditoriaRed.ent(lotesAbiertos)).append(',');
		f.append(AuditoriaRed.num(loteMasAntiguoDias)).append(',');
		f.append(AuditoriaRed.num(descuadre())).append(',');

		f.append(AuditoriaRed.txt(AuditoriaRed.fecha(dia)));

		return f.toString();
	}
}
