# Enfermería iDreyK 360 — Android nativo v0.5.0

> **Continuidad con Google AI Studio:** empezar por [INICIAR_EN_AI_STUDIO.md](INICIAR_EN_AI_STUDIO.md), [identidad técnica](CONTINUIDAD.json) e [inventario funcional actual](docs/INVENTARIO_FUNCIONAL_ACTUAL.md). Se traslada esta app nativa completa, no una recreación. Cada actualización parte de un commit exacto de `desarrollo`; la copia importada se verifica antes de compilar. El propietario informa que Google construyó la revisión anterior sin cambios; su paridad de fuentes/renderizado no se verificó independientemente.

Evolución del mismo aplicativo del **Prototipo B**, en Kotlin/Jetpack Compose. La biblioteca descargada sigue disponible sin conexión. Se conservan la estructura farmacológica aportada por el usuario, rendimiento adaptable y un conector opcional de cuentas/actualización de contenido.

**Estado actual de infraestructura (2 de noviembre de 2026):** se creó `idreyk-360-dev` en Supabase, región Virginia autorizada, y se aplicó su migración. Pasaron **7 controles HTTPS y 12 comprobaciones SQL reales**. Correo OTP y sesión real siguen pendientes; el propietario autorizó aplazar esa configuración y continuar en modo local, sin cuenta obligatoria. Ver [docs/SUPABASE_REAL_Y_GITHUB.md](docs/SUPABASE_REAL_Y_GITHUB.md).

