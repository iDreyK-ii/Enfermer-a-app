#!/usr/bin/env python3
"""Build a self-contained report from actual native captures; never generates mock app images."""
from pathlib import Path
import argparse, base64, hashlib, html, json, shutil

SCREENS = [
    ("01-inicio-390", "Inicio · teléfono", "La identidad, imagen y navegación del Prototipo B siguen presentes. Las nuevas entradas aparecen al desplazarse; no reemplazan los módulos anteriores."),
    ("02-biblioteca-390", "Biblioteca de enfermería", "20 temas locales, búsqueda de texto y filtros por área/nivel. Son contenidos educativos iniciales con revisión pendiente, no un plan por ciclos."),
    ("03-atlas-jeringa-390", "Atlas · parte seleccionada", "Pistón o sello seleccionado, distinto del vástago del émbolo. Vector, nombre, función y precaución proceden del código y datos del atlas."),
    ("04-calculo-390", "Cálculo decimal · goteo", "500 mL, 4 h y factor 20 fueron introducidos por la prueba. El resultado matemático es 42 gotas/min con redondeo visible. Es un ejercicio ficticio, no una pauta."),
    ("05-higiene-390", "Higiene · estado en pausa", "Cronómetro real a 0/50 s, selector, ilustración y controles nativos. Movimiento reducido activado. No certifica técnica ni descontaminación."),
    ("06-caso-390", "Caso sintético progresivo", "Primera etapa con datos inventados, opciones y advertencias. Las respuestas se guardan antes de avanzar; los siete casos esperan revisión docente."),
    ("07-pae-390", "PAE · campos editables", "Entrada real de texto ficticio en datos subjetivos y campo de datos objetivos. Se preservan los campos anteriores. Esta imagen no demuestra una exportación PDF."),
    ("08-inicio-tablet-800", "Inicio · tablet", "Layout nativo a 800 dp, conservando el hero, cuadrícula y nuevas filas. Es una columna amplia, no se afirma un diseño multipanel ni prueba de tablet física."),
    ("09-atlas-tablet-800", "Atlas · tablet", "Catéter/cánula flexible y aguja introductora aparecen como partes diferentes. Zoom, capas y alternativa textual. Esquema genérico, no modelo de fabricante validado."),
    ("10-atlas-compacto-320-oscuro", "Compacto · tema oscuro", "Llave de tres vías en 320 dp. La prueba continúa seleccionando una parte y regresando al atlas. El recorte inferior es el límite del área desplazable, no ausencia de contenido."),
]

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--capturas", type=Path, default=Path(__file__).parent / "capturas")
    ap.add_argument("--salida", type=Path, default=Path(__file__).parent / "desglose_visual.html")
    ap.add_argument("--copiar-desde", type=Path)
    args = ap.parse_args()
    args.capturas.mkdir(parents=True, exist_ok=True)
    if args.copiar_desde:
        for key, _, _ in SCREENS:
            for ext in ("png", "json"):
                shutil.copyfile(args.copiar_desde / f"{key}.{ext}", args.capturas / f"{key}.{ext}")
    cards, checks = [], []
    for i, (key, title, text) in enumerate(SCREENS, 1):
        image = args.capturas / f"{key}.png"
        meta = json.loads((args.capturas / f"{key}.json").read_text())
        digest = hashlib.sha256(image.read_bytes()).hexdigest()
        assert digest == meta["sha256"] and meta["physical_device"] is False and meta["apk_emulator"] is False
        assert "API 35" in meta["render"] and meta["sample_colors"] > 40
        data = base64.b64encode(image.read_bytes()).decode("ascii")
        theme = meta.get("theme", "oscuro" if i == 10 else "claro")
        size = f'{meta["width"]} × {meta["height"]} px · mdpi · {theme}'
        cards.append(f'''<figure class="{'wide' if meta['width'] == 800 else 'phone'}" id="s{i}">
        <div class="label"><span>{i:02d}</span><h3>{html.escape(title)}</h3></div>
        <p class="meta">{size}</p><img src="data:image/png;base64,{data}" alt="{html.escape(title)}; render del código Android, no captura de dispositivo" width="{meta['width']}" height="{meta['height']}" loading="lazy">
        <figcaption>{html.escape(text)}</figcaption></figure>''')
        checks.append(f'<tr><td>{html.escape(image.name)}</td><td><code>{digest}</code></td></tr>')
    result = '''<!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>iDreyK 360 · desglose visual del MVP</title>
<style>
:root{font-family:system-ui,-apple-system,Segoe UI,sans-serif;color:#342841;background:#f7f3fa;line-height:1.6}*{box-sizing:border-box}body{margin:0}header,main,footer{max-width:1120px;margin:auto;padding:30px 24px}header{padding-top:48px}h1{font-size:clamp(28px,5vw,44px);line-height:1.15;max-width:850px;margin:18px 0}h2{font-size:23px;margin:0 0 10px}h3{font-size:18px;margin:0}p{margin:8px 0 16px}.eyebrow{letter-spacing:.14em;text-transform:uppercase;color:#8657a7;font-size:12px;font-weight:800}.pill{display:inline-block;padding:5px 12px;background:#ede4f5;border-radius:24px;font-size:12px;margin:0 6px 8px 0}.notice{background:#fff8e7;border:1px solid #dfcca0;border-left:4px solid #b58839;padding:18px 22px;border-radius:12px;margin:24px 0}.grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:24px;align-items:start}figure{margin:0;background:white;border:1px solid #e1d7eb;padding:20px;border-radius:20px;min-width:0}.label{display:flex;align-items:baseline;gap:12px}.label span{color:#9a6cbc;font-size:13px;font-weight:750}.meta{font-size:12px;color:#766584;margin:6px 0 18px}img{display:block;max-width:100%;height:auto;border:1px solid #e9dfef;border-radius:9px;margin:0 auto;background:#f6effa}.phone img{max-width:min(100%,390px)}.wide{grid-column:1/-1}.wide img{max-width:min(100%,800px)}figcaption{padding-top:18px;font-size:14px;color:#584865}.summary{background:#eee5f5;padding:24px;border-radius:18px;margin:0 0 26px}.summary ul{padding-left:22px;margin-bottom:0}nav{display:flex;flex-wrap:wrap;gap:10px}nav a{color:#7c468f;font-size:14px}details{margin:28px 0;border-top:1px solid #dfd2e9;padding-top:20px}summary{font-weight:700;cursor:pointer}table{border-collapse:collapse;width:100%;font-size:12px;margin-top:15px}td{vertical-align:top;padding:9px;border-bottom:1px solid #ddd}code{overflow-wrap:anywhere}footer{font-size:13px;color:#73617e;padding-bottom:45px}@media(max-width:650px){header,main,footer{padding-left:16px;padding-right:16px}.grid{grid-template-columns:1fr}figure{padding:14px}.wide{grid-column:auto}.notice{padding:16px}td{display:block}}@media print{header,main,footer{max-width:none}figure{break-inside:avoid}nav{display:none}.grid{display:block}figure{margin:18px 0}.phone img{max-height:620px;width:auto}details{display:none}}
</style></head><body><header><div class="eyebrow">Enfermería iDreyK 360 · verificación visual</div><h1>La misma app nativa.<br>El nuevo bloque, visible.</h1><p>Base Maestra 2.1 · Android 0.6.0-dev · 3 de noviembre de 2026</p><span class="pill">Prototipo B conservado</span><span class="pill">10 renders del código real</span><span class="pill">Teléfono · tablet · compacto</span><div class="notice"><strong>No son capturas de un APK instalado.</strong><br>Son imágenes producidas por la vista Android/Compose de producción en Robolectric, con gráficos nativos API 35. No son maquetas reconstruidas en HTML. No verifican un teléfono físico, emulador APK, GPU, teclado, TalkBack ni fluidez.</div><nav><a href="#s1">Inicio</a><a href="#s2">Biblioteca</a><a href="#s3">Atlas</a><a href="#s4">Cálculos</a><a href="#s5">Higiene</a><a href="#s6">Casos</a><a href="#s7">PAE</a><a href="#s8">Tablet</a><a href="#s10">Compacto</a></nav></header><main><section class="summary"><h2>Qué se conserva y qué se amplía</h2><ul><li>Paleta, tipografías, recursos, componentes y política de rendimiento existentes. <code>Design.kt</code> y <code>Performance.kt</code> no se modificaron.</li><li>Accesos nuevos integrados en la navegación anterior; no una app paralela ni cambio de framework.</li><li>Cuaderno, biblioteca, calculadoras, atlas, bioseguridad y casos usan los mismos componentes nativos.</li><li>Las capturas se estabilizan con movimiento reducido. En la app se conserva la política adaptable; aquí no se midieron FPS.</li><li>Datos de ejemplo inequívocamente ficticios. La impresión/PDF real, los instrumentos bloqueados y la validación clínica siguen pendientes.</li></ul></section><div class="grid">''' + "\n".join(cards) + '''</div><details><summary>Método, trazabilidad y límites</summary><p>Robolectric 4.14.1 / NATIVE graphics / Android framework API 35. Rasterización del contenido de la Activity mediante <code>View.draw(Canvas(Bitmap))</code>. API 35 evitó un recorte observado en el renderer software API 28. PixelCopy no recibió un callback de display en este entorno sin pantalla.</p><p>Cada imagen tiene un JSON adyacente con tamaño, versión, método, hash y los indicadores <code>physical_device: false</code> y <code>apk_emulator: false</code>. No se afirma paridad de píxeles con un diseño aportado ni verificación clínica. El desglose es un documento: no reemplaza al APK.</p><table><tbody>''' + "\n".join(checks) + '''</tbody></table></details></main><footer>Consulta la matriz de estados y las pruebas en <code>docs/MVP_BASE_MAESTRA_v21.md</code> y <code>qa/RESULTADOS_v0.6.0.md</code>. Las dos pruebas instrumentadas PDF se compilaron, pero no se ejecutaron. Ninguna imagen acredita administración segura ni autoriza una decisión asistencial.</footer></body></html>'''
    args.salida.parent.mkdir(parents=True, exist_ok=True)
    args.salida.write_text(result, encoding="utf-8")
    print(json.dumps({"file":str(args.salida),"bytes":args.salida.stat().st_size,"native_images":len(SCREENS),"physical_device":False},ensure_ascii=False))

if __name__ == "__main__":
    main()
