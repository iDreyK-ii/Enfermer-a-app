# Guía breve para trabajar con IA en iDreyK 360

## Antes de editar

1. Leer `README.md`, `CONTINUIDAD.json` e `INICIAR_EN_AI_STUDIO.md` antes de un traslado; luego el archivo del módulo afectado. No cargar todo el repositorio, todos los XML de QA o todos los recursos en una sola respuesta.
2. Consultar `git status` y preservar cambios preexistentes. La aplicación funcional actual es Android v0.5.0; no empezar un proyecto web nuevo por una plantilla genérica.
3. Para datos, leer `database/schema.sql` y contrastarlo con `LocalDatabase` en `Repository.kt`. El Kotlin es la implementación ejecutada; el SQL es un espejo verificado.
4. Para alcance clínico, consultar solo la sección pertinente de `docs/ESTADO_FUNCIONAL_v0.3.md` y `research/REVISION_DOCUMENTAL.md`.

## Arquitectura y límites

- Kotlin + Jetpack Compose. UI/navegación en `MainActivity.kt`, `Design.kt` y `Screens*.kt`.
- Operaciones persistentes: `AppViewModel` -> `ContentRepository` -> SQLite/archivos privados. La lógica matemática y la validación permanecen separadas en sus modelos.
- El usuario pidió expresamente cuentas y catálogo remoto en la fase v0.4. El proyecto Supabase real `idreyk-360-dev` ya existe en `us-east-1` y tiene migración/RLS verificados. El APK entregado v0.4.1 sigue sin proyecto incorporado. Leer `docs/SUPABASE_REAL_Y_GITHUB.md`: no repetir el SQL inicial; correo/sesión real y compilación conectada siguen pendientes. Mantener lectura local sin cuenta obligatoria. Nunca afirmar conexión real, correo enviado o despliegue sin verificarlos. No crear una app web que sustituya Android.
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

Registrar el cambio, su alcance, resultados y pendientes. El historial técnico está en `DESARROLLO.md`; la fase de servidor está en `qa/SUPABASE_REAL_2026-11-02.md`; los últimos resultados del APK están en `qa/RESULTADOS_v0.4.1.md`. `qa/RESULTADOS.md` conserva la evidencia histórica de v0.3. Las pruebas de una sesión anterior no deben describirse como ejecutadas de nuevo sin haberlo hecho.


## Traslado a Google AI Studio y actualizaciones

- Fuente canónica: este repositorio, rama `desarrollo`. Cargar el commit exacto de cada entrega; no usar «último» sin identificarlo. Leer `docs/INVENTARIO_FUNCIONAL_ACTUAL.md` y `docs/PROMPT_IA_STUDIO.md`.
- Importar el proyecto completo en modo Android. No regenerarlo desde capturas, convertirlo en web o reemplazar SQLite/Supabase por Firebase. No omitir recursos, fuentes/licencias, módulos funcionales, validaciones, backend, pruebas o documentación.
- Antes de modificar una copia importada, contrastar sus archivos con `scripts/verificar_traslado.py` ejecutado desde el Git confiable. Si AI Studio no permite importar/verificar/exportar, informar la limitación; no afirmar paridad por una vista previa.
- Flujo: cambios revisados aquí → GitHub/CI → recarga por el propietario en AI Studio. No hacer cambios divergentes o push automático desde allí; devolver una propuesta/diff para integrarla primero.
- OTP y sesión real fueron aplazados por el propietario tras un problema de acceso al panel. Continuar perfil local sin cuenta obligatoria; no desactivar confirmación ni declarar cuentas listas. El cliente remoto y el servidor se conservan.
- Cada cambio funcional requiere pruebas, actualización coherente de `CONTINUIDAD.json`, versión/migración si procede, commit y comprobación de remoto/CI. No actualizar un contrato o prueba solo para ocultar una regresión.
- Una APK de Google/CI no hereda la firma privada. Verificar el certificado antes de actualizar; nunca desinstalar o borrar datos como solución. La clave privada permanece fuera del repositorio y del prompt.


## Tests clínicos y escalas (v0.5.0)

- Leer `docs/ESCALAS_CLINICAS_v0.5.0.md` y la ficha concreta en `research/escalas/REVISION_2026-11-02.md`. No confundir 44 fichas con 44 calculadoras o con todos los instrumentos existentes.
- Fuente editorial: `research/escalas/catalogo_fuente.json`; regenerar el asset con `python3 research/escalas/compilar_catalogo.py`. Python y Kotlin comprueban coherencia; no equivalen a validación clínica.
- Preservar la distinción entre cero, pendiente y no valorable de CADA instrumento. Glasgow NV no suma; AMT4/atención 4AT no valorables sí puntúan 2. No reutilizar indiscriminadamente una regla entre escalas.
- Respetar licencia/atribución 4AT y señalar adaptación. No redibujar caras, estímulos ni tablas normalizadas para ajustarlos a la paleta. No reproducir Braden/Morse/FPS-R/Wong-Baker/MoCA sin resolver derechos de la versión/uso.
- Solo datos ficticios, sin respuestas identificables ni subida automática de prácticas. No añadir recomendaciones farmacológicas por puntaje. El propietario dejó farmacología para el final y no solicitó PDFs en esta fase.
