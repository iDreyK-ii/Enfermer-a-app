# Enfermería iDreyK 360 — Android nativo v0.3

Aplicación Android en Kotlin y Jetpack Compose, integrada en el mismo proyecto del Prototipo B. **Desarrollo continuo, no producto de venta ni aplicación clínicamente certificada.** Funciona localmente, sin cuentas ni permiso de Internet. Android 8.0+ (API 26), compilación/objetivo API 35.

La ampliación v0.3 sustituye las pantallas preparatorias por herramientas de cálculo, edición y persistencia. No se ha confundido completar software con validar contenidos clínicos o conseguir permisos de instrumentos.

## Funciones y alcance real

| Área | Implementación actual | Límite importante |
|---|---|---|
| Diseño y navegación | Compose nativo; paleta, ilustración y tipografías del Prototipo B; transiciones, tema oscuro, texto ampliado y reducción de movimiento. | Sin WebView. Falta revisión visual y accesible en dispositivos reales. |
| Farmacología | Buscar, favoritos, crear, editar y eliminar fichas con 20 secciones; sinónimos, fuentes, licencia y procedencia. | Las seis fichas iniciales siguen vacías/en borrador. No se inventaron mecanismos, dosis, diluciones ni compatibilidades. |
| Revisión editorial | Una edición local vuelve a borrador, elimina la declaración de revisor anterior y conserva procedencia y referencias. | Las revisiones importadas son declaraciones del autor, no certificaciones de la app. |
| Glasgow adulto | Componentes ocular, verbal y motor; NV/pending impiden total; guardar práctica. | Sin diagnóstico, categoría de gravedad ni indicaciones. |
| Glasgow pediátrica | Registrar componentes y NV de una ficha externa identificada; sumar si procede. | No adapta automáticamente descriptores adultos ni reproduce una versión pediátrica completa. |
| EVA | Simulación digital de posición y registro de práctica. | No calibración física de 10 cm ni validación de un instrumento digital. |
| Capurro B | Cinco componentes, puntajes y fórmula 204 + suma; semanas y días. | Descriptores resumidos, referencia NTS 214-2024 anexo 4; no decide aplicabilidad ni exactitud obstétrica. |
| Capurro A | Registro de seis puntuaciones externas: cuatro somáticas sin pezón y dos neurológicas; constante 200. | No enseña maniobras. No mezclar variantes o nombres de materiales secundarios. |
| Apgar | Cinco componentes, momento de registro, suma 0–10 y guardado; cambiar momento limpia respuestas. | Sin pronóstico ni guía de reanimación. |
| Silverman-Andersen | Cinco componentes y suma 0–10, identificando la referencia NOM-007-SSA2-2016. | Sin clasificación automática ni indicación de soporte respiratorio. |
| Braden y Morse | Registro de total externo, fuente y contexto; validación de entero/rango. | **No se reproduce ni administra el cuestionario.** No se interpreta riesgo; permisos pendientes. |
| Medicación segura | Crear/editar listas de 1–100 ítems, reordenar, marcar, reiniciar y guardar el recorrido; institución, edición y fuentes. | No impone un número universal de correctos. La lista inicial de cinco básicos no es un protocolo completo. |
| Dosis a volumen | Aritmética con mg/g/mcg y UI; conversiones explícitas, comprobaciones numéricas y de dimensiones. | No selecciona dosis, medicamento o vía; no convierte UI a masa. Solo ejercicios ficticios. |
| Dilución | C1V1 = C2V2; volumen de solución inicial, volumen adicional teórico y volumen final; masa o UI en concentraciones compatibles. | Mezcla ideal/volumen aditivo; no reconstitución de polvos, compatibilidad, estabilidad ni pauta de administración. |
| IMC, goteo y conversor | Operaciones de práctica conservadas; resultados y guardado en historial. | Límites de entrada numéricos, no rangos de normalidad. |
| PAE / ADPIE | Planes editables por ficha ficticia: valoración, problema libre, resultados, planificación, ejecución, evaluación y referencias; estado del documento. | No genera diagnósticos ni reproduce NANDA-I, NIC o NOC. |
| Fichas ficticias | Mario, Rosa y Luis; notas, signos y balance; Gordon con once espacios. | No historias clínicas reales ni creación de identificadores reales. |
| Tendencias de signos | Gráfico de FC a partir de las últimas 20 lecturas ficticias guardadas; sin curva decorativa predeterminada. | Eje horizontal por orden de registro, no intervalos temporales proporcionales; sin alertas. |
| Apuntes | Crear, editar, buscar, etiquetar, eliminar y respaldar apuntes; se conservan las lecturas originales. | Solo texto propio o autorizado y datos inventados. |
| Casos y protocolos | Catálogos editables e importables, búsqueda de protocolos, fuentes y revisión visible. | No biblioteca clínica completa ni extracción automática de documentos. |
| Multimedia | Copiar PDF, imágenes, audio y video a almacenamiento privado; buscar, renombrar y quitar copias. | Abrir con visor compatible externo; no reproductor integrado ni contenido multimedia descargado/bundled. |
| Aprendizaje | Flashcards, quiz y casos; progreso e historial de práctica. | No certificación profesional. ECG sigue siendo animación ilustrativa sin sensores. |
| Persistencia | SQLite v2, migración aditiva desde v1; preferencias locales. | No cifrado clínico, roles, autenticación ni auditoría para registros asistenciales. |

