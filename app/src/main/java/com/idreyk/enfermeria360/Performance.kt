package com.idreyk.enfermeria360

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.database.ContentObserver
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/** Device hints, not a benchmark or a promise of a particular frame rate. */
data class DeviceSignals(
    val lowRam: Boolean = false,
    val totalRamBytes: Long = 0,
    val memoryPressure: Boolean = false,
    val powerSave: Boolean = false,
    val systemReduced: Boolean = false,
    val foreground: Boolean = true,
)

data class PerformanceDecision(val reduced: Boolean, val reason: String)

object PerformancePolicy {
    val modes = linkedMapOf("auto" to "Automático", "light" to "Ligero", "full" to "Completo")

    fun decide(mode: String, manualReduced: Boolean, hints: DeviceSignals): PerformanceDecision {
        val reason =
            when {
                !hints.foreground -> "Animación pausada fuera de primer plano"
                manualReduced || hints.systemReduced -> "Reducción de movimiento activada"
                hints.powerSave -> "Ahorro de batería de Android"
                hints.memoryPressure -> "Protección por presión de memoria"
                mode == "light" -> "Modo ligero elegido"
                mode != "full" &&
                    (hints.lowRam || hints.totalRamBytes <= 3L * 1024 * 1024 * 1024) ->
                    "Ajuste automático para recursos limitados"
                else -> null
            }
        return PerformanceDecision(reason != null, reason ?: "Transiciones e ilustración animadas")
    }
}

val LocalPerformance = staticCompositionLocalOf { PerformanceDecision(false, "Modo completo") }

@Composable
fun rememberDeviceSignals(lifecycle: Lifecycle): DeviceSignals {
    val context = LocalContext.current.applicationContext
    var signals by remember { mutableStateOf(DeviceSignals(lowRam = true)) }
    DisposableEffect(context, lifecycle) {
        val handler = Handler(Looper.getMainLooper())
        fun refresh() {
            val activity = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memory = ActivityManager.MemoryInfo()
            runCatching { activity?.getMemoryInfo(memory) }
            val power = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            signals =
                DeviceSignals(
                    lowRam = runCatching { activity?.isLowRamDevice ?: true }.getOrDefault(true),
                    totalRamBytes = memory.totalMem,
                    memoryPressure = memory.lowMemory,
                    powerSave = power?.isPowerSaveMode == true,
                    systemReduced =
                        runCatching {
                                Settings.Global.getFloat(
                                    context.contentResolver,
                                    Settings.Global.ANIMATOR_DURATION_SCALE,
                                    1f,
                                ) == 0f
                            }
                            .getOrDefault(true),
                    foreground = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED),
                )
        }
        val lifecycleObserver = LifecycleEventObserver { _, _ -> refresh() }
        val settingObserver =
            object : ContentObserver(handler) {
                override fun onChange(selfChange: Boolean) {
                    refresh()
                }
            }
        val receiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    refresh()
                }
            }
        val memoryObserver =
            object : ComponentCallbacks2 {
                override fun onConfigurationChanged(newConfig: Configuration) {
                    refresh()
                }

                override fun onLowMemory() {
                    signals = signals.copy(memoryPressure = true)
                }

                override fun onTrimMemory(level: Int) {
                    if (
                        level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW ||
                            level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL
                    ) {
                        signals = signals.copy(memoryPressure = true)
                    }
                }
            }
        lifecycle.addObserver(lifecycleObserver)
        context.registerComponentCallbacks(memoryObserver)
        context.contentResolver.registerContentObserver(
            Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE),
            false,
            settingObserver,
        )
        // This is a protected system-only broadcast; never read values from its extras.
        // On API 26–32 the platform overload avoids a synthetic permission dependency.
        val powerFilter = IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        if (Build.VERSION.SDK_INT >= 33) {
            context.registerReceiver(receiver, powerFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, powerFilter)
        }
        refresh()
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
            context.unregisterComponentCallbacks(memoryObserver)
            context.contentResolver.unregisterContentObserver(settingObserver)
            context.unregisterReceiver(receiver)
        }
    }
    return signals
}
