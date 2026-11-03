package com.idreyk.enfermeria360

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
    SQLiteOpenHelper(context, "idreyk_local.db", null, 2) {
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
        createVersion2(db)
    }

    private fun createVersion2(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE notebooks(id TEXT PRIMARY KEY,kind TEXT NOT NULL,owner TEXT NOT NULL,title TEXT NOT NULL,payload TEXT NOT NULL,modified INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE media(id TEXT PRIMARY KEY,title TEXT NOT NULL,mime TEXT NOT NULL,filename TEXT NOT NULL,bytes INTEGER NOT NULL,created INTEGER NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        check(oldVersion == 1 && newVersion == 2) {
            "Ruta de migración no soportada; no se borraron datos."
        }
        // SQLiteOpenHelper encloses this migration in its own transaction. v1 tables are untouched.
        createVersion2(db)
    }
}

class ContentRepository(private val context: Context) {
    private val masterLoad: MasterLoad by lazy {
        try { MasterLoad(MasterCatalog.load(context)) }
        catch (e: Exception) { MasterLoad(error = "No se pudo cargar la base educativa: ${e.message}") }
    }
    fun masterCatalog(): MasterLoad = masterLoad

    fun learningRecords(): Map<String, LearningProgress> =
        helper.readableDatabase.rawQuery("SELECT key,value FROM meta WHERE key LIKE ?", arrayOf("learning:%")).use { c ->
            buildMap {
                while (c.moveToNext()) {
                    val progress = LearningProgress.decode(c.getString(1))
                    require(c.getString(0) == "learning:${progress.route}") { "Progreso local inconsistente." }
                    put(progress.route, progress)
                }
            }
        }

    fun saveLearning(progress: LearningProgress) {
        val master = masterLoad.catalog ?: throw IllegalStateException(masterLoad.error)
        master.validate(progress)
        val old = learningRecords()[progress.route]
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            check(db.insertWithOnConflict("meta", null, ContentValues().apply {
                put("key", "learning:${progress.route}")
                put("value", progress.encode())
            }, SQLiteDatabase.CONFLICT_REPLACE) != -1L)
            if (progress.completed && (old?.completed != true || old.version != progress.version)) {
                val case = master.cases.firstOrNull { "simulacion/${it.id}" == progress.route }
                if (case != null) savePractice("Caso sintético · ${case.title}",
                    "EJERCICIO EDUCATIVO. ${case.score(progress.answers)} de ${case.questions.size} respuestas correctas en el recorrido. No acredita competencia clínica. Versión ${progress.version}.")
                else if (progress.route.startsWith("atlas/")) {
                    val device = master.devices.single { "atlas/${it.id}" == progress.route }
                    savePractice("Identificación · ${device.name}",
                        "EJERCICIO EDUCATIVO. ${device.score(progress.answers)} de ${device.quizTargets.size}. Imagen generada; revisión profesional pendiente. No acredita competencia clínica. Versión ${progress.version}.")
                }
                else savePractice("Lectura educativa", "${master.topics.single { "tema/${it.id}" == progress.route }.title}. Marcada como leída por el usuario; no acredita comprensión ni competencia. Versión ${progress.version}.")
            }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }

    fun clearLearning() { helper.writableDatabase.delete("meta", "key LIKE ?", arrayOf("learning:%")) }

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
        val known = media().map { it.filename }.toSet()
        MediaFiles.directory(context)
            .listFiles()
            ?.filter { file ->
                file.name.startsWith("temp_") ||
                    (Regex("[a-f0-9-]{36}\\.[a-z0-9]{2,5}").matches(file.name) &&
                        file.name !in known)
            }
            ?.forEach { it.delete() }
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

    fun saveLocalEntry(kind: String, edited: JSONObject, author: String, license: String) {
        require(kind in CONTENT_ARRAYS) { "Tipo de ficha no reconocido." }
        val id = edited.optString("id")
        require(NotebookCodec.slug(id)) { "ID de ficha inválido." }
        val original = entries().find { it.kind == kind && it.id == id }
        val draft = CatalogAuthoring.draft(original?.json, edited)
        val hash =
            java.security.MessageDigest.getInstance("SHA-256")
                .digest("$kind/$id".toByteArray())
                .take(16)
                .joinToString("") { "%02x".format(it) }
        val packageId = "edicion-local-$hash"
        val previous =
            helper.readableDatabase
                .rawQuery("SELECT version FROM packages WHERE id=?", arrayOf(packageId))
                .use { if (it.moveToFirst()) it.getLong(0) else 0L }
        val root =
            JSONObject()
                .put("version_esquema", 3)
                .put("id_paquete", packageId)
                .put("version_contenido", previous + 1)
                .put(
                    "titulo",
                    "Edición local · " +
                        draft.optString(if (kind == "med") "nombre" else "titulo").take(120),
                )
                .put("autor", author)
                .put("fecha_actualizacion", LocalDate.now().toString())
                .put("licencia", license)
                .put(CONTENT_ARRAYS.getValue(kind), JSONArray().put(draft))
        writePackage(ContentParser.parse(root.toString()))
    }

    fun deleteEntry(key: String) {
        val entry =
            entries().find { it.key == key }
                ?: throw IllegalArgumentException("La ficha ya no existe.")
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
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
            db.delete("content", "kind=? AND id=?", arrayOf(entry.kind, entry.id))
            ContentParser.parse(snapshot().toString())
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun notebooks(): List<NotebookRecord> =
        helper.readableDatabase
            .rawQuery("SELECT payload FROM notebooks ORDER BY modified DESC,id", null)
            .use { c ->
                buildList {
                    while (c.moveToNext()) add(NotebookCodec.record(JSONObject(c.getString(0))))
                }
            }

    fun notebookText(): String = NotebookCodec.encode(notebooks())

    fun hasNotebookUndo(): Boolean =
        helper.readableDatabase.rawQuery("SELECT 1 FROM meta WHERE key='notebook_undo'", null).use {
            it.moveToFirst()
        }

    private fun backupNotebook(db: SQLiteDatabase) {
        db.insertWithOnConflict(
                "meta",
                null,
                ContentValues().apply {
                    put("key", "notebook_undo")
                    put("value", notebookText())
                },
                SQLiteDatabase.CONFLICT_REPLACE,
            )
            .also { check(it != -1L) }
    }

    private fun putNotebook(db: SQLiteDatabase, r: NotebookRecord) {
        db.insertWithOnConflict(
                "notebooks",
                null,
                ContentValues().apply {
                    put("id", r.id)
                    put("kind", r.kind)
                    put("owner", r.owner)
                    put("title", r.title)
                    put("payload", r.json().toString())
                    put("modified", r.modified)
                },
                SQLiteDatabase.CONFLICT_REPLACE,
            )
            .also { check(it != -1L) }
    }

    fun validateNotebooks(incoming: List<NotebookRecord>) {
        NotebookCodec.encode(incoming)
        val merged = notebooks().associateBy { it.id }.toMutableMap()
        incoming.forEach { r ->
            require(merged[r.id]?.kind.let { it == null || it == r.kind }) {
                "Un ID existente no puede cambiar de tipo de documento."
            }
            merged[r.id] = r
        }
        NotebookCodec.encode(merged.values.toList())
    }

    fun mergeNotebooks(incoming: List<NotebookRecord>) {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            validateNotebooks(incoming)
            backupNotebook(db)
            incoming.forEach { putNotebook(db, NotebookCodec.record(it.json())) }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun exportEducationalMarkdown(uri: Uri, record: NotebookRecord) = NotebookEducation.write(context, uri, record)

    fun importStudyMarkdown(uri: Uri) {
        val (title, body) = NotebookEducation.readMarkdown(context, uri)
        saveNotebook(NotebookRecord("local-" + java.util.UUID.randomUUID().toString(), "note", "", title,
            JSONObject().put("contenido", body).put("etiquetas", "Markdown importado localmente; origen: $title; revisión clínica no acreditada".take(300)),
            System.currentTimeMillis()))
    }

    fun saveNotebook(record: NotebookRecord) =
        mergeNotebooks(listOf(record.copy(modified = System.currentTimeMillis())))

    fun deleteNotebook(id: String) {
        require(notebooks().any { it.id == id }) { "El documento ya no existe." }
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            backupNotebook(db)
            db.delete("notebooks", "id=?", arrayOf(id))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun undoNotebook() {
        val raw =
            helper.readableDatabase
                .rawQuery("SELECT value FROM meta WHERE key='notebook_undo'", null)
                .use {
                    require(it.moveToFirst()) { "No hay cambios de cuaderno para deshacer." }
                    it.getString(0)
                }
        val records = NotebookCodec.decode(raw)
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            db.delete("notebooks", null, null)
            records.forEach { putNotebook(db, it) }
            db.delete("meta", "key=?", arrayOf("notebook_undo"))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun exportNotebooks(uri: Uri) {
        context.contentResolver.openOutputStream(uri, "wt")?.use {
            it.write(notebookText().toByteArray(Charsets.UTF_8))
        } ?: error("No se pudo exportar el cuaderno.")
    }

    fun media(): List<MediaRecord> =
        helper.readableDatabase
            .rawQuery(
                "SELECT id,title,mime,filename,bytes,created FROM media ORDER BY created DESC",
                null,
            )
            .use { c ->
                buildList {
                    while (c.moveToNext()) add(
                        MediaRecord(
                            c.getString(0),
                            c.getString(1),
                            c.getString(2),
                            c.getString(3),
                            c.getLong(4),
                            c.getLong(5),
                        )
                    )
                }
            }

    fun importMedia(uri: Uri) {
        require(media().size < 200) { "Máximo 200 archivos en la biblioteca." }
        val record =
            MediaFiles.copy(context, uri, MediaFiles.MAX_LIBRARY - media().sumOf { it.bytes })
        try {
            helper.writableDatabase.insertOrThrow(
                "media",
                null,
                ContentValues().apply {
                    put("id", record.id)
                    put("title", record.title)
                    put("mime", record.mime)
                    put("filename", record.filename)
                    put("bytes", record.bytes)
                    put("created", record.created)
                },
            )
        } catch (e: Exception) {
            MediaFiles.delete(context, record)
            throw e
        }
    }

    fun renameMedia(id: String, title: String) {
        require(title.isNotBlank() && title.length <= 140) {
            "Escribe un título de 1 a 140 caracteres."
        }
        require(
            helper.writableDatabase.update(
                "media",
                ContentValues().apply { put("title", title.trim()) },
                "id=?",
                arrayOf(id),
            ) == 1
        ) {
            "El archivo ya no existe."
        }
    }

    fun deleteMedia(id: String) {
        val record =
            media().find { it.id == id }
                ?: throw IllegalArgumentException("El archivo ya no existe.")
        MediaFiles.delete(context, record)
        helper.writableDatabase.delete("media", "id=?", arrayOf(id))
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
        val current = entries()
        val keys = current.map { it.key }.toSet()
        return ImportPreview(
            pack,
            pack.entries.count { it.key !in keys },
            pack.entries.count { it.key in keys },
            ImportReview.compare(pack.entries, current),
            ImportReview.fingerprint(current, pack, existing),
        )
    }

    fun applyReviewedImport(review: ImportPreview, replacementsAcknowledged: Boolean) {
        // Parse the immutable text again: exposed JSONObject instances are not the source of truth.
        writePackage(
            ContentParser.parse(review.content.raw),
            expectedFingerprint = review.fingerprint,
            replacementsAcknowledged = replacementsAcknowledged,
        )
    }

    private fun snapshot(): JSONObject {
        val now = System.currentTimeMillis()
        val root =
            JSONObject()
                .put("version_esquema", 3)
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
        for ((kind, key) in CONTENT_ARRAYS) {
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
        for ((kind, key) in CONTENT_ARRAYS) {
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

    fun writePackage(
        pack: ContentPackage,
        makeBackup: Boolean = true,
        expectedFingerprint: String? = null,
        replacementsAcknowledged: Boolean = false,
    ) {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            if (makeBackup) {
                val fresh = preview(pack.raw)
                if (expectedFingerprint != null) {
                    require(fresh.fingerprint == expectedFingerprint) {
                        "El catálogo cambió desde la vista previa. Revisa de nuevo el paquete; no se modificaron tus datos."
                    }
                    require(fresh.updateCount == 0 || replacementsAcknowledged) {
                        "Confirma que revisaste las fichas que serán sustituidas."
                    }
                }
            } else validateCatalog(pack)
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
                    listOf(
                        "content",
                        "packages",
                        "favorites",
                        "notes",
                        "practice",
                        "meta",
                        "notebooks",
                        "media",
                    )) delete(t, null, null)
                setTransactionSuccessful()
            } finally {
                endTransaction()
            }
        }
        MediaFiles.directory(context).listFiles()?.forEach { it.delete() }
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
    val notebooks: List<NotebookRecord> = emptyList(),
    val media: List<MediaRecord> = emptyList(),
    val hasNotebookUndo: Boolean = false,
    val master: MasterLoad = MasterLoad(),
    val learning: Map<String, LearningProgress> = emptyMap(),
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AppViewModel(application: Application) : AndroidViewModel(application) {
    // A serial I/O lane also lets lifecycle cleanup wait behind any active transaction.
    private val io = Dispatchers.IO.limitedParallelism(1)
    private val prefs = application.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    private val repository = ContentRepository(application)
    var data by mutableStateOf(LocalState())
        private set

    var dark by mutableStateOf(prefs.getBoolean("dark", false))
        private set

    var reduced by mutableStateOf(prefs.getBoolean("reduced", false))
        private set

    var performanceMode by mutableStateOf(prefs.getString("performance", "auto") ?: "auto")
        private set

    fun setPerformance(mode: String) {
        require(mode in PerformancePolicy.modes)
        performanceMode = mode
        prefs.edit().putString("performance", mode).apply()
    }

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

    val cloudConfig = CloudConfig(BuildConfig.CLOUD_URL, BuildConfig.CLOUD_PUBLISHABLE_KEY)
    private val cloud = CloudService(cloudConfig)
    private val sessionVault = SessionVault(application)
    private var cloudJob: kotlinx.coroutines.Job? = null
    var cloudSession by mutableStateOf<CloudSession?>(null)
        private set

    var cloudStatus by
        mutableStateOf(
            if (cloudConfig.configured) "Proyecto configurado; solicita la operación que necesites."
            else "Proyecto cloud sin configurar. Tu biblioteca local sigue disponible."
        )
        private set

    var nextCodeAt by mutableLongStateOf(0L)
        private set

    var lastCloudCheck by mutableLongStateOf(prefs.getLong("cloud_last_check", 0))
        private set

    private fun cloudWork(block: suspend () -> Unit) {
        if (busy) return
        busy = true
        cloudJob =
            viewModelScope.launch {
                try {
                    block()
                } catch (cancelled: kotlinx.coroutines.CancellationException) {
                    throw cancelled
                } catch (failure: CloudFailure) {
                    cloudStatus = failure.message.orEmpty()
                } catch (invalid: IllegalArgumentException) {
                    cloudStatus = invalid.message ?: "Datos no admitidos."
                } catch (invalid: org.json.JSONException) {
                    cloudStatus =
                        "El servicio devolvió una respuesta no válida. No cambió tu biblioteca."
                } catch (failure: java.io.IOException) {
                    cloudStatus =
                        "No se pudo completar la conexión. La biblioteca guardada sigue disponible."
                } catch (failure: Exception) {
                    cloudStatus =
                        "No se completó la operación de cuenta o conexión. Los datos locales permanecen disponibles."
                } finally {
                    busy = false
                }
            }
    }

    fun cancelCloud() {
        cloud.cancel()
        cloudJob?.cancel()
        cloudStatus = "Operación cancelada. No se confirmó ninguna importación."
    }

    fun sendCloudCode(email: String) = cloudWork {
        require(System.currentTimeMillis() >= nextCodeAt) {
            "Espera al menos un minuto entre solicitudes de código."
        }
        withContext(io) { cloud.sendCode(email.trim()) }
        nextCodeAt = System.currentTimeMillis() + 60_000
        cloudStatus =
            "Si la solicitud es admitida, recibirás un código. Revisa tu correo; solicitarlo todavía no inicia sesión."
    }

    fun verifyCloudCode(email: String, code: String) = cloudWork {
        val session = withContext(io) { cloud.verify(email.trim(), code.trim()) }
        try {
            withContext(io) { sessionVault.save(cloudConfig.url, session) }
            cloudSession = session
            cloudStatus =
                "Correo verificado y sesión protegida en este dispositivo. No se subieron notas ni datos de pacientes."
        } catch (cancelled: kotlinx.coroutines.CancellationException) {
            withContext(kotlinx.coroutines.NonCancellable + io) { sessionVault.clear() }
            throw cancelled
        }
    }

    fun refreshCloudSession() = cloudWork {
        val previous = cloudSession ?: throw IllegalArgumentException("No hay una sesión guardada.")
        val renewed = withContext(io) { cloud.refresh(previous) }
        withContext(io) { sessionVault.save(cloudConfig.url, renewed) }
        cloudSession = renewed
        cloudStatus =
            "Sesión validada con el servidor. La cuenta no tiene permisos editoriales por registrarse."
    }

    fun signOutCloud() = cloudWork {
        val previous = cloudSession
        withContext(io) { sessionVault.clear() }
        cloudSession = null
        val notified =
            withContext(io) {
                if (previous == null) true else runCatching { cloud.logout(previous) }.isSuccess
            }
        cloudStatus =
            if (notified) "Sesión cerrada en este dispositivo."
            else
                "Sesión borrada de este dispositivo. No se pudo notificar al servidor; no se cerraron otras sesiones."
    }

    fun checkCloudCatalog() = cloudWork {
        val raw = withContext(io) { cloud.latest() }
        lastCloudCheck = System.currentTimeMillis()
        prefs.edit().putLong("cloud_last_check", lastCloudCheck).apply()
        if (raw == null) {
            cloudStatus = "No hay publicaciones disponibles. La biblioteca local permanece intacta."
        } else {
            preview = withContext(io) { repository.preview(raw) }
            cloudStatus =
                "Publicación descargada e integridad comprobada. Revisa los cambios y confirma antes de guardarlos."
        }
    }

    init {
        viewModelScope.launch {
            try {
                withContext(io) { repository.initialize() }
                reload()
                try {
                    cloudSession = withContext(io) { sessionVault.load(cloudConfig.url) }
                } catch (failure: Exception) {
                    cloudStatus =
                        "La sesión anterior no está disponible; inicia sesión de nuevo si lo necesitas."
                }
            } catch (e: Exception) {
                data = data.copy(loading = false)
                message = "No se pudo abrir el catálogo local: ${e.message}"
            }
        }
    }

    private suspend fun reload() {
        val state =
            withContext(io) {
                LocalState(
                    false,
                    repository.entries(),
                    repository.favorites(),
                    repository.notes(),
                    repository.practices(),
                    repository.hasBackup(),
                    repository.notebooks(),
                    repository.media(),
                    repository.hasNotebookUndo(),
                    repository.masterCatalog(),
                    repository.learningRecords(),
                )
            }
        data = state
    }

    private fun work(success: String, onSuccess: () -> Unit = {}, notifySuccess: Boolean = true, block: () -> Unit) {
        if (busy) return
        busy = true
        viewModelScope.launch {
            try {
                withContext(io) { block() }
                reload()
                if (notifySuccess) message = success
                busy = false
                onSuccess()
            } catch (e: Exception) {
                message = e.message ?: "No se completó la operación. No se enviaron datos."
            } finally {
                busy = false
            }
        }
    }

    fun favorite(key: String) = work("Favoritos actualizados") { repository.toggleFavorite(key) }

    fun saveLearning(progress: LearningProgress, onSaved: () -> Unit = {}) =
        // Identification already has persisted inline feedback; a success Snackbar would cover Next.
        // Errors still surface through work(), and other learning flows retain their notifications.
        work("Progreso guardado solo en el dispositivo", onSaved, notifySuccess = !progress.route.startsWith("atlas/")) {
            repository.saveLearning(progress)
        }

    fun clearLearning() = work("Progreso eliminado; el historial y los documentos se conservan") { repository.clearLearning() }


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
                preview = withContext(io) { repository.preview(repository.readDocument(uri)) }
            } catch (e: Exception) {
                message = e.message ?: "No se pudo revisar el archivo."
            } finally {
                busy = false
            }
        }
    }

    fun importConfirmed(replacementsAcknowledged: Boolean = false) {
        if (busy) return
        val p = preview ?: return
        if (p.updateCount > 0 && !replacementsAcknowledged) {
            message = "Revisa y acepta expresamente las sustituciones antes de importar."
            return
        }
        preview = null
        work("Paquete importado. El contenido conserva su estado de revisión declarado.") {
            repository.applyReviewedImport(p, replacementsAcknowledged)
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

    fun saveLocalEntry(kind: String, json: JSONObject, license: String, onSaved: () -> Unit) =
        work("Ficha local guardada como borrador", onSaved) {
            repository.saveLocalEntry(kind, json, name, license)
        }

    fun deleteEntry(key: String, onDeleted: () -> Unit) =
        work("Ficha eliminada; puedes deshacer desde Biblioteca local", onDeleted) {
            repository.deleteEntry(key)
        }

    fun exportEducationalMarkdown(uri: Uri, record: NotebookRecord) =
        work("Documento educativo exportado en Markdown") { repository.exportEducationalMarkdown(uri, record) }

    fun importStudyMarkdown(uri: Uri) =
        work("Apunte Markdown importado como texto personal sin revisión clínica") { repository.importStudyMarkdown(uri) }

    fun saveNotebook(record: NotebookRecord, onSaved: () -> Unit) =
        work("Documento de práctica guardado", onSaved) { repository.saveNotebook(record) }

    fun deleteNotebook(id: String, onDeleted: () -> Unit = {}) =
        work("Documento eliminado del cuaderno", onDeleted) { repository.deleteNotebook(id) }

    fun undoNotebook() = work("Último cambio de cuaderno deshecho") { repository.undoNotebook() }

    fun exportNotebook(uri: Uri) =
        work("Cuaderno exportado: planes y apuntes; no incluye archivos multimedia") {
            repository.exportNotebooks(uri)
        }

    var notebookPreview by mutableStateOf<List<NotebookRecord>?>(null)
        private set

    fun previewNotebook(uri: Uri) {
        if (busy) return
        busy = true
        viewModelScope.launch {
            try {
                notebookPreview =
                    withContext(io) {
                        NotebookCodec.decode(repository.readDocument(uri)).also {
                            repository.validateNotebooks(it)
                        }
                    }
            } catch (e: Exception) {
                message = e.message ?: "No se pudo revisar el cuaderno."
            } finally {
                busy = false
            }
        }
    }

    fun cancelNotebookImport() {
        notebookPreview = null
    }

    fun importNotebookConfirmed() {
        val records = notebookPreview ?: return
        notebookPreview = null
        work("Cuaderno importado; puedes deshacer el último cambio") {
            repository.mergeNotebooks(records)
        }
    }

    fun importMedia(uri: Uri) =
        work("Copia local añadida a la biblioteca") { repository.importMedia(uri) }

    fun renameMedia(id: String, title: String, onSaved: () -> Unit) =
        work("Título actualizado", onSaved) { repository.renameMedia(id, title) }

    fun deleteMedia(id: String) =
        work("Copia local eliminada; el archivo original no se modificó") {
            repository.deleteMedia(id)
        }

    override fun onCleared() {
        cloud.cancel()
        kotlinx.coroutines.CoroutineScope(io).launch { repository.close() }
        super.onCleared()
    }

    fun clearMessage() {
        message = null
    }
}
