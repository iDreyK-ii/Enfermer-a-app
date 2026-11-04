# QA de escalas kawaii — Android 0.9.0-dev

**Estado: entrega local de desarrollo comprobada, sin publicar.** La aplicación sigue siendo educativa y su adaptación no tiene validación clínica ni aprobación profesional. La revisión de fuentes es **parcial**, no sistemática: ver `research/escalas_kawaii/REVISION_2026-11-03.md` y cada ficha. Los resultados de 0.7/0.8 son históricos y no se atribuyen a 0.9 salvo las comprobaciones expresamente repetidas aquí.

## Alcance de esta entrega

- 45 fichas, 43 referencias y 13 accesos de distintas modalidades. Una ficha no implica calculadora. Norton y RASS enlazan solo a registros externos adultos ya existentes; no administran los criterios originales.
- Siete PNG de IA decorativos, 960 × 600, reutilizados en nueve áreas y doce claves educativas con fuente. No sustituyen preguntas, caras, criterios ni estímulos oficiales; no son resultados clínicos. Procedencia y límites: `research/escalas_kawaii/registro_ilustraciones.json`.
- Filtros por población, área y tipo de acceso, y para ver solo las fichas con práctica, simulación o registro. Fuentes HTTPS abiertas únicamente por decisión explícita.
- Pausa opcional y movimiento reducido respetado. Sin cambios de paquete, esquema SQLite local ni sincronización personal automática.

## Pruebas ejecutadas sobre 0.9

| Comprobación | Resultado y alcance |
|---|---|
| JVM Android | **268/268** pruebas en **23 suites**, sin fallos ni omitidas. XML: `junit/`. Incluye catálogo, navegación, fuentes, estado vacío y limpieza de filtros; cuatro capturas de Compose en `render/` son Robolectric NATIVE, no capturas de teléfono. |
| Python | **93** guardas del repositorio, **15** del conversor y **21** del backend: **129/129** en los logs de esta ejecución. |
| SQLite / PostgreSQL emulado | **5/5** SQLite y **17/17** PGLite. PGLite no sustituye una revisión integral de un servidor de producción. |
| Lint Android | **0 errores y 26 advertencias**; detalle en `lint.xml`. No equivale a cero avisos ni a auditoría de seguridad. |
| PDF instrumentado | **2/2** `EducationalPdfDeviceTests` aprobadas en API28 con el APK final: PDF multipágina y selección de una página abiertos con `PdfRenderer`. Las salidas se identificaron como 4 y 1 páginas. No prueba un servicio de impresión real ni un flujo SAF. Registro: `apk/pdf-instrumentado.txt`. |
| Recursos del APK | **60/60** archivos de `app/src/main/assets` coinciden byte a byte con los incluidos en el APK; no incluye comparación de otros recursos compilados. Detalle: `apk/assets-apk.json`. |
| Filtro en emulador | El catálogo mostró **45** fichas; al pulsar «Solo con acceso: práctica, simulación o registro» mostró **13**. Ver `apk/02-catalogo-final.png` y `apk/03-filtro-aplicado.png`. El restablecimiento se probó en UI Robolectric, no manualmente en emulador. |
| Movimiento en emulador | Con modo Completo y animaciones de sistema habilitadas, dos capturas difieren en **4.203 píxeles** dentro de la región del dibujo; tras pulsar «Pausar dibujo», dos capturas de esa región son idénticas. `verificar_movimiento.py` reproduce la comparación, con cuatro capturas y hashes en `apk/movimiento/`. Con animaciones de sistema deshabilitadas, la portada mostró «Quieto»; `apk/01-portada-final.png`. No mide FPS ni rendimiento en teléfono físico. |

Se ejecutó en emulador **Android 9 / API28 x86_64 sin KVM**, pantalla física virtual 480 × 800 a 160 dpi. El frontend anunció 1536 MiB, pero QEMU se limitó con `-qemu -m 768` y el invitado informó 758.292 kB. Se presentó un aviso transitorio «System UI isn't responding» que se recuperó con «Wait»; el dispositivo fue lento y algunos volcados de accesibilidad devolvieron raíz nula. Las capturas se inspeccionaron directamente. En la ventana final de logcat consultada no se encontraron `FATAL EXCEPTION` ni `ANR in com.idreyk.enfermeria360.dev`; no demuestra que nunca ocurran.

## Actualización sin desinstalar

1. Se instaló el APK **0.8.0 debug**, luego se copió con `run-as` una base v2 y preferencias **exclusivamente sintéticas**, junto con un archivo PNG sintético, en la app de prueba. No son datos de pacientes; no se usó un teléfono personal.
2. Tras solicitar el arranque de 0.8 y detenerla se cotejó la copia de entrada con sus ocho tablas, preferencias y el archivo (`apk/fixture-en-080.json`). La interfaz inicial de 0.8 no se confirmó visualmente por la lentitud del emulador. El WAL de SQLite estaba vacío en la captura.
3. Se ejecutó `adb install -r` del **APK 0.9 final** con el mismo paquete y firmante, sin desinstalar ni borrar datos. Se compararon esquema, versión 2, `quick_check`, columnas y todas las filas de las ocho tablas; archivo de preferencias y PNG se compararon por SHA-256. Resultado: igualdad (`apk/conservacion-080-090.json`).
4. Se abrió 0.9, se visitaron portada, catálogo y filtro y se repitió la comparación tras detener la app; el WAL estaba vacío: mismas ocho tablas/filas, preferencias y archivo (`apk/conservacion-tras-abrir.json`). La muestra contenía 8 registros de contenido; 1 favorito, 1 medio, 3 entradas meta, 1 apunte, 1 nota, 1 paquete y 1 práctica, además de la preferencia sintética. No prueba todos los tipos de datos ni situaciones reales.

`crear_fixture.py` y `comparar_datos.py` documentan la reproducción de esta muestra. La primera herramienta necesita una base v2 sintética compatible; no usar bases de pacientes. La coincidencia del certificado es necesaria para esta actualización, pero no garantiza compatibilidad con un APK compilado en CI o Google AI Studio. Si un teléfono rechaza la firma, **no desinstalar ni borrar datos**: verificar la firma y hacer un respaldo antes de cualquier cambio.

## Identidad del APK

- Archivo entregado: `Enfermeria_iDreyK_360_v0.9.0_debug.apk`, **13.075.981 bytes**; aplicación `com.idreyk.enfermeria360.dev`; versión `0.9.0-dev-debug`, code 10; minSdk 26, targetSdk 35.
- SHA-256: `54aa3bb57f4161ebbb709f6fd59d94dc539098d3a24100fa5447536afe7f99a4`.
- Certificado debug SHA-256: `d6874c68417eefb1b1b902bf82ba4ce7d8734e47877e12bd1733eb498f0e522b`, igual al verificado para 0.8. La clave privada no forma parte del repositorio ni de esta entrega.
- El `BuildConfig` generado para esta compilación tenía URL y clave publicable de Supabase vacías. No se probó sesión real, OTP ni sincronización personal.
- Informe verificable: `apk-verificacion.json`. Solo se comprobó la instalación en emulador; no hay prueba de teléfono físico ni validación clínica.

## Pendiente

Revisión profesional de contenido e ilustraciones y adaptación a protocolos locales; derechos por instrumento y evaluación de versiones digitales; pruebas en hardware físico, impresión por servicio/SAF, accesibilidad y rendimiento fuera de este emulador; pruebas reales de OTP y nube si se configuran; importación y compilación en Google AI Studio. No publicar ni hacer push sin aprobación expresa. El espacio debe volver a medirse antes de cualquier entrega grande.
