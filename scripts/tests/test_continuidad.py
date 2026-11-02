"""Evita que el contrato de traslado describa otra app, esquema o identidad."""
import json
from pathlib import Path
import re
import unittest
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[2]


class ContinuidadTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.contract = json.loads((ROOT / 'CONTINUIDAD.json').read_text())
        cls.gradle = (ROOT / 'app/build.gradle.kts').read_text()
        cls.android = ET.parse(ROOT / 'app/src/main/AndroidManifest.xml').getroot()
        cls.ns = '{http://schemas.android.com/apk/res/android}'

    def test_native_module(self):
        modules = re.findall(r'include\("([^"]+)"\)', (ROOT / 'settings.gradle.kts').read_text())
        self.assertEqual(modules, self.contract['android']['modulos'])
        self.assertIn('id("org.jetbrains.kotlin.plugin.compose")', self.gradle)
        self.assertEqual(modules, [':app'])

    def test_single_activity_and_permissions(self):
        activities = [a.attrib[self.ns + 'name'] for a in self.android.findall('application/activity')]
        self.assertEqual(activities, [self.contract['android']['actividad']])
        permissions = [p.attrib[self.ns + 'name'] for p in self.android.findall('uses-permission')]
        self.assertEqual(permissions, [self.contract['android']['permiso_directo']])

    def test_package_and_debug_identity(self):
        base = re.search(r'applicationId = "([^"]+)"', self.gradle).group(1)
        namespace = re.search(r'namespace = "([^"]+)"', self.gradle).group(1)
        suffix = re.search(r'applicationIdSuffix = "([^"]+)"', self.gradle).group(1)
        self.assertEqual(base, self.contract['android']['application_id'])
        self.assertEqual(namespace, self.contract['android']['namespace'])
        self.assertEqual(base + suffix, self.contract['android']['debug_application_id'])
        self.assertEqual(base + suffix, 'com.idreyk.enfermeria360.dev')

    def test_version_and_sdk_match_code(self):
        for field, gradle_name in [('version_code', 'versionCode'), ('min_sdk', 'minSdk'),
                                   ('target_sdk', 'targetSdk'), ('compile_sdk', 'compileSdk')]:
            actual = int(re.search(gradle_name + r' = (\d+)', self.gradle).group(1))
            self.assertEqual(actual, self.contract['android'][field])
        version = re.search(r'versionName = "([^"]+)"', self.gradle).group(1)
        self.assertEqual(version, self.contract['android']['version_name'])

    def test_sqlite_identity_and_tables(self):
        kotlin = (ROOT / 'app/src/main/java/com/idreyk/enfermeria360/Repository.kt').read_text()
        db = re.search(r'SQLiteOpenHelper\(context, "([^"]+)", null, (\d+)\)', kotlin)
        self.assertIsNotNone(db)
        self.assertEqual(db.group(1), self.contract['persistencia']['archivo'])
        self.assertEqual(int(db.group(2)), self.contract['persistencia']['version'])
        tables = set(re.findall(r'CREATE TABLE (\w+)\(', kotlin))
        self.assertEqual(tables, set(self.contract['persistencia']['tablas']))

    def test_selected_design_is_present(self):
        for field in ('inicio', 'ficha', 'ficha_legible', 'guia'):
            self.assertTrue((ROOT / self.contract['diseno'][field]).is_file(), field)

    def test_public_cloud_target_and_migration(self):
        deployment = json.loads((ROOT / self.contract['servidor']['parametros_publicables']).read_text())
        reference = self.contract['servidor']['referencia']
        self.assertIn(reference, json.dumps(deployment))
        self.assertTrue((ROOT / 'supabase/migrations' / (self.contract['servidor']['migracion_aplicada'] + '.sql')).is_file())
        self.assertTrue(self.contract['servidor']['no_reaplicar_migracion_inicial'])
        self.assertFalse(self.contract['servidor']['sustitucion_por_firebase_autorizada'])

    def test_fingerprint_is_public_and_verifier_accepts_expected_certificate(self):
        fingerprint = self.contract['firma']['certificado_debug_conservado_sha256']
        self.assertRegex(fingerprint, r'^[0-9a-f]{64}$')
        self.assertFalse(self.contract['firma']['clave_privada_en_repositorio'])
        self.assertIn("'--certificado'", (ROOT / 'scripts/verificar_apk.py').read_text())

    def test_quality_remains_isolated_from_real_accounts(self):
        quality = (ROOT / 'scripts/comprobar.sh').read_text()
        self.assertIn('-PIDREYK_SUPABASE_URL=', quality)
        self.assertIn('-PIDREYK_SUPABASE_PUBLISHABLE_KEY=', quality)
        self.assertIn(':app:testDebugUnitTest', quality)
        self.assertIn(':app:lintDebug', quality)


if __name__ == '__main__':
    unittest.main()
