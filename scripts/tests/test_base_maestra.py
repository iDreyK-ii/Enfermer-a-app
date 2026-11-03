import hashlib
import importlib.util
import json
from pathlib import Path
import subprocess
import sys
import unittest

ROOT = Path(__file__).resolve().parents[2]
HERE = ROOT / 'research/base_maestra_v21'
ASSET = ROOT / 'app/src/main/assets/base_maestra_mvp.json'
spec = importlib.util.spec_from_file_location('compile_mvp', HERE / 'compilar_mvp.py')
compiler = importlib.util.module_from_spec(spec)
spec.loader.exec_module(compiler)


class BaseMaestraTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.data = json.loads(ASSET.read_text())
        cls.raw = (HERE / 'BASE_EDITORIAL_NO_CLINICA.md').read_bytes()
        cls.lines = cls.raw.decode().splitlines()

    def test_complete_editorial_input_is_preserved_but_not_shipped_as_clinical_app_content(self):
        self.assertEqual('f0b9107128659d9b225d44424b3d7ea4bdd366ab29ba2b81627d7c380a5908ee', hashlib.sha256(self.raw).hexdigest())
        self.assertEqual(4079, len(self.lines))
        for number in range(1, 14):
            self.assertTrue(any(s.startswith(f'# SECCIÓN {number:02}') for s in self.lines))
        self.assertTrue(any(s.startswith('# ANEXO A') for s in self.lines))
        self.assertTrue(any(s.startswith('# ANEXO B') for s in self.lines))
        self.assertFalse(any(p.name == 'BASE_EDITORIAL_NO_CLINICA.md' for p in (ROOT/'app/src/main').rglob('*')))

    def test_packaging_and_provenance_are_reproducible_without_modifying_source(self):
        before = ASSET.read_bytes()
        result = subprocess.run([sys.executable, str(HERE/'compilar_mvp.py'), '--check'], cwd=ROOT, capture_output=True, text=True)
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertEqual(before, ASSET.read_bytes())
        data, trace = compiler.compile_data()
        self.assertEqual(data, self.data)
        self.assertEqual(trace, json.loads((HERE/'trazabilidad_inventario.json').read_text()))

    def test_nominal_count_is_not_a_claim_of_validated_drugs_or_peruvian_stock(self):
        items = self.data['inventory']
        self.assertEqual(554, len(items))
        self.assertEqual(len(items), len({x['id'] for x in items}))
        self.assertEqual(len(items), len({compiler.norm(x['name']) for x in items}))
        self.assertEqual({'IDENTIFICADO'}, {x['status'] for x in items})
        allowed = {'id','name','kind','status','primary','relations','aliases','formulations','source_lines','notes','existing_id'}
        self.assertTrue(all(set(x) == allowed for x in items))
        self.assertTrue(all(x['primary'] not in x['relations'] for x in items))

    def test_all_import_occurrences_refer_to_a_real_source_line_and_one_master(self):
        ids = {x['id'] for x in self.data['inventory']}
        trace = json.loads((HERE/'trazabilidad_inventario.json').read_text())
        self.assertEqual(737, len(trace))
        for row in trace:
            self.assertIn(row['master'], ids)
            self.assertGreaterEqual(row['line'], 1)
            self.assertLessEqual(row['line'], len(self.lines))
            self.assertTrue(self.lines[row['line'] - 1].strip())
        for item in self.data['inventory']:
            self.assertEqual(set(item['source_lines']), {r['line'] for r in trace if r['master'] == item['id']})

    def test_required_ambiguous_and_non_drug_items_are_retained(self):
        by_name = {x['name']:x for x in self.data['inventory']}
        for name in ('altilice','Imipenem','Alteplasa','Ranibizumab','Insulina degludec','Concentrado de hematíes','Fórmula nutricional enteral tipo Ensure'):
            self.assertIn(name, by_name)
        self.assertEqual('POR-IDENTIFICAR', by_name['altilice']['kind'])
        self.assertEqual('POR-IDENTIFICAR', by_name['Imipenem']['kind'])
        self.assertEqual('HEMOCOMPONENTE', by_name['Concentrado de hematíes']['kind'])
        self.assertEqual('NUTRICIÓN', by_name['Fórmula nutricional enteral tipo Ensure']['kind'])
        self.assertNotIn('altilice', by_name['Alteplasa']['aliases'])

    def test_known_synonyms_and_insulin_formulations_do_not_create_duplicate_masters(self):
        names = {x['name'] for x in self.data['inventory']}
        for alias in ('Potasio cloruro','Sodio cloruro','Calcio gluconato','Magnesio sulfato','Flumazenil','Insulina humana isófana NPH'):
            self.assertNotIn(alias, names)
        insulin = next(x for x in self.data['inventory'] if x['name']=='Insulina humana')
        self.assertTrue(any('NPH' in x for x in insulin['formulations']))
        self.assertEqual(6, sum(bool(x['existing_id']) for x in self.data['inventory']))

    def test_twenty_authored_topics_expose_metadata_not_a_university_cycle(self):
        self.assertEqual(20, len(self.data['topics']))
        for topic in self.data['topics']:
            self.assertTrue(topic['title'] and topic['markdown'] and topic['author'] and topic['sources'])
            self.assertIn(topic['level'], ('Inicial','Intermedio','Avanzado'))
            self.assertIn('PENDIENTE', topic['status'])
            for source in topic['sources']:
                self.assertTrue(source['url'].startswith('https://'))
                self.assertNotIn('@', source['url'].split('/')[2])
                self.assertTrue(source['date'] and source['publisher'])

    def test_atlas_has_eight_distinct_devices_and_piston_is_not_plunger(self):
        devices = {d['id']:d for d in self.data['devices']}
        self.assertEqual({'jeringa','aguja','estetoscopio','tensiometro','cateter-periferico','equipo-iv','bureta','llave-tres-vias'}, set(devices))
        self.assertTrue({'piston','embolo'} <= {p['id'] for p in devices['jeringa']['parts']})
        self.assertTrue({'cateter','aguja-introductora'} <= {p['id'] for p in devices['cateter-periferico']['parts']})
        for device in devices.values():
            self.assertTrue(device['model_note'])
            for part in device['parts']:
                self.assertTrue(part['name'] and part['function'] and part['safety'] and part['shapes'])

    def test_exactly_ten_institutional_corrects_are_separate_from_twenty_own_controls(self):
        official, own = self.data['checklists']
        self.assertEqual(10, len(official['items'])); self.assertTrue(official['official'])
        self.assertIn('alergias', official['items'][8]); self.assertIn('otro medicamento', official['items'][8])
        self.assertIn('p. 6', official['scope']); self.assertIn('neonatal', official['scope'])
        self.assertEqual(20, len(own['items'])); self.assertFalse(own['official'])
        self.assertIn('No es una norma oficial', own['scope'])

    def test_seven_progressive_cases_have_feedback_and_a_real_scoring_key(self):
        cases = self.data['cases']
        self.assertEqual(7, len(cases)); self.assertEqual(21, sum(len(x['questions']) for x in cases))
        self.assertEqual({'diabetes-itu-hta','tce-uci','hda','pancreatitis','neumonia-asma','sepsis-shock','ecg'}, {c['id'] for c in cases})
        for case in cases:
            self.assertIn('SINTÉTICO', case['status'])
            for q in case['questions']:
                self.assertIs(type(q['correct']), int)
                self.assertIn(q['correct'], range(len(q['options'])))
                self.assertTrue(q['scene'] and q['feedback'] and q['rubric'])

    def test_future_modules_do_not_have_fake_function_routes_and_licensing_gates_remain(self):
        states = {m['name']:m for m in self.data['modules']}
        self.assertTrue(all(not m['route'] for m in states.values() if m['status']=='FALTA'))
        for name in ('Braden completo','Norton completo','RASS administrado','Monografías DIGEMID producto a producto'):
            self.assertEqual('BLOQUEADO-POR-VALIDACIÓN', states[name]['status'])
        self.assertEqual('registro-escala/norton', states['Norton completo']['route'])


if __name__ == '__main__':
    unittest.main()
