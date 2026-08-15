#!/usr/bin/env python3
"""Genera datos/entrada_ejemplo.xlsx con un escenario sintetico del modelo.

El libro no se escribe a mano: se compila el espejo de `model_src/` y se corre
el propio `GeneradorSintetico` del modelo, de modo que el ejemplo y la fase
sintetica sean el mismo escenario. Sirve como plantilla para cargar datos
reales: alcanza con reemplazar los valores respetando hojas y encabezados.

Uso: python3 tools/generar_excel_ejemplo.py [id_escenario] [semilla]
"""

import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

from openpyxl import Workbook

RAIZ = Path(__file__).resolve().parent.parent
ESPEJO = RAIZ / "model_src"
SALIDA = RAIZ / "datos" / "entrada_ejemplo.xlsx"

# Enumerados del modelo: en el .alp son Option Lists, no clases Java, así que el
# volcado necesita una definición equivalente para compilar fuera de AnyLogic.
ENUMS = {
    "TipoProducto.java": "public enum TipoProducto { JUGO, ACEITE, CASCARA }\n",
    "TipoContenedor.java": "public enum TipoContenedor { REEFER_40, DRY_HC_40, IMO_DRY_20 }\n",
    "Naviera.java": "public enum Naviera { SIN_DEFINIR, MSC, MAERSK, HAPAG_LLOYD, CMA_CGM, ONE }\n",
}


def volcar(id_escenario: str, semilla: str) -> str:
    with tempfile.TemporaryDirectory() as tmp:
        trabajo = Path(tmp)
        for fuente in ("DatosEntrada.java", "GeneradorSintetico.java", "AuditoriaRed.java"):
            shutil.copy(ESPEJO / fuente, trabajo / fuente)
        shutil.copy(RAIZ / "tools" / "VolcarDatos.java", trabajo / "VolcarDatos.java")
        for nombre, cuerpo in ENUMS.items():
            (trabajo / nombre).write_text(cuerpo, encoding="utf-8")

        subprocess.run(
            ["javac", "-nowarn", "-d", str(trabajo / "out")]
            + [str(p) for p in trabajo.glob("*.java")],
            check=True,
        )
        return subprocess.run(
            ["java", "-cp", str(trabajo / "out"), "VolcarDatos", id_escenario, semilla],
            check=True,
            capture_output=True,
            text=True,
        ).stdout


def escribir(volcado: str) -> int:
    libro = Workbook()
    libro.remove(libro.active)
    hoja = None
    filas = 0

    for linea in volcado.splitlines():
        if linea.startswith("#HOJA\t"):
            hoja = libro.create_sheet(linea.split("\t")[1])
            continue
        celdas = linea.split("\t")
        hoja.append([convertir(c) for c in celdas])
        filas += 1

    SALIDA.parent.mkdir(exist_ok=True)
    libro.save(SALIDA)
    return filas


def convertir(celda: str):
    try:
        return int(celda)
    except ValueError:
        pass
    try:
        return float(celda)
    except ValueError:
        return celda


def main() -> None:
    id_escenario = sys.argv[1] if len(sys.argv) > 1 else "E-00"
    semilla = sys.argv[2] if len(sys.argv) > 2 else "1"

    filas = escribir(volcar(id_escenario, semilla))
    print(f"{SALIDA} escrito ({filas} filas, encabezados incluidos)")


if __name__ == "__main__":
    main()
