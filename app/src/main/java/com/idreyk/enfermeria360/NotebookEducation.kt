package com.idreyk.enfermeria360

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.os.OperationCanceledException
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.print.pdf.PrintedPdfDocument
import android.provider.OpenableColumns
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction

val EXTRA_PLAN_SECTIONS = linkedMapOf(
    "Datos y fuentes" to linkedMapOf(
        "subjetivos" to "Datos subjetivos ficticios",
        "objetivos_datos" to "Datos objetivos ficticios",
        "fuentes_datos" to "Fuente, método y momento de cada dato",
    ),
    "Gordon" to linkedMapOf(
        "gordon_1" to "Gordon · percepción y manejo de salud", "gordon_2" to "Gordon · nutrición y metabolismo",
        "gordon_3" to "Gordon · eliminación", "gordon_4" to "Gordon · actividad y ejercicio",
        "gordon_5" to "Gordon · sueño y descanso", "gordon_6" to "Gordon · cognición y percepción",
        "gordon_7" to "Gordon · autopercepción", "gordon_8" to "Gordon · roles y relaciones",
        "gordon_9" to "Gordon · sexualidad y reproducción", "gordon_10" to "Gordon · adaptación al estrés",
        "gordon_11" to "Gordon · valores y creencias",
    ),
    "ABCDE" to linkedMapOf(
        "abcde_a" to "A · observación de vía aérea", "abcde_b" to "B · observación respiratoria",
        "abcde_c" to "C · observación circulatoria", "abcde_d" to "D · observación neurológica",
        "abcde_e" to "E · exposición y entorno",
    ),
    "Razonamiento / NOC / NIC propios" to linkedMapOf(
        "priorizacion" to "Priorización y razonamiento del estudiante",
        "noc_propio" to "NOC · resultado e indicador en texto propio, sin catálogo licenciado",
        "nic_propio" to "NIC · intervención en texto propio, sin catálogo licenciado",
    ),
    "SOAPIE" to linkedMapOf(
        "soapie_s" to "SOAPIE · S subjetivo", "soapie_o" to "SOAPIE · O objetivo", "soapie_a" to "SOAPIE · A análisis",
        "soapie_p" to "SOAPIE · P plan", "soapie_i" to "SOAPIE · I intervención simulada", "soapie_e" to "SOAPIE · E evaluación",
    ),
    "SBAR" to linkedMapOf(
        "sbar_s" to "SBAR · S situación", "sbar_b" to "SBAR · B antecedentes",
        "sbar_a" to "SBAR · A valoración", "sbar_r" to "SBAR · R solicitud al equipo",
    ),
)
val EXTRA_PLAN_FIELDS = EXTRA_PLAN_SECTIONS.values.flatMap { it.entries }.associate { it.key to it.value }

object NotebookEducation {
    fun markdown(record: NotebookRecord): String {
        val valid = NotebookCodec.record(record.json())
        return buildString {
            appendLine("# EJERCICIO EDUCATIVO — iDreyK 360")
            appendLine()
            appendLine("**No es una historia clínica ni una prescripción. Solo datos ficticios.**")
            appendLine()
            appendLine("## ${valid.title.replace('\n', ' ')}")
            appendLine("Documento local de práctica; revisión clínica no acreditada.")
            appendLine("Fecha de modificación (UTC): ${java.time.Instant.ofEpochMilli(valid.modified)}")
            appendLine()
            if (valid.kind == "plan") {
                appendLine("Ficha de demostración: ${valid.owner}. Estado del documento: ${valid.fields.optString("estado")}.")
                appendLine("NANDA-I/NOC/NIC: texto del estudiante, sin catálogos ni códigos suministrados por la app.")
                (PLAN_FIELDS + EXTRA_PLAN_FIELDS).forEach { (key, title) ->
                    if (key in PLAN_FIELDS || valid.fields.optString(key).isNotBlank()) {
                        appendLine(); appendLine("### $title"); appendLine(valid.fields.optString(key).ifBlank { "Pendiente de completar." })
                    }
                }
            } else {
                appendLine("Procedencia / etiquetas declaradas: ${valid.fields.optString("etiquetas")}")
                appendLine(); appendLine(valid.fields.optString("contenido"))
            }
            appendLine(); appendLine("---"); appendLine("EJERCICIO EDUCATIVO. No acredita competencia ni autoriza decisiones asistenciales.")
        }
    }

    fun write(context: Context, uri: Uri, record: NotebookRecord) {
        val text = markdown(record)
        val stream = context.contentResolver.openOutputStream(uri, "wt")
            ?: throw IllegalStateException("No se pudo abrir el destino de exportación.")
        stream.use { it.write(text.toByteArray(Charsets.UTF_8)) }
    }

