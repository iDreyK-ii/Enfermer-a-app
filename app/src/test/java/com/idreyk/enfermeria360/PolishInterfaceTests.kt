package com.idreyk.enfermeria360

import android.os.Looper
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], qualifiers = "w360dp-h800dp")
@LooperMode(LooperMode.Mode.PAUSED)
class PolishInterfaceTests {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()

    @Before
    fun finishInitialLoad() {
        lateinit var vm: AppViewModel
        ui.runOnIdle { vm = ViewModelProvider(ui.activity)[AppViewModel::class.java] }
        ui.waitUntil(30_000) {
            shadowOf(Looper.getMainLooper()).idle()
            !vm.data.loading
        }
        ui.waitForIdle()
    }

    private fun mount(content: @Composable () -> Unit) {
        ui.runOnUiThread { ui.activity.setContent { NursingTheme(false, true) { content() } } }
        ui.waitForIdle()
    }

    @Test
    fun enlargedWelcomeUsesContentHeightInsteadOfClippingTheGreeting() {
        mount {
            CompositionLocalProvider(
                LocalDensity provides Density(LocalDensity.current.density, 2f)
            ) {
                Box(Modifier.width(300.dp)) { WelcomeHero("Nombre de QA", false) }
            }
        }
        ui.onNodeWithTag("welcome-art").assertDoesNotExist()
        val card = ui.onNodeWithTag("welcome-card").getUnclippedBoundsInRoot()
        val greeting = ui.onNodeWithTag("welcome-greeting").getUnclippedBoundsInRoot()
        assertTrue(card.bottom - card.top > 202.dp)
        assertTrue(greeting.bottom <= card.bottom)
        assertTrue(greeting.right <= card.right)
    }

    @Test
    fun moduleGridBecomesSingleColumnWithLargeTextAndStillNavigates() {
        var destination = ""
        mount {
            CompositionLocalProvider(
                LocalDensity provides Density(LocalDensity.current.density, 2f)
            ) {
                Box(Modifier.width(300.dp)) {
                    ModuleGrid(
                        listOf(
                            Module("Primero", "Ejemplo", "primero", R.drawable.ic_book, Purple),
                            Module("Segundo", "Ejemplo", "segundo", R.drawable.ic_book, Teal),
                        ),
                        { destination = it },
                        "test-grid",
                    )
                }
            }
        }
        val first = ui.onNodeWithTag("module-primero").getUnclippedBoundsInRoot()
        val second = ui.onNodeWithTag("module-segundo").getUnclippedBoundsInRoot()
        assertTrue(second.top >= first.bottom)
        ui.onNodeWithTag("module-segundo").performClick()
        assertEquals("segundo", destination)
    }

    @Test
    fun catalogueReplacementCannotBeConfirmedBeforeExplicitAcknowledgement() {
        val previous = PolishFixtures.pack(PolishFixtures.drug()).entries
        val next = PolishFixtures.pack(PolishFixtures.drug(text = ""))
        val preview = PolishFixtures.review(next, previous)
        var confirmed = false
        mount { CatalogImportSheet(preview, false, { confirmed = it }, {}) }
        ui.onNodeWithText("Confirmar importación local").performScrollTo().assertIsNotEnabled()
        ui.onNodeWithTag("import-replace-ack").performScrollTo().assertIsOff().performClick()
        ui.onNodeWithTag("import-replace-ack").assertIsOn()
        ui.onNodeWithText("Confirmar importación local")
            .performScrollTo()
            .assertIsEnabled()
            .performClick()
        assertTrue(confirmed)
    }

    @Test
    fun pendingWritesDisableInputsEvenWhenTheFieldWasEnabledLocally() {
        mount {
            CompositionLocalProvider(LocalFormEnabled provides false) {
                Input("Campo bloqueado", "Texto sin cambios", {}, enabled = true)
            }
        }
        ui.onNodeWithText("Campo bloqueado").assertIsNotEnabled()
    }

    @Test
    fun everyImportPageRemainsReachableWithoutRenderingHundredsOfCardsAtOnce() {
        val next =
            PolishFixtures.pack(*(1..13).map { PolishFixtures.drug("ficha-$it") }.toTypedArray())
        mount { CatalogImportSheet(PolishFixtures.review(next), false, {}, {}) }
        ui.onNodeWithText("Ficha ficticia ficha-13").assertDoesNotExist()
        ui.onNodeWithText("Siguiente").performScrollTo().performClick()
        ui.onNodeWithText("Ficha ficticia ficha-13").assertExists()
        ui.onNodeWithText("Anterior").performScrollTo().performClick()
        ui.onNodeWithText("Ficha ficticia ficha-1", substring = false).assertExists()
    }
}
