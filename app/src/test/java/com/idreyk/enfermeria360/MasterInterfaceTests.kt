package com.idreyk.enfermeria360

import android.os.Looper
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsActions
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@LooperMode(LooperMode.Mode.PAUSED)
class MasterInterfaceTests {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()
    private lateinit var vm: AppViewModel
    private fun await(condition: () -> Boolean) {
        ui.waitUntil(30_000) { shadowOf(Looper.getMainLooper()).idle(); !vm.busy && condition() }
        ui.waitForIdle()
    }
    @Before fun ready() {
        ui.runOnIdle { vm = ViewModelProvider(ui.activity)[AppViewModel::class.java] }
        await { !vm.data.loading }
        assertNotNull(vm.data.master.error, vm.data.master.catalog)
        if (!vm.welcomed) ui.onNodeWithText("Explorar la app").performClick()
        ui.runOnIdle { vm.updateReduced(true) }; ui.waitForIdle()
    }
    private fun click(s: String) { ui.onNodeWithText(s).performScrollTo().performClick() }
    private fun input(label: String, value: String) { ui.onNodeWithText(label).performScrollTo().performTextReplacement(value) }
    private fun choice(label: String, option: String) { click(label); ui.onNodeWithText(option).performClick() }
    private fun modules() { ui.onNodeWithText("Módulos").performClick() }
    private fun back() { ui.onNodeWithContentDescription("Volver").performClick() }

