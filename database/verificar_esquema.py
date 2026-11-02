#!/usr/bin/env python3
"""Comprueba el espejo SQL frente al Kotlin actual sin compilar Android.

Solo biblioteca estándar (Python 3.10+). No abre bases del teléfono.
Opcional: --crear-ejemplo RUTA crea un archivo NUEVO con el seed de la app.
Estas comprobaciones no sustituyen a Robolectric, pruebas físicas o validación clínica.
"""
from __future__ import annotations

import argparse
from contextlib import closing
from copy import deepcopy
import json
import os
from pathlib import Path
import re
import sqlite3
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[1]
KOTLIN = ROOT / "app/src/main/java/com/idreyk/enfermeria360/Repository.kt"
SCHEMA = ROOT / "database/schema.sql"
SEED = ROOT / "app/src/main/assets/contenido_inicial.json"
TABLES = {"content", "packages", "favorites", "notes", "practice", "meta", "notebooks", "media"}
ARRAYS = {"med": "medicamentos", "case": "casos", "safety": "listas_seguridad", "protocol": "protocolos"}


def native_definition() -> tuple[int, list[str]]:
    """Extractor deliberadamente acotado al LocalDatabase actual, no parser Kotlin general."""
    source = KOTLIN.read_text(encoding="utf-8")
    block = re.search(r"private class LocalDatabase\b.*?(?=\nclass ContentRepository\b)", source, re.S)
    if block is None:
        raise ValueError("No se localiza LocalDatabase; revisar el verificador si cambió la estructura.")
    version = re.search(r'SQLiteOpenHelper\(context,\s*"idreyk_local\.db",\s*null,\s*(\d+)\)', block[0])
    if version is None:
        raise ValueError("No se localiza la versión SQLite del proyecto.")
    literals = re.findall(r'db\.execSQL\(\s*("(?:[^"\\]|\\.)*")\s*\)', block[0])
    statements = [json.loads(value) for value in literals]
    names = []
    for statement in statements:
        match = re.match(r"CREATE TABLE ([a-z_]+)\(", statement)
        if not match or ";" in statement:
            raise ValueError("DDL nativo distinto del formato soportado; no asumir equivalencia.")
        names.append(match[1])
    if set(names) != TABLES or len(names) != len(TABLES):
        raise ValueError("Las tablas nativas cambiaron: actualizar documentación y verificador explícitamente.")
    return int(version[1]), statements


def new_documented_database() -> sqlite3.Connection:
    db = sqlite3.connect(":memory:")
    try:
        db.executescript(SCHEMA.read_text(encoding="utf-8"))
    except Exception:
        db.close()
        raise
    return db


def layout(db: sqlite3.Connection) -> dict:
    definitions = dict(db.execute("SELECT name, sql FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'"))
    result = {}
    for table in sorted(definitions):
        # Las funciones de PRAGMA admiten nombres enlazados: no concatenar entradas.
        indexes = []
        for index in db.execute("SELECT name, \"unique\", origin, partial FROM pragma_index_list(?)", (table,)):
            columns = list(db.execute("SELECT * FROM pragma_index_info(?)", (index[0],)))
            indexes.append((index[1:], columns))
        # También comparar el DDL completo: PRAGMA por sí solo no revela CHECK,
        # COLLATE u otras restricciones. Ignorar solo espacios y mayúsculas de
        # tokens no citados; conservar literalmente strings/identificadores citados.
        tokens = re.findall(r"'(?:''|[^'])*'|\"(?:\"\"|[^\"])*\"|[A-Za-z_][A-Za-z_0-9]*|[0-9]+|[^\s]", definitions[table])
        normalized_ddl = tuple(token if token.startswith(("'", '"')) else token.upper() for token in tokens)
        result[table] = {
            "ddl": normalized_ddl,
            "columns": list(db.execute("SELECT * FROM pragma_table_info(?)", (table,))),
            "foreign_keys": list(db.execute("SELECT * FROM pragma_foreign_key_list(?)", (table,))),
            "indexes": indexes,
            "autoincrement": "AUTOINCREMENT" in definitions[table].upper(),
        }
    return result


def load_seed(db: sqlite3.Connection) -> int:
    """Fixture de documentación con el JSON fuente; no reemplaza ContentParser de Android."""
    pack = json.loads(SEED.read_text(encoding="utf-8"))
    count = 0
    with db:
        for kind, array in ARRAYS.items():
            for entry in pack.get(array, []):
                payload = deepcopy(entry)
                payload.update(
                    _paquete=pack["id_paquete"],
                    _autor=pack["autor"],
                    _actualizacion=pack["fecha_actualizacion"],
                    _licencia=pack.get("licencia", ""),
                )
                db.execute(
                    "INSERT INTO content(kind,id,name,payload) VALUES(?,?,?,?)",
                    (kind, entry["id"], entry["nombre" if kind == "med" else "titulo"], json.dumps(payload, ensure_ascii=False)),
                )
                count += 1
        # El instante 0 es intencional y determinista en esta fixture, no una fecha de uso real.
        db.execute(
            "INSERT INTO packages(id,version,title,imported) VALUES(?,?,?,?)",
            (pack["id_paquete"], pack["version_contenido"], pack["titulo"], 0),
        )
        db.execute("INSERT INTO meta(key,value) VALUES(?,?)", ("seed", "1"))
    return count


