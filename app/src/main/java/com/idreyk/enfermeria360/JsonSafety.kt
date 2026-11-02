package com.idreyk.enfermeria360

/** Preflight only: the actual codec still validates types, keys, lengths and structure. */
object JsonSafety {
    fun requireBoundedDepth(raw: String, maximum: Int = 32) {
        var depth = 0
        var quoted = false
        var escaped = false
        raw.forEach { character ->
            if (quoted) {
                when {
                    escaped -> escaped = false
                    character == '\\' -> escaped = true
                    character == '"' -> quoted = false
                }
            } else {
                when (character) {
                    '"' -> quoted = true
                    '{',
                    '[' -> {
                        depth++
                        require(depth <= maximum) {
                            "El JSON está demasiado anidado. Usa la plantilla del proyecto."
                        }
                    }
                    '}',
                    ']' -> {
                        depth--
                        require(depth >= 0) { "El JSON tiene delimitadores sin correspondencia." }
                    }
                }
            }
        }
        require(depth == 0 && !quoted) {
            "El JSON está incompleto o tiene delimitadores sin cerrar."
        }
    }
}
