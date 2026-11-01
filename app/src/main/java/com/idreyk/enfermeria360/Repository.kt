package com.idreyk.enfermeria360

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

private class LocalDatabase(context: Context) :
    SQLiteOpenHelper(context, "idreyk_local.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE content(kind TEXT NOT NULL,id TEXT NOT NULL,name TEXT NOT NULL,payload TEXT NOT NULL,PRIMARY KEY(kind,id))"
        )
        db.execSQL(
            "CREATE TABLE packages(id TEXT PRIMARY KEY,version INTEGER NOT NULL,title TEXT NOT NULL,imported INTEGER NOT NULL)"
        )
        db.execSQL("CREATE TABLE favorites(key TEXT PRIMARY KEY)")
        db.execSQL(
            "CREATE TABLE notes(id INTEGER PRIMARY KEY AUTOINCREMENT,patient_id TEXT NOT NULL,text TEXT NOT NULL,created INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE practice(id INTEGER PRIMARY KEY AUTOINCREMENT,type TEXT NOT NULL,summary TEXT NOT NULL,created INTEGER NOT NULL)"
        )
        db.execSQL("CREATE TABLE meta(key TEXT PRIMARY KEY,value TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        error("Se requiere una migración explícita para esta versión de base de datos.")
    }
}

class ContentRepository(private val context: Context) {
    private val helper = LocalDatabase(context)

    fun initialize() {
        val seeded =
            helper.readableDatabase.rawQuery("SELECT value FROM meta WHERE key='seed'", null).use {
                it.moveToFirst()
            }
        if (!seeded) {
            val pack =
                ContentParser.parse(
                    context.assets.open("contenido_inicial.json").bufferedReader().use {
                        it.readText()
                    }
                )
            writePackage(pack, false)
            helper.writableDatabase.execSQL(
                "INSERT OR REPLACE INTO meta(key,value) VALUES('seed','1')"
            )
        }
    }

    fun entries(): List<Entry> =
        helper.readableDatabase
            .rawQuery("SELECT kind,id,name,payload FROM content ORDER BY name COLLATE NOCASE", null)
            .use { c ->
                buildList {
                    while (c.moveToNext()) {
                        val o = JSONObject(c.getString(3))
                        add(
                            Entry(
                                c.getString(0),
                                c.getString(1),
                                c.getString(2),
                                o.optString(
                                    if (c.getString(0) == "med") "grupo" else "area",
                                    "General",
                                ),
                                o,
                            )
                        )
                    }
                }
            }

    fun favorites(): Set<String> =
        helper.readableDatabase.rawQuery("SELECT key FROM favorites", null).use { c ->
            buildSet { while (c.moveToNext()) add(c.getString(0)) }
        }

    fun toggleFavorite(key: String) {
        val db = helper.writableDatabase
        if (key in favorites()) db.delete("favorites", "key=?", arrayOf(key))
        else db.insertOrThrow("favorites", null, ContentValues().apply { put("key", key) })
    }

    fun notes(): List<StudyNote> =
        helper.readableDatabase
            .rawQuery("SELECT id,patient_id,text,created FROM notes ORDER BY created DESC", null)
            .use { c ->
                buildList {
                    while (c.moveToNext()) add(
                        StudyNote(c.getLong(0), c.getString(1), c.getString(2), c.getLong(3))
                    )
                }
            }

    fun saveNote(patientId: String, text: String) {
        require(text.isNotBlank() && text.length <= 2000)
        helper.writableDatabase.insertOrThrow(
            "notes",
            null,
            ContentValues().apply {
                put("patient_id", patientId)
                put("text", text.trim())
                put("created", System.currentTimeMillis())
            },
        )
    }

    fun practices(): List<PracticeRecord> =
        helper.readableDatabase
            .rawQuery(
                "SELECT id,type,summary,created FROM practice ORDER BY created DESC LIMIT 100",
                null,
            )
            .use { c ->
                buildList {
                    while (c.moveToNext()) add(
                        PracticeRecord(c.getLong(0), c.getString(1), c.getString(2), c.getLong(3))
                    )
                }
            }

