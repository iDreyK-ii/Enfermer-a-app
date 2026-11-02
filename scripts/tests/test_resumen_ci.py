import importlib.util
import json
from pathlib import Path
import tempfile
import unittest

SPEC = importlib.util.spec_from_file_location('qa_summary', Path(__file__).resolve().parents[1] / 'resumir_qa_ci.py')
qa = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(qa)


class QaSummaryTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.tests = self.root / 'tests'
        self.tests.mkdir()
        self.xml = self.tests / 'TEST-example.xml'
        self.xml.write_text('<testsuite tests="2" failures="0" errors="0" skipped="1"><testcase name="a"/><testcase name="b"><skipped/></testcase></testsuite>')
        self.lint = self.root / 'lint.xml'
        self.lint.write_text('<issues><issue severity="Warning"/></issues>')
        self.apk = self.root / 'apk.json'
        self.apk.write_text(json.dumps(dict(version='0.5.0-dev-debug', version_code=6,
            sha256='a'*64, min_sdk=26, target_sdk=35, certificate_matches_expected=False,
            physical_installation_tested=False, arbitrary_field='must not be copied')))

    def summary(self):
        return qa.summarize(self.tests, self.lint, self.apk)

    def test_counts_cases_without_hiding_skips_or_copying_arbitrary_fields(self):
        (self.tests / 'TEST-second.xml').write_text('<testsuite tests="1" failures="0" errors="0" skipped="0"><testcase/></testsuite>')
        result = self.summary()
        self.assertEqual(dict(tests=3, failures=0, errors=0, skipped=1, passed=2, suites=2), result['android'])
        self.assertEqual(dict(errors=0, warnings=1), result['lint'])
        self.assertNotIn('arbitrary_field', result['apk'])
        self.assertFalse(result['clinical_validation'])
        self.assertFalse(result['google_import_verified'])
        self.assertIn('::notice title=Resumen técnico de CI::', qa.notice(result))

    def test_missing_or_malformed_results_do_not_become_zero_successes(self):
        self.xml.unlink()
        with self.assertRaises(ValueError): self.summary()
        self.xml.write_text('<bad')
        with self.assertRaises(qa.ET.ParseError): self.summary()

    def test_disagreement_between_suite_totals_and_cases_is_rejected(self):
        self.xml.write_text('<testsuite tests="3" failures="0" errors="0" skipped="0"><testcase/></testsuite>')
        with self.assertRaises(ValueError): self.summary()

    def test_failures_errors_and_lint_errors_are_never_summarized_as_success(self):
        original = self.xml.read_text()
        for problem in ['failure', 'error']:
            self.xml.write_text(f'<testsuite tests="1" failures="{int(problem == "failure")}" errors="{int(problem == "error")}" skipped="0"><testcase><{problem}/></testcase></testsuite>')
            with self.assertRaises(ValueError): self.summary()
        self.xml.write_text(original)
        self.lint.write_text('<issues><issue severity="Error"/></issues>')
        with self.assertRaises(ValueError): self.summary()

    def test_invalid_apk_identity_is_rejected_and_notices_escape_commands(self):
        self.apk.write_text('{"sha256":"not-a-hash"}')
        with self.assertRaises(ValueError): self.summary()
        emitted = qa.notice({'text': '%\r\n::error::example'})
        self.assertNotIn('\n', emitted)
        self.assertNotIn('\r', emitted)
        self.assertIn('%25', emitted)


if __name__ == '__main__':
    unittest.main()
