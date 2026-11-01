package com.idreyk.enfermeria360

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.util.UUID

object MediaFiles {
    const val MAX_FILE = 20L * 1024 * 1024
    const val MAX_LIBRARY = 100L * 1024 * 1024
    val extensions =
        linkedMapOf(
            "application/pdf" to "pdf",
            "image/jpeg" to "jpg",
            "image/png" to "png",
            "image/webp" to "webp",
            "image/gif" to "gif",
            "audio/mpeg" to "mp3",
            "audio/wav" to "wav",
            "audio/x-wav" to "wav",
            "audio/ogg" to "ogg",
            "video/mp4" to "mp4",
            "video/webm" to "webm",
        )

    fun directory(context: Context) = File(context.filesDir, "biblioteca").apply { mkdirs() }

    fun file(context: Context, filename: String): File {
        require(Regex("[a-f0-9-]{36}\\.[a-z0-9]{2,5}").matches(filename)) {
            "Nombre interno de archivo inválido."
        }
        return File(directory(context), filename)
    }

    fun copy(context: Context, uri: Uri, availableBytes: Long): MediaRecord {
        require(availableBytes > 0) { "La biblioteca alcanzó 100 MB. Quita alguna copia local." }
        var title =
            if (uri.scheme == "file")
                uri.lastPathSegment.orEmpty().take(140).ifBlank { "Recurso local" }
            else "Recurso local"
        runCatching {
            context.contentResolver
                .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use {
                    if (it.moveToFirst())
                        title =
                            it.getString(0)
                                .orEmpty()
                                .replace(Regex("[\\p{Cntrl}/\\\\]"), " ")
                                .take(140)
                                .ifBlank { "Recurso local" }
                }
        }
        val declared = context.contentResolver.getType(uri)
        require(
            declared == null || declared == "application/octet-stream" || declared in extensions
        ) {
            "Tipo de archivo no admitido."
        }
        val mime =
            if (declared in extensions) declared!!
            else
                extensions.entries
                    .firstOrNull { title.substringAfterLast('.', "").lowercase() == it.value }
                    ?.key
        require(mime != null && mime in extensions) {
            "Formato no admitido. Usa PDF, JPG, PNG, WebP, GIF, MP3, WAV, OGG, MP4 o WebM."
        }
        val id = UUID.randomUUID().toString()
        val filename = "$id.${extensions.getValue(mime)}"
        val temporary = File(directory(context), "temp_$id")
        val destination = file(context, filename)
        var size = 0L
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                temporary.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    while (true) {
                        val read = input.read(buffer)
                        if (read < 0) break
                        if (read == 0) continue
                        size += read
                        require(size <= MAX_FILE && size <= availableBytes) {
                            "Máximo 20 MB por archivo y 100 MB para toda la biblioteca."
                        }
                        output.write(buffer, 0, read)
                    }
                }
            } ?: throw IllegalArgumentException("No se pudo leer el archivo seleccionado.")
            require(size > 0) { "El archivo está vacío." }
            require(temporary.renameTo(destination)) { "No se pudo conservar la copia local." }
            return MediaRecord(id, title, mime, filename, size, System.currentTimeMillis())
        } finally {
            if (temporary.exists()) temporary.delete()
        }
    }

    fun delete(context: Context, record: MediaRecord) {
        val stored = file(context, record.filename)
        require(!stored.exists() || stored.delete()) { "No se pudo quitar la copia local." }
    }
}
