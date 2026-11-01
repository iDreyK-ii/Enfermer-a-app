package com.idreyk.enfermeria360

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

/** Software arithmetic/structure tests; not a clinical validation of digital instruments. */
class NursingModuleTests {
    private fun combinations(
        items: List<ScaleItem>,
        at: Int = 0,
        chosen: List<Int> = emptyList(),
        check: (List<Int>) -> Unit,
    ) {
        if (at == items.size) check(chosen)
        else items[at].choices.forEach { combinations(items, at + 1, chosen + it.second, check) }
    }

    private fun root(): JSONObject =
        JSONObject()
            .put("version_esquema", 2)
            .put("id_paquete", "prueba-modulos")
            .put("version_contenido", 1)
            .put("titulo", "Pruebas de formato")
            .put("autor", "QA")
            .put("fecha_actualizacion", "2026-11-01")
            .put("licencia", "Texto ficticio")

    private fun note(id: String = "nota-1"): NotebookRecord =
        NotebookRecord(
            id,
            "note",
            "",
            "Apunte ficticio",
            JSONObject().put("contenido", "Texto ficticio").put("etiquetas", "QA"),
            1,
        )

    @Test
    fun capurroBAll1600CombinationsUse204AndFiveComponents() {
        var checked = 0
        combinations(NursingScales.capurroB.items) { scores ->
            val result = NursingScales.capurro("B", scores)!!
            val expected = 204 + scores.sum()
            assertEquals(expected, result.days)
            assertEquals(expected / 7, result.weeks)
            assertEquals(expected % 7, result.remainingDays)
            assertTrue(result.points in 0..94)
            checked++
        }
        assertEquals(1600, checked)
        assertEquals(
            GestationalEstimate(94, 298),
            NursingScales.capurro("B", listOf(24, 15, 15, 20, 20)),
        )
    }

    @Test
    fun capurroAAll6400CombinationsUse200AndExcludeNipple() {
        var checked = 0
        combinations(NursingScales.capurroA.items) { scores ->
            val result = NursingScales.capurro("A", scores)!!
            assertEquals(200 + scores.sum(), result.days)
            assertTrue(result.points in 0..109)
            checked++
        }
        assertEquals(6400, checked)
        assertEquals(6, NursingScales.capurroA.items.size)
        assertFalse(NursingScales.capurroA.items.any { it.label.contains("pezón") })
        assertEquals(
            GestationalEstimate(109, 309),
            NursingScales.capurro("A", listOf(24, 15, 20, 20, 18, 12)),
        )
    }

    @Test
    fun capurroNoDefaultsAndNoMixingVariants() {
        assertNull(NursingScales.capurro("B", List(5) { NursingScales.UNSET }))
        assertNull(NursingScales.capurro("A", listOf(0, 0, 0, 0, 0, NursingScales.UNSET)))
        assertThrows(IllegalArgumentException::class.java) {
            NursingScales.capurro("A", List(5) { 0 })
        }
        assertThrows(IllegalArgumentException::class.java) {
            NursingScales.capurro("B", listOf(1, 0, 0, 0, 0))
        }
        assertThrows(IllegalArgumentException::class.java) {
            NursingScales.capurro("C", List(5) { 0 })
        }
    }

    @Test
    fun neonatalTotalsAll243CombinationsEach() {
        for (def in listOf(NursingScales.apgar, NursingScales.silverman)) {
            var count = 0
            combinations(def.items) { scores ->
                assertEquals(scores.sum(), NursingScales.total(def, scores))
                count++
            }
            assertEquals(243, count)
            assertEquals(0, NursingScales.total(def, List(5) { 0 }))
            assertEquals(10, NursingScales.total(def, List(5) { 2 }))
            assertNull(NursingScales.total(def, List(5) { NursingScales.UNSET }))
            assertThrows(IllegalArgumentException::class.java) {
                NursingScales.total(def, listOf(0, 1, 2, 3, 0))
            }
        }
    }

    @Test
    fun apgarHundredIsExplicitInTwoPointDescriptor() {
        val pulse = NursingScales.apgar.items.first()
        assertEquals("100 latidos/min o más", pulse.choices.single { it.second == 2 }.first)
    }

    @Test
    fun externallyObtainedScoresOnlyCheckIntegerAndRange() {
        assertEquals(6, NursingScales.externalScore("braden", "6"))
        assertEquals(23, NursingScales.externalScore("braden", "23"))
        assertEquals(0, NursingScales.externalScore("morse", "0"))
        assertEquals(125, NursingScales.externalScore("morse", "125"))
        for ((kind, value) in
            listOf(
                "braden" to "5",
                "braden" to "24",
                "morse" to "126",
                "morse" to "-1",
                "morse" to "1.5",
                "unknown" to "5",
            )) assertThrows(IllegalArgumentException::class.java) {
            NursingScales.externalScore(kind, value)
        }
    }

