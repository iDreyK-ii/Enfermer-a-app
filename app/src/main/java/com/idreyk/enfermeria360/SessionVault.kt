package com.idreyk.enfermeria360

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import org.json.JSONObject

object SessionCipher {
    fun encrypt(plain: String, key: SecretKey): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encrypted = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(cipher.iv) +
            ":" +
            Base64.getEncoder().encodeToString(encrypted)
    }

    fun decrypt(blob: String, key: SecretKey): String {
        val parts = blob.split(':')
        require(parts.size == 2 && blob.length < 100000) { "Sesión almacenada inválida." }
        val iv = Base64.getDecoder().decode(parts[0])
        require(iv.size == 12) { "Sesión almacenada inválida." }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        return String(cipher.doFinal(Base64.getDecoder().decode(parts[1])), Charsets.UTF_8)
    }
}

/** Only encrypted session material is persisted. Failure never falls back to plaintext. */
class SessionVault(context: Context) {
    private val preferences = context.getSharedPreferences("cloud_auth", Context.MODE_PRIVATE)
    private val alias = "idreyk_cloud_session_v1"

    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(alias, null) as? SecretKey)?.let {
            return it
        }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            .apply {
                init(
                    KeyGenParameterSpec.Builder(
                            alias,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                        )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(256)
                        .build()
                )
            }
            .generateKey()
    }

    fun load(project: String): CloudSession? {
        val stored = preferences.getString("sealed", null) ?: return null
        if (preferences.getString("project", null) != project || project.isBlank()) {
            clear()
            return null
        }
        return try {
            CloudSession.stored(JSONObject(SessionCipher.decrypt(stored, key())))
        } catch (error: Exception) {
            clear()
            throw IllegalStateException(
                "No se pudo recuperar la sesión protegida; vuelve a iniciar sesión."
            )
        }
    }

    fun save(project: String, session: CloudSession) {
        val encrypted = SessionCipher.encrypt(session.json().toString(), key())
        check(
            preferences.edit().putString("project", project).putString("sealed", encrypted).commit()
        ) {
            "No se pudo guardar la sesión protegida."
        }
    }

    fun clear() {
        check(preferences.edit().clear().commit()) { "No se pudo eliminar la sesión local." }
    }
}
