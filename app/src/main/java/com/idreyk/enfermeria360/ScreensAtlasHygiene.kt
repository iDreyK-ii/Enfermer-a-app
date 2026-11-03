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

@Composable
fun AtlasLibraryScreen(vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    val catalog = vm.data.master.catalog
    Page("Atlas de dispositivos", "Vectores táctiles · reconocimiento educativo", back) {
        Notice("No es un tutorial de inserción ni un manual de fabricante. Los dibujos son genéricos y sus etiquetas son texto editable en los datos del proyecto.", true)
        catalog?.devices?.forEach { device ->
            ActionRow(device.name, "${device.parts.size} partes · tocar, ampliar y explorar", R.drawable.ic_stethoscope, Teal) { nav("dispositivo/${device.id}") }
        }
        if (catalog == null) Notice(vm.data.master.error ?: "Atlas no disponible.", true)
    }
}

@Composable
fun AtlasDeviceScreen(id: String, vm: AppViewModel, back: () -> Unit) {
    val device = vm.data.master.catalog?.devices?.find { it.id == id }
    if (device == null) { Page("Atlas", back = back) { Notice("Dispositivo no disponible.", true) }; return }
    var selected by rememberSaveable(id) { mutableIntStateOf(0) }
    var zoom by rememberSaveable(id) { mutableFloatStateOf(1f) }
    var labels by rememberSaveable(id) { mutableStateOf(true) }
    var layer by rememberSaveable(id) { mutableIntStateOf(0) }
    val reduced = LocalReduced.current
    val palette = LocalPalette.current
    val colors = device.parts.indices.map { i ->
        val target = if (i == selected) Purple else Teal.copy(alpha = .7f)
        if (reduced) target else animateColorAsState(target, tween(180), label = "Selección de parte $i").value
    }
    val chosen = device.parts[selected]
    Page(device.name, "Atlas vectorial · esquema no calibrado", back, { Favorite("dispositivo/$id", device.name, vm) }) {
        Text("Toca una parte o elige su nombre. Los números corresponden a la lista inferior.", style = MaterialTheme.typography.bodySmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { zoom = (zoom - .5f).coerceAtLeast(1f) }, enabled = zoom > 1f) { Text("Alejar") }
            Text("${zoom}×", style = MaterialTheme.typography.labelMedium)
            OutlinedButton(onClick = { zoom = (zoom + .5f).coerceAtMost(2f) }, enabled = zoom < 2f) { Text("Acercar") }
        }
        ChoiceField("Capa del dibujo", listOf("Conjunto" to 0, "Parte seleccionada" to 1), layer, showPoints = false) { layer = it }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(labels, { labels = it }, Modifier.semantics { contentDescription = "Mostrar números de las partes" })
            Text("Etiquetas numeradas", style = MaterialTheme.typography.bodySmall)
        }
        BoxWithConstraints(Modifier.fillMaxWidth().testTag("atlas-viewport")) {
            val diagramWidth = maxWidth * zoom
            val diagramHeight = diagramWidth * (380f / 600f)
            Box(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).background(palette.surface)) {
                Box(Modifier.size(diagramWidth, diagramHeight)) {
                    Canvas(Modifier.fillMaxSize().testTag("atlas-vector")
                        .semantics { contentDescription = "Esquema de ${device.name}; ${chosen.name} seleccionada. Alternativa accesible: lista de partes." }
                        .pointerInput(id, zoom, layer) {
                            detectTapGestures { tap ->
                                val x = tap.x / size.width * 600f
                                val y = tap.y / size.height * 380f
                                val nearest = device.parts.indices.filter { layer == 0 || it == selected }.minByOrNull {
                                    hypot(device.parts[it].x - x, device.parts[it].y - y)
                                }
                                if (nearest != null && hypot(device.parts[nearest].x - x, device.parts[nearest].y - y) < 90f) selected = nearest
                            }
                        }) {
                        scale(size.width / 600f, size.height / 380f, Offset.Zero) {
                            device.parts.forEachIndexed { index, part ->
                                if (layer == 0 || index == selected) {
                                    val color = colors[index]
                                    val stroke = if (index == selected) 4.5f else 2.5f
                                    part.shapes.forEach { shape ->
                                        val a = shape.points
                                        when (shape.kind) {
                                            "rect", "oval" -> {
                                                val top = Offset(a[0], a[1]); val box = Size(a[2] - a[0], a[3] - a[1])
                                                if (shape.kind == "rect") {
                                                    drawRect(color.copy(alpha = .09f), top, box)
                                                    drawRect(color, top, box, style = Stroke(stroke))
                                                } else {
                                                    drawOval(color.copy(alpha = .10f), top, box)
                                                    drawOval(color, top, box, style = Stroke(stroke))
                                                }
                                            }
                                            "line" -> drawLine(color, Offset(a[0], a[1]), Offset(a[2], a[3]), stroke)
                                            "poly" -> {
                                                val path = Path().apply { moveTo(a[0], a[1]); for (n in 2 until a.size step 2) lineTo(a[n], a[n + 1]) }
                                                drawPath(path, color, style = Stroke(stroke))
                                            }
                                        }
                                    }
                                    if (index == selected) drawCircle(Pink.copy(alpha = .2f), 24f, Offset(part.x, part.y))
                                }
                            }
                        }
                    }
                    if (labels) device.parts.forEachIndexed { index, part ->
                        if (layer == 0 || index == selected) Box(
                            Modifier.offset(x = diagramWidth * (part.x / 600f) - 14.dp, y = diagramHeight * (part.y / 380f) - 36.dp)
                                .size(28.dp).clip(CircleShape).background(if (index == selected) Purple else Teal)
                                .clearAndSetSemantics { }, contentAlignment = Alignment.Center,
                        ) { Text("${index + 1}", color = Color.White, style = MaterialTheme.typography.labelSmall) }
                    }
                }
            }
        }
        AppCard(Modifier.fillMaxWidth().testTag("atlas-part-detail")) {
            Badge("PARTE ${selected + 1} · ESQUEMA EDUCATIVO", Teal)
            Text(chosen.name, style = MaterialTheme.typography.titleMedium)
            Text(chosen.function, style = MaterialTheme.typography.bodyMedium)
            Notice(chosen.safety, true)
        }
        Text("Selecciona una parte por su nombre", style = MaterialTheme.typography.titleSmall)
        device.parts.forEachIndexed { index, part ->
            ActionRow("${index + 1}. ${part.name}", if (selected == index) "Seleccionada" else "Ver función y precaución", R.drawable.ic_info, if (selected == index) Purple else Teal) { selected = index }
        }
        Notice(device.modelNote, true)
        Text("Sin técnica invasiva autónoma. Geometría y nombres editables por el desarrollador en el asset; revisión de fabricante y experto pendiente.", style = MaterialTheme.typography.bodySmall)
        device.sources.forEach { SourceCard(it) }
    }
}

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
    val phase = if (running && !LocalReduced.current) {
        val transition = rememberInfiniteTransition(label = "Manos educativas")
        transition.animateFloat(0f, 1f, infiniteRepeatable(tween(1100), RepeatMode.Reverse), label = "Movimiento suave de manos").value
    } else .5f
    val surface = LocalPalette.current.surface
    Canvas(Modifier.fillMaxWidth().height(190.dp).background(surface).testTag("hygiene-hands")
        .semantics { contentDescription = "Ilustración esquemática de dos manos; el paso y su descripción se presentan como texto. No evalúa una técnica real." }) {
        val magnification = minOf(size.width / 400f, size.height / 220f)
        scale(magnification, magnification, Offset(size.width / 2, size.height / 2)) {
            val center = Offset(size.width / 2, size.height / 2 + 25f)
            for (hand in 0..1) {
                val x = center.x + if (hand == 0) -69f else 69f
                val angle = (phase - .5f) * (if (hand == 0) 12f else -12f)
                rotate(angle, Offset(x, center.y)) {
                    val tint = if ((step + hand) % 2 == 0) Purple else Teal
                    drawRoundRect(tint.copy(alpha = .13f), Offset(x - 37, center.y - 30), Size(74f, 85f), CornerRadius(22f))
                    drawRoundRect(tint, Offset(x - 37, center.y - 30), Size(74f, 85f), CornerRadius(22f), style = Stroke(3f))
                    for (finger in 0..3) {
                        val height = if (finger == 0 || finger == 3) 52f else 68f
                        drawRoundRect(tint.copy(alpha = .14f), Offset(x - 36 + finger * 19, center.y - 25 - height), Size(16f, height + 30), CornerRadius(9f))
                        drawRoundRect(tint, Offset(x - 36 + finger * 19, center.y - 25 - height), Size(16f, height + 30), CornerRadius(9f), style = Stroke(3f))
                    }
                    rotate(if (hand == 0) -32f else 32f, Offset(x, center.y)) {
                        drawRoundRect(tint, Offset(x + if (hand == 0) -52f else 34f, center.y - 8), Size(18f, 55f), CornerRadius(9f), style = Stroke(3f))
                    }
                }
            }
            for (bubble in 0..4) drawCircle(Teal.copy(alpha = .35f), 3f + bubble,
                Offset(center.x + (bubble - 2) * 15f, center.y - 110f - phase * 10f + bubble * 3f))
        }
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
    Page("Higiene en movimiento", "Animación educativa · no evaluación de técnica", back) {
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
        if (reduced) Notice("Movimiento reducido: ilustración estática, cronómetro y controles completos.")
        Notice("OMS/HUG: 40–60 s para el lavado completo y 20–30 s para la fricción completa. El reparto entre pasos y el dibujo son didácticos propios, no tiempos oficiales por maniobra. Usa el producto conforme a su etiqueta.", true)
        Text("La preparación quirúrgica depende del fabricante y protocolo; no tiene un tiempo universal en este simulador.", style = MaterialTheme.typography.bodySmall)
        vm.data.master.catalog?.topics?.find { it.id == "lavado-manos" }?.sources?.forEach { SourceCard(it) }
    }
}
