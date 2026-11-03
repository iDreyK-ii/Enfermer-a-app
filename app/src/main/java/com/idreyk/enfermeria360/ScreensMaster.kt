package com.idreyk.enfermeria360

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
private fun MissingMaster(vm: AppViewModel, back: () -> Unit) {
    Page("Biblioteca educativa", back = back) { Notice(vm.data.master.error ?: "Contenido educativo no disponible. Las demás funciones se conservan.", true) }
}

private fun inlineStudy(text: String) = buildAnnotatedString {
    var cursor = 0
    Regex("\\*\\*([^*]+)\\*\\*").findAll(text).forEach { match ->
        append(text.substring(cursor, match.range.first))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(match.groupValues[1]) }
        cursor = match.range.last + 1
    }
    append(text.substring(cursor))
}

@Composable
fun StudyMarkdownView(markdown: String) {
    val parsed = remember(markdown) { runCatching { StudyMarkdown.blocks(markdown) } }
    val blocks = parsed.getOrNull()
    if (blocks == null) { Notice(parsed.exceptionOrNull()?.message ?: "No se pudo mostrar Markdown. El texto original se conserva en el editor.", true); return }
    blocks.forEach { block ->
        when (block) {
            is StudyBlock.Heading -> Text(inlineStudy(block.text), style =
                if (block.level == 1) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium)
            is StudyBlock.Paragraph -> Text(inlineStudy(block.text), style = MaterialTheme.typography.bodyMedium)
            is StudyBlock.Bullet -> Text(inlineStudy(block.text), Modifier.padding(start = 8.dp), style = MaterialTheme.typography.bodyMedium)
            is StudyBlock.Quote -> Notice(block.text)
            is StudyBlock.Formula -> AppCard(Modifier.fillMaxWidth()) {
                Text(block.text, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodyMedium)
            }
            is StudyBlock.Table -> {
                Text("Tabla · desliza horizontalmente si hace falta", style = MaterialTheme.typography.labelSmall, color = LocalPalette.current.muted)
                Column(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    block.rows.forEachIndexed { i, row ->
                        Row {
                            row.forEach { cell ->
                                Box(Modifier.width(210.dp).border(1.dp, Purple.copy(alpha = .18f)).padding(10.dp)) {
                                    Text(inlineStudy(cell), fontWeight = if (i == 0) FontWeight.Bold else FontWeight.Normal,
                                        style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KnowledgeLibraryScreen(vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    val master = vm.data.master.catalog ?: return MissingMaster(vm, back)
    var query by rememberSaveable { mutableStateOf("") }
    var area by rememberSaveable { mutableIntStateOf(0) }
    var level by rememberSaveable { mutableIntStateOf(0) }
    val areas = listOf("Todas las áreas") + master.topics.map { it.area }.distinct().sorted()
    val levels = listOf("Todos los niveles", "Inicial", "Intermedio", "Avanzado")
    val matches = master.search(query, areas.getOrNull(area).takeUnless { area == 0 }.orEmpty(), levels.getOrNull(level).takeUnless { level == 0 }.orEmpty(), "Tema")
    Page("Biblioteca de enfermería", "Markdown local · por competencias, no por ciclos", back) {
        SearchBox(query, { query = it.take(160) }, "Texto, tema, etiqueta o procedimiento…")
        ChoiceField("Área", areas.mapIndexed { i, s -> s to i }, area, showPoints = false) { area = it }
        ChoiceField("Nivel", levels.mapIndexed { i, s -> s to i }, level, showPoints = false) { level = it }
        Text("${matches.size} temas · versión ${master.version}", style = MaterialTheme.typography.labelMedium)
        matches.forEach { item ->
            val read = vm.data.learning[item.route]?.let { it.version == master.version && it.completed } == true
            ActionRow(item.title, item.subtitle + if (read) " · Marcado como leído" else " · Pendiente", R.drawable.ic_book, if (read) Teal else Purple) { nav(item.route) }
        }
        if (matches.isEmpty()) EmptyState("Sin coincidencias", "Prueba otra combinación de texto, nivel y área.")
        Notice("Conjunto inicial de contenidos educativos. Fuente, versión y revisión se muestran en cada tema; no equivale a una biblioteca clínica exhaustiva.", true)
        Secondary("Mis apuntes Markdown", { nav("apuntes") })
    }
}

@Composable
fun KnowledgeTopicScreen(id: String, vm: AppViewModel, back: () -> Unit) {
    val master = vm.data.master.catalog ?: return MissingMaster(vm, back)
    val topic = master.topics.find { it.id == id } ?: return MissingMaster(vm, back)
    val key = "tema/$id"
    val read = vm.data.learning[key]?.let { it.version == master.version && it.completed } == true
    Page(topic.title, "${topic.area} · ${topic.level}", back, { Favorite(key, topic.title, vm) }) {
        Badge(topic.status, Gold)
        Text("${topic.author}\nVersión ${master.version} · ${master.date}", style = MaterialTheme.typography.bodySmall, color = LocalPalette.current.muted)
        Text("Etiquetas: ${topic.tags.joinToString(" · ")}", style = MaterialTheme.typography.labelSmall)
        StudyMarkdownView(topic.markdown)
        Primary(if (read) "Marcar lectura como pendiente" else "Marcar como leído", {
            vm.saveLearning(LearningProgress(key, master.version, !read))
        }, !vm.busy)
        Text("Marca personal de lectura; no acredita comprensión ni competencia clínica.", style = MaterialTheme.typography.bodySmall)
        Section("Fuentes y alcance de consulta")
        topic.sources.forEach { SourceCard(it) }
    }
}

@Composable
fun NominalInventoryScreen(vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    val master = vm.data.master.catalog ?: return MissingMaster(vm, back)
    var query by rememberSaveable { mutableStateOf("") }
    var kind by rememberSaveable { mutableIntStateOf(0) }
    var limit by rememberSaveable { mutableIntStateOf(30) }
    val kinds = listOf("Todos los tipos") + master.inventory.map { it.kind }.distinct().sorted()
    val byId = remember(master) { master.inventory.associateBy { "nominal/${it.id}" } }
    val matches = master.search(query, kind = "Inventario").filter { kind == 0 || byId[it.route]?.kind == kinds.getOrNull(kind) }
    Page("Inventario farmacológico", "Identificación nominal · no vademécum clínico validado", back) {
        Notice("${master.inventory.size} entradas nominales, no ${master.inventory.size} fármacos validados. Todas permanecen IDENTIFICADO. Incluye combinaciones, biológicos, soporte, nutrición y términos ambiguos.", true)
        SearchBox(query, { query = it.take(160); limit = 30 }, "Nombre, alias, sistema o relación editorial…")
        ChoiceField("Tipo de entrada", kinds.mapIndexed { i, s -> s to i }, kind, showPoints = false) { kind = it; limit = 30 }
        Text("${matches.size} coincidencias · clasificación editorial provisional", style = MaterialTheme.typography.labelMedium)
        matches.take(limit).forEach { item -> ActionRow(item.title, item.subtitle, R.drawable.ic_pill, Pink) { nav(item.route) } }
        if (matches.size > limit) Secondary("Mostrar 30 más", { limit += 30 })
        if (matches.isEmpty()) EmptyState("Sin coincidencias", "Prueba el nombre del principio activo o el término original.")
    }
}

@Composable
fun NominalDetailScreen(id: String, vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    val master = vm.data.master.catalog ?: return MissingMaster(vm, back)
    val item = master.inventory.find { it.id == id } ?: return MissingMaster(vm, back)
    Page(item.name, "${item.kind} · inventario nominal", back, { Favorite("nominal/$id", item.name, vm) }) {
        Badge(item.status, Gold)
        Notice("No hay monografía validada. Esta entrada no acredita indicación, registro DIGEMID, disponibilidad, stock ni revisión profesional. No proporciona dosis, preparación, compatibilidad o velocidad.", true)
        Section("Ubicación maestra editorial")
        Text(item.primary, style = MaterialTheme.typography.bodyMedium)
        if (item.aliases.isNotEmpty()) { Section("Nombres relacionados"); Text(item.aliases.joinToString(" · ")) }
        if (item.formulations.isNotEmpty()) {
            Section("Formulaciones citadas · NO verificadas")
            item.formulations.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
            Notice("Mención nominal, no presentación comercial confirmada. No se presume que sean intercambiables.", true)
        }
        item.notes.forEach { Notice(it, true) }
        if (item.relations.isNotEmpty()) {
            Section("Relaciones del material editorial")
            Text("No son indicaciones verificadas ni nuevas fichas maestras.", style = MaterialTheme.typography.bodySmall)
            item.relations.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
        }
        Section("Etapas futuras · todavía no alcanzadas")
        listOf("PENDIENTE-FICHA", "FUENTE-ENCONTRADA", "VALIDADO-DIGEMID / VALIDADO-GPC", "REVISIÓN-FARMACÉUTICA", "PUBLICABLE").forEach {
            Text("Pendiente: $it", style = MaterialTheme.typography.bodySmall, color = LocalPalette.current.muted)
        }
        Text("Procedencia: Base Maestra 2.1 · líneas ${item.lines.joinToString()}.\nImportación ${master.date} · ${master.version}\nHuella del original: ${master.sourceHash}", style = MaterialTheme.typography.bodySmall)
        val existing = vm.data.entries.firstOrNull { it.kind == "med" && it.id == item.existingId }
        if (existing != null) Secondary("Abrir ficha local existente · ${existing.revision}", { nav(existing.key) })
    }
}

@Composable
fun LearningProgressScreen(vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    val master = vm.data.master.catalog ?: return MissingMaster(vm, back)
    var clear by remember { mutableStateOf(false) }
    val progress = vm.data.learning.values.filter { it.version == master.progressVersion(it.route) }
    Page("Tu avance real", "Guardado local · no calificación clínica", back) {
        AppCard(Modifier.fillMaxWidth()) {
            Text("${master.topics.count { t -> progress.any { it.route == "tema/${t.id}" && it.completed } }} / ${master.topics.size} lecturas marcadas", style = MaterialTheme.typography.titleMedium)
            Text("${master.cases.count { c -> progress.any { it.route == "simulacion/${c.id}" && it.completed } }} / ${master.cases.size} casos terminados", style = MaterialTheme.typography.titleMedium)
            Text("${master.devices.count { d -> progress.any { it.route == "atlas/${d.id}" && it.completed } }} / ${master.devices.count { it.image != null }} identificaciones terminadas", style = MaterialTheme.typography.titleMedium)
            Text("${vm.data.favorites.size} favoritos · ${vm.data.practices.size} resultados en el historial visible", style = MaterialTheme.typography.bodySmall)
        }
        Notice("El avance procede de tus acciones, no de un porcentaje de desarrollo inventado. Marcar una lectura no acredita comprensión. No se sincroniza con una cuenta.")
        ActionRow("Continuar lecturas", "Abrir biblioteca por temas", R.drawable.ic_book, Purple) { nav("biblioteca") }
        ActionRow("Casos progresivos", "Retomar respuestas guardadas", R.drawable.ic_stethoscope, Teal) { nav("casos") }
        ActionRow("Estado de los módulos", "Funciones, límites y bloqueos", R.drawable.ic_grid, Purple) { nav("estado-modulos") }
        ActionRow("Historial de práctica", "Resultados guardados en este dispositivo", R.drawable.ic_clipboard, Teal) { nav("historial") }
        Secondary("Restablecer solo progreso", { clear = true }, !vm.busy && vm.data.learning.isNotEmpty())
    }
    if (clear) AlertDialog(onDismissRequest = { clear = false }, title = { Text("¿Restablecer el progreso?") },
        text = { Text("Se borran las marcas de lectura y respuestas de casos e identificaciones del atlas. Se conservan cuaderno, favoritos e historial. No se puede deshacer.") },
        confirmButton = { TextButton(onClick = { clear = false; vm.clearLearning() }) { Text("Restablecer") } },
        dismissButton = { TextButton(onClick = { clear = false }) { Text("Conservar") } })
}

@Composable
fun ModuleStatusScreen(vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    val master = vm.data.master.catalog ?: return MissingMaster(vm, back)
    Page("Estado de los módulos", "Alcance real · versión ${master.version}", back) {
        Notice("HECHO describe una función educativa, no aprobación clínica. PARCIAL conserva límites; los bloqueos no se sustituyen con datos o permisos inventados.")
        master.modules.forEach { module ->
            AppCard(Modifier.fillMaxWidth()) {
                Badge(module.status, if (module.status == "HECHO") Teal else Gold)
                Text(module.name, style = MaterialTheme.typography.titleMedium)
                Text(module.note, style = MaterialTheme.typography.bodySmall)
                if (module.route.isNotBlank()) Secondary(if (module.status == "BLOQUEADO-POR-VALIDACIÓN") "Ver información y límites" else "Abrir ${module.name}", { nav(module.route) })
            }
        }
    }
}

@Composable
fun SyntheticCaseScreen(id: String, vm: AppViewModel, back: () -> Unit) {
    val master = vm.data.master.catalog ?: return MissingMaster(vm, back)
    val case = master.cases.find { it.id == id } ?: return MissingMaster(vm, back)
    val key = "simulacion/$id"
    val answers = vm.data.learning[key]?.takeIf { it.version == master.version }?.answers.orEmpty()
    var index by rememberSaveable(id) { mutableIntStateOf(answers.size.coerceAtMost(case.questions.lastIndex)) }
    var reset by remember { mutableStateOf(false) }
    val stage = index.coerceIn(0, answers.size.coerceAtMost(case.questions.lastIndex))
    val question = case.questions[stage]
    val answered = stage < answers.size
    Page(case.title, "${case.area} · escenario sintético", back, { Favorite(key, case.title, vm) }) {
        Badge(case.status, Gold)
        Notice("Solo datos inventados para aprendizaje. No diagnostica, no prescribe y no reconstruye órdenes de historias reales.", true)
        Text(case.intro, style = MaterialTheme.typography.bodyMedium)
        Section("Etapa ${stage + 1} de ${case.questions.size}")
        AppCard(Modifier.fillMaxWidth()) { Text(question.scene); Text(question.prompt, style = MaterialTheme.typography.titleMedium) }
        question.options.forEachIndexed { option, text ->
            AppCard(Modifier.fillMaxWidth(), onClick = if (!answered && !vm.busy) ({
                val updated = answers + option
                vm.saveLearning(LearningProgress(key, master.version, updated.size == case.questions.size, updated))
            }) else null) {
                Text("${('A' + option)} · $text", style = MaterialTheme.typography.bodyMedium)
                if (answered && answers[stage] == option) Badge("TU RESPUESTA", Purple)
                if (answered && question.correct == option) Badge("RESPUESTA JUSTIFICADA", Teal)
            }
        }
        if (answered) {
            Notice(question.feedback)
            Text("Rúbrica: ${question.rubric}", style = MaterialTheme.typography.bodySmall)
            if (stage < case.questions.lastIndex) Primary("Siguiente etapa", { index = stage + 1 }, !vm.busy)
        }
        if (stage > 0) Secondary("Etapa anterior", { index = stage - 1 }, !vm.busy)
        if (answers.size == case.questions.size) AppCard(Modifier.fillMaxWidth()) {
            Badge("RECORRIDO COMPLETADO", Teal)
            Text("${case.score(answers)} de ${case.questions.size} respuestas correctas", style = MaterialTheme.typography.titleMedium)
            Text("Contadas desde tus elecciones. La rúbrica es educativa; no acredita competencia profesional.", style = MaterialTheme.typography.bodySmall)
        }
        if (answers.isNotEmpty()) Secondary("Reiniciar este caso", { reset = true }, !vm.busy)
        Section("Procedencia y límites")
        case.sources.forEach { SourceCard(it) }
    }
    if (reset) AlertDialog(onDismissRequest = { reset = false }, title = { Text("¿Reiniciar el recorrido?") },
        text = { Text("Se reemplazan las respuestas de este caso. El resultado anterior del historial se conserva.") },
        confirmButton = { TextButton(onClick = { reset = false; vm.saveLearning(LearningProgress(key, master.version, false)) { index = 0 } }) { Text("Reiniciar") } },
        dismissButton = { TextButton(onClick = { reset = false }) { Text("Conservar") } })
}

@Composable
fun BiosecurityScreen(vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    val master = vm.data.master.catalog ?: return MissingMaster(vm, back)
    Page("Bioseguridad", "Higiene, protección y listas con ámbito explícito", back) {
        ActionRow("Higiene en movimiento", "Lavado 40–60 s · fricción 20–30 s · pausa y modo reducido", R.drawable.ic_shield_check, Teal) { nav("higiene") }
        master.topics.filter { it.area == "Bioseguridad" }.forEach { t -> ActionRow(t.title, t.level, R.drawable.ic_book, Purple) { nav("tema/${t.id}") } }
        Section("Dos listas, sin confundir su autoría")
        master.checklists.forEach { l -> ActionRow(l.title, if (l.official) "Fuente institucional INSN-SB" else "Lista propia, no oficial", R.drawable.ic_clipboard_check, Teal) { nav("lista/${l.id}") } }
        Notice("La preparación quirúrgica no utiliza un tiempo universal del simulador. Consulta fabricante y protocolo institucional.", true)
    }
}

@Composable
fun EducationalChecklistScreen(id: String, vm: AppViewModel, back: () -> Unit) {
    val master = vm.data.master.catalog ?: return MissingMaster(vm, back)
    val list = master.checklists.find { it.id == id } ?: return MissingMaster(vm, back)
    var mask by rememberSaveable(id) { mutableIntStateOf(0) }
    Page(list.title, "${list.items.size} ítems · ejercicio educativo", back, { Favorite("lista/$id", list.title, vm) }) {
        Badge(if (list.official) "LISTA INSTITUCIONAL CITADA" else "LISTA PROPIA · NO OFICIAL", if (list.official) Purple else Gold)
        Notice(list.scope, true)
        list.items.forEachIndexed { i, item ->
            val checked = mask and (1 shl i) != 0
            Row(Modifier.fillMaxWidth().toggleable(checked, enabled = !vm.busy, role = Role.Checkbox) { mask = mask xor (1 shl i) }.padding(vertical = 5.dp),
                verticalAlignment = Alignment.Top) {
                Checkbox(checked, onCheckedChange = null)
                Text("${i + 1}. $item", Modifier.weight(1f).padding(top = 10.dp), style = MaterialTheme.typography.bodyMedium)
            }
        }
        val count = list.items.indices.count { mask and (1 shl it) != 0 }
        Text("$count / ${list.items.size} casillas marcadas · no es autorización de administración", style = MaterialTheme.typography.labelMedium)
        Primary("Guardar ejercicio de lista", { vm.practice(list.title, "EJERCICIO EDUCATIVO. $count/${list.items.size} ítems marcados. ${list.scope} No acredita administración segura.") }, !vm.busy)
        Secondary("Limpiar marcas", { mask = 0 }, !vm.busy)
        list.sources.forEach { SourceCard(it) }
    }
}
