package com.idreyk.enfermeria360

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/** Layout hints only; no device-model lists or performance claims. */
object AdaptiveLayoutPolicy {
    fun columns(widthDp: Float, fontScale: Float, preferred: Int = 3): Int {
        val scale = fontScale.takeIf { it.isFinite() && it >= 1f } ?: 1f
        val width = widthDp.takeIf { it.isFinite() && it > 0f } ?: 0f
        return ((width + 9f) / (104f * scale + 9f)).toInt().coerceIn(1, preferred.coerceIn(1, 3))
    }

    fun showWelcomeArt(widthDp: Float, fontScale: Float): Boolean =
        widthDp >= 310f && fontScale.isFinite() && fontScale <= 1.2f
}

object UiFeedbackPolicy {
    fun pressScale(reduced: Boolean, pressed: Boolean): Float =
        if (!reduced && pressed) .985f else 1f

    fun tabScale(reduced: Boolean, selected: Boolean): Float =
        if (!reduced && selected) 1.07f else 1f
}

@Composable
internal fun ModuleGrid(
    modules: List<Module>,
    nav: (String) -> Unit,
    tag: String,
    preferred: Int = 3,
) {
    val scale = LocalDensity.current.fontScale
    BoxWithConstraints(Modifier.fillMaxWidth().testTag(tag)) {
        val count = AdaptiveLayoutPolicy.columns(maxWidth.value, scale, preferred)
        Column(
            Modifier.fillMaxWidth().semantics {
                collectionInfo = CollectionInfo((modules.size + count - 1) / count, count)
            },
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            modules.chunked(count).forEach { group ->
                Row(
                    Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                ) {
                    group.forEach { module ->
                        ModuleTile(
                            module,
                            nav,
                            Modifier.weight(1f).fillMaxHeight().testTag("module-${module.route}"),
                        )
                    }
                    repeat(count - group.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
internal fun WelcomeHero(name: String, dark: Boolean) {
    val density = LocalDensity.current
    BoxWithConstraints(
        Modifier.fillMaxWidth()
            .testTag("welcome-card")
            .clip(RoundedCornerShape(25.dp))
            .background(
                Brush.linearGradient(
                    if (dark) listOf(Color(0xFF4A335B), Color(0xFF6A436C))
                    else listOf(Color(0xFFEEE6FF), Color(0xFFF1DDF4))
                )
            )
            .border(1.dp, Purple.copy(alpha = .15f), RoundedCornerShape(25.dp))
    ) {
        val showArt = AdaptiveLayoutPolicy.showWelcomeArt(maxWidth.value, density.fontScale)
        val illustrationWidth = (maxWidth * .38f).coerceAtMost(160.dp)
        Row(
            Modifier.fillMaxWidth().heightIn(min = 202.dp).padding(19.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(
                    "TU ESPACIO DE CUIDADO",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (dark) Color(0xFFD4B4E8) else Color(0xFF8F66A9),
                )
                Text(
                    "Hola,\n${name.substringBefore(' ').take(13)}",
                    modifier = Modifier.testTag("welcome-greeting"),
                    style = MaterialTheme.typography.headlineLarge,
                )
                Text(
                    "Un nuevo día para\nhacer la diferencia.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (dark) Color(0xFFD9C4E2) else Color(0xFF866C99),
                )
                Symbol(
                    R.drawable.ic_heart_pulse,
                    tint = Purple.copy(alpha = .6f),
                    modifier = Modifier.size(28.dp),
                )
            }
            if (showArt) {
                val amplitude = with(density) { 4.dp.toPx() }
                val bob =
                    if (LocalReduced.current) 0f
                    else
                        rememberInfiniteTransition(label = "Bienvenida")
                            .animateFloat(
                                0f,
                                -amplitude,
                                infiniteRepeatable(tween(3000), RepeatMode.Reverse),
                                label = "Ilustración decorativa",
                            )
                            .value
                Image(
                    painterResource(R.drawable.nurse),
                    null,
                    Modifier.width(illustrationWidth)
                        .height(188.dp)
                        .testTag("welcome-art")
                        .graphicsLayer { translationY = bob },
                )
            }
        }
    }
}
