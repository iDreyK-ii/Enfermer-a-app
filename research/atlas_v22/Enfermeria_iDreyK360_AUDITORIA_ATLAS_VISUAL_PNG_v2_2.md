# ENFERMERÍA iDreyK 360 — AUDITORÍA DE IMPLEMENTACIÓN Y ATLAS VISUAL PNG

**Versión:** 2.2  
**Fecha:** 3 de noviembre de 2026  
**Documento para:** Arena IA / equipo de desarrollo  
**Documento base de conocimiento:** `Enfermeria_iDreyK360_BASE_MAESTRA_v2_1.md`  
**Objetivo:** corregir la calidad de las ilustraciones existentes, convertir el contenido documental en funcionalidades reales de la aplicación y establecer una lista verificable de implementación.

---

## 0. INSTRUCCIÓN PRINCIPAL

Este documento complementa la Base Maestra v2.1. No reemplaza el contenido clínico ni el brief técnico anterior. Debe aplicarse sobre el repositorio actual de Enfermería iDreyK 360.

**No se acepta como final una aplicación que solamente muestre una lista de títulos, pantallas estáticas, imágenes esquemáticas de palitos, ilustraciones robóticas, iconos genéricos o botones sin funcionamiento.** El contenido de la Base Maestra debe convertirse en fichas, buscadores, escalas, herramientas interactivas, casos y atlas visuales utilizables.

Antes de crear o rotular una imagen de instrumental, el desarrollador debe investigar el dispositivo real y comprobar sus componentes con fuentes técnicas. No debe inventar partes ni combinar componentes de modelos incompatibles.

El resultado debe conservar la identidad visual del **Prototipo B**: interfaz limpia, profesional, agradable, adaptada a móvil y tablet, con el objeto clínico como protagonista. Las imágenes deben adaptarse al fondo del aplicativo y destacar por sí solas.

## 1. ESTADO DE CONTROL: NO CONFUNDIR DOCUMENTACIÓN CON IMPLEMENTACIÓN

La Base Maestra v2.1 ya fue preparada y el usuario confirmó que la cargó en Arena IA. Eso demuestra que la documentación fue entregada, pero **no demuestra que las funcionalidades estén programadas o integradas**.

Arena IA debe completar el estado de cada fila con evidencia verificable. No marcar una tarea como terminada solo porque exista una pantalla, un mockup, una tarjeta o un texto de ejemplo.

| Estado | Significado |
|---|---|
| `HECHO — EVIDENCIA` | Funciona en la aplicación y se aportó una prueba reproducible. |
| `PARCIAL` | Existe una parte funcional, pero faltan requisitos o pruebas. |
| `PENDIENTE` | No está implementado. |
| `BLOQUEADO` | Depende de un recurso, permiso, decisión o fuente que debe identificarse. |
| `FUTURO` | No forma parte del MVP; queda documentado para una versión posterior. |

### 1.1. Auditoría inicial obligatoria

- [ ] Confirmar framework, estructura del proyecto, rutas, estado, almacenamiento y sistema de estilos del repositorio actual.
- [ ] Identificar qué módulos ya funcionan realmente y qué módulos son solamente diseño o contenido estático.
- [ ] Revisar dependencias, errores de consola, rutas rotas, controles sin acción y contenido duplicado.
- [ ] Conservar las funcionalidades existentes que estén correctas. No reconstruir ni borrar sin necesidad.
- [ ] Comprobar el Prototipo B en teléfono y tablet, en modo claro y oscuro.
- [ ] Inventariar todas las imágenes actuales y retirar o reemplazar las imágenes de aspecto robótico, de palitos, clipart, diagramas pobres o dibujos que no representen un dispositivo real.
- [ ] Crear una tabla de auditoría con: módulo, estado, archivos/código relacionados, problemas encontrados, corrección aplicada y evidencia.

**Evidencia mínima:** resumen técnico del repositorio y capturas reales de las pantallas auditadas. No se acepta una declaración genérica de “todo listo”.

---

## 2. LISTA MAESTRA DE FUNCIONALIDADES QUE DEBE TENER EL APLICATIVO

### 2.1. Inicio, navegación y biblioteca de conocimiento

- [ ] Inicio funcional con accesos a los módulos principales.
- [ ] Navegación consistente, sin rutas vacías ni controles decorativos que parezcan botones.
- [ ] Biblioteca organizada por áreas, sistemas, procedimientos, fundamentos y especialidades.
- [ ] Buscador global que encuentre títulos, sinónimos, términos técnicos y contenido dentro de las fichas.
- [ ] Filtros por área clínica, población, nivel formativo, tipo de recurso y prioridad.
- [ ] Cada tema abre una ficha completa, legible y estructurada, no únicamente un resumen.
- [ ] Cada contenido indica procedencia: norma peruana, guía institucional, fuente profesional, material académico previo o contenido pendiente de validación.
- [ ] Mostrar fecha de revisión, versión y fuentes consultadas.
- [ ] Marcar claramente el contenido de aprendizaje frente a indicaciones clínicas institucionales.
- [ ] Favoritos, historial o guardado local, si estas funciones forman parte de la arquitectura actual.
- [ ] Búsqueda sin conexión para el contenido que la aplicación almacene localmente, si el alcance técnico lo permite.

### 2.2. Biblioteca de escalas, tests y valoraciones

- [ ] Integrar las escalas identificadas en la Base Maestra, no limitarse a mostrar sus nombres.
- [ ] Ficha de cada escala con objetivo, población, contexto de uso, instrucciones, ítems, puntuación, interpretación, límites, versión y fuente.
- [ ] Formulario de aplicación con controles adecuados al tipo de ítem.
- [ ] Cálculo automático de puntuación cuando la escala permita puntuación cuantitativa.
- [ ] Mostrar desglose de cómo se obtuvo el resultado.
- [ ] Validar valores imposibles, ítems omitidos y combinaciones no permitidas.
- [ ] Permitir guardar el resultado en un caso ficticio o ejercicio, sin almacenar identificadores reales del paciente.
- [ ] No generar automáticamente un diagnóstico médico o diagnóstico NANDA a partir de una puntuación aislada.
- [ ] Advertir cuando una herramienta sea de cribado, apoyo o evaluación profesional y no diagnóstico definitivo.
- [ ] Verificar derechos de autor, permisos y condiciones de uso antes de reproducir cuestionarios completos.

**Prueba:** introducir un caso de prueba conocido en cada escala prioritaria, calcular manualmente y comparar el resultado con la aplicación. Documentar la fuente de la puntuación e interpretación.

### 2.3. Atlas visual de instrumental y dispositivos