    /** Creates text only. No HTML renderer, script execution or remote image fetching. */
    fun readMarkdown(context: Context, uri: Uri): Pair<String, String> {
        val name = context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
            if (it.moveToFirst()) it.getString(0) else null
        } ?: "Apunte Markdown"
        val stream = context.contentResolver.openInputStream(uri) ?: throw IllegalArgumentException("No se pudo abrir el documento.")
        val bytes = stream.use { input ->
            val result = ByteArrayOutputStream()
            val buffer = ByteArray(8192)
            while (true) {
                val n = input.read(buffer)
                if (n == -1) break
                require(result.size() + n <= 64_000) { "El apunte supera 64 KB UTF-8. Divide el documento en temas." }
                result.write(buffer, 0, n)
            }
            result.toByteArray()
        }
        val text = Charsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT)
            .decode(ByteBuffer.wrap(bytes)).toString().removePrefix("\uFEFF")
        require(text.isNotBlank() && text.length <= 16_000 && '\u0000' !in text) { "Importa texto UTF-8 de 1 a 16 000 caracteres, no un archivo binario." }
        val title = name.filter { !it.isISOControl() }.take(140).ifBlank { "Apunte importado" }
        return title to text
    }

    fun print(context: Context, record: NotebookRecord) {
        val text = markdown(record)
        val manager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            ?: throw IllegalStateException("Este dispositivo no ofrece servicio de impresión.")
        manager.print("iDreyK · ejercicio educativo", EducationalPrintAdapter(context, text),
            PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(PrintAttributes.Resolution("educativo", "Documento educativo", 300, 300))
                .setMinMargins(PrintAttributes.Margins(400, 400, 400, 400)).setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME).build())
    }
}

/** Android print framework, with local PDF output. Actual printer/device remains user-selected. */
internal class EducationalPrintAdapter(private val context: Context, private val text: String) : PrintDocumentAdapter() {
    private var attributes: PrintAttributes? = null
    override fun onLayout(oldAttributes: PrintAttributes?, newAttributes: PrintAttributes, cancellationSignal: CancellationSignal,
        callback: LayoutResultCallback, extras: Bundle?) {
        if (cancellationSignal.isCanceled) { callback.onLayoutCancelled(); return }
        attributes = newAttributes
        callback.onLayoutFinished(PrintDocumentInfo.Builder("idreyk_ejercicio_educativo.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN).build(), oldAttributes != newAttributes)
    }
    override fun onWrite(pages: Array<out PageRange>, destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal, callback: WriteResultCallback) {
        val configured = attributes ?: run { callback.onWriteFailed("Falta preparar el documento."); return }
        try {
            cancellationSignal.throwIfCanceled()
            val written = FileOutputStream(destination.fileDescriptor).use {
                EducationalPdf.write(context, text, configured, pages, it, cancellationSignal)
            }
            callback.onWriteFinished(written)
        } catch (_: OperationCanceledException) {
            callback.onWriteCancelled()
        } catch (e: Exception) { callback.onWriteFailed(e.message ?: "No se pudo preparar el PDF educativo.") }
    }
}

/** Native renderer isolated from print-service callbacks so an actual device can test it directly. */
internal object EducationalPdf {
    fun write(context: Context, text: String, attributes: PrintAttributes, pages: Array<out PageRange>,
        output: OutputStream, cancellation: CancellationSignal): Array<PageRange> {
        cancellation.throwIfCanceled()
        val pdf = PrintedPdfDocument(context, attributes)
        try {
            val width = pdf.pageContentRect.width().toFloat()
            require(width >= 240 && pdf.pageContentRect.height() >= 120) {
                "El área imprimible es demasiado pequeña. Usa papel mayor o reduce los márgenes."
            }
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK; textSize = 10.5f; typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            }
            val lines = buildList {
                text.lines().forEach { paragraph ->
                    cancellation.throwIfCanceled()
                    var rest = paragraph
                    if (rest.isEmpty()) add("")
                    while (rest.isNotEmpty()) {
                        val n = paint.breakText(rest, true, width, null).coerceAtLeast(1)
                        add(rest.take(n)); rest = rest.drop(n)
                    }
                }
            }
            val perPage = ((pdf.pageContentRect.height() - 50) / 15).coerceAtLeast(1)
            require((lines.size + perPage - 1) / perPage <= 100) {
                "El documento supera 100 páginas educativas. Divídelo antes de imprimir."
            }
            val written = mutableListOf<PageRange>()
            lines.chunked(perPage).forEachIndexed { i, group ->
                cancellation.throwIfCanceled()
                if (pages.any { i in it.start..it.end }) {
                    val page = pdf.startPage(i + 1)
                    val canvas = page.canvas
                    val left = pdf.pageContentRect.left.toFloat()
                    val top = pdf.pageContentRect.top.toFloat()
                    canvas.drawText("EJERCICIO EDUCATIVO · NO HISTORIA CLÍNICA", left, top + 12, paint)
                    group.forEachIndexed { line, value -> canvas.drawText(value, left, top + 36 + line * 15, paint) }
                    canvas.drawText("Página ${i + 1} · iDreyK 360", left, pdf.pageContentRect.bottom.toFloat(), paint)
                    pdf.finishPage(page)
                    written += PageRange(i, i)
                }
            }
            require(written.isNotEmpty()) { "El intervalo no contiene páginas del documento." }
            cancellation.throwIfCanceled()
            pdf.writeTo(output)
            return written.toTypedArray()
        } finally { pdf.close() }
    }
}
