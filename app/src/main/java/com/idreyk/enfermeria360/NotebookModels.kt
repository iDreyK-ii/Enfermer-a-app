package com.idreyk.enfermeria360

import org.json.JSONArray
import org.json.JSONObject

val PLAN_FIELDS =
    linkedMapOf(
        "valoracion" to "Valoración · datos subjetivos y objetivos ficticios",
        "diagnostico" to "Diagnóstico / problema · texto libre, sin catálogo automático",
        "objetivos" to "Resultados esperados · indicadores y plazo",
        "intervenciones" to "Planificación · acciones propuestas para el ejercicio",
        "ejecucion" to "Ejecución · acciones documentadas en la simulación",
        "evaluacion" to "Evaluación · resultado, comparación y revisión del plan",
        "referencias" to "Fuentes y justificación del ejercicio",
    )

data class NotebookRecord(
    val id: String,
    val kind: String,
    val owner: String,
    val title: String,
    val fields: JSONObject,
    val modified: Long,
) {
    fun json(): JSONObject =
        JSONObject()
            .put("id", id)
            .put("tipo", kind)
            .put("persona_ficticia", owner)
            .put("titulo", title)
            .put("campos", fields)
            .put("modificado", modified)
}

data class MediaRecord(
    val id: String,
    val title: String,
    val mime: String,
    val filename: String,
    val bytes: Long,
    val created: Long,
)

object NotebookCodec {
    const val MAX_RECORDS = 200
    const val MAX_BYTES = 2_000_000
    val owners = listOf("mario", "rosa", "luis")
    val statuses = listOf("borrador", "en_practica", "cerrado")

    fun slug(id: String): Boolean = Regex("[a-z0-9][a-z0-9_-]{0,99}").matches(id)

    private fun text(json: JSONObject, key: String, max: Int): String {
        require(json.opt(key) is String) { "El campo $key debe ser texto." }
        return json.getString(key).also {
            require(it.length <= max) { "El campo $key excede $max caracteres." }
        }
    }

    fun record(json: JSONObject): NotebookRecord {
        val id = text(json, "id", 100)
        require(slug(id)) { "ID de cuaderno inválido." }
        val kind = text(json, "tipo", 20)
        require(kind in listOf("plan", "note")) { "Tipo de cuaderno desconocido." }
        val owner = text(json, "persona_ficticia", 100)
        require(if (kind == "plan") owner in owners else owner == "") {
            "El plan solo admite una ficha ficticia incluida; el apunte no tiene paciente."
        }
        val title = text(json, "titulo", 140).trim()
        require(title.isNotEmpty()) { "Escribe un título." }
        val fields =
            json.optJSONObject("campos")
                ?: throw IllegalArgumentException("Faltan los campos del documento.")
        val allowed =
            if (kind == "plan") PLAN_FIELDS.keys + EXTRA_PLAN_FIELDS.keys + "estado" else setOf("contenido", "etiquetas")
        for (key in fields.keys()) require(key in allowed) { "Campo desconocido: $key." }
        if (kind == "plan") {
            PLAN_FIELDS.keys.forEach { text(fields, it, 4000) }
            EXTRA_PLAN_FIELDS.keys.filter { fields.has(it) }.forEach { text(fields, it, 4000) }
            require(text(fields, "estado", 30) in statuses) { "Estado del plan no reconocido." }
        } else {
            text(fields, "contenido", 16000)
            text(fields, "etiquetas", 300)
        }
        val stamp = json.opt("modificado")
        require((stamp is Int || stamp is Long) && json.getLong("modificado") >= 0) {
            "Fecha del documento inválida."
        }
        return NotebookRecord(
            id,
            kind,
            owner,
            title,
            JSONObject(fields.toString()),
            json.getLong("modificado"),
        )
    }

    fun encode(records: List<NotebookRecord>): String {
        require(records.map { it.id }.distinct().size == records.size) {
            "ID de cuaderno duplicado."
        }
        require(records.size <= MAX_RECORDS) { "Máximo $MAX_RECORDS documentos en el cuaderno." }
        val root =
            JSONObject()
                .put("tipo_respaldo", "cuaderno_practica")
                .put("version_esquema", 2)
                .put("registros", JSONArray(records.map { record(it.json()).json() }))
        val raw = root.toString()
        require(raw.toByteArray(Charsets.UTF_8).size <= MAX_BYTES) {
            "El cuaderno supera 2 MB; reduce el contenido antes de continuar."
        }
        return raw
    }

    fun decode(raw: String): List<NotebookRecord> {
        require(raw.toByteArray(Charsets.UTF_8).size <= MAX_BYTES) { "El cuaderno supera 2 MB." }
        JsonSafety.requireBoundedDepth(raw)
        val root =
            try {
                JSONObject(raw)
            } catch (e: Exception) {
                throw IllegalArgumentException("El respaldo de cuaderno no es JSON válido.")
            }
        require(
            root.optString("tipo_respaldo") == "cuaderno_practica" &&
                root.opt("version_esquema") in listOf(1, 2)
        ) {
            "Elige un respaldo de cuaderno de práctica, no un catálogo."
        }
        val array =
            root.optJSONArray("registros")
                ?: throw IllegalArgumentException("Faltan los registros del cuaderno.")
        require(array.length() <= MAX_RECORDS) { "Demasiados documentos." }
        val seen = mutableSetOf<String>()
        return (0 until array.length()).map { i ->
            val r =
                record(
                    array.optJSONObject(i) ?: throw IllegalArgumentException("Documento inválido.")
                )
            require(seen.add(r.id)) { "ID de cuaderno duplicado." }
            r
        }
    }
}

object CatalogAuthoring {
    fun empty(kind: String, id: String): JSONObject {
        require(kind in CONTENT_ARRAYS && NotebookCodec.slug(id))
        return JSONObject()
            .put("id", id)
            .put(if (kind == "med") "nombre" else "titulo", "")
            .put(if (kind == "med") "grupo" else "area", "General")
            .put("revision", "borrador")
            .put("fuentes", JSONArray())
            .apply {
                when (kind) {
                    "med" -> {
                        put("secciones", JSONObject())
                        put("sinonimos", JSONArray())
                    }
                    "case",
                    "protocol" -> {
                        put("contenido", "")
                        put("objetivo", "")
                    }
                    "safety" -> {
                        put("institucion", "")
                        put("edicion", "")
                        put(
                            "items",
                            JSONArray().put(JSONObject().put("titulo", "").put("detalle", "")),
                        )
                    }
                }
            }
    }

    fun draft(original: JSONObject?, edited: JSONObject): JSONObject {
        val out = JSONObject(edited.toString()).put("revision", "borrador")
        out.remove("revisor")
        out.remove("fecha_revision")
        if (
            original != null &&
                !out.has("derivado_de") &&
                original.optString("_paquete").isNotBlank()
        ) {
            out.put(
                "derivado_de",
                JSONObject()
                    .put("paquete", original.optString("_paquete"))
                    .put("autor", original.optString("_autor"))
                    .put("licencia", original.optString("_licencia"))
                    .put("actualizacion", original.optString("_actualizacion")),
            )
        }
        return out
    }
}