- [ ] Crear un catálogo real de instrumental y dispositivos por categorías.
- [ ] Cada instrumento tendrá imagen PNG individual, fondo transparente real y resolución suficiente.
- [ ] La imagen debe representar un dispositivo médico físico realista, no una figura de palitos, un robot, un muñeco, un icono, una ilustración plana o una caricatura.
- [ ] Mostrar el instrumento completo y, cuando sea útil, una ampliación o vista explotada de los componentes.
- [ ] Nombres de las partes en español correcto mediante etiquetas superpuestas por la aplicación.
- [ ] Los puntos interactivos deben abrir el nombre, función y descripción de cada parte.
- [ ] Permitir ampliar la imagen sin pixelación excesiva.
- [ ] En móvil, las etiquetas no deben quedar fuera del panel ni tapar el objeto.
- [ ] Incorporar control de visibilidad de etiquetas o modo “examinar sin nombres” para estudiar.
- [ ] Incluir una opción de autoevaluación: tocar una parte y elegir su nombre/función.
- [ ] Añadir debajo de la imagen función, variantes, riesgos, comprobaciones y fuente del modelo representado.
- [ ] Diferenciar partes universales de elementos opcionales que cambian según fabricante o modelo.

### 2.4. Bioseguridad, higiene de manos y prevención de infecciones

- [ ] Diferenciar claramente lavado clínico, fricción alcohólica y preparación quirúrgica.
- [ ] Incluir los cinco momentos de higiene de manos.
- [ ] Presentar técnica paso a paso con ilustraciones realistas de manos y movimientos anatómicamente correctos.
- [ ] Añadir precauciones estándar, EPP y precauciones basadas en transmisión.
- [ ] Mostrar los diez correctos documentados en la fuente institucional correspondiente sin atribuirlos erróneamente a una norma universal.
- [ ] Mostrar por separado el checklist extendido de seguridad creado para el aprendizaje de la app; no rotularlo como “20 correctos oficiales del MINSA”.
- [ ] Integrar evaluación interactiva de errores frecuentes y prevención de lesiones por punzocortantes.

### 2.5. Medicamentos y farmacología

- [ ] Integrar el inventario farmacológico de la Base Maestra y todos los fármacos recuperados de los casos previos.
- [ ] Agrupar por sistema, familia, indicación clínica y nivel de prioridad.
- [ ] Distinguir medicamento, solución, hemocomponente, producto nutricional y dispositivo.
- [ ] Fichas completas únicamente cuando presentación peruana, ficha técnica, indicación, dosis y seguridad estén verificadas.
- [ ] Estructura de ficha: qué es; presentación en Perú; dosis por indicación/población; indicaciones; contraindicaciones; reacciones adversas; interacciones; preparación/dilución cuando corresponda; administración; vigilancia; cuidados de Enfermería; alertas; fuentes y fecha de revisión.
- [ ] Consultar DIGEMID y fuentes oficiales para registro, ficha técnica y presentación peruana.
- [ ] No inventar concentraciones, marcas, disponibilidad, dosis ni compatibilidades.
- [ ] Medicamentos de alto riesgo con advertencias visuales y barreras adicionales.
- [ ] Mostrar unidades inequívocas (mg, microgramos, mL, mg/mL, unidades, mEq) y evitar abreviaturas peligrosas.
- [ ] No convertir una orden de un caso académico antiguo en recomendación universal.
- [ ] Mantener claramente pendientes los fármacos cuya información peruana aún no esté verificada.

### 2.6. Calculadoras y herramientas matemáticas

- [ ] Dosis por peso.
- [ ] Conversión de unidades.
- [ ] Dilución y concentración final.
- [ ] Velocidad en mL/h.
- [ ] Goteo en gotas/min con factor del equipo introducido y confirmado por el usuario.
- [ ] Tiempo de infusión.
- [ ] Balance hídrico.
- [ ] Cálculo de dosis por superficie corporal, si se aprueba para una fase futura y se valida.
- [ ] Mostrar fórmula, sustitución de datos, resultado y unidades.
- [ ] Validar datos vacíos, cero, negativos, incompatibles o fuera de rango.
- [ ] Mostrar advertencia de doble comprobación para fármacos de alto riesgo.
- [ ] No dar una pauta médica; calcular únicamente con los datos y la prescripción proporcionados.
- [ ] Pruebas automatizadas con resultados calculados independientemente.

### 2.7. PAE y documentación de Enfermería

- [ ] Valoración por los 11 patrones funcionales de Gordon.
- [ ] Registro subjetivo y objetivo separado.
- [ ] Signos vitales y datos clínicos con unidades.
- [ ] Diagnósticos de Enfermería con edición de taxonomía identificada.
- [ ] Relación de datos/evidencias con factores relacionados o factores de riesgo.
- [ ] NOC con indicadores y metas medibles.
- [ ] NIC con actividades individualizadas y fundamento.
- [ ] Priorización clínica razonada.
- [ ] Formato SOAPIE completo y editable.
- [ ] Formato SBAR para comunicación clínica.
- [ ] Guardar/editar/exportar según funciones soportadas por el proyecto.
- [ ] No copiar automáticamente datos de un caso a otro.
- [ ] No inventar valores de valoración faltantes.

### 2.8. Casos clínicos y simulador

- [ ] Integrar los casos recuperados: diabetes/ITU/HTA, TEC/politrauma/UCI, HDA, pancreatitis, neumonía y casos de sepsis, asma, HTA, arritmias y adulto mayor.
- [ ] Distinguir caso real anonimizado, caso académico previo y caso ficticio de simulación.
- [ ] Permitir revisar la valoración y detectar datos faltantes o inconsistencias.
- [ ] Presentar decisiones clínicas por etapas, con retroalimentación y justificación.
- [ ] Enseñar priorización ABCDE cuando aplique y razonamiento de deterioro clínico.
- [ ] No revelar la respuesta antes de que el estudiante tome una decisión en modo evaluación.
- [ ] Ofrecer modo estudio y modo examen.
- [ ] No usar nombres, DNI, historias clínicas ni datos identificables reales.

### 2.9. Emergencias y coche de paro

- [ ] Ficha visual del coche de paro por compartimentos y equipos.
- [ ] Inventario configurable por institución y servicio; nunca asumir una lista universal.
- [ ] Separar fármacos de dispositivos, consumibles y soluciones.
- [ ] Control de caducidad, cantidad, sello, funcionamiento y reposición, si se implementa inventario.
- [ ] Integrar algoritmos de soporte vital solo después de validar guías vigentes y población a la que aplican.
- [ ] Añadir desfibrilador, monitor, bolsa-válvula-mascarilla, aspirador y equipo de vía aérea al atlas.

### 2.10. Experiencia visual, accesibilidad y calidad

- [ ] Respetar Prototipo B en navegación, cards, tipografía, espaciado, iconografía y colores.
- [ ] Imágenes realistas y no excesivamente ornamentadas.
- [ ] Diseñar para pantalla vertical y horizontal de tablet y teléfono.
- [ ] Modo claro/oscuro; probar transparencia PNG en ambos.
- [ ] No usar fondos sólidos incrustados en las imágenes.
- [ ] Animaciones ligeras, desactivables y adaptadas al rendimiento del dispositivo.
- [ ] Tamaño táctil adecuado, contraste, textos legibles y navegación accesible.
- [ ] Optimizar peso de PNG sin destruir detalles.
- [ ] Añadir texto alternativo y descripción accesible de cada imagen.

