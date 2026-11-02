package com.idreyk.enfermeria360

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

internal object PolishFixtures {
    fun drug(id: String = "ficha-qa", text: String = "Texto ficticio") =
        JSONObject()
            .put("id", id)
            .put("nombre", "Ficha ficticia $id")
            .put("revision", "borrador")
            .put("secciones", JSONObject().put("descripcion", text))

    fun pack(vararg records: JSONObject): ContentPackage =
        ContentParser.parse(
            JSONObject()
                .put("version_esquema", 3)
                .put("id_paquete", "paquete-polish-qa")
                .put("version_contenido", 1)
                .put("titulo", "Prueba de estructura, no clínica")
                .put("autor", "QA")
                .put("fecha_actualizacion", "2026-11-01")
                .put("licencia", "Solo datos ficticios")
                .put("medicamentos", JSONArray(records.toList()))
                .toString()
        )

    fun review(pack: ContentPackage, current: List<Entry> = emptyList()) =
        ImportPreview(
            pack,
            pack.entries.count { next -> current.none { it.key == next.key } },
            pack.entries.count { next -> current.any { it.key == next.key } },
            ImportReview.compare(pack.entries, current),
            ImportReview.fingerprint(current, pack, 0),
        )
}

class PolishTests {
    @Test
    fun narrowScreensReduceColumnsWithoutHidingModules() {
        assertEquals(3, AdaptiveLayoutPolicy.columns(353f, 1f))
        assertEquals(2, AdaptiveLayoutPolicy.columns(280f, 1f))
        assertEquals(1, AdaptiveLayoutPolicy.columns(150f, 1f))
    }

    @Test
    fun enlargedTextReducesColumnsAndKeepsAValidMinimum() {
        assertEquals(2, AdaptiveLayoutPolicy.columns(353f, 1.3f))
        assertEquals(1, AdaptiveLayoutPolicy.columns(300f, 2f))
        assertEquals(1, AdaptiveLayoutPolicy.columns(Float.NaN, 1f))
        assertEquals(2, AdaptiveLayoutPolicy.columns(900f, 1f, 2))
    }

    @Test
    fun welcomeArtYieldsSpaceToSmallScreensAndLargeText() {
        assertTrue(AdaptiveLayoutPolicy.showWelcomeArt(353f, 1f))
        assertFalse(AdaptiveLayoutPolicy.showWelcomeArt(280f, 1f))
        assertFalse(AdaptiveLayoutPolicy.showWelcomeArt(353f, 2f))
    }

    @Test
    fun reducedMotionNeverScalesPressedCardsOrSelectedTabs() {
        assertEquals(1f, UiFeedbackPolicy.pressScale(true, true), 0f)
        assertEquals(1f, UiFeedbackPolicy.tabScale(true, true), 0f)
        assertTrue(UiFeedbackPolicy.pressScale(false, true) < 1f)
        assertTrue(UiFeedbackPolicy.tabScale(false, true) > 1f)
        assertEquals(1f, UiFeedbackPolicy.pressScale(false, false), 0f)
    }

    @Test
    fun depthGuardIgnoresBracketsAndEscapesInsideText() {
        JsonSafety.requireBoundedDepth(
            JSONObject().put("text", "[".repeat(2000) + "\\\"}" + "]".repeat(2000)).toString()
        )
    }

    @Test
    fun depthGuardAcceptsTheBoundaryAndRejectsExcessNesting() {
        JsonSafety.requireBoundedDepth("[".repeat(32) + "0" + "]".repeat(32))
        assertThrows(IllegalArgumentException::class.java) {
            JsonSafety.requireBoundedDepth("[".repeat(33) + "0" + "]".repeat(33))
        }
    }

    @Test
    fun depthGuardRejectsUnfinishedStringsAndDelimiters() {
        for (raw in listOf("{\"text\":\"unfinished", "{", "]")) {
            assertThrows(IllegalArgumentException::class.java) {
                JsonSafety.requireBoundedDepth(raw)
            }
        }
    }

