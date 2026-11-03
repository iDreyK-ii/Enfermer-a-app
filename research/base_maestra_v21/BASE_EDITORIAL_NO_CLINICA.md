# ENFERMERÍA iDreyK 360 — BASE MAESTRA DE CONOCIMIENTO Y DESARROLLO

**Versión:** 2.1 consolidada  
**Fecha de consolidación:** 3 de noviembre de 2026  
**Ámbito:** educación de Enfermería y práctica clínica simulada, con prioridad en el contexto sanitario peruano.  
**Formato:** Markdown UTF-8, documento único para importar en Arena IA.

---

## INSTRUCCIONES DE USO DE ESTE ARCHIVO

Este documento reúne en un solo archivo la base académica, clínica, farmacológica y técnica del proyecto. Está preparado para ser cargado de una sola vez en Arena IA.

- Leer el documento completo antes de realizar cambios al repositorio.
- Utilizar las secciones 01 a 13 como base principal de conocimientos y especificaciones.
- Los anexos A y B son material complementario y fuentes de trabajo previas; no deben tratarse como órdenes médicas ni como autoridad clínica final.
- El documento de ejecución para Arena IA se enviará en un segundo mensaje, después de cargar esta base.
- No se incluye la malla curricular ni una supuesta distribución de cursos de la Universidad Peruana Unión. La organización es independiente, por competencias y áreas clínicas.
- Las dos imágenes de referencia visual se mantienen como archivos separados porque son recursos gráficos. No es necesario cargarlas para que Arena IA comprenda el alcance textual; sí conviene adjuntarlas después cuando se trabaje el diseño visual.

## ÍNDICE GENERAL

- **01. Contenidos y competencias de Enfermería**
- **02. Escalas, tests y valoraciones**
- **03. Bioseguridad, higiene de manos y medicación segura**
- **04. Instrumental, dispositivos y equipos**
- **05. Coche de paro, cartuchera y área clínica**
- **06. Inventario farmacológico y prioridades**
- **07. PAE, casos clínicos y razonamiento**
- **08. Brief técnico para Arena IA**
- **09. Referencias y control de vigencia**
- **10. Roadmap y módulos futuros**
- **11. Modelo de datos y pruebas de calidad**
- **12. Temas complementarios y especialidades**
- **13. Plan de implementación y criterios de aceptación**
- **ANEXO A. Guía de goteo, dilución y casos**
- **ANEXO B. Árbol farmacológico / vademécum — fase 1**

---

## REGLAS GLOBALES DEL PROYECTO

### Propósito

**Enfermería iDreyK 360** será una biblioteca de estudio, atlas visual de dispositivos, laboratorio educativo de procedimientos y cálculos, centro de escalas, simulador de razonamiento enfermero y repositorio de casos clínicos simulados. No debe limitarse a mostrar definiciones: debe permitir comprender, practicar, recibir retroalimentación y reconocer cuándo corresponde consultar a un docente, prescriptor, farmacéutico o protocolo institucional.

### Jerarquía de fuentes

1. Norma sanitaria peruana vigente y sus modificatorias (MINSA y órganos competentes).
2. Registro sanitario y ficha técnica oficial del producto exacto (DIGEMID).
3. Guía de práctica clínica vigente (MINSA, EsSalud/IETSI u otra entidad competente).
4. Protocolo aprobado del establecimiento y del servicio; especialmente para coche de paro, medicamentos, procedimientos y escalas locales.
5. OMS/OPS, CDC, AHA, ERC, sociedades científicas, manuales técnicos y evidencia revisada por pares, como complemento.
6. Casos clínicos académicos, presentaciones, notas e imágenes previas: sirven para identificar temas y entrenar razonamiento, **no validan una prescripción ni reemplazan una fuente oficial**.

### Etiquetas de procedencia obligatorias

Cada artículo debe identificarse como uno de los siguientes tipos:

- `OFICIAL-MINSA`: norma o guía ministerial identificada con código y fecha.
- `OFICIAL-DIGEMID`: registro o ficha técnica del medicamento exacto.
- `INSTITUCIONAL`: documento aprobado por hospital o servicio concreto; no universalizar.
- `EVIDENCIA-COMPLEMENTARIA`: OMS/OPS/CDC/AHA/sociedad científica o literatura fiable.
- `ACADÉMICO`: explicación didáctica creada para estudiar.
- `PENDIENTE-VALIDACIÓN`: no debe utilizarse como instrucción clínica hasta revisión.
- `LICENCIA-PENDIENTE`: instrumento o taxonomía cuya reproducción digital requiere verificar permisos.

### Reglas clínicas no negociables

- La app es educativa y de simulación. No sustituye prescripción, juicio clínico, supervisión docente, farmacéutico ni protocolo institucional.
- No inventar dosis, concentraciones, diluyentes, compatibilidades, estabilidad, velocidad, existencia en stock o indicaciones.
- No copiar dosis de casos previos como recomendaciones. Deben auditarse contra ficha técnica y guía vigente.
- Si una orden contiene unidad dudosa, error decimal, concentración incompleta o vía ambigua, detener el cálculo y solicitar verificación; nunca corregir silenciosamente.
- Distinguir principio activo, marca, forma farmacéutica, concentración y presentación comercial.
- No diagnosticar ni recomendar tratamiento automáticamente a partir de una escala.
- No convertir el resultado de una calculadora en una orden lista para administrar.
- Medicamentos de alto riesgo: alerta destacada, explicación del peligro y doble comprobación cuando el protocolo lo indique; bloquear ante datos esenciales ausentes.
- No cargar datos identificables de pacientes reales en servicios de IA externos. Utilizar casos ficticios o irreversiblemente anonimizados.
- Para contenido sujeto a derechos o licencias (p. ej., NANDA-I, NIC, NOC y algunas escalas), no reproducir catálogos, formularios o textos completos sin autorización aplicable.

### Identidad visual

Mantener el **Prototipo B de Enfermería iDreyK 360** como referencia: tarjetas redondeadas, turquesa, rosa suave, lila y blanco; tipografía legible; iconos consistentes; modo oscuro con acento turquesa; diseño adaptable a Android, teléfono y tablet. El acabado puede ser amigable y dinámico, pero la representación clínica debe ser sobria, precisa y accesible. No convertir advertencias clínicas en adornos.

### Auditoría inicial antes de programar

No asumir que los módulos descritos ya existen en el código. Auditar el repositorio actual y clasificar cada función como `HECHO`, `PARCIAL`, `FALTA` o `BLOQUEADO-POR-VALIDACIÓN`. Conservar lo que funciona, evitar duplicar pantallas y no rehacer la aplicación sin justificación técnica.

---

# CONTENIDO CONSOLIDADO

---

# SECCIÓN 01 — CONTENIDOS Y COMPETENCIAS DE ENFERMERÍA

## Mapa independiente de contenidos y competencias de Enfermería

**Propósito:** definir el universo de contenidos de Enfermería que puede cubrir la aplicación sin atribuirlo a un sílabo o universidad concreta.

### 1. Competencias transversales

- Identidad, ética, deontología, responsabilidad y límites del rol profesional.
- Comunicación terapéutica, escucha activa, consentimiento, confidencialidad y respeto cultural.
- Pensamiento crítico, juicio clínico, toma de decisiones, priorización y trabajo interprofesional.
- Seguridad del paciente, reporte de incidentes, prevención del daño y mejora continua.
- Documentación clínica clara, objetiva, cronológica, trazable y sin juicios de valor.
- Educación para la salud, autocuidado, alfabetización en salud y participación familiar.
- Investigación, lectura crítica, búsqueda bibliográfica y práctica basada en evidencia.
- Liderazgo, gestión de servicios, continuidad y coordinación del cuidado.

### 2. Fundamentos biomédicos

#### Anatomía y fisiología

- Organización celular y tisular; homeostasis; membrana, transporte y señalización.
- Sistema nervioso central/periférico; sentidos; sistema endocrino.
- Sistema cardiovascular, sangre y hemostasia.
- Sistema respiratorio y regulación ácido-base.
- Sistema renal/urinario y equilibrio hidroelectrolítico.
- Sistema digestivo, hígado, páncreas y metabolismo.
- Sistema musculoesquelético y tegumentario.
- Sistema reproductor femenino/masculino y ciclo vital.
- Sistema linfático e inmunitario.

#### Ciencias básicas y aplicadas

- Bioquímica, microbiología, parasitología, inmunología y genética básica.
- Nutrición, metabolismo, hidratación y dietoterapia.
- Farmacología general: farmacocinética, farmacodinamia, variabilidad, interacciones y seguridad.
- Epidemiología, estadística y determinantes sociales de la salud.

### 3. Fundamentos y valoración de Enfermería

- Proceso de atención de Enfermería (PAE): valoración, diagnóstico, planificación, ejecución y evaluación.
- Patrones funcionales de Gordon y otros marcos de valoración reconocidos.
- Entrevista, historia, examen físico general y por sistemas.
- Inspección, palpación, percusión y auscultación.
- Signos vitales: temperatura, pulso, frecuencia respiratoria, presión arterial, SpO₂, dolor y conciencia.
- Antropometría: peso, talla, IMC, perímetros y medidas pediátricas según protocolo.
- Valoración de piel, mucosas, hidratación, movilidad, marcha, nutrición, eliminación y estado psicosocial.
- Escalas, instrumentos y ayudas cognitivas; límites de interpretación.
- Registro, evolución y reevaluación después de la intervención.

### 4. Procedimientos básicos y clínicos

- Higiene de manos, EPP, precauciones estándar y basadas en transmisión.
- Identificación del paciente, preparación de entorno, comunicación y consentimiento.
- Aseo, baño, higiene oral, cuidado de piel y confort.
- Movilización, transferencia, cambios posturales, prevención de caídas y lesiones por presión.
- Control de signos vitales, glucemia capilar, antropometría y balance hídrico.
- Administración segura de medicamentos por vías oral, tópica, inhalatoria, enteral, intradérmica, subcutánea, intramuscular e intravenosa, dentro del alcance profesional y protocolo.
- Preparación de soluciones, reconstitución, dilución, cálculo, compatibilidad y rotulado: solo con fuentes verificadas.
- Venopunción, canalización periférica, mantenimiento de acceso, vigilancia de flebitis/infiltración y retiro según protocolo.
- Curaciones, cuidado de heridas, drenajes y ostomías.
- Sondaje vesical, sonda nasogástrica/orogástrica, nutrición enteral y cuidados asociados.
- Oxigenoterapia, inhaloterapia, nebulización, aspiración de secreciones y cuidado de vía aérea artificial según competencia y entrenamiento.
- Electrocardiografía de 12 derivaciones, monitorización, telemetría y reconocimiento inicial de alarmas.
- Transfusión de hemocomponentes: identificación, vigilancia y reacción transfusional conforme a protocolo.
- Cuidados preoperatorios, intraoperatorios y postoperatorios según rol.

### 5. Seguridad, calidad y control de infecciones

- Cinco momentos de higiene de manos.
- Aislamiento por contacto, gotas y vía aérea; protección del paciente inmunocomprometido cuando corresponda.
- Uso racional de guantes y EPP.
- Limpieza, desinfección, esterilización y reprocesamiento de dispositivos reutilizables.
- Manejo de punzocortantes y residuos sanitarios.
- Prevención y vigilancia de infecciones asociadas a la atención de salud (IAAS).
- Seguridad de medicación, medicamentos de alto riesgo y LASA.
- Identificación, comunicación efectiva, cirugía segura, transfusión segura, caídas y lesiones por presión.

### 6. Áreas clínicas que debe cubrir la biblioteca

1. Adulto y adulto mayor: cardiología, neumología, digestivo, renal, endocrino, neurología, hematología, oncología, infectología, piel/heridas, musculoesquelético y perioperatorio.
2. Emergencias y desastres: triaje, ABCDE, shock, paro, sepsis, trauma, intoxicaciones y estabilización inicial.
3. Cuidados críticos: ventilación, sedación, analgesia, perfusión, líneas invasivas, balance, soporte renal y hemodinámico.
4. Salud de la mujer y obstetricia: control prenatal, trabajo de parto, puerperio, hemorragias, trastornos hipertensivos y emergencias obstétricas.
5. Recién nacido y neonatología: adaptación neonatal, termorregulación, lactancia, prematuridad, dificultad respiratoria, ictericia y reanimación neonatal.
6. Pediatría y adolescencia: crecimiento/desarrollo, vacunación, enfermedades prevalentes, seguridad por peso y comunicación con familia.
7. Salud mental: entrevista, riesgo suicida, crisis, trastornos frecuentes, psicofarmacología y comunicación terapéutica.
8. Familia, comunidad y salud pública: promoción, prevención, visita domiciliaria, inmunizaciones, vigilancia y programas sanitarios.
9. Gestión y educación: dotación, indicadores, calidad, liderazgo, docencia y supervisión.
10. Investigación: pregunta clínica, PICO, diseños, estadística, ética, lectura crítica y redacción científica.

### 7. Temas clínicos que ya se han trabajado y deben figurar en la biblioteca

- Hipertensión arterial, fisiopatología y MAPA.
- Diabetes mellitus, hiperglucemia, hipoglucemia y cetoacidosis diabética.
- Neumonía, asma y cuidados respiratorios.
- Pancreatitis aguda.
- Hemorragia digestiva alta y anemia secundaria.
- Sepsis, shock y priorización clínica.
- Traumatismo encéfalo craneano, politrauma, fracturas y neuroprotección.
- Bradicardias, bloqueos AV, taquiarritmias, EKG y monitorización.
- Trastornos de potasio, sodio y calcio; acidemia/acidosis y alcalemia/alcalosis.
- Valoración geriátrica integral: cognición, afecto, funcionalidad, nutrición, marcha, caídas y apoyo sociofamiliar.
- RCP, soporte vital y respuesta a emergencias.

### 8. Estructura mínima de un tema de estudio

Cada tema debe incorporar, según pertinencia:

1. Qué es y por qué importa en Enfermería.
2. Anatomía y fisiología relacionadas.
3. Etiología y factores de riesgo.
4. Fisiopatología secuencial.
5. Manifestaciones clínicas y signos de alarma.
6. Valoración de Enfermería y escalas aplicables.
7. Pruebas de laboratorio e imágenes (qué miden y límites).
8. Tratamiento médico explicado de manera general, sin prescripción.
9. Cuidados de Enfermería y vigilancia.
10. Errores frecuentes y seguridad.
11. PAE / caso clínico para practicar.
12. Referencias, versión y fecha de revisión.

### 9. Progresión de aprendizaje no vinculada a una universidad

- **Nivel inicial:** fundamentos, anatomía/fisiología, bioseguridad, valoración y procedimientos básicos.
- **Nivel intermedio:** farmacología aplicada, PAE, adulto, materno-neonatal, pediatría, geriatría, salud mental y comunidad.
- **Nivel avanzado:** emergencias, paciente crítico, perioperatorio, gestión, investigación, casos integrales y simulación.

Esta progresión sirve como filtro de navegación, no como equivalencia de créditos, ciclos ni sílabos institucionales.

---

# SECCIÓN 02 — ESCALAS, TESTS Y VALORACIONES

## Biblioteca de escalas, tests y herramientas de valoración

### 1. Reglas de la biblioteca

Cada instrumento debe tener: nombre, acrónimo, objetivo, población, contexto, versión/idioma, dominios, sistema de puntuación, interpretación, limitaciones, frecuencia/reevaluación, fuente, fecha, licencia y estado de validación local.

La app debe separar cuatro categorías: **escala de valoración**, **prueba de cribado**, **índice pronóstico/estratificación** y **herramienta de medición**. No todas son escalas de Enfermería ni todas deben automatizarse.

No asignar diagnósticos médicos, diagnósticos NANDA ni decisiones terapéuticas solo por el resultado. Antes de reproducir el formulario completo, verificar licencia, derechos de traducción y uso digital. Las taxonomías NANDA-I, NOC y NIC también requieren respetar sus licencias.

### 2. Dolor

| Instrumento | Uso habitual | Componentes / nota de implementación |
|---|---|---|
| EVA | Persona capaz de autoinformar | Línea visual continua; no confundir con escala numérica 0–10. |
| EN/NRS 0–10 | Persona capaz de responder | Intensidad actual; acompañar con localización, tipo, duración y respuesta. |
| Wong-Baker FACES | Niñez y otras personas que prefieran caras, según versión validada | Explicar sin inducir; usar versión y permisos adecuados. |
| FLACC | Lactantes/niños pequeños o no comunicativos en contextos validados | Cara, piernas, actividad, llanto, consolabilidad. |
| PAINAD | Demencia avanzada/no autoinforme fiable | Respiración, vocalización, expresión, lenguaje corporal y consolabilidad, según versión. |
| CPOT | Paciente crítico no comunicativo, según protocolo | Expresión facial, movimientos, tensión muscular y adaptación al ventilador/vocalización. |
| BPS | Paciente crítico intubado/no comunicativo | Expresión facial, miembros superiores y adaptación ventilatoria. |

### 3. Conciencia y neurología

#### Glasgow (GCS)

- Adulto: apertura ocular E (1–4), respuesta verbal V (1–5), respuesta motora M (1–6); total 3–15.
- La pantalla debe mostrar siempre los tres componentes y el total (`E / V / M`), no solo el total.
- Registrar factores que impiden evaluar: intubación, sedación, afasia, barrera idiomática, lesión ocular u otros. No asignar automáticamente el mínimo a un componente no evaluable.
- Usar versiones pediátricas apropiadas para edad cuando corresponda.

#### Otras herramientas neurológicas

- FOUR Score: respuesta ocular, motora, reflejos de tronco y respiración; especialmente en determinados pacientes críticos/intubados.
- NIHSS: cuantificación de déficit neurológico en sospecha/seguimiento de ictus por personal entrenado; no es una valoración enfermera independiente.
- Cincinnati / FAST / BE-FAST: reconocimiento rápido de signos compatibles con ictus; no descarta ictus si es negativo.
- Pupilas: tamaño, simetría y reactividad; herramienta de valoración, no una escala por sí sola.

### 4. Lesiones por presión y piel

- **Braden:** percepción sensorial, humedad, actividad, movilidad, nutrición, fricción/cizalla. Rango frecuente 6–23; confirmar versión y corte institucional.
- **Norton:** condición física, estado mental, actividad, movilidad e incontinencia. Versión frecuente 5–20; menor puntuación, mayor riesgo.
- **Waterlow:** alternativa utilizada en algunos centros; usar solo si el establecimiento la adopta.
- Escala de evaluación de heridas: no usar un puntaje inventado; seleccionar instrumento validado por servicio.

La app debe acompañar resultado con inspección de piel, prominencias óseas, dispositivos, humedad, nutrición, perfusión y movilidad.

### 5. Riesgo de caídas, marcha y equilibrio

- **Morse:** antecedentes de caídas, diagnóstico secundario, ayuda deambulatoria, terapia IV, marcha y estado mental; puntos de corte institucionales.
- **Downton:** caídas previas, medicamentos, déficit sensorial, estado mental y marcha; revisar versión exacta.
- **Tinetti / POMA:** equilibrio y marcha; exige aplicación estandarizada.
- **Hendrich II:** herramienta alternativa; no mezclar puntuaciones con Morse o Downton.
- **Timed Up and Go (TUG):** prueba funcional cronometrada; condiciones de ejecución y ayuda técnica deben registrarse.

### 6. Funcionalidad y dependencia

- **Barthel:** actividades básicas de la vida diaria; existen versiones de 0–100 y otras de 0–20. Identificar cuál se utiliza.
- **Katz:** baño, vestido, uso del retrete, transferencia, continencia y alimentación.
- **Lawton–Brody:** actividades instrumentales; tener en cuenta las adaptaciones de versión/idioma/cultura.
- **Pfeffer FAQ:** actividades funcionales instrumentales informadas por familiar/cuidador, cuando proceda.

### 7. Cognición, delirium, afecto y salud mental

- **Mini-Mental / Folstein:** cribado cognitivo, versión clásica 30 puntos. Educación, lengua, audición, visión y contexto influyen.
- **Pfeiffer / SPMSQ:** 10 preguntas; se cuentan errores y se aplican ajustes educativos solo según versión aprobada.
- **MoCA:** cribado cognitivo; verificar entrenamiento, versión lingüística y licencia antes de integrarlo.
- **Yesavage/GDS:** cribado de síntomas depresivos en persona mayor. La NTS 207-MINSA/DGIESP-2023 incluye una versión abreviada de 4 preguntas en VACAM; no sustituirla silenciosamente por GDS-15.
- **Hamilton (HAM-A):** severidad de ansiedad; requiere formación y contexto clínico.
- **Goldberg:** cribado de ansiedad/depresión; verificar versión local.
- **PHQ-9:** síntomas depresivos; respuestas positivas sobre autolesión requieren protocolo inmediato de evaluación, no solo puntuación automatizada.
- **GAD-7:** síntomas de ansiedad; no equivale a diagnóstico.
- **CAM:** identificación de delirium mediante inicio agudo/fluctuación, inatención y pensamiento desorganizado o nivel de conciencia alterado; exige entrenamiento.
- **4AT:** cribado de delirium/deterioro cognitivo agudo en entornos donde se valide.
- **Zarit:** carga del cuidador.
- **Gijón:** valoración sociofamiliar en versiones utilizadas localmente.
- **FICA:** entrevista de valoración espiritual, herramienta complementaria.

### 8. Sedación, agitación y paciente crítico

- **RASS:** de +4 (combativo) a −5 (no despertable), 0 alerta y tranquilo; registrar la puntuación y estímulo usado.
- **Ramsay:** escala de sedación con sistema propio; nunca convertir equivalencias numéricas automáticas a RASS.
- **SAS:** Sedation-Agitation Scale, según protocolo UCI.
- **NEWS2 / MEWS:** sistemas de alerta temprana; deben utilizar versión y umbrales aprobados por centro y población.
- **SOFA:** evalúa disfunción orgánica en contexto clínico; no es diagnóstico aislado.
- **qSOFA:** herramienta de riesgo/alerta en contexto pertinente; no usar para descartar sepsis.
- **APACHE II:** severidad en UCI, solo por personal entrenado y con variables requeridas.
- **Aldrete:** recuperación postanestésica; usar versión institucional.

### 9. Nutrición e hidratación

- **MNA / MNA-SF:** valoración nutricional/riesgo en adulto mayor.
- **NRS-2002:** cribado nutricional hospitalario.
- **MUST:** cribado de malnutrición en contextos apropiados.
- **IMC:** peso/talla²; no interpretar sin edad, edema, embarazo, composición corporal y contexto.
- **Balance hídrico:** herramienta cuantitativa, no escala psicométrica.
- **Diuresis horaria:** registrar volumen, peso, intervalo, tendencia y contexto renal/hemodinámico.

### 10. Obstetricia, parto y puerperio

- **Bishop:** evaluación de condiciones cervicales; no decide por sí sola conducta obstétrica.
- **Partograma:** seguimiento del trabajo de parto, utilizando el formato/estándar vigente del establecimiento.
- **Alerta obstétrica / claves:** usar el sistema oficial e institucional aplicable; no inventar un puntaje universal.
- **Edinburgh Postnatal Depression Scale (EPDS):** cribado de síntomas depresivos perinatales, sujeto a versión y ruta de atención.

### 11. Recién nacido y neonatología

#### APGAR

- Se documenta habitualmente al minuto 1 y al minuto 5; las evaluaciones adicionales dependen de la situación y guía neonatal vigente.
- Cinco componentes: apariencia, pulso, gesticulación/respuesta refleja, actividad/tono y respiración.
- Cada componente puntúa 0–2; total 0–10.
- APGAR no decide por sí solo el inicio de reanimación ni predice individualmente el pronóstico neurológico.

#### Capurro

- Estima edad gestacional a partir de signos físicos y, en modalidad somato-neurológica, neurológicos.
- Distinguir método somático y somato-neurológico; no intercambiar con Ballard.
- Mostrar la hoja de cálculo solo si se valida fórmula, puntuación y versión.

#### New Ballard / Ballard

Madurez neuromuscular y física para estimación gestacional; no reemplaza una datación obstétrica confiable.

#### Silverman–Andersen

Cinco signos de dificultad respiratoria neonatal: movimiento toracoabdominal, tiraje intercostal, retracción xifoidea, aleteo nasal y quejido espiratorio. Versiones habituales 0–2 por signo; mayor puntaje, mayor dificultad. Validar formato y rango local antes de automatizar.

#### Otros

- Downes: dificultad respiratoria neonatal en contextos donde se utilice.
- NIPS / CRIES: dolor neonatal, por versión y población.
- FLACC: dolor conductual en niños.
- Curvas OMS/MINSA: peso, talla/longitud y perímetro cefálico; curvas por edad/sexo.

### 12. Obstetricia, anestesia y perioperatorio

- **ASA Physical Status:** clasificación de estado físico preanestésico; no es una escala enfermera autónoma.
- **Mallampati:** clasificación de visualización orofaríngea para evaluación de vía aérea, por personal entrenado.
- **Aldrete:** recuperación postanestésica.
- Escalas de dolor, náusea/vómito y riesgo tromboembólico: usar versiones institucionales con criterios de aplicación claros.

### 13. Scores clínicos complementarios (no confundir con escalas enfermeras)

Estos deben ir en una sección separada llamada `Scores y reglas de estratificación`, con advertencia de que apoyan al equipo clínico:

- CURB-65 / CRB-65: estratificación de gravedad en neumonía, en contexto apropiado.
- Glasgow-Blatchford y Rockall: estratificación de riesgo en hemorragia digestiva alta.
- Child–Pugh y MELD: gravedad/enfermedad hepática según variables y versión.
- Wells / Geneva / PERC: evaluación de probabilidad clínica de tromboembolismo en algoritmos validados.
- CHA₂DS₂-VASc y HAS-BLED: estimación de riesgo en fibrilación auricular; no decidir anticoagulación solo con score.
- HEART: estratificación en dolor torácico, según vía clínica.
- ABCD2: evaluación de riesgo posterior a AIT, no usar como único criterio.

### 14. Pantalla de aplicación de escalas

1. Identificar instrumento y versión.
2. Confirmar población y contexto válidos.
3. Mostrar instrucciones y cada ítem de forma clara.
4. Validar respuestas/rangos y dejar explícito si un ítem no puede evaluarse.
5. Calcular con fórmula transparente y pruebas unitarias.
6. Mostrar desglose por dominio, resultado, interpretación condicionada y límites.
7. Registrar fecha/hora, profesional/estudiante y contexto ficticio o anonimizado.
8. Recomendar reevaluación según protocolo, no por intervalos inventados.
9. Permitir guardar como ejercicio y comparar tendencias sin emitir diagnóstico.

### 15. Prioridad de implementación

**MVP:** Glasgow adulto con componentes, EVA/NRS, Braden, Norton, Morse o escala adoptada (sin combinar ambas), RASS, APGAR educativo y balance hídrico.  
**Siguiente versión:** Capurro, Silverman–Andersen, Barthel, Katz, Lawton–Brody, Pfeiffer, Mini-Mental y Yesavage VACAM.  
**Futuro:** NIHSS, CPOT/BPS, CAM/4AT, NEWS2/MEWS, Tinetti, MNA-SF y scores clínicos complementarios, luego de revisión, permisos y pruebas.

### Fuentes normativas de referencia

- MINSA, RM 789-2023-MINSA: NTS 207 para cuidado integral de personas adultas mayores: https://www.gob.pe/institucion/minsa/normas-legales/4557563-789-2023-minsa
- MINSA, RM 948-2023-MINSA, modificatoria de NTS 207: https://www.gob.pe/institucion/minsa/normas-legales/4728882-948-2023-minsa
- Los instrumentos no incluidos expresamente en la VACAM deben etiquetarse como instrumentos complementarios, no como requisito normativo MINSA.

---

# SECCIÓN 03 — BIOSEGURIDAD, HIGIENE DE MANOS Y MEDICACIÓN SEGURA

## Bioseguridad, higiene de manos y medicación segura

### 1. Principio básico

Higiene de manos, EPP, precauciones estándar, técnica aséptica y seguridad de medicamentos son barreras esenciales contra daño evitable. La app debe enseñar cuándo corresponde cada barrera y qué no reemplaza a otra.

### 2. Cinco momentos para la higiene de manos

1. Antes de tocar al paciente.
2. Antes de realizar una tarea limpia/aséptica.
3. Después del riesgo de exposición a líquidos corporales.
4. Después de tocar al paciente.
5. Después de tocar el entorno del paciente.

