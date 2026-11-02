# Guía breve para trabajar con IA en iDreyK 360

## Antes de editar

1. Leer `README.md`; luego el archivo del módulo afectado. No cargar todo el repositorio, todos los XML de QA o todos los recursos en una sola respuesta.
2. Consultar `git status` y preservar cambios preexistentes. La aplicación funcional actual es Android v0.4; no empezar un proyecto web nuevo por una plantilla genérica.
3. Para datos, leer `database/schema.sql` y contrastarlo con `LocalDatabase` en `Repository.kt`. El Kotlin es la implementación ejecutada; el SQL es un espejo verificado.
4. Para alcance clínico, consultar solo la sección pertinente de `docs/ESTADO_FUNCIONAL_v0.3.md` y `research/REVISION_DOCUMENTAL.md`.

## Arquitectura y límites

- Kotlin + Jetpack Compose. UI/navegación en `MainActivity.kt`, `Design.kt` y `Screens*.kt`.
- Operaciones persistentes: `AppViewModel` -> `ContentRepository` -> SQLite/archivos privados. La lógica matemática y la validación permanecen separadas en sus modelos.
- El usuario pidió expresamente cuentas y catálogo remoto en la fase v0.4. Hay conector opcional a Supabase (PostgreSQL/Auth/Storage), pero el APK entregado no tiene proyecto configurado. Mantener lectura local sin cuenta obligatoria. Nunca afirmar conexión real, correo enviado o despliegue sin verificarlos. No crear una app web que sustituya Android.
- No renombrar tablas, paquete Android, versión de datos o clave de firma sin plan de compatibilidad/migración.
- No copiar `AUTO_INCREMENT`, usuarios de administrador o contraseñas de un ejemplo de servidor dentro de esta base SQLite.

## Integración y errores

- Consultas con parámetros enlazados/`selectionArgs` y escrituras con `ContentValues`. Nunca interpolar datos de formularios en SQL. Identificadores dinámicos solo desde listas internas controladas.
- Manejar errores en el límite de la operación asíncrona/ViewModel; propagar desde repositorio cuando corresponda. No ocultar errores con `catch` vacío ni devolver éxito tras una escritura fallida.
- Transacciones con confirmación explícita y cierre en `finally`; cerrar cursores/streams con `use`. Mantener el orden de operaciones y la protección del punto de deshacer.
- Conservar UTF-8, límites agregados, versiones, fuentes y el retorno a borrador al editar contenido.
- No presentar pruebas matemáticas como validación clínica. No inventar monografías, dosis, permisos o revisiones profesionales.

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

Registrar el cambio, su alcance, resultados y pendientes. El historial técnico está en `DESARROLLO.md`; los resultados actuales están en `qa/RESULTADOS_v0.4.md`. `qa/RESULTADOS.md` conserva la evidencia histórica de v0.3. Las pruebas de una sesión anterior no deben describirse como ejecutadas de nuevo sin haberlo hecho.