## Datos, compatibilidad y respaldos

**La actualización mantiene el identificador `com.idreyk.enfermeria360.dev` y la clave de firma del APK v0.2.** La migración SQLite 1 → 2 crea `notebooks` y `media`; no recrea ni borra tablas de v0.2. Los antiguos checklists de cuatro tareas se conservan como registros de recorrido y no se hacen pasar por planes clínicos. La prueba de migración está automatizada, pero falta instalación/actualización en teléfono.

- **Catálogo:** JSON de esquema 2 con medicamentos, casos, listas y protocolos. La app lee esquema 1 y 2. v0.2 rechazará esquema 2; no se intenta que ignore protocolos silenciosamente. Máximo 2.000.000 bytes, 500 registros agregados y 300 por tipo. Un respaldo vacío es válido.
- **Cuaderno:** formato separado `tipo_respaldo: cuaderno_practica`, versión 1, con planes y apuntes. Hasta 200 documentos y 2.000.000 bytes agregados. Importación con vista previa/confirmación, reemplazo solo de IDs coincidentes y deshacer último cambio.
- **Multimedia:** hasta 20 MiB por archivo, 100 MiB en total y 200 archivos; copia por streaming y nombres internos aleatorios. No forma parte de los JSON. Conserva los archivos originales.
- **No existe una copia completa del dispositivo:** las notas de las fichas ficticias/Gordon/signos/balance de v0.2, favoritos, historial de prácticas y preferencias no se exportan con esos JSON.
- Exportaciones legibles, no cifradas. Exportar no concede derechos adicionales. El respaldo externo del catálogo conserva atribuciones, pero no restaura el control de versiones de paquetes originales; el deshacer interno sí lo conserva.
- Restablecer borra catálogo editado/importado, protocolos, cuaderno, copias multimedia, notas, favoritos e historial, previa confirmación. Desinstalar o borrar datos también elimina almacenamiento local. **No desinstalar v0.2 para actualizar.** No hay migración inversa para volver a v0.2.

## Compilar y probar

Requisitos: JDK 17/21, SDK Platform 35. Gradle 8.9, AGP 8.7.3, Kotlin 2.0.21, Compose BOM 2024.12.01. Las descargas de compilación no implican conexión de la app instalada.

```sh
./gradlew --no-daemon --max-workers=1 compileDebugUnitTestKotlin \
  '-Dorg.gradle.jvmargs=-Xmx640m -XX:MaxMetaspaceSize=384m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
./gradlew --no-daemon --max-workers=1 testDebugUnitTest \
  '-Dorg.gradle.jvmargs=-Xmx384m -XX:MaxMetaspaceSize=256m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
./gradlew --no-daemon --max-workers=1 assembleDebug lintDebug \
  '-Dorg.gradle.jvmargs=-Xmx640m -XX:MaxMetaspaceSize=384m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
```

