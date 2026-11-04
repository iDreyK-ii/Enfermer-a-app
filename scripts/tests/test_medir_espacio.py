"""La medición es solo orientativa y nunca modifica los archivos."""
import importlib.util
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest

SCRIPT = Path(__file__).resolve().parents[1] / 'medir_espacio.py'
spec = importlib.util.spec_from_file_location('medir_espacio', SCRIPT)
space = importlib.util.module_from_spec(spec)
spec.loader.exec_module(space)


class MedirEspacioTest(unittest.TestCase):
    def test_counts_persistent_files_but_not_known_caches_or_git_config(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            for name, value in [('keep.txt', b'12345'), ('child/document.md', b'abcd'),
                                ('.cache/pic.png', b'ignored'), ('app/build/apk', b'ignored'),
                                ('.git/config', b'ignored'), ('.git/objects/blob', b'git')]:
                path = root / name
                path.parent.mkdir(parents=True, exist_ok=True)
                path.write_bytes(value)
            result = space.estimate(root)
            self.assertEqual({'bytes_logicos': 12, 'archivos': 3, 'errores_lectura': 0}, result)
            self.assertTrue((root / '.cache/pic.png').exists())
            self.assertTrue((root / '.git/config').exists())

    def test_directory_symlink_is_not_followed(self):
        with tempfile.TemporaryDirectory() as tmp, tempfile.TemporaryDirectory() as outside:
            root = Path(tmp)
            (Path(outside) / 'external').write_bytes(b'outside')
            (root / 'link').symlink_to(outside, target_is_directory=True)
            self.assertEqual(0, space.estimate(root)['archivos'])

    def test_projected_reservation_can_trigger_an_early_alert(self):
        with tempfile.TemporaryDirectory() as tmp:
            output = subprocess.check_output([sys.executable, str(SCRIPT), '--root', tmp,
                '--limite-mib', '10', '--reserva-mib', '9', '--json'], text=True)
            info = json.loads(output)
            self.assertEqual(0, info['bytes_logicos'])
            self.assertEqual(9, info['mib_proyectados'])
            self.assertTrue(info['alerta'])

    def test_root_must_be_directory(self):
        with tempfile.TemporaryDirectory() as tmp:
            file = Path(tmp) / 'file'
            file.write_text('x')
            with self.assertRaises(ValueError):
                space.estimate(file)


if __name__ == '__main__':
    unittest.main()
