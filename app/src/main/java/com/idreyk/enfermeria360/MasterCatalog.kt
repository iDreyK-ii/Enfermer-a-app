package com.idreyk.enfermeria360

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Offline educational assets are distinct from the editable, reviewed catalog packages. */
data class KnowledgeTopic(val id: String, val title: String, val area: String, val level: String,
    val tags: List<String>, val markdown: String, val status: String, val author: String, val sources: List<Reference>)
data class NominalMedicine(val id: String, val name: String, val kind: String, val status: String,
    val primary: String, val relations: List<String>, val aliases: List<String>, val formulations: List<String>,
    val lines: List<Int>, val notes: List<String>, val existingId: String)
data class AtlasShape(val kind: String, val points: List<Float>)
data class AtlasPart(val id: String, val name: String, val function: String, val safety: String,
    val x: Float, val y: Float, val shapes: List<AtlasShape>)
data class AtlasDevice(val id: String, val name: String, val area: String, val modelNote: String,
    val parts: List<AtlasPart>, val sources: List<Reference>)
data class CaseQuestion(val scene: String, val prompt: String, val options: List<String>, val correct: Int,
    val feedback: String, val rubric: String)
data class SyntheticCase(val id: String, val title: String, val area: String, val intro: String,
    val questions: List<CaseQuestion>, val sources: List<Reference>, val status: String) {
    fun score(answers: List<Int>): Int {
        require(answers.size <= questions.size)
        answers.forEachIndexed { i, answer -> require(answer in questions[i].options.indices) }
        return answers.indices.count { answers[it] == questions[it].correct }
    }
}
data class EducationalChecklist(val id: String, val title: String, val scope: String, val official: Boolean,
    val items: List<String>, val sources: List<Reference>)
data class ModuleState(val route: String, val name: String, val status: String, val note: String)
data class MasterSearchItem(val route: String, val title: String, val subtitle: String, val kind: String,
    val searchable: String, val area: String = "", val level: String = "") {
    fun matches(query: String): Boolean {
        val haystack = normalized(searchable)
        return normalized(query).trim().split(Regex("\\s+")).all { it in haystack }
    }
}
data class MasterLoad(val catalog: MasterCatalog? = null, val error: String? = null)

