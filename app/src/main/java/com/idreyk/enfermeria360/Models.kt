package com.idreyk.enfermeria360

import java.net.URI
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.text.Normalizer
import java.time.LocalDate
import kotlin.math.roundToInt
import org.json.JSONArray
import org.json.JSONObject

val CONTENT_ARRAYS =
    linkedMapOf(
        "med" to "medicamentos",
        "case" to "casos",
        "safety" to "listas_seguridad",
        "protocol" to "protocolos",
    )

val DRUG_FIELDS =
    linkedMapOf(
        "descripcion" to "Descripción general",
        "mecanismo_accion" to "Mecanismo de acción",
        "presentaciones" to "Presentaciones y concentraciones",
        "vias" to "Vías de administración",
        "indicaciones" to "Indicaciones",
        "contraindicaciones" to "Contraindicaciones",
        "precauciones" to "Precauciones y advertencias",
        "dosificacion" to "Dosificación · pendiente de validación",
        "reconstitucion" to "Reconstitución",
        "dilucion" to "Dilución",
        "compatibilidad" to "Compatibilidad e incompatibilidades",
        "administracion" to "Administración y velocidad",
        "estabilidad" to "Estabilidad y conservación",
        "reacciones_adversas" to "Reacciones adversas",
        "interacciones" to "Interacciones",
        "monitorizacion" to "Monitorización",
        "cuidados_enfermeria" to "Cuidados de enfermería",
        "poblaciones_especiales" to "Poblaciones especiales",
        "educacion" to "Educación a la persona",
        "alertas" to "Alertas y observaciones",
        "nombres_comerciales" to "Nombre comercial / laboratorio",
        "almacenamiento" to "Almacenamiento",
        "formas_presentacion" to "Formas de presentación / registro documentado",
        "velocidad_administracion" to "Velocidad de administración",
        "sueros_compatibles" to "Sueros compatibles",
        "incompatibilidades" to "Incompatibilidades",
        "objetivo_terapeutico" to "Objetivo del tratamiento",
        "antes_administrar" to "Antes de administrar",
        "durante_administracion" to "Durante la administración",
        "despues_administracion" to "Después de administrar",
    )

val LEGACY_DRUG_KEYS = DRUG_FIELDS.keys.take(20).toSet()

data class Reference(
    val title: String,
    val url: String,
    val publisher: String = "",
    val accessed: String = "",
) {
    fun json() =
        JSONObject()
            .put("titulo", title)
            .put("url", url)
            .put("editor", publisher)
            .put("fecha_consulta", accessed)
}

data class Entry(
    val kind: String,
    val id: String,
    val name: String,
    val group: String,
    val json: JSONObject,
) {
    val key: String
        get() = "$kind/$id"

    val sections: Map<String, String>
        get() =
            DRUG_FIELDS.keys.associateWith {
                json.optJSONObject("secciones")?.optString(it, "") ?: ""
            }

    val aliases: String
        get() =
            buildList {
                    json.optJSONArray("sinonimos")?.let { a ->
                        for (i in 0 until a.length()) add(a.optString(i))
                    }
                    add(json.optJSONObject("secciones")?.optString("nombres_comerciales").orEmpty())
                }
                .joinToString(" ")

    val revision: String
        get() = json.optString("revision", "borrador")

    val references: List<Reference>
        get() = ContentParser.references(json.optJSONArray("fuentes"))

    val body: String
        get() = json.optString("contenido", "")

    val objective: String
        get() = json.optString("objetivo", "")

    val checklist: List<Pair<String, String>>
        get() =
            json.optJSONArray("items")?.let { a ->
                (0 until a.length()).map {
                    a.getJSONObject(it).let { o ->
                        o.getString("titulo") to o.optString("detalle", "")
                    }
                }
            } ?: emptyList()
}

data class ContentPackage(
    val id: String,
    val version: Long,
    val title: String,
    val author: String,
    val updated: String,
    val license: String,
    val entries: List<Entry>,
    val raw: String,
    val isBackup: Boolean = false,
)

data class ImportPreview(
    val content: ContentPackage,
    val newCount: Int,
    val updateCount: Int,
    val changes: List<CatalogChange>,
    val fingerprint: String,
)