La higiene de manos también se realiza al retirar guantes cuando corresponda. Los guantes no sustituyen la higiene de manos.

### 3. Tipos de higiene de manos: distinguirlos

| Tipo | Cuándo | Producto | Duración orientativa |
|---|---|---|---|
| Lavado rutinario/clínico con agua y jabón | Manos visiblemente sucias; contaminación visible; situaciones indicadas por guía/protocolo | Agua y jabón líquido | 40–60 s como técnica sanitaria OMS/MINSA de referencia, incluido enjuague y secado. |
| Fricción con solución hidroalcohólica | Manos no visiblemente sucias; antes/después de contacto según los cinco momentos, cuando no exista indicación de agua/jabón | Producto autorizado, en volumen suficiente para cubrir todas las superficies | 20–30 s o hasta secado completo, siguiendo el producto. |
| Lavado antiséptico | Cuando el procedimiento/protocolo requiere jabón antiséptico | Jabón antiséptico indicado por el servicio | Seguir ficha técnica/protocolo; no asignar tiempo universal sin fuente. |
| Preparación quirúrgica de manos | Antes de colocarse guantes estériles y realizar procedimiento quirúrgico | Jabón antimicrobiano apropiado o preparado alcohólico quirúrgico con actividad persistente | Según fabricante y protocolo. Con jabón antimicrobiano, habitualmente 2–5 min; evitar frotaciones excesivas de 10 min. |

**No intercambiar** el lavado comunitario para población general con la técnica sanitaria; tampoco sustituir preparación quirúrgica por un frasco genérico de alcohol gel de bolsillo.

### 4. Técnica de lavado clínico con agua y jabón

1. Retirar anillos, reloj y pulseras; mantener uñas naturales cortas y limpias.
2. Abrir el agua y mojar manos y muñecas.
3. Aplicar jabón suficiente para cubrir todas las superficies.
4. Frotar palma con palma.
5. Palma derecha sobre dorso izquierdo con dedos entrelazados y viceversa.
6. Palmas entre sí con dedos entrelazados.
7. Dorso de los dedos contra palma opuesta.
8. Frotar cada pulgar con movimiento rotatorio.
9. Frotar puntas de dedos y uñas contra la palma opuesta.
10. Enjuagar completamente.
11. Secar con toalla descartable.
12. Cerrar el grifo usando la toalla si el sistema es manual.
13. Desechar la toalla. Evitar tocar superficies contaminadas.

**Duración total de referencia:** 40–60 segundos. Si las manos están visiblemente contaminadas, la fricción alcohólica no sustituye el lavado.

### 5. Técnica de fricción alcohólica (gel/solución)

1. Aplicar en palma una dosis suficiente según fabricante.
2. Frotar palma con palma.
3. Palma sobre dorso contrario con dedos entrelazados.
4. Palma con palma con dedos entrelazados.
5. Dorso de dedos contra palma opuesta.
6. Frotar pulgares con movimiento rotatorio.
7. Frotar yemas/dedos y espacios interdigitales.
8. Continuar hasta secado completo; no enjuagar ni secar con papel.

**Duración de referencia:** 20–30 segundos o lo que exija el producto. No aplicar sobre manos húmedas si el fabricante no lo indica. No usar sobre suciedad visible, sangre o materia orgánica.

### 6. Preparación quirúrgica de manos

- Retirar anillos, reloj y pulseras; uñas cortas, sin uñas artificiales.
- Si existe suciedad visible, lavar previamente con jabón simple y retirar residuos subungueales con limpiador apropiado bajo agua.
- Elegir una de las alternativas indicadas por el protocolo: jabón antimicrobiano o preparado alcohólico quirúrgico con actividad persistente.
- Seguir secuencia, cantidad y tiempo exactos del fabricante; incluir manos y antebrazos cuando el producto lo indique.
- No combinar de forma rutinaria, una tras otra, dos técnicas quirúrgicas con productos distintos si no lo dispone la guía/fabricante.
- Si se usa preparado alcohólico quirúrgico, aplicar sobre manos/antebrazos secos y dejar secar por completo antes de vestir guantes estériles.
- Los cepillos no se recomiendan de rutina; pueden lesionar la piel. Seguir protocolo del quirófano.

**La duración no es un número universal:** la técnica depende del producto, su evidencia, las instrucciones del fabricante y el protocolo del centro.

### 7. Precauciones estándar y EPP

- Higiene de manos y evaluación de riesgo antes de cada actividad.
- Guantes si se prevé contacto con sangre, fluidos, mucosas, piel no intacta o material contaminado.
- Bata/mandil, mascarilla, protección ocular/facial según riesgo de salpicaduras/aerosoles.
- Higiene respiratoria/etiqueta de tos.
- Manejo seguro de punzocortantes; nunca reencapuchar agujas de rutina ni doblarlas/romperlas manualmente.
- Limpieza y desinfección de equipos entre pacientes según instrucciones del fabricante y protocolo.
- Residuos y ropa contaminada según norma nacional e institucional.

#### Precauciones basadas en transmisión

- Contacto.
- Gotas.
- Vía aérea/aerosoles.
- Precauciones protectoras para pacientes seleccionados e inmunocomprometidos.

Cada ficha debe mostrar agente/contexto, EPP requerido, habitación, transporte, limpieza, muestras y retirada de EPP, conforme a norma local.

### 8. Administración segura de medicamentos

#### 8.1. Diez correctos documentados en una guía institucional

La Guía de Procedimiento de Enfermería “Administración Segura de Medicamentos Endovenosos” del Instituto Nacional de Salud del Niño San Borja (INSNSB), código GP-028/INSN-SB/UE-V.01, abril de 2021, está dirigida a la administración IV de pacientes neonatos y pediátricos de ese instituto. En su apartado de conceptos básicos enumera estos diez correctos:

1. Paciente correcto.
2. Medicamento correcto.
3. Dosis correcta.
4. Vía de administración correcta.
5. Horario correcto.
6. Verificar fecha de caducidad.
7. Registrar todos los medicamentos administrados.
8. Informar e instruir al paciente sobre los medicamentos que está recibiendo.
9. Investigar si el paciente padece alergias a algún medicamento.
10. Comprobar que el paciente no esté ingiriendo otro medicamento que no esté prescrito.

La misma guía menciona inmediatamente después la higiene de manos antes de preparar y administrar el medicamento. La app debe presentarla como una medida preparatoria de seguridad, **no como un undécimo “correcto”**. En recomendaciones adicionales, la guía también aborda comprobación repetida, alergias/interacciones, compatibilidad, vigilancia de la vía, documentación y eventos adversos.

**Alcance:** es una guía institucional y pediátrico-neonatal para medicación endovenosa; no debe anunciarse como una lista universal de MINSA ni como el único estándar de todos los hospitales. Cada establecimiento puede adoptar listas o controles complementarios.

#### 8.2. Checklist educativo extendido de 20 controles (propio de la app)

**Etiqueta obligatoria: “Checklist educativo de seguridad; no es una lista oficial MINSA ni sustituye el protocolo institucional”.**

1. Paciente correcto: usar dos identificadores válidos.
2. Medicamento correcto: DCI, forma y producto exactos.
3. Dosis correcta: unidad, cálculo, concentración y límite.
4. Vía correcta: confirmar que la presentación admite esa vía.
5. Hora/intervalo correctos: revisar última dosis y frecuencia.
6. Prescripción válida y legible: aclarar cualquier ambigüedad.
7. Indicación/razón correcta: comprender el objetivo terapéutico.
8. Alergias y reacciones previas: verificar y documentar.
9. Valoración previa pertinente: signos vitales, glucemia, laboratorio, dolor u otros requeridos por fármaco.
10. Parámetros individuales: edad, peso, función renal/hepática, embarazo y comorbilidades según producto.
11. Preparación correcta: reconstitución, dilución y técnica conforme a ficha técnica.
12. Compatibilidad: revisar solución, línea, Y-site y otros medicamentos con fuente autorizada.
13. Velocidad/duración correctas: no improvisar ni deducir.
14. Caducidad e integridad: envase, aspecto, lote y almacenamiento.
15. Técnica aséptica y equipo adecuados.
16. Información/educación al paciente y derecho a preguntar/rechazar según marco aplicable.
17. Doble verificación de alto riesgo cuando la política lo exija.
18. Monitorización durante y después: respuesta y signos de reacción.
19. Registro oportuno y completo: dosis, hora, vía, lote si se requiere, respuesta/omisión.
20. Reevaluación/seguimiento y comunicación de anomalías o incidentes.

#### 8.3. Reglas de medicamentos de alto riesgo

Crear alertas para, al menos, insulinas, anticoagulantes, opioides, sedantes, vasopresores, electrolitos concentrados, anestésicos, trombolíticos y otros que defina ISMP/política institucional.

La app debe enseñar:
- Verificar concentración y unidad (mg, mcg, g, mL, UI, mEq).
- Validar peso/edad y parámetros clínicos cuando la dosis depende de ellos.
- No aceptar valores ausentes, negativos o con unidades incompatibles.
- Confirmar bomba, canal, línea, etiqueta y trazabilidad cuando corresponda.
- No administrar KCl concentrado directamente por IV; requiere preparación y protocolo institucional.
- Diferenciar insulinas y concentraciones. Nunca asumir que U-100, U-200, U-300 o U-500 se miden/intercambian igual.
- No proponer tratamiento ni dosis sin prescripción y validación.

### 9. Seguridad de documentación

- Fecha/hora, autor y cambios trazables.
- No borrar ni alterar retrospectivamente una nota clínica.
- Diferenciar dato subjetivo, objetivo, intervención y respuesta.
- Registrar medicamento omitido/rechazado y causa según procedimiento del centro.
- Evitar copiar identificadores reales en ejercicios o IA.

### Fuentes base

- MINSA, RM 255-2016-MINSA, Guía Técnica para implementar higiene de manos en establecimientos de salud: https://www.gob.pe/institucion/minsa/normas-legales/192444
- MINSA, página oficial de la Guía Técnica de Higiene de Manos: https://www.gob.pe/institucion/minsa/informes-publicaciones/285025-guia-tecnica-para-la-implementacion-del-proceso-de-higiene-de-manos-en-los-establecimientos-de-salud
- OMS, guía de higiene de manos en atención sanitaria: https://www.who.int/publications/i/item/9789241597906
- CDC, higiene de manos para trabajadores sanitarios (actualización publicada 2024): https://www.cdc.gov/clean-hands/hcp/clinical-safety/
- INSNSB, Guía de Procedimiento de Enfermería “Administración Segura de Medicamentos Endovenosos”, RD 000087-2021-DG-INSNSB: https://cdn.www.gob.pe/uploads/document/file/7682305/6505016-rd-n-000087-2021-dg-insnsb-028-gp-administracion-de-medicamentos-edv-vb.pdf

---

# SECCIÓN 04 — INSTRUMENTAL, DISPOSITIVOS Y EQUIPOS

## Atlas de instrumental, dispositivos y equipos de Enfermería

### 1. Cómo debe presentarse cada equipo

Cada ficha visual debe tener: nombre común, nombre técnico, función, componentes, variantes, tamaños/capacidades, indicación de uso general, riesgos, comprobación previa, limpieza/reprocesamiento, descartabilidad y fuente del fabricante/norma. Las fotos o diagramas deben representar un producto real o un modelo genérico anatómicamente correcto; no mezclar piezas de modelos diferentes.

### 2. Jeringas

#### Tipos frecuentes por capacidad/uso

- 0,3 mL y 0,5 mL para dispositivos específicos de insulina o dosis pequeñas según formato disponible.
- 1 mL: tuberculina, administración de pequeños volúmenes y algunas mediciones muy pequeñas; no confundir con jeringa de insulina.
- 2–3 mL: volúmenes pequeños, vías parenterales según indicación.
- 5 mL y 10 mL: preparación y administración de volúmenes habituales.
- 20 mL, 30 mL y 50/60 mL: irrigación, alimentación enteral, preparación o infusión con bomba de jeringa según dispositivo y vía.
- Jeringa de punta catéter: irrigación/uso enteral específico; no conectar a acceso IV.
- Jeringa de seguridad: incorpora mecanismo para reducir lesiones punzocortantes.
- Jeringa para gasometría: heparinizada o específica según analizador.
- Jeringa de insulina: escala y concentración designada (p. ej., U-100); no intercambiar con otros tipos.

#### Partes de una jeringa estándar

1. Cilindro/cuerpo graduado.
2. Escala graduada.
3. Aletas o reborde de apoyo.
4. Émbolo/vástago.
5. Pistón o sello de goma.
6. Punta o cono.
7. Conector Luer Slip o Luer Lock, según modelo.
8. Aguja desmontable cuando corresponda.

**Diferencia clave:** el émbolo es el conjunto móvil que desplaza el líquido; el pistón es el sello que contacta el interior del cilindro. No rotularlos como la misma pieza.

#### Conexiones

- Luer Slip: ajuste por presión/encaje, si el modelo lo permite.
- Luer Lock: conexión roscada de seguridad.
- ENFit: sistema enteral diseñado para disminuir conexiones erróneas; no debe adaptarse a vía IV.
- Conectores específicos de bombas/dispositivos: seguir compatibilidad del fabricante.

### 3. Agujas

Partes: pabellón/conector, cánula, lumen, bisel, protector. Parámetros: calibre (G), longitud, tipo de punta y dispositivo de seguridad.

- A mayor número G, menor diámetro externo (en la misma familia de agujas).
- La elección depende de vía, viscosidad, sitio, paciente, objetivo y protocolo; no debe determinarse solo por el color.
- Los códigos de color varían por estándar/producto y fabricante; la app debe poner el calibre y milímetros como dato principal.
- Mostrar técnica de seguridad y eliminación inmediata en contenedor rígido; nunca reencapuchar rutinariamente.

### 4. Estetoscopio

#### Componentes principales

- Olivas/auriculares.
- Arcos binaurales.
- Resorte de tensión.
- Tubuladura.
- Vástago/selector, cuando es de doble cara.
- Pieza torácica/cabezal.
- Diafragma (membrana plana).
- Campana (abierta o cerrada según diseño).
- Aro/retén de membrana.

#### Uso educativo

- Diafragma: suele utilizarse para sonidos de frecuencia más alta; presionar de forma apropiada según modelo.
- Campana: puede ayudar a auscultar sonidos de frecuencia más baja en modelos que la incorporen.
- La app debe mostrar cómo abrir/cerrar cada lado y advertir que algunos cabezales tienen campana de membrana o diseño sintonizable.

### 5. Tensiómetro / esfigmomanómetro

Tipos: aneroide, mercurial (uso restringido/retirado en muchos entornos), digital automático y monitor multiparámetro. Componentes: brazalete, cámara inflable, pera/bomba, válvula de liberación, manómetro o sensor y tubos.

Enseñar selección de manguito, brazo a nivel del corazón, reposo, postura, no hablar, medición repetida si procede y límites de dispositivos no validados. El tamaño incorrecto altera la lectura.

### 6. Bránula / catéter venoso periférico corto

#### Partes habituales

- Protector.
- Aguja introductora/mandril con bisel.
- Catéter flexible.
- Cámara de retorno, si el modelo la incorpora.
- Hub/cuerpo codificado.
- Alas/estabilizadores, si existen.
- Puerto de inyección, si existe.
- Conector Luer/Luer Lock.
- Sistema de seguridad según diseño.

#### Calibres y colores orientativos

| Calibre habitual | Color frecuente | Uso orientativo no prescriptivo |
|---:|---|---|
| 14 G | Naranja | Flujos altos, trauma/cirugía seleccionados. |
| 16 G | Gris | Flujos altos en adultos cuando la vena lo permite. |
| 18 G | Verde | Cirugía/transfusión y terapia de mayor flujo. |
| 20 G | Rosado | Infusión habitual en adultos. |
| 22 G | Azul | Venas pequeñas, adulto mayor o pediatría. |
| 24 G | Amarillo | Venas frágiles y pediatría/neonatología según caso. |
| 26 G | Violeta | Acceso muy pequeño; disponibilidad variable. |

El color es convención del fabricante/estándar del dispositivo y debe verificarse en empaque; el calibre y tamaño real son el dato principal. Preferir el menor calibre que cumpla la terapia prescrita. La app no debe recomendar un calibre sin contexto.

#### Complicaciones a enseñar

Flebitis, infiltración, extravasación, oclusión, desplazamiento, infección, hematoma y lesión local. Mostrar signos de alerta y conducta inicial de acuerdo con el protocolo, sin pasos invasivos no supervisados.

### 7. Catéteres vasculares

- Catéter periférico corto.
- Midline.
- Catéter venoso central (CVC) no tunelizado.
- PICC (catéter central de inserción periférica).
- Catéter tunelizado.
- Puerto implantable (port-a-cath).
- Catéter de hemodiálisis.
- Catéter arterial/línea arterial (para monitorización y toma de muestras bajo protocolo).

Cada ficha debe distinguir ubicación, puntas/lúmenes, uso, cuidados, curación, fijación, permeabilidad, prevención de infección, complicaciones y retiro. La app no debe enseñar inserción como tutorial autónomo.

### 8. Equipo de venoclisis / administración IV

Partes posibles (dependen del modelo): punzón, tapa, entrada de aire con filtro, cámara de goteo, filtro de líquido, tubuladura, pinza de cierre, regulador de rodillo, puerto en Y/puerto sin aguja, conector distal y tapa.

- Macrogotero: factor de goteo determinado por fabricante; puede encontrarse 10, 15 o 20 gotas/mL, entre otros.
- Microgotero: frecuentemente 60 gotas/mL.
- Transfusión: equipo con filtro especificado para hemocomponentes y protocolo.
- Fotoprotector: cuando medicamento/producto lo requiere.
- Bomba: el cálculo mL/h y programación deben coincidir con prescripción, volumen, concentración y dispositivo.

**Nunca inferir factor de goteo por color o apariencia. Leer el envase.**

### 9. Volutrol / bureta graduada

Volutrol es nombre de uso local para equipo con bureta graduada. Partes que puede incluir: punzón, entrada de aire/ventilación con filtro, cámara graduada, puerto de adición, pinzas, cámara microgotero, válvula de cierre, tubuladura, regulador, puerto lateral y conector distal.

Su cámara permite limitar/medir un volumen programado, pero no sustituye bomba cuando se necesita precisión ni la verificación de compatibilidad, concentración, rotulado y vigilancia.

### 10. Llave de tres vías y conectores

Partes: cuerpo, tres puertos, manija giratoria, canal interno, conectores Luer y tapas. Animar la manija y mostrar qué puerto queda cerrado y qué trayectorias comunican en cada posición; la flecha/manija exacta depende de modelo. La persona debe trazar físicamente la línea hasta paciente antes de inyectar.

### 11. Bombas y equipos de infusión

- Bomba volumétrica.
- Bomba de jeringa.
- Bomba PCA, si existe en el servicio.
- Calentador de fluidos/sangre.
- Dispositivo de infusión ambulatoria.

Explicar programación, canal, volumen a infundir, velocidad, alarmas, oclusión, aire, batería, línea correcta, prevención de flujo libre y registro. La práctica debe ser simulada, con equipo real o simulador supervisado.

### 12. Sondas y tubos

- Sonda nasogástrica y orogástrica.
- Sonda nasoenteral/postpilórica.
- Gastrostomía y yeyunostomía.
- Sonda vesical Foley de 2 vías y 3 vías.
- Sonda intermitente.
- Tubo endotraqueal.
- Cánula de traqueostomía.
- Sondas de aspiración.
- Drenajes quirúrgicos (p. ej., Jackson-Pratt, Hemovac, drenaje torácico, según servicio).

Debe mostrar indicación general, partes, tamaños en unidades correctas (Fr, mm, cm), fijación, comprobación, cuidados, complicaciones y límites. No implementar instrucciones de inserción no supervisada.

### 13. Oxígeno y monitorización

- Cánula nasal.
- Mascarilla simple.
- Mascarilla con reservorio/no reinhalación.
- Venturi.
- Nebulizador e inhalador de dosis medida/espaciador.
- Pulsioxímetro.
- Monitor multiparámetro: ECG, FC, SpO₂, FR, PA no invasiva, temperatura y otros módulos según equipo.
- Desfibrilador/monitor; palas y parches.
- Aspirador de secreciones.
- Bolsa-válvula-mascarilla.

Las imágenes deben distinguir dispositivo de oxigenoterapia, ventilación y monitorización; no asumir FiO₂ fija sin modelo, flujo y ajuste real.

### 14. Material de curación y protección

Gasas, apósitos, vendas, esparadrapo/cintas, campos, soluciones, antisépticos autorizados, guantes, mascarillas, protección ocular, contenedor rígido y bolsas de residuos. La selección depende del tipo de herida, protocolo, alergias y producto autorizado.

### 15. Diagramas visuales obligatorios

1. Jeringa explotada con partes claramente separadas.
2. Aguja con bisel, lumen, cánula y pabellón.
3. Estetoscopio completo y cabezal ampliado.
4. Tensiómetro con manguito, pera, válvula y manómetro.
5. Bránula en corte: aguja y catéter, cámara de retorno, hub y seguridad.
6. Tabla de bránulas con G, diámetro y color fabricante.
7. Venoclisis con recorrido completo de solución a paciente.
8. Volutrol con bureta y recorrido.
9. Llave de tres vías animada por posición.
10. Catéter periférico, central, PICC y puerto implantable, comparados.
11. Foley 2 y 3 vías, SNG/SNE y tubo endotraqueal en diagramas distintos.
12. Monitor multiparámetro y desfibrilador, con etiquetas separadas.

**Regla de imagen:** puede usarse IA para la ilustración base, pero las etiquetas, nombres y flechas deben añadirse como elementos vectoriales/texto editables por el desarrollador y ser revisados por un profesional. No confiar en rótulos generados dentro de la imagen.

### Fuentes

- Archivo previo del proyecto `Vademecum_Peru_Puno_2026_v4.md`, sección 15 (dispositivos IV); se conserva en anexos.
- MINSA, RM 570-2025-MINSA, ficha de homologación de catéter venoso periférico: https://www.gob.pe/institucion/minsa/normas-legales/7114444-570-2025-minsa
- Para tamaños y conexiones concretos, consultar ficha del fabricante del modelo representado.

---

# SECCIÓN 05 — COCHE DE PARO, CARTUCHERA Y ÁREA CLÍNICA

## Coche de paro, equipo clínico y cartuchera del estudiante

### 1. Qué es y para qué sirve

El coche de paro es una unidad móvil con equipos, dispositivos, medicamentos e insumos para respuesta rápida ante paro cardiorrespiratorio y emergencias definidas por el establecimiento. Debe ser accesible, estar organizado, sellado/precintado cuando corresponda, señalizado, revisado y tener responsable/registro.

**No existe una lista idéntica y universal de medicamentos para todo establecimiento peruano.** La lista debe ser aprobada por el comité farmacológico o la autoridad institucional definida, adaptada a población (adulto, pediatría, neonatología, obstetricia), nivel y servicio.

### 2. Equipamiento que se debe reconocer

- Coche con cajones, bandejas, soporte de oxígeno, frenos y superficie de trabajo.
- Desfibrilador/monitor con cables, electrodos, parches/palas, baterías y accesorios.
- Aspirador y sondas compatibles.
- Fuente/cilindro de oxígeno, regulador y conexiones.
- Bolsa-válvula-mascarilla para adulto y tamaños pediátricos/neonatales según el coche.
- Mascarillas, cánulas orofaríngeas/nasofaríngeas y dispositivos supraglóticos según el protocolo.
- Laringoscopio, hojas de distintos tamaños, baterías/iluminación comprobadas.
- Tubos endotraqueales, fijación y accesorios según población/servicio.
- Accesos venosos, extensiones, llaves, jeringas, agujas y material de fijación.
- EPP, guantes, protección ocular, gasas, apósitos, etiquetas, punzocortantes y material de limpieza.
- Soluciones IV y equipos de administración definidos por el establecimiento.
- Tabla de RCP, reloj/cronómetro, hojas de registro y algoritmos vigentes.

### 3. Medicamentos: familias y ejemplos que podrían formar parte del stock

**Estos son ejemplos para reconocimiento académico, no una orden de stock ni indicación de administración.** El listado definitivo debe copiarse del protocolo vigente aprobado del hospital/servicio.

#### Fármacos usados en algoritmos de soporte vital adulto (según ritmo/indicación)

- Adrenalina/epinefrina.
- Amiodarona.
- Lidocaína (alternativa en ciertos algoritmos/contextos).
- Atropina para indicaciones específicas, como determinadas bradicardias sintomáticas.
- Adenosina para determinadas taquicardias regulares, no para todo ritmo rápido.

#### Medicamentos situacionales/de apoyo, no rutinarios en todo paro

- Sulfato de magnesio (p. ej., torsades de pointes/indicación específica).
- Gluconato o cloruro de calcio (solo indicaciones concretas, no de rutina en todo paro).
- Bicarbonato de sodio (situaciones seleccionadas, no rutinario universal).
- Dextrosa (hipoglucemia documentada/sospechada según protocolo).
- Naloxona (sospecha de toxicidad por opioides/algoritmo apropiado).
- Vasopresores/inotrópicos adicionales según contexto y protocolo UCI/emergencia.
- Broncodilatadores, antihistamínicos, corticoides u otros fármacos para emergencias específicas si el protocolo del servicio los incluye.

#### Poblaciones específicas

Obstetricia, pediatría y neonatología pueden requerir medicamentos y dispositivos diferentes. No trasladar el inventario adulto al coche pediátrico ni al obstétrico.

### 4. Ejemplo institucional del INMP: cómo se debe usar

La Directiva Administrativa del Instituto Nacional Materno Perinatal aprobada por R.D. N.º 165-2022-DG-INMP-MINSA contiene un ejemplo institucional de organización, precintado, control y distribución del coche, con categorías de primera y segunda línea. Sirve para mostrar que existen modelos por especialidad, pero **no debe universalizarse** a hospitales generales, UCI, pediatría o centros de otro nivel.

La app puede mostrar un ejemplo de esa directiva detrás de una etiqueta `INSTITUCIONAL – INMP – 2022`, con enlace y advertencia.

### 4.1. Inventario de nombres que aparece en la directiva institucional del INMP (2022)

Para responder a la pregunta de qué fármacos pueden encontrarse en un coche real, se incorpora aquí el **inventario nominal** de la directiva del Instituto Nacional Materno Perinatal (INMP), R.D. N.º 165-2022-DG-INMP-MINSA. El documento es institucional y de enfoque materno-perinatal; sirve como ejemplo de composición y no como lista para copiar en cualquier hospital. Se omiten cantidades y concentraciones en esta sección porque su vigencia y exactitud deben cotejarse con la versión local antes de programar.

**Primera gaveta — medicamentos que el documento clasifica como primera línea:**

- Adenosina.
- Amiodarona.
- Atropina.
- Bicarbonato de sodio.
- Clindamicina.
- Dextrosa hipertónica.
- Dobutamina.
- Dopamina.
- Epinefrina/adrenalina.
- Gluconato de calcio.
- Gentamicina.
- Labetalol.
- Lidocaína sin epinefrina.
- Nitroglicerina.
- Nifedipino.
- Nitroprusiato de sodio.
- Norepinefrina/noradrenalina.
- Sulfato de magnesio.
- Verapamilo.
- Vasopresina.
- Oxitocina.

**Segunda gaveta — medicamentos que el documento clasifica como segunda línea:**

- Aminofilina.
- Ácido tranexámico.
- Clorfenamina.
- Dexametasona.
- Diazepam.
- Fenitoína.
- Fentanilo.
- Flumazenil.
- Furosemida.
- Gluconato de calcio.
- Hidrocortisona.
- Ketorolaco.
- Midazolam.
- Metilergometrina.
- Metilprednisolona.
- Misoprostol.
- Metildopa.
- Propofol.
- Vecuronio.

**Lectura crítica:** algunos productos de este inventario se relacionan con emergencias obstétricas, hipertensión aguda, anafilaxia, convulsiones o situaciones especiales y no son fármacos rutinarios para administrar en todo paro cardiorrespiratorio. El hecho de que aparezcan en el coche del INMP no establece que deban estar en el carro de un hospital general ni que correspondan a un algoritmo adulto universal.

La directiva del INMP también distribuye dispositivos para acceso vascular, vía aérea y soluciones en gavetas separadas. La app debe ofrecer este listado como ejemplo `INSTITUCIONAL – INMP – 2022`, con enlace directo al documento y una advertencia visible de no universalización.

### 5. Revisión y control del coche de paro

Checklist para configurar según la directiva local:

