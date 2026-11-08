package com.idreyk.enfermeria360

import java.net.URI
import java.time.LocalDate
import org.json.JSONArray
import org.json.JSONObject

internal fun clinicalInstrumentTypeGroup(type: String): String = when {
    type.contains("carga", true) || type.contains("actividad de enfermería", true) || type.contains("intervención terapéutica", true) || type.contains("actividad terapéutica", true) -> "Carga asistencial"
    type.contains("triaje", true) -> "Triaje"
    type.contains("alerta", true) || type.contains("monitoriz", true) -> "Monitorización y alerta"
    type.contains("cribado", true) -> "Cribado"
    type.contains("pronóstico", true) || type.contains("pronostico", true) -> "Pronóstico"
    type.contains("clasificación", true) || type.contains("clasificacion", true) -> "Clasificación"
    type.contains("funcional", true) -> "Funcionalidad"
    type.contains("valoración", true) || type.contains("valoracion", true) || type.contains("autoinforme", true) -> "Valoración"
    type.contains("índice", true) || type.contains("indice", true) || type.contains("indicador", true) -> "Indicadores e índices"
    else -> "Herramienta clínica"
}

enum class ClinicalAvailability(val key: String, val label: String) {
    PRACTICE("calculo", "Práctica interactiva"),
    RECORD("registro", "Solo registro"),
    SIMULATION("simulacion", "Simulación"),
    DOCUMENT("ficha", "Ficha documental"),
    PERMISSION("permiso", "Permiso pendiente"),
}

data class ClinicalSource(val id: String, val reference: Reference, val reading: String)

data class ClinicalInstrument(
    val id: String,
    val name: String,
    val area: String,
    val services: List<String>,
    val instrumentType: String,
    val nursingRole: String,
    val localContext: String,
    val populations: List<String>,
    val purpose: String,
    val populationDetail: String,
    val version: String,
    val method: String,
    val limitations: String,
    val rights: String,
    val sourceIds: List<String>,
    val availability: ClinicalAvailability,
    val route: String,
    val nextStep: String,
    val reviewed: String,
) {
    val hasPractice: Boolean get() = route.isNotEmpty()
}

