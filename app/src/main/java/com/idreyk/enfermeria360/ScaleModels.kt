package com.idreyk.enfermeria360

/** Independent scoring arithmetic. No severity category or treatment is inferred. */
data class ScaleItem(val label: String, val choices: List<Pair<String, Int>>)

data class ScaleDefinition(
    val id: String,
    val title: String,
    val items: List<ScaleItem>,
    val maximum: Int,
)

data class GestationalEstimate(val points: Int, val days: Int) {
    val weeks: Int
        get() = days / 7

    val remainingDays: Int
        get() = days % 7
}

object NursingScales {
    const val UNSET = -99

    private fun item(label: String, vararg options: Pair<String, Int>) =
        ScaleItem(label, options.toList())

    private fun numerical(label: String, values: List<Int>) =
        ScaleItem(label, values.map { "Puntaje de la ficha: $it" to it })

    val capurroB =
        ScaleDefinition(
            "capurro-b",
            "Capurro B · somático",
            listOf(
                item(
                    "Forma de la oreja",
                    "Sin incurvación" to 0,
                    "Borde superior parcialmente curvado" to 8,
                    "Borde superior completamente curvado" to 16,
                    "Pabellón totalmente incurvado" to 24,
                ),
                item(
                    "Glándula mamaria",
                    "No palpable" to 0,
                    "Palpable, menor de 5 mm" to 5,
                    "De 5 a 10 mm" to 10,
                    "Mayor de 10 mm" to 15,
                ),
                item(
                    "Formación del pezón",
                    "Apenas visible, sin areola" to 0,
                    "Menor de 7,5 mm, areola lisa" to 5,
                    "Mayor de 7,5 mm, areola punteada sin relieve" to 10,
                    "Mayor de 7,5 mm, borde areolar elevado" to 15,
                ),
                item(
                    "Textura cutánea",
                    "Muy fina, gelatinosa" to 0,
                    "Fina y lisa" to 5,
                    "Descamación superficial discreta" to 10,
                    "Gruesa, grietas superficiales" to 15,
                    "Gruesa, grietas profundas" to 20,
                ),
                item(
                    "Pliegues plantares",
                    "Ausentes" to 0,
                    "Marcas poco definidas en mitad anterior" to 5,
                    "Marcas definidas y primeros surcos anteriores" to 10,
                    "Surcos en mitad anterior" to 15,
                    "Surcos más allá de mitad anterior" to 20,
                ),
            ),
            94,
        )

    // A = four somatic components (without nipple formation) plus two neurological scores.
    // Neurological scoring must be obtained from a correctly applied, authorized source instrument.
    val capurroA =
        ScaleDefinition(
            "capurro-a",
            "Capurro A · registro de componentes",
            listOf(
                numerical("Forma de la oreja", listOf(0, 8, 16, 24)),
                numerical("Glándula mamaria", listOf(0, 5, 10, 15)),
                numerical("Textura cutánea", listOf(0, 5, 10, 15, 20)),
                numerical("Pliegues plantares", listOf(0, 5, 10, 15, 20)),
                numerical("Signo de la bufanda · puntaje externo", listOf(0, 6, 12, 18)),
                numerical("Caída de la cabeza · puntaje externo", listOf(0, 4, 8, 12)),
            ),
            109,
        )

    val apgar =
        ScaleDefinition(
            "apgar",
            "Apgar",
            listOf(
                item(
                    "Frecuencia cardiaca",
                    "Ausente" to 0,
                    "Menor de 100 latidos/min" to 1,
                    "100 latidos/min o más" to 2,
                ),
                item(
                    "Esfuerzo respiratorio",
                    "Ausente" to 0,
                    "Lento o irregular" to 1,
                    "Respiración eficaz, llanto vigoroso" to 2,
                ),
                item(
                    "Tono muscular",
                    "Flacidez" to 0,
                    "Flexión parcial de extremidades" to 1,
                    "Movimiento activo" to 2,
                ),
                item(
                    "Respuesta refleja",
                    "Sin respuesta" to 0,
                    "Mueca o respuesta débil" to 1,
                    "Llanto, tos o estornudo vigoroso" to 2,
                ),
                item(
                    "Coloración",
                    "Palidez o coloración azulada general" to 0,
                    "Tronco rosado, extremidades azuladas" to 1,
                    "Coloración rosada general" to 2,
                ),
            ),
            10,
        )

    val silverman =
        ScaleDefinition(
            "silverman",
            "Silverman-Andersen",
            listOf(
                item(
                    "Movimiento toracoabdominal",
                    "Sincronizado" to 0,
                    "Predominio abdominal; retraso torácico" to 1,
                    "Movimiento paradójico" to 2,
                ),
                item("Tiraje intercostal", "Ausente" to 0, "Discreto" to 1, "Acentuado" to 2),
                item("Retracción xifoidea", "Ausente" to 0, "Discreta" to 1, "Acentuada" to 2),
                item("Aleteo nasal", "Ausente" to 0, "Discreto" to 1, "Acentuado" to 2),
                item(
                    "Quejido espiratorio",
                    "Ausente" to 0,
                    "Leve o inconstante" to 1,
                    "Acentuado y constante" to 2,
                ),
            ),
            10,
        )

    fun total(definition: ScaleDefinition, scores: List<Int>): Int? {
        require(scores.size == definition.items.size) { "Cantidad de componentes incorrecta." }
        scores.forEachIndexed { index, value ->
            require(value == UNSET || definition.items[index].choices.any { it.second == value }) {
                "Puntaje no permitido en ${definition.items[index].label}."
            }
        }
        if (scores.any { it == UNSET }) return null
        return scores.sum()
    }

    fun capurro(variant: String, scores: List<Int>): GestationalEstimate? {
        require(variant in listOf("A", "B")) { "Variante de Capurro desconocida." }
        val points = total(if (variant == "A") capurroA else capurroB, scores) ?: return null
        return GestationalEstimate(points, (if (variant == "A") 200 else 204) + points)
    }

    fun externalScore(kind: String, text: String): Int {
        val value =
            text.trim().toIntOrNull()
                ?: throw IllegalArgumentException(
                    "Transcribe un puntaje entero de la ficha externa."
                )
        val range =
            when (kind) {
                "braden" -> 6..23
                "morse" -> 0..125
                "norton" -> 5..20
                "rass" -> -5..4
                else -> throw IllegalArgumentException("Registro de escala no reconocido.")
            }
        require(value in range) {
            "Puntaje fuera del rango de la versión indicada (${range.first}–${range.last})."
        }
        return value
    }
}
