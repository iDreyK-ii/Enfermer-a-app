# Cierre de verificación — Atlas v2.2 / Android 0.7.0

Fecha: **2026-11-03**. Paquete `com.idreyk.enfermeria360.dev`, versión `0.7.0-dev-debug`, code **8**, minSdk 26 / target 35. **Entrega local, no publicada.** Última revisión publicada: `b463c580f6e965d971116ef593df0059cb105c86` (0.6.0). No hay CI remoto nuevo ni verificación en Google.

## 1. Resultado ejecutado, no estimado

| Verificación | Resultado | Evidencia |
|---|---:|---|
| JVM / Android simulado | **252/252**, 21 suites, 0 fallos, 0 errores, 0 omitidas | `junit/`, `logs/`, `resultado.json` |
| Python: guardas, conversión y publicación | **118**: 82 + 15 + 21 | Logs de `scripts/comprobar.sh --todo` |
| SQLite de backend | **5** | Log de QA local |
| PostgreSQL/RLS en PGLite | **17** | Log de QA local; no es una consulta al servidor remoto |
| Lint | **0 errores / 26 advertencias** | `lint.xml`: 7 GradleDependency y 19 UnusedResources |
| APK principal y APK de pruebas | Compilados | Logs Gradle; APK principal verificado |
| PDF instrumentado en Android real emulado | **2/2**, 6,807 segundos de tests | `pdf-instrumentado.txt`, dos PDF de salida reales |
| Capturas de APK instalado | **8**, Android 9/API 28 | `apk/*.png` y JSON individuales |
| Renders de código de producción | **17**, Robolectric/API 35 | `capturas/*.png` y JSON individuales |
| Conservación de código probado | **160 hashes** registrados, sin cambios posteriores de esas fuentes | `fuentes_probadas.json` |
| Assets del APK frente al árbol probado | **18/18 idénticos** | Comparación de bytes de `assets/` del ZIP APK |

El cierre pasó después de dos cambios finales: búsqueda por `technicalName` y eliminación del fondo opaco del viewport, con selección acotada y centrado. El resultado verde intermedio de 251 pruebas **no sustituye** este resultado de 252.

### Fallo encontrado y corregido

La primera suite completa encontró un fallo real en identificación: el Snackbar de éxito al guardar una respuesta tapaba «Siguiente parte», de modo que la siguiente opción seguía sin acción de clic. Se corrigió **solo para el atlas**, que ya muestra retroalimentación persistida en la ficha. Se conservan las notificaciones de error y las de otros flujos.

El test continúa usando clics reales, comprueba acción de clic, ausencia del mensaje que obstruía, respuestas, guardado, reanudación y reinicio. Pasaron la prueba focalizada, la suite completa y, finalmente, el avance de pregunta 1 a 2 por toque en el APK instalado (capturas 07–08). No se ocultó el error eliminando la aserción ni cambiando la prueba por una llamada directa al ViewModel.

## 2. Qué se ejecutó en Android

### Entorno y arranque

- Emulador 37.2.13, Android 9/API 28 x86_64, **sin KVM**, CPU emulada y SwiftShader; host de aproximadamente 1982 MiB, sin swap.
- Intento 1: el emulador forzó 1536 MiB, dejó al host casi sin memoria y no terminó la comprobación de arranque. Se detuvo.
- Intento 2: `-memory 768 ... -qemu -m 768`, Vulkan desactivado, dos núcleos, 480×800 a 160 dpi. Aunque el frontend imprimió 1536 MB, `/proc/meminfo` del invitado confirmó **758292 kB**. Arranque completado en **158,548 s**. Gradle estaba detenido.
- Se instaló la versión conservada 0.6, se cerró la bienvenida y se cambió el tema. Después se aplicó **`adb install -r`** con el APK 0.7. **No se desinstaló ni borró la aplicación para forzar compatibilidad de firma.**

### Observaciones y evidencia acotada

