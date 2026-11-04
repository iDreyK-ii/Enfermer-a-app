> **Revisión corriente 0.9.1-dev:** evidencia y límites en `qa/pulido_final/RESULTADOS.md`; no publicar sin autorización expresa. La QA 0.9.0 es histórica.

> **Antecedente 0.8.0-dev:** resultados y límites en `qa/atlas_kawaii/RESULTADOS.md`; no publicar sin autorización expresa.

> Histórico 0.7.0, no evidencia automática de 0.8: 252 JVM, 118 Python, 5 SQLite y 17 PGLite; lint 0 errores/26 advertencias. Ocho capturas del APK en emulador API28, 17 renders Robolectric separados y 2/2 pruebas PDF instrumentadas aprobadas. No hardware físico, no nuevo CI/publicación. Detalle: `qa/atlas_v22/RESULTADOS.md`.

# Compilar, probar e instalar iDreyK 360 v0.9.1-dev (local)

Ejecutar desde la raíz `idreyk-android/`. Es Android nativo: **no hay servidor web ni puerto que levantar**. Gradle compila la app; Python y Node solo se usan para herramientas y pruebas locales.

## Preparar herramientas

JDK17/21, Android SDK Command-line Tools, Python3.10+ y Node para el harness PostgreSQL. Android Studio es otra forma de gestionar JDK/SDK.

```sh
cp -n .env.example .env
# Editar rutas reales. No sobrescribir una configuración privada existente.
set -a
. ./.env
set +a

"$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" --licenses
"$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" \
  "platforms;android-35" "build-tools;34.0.0" "build-tools;35.0.0" "platform-tools"
chmod +x gradlew scripts/comprobar.sh
python3 -m pip install -r content-tools/requirements.txt
npm ci --prefix backend --ignore-scripts
```

El desarrollador revisa/acepta las licencias; copiar los comandos no constituye aceptación. `local.properties`, si existe, debe usar el mismo SDK. En Windows preparar SDK/JDK con Android Studio y usar `gradlew.bat`; el script Bash requiere un entorno compatible.

## Comprobación completa reproducible

Si partiste del ZIP sin `.git`, recupera primero `historial/desarrollo.bundle` en otra carpeta (ver la sección de recuperación en `docs/GITHUB_Y_CI.md`). La guarda revisa archivos versionados y exige el Git del propio proyecto, no uno de una carpeta superior.

```sh
bash scripts/comprobar.sh --todo
```

Ejecuta guardas y pruebas de archivos, SQLite y PostgreSQL local, compilación Kotlin, ensamblado de pruebas instrumentadas (sin ejecutarlas), pruebas Android/JVM, lint, APK y verificación de firma/metadatos/permisos. `--rapido` omite Android. Logs en `.cache/qa-actual/`.

**Para aislar las pruebas, `--todo` fuerza URL/clave publicable vacías.** No envía correos ni usa cuentas reales. El APK que deja ese comando es de configuración local. La documentación de CI explica la diferencia entre un APK de runner y uno firmado para actualizar: `docs/GITHUB_Y_CI.md`.

## Comandos Android por separado

```sh
./gradlew --no-daemon --max-workers=1 :app:compileDebugUnitTestKotlin \
  '-Dorg.gradle.jvmargs=-Xmx640m -XX:MaxMetaspaceSize=384m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
./gradlew --no-daemon --max-workers=1 :app:testDebugUnitTest \
  '-Dorg.gradle.jvmargs=-Xmx384m -XX:MaxMetaspaceSize=256m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
./gradlew --no-daemon --max-workers=1 :app:assembleDebug :app:lintDebug \
  '-Dorg.gradle.jvmargs=-Xmx640m -XX:MaxMetaspaceSize=384m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
python3 scripts/verificar_apk.py
```

APK: `app/build/outputs/apk/debug/app-debug.apk`. La primera compilación descarga dependencias. Desde v0.4 el APK **sí declara INTERNET** para el conector opcional; sin configuración de nube no realiza sus solicitudes. No requiere contactos, cuentas del sistema, cámara ni almacenamiento general.

## Activar nube, separado de la comprobación local

