#!/usr/bin/env python3
"""Validate the offline Escalas 360 catalog before Android packaging."""
from __future__ import annotations
import json
import sys
from pathlib import Path
from urllib.parse import urlparse

EXPECTED_INSTRUMENTS = 191
EXPECTED_SOURCES = 86
VALID_POPULATIONS = {"adultos", "pediatria", "neonatos"}
VALID_STATES = {"calculo", "registro", "simulacion", "ficha", "permiso"}
ALLOWED_ROUTES = {
    "glasgow": "glasgow",
    "glasgow-pediatrico": "glasgow-pediatrico",
    "eva": "eva",
    "numerica": "dolor-numerico",
    "4at": "test-4at",
    "braden": "registro-escala/braden",
    "morse": "registro-escala/morse",
    "norton": "registro-escala/norton",
    "rass": "registro-escala/rass",
    "apgar": "escala/apgar",
    "silverman": "escala/silverman",
    "capurro-a": "capurro/A",
    "capurro-b": "capurro/B",
}
REQUIRED_IDS = {
    "tiss-76", "tiss-28", "nas", "nems", "sofa", "qsofa",
    "apgar", "capurro-a", "capurro-b", "glasgow", "eva", "rass", "norton",
}
SERVICE_POLICIES = {
    "tiss-76": {"UCI adulto"},
    "tiss-28": {"UCI adulto"},
    "nas": {"UCI adulto"},
    "nems": {"UCI adulto"},
}
NEWBORN_ONLY = {
    "nips", "pipp-r", "capurro-a", "capurro-b", "ballard", "downes",
    "crib-ii", "snap-ii", "snappe-ii", "ntiss", "comfortneo",
    "n-pass", "finnegan", "eat-sleep-console",
}

def fail(message: str) -> None:
    raise SystemExit("ERROR: " + message)

def main() -> None:
    path = Path(sys.argv[1] if len(sys.argv) > 1 else "app/src/main/assets/escalas_clinicas_v1.json")
    raw = path.read_bytes()
    if len(raw) > 500_000:
        fail(f"catalog bytes {len(raw)} exceed 500000")
    try:
        data = json.loads(raw)
    except Exception as exc:
        fail(f"invalid JSON: {exc}")

    if data.get("formato") != 1:
        fail("unknown format")
    instruments = data.get("instrumentos")
    sources = data.get("fuentes")
    if not isinstance(instruments, list) or len(instruments) != EXPECTED_INSTRUMENTS:
        fail(f"expected {EXPECTED_INSTRUMENTS} instruments")
    if not isinstance(sources, list) or len(sources) != EXPECTED_SOURCES:
        fail(f"expected {EXPECTED_SOURCES} sources")

    instrument_ids = [item.get("id") for item in instruments]
    source_ids = [item.get("id") for item in sources]
    if len(set(instrument_ids)) != len(instrument_ids):
        fail("duplicate instrument IDs")
    if len(set(source_ids)) != len(source_ids):
        fail("duplicate source IDs")
    source_set = set(source_ids)
    missing_required = REQUIRED_IDS - set(instrument_ids)
    if missing_required:
        fail("missing expected IDs: " + ", ".join(sorted(missing_required)))

    for source in sources:
        parsed = urlparse(source.get("url", ""))
        if parsed.scheme != "https" or not parsed.hostname or parsed.username or parsed.password:
            fail(f"unsafe source URL: {source.get('id')}")
        if not source.get("consulta") or len(source.get("lectura", "")) > 600:
            fail(f"invalid source metadata: {source.get('id')}")

    for item in instruments:
        instrument_id = item.get("id", "<missing>")
        if item.get("revision_clinica") != "pendiente":
            fail(f"{instrument_id} must remain clinically pending")
        if item.get("estado") not in VALID_STATES:
            fail(f"invalid availability for {instrument_id}")
        populations = item.get("poblaciones", [])
        if not populations or not set(populations).issubset(VALID_POPULATIONS):
            fail(f"invalid population for {instrument_id}")
        services = item.get("servicios", [])
        if not services or len(services) != len(set(services)):
            fail(f"missing/duplicate services for {instrument_id}")
        if not item.get("tipo_instrumento") or not item.get("rol_enfermeria") or not item.get("contexto_peru"):
            fail(f"missing editorial metadata for {instrument_id}")
        refs = item.get("fuentes", [])
        if not refs or len(refs) > 10 or not set(refs).issubset(source_set):
            fail(f"invalid source links for {instrument_id}")
        route = item.get("ruta", "")
        state = item.get("estado")
        actionable = state in {"calculo", "registro", "simulacion"}
        if actionable != bool(route):
            fail(f"state/route mismatch for {instrument_id}")
        if route and ALLOWED_ROUTES.get(instrument_id) != route:
            fail(f"unapproved route for {instrument_id}")
        if instrument_id in SERVICE_POLICIES and set(services) != SERVICE_POLICIES[instrument_id]:
            fail(f"invalid service mapping for workload tool {instrument_id}")
        if instrument_id == "news2" and any(x in services for x in ("Pediatría", "UCI pediátrica", "Obstetricia y sala de partos", "UCI adulto")):
            fail("NEWS2 must not be mapped to pediatric, obstetric or ICU services")
        if instrument_id == "pews" and any(x in services for x in ("UCI adulto", "Obstetricia y sala de partos")):
            fail("PEWS must not be mapped to adult ICU or obstetrics")
        if instrument_id == "rass" and populations != ["adultos"]:
            fail("RASS entry must identify its adult version")
        if instrument_id in NEWBORN_ONLY and set(services) != {"Neonatología y UCIN"}:
            fail(f"invalid neonatal service mapping for {instrument_id}")

    actionable_count = sum(bool(item.get("ruta")) for item in instruments)
    service_count = len({service for item in instruments for service in item["servicios"]})
    print("OK · Escalas 360 catalog")
    print(f"Instruments: {len(instruments)}")
    print(f"Sources: {len(sources)}")
    print(f"Services: {service_count}")
    print(f"Existing actionable routes: {actionable_count}")
    print(f"Bytes: {len(raw)}")

if __name__ == "__main__":
    main()
