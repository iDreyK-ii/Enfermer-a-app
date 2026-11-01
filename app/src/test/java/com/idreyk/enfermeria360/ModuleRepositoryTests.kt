package com.idreyk.enfermeria360

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ModuleRepositoryTests {
    private lateinit var context: Context
    private lateinit var repo: ContentRepository

    @Before
    fun create() {
        context = RuntimeEnvironment.getApplication()
        context.deleteDatabase("idreyk_local.db")
        MediaFiles.directory(context).listFiles()?.forEach { it.delete() }
        repo = ContentRepository(context)
        repo.initialize()
    }

    @After
    fun close() {
        repo.close()
        MediaFiles.directory(context).listFiles()?.forEach { it.delete() }
    }

    private fun note(id: String = "nota-1", text: String = "Apunte ficticio") =
        NotebookRecord(
            id,
            "note",
            "",
            "Título QA",
            JSONObject().put("contenido", text).put("etiquetas", "QA"),
            1,
        )

    @Test
    fun sqliteOneToTwoMigrationPreservesAllOldTablesAndUndo() {
        repo.saveNote("mario", "NOTA CONSERVADA")
        repo.toggleFavorite("med/paracetamol")
        repo.savePractice("Prueba", "PRÁCTICA CONSERVADA")
        val original = repo.entries().first { it.id == "paracetamol" }
        repo.saveLocalEntry(
            "med",
            JSONObject(original.json.toString()).put("nombre", "Paracetamol QA"),
            "Editor QA",
            "Texto de prueba",
        )
        repo.close()
        // Exact v1 schema fixture: v2 only adds these two tables; keep every v1 row intact.
        SQLiteDatabase.openDatabase(
                context.getDatabasePath("idreyk_local.db").path,
                null,
                SQLiteDatabase.OPEN_READWRITE,
            )
            .use { db ->
                db.execSQL("DROP TABLE notebooks")
                db.execSQL("DROP TABLE media")
                db.version = 1
            }
        repo = ContentRepository(context)
        repo.initialize()
        assertEquals(8, repo.entries().size)
        assertEquals("NOTA CONSERVADA", repo.notes().single().text)
        assertEquals("PRÁCTICA CONSERVADA", repo.practices().single().summary)
        assertTrue("med/paracetamol" in repo.favorites())
        assertTrue(repo.notebooks().isEmpty())
        assertTrue(repo.media().isEmpty())
        assertTrue(repo.hasBackup())
        repo.undoImport()
        assertEquals("Paracetamol", repo.entries().first { it.id == "paracetamol" }.name)
        SQLiteDatabase.openDatabase(
                context.getDatabasePath("idreyk_local.db").path,
                null,
                SQLiteDatabase.OPEN_READONLY,
            )
            .use { assertEquals(2, it.version) }
    }

    @Test
    fun localMedicationEditPersistsTwentyFieldsAndProvenance() {
        val existing = repo.entries().first { it.id == "paracetamol" }
        val edited =
            JSONObject(existing.json.toString())
                .put(
                    "secciones",
                    JSONObject().apply {
                        DRUG_FIELDS.keys.forEach { put(it, "Texto ficticio para $it") }
                    },
                )
        repo.saveLocalEntry("med", edited, "Editor local QA", "Permiso ficticio")
        repo.close()
        repo = ContentRepository(context)
        repo.initialize()
        val saved = repo.entries().first { it.id == "paracetamol" }
        assertTrue(saved.sections.values.all { it.startsWith("Texto ficticio") })
        assertEquals("borrador", saved.revision)
        assertEquals("Editor local QA", saved.json.getString("_autor"))
        assertEquals(
            existing.json.getString("_autor"),
            saved.json.getJSONObject("derivado_de").getString("autor"),
        )
        repo.undoImport()
        assertTrue(
            repo.entries().first { it.id == "paracetamol" }.sections.values.all { it.isEmpty() }
        )
    }

    @Test
    fun localProtocolAndConfigurableListSurviveCatalogRoundTrip() {
        val protocol =
            CatalogAuthoring.empty("protocol", "protocolo-qa")
                .put("titulo", "Documento QA")
                .put("contenido", "Texto ficticio, sin pautas clínicas")
        repo.saveLocalEntry("protocol", protocol, "QA", "")
        val checklist =
            CatalogAuthoring.empty("safety", "lista-qa")
                .put("titulo", "Lista de 20 ítems ficticios")
                .put(
                    "items",
                    JSONArray().apply {
                        for (i in 1..20) put(
                            JSONObject().put("titulo", "Prueba $i").put("detalle", "Dato ficticio")
                        )
                    },
                )
        repo.saveLocalEntry("safety", checklist, "QA", "")
        val exported = repo.exportText()
        assertEquals(2, JSONObject(exported).getInt("version_esquema"))
        repo.reset()
        repo.writePackage(ContentParser.parse(exported))
        assertEquals(
            "Texto ficticio, sin pautas clínicas",
            repo.entries().single { it.kind == "protocol" }.body,
        )
        assertEquals(20, repo.entries().single { it.id == "lista-qa" }.checklist.size)
    }

    @Test
    fun invalidEditDoesNotEraseDataOrPreviousUndo() {
        val original = repo.entries().first { it.id == "paracetamol" }
        repo.saveLocalEntry(
            "med",
            JSONObject(original.json.toString()).put("nombre", "Nombre QA"),
            "QA",
            "",
        )
        assertThrows(IllegalArgumentException::class.java) {
            repo.saveLocalEntry(
                "med",
                JSONObject(original.json.toString()).put("nombre", ""),
                "QA",
                "",
            )
        }
        assertEquals("Nombre QA", repo.entries().first { it.id == "paracetamol" }.name)
        repo.undoImport()
        assertEquals(original.name, repo.entries().first { it.id == "paracetamol" }.name)
    }

    @Test
    fun deletionCanBeUndoneAndEvenEmptyCatalogIsExportable() {
        val keys = repo.entries().map { it.key }
        keys.forEach { repo.deleteEntry(it) }
        assertTrue(repo.entries().isEmpty())
        assertTrue(ContentParser.parse(repo.exportText()).isBackup)
        repo.undoImport()
        assertEquals(1, repo.entries().size)
    }

    @Test
    fun notesAndStructuredPlansPersistWithoutReplacingV02Notes() {
        repo.saveNote("plan:mario", "1010")
        repo.saveNotebook(note())
        val fields =
            JSONObject().apply {
                PLAN_FIELDS.keys.forEach { put(it, "Ejercicio ficticio de $it") }
                put("estado", "borrador")
            }
        repo.saveNotebook(NotebookRecord("plan-1", "plan", "mario", "Plan ficticio", fields, 1))
        repo.close()
        repo = ContentRepository(context)
        repo.initialize()
        assertEquals(2, repo.notebooks().size)
        assertEquals("1010", repo.notes().single().text)
        assertEquals("mario", repo.notebooks().single { it.kind == "plan" }.owner)
        assertEquals(2, NotebookCodec.decode(repo.notebookText()).size)
        assertFalse(repo.exportText().contains("Apunte ficticio"))
    }

    @Test
    fun notebookImportMergeAndUndoAreTransactional() {
        repo.saveNotebook(note("nota-1", "ANTES"))
        repo.mergeNotebooks(listOf(note("nota-1", "DESPUÉS"), note("nota-2", "NUEVA")))
        assertEquals(2, repo.notebooks().size)
        repo.undoNotebook()
        assertEquals(1, repo.notebooks().size)
        assertEquals("ANTES", repo.notebooks().single().fields.getString("contenido"))
        assertFalse(repo.hasNotebookUndo())
        repo.deleteNotebook("nota-1")
        assertTrue(repo.notebooks().isEmpty())
        repo.undoNotebook()
        assertEquals(1, repo.notebooks().size)
    }

    @Test
    fun rejectedNotebookMergePreservesExistingRecordsAndUndo() {
        repo.saveNotebook(note("nota-1", "ORIGINAL"))
        repo.saveNotebook(note("nota-1", "EDITADO"))
        assertThrows(IllegalArgumentException::class.java) {
            repo.mergeNotebooks((0..200).map { note("otra-$it") })
        }
        assertEquals("EDITADO", repo.notebooks().single().fields.getString("contenido"))
        repo.undoNotebook()
        assertEquals("ORIGINAL", repo.notebooks().single().fields.getString("contenido"))
    }

    @Test
    fun mediaCopyIsPrivateAndOpeningGrantsOnlyItsFile() {
        val original =
            File(context.cacheDir, "recurso-qa.pdf").apply {
                writeText("%PDF-1.4\nQA ficticio para probar copia, no lectura PDF")
            }
        repo.importMedia(Uri.fromFile(original))
        val record = repo.media().single()
        val stored = MediaFiles.file(context, record.filename)
        assertTrue(stored.exists())
        assertArrayEquals(original.readBytes(), stored.readBytes())
        assertEquals("application/pdf", record.mime)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", stored)
        assertEquals("content", uri.scheme)
        assertEquals("${context.packageName}.files", uri.authority)
        assertThrows(IllegalArgumentException::class.java) {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.files",
                File(context.filesDir, "private.txt"),
            )
        }
        repo.renameMedia(record.id, "Mi archivo QA")
        assertEquals("Mi archivo QA", repo.media().single().title)
        repo.close()
        repo = ContentRepository(context)
        repo.initialize()
        assertEquals(1, repo.media().size)
        repo.deleteMedia(record.id)
        assertFalse(stored.exists())
        assertTrue(original.exists())
        assertTrue(repo.media().isEmpty())
    }

    @Test
    fun mediaRejectsTraversalEmptyOrOversizeWithoutLeavingPartialFiles() {
        assertThrows(IllegalArgumentException::class.java) {
            MediaFiles.file(context, "../preferences.xml")
        }
        val empty = File(context.cacheDir, "vacio.pdf").apply { writeBytes(byteArrayOf()) }
        assertThrows(IllegalArgumentException::class.java) { repo.importMedia(Uri.fromFile(empty)) }
        val large = File(context.cacheDir, "grande.pdf")
        java.io.RandomAccessFile(large, "rw").use { it.setLength(MediaFiles.MAX_FILE + 1) }
        assertThrows(IllegalArgumentException::class.java) { repo.importMedia(Uri.fromFile(large)) }
        assertTrue(repo.media().isEmpty())
        assertTrue(MediaFiles.directory(context).listFiles().orEmpty().isEmpty())
        large.delete()
    }

    @Test
    fun resetExplicitlyClearsNewDocumentsAndMediaToo() {
        repo.saveNotebook(note())
        val original = File(context.cacheDir, "reset-qa.pdf").apply { writeText("%PDF-QA") }
        repo.importMedia(Uri.fromFile(original))
        repo.reset()
        assertTrue(repo.notebooks().isEmpty())
        assertTrue(repo.media().isEmpty())
        assertFalse(repo.hasNotebookUndo())
        assertTrue(MediaFiles.directory(context).listFiles().orEmpty().isEmpty())
        assertTrue(original.exists())
        assertEquals(8, repo.entries().size)
    }
}
