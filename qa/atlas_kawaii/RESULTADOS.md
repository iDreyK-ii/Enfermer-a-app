# QA de la revisión local 0.8.0-dev / atlas kawaii

Fecha: 2026-11-03. **Pruebas automáticas y comprobación acotada en emulador completas. No es aprobación clínica ni publicación.** La última revisión pública es `b463c580` (0.6.0). Los informes y capturas de 0.7.0 en `qa/atlas_v22/` son históricos; no se atribuyen automáticamente a esta versión.

## Alcance integrado

- 24 ilustraciones 2D originales con caritas discretas, rasterizadas directamente a PNG RGBA 2000 × 1250. No son fotografías ni indicaciones para ejecutar procedimientos. Cada una tiene sidecar, fuentes, variantes y ficha. Revisión profesional pendiente para las 24.
- 165 partes en metadatos: 160 visibles y 5 no visibles; los controles automáticos comprueban coordenadas y píxeles no transparentes para los puntos visibles y exclusión de las caras. Es una comprobación mecánica, no validación de anatomía o legibilidad de cada punto.
- Preguntas solo sobre partes visibles, con hasta cinco objetivos por recurso, opciones y puntuación calculada desde respuestas guardadas. Los IDs, orden de partes/opciones/objetivos de las cinco fichas antes jugables y la versión `atlas-v22.1` se cotejaron con el registro previo.
- La vista previa incluye una traslación decorativa de ±2 dp que une bitmap, puntos y zona táctil. El modo reducido, el zoom, la selección y el quiz detienen el movimiento; hay pausa/reactivación manual. Los tests indican el nivel de prueba realmente alcanzado.

## Ejecución hasta ahora

| Comprobación | Resultado |
|---|---|
| Guardas y pruebas de contenido en `scripts/tests` | 85/85 pasaron |
| Conversor editorial | 15/15 pasaron |
| Validadores backend Python | 21/21 pasaron |
| SQLite | 5/5 pasaron |
| PostgreSQL/RLS en PGLite local | 17/17 pasaron; no es una prueba del servidor remoto |
| Catálogo generado | `--check` pasó; 20 temas, 24 dispositivos, 7 casos, 554 entradas y 737 ocurrencias |
| Compilación Kotlin y APK debug | Gradle `BUILD SUCCESSFUL`; APK final firmado y comprobado |
| PDF instrumentado, APK final | 2/2 pruebas `EducationalPdfDeviceTests` en emulador API28; PDF de 4 páginas y selección de 1 página abiertos mediante `PdfRenderer`. No prueba interacción con un servicio de impresión ni SAF |
| Inspección del APK | versión 0.8.0-dev-debug, code 9; integridad ZIP, permisos, minSdk 26 y certificado esperado coincidentes |
| JVM/Robolectric | 255/255 pruebas en 21 suites; 0 fallos y 0 omitidas; 17 renders nativos API35 con movimiento reducido |
| Lint debug | 0 errores / 26 advertencias |
| APK preliminar 0.8 instalado en emulador | Actualización 0.7 → 0.8 con `adb install -r`: tablas, preferencia y respuesta antigua se conservaron; se reanudó la pregunta 2 de jeringa |
| Selección, zoom y nueva pregunta | En APK preliminar: puerto de irrigación de Foley de tres vías seleccionado por toque; primera respuesta correcta y retroalimentación; zoom 1.5× detenido |
| Movimiento | En APK preliminar: modo Completo, bitmap y marcador se desplazaron juntos en el par analizado; al pausar dos capturas sucesivas fueron idénticas en la región del dibujo (`verificar_movimiento.py`). El modo automático en este emulador de 768 MiB redujo movimiento |
| APK corregido instalado | Actualización sin desinstalar desde 0.8 preliminar; tablas, preferencias y dos respuestas sintéticas conservadas. Rótulo «24 dispositivos» y selección de alas del catéter vistos en APK final |
| Capturas y hardware | 9 capturas de APK instalado (6 preliminares identificadas y 3 finales), 17 renders Robolectric separados. No se ha probado un teléfono o tablet físicos |
| Revisión visual del visor web de muestras | DOM/JavaScript: 24 tarjetas, 165 partes, filtro y fondos comprobados; no prueba diseño, accesibilidad ni rendimiento del navegador o Android |