---

## 3. ESTÁNDAR OBLIGATORIO PARA CADA IMAGEN PNG

### 3.1. Formato técnico

- Archivo individual `.png` en RGBA, con canal alfa real.
- Fondo 100 % transparente: no simular transparencia con cuadros grises/blancos.
- Resolución de producción recomendada: lado mayor de 2000–3000 px, proporcional al objeto.
- Sin texto, letras, números, nombres, marcas de agua, logotipos o etiquetas incrustadas.
- Sin marco, tarjeta, fondo de color, mesa, habitación ni escena hospitalaria, salvo una solicitud concreta.
- Objeto entero dentro del lienzo, sin recortar extremos, conectores, tubos, asas ni accesorios esenciales.
- Iluminación de fotografía de producto; materiales, brillos, transparencias, escalas y texturas realistas.
- Vista en tres cuartos o vista técnica adecuada a cada objeto, con detalles visibles.
- Una sola unidad del dispositivo, salvo que el prompt solicite comparación de dos variantes.
- Sombra de contacto muy sutil, solo si conserva el canal alfa y no crea un halo opaco.
- Exportar PNG real. No entregar SVG, CSS, emoji, icono ni un dibujo como sustituto del PNG.

### 3.2. Estilo visual prohibido

No generar:

- Dibujos de palitos, anatomía simplificada, figuras robóticas o humanoides.
- Clipart, iconos, caricaturas, estilo kawaii del objeto, anime o cartoon.
- Render 3D plástico, low-poly, isométrico, futurista o de videojuego.
- Esquemas lineales planos como imagen principal del instrumento.
- Imágenes genéricas que no representen la forma del dispositivo real.
- Texto generado por IA dentro de la imagen.
- Partes inventadas, duplicadas, deformadas o fusionadas.
- Piezas de un modelo conectadas a piezas incompatibles de otro modelo.

### 3.3. Requisito de investigación antes de generar

Para cada dispositivo, Arena IA debe:

1. Identificar el nombre técnico y las variantes principales usadas en Enfermería.
2. Consultar al menos una fuente primaria del fabricante (ficha de producto, catálogo técnico o instrucciones de uso/IFU) para el modelo de referencia.
3. Contrastar la nomenclatura con una segunda fuente técnica fiable (otro fabricante, norma aplicable, manual de formación sanitaria o fuente institucional).
4. Crear una lista de componentes y marcar cuáles son universales, opcionales o dependientes del modelo.
5. Seleccionar un modelo visual genérico realista o declarar el fabricante/modelo de referencia internamente; evitar logos y marcas en la ilustración final.
6. Generar el PNG con el prompt individual que aparece más abajo.
7. Revisar el resultado contra la referencia técnica y corregirlo antes de integrar.
8. Crear un archivo de metadatos `.json` por imagen con: `asset_id`, `file`, `display_name_es`, `technical_name`, `view`, `parts[]`, `part_status` (universal/opcional/modelo), `hotspot_x`, `hotspot_y`, `part_description`, `function`, `source_urls[]`, `review_date`, `review_status`.
9. Añadir etiquetas como texto y hotspots desde la aplicación. **No usar el texto generado dentro del PNG como fuente de verdad.**
10. Registrar el estado de revisión técnica y la persona que aprobó la precisión.

### 3.4. Cómo rotular las partes en la aplicación

La imagen PNG es el recurso visual limpio. Los nombres se dibujan por encima usando componentes de interfaz: puntos interactivos, líneas de llamada discretas y panel de descripción. Esto permite traducción, corrección y adaptación a móvil sin regenerar la imagen.

Cada punto interactivo debe usar coordenadas normalizadas entre 0 y 1 relativas al ancho y alto de la imagen. En tablet se pueden mostrar varias etiquetas; en móvil, el usuario toca el punto para abrir el nombre y la descripción en un panel inferior.

---

## 4. PROMPTS INDIVIDUALES PARA CREAR LAS IMÁGENES PNG

**Regla común para todos los prompts:** antes de generar, investigar el dispositivo y sus partes con las fuentes técnicas descritas en la sección 3. No asumir que todos los modelos incluyen todas las piezas. Generar una imagen de fotografía de producto realista, sin texto ni etiquetas, con transparencia alfa real.

### PNG-01 — Estetoscopio clínico adulto

**Archivo:** `atlas_estetoscopio_adulto.png`  
**Partes que la app debe poder señalar:** olivas/auriculares; tubos binaurales; resorte o unión de tensión; tubuladura flexible; vástago/selector cuando corresponda; pieza torácica; membrana/diafragma; campana o lado pequeño cuando el modelo la incluya; aro/retén de la membrana. La configuración de una o dos caras cambia entre modelos.

**Prompt:** “Fotografía de producto médico hiperrealista de un estetoscopio clínico adulto de doble cara, genérico y sin marca. Representar el instrumento completo con anatomía de producto real: dos olivas suaves, tubos binaurales metálicos curvos, unión/resorte, tubuladura flexible continua y pieza torácica metálica con diafragma visible y campana en el reverso mediante un pequeño detalle ampliado separado pero conectado visualmente al mismo instrumento. Mostrar la conexión del vástago con la tubuladura. Materiales auténticos: metal satinado, PVC/silicona mate y membrana translúcida. Vista tres cuartos, composición ordenada, máxima nitidez de cada unión. Fotografía real de un instrumento físico sobre fondo transparente alfa. Sin texto, números, flechas, líneas, logotipos, manos, personas, fondo, tarjeta ni estética de dibujo, robot o render CGI.”

### PNG-02 — Jeringa hipodérmica estándar Luer Lock

**Archivo:** `atlas_jeringa_luer_lock.png`  
**Partes:** cilindro/cuerpo; escala graduada sin cifras legibles inventadas; aletas/reborde; vástago del émbolo; apoyo del pulgar; pistón/sello elastomérico; punta Luer Lock roscada; aguja desmontable solo si se muestra como accesorio separado. No confundir el vástago con el sello.

**Prompt:** “Fotografía macro de producto de una jeringa hipodérmica desechable transparente tipo Luer Lock, sin marca, con el cuerpo cilíndrico, escala moldeada realista, aletas laterales, vástago del émbolo, apoyo del pulgar y pistón/sello de goma claramente distinguibles. Mostrar una vista principal ensamblada y un pequeño despiece ordenado a un lado que revele el émbolo y el sello sin deformar su geometría. La punta debe ser realmente Luer Lock roscada, sin inventar una aguja integrada. Plástico médico transparente con graduaciones discretas, goma y piezas de polipropileno realistas. Fotografía clínica de producto, fondo transparente alfa. Sin textos, números legibles, etiquetas, flechas, marcas, sangre, manos, pacientes, dibujo lineal o render 3D plástico.”

### PNG-03 — Aguja hipodérmica desechable