**El APK entregado v0.4.1 no cambió:** todavía tiene nube vacía. Se dejaron parámetros públicos y un comando para la próxima compilación conectada; no se ejecutó esa compilación en esta fase. El repositorio de trabajo autorizado es [iDreyK-ii/Enfermer-a-app](https://github.com/iDreyK-ii/Enfermer-a-app), rama `desarrollo`, público por decisión del propietario. Ver [destino y comprobación de sincronización](docs/GITHUB_DESTINO.md). Solo contenido de práctica, sin validación para pacientes reales.

## Actualización v0.5.0 — tests y escalas clínicas

**Primer bloque, no catálogo universal ni certificación clínica:** 44 fichas documentales/40 fuentes, filtros por adultos/pediatría/neonatos y área, búsqueda global, estado de permisos y profundidad de lectura. Once fichas enlazan práctica, simulación o registro; no son 44 calculadoras. Nuevas pantallas: escala numérica de dolor separada de EVA y 4AT adulto como adaptación educativa con atribución CC BY 4.0. Se conservan las herramientas previas, Prototipo B, SQLite v2 y nube opcional. Farmacología queda al final y no se completó aquí.

Detalle: [funciones, diseño y límites](docs/ESCALAS_CLINICAS_v0.5.0.md); [investigación por instrumento](research/escalas/REVISION_2026-11-02.md). versionCode **6**; resultados técnicos: [QA v0.5.0](qa/RESULTADOS_v0.5.0.md). Comprobar el run CI del SHA final, no inferirlo de las pruebas históricas.

## Historial: pulido v0.4.1 antes de sincronizar

Además de la base v0.4: comparación y aceptación de sustituciones; bloqueo de vistas previas desactualizadas; detección de campos que se vaciarían; paginación de revisión; cuadrícula/hero adaptables a texto grande; respuesta táctil y selección animadas; bloqueo de campos durante escrituras; rechazo de JSON excesivamente anidado.

**133 pruebas Android superadas en v0.4.1 (histórico).** Resultados de aquella versión: [qa/RESULTADOS_v0.4.1.md](qa/RESULTADOS_v0.4.1.md). Detalle: [docs/MEJORAS_v0.4.1.md](docs/MEJORAS_v0.4.1.md). La plantilla y los esquemas de contenido/SQLite no cambiaron.

Preparación de Git/CI: [docs/GITHUB_Y_CI.md](docs/GITHUB_Y_CI.md). Consultar la ejecución del commit concreto en [GitHub Actions](https://github.com/iDreyK-ii/Enfermer-a-app/actions/workflows/calidad.yml); la evidencia local histórica no sustituye esa comprobación. No necesita secretos de nube. El APK de un runner usa firma temporal: no confundirlo con una actualización del APK entregado.

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
 qa/v041/                                   Evidencia del pulido actual
 historial/desarrollo.bundle                Historial portátil incluido en el ZIP
```

## 4. Formato de medicamentos y exportación

El PDF del usuario aporta **17 conceptos**, no monografías ni prueba de aval DIGEMID. Su mapeo está en [content-tools/GUIA_FARMACOLOGIA_v3.md](content-tools/GUIA_FARMACOLOGIA_v3.md). La app conserva los 20 campos anteriores y añade 10: en total **30 secciones opcionales**, además del nombre/identidad y fuentes.

- Nueva plantilla: `content-tools/Plantilla_farmacos_v3.xlsx`.
- Ejemplo editable: `content-tools/plantilla_farmaco_v3.json`.
- Desde v0.4 se importan formatos **1, 2 y 3** y se exporta **3**. v0.2/v0.3 no admiten el nuevo formato 3; no volver atrás instalando una versión antigua.
- Los seis medicamentos iniciales siguen en borrador. Sin dosis, controles o fuentes inventadas.
- Se conservan límites actuales: 2 MB, 500 registros agregados, 300 por tipo. La revisión de importación está paginada; los catálogos remotos mayores que estos límites siguen pendientes.

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

Salida: `app/build/outputs/apk/debug/app-debug.apk`. No hay servidor local/puerto que levantar. Para el proyecto ya creado, seguir **[docs/SUPABASE_REAL_Y_GITHUB.md](docs/SUPABASE_REAL_Y_GITHUB.md)**: terminar correo y preparar la compilación conectada. No volver a aplicar el esquema inicial sobre ese proyecto. Las claves secretas nunca van en el APK.

Desde v0.4 el APK declara INTERNET; sin configuración no realiza solicitudes. Las consultas de actualización son manuales y requieren confirmación para guardar. No hay push, reemplazo silencioso, retiros automáticos ni subida de notas/planes.

**Firma:** usar el APK entregado para actualizar la instalación anterior sin desinstalar. Una compilación en otra máquina puede tener otra clave debug y no actualizarla. La clave privada no se distribuye. No se ha probado todavía la actualización en un teléfono físico.

## 6. Comprobaciones y límites

Ver [qa/RESULTADOS_v0.4.1.md](qa/RESULTADOS_v0.4.1.md): pruebas Android/JVM y Compose simulado, conversor/publicación, PostgreSQL/RLS y espejo SQLite. Los resultados de v0.3/v0.4/v0.4.1 permanecen como evidencia histórica separada; para este bloque consultar qa/RESULTADOS_v0.5.0.md.

El servidor real tiene la primera validación de esquema y API documentada en [qa/SUPABASE_REAL_2026-11-02.md](qa/SUPABASE_REAL_2026-11-02.md). No se verificaron correo/sesión reales, instalación física, hardware Keystore, rendimiento medido en gamas distintas ni validación clínica. El diseño exportado es una **maqueta**, no una captura del APK. Las pruebas de reglas y aritmética no certifican atención clínica.

## 7. Continuidad con IA

Leer [AGENTS.md](AGENTS.md), después solo los archivos del módulo afectado. No borrar trabajo previo para encajar una plantilla. Mantener SQL parametrizado, límites, errores explícitos, transacciones y compatibilidad. Documentar qué está compilado, probado, configurado o pendiente.

- [DESARROLLO.md](DESARROLLO.md): historial y recuperación.
- [docs/EVOLUCION_CATALOGO_CUENTAS.md](docs/EVOLUCION_CATALOGO_CUENTAS.md): solicitud y decisiones de esta fase.
- [docs/ESTADO_FUNCIONAL_v0.3.md](docs/ESTADO_FUNCIONAL_v0.3.md): funciones previas conservadas.

El ZIP/bundle recupera código, recursos y diseño; **no los datos de tu teléfono**. Catálogo, cuaderno y medios tienen respaldos distintos. Conserva también una copia externa. El destino y el procedimiento para contrastar el historial remoto están en `docs/GITHUB_DESTINO.md`.
