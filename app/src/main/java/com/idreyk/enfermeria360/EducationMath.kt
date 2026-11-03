package com.idreyk.enfermeria360

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/** Decimal, dimensional arithmetic for fictitious exercises only. Never a dose recommendation. */
data class EducationResult(
    val formula: String,
    val substitution: String,
    val value: BigDecimal,
    val unit: String,
    val explanation: String = "",
    val whole: Boolean = false,
) {
    val presented: String get() = EducationMath.display(value, whole)
    val summary: String get() =
        "EJERCICIO EDUCATIVO\n$formula\n$substitution\nResultado: $presented $unit\n" +
            "Valor decimal: ${value.stripTrailingZeros().toEngineeringString()} $unit\n" +
            (if (whole) "Redondeo de presentación: entero, mitades hacia arriba." else
                "Redondeo de presentación: hasta 6 decimales; cantidades diminutas se muestran en notación científica, no como cero.") +
            " División final: hasta 34 cifras significativas. No se redondean cantidades de entrada ni productos intermedios. $explanation\nNo autoriza administración."
}

object EducationMath {
    private val limit = BigDecimal("1000000000")
    private val resultLimit = BigDecimal("1000000000000")
    private val context = MathContext.DECIMAL128
    val amounts = listOf("mg", "g", "mcg", "UI", "mEq")
    val concentrations = listOf("mg/mL", "g/mL", "mcg/mL", "g/L", "UI/mL", "mEq/mL", "% m/v", "% v/v")
    val conversions = listOf("mcg", "mg", "g", "mL", "L", "min", "h", "UI", "mEq")

    /** No exponents, grouping separators, NaN, Infinity, negatives or silent truncation. */
    fun number(raw: String, zero: Boolean = false): BigDecimal {
        val text = raw.trim()
        require(text.length in 1..32 && Regex("(?:[0-9]+(?:[.,][0-9]+)?|[.,][0-9]+)").matches(text)) {
            "Completa el campo con un decimal simple; usa coma o punto, sin miles ni exponentes."
        }
        val value = text.replace(',', '.').toBigDecimal()
        require(value.precision() <= 18 && value.scale() <= 9 && value <= limit &&
            (if (zero) value >= BigDecimal.ZERO else value > BigDecimal.ZERO)) {
            "Valor fuera del rango técnico del ejercicio: ${if (zero) "0" else "mayor que 0"} a 1 000 000 000, hasta 9 decimales y 18 cifras. No es un rango clínico seguro."
        }
        return value
    }

    private fun ratio(top: BigDecimal, bottom: BigDecimal): BigDecimal {
        require(bottom > BigDecimal.ZERO) { "No se divide entre cero." }
        return top.divide(bottom, context).also {
            require(it.abs() <= resultLimit) { "El resultado excede el límite técnico del ejercicio." }
        }
    }

    private fun unit(name: String): Pair<String, BigDecimal> = when (name) {
        "mcg" -> "masa" to BigDecimal("0.001")
        "mg" -> "masa" to BigDecimal.ONE
        "g" -> "masa" to BigDecimal("1000")
        "mL" -> "volumen" to BigDecimal.ONE
        "L" -> "volumen" to BigDecimal("1000")
        "min" -> "tiempo" to BigDecimal.ONE
        "h" -> "tiempo" to BigDecimal("60")
        "UI" -> "actividad" to BigDecimal.ONE
        "mEq" -> "equivalentes" to BigDecimal.ONE
        else -> throw IllegalArgumentException("Unidad no admitida.")
    }

    private fun normalizedAmount(value: BigDecimal, from: String, to: String): BigDecimal {
        val a = unit(from)
        val b = unit(to)
        require(a.first == b.first) { "Unidades incompatibles. UI, masa, mEq, volumen y tiempo no son intercambiables." }
        return value.multiply(a.second).divide(b.second) // all conversion factors terminate exactly
    }

    fun dose(requested: String, requestedUnit: String, available: String, availableUnit: String, ml: String): EducationResult {
        require(requestedUnit in amounts && availableUnit in amounts) { "Selecciona unidades de cantidad." }
        val a = number(requested)
        val b = number(available)
        val v = number(ml)
        val converted = normalizedAmount(a, requestedUnit, availableUnit)
        return EducationResult(
            "V = cantidad del ejercicio / cantidad disponible × volumen disponible",
            "$a $requestedUnit = $converted $availableUnit; ($converted $availableUnit / $b $availableUnit) × $v mL",
            ratio(converted.multiply(v), b), "mL",
            "No obtiene dosis de un medicamento ni comprueba presentación, vía o reconstitución. El volumen disponible debe ser conocido, no deducido del diluyente añadido.",
        )
    }

    private fun concentration(raw: String, value: BigDecimal): Pair<String, BigDecimal> = when (raw) {
        "mg/mL" -> "masa/volumen" to value
        "g/mL" -> "masa/volumen" to value.multiply(BigDecimal("1000"))
        "mcg/mL" -> "masa/volumen" to value.multiply(BigDecimal("0.001"))
        "g/L" -> "masa/volumen" to value
        "UI/mL" -> "actividad/volumen" to value
        "mEq/mL" -> "equivalentes/volumen" to value
        "% m/v", "% v/v" -> raw to value.also { require(it <= BigDecimal("100")) { "El porcentaje debe ser mayor que 0 y no superar 100." } }
        else -> throw IllegalArgumentException("Define concentración y unidad; no se admite un porcentaje sin tipo.")
    }

