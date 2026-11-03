package com.idreyk.enfermeria360

import android.content.Context
import android.graphics.BitmapFactory
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AtlasPngTests {
    private lateinit var context: Context
    private lateinit var master: MasterCatalog
    private lateinit var repo: ContentRepository
    @Before fun setup() {
        context = RuntimeEnvironment.getApplication()
        context.deleteDatabase("idreyk_local.db")
        master = MasterCatalog.load(context)
        repo = ContentRepository(context); repo.initialize()
    }
    @After fun close() { repo.close() }
    private fun source(): JSONObject = context.assets.open("base_maestra_mvp.json").bufferedReader().use { JSONObject(it.readText()) }
    private fun reject(block: () -> Unit) { assertThrows(IllegalArgumentException::class.java) { block() } }
    private fun altered(change: (JSONObject) -> Unit) {
        val j = source(); val list = j.getJSONArray("devices")
        val d = (0 until list.length()).map { list.getJSONObject(it) }.single { it.getString("id") == "jeringa" }
        change(d); reject { MasterCatalog.decode(j.toString()) }
    }
    @Test fun twentyFourDistinctDecodedPngsHaveRealAlphaAndHotspotsOnVisiblePixels() {
        val available = master.devices.filter { it.image != null }
        assertEquals(24, available.size); assertEquals(24, available.map { it.image }.distinct().size)
        available.forEach { d ->
            val b = context.assets.open(d.image!!).use { BitmapFactory.decodeStream(it) }!!
            try {
                assertEquals(2000, b.width); assertEquals(d.imageHeight, b.height); assertTrue(b.hasAlpha())
                assertEquals(0, b.getPixel(0, 0) ushr 24)
                assertEquals(0, b.getPixel(b.width - 1, b.height - 1) ushr 24)
                d.parts.filter { it.visible }.forEach { p ->
                    assertTrue("${d.id}/${p.id}: hotspot fuera de la pieza", (b.getPixel((p.x!! * b.width).toInt(), (p.y!! * b.height).toInt()) ushr 24) >= 128)
                }
            } finally { b.recycle() }
        }
    }
    @Test fun unknownOrTraversalImagePathsAndOversizedImagesAreRejected() {
        altered { it.put("file", "../../secreto.png") }
        altered { it.put("file", "https://example.com/image.png") }
        altered { it.put("image_width", 40000) }
        altered { it.put("image_height", 0) }
    }
    @Test fun outOfBoundsAndIncompleteHotspotsAreRejected() {
        altered { it.getJSONArray("parts").getJSONObject(0).put("hotspot_x", -.01) }
        altered { it.getJSONArray("parts").getJSONObject(0).put("hotspot_y", 1.01) }
        altered { it.getJSONArray("parts").getJSONObject(0).put("hotspot_y", JSONObject.NULL) }
        altered { it.getJSONArray("parts").getJSONObject(0).put("visible", false) }
    }
    @Test fun hiddenBellBladderAndFilterAreNeverQuizTargetsOrOptions() {
        master.devices.forEach { d ->
            d.quizTargets.forEachIndexed { i, target ->
                assertTrue(d.parts[target].visible)
                assertTrue(d.quizOptions(i).all { d.parts[it].visible })
                assertEquals(d.quizOptions(i).distinct().size, d.quizOptions(i).size)
                assertTrue(target in d.quizOptions(i))
            }
        }
        assertFalse(master.devices.single { it.id == "estetoscopio" }.parts.single { it.id == "campana" }.visible)
        assertFalse(master.devices.single { it.id == "tensiometro" }.parts.single { it.id == "vejiga" }.visible)
        assertFalse(master.devices.single { it.id == "equipo-iv" }.parts.single { it.id == "filtro" }.visible)
    }
    @Test fun formerlyRejectedDevicesAreNowDifferentExplicitKawaiiAssets() {
        val recovered = master.devices.filter { it.id in setOf("cateter-periferico", "bureta", "llave-tres-vias") }
        assertEquals(3, recovered.size)
        recovered.forEach { d ->
            assertNotNull(d.image); assertTrue(d.parts.count { it.visible } >= 4)
            assertTrue(d.quizTargets.isNotEmpty())
        }
        assertFalse(master.devices.single { it.id == "cateter-periferico" }.parts.any { it.id == "puerto" })
        assertEquals(3, master.devices.single { it.id == "llave-tres-vias" }.parts.count { it.id.startsWith("puerto-") })
    }
    @Test fun missingImageStillRequiresExplicitPendingState() {
        altered { d ->
            d.put("file", JSONObject.NULL); d.put("image_width", 0); d.put("image_height", 0)
            d.put("pending_reason", "")
        }
        altered { d ->
            d.put("file", JSONObject.NULL); d.put("image_width", 0); d.put("image_height", 0)
            d.put("pending_reason", "Pendiente") // Existing visible points must not survive a missing image.
        }
    }
    @Test fun decorativeMotionPolicyStopsForQuizZoomReducedAndPause() {
        assertTrue(AtlasMotionPolicy.allow(true, false, 1f, false))
        assertFalse(AtlasMotionPolicy.allow(false, false, 1f, false))
        assertFalse(AtlasMotionPolicy.allow(true, true, 1f, false))
        assertFalse(AtlasMotionPolicy.allow(true, false, 1.5f, false))
        assertFalse(AtlasMotionPolicy.allow(true, false, 1f, true))
    }
    @Test fun allTwentyFourQuizzesScoreActualChoicesAndRejectInvalidOrExtraAnswers() {
        master.devices.filter { it.image != null }.forEach { d ->
            assertEquals(0, d.score(emptyList()))
            assertEquals(d.quizTargets.size, d.score(d.quizTargets))
            assertEquals(0, d.score(d.quizTargets.indices.map { i -> d.quizOptions(i).first { it != d.quizTargets[i] } }))
            reject { d.score(listOf(-1)) }; reject { d.score(listOf(99)) }
            reject { d.score(d.quizTargets + 0) }
        }
    }
    @Test fun routeVersionCompletionAndUnknownDeviceAreGuarded() {
        reject { repo.saveLearning(LearningProgress("atlas/inexistente", "atlas-v22.1", false)) }
        reject { repo.saveLearning(LearningProgress("atlas/jeringa", master.version, false)) }
        reject { repo.saveLearning(LearningProgress("atlas/jeringa", "atlas-v22.1", true)) }
        val d = master.devices.single { it.id == "jeringa" }
        reject { repo.saveLearning(LearningProgress("atlas/jeringa", "atlas-v22.1", false, d.quizTargets)) }
    }
    @Test fun completedQuizPersistsAndCreatesOneHistoryEntryWithoutDestroyingOldProgress() {
        val topic = LearningProgress("tema/pae", "2.1-mvp.1", true)
        repo.saveLearning(topic); repo.toggleFavorite("dispositivo/jeringa")
        val before = repo.learningRecords()[topic.route]
        val d = master.devices.single { it.id == "jeringa" }
        val p = LearningProgress("atlas/jeringa", "atlas-v22.1", true, d.quizTargets)
        repo.saveLearning(p); repo.saveLearning(p)
        repo.close(); repo = ContentRepository(context); repo.initialize()
        assertEquals(p, repo.learningRecords()[p.route]); assertEquals(before, repo.learningRecords()[topic.route])
        assertEquals(1, repo.practices().count { it.type == "Identificación · Jeringa" })
        assertTrue(repo.favorites().contains("dispositivo/jeringa"))
    }
    @Test fun technicalNamesAndAliasesAreActuallySearchableOffline() {
        listOf("bránula", "catéter periférico corto", "catéter IV periférico").forEach { query ->
            assertTrue(query, master.search(query).any { it.route == "dispositivo/cateter-periferico" })
        }
        assertTrue(master.search("esfigmomanómetro").any { it.route == "dispositivo/tensiometro" })
    }
    @Test fun metadataNeverInventsExpertApprovalAndDisclosesDirectRasterization() {
        val j = source().getJSONArray("devices")
        for (i in 0 until j.length()) {
            val d = j.getJSONObject(i)
            assertTrue(d.isNull("accuracy_approved_by")); assertFalse(d.getBoolean("clinical_approved"))
            assertTrue(d.getString("review_status").contains("PENDIENTE"))
            if (!d.isNull("file")) {
                val processing = d.getJSONObject("processing")
                assertEquals(2000, processing.getJSONArray("native_dimensions").getInt(0)); assertFalse(processing.getBoolean("upscaled"))
                assertEquals(2000, processing.getJSONArray("export_dimensions").getInt(0))
            }
        }
    }
}
