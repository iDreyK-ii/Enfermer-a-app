package com.idreyk.enfermeria360

import javax.crypto.KeyGenerator
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class EvolutionTests {
    private fun seed(): JSONObject =
        JSONObject(java.io.File("src/main/assets/contenido_inicial.json").readText())

    private fun v3(): JSONObject = seed().put("version_esquema", 3)

    private val capable = DeviceSignals(totalRamBytes = 8L * 1024 * 1024 * 1024)
    private val config =
        CloudConfig("https://test-project.supabase.co", "sb_publishable_testing_only_12345678")

    @Test
    fun pdfHasSeventeenDistinctConceptsAndLegacyFieldsRemain() {
        assertEquals(17, PharmacologyLayout.technical.size)
        assertEquals(20, LEGACY_DRUG_KEYS.size)
        assertEquals(30, DRUG_FIELDS.size)
        assertTrue(
            PharmacologyLayout.technical.keys.filter { it != "nombre" }.all { it in DRUG_FIELDS }
        )
    }

    @Test
    fun editorGroupsExposeEveryFieldExactlyOnce() {
        val fields = PharmacologyLayout.editorGroups().flatMap { it.second }
        assertEquals(DRUG_FIELDS.keys, fields.toSet())
        assertEquals(fields.size, fields.toSet().size)
    }

    @Test
    fun partialV3AcceptsOnlyTheProvidedFields() {
        val root = v3()
        root
            .getJSONArray("medicamentos")
            .getJSONObject(0)
            .put(
                "secciones",
                JSONObject().put("antes_administrar", "Contenido ficticio de formato"),
            )
        val record = ContentParser.parse(root.toString()).entries.first { it.kind == "med" }
        assertEquals("Contenido ficticio de formato", record.sections["antes_administrar"])
        assertEquals("", record.sections["sueros_compatibles"])
        assertEquals("borrador", record.revision)
    }

    @Test
    fun newFieldsRequireExplicitSchemaThree() {
        for (version in listOf(1, 2)) {
            val root = seed().put("version_esquema", version)
            root
                .getJSONArray("medicamentos")
                .getJSONObject(0)
                .getJSONObject("secciones")
                .put("almacenamiento", "Dato ficticio")
            assertThrows(IllegalArgumentException::class.java) {
                ContentParser.parse(root.toString())
            }
        }
    }

    @Test
    fun allOldSeedEntriesStillReadWithoutInventingNursingInstructions() {
        val entries = ContentParser.parse(seed().toString()).entries
        assertEquals(8, entries.size)
        val med = entries.first { it.kind == "med" }
        assertEquals(
            "",
            PharmacologyLayout.summary(med, listOf("objetivo_terapeutico", "antes_administrar")),
        )
        assertEquals(med.name, PharmacologyLayout.text(med, "nombre"))
    }

    @Test
    fun automaticProfileIsFullOnlyWithSufficientResources() {
        assertFalse(PerformancePolicy.decide("auto", false, capable).reduced)
        assertTrue(
            PerformancePolicy.decide(
                    "auto",
                    false,
                    capable.copy(totalRamBytes = 2L * 1024 * 1024 * 1024),
                )
                .reduced
        )
    }

    @Test
    fun lowRamAndUnknownMemoryChooseLightAutomatically() {
        assertTrue(PerformancePolicy.decide("auto", false, capable.copy(lowRam = true)).reduced)
        assertTrue(PerformancePolicy.decide("auto", false, DeviceSignals()).reduced)
    }

    @Test
    fun explicitModeDoesNotRemoveAccessibilityPreference() {
        assertTrue(PerformancePolicy.decide("full", true, capable).reduced)
        assertTrue(
            PerformancePolicy.decide("full", false, capable.copy(systemReduced = true)).reduced
        )
        assertTrue(PerformancePolicy.decide("light", false, capable).reduced)
    }

    @Test
    fun BatteryAndMemoryPressureOverrideFullMode() {
        assertTrue(PerformancePolicy.decide("full", false, capable.copy(powerSave = true)).reduced)
        assertTrue(
            PerformancePolicy.decide("full", false, capable.copy(memoryPressure = true)).reduced
        )
    }

    @Test
    fun backgroundPausesDecorativeMotion() {
        assertTrue(
            PerformancePolicy.decide("full", false, capable.copy(foreground = false)).reduced
        )
        assertFalse(PerformancePolicy.decide("full", false, capable).reduced)
    }

    @Test
    fun cloudNeedsHttpsProjectAndPublishableKey() {
        config.validate()
        for (url in
            listOf(
                "http://test.supabase.co",
                "https://evil.example",
                "https://test.supabase.co.evil.example",
                "https://user@test.supabase.co",
                "https://test.supabase.co/path",
            )) {
            assertThrows(IllegalArgumentException::class.java) {
                CloudConfig(url, config.publishableKey).validate()
            }
        }
        assertThrows(IllegalArgumentException::class.java) {
            CloudConfig(config.url, "sb_secret_never_ship_this").validate()
        }
    }

    @Test
    fun unconfiguredClientDoesNotCallTransport() {
        val fake = FakeTransport("{}")
        assertThrows(IllegalArgumentException::class.java) {
            CloudService(CloudConfig("", ""), fake).sendCode("qa@example.invalid")
        }
        assertEquals(0, fake.calls)
    }

    private class FakeTransport(var result: String) : CloudTransport {
        var calls = 0
        var path = ""
        var body: JSONObject? = null

        override fun request(
            config: CloudConfig,
            path: String,
            body: JSONObject?,
            token: String?,
            maximum: Int,
        ): String {
            calls++
            this.path = path
            this.body = body
            return result
        }
    }

    @Test
    fun requestingCodeUsesJsonAndDoesNotCreateAFakeSession() {
        val fake = FakeTransport("{}")
        CloudService(config, fake).sendCode("qa+test@example.invalid")
        assertEquals("/auth/v1/otp", fake.path)
        assertEquals("qa+test@example.invalid", fake.body!!.getString("email"))
        assertTrue(fake.body!!.getBoolean("create_user"))
    }

    @Test
    fun invalidEmailAndCodeNeverReachTransport() {
        val fake = FakeTransport("{}")
        val service = CloudService(config, fake)
        assertThrows(IllegalArgumentException::class.java) { service.sendCode("bad email") }
        assertThrows(IllegalArgumentException::class.java) {
            service.verify("qa@example.invalid", "short")
        }
        assertEquals(0, fake.calls)
    }

    private fun sessionResponse(): JSONObject =
        JSONObject()
            .put("access_token", "fake-access-token-for-unit-test-only")
            .put("refresh_token", "fake-refresh-token-for-unit-test-only")
            .put("expires_in", 3600)
            .put(
                "user",
                JSONObject()
                    .put("id", "720fe413-8773-4b5a-9607-b812944f0f00")
                    .put("email", "qa@example.invalid"),
            )

    @Test
    fun verificationRequiresRealResponseStructureAndRedactsSessionPrinting() {
        val fake = FakeTransport(sessionResponse().toString())
        val session = CloudService(config, fake).verify("qa@example.invalid", "123456")
        assertEquals("email", fake.body!!.getString("type"))
        assertEquals("/auth/v1/verify", fake.path)
        assertFalse(session.toString().contains(session.access))
        assertEquals(3700L, CloudSession.fromResponse(sessionResponse().toString(), 100).expiresAt)
    }

    @Test
    fun missingSessionFieldsCannotBeReportedAsLoginSuccess() {
        assertThrows(org.json.JSONException::class.java) { CloudSession.fromResponse("{}") }
    }

    private fun releasePackage(): String {
        val record =
            JSONObject()
                .put("id", "caso-formato-qa")
                .put("titulo", "Ejemplo ficticio de estructura")
                .put("revision", "declarada_por_autor")
                .put("revisor", "QA de formato, no revisión clínica")
                .put("fecha_revision", "2026-11-01")
                .put(
                    "fuentes",
                    JSONArray()
                        .put(
                            JSONObject()
                                .put("titulo", "Referencia ficticia de prueba")
                                .put("url", "https://example.invalid/qa")
                        ),
                )
        return JSONObject()
            .put("version_esquema", 3)
            .put("id_paquete", "catalogo-oficial")
            .put("version_contenido", 1)
            .put("titulo", "Fixture no clínica")
            .put("autor", "QA de software")
            .put("fecha_actualizacion", "2026-11-01")
            .put("licencia", "Datos ficticios de prueba; no usar clínicamente")
            .put("casos", JSONArray().put(record))
            .toString()
    }

    private fun manifest(payload: String = releasePackage()): JSONObject =
        JSONObject()
            .put("version", 1)
            .put("min_app_version", 4)
            .put("sha256", CloudProtocol.hash(payload))
            .put("package_json", payload)

    @Test
    fun correctIntegrityAndPublishedPackageRoundTrip() {
        val payload = releasePackage()
        assertEquals(
            payload,
            CloudProtocol.release(JSONArray().put(manifest(payload)).toString(), 4),
        )
    }

    @Test
    fun emptyPublicationIsNotFabricatedContent() {
        assertNull(CloudProtocol.release("[]", 4))
    }

    @Test
    fun corruptionNeverPassesIntegrityCheck() {
        val row = manifest().put("sha256", "0".repeat(64))
        assertThrows(IllegalArgumentException::class.java) {
            CloudProtocol.release(JSONArray().put(row).toString(), 4)
        }
    }

    @Test
    fun incompatibleAppAndMismatchedVersionAreRejected() {
        for (row in listOf(manifest().put("min_app_version", 5), manifest().put("version", 2))) {
            assertThrows(IllegalArgumentException::class.java) {
                CloudProtocol.release(JSONArray().put(row).toString(), 4)
            }
        }
    }

    @Test
    fun draftsDoNotEnterPublishedChannelEvenWithMatchingHash() {
        val pack = JSONObject(releasePackage())
        pack.getJSONArray("casos").getJSONObject(0).put("revision", "borrador")
        assertThrows(IllegalArgumentException::class.java) {
            CloudProtocol.release(JSONArray().put(manifest(pack.toString())).toString(), 4)
        }
    }

    @Test
    fun wrongPackageNamespaceIsRejected() {
        val pack = JSONObject(releasePackage()).put("id_paquete", "otro-canal").toString()
        assertThrows(IllegalArgumentException::class.java) {
            CloudProtocol.release(JSONArray().put(manifest(pack)).toString(), 4)
        }
    }

    @Test
    fun multipleOrOversizedPublicationsAreRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            CloudProtocol.release(JSONArray().put(manifest()).put(manifest()).toString(), 4)
        }
        val payload = "x".repeat(ContentParser.MAX_BYTES + 1)
        assertThrows(IllegalArgumentException::class.java) {
            CloudProtocol.release(JSONArray().put(manifest(payload)).toString(), 4)
        }
    }

    @Test
    fun authenticatedEncryptionIsRandomizedAndNotPlaintext() {
        val key = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
        val secret = sessionResponse().toString()
        val first = SessionCipher.encrypt(secret, key)
        val second = SessionCipher.encrypt(secret, key)
        assertNotEquals(first, second)
        assertFalse(first.contains("fake-access-token"))
        assertEquals(secret, SessionCipher.decrypt(first, key))
    }

    @Test
    fun wrongKeyOrTamperingCannotRevealStoredSession() {
        val firstKey = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
        val secondKey = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
        val encrypted = SessionCipher.encrypt("fake-secret", firstKey)
        assertThrows(Exception::class.java) { SessionCipher.decrypt(encrypted, secondKey) }
        val parts = encrypted.split(':')
        val bytes = java.util.Base64.getDecoder().decode(parts[1])
        bytes[0] = (bytes[0].toInt() xor 1).toByte()
        assertThrows(Exception::class.java) {
            SessionCipher.decrypt(
                parts[0] + ":" + java.util.Base64.getEncoder().encodeToString(bytes),
                firstKey,
            )
        }
    }

    @Test
    fun commercialNamesAreSearchableWithoutLosingSynonyms() {
        val root = v3()
        val original = root.getJSONArray("medicamentos").getJSONObject(0)
        original.put("sinonimos", JSONArray().put("Alias ficticio"))
        original.getJSONObject("secciones").put("nombres_comerciales", "Marca ficticia QA")
        val entry = ContentParser.parse(root.toString()).entries.first { it.kind == "med" }
        assertTrue(normalized(entry.aliases).contains("marca ficticia qa"))
        assertTrue(normalized(entry.aliases).contains("alias ficticio"))
    }
}
