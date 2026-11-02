#!/usr/bin/env python3
"""Compara una exportación con un commit del Git canónico, sin extraer el ZIP ni usar red.

Ejecutar desde la copia confiable del repositorio, no desde código importado sin revisar.
Verifica fuentes y bit ejecutable, no APK, firma, clínica ni ausencia exhaustiva de secretos.
"""
from __future__ import annotations

import argparse
import hashlib
import json
import os
from pathlib import Path, PurePosixPath
import re
import stat
import subprocess
import sys
import zipfile

ROOT = Path(__file__).resolve().parents[1]
MAX_FILES = 10000
MAX_FILE_BYTES = 32 * 1024 * 1024
MAX_TOTAL_BYTES = 192 * 1024 * 1024
GENERATED = {'.git', '.cache', '.gradle', '.idea', '.vscode', '.kotlin',
             '__pycache__', '__MACOSX', 'node_modules', 'build', 'coverage'}


def safe_name(name: str) -> str:
    if (not name or name.startswith('/') or '\\' in name or '\x00' in name
            or re.match(r'^[A-Za-z]:', name)
            or any(p in ('', '.', '..') for p in name.rstrip('/').split('/'))):
        raise ValueError('Ruta de archivo no admitida: ' + repr(name))
    return name.rstrip('/')


def excluded_extra(name: str) -> bool:
    path = PurePosixPath(name)
    return (bool(set(path.parts) & GENERATED) or path.name == '.DS_Store'
            or name == 'historial/desarrollo.bundle')


def git(repo: Path, *args: str) -> bytes:
    result = subprocess.run(['git', '-C', str(repo), *args], capture_output=True)
    if result.returncode:
        raise ValueError('No se pudo leer la referencia del Git canónico. Comprueba el repositorio y el commit; no se hizo ninguna subida.')
    return result.stdout


def reference(repo: Path, revision: str) -> dict:
    top = Path(git(repo, 'rev-parse', '--show-toplevel').decode().strip()).resolve()
    if top != repo.resolve():
        raise ValueError('Usa el Git del propio proyecto, no el de una carpeta superior.')
    commit = git(repo, 'rev-parse', '--verify', '--end-of-options', revision + '^{commit}').decode().strip()
    tree = git(repo, 'rev-parse', commit + '^{tree}').decode().strip()
    files = {}
    total = 0
    for entry in git(repo, 'ls-tree', '-rlz', commit).split(b'\x00'):
        if not entry:
            continue
        attrs, raw_name = entry.split(b'\t', 1)
        mode, kind, oid, size_text = attrs.decode('ascii').split()
        name = safe_name(raw_name.decode('utf-8'))
        if kind != 'blob' or mode not in ('100644', '100755'):
            raise ValueError('Referencia con submódulo/enlace/tipo no admitido: ' + name)
        size = int(size_text)
        total += size
        if size > MAX_FILE_BYTES or total > MAX_TOTAL_BYTES or len(files) >= MAX_FILES:
            raise ValueError('La referencia supera los límites de comprobación.')
        data = git(repo, 'cat-file', 'blob', oid)
        if len(data) != size:
            raise ValueError('Tamaño Git inesperado: ' + name)
        files[name] = {'sha256': hashlib.sha256(data).hexdigest(), 'bytes': size, 'git_mode': mode}
    if not files:
        raise ValueError('La referencia no contiene archivos.')
    return {'format': 'idreyk-source-transfer-v1', 'commit': commit, 'tree': tree,
            'tracked_files': len(files), 'files': files,
            'scope': 'Fuentes versionadas y bit ejecutable; no APK, firma, datos del teléfono ni auditoría de secretos.'}


def digest(stream, limit: int) -> str:
    sha = hashlib.sha256()
    count = 0
    while True:
        block = stream.read(65536)
        if not block:
            break
        count += len(block)
        if count > limit:
            raise ValueError('El contenido real supera el tamaño permitido.')
        sha.update(block)
    return sha.hexdigest()


def empty_result(manifest: dict, source: str) -> dict:
    return {'commit': manifest['commit'], 'tree': manifest['tree'], 'source': source,
            'expected_files': len(manifest['files']), 'identical_files': 0,
            'missing': [], 'changed': [], 'unexpected': [], 'mode_differences': [],
            'mode_unavailable': [], 'ignored_generated': [], 'errors': []}