**Archivo:** `atlas_aguja_hipodermica.png`  
**Partes:** pabellón/hub; cánula metálica; lumen (solo mediante pequeño corte técnico realista si se necesita); bisel distal; protector. El color del hub depende del calibre/estándar y no debe utilizarse como único identificador.

**Prompt:** “Fotografía macro hiperrealista de una aguja hipodérmica estéril de un solo uso, genérica y sin marca, colocada en diagonal para que se distingan el pabellón plástico, la cánula metálica fina, la punta biselada y el protector transparente separado al lado. Añadir una ampliación fotográfica de la punta con el bisel y el orificio visibles, sin exagerar el lumen ni crear piezas irreales. No imponer un color de pabellón como regla universal; utilizar acabado neutro o un color de ejemplo y dejar el calibre como metadato. Fondo transparente alfa, enfoque nítido, materiales reales. Sin texto, medidas, números, flechas, manos, sangre ni estilo esquemático.”

### PNG-04 — Catéter venoso periférico corto de seguridad (bránula)

**Archivo:** `atlas_branula_cateter_periferico.png`  
**Partes:** catéter flexible; aguja introductora con bisel; cámara de retorno de sangre; hub/conector; alas de estabilización si el modelo las tiene; puerto de inyección si el modelo lo tiene; mecanismo/escudo de seguridad si corresponde; tapa protectora. No presentar un catéter cerrado sin puerto como si tuviera puerto lateral.

**Prompt:** “Fotografía macro de producto médico hiperrealista de un catéter venoso periférico corto de seguridad, genérico, con configuración basada en una ficha técnica real de un modelo con alas y sistema de protección de aguja. Mostrar el catéter flexible translúcido separado solo unos milímetros de la aguja introductora para hacer visible que son piezas distintas; destacar cámara de retorno, hub, alas, protector de aguja y tapa. Si el modelo de referencia no tiene puerto lateral, no añadirlo. Añadir un recuadro de detalle fotográfico del bisel y del catéter sobre la aguja, sin corte anatómico del paciente. Fondo transparente alfa; materiales médicos auténticos y escala realista. Sin texto, etiquetas, números, marcas, sangre, piel, venas, manos, dibujo ni render CGI.”

### PNG-05 — Equipo de venoclisis por gravedad

**Archivo:** `atlas_equipo_venoclisis.png`  
**Partes posibles según el modelo:** protector del punzón; punzón; toma de aire con filtro si es ventilado; cámara de goteo; filtro de líquido si está presente; tubuladura; pinza de rodillo/regulador; puerto de inyección en Y si existe; conector distal Luer; tapa distal. El factor de goteo debe estar rotulado en metadatos según envase, no inferido por la imagen.

**Prompt:** “Fotografía de producto médico hiperrealista de un equipo de administración intravenosa por gravedad completo y extendido verticalmente, sin bolsa de solución ni paciente. Que el recorrido físico sea continuo y correcto: punzón con protector en el extremo superior, entrada de aire filtrada solo si es modelo ventilado, cámara de goteo transparente, tubuladura flexible, regulador de rodillo, puerto lateral en Y solo si la referencia lo incluye y conector Luer Lock distal con tapa. Mostrar la cámara de goteo con claridad y una gota pequeña dentro, sin exagerarla. No inventar filtro ni puerto. Fondo transparente alfa, plástico médico real, fotografía de catálogo. Sin texto, factor de goteo, números, logotipos, flechas, fondo, clipart ni esquema de palitos.”

### PNG-06 — Equipo de infusión con bureta graduada (Volutrol)

**Archivo:** `atlas_equipo_bureta_volutrol.png`  
**Partes:** punzón; entrada de aire filtrada; cámara graduada rígida; puerto de adición; válvula de cierre; tubo de goteo; cámara de goteo; filtro si está incluido; pinzas; tubuladura y conector distal. Las configuraciones varían entre fabricante.

**Prompt:** “Fotografía clínica de producto hiperrealista de un equipo de infusión con bureta graduada tipo Volutrol, genérico y sin marca, extendido verticalmente y completo. Mostrar claramente la cámara rígida graduada, su tapa/entrada de aire filtrada, puerto de adición cuando esté presente, conexión superior desde el contenedor, válvula de cierre de bureta, tubo de goteo, cámara de goteo inferior, tubuladura y conector Luer distal. La escala de la bureta debe ser visualmente graduada pero sin números falsos legibles. Seguir el diseño del modelo de referencia y no añadir filtros o puertos opcionales si no existen. Fondo transparente alfa real, fotografía de producto de alta resolución. Sin texto, flechas, marcas, logos, manos, pacientes, render 3D ni dibujo plano.”

### PNG-07 — Llave de tres vías

**Archivo:** `atlas_llave_tres_vias.png`  
**Partes:** cuerpo; tres puertos Luer; manija giratoria; canal interno representado mediante un pequeño corte transversal realista; tapas protectoras. La orientación de apertura/cierre debe verificarse en las instrucciones del modelo concreto.

**Prompt:** “Fotografía macro hiperrealista de una llave de paso médica de tres vías, transparente y sin marca. Mostrar el cuerpo con sus tres puertos Luer, manija giratoria claramente diferenciada, tapas protectoras y un pequeño detalle ampliado de la trayectoria interna del canal en una posición concreta. El canal debe corresponder al diseño físico real de la referencia, no asumir que la manija apunta siempre al puerto cerrado. Composición aislada, material plástico médico translúcido, fondo transparente alfa. Sin texto, letras, números, flechas, símbolos inventados, manos, paciente, caricatura o dibujo.”

### PNG-08 — Tensiómetro aneroide manual

**Archivo:** `atlas_tensiometro_aneroide.png`  
**Partes:** brazalete; cámara inflable interna representada en un pequeño despiece; tubos; pera de insuflación; válvula de liberación; manómetro circular y aguja. No incluir un estetoscopio en esta imagen.

**Prompt:** “Fotografía de producto hiperrealista de un esfigmomanómetro aneroide manual para adulto, genérico y sin marca. Mostrar el brazalete textil con cierre, el tubo, la pera de insuflación, válvula de liberación gradual y manómetro circular aneroide con escala auténtica sin valores clínicos inventados. Añadir un pequeño despiece que permita ver la cámara inflable interior del brazalete separada de la cubierta textil. Mantener conexiones físicas correctas y proporciones realistas. Fondo transparente alfa; luz de estudio; sin brazo humano, sin estetoscopio, sin texto añadido, sin etiquetas ni estilo clipart.”

### PNG-09 — Pulsioxímetro de dedo

**Archivo:** `atlas_pulsioximetro_dedo.png`  
**Partes:** cuerpo del dispositivo; pantalla; botón/controles; bisagra; pinza; emisor y detector óptico dentro de las mordazas (detalle ampliado). No mostrar pantalla con números o SpO₂ como si fueran una lectura real.

