# Base Maestra 2.1 — entrega MVP educativa sobre la app existente

**Fecha:** 3 de noviembre de 2026. **Android:** `0.6.0-dev` / debug `0.6.0-dev-debug`, versionCode **7**, minSdk 26, target/compileSdk 35.
**La especificación 2.1 no es la versión de Android.** Esta entrega no declara la app completa ni clínicamente certificada.

Se continúa Kotlin/Jetpack Compose, Prototipo B, inicio 2/ficha 1, el repositorio canónico y la rama `desarrollo`. No se ha creado una app web alternativa. La revisión exacta es la comunicada en la entrega y en la etiqueta `base-maestra-v21-mvp`; comprobarla antes de importar.

## 1. Lectura, respaldo y preservación

- Leídos los **13 apartados y anexos A/B completos**, más la instrucción de ejecución. Entrada editorial íntegra: `research/base_maestra_v21/BASE_EDITORIAL_NO_CLINICA.md`; SHA-256 `f0b9107128659d9b225d44424b3d7ea4bdd366ab29ba2b81627d7c380a5908ee`. No se empaqueta ese texto bruto como guía clínica.
- Los PNG aportados se tratan como referencias de diseño, no como fuentes de dosis, técnica o validación.
- Base anterior: `cb05f1f0b27d6f1d50ae651df89bd0fab2c95867`. Respaldo publicado: `respaldo/base-maestra-v21-cb05f1f` en ese mismo commit.
- Antes de modificar: **163 pruebas Android/11 suites, 100 Python, 5 SQLite, 17 PGLite; lint 0 errores/24 advertencias; ensamblado e inspección del APK correctos**. Evidencia: `qa/base_maestra_v21/antes.json`. Auditoría inicial: `AUDITORIA_BASE_MAESTRA_v21.md`.
- Se conservan los archivos anteriores, recursos, diseños, PDF original, 44 fichas de escalas, 40 fuentes, seis borradores farmacológicos, editores, revisión/importación/deshacer y funciones locales. La ficha farmacológica mantiene sus campos anteriores; no se sustituyen por el inventario nominal.
- `Design.kt` y `Performance.kt` conservan sus bytes. El inicio y la navegación se amplían con accesos, no se afirma identidad de píxeles de todas las pantallas. Evidencia específica de preservación en `qa/base_maestra_v21/preservacion.json`.

## 2. Qué se añadió o reforzó realmente

### Inicio, biblioteca, búsqueda y progreso

Biblioteca local de **20 temas Markdown** con títulos, subtítulos, listas, tablas, fórmulas como texto, fuentes, autoría/versión/fecha y revisión pendiente. Filtros de área/nivel y búsqueda en contenido, no una organización por ciclos universitarios. Se conserva la búsqueda anterior y se amplía a temas, etiquetas, áreas, procedimientos, partes de dispositivos, casos, escalas y nombres del inventario.

Marcar lectura, guardar favoritos y responder casos cambia datos reales del dispositivo. No se infieren lecturas por abrir una pantalla ni se inventan porcentajes de dominio. Hay pantalla de avance y de estado de módulos. Un progreso de una versión incompatible no acredita la versión nueva.

### Aritmética educativa

Motor `BigDecimal`/DECIMAL128 para dosis-volumen, cantidad/kg, conversiones dimensionales, mL/h, goteo, duración, C1V1=C2V2 cuando corresponde, balance e IMC. Se mantienen las tendencias de signos ficticios.

- Fórmula, sustitución, unidades, valor decimal y redondeo de presentación visibles; **EJERCICIO EDUCATIVO**.
- El factor del equipo se **introduce obligatoriamente**. No se presume 20, 60 ni una presentación comercial.
- Cantidad/kg exige edad del ejemplo en días y base por dosis/por día; no propone una dosis por edad o peso.
- Balance exige un intervalo común. Dilución distingue volumen final de diluyente añadido y no prueba estabilidad, compatibilidad ni reconstitución.
- Se rechazan vacíos, negativos no admitidos, exponentes, separadores ambiguos, dimensiones incompatibles y excesos técnicos. Entrada de hasta 18 dígitos/9 decimales, magnitud hasta 10^9; resultado hasta 10^12. Estos **no son límites clínicos de seguridad**.
- Editar entradas invalida el resultado anterior y su guardado. Las gotas se muestran enteras con mitades hacia arriba; los valores internos no se redondean en cada paso.

### Atlas y bioseguridad

