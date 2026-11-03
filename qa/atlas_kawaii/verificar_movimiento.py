#!/usr/bin/env python3
"""Reanaliza cuatro capturas instaladas; no mide FPS, alineación clínica ni hardware físico."""
from pathlib import Path
from PIL import Image, ImageChops
import json

HERE = Path(__file__).resolve().parent / 'movimiento'

def load(name):
    with Image.open(HERE / name) as image:
        return image.convert('RGB')

def changed(a, b):
    # Region of the image only; excludes the status bar clock and other UI.
    a, b = a.crop((20, 390, 460, 650)), b.crop((20, 390, 460, 650))
    red, green, blue = ImageChops.difference(a, b).split()
    mask = ImageChops.lighter(ImageChops.lighter(red, green), blue)
    return sum(mask.histogram()[1:])

def vertical_shift(a, b, rect):
    x1, y1, x2, y2 = rect
    patch = a.crop(rect)
    costs = []
    for dy in range(-5, 6):
        other = b.crop((x1, y1 + dy, x2, y2 + dy))
        hist = ImageChops.difference(patch, other).histogram()
        cost = sum(i % 256 * count for i, count in enumerate(hist))
        costs.append((cost, dy))
    return min(costs)[1]

moving1, moving2 = load('mov-1.png'), load('mov-2.png')
paused1, paused2 = load('pausa-1.png'), load('pausa-2.png')
result = {
    'scope': 'Dos fotogramas en movimiento y dos en pausa del APK preliminar instalado en emulador API28. No mide rendimiento ni valida anatomía.',
    'moving_changed_pixels': changed(moving1, moving2),
    'marker_patch_shift_px': vertical_shift(moving1, moving2, (75, 530, 140, 610)),
    'distal_patch_shift_px': vertical_shift(moving1, moving2, (315, 425, 420, 535)),
    'paused_changed_pixels': changed(paused1, paused2),
}
assert result['moving_changed_pixels'] > 100
assert result['marker_patch_shift_px'] == result['distal_patch_shift_px'] != 0
assert result['paused_changed_pixels'] == 0
print(json.dumps(result, ensure_ascii=False, indent=2))
