package com.idreyk.enfermeria360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: AppViewModel = viewModel()
            val density = LocalDensity.current
            val view = LocalView.current
            val signals = rememberDeviceSignals(lifecycle)
            val performance = PerformancePolicy.decide(vm.performanceMode, vm.reduced, signals)
            SideEffect {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                    !vm.dark
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                    !vm.dark
            }
            CompositionLocalProvider(
                LocalDensity provides
                    Density(density.density, density.fontScale * (if (vm.largeText) 1.12f else 1f)),
                LocalPerformance provides performance,
                LocalFormEnabled provides !vm.busy,
            ) {
                NursingTheme(vm.dark, performance.reduced) { NursingApp(vm) }
            }
        }
    }
}

@Composable
fun NursingApp(vm: AppViewModel) {
    var stack by rememberSaveable { mutableStateOf("inicio") }
    val route = stack.substringAfterLast('|')
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    LaunchedEffect(vm.busy, route) {
        if (vm.busy && route.startsWith("editar")) focusManager.clearFocus(force = true)
    }
    val holder = rememberSaveableStateHolder()
    val snackbar = remember { SnackbarHostState() }
    var infoTitle by rememberSaveable { mutableStateOf("") }
    var infoText by rememberSaveable { mutableStateOf("") }
    val nav: (String) -> Unit = { next ->
        if (next != route && !vm.busy) {
            snackbar.currentSnackbarData?.dismiss()
            stack += "|$next"
        }
    }
    val rootNav: (String) -> Unit = { next ->
        if (!vm.busy) {
            snackbar.currentSnackbarData?.dismiss()
            stack = if (next == "inicio") "inicio" else "inicio|$next"
        }
    }
    val back: () -> Unit = {
        if (!vm.busy) {
            snackbar.currentSnackbarData?.dismiss()
            if (route.startsWith("editar")) holder.removeState(route)
            stack = if ('|' in stack) stack.substringBeforeLast('|') else "inicio"
        }
    }
    val info: (String, String) -> Unit = { title, body ->
        infoTitle = title
        infoText = body
    }
    val importer =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) vm.inspect(uri)
        }
    val exporter =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/json")
        ) { uri ->
            if (uri != null) vm.export(uri)
        }
    val mediaImporter =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) vm.importMedia(uri)
        }
    val notebookImporter =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) vm.previewNotebook(uri)
        }
    val notebookExporter =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/json")
        ) { uri ->
            if (uri != null) vm.exportNotebook(uri)
        }
    val openImport: () -> Unit = { importer.launch(arrayOf("*/*")) }
    val exportCatalog: () -> Unit = { exporter.launch("idreyk_catalogo_${LocalDate.now()}.json") }
    BackHandler('|' in stack) { back() }
    LaunchedEffect(vm.message) {
        vm.message?.let {
            snackbar.showSnackbar(it, duration = SnackbarDuration.Short)
            vm.clearMessage()
        }
    }
    val palette = LocalPalette.current
    Scaffold(
        containerColor = palette.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (!vm.data.loading && !route.startsWith("editar")) AppBottomBar(route, rootNav)
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Box(
            Modifier.fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        if (vm.dark) listOf(Color(0xFF2A1E36), palette.background)
                        else listOf(Color(0xFFF5EBFC), Color(0xFFFCF2F9), palette.background)
                    )
                )
        ) {
            if (vm.data.loading) {
                Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    ColorIcon(R.drawable.ic_brand, Purple, 75)
                    Text("Tu espacio está abriendo…", style = MaterialTheme.typography.titleMedium)
                    CircularProgressIndicator(color = Purple)
                }
            } else {
                val duration = if (LocalReduced.current) 0 else 210
                AnimatedContent(
                    targetState = route,
                    transitionSpec = {
                        fadeIn(tween(duration)) +
                            slideInVertically(tween(duration)) { it / 40 } togetherWith
                            fadeOut(tween(if (duration == 0) 0 else 90))
                    },
                    label = "Navegación de pantallas",
                ) { current ->
                    holder.SaveableStateProvider(current) {
                        val section = current.substringBefore('/')
                        val id = current.substringAfter('/', "")
                        when (section) {
                            "inicio" -> HomeScreen(vm, nav, info)
                            "modulos" -> ModulesScreen(nav, back)
                            "farmacologia" -> PharmacyScreen(vm, nav, back)
                            "med" -> DrugScreen(id, vm, nav, back)
                            "escalas" -> ScaleLibraryScreen(nav, back)
                            "biblioteca-escalas" -> ClinicalCatalogScreen(nav, back)
                            "instrumento" -> ClinicalInstrumentScreen(id, nav, back)
                            "dolor-numerico" -> NumericPainPracticeScreen(vm, back)
                            "test-4at" -> FourAtPracticeScreen(vm, back)
                            "glasgow" -> GlasgowScreen(vm, back)
                            "eva" -> EvaScreen(vm, back)
                            "capurro" -> CapurroCalculatorScreen(vm, back, if (id == "A") "A" else "B")
                            "escala" -> NeonatalScoreScreen(id, vm, back)
                            "glasgow-pediatrico" -> PediatricGlasgowRecord(vm, back)
                            "registro-escala" -> ExternalScaleScreen(id, vm, back)
                            "editar" ->
                                EntryEditorScreen(
                                    id.substringBefore('/'),
                                    id.substringAfter('/'),
                                    vm,
                                    back,
                                )
                            "protocolos" -> ProtocolsScreen(vm, nav, back)
                            "protocol" -> ProtocolDetail(id, vm, nav, back)
                            "cuaderno" ->
                                NotebookHub(
                                    vm,
                                    nav,
                                    back,
                                    { notebookImporter.launch(arrayOf("*/*")) },
                                    {
                                        notebookExporter.launch(
                                            "idreyk_cuaderno_${LocalDate.now()}.json"
                                        )
                                    },
                                )
                            "editar-plan" ->
                                PlanEditorScreen(
                                    id.substringBefore('/'),
                                    id.substringAfter('/'),
                                    vm,
                                    back,
                                )
                            "editar-apunte" -> StudyNoteEditor(id, vm, back)
                            "multimedia" ->
                                MediaLibraryScreen(vm, back) {
                                    mediaImporter.launch(MediaFiles.extensions.keys.toTypedArray())
                                }
                            "seguridad" -> SafetyScreen(vm, nav, back)
                            "safety" -> SafetyDetail(id, vm, nav, back)
                            "contenido" -> ContentScreen(vm, nav, back, openImport, exportCatalog)
                            "pae" -> PaeScreen(nav, back, info)
                            "valoracion" -> AssessmentScreen(vm, back)
                            "pacientes" -> PatientsScreen(nav, back)
                            "paciente" -> PatientScreen(id, vm, nav, back)
                            "evolucion" -> EvolutionScreen(id, vm, back)
                            "signos" -> VitalsScreen(id, vm, back)
                            "balance" -> BalanceScreen(id, vm, back)
                            "plan" -> CarePlansScreen(id, vm, nav, back)
                            "herramientas" -> ToolsScreen(nav, back)
                            "calc" ->
                                if (id in listOf("dosis", "dilucion"))
                                    DoseExerciseScreen(id, vm, back)
                                else CalculatorScreen(id, vm, back)
                            "ecg" -> EcgScreen(back)
                            "aprendizaje" -> LearningScreen(nav, back, info)
                            "tarjetas" -> FlashScreen(vm, back)
                            "quiz" -> QuizScreen(vm, back)
                            "apuntes" -> StudyNotesScreen(vm, nav, back)
                            "casos" -> CasesScreen(vm, nav, back)
                            "case" -> CaseScreen(id, vm, nav, back)
                            "favoritos" -> FavoritesScreen(vm, nav, back)
                            "perfil" -> ProfileScreen(vm, nav, back, info)
                            "nube" -> CloudScreen(vm, nav, back)
                            "historial" -> HistoryScreen(vm, back)
                            "buscar" -> GlobalSearchScreen(vm, nav, back)
                            "biblioteca" -> KnowledgeLibraryScreen(vm, nav, back)
                            "tema" -> KnowledgeTopicScreen(id, vm, back)
                            "inventario" -> NominalInventoryScreen(vm, nav, back)
                            "nominal" -> NominalDetailScreen(id, vm, nav, back)
                            "atlas" -> AtlasLibraryScreen(vm, nav, back)
                            "dispositivo" -> AtlasDeviceScreen(id, vm, back)
                            "bioseguridad" -> BiosecurityScreen(vm, nav, back)
                            "higiene" -> HygieneScreen(vm, back)
                            "lista" -> EducationalChecklistScreen(id, vm, back)
                            "simulacion" -> SyntheticCaseScreen(id, vm, back)
                            "avance" -> LearningProgressScreen(vm, nav, back)
                            "estado-modulos" -> ModuleStatusScreen(vm, nav, back)
                            else ->
                                Page("Volvamos al inicio", back = back) {
                                    EmptyState(
                                        "Este espacio no está disponible",
                                        "El contenido pudo cambiar al importar un paquete.",
                                    )
                                    Primary("Ir al inicio", { rootNav("inicio") })
                                }
                        }
                    }
                }
            }
        }
    }
    if (vm.busy && route.startsWith("editar")) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Aplicando cambio local…") },
            text = {
                Text(
                    "El formulario está temporalmente bloqueado para no perder cambios mientras se escribe en el dispositivo."
                )
            },
            confirmButton = {},
        )
    }
    if (!vm.welcomed && !vm.data.loading)
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Symbol(R.drawable.ic_shield_check, tint = Teal, modifier = Modifier.size(34.dp))
            },
            title = { Text("Bienvenido a tu espacio") },
            text = {
                Text(
                    "iDreyK 360 · edición Android de desarrollo.\n\nLa consulta local funciona sin conexión y sin cuenta obligatoria. Las cuentas y novedades online son opcionales y requieren configurar Supabase. El catálogo inicial está incluido como borradores.\n\nUsa únicamente datos ficticios. Las funciones clínicas y contenidos pendientes están señalados. No es un producto certificado para atención de pacientes."
                )
            },
            confirmButton = {
                TextButton(onClick = { vm.acceptWelcome() }) { Text("Explorar la app") }
            },
        )
    if (infoTitle.isNotBlank())
        AppSheet(infoTitle, { infoTitle = "" }) {
            Text(infoText, style = MaterialTheme.typography.bodyMedium, color = palette.muted)
            Secondary("Volver al recorrido", { infoTitle = "" })
        }
    vm.notebookPreview?.let { records ->
        AppSheet("Revisar importación de cuaderno", { vm.cancelNotebookImport() }) {
            Text(
                "${records.size} documentos. ${records.count {incoming -> vm.data.notebooks.any {it.id==incoming.id}}} reemplazan IDs existentes.",
                style = MaterialTheme.typography.titleMedium,
            )
            Notice(
                "Solo planes y apuntes de práctica. Se conservarán los demás documentos y un estado anterior para deshacer. No incluyas datos reales de pacientes.",
                true,
            )
            records.take(10).forEach {
                Text("• ${it.title}", style = MaterialTheme.typography.bodySmall)
            }
            Primary(
                "Confirmar importación del cuaderno",
                { vm.importNotebookConfirmed() },
                !vm.busy,
            )
            Secondary("Cancelar", { vm.cancelNotebookImport() }, !vm.busy)
        }
    }
    BackHandler(vm.busy) { /* Do not leave a pending transactional operation. */ }
    vm.preview?.let { preview ->
        CatalogImportSheet(preview, vm.busy, vm::importConfirmed, vm::cancelImport)
    }
}