    fun savePractice(type: String, summary: String) {
        require(summary.length <= 3000)
        helper.writableDatabase.insertOrThrow(
            "practice",
            null,
            ContentValues().apply {
                put("type", type)
                put("summary", summary)
                put("created", System.currentTimeMillis())
            },
        )
    }

    fun deletePractice() {
        helper.writableDatabase.delete("practice", null, null)
    }

    fun hasBackup(): Boolean =
        helper.readableDatabase.rawQuery("SELECT 1 FROM meta WHERE key='undo'", null).use {
            it.moveToFirst()
        }

    fun close() = helper.close()

    fun readDocument(uri: Uri): String {
        val result = ByteArrayOutputStream()
        context.contentResolver.openInputStream(uri)?.use { stream ->
            val b = ByteArray(8192)
            while (true) {
                val n = stream.read(b)
                if (n < 0) break
                require(result.size() + n <= ContentParser.MAX_BYTES) { "El archivo supera 2 MB." }
                result.write(b, 0, n)
            }
        } ?: error("No se pudo abrir el archivo seleccionado.")
        return ContentParser.decodeUtf8(result.toByteArray())
    }

    fun preview(raw: String): ImportPreview {
        val pack = ContentParser.parse(raw)
        val existing =
            helper.readableDatabase
                .rawQuery("SELECT version FROM packages WHERE id=?", arrayOf(pack.id))
                .use { if (it.moveToFirst()) it.getLong(0) else 0L }
        require(pack.version > existing) {
            "Ya tienes esa versión o una más reciente. Incrementa version_contenido para actualizar este paquete."
        }
        validateCatalog(pack)
        val keys = entries().map { it.key }.toSet()
        return ImportPreview(
            pack,
            pack.entries.count { it.key !in keys },
            pack.entries.count { it.key in keys },
        )
    }

    private fun snapshot(): JSONObject {
        val now = System.currentTimeMillis()
        val root =
            JSONObject()
                .put("version_esquema", 1)
                .put("tipo_paquete", "respaldo_catalogo")
                .put("id_paquete", "respaldo-$now")
                .put("version_contenido", 1)
                .put("titulo", "Respaldo del catálogo local")
                .put("autor", "Exportación local de iDreyK 360")
                .put("fecha_actualizacion", LocalDate.now().toString())
                .put(
                    "licencia",
                    "Consultar las fuentes y permisos de cada ficha. Exportar no concede derechos adicionales.",
                )
        for ((kind, key) in
            listOf("med" to "medicamentos", "case" to "casos", "safety" to "listas_seguridad")) {
            val a = JSONArray()
            entries().filter { it.kind == kind }.forEach { a.put(it.json) }
            root.put(key, a)
        }
        val packages = JSONArray()
        helper.readableDatabase
            .rawQuery("SELECT id,version,title,imported FROM packages", null)
            .use { c ->
                while (c.moveToNext()) packages.put(
                    JSONObject()
                        .put("id", c.getString(0))
                        .put("version", c.getLong(1))
                        .put("title", c.getString(2))
                        .put("imported", c.getLong(3))
                )
            }
        root.put("_historial_paquetes", packages)
        return root
    }

    fun exportText(): String {
        val raw = snapshot().toString()
        ContentParser.parse(raw)
        return raw
    }

    fun exportDocument(uri: Uri) {
        val bytes = exportText().toByteArray(Charsets.UTF_8)
        context.contentResolver.openOutputStream(uri, "wt")?.use { it.write(bytes) }
            ?: error("No se pudo escribir el respaldo.")
    }

    private fun payload(pack: ContentPackage, entry: Entry): JSONObject {
        val o = JSONObject(entry.json.toString())
        if (!pack.isBackup) {
            o.put("_paquete", pack.id)
                .put("_autor", pack.author)
                .put("_actualizacion", pack.updated)
                .put("_licencia", pack.license)
        }
        return o
    }

