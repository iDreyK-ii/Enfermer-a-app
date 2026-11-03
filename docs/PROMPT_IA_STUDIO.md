> **Revisión local actual: Base Maestra 2.1 + atlas kawaii / Android 0.8.0-dev (code 9).** No publicada. Usar solo el SHA exacto que el propietario revise y autorice. `base-maestra-v21-mvp` es el checkpoint anterior 0.6.0, no incluye los nuevos PNG. Matriz: `docs/AUDITORIA_ATLAS_KAWAII.md`. No atribuir permisos, revisión profesional o pruebas instaladas inexistentes.

# Texto para la primera carga o una actualización en Google AI Studio

Usar con el selector **Android**. Para una actualización usar únicamente el SHA exacto comunicado y aprobado; el tag `base-maestra-v21-mvp` solo identifica la versión histórica 0.6.0. `traslado-ia-studio-v1` es la base histórica. No pedir simplemente «haz una app de enfermería».

## Instrucción para copiar

```text
Vas a CONTINUAR un proyecto Android existente, no a crear una aplicación nueva a partir de su descripción.

Fuente canónica: https://github.com/iDreyK-ii/Enfermer-a-app
Rama: desarrollo.
Checkpoint anterior: tag base-maestra-v21-mvp, code 7. Borrador local actual: code 9, 0.8.0-dev; esperar el SHA expresamente aprobado y publicado.
Los tags escalas-clinicas-v1 y traslado-ia-studio-v1 son históricos: no cargarlos como esta actualización.
Si el propietario facilita un SHA de actualización, ese SHA tiene prioridad sobre el tag. Confirma qué commit cargaste; no confundas la rama con la revisión concreta ni inventes un hash.

ANTES DE EDITAR:
1. Comprueba que trabajas en modo Android nativo. Si no puedes importar ese proyecto en ese modo, DETENTE y explica la limitación. No lo conviertas a una app web.
2. Lee AGENTS.md, CONTINUIDAD.json, INICIAR_EN_AI_STUDIO.md, docs/INVENTARIO_FUNCIONAL_ACTUAL.md y docs/MVP_BASE_MAESTRA_v21.md; también docs/AUDITORIA_ATLAS_KAWAII.md y qa/atlas_kawaii/RESULTADOS.md (qa/atlas_v22 y qa/RESULTADOS_v0.6.0.md son históricos) y docs/10_ROADMAP_Y_MODULOS_FUTUROS.md.
3. Conserva todos los archivos versionados: código, recursos, fuentes/licencias, contenido inicial, tests, backend, plantillas y documentación. No omitas funciones porque no aparezcan en una captura. No sustituyas pantallas funcionales por demos o maquetas.
4. No alteres archivos durante la importación para hacer pasar una comprobación. Necesitamos comparar la copia con el commit canónico. Si no puedes ejecutar el comprobador, permite exportar las fuentes y declara que la paridad sigue pendiente.

INVARIANTES:
- Kotlin y Jetpack Compose, módulo :app, Prototipo B y diseño aprobado inicio 2/ficha 1. No React, Flutter, Expo, React Native, PWA o WebView como reemplazo.
- Mantener identificador de aplicación, sufijo debug, tablas SQLite v2, migraciones y archivos locales. No recrear la DB ni borrar datos para resolver errores.
- Mantener todas las funciones del inventario, datos parciales, fuentes, controles de importación, deshacer, cálculos/validaciones, accesibilidad y movimiento adaptable.
- Mantener Supabase y SQLite. No aprovisionar Firebase para sustituirlos ni volver a aplicar la migración inicial ya desplegada.
- No exigir cuenta para leer y trabajar localmente. OTP, envío y sesión reales están aplazados. No quitar la confirmación del correo ni declarar autenticación real sin probarla.
- No inventar contenido clínico, dosis, licencias o validación profesional. Conservar borradores y límites. Las 44 fichas de escalas no son 44 calculadoras: mantener estados y permisos. No redibujar caras ni estímulos de las escalas de terceros, ni cambiar las reglas de NV/pendiente; las caritas originales del atlas son decorativas. Los 554 nombres nominales siguen en IDENTIFICADO; altilice permanece ambiguo. Monografías farmacológicas quedan para el final.
- Conservar biblioteca Markdown y sus fuentes, atlas kawaii PNG y metadatos (24 imágenes ilustradas y 24 fichas, sin revisión profesional), higiene y listas 10 institucionales/20 propias, siete casos sintéticos/21 preguntas y todos los campos PAE/SOAPIE/SBAR. No extraer órdenes del anexo editorial como recomendaciones.
- Mantener BigDecimal, guardas de unidades/rangos y factor del equipo obligatorio; borrar resultados obsoletos al editar. Los registros Braden/Morse/Norton/RASS NO son cuestionarios administrados ni interpretación de riesgo.
- Mantener cuaderno que lee v1/v2 y exporta v2, progreso versionado en meta y los datos locales anteriores. No declarar sincronización personal automática.
- Las 235 pruebas JVM y diez renders de 0.6.0 son históricos; consultar el QA corriente. Los renders Robolectric no son instalación física. En el cierre histórico 0.7 / v2.2 se ejecutaron 2/2 pruebas PDF instrumentadas sobre Android API28, con salidas reales archivadas, y ocho capturas instaladas en emulador. Son pruebas acotadas, no validación de servicio de impresión, FPS, teléfono físico, sesión remota ni Google.
- No solicitar ni pegar claves administrativas, contraseña de DB o firma privada en el código, chats o repositorio.

PRIMER OBJETIVO:
Cargar exactamente esa revisión y compilar su perfil local, con IDREYK_SUPABASE_URL e IDREYK_SUPABASE_PUBLISHABLE_KEY vacíos. No cambiar el diseño, dependencias o arquitectura para conseguir una vista previa. Si hay un error, devuelve el mensaje, archivo y causa propuesta antes de cambiar código.

ENTREGA TU INFORME:
- Commit realmente cargado y modo Android utilizado.
- Archivos faltantes, modificados o adicionales, si existen.
- Comandos ejecutados y resultado real de compilación/pruebas; no inventar pruebas no ejecutadas.
- Paquete, versión y certificado del APK, si se generó.
- Limitaciones de importación/exportación de la cuenta y lo que aún no se verificó.

Una firma de AI Studio no se asume equivalente a la firma conservada. No recomendar desinstalar ni borrar datos. No publicar en Play, activar servicios de pago ni sobrescribir GitHub. Las mejoras se revisan localmente y se muestran al propietario antes de publicar. No interpretar una solicitud de mejoras como aprobación para subirlas. Solo tras su aprobación expresa se publicará una revisión nueva; después cargarás exactamente el SHA que indique el propietario.
```

Este texto es una instrucción de preservación, no una prueba de que la carga haya sucedido. La aprobación se basa en comparación de archivos, pruebas y revisión del APK, no únicamente en la respuesta del agente.
