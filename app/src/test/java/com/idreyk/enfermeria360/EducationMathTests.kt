package com.idreyk.enfermeria360

import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Test

class EducationMathTests {
    private fun value(expected: String, actual: EducationResult) = assertEquals(0, BigDecimal(expected).compareTo(actual.value))
    private fun blocked(body: () -> Unit) { assertThrows(IllegalArgumentException::class.java) { body() } }

    @Test fun decimalMultiplicationDoesNotIntroduceBinaryError() { value("0.3", EducationMath.dose("0.1", "mg", "0.3", "mg", "0.9")) }
    @Test fun doseVolumeCancelsCompatibleMassUnits() { value("2.5", EducationMath.dose("0.25", "g", "500", "mg", "5")) }
    @Test fun microgramsToMilligramsAreExplicit() { value("2", EducationMath.dose("1000", "mcg", "0.5", "mg", "1")) }
    @Test fun internationalUnitsAreNotMass() { blocked { EducationMath.dose("1", "UI", "1", "mg", "1") } }
    @Test fun equivalentsAreIsolatedFromMassAndActivity() {
        value("2", EducationMath.dose("2", "mEq", "4", "mEq", "4"))
        blocked { EducationMath.dose("1", "mEq", "1", "UI", "1") }
        blocked { EducationMath.dose("1", "mg", "1", "mEq", "1") }
    }
    @Test fun missingAndZeroDenominatorsNeverProduceResults() {
        for (v in listOf("", " ", "0", "0.000")) blocked { EducationMath.dose("1", "mg", v, "mg", "1") }
    }
    @Test fun decimalCommaAndLeadingDecimalAreSupported() {
        assertEquals(BigDecimal("0.5"), EducationMath.number(",5"))
        value("2.5", EducationMath.dose("0,25", "g", "500", "mg", "5"))
    }
    @Test fun nanInfinityExponentsNegativesGroupingAndMixedSeparatorsAreRejected() {
        for (s in listOf("NaN", "Infinity", "-1", "+1", "1e3", "1,000,000", "1.000,50", "1/2", "--2", "1 000")) blocked { EducationMath.number(s) }
    }
    @Test fun explicitTechnicalPrecisionAndMagnitudeLimitsAreEnforced() {
        for (s in listOf("1000000001", "0.0000000001", "0".repeat(40), "999999999.9999999999")) blocked { EducationMath.number(s) }
        assertTrue(EducationMath.number("1000000000") > BigDecimal.ZERO)
    }
    @Test fun dilutionUsesFinalVolumeAndExplicitNormalizedConcentrations() {
        val r = EducationMath.dilution("1", "g/L", "0.5", "mg/mL", "100")
        value("50", r)
        assertTrue(r.explanation.contains("Volumen adicional ideal: 50 mL"))
        assertTrue(r.substitution.contains("V2=100 mL"))
    }
    @Test fun sameConcentrationNeedsNoAdditionalVolume() {
        assertTrue(EducationMath.dilution("2", "mg/mL", "2", "mg/mL", "5").explanation.contains("adicional ideal: 0 mL"))
    }
    @Test fun dilutionDoesNotConcentrateOrMixDimensions() {
        blocked { EducationMath.dilution("1", "mg/mL", "2", "mg/mL", "100") }
        blocked { EducationMath.dilution("1", "UI/mL", "1", "mg/mL", "100") }
        blocked { EducationMath.dilution("1", "% m/v", "1", "% v/v", "100") }
        blocked { EducationMath.dilution("1", "%", "1", "%", "100") }
        blocked { EducationMath.dilution("101", "% m/v", "10", "% m/v", "100") }
    }
    @Test fun idealPercentageDilutionHasTypeAndAssumptionWarnings() {
        val r = EducationMath.dilution("20", "% m/v", "5", "% m/v", "100")
        value("25", r)
        assertTrue(r.explanation.contains("No sirve para reconstitución"))
    }
    @Test fun dosePerKgMultipliesOnlyProvidedFictitiousQuantity() {
        val r = EducationMath.perKg("0.5", "mg", "20", "por dosis", "3650")
        value("10", r); assertEquals("mg por dosis", r.unit)
    }
    @Test fun perDayIsNotSilentlySplitIntoDoses() {
        val r = EducationMath.perKg("2", "UI", "3", "por día", "0")
        value("6", r); assertEquals("UI por día", r.unit)
        assertTrue(r.explanation.contains("no se distribuye"))
    }
    @Test fun perKgRequiresContextAndRejectsInvalidWeightAndAge() {
        blocked { EducationMath.perKg("1", "mg", "10", "", "20") }
        blocked { EducationMath.perKg("1", "mg", "0", "por dosis", "20") }
        blocked { EducationMath.perKg("1", "mg", "501", "por dosis", "20") }
        blocked { EducationMath.perKg("1", "mg", "10", "por dosis", "") }
        blocked { EducationMath.perKg("1", "mg", "10", "por dosis", "1.5") }
        blocked { EducationMath.perKg("1", "mg", "10", "por dosis", "50001") }
    }
    @Test fun infusionRateAndDurationAreInverseForExactExample() {
        value("125", EducationMath.rate("500", "4"))
        value("4", EducationMath.duration("500", "125"))
    }
    @Test fun zeroTimeAndRateAreBlocked() {
        blocked { EducationMath.rate("500", "0") }; blocked { EducationMath.duration("500", "0") }
    }
    @Test fun dripRequiresUserEnteredEquipmentFactor() {
        for (factor in listOf("", "0", "-20", "20.5", "201")) blocked { EducationMath.drops("500", "4", factor) }
        val r = EducationMath.drops("500", "4", "20")
        assertEquals("42", r.presented); assertEquals("gotas/min", r.unit)
        assertTrue(r.substitution.contains("20 gotas/mL"))
    }
    @Test fun dripHalfUpRoundingIsPresentationOnly() {
        val r = EducationMath.drops("10.5", "1", "60")
        value("10.5", r); assertEquals("11", r.presented)
        assertTrue(r.summary.contains("mitades hacia arriba"))
    }
    @Test fun convertersRespectMassVolumeTimeAndIdentity() {
        value("1000", EducationMath.convert("1", "g", "mg"))
        value("0.001", EducationMath.convert("1", "mcg", "mg"))
        value("1000", EducationMath.convert("1", "L", "mL"))
        value("2", EducationMath.convert("120", "min", "h"))
        value("0", EducationMath.convert("0", "UI", "UI"))
    }
    @Test fun convertersRejectDifferentDimensionsOrOutputAboveTechnicalLimit() {
        blocked { EducationMath.convert("1", "mg", "mL") }
        blocked { EducationMath.convert("1", "mEq", "mg") }
        blocked { EducationMath.convert("1", "UI", "mEq") }
        blocked { EducationMath.convert("1000000000", "g", "mcg") }
    }
    @Test fun balanceAcceptsExplicitZeroButNotMissingQuantities() {
        value("0", EducationMath.balance("0", "0"))
        value("-0.2", EducationMath.balance("0.1", "0.3"))
        blocked { EducationMath.balance("", "0") }; blocked { EducationMath.balance("0", "-1") }
    }
    @Test fun adultBmiIsArithmeticWithoutDiagnosis() {
        value("25", EducationMath.bmi("100", "200"))
        assertTrue(EducationMath.bmi("100", "200").explanation.contains("sin clasificación"))
        blocked { EducationMath.bmi("70", "0") }; blocked { EducationMath.bmi("501", "170") }
    }
    @Test fun tinyPositiveVolumeIsNotDisplayedAsZero() {
        val r = EducationMath.dose("0.000000001", "mg", "1000000000", "mg", "0.000000001")
        assertTrue(r.value > BigDecimal.ZERO); assertNotEquals("0", r.presented)
    }
    @Test fun finalDivisionPrecisionAndRoundingAreDisclosed() {
        val r = EducationMath.dose("1", "mg", "3", "mg", "1")
        assertEquals(34, r.value.precision()); assertEquals("0.333333", r.presented)
        assertTrue(r.summary.contains("34 cifras"))
    }
    @Test fun everyResultIncludesEducationalWarningFormulaSubstitutionAndUnit() {
        val results = listOf(EducationMath.dose("1", "mg", "2", "mg", "1"), EducationMath.dilution("2", "mg/mL", "1", "mg/mL", "10"),
            EducationMath.rate("10", "2"), EducationMath.drops("10", "2", "20"), EducationMath.duration("10", "5"),
            EducationMath.convert("2", "g", "mg"), EducationMath.bmi("70", "170"), EducationMath.balance("1", "2"), EducationMath.perKg("1", "mg", "3", "por dosis", "0"))
        results.forEach { assertTrue(it.summary.startsWith("EJERCICIO EDUCATIVO")); assertTrue(it.formula.isNotBlank()); assertTrue(it.substitution.isNotBlank()); assertTrue(it.unit.isNotBlank()); assertTrue(it.summary.contains("No autoriza administración")) }
    }
}
