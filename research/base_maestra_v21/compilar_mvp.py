#!/usr/bin/env python3
"""Reproducible nominal import, NEVER a clinical monograph compiler.

Source line numbers and the supplied source hash remain auditable. Editorial
relationships are not indications, product registration or stock verification.
"""
from pathlib import Path
import argparse
import hashlib
import json
import re
import unicodedata

ROOT = Path(__file__).resolve().parents[2]
HERE = Path(__file__).resolve().parent
SOURCE = HERE / 'BASE_EDITORIAL_NO_CLINICA.md'
ASSET = ROOT / 'app/src/main/assets/base_maestra_mvp.json'


def norm(s):
    return ''.join(c for c in unicodedata.normalize('NFKD', s.lower()) if not unicodedata.combining(c)).strip()


def slug(s):
    return re.sub(r'[^a-z0-9]+', '-', norm(s)).strip('-')[:78]


# Explicit nominal synonyms only. No silent conversion of altilice or imipenem.
ALIASES = {
    'noradrenalina': 'Norepinefrina',
    'noradrenalina (norepinefrina)': 'Norepinefrina',
    'adrenalina': 'Epinefrina',
    'metamizol': 'Metamizol sódico',
    'fitomenadiona (vitamina k1)': 'Fitomenadiona',
    'acido acetilsalicilico (aspirina)': 'Ácido acetilsalicílico',
    'kcl': 'Cloruro de potasio',
    'potasio cloruro': 'Cloruro de potasio',
    'sodio cloruro': 'Cloruro de sodio',
    'sodio bicarbonato': 'Bicarbonato de sodio',
    'calcio gluconato': 'Gluconato de calcio',
    'magnesio sulfato': 'Sulfato de magnesio',
    'flumazenil': 'Flumazenilo',
    'insulina humana isofana nph': 'Insulina humana',
    'glucosa en agua': 'Glucosa',
    'macrogol 3350 + electrolitos': 'Macrogol + electrolitos',
    'macrogol 3350 + potasio cloruro + sodio cloruro + sodio bicarbonato con o sin sodio sulfato': 'Macrogol + electrolitos',
    'dextrosa': 'Glucosa',
    'insulina regular': 'Insulina humana',
    'insulina humana regular': 'Insulina humana',
    'insulina humana regular (cristalina)': 'Insulina humana',
    'insulina humana nph': 'Insulina humana',
    'insulina nph': 'Insulina humana',
    'insulina humana de accion corta': 'Insulina humana',
    'insulina humana de accion intermedia': 'Insulina humana',
}


