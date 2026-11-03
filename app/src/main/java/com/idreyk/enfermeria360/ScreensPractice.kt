package com.idreyk.enfermeria360

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.*

private fun number(value: String) =
    value.trim().replace(',', '.').toDoubleOrNull()
        ?: throw IllegalArgumentException("Completa los campos con números válidos.")

private fun decimal(n: Double, digits: Int = 1) =
    String.format(Locale.forLanguageTag("es-PE"), "%.${digits}f", n)

internal fun readableTime(ms: Long) =
    Instant.ofEpochMilli(ms)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("dd/MM · HH:mm"))

private val StageNames =
    listOf("Valoración", "Diagnóstico", "Planificación", "Ejecución", "Evaluación")
private val StageIcons =
    listOf(
        R.drawable.ic_clipboard_check,
        R.drawable.ic_heart_pulse,
        R.drawable.ic_target,
        R.drawable.ic_edit,
        R.drawable.ic_check_circle,
    )
private val StageColors = listOf(Purple, Pink, Gold, Blue, Teal)
private val StageTexts =
    listOf(
        "Recoge y organiza la información.",
        "Organiza las respuestas y problemas identificados.",
        "Define objetivos y organiza el cuidado.",
        "Documenta las acciones planificadas.",
        "Revisa los resultados y reconsidera el plan.",
    )
private val Patterns =
    listOf(
        "Percepción y manejo de la salud",
        "Nutricional y metabólico",
        "Eliminación",
        "Actividad y ejercicio",
        "Sueño y descanso",
        "Cognitivo y perceptivo",
        "Autopercepción y autoconcepto",
        "Rol y relaciones",
        "Sexualidad y reproducción",
        "Adaptación y tolerancia al estrés",
        "Valores y creencias",
    )

@Composable
fun PaeScreen(nav: (String) -> Unit, back: () -> Unit, info: (String, String) -> Unit) {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    Page("PAE · ADPIE", "Un cuidado integral, paso a paso", back) {
        Text(
            "Cada etapa cuenta. Elige una para explorar.",
            style = MaterialTheme.typography.bodyMedium,
            color = LocalPalette.current.muted,
        )
        Box(Modifier.size(300.dp).align(Alignment.CenterHorizontally)) {
            Canvas(Modifier.fillMaxSize()) {
                val inset = size.minDimension * .16f
                StageColors.forEachIndexed { i, c ->
                    drawArc(
                        color = c.copy(alpha = if (i == selected) 1f else .76f),
                        startAngle = -124f + i * 72,
                        sweepAngle = 68f,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = Size(size.width - 2 * inset, size.height - 2 * inset),
                        style = Stroke(width = size.minDimension * .24f),
                    )
                }
            }
            StageNames.forEachIndexed { i, name ->
                val a = (-90 + i * 72) * Math.PI / 180
                Column(
                    Modifier.offset(
                            x = (150 + 99 * cos(a) - 43).dp,
                            y = (150 + 99 * sin(a) - 32).dp,
                        )
                        .size(86.dp, 64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selected = i },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Symbol(StageIcons[i], "Etapa $name", Color.White, Modifier.size(23.dp))
                    Text(
                        name,
                        color = Color.White,
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 5.dp),
                    )
                }
            }
            Column(
                Modifier.size(100.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(LocalPalette.current.surface)
                    .border(4.dp, LocalPalette.current.background, CircleShape),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Symbol(R.drawable.ic_clipboard, tint = Teal, modifier = Modifier.size(30.dp))
                Text(
                    "PAE",
                    fontFamily = HeadingFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 27.sp,
                )
                Text("ADPIE", fontSize = 8.sp, color = Purple)
            }
        }
        AppCard(Modifier.fillMaxWidth()) {
            Badge("ETAPA ${selected+1} DE 5", StageColors[selected])
            Text(StageNames[selected], style = MaterialTheme.typography.titleLarge)
            Text(
                StageTexts[selected],
                color = LocalPalette.current.muted,
                style = MaterialTheme.typography.bodyMedium,
            )
            Primary(
                "Explorar etapa",
                {
                    when (selected) {
                        0 -> nav("valoracion")
                        1 -> nav("plan/mario")
                        2,
                        4 -> nav("plan/mario")
                        else -> nav("evolucion/mario")
                    }
                },
            )
        }
        ActionRow(
            "Patrones de Gordon",
            "Once espacios para observaciones ficticias",
            R.drawable.ic_stethoscope,
            Teal,
        ) {
            nav("valoracion")
        }
        Notice(
            "Esta edición organiza el aprendizaje. No propone diagnósticos ni intervenciones para personas reales.",
            true,
        )
    }
}

@Composable
fun AssessmentScreen(vm: AppViewModel, back: () -> Unit) {
    var index by rememberSaveable { mutableIntStateOf(-1) }
    var observation by rememberSaveable { mutableStateOf("") }
    val completed =
        vm.data.notes
            .filter { it.patientId.startsWith("gordon-") }
            .map { it.patientId }
            .distinct()
            .size
    Page("Patrones de Gordon", "Formulario de práctica · sin datos reales", back) {
        Badge("$completed DE 11 ESPACIOS EXPLORADOS", Teal)
        ProgressLine(completed / 11f)
        Patterns.forEachIndexed { i, p ->
            ActionRow(
                "${(i+1).toString().padStart(2,'0')} · $p",
                if (vm.data.notes.any { it.patientId == "gordon-$i" })
                    "Observación ficticia guardada"
                else "Añadir una observación de ejemplo",
                R.drawable.ic_clipboard_check,
                if (vm.data.notes.any { it.patientId == "gordon-$i" }) Teal else Purple,
            ) {
                index = i
                observation =
                    vm.data.notes.firstOrNull { it.patientId == "gordon-$i" }?.text.orEmpty()
            }
        }
        Notice(
            "Los registros se guardan en el dispositivo. No introduzcas datos identificables de pacientes.",
            true,
        )
    }
    if (index >= 0)
        AppSheet(Patterns[index], { index = -1 }) {
            Notice("Usa únicamente información inventada.")
            Input(
                "Observación de ejemplo",
                observation,
                { observation = it.take(2000) },
                multi = true,
            )
            Primary(
                "Guardar observación ficticia",
                {
                    vm.note("gordon-$index", observation)
                    index = -1
                },
                observation.isNotBlank() && !vm.busy,
            )
        }
}

