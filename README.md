# AnyLogic Logística de Exportación Argentina 2026

Modelo de simulación para producción, almacenamiento, consolidación, cross docking, transporte y entrega portuaria de subproductos cítricos de exportación.

## Estado

Proyecto en desarrollo sobre AnyLogic 8.9.9 PLE (licencia a resolver, ver ADR-020).

**Uso primario del modelo:** dimensionar depósitos, flota, posiciones de consolidación y costo total de campaña. No es un modelo de decisión táctica por pedido. Ver [Definición del proyecto](docs/09_Definicion/Definicion_del_Proyecto.md).

**Advertencia:** el modelo `.alp` todavía no está versionado en este repositorio. Hasta que lo esté, la documentación describe un artefacto que vive fuera del control de versiones.

## Documentación

La documentación técnica está organizada en módulos navegables:

- [Índice general](docs/README.md)
- [Glosario](docs/00_Glosario.md)
- [Definición del proyecto](docs/09_Definicion/Definicion_del_Proyecto.md)
- [Contrato de datos de entrada](docs/09_Definicion/Contrato_de_Datos.md)
- [Escenarios y experimentos](docs/09_Definicion/Escenarios_y_Experimentos.md)
- [Especificación técnica maestra](docs/ESPECIFICACION_TECNICA_MODELO_LOGISTICO.md)
- [Arquitectura general](docs/01_Arquitectura/Arquitectura_General.md)
- [Agentes](docs/02_Modelo/Agentes.md)
- [Modelo de datos](docs/02_Modelo/Modelo_de_Datos.md)
- [Funciones](docs/03_Logica/Funciones.md)
- [Flujos operativos](docs/04_Flujos/Flujos_Operativos.md)
- [Costos](docs/05_Costos/Modelo_de_Costos.md)
- [Validación](docs/06_Validacion/Plan_de_Validacion.md)
- [Roadmap](docs/07_Roadmap/Roadmap.md)
- [Decisiones de arquitectura](docs/08_Decisiones/Decisiones_de_Arquitectura.md)

## Principios de diseño

- Separar planificación, ejecución y costeo.
- Representar cada contenedor como una entidad independiente.
- Permitir que un lote comercial se produzca durante varios días.
- Permitir despachos parciales antes de finalizar el lote.
- Permitir múltiples ubicaciones físicas para un mismo lote.
- Mantener migración segura desde la lógica actual.
- No eliminar componentes existentes hasta validar su reemplazo.
- Ningún parámetro, tarifa ni duración vive en código: todo se lee de tablas de datos.
- Ninguna magnitud tiene dos fuentes de verdad.

## Productos y equipos

| Producto | Contenedor |
|---|---|
| Jugo | Reefer 40 ft |
| Cáscara | HC Dry 40 ft |
| Aceite | IMO Dry 20 ft |

## Alcance operativo

- Planta.
- Depósitos de terceros.
- Consolidación en planta o depósito.
- Cross docking en depósito o terminal.
- Terminales Zárate y T4.
- Camión de producto.
- Camión portacontenedor.
- Costos locales, almacenamiento, THC, terminal y despachante.

## Convención de estados documentales

- **Implementado:** existe y fue probado.
- **En transición:** existe parcialmente o convive con lógica anterior.
- **Objetivo:** diseño validado todavía no implementado completamente.
