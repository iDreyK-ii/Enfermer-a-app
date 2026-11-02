package com.idreyk.enfermeria360

import android.content.Context
import android.os.Looper
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

/** Compose semantics tests in Robolectric, not device certification or visual approval. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@LooperMode(LooperMode.Mode.PAUSED)
class InterfaceTests {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()
    private lateinit var vm: AppViewModel

    @Before
    fun loadLocalLibrary() {
        ui.runOnIdle { vm = ViewModelProvider(ui.activity)[AppViewModel::class.java] }
        ui.waitUntil(30_000) {
            shadowOf(Looper.getMainLooper()).idle()
            !vm.data.loading
        }
        if (!vm.welcomed) ui.onNodeWithText("Explorar la app").performClick()
        ui.runOnIdle { vm.updateReduced(true) }
        ui.waitForIdle()
    }

    @Test
    fun homeOpensNativePharmacologyAndPreparedFields() {
        ui.onNodeWithText("Farmacología").performScrollTo().performClick()
        ui.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Paracetamol"))
        ui.onNodeWithText("Paracetamol").performClick()
        ui.onNodeWithText("Ficha técnica").performScrollTo().performClick()
        ui.onNodeWithText("4. Mecanismo de acción").performScrollTo().assertExists()
    }

    @Test
    fun nativeProfileWritesLocalThemePreference() {
        ui.onNodeWithText("Perfil").performClick()
        ui.onNodeWithText("Modo oscuro").assertExists()
        val previous = vm.dark
        ui.onAllNodes(isToggleable())[0].performScrollTo().performClick()
        ui.runOnIdle {
            assertTrue(vm.dark != previous)
            // Read the Application owned by AndroidViewModel, matching the storage scope used by
            // the app.
            assertTrue(
                vm.getApplication<android.app.Application>()
                    .getSharedPreferences("preferences", Context.MODE_PRIVATE)
                    .getBoolean("dark", false) == vm.dark
            )
        }
    }
}
