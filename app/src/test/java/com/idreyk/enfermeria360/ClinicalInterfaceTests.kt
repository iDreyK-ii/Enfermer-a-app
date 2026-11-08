package com.idreyk.enfermeria360

import android.os.Looper
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import java.time.Duration
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

/** Native Compose navigation under Robolectric; not physical/clinical validation. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@LooperMode(LooperMode.Mode.PAUSED)
class ClinicalInterfaceTests {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()
    private lateinit var vm: AppViewModel

    private fun await(condition: () -> Boolean) {
        ui.waitUntil(30_000) { shadowOf(Looper.getMainLooper()).idle(); condition() }
        ui.waitForIdle()
    }

    @Before fun ready() {
        ui.runOnIdle { vm = ViewModelProvider(ui.activity)[AppViewModel::class.java] }
        await { !vm.data.loading }
        if (!vm.welcomed) ui.onNodeWithText("Explorar la app").performClick()
        ui.runOnIdle { vm.updateReduced(true) }
        ui.waitForIdle()
    }

    private fun click(text: String) { ui.onNodeWithText(text).performScrollTo().performClick() }
    private fun choice(field: String, answer: String) { click(field); ui.onNodeWithText(answer).performClick() }
    private fun scales() { click("Escalas") }
    private fun library() {
        scales()
        click("Explorar tests y escalas")
        await { ui.onAllNodesWithTag("ficha-glasgow").fetchSemanticsNodes().isNotEmpty() }
    }

    @Test fun neonatalFilterAndCapurroDeepLinkKeepTheCorrectVariant() {
        library()
        ui.onNodeWithTag("poblacion-neonatos").performScrollTo().performClick()
        ui.onNodeWithTag("ficha-glasgow").assertDoesNotExist()
        ui.onNodeWithTag("ficha-apgar").assertExists()
        ui.onNodeWithTag("ficha-capurro-a").performScrollTo().performClick()
        await { ui.onAllNodesWithText("Abrir registro de práctica").fetchSemanticsNodes().isNotEmpty() }
        click("Abrir registro de práctica")
        ui.onNodeWithText("Capurro A").assertIsSelected()
        ui.onNodeWithText("Guardar práctica de Capurro").assertIsNotEnabled()
    }

    @Test fun restrictedFacesCardDoesNotExposeAnInventedCalculator() {
        library()
        ui.onAllNodes(hasSetTextAction()).onFirst().performTextReplacement("FPS-R")
        ui.onNodeWithTag("ficha-fps-r").performScrollTo().performClick()
        await { ui.onAllNodesWithText("Permiso pendiente").fetchSemanticsNodes().isNotEmpty() }
        ui.onNodeWithText("Abrir práctica interactiva").assertDoesNotExist()
        ui.onNodeWithText("Abrir registro de práctica").assertDoesNotExist()
        ui.onNodeWithText("Esta ficha NO administra", substring = true).assertExists()
    }

    @Test fun accessTypeFilterAndResetWorkThroughTheNativeUi() {
        library()
        click("Tipo de acceso")
        ui.onNode(hasText("Solo registro") and hasAnyAncestor(isPopup())).performClick()
        ui.onNodeWithTag("ficha-norton").assertExists()
        ui.onNodeWithTag("ficha-apgar").assertDoesNotExist()
        ui.onAllNodes(hasSetTextAction()).onFirst().performTextReplacement("ningún resultado QA")
        ui.onNodeWithText("Sin coincidencias").assertExists()
        click("Limpiar filtros")
        ui.onNodeWithTag("ficha-apgar").assertExists()
    }

    @Test fun accessFilterCanBeToggledByTappingItsLabelAndClearedWithoutEmptyResults() {
        library()
        val filter = ui.onNodeWithTag("filtro-con-acceso").performScrollTo()
        filter.assertHeightIsAtLeast(48.dp)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            .assertTextContains("Solo con acceso: práctica, simulación o registro")
            .assertIsOff()
        // Tap on the text side of the row, not just the checkbox itself.
        filter.performTouchInput { click(Offset(width * .75f, height / 2f)) }
        filter.assertIsOn()
        ui.onNodeWithText("13 FICHAS EN ESTA VISTA").assertExists()
        ui.onNodeWithTag("ficha-norton").assertExists()
        click("Limpiar filtros")
        filter.assertIsOff()
        ui.onNodeWithText("191 FICHAS EN ESTA VISTA").assertExists()
        ui.onNodeWithText("Limpiar filtros").assertDoesNotExist()
    }

    @Test fun combinedFiltersCanBeResetTogetherWhileAResultRemains() {
        library()
        ui.onAllNodes(hasSetTextAction()).onFirst().performTextReplacement("Norton")
        ui.onNodeWithTag("poblacion-adultos").performScrollTo().performClick()
        click("Servicio hospitalario")
        ui.onNode(hasText("Medicina y hospitalización") and hasAnyAncestor(isPopup())).performClick()
        click("Tipo de acceso")
        ui.onNode(hasText("Solo registro") and hasAnyAncestor(isPopup())).performClick()
        ui.onNodeWithTag("filtro-con-acceso").performScrollTo().performClick()
        ui.onNodeWithTag("ficha-norton").assertExists()
        ui.onNodeWithText("1 FICHA EN ESTA VISTA").assertExists()
        click("Limpiar filtros")
        ui.onNodeWithTag("filtro-con-acceso").assertIsOff()
        ui.onNodeWithTag("poblacion-todas").assertIsSelected()
        ui.onNodeWithText("Todas las clasificaciones").assertExists()
        ui.onNodeWithText("Todos los tipos").assertExists()
        ui.onNodeWithText("191 FICHAS EN ESTA VISTA").assertExists()
    }

    @Test fun nortonCardLinksOnlyToTheExistingAdultExternalRecorder() {
        library()
        ui.onAllNodes(hasSetTextAction()).onFirst().performTextReplacement("Norton")
        ui.onNodeWithTag("ficha-norton").performScrollTo().performClick()
        await { ui.onAllNodesWithTag("clave-norton").fetchSemanticsNodes().isNotEmpty() }
        ui.onNodeWithTag("escala-arte").assertExists()
        ui.onNodeWithText("Abrir registro de práctica · adulto").performScrollTo().performClick()
        ui.onNodeWithText("Norton · registro externo").assertExists()
        ui.onNodeWithText("Confirmo un ejemplo adulto, no pediátrico ni neonatal").assertExists()
        ui.onNodeWithText("Guardar puntaje externo").assertIsNotEnabled()
        ui.onNodeWithTag("escala-arte").assertDoesNotExist()
    }

    @Test fun rassCardMakesItsAdultOnlyRecorderExplicit() {
        library()
        ui.onAllNodes(hasSetTextAction()).onFirst().performTextReplacement("RASS")
        ui.onNodeWithTag("ficha-rass").performScrollTo().performClick()
        await { ui.onAllNodesWithTag("clave-rass").fetchSemanticsNodes().isNotEmpty() }
        ui.onNodeWithText("Abrir registro de práctica · adulto").performScrollTo().performClick()
        ui.onNodeWithText("RASS · registro externo").assertExists()
        ui.onNodeWithText("Adulto · no administra ni interpreta el instrumento").assertExists()
        ui.onNodeWithText("Guardar puntaje externo").assertIsNotEnabled()
        ui.onNodeWithTag("escala-arte").assertDoesNotExist()
    }

    @Test fun numericPainStartsUnsetCanSaveZeroAndDoesNotSaveMissingSelfReport() {
        scales()
        click("Escala numérica del dolor")
        ui.onNodeWithText("Guardar autoinforme de práctica").assertIsNotEnabled()
        ui.onNodeWithTag("dolor-0").performScrollTo().performClick()
        ui.onNodeWithText("0 / 10").assertExists()
        click("Guardar autoinforme de práctica")
        await { vm.data.practices.any { it.type == "Dolor · escala numérica" } }
        assertTrue(vm.data.practices.first { it.type == "Dolor · escala numérica" }.summary.contains("0/10"))
        click("No puede autoinformar")
        ui.onNodeWithText("Sin autoinforme: sin puntaje").assertExists()
        ui.onNodeWithText("Guardar autoinforme de práctica").assertIsNotEnabled()
    }

    @Test fun numericPainChangingContextInvalidatesTheOldAnswer() {
        scales()
        click("Escala numérica del dolor")
        ui.onNodeWithTag("dolor-8").performScrollTo().performClick()
        ui.onNodeWithText("8 / 10").assertExists()
        choice("Contexto del ejemplo", "En movimiento")
        ui.onNodeWithText("8 / 10").assertDoesNotExist()
        ui.onNodeWithText("Guardar autoinforme de práctica").assertIsNotEnabled()
    }

    @Test fun fourAtUntestableItemsScoreTwoAndResetInvalidatesTotal() {
        scales()
        click("4AT")
        ui.onNodeWithText("Guardar práctica 4AT").assertIsNotEnabled()
        ui.onNodeWithTag("4at-adulto").performScrollTo().performClick()
        choice("1 · Estado de consciencia", "Normal, o somnolencia menor de 10 segundos al despertar y luego normal · 0")
        choice("2 · AMT4", "Dos o más errores / no valorable · 2")
        choice("3 · Atención", "No valorable por malestar, somnolencia o falta de atención · 2")
        choice("4 · Cambio agudo o curso fluctuante", "No · 0")
        ui.onNodeWithText("4 / 12").assertExists()
        ui.onNodeWithText("Posible delirium,", substring = true).assertExists()
        click("Guardar práctica 4AT")
        await { !vm.busy && vm.data.practices.any { it.type == "4AT · práctica" } }
        assertTrue(vm.data.practices.first { it.type == "4AT · práctica" }.summary.contains("0/2/2/0"))
        // A real pointer click must not hit the transient save Snackbar over the lower button.
        // Advance both virtual clocks (Compose and paused Android Looper), not wall-clock sleep.
        ui.mainClock.advanceTimeBy(6_000)
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(6))
        ui.mainClock.advanceTimeBy(1_000)
        ui.waitForIdle()
        ui.onNodeWithText("Resultado de práctica guardado, sin datos de pacientes").assertDoesNotExist()
        ui.onNodeWithText("Reiniciar 4AT").assertIsEnabled()
        click("Reiniciar 4AT")
        ui.onNodeWithText("4 / 12").assertDoesNotExist()
        ui.onNodeWithText("0 DE 4 COMPONENTES").assertExists()
        ui.onAllNodesWithText("Selecciona una respuesta").assertCountEquals(4)
        ui.onNodeWithText("Guardar práctica 4AT").assertIsNotEnabled()
    }

    @Test fun fourAtResetBeforeSavingClearsEveryComponentWithoutCreatingARecord() {
        scales()
        click("4AT")
        val previousRecords = vm.data.practices.size
        ui.onNodeWithTag("4at-adulto").performScrollTo().performClick()
        choice("1 · Estado de consciencia", "Claramente anormal · 4")
        choice("2 · AMT4", "Sin errores · 0")
        choice("3 · Atención", "Siete meses o más correctamente · 0")
        choice("4 · Cambio agudo o curso fluctuante", "No · 0")
        ui.onNodeWithText("4 / 12").assertExists()
        click("Reiniciar 4AT")
        ui.onNodeWithText("4 / 12").assertDoesNotExist()
        ui.onNodeWithText("0 DE 4 COMPONENTES").assertExists()
        ui.onAllNodesWithText("Selecciona una respuesta").assertCountEquals(4)
        ui.onNodeWithText("Guardar práctica 4AT").assertIsNotEnabled()
        assertEquals(previousRecords, vm.data.practices.size)
    }

    @Test fun fourAtPopulationAcknowledgementCanBeWithdrawnWithoutKeepingScores() {
        scales()
        click("4AT")
        ui.onNodeWithTag("4at-adulto").performScrollTo().performClick()
        choice("1 · Estado de consciencia", "Claramente anormal · 4")
        ui.onNodeWithText("Los componentes registrados ya alcanzan", substring = true).assertExists()
        ui.onNodeWithTag("4at-adulto").performScrollTo().performClick()
        ui.onNodeWithText("1 · Estado de consciencia").assertDoesNotExist()
        ui.onNodeWithText("Guardar práctica 4AT").assertIsNotEnabled()
    }
}