1. Inicio 0.7 visible, oscuro, sin repetir bienvenida: `01-inicio-apk.png`.
2. Búsqueda offline por el prefijo efectivamente mostrado `jeri`, con resultado de teoría y de dispositivo: `02-busqueda-prefijo-apk.png`. La entrada automatizada intentó `jeringa`, pero no terminó mostrando la palabra completa; no se afirma prueba satisfactoria del teclado completo.
3. Apertura de la jeringa PNG y su cilindro: `03-jeringa-png-apk.png`.
4. Toque sobre el pistón en la imagen, cambio de etiqueta y detalle: `04-jeringa-piston-apk.png`.
5. Reconfiguración del espacio lógico a 800×1280: aparece el panel lateral y se conserva la selección: `05-jeringa-tablet-apk.png`.
6. Inicio de identificación, selección de respuesta, retroalimentación y avance a pregunta 2: `06`–`08`.
7. Después de `force-stop`, extracción **solo de la base sintética de QA**: `learning:atlas/jeringa`, versión `atlas-v22.1`, `answers:[0]`, `completed:false`. Evidencia: `persistencia-identificacion-apk.json`.
8. Esquema SQLite v2 y las mismas ocho tablas; siete tablas no-meta idénticas, entrada meta anterior conservada y una entrada nueva de aprendizaje. `conservacion-db-apk.json`. No había cuadernos ni favoritos previos en este fixture; no se extrapola a todas las combinaciones de datos de usuario.

**Dimensiones importantes:** las ocho capturas originales son **480×800 píxeles**. En 05–08 se utilizó `wm size 800x1280`: el emulador reescala esa superficie lógica y añade bandas dentro de su pantalla física de 480×800. Es prueba de recomposición al ancho lógico de tablet, **no una captura nativa de 800×1280 ni una tablet física**. Los renders Robolectric de tablet sí tienen las dimensiones de bitmap indicadas en sus JSON; son otra clase de evidencia.

Las capturas instaladas se obtuvieron con `adb exec-out screencap -p`, sin recortes, retoques ni sustitución del contenido. Sus metadatos vinculan el APK exacto por SHA-256.

### PDF: avance respecto de 0.6

Las dos pruebas `EducationalPdfDeviceTests` se ejecutaron con AndroidJUnitRunner sobre el APK instalado:

- `longEducationalDocumentProducesAReadableMultipagePdf`.
- `selectedZeroBasedPageProducesOnlyOneReadablePdfPage`.

Ambas pasaron. Se conservan **`idreyk-export-qa.pdf`** y **`idreyk-export-qa-selected.pdf`**, extraídos del cache privado de esa ejecución sintética. El propio test abre los resultados con **Android PdfRenderer**; no son PDF generados externamente para simular el éxito del aplicativo.

Esto verifica el escritor y la selección de páginas en ese entorno. **No verifica aún** el recorrido completo del usuario por el servicio de impresión, SAF, proveedores externos, impresora física ni recuperación de archivos en un teléfono real.

### Problemas del entorno que no se ocultan

- Durante el primer arranque de la versión previa apareció «System UI isn't responding»; se eligió esperar y el sistema continuó. No se presenta esa captura antigua como evidencia de la versión nueva.
- `am start -W` agotó su espera; la interfaz se dibujó después. Los logs se conservan, no se reescriben como arranques rápidos.
- UIAutomator devolvió `null root` intermitentemente. Se revisaron las capturas actuales; los árboles fallidos se identifican como errores, **no se reutilizan XML viejos como comprobación de una pantalla nueva**.
- Entrada automatizada lenta/incompleta (`jeri` observado). Hace falta reproducir la edición/IME en hardware viable; no se atribuye con certeza exclusivamente al emulador o a la app.
- Esta emulación no permite concluir FPS, consumo, fluidez, TalkBack, contraste exhaustivo, teclado, proceso muerto en todos los flujos, paridad física de diseño ni compatibilidad con todas las versiones Android.

## 3. Alcance del atlas

**5 PNG integrados / 24 solicitados.** Jeringa, aguja, estetoscopio, equipo de venoclisis y tensiómetro aneroide. Siguen `PARCIAL` en la aceptación integral, porque no tienen aprobación profesional y no cumplen detalle nativo de 2000 px.

**19 PENDIENTES:** catéter, bureta y llave tuvieron candidatos rechazados; los otros 16 no se generaron. No se reclasifican como FUTURO. Las 12 solicitudes futuras de la sección 5 son un grupo distinto. La matriz exacta está en `inventario_24_png.json` y en la entrega visual.