APK generado en `app/build/outputs/apk/debug/app-debug.apk`. En Windows: `gradlew.bat`. Configura tu SDK en `ANDROID_HOME` o `local.properties` (este último no se distribuye). El wrapper verifica la descarga de Gradle por SHA-256.

En el entorno de desarrollo actual se conservan las herramientas en `/home/user/.cache/`; se usa `GRADLE_USER_HOME=/home/user/.cache/idreyk-gradle`. Robolectric descarga dependencias a `.cache/robolectric-maven/` del proyecto. No llevar esos cachés al ZIP. No compilar y ejecutar un emulador pesado simultáneamente en este entorno limitado.

```sh
cd content-tools
python -m pip install -r requirements.txt
python -m unittest -v test_converter
python convertir_plantilla.py Plantilla_contenidos_iDreyK_360.xlsx salida.json
```

`qa/RESULTADOS.md` y `qa/resultados.json` contienen los resultados exactos ejecutados. Pruebas de interfaz y persistencia en Robolectric/API 28: **no equivalen a APK probado en teléfono ni a validación clínica**. No se declara éxito de emulador completo o instalación física.

## Organización

- `MainActivity.kt`, `Design.kt`: navegación, SAF, tema y componentes.
- `Models.kt`, `Repository.kt`: catálogo, validación, SQLite/transacciones, migraciones y ViewModel con E/S serializada.
- `ScaleModels.kt`, `ScreensScales.kt`: escalas, registro externo, fuentes y resultados.
- `DoseMath.kt`, `ScreensDose.kt`: aritmética dimensional y formularios.
- `NotebookModels.kt`, `ScreensNotebook.kt`: planes PAE, apuntes y respaldos.
- `ScreensAuthoring.kt`: editores de fichas, listas, casos y protocolos; procedencia.
- `MediaFiles.kt`, `ScreensMedia.kt`: copia local y apertura externa con FileProvider restringido.
- `PracticeTrends.kt`: series de FC de registros guardados, sin datos predeterminados.
- `ScreensLibrary.kt`, `ScreensPractice.kt`: módulos principales y funciones conservadas.
- `app/src/test/`: pruebas matemáticas, estructurales, SQLite/migración y navegación real Compose simulada.
- `content-tools/`: plantilla XLSX, esquemas 1/2, conversor y ejemplos no clínicos.
- `research/`, `qa/`, `design/`: fuentes, evidencias y recurso original de diseño.
- `DESARROLLO.md`: continuidad, historial y recuperación del proyecto.

## Seguridad y tareas pendientes

Sin permisos de Internet, ubicación, cámara, micrófono ni acceso general a almacenamiento. SAF accede a lo elegido por el usuario. FileProvider no exportado, limitado a `files/biblioteca/`; apertura externa con permiso temporal de lectura, no escritura. Los visores externos tienen sus propias condiciones y pueden usar red; la app no controla esas aplicaciones.

Copias automáticas deshabilitadas y reglas de extracción restringidas. No telemetría ni cuentas. El aislamiento Android no convierte esta edición en una historia clínica segura o conforme a normativa.

Pendientes: revisión clínica independiente de cada instrumento y cálculo; fuentes y permisos de contenido clínico; autorización de instrumentos restringidos; pruebas físicas de instalación, actualización, SAF y visores; accesibilidad/rotación/cargas grandes/fallo de disco; auditoría de privacidad y seguridad; firma de publicación y política de actualización. No se han implementado IA, extracción Word/PDF, sincronización remota ni integración editorial externa.

Ilustración original generada con IA. Outfit y Nunito Sans bajo OFL, licencias incluidas. Se enlazan fuentes documentales; no se empaquetan manuales clínicos completos ni se presume autorización por disponibilidad pública.
