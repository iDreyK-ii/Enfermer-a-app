package com.idreyk.enfermeria360

import android.content.Context
import android.net.Uri
import java.io.File
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class MasterCatalogTests {
    private lateinit var context: Context
    private lateinit var master: MasterCatalog
    private lateinit var repo: ContentRepository
    @Before fun setup() {
        context = RuntimeEnvironment.getApplication()
        context.deleteDatabase("idreyk_local.db")
        master = MasterCatalog.load(context)
        repo = ContentRepository(context)
        repo.initialize()
    }
    @After fun close() { repo.close() }
    private fun reject(action: () -> Unit) { assertThrows(IllegalArgumentException::class.java) { action() } }
    private fun source(): JSONObject = context.assets.open("base_maestra_mvp.json").bufferedReader().use { JSONObject(it.readText()) }
    private fun plan(): NotebookRecord = NotebookRecord("plan-qa", "plan", "mario", "Plan solo ficticio",
        JSONObject().apply { PLAN_FIELDS.keys.forEach { put(it, "") }; put("estado", "borrador") }, 1)

    @Test fun packagedInventoryHasExplicitCountsWithoutNewEditableMedicationDuplicates() {
        assertEquals(20, master.topics.size); assertEquals(554, master.inventory.size)
        assertEquals(8, master.devices.size); assertEquals(7, master.cases.size)
        assertEquals(21, master.cases.sumOf { it.questions.size })
        assertEquals(6, repo.entries().count { it.kind == "med" })
        assertEquals(8, repo.entries().size)
    }
    @Test fun importedNamesAreNeverPromotedByPresenceOfAnEditorialReference() {
        assertTrue(master.inventory.all { it.status == "IDENTIFICADO" && it.lines.isNotEmpty() && it.primary.isNotBlank() })
        assertEquals(master.inventory.size, master.inventory.map { normalized(it.name) }.distinct().size)
        val bad = source(); bad.getJSONArray("inventory").getJSONObject(0).put("status", "PUBLICABLE")
        reject { MasterCatalog.decode(bad.toString()) }
    }
    @Test fun unclearTranscriptionIsNotSilentlyChangedIntoAnotherMedicine() {
        val uncertain = master.inventory.single { it.name == "altilice" }
        assertEquals("POR-IDENTIFICAR", uncertain.kind)
        assertNotEquals(uncertain.id, master.inventory.single { it.name == "Alteplasa" }.id)
        assertTrue(uncertain.notes.any { it.contains("no se transforma") })
        assertTrue(master.inventory.any { it.name == "Imipenem" && it.kind == "POR-IDENTIFICAR" })
    }
    @Test fun nominalSynonymsAndFormulationsHaveOneMasterAndTraceableRelations() {
        assertEquals(1, master.inventory.count { it.name == "Cloruro de potasio" })
        assertTrue(master.inventory.single { it.name == "Cloruro de potasio" }.aliases.contains("Potasio cloruro"))
        assertTrue(master.inventory.single { it.name == "Insulina humana" }.formulations.any { it.contains("NPH") })
        assertTrue(master.inventory.single { it.name == "Paracetamol" }.relations.isNotEmpty())
        assertEquals("paracetamol", master.inventory.single { it.name == "Paracetamol" }.existingId)
    }
    @Test fun nonDrugProductsAreNotCountedAsGenericDci() {
        assertEquals("HEMOCOMPONENTE", master.inventory.single { it.name == "Concentrado de hematíes" }.kind)
        assertEquals("NUTRICIÓN", master.inventory.single { it.name.contains("Ensure") }.kind)
        assertEquals("BIOLÓGICO", master.inventory.single { it.name == "Insulina humana" }.kind)
        assertEquals("COMBINACIÓN", master.inventory.single { it.name == "Amoxicilina + ácido clavulánico" }.kind)
    }
    @Test fun offlineSearchCoversContentTagsAreaDevicePartsAndAmbiguity() {
        assertTrue(master.search("no se convierten").any { it.route == "tema/unidades" })
        assertTrue(master.search("pistón").any { it.route == "dispositivo/jeringa" })
        assertTrue(master.search("altilice").any { it.kind == "Inventario" })
        assertTrue(master.search("TCE", kind = "Caso").any { it.route == "simulacion/tce-uci" })
        assertTrue(master.search("", area = "PAE", level = "Inicial", kind = "Tema").all { it.area == "PAE" && it.level == "Inicial" })
        assertTrue(master.search("sepsis", area = "PAE", kind = "Caso").isEmpty())
    }
    @Test fun everyTopicExposesAuthorshipVersionSourcesAndUnreviewedClinicalState() {
        assertEquals("2.1-mvp.1", master.version)
        assertEquals(64, master.sourceHash.length)
        master.topics.forEach { assertTrue(it.author.isNotBlank()); assertTrue(it.sources.isNotEmpty()); assertTrue(it.status.contains("PENDIENTE")) }
    }
    @Test fun pngPartsAreSeparateEntitiesWithAccessibleTextAndNormalizedCoordinates() {
        val syringe = master.devices.single { it.id == "jeringa" }
        assertNotEquals(syringe.parts.single { it.id == "embolo" }, syringe.parts.single { it.id == "piston" })
        val catheter = master.devices.single { it.id == "cateter-periferico" }
        assertTrue(catheter.parts.any { it.id == "cateter" }); assertTrue(catheter.parts.any { it.id == "aguja-introductora" })
        master.devices.forEach { d -> d.parts.forEach { assertTrue(it.name.isNotBlank()); assertTrue(it.function.isNotBlank()); assertTrue(it.safety.isNotBlank()) } }
        val bad = source(); bad.getJSONArray("devices").getJSONObject(0).getJSONArray("parts").getJSONObject(0).put("hotspot_x", 1.01)
        reject { MasterCatalog.decode(bad.toString()) }
    }
    @Test fun exactlyTenInstitutionalItemsStaySeparateFromTwentyOwnControls() {
        val official = master.checklists.single { it.id == "insnsb-10" }
        assertTrue(official.official); assertEquals(10, official.items.size)
        assertTrue(official.items[8].contains("alergias") && official.items[8].contains("otro medicamento"))
        assertTrue(official.scope.contains("p. 6")); assertTrue(official.scope.contains("neonatal"))
        val own = master.checklists.single { it.id == "idreyk-20" }
        assertFalse(own.official); assertEquals(20, own.items.size); assertTrue(own.scope.contains("No es una norma oficial"))
    }
    @Test fun allSevenCasesScoreActualChoicesAndRejectInvalidOrExtraAnswers() {
        master.cases.forEach { c ->
            assertEquals(0, c.score(emptyList()))
            assertEquals(c.questions.size, c.score(c.questions.map { it.correct }))
            assertEquals(0, c.score(c.questions.map { (it.correct + 1) % it.options.size }))
            reject { c.score(listOf(99)) }; reject { c.score(List(c.questions.size + 1) { 0 }) }
        }
    }
    @Test fun readingsAndCaseAnswersSurviveRepositoryCloseWithoutChangingClinicalData() {
        repo.toggleFavorite("dispositivo/jeringa")
        repo.saveLearning(LearningProgress("tema/unidades", master.version, true))
        val c = master.cases.first()
        repo.saveLearning(LearningProgress("simulacion/${c.id}", master.version, false, listOf(c.questions[0].correct)))
        repo.close(); repo = ContentRepository(context); repo.initialize()
        assertTrue(repo.learningRecords().getValue("tema/unidades").completed)
        assertEquals(1, repo.learningRecords().getValue("simulacion/${c.id}").answers.size)
        assertTrue("dispositivo/jeringa" in repo.favorites())
        assertEquals(6, repo.entries().count { it.kind == "med" })
    }
    @Test fun incompleteOrInvalidProgressCannotFalselyCompleteACaseOrWriteHistory() {
        val c = master.cases.first()
        reject { repo.saveLearning(LearningProgress("simulacion/${c.id}", master.version, true, listOf(0))) }
        reject { repo.saveLearning(LearningProgress("simulacion/${c.id}", master.version, false, listOf(99))) }
        reject { repo.saveLearning(LearningProgress("tema/inexistente", master.version, true)) }
        reject { repo.saveLearning(LearningProgress("tema/unidades", "otra-version", true)) }
        assertTrue(repo.learningRecords().isEmpty()); assertTrue(repo.practices().isEmpty())
    }
    @Test fun completingCaseAddsOneRealHistoryRecordAndResetDoesNotEraseHistory() {
        val c = master.cases.first()
        val p = LearningProgress("simulacion/${c.id}", master.version, true, c.questions.map { it.correct })
        repo.saveLearning(p); repo.saveLearning(p)
        assertEquals(1, repo.practices().size); assertTrue(repo.practices().single().summary.contains("3 de 3"))
        repo.clearLearning(); assertTrue(repo.learningRecords().isEmpty()); assertEquals(1, repo.practices().size)
    }
    @Test fun learningProgressRoundTripDoesNotCoerceFractionalAnswers() {
        val p = LearningProgress("simulacion/ecg", master.version, false, listOf(0), 1)
        assertEquals(p, LearningProgress.decode(p.encode()))
        val j = JSONObject(p.encode()).put("answers", JSONArray().put(1.5))
        reject { LearningProgress.decode(j.toString()) }
    }
    @Test fun oldNotebookFormatStillLoadsWhileNewFieldsRoundTripInVersionTwo() {
        val old = JSONObject().put("tipo_respaldo", "cuaderno_practica").put("version_esquema", 1).put("registros", JSONArray().put(plan().json()))
        assertEquals("plan-qa", NotebookCodec.decode(old.toString()).single().id)
        val current = plan().copy(fields = JSONObject(plan().fields.toString()).apply { EXTRA_PLAN_FIELDS.keys.forEach { put(it, "Ficticio: $it") } })
        val text = NotebookCodec.encode(listOf(current))
        assertEquals(2, JSONObject(text).getInt("version_esquema"))
        val back = NotebookCodec.decode(text).single()
        EXTRA_PLAN_FIELDS.keys.forEach { assertEquals("Ficticio: $it", back.fields.getString(it)) }
    }
    @Test fun unknownNotebookFieldsAreRejectedInsteadOfSilentlyDiscarded() {
        val bad = plan().copy(fields = JSONObject(plan().fields.toString()).put("campo_desconocido", "No perder en silencio"))
        reject { NotebookCodec.encode(listOf(bad)) }
        val tooLong = plan().copy(fields = JSONObject(plan().fields.toString()).put("soapie_s", "a".repeat(4001)))
        reject { NotebookCodec.encode(listOf(tooLong)) }
    }
    @Test fun structuredPaeSoapieAndSbarPersistThroughSqliteAndEducationalExport() {
        val p = plan().copy(fields = JSONObject(plan().fields.toString()).put("subjetivos", "Relato inventado").put("abcde_b", "Observación simulada").put("soapie_e", "Evaluación del ejercicio").put("sbar_r", "Solicitud de aclaración"))
        repo.saveNotebook(p)
        repo.close(); repo = ContentRepository(context); repo.initialize()
        val saved = repo.notebooks().single()
        assertEquals("Solicitud de aclaración", saved.fields.getString("sbar_r"))
        val text = NotebookEducation.markdown(saved)
        assertTrue(text.startsWith("# EJERCICIO EDUCATIVO")); assertTrue(text.contains("SOAPIE · E evaluación")); assertTrue(text.contains("SBAR · R solicitud"))
        val f = File(context.filesDir, "qa-plan.md")
        repo.exportEducationalMarkdown(Uri.fromFile(f), saved)
        assertEquals(text, f.readText())
    }
    @Test fun importingMarkdownCreatesAnUnreviewedNoteWithOriginalTextAndUndo() {
        val f = File(context.filesDir, "qa.md").apply { writeText("# Título\n\n| A | B |\n|---|---|\n| dato | ficticio |\n<script>texto literal</script>") }
        repo.importStudyMarkdown(Uri.fromFile(f))
        val note = repo.notebooks().single()
        assertEquals("note", note.kind); assertEquals(f.readText(), note.fields.getString("contenido"))
        assertTrue(note.fields.getString("etiquetas").contains("no acreditada"))
        repo.undoNotebook(); assertTrue(repo.notebooks().isEmpty())
    }
    @Test fun markdownImportRejectsBinaryOversizedOrMalformedUtf8() {
        val f = File(context.filesDir, "qa-mal.md")
        f.writeText("a".repeat(16_001)); reject { NotebookEducation.readMarkdown(context, Uri.fromFile(f)) }
        f.writeText("\u0000"); reject { NotebookEducation.readMarkdown(context, Uri.fromFile(f)) }
        f.writeBytes(byteArrayOf(0xC3.toByte(), 0x28)); assertThrows(java.nio.charset.CharacterCodingException::class.java) { NotebookEducation.readMarkdown(context, Uri.fromFile(f)) }
    }
    @Test fun markdownParsesStructureWithoutExecutingHtmlOrFetchingImages() {
        val b = StudyMarkdown.blocks("# Título\n- Ítem\n|A|B|\n|---|---|\n|1|2|\n```formula\na=b/c\n```\n<script>no ejecutar</script>")
        assertTrue(b.any { it is StudyBlock.Heading }); assertTrue(b.any { it is StudyBlock.Bullet })
        assertEquals(2, (b.first { it is StudyBlock.Table } as StudyBlock.Table).rows.size)
        assertEquals("a=b/c", (b.first { it is StudyBlock.Formula } as StudyBlock.Formula).text)
        assertTrue(b.any { it is StudyBlock.Paragraph && it.text.startsWith("<script>") })
        reject { StudyMarkdown.blocks("a\n".repeat(1001)) }
        reject { StudyMarkdown.blocks("|" + "a|".repeat(13)) }
    }
    @Test fun timerStagesHaveBoundariesAndNoUniversalSurgicalDuration() {
        for (seconds in listOf(40, 50, 60)) for (i in HygieneLesson.wash.indices) assertEquals(i, HygieneLesson.stage(HygieneLesson.startOfStep(i, seconds, HygieneLesson.wash.size), seconds, HygieneLesson.wash.size))
        assertEquals(HygieneLesson.rub.lastIndex, HygieneLesson.stage(30_000, 30, HygieneLesson.rub.size))
        reject { HygieneLesson.stage(10, 180, 8) }
    }
    @Test fun externalNortonAndRassOnlyValidateTheDocumentedNumericRanges() {
        assertEquals(5, NursingScales.externalScore("norton", "5")); assertEquals(20, NursingScales.externalScore("norton", "20"))
        assertEquals(-5, NursingScales.externalScore("rass", "-5")); assertEquals(4, NursingScales.externalScore("rass", "+4"))
        assertEquals(0, NursingScales.externalScore("rass", "0"))
        for (s in listOf("", "-6", "5", "1.5")) reject { NursingScales.externalScore("rass", s) }
        reject { NursingScales.externalScore("norton", "4") }; reject { NursingScales.externalScore("norton", "21") }
        assertTrue(master.modules.single { it.name == "Norton completo" }.status.startsWith("BLOQUEADO"))
    }
}
