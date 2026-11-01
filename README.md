# Enfermería iDreyK 360 · Android nativo

**Prototipo B · edición de desarrollo 0.2.0-dev-debug · 1 de noviembre de 2026**

**Desarrollo en curso:** seguimos construyendo y mejorando el aplicativo, sin enfocarlo como un producto de venta. La APK actual es una versión de trabajo, no una aplicación terminada.

Consulta **[DESARROLLO.md](DESARROLLO.md)** para distinguir código permanente y cachés, ver el estado de cada módulo y recuperar el historial incluido en el ZIP. Este es el proyecto principal sobre el que continuarán las mejoras.

Aplicación Kotlin + Jetpack Compose. No es un WebView ni un acceso directo al prototipo HTML. Conserva la identidad pastel, las tarjetas redondeadas, la ilustración, la navegación inferior y las tipografías Outfit / Nunito Sans.

> Esta entrega desarrolla el software y el flujo de contenidos. No constituye una versión clínica validada, una historia clínica electrónica ni una garantía de funcionamiento en todos los teléfonos. Utilizar únicamente datos ficticios.

## Instalación

- Requiere **Android 8.0 o superior**; SDK mínimo 26 y SDK objetivo 35.
- El APK de desarrollo se entrega por separado como `Enfermeria_iDreyK_360_v0.2_debug.apk`.
- Copia o descarga el APK en tu teléfono, ábrelo y confirma la instalación. Si Android lo solicita, autoriza temporalmente a ese navegador o gestor de archivos a instalar aplicaciones. Mantén Play Protect activo y revoca después el permiso de instalar desde esa fuente.
- No necesitas Android Studio para instalar el APK. La app no pide una cuenta ni tiene permiso de internet. Su catálogo inicial está incluido.
- No es una publicación de Google Play. Su firma es de desarrollo. No se entrega una clave de producción.
- Si recompilas con otra clave, Android podría exigir desinstalar la versión anterior. **Desinstalar elimina los datos internos.** Exporta antes el catálogo; esa exportación no incluye notas, favoritos ni preferencias.

## Implementado en esta edición

| Área | Alcance actual |
|---|---|
| Experiencia B | Pantallas nativas, navegación atrás, tarjetas, transiciones, modo oscuro, texto ampliado y reducción de animaciones. |
| Farmacología | Búsqueda, filtros, favoritos, detalle por pestañas y 20 campos preparados. Seis fichas iniciales en borrador, sin pautas clínicas rellenadas. |
| Glasgow | Selección de componentes adultos; suma de 3 a 15 cuando todos son valorables. Un componente NV o no seleccionado impide mostrar un total. Sin clasificación de gravedad ni decisiones terapéuticas. |
| EVA | Simulación digital de una posición continua 0–100. No se presenta como una regla física calibrada de 100 mm ni como un instrumento validado en el dispositivo. |
| Capurro | Pantalla A/B y espacio documental. Puntuación y estimación de edad gestacional deshabilitadas. |
| Seguridad | Listas importables de 1 a 100 ítems. No se fija 20 como estándar universal ni se autoriza administrar un medicamento al completar una lista. |
| PAE / Gordon | Plantillas y registros de práctica. Sin reproducción de taxonomías clínicas propietarias. |
| Pacientes | Tres personas ficticias; notas, registros de signos, balance y planes locales separados por identificador. No permite crear una historia clínica real. |
| Herramientas | Aritmética de IMC, conversión de unidades y goteo de ejemplo. Dosis y dilución deshabilitadas. ECG puramente ilustrativo, sin sensores. |
| Aprendizaje | Tarjetas con posición guardada, cuestionario breve, material original de práctica y casos importables. |
| Biblioteca | Selector Android de archivos, validación, previsualización, confirmación, importación transaccional, exportación del catálogo y deshacer la última importación. |
| Persistencia | SQLite y preferencias privadas de la app. Notas, favoritos, prácticas y ajustes sobreviven al cierre normal. |

Los apartados pendientes tienen mensajes explícitos; no se presentan como protocolos o funciones médicas terminadas. La gráfica de signos es decorativa: no se calcula a partir de los registros.

## Compilar y probar

Requisitos: JDK 17 o 21, Android SDK Platform 35 y herramientas de compilación. La compilación inicial descarga dependencias; eso **no** significa que la app necesite conexión al funcionar.

1. Abre esta carpeta en Android Studio o configura `ANDROID_HOME`.
2. Si lo necesitas, crea tu propio `local.properties` con `sdk.dir=...`.
3. Ejecuta:

```sh
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

En Windows utiliza `gradlew.bat`. El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`. El wrapper está incluido y verifica la distribución de Gradle con SHA-256.

