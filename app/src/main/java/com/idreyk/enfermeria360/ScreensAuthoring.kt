package com.idreyk.enfermeria360

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject

fun kindTitle(kind: String): String =
    when (kind) {
        "med" -> "Medicamento"
        "case" -> "Caso de estudio"
        "safety" -> "Lista de seguridad"
        "protocol" -> "Protocolo"
        else -> "Contenido"
    }

@Composable
private fun EditableSection(title: String, value: String, onChange: (String) -> Unit) {
    var open by rememberSaveable(title) { mutableStateOf(false) }
    AppCard(Modifier.fillMaxWidth()) {
        TextButton(onClick = { open = !open }) {
            Text("${if(open) "−" else "+"} $title · ${value.length} caracteres")
        }
        if (open) Input(title, value, { onChange(it.take(16000)) }, multi = true)
    }
}

@Composable
fun EntryEditorScreen(kind: String, id: String, vm: AppViewModel, back: () -> Unit) {
    val original = vm.data.entries.find { it.kind == kind && it.id == id }
    val initial = original?.json?.toString() ?: CatalogAuthoring.empty(kind, id).toString()
    var raw by rememberSaveable(kind, id, initial) { mutableStateOf(initial) }
    val aliasesInitial =
        original
            ?.json
            ?.optJSONArray("sinonimos")
            ?.let { a -> (0 until a.length()).joinToString("; ") { a.getString(it) } }
            .orEmpty()
    var aliases by rememberSaveable(kind, id, initial) { mutableStateOf(aliasesInitial) }
    var license by
        rememberSaveable(kind, id, initial) {
            mutableStateOf(original?.json?.optString("_licencia").orEmpty())
        }
    var discard by remember { mutableStateOf(false) }
    var delete by remember { mutableStateOf(false) }
    val o = JSONObject(raw)
    val nameKey = if (kind == "med") "nombre" else "titulo"
    val groupKey = if (kind == "med") "grupo" else "area"
    val dirty =
        raw != initial ||
            aliases != aliasesInitial ||
            license != original?.json?.optString("_licencia").orEmpty()
    val leave: () -> Unit = { if (dirty) discard = true else back() }
    fun change(key: String, value: String) {
        raw = JSONObject(raw).put(key, value).toString()
    }
    fun changeItem(index: Int, key: String, value: String) {
        val copy = JSONObject(raw)
        copy.getJSONArray("items").getJSONObject(index).put(key, value)
        raw = copy.toString()
    }
    fun changeSource(index: Int, key: String, value: String) {
        val copy = JSONObject(raw)
        copy.getJSONArray("fuentes").getJSONObject(index).put(key, value)
        raw = copy.toString()
    }
    BackHandler(dirty && !vm.busy) { discard = true }
    Page(
        "Editar ${kindTitle(kind).lowercase()}",
        "Autoría local · ID estable · guardado como borrador",
        leave,
    ) {
        Notice(
            "Escribe solo material propio o autorizado. Cualquier edición local vuelve a BORRADOR y conserva la procedencia anterior; guardar no valida exactitud clínica, vigencia ni derechos.",
            true,
        )
        Input(
            if (kind == "med") "Nombre del medicamento" else "Título",
            o.optString(nameKey),
            { change(nameKey, it.take(140)) },
        )
        Input(
            if (kind == "med") "Grupo farmacológico" else "Área / tema",
            o.optString(groupKey),
            { change(groupKey, it.take(140)) },
        )
        if (kind == "med") {
            Input("Sinónimos · separados por punto y coma", aliases, { aliases = it.take(4000) })
            var fieldGroup by rememberSaveable(kind, id) { mutableIntStateOf(0) }
            val groups = PharmacologyLayout.editorGroups()
            Section("Ficha flexible · 17 conceptos del PDF y cuidados")
            Text(
                "Puedes guardar una ficha parcial. Vacío significa pendiente; si no aplica, escribe la justificación. El nombre genérico se edita arriba.",
                style = MaterialTheme.typography.bodySmall,
            )
            Row(
                Modifier.testTag("medication-fields-tabs").horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                groups.forEachIndexed { index, group ->
                    FilterChip(
                        fieldGroup == index,
                        { fieldGroup = index },
                        label = { Text(group.first) },
                    )
                }
            }
            groups[fieldGroup].second.forEach { key ->
                val label = DRUG_FIELDS.getValue(key)
                EditableSection(label, o.optJSONObject("secciones")?.optString(key).orEmpty()) {
                    value ->
                    val copy = JSONObject(raw)
                    val sections = copy.optJSONObject("secciones") ?: JSONObject()
                    sections.put(key, value)
                    raw = copy.put("secciones", sections).toString()
                }
            }
        } else if (kind == "safety") {
            Input(
                "Institución / procedencia",
                o.optString("institucion"),
                { change("institucion", it.take(500)) },
            )
            Input(
                "Edición o fecha de la lista",
                o.optString("edicion"),
                { change("edicion", it.take(140)) },
            )
            val items = o.optJSONArray("items") ?: JSONArray()
            Section("${items.length()} ítems · orden de verificación")
            Text(
                "No se impone un número universal de correctos. Usa una lista identificada por fuente y contexto; una lista local no reemplaza el protocolo institucional.",
                style = MaterialTheme.typography.bodySmall,
                color = LocalPalette.current.muted,
            )
            (0 until items.length()).forEach { i ->
                AppCard(Modifier.fillMaxWidth()) {
                    Badge("ÍTEM ${i+1}", Teal)
                    Input(
                        "Título del ítem ${i+1}",
                        items.getJSONObject(i).optString("titulo"),
                        { changeItem(i, "titulo", it.take(180)) },
                    )
                    Input(
                        "Detalle del ítem ${i+1}",
                        items.getJSONObject(i).optString("detalle"),
                        { changeItem(i, "detalle", it.take(3000)) },
                        multi = true,
                    )
                    if (i > 0)
                        TextButton(
                            onClick = {
                                val copy = JSONObject(raw)
                                val a = copy.getJSONArray("items")
                                val current = a.getJSONObject(i)
                                val previous = a.getJSONObject(i - 1)
                                a.put(i - 1, current)
                                a.put(i, previous)
                                raw = copy.toString()
                            }
                        ) {
                            Text("Subir un lugar")
                        }
                    TextButton(
                        onClick = {
                            val copy = JSONObject(raw)
                            val a = copy.getJSONArray("items")
                            a.remove(i)
                            raw = copy.toString()
                        }
                    ) {
                        Text("Quitar ítem")
                    }
                }
            }
            Secondary(
                "Añadir ítem",
                {
                    val copy = JSONObject(raw)
                    copy
                        .getJSONArray("items")
                        .put(JSONObject().put("titulo", "").put("detalle", ""))
                    raw = copy.toString()
                },
                items.length() < 100,
            )
        } else {
            Input(
                "Objetivo y alcance",
                o.optString("objetivo"),
                { change("objetivo", it.take(4000)) },
                multi = true,
            )
            Input(
                if (kind == "protocol") "Contenido / procedimiento autorizado"
                else "Contenido del caso ficticio",
                o.optString("contenido"),
                { change("contenido", it.take(60000)) },
                multi = true,
            )
            Text(
                "Texto literal. La app no interpreta ni recomienda lo escrito.",
                style = MaterialTheme.typography.bodySmall,
                color = LocalPalette.current.muted,
            )
        }
        Section("Fuentes documentales")
        val sources = o.optJSONArray("fuentes") ?: JSONArray()
        (0 until sources.length()).forEach { i ->
            val source = sources.getJSONObject(i)
            AppCard(Modifier.fillMaxWidth()) {
                Badge("FUENTE ${i+1}", Purple)
                Input(
                    "Título de la fuente ${i+1}",
                    source.optString("titulo"),
                    { changeSource(i, "titulo", it.take(500)) },
                )
                Input(
                    "URL HTTPS de la fuente ${i+1}",
                    source.optString("url"),
                    { changeSource(i, "url", it.take(2000)) },
                )
                Input(
                    "Autor / editor de la fuente ${i+1}",
                    source.optString("editor"),
                    { changeSource(i, "editor", it.take(500)) },
                )
                Input(
                    "Consulta AAAA-MM-DD · opcional",
                    source.optString("fecha_consulta"),
                    { changeSource(i, "fecha_consulta", it.take(10)) },
                )
                TextButton(
                    onClick = {
                        val copy = JSONObject(raw)
                        copy.getJSONArray("fuentes").remove(i)
                        raw = copy.toString()
                    }
                ) {
                    Text("Quitar fuente ${i+1}")
                }
            }
        }
        Secondary(
            "Añadir fuente",
            {
                val copy = JSONObject(raw)
                val a = copy.optJSONArray("fuentes") ?: JSONArray()
                a.put(
                    JSONObject()
                        .put("titulo", "")
                        .put("url", "")
                        .put("editor", "")
                        .put("fecha_consulta", "")
                )
                raw = copy.put("fuentes", a).toString()
            },
            sources.length() < 30,
        )
        Input(
            "Licencia / permisos documentados · opcional",
            license,
            { license = it.take(2000) },
            multi = true,
        )
        Text(
            "Editor local: ${vm.name}. ID: $id. Se conserva una copia del catálogo anterior para deshacer desde Biblioteca local.",
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
        Primary(
            "Guardar ficha como borrador",
            {
                val edited = JSONObject(raw)
                if (kind == "med")
                    edited.put(
                        "sinonimos",
                        JSONArray(aliases.split(';').map { it.trim() }.filter { it.isNotEmpty() }),
                    )
                vm.saveLocalEntry(kind, edited, license, back)
            },
            o.optString(nameKey).isNotBlank() && !vm.busy,
        )
        if (original != null) Secondary("Eliminar esta ficha", { delete = true }, !vm.busy)
    }
    if (discard) DiscardDialog(back, { discard = false })
    if (delete)
        AlertDialog(
            onDismissRequest = { delete = false },
            title = { Text("¿Eliminar esta ficha?") },
            text = {
                Text(
                    "Se quitará del catálogo local. Puedes recuperar el catálogo anterior con Deshacer en Biblioteca local."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.deleteEntry("$kind/$id", back)
                        delete = false
                    }
                ) {
                    Text("Eliminar ficha")
                }
            },
            dismissButton = { TextButton(onClick = { delete = false }) { Text("Conservar") } },
        )
}