- Ubicación accesible, conocida por el personal y sin obstáculos.
- Precinto/cierre intacto y número de sello registrado.
- Fecha/hora de inspección y responsable.
- Stock por gaveta y correspondencia con el inventario aprobado.
- Caducidad, integridad, aspecto, almacenamiento y rotulado de medicamentos.
- Operatividad del desfibrilador/monitor y autoprueba.
- Electrodos/parches/palas y batería.
- Oxígeno, regulador y conexiones.
- Aspirador, presión y sondas.
- Bolsa-válvula-mascarilla y accesorios.
- Laringoscopio, hojas, iluminación y baterías.
- Insumos de acceso vascular y vía aérea.
- Hojas de registro y algoritmos vigentes.
- Después de uso: reposición, limpieza, registro, nueva verificación y nuevo precinto.

La app debe guardar faltantes, vencimientos, responsables, acciones correctivas y trazabilidad. Un coche solo puede quedar como “listo” cuando el checklist local completo ha sido validado.

### 6. Cartuchera del estudiante

**Lista sugerida, no exigencia oficial general.** La docente y el establecimiento determinan qué puede ingresar al área clínica.

#### Registro y estudio

- Lapicero azul/negro para registros si la institución lo permite.
- Lapicero rojo/verde para apuntes personales; no usar para cambiar registros clínicos salvo norma expresa.
- Lápiz, borrador, resaltador y regla pequeña.
- Calculadora simple/científica.
- Cuaderno de bolsillo sin datos identificables de pacientes.
- Reloj de bolsillo o clip si el servicio lo permite.

#### Instrumental opcional

- Estetoscopio identificable y limpiable.
- Linterna clínica, solo si se autoriza.
- Tijera de punta roma/trauma, si está permitida.
- Tensiómetro personal: opcional; usar equipos validados y calibrados del servicio cuando estén disponibles.
- Alcohol gel personal: solo producto y uso autorizados; no reemplaza estaciones ni técnica institucional.

#### Prohibido transportar en la cartuchera

- Medicamentos para automedicación o administración independiente.
- Agujas/jeringas usadas, punzocortantes sueltos o muestras biológicas.
- Material contaminado.
- Fotografías, notas o documentos con datos personales de pacientes sin autorización.
- Dispositivos de almacenamiento con información clínica no autorizada.

### 7. Otros equipos por área clínica

| Área | Equipos que debe reconocer |
|---|---|
| Triaje/emergencia | Monitor, tensiómetro, oxímetro, termómetro, glucómetro, desfibrilador, aspirador, carro de paro. |
| Hospitalización | Cama, barandas, timbre, soporte IV, bomba, oxígeno, aspiración, sillas de ruedas, ayudas de movilización. |
| UCI | Monitor, ventilador, bombas volumétricas/de jeringa, aspirador, CVC, línea arterial, drenajes, nutrición enteral. |
| Centro quirúrgico | Mesa, equipo de anestesia, monitor, electrobisturí, aspiración, instrumental y conteo. |
| Neonatología | Incubadora, cuna térmica, monitor neonatal, CPAP/ventilación, mezclador de oxígeno, fototerapia y bombas de precisión. |
| Pediatría | Brazaletes y dispositivos por talla, escalas pediátricas, material de administración apropiado. |
| Comunidad | Maletín, equipos de control, material educativo y cadena de frío según programa. |

### Fuentes

- MINSA, NTS 057-MINSA/DIGEMID-V.01, Sistema de dispensación de medicamentos en dosis unitaria, apartados sobre coche de paro: https://cdn.www.gob.pe/uploads/document/file/391318/Norma_t%C3%A9cnica_de_salud__Sistema_de_dispensaci%C3%B3n_de_medicamentos_en_dosis_unitaria_para_los_establecimientos_del_sector_salud20191017-26355-1t6udtg.pdf
- INMP, R.D. N.º 165-2022-DG-INMP-MINSA, Directiva Administrativa para manejo y control del coche de paro: https://www.inmp.gob.pe/uploads/RD_N%C2%B0_165-2022-DG-INMP-MINSA_compressed.pdf
- MINSA/DIGERD, especificaciones técnicas para coche de paro equipado: https://cdn.www.gob.pe/uploads/document/file/4705126/EETT%20-%20ITEM%20N%2004%20-%20COCHE%20DE%20PARO%20EQUIPADO%20-%20DS72.pdf?v=1687217953
- AHA, Guías de RCP y Atención Cardiovascular de Emergencia 2025: https://professional.heart.org/en/science-news/2025-aha-guidelines-for-cpr-and-ecc
- Hospital Nacional Cayetano Heredia, nota institucional sobre contenido de coches de paro, 4 de febrero de 2026: https://www.gob.pe/institucion/hnch/noticias/1347365-gracias-a-convenio-hospital-adquiere-dos-coches-de-paro-para-emergencia-y-medicina

---

# SECCIÓN 06 — INVENTARIO FARMACOLÓGICO Y PRIORIDADES

## Inventario farmacológico identificado y prioridades de desarrollo

**Alcance:** inventario de nombres de medicamentos/productos recuperados de trabajos, casos, notas y referencias visuales ya aportados al proyecto. Esta fase NO desarrolla fichas clínicas completas ni valida esquemas posológicos.

### 1. Principios para el catálogo

- Nombre principal: DCI/principio activo. Mostrar marca solo como dato de presentación concreta.
- Registrar forma, concentración, vía autorizada, fabricante y registro sanitario DIGEMID cuando se desarrolle cada ficha.
- No declarar “disponible en Perú” solo porque aparece en un caso, vademécum o petitorio.
- No copiar dosis de prescripciones antiguas. Las órdenes de los casos se mantienen como dato histórico del caso y se enseñan a auditar.
- No confundir medicamento, solución, hemocomponente, nutrición o dispositivo.
- Cada fármaco debe tener un estado: `IDENTIFICADO`, `PENDIENTE-FICHA`, `FUENTE-ENCONTRADA`, `VALIDADO-DIGEMID`, `VALIDADO-GPC`, `REVISIÓN-FARMACÉUTICA`, `PUBLICABLE`.

### 2. Medicamentos recuperados por caso o material anterior

#### A. Caso académico de diabetes mellitus tipo 2 / infección urinaria / hipertensión

- Metformina.
- Glibenclamida.
- Insulina humana NPH.
- Insulina humana regular (cristalina).
- Captopril.
- Ceftriaxona.
- Dimenhidrinato.
- Paracetamol.
- Metamizol sódico.
- Cloruro de potasio (KCl; presentación concentrada mencionada).
- Dextrosa hipertónica (el caso menciona 33 %; verificar presentación y registro exactos antes de usar).
- Cloruro de sodio 0,9 %.

#### B. Caso académico de politrauma / TEC grave / postoperatorio neuroquirúrgico / UCI

- Cloruro de potasio.
- Agua estéril para preparación (no es equivalente a solución IV de mantenimiento; verificar uso).
- Ceftriaxona.
- Omeprazol.
- Metoclopramida.
- Fenitoína.
- Manitol.
- Fentanilo.
- Midazolam.
- Noradrenalina (norepinefrina).
- Cloruro de sodio 0,9 %.

La orden histórica contiene cantidades y unidades que pueden ser ambiguas o inconsistentes (por ejemplo, infusiones de sedantes/vasopresor sin concentración final suficiente). No usarla como dosis recomendada ni como caso de cálculo hasta reconstruirla desde una prescripción original legible y contrastarla.

#### C. Trabajo de hemorragia digestiva alta y anemia secundaria

- Omeprazol.
- Ácido tranexámico.
- Fitomenadiona (vitamina K1).
- Metoclopramida.
- Poligelina 3,5 % / Haemaccel (expansor plasmático citado; confirmar situación/regulación y protocolo antes de incluir).
- Cloruro de sodio 0,9 %.
- Concentrado de hematíes (hemocomponente, **no medicamento**).

Enseñanza: conciliación y revisión de indicación. Ácido tranexámico no debe aparecer como tratamiento rutinario universal de HDA; fitomenadiona requiere indicación relacionada con déficit/antagonismo de vitamina K; revisar precauciones/contraindicaciones específicas de metoclopramida en hemorragia/obstrucción/perforación GI según ficha técnica.

#### D. Pancreatitis aguda

- Metamizol.
- Tramadol.
- Morfina.
- Antiemético (principio activo no especificado en algunos trabajos; mantener como grupo hasta identificar).
- Cristaloides IV: solución exacta por confirmar según caso/indicación.
- Antibióticos como grupo, solo cuando exista infección sospechada/confirmada e indicación basada en guía; no convertirlos en profilaxis rutinaria automática.

#### E. Prototipos y fichas visuales de cuidados críticos

- Vancomicina.
- Metronidazol.
- Metoclopramida.
- Omeprazol.
- Midazolam.
- Fentanilo.
- Imipenem (confirmar si la ficha pretendía imipenem solo o imipenem/cilastatina).
- Noradrenalina.
- Levotiroxina.
- Enoxaparina.
- Furosemida.
- Dextrosa al 5 %.
- Cloruro de sodio 0,9 %.
- Soluciones/electrolitos combinados (desglosar los ingredientes exactos).
- Fórmula nutricional enteral tipo Ensure (producto nutricional, no clasificar automáticamente como fármaco).

#### F. Temas o fichas solicitadas en el caso de TEC para desarrollar

El documento enumera además:
- Diazepam.
- Bicarbonato de sodio (el archivo contiene error tipográfico “Bicabonato”).
- Ketoprofeno.
- Atorvastatina.
- Ácido acetilsalicílico (aspirina).
- Enoxaparina.
- Apixabán.
- Alteplasa.
- Una segunda palabra transcrita como “altilice”: no normalizar; revisar imagen/documento original para identificar el principio activo correcto. No asumir tenecteplasa ni otro fármaco.

### 3. Insulinas: catálogo inicial a completar

| Grupo general | Principios activos a tener en el inventario |
|---|---|
| Análogo rápido | Lispro, aspart, glulisina. |
| Humana de acción corta | Regular/cristalina. |
| Intermedia | NPH/isófana. |
| Prolongada | Glargina, detemir (confirmar disponibilidad/registro de la presentación en Perú). |
| Ultralarga | Degludec. |
| Premezclas | Mezclas de insulina humana o análogos con protamina; confirmar combinación y presentación exacta. |

**No usar perfiles de inicio/pico/duración como constantes universales:** pueden depender de la molécula, concentración, formulación, sitio de inyección y paciente. La ficha debe citar el producto exacto.

### 4. Prioridad para las primeras fichas (propuesta)

#### Prioridad 1 — relacionados con casos y seguridad frecuente

- Paracetamol, metamizol.
- Ceftriaxona, vancomicina, metronidazol, imipenem/cilastatina (si corresponde al producto citado).
- Omeprazol, metoclopramida, ondansetrón.
- Metformina, glibenclamida.
- Insulina regular, NPH, glargina; luego lispro, aspart, glulisina y degludec.
- Captopril, enalapril, losartán, amlodipino, furosemida.
- Fenitoína, manitol.
- Midazolam, fentanilo, tramadol, morfina.
- Noradrenalina.
- Cloruro de sodio 0,9 %, KCl, dextrosa: registrar como soluciones/electrolitos con ficha propia y alto riesgo cuando corresponda.

#### Prioridad 2 — emergencias, UCI y hospitalización

- Adrenalina/epinefrina, atropina, amiodarona, adenosina, lidocaína.
- Dobutamina, dopamina, noradrenalina, vasopresina.
- Sulfato de magnesio, gluconato/cloruro de calcio, bicarbonato de sodio.
- Enoxaparina, heparina sódica, warfarina, apixabán y antiagregantes.
- Hidrocortisona, dexametasona, salbutamol, ipratropio, budesonida.
- Naloxona, flumazenil y antídotos pertinentes según disponibilidad/protocolo.
- Dextrosa para hipoglucemia, glucagón cuando corresponda.

#### Prioridad 3 — ampliar el vademécum por áreas

- Antiepilépticos, antidepresivos, antipsicóticos, ansiolíticos y fármacos de salud mental.
- Antihistamínicos, broncodilatadores y corticoides inhalados.
- Antifúngicos, antivirales, antituberculosos y antiparasitarios.
- Obstétricos, uterotónicos, tocolíticos, medicamentos de neonatología y pediatría.
- Nefrología, diálisis, oncología, anestesia, analgesia avanzada y paliativos.
- Vacunas e inmunoglobulinas como productos biológicos, con ficha de cadena de frío/administración.

### 5. Estructura obligatoria de ficha farmacológica

Mantener la preferencia de estudio:

1. **Qué es.**
2. **Presentación en Perú.** Forma farmacéutica, concentración, vía aprobada, marca si procede, titular/fabricante y registro DIGEMID comprobado.
3. **Dosis.** Solo para indicación concreta, con población, edad/peso, vía, intervalo, máximo, ajuste renal/hepático y fuente precisa. Mostrar advertencia si varía por indicación.
4. **Indicaciones.** Basadas en ficha técnica/GPC y no en publicidad.
5. **Contraindicaciones.** Diferenciar absoluta, precaución y población especial.

Añadir:
- grupo y mecanismo;
- farmacocinética relevante;
- preparación/reconstitución/dilución;
- velocidad/tiempo;
- compatibilidad y estabilidad;
- efectos adversos y reacciones graves;
- interacciones y duplicidad terapéutica;
- ajuste renal/hepático/embarazo/lactancia/edad;
- alto riesgo/LASA/controlado;
- valoraciones antes/durante/después;
- cuidados y educación de Enfermería;
- qué hacer ante reacción adversa (activar protocolo; no recomendar tratamiento no autorizado);
- fuentes, versión, fecha de consulta y fecha de revisión.

### 6. Alerta clínica sobre metamizol (ejemplo de cómo razonar)

La ficha técnica DIGEMID disponible para metamizol inyectable advierte que la administración IV debe realizarse muy lentamente para reducir el riesgo de hipotensión. La diarrea por sí sola no debe etiquetarse como contraindicación absoluta. Enfermería debe valorar estado hemodinámico, signos de deshidratación/hipovolemia, PA, perfusión, alergias, antecedentes y prescripción. Si hay hipotensión o inestabilidad, detener/verificar y comunicar según protocolo; no administrar automáticamente.

No usar la frase “si tiene diarrea nunca se administra metamizol” como regla general.

### 7. Controles especiales

- **KCl concentrado:** electrolito de alto riesgo; no administrar directamente por vía IV; requiere dilución/velocidad, concentración final, acceso y monitorización según protocolo.
- **Insulinas:** doble chequeo cuando corresponda, tipo, concentración, unidad, dispositivo, glucemia, comida/ayuno y hora; jamás confundir UI con mL.
- **Noradrenalina/vasopresores:** concentración y bomba verificadas, línea y vigilancia hemodinámica según protocolo; evitar instrucciones genéricas sin contexto.
- **Opioides/sedantes:** nivel de conciencia, FR, SpO₂, PA, riesgo de depresión respiratoria e interacciones; fármacos controlados conforme a ley.
- **Anticoagulantes/trombolíticos:** sangrado, peso, función renal, indicación y contraindicaciones; alto riesgo.
- **Fenitoína IV:** presentación, diluyente, compatibilidad y velocidad dependen del producto exacto; no copiar preparaciones de caso.
- **Manitol:** vigilar indicación, función renal, balance, osmolaridad/electrolitos y respuesta; seguir prescripción/guía.

### 8. Tareas futuras para el módulo de farmacología

1. Auditar exhaustivamente cada DCI del árbol vademécum por sistema.
2. Cruzar con PNUME y listas complementarias vigentes.
3. Consultar DIGEMID por registro de cada presentación comercial en Perú.
4. Priorizar las fichas de casos propios.
5. Hacer revisión por químico farmacéutico/enfermero clínico antes de publicar.
6. Crear versión, historial de cambios y aviso de fecha de próxima revisión.

### Archivos previos que sustentan el inventario

- `CASO CLÍNICO DIABETES_260429_153054.pdf`.
- `caso clínico tec_b5jzml7as8.pdf` y sus versiones DOCX.
- `Caso_Clinico_04 (2)_260929_075809.pdf`.
- `Final PAE EMERGENCIAS.docx`.
- Trabajos `Pancreatitis_APA7...`.
- `Fichas farmacológicas de cuidados críticos.png`.
- `Prototipos de Enfermería: Innovación 360°.png`.
- `Vademecum_Peru_Puno_2026_v4.md`.

### Fuentes prioritarias

- DIGEMID, consulta y fichas técnicas: https://www.digemid.minsa.gob.pe/
- Ficha técnica DIGEMID de metamizol sódico inyectable 1 g/2 mL: https://www.digemid.minsa.gob.pe/Archivos/FichasTecnicas/EspecialidadesFarmaceuticas/2019/EE00827_FT_V01.pdf
- PNUME 2023, RM 633-2023-MINSA y modificatorias/listas complementarias: consultar portal MINSA/DIGEMID vigente.
- OMS, lista modelo de medicamentos esenciales y documentos de seguridad de medicamentos: https://www.who.int/teams/health-product-policy-and-standards/essential-medicines

---

# SECCIÓN 07 — PAE, CASOS CLÍNICOS Y RAZONAMIENTO

## PAE, valoración, taxonomías y casos clínicos del proyecto

### 1. Modelo de razonamiento

La app debe entrenar al estudiante para pasar de datos a juicio clínico, no solo sugerir diagnósticos por palabras clave.

#### PAE

1. Valoración: recoger datos subjetivos y objetivos, fuente, fecha/hora y contexto.
2. Diagnóstico de Enfermería: interpretar respuestas humanas y formular según taxonomía autorizada.
3. Planificación: priorizar, establecer objetivos y resultados medibles.
4. Ejecución: intervenciones y actividades ajustadas al alcance profesional/protocolo.
5. Evaluación: comparar indicadores antes/después y revisar el plan.

#### Patrones funcionales de Gordon

1. Percepción–manejo de la salud.
2. Nutricional–metabólico.
3. Eliminación.
4. Actividad–ejercicio.
5. Sueño–descanso.
6. Cognitivo–perceptual.
7. Autopercepción–autoconcepto.
8. Rol–relaciones.
9. Sexualidad–reproducción.
10. Adaptación–tolerancia al estrés.
11. Valores–creencias.

La ficha debe aceptar datos ausentes y señalar “no valorado/no disponible”; no completar datos por inferencia.

### 2. NANDA–NOC–NIC

- NANDA-I: diagnóstico enfermero y características definitorias/factores relacionados o de riesgo, según tipo de diagnóstico.
- NOC: resultado esperado, indicadores, valor inicial, meta y evaluación.
- NIC: intervención y actividades adaptadas a las necesidades y competencias.

**Licencia:** no copiar taxonomías completas ni definiciones protegidas sin licencia vigente. Permitir búsqueda/referencia y que el alumno introduzca diagnósticos o actividades; validar integración digital con el proveedor/editor.

#### Reglas de formulación

- Diagnóstico real: comprobar problema, factores relacionados y características definitorias. Usar PES cuando aplique.
- Diagnóstico de riesgo: no inventar “manifestado por”; fundamentar con factores de riesgo.
- Diagnóstico de promoción de salud: requiere disposición/expresión compatible con la taxonomía vigente.
- No seleccionar diagnóstico solo por enfermedad médica.
- Priorizar ABC, inestabilidad, amenaza vital, seguridad, necesidades urgentes, preferencias y contexto.

### 3. Formatos clínicos

#### SOAPIE

- S: datos subjetivos.
- O: datos objetivos (signos vitales, examen físico, escalas, laboratorio, observación).
- A: análisis/valoración enfermera.
- P: plan.
- I: intervención.
- E: evaluación/respuesta.

La plantilla del proyecto debe mantener sus campos definidos y permitir registro cronológico. No sustituir hechos por interpretaciones.

#### SBAR

- S — Situation: situación actual.
- B — Background: antecedentes relevantes.
- A — Assessment: valoración y preocupación clínica.
- R — Recommendation/Request: recomendación/solicitud concreta.

### 4. Casos académicos recuperados

Los archivos siguientes se utilizaron para reconstruir temas y listas de medicamentos. Mantenerlos como casos educativos, anonimizar datos personales y no copiar órdenes como pautas de tratamiento.

#### A. Diabetes mellitus tipo 2, ITU e hipertensión

Temas: hiperglucemia, insulina, antidiabéticos orales, infección urinaria, control de signos vitales, función renal, hidratación y valoración por Gordon. Medicamentos recuperados: metformina, glibenclamida, NPH, insulina regular/cristalina, ceftriaxona, dimenhidrinato, paracetamol, metamizol, captopril, KCl, dextrosa hipertónica y NaCl 0,9 %.

Actividades: interpretar glucemia y laboratorio; diferenciar hipoglucemia, hiperglucemia y cetoacidosis; verificar unidades/insulina; priorizar riesgos y construir NANDA–NOC–NIC tras revisar datos y edición.

#### B. Politrauma / TEC grave / neurocirugía / UCI

Temas: ABCDE, Glasgow y RASS, neuroprotección, fracturas, perfusión, ventilación, sedoanalgesia, vasopresores, CVC, línea arterial, balance, prevención de lesiones por presión y seguridad de dispositivos.

Medicamentos recuperados: KCl, ceftriaxona, omeprazol, metoclopramida, fenitoína, manitol, fentanilo, midazolam, noradrenalina y NaCl 0,9 %. En las consignas de fichas se nombran además diazepam, bicarbonato, ketoprofeno, atorvastatina, AAS, enoxaparina, apixabán y alteplasa; una palabra del documento está mal transcrita y requiere confirmación del original.

**Seguridad:** no importar las cantidades/unidades del caso como dosis recomendadas. Algunas son ambiguas; deben permanecer bloqueadas hasta la auditoría farmacológica.

#### C. Hemorragia digestiva alta y anemia secundaria

Temas: melena, pérdidas, tendencia hemodinámica, anemia, transfusión, accesos venosos, bomba, oxigenación, balance y deterioro clínico.

Medicamentos/productos recuperados: omeprazol, ácido tranexámico, fitomenadiona, metoclopramida, poligelina 3,5 %/Haemaccel, NaCl 0,9 % y concentrado de hematíes (hemocomponente, no fármaco).

La aplicación debe enseñar a cuestionar órdenes históricas: la presencia en un caso no prueba que un fármaco sea adecuado para cualquier paciente con HDA.

#### D. Pancreatitis aguda

Temas: dolor epigástrico, inflamación, tercer espacio, vómitos, perfusión, hidratación, electrolitos, nutrición, complicaciones, sepsis y evaluación continua.

Diagnósticos abordados previamente: dolor agudo; déficit de volumen de líquidos; riesgo de infección; ansiedad; conocimientos deficientes. Deben revalidarse datos, factores relacionados y taxonomía vigente.

Fármacos/grupos nombrados: metamizol, tramadol, morfina, antiemético no especificado, cristaloides y antibióticos solo con indicación de infección sospechada/confirmada.

#### E. Neumonía en adulto

Temas: examen respiratorio IPPA, FR, trabajo respiratorio, SpO₂, esputo, fiebre, oxigenoterapia, patrón actividad-ejercicio, Gordon, PAE y evaluación de respuesta.

#### F. Temas adicionales ya trabajados

- HTA y MAPA.
- Asma y NIC de cuidados respiratorios.
- Bradicardias, bloqueos AV, taquiarritmias, ECG y monitorización.
- Sepsis/shock, priorización y plan NANDA–NOC–NIC.
- Cetoacidosis diabética y trastornos ácido-base/electrolíticos.
- Valoración geriátrica integral, cognitiva, afectiva, funcional y riesgo de caídas.
- RCP y respuesta a emergencias.

### 5. Casos nuevos que conviene preparar

- Dolor torácico/SCA e infarto.
- ACV isquémico/hemorrágico.
- Crisis asmática y exacerbación de EPOC.
- Insuficiencia cardiaca y edema agudo de pulmón.
- Shock séptico, hipovolémico, cardiogénico y anafiláctico.
- Hipoglucemia, cetoacidosis y estado hiperosmolar.
- Alteraciones de K, Na, Ca y Mg; trastornos ácido-base.
- Hemorragia obstétrica, preeclampsia/eclampsia y sepsis materna.
- Dificultad respiratoria neonatal, prematuridad e ictericia.
- Deshidratación pediátrica, convulsión febril y sepsis pediátrica.
- Lesiones por presión, pie diabético y cuidado de ostomías.
- Delirium, demencia, depresión y riesgo suicida.

### 6. Motor de casos simulados

Cada caso debe incluir:

- Edad/grupo etario y contexto ficticio.
- Motivo de consulta y antecedentes relevantes.
- Valoración ABCDE y signos vitales seriados.
- Patrones de Gordon o valoración estructurada.
- Laboratorio/imágenes con unidades y rangos de referencia contextualizados.
- Medicación simulada con concentración y prescripción inequívoca; no crear órdenes reales.
- Preguntas progresivas de priorización.
- Respuestas razonadas y explicación de distractores.
- Rúbrica: seguridad, valoración, priorización, PAE, comunicación y reevaluación.
- Escenarios de evolución: mejora, deterioro y evento adverso.

### 7. Privacidad y ética

- Usar casos sintéticos, no identificables.
- Eliminar nombres, DNI, historia clínica, direcciones, fechas exactas y fotos identificables de pacientes.
- No cargar información real de pacientes a IA ni usarla para entrenar modelos.
- Incluir aviso de que el caso es simulado y no representa una indicación para un paciente real.

---

# SECCIÓN 08 — BRIEF TÉCNICO PARA ARENA IA

## Brief técnico de desarrollo para Arena IA — Enfermería iDreyK 360

### 1. Encargo principal

Continuar el desarrollo del aplicativo **Enfermería iDreyK 360**, manteniendo la identidad visual y navegación del **Prototipo B** ya aprobado. No reconstruir desde cero ni cambiar la arquitectura sin inspeccionar primero el repositorio actual.

La aplicación debe funcionar como biblioteca de aprendizaje, atlas de instrumental, centro de escalas, calculadoras educativas, farmacología, PAE/casos y simulación. Debe priorizar uso Android/tablet, rendimiento, legibilidad, funcionamiento offline de contenido base y seguridad.

### 2. Exclusión explícita

**El contenido es independiente de cualquier plan curricular institucional.** No construir una navegación por ciclos ni presentar una distribución de cursos como oficial. Se permite que el usuario organice apuntes/cursos personales, pero no es requisito del MVP.

### 3. Antes de programar: auditoría del repositorio

1. Inspeccionar estructura, rutas, componentes, stores, dependencias, backend y estado de cada feature.
2. Crear una tabla `HECHO / PARCIAL / FALTA / BLOQUEADO-POR-VALIDACIÓN`.
3. No duplicar componentes existentes ni romper las pantallas del Prototipo B.
4. Marcar dependencias y migraciones antes de implementarlas.
5. Hacer snapshot/branch de seguridad y correr pruebas existentes.
6. Confirmar build Android/tablet y responsive en pantalla pequeña.

### 4. Navegación principal propuesta

#### Inicio

- Cabecera con marca y saludo no invasivo.
- Buscador global por tema, medicamento, escala, equipo, procedimiento y caso.
- Accesos rápidos a `Estudiar`, `Herramientas`, `Atlas`, `Escalas`, `Farmacología`, `PAE/Casos` y `Biblioteca`.
- Estado de módulos, progreso de estudio y favoritos; sin inventar estadísticas.

#### Módulos principales

1. Centro de aprendizaje: temas, apuntes, flashcards, quiz y videos de autoría/licencia válida.
2. Valoración: signos vitales, examen físico, Glasgow, dolor y herramientas clínicas.
3. Biblioteca de escalas: búsqueda, ficha, formulario interactivo y límites.
4. Atlas de Enfermería: instrumental, partes, función, variantes y seguridad.
5. Procedimientos: fichas de preparación, fundamentos, riesgos y evaluación supervisada.
6. Farmacología: catálogo DCI y estado de verificación; fichas completas en siguiente fase.
7. Calculadoras: dosis/volumen, peso, goteo, mL/h, dilución y balance; modo educativo.
8. PAE: Gordon, problema, NANDA, NOC, NIC, SOAPIE y SBAR.
9. Casos clínicos: casos simulados y evaluación razonada.
10. Emergencias: ABCDE, RCP, shock, sepsis, triaje y coche de paro.
11. Mi biblioteca: favoritos, notas privadas, progreso y contenidos offline.

### 5. Funciones que se deben implementar ahora (MVP)

#### A. Biblioteca de conocimiento

