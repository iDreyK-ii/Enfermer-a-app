package com.idreyk.enfermeria360

import java.net.URI

/** Opening a reference is an explicit user action, never an automatic in-app fetch. */
object ExternalReferencePolicy {
    fun canOpen(url: String): Boolean {
        if (url.isBlank() || url.length > 2000 || url.any { it.isWhitespace() || it.isISOControl() }) return false
        return try {
            val uri = URI(url)
            uri.scheme.equals("https", ignoreCase = true) && !uri.host.isNullOrBlank() && uri.userInfo == null
        } catch (_: IllegalArgumentException) {
            false
        } catch (_: java.net.URISyntaxException) {
            false
        }
    }
}
