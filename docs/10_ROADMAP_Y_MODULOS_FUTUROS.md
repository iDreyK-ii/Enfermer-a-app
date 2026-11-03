# 10 — Roadmap y módulos futuros

Fecha: 2026-11-03 · Base Maestra 2.1 · Android 0.6.0-dev.

Este archivo separa código ya disponible de ampliaciones y bloqueos. **No es una lista de funciones supuestamente terminadas ni un porcentaje de avance.** La matriz actual está en `MVP_BASE_MAESTRA_v21.md` y la app muestra estados explícitos.

## P0 — Cerrar verificación de la entrega actual

1. **Instalación/actualización real con firma compatible**, conservando SQLite v2, cuaderno, medios y preferencias. Probar una actualización desde la versión ya instalada. No desinstalar para resolver firmas incompatibles.
2. **SAF e impresión/PDF:** ejecutar `EducationalPdfDeviceTests` en Android, abrir el PDF, comprobar encabezados educativos/paginación/texto final y selección de páginas; después ensayar el servicio de impresión y cancelación del selector. Las dos pruebas están compiladas, no ejecutadas. Robolectric no implementa el escritor nativo `PdfDocument` y no se acepta un mock como prueba de exportación real.
3. **Móvil/tablet reales:** teclado/IME, orientación, proceso recreado, aplicación en segundo plano, accesibilidad con TalkBack y texto grande, contraste, navegación por partes, respuestas y persistencia. Los renders 390/800/320 dp no sustituyen esta matriz.
4. **Rendimiento:** medir inicio, memoria, fluidez y batería en un equipo limitado y otro capaz. Conservar selección automática/manual de rendimiento y prioridad de movimiento reducido. El cronómetro debe seguir operable sin animación.
5. **Cuenta opcional:** cuando el propietario retome la configuración, guardar realmente ambas plantillas OTP y verificar envío, código, renovación/cierre, errores y publicación/importación con RLS. No volver a crear el proyecto ni aplicar otra vez la migración inicial. No sincronizar datos personales sin diseño y autorización específicos.
6. **Revisión docente/experta** de los 20 temas, siete casos/21 preguntas y ocho esquemas; registrar quién revisó, versión, ámbito y fecha. El funcionamiento técnico no acredita esa revisión.

## P1 — Biblioteca, trazabilidad y escalas

- Ampliar contenidos y competencias de la sección 01 sin convertir la app en un plan por ciclos universitarios.
- Mejorar navegación documental, historial editorial, versiones retiradas, avisos de vigencia y búsqueda de nuevas fuentes. No marcar una referencia como leída completa si solo se consultaron páginas o un resumen.
- **Braden, Morse, Norton y RASS administrados:** resolver instrumento exacto, población, traducción, protocolo de administración, reglas de datos incompletos, derechos digitales, atribución y pruebas de puntuación antes de habilitar un formulario. Los registros externos actuales no satisfacen esta fase.
- Extender escalas de riesgo, función, cognición, salud mental, dolor y deterioro fisiológico solo con expediente por instrumento. No redibujar caras/estímulos restringidos ni transformar versiones autorizadas para adaptarlas a la estética.
- Separar adulto, pediatría y neonatología. **No volver a etiquetar como futuros** el GCS/EVA/NRS/4AT, Capurro, Silverman, Apgar y registros ya existentes: revisar primero su alcance real.

**Criterio de salida:** procedencia y alcance verificables, permiso suficiente cuando corresponda, población/versiones explícitas, pruebas completas y revisión profesional registrada. Una ficha documental no equivale a calculadora.

## P2 — Atlas, bioseguridad y entorno clínico

- Revisar anatomía visual y denominaciones de los ocho dispositivos con fabricante/experto; añadir variantes sin afirmar que un esquema genérico representa todos los modelos.
- Añadir instrumental y equipos de otras áreas con partes independientes, textos accesibles, zoom/capas y fuentes; nunca un tutorial invasivo autónomo.
- Refinar ilustraciones/movimientos de higiene por maniobra bajo revisión, conservando texto y controles en modo reducido. No universalizar preparación quirúrgica ni convertir un temporizador en aprobación de limpieza.
- **Coche de paro, cartuchera y área clínica completos:** modelos institucionales, responsables de revisión, fechas y listas de comprobación. No inventar existencias, composición obligatoria universal, medicación disponible o un protocolo de emergencia validado.
- Profundizar EPP, precauciones estándar y adicionales, exposición ocupacional, aislamiento, material punzocortante y gestión segura de residuos con protocolo local/fuentes vigentes.

**Criterio de salida:** navegación funcional y revisión de cada objeto/procedimiento; accesibilidad comprobada; ámbito institucional visible; nada publicado como instrucción profesional sin revisión.

