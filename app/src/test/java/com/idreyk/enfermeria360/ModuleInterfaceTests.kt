package com.idreyk.enfermeria360

import android.os.Looper
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

/** Real Compose routes under a simulated framework, not physical-device UI approval. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@LooperMode(LooperMode.Mode.PAUSED)
class ModuleInterfaceTests {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()
    private lateinit var vm: AppViewModel

    private fun await(condition: () -> Boolean) {
        ui.waitUntil(30_000) {
            shadowOf(Looper.getMainLooper()).idle()
            condition()
        }
        ui.waitForIdle()
    }

    @Before
    fun ready() {
        ui.runOnIdle { vm = ViewModelProvider(ui.activity)[AppViewModel::class.java] }
        await { !vm.data.loading }
        if (!vm.welcomed) ui.onNodeWithText("Explorar la app").performClick()
        ui.runOnIdle { vm.updateReduced(true) }
        ui.waitForIdle()
    }

    private fun click(text: String) {
        ui.onNodeWithText(text).performScrollTo().performClick()
    }

    private fun modules() {
        ui.onNodeWithText("Módulos").performClick()
    }

    private fun input(label: String, text: String) {
        ui.onNodeWithText(label).performScrollTo().performTextReplacement(text)
    }

    private fun choice(label: String, option: String) {
        click(label)
        ui.onNodeWithText(option).performClick()
    }

    @Test
    fun capurroZeroSelectionsAreCompleteAndCanBeSaved() {
        click("Escalas")
        click("Capurro A y B")
        ui.onNodeWithText("Guardar práctica de Capurro").assertIsNotEnabled()
        choice("Forma de la oreja", "Sin incurvación · 0")
        choice("Glándula mamaria", "No palpable · 0")
        choice("Formación del pezón", "Apenas visible, sin areola · 0")
        choice("Textura cutánea", "Muy fina, gelatinosa · 0")
        choice("Pliegues plantares", "Ausentes · 0")
        ui.onNodeWithText("29 semanas + 1", substring = true).assertExists()
        click("Guardar práctica de Capurro")
        await { vm.data.practices.any { it.type == "Capurro B" } }
        assertTrue(vm.data.practices.first { it.type == "Capurro B" }.summary.contains("Suma 0"))
        click("Capurro A")
        ui.onNodeWithText("Guardar práctica de Capurro").assertIsNotEnabled()
    }

    @Test
    fun apgarChangingMinuteClearsPriorComponents() {
        click("Escalas")
        click("Apgar")
        choice("Frecuencia cardiaca", "100 latidos/min o más · 2")
        choice("Esfuerzo respiratorio", "Respiración eficaz, llanto vigoroso · 2")
        choice("Tono muscular", "Movimiento activo · 2")
        choice("Respuesta refleja", "Llanto, tos o estornudo vigoroso · 2")
        choice("Coloración", "Coloración rosada general · 2")
        ui.onNodeWithText("10 / 10").assertExists()
        choice("Minuto del registro de ejemplo", "5 min")
        ui.onNodeWithText("10 / 10").assertDoesNotExist()
        ui.onNodeWithText("Guardar puntaje de práctica").assertIsNotEnabled()
    }

    @Test
    fun doseRouteCalculatesAndInvalidatesStaleResultWhenEdited() {
        click("Herramientas")
        click("Dosis")
        input("Cantidad indicada en el ejercicio", "250")
        input("Cantidad disponible en la presentación", "500")
        input("Volumen de la presentación disponible", "5")
        click("Calcular ejercicio")
        val result = "≈ ${DoseMath.display(2.5)} mL teóricos"
        ui.onNodeWithText(result).assertExists()
        input("Cantidad indicada en el ejercicio", "500")
        ui.onNodeWithText(result).assertDoesNotExist()
        ui.onNodeWithText("Guardar ejercicio matemático").assertDoesNotExist()
    }

    @Test
    fun nativeMedicationEditorActuallySavesLocalContent() {
        click("Farmacología")
        ui.onNodeWithContentDescription("Crear medicamento").performClick()
        input("Nombre del medicamento", "Ficha QA sin información clínica")
        ui.onNodeWithTag("medication-fields-tabs").performScrollTo()
        click("Otros datos")
        click("+ Descripción general · 0 caracteres")
        input("Descripción general", "Texto ficticio de prueba de edición")
        click("Guardar ficha como borrador")
        await { vm.data.entries.any { it.name == "Ficha QA sin información clínica" } }
        val entry = vm.data.entries.single { it.name == "Ficha QA sin información clínica" }
        assertEquals("borrador", entry.revision)
        assertEquals("Texto ficticio de prueba de edición", entry.sections["descripcion"])
        ui.onNodeWithText("Farmacología").assertExists()
    }

    @Test
    fun nativePaeEditorStoresStructuredFieldsInsteadOfFourCheckboxes() {
        modules()
        click("Cuaderno PAE")
        click("Planes de Mario Quispe")
        click("Crear plan PAE")
        input("Título del plan", "Plan ficticio QA")
        input(PLAN_FIELDS.getValue("valoracion"), "Información inventada para aprender")
        input(PLAN_FIELDS.getValue("diagnostico"), "Problema ficticio escrito por el estudiante")
        click("Guardar plan PAE")
        await { vm.data.notebooks.any { it.title == "Plan ficticio QA" } }
        val plan = vm.data.notebooks.single { it.title == "Plan ficticio QA" }
        assertEquals("mario", plan.owner)
        assertEquals("plan", plan.kind)
        assertEquals(
            "Problema ficticio escrito por el estudiante",
            plan.fields.getString("diagnostico"),
        )
        ui.onNodeWithText("Plan ficticio QA").assertExists()
    }

    @Test
    fun nativeStudyNoteAndProtocolEditorsAreReachableAndSave() {
        modules()
        click("Cuaderno PAE")
        click("Mis apuntes")
        click("Nuevo apunte")
        input("Título", "Apunte QA")
        input("Contenido del apunte", "Contenido propio ficticio")
        click("Guardar apunte")
        await { vm.data.notebooks.any { it.title == "Apunte QA" } }
        modules()
        click("Protocolos")
        click("Crear protocolo local")
        input("Título", "Protocolo de formato QA")
        input(
            "Contenido / procedimiento autorizado",
            "Texto de prueba; no contiene un procedimiento clínico.",
        )
        click("Guardar ficha como borrador")
        try {
            await {
                vm.data.entries.any {
                    it.kind == "protocol" && it.name == "Protocolo de formato QA"
                }
            }
        } catch (error: AssertionError) {
            throw AssertionError(
                "Protocol save: busy=${vm.busy}, message=${vm.message}, entries=${vm.data.entries.map{it.name}}; UI=${ui.onRoot().printToString()}",
                error,
            )
        }
        ui.onNodeWithText("Protocolo de formato QA").assertExists()
    }

    @Test
    fun multimediaRouteHasRealImportEntryPointAndExplicitExternalViewer() {
        modules()
        click("Multimedia")
        ui.onNodeWithText("Multimedia y documentos").assertExists()
        ui.onNodeWithText("Añadir archivo local").assertIsEnabled()
        ui.onNodeWithText("Los archivos se abren con una aplicación compatible", substring = true)
            .assertExists()
    }

    @Test
    fun cloudScreenNeverPretendsToBeConnectedWithoutConfiguration() {
        click("Actualizar biblioteca")
        ui.onNodeWithText("NUBE SIN CONFIGURAR").assertExists()
        ui.onNodeWithText("Buscar actualización de contenido").assertIsNotEnabled()
        ui.onNodeWithText("Enviar código de acceso").performScrollTo().assertIsNotEnabled()
        assertNull(vm.cloudSession)
    }

    @Test
    fun performanceChoicePersistsWithoutOverridingReducedMotion() {
        ui.onNodeWithText("Perfil").performClick()
        ui.onNodeWithTag("performance-options").performScrollTo()
        click("Ligero")
        ui.runOnIdle {
            assertEquals("light", vm.performanceMode)
            assertEquals(
                "light",
                vm.getApplication<android.app.Application>()
                    .getSharedPreferences("preferences", 0)
                    .getString("performance", ""),
            )
        }
        click("Completo")
        ui.runOnIdle {
            assertEquals("full", vm.performanceMode)
            assertTrue(vm.reduced)
        }
        ui.onNodeWithText("Reducción de movimiento activada").assertExists()
    }

    @Test
    fun backingOutOfAMedicationDraftKeepsTextUntilDiscardIsExplicit() {
        click("Farmacología")
        ui.onNodeWithContentDescription("Crear medicamento").performClick()
        input("Nombre del medicamento", "Borrador parcial de QA")
        ui.runOnIdle { ui.activity.onBackPressedDispatcher.onBackPressed() }
        ui.onNodeWithText("¿Salir sin guardar?").assertExists()
        ui.onNodeWithText("Seguir editando").performClick()
        ui.onNodeWithText("Nombre del medicamento").assertTextContains("Borrador parcial de QA")
        ui.onNodeWithContentDescription("Volver").performClick()
        ui.onNodeWithText("Descartar cambios").performClick()
        ui.onNodeWithText("Farmacología").assertExists()
        assertFalse(vm.data.entries.any { it.name == "Borrador parcial de QA" })
    }

    @Test
    fun patientRoutesKeepTheModulesTabSelected() {
        modules()
        click("Pacientes")
        ui.onNode(hasText("Módulos") and isSelectable()).assertIsSelected()
    }
}
