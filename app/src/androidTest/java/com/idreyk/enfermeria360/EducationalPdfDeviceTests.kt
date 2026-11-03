package com.idreyk.enfermeria360

import android.graphics.pdf.PdfRenderer
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/** Requires an actual Android framework (device/emulator). Compiling is NOT executing these tests.
 * Robolectric 4.14.1 does not implement PdfDocument's native writer; do not replace it with a fake PDF.
 * This suite verifies the writer only, not user interaction with a print service/SAF provider.
 */
@RunWith(AndroidJUnit4::class)
class EducationalPdfDeviceTests {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private fun attributes(): PrintAttributes = PrintAttributes.Builder()
        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
        .setResolution(PrintAttributes.Resolution("qa", "Educational test", 300, 300))
        .setMinMargins(PrintAttributes.Margins(400, 400, 400, 400))
        .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME).build()
    private fun markdown(): String = NotebookEducation.markdown(NotebookRecord(
        "print-qa", "note", "", "Prueba educativa · sin pacientes reales",
        JSONObject().put("etiquetas", "QA sintética").put("contenido",
            (1..150).joinToString("\n") { "Línea ficticia $it: prueba de conservación del texto y paginación." }), 0L,
    ))
    private fun pageCount(file: File): Int = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
        val renderer = PdfRenderer(descriptor)
        try { renderer.pageCount } finally { renderer.close() }
    }
    @Test fun longEducationalDocumentProducesAReadableMultipagePdf() {
        val file = File(context.cacheDir, "idreyk-export-qa.pdf")
        val written = file.outputStream().use {
            EducationalPdf.write(context, markdown(), attributes(), arrayOf(PageRange.ALL_PAGES), it, CancellationSignal())
        }
        assertTrue(written.size >= 3)
        assertEquals(written.size, pageCount(file))
        assertTrue(file.length() > 1000)
    }
    @Test fun selectedZeroBasedPageProducesOnlyOneReadablePdfPage() {
        val file = File(context.cacheDir, "idreyk-export-qa-selected.pdf")
        val written = file.outputStream().use {
            EducationalPdf.write(context, markdown(), attributes(), arrayOf(PageRange(1, 1)), it, CancellationSignal())
        }
        assertArrayEquals(arrayOf(PageRange(1, 1)), written)
        assertEquals(1, pageCount(file))
    }
}
