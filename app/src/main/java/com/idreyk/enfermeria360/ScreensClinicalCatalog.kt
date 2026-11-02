package com.idreyk.enfermeria360

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal data class ClinicalLoad(val catalog: ClinicalCatalog? = null, val failed: Boolean = false)

/** Offline asset, loaded off the main thread; no accounts, remote calls or patient data. */
@Composable
internal fun rememberClinicalCatalog(): ClinicalLoad {
    val context = LocalContext.current.applicationContext
    val load = remember(context) { mutableStateOf(ClinicalLoad()) }
    LaunchedEffect(context) {
        load.value = try {
            withContext(Dispatchers.IO) {
                val text = context.assets.open(ClinicalCatalog.ASSET).bufferedReader(Charsets.UTF_8).use { it.readText() }
                ClinicalLoad(ClinicalCatalog.parse(text))
            }
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            ClinicalLoad(failed = true)
        }
    }
    return load.value
}

internal fun clinicalColor(area: String): Color = when (area) {
    "Neurología" -> Blue
    "Dolor" -> Pink
    "Neonatología" -> Teal
    "Piel y caídas" -> Gold
    "Funcionalidad" -> Green
    else -> Purple
}

@Composable
private fun ClinicalLoadNotice(load: ClinicalLoad) {
    if (load.failed) Notice("No se pudo cargar la biblioteca documental. Los módulos existentes siguen disponibles desde Escalas. Revisa los archivos de esta compilación.", true)
    else Text("Cargando biblioteca local…", style = MaterialTheme.typography.bodyMedium)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClinicalCatalogScreen(nav: (String) -> Unit, back: () -> Unit) {
    val load = rememberClinicalCatalog()
    val catalog = load.catalog
    var query by rememberSaveable { mutableStateOf("") }
    var population by rememberSaveable { mutableStateOf("") }
    var area by rememberSaveable { mutableStateOf("") }
    var withPractice by rememberSaveable { mutableStateOf(false) }
    Page("Tests y escalas clínicas", "Biblioteca por población · sin conexión", back) {
        if (catalog == null) {
            ClinicalLoadNotice(load)
        } else {
            AppCard(Modifier.fillMaxWidth()) {
                ColorIcon(R.drawable.ic_clipboard_check, Teal, 48)
                Badge("INVESTIGACIÓN · ${catalog.reviewed}", Teal)
                Text("Valorar empieza por elegir bien.", style = MaterialTheme.typography.titleLarge)
                Text(
                    "${catalog.instruments.size} fichas documentales · ${catalog.instruments.count { it.hasPractice }} con práctica, simulación o registro. No todas son calculadoras.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text("La misma escala, su población y su versión. Nunca un resultado fuera de contexto.", color = LocalPalette.current.muted, style = MaterialTheme.typography.bodySmall)
            }
            Notice("Primer bloque, no inventario universal. La validación clínica de esta app sigue pendiente. No usar puntajes para decidir diagnósticos o tratamientos ni demorar una emergencia.", true)
            SearchBox(query, { query = it.take(160) }, "Buscar Glasgow, dolor, 4AT, Barthel…")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                (mapOf("" to "Todas") + ClinicalCatalog.POPULATIONS).forEach { (key, label) ->
                    FilterChip(
                        selected = population == key,
                        onClick = { population = key },
                        label = { Text(label) },
                        modifier = Modifier.heightIn(min = 48.dp).testTag("poblacion-${key.ifEmpty { "todas" }}"),
                    )
                }
            }
            val areas = catalog.instruments.map { it.area }.distinct()
            ChoiceField("Área de valoración", listOf("Todas las áreas" to 0) + areas.mapIndexed { i, label -> label to i + 1 }, areas.indexOf(area) + 1, showPoints = false) {
                area = if (it == 0) "" else areas[it - 1]
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(withPractice, { withPractice = it }, Modifier.semantics { contentDescription = "Solo con práctica o registro" })
                Text("Solo con práctica o registro", style = MaterialTheme.typography.bodyMedium)
            }
            val matches = catalog.filter(query, population, area, withPractice)
            Badge("${matches.size} FICHAS EN ESTA VISTA", Purple)
            if (matches.isEmpty()) {
                EmptyState("Sin coincidencias", "Cambia la búsqueda o los filtros. Una escala puede corresponder a otra población o estar pendiente.")
                Secondary("Limpiar filtros", { query = ""; population = ""; area = ""; withPractice = false })
            }
            matches.forEach { instrument ->
                AppCard(Modifier.fillMaxWidth().testTag("ficha-${instrument.id}"), onClick = { nav("instrumento/${instrument.id}") }) {
                    Badge(instrument.area.uppercase(), clinicalColor(instrument.area))
                    Text(instrument.name, style = MaterialTheme.typography.titleMedium)
                    Text(instrument.purpose, style = MaterialTheme.typography.bodyMedium, color = LocalPalette.current.muted)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Badge(instrument.availability.label, Purple)
                        instrument.populations.forEach { Badge(ClinicalCatalog.POPULATIONS.getValue(it), Teal) }
                    }
                    Text("Ver población, límites y fuentes →", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
            Text("Los colores organizan áreas, no expresan gravedad. No se almacenan datos reales de pacientes en estas fichas.", style = MaterialTheme.typography.bodySmall, color = LocalPalette.current.muted)
        }
    }
}

@Composable
fun ClinicalInstrumentScreen(id: String, nav: (String) -> Unit, back: () -> Unit) {
    val load = rememberClinicalCatalog()
    val catalog = load.catalog
    val instrument = catalog?.instruments?.find { it.id == id }
    Page(instrument?.name ?: "Ficha clínica", "Propósito · población · versión · fuentes", back) {
        when {
            catalog == null -> ClinicalLoadNotice(load)
            instrument == null -> EmptyState("Ficha no encontrada", "Vuelve a la biblioteca para seleccionar un instrumento de esta edición.")
            else -> {
                AppCard(Modifier.fillMaxWidth()) {
                    Badge(instrument.area.uppercase(), clinicalColor(instrument.area))
                    Text(instrument.purpose, style = MaterialTheme.typography.titleMedium)
                    Badge(instrument.availability.label, Purple)
                    Text("Revisión documental: ${instrument.reviewed} · revisión clínica: pendiente", style = MaterialTheme.typography.bodySmall, color = LocalPalette.current.muted)
                }
                Notice("Edición de práctica y consulta. Una ficha documentada o un cálculo probado no equivalen a un instrumento digital clínicamente validado.", true)
                Section("Población y versión")
                AppCard(Modifier.fillMaxWidth()) {
                    Text(instrument.populationDetail, style = MaterialTheme.typography.bodyMedium)
                    Text(instrument.version, style = MaterialTheme.typography.bodySmall, color = LocalPalette.current.muted)
                }
                Section("Método y alcance actual")
                Text(instrument.method, style = MaterialTheme.typography.bodyMedium)
                Section("Límites que debes conservar")
                Notice(instrument.limitations, true)
                if (instrument.hasPractice) {
                    val label = when (instrument.availability) {
                        ClinicalAvailability.RECORD -> "Abrir registro de práctica"
                        ClinicalAvailability.SIMULATION -> "Abrir simulación"
                        else -> "Abrir práctica interactiva"
                    }
                    Primary(label, { nav(instrument.route) })
                } else {
                    Notice("Esta ficha NO administra ni calcula el instrumento. No se han añadido preguntas o imágenes de reemplazo.")
                }
                Expandable("Derechos y reproducción", instrument.rights)
                Expandable("Qué falta antes de ampliarlo", instrument.nextStep)
                Section("Fuentes y profundidad de revisión")
                instrument.sourceIds.forEach { sourceId ->
                    val source = catalog.sources.getValue(sourceId)
                    SourceCard(source.reference)
                    Text("Alcance de consulta: ${source.reading}.", style = MaterialTheme.typography.bodySmall, color = LocalPalette.current.muted)
                }
            }
        }
    }
}

val PainMeasurementReference = Reference(
    "NRS y VAS · Evaluations and measurements",
    "https://www.iasp-pain.org/wp-content/uploads/2021/11/IASP-MPC_toolkit_chapt4.pdf",
    "International Association for the Study of Pain · referencia conceptual, extracto consultado",
    "2026-11-02",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NumericPainPracticeScreen(vm: AppViewModel, back: () -> Unit) {
    var selected by rememberSaveable { mutableIntStateOf(NursingScales.UNSET) }
    var context by rememberSaveable { mutableIntStateOf(0) }
    val score = ClinicalPractice.numericPain(selected)
    Page("Escala numérica del dolor", "Autoinforme 0–10 · práctica, no EVA", back) {
        Notice("Usa solo un ejemplo ficticio de una persona que comprenda la escala y pueda autoinformar. Esta adaptación digital no tiene validación clínica. No puntúes por la apariencia ni indiques tratamiento por la cifra.", true)
        ChoiceField("Contexto del ejemplo", listOf("En reposo" to 0, "En movimiento" to 1), context, showPoints = false) {
            context = it
            selected = NursingScales.UNSET
        }
        AppCard(Modifier.fillMaxWidth()) {
            Badge("ELECCIÓN EXPLÍCITA · SIN VALOR PRESELECCIONADO", Pink)
            Text("¿Qué número indica el dolor del ejemplo?", style = MaterialTheme.typography.titleMedium)
            Text("0: sin dolor · 10: máximo dolor imaginable", style = MaterialTheme.typography.bodyMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                (0..10).forEach { number ->
                    FilterChip(
                        selected == number,
                        { selected = number },
                        label = { Text(number.toString(), style = MaterialTheme.typography.titleMedium) },
                        modifier = Modifier.sizeIn(minWidth = 52.dp, minHeight = 48.dp).testTag("dolor-$number")
                            .semantics { contentDescription = "Autoinforme de dolor $number de 10" },
                    )
                }
            }
        }
        Secondary("No puede autoinformar", { selected = ClinicalPractice.NO_SELF_REPORT })
        AppCard(Modifier.fillMaxWidth()) {
            Badge("REGISTRO DE PRÁCTICA", Pink)
            Text(score?.let { "$it / 10" } ?: if (selected == ClinicalPractice.NO_SELF_REPORT) "Sin autoinforme: sin puntaje" else "Aún sin respuesta", style = MaterialTheme.typography.headlineMedium, color = Pink)
            Text(
                if (selected == ClinicalPractice.NO_SELF_REPORT) "No se asigna cero. La selección de otro instrumento requiere valorar población y contexto."
                else "La cifra no clasifica gravedad ni recomienda una intervención. Cambiar el contexto borra el puntaje anterior.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Primary("Guardar autoinforme de práctica", {
            score?.let {
                vm.practice("Dolor · escala numérica", "$it/10. Contexto: ${if (context == 0) "reposo" else "movimiento"}. Autoinforme ficticio; no EVA, sin interpretación terapéutica. App ${BuildConfig.VERSION_NAME}.")
            }
        }, score != null && !vm.busy)
        Secondary("Reiniciar autoinforme", { selected = NursingScales.UNSET })
        Expandable("Pediatría y comunicación", "No basta la edad: confirmar comprensión de los extremos y del orden numérico. Las referencias pediátricas ofrecen edades orientativas distintas. Si la persona no puede responder no debe inventarse un valor ni observar su cara como sustituto del autoinforme.")
        SourceCard(PainMeasurementReference)
        SourceCard(Reference("Dolor pediátrico: elegir un instrumento apropiado al desarrollo", "https://www.rch.org.au/clinicalguide/guideline_index/Acute_pain_management/", "Royal Children’s Hospital Melbourne · extracto consultado", "2026-11-02"))
    }
}

private val FourAtInstructions = listOf(
    "Observar el estado de alerta. Si está dormido, la ficha permite despertar con la voz o un toque suave en el hombro. La somnolencia leve de menos de diez segundos al despertar seguida de normalidad puntúa cero; la alteración clara puntúa cuatro.",
    "Edad, fecha de nacimiento, lugar y año actual. Aquí solo se registra el número de errores del ejemplo; no escribas respuestas identificables. Dos o más errores o imposibilidad de realizar el test puntúan dos.",
    "Meses del año hacia atrás desde diciembre. Se permite la ayuda inicial de preguntar el mes anterior a diciembre. Negarse a iniciar y no poder iniciar por malestar/somnolencia/inatención son opciones diferentes.",
    "Evidencia de cambio significativo o fluctuación de alerta, cognición u otra función mental que surge en las últimas dos semanas y sigue evidente en las últimas veinticuatro horas. Usar historia, observación o informantes; desconocido no se rellena automáticamente como «No».",
)

@Composable
fun FourAtPracticeScreen(vm: AppViewModel, back: () -> Unit) {
    var confirmedAdult by rememberSaveable { mutableStateOf(false) }
    var scores by rememberSaveable { mutableStateOf(IntArray(4) { NursingScales.UNSET }) }
    val definition = ClinicalPractice.fourAt
    val total = ClinicalPractice.fourAtTotal(scores.toList())
    val completed = scores.count { it != NursingScales.UNSET }
    Page("4AT · práctica", "Cribado de delirium · adaptación digital", back) {
        Notice("Solo ejemplo adulto, no pediatría/neonatos. El cribado no diagnostica delirium o demencia y no debe demorar atención ante un cambio agudo. Esta adaptación digital requiere revisión clínica independiente.", true)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(confirmedAdult, {
                confirmedAdult = it
                scores = IntArray(4) { NursingScales.UNSET }
            }, Modifier.testTag("4at-adulto").semantics { contentDescription = "Confirmar ejemplo ficticio adulto" })
            Text("Es un ejemplo ficticio adulto", style = MaterialTheme.typography.bodyMedium)
        }
        if (!confirmedAdult) {
            Notice("Confirma la población del ejemplo antes de explorar los componentes. No se guardan respuestas personales.")
        } else {
            Badge("$completed DE 4 COMPONENTES", Teal)
            ProgressLine(completed / 4f)
            definition.items.forEachIndexed { i, item ->
                ChoiceField(item.label, item.choices, scores[i]) { value ->
                    scores = scores.copyOf().apply { this[i] = value }
                }
                Expandable("Guía del componente ${i + 1}", FourAtInstructions[i])
            }
            Notice("En 4AT, «no valorable» en AMT4 o atención tiene DOS puntos. No se usa la regla de Glasgow: son instrumentos distintos.")
        }
        AppCard(Modifier.fillMaxWidth()) {
            Badge("RESULTADO DE CRIBADO · EJEMPLO", Teal)
            Text(total?.let { "$it / 12" } ?: "Completa los cuatro componentes", style = MaterialTheme.typography.headlineMedium, color = Teal)
            if (total != null) Text(ClinicalPractice.fourAtMeaning(total), style = MaterialTheme.typography.bodyMedium)
            else if (scores.filter { it >= 0 }.sum() >= 4) Text("Los componentes registrados ya alcanzan al menos cuatro puntos. Falta completar la captura, pero eso no debe retrasar una valoración clínica. No se presenta un total final.", style = MaterialTheme.typography.bodyMedium)
            else Text("Pendiente de seleccionar no significa normal. La incapacidad de realizar AMT4 o atención tiene una opción puntuable; no es una respuesta en blanco.", style = MaterialTheme.typography.bodySmall)
        }
        Primary("Guardar práctica 4AT", {
            total?.let {
                vm.practice("4AT · práctica", "Componentes: ${scores.joinToString("/")}. Total $it/12. 4AT v1.2; adaptación digital educativa de traducción española 2016. Cribado, no diagnóstico. App ${BuildConfig.VERSION_NAME}.")
            }
        }, confirmedAdult && total != null && !vm.busy)
        Secondary("Reiniciar 4AT", { scores = IntArray(4) { NursingScales.UNSET } })
        Expandable("Atribución y cambios", "Adaptado de 4AT ©2011–2014 Alasdair MacLullich, Tracy Ryan y Helen Cash, CC BY 4.0 (https://creativecommons.org/licenses/by/4.0/). Español: Sander, Corretge y Dorrian, 2016. Interfaz, instrucciones auxiliares y presentación modificadas; se conservan dominios y puntos. La evidencia del original no valida automáticamente esta adaptación. Sin garantía de exactitud o adecuación clínica; sin aval de los autores. Fuente oficial: https://www.the4at.com/.")
        SourceCard(Reference("4AT español · versión 1.0 (2016), instrumento 1.2", "https://www.the4at.com/wp-content/uploads/2026/07/4AT-Spanish.doc", "Sander, Corretge, Dorrian / MacLullich, Ryan y Cash", "2026-11-02"))
        SourceCard(Reference("4AT: guía de administración, puntuación y límites", "https://www.the4at.com/userguide", "Equipo 4AT", "2026-11-02"))
        SourceCard(Reference("4AT: CC BY 4.0, atribución y adaptaciones", "https://www.the4at.com/attribution", "Equipo 4AT · no respalda esta app", "2026-11-02"))
    }
}