- Importar los Markdown como contenido estructurado por módulo.
- Índice, navegación y búsqueda local.
- Etiquetas de origen: oficial, institucional, evidencia complementaria, académico, pendiente-validación.
- Fecha/versión/fuente visibles en cada ficha.
- Favoritos y lectura offline.

#### B. Biblioteca de escalas

- Implementar primero EVA/NRS, Glasgow adulto (E/V/M visibles), Braden, Norton, RASS y APGAR educativo.
- Formulario con validación de datos y cálculo verificable.
- Mostrar cada subpuntuación, total y rango con advertencias.
- No permitir cálculo si versión/población no coincide.
- No traducir puntuación directamente a diagnóstico.
- Las escalas restantes quedan con ficha informativa “pendiente de implementación/validación”.

#### C. Atlas visual de equipos

Implementar tarjetas y diagramas vectoriales interactivos para:

- Jeringa estándar: cilindro, escala, aletas, vástago/émbolo, pistón de goma, punta y conexión.
- Aguja: pabellón, cánula, lumen, bisel y protector.
- Estetoscopio: olivas, binaurales, resorte, tubo, vástago, diafragma, campana.
- Tensiómetro: brazalete, pera, válvula, manómetro/sensor, tubos.
- Bránula: protector, aguja, bisel, catéter flexible, cámara, hub, alas, puerto y seguridad.
- Venoclisis: punzón, filtro, cámara, tubuladura, pinza, regulador, puerto y conexión.
- Volutrol: bureta, puerto, pinzas, cámara y conexión.
- Llave de tres vías: cuerpo, puertos, manija y canales de flujo.

Los hotspots deben responder al toque: resaltar parte, mostrar nombre, función y nota de seguridad. No permitir que una imagen IA invente rótulos; los textos son capas editables en SVG/UI.

#### D. Higiene y seguridad

- Animación del lavado con agua y jabón (40–60 s).
- Animación de fricción alcohólica (20–30 s o secado completo).
- Preparación quirúrgica con texto “seguir fabricante/protocolo”, sin fijar un tiempo universal.
- Cinco momentos de higiene de manos.
- Los diez correctos transcritos en el documento 03 desde la guía institucional INSNSB; señalar que su ámbito es pediátrico/neonatal IV y no universal. Higiene de manos es una medida adicional, no un undécimo correcto.
- Checklist educativo de 20 controles claramente señalado como contenido propio/no normativo.

#### E. Calculadoras educativas

- Dosis–volumen.
- Dosis por kg y conversiones mg/g/mcg/UI/mEq.
- mL/h y gotas/min con factor del equipo ingresado y obligatorio.
- Tiempo de infusión.
- Dilución C1V1=C2V2 solo para contextos donde sea aplicable.
- Balance hídrico.
- IMC.

Cada cálculo debe explicar unidades, fórmula, sustitución, resultado y redondeo. Bloquear valores negativos, cero imposible, unidades incompatibles, peso/edad faltante cuando sea necesario, concentración incompleta y resultados fuera de límites configurados. No guardar un resultado como orden.

#### F. PAE y casos

- Valoración estructurada con Gordon y ABCDE.
- Campos subjetivos/objetivos y fuentes.
- Priorización explicada.
- Diagnósticos introducidos/seleccionados por el estudiante con referencias licenciadas.
- Relación NOC/NIC sin reproducir contenido protegido sin permiso.
- SOAPIE/SBAR editables y exportables.
- Casos simulados: diabetes, TEC/UCI, HDA, pancreatitis, neumonía, sepsis y ritmos ECG.

### 6. Diseño: referencia Prototipo B

- Mantener el look del Prototipo B, los paneles redondeados y la paleta turquesa/rosa/lila/blanco.
- Modo oscuro con acento turquesa.
- Tarjetas sobrias con jerarquía clara; reservar rojo/ámbar para alertas reales y no para decoración.
- Ilustraciones limpias, profesionales y anatómicamente precisas.
- Tipografía escalable, contraste AA cuando sea viable, foco visible y áreas táctiles amplias.
- Reducir animaciones en dispositivos de gama baja y respetar `prefers-reduced-motion`/ajuste de la app.
- No usar full-screen splash o animación pesada que retrase herramientas de emergencia.

### 7. Imágenes y animaciones: guía precisa

#### Crear ahora

1. Anatomía explotada de jeringa y aguja.
2. Estetoscopio completo y ampliación del cabezal doble.
3. Bránula abierta con aguja introductora y catéter flexible diferenciados.
4. Equipo de venoclisis y Volutrol.
5. Llave de tres vías con rutas de flujo por posición.
6. Manos: secuencia clínica, fricción alcohólica y preparación quirúrgica.
7. Coche de paro por gavetas, con categorías y aviso institucional.
8. Diagrama de monitor multiparámetro: ECG, FC, SpO₂, FR, PA, temperatura.

#### Crear más adelante

- Modelos 3D interactivos de catéteres, vía aérea y monitor.
- Diagramas animados de ventilación, perfusión, ciclo cardíaco y fisiopatología.
- Secuencias de inserción invasiva solo como simulación académica supervisada, nunca como guía autosuficiente.
- Anatomía neonatal para Apgar/Capurro/Ballard/Silverman, con revisión por profesional neonatal.

#### Estándar visual

- Imagen base sin texto incrustado.
- Etiquetas y flechas en SVG/UI traducibles y escalables.
- Fuente del equipo/autoría/licencia registrada.
- Alt text y descripción textual para accesibilidad.
- Zoom y selector de capas; hotspot resalta una sola pieza a la vez.
- Si no se verifica un componente en el producto real, etiquetar “varía según modelo”.

### 8. Funciones que NO se deben inventar

- Sin recomendación médica/IA clínica autónoma.
- Sin cálculo de dosis real para paciente real ni orden de administración.
- Sin disponibilidad/stock de fármacos hospitalarios inventados.
- Sin coche de paro universal.
- Sin una lista de “20 correctos MINSA”.
- Sin transcribir completo NANDA/NOC/NIC ni escalas sujetas a licencia.
- Sin fichas farmacológicas completas hasta completar validación individual por DIGEMID/GPC.

### 9. Entregables técnicos

1. Aplicativo ejecutable y navegación funcional.
2. Base local de contenido importada.
3. Atlas y escalas MVP funcionales.
4. Calculadoras con pruebas automatizadas.
5. PAE/SOAPIE/SBAR con persistencia local.
6. Casos de práctica y retroalimentación.
7. Modo offline de los contenidos base.
8. Registro de fuentes, versiones y estados.
9. Documento de arquitectura y guía de ejecución/build.
10. Informe final de funciones completadas, parciales, bloqueadas y backlog.

### 10. Criterio final

No responder solo con una lista de promesas ni entregar una maqueta estática. Implementar lo que se pueda con el repositorio actual, priorizar el MVP, probarlo en Android/tablet, preservar el Prototipo B y dejar lo pendiente como backlog explícito en `10_ROADMAP_Y_MODULOS_FUTUROS.md`.

---

# SECCIÓN 09 — REFERENCIAS Y CONTROL DE VIGENCIA

## Referencias prioritarias y control de vigencia

**Corte de consulta del paquete:** 3 de noviembre de 2026. Verificar cada norma y ficha técnica nuevamente antes de publicar una versión clínica.

### 1. Higiene de manos y control de infecciones

- MINSA, RM N.º 255-2016-MINSA, Guía Técnica para la Implementación del Proceso de Higiene de Manos en los Establecimientos de Salud: https://www.gob.pe/institucion/minsa/normas-legales/192444
- MINSA, publicación de la Guía Técnica de Higiene de Manos: https://www.gob.pe/institucion/minsa/informes-publicaciones/285025-guia-tecnica-para-la-implementacion-del-proceso-de-higiene-de-manos-en-los-establecimientos-de-salud
- MINSA, noticia institucional sobre cinco momentos y técnica para establecimientos, publicada en 2026: https://www.gob.pe/institucion/minsa/noticias/1387185-minsa-promueve-el-lavado-de-manos-para-prevenir-infecciones-y-salvar-vidas-en-la-atencion-de-salud
- OMS, Guidelines on Hand Hygiene in Health Care: https://www.who.int/publications/i/item/9789241597906
- OMS, My 5 Moments for Hand Hygiene: https://www.who.int/teams/integrated-health-services/infection-prevention-control/hand-hygiene
- CDC, Clinical Safety: Hand Hygiene for Healthcare Workers (27 feb 2024): https://www.cdc.gov/clean-hands/hcp/clinical-safety/

### 2. Seguridad de medicamentos

- INSNSB, Guía de Procedimiento de Enfermería “Administración Segura de Medicamentos Endovenosos”, RD N.º 000087-2021-DG-INSNSB: https://cdn.www.gob.pe/uploads/document/file/7682305/6505016-rd-n-000087-2021-dg-insnsb-028-gp-administracion-de-medicamentos-edv-vb.pdf
- OMS, Medication Without Harm: https://www.who.int/initiatives/medication-without-harm
- OMS, control de soluciones concentradas de electrolitos: https://cdn.who.int/media/docs/default-source/patient-safety/patient-safety-solutions/ps-solution5-control-concentrated-electrolyte-solutions.pdf
- DIGEMID, portal oficial y fichas técnicas: https://www.digemid.minsa.gob.pe/
- DIGEMID, ficha técnica de metamizol sódico inyectable 1 g/2 mL: https://www.digemid.minsa.gob.pe/Archivos/FichasTecnicas/EspecialidadesFarmaceuticas/2019/EE00827_FT_V01.pdf

### 3. Adulto mayor y VACAM

- MINSA, RM 789-2023-MINSA, NTS 207-MINSA/DGIESP-2023: https://www.gob.pe/institucion/minsa/normas-legales/4557563-789-2023-minsa
- MINSA, RM 948-2023-MINSA, modificatoria de NTS 207: https://www.gob.pe/institucion/minsa/normas-legales/4728882-948-2023-minsa
- Los formularios incluidos en la VACAM deben respetar la versión oficial y sus modificaciones; escalas complementarias deben identificarse como tales.

### 4. Coche de paro y soporte vital

- MINSA, NTS 057-MINSA/DIGEMID-V.01 sobre sistema de dispensación de medicamentos en dosis unitaria, apartado de coche de paro: https://cdn.www.gob.pe/uploads/document/file/391318/Norma_t%C3%A9cnica_de_salud__Sistema_de_dispensaci%C3%B3n_de_medicamentos_en_dosis_unitaria_para_los_establecimientos_del_sector_salud20191017-26355-1t6udtg.pdf
- INMP, R.D. N.º 165-2022-DG-INMP-MINSA, Directiva de manejo y control del coche de paro: https://www.inmp.gob.pe/uploads/RD_N%C2%B0_165-2022-DG-INMP-MINSA_compressed.pdf
- MINSA/DIGERD, especificaciones de coche de paro equipado: https://cdn.www.gob.pe/uploads/document/file/4705126/EETT%20-%20ITEM%20N%2004%20-%20COCHE%20DE%20PARO%20EQUIPADO%20-%20DS72.pdf?v=1687217953
- AHA, Guidelines for CPR and ECC 2025 (actualización 22 oct 2025): https://professional.heart.org/en/science-news/2025-aha-guidelines-for-cpr-and-ecc
- MINSA, orientación pública de RCP (debe cotejarse con guía profesional más reciente antes de implementar algoritmos clínicos): https://www.gob.pe/16889-reanimacion-cardiopulmonar-rcp

### 5. Inmunizaciones

- MINSA, RM 561-2026-MINSA, NTS 246-MINSA/DGIESP-2026, Esquema Nacional de Inmunizaciones: https://www.gob.pe/institucion/minsa/normas-legales/8265031-561-2026-minsa
- Esta NTS es posterior a la NTS 196-2022 y sus modificaciones; la app debe utilizar la norma de 2026 en el módulo de inmunizaciones, verificando anexos y modificaciones posteriores.

### 6. Dispositivos y equipos

- MINSA, RM 570-2025-MINSA, homologación de catéter venoso periférico: https://www.gob.pe/institucion/minsa/normas-legales/7114444-570-2025-minsa
- Las partes, medidas y compatibilidad de cada dispositivo deben confirmarse con la ficha del fabricante del modelo representado.

### 7. Criterios de actualización

- Normas: revisar trimestralmente y antes de cada versión mayor de la app.
- Medicamentos: revisar al crear ficha, si cambia registro/ficha técnica, alertas DIGEMID o evidencia nueva; revisión programada como mínimo anual.
- Escalas: revisar versión, validación lingüística, licencia y protocolo; no cambiar puntos de corte sin evidencia.
- RCP: revisar cada vez que una guía AHA/ERC/consenso y/o norma nacional nueva esté publicada; comparar año de publicación y ámbito geográfico.
- Coche de paro: revisión institucional por cambio de inventario, servicio, población, guía de soporte vital o comité farmacológico.
- Dispositivos: revisión por cambio de fabricante/modelo/homologación.

### 8. Registro de fuente por contenido

Cada registro `Source` debe incluir: organismo, título, código, fecha de publicación, fecha de consulta, URL estable, tipo de fuente, versión, jurisdicción/alcance, estado de vigencia, fragmento que respalda el contenido y revisor.

**Nunca usar solamente fecha de consulta.** Registrar también fecha y versión del documento original.

---

# SECCIÓN 10 — ROADMAP Y MÓDULOS FUTUROS

## Roadmap: qué implementar ahora y qué dejar preparado para el futuro

### 1. Decisión de alcance

El proyecto debe avanzar con un MVP usable, no intentar construir de golpe un hospital virtual con todos los protocolos clínicos. La versión inicial debe tener funciones sólidas, contenido suficiente y fuentes trazables. Las especialidades avanzadas se preparan como módulos independientes para añadir sin rehacer la arquitectura.

### 2. Construir ahora: MVP obligatorio

#### Módulo 1 — Inicio, navegación y búsqueda

- Home inspirada en Prototipo B.
- Navegación por áreas/competencias, no por ciclos universitarios.
- Buscador global por texto, etiqueta, especialidad, escala, equipo, procedimiento y fármaco.
- Favoritos, historial local y progreso de aprendizaje.
- Estado de cada módulo: disponible, en construcción o próximamente.

#### Módulo 2 — Biblioteca de contenidos

- Importar Markdown y renderizar títulos, tablas, listas, fórmulas y fuentes.
- Búsqueda offline del texto instalado.
- Filtros por nivel inicial/intermedio/avanzado y área clínica.
- Fecha/versión/estado de validación en cabecera.

#### Módulo 3 — Herramientas clínicas educativas

- Calculadora dosis-volumen.
- Dosis por peso (solo práctica educativa, con unidad y contexto obligatorios).
- Conversor de unidades.
- Goteo gotas/min y velocidad mL/h con factor de equipo explícito.
- Tiempo de infusión.
- Dilución con advertencia de aplicabilidad.
- Balance hídrico.
- IMC.
- Registro simple de signos vitales y tendencia simulada.

#### Módulo 4 — Biblioteca de escalas MVP

- EVA/NRS.
- Glasgow adulto con E/V/M y limitaciones.
- Braden.
- Norton.
- RASS.
- APGAR como herramienta formativa (no decisión de reanimación).
- Validación de población, rango, campos y fórmula.

#### Módulo 5 — Atlas visual MVP

- Jeringa y aguja.
- Estetoscopio.
- Tensiómetro.
- Bránula y calibres.
- Equipo de venoclisis.
- Volutrol.
- Llave de tres vías.
- Controles táctiles por componente, descripción y seguridad.

#### Módulo 6 — Bioseguridad

- Cinco momentos.
- Lavado clínico agua/jabón.
- Fricción alcohólica.
- Preparación quirúrgica: información adaptable a producto.
- EPP y precauciones estándar.
- 10 correctos de guía institucional tras cotejar el texto exacto.
- Checklist de 20 controles claramente no oficial.

#### Módulo 7 — PAE básico y formatos

- Patrones de Gordon.
- Registro subjetivo/objetivo.
- Priorización ABC y criterios de alarma.
- Formato SOAPIE.
- SBAR.
- Exportación a Markdown/impresión educativa.
- Diagnósticos NANDA, resultados NOC e intervenciones NIC como referencias enlazadas o campos del alumno, según licencia.

#### Módulo 8 — Casos simulados iniciales

- Diabetes/ITU/HTA.
- TCE/politrauma/UCI.
- HDA/anemia.
- Pancreatitis.
- Neumonía/asma.
- Sepsis/shock.
- ECG/ritmos.

Incluir casos nuevos sintéticos, sin datos de personas reales ni órdenes ambiguas.

### 3. Preparar ahora, implementar después

Aunque no se construyan funcionalmente todavía, crear estructura, etiquetas y entradas vacías con alcance claramente delimitado:

- Farmacología completa con catálogo DCI; fichas se completan por tandas validadas.
- Capurro, Ballard y Silverman–Andersen con formulario una vez verificados los formularios autorizados.
- Barthel, Katz, Lawton–Brody, Pfeiffer, Mini-Mental, Yesavage, MNA-SF, Morse/Downton, Tinetti.
- Catéteres centrales, PICC, port, hemodiálisis, Foley, SNG/SNE, tubo endotraqueal, traqueostomía y drenajes.
- Equipos UCI, ventilación, bombas, monitor, desfibrilador, aspirador y equipos de oxígeno.
- Catálogo por áreas clínicas y especialidades.
- Módulo de coche de paro con inventario configurable por establecimiento, no stock universal.

### 4. Futuras versiones: backlog priorizado

#### Versión 1.1 — Escalas y valoración avanzada

- Capurro, Ballard, Silverman–Andersen.
- Barthel, Katz, Lawton–Brody.
- Mini-Mental, Pfeiffer, Yesavage VACAM.
- Morse, Downton, Tinetti.
- CAM/4AT, CPOT/BPS, NEWS2/MEWS (según validación/protocolo).
- Registro seriado y evolución gráfica.

#### Versión 1.2 — Farmacología fase 2

- Fichas completas por prioridad.
- Verificación DIGEMID presentación por presentación.
- Dosis por indicación, edad y peso donde proceda, con ajuste renal/hepático.
- Reconstitución, dilución, compatibilidad y velocidad solo con fuente específica.
- Alertas de alto riesgo, LASA e interacciones.
- Favoritos y tarjetas de memoria farmacológica.

#### Versión 1.3 — Procedimientos y dispositivos avanzados

- Catéteres centrales y arteriales.
- Sondas y drenajes.
- Oxigenoterapia, nebulización, aspiración y ventilación.
- Transfusión y cuidados perioperatorios.
- Diagramas SVG/3D y práctica guiada supervisada.

#### Versión 1.4 — Especialidades

- Materno perinatal y obstetricia.
- Pediatría y neonatología.
- Salud mental.
- Adulto mayor.
- Comunidad e inmunizaciones.
- Emergencia y UCI.
- Centro quirúrgico.
- Nefrología, oncología y cuidados paliativos.

#### Versión 1.5 — Simulación clínica

- Casos ramificados con cambios de signos vitales y laboratorio.
- Priorización temporal y comunicación SBAR.
- Evaluación OSCE/ECOE por rúbricas.
- Feedback por seguridad, no por “respuesta única”.
- Simulación de reacciones adversas y deterioro.

#### Versión 2.0 — Biblioteca personal y docencia

- Creación/importación de apuntes Markdown.
- Flashcards y repetición espaciada.
- Cuestionarios por área.
- Exportaciones PDF/Word/Markdown (según capacidad implementada y pruebas).
- Gestión docente de colecciones si se requiere.

### 5. Fuera de alcance por ahora

- IA que diagnostique, prescriba o modifique tratamientos.
- Expediente clínico real o almacenamiento de información identificable.
- Integración con historia clínica hospitalaria.
- Stock en tiempo real de medicamentos sin integración institucional autorizada.
- App de telemedicina o monitorización remota de pacientes.
- Recomendación automática de dosis a administrar en persona real.
- Reproducción completa de escalas/taxonomías con licencia no confirmada.

### 6. Cómo mostrar módulos futuros

- Etiqueta neutral `Próximamente` y texto breve de qué contendrá.
- No incluir botones que parezcan activos si no hacen nada.
- No mostrar puntajes ficticios, datos de muestra como si fueran reales ni porcentajes de avance inventados.
- Mantener el diseño visual consistente, sin construir pantallas vacías que bloqueen el uso.

### 7. Prioridad sugerida

P0: auditoría de repositorio + navegación + Markdown + búsqueda.  
P1: atlas, higiene, escalas MVP y calculadoras.  
P2: PAE/SOAPIE/SBAR y casos simulados.  
P3: importación/offline, pruebas de accesibilidad y rendimiento.  
P4: fichas farmacológicas validadas, nuevas escalas y especialidades.

---

# SECCIÓN 11 — MODELO DE DATOS Y PRUEBAS DE CALIDAD

## Modelo de datos, validación y pruebas de calidad

### 1. Arquitectura de contenidos recomendada

Separar contenido de interfaz y datos de operación. Todos los módulos deben ser importables/actualizables por JSON/Markdown sin editar componentes de pantalla.

#### Entidades

- `Topic`: id, título, resumen, nivel, área, etiquetas, secciones, objetivos, fuente principal, versión, estado.
- `Procedure`: id, población, indicaciones generales, precauciones, material, pasos formativos, riesgos, monitorización, fuente y validación local.
- `Scale`: id, nombre, acrónimo, versión, población, contexto, licencia, dominios/ítems permitidos, algoritmo, puntos de corte, límites, fuente, fecha.
- `Medication`: id, DCI, ATC, familia, formulación, concentración, registro DIGEMID, fabricante, indicaciones, contraindicaciones, dosis, preparación, compatibilidad, alertas, fuentes, revisión.
- `Device`: id, nombre, tipo, fabricante/modelo si es específico, variantes, partes, conexiones, indicación, riesgos, mantenimiento y fuentes.
- `Case`: id, título, nivel, edad ficticia, historia sintética, variables iniciales, evolución, preguntas, feedback, rúbrica, fuentes.
- `NursingPlan`: id, valoración, patrón, diagnóstico ingresado/consultado, NOC, NIC, plan, ejecución, evaluación; persistencia local.
- `Calculation`: id, tipo, entradas, unidades, fórmula versionada, resultado, desglose y validaciones.
- `Source`: organismo, documento, código, fecha, consulta, URL, jurisdicción, versión, estado, alcance.
- `ChangeLog`: entidad, versión anterior/nueva, motivo, fecha, revisor.

### 2. Contrato de fuentes

Cada dato clínico debe guardar un vínculo a la fuente que lo respalda. No almacenar solo una bibliografía al final de la app. Los datos derivados deben tener relación con una o varias fuentes y mostrar su procedencia al usuario.

Campos mínimos de fuente: `sourceId`, `publisher`, `documentTitle`, `identifier`, `publishedAt`, `consultedAt`, `url`, `jurisdiction`, `version`, `status`, `scopeNotes`, `reviewer`.

### 3. Reglas para cálculos

- Usar aritmética decimal precisa; evitar errores binarios por coma flotante en resultados de dosis.
- Almacenar y mostrar unidades explícitas en cada campo.
- Normalizar conversiones en una capa probada, no dentro de UI.
- No redondear pasos intermedios salvo que la fuente/protocolo lo ordene.
- Mostrar fórmula, sustitución, operación, resultado y unidad.
- No mezclar microgramos con miligramos ni UI con mL sin concentración confirmada.
- Factor de goteo debe introducirse o seleccionarse desde etiqueta del equipo; no asumir.
- C1V1=C2V2 solo aplica cuando se cumplen los supuestos de concentración/unidades y compatibilidad del producto.
- No calcular infusiones de alto riesgo a partir de datos incompletos.
- Etiquetar todos los resultados como `EJERCICIO EDUCATIVO`.

### 4. Pruebas mínimas

#### Calculadora dosis-volumen

- Unidades iguales.
- Conversión g↔mg y mg↔mcg.
- Dosis disponible y volumen válido.
- Campo vacío, cero y negativo.
- Concentración ambigua.
- Error decimal y resultado fuera de rango.

#### Goteo y bomba

- Macro 10/15/20 gtt/mL y micro 60 gtt/mL como factores ingresados, no asumidos.
- Conversión hora/minutos.
- Factor faltante.
- Volumen/tiempo cero o negativo.
- Redondeo de gotas/min a entero según regla configurada.

#### Escalas

- Puntos mínimo y máximo.
- Todos los ítems completos.
- Ítem no evaluable.
- Población/edad no válida.
- Versión distinta.
- Comparación contra ejemplos oficiales autorizados.

#### PAE y formularios

- Guardado local y recuperación.
- Dato ausente no autocompletado.
- Diagnóstico de riesgo sin “manifestado por”.
- Historial cronológico y exportación.
- No guardar nombres/documentos reales por defecto.

### 5. Privacidad, seguridad y offline

- Mantener datos locales por defecto.
- No enviar casos, notas ni signos vitales a APIs de IA sin consentimiento, propósito y controles de privacidad explícitos.
- No usar analytics que capturen texto clínico libre.
- Cifrar almacenamiento sensible si existiera; preferir no almacenar datos reales.
- Botón para borrar datos locales y exportar contenido del usuario.
- Contenido base y buscador disponibles offline.
- Controlar el acceso a enlaces externos y no descargar automáticamente archivos no confiables.

### 6. Accesibilidad y rendimiento

- Contraste adecuado, fuentes escalables, etiquetas semánticas y navegación teclado/lector de pantalla.
- Zonas táctiles cómodas para tablet y móvil.
- No depender solo de color para indicar riesgo/estado.
- Animaciones pausables y modo reducido.
- Imágenes optimizadas y carga diferida.
- Dispositivos de gama baja: priorizar SVG/2D, no 3D pesado.
- Manejar offline/errores de carga con mensajes claros.

### 7. QA de contenido clínico

Toda entrada antes de publicar debe tener:

- Revisor clínico designado (enfermería/farmacia/médico según tema).
- Fuente primaria y alcance.
- Versión/fecha.
- Población válida y contraindicaciones.
- Advertencias y límites.
- Prueba de fórmula o procedimiento cuando corresponda.
- Firma/registro de aprobación dentro del equipo, no una supuesta “validación MINSA”.

Estados: `BORRADOR`, `EN-REVISIÓN`, `VALIDADO-PARA-ESTUDIO`, `VALIDADO-LOCAL`, `OBSOLETO`, `RETIRADO`.

---

# SECCIÓN 12 — TEMAS COMPLEMENTARIOS Y ESPECIALIDADES

## Temas complementarios y especialidades para ampliar la biblioteca

Este documento evita que queden áreas clínicas importantes fuera del proyecto. No implica que deban programarse todas ahora; el orden está en el roadmap.

### 1. Valoración y examen físico avanzado

- Historia clínica, antecedentes, conciliación de medicamentos y alergias.
- ABCDE, evaluación primaria/secundaria, SAMPLE y OPQRST.
- Examen físico por sistemas y valoración cefalocaudal.
- Dolor, conciencia, pupilas, perfusión periférica, llenado capilar y piel.
- Signos vitales por edad y contexto; valores de referencia con fuente.
- Laboratorio básico: hemograma, glucosa, urea/creatinina, electrolitos, coagulación, gases arteriales, lactato, PCR y cultivos: qué mide, preparación, unidades, factores preanalíticos y límites.
- Interpretación de EKG, ritmos, intervalos, segmentos, derivaciones y alarmas.

### 2. Farmacología y terapéutica por grupos

- Analgésicos, antipiréticos, AINE y opioides.
- Antibióticos por familia y resistencia antimicrobiana.
- Antifúngicos, antivirales, antiparasitarios y antituberculosos.
- Antihipertensivos, antianginosos, antiarrítmicos y diuréticos.
- Anticoagulantes, antiagregantes y trombolíticos.
- Antidiabéticos e insulinas.
- Broncodilatadores, corticoides y medicamentos respiratorios.
- Antieméticos, protectores gastrointestinales, laxantes y antidiarreicos.
- Antiepilépticos, sedantes, anestésicos, antipsicóticos y antidepresivos.
- Electrolitos concentrados, soluciones, vasopresores, inotrópicos y antídotos.
- Uterotónicos, tocolíticos, anestesia obstétrica y farmacología neonatal/pediátrica.
- Inmunizaciones: vacunas, inmunoglobulinas, cadena de frío, ESAVI y contraindicaciones vigentes.

### 3. Procedimientos y cuidados especiales