    fun dilution(c1: String, u1: String, c2: String, u2: String, finalMl: String): EducationResult {
        val a = number(c1)
        val b = number(c2)
        val v = number(finalMl)
        val initial = concentration(u1, a)
        val final = concentration(u2, b)
        require(initial.first == final.first) { "Concentraciones incompatibles; el tipo de porcentaje debe coincidir." }
        require(final.second <= initial.second) { "C2 no puede superar C1 en este ejercicio de dilución." }
        val stock = ratio(final.second.multiply(v), initial.second)
        return EducationResult(
            "C1 × V1 = C2 × V2; V1 = C2 × V2 / C1",
            "C1=$a $u1; C2=$b $u2; normalizadas=${initial.second} y ${final.second}; V2=$v mL",
            stock, "mL de solución inicial",
            "Volumen adicional ideal: ${display(v.subtract(stock))} mL; volumen FINAL: $v mL. Solo mezcla ideal de la misma sustancia y volúmenes aditivos. No sirve para reconstitución, mezclas incompatibles ni porcentaje masa/masa.",
        )
    }

    fun perKg(dose: String, amountUnit: String, kg: String, basis: String, ageDays: String): EducationResult {
        require(amountUnit in amounts && basis in listOf("por dosis", "por día")) { "Indica unidad y si la cantidad es por dosis o por día." }
        val age = number(ageDays, true)
        require(age.stripTrailingZeros().scale() <= 0 && age <= BigDecimal("50000")) { "Indica edad ficticia en días completos, de 0 a 50 000." }
        val weight = number(kg)
        require(weight <= BigDecimal("500")) { "Peso fuera del límite técnico del ejemplo (hasta 500 kg); no es un rango clínico." }
        val a = number(dose)
        val result = a.multiply(weight)
        require(result <= resultLimit)
        return EducationResult("Cantidad = cantidad por kg × peso", "$a $amountUnit/kg $basis × $weight kg; edad del ejemplo: $age días", result, "$amountUnit $basis",
            "La cantidad por kg la introduce el estudiante a partir de un enunciado ficticio. No se deriva de la edad ni de un diagnóstico. Por día no significa por dosis; no se distribuye en tomas automáticamente.")
    }

    fun rate(ml: String, hours: String): EducationResult {
        val v = number(ml); val t = number(hours)
        return EducationResult("mL/h = volumen / horas", "$v mL / $t h", ratio(v, t), "mL/h")
    }

    fun drops(ml: String, hours: String, equipment: String): EducationResult {
        val v = number(ml); val t = number(hours); val f = number(equipment)
        require(f.stripTrailingZeros().scale() <= 0 && f <= BigDecimal("200")) { "Introduce el factor entero indicado en el equipo ficticio (límite técnico 200 gotas/mL). No se supone 20 ni 60." }
        return EducationResult("gotas/min = volumen × factor / (horas × 60)", "$v mL × $f gotas/mL / ($t h × 60 min/h)", ratio(v.multiply(f), t.multiply(BigDecimal("60"))), "gotas/min",
            "Factor introducido explícitamente. La app no verifica un equipo real ni prescribe una velocidad.", whole = true)
    }

    fun duration(ml: String, mlHour: String): EducationResult {
        val v = number(ml); val r = number(mlHour)
        val h = ratio(v, r)
        return EducationResult("horas = volumen / velocidad", "$v mL / ($r mL/h)", h, "h", "Equivale aproximadamente a ${display(h.multiply(BigDecimal("60")))} min.")
    }

    fun convert(raw: String, from: String, to: String): EducationResult {
        val value = number(raw, true)
        return EducationResult("Valor destino = valor origen × factor dimensional", "$value $from × ${unit(from).second} / ${unit(to).second}", normalizedAmount(value, from, to).also { require(it.abs() <= resultLimit) { "El resultado supera el límite técnico del ejercicio." } }, to)
    }

    fun bmi(kg: String, cm: String): EducationResult {
        val weight = number(kg); val height = number(cm)
        require(weight <= BigDecimal("500") && height >= BigDecimal("30") && height <= BigDecimal("250")) { "Fuera del intervalo técnico del ejercicio: peso hasta 500 kg y talla de 30 a 250 cm. No es una valoración clínica." }
        val m = height.movePointLeft(2)
        return EducationResult("IMC = peso / talla²", "$weight kg / ($m m × $m m)", ratio(weight, m.multiply(m)), "kg/m²", "Ejemplo adulto, sin clasificación. No interpreta crecimiento pediátrico, embarazo ni composición corporal.")
    }

    fun balance(income: String, output: String): EducationResult {
        val a = number(income, true); val b = number(output, true)
        return EducationResult("Balance = ingresos − egresos", "$a mL − $b mL (mismo periodo)", a.subtract(b), "mL", "Solo cantidades registradas del mismo intervalo. No estima pérdidas insensibles ni propone tratamiento.")
    }

    fun display(value: BigDecimal, whole: Boolean = false): String {
        val rounded = value.setScale(if (whole) 0 else 6, RoundingMode.HALF_UP)
        if (!whole && rounded.signum() == 0 && value.signum() != 0) return value.round(MathContext(6, RoundingMode.HALF_UP)).stripTrailingZeros().toEngineeringString()
        return rounded.stripTrailingZeros().toPlainString()
    }
}