def inventory(lines):
    entries = {}
    occurrences = []

    def put(name, path, number, form=None, kind=None, note=''):
        name = name.strip().strip('.').strip()
        if not name:
            return
        original = name
        canonical = ALIASES.get(norm(name), name)
        # Concentrations in the submitted inventory remain UNVERIFIED formulations,
        # never recommendations, safe concentrations or a separate active substance.
        for pattern, parent in ((r'^Cloruro de sodio\s+0[,.]9\s*%', 'Cloruro de sodio'),
                                (r'^Dextrosa (?:al )?(?:5\s*%|hipertónica)', 'Glucosa'),
                                (r'^Poligelina\s+3[,.]5\s*%', 'Poligelina')):
            if re.search(pattern, name, re.I):
                form = original
                canonical = parent
        if canonical == 'Macrogol + electrolitos':
            form = original
            kind = 'COMBINACIÓN-POR-IDENTIFICAR'
            note = 'Familia nominal; composición fija por confirmar. Las formulaciones listadas no se presumen intercambiables.'
        if original == 'Glucosa en agua':
            form = original
        if canonical == 'Insulina humana' and norm(name) != 'insulina humana':
            form = original
        if 'combinaciones' in norm(canonical) and '+' not in canonical:
            kind = kind or 'GRUPO-POR-IDENTIFICAR'
        key = norm(canonical)
        if key not in entries:
            inferred = 'COMBINACIÓN' if '+' in canonical else 'DCI-NOMINAL'
            n = norm(canonical + ' ' + path)
            if any(word in norm(canonical) for word in ('vacuna', 'inmunoglobulina', 'suero ant', 'factor viii', 'factor ix', 'insulina', 'interferon', 'epoetina', 'filgrastim', 'antitoxina', 'concentrado de complejo')) or norm(canonical).endswith('mab') or canonical in ('Etanercept', 'Alteplasa', 'Estreptoquinasa', 'Asparaginasa', 'Glucagón', 'Gonadotrofina coriónica', 'Menotropina'):
                inferred = 'BIOLÓGICO'
            if any(word in norm(canonical) for word in ('hematies', 'plasma fresco', 'plaquetas', 'crioprecipitado')):
                inferred = 'HEMOCOMPONENTE'
            elif any(word in norm(canonical) for word in ('nutric', 'aminoacidos', 'lipidos', 'ensure')):
                inferred = 'NUTRICIÓN'
            elif any(word in norm(canonical) for word in ('agua ', 'solucion', 'cloruro de sodio', 'glucosa', 'poligelina', 'cristaloides')):
                inferred = 'SOLUCIÓN-SOPORTE'
            if canonical in ('altilice', 'Imipenem', 'Antiemético no especificado', 'Antibióticos sin especificar', 'Premezclas de insulina por identificar', 'Soluciones/electrolitos combinados por identificar'):
                inferred = 'POR-IDENTIFICAR'
            entries[key] = dict(id='nom-' + slug(canonical), name=canonical,
                                kind=kind or inferred, status='IDENTIFICADO',
                                primary=path, relations=[], aliases=[], formulations=[],
                                source_lines=[], notes=[], existing_id='')
        item = entries[key]
        if original != canonical and original not in item['aliases']:
            item['aliases'].append(original)
        if path != item['primary'] and path not in item['relations']:
            item['relations'].append(path)
        if form and form not in item['formulations']:
            item['formulations'].append(form)
        if number not in item['source_lines']:
            item['source_lines'].append(number)
        if note and note not in item['notes']:
            item['notes'].append(note)
        occurrences.append(dict(line=number, nominal=original, master=item['id'], path=path))

    # All tree leaves and semicolon indexes in Annex B, sections 3 and 4.
    begin = next(i for i, s in enumerate(lines) if s.startswith('## 3. ÁRBOL MAESTRO'))
    end = next(i for i in range(begin, len(lines)) if lines[i].startswith('## 5.'))
    in_fence = False
    nodes = []
    for i in range(begin, end):
        text = lines[i]
        if text.startswith('```'):
            if in_fence:
                for pos, (depth, label, ln) in enumerate(nodes):
                    if pos + 1 < len(nodes) and nodes[pos + 1][0] > depth:
                        continue
                    parents = []
                    last = depth
                    for d, title, _ in reversed(nodes[:pos]):
                        if d < last:
                            parents.insert(0, title)
                            last = d
                    path = ' → '.join(parents) or 'Ubicación editorial pendiente'
                    if ':' in label:
                        family, names = label.split(':', 1)
                        for name in names.split(';'):
                            put(name, path + ' → ' + family.strip(), ln)
                    elif depth > 0:
                        put(label, path, ln)
                nodes = []
            in_fence = not in_fence
            continue
        if not in_fence or not text.strip(' │'):
            continue
        match = re.match(r'^([ │]*)(?:├──|└──)\s*(.+)', text)
        if match:
            nodes.append((len(match[1]) + 4, match[2], i + 1))
        else:
            nodes.append((0, text.strip(), i + 1))

    # Section 06: case/material nominal bullets, not the clinical narrative.
    b = next(i for i, s in enumerate(lines) if s.startswith('# SECCIÓN 06'))
    e = next(i for i in range(b, len(lines)) if lines[i].startswith('### 3. Insulinas'))
    area = 'Sección 06 · ubicación principal por revisar'
    active = False
    for i in range(b, e):
        s = lines[i]
        if s.startswith('#### '):
            area = 'Sección 06 → ' + s[5:]
            active = True
        if not active or not s.startswith('- '):
            continue
        original = s[2:].strip().rstrip('.')
        raw = original.split(' (', 1)[0].rstrip('.')
        if 'altilice' in raw:
            put('altilice', area, i + 1, kind='POR-IDENTIFICAR', note='Transcripción ambigua: no se transforma en alteplasa ni tenecteplasa. Consultar original.')
        elif raw.startswith('Antiemético'):
            put('Antiemético no especificado', area, i + 1, kind='POR-IDENTIFICAR')
        elif raw.startswith('Cristaloides IV'):
            put('Cristaloides IV por identificar', area, i + 1, kind='SOLUCIÓN-SOPORTE')
        elif raw.startswith('Antibióticos como grupo'):
            put('Antibióticos sin especificar', area, i + 1, kind='POR-IDENTIFICAR')
        elif raw.startswith('Soluciones/electrolitos'):
            put('Soluciones/electrolitos combinados por identificar', area, i + 1, kind='POR-IDENTIFICAR')
        elif raw.startswith('Fórmula nutricional'):
            put('Fórmula nutricional enteral tipo Ensure', area, i + 1, kind='NUTRICIÓN', note='Nombre citado en el material; composición y producto pendientes de identificar. No es una DCI.')
        elif raw.startswith('Agua estéril'):
            put('Agua estéril para preparación', area, i + 1, kind='SOLUCIÓN-SOPORTE', note='Uso y producto pendientes; no equivale a solución IV de mantenimiento.')
        else:
            put(raw, area, i + 1, note=('Sal/formulación de metamizol por comprobar en el material de origen.' if raw == 'Metamizol' else ''))

    # Named insulin inventory in section 06, not kinetic profiles or doses.
    insulin_rows = {
        'Análogo rápido': ['Insulina lispro', 'Insulina aspart', 'Insulina glulisina'],
        'Humana de acción corta': ['Insulina humana regular'],
        'Intermedia': ['Insulina humana NPH'],
        'Prolongada': ['Insulina glargina', 'Insulina detemir'],
        'Ultralarga': ['Insulina degludec'],
        'Premezclas': ['Premezclas de insulina por identificar'],
    }
    for i in range(e, e + 30):
        for marker, names in insulin_rows.items():
            if lines[i].startswith('| ' + marker + ' |'):
                for name in names:
                    put(name, 'Sección 06 → Insulinas', i + 1)

    # Priority names explicitly supplied but potentially absent from the tree.
    # Each points to the exact supplied line; no inferred indication or registry.
    for marker, names in [
        ('- Adrenalina/epinefrina, atropina', ['Epinefrina', 'Atropina', 'Amiodarona', 'Adenosina', 'Lidocaína']),
        ('- Dobutamina, dopamina', ['Dobutamina', 'Dopamina', 'Norepinefrina', 'Vasopresina']),
        ('- Sulfato de magnesio, gluconato/cloruro', ['Sulfato de magnesio', 'Gluconato de calcio', 'Cloruro de calcio', 'Bicarbonato de sodio']),
        ('- Enoxaparina, heparina sódica', ['Enoxaparina', 'Heparina sódica', 'Warfarina', 'Apixabán']),
        ('- Hidrocortisona, dexametasona', ['Hidrocortisona', 'Dexametasona', 'Salbutamol', 'Ipratropio', 'Budesonida']),
        ('- Naloxona, flumazenil', ['Naloxona', 'Flumazenil']),
        ('- Dextrosa para hipoglucemia', ['Glucosa', 'Glucagón']),
        ('- Omeprazol, metoclopramida, ondansetrón', ['Omeprazol', 'Metoclopramida', 'Ondansetrón']),
        ('- Captopril, enalapril, losartán', ['Captopril', 'Enalapril', 'Losartán', 'Amlodipino', 'Furosemida']),
    ]:
        line = next(i for i, s in enumerate(lines[b:], b) if s.startswith(marker))
        for name in names:
            put(name, 'Sección 06 → Prioridades editoriales', line + 1)

    # Institutional mentions remain nominal, without inheriting their unverified stock claims.
    for name, marker in [('Ranibizumab', 'RANIBIZUMAB'), ('Dactinomicina', 'DACTINOMICINA / FLUDARABINA'), ('Fludarabina', 'DACTINOMICINA / FLUDARABINA')]:
        ln = next(i for i, text in enumerate(lines) if text == marker)
        put(name, 'Anexo B → Mención institucional editorial, sin cotejo independiente', ln + 1, note='La mención no acredita compra, disponibilidad ni inclusión en PNUME.')

    # Annex B combination and formulation lists, not new invented monographs.
    comb = next(i for i, s in enumerate(lines) if s.startswith('### 7.1. Combinaciones'))
    end_comb = next(i for i in range(comb, len(lines)) if lines[i].startswith('## 8.'))
    for i in range(comb, end_comb):
        s = lines[i]
        if s.startswith('- ') and '+' in s:
            nominal = s[2:].split(':', 1)[0].split(', cuando', 1)[0].strip().rstrip('.')
            if nominal in ('Bupivacaína + glucosa', 'Lidocaína + glucosa'):
                parent = nominal.split(' + ')[0]
                put(parent, 'Anexo B → Formulación, no nueva DCI', i + 1, form=nominal)
            else:
                put(nominal, 'Anexo B → Combinaciones', i + 1)

    # Clinical cross-index table: provenance relationships, not recommendations.
    cross = next(i for i, s in enumerate(lines) if s.startswith('## 6. Relaciones cruzadas'))
    for i in range(cross, comb):
        cells = [c.strip() for c in lines[i].strip('|').split('|')]
        if lines[i].startswith('|') and len(cells) == 3 and not cells[0].startswith(('DCI', '---')):
            put(cells[0], 'Índice editorial → ' + cells[2], i + 1)

    existing = {'Paracetamol': 'paracetamol', 'Ceftriaxona': 'ceftriaxona', 'Enoxaparina': 'enoxaparina',
                'Omeprazol': 'omeprazol', 'Furosemida': 'furosemida', 'Insulina humana': 'insulina-regular'}
    for name, id_ in existing.items():
        entries[norm(name)]['existing_id'] = id_
    items = sorted(entries.values(), key=lambda v: norm(v['name']))
    assert len({e['id'] for e in items}) == len(items), 'ID collision: review, never silently merge'
    assert all(e['status'] == 'IDENTIFICADO' for e in items)
    assert any(e['name'] == 'altilice' and e['kind'] == 'POR-IDENTIFICAR' for e in items)
    return items, occurrences


