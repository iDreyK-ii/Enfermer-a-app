# QA del pulido final — Android 0.9.1-dev

**Corte del 3 de noviembre de 2026.** Revisión local de desarrollo, todavía sin aprobación clínica ni profesional. Las pruebas que siguen corresponden a 0.9.1; las de 0.9.0 permanecen como evidencia histórica en `qa/escalas_kawaii/RESULTADOS.md`. No se realizaron cambios a instrumentos, cálculos clínicos, base local ni sincronización de datos.

## Alcance del retoque

- La fila completa «Solo con acceso: práctica, simulación o registro» puede activarse con un toque. Se añadió semántica de casilla y altura mínima de 48 dp; no sustituye una evaluación con TalkBack ni en un teléfono físico.
- «Limpiar filtros» aparece siempre que hay filtros activos, incluso si todavía hay resultados, y restablece búsqueda, población, área, tipo de acceso y casilla. El contador usa correctamente singular y plural.
- Se retiraron 18 dibujos vectoriales y un archivo de color Android señalados como sin uso por lint y sin referencias funcionales encontradas. Sus bytes se verificaron contra la rama local `respaldo/antes-pulido-0bb4cca` antes de retirarlos; los manifiestos QA históricos no se alteraron. Recuperables del historial Git. No se retiraron las ilustraciones kawaii ni sus fuentes.
- Se actualizaron las guías de continuidad y transferencia: Google AI Studio no ha sido probado con este repositorio y no se promete sincronización bidireccional. `docs/PLAN_GITHUB_AI_STUDIO.md` explica los controles y la aprobación necesarios.

## Pruebas de esta revisión

| Comprobación | Resultado y límite |
|---|---|
| JVM Android | **270/270 pruebas**, 23 suites, sin fallos ni omitidas. Incluye dos pruebas nuevas de semántica, área mínima, toque de la fila, filtros combinados, limpieza y singular. XML en `junit/`. Robolectric/Compose no equivale a un teléfono ni a TalkBack. |
| Python y datos simulados | **93** guardas, **15** pruebas del conversor, **21** de backend, **5** SQLite y **17** PostgreSQL/RLS en PGLite, todas aprobadas. Logs en `comprobaciones/`; no prueban el servicio Supabase real ni una cuenta. |
| Lint | **0 errores, 7 advertencias**, todas de versiones de dependencias (`lint.xml`). Antes había 19 avisos de recursos sin uso y 7 de versiones. No se actualizaron dependencias sin una revisión de compatibilidad. |
| PDF instrumentado | **2/2** `EducationalPdfDeviceTests` aprobadas en Android API28; el código abre los archivos con `PdfRenderer` y comprueba multipágina y selección de una página. Los PDF de prueba registran 4 y 1 páginas. `pdf-instrumentado.txt`. No se probó servicio real de impresión ni SAF. |
| Integridad de recursos | **60/60** archivos de `app/src/main/assets` comparados byte a byte con el APK temporal (`assets-apk.json`); no incluye otros recursos compilados. |
| Filtros en emulador | Una pulsación en el texto del filtro pasó de **45 a 13** fichas (`filtro-activado-emulador.png`). Con Pediatría + acceso se observaron **4 fichas** y, tras tocar «Limpiar filtros», volvieron **45**, selección «Todas» y casilla desmarcada (`filtros-combinados-emulador.png` / `filtros-limpiados-emulador.png`). Las búsquedas y otros filtros combinados se cubren con pruebas JVM. |

### Instalación sin desinstalar

En emulador Android 9 / API28 se instaló primero el APK 0.9.0 debug, se inyectó una muestra **sintética** de base v2, preferencias y un PNG y se actualizó mediante `adb install -r` al APK 0.9.1 temporal, con el mismo paquete y certificado. No se utilizó información real ni teléfono personal.

La verificación de integridad, versión, columnas y filas de ocho tablas, preferencias y PNG pasó antes de abrir la nueva versión (`actualizacion-090-091.json`), tras navegar (`datos-tras-navegacion.json`) y al terminar las pruebas de PDF (`datos-finales.json`). La muestra se detalla en `fixture-sintetica.json`: 8 registros de contenido, 1 favorito, 1 medio, 3 entradas meta, 1 apunte, 1 nota, 1 paquete y 1 práctica. Es un muestreo, no garantiza todas las migraciones ni todos los teléfonos. Los datos y PDF sintéticos se conservaron fuera del repositorio en `evidencia_pulido_final_sintetica/` con hashes; nunca usar datos de pacientes para reproducir estas pruebas.

El emulador de 480 × 800 / 160 dpi corrió sin KVM y fue lento. Se recuperó de un aviso transitorio de **System UI** con «Wait»; algunos volcados de accesibilidad fallaron con raíz nula, por lo que las capturas se inspeccionaron visualmente. En la ventana final de logcat revisada no se encontraron `FATAL EXCEPTION` ni ANR del paquete; eso no demuestra ausencia de fallos en otras condiciones.

### Identidad de la compilación temporal

- Paquete `com.idreyk.enfermeria360.dev`; `0.9.1-dev-debug`, **versionCode 11**, minSdk 26, targetSdk 35.
- Tamaño **13.064.620 bytes**, SHA-256 `6875858d0b4ef83ea75899f63773b3ed7c6dcfceabdf290686a3741de39d04e5`; certificado debug SHA-256 `d6874c68417eefb1b1b902bf82ba4ce7d8734e47877e12bd1733eb498f0e522b` (`apk-verificacion.json`). Las variables de nube estaban vacías en esta compilación.
- El APK fue usado para las pruebas pero **no se entrega ni se conserva como archivo final** por el margen de espacio. El APK 0.9.0 previamente entregado permanece intacto y **no incluye el pulido 0.9.1**. Una futura compilación puede tener un hash distinto y debe verificarse de nuevo.
- Si la firma de una compilación de CI o AI Studio no coincide, Android puede rechazar la actualización. No desinstalar ni borrar datos: respaldar y comprobar identidad y firma primero.

## Pendientes

Revisión profesional de contenido y dibujos, derechos de reproducción de instrumentos, adaptación a protocolos locales, hardware físico, accesibilidad con lectores de pantalla, rendimiento, impresión por servicio/SAF, OTP/nube real si se configura, e importación y compilación en Google AI Studio. La herramienta `pdfinfo` no estaba disponible; el resultado de PDFs se basa en las pruebas instrumentadas y los metadatos del archivo. No se realizó ningún push ni despliegue durante esta preparación.
