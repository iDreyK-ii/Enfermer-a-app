package com.idreyk.enfermeria360

import org.json.JSONArray
import org.json.JSONObject

/** Describes structural changes, never judges whether a clinical statement is correct. */
data class CatalogChange(
    val key: String,
    val name: String,
    val previousName: String? = null,
    val changedFields: List<String> = emptyList(),
    val clearedFields: List<String> = emptyList(),
    val replacesLocalEdit: Boolean = false,
) {
    val isReplacement: Boolean
        get() = previousName != null
}

object ImportReview {
    private val labels =
        mapOf(
            "nombre" to "Nombre",
            "titulo" to "Título",
            "grupo" to "Grupo",
            "area" to "Área",
            "sinonimos" to "Sinónimos",
            "objetivo" to "Objetivo",
            "contenido" to "Contenido",
            "items" to "Ítems de la lista",
            "institucion" to "Institución",
            "edicion" to "Edición",
            "fuentes" to "Fuentes documentales",
            "revision" to "Estado de revisión",
            "revisor" to "Revisor",
            "fecha_revision" to "Fecha de revisión",
        )

    private fun populated(value: Any?): Boolean =
        when (value) {
            null,
            JSONObject.NULL -> false
            is String -> value.isNotBlank()
            is JSONArray -> (0 until value.length()).any { populated(value.opt(it)) }
            is JSONObject -> value.keys().asSequence().any { populated(value.opt(it)) }
            else -> true
        }

    /** Stable even when JSON object key order differs. Arrays remain ordered. */
    internal fun canonical(value: Any?): String =
        when (value) {
            null,
            JSONObject.NULL -> "null"
            is String -> JSONObject.quote(value)
            is JSONObject ->
                value.keys().asSequence().toList().sorted().joinToString(",", "{", "}") {
                    JSONObject.quote(it) + ":" + canonical(value.opt(it))
                }
            is JSONArray ->
                (0 until value.length()).joinToString(",", "[", "]") { canonical(value.opt(it)) }
            else -> value.toString()
        }

    private fun fields(entry: Entry): Map<String, String> = buildMap {
        entry.json.keys().forEach { key ->
            if (key != "id" && key != "secciones" && !key.startsWith("_")) {
                val value = entry.json.opt(key)
                put(labels[key] ?: key, if (populated(value)) canonical(value) else "")
            }
        }
        put("Estado de revisión", canonical(entry.revision))
        if (entry.kind == "med") {
            val sections = entry.sections
            DRUG_FIELDS.forEach { (key, label) ->
                val value = sections[key].orEmpty()
                put(label, if (value.isBlank()) "" else canonical(value))
            }
        }
    }

    fun compare(incoming: List<Entry>, current: List<Entry>): List<CatalogChange> {
        val existing = current.associateBy { it.key }
        return incoming.map { next ->
            val previous = existing[next.key]
            if (previous == null) CatalogChange(next.key, next.name)
            else {
                val before = fields(previous)
                val after = fields(next)
                val names = (before.keys + after.keys).sorted()
                CatalogChange(
                    next.key,
                    next.name,
                    previous.name,
                    names.filter { before[it].orEmpty() != after[it].orEmpty() },
                    names.filter {
                        before[it].orEmpty().isNotEmpty() && after[it].orEmpty().isEmpty()
                    },
                    previous.json.optString("_paquete").startsWith("edicion-local-"),
                )
            }
        }
    }

    /** Binds confirmation to the reviewed payload and catalogue state, not the current time. */
    fun fingerprint(current: List<Entry>, incoming: ContentPackage, previousVersion: Long): String {
        val rows = JSONArray()
        current
            .sortedBy { it.key }
            .forEach { rows.put(JSONObject().put("key", it.key).put("payload", it.json)) }
        return CloudProtocol.hash(
            canonical(
                JSONObject()
                    .put("catalog", rows)
                    .put("incoming", JSONObject(incoming.raw))
                    .put("previous_version", previousVersion)
            )
        )
    }
}
