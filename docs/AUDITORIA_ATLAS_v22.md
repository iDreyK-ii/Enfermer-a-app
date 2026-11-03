# Auditoría ejecutada v2.2 — entrega local Android 0.7.0

**No publicado.** Esta revisión requiere que el propietario vea la entrega visual y autorice expresamente el siguiente envío. El último commit publicado continúa siendo `b463c580f6e965d971116ef593df0059cb105c86` (0.6.0). Un CI de esa revisión no verifica los cambios actuales.

## 1. Qué se auditó realmente

- Aplicación nativa **Kotlin / Jetpack Compose**, una `MainActivity`, módulo `:app`, minSdk 26, compile/target 35. Se conserva Prototipo B, inicio 2 y ficha 1; no hay sustitución por React, Flutter ni maqueta HTML.
- Rutas y estados: `MainActivity.kt`, pantallas de biblioteca, calculadoras, instrumentos, PAE, casos, contenido, perfil/nube y progreso. El atlas mantiene `atlas` y `dispositivo/{id}`; su autoevaluación es un estado nativo de la ficha, no una ruta web falsa.
- Persistencia: `Repository.kt`, **SQLite v2, ocho tablas**, `meta learning:`. No se recrea la base ni se borra información. Tema/movimiento/perfil permanecen en su infraestructura anterior; cuentas remotas en Supabase Auth, no una nueva tabla local de contraseñas.
- Estilos: `Design.kt`, `Performance.kt`, recursos del Prototipo B y fuentes/colores nativos. Estos archivos permanecen idénticos a la base publicada, con hashes de conservación.
- Contenido: 20 temas, 554 maestros nominales/737 apariciones, siete casos/21 preguntas, dos listas diferenciadas, catálogo previo de 44 fichas/40 fuentes. Los primeros cuatro bloques se comprueban por hash semántico en `baseline_conservado.json`; no se elevan nombres identificados a monografías validadas.
- Imágenes: se retiró la geometría vectorial principal de ocho dispositivos y la ilustración de manos. Se revisaron diez candidatos generados; hubo rechazos y correcciones. Cinco recursos PNG+JSON están integrados; faltan diecinueve de los 24 solicitados.
- Dependencias: Kotlin 2.0.21 / AGP 8.7.3 / Gradle 8.9 / Robolectric 4.14.1 conservados. El atlas no añade una librería de carga remota ni una dependencia de IA al APK: usa `BitmapFactory`. Pillow se añade a las dependencias de verificación editorial, no al runtime Android. OpenCV/rembg se usaron fuera del aplicativo para preparar los recursos.
- Duplicados: inventario nominal sigue con 554 IDs/nombres normalizados distintos; los PNG tienen rutas distintas y digest por imagen. No se duplica una ilustración genérica entre tarjetas. Los esquemas antiguos dejan de generarse con `crear_contenido.py`.

## 2. Cambios implementados

1. Cinco PNG RGBA individuales, transparente real, con tamaño de exportación de 2000 px; originales de menor resolución declarados, no detalle nativo inventado.
2. JSON individual con modelo, vista, partes, función/precauciones, categorías, puntos 0–1, fuentes, revisión, proceso y persona aprobadora nula. Tres fichas sin imagen tienen coordenadas nulas y motivos de rechazo.
3. Carga nativa y acotada; recurso pendiente ante ausencia/error, sin fallback robótico. Nombres fuera de la imagen, etiqueta única para evitar colisiones, zoom, desplazamiento/reinicio, lista accesible y panel lateral desde 600 dp. Ajuste de altura en paisaje.
4. Autoevaluación para las cinco imágenes: hasta cinco objetivos visibles (cuatro en la aguja), opciones, retroalimentación, puntuación desde respuestas, guardado/reanudación y reinicio confirmado. Ruta/versión/opciones/finalización verificadas. No se pregunta por campana, vejiga, lumen o filtro ocultos.
5. Historial y pantalla de avance incluyen identificación. Las lecturas/casos mantienen su versión anterior compatible, sin reinicio artificial del progreso.
6. Higiene mantiene pasos, lavado 40–60 s / fricción 20–30 s, cronómetro, pausa/reinicio, seguridad de ciclo de vida y modo reducido. **La nueva animación anatómica está pendiente**, no se reemplaza por otras manos robóticas.
7. Pruebas de contenido/píxeles/alfa/contratos/persistencia/interfaz y capturas de producción en tamaños/temas/orientaciones distintos. La ejecución de cada suite y sus límites constan en `qa/atlas_v22/RESULTADOS.md`.

