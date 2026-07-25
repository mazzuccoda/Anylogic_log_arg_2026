# Índice de documentación

Este directorio contiene la documentación técnica viva del modelo.

## 0. Base

- [Glosario](00_Glosario.md)
- [Definición del proyecto](09_Definicion/Definicion_del_Proyecto.md) — alcance vigente, prevalece ante conflicto
- [Contrato de datos de entrada](09_Definicion/Contrato_de_Datos.md)
- [Escenarios y experimentos](09_Definicion/Escenarios_y_Experimentos.md)

## Manual

- [Manual de uso](10_Manual/Manual_de_Uso.md) — abrir el modelo, correr una campaña, cargar datos desde Excel, correr el barrido y leer los resultados
- [Tablero e indicadores](10_Manual/Tablero_e_Indicadores.md) — qué muestra cada panel y cómo se define cada KPI

## 1. Arquitectura

- [Arquitectura general](01_Arquitectura/Arquitectura_General.md)

## 2. Modelo

- [Agentes](02_Modelo/Agentes.md)
- [Modelo de datos](02_Modelo/Modelo_de_Datos.md)

## 3. Lógica

- [Inventario del modelo real](03_Logica/Inventario_del_Modelo.md) — qué contiene hoy el `.alp` y qué hallazgos tiene
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

- [Especificación técnica maestra](ESPECIFICACION_TECNICA_MODELO_LOGISTICO.md) — dominio, reglas de negocio y código actual

## Jerarquía de documentos

Ante contradicción, prevalece el de mayor jerarquía:

1. [Decisiones de arquitectura](08_Decisiones/Decisiones_de_Arquitectura.md) — única fuente de numeración de ADR
2. [Definición del proyecto](09_Definicion/Definicion_del_Proyecto.md) — alcance, tiempo, recursos, variabilidad
3. Módulos temáticos (01 a 06)
4. Especificación técnica maestra

El estado de avance vive únicamente en el [Roadmap](07_Roadmap/Roadmap.md).

Si la documentación y el modelo discrepan, manda el modelo: el desvío se anota como hallazgo en el [Inventario del modelo](03_Logica/Inventario_del_Modelo.md) y se corrige el documento.

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
