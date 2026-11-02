-- iDreyK 360 — esquema SQLite v2 (app Android v0.3/v0.4).
-- Espejo documental de LocalDatabase, en:
-- app/src/main/java/com/idreyk/enfermeria360/Repository.kt
--
-- SOLO para una base nueva de desarrollo. NO ejecutar contra datos del teléfono.
-- La app crea/migra su base con SQLiteOpenHelper; no lee este archivo en runtime.
-- La migración real 1 -> 2 conserva las tablas v1 y añade notebooks/media.
-- Ocho tablas de aplicación; no se documentan tablas internas de SQLite/Android.
-- No se añaden tablas de usuarios, autenticación, claves foráneas ni cascadas.
-- Las relaciones actuales son lógicas y las gestiona el código del aplicativo.
--
-- SEED: la fuente única es app/src/main/assets/contenido_inicial.json.
-- La app lo valida y carga desde ContentRepository.initialize().
-- Para una copia de demostración fuera de Android, sin duplicar los datos:
-- python3 database/verificar_esquema.py --crear-ejemplo .cache/idreyk-ejemplo.sqlite
-- No contiene administradores, contraseñas ni hashes ficticios de autenticación.

BEGIN TRANSACTION;

-- Medicamentos, casos, listas de seguridad y protocolos.
-- payload conserva contenido, fuentes, revisión declarada y procedencia.
CREATE TABLE content (
    kind TEXT NOT NULL,
    id TEXT NOT NULL,
    name TEXT NOT NULL,
    payload TEXT NOT NULL,
    PRIMARY KEY (kind, id)
);

-- Control de versiones de paquetes importados; imported son milisegundos Unix.
CREATE TABLE packages (
    id TEXT PRIMARY KEY,
    version INTEGER NOT NULL,
    title TEXT NOT NULL,
    imported INTEGER NOT NULL
);

-- key tiene la forma tipo/id. No existe FOREIGN KEY hacia content.
CREATE TABLE favorites (
    key TEXT PRIMARY KEY
);

-- Notas de práctica. patient_id también puede representar ámbitos
-- como gordon-0 o vitals:mario; no hay una tabla de pacientes reales.
CREATE TABLE notes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    patient_id TEXT NOT NULL,
    text TEXT NOT NULL,
    created INTEGER NOT NULL
);

-- Historial de ejercicios. created son milisegundos Unix.
CREATE TABLE practice (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    type TEXT NOT NULL,
    summary TEXT NOT NULL,
    created INTEGER NOT NULL
);

-- Indicador seed y estados internos de deshacer, entre otros metadatos.
CREATE TABLE meta (
    key TEXT PRIMARY KEY,
    value TEXT
);

-- Añadida en v2: planes PAE y apuntes. payload es el documento JSON.
-- owner identifica una ficha ficticia en planes y queda vacío en apuntes.
CREATE TABLE notebooks (
    id TEXT PRIMARY KEY,
    kind TEXT NOT NULL,
    owner TEXT NOT NULL,
    title TEXT NOT NULL,
    payload TEXT NOT NULL,
    modified INTEGER NOT NULL
);

-- Añadida en v2: metadatos de archivos. Los binarios no están en SQLite:
-- se guardan en files/biblioteca/, con nombres internos generados por la app.
CREATE TABLE media (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    mime TEXT NOT NULL,
    filename TEXT NOT NULL,
    bytes INTEGER NOT NULL,
    created INTEGER NOT NULL
);

-- Solo para este esquema nuevo de desarrollo; no es una instrucción de migración.
PRAGMA user_version = 2;
COMMIT;
