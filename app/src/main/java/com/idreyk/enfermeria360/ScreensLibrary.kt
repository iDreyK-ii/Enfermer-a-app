package com.idreyk.enfermeria360

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal data class Module(
    val title: String,
    val sub: String,
    val route: String,
    val icon: Int,
    val color: Color,
)

internal val Modules =
    listOf(
        Module("PAE · ADPIE", "Proceso enfermero", "pae", R.drawable.ic_clipboard_check, Teal),
        Module("Medicamentos", "Farmacología", "farmacologia", R.drawable.ic_pill, Pink),
        Module(
            "Herramientas",
            "Cálculos de práctica",
            "herramientas",
            R.drawable.ic_calculator,
            Gold,
        ),
        Module("Pacientes", "Fichas ficticias", "pacientes", R.drawable.ic_users, Purple),
        Module("Escalas", "Valoración educativa", "escalas", R.drawable.ic_clipboard, Blue),
        Module("Aprendizaje", "Crece a tu ritmo", "aprendizaje", R.drawable.ic_book, Pink),
    )
internal val ExtraModules =
    listOf(
        Module(
            "Medicación segura",
            "Listas con fuentes",
            "seguridad",
            R.drawable.ic_shield_check,
            Teal,
        ),
        Module(
            "Biblioteca local",
            "Importar y respaldar",
            "contenido",
            R.drawable.ic_folder,
            Purple,
        ),
        Module("Cuaderno PAE", "Planes y apuntes", "cuaderno", R.drawable.ic_edit, Teal),
        Module("Protocolos", "Documentos locales", "protocolos", R.drawable.ic_file, Purple),
        Module("Multimedia", "Archivos del dispositivo", "multimedia", R.drawable.ic_play, Blue),
        Module("Biblioteca temática", "Markdown y fuentes", "biblioteca", R.drawable.ic_book, Purple),
        Module("Atlas interactivo", "24 dispositivos", "atlas", R.drawable.ic_stethoscope, Teal),
        Module("Bioseguridad", "Higiene y protección", "bioseguridad", R.drawable.ic_shield_check, Teal),
        Module("Casos progresivos", "Siete escenarios sintéticos", "casos", R.drawable.ic_clipboard, Pink),
        Module("Tu avance real", "Progreso local", "avance", R.drawable.ic_activity, Purple),
        Module("Estado de módulos", "Funciones y pendientes", "estado-modulos", R.drawable.ic_grid, Teal),
    )

