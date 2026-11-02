package com.idreyk.enfermeria360

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun CatalogImportSheet(
    preview: ImportPreview,
    busy: Boolean,
    confirm: (Boolean) -> Unit,
    cancel: () -> Unit,
) {
    var acknowledged by rememberSaveable(preview.fingerprint) { mutableStateOf(false) }
    var page by rememberSaveable(preview.fingerprint) { mutableIntStateOf(0) }
    val pageSize = 12
    val pageCount = ((preview.changes.size + pageSize - 1) / pageSize).coerceAtLeast(1)
    val scroll = rememberScrollState()
    val scope = rememberCoroutineScope()
    AppSheet("Revisa antes de importar", cancel, scrollState = scroll) {
        Badge(
            "CONTENIDO · ESQUEMA ${JSONObject(preview.content.raw).getInt("version_esquema")}",
            Teal,
        )
        Text(preview.content.title, style = MaterialTheme.typography.titleLarge)
        Text(
            "Autor: ${preview.content.author}\nVersión: ${preview.content.version}\nActualización declarada: ${preview.content.updated}",
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
        Text(
            "${preview.newCount} registros nuevos\n${preview.updateCount} registros que se reemplazarán",
            style = MaterialTheme.typography.titleMedium,
        )
        val localEdits = preview.changes.count { it.replacesLocalEdit }
        val cleared = preview.changes.sumOf { it.clearedFields.size }
        if (localEdits > 0 || cleared > 0)
            Notice(
                "$localEdits fichas con edición local. $cleared campos con contenido quedarán vacíos o ausentes. No se mezclan los campos automáticamente: se sustituye la ficha confirmada.",
                true,
            )
        preview.changes.drop(page * pageSize).take(pageSize).forEach { change ->
            AppCard(Modifier.fillMaxWidth()) {
                Badge(
                    if (change.isReplacement) "REEMPLAZO" else "NUEVO",
                    if (change.isReplacement) Gold else Teal,
                )
                Text(change.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    change.key,
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalPalette.current.muted,
                )
                if (change.previousName != null && change.previousName != change.name)
                    Text(
                        "Antes: ${change.previousName}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                if (change.replacesLocalEdit)
                    Text(
                        "Sustituye una edición hecha en este dispositivo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                if (change.isReplacement) {
                    Text(
                        if (change.changedFields.isEmpty())
                            "Campos sin cambios; se actualizarán versión y procedencia."
                        else "Cambian: ${change.changedFields.joinToString()}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    if (change.clearedFields.isNotEmpty())
                        Text(
                            "Quedarán pendientes: ${change.clearedFields.joinToString()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                }
            }
        }
        if (pageCount > 1)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    {
                        page--
                        scope.launch { scroll.scrollTo(0) }
                    },
                    enabled = !busy && page > 0,
                ) {
                    Text("Anterior")
                }
                Text("${page + 1} / $pageCount", style = MaterialTheme.typography.labelMedium)
                TextButton(
                    {
                        page++
                        scope.launch { scroll.scrollTo(0) }
                    },
                    enabled = !busy && page + 1 < pageCount,
                ) {
                    Text("Siguiente")
                }
            }
        Notice(
            "Esta comparación detecta cambios estructurales, no valida su exactitud clínica ni sus derechos. No incluyas datos reales de pacientes.",
            true,
        )
        Text(
            "Se conservarán los IDs ausentes del paquete y un estado anterior para deshacer. Si el catálogo cambia después de esta revisión, tendrás que revisar de nuevo.",
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
        if (preview.updateCount > 0)
            Row(
                Modifier.fillMaxWidth()
                    .testTag("import-replace-ack")
                    .toggleable(
                        acknowledged,
                        enabled = !busy,
                        role = Role.Checkbox,
                        onValueChange = { acknowledged = it },
                    )
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(acknowledged, onCheckedChange = null, enabled = !busy)
                Text(
                    "He revisado las sustituciones y los campos que quedarán pendientes.",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        Primary(
            "Confirmar importación local",
            { confirm(acknowledged) },
            !busy && (preview.updateCount == 0 || acknowledged),
            R.drawable.ic_check,
        )
        Secondary("Cancelar", cancel, !busy)
    }
}