data class DemoPatient(
    val id: String,
    val name: String,
    val initials: String,
    val age: Int,
    val bed: String,
    val service: String,
)

val DemoPatients =
    listOf(
        DemoPatient("mario", "Mario Quispe", "MQ", 72, "104", "Medicina B"),
        DemoPatient("rosa", "Rosa Flores", "RF", 48, "108", "Medicina A"),
        DemoPatient("luis", "Luis Mendoza", "LM", 35, "112", "Observación"),
    )

private fun demo(id: String) = DemoPatients.find { it.id == id } ?: DemoPatients.first()

@Composable
fun PatientIdentity(p: DemoPatient) {
    AppCard(Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                Modifier.size(65.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Purple.copy(alpha = .15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(p.initials, fontFamily = HeadingFont, fontSize = 24.sp, color = Purple)
            }
            Column(Modifier.weight(1f)) {
                Text(p.name, style = MaterialTheme.typography.titleLarge)
                Text(
                    "${p.age} años · Cama ${p.bed}\n${p.service}",
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalPalette.current.muted,
                )
                Spacer(Modifier.height(7.dp))
                Badge("PERSONA FICTICIA", Teal)
            }
        }
    }
}

@Composable
fun PatientsScreen(nav: (String) -> Unit, back: () -> Unit) {
    var search by rememberSaveable { mutableStateOf("") }
    Page("Pacientes de ejemplo", "Fichas de demostración, no historias clínicas", back) {
        SearchBox(search, { search = it }, "Buscar nombre ficticio o cama…")
        DemoPatients.filter { normalized(it.name + it.bed).contains(normalized(search)) }
            .forEach { p ->
                Box(Modifier.clickable { nav("paciente/${p.id}") }) { PatientIdentity(p) }
            }
        Notice(
            "No se crean fichas de personas reales en esta edición. Primero deben definirse protección de datos, seguridad y requisitos asistenciales.",
            true,
        )
    }
}

@Composable
fun PatientScreen(id: String, vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    val p = demo(id)
    Page("Ficha de ejemplo", "Cada persona, un cuidado", back) {
        PatientIdentity(p)
        val cards =
            listOf(
                Module(
                    "Evolución",
                    "Notas ficticias",
                    "evolucion/${p.id}",
                    R.drawable.ic_edit,
                    Purple,
                ),
                Module(
                    "Signos vitales",
                    "Registros de ejemplo",
                    "signos/${p.id}",
                    R.drawable.ic_heart_pulse,
                    Pink,
                ),
                Module(
                    "Balance hídrico",
                    "Ejercicio aritmético",
                    "balance/${p.id}",
                    R.drawable.ic_droplet,
                    Blue,
                ),
                Module(
                    "Plan de cuidados",
                    "Documento PAE estructurado",
                    "plan/${p.id}",
                    R.drawable.ic_clipboard_check,
                    Gold,
                ),
            )
        cards.chunked(2).forEach { r ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                r.forEach { ModuleTile(it, nav, Modifier.weight(1f)) }
            }
        }
        ActionRow(
            "Notas guardadas",
            "${vm.data.notes.count{it.patientId==p.id}} notas de aprendizaje",
            R.drawable.ic_file,
            Purple,
        ) {
            nav("evolucion/${p.id}")
        }
        Notice(
            "Fichas separadas por ID y conservadas localmente. El diseño no es un sistema clínico listo para datos reales.",
            true,
        )
    }
}

@Composable
fun EvolutionScreen(id: String, vm: AppViewModel, back: () -> Unit) {
    val p = demo(id)
    var editing by remember { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf("") }
    Page("Evolución de ejemplo", p.name + " · persona ficticia", back) {
        Badge("SOLO INFORMACIÓN INVENTADA", Purple)
        val notes = vm.data.notes.filter { it.patientId == p.id }
        if (notes.isEmpty())
            EmptyState(
                "La historia de tu práctica",
                "Añade una nota inventada para comprobar que el registro se conserva al cerrar y abrir la app.",
            )
        notes.forEach { n ->
            AppCard(Modifier.fillMaxWidth()) {
                Text(
                    readableTime(n.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Purple,
                )
                Text(n.text, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Nota ficticia de aprendizaje",
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalPalette.current.muted,
                )
            }
        }
        Primary(
            "Añadir nota de ejemplo",
            {
                text = ""
                editing = true
            },
            !vm.busy,
            R.drawable.ic_plus,
        )
        Notice(
            "Las notas no se incluyen en el respaldo del catálogo. No registres información identificable ni clínica real.",
            true,
        )
    }
    if (editing)
        AppSheet("Nueva nota ficticia", { editing = false }) {
            Input("Nota de ejemplo", text, { text = it.take(2000) }, multi = true)
            Primary(
                "Guardar en este dispositivo",
                {
                    vm.note(p.id, text)
                    editing = false
                },
                text.isNotBlank() && !vm.busy,
            )
        }
}

@Composable
private fun ValueTile(
    title: String,
    value: String,
    unit: String,
    icon: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Symbol(icon, tint = color, modifier = Modifier.size(16.dp))
            Text(title, style = MaterialTheme.typography.labelSmall, color = color)
        }
        Text(value, style = MaterialTheme.typography.headlineMedium)
        Text(unit, style = MaterialTheme.typography.bodySmall, color = LocalPalette.current.muted)
    }
}