@Composable
internal fun ModuleTile(module: Module, nav: (String) -> Unit, modifier: Modifier = Modifier) {
    AppCard(modifier.heightIn(min = 116.dp), onClick = { nav(module.route) }) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            ColorIcon(module.icon, module.color, 40)
            Text(
                module.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )
            Text(
                module.sub,
                style = MaterialTheme.typography.labelSmall,
                color = LocalPalette.current.muted,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun HomeScreen(vm: AppViewModel, nav: (String) -> Unit, info: (String, String) -> Unit) {
    val p = LocalPalette.current
    Column(
        Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 9.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    "ENFERMERÍA",
                    style = MaterialTheme.typography.labelSmall,
                    color = Purple,
                    letterSpacing = 1.5.sp,
                )
                Text(
                    androidx.compose.ui.text.buildAnnotatedString {
                        append("iDreyK ")
                        pushStyle(androidx.compose.ui.text.SpanStyle(color = Pink))
                        append("360")
                        pop()
                    },
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            IconButton(onClick = { vm.updateDark(!vm.dark) }) {
                Symbol(if (vm.dark) R.drawable.ic_sun else R.drawable.ic_moon, "Cambiar tema")
            }
            IconButton(
                onClick = {
                    info(
                        "Tu espacio está listo",
                        "La biblioteca descargada funciona sin conexión. Las cuentas y novedades requieren un proyecto Supabase configurado; no hay notificaciones push. Usa solo información ficticia.",
                    )
                }
            ) {
                Symbol(R.drawable.ic_bell, "Mensajes de la aplicación")
            }
        }
        WelcomeHero(vm.name, vm.dark)
        Row(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(p.surface)
                .border(1.dp, p.border, RoundedCornerShape(15.dp))
                .clickable { nav("buscar") }
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            Symbol(R.drawable.ic_search, tint = Purple, modifier = Modifier.size(18.dp))
            Text(
                "Busca un módulo, fármaco o recurso…",
                style = MaterialTheme.typography.bodySmall,
                color = p.muted,
            )
        }
        AppCard(Modifier.fillMaxWidth(), onClick = { nav("farmacologia") }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ColorIcon(R.drawable.ic_pill, Pink, 46)
                Column(Modifier.weight(1f)) {
                    Text("Farmacología", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Fichas, preparación y cuidados",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Symbol(R.drawable.ic_chevron_right, "Explorar farmacología", Purple)
            }
        }
        Section("Tu universo 360°", "Ver todo") { nav("modulos") }
        val shortcuts =
            listOf(
                Module("PAE", "Proceso enfermero", "pae", R.drawable.ic_clipboard_check, Teal),
                Module("Escalas", "Práctica educativa", "escalas", R.drawable.ic_clipboard, Purple),
                Module("Cálculos", "Herramientas", "herramientas", R.drawable.ic_calculator, Pink),
                Module("Apuntes", "Tu cuaderno", "apuntes", R.drawable.ic_book, Teal),
                Module("Protocolos", "Biblioteca", "protocolos", R.drawable.ic_folder, Purple),
                Module("Multimedia", "Archivos locales", "multimedia", R.drawable.ic_play, Pink),
            )
        ModuleGrid(shortcuts, nav, "home-module-grid")
        ActionRow("Biblioteca temática", "Lecturas, filtros y fuentes · sin conexión", R.drawable.ic_book, Purple) { nav("biblioteca") }
        val masterVersion = vm.data.master.catalog?.version
        val completed = vm.data.learning.values.count { it.version == masterVersion && it.completed }
        ActionRow("Tu avance real", "$completed lecturas o recorridos marcados · ${vm.data.favorites.size} favoritos", R.drawable.ic_activity, Teal) { nav("avance") }
        ActionRow(
            "Actualizar biblioteca",
            "Consulta local · novedades opcionales",
            R.drawable.ic_download,
            Purple,
        ) {
            nav("nube")
        }
        Section("Tus aliados de cada día")
        ActionRow(
            "Medicación segura",
            "Listas configurables · fuentes visibles",
            R.drawable.ic_shield_check,
            Teal,
        ) {
            nav("seguridad")
        }
        ActionRow(
            "Fundamentos del PAE",
            "5 flashcards · sigue por la ${vm.flashIndex+1}",
            R.drawable.ic_cards,
            Purple,
        ) {
            nav("tarjetas")
        }
        Text(
            "EDICIÓN DE DESARROLLO · LECTURA LOCAL",
            Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelSmall,
            color = p.muted,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ModulesScreen(nav: (String) -> Unit, back: () -> Unit) {
    Page("Tu universo 360°", "Un espacio para cada parte de tu vocación", back) {
        ModuleGrid(Modules + ExtraModules, nav, "all-module-grid", preferred = 2)
        Notice(
            "Esta edición permite validar funciones y contenido de ejemplo. No está habilitada para decisiones clínicas."
        )
    }
}

@Composable
fun PharmacyScreen(vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val drugs =
        vm.data.entries
            .filter {
                it.kind == "med" &&
                    normalized(it.name + it.aliases + it.group).contains(normalized(query)) &&
                    (tab != 1 || it.key in vm.data.favorites) &&
                    (tab != 2 || it.sections.values.any { v -> v.isNotBlank() })
            }
            .sortedBy { normalized(it.name) }
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        PageHeader("Farmacología", "Tu biblioteca local de consulta", back) {
            IconButton(onClick = { nav(newEntryRoute("med")) }) {
                Symbol(R.drawable.ic_plus, "Crear medicamento")
            }
            IconButton(onClick = { nav("contenido") }) {
                Symbol(R.drawable.ic_folder, "Gestionar biblioteca")
            }
        }
        SearchBox(query, { query = it }, "Buscar medicamento o sinónimo…")
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("Todos", "Favoritos", "Con contenido").forEachIndexed { i, t ->
                FilterChip(
                    tab == i,
                    { tab = i },
                    label = { Text(t, style = MaterialTheme.typography.labelMedium) },
                )
            }
        }
        Text(
            "${drugs.size} fichas · estado de revisión visible",
            style = MaterialTheme.typography.labelSmall,
            color = LocalPalette.current.muted,
            modifier = Modifier.padding(bottom = 11.dp),
        )
        LazyColumn(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(11.dp),
            contentPadding = PaddingValues(bottom = 25.dp),
        ) {
            item { ActionRow("Inventario nominal · Base 2.1", "Nombres identificados, no monografías validadas", R.drawable.ic_book, Purple) { nav("inventario") } }
            if (drugs.isEmpty())
                item {
                    EmptyState(
                        "Tu biblioteca puede crecer",
                        "Prueba otra búsqueda o importa un paquete de contenido autorizado desde Biblioteca local.",
                    )
                }
            items(drugs, key = { it.key }) { m ->
                AppCard(Modifier.fillMaxWidth(), onClick = { nav(m.key) }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        ColorIcon(
                            R.drawable.ic_pill,
                            if (m.id.hashCode() % 2 == 0) Pink else Teal,
                            40,
                        )
                        Column(Modifier.weight(1f)) {
                            Text(m.name, style = MaterialTheme.typography.titleSmall)
                            Text(
                                m.group,
                                style = MaterialTheme.typography.bodySmall,
                                color = LocalPalette.current.muted,
                            )
                            Text(
                                if (m.revision == "borrador") "BORRADOR" else "REVISIÓN DECLARADA",
                                style = MaterialTheme.typography.labelSmall,
                                color = Purple,
                                modifier = Modifier.padding(top = 5.dp),
                            )
                        }
                        Favorite(m.key, m.name, vm)
                    }
                }
            }
            item {
                Notice(
                    "Las fichas iniciales contienen la estructura, no pautas farmacológicas. Los datos importados no se validan clínicamente de forma automática.",
                    true,
                )
            }
        }
    }
}

@Composable
fun DrugScreen(id: String, vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    val e = vm.data.entries.find { it.kind == "med" && it.id == id }
    if (e == null) {
        Page("Ficha no disponible", back = back) {
            EmptyState("La ficha cambió", "Vuelve a la biblioteca y selecciona un medicamento.")
        }
        return
    }
    var tab by rememberSaveable(id) { mutableIntStateOf(0) }
    Page(
        "Ficha farmacológica",
        "Contenido local · estado de revisión visible",
        back,
        { Favorite(e.key, e.name, vm) },
    ) {
        AppCard(Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                ColorIcon(R.drawable.ic_pill, Pink, 54)
                Column(Modifier.weight(1f)) {
                    Text(e.name, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        e.group,
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalPalette.current.muted,
                    )
                }
            }
            Badge(
                if (e.revision == "borrador") "BORRADOR · NO USO CLÍNICO"
                else "REVISIÓN DECLARADA POR EL AUTOR",
                if (e.revision == "borrador") Gold else Teal,
            )
        }
        Notice(
            if (e.revision == "borrador")
                "La estructura está lista; falta completar y revisar el contenido. No utilizar esta ficha para indicar o administrar medicamentos."
            else
                "La revisión es una declaración del autor del paquete. La app no certifica el contenido ni sustituye las fuentes y protocolos aplicables.",
            true,
        )
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            listOf("Lo esencial", "Ficha técnica", "Otros", "Fuentes").forEachIndexed { i, t ->
                FilterChip(
                    tab == i,
                    { tab = i },
                    label = { Text(t, style = MaterialTheme.typography.labelMedium) },
                )
            }
        }
        Secondary("Editar ficha local", { nav("editar/med/$id") }, !vm.busy)
        if (tab == 0) {
            Section("Lo esencial para enfermería")
            PharmacologyLayout.essentials.entries.forEachIndexed { index, block ->
                Expandable(
                    block.key,
                    PharmacologyLayout.summary(e, block.value),
                    initial = index == 0,
                )
            }
            Secondary("Ver ficha técnica · 17 apartados", { tab = 1 })
        } else if (tab == 1) {
            PharmacologyLayout.technical.entries.forEachIndexed { index, field ->
                Expandable("${index + 1}. ${field.value}", PharmacologyLayout.text(e, field.key))
            }
        } else if (tab == 2) {
            Section("Información complementaria conservada")
            DRUG_FIELDS.keys
                .filter { it !in PharmacologyLayout.technical.keys }
                .forEach { key -> Expandable(DRUG_FIELDS.getValue(key), e.sections[key].orEmpty()) }
        } else {
            AppCard(Modifier.fillMaxWidth()) {
                Text("Trazabilidad de la ficha", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Autor del paquete: ${e.json.optString("_autor","No indicado")}\nActualización declarada: ${e.json.optString("_actualizacion","Pendiente")}\nRevisor declarado: ${e.json.optString("revisor").ifBlank{"Pendiente"}}\nFecha de revisión: ${e.json.optString("fecha_revision").ifBlank{"Pendiente"}}",
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalPalette.current.muted,
                )
                Text(
                    e.json.optString("_licencia").ifBlank { "Permisos pendientes de documentar." },
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalPalette.current.muted,
                )
            }
            if (e.references.isEmpty())
                EmptyState(
                    "Fuentes por incorporar",
                    "Añade las referencias, edición y permisos junto al contenido. El nombre del archivo no basta para validar una ficha.",
                )
            else e.references.forEach { SourceCard(it) }
            Provenance(e)
            Secondary("Gestionar contenido local", { nav("contenido") })
        }
    }
}