## 3. Matriz de todas las funcionalidades auditadas

`HECHO — EVIDENCIA` se limita a la función educativa descrita y al nivel de prueba indicado; no es aprobación clínica ni una declaración de prueba física. Las pruebas automatizadas enumeradas deben leerse junto con el resultado final de la ejecución, no por su mera existencia.

| Funcionalidad | Estado | Ruta / archivo / evidencia reproducible | Pendiente concreto |
|---|---|---|---|
| Inicio, bienvenida, perfil y diseño elegido | HECHO — EVIDENCIA | `inicio`, `perfil`; `MainActivity`, `Design`; hashes base + renders nativos | Paridad física de píxeles con diseño, equipo real |
| Navegación y regreso | PARCIAL | Rutas nativas en `MainActivity`; `InterfaceTests`, `MasterInterfaceTests` | Hardware back/teclado/rotación real y cambios sin pérdida en equipos |
| Claro/oscuro, compacto, tablet y paisaje | PARCIAL | Cinco clases `Native*RenderTests`; galería de código actual | No equivalen a pruebas instaladas ni TalkBack/escala tipográfica exhaustiva |
| Movimiento adaptativo | PARCIAL | `Performance.kt` conservado; controles funcionales en modo reducido | FPS, RAM y consumo en gama baja/alta; no hay benchmark físico |
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
| Atlas: carga, partes, zoom y fuentes | PARCIAL | `atlas`, `dispositivo/{id}`; `ScreensAtlasPng`, `AtlasPngTests` | 5 PNG integrados; 3 rechazados y 16 aún pendientes; revisión profesional |
| Autoevaluación de identificación | HECHO — EVIDENCIA | Cinco dispositivos, respuestas/score/persistencia; tests nuevos de modelo/UI | Primera respuesta, guardado y Siguiente comprobados en APK; finalización completa/reanudación probadas en JVM, no ciclo íntegro instalado. No cuestionario clínico |
| Bioseguridad, cinco momentos y EPP | PARCIAL | `bioseguridad`; temas y contenido anterior | Revisión institucional/supervisada, ampliación de recursos visuales |
| Higiene temporal | PARCIAL | `higiene`; timer, paso, pausa/reinicio, tests de ciclo de vida/UI | Recurso anatómico/animación visual pendiente, explícito en pantalla |
| Listas de seguridad | HECHO — EVIDENCIA | `lista/insnsb-10`, `lista/idreyk-20`; 10 institucionales vs 20 propios | No universalizar institución/ámbito ni autorizar administración por checklist |
| PAE y valoración Gordon/ABCDE | PARCIAL | `cuaderno`, `editar-plan`, `valoracion`; 32 complementarios + campos previos | Validación docente; no diagnósticos automáticos ni catálogos licenciados completos |
| SOAPIE / SBAR | PARCIAL | Campos/editado/Markdown en `NotebookEducation`, tests | Contenido ficticio; revisión de calidad comunicacional profesional |
| Apuntes, cuaderno y respaldos | HECHO — EVIDENCIA | CRUD, búsqueda, etiquetas, revisión/deshacer; lee v1/v2, exporta v2 | Límite 200/2 MB; medios no incluidos; SAF real pendiente |
| Impresión / PDF | PARCIAL | Adaptador/generador; guardas JVM y dos pruebas `androidTest` ejecutadas: 2/2, API28, PDF reales conservados | Interacción del usuario con servicio de impresión/SAF y prueba física pendientes |
| Casos progresivos | PARCIAL | Siete sintéticos /21 preguntas; `simulacion/{id}`, score/rúbricas/persistencia | Revisión docente/clinical. No se extraen indicaciones de órdenes históricas |
| Flashcards, quiz y ECG educativo anterior | PARCIAL | `tarjetas`, `quiz`, `ecg`; código previo conservado | ECG ilustrativo, no monitor ni diagnóstico; mayor validación de contenido |
| Personas ficticias, notas, signos y tendencias | PARCIAL | `paciente`, `evolucion`, `signos`, `balance`; DB/guardas | Solo escenarios inventados; no sensores, alertas clínicas ni pacientes reales |
| Multimedia local | PARCIAL | `multimedia`, copia privada y FileProvider/visor externo | No reproductor propio; prueba instalada de permisos/visores pendiente |
| SQLite y conservación | HECHO — EVIDENCIA | v2/ocho tablas; tests; update real -r 0.6→0.7 en emulador, siete tablas iguales y meta previa conservada, respuesta nueva persistida | No probaron todas las prácticas/importaciones con datos previos; backup/upgrade en teléfono físico pendientes |
| Cuenta opcional / sesión remota | PARCIAL | Cliente Supabase anterior conservado, local sin cuenta | OTP/plantillas/sesión E2E aplazados; APK local sin configuración de nube |
| Publicación editorial, RLS y esquema servidor | PARCIAL | Infraestructura previa + tests SQLite/PGLite locales | No se reaplica migración ni publica. QA local no certifica estado remoto actual |
| Carro de paro, stock y algoritmos | BLOQUEADO | Backlog Base 2.1; no inventar existencias/protocolos | Institución, responsables, inventario y guías validadas |
| Monografías clínicas completas | BLOQUEADO | Prioridad final del plan original | Registro/producto/IFU oficial, revisión y autorización editorial |
| Especialidades/simulación avanzada | FUTURO | Sección 10/13 y roadmap conservados | Implementación priorizada con evidencia, no tarjetas de simulación |
| 12 recursos futuros adicionales | FUTURO | Sección 5 de la instrucción v2.2 | Separados de los 24 PNG actualmente exigidos |
| Importación/compilación/paridad en Google | PENDIENTE | Guías preservadas; no reescritura del nativo | Importar revisión expresamente aprobada, compilar/comparar/exportar evidencia. No se verificó Google desde aquí |
| Publicar esta actualización en GitHub | PENDIENTE | Entrega LOCAL para revisión | Autorización expresa del propietario; no push automático |

