#!/usr/bin/env python3
"""Resume evidencia ya producida en este run; no descarga ni publica archivos/secretos."""
import argparse
import json
import os
from pathlib import Path
import re
import xml.etree.ElementTree as ET


def summarize(results: Path, lint: Path, apk_log: Path) -> dict:
    files = sorted(results.glob('TEST-*.xml'))
    if not files:
        raise ValueError('Faltan XML nativos: no se puede afirmar que se ejecutaron tests.')
    total = dict(tests=0, failures=0, errors=0, skipped=0)
    for path in files:
        suite = ET.parse(path).getroot()
        if suite.tag != 'testsuite':
            raise ValueError('Formato de suite XML no reconocido.')
        cases = suite.findall('testcase')
        counts = {'tests': len(cases), 'failures': sum(c.find('failure') is not None for c in cases),
                  'errors': sum(c.find('error') is not None for c in cases),
                  'skipped': sum(c.find('skipped') is not None for c in cases)}
        for key, actual in counts.items():
            if int(suite.get(key, '-1')) != actual:
                raise ValueError('Recuento XML nativo incoherente: ' + key)
            total[key] += actual
    if total['tests'] == 0 or total['failures'] or total['errors']:
        raise ValueError('No se resume como correcta una suite vacía o con fallos.')
    total['passed'] = total['tests'] - total['skipped']
    total['suites'] = len(files)
    issues = ET.parse(lint).getroot()
    if issues.tag != 'issues':
        raise ValueError('Formato de lint no reconocido.')
    entries = issues.findall('issue')
    lint_counts = {'errors': sum(x.get('severity') in ('Error', 'Fatal') for x in entries),
                   'warnings': sum(x.get('severity') == 'Warning' for x in entries)}
    if lint_counts['errors']:
        raise ValueError('Lint aún tiene errores.')
    apk = json.loads(apk_log.read_text())
    if not re.fullmatch(r'[a-f0-9]{64}', apk.get('sha256', '')):
        raise ValueError('Falta hash verificable del APK inspeccionado.')
    if not re.fullmatch(r'[0-9A-Za-z.+-]{1,80}', apk.get('version', '')):
        raise ValueError('Versión APK inválida.')
    # Whitelist avoids copying paths, environment, test names, log bodies or arbitrary fields.
    apk_summary = {key: apk[key] for key in ('version', 'version_code', 'sha256', 'min_sdk', 'target_sdk',
                                            'certificate_matches_expected', 'physical_installation_tested')}
    return {'android': total, 'lint': lint_counts, 'apk': apk_summary,
            'clinical_validation': False, 'google_import_verified': False}


def notice(result: dict) -> str:
    body = json.dumps(result, ensure_ascii=False, separators=(',', ':'))
    return '::notice title=Resumen técnico de CI::' + body.replace('%', '%25').replace('\r', '%0D').replace('\n', '%0A')


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--tests', type=Path, default=Path('app/build/test-results/testDebugUnitTest'))
    parser.add_argument('--lint', type=Path, default=Path('app/build/reports/lint-results-debug.xml'))
    parser.add_argument('--apk-log', type=Path, required=True)
    parser.add_argument('--salida', type=Path, required=True)
    args = parser.parse_args()
    result = summarize(args.tests, args.lint, args.apk_log)
    args.salida.parent.mkdir(parents=True, exist_ok=True)
    args.salida.write_text(json.dumps(result, ensure_ascii=False, indent=2) + '\n')
    print(notice(result) if os.environ.get('GITHUB_ACTIONS') == 'true' else json.dumps(result, ensure_ascii=False))
