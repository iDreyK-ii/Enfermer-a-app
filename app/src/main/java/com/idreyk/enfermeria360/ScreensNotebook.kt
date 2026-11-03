package com.idreyk.enfermeria360

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import java.util.UUID
import org.json.JSONObject

fun newLocalId(): String = "local-" + UUID.randomUUID().toString()

fun newEntryRoute(kind: String): String = "editar/$kind/${newLocalId()}"

@Composable
fun DiscardDialog(onDiscard: () -> Unit, onStay: () -> Unit) {
    AlertDialog(
        onDismissRequest = onStay,
        title = { Text("¿Salir sin guardar?") },
        text = { Text("Los cambios del formulario todavía no están guardados en el dispositivo.") },
        confirmButton = { TextButton(onClick = onDiscard) { Text("Descartar cambios") } },
        dismissButton = { TextButton(onClick = onStay) { Text("Seguir editando") } },
    )
}

@Composable
fun CarePlansScreen(owner: String, vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    val patient = DemoPatients.firstOrNull { it.id == owner } ?: DemoPatients.first()
    val plans = vm.data.notebooks.filter { it.kind == "plan" && it.owner == patient.id }
    Page(
        "Planes PAE · ${patient.name.substringBefore(' ')}",
        "Documentos estructurados para la ficha ficticia",
        back,
    ) {
        Notice(
            "Organiza las cinco etapas con texto propio. No se generan diagnósticos ni se incluyen catálogos NANDA-I, NOC o NIC. No usar como historia clínica real.",
            true,
        )
        Primary(
            "Crear plan PAE",
            { nav("editar-plan/${patient.id}/${newLocalId()}") },
            !vm.busy,
            R.drawable.ic_plus,
        )
        plans.forEach { record ->
            AppCard(
                Modifier.fillMaxWidth(),
                onClick = { nav("editar-plan/${patient.id}/${record.id}") },
            ) {
                Badge(record.fields.optString("estado").replace('_', ' ').uppercase(), Teal)
                Text(record.title, style = MaterialTheme.typography.titleMedium)
                val complete = PLAN_FIELDS.keys.count { record.fields.optString(it).isNotBlank() }
                Text(
                    "$complete / ${PLAN_FIELDS.size} campos con texto · ${readableTime(record.modified)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalPalette.current.muted,
                )
                ProgressLine(complete.toFloat() / PLAN_FIELDS.size)
                Text("Abrir y editar", style = MaterialTheme.typography.labelMedium, color = Purple)
            }
        }
        if (plans.isEmpty())
            EmptyState(
                "Tu primer plan estructurado",
                "Incluye valoración, problema en texto libre, resultados esperados, planificación, ejecución y evaluación.",
            )
        val old = vm.data.notes.firstOrNull { it.patientId == "plan:${patient.id}" }
        if (old != null)
            Expandable(
                "Recorrido de aprendizaje conservado de v0.2",
                "El checklist anterior permanece guardado: ${old.text.count {it=='1'}} de 4 tareas del recorrido. No se ha convertido en un plan clínico ni se ha eliminado.",
            )
        ActionRow(
            "Respaldar cuaderno",
            "Exporta planes y apuntes en JSON",
            R.drawable.ic_folder,
            Purple,
        ) {
            nav("cuaderno")
        }
    }
}

