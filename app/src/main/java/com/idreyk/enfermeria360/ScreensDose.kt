package com.idreyk.enfermeria360

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier

internal fun calculationInput(text: String): String = if (text.length <= 128) text else "ENTRADA DEMASIADO LARGA"

@Composable
fun EducationResultCard(result: EducationResult, title: String = "${result.presented} ${result.unit}") {
    AppCard(Modifier.fillMaxWidth()) {
        Badge("EJERCICIO EDUCATIVO", Teal)
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(result.summary, style = MaterialTheme.typography.bodySmall, color = LocalPalette.current.muted)
    }
}

/** Existing route and labels retained; calculation engine now uses decimal values. */
@Composable
fun DoseExerciseScreen(type: String, vm: AppViewModel, back: () -> Unit) {
    val dilution = type == "dilucion"
    val units = if (dilution) EducationMath.concentrations else EducationMath.amounts
    var first by rememberSaveable(type) { mutableStateOf("") }
    var second by rememberSaveable(type) { mutableStateOf("") }
    var volume by rememberSaveable(type) { mutableStateOf("") }
    var unit1 by rememberSaveable(type) { mutableIntStateOf(0) }
    var unit2 by rememberSaveable(type) { mutableIntStateOf(0) }
    var result by remember(type) { mutableStateOf<EducationResult?>(null) }
    var error by remember { mutableStateOf("") }
    fun clear() { result = null; error = "" }
    Page(if (dilution) "Dilución · ejercicio" else "Dosis a volumen · ejercicio", "Aritmética decimal con unidades, sin selección de tratamiento", back) {
        Notice("Solo cantidades ficticias. No calcula una dosis prescrita ni valida fármaco, vía, concentración segura, estabilidad, compatibilidad o velocidad. Nunca uses el resultado como indicación de administración.", true)
        Input(if (dilution) "Concentración inicial C1" else "Cantidad indicada en el ejercicio", first,
            { first = calculationInput(it); clear() }, numeric = true)
        ChoiceField(if (dilution) "Unidad de C1" else "Unidad de la cantidad indicada", units.mapIndexed { i, s -> s to i }, unit1, showPoints = false) { unit1 = it; clear() }
        Input(if (dilution) "Concentración final C2" else "Cantidad disponible en la presentación", second,
            { second = calculationInput(it); clear() }, numeric = true)
        ChoiceField(if (dilution) "Unidad de C2" else "Unidad de la cantidad disponible", units.mapIndexed { i, s -> s to i }, unit2, showPoints = false) { unit2 = it; clear() }
        Input(if (dilution) "Volumen FINAL total V2" else "Volumen de la presentación disponible", volume,
            { volume = calculationInput(it); clear() }, numeric = true, unit = "mL")
        Text(if (dilution) "C1 × V1 = C2 × V2. Misma sustancia, mezcla ideal y volumen aditivo. V2 es el volumen final, no diluyente. No reconstituye polvos."
            else "V = cantidad del ejercicio / cantidad disponible × mL disponibles. Masa, UI y mEq no son intercambiables.", style = MaterialTheme.typography.bodySmall)
        if (error.isNotBlank()) Notice(error, true)
        Primary("Calcular ejercicio", {
            clear()
            try {
                result = if (dilution) EducationMath.dilution(first, units[unit1], second, units[unit2], volume)
                    else EducationMath.dose(first, units[unit1], second, units[unit2], volume)
            } catch (e: IllegalArgumentException) { error = e.message ?: "Revisa las cantidades y unidades." }
        }, first.isNotBlank() && second.isNotBlank() && volume.isNotBlank())
        result?.let { value ->
            EducationResultCard(value, if (dilution) "${value.presented} mL de solución inicial" else "≈ ${value.presented} mL teóricos")
            Primary("Guardar ejercicio matemático", { vm.practice(if (dilution) "Dilución matemática" else "Dosis a volumen", value.summary) }, !vm.busy)
        }
        Secondary("Limpiar campos", { first = ""; second = ""; volume = ""; clear() })
    }
}