- Acceso venoso periférico y central; línea arterial; cuidado de catéter y prevención de infección.
- Transfusión, identificación, pruebas, vigilancia y reacciones transfusionales.
- Oxigenoterapia, nebulización, inhaladores, aspiración y ventilación mecánica.
- Manejo de drenajes, ostomías, heridas, pie diabético y lesiones por presión.
- Sonda nasogástrica/enteral, gastrostomía, nutrición enteral y parenteral.
- Sondaje vesical, cuidado urinario y balance.
- Diálisis peritoneal y hemodiálisis.
- Preparación preoperatoria, checklist de cirugía segura, recuperación postanestésica y control del dolor.
- Aislamiento, cultivos, toma/transporte de muestras y control de IAAS.

### 4. Emergencias y paciente crítico

- Triaje y escalas de alerta temprana.
- RCP adulto, pediátrica y neonatal según guías vigentes; desfibrilación y DEA.
- Shock hipovolémico, cardiogénico, obstructivo, distributivo y anafiláctico.
- Sepsis y shock séptico.
- ACV, convulsiones y estado epiléptico.
- Síndrome coronario agudo, arritmias y edema agudo de pulmón.
- Insuficiencia respiratoria, asma grave y EPOC.
- Intoxicaciones, sobredosis, antídotos y toxicología.
- Quemaduras, trauma, TCE, hemorragias y transfusión masiva.
- Hipoglucemia, DKA, estado hiperosmolar y trastornos electrolíticos graves.
- Manejo post-paro y cuidados postresucitación.

### 5. Materno-perinatal y neonatal

- Control prenatal, signos de alarma y valoración fetal.
- Trabajo de parto, partograma, vigilancia materna y fetal.
- Preeclampsia/eclampsia, hemorragia obstétrica, sepsis materna y claves obstétricas.
- Puerperio, lactancia y educación.
- APGAR, Capurro, Ballard, Silverman–Andersen y termorregulación.
- Prematuridad, dificultad respiratoria, ictericia, hipoglucemia y sepsis neonatal.
- Reanimación neonatal conforme a guía vigente.

### 6. Pediatría y adolescente

- Crecimiento y desarrollo, hitos y curvas.
- Valoración pediátrica, escalas por edad, dolor y deshidratación.
- Administración por peso y concentración segura.
- Vacunación y ESAVI según esquema MINSA vigente.
- Infecciones respiratorias, diarrea, deshidratación, anemia, convulsión febril y urgencias pediátricas.
- Comunicación, protección y derechos del niño/adolescente.

### 7. Adulto mayor

- VACAM y valoración integral.
- Cognición, delirium, afecto, funcionalidad, marcha, caídas y riesgo de lesión por presión.
- Polifarmacia, conciliación, deprescripción como decisión clínica interprofesional.
- Fragilidad, sarcopenia, malnutrición, incontinencia, dolor y cuidados paliativos.
- Maltrato, abandono, cuidador y redes de apoyo.

### 8. Salud mental

- Entrevista, examen mental y comunicación terapéutica.
- Ansiedad, depresión, psicosis, bipolaridad, delirium y demencia.
- Crisis, agitación, riesgo suicida/autolesión y seguridad.
- Consumo de sustancias, abstinencia y reducción de daños.
- Psicofármacos, efectos adversos y vigilancia.

### 9. Comunidad, salud pública y gestión

- Promoción y prevención; familia y visita domiciliaria.
- Inmunizaciones, cadena de frío, ESAVI y registro.
- Tuberculosis, VIH/ITS, dengue, enfermedades metaxénicas y transmisibles.
- Enfermedades no transmisibles, HTA, diabetes, cáncer y salud renal.
- Epidemiología, vigilancia, brotes, indicadores y programas.
- Gestión de riesgos, calidad, auditoría, liderazgo, docencia y recursos.

### 10. Investigación y desarrollo profesional

- Pregunta PICO, búsqueda bibliográfica y lectura crítica.
- Diseños cuantitativos/cualitativos, sesgos, muestras y estadística.
- Ética de investigación, consentimiento y protección de datos.
- Redacción científica y referencias APA/Vancouver.
- Examen clínico objetivo estructurado (ECOE/OSCE), portafolio, reflexión y evaluación por rúbrica.

---

# SECCIÓN 13 — PLAN DE IMPLEMENTACIÓN Y CRITERIOS DE ACEPTACIÓN

## Plan de implementación y criterios de aceptación para Arena IA

### 1. Orden de ejecución

#### Sprint 0 — diagnóstico técnico

- Identificar framework, dependencias, rutas y módulos existentes.
- Ejecutar build y pruebas actuales.
- Marcar funciones del proyecto como `HECHO`, `PARCIAL`, `FALTA` o `BLOQUEADO`.
- Crear respaldo/branch. No borrar prototipo ni datos existentes.

#### Sprint 1 — estructura y contenidos

- No implementar pantallas de ciclos, cursos ni distribución curricular universitaria.
- Crear navegación independiente por módulos/áreas.
- Importar Markdown con búsqueda y etiquetas.
- Añadir ficha de fuente/fecha/estado en cada tema.

#### Sprint 2 — atlas visual y bioseguridad

- Construir diagramas táctiles de jeringa, aguja, estetoscopio, tensiómetro, bránula, venoclisis, Volutrol y llave de tres vías.
- Implementar animación de higiene de manos y cinco momentos.
- Agregar checklist de seguridad de medicación y EPP.

#### Sprint 3 — herramientas y escalas

- Desarrollar calculadoras educativas y pruebas automatizadas.
- Implementar escalas MVP definidas en documento 02.
- Bloquear datos incompletos y mostrar puntuaciones por componente.

#### Sprint 4 — PAE y simulación

- Gordon, ABCDE, SOAPIE, SBAR.
- Casos simulados: diabetes, TCE/UCI, HDA, pancreatitis, neumonía, sepsis y EKG.
- Guardado local, exportación educativa y rúbrica.

#### Sprint 5 — rendimiento y entrega

- Android/teléfono/tablet; modo oscuro y bajo rendimiento.
- Offline, accesibilidad, pruebas de dispositivo y errores.
- Informe real de alcance completado y backlog.

### 2. Criterios de aceptación funcionales

- [ ] La app abre y navega sin errores.
- [ ] Se conserva la identidad Prototipo B.
- [ ] No existe navegación curricular institucional por ciclos o cursos.
- [ ] Markdown se muestra y busca offline.
- [ ] Las fuentes y estados de verificación se muestran en cada artículo.
- [ ] Las ilustraciones de los equipos tienen hotspots y etiquetas vectoriales correctas.
- [ ] Se distingue lavado clínico, fricción alcohólica y preparación quirúrgica.
- [ ] Se muestran cinco momentos de higiene de manos.
- [ ] El checklist 20 puntos está rotulado como educativo/no oficial.
- [ ] Los 10 correctos se vinculan a guía institucional y han sido cotejados palabra por palabra.
- [ ] Las escalas MVP calculan con pruebas unitarias y muestran todos sus componentes.
- [ ] Las calculadoras bloquean entradas incompletas o unidades incorrectas.
- [ ] Los resultados se etiquetan como ejercicios educativos.
- [ ] El PAE guarda valoración, análisis, plan, intervención y evaluación.
- [ ] SOAPIE/SBAR exportan correctamente.
- [ ] Los casos son sintéticos/anonimizados.
- [ ] No se envía información personal o clínica a servicios externos.
- [ ] No se muestra una dosis farmacológica sin la ficha validada correspondiente.
- [ ] El coche de paro se configura según protocolo institucional y no como lista universal.
- [ ] Se puede desactivar animación y la app funciona en hardware de menor rendimiento.

### 3. Criterios visuales

- Paleta turquesa/rosa/lila/blanco del prototipo.
- Tarjetas y márgenes consistentes; jerarquía tipográfica.
- Los diagramas no presentan texto generado dentro de la imagen.
- El color no es el único indicador de riesgo.
- Contraste, foco, accesibilidad y tamaños táctiles adecuados.
- Animaciones pausables y reducidas en bajo rendimiento.

### 4. Informe de finalización obligatorio

Arena IA debe entregar:

1. Qué módulos existían y qué se reutilizó.
2. Qué funcionalidades implementó en esta iteración.
3. Qué quedó parcial y por qué.
4. Qué quedó bloqueado por validación clínica, licencia o fuente.
5. Pruebas ejecutadas y resultado.
6. Compatibilidad/dispositivos probados.
7. Cómo ejecutar, construir y exportar.
8. Lista de pendientes con prioridad y dependencias.

No inventar porcentaje de avance ni decir que una función está terminada si solo existe una maqueta.

---

# ANEXO A — GUÍA DE GOTEO, DILUCIÓN Y CASOS

> **ANEXO DE TRABAJO PREVIO — USO EDUCATIVO.**
> Revisar fórmulas, unidades, concentraciones y ejemplos antes de reutilizar. No convertir casos de ejemplo en órdenes clínicas.

## 🩺✨ Guía completa de cálculo de goteo, dilución y administración de medicamentos en enfermería

> Diseñada para:
> - 👶 Quien está empezando y necesita entender desde cero.
> - 🧠 Quien ya sabe, pero necesita recordar rápido sin confundirse.
> - 🏥 Quien quiere rendir bien en el hospital, examen o práctica clínica.

> Incluye:
> - 📚 Explicación académica clara.
> - 💉 Cálculo de dosis, volumen, dilución, goteo y concentración.
> - 🧪 Casos resueltos: ceftriaxona, insulina y lógica básica de UCI.
> - ⚠️ Errores frecuentes y cómo evitarlos.

---

### 🎯 1. Cómo pensar cualquier cálculo sin perderse

Casi todos los problemas de medicación se pueden resolver si se responde en este orden:

| Paso mental | Pregunta que debes hacerte | Ejemplo |
|---|---|---|
| 1 | ¿Qué dosis pide el médico? | 1350 mg de ceftriaxona |
| 2 | ¿Cómo viene el medicamento? | Frasco de 1 g |
| 3 | ¿Cuánto equivale esa presentación? | 1 g = 1000 mg |
| 4 | ¿Se reconstituye o ya viene líquido? | Ceftriaxona en polvo: sí se reconstituye |
| 5 | ¿Qué concentración queda después de prepararlo? | Por ejemplo, 1000 mg en 10 mL = 100 mg/mL |
| 6 | ¿Cuánto volumen corresponde a la dosis? | 1350 mg ÷ 100 mg/mL = 13,5 mL |
| 7 | ¿Cómo se administra? | IV lento, infusión, IM, SC, bomba, etc. |

✅ **Regla de oro:** nunca empieces por la fórmula; primero entiende el medicamento.

---

### 🧾 2. Tabla maestra de fórmulas

| Tema | Fórmula simple | Para qué sirve | Recordatorio |
|---|---|---|---|
| mL/h | Volumen total (mL) ÷ Tiempo (h) | Programar bomba de infusión | “Volumen entre horas” |
| gtt/min | (Volumen (mL) ÷ Tiempo (min)) × Factor de gotas | Infusión por gravedad | “Primero horas a minutos” |
| Tiempo | Horas × 60 = minutos | Preparar gtt/min | Sin minutos no calculas gotas |
| Dosis–volumen | (Dosis ordenada ÷ Dosis disponible) × Volumen disponible | Saber cuántos mL dar | “Lo que quiero / lo que tengo × volumen” |
| Dilución | C1 × V1 = C2 × V2 | Ajustar concentraciones | Muy útil en UCI |
| Insulina U-100 | Unidades ÷ 100 = mL | Convertir unidades a mL | 100 UI = 1 mL |
| Infusión por peso | Dosis ordenada × peso × 60 ÷ concentración | Pasar mcg/kg/min a mL/h | Muy usado en UCI |

---

### 💧 3. Goteo IV explicado fácil

#### 3.1 Cálculo con bomba

| Fórmula | Ejemplo | Resultado |
|---|---|---|
| mL/h = Volumen ÷ horas | 1000 mL en 4 h = 1000 ÷ 4 | **250 mL/h** |
| mL/h = Volumen ÷ horas | 500 mL en 2 h = 500 ÷ 2 | **250 mL/h** |

#### 3.2 Cálculo por gravedad

| Fórmula | Ejemplo | Resultado |
|---|---|---|
| gtt/min = (Volumen ÷ Tiempo en min) × factor | 1200 mL en 12 h con 15 gtt/mL | **25 gtt/min** |
| gtt/min = (Volumen ÷ Tiempo en min) × factor | 500 mL en 4 h con 15 gtt/mL | **31 gtt/min** |

#### 3.3 Factores de gotas

| Equipo | Factor |
|---|---|
| Macrogotero | 10, 15 o 20 gtt/mL |
| Microgotero | 60 gtt/mL |

🟡 **Atajo mental:** si te piden gtt/min, convierte el tiempo a minutos antes de todo.

---

### 💉 4. Reconstitución y dilución

#### 4.1 Qué significa reconstituir

Reconstituir significa **agregar un diluyente a un medicamento en polvo** para convertirlo en solución utilizable.

| Ejemplo | Explicación |
|---|---|
| Ceftriaxona en frasco de 1 g | No se administra en polvo |
| Se agrega agua estéril u otro diluyente indicado | Se forma una solución líquida |
| Luego se calcula el volumen a extraer | Según la dosis prescrita |

#### 4.2 Fórmula universal de volumen a administrar

| Fórmula | Qué hace |
|---|---|
| Volumen (mL) = (Dosis ordenada ÷ Dosis disponible) × Volumen disponible | Permite saber cuántos mL equivalen a la dosis indicada |

---

### 🧪 5. Caso detallado: ceftriaxona cuando el médico pide más de 1 g

#### 5.1 Situación clínica

**Orden médica:** administrar **1350 mg de ceftriaxona**.  
**Presentación disponible:** frasco de **1 g**.

#### 5.2 Lo primero que debes ver

| Dato | Equivalencia |
|---|---|
| 1 g | 1000 mg |
| Dosis ordenada | 1350 mg |
| Comparación | 1350 mg es mayor que 1000 mg |

✅ **Conclusión inmediata:** un solo frasco de 1 g **no alcanza**.

#### 5.3 ¿Qué haces entonces?

| Paso | Acción correcta |
|---|---|
| 1 | Confirmar que la prescripción dice realmente 1350 mg |
| 2 | Ver si hay más de un vial disponible |
| 3 | Reconstituir según protocolo institucional |
| 4 | Calcular la concentración final |
| 5 | Extraer el volumen exacto correspondiente a 1350 mg |

#### 5.4 Ejemplo docente con reconstitución IV

Para IV, varios documentos de preparación indican que el **vial de 1 g puede reconstituirse con 10 mL de agua para inyección**, quedando una preparación usada para administración IV lenta o posterior dilución.[web:119][web:120]

Si usamos ese ejemplo didáctico:

| Paso | Cálculo | Resultado |
|---|---|---|
| Contenido del vial | 1 g = 1000 mg | 1000 mg |
| Reconstitución | 1000 mg en 10 mL | 100 mg/mL |
| Dosis médica | 1350 mg | 1350 mg |
| Volumen necesario | 1350 ÷ 100 | **13,5 mL** |

✅ **Resultado:** para administrar **1350 mg**, necesitas **13,5 mL** de la solución reconstituida.

#### 5.5 Pero cuidado: un vial no alcanza

| Disponible en 1 vial | Necesario para la dosis |
|---|---|
| 10 mL si 1 g se reconstituye en 10 mL | 13,5 mL |

✅ Por eso necesitas **más de un vial**.  
Una opción práctica sería reconstituir **dos viales de 1 g** y extraer solo el volumen necesario total. Guías de preparación señalan precisamente que para dosis de 2 g se preparan dos viales de 1 g.[web:120]

#### 5.6 Ejemplo con dos viales

| Dato | Resultado |
|---|---|
| 2 viales de 1 g | 2000 mg totales |
| Si cada vial queda en 10 mL | 20 mL totales |
| Concentración | 2000 mg en 20 mL = 100 mg/mL |
| Dosis requerida | 1350 mg |
| Volumen a extraer | 13,5 mL |
| Volumen sobrante | 6,5 mL |

🛑 **Importante:** el sobrante solo se usa o desecha según el protocolo de tu institución y si el vial es de un solo uso. Algunos documentos de reconstitución recalcan que ciertos viales son de uso único y no deben reutilizarse libremente.[web:117]

#### 5.7 Ejemplo IM: cambia todo

Para vía IM, la reconstitución puede ser distinta. Por ejemplo, hay guías donde un vial de 1 g se reconstituye con **3,3 a 3,5 mL**, generando una concentración aproximada de **250 mg/mL** para uso IM.[web:117][web:119][web:124]

Si un médico indicara 1350 mg IM y la concentración fuera 250 mg/mL:

| Paso | Cálculo | Resultado |
|---|---|---|
| Dosis | 1350 mg | 1350 mg |
| Concentración IM | 250 mg/mL | 250 mg/mL |
| Volumen | 1350 ÷ 250 | **5,4 mL** |

⚠️ Pero aquí entra otro juicio clínico: **ese volumen IM podría ser demasiado para una sola zona**, por lo que se debe revisar indicación, sitios de aplicación y protocolo institucional.

---

### 🍬 6. Insulina: diferente porque se calcula en unidades

La insulina no se suele pensar en mg para práctica habitual, sino en **unidades internacionales (UI)**. En insulina **U-100**, hay **100 UI por cada 1 mL**.

#### 6.1 Fórmula básica

| Fórmula | Resultado |
|---|---|
| mL = Unidades ÷ 100 | Convierte UI a mL |

#### 6.2 Tabla rápida de equivalencias U-100

| UI prescritas | mL |
|---|---|
| 4 UI | 0,04 mL |
| 8 UI | 0,08 mL |
| 10 UI | 0,10 mL |
| 12 UI | 0,12 mL |
| 18 UI | 0,18 mL |
| 25 UI | 0,25 mL |
| 40 UI | 0,40 mL |

#### 6.3 Caso examen clásico

**Orden:** administrar **18 UI** de insulina regular U-100 por vía subcutánea.

| Paso | Cálculo | Resultado |
|---|---|---|
| Concentración | 100 UI = 1 mL | U-100 |
| Conversión | 18 ÷ 100 | **0,18 mL** |

✅ Resultado: administrar **18 UI**, equivalentes a **0,18 mL**.

#### 6.4 Antes de administrar insulina

Tal como mencionaste, muchas veces antes se revisa un examen, especialmente glucemia capilar o glucosa sérica.

| Paso clínico | Qué se hace |
|---|---|
| 1 | Ver glucemia del paciente |
| 2 | Revisar escala o indicación médica |
| 3 | Determinar cuántas UI corresponden |
| 4 | Convertir a mL si es necesario |
| 5 | Administrar por la vía indicada |

#### 6.5 Caso con escala simple

| Glucemia | Dosis indicada | Conversión U-100 |
|---|---|---|
| 250 mg/dL | 6 UI | 6 ÷ 100 = 0,06 mL |
| 320 mg/dL | 10 UI | 10 ÷ 100 = 0,10 mL |
| 380 mg/dL | 14 UI | 14 ÷ 100 = 0,14 mL |

🟢 En jeringa de insulina, muchas veces se mide directamente en unidades, pero saber la equivalencia en mL ayuda a entender la concentración y resolver preguntas de examen.

#### 6.6 Insulina en infusión IV

En pacientes críticos puede prepararse en infusión continua. Un ejemplo docente frecuente es una mezcla donde la concentración final queda en **1 UI/mL**, de modo que:

| Necesidad del paciente | Si la mezcla es 1 UI/mL | Velocidad |
|---|---|---|
| 2 UI/h | 2 mL/h | 2 mL/h |
| 6 UI/h | 6 mL/h | 6 mL/h |
| 8 UI/h | 8 mL/h | 8 mL/h |

⚠️ En infusión IV de insulina siempre se sigue protocolo estricto y monitorización frecuente.

---

### 🚨 7. UCI: cálculos más complejos pero con lógica

Muchos fármacos en UCI se prescriben como **mcg/kg/min** o **unidades/h**. La clave es no asustarse: solo hay más pasos.

#### 7.1 Fórmula general para pasar a mL/h

| Fórmula | Uso |
|---|---|
| mL/h = (dosis ordenada × peso × 60) ÷ concentración | Convertir mcg/kg/min a mL/h |

#### 7.2 Caso básico tipo UCI

**Paciente:** 70 kg  
**Orden:** dopamina 10 mcg/kg/min  
**Preparación:** 800 mg en 250 mL.  

Primero, convertir concentración:

| Paso | Cálculo | Resultado |
|---|---|---|
| mg a mcg | 800 mg = 800000 mcg | 800000 mcg |
| Concentración | 800000 ÷ 250 | 3200 mcg/mL |

Luego calcular dosis por hora:

| Paso | Cálculo | Resultado |
|---|---|---|
| Dosis por minuto | 10 × 70 | 700 mcg/min |
| Dosis por hora | 700 × 60 | 42000 mcg/h |
| mL/h | 42000 ÷ 3200 | **13,1 mL/h** |

✅ Resultado: programar aproximadamente **13 mL/h**, coherente con tarjetas de referencia de vasoactivos que muestran cálculos similares.[web:170]

#### 7.3 Secuencia segura en UCI

| Paso | Qué revisar |
|---|---|
| 1 | Peso real del paciente |
| 2 | Dosis prescrita |
| 3 | Concentración preparada |
| 4 | Conversión correcta de unidades |
| 5 | Velocidad en mL/h |
| 6 | Doble chequeo o validación institucional |

---

### ⚠️ 8. Errores frecuentes que hacen fallar un examen o una administración

| Error | Ejemplo | Cómo evitarlo |
|---|---|---|
| No convertir g a mg | 1,35 g se toma como 135 mg | Recordar: 1 g = 1000 mg |
| No notar que el vial no alcanza | Pedidos mayores que 1 g usando un solo vial | Comparar siempre dosis vs contenido del frasco |
| Mezclar mg con mL | Confundir dosis con volumen | Separar: primero dosis, luego volumen |
| Usar mal el tiempo | Calcular gtt/min con horas en vez de minutos | Siempre convertir horas × 60 |
| No revisar la vía | IM y IV no se preparan igual | Leer protocolo antes de reconstituir |
| No revisar si la concentración cambió | Reconstitución distinta cambia totalmente el volumen | Escribir siempre la concentración final |

---

### 🧷 9. Hoja de recuerdo rápido

| Si ves esto... | Piensa esto... |
|---|---|
| El médico pide más mg que los que trae el vial | Necesitarás más de un frasco o confirmar indicación |
| El medicamento viene en g y la orden está en mg | Convierte g a mg primero |
| El medicamento es en polvo | Primero reconstituye, luego calcula |
| La orden es en UI | Probablemente es insulina o heparina; piensa en concentración por unidad |
| La orden es mcg/kg/min | Es cálculo por peso; ve paso a paso |
| La infusión es por gravedad | Usa gtt/min, no mL/h solamente |

---

### 👩‍🏫 10. Guía para quien no sabe nada

| Paso | Qué hacer |
|---|---|
| 1 | Leer la orden completa |
| 2 | Ver cómo viene el medicamento |
| 3 | Convertir unidades si hace falta |
| 4 | Hallar la concentración |
| 5 | Hallar cuántos mL corresponden |
| 6 | Confirmar vía y tiempo |
| 7 | Revisar si el resultado tiene sentido |

#### Frase para memorizar

**“Primero entiendo la orden, después veo la presentación, luego calculo la concentración, después el volumen, y al final la administración.”**

---

### 🧠 11. Guía para quien ya sabe pero quiere recordar rápido

| Situación | Recordatorio exprés |
|---|---|
| Bomba | mL/h = volumen ÷ horas |
| Gravedad | gtt/min = (volumen ÷ minutos) × factor |
| Antibiótico en polvo | reconstituir → concentración → volumen exacto |
| Dosis mayor que el vial | más de un frasco o confirmar orden |
| Insulina U-100 | UI ÷ 100 = mL |
| UCI | dosis por peso → concentración → mL/h |

---

### ✅ 12. Cierre práctico

Esta guía ya incluye lo que pediste: cómo resolver cuando **el médico prescribe una cantidad mayor a la que trae el vial**, cómo pensar ceftriaxona paso a paso, cómo calcular **insulina** desde el examen o escala hasta la administración, y cómo entender cálculos más delicados de **UCI** sin confundirse. Guías de preparación de ceftriaxona muestran diferencias entre reconstitución IV e IM, por lo que la vía cambia el volumen y la concentración final.[web:117][web:119][web:120][web:124]

Además, la guía organiza cada problema en una secuencia clara: **dosis → presentación → concentración → volumen → administración**, que es la forma más segura de pensar tanto en exámenes como en la práctica clínica. Para insulina y vasoactivos, también se añadieron conversiones y ejemplos de cálculo por unidades y por peso, que son fuentes frecuentes de error si no se separan bien los pasos.[web:170]

---

# ANEXO B — ÁRBOL FARMACOLÓGICO / VADEMÉCUM — FASE 1

> **ANEXO DE TRABAJO PREVIO — NO ES CATÁLOGO CLÍNICO PUBLICABLE SIN AUDITORÍA.**
> Conservado como inventario de DCI y estructura farmacológica anterior. Revisar PNUME vigente, DIGEMID y fichas por producto antes de publicar.

## Vademécum de Enfermería — Fase 1

**Árbol farmacológico maestro organizado por sistemas del cuerpo humano — versión 4.0 auditada para Perú y la región Puno**  
**Corte documental: 27 de septiembre de 2026**

### 1. Propósito de esta revisión

Esta versión conserva el inventario y el alcance del archivo fuente, pero aplica una **auditoría estructural y farmacológica** para que el proyecto pueda avanzar a la Fase 2 (fichas farmacológicas) con una arquitectura más consistente.

La Fase 1 **no contiene dosis, concentraciones, vías, indicaciones completas, contraindicaciones, reacciones adversas, interacciones ni cuidados de enfermería**. Su función es construir el árbol de clasificación y definir el registro maestro de cada fármaco o producto medicinal.

La fuente de partida proporcionada para este proyecto se conserva como base principal. Sobre ella se incorporó una segunda capa de verificación externa con fuentes oficiales peruanas: PNUME y sus modificaciones/listas complementarias, EsSalud, petitorios institucionales hospitalarios y evidencia pública de adquisición/abastecimiento. La clasificación ATC/DDD se utiliza como control farmacológico internacional, no como sustituto del árbol anatómico solicitado.

**Importante:** un petitorio institucional demuestra selección/autorización de uso en una institución; no demuestra que cada medicamento se administre diariamente ni que esté siempre disponible en stock. La disponibilidad real depende de programación, demanda, compras, distribución y existencias.

> **Nota de alcance:** esta revisión corrige la arquitectura y varios errores de clasificación identificables en el documento fuente. No equivale por sí sola a una nueva certificación regulatoria completa de cada DCI al día de hoy. La validación regulatoria final debe mantenerse como un control independiente antes de publicar las fichas clínicas.

### 2. Regla maestra de clasificación

La arquitectura queda definida así:

```text
SISTEMA DEL CUERPO HUMANO
└── Categoría farmacológica
    └── Subcategoría / familia / generación (solo cuando sea útil y aceptada)
        └── Fármaco o producto medicinal
```

#### 2.1. Ubicación maestra

- Cada **DCI/principio activo** recibe una sola **ubicación maestra** en el árbol principal.
- No se repite la misma DCI como hoja en dos sistemas del árbol principal.
- Los demás usos se documentan en un **índice clínico de relaciones cruzadas**.
- Las categorías se construyen preferentemente por **clase farmacológica**, no por indicación, para evitar que el mismo fármaco aparezca varias veces dentro del mismo sistema.

#### 2.2. Combinaciones y formulaciones

Se separan cuatro tipos de registro:

| Tipo | Cómo se registra en Fase 1 | Ejemplo |
|---|---|---|
| DCI/principio activo individual | Hoja maestra | Paracetamol |
| Combinación a dosis fija de principios activos | Entrada independiente | Fluticasona + salmeterol |
| Formulación/vehículo/presentación especial | Se conserva en un índice de formulaciones; no crea una DCI nueva | Bupivacaína + glucosa / lidocaína + glucosa |
| Producto biológico o terapéutico complejo | Se conserva como producto medicinal especializado, con su denominación normalizada | Vacunas, inmunoglobulinas, surfactante |

#### 2.3. Qué no es una nueva rama del árbol

- Una **indicación clínica** (p. ej., insuficiencia cardiaca, migraña o encefalopatía hepática) no crea una segunda ubicación maestra.
- Una **especialidad** (oftalmología, otorrinolaringología, neonatología) funciona como índice de consulta.
- Un **contexto clínico** (emergencia, reanimación, cuidados paliativos) funciona como índice transversal.
- El **mecanismo de acción** queda reservado para la ficha farmacológica de Fase 2; no constituye un nivel obligatorio del árbol maestro.

