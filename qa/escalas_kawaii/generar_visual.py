#!/usr/bin/env python3
"""Genera un desglose HTML offline desde PNG y capturas existentes; no modifica la app."""
from base64 import b64encode
from html import escape
from pathlib import Path
import argparse

ROOT = Path(__file__).resolve().parents[2]
HERE = Path(__file__).resolve().parent
ART = ROOT / 'app/src/main/assets/escalas_art'


def embed(path):
    return 'data:image/png;base64,' + b64encode(path.read_bytes()).decode('ascii')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--salida', type=Path, default=ROOT.parent / 'DESGLOSE_VISUAL_iDreyK_v0.9.0.html')
    args = parser.parse_args()
    illustrations = [
        ('general','Biblioteca','Leer con contexto','Portapapeles con lupa y libro'),
        ('neuro','Neurología','Respuestas y funciones','Nube cerebral simbólica'),
        ('dolor','Dolor','Escuchar el autoinforme','Burbuja de comunicación y corazón'),
        ('seguridad','Seguridad','Riesgos diferentes','Escudo de cuidado decorativo'),
        ('neonatal','Neonatología','Edad y momento','Gorrito y manta'),
        ('nutricion','Nutrición','Cribado, no dieta','Cuenco, cuchara y manzana'),
        ('funcion','Funcionalidad','Actividad y autonomía','Zapatilla y estrella'),
    ]
    cards = ''.join(f'''<article class="art-card"><img src="{embed(ART / ('escala_'+name+'.png'))}" alt="{escape(alt)}"><div><span>{escape(area)}</span><h3>{escape(label)}</h3></div></article>''' for name, area, label, alt in illustrations)
    renders = [
        ('18-escalas-portada-390.png','Portada de escalas · claro','Render Compose en Robolectric; 390 dp, movimiento reducido.'),
        ('19-fps-r-ficha-390.png','Ficha FPS-R · apoyo','Render Compose en Robolectric. El dibujo no es una cara oficial.'),
        ('20-fps-r-clave-390.png','Explicación y fuente','Render Compose en Robolectric; la referencia completa figura en la ficha.'),
        ('21-escalas-compacto-oscuro.png','Teléfono compacto · oscuro','Render Compose en Robolectric; 320 dp, movimiento reducido.'),
    ]
    shots = ''.join(f'''<figure><div class="shot"><img loading="lazy" src="{embed(HERE / 'render' / filename)}" alt="{escape(label)}"></div><figcaption><strong>{escape(label)}</strong><span>{escape(note)}</span></figcaption></figure>''' for filename,label,note in renders)
    apk_shots = [
        ('01-portada-final.png', 'Portada instalada', 'Movimiento reducido por ajuste del sistema.'),
        ('02-catalogo-final.png', 'Catálogo instalado', '45 fichas; no todas son calculadoras.'),
        ('03-filtro-aplicado.png', 'Filtro aplicado', '13 fichas con práctica, simulación o registro.'),
    ]
    apk_figures = ''.join(
        f'''<figure><div class="shot"><img loading="lazy" src="{embed(HERE / 'apk' / name)}" alt="{escape(label)} del APK en emulador"></div><figcaption><strong>{escape(label)}</strong><span>{escape(note)}</span></figcaption></figure>'''
        for name, label, note in apk_shots if (HERE / 'apk' / name).is_file()
    )
    actual = ''
    if apk_figures:
        actual = f'''<section><div class="section-title"><span class="eyebrow">APK EN EMULADOR</span><h2>Una comprobación instalada</h2><p>Capturas sin modificar del APK final 0.9 en emulador Android API 28 con datos sintéticos. No son de un dispositivo físico ni validación clínica.</p></div><div class="phone-row">{apk_figures}</div></section>'''
    document = '''<!doctype html>
<html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>iDreyK 360 · Escalas kawaii 0.9</title><style>
:root{color-scheme:light;--ink:#30233f;--muted:#685b77;--pink:#bd2d79;--teal:#166a70;--border:#e8ddeb;--lav:#f6f0fa}*{box-sizing:border-box}body{margin:0;background:#fbf7fb;color:var(--ink);font-family:system-ui,-apple-system,"Segoe UI",sans-serif;line-height:1.55}a{color:#8d2862;text-underline-offset:3px}header,main,footer{max-width:1220px;margin:auto;padding-left:clamp(20px,5vw,64px);padding-right:clamp(20px,5vw,64px)}header{padding-top:24px;padding-bottom:24px;display:flex;justify-content:space-between;gap:16px;align-items:center}.brand{font-weight:800;letter-spacing:-.03em;font-size:18px}.brand span{color:var(--pink)}.version{font-size:12px;font-weight:700;background:#fff;border:1px solid var(--border);border-radius:100px;padding:7px 12px}main{padding-bottom:64px}.hero{border:1px solid #eddfef;border-radius:32px;padding:clamp(24px,5vw,54px);background:linear-gradient(125deg,#f3e9fa,#fff7f8);display:grid;grid-template-columns:1.15fr .85fr;align-items:center;gap:24px;overflow:hidden}.eyebrow{font-size:11px;font-weight:800;letter-spacing:.12em;text-transform:uppercase;color:var(--teal)}h1{font-size:clamp(36px,5.3vw,64px);line-height:1.06;letter-spacing:-.055em;margin:14px 0 18px}h1 em{font-style:normal;color:var(--pink)}.lead{font-size:clamp(16px,1.7vw,19px);max-width:570px;color:var(--muted);margin:0}.hero img{width:100%;max-width:440px;display:block;margin:auto;filter:drop-shadow(0 14px 25px #b98ba222)}.pills{display:flex;flex-wrap:wrap;gap:8px;margin-top:22px}.pill{font-size:12px;background:#fff;border:1px solid #eadcea;border-radius:100px;padding:7px 12px;font-weight:700}.alert{background:#fff8e9;border:1px solid #eddbb7;border-radius:18px;padding:17px 20px;margin:22px 0;color:#675333;font-size:14px}.stats{display:grid;grid-template-columns:repeat(4,1fr);gap:12px;margin:28px 0}.stat{background:white;border:1px solid var(--border);border-radius:18px;padding:18px}.stat strong{display:block;font-size:28px;letter-spacing:-.04em}.stat span{font-size:13px;color:var(--muted)}section{margin-top:58px}.section-title{max-width:740px;margin-bottom:24px}.section-title h2{font-size:clamp(26px,3vw,36px);letter-spacing:-.04em;line-height:1.18;margin:8px 0 10px}.section-title p{color:var(--muted);margin:0}.art-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:14px}.art-card{background:white;border:1px solid var(--border);border-radius:20px;overflow:hidden}.art-card img{width:100%;height:135px;object-fit:contain;background:linear-gradient(#f8f4fa,#fff);padding:14px}.art-card div{padding:15px 17px 18px}.art-card span{font-size:10px;text-transform:uppercase;letter-spacing:.09em;font-weight:800;color:var(--teal)}.art-card h3{font-size:14px;line-height:1.3;margin:4px 0}.phone-row{display:grid;grid-template-columns:repeat(4,1fr);gap:16px}figure{margin:0}.shot{background:#ece4f1;border:1px solid var(--border);border-radius:23px;overflow:hidden;padding:8px}.shot img{width:100%;height:auto;border-radius:15px;display:block}figcaption{padding:10px 3px;font-size:13px}figcaption strong,figcaption span{display:block}figcaption span{color:var(--muted);font-size:12px}.columns{display:grid;grid-template-columns:1fr 1fr;gap:16px}.panel{background:#fff;border:1px solid var(--border);border-radius:22px;padding:23px}.panel h3{margin:0 0 10px;font-size:17px}.panel ul{padding-left:20px;margin:0}.panel li{margin:7px 0;color:var(--muted);font-size:14px}.sources{display:flex;gap:9px;flex-wrap:wrap}.sources a{background:#fff;border:1px solid var(--border);border-radius:100px;padding:8px 12px;font-size:13px}.footnote{color:var(--muted);font-size:13px;margin-top:14px}footer{padding-top:8px;padding-bottom:35px;font-size:12px;color:var(--muted)}@media(max-width:760px){.hero{grid-template-columns:1fr}.hero img{max-width:270px}.stats{grid-template-columns:repeat(2,1fr)}.art-grid,.phone-row{grid-template-columns:repeat(2,1fr)}.columns{grid-template-columns:1fr}}@media(max-width:390px){.art-grid{grid-template-columns:repeat(2,1fr)}.art-card img{height:110px}.art-card div{padding:11px}.art-card h3{font-size:13px}.phone-row{gap:9px}header{font-size:12px}.version{font-size:10px}}
</style></head><body><header><div class="brand">iDreyK <span>360</span></div><div class="version">ANDROID NATIVO · 0.9 LOCAL</div></header><main>
<section class="hero"><div><span class="eyebrow">Una mejora por etapas</span><h1>Escalas más claras.<br><em>Sin perder el contexto.</em></h1><p class="lead">La app de siempre, con acompañamiento kawaii, fuentes accesibles y explicaciones breves para distinguir qué ofrece cada instrumento.</p><div class="pills"><span class="pill">Prototipo B conservado</span><span class="pill">Lectura sin conexión</span><span class="pill">Movimiento opcional</span></div></div><img src="__HERO__" alt="Ilustración decorativa de un portapapeles, lupa y libro"></section>
<div class="alert"><strong>Solo práctica y consulta.</strong> La revisión de fuentes es parcial; la adaptación no tiene aprobación clínica. Los dibujos no son resultados, preguntas ni caras oficiales.</div>
<div class="stats"><div class="stat"><strong>45</strong><span>fichas documentales</span></div><div class="stat"><strong>43</strong><span>fuentes referenciadas</span></div><div class="stat"><strong>13</strong><span>accesos de distinto tipo</span></div><div class="stat"><strong>7</strong><span>ilustraciones de apoyo</span></div></div>
<section><div class="section-title"><span class="eyebrow">ILUSTRACIONES ORIGINALES DE IA</span><h2>Una pista visual, no una respuesta</h2><p>Siete recursos simbólicos se reutilizan en nueve áreas. Puedes pausar el movimiento; la reducción de movimiento tiene prioridad.</p></div><div class="art-grid">__ART__</div></section>
<section><div class="section-title"><span class="eyebrow">EL DISEÑO EN ANDROID</span><h2>Así se ve la mejora</h2><p>Estas imágenes son renders del código Compose en Robolectric, no fotografías ni capturas de un teléfono físico.</p></div><div class="phone-row">__SHOTS__</div></section>
__ACTUAL__
<section><div class="section-title"><span class="eyebrow">ALCANCE HONESTO</span><h2>Qué se mejoró y qué sigue pendiente</h2></div><div class="columns"><div class="panel"><h3>En esta fase</h3><ul><li>Filtro por tipo de acceso: práctica, registro, simulación o documentación.</li><li>Doce claves educativas enlazadas a sus fuentes y fechas de consulta.</li><li>Norton y RASS enlazan sus registros externos ya existentes, solo para ejemplos adultos.</li><li>Las fuentes HTTPS se abren únicamente cuando eliges hacerlo.</li></ul></div><div class="panel"><h3>Por revisar</h3><ul><li>La mayor parte de las fichas son documentales, no calculadoras.</li><li>No se añadieron formularios protegidos ni sustitutos de caras oficiales.</li><li>Faltan revisión profesional, pruebas en teléfono real y permisos por instrumento.</li><li>El material de otros países no reemplaza protocolos locales.</li></ul></div></div></section>
<section><div class="section-title"><span class="eyebrow">FUENTES DESTACADAS</span><h2>Para consultar directamente</h2><p>Consulta parcial del 3 de noviembre de 2026. Abrir enlaces externos puede requerir Internet.</p></div><div class="sources"><a href="https://www.glasgowcomascale.org/faq/">Glasgow · FAQ</a><a href="https://www.the4at.com/userguide">4AT · guía</a><a href="https://www.iasp-pain.org/resources/faces-pain-scale-revised/">IASP · FPS-R</a><a href="https://scireproject.com/wp-content/uploads/2025/01/Clinician-Summary_Norton-Scale-v.8.1.pdf">SCIRE · Norton</a><a href="https://www.england.nhs.uk/publication/national-pews-observation-and-escalation-charts/">NHS · PEWS</a></div><p class="footnote">En la vista previa aislada los enlaces pueden no abrir; descarga este HTML o copia la dirección. Las condiciones y profundidad de cada fuente se documentan en la app y en el informe de investigación.</p></section>
</main><footer>iDreyK 360 · revisión local no publicada · las funciones y contenidos deben validarse antes de cualquier uso asistencial.</footer></body></html>'''
    html = document.replace('__HERO__',embed(ART/'escala_general.png')).replace('__ART__',cards).replace('__SHOTS__',shots).replace('__ACTUAL__',actual)
    args.salida.write_text(html, encoding='utf-8')
    installed_count = sum((HERE / 'apk' / name).is_file() for name, _, _ in apk_shots)
    print(f'Desglose autocontenido: {args.salida} · {args.salida.stat().st_size} bytes · 7 dibujos, {len(renders)} renders y {installed_count} capturas del APK en emulador')


if __name__ == '__main__':
    main()
