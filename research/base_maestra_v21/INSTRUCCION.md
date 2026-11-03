# INSTRUCCIÓN DE EJECUCIÓN PARA ARENA IA — ENFERMERÍA iDREYK 360

Ya cargué el archivo `Enfermeria_iDreyK360_BASE_MAESTRA_v2_1.md`. Léelo COMPLETO, incluidos sus anexos, antes de modificar el proyecto.

Actúa como desarrollador senior full-stack, arquitecto de software educativo en salud y diseñador UX/UI. Continúa sobre el repositorio existente de **Enfermería iDreyK 360**. No hagas una maqueta ni respondas solamente con otro plan: audita el código real y desarrolla las funcionalidades priorizadas en la base maestra.

## Orden obligatorio

1. Confirma que has leído la base maestra y resume brevemente sus reglas críticas.
2. Audita el repositorio y clasifica cada módulo `HECHO`, `PARCIAL`, `FALTA` o `BLOQUEADO-POR-VALIDACIÓN`.
3. Conserva el Prototipo B, la navegación y las funciones que ya funcionan. Antes de refactorizar, crea respaldo o rama de seguridad y ejecuta las pruebas existentes.
4. Implementa el MVP según las secciones 10 y 13 de la base maestra: inicio/buscador, biblioteca, escalas iniciales, atlas interactivo, bioseguridad, calculadoras educativas, PAE/SOAPIE/SBAR y casos simulados.
5. Importa el inventario farmacológico como catálogo con estado de validación. No publiques dosis, diluciones ni compatibilidades no verificadas con la ficha oficial del producto exacto y fuentes clínicas vigentes.
6. Deja los módulos futuros organizados según el roadmap. No intentes simular que están implementados.
7. Ejecuta pruebas, corrige errores y verifica build/responsive en móvil y tablet, hasta donde el entorno lo permita.

## Reglas de seguridad

No añadas IA para diagnosticar o prescribir. No uses datos identificables de pacientes reales. No inventes dosis, resultados, stock hospitalario ni protocolos. No conviertas órdenes históricas en recomendaciones. Respeta licencias de NANDA-I, NIC, NOC y escalas. Bloquea cálculos con datos incompletos o unidades incompatibles. Los resultados deben indicar `EJERCICIO EDUCATIVO`.

## Referencias visuales

La base maestra describe el Prototipo B. Si adjunto los archivos visuales `Prototipo_B_Enfermeria_iDreyK360.png` y `Referencia_Fichas_Farmacologicas_Cuidados_Criticos.png`, utilízalos únicamente como referencias de diseño; no extraigas dosis ni texto clínico de las imágenes sin validarlo.

## Entregables obligatorios

- Código funcional y build ejecutable.
- Resumen de lo implementado y reutilizado.
- Tabla de estado `HECHO / PARCIAL / FALTA / BLOQUEADO-POR-VALIDACIÓN`.
- Pruebas ejecutadas y resultados reales.
- Capturas de pantallas reales.
- Compatibilidad verificada en móvil/tablet, o limitaciones concretas.
- Instrucciones de ejecución y build.
- Backlog futuro priorizado.

No afirmes que algo está terminado si no existe en el código y no ha sido comprobado. Si hay una limitación de acceso, dependencias o validación clínica, indícala y continúa con lo que sí pueda implementarse.
