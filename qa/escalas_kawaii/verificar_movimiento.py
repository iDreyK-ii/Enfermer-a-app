#!/usr/bin/env python3
"""Compara cuatro capturas del APK final en emulador; no mide FPS ni uso clínico."""
from pathlib import Path
from PIL import Image, ImageChops
import hashlib
import json

HERE = Path(__file__).resolve().parent / 'apk/movimiento'
REGION = (170, 190, 310, 325)  # Solo la ilustración, no texto ni reloj.


def load(name):
    path = HERE / name
    with Image.open(path) as image:
        if image.size != (480, 800):
            raise ValueError('Las capturas deben conservar su tamaño original 480×800')
        return image.convert('RGB')


def changed(a, b):
    r, g, blue = ImageChops.difference(a.crop(REGION), b.crop(REGION)).split()
    mask = ImageChops.lighter(ImageChops.lighter(r, g), blue)
    return sum(mask.histogram()[1:])


def main():
    names = ['mov-a.png', 'mov-b.png', 'pausa-a.png', 'pausa-b.png']
    a, b, c, d = map(load, names)
    result = {
        'ambito': 'APK final 0.9, emulador API28: dos fotogramas al moverse y dos tras pulsar pausa; no mide FPS ni teléfono físico',
        'region_xyxy': REGION,
        'pixeles_distintos_movimiento': changed(a, b),
        'pixeles_distintos_pausa': changed(c, d),
        'sha256': {name: hashlib.sha256((HERE / name).read_bytes()).hexdigest() for name in names},
    }
    if result['pixeles_distintos_movimiento'] <= 100 or result['pixeles_distintos_pausa'] != 0:
        raise SystemExit('La comparación no confirma el resultado esperado')
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == '__main__':
    main()
