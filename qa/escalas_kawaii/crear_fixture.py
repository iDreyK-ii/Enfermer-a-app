#!/usr/bin/env python3
"""Crea una COPIA sintética v2 para ensayar actualización; nunca toca la base fuente.

Uso: python crear_fixture.py --base /ruta/base-sintetica-v2.db --salida /ruta/nueva
La carpeta de salida debe estar vacía y ser temporal. No usar bases de pacientes reales.
"""
import argparse
import hashlib
import json
from pathlib import Path
import shutil
import sqlite3
import uuid

ROOT = Path(__file__).resolve().parents[2]
TABLES = {'content', 'packages', 'favorites', 'notes', 'practice', 'meta', 'notebooks', 'media'}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--base', required=True, type=Path)
    parser.add_argument('--salida', required=True, type=Path)
    args = parser.parse_args()
    source = args.base.resolve(strict=True)
    destination = args.salida.resolve()
    if destination.exists() and any(destination.iterdir()):
        raise FileExistsError('La carpeta de salida debe estar vacía; no se sobrescribirá nada.')
    destination.mkdir(parents=True, exist_ok=True)
    dbfile = destination / 'fixture.db'
    with source.open('rb') as src, dbfile.open('xb') as dst:
        shutil.copyfileobj(src, dst)
    asset = ROOT / 'app/src/main/assets/escalas_art/escala_general.png'
    media_id = str(uuid.uuid4())
    filename = media_id + '.png'
    media = destination / filename
    shutil.copyfile(asset, media)
    stamp = 1700000000000
    note_id = 'qa-preservacion-090'
    record = {'id': note_id, 'tipo': 'note', 'persona_ficticia': '', 'titulo': 'Apunte sintético de QA',
              'campos': {'contenido': 'Texto ficticio para comprobar conservación al actualizar.', 'etiquetas': 'QA'},
              'modificado': stamp}
    with sqlite3.connect(dbfile) as db:
        tables = {r[0] for r in db.execute("SELECT name FROM sqlite_master WHERE type='table'")}
        if not TABLES <= tables or db.execute('PRAGMA user_version').fetchone()[0] != 2:
            raise ValueError('La fuente no coincide con el esquema de prueba v2')
        if db.execute('PRAGMA quick_check').fetchone()[0] != 'ok':
            raise ValueError('La copia no pasó quick_check')
        db.execute('INSERT OR IGNORE INTO favorites(key) VALUES(?)', ('med/paracetamol',))
        db.execute('INSERT INTO notes(patient_id,text,created) VALUES(?,?,?)', ('mario', 'Nota ficticia de conservación QA, sin datos reales.', stamp))
        db.execute('INSERT INTO practice(type,summary,created) VALUES(?,?,?)', ('Prueba de conservación', 'Ejercicio sintético sin paciente real.', stamp))
        db.execute('INSERT INTO notebooks VALUES(?,?,?,?,?,?)', (note_id, 'note', '', record['titulo'], json.dumps(record, ensure_ascii=False), stamp))
        db.execute('INSERT INTO media VALUES(?,?,?,?,?,?)', (media_id, 'Imagen sintética QA', 'image/png', filename, media.stat().st_size, stamp))
        db.commit()
        counts = {t: db.execute(f'SELECT COUNT(*) FROM "{t}"').fetchone()[0] for t in sorted(TABLES)}
    prefs = destination / 'preferences.xml'
    prefs.write_text('<?xml version="1.0" encoding="utf-8" standalone="yes" ?>\n<map><boolean name="welcomed" value="true"/><string name="name">Prueba sintética</string><string name="performance">full</string></map>\n')
    info = {'synthetic': True, 'database_schema': 2, 'rows': counts, 'media_filename': filename,
            'media_sha256': hashlib.sha256(media.read_bytes()).hexdigest(),
            'source_fixture_sha256': hashlib.sha256(source.read_bytes()).hexdigest()}
    (destination / 'fixture.json').write_text(json.dumps(info, ensure_ascii=False, indent=2) + '\n')
    print(json.dumps(info, ensure_ascii=False, indent=2))


if __name__ == '__main__':
    main()
