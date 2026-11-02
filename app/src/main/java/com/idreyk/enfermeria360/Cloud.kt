package com.idreyk.enfermeria360

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URI
import java.net.URL
import java.security.MessageDigest
import java.util.UUID
import javax.net.ssl.HttpsURLConnection
import org.json.JSONArray
import org.json.JSONObject

data class CloudConfig(val url: String, val publishableKey: String) {
    val configured: Boolean
        get() = url.isNotBlank() && publishableKey.isNotBlank()

    fun validate() {
        require(configured) {
            "Proyecto Supabase sin configurar. La biblioteca local sigue disponible."
        }
        val uri = URI(url)
        require(
            uri.scheme == "https" &&
                uri.host?.matches(Regex("[a-z0-9-]+\\.supabase\\.co")) == true &&
                uri.rawUserInfo == null &&
                uri.port == -1 &&
                uri.rawQuery == null &&
                uri.rawFragment == null &&
                uri.path.isNullOrEmpty()
        ) {
            "Se requiere la URL HTTPS del proyecto Supabase, sin rutas ni credenciales."
        }
        require(publishableKey.matches(Regex("sb_publishable_[A-Za-z0-9_-]{8,500}"))) {
            "Usa solo una clave publicable. Las claves secretas nunca pertenecen al APK."
        }
    }
}

class CloudFailure(val status: Int) :
    IOException(
        when (status) {
            401,
            403 -> "Solicitud no autorizada. Revisa la configuración o vuelve a validar tu sesión."
            429 -> "Límite de solicitudes alcanzado. Espera antes de pedir otro código."
            400,
            422 -> "Solicitud rechazada. Revisa el correo, el código y su vigencia."
            else ->
                "El servicio no completó la solicitud (HTTP $status). No cambió tu biblioteca local."
        }
    )

interface CloudTransport {
    fun request(
        config: CloudConfig,
        path: String,
        body: JSONObject?,
        token: String?,
        maximum: Int,
    ): String

    fun cancel() {}
}

class HttpsCloudTransport : CloudTransport {
    @Volatile private var active: HttpsURLConnection? = null

    override fun cancel() {
        active?.disconnect()
    }

    override fun request(
        config: CloudConfig,
        path: String,
        body: JSONObject?,
        token: String?,
        maximum: Int,
    ): String {
        config.validate()
        require(path.startsWith("/") && !path.startsWith("//"))
        val connection = URL(config.url + path).openConnection() as HttpsURLConnection
        active = connection
        try {
            connection.connectTimeout = 12000
            connection.readTimeout = 12000
            connection.instanceFollowRedirects = false
            connection.useCaches = false
            connection.setRequestProperty("apikey", config.publishableKey)
            connection.setRequestProperty("Accept", "application/json")
            if (token != null) connection.setRequestProperty("Authorization", "Bearer $token")
            if (body != null) {
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                val bytes = body.toString().toByteArray(Charsets.UTF_8)
                connection.setFixedLengthStreamingMode(bytes.size)
                connection.outputStream.use { it.write(bytes) }
            }
            val status = connection.responseCode
            // Never forward keys to redirects or include server bodies/tokens in errors/logs.
            if (status !in 200..299) throw CloudFailure(status)
            val declared = connection.contentLengthLong
            require(declared <= maximum) {
                "Respuesta demasiado grande; no se modificó la biblioteca."
            }
            val output = ByteArrayOutputStream()
            val deadline = System.nanoTime() + 30_000_000_000L
            connection.inputStream.use { stream ->
                val buffer = ByteArray(8192)
                while (true) {
                    if (Thread.currentThread().isInterrupted || System.nanoTime() > deadline)
                        throw IOException("La descarga se canceló o agotó su tiempo.")
                    val count = stream.read(buffer)
                    if (count < 0) break
                    require(output.size() + count <= maximum) {
                        "Respuesta demasiado grande; no se modificó la biblioteca."
                    }
                    output.write(buffer, 0, count)
                }
            }
            return Charsets.UTF_8.newDecoder()
                .decode(java.nio.ByteBuffer.wrap(output.toByteArray()))
                .toString()
        } finally {
            connection.disconnect()
            active = null
        }
    }
}