data class MasterCatalog(val version: String, val date: String, val sourceHash: String,
    val topics: List<KnowledgeTopic>, val inventory: List<NominalMedicine>, val devices: List<AtlasDevice>,
    val cases: List<SyntheticCase>, val checklists: List<EducationalChecklist>, val modules: List<ModuleState>) {
    val searchItems: List<MasterSearchItem> by lazy {
        topics.map { MasterSearchItem("tema/${it.id}", it.title, "${it.area} · ${it.level}", "Tema",
            "${it.title} ${it.area} ${it.level} ${it.tags.joinToString(" ")} ${it.markdown}", it.area, it.level) } +
        inventory.map { MasterSearchItem("nominal/${it.id}", it.name, "${it.kind} · ${it.status}", "Inventario",
            "${it.name} ${it.kind} ${it.primary} ${it.relations.joinToString(" ")} ${it.aliases.joinToString(" ")} ${it.formulations.joinToString(" ")}", "Farmacología") } +
        devices.map { MasterSearchItem("dispositivo/${it.id}", it.name, "Atlas · esquema genérico", "Dispositivo",
            "${it.name} ${it.modelNote} ${it.parts.joinToString(" ") { p -> p.name + " " + p.function + " " + p.safety }}", it.area) } +
        cases.map { MasterSearchItem("simulacion/${it.id}", it.title, "${it.area} · caso sintético", "Caso",
            "${it.title} ${it.area} ${it.intro} ${it.questions.joinToString(" ") { q -> q.scene + " " + q.prompt }}", it.area) } +
        checklists.map { MasterSearchItem("lista/${it.id}", it.title, "${it.items.size} ítems · ${if (it.official) "institucional" else "propia"}", "Lista",
            "${it.title} ${it.items.size} correctos ${it.scope} ${it.items.joinToString(" ")}", "Bioseguridad") }
    }
    fun search(query: String, area: String = "", level: String = "", kind: String = ""): List<MasterSearchItem> =
        searchItems.filter { (area.isEmpty() || it.area == area) && (level.isEmpty() || it.level == level) &&
            (kind.isEmpty() || it.kind == kind) && it.matches(query) }
    fun validate(progress: LearningProgress) {
        require(progress.version == version && progress.modified >= 0)
        when {
            progress.route.startsWith("tema/") -> {
                require(topics.any { "tema/${it.id}" == progress.route })
                require(progress.answers.isEmpty())
            }
            progress.route.startsWith("simulacion/") -> {
                val case = cases.singleOrNull { "simulacion/${it.id}" == progress.route }
                    ?: throw IllegalArgumentException("Caso de aprendizaje inexistente.")
                case.score(progress.answers)
                require(progress.completed == (progress.answers.size == case.questions.size))
            }
            else -> throw IllegalArgumentException("Ruta de progreso no admitida.")
        }
    }

    companion object {
        private fun texts(a: JSONArray): List<String> = (0 until a.length()).map { a.getString(it) }
        private fun objects(a: JSONArray): List<JSONObject> = (0 until a.length()).map { a.getJSONObject(it) }
        private fun refs(j: JSONObject): List<Reference> = objects(j.getJSONArray("sources")).map {
            val url = it.getString("url")
            val uri = java.net.URI(url)
            require(uri.scheme == "https" && uri.host != null && uri.userInfo == null)
            Reference(it.getString("title"), url, it.getString("publisher"), it.getString("date"))
        }
        private fun ids(values: List<String>) {
            require(values.distinct().size == values.size && values.all { NotebookCodec.slug(it) }) { "IDs editoriales inválidos o repetidos." }
        }
        fun load(context: Context): MasterCatalog = context.assets.open("base_maestra_mvp.json").bufferedReader().use { decode(it.readText()) }
        fun decode(raw: String): MasterCatalog {
            require(raw.toByteArray(Charsets.UTF_8).size <= 2_000_000) { "Base editorial demasiado grande." }
            JsonSafety.requireBoundedDepth(raw)
            val j = JSONObject(raw)
            require(j.opt("format") == 1)
            val topics = objects(j.getJSONArray("topics")).map {
                KnowledgeTopic(it.getString("id"), it.getString("title"), it.getString("area"), it.getString("level"),
                    texts(it.getJSONArray("tags")), it.getString("markdown"), it.getString("status"), it.getString("author"), refs(it))
            }
            val inventory = objects(j.getJSONArray("inventory")).map {
                val lines = it.getJSONArray("source_lines")
                NominalMedicine(it.getString("id"), it.getString("name"), it.getString("kind"), it.getString("status"),
                    it.getString("primary"), texts(it.getJSONArray("relations")), texts(it.getJSONArray("aliases")),
                    texts(it.getJSONArray("formulations")), (0 until lines.length()).map { i -> lines.getInt(i) },
                    texts(it.getJSONArray("notes")), it.getString("existing_id"))
            }
            val devices = objects(j.getJSONArray("devices")).map { d ->
                AtlasDevice(d.getString("id"), d.getString("name"), d.getString("area"), d.getString("model_note"),
                    objects(d.getJSONArray("parts")).map { p ->
                        AtlasPart(p.getString("id"), p.getString("name"), p.getString("function"), p.getString("safety"),
                            p.getDouble("x").toFloat(), p.getDouble("y").toFloat(), objects(p.getJSONArray("shapes")).map { s ->
                                val a = s.getJSONArray("points")
                                AtlasShape(s.getString("kind"), (0 until a.length()).map { a.getDouble(it).toFloat() })
                            })
                    }, refs(d))
            }
            val cases = objects(j.getJSONArray("cases")).map { c ->
                SyntheticCase(c.getString("id"), c.getString("title"), c.getString("area"), c.getString("intro"),
                    objects(c.getJSONArray("questions")).map { q -> CaseQuestion(q.getString("scene"), q.getString("prompt"),
                        texts(q.getJSONArray("options")), q.getInt("correct").also { require(q.opt("correct") is Int) }, q.getString("feedback"), q.getString("rubric")) }, refs(c), c.getString("status"))
            }
            val lists = objects(j.getJSONArray("checklists")).map { EducationalChecklist(it.getString("id"), it.getString("title"),
                it.getString("scope"), it.getBoolean("official"), texts(it.getJSONArray("items")), refs(it)) }
            val modules = objects(j.getJSONArray("modules")).map { ModuleState(it.getString("route"), it.getString("name"), it.getString("status"), it.getString("note")) }
            listOf(topics.map { it.id }, inventory.map { it.id }, devices.map { it.id }, cases.map { it.id }, lists.map { it.id }).forEach { ids(it) }
            require(topics.size <= 200 && inventory.size <= 1000 && devices.size <= 100 && cases.size <= 100)
            require(inventory.map { normalized(it.name) }.distinct().size == inventory.size)
            // An unreviewed submitted name must never appear advanced by a generic import.
            require(inventory.all { it.status == "IDENTIFICADO" && it.primary.isNotBlank() && it.lines.isNotEmpty() })
            devices.forEach { d ->
                ids(d.parts.map { it.id })
                require(d.parts.size in 1..30)
                d.parts.forEach { p ->
                    require(p.x in 0f..600f && p.y in 0f..380f && p.function.isNotBlank() && p.safety.isNotBlank())
                    p.shapes.forEach { s ->
                        require(s.kind in listOf("rect", "oval", "line", "poly") && s.points.size % 2 == 0 && s.points.size >= 4)
                        require(s.points.indices.all { i -> s.points[i].isFinite() && s.points[i] in 0f..(if (i % 2 == 0) 600f else 380f) })
                    }
                }
            }
            cases.forEach { c ->
                require(c.questions.size in 1..20)
                c.questions.forEach { q -> require(q.options.size in 2..4 && q.correct in q.options.indices && q.feedback.isNotBlank() && q.rubric.isNotBlank()) }
            }
            require(lists.single { it.id == "insnsb-10" }.let { it.official && it.items.size == 10 })
            require(lists.single { it.id == "idreyk-20" }.let { !it.official && it.items.size == 20 })
            require(modules.all { it.status in listOf("HECHO", "PARCIAL", "FALTA", "BLOQUEADO-POR-VALIDACIÓN") })
            return MasterCatalog(j.getString("version"), j.getString("date"), j.getString("source_sha256"), topics, inventory, devices, cases, lists, modules)
        }
    }
}

