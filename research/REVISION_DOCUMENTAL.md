# Revisión documental preparatoria — ampliación v0.3

**Consulta: 1 de noviembre de 2026.** Revisión dirigida para implementar software de práctica, no validación clínica, revisión sistemática de normativa vigente, autorización general de redistribución ni selección del protocolo de un establecimiento peruano.

## Correctos de administración

En la revisión previa se inspeccionó el PDF institucional de E.S.E. BelloSalud **SP-PT-009, versión 03, julio de 2026**, cuya página 19 enumera diez correctos. Es un ejemplo institucional colombiano, no una norma universal ni el protocolo local del usuario. [6](https://www.esebellosalud.gov.co/storage/docs/V9R0xKf98Irm7BP6XdTz.pdf)

Se conserva el Boletín 53 de ISMP-España, diciembre de 2023, como referencia de la necesidad de otras barreras además de verificaciones individuales. No se redistribuyen tablas o protocolos completos. [3](https://seguridaddelpaciente.sanidad.gob.es/informacion/publicaciones/2023/docs/Boletin_53_Diciembre_2023.pdf)

**Decisión de desarrollo:** listas configurables de 1–100 ítems, con institución, edición, fuentes y estado. No afirmar «20 correctos universales». La lista inicial de cinco básicos es educativa e incompleta. Las pruebas de 20 ítems comprueban el formato, no avalan un estándar clínico. v0.3 permite editar, reordenar y guardar el recorrido.

## Capurro B — referencia peruana

Se identificó la **NTS 214-MINSA/DGIESP-2024**, aprobada por **RM 545-2024/MINSA**, publicada el 15 de agosto de 2024. [1](https://www.gob.pe/institucion/minsa/normas-legales/5874349-545-2024-minsa)

En esta ampliación se descargó e inspeccionó el PDF aprobado, no solo la ficha de la resolución. **Anexo 4, página impresa 40 / página PDF 42**: cinco características somáticas y fórmula `(204 + suma) / 7`. Se revisaron el texto extraído y la imagen del cuadro.

Documento inspeccionado: https://cdn.www.gob.pe/uploads/document/file/6780375/5874349-resolucion-ministerial-n-545-2024-minsa.pdf

| Componente somático | Valores representados en el software |
|---|---|
| Forma de la oreja | 0, 8, 16, 24 |
| Glándula mamaria | 0, 5, 10, 15 |
| Formación del pezón | 0, 5, 10, 15 |
| Textura cutánea | 0, 5, 10, 15, 20 |
| Pliegues plantares | 0, 5, 10, 15, 20 |

La app muestra descriptores resumidos, exige seleccionar todos los componentes, no imputa datos faltantes y presenta semanas completas más días remanentes. **La exactitud aritmética de ese formato no es exactitud de datación obstétrica.** No se clasifica ni se prescribe una conducta. No se enseña una exploración completa ni se determina aplicabilidad clínica.

La RM 372-2024/MINSA localizada en la primera fase era un proyecto para opinión. Fue descartada como norma aprobada. No se ha comprobado exhaustivamente cada modificación posterior o su vigencia normativa a noviembre de 2026.

## Capurro A — no mezclar variantes

Se inspeccionó el texto de la **NOM-007-SSA2-2016** de México, página PDF 29: cuatro signos somáticos, excluyendo formación del pezón, más dos neurológicos y constante 200; la variante solo somática utiliza cinco signos y constante 204. Esta fuente identifica también condiciones de aplicación; la app no las sustituye ni decide automáticamente elegibilidad.

Documento inspeccionado: https://www.gob.mx/cms/uploads/attachment/file/512098/NOM-007-SSA2-2016.pdf

**Alcance de implementación A:** transcribir puntuaciones de una ficha externa de composición 4+2; no se reproducen instrucciones de maniobras neurológicas. Los selectores representan oreja 0/8/16/24, glándula 0/5/10/15, piel y pliegues 0/5/10/15/20, bufanda 0/6/12/18 y caída de cabeza 0/4/8/12. Es necesario cotejar la ficha y versión autorizadas con un profesional antes de emplear la interfaz en atención real. El ensayo de 6.400 combinaciones verifica la suma y constante, **no** valida exploración, descriptores, población o equivalencia digital.

Se detectó información secundaria inconsistente en la denominación A/B y en guías que describen A como cinco somáticos más dos neurológicos. No se implementó una suma de siete componentes: la composición y constante se identifican expresamente.

Se localizó un trabajo académico de la Universidad de Cuenca que menciona cuatro somáticos y dos neurológicos, así como rangos 0–18 y 0–12. El intento de descargar su antigua ruta PDF devolvió 404; no se declara una lectura íntegra de esa tesis. [1](https://dspace.ucuenca.edu.ec/bitstream/123456789/22776/1/Tesis.pdf)

## Apgar y Silverman-Andersen

Se inspeccionaron las tablas de Apgar y Silverman del PDF NOM-007-SSA2-2016, página PDF 28. La variante de Silverman descrita allí identifica quejido leve/inconstante y acentuado/constante; no debe mezclarse silenciosamente con adaptaciones que definan ese componente de otro modo. Se identifican la fuente y los cinco componentes en pantalla; se suma 0–10 sin clasificar gravedad ni indicar soporte ventilatorio.

Para Apgar se contrastó el límite de frecuencia cardiaca **100 o más** con la calculadora de MSD, ya que algunas tablas textuales usan signos «menor/mayor de 100» sin aclarar exactamente 100. [3](https://www.msdmanuals.com/medical-calculators/ApgarScore-en.htm)

La app registra un momento elegido, limpia respuestas al cambiarlo y suma cinco componentes 0–2. El componente de coloración tiene limitaciones según el fototipo; el módulo no sustituye otras mediciones. No se usa el resultado para dirigir reanimación, decidir si esperar asistencia ni establecer pronóstico.

## Glasgow y EVA

Se conserva la ayuda oficial de Glasgow en español con atribución a Universidad de Glasgow / Sir Graham Teasdale: https://www.glasgowcomascale.org/downloads/GCS-Assessment-Aid-Spanish.pdf

La página de permisos fue consultada: https://www.glasgowcomascale.org/permissions/ . No extrapolar condiciones de uso clínico/investigación a cualquier redistribución o uso comercial. Revisar autorización para el uso previsto antes de publicación clínica.

Glasgow adulto mantiene componentes y NV sin total inventado. Glasgow pediátrica v0.3 **solo transcribe componentes** de una ficha externa identificada, sin reutilizar los descriptores adultos como si fueran pediátricos. Las pruebas numéricas no validan la exploración.

EVA sigue siendo una simulación de posición digital, no una línea física calibrada de 10 cm ni una versión digital validada.

## Braden y Morse — límites de derechos y de función

Se consultó la FAQ de Fall TIPS: indica que la Morse Fall Scale es gratuita pero requiere permiso del autor. https://falltips.org/faqs/

Para Braden se localizaron avisos de copyright y uso con permiso, incluida la ficha LOINC: https://loinc.org/38228-3 . Las condiciones de una institución o de un repositorio no se transfieren automáticamente a esta app.

**Decisión:** no reproducir cuestionarios, criterios ni clasificaciones de riesgo mientras no se aclaren permisos. La app permite registrar puntajes externos, la versión/fuente y el contexto ficticio. Solo valida formato entero y rango de referencia (Braden 6–23, Morse 0–125); no comprueba coherencia de los ítems ni calcula el instrumento. Esto no equivale a implementar la valoración completa.

## Cálculos, planes y contenido farmacológico

- Dosis a volumen: regla dimensional con valores ficticios, no selección de dosis. Masa y UI no se convierten entre sí.
- Dilución: C1V1=C2V2 con mezcla ideal; V2 es volumen final total. No reconstitución, compatibilidad, estabilidad ni recomendaciones de administración.
- PAE: documentación libre por etapas, no catálogo NANDA-I/NIC/NOC ni generador diagnóstico.
- Seis fichas farmacológicas iniciales en borrador y sin pautas clínicas. Ahora son editables, pero no se han completado con información inventada.
- Protocolos/apuntes/multimedia: capacidad de almacenar material propio o autorizado, no biblioteca clínica prevalidada.

## Revisión profesional pendiente

Antes del uso asistencial deben verificarse derechos, versión y población, descriptores completos, contraindicaciones/limitaciones del método, concordancia con fichas de referencia, accesibilidad y desempeño en equipos reales. Las pruebas automatizadas verifican reglas matemáticas, datos y navegación; no autorizan uso clínico.

Esta edición no hace búsquedas continuas ni actualiza normativa por sí sola. Un recurso offline requiere un proceso editorial explícito para mantener su contenido.
