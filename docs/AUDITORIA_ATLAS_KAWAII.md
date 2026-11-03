# Auditoría actual: Atlas kawaii y aplicación nativa 0.8.0 (local)

**Estado:** revisión parcial educativa, no clínica y aún sin publicar. La elección de estilo del propietario reemplaza el fotorrealismo por kawaii, no elimina los controles de anatomía, transparencia, fuentes ni el requisito de revisión profesional. La última publicación continúa siendo `b463c580` (0.6.0); ninguna mejora o solicitud de continuación autoriza un nuevo push.

## Realizado en esta revisión

- Cinco imágenes anteriores redibujadas y diecinueve nuevas: **24 archivos PNG RGBA 2000 × 1250**, rasterizados directamente de ilustraciones 2D originales, no fotos ampliadas. El inventario por recurso está en `qa/atlas_kawaii/inventario_24_png.json`; cada ficha/JSON enlaza fuentes y distingue partes visibles.
- Las caritas son decorativas; pantallas vacías, manómetro sin aguja y sin cifras clínicas. Los puntos y las etiquetas los dibuja Compose; las partes ocultas no se preguntan. Las configuraciones se cotejaron con fuentes indicadas, pero ningún experto ha aprobado estas ilustraciones.
- Fichas para los 24 dispositivos, búsqueda, selección de parte, zoom, detalle, fuentes y hasta cinco preguntas con puntuación y guardado. Las cinco fichas antes disponibles mantienen el orden/índices de preguntas y opciones. El progreso previo sigue usando `atlas-v22.1`; temas y casos no se migran.
- Movimiento de vista previa: traslación de ±2 dp, 2200 ms por tramo; se desactiva con la política existente de movimiento reducido/rendimiento y se detiene al seleccionar una parte, ampliar o entrar al cuestionario. Bitmap y marcador comparten la transformación. También se puede pausar.
- Antecedentes 0.7: `qa/atlas_v22/RESULTADOS.md` contiene la evidencia histórica, incluidos PDF instrumentados y capturas antiguas. No atribuir automáticamente esa prueba al nuevo APK. Resultados de esta revisión: `qa/atlas_kawaii/RESULTADOS.md`.
- Farmacología nominal, escalas con restricciones, Supabase opcional, PAE, cálculo y casos previos conservan sus límites. La app no recibe pacientes reales ni autoriza procedimientos o dosis.

## Matriz funcional

`HECHO — EVIDENCIA` indica únicamente el alcance educativo y la clase de prueba señalada; `PARCIAL`, `PENDIENTE`, `BLOQUEADO` y `FUTURO` mantienen sus límites. Las referencias de versiones anteriores deben leerse como históricas.