Ocho dispositivos: jeringa, aguja, estetoscopio, tensiómetro, catéter venoso periférico, equipo IV, bureta/Volutrol y llave de tres vías. Vectores parametrizados, selección táctil, lista textual accesible, función/precaución, números, zoom 1–2× y capa de la parte seleccionada. **Pistón/sello y vástago del émbolo son entidades distintas; cánula flexible y aguja introductora también.** Son esquemas genéricos, no instrucciones autónomas de inserción ni dibujos calibrados de un fabricante.

Cinco momentos, precauciones estándar/EPP y un recorrido de higiene: lavado completo 40–60 s, fricción completa 20–30 s, iniciar/pausar/reiniciar/pasos, pausa al salir del primer plano y controles disponibles con movimiento reducido. El dibujo y reparto temporal por paso son didácticos propios; no certifican técnica ni descontaminación. **Preparación quirúrgica sin cronómetro universal:** depende de fabricante/protocolo.

Dos listas separadas:

1. **10 correctos institucionales** de GP-028/INSN-SB/UE-V.01, abril 2021, lista p. 6 y ámbito p. 3, para el contexto IV pediátrico/neonatal de esa guía. Se respeta el agrupamiento original, incluido alergias/medicamento no prescrito en el mismo ítem.
2. **20 controles propios, no oficiales**, sin presentarlos como una norma universal MINSA.

### PAE, apuntes y casos

Los siete campos previos del plan se conservan. Se agregan **32 campos opcionales**: datos subjetivos/objetivos/fuente (3), Gordon (11), ABCDE (5), razonamiento/NOC/NIC propios (3), SOAPIE (6) y SBAR (4). Sin taxonomías, códigos licenciados o diagnósticos sugeridos automáticamente.

Persistencia, búsqueda, vista previa, exportación Markdown educativa e importación de apuntes UTF-8 como nuevas notas sin validar. Se conserva respaldo/revisión/deshacer. El respaldo del cuaderno exporta esquema **2** y lee **1 y 2**; límite de 200 documentos/2 MB. Importación Markdown: 64 KB y 16 000 caracteres, sin ejecutar HTML ni cargar imágenes remotas. El lector limita estructura a 1000 líneas/12 columnas/200 filas y muestra un error comprensible ante excesos.

**Impresión/PDF:** adaptador Android y generador `PrintedPdfDocument` implementados; guardas de preparación, cancelación, límite de 100 páginas y área imprimible probadas. No se ha verificado una salida PDF real ni el servicio de impresión. Las dos pruebas de generación/selección de página están en `androidTest`: **compiladas, no ejecutadas**. No se presenta un PDF fabricado por otro programa como resultado de la app.

**7 casos sintéticos, 21 preguntas**: diabetes/ITU/HTA; TCE/UCI; hemorragia digestiva alta; pancreatitis; neumonía/asma; sepsis/shock; ECG. Etapas desbloqueadas tras guardar respuesta, reanudación, explicación, rúbrica y puntaje calculado desde elecciones reales. No se copiaron historias identificables ni órdenes de los casos de referencia no aportados. Revisión docente/clinica pendiente.

### Escalas y farmacología

Se conservan Glasgow adulto, EVA/NRS, Apgar educativo, 4AT, Capurro, Silverman y los registros de alcance explícito ya disponibles. Las **44 fichas documentales no son 44 calculadoras**.

Braden original (6–23), Morse (0–125), Norton original (5–20) y RASS (−5 a +4) tienen **solo registro de un total externo ficticio**, entero, con confirmación adulta y fuente/versión/contexto. Cero y vacío se distinguen. Se rechazan identificadores desconocidos. Norton/RASS tienen rangos numéricos cotejados, no una validación del cuestionario. No se administran formularios, no se interpretan umbrales ni se infiere autorización digital. Braden/Morse mantienen sus restricciones de derechos.

Se importan **554 entradas nominales / 737 apariciones trazadas** de la sección 06 y anexo B, con un maestro editorial por nombre normalizado y relaciones secundarias. Se distinguen combinaciones, formulaciones, biológicos, soporte, nutrición y hemocomponentes. Son entradas editoriales, **no 554 DCI confirmadas, monografías aprobadas o productos disponibles en Perú**.

Todas permanecen en **IDENTIFICADO**. El flujo futuro es IDENTIFICADO → PENDIENTE-FICHA → FUENTE-ENCONTRADA → VALIDADO-DIGEMID/GPC → REVISIÓN-FARMACÉUTICA → PUBLICABLE; esta importación no adelanta ninguno. `altilice` sigue ambiguo, sin sustituirlo por un medicamento supuesto. No se han añadido pautas, diluciones o compatibilidades farmacológicas desde nombres, PNG o citas incompletas. Monografías producto a producto: **P4, después del núcleo**.

## 3. Matriz final de alcance

