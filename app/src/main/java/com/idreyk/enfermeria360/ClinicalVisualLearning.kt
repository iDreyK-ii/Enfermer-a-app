package com.idreyk.enfermeria360

import android.graphics.BitmapFactory
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/** Seven original, symbolic area illustrations, not standardized assessment stimuli. */
data class ClinicalAreaArt(val asset: String, val description: String, val areaTip: String)
data class ClinicalReadingKey(val title: String, val text: String, val sourceId: String)

object ClinicalVisualLearning {
    val general = ClinicalAreaArt(
        "escalas_art/escala_general.png",
        "Portapapeles con lupa y libro",
        "Una ficha describe propósito, población, versión, límites y fuentes.",
    )
    val areas: Map<String, ClinicalAreaArt> = linkedMapOf(
        "Neurología" to ClinicalAreaArt("escalas_art/escala_neuro.png", "Nube cerebral simbólica",
            "Cada instrumento describe aspectos distintos de la respuesta o de las funciones neurológicas."),
        "Dolor" to ClinicalAreaArt("escalas_art/escala_dolor.png", "Burbuja de comunicación y corazón",
            "Autoinforme y observación son métodos distintos; la carita de la burbuja no representa dolor."),
        "Delirium y sedación" to ClinicalAreaArt("escalas_art/escala_neuro.png", "Nube cerebral simbólica",
            "El cribado de delirium y la descripción de sedación no son lo mismo; un número aislado no es un diagnóstico."),
        "Piel y caídas" to ClinicalAreaArt("escalas_art/escala_seguridad.png", "Escudo de cuidado y apósito decorativo",
            "El riesgo de lesión por presión y el riesgo de caídas son objetivos diferentes."),
        "Neonatología" to ClinicalAreaArt("escalas_art/escala_neonatal.png", "Gorrito y manta",
            "Las herramientas neonatales describen aspectos y momentos diferentes; sus resultados no son intercambiables."),
        "Deterioro clínico" to ClinicalAreaArt("escalas_art/escala_seguridad.png", "Escudo simbólico de seguridad",
            "Las herramientas de alerta dependen de la edad, el entorno y la versión; no traslades umbrales entre poblaciones."),
        "Nutrición" to ClinicalAreaArt("escalas_art/escala_nutricion.png", "Cuenco, cuchara y manzana",
            "Las fichas describen instrumentos de cribado; no prescriben dietas ni tratamientos."),
        "Funcionalidad" to ClinicalAreaArt("escalas_art/escala_funcion.png", "Zapatilla y estrella",
            "La autonomía cotidiana y la movilidad son aspectos diferentes; revisa qué explora cada instrumento."),
        "Cognición y salud mental" to ClinicalAreaArt("escalas_art/escala_neuro.png", "Nube de pensamiento simbólica",
            "Cognición, ánimo y ansiedad son ámbitos distintos; un cribado no equivale por sí solo a un diagnóstico."),
    )

    /** Concise original explanations, each tied to a cited source already in that fact sheet. */
    val keys: Map<String, ClinicalReadingKey> = mapOf(
        "glasgow" to ClinicalReadingKey("Componentes y datos no valorables",
            "Se describen las respuestas ocular, verbal y motora por separado. Si un componente no puede valorarse, el FAQ oficial indica registrarlo como no valorable y no informar un total calculado con ese componente.", "gcs"),
        "glasgow-pediatrico" to ClinicalReadingKey("La edad y la versión importan",
            "La referencia oficial explica que en niños pequeños pueden emplearse respuestas adaptadas al desarrollo. Esta pantalla solo transcribe componentes de una ficha externa identificada.", "gcs"),
        "eva" to ClinicalReadingKey("Una línea no es una cifra seleccionada",
            "La EVA de referencia usa una línea continua; la escala numérica pide un número. La simulación de esta app no está calibrada en milímetros ni se ha validado como EVA clínica.", "iasp"),
        "numerica" to ClinicalReadingKey("Autoinforme, no expresión facial",
            "En la escala numérica se registra una cifra comunicada por la persona. Si no puede autoinformar, en este ejercicio se deja sin puntaje; no se deduce de su cara.", "iasp"),
        "fps-r" to ClinicalReadingKey("Las caritas decorativas no son la escala",
            "La FPS-R es un autoinforme infantil, no una lectura de la expresión de la persona. La IASP exige no modificar la escala y gestionar las condiciones de su versión digital; aquí no se reproducen sus caras.", "fps"),
        "wong-baker" to ClinicalReadingKey("No sustituir los estímulos",
            "La escala de caras es de autoinforme: el niño elige la imagen que comunica su experiencia. Las mascotas de esta app no son sus caras oficiales ni sus opciones de respuesta.", "rch-pain"),
        "4at" to ClinicalReadingKey("No valorable no significa siempre lo mismo",
            "La guía 4AT asigna 2 puntos a AMT4 o atención cuando cada uno es no valorable según sus criterios; no es la misma regla que Glasgow. El 4AT es cribado y su resultado por sí solo no es diagnóstico.", "4at-guide"),
        "apgar" to ClinicalReadingKey("Qué describe y qué no predice",
            "Apgar describe el estado del recién nacido en un momento tras el nacimiento. Por sí solo no predice el resultado neurológico individual y no debe retrasar la atención necesaria.", "apgar"),
        "rass" to ClinicalReadingKey("Nivel, no suma",
            "RASS describe niveles de agitación y sedación. Aquí se transcribe un valor externo ficticio de adulto; la app no administra los criterios ni propone objetivos de sedación.", "rass-overview"),
        "norton" to ClinicalReadingKey("Registro no es administración",
            "Norton trata el riesgo de lesión por presión. Aquí solo se transcribe un total ficticio de la versión original; una cifra no sustituye la valoración clínica y los umbrales dependen del contexto.", "norton-scire"),
        "news2" to ClinicalReadingKey("No trasladar a otra población",
            "El informe RCP 2017 no recomienda NEWS2 en menores de 16 años ni durante el embarazo. Esta ficha es documental: no incluye ni calcula la tabla oficial.", "news-report"),
        "pews" to ClinicalReadingKey("La versión depende de edad y entorno",
            "NHS England publica tablas pediátricas diferenciadas por edad y por contexto de atención. Son referencias británicas, no un protocolo peruano; esta app no las reproduce ni las calcula.", "pews"),
    )

