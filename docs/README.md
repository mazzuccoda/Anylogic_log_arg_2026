# Índice de documentación

Este directorio contiene la documentación técnica viva del modelo.

## 1. Arquitectura

- [Arquitectura general](01_Arquitectura/Arquitectura_General.md)

## 2. Modelo

- [Agentes](02_Modelo/Agentes.md)
- [Modelo de datos](02_Modelo/Modelo_de_Datos.md)

## 3. Lógica

- [Inventario y especificación de funciones](03_Logica/Funciones.md)

## 4. Flujos

- [Flujos operativos](04_Flujos/Flujos_Operativos.md)

## 5. Costos

- [Modelo de costos](05_Costos/Modelo_de_Costos.md)

## 6. Validación

- [Plan de validación](06_Validacion/Plan_de_Validacion.md)

## 7. Roadmap

- [Roadmap y estado de avance](07_Roadmap/Roadmap.md)

## 8. Decisiones

- [Decisiones de arquitectura](08_Decisiones/Decisiones_de_Arquitectura.md)

## Documento consolidado

- [Especificación técnica maestra](ESPECIFICACION_TECNICA_MODELO_LOGISTICO.md)

## Regla de mantenimiento

Cada cambio del modelo debe actualizar como mínimo:

1. el archivo del módulo afectado;
2. el roadmap;
3. la decisión de arquitectura correspondiente si cambia el diseño;
4. el plan de validación si introduce una nueva regla o riesgo.

## Convención de estado

| Estado | Significado |
|---|---|
| Implementado | Existe en AnyLogic y fue probado |
| Parcial | Existe, pero no cubre toda la regla de negocio |
| En transición | Convive con lógica anterior |
| Diseño validado | Acordado, todavía no implementado |
| Pendiente | No iniciado |