@Composable
fun VitalsScreen(id: String, vm: AppViewModel, back: () -> Unit) {
    val p = demo(id)
    val default =
        if (id == "rosa") listOf("122", "78", "78", "36.7", "98")
        else if (id == "luis") listOf("116", "74", "80", "36.5", "98")
        else listOf("118", "76", "74", "36.6", "97")
    val saved =
        vm.data.notes
            .firstOrNull { it.patientId == "vitals:${p.id}" }
            ?.text
            ?.split('|')
            ?.takeIf { it.size == 5 } ?: default
    var editing by remember { mutableStateOf(false) }
    var a by rememberSaveable { mutableStateOf(saved[0]) }
    var b by rememberSaveable { mutableStateOf(saved[1]) }
    var pulse by rememberSaveable { mutableStateOf(saved[2]) }
    var temp by rememberSaveable { mutableStateOf(saved[3]) }
    var o2 by rememberSaveable { mutableStateOf(saved[4]) }
    var error by remember { mutableStateOf<String?>(null) }
    Page("Signos vitales", p.name + " · valores ficticios", back) {
        Badge("REGISTRO SIMULADO", Pink)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ValueTile(
                "Presión arterial",
                "${saved[0]}/${saved[1]}",
                "mmHg",
                R.drawable.ic_heart_pulse,
                Pink,
                Modifier.weight(1f),
            )
            ValueTile(
                "Frecuencia cardíaca",
                saved[2],
                "lpm",
                R.drawable.ic_activity,
                Purple,
                Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ValueTile(
                "Temperatura",
                saved[3],
                "°C",
                R.drawable.ic_thermometer,
                Gold,
                Modifier.weight(1f),
            )
            ValueTile("Saturación O₂", saved[4], "%", R.drawable.ic_wind, Teal, Modifier.weight(1f))
        }
        val history = PracticeTrends.pulse(vm.data.notes, p.id)
        AppCard(Modifier.fillMaxWidth()) {
            Text("Tendencia de FC guardada", style = MaterialTheme.typography.titleMedium)
            if (history.isEmpty())
                Text(
                    "Guarda lecturas ficticias para construir el gráfico. No se dibujan valores inventados como si fueran mediciones guardadas.",
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalPalette.current.muted,
                )
            else {
                val low = (history.minOf { it.beats } - 5.0).coerceAtLeast(0.0)
                val high = history.maxOf { it.beats } + 5.0
                Text(
                    "Escala vertical: ${DoseMath.display(low)}–${DoseMath.display(high)} lpm",
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalPalette.current.muted,
                )
                Canvas(Modifier.fillMaxWidth().height(120.dp)) {
                    val inset = 8.dp.toPx()
                    for (i in 0..4) drawLine(
                        Purple.copy(alpha = .12f),
                        Offset(inset, inset + (size.height - 2 * inset) * i / 4),
                        Offset(size.width - inset, inset + (size.height - 2 * inset) * i / 4),
                    )
                    val points =
                        history.mapIndexed { i, record ->
                            Offset(
                                if (history.size == 1) size.width / 2
                                else inset + (size.width - 2 * inset) * i / (history.size - 1),
                                inset +
                                    (size.height - 2 * inset) *
                                        (1 - (record.beats - low) / (high - low)).toFloat(),
                            )
                        }
                    points.zipWithNext().forEach { (a, b) -> drawLine(Teal, a, b, 3.dp.toPx()) }
                    points.forEach { drawCircle(Purple, 4.dp.toPx(), it) }
                }
                Text(
                    "${history.size} registros ficticios · eje horizontal por orden de guardado, no por intervalos proporcionales de tiempo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalPalette.current.muted,
                )
                history.takeLast(5).reversed().forEach { record ->
                    Text(
                        "${readableTime(record.timestamp)} · ${DoseMath.display(record.beats)} lpm",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            Text(
                "Sin alertas, rangos de normalidad ni interpretación clínica.",
                style = MaterialTheme.typography.bodySmall,
                color = LocalPalette.current.muted,
            )
        }
        Primary(
            "Editar registro de ejemplo",
            {
                a = saved[0]
                b = saved[1]
                pulse = saved[2]
                temp = saved[3]
                o2 = saved[4]
                error = null
                editing = true
            },
            !vm.busy,
            R.drawable.ic_edit,
        )
    }
    if (editing)
        AppSheet("Registro ficticio", { editing = false }) {
            Notice(
                "Solo datos de prueba. Los límites del formulario no son rangos de normalidad.",
                true,
            )
            Input("Presión sistólica", a, { a = it }, true, "mmHg")
            Input("Presión diastólica", b, { b = it }, true, "mmHg")
            Input("Frecuencia cardíaca", pulse, { pulse = it }, true, "lpm")
            Input("Temperatura", temp, { temp = it }, true, "°C")
            Input("Saturación O₂", o2, { o2 = it }, true, "%")
            error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Primary(
                "Guardar ejemplo",
                {
                    try {
                        val values = listOf(a, b, pulse, temp, o2).map { number(it) }
                        require(
                            values.all { it.isFinite() && it > 0 } &&
                                values[0] <= 400 &&
                                values[1] <= 300 &&
                                values[2] <= 400 &&
                                values[3] <= 50 &&
                                values[4] <= 100
                        ) {
                            "Revisa los números del formulario."
                        }
                        vm.note("vitals:${p.id}", listOf(a, b, pulse, temp, o2).joinToString("|"))
                        editing = false
                    } catch (e: Exception) {
                        error = e.message
                    }
                },
                !vm.busy,
            )
        }
}

@Composable
fun BalanceScreen(id: String, vm: AppViewModel, back: () -> Unit) {
    val patient = demo(id)
    val stored = vm.data.notes.firstOrNull { it.patientId == "balance:${patient.id}" }?.text?.split('|')
    var ins by rememberSaveable(id) { mutableStateOf(stored?.getOrNull(0) ?: "1500") }
    var outs by rememberSaveable(id) { mutableStateOf(stored?.getOrNull(1) ?: "1200") }
    var period by rememberSaveable(id) { mutableStateOf(stored?.getOrNull(2).orEmpty()) }
    val result = runCatching {
        require(period.isNotBlank()) { "Indica el mismo intervalo para ingresos y egresos." }
        EducationMath.balance(ins, outs)
    }
    Page("Balance hídrico", patient.name + " · ejercicio ficticio", back) {
        Notice("Diferencia aritmética decimal. No interpreta el balance ni recomienda fluidos. Los registros anteriores se conservan, sin inventar su periodo.", true)
        Input("Ingresos de ejemplo", ins, { ins = calculationInput(it) }, true, "mL")
        Input("Egresos de ejemplo", outs, { outs = calculationInput(it) }, true, "mL")
        Input("Intervalo común de ingresos y egresos", period, { period = it.replace('|', ' ').take(120) })
        result.getOrNull()?.let { EducationResultCard(it.copy(explanation = "Intervalo ficticio: $period. ${it.explanation}")) }
            ?: Notice(result.exceptionOrNull()?.message ?: "Revisa los datos del ejercicio.", true)
        Primary("Guardar cantidades ficticias", { vm.note("balance:${patient.id}", "$ins|$outs|$period") }, result.isSuccess && !vm.busy)
    }
}

@Composable
fun ToolsScreen(nav: (String) -> Unit, back: () -> Unit) {
    val tools =
        listOf(
            Module(
                "Dosis",
                "Ejercicio con unidades",
                "calc/dosis",
                R.drawable.ic_calculator,
                Purple,
            ),
            Module("Goteo", "Ejemplo aritmético", "calc/goteo", R.drawable.ic_iv, Teal),
            Module("Cantidad por peso", "Por dosis o por día explícitos", "calc/kg", R.drawable.ic_calculator, Purple),
            Module("mL/h", "Volumen y tiempo", "calc/bomba", R.drawable.ic_iv, Teal),
            Module("Duración", "Volumen / velocidad", "calc/tiempo", R.drawable.ic_stopwatch, Purple),
            Module("Balance", "Mismo intervalo", "calc/balance", R.drawable.ic_clipboard, Teal),
            Module(
                "Dilución",
                "Ejercicio con unidades",
                "calc/dilucion",
                R.drawable.ic_flask,
                Purple,
            ),
            Module("Conversor", "Masa y volumen", "calc/conversor", R.drawable.ic_arrows, Teal),
            Module("IMC", "Peso y talla de ejemplo", "calc/imc", R.drawable.ic_stopwatch, Teal),
            Module(
                "Escalas",
                "Glasgow · EVA · Capurro",
                "escalas",
                R.drawable.ic_clipboard,
                Purple,
            ),
            Module(
                "Seguridad",
                "Listas configurables",
                "seguridad",
                R.drawable.ic_shield_check,
                Gold,
            ),
            Module(
                "ECG ilustrativo",
                "Animación, sin sensores",
                "ecg",
                R.drawable.ic_activity,
                Green,
            ),
        )
    Page("Herramientas 360°", "Pequeñas ayudas para tu aprendizaje", back) {
        tools.chunked(2).forEach { r ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                r.forEach { ModuleTile(it, nav, Modifier.weight(1f)) }
            }
        }
        Notice(
            "Las operaciones habilitadas son ejercicios matemáticos. No se usarán para indicar o administrar tratamientos.",
            true,
        )
    }
}

@Composable
fun CalculatorScreen(type: String, vm: AppViewModel, back: () -> Unit) {
    if (type in listOf("dosis", "dilucion")) DoseExerciseScreen(type, vm, back)
    else DecimalCalculatorScreen(type, vm, back)
}

@Composable
fun EcgScreen(back: () -> Unit) {
    var paused by rememberSaveable { mutableStateOf(false) }
    val motion = !LocalReduced.current && !paused
    val phase =
        if (motion)
            rememberInfiniteTransition(label = "ECG ficticio")
                .animateFloat(
                    0f,
                    1f,
                    infiniteRepeatable(tween(3500, easing = LinearEasing)),
                    label = "Trazo",
                )
                .value
        else 0f
    Page("ECG ilustrativo", "Animación de diseño · sin dispositivos", back) {
        Box(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF203D45))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "SIMULACIÓN VISUAL",
                    color = Color(0xFF8BCFC0),
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    "72 lpm · ejemplo",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF80DEC0),
                )
                Canvas(Modifier.fillMaxWidth().height(130.dp)) {
                    val c = Color(0xFF89E6C5)
                    for (i in 0..20) drawLine(
                        c.copy(alpha = .08f),
                        Offset(i * size.width / 20, 0f),
                        Offset(i * size.width / 20, size.height),
                    )
                    for (i in 0..8) drawLine(
                        c.copy(alpha = .08f),
                        Offset(0f, i * size.height / 8),
                        Offset(size.width, i * size.height / 8),
                    )
                    clipRect {
                        translate(left = -phase * size.width / 2) {
                            val path = Path()
                            val cycle = size.width / 2
                            for (i in 0..3) {
                                val x = i * cycle
                                if (i == 0) path.moveTo(x, size.height * .65f)
                                path.lineTo(x + cycle * .2f, size.height * .65f)
                                path.quadraticTo(
                                    x + cycle * .25f,
                                    size.height * .4f,
                                    x + cycle * .3f,
                                    size.height * .65f,
                                )
                                path.lineTo(x + cycle * .42f, size.height * .65f)
                                path.lineTo(x + cycle * .47f, size.height * .75f)
                                path.lineTo(x + cycle * .53f, size.height * .12f)
                                path.lineTo(x + cycle * .6f, size.height * .92f)
                                path.lineTo(x + cycle * .65f, size.height * .65f)
                                path.lineTo(x + cycle, size.height * .65f)
                            }
                            drawPath(path, c, style = Stroke(2.dp.toPx()))
                        }
                    }
                }
            }
        }
        Secondary(if (paused) "Reanudar animación" else "Pausar animación", { paused = !paused })
        Notice(
            "Trazo decorativo: no adquiere una señal real, no identifica ritmos y no permite monitorizar pacientes.",
            true,
        )
    }
}