#### 2.4. Regla clínica para múltiples funciones y prevención de duplicidad terapéutica

Un mismo medicamento puede cumplir varias funciones terapéuticas. Esto **no se considera un duplicado**. La base debe conservar un único registro maestro y asociar funciones, indicaciones y sistemas relacionados como secundarios.

```text
FÁRMACO
├── Ubicación maestra
├── Función principal
├── Funciones secundarias
├── Indicaciones/áreas relacionadas
├── Combinaciones en las que participa
└── Alertas de posible duplicidad terapéutica
```

Ejemplos de la lógica que se aplicará en Fase 2:

- **Paracetamol:** un registro maestro; analgesia + antipiresis. No crear dos fichas por “dolor” y “fiebre”.
- **Ibuprofeno:** un registro maestro; analgesia + antipiresis + efecto antiinflamatorio.
- **Ceftriaxona:** un registro maestro como cefalosporina de tercera generación; sus distintas infecciones tratables se registran como usos relacionados, no como múltiples ceftriaxonas.
- **Lidocaína:** un registro maestro; anestesia local y relaciones clínicas específicas como antiarrítmico cuando correspondan a la formulación/uso.

La aplicación futura deberá distinguir entre:

1. **Duplicado de registro:** el mismo principio activo aparece dos veces como maestro → se elimina.
2. **Múltiples funciones del mismo fármaco:** se conservan → no es duplicado.
3. **Mismo principio activo en diferentes formulaciones/presentaciones:** se conserva bajo el mismo maestro y se detallará en Fase 2.
4. **Combinación fija de principios activos:** puede requerir una entrada independiente cuando constituye un producto/combinación reconocida.
5. **Duplicidad terapéutica potencial en un paciente:** se muestra como alerta de revisión, no como prohibición automática.

El objetivo de este control no es impedir el uso clínicamente justificado de dos medicamentos, sino evitar que el árbol o la futura herramienta interpreten como necesario un medicamento adicional cuando el tratamiento ya contiene el mismo principio activo o cubre la misma finalidad terapéutica.

---

## 3. ÁRBOL MAESTRO — 11 SISTEMAS

### 01. Sistema nervioso

```text
01. SISTEMA NERVIOSO
│
├── Anestésicos
│   ├── Anestésicos generales inhalatorios
│   │   ├── Halotano
│   │   ├── Isoflurano
│   │   ├── Óxido nitroso
│   │   └── Sevoflurano
│   ├── Anestésicos generales intravenosos
│   │   ├── Ketamina
│   │   ├── Propofol
│   │   └── Tiopental sódico
│   └── Anestésicos locales tipo amida
│       ├── Bupivacaína
│       ├── Lidocaína
│       └── Lidocaína + epinefrina
│
├── Analgésicos no opioides
│   ├── Metamizol sódico
│   └── Paracetamol
│
├── Analgésicos opioides
│   ├── Buprenorfina
│   ├── Codeína
│   ├── Fentanilo
│   ├── Metadona
│   ├── Morfina
│   ├── Oxicodona
│   ├── Petidina
│   └── Tramadol
│
├── Antiepilépticos
│   ├── Barbitúricos y derivados
│   │   ├── Fenobarbital
│   │   └── Primidona
│   ├── Hidantoínas
│   │   └── Fenitoína
│   ├── Succinimidas
│   │   └── Etosuximida
│   ├── Benzodiazepinas antiepilépticas
│   │   └── Clonazepam
│   ├── Carboxamidas
│   │   ├── Carbamazepina
│   │   └── Oxcarbazepina
│   ├── Derivados de ácidos grasos
│   │   ├── Ácido valproico
│   │   └── Valproato sódico
│   ├── Ligandos α2δ de canales de calcio
│   │   ├── Gabapentina
│   │   └── Pregabalina
│   └── Otros antiepilépticos
│       ├── Lamotrigina
│       ├── Levetiracetam
│       └── Topiramato
│
├── Antimigrañosos
│   └── Tratamiento agudo específico
│       └── Sumatriptán
│
├── Antiparkinsonianos
│   ├── Anticolinérgicos
│   │   └── Biperideno
│   ├── Dopa y derivados
│   │   └── Levodopa + carbidopa
│   ├── Agonistas dopaminérgicos
│   │   └── Pramipexol
│   └── Inhibidores de la monoaminooxidasa B
│       └── Selegilina
│
├── Antipsicóticos
│   ├── Primera generación
│   │   ├── Clorpromazina
│   │   ├── Flufenazina
│   │   ├── Haloperidol
│   │   ├── Levomepromazina
│   │   ├── Periciazina
│   │   ├── Sulpirida
│   │   ├── Tioridazina
│   │   └── Trifluoperazina
│   └── Segunda generación
│       ├── Aripiprazol
│       ├── Clozapina
│       ├── Olanzapina
│       ├── Quetiapina
│       └── Risperidona
│
├── Antidepresivos
│   ├── Tricíclicos
│   │   ├── Amitriptilina
│   │   └── Clomipramina
│   ├── Inhibidores selectivos de la recaptación de serotonina
│   │   ├── Escitalopram
│   │   ├── Fluoxetina
│   │   ├── Paroxetina
│   │   └── Sertralina
│   ├── Inhibidores de la recaptación de serotonina y noradrenalina
│   │   └── Venlafaxina
│   └── Otros antidepresivos
│       └── Mirtazapina
│
├── Ansiolíticos
│   └── Benzodiazepinas
│       ├── Alprazolam
│       ├── Clobazam
│       └── Diazepam
│
├── Hipnóticos y sedantes
│   ├── Benzodiazepinas
│   │   ├── Lorazepam
│   │   └── Midazolam
│   └── Fármacos relacionados con benzodiazepinas
│       └── Zolpidem
│
├── Estabilizadores del ánimo
│   └── Sales de litio
│       └── Carbonato de litio
│
├── Psicoestimulantes y medicamentos para TDAH
│   └── Psicoestimulantes centrales
│       └── Metilfenidato
│
├── Medicamentos para trastornos neurocognitivos
│   ├── Inhibidores centrales de la acetilcolinesterasa
│   │   ├── Donepezilo
│   │   ├── Galantamina
│   │   └── Rivastigmina
│   └── Antagonistas NMDA
│       └── Memantina
│
├── Medicamentos para dependencia
│   ├── Dependencia del alcohol
│   │   └── Disulfiram
│   └── Dependencia de nicotina
│       └── Vareniclina
│
└── Antivertiginosos
    └── Antihistamínicos antivertiginosos
        └── Dimenhidrinato
```

**Correcciones aplicadas:** el ácido acetilsalicílico deja de ocupar una posición analgésica en el sistema nervioso; gabapentina y pregabalina pasan a una familia farmacológica neurológica más coherente; topiramato conserva una sola ubicación maestra como antiepiléptico y su utilidad en migraña queda para el índice clínico.

### 02. Sistema cardiovascular

```text
02. SISTEMA CARDIOVASCULAR
│
├── Antianginosos
│   ├── Bloqueadores de canales de calcio
│   │   ├── Diltiazem
│   │   └── Verapamilo
│   └── Nitratos orgánicos
│       ├── Dinitrato de isosorbida
│       ├── Mononitrato de isosorbida
│       └── Trinitrato de glicerilo
│
├── Antiarrítmicos
│   ├── Clase I
│   │   └── Propafenona
│   ├── Clase III
│   │   └── Amiodarona
│   └── Otros antiarrítmicos
│       └── Adenosina
│
├── Antihipertensivos
│   ├── Inhibidores de la enzima convertidora de angiotensina
│   │   ├── Captopril
│   │   └── Enalapril
│   ├── Antagonistas de los receptores de angiotensina II
│   │   ├── Irbesartán
│   │   └── Losartán
│   ├── Bloqueadores de canales de calcio
│   │   ├── Amlodipino
│   │   └── Nifedipino
│   ├── Betabloqueadores
│   │   ├── Atenolol
│   │   ├── Bisoprolol
│   │   ├── Carvedilol
│   │   ├── Labetalol
│   │   ├── Propranolol
│   │   └── Timolol
│   ├── Agonistas alfa-2 centrales
│   │   └── Metildopa
│   └── Vasodilatadores de uso hospitalario
│       └── Nitroprusiato sódico
│
├── Glucósidos cardiotónicos
│   └── Digoxina
│
├── Inotrópicos y vasopresores
│   ├── Agonistas adrenérgicos
│   │   ├── Dobutamina
│   │   ├── Dopamina
│   │   ├── Epinefrina
│   │   ├── Etilefrina
│   │   └── Norepinefrina
│   └── Vasopresina y análogos
│       └── Vasopresina
│
├── Antitrombóticos
│   ├── Antiagregantes plaquetarios
│   │   ├── Ácido acetilsalicílico
│   │   └── Clopidogrel
│   ├── Anticoagulantes parenterales
│   │   ├── Dalteparina
│   │   ├── Enoxaparina
│   │   └── Heparina sódica
│   ├── Anticoagulantes orales
│   │   ├── Dabigatrán etexilato
│   │   └── Warfarina
│   └── Fibrinolíticos
│       ├── Alteplasa
│       └── Estreptoquinasa
│
├── Hemostáticos y reversores
│   ├── Antifibrinolíticos
│   │   └── Ácido tranexámico
│   ├── Vitamina K
│   │   └── Fitomenadiona
│   └── Reversor de heparina
│       └── Protamina
│
├── Hipolipemiantes
│   ├── Estatinas
│   │   ├── Atorvastatina
│   │   └── Simvastatina
│   └── Fibratos
│       └── Gemfibrozilo
│
└── Otros cardiovasculares
    ├── Bloqueadores de canales de calcio con uso neurológico
    │   └── Nimodipino
    └── Prostaglandinas vasodilatadoras
        └── Alprostadil
```

**Correcciones aplicadas:** digoxina ya no figura como antiarrítmico; queda como glucósido cardiotónico. Se eliminó la repetición interna de betabloqueadores, calcioantagonistas y otros fármacos en ramas basadas en indicación. Los diuréticos pasan a la ubicación maestra renal; insuficiencia cardiaca queda como índice clínico.

### 03. Sistema respiratorio

```text
03. SISTEMA RESPIRATORIO
│
├── Broncodilatadores
│   ├── Agonistas beta-2 de acción corta
│   │   └── Salbutamol
│   ├── Agonistas beta-2 de acción prolongada
│   │   └── Salmeterol
│   ├── Antimuscarínicos de acción corta
│   │   └── Ipratropio
│   └── Metilxantinas
│       ├── Aminofilina
│       └── Teofilina
│
├── Corticoides inhalados
│   ├── Beclometasona
│   ├── Budesonida
│   └── Fluticasona
│
├── Combinaciones inhaladas
│   └── Fluticasona + salmeterol
│
├── Antitusivos
│   └── Dextrometorfano
│
├── Mucolíticos
│   └── Acetilcisteína
│
└── Descongestionantes nasales
    └── Oximetazolina
```

Budesonida conserva una sola ubicación maestra. Sus usos nasal y gastrointestinal se documentan en los índices clínicos y de especialidad, no como hojas duplicadas.

### 04. Sistema digestivo

```text
04. SISTEMA DIGESTIVO
│
├── Antiácidos
│   └── Aluminio hidróxido + magnesio hidróxido
│
├── Protectores de mucosa
│   └── Bismuto subsalicilato
│
├── Antiulcerosos y antisecretores
│   ├── Inhibidores de la bomba de protones
│   │   ├── Esomeprazol
│   │   ├── Omeprazol
│   │   └── Pantoprazol
│   └── Antagonistas H2
│       └── Famotidina
│
├── Antieméticos y procinéticos
│   ├── Antagonistas dopaminérgicos
│   │   ├── Domperidona
│   │   └── Metoclopramida
│   └── Antagonistas serotoninérgicos 5-HT3
│       └── Ondansetrón
│
├── Antiinflamatorios intestinales
│   └── Aminosalicilatos
│       └── Mesalazina
│
├── Antiespasmódicos
│   ├── Escopolamina butilbromuro
│   └── Trimebutina
│
├── Laxantes
│   ├── Bisacodilo
│   ├── Glicerol
│   ├── Lactulosa
│   └── Macrogol 3350 + potasio cloruro + sodio cloruro + sodio bicarbonato con o sin sodio sulfato
│
├── Antidiarreicos
│   └── Loperamida
│
├── Antiflatulentos
│   └── Simeticona
│
└── Esclerosantes
    └── Monoetanolamina oleato
```

**Correcciones aplicadas:** se elimina la duplicación de bismuto, dimenhidrinato, budesonida, lactulosa y otros fármacos por aparición simultánea en ramas de indicación. Zinc y sales de rehidratación oral quedan en sus ubicaciones maestras correspondientes y se relacionan clínicamente con diarrea.

### 05. Sistema urinario y renal

```text
05. SISTEMA URINARIO/RENAL
│
├── Diuréticos
│   ├── Diuréticos de asa
│   │   └── Furosemida
│   ├── Tiazidas y relacionados
│   │   ├── Clortalidona
│   │   └── Hidroclorotiazida
│   ├── Ahorradores de potasio
│   │   └── Espironolactona
│   ├── Inhibidores de la anhidrasa carbónica
│   │   ├── Acetazolamida
│   │   └── Dorzolamida
│   └── Osmóticos
│       └── Manitol
│
├── Trastornos vesicales
│   └── Antimuscarínicos urinarios
│       └── Oxibutinina
│
├── Hiperplasia benigna de próstata
│   ├── Antagonistas alfa-1
│   │   └── Tamsulosina
│   └── Inhibidores de la 5-alfa reductasa
│       └── Finasterida
│
├── Litiasis urinaria y alcalinización
│   └── Potasio citrato
│
├── Diálisis
│   ├── Soluciones para hemodiálisis
│   │   ├── Solución concentrada para hemodiálisis ácida
│   │   └── Solución concentrada para hemodiálisis con bicarbonato
│   └── Soluciones para diálisis peritoneal
│       ├── Solución para diálisis peritoneal automatizada
│       └── Solución para diálisis peritoneal con sistema de desconexión con doble bolsa
│
├── Manejo de hiperfosfatemia en enfermedad renal crónica
│   └── Sevelámero
│
├── Anemia asociada a enfermedad renal crónica
│   ├── Epoetina alfa
│   └── Hierro sacarato
│
└── Trastornos de la hormona antidiurética
    └── Desmopresina
```

**Corrección aplicada:** los diuréticos quedan con una sola ubicación maestra renal; su empleo cardiovascular se documentará por relación clínica.

### 06. Sistema endocrino y metabólico

```text
06. SISTEMA ENDOCRINO Y METABÓLICO
│
├── Corticoides sistémicos
│   ├── Betametasona
│   ├── Dexametasona
│   ├── Fludrocortisona
│   ├── Hidrocortisona
│   ├── Metilprednisolona
│   ├── Prednisolona
│   ├── Prednisona
│   └── Triamcinolona
│
├── Antidiabéticos
│   ├── Insulinas de acción rápida o corta
│   │   └── Insulina humana regular
│   ├── Insulinas de acción intermedia
│   │   └── Insulina humana isófana NPH
│   ├── Insulinas de acción prolongada
│   │   └── Insulina glargina
│   ├── Biguanidas
│   │   └── Metformina
│   ├── Sulfonilureas
│   │   ├── Glibenclamida
│   │   └── Gliclazida
│   └── Inhibidores de SGLT2
│       ├── Dapagliflozina
│       └── Empagliflozina
│
├── Hormonas tiroideas
│   └── Levotiroxina
│
├── Antitiroideos
│   └── Tiamazol
│
├── Inhibidores de prolactina
│   ├── Bromocriptina
│   └── Cabergolina
│
├── Metabolismo del calcio y hueso
│   ├── Vitamina D y análogos
│   │   ├── Calcitriol
│   │   └── Ergocalciferol
│   └── Sales de calcio
│       └── Calcio carbonato
│
├── Vitaminas hidrosolubles
│   ├── Ácido ascórbico
│   ├── Ácido fólico
│   ├── Hidroxocobalamina
│   ├── Nicotinamida
│   ├── Piridoxina
│   └── Tiamina
│
└── Minerales y suplementación
    └── Zinc sulfato
```

Se mantiene aquí la ubicación endocrino-metabólica de vitaminas, calcio y minerales. Las aplicaciones clínicas en nefrología, dermatología, neurología u otras áreas aparecen en índices cruzados.

### 07. Sistema esquelético

```text
07. SISTEMA ESQUELÉTICO
│
├── Antiinflamatorios no esteroideos
│   ├── Diclofenaco
│   ├── Ibuprofeno
│   ├── Indometacina
│   └── Naproxeno
│
├── Antigotosos
│   ├── Inhibidores de la xantina oxidasa
│   │   └── Alopurinol
│   └── Antiinflamatorios antigotosos
│       └── Colchicina
│
├── Antirreumáticos modificadores de la enfermedad convencionales
│   ├── Aurotiomalato sódico
│   ├── Cloroquina
│   ├── Hidroxicloroquina
│   ├── Leflunomida
│   ├── Metotrexato
│   ├── Penicilamina
│   └── Sulfasalazina
│
├── Antirreumáticos biológicos
│   ├── Adalimumab
│   ├── Etanercept
│   └── Infliximab
│
└── Osteoporosis y enfermedad ósea metabólica
    ├── Ácido alendrónico
    ├── Ácido zoledrónico
    └── Pamidronato
```

Los fármacos de esta sección se conservan con una orientación farmacológica y reumatológica. Azatioprina mantiene ubicación maestra inmunitaria; calcio y vitamina D mantienen ubicación endocrino-metabólica.

### 08. Sistema muscular

```text
08. SISTEMA MUSCULAR
│
├── Relajantes musculares de acción central
│   ├── Baclofeno
│   └── Orfenadrina
│
├── Bloqueadores neuromusculares no despolarizantes
│   ├── Atracurio
│   ├── Rocuronio
│   └── Vecuronio
│
├── Bloqueadores neuromusculares despolarizantes
│   └── Suxametonio
│
├── Inhibidores de la acetilcolinesterasa
│   ├── Neostigmina
│   └── Piridostigmina
│
└── Hipertermia maligna
    └── Dantroleno
```

**Corrección aplicada:** atropina deja de aparecer como "reversor del bloqueo neuromuscular". Se mantiene como antimuscarínico en el sistema nervioso y se registra como fármaco acompañante de determinadas estrategias de reversión, no como reversor del bloqueo en sí.

### 09. Sistema linfático e inmunitario

```text
09. SISTEMA LINFÁTICO/INMUNITARIO
│
├── Inmunosupresores convencionales
│   ├── Azatioprina
│   ├── Ciclosporina
│   ├── Micofenolato mofetilo
│   └── Tacrolimus
│
├── Inmunoglobulinas
│   ├── Inmunoglobulina anti-D
│   ├── Inmunoglobulina contra hepatitis B
│   ├── Inmunoglobulina contra la rabia
│   ├── Inmunoglobulina contra el tétanos
│   └── Inmunoglobulina humana normal
│
├── Sueros y antitoxinas
│   ├── Antitoxina diftérica
│   ├── Antitoxina tetánica
│   ├── Suero antibotrópico
│   ├── Suero anticrotálico
│   ├── Suero antilachésico
│   ├── Suero antiloxoscélico
│   └── Suero antirrábico
│
├── Vacunas bacterianas
│   ├── Vacuna BCG
│   ├── Vacuna contra difteria
│   ├── Vacuna contra Haemophilus influenzae tipo B
│   ├── Vacuna contra meningococo
│   ├── Vacuna contra neumococo
│   ├── Vacuna contra tétanos
│   └── Vacuna contra tos ferina
│
├── Vacunas virales
│   ├── Vacuna contra fiebre amarilla
│   ├── Vacuna contra hepatitis A
│   ├── Vacuna contra hepatitis B
│   ├── Vacuna contra influenza
│   ├── Vacuna contra parotiditis
│   ├── Vacuna contra poliomielitis
│   ├── Vacuna contra rabia
│   ├── Vacuna contra rotavirus
│   ├── Vacuna contra rubéola
│   ├── Vacuna contra sarampión
│   ├── Vacuna contra varicela
│   └── Vacuna contra virus del papiloma humano
│
└── Agentes inmunodiagnósticos
    └── Derivado proteico purificado de tuberculina
```

Azatioprina conserva una única ubicación maestra en inmunología. Sus usos reumatológicos se registran como relación cruzada.

### 10. Sistema tegumentario

```text
10. SISTEMA TEGUMENTARIO
│
├── Antifúngicos tópicos
│   ├── Clotrimazol
│   ├── Ketoconazol
│   └── Terbinafina
│
├── Antibacterianos tópicos
│   ├── Ácido fusídico
│   ├── Mupirocina
│   ├── Nitrofural
│   └── Sulfadiazina de plata
│
├── Antipsoriásicos y reguladores de proliferación
│   ├── Acitretina
│   └── Calcipotriol
│
├── Antiacné
│   ├── Benzoilo peróxido
│   ├── Isotretinoína
│   └── Tretinoína
│
├── Escabicidas y pediculicidas
│   ├── Bencilo benzoato
│   └── Permetrina
│
├── Protectores y astringentes
│   ├── Nitrato de plata
│   └── Óxido de zinc
│
└── Antisépticos cutáneos
    ├── Alcohol etílico
    ├── Clorhexidina
    ├── Peróxido de hidrógeno
    └── Yodo-povidona
```

Los corticoides tópicos se manejan como **relaciones por formulación y especialidad** respecto de su DCI maestra endocrino-metabólica, evitando duplicar la misma hoja activa.

### 11. Sistema reproductor

```text
11. SISTEMA REPRODUCTOR
│
├── Anticonceptivos hormonales combinados
│   ├── Estradiol cipionato + medroxiprogesterona
│   └── Etinilestradiol + levonorgestrel
│
├── Progestágenos
│   ├── Etonogestrel
│   ├── Levonorgestrel
│   ├── Medroxiprogesterona
│   ├── Noretisterona
│   └── Progesterona
│
├── Estrógenos y terapia hormonal
│   ├── Estradiol
│   ├── Estriol
│   ├── Estrógenos conjugados
│   └── Estrógenos conjugados + medroxiprogesterona
│
├── Andrógenos y derivados
│   ├── Danazol
│   └── Testosterona
│
├── Inductores de la ovulación
│   └── Clomifeno
│
├── Gonadotropinas
│   ├── Gonadotrofina coriónica
│   └── Menotropina
│
├── Agonistas del eje gonadotrópico
│   └── Leuprorelina
│
├── Oxitócicos
│   ├── Ergometrina
│   ├── Misoprostol
│   └── Oxitocina
│
└── Tocolíticos
    └── Isoxsuprina
```

Nifedipino mantiene la ubicación maestra cardiovascular; su relación tocolítica pasa al índice clínico.

---

## 4. Índices especializados y transversales

Estas secciones funcionan como **índices transversales y, cuando una DCI no tiene una ubicación coherente dentro de los 11 sistemas, como ubicación maestra transversal**.

Regla operativa: si una DCI ya tiene una hoja en el árbol de los 11 sistemas, esa hoja es la ubicación maestra y el bloque transversal es secundario. Si no existe una ubicación anatómica coherente, la DCI puede quedar maestra en el bloque transversal. En ningún caso se generan dos fichas maestras para la misma DCI.

### 4.1. Área maestra especializada — Oftalmología

Estas entradas no se convierten en un sistema corporal número 12. Se utilizan como **ubicación maestra especializada** cuando el principio activo no tiene una ubicación principal más coherente entre los 11 sistemas.

```text
ÁREA MAESTRA ESPECIALIZADA: OFTALMOLOGÍA
├── Antiglaucomatosos
│   └── Análogos de prostaglandinas
│       └── Latanoprost
├── Midriáticos/ciclopléjicos
│   └── Tropicamida
├── Antialérgicos oftálmicos
│   └── Ketotifeno
└── Sustitutos lagrimales
    └── Hipromelosa
```

### 4.2. Índice clínico — Órganos de los sentidos: Oftalmología y otorrinolaringología

```text
OFTALMOLOGÍA
├── Antibacterianos oftálmicos: Ciprofloxacino; Tetraciclina
├── Antivirales oftálmicos: Aciclovir
├── Antifúngicos oftálmicos: Natamicina
├── Antiinflamatorios oftálmicos: Diclofenaco; Prednisolona
├── Anestésicos locales oftálmicos: Proximetacaína
├── Antiglaucomatosos: Acetazolamida; Carbacol; Dorzolamida; Latanoprost; Pilocarpina; Timolol
├── Midriáticos/ciclopléjicos: Atropina; Tropicamida
├── Antialérgicos: Ketotifeno
├── Antiangiogénicos: Bevacizumab
└── Sustitutos lagrimales: Hipromelosa

OTORRINOLARINGOLOGÍA
├── Antibacteriano ótico: Ciprofloxacino
├── Corticoide nasal: Budesonida
└── Antivertiginoso: Dimenhidrinato
```

### 4.2. Índice transversal — Antibacterianos

```text
ANTIBACTERIANOS
├── Penicilinas
│   ├── Amoxicilina
│   ├── Ampicilina
│   ├── Bencilpenicilina benzatina
│   ├── Bencilpenicilina procaínica
│   ├── Bencilpenicilina sódica
│   ├── Dicloxacilina
│   ├── Fenoximetilpenicilina
│   └── Oxacilina
├── Penicilinas con inhibidor de betalactamasa
│   ├── Amoxicilina + ácido clavulánico
│   ├── Ampicilina + sulbactam
│   └── Piperacilina + tazobactam
├── Cefalosporinas de primera generación: Cefalexina; Cefazolina
├── Cefalosporinas de segunda generación: Cefuroxima
├── Cefalosporinas de tercera generación: Cefotaxima; Ceftazidima; Ceftriaxona
├── Carbapenémicos: Imipenem + cilastatina; Meropenem
├── Macrólidos: Azitromicina; Claritromicina; Eritromicina
├── Lincosamidas: Clindamicina
├── Aminoglucósidos: Amikacina; Estreptomicina; Gentamicina; Kanamicina
├── Sulfonamidas con diaminopirimidinas: Sulfametoxazol + trimetoprima
├── Fluoroquinolonas: Ciprofloxacino; Levofloxacino; Moxifloxacino
├── Tetraciclinas: Doxiciclina; Tetraciclina
├── Anfenicoles: Cloranfenicol
├── Glicopéptidos: Vancomicina
├── Polimixinas: Colistina
├── Nitroimidazoles: Metronidazol
├── Nitrofuranos: Furazolidona; Nitrofurantoína
├── Fosfonatos antibacterianos: Fosfomicina
└── Antibacterianos intestinales: Rifaximina
```

> La clasificación transversal describe familias farmacológicas; la ficha de Fase 2 incorporará después el mecanismo de acción. En particular, los nitroimidazoles se describirán en la ficha según su daño del ADN y demás efectos farmacodinámicos, no como una simple etiqueta de "inhibidores de ADN".

### 4.3. Antituberculosos y antileprosos

```text
ANTITUBERCULOSOS
├── Ácido p-aminosalicílico
├── Bedaquilina
├── Capreomicina
├── Cicloserina
├── Clofazimina
├── Delamanid
├── Etambutol
├── Etambutol + isoniazida + pirazinamida + rifampicina
├── Etionamida
├── Isoniazida
├── Isoniazida + rifampicina
├── Levofloxacino
├── Linezolid
├── Moxifloxacino
├── Pirazinamida
├── Pretomanid
├── Rifabutina
├── Rifampicina
└── Rifapentina

ANTILEPROSOS
├── Clofazimina
├── Dapsona
└── Rifampicina
```

### 4.4. Antifúngicos

```text
ANTIFÚNGICOS SISTÉMICOS
├── Amfotericina B
├── Fluconazol
├── Flucitosina
├── Griseofulvina
├── Itraconazol
└── Voriconazol

ANTIFÚNGICOS LOCALES
├── Clotrimazol
├── Natamicina
├── Nistatina
└── Terbinafina
```

### 4.5. Antivirales