**Prompt:** “Fotografía macro realista de un pulsioxímetro portátil de dedo, genérico y sin marca, mostrado abierto en tres cuartos. Mostrar cuerpo, pantalla apagada o con interfaz neutra no legible, botón de control, bisagra y mordazas acolchadas. Añadir un pequeño detalle técnico del emisor de luz y detector óptico enfrentados dentro de la pinza, sin dedo ni paciente. Plástico médico realista, fondo transparente alfa, sin cifras, sin texto, sin logo, sin iconos flotantes, sin diagrama de palitos.”

### PNG-10 — Bomba de infusión volumétrica

**Archivo:** `atlas_bomba_volumetrica.png`  
**Partes que dependen del modelo:** pantalla; teclas/mandos; puerta y mecanismo de carga; canal de tubuladura; pinza anti-flujo libre; detector de aire; detector de oclusión; asa; indicadores de energía/batería y alarmas. La ubicación exacta debe corresponder al fabricante de referencia.

**Prompt:** “Fotografía de producto frontal en tres cuartos de una bomba de infusión volumétrica hospitalaria realista, genérica y sin marca, no un render futurista. Mostrar carcasa, pantalla con interfaz neutra no legible, teclado o controles físicos, asa superior si corresponde, puerta frontal abierta ligeramente para revelar el canal de la línea de infusión, zona de bombeo y sensores de la tubuladura. Mostrar el recorrido del equipo de administración en su canal, pero sin conectarlo a un paciente. Añadir una vista pequeña de la puerta cerrada. Fondo transparente alfa, materiales y botones físicos precisos. Sin valores programados, texto, etiquetas, logos ni estética sci-fi.”

### PNG-11 — Bomba de jeringa

**Archivo:** `atlas_bomba_jeringa.png`  
**Partes según fabricante:** pantalla; controles; soporte/cuna de jeringa; abrazadera del cilindro; empujador del émbolo; mecanismo de avance; sensor de tamaño/colocación; asa y alimentación. La jeringa debe estar correctamente fijada.

**Prompt:** “Fotografía de producto de una bomba de jeringa de uso hospitalario, genérica y sin marca, en vista tres cuartos. Mostrar una jeringa real correctamente colocada en su cuna, fijación del cuerpo de la jeringa, abrazadera del cilindro, empujador acoplado al apoyo del émbolo, pantalla neutra, controles físicos y carcasa. Añadir una pequeña vista del soporte abierto para reconocer los puntos de fijación. No mostrar una jeringa flotando ni el empujador fuera de eje. Fondo transparente alfa, fotografía de catálogo clínico. Sin números programados, texto, logos, paciente, caricatura ni render CGI.”

### PNG-12 — Monitor multiparámetro de cabecera

**Archivo:** `atlas_monitor_multiparametro.png`  
**Elementos:** pantalla; controles; indicador/alarma; cable ECG y derivaciones; sensor SpO₂; manguito PANI con tubo; sensor de temperatura; módulos de presión invasiva si el modelo los tiene. Los accesorios deben conectarse a sus puertos reales y no mostrar cifras de paciente identificables.

**Prompt:** “Fotografía realista de producto de un monitor multiparámetro hospitalario de cabecera, genérico y sin marca, en vista frontal tres cuartos, con pantalla limpia y ondas fisiológicas de demostración no diagnósticas. Mostrar carcasa, pantalla, controles, barra luminosa de alarma y conectores laterales. Al lado, acomodar ordenadamente los accesorios compatibles con sus cables: cable ECG con derivaciones, sensor de pulsioximetría, manguito de presión no invasiva y sensor de temperatura. Incluir canal de presión invasiva solo si la configuración de referencia lo admite. No inventar puertos. Fondo transparente alfa, sin persona, sin datos reales, sin números de parámetros, sin texto ni estilo futurista.”

### PNG-13 — Cánula nasal de oxígeno

**Archivo:** `atlas_canula_nasal.png`  
**Partes:** dos puntas nasales; puente curvo; tubuladura; regulador deslizante/ajuste; conectores. No incluir una cara ni fijación a paciente.

**Prompt:** “Fotografía de producto hiperrealista de una cánula nasal de oxígeno adulta, transparente o verde translúcida según un producto genérico real, totalmente extendida y sin paciente. Mostrar dos puntas nasales curvas orientadas correctamente, puente, tubuladura flexible, pieza deslizante de ajuste y conectores. Fondo transparente alfa, enfoque claro en geometría y flexibilidad del tubo. Sin rostro, sin manos, sin flujo dibujado, sin texto, etiquetas o símbolos.”

### PNG-14 — Mascarilla simple de oxígeno

**Archivo:** `atlas_mascarilla_simple_oxigeno.png`  
**Partes:** cuerpo de mascarilla; almohadilla/contorno nasal; orificios laterales de exhalación; conector inferior; tubo de oxígeno; banda elástica. Debe diferenciarse de Venturi y de no reinhalación.

**Prompt:** “Fotografía macro de producto de una mascarilla simple de oxígeno adulta, genérica y sin marca, sin rostro ni modelo humano. Mostrar la máscara transparente moldeada, borde nasal, orificios laterales de exhalación, conector inferior, tubo de oxígeno y banda elástica colocada de manera realista. No incluir bolsa reservorio ni adaptador Venturi. Fondo transparente alfa, iluminación suave de estudio, material médico translúcido. Sin texto, flechas, marca o dibujos.”

### PNG-15 — Mascarilla con reservorio no reinhalación

**Archivo:** `atlas_mascarilla_no_reinhalacion.png`  
**Partes:** máscara; orificios laterales con válvulas unidireccionales según el modelo; bolsa reservorio; entrada de oxígeno; tubuladura y banda elástica. No representar la bolsa rígida o completamente colapsada.

**Prompt:** “Fotografía de producto médico hiperrealista de una mascarilla de oxígeno con bolsa reservorio de no reinhalación, genérica y sin marca. Mostrar la máscara facial transparente, orificios laterales con válvulas unidireccionales, bolsa reservorio conectada a la entrada inferior, tubo de oxígeno y banda elástica. La bolsa debe estar parcialmente expandida y con forma flexible realista; no agregar un adaptador Venturi. Fondo transparente alfa, sin rostro, sin paciente, sin texto, sin flechas ni diagramas planos.”

### PNG-16 — Mascarilla Venturi

**Archivo:** `atlas_mascarilla_venturi.png`  
**Partes:** mascarilla; tubo corrugado; adaptador/jet Venturi; entrada de oxígeno; puertos de arrastre de aire y conectores. Los adaptadores y colores cambian por marca/porcentaje; no rotular FiO₂ sin fuente del modelo.

**Prompt:** “Fotografía clínica hiperrealista de un sistema de mascarilla Venturi para oxigenoterapia, genérico, aislado y sin paciente. Mostrar máscara facial transparente, tubo corrugado, adaptador Venturi intercambiable claramente acoplado y entrada de oxígeno. El adaptador debe tener geometría auténtica y puertos de arrastre de aire; no inventar colores ni porcentajes. Si se muestran adaptadores intercambiables, colocarlos como accesorios separados y sin valores impresos legibles. Fondo transparente alfa. Sin rostro, etiquetas, texto, flechas ni render futurista.”