Durante la inspección del APK preliminar se detectó el rótulo obsoleto «Ocho dispositivos» en la tarjeta del módulo. Se corrigió a «24 dispositivos», se agregó una aserción de coherencia con el catálogo y se recompiló; la batería final se volvió a ejecutar con 255/255 y lint 0/26. Las capturas preliminares están identificadas por su SHA. El emulador sin KVM fue lento y presentó un ANR transitorio de System UI; no es una medición de rendimiento.

Los primeros intentos se interrumpieron por restauración del entorno. Antes de corregir el contrato de versión, una prueba de continuidad falló (`9 != 8`); se actualizó `CONTINUIDAD.json` y la batería Python completa se volvió a ejecutar exitosamente. Se preservan los logs correspondientes en el espacio de evidencia. La prueba JVM interrumpida no se atribuye como aprobada: posteriormente se ejecutó de nuevo y concluyó con 255/255.

## Firma, datos y nube

El APK corregido tiene SHA-256 `0c2e77b6010e07315d0e765773d9ce75c2ad3bb9931ed65f793e26307df3688d`. El certificado SHA-256 del APK compilado coincide con `d6874c68417eefb1b1b902bf82ba4ce7d8734e47877e12bd1733eb498f0e522b`, igual al APK 0.7. La cadena de dos actualizaciones sin desinstalar se probó solo en un emulador con datos sintéticos, no en un teléfono físico. La coincidencia no garantiza la firma de una compilación de Google/CI. No desinstalar ni borrar datos si un teléfono rechaza la firma: verificar y hacer respaldo. Este APK de prueba se compila con URL y clave de Supabase vacías; no hay sesión real/OTP ni sincronización personal probadas aquí.

## Límites abiertos

Revisión profesional anatómica/documental de cada ilustración, aceptación visual, pruebas con TalkBack y equipos físicos, rendimiento en hardware, validación docente, derechos de escalas, monografías clínicas, sesión OTP y paridad de importación/compilación en Google. Farmacología nominal permanece como IDENTIFICADO; la autoevaluación no certifica competencias. No usar con pacientes reales y no publicar sin aprobación expresa del propietario.

## Evidencia instalada y alcance de persistencia

- `conservacion-070-080.json`: de 0.7 al APK 0.8 preliminar sin desinstalar; las ocho tablas de aplicación (y `android_metadata`), la preferencia de bienvenida y una respuesta previa de jeringa permanecieron iguales. La interfaz reanudó la pregunta 2 de 5. Muchas tablas estaban vacías, por lo que no se extrapola a todos los datos de usuarios.
- `conservacion-prelim-final.json`: del APK preliminar al final con el rótulo corregido, ambos code 9 y mismo firmante. Tablas, preferencias y las respuestas de jeringa/Foley de QA permanecieron iguales. El APK final se abrió, mostró «24 dispositivos» en la tarjeta y permitió seleccionar las alas en la imagen del catéter.
- Se conservan 6 capturas del APK preliminar y 3 del APK final con SHA y procedencia en `apk/*.json`. El análisis de movimiento usa el preliminar, que difiere del final en el texto del módulo; no se presenta como una medición sobre el binario final. `movimiento-analisis.json` reproduce la comparación con cuatro capturas; no mide suavidad ni FPS.
- El test de PDF genera archivos ficticios en caché y los abre mediante el marco Android. `pdf-instrumentado.txt` registra 2/2. El servicio de impresión/selector de archivos y el hardware físico siguen sin probarse.
- Android API28 x86_64 sin KVM, `-qemu -m 768`, pantalla 480 × 800: arranque lento y aviso transitorio de System UI no respondía, recuperado con «Wait». Las últimas 2500 líneas no mostraron excepción fatal/ANR de la app; no implica ausencia de fallos fuera de esa ventana.
