"""Comprobaciones estructurales; no validan clínica, accesibilidad real ni derechos de terceros."""
import hashlib
import json
from pathlib import Path
import struct
import unittest

ROOT = Path(__file__).resolve().parents[2]
ASSETS = ROOT / 'app/src/main/assets'
RESEARCH = ROOT / 'research/escalas_kawaii'


class EscalasKawaiiTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.register = json.loads((RESEARCH / 'registro_ilustraciones.json').read_text())
        cls.catalog = json.loads((ASSETS / 'escalas_clinicas_v1.json').read_text())

    def test_seven_distinct_local_pngs_match_manifest_and_have_transparency(self):
        entries = self.register['assets']
        self.assertEqual(7, len(entries))
        self.assertEqual(7, len({e['id'] for e in entries}))
        self.assertEqual(7, len({e['asset_sha256'] for e in entries}))
        self.assertFalse(self.register['clinical_approval'])
        for e in entries:
            with self.subTest(id=e['id']):
                path = ASSETS / e['asset']
                self.assertTrue(path.is_file())
                data = path.read_bytes()
                self.assertEqual(hashlib.sha256(data).hexdigest(), e['asset_sha256'])
                self.assertRegex(e['source_sha256'], r'^[a-f0-9]{64}$')
                self.assertEqual(data[:8], b'\x89PNG\r\n\x1a\n')
                w, h = struct.unpack('>II', data[16:24])
                self.assertEqual((960, 600), (w, h))
                self.assertEqual([w, h], e['asset_size'])
                self.assertIn(b'tRNS', data)
                self.assertTrue(e['transparent_background'])
                self.assertFalse(e['is_standardized_test_stimulus'])

    def test_only_cover_and_fact_sheets_mount_art_not_question_or_answer_screens(self):
        library = (ROOT / 'app/src/main/java/com/idreyk/enfermeria360/ScreensScales.kt').read_text()
        cover, other_scale_screens = library.split('fun CapurroCalculatorScreen', 1)
        self.assertIn('ClinicalAreaIllustration(ClinicalVisualLearning.general)', cover)
        self.assertNotIn('ClinicalAreaIllustration(', other_scale_screens)
        catalog = (ROOT / 'app/src/main/java/com/idreyk/enfermeria360/ScreensClinicalCatalog.kt').read_text()
        fact_sheets, answer_forms = catalog.split('fun NumericPainPracticeScreen', 1)
        self.assertIn('ClinicalAreaIllustration(art)', fact_sheets)
        self.assertIn('Solo con acceso: práctica, simulación o registro', fact_sheets)
        self.assertNotIn('ClinicalAreaIllustration(', answer_forms)

    def test_two_new_links_are_record_only_and_norton_is_adult(self):
        items = {i['id']: i for i in self.catalog['instrumentos']}
        for name in ('norton', 'rass'):
            self.assertEqual('registro', items[name]['estado'])
            self.assertEqual('registro-escala/' + name, items[name]['ruta'])
            self.assertIn('adult', items[name]['poblacion_detalle'].lower())
            self.assertIn('no administra', items[name]['metodo'].lower())
        self.assertEqual(['adultos'], items['norton']['poblaciones'])

    def test_existing_standardized_source_rules_remain_distinct(self):
        model = (ROOT / 'app/src/main/java/com/idreyk/enfermeria360/ClinicalCatalog.kt').read_text()
        self.assertIn('fourAtTotal', model)
        self.assertIn('numericPain', model)
        ui = (ROOT / 'app/src/main/java/com/idreyk/enfermeria360/ScreensLibrary.kt').read_text()
        self.assertIn('Sin total: componente NV', ui)
        self.assertNotIn('escala_dolor.png', ui)


if __name__ == '__main__':
    unittest.main()
