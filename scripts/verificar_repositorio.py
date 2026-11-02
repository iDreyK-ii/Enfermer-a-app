#!/usr/bin/env python3
"""Guardas locales antes de compartir. No es una auditoría completa de secretos/historial."""
from pathlib import Path, PurePosixPath
import re
import subprocess
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
PRIVATE = {'.env', 'local.properties'}
EXCLUDED = {'.git', '.cache', '.gradle', 'build', 'node_modules', 'keys'}
SUFFIXES = {'.apk', '.aab', '.keystore', '.jks', '.p12', '.pfx', '.key', '.pem', '.db', '.sqlite', '.sqlite3'}
SECRET_PATTERNS = [
    re.compile(rb'sb_secret_[A-Za-z0-9_-]{25,}'),
    re.compile(rb'gh[pousr]_[A-Za-z0-9]{30,}'),
    re.compile(rb'github_pat_[A-Za-z0-9_]{40,}'),
    re.compile(rb'-----BEGIN (?:RSA |EC |OPENSSH |ENCRYPTED )?PRIVATE KEY-----'),
]
TEXT_SUFFIXES = {'.kt', '.kts', '.py', '.json', '.yml', '.yaml', '.md', '.txt', '.sql', '.sh', '.properties', '.example', '.mjs', '.xml', '.java', '.toml', '.ini', '.html'}


def forbidden_path(name: str) -> bool:
    p = PurePosixPath(name)
    return bool(set(p.parts) & EXCLUDED or p.suffix.lower() in SUFFIXES
                or p.name in PRIVATE or (p.name.startswith('.env.') and p.name != '.env.example'))


def suspicious_text(data: bytes) -> bool:
    return any(pattern.search(data) for pattern in SECRET_PATTERNS)


def check() -> list[str]:
    failures = []
    try:
        top = subprocess.check_output(['git', 'rev-parse', '--show-toplevel'], cwd=ROOT,
                                      stderr=subprocess.DEVNULL, text=True).strip()
    except subprocess.CalledProcessError:
        return ['Falta el repositorio Git. Desde el ZIP, clona historial/desarrollo.bundle en otra carpeta antes de ejecutar estas guardas.']
    if Path(top).resolve() != ROOT:
        return ['Se detectó el Git de una carpeta superior, no el del proyecto. Recupera el bundle en una carpeta propia.']
    paths = subprocess.check_output(['git', 'ls-files', '-z'], cwd=ROOT).decode().split('\0')[:-1]
    for name in paths:
        if forbidden_path(name):
            failures.append(f'Archivo privado/generado versionado: {name}')
            continue
        path = ROOT / name
        if path.is_file() and (path.suffix in TEXT_SUFFIXES or path.name == '.env.example'):
            if suspicious_text(path.read_bytes()):
                # Deliberately never print the matched value.
                failures.append(f'Posible credencial privada en: {name}; revisar sin copiarla a logs')
    wrapper = (ROOT / 'gradle/wrapper/gradle-wrapper.properties').read_text()
    if not re.search(r'^distributionSha256Sum=[a-f0-9]{64}$', wrapper, re.M):
        failures.append('Falta SHA-256 de la distribución Gradle')
    ns = '{http://schemas.android.com/apk/res/android}'
    manifest = ET.parse(ROOT / 'app/src/main/AndroidManifest.xml').getroot()
    permissions = {item.attrib[ns+'name'] for item in manifest.findall('uses-permission')}
    if permissions != {'android.permission.INTERNET'}:
        failures.append('Permisos directos de Android cambiaron: revisar explícitamente')
    workflow = (ROOT / '.github/workflows/calidad.yml').read_text()
    for action in re.findall(r'uses:\s*([^\s#]+)', workflow):
        if not re.fullmatch(r'[\w.-]+/[\w./-]+@[a-f0-9]{40}', action):
            failures.append('Las acciones externas deben fijarse a un commit, no a una etiqueta móvil')
    if 'pull_request_target' in workflow or 'secrets.' in workflow:
        failures.append('La CI de pruebas no debe necesitar secretos ni pull_request_target')
    if 'contents: read' not in workflow or 'persist-credentials: false' not in workflow:
        failures.append('Revisar permisos mínimos y credenciales temporales de checkout')
    return failures


if __name__ == '__main__':
    errors = check()
    if errors:
        print('\n'.join(errors), file=sys.stderr)
        raise SystemExit(1)
    print('Guardas de archivos versionados, credenciales evidentes, Gradle, permisos y CI: OK.')
    print('No sustituye revisión humana, análisis del historial o pruebas reales del servicio.')
