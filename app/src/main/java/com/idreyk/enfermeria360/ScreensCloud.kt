package com.idreyk.enfermeria360

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.text.DateFormat
import java.util.Date
import kotlinx.coroutines.delay

@Composable
fun CloudScreen(vm: AppViewModel, nav: (String) -> Unit, back: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var consent by remember { mutableStateOf(false) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(vm.nextCodeAt) {
        now = System.currentTimeMillis()
        while (now < vm.nextCodeAt) {
            delay(1000)
            now = System.currentTimeMillis()
        }
    }
    LaunchedEffect(vm.cloudSession) { if (vm.cloudSession != null) code = "" }
    val ready = vm.cloudConfig.configured
    val enabled = ready && !vm.busy
    Page("Cuenta y actualizaciones", "Lectura local · conexión opcional", back) {
        AppCard(Modifier.fillMaxWidth()) {
            Badge(
                if (ready) "PROYECTO CONFIGURADO" else "NUBE SIN CONFIGURAR",
                if (ready) Teal else Gold,
            )
            Text(
                if (ready) vm.cloudConfig.url
                else "Las funciones online aún no están habilitadas en esta compilación.",
                style = MaterialTheme.typography.bodySmall,
            )
            Text(vm.cloudStatus, style = MaterialTheme.typography.bodyMedium)
            if (vm.busy) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                TextButton(onClick = vm::cancelCloud) { Text("Cancelar conexión") }
            }
        }
        if (!ready)
            Notice(
                "El responsable del proyecto debe configurar Supabase, sus permisos y el envío de correos, y compilar con URL y clave publicable. No necesitas una cuenta para usar los módulos locales."
            )
        Section("Biblioteca de medicamentos")
        Text(
            "Consultar novedades no exige iniciar sesión. Las descargas se validan y se muestran antes de confirmar: no se sobrescriben tus fichas silenciosamente.",
            style = MaterialTheme.typography.bodySmall,
        )
        if (vm.lastCloudCheck > 0)
            Text(
                "Última consulta: ${DateFormat.getDateTimeInstance().format(Date(vm.lastCloudCheck))}",
                style = MaterialTheme.typography.bodySmall,
            )
        Primary(
            "Buscar actualización de contenido",
            vm::checkCloudCatalog,
            enabled,
            R.drawable.ic_download,
        )
        Secondary("Importar o respaldar archivo local", { nav("contenido") }, !vm.busy)
        Notice(
            "Los paquetes actuales tienen límites de 2 MB y 500 registros agregados. La actualización añade o reemplaza IDs confirmados; no retira automáticamente fichas omitidas. Una revisión declarada no certifica su uso clínico.",
            true,
        )
        Section("Tu cuenta opcional")
        Text(
            "El cuaderno y los archivos pertenecen a esta instalación. Iniciar o cerrar sesión no los sube, borra ni separa por cuenta.",
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
        val session = vm.cloudSession
        if (session == null) {
            Text(
                "Usaremos un código enviado a tu correo, sin guardar contraseñas. El mismo flujo crea la cuenta o accede a una existente. Solicitar un código no verifica tu identidad.",
                style = MaterialTheme.typography.bodySmall,
            )
            Input("Correo electrónico", email, { email = it.take(254) }, enabled = enabled)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(consent, { consent = it }, enabled = enabled)
                Text(
                    "Acepto enviar mi correo al proyecto mostrado para crear o acceder a mi cuenta. No se envían datos de pacientes.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                )
            }
            val seconds = ((vm.nextCodeAt - now).coerceAtLeast(0) + 999) / 1000
            Secondary(
                if (seconds > 0) "Volver a solicitar en ${seconds}s" else "Enviar código de acceso",
                { vm.sendCloudCode(email) },
                enabled && consent && seconds == 0L && email.isNotBlank(),
            )
            Input(
                "Código del correo",
                code,
                { code = it.filter { c -> c in '0'..'9' }.take(10) },
                numeric = true,
                enabled = enabled,
            )
            Primary(
                "Verificar código",
                { vm.verifyCloudCode(email, code) },
                enabled && consent && code.length >= 6 && email.isNotBlank(),
            )
        } else {
            AppCard(Modifier.fillMaxWidth()) {
                Text(session.email, style = MaterialTheme.typography.titleMedium)
                Text(
                    "Sesión guardada en este dispositivo. Sin conexión no se puede confirmar su vigencia en el servidor.",
                    style = MaterialTheme.typography.bodySmall,
                )
                Secondary("Validar sesión con el servidor", vm::refreshCloudSession, enabled)
                Secondary("Cerrar sesión en este dispositivo", vm::signOutCloud, !vm.busy)
            }
        }
        Notice(
            "Los tokens se guardan cifrados mediante Android Keystore. No se sincronizan notas, planes ni archivos personales. No introduzcas datos reales de pacientes. Para eliminar una cuenta del servidor hay que solicitarlo al responsable; cerrar sesión no la elimina."
        )
    }
}