val FlashQuestions =
    listOf(
        "¿Qué significa PAE?",
        "¿Cuál es la primera etapa del PAE?",
        "¿Qué sigue a la planificación?",
        "¿Para qué sirve la evaluación?",
        "¿Qué datos puedes usar aquí?",
    )
val FlashAnswers =
    listOf(
        "Proceso de Atención de Enfermería.",
        "La valoración: reunir y organizar la información.",
        "La ejecución de las acciones planificadas.",
        "Para revisar resultados y reconsiderar el plan.",
        "Solo datos ficticios, sin identificadores reales.",
    )

@Composable
fun LearningScreen(nav: (String) -> Unit, back: () -> Unit, info: (String, String) -> Unit) {
    Page("Centro de aprendizaje", "Un poco cada día, un gran cambio", back) {
        Box(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(Purple, Color(0xFFD49BD5))))
                .padding(22.dp)
        ) {
            Column(Modifier.fillMaxWidth(.78f), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                Text(
                    "CURIOSIDAD QUE TRANSFORMA",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                )
                Text(
                    "Aprender también\nes cuidar.",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                )
                Text(
                    "Una nueva idea, todos los días.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = .9f),
                )
            }
            Symbol(
                R.drawable.ic_book,
                tint = Color.White.copy(alpha = .8f),
                modifier = Modifier.size(60.dp).align(Alignment.BottomEnd),
            )
        }
        Section("Elige cómo aprender")
        ActionRow(
            "Apuntes interactivos",
            "Lecturas originales de demostración",
            R.drawable.ic_book,
            Purple,
        ) {
            nav("apuntes")
        }
        ActionRow("Flashcards", "5 tarjetas · fundamentos del PAE", R.drawable.ic_cards, Pink) {
            nav("tarjetas")
        }
        ActionRow(
            "Quiz de aprendizaje",
            "3 preguntas · sin certificación",
            R.drawable.ic_trophy,
            Gold,
        ) {
            nav("quiz")
        }
        ActionRow(
            "Casos de aprendizaje",
            "Casos ficticios y catálogo importable",
            R.drawable.ic_stethoscope,
            Teal,
        ) {
            nav("casos")
        }
        ActionRow(
            "Protocolos y guías",
            "Crear, editar e importar documentos",
            R.drawable.ic_clipboard,
            Purple,
        ) {
            nav("protocolos")
        }
        ActionRow(
            "Biblioteca multimedia",
            "Copias locales · PDF, imágenes, audio y video",
            R.drawable.ic_play,
            Blue,
        ) {
            nav("multimedia")
        }
    }
}

