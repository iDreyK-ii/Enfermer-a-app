> Cierre local Atlas v2.2: 252 JVM, 118 Python, 5 SQLite y 17 PGLite; lint 0 errores/26 advertencias. Ocho capturas del APK en emulador API28, 17 renders Robolectric separados y 2/2 pruebas PDF instrumentadas aprobadas. No hardware físico, no nuevo CI/publicación. Detalle: `qa/atlas_v22/RESULTADOS.md`.

> **BORRADOR LOCAL 0.7.0 / Atlas v2.2:** no cargar `base-maestra-v21-mvp` esperando estos PNG o el quiz nuevo: ese tag es 0.6.0. Esperar revisión/autorización y usar el SHA exacto aprobado; la publicación y la paridad Google de esta revisión están pendientes. Leer `docs/AUDITORIA_ATLAS_v22.md` y `qa/atlas_v22/RESULTADOS.md`.

# Continuar iDreyK 360 en Google AI Studio, sin rehacer la app

> **Checkpoint publicado anterior v0.6.0 / Base Maestra 2.1:** leer `docs/MVP_BASE_MAESTRA_v21.md` y cargar el SHA exacto de esta entrega o la etiqueta `base-maestra-v21-mvp`. `escalas-clinicas-v1` y `traslado-ia-studio-v1` son checkpoints anteriores, no esta actualización. La importación/compilación de esta revisión en Google no se ha comprobado aquí.

## Regla principal

**Este repositorio es el aplicativo existente. No es una descripción para generar otro.** La fuente de verdad es https://github.com/iDreyK-ii/Enfermer-a-app, rama `desarrollo`.

Flujo acordado: **revisión visual aquí → mejoras locales y pruebas → aprobación expresa del propietario para publicar → commit/push a GitHub → importar o actualizar esa revisión exacta en Google AI Studio → comprobar paridad → compilar**. AI Studio se utilizará inicialmente como destino de carga/compilación, no como una segunda rama de cambios silenciosos.

Antes de empezar, leer `AGENTS.md`, `CONTINUIDAD.json` y `docs/INVENTARIO_FUNCIONAL_ACTUAL.md`. El texto listo para pedir el traslado está en `docs/PROMPT_IA_STUDIO.md`.

## 1. Seleccionar plataforma y cargar el código existente

1. Abrir Google AI Studio directamente en el navegador, no dentro del visor del chat.
2. Entrar en **Build** y seleccionar **Android**. No seleccionar Web ni pedir una reconstrucción desde imágenes.
3. Si la cuenta permite **Add files (+) → Import from GitHub** en ese modo, usar el repositorio canónico y `desarrollo`. Confirmar el commit exacto indicado en la entrega, no solo el nombre de la rama.
4. No crear un repositorio nuevo, no sobrescribir este remoto, no activar Firebase por sustitución ni regenerar las imágenes.
5. Si la opción de importación no existe en modo Android, o no conserva archivos, **detenerse y comunicarlo**. No cambiar a React/Flutter/WebView para que la vista previa funcione. Se comprobará la alternativa de cargar fuentes/ZIP que esa cuenta ofrezca; no se da por probada ninguna opción que no se haya usado.

### Capacidad documentada ≠ importación de esta app verificada

