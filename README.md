# Enfermería iDreyK 360 — Android nativo v0.4

Evolución del mismo aplicativo del **Prototipo B**, en Kotlin/Jetpack Compose. La biblioteca descargada sigue disponible sin conexión. Se incorporan la estructura farmacológica aportada por el usuario, rendimiento adaptable y un conector opcional de cuentas/actualización de contenido.

**Estado real:** código compilado y pruebas locales ejecutadas; **ningún proyecto Supabase está conectado**. El APK entregado no contiene URL/clave de servidor y muestra «Nube sin configurar». No se simulan cuentas, envíos de correo ni descargas. Solo contenido de práctica: no certificada para pacientes reales.

## 1. Qué cambia

- Inicio con Farmacología prioritaria y accesos directos a PAE, escalas, cálculos, apuntes, protocolos y multimedia. Se conservan los demás módulos.
- Ficha: **Lo esencial → Ficha técnica de 17 conceptos → Otros datos → Fuentes**. Los campos anteriores no se descartan.
- Resumen de enfermería: objetivo, antes de administrar, preparación/administración y vigilancia. No se generaron indicaciones clínicas ni dosis para rellenarlo.
- Fichas parciales: vacío significa pendiente. «No aplica» debe justificarse; no se deduce que no exista riesgo.
- Modos Automático/Ligero/Completo. En automático se consideran RAM, memoria y batería; accesibilidad y segundo plano reducen/pausan movimiento. Mismas funciones en todos los modos; no es un benchmark de todos los teléfonos.
- Cliente Supabase: código por correo, sesión protegida con Android Keystore y comprobación manual de publicaciones con integridad/confirmación antes de importar. Requiere servidor configurado.

## 2. Arquitectura y bases de datos

| Capa | Implementación |
|---|---|
| UI Android | Kotlin 2.0.21, Compose BOM 2024.12.01; sin WebView |
| Lógica | Modelos/validadores → AppViewModel → ContentRepository |
| Datos del dispositivo | SQLite `idreyk_local.db`, esquema **v2**; preferencias/archivos privados |
| Servidor elegido | **PostgreSQL administrado por Supabase**; catálogo/revisiones/permisos |
| Cuentas | **Supabase Auth**, verificación por código de correo; no tabla propia de contraseñas |
| Documentos fuente | **Supabase Storage**, bucket editorial privado |
| Compilación | Gradle 8.9, AGP 8.7.3, JDK 17/21; Android mínimo API 26, objetivo 35 |

La petición nueva autoriza expresamente cuentas y nube; no es una migración inferida de la plantilla anterior. No se reemplaza Android por Node/React/MySQL. La cuenta no es necesaria para leer el catálogo publicado y **no respalda automáticamente los datos personales del teléfono**.

### Dos esquemas distintos

- [database/schema.sql](database/schema.sql): espejo de las **ocho tablas SQLite actuales**: `content`, `packages`, `favorites`, `notes`, `practice`, `meta`, `notebooks`, `media`. Sin claves foráneas SQL; asociaciones controladas por la app. Timestamps en milisegundos Unix. La fuente ejecutable sigue siendo `LocalDatabase` en `Repository.kt`.
- [backend/supabase/schema.sql](backend/supabase/schema.sql): PostgreSQL para un proyecto Supabase nuevo. Perfiles por usuario, permisos editoriales privados, borradores y publicaciones inmutables; RLS y bucket privado. No ejecutar este archivo en el teléfono.

La ficha ampliada se guarda en el JSON de `content.payload`; por eso no necesita recrear las tablas SQLite. Las versiones JSON no son la versión de la base.

## 3. Dónde trabajar

```text
app/src/main/java/com/idreyk/enfermeria360/
  MainActivity.kt / Design.kt / Screens*.kt   UI y navegación nativas
  Repository.kt                             SQLite, repositorio y ViewModel
  Models.kt / Pharmacology.kt                Catálogo y estructura farmacológica
  Performance.kt                            Política adaptable y señales Android
  Cloud.kt / SessionVault.kt / ScreensCloud.kt  Conector opcional y cuenta
  ScaleModels.kt / DoseMath.kt / ...         Módulos de enfermería conservados
app/src/main/assets/contenido_inicial.json    Seed original, todavía borradores
app/src/test/                               Pruebas de lógica, persistencia y UI
content-tools/                              XLSX, JSON, conversor y esquemas 1/2/3
backend/                                    SQL Supabase, publicación y pruebas RLS
 database/schema.sql                        Espejo SQLite local
 design/propuesta_v0.4/                     Imágenes elegidas, corrección legible y guía
 docs/                                      Compilación, alcance y continuidad
 qa/v04/                                    Evidencia de esta ampliación
 historial/desarrollo.bundle                Historial portátil incluido en el ZIP
```

