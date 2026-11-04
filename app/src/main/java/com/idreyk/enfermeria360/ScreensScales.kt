package com.idreyk.enfermeria360

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.semantics.Role
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
        AppCard(Modifier.fillMaxWidth()) {
            ClinicalAreaIllustration(ClinicalVisualLearning.general)
            Badge("BIBLIOTECA CLÍNICA · PRIMER BLOQUE", Teal)
            Text("El instrumento correcto, con contexto.", style = MaterialTheme.typography.titleLarge)
            Text("Explora por población y área. Cada ficha distingue práctica, registro, simulación o documentación pendiente.", style = MaterialTheme.typography.bodyMedium)
            Primary("Explorar tests y escalas", { nav("biblioteca-escalas") })
        }
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
        ActionRow("Escala numérica del dolor", "Autoinforme 0–10 · no es EVA", R.drawable.ic_heart_pulse, Pink) {
            nav("dolor-numerico")
        }
        Section("Delirium · adulto")
        ActionRow("4AT", "Adaptación de práctica · cribado, no diagnóstico", R.drawable.ic_clipboard_check, Purple) {
            nav("test-4at")
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
        ActionRow("Norton · registro externo", "Adulto · transcripción, no aplicación", R.drawable.ic_bed, Purple) { nav("registro-escala/norton") }
        ActionRow("RASS · registro externo", "Adulto · transcripción, no formulario", R.drawable.ic_clipboard, Teal) { nav("registro-escala/rass") }
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
fun CapurroCalculatorScreen(vm: AppViewModel, back: () -> Unit, initialVariant: String = "B") {
    require(initialVariant in listOf("A", "B"))
    var variant by rememberSaveable(initialVariant) { mutableStateOf(initialVariant) }
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
                        "${definition.items.indices.joinToString("; ") { "${definition.items[it].label}: ${scores[it]}" }}. Suma ${r.points}; ${r.weeks} semanas + ${r.remainingDays} días. Referencia: ${if(variant=="B") "NTS 214-2024, anexo 4" else "composición 4+2, constante 200; NOM-007-2016"}. App ${BuildConfig.VERSION_NAME}. Estimación de práctica, no validación clínica.",
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
                            ". Total $it/10. Sin interpretación clínica. App ${BuildConfig.VERSION_NAME}; ${if(id=="apgar") "referencia MSD" else "referencia NOM-007-2016"}.",
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
    val names = mapOf("braden" to "Braden", "morse" to "Morse", "norton" to "Norton", "rass" to "RASS")
    val name = names[id]
    if (name == null) { Page("Registro no disponible", back = back) { Notice("Elige un instrumento conocido.", true) }; return }
    var value by rememberSaveable(id) { mutableStateOf("") }
    var origin by rememberSaveable(id) { mutableStateOf("") }
    var adult by rememberSaveable(id) { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    val range = when (id) { "braden" -> "6–23"; "norton" -> "5–20"; "rass" -> "−5 a +4"; else -> "0–125" }
    Page("$name · registro externo", "Adulto · no administra ni interpreta el instrumento", back) {
        Notice("No se incluye el cuestionario ni sus criterios. No hay permiso aportado para reproducir o adaptar el formulario completo de esta versión en la app. Solo transcribe un puntaje ficticio de una ficha externa autorizada.", true)
        Text("Comprobar el rango no demuestra que la valoración sea correcta. Una puntuación no genera tratamientos ni un diagnóstico.", style = MaterialTheme.typography.bodySmall)
        Row(Modifier.fillMaxWidth().toggleable(adult, role = Role.Checkbox) { adult = it; error = "" }, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(adult, onCheckedChange = null)
            Text("Confirmo un ejemplo adulto, no pediátrico ni neonatal", Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        }
        Input("Puntaje de la ficha externa", value, { value = calculationInput(it); error = "" }, numeric = id != "rass")
        Text("Versión de referencia: ${if (id == "braden") "Braden original, no Braden II; " else if (id == "norton") "Norton original, no modificada; " else if (id == "rass") "Richmond Agitation-Sedation Scale adulta; " else ""}total entero $range. Vacío no es cero.", style = MaterialTheme.typography.bodySmall)
        Input("Fuente, versión y contexto de práctica", origin, { origin = it.take(500) }, multi = true)
        if (error.isNotBlank()) Notice(error, true)
        Primary("Guardar puntaje externo", {
            try {
                val score = NursingScales.externalScore(id, value)
                vm.practice("$name · externo", "EJERCICIO EDUCATIVO. Adulto. $origin. Puntaje transcrito: $score. No calculado ni interpretado por la app.")
                error = ""
            } catch (e: IllegalArgumentException) { error = e.message.orEmpty() }
        }, adult && value.isNotBlank() && origin.isNotBlank() && !vm.busy)
        SourceCard(when (id) {
            "braden" -> Reference("Braden/Braden II: titular y licencias", "https://www.bradenscale.com/", "Health Sense Ai / HD Nursing · no autorización obtenida", "2026-11-02")
            "norton" -> Reference("Norton · resumen de medición, rango y límites", "https://scireproject.com/wp-content/uploads/2025/01/Clinician-Summary_Norton-Scale-v.8.1.pdf", "SCIRE Professional · resumen consultado; no permiso de formulario", "2026-11-03")
            "rass" -> Reference("RASS · tarjeta de referencia de CIBS Center", "https://cdn.prod.website-files.com/5b0849daec50243a0a1e5e0c/64494326d62bbde72c3bafc4_CAM-ICU-Pocket-Cards.pdf", "ICU Delirium / Vanderbilt · tarjeta consultada; no autorización digital inferida", "2026-11-03")
            else -> Reference("Morse Fall Scale · información de permisos", "https://falltips.org/faqs/", "Fall TIPS · gratuito no equivale a permiso de reproducción", "2026-11-02")
        })
    }
}
