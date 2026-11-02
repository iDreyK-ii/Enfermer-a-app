import importlib.util
from pathlib import Path
import tempfile
import unittest

SPEC = importlib.util.spec_from_file_location('ci_diagnostic', Path(__file__).resolve().parents[1] / 'resumir_fallo_ci.py')
module = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(module)


class CiDiagnosticTest(unittest.TestCase):
    def test_annotation_escapes_workflow_command_injection(self):
        result = module.annotation('QA\n,title=x', 'error\n::notice::not a command\r100%')
        self.assertNotIn('\n', result)
        self.assertNotIn('\r', result)
        self.assertIn('%0A', result)
        self.assertIn('%25', result)

    def test_body_is_bounded(self):
        result = module.annotation('QA', 'x' * 40000)
        self.assertLess(len(result), 12100)

    def test_tokens_are_redacted(self):
        token = 'sb_' + 'secret_' + 'a' * 30
        self.assertNotIn(token, module.redact('error ' + token))
        self.assertIn('OMITIDA', module.redact(token))

    def test_private_material_and_bearer_are_redacted(self):
        marker = '-----BEGIN ' + 'OPENSSH PRIVATE KEY-----\nsecret-example\n-----END OPENSSH PRIVATE KEY-----'
        result = module.redact(marker + '\nAuthorization: Bearer fake-example')
        self.assertNotIn('secret-example', result)
        self.assertNotIn('fake-example', result)

    def test_embedded_url_credentials_are_redacted(self):
        self.assertNotIn('person:example', module.redact('https://person:example@example.invalid/path'))

    def test_bash_wrapper_does_not_hide_the_original_failure(self):
        import subprocess
        script = (Path(__file__).resolve().parents[1] / 'comprobar.sh').read_text()
        function = script[script.index('run_check() {'):script.index('\nrun_check repositorio')]
        with tempfile.TemporaryDirectory() as d:
            command = 'set -euo pipefail\nlogs="' + d + '"\npython=true\nGITHUB_ACTIONS=true\n' + function + '\nrun_check prueba bash -c "exit 7"\necho NOT_REACHED\n'
            result = subprocess.run(['bash', '-c', command], text=True, capture_output=True)
            self.assertEqual(7, result.returncode)
            self.assertNotIn('NOT_REACHED', result.stdout)

    def test_reads_failures_and_errors_without_calling_network_or_ignoring_bad_xml(self):
        with tempfile.TemporaryDirectory() as d:
            root = Path(d)
            (root / 'TEST-ok.xml').write_text('<testsuite><testcase classname="C" name="a"/><testcase classname="C" name="b"><failure message="bad">detail</failure></testcase><testcase classname="C" name="c"><error message="oops">other</error></testcase></testsuite>')
            (root / 'TEST-bad.xml').write_text('<broken')
            results = module.failed_tests(root)
            self.assertEqual(2, len(results))
            self.assertTrue(results[0].startswith('C.b'))
            self.assertTrue(results[1].startswith('C.c'))


if __name__ == '__main__':
    unittest.main()
