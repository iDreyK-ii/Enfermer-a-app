package com.idreyk.enfermeria360

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

data class DilutionExercise(val stockMl: Double, val additionalMl: Double, val finalMl: Double)

/** Dimensional arithmetic only. Not a prescription, dose selector or compatibility engine. */
object DoseMath {
    val amountUnits = listOf("mg", "g", "mcg", "UI")
    val concentrationUnits = listOf("mg/mL", "mcg/mL", "g/L", "UI/mL")
    private val massMg = mapOf("mg" to 1.0, "g" to 1000.0, "mcg" to 0.001)
    private val concentrationMgMl = mapOf("mg/mL" to 1.0, "mcg/mL" to 0.001, "g/L" to 1.0)

    fun number(text: String): Double =
        text.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() }
            ?: throw IllegalArgumentException(
                "Introduce números finitos; usa coma o punto decimal sin separadores de miles."
            )

    private fun positive(value: Double) {
        require(value.isFinite() && value > 0 && value <= 1e9) {
            "Solo se admiten valores positivos finitos hasta 1 000 000 000; es un límite numérico, no clínico."
        }
    }

    private fun checked(value: Double): Double {
        require(value.isFinite() && value > 0 && value <= 1e12) {
            "Resultado fuera del rango numérico admitido."
        }
        return value
    }

    fun volume(
        requested: Double,
        requestedUnit: String,
        available: Double,
        availableUnit: String,
        availableMl: Double,
    ): Double {
        listOf(requested, available, availableMl).forEach(::positive)
        require(requestedUnit in amountUnits && availableUnit in amountUnits) {
            "Unidad no reconocida."
        }
        require((requestedUnit == "UI") == (availableUnit == "UI")) {
            "No se convierten UI a unidades de masa ni viceversa."
        }
        val a = checked(requested * (massMg[requestedUnit] ?: 1.0))
        val b = checked(available * (massMg[availableUnit] ?: 1.0))
        return checked(a / b * availableMl)
    }

    fun dilution(
        stock: Double,
        stockUnit: String,
        desired: Double,
        desiredUnit: String,
        finalMl: Double,
    ): DilutionExercise {
        listOf(stock, desired, finalMl).forEach(::positive)
        require(stockUnit in concentrationUnits && desiredUnit in concentrationUnits) {
            "Unidad de concentración no reconocida."
        }
        require((stockUnit == "UI/mL") == (desiredUnit == "UI/mL")) {
            "No se mezclan concentraciones en UI y masa."
        }
        val c1 = checked(stock * (concentrationMgMl[stockUnit] ?: 1.0))
        val c2 = checked(desired * (concentrationMgMl[desiredUnit] ?: 1.0))
        require(c2 <= c1) {
            "La concentración final no puede superar la inicial en un ejercicio de dilución."
        }
        val v1 = checked(c2 / c1 * finalMl)
        return DilutionExercise(v1, (finalMl - v1).coerceAtLeast(0.0), finalMl)
    }

    fun display(value: Double): String {
        require(value.isFinite())
        val pattern =
            if (value != 0.0 && (abs(value) < 0.00001 || abs(value) >= 1e7)) "0.######E0"
            else "0.######"
        return DecimalFormat(pattern, DecimalFormatSymbols(Locale.forLanguageTag("es-PE")))
            .format(value)
    }
}