## 4. Formato de medicamentos y exportación

El PDF del usuario aporta **17 conceptos**, no monografías ni prueba de aval DIGEMID. Su mapeo está en [content-tools/GUIA_FARMACOLOGIA_v3.md](content-tools/GUIA_FARMACOLOGIA_v3.md). La app conserva los 20 campos anteriores y añade 10: en total **30 secciones opcionales**, además del nombre/identidad y fuentes.

- Nueva plantilla: `content-tools/Plantilla_farmacos_v3.xlsx`.
- Ejemplo editable: `content-tools/plantilla_farmaco_v3.json`.
- v0.4 importa formatos **1, 2 y 3** y exporta **3**. v0.2/v0.3 no admiten el nuevo formato 3; no volver atrás instalando una versión antigua.
- Los seis medicamentos iniciales siguen en borrador. Sin dosis, controles o fuentes inventadas.
- Se conservan límites actuales: 2 MB, 500 registros agregados, 300 por tipo. La paginación para grandes catálogos queda pendiente.

```sh
python3 -m pip install -r content-tools/requirements.txt
python3 content-tools/convertir_plantilla.py content-tools/Plantilla_farmacos_v3.xlsx mis-farmacos.json
```

## 5. Compilar y conectar

Comandos completos de SDK/JDK, compilación, pruebas e instalación: [docs/COMPILAR_Y_PROBAR.md](docs/COMPILAR_Y_PROBAR.md).

`.env.example` contiene herramientas y las dos variables **publicables** opcionales del cliente. Gradle no carga `.env` automáticamente. Con JDK/SDK configurados:

```sh
chmod +x gradlew
./gradlew --no-daemon --max-workers=1 :app:assembleDebug \
  '-Dorg.gradle.jvmargs=-Xmx640m -XX:MaxMetaspaceSize=384m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
```

Salida: `app/build/outputs/apk/debug/app-debug.apk`. No hay servidor local/puerto que levantar. Para activar nube, seguir **[backend/README.md](backend/README.md)**: crear/configurar proyecto, aplicar RLS, preparar correo y compilar con URL/clave publicable. Las claves secretas nunca van en el APK.

El APK v0.4 declara INTERNET; sin configuración no realiza solicitudes. Las consultas de actualización son manuales y requieren confirmación para guardar. No hay push, reemplazo silencioso, retiros automáticos ni subida de notas/planes.

**Firma:** usar el APK entregado para actualizar la instalación anterior sin desinstalar. Una compilación en otra máquina puede tener otra clave debug y no actualizarla. La clave privada no se distribuye. No se ha probado todavía la actualización en un teléfono físico.

## 6. Comprobaciones y límites

Ver [qa/RESULTADOS_v0.4.md](qa/RESULTADOS_v0.4.md): pruebas Android/JVM y Compose simulado, conversor/publicación, PostgreSQL/RLS y espejo SQLite. Los resultados de v0.3 permanecen como evidencia histórica separada.

No se verificaron servidor Supabase real, correo/SMTP, instalación física, hardware Keystore, rendimiento medido en gamas distintas ni validación clínica. El diseño exportado es una **maqueta**, no una captura del APK. Las pruebas de reglas y aritmética no certifican atención clínica.

## 7. Continuidad con IA

Leer [AGENTS.md](AGENTS.md), después solo los archivos del módulo afectado. No borrar trabajo previo para encajar una plantilla. Mantener SQL parametrizado, límites, errores explícitos, transacciones y compatibilidad. Documentar qué está compilado, probado, configurado o pendiente.

- [DESARROLLO.md](DESARROLLO.md): historial y recuperación.
- [docs/EVOLUCION_CATALOGO_CUENTAS.md](docs/EVOLUCION_CATALOGO_CUENTAS.md): solicitud y decisiones de esta fase.
- [docs/ESTADO_FUNCIONAL_v0.3.md](docs/ESTADO_FUNCIONAL_v0.3.md): funciones previas conservadas.

El ZIP/bundle recupera código, recursos y diseño; **no los datos de tu teléfono**. Catálogo, cuaderno y medios tienen respaldos distintos. Descarga una copia externa del proyecto; no se ha subido a ningún repositorio remoto.
