package com.idreyk.enfermeria360

/** Structure supplied by the user; labels are not clinical instructions. */
object PharmacologyLayout {
    val technical =
        linkedMapOf(
            "nombre" to "Nombre genérico",
            "nombres_comerciales" to "Nombre comercial",
            "presentaciones" to "Presentaciones",
            "mecanismo_accion" to "Mecanismo de acción",
            "indicaciones" to "Indicaciones",
            "almacenamiento" to "Almacenamiento",
            "formas_presentacion" to "Formas de presentación",
            "reconstitucion" to "Reconstitución",
            "dosificacion" to "Dosis · requiere revisión",
            "dilucion" to "Dilución",
            "vias" to "Vía de administración",
            "velocidad_administracion" to "Velocidad de administración",
            "contraindicaciones" to "Contraindicaciones",
            "estabilidad" to "Estabilidad de solución",
            "sueros_compatibles" to "Sueros compatibles",
            "incompatibilidades" to "Incompatibilidades",
            "reacciones_adversas" to "Efectos adversos / observaciones / RAM",
        )
    val essentials =
        linkedMapOf(
            "Objetivo del tratamiento" to listOf("objetivo_terapeutico"),
            "Antes de administrar" to listOf("antes_administrar"),
            "Preparación y administración" to
                listOf("reconstitucion", "dilucion", "administracion", "velocidad_administracion"),
            "Vigilancia y alertas" to
                listOf(
                    "durante_administracion",
                    "despues_administracion",
                    "monitorizacion",
                    "alertas",
                ),
        )
    val nursingFields =
        listOf(
            "objetivo_terapeutico",
            "antes_administrar",
            "durante_administracion",
            "despues_administracion",
            "monitorizacion",
            "cuidados_enfermeria",
            "alertas",
        )

    fun editorGroups(): List<Pair<String, List<String>>> {
        val technicalKeys = technical.keys.filter { it != "nombre" }
        return listOf(
            "Enfermería" to nursingFields,
            "PDF · 17 conceptos" to technicalKeys,
            "Otros datos" to
                DRUG_FIELDS.keys.filter { it !in technicalKeys && it !in nursingFields },
        )
    }

    fun text(entry: Entry, key: String): String =
        if (key == "nombre") entry.name else entry.sections[key].orEmpty()

    fun summary(entry: Entry, keys: List<String>): String =
        keys
            .mapNotNull { key ->
                text(entry, key)
                    .takeIf { it.isNotBlank() }
                    ?.let { "${DRUG_FIELDS.getValue(key)}\n$it" }
            }
            .joinToString("\n\n")
}
