import importlib.util
from pathlib import Path
import unittest

path=Path(__file__).resolve().parents[1]/'verificar_repositorio.py'
spec=importlib.util.spec_from_file_location('guard',path)
guard=importlib.util.module_from_spec(spec);spec.loader.exec_module(guard)

class RepositoryGuards(unittest.TestCase):
    def test_nested_private_configuration_is_rejected(self):
        for path in ['.env','backend/.env','backend/.env.production','local.properties','firma/debug.keystore']:
            self.assertTrue(guard.forbidden_path(path),path)
    def test_generated_files_do_not_belong_in_source(self):
        for path in ['app/build/outputs/apk/file.apk','backend/node_modules/a.js','private.db','release.aab','keys/archivo.txt']:
            self.assertTrue(guard.forbidden_path(path),path)
    def test_examples_source_and_templates_remain_allowed(self):
        for path in ['.env.example','backend/.env.example','app/src/main/AndroidManifest.xml','content-tools/Plantilla_farmacos_v3.xlsx','.github/workflows/calidad.yml']:
            self.assertFalse(guard.forbidden_path(path),path)
    def test_obvious_private_credentials_are_detected_without_showing_them(self):
        fake=('sb_'+'secret_'+'a'*40).encode()
        fake_pat=('github_'+'pat_'+'b'*60).encode()
        self.assertTrue(guard.suspicious_text(fake))
        self.assertTrue(guard.suspicious_text(fake_pat))
        self.assertTrue(guard.suspicious_text(('-----BEGIN '+'PRIVATE KEY-----').encode()))
    def test_public_configuration_and_short_named_fixtures_are_not_secrets(self):
        self.assertFalse(guard.suspicious_text(b'IDREYK_SUPABASE_PUBLISHABLE_KEY=""'))
        self.assertFalse(guard.suspicious_text(b'sb_secret_never_ship_this'))

if __name__=='__main__':unittest.main()
