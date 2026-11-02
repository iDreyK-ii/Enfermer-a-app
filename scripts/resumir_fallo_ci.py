#!/usr/bin/env python3
"""Diagnóstico acotado de fallos de CI como anotación pública, sin ampliar permisos.
No recupera logs privados ni necesita tokens. Solo procesa la salida de esta ejecución.
"""
import argparse
from pathlib import Path
import re
import xml.etree.ElementTree as ET


def redact(text: str) -> str:
    text = re.sub(r'-----BEGIN (?:RSA |EC |OPENSSH |ENCRYPTED )?PRIVATE KEY-----.*?-----END [^-]*PRIVATE KEY-----', '[CLAVE PRIVADA OMITIDA]', text, flags=re.S)
    text = re.sub(r'(?:sb_secret_[A-Za-z0-9_-]{8,}|gh[pousr]_[A-Za-z0-9_]{20,}|github_pat_[A-Za-z0-9_]{20,})', '[CREDENCIAL OMITIDA]', text)
    text = re.sub(r'(https?://)[^/\s@]+:[^/\s@]+@', r'\1[CREDENCIAL OMITIDA]@', text)
    text = re.sub(r'(?i)(authorization:\s*bearer\s+)\S+', r'\1[OMITIDO]', text)
    return text


def annotation(title: str, body: str) -> str:
    title = re.sub(r'[^A-Za-z0-9 _.-]', '_', title)[:100]
    body = redact(body)[-12000:]
    escaped = body.replace('%', '%25').replace('\r', '%0D').replace('\n', '%0A')
    return f'::error title={title}::{escaped}'


def failed_tests(directory: Path) -> list[str]:
    results = []
    for path in sorted(directory.glob('TEST-*.xml')):
        if path.stat().st_size > 4_000_000:
            continue
        try:
            suite = ET.parse(path).getroot()
        except ET.ParseError:
            continue
        for case in suite.iter('testcase'):
            failures = list(case.findall('failure')) + list(case.findall('error'))
            for failure in failures:
                results.append(f"{case.get('classname', '')}.{case.get('name', '')}\n{failure.get('message', '')}\n{failure.text or ''}")
    return results[:10]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--etapa', required=True)
    parser.add_argument('--log', type=Path, required=True)
    args = parser.parse_args()
    text = args.log.read_text(encoding='utf-8', errors='replace') if args.log.is_file() else 'No se pudo leer el log de esta etapa.'
    print(annotation('QA ' + args.etapa, '\n'.join(text.splitlines()[-65:])))
    if args.etapa == 'android':
        root = Path(__file__).resolve().parents[1]
        for i, failure in enumerate(failed_tests(root / 'app/build/test-results/testDebugUnitTest'), 1):
            print(annotation(f'Fallo nativo {i}', failure))


if __name__ == '__main__':
    main()