### PNG-17 — Bolsa-válvula-mascarilla (BVM / resucitador manual)

**Archivo:** `atlas_bolsa_valvula_mascarilla.png`  
**Partes que pueden variar:** mascarilla facial; válvula del paciente/no reinhalación; bolsa autoinflable; válvula de entrada; entrada de oxígeno; reservorio de oxígeno o tubo corrugado; válvula limitadora de presión en ciertos modelos; puerto PEEP opcional. No presentar los accesorios opcionales como universales.

**Prompt:** “Fotografía de producto hiperrealista de un resucitador manual bolsa-válvula-mascarilla adulto, genérico y sin marca, completamente ensamblado y con accesorios principales separados ordenadamente en pequeño detalle lateral. Mostrar mascarilla, válvula del paciente, bolsa autoinflable, válvula de entrada, conexión a oxígeno y bolsa reservorio o tubo de reservorio según el diseño de referencia. Incluir válvula limitadora de presión solo si el modelo concreto la tiene; válvula PEEP solo como accesorio opcional separado si corresponde. Las conexiones deben ser físicamente coherentes. Fondo transparente alfa, sin paciente, sin manos, sin texto ni esquema de palitos.”

### PNG-18 — Sonda Foley de dos vías

**Archivo:** `atlas_sonda_foley_dos_vias.png`  
**Partes:** punta distal redondeada; ojos/orificios de drenaje; balón de retención; cuerpo/tubo; embudo de drenaje; puerto de inflado con válvula antirretorno. Mostrar un detalle ampliado de balón inflado y desinflado como dos estados del mismo diseño, no dos balones en un solo catéter.

**Prompt:** “Fotografía macro realista de producto de una sonda vesical Foley de dos vías, sin marca y sin paciente. Mostrar el tubo flexible, punta distal redondeada, ojos laterales de drenaje, balón de retención cercano a la punta, puerto separado de inflado con válvula antirretorno y embudo proximal de drenaje. Añadir dos pequeños detalles comparativos del balón desinflado e inflado correspondientes al mismo modelo, sin alterar la anatomía de la sonda principal. Material de silicona/látex médico según modelo de referencia. Fondo transparente alfa. Sin texto, logos, vejiga, cuerpo humano ni dibujo esquemático.”

### PNG-19 — Sonda Foley de tres vías

**Archivo:** `atlas_sonda_foley_tres_vias.png`  
**Partes:** punta distal y ojos; balón; tubo; puerto de drenaje; puerto de inflado con válvula; puerto adicional de irrigación. Debe verse claramente que es una variante de tres vías y no una Foley común con una llave añadida al azar.

**Prompt:** “Fotografía de producto hiperrealista de una sonda vesical Foley de tres vías para irrigación, genérica y sin marca. Mostrar punta redondeada, ojos laterales, balón de retención, tubo flexible y tres conexiones proximales claramente diferenciadas: drenaje urinario, inflado del balón con válvula antirretorno e irrigación vesical. Las tres vías deben conectarse a los canales anatómicos correctos del catéter. Mostrar un detalle de la punta y balón. Fondo transparente alfa, sin paciente, sin vejiga, sin texto, sin flechas ni piezas inventadas.”

### PNG-20 — Sonda nasogástrica de alimentación/drenaje

**Archivo:** `atlas_sonda_nasogastrica.png`  
**Partes según modelo:** conector proximal y tapa; tubo flexible; marcas de longitud; línea radiopaca; orificios laterales distales; punta distal; guía/estilete solo si el modelo específico lo incluye. Diferenciar sonda gástrica simple de sonda de doble lumen.

**Prompt:** “Fotografía de producto médico hiperrealista de una sonda nasogástrica adulta genérica, sin marca, extendida con curvatura suave y sin paciente. Mostrar conector proximal, tapa o puerto, tubo flexible, marcas de profundidad discretas no legibles, línea radiopaca visible como franja interna y punta distal con orificios laterales de salida. Si la referencia es de doble lumen, representar ambos canales y sus conexiones correctamente; si no, no añadir un segundo puerto. No incluir guía metálica salvo que el modelo de referencia la tenga. Fondo transparente alfa, sin rostro, sin cuerpo, sin texto ni dibujos planos.”

### PNG-21 — Tubo endotraqueal con balón

**Archivo:** `atlas_tubo_endotraqueal_con_balón.png`  
**Partes:** conector proximal de 15 mm; tubo con marcas de profundidad; balón/cuff distal; línea de inflado; balón piloto; válvula de inflado; ojo de Murphy cuando el modelo lo incorpora; bisel distal. No añadir un estilete como parte integrada.

**Prompt:** “Fotografía de producto macro hiperrealista de un tubo endotraqueal adulto con balón de baja presión, genérico y sin marca, curvado en una forma realista. Mostrar conector proximal estándar, cuerpo del tubo transparente, marcas de profundidad discretas, línea de inflado externa, balón piloto con válvula, cuff distal y bisel. Incluir ojo de Murphy si la referencia del modelo lo incluye, sin inventarlo. Un detalle ampliado debe mostrar el cuff desinflado y el ojo distal. Fondo transparente alfa, sin paciente, sin laringe, sin texto, flechas, logotipo o estilo de dibujo.”

### PNG-22 — Cánula de traqueostomía con cuff

**Archivo:** `atlas_canula_traqueostomia_con_cuff.png`  
**Partes según versión:** cánula externa; cánula interna removible; placa/alas de cuello; conector de 15 mm; cuff si es cuffed; línea de inflado y balón piloto; obturador como accesorio separado; cintas de fijación si forman parte del set. No mostrar cuff en la versión sin balón.

**Prompt:** “Fotografía de producto hiperrealista de un set de cánula de traqueostomía adulta con cuff, genérico y sin marca, aislado y sin cuello humano. Mostrar cánula externa curva, cánula interna removible separada ligeramente, placa cervical con orificios de fijación, conector proximal de 15 mm, balón/cuff distal, línea de inflado y balón piloto. Colocar el obturador como accesorio separado, no insertado como si fuera permanente. Todas las piezas deben corresponder a una misma familia de dispositivo. Fondo transparente alfa, sin cuerpo humano, sin texto, logos, etiquetas ni render futurista.”

### PNG-23 — Catéter venoso central de tres lúmenes

**Archivo:** `atlas_cateter_venoso_central_triple_lumen.png`  
**Partes:** cuerpo del catéter; punta distal; tres extensiones/lúmenes; clamps; hubs/conectores; marcas de profundidad; aletas de fijación si el modelo las tiene. No añadir guía, dilatador ni aguja como si fueran parte integral del catéter final; pueden aparecer en una imagen futura de set de inserción.