@Composable
fun DecimalCalculatorScreen(type: String, vm: AppViewModel, back: () -> Unit) {
    val names = mapOf("imc" to "Índice de masa corporal", "goteo" to "Goteo", "conversor" to "Conversor de unidades", "kg" to "Cantidad por peso", "bomba" to "Velocidad en mL/h", "tiempo" to "Duración de infusión", "balance" to "Balance aritmético")
    val name = names[type]
    if (name == null) { Page("Herramienta no disponible", back = back) { Notice("Vuelve al catálogo de ejercicios.", true) }; return }
    var first by rememberSaveable(type) { mutableStateOf("") }
    var second by rememberSaveable(type) { mutableStateOf("") }
    var factor by rememberSaveable(type) { mutableStateOf("") } // no equipment factor is assumed
    var age by rememberSaveable(type) { mutableStateOf("") }
    var period by rememberSaveable(type) { mutableStateOf("") }
    var basis by rememberSaveable(type) { mutableIntStateOf(-1) }
    var amount by rememberSaveable(type) { mutableIntStateOf(0) }
    var from by rememberSaveable(type) { mutableIntStateOf(1) }
    var to by rememberSaveable(type) { mutableIntStateOf(2) }
    var result by remember(type) { mutableStateOf<EducationResult?>(null) }
    var error by remember(type) { mutableStateOf("") }
    fun clear() { result = null; error = "" }
    val firstLabel = when (type) { "imc" -> "Peso de ejemplo"; "kg" -> "Cantidad por kg del enunciado"; "balance" -> "Ingresos de ejemplo"; "conversor" -> "Cantidad"; else -> "Volumen de ejemplo" }
    val firstUnit = when (type) { "imc" -> "kg"; "conversor", "kg" -> null; else -> "mL" }
    val secondLabel = when (type) { "imc" -> "Talla de ejemplo"; "kg" -> "Peso del ejemplo"; "tiempo" -> "Velocidad del ejemplo"; "balance" -> "Egresos de ejemplo"; else -> "Tiempo de ejemplo" }
    val secondUnit = when (type) { "imc" -> "cm"; "kg" -> "kg"; "tiempo" -> "mL/h"; "balance" -> "mL"; else -> "h" }
    Page(name, "Cantidades ficticias · cálculo decimal y dimensional", back) {
        Notice("EJERCICIO EDUCATIVO. No selecciona tratamientos ni determina valores clínicamente seguros. Los límites de entrada son técnicos, no límites de administración.", true)
        Input(firstLabel, first, { first = calculationInput(it); clear() }, true, firstUnit)
        if (type != "conversor") Input(secondLabel, second, { second = calculationInput(it); clear() }, true, secondUnit)
        if (type == "goteo") {
            Input("Factor leído en el equipo ficticio", factor, { factor = calculationInput(it); clear() }, true, "gotas/mL")
            Text("Dato obligatorio. No se preselecciona un factor de 20 ni de 60.", style = MaterialTheme.typography.bodySmall)
        }
        if (type == "kg") {
            ChoiceField("Unidad de la cantidad por kg", EducationMath.amounts.mapIndexed { i, s -> s to i }, amount, showPoints = false) { amount = it; clear() }
            ChoiceField("Base de la cantidad del enunciado", listOf("Por dosis" to 0, "Por día" to 1), basis, showPoints = false) { basis = it; clear() }
            Input("Edad del ejemplo en días completos", age, { age = calculationInput(it); clear() }, true, "días")
            Notice("La edad contextualiza el ejercicio, no calcula la dosis. Por día no se convierte automáticamente en por dosis. No se propone cantidad por kg.")
        }
        if (type == "balance") Input("Intervalo común de ingresos y egresos", period, { period = it.take(120); clear() })
        if (type == "imc") Notice("Ejemplo adulto sin clasificación. No interpreta pediatría, embarazo ni composición corporal.")
        if (type == "conversor") {
            ChoiceField("Unidad de origen", EducationMath.conversions.mapIndexed { i, s -> s to i }, from, showPoints = false) { from = it; clear() }
            ChoiceField("Unidad de destino", EducationMath.conversions.mapIndexed { i, s -> s to i }, to, showPoints = false) { to = it; clear() }
        }
        val complete = first.isNotBlank() && (type == "conversor" || second.isNotBlank()) &&
            (type != "goteo" || factor.isNotBlank()) && (type != "kg" || (basis >= 0 && age.isNotBlank())) && (type != "balance" || period.isNotBlank())
        if (error.isNotBlank()) Notice(error, true)
        Primary("Calcular ejemplo", {
            clear()
            try {
                result = when (type) {
                    "imc" -> EducationMath.bmi(first, second)
                    "goteo" -> EducationMath.drops(first, second, factor)
                    "bomba" -> EducationMath.rate(first, second)
                    "tiempo" -> EducationMath.duration(first, second)
                    "kg" -> EducationMath.perKg(first, EducationMath.amounts[amount], second, if (basis == 0) "por dosis" else "por día", age)
                    "balance" -> EducationMath.balance(first, second).let { it.copy(explanation = "Intervalo ficticio: $period. ${it.explanation}") }
                    else -> EducationMath.convert(first, EducationMath.conversions[from], EducationMath.conversions[to])
                }
            } catch (e: IllegalArgumentException) { error = e.message ?: "Revisa valores y dimensiones." }
        }, complete, R.drawable.ic_calculator)
        result?.let { value -> EducationResultCard(value); Secondary("Guardar práctica", { vm.practice(name, value.summary) }, !vm.busy) }
        Secondary("Limpiar campos", { first = ""; second = ""; factor = ""; age = ""; period = ""; basis = -1; clear() })
    }
}