    // The complete library obeys the same limits as an import. Therefore every
    // committed state has a readable export and an equally readable undo state.
    private fun validateCatalog(pack: ContentPackage) {
        val merged = entries().associateBy { it.key }.toMutableMap()
        for (e in pack.entries) merged[e.key] = e.copy(json = payload(pack, e))
        val root = snapshot()
        for ((kind, key) in
            listOf("med" to "medicamentos", "case" to "casos", "safety" to "listas_seguridad")) {
            val array = JSONArray()
            merged.values.filter { it.kind == kind }.forEach { array.put(it.json) }
            root.put(key, array)
        }
        val packages = root.getJSONArray("_historial_paquetes")
        val history = JSONArray()
        for (i in 0 until packages.length()) {
            val previous = packages.getJSONObject(i)
            if (previous.getString("id") != pack.id) history.put(previous)
        }
        history.put(
            JSONObject()
                .put("id", pack.id)
                .put("version", pack.version)
                .put("title", pack.title)
                .put("imported", System.currentTimeMillis())
        )
        root.put("_historial_paquetes", history)
        try {
            ContentParser.parse(root.toString())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(
                "El catálogo resultante supera los límites de esta edición o no es válido: ${e.message} No se modificaron tus datos."
            )
        }
    }

    fun writePackage(pack: ContentPackage, makeBackup: Boolean = true) {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            if (makeBackup) preview(pack.raw) else validateCatalog(pack)
            if (makeBackup) {
                // SQLite commits the previous catalog and the new records together.
                // A failed import cannot destroy the previous successful undo point.
                db.insertWithOnConflict(
                        "meta",
                        null,
                        ContentValues().apply {
                            put("key", "undo")
                            put("value", exportText())
                        },
                        SQLiteDatabase.CONFLICT_REPLACE,
                    )
                    .also { check(it != -1L) }
            }
            for (entry in pack.entries) {
                db.insertWithOnConflict(
                        "content",
                        null,
                        ContentValues().apply {
                            put("kind", entry.kind)
                            put("id", entry.id)
                            put("name", entry.name)
                            put("payload", payload(pack, entry).toString())
                        },
                        SQLiteDatabase.CONFLICT_REPLACE,
                    )
                    .also { check(it != -1L) { "No se pudo guardar ${entry.name}." } }
            }
            db.insertWithOnConflict(
                    "packages",
                    null,
                    ContentValues().apply {
                        put("id", pack.id)
                        put("version", pack.version)
                        put("title", pack.title)
                        put("imported", System.currentTimeMillis())
                    },
                    SQLiteDatabase.CONFLICT_REPLACE,
                )
                .also { check(it != -1L) { "No se pudo guardar la versión del paquete." } }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun undoImport() {
        val raw =
            helper.readableDatabase.rawQuery("SELECT value FROM meta WHERE key='undo'", null).use {
                require(it.moveToFirst()) { "No hay una importación anterior para deshacer." }
                it.getString(0)
            }
        val pack = ContentParser.parse(raw)
        val json = JSONObject(raw)
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            db.delete("content", null, null)
            db.delete("packages", null, null)
            pack.entries.forEach { e ->
                db.insertOrThrow(
                    "content",
                    null,
                    ContentValues().apply {
                        put("kind", e.kind)
                        put("id", e.id)
                        put("name", e.name)
                        put("payload", e.json.toString())
                    },
                )
            }
            json.optJSONArray("_historial_paquetes")?.let { a ->
                for (i in 0 until a.length()) {
                    val o = a.getJSONObject(i)
                    db.insertOrThrow(
                        "packages",
                        null,
                        ContentValues().apply {
                            put("id", o.getString("id"))
                            put("version", o.getLong("version"))
                            put("title", o.getString("title"))
                            put("imported", o.getLong("imported"))
                        },
                    )
                }
            }
            db.delete("meta", "key=?", arrayOf("undo"))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun reset() {
        helper.writableDatabase.apply {
            beginTransaction()
            try {
                for (t in
                    listOf("content", "packages", "favorites", "notes", "practice", "meta")) delete(
                    t,
                    null,
                    null,
                )
                setTransactionSuccessful()
            } finally {
                endTransaction()
            }
        }
        initialize()
    }
}

data class LocalState(
    val loading: Boolean = true,
    val entries: List<Entry> = emptyList(),
    val favorites: Set<String> = emptySet(),
    val notes: List<StudyNote> = emptyList(),
    val practices: List<PracticeRecord> = emptyList(),
    val hasBackup: Boolean = false,
)

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    private val repository = ContentRepository(application)
    var data by mutableStateOf(LocalState())
        private set

    var dark by mutableStateOf(prefs.getBoolean("dark", false))
        private set

    var reduced by mutableStateOf(prefs.getBoolean("reduced", false))
        private set

    var largeText by mutableStateOf(prefs.getBoolean("large_text", false))
        private set

    var name by mutableStateOf(prefs.getString("name", "Franklin") ?: "Franklin")
        private set

    var welcomed by mutableStateOf(prefs.getBoolean("welcomed", false))
        private set

    var flashIndex by mutableIntStateOf(prefs.getInt("flash_index", 0).coerceIn(0, 4))
        private set

    var busy by mutableStateOf(false)
        private set

    var message by mutableStateOf<String?>(null)
        private set

    var preview by mutableStateOf<ImportPreview?>(null)
        private set

    init {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { repository.initialize() }
                reload()
            } catch (e: Exception) {
                data = data.copy(loading = false)
                message = "No se pudo abrir el catálogo local: ${e.message}"
            }
        }
    }

    private suspend fun reload() {
        val state =
            withContext(Dispatchers.IO) {
                LocalState(
                    false,
                    repository.entries(),
                    repository.favorites(),
                    repository.notes(),
                    repository.practices(),
                    repository.hasBackup(),
                )
            }
        data = state
    }

    private fun work(success: String, block: () -> Unit) {
        if (busy) return
        busy = true
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { block() }
                reload()
                message = success
            } catch (e: Exception) {
                message = e.message ?: "No se completó la operación. No se enviaron datos."
            } finally {
                busy = false
            }
        }
    }

    fun favorite(key: String) = work("Favoritos actualizados") { repository.toggleFavorite(key) }

    fun note(patientId: String, text: String) =
        work("Nota de práctica guardada en el dispositivo") { repository.saveNote(patientId, text) }

    fun practice(type: String, summary: String) =
        work("Resultado de práctica guardado, sin datos de pacientes") {
            repository.savePractice(type, summary)
        }

    fun clearHistory() = work("Historial de prácticas eliminado") { repository.deletePractice() }

    fun inspect(uri: Uri) {
        if (busy) return
        busy = true
        viewModelScope.launch {
            try {
                preview =
                    withContext(Dispatchers.IO) { repository.preview(repository.readDocument(uri)) }
            } catch (e: Exception) {
                message = e.message ?: "No se pudo revisar el archivo."
            } finally {
                busy = false
            }
        }
    }

    fun importConfirmed() {
        val p = preview ?: return
        preview = null
        work("Paquete importado. El contenido conserva su estado de revisión declarado.") {
            repository.writePackage(p.content)
        }
    }

    fun cancelImport() {
        preview = null
    }

    fun export(uri: Uri) =
        work("Catálogo exportado. No incluye notas ni datos de pacientes.") {
            repository.exportDocument(uri)
        }

    fun undo() = work("Catálogo anterior restaurado") { repository.undoImport() }

    fun reset() = work("Biblioteca restablecida a los ejemplos iniciales") { repository.reset() }

    fun updateDark(value: Boolean) {
        dark = value
        prefs.edit().putBoolean("dark", value).apply()
    }

    fun updateReduced(value: Boolean) {
        reduced = value
        prefs.edit().putBoolean("reduced", value).apply()
    }

    fun setLarge(value: Boolean) {
        largeText = value
        prefs.edit().putBoolean("large_text", value).apply()
    }

    fun updateName(value: String) {
        name = value.trim().take(24).ifEmpty { "Franklin" }
        prefs.edit().putString("name", name).apply()
    }

    fun acceptWelcome() {
        welcomed = true
        prefs.edit().putBoolean("welcomed", true).apply()
    }

    fun setFlash(index: Int) {
        flashIndex = index.coerceIn(0, 4)
        prefs.edit().putInt("flash_index", flashIndex).apply()
    }

    fun notify(text: String) {
        message = text
    }

    fun clearMessage() {
        message = null
    }
}