def finish(result: dict) -> dict:
    result['source_bytes_identical'] = not any(result[k] for k in ('missing', 'changed', 'unexpected', 'errors'))
    result['executable_modes_verified'] = not any(result[k] for k in ('missing', 'mode_differences', 'mode_unavailable', 'errors'))
    result['ok'] = result['source_bytes_identical'] and result['executable_modes_verified']
    result['limitations'] = 'Paridad con este commit, no auditoría clínica/seguridad ni compatibilidad de firma. Los elementos excluidos no se validan.'
    return result


def compare_one(name: str, meta: dict, size: int, executable, opener, result: dict):
    if size != meta['bytes']:
        result['changed'].append({'path': name, 'reason': 'size', 'expected_bytes': meta['bytes'], 'actual_bytes': size})
    else:
        with opener() as stream:
            sha = digest(stream, min(meta['bytes'], MAX_FILE_BYTES))
        if sha != meta['sha256']:
            result['changed'].append({'path': name, 'reason': 'sha256', 'actual_sha256': sha})
        else:
            result['identical_files'] += 1
    if executable is None:
        result['mode_unavailable'].append(name)
    elif executable != (meta['git_mode'] == '100755'):
        result['mode_differences'].append(name)


def verify_directory(manifest: dict, directory: Path) -> dict:
    directory = directory.resolve(strict=True)
    if not directory.is_dir():
        raise ValueError('La ruta de la copia debe ser una carpeta.')
    result = empty_result(manifest, str(directory))
    found = set()
    expected = manifest['files']
    for current, dirs, files in os.walk(directory, followlinks=False):
        for name in list(dirs):
            child = Path(current) / name
            relative = child.relative_to(directory).as_posix()
            if child.is_symlink():
                result['errors'].append('Enlace de directorio no admitido: ' + relative)
                dirs.remove(name)
            elif excluded_extra(relative) and not any(p.startswith(relative + '/') for p in expected):
                result['ignored_generated'].append(relative + '/')
                dirs.remove(name)
        for file in files:
            path = Path(current) / file
            name = safe_name(path.relative_to(directory).as_posix())
            found.add(name)
            if len(found) > MAX_FILES:
                raise ValueError('La copia contiene demasiados archivos.')
            if path.is_symlink() or not path.is_file():
                result['errors'].append('Enlace/tipo de archivo no admitido: ' + name)
                continue
            if name not in expected:
                result['ignored_generated' if excluded_extra(name) else 'unexpected'].append(name)
                continue
            mode = path.stat().st_mode
            executable = bool(mode & 0o111) if os.name != 'nt' else None
            compare_one(name, expected[name], path.stat().st_size, executable,
                        lambda p=path: p.open('rb'), result)
    result['missing'] = sorted(set(expected) - found)
    return finish(result)


def choose_prefix(names: list[str], expected: set[str], explicit: str | None) -> str:
    if explicit is not None:
        return safe_name(explicit) + '/' if explicit else ''
    anchors = [p for p in ('app/build.gradle.kts', 'AGENTS.md', 'README.md') if p in expected]
    if not anchors:
        anchors = sorted(expected)[:1]
    candidates = {''}
    for name in names:
        if excluded_extra(name):
            continue
        for anchor in anchors:
            if name == anchor or name.endswith('/' + anchor):
                candidates.add(name[:-len(anchor)])
    if len(candidates) > 100:
        raise ValueError('Demasiadas raíces posibles. Indica --prefijo.')
    scored = {p: sum(n.startswith(p) and n[len(p):] in expected for n in names) for p in candidates}
    maximum = max(scored.values())
    best = [p for p, score in scored.items() if score == maximum]
    if maximum == 0 or len(best) != 1:
        raise ValueError('No se puede identificar una raíz única del proyecto. Indica --prefijo sin ocultar diferencias.')
    return best[0]