def compile_data():
    raw = SOURCE.read_bytes()
    assert hashlib.sha256(raw).hexdigest() == 'f0b9107128659d9b225d44424b3d7ea4bdd366ab29ba2b81627d7c380a5908ee'
    data = json.loads((HERE / 'contenido.json').read_text())
    items, origins = inventory(raw.decode('utf-8').splitlines())
    data.update(format=1, version='2.1-mvp.1', date='2026-11-03', source_sha256=hashlib.sha256(raw).hexdigest(), inventory=items)
    return data, origins


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    data, origins = compile_data()
    encoded = (json.dumps(data, ensure_ascii=False, indent=2) + '\n').encode('utf-8')
    trace = (json.dumps(origins, ensure_ascii=False, indent=2) + '\n').encode('utf-8')
    provenance = HERE / 'trazabilidad_inventario.json'
    if args.check:
        assert ASSET.read_bytes() == encoded, 'Packaged asset differs from editorial data'
        assert provenance.read_bytes() == trace, 'Inventory provenance differs'
    else:
        ASSET.write_bytes(encoded)
        provenance.write_bytes(trace)
    print(json.dumps(dict(inventory=len(data['inventory']), occurrences=len(origins), topics=len(data['topics']), devices=len(data['devices']), cases=len(data['cases']), bytes=len(encoded)), ensure_ascii=False))


if __name__ == '__main__':
    main()
