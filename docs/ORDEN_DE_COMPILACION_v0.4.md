> **Actualización v0.4.1:** versionCode5 / 0.4.1-dev-debug. La arquitectura siguiente se conserva. Usa `docs/COMPILAR_Y_PROBAR.md`, `docs/GITHUB_Y_CI.md` y `scripts/comprobar.sh --todo` para la comprobación actual. Tag: `desarrollo-v0.4.1`. El resto del documento corresponde al punto de control v0.4.

# Orden de continuidad y compilación — iDreyK 360 v0.4

Texto de entrega para quien retome el proyecto. No es una orden enviada a un agente remoto ni un despliegue ya realizado.

## Trabajar aquí, no empezar otra aplicación

- En este entorno: `/home/user/idreyk-android`.
- En otra máquina: extraer `Android_iDreyK_360_codigo.zip` y entrar en su carpeta `idreyk-android/`.
- Rama local: `desarrollo`; punto de control de esta entrega: `desarrollo-v0.4`.
- Mantener Kotlin/Compose y el diseño Prototipo B. No sustituir por web/React/Flutter ni recrear tablas para adaptar un ejemplo.
- Leer `README.md`, `AGENTS.md`, la sección pertinente de `qa/RESULTADOS_v0.4.md` y los archivos del módulo afectado; no cargar todas las fuentes y evidencias juntas.

## Contratos que debe respetar la compilación

1. Aplicación `com.idreyk.enfermeria360.dev` en debug, versión4/0.4.0-dev-debug. Conservar datos y firma para actualizar instalaciones anteriores.
2. SQLite local v2 con ocho tablas; el SQL de `database/` no se aplica como migración manual al teléfono.
3. Catálogo: lectura1/2/3, exportación3; 17 conceptos técnicos del PDF + campos anteriores y lectura de enfermería. No inventar contenido faltante.
4. Nube elegida: PostgreSQL/Supabase + Auth + Storage. Su SQL está en `backend/supabase/schema.sql`; no es SQLite.
5. Compilar sin URL/clave pública deja la app local y sin solicitudes de red. Para conectar se deben configurar **ambas** variables públicas y un proyecto real con RLS/correo previamente probado.
6. Prohibido incorporar claves secretas, `service_role`, contraseña PostgreSQL o clave privada de firma al APK/ZIP.
7. Mantener modos Automático/Ligero/Completo y prevalencia de accesibilidad/ahorro de batería. No retirar funciones al activar modo ligero.

## Comandos Android

Requiere JDK17/21, SDK35 y Build Tools; preparación detallada en `docs/COMPILAR_Y_PROBAR.md`. Desde la raíz:

```sh
chmod +x gradlew
./gradlew --no-daemon --max-workers=1 :app:compileDebugUnitTestKotlin \
  '-Dorg.gradle.jvmargs=-Xmx640m -XX:MaxMetaspaceSize=384m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
./gradlew --no-daemon --max-workers=1 :app:testDebugUnitTest \
  '-Dorg.gradle.jvmargs=-Xmx384m -XX:MaxMetaspaceSize=256m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
./gradlew --no-daemon --max-workers=1 :app:assembleDebug :app:lintDebug \
  '-Dorg.gradle.jvmargs=-Xmx640m -XX:MaxMetaspaceSize=384m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
```

APK: `app/build/outputs/apk/debug/app-debug.apk`. Las pruebas de UI sin configuración se ejecutaron con variables cloud vacías; para probar un proyecto conectado se necesita un plan de integración separado, sin enviar correos reales desde pruebas unitarias.

## Comandos de contenido y base

```sh
python3 -m pip install -r content-tools/requirements.txt
python3 -m unittest discover -s content-tools -p 'test_converter.py' -v
python3 -m unittest discover -s backend/tests -p 'test_*.py' -v
python3 database/verificar_esquema.py
npm ci --prefix backend
npm test --prefix backend
```

Para publicar contenido seguir `backend/README.md`. Un nuevo medicamento compatible actualiza el catálogo, **no requiere recompilar el APK**. Un cambio de formato/lógica o del endpoint de compilación sí puede requerir otra versión.

## Entregar sin sobreafirmar

- Reportar versión, pruebas ejecutadas, errores/advertencias, firma y permisos.
- Actualizar código, guías y bundle; probar un clon y comparar hashes antes de empaquetar.
- No llamar captura del APK a una maqueta ni afirmar un servidor/correo/cuenta operativos sin comprobarlos.
- No afirmar validación clínica ni listo para producción. Revisión, permisos, pruebas físicas, privacidad y hardening de publicación siguen pendientes.