HECHO = función técnica disponible y comprobada dentro de lo indicado, no aprobación clínica. PARCIAL = existe una parte funcional y se identifica lo restante. FALTA = no implementado; no botón simulado. BLOQUEADO-POR-VALIDACIÓN = no se habilita administración/publicación hasta resolver fuentes, versión, población o derechos.

| Requisito | Estado | Evidencia / límite concreto |
|---|---|---|
| Android nativo, diseño y funciones previas | HECHO | Preservación, regresión y compilación; sin paridad física de píxeles afirmada. |
| Inicio, búsqueda global offline, favoritos, historial | HECHO | Integración nativa y tests de navegación/repository. |
| Progreso y estado de módulos | HECHO | SQLite `meta`, acciones reales, persistencia y rechazo de respuestas/versiones inválidas. |
| Biblioteca Markdown y filtros | HECHO | 20 temas iniciales, parser acotado y pruebas. No toda la Base convertida en contenido clínico. |
| Calculadoras educativas decimales | HECHO | 27 tests nuevos de motor; entradas, unidades, límites y flujos UI. |
| GCS adulto, EVA/NRS, Apgar y funciones previas | HECHO | Se conservan sus contratos y regresión. Uso educativo de alcance explícito. |
| Registro externo Braden/Morse/Norton/RASS | HECHO | Validación numérica/contexto; no aplicación ni interpretación de instrumentos. |
| Administración completa Braden/Norton/RASS/Morse | BLOQUEADO-POR-VALIDACIÓN | Versiones, revisión clínica y derechos digitales según instrumento. |
| Atlas de 8 dispositivos | PARCIAL | Interacción, partes y controles hechos; cotejo fabricante/experto pendiente. |
| Bioseguridad, higiene y EPP | PARCIAL | Lecturas y cronómetro hechos; técnica visual/uso supervisado no certificados. |
| Diez correctos INSN-SB y veinte controles propios | HECHO | Listas separadas, ámbito y autoría explícitos; no autorización de administración. |
| PAE/Gordon/ABCDE/SOAPIE/SBAR | HECHO | Campos persistentes y compatibles; texto propio, sin taxonomías licenciadas. |
| Importación/exportación Markdown | HECHO | Validación UTF-8/límites/UUID nuevo y pruebas de persistencia. SAF real pendiente. |
| Impresión y salida PDF educativa | PARCIAL | Código y cinco guardas probadas; dos pruebas instrumentadas compiladas, sin ejecución real. |
| Casos progresivos | PARCIAL | Siete recorridos/21 preguntas funcionales; revisión docente/clinica pendiente. |
| Inventario nominal | HECHO | 554/737, procedencia por línea y regeneración idéntica comprobadas. |
| Monografías/registro/stock/permisos de productos | BLOQUEADO-POR-VALIDACIÓN | No validación DIGEMID ni farmacéutica atribuida a esta fase. |
| Coche de paro/cartuchera/área clínica completos | FALTA | Lecturas iniciales no equivalen a inventario institucional o checklist operativo. |
| Especialidades/simulación/docencia avanzadas | FALTA | Roadmap; sin funciones ficticias. |
| Animación adaptable y modo reducido | HECHO | Política anterior conservada; controles nuevos operan sin animación. FPS real no medido. |
| Renderizado de teléfono/tablet | HECHO | 10 imágenes del código Compose, API 35, 390/800/320 dp; no APK instalado. |
| Accesibilidad/rendimiento/dispositivos físicos | PARCIAL | Roles, texto alternativo, zoom y pruebas de UI; TalkBack, teclado, recortes, baja RAM y hardware reales pendientes. |
| Cliente de cuenta/contenido remoto | PARCIAL | Infraestructura previa intacta. OTP/sesión/extremo a extremo aplazados; APK local sin nube. |
| Build, lint y QA automatizada | HECHO | Recuentos y archivos en `qa/RESULTADOS_v0.6.0.md`; advertencias no ocultas. |

## 4. Trazabilidad de todos los apartados leídos

