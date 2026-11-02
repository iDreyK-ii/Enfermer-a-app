package com.idreyk.enfermeria360

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class CoreTests {
    private fun seed() =
        javaClass.classLoader!!
            .getResourceAsStream("contenido_inicial.json")!!
            .bufferedReader()
            .use { it.readText() }

    private fun altered(change: (JSONObject) -> Unit): String =
        JSONObject(seed()).also(change).toString()

    @Test
    fun glasgowEveryCombination() {
        for (e in 1..4) for (v in 1..5) for (m in 1..6) {
            assertEquals(e + v + m, PracticeMath.glasgow(e, v, m))
            assertTrue(PracticeMath.glasgow(e, v, m)!! in 3..15)
        }
    }

    @Test
    fun glasgowDoesNotInventMissingValues() {
        assertNull(PracticeMath.glasgow(0, 5, 6))
        assertNull(PracticeMath.glasgow(4, 0, 6))
        assertNull(PracticeMath.glasgow(4, 5, 0))
        assertNull(PracticeMath.glasgow(-1, 5, 6))
        assertNull(PracticeMath.glasgow(4, -1, 6))
        assertNull(PracticeMath.glasgow(4, 5, -1))
        assertThrows(IllegalArgumentException::class.java) { PracticeMath.glasgow(5, 5, 6) }
    }

    @Test
    fun bmiReferenceArithmetic() {
        assertEquals(24.221453287, PracticeMath.bmi(70.0, 170.0), .000001)
    }

    @Test
    fun bmiRejectsInvalidNumbers() {
        for (pair in
            listOf(
                70.0 to 0.0,
                0.0 to 170.0,
                -1.0 to 170.0,
                Double.NaN to 170.0,
                70.0 to Double.POSITIVE_INFINITY,
                501.0 to 170.0,
            )) assertThrows(IllegalArgumentException::class.java) {
            PracticeMath.bmi(pair.first, pair.second)
        }
    }

    @Test
    fun dripReferenceArithmetic() {
        val result = PracticeMath.drip(500.0, 4.0, 20)
        assertEquals(125.0, result.first, 0.0)
        assertEquals(42, result.second)
        assertEquals(125, PracticeMath.drip(500.0, 4.0, 60).second)
    }

    @Test
    fun dripRejectsInvalidInputs() {
        assertThrows(IllegalArgumentException::class.java) { PracticeMath.drip(500.0, 0.0, 20) }
        assertThrows(IllegalArgumentException::class.java) { PracticeMath.drip(500.0, 4.0, 25) }
        assertThrows(IllegalArgumentException::class.java) {
            PracticeMath.drip(Double.NaN, 4.0, 20)
        }
    }

    @Test
    fun conversionArithmetic() {
        assertEquals(.5, PracticeMath.convert(500.0, "mg-g"), 0.0)
        assertEquals(2000.0, PracticeMath.convert(2.0, "L-mL"), 0.0)
        assertEquals(0.0, PracticeMath.convert(0.0, "mL-L"), 0.0)
        assertThrows(IllegalArgumentException::class.java) { PracticeMath.convert(-1.0, "mg-g") }
        assertThrows(IllegalArgumentException::class.java) { PracticeMath.convert(1.0, "unknown") }
    }

    @Test
    fun evaIsOnlyNormalizedPosition() {
        assertEquals(0, PracticeMath.evaPosition(0f))
        assertEquals(100, PracticeMath.evaPosition(1f))
        assertEquals(63, PracticeMath.evaPosition(.63f))
        assertEquals(100, PracticeMath.evaPosition(2f))
        assertEquals(0, PracticeMath.evaPosition(-2f))
        assertThrows(IllegalArgumentException::class.java) { PracticeMath.evaPosition(Float.NaN) }
    }

    @Test
    fun bundledContentPasses() {
        val p = ContentParser.parse(seed())
        assertEquals("idreyk-inicial", p.id)
        assertEquals(1L, p.version)
        assertEquals(6, p.entries.count { it.kind == "med" })
        assertEquals(1, p.entries.count { it.kind == "case" })
        assertEquals(5, p.entries.first { it.kind == "safety" }.checklist.size)
        assertTrue(
            p.entries
                .filter { it.kind == "med" }
                .all { e -> e.sections.values.all { it.isBlank() } }
        )
    }

    @Test
    fun parserRejectsInvalidJsonAndSchema() {
        assertThrows(IllegalArgumentException::class.java) { ContentParser.parse("not json") }
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(altered { it.put("version_esquema", 4) })
        }
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(altered { it.put("version_esquema", 1.5) })
        }
    }

    @Test
    fun parserRejectsBadIdentityAndVersion() {
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(altered { it.put("id_paquete", "../unsafe") })
        }
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(altered { it.put("version_contenido", 0) })
        }
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(altered { it.put("version_contenido", 1.5) })
        }
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(altered { it.put("autor", "") })
        }
    }

    @Test
    fun parserRejectsDuplicateIds() {
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(
                altered {
                    val a = it.getJSONArray("medicamentos")
                    a.put(a.getJSONObject(0))
                }
            )
        }
    }

    @Test
    fun parserRejectsBadDate() {
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(altered { it.put("fecha_actualizacion", "2026-02-31") })
        }
    }

    @Test
    fun parserRejectsUnknownSections() {
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(
                altered {
                    it.getJSONArray("medicamentos")
                        .getJSONObject(0)
                        .getJSONObject("secciones")
                        .put("campo_inventado", "x")
                }
            )
        }
    }

    @Test
    fun parserRejectsUnsupportedSourceSchemes() {
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(
                altered {
                    it.getJSONArray("listas_seguridad")
                        .getJSONObject(0)
                        .getJSONArray("fuentes")
                        .getJSONObject(0)
                        .put("url", "file:///private")
                }
            )
        }
    }

    @Test
    fun declaredReviewRequiresMetadata() {
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(
                altered {
                    it.getJSONArray("medicamentos")
                        .getJSONObject(0)
                        .put("revision", "declarada_por_autor")
                }
            )
        }
        val p =
            ContentParser.parse(
                altered {
                    val e = it.getJSONArray("medicamentos").getJSONObject(0)
                    e.put("revision", "declarada_por_autor")
                        .put("revisor", "Nombre de prueba")
                        .put("fecha_revision", "2026-11-01")
                        .put(
                            "fuentes",
                            JSONArray()
                                .put(
                                    JSONObject()
                                        .put("titulo", "Referencia de prueba")
                                        .put("url", "https://example.org/reference")
                                ),
                        )
                }
            )
        assertEquals("declarada_por_autor", p.entries.first { it.id == "paracetamol" }.revision)
    }

    @Test
    fun listsAreNotHardCodedToTwenty() {
        val p =
            ContentParser.parse(
                altered {
                    val a = JSONArray()
                    for (i in 1..20) a.put(
                        JSONObject()
                            .put("titulo", "Ítem ficticio $i")
                            .put("detalle", "Prueba del formato, no protocolo")
                    )
                    it.getJSONArray("listas_seguridad").getJSONObject(0).put("items", a)
                }
            )
        assertEquals(20, p.entries.first { it.kind == "safety" }.checklist.size)
    }

    @Test
    fun searchHandlesSpanishAccents() {
        assertEquals("valoracion pediatrica", normalized("Valoración pediátrica"))
    }

    @Test
    fun importNeverInterpretsMarkup() {
        val text = "<script>alert('demo')</script> No debe ejecutarse"
        val p =
            ContentParser.parse(
                altered {
                    it.getJSONArray("medicamentos")
                        .getJSONObject(0)
                        .getJSONObject("secciones")
                        .put("descripcion", text)
                }
            )
        assertEquals(text, p.entries.first { it.id == "paracetamol" }.sections["descripcion"])
    }

    @Test
    fun parserRejectsOversizedPayload() {
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse("ñ".repeat(1_000_001))
        }
    }

    @Test
    fun parserRejectsWrongOptionalTypes() {
        for (key in listOf("licencia", "tipo_paquete")) assertThrows(
            IllegalArgumentException::class.java
        ) {
            ContentParser.parse(altered { it.put(key, 123) })
        }
        for (key in listOf("grupo", "revisor", "fecha_revision")) assertThrows(
            IllegalArgumentException::class.java
        ) {
            ContentParser.parse(
                altered { it.getJSONArray("medicamentos").getJSONObject(0).put(key, true) }
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(
                altered { it.getJSONArray("casos").getJSONObject(0).put("contenido", JSONObject()) }
            )
        }
    }

    @Test
    fun sourceUrlsMustBeCompleteAndDatesValid() {
        for (url in
            listOf(
                "https://",
                "https://user:password@example.org/x",
                "https://bad host/path",
            )) assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(
                altered {
                    it.getJSONArray("listas_seguridad")
                        .getJSONObject(0)
                        .getJSONArray("fuentes")
                        .getJSONObject(0)
                        .put("url", url)
                }
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(
                altered {
                    it.getJSONArray("listas_seguridad")
                        .getJSONObject(0)
                        .getJSONArray("fuentes")
                        .getJSONObject(0)
                        .put("fecha_consulta", "2026-02-31")
                }
            )
        }
    }

    @Test
    fun noSilentTextTruncation() {
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(altered { it.put("licencia", "x".repeat(2001)) })
        }
    }

    @Test
    fun invalidUtf8IsNotSilentlyReplaced() {
        assertEquals(
            "Dilución y valoración",
            ContentParser.decodeUtf8("Dilución y valoración".toByteArray(Charsets.UTF_8)),
        )
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.decodeUtf8(byteArrayOf(0xc3.toByte(), 0x28))
        }
    }

    @Test
    fun textIsRequiredInSafetyItems() {
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(
                altered {
                    it.getJSONArray("listas_seguridad")
                        .getJSONObject(0)
                        .getJSONArray("items")
                        .getJSONObject(0)
                        .put("detalle", 500)
                }
            )
        }
    }

    @Test
    fun numericUnderflowDoesNotProduceInfiniteBmi() {
        assertThrows(IllegalArgumentException::class.java) { PracticeMath.bmi(70.0, 1e-300) }
    }

    @Test
    fun backupsMustRetainOriginalAuthorship() {
        assertThrows(IllegalArgumentException::class.java) {
            ContentParser.parse(altered { it.put("tipo_paquete", "respaldo_catalogo") })
        }
    }
}
