# Guía breve para trabajar con IA en iDreyK 360

## Antes de editar

1. Leer `README.md`, `CONTINUIDAD.json` e `INICIAR_EN_AI_STUDIO.md` antes de un traslado; luego el archivo del módulo afectado. No cargar todo el repositorio, todos los XML de QA o todos los recursos en una sola respuesta.
2. Consultar `git status` y preservar cambios preexistentes. La revisión local actual es Android v0.7.0 / atlas v2.2, sobre Base Maestra 2.1; no empezar un proyecto web nuevo por una plantilla genérica.
3. Para datos, leer `database/schema.sql` y contrastarlo con `LocalDatabase` en `Repository.kt`. El Kotlin es la implementación ejecutada; el SQL es un espejo verificado.
4. Para alcance clínico, consultar la matriz de `docs/MVP_BASE_MAESTRA_v21.md` y la investigación pertinente, conservando los límites históricos de `research/REVISION_DOCUMENTAL.md`.

## Arquitectura y límites

- Kotlin + Jetpack Compose. UI/navegación en `MainActivity.kt`, `Design.kt` y `Screens*.kt`.
- Operaciones persistentes: `AppViewModel` -> `ContentRepository` -> SQLite/archivos privados. La lógica matemática y la validación permanecen separadas en sus modelos.
- El usuario pidió expresamente cuentas y catálogo remoto en la fase v0.4. El proyecto Supabase real `idreyk-360-dev` ya existe en `us-east-1` y tiene migración/RLS verificados. El APK local 0.6.0 se entrega sin configuración de nube. Leer `docs/SUPABASE_REAL_Y_GITHUB.md`: no repetir el SQL inicial; correo/sesión real y compilación conectada siguen pendientes. Mantener lectura local sin cuenta obligatoria. Nunca afirmar conexión real, correo enviado o despliegue sin verificarlos. No crear una app web que sustituya Android.
- No renombrar tablas, paquete Android, versión de datos o clave de firma sin plan de compatibilidad/migración.
- No copiar `AUTO_INCREMENT`, usuarios de administrador o contraseñas de un ejemplo de servidor dentro de esta base SQLite.

## Integración y errores

- Consultas con parámetros enlazados/`selectionArgs` y escrituras con `ContentValues`. Nunca interpolar datos de formularios en SQL. Identificadores dinámicos solo desde listas internas controladas.
- Manejar errores en el límite de la operación asíncrona/ViewModel; propagar desde repositorio cuando corresponda. No ocultar errores con `catch` vacío ni devolver éxito tras una escritura fallida.
- Transacciones con confirmación explícita y cierre en `finally`; cerrar cursores/streams con `use`. Mantener el orden de operaciones y la protección del punto de deshacer.
- Conservar UTF-8, límites agregados, versiones, fuentes y el retorno a borrador al editar contenido.
- No presentar pruebas matemáticas como validación clínica. No inventar monografías, dosis, permisos o revisiones profesionales.

## Importación y cambios v0.4.1

- Mantener la comparación de campos, aceptación de reemplazos y comprobación de la huella revisada dentro de la transacción. No aplicar objetos de presentación mutables en lugar del texto revisado.
- Mantener JSON acotado en profundidad, límites previos, datos parciales y deshacer. No interpretar una comparación estructural como evaluación clínica.
- No retirar accesibilidad/reducción de movimiento para añadir efectos. Las tarjetas se adaptan al ancho/fuente; conservar la altura dinámica de bienvenida.
- `scripts/comprobar.sh --todo` exige Git del proyecto y fuerza nube vacía. Un cliente conectado necesita compilación y pruebas de integración separadas.
- Destino autorizado: `https://github.com/iDreyK-ii/Enfermer-a-app`, público, rama `desarrollo`; ver `docs/GITHUB_DESTINO.md`. Verificar el run de CI del commit correspondiente: no inferirlo de resultados históricos. No cambiar de destino ni forzar un push. No dar por compatible la firma temporal de CI con los APK entregados.

## Comprobación y entrega

