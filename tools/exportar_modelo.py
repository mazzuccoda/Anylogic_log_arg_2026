#!/usr/bin/env python3
"""Exporta el contenido de un archivo .alp de AnyLogic a fuentes legibles y diffeables.

El .alp es un XML de una sola línea por elemento con el código Java embebido en
etiquetas <Body>. Eso hace que un cambio de una línea de código produzca un diff
ilegible y que revisar el modelo en un pull request sea imposible.

Este script escribe, por cada tipo de agente, un archivo `.java` de sólo lectura
con sus parámetros, variables, colecciones, funciones, eventos y objetos
embebidos. Los archivos generados no se compilan ni se editan: son el espejo
revisable del modelo.

Uso:
    python3 tools/exportar_modelo.py [RedLogistica_Exportacion.alp] [model_src]

Regla del proyecto: ejecutar después de cada cambio del modelo y versionar el
resultado junto al .alp en el mismo commit.
"""

from __future__ import annotations

import hashlib
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

CABECERA = (
    "// ARCHIVO GENERADO POR tools/exportar_modelo.py — NO EDITAR A MANO.\n"
    "// Espejo legible del modelo AnyLogic. La fuente de verdad es el .alp.\n"
)


def texto(elemento: ET.Element | None, etiqueta: str, defecto: str = "") -> str:
    if elemento is None:
        return defecto
    valor = elemento.findtext(etiqueta)
    return defecto if valor is None else valor.strip()


def indentar(codigo: str, sangria: str = "        ") -> str:
    if not codigo.strip():
        return sangria + "// (vacío)"
    return "\n".join(sangria + linea if linea.strip() else "" for linea in codigo.splitlines())


def valor_por_defecto(propiedades: ET.Element | None) -> str:
    if propiedades is None:
        return ""
    for etiqueta in ("DefaultValue", "InitialValue", "CollectionInitializer"):
        elemento = propiedades.find(etiqueta)
        if elemento is not None:
            return texto(elemento, "Code")
    return ""


def exportar_variables(agente: ET.Element) -> list[str]:
    lineas: list[str] = []
    contenedor = agente.find("Variables")
    if contenedor is None:
        return lineas

    parametros = []
    variables = []
    colecciones = []

    for variable in contenedor:
        clase = variable.get("Class", "Variable")
        propiedades = variable.find("Properties")
        nombre = texto(variable, "Name")
        tipo = texto(propiedades, "Type") or texto(propiedades, "CollectionClass") or "Object"
        inicial = valor_por_defecto(propiedades)
        if clase == "Parameter":
            parametros.append((tipo, nombre, inicial))
        elif clase.endswith("Variable") and clase != "CollectionVariable":
            variables.append((tipo, nombre, inicial))
        elif clase.endswith("Collection") or clase == "CollectionVariable":
            elemento = texto(propiedades, "ElementClass")
            colecciones.append((f"{tipo}<{elemento}>" if elemento else tipo, nombre, inicial))
        else:
            variables.append((tipo, nombre, inicial))

    for titulo, grupo in (
        ("Parámetros", parametros),
        ("Variables", variables),
        ("Colecciones", colecciones),
    ):
        if not grupo:
            continue
        lineas.append(f"\n    // ----- {titulo} -----")
        for tipo, nombre, inicial in grupo:
            sufijo = f" = {inicial}" if inicial else ""
            lineas.append(f"    {tipo} {nombre}{sufijo};")
    return lineas


def exportar_funciones(agente: ET.Element) -> list[str]:
    lineas: list[str] = []
    contenedor = agente.find("Functions")
    if contenedor is None:
        return lineas

    lineas.append("\n    // ----- Funciones -----")
    for funcion in contenedor:
        nombre = texto(funcion, "Name")
        modificador = texto(funcion, "ReturnModificator")
        retorno = "void" if modificador == "VOID" else texto(funcion, "ReturnType", "void")
        parametros = ", ".join(
            f"{texto(p, 'Type')} {texto(p, 'Name')}" for p in funcion.findall("Parameter")
        )
        cuerpo = funcion.findtext("Body") or ""
        lineas.append(f"\n    {retorno} {nombre}({parametros}) {{")
        lineas.append(indentar(cuerpo))
        lineas.append("    }")
    return lineas