    @Test
    fun volumeDimensionalArithmetic() {
        assertEquals(2.5, DoseMath.volume(250.0, "mg", 500.0, "mg", 5.0), 1e-12)
        assertEquals(5.0, DoseMath.volume(0.5, "g", 1000.0, "mg", 10.0), 1e-12)
        assertEquals(4.0, DoseMath.volume(200.0, "mcg", 0.1, "mg", 2.0), 1e-12)
        assertEquals(0.5, DoseMath.volume(50.0, "UI", 100.0, "UI", 1.0), 1e-12)
    }

    @Test
    fun volumeRejectsIncompatibleAndUnknownUnits() {
        for (pair in listOf("UI" to "mg", "mg" to "UI", "mL" to "mg", "µg" to "mg")) assertThrows(
            IllegalArgumentException::class.java
        ) {
            DoseMath.volume(1.0, pair.first, 2.0, pair.second, 1.0)
        }
    }

    @Test
    fun dilutionConservesAmountAndDistinguishesFinalVolume() {
        val r = DoseMath.dilution(20.0, "mg/mL", 5.0, "mg/mL", 100.0)
        assertEquals(25.0, r.stockMl, 1e-12)
        assertEquals(75.0, r.additionalMl, 1e-12)
        assertEquals(100.0, r.finalMl, 1e-12)
        assertEquals(20 * r.stockMl, 5 * r.finalMl, 1e-12)
        val converted = DoseMath.dilution(2.0, "g/L", 500.0, "mcg/mL", 100.0)
        assertEquals(25.0, converted.stockMl, 1e-12)
        assertEquals(0.0, DoseMath.dilution(5.0, "mg/mL", 5.0, "mg/mL", 50.0).additionalMl, 0.0)
    }

    @Test
    fun dilutionCannotConcentrateOrMixUnits() {
        assertThrows(IllegalArgumentException::class.java) {
            DoseMath.dilution(1.0, "mg/mL", 2.0, "mg/mL", 10.0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            DoseMath.dilution(10.0, "UI/mL", 1.0, "mg/mL", 10.0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            DoseMath.dilution(10.0, "%", 1.0, "%", 10.0)
        }
    }

    @Test
    fun invalidNumbersAndUnderflowNeverYieldAnAdministrableZero() {
        for (n in listOf(Double.NaN, Double.POSITIVE_INFINITY, 0.0, -1.0, 1e10)) {
            assertThrows(IllegalArgumentException::class.java) {
                DoseMath.volume(n, "mg", 1.0, "mg", 1.0)
            }
            assertThrows(IllegalArgumentException::class.java) {
                DoseMath.dilution(10.0, "mg/mL", 1.0, "mg/mL", n)
            }
        }
        assertThrows(IllegalArgumentException::class.java) {
            DoseMath.volume(1e-300, "mcg", 1e9, "g", 1e-30)
        }
        assertFalse(DoseMath.display(1e-12) == "0")
    }

    @Test
    fun decimalInputIsExplicitAndFinite() {
        assertEquals(2.5, DoseMath.number(" 2,5 "), 0.0)
        for (text in listOf("NaN", "Infinity", "1e999", "", "1,000.5")) assertThrows(
            IllegalArgumentException::class.java
        ) {
            DoseMath.number(text)
        }
    }

    @Test
    fun protocolUsesSchemaTwoAndOneDoesNotIgnoreIt() {
        val protocol =
            CatalogAuthoring.empty("protocol", "protocolo-1")
                .put("titulo", "Documento ficticio")
                .put("contenido", "No es un procedimiento clínico")
        val json = root().put("protocolos", JSONArray().put(protocol))
        val result = ContentParser.parse(json.toString())
        assertEquals("protocol", result.entries.single().kind)
        assertEquals("No es un procedimiento clínico", result.entries.single().body)
        json.put("version_esquema", 1)
        assertThrows(IllegalArgumentException::class.java) { ContentParser.parse(json.toString()) }
    }

    @Test
    fun protocolKeepsTextLimitsAndReviewChecks() {
        val protocol =
            CatalogAuthoring.empty("protocol", "p1").put("titulo", "QA").put("contenido", true)
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(root().put("protocolos", JSONArray().put(protocol)).toString())
        }
        protocol.put("contenido", "").put("revision", "declarada_por_autor")
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(root().put("protocolos", JSONArray().put(protocol)).toString())
        }
    }

    @Test
    fun localEditingResetsReviewWithoutErasingAttributionOrMutatingOriginal() {
        val original =
            CatalogAuthoring.empty("med", "qa")
                .put("revision", "declarada_por_autor")
                .put("revisor", "Revisor ficticio")
                .put("fecha_revision", "2026-11-01")
                .put("_paquete", "origen")
                .put("_autor", "Autor original")
                .put("_licencia", "Permiso de prueba")
                .put("_actualizacion", "2026-11-01")
        val copy = CatalogAuthoring.draft(original, original)
        assertEquals("borrador", copy.getString("revision"))
        assertFalse(copy.has("revisor"))
        assertFalse(copy.has("fecha_revision"))
        assertEquals("Autor original", copy.getJSONObject("derivado_de").getString("autor"))
        assertEquals("declarada_por_autor", original.getString("revision"))
        assertEquals(
            copy.getJSONObject("derivado_de").toString(),
            CatalogAuthoring.draft(copy, copy).getJSONObject("derivado_de").toString(),
        )
    }

