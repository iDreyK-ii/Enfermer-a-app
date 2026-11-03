> **Bloque actual: Base Maestra 2.1 / Android 0.6.0-dev (code 7).** Usar el SHA exacto de la entrega o `base-maestra-v21-mvp`. Los tags de escalas/traslado son históricos. La matriz de pendientes es `MVP_BASE_MAESTRA_v21.md`. No presentar formularios bloqueados como disponibles ni impresión/PDF como verificados.

# Texto para la primera carga o una actualización en Google AI Studio

Usar con el selector **Android**. Para este bloque usar `base-maestra-v21-mvp`, o preferiblemente el SHA exacto comunicado en la entrega. `traslado-ia-studio-v1` es la base histórica. No pedir simplemente «haz una app de enfermería».

## Instrucción para copiar

```text
Vas a CONTINUAR un proyecto Android existente, no a crear una aplicación nueva a partir de su descripción.

Fuente canónica: https://github.com/iDreyK-ii/Enfermer-a-app
Rama: desarrollo.
Actualización Base Maestra: tag base-maestra-v21-mvp (comprobar que exista); Android code 7, 0.6.0-dev.
Los tags escalas-clinicas-v1 y traslado-ia-studio-v1 son históricos: no cargarlos como esta actualización.
Si el propietario facilita un SHA de actualización, ese SHA tiene prioridad sobre el tag. Confirma qué commit cargaste; no confundas la rama con la revisión concreta ni inventes un hash.

ANTES DE EDITAR:
1. Comprueba que trabajas en modo Android nativo. Si no puedes importar ese proyecto en ese modo, DETENTE y explica la limitación. No lo conviertas a una app web.
2. Lee AGENTS.md, CONTINUIDAD.json, INICIAR_EN_AI_STUDIO.md, docs/INVENTARIO_FUNCIONAL_ACTUAL.md y docs/MVP_BASE_MAESTRA_v21.md; también qa/RESULTADOS_v0.6.0.md y docs/10_ROADMAP_Y_MODULOS_FUTUROS.md.
3. Conserva todos los archivos versionados: código, recursos, fuentes/licencias, contenido inicial, tests, backend, plantillas y documentación. No omitas funciones porque no aparezcan en una captura. No sustituyas pantallas funcionales por demos o maquetas.
4. No alteres archivos durante la importación para hacer pasar una comprobación. Necesitamos comparar la copia con el commit canónico. Si no puedes ejecutar el comprobador, permite exportar las fuentes y declara que la paridad sigue pendiente.

INVARIANTES:
- Kotlin y Jetpack Compose, módulo :app, Prototipo B y diseño aprobado inicio 2/ficha 1. No React, Flutter, Expo, React Native, PWA o WebView como reemplazo.
- Mantener identificador de aplicación, sufijo debug, tablas SQLite v2, migraciones y archivos locales. No recrear la DB ni borrar datos para resolver errores.
- Mantener todas las funciones del inventario, datos parciales, fuentes, controles de importación, deshacer, cálculos/validaciones, accesibilidad y movimiento adaptable.
- Mantener Supabase y SQLite. No aprovisionar Firebase para sustituirlos ni volver a aplicar la migración inicial ya desplegada.
- No exigir cuenta para leer y trabajar localmente. OTP, envío y sesión reales están aplazados. No quitar la confirmación del correo ni declarar autenticación real sin probarla.
- No inventar contenido clínico, dosis, licencias o validación profesional. Conservar borradores y límites. Las 44 fichas de escalas no son 44 calculadoras: mantener estados y permisos. No redibujar caras ni estímulos, ni cambiar las reglas de NV/pendiente. Los 554 nombres nominales siguen en IDENTIFICADO; altilice permanece ambiguo. Monografías farmacológicas quedan para el final.
- Conservar biblioteca Markdown y sus fuentes, atlas vectorial de ocho dispositivos, higiene y listas 10 institucionales/20 propias, siete casos sintéticos/21 preguntas y todos los campos PAE/SOAPIE/SBAR. No extraer órdenes del anexo editorial como recomendaciones.
- Mantener BigDecimal, guardas de unidades/rangos y factor del equipo obligatorio; borrar resultados obsoletos al editar. Los registros Braden/Morse/Norton/RASS NO son cuestionarios administrados ni interpretación de riesgo.
- Mantener cuaderno que lee v1/v2 y exporta v2, progreso versionado en meta y los datos locales anteriores. No declarar sincronización personal automática.
- Las 235 pruebas JVM pasadas y 10 renders API35 no son instalación física. Las dos pruebas PDF instrumentadas compilan, pero NO se han ejecutado; no inventar un PDF exportado, FPS, sesión real ni verificación Google.
- No solicitar ni pegar claves administrativas, contraseña de DB o firma privada en el código, chats o repositorio.

PRIMER OBJETIVO:
Cargar exactamente esa revisión y compilar su perfil local, con IDREYK_SUPABASE_URL e IDREYK_SUPABASE_PUBLISHABLE_KEY vacíos. No cambiar el diseño, dependencias o arquitectura para conseguir una vista previa. Si hay un error, devuelve el mensaje, archivo y causa propuesta antes de cambiar código.

ENTREGA TU INFORME:
- Commit realmente cargado y modo Android utilizado.
- Archivos faltantes, modificados o adicionales, si existen.
- Comandos ejecutados y resultado real de compilación/pruebas; no inventar pruebas no ejecutadas.
- Paquete, versión y certificado del APK, si se generó.
- Limitaciones de importación/exportación de la cuenta y lo que aún no se verificó.

Una firma de AI Studio no se asume equivalente a la firma conservada. No recomendar desinstalar ni borrar datos. No publicar en Play, activar servicios de pago ni sobrescribir GitHub. Las mejoras se revisan en el repositorio canónico; después cargarás la nueva revisión exacta que el propietario indique.
```

Este texto es una instrucción de preservación, no una prueba de que la carga haya sucedido. La aprobación se basa en comparación de archivos, pruebas y revisión del APK, no únicamente en la respuesta del agente.