def create_example(destination: Path) -> int:
    destination = destination.expanduser().resolve()
    destination.parent.mkdir(parents=True, exist_ok=True)
    # Creación exclusiva: nunca abrir y modificar un archivo preexistente.
    descriptor = os.open(destination, os.O_CREAT | os.O_EXCL | os.O_WRONLY, 0o600)
    os.close(descriptor)
    try:
        with closing(sqlite3.connect(destination)) as db:
            db.executescript(SCHEMA.read_text(encoding="utf-8"))
            return load_seed(db)
    except Exception:
        destination.unlink(missing_ok=True)
        raise


class ArchitectureTests(unittest.TestCase):
    def test_tables_columns_indexes_and_autoincrement_match_kotlin(self):
        version, statements = native_definition()
        with closing(new_documented_database()) as documented, closing(sqlite3.connect(":memory:")) as native:
            for statement in statements:
                native.execute(statement)
            self.assertEqual(layout(native), layout(documented))
            self.assertEqual(version, documented.execute("PRAGMA user_version").fetchone()[0])

    def test_actual_schema_has_eight_tables_and_no_accounts_or_foreign_keys(self):
        with closing(new_documented_database()) as db:
            description = layout(db)
            self.assertEqual(TABLES, set(description))
            self.assertTrue(all(not table["foreign_keys"] for table in description.values()))
            self.assertNotIn("usuarios", description)
            self.assertNotIn("registros", description)

    def test_original_seed_round_trips_with_attribution(self):
        pack = json.loads(SEED.read_text(encoding="utf-8"))
        expected = sum(len(pack.get(array, [])) for array in ARRAYS.values())
        with closing(new_documented_database()) as db:
            self.assertEqual(expected, load_seed(db))
            self.assertEqual(expected, db.execute("SELECT COUNT(*) FROM content").fetchone()[0])
            for kind, array in ARRAYS.items():
                for original in pack.get(array, []):
                    row = db.execute("SELECT payload FROM content WHERE kind=? AND id=?", (kind, original["id"])).fetchone()
                    payload = json.loads(row[0])
                    self.assertEqual(pack["autor"], payload["_autor"])
                    self.assertEqual(pack["id_paquete"], payload["_paquete"])
                    for key, value in original.items():
                        self.assertEqual(value, payload[key])
            self.assertEqual(("1",), db.execute("SELECT value FROM meta WHERE key=?", ("seed",)).fetchone())

    def test_parameterized_example_keeps_sql_like_text_literal(self):
        value = "Texto ficticio '); DROP TABLE notes; --"
        with closing(new_documented_database()) as db:
            db.execute("INSERT INTO notes(patient_id,text,created) VALUES(?,?,?)", ("mario", value, 0))
            self.assertEqual(value, db.execute("SELECT text FROM notes").fetchone()[0])
            self.assertEqual(TABLES, set(layout(db)))

    def test_example_creation_refuses_to_overwrite_any_existing_file(self):
        with tempfile.TemporaryDirectory(prefix="idreyk-schema-qa-") as folder:
            path = Path(folder) / "ejemplo.sqlite"
            count = create_example(path)
            before = path.read_bytes()
            with self.assertRaises(FileExistsError):
                create_example(path)
            self.assertEqual(before, path.read_bytes())
            with closing(sqlite3.connect(path)) as db:
                self.assertEqual(count, db.execute("SELECT COUNT(*) FROM content").fetchone()[0])
                self.assertEqual(2, db.execute("PRAGMA user_version").fetchone()[0])


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--crear-ejemplo", type=Path, metavar="RUTA", help="Crear una base NUEVA con el seed de la app; nunca sobrescribir.")
    arguments = parser.parse_args()
    suite = unittest.defaultTestLoader.loadTestsFromTestCase(ArchitectureTests)
    result = unittest.TextTestRunner(verbosity=2).run(suite)
    if not result.wasSuccessful():
        return 1
    if arguments.crear_ejemplo:
        try:
            count = create_example(arguments.crear_ejemplo)
        except FileExistsError:
            print("No se sobrescribió: el destino ya existe. Elige otro archivo de desarrollo.", file=sys.stderr)
            return 2
        except (OSError, sqlite3.Error, ValueError, KeyError) as error:
            print(f"No se creó la copia de demostración: {error}", file=sys.stderr)
            return 2
        print(f"Base de demostración creada: {arguments.crear_ejemplo} ({count} registros del seed).")
        print("No instalar en el teléfono: es una fixture documental, no una exportación de datos del usuario.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
