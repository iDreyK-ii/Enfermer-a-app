package com.idreyk.enfermeria360

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Pink = Color(0xFFD64792)
val Purple = Color(0xFFA271CD)
val Teal = Color(0xFF23AEB3)
val Gold = Color(0xFFD9A04B)
val Blue = Color(0xFF6898DA)
val Green = Color(0xFF48AD8A)
val HeadingFont =
    FontFamily(
        Font(R.font.outfit_400, FontWeight.Normal),
        Font(R.font.outfit_600, FontWeight.SemiBold),
        Font(R.font.outfit_800, FontWeight.ExtraBold),
    )
val BodyFont =
    FontFamily(
        Font(R.font.nunito_sans_400, FontWeight.Normal),
        Font(R.font.nunito_sans_600, FontWeight.SemiBold),
        Font(R.font.nunito_sans_800, FontWeight.ExtraBold),
    )

data class Palette(
    val background: Color,
    val surface: Color,
    val ink: Color,
    val muted: Color,
    val border: Color,
    val soft: Color,
)

val LightPalette =
    Palette(
        Color(0xFFFAF6FF),
        Color.White,
        Color(0xFF30253E),
        Color(0xFF7A6785),
        Color(0xFFEBDFF1),
        Color(0xFFF2E8F8),
    )
val DarkPalette =
    Palette(
        Color(0xFF211A2D),
        Color(0xFF30263E),
        Color(0xFFF1E8F8),
        Color(0xFFC1ADCF),
        Color(0xFF493553),
        Color(0xFF3E2C4B),
    )
val LocalPalette = staticCompositionLocalOf { LightPalette }
val LocalReduced = staticCompositionLocalOf { false }
val LocalFormEnabled = staticCompositionLocalOf { true }