- SQLite local: `python3 database/verificar_esquema.py`. PostgreSQL/Supabase es otro esquema: `backend/supabase/schema.sql`; sus pruebas se ejecutan con `npm test --prefix backend`. No aplicar un SQL al motor equivocado.
- Catálogo: v0.4 lee esquemas 1/2/3 y exporta 3. Conservar las 30 secciones, los 17 conceptos del PDF y los datos anteriores. Las fichas parciales no deben transformarse en una declaración de seguridad clínica.
- Cliente: solo URL y clave publicable. Nunca claves secretas en APK, commits, logs o ZIP. Mantener RLS, tokens cifrados, límites, integridad, vistas previas y confirmación de importación. No subir notas/PAE/favoritos automáticamente.
- Cambios del APK: compilar, ejecutar pruebas y lint con `docs/COMPILAR_Y_PROBAR.md`; informar exactamente qué se ejecutó y qué falta.
- No aumentar el paralelismo de JVM ni ejecutar un emulador pesado simultáneamente en un entorno con poca memoria.
- Mantener el Prototipo B. No agregar dependencias o actualizar versiones arbitrariamente para resolver un error no analizado.
- Versionar fuentes, pruebas y documentación; excluir cachés, claves, `.env`, `local.properties` y APK/AAB del repositorio.
- Refrescar el bundle/ZIP cuando se entregue código. Verificar recuperación por clon y comparar archivos; no afirmar que existe un remoto o un respaldo externo no comprobado.

## Continuidad

Registrar el cambio, su alcance, resultados y pendientes. El historial técnico está en `DESARROLLO.md`; la fase de servidor está en `qa/SUPABASE_REAL_2026-11-02.md`; los resultados actuales están en `qa/atlas_v22/RESULTADOS.md` (0.6.0 conserva evidencia histórica). `qa/RESULTADOS.md` conserva la evidencia histórica de v0.3. Las pruebas de una sesión anterior no deben describirse como ejecutadas de nuevo sin haberlo hecho.


## Traslado a Google AI Studio y actualizaciones

- Fuente canónica: este repositorio, rama `desarrollo`. Cargar el commit exacto de cada entrega; no usar «último» sin identificarlo. Leer `docs/INVENTARIO_FUNCIONAL_ACTUAL.md` y `docs/PROMPT_IA_STUDIO.md`.
- Importar el proyecto completo en modo Android. No regenerarlo desde capturas, convertirlo en web o reemplazar SQLite/Supabase por Firebase. No omitir recursos, fuentes/licencias, módulos funcionales, validaciones, backend, pruebas o documentación.
- Antes de modificar una copia importada, contrastar sus archivos con `scripts/verificar_traslado.py` ejecutado desde el Git confiable. Si AI Studio no permite importar/verificar/exportar, informar la limitación; no afirmar paridad por una vista previa.
- Flujo actualizado por el propietario: revisión visual aquí → mejoras locales y pruebas → mostrar diferencias/resultado y límites → aprobación expresa para publicar → GitHub/CI → recarga del SHA exacto en AI Studio. No hacer cambios divergentes o push automático desde allí; devolver una propuesta/diff para integrarla primero.
- No hacer push, publicar etiquetas remotas o actualizar GitHub sin esa aprobación expresa de la revisión. «Continuar», pedir una revisión o solicitar mejoras NO autoriza publicarlas. Los commits locales de trabajo no equivalen a publicación ni aprobación. La base b463c580f6e965d971116ef593df0059cb105c86 ya se publicó antes de esta instrucción: no ocultarlo ni revertir/recrear la historia sin autorización.
- Distinguir APK de referencia, cambios locales pendientes y revisión publicada. No presentar un ZIP/APK de un SHA anterior como si incluyera mejoras locales nuevas. Mostrar renders como renders, no como ejecución del APK.
- OTP y sesión real fueron aplazados por el propietario tras un problema de acceso al panel. Continuar perfil local sin cuenta obligatoria; no desactivar confirmación ni declarar cuentas listas. El cliente remoto y el servidor se conservan.
- Cada cambio funcional requiere pruebas, actualización coherente de `CONTINUIDAD.json`, versión/migración si procede, commit y comprobación de remoto/CI. No actualizar un contrato o prueba solo para ocultar una regresión.
- Una APK de Google/CI no hereda la firma privada. Verificar el certificado antes de actualizar; nunca desinstalar o borrar datos como solución. La clave privada permanece fuera del repositorio y del prompt.