@Composable
fun ProtocolsScreen(vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val protocols =
        vm.data.entries.filter {
            it.kind == "protocol" &&
                normalized(it.name + it.group + it.body).contains(normalized(query))
        }
    Page("Protocolos locales", "Textos propios o autorizados · fuentes visibles", back) {
        Notice(
            "No se incluye un catálogo clínico inventado. Crea o importa tus documentos; la app identifica su revisión declarada, pero no verifica su validez ni los adopta como protocolo asistencial.",
            true,
        )
        Primary(
            "Crear protocolo local",
            { nav(newEntryRoute("protocol")) },
            !vm.busy,
            R.drawable.ic_plus,
        )
        SearchBox(query, { query = it }, "Buscar título, área o texto…")
        protocols.forEach { e ->
            ActionRow(
                e.name,
                "${e.group} · ${if(e.revision=="borrador")"BORRADOR" else "REVISIÓN DECLARADA"}",
                R.drawable.ic_file,
                Teal,
            ) {
                nav(e.key)
            }
        }
        if (protocols.isEmpty())
            EmptyState(
                "Biblioteca de protocolos",
                "Añade un documento propio o importa un paquete JSON de esquema 2 o 3 desde Biblioteca local.",
            )
        ActionRow(
            "Importar / exportar catálogo",
            "Los protocolos se admiten en esquemas 2 y 3",
            R.drawable.ic_folder,
            Purple,
        ) {
            nav("contenido")
        }
    }
}