Las bibliotecas de Robolectric se descargan en `.cache/robolectric-maven/`, dentro del proyecto. Son caché regenerable y se excluyen del guardado y del ZIP de código. En este entorno, conserva también las herramientas bajo `/home/user/.cache/`: al compilar, usa `GRADLE_USER_HOME=/home/user/.cache/idreyk-gradle` y `ANDROID_HOME=/home/user/.cache/android-sdk`.

En una máquina con memoria limitada, separa compilación y ejecución de pruebas:

```sh
./gradlew assembleDebug compileDebugUnitTestKotlin lintDebug \
  '-Dorg.gradle.jvmargs=-Xmx640m -XX:MaxMetaspaceSize=384m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
./gradlew testDebugUnitTest \
  '-Dorg.gradle.jvmargs=-Xmx384m -XX:MaxMetaspaceSize=256m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
```

Las pruebas de repositorio e interfaz usan **Robolectric / Android 9 simulado en JVM**. No sustituyen las pruebas con APK instalado, diferentes fabricantes, versiones Android y tamaños de pantalla. Consulta `qa/RESULTADOS.md` para conocer exactamente lo ejecutado, sus resultados y las limitaciones.

Versiones fijadas: Gradle 8.9, AGP 8.7.3, Kotlin 2.0.21 y Compose BOM 2024.12.01. Antes de producción deben revisarse actualizaciones de dependencias, SDK objetivo y políticas vigentes de distribución.

## Organización del proyecto

- `Models.kt`: modelos, validación de paquetes y aritmética de práctica.
- `Repository.kt`: SQLite, metadatos, transacciones, punto de deshacer y ViewModel.
- `MainActivity.kt`: entrada, navegación, selector/creador de documentos y confirmaciones.
- `Design.kt`: paleta, tipografías y componentes reutilizables.
- `ScreensLibrary.kt`: inicio, farmacología, escalas, seguridad y biblioteca.
- `ScreensPractice.kt`: PAE, datos ficticios, herramientas y aprendizaje.
- `app/src/main/assets/contenido_inicial.json`: biblioteca incluida al instalar.
- `app/src/test/`: pruebas de lógica, persistencia e interfaz simulada.
- `content-tools/`: plantillas, conversor XLSX → JSON, esquema y documentación.
- `design/`: original editable de la ilustración; el APK usa la versión optimizada de los recursos.
- `research/`: referencias preparatorias y límites de la revisión documental.
- `qa/`: evidencias de pruebas y lista de verificación en teléfono.

## Seguridad y alcance de los respaldos

No se solicitan permisos de internet, cámara, micrófono, ubicación o acceso general a archivos. La biblioteca usa el selector de documentos de Android y únicamente accede al archivo que el usuario elige. AndroidX puede añadir un permiso interno de firma para receptores no exportados; no es un permiso de acceso a tus datos.

Se deshabilitan las copias automáticas y se excluyen los dominios de almacenamiento de las reglas de extracción y transferencia Android 12+. No hay telemetría ni servicios remotos implementados. El aislamiento normal de Android no equivale a cifrado clínico propio, autenticación biométrica, controles de roles o cumplimiento normativo para historias clínicas.

**Exportar catálogo** incluye fichas, casos, listas y metadatos de procedencia. Es JSON legible, no cifrado. No exporta notas de práctica, favoritos, historial de ejercicios ni ajustes. Una importación de respaldo conserva la autoría original, pero se registra como un paquete nuevo: no restaura el control de versiones de los paquetes de origen. **Deshacer**, en cambio, restaura el catálogo y su control de versiones previos dentro de una transacción local.

En esta edición, el paquete **y el catálogo agregado** deben caber en 2.000.000 bytes, 500 registros totales y 300 por tipo. Se rechaza una operación que supere los límites, para conservar la posibilidad de exportar y deshacer sin archivos ilegibles.

## Antes de un uso clínico o de producción

Faltan revisión profesional de criterios, fórmulas y contenido; autorización de fuentes; protocolo institucional aplicable; pruebas instrumentadas y en teléfonos; auditoría de seguridad y accesibilidad; migraciones de base de datos; gestión de errores de almacenamiento; firma de publicación y estrategia de actualización. Si se plantea registrar pacientes reales, hace falta otro diseño de privacidad, cifrado, acceso y cumplimiento normativo.

La futura integración editorial puede ser **carpeta → conversión estructurada → revisión humana → paquete aprobado → app**. No se han implementado lectura automática de Word/PDF, IA dentro de la aplicación, sincronización con Google Drive ni publicación automática.

## Recursos

Ilustración de enfermero generada con IA para el prototipo. Licencias de Outfit y Nunito Sans incluidas en los assets. Las fuentes documentales se citan como referencias; no se empaquetan manuales clínicos completos ni se presume autorización de redistribución por estar disponibles públicamente.
