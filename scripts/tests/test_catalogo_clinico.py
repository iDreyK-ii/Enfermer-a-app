"""Consistencia editorial offline; no evaluación clínica de los instrumentos."""
import importlib.util
import json
from pathlib import Path
import re
import unittest
from urllib.parse import urlsplit

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / 'research/escalas/catalogo_fuente.json'
ASSET = ROOT / 'app/src/main/assets/escalas_clinicas_v1.json'


class CatalogoClinicoTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.data = json.loads(SOURCE.read_text())
        cls.items = cls.data['instrumentos']
        cls.sources = cls.data['fuentes']

    def test_editorial_and_packaged_bytes_match(self):
        self.assertEqual(SOURCE.read_bytes(), ASSET.read_bytes())

    def test_generator_is_reproducible_and_check_mode_does_not_change_files(self):
        spec = importlib.util.spec_from_file_location('clinical_builder', ROOT / 'research/escalas/compilar_catalogo.py')
        module = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(module)
        before = ASSET.read_bytes()
        module.build(check=True)
        self.assertEqual(before, ASSET.read_bytes())

    def test_unique_inventory_and_sources(self):
        self.assertEqual(44, len(self.items))
        self.assertEqual(44, len({x['id'] for x in self.items}))
        self.assertEqual(40, len(self.sources))
        self.assertEqual(40, len({x['id'] for x in self.sources}))

    def test_every_fact_sheet_names_version_limits_rights_and_review_depth(self):
        source_ids = {x['id'] for x in self.sources}
        for entry in self.items:
            for field in ('nombre', 'area', 'objetivo', 'poblacion_detalle', 'version', 'metodo', 'limites', 'derechos', 'siguiente'):
                self.assertTrue(entry[field].strip(), (entry['id'], field))
            self.assertTrue(set(entry['fuentes']) <= source_ids)
            self.assertEqual('pendiente', entry['revision_clinica'])
        self.assertTrue(all(x['lectura'].strip() for x in self.sources))

    def test_sources_use_https_without_embedded_credentials(self):
        for source in self.sources:
            url = urlsplit(source['url'])
            self.assertEqual('https', url.scheme)
            self.assertTrue(url.hostname)
            self.assertIsNone(url.username)
            self.assertIsNone(url.password)
            self.assertRegex(source['consulta'], r'^\d{4}-\d{2}-\d{2}$')

    def test_no_documentary_or_permission_card_is_a_fake_calculator(self):
        interactive = [x for x in self.items if x['ruta']]
        self.assertEqual(11, len(interactive))
        for entry in self.items:
            self.assertEqual(bool(entry['ruta']), entry['estado'] in ('calculo', 'registro', 'simulacion'))

    def test_population_separation_is_explicit(self):
        self.assertEqual({'adultos', 'pediatria', 'neonatos'}, {p for x in self.items for p in x['poblaciones']})
        by_id = {x['id']: x for x in self.items}
        for name in ('4at', 'news2', 'morse', 'braden'):
            self.assertEqual(['adultos'], by_id[name]['poblaciones'])
        self.assertEqual(['neonatos'], by_id['apgar']['poblaciones'])
        self.assertEqual('capurro/A', by_id['capurro-a']['ruta'])
        self.assertEqual('capurro/B', by_id['capurro-b']['ruta'])

    def test_claims_are_not_marked_as_exhaustive_or_clinically_validated(self):
        self.assertIn('no exhaustivo', self.data['alcance'])
        contract = json.loads((ROOT / 'CONTINUIDAD.json').read_text())
        self.assertFalse(contract['contenido_clinico']['inventario_exhaustivo'])
        self.assertFalse(contract['traslado']['validacion_clinica'])
        self.assertEqual(len(self.items), contract['contenido_clinico']['fichas_documentales'])

    def test_known_native_routes_and_license_notice_are_versioned(self):
        kotlin = (ROOT / 'app/src/main/java/com/idreyk/enfermeria360/ClinicalCatalog.kt').read_text()
        for item in self.items:
            if item['ruta']:
                self.assertIn('"' + item['id'] + '" to "' + item['ruta'] + '"', kotlin)
        notice = (ROOT / 'research/escalas/ATRIBUCION_4AT.md').read_text()
        self.assertIn('CC BY 4.0', notice)
        self.assertIn('Sander', notice)
        self.assertIn('NO es una publicación oficial', notice)

    def test_no_automatic_patient_upload_or_new_storage_schema(self):
        screen = (ROOT / 'app/src/main/java/com/idreyk/enfermeria360/ScreensClinicalCatalog.kt').read_text()
        self.assertIn('Dispatchers.IO', screen)
        for marker in ('HttpURLConnection', 'https://dmxfdvvcvfecqhbbmrlu', 'CREATE TABLE', 'DROP TABLE'):
            self.assertNotIn(marker, screen)
        repo = (ROOT / 'app/src/main/java/com/idreyk/enfermeria360/Repository.kt').read_text()
        self.assertIn('SQLiteOpenHelper(context, "idreyk_local.db", null, 2)', repo)


if __name__ == '__main__':
    unittest.main()