data class CloudSession(
    val userId: String,
    val email: String,
    val access: String,
    val refresh: String,
    val expiresAt: Long,
) {
    override fun toString(): String = "CloudSession(redacted)"

    fun json(): JSONObject =
        JSONObject()
            .put("userId", userId)
            .put("email", email)
            .put("access", access)
            .put("refresh", refresh)
            .put("expiresAt", expiresAt)

    companion object {
        fun stored(json: JSONObject): CloudSession =
            CloudSession(
                json.getString("userId"),
                json.getString("email"),
                json.getString("access"),
                json.getString("refresh"),
                json.getLong("expiresAt"),
            )

        fun fromResponse(raw: String, now: Long = System.currentTimeMillis() / 1000): CloudSession {
            JsonSafety.requireBoundedDepth(raw)
            val root = JSONObject(raw)
            val user = root.getJSONObject("user")
            val id = user.getString("id").also { UUID.fromString(it) }
            val email = user.getString("email").also { CloudProtocol.validateEmail(it) }
            val access = root.getString("access_token")
            val refresh = root.getString("refresh_token")
            require(access.length in 16..16000 && refresh.length in 8..4096) {
                "Respuesta de sesión inválida."
            }
            val expiresIn = root.getLong("expires_in")
            require(expiresIn in 1..86400) { "Vigencia de sesión no admitida." }
            return CloudSession(id, email, access, refresh, now + expiresIn)
        }
    }
}

object CloudProtocol {
    fun validateEmail(value: String) {
        require(value.length in 3..254 && value.matches(Regex("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"))) {
            "Escribe un correo válido."
        }
    }

    fun hash(raw: String): String =
        MessageDigest.getInstance("SHA-256").digest(raw.toByteArray(Charsets.UTF_8)).joinToString(
            ""
        ) {
            "%02x".format(it.toInt() and 255)
        }

    fun release(raw: String, appVersion: Int): String? {
        JsonSafety.requireBoundedDepth(raw)
        val array = JSONArray(raw)
        require(array.length() <= 1) { "Respuesta de catálogo inválida." }
        if (array.length() == 0) return null
        val row = array.getJSONObject(0)
        require(row.getInt("min_app_version") <= appVersion) {
            "Esta publicación requiere actualizar el APK antes de importar su contenido."
        }
        val payload = row.getString("package_json")
        val bytes = payload.toByteArray(Charsets.UTF_8)
        require(bytes.size <= ContentParser.MAX_BYTES) {
            "La publicación supera el límite de 2 MB."
        }
        val expected = row.getString("sha256")
        require(
            expected.matches(Regex("[a-f0-9]{64}")) &&
                MessageDigest.isEqual(hash(payload).toByteArray(), expected.toByteArray())
        ) {
            "La integridad del paquete no coincide. No se guardó ningún contenido."
        }
        val pack = ContentParser.parse(payload)
        require(
            pack.id == "catalogo-oficial" &&
                pack.version == row.getLong("version") &&
                !pack.isBackup
        ) {
            "La identidad o versión de la publicación no coincide."
        }
        require(
            pack.entries.isNotEmpty() && pack.entries.all { it.revision == "declarada_por_autor" }
        ) {
            "El canal publicado no admite borradores. La revisión declarada no sustituye la validación clínica."
        }
        return payload
    }
}

class CloudService(
    val config: CloudConfig,
    private val transport: CloudTransport = HttpsCloudTransport(),
) {
    fun cancel() = transport.cancel()

    fun sendCode(email: String) {
        config.validate()
        CloudProtocol.validateEmail(email)
        transport.request(
            config,
            "/auth/v1/otp",
            JSONObject().put("email", email).put("create_user", true),
            null,
            65536,
        )
    }

    fun verify(email: String, code: String): CloudSession {
        config.validate()
        CloudProtocol.validateEmail(email)
        require(code.matches(Regex("[0-9]{6,10}"))) { "Introduce el código recibido por correo." }
        return CloudSession.fromResponse(
            transport.request(
                config,
                "/auth/v1/verify",
                JSONObject().put("email", email).put("token", code).put("type", "email"),
                null,
                65536,
            )
        )
    }

    fun refresh(session: CloudSession): CloudSession {
        val result =
            CloudSession.fromResponse(
                transport.request(
                    config,
                    "/auth/v1/token?grant_type=refresh_token",
                    JSONObject().put("refresh_token", session.refresh),
                    null,
                    65536,
                )
            )
        require(result.userId == session.userId) {
            "La identidad de la sesión cambió inesperadamente."
        }
        return result
    }

    fun logout(session: CloudSession) {
        transport.request(
            config,
            "/auth/v1/logout?scope=local",
            JSONObject(),
            session.access,
            65536,
        )
    }

    fun latest(): String? {
        config.validate()
        val raw =
            transport.request(
                config,
                "/rest/v1/catalog_releases?select=version,min_app_version,sha256,package_json&published=eq.true&order=version.desc&limit=1",
                null,
                null,
                14_000_000,
            )
        return CloudProtocol.release(raw, BuildConfig.VERSION_CODE)
    }
}
