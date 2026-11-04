package com.idreyk.enfermeria360

import android.os.Looper
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

/** Component behaviour in Robolectric; not physical accessibility or frame-rate testing. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], qualifiers = "w360dp-h800dp")
@LooperMode(LooperMode.Mode.PAUSED)
class ClinicalVisualInterfaceTests {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()

    @Before fun ready() {
        lateinit var vm: AppViewModel
        ui.runOnIdle { vm = ViewModelProvider(ui.activity)[AppViewModel::class.java] }
        ui.waitUntil(30_000) { shadowOf(Looper.getMainLooper()).idle(); !vm.data.loading }
        ui.waitForIdle()
    }

    @Test fun illustrationCanBePausedAndReducedMotionAlwaysTakesPriority() {
        var reduced by mutableStateOf(false)
        ui.runOnUiThread {
            ui.activity.setContent { NursingTheme(false, reduced) { ClinicalAreaIllustration(ClinicalVisualLearning.general) } }
        }
        ui.waitForIdle()
        ui.onNodeWithContentDescription("Portapapeles con lupa y libro. Dibujo decorativo; no es una respuesta ni un instrumento clínico.").assertExists()
        ui.onNodeWithTag("escala-movimiento").assertIsEnabled().performClick()
        ui.onNodeWithText("Animar dibujo").assertExists()
        ui.onNodeWithTag("escala-movimiento").performClick()
        ui.onNodeWithText("Pausar dibujo").assertExists()
        ui.runOnIdle { reduced = true }
        ui.onNodeWithTag("escala-movimiento").assertIsNotEnabled()
        ui.onNodeWithText("Movimiento reducido: ilustración quieta.").assertExists()
    }

    @Test fun sourceOnlyOpensWhenUserExplicitlyClicksAndFailureIsReadable() {
        var opened = ""
        val handler = object : UriHandler { override fun openUri(uri: String) { opened = uri } }
        val reference = Reference("4AT guía", "https://www.the4at.com/userguide", "Fuente de prueba", "2026-11-03")
        ui.runOnUiThread {
            ui.activity.setContent {
                NursingTheme(false, true) { CompositionLocalProvider(LocalUriHandler provides handler) { SourceCard(reference) } }
            }
        }
        ui.waitForIdle()
        assertEquals("", opened)
        ui.onNodeWithText("Abrir fuente externa").performClick()
        assertEquals(reference.url, opened)

        val failing = object : UriHandler { override fun openUri(uri: String) { throw IllegalArgumentException("sin navegador") } }
        ui.runOnUiThread {
            ui.activity.setContent {
                NursingTheme(false, true) { CompositionLocalProvider(LocalUriHandler provides failing) { SourceCard(reference) } }
            }
        }
        ui.waitForIdle()
        ui.onNodeWithText("Abrir fuente externa").performClick()
        ui.onNodeWithText("No se pudo abrir. Puedes copiar la dirección mostrada.").assertExists()
    }

    @Test fun unsafeSourceNeverPresentsAnOpenButton() {
        ui.runOnUiThread { ui.activity.setContent { NursingTheme(false, true) { SourceCard(Reference("Prueba", "javascript:alert(1)")) } } }
        ui.waitForIdle()
        ui.onNodeWithText("javascript:alert(1)").assertExists()
        ui.onNodeWithText("Abrir fuente externa").assertDoesNotExist()
    }
}