**Prompt:** “Fotografía de producto hiperrealista de un catéter venoso central de tres lúmenes, genérico y sin marca, extendido sobre fondo transparente. Mostrar un cuerpo de catéter flexible con punta distal, tres extensiones diferenciadas y conectores proximales con clamps individuales; incluir hubs, marcas de profundidad discretas y aletas de sutura/estabilización si existen en el modelo. Los tres canales deben corresponder a un solo dispositivo coherente. No representar la guía, aguja, bisturí o dilatador como partes permanentes del catéter. Sin paciente, sin anatomía humana, sin texto o números legibles, fondo alfa transparente y fotografía realista.”

### PNG-24 — Desfibrilador/monitor hospitalario

**Archivo:** `atlas_desfibrilador_monitor.png`  
**Partes:** pantalla; controles; selector de energía o controles de terapia según modelo; botón de carga/descarga cuando corresponda; palas externas o parches adhesivos; cable de ECG; compartimento/impresora si existe. Diferenciar desfibrilador manual de DEA.

**Prompt:** “Fotografía profesional de producto de un desfibrilador-monitor hospitalario manual, genérico y sin marca, en vista tres cuartos. Mostrar pantalla, controles físicos de monitorización/terapia, mandos de energía si el modelo los tiene, controles de carga y descarga, cable ECG y palas externas o parches adhesivos compatibles colocados al lado. No mezclar controles de DEA con mandos de desfibrilador manual de forma incorrecta. Mostrar puertos y accesorios en ubicaciones verosímiles. Pantalla sin datos reales ni valores legibles. Fondo transparente alfa, fotografía de equipo hospitalario real, sin paciente, sin texto, sin rayos, sin chispas, sin estética de videojuego.”

---

## 5. IMÁGENES COMPLEMENTARIAS PARA VERSIONES FUTURAS

Marcar como `FUTURO`, sin bloquear el MVP:

- [ ] Catéter PICC (diferenciar de CVC yugular/subclavio y de midline).
- [ ] Puerto venoso implantable y aguja Huber.
- [ ] Catéter de hemodiálisis de dos lúmenes.
- [ ] Drenaje Jackson-Pratt y Hemovac.
- [ ] Drenaje torácico y sistema de sello de agua.
- [ ] Aspirador de secreciones y frasco recolector.
- [ ] Nebulizador y espaciador para inhalador.
- [ ] Ventilador mecánico con circuito respiratorio.
- [ ] Equipo de curación con instrumental real.
- [ ] Bolsa de ostomía y componentes de cuidado de estoma.
- [ ] Equipo de transfusión con filtro para hemocomponentes.
- [ ] Equipo de protección personal: guantes, mascarilla, respirador, protector ocular, bata y mandil.

Cada imagen futura requiere su propio prompt, referencia técnica, revisión y PNG alfa individual. No crear una lámina con todos los objetos pegados.

---

## 6. ESTRUCTURA DE ARCHIVOS Y VINCULACIÓN CON LA APLICACIÓN

Guardar los recursos de la siguiente manera (adaptar al proyecto actual sin romper su arquitectura):

```text
/public/assets/atlas/
  estetoscopio/
    atlas_estetoscopio_adulto.png
    atlas_estetoscopio_adulto.json
    source_notes.md
  jeringa/
    atlas_jeringa_luer_lock.png
    atlas_jeringa_luer_lock.json
    source_notes.md
  branula/
    atlas_branula_cateter_periferico.png
    atlas_branula_cateter_periferico.json
    source_notes.md
  ...
```

Cada ficha de imagen debe estar vinculada con un registro de contenido que tenga como mínimo:

```json
{
  "asset_id": "atlas_estetoscopio_adulto",
  "file": "/assets/atlas/estetoscopio/atlas_estetoscopio_adulto.png",
  "display_name_es": "Estetoscopio clínico adulto",
  "technical_name": "Estetoscopio acústico de doble cara",
  "parts": [
    {
      "part_id": "olivas",
      "name_es": "Olivas o auriculares",
      "status": "universal_or_model_dependent",
      "hotspot_x": 0.25,
      "hotspot_y": 0.13,
      "function": "Sellan la entrada del oído y transmiten el sonido a través de los tubos binaurales.",
      "source_urls": ["https://multimedia.3m.com/mws/media/1346109O/littmann-anatomy-of-a-stethoscope-poster-pdf.pdf"]
    }
  ],
  "review_date": "2026-11-03",
  "review_status": "pending_clinical_review"
}
```

Los datos de ejemplo son ilustrativos. Las coordenadas reales deben calcularse para cada PNG generado; no copiar coordenadas de otro instrumento.

### Reglas de integración

- [ ] Cargar la imagen correcta desde el manifiesto de assets; no usar una sola imagen genérica en todas las tarjetas.
- [ ] Comprobar que el PNG conserva transparencia en fondos claro y oscuro.
- [ ] Ajustar `object-fit: contain` o su equivalente para no cortar el dispositivo.
- [ ] Mostrar todas las partes en modo ficha y permitir activar/desactivar etiquetas.
- [ ] En el modo móvil, mostrar una etiqueta por vez al tocar un hotspot.
- [ ] Si falta una imagen, mostrar una tarjeta de “recurso pendiente” claramente identificada, nunca sustituir por un dibujo robótico.
- [ ] Registrar créditos/fuente de referencia y licencia cuando se use fotografía externa. La imagen generada debe ser original y no replicar exactamente una foto protegida.
- [ ] Mantener nombres técnicos y alternativos para que el buscador encuentre “bránula”, “catéter periférico corto” y “catéter IV periférico”.

---

## 7. PRUEBAS DE ACEPTACIÓN DEL ATLAS VISUAL

- [ ] Todas las imágenes MVP tienen archivo PNG real y canal alfa validado.
- [ ] No hay fondo blanco/cuadriculado incrustado.
- [ ] No hay partes truncadas o desconectadas.
- [ ] Cada componente señalado existe físicamente en el modelo de referencia.
- [ ] Partes opcionales están marcadas como opcionales/modelo-dependientes.
- [ ] Ninguna flecha o nombre queda dibujado por IA dentro del PNG.
- [ ] Hotspots de todas las partes abren el nombre y su función correcta.
- [ ] Al cambiar de modo claro a oscuro, la imagen se integra sin un recuadro rectangular.
- [ ] Al cambiar de tablet a teléfono, la imagen no queda cortada ni sus etiquetas se superponen.
- [ ] El usuario puede ampliar y volver a la vista general.
- [ ] Existe una mini evaluación de identificación de partes para al menos los primeros instrumentos.
- [ ] Cada imagen tiene metadatos y fuentes revisadas.

**Criterio de rechazo inmediato:** una imagen con aspecto de muñeco/robot, dibujos de palitos, estilo cartoon, piezas inventadas, texto ilegible incrustado o fondo falso no pasa a producción.

---

## 8. ENTREGA QUE ARENA IA DEBE DEVOLVER

Arena IA debe entregar un informe final con estas columnas:

