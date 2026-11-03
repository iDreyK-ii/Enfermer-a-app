package com.idreyk.enfermeria360

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import org.json.JSONObject

@Composable
fun NotebookExportActions(vm: AppViewModel, enabled: Boolean, record: () -> NotebookRecord) {
    val context = LocalContext.current
    // The reviewed snapshot survives the document picker; never export a later changed draft silently.
    var pending by rememberSaveable { mutableStateOf<String?>(null) }
    val exporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/markdown")) { uri ->
        if (uri != null) {
            try {
                val raw = pending ?: throw IllegalStateException("No queda una copia del borrador para exportar. Repite la exportación.")
                vm.exportEducationalMarkdown(uri, NotebookCodec.record(JSONObject(raw)))
            } catch (e: Exception) { vm.notify(e.message ?: "No se exportó el documento.") }
        }
        pending = null
    }
    Secondary("Exportar ejercicio Markdown", {
        try {
            pending = NotebookCodec.record(record().json()).json().toString()
            exporter.launch("idreyk_ejercicio_educativo.md")
        } catch (e: Exception) { vm.notify(e.message ?: "Revisa el documento antes de exportarlo.") }
    }, enabled && !vm.busy)
    Secondary("Imprimir ejercicio / PDF", {
        try { NotebookEducation.print(context, record()) }
        catch (e: Exception) { vm.notify(e.message ?: "No se pudo abrir la impresión educativa.") }
    }, enabled && !vm.busy)
    Text("Exporta el borrador visible con la marca EJERCICIO EDUCATIVO. No guarda automáticamente cambios en el cuaderno. Impresión/PDF utiliza el servicio Android del dispositivo.", style = MaterialTheme.typography.bodySmall)
}

@Composable
fun MarkdownImportAction(vm: AppViewModel) {
    val importer = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) vm.importStudyMarkdown(uri)
    }
    Secondary("Importar apunte Markdown", { importer.launch(arrayOf("text/markdown", "text/x-markdown", "text/plain", "application/octet-stream")) }, !vm.busy)
    Text("Importación local UTF-8: hasta 16 000 caracteres / 64 KB por tema. Crea un apunte nuevo, no valida el contenido ni carga imágenes remotas. Solo documentos propios/autorizados, sin datos reales.", style = MaterialTheme.typography.bodySmall)
}