def verify_zip(manifest: dict, path: Path, prefix: str | None = None) -> dict:
    result = empty_result(manifest, str(path))
    with zipfile.ZipFile(path) as archive:
        entries = archive.infolist()
        if len(entries) > MAX_FILES or sum(z.file_size for z in entries) > MAX_TOTAL_BYTES:
            raise ValueError('El ZIP supera los límites de tamaño o cantidad de archivos.')
        files = {}
        all_names = set()
        for item in entries:
            name = safe_name(item.filename)
            if name in all_names:
                raise ValueError('Entrada ZIP duplicada: ' + name)
            all_names.add(name)
            mode = item.external_attr >> 16
            kind = stat.S_IFMT(mode)
            allowed_kind = stat.S_IFDIR if item.is_dir() else stat.S_IFREG
            if kind not in (0, allowed_kind):
                raise ValueError('Enlace/tipo ZIP no admitido: ' + name)
            if item.flag_bits & 1:
                raise ValueError('No se admiten ZIP cifrados en esta comprobación.')
            if not item.is_dir():
                if item.file_size > MAX_FILE_BYTES:
                    raise ValueError('Archivo ZIP demasiado grande: ' + name)
                files[name] = item
        root = choose_prefix(list(files), set(manifest['files']), prefix)
        result['zip_prefix'] = root
        found = set()
        for original, item in files.items():
            if not original.startswith(root):
                result['ignored_generated' if excluded_extra(original) else 'unexpected'].append(original)
                continue
            name = original[len(root):]
            found.add(name)
            if name not in manifest['files']:
                result['ignored_generated' if excluded_extra(name) else 'unexpected'].append(name)
                continue
            mode = item.external_attr >> 16
            executable = bool(mode & 0o111) if item.create_system == 3 and mode else None
            compare_one(name, manifest['files'][name], item.file_size, executable,
                        lambda z=item: archive.open(z), result)
        result['missing'] = sorted(set(manifest['files']) - found)
    return finish(result)


def save_report(path: Path, data: dict, manifest: dict):
    resolved = path.resolve()
    if resolved.is_relative_to(ROOT):
        relative = resolved.relative_to(ROOT).as_posix()
        tracked_now = set(git(ROOT, 'ls-files', '-z').decode('utf-8').split('\x00'))
        if relative in manifest['files'] or relative in tracked_now or '.git' in resolved.relative_to(ROOT).parts:
            raise ValueError('La salida no puede sobrescribir fuentes versionadas o metadatos Git.')
    resolved.parent.mkdir(parents=True, exist_ok=True)
    resolved.write_text(json.dumps(data, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')


def main(argv=None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    sub = parser.add_subparsers(dest='command', required=True)
    make = sub.add_parser('manifiesto', help='Generar hashes a partir de objetos Git, no de archivos sin commit.')
    make.add_argument('--ref', default='HEAD')
    make.add_argument('--salida', type=Path, required=True)
    check = sub.add_parser('verificar', help='Comprobar una carpeta o ZIP contra el commit canónico.')
    check.add_argument('--ref', required=True, help='Commit/tag exacto aprobado, no una referencia de la copia importada.')
    group = check.add_mutually_exclusive_group(required=True)
    group.add_argument('--directorio', type=Path)
    group.add_argument('--zip', type=Path)
    check.add_argument('--prefijo', help='Carpeta raíz dentro del ZIP; se detecta si hay una única candidata.')
    check.add_argument('--salida', type=Path)
    args = parser.parse_args(argv)
    try:
        manifest = reference(ROOT, args.ref)
        if args.command == 'manifiesto':
            save_report(args.salida, manifest, manifest)
            print(json.dumps({'commit': manifest['commit'], 'tracked_files': manifest['tracked_files'],
                              'manifest': str(args.salida)}, ensure_ascii=False))
            return 0
        report = (verify_directory(manifest, args.directorio) if args.directorio
                  else verify_zip(manifest, args.zip, args.prefijo))
        if args.salida:
            save_report(args.salida, report, manifest)
        print(json.dumps(report, ensure_ascii=False, indent=2))
        return 0 if report['ok'] else 1
    except (ValueError, OSError, zipfile.BadZipFile, UnicodeError, RuntimeError) as error:
        print('Traslado NO aprobado: ' + str(error), file=sys.stderr)
        return 2


if __name__ == '__main__':
    raise SystemExit(main())