val GlasgowReference =
    Reference(
        "GCS Assessment Aid · versión en español",
        "https://www.glasgowcomascale.org/downloads/GCS-Assessment-Aid-Spanish.pdf",
        "Glasgow University / Sir Graham Teasdale",
        "2026-11-01",
    )
val GlasgowPermissions =
    Reference(
        "Glasgow Coma Scale · condiciones publicadas de uso",
        "https://www.glasgowcomascale.org/permissions/",
        "Glasgow Coma Scale",
        "2026-11-01",
    )
val NeonatalReference =
    Reference(
        "NTS 214-MINSA/DGIESP-2024 · RM 545-2024/MINSA · Atención Integral de Salud Neonatal",
        "https://www.gob.pe/institucion/minsa/normas-legales/5874349-545-2024-minsa",
        "Ministerio de Salud del Perú",
        "2026-11-01",
    )

@Composable
fun ChoiceField(
    title: String,
    options: List<Pair<String, Int>>,
    value: Int,
    showPoints: Boolean = true,
    onChange: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    AppCard(Modifier.fillMaxWidth(), onClick = { expanded = true }) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Box {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    options.find { it.second == value }?.first ?: "Selecciona una respuesta",
                    Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalPalette.current.muted,
                )
                Symbol(R.drawable.ic_chevron_down, tint = Purple)
            }
            DropdownMenu(expanded, { expanded = false }) {
                options.forEach { (label, n) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (n < 0 || !showPoints) label else "$label · $n",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        },
                        onClick = {
                            onChange(n)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun GlasgowScreen(vm: AppViewModel, back: () -> Unit) {
    var eye by rememberSaveable { mutableIntStateOf(0) }
    var verbal by rememberSaveable { mutableIntStateOf(0) }
    var motor by rememberSaveable { mutableIntStateOf(0) }
    val total = PracticeMath.glasgow(eye, verbal, motor)
    val anyNv = eye < 0 || verbal < 0 || motor < 0
    fun component(n: Int) = if (n < 0) "NV" else if (n == 0) "—" else n.toString()
    Page("Escala de Glasgow", "Versión estándar · ejercicio educativo", back) {
        Notice(
            "No es la versión pediátrica. La valoración requiere formación y considerar factores que interfieren. No infiere diagnósticos ni decisiones de tratamiento.",
            true,
        )
        ChoiceField(
            "Apertura ocular",
            listOf(
                "Espontánea" to 4,
                "Al sonido" to 3,
                "A la presión" to 2,
                "Ninguna" to 1,
                "No valorable (NV)" to -1,
            ),
            eye,
            onChange = { eye = it },
        )
        ChoiceField(
            "Respuesta verbal",
            listOf(
                "Orientada" to 5,
                "Confusa" to 4,
                "Palabras" to 3,
                "Sonidos" to 2,
                "Ninguna" to 1,
                "No valorable (NV)" to -1,
            ),
            verbal,
            onChange = { verbal = it },
        )
        ChoiceField(
            "Mejor respuesta motora",
            listOf(
                "Obedece comandos" to 6,
                "Localiza" to 5,
                "Flexión normal" to 4,
                "Flexión anormal" to 3,
                "Extensión" to 2,
                "Ninguna" to 1,
                "No valorable (NV)" to -1,
            ),
            motor,
            onChange = { motor = it },
        )
        AppCard(Modifier.fillMaxWidth()) {
            Badge("RESULTADO DE PRÁCTICA", Teal)
            Text(
                "O${component(eye)} · V${component(verbal)} · M${component(motor)}",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                total?.let { "$it / 15" }
                    ?: if (anyNv) "Sin total: componente NV" else "Completa las tres respuestas",
                style = MaterialTheme.typography.headlineMedium,
                color = Teal,
            )
            Text(
                if (anyNv)
                    "No se sustituye un componente no valorable por un punto. Se conservan los componentes sin sumar."
                else "Suma descriptiva, sin clasificación de gravedad ni recomendaciones.",
                style = MaterialTheme.typography.bodySmall,
                color = LocalPalette.current.muted,
            )
        }
        Primary(
            "Guardar práctica",
            {
                vm.practice(
                    "Glasgow",
                    "O${component(eye)} · V${component(verbal)} · M${component(motor)}" +
                        (total?.let { " = $it/15" } ?: " · Sin total (NV)"),
                )
            },
            enabled = eye != 0 && verbal != 0 && motor != 0 && !vm.busy,
            icon = R.drawable.ic_check,
        )
        Secondary(
            "Reiniciar respuestas",
            {
                eye = 0
                verbal = 0
                motor = 0
            },
        )
        Expandable("Factores que pueden interferir", "Considerar intubación, sedación, lesiones, déficits previos, audición e idioma. No confundir un componente no valorable con ausencia de respuesta ni asignarle un punto. Esta pantalla no enseña maniobras ni autoriza decisiones de tratamiento.")
        SourceCard(Reference("Glasgow: interferencias, NV y variantes por edad", "https://www.glasgowcomascale.org/faq/", "Glasgow Coma Scale · secciones 1–3 consultadas", "2026-11-02"))
        SourceCard(GlasgowReference)
        SourceCard(GlasgowPermissions)
        Text(
            "Se reconoce la autoría de Glasgow University (GCS) y Sir Graham Teasdale (GCS Aid). No se redistribuye la lámina original.",
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
    }
}

@Composable
fun EvaScreen(vm: AppViewModel, back: () -> Unit) {
    var position by rememberSaveable { mutableFloatStateOf(.5f) }
    var touched by rememberSaveable { mutableStateOf(false) }
    var reveal by rememberSaveable { mutableStateOf(false) }
    Page("EVA · línea digital", "Demostración de interfaz, no instrumento validado", back) {
        Notice(
            "Esta línea no está calibrada a 100 mm ni se ha validado como instrumento digital. No equivale a una EVA física validada ni se presenta como una escala numérica clínica.",
            true,
        )
        AppCard(Modifier.fillMaxWidth()) {
            Badge("SIMULACIÓN DE APRENDIZAJE", Pink)
            Text("Explora una posición en la línea", style = MaterialTheme.typography.titleMedium)
            Text(
                "Mueve el indicador entre ambos extremos. No introduzcas un registro de una persona real.",
                style = MaterialTheme.typography.bodyMedium,
                color = LocalPalette.current.muted,
            )
            Spacer(Modifier.height(12.dp))
            Slider(
                value = position,
                onValueChange = {
                    position = it
                    touched = true
                    reveal = false
                },
                valueRange = 0f..1f,
                steps = 0,
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Sin dolor", style = MaterialTheme.typography.bodySmall)
                Text(
                    "Máximo dolor\nimaginable",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.End,
                )
            }
        }
        Primary("Ver posición de ejemplo", { reveal = true }, enabled = touched)
        if (reveal) {
            AppCard(Modifier.fillMaxWidth()) {
                Text(
                    "${PracticeMath.evaPosition(position)} / 100",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Pink,
                )
                Text(
                    "Posición normalizada de la interfaz. No son milímetros ni una valoración clínica validada.",
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalPalette.current.muted,
                )
            }
            Secondary(
                "Guardar práctica de interfaz",
                {
                    vm.practice(
                        "EVA · interfaz",
                        "Posición normalizada ${PracticeMath.evaPosition(position)}/100. Simulación no calibrada.",
                    )
                },
                !vm.busy,
            )
        }
        Secondary("Reiniciar línea", { position = .5f; touched = false; reveal = false })
        Expandable("EVA no es escala numérica", "La EVA física usa una línea continua; la escala numérica pide escoger un número. No añadir caras, divisiones de gravedad o números visibles a la línea para convertirla en otro instrumento. Esta pantalla conserva solo una simulación digital, sin calibración clínica.")
        SourceCard(PainMeasurementReference)
        Expandable(
            "Antes de habilitar uso clínico",
            "Seleccionar y documentar la versión del instrumento, condiciones de uso, calibración y validación digital con profesionales. La app no aplica umbrales ni interpreta la intensidad del dolor.",
        )
    }
}

@Composable
fun SafetyScreen(vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    Page("Medicación segura", "Verificaciones con fuente y versión", back) {
        ActionRow("Bioseguridad y listas cotejadas", "Cinco momentos · INSN-SB 10 · controles propios 20", R.drawable.ic_shield_check, Teal) { nav("bioseguridad") }
        AppCard(Modifier.fillMaxWidth()) {
            ColorIcon(R.drawable.ic_shield_check, Teal, 48)
            Text("La seguridad no es solo un número.", style = MaterialTheme.typography.titleLarge)
            Text(
                "El catálogo admite listas institucionales de distinta longitud. No fijamos veinte como un estándar universal. Cada lista conserva su fuente y estado de revisión.",
                style = MaterialTheme.typography.bodyMedium,
                color = LocalPalette.current.muted,
            )
        }
        Primary("Crear lista local", { nav(newEntryRoute("safety")) }, !vm.busy, R.drawable.ic_plus)
        vm.data.entries
            .filter { it.kind == "safety" }
            .forEach { e ->
                ActionRow(
                    e.name,
                    "${e.checklist.size} verificaciones · ${if(e.revision=="borrador")"borrador"else"revisión declarada"}",
                    R.drawable.ic_clipboard_check,
                    Teal,
                ) {
                    nav(e.key)
                }
            }
        Secondary("Importar una lista autorizada", { nav("contenido") })
        Notice(
            "La lista inicial es una actividad de aprendizaje. Completarla no autoriza una administración ni reemplaza los procedimientos institucionales.",
            true,
        )
    }
}

@Composable
fun SafetyDetail(id: String, vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    val e = vm.data.entries.find { it.kind == "safety" && it.id == id } ?: return
    var checks by
        rememberSaveable(id, e.json.toString()) { mutableStateOf(BooleanArray(e.checklist.size)) }
    if (checks.size != e.checklist.size) checks = BooleanArray(e.checklist.size)
    Page("Lista de práctica", e.name, back) {
        Text(
            e.json.optString("institucion", "Lista importada"),
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
        Badge("${checks.count{it}} / ${checks.size} · PRÁCTICA", Teal)
        ProgressLine(checks.count { it }.toFloat() / checks.size)
        e.checklist.forEachIndexed { i, (title, detail) ->
            AppCard(
                Modifier.fillMaxWidth(),
                onClick = { checks = checks.copyOf().apply { this[i] = !this[i] } },
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Checkbox(
                        checks[i],
                        onCheckedChange = { v -> checks = checks.copyOf().apply { this[i] = v } },
                    )
                    Column(Modifier.weight(1f).padding(top = 10.dp)) {
                        Text(title, style = MaterialTheme.typography.titleSmall)
                        if (detail.isNotBlank())
                            Text(
                                detail,
                                style = MaterialTheme.typography.bodySmall,
                                color = LocalPalette.current.muted,
                            )
                    }
                }
            }
        }
        if (checks.all { it })
            Notice(
                "Recorrido de práctica completado. Esto no constituye autorización para administrar un medicamento.",
                true,
            )
        Secondary("Limpiar verificaciones", { checks = BooleanArray(e.checklist.size) })
        Primary(
            "Guardar recorrido de la lista",
            {
                vm.practice(
                    "Lista de seguridad",
                    "${e.name}: ${checks.count {it}}/${checks.size} verificaciones marcadas. Ítems marcados: ${checks.indices.filter {checks[it]}.joinToString(",") {"${it+1}"}.ifBlank {"ninguno"}}. Lista ${e.id}; edición ${e.json.optString("edicion")}. Solo práctica, sin autorización de administración.",
                )
            },
            !vm.busy,
        )
        Secondary("Editar lista local", { nav("editar/safety/$id") }, !vm.busy)
        Provenance(e)
        Section("Fuentes de la lista")
        if (e.references.isEmpty())
            Notice("La lista no declara fuentes; permanece como borrador.", true)
        else e.references.forEach { SourceCard(it) }
    }
}

@Composable
fun ContentScreen(
    vm: AppViewModel,
    nav: (String) -> Unit,
    back: () -> Unit,
    importFile: () -> Unit,
    exportFile: () -> Unit,
) {
    var undo by remember { mutableStateOf(false) }
    var reset by remember { mutableStateOf(false) }
    Page("Biblioteca local", "Tu contenido, disponible sin conexión", back) {
        AppCard(Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(13.dp),
            ) {
                ColorIcon(R.drawable.ic_folder, Purple, 48)
                Column {
                    Text(
                        "Todo empieza en tu dispositivo.",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        "Datos en este dispositivo",
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalPalette.current.muted,
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf(
                        "med" to "Fármacos",
                        "case" to "Casos",
                        "safety" to "Listas",
                        "protocol" to "Protocolos",
                    )
                    .forEach { (kind, label) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                vm.data.entries.count { it.kind == kind }.toString(),
                                fontFamily = HeadingFont,
                                fontSize = 28.sp,
                                color = Purple,
                            )
                            Text(label, style = MaterialTheme.typography.labelSmall)
                        }
                    }
            }
        }
        Notice(
            "La primera biblioteca ya viene en el APK. Los contenidos de farmacología están preparados como borradores, no como fichas clínicas completas."
        )
        Section("Editar en el dispositivo")
        listOf("med", "case", "safety", "protocol").forEach { kind ->
            Secondary(
                "Crear ${kindTitle(kind).lowercase()}",
                { nav(newEntryRoute(kind)) },
                !vm.busy,
            )
        }
        ActionRow(
            "Cuaderno PAE y apuntes",
            "Respaldo independiente de documentos de práctica",
            R.drawable.ic_edit,
            Teal,
        ) {
            nav("cuaderno")
        }
        ActionRow(
            "Multimedia y documentos",
            "Copias de archivos locales",
            R.drawable.ic_play,
            Blue,
        ) {
            nav("multimedia")
        }
        Section("Añade tu contenido")
        Primary(
            if (vm.busy) "Procesando…" else "Importar paquete JSON",
            importFile,
            !vm.busy,
            R.drawable.ic_download,
        )
        Expandable(
            "¿Cómo funciona?",
            "1. Completa la plantilla del proyecto.\n2. Convierte el XLSX a JSON con el conversor incluido, o usa directamente la plantilla JSON.\n3. Selecciona el archivo.\n4. Revisa cuántas fichas se añadirán o actualizarán.\n5. Confirma la importación.\n\nNo se inventa información a partir del nombre de un archivo. El texto se conserva literalmente.",
            true,
        )
        Expandable(
            "Formato y límites",
            "Importa esquemas 1, 2 o 3 y exporta esquema 3. El esquema 3 incorpora la ficha ampliada y requiere app v0.4: no lo importes en v0.2/v0.3. En esta edición, tanto el paquete como el catálogo completo admiten hasta 2 MB, 500 registros en total y 300 de cada tipo. Si una importación excede el límite conjunto, se rechaza sin modificar el catálogo. Cada registro tiene un ID estable. Para actualizar un paquete existente, incrementa version_contenido. La operación usa una transacción local; no borra otras fichas. Un respaldo importado conserva la autoría original de las fichas; se incorpora como un paquete nuevo, sin restaurar el control de versiones de los paquetes de origen. Una revisión declarada no es una certificación clínica.",
        )
        Section("Respaldo del catálogo")
        Secondary("Exportar catálogo a un archivo", exportFile, !vm.busy)
        Secondary(
            "Deshacer último cambio del catálogo",
            { undo = true },
            vm.data.hasBackup && !vm.busy,
        )
        Text(
            "El archivo exportado contiene fichas, casos, listas y protocolos. No incluye el cuaderno, multimedia, notas de pacientes ficticios, favoritos ni el historial de prácticas. Es un JSON legible, no un archivo cifrado.",
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
        Section("Preparado para el futuro")
        AppCard(Modifier.fillMaxWidth()) {
            Badge("SIN CONEXIÓN REMOTA", Teal)
            Text(
                "Carpeta editorial → paquete revisado → app",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                "La misma estructura podrá alimentar una actualización remota en otra fase. Google Drive, la lectura automática de documentos y los servicios de IA no están conectados en esta app.",
                style = MaterialTheme.typography.bodyMedium,
                color = LocalPalette.current.muted,
            )
        }
        TextButton(onClick = { reset = true }, enabled = !vm.busy) {
            Text("Restablecer biblioteca de demostración")
        }
        if (vm.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
    }
    if (undo)
        AlertDialog(
            onDismissRequest = { undo = false },
            title = { Text("¿Restaurar el catálogo anterior?") },
            text = {
                Text(
                    "Se reemplazará el catálogo actual por el estado previo a la última importación, edición o eliminación de una ficha. No se borrarán tus notas ni el historial de prácticas."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        undo = false
                        vm.undo()
                    }
                ) {
                    Text("Restaurar")
                }
            },
            dismissButton = { TextButton(onClick = { undo = false }) { Text("Cancelar") } },
        )
    if (reset)
        AlertDialog(
            onDismissRequest = { reset = false },
            title = { Text("¿Restablecer los ejemplos?") },
            text = {
                Text(
                    "Se borrarán fichas importadas o editadas, protocolos, planes PAE, apuntes, copias multimedia, notas de práctica, favoritos e historial. Exporta por separado el catálogo y el cuaderno, y conserva los originales multimedia. Estos respaldos no incluyen las notas de las fichas ficticias ni el historial."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        reset = false
                        vm.reset()
                    }
                ) {
                    Text("Restablecer")
                }
            },
            dismissButton = { TextButton(onClick = { reset = false }) { Text("Cancelar") } },
        )
}

@Composable
fun FavoritesScreen(vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    Page("Tus favoritos", "Tu biblioteca, a un toque", back) {
        val entries = vm.data.entries.filter { it.key in vm.data.favorites }
        val educational = vm.data.master.catalog?.searchItems.orEmpty().filter { it.route in vm.data.favorites }
        if (entries.isEmpty() && educational.isEmpty())
            EmptyState(
                "Un espacio para tus aliados",
                "Toca la estrella de una ficha, tema, dispositivo o caso para guardarlo aquí.",
            )
        educational.forEach { item ->
            AppCard(Modifier.fillMaxWidth(), onClick = { nav(item.route) }) {
                Text(item.title, style = MaterialTheme.typography.titleSmall)
                Text(item.subtitle, style = MaterialTheme.typography.bodySmall)
                Favorite(item.route, item.title, vm)
            }
        }
        entries.forEach { e ->
            AppCard(Modifier.fillMaxWidth(), onClick = { nav(e.key) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(e.name, style = MaterialTheme.typography.titleSmall)
                        Text(
                            e.group,
                            style = MaterialTheme.typography.bodySmall,
                            color = LocalPalette.current.muted,
                        )
                    }
                    Favorite(e.key, e.name, vm)
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    vm: AppViewModel,
    nav: (String) -> Unit,
    back: () -> Unit,
    info: (String, String) -> Unit,
) {
    var edit by remember { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf(vm.name) }
    Page("Mi espacio", "Una experiencia a tu medida", back) {
        AppCard(Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    Modifier.size(86.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Purple.copy(alpha = .18f))
                ) {
                    Image(
                        painterResource(R.drawable.nurse),
                        "Avatar de ejemplo",
                        Modifier.height(130.dp).align(Alignment.TopCenter),
                    )
                }
            }
            Text(
                vm.name,
                Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                "Perfil local de aprendizaje",
                Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                color = LocalPalette.current.muted,
                textAlign = TextAlign.Center,
            )
            TextButton(
                onClick = {
                    name = vm.name
                    edit = true
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text("Editar nombre de ejemplo")
            }
        }
        ActionRow(
            "Cuenta y actualizaciones",
            "Supabase opcional · biblioteca offline",
            R.drawable.ic_wifi,
            Teal,
        ) {
            nav("nube")
        }
        Section("Rendimiento adaptable")
        Row(
            Modifier.testTag("performance-options").horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            PerformancePolicy.modes.forEach { (mode, label) ->
                FilterChip(
                    vm.performanceMode == mode,
                    { vm.setPerformance(mode) },
                    label = { Text(label) },
                )
            }
        }
        Text(
            LocalPerformance.current.reason,
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
        Text(
            "Las funciones no cambian. Ahorro de batería, memoria crítica y accesibilidad pueden reducir movimiento incluso en modo completo.",
            style = MaterialTheme.typography.bodySmall,
        )
        Section("Hazla tuya")
        SettingSwitch(
            "Modo oscuro",
            "Una luz más suave para tus noches",
            R.drawable.ic_moon,
            vm.dark,
            vm::updateDark,
        )
        SettingSwitch(
            "Reducir animaciones",
            "Menos movimiento en la interfaz",
            R.drawable.ic_accessibility,
            vm.reduced,
            vm::updateReduced,
        )
        SettingSwitch(
            "Texto más grande",
            "Se suma al tamaño de texto de Android",
            R.drawable.ic_book,
            vm.largeText,
            vm::setLarge,
        )
        ActionRow(
            "Biblioteca local",
            "Importa y respalda el catálogo",
            R.drawable.ic_folder,
            Purple,
        ) {
            nav("contenido")
        }
        ActionRow(
            "Historial de práctica",
            "${vm.data.practices.size} registros educativos",
            R.drawable.ic_history,
            Teal,
        ) {
            nav("historial")
        }
        ActionRow(
            "Privacidad",
            "Datos locales · conexión opcional",
            R.drawable.ic_shield_check,
            Teal,
        ) {
            info(
                "Tu información, local",
                "El catálogo, favoritos, cuaderno y ejercicios permanecen en el teléfono. Si configuras Supabase y solicitas un código, tu correo se envía a ese proyecto. No se suben notas, planes, medios ni datos de pacientes. Los tokens de la sesión se protegen con Android Keystore.\n\nInternet se usa solo para las operaciones de cuenta y consulta de novedades solicitadas. Sin configuración no hay conexión. La app no es una historia clínica certificada: usa datos ficticios.\n\nLos respaldos de catálogo y cuaderno son separados y legibles, no cifrados; los medios requieren conservar originales. Desinstalar puede borrar los datos locales.",
            )
        }
        ActionRow(
            "Acerca de esta edición",
            "Android nativo · ${BuildConfig.VERSION_NAME}",
            R.drawable.ic_heart,
            Pink,
        ) {
            info(
                "Tu vocación, en 360°",
                "Enfermería iDreyK 360 · Prototipo B.\n\nEsta es una edición Android nativa de desarrollo, con almacenamiento local e importación de contenidos. No es una certificación clínica ni una versión de producción.\n\nIlustración generada con IA. Fuentes Outfit y Nunito Sans bajo SIL Open Font License; licencias incluidas en los recursos.\n\nLos módulos señalan su estado: práctico, simulado o pendiente de validar.",
            )
        }
        Text(
            "iDreyK 360 · Cuidar empieza por aprender.",
            Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = LocalPalette.current.muted,
        )
    }
    if (edit)
        AppSheet("Tu nombre de ejemplo", { edit = false }) {
            Input("Nombre", name, { name = it.take(24) })
            Primary(
                "Guardar nombre",
                {
                    vm.updateName(name)
                    edit = false
                },
                name.isNotBlank(),
            )
        }
}

@Composable
fun SettingSwitch(
    title: String,
    subtitle: String,
    icon: Int,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    AppCard(Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Symbol(icon, tint = Purple)
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalPalette.current.muted,
                )
            }
            Switch(checked, onChange)
        }
    }
}