def exportar_eventos(agente: ET.Element) -> list[str]:
    lineas: list[str] = []
    contenedor = agente.find("Events")
    if contenedor is None:
        return lineas

    lineas.append("\n    // ----- Eventos -----")
    for evento in contenedor:
        nombre = texto(evento, "Name")
        propiedades = evento.find("Properties")
        modo = ""
        periodo = ""
        if propiedades is not None:
            disparo = propiedades.get("TriggerType", "")
            ciclo = propiedades.get("Mode", "")
            modo = " ".join(x for x in (disparo, ciclo) if x)
            recurrencia = propiedades.find("RecurrenceCode")
            if recurrencia is not None and ciclo == "cyclic":
                unidad = recurrencia.findtext("Unit") or ""
                periodo = f" cada {texto(recurrencia, 'Code')} {unidad.lower()}"
        cuerpo = evento.findtext("Action") or ""
        detalle = f" [{modo}]" if modo else ""
        lineas.append(f"\n    // evento {nombre}{detalle}{periodo}")
        lineas.append(f"    void {nombre}_accion() {{")
        lineas.append(indentar(cuerpo))
        lineas.append("    }")
    return lineas


def exportar_embebidos(agente: ET.Element) -> list[str]:
    lineas: list[str] = []
    contenedor = agente.find("EmbeddedObjects")
    if contenedor is None:
        return lineas

    lineas.append("\n    // ----- Objetos embebidos (poblaciones y bloques de flowchart) -----")
    for objeto in contenedor:
        nombre = texto(objeto, "Name")
        clase = texto(objeto, "ActiveObjectClassName") or texto(objeto, "PresentationName")
        lineas.append(f"    // {clase} {nombre}")
    return lineas


def exportar(ruta_alp: Path, destino: Path) -> int:
    raiz = ET.parse(ruta_alp).getroot()
    modelo = raiz.find("Model")
    if modelo is None:
        raise SystemExit(f"{ruta_alp}: no contiene un elemento <Model>")

    destino.mkdir(parents=True, exist_ok=True)
    for obsoleto in destino.glob("*.java"):
        obsoleto.unlink()

    generados = 0

    listas = modelo.find("OptionLists")
    if listas is not None and len(listas):
        contenido = [CABECERA, "// Option Lists del modelo\n"]
        for lista in listas:
            opciones = ", ".join(texto(o, "Name") for o in lista.findall("Option"))
            contenido.append(f"enum {texto(lista, 'Name')} {{ {opciones} }}\n")
        (destino / "OptionLists.java").write_text("\n".join(contenido), encoding="utf-8")
        generados += 1

    nombres = []
    agentes = modelo.find("ActiveObjectClasses")
    for agente in agentes if agentes is not None else []:
        nombre = texto(agente, "Name")
        nombres.append(nombre)
        cuerpo = [CABECERA, f"\nclass {nombre} extends Agent {{"]
        cuerpo += exportar_variables(agente)
        cuerpo += exportar_embebidos(agente)
        cuerpo += exportar_funciones(agente)
        cuerpo += exportar_eventos(agente)
        cuerpo.append("}")
        (destino / f"{nombre}.java").write_text("\n".join(cuerpo) + "\n", encoding="utf-8")
        generados += 1

    escribir_manifiesto(ruta_alp, destino, raiz, nombres)
    return generados + 1


def escribir_manifiesto(
    ruta_alp: Path, destino: Path, raiz: ET.Element, nombres: list[str]
) -> None:
    """Deja constancia de qué .alp produjo este espejo.

    El hash permite detectar en revisión que alguien cambió el modelo sin
    regenerar model_src/.
    """
    digest = hashlib.sha256(ruta_alp.read_bytes()).hexdigest()
    lineas = [
        "# Manifiesto del espejo del modelo",
        "",
        "Generado por `tools/exportar_modelo.py`. No editar a mano.",
        "",
        f"- Archivo: `{ruta_alp.name}`",
        f"- SHA-256: `{digest}`",
        f"- Bytes: {ruta_alp.stat().st_size}",
        f"- AnyLogic: {raiz.get('AnyLogicVersion', 'desconocida')}",
        f"- Tipos de agente: {len(nombres)} de 10 permitidos por PLE",
        "",
        "| # | Tipo de agente |",
        "|---:|---|",
    ]
    lineas += [f"| {i} | `{n}` |" for i, n in enumerate(nombres, 1)]
    lineas.append("")
    (destino / "MANIFIESTO.md").write_text("\n".join(lineas), encoding="utf-8")


def main() -> None:
    raiz = Path(__file__).resolve().parent.parent
    ruta_alp = Path(sys.argv[1]) if len(sys.argv) > 1 else raiz / "RedLogistica_Exportacion.alp"
    destino = Path(sys.argv[2]) if len(sys.argv) > 2 else raiz / "model_src"

    if not ruta_alp.exists():
        raise SystemExit(f"No se encontró el modelo: {ruta_alp}")

    generados = exportar(ruta_alp, destino)
    print(f"{generados} archivos escritos en {destino}")


if __name__ == "__main__":
    main()
