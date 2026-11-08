package com.idreyk.enfermeria360

import java.io.File
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class ClinicalCatalogTests {
    private fun raw(): String = File("src/main/assets/${ClinicalCatalog.ASSET}").readText()
    private fun catalog() = ClinicalCatalog.parse(raw())
    private fun rejected(change: (JSONObject) -> Unit) {
        val obj = JSONObject(raw())
        change(obj)
        assertThrows(IllegalArgumentException::class.java) { ClinicalCatalog.parse(obj.toString()) }
    }

    @Test fun packagedCatalogHasExplicitScopeSourcesAndNoClinicalApproval() {
        val data = catalog()
        assertEquals(191, data.instruments.size)
        assertEquals(86, data.sources.size)
        assertEquals(13, data.instruments.count { it.hasPractice })
        assertTrue(data.scope.contains("biblioteca educativa/documental"))
        assertTrue(data.instruments.all { it.sourceIds.isNotEmpty() && it.rights.isNotBlank() })
    }

    @Test fun neonatalFilterDoesNotIncludeAdultOrGeneralPediatricTools() {
        val entries = catalog().filter(population = "neonatos")
        assertEquals(17, entries.size)
        assertTrue(entries.any { it.id == "apgar" })
        assertFalse(entries.any { it.id in listOf("glasgow", "4at", "news2", "pews", "numerica") })
    }

    @Test fun searchIgnoresAccentsAndCase() {
        val data = catalog()
        // Search intentionally includes the version, not only the title. Barthel documents
        // "escalas numéricas" in its version warning and is therefore a valid broad match.
        val broadMatches = listOf("numerica", "barthel")
        assertEquals(broadMatches, data.filter(query = "numérica").map { it.id })
        assertEquals(broadMatches, data.filter(query = "NUMERICA").map { it.id })
        assertEquals(listOf("numerica"), data.filter(query = "ESCALA NUMERICA").map { it.id })
        assertTrue(data.filter(query = "neonatologia").any { it.id == "apgar" })
    }

    @Test fun filtersCombineInsteadOfReplacingEachOther() {
        val data = catalog()
        assertEquals(listOf("nips", "pipp-r"), data.filter(population = "neonatos", area = "Dolor").map { it.id })
        assertTrue(data.filter(population = "neonatos", area = "Dolor", withPracticeOnly = true).isEmpty())
        assertEquals(13, data.filter(withPracticeOnly = true).size)
    }

    @Test fun accessTypeFiltersCombineWithPopulationAndNeverMakeDocumentaryCardsInteractive() {
        val data = catalog()
        val records = data.filter(availability = ClinicalAvailability.RECORD)
        assertEquals(setOf("glasgow-pediatrico", "numerica", "capurro-a", "braden", "morse", "norton", "rass"), records.map { it.id }.toSet())
        assertEquals(listOf("norton"), data.filter(query = "norton", population = "adultos", availability = ClinicalAvailability.RECORD).map { it.id })
        assertTrue(data.filter(population = "neonatos", availability = ClinicalAvailability.PERMISSION).none { it.hasPractice })
        assertTrue(data.filter(availability = ClinicalAvailability.DOCUMENT, withPracticeOnly = true).isEmpty())
        assertEquals("registro-escala/norton", data.instruments.single { it.id == "norton" }.route)
        assertEquals("registro-escala/rass", data.instruments.single { it.id == "rass" }.route)
    }

    @Test fun eachActionHasAnExplicitNativeRouteAndCapurroVariantsRemainDistinct() {
        val data = catalog()
        assertEquals(ClinicalCatalog.PRACTICE_ROUTES, data.instruments.filter { it.hasPractice }.associate { it.id to it.route })
        assertEquals("capurro/A", data.instruments.single { it.id == "capurro-a" }.route)
        assertEquals("capurro/B", data.instruments.single { it.id == "capurro-b" }.route)
    }

    @Test fun expandedServicesAreIndexedWithoutDuplicatingInstrumentIds() {
        val data = catalog()
        assertTrue(data.instruments.map { it.id }.distinct().size == 191)
        assertTrue(data.instruments.single { it.id == "tiss-28" }.services.contains("UCI adulto"))
        assertFalse(data.instruments.single { it.id == "tiss-28" }.services.contains("Emergencia"))
        assertTrue(data.instruments.single { it.id == "sofa" }.instrumentType.contains("clasificacion"))
        assertEquals(1, data.filter(service = "UCI adulto", query = "TISS-28").size)
        assertEquals(0, data.filter(service = "Neonatología", query = "TISS-28").size)
        assertEquals(0, data.filter(service = "Emergencia", query = "TISS-28").size)
        assertEquals(0, data.filter(service = "Pediatría", query = "NEWS2").size)
        assertEquals(0, data.filter(service = "Obstetricia y sala de partos", query = "NEWS2").size)
        assertEquals(0, data.filter(service = "UCI adulto", query = "PEWS").size)
        assertEquals(listOf("adultos"), data.instruments.single { it.id == "rass" }.populations)
        assertTrue(data.instruments.all { it.services.isNotEmpty() && it.nursingRole.isNotBlank() })
        assertTrue(data.filter(instrumentType = "Clasificación").any { it.id == "sofa" })
        assertTrue(data.filter(instrumentType = "Carga asistencial").any { it.id == "tiss-28" })
    }

    @Test fun futureFormatIsNotSilentlyAccepted() = rejected { it.put("formato", 2) }

    @Test fun duplicateInstrumentIsRejected() = rejected {
        val list = it.getJSONArray("instrumentos")
        list.put(list.getJSONObject(0))
    }

    @Test fun duplicateSourceIsRejected() = rejected {
        val list = it.getJSONArray("fuentes")
        list.put(list.getJSONObject(0))
    }

    @Test fun missingSourceIsRejected() = rejected {
        it.getJSONArray("instrumentos").getJSONObject(0).getJSONArray("fuentes").put(0, "inventada")
    }

    @Test fun insecureSourceIsRejected() = rejected {
        it.getJSONArray("fuentes").getJSONObject(0).put("url", "http://example.test/source")
    }

    @Test fun sourceWithEmbeddedCredentialsIsRejected() = rejected {
        it.getJSONArray("fuentes").getJSONObject(0).put("url", "https://user:password@example.test/source")
    }

    @Test fun falseClinicalValidationIsRejected() = rejected {
        it.getJSONArray("instrumentos").getJSONObject(0).put("revision_clinica", "aprobada")
    }

    @Test fun unrecognisedPopulationIsRejected() = rejected {
        it.getJSONArray("instrumentos").getJSONObject(0).getJSONArray("poblaciones").put(0, "cualquiera")
    }

    @Test fun undocumentedNativeRouteIsRejected() = rejected {
        it.getJSONArray("instrumentos").getJSONObject(0).put("ruta", "farmacologia")
    }

    @Test fun documentaryCardCannotPretendToBeInteractive() = rejected {
        it.getJSONArray("instrumentos").getJSONObject(0).put("estado", "ficha")
    }

    @Test fun oversizedInputIsRejectedBeforeParsing() {
        assertThrows(IllegalArgumentException::class.java) { ClinicalCatalog.parse(" ".repeat(500001)) }
    }

    @Test fun numericPainDistinguishesZeroPendingAndAbsentSelfReport() {
        assertEquals(0, ClinicalPractice.numericPain(0))
        assertEquals(10, ClinicalPractice.numericPain(10))
        assertNull(ClinicalPractice.numericPain(NursingScales.UNSET))
        assertNull(ClinicalPractice.numericPain(ClinicalPractice.NO_SELF_REPORT))
    }

    @Test fun numericPainRejectsAllOtherOutOfRangeValues() {
        listOf(-100, -2, -1, 11, 100, Int.MAX_VALUE).forEach {
            assertThrows(IllegalArgumentException::class.java) { ClinicalPractice.numericPain(it) }
        }
        (0..10).forEach { assertEquals(it, ClinicalPractice.numericPain(it)) }
    }

    @Test fun allThirtySixFourAtCombinationsUseTheOriginalWeights() {
        var count = 0
        for (a in listOf(0, 4)) for (b in 0..2) for (c in 0..2) for (d in listOf(0, 4)) {
            assertEquals(a + b + c + d, ClinicalPractice.fourAtTotal(listOf(a, b, c, d)))
            count++
        }
        assertEquals(36, count)
        assertEquals(12, ClinicalPractice.fourAtTotal(listOf(4, 2, 2, 4)))
        assertEquals(0, ClinicalPractice.fourAtTotal(listOf(0, 0, 0, 0)))
    }

    @Test fun untestableFourAtCognitiveItemsAreTwoPointsNotZeroOrGlasgowNV() {
        assertEquals(4, ClinicalPractice.fourAtTotal(listOf(0, 2, 2, 0)))
        assertTrue(ClinicalPractice.fourAt.items[1].choices.single { it.second == 2 }.first.contains("no valorable"))
        assertTrue(ClinicalPractice.fourAt.items[2].choices.single { it.second == 2 }.first.contains("No valorable"))
        assertThrows(IllegalArgumentException::class.java) { ClinicalPractice.fourAtTotal(listOf(0, -1, 2, 0)) }
    }

    @Test fun unselectedFourAtItemDoesNotGetImputedAsNormal() {
        (0..3).forEach { missing ->
            val scores = MutableList(4) { 0 }
            scores[missing] = NursingScales.UNSET
            assertNull(ClinicalPractice.fourAtTotal(scores))
        }
    }

    @Test fun invalidFourAtWeightsAndComponentCountsAreRejected() {
        listOf(listOf(1, 0, 0, 0), listOf(0, 3, 0, 0), listOf(0, 0, 3, 0), listOf(0, 0, 0, 1), listOf(0, 0, 0)).forEach {
            assertThrows(IllegalArgumentException::class.java) { ClinicalPractice.fourAtTotal(it) }
        }
    }

    @Test fun screeningBandsDoNotClaimDiagnosisOrRuleOutDelirium() {
        assertTrue(ClinicalPractice.fourAtMeaning(0).contains("NO los excluye"))
        for (score in 1..3) assertTrue(ClinicalPractice.fourAtMeaning(score).contains("Posible deterioro cognitivo"))
        for (score in 4..12) assertTrue(ClinicalPractice.fourAtMeaning(score).contains("no es un diagnóstico automático"))
        assertThrows(IllegalArgumentException::class.java) { ClinicalPractice.fourAtMeaning(-1) }
        assertThrows(IllegalArgumentException::class.java) { ClinicalPractice.fourAtMeaning(13) }
    }
}