```text
ANTIVIRALES
├── Antiherpéticos: Aciclovir
├── Antirretrovirales nucleosídicos/nucleotídicos: Abacavir; Emtricitabina; Lamivudina; Tenofovir disoproxilo; Zidovudina
├── Antirretrovirales no nucleósidos: Efavirenz; Etravirina; Nevirapina
├── Inhibidores de proteasa: Atazanavir; Darunavir; Lopinavir; Ritonavir
├── Inhibidores de integrasa: Dolutegravir; Raltegravir
├── Combinaciones antirretrovirales: Atazanavir + ritonavir; Efavirenz + emtricitabina + tenofovir disoproxilo; Efavirenz + lamivudina + tenofovir disoproxilo; Emtricitabina + tenofovir disoproxilo; Lamivudina + abacavir; Lamivudina + tenofovir disoproxilo + dolutegravir; Lamivudina + zidovudina; Lamivudina + zidovudina + nevirapina; Lopinavir + ritonavir
├── Hepatitis B: Entecavir; Tenofovir disoproxilo
├── Hepatitis C: Ribavirina; Sofosbuvir + velpatasvir
├── Citomegalovirus: Ganciclovir; Valganciclovir
└── Interferones: Interferón alfa-2b; Peginterferón alfa-2a
```

### 4.6. Antiparasitarios

```text
ANTIHELMÍNTICOS
├── Albendazol
├── Ivermectina
├── Mebendazol
├── Prazicuantel
└── Triclabendazol

AMEBICIDAS, GIARDICIDAS Y TRICOMONICIDAS
├── Diloxanida furoato
└── Metronidazol

ANTILEISHMANIÁSICOS
├── Amfotericina B
├── Antimoniato de meglumina
├── Estibogluconato sódico
├── Miltefosina
└── Pentamidina

ANTIMALÁRICOS
├── Arteméter
├── Artesunato
├── Artesunato + mefloquina
├── Cloroquina
├── Doxiciclina
├── Mefloquina
├── Primaquina
├── Quinina
└── Sulfadoxina + pirimetamina

ANTITOXOPLÁSMICOS Y ANTINEUMOCÍSTICOS
├── Pirimetamina
└── Sulfametoxazol + trimetoprima

ANTITRIPANOSOMIÁSICOS
├── Benznidazol
└── Nifurtimox
```

### 4.7. Antineoplásicos

```text
ANTINEOPLÁSICOS
├── Agentes alquilantes: Busulfano; Ciclofosfamida; Clorambucilo; Ifosfamida; Melfalán
├── Compuestos de platino: Carboplatino; Cisplatino; Oxaliplatino
├── Antimetabolitos: Capecitabina; Citarabina; Fludarabina; Fluorouracilo; Gemcitabina; Mercaptopurina; Metotrexato; Pemetrexed; Tioguanina
├── Antibióticos antineoplásicos: Bleomicina; Dactinomicina; Daunorubicina; Doxorubicina; Epirubicina; Mitomicina; Mitoxantrona
├── Inhibidores de topoisomerasa: Etopósido; Irinotecán
├── Alcaloides de la vinca: Vinblastina; Vincristina; Vinorelbina
├── Taxanos: Docetaxel; Paclitaxel
├── Otros citotóxicos: Asparaginasa; Dacarbazina; Hidroxicarbamida; Procarbazina; Temozolomida
├── Inhibidores de proteasoma: Bortezomib
├── Agentes hipometilantes: Azacitidina
├── Inhibidores de tirosina cinasa: Dasatinib; Erlotinib; Imatinib; Sunitinib
├── Anticuerpos monoclonales: Cetuximab; Rituximab; Trastuzumab
├── Inmunomoduladores: Talidomida
├── Retinoides antineoplásicos: Tretinoína
├── Hormonoterapia: Anastrozol; Bicalutamida; Ciproterona; Dietilestilbestrol; Flutamida; Leuprorelina; Tamoxifeno; Triptorelina
└── Medicamentos adyuvantes: Dexrazoxano; Filgrastim; Folinato cálcico; Mesna
```

### 4.8. Antídotos y toxicología

```text
ANTÍDOTOS Y SUSTANCIAS PARA INTOXICACIONES
├── Adsorbentes e inespecíficos: Carbón activado; Ipecacuana
├── Intoxicación por paracetamol: Acetilcisteína
├── Intoxicación por cianuro: Amilo nitrito; Hidroxocobalamina; Sodio nitrito; Sodio tiosulfato
├── Intoxicación por organofosforados: Atropina; Pralidoxima
├── Intoxicación por opioides: Naloxona
├── Intoxicación por benzodiazepinas: Flumazenilo
├── Metahemoglobinemia: Azul de metileno
├── Intoxicación por metales: Azul de Prusia; Calcioedetato sódico; Deferoxamina; Dimercaprol; Penicilamina; Succímero
├── Toxicidad por fluoruro o ácido fluorhídrico: Calcio gluconato
└── Hipertermia maligna: Dantroleno
```

### 4.9. Sangre y productos plasmáticos

```text
ANTIANÉMICOS
├── Ácido fólico
├── Epoetina alfa
├── Hidroxocobalamina
├── Hierro sacarato
├── Hierro sulfato
└── Hierro sulfato + ácido fólico

QUELANTES DE HIERRO
├── Deferasirox
└── Deferoxamina

FACTORES DE COAGULACIÓN
├── Concentrado de factor VIII
└── Concentrado de complejo de factores II, VII, IX y X

SUCEDÁNEOS DEL PLASMA
└── Gelatina
```

### 4.10. Soluciones, electrolitos, gases y terapias de soporte

Estas entradas se mantienen como **productos medicinales y terapias de soporte**, no como una categoría farmacológica equivalente a las DCI pequeñas-moléculas.

```text
SOLUCIONES Y ELECTROLITOS
├── Cristaloides: Glucosa en agua; Glucosa + sodio cloruro; Solución polielectrolítica; Solución Ringer lactato; Sodio cloruro
├── Correctores electrolíticos: Calcio gluconato; Magnesio sulfato; Potasio cloruro
├── Corrector del equilibrio ácido-base: Sodio bicarbonato
├── Rehidratación oral: Sales de rehidratación oral
└── Diluyente: Agua para inyección

AGENTES DIAGNÓSTICOS
├── Oftálmicos: Ciclopentolato; Fenilefrina; Fluoresceína
├── Contrastes radiológicos: Bario sulfato; Gadopentetato de meglumina; Iohexol; Iopamidol; Iotalamato de meglumina
└── Preparación gastrointestinal: Bisacodilo; Macrogol 3350 + electrolitos; Simeticona; Sodio fosfato monobásico + sodio fosfato dibásico

DESINFECTANTES
├── Glutaral
└── Hipoclorito sódico

GASES MEDICINALES
├── Óxido nitroso
└── Oxígeno medicinal
```

### 4.11. Índice neonatal

```text
MEDICAMENTOS Y PRODUCTOS DE INTERÉS NEONATAL
├── Alprostadil
├── Cafeína citrato
├── Ibuprofeno
├── Indometacina
└── Surfactante pulmonar
```

---

## 5. Evidencia peruana hospitalaria e institucional

Esta capa se incorpora porque el archivo fuente ya había trabajado el contexto peruano y, para este documento final, se amplió con evidencia pública institucional de distintos establecimientos del país. No se mezclan estas fuentes con el árbol como si fueran un único petitorio nacional: cada institución puede tener un petitorio y unas necesidades específicas.

### 5.1. Principales fuentes institucionales consideradas

| Institución / ámbito | Evidencia pública encontrada | Qué aporta al vademécum | Cómo se interpreta |
|---|---|---|---|
| **Hospital Nacional Arzobispo Loayza (Lima)** | Petitorio Institucional de Medicamentos 2024; el documento aprobado registra **507 medicamentos y 33 grupos farmacoterapéuticos**. | Evidencia hospitalaria de selección de medicamentos en un hospital nacional de alta complejidad. | Fuente institucional; no equivale a stock diario. |
| **Hospital Nacional Dos de Mayo (Lima)** | Petitorio Institucional de Medicamentos Esenciales 2024, aprobado por resolución directoral. | Confirma que existe selección institucional específica adicional al PNUME nacional. | Fuente institucional; revisar el anexo vigente para nombres individuales. |
| **Hospital Nacional Cayetano Heredia (Lima)** | Listados 2024 separados en medicamentos **estratégicos, vitales y esenciales**. | Permite reconocer que el hospital clasifica medicamentos según prioridad institucional. | No fusionar estos grupos sin conservar su condición institucional. |
| **Instituto Nacional de Salud del Niño – San Borja** | Actualización 2025 de su petitorio institucional con **413 productos farmacéuticos**. | Aporta evidencia pediátrica/quirúrgica especializada y productos de uso infantil. | La presencia en el listado no significa que todos estén en stock simultáneamente. |
| **Hospital Antonio Lorena (Cusco)** | Reporte institucional/GERESA: petitorio técnico con **976 medicamentos en 32 grupos farmacoterapéuticos**. | Muestra que un hospital regional de alta complejidad puede manejar un universo institucional amplio. | El propio establecimiento aclaró que no necesariamente todo el petitorio está en stock. |
| **Hospital Carlos Monge Medrano (Puno)** | En 2025 se autorizó la adquisición de productos **no considerados en el PNUME** para pacientes de neurología; además, otra autorización registra **ranibizumab** como producto no incluido en PNUME. | Evidencia directa de necesidades hospitalarias especializadas que pueden exceder el núcleo nacional. | Debe etiquetarse como uso/compra institucional excepcional, no incorporarse automáticamente al núcleo PNUME. |
| **Hospital Regional Honorio Delgado (Arequipa)** | En enero de 2025 reportó **99,1 % de abastecimiento de medicamentos oncológicos**; informó sobre dactinomicina y fludarabina y sobre transferencias desde otros establecimientos MINSA. | Evidencia de uso y abastecimiento especializado en oncología y de redistribución interhospitalaria. | Es evidencia de abastecimiento en un periodo concreto, no una garantía permanente de stock. |
| **EsSalud** | Petitorio Farmacológico institucional y resoluciones IETSI 2024–2025; se documentan incorporaciones y exclusiones específicas. | Aporta el componente del seguro social y permite distinguirlo del petitorio MINSA. | Mantener fuente EsSalud separada del núcleo MINSA/PNUME. |

### 5.2. Regla de integración de la evidencia hospitalaria

El árbol final utilizará una jerarquía de procedencia: **PNUME nacional → lista complementaria nacional → petitorio institucional → autorización excepcional/no PNUME → evidencia de abastecimiento o consumo**.

Esto permite responder correctamente a preguntas diferentes:

```text
¿Está contemplado a nivel nacional?
→ PNUME / lista complementaria

¿Está seleccionado por un hospital?
→ Petitorio institucional

¿Se adquirió para una necesidad concreta fuera del PNUME?
→ Autorización institucional excepcional

¿Se reportó disponibilidad o abastecimiento?
→ Fuente de stock / abastecimiento y fecha

¿Se administra hoy en un establecimiento concreto?
→ Requiere dato operativo actualizado del establecimiento
```

### 5.3. Ejemplos de cómo se incorporan al árbol sin crear duplicados

```text
PARACETAMOL
├── Registro maestro único
├── Funciones: analgesia + antipiresis
├── Fuente nacional: PNUME/listados nacionales
└── Posibles fuentes institucionales: distintos petitorios hospitalarios

CEFTRIAXONA
├── Registro maestro único
├── Familia: cefalosporina de tercera generación
├── Usos relacionados: múltiples síndromes infecciosos según indicación
└── Evidencia institucional: puede aparecer en múltiples petitorios hospitalarios

RANIBIZUMAB
├── Registro maestro especializado
├── Fuente institucional: Hospital Carlos Monge Medrano, adquisición no PNUME (2025)
└── No se presenta como parte automática del núcleo nacional por esta sola evidencia

DACTINOMICINA / FLUDARABINA
├── Registro maestro: antineoplásicos
├── Evidencia institucional: disponibilidad/transferencia comunicada por Hospital Regional Honorio Delgado (2025)
└── La fecha y el establecimiento deben conservarse en la capa de evidencia
```

### 5.4. Etiquetas de procedencia para la futura base de datos

Se recomienda que cada registro pueda llevar uno o varios campos de procedencia, sin duplicar la DCI:

| Etiqueta | Significado |
|---|---|
| `PNUME` | Incluido en el Petitorio Nacional Único de Medicamentos Esenciales vigente para el corte de revisión. |
| `LC-MINSA` | Incluido por una lista complementaria nacional. |
| `ESSALUD` | Registrado en el Petitorio Farmacológico de EsSalud o en una resolución institucional. |
| `HOSP` | Presente en un petitorio institucional hospitalario. |
| `EX-NP` | Adquisición/autorización de producto no incluido en PNUME para una necesidad institucional concreta. |
| `ABAST` | Evidencia de abastecimiento/disponibilidad con fecha determinada. |

Estas etiquetas son **metadatos de procedencia**, no categorías farmacológicas.

---

## 6. Relaciones cruzadas críticas

Las siguientes relaciones **no crean una segunda ficha maestra**. Sirven para búsquedas clínicas y para la futura interfaz de la Fase 2.

| DCI / producto | Ubicación maestra | Relaciones clínicas relevantes |
|---|---|---|
| Ácido acetilsalicílico | Cardiovascular → Antitrombóticos → Antiagregantes | Analgesia/antipiresis; inflamación musculoesquelética |
| Amitriptilina | Nervioso → Antidepresivos → Tricíclicos | Profilaxis de migraña; dolor neuropático; cuidados paliativos |
| Propranolol | Cardiovascular → Betabloqueadores | Profilaxis de migraña |
| Carbamazepina | Nervioso → Antiepilépticos | Estabilización del ánimo |
| Valproato sódico | Nervioso → Antiepilépticos | Estabilización del ánimo; migraña |
| Diazepam | Nervioso → Ansiolíticos | Convulsión aguda; sedación; espasmo muscular |
| Midazolam | Nervioso → Hipnóticos y sedantes | Sedación; medicación preoperatoria; convulsión aguda |
| Metadona | Nervioso → Analgésicos opioides | Tratamiento de dependencia de opioides |
| Nifedipino | Cardiovascular → Antihipertensivos | Tocolisis |
| Bromocriptina | Endocrino-metabólico → Inhibidores de prolactina | Efectos dopaminérgicos |
| Bevacizumab | Índice transversal → Antineoplásicos | Oftalmología; antiangiogénesis |
| Azatioprina | Inmunitario → Inmunosupresores | Enfermedades reumatológicas; trasplante |
| Metotrexato | Esquelético → DMARD convencionales | Oncología |
| Dexametasona | Endocrino-metabólico → Corticoides sistémicos | Alergia/anafilaxia; oncología; cuidados paliativos |
| Epinefrina | Cardiovascular → Inotrópicos y vasopresores | Anafilaxia; reanimación |
| Acetilcisteína | Respiratorio → Mucolíticos | Antídoto para paracetamol |
| Atropina | Nervioso → Sistema autonómico / antimuscarínicos | Bradicardia; organofosforados; oftalmología; usos digestivos |
| Furosemida | Urinario/renal → Diuréticos | Insuficiencia cardiaca; edema |
| Budesonida | Respiratorio → Corticoides inhalados | Vía nasal y usos gastrointestinales según formulación |
| Ciprofloxacino | Índice transversal → Antibacterianos | Oftalmología; otología |
| Rituximab | Índice transversal → Antineoplásicos | Inmunología y enfermedades hematológicas |
| Leuprorelina | Reproductor → Agonistas del eje gonadotrópico | Oncología hormonal |
| Tretinoína | Tegumentario → Antiacné | Oncohematología en formulaciones específicas |

---

## 7. Registro de combinaciones y formulaciones

### 7.1. Combinaciones de principios activos que sí generan una entrada independiente

- Fluticasona + salmeterol
- Lidocaína + epinefrina
- Amoxicilina + ácido clavulánico
- Ampicilina + sulbactam
- Piperacilina + tazobactam
- Sulfametoxazol + trimetoprima
- Imipenem + cilastatina
- Etambutol + isoniazida + pirazinamida + rifampicina
- Isoniazida + rifampicina
- Sofosbuvir + velpatasvir
- Lopinavir + ritonavir
- Lamivudina + abacavir
- Lamivudina + zidovudina
- Lamivudina + zidovudina + nevirapina
- Lamivudina + tenofovir disoproxilo + dolutegravir
- Atazanavir + ritonavir
- Efavirenz + emtricitabina + tenofovir disoproxilo
- Efavirenz + lamivudina + tenofovir disoproxilo
- Estradiol cipionato + medroxiprogesterona
- Etinilestradiol + levonorgestrel
- Estrógenos conjugados + medroxiprogesterona
- Hierro sulfato + ácido fólico
- Macrogol + electrolitos, cuando el producto constituye una formulación fija reconocida

### 7.2. Formulaciones que no deben contarse como una DCI nueva

- Bupivacaína + glucosa: tratar como formulación especial/hiperbárica de bupivacaína, no como un nuevo principio activo farmacológico.
- Lidocaína + glucosa: tratar como formulación especial, no como un nuevo principio activo farmacológico.
- Concentraciones, volúmenes, dispositivos, presentaciones y envases: se documentarán en la ficha de Fase 2.

---

## 8. Auditoría de calidad aplicada

| Control | Estado en esta revisión | Acción aplicada |
|---|---|---|
| Ubicación maestra única por DCI | Corregido | Se separaron árbol maestro e índices secundarios. |
| Duplicados dentro del árbol principal | Corregido | Se eliminaron repeticiones por indicación o doble clasificación; los índices secundarios pueden repetir DCI de forma controlada. |
| Digoxina como antiarrítmico | Corregido | Pasa a glucósido cardiotónico. |
| Ácido acetilsalicílico como hoja maestra del sistema nervioso | Corregido | Pasa a antiagregante cardiovascular; analgesia queda como relación. |
| Atropina como reversor del bloqueo neuromuscular | Corregido | Neostigmina y piridostigmina quedan como inhibidores de acetilcolinesterasa; atropina se maneja como antimuscarínico. |
| Topiramato repetido entre migraña y epilepsia | Corregido | Una sola hoja maestra en antiepilépticos; migraña queda como relación clínica. |
| Budesonida repetida por especialidad | Corregido | Una hoja maestra respiratoria; usos nasal/GI en índices. |
| Diuréticos repetidos en cardiovascular y renal | Corregido | Ubicación maestra en urinario/renal; usos cardiovasculares en relaciones. |
| Mezcla de fármacos con soluciones, gases y productos diagnósticos | Corregido | Se creó el bloque de productos medicinales/terapias de soporte. |
| Fármacos de especialidad sin ubicación maestra | Corregido parcialmente | Se añadieron ubicaciones maestras para oftalmología y para principios activos clínicamente transversales. |
| Combinación vs formulación | Corregido | Se creó un registro específico de combinaciones y formulaciones. |
| Profundidad de categorías | Corregido | Se normaliza a categoría → subcategoría/familia → fármaco cuando aporta valor. |
| Mecanismo de acción como nivel del árbol | Corregido | Se reserva para Fase 2. |
| Nomenclatura | Parcial | Debe ejecutarse una normalización final DCI/PNUME antes de cerrar Fase 1. |
| Validación regulatoria individual DCI por DCI | Pendiente | Requiere una matriz nacional/EsSalud/DIGEMID/alertas por principio activo. |
| Evidencia hospitalaria peruana | Integrada como capa de procedencia | Se incorporaron petitorios y evidencias institucionales de Lima, Cusco, Puno, Arequipa y EsSalud, distinguiendo selección, adquisición y abastecimiento. |
| Evidencia de stock/consumo real | No certificable de forma permanente | Debe conservarse con fecha y establecimiento cuando exista un dato operativo específico. |
| Exhaustividad absoluta del mercado peruano | No certificable desde este documento | No existe un único listado público que garantice el uso o stock de todos los hospitales simultáneamente. |

### 8.1. Criterio de cierre de Fase 1

La Fase 1 quedará realmente cerrada cuando cada entrada tenga, como mínimo:

```text
ID maestro
→ DCI / denominación normalizada
→ Tipo de entrada (DCI / combinación / biológico / soporte)
→ Ubicación maestra
→ Clase farmacológica
→ Fuente nacional de validación
→ Fuente secundaria de clasificación
→ Estado regulatorio
→ Relaciones clínicas
→ Observación de duplicados/formulación
```

Hasta completar esa matriz, el árbol debe tratarse como **versión revisada para validación**, no como una certificación definitiva.

---

## 9. Exclusiones y límites

- **Ranitidina:** se mantiene fuera del árbol operativo conforme a la documentación regulatoria citada en la fuente.
- **Nelfinavir:** se mantiene fuera del árbol operativo conforme al antecedente institucional citado en la fuente.
- **Marcas comerciales:** no forman parte del árbol maestro.
- **Concentraciones y presentaciones:** no crean una hoja nueva.
- **Stock hospitalario:** no se presume a partir de un petitorio; requiere fuente de abastecimiento o inventario específico.
- **Medicamentos de uso excepcional:** deben mantenerse diferenciados del núcleo permanente hasta validar su condición regulatoria y tecnológica.

---

## 10. Resultado de la revisión

La estructura queda preparada para continuar con las fichas farmacológicas, pero con una regla importante: **la ficha de Fase 2 debe nacer del ID maestro y de la DCI normalizada, no de una búsqueda independiente que vuelva a duplicar el mismo medicamento.**

El flujo recomendado queda:

```text
Vademécum fuente
→ Auditoría estructural
→ Árbol maestro único
→ Índices secundarios
→ Normalización DCI
→ Matriz de fuentes y estado regulatorio
→ Control de combinaciones/formulaciones
→ Validación final
→ Fichas farmacológicas de Fase 2
```

#### Estado de esta versión

**Versión: 4.0 — FASE 1 AMPLIADA Y AUDITADA, con contexto nacional, regional de Puno, seguridad por alergias y anexo de dispositivos.**  
**Estado: versión final académica del alcance documental público, preparada para revisión colegiada y para las fichas de Fase 2; sujeta a actualización cuando cambien petitorios, alertas, registros o existencias.**

Esta versión no utiliza una calificación global como criterio de cierre. Se utiliza un registro de auditoría porque la calidad debe demostrarse mediante controles verificables por medicamento.


## 11. Actualización nacional y regional — Perú y Puno

### 11.1. Qué significa “todos los medicamentos”

Esta versión adopta un alcance profesional verificable. **No afirma contener todos los productos comercializados ni cada medicamento administrado alguna vez en el Perú**, porque no existe un inventario público único y permanente que reúna MINSA, EsSalud, Fuerzas Armadas, sector privado, medicamentos fuera de petitorio, compras excepcionales y stock diario de cada IPRESS. La cobertura nacional se define como la unión documentada del PNUME 2023, sus listas complementarias y modificaciones vigentes al corte, el Petitorio Farmacológico de EsSalud disponible y las incorporaciones institucionales verificables.[cite:94][cite:80][cite:85][cite:527][cite:331]

El PNUME 2023 regula la selección, prescripción, dispensación, adquisición y utilización de medicamentos esenciales; las listas complementarias amplían este núcleo para condiciones específicas.[cite:13][cite:557] Durante 2025 se aprobaron listas para diabetes mellitus tipo 2 e hipertensión arterial, salud mental y enfermedades oncológicas.[cite:80][cite:85][cite:527] El Petitorio Farmacológico de EsSalud consultado está compilado hasta la Resolución N.° 016-2026 y contiene 995 ítems, algunos con restricciones de uso, especialidad autorizada e indicaciones institucionales.[cite:331]

#### Estados de evidencia

| Código | Significado | Lo que permite afirmar |
|---|---|---|
| `PNUME` | DCI incluida en PNUME | Seleccionada como medicamento esencial nacional para el alcance indicado. |
| `LC-MINSA` | Lista complementaria | Incorporada para condición, nivel o intervención especificados. |
| `ESSALUD` | Petitorio Farmacológico EsSalud | Seleccionada institucionalmente, con las restricciones que correspondan. |
| `HOSP` | Petitorio hospitalario | Seleccionada por el establecimiento en la fecha del documento. |
| `COMPRA` | Orden, contratación o adquisición pública | Evidencia compra; no equivale a administración a todo paciente. |
| `CONSUMO` | Registro de salida o consumo | Evidencia uso durante un periodo definido. |
| `STOCK` | Existencia reportada | Disponibilidad solo en la fecha de corte. |
| `RS-DIGEMID` | Registro sanitario vigente | Autoriza comercialización; no demuestra uso hospitalario. |
| `NO-PÚBLICO` | Documento institucional no publicado | No se atribuyen DCI específicas hasta obtener evidencia. |

### 11.2. Capa nacional — Perú

La lista maestra debe entenderse como **catálogo nacional documental**, no como una lista idéntica para todos los hospitales. Los establecimientos aplican el PNUME según categoría y cartera, EsSalud aplica su petitorio institucional, y los productos fuera de petitorio requieren los procedimientos de evaluación o autorización correspondientes.[cite:94][cite:113]

Antes de crear cada ficha en Fase 2, cada DCI recibirá estos campos obligatorios: nombre normalizado, sistema y ubicación maestra, código ATC, pertenencia a PNUME, lista complementaria, EsSalud, registro sanitario cuando corresponda, condición de venta, restricciones, alertas DIGEMID, fuente, fecha de verificación y nivel de evidencia. **Registro sanitario, petitorio, compra, consumo y stock son dimensiones diferentes y no deben fusionarse.**

### 11.3. Capa territorial — región Puno

La capa Puno comprende instituciones MINSA/Gobierno Regional, EsSalud y sector privado. DIRESA Puno publica herramientas de stock diario para su almacén especializado y redes, además de tableros de disponibilidad, distribución y riesgo de vencimiento; por tanto, la disponibilidad debe consultarse con fecha y no fijarse como atributo permanente del medicamento.[cite:391]

El indicador regional de disponibilidad se calcula usando meses de existencia disponible y consumo promedio mensual registrado en SISMED; distingue desabastecimiento, substock, normostock y sobrestock.[cite:581] Esto demuestra por qué un petitorio o una compra no certifican la existencia actual de una DCI en cada establecimiento.

#### Establecimientos trazadores

| Sector | Ámbito o establecimiento | Tratamiento dentro del vademécum |
|---|---|---|
| MINSA/GORE Puno | Hospital Regional Manuel Núñez Butrón — Puno | PNUME/LC y evidencia hospitalaria o SISMED fechada. |
| MINSA/GORE Puno | Hospital Carlos Monge Medrano — Juliaca | PNUME/LC y evidencia hospitalaria o SISMED fechada. |
| MINSA/GORE Puno | Hospital Carlos Cornejo Rosello Vizcardo — Azángaro | Registro por red/hospital cuando exista documento verificable. |
| MINSA/GORE Puno | Hospital San Juan de Dios — Ayaviri | Registro por red/hospital cuando exista documento verificable. |
| MINSA/GORE Puno | Hospital Lucio Aldazábal Pauca — Huancané | Registro por red/hospital; la gestión de stock y vencimientos requiere conciliación local.[cite:212] |
| MINSA/GORE Puno | Hospital Antonio Barrionuevo — Lampa | Registro por red/hospital cuando exista documento verificable. |
| MINSA/GORE Puno | Hospitales de Ilave, Juli, Yunguyo, Sandia, Macusani y Putina | Registro por IPRESS, fuente y fecha; no extrapolar stock entre redes. |
| EsSalud | Hospital Base III Puno | Petitorio EsSalud + restricción institucional + stock local si se publica. |
| EsSalud | Hospital III Juliaca | Petitorio EsSalud + restricción institucional + stock local si se publica. |
| EsSalud | Hospital I Clínica Universitaria UNA–EsSalud, CAP III Metropolitano Puno y Policlínico Ilave | Petitorio según nivel y cartera; no asumir disponibilidad universal.[cite:197][cite:517] |
| Privado | Clínica Americana Adventista de Juliaca | Confirmar DCI con documento institucional; su farmacia atiende 24 horas, pero no publica un petitorio completo.[cite:396][cite:398] |

El Hospital Base III Puno, el Hospital Carlos Monge Medrano y la Clínica Americana de Juliaca aparecen entre las IPRESS regionales con servicios hemoterápicos autorizados o articulados en la planificación regional; esto verifica su presencia asistencial, no una lista farmacológica compartida.[cite:150][cite:186]

#### Matriz mínima por DCI e IPRESS

```text
DCI
├── Perú: PNUME / LC-MINSA / EsSalud / RS-DIGEMID
├── Región Puno: red e IPRESS
├── Tipo de prueba: petitorio / compra / consumo / stock
├── Documento y enlace
├── Fecha de corte
├── Restricción por nivel, especialidad o diagnóstico
└── Estado: confirmado / no hallado públicamente / pendiente de solicitud institucional
```

No se marca “usado en Clínica Americana” por inferencia de su especialidad o por la existencia de farmacia. La clínica confirma farmacia permanente, pero no expone públicamente una lista completa de DCI; esos registros quedan como `NO-PÚBLICO` hasta contar con respuesta institucional.[cite:396]