@Composable
fun AppBottomBar(route: String, nav: (String) -> Unit) {
    val section = route.substringBefore('/')
    val active =
        when (section) {
            "inicio" -> "inicio"
            "perfil",
            "nube",
            "historial",
            "contenido" -> "perfil"
            "favoritos" -> "favoritos"
            "farmacologia",
            "inventario",
            "nominal",
            "med" -> "farmacologia"
            "paciente",
            "pacientes",
            "evolucion",
            "signos",
            "balance",
            "plan" -> "modulos"
            else -> "modulos"
        }
    Surface(
        color = LocalPalette.current.surface,
        shadowElevation = if (LocalReduced.current) 0.dp else 7.dp,
    ) {
        Row(
            Modifier.fillMaxWidth()
                .navigationBarsPadding()
                .heightIn(min = 72.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf(
                    Triple("modulos", "Módulos", R.drawable.ic_grid),
                    Triple("farmacologia", "Fármacos", R.drawable.ic_pill),
                    Triple("inicio", "Inicio", R.drawable.ic_home),
                    Triple("favoritos", "Favoritos", R.drawable.ic_heart),
                    Triple("perfil", "Perfil", R.drawable.ic_user),
                )
                .forEach { (key, title, icon) ->
                    val reduced = LocalReduced.current
                    val targetTint = if (active == key) Pink else LocalPalette.current.muted
                    val tint =
                        if (reduced) targetTint
                        else
                            animateColorAsState(
                                    targetTint,
                                    tween(160),
                                    label = "Pestaña seleccionada",
                                )
                                .value
                    val iconScale =
                        if (reduced) 1f
                        else
                            androidx.compose.animation.core
                                .animateFloatAsState(
                                    UiFeedbackPolicy.tabScale(false, active == key),
                                    tween(180),
                                    label = "Énfasis de navegación",
                                )
                                .value
                    Column(
                        Modifier.weight(1f)
                            .clip(RoundedCornerShapeCompat)
                            .clickable(role = Role.Tab) { nav(key) }
                            .semantics { selected = active == key }
                            .padding(vertical = 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        if (key == "inicio")
                            Box(
                                Modifier.size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(Color(0xFFF78ABD), Pink))
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Symbol(icon, title, Color.White, Modifier.size(24.dp))
                            }
                        else
                            Symbol(
                                icon,
                                title,
                                tint,
                                Modifier.size(22.dp).graphicsLayer {
                                    scaleX = iconScale
                                    scaleY = iconScale
                                },
                            )
                        Text(
                            title,
                            fontFamily = BodyFont,
                            fontSize = 10.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            color =
                                if (active == key) MaterialTheme.colorScheme.primary
                                else LocalPalette.current.muted,
                        )
                    }
                }
        }
    }
}

private val RoundedCornerShapeCompat = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
