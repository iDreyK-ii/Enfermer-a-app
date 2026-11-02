# Compilar, probar e instalar iDreyK 360

Todos los comandos se ejecutan desde la raíz `idreyk-android/`, no desde `docs/`. Esta guía no cambia las versiones fijadas ni presupone que las herramientas estén instaladas.


No hay `npm install`, servidor que levantar ni puerto 3000. Gradle resuelve las dependencias Android. Instala previamente JDK 17/21 y Android SDK Command-line Tools (también puedes hacerlo con Android Studio).

**Linux/macOS, desde la raíz del proyecto:**

```sh
cp .env.example .env
# EDITA .env: JAVA_HOME y ANDROID_HOME deben ser rutas reales antes de continuar.
set -a
. ./.env
set +a

"$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" --licenses
"$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" \
  "platforms;android-35" "build-tools;35.0.0" "platform-tools"
chmod +x gradlew

./gradlew --no-daemon --max-workers=1 :app:compileDebugUnitTestKotlin \
  '-Dorg.gradle.jvmargs=-Xmx640m -XX:MaxMetaspaceSize=384m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
./gradlew --no-daemon --max-workers=1 :app:testDebugUnitTest \
  '-Dorg.gradle.jvmargs=-Xmx384m -XX:MaxMetaspaceSize=256m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
./gradlew --no-daemon --max-workers=1 :app:assembleDebug :app:lintDebug \
  '-Dorg.gradle.jvmargs=-Xmx640m -XX:MaxMetaspaceSize=384m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
```

Asegúrate de que `local.properties`, si existe, apunte al mismo SDK. Las licencias las acepta el desarrollador; no se presuponen aceptadas por copiar comandos. En Windows configura el SDK/JDK con Android Studio y usa `gradlew.bat`; el bloque de carga `.env` anterior es de shell POSIX.

APK generado: `app/build/outputs/apk/debug/app-debug.apk`. No es necesaria una copia de `.env` dentro de `app/`. La primera compilación necesita descargar herramientas/dependencias; la app instalada sigue sin permiso de Internet.

### Instalar y abrir en un dispositivo de prueba

Con un único emulador operativo o teléfono conectado, depuración USB autorizada y ADB disponible:

```sh
"$ANDROID_HOME/platform-tools/adb" devices
"$ANDROID_HOME/platform-tools/adb" install -r app/build/outputs/apk/debug/app-debug.apk
"$ANDROID_HOME/platform-tools/adb" shell am start \
  -n com.idreyk.enfermeria360.dev/com.idreyk.enfermeria360.MainActivity
```

**Firma:** el repositorio no contiene la clave privada. Compilar en otra máquina suele generar una clave debug diferente. Los comandos anteriores sirven en un dispositivo nuevo de prueba o con la clave correcta; no garantizan actualizar una instalación existente. Si hay firma incompatible, **no desinstalar para forzarla**. Para actualizar la v0.2 entregada se conserva el APK v0.3 ya firmado con el mismo certificado. No hay prueba física completada ni migración inversa a v0.2.

Para el conversor opcional, independientemente de Android:

```sh
python3 -m pip install -r content-tools/requirements.txt
python3 -m unittest discover -s content-tools -p 'test_converter.py' -v
```


## Configuración opcional de nube en v0.4

El cliente lee `IDREYK_SUPABASE_URL` e `IDREYK_SUPABASE_PUBLISHABLE_KEY` de variables de entorno o propiedades Gradle. Ambas vacías = sin llamadas al servidor; ambas reales = conector habilitado. `.env` sigue sin cargarse automáticamente. Instrucciones de permisos, cuentas por correo y publicación: `backend/README.md`.

El APK entregado se compiló sin endpoint ni clave publicable; no contiene un servidor ficticio. Desde v0.4 declara INTERNET para el conector opcional. No incorpora claves secretas. Cambiar el contenido del servidor no requiere recompilar si el formato es compatible; configurar el endpoint inicial o cambiar lógica sí.

## Verificación de esta edición

Se restauraron JDK21/SDK35/Gradle8.9 para compilar v0.4 y se ejecutaron pruebas Android/JVM y lint. Consultar `qa/RESULTADOS_v0.4.md` y `qa/v04/`. La prueba en teléfono real, SMTP, Auth real y rendimiento medido siguen pendientes; las maquetas no son capturas del APK.