    fun area(name: String): ClinicalAreaArt {
        areas[name]?.let { return it }
        val key = when {
            listOf("neurolog", "neurocir", "ictus", "hemorragia intracerebral", "ataque isqu").any { name.contains(it, true) } -> "Neurología"
            listOf("dolor", "analges", "paliat").any { name.contains(it, true) } -> "Dolor"
            listOf("neonat", "obstetric", "puerper", "abstinencia neonatal", "recién nacido").any { name.contains(it, true) } -> "Neonatología"
            listOf("piel", "caída", "herida", "quemad", "ostom", "presión").any { name.contains(it, true) } -> "Piel y caídas"
            listOf("nutri", "malnutri", "deshidrat").any { name.contains(it, true) } -> "Nutrición"
            listOf("funcion", "rehab", "frail", "fragilidad", "movilidad", "discapacidad").any { name.contains(it, true) } -> "Funcionalidad"
            listOf("salud mental", "psiquiatr", "sedación", "delirium", "ansiedad", "depres").any { name.contains(it, true) } -> "Cognición y salud mental"
            else -> null
        }
        return key?.let { areas[it] } ?: general
    }
}

object ClinicalIllustrationMotion {
    fun allow(reduced: Boolean, paused: Boolean): Boolean = !reduced && !paused
}

/**
 * Only shown in information cards, never inside a standardized response/assessment control.
 * A PNG does not animate itself: this optional translation is native Compose decoration.
 */
@Composable
fun ClinicalAreaIllustration(art: ClinicalAreaArt, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap = remember(art.asset) {
        runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.assets.open(art.asset).use { BitmapFactory.decodeStream(it, null, bounds) }
            require(bounds.outWidth == 960 && bounds.outHeight == 600)
            context.assets.open(art.asset).use {
                requireNotNull(BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = 2 }))
            }.asImageBitmap()
        }.getOrNull()
    }
    var paused by rememberSaveable(art.asset) { mutableStateOf(false) }
    val reduced = LocalReduced.current
    val moving = bitmap != null && ClinicalIllustrationMotion.allow(reduced, paused)
    val drift = if (moving) {
        val transition = rememberInfiniteTransition(label = "Ilustración de apoyo")
        transition.animateFloat(-1.5f, 1.5f,
            infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "Movimiento decorativo").value
    } else 0f
    if (bitmap == null) {
        Text("Ilustración de apoyo no disponible. La ficha y sus fuentes siguen accesibles.",
            modifier.testTag("escala-arte-pendiente"), style = MaterialTheme.typography.bodySmall)
        return
    }
    Box(modifier.fillMaxWidth().height(130.dp).testTag("escala-arte"), contentAlignment = Alignment.Center) {
        Image(bitmap, "${art.description}. Dibujo decorativo; no es una respuesta ni un instrumento clínico.",
            Modifier.fillMaxHeight().aspectRatio(1.6f).graphicsLayer { translationY = drift.dp.toPx() },
            contentScale = ContentScale.Fit)
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("Dibujo de apoyo; no es una escala ni representa un resultado.",
            Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = LocalPalette.current.muted)
        TextButton(onClick = { paused = !paused }, enabled = !reduced,
            modifier = Modifier.heightIn(min = 48.dp).testTag("escala-movimiento")) {
            Text(when { reduced -> "Quieto"; paused -> "Animar dibujo"; else -> "Pausar dibujo" })
        }
    }
    if (reduced) Text("Movimiento reducido: ilustración quieta.", style = MaterialTheme.typography.labelSmall,
        color = LocalPalette.current.muted)
}