data class StudyNote(val id: Long, val patientId: String, val text: String, val timestamp: Long)

data class PracticeRecord(val id: Long, val type: String, val summary: String, val timestamp: Long)

object ContentParser {
    const val MAX_BYTES = 2_000_000

    fun references(array: JSONArray?): List<Reference> {
        if (array == null) return emptyList()
        require(array.length() <= 30) { "Máximo 30 fuentes por registro." }
        return (0 until array.length()).map { index ->
            val o = array.optJSONObject(index) ?: throw IllegalArgumentException("Fuente inválida.")
            val title = requiredText(o, "titulo", 500)
            val url = requiredText(o, "url", 2000)
            val valid =
                runCatching {
                        URI(url).let {
                            it.scheme == "https" &&
                                !it.host.isNullOrBlank() &&
                                it.rawUserInfo == null
                        }
                    }
                    .getOrDefault(false)
            require(valid) { "Cada fuente necesita una URL HTTPS completa, sin credenciales." }
            val accessed = optionalText(o, "fecha_consulta", 10)
            if (accessed.isNotBlank()) validDate(accessed, "fecha_consulta")
            Reference(title, url, optionalText(o, "editor", 500), accessed)
        }
    }

    fun decodeUtf8(bytes: ByteArray): String {
        require(bytes.size <= MAX_BYTES) { "El archivo supera 2 MB." }
        return try {
            Charsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT)
                .decode(ByteBuffer.wrap(bytes))
                .toString()
        } catch (e: Exception) {
            throw IllegalArgumentException(
                "El archivo debe estar codificado en UTF-8 sin caracteres dañados."
            )
        }
    }

    fun parse(raw: String): ContentPackage {
        require(raw.toByteArray(Charsets.UTF_8).size <= MAX_BYTES) {
            "El archivo supera 2 MB. Divide el contenido en varios paquetes."
        }
        JsonSafety.requireBoundedDepth(raw)
        val root =
            try {
                JSONObject(raw)
            } catch (e: Exception) {
                throw IllegalArgumentException(
                    "No es un JSON válido. Usa la plantilla del proyecto."
                )
            }
        val schema = root.opt("version_esquema")
        require(schema == 1 || schema == 2 || schema == 3) {
            "Versión de esquema incompatible. Se admiten esquemas 1, 2 y 3."
        }
        require(schema != 1 || !root.has("protocolos")) {
            "Los protocolos requieren version_esquema 2 o 3; no se ignorarán silenciosamente."
        }
        val id = slug(root, "id_paquete")
        val rawVersion = root.opt("version_contenido")
        require(rawVersion is Int || rawVersion is Long) {
            "version_contenido debe ser un entero, no texto ni decimal."
        }
        val version = root.optLong("version_contenido", 0)
        require(version in 1..9_999_999L) { "version_contenido debe ser un entero positivo." }
        val title = requiredText(root, "titulo", 140)
        val author = requiredText(root, "autor", 140)
        val updated = requiredText(root, "fecha_actualizacion", 10)
        try {
            LocalDate.parse(updated)
        } catch (e: Exception) {
            throw IllegalArgumentException("fecha_actualizacion debe ser AAAA-MM-DD.")
        }
        val license = optionalText(root, "licencia", 2000)
        val packageType = optionalText(root, "tipo_paquete", 30, "contenido")
        require(packageType in listOf("contenido", "respaldo_catalogo")) {
            "Tipo de paquete no reconocido."
        }
        val isBackup = packageType == "respaldo_catalogo"
        val entries = mutableListOf<Entry>()
        val seen = mutableSetOf<String>()
        for ((arrayName, kind) in CONTENT_ARRAYS.map { (kind, array) -> array to kind }) {
            if (root.has(arrayName))
                require(root.optJSONArray(arrayName) != null) {
                    "$arrayName debe ser una lista JSON."
                }
            val arr = root.optJSONArray(arrayName) ?: JSONArray()
            require(arr.length() <= 300) { "Máximo 300 registros de cada tipo por paquete." }
            for (i in 0 until arr.length()) {
                val o =
                    arr.optJSONObject(i)
                        ?: throw IllegalArgumentException("Registro ${i+1} de $arrayName inválido.")
                val entryId = slug(o, "id")
                require(seen.add("$kind/$entryId")) { "ID duplicado: $entryId." }
                val name = requiredText(o, if (kind == "med") "nombre" else "titulo", 140)
                val group = optionalText(o, if (kind == "med") "grupo" else "area", 140, "General")
                val revision = optionalText(o, "revision", 30, "borrador")
                optionalText(o, "revisor", 140)
                val reviewDate = optionalText(o, "fecha_revision", 10)
                if (reviewDate.isNotBlank()) validDate(reviewDate, "fecha_revision")
                if (isBackup) {
                    slug(o, "_paquete")
                    requiredText(o, "_autor", 140)
                    validDate(requiredText(o, "_actualizacion", 10), "_actualizacion")
                    optionalText(o, "_licencia", 2000)
                }
                require(revision in listOf("borrador", "declarada_por_autor")) {
                    "Revisión no reconocida en $name."
                }
                if (o.has("fuentes"))
                    require(o.optJSONArray("fuentes") != null) {
                        "fuentes debe ser una lista en $name."
                    }
                val refs = references(o.optJSONArray("fuentes"))
                require(refs.size <= 30) { "Máximo 30 fuentes por registro." }
                for (ref in refs) {
                    require(ref.title.isNotBlank() && ref.title.length <= 500) {
                        "Cada fuente necesita un título."
                    }
                    require(ref.url.startsWith("https://") && ref.url.length <= 2000) {
                        "Las fuentes deben usar URL https://."
                    }
                }
                if (revision == "declarada_por_autor") {
                    require(
                        o.optString("revisor").isNotBlank() &&
                            refs.isNotEmpty() &&
                            (if (isBackup) o.optString("_licencia") else license).isNotBlank()
                    ) {
                        "La revisión declarada requiere revisor, fuentes y licencia. Si falta algo, usa borrador."
                    }
                    try {
                        LocalDate.parse(o.optString("fecha_revision"))
                    } catch (e: Exception) {
                        throw IllegalArgumentException(
                            "La fecha de revisión de $name no es válida."
                        )
                    }
                }
                if (kind == "med") {
                    require(o.optJSONObject("secciones") != null) {
                        "Faltan las secciones del medicamento $name."
                    }
                    val sections = o.getJSONObject("secciones")
                    for (key in sections.keys()) {
                        require(key in DRUG_FIELDS) { "Sección desconocida en $name: $key." }
                        require(schema == 3 || key in LEGACY_DRUG_KEYS) {
                            "La sección $key requiere version_esquema 3 y app v0.4."
                        }
                        require(sections.get(key) is String) {
                            "La sección $key debe contener texto."
                        }
                        require(sections.getString(key).length <= 16000) {
                            "La sección $key es demasiado larga."
                        }
                    }
                    if (o.has("sinonimos")) {
                        val aliases =
                            o.optJSONArray("sinonimos")
                                ?: throw IllegalArgumentException("sinonimos debe ser una lista.")
                        require(aliases.length() <= 30) { "Demasiados sinónimos." }
                        for (j in 0 until aliases.length()) require(
                            aliases.get(j) is String && aliases.getString(j).length <= 120
                        ) {
                            "Sinónimo inválido."
                        }
                    }
                } else if (kind == "case" || kind == "protocol") {
                    require(optionalText(o, "contenido", 60000).length <= 60000) {
                        "El caso $name es demasiado largo."
                    }
                    require(optionalText(o, "objetivo", 4000).length <= 4000) {
                        "El objetivo de $name es demasiado largo."
                    }
                } else {
                    optionalText(o, "institucion", 500)
                    optionalText(o, "edicion", 140)
                    val items =
                        o.optJSONArray("items")
                            ?: throw IllegalArgumentException("Faltan los ítems de $name.")
                    require(items.length() in 1..100) { "Cada lista necesita de 1 a 100 ítems." }
                    for (j in 0 until items.length()) {
                        val item =
                            items.optJSONObject(j)
                                ?: throw IllegalArgumentException("Ítem de lista inválido.")
                        requiredText(item, "titulo", 180)
                        require(optionalText(item, "detalle", 3000).length <= 3000) {
                            "Detalle de lista demasiado largo."
                        }
                    }
                }
                if (o.has("derivado_de")) {
                    val origin =
                        o.optJSONObject("derivado_de")
                            ?: throw IllegalArgumentException("Procedencia original inválida.")
                    optionalText(origin, "paquete", 100)
                    optionalText(origin, "autor", 140)
                    optionalText(origin, "licencia", 2000)
                    optionalText(origin, "actualizacion", 10)
                        .takeIf { it.isNotBlank() }
                        ?.let { validDate(it, "actualizacion original") }
                }
                o.put("revision", revision)
                entries += Entry(kind, entryId, name, group, o)
            }
        }
        require(entries.isNotEmpty() || isBackup) {
            "El paquete no contiene medicamentos, casos, listas de seguridad ni protocolos."
        }
        require(entries.size <= 500) { "Máximo 500 registros por paquete." }
        return ContentPackage(
            id,
            version,
            title,
            author,
            updated,
            license,
            entries,
            root.toString(),
            isBackup,
        )
    }

    private fun optionalText(o: JSONObject, key: String, max: Int, default: String = ""): String {
        if (!o.has(key)) return default
        require(o.opt(key) is String) { "$key debe ser texto, no un número, objeto ni null." }
        val value = o.getString(key)
        require(value.length <= max) { "$key supera $max caracteres." }
        return value
    }

    private fun validDate(value: String, key: String) {
        require(
            Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}").matches(value) &&
                runCatching { LocalDate.parse(value) }.isSuccess
        ) {
            "$key debe ser una fecha válida AAAA-MM-DD."
        }
    }

    private fun requiredText(o: JSONObject, key: String, max: Int): String {
        require(o.opt(key) is String) { "Falta el campo de texto $key." }
        val value = o.getString(key).trim()
        require(value.isNotEmpty() && value.length <= max) {
            "$key debe tener entre 1 y $max caracteres."
        }
        return value
    }

    private fun slug(o: JSONObject, key: String): String {
        val s = requiredText(o, key, 100)
        require(Regex("[a-z0-9][a-z0-9_-]*").matches(s)) {
            "$key solo admite minúsculas, números, guion y guion bajo."
        }
        return s
    }
}