data class LearningProgress(val route: String, val version: String, val completed: Boolean,
    val answers: List<Int> = emptyList(), val modified: Long = System.currentTimeMillis()) {
    fun encode(): String = JSONObject().put("route", route).put("version", version).put("completed", completed)
        .put("answers", JSONArray(answers)).put("modified", modified).toString()
    companion object {
        fun decode(raw: String): LearningProgress {
            require(raw.length <= 4000)
            JsonSafety.requireBoundedDepth(raw)
            val j = JSONObject(raw)
            val a = j.getJSONArray("answers")
            require(j.opt("completed") is Boolean && (j.opt("modified") is Long || j.opt("modified") is Int))
            require(a.length() <= 20 && j.getString("route").length <= 150)
            return LearningProgress(j.getString("route"), j.getString("version"), j.getBoolean("completed"),
                (0 until a.length()).map { require(a.opt(it) is Int); a.getInt(it) }, j.getLong("modified"))
        }
    }
}

/** Small safe Markdown reader: no HTML execution, remote embeds or JavaScript. */
sealed interface StudyBlock {
    data class Heading(val text: String, val level: Int) : StudyBlock
    data class Paragraph(val text: String) : StudyBlock
    data class Bullet(val text: String) : StudyBlock
    data class Quote(val text: String) : StudyBlock
    data class Formula(val text: String) : StudyBlock
    data class Table(val rows: List<List<String>>) : StudyBlock
}
object StudyMarkdown {
    fun blocks(markdown: String): List<StudyBlock> {
        require(markdown.length <= 100_000)
        val lines = markdown.lines()
        require(lines.size <= 1000) { "Demasiadas líneas para la vista Markdown; divide el apunte en temas." }
        val blocks = mutableListOf<StudyBlock>()
        var i = 0
        while (i < lines.size) {
            val s = lines[i].trim()
            when {
                s.isEmpty() -> i++
                s.startsWith("```") -> {
                    i++
                    val content = mutableListOf<String>()
                    while (i < lines.size && !lines[i].trim().startsWith("```")) content += lines[i++]
                    if (i < lines.size) i++
                    blocks += StudyBlock.Formula(content.joinToString("\n"))
                }
                s.startsWith("|") -> {
                    val rows = mutableListOf<List<String>>()
                    while (i < lines.size && lines[i].trim().startsWith("|")) {
                        val cells = lines[i++].trim().trim('|').split('|').map { it.trim() }
                        require(cells.size <= 12 && rows.size < 200) { "Tabla demasiado grande para esta vista: máximo 12 columnas y 200 filas." }
                        if (!cells.all { it.matches(Regex(":?-{3,}:?")) }) rows += cells
                    }
                    blocks += StudyBlock.Table(rows)
                }
                s.startsWith("#") -> {
                    blocks += StudyBlock.Heading(s.trimStart('#', ' '), s.takeWhile { it == '#' }.length.coerceIn(1, 6)); i++
                }
                s.startsWith(">") -> { blocks += StudyBlock.Quote(s.removePrefix(">").trim()); i++ }
                s.startsWith("- ") || s.matches(Regex("[0-9]+\\. .+")) -> {
                    blocks += StudyBlock.Bullet(if (s.startsWith("- ")) "• " + s.removePrefix("- ") else s); i++
                }
                else -> { blocks += StudyBlock.Paragraph(s); i++ }
            }
        }
        return blocks
    }
}
