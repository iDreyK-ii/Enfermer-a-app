package com.idreyk.enfermeria360

import android.graphics.BitmapFactory
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.hypot

@Composable
fun AtlasLibraryScreen(vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    val catalog = vm.data.master.catalog
    Page("Atlas de dispositivos", "PNG interactivo · reconocimiento educativo", back) {
        Notice("Ilustraciones kawaii originales, no fotografías del fabricante. Revisión profesional pendiente. No son instrucciones de inserción ni acreditan competencia clínica.", true)
        catalog?.devices?.forEach { d ->
            ActionRow(d.name, if (d.image != null) "PNG · ${d.parts.count { it.visible }} puntos · autoevaluación" else "RECURSO VISUAL PENDIENTE",
                R.drawable.ic_stethoscope, if (d.image != null) Teal else Gold) { nav("dispositivo/${d.id}") }
        }
        if (catalog == null) Notice(vm.data.master.error ?: "Atlas no disponible.", true)
        Notice("Solicitud v2.2: ${catalog?.devices?.size ?: 0} fichas cargadas de 24 dispositivos. Las caritas son decorativas; las imágenes no representan lecturas ni sustituyen instrucciones del producto.")
    }
}

/** Decorative preview only. Identification and enlarged views must remain stationary. */
object AtlasMotionPolicy {
    fun allow(preview: Boolean, reduced: Boolean, zoom: Float, question: Boolean): Boolean =
        preview && !reduced && zoom == 1f && !question
}