@Composable
fun FlashScreen(vm: AppViewModel, back: () -> Unit) {
    var flipped by rememberSaveable { mutableStateOf(false) }
    val angle by
        animateFloatAsState(
            if (flipped) 180f else 0f,
            tween(if (LocalReduced.current) 0 else 450),
            label = "Voltear tarjeta",
        )
    val index = vm.flashIndex
    Page("Flashcards", "Fundamentos del PAE · avanza a tu ritmo", back) {
        Badge("TARJETA ${index+1} DE 5", Purple)
        ProgressLine((index + 1) / 5f)
        Box(
            Modifier.fillMaxWidth()
                .heightIn(min = 300.dp)
                .graphicsLayer {
                    rotationY = angle
                    cameraDistance = 14 * density
                }
                .clip(RoundedCornerShape(25.dp))
                .background(if (angle > 90) Teal.copy(alpha = .13f) else Purple.copy(alpha = .13f))
                .border(1.dp, Purple.copy(alpha = .2f), RoundedCornerShape(25.dp))
                .clickable { flipped = !flipped }
                .padding(26.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier.graphicsLayer { rotationY = if (angle > 90) 180f else 0f },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(23.dp),
            ) {
                Symbol(
                    if (angle > 90) R.drawable.ic_check_circle else R.drawable.ic_cards,
                    tint = if (angle > 90) Teal else Purple,
                    modifier = Modifier.size(40.dp),
                )
                Text(
                    if (angle > 90) FlashAnswers[index] else FlashQuestions[index],
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )
                Text(
                    if (flipped) "Toca para volver a la pregunta"
                    else "Toca para descubrir la respuesta",
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalPalette.current.muted,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.weight(1f)) {
                Secondary(
                    "Anterior",
                    {
                        flipped = false
                        vm.setFlash(index - 1)
                    },
                    index > 0,
                )
            }
            Box(Modifier.weight(1f)) {
                Primary(
                    if (index == 4) "Volver al inicio" else "Siguiente",
                    {
                        flipped = false
                        vm.setFlash((index + 1) % 5)
                    },
                )
            }
        }
        Notice("Tu posición se conserva en este dispositivo.")
    }
}

private val QuizTitles =
    listOf(
        "¿Con qué etapa comienza el PAE?",
        "¿Qué etapa sigue a la planificación?",
        "¿Qué información puedes usar para probar esta app?",
    )
private val QuizOptions =
    listOf(
        listOf("Planificación", "Valoración", "Ejecución", "Evaluación"),
        listOf("Ejecución", "Valoración", "Diagnóstico", "Ninguna de las anteriores"),
        listOf(
            "Historias clínicas reales",
            "Fotos sin permiso",
            "Datos ficticios de ejemplo",
            "Credenciales del hospital",
        ),
    )
private val QuizCorrect = listOf(1, 0, 2)
private val QuizFeedback =
    listOf(
        "La valoración reúne y organiza la información que sirve de base a las etapas siguientes.",
        "Después de planificar se desarrollan las acciones previstas en la ejecución.",
        "Un entorno de demostración debe utilizar información ficticia para proteger la privacidad.",
    )

@Composable
fun QuizScreen(vm: AppViewModel, back: () -> Unit) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    var answers by rememberSaveable { mutableStateOf(IntArray(3) { -1 }) }
    var finished by rememberSaveable { mutableStateOf(false) }
    val score = answers.indices.count { answers[it] == QuizCorrect[it] }
    Page(
        if (finished) "¡Un paso más!" else "Pon a prueba tus ideas",
        "Fundamentos del PAE · práctica",
        back,
    ) {
        if (finished) {
            AppCard(Modifier.fillMaxWidth()) {
                ColorIcon(R.drawable.ic_trophy, Gold, 65)
                Text(
                    "Lo importante es seguir aprendiendo.",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text("$score / 3", fontFamily = HeadingFont, fontSize = 48.sp, color = Purple)
                Text(
                    "Respuestas correctas. Actividad educativa, no certificación.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalPalette.current.muted,
                )
            }
            Primary(
                "Volver a practicar",
                {
                    index = 0
                    answers = IntArray(3) { -1 }
                    finished = false
                },
            )
        } else {
            Badge("PREGUNTA ${index+1} DE 3", Gold)
            ProgressLine((index + 1) / 3f)
            Text(QuizTitles[index], style = MaterialTheme.typography.headlineMedium)
            QuizOptions[index].forEachIndexed { i, s ->
                val answered = answers[index] >= 0
                AppCard(
                    Modifier.fillMaxWidth(),
                    onClick =
                        if (!answered) ({ answers = answers.copyOf().apply { this[index] = i } })
                        else null,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Badge(
                            if (answered && i == QuizCorrect[index]) "✓" else ('A' + i).toString(),
                            if (answered && i == QuizCorrect[index]) Teal else Purple,
                        )
                        Text(
                            s,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            if (answers[index] >= 0) {
                Notice(
                    (if (answers[index] == QuizCorrect[index]) "¡Muy bien! "
                    else "Cada intento es aprendizaje. ") + QuizFeedback[index]
                )
            }
            Primary(
                if (index == 2) "Ver resultado" else "Siguiente pregunta",
                {
                    if (index == 2) {
                        finished = true
                        vm.practice(
                            "Quiz de fundamentos",
                            "$score de 3 respuestas correctas. Actividad de aprendizaje.",
                        )
                    } else index++
                },
                answers[index] >= 0,
            )
        }
    }
}

val NoteTitles =
    listOf("El PAE, en cinco pasos", "Un registro claro y ordenado", "Cuidar también es proteger")
val NoteTexts =
    listOf(
        "El Proceso de Atención de Enfermería organiza el cuidado en valoración, diagnóstico, planificación, ejecución y evaluación.\n\nEstas etapas ayudan a estructurar el razonamiento y el registro. No reemplazan la formación profesional, el juicio clínico ni los procedimientos del centro.\n\nEn esta edición puedes recorrerlas con una ficha ficticia.",
        "Un registro organizado facilita ubicar la información. Para practicar, distingue el momento del registro, su contenido y la persona que lo elabora.\n\nUtiliza lenguaje claro. No introduzcas información real en herramientas de demostración.\n\nLos requisitos asistenciales concretos dependen del establecimiento y de las normas aplicables.",
        "La información de salud necesita un cuidado especial. No compartas historias, imágenes ni identificadores reales en un prototipo.\n\nPara probar el diseño bastan nombres inventados y datos simulados.\n\nUna futura versión profesional requiere controles de acceso, protección del almacenamiento y evaluación normativa antes de tratar información real.",
    )

@Composable
fun CasesScreen(vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    Page("Casos de aprendizaje", "Catálogo local · ejemplos o contenido importado", back) {
        Section("Siete recorridos sintéticos progresivos")
        vm.data.master.catalog?.let { master ->
            master.cases.forEach { case ->
                val count = vm.data.learning["simulacion/${case.id}"]?.takeIf { it.version == master.version }?.answers?.size ?: 0
                ActionRow(case.title, "${case.area} · $count/${case.questions.size} etapas respondidas", R.drawable.ic_stethoscope, Teal) { nav("simulacion/${case.id}") }
            }
        }
        Section("Casos editables de tu biblioteca")
        Primary("Crear caso ficticio", { nav(newEntryRoute("case")) }, !vm.busy, R.drawable.ic_plus)
        vm.data.entries
            .filter { it.kind == "case" }
            .forEach { e ->
                ActionRow(e.name, e.group, R.drawable.ic_stethoscope, Teal) { nav(e.key) }
            }
        Secondary("Añadir casos mediante un paquete", { nav("contenido") })
        Notice(
            "Los recorridos nuevos son simulaciones sintéticas explícitas. No reconstruyen historias reales ni prescripciones de los archivos mencionados en la base. Los casos locales previos se conservan.",
            true,
        )
    }
}

@Composable
fun CaseScreen(id: String, vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    val e = vm.data.entries.find { it.kind == "case" && it.id == id } ?: return
    Page(e.name, e.group, back, { Favorite(e.key, e.name, vm) }) {
        Badge(
            if (e.revision == "borrador") "BORRADOR · APRENDIZAJE" else "REVISIÓN DECLARADA",
            Teal,
        )
        if (e.objective.isNotBlank())
            AppCard(Modifier.fillMaxWidth()) {
                Text("Objetivo", style = MaterialTheme.typography.titleMedium)
                Text(
                    e.objective,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalPalette.current.muted,
                )
            }
        AppCard(Modifier.fillMaxWidth()) {
            Text(
                e.body.ifBlank { "Contenido del caso pendiente de incorporar." },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Secondary("Editar caso local", { nav("editar/case/$id") }, !vm.busy)
        Provenance(e)
        if (id == "recorrido-mario")
            Primary("Explorar la ficha ficticia", { nav("paciente/mario") })
        if (e.references.isNotEmpty()) {
            Section("Fuentes del caso")
            e.references.forEach { SourceCard(it) }
        }
        Notice(
            "Caso de aprendizaje. No sustituye una valoración profesional ni autoriza decisiones clínicas.",
            true,
        )
    }
}

@Composable
fun HistoryScreen(vm: AppViewModel, back: () -> Unit) {
    var clear by remember { mutableStateOf(false) }
    Page("Historial de práctica", "Últimos 100 resultados educativos", back) {
        if (vm.data.practices.isEmpty())
            EmptyState(
                "Tu aprendizaje, paso a paso",
                "Guarda resultados de los ejercicios o completa el quiz para verlos aquí.",
            )
        vm.data.practices.forEach { r ->
            AppCard(Modifier.fillMaxWidth()) {
                Badge(r.type, Teal)
                Text(r.summary, style = MaterialTheme.typography.bodyMedium)
                Text(
                    readableTime(r.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalPalette.current.muted,
                )
            }
        }
        if (vm.data.practices.isNotEmpty())
            Secondary("Borrar historial de práctica", { clear = true }, !vm.busy)
    }
    if (clear)
        AlertDialog(
            onDismissRequest = { clear = false },
            title = { Text("¿Borrar las prácticas?") },
            text = {
                Text(
                    "Se eliminará el historial educativo de este dispositivo. El catálogo no cambiará."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        clear = false
                        vm.clearHistory()
                    }
                ) {
                    Text("Borrar")
                }
            },
            dismissButton = { TextButton(onClick = { clear = false }) { Text("Cancelar") } },
        )
}

@Composable
fun GlobalSearchScreen(vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var resultLimit by rememberSaveable { mutableIntStateOf(40) }
    val fixed =
        (Modules + ExtraModules) +
            listOf(
                Module(
                    "Glasgow",
                    "Escala estándar de práctica",
                    "glasgow",
                    R.drawable.ic_activity,
                    Blue,
                ),
                Module(
                    "EVA",
                    "Simulación digital no calibrada",
                    "eva",
                    R.drawable.ic_heart_pulse,
                    Pink,
                ),
                Module(
                    "Capurro",
                    "Estimación A/B de práctica",
                    "capurro",
                    R.drawable.ic_stethoscope,
                    Teal,
                ),
                Module(
                    "Apgar",
                    "Cinco componentes neonatales",
                    "escala/apgar",
                    R.drawable.ic_heart,
                    Pink,
                ),
                Module(
                    "Silverman-Andersen",
                    "Valoración neonatal de práctica",
                    "escala/silverman",
                    R.drawable.ic_wind,
                    Teal,
                ),
                Module(
                    "Glasgow pediátrica",
                    "Registro externo de componentes",
                    "glasgow-pediatrico",
                    R.drawable.ic_clipboard,
                    Blue,
                ),
                Module(
                    "Braden",
                    "Registrar puntuación externa",
                    "registro-escala/braden",
                    R.drawable.ic_bed,
                    Purple,
                ),
                Module(
                    "Morse",
                    "Registrar puntuación externa",
                    "registro-escala/morse",
                    R.drawable.ic_shield_check,
                    Gold,
                ),
                Module(
                    "Dosis a volumen",
                    "Aritmética con unidades",
                    "calc/dosis",
                    R.drawable.ic_calculator,
                    Gold,
                ),
                Module(
                    "Dilución",
                    "Ejercicio C1V1 = C2V2",
                    "calc/dilucion",
                    R.drawable.ic_flask,
                    Teal,
                ),
                Module("Apuntes", "Cuaderno editable", "apuntes", R.drawable.ic_edit, Purple),
                Module("IMC", "Índice de masa corporal", "calc/imc", R.drawable.ic_stopwatch, Teal),
                Module("Goteo", "Factor de equipo obligatorio", "calc/goteo", R.drawable.ic_iv, Teal),
                Module("Cantidad por peso", "Dosis/kg o cantidad por día del enunciado", "calc/kg", R.drawable.ic_calculator, Purple),
                Module("Velocidad mL/h", "Volumen y tiempo de ejemplo", "calc/bomba", R.drawable.ic_iv, Teal),
                Module("Duración de infusión", "Volumen / velocidad", "calc/tiempo", R.drawable.ic_stopwatch, Purple),
                Module("Conversor dimensional", "Masa, volumen y tiempo", "calc/conversor", R.drawable.ic_arrows, Teal),
                Module("Balance aritmético", "Ingresos y egresos del mismo intervalo", "calc/balance", R.drawable.ic_clipboard, Teal),
                Module("Norton · registro externo", "Adulto · no administra el instrumento", "registro-escala/norton", R.drawable.ic_bed, Purple),
                Module("RASS · registro externo", "Adulto · no administra el instrumento", "registro-escala/rass", R.drawable.ic_clipboard, Teal),
                Module("Flashcards", "Cinco tarjetas PAE", "tarjetas", R.drawable.ic_cards, Pink),
                Module("Biblioteca de tests clínicos", "Fichas por población y área", "biblioteca-escalas", R.drawable.ic_book, Teal),
                Module("Escala numérica del dolor", "Autoinforme de práctica 0–10", "dolor-numerico", R.drawable.ic_heart_pulse, Pink),
                Module("4AT", "Cribado adulto · adaptación de práctica", "test-4at", R.drawable.ic_clipboard_check, Purple),
            )
    val clinicalLoad = rememberClinicalCatalog()
    val clinicalMatches = if (query.isBlank()) emptyList() else clinicalLoad.catalog?.filter(query).orEmpty()
    val masterMatches = if (query.isBlank()) emptyList() else vm.data.master.catalog?.search(query).orEmpty()
    val noteMatches = if (query.isBlank()) emptyList() else vm.data.notebooks.filter {
        normalized(it.title + " " + it.fields.toString()).contains(normalized(query))
    }
    val matching = fixed.filter { normalized(it.title + it.sub).contains(normalized(query)) }
    val entries =
        if (query.isBlank()) emptyList()
        else vm.data.entries.filter { normalized(it.name + " " + it.aliases + " " + it.group + " " + it.body + " " + it.sections.values.joinToString(" ")).contains(normalized(query)) }
    Page("¿Qué necesitas hoy?", "Busca en tu universo 360°", back) {
        SearchBox(query, { query = it.take(160); resultLimit = 40 }, "Tema, etiqueta, especialidad, escala o dispositivo…")
        Text(
            if (query.isBlank()) "Empieza por aquí" else "${matching.size+entries.size+clinicalMatches.size+masterMatches.size+noteMatches.size} resultados",
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
        matching.forEach { m -> ActionRow(m.title, m.sub, m.icon, m.color) { nav(m.route) } }
        entries.forEach { e ->
            ActionRow(
                e.name,
                e.group,
                if (e.kind == "med") R.drawable.ic_pill else R.drawable.ic_book,
                Purple,
            ) {
                nav(e.key)
            }
        }
        if (clinicalMatches.isNotEmpty()) Section("Tests y escalas · fichas documentales")
        clinicalMatches.forEach { instrument ->
            ActionRow(instrument.name, instrument.availability.label, R.drawable.ic_book, clinicalColor(instrument.area)) {
                nav("instrumento/${instrument.id}")
            }
        }
        if (masterMatches.isNotEmpty()) Section("Base 2.1 · búsqueda en texto y metadatos")
        masterMatches.take(resultLimit).forEach { item ->
            ActionRow(if (item.kind == "Inventario") "Inventario · ${item.title}" else item.title,
                "${item.kind} · ${item.subtitle}", R.drawable.ic_book, Purple) { nav(item.route) }
        }
        if (masterMatches.size > resultLimit) Secondary("Mostrar 40 resultados más", { resultLimit += 40 })
        if (noteMatches.isNotEmpty()) Section("Cuaderno personal · solo local")
        noteMatches.forEach { note ->
            ActionRow(note.title, if (note.kind == "plan") "Plan PAE editable" else "Apunte personal", R.drawable.ic_edit, Teal) {
                nav(if (note.kind == "plan") "editar-plan/${note.owner}/${note.id}" else "editar-apunte/${note.id}")
            }
        }
        vm.data.master.error?.let { Notice(it, true) }
        if (clinicalLoad.failed) Notice("La búsqueda de fichas clínicas no se pudo cargar. Los demás resultados siguen disponibles.")
        if (matching.isEmpty() && entries.isEmpty() && clinicalMatches.isEmpty() && masterMatches.isEmpty() && noteMatches.isEmpty())
            EmptyState(
                "No hay coincidencias",
                "Prueba con Glasgow, EVA, Capurro, paracetamol o PAE.",
            )
    }
}
