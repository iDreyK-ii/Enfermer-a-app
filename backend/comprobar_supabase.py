#!/usr/bin/env python3
"""Read-only hosted Supabase checks. Explicit --remoto; no signup or publication."""
import argparse
import datetime
import hashlib
import json
import re
import sys
import urllib.error
import urllib.request
from dataclasses import dataclass, field
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MAX_RESPONSE = 65536


@dataclass(frozen=True)
class PublicConfig:
    project_ref: str
    url: str
    publishable_key: str = field(repr=False)

    @classmethod
    def from_dict(cls, value):
        ref, url, key = (value.get(name, "") for name in
                         ("project_ref", "url", "publishable_key"))
        if not all(isinstance(x, str) for x in (ref, url, key)):
            raise ValueError("La configuración pública debe contener cadenas.")
        if not re.fullmatch(r"[a-z0-9]{20}", ref) or url != f"https://{ref}.supabase.co":
            raise ValueError("Usa el endpoint HTTPS exacto de tu proyecto autorizado.")
        if not re.fullmatch(r"sb_publishable_[A-Za-z0-9_-]{8,500}", key):
            raise ValueError("Solo se permite una clave publicable; nunca una clave administrativa.")
        return cls(ref, url, key)


@dataclass(frozen=True)
class Probe:
    name: str
    path: str
    with_key: bool = True
    profile: str = ""


PROBES = (
    Probe("auth_health", "/auth/v1/health"),
    Probe("email_provider_enabled", "/auth/v1/settings"),
    Probe("catalog_read", "/rest/v1/catalog_releases?select=version,min_app_version,sha256&published=eq.true&order=version.desc&limit=1"),
    Probe("profiles_denied", "/rest/v1/profiles?select=user_id&limit=1"),
    Probe("drafts_denied", "/rest/v1/catalog_drafts?select=id&limit=1"),
    Probe("private_schema_not_exposed", "/rest/v1/editors?select=user_id&limit=1", profile="idreyk_private"),
    Probe("missing_key_denied", "/rest/v1/catalog_releases?select=version&limit=1", with_key=False),
)


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, *args, **kwargs):
        return None


def request(config, probe):
    headers = {"Accept": "application/json", "User-Agent": "idreyk-readonly-check/1"}
    if probe.with_key:
        headers["apikey"] = config.publishable_key
    if probe.profile:
        headers["Accept-Profile"] = probe.profile
    req = urllib.request.Request(config.url + probe.path, headers=headers, method="GET")
    opener = urllib.request.build_opener(NoRedirect())
    try:
        response = opener.open(req, timeout=12)
    except urllib.error.HTTPError as error:
        response = error
    with response:
        status, body = response.status, response.read(MAX_RESPONSE + 1)
    if len(body) > MAX_RESPONSE:
        raise ValueError("Respuesta fuera del límite de la comprobación.")
    return status, json.loads(body.decode("utf-8"))


def evaluate(name, status, data):
    error_code = data.get("code") if isinstance(data, dict) else None
    if name == "auth_health":
        return status == 200 and isinstance(data, dict) and data.get("name") == "GoTrue"
    if name == "email_provider_enabled":
        return (status == 200 and isinstance(data, dict)
                and isinstance(data.get("external"), dict)
                and data["external"].get("email") is True)
    if name == "catalog_read":
        return status == 200 and isinstance(data, list) and len(data) <= 1
    if name in ("profiles_denied", "drafts_denied"):
        return status in (401, 403) and error_code == "42501"
    if name == "private_schema_not_exposed":
        return status == 406 and error_code == "PGRST106"
    if name == "missing_key_denied":
        return status == 401
    return False


def run_checks(config, transport=request):
    results = []
    for probe in PROBES:
        try:
            status, data = transport(config, probe)
            result = {"check": probe.name, "ok": evaluate(probe.name, status, data),
                      "http_status": status}
            if isinstance(data, dict) and isinstance(data.get("code"), str):
                result["error_code"] = data["code"][:40]
            if probe.name == "catalog_read" and isinstance(data, list):
                result["row_count"] = len(data)
        except Exception as error:
            # Deliberately omit exception text, request headers and response bodies.
            result = {"check": probe.name, "ok": False,
                      "error_type": type(error).__name__}
        results.append(result)
    return {
        "project_ref": config.project_ref,
        "checked_at_utc": datetime.datetime.now(datetime.timezone.utc).isoformat(),
        "public_key_sha256": hashlib.sha256(config.publishable_key.encode()).hexdigest(),
        "checks": results,
        "passed": sum(item["ok"] for item in results),
        "total": len(results),
        "method": "GET only; public key; no redirect; bounded responses",
        "email_delivery_tested": False,
        "authenticated_session_tested": False,
        "write_or_publication_performed": False,
        "physical_android_tested": False,
    }


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--config", type=Path, default=ROOT / "backend/supabase/deployment.public.json")
    parser.add_argument("--remoto", action="store_true", help="Autoriza solo las consultas GET descritas.")
    parser.add_argument("--salida", type=Path, default=ROOT / ".cache/supabase-live/comprobacion.json")
    args = parser.parse_args(argv)
    try:
        config = PublicConfig.from_dict(json.loads(args.config.read_text(encoding="utf-8")))
    except (OSError, ValueError, TypeError, AttributeError):
        parser.error("Configuración no válida. Revisa el archivo público, sin añadir secretos.")
    if not args.remoto:
        print("Configuración pública válida. No se realizó ninguna solicitud de red.")
        print("Usa --remoto para las comprobaciones GET; no envía códigos ni crea cuentas.")
        return 0
    report = run_checks(config)
    args.salida.parent.mkdir(parents=True, exist_ok=True)
    args.salida.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    for item in report["checks"]:
        print(("OK" if item["ok"] else "FALLO") + " " + item["check"])
    print(f"{report['passed']}/{report['total']} comprobaciones HTTPS. No equivale a probar correo, sesión o Android físico.")
    return 0 if report["passed"] == report["total"] else 1


if __name__ == "__main__":
    sys.exit(main())
