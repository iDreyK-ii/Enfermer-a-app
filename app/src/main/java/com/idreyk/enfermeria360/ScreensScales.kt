package com.idreyk.enfermeria360

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val NomNeonatalReference =
    Reference(
        "NOM-007-SSA2-2016 · apéndices G y H",
        "https://www.gob.mx/cms/uploads/attachment/file/512098/NOM-007-SSA2-2016.pdf",
        "Secretaría de Salud de México · referencia de la variante, no protocolo local",
        "2026-11-01",
    )
val CapurroBReference =
    Reference(
        "NTS 214-MINSA/DGIESP-2024 · anexo 4, página 40",
        "https://cdn.www.gob.pe/uploads/document/file/6780375/5874349-resolucion-ministerial-n-545-2024-minsa.pdf",
        "MINSA · aprobado por RM 545-2024/MINSA",
        "2026-11-01",
    )
val ApgarReference =
    Reference(
        "Apgar · componentes y frecuencia cardiaca de 100 o más",
        "https://www.msdmanuals.com/medical-calculators/ApgarScore-en.htm",
        "MSD Manuals · referencia aritmética",
        "2026-11-01",
    )

@Composable
fun ScaleLibraryScreen(nav: (String) -> Unit, back: () -> Unit) {
    Page("Escalas y valoración", "Cálculo, registro y fuentes identificadas", back) {
        Notice(
            "Edición de desarrollo y práctica. Los puntajes no generan diagnósticos ni tratamientos. Las variantes y límites se indican en cada pantalla.",
            true,
        )
        Section("Neurología y dolor")
        ActionRow(
            "Glasgow adulto",
            "Componentes y NV · sin clasificar gravedad",
            R.drawable.ic_activity,
            Blue,
        ) {
            nav("glasgow")
        }
        ActionRow(
            "Glasgow pediátrica",
            "Registro de componentes de una ficha externa",
            R.drawable.ic_clipboard,
            Blue,
        ) {
            nav("glasgow-pediatrico")
        }
        ActionRow(
            "EVA digital",
            "Línea continua · simulación no calibrada",
            R.drawable.ic_heart_pulse,
            Pink,
        ) {
            nav("eva")
        }
        Section("Neonatología")
        ActionRow(
            "Capurro A y B",
            "Variantes separadas · semanas y días",
            R.drawable.ic_stethoscope,
            Teal,
        ) {
            nav("capurro")
        }
        ActionRow("Apgar", "Cinco componentes · momento del registro", R.drawable.ic_heart, Pink) {
            nav("escala/apgar")
        }
        ActionRow(
            "Silverman-Andersen",
            "Cinco componentes · referencia de la variante",
            R.drawable.ic_wind,
            Teal,
        ) {
            nav("escala/silverman")
        }
        Section("Puntuaciones de instrumentos externos")
        ActionRow(
            "Braden",
            "Registrar puntaje externo · no reproduce el cuestionario",
            R.drawable.ic_bed,
            Purple,
        ) {
            nav("registro-escala/braden")
        }
        ActionRow(
            "Morse",
            "Registrar puntaje externo · no reproduce el cuestionario",
            R.drawable.ic_shield_check,
            Gold,
        ) {
            nav("registro-escala/morse")
        }
    }
}

