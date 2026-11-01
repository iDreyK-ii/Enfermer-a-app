package com.idreyk.enfermeria360

import android.os.Bundle
import android.provider.Settings
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
            val systemReduced = remember {
                Settings.Global.getFloat(
                    contentResolver,
                    Settings.Global.ANIMATOR_DURATION_SCALE,
                    1f,
                ) == 0f
            }
            SideEffect {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                    !vm.dark
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                    !vm.dark
            }
            CompositionLocalProvider(
                LocalDensity provides
                    Density(density.density, density.fontScale * (if (vm.largeText) 1.12f else 1f))
            ) {
                NursingTheme(vm.dark, vm.reduced || systemReduced) { NursingApp(vm) }
            }
        }
    }
}

@Composable
fun NursingApp(vm: AppViewModel) {
    var stack by rememberSaveable { mutableStateOf("inicio") }
    val route = stack.substringAfterLast('|')
    val holder = rememberSaveableStateHolder()
    val snackbar = remember { SnackbarHostState() }
    var infoTitle by rememberSaveable { mutableStateOf("") }
    var infoText by rememberSaveable { mutableStateOf("") }
    val nav: (String) -> Unit = { next ->
        if (next != route) {
            stack += "|$next"
        }
    }
    val rootNav: (String) -> Unit = { next ->
        stack = if (next == "inicio") "inicio" else "inicio|$next"
    }
    val back: () -> Unit = {
        stack = if ('|' in stack) stack.substringBeforeLast('|') else "inicio"
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
        bottomBar = { if (!vm.data.loading) AppBottomBar(route, rootNav) },
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
                            "escalas" -> ScalesScreen(nav, back, info)
                            "glasgow" -> GlasgowScreen(vm, back)
                            "eva" -> EvaScreen(vm, back)
                            "capurro" -> CapurroScreen(back)
                            "seguridad" -> SafetyScreen(vm, nav, back)
                            "safety" -> SafetyDetail(id, vm, back)
                            "contenido" -> ContentScreen(vm, back, openImport, exportCatalog)
                            "pae" -> PaeScreen(nav, back, info)
                            "valoracion" -> AssessmentScreen(vm, back)
                            "pacientes" -> PatientsScreen(nav, back)
                            "paciente" -> PatientScreen(id, vm, nav, back)
                            "evolucion" -> EvolutionScreen(id, vm, back)
                            "signos" -> VitalsScreen(id, vm, back)
                            "balance" -> BalanceScreen(id, vm, back)
                            "plan" -> CareScreen(id, vm, back)
                            "herramientas" -> ToolsScreen(nav, back)
                            "calc" -> CalculatorScreen(id, vm, back)
                            "ecg" -> EcgScreen(back)
                            "aprendizaje" -> LearningScreen(nav, back, info)
                            "tarjetas" -> FlashScreen(vm, back)
                            "quiz" -> QuizScreen(vm, back)
                            "apuntes" -> NotesScreen(back, info)
                            "casos" -> CasesScreen(vm, nav, back)
                            "case" -> CaseScreen(id, vm, nav, back)
                            "favoritos" -> FavoritesScreen(vm, nav, back)
                            "perfil" -> ProfileScreen(vm, nav, back, info)
                            "historial" -> HistoryScreen(vm, back)
                            "buscar" -> GlobalSearchScreen(vm, nav, back)
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
    if (!vm.welcomed && !vm.data.loading)
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Symbol(R.drawable.ic_shield_check, tint = Teal, modifier = Modifier.size(34.dp))
            },
            title = { Text("Bienvenido a tu espacio") },
            text = {
                Text(
                    "iDreyK 360 · edición Android de desarrollo.\n\nFunciona sin cuentas y sin conexión. El catálogo inicial ya está incluido.\n\nUsa únicamente datos ficticios. Las funciones clínicas y contenidos pendientes están señalados. No es un producto certificado para atención de pacientes."
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
    vm.preview?.let { preview ->
        AppSheet("Revisa antes de importar", { vm.cancelImport() }) {
            Badge("PAQUETE LOCAL · ESQUEMA 1", Teal)
            Text(preview.content.title, style = MaterialTheme.typography.titleLarge)
            Text(
                "Autor: ${preview.content.author}\nVersión: ${preview.content.version}\nActualización declarada: ${preview.content.updated}",
                style = MaterialTheme.typography.bodySmall,
                color = palette.muted,
            )
            AppCard(Modifier.fillMaxWidth()) {
                Text(
                    "${preview.newCount} registros nuevos\n${preview.updateCount} registros que se actualizarán",
                    style = MaterialTheme.typography.titleMedium,
                )
                preview.content.entries.take(12).forEach {
                    Text("• ${it.name}", style = MaterialTheme.typography.bodySmall)
                }
                if (preview.content.entries.size > 12)
                    Text(
                        "Y ${preview.content.entries.size-12} más…",
                        style = MaterialTheme.typography.bodySmall,
                    )
            }
            Notice(
                "Solo se revisó la estructura del archivo, no su validez clínica ni sus derechos. Confirma que puedes usar el contenido. No incluyas información de pacientes reales.",
                true,
            )
            Text(
                "La importación reemplaza fichas con el mismo tipo e ID y conserva las demás. Se guardará un respaldo del catálogo anterior.",
                style = MaterialTheme.typography.bodySmall,
                color = palette.muted,
            )
            Primary(
                "Confirmar importación local",
                { vm.importConfirmed() },
                !vm.busy,
                R.drawable.ic_check,
            )
            Secondary("Cancelar", { vm.cancelImport() }, !vm.busy)
        }
    }
}

@Composable
fun AppBottomBar(route: String, nav: (String) -> Unit) {
    val section = route.substringBefore('/')
    val active =
        when (section) {
            "inicio" -> "inicio"
            "perfil",
            "historial",
            "contenido" -> "perfil"
            "favoritos" -> "favoritos"
            "paciente",
            "pacientes",
            "evolucion",
            "signos",
            "balance",
            "plan" -> "pacientes"
            else -> "modulos"
        }
    Surface(color = LocalPalette.current.surface, shadowElevation = 7.dp) {
        Row(
            Modifier.fillMaxWidth()
                .navigationBarsPadding()
                .heightIn(min = 72.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf(
                    Triple("modulos", "Módulos", R.drawable.ic_grid),
                    Triple("pacientes", "Pacientes", R.drawable.ic_users),
                    Triple("inicio", "Inicio", R.drawable.ic_home),
                    Triple("favoritos", "Favoritos", R.drawable.ic_heart),
                    Triple("perfil", "Perfil", R.drawable.ic_user),
                )
                .forEach { (key, title, icon) ->
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
                                if (active == key) Pink else LocalPalette.current.muted,
                                Modifier.size(22.dp),
                            )
                        Text(
                            title,
                            fontFamily = BodyFont,
                            fontSize = 10.sp,
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