@Composable
fun PlanEditorScreen(owner: String, id: String, vm: AppViewModel, back: () -> Unit) {
    val original = vm.data.notebooks.find { it.id == id }
    val initial =
        original?.fields?.toString()
            ?: JSONObject()
                .apply {
                    PLAN_FIELDS.keys.forEach { put(it, "") }
                    put("estado", "borrador")
                }
                .toString()
    var title by rememberSaveable(id, original?.modified) { mutableStateOf(original?.title ?: "") }
    var raw by rememberSaveable(id, original?.modified) { mutableStateOf(initial) }
    var error by remember { mutableStateOf("") }
    var discard by remember { mutableStateOf(false) }
    var delete by remember { mutableStateOf(false) }
    val fields = JSONObject(raw)
    var extraSection by rememberSaveable(id) { mutableIntStateOf(0) }
    val dirty = title != (original?.title ?: "") || raw != initial
    val leave: () -> Unit = { if (dirty) discard = true else back() }
    BackHandler(dirty && !vm.busy) { discard = true }
    Page(
        "Editor de plan PAE",
        "Ficha ficticia · ${DemoPatients.firstOrNull {it.id==owner}?.name.orEmpty()}",
        leave,
    ) {
        Notice(
            "Escritura libre para practicar el proceso enfermero. Ningún texto se convierte automáticamente en una recomendación o contenido validado.",
            true,
        )
        Input("Título del plan", title, { title = it.take(140) })
        ChoiceField(
            "Estado del documento",
            listOf("Borrador" to 0, "En práctica" to 1, "Cerrado" to 2),
            NotebookCodec.statuses.indexOf(fields.optString("estado")),
            showPoints = false,
        ) { n ->
            raw = JSONObject(raw).put("estado", NotebookCodec.statuses[n]).toString()
        }
        PLAN_FIELDS.forEach { (key, label) ->
            Input(
                label,
                fields.optString(key),
                {
                    raw = JSONObject(raw).put(key, it.take(4000)).toString()
                    error = ""
                },
                multi = true,
            )
        }

        Section("Valoración y comunicación estructuradas")
        Notice("Datos solo ficticios. ABCDE no dirige una reanimación. NANDA-I/NOC/NIC: texto propio, sin taxonomías licenciadas ni sugerencias automáticas.", true)
        val sections = EXTRA_PLAN_SECTIONS.keys.toList()
        ChoiceField("Sección complementaria", sections.mapIndexed { i, name -> name to i }, extraSection, showPoints = false) { extraSection = it }
        EXTRA_PLAN_SECTIONS.getValue(sections[extraSection]).forEach { (key, label) ->
            Input(label, fields.optString(key), {
                raw = JSONObject(raw).put(key, it.take(4000)).toString()
                error = ""
            }, multi = true)
        }
        if (error.isNotBlank()) Notice(error, true)
        Primary(
            "Guardar plan PAE",
            {
                try {
                    val record =
                        NotebookCodec.record(
                            NotebookRecord(
                                    id,
                                    "plan",
                                    owner,
                                    title,
                                    JSONObject(raw),
                                    System.currentTimeMillis(),
                                )
                                .json()
                        )
                    vm.saveNotebook(record, back)
                } catch (e: IllegalArgumentException) {
                    error = e.message.orEmpty()
                }
            },
            title.isNotBlank() && !vm.busy,
        )
        Text(
            "Guardar conserva un borrador parcial. El estado describe tu documento, no la resolución clínica de un problema.",
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
        NotebookExportActions(vm, title.isNotBlank()) {
            NotebookRecord(id, "plan", owner, title, JSONObject(raw), System.currentTimeMillis())
        }
        if (original != null) Secondary("Eliminar este plan", { delete = true }, !vm.busy)
    }
    if (discard) DiscardDialog(back, { discard = false })
    if (delete)
        AlertDialog(
            onDismissRequest = { delete = false },
            title = { Text("¿Eliminar el plan?") },
            text = { Text("Podrás deshacer el último cambio desde Cuaderno de práctica.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.deleteNotebook(id, back)
                        delete = false
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = { TextButton(onClick = { delete = false }) { Text("Conservar") } },
        )
}

@Composable
fun StudyNotesScreen(vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    var search by rememberSaveable { mutableStateOf("") }
    val notes =
        vm.data.notebooks.filter {
            it.kind == "note" &&
                normalized(
                        it.title +
                            it.fields.optString("contenido") +
                            it.fields.optString("etiquetas")
                    )
                    .contains(normalized(search))
        }
    Page("Apuntes de enfermería", "Crear, editar, buscar y conservar localmente", back) {
        Primary(
            "Nuevo apunte",
            { nav("editar-apunte/${newLocalId()}") },
            !vm.busy,
            R.drawable.ic_plus,
        )
        MarkdownImportAction(vm)
        SearchBox(search, { search = it }, "Buscar título, texto o etiqueta…")
        notes.forEach { record ->
            AppCard(Modifier.fillMaxWidth(), onClick = { nav("editar-apunte/${record.id}") }) {
                Text(record.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    record.fields.optString("contenido").take(220),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalPalette.current.muted,
                )
                Text(
                    "${record.fields.optString("etiquetas")} · ${readableTime(record.modified)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Purple,
                )
            }
        }
        if (notes.isEmpty())
            EmptyState(
                "Tu cuaderno está listo",
                "Añade un apunte propio o cambia la búsqueda. No incluyas información identificable de pacientes.",
            )
        ActionRow(
            "Respaldo del cuaderno",
            "Importar o exportar planes y apuntes",
            R.drawable.ic_folder,
            Purple,
        ) {
            nav("cuaderno")
        }
        Section("Lecturas originales de orientación")
        NoteTitles.forEachIndexed { i, title -> Expandable(title, NoteTexts[i]) }
    }
}

@Composable
fun StudyNoteEditor(id: String, vm: AppViewModel, back: () -> Unit) {
    val original = vm.data.notebooks.find { it.id == id }
    var title by
        rememberSaveable(id, original?.modified) { mutableStateOf(original?.title.orEmpty()) }
    var body by
        rememberSaveable(id, original?.modified) {
            mutableStateOf(original?.fields?.optString("contenido").orEmpty())
        }
    var tags by
        rememberSaveable(id, original?.modified) {
            mutableStateOf(original?.fields?.optString("etiquetas").orEmpty())
        }
    var previewMarkdown by rememberSaveable(id) { mutableStateOf(false) }
    var discard by remember { mutableStateOf(false) }
    var delete by remember { mutableStateOf(false) }
    val dirty =
        title != original?.title.orEmpty() ||
            body != original?.fields?.optString("contenido").orEmpty() ||
            tags != original?.fields?.optString("etiquetas").orEmpty()
    val leave: () -> Unit = { if (dirty) discard = true else back() }
    BackHandler(dirty && !vm.busy) { discard = true }
    Page("Editar apunte", "Texto propio, sin datos reales", leave) {
        Input("Título", title, { title = it.take(140) })
        Input("Etiquetas / tema", tags, { tags = it.take(300) })
        Input("Contenido del apunte", body, { body = it.take(16000) }, multi = true)
        Text(
            "${body.length} / 16000 caracteres",
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
        Primary(
            "Guardar apunte",
            {
                vm.saveNotebook(
                    NotebookRecord(
                        id,
                        "note",
                        "",
                        title,
                        JSONObject().put("contenido", body).put("etiquetas", tags),
                        System.currentTimeMillis(),
                    ),
                    back,
                )
            },
            title.isNotBlank() && !vm.busy,
        )
        Secondary(if (previewMarkdown) "Ocultar lectura Markdown" else "Vista de lectura Markdown", { previewMarkdown = !previewMarkdown })
        if (previewMarkdown) {
            Badge("APUNTE PERSONAL · SIN REVISIÓN CLÍNICA", Gold)
            StudyMarkdownView(body)
        }
        NotebookExportActions(vm, title.isNotBlank()) {
            NotebookRecord(id, "note", "", title, JSONObject().put("contenido", body).put("etiquetas", tags), System.currentTimeMillis())
        }
        if (original != null) Secondary("Eliminar apunte", { delete = true }, !vm.busy)
    }
    if (discard) DiscardDialog(back, { discard = false })
    if (delete)
        AlertDialog(
            onDismissRequest = { delete = false },
            title = { Text("¿Eliminar apunte?") },
            text = { Text("Se elimina del cuaderno local; puedes deshacer el último cambio.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.deleteNotebook(id, back)
                        delete = false
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = { TextButton(onClick = { delete = false }) { Text("Conservar") } },
        )
}

@Composable
fun NotebookHub(
    vm: AppViewModel,
    nav: (String) -> Unit,
    back: () -> Unit,
    importer: () -> Unit,
    exporter: () -> Unit,
) {
    var undo by remember { mutableStateOf(false) }
    Page("Cuaderno de práctica", "Planes PAE y apuntes · respaldo independiente", back) {
        Badge("${vm.data.notebooks.size} / ${NotebookCodec.MAX_RECORDS} DOCUMENTOS", Teal)
        Notice(
            "Este respaldo incluye solo planes PAE y apuntes nuevos. No contiene el catálogo, multimedia, preferencias ni notas y registros de la ficha ficticia de v0.2. No es una copia completa del dispositivo.",
            true,
        )
        ActionRow("Mis apuntes", "Crear y editar textos propios", R.drawable.ic_edit, Purple) {
            nav("apuntes")
        }
        DemoPatients.forEach { p ->
            ActionRow(
                "Planes de ${p.name}",
                "Ficha ficticia · ${vm.data.notebooks.count {it.kind=="plan" && it.owner==p.id}} planes",
                R.drawable.ic_clipboard_check,
                Teal,
            ) {
                nav("plan/${p.id}")
            }
        }
        Primary("Exportar cuaderno JSON", exporter, !vm.busy, R.drawable.ic_download)
        Secondary("Importar cuaderno JSON", importer, !vm.busy)
        Secondary(
            "Deshacer último cambio de cuaderno",
            { undo = true },
            vm.data.hasNotebookUndo && !vm.busy,
        )
        Text(
            "Esquema 2: lee respaldos 1 y 2, conserva campos nuevos. Límites: 200 documentos y 2 MB de texto. La importación combina los documentos y reemplaza solo IDs coincidentes tras tu confirmación.",
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
    }
    if (undo)
        AlertDialog(
            onDismissRequest = { undo = false },
            title = { Text("¿Deshacer el último cambio?") },
            text = {
                Text(
                    "El cuaderno completo volverá al estado anterior al último guardado, borrado o importación. El catálogo y los archivos multimedia no cambian."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.undoNotebook()
                        undo = false
                    }
                ) {
                    Text("Deshacer")
                }
            },
            dismissButton = { TextButton(onClick = { undo = false }) { Text("Cancelar") } },
        )
}
