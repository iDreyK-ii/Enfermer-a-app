package com.idreyk.enfermeria360

/** Actual saved fictitious readings, not a decorative or diagnostic waveform. */
data class PulseSample(val timestamp: Long, val beats: Double)

object PracticeTrends {
    fun pulse(notes: List<StudyNote>, owner: String): List<PulseSample> =
        notes
            .filter { it.patientId == "vitals:$owner" }
            .sortedWith(compareBy<StudyNote> { it.timestamp }.thenBy { it.id })
            .mapNotNull { note ->
                val fields = note.text.split('|')
                if (fields.size != 5) null
                else
                    fields[2]
                        .trim()
                        .replace(',', '.')
                        .toDoubleOrNull()
                        ?.takeIf { it.isFinite() && it in 0.0..1000.0 }
                        ?.let { PulseSample(note.timestamp, it) }
            }
            .takeLast(20)
}