**Actualización de infraestructura:** el servidor `idreyk-360-dev` ya está creado. No repetir el SQL inicial ni crear otro proyecto para esta app. Ver `docs/SUPABASE_REAL_Y_GITHUB.md`. Se puede validar la configuración con `python3 scripts/compilar_con_supabase.py` y compilar explícitamente agregando `--compilar` una vez preparados herramientas y firma; esta compilación no se ejecutó en la fase de servidor.


Gradle lee `IDREYK_SUPABASE_URL` e `IDREYK_SUPABASE_PUBLISHABLE_KEY` de entorno o propiedades Gradle; `.env` no se carga automáticamente. Ambas vacías mantienen la app local. Ambas válidas habilitan el conector.

Para otro proyecto nuevo autorizado, crear/configurar RLS y correo siguiendo `backend/README.md`; para el ya creado, seguir el estado y los pendientes de la guía actual. Después compilar con `:app:assembleDebug` y los valores públicos reales, **sin los argumentos que los vacían en el script de pruebas**. Nunca incorporar la clave secreta, `service_role`, contraseña PostgreSQL o clave privada de firma.

La compilación local de prueba 0.9.1 usa nube vacía; no confundirla con el APK 0.9.0 conservado. No se ha ejecutado la compilación conectada en esta fase. No existe una conexión simulada presentada como real. La integración real necesita su propio plan de pruebas y consentimiento; no reutilizar cuentas reales desde pruebas unitarias.

## Instalar sin perder datos

Con un único dispositivo de prueba conectado, depuración USB autorizada y ADB:

```sh
"$ANDROID_HOME/platform-tools/adb" devices
"$ANDROID_HOME/platform-tools/adb" install -r app/build/outputs/apk/debug/app-debug.apk
"$ANDROID_HOME/platform-tools/adb" shell am start \
  -n com.idreyk.enfermeria360.dev/com.idreyk.enfermeria360.MainActivity
```

**Firma:** los APK locales entregados hasta 0.9.0 y la compilación temporal 0.9.1 de esta revisión comparten el certificado verificado. Una compilación de otra máquina/CI normalmente tiene otra clave debug y no puede actualizar la anterior. No desinstalar ni borrar datos para resolver un conflicto de firma. Exportar catálogo/cuaderno y conservar medios por separado antes de pruebas importantes.

Para comparar un certificado conocido usar `IDREYK_EXPECTED_CERT_SHA256` con `scripts/verificar_apk.py`; el valor es la huella pública, no una clave. No se ha probado aún instalación física ni downgrade; no forzar vuelta a versiones anteriores.

## Evidencia y alcance

Ver `qa/escalas_kawaii/RESULTADOS.md` y `research/escalas_kawaii/REVISION_2026-11-03.md`. Los informes de 0.8.0, 0.7.0 y 0.6.0 son históricos. Pruebas JVM/Robolectric, no certificación física o clínica. SMTP/Auth/Storage reales, hardware Keystore, FPS y publicación de producción siguen pendientes. Las capturas Robolectric se distinguen de cualquier ejecución instalada. Consultar procedencia por archivo y limitaciones concretas en el informe actual.


## Capturas y pruebas instrumentadas: reproducción y límites

La QA crea PNG/JSON en `.cache/native-mvp/` con Robolectric NATIVE/API35, `View.draw`, sin dispositivo. El tmpdir de tests está en `.cache/test-tmp/` (disco), evitando bibliotecas grandes en un `/tmp` tmpfs; las suites usan forks secuenciales. No se necesita KVM para esta QA; por separado, la versión 0.9 se probó en un emulador API28 sin KVM, no en teléfono físico.

```sh
# Compila y empaqueta las dos pruebas PDF: NO las ejecuta.
./gradlew :app:assembleDebugAndroidTest
# Solo con dispositivo/emulador de prueba autorizado, firmado/configurado de forma compatible:
./gradlew :app:connectedDebugAndroidTest
```

`EducationalPdfDeviceTests` comprueba generación y selección de página mediante el framework real. En 0.9.1 se ejecutaron 2/2 en emulador API28 sobre la compilación temporal; el escritor nativo PdfDocument no está implementado por Robolectric 4.14.1. Después debe comprobarse también el servicio de impresión/selector SAF, que no es lo mismo que invocar el generador.

Ejercitar teléfono, tablet y un equipo de recursos limitados; rotación, texto grande, teclado, TalkBack, paso a segundo plano del cronómetro, importación cancelada, guardado y actualización sin pérdida de datos. No atribuir resultados aún no obtenidos.