@Composable
fun NursingTheme(dark: Boolean, reduced: Boolean, content: @Composable () -> Unit) {
    val p = if (dark) DarkPalette else LightPalette
    val colors =
        if (dark)
            darkColorScheme(
                primary = Color(0xFFF291C4),
                secondary = Color(0xFFC49BE0),
                tertiary = Color(0xFF7CCFCB),
                background = p.background,
                surface = p.surface,
                onBackground = p.ink,
                onSurface = p.ink,
                onPrimary = Color(0xFF381A2C),
                outline = p.border,
            )
        else
            lightColorScheme(
                primary = Color(0xFFC32F7A),
                secondary = Purple,
                tertiary = Teal,
                background = p.background,
                surface = p.surface,
                onBackground = p.ink,
                onSurface = p.ink,
                onPrimary = Color.White,
                outline = p.border,
            )
    val type =
        Typography(
            headlineLarge =
                TextStyle(
                    fontFamily = HeadingFont,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 34.sp,
                ),
            headlineMedium =
                TextStyle(
                    fontFamily = HeadingFont,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 30.sp,
                ),
            titleLarge =
                TextStyle(
                    fontFamily = HeadingFont,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 26.sp,
                ),
            titleMedium =
                TextStyle(
                    fontFamily = HeadingFont,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 23.sp,
                ),
            titleSmall =
                TextStyle(
                    fontFamily = BodyFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 20.sp,
                ),
            bodyLarge = TextStyle(fontFamily = BodyFont, fontSize = 16.sp, lineHeight = 24.sp),
            bodyMedium = TextStyle(fontFamily = BodyFont, fontSize = 14.sp, lineHeight = 21.sp),
            bodySmall = TextStyle(fontFamily = BodyFont, fontSize = 12.sp, lineHeight = 18.sp),
            labelLarge =
                TextStyle(
                    fontFamily = BodyFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                ),
            labelMedium =
                TextStyle(
                    fontFamily = BodyFont,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
            labelSmall =
                TextStyle(fontFamily = BodyFont, fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
        )
    CompositionLocalProvider(LocalPalette provides p, LocalReduced provides reduced) {
        MaterialTheme(colorScheme = colors, typography = type, content = content)
    }
}

@Composable
fun Symbol(
    id: Int,
    description: String? = null,
    tint: Color = LocalPalette.current.muted,
    modifier: Modifier = Modifier,
) {
    Icon(painterResource(id), description, modifier.size(22.dp), tint)
}

@Composable
fun ColorIcon(id: Int, color: Color = Purple, size: Int = 43) {
    Box(
        Modifier.size(size.dp)
            .shadow(if (LocalReduced.current) 0.dp else 3.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(color.copy(alpha = .6f), color)))
            .border(1.dp, Color.White.copy(alpha = .45f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Symbol(id, tint = Color.White, modifier = Modifier.size((size * .58).dp))
    }
}

@Composable
fun Badge(text: String, color: Color = Purple) {
    Text(
        text,
        Modifier.clip(RoundedCornerShape(7.dp))
            .background(color.copy(alpha = .12f))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        color =
            if (LocalPalette.current == DarkPalette)
                color.copy(alpha = 1f).let {
                    Color(
                        (it.red + .25f).coerceAtMost(1f),
                        (it.green + .2f).coerceAtMost(1f),
                        (it.blue + .15f).coerceAtMost(1f),
                    )
                }
            else color,
        style = MaterialTheme.typography.labelSmall,
    )
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val p = LocalPalette.current
    val source = if (onClick != null) remember { MutableInteractionSource() } else null
    val pressed = source?.collectIsPressedAsState()?.value ?: false
    val reduced = LocalReduced.current
    val scale =
        if (reduced || onClick == null) 1f
        else
            animateFloatAsState(
                    UiFeedbackPolicy.pressScale(false, pressed),
                    spring(dampingRatio = 1f, stiffness = 650f),
                    label = "Respuesta de tarjeta",
                )
                .value
    Column(
        modifier
            .then(
                if (!reduced && onClick != null)
                    Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                else Modifier
            )
            .shadow(if (reduced) 0.dp else 1.dp, RoundedCornerShape(21.dp))
            .clip(RoundedCornerShape(21.dp))
            .background(if (pressed) lerp(p.surface, Purple, .07f) else p.surface)
            .border(1.dp, p.border, RoundedCornerShape(21.dp))
            .then(
                if (onClick != null)
                    Modifier.clickable(
                        interactionSource = source!!,
                        indication = LocalIndication.current,
                        role = Role.Button,
                        onClick = onClick,
                    )
                else Modifier
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
        content = content,
    )
}

@Composable
fun PageHeader(
    title: String,
    subtitle: String = "",
    back: () -> Unit,
    action: @Composable () -> Unit = {},
) {
    Row(
        Modifier.fillMaxWidth().padding(top = 9.dp, bottom = 17.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        IconButton(
            onClick = back,
            modifier =
                Modifier.size(48.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(LocalPalette.current.surface)
                    .border(1.dp, LocalPalette.current.border, RoundedCornerShape(13.dp)),
        ) {
            Symbol(R.drawable.ic_chevron_left, "Volver", Purple)
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            if (subtitle.isNotBlank())
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalPalette.current.muted,
                    modifier = Modifier.padding(top = 4.dp),
                )
        }
        action()
    }
}

@Composable
fun Page(
    title: String,
    subtitle: String = "",
    back: () -> Unit,
    action: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        PageHeader(title, subtitle, back, action)
        Column(
            Modifier.weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 25.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            content = content,
        )
    }
}

@Composable
fun Section(title: String, action: String? = null, onAction: () -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth().padding(top = 7.dp, bottom = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
        if (action != null)
            TextButton(onClick = onAction) {
                Text(action, style = MaterialTheme.typography.labelMedium)
            }
    }
}

@Composable
fun Notice(text: String, warning: Boolean = false) {
    val c = if (warning) Gold else Purple
    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(c.copy(alpha = .09f))
            .border(1.dp, c.copy(alpha = .18f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Symbol(
            if (warning) R.drawable.ic_lock else R.drawable.ic_info,
            tint = c,
            modifier = Modifier.size(17.dp),
        )
        Text(text, style = MaterialTheme.typography.bodySmall, color = LocalPalette.current.muted)
    }
}

@Composable
fun ActionRow(title: String, subtitle: String, icon: Int, color: Color, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .shadow(if (LocalReduced.current) 0.dp else 1.dp, RoundedCornerShape(19.dp))
            .clip(RoundedCornerShape(19.dp))
            .background(LocalPalette.current.surface)
            .border(1.dp, LocalPalette.current.border, RoundedCornerShape(19.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(color.copy(alpha = .12f)),
            contentAlignment = Alignment.Center,
        ) {
            Symbol(icon, tint = color)
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(
                subtitle,
                color = LocalPalette.current.muted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        Symbol(R.drawable.ic_chevron_right, tint = Purple, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun Primary(text: String, onClick: () -> Unit, enabled: Boolean = true, icon: Int? = null) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 13.dp),
    ) {
        if (icon != null) {
            Symbol(
                icon,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(text, textAlign = TextAlign.Center)
    }
}

@Composable
fun Secondary(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().heightIn(min = 46.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, LocalPalette.current.border),
    ) {
        Text(text, textAlign = TextAlign.Center)
    }
}

@Composable
fun Input(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    numeric: Boolean = false,
    unit: String? = null,
    enabled: Boolean = true,
    multi: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        singleLine = !multi,
        minLines = if (multi) 3 else 1,
        enabled = enabled && LocalFormEnabled.current,
        keyboardOptions =
            KeyboardOptions(
                keyboardType = if (numeric) KeyboardType.Decimal else KeyboardType.Text
            ),
        suffix =
            if (unit != null) ({ Text(unit, style = MaterialTheme.typography.bodySmall) }) else null,
    )
}

@Composable
fun SearchBox(value: String, onChange: (String) -> Unit, placeholder: String = "Buscar...") {
    OutlinedTextField(
        value,
        onChange,
        Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall) },
        leadingIcon = { Symbol(R.drawable.ic_search) },
        trailingIcon =
            if (value.isNotEmpty())
                ({
                    IconButton(onClick = { onChange("") }) {
                        Symbol(R.drawable.ic_close, "Limpiar búsqueda")
                    }
                })
            else null,
        singleLine = true,
        shape = RoundedCornerShape(15.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = LocalPalette.current.surface,
                focusedContainerColor = LocalPalette.current.surface,
                unfocusedBorderColor = LocalPalette.current.border,
            ),
    )
}

@Composable
fun Favorite(key: String, name: String, vm: AppViewModel) {
    val active = key in vm.data.favorites
    IconButton(onClick = { vm.favorite(key) }, enabled = !vm.busy) {
        Symbol(
            R.drawable.ic_star,
            if (active) "Quitar $name de favoritos" else "Guardar $name en favoritos",
            if (active) Gold else LocalPalette.current.muted,
        )
    }
}

@Composable
fun EmptyState(title: String, description: String) {
    AppCard(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Symbol(R.drawable.ic_book, tint = Purple, modifier = Modifier.size(36.dp))
        }
        Text(
            title,
            Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            description,
            Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium,
            color = LocalPalette.current.muted,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun Expandable(title: String, body: String, initial: Boolean = false) {
    var open by remember(title) { mutableStateOf(initial) }
    val duration = if (LocalReduced.current) 0 else 220
    AppCard(
        Modifier.fillMaxWidth().animateContentSize(tween(duration)),
        onClick = { open = !open },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
            Symbol(
                if (open) R.drawable.ic_minus else R.drawable.ic_plus,
                tint = Purple,
                modifier = Modifier.size(17.dp),
            )
        }
        if (open)
            Text(
                body.ifBlank {
                    "Pendiente de documentar y revisar. Un campo vacío no significa ausencia de riesgo. Si no aplica, debe justificarse."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = LocalPalette.current.muted,
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSheet(
    title: String,
    onDismiss: () -> Unit,
    scrollState: androidx.compose.foundation.ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = LocalPalette.current.background,
    ) {
        Column(
            Modifier.fillMaxWidth()
                .heightIn(max = 600.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 23.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
            content()
        }
    }
}

@Composable
fun ProgressLine(progress: Float) {
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape),
        color = Purple,
        trackColor = Purple.copy(alpha = .14f),
    )
}

@Composable
fun SourceCard(reference: Reference) {
    val uriHandler = LocalUriHandler.current
    var openFailed by remember(reference.url) { mutableStateOf(false) }
    AppCard(Modifier.fillMaxWidth()) {
        Badge("FUENTE DOCUMENTAL", Teal)
        Text(reference.title, style = MaterialTheme.typography.titleSmall)
        if (reference.publisher.isNotBlank())
            Text(
                reference.publisher,
                style = MaterialTheme.typography.bodySmall,
                color = LocalPalette.current.muted,
            )
        androidx.compose.foundation.text.selection.SelectionContainer {
            Text(
                reference.url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        if (reference.accessed.isNotBlank())
            Text(
                "Consulta documental: ${reference.accessed}",
                style = MaterialTheme.typography.labelSmall,
                color = LocalPalette.current.muted,
            )
        if (ExternalReferencePolicy.canOpen(reference.url)) {
            TextButton(onClick = {
                openFailed = try {
                    uriHandler.openUri(reference.url)
                    false
                } catch (_: Exception) {
                    true
                }
            }, modifier = Modifier.heightIn(min = 48.dp)) {
                Text("Abrir fuente externa")
            }
            Text("Se abre en otra aplicación; puede requerir Internet.", style = MaterialTheme.typography.labelSmall,
                color = LocalPalette.current.muted)
            if (openFailed) Text("No se pudo abrir. Puedes copiar la dirección mostrada.",
                style = MaterialTheme.typography.bodySmall, color = LocalPalette.current.muted)
        }
    }
}
