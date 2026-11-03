package com.idreyk.enfermeria360

import android.content.Context
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.RecordingPrintCallbacks.Layout
import android.print.RecordingPrintCallbacks.Write
import androidx.test.core.app.ApplicationProvider
import java.io.File
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Callback guards only. Robolectric 4.14.1 has no PdfDocument native implementation.
 * Real PDF creation and page-range tests are in androidTest, not silently mocked here. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class NotebookPrintTests {
    @get:Rule val temp = TemporaryFolder()
    private val context: Context get() = ApplicationProvider.getApplicationContext()
    private fun attributes(margins: Int = 400): PrintAttributes = PrintAttributes.Builder()
        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
        .setResolution(PrintAttributes.Resolution("test", "Educational test", 300, 300))
        .setMinMargins(PrintAttributes.Margins(margins, margins, margins, margins))
        .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME).build()
    private fun longMarkdown(): String = NotebookEducation.markdown(NotebookRecord(
        "print-qa", "note", "", "Prueba de paginación · datos inventados",
        JSONObject().put("etiquetas", "QA · sin datos de pacientes").put("contenido",
            (1..150).joinToString("\n") { "Línea ficticia $it: solamente verifica conservación del texto y paginación." }), 0L,
    ))
    private fun layout(adapter: EducationalPrintAdapter, margins: Int = 400): Layout = Layout().also {
        adapter.onLayout(null, attributes(margins), CancellationSignal(), it, null)
    }
    private fun write(adapter: EducationalPrintAdapter, file: File,
        pages: Array<out PageRange> = arrayOf(PageRange.ALL_PAGES),
        cancellation: CancellationSignal = CancellationSignal()): Write = Write().also { callback ->
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE).use { destination ->
            adapter.onWrite(pages, destination, cancellation, callback)
        }
    }

    @Test fun writingBeforeLayoutFailsWithoutTouchingDestination() {
        val file = temp.newFile().apply { writeText("PRESERVAR") }
        val result = write(EducationalPrintAdapter(context, "Ejercicio ficticio"), file)
        assertNull(result.pages); assertNotNull(result.error)
        assertEquals("PRESERVAR", file.readText())
    }
    @Test fun cancelledLayoutDoesNotAnnounceAReadyDocument() {
        val adapter = EducationalPrintAdapter(context, "Ejercicio ficticio")
        val callback = Layout()
        adapter.onLayout(null, attributes(), CancellationSignal().apply { cancel() }, callback, null)
        assertTrue(callback.cancelled); assertNull(callback.info); assertNull(callback.error)
    }
    @Test fun cancelledWriteDoesNotTruncateOrWriteTheDestination() {
        val adapter = EducationalPrintAdapter(context, longMarkdown()); layout(adapter)
        val file = temp.newFile().apply { writeText("PRESERVAR") }
        val result = write(adapter, file, cancellation = CancellationSignal().apply { cancel() })
        assertTrue(result.cancelled); assertNull(result.pages); assertNull(result.error)
        assertEquals("PRESERVAR", file.readText())
    }
    @Test fun excessivePageCountFailsBeforeNativeRenderingOrWriting() {
        val adapter = EducationalPrintAdapter(context, "\n".repeat(6000)); layout(adapter)
        val file = temp.newFile().apply { writeText("PRESERVAR") }
        val result = write(adapter, file)
        assertNull(result.pages)
        assertTrue(result.error.toString().contains("100 páginas"))
        assertEquals("PRESERVAR", file.readText())
    }
    @Test fun unusablePrintableAreaFailsInsteadOfAllocatingThousandsOfTinyPages() {
        val adapter = EducationalPrintAdapter(context, longMarkdown()); layout(adapter, margins = 4000)
        val file = temp.newFile().apply { writeText("PRESERVAR") }
        val result = write(adapter, file)
        assertNull(result.pages)
        assertTrue(result.error.toString().contains("área imprimible"))
        assertEquals("PRESERVAR", file.readText())
    }
}
