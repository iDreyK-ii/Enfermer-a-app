#!/usr/bin/env python3
"""Validar/preparar una publicación. SIN --enviar nunca contacta un servidor.
Ejemplo: python backend/publicar_paquete.py catalogo.json
Solo operador: SUPABASE_URL y SUPABASE_SECRET_KEY en el entorno, nunca en el APK.
"""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import urllib.error
import urllib.request
from urllib.parse import urlsplit
import jsonschema

ROOT = Path(__file__).resolve().parents[1]
COLLECTIONS = ('medicamentos', 'casos', 'listas_seguridad', 'protocolos')


def validate(raw: str, published: bool = False) -> dict:
    if len(raw.encode('utf-8')) > 2_000_000: raise ValueError('El paquete supera 2 MB.')
    data = json.loads(raw)
    schema = json.loads((ROOT / 'content-tools/esquema_contenido_v3.schema.json').read_text())
    jsonschema.validate(data, schema, format_checker=jsonschema.FormatChecker())
    if data['id_paquete'] != 'catalogo-oficial' or data.get('tipo_paquete', 'contenido') != 'contenido':
        raise ValueError('Se requiere id_paquete=catalogo-oficial; no publicar respaldos personales.')
    entries = [(kind, entry) for kind in COLLECTIONS for entry in data.get(kind, [])]
    if not 1 <= len(entries) <= 500: raise ValueError('Admite entre 1 y 500 registros agregados.')
    if len({(kind, entry['id']) for kind, entry in entries}) != len(entries): raise ValueError('IDs duplicados dentro de un tipo.')
    for _, entry in entries:
        for source in entry.get('fuentes', []):
            parsed = urlsplit(source['url'])
            if parsed.scheme != 'https' or not parsed.hostname or parsed.username is not None or parsed.password is not None:
                raise ValueError('Las referencias requieren HTTPS, dominio y ninguna credencial incrustada.')
    if published:
        if not data.get('licencia', '').strip(): raise ValueError('Documenta licencia y procedencia.')
        if any(entry.get('revision') != 'declarada_por_autor' for _, entry in entries):
            raise ValueError('No se publican borradores. Completar/revisar y documentar fuentes antes.')
    return {
        'version': data['version_contenido'], 'min_app_version': 4,
        'package_json': raw, 'sha256': hashlib.sha256(raw.encode('utf-8')).hexdigest(),
        'published': published,
    }


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl): return None


def send(record: dict):
    url = os.environ.get('SUPABASE_URL', '').rstrip('/')
    secret = os.environ.get('SUPABASE_SECRET_KEY', '')
    if not re.fullmatch(r'https://[a-z0-9-]+\.supabase\.co', url):
        raise ValueError('Configura la URL HTTPS de tu propio proyecto Supabase.')
    if not re.fullmatch(r'sb_secret_[A-Za-z0-9_-]+', secret):
        raise ValueError('Configura la clave secreta del operador en el entorno local; nunca en el APK ni en Git.')
    body = json.dumps(record, ensure_ascii=False).encode('utf-8')
    request = urllib.request.Request(url + '/rest/v1/catalog_releases', body, method='POST', headers={
        'apikey': secret, 'Content-Type': 'application/json', 'Prefer': 'return=minimal',
    })
    # No redirects: never forward the operator's secret to another origin.
    with urllib.request.build_opener(NoRedirect).open(request, timeout=30) as response:
        if response.status not in (200, 201, 204): raise ValueError('El servidor no confirmó la escritura.')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('archivo', type=Path)
    parser.add_argument('--enviar', action='store_true', help='Escribir en el servidor configurado; requiere autorización del operador.')
    parser.add_argument('--publicar', action='store_true', help='Hacer visible la revisión; requiere contenido revisado. Sin esta opción queda no publicada.')
    args = parser.parse_args()
    try:
        record = validate(args.archivo.read_text(encoding='utf-8'), args.publicar)
        print(f"Formato comprobado. Versión {record['version']}; SHA-256 {record['sha256']}.")
        print('Esto no verifica exactitud clínica, permisos ni la identidad profesional del revisor.')
        if args.enviar:
            send(record)
            print('Servidor confirmó la creación.' + (' Publicación visible.' if args.publicar else ' Aún no publicada.'))
        else:
            print('Modo validación: no se envió ningún archivo ni se contactó un servidor.')
    except urllib.error.HTTPError as error:
        print(f'El servidor rechazó la operación (HTTP {error.code}). No se muestran cuerpos, claves ni tokens.'); return 1
    except (OSError, ValueError, jsonschema.ValidationError) as error:
        print('No se completó: ' + (error.message if isinstance(error, jsonschema.ValidationError) else str(error))); return 1
    return 0


if __name__ == '__main__': raise SystemExit(main())