data class ClinicalCatalog(
    val reviewed: String,
    val scope: String,
    val sources: Map<String, ClinicalSource>,
    val instruments: List<ClinicalInstrument>,
) {
    fun filter(
        query: String = "",
        population: String = "",
        area: String = "",
        withPracticeOnly: Boolean = false,
        availability: ClinicalAvailability? = null,
        service: String = "",
        instrumentType: String = "",
    ): List<ClinicalInstrument> {
        val needle = normalized(query.trim())
        return instruments.filter {
            (population.isEmpty() || population in it.populations) &&
                (area.isEmpty() || area == it.area) &&
                (service.isEmpty() || service in it.services) &&
                (instrumentType.isEmpty() || clinicalInstrumentTypeGroup(it.instrumentType) == instrumentType) &&
                (availability == null || availability == it.availability) &&
                (!withPracticeOnly || it.hasPractice) &&
                normalized("${it.name} ${it.area} ${it.instrumentType} ${it.services.joinToString(" ")} ${it.purpose} ${it.version}").contains(needle)
        }
    }

    companion object {
        const val ASSET = "escalas_clinicas_v1.json"
        val POPULATIONS = linkedMapOf(
            "adultos" to "Adultos",
            "pediatria" to "Pediatría",
            "neonatos" to "Neonatos",
        )
        // Bundled editorial data cannot introduce arbitrary navigation destinations.
        val PRACTICE_ROUTES = mapOf(
            "glasgow" to "glasgow",
            "glasgow-pediatrico" to "glasgow-pediatrico",
            "eva" to "eva",
            "numerica" to "dolor-numerico",
            "4at" to "test-4at",
            "braden" to "registro-escala/braden",
            "morse" to "registro-escala/morse",
            "norton" to "registro-escala/norton",
            "rass" to "registro-escala/rass",
            "apgar" to "escala/apgar",
            "silverman" to "escala/silverman",
            "capurro-a" to "capurro/A",
            "capurro-b" to "capurro/B",
        )

        private fun field(obj: JSONObject, key: String, maximum: Int = 1800): String {
            val value = obj.get(key)
            require(value is String && value.isNotBlank() && value.length <= maximum) {
                "Campo documental inválido: $key."
            }
            return value
        }

        private fun strings(array: JSONArray, maximum: Int): List<String> {
            require(array.length() in 1..maximum) { "Cantidad documental no permitida." }
            return (0 until array.length()).map {
                val value = array.get(it)
                require(value is String && value.isNotBlank() && value.length <= 120)
                value
            }.also { require(it.distinct().size == it.size) { "Valores documentales repetidos." } }
        }

        private fun date(obj: JSONObject, fieldName: String): String =
            field(obj, fieldName, 10).also { LocalDate.parse(it) }

        fun parse(text: String): ClinicalCatalog {
            require(text.toByteArray(Charsets.UTF_8).size <= 500_000) { "Catálogo demasiado grande." }
            JsonSafety.requireBoundedDepth(text)
            val root = JSONObject(text)
            require(root.getInt("formato") == 1) { "Formato de biblioteca desconocido." }
            val reviewedDate = date(root, "fecha_revision")
            val sourceArray = root.getJSONArray("fuentes")
            require(sourceArray.length() in 1..100)
            val sourceList = (0 until sourceArray.length()).map {
                val obj = sourceArray.getJSONObject(it)
                val id = field(obj, "id", 60)
                require(id.matches(Regex("[a-z0-9-]+")))
                val url = field(obj, "url", 2000)
                val uri = URI(url)
                require(uri.scheme == "https" && !uri.host.isNullOrBlank() && uri.userInfo == null) {
                    "Fuente documental no HTTPS o con credenciales."
                }
                ClinicalSource(
                    id,
                    Reference(field(obj, "titulo", 300), url, field(obj, "editor", 300), date(obj, "consulta")),
                    field(obj, "lectura", 600),
                )
            }
            require(sourceList.map { it.id }.distinct().size == sourceList.size) { "Fuentes duplicadas." }
            val sources = sourceList.associateBy { it.id }
            val array = root.getJSONArray("instrumentos")
            require(array.length() in 1..220)
            val instruments = (0 until array.length()).map {
                val obj = array.getJSONObject(it)
                val id = field(obj, "id", 60)
                require(id.matches(Regex("[a-z0-9-]+")))
                val populations = strings(obj.getJSONArray("poblaciones"), 3)
                require(populations.all { it in POPULATIONS })
                val services = strings(obj.getJSONArray("servicios"), 30)
                val instrumentType = field(obj, "tipo_instrumento", 120)
                val nursingRole = field(obj, "rol_enfermeria", 1800)
                val localContext = field(obj, "contexto_peru", 1800)
                val refs = strings(obj.getJSONArray("fuentes"), 10)
                require(refs.all { it in sources }) { "Falta una fuente del instrumento." }
                require(obj.getString("revision_clinica") == "pendiente") {
                    "Esta edición no declara validación clínica."
                }
                val availability = ClinicalAvailability.entries.singleOrNull {
                    it.key == obj.getString("estado")
                } ?: throw IllegalArgumentException("Estado documental desconocido.")
                val route = obj.getString("ruta")
                val actionable = availability in listOf(
                    ClinicalAvailability.PRACTICE, ClinicalAvailability.RECORD, ClinicalAvailability.SIMULATION,
                )
                require(actionable == route.isNotEmpty()) { "Estado y función no coinciden." }
                require(route.isEmpty() || PRACTICE_ROUTES[id] == route) { "Ruta no autorizada para esta ficha." }
                ClinicalInstrument(
                    id, field(obj, "nombre", 160), field(obj, "area", 100), services, instrumentType,
                    nursingRole, localContext, populations,
                    field(obj, "objetivo"), field(obj, "poblacion_detalle"), field(obj, "version"),
                    field(obj, "metodo"), field(obj, "limites"), field(obj, "derechos"), refs,
                    availability, route, field(obj, "siguiente"), date(obj, "revision_documental"),
                )
            }
            require(instruments.map { it.id }.distinct().size == instruments.size) { "Instrumentos duplicados." }
            return ClinicalCatalog(reviewedDate, field(root, "alcance"), sources, instruments)
        }
    }
}

/** Pure arithmetic for explicitly labelled practice; no treatment decisions. */
object ClinicalPractice {
    const val NO_SELF_REPORT = -98

    fun numericPain(value: Int): Int? {
        require(value == NursingScales.UNSET || value == NO_SELF_REPORT || value in 0..10) {
            "El autoinforme numérico debe ser un entero de 0 a 10."
        }
        return value.takeIf { it in 0..10 }
    }

    val fourAt = ScaleDefinition(
        "4at", "4AT · adaptación de práctica",
        listOf(
            ScaleItem("1 · Estado de consciencia", listOf(
                "Normal, o somnolencia menor de 10 segundos al despertar y luego normal" to 0,
                "Claramente anormal" to 4,
            )),
            ScaleItem("2 · AMT4", listOf(
                "Sin errores" to 0,
                "Un error" to 1,
                "Dos o más errores / no valorable" to 2,
            )),
            ScaleItem("3 · Atención", listOf(
                "Siete meses o más correctamente" to 0,
                "Inicia pero menos de siete / se niega a iniciar" to 1,
                "No valorable por malestar, somnolencia o falta de atención" to 2,
            )),
            ScaleItem("4 · Cambio agudo o curso fluctuante", listOf(
                "No" to 0,
                "Sí" to 4,
            )),
        ),
        12,
    )

    fun fourAtTotal(scores: List<Int>): Int? = NursingScales.total(fourAt, scores)

    fun fourAtMeaning(total: Int): String {
        require(total in 0..12) { "Puntaje 4AT no permitido." }
        return when (total) {
            0 -> "El cribado no sugiere delirium ni deterioro moderado-grave, pero un cero NO los excluye. Revisar el contexto y la información sobre cambio agudo."
            in 1..3 -> "Posible deterioro cognitivo. Requiere valoración contextual; el resultado no excluye delirium ni diagnostica demencia."
            else -> "Posible delirium, con o sin deterioro cognitivo. Requiere evaluación clínica; no es un diagnóstico automático."
        }
    }
}