    @Test fun markdownLibraryFiltersAndPersistsExplicitReadMark() {
        click("Biblioteca temática")
        choice("Área", "PAE")
        choice("Nivel", "Inicial")
        click("PAE: datos, razonamiento y evaluación")
        ui.onNodeWithText("Fuente").assertExists() // actual Markdown table cell
        click("Marcar como leído")
        await { vm.data.learning["tema/pae"]?.completed == true }
        ui.onNodeWithText("Marcar lectura como pendiente").assertExists()
        back(); back()
        click("Tu avance real")
        ui.onNodeWithText("1 / 20 lecturas marcadas").assertExists()
    }
    @Test fun dripHasNoAssumedFactorAndEditingInvalidatesPriorResult() {
        click("Cálculos"); click("Goteo")
        input("Volumen de ejemplo", "500"); input("Tiempo de ejemplo", "4")
        ui.onNodeWithText("Calcular ejemplo").assertIsNotEnabled()
        input("Factor leído en el equipo ficticio", "20")
        click("Calcular ejemplo")
        ui.onNodeWithText("42 gotas/min").assertExists()
        input("Factor leído en el equipo ficticio", "0")
        ui.onNodeWithText("42 gotas/min").assertDoesNotExist()
        click("Calcular ejemplo")
        ui.onNodeWithText("Guardar práctica").assertDoesNotExist()
    }
    @Test fun weightExerciseNeedsExplicitAgeAndPerDayOrPerDoseBasis() {
        click("Cálculos"); click("Cantidad por peso")
        input("Cantidad por kg del enunciado", "2"); input("Peso del ejemplo", "3")
        ui.onNodeWithText("Calcular ejemplo").assertIsNotEnabled()
        input("Edad del ejemplo en días completos", "0")
        ui.onNodeWithText("Calcular ejemplo").assertIsNotEnabled()
        choice("Base de la cantidad del enunciado", "Por día")
        click("Calcular ejemplo")
        ui.onNodeWithText("6 mg por día").assertExists()
        ui.onNodeWithText("EJERCICIO EDUCATIVO").assertExists()
    }
    @Test fun nominalInventoryShowsAmbiguityWithoutAClinicalDose() {
        click("Farmacología"); click("Inventario nominal · Base 2.1")
        ui.onNode(hasSetTextAction()).performTextInput("altilice")
        ui.onNode(hasText("altilice") and !hasSetTextAction()).performScrollTo().performClick()
        ui.onNodeWithText("IDENTIFICADO").assertExists()
        ui.onNodeWithText("Transcripción ambigua", substring = true).assertExists()
        ui.onNodeWithText("No hay monografía validada", substring = true).assertExists()
    }
    @Test fun syringePngSelectsPistonIndependentlyFromPlungerAndZooms() {
        modules(); click("Atlas interactivo"); click("Jeringa")
        ui.onNodeWithTag("atlas-photo").assertExists()
        ui.onNodeWithTag("atlas-viewport").performScrollTo()
        ui.onNodeWithTag("atlas-photo").performTouchInput { click(Offset(width * .676f, height * .55f)) }
        ui.onNodeWithText("Pistón o sello").assertExists()
        ui.onNodeWithText("Sella y se desplaza dentro del cilindro.").assertExists()
        click("2. Émbolo: vástago móvil")
        ui.onNodeWithText("Émbolo: vástago móvil").assertExists()
        click("Acercar")
        ui.onNodeWithText("1.5×", substring = true).assertExists()
    }
    @Test fun atlasIdentificationScoresPersistsAndResumesWithoutMarkingClinicalCompetence() {
        modules(); click("Atlas interactivo"); click("Jeringa"); click("Practicar identificación")
        val d = vm.data.master.catalog!!.devices.single { it.id == "jeringa" }
        d.quizTargets.forEachIndexed { i, target ->
            ui.onNodeWithTag("atlas-answer-$target").performScrollTo().assertHasClickAction().performClick()
            await { vm.data.learning["atlas/jeringa"]?.answers?.size == i + 1 }
            assertNull("La confirmación no debe tapar el siguiente control", vm.message)
            if (i < d.quizTargets.lastIndex) click("Siguiente parte")
        }
        ui.onNodeWithText("5 de 5 identificaciones correctas").assertExists()
        assertTrue(vm.data.learning["atlas/jeringa"]!!.completed)
        back(); click("Practicar identificación")
        ui.onNodeWithText("5 de 5 identificaciones correctas").assertExists()
        click("Reiniciar identificación")
        ui.onNodeWithText("Conservar").performClick()
        assertEquals(5, vm.data.learning["atlas/jeringa"]!!.answers.size)
    }
    @Test fun rejectedAtlasImageHasAnExplicitPendingStateAndNoQuizOrVectorFallback() {
        modules(); click("Atlas interactivo"); click("Catéter venoso periférico")
        ui.onNodeWithTag("atlas-pending").assertExists()
        ui.onNodeWithTag("atlas-photo").assertDoesNotExist()
        ui.onNodeWithTag("atlas-vector").assertDoesNotExist()
        ui.onNodeWithText("Practicar identificación").assertDoesNotExist()
    }
    @Test fun atlasZoomCanResetAndLabelsCanBeHiddenWhileDescriptionsRemainAccessible() {
        modules(); click("Atlas interactivo"); click("Estetoscopio")
        click("Acercar"); click("Restablecer vista")
        ui.onNodeWithText("1.0×", substring = true).assertExists()
        ui.onNodeWithContentDescription("Mostrar etiqueta seleccionada").performScrollTo().performClick()
        click("8. Campana / cara posterior")
        ui.onNodeWithText("No visible: sin punto ni pregunta visual.").assertExists()
    }
    @Test fun hygienePausesResetsAndKeepsControlsWithReducedMotion() {
        modules(); click("Bioseguridad"); click("Higiene en movimiento")
        ui.onNodeWithText("0 / 50 s").assertExists()
        ui.onNodeWithText("Movimiento reducido:", substring = true).assertExists()
        click("Iniciar / continuar")
        ui.onNodeWithText("EN CURSO").assertExists()
        click("Pausar recorrido")
        ui.onNodeWithText("EN PAUSA").assertExists()
        click("Reiniciar cronómetro")
        ui.onNodeWithText("0 / 50 s").assertExists()
        choice("Método del ejercicio", "Fricción alcohólica · 20–30 s")
        ui.onNodeWithText("0 / 25 s").assertExists()
    }
    @Test fun officialAndOwnChecklistsHaveDistinctAuthorityAndCounts() {
        modules(); click("Bioseguridad"); click("Diez correctos · INSN San Borja")
        ui.onNodeWithText("LISTA INSTITUCIONAL CITADA").assertExists()
        ui.onNodeWithText("0 / 10 casillas marcadas", substring = true).assertExists()
        back(); click("Veinte controles educativos · iDreyK")
        ui.onNodeWithText("LISTA PROPIA · NO OFICIAL").assertExists()
        ui.onNodeWithText("0 / 20 casillas marcadas", substring = true).assertExists()
    }
    @Test fun paeSavesSubjectiveObjectiveSoapieAndSbarWithoutReplacingOldFields() {
        modules(); click("Cuaderno PAE"); click("Planes de Mario Quispe"); click("Crear plan PAE")
        input("Título del plan", "Plan sintético v21")
        input("Datos subjetivos ficticios", "Relato inventado QA")
        input("Datos objetivos ficticios", "Observación simulada QA")
        choice("Sección complementaria", "SOAPIE")
        input("SOAPIE · A análisis", "Razonamiento del estudiante QA")
        choice("Sección complementaria", "SBAR")
        input("SBAR · R solicitud al equipo", "Aclaración simulada QA")
        ui.onNodeWithText("Exportar ejercicio Markdown").assertExists()
        ui.onNodeWithText("Imprimir ejercicio / PDF").assertExists()
        click("Guardar plan PAE")
        await { vm.data.notebooks.any { it.title == "Plan sintético v21" } }
        val fields = vm.data.notebooks.single { it.title == "Plan sintético v21" }.fields
        assertEquals("Relato inventado QA", fields.getString("subjetivos"))
        assertEquals("Observación simulada QA", fields.getString("objetivos_datos"))
        assertEquals("Razonamiento del estudiante QA", fields.getString("soapie_a"))
        assertEquals("Aclaración simulada QA", fields.getString("sbar_r"))
        assertTrue(PLAN_FIELDS.keys.all { fields.has(it) })
    }
    @Test fun syntheticCaseUnlocksNextStageOnlyAfterPersistedAnswerAndResumes() {
        modules(); click("Casos progresivos"); click("Diabetes, ITU e hipertensión")
        ui.onNodeWithText("Siguiente etapa").assertDoesNotExist()
        click("A · Separar relato, medición y fuente; aclarar el momento")
        await { vm.data.learning["simulacion/diabetes-itu-hta"]?.answers?.size == 1 }
        // The persisted-result Snackbar overlays the bottom button on a compact viewport.
        // Dismiss its real accessibility action before sending the next touch (do not bypass the button).
        ui.onNode(SemanticsMatcher.keyIsDefined(SemanticsActions.Dismiss))
            .performSemanticsAction(SemanticsActions.Dismiss) { it() }
        ui.onNodeWithText("Siguiente etapa").assertIsEnabled()
        click("Siguiente etapa")
        ui.onNodeWithText("¿Puede la app completar esa información?").assertExists()
        back(); click("Diabetes, ITU e hipertensión")
        ui.onNodeWithText("Etapa 2 de 3").assertExists()
        assertEquals(listOf(0), vm.data.learning.getValue("simulacion/diabetes-itu-hta").answers)
    }
    @Test fun externalRassDistinguishesBlankFromZeroAndRequiresAdultAndSource() {
        click("Escalas"); click("RASS · registro externo")
        ui.onNodeWithText("Guardar puntaje externo").assertIsNotEnabled()
        click("Confirmo un ejemplo adulto, no pediátrico ni neonatal")
        input("Puntaje de la ficha externa", "0")
        ui.onNodeWithText("Guardar puntaje externo").assertIsNotEnabled()
        input("Fuente, versión y contexto de práctica", "Ficha autorizada ficticia QA; versión adulta")
        click("Guardar puntaje externo")
        await { vm.data.practices.any { it.type == "RASS · externo" } }
        assertTrue(vm.data.practices.first { it.type == "RASS · externo" }.summary.contains("Puntaje transcrito: 0"))
    }
    @Test fun globalOfflineSearchFindsDevicePartsAndNativeRecorderRoutes() {
        click("Busca un módulo, fármaco o recurso…")
        ui.onNode(hasSetTextAction()).performTextInput("pistón")
        ui.onNodeWithText("Jeringa").assertExists()
        ui.onNode(hasSetTextAction()).performTextReplacement("Norton")
        click("Norton · registro externo")
        ui.onNodeWithText("Norton · registro externo").assertExists()
        ui.onNodeWithText("Adulto · no administra ni interpreta el instrumento").assertExists()
    }
}