## 4. Matriz individual de los 24 PNG

Inventario verificable, con nombre de archivo pedido, ruta y pendiente concreto: **`qa/atlas_v22/inventario_24_png.json`**. La entrega visual muestra los cinco recursos actuales y una tabla con las 24 solicitudes. Los cinco integrados siguen `PARCIAL` respecto de la aceptación completa (no se atribuye aprobación profesional); los otros diecinueve son `PENDIENTE`, no `FUTURO`.

## 5. Verificación y limitaciones

Comandos principales:

```sh
python3 -m pip install -r content-tools/requirements.txt
npm ci --prefix backend --ignore-scripts
bash scripts/comprobar.sh --rapido
bash scripts/comprobar.sh --todo
```

JDK 21, SDK 35/build-tools 35, una tarea Gradle a la vez. `--todo` compila también el APK de pruebas, pero **no ejecuta** `androidTest`. La verificación instalada debe hacerse aparte con un dispositivo/emulador viable, sin desinstalar para forzar un cambio de firma.

Los resultados corrientes, hashes, capturas, advertencias y errores residuales pertenecen a `qa/atlas_v22/RESULTADOS.md` y sus evidencias. Los informes 0.6/Base 2.1 quedan históricos. La galería HTML es un visor autocontenido de evidencia y recursos, no una segunda implementación de la app.

El intento inicial de emulador sin KVM forzó 1536 MiB pese a solicitar 768; el host de 1982 MiB quedó con 29 MiB disponibles y no respondió la comprobación de arranque. Se detuvo para proteger el trabajo. Enumerar ADB no equivale a ejecutar la app.

## 6. Seguridad y pendientes que no se ocultan

Sin diagnóstico/prescripción por IA, dosis inventadas, datos identificables, stock fabricado ni permisos clínicos supuestos. Las imágenes no son fuentes de verdad de valores/dosis, ni prueba de compatibilidad, esterilidad o calibración. Persisten permisos/versiones de instrumentos, revisión profesional y varios módulos abiertos; no se declara porcentaje de finalización, certificación ni 100% de eficacia.

### Cierre ejecutado

252/252 JVM (21 suites), 118 Python, 5 SQLite, 17 PGLite; lint 0 errores/26 advertencias. APK y test-APK compilados. Segundo emulador arrancó con `-qemu -m 768` (758292 kB visibles), sin KVM: se actualizó con `install -r`, se capturaron ocho pantallas de la app real y pasaron 2/2 pruebas PDF instrumentadas (6,807 s). Diecisiete renders Robolectric se conservan aparte.

La emulación mostró ANR transitorio de System UI, esperas agotadas de arranque y `null root` intermitente en UIAutomator; la entrada automatizada dejó el prefijo `jeri`, suficiente para encontrar la ficha pero no para validar teclado fluido. Capturas 05–08: viewport lógico 800×1280, pantalla/captura física 480×800 reescalada por el emulador. No se afirma prueba en tablet física ni rendimiento aceptable. Detalles y limitaciones: `qa/atlas_v22/RESULTADOS.md`.