/** Decodes a bounded local asset only, at half export size to limit memory on mobile. */
@Composable
private fun AtlasPhotoViewport(d: AtlasDevice, selected: Int, zoom: Float, labels: Boolean,
    question: Boolean = false, animatePreview: Boolean = false,
    onSelect: (Int) -> Unit = {}, onAvailability: (Boolean) -> Unit = {}) {
    val context = LocalContext.current
    val photo = remember(d.id, d.image) {
        runCatching {
            val file = requireNotNull(d.image)
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.assets.open(file).use { BitmapFactory.decodeStream(it, null, bounds) }
            require(bounds.outWidth == d.imageWidth && bounds.outHeight == d.imageHeight)
            require(bounds.outWidth in 200..3072 && bounds.outHeight in 200..3072)
            context.assets.open(file).use {
                requireNotNull(BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = 2 }))
            }.asImageBitmap()
        }.getOrNull()
    }
    LaunchedEffect(d.id, photo) { onAvailability(photo != null) }
    if (photo == null) {
        Box(Modifier.fillMaxWidth().testTag("atlas-pending")) {
            Notice("Recurso visual pendiente. ${d.pendingReason.ifBlank { "No se pudo cargar el PNG local; no se muestra un esquema sustituto." }}", true)
        }
        return
    }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val chosen = d.parts[selected]
    val target = if (question) Teal else Purple
    val reduced = LocalReduced.current
    val color = if (reduced) target else animateColorAsState(target, tween(180), label = "Marcador de atlas").value
    val moving = AtlasMotionPolicy.allow(animatePreview, reduced, zoom, question)
    val drift = if (moving) {
        val cycle = rememberInfiniteTransition(label = "Movimiento suave de la ilustración")
        cycle.animateFloat(-2f, 2f,
            animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "Desplazamiento decorativo").value
    } else 0f
    BoxWithConstraints(Modifier.fillMaxWidth().testTag("atlas-viewport")) {
        val fittedWidth = minOf(maxWidth, screenHeight * .40f * (d.imageWidth.toFloat() / d.imageHeight))
        val w = fittedWidth * zoom
        val h = w * (d.imageHeight.toFloat() / d.imageWidth)
        val scroll = rememberScrollState()
        LaunchedEffect(zoom) { if (zoom == 1f) scroll.scrollTo(0) }
        Box(Modifier.fillMaxWidth().horizontalScroll(scroll), contentAlignment = Alignment.Center) {
            // The bitmap, hit region and native marker share one transform, so they never drift apart.
            Box(Modifier.size(w, h).graphicsLayer { translationY = drift.dp.toPx() }.testTag("atlas-photo")
                .semantics { contentDescription = d.alt + if (question) " Punto de identificación señalado; responde con las opciones." else " Alternativa accesible: lista de partes inferior." }
                .pointerInput(d.id, zoom, question) {
                    if (!question) detectTapGestures { tap ->
                        val nearest = d.parts.indices.filter { d.parts[it].visible }.minByOrNull {
                            hypot(d.parts[it].x!! * size.width - tap.x, d.parts[it].y!! * size.height - tap.y)
                        }
                        if (nearest != null && hypot(d.parts[nearest].x!! * size.width - tap.x,
                                d.parts[nearest].y!! * size.height - tap.y) <= 48.dp.toPx()) onSelect(nearest)
                    }
                }) {
                Image(photo, null, Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
                // One label at a time avoids collisions. The native list supplies 48dp+ alternatives.
                if (labels && chosen.visible) {
                    Canvas(Modifier.fillMaxSize().clearAndSetSemantics { }) {
                        val point = Offset(chosen.x!! * size.width, chosen.y!! * size.height)
                        drawCircle(color.copy(alpha = .18f), 21.dp.toPx(), point)
                        drawCircle(color, 13.dp.toPx(), point)
                        drawCircle(Color.White, 13.dp.toPx(), point, style = Stroke(2.dp.toPx()))
                    }
                    Box(Modifier.offset(w * chosen.x!! - 13.dp, h * chosen.y!! - 13.dp).size(26.dp)
                        .clearAndSetSemantics { }, contentAlignment = Alignment.Center) {
                        Text(if (question) "?" else "${selected + 1}", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AtlasZoom(zoom: Float, change: (Float) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedButton({ change((zoom - .5f).coerceAtLeast(1f)) }, enabled = zoom > 1f) { Text("Alejar") }
        OutlinedButton({ change((zoom + .5f).coerceAtMost(2f)) }, enabled = zoom < 2f) { Text("Acercar") }
        OutlinedButton({ change(1f) }) { Text("Restablecer vista") }
    }
    Text("${zoom}× · Desliza la imagen ampliada", style = MaterialTheme.typography.labelMedium)
}

@Composable
private fun AtlasPartDetail(p: AtlasPart, index: Int) {
    AppCard(Modifier.fillMaxWidth().testTag("atlas-part-detail")) {
        Badge("PARTE ${index + 1} · ${p.status.uppercase()}", Teal)
        Text(p.name, style = MaterialTheme.typography.titleMedium)
        Text(p.function, style = MaterialTheme.typography.bodyMedium)
        if (!p.visible) Text("No visible: sin punto ni pregunta visual.", style = MaterialTheme.typography.labelMedium)
        Notice(p.safety, true)
    }
}

@Composable
fun AtlasDeviceScreen(id: String, vm: AppViewModel, back: () -> Unit) {
    val d = vm.data.master.catalog?.devices?.find { it.id == id }
    if (d == null) { Page("Atlas", back = back) { Notice("Dispositivo no disponible.", true) }; return }
    var imageReady by remember(id) { mutableStateOf(false) }
    var quiz by rememberSaveable(id) { mutableStateOf(false) }
    if (quiz && d.image != null) { AtlasQuizScreen(d, vm) { quiz = false }; return }
    var selected by rememberSaveable(id) { mutableIntStateOf(0) }
    var zoom by rememberSaveable(id) { mutableFloatStateOf(1f) }
    var labels by rememberSaveable(id) { mutableStateOf(true) }
    var animatePreview by rememberSaveable(id) { mutableStateOf(true) }
    val reduced = LocalReduced.current
    val selection = selected.coerceIn(d.parts.indices)
    val chosen = d.parts[selection]
    Page(d.name, "Atlas PNG · imagen educativa, no calibrada", back, { Favorite("dispositivo/$id", d.name, vm) }) {
        Text("Toca cerca de una parte o elige su nombre. Se muestra una etiqueta por vez para evitar solapamientos.", style = MaterialTheme.typography.bodySmall)
        if (d.image != null) {
            AtlasZoom(zoom) { zoom = it; animatePreview = false }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(labels, { labels = it; animatePreview = false }, Modifier.semantics { contentDescription = "Mostrar etiqueta seleccionada" })
                Text("Etiqueta seleccionada", style = MaterialTheme.typography.bodySmall)
            }
            OutlinedButton(onClick = { animatePreview = !animatePreview }, enabled = !reduced && zoom == 1f,
                modifier = Modifier.testTag("atlas-motion-toggle")) {
                Text(if (animatePreview && !reduced && zoom == 1f) "Pausar movimiento" else "Animar vista")
            }
            Text(if (reduced) "Movimiento desactivado por accesibilidad o rendimiento."
                else "Movimiento decorativo suave; se detiene al elegir una parte, ampliar o practicar.",
                style = MaterialTheme.typography.bodySmall)
        }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            if (maxWidth >= 600.dp && d.image != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top) {
                    Box(Modifier.weight(.62f)) { AtlasPhotoViewport(d, selection, zoom, labels, animatePreview = animatePreview,
                        onSelect = { selected = it; animatePreview = false }, onAvailability = { imageReady = it }) }
                    Box(Modifier.weight(.38f)) { AtlasPartDetail(chosen, selection) }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                    AtlasPhotoViewport(d, selection, zoom, labels, animatePreview = animatePreview,
                        onSelect = { selected = it; animatePreview = false }, onAvailability = { imageReady = it })
                    AtlasPartDetail(chosen, selection)
                }
            }
        }
        if (d.image != null) Primary("Practicar identificación", { animatePreview = false; quiz = true }, imageReady)
        Text("Selecciona una parte por su nombre", style = MaterialTheme.typography.titleSmall)
        d.parts.forEachIndexed { index, p ->
            ActionRow("${index + 1}. ${p.name}", if (!p.visible) "Sin punto visual · consultar descripción" else if (index == selection) "Seleccionada" else "Ver función y precaución",
                R.drawable.ic_info, if (index == selection) Purple else Teal) { selected = index; animatePreview = false }
        }
        Section("Modelo, variantes y precauciones")
        Notice(d.modelNote, true)
        d.checks.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
        Text("Estado: ${d.reviewStatus}\nRevisión documental/visual asistida: ${d.reviewDate}. Persona que aprueba precisión: pendiente. No es fotografía del fabricante.", style = MaterialTheme.typography.bodySmall)
        if (d.image != null) Text("Ilustración 2D original rasterizada directamente en PNG RGBA de ${d.imageWidth} × ${d.imageHeight}; no es fotografía ni detalle clínico a escala.", style = MaterialTheme.typography.bodySmall)
        d.sources.forEach { SourceCard(it) }
    }
}

@Composable
private fun AtlasQuizScreen(d: AtlasDevice, vm: AppViewModel, back: () -> Unit) {
    val master = vm.data.master.catalog ?: return
    var imageReady by remember(d.id) { mutableStateOf(false) }
    val key = "atlas/${d.id}"
    val version = master.progressVersion(key)
    val answers = vm.data.learning[key]?.takeIf { it.version == version }?.answers.orEmpty()
    var index by rememberSaveable(d.id) { mutableIntStateOf(answers.size.coerceAtMost(d.quizTargets.lastIndex)) }
    var zoom by rememberSaveable(d.id) { mutableFloatStateOf(1f) }
    var reset by remember { mutableStateOf(false) }
    val stage = index.coerceIn(0, answers.size.coerceAtMost(d.quizTargets.lastIndex))
    val target = d.quizTargets[stage]
    val answered = stage < answers.size
    Page("Identificar · ${d.name}", "Autoevaluación local · no competencia clínica", back) {
        Notice("Ilustración educativa con revisión profesional pendiente. Solo se preguntan partes visibles; la imagen permanece quieta. La respuesta se guarda localmente.", true)
        Text("Parte ${stage + 1} de ${d.quizTargets.size} · ¿Cómo se llama la parte señalada?", style = MaterialTheme.typography.titleMedium)
        AtlasZoom(zoom) { zoom = it }
        AtlasPhotoViewport(d, target, zoom, true, question = true, onAvailability = { imageReady = it })
        d.quizOptions(stage).forEachIndexed { letter, option ->
            AppCard(Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("atlas-answer-$option"), onClick = if (!answered && !vm.busy && imageReady) ({
                val updated = answers + option
                vm.saveLearning(LearningProgress(key, version, updated.size == d.quizTargets.size, updated))
            }) else null) {
                Text("${'A' + letter} · ${d.parts[option].name}")
                if (answered && answers[stage] == option) Badge("TU RESPUESTA", Purple)
                if (answered && option == target) Badge("IDENTIFICACIÓN", Teal)
            }
        }
        if (answered) {
            Notice((if (answers[stage] == target) "Correcto. " else "Repasa: ") + d.parts[target].name + ". " + d.parts[target].function)
            if (stage < d.quizTargets.lastIndex) Primary("Siguiente parte", { index = stage + 1; zoom = 1f }, !vm.busy)
        }
        if (answers.size == d.quizTargets.size) {
            Text("${d.score(answers)} de ${d.quizTargets.size} identificaciones correctas", style = MaterialTheme.typography.titleMedium)
            Text("Resultado contado desde tus respuestas; no certifica habilidades clínicas.", style = MaterialTheme.typography.bodySmall)
        }
        if (answers.isNotEmpty()) Secondary("Reiniciar identificación", { reset = true }, !vm.busy)
    }
    if (reset) AlertDialog(onDismissRequest = { reset = false }, title = { Text("¿Reiniciar identificación?") },
        text = { Text("Se reemplazan las respuestas de este dispositivo. El resultado anterior del historial se conserva.") },
        confirmButton = { TextButton(onClick = { reset = false; vm.saveLearning(LearningProgress(key, version, false)) { index = 0 } }) { Text("Reiniciar") } },
        dismissButton = { TextButton(onClick = { reset = false }) { Text("Conservar") } })
}
