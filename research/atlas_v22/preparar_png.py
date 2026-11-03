#!/usr/bin/env python3
"""Recorte de candidatos existentes, no generación de detalle médico.
Requiere Pillow, numpy y opencv-python-headless. Guarda medidas nativas y de exportación.
Los originales rechazados nunca se empaquetan. Revisión humana profesional pendiente.
"""
from pathlib import Path
import json, hashlib
import cv2
import numpy as np
from PIL import Image
ROOT = Path(__file__).resolve().parents[2]
HERE = Path(__file__).resolve().parent
OUT = ROOT / 'app/src/main/assets/atlas'
OUT.mkdir(exist_ok=True)
QA = ROOT / '.cache/atlas-mask-review'; QA.mkdir(parents=True, exist_ok=True)
CANDIDATES = {'jeringa':'jeringa-corregida', 'aguja':'aguja-final', 'estetoscopio':'estetoscopio', 'equipo-iv':'equipo-iv', 'tensiometro':'tensiometro'}
FILES = {'jeringa':'atlas_jeringa_luer_lock','aguja':'atlas_aguja_hipodermica','estetoscopio':'atlas_estetoscopio_adulto','equipo-iv':'atlas_equipo_venoclisis','tensiometro':'atlas_tensiometro_aneroide'}
records = []
for id, source in CANDIDATES.items():
    path = HERE / 'candidatos' / (source + '.png')
    rgb = np.array(Image.open(path).convert('RGB'))
    h, w = rgb.shape[:2]
    retouches = []
    if id == 'estetoscopio':
        # El candidato tenía una mancha/pseudoorificio central inexistente en el diafragma.
        blemish = np.zeros((h,w),np.uint8)
        cv2.circle(blemish, (1188,518), 11, 255, -1)
        rgb = cv2.inpaint(rgb, blemish, 4, cv2.INPAINT_TELEA)
        retouches.append('Eliminación local de mancha central del diafragma, sin añadir componentes.')
    threshold = 125 if id == 'tensiometro' else 232 if id == 'estetoscopio' else 249
    mask = ((rgb.min(axis=2) < threshold) * 255).astype(np.uint8)
    if id == 'tensiometro':
        # Conserva la esfera clara, no la sombra de estudio alrededor del objeto.
        cv2.ellipse(mask,(894,352),(78,53),-15,0,360,255,-1)
        cv2.fillPoly(mask,[np.array([(1018,486),(1036,473),(1052,478),(1062,501),(1091,507),(1101,532),(1072,549),(1044,544),(1038,519)],np.int32)],255)
    mask = cv2.morphologyEx(mask, cv2.MORPH_CLOSE, np.ones((3,3),np.uint8))
    # Conserva componentes desconectados reales, como la funda de la aguja; quita ruido mínimo.
    count, labels, stats, _ = cv2.connectedComponentsWithStats(mask)
    mask[:] = 0
    for i in range(1,count):
        if stats[i,cv2.CC_STAT_AREA] >= 45: mask[labels == i] = 255
    # Rellena reflejos pequeños interiores, nunca el gran espacio dentro de un tubo enrollado.
    contours, hierarchy = cv2.findContours(mask, cv2.RETR_CCOMP, cv2.CHAIN_APPROX_SIMPLE)
    if hierarchy is not None:
        for i,c in enumerate(contours):
            if hierarchy[0][i][3] >= 0 and cv2.contourArea(c) < (120 if id == 'estetoscopio' else 5000):
                hole = np.zeros_like(mask);cv2.drawContours(hole,[c],-1,255,-1)
                if id != 'tensiometro' or rgb.min(axis=2)[hole>0].mean() < 230:
                    cv2.drawContours(mask,[c],-1,255,-1)
    if id == 'jeringa':
        # Una pieza de plástico blanca no debe volverse un contorno de alambre.
        contours,_ = cv2.findContours(mask,cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)
        cv2.drawContours(mask,contours,-1,255,-1)
    if id == 'tensiometro':
        # Máscara del candidato existente con rembg/u2netp; no genera piezas nuevas.
        mask = np.array(Image.open(HERE/'mascara-tensiometro.png').convert('L'))
        mask[mask < 24] = 0
        retouches.append('Máscara u2netp + matting para retirar la sombra opaca; píxeles de producto originales conservados.')
    # Suavizado subpíxel y eliminación del blanco de premezcla en los bordes.
    alpha = cv2.GaussianBlur(mask, (3,3), .55).astype(np.float32) / 255
    color = np.clip((rgb.astype(np.float32) - (1-alpha[:,:,None])*255) / np.maximum(alpha[:,:,None],.001),0,255).astype(np.uint8)
    if id == 'tensiometro':
        # Sombras de borde semitransparentes oscuras, nunca una aureola blanca opaca.
        edge = alpha < .6
        color[edge] = 0
        alpha[edge] *= .4
    color[alpha == 0] = 0
    rgba = Image.fromarray(np.dstack([color, (alpha*255).astype(np.uint8)]), 'RGBA')
    native_size = list(rgba.size)
    rgba = rgba.resize((2000, round(2000*h/w)), Image.Resampling.LANCZOS)
    file = OUT / (FILES[id]+'.png'); rgba.save(file, optimize=True)
    a = np.array(rgba.getchannel('A'))
    assert a.min() == 0 and a.max() == 255 and (a==0).mean() > .25
    assert a[0,:].max() == a[-1,:].max() == a[:,0].max() == a[:,-1].max() == 0
    for name,bg in [('oscuro','#15263b'),('claro','#f4edf8')]:
        review = Image.new('RGBA',rgba.size,bg);review.alpha_composite(rgba)
        review.thumbnail((1000,650));review.convert('RGB').save(QA/f'{id}-{name}.jpg',quality=90)
    records.append(dict(id=id, file='atlas/'+file.name, sha256=hashlib.sha256(file.read_bytes()).hexdigest(), native_file=path.name, native_sha256=hashlib.sha256(path.read_bytes()).hexdigest(), native_dimensions=native_size, export_dimensions=list(rgba.size), mode=rgba.mode, transparent_fraction=round(float((a==0).mean()),4), bytes=file.stat().st_size, processing='Recorte con máscara y antialias; ampliación Lanczos a 2000 px, NO detalle nativo de 2000 px.', retouches=retouches, alpha_bbox=rgba.getchannel('A').getbbox()))
(HERE/'procesamiento_png.json').write_text(json.dumps(records,ensure_ascii=False,indent=2)+'\n')
print(json.dumps(records,ensure_ascii=False,indent=2))
