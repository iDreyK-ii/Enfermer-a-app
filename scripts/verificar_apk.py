#!/usr/bin/env python3
"""Inspecciona un APK debug real, sin instalarlo ni leer la clave privada de firma."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import subprocess
import zipfile

ROOT = Path(__file__).resolve().parents[1]


def inspect(apk: Path, sdk: Path, expected_certificate: str = '') -> dict:
    tools = sdk / 'build-tools/35.0.0'
    signature = subprocess.check_output([str(tools/'apksigner'), 'verify', '--verbose', '--print-certs', str(apk)], text=True)
    metadata = subprocess.check_output([str(tools/'aapt'), 'dump', 'badging', str(apk)], text=True)
    permissions = subprocess.check_output([str(tools/'aapt'), 'dump', 'permissions', str(apk)], text=True)
    certificates = re.findall(r'certificate SHA-256 digest: ([a-f0-9]+)', signature)
    if len(certificates) != 1:
        raise ValueError('Se esperaba exactamente un firmante; revisar antes de actualizar.')
    cert = certificates[0]
    if expected_certificate and cert != expected_certificate.lower():
        raise ValueError('La firma no coincide: NO desinstales ni borres datos para forzar la instalación.')
    build = (ROOT / 'app/build.gradle.kts').read_text()
    version = re.search(r'versionName = "([^"]+)"', build).group(1) + '-debug'
    code = re.search(r'versionCode = (\d+)', build).group(1)
    if f"name='com.idreyk.enfermeria360.dev' versionCode='{code}' versionName='{version}'" not in metadata:
        raise ValueError('El paquete/versión no corresponde al código debug actual.')
    minimum = re.search(r'minSdk = (\d+)', build).group(1)
    target = re.search(r'targetSdk = (\d+)', build).group(1)
    if f"sdkVersion:'{minimum}'" not in metadata or f"targetSdkVersion:'{target}'" not in metadata:
        raise ValueError('El nivel de Android del APK no coincide con el proyecto.')
    declared = set(re.findall(r"uses-permission: name='([^']+)'", permissions))
    allowed = {'android.permission.INTERNET', 'com.idreyk.enfermeria360.dev.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION'}
    if 'android.permission.INTERNET' not in declared or not declared <= allowed:
        raise ValueError('El APK tiene permisos inesperados; revisar antes de entregar.')
    with zipfile.ZipFile(apk) as archive:
        if archive.testzip() is not None:
            raise ValueError('El APK no pasó la verificación de integridad ZIP.')
    return {'apk': apk.name, 'bytes': apk.stat().st_size, 'version': version, 'version_code': int(code), 'min_sdk': int(minimum), 'target_sdk': int(target),
            'sha256': hashlib.sha256(apk.read_bytes()).hexdigest(), 'certificate_sha256': cert,
            'certificate_matches_expected': bool(expected_certificate), 'permissions': sorted(declared),
            'physical_installation_tested': False}


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--apk', type=Path, default=ROOT/'app/build/outputs/apk/debug/app-debug.apk')
    parser.add_argument('--sdk', type=Path, default=Path(os.environ.get('ANDROID_HOME') or os.environ.get('ANDROID_SDK_ROOT') or '/sdk-no-configurado'))
    parser.add_argument('--certificado', default=os.environ.get('IDREYK_EXPECTED_CERT_SHA256', ''))
    args = parser.parse_args()
    print(json.dumps(inspect(args.apk, args.sdk, args.certificado), ensure_ascii=False, indent=2))