| Entrada | Tratamiento en esta entrega |
|---|---|
| 01 Contenidos/competencias | Biblioteca por nivel/área; expansión curricular independiente en roadmap. |
| 02 Escalas/tests | Funciones preservadas, registros externos y bloqueo explícito de formularios no autorizados. |
| 03 Bioseguridad/higiene/medicación segura | Lecturas, timer, dos listas diferenciadas. |
| 04 Instrumental/dispositivos | Atlas inicial de ocho; expansión y revisión pendiente. |
| 05 Coche de paro/cartuchera/área | Lecturas iniciales, módulo operativo futuro. |
| 06 Inventario farmacológico | Importación nominal trazable; monografías P4. |
| 07 PAE/casos/razonamiento | Cuaderno ampliado y siete escenarios nuevos sintéticos. |
| 08 Brief técnico | Mismo repositorio, nativo, offline, bases existentes; no reescritura. |
| 09 Referencias/vigencia | Metadatos y límites; no se inventa revisión completa de referencias. |
| 10 Roadmap | `10_ROADMAP_Y_MODULOS_FUTUROS.md`. |
| 11 Datos/QA | Esquemas compatibles, tipos estrictos, límites y pruebas reales. |
| 12 Especialidades | Backlog explícito, no pantallas supuestamente terminadas. |
| 13 Implementación/aceptación | Esta matriz, APK, tests, imágenes y limitaciones verificables. |
| Anexo A | Fórmulas abstractas/ejercicios nuevos; no se publican órdenes históricas como recomendaciones. |
| Anexo B | Maestro nominal y relaciones; calificación editorial 20/20 no se toma como aprobación profesional. |

## 5. Base de datos, cuentas y actualizaciones

**SQLite v2, ocho tablas sin cambio de esquema.** Cuaderno en `notebooks`, progreso en `meta` con prefijo `learning:`, favoritos/historial en las tablas existentes. No se borra ni recrea la base. Preferencias y archivos siguen locales. El inventario y la biblioteca maestra se distribuyen como assets con la actualización del APK; los apuntes Markdown se importan por el usuario.

La infraestructura de cuentas sigue siendo **Supabase Auth**, con perfiles/editorial/RLS previos; no SQLite ni una cuenta simulada. Crear cuenta no sincroniza automáticamente cuaderno, favoritos o medios. El cliente remoto anterior permanece, pero esta QA y el APK local tienen URL/clave vacías. No se ha modificado el servidor ni repetido su migración; OTP/plantillas/sesión reales siguen aplazados.

## 6. Pruebas y apariencia: qué demuestran y qué no

- **235 tests Android/JVM, 18 suites, 0 fallos/errores/omisiones**; 72 más que la base, incluidos cinco contratos de impresión. **111 Python, 5 SQLite y 17 PostgreSQL/PGLite** locales. No son pruebas de cuentas reales.
- **Lint: 0 errores, 26 advertencias**: siete avisos de versiones de dependencias y 19 recursos sin uso. No se actualizaron bibliotecas a ciegas para silenciarlos.
- Dos pruebas de PDF de `app/src/androidTest/` ensambladas, **no ejecutadas ni sumadas a 235**.
- Diez capturas reales del **render del código**, no de un APK físico/emulador: Robolectric 4.14.1, gráficos NATIVE, framework API 35, `View.draw(Canvas(Bitmap))`, mdpi y movimiento reducido. PNG y metadatos/SHA en `qa/base_maestra_v21/capturas/`; desglose `qa/base_maestra_v21/desglose_visual.html`.
- No se atribuye revisión visual física, igualdad de píxeles con un PNG de diseño, rendimiento/FPS, TalkBack real, salida PDF real, certificación clínica ni importación Google verificada.

Se registraron y corrigieron fallos de pruebas/selectores y un límite de memoria del entorno. Los temporales nativos dejaron el tmpfs `/tmp`; las suites se aíslan secuencialmente. PixelCopy no obtuvo callbacks de display en el entorno sin pantalla; se usa rasterización nativa de la vista. El renderer software API 28 recortaba higiene pese a geometría correcta; las capturas finales son API 35. **Robolectric 4.14.1 no implementa el escritor nativo PdfDocument**: se separaron sus pruebas reales a instrumentación, sin sustituirlas por un mock que aparentara un PDF generado.

## 7. Compilar, comprobar y continuar

Ver `COMPILAR_Y_PROBAR.md` y `INICIAR_EN_AI_STUDIO.md`. Comando local: `bash scripts/comprobar.sh --todo`; Android/SDK/JDK y dependencias deben estar preparados. El comando compila las pruebas instrumentadas pero **no conecta ni prueba un teléfono**.

Antes de instalar: comparar paquete, versionCode y certificado, respaldar catálogo/cuaderno y conservar medios aparte. Usar actualización `adb install -r` solo con firma compatible. **No desinstalar ni borrar datos para forzar una actualización.** CI/Google no comparten necesariamente la firma debug conservada.

Pendientes prioritarios: prueba física controlada de actualización/SAF/PDF/teclado/TalkBack/rotación/tiempo en segundo plano; revisión docente/experta; derechos de instrumentos; cierre OTP cuando se retome; luego expansión y monografías P4. No activar servicios de pago ni publicar como producto clínico sin autorización.
