#!/usr/bin/env python3
"""Procesa ilustraciones IA existentes; no vuelve a generarlas ni consulta redes.

Uso: python procesar_imagenes.py --input-dir /ruta/con/originales
Los PNG finales se guardan en assets; el registro conserva hashes de entrada/salida.
La generación visual original no es reproducible únicamente con este script.
"""
import argparse
import hashlib
import json
from pathlib import Path

import cv2
import numpy as np
from PIL import Image, ImageFilter

ROOT = Path(__file__).resolve().parents[2]
DEST = ROOT / 'app/src/main/assets/escalas_art'
REGISTER = Path(__file__).with_name('registro_ilustraciones.json')
SOURCES = {
    'general': ('hero-general-clean.png', 'Portapapeles, lupa y libro de consulta'),
    'neuro': ('neuro.png', 'Nube cerebral simbólica y pensamiento'),
    'dolor': ('dolor.png', 'Burbuja de comunicación y corazón'),
    'seguridad': ('seguridad.png', 'Escudo simbólico y apósito decorativo'),
    'neonatal': ('neonatal.png', 'Gorrito y manta, sin bebé ni anatomía'),
    'nutricion': ('nutricion.png', 'Cuenco, cuchara y manzana'),
    'funcion': ('funcion.png', 'Calzado y estrella, actividad cotidiana'),
}


def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def process(source, target):
    im = Image.open(source).convert('RGB')
    rgb = np.asarray(im)
    # The generation used an almost-white background. Remove only the near-white
    # region connected to the canvas corner; enclosed white highlights stay intact.
    near_white = np.uint8(np.all(rgb >= 235, axis=2)) * 255
    if near_white[0, 0] != 255:
        raise ValueError(f'El fondo de {source.name} no coincide con el esperado')
    cv2.floodFill(near_white, None, (0, 0), 128, flags=4)
    alpha = Image.fromarray(np.where(near_white == 128, 0, 255).astype('uint8'))
    alpha = alpha.filter(ImageFilter.GaussianBlur(radius=0.55))
    rgba = im.convert('RGBA')
    rgba.putalpha(alpha)
    box = alpha.point(lambda value: 255 if value > 32 else 0).getbbox()
    if not box:
        raise ValueError(f'Ilustración vacía: {source.name}')
    left, top, right, bottom = box
    pad = 45
    box = (max(0, left - pad), max(0, top - pad), min(im.width, right + pad), min(im.height, bottom + pad))
    art = rgba.crop(box)
    art.thumbnail((900, 540), Image.Resampling.LANCZOS)
    canvas = Image.new('RGBA', (960, 600))
    canvas.alpha_composite(art, ((960 - art.width) // 2, (600 - art.height) // 2))
    canvas.quantize(colors=256, method=Image.Quantize.FASTOCTREE, dither=Image.Dither.NONE).save(target, optimize=True)
    return im.size


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--input-dir', required=True, type=Path)
    args = parser.parse_args()
    DEST.mkdir(parents=True, exist_ok=True)
    assets = []
    for key, (name, description) in SOURCES.items():
        source = args.input_dir / name
        target = DEST / f'escala_{key}.png'
        size = process(source, target)
        assets.append({
            'id': key, 'description': description,
            'source_file': name, 'source_sha256': sha(source), 'source_size': list(size),
            'asset': f'escalas_art/{target.name}', 'asset_sha256': sha(target),
            'asset_size': [960, 600], 'transparent_background': True,
            'is_standardized_test_stimulus': False,
        })
    REGISTER.write_text(json.dumps({
        'origin': 'Ilustraciones simbólicas generadas por IA como recursos individuales, revisadas visualmente; no son instrumentos, instrucciones ni imágenes oficiales.',
        'processing': 'Fondo casi blanco contiguo eliminado; recorte, encaje en 960x600 y cuantización; originales temporales no incluidos en la entrega.',
        'clinical_approval': False, 'assets': assets,
    }, ensure_ascii=False, indent=2) + '\n')
    print(f'{len(assets)} ilustraciones procesadas; ninguna es un estímulo o escala.')


if __name__ == '__main__':
    main()
