# Enfermería iDreyK 360 — guía del proyecto

Aplicación **Android nativa v0.3**, basada en el Prototipo B. Desarrollo continuo del mismo aplicativo: **offline, sin cuentas ni servidor**. Solo práctica y datos ficticios; no certificada para atención clínica.

## 1. Arquitectura real

| Componente | Implementación |
|---|---|
| Interfaz | Kotlin 2.0.21 + Jetpack Compose, BOM 2024.12.01 |
| Lógica local | Modelos/validadores, `AppViewModel` y corrutinas |
| Persistencia | SQLite con `SQLiteOpenHelper` v2, preferencias y archivos privados |
| Servidor / API / JWT | **No existen en esta edición** |
| Compilación | Gradle 8.9, AGP 8.7.3, JDK 17 o 21 |
| Android | Mínimo API 26; compilación/objetivo API 35 |
| Pruebas | JUnit, Robolectric 4.14.1, Compose; Python para herramientas editoriales |

Flujo: **pantalla → AppViewModel → ContentRepository → SQLite/archivos**. Los modelos separan cálculo y validación. Son responsabilidades dentro del módulo Android, no módulos Gradle independientes. Las rutas son navegación entre pantallas, no endpoints HTTP.

**No sustituir por Node/React/MySQL ni añadir usuarios por copiar una plantilla.** Registro, autenticación o sincronización requieren acordar un cambio de alcance y su seguridad. El nombre del perfil actual es una preferencia local, no una identidad autenticada.

## 2. Estructura

```text
idreyk-android/
├── README.md / AGENTS.md               # Entrada y reglas para trabajo con IA
├── .env.example                       # Rutas opcionales de herramientas
├── build.gradle.kts / settings.gradle.kts / gradle.properties
├── gradlew / gradlew.bat / gradle/wrapper/
├── app/build.gradle.kts
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/idreyk/enfermeria360/   # Kotlin nativo
│   ├── assets/contenido_inicial.json   # Seed único del catálogo
│   └── res/                           # Recursos, fuentes e ilustración
├── app/src/test/                      # Pruebas Kotlin/JVM
├── database/                          # Espejo SQL y verificador
├── content-tools/                     # XLSX → JSON, esquemas y ejemplos
├── docs/                              # Compilación y alcance funcional detallado
├── DESARROLLO.md                       # Continuidad y recuperación
├── research/ / qa/ / design/           # Referencias, evidencias y diseño
└── historial/desarrollo.bundle        # Historial portátil incluido en el ZIP
```

Mapa Kotlin: `MainActivity.kt` (entrada/navegación/selectores), `Design.kt` y `Screens*.kt` (UI), `Repository.kt` (BD, migración, repositorio y ViewModel), `Models.kt`, `ScaleModels.kt`, `DoseMath.kt`, `PracticeTrends.kt`, `NotebookModels.kt` y `MediaFiles.kt` (modelos, validación y operaciones de su módulo). No crear `frontend/` y `backend/` vacíos para simular otra arquitectura.

## 3. SQLite: esquema y relaciones

Base privada: `idreyk_local.db`. **La implementación ejecutada es `LocalDatabase` en `Repository.kt`.** [database/schema.sql](database/schema.sql) es un espejo documental, no un script cargado por el APK ni un reemplazo de las migraciones. La migración 1 → 2 agrega `notebooks` y `media`, conservando las tablas anteriores.

| Tabla | Función / clave |
|---|---|
| `content` | Catálogo JSON; clave compuesta `(kind, id)` |
| `packages` | Versiones/importaciones de paquetes; `id` |
| `favorites` | Claves lógicas `tipo/id`; `key` |
| `notes` | Notas de práctica por ámbito/ficha ficticia; `id` autoincremental |
| `practice` | Historial de ejercicios; `id` autoincremental |
| `meta` | Seed y puntos de deshacer; `key` |
| `notebooks` | Planes PAE/apuntes con contenido JSON; `id` |
| `media` | Metadatos de archivos privados; `id` |

**No hay claves foráneas SQL ni cascadas.** Las asociaciones las gestiona el código. `notes.patient_id` puede ser un ámbito como `gordon-0` o `vitals:mario`: no hay tabla de pacientes. La procedencia `_paquete` del JSON no garantiza que el paquete original siga en `packages` tras restaurar un respaldo externo.

Timestamps: milisegundos Unix. No confundir la versión SQLite con las versiones JSON de catálogo o cuaderno. Los binarios multimedia están en archivos privados, no dentro de SQLite.

