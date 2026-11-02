"""Offline tests; no real service, credentials, signup or email."""
import contextlib
import io
import json
from pathlib import Path
import sys
import tempfile
import unittest
from unittest.mock import patch, Mock

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import comprobar_supabase as cloud


class CloudProbeTests(unittest.TestCase):
    def setUp(self):
        self.data = {"project_ref": "a" * 20,
                     "url": "https://" + "a" * 20 + ".supabase.co",
                     "publishable_key": "sb_publishable_" + "x" * 24}
        self.config = cloud.PublicConfig.from_dict(self.data)

    def test_exact_public_configuration(self):
        self.assertEqual(self.data["url"], self.config.url)

    def test_ref_origin_and_https_are_bound(self):
        for url in ["http://" + "a" * 20 + ".supabase.co", self.data["url"] + "/path",
                    self.data["url"] + "@example.com", "https://" + "b" * 20 + ".supabase.co"]:
            with self.subTest(url=url), self.assertRaises(ValueError):
                cloud.PublicConfig.from_dict({**self.data, "url": url})

    def test_only_publishable_key_is_accepted(self):
        for key in ["", "eyJ" + "x" * 100, "sb_" + "secret_" + "x" * 25, None]:
            with self.subTest(kind=type(key).__name__), self.assertRaises(ValueError):
                cloud.PublicConfig.from_dict({**self.data, "publishable_key": key})

    def test_configuration_repr_omits_key(self):
        self.assertNotIn(self.config.publishable_key, repr(self.config))

    def test_denial_must_be_permission_error_not_missing_table(self):
        for name in ["profiles_denied", "drafts_denied"]:
            self.assertTrue(cloud.evaluate(name, 401, {"code": "42501"}))
            self.assertFalse(cloud.evaluate(name, 404, {"code": "PGRST205"}))
            self.assertFalse(cloud.evaluate(name, 200, []))
            self.assertFalse(cloud.evaluate(name, 401, {"code": "invalid_key"}))

    def test_happy_probe_pipeline_does_not_claim_login(self):
        seen = []
        def fake(config, probe):
            seen.append(probe.name)
            if probe.name == "auth_health": return 200, {"name": "GoTrue"}
            if probe.name == "email_provider_enabled": return 200, {"external": {"email": True}}
            if probe.name == "catalog_read": return 200, []
            if probe.name == "private_schema_not_exposed": return 406, {"code": "PGRST106"}
            if probe.name == "missing_key_denied": return 401, {}
            return 401, {"code": "42501"}
        report = cloud.run_checks(self.config, fake)
        self.assertEqual(7, report["passed"])
        self.assertEqual([probe.name for probe in cloud.PROBES], seen)
        self.assertFalse(report["authenticated_session_tested"])
        self.assertFalse(report["email_delivery_tested"])
        self.assertFalse(report["write_or_publication_performed"])
        self.assertNotIn(self.config.publishable_key, json.dumps(report))

    def test_transport_failure_does_not_disclose_exception_text(self):
        marker = "sensitive-response-not-to-log"
        def fail(*args): raise RuntimeError(marker)
        report = cloud.run_checks(self.config, fail)
        self.assertEqual(0, report["passed"])
        self.assertNotIn(marker, json.dumps(report))

    def test_redirect_is_not_followed(self):
        self.assertIsNone(cloud.NoRedirect().redirect_request(None, None, 302, "", {}, "https://example.com"))

    def test_without_explicit_flag_there_is_no_network(self):
        with tempfile.TemporaryDirectory() as folder:
            config = Path(folder) / "public.json"
            config.write_text(json.dumps(self.data))
            with patch.object(cloud.urllib.request, "build_opener") as opener, contextlib.redirect_stdout(io.StringIO()):
                self.assertEqual(0, cloud.main(["--config", str(config)]))
                opener.assert_not_called()

    def test_response_is_bounded_and_request_is_get(self):
        response = Mock(status=200)
        response.read.return_value = b"x" * (cloud.MAX_RESPONSE + 1)
        response.__enter__ = Mock(return_value=response)
        response.__exit__ = Mock(return_value=False)
        opener = Mock()
        opener.open.return_value = response
        with patch.object(cloud.urllib.request, "build_opener", return_value=opener):
            with self.assertRaises(ValueError):
                cloud.request(self.config, cloud.PROBES[0])
        req = opener.open.call_args.args[0]
        self.assertEqual("GET", req.method)
        self.assertEqual(self.config.publishable_key, req.get_header("Apikey"))
        response.read.assert_called_once_with(cloud.MAX_RESPONSE + 1)

    def test_schema_error_is_explicit(self):
        self.assertTrue(cloud.evaluate("private_schema_not_exposed", 406, {"code": "PGRST106"}))
        self.assertFalse(cloud.evaluate("private_schema_not_exposed", 404, {}))
        self.assertFalse(cloud.evaluate("email_provider_enabled", 200, {"external": {"email": False}}))
        self.assertFalse(cloud.evaluate("unknown", 200, {}))

    def test_migration_matches_the_previously_tested_schema(self):
        root = Path(__file__).resolve().parents[2]
        def normalized(path):
            return "\n".join(line.strip() for line in path.read_text().splitlines()
                             if line.strip() and not line.lstrip().startswith("--")
                             and line.strip() not in ("BEGIN;", "COMMIT;"))
        self.assertEqual(normalized(root / "backend/supabase/schema.sql"),
                         normalized(root / "supabase/migrations/20261102110405_inicial_catalogo_seguro_v1.sql"))


if __name__ == "__main__":
    unittest.main()
