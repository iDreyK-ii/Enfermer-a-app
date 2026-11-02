#!/usr/bin/env bash
# Reproducible local/CI checks. No deployment, account creation or publishing.
set -euo pipefail
cd "$(dirname "$0")/.."
mode="${1:---rapido}"
if [[ "$mode" != --rapido && "$mode" != --todo ]]; then
  echo 'Uso: scripts/comprobar.sh [--rapido|--todo]' >&2
  exit 2
fi
python="${PYTHON:-python3}"
gradle="${GRADLE:-./gradlew}"
logs="${QA_LOG_DIR:-.cache/qa-actual}"
mkdir -p "$logs"
run_check() {
  local name="$1"; shift
  "$@" 2>&1 | tee "$logs/$name.txt"
}
run_check repositorio "$python" scripts/verificar_repositorio.py
run_check guardas "$python" -m unittest discover -s scripts/tests -p 'test_*.py' -v
run_check conversor "$python" -m unittest discover -s content-tools -p test_converter.py -v
run_check publicacion "$python" -m unittest discover -s backend/tests -p 'test_*.py' -v
run_check sqlite "$python" database/verificar_esquema.py
run_check postgresql npm test --prefix backend
if [[ "$mode" == --todo ]]; then
  # Explicitly blank PUBLIC settings keep unit tests isolated from all real accounts/services.
  common=(--no-daemon --max-workers=1 -PIDREYK_SUPABASE_URL= -PIDREYK_SUPABASE_PUBLISHABLE_KEY=)
  memory='-Dorg.gradle.jvmargs=-Xmx640m -XX:MaxMetaspaceSize=384m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
  test_memory='-Dorg.gradle.jvmargs=-Xmx384m -XX:MaxMetaspaceSize=256m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
  run_check compilacion "$gradle" "${common[@]}" :app:compileDebugUnitTestKotlin "$memory"
  run_check android "$gradle" "${common[@]}" :app:testDebugUnitTest "$test_memory"
  run_check ensamblado-lint "$gradle" "${common[@]}" :app:assembleDebug :app:lintDebug "$memory"
  run_check apk "$python" scripts/verificar_apk.py
fi
printf '\nComprobaciones locales completadas. Este script no despliega servicios ni realiza pruebas físicas.\n'