| Funcionalidad | Estado | Ruta / archivo / evidencia reproducible | Pendiente concreto |
|---|---|---|---|
| Inicio, bienvenida, perfil y diseño elegido | HECHO — EVIDENCIA | `inicio`, `perfil`; `MainActivity`, `Design`; hashes base + renders nativos | Paridad física de píxeles con diseño, equipo real |
| Navegación y regreso | PARCIAL | Rutas nativas en `MainActivity`; `InterfaceTests`, `MasterInterfaceTests` | Hardware back/teclado/rotación real y cambios sin pérdida en equipos |
| Claro/oscuro, compacto, tablet y paisaje | PARCIAL | Cinco clases `Native*RenderTests`; galería de código actual | No equivalen a pruebas instaladas ni TalkBack/escala tipográfica exhaustiva |
| Movimiento adaptativo | PARCIAL | `Performance.kt` conservado; vista previa kawaii y política `AtlasMotionPolicy` | FPS, RAM y consumo en gama baja/alta; no hay benchmark físico |
| Búsqueda global offline y filtros | HECHO — EVIDENCIA | `buscar`, `biblioteca`, `inventario`; `MasterCatalogTests`, pruebas de interfaz | Solo contenido empaquetado/local, no resultados remotos inventados |
| Biblioteca de temas | HECHO — EVIDENCIA | 20 temas; `ScreensMaster`, `StudyMarkdown`, parser acotado | Ampliación curricular y revisión docente/profesional |
| Favoritos | HECHO — EVIDENCIA | `favoritos`; SQLite, pruebas de repositorio/UI | Sin sincronización personal automática |
| Historial y avance versionado | HECHO — EVIDENCIA | `historial`, `avance`; `learning:`; casos/lecturas/identificación | No interpreta el avance como competencia clínica |
| Fichas farmacológicas previas | PARCIAL | `farmacologia`, `med/{id}`; seis borradores/estructura anterior conservada | Producto y fuente exactos, revisión farmacéutica; fase clínica final |
| Inventario nominal | HECHO — EVIDENCIA | `inventario`, `nominal/{id}`; 554/737, hashes y tests Python/JVM | Nombres IDENTIFICADO; no 554 monografías, stock o DCI confirmadas |
| Editores y gestión de contenido | HECHO — EVIDENCIA | `editar`, `contenido`; editores/pruebas de repositorio | Guardar/importar no aprueba ni publica un contenido |
| Importación de catálogos y deshacer | HECHO — EVIDENCIA | JSON v1/2/3, diferencias, aceptación y guardas de reemplazo | Interacción real con SAF y proveedores externos |
| Fichas documentales de escalas | PARCIAL | `biblioteca-escalas`, `instrumento`; 44 fichas/40 fuentes conservadas | No todas tienen formulario; verificar versiones, permisos y autoría |
| Instrumentos funcionales previos | PARCIAL | GCS/NV, EVA/NRS, 4AT, Apgar, Capurro, Silverman; tests existentes | Validación clínica/local de versiones/poblaciones; no diagnóstico automático |
| Braden / Morse completos | BLOQUEADO | Registradores externos conservados, sin cuestionario fabricado | Permisos digitales, versión y aprobación profesional |
| Norton / RASS administrados | BLOQUEADO | `registro-escala`, límites enteros/versión/contexto | Registro externo no equivale a aplicar el instrumento; derechos/validación |
| Matemática: dosis-volumen, kg, unidades | HECHO — EVIDENCIA | `calc`, `DoseMath`, `EducationMath`, `CoreTests`, `EducationMathTests` | Solo ejercicios; no selecciona dosis/regímenes ni valida prescripciones |
| mL/h, goteo, duración y dilución aplicable | HECHO — EVIDENCIA | Entradas obligatorias, factor explícito, BigDecimal, fórmula/redondeo, tests | Leer factor del equipo; no usar la imagen para inferirlo |
| Balance e IMC | HECHO — EVIDENCIA | Intervalo compartido/unidades y límites; pruebas matemáticas | No interpretación/diagnóstico ni datos identificables |
| Atlas: carga, partes, zoom y fuentes | PARCIAL | `atlas`, `dispositivo/{id}`; `ScreensAtlasPng`, `AtlasPngTests` | 24 PNG y 24 fichas integradas; revisión profesional y pruebas físicas pendientes |
| Autoevaluación de identificación | HECHO — EVIDENCIA | 24 dispositivos, respuestas/score/persistencia; tests de modelo/UI | No se presume que los 24 ciclos completos se hayan ejercitado en APK instalado. No cuestionario clínico |
| Bioseguridad, cinco momentos y EPP | PARCIAL | `bioseguridad`; temas y contenido anterior | Revisión institucional/supervisada, ampliación de recursos visuales |
| Higiene temporal | PARCIAL | `higiene`; timer, paso, pausa/reinicio, tests de ciclo de vida/UI | Recurso anatómico/animación visual pendiente, explícito en pantalla |
| Listas de seguridad | HECHO — EVIDENCIA | `lista/insnsb-10`, `lista/idreyk-20`; 10 institucionales vs 20 propios | No universalizar institución/ámbito ni autorizar administración por checklist |
| PAE y valoración Gordon/ABCDE | PARCIAL | `cuaderno`, `editar-plan`, `valoracion`; 32 complementarios + campos previos | Validación docente; no diagnósticos automáticos ni catálogos licenciados completos |
| SOAPIE / SBAR | PARCIAL | Campos/editado/Markdown en `NotebookEducation`, tests | Contenido ficticio; revisión de calidad comunicacional profesional |
| Apuntes, cuaderno y respaldos | HECHO — EVIDENCIA | CRUD, búsqueda, etiquetas, revisión/deshacer; lee v1/v2, exporta v2 | Límite 200/2 MB; medios no incluidos; SAF real pendiente |
| Impresión / PDF | PARCIAL | Adaptador/generador; guardas JVM y dos pruebas `androidTest` ejecutadas en APK 0.8 final: 2/2, API28, PDF reales conservados | Interacción del usuario con servicio de impresión/SAF y prueba física pendientes |
| Casos progresivos | PARCIAL | Siete sintéticos /21 preguntas; `simulacion/{id}`, score/rúbricas/persistencia | Revisión docente/clinical. No se extraen indicaciones de órdenes históricas |
| Flashcards, quiz y ECG educativo anterior | PARCIAL | `tarjetas`, `quiz`, `ecg`; código previo conservado | ECG ilustrativo, no monitor ni diagnóstico; mayor validación de contenido |
| Personas ficticias, notas, signos y tendencias | PARCIAL | `paciente`, `evolucion`, `signos`, `balance`; DB/guardas | Solo escenarios inventados; no sensores, alertas clínicas ni pacientes reales |
| Multimedia local | PARCIAL | `multimedia`, copia privada y FileProvider/visor externo | No reproductor propio; prueba instalada de permisos/visores pendiente |
| SQLite y conservación | HECHO — EVIDENCIA | v2/ocho tablas; tests; 0.7→0.8 preliminar→0.8 final en emulador, tablas/preferencias y respuestas sintéticas conservadas; ver QA actual | No probaron todas las prácticas/importaciones con datos previos; backup/upgrade en teléfono físico pendientes |
| Cuenta opcional / sesión remota | PARCIAL | Cliente Supabase anterior conservado, local sin cuenta | OTP/plantillas/sesión E2E aplazados; APK local sin configuración de nube |
| Publicación editorial, RLS y esquema servidor | PARCIAL | Infraestructura previa + tests SQLite/PGLite locales | No se reaplica migración ni publica. QA local no certifica estado remoto actual |
| Carro de paro, stock y algoritmos | BLOQUEADO | Backlog Base 2.1; no inventar existencias/protocolos | Institución, responsables, inventario y guías validadas |
| Monografías clínicas completas | BLOQUEADO | Prioridad final del plan original | Registro/producto/IFU oficial, revisión y autorización editorial |
| Especialidades/simulación avanzada | FUTURO | Sección 10/13 y roadmap conservados | Implementación priorizada con evidencia, no tarjetas de simulación |
| 12 recursos futuros adicionales | FUTURO | Sección 5 de la instrucción v2.2 | Separados de los 24 PNG actualmente exigidos |
| Importación/compilación/paridad en Google | PENDIENTE | Guías preservadas; no reescritura del nativo | Importar revisión expresamente aprobada, compilar/comparar/exportar evidencia. No se verificó Google desde aquí |
| Publicar esta actualización en GitHub | PENDIENTE | Entrega LOCAL para revisión | Autorización expresa del propietario; no push automático |


## Inventario individual y criterios abiertos

Los 24 recursos solicitados figuran por nombre, ruta, evidencia y trabajo restante en `qa/atlas_kawaii/inventario_24_png.json`. Todos permanecen **PARCIAL** porque falta aprobación profesional y prueba física; ninguno se marca como FUTURO. Los doce recursos adicionales de la sección 5 son otra fase.

Quedan pendientes: revisión de los 24 modelos e identificaciones por profesionales competentes; licencias y validación de instrumentos bloqueados; monografías por producto con fuente oficial; pruebas físicas y accesibilidad con TalkBack; evaluación de rendimiento en equipos de distintas gamas; sesión OTP, importación y paridad Google; y consentimiento explícito para publicar. No se promete una finalización clínica ni paridad de píxeles.

Las referencias documentales y sus límites están en los JSON individuales y `research/atlas_kawaii/README.md`. La prueba automatizada de alfa/posición demuestra propiedades mecánicas, no que una parte sea clínicamente correcta.