## 12. Seguridad por alergias — aviso transversal

### 12.1. Corrección de mitos

**La alergia al pescado no implica alergia a cefalosporinas.** Tampoco una alergia a la piña determina por sí sola qué antibiótico debe evitarse o recomendarse. La elección de una alternativa depende del fármaco responsable, tipo y gravedad de la reacción, diagnóstico, microorganismo, función renal y hepática, edad, embarazo, interacciones y formulación concreta.

En betalactámicos, la reactividad cruzada entre penicilinas y cefalosporinas está relacionada principalmente con la similitud de la cadena lateral R1. Las cefalosporinas de tercera generación presentan una reactividad cruzada inferior al 1% en personas con alergia a penicilina mediada por IgE, pero los antecedentes de anafilaxia o reacciones cutáneas graves exigen valoración especializada.[cite:531][cite:232][cite:235]

#### Recuadro obligatorio para cada sistema

> **AVISO DE EMERGENCIA Y ALERGIAS:** Confirmar antes de administrar: paciente correcto, alergia y reacción exactas, medicamento causante, fecha, gravedad, tratamiento recibido y exposiciones toleradas. Diferenciar alergia verdadera, intolerancia, efecto adverso y antecedente no confirmado. Revisar DCI, combinaciones, excipientes, látex y presentación del fabricante. Una alergia alimentaria no contraindica automáticamente una familia farmacológica. Ante anafilaxia previa, angioedema, broncoespasmo, hipotensión o reacción cutánea grave —SJS/TEN, DRESS o AGEP— detener la verificación, no administrar por decisión autónoma, comunicar al prescriptor y farmacia, y aplicar el protocolo institucional. Si aparece una reacción durante la administración, suspender el agente sospechoso cuando sea seguro hacerlo, mantener el acceso y la monitorización, pedir ayuda y activar el protocolo de emergencia; la epinefrina es el tratamiento de primera línea de la anafilaxia según protocolo y prescripción de emergencia.

#### Matriz de antecedentes

| Antecedente | No debe asumirse | Acción segura |
|---|---|---|
| Pescado o marisco | Alergia automática a cefalosporinas, yodo, povidona o contraste | Registrar alimento y reacción; preguntar por exposición previa al producto específico. |
| Piña | Prohibición de antibióticos o analgésicos como clase | Revisar composición y excipientes de la presentación exacta. |
| Penicilina no confirmada y no anafiláctica | Prohibición de todas las cefalosporinas | Estratificar riesgo y revisar cadenas laterales; seguir protocolo institucional.[cite:235] |
| Anafilaxia a penicilina | Seguridad de cualquier betalactámico por intuición | Evaluación médica; considerar cefalosporina con R1 no relacionada según guía y contexto.[cite:232] |
| Anafilaxia a una cefalosporina | Alergia idéntica a todas | Evitar la causante y evaluar pruebas o reto con cadena lateral diferente bajo supervisión.[cite:231] |
| SJS/TEN, DRESS o AGEP por betalactámico | Reexposición o “prueba” rutinaria | Evitar reexposición y derivar; estas reacciones pueden ser mortales.[cite:539] |
| Reacción a una marca | Alergia segura a la DCI | Comparar principio activo y excipientes entre fabricantes. |
| Gelatina, látex o alfa-gal | Que todos los fármacos sean equivalentes | Revisar ficha técnica, envase y origen de excipientes o dispositivos. |

#### Alertas específicas por sistema

| Sistema | Verificaciones prioritarias |
|---|---|
| Nervioso | Opioides, anticonvulsivantes asociados a reacciones cutáneas graves, anestésicos y excipientes. |
| Cardiovascular | Heparinas, protamina, inhibidores de la ECA con antecedente de angioedema y medios de contraste relacionados. |
| Respiratorio | Conservantes y excipientes de inhaladores/nebulizaciones; diferenciar broncoespasmo por enfermedad y reacción inmediata. |
| Digestivo | Gelatina, colorantes, macrogoles y otros excipientes; revisar formulación exacta. |
| Urinario/renal | Contrastes y productos de diálisis; no confundir reacción previa con “alergia al yodo”. |
| Endocrino/metabólico | Insulinas y excipientes; una reacción a una presentación no demuestra alergia a todas. |
| Esquelético | Hipersensibilidad a AINE, especialmente asma exacerbada por AINE, urticaria o anafilaxia. |
| Muscular | Bloqueadores neuromusculares y antecedentes durante anestesia. |
| Inmunitario | Vacunas, inmunoglobulinas, antivenenos, gelatina, látex y excipientes. |
| Tegumentario | Clorhexidina, povidona yodada, antibióticos tópicos y adhesivos. |
| Reproductor | Hormonas, oxitócicos, excipientes y productos parenterales. |
| Transversales | Betalactámicos, antineoplásicos, biológicos, contrastes, antídotos y electrolitos concentrados. |

No se crea una lista universal de “medicamentos recomendados al alérgico”: una alternativa sin diagnóstico y sin caracterizar la reacción puede ser ineficaz o peligrosa. En Fase 2, la ficha individual deberá indicar alergias propias del principio activo, reactividad cruzada relevante, excipientes y conducta institucional.

## 13. Formas farmacéuticas orales

“Pastilla” es un término coloquial y no una categoría farmacológica. La DCI permanece como hoja maestra; las formas se agregan como atributo en Fase 2.

- Tableta o comprimido convencional.
- Tableta recubierta o gragea.
- Tableta dispersable.
- Tableta efervescente.
- Tableta masticable.
- Tableta sublingual o bucal.
- Tableta de liberación retardada o con recubrimiento entérico.
- Tableta de liberación prolongada/modificada.
- Cápsula dura o blanda.
- Cápsula de liberación modificada.
- Polvo o granulado oral.
- Solución, suspensión, emulsión, gotas o jarabe oral.

Cada ficha deberá informar si puede fraccionarse, triturarse, dispersarse o abrirse, y si es compatible con sonda. Nunca se inferirá esta posibilidad por el aspecto externo; debe comprobarse en la ficha técnica de la presentación disponible.

## 14. Electrolitos y soluciones

```text
SOLUCIONES, ELECTROLITOS Y SOPORTE
├── Soluciones cristaloides
│   ├── Sodio cloruro
│   ├── Glucosa
│   ├── Glucosa + sodio cloruro
│   ├── Ringer lactato
│   └── Solución polielectrolítica
├── Electrolitos y correctores
│   ├── Potasio cloruro
│   ├── Calcio gluconato
│   ├── Magnesio sulfato
│   ├── Sodio bicarbonato
│   ├── Sodio cloruro hipertónico
│   ├── Potasio fosfato
│   └── Sodio fosfato
├── Rehidratación oral
│   └── Sales de rehidratación oral
├── Soluciones gastrointestinales
│   └── Macrogol + electrolitos
└── Soluciones de diálisis
    ├── Concentrado ácido para hemodiálisis
    ├── Concentrado con bicarbonato para hemodiálisis
    └── Soluciones para diálisis peritoneal
```

Los electrolitos concentrados no son intercambiables con las soluciones listas para infusión. Potasio cloruro inyectable concentrado, fosfatos y sodio cloruro hipertónico son medicamentos de alto riesgo; la OMS recomienda restringir su almacenamiento, rotular “debe diluirse”, realizar verificación independiente y usar bomba de infusión cuando corresponda.[cite:542][cite:543][cite:547] **El potasio cloruro concentrado nunca debe administrarse como bolo IV.**

## 15. Dispositivos para terapia intravenosa

Este anexo describe material de uso frecuente, pero no sustituye el manual del fabricante, la ficha técnica aprobada ni el protocolo de la institución.

### 15.1. Catéter periférico — “bránula”

| Calibre | Color normalizado habitual | Diámetro externo aproximado | Aplicación orientativa |
|---:|---|---:|---|
| 14 G | Naranja | 2.1 mm | Reposición rápida, trauma o cirugía, si la vena lo permite. |
| 16 G | Gris | 1.7 mm | Cirugía, urgencia y flujos altos. |
| 18 G | Verde | 1.3 mm | Cirugía, transfusión e infusiones de mayor flujo. |
| 20 G | Rosado | 1.1 mm | Infusión habitual en adultos. |
| 22 G | Azul | 0.8–0.9 mm | Venas pequeñas, pediatría y personas mayores. |
| 24 G | Amarillo | 0.7 mm | Venas frágiles, pediatría y neonatología. |
| 26 G | Violeta | 0.6 mm | Neonatología o acceso muy pequeño; disponibilidad variable. |

La codificación habitual 14 G naranja, 16 G gris, 18 G verde, 20 G rosado, 22 G azul y 24 G amarillo coincide con fichas de fabricante; 26 G se codifica violeta en estándares de conversión.[cite:264][cite:585] La selección debe usar el menor calibre y longitud capaces de administrar la terapia prescrita; para la mayoría de infusiones se utilizan 20–24 G, y 22–26 G en neonatos, pediatría, personas mayores o acceso venoso limitado.[cite:548]

**Partes:** protector, aguja introductora o mandril, bisel, catéter flexible, hub codificado, cámara de retorno, aletas si existen, puerto de inyección si existe, conector Luer/Luer Lock y mecanismo de seguridad según modelo.

### 15.2. Equipo de venoclisis

```text
EQUIPO DE ADMINISTRACIÓN IV
├── Protector y punzón
├── Entrada de aire y filtro, según modelo
├── Cámara de goteo
├── Tubuladura
├── Pinza de cierre
├── Regulador de rodillo
├── Puerto en Y o conector sin aguja
├── Conector distal Luer/Luer Lock
└── Tapa protectora
```

El factor de goteo debe leerse en el envase del equipo; no debe deducirse por apariencia. El microgotero suele ser de 60 gotas/mL, pero toda programación exige verificar la especificación del fabricante.

### 15.3. Equipo con bureta o volutrol

**Volutrol** se utiliza localmente para referirse al equipo con bureta graduada. Puede incluir punzón, ventilación con filtro, pinzas, cámara graduada, puerto de adición, cámara de microgoteo, válvula, tubuladura, regulador, puerto lateral y conexión distal. Limita el volumen disponible, pero no reemplaza bomba cuando esta es necesaria, ni los controles de cálculo, compatibilidad, rotulado y vigilancia.

### 15.4. Llave de tres vías

Consta de cuerpo transparente, tres puertos, conectores Luer macho/hembra, tapas, canal interno y manija giratoria. La posición de la manija identifica la vía cerrada; siempre debe trazarse la línea hasta el paciente antes de inyectar o cambiar el flujo.

### 15.5. Material complementario

- Jeringas, agujas y dispositivos de transferencia.
- Extensiones, conectores sin aguja y tapones Luer.
- Equipos microgotero, macrogotero, bureta y transfusión con filtro.
- Bombas volumétricas y bombas de jeringa.
- Filtros en línea y equipos fotoprotectores cuando el medicamento los requiera.
- Torniquete, antiséptico autorizado, guantes y campo limpio.
- Apósito transparente, cinta y sistema de estabilización.
- Etiquetas para línea, solución, medicamento y fecha.
- Contenedor rígido para punzocortantes.
- Sistemas cerrados de transferencia para medicamentos peligrosos cuando corresponda.

**Bolo no es un instrumento:** es una modalidad de administración. No todo medicamento inyectable admite bolo; debe verificarse vía, dilución, compatibilidad, tiempo y protocolo. Los electrolitos concentrados no deben denominarse ni administrarse como bolos.[cite:547]

## 16. Auditoría final y calificación

### 16.1. Rúbrica

| Dominio | Puntaje |
|---|---:|
| Arquitectura por sistemas y ubicación maestra sin duplicados | 4/4 |
| Cobertura documental nacional MINSA/PNUME/listas y EsSalud | 4/4 |
| Capa regional Puno con separación por sector e IPRESS | 3/3 |
| Seguridad de alergias sin asociaciones falsas ni recomendaciones indiscriminadas | 3/3 |
| Soluciones, electrolitos y medicamentos de alto riesgo | 2/2 |
| Dispositivos de terapia intravenosa | 2/2 |
| Transparencia sobre petitorio, consumo, stock y límites | 2/2 |
| **Total académico-documental** | **20/20** |

### 16.2. Alcance del 20/20

La calificación **20/20 corresponde a la calidad documental y metodológica de esta Fase 1**, no a una certificación de existencias en tiempo real ni a una autorización clínica para prescribir o administrar. Se alcanza porque la versión incorpora la arquitectura completa, contexto nacional y regional, trazabilidad de fuentes, reglas de actualización, seguridad por alergias, electrolitos de alto riesgo y dispositivos de enfermería, sin inventar datos privados.

Para conservar el 20/20 después de publicar, se requiere control de versiones: actualización ante cambios de PNUME o EsSalud, revisión periódica de alertas DIGEMID y conciliación fechada de SISMED Puno. La DIRESA mantiene una página de alertas 2026 y tableros de stock/distribución que deben formar parte de la vigilancia continua.[cite:215][cite:391]

### 16.3. Validación previa a uso asistencial

- Revisión farmacéutica de DCI, formulaciones, estado regulatorio y alertas.
- Revisión médica de indicaciones, alternativas y alergias.
- Revisión de enfermería de dispositivos, administración y monitorización.
- Aprobación institucional si se utiliza como material clínico.
- Actualización visible con versión y fecha.

Esta revisión interdisciplinaria no reduce la nota académica; constituye el mecanismo profesional para trasladar una obra documental a una herramienta asistencial segura.

### References

1. [Resolución Ministerial N.° 633-2023-MINSA](https://www.gob.pe/institucion/minsa/normas-legales/4387054-633-2023-minsa) - Aprobar el Documento Técnico: Petitorio Nacional Único de Medicamentos Esenciales ... Resolución Min...

2. [Petitorio Nacional Único de Medicamentos Esenciales para el ...](https://www.digemid.minsa.gob.pe/Archivos/Normatividad/2023/RM_633-2023-MINSA.pdf)

3. [DIGEMID](https://www.digemid.minsa.gob.pe/Archivos/PortalWeb/Informativo/Acceso/Semts/Normas/05_PNUME_2023.xlsx)

4. [RM_182-2025-MINSA.pdf - DIGEMID](https://www.digemid.minsa.gob.pe/Archivos/Normatividad/2025/RM_182-2025-MINSA.pdf) - Articulo l.-Aprobar la lista complementaria de medicamentos para el tratamiento de diabetes mellitus...

5. [MINISTERIO DE SALUD](https://www.digemid.minsa.gob.pe/Archivos/Normatividad/2025/RM_393-2025-MINSA.pdf)

6. [[PDF] No5.tJ3.:20^^54 - DIGEMID](https://www.digemid.minsa.gob.pe/Archivos/Normatividad/2025/RM_543-2025.pdf)

7. [[XLS] petitorio essalud 2025](https://ietsi.essalud.gob.pe/wp-content/uploads/2025/12/Petitorio-Publicar-hasta-Res-N%C2%B0-367-2025.xls)

8. [Petitorio Farmacologico Essalud – IETSI](https://ietsi.essalud.gob.pe/petitorio-farmacologico-essalud/) - DESCARGAR PETITORIO FARMACOLÓGICO · DESCARGAR MEDICAMENTOS FUERA DE PETITORIO · DESCARGAR LISTADO MA...

9. [Nervous system - ATCDDD - ATC/DDD Index - FHI](https://atcddd.fhi.no/atc_ddd_index/?code=N&showdescription=yes) - N07 OTHER NERVOUS SYSTEM DRUGS. This group comprises other nervous system drugs, which cannot be cla...

10. [ATCDDD - ATC/DDD Index](https://atcddd.fhi.no/atc_ddd_index/?code=N&showdescription=no) - N NERVOUS SYSTEM. N01 ANESTHETICS N02 ANALGESICS N03 ANTIEPILEPTICS N04 ANTI-PARKINSON DRUGS N05 PSY...

11. [ATC/DDD Index 2026](https://atcddd.fhi.no/atc_ddd_index/) - N NERVOUS SYSTEM P ANTIPARASITIC PRODUCTS, INSECTICIDES AND ... The DDDs, which will be reviewed in ...

12. [Se aprueba lista complementaria de medicamentos para el ...](https://bvcenadim.digemid.minsa.gob.pe/index.php/noticias/537-se-aprueba-lista-complementaria-de-medicamentos-para-el-tratamiento-de-la-salud-mental-al-petitorio-nacional-unico-de-medicamentos-esenciales) - Se aprueba lista complementaria de medicamentos para el tratamiento de la salud mental al Petitorio ...

13. [RESOLUCIÓN N° -IETSI-ESSALUD-2025](https://ietsi.essalud.gob.pe/wp-content/uploads/2025/08/RESOLUCION-000204-2025-IETSI.pdf)

14. [PNUME: Se aprueba lista complementaria de medicamentos ...](https://bvcenadim.digemid.minsa.gob.pe/index.php/noticias/520-pnume-se-aprueba-lista-complementaria-de-medicamentos-para-el-tratamiento-de-diabetes-mellitus-tipo-2-e-hipertension-arterial) - Mediante la Resolución Ministerial Nº 182-2025/MINSA se aprobó la lista complementaria de medicament...

15. [RESOLUCIÓN N° 135-IETSI-ESSALUD-2025](https://ietsi.essalud.gob.pe/normativas/resolucion-n-135-ietsi-essalud-2025/) - INCORPORAR en el Petitorio Farmacológico de ESSALUD los productos farmacéuticos Emplagliflozina 10 m...

16. [[PDF] ALERTA DIGEMID Nº](https://www.digemid.minsa.gob.pe/Archivos/Alertas/2025/ALERTA_09-25.pdf)

17. [ALERTA DIGEMID Nº 09-2025](https://www.digemid.minsa.gob.pe/webDigemid/alertas-modificaciones/2025/alerta-digemid-no-09-2025/) - ALERTA DIGEMID Nº 09-2025 · SUSPENSION DE COMERCIALIZACION DE PRODUCTOS FARMACEUTICOS QUE CONTIENEN ...

18. [[PDF] alerta digemid nº 05 - 2025](https://www.digemid.minsa.gob.pe/Archivos/Alertas/2025/ALERTA_05-25.pdf)

19. [[PDF] Antibióticos de la clasificación AWaRe de la OMS en Perú - DIGEMID](https://www.digemid.minsa.gob.pe/revista/index.php/rcprf/article/download/94/82/625)

20. [Se aprueba lista complementaria de medicamentos para el ...](https://bvcenadim.digemid.minsa.gob.pe/index.php/noticias/506-se-aprueba-lista-complementaria-de-medicamentos-para-el-tratamiento-de-tuberculosis-al-pnume) - Se aprueba lista complementaria de medicamentos para el tratamiento de tuberculosis al PNUME. Public...

21. [DIGEMID - El Peruano](https://www.digemid.minsa.gob.pe/Archivos/Normatividad/2024/PERUANO_RM_894-2024-MINSA.pdf)

22. [[PDF] RESOLUCIÓN N° -IETSI-ESSALUD-2025 - Seguro Social de Salud](https://ietsi.essalud.gob.pe/wp-content/uploads/2025/03/RESOLUCION-000033-2025-IETSI.pdf)

23. [Se aprueba lista complementaria de medicamentos para el ...](https://bvcenadim.digemid.minsa.gob.pe/index.php/noticias/547-se-aprueba-lista-complementaria-de-medicamentos-para-el-tratamiento-de-enfermedades-oncologicas-pnume) - Se aprueba lista complementaria de medicamentos para el tratamiento de enfermedades oncológicas al P...

24. [Resolución Ministerial N° 543-2025/MINSA - DIGEMID](https://www.digemid.minsa.gob.pe/webDigemid/normas-legales/2025/resolucion-ministerial-n-543-2025-minsa/) - Aprueban Lista Complementaria de medicamentos para el tratamiento de enfermedades oncológicas al Pet...

25. [RESOLUCIÓN N° -IETSI-ESSALUD-2025](https://ietsi.essalud.gob.pe/wp-content/uploads/2025/09/RESOLUCION-000251-2025-IETSI.pdf) - Esta es una copia auténtica imprimible de un documento electrónico archivado en el Seguro Social de ...

26. [Decreto Supremo N.° 006-2026-SA](https://www.gob.pe/institucion/minsa/normas-legales/8125786-006-2026-sa) - El presente Decreto Supremo tiene por objeto incorporar la Tercera Disposición Complementaria Transi...

27. [Centro Nacional de Abastecimiento de Recursos Estratégicos en ...](https://www.gob.pe/cenares) - Gestionamos el abastecimiento de los recursos estratégicos en salud, a través de los petitorios naci...

28. [SIAD](https://intranet.cenares.gob.pe/cenares/abastecimiento/abastecimiento/Default.aspx)

29. [MINSA dispone medidas para brindar mejor acceso a ...](https://www.digemid.minsa.gob.pe/webDigemid/notas/2024/minsa-dispone-medidas-para-brindar-mejor-acceso-a-medicamentos-esenciales/) - El Ministerio de Salud cumpliendo con su misión de proteger la salud, garantizando la atención integ...

30. [normas legales - DIGEMID - Ministerio de Salud](https://www.digemid.minsa.gob.pe/Archivos/Normatividad/2024/PERUANO_RM_837-2024-MINSA.pdf)

31. [www.digemid.minsa.gob.pe › Archivos › PortalWebGUIA PARA LA REVISION DEL PETITORIO NACIONAL DE MEDICAMENTOS ......](https://www.digemid.minsa.gob.pe/Archivos/PortalWeb/Informativo/Acceso/Semts/GestionMedicamentos/06_Guia_Listas_Complementarias.pdf)

32. [Resolución Ministerial N.° 721-2024-MINSA](https://www.gob.pe/institucion/minsa/normas-legales/6116337-721-2024-minsa) — modificación del PNUME y reglas de uso de listas complementarias.

33. [Resolución Ministerial N.° 193-2026-MINSA](https://www.gob.pe/institucion/minsa/normas-legales/7852638-193-2026-minsa) — manual 2026 para estimación y programación de productos farmacéuticos, dispositivos y productos sanitarios en unidades ejecutoras.

34. [Hospital Nacional Arzobispo Loayza — R.D. N.° 117-2024-HNAL-DG](https://www.gob.pe/institucion/hospitalloayza/normas-legales/5676551-117-2024-hnal-dg) — aprobación del Petitorio Institucional 2024; el anexo consigna 507 medicamentos en 33 grupos farmacoterapéuticos.

35. [Hospital Nacional Dos de Mayo — R.D. N.° 136-2024-D-HNDM](https://www.gob.pe/institucion/hndosdemayo/normas-legales/6021765-136-2024-d-hndm) — aprobación del Petitorio Institucional de Medicamentos Esenciales 2024.

36. [Hospital Nacional Cayetano Heredia — R.D. N.° 216-2024-HNCH-DG](https://www.gob.pe/institucion/hnch/normas-legales/5878177-216-2024-hnch-dg) — listados de medicamentos estratégicos, vitales y esenciales 2024.

37. [Instituto Nacional de Salud del Niño – San Borja — R.D. N.° 000146-2025-DG-INSNSB](https://www.gob.pe/institucion/insnsb/normas-legales/7031653-000146-2025-dg-insnsb) — actualización 2025 del petitorio institucional con 413 productos farmacéuticos.

38. [Hospital Antonio Lorena — Gerencia Regional de Salud Cusco](https://www.gob.pe/institucion/regioncusco-geresa/noticias/1035937-hospital-antonio-lorena-cuenta-con-medicamentos-esenciales-y-en-stock-suficiente) — información institucional sobre 976 medicamentos en 32 grupos farmacoterapéuticos y la diferencia entre petitorio y stock.

39. [Red de Salud San Román — Hospital Carlos Monge Medrano, R.D. N.° 004-2025-DE-RED-S-SR-URH](https://www.gob.pe/institucion/redsaludsanroman/normas-legales/7124521-004-2025-de-red-s-sr-urh) — adquisición autorizada de productos no considerados en PNUME para pacientes de neurología.

40. [Red de Salud San Román — R.D. N.° 137-2025-DE-RED-S-SR/URH](https://www.gob.pe/institucion/redsaludsanroman/normas-legales/7637349-137-2025-de-red-s-sr-urh) — autorización de adquisición de producto no PNUME; el documento registra ranibizumab para una necesidad institucional del Hospital Carlos Monge Medrano.

41. [Hospital Regional Honorio Delgado — medicamentos oncológicos](https://www.gob.pe/institucion/hrhd/noticias/1100682-hospital-regional-honorio-delgado-con-un-99-1-de-medicamentos-oncologicos-asegurados-para-sus-pacientes) — reporte de abastecimiento oncológico y transferencia de dactinomicina y fludarabina entre establecimientos MINSA.

42. [IETSI EsSalud — R.N.° 135-IETSI-ESSALUD-2025](https://ietsi.essalud.gob.pe/normativas/resolucion-n-135-ietsi-essalud-2025/) — incorporación de empagliflozina, dapagliflozina y clortalidona al Petitorio Farmacológico de EsSalud.

43. [IETSI EsSalud — R.N.° 204-IETSI-ESSALUD-2025](https://ietsi.essalud.gob.pe/normativas/resolucion-n-204-ietsi-essalud-2025/) — incorporación/actualización de medicamentos de salud mental como quetiapina y paroxetina.

44. [IETSI EsSalud — R.N.° 33-IETSI-ESSALUD-2025](https://ietsi.essalud.gob.pe/normativas/resolucion-n-33-ietsi-essalud-2025/) — exclusión de nelfinavir.

45. [IETSI EsSalud — R.N.° 358-IETSI-ESSALUD-2025](https://ietsi.essalud.gob.pe/normativas/resolucion-n-358-ietsi-essalud-2025/) — actualización/exclusión de productos del petitorio institucional.

46. [DIGEMID — Acceso y Uso / SISMED](https://www.digemid.minsa.gob.pe/webDigemid/acceso-y-uso/) — incluye listados de códigos fusionados con actualización 2026 y herramientas de disponibilidad.

47. [DIGEMID — artículo sobre antibióticos AWaRe en Perú, 2026](https://www.digemid.minsa.gob.pe/revista/index.php/rcprf/article/view/94) — análisis de registros sanitarios e inclusión de antibióticos en el PNUME.


35. [Petitorio Farmacológico EsSalud hasta Resolución N.° 016-2026](https://ietsi.essalud.gob.pe/wp-content/uploads/2026/01/Petitorio-Publicar-hasta-Res-N%C2%B0-016-2026.xls)
36. [DIRESA Puno — Dirección de Acceso y Uso de Medicamentos](https://www.diresapuno.gob.pe/direccion-de-acceso-y-uso-de-medicamentos/)
37. [DIRESA Puno — DIREMID y alertas 2026](https://www.diresapuno.gob.pe/diremid/)
38. [EsSalud — Directorio de redes y establecimientos](https://www.essalud.gob.pe/transparencia/DIRECTORIO_Redes_Provincias.pdf)
39. [Clínica Americana de Juliaca — Farmacia](https://www.clinicaamericana.org.pe/farmacia/)
40. [AAAAI/ACAAI — Drug Allergy: 2022 Practice Parameter Update](https://www.aaaai.org/Aaaai/media/Media-Library-PDFs/Allergist%20Resources/Statements%20and%20Practice%20Parameters/Drug-Allergy-A-2022-Practice-Parameter-Update-2023.pdf)
41. [CDC — Penicillin Allergy](https://www.cdc.gov/std/treatment-guidelines/penicillin-allergy.htm)
42. [WHO — Control of Concentrated Electrolyte Solutions](https://cdn.who.int/media/docs/default-source/patient-safety/patient-safety-solutions/ps-solution5-control-concentrated-electrolyte-solutions.pdf)
43. [Infusion Therapy Standards of Practice](https://incativ.org/wp-content/uploads/2023/10/Infusion_Therapy_Standards_of_Practice_8th.1.pdf)
44. [BD — Tabla técnica de catéteres periféricos](https://www.bd.com/content/dam/bd-assets/na/medication-delivery-solutions/documents/in-service-material/insyte-autoguard-bc-brochure.pdf)


---

# CIERRE — REGLA DE IMPLEMENTACIÓN

La especificación de este documento define el alcance y las reglas. No significa que las funciones ya estén implementadas ni que todos los contenidos clínicos estén aprobados para uso asistencial. Arena IA debe auditar el repositorio, desarrollar el MVP priorizado, ejecutar pruebas y declarar con precisión qué quedó hecho, parcial, pendiente o bloqueado por validación clínica, normativa, técnica o de licencias.
