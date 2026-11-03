package com.idreyk.enfermeria360

import android.os.SystemClock
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlin.math.hypot

object HygieneLesson {
    val wash = listOf("Humedecer las manos", "Aplicar jabón", "Palmas", "Dorsos de las manos", "Espacios entre dedos", "Dorsos de dedos", "Pulgares", "Puntas de los dedos", "Enjuagar", "Secar con material de un solo uso", "Evitar recontaminar al cerrar el grifo", "Revisar el recorrido, no certificar limpieza")
    val rub = listOf("Aplicar producto según etiqueta", "Palmas", "Dorsos de manos", "Espacios entre dedos", "Dorsos de dedos", "Pulgares", "Puntas de los dedos", "Dejar secar; revisar el recorrido")
    fun stage(elapsed: Long, seconds: Int, count: Int): Int {
        require(seconds in listOf(20, 25, 30, 40, 50, 60) && count > 0 && elapsed >= 0)
        return ((elapsed.coerceAtMost(seconds * 1000L) * count) / (seconds * 1000L)).toInt().coerceAtMost(count - 1)
    }
    fun startOfStep(index: Int, seconds: Int, count: Int): Long {
        require(index in 0 until count)
        return (seconds * 1000L * index + count - 1) / count
    }
}

@Composable
private fun HygieneHands(running: Boolean, step: Int) {
    Box(Modifier.fillMaxWidth().testTag("hygiene-hands")) {
        Notice("Recurso visual pendiente: la animación de manos anterior fue retirada. Los pasos y el cronómetro siguen disponibles; no evalúan una técnica real.", true)
    }
}

@Composable
fun HygieneScreen(vm: AppViewModel, back: () -> Unit) {
    var mode by rememberSaveable { mutableIntStateOf(0) }
    var duration by rememberSaveable { mutableIntStateOf(50) }
    var elapsed by rememberSaveable { mutableLongStateOf(0L) }
    var running by remember { mutableStateOf(false) } // never resumes after process recreation
    var anchor by remember { mutableLongStateOf(0L) }
    var base by remember { mutableLongStateOf(0L) }
    val owner = LocalLifecycleOwner.current
    val total = duration * 1000L
    val reduced = LocalReduced.current
    val steps = if (mode == 0) HygieneLesson.wash else HygieneLesson.rub
    val stage = HygieneLesson.stage(elapsed, duration, steps.size)
    fun pause() {
        if (running) elapsed = (base + (SystemClock.elapsedRealtime() - anchor).coerceAtLeast(0)).coerceAtMost(total)
        running = false
    }
    fun reset() { running = false; elapsed = 0L }
    LaunchedEffect(running, duration) {
        if (running) while (running) {
            delay(if (reduced) 500L else 100L)
            elapsed = (base + (SystemClock.elapsedRealtime() - anchor).coerceAtLeast(0)).coerceAtMost(total)
            if (elapsed >= total) running = false
        }
    }
    DisposableEffect(owner, total) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_STOP) pause() }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer); pause() }
    }
    Page("Higiene en movimiento", "Recorrido temporal · recurso visual pendiente", back) {
        ChoiceField("Método del ejercicio", listOf("Agua y jabón · 40–60 s" to 0, "Fricción alcohólica · 20–30 s" to 1), mode, showPoints = false) {
            reset(); mode = it; duration = if (it == 0) 50 else 25
        }
        ChoiceField("Duración total didáctica", (if (mode == 0) listOf(40, 50, 60) else listOf(20, 25, 30)).map { "$it segundos" to it }, duration, showPoints = false) {
            reset(); duration = it
        }
        HygieneHands(running, stage)
        AppCard(Modifier.fillMaxWidth().testTag("hygiene-clock")) {
            Badge(if (running) "EN CURSO" else "EN PAUSA", Teal)
            Text("${elapsed / 1000} / $duration s", style = MaterialTheme.typography.headlineMedium)
            Text("Paso ${stage + 1} de ${steps.size} · ${steps[stage]}", style = MaterialTheme.typography.titleMedium)
            ProgressLine(elapsed.toFloat() / total)
        }
        Primary(if (running) "Pausar recorrido" else "Iniciar / continuar", {
            if (running) pause() else { base = elapsed; anchor = SystemClock.elapsedRealtime(); running = true }
        }, elapsed < total)
        Secondary("Reiniciar cronómetro", { reset() })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f)) { Secondary("Paso anterior", { pause(); elapsed = HygieneLesson.startOfStep(stage - 1, duration, steps.size) }, stage > 0) }
            Box(Modifier.weight(1f)) { Secondary("Paso siguiente", { pause(); elapsed = HygieneLesson.startOfStep(stage + 1, duration, steps.size) }, stage < steps.lastIndex) }
        }
        if (elapsed >= total) Notice("Recorrido temporal terminado. No acredita higiene eficaz ni autoriza un procedimiento.")
        if (reduced) Notice("Movimiento reducido: cronómetro y controles completos; recurso visual pendiente.")
        Notice("OMS/HUG: 40–60 s para el lavado completo y 20–30 s para la fricción completa. El reparto entre pasos es didáctico propio, no tiempos oficiales por maniobra. Usa el producto conforme a su etiqueta.", true)
        Text("La preparación quirúrgica depende del fabricante y protocolo; no tiene un tiempo universal en este simulador.", style = MaterialTheme.typography.bodySmall)
        vm.data.master.catalog?.topics?.find { it.id == "lavado-manos" }?.sources?.forEach { SourceCard(it) }
    }
}
