package com.idreyk.enfermeria360

import android.content.ClipData
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider

@Composable
fun MediaLibraryScreen(vm: AppViewModel, back: () -> Unit, importer: () -> Unit) {
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    var deleting by remember { mutableStateOf<MediaRecord?>(null) }
    var editing by remember { mutableStateOf<MediaRecord?>(null) }
    var title by rememberSaveable { mutableStateOf("") }
    val media = vm.data.media.filter { normalized(it.title + it.mime).contains(normalized(query)) }
    Page("Multimedia y documentos", "Copias privadas en este dispositivo", back) {
        Notice(
            "Añade recursos propios o autorizados, sin datos reales de pacientes. Se conserva una copia privada; no se procesa su contenido. Los archivos se abren con una aplicación compatible del dispositivo, no con un reproductor integrado.",
            true,
        )
        Primary("Añadir archivo local", importer, !vm.busy, R.drawable.ic_paperclip)
        Text(
            "PDF · imágenes · audio · video\n20 MB por archivo · 100 MB en total · ${vm.data.media.size} archivos",
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
        SearchBox(query, { query = it }, "Buscar título o formato…")
        media.forEach { record ->
            AppCard(Modifier.fillMaxWidth()) {
                Badge(record.mime, Teal)
                Text(record.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${DoseMath.display(record.bytes/1024.0)} KB · ${readableTime(record.created)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalPalette.current.muted,
                )
                Primary(
                    "Abrir archivo",
                    {
                        runCatching {
                                val file = MediaFiles.file(context, record.filename)
                                require(file.exists()) { "La copia local no está disponible." }
                                val uri =
                                    FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.files",
                                        file,
                                    )
                                val intent =
                                    Intent(Intent.ACTION_VIEW)
                                        .setDataAndType(uri, record.mime)
                                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        .apply {
                                            clipData = ClipData.newRawUri("Recurso local", uri)
                                        }
                                context.startActivity(
                                    Intent.createChooser(intent, "Abrir copia local")
                                )
                            }
                            .onFailure {
                                vm.notify(
                                    "No se pudo abrir. Comprueba que tienes una aplicación compatible con ${record.mime}. ${it.message.orEmpty().take(100)}"
                                )
                            }
                    },
                    !vm.busy,
                )
                TextButton(
                    onClick = {
                        editing = record
                        title = record.title
                    },
                    enabled = !vm.busy,
                ) {
                    Text("Cambiar título")
                }
                TextButton(onClick = { deleting = record }, enabled = !vm.busy) {
                    Text("Quitar copia local")
                }
            }
        }
        if (media.isEmpty())
            EmptyState(
                "Tus recursos, a mano",
                "Añade un archivo del dispositivo. La app no descarga cursos ni incorpora material protegido sin autorización.",
            )
        Notice(
            "Los respaldos JSON del catálogo y del cuaderno no incluyen estos archivos. Conserva los originales. Al desinstalar o borrar los datos se eliminan las copias privadas. La aplicación externa que abra un archivo tiene sus propias condiciones de privacidad."
        )
    }
    deleting?.let { record ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("¿Quitar la copia local?") },
            text = {
                Text(
                    "Se quitará ${record.title}. El archivo original no se modifica y esta acción no tiene deshacer."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.deleteMedia(record.id)
                        deleting = null
                    }
                ) {
                    Text("Quitar copia")
                }
            },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Conservar") } },
        )
    }
    editing?.let { record ->
        AppSheet("Título del recurso", { editing = null }) {
            Input("Título local", title, { title = it.take(140) })
            Primary(
                "Guardar título",
                { vm.renameMedia(record.id, title) { editing = null } },
                title.isNotBlank() && !vm.busy,
            )
        }
    }
}