- Cinco PNG RGBA y cinco JSON de imagen; tres JSON adicionales explícitamente pendientes.
- 33 puntos visibles comprobados sobre el material del recorte. Partes ocultas no tienen puntos ni preguntas.
- Autoevaluación: **cuatro objetivos en aguja, cinco en los otros cuatro dispositivos**; opciones, corrección, score, persistencia y reinicio. No son pruebas clínicas.
- Resolución original 1376×768 o 1408×768; exportación de 2000 px por ampliación declarada. Alfa real no equivale a precisión anatómica.
- Investigación y procedencia en `research/atlas_v22/REVISION_TECNICA.md` y sidecars. Algunas lecturas completas fueron posteriores a la primera generación; no se altera la cronología. Referencias del mismo fabricante no cuentan como revisión profesional independiente.
- Persona aprobadora de precisión: **pendiente**, nunca inventada.
- Se retiró el atlas vectorial y las manos esquemáticas; higiene conserva pasos y timer, con aviso de recurso anatómico pendiente.

## 4. APK y firma

- SHA-256: `f19d11b037e08fc3d6034ca7f8802afc545f8db9773f105af2e947a1838a1f0d`.
- Tamaño: **14546975 bytes**.
- Certificado SHA-256: `d6874c68417eefb1b1b902bf82ba4ce7d8734e47877e12bd1733eb498f0e522b`, igual al local conservado.
- `apk_verificado.json`: **`certificate_matches_expected:true`**, verificado suministrando expresamente el certificado esperado. El test-APK lleva el mismo certificado.
- El log automático de `--todo` contiene `certificate_matches_expected:false`: en ese comando no se había pasado el argumento esperado y el script devuelve `bool(expected_certificate)`. **No era una diferencia de firma.** Se conservó el log y se repitió correctamente la verificación explícita, además de probar la actualización `-r`.

No se incluye la clave privada en el ZIP. Google/CI pueden firmar con otra clave: **nunca desinstalar ni borrar datos para resolverlo**. Investigar la firma y conservar/respaldar los datos antes de cualquier instalación en equipo del propietario.

## 5. Reproducción y lectura de pruebas

```sh
# JDK 21, SDK 35/build-tools 35, Gradle 8.9
python3 -m pip install -r content-tools/requirements.txt
npm ci --prefix backend --ignore-scripts
bash scripts/comprobar.sh --todo
python3 scripts/verificar_apk.py --certificado d6874c68417eefb1b1b902bf82ba4ce7d8734e47877e12bd1733eb498f0e522b

# Solo con dispositivo/emulador propio viable y firma compatible:
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w -r -e class com.idreyk.enfermeria360.EducationalPdfDeviceTests \
  com.idreyk.enfermeria360.dev.test/androidx.test.runner.AndroidJUnitRunner

# Visor offline; evidencia, no otra implementación de la app:
python3 qa/atlas_v22/generar_visual.py --output revision_atlas.html
```

`--todo` compila el APK instrumentado, pero no lo ejecuta por sí mismo. En este cierre se realizó la ejecución adicional indicada. El último `--todo` está conservado en `logs/`; los logs de intentos fallidos/intermedios permanecen fuera de los resultados finales.

## 6. Límites de entrega

Se preservan Prototipo B, rutas, contenido previo, matemáticas educativas, escalas, PAE/cuaderno, casos, SQLite y uso local opcional. La tabla completa de funcionalidades está en `docs/AUDITORIA_ATLAS_v22.md`.

Permanecen abiertos: 19 PNG, revisión anatómica/educativa/clinico-profesional, mayor resolución nativa, permisos/versiones de instrumentos, farmacología clínica completa al final, pruebas físicas/accesibilidad/servicio de impresión, OTP y plantillas no guardadas, configuración/sesión remota E2E y paridad de importación Google. Ninguna prueba local certifica el estado actual del backend remoto.

**No es producto clínico certificado. No usar pacientes identificables ni tomar decisiones asistenciales a partir de imágenes generadas. No se publica esta actualización sin autorización expresa.**
