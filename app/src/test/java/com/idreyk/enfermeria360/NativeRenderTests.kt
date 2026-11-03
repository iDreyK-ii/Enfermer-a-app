package com.idreyk.enfermeria360

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.os.Looper
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.security.MessageDigest
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

/** Real production Compose rendered through Robolectric native graphics, NOT a physical APK screenshot. */
abstract class NativeRenderHarness {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()
    protected lateinit var vm: AppViewModel
    @Before fun ready() {
        ui.runOnIdle { vm = ViewModelProvider(ui.activity)[AppViewModel::class.java] }
        ui.waitUntil(30_000) { shadowOf(Looper.getMainLooper()).idle(); !vm.data.loading }
        if (!vm.welcomed) ui.onNodeWithText("Explorar la app").performClick()
        ui.runOnIdle { vm.updateReduced(true); vm.updateDark(false) }
        ui.waitForIdle()
        assertNotNull(vm.data.master.error, vm.data.master.catalog)
    }
    protected fun click(text: String) { ui.onNodeWithText(text).performScrollTo().performClick() }
    protected fun modules() { ui.onNodeWithText("Módulos").performClick() }
    protected fun input(label: String, value: String) { ui.onNodeWithText(label).performScrollTo().performTextReplacement(value) }
    protected fun capture(name: String, expectedWidth: Int) {
        ui.waitForIdle()
        // PixelCopy needs a real display frame callback, absent in this headless Robolectric run.
        // Rasterize the actual Android content view through NATIVE Skia, not a recreated mock UI.
        val bitmap = ui.runOnIdle {
            val view = ui.activity.findViewById<View>(android.R.id.content)
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888).also {
                view.draw(Canvas(it))
            }
        }
        assertEquals(expectedWidth, bitmap.width)
        assertTrue("Capture height must be meaningful", bitmap.height >= 400)
        val samples = mutableSetOf<Int>()
        for (x in 0 until bitmap.width step 7) for (y in 0 until bitmap.height step 7) samples.add(bitmap.getPixel(x, y))
        assertTrue("A blank/flat frame is not screen evidence: ${samples.size} sampled colors", samples.size > 40)
        val dir = File(System.getProperty("idreyk.captureDir") ?: "build/native-captures").apply { mkdirs() }
        val file = File(dir, "$name.png")
        file.outputStream().use { assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        val sha = MessageDigest.getInstance("SHA-256").digest(file.readBytes()).joinToString("") { "%02x".format(it) }
        File(dir, "$name.json").writeText(JSONObject().put("file", file.name).put("sha256", sha)
            .put("width", bitmap.width).put("height", bitmap.height).put("sample_colors", samples.size)
            .put("render", "Robolectric 4.14.1 / NATIVE graphics / production Compose / Android framework API ${android.os.Build.VERSION.SDK_INT}")
            .put("capture_method", "Android content View.draw(Canvas(Bitmap)); native Skia software rasterization")
            .put("physical_device", false).put("apk_emulator", false).put("motion_reduced", true)
            .put("theme", if (vm.dark) "oscuro" else "claro")
            .put("app_version", BuildConfig.VERSION_NAME).put("clinical_validation", false).toString(2))
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w390dp-h844dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
class NativePhoneRenderTests : NativeRenderHarness() {
    @Test fun homePreservesNativePrototypeAndCapturesNewNavigation() {
        capture("01-inicio-390", 390)
        click("Biblioteca temática")
        capture("02-biblioteca-390", 390)
    }
    @Test fun syringeIsActualInteractiveVectorNotAnIllustrationOfAnUnbuiltScreen() {
        modules(); click("Atlas interactivo"); click("Jeringa")
        click("3. Pistón o sello")
        ui.onNodeWithTag("atlas-part-detail").performScrollTo()
        capture("03-atlas-jeringa-390", 390)
    }
    @Test fun decimalResultComesFromEnteredNumbers() {
        click("Cálculos"); click("Goteo")
        input("Volumen de ejemplo", "500"); input("Tiempo de ejemplo", "4"); input("Factor leído en el equipo ficticio", "20")
        click("Calcular ejemplo")
        ui.onNodeWithText("42 gotas/min").performScrollTo().assertIsDisplayed()
        capture("04-calculo-390", 390)
    }
    @Test fun pausedHygieneShowsRealNativeState() {
        modules(); click("Bioseguridad"); click("Higiene en movimiento")
        ui.onNodeWithText("0 / 50 s").assertExists()
        val clock = ui.onNodeWithTag("hygiene-clock")
        assertEquals(370f, clock.fetchSemanticsNode().boundsInRoot.right, .1f)
        assertEquals(350f, ui.onNodeWithTag("hygiene-hands").fetchSemanticsNode().boundsInRoot.width, .1f)
        capture("05-higiene-390", 390)
    }
    @Test fun caseAndPaeAreNativeEditableRoutes() {
        modules(); click("Casos progresivos"); click("Diabetes, ITU e hipertensión")
        capture("06-caso-390", 390)
        modules(); click("Cuaderno PAE"); click("Planes de Mario Quispe"); click("Crear plan PAE")
        input("Título del plan", "Ejercicio sintético de demostración")
        input("Datos subjetivos ficticios", "Relato inventado para comprobar el editor")
        ui.onNodeWithText("Datos objetivos ficticios").performScrollTo()
        capture("07-pae-390", 390)
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w800dp-h1280dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
class NativeTabletRenderTests : NativeRenderHarness() {
    @Test fun tabletHomeAndAtlasRemainFunctionalAtEightHundredDp() {
        capture("08-inicio-tablet-800", 800)
        modules(); click("Atlas interactivo"); click("Catéter venoso periférico")
        ui.onNodeWithTag("atlas-viewport").performScrollTo()
        val bounds = ui.onNodeWithTag("atlas-viewport").getUnclippedBoundsInRoot()
        val root = ui.onRoot().getUnclippedBoundsInRoot()
        assertTrue(bounds.left >= root.left && bounds.right <= root.right)
        capture("09-atlas-tablet-800", 800)
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w320dp-h640dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
class NativeCompactRenderTests : NativeRenderHarness() {
    @Test fun narrowPhoneSupportsDarkThemeAndAtlasControlsWithoutLosingNavigation() {
        ui.runOnIdle { vm.updateDark(true) }
        modules(); click("Atlas interactivo"); click("Llave de tres vías")
        ui.onNodeWithTag("atlas-viewport").performScrollTo()
        capture("10-atlas-compacto-320-oscuro", 320)
        click("3. Conexión lateral 1")
        ui.onNodeWithText("Conexión lateral 1").assertExists()
        ui.onNodeWithContentDescription("Volver").performClick()
        ui.onNodeWithText("Atlas de dispositivos").assertExists()
    }
}