| N.º | Funcionalidad/imagen | Estado | Evidencia concreta | Pendiente o incidencia |
|---:|---|---|---|---|
| 1 | Biblioteca de conocimiento | Pendiente de auditoría | Ruta, captura y prueba de búsqueda | |
| 2 | Escalas interactivas | Pendiente de auditoría | Escalas funcionales probadas y resultado validado | |
| 3 | Bioseguridad | Pendiente de auditoría | Pantallas y simulación ejecutable | |
| 4 | Calculadoras | Pendiente de auditoría | Fórmulas y pruebas conocidas | |
| 5 | PAE/SOAPIE/SBAR | Pendiente de auditoría | Flujo completo probado | |
| 6 | Farmacología | Pendiente de auditoría | Registros, fuentes y fichas validadas | |
| 7 | Casos clínicos | Pendiente de auditoría | Casos abiertos, decisiones y retroalimentación | |
| 8 | Coche de paro | Pendiente de auditoría | Organización e inventario configurable | |
| 9 | Atlas instrumental | Pendiente de auditoría | PNG + JSON + hotspots por instrumento | |
| 10 | Prototipo B y responsive | Pendiente de auditoría | Capturas en teléfono/tablet, claro/oscuro | |

### Condiciones de cierre

- [ ] El proyecto compila y arranca sin errores bloqueantes.
- [ ] Las funciones marcadas como terminadas fueron probadas en el aplicativo real.
- [ ] Los errores conocidos se documentaron; no se ocultaron.
- [ ] Todas las imágenes marcadas como terminadas tienen PNG y metadatos asociados.
- [ ] Se separó claramente lo terminado del MVP, lo parcial y lo futuro.
- [ ] Se proporcionó una lista de archivos modificados y añadidos.
- [ ] Se conservaron las características válidas del Prototipo B.
- [ ] No se afirmó que una función está lista si solamente existe el diseño o prompt.

---

## 9. MENSAJE FINAL DE EJECUCIÓN PARA ARENA IA

Lee la Base Maestra v2.1 y este documento completo. Audita el repositorio real antes de cambiarlo. Conserva y mejora la aplicación existente; no entregues únicamente otro plan, una maqueta o una lista de recomendaciones.

Implementa y conecta los módulos priorizados para que la aplicación **cargue, busque, abra, calcule, puntúe, guarde y muestre** la información en pantallas funcionales. Empieza por el MVP y prueba cada función. No inventes información clínica ni datos de medicamentos. Todo contenido que no esté validado debe aparecer como pendiente, no como una recomendación confirmada.

Reemplaza obligatoriamente las imágenes robóticas o de palitos por las imágenes PNG individuales descritas en este documento. Investiga primero cada dispositivo, verifica sus partes con fuente primaria y segunda fuente técnica, genera la imagen con fondo transparente real, crea los metadatos JSON y añade los hotspots en la interfaz. No uses etiquetas generadas dentro de la imagen. No integres ninguna imagen que falle la lista de aceptación.

Al finalizar, devuelve la lista maestra con cada módulo e imagen marcado `HECHO — EVIDENCIA`, `PARCIAL`, `PENDIENTE`, `BLOQUEADO` o `FUTURO`, junto con capturas, archivos añadidos/modificados, pruebas realizadas y errores pendientes. **No marcar como realizado algo que no esté implementado y probado en el aplicativo.**

---

## 10. FUENTES TÉCNICAS INICIALES PARA VERIFICAR PARTES Y DISEÑO

Estas referencias sirven como punto de partida; cada imagen debe usar además la fuente del modelo concreto que Arena IA seleccione.

1. 3M Littmann — Anatomía de un estetoscopio: https://multimedia.3m.com/mws/media/1346109O/littmann-anatomy-of-a-stethoscope-poster-pdf.pdf
2. BD — Anatomía de una jeringa y componentes: https://academy.bd.com/content/dam/bd-assets/na/medication-delivery-solutions/documents/in-service-material/BD-2125_Principles_of_Injection_Technique.pdf
3. ISO — ISO 6009:2016, código de color de agujas hipodérmicas de un solo uso: https://www.iso.org/standard/63277.html
4. B. Braun — Instrucciones de manejo Introcan Safety, componentes de catéter periférico: https://www.bbraun-asiapacific.com/content/dam/catalog/bbraun/bbraunProductCatalog/CW_AP/en-ap/b168/introcan-safety-handlinginstructions.pdf
5. B. Braun España — Introcan Safety 3, aletas, septo, clip y doble retorno: https://www.bbraun.es/es/productos-y-soluciones/terapias/acceso_vascular/introcan-safety-3.html
6. JMI — Componentes de equipo de infusión: https://jhrml-bd.com/product/components-of-iv-infusion-set
7. TrueCare Biomedix — Componentes de set IV (punzón, cámara, rodillo, puerto Y, conector): https://www.tcbiomedix.com/our-products/iv-administration-sets/gravity-iv-administration-sets/iv-administration-set
8. ISO FDIS 8536-5:2026 — nomenclatura y diseño de buretas de infusión (confirmar estado final de publicación antes de tratarlo como norma vigente): https://standards.iteh.ai/catalog/standards/iso/3da57975-3d30-46ea-85b5-7042898bf06a/iso-fdis-8536-5
9. SMD Medical — ejemplo de equipo de infusión tipo bureta e IFU: https://www.smd-medical.com/catalogue/iv-therapy-vascular-access/12-infusion-set-burette-type-catalogue.pdf
10. Sterimed — IFU de sonda Foley de dos y tres vías: https://www.sterimedgroup.com/wp-content/uploads/2024/08/IFU-Foley-Balloon-Catheter.pdf
11. Teleflex — catálogo técnico de tubo endotraqueal Murphy Eye: https://www.teleflexarcatalog.com/anesthesia-respiratory/airway/category/sheridan-cf-sup-reg-sup-murphy-eye-ett.pdf
12. NCBI Bookshelf / Open RN — dispositivos de oxigenoterapia y bolsa-válvula-mascarilla: https://www.ncbi.nlm.nih.gov/books/NBK596733/
13. AirLife — componentes y accesorios de resucitador manual: https://myairlife.com/product/airflow-manual-resuscitator
14. Philips — monitor de paciente IntelliVue y mediciones/configuraciones posibles: https://www.usa.philips.com/healthcare/brand/intellivue-patient-monitor-6000-series
15. MINSA Perú — RM 570-2025-MINSA, homologación de catéter venoso periférico: https://www.gob.pe/institucion/minsa/normas-legales/7114444-570-2025-minsa
16. BD — ejemplo de catéter periférico con características y opciones según modelo: https://www.bd.com/content/dam/bd-assets/na/medication-delivery-solutions/documents/instructions-for-use/BD-29510%20CA%20MDS%20-%20BD%20Insyte%20Autoguard%20BC%20Pro%20-%20Points%20to%20Practice%20-%20EN.pdf

**Importante:** las fuentes describen dispositivos o modelos concretos. La aplicación debe evitar generalizar una característica opcional de un producto a todos los dispositivos de la misma familia.
