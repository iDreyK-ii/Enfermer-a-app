# Primera validación técnica del proyecto Supabase real

Fecha: 2 de noviembre de 2026. Proyecto `idreyk-360-dev` (`dmxfdvvcvfecqhbbmrlu`), Virginia `us-east-1`, región autorizada expresamente por el propietario tras descartar Brasil. Creación solicitada con plan `free`; no se efectuó un cambio a un plan de pago. La respuesta no incluía el detalle de facturación.

## Ejecutado contra el servicio real

- La consulta inicial devolvió cero proyectos accesibles. Se creó uno y se volvió a listar para confirmar su referencia y estado.
- Auth, DB, REST y Storage informaron `ACTIVE_HEALTHY`.
- Antes de la migración, no existían tablas propias en `public`/`idreyk_private`, ni migraciones registradas.
- Migración real registrada: `20261102110405_inicial_catalogo_seguro_v1`.
- **12/12 comprobaciones SQL verdaderas** sobre permisos, ausencia de concesión editorial a usuarios normales, función de autorización, trigger de publicación, bucket privado y ausencia de cuentas/publicaciones insertadas por esta operación.
- Inspección adicional: **4 tablas con RLS**, **10 políticas** y `search_path=pg_catalog` en la función editorial `SECURITY DEFINER`.
- Bucket `editorial-fuentes`: privado, límite 10 MiB; PDF/JSON/PNG/JPEG permitidos.
- **7/7 comprobaciones HTTPS de solo lectura** usando exclusivamente la clave publicable:

| Control | Respuesta real |
|---|---|
| Salud de Auth | 200, GoTrue |
| Proveedor de correo activo | 200, email habilitado |
| Catálogo publicado | 200, lista vacía |
| Perfiles sin sesión | 401, error PostgreSQL 42501 |
| Borradores sin sesión | 401, error PostgreSQL 42501 |
| Esquema editorial privado | 406, PGRST106: no expuesto |
| Petición sin API key | 401 |

No se enviaron correos, crearon usuarios ni publicaron fichas para simular éxito. Una respuesta vacía del catálogo valida conectividad/lectura, no descarga de contenido clínico ni validación de una sesión.

## Correspondencia de la migración

El historial remoto devolvió una sentencia de **7.942 bytes**, SHA-256 `3c465d79883b47520ad0f05fa3f0aadfcf0a98be64e95d005bf82f0271e49581`.

El archivo versionado contiene los mismos bytes más un salto de línea final (7.943 bytes). Se comparó su hash tras retirar solo ese LF y coincidió. Además, una prueba local compara su cuerpo con el esquema previamente probado, excluyendo comentarios y la transacción exterior. No se debe ejecutar otra vez la migración inicial en este proyecto.

## Ejecutado localmente en esta fase

- **41 pruebas Python**: 15 conversor + 21 backend (9 publicación y 12 comprobador remoto) + 5 guardas.
- **5 comprobaciones SQLite**.
- **17 comprobaciones PostgreSQL/RLS con PGlite**, separadas del servicio real. Usan esquemas Auth/Storage mínimos simulados.
- El comprobador remoto se probó con transportes ficticios y luego con el endpoint real. Por defecto no hace red; solo `--remoto` autoriza sus GET.
- Se verificó la configuración del comando de compilación conectada, **sin ejecutar Gradle ni recompilar**.

## Conservado, no ejecutado de nuevo

Todo el directorio `app/` coincide con `desarrollo-v0.4.1`. El APK entregado sigue con SHA-256 `29799b5b623a2830fdd9ad08e811f7e254060d5729b9e71684d373d9b8bcb204`, sin cambios y sin proyecto incorporado.

Las **133 pruebas Android** y el lint de la entrega v0.4.1 son evidencia histórica de ese APK, no una nueva ejecución en esta fase. Las pruebas de teléfono se pospusieron expresamente hasta después de la sincronización solicitada.

## Pendiente para cerrar autenticación y continuidad

Auth está habilitado, pero sus plantillas observadas todavía enviaban enlaces sin `{{ .Token }}`; no había SMTP propio y `site_url` conservaba localhost. Las instrucciones del propietario están en `docs/SUPABASE_REAL_Y_GITHUB.md`. El conector no expone la edición de plantillas/SMTP, por lo que no se afirma haberlas cambiado.

Faltan recepción real de OTP, verificación de sesión, renovación/cierre en una prueba real, aislamiento entre dos sesiones reales y transferencia de un archivo de prueba a Storage. No se han asignado editores ni creado datos clínicos. No se ha construido un APK conectado, hecho push o ejecutado Actions en GitHub.

Estas comprobaciones no son auditoría exhaustiva de seguridad, garantía de facturación/disponibilidad ni validación clínica o regulatoria.

Evidencias seleccionadas, sin claves administrativas ni contraseñas: `qa/supabase_real_20261102/`. El archivo de configuración pública es intencional y no concede identidad de usuario ni privilegio editorial.