La guía oficial actual describe Android nativo Kotlin/Compose, Gradle, una actividad y un módulo. Esta app tiene una actividad y el módulo `:app`, sin layouts de Views ni código C/C++ propio. Esto es compatible con el tipo de proyecto descrito, **no prueba que el importador de AI Studio haya cargado todo**. [1](https://ai.google.dev/gemini-api/docs/aistudio-android)

La guía general documenta importación GitHub y sincronización, pero la guía específica Android todavía indica exportación mediante ZIP y no exportación GitHub. No prometer sincronización Android bidireccional automática: verificar la función concreta en la cuenta. Nuestro flujo de una sola fuente de verdad no depende de que AI Studio haga push. [1](https://ai.google.dev/gemini-api/docs/aistudio-android) [2](https://ai.google.dev/gemini-api/docs/aistudio-build-mode)

Documentación consultada el 2 de noviembre de 2026: guía Android actualizada el 20 de octubre; guía general, el 26 de octubre. No se operó ni autenticó una cuenta AI Studio desde este entorno.

## 2. Comprobar que la carga es fiel

La apariencia por sí sola no demuestra una copia completa. Antes de que el agente modifique archivos, obtener una exportación de fuentes o una copia verificable de lo importado.

Desde **la copia Git confiable**, no desde un script modificado por el importador:

```sh
# Usar el SHA exacto de la entrega, o el tag inmutable de este primer punto.
python3 scripts/verificar_traslado.py verificar \
  --ref base-maestra-v21-mvp \
  --zip /ruta/exportacion-de-ai-studio.zip \
  --salida .cache/traslado/resultado.json
```

También acepta `--directorio /ruta/copia-importada`. Si el ZIP tiene una única carpeta raíz se detecta automáticamente; una raíz ambigua se rechaza. No extrae el ZIP ni hace llamadas de red.

**Para aprobar:** `ok: true`, sin archivos faltantes/modificados/inesperados, con permisos ejecutables comprobados. Si una exportación no registra esos permisos, informa que falta verificarlos: no declarar paridad completa. El comprobador omite cachés/dependencias generadas y el bundle opcional; lo informa. No inspecciona secretos ni valida funcionalidad clínica.

El manifiesto exacto de un commit se puede obtener así:

```sh
python3 scripts/verificar_traslado.py manifiesto --ref HEAD \
  --salida .cache/traslado/manifiesto-fuentes.json
```

La CI también produce ese manifiesto entre sus evidencias. Los hashes se calculan desde objetos Git, no desde archivos sin commit. El historial se comprueba en GitHub/bundle por separado.

## 3. Compilar la misma app

Para la primera carga, conservar el perfil local. La configuración de correo OTP fue aplazada, no eliminada ni resuelta.

```sh
./gradlew --no-daemon --max-workers=1 \
  -PIDREYK_SUPABASE_URL= -PIDREYK_SUPABASE_PUBLISHABLE_KEY= \
  :app:assembleDebug \
  '-Dorg.gradle.jvmargs=-Xmx640m -XX:MaxMetaspaceSize=384m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
```

Salida: `app/build/outputs/apk/debug/app-debug.apk`. Herramientas y pruebas: `docs/COMPILAR_Y_PROBAR.md`; control completo en un entorno preparado: `bash scripts/comprobar.sh --todo`. Si AI Studio no ejecuta Python/Node o alguna prueba, decirlo: no inventar resultados. La CI del mismo commit es una evidencia separada.

No subir versiones de Kotlin, Gradle, AGP o dependencias automáticamente para esconder un error; presentar el error y proponer un cambio revisable. Si hace falta corregir algo, se integra y valida en el repositorio canónico antes de recargar.

## 4. Verificar firma antes de instalar

- Identidad debug: `com.idreyk.enfermeria360.dev`.
- Borrador local actual: versionCode **8**, nombre **0.7.0-dev-debug**. Para entregas posteriores, contrastar con `CONTINUIDAD.json`, `app/build.gradle.kts` y el manifiesto del APK; no reutilizar esta versión como si fuera siempre la última.
- Certificado conservado: `d6874c68417eefb1b1b902bf82ba4ce7d8734e47877e12bd1733eb498f0e522b`.

Con SDK adecuado, comprobar el APK externo con:

```sh
python3 scripts/verificar_apk.py --apk /ruta/app-debug.apk \
  --certificado d6874c68417eefb1b1b902bf82ba4ce7d8734e47877e12bd1733eb498f0e522b
```

Una firma administrada por Google o temporal del runner no se presume compatible. Si no coincide, **no desinstalar ni borrar datos**: volver a las fuentes y organizar una compilación/firma con la clave conservada. No pegar esa clave en chats ni publicarla en GitHub. La guía Android de Google describe firma administrada para publicación; no se da por disponible la importación de nuestra clave en AI Studio. [1](https://ai.google.dev/gemini-api/docs/aistudio-android)

No publicar en Play, crear servicios de pago ni activar facturación sin autorización. Compilar no equivale a publicar.

## 5. Cada actualización posterior

1. Revisar cambios y pruebas aquí; mantener versiones y migraciones compatibles. Para cambios del APK, incrementar versionCode de forma controlada.
2. Mostrar el resultado, diferencias y límites; obtener aprobación expresa del propietario para publicar esa revisión. Solo entonces, commit y push no forzado a `desarrollo`; comprobar SHA remoto y CI de ese mismo commit. «Continuar» o pedir mejoras no sustituye esta aprobación.
3. Comunicar al propietario el **SHA exacto**, alcance del cambio, perfil de nube y estado de firma/pruebas.
4. Pedir a AI Studio cargar esa revisión sin conservar cambios divergentes no revisados.
5. Verificar de nuevo fuentes y compilación. Si se usó una importación nueva por limitaciones de la herramienta, es otra copia del mismo proyecto, no permiso para regenerarlo.
6. Si hubo cambios dentro de AI Studio, exportarlos y compararlos antes de integrarlos. No aceptar «la versión más nueva» a ciegas ni usar `push --force`.

## Criterio de salida

**Listo para iniciar un traslado controlado** significa repositorio, contrato, código, historial y herramientas de verificación preparados, con CI correspondiente revisada. **Traslado completado** exige comprobar la copia real en AI Studio. **APK apto para actualizar** exige firma/identidad y prueba física de actualización. Son tres estados distintos.
