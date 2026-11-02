# Texto para la primera carga o una actualización en Google AI Studio

Usar con el selector **Android**. En la primera carga, el hito es `traslado-ia-studio-v1`; en futuras cargas, indicar el nuevo SHA que acompaña cada entrega. No pedir simplemente «haz una app de enfermería».

## Instrucción para copiar

```text
Vas a CONTINUAR un proyecto Android existente, no a crear una aplicación nueva a partir de su descripción.

Fuente canónica: https://github.com/iDreyK-ii/Enfermer-a-app
Rama: desarrollo.
Primera carga: tag traslado-ia-studio-v1.
Si el propietario facilita un SHA de actualización, ese SHA tiene prioridad sobre el tag inicial. Confirma qué commit cargaste; no confundas la rama con la revisión concreta ni inventes un hash.

ANTES DE EDITAR:
1. Comprueba que trabajas en modo Android nativo. Si no puedes importar ese proyecto en ese modo, DETENTE y explica la limitación. No lo conviertas a una app web.
2. Lee AGENTS.md, CONTINUIDAD.json, INICIAR_EN_AI_STUDIO.md y docs/INVENTARIO_FUNCIONAL_ACTUAL.md.
3. Conserva todos los archivos versionados: código, recursos, fuentes/licencias, contenido inicial, tests, backend, plantillas y documentación. No omitas funciones porque no aparezcan en una captura. No sustituyas pantallas funcionales por demos o maquetas.
4. No alteres archivos durante la importación para hacer pasar una comprobación. Necesitamos comparar la copia con el commit canónico. Si no puedes ejecutar el comprobador, permite exportar las fuentes y declara que la paridad sigue pendiente.

INVARIANTES:
- Kotlin y Jetpack Compose, módulo :app, Prototipo B y diseño aprobado inicio 2/ficha 1. No React, Flutter, Expo, React Native, PWA o WebView como reemplazo.
- Mantener identificador de aplicación, sufijo debug, tablas SQLite v2, migraciones y archivos locales. No recrear la DB ni borrar datos para resolver errores.
- Mantener todas las funciones del inventario, datos parciales, fuentes, controles de importación, deshacer, cálculos/validaciones, accesibilidad y movimiento adaptable.
- Mantener Supabase y SQLite. No aprovisionar Firebase para sustituirlos ni volver a aplicar la migración inicial ya desplegada.
- No exigir cuenta para leer y trabajar localmente. OTP, envío y sesión reales están aplazados. No quitar la confirmación del correo ni declarar autenticación real sin probarla.
- No inventar contenido clínico, dosis, licencias o validación profesional. Conservar borradores y límites.
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