@Composable
fun ProtocolDetail(id: String, vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    val e = vm.data.entries.find { it.kind == "protocol" && it.id == id }
    Page(e?.name ?: "Protocolo no disponible", "Documento local, no validación asistencial", back) {
        if (e == null)
            EmptyState(
                "No se encontró el documento",
                "El protocolo pudo ser eliminado o reemplazado.",
            )
        else {
            Badge(
                if (e.revision == "borrador") "BORRADOR" else "REVISIÓN DECLARADA POR SU AUTOR",
                Gold,
            )
            Notice(
                "La app no verifica ni certifica este procedimiento. Confirma vigencia, contexto institucional, permisos y revisión profesional antes de cualquier uso clínico.",
                true,
            )
            Favorite(e.key, e.name, vm)
            Text(e.group, style = MaterialTheme.typography.bodySmall, color = Purple)
            Expandable("Objetivo y alcance", e.objective, true)
            AppCard(Modifier.fillMaxWidth()) {
                Text(
                    e.body.ifBlank { "Documento vacío. Edítalo o importa contenido autorizado." },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            e.references.forEach { SourceCard(it) }
            Text(
                "Autor declarado: ${e.json.optString("_autor")}\nActualización declarada: ${e.json.optString("_actualizacion")}\nPermisos: ${e.json.optString("_licencia").ifBlank {"Sin documentar"}}",
                style = MaterialTheme.typography.bodySmall,
                color = LocalPalette.current.muted,
            )
            Provenance(e)
            Secondary("Editar protocolo como borrador", { nav("editar/protocol/$id") })
        }
    }
}

@Composable
fun Provenance(entry: Entry) {
    entry.json.optJSONObject("derivado_de")?.let { source ->
        Expandable(
            "Procedencia conservada",
            "Paquete: ${source.optString("paquete")}\nAutor original declarado: ${source.optString("autor")}\nFecha original declarada: ${source.optString("actualizacion")}\nLicencia original: ${source.optString("licencia").ifBlank {"No documentada"}}\nLa edición local no hereda una declaración de revisión clínica.",
        )
    }
}