## P3 — PAE, casos, especialidades y docencia

### PAE y biblioteca personal

- Ejecutar y cerrar los ensayos reales de exportación; ampliar presentación del documento solo después de verificar conservación de datos.
- Guardado/reanudación robustos ante recreación del proceso, adjuntos vinculados y organización de biblioteca personal con límites/tipos claros. No confundir un respaldo del cuaderno con respaldo de los medios.
- Mejorar razonamiento explícito, criterios de evaluación, SOAPIE y SBAR sin diagnóstico automático ni catálogos NANDA-I/NOC/NIC copiados. La app no suministra los códigos licenciados.

### Casos y simulación

- Revisión docente de los siete recorridos actuales, diversidad de respuestas, rúbricas y accesibilidad. La puntuación debe derivarse de acciones reales, no de animaciones de éxito.
- Nuevos casos y escenarios multisistema/simulación avanzada con datos inequívocamente ficticios, sin reconstruir órdenes de documentos históricos.
- ECG, tendencias y escenarios de deterioro más profundos: no confundir una señal ilustrativa con adquisición de sensores, interpretación diagnóstica o monitorización real.
- Evaluación/docencia y seguimiento de grupos solo después de definir permisos, privacidad, datos mínimos y mecanismos de revisión; no fingir un panel multiusuario conectado.

### Especialidades y temas complementarios

Desarrollar por paquetes revisados: urgencias y cuidados críticos; medicina interna y cirugía/perioperatorio; pediatría y neonatología; materno-infantil; salud mental; adulto mayor; comunidad/atención primaria; rehabilitación, cronicidad y cuidados paliativos; nutrición y soporte; áreas renal, respiratoria y cardiovascular; heridas/piel y prevención. El inventario editorial de la Base guía la planificación, no acredita que estos módulos estén ya implementados.

**Criterio de salida:** contenido revisado, persistencia y rutas reales, pruebas de errores/retorno/recuperación, población y límites visibles; licencias y privacidad resueltas donde apliquen.

## P4 — Farmacología, al final

La importación nominal **ya está hecha**. Lo pendiente son las monografías clínicas y su publicación, no volver a copiar nombres sin trazabilidad.

1. Aclarar ambigüedades como **`altilice`**, formulaciones y combinaciones; conservar siempre la cadena original y la razón de cualquier corrección. No tratar nutrición, soporte o hemocomponentes como DCI por conveniencia.
2. Priorizar por necesidad educativa y fuente accesible, no por supuestas existencias de una institución. Diferenciar registro sanitario, listado/formulario institucional, disponibilidad/stock y recomendación clínica.
3. Por producto: identidad exacta, país/registro, principio(s), forma, concentración, fabricante/presentación, fecha y fuente oficial pertinente. Una mención en PNUME o una guía no demuestra stock ni registro vigente de una presentación concreta.
4. Revisión de datos de preparación, volumen final, dosis, ajuste, compatibilidad y estabilidad solo con fuentes exactas y vigentes. **Nunca deducir volumen final de reconstitución por el diluyente añadido.**
5. Mantener borradores incompletos utilizables editorialmente, sin proclamarlos fichas clínicas completas. Registrar evidencia por campo y revisión profesional con identidad/fecha/alcance.
6. Avanzar de estado únicamente con evidencia: **IDENTIFICADO → PENDIENTE-FICHA → FUENTE-ENCONTRADA → VALIDADO-DIGEMID/GPC → REVISIÓN-FARMACÉUTICA → PUBLICABLE**. Separar lo regulatorio de lo clínico aunque compartan el nombre del estado. Hoy no se atribuye avance a ninguna de las 554 entradas por estar importada.
7. Publicación versionada y revisada por los mecanismos existentes, conservando campos anteriores y sin sobrescribir cuadernos/favoritos. Las fuentes editoriales privadas no se convierten automáticamente en contenido público aprobado.

**Criterio de salida:** producto inequívoco, fuentes verificadas, permisos suficientes, revisión farmacéutica/clínica y validación de importación/publicación. El conteo de entradas no sirve como criterio de aprobación.

## Reglas permanentes

- Mantener el repositorio Android canónico y la revisión exacta; nada de recrear el proyecto desde una captura.
- Sin IA que diagnostique/prescriba, sin pacientes identificables, sin dosis/stock/resultados clínicos presentados como reales sin evidencia.
- Sin porcentajes de finalización inventados, botones vacíos o módulos bloqueados presentados como calculadoras.
- Ningún pago, despliegue nuevo o publicación en Play sin autorización. No enviar credenciales administrativas ni claves de firma.
- Cada incremento: auditoría de impacto → código/pruebas → evidencia y límites → commit/push → importación exacta en Google → comprobación. No dos aplicaciones divergentes.
