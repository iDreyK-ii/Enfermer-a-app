#!/usr/bin/env python3
"""Construye el catálogo documental offline; no descarga ni diagnostica.
La investigación y los datos editables están en catalogo_fuente.json.
"""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SOURCE = Path(__file__).with_name('catalogo_fuente.json')
DEST = ROOT / 'app/src/main/assets/escalas_clinicas_v1.json'


def build(check=False):
    raw = json.loads(SOURCE.read_text(encoding='utf-8'))
    data = json.dumps(raw, ensure_ascii=False, indent=2) + '\n'
    if check:
        if not DEST.is_file() or DEST.read_text(encoding='utf-8') != data:
            raise SystemExit('El catálogo empaquetado no coincide con su fuente editorial.')
    else:
        DEST.write_text(data, encoding='utf-8')
    return raw


if __name__ == '__main__':
    import argparse
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--comprobar', action='store_true')
    args = parser.parse_args()
    data = build(args.comprobar)
    print(f"Catálogo documental: {len(data['instrumentos'])} fichas, {len(data['fuentes'])} fuentes. No es certificación clínica.")
