package com.idreyk.enfermeria360

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier

@Composable
fun DoseExerciseScreen(type: String, vm: AppViewModel, back: () -> Unit) {
    val dilution = type == "dilucion"
    val units = if (dilution) DoseMath.concentrationUnits else DoseMath.amountUnits
    var first by rememberSaveable(type) { mutableStateOf("") }
    var second by rememberSaveable(type) { mutableStateOf("") }
    var volume by rememberSaveable(type) { mutableStateOf("") }
    var unit1 by rememberSaveable(type) { mutableIntStateOf(0) }
    var unit2 by rememberSaveable(type) { mutableIntStateOf(0) }
    var result by rememberSaveable(type) { mutableStateOf("") }
    var summary by rememberSaveable(type) { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    fun clear() {
        result = ""
        summary = ""
        error = ""
    }
    Page(
        if (dilution) "Dilución · ejercicio" else "Dosis a volumen · ejercicio",
        "Aritmética con unidades, sin selección de tratamiento",
        back,
    ) {
        Notice(
            "Solo para práctica con cantidades ficticias. No calcula una dosis prescrita ni valida fármaco, vía, concentración segura, estabilidad, compatibilidad o velocidad. Nunca uses el resultado como indicación de administración.",
            true,
        )
        Input(
            if (dilution) "Concentración inicial C1" else "Cantidad indicada en el ejercicio",
            first,
            {
                first = it.take(40)
                clear()
            },
            numeric = true,
        )
        ChoiceField(
            if (dilution) "Unidad de C1" else "Unidad de la cantidad indicada",
            units.mapIndexed { i, s -> s to i },
            unit1,
            showPoints = false,
        ) {
            unit1 = it
            clear()
        }
        Input(
            if (dilution) "Concentración final C2" else "Cantidad disponible en la presentación",
            second,
            {
                second = it.take(40)
                clear()
            },
            numeric = true,
        )
        ChoiceField(
            if (dilution) "Unidad de C2" else "Unidad de la cantidad disponible",
            units.mapIndexed { i, s -> s to i },
            unit2,
            showPoints = false,
        ) {
            unit2 = it
            clear()
        }
        Input(
            if (dilution) "Volumen FINAL total V2" else "Volumen de la presentación disponible",
            volume,
            {
                volume = it.take(40)
                clear()
            },
            numeric = true,
            unit = "mL",
        )
        Text(
            if (dilution)
                "C1 × V1 = C2 × V2. V2 es el volumen total, no el volumen de diluyente. Se supone mezcla ideal y volumen aditivo; no se usa para reconstitución de polvos."
            else
                "mL teóricos = cantidad indicada / cantidad disponible × mL disponibles. La masa se convierte explícitamente; UI solo se compara con UI.",
            style = MaterialTheme.typography.bodySmall,
            color = LocalPalette.current.muted,
        )
        if (error.isNotBlank()) Notice(error, true)
        Primary(
            "Calcular ejercicio",
            {
                clear()
                try {
                    val a = DoseMath.number(first)
                    val b = DoseMath.number(second)
                    val v = DoseMath.number(volume)
                    if (dilution) {
                        val r = DoseMath.dilution(a, units[unit1], b, units[unit2], v)
                        result =
                            "Solución inicial: ≈ ${DoseMath.display(r.stockMl)} mL\nVolumen adicional teórico: ≈ ${DoseMath.display(r.additionalMl)} mL\nVolumen final: ${DoseMath.display(r.finalMl)} mL"
                        summary =
                            "C1=$first ${units[unit1]}; C2=$second ${units[unit2]}; V2=$volume mL. $result. Mezcla ideal, sin validar compatibilidad."
                    } else {
                        val r = DoseMath.volume(a, units[unit1], b, units[unit2], v)
                        result = "≈ ${DoseMath.display(r)} mL teóricos"
                        summary =
                            "Cantidad del ejercicio: $first ${units[unit1]}; presentación: $second ${units[unit2]} en $volume mL. $result. No es indicación de administración."
                    }
                } catch (e: IllegalArgumentException) {
                    error = e.message.orEmpty()
                }
            },
            first.isNotBlank() && second.isNotBlank() && volume.isNotBlank(),
        )
        if (result.isNotBlank()) {
            AppCard(Modifier.fillMaxWidth()) {
                Badge("RESULTADO MATEMÁTICO", Teal)
                Text(result, style = MaterialTheme.typography.titleLarge)
                Text(
                    "Visualización aproximada. No se redondea a una presentación, jeringa ni pauta de administración.",
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalPalette.current.muted,
                )
            }
            Primary(
                "Guardar ejercicio matemático",
                {
                    vm.practice(if (dilution) "Dilución matemática" else "Dosis a volumen", summary)
                },
                !vm.busy,
            )
        }
        Secondary(
            "Limpiar campos",
            {
                first = ""
                second = ""
                volume = ""
                clear()
            },
        )
    }
}