    @Test
    fun allExternalJsonCodecsRejectDeepInputBeforeRecursiveParsing() {
        val raw = "{\"x\":" + "[".repeat(2000) + "0" + "]".repeat(2000) + "}"
        val codecs: List<() -> Unit> =
            listOf(
                { ContentParser.parse(raw) },
                { NotebookCodec.decode(raw) },
                { CloudSession.fromResponse(raw) },
                { CloudProtocol.release(raw, 5) },
            )
        codecs.forEach { codec ->
            val error = assertThrows(IllegalArgumentException::class.java) { codec() }
            assertTrue(error.message.orEmpty().contains("anidado"))
        }
    }

    @Test
    fun partialReplacementReportsWhichExistingFieldsWillBeCleared() {
        val previous = PolishFixtures.pack(PolishFixtures.drug()).entries
        val next =
            PolishFixtures.pack(PolishFixtures.drug(text = "").put("secciones", JSONObject()))
        val change = ImportReview.compare(next.entries, previous).single()
        assertEquals(listOf(DRUG_FIELDS.getValue("descripcion")), change.clearedFields)
        assertTrue(change.isReplacement)
        assertEquals("Texto ficticio", previous.single().sections["descripcion"])
    }

    @Test
    fun newlyAddedPartialRecordsDoNotClaimExistingDataWillBeLost() {
        val next = PolishFixtures.pack(PolishFixtures.drug(text = ""))
        val change = ImportReview.compare(next.entries, emptyList()).single()
        assertFalse(change.isReplacement)
        assertTrue(change.clearedFields.isEmpty())
    }

    @Test
    fun existingLocalEditsAreExplicitlyIdentified() {
        val previous = PolishFixtures.pack(PolishFixtures.drug()).entries.single()
        previous.json.put("_paquete", "edicion-local-fixture")
        val next = PolishFixtures.pack(PolishFixtures.drug(text = "Otro texto ficticio"))
        assertTrue(ImportReview.compare(next.entries, listOf(previous)).single().replacesLocalEdit)
    }

    @Test
    fun removingReferencesIsAlsoReportedInsteadOfOnlyCheckingDrugSections() {
        val record =
            PolishFixtures.drug()
                .put(
                    "fuentes",
                    JSONArray()
                        .put(
                            JSONObject()
                                .put("titulo", "Fuente ficticia de QA")
                                .put("url", "https://example.invalid/qa")
                        ),
                )
        val previous = PolishFixtures.pack(record).entries
        val next = PolishFixtures.pack(PolishFixtures.drug())
        assertTrue(
            ImportReview.compare(next.entries, previous)
                .single()
                .clearedFields
                .contains("Fuentes documentales")
        )
    }

    @Test
    fun absentAndEmptyOptionalSectionsAreEquivalent() {
        val previous = PolishFixtures.pack(PolishFixtures.drug(text = "")).entries
        val next = PolishFixtures.pack(PolishFixtures.drug().put("secciones", JSONObject()))
        assertTrue(ImportReview.compare(next.entries, previous).single().changedFields.isEmpty())
    }

    @Test
    fun canonicalObjectsIgnoreKeyOrderButArraysKeepOrder() {
        assertEquals(
            ImportReview.canonical(JSONObject("{\"a\":1,\"b\":2}")),
            ImportReview.canonical(JSONObject("{\"b\":2,\"a\":1}")),
        )
        assertNotEquals(
            ImportReview.canonical(JSONArray("[1,2]")),
            ImportReview.canonical(JSONArray("[2,1]")),
        )
    }

    @Test
    fun reviewFingerprintBindsBothContentAndPreviouslyInstalledVersion() {
        val pack = PolishFixtures.pack(PolishFixtures.drug())
        val changed = PolishFixtures.pack(PolishFixtures.drug(text = "Otro dato ficticio"))
        assertNotEquals(
            ImportReview.fingerprint(emptyList(), pack, 0),
            ImportReview.fingerprint(emptyList(), changed, 0),
        )
        assertNotEquals(
            ImportReview.fingerprint(emptyList(), pack, 0),
            ImportReview.fingerprint(emptyList(), pack, 1),
        )
    }
}