fun normalized(value: String) =
    Normalizer.normalize(value, Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "").lowercase()

object PracticeMath {
    fun glasgow(eye: Int, verbal: Int, motor: Int): Int? {
        require(eye in -1..4 && verbal in -1..5 && motor in -1..6) { "Componente fuera de rango." }
        if (eye <= 0 || verbal <= 0 || motor <= 0) return null
        return eye + verbal + motor
    }

    fun bmi(weight: Double, heightCm: Double): Double {
        require(
            weight.isFinite() &&
                heightCm.isFinite() &&
                weight > 0 &&
                weight <= 500 &&
                heightCm > 0 &&
                heightCm <= 300
        ) {
            "Revisa peso y talla: deben ser positivos y estar dentro de los límites del formulario."
        }
        val result = weight / ((heightCm / 100) * (heightCm / 100))
        require(result.isFinite()) { "Resultado fuera del rango numérico admitido." }
        return result
    }

    fun drip(volume: Double, hours: Double, factor: Int): Pair<Double, Int> {
        require(
            volume.isFinite() &&
                hours.isFinite() &&
                volume > 0 &&
                volume <= 100000 &&
                hours > 0 &&
                hours <= 1000 &&
                factor in listOf(10, 15, 20, 60)
        ) {
            "Revisa volumen, tiempo y factor del ejemplo."
        }
        val drops = volume * factor / (hours * 60)
        require(drops <= Int.MAX_VALUE) { "Resultado fuera del rango admitido." }
        return volume / hours to drops.roundToInt()
    }

    fun convert(amount: Double, type: String): Double {
        require(amount.isFinite() && amount >= 0 && amount <= 1e9) { "Cantidad inválida." }
        return when (type) {
            "mg-g",
            "mL-L" -> amount / 1000
            "g-mg",
            "L-mL" -> amount * 1000
            else -> throw IllegalArgumentException("Conversión no reconocida.")
        }
    }

    fun evaPosition(fraction: Float): Int {
        require(fraction.isFinite())
        return (fraction.coerceIn(0f, 1f) * 100).roundToInt()
    }
}
