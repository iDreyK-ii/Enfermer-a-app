#!/usr/bin/env python3
"""Compara dos copias de prueba SQLite/preferencias/archivo sin exponer sus filas."""
import argparse
import hashlib
import json
from pathlib import Path
import sqlite3

TABLES = ['content', 'packages', 'favorites', 'notes', 'practice', 'meta', 'notebooks', 'media']


def hash_bytes(data):
    return hashlib.sha256(data).hexdigest()


def summarize(path):
    conn = sqlite3.connect(f'file:{path.resolve()}?mode=ro', uri=True)
    try:
        integrity = conn.execute('PRAGMA quick_check').fetchone()[0]
        version = conn.execute('PRAGMA user_version').fetchone()[0]
        result = {}
        for name in TABLES:
            cols = [row[1] for row in conn.execute(f'PRAGMA table_info("{name}")')]
            if not cols:
                raise ValueError(f'Falta tabla {name}')
            values = conn.execute(f'SELECT * FROM "{name}"').fetchall()
            def encode(row):
                return json.dumps(row, ensure_ascii=False, separators=(',', ':'), default=lambda b: b.hex())
            rows = sorted(encode(row) for row in values)
            result[name] = {'filas': len(rows), 'columnas': cols, 'sha256_filas': hash_bytes(('\n'.join(rows)).encode())}
        return {'integridad': integrity, 'version': version, 'tablas': result}
    finally:
        conn.close()


def main():
    p = argparse.ArgumentParser(description=__doc__)
    for what in ('antes-db','despues-db','antes-pref','despues-pref','antes-media','despues-media','salida'):
        p.add_argument('--'+what, type=Path, required=True)
    a = p.parse_args()
    before, after = summarize(a.antes_db), summarize(a.despues_db)
    files = {
        'preferencias': [hash_bytes(a.antes_pref.read_bytes()), hash_bytes(a.despues_pref.read_bytes())],
        'archivo_multimedia': [hash_bytes(a.antes_media.read_bytes()), hash_bytes(a.despues_media.read_bytes())],
    }
    checks = {
        'sqlite_integridad_ambas': before['integridad'] == after['integridad'] == 'ok',
        'sqlite_version_conservada': before['version'] == after['version'] == 2,
        'ocho_tablas_y_filas_conservadas': before['tablas'] == after['tablas'],
        'preferencias_sinteticas_iguales': files['preferencias'][0] == files['preferencias'][1],
        'archivo_multimedia_sintetico_igual': files['archivo_multimedia'][0] == files['archivo_multimedia'][1],
    }
    result = {'ambito': 'Comparación de las copias proporcionadas; procedencia y momento se documentan aparte. Usar solo datos sintéticos.',
              'antes': before, 'despues': after, 'sha256_archivos': files, 'comprobaciones': checks}
    a.salida.write_text(json.dumps(result, ensure_ascii=False, indent=2) + '\n')
    print(json.dumps(checks, ensure_ascii=False, indent=2))
    if not all(checks.values()):
        raise SystemExit('Diferencias detectadas; no afirmar conservación sin investigarlas.')


if __name__ == '__main__':
    main()
