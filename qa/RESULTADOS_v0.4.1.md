# Comprobación del pulido v0.4.1 — 2026-11-01

| Comprobación | Resultado |
|---|---|
| Android/JVM + Compose/Robolectric | **133 pruebas, 0 fallos, 0 errores, 0 omitidas** |
| Conversor XLSX | **15 pruebas** |
| Publicación / formato3 | **9 pruebas** |
| Guardas de repositorio | **5 pruebas** |
| Espejo SQLite | **5 pruebas** |
| PostgreSQL/RLS local con PGlite | **17 comprobaciones** |
| Ensamblado debug | Correcto; también recompuesto tras limpiar `app/build` |
| Android lint | **0 errores, 24 advertencias** (5 GradleDependency, 19 UnusedResources) |
| Workflow | Sintaxis/expresiones correctas con actionlint1.7.12; **sin ejecución en GitHub** |
| Firma, versión y permisos APK | Verificados contra el APK real |
| Conservación | 84 recursos/assets sin cambios; APK v0.2/v0.3/v0.4 sin cambios |

## Qué aportan las nuevas pruebas

- Vista previa detecta datos que se vaciarían y ediciones locales; exige aceptación antes de reemplazar.
- Una vista previa desactualizada no sobrescribe una edición posterior ni destruye su punto de deshacer.
- La importación usa el texto revisado, no objetos JSON mutables de presentación.
- Las altas sin reemplazos siguen funcionando sin una aceptación innecesaria.
- JSON excesivamente anidado se rechaza en los cuatro puntos de entrada comprobados; las llaves/corchetes dentro del texto no se alteran.
- Bienvenida con escala de fuente2 sin altura fija que recorte el saludo; cuadrícula a una columna en ese caso, con navegación funcional.
- Campo deshabilitado durante escritura; confirmación y paginación de importaciones en UI.
- Advertencia al abandonar una ficha: continuar conserva el texto y descartar no lo guarda silenciosamente.
- Estado de navegación correcto al abrir pacientes ficticios.
- Política de escala táctil: la reducción de movimiento prevalece.

Las pruebas no comprueban una interpretación clínica de los campos ni equivalencia visual píxel a píxel con las maquetas.

## Cómo se ejecutó

Se realizó una ejecución completa de las 133 pruebas después de compilar. Se probó también `scripts/comprobar.sh --todo`, incluyendo Python, PostgreSQL, compilación, pruebas, lint e inspección de APK. Después se limpió únicamente `app/build` y se recompuso con el mismo script: Gradle reutilizó tareas de caché válidas y el APK tuvo **el mismo SHA-256**.

Evidencia en `qa/v041/`: XML por suite, primera ejecución Android, reconstrucción, logs del script, lint, metadatos y procedencia del validador de workflow. En las copias de logs se normalizaron únicamente espacios al final de línea. Los avisos de dos constantes Android TRIM_MEMORY obsoletas continúan. La biblioteca `libandroidx.graphics.path.so` se empaqueta sin strip; no impidió ensamblar.

Al reforzar la prueba de dimensiones se corrigió una referencia de prueba a una propiedad `height` no disponible en ese tipo de rectángulo; se compara `bottom - top`. No se ocultó ni se deshabilitó la prueba.

## APK entregado

- `Enfermeria_iDreyK_360_v0.4.1_debug.apk`
- `com.idreyk.enfermeria360.dev`, versionCode5, `0.4.1-dev-debug`.
- Android mínimo26, objetivo35; tamaño **9.455.430 bytes**.
- SHA-256: `29799b5b623a2830fdd9ad08e811f7e254060d5729b9e71684d373d9b8bcb204`.
- Un firmante; certificado conservado: `d6874c68417eefb1b1b902bf82ba4ce7d8734e47877e12bd1733eb498f0e522b`.
- Solo INTERNET y el permiso de firma interno de AndroidX en el APK combinado. Sin contactos, cámara, cuentas del sistema ni almacenamiento general.
- BuildConfig revisado con URL y clave publicable cloud vacías. No se incorporaron claves administrativas.

## Pendiente, no confundirse con estas pruebas

No se conectó/sincronizó un repositorio remoto, no se ejecutó Actions en GitHub, no se desplegó Supabase ni se comprobaron cuentas/correos/Storage reales. PGlite usa un motor PostgreSQL local con Auth/Storage mínimos simulados.

No se probó instalación/actualización física, hardware Keystore, FPS/consumo ni varias gamas de teléfono. Revisar también rotación/muerte de proceso y formularios muy extensos: no hay autosave garantizado de contenido sin guardar. Guardar fichas parciales sigue siendo explícito.

No hay validación clínica, licencia general de publicación del proyecto ni autorización de uso asistencial. No se cambió el contenido clínico inicial: permanece en borrador. La firma/compilación y las pruebas locales no equivalen a una edición release o certificación de producción.