    @Test
    fun notebookRoundTripAndLiteralText() {
        val r =
            note()
                .copy(
                    fields =
                        JSONObject()
                            .put("contenido", "<script>texto literal</script>\nValoración ficticia")
                            .put("etiquetas", "PAE")
                )
        val parsed = NotebookCodec.decode(NotebookCodec.encode(listOf(r))).single()
        assertEquals(r.json().toString(), parsed.json().toString())
        assertEquals(
            emptyList<NotebookRecord>(),
            NotebookCodec.decode(NotebookCodec.encode(emptyList())),
        )
    }

    @Test
    fun notebookRejectsDuplicateIdsForeignTypesAndUnknownOwners() {
        assertThrows(IllegalArgumentException::class.java) {
            NotebookCodec.encode(listOf(note(), note()))
        }
        assertThrows(IllegalArgumentException::class.java) {
            NotebookCodec.record(note().json().put("tipo", "prescripcion"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            NotebookCodec.record(note().json().put("persona_ficticia", "mario"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            NotebookCodec.record(note().json().put("id", "../unsafe"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            NotebookCodec.decode(root().toString())
        }
    }

    @Test
    fun structuredPlanHasAllFieldsAndNoAutomaticTaxonomies() {
        val fields =
            JSONObject().apply {
                PLAN_FIELDS.keys.forEach { put(it, "") }
                put("estado", "borrador")
            }
        val plan = NotebookRecord("plan-1", "plan", "mario", "Plan de prueba", fields, 1)
        assertEquals(
            "mario",
            NotebookCodec.decode(NotebookCodec.encode(listOf(plan))).single().owner,
        )
        assertThrows(IllegalArgumentException::class.java) {
            NotebookCodec.record(plan.json().put("persona_ficticia", "paciente-real"))
        }
        fields.put("diagnostico", 123)
        assertThrows(IllegalArgumentException::class.java) { NotebookCodec.record(plan.json()) }
    }

    @Test
    fun notebookCapsKeepRestorableExportsAndValidateFields() {
        assertThrows(IllegalArgumentException::class.java) {
            NotebookCodec.encode((0..200).map { note("nota-$it") })
        }
        assertThrows(IllegalArgumentException::class.java) {
            NotebookCodec.record(note().json().put("titulo", ""))
        }
        assertThrows(IllegalArgumentException::class.java) {
            NotebookCodec.record(note().json().put("modificado", 1.5))
        }
        val tooLong =
            note()
                .copy(
                    fields = JSONObject().put("contenido", "x".repeat(16001)).put("etiquetas", "")
                )
        assertThrows(IllegalArgumentException::class.java) { NotebookCodec.encode(listOf(tooLong)) }
        assertThrows(IllegalArgumentException::class.java) {
            NotebookCodec.encode(
                (0 until 150).map {
                    note("nota-$it")
                        .copy(
                            fields =
                                JSONObject()
                                    .put("contenido", "x".repeat(16000))
                                    .put("etiquetas", "")
                        )
                }
            )
        }
    }

    @Test
    fun pulseChartUsesOnlySavedReadingsForTheSelectedFictitiousPerson() {
        val records =
            listOf(
                StudyNote(1, "vitals:mario", "120|80|70|36.5|98", 2),
                StudyNote(2, "vitals:rosa", "110|70|90|36.4|98", 3),
                StudyNote(3, "vitals:mario", "120|80|72,5|36.6|98", 4),
                StudyNote(4, "mario", "Nota libre", 5),
            )
        assertEquals(listOf(70.0, 72.5), PracticeTrends.pulse(records, "mario").map { it.beats })
        assertTrue(PracticeTrends.pulse(records, "luis").isEmpty())
    }

    @Test
    fun pulseChartDoesNotInventFallbackDataAndCapsAt20() {
        val invalid =
            listOf(
                "120|80|NaN|36.5|98",
                "texto sin estructura",
                "120|80|Infinity|36.5|98",
                "120|80|-1|36.5|98",
            )
        assertTrue(
            PracticeTrends.pulse(
                    invalid.mapIndexed { i, t -> StudyNote(i.toLong(), "vitals:mario", t, 1) },
                    "mario",
                )
                .isEmpty()
        )
        val saved =
            (1..30)
                .map { StudyNote(it.toLong(), "vitals:mario", "120|80|$it|36.5|98", it.toLong()) }
                .reversed()
        assertEquals(
            (11..30).map { it.toDouble() },
            PracticeTrends.pulse(saved, "mario").map { it.beats },
        )
    }
}