@Composable
fun CapurroCalculatorScreen(vm: AppViewModel, back: () -> Unit) {
    var variant by rememberSaveable { mutableStateOf("B") }
    val definition = if (variant == "A") NursingScales.capurroA else NursingScales.capurroB
    var scores by
        rememberSaveable(variant) {
            mutableStateOf(IntArray(definition.items.size) { NursingScales.UNSET })
        }
    val result = NursingScales.capurro(variant, scores.toList())
    Page("Capurro · A / B", "Estimación de práctica, no datación exacta", back) {
        Notice(
            "No sustituye la datación obstétrica ni una valoración neonatal profesional. La aplicación del método y sus limitaciones deben revisarse antes de usarlo en atención real.",
            true,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("B", "A").forEach { v ->
                FilterChip(variant == v, { variant = v }, label = { Text("Capurro $v") })
            }
        }
        Text(definition.title, style = MaterialTheme.typography.titleLarge)
        if (variant == "A")
            Notice(
                "A usa cuatro componentes somáticos (sin formación del pezón) y dos neurológicos. Transcribe puntos de una ficha autorizada; esta pantalla no enseña maniobras ni decide si corresponde aplicar la variante."
            )
        else
            Notice(
                "B usa cinco componentes somáticos. Selección y fórmula revisadas documentalmente con el anexo 4 de la NTS 214; la implementación digital aún necesita revisión clínica independiente."
            )
        Badge("${scores.count { it != NursingScales.UNSET }} / ${scores.size} COMPONENTES", Teal)
        definition.items.forEachIndexed { i, item ->
            ChoiceField(item.label, item.choices, scores[i]) { value ->
                scores = scores.copyOf().apply { this[i] = value }
            }
        }
        Text(
            "Días = ${if (variant == "A") 200 else 204} + suma de puntos; semanas = días / 7",
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
        AppCard(Modifier.fillMaxWidth()) {
            Badge("RESULTADO ARITMÉTICO DE PRÁCTICA", Teal)
            if (result == null)
                Text("Completa todos los componentes", style = MaterialTheme.typography.titleMedium)
            else {
                Text(
                    "${result.weeks} semanas + ${result.remainingDays} ${if(result.remainingDays==1) "día" else "días"}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Teal,
                )
                Text(
                    "Suma: ${result.points} puntos · ${result.days} días calculados",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "El número de días es una representación de la fórmula, no una medida exacta de edad gestacional. No se clasifica ni se indica una conducta.",
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalPalette.current.muted,
                )
            }
        }
        Primary(
            "Guardar práctica de Capurro",
            {
                result?.let { r ->
                    vm.practice(
                        "Capurro $variant",
                        "${definition.items.indices.joinToString("; ") { "${definition.items[it].label}: ${scores[it]}" }}. Suma ${r.points}; ${r.weeks} semanas + ${r.remainingDays} días. Referencia: ${if(variant=="B") "NTS 214-2024, anexo 4" else "composición 4+2, constante 200; NOM-007-2016"}. App v0.3. Estimación de práctica, no validación clínica.",
                    )
                }
            },
            result != null && !vm.busy,
        )
        Secondary(
            "Reiniciar componentes",
            { scores = IntArray(definition.items.size) { NursingScales.UNSET } },
        )
        SourceCard(if (variant == "B") CapurroBReference else NomNeonatalReference)
        if (variant == "A")
            Text(
                "Los nombres A/B varían en materiales secundarios. Aquí se identifica además la composición 4+2 y la constante 200 para evitar mezclar variantes.",
                style = MaterialTheme.typography.bodySmall,
                color = LocalPalette.current.muted,
            )
    }
}

@Composable
fun NeonatalScoreScreen(id: String, vm: AppViewModel, back: () -> Unit) {
    val definition = if (id == "apgar") NursingScales.apgar else NursingScales.silverman
    var scores by rememberSaveable(id) { mutableStateOf(IntArray(5) { NursingScales.UNSET }) }
    var minute by rememberSaveable(id) { mutableIntStateOf(1) }
    val total = NursingScales.total(definition, scores.toList())
    Page(definition.title, "Selección de componentes · práctica", back) {
        Notice(
            "No esperar un puntaje para atender una emergencia. Este módulo no dirige reanimación, soporte respiratorio ni pronóstico; requiere revisión clínica antes del uso asistencial.",
            true,
        )
        if (id == "apgar") {
            ChoiceField(
                "Minuto del registro de ejemplo",
                listOf(1, 5, 10, 15, 20).map { "$it min" to it },
                minute,
                showPoints = false,
            ) {
                minute = it
                scores = IntArray(5) { NursingScales.UNSET }
            }
            Text(
                "Cambiar el minuto reinicia las respuestas. La valoración de coloración tiene limitaciones según el fototipo; no sustituye otras mediciones.",
                style = MaterialTheme.typography.bodySmall,
                color = LocalPalette.current.muted,
            )
        } else
            Notice(
                "Se identifican los descriptores de la referencia NOM-007-SSA2-2016. No mezclar con formularios adaptados que definan los componentes de otro modo."
            )
        definition.items.forEachIndexed { i, item ->
            ChoiceField(item.label, item.choices, scores[i]) { value ->
                scores = scores.copyOf().apply { this[i] = value }
            }
        }
        AppCard(Modifier.fillMaxWidth()) {
            Badge("SUMA DE PRÁCTICA", Teal)
            Text(
                total?.let { "$it / ${definition.maximum}" } ?: "Completa los cinco componentes",
                style = MaterialTheme.typography.headlineMedium,
                color = Teal,
            )
            Text(
                "Sin categorías de gravedad ni recomendaciones automáticas.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Primary(
            "Guardar puntaje de práctica",
            {
                total?.let {
                    vm.practice(
                        definition.title,
                        (if (id == "apgar") "Minuto $minute. " else "") +
                            definition.items.indices.joinToString("; ") { i ->
                                "${definition.items[i].label}: ${scores[i]}"
                            } +
                            ". Total $it/10. Sin interpretación clínica. App v0.3; ${if(id=="apgar") "referencia MSD" else "referencia NOM-007-2016"}.",
                    )
                }
            },
            total != null && !vm.busy,
        )
        Secondary("Reiniciar respuestas", { scores = IntArray(5) { NursingScales.UNSET } })
        SourceCard(if (id == "apgar") ApgarReference else NomNeonatalReference)
    }
}

@Composable
fun PediatricGlasgowRecord(vm: AppViewModel, back: () -> Unit) {
    var eye by rememberSaveable { mutableIntStateOf(0) }
    var verbal by rememberSaveable { mutableIntStateOf(0) }
    var motor by rememberSaveable { mutableIntStateOf(0) }
    var version by rememberSaveable { mutableStateOf("") }
    val total = PracticeMath.glasgow(eye, verbal, motor)
    fun component(n: Int) = if (n < 0) "NV" else if (n == 0) "—" else "$n"
    Page("Glasgow pediátrica", "Transcripción de una valoración externa", back) {
        Notice(
            "No se aplican automáticamente los descriptores adultos a niños. Registra componentes obtenidos con una versión pediátrica apropiada para edad y desarrollo. Esta pantalla no reproduce ni valida esa versión.",
            true,
        )
        Input("Fuente / versión pediátrica utilizada", version, { version = it.take(160) })
        listOf(Triple("Ocular", 4, eye), Triple("Verbal", 5, verbal), Triple("Motora", 6, motor))
            .forEachIndexed { i, (title, max, value) ->
                ChoiceField(
                    title,
                    (1..max).map { "Puntos externos: $it" to it } + ("No valorable (NV)" to -1),
                    value,
                ) { n ->
                    when (i) {
                        0 -> eye = n
                        1 -> verbal = n
                        else -> motor = n
                    }
                }
            }
        Text(
            "O${component(eye)} · V${component(verbal)} · M${component(motor)}",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            total?.let { "$it / 15" } ?: "Sin total: componente pendiente o NV",
            style = MaterialTheme.typography.headlineMedium,
            color = Blue,
        )
        Primary(
            "Guardar registro externo",
            {
                vm.practice(
                    "Glasgow pediátrica · externo",
                    "$version. O${component(eye)} V${component(verbal)} M${component(motor)}. ${total?.let { "$it/15" } ?: "Sin total (NV)"}. Transcripción, sin interpretación.",
                )
            },
            version.isNotBlank() && eye != 0 && verbal != 0 && motor != 0 && !vm.busy,
        )
        SourceCard(GlasgowReference)
    }
}

@Composable
fun ExternalScaleScreen(id: String, vm: AppViewModel, back: () -> Unit) {
    val name = if (id == "braden") "Braden" else "Morse"
    var value by rememberSaveable(id) { mutableStateOf("") }
    var origin by rememberSaveable(id) { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    Page(
        "$name · registro externo",
        "Registro de un puntaje, no aplicación del instrumento",
        back,
    ) {
        Notice(
            "No se incluye el cuestionario ni sus criterios: los permisos para redistribuir el instrumento no están aportados. Puedes registrar un resultado ficticio obtenido con una ficha externa autorizada. No se interpreta el riesgo.",
            true,
        )
        Text(
            "Solo se comprueba el formato y el rango, no la coherencia de la valoración externa.",
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
        Input(
            "Puntaje de la ficha externa",
            value,
            {
                value = it
                error = ""
            },
            numeric = true,
        )
        Text(
            if (id == "braden") "Versión de referencia: total entero entre 6 y 23."
            else "Versión de referencia: total entero entre 0 y 125.",
            style = MaterialTheme.typography.bodySmall,
        )
        Input(
            "Fuente, versión y contexto de práctica",
            origin,
            { origin = it.take(500) },
            multi = true,
        )
        if (error.isNotBlank()) Notice(error, true)
        Primary(
            "Guardar puntaje externo",
            {
                try {
                    val score = NursingScales.externalScore(id, value)
                    vm.practice(
                        "$name · externo",
                        "$origin. Puntaje transcrito: $score. No calculado ni interpretado por la app.",
                    )
                    error = ""
                } catch (e: IllegalArgumentException) {
                    error = e.message.orEmpty()
                }
            },
            value.isNotBlank() && origin.isNotBlank() && !vm.busy,
        )
        SourceCard(
            if (id == "braden")
                Reference(
                    "Condiciones de uso del instrumento Braden",
                    "https://loinc.org/38228-3",
                    "Referencia de derechos, no autorización para esta app",
                    "2026-11-01",
                )
            else
                Reference(
                    "Morse Fall Scale · información de permisos",
                    "https://falltips.org/faqs/",
                    "Fall TIPS · uso sin coste no equivale a permiso de redistribución",
                    "2026-11-01",
                )
        )
    }
}
