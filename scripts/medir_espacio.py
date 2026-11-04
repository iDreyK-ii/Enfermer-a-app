#!/usr/bin/env python3
"""Estima el espacio persistible; no borra ni modifica archivos.

El cálculo es conservador (tamaño lógico), no garantiza el límite real del
servicio ni sustituye una copia de seguridad. Se pueden indicar otras rutas.
"""
import argparse
import json
import os
from pathlib import Path

# Carpetas regenerables excluidas en el entorno Arena en esta revisión.
EXCLUDED_DIRS = {
    '.arena', '.cache', '.local', '.mypy_cache', '.next', '.nox', '.npm', '.nuxt',
    '.output', '.parcel-cache', '.playwright-cli', '.pytest_cache', '.ruff_cache',
    '.svelte-kit', '.tox', '.turbo', '.venv', '.vite', '__pycache__', 'build',
    'coverage', 'dist', 'node_modules', 'out', 'target',
}


def estimate(root):
    root = Path(root).resolve(strict=True)
    if not root.is_dir():
        raise ValueError('La ruta debe ser un directorio')
    total = count = errors = 0
    def failed(_):
        nonlocal errors
        errors += 1
    for directory, dirs, files in os.walk(root, followlinks=False, onerror=failed):
        dirs[:] = [name for name in dirs if name not in EXCLUDED_DIRS and not (Path(directory) / name).is_symlink()]
        for name in files:
            path = Path(directory) / name
            if name in {'.git-credentials', '.netrc'} or (path.parent.name == '.git' and name in {'config', 'credentials'}):
                continue
            try:
                total += path.lstat().st_size
                count += 1
            except OSError:
                errors += 1
    return {'bytes_logicos': total, 'archivos': count, 'errores_lectura': errors}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
    parser.add_argument('--limite-mib', type=float, default=128.0)
    parser.add_argument('--reserva-mib', type=float, default=0.0, help='Tamaño previsto de una entrega adicional')
    parser.add_argument('--limite-archivos', type=int, default=10000)
    parser.add_argument('--umbral', type=float, default=0.90)
    parser.add_argument('--json', action='store_true')
    args = parser.parse_args()
    if args.limite_mib <= 0 or args.reserva_mib < 0 or args.limite_archivos <= 0 or not 0 < args.umbral <= 1:
        parser.error('límites y umbral deben ser positivos; la reserva no puede ser negativa')
    result = estimate(args.root)
    projected = result['bytes_logicos'] + args.reserva_mib * 1048576
    result.update({
        'mib_aproximados': round(result['bytes_logicos'] / 1048576, 2),
        'reserva_mib': args.reserva_mib,
        'mib_proyectados': round(projected / 1048576, 2),
        'limite_mib_orientativo': args.limite_mib,
        'limite_archivos_orientativo': args.limite_archivos,
        'alerta': (projected >= args.limite_mib * 1048576 * args.umbral or
                   result['archivos'] >= args.limite_archivos * args.umbral or result['errores_lectura'] > 0),
        'nota': 'Estimación de tamaño lógico, no cuota garantizada; revisar también el disco real y los respaldos.',
    })
    if args.json:
        print(json.dumps(result, ensure_ascii=False, indent=2))
    else:
        print(f"Estimación persistible: {result['mib_aproximados']} MiB / ~{args.limite_mib:g} MiB; {result['archivos']} / ~{args.limite_archivos} archivos.")
        if args.reserva_mib:
            print(f"Con la reserva de {args.reserva_mib:g} MiB: aproximadamente {result['mib_proyectados']} MiB.")
        print('AVISO: margen reducido o lectura incompleta; revisa antes de añadir archivos.' if result['alerta'] else 'Margen orientativo disponible; no es una garantía de cuota.')
        print(result['nota'])


if __name__ == '__main__':
    main()
