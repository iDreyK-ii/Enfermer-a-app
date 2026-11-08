package com.idreyk.enfermeria360

import java.io.File
import org.junit.Assert.*
import org.junit.Test

/** Educational scope and safe-link policies, not clinical or copyright certification. */
class ClinicalVisualLearningTests {
    private val catalog by lazy { ClinicalCatalog.parse(File("src/main/assets/${ClinicalCatalog.ASSET}").readText()) }

    @Test fun everyCatalogueAreaHasSymbolicArtAndAClearBoundary() {
        val catalogueAreas = catalog.instruments.map { it.area }.toSet()
        assertTrue(catalogueAreas.isNotEmpty())
        catalogueAreas.forEach { area ->
            val art = ClinicalVisualLearning.area(area)
            assertTrue("$area must resolve to a valid illustration asset",
                art.asset in setOf(
                    "escalas_art/escala_neuro.png",
                    "escalas_art/escala_dolor.png",
                    "escalas_art/escala_funcion.png",
                    "escalas_art/escala_general.png",
                    "escalas_art/escala_neonatal.png",
                    "escalas_art/escala_nutricion.png",
                    "escalas_art/escala_seguridad.png",
                ))
            assertTrue("$area needs an explanatory boundary", art.description.isNotBlank() && art.areaTip.isNotBlank())
        }
        assertEquals(7, (ClinicalVisualLearning.areas.values.map { it.asset } + ClinicalVisualLearning.general.asset).toSet().size)
        assertTrue(ClinicalVisualLearning.areas.values.all { it.description.isNotBlank() && it.areaTip.isNotBlank() })
        assertEquals(ClinicalVisualLearning.general, ClinicalVisualLearning.area("un área futura"))
    }

    @Test fun everyReadingKeyHasAnExistingSourceCitedOnItsOwnCard() {
        assertEquals(12, ClinicalVisualLearning.keys.size)
        ClinicalVisualLearning.keys.forEach { (id, key) ->
            val item = catalog.instruments.single { it.id == id }
            assertTrue("$id must cite ${key.sourceId}", item.sourceIds.contains(key.sourceId))
            assertTrue(catalog.sources.containsKey(key.sourceId))
            assertTrue(key.title.isNotBlank() && key.text.isNotBlank())
            assertEquals("2026-11-03", catalog.sources.getValue(key.sourceId).reference.accessed)
        }
    }

    @Test fun artworkNeverOverridesReducedMotionOrTheUsersPause() {
        assertTrue(ClinicalIllustrationMotion.allow(reduced = false, paused = false))
        assertFalse(ClinicalIllustrationMotion.allow(reduced = true, paused = false))
        assertFalse(ClinicalIllustrationMotion.allow(reduced = false, paused = true))
        assertFalse(ClinicalIllustrationMotion.allow(reduced = true, paused = true))
    }

    @Test fun onlyExplicitWellFormedHttpsLinksWithoutCredentialsCanOpen() {
        assertTrue(ExternalReferencePolicy.canOpen("https://www.the4at.com/userguide"))
        assertTrue(ExternalReferencePolicy.canOpen("HTTPS://example.org/document.pdf?q=1#section"))
        listOf("", "example.org", "http://example.org", "javascript:alert(1)", "file:///data/private",
            "intent://example.org", "https://user:pass@example.org/path", "https://", "https://a.example/with space",
            "https://example.org/\nnext", "https://example.org/" + "x".repeat(2100)).forEach {
            assertFalse(it, ExternalReferencePolicy.canOpen(it))
        }
    }
}
