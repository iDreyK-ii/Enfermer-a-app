import hashlib
import importlib.util
import json
import os
from pathlib import Path
import stat
import subprocess
import tempfile
import unittest
from unittest.mock import patch
import warnings
import zipfile

SPEC = importlib.util.spec_from_file_location('traslado', Path(__file__).resolve().parents[1] / 'verificar_traslado.py')
t = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(t)


class TrasladoTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        self.base = Path(self.tmp.name)
        self.repo = self.base / 'canonical'
        self.copy = self.base / 'copy'
        self.repo.mkdir()
        self.copy.mkdir()
        self.files = {'README.md': b'Proyecto de prueba\n', 'AGENTS.md': b'No recrear\n',
                      'app/build.gradle.kts': b'android {}\n', 'gradlew': b'#!/bin/sh\ntrue\n',
                      'app/src/main/res/drawable/imagen.png': b'\x89PNG\r\n\x00binario'}
        for root in (self.repo, self.copy):
            for name, data in self.files.items():
                p = root / name
                p.parent.mkdir(parents=True, exist_ok=True)
                p.write_bytes(data)
                p.chmod(0o755 if name == 'gradlew' else 0o644)
        subprocess.run(['git', 'init', '-q', '--initial-branch=desarrollo', str(self.repo)], check=True)
        subprocess.run(['git', '-C', str(self.repo), 'add', '.'], check=True)
        subprocess.run(['git', '-C', str(self.repo), '-c', 'user.name=Prueba', '-c', 'user.email=prueba@example.invalid',
                        '-c', 'commit.gpgsign=false', 'commit', '-qm', 'Fixture sin datos reales'], check=True)
        self.manifest = t.reference(self.repo, 'HEAD')

    def make_zip(self, prefixes=('',), omit=()):
        path = self.base / 'export.zip'
        with zipfile.ZipFile(path, 'w', zipfile.ZIP_DEFLATED) as archive:
            for prefix in prefixes:
                for name in self.files:
                    if name not in omit:
                        archive.write(self.copy / name, prefix + name)
        return path

    def test_manifest_uses_committed_objects_not_dirty_working_tree(self):
        (self.repo / 'README.md').write_bytes(b'cambio sin commit')
        result = t.reference(self.repo, 'HEAD')
        self.assertEqual(result['files']['README.md']['sha256'], hashlib.sha256(self.files['README.md']).hexdigest())
        self.assertEqual(result['tracked_files'], 5)
        self.assertEqual(result['files']['gradlew']['git_mode'], '100755')

    def test_invalid_revision_does_not_select_unintended_commit(self):
        with self.assertRaises(ValueError):
            t.reference(self.repo, '--all')

    def test_parent_git_is_not_accepted(self):
        with self.assertRaises(ValueError):
            t.reference(self.repo / 'app', 'HEAD')

    def test_identical_directory(self):
        result = t.verify_directory(self.manifest, self.copy)
        self.assertTrue(result['ok'])
        self.assertEqual(result['identical_files'], 5)

    def test_missing_file(self):
        (self.copy / 'README.md').unlink()
        result = t.verify_directory(self.manifest, self.copy)
        self.assertFalse(result['ok'])
        self.assertIn('README.md', result['missing'])
        self.assertFalse(result['executable_modes_verified'])

    def test_same_size_changed_content_is_detected(self):
        (self.copy / 'README.md').write_bytes(b'X' * len(self.files['README.md']))
        result = t.verify_directory(self.manifest, self.copy)
        self.assertFalse(result['ok'])
        self.assertEqual(result['changed'][0]['reason'], 'sha256')

    def test_different_size_is_detected(self):
        (self.copy / 'README.md').write_bytes(b'otro')
        result = t.verify_directory(self.manifest, self.copy)
        self.assertFalse(result['ok'])
        self.assertEqual(result['changed'][0]['reason'], 'size')

    def test_new_source_is_not_silently_approved(self):
        (self.copy / 'otro.kt').write_text('cambio')
        result = t.verify_directory(self.manifest, self.copy)
        self.assertFalse(result['ok'])
        self.assertEqual(result['unexpected'], ['otro.kt'])

    def test_generated_files_are_explicitly_excluded(self):
        p = self.copy / 'app/build/salida.txt'
        p.parent.mkdir()
        p.write_text('generado')
        result = t.verify_directory(self.manifest, self.copy)
        self.assertTrue(result['ok'])
        self.assertIn('app/build/', result['ignored_generated'])

    def test_executable_bit_loss(self):
        (self.copy / 'gradlew').chmod(0o644)
        result = t.verify_directory(self.manifest, self.copy)
        self.assertFalse(result['ok'])
        self.assertTrue(result['source_bytes_identical'])
        self.assertEqual(result['mode_differences'], ['gradlew'])

    @unittest.skipIf(os.name == 'nt', 'Enlaces Unix')
    def test_file_symlink_is_not_followed(self):
        (self.copy / 'README.md').unlink()
        (self.copy / 'README.md').symlink_to(self.repo / 'README.md')
        result = t.verify_directory(self.manifest, self.copy)
        self.assertFalse(result['ok'])
        self.assertTrue(result['errors'])

    @unittest.skipIf(os.name == 'nt', 'Enlaces Unix')
    def test_directory_symlink_is_not_followed(self):
        (self.copy / 'alias').symlink_to(self.repo, target_is_directory=True)
        result = t.verify_directory(self.manifest, self.copy)
        self.assertFalse(result['ok'])
        self.assertTrue(result['errors'])

    def test_flat_zip(self):
        result = t.verify_zip(self.manifest, self.make_zip())
        self.assertTrue(result['ok'])
        self.assertEqual(result['zip_prefix'], '')

    def test_wrapped_zip(self):
        result = t.verify_zip(self.manifest, self.make_zip(('proyecto/',)))
        self.assertTrue(result['ok'])
        self.assertEqual(result['zip_prefix'], 'proyecto/')

    def test_missing_zip_file(self):
        result = t.verify_zip(self.manifest, self.make_zip(omit=('AGENTS.md',)))
        self.assertFalse(result['ok'])
        self.assertIn('AGENTS.md', result['missing'])

    def test_changed_zip_file(self):
        (self.copy / 'app/build.gradle.kts').write_text('android { cambio }')
        result = t.verify_zip(self.manifest, self.make_zip())
        self.assertFalse(result['ok'])
        self.assertEqual(result['changed'][0]['path'], 'app/build.gradle.kts')

    def test_unexpected_source_in_zip(self):
        path = self.make_zip()
        with zipfile.ZipFile(path, 'a') as archive:
            archive.writestr('nuevo.tsx', 'No reemplazar Android')
        result = t.verify_zip(self.manifest, path)
        self.assertFalse(result['ok'])
        self.assertIn('nuevo.tsx', result['unexpected'])

    def test_parent_traversal_zip_rejected_without_extracting(self):
        path = self.make_zip()
        with zipfile.ZipFile(path, 'a') as archive:
            archive.writestr('../fuera.txt', 'no extraer')
        with self.assertRaises(ValueError):
            t.verify_zip(self.manifest, path)
        self.assertFalse((self.base / 'fuera.txt').exists())

    def test_duplicate_entry_rejected(self):
        path = self.make_zip()
        with warnings.catch_warnings():
            warnings.simplefilter('ignore', UserWarning)
            with zipfile.ZipFile(path, 'a') as archive:
                archive.writestr('README.md', 'duplicado')
        with self.assertRaises(ValueError):
            t.verify_zip(self.manifest, path)

    def test_zip_symlink_rejected(self):
        path = self.make_zip()
        info = zipfile.ZipInfo('enlace')
        info.create_system = 3
        info.external_attr = (stat.S_IFLNK | 0o777) << 16
        with zipfile.ZipFile(path, 'a') as archive:
            archive.writestr(info, 'README.md')
        with self.assertRaises(ValueError):
            t.verify_zip(self.manifest, path)

    def test_ambiguous_roots_are_not_guessed(self):
        with self.assertRaises(ValueError):
            t.verify_zip(self.manifest, self.make_zip(('uno/', 'dos/')))

    def test_explicit_prefix_does_not_hide_other_source(self):
        result = t.verify_zip(self.manifest, self.make_zip(('uno/', 'dos/')), 'uno')
        self.assertFalse(result['ok'])
        self.assertTrue(result['unexpected'])

    def test_size_limit_prevents_zip_bomb_processing(self):
        path = self.make_zip()
        with patch.object(t, 'MAX_TOTAL_BYTES', 1):
            with self.assertRaises(ValueError):
                t.verify_zip(self.manifest, path)

    def test_unknown_permissions_are_not_claimed_verified(self):
        path = self.base / 'sin-modos.zip'
        with zipfile.ZipFile(path, 'w') as archive:
            for name, data in self.files.items():
                info = zipfile.ZipInfo(name)
                info.create_system = 0
                archive.writestr(info, data)
        result = t.verify_zip(self.manifest, path)
        self.assertTrue(result['source_bytes_identical'])
        self.assertFalse(result['ok'])
        self.assertFalse(result['executable_modes_verified'])

    def test_manifest_cannot_overwrite_tracked_source(self):
        with patch.object(t, 'ROOT', self.repo):
            with self.assertRaises(ValueError):
                t.save_report(self.repo / 'README.md', {'not': 'source'}, self.manifest)
        self.assertEqual((self.repo / 'README.md').read_bytes(), self.files['README.md'])

    def test_new_index_file_is_protected_even_against_old_reference(self):
        path = self.repo / 'nuevo.json'
        path.write_text('{}')
        subprocess.run(['git', '-C', str(self.repo), 'add', 'nuevo.json'], check=True)
        with patch.object(t, 'ROOT', self.repo):
            with self.assertRaises(ValueError):
                t.save_report(path, {'cambio': True}, self.manifest)
        self.assertEqual(path.read_text(), '{}')

    def test_inconsistent_directory_type_in_zip_rejected(self):
        path = self.make_zip()
        info = zipfile.ZipInfo('directorio-sin-barra')
        info.create_system = 3
        info.external_attr = (stat.S_IFDIR | 0o644) << 16
        with zipfile.ZipFile(path, 'a') as archive:
            archive.writestr(info, 'contenido')
        with self.assertRaises(ValueError):
            t.verify_zip(self.manifest, path)

    def test_safe_path_validation(self):
        for name in ('/root/x', 'C:/x', 'a/../x', 'a\\x', 'a//x', './x'):
            with self.subTest(name=name), self.assertRaises(ValueError):
                t.safe_name(name)


if __name__ == '__main__':
    unittest.main()
