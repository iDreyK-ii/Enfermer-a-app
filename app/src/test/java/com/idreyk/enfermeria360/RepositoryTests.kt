package com.idreyk.enfermeria360

import android.content.Context
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

/** SQLite/repository checks in a simulated Android 9 framework (not a phone test). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class RepositoryTests {
    private lateinit var context: Context
    private lateinit var repo: ContentRepository

    @Before
    fun create() {
        context = RuntimeEnvironment.getApplication()
        context.deleteDatabase("idreyk_local.db")
        repo = ContentRepository(context)
        repo.initialize()
    }

    @After
    fun close() {
        repo.close()
    }

    private fun root(id: String = "paquete-prueba", version: Int = 1): JSONObject =
        JSONObject()
            .put("version_esquema", 1)
            .put("id_paquete", id)
            .put("version_contenido", version)
            .put("titulo", "Paquete ficticio de pruebas")
            .put("autor", "Autor de prueba")
            .put("fecha_actualizacion", "2026-11-01")
            .put("licencia", "Datos ficticios de prueba")
            .put("medicamentos", JSONArray())
            .put("casos", JSONArray())
            .put("listas_seguridad", JSONArray())

    private fun drug(id: String, text: String = ""): JSONObject =
        JSONObject()
            .put("id", id)
            .put("nombre", "Ficha ficticia $id")
            .put("grupo", "Prueba")
            .put("revision", "borrador")
            .put("secciones", JSONObject().put("descripcion", text))

    private fun pack(
        id: String = "paquete-prueba",
        version: Int = 1,
        text: String = "ACTUALIZACIÓN FICTICIA",
    ): ContentPackage =
        ContentParser.parse(
            root(id, version)
                .put("medicamentos", JSONArray().put(drug("paracetamol", text)))
                .toString()
        )

    @Test
    fun initialLibraryIsLocalAndRepeatable() {
        assertEquals(8, repo.entries().size)
        repo.initialize()
        assertEquals(8, repo.entries().size)
        assertFalse(repo.hasBackup())
    }

    @Test
    fun previewDoesNotWriteAndCountsReplacements() {
        val preview = repo.preview(pack().raw)
        assertEquals(1, preview.updateCount)
        assertEquals(0, preview.newCount)
        assertEquals("", repo.entries().first { it.id == "paracetamol" }.sections["descripcion"])
        assertFalse(repo.hasBackup())
    }

    @Test
    fun versionsCannotGoBackwards() {
        repo.writePackage(pack(version = 2))
        assertThrows(IllegalArgumentException::class.java) { repo.preview(pack(version = 1).raw) }
        assertThrows(IllegalArgumentException::class.java) { repo.writePackage(pack(version = 2)) }
        repo.writePackage(pack(version = 3, text = "TERCERA REVISIÓN FICTICIA"))
        assertEquals(
            "TERCERA REVISIÓN FICTICIA",
            repo.entries().first { it.id == "paracetamol" }.sections["descripcion"],
        )
    }

    @Test
    fun notesFavoritesAndExercisesSurviveReopening() {
        repo.saveNote("mario", "Nota ficticia A")
        repo.saveNote("rosa", "Nota ficticia B")
        repo.toggleFavorite("med/paracetamol")
        repo.savePractice("Ejemplo", "Operación ficticia")
        repo.close()
        repo = ContentRepository(context)
        repo.initialize()
        assertEquals("Nota ficticia A", repo.notes().single { it.patientId == "mario" }.text)
        assertEquals("Nota ficticia B", repo.notes().single { it.patientId == "rosa" }.text)
        assertTrue("med/paracetamol" in repo.favorites())
        assertEquals(1, repo.practices().size)
    }

    @Test
    fun undoRestoresCatalogAndVersionButNotPracticeData() {
        repo.saveNote("mario", "No se debe eliminar esta nota ficticia")
        repo.toggleFavorite("med/paracetamol")
        repo.savePractice("Prueba", "Práctica ficticia")
        repo.writePackage(pack())
        assertTrue(repo.hasBackup())
        assertEquals(
            "ACTUALIZACIÓN FICTICIA",
            repo.entries().first { it.id == "paracetamol" }.sections["descripcion"],
        )
        repo.undoImport()
        assertEquals("", repo.entries().first { it.id == "paracetamol" }.sections["descripcion"])
        assertFalse(repo.hasBackup())
        assertEquals(1, repo.notes().size)
        assertEquals(1, repo.practices().size)
        assertTrue("med/paracetamol" in repo.favorites())
        assertEquals(1, repo.preview(pack().raw).updateCount)
    }

    @Test
    fun rejectedAggregatePreservesPreviousUndoPoint() {
        repo.writePackage(pack())
        val records = JSONArray()
        for (i in 1..300) records.put(drug("nuevo-$i"))
        val tooMany =
            ContentParser.parse(root("muchos-registros").put("medicamentos", records).toString())
        assertThrows(IllegalArgumentException::class.java) { repo.writePackage(tooMany) }
        assertEquals(8, repo.entries().size)
        assertEquals(
            "ACTUALIZACIÓN FICTICIA",
            repo.entries().first { it.id == "paracetamol" }.sections["descripcion"],
        )
        repo.undoImport()
        assertEquals("", repo.entries().first { it.id == "paracetamol" }.sections["descripcion"])
    }

    @Test
    fun aggregateByteLimitKeepsExportsReadable() {
        val records = JSONArray()
        for (i in 1..6) {
            val fields = JSONObject()
            for (key in DRUG_FIELDS.keys) fields.put(key, "x".repeat(15000))
            records.put(drug("extenso-$i").put("secciones", fields))
        }
        repo.writePackage(
            ContentParser.parse(root("biblioteca-extensa").put("medicamentos", records).toString())
        )
        val more =
            JSONArray()
                .put(JSONObject(records.getJSONObject(0).toString()).put("id", "excede-limite"))
        val exceeding =
            ContentParser.parse(root("excede-bytes").put("medicamentos", more).toString())
        assertThrows(IllegalArgumentException::class.java) { repo.writePackage(exceeding) }
        val exported = repo.exportText()
        assertTrue(exported.toByteArray(Charsets.UTF_8).size <= ContentParser.MAX_BYTES)
        assertEquals(14, ContentParser.parse(exported).entries.size)
        repo.undoImport()
        assertEquals(8, repo.entries().size)
    }

    @Test
    fun exportedCatalogKeepsOriginalAttributionWithoutNotes() {
        repo.writePackage(pack())
        repo.saveNote("mario", "NOTA_FICTICIA_EXCLUSIVA_QA_437")
        repo.savePractice("Ejercicio", "PRACTICA_FICTICIA_EXCLUSIVA_QA_887")
        val exported = repo.exportText()
        assertFalse(exported.contains("NOTA_FICTICIA_EXCLUSIVA_QA_437"))
        assertFalse(exported.contains("PRACTICA_FICTICIA_EXCLUSIVA_QA_887"))
        val copy = ContentParser.parse(exported)
        assertTrue(copy.isBackup)
        repo.reset()
        repo.writePackage(copy)
        val restored = repo.entries().first { it.id == "paracetamol" }
        assertEquals("ACTUALIZACIÓN FICTICIA", restored.sections["descripcion"])
        assertEquals("Autor de prueba", restored.json.getString("_autor"))
        assertEquals("Datos ficticios de prueba", restored.json.getString("_licencia"))
        assertEquals("paquete-prueba", restored.json.getString("_paquete"))
    }

    @Test
    fun resetClearsOnlyLocalPracticeAndCatalog() {
        repo.saveNote("mario", "Ejemplo")
        repo.savePractice("Ejemplo", "Dato ficticio")
        repo.toggleFavorite("med/paracetamol")
        repo.writePackage(pack())
        repo.reset()
        assertEquals(8, repo.entries().size)
        assertTrue(repo.notes().isEmpty())
        assertTrue(repo.practices().isEmpty())
        assertTrue(repo.favorites().isEmpty())
        assertFalse(repo.hasBackup())
    }
}
