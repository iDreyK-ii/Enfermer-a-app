#!/usr/bin/env python3
"""Prepare a connected debug build; default only checks PUBLIC configuration."""
import argparse
import os
from pathlib import Path
import subprocess
import sys
import json

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "backend"))
from comprobar_supabase import PublicConfig


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--compilar", action="store_true", help="Ejecutar Gradle con la configuración pública comprobada.")
    parser.add_argument("--config", type=Path, default=ROOT / "backend/supabase/deployment.public.json")
    args = parser.parse_args(argv)
    try:
        config = PublicConfig.from_dict(json.loads(args.config.read_text(encoding="utf-8")))
    except (OSError, ValueError, TypeError, AttributeError):
        parser.error("Revisa el archivo de configuración pública. No usar claves administrativas.")
    print("Proyecto:", config.project_ref)
    print("Se usará únicamente URL HTTPS y clave publicable; la firma depende de esta máquina.")
    if not args.compilar:
        print("Configuración válida. No se compiló ni se contactó el servidor.")
        print("Con JDK/SDK preparados, usa --compilar. Ejecuta antes la comprobación local aislada.")
        return 0
    command = [os.environ.get("GRADLE", str(ROOT / "gradlew")), "--no-daemon", "--max-workers=1",
               "-PIDREYK_SUPABASE_URL=" + config.url,
               "-PIDREYK_SUPABASE_PUBLISHABLE_KEY=" + config.publishable_key,
               ":app:assembleDebug", ":app:lintDebug",
               "-Dorg.gradle.jvmargs=-Xmx640m -XX:MaxMetaspaceSize=384m -XX:+UseSerialGC -Dfile.encoding=UTF-8"]
    return subprocess.run(command, cwd=ROOT, check=False).returncode


if __name__ == "__main__":
    sys.exit(main())