**Seed:** `app/src/main/assets/contenido_inicial.json`, cargado por `ContentRepository.initialize()`: seis fichas farmacológicas en borrador, un caso ficticio y una lista educativa. No duplicar esos textos en SQL ni crear administradores con contraseñas/hashes de ejemplo.

## 4. Comandos y entorno

Instrucciones exactas para **preparar SDK/JDK, resolver dependencias, compilar, probar, instalar y abrir**: [docs/COMPILAR_Y_PROBAR.md](docs/COMPILAR_Y_PROBAR.md). No hay `npm install` ni servidor que levantar. La primera compilación descarga dependencias; el APK instalado sigue sin permiso de Internet.

`.env.example` solo contiene rutas de herramientas. **Android/Gradle no lee `.env` automáticamente**; la guía explica cómo cargarlo opcionalmente en Linux/macOS o configurar Android Studio. Si existe `local.properties`, `sdk.dir` debe coincidir con el SDK configurado. No se incluyen secretos de BD/JWT.

Desde la raíz, comprobar documentación (Python 3.10+, sin dependencias adicionales):

```sh
python3 database/verificar_esquema.py
python3 database/verificar_esquema.py --crear-ejemplo .cache/idreyk-ejemplo.sqlite
```

La segunda orden crea una base de demostración usando el seed original, con parámetros enlazados; se niega a sobrescribir un archivo existente. No usarla contra datos del teléfono.

Con JDK/SDK preparados, compilar el APK:

```sh
chmod +x gradlew
./gradlew --no-daemon --max-workers=1 :app:assembleDebug \
  '-Dorg.gradle.jvmargs=-Xmx640m -XX:MaxMetaspaceSize=384m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
```

Salida: `app/build/outputs/apk/debug/app-debug.apk`. La clave privada no se distribuye: otra máquina puede firmar con una clave diferente. **No desinstalar la app para forzar una actualización incompatible.** Usar el APK v0.3 entregado con la firma original, o un dispositivo nuevo de prueba para una compilación con otra firma.

## 5. Reglas de trabajo

Ver [AGENTS.md](AGENTS.md). En particular:

- Mantener Kotlin/Compose, Prototipo B, datos existentes y versiones fijadas.
- Separar UI y persistencia. Usar parámetros/`selectionArgs` y `ContentValues`, no interpolar entradas en SQL.
- Capturar y comunicar errores en límites asíncronos; no ocultarlos con `catch` vacío. Cerrar recursos con `use` y transacciones con `finally`.
- Un cambio de esquema requiere migración, pruebas y actualización del espejo SQL.
- No versionar `.env`, claves, bases del teléfono, APK/AAB, cachés ni `local.properties`. No subir a remotos sin autorización/destino real.
- No inventar contenido clínico ni permisos. Una ficha en borrador o una prueba matemática no es validación profesional.

## 6. Estado y lectura selectiva

- [Alcance funcional v0.3](docs/ESTADO_FUNCIONAL_v0.3.md): README anterior conservado íntegramente; rutas y comandos relativos a la raíz del proyecto.
- [DESARROLLO.md](DESARROLLO.md): historial y recuperación. El ZIP/bundle recupera código, **no los datos del teléfono**.
- [qa/RESULTADOS.md](qa/RESULTADOS.md): evidencia histórica de v0.3 — **77 pruebas Android/JVM y 15 del conversor**, 0 errores/25 advertencias de lint. No se presentan como repetidas en esta revisión documental.
- [qa/ARQUITECTURA.md](qa/ARQUITECTURA.md): comprobaciones nuevas del SQL y preservación.
- [qa/PRUEBA_EN_TELEFONO.md](qa/PRUEBA_EN_TELEFONO.md): pruebas reales pendientes.

Añadir README/SQL no hace la app «lista para producción». Faltan revisión clínica, permisos aplicables, pruebas en teléfono y controles para cualquier futuro uso asistencial.

### Mensaje para retomar con otra IA

> Trabaja sobre iDreyK 360 existente; no crees otra app. Lee README.md y AGENTS.md; para datos, database/schema.sql y LocalDatabase. Confirma el estado antes de editar. Conserva Android nativo, modo offline y datos existentes. Implementa solo el cambio acordado, con pruebas/migración si corresponde. Lee archivos pertinentes, no todo el repositorio. Distingue software probado de validación clínica y no afirmes acceso a GitHub sin URL/acceso real. Documenta cambios, límites y pendientes; entrega código recuperable.