## Tests clínicos y escalas (v0.5.0)

- Leer `docs/ESCALAS_CLINICAS_v0.5.0.md` y la ficha concreta en `research/escalas/REVISION_2026-11-02.md`. No confundir 44 fichas con 44 calculadoras o con todos los instrumentos existentes.
- Fuente editorial: `research/escalas/catalogo_fuente.json`; regenerar el asset con `python3 research/escalas/compilar_catalogo.py`. Python y Kotlin comprueban coherencia; no equivalen a validación clínica.
- Preservar la distinción entre cero, pendiente y no valorable de CADA instrumento. Glasgow NV no suma; AMT4/atención 4AT no valorables sí puntúan 2. No reutilizar indiscriminadamente una regla entre escalas.
- Respetar licencia/atribución 4AT y señalar adaptación. No redibujar caras, estímulos ni tablas normalizadas para ajustarlos a la paleta. No reproducir Braden/Morse/FPS-R/Wong-Baker/MoCA sin resolver derechos de la versión/uso.
- Solo datos ficticios, sin respuestas identificables ni subida automática de prácticas. No añadir recomendaciones farmacológicas por puntaje. El propietario dejó farmacología para el final y no solicitó PDFs en esta fase.


## Base Maestra 2.1 / Android 0.6.0

- Leer `docs/MVP_BASE_MAESTRA_v21.md` y `docs/10_ROADMAP_Y_MODULOS_FUTUROS.md`. No confundir especificación 2.1 con versionName Android.
- Entrada íntegra leída: 13 secciones y anexos A/B, archivada en `research/base_maestra_v21/BASE_EDITORIAL_NO_CLINICA.md`; no empaquetarla como guía clínica ni transformar órdenes históricas en recomendaciones.
- Generadores: `crear_contenido.py` y `compilar_mvp.py`; verificar `compilar_mvp.py --check` y las pruebas de regeneración/procedencia. Los 554 maestros/737 apariciones siguen IDENTIFICADO; altilice no resuelto.
- Preservar aritmética decimal/unidades/factor obligatorio, progreso versionado, atlas accesible, higiene, dos listas con distinta autoría y casos sintéticos. Mantener los 32 campos nuevos y los antiguos del PAE; cuaderno lee 1/2 y exporta 2.
- Braden/Morse/Norton/RASS: solo registros externos con ámbito/rango, no cuestionarios, interpretación ni permiso clínico inferido.
- Histórico 0.6: 235 JVM/10 renders; PDF instrumentado aún no ejecutado entonces. En v2.2 se ejecutaron 2/2 pruebas PDF reales en emulador API28; salidas y ocho capturas instaladas están en qa/atlas_v22. El cierre corriente tiene 252 JVM/17 renders. No confundirlo con hardware físico ni con CI remoto nuevo.
- No reconstruir pantallas desde el HTML del desglose: contiene imágenes del render nativo para revisión, no una implementación alternativa.

## Atlas v2.2 / revisión local Android 0.7.0

- Leer `docs/AUDITORIA_ATLAS_v22.md`, `research/atlas_v22/REVISION_TECNICA.md` y el inventario de 24 PNG. No reducir el alcance pedido a los cinco recursos integrados ni convertir los otros diecinueve en FUTURO.
- PNG RGBA individual y JSON por recurso. No resucitar `AtlasShape`, dibujos de palitos o manos robóticas. Ausencia/fallo se muestra como recurso pendiente.
- Metadatos nativos, no texto generado dentro del PNG. Partes ocultas no admiten hotspots ni quiz. Ningún JSON atribuye aprobación profesional. Declarar resolución nativa vs ampliación.
- Quiz `atlas-v22.1` guarda en `learning:` sin migración; lecturas/casos conservan `2.1-mvp.1`. El éxito de cada respuesta se confirma en la ficha, sin Snackbar que tape Siguiente. No silenciar los errores.
- Capturas Robolectric no son APK instalado. Consultar el informe corriente para los intentos de emulador y evidencia instalada, si existe. No reciclar CI/capturas de 0.6.0 como pruebas del código actual.
- No push ni despliegue sin la revisión y autorización explícitas del usuario. El último publicado sigue siendo b463c580; los cambios actuales son locales.
