# Catálogo ampliado de escalas y herramientas clínicas · Enfermería iDreyK 360

**Fecha de incorporación:** 8 de noviembre de 2026  
**Versión del inventario:** 2.0 · 191 fichas únicas · 86 referencias enlazadas  
**Rama de integración:** `integracion/escalas360-191`

## Alcance y clasificación

La biblioteca conserva las 45 fichas originales y amplía el inventario con 146 fichas adicionales. Un instrumento tiene una sola ficha y puede pertenecer a más de un servicio; no se duplica para cada área.

La clasificación separa:
- Valoración de Enfermería y valoración interdisciplinaria.
- Cribado y alerta temprana.
- Sistemas de triaje.
- Clasificación clínica, radiológica y de resultados.
- Pronóstico y severidad.
- Carga asistencial / intensidad de intervenciones.
- Funcionalidad, nutrición, monitorización y autoinforme.

**No todas las fichas son escalas propias de Enfermería.** Algunas son herramientas médicas/interdisciplinarias, scores pronósticos o de imagen, clasificaciones de complicaciones, criterios diagnósticos o herramientas de registro. El tipo se muestra en cada ficha para evitar presentarlas de manera equívoca.

## Instrumentos de carga de trabajo y UCI

Se incorporan explícitamente TISS-76, TISS-28, NAS y NEMS. Deben mantenerse como herramientas distintas:
- **TISS-76 y TISS-28:** sistemas basados en intervenciones terapéuticas; no son una valoración integral de Enfermería ni una medida directa y exclusiva del tiempo enfermero.
- **NAS (Nursing Activities Score):** orientado a cuantificar actividades/carga de trabajo de Enfermería y estimar el tiempo de cuidado asociado según su método.
- **NEMS:** versión más breve de puntuación de actividad/intervenciones terapéuticas y recursos.

No intercambiar sus puntuaciones, puntos de corte, finalidades o reglas de dotación. La aplicación ofrece ficha documental; no calcula necesidades de personal ni decide asignación de pacientes.

También se incorporan APACHE, SAPS, SOFA, qSOFA, NEWS/MEWS y herramientas relacionadas. Su presencia no convierte una escala pronóstica o de cribado en diagnóstico. En particular, qSOFA no debe usarse como único cribado para descartar sepsis ni SOFA como diagnóstico aislado.

## Cobertura temática incorporada

El catálogo ampliado contempla UCI adulto, emergencia, medicina, cirugía, centro quirúrgico, URPA, cardiología, neumología, neurología/neurocirugía, nefrología/hemodiálisis, obstetricia, puerperio, neonatología/UCIN, pediatría, UCI pediátrica, geriatría, salud mental, oncología/paliativos, quemados/heridas, ostomías, rehabilitación, infecciones/sepsis y prevención de tromboembolismo venoso.

Entre las incorporaciones se incluyen:
- **UCI y deterioro clínico:** TISS-76, TISS-28, NAS, NEMS, APACHE, SAPS, SOFA, qSOFA, SIRS, NEWS2/MEWS y herramientas de sedación/agitación/delirium.
- **Emergencia/trauma:** sistemas ESI, Manchester y otros triajes; shock index; RTS, ISS, NISS, AIS y TRISS.
- **Neurología:** NIHSS, Cincinnati/FAST/BE-FAST, Hunt-Hess, WFNS, Fisher/modified Fisher, ICH Score, ASPECTS, ABCD2 y Rankin.
- **Cardiorrespiratorio:** escalas funcionales/síntomas, scores de dolor torácico, riesgo cardiovascular y tromboembolismo, CURB-65/PSI, ROX/HACOR y mediciones relacionadas.
- **Renal/nutrición:** clasificación de lesión renal aguda, medidas de diálisis y cribados nutricionales en los contextos descritos.
- **Pediatría/neonatología:** PEWS y scores de UCI, PRAM/PASS, herramientas de deshidratación, dolor/sedación neonatal, N-PASS, Finnegan y Eat-Sleep-Console.
- **Obstetricia/geriatría:** alertas maternas, trabajo de parto/posparto, funcionalidad, fragilidad, cognición y riesgo geriátrico.
- **Salud mental/oncología/heridas/cirugía:** escalas de síntomas y riesgo, estado funcional, nutrición, heridas/ostomías, riesgo perioperatorio y clasificación de complicaciones.

Los nombres de las fichas no implican que todos los instrumentos estén adoptados en cada hospital o sean obligatorios en Perú.

## Integración en la interfaz

La biblioteca utiliza componentes nativos de Jetpack Compose. Se filtra por población, clasificación y servicio hospitalario, además del tipo de acceso. En teléfonos se presenta una columna; a partir de 600 dp se usan tarjetas en dos columnas.

Se reutilizan las ilustraciones originales de área del proyecto como recurso decorativo tanto en tarjetas como en fichas. Las ilustraciones no contienen instrucciones oficiales, reactivos, puntajes ni resultados. Los textos, filtros, controles, metadatos y enlaces siguen siendo elementos nativos y accesibles.

La ficha informa objetivo, población, versión, método, límites, tipo, servicios relacionados, rol de Enfermería, contexto peruano, derechos y fuentes.

## Estados de disponibilidad y seguridad

Se conservan los estados del sistema original:
- `calculo`: práctica ya implementada dentro de una ruta nativa explícita.
- `registro`: registro de dato/autoinforme/transcripción; no significa que la app administre la escala externa.
- `simulacion`: ejercicio didáctico, sin equivalencia clínica automática.
- `ficha`: contenido descriptivo, sin formulario ni puntuación.
- `permiso`: no reproducir ni digitalizar el formulario protegido mientras no se acredite autorización.

Las 13 rutas originales se conservan. Las nuevas fichas no obtienen rutas de puntuación por defecto. La validación clínica global permanece pendiente y la presencia de una ficha no autoriza decisiones de diagnóstico, tratamiento, alta, pronóstico individual o dotación.

## Verificación técnica

Se incorpora `tools/validate_scale_catalog.py` y el workflow `.github/workflows/catalog-integrity.yml`, que comprueban automáticamente:
- 191 instrumentos y 86 fuentes.
- IDs únicos y referencias existentes.
- Poblaciones, servicios y metadatos obligatorios.
- URLs HTTPS sin credenciales incrustadas.
- Estados y rutas permitidas.
- Revisión clínica pendiente.
- Tamaño máximo del catálogo.

La prueba del catálogo se ejecuta en GitHub Actions. La compilación Android, tests unitarios y lint deben completarse también en esta rama. No se declara la integración terminada hasta que esos controles pasen.

## Referencias peruanas para contextualizar

- MINSA. NTS 214-MINSA/DGIESP-2024 para atención integral de salud neonatal (RM 545-2024/MINSA): https://www.gob.pe/institucion/minsa/normas-legales/5874349-545-2024-minsa
- MINSA. NTS 207-MINSA/DGIESP-2023 para la atención integral de salud de las personas adultas mayores (RM 789-2023/MINSA): https://www.gob.pe/institucion/minsa/normas-legales/4557563-789-2023-minsa
- EsSalud. Directiva de prácticas seguras para la prevención de caídas: https://repositorio.essalud.gob.pe/bitstream/handle/20.500.12959/2069/Directiva%20de%20pr%C3%A1cticas%20seguras%20para%20la%20prevenci%C3%B3n%20de%20ca%C3%ADdas%20de%20pacientes.pdf?sequence=1

Son fuentes de contexto peruano; no prueban la adopción hospitalaria universal de cada instrumento.
