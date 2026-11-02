# Servidor administrado — Supabase / PostgreSQL

La petición actual autoriza cuentas y actualización remota del catálogo. Se ha añadido el conector Android y este esquema/configuración; **no se ha creado ni conectado un proyecto real**. El APK entregado no contiene URL/clave de ningún servidor y muestra «Nube sin configurar». No crea cuentas ficticias ni finge una descarga.

No es otro aplicativo web ni un servidor Node. Node se usa únicamente para probar SQL/RLS con PostgreSQL en WebAssembly.

## Qué base guarda cada cosa

| Ubicación | Contenido |
|---|---|
| SQLite `idreyk_local.db` del teléfono | Catálogo descargado/editado, favoritos, práctica, cuaderno y metadatos de archivos. Continúa esquema local v2. |
| Supabase Auth / `auth.users` | Identidad y correo verificado; creación/acceso por código de correo. No contraseñas propias en SQLite. |
| PostgreSQL `public.profiles` | Perfil mínimo preparado con RLS por propietario. La interfaz actual conserva el nombre local: no sincroniza ese nombre automáticamente. |
| PostgreSQL `public.catalog_drafts` | Fichas editoriales privadas, con JSONB flexible. Solo editores autorizados. |
| PostgreSQL `public.catalog_releases` | Paquetes de contenido versionados e inmutables; lectura pública solo cuando están publicados. |
| `idreyk_private.editors` | Permisos editoriales asignados por el operador, nunca desde los metadatos editables de la cuenta. |
| Supabase Storage `editorial-fuentes` | PDF, JSON e imágenes fuente, privados. Subir un PDF no lo publica ni lo valida. |
| Android Keystore + preferencias privadas | Token de sesión cifrado AES-GCM; nunca contraseña, código OTP o clave administrativa en texto plano. |

Supabase recomienda claves publicables en clientes y RLS/permisos mínimos; las claves secretas eluden RLS y solo pertenecen al servidor/operador. [1](https://supabase.com/docs/guides/database/secure-data) [2](https://supabase.com/docs/guides/getting-started/api-keys)

## Configurar un proyecto propio

1. Crear el proyecto en Supabase bajo una cuenta del responsable. Elegir conscientemente plan, región, privacidad y copias. Esta entrega no acepta costes ni elige residencia de datos por ti.
2. En un **proyecto nuevo**, ejecutar `backend/supabase/schema.sql` en SQL Editor. Es una migración inicial para Supabase, no SQLite y no un script para repetir sobre tablas existentes. No borra tablas ni contiene usuarios/contraseñas de muestra.
3. Habilitar Email en Auth y configurar envío de correo/SMTP para los usuarios previstos. El remitente de prueba puede tener restricciones; no afirmar que envía a cualquier persona sin probarlo.
4. Configurar los correos de Magic Link/OTP y confirmación de alta para mostrar `{{ .Token }}`. La app pide código, no abre enlaces mágicos ni usa WebView. Supabase documenta que solicitar OTP puede crear la cuenta y que la verificación recibe correo, token y tipo `email`. [3](https://supabase.com/docs/guides/auth/auth-email-passwordless)

   ```html
   <h2>Tu código de iDreyK 360</h2>
   <p>Introduce este código en la aplicación: <strong>{{ .Token }}</strong></p>
   <p>Si no lo solicitaste, ignora el mensaje.</p>
   ```

5. Revisar expiración de códigos, límites de envío, controles antiabuso y política de privacidad. El cliente impone una espera orientativa de 60 segundos; la protección real debe estar en el servidor. No hay integración CAPTCHA móvil en esta entrega: si se exige desde Auth, debe integrarse antes de una publicación abierta.
6. Copiar **solo URL del proyecto y clave publicable** en las variables de compilación indicadas abajo. Nunca una clave `sb_secret_…`, `service_role`, contraseña PostgreSQL o secreto de firma JWT.
7. Probar alta, recepción de correo, código vencido/erróneo, reentrada, renovación de sesión, cierre local y pérdida de red con un proyecto controlado. Registrar los resultados antes de usar cuentas reales públicamente.

## Compilar el cliente conectado

Desde la raíz del proyecto, con JDK/SDK preparados:

```sh
cp -n .env.example .env
# Editar las rutas, IDREYK_SUPABASE_URL e IDREYK_SUPABASE_PUBLISHABLE_KEY.
set -a
. ./.env
set +a
./gradlew --no-daemon --max-workers=1 :app:assembleDebug \
  '-Dorg.gradle.jvmargs=-Xmx640m -XX:MaxMetaspaceSize=384m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
```

Sin ambas variables, no hay llamadas al servidor. Con ambas, se usan HTTPS y claves publicables, sin seguir redirecciones. El permiso INTERNET ya está declarado en v0.4; no hay permiso de contactos, cuentas Android, cámara ni almacenamiento general.

La clave publicable viaja dentro del APK: ocultarla no protege una base. **Aplicar y probar RLS antes de distribuir una compilación conectada.** `profiles` es por usuario; el catálogo publicado es legible sin obligar a registrarse. No se suben notas, planes, favoritos o medios privados.

## Dónde subir la información y cómo aparece

**Lugar inicial: el panel privado [Supabase Studio](https://supabase.com/dashboard)**, no una carpeta pública ni un portal editorial nuevo ya desarrollado.

1. Completar `content-tools/Plantilla_farmacos_v3.xlsx` o el JSON equivalente. Se permiten fichas parciales en borrador. Revisar las fuentes y permisos.
2. Subir documentos fuente a Storage → `editorial-fuentes`, si corresponde. Registrar borradores en `catalog_drafts` desde Table Editor. Para habilitar un editor de la app/portal futuro, el operador debe obtener primero su UUID real de Auth y asignarlo en `idreyk_private.editors`; no usar UUID inventados ni abrir permisos a todos.
3. Generar el paquete con el conversor. Para el canal de publicación, usar `id_paquete: catalogo-oficial`, versión creciente, formato 3 y metadatos editoriales reales. No convertir un respaldo privado en publicación sin revisión.
4. La publicación requiere `revision: declarada_por_autor`, revisor, fecha, fuentes y licencia. Esto documenta una declaración, no prueba exactitud clínica. Los borradores iniciales no pueden publicarse por este canal.
5. Validar localmente. Solo cuando haya revisión y autorización, enviar/publicar con la herramienta privada.

```sh
python3 -m pip install -r content-tools/requirements.txt
python3 content-tools/convertir_plantilla.py content-tools/Plantilla_farmacos_v3.xlsx catalogo.json
# Editar el paquete editorial y sus metadatos reales antes de continuar.
python3 backend/publicar_paquete.py catalogo.json

# SOLO OPERADOR: preparar backend/.env con variables privadas, sin compartirlo.
set -a
. ./backend/.env
set +a
# Escribir una revisión todavía no visible:
python3 backend/publicar_paquete.py catalogo.json --enviar
# O crear una NUEVA versión ya revisada y visible:
python3 backend/publicar_paquete.py catalogo-revisado.json --enviar --publicar
```

Sin `--enviar` no hay red. No publicar dos archivos con la misma versión: la revisión es inmutable. El servidor puede cambiar el indicador de publicación desde su panel, pero no reescribir su contenido/versionado. La herramienta no muestra respuestas que puedan incluir claves/tokens.

En Android: **Inicio → Actualizar biblioteca → Buscar actualización de contenido**. La app comprueba integridad SHA-256, esquema, identidad de paquete, versión de app y límites. Presenta altas/reemplazos y pide confirmación antes de escribir SQLite; conserva el mecanismo de deshacer del catálogo. La cuenta es opcional para leer publicaciones.

**Es una actualización de contenido**, sin nuevo APK mientras el formato sea compatible. Cambios de interfaz/lógica o un formato incompatible sí requieren recompilar. La consulta es manual en esta entrega: no hay sondeo permanente, push o actualización silenciosa que pueda pisar ediciones locales.

## Límites que no deben ocultarse

- Inicialmente: 2 MB, 500 registros agregados y 300 por tipo. No se implementó todavía paginación para un catálogo ilimitado.
- Importar añade/reemplaza IDs confirmados; no borra fichas omitidas. Retirar una publicación del servidor no revoca automáticamente una copia ya descargada. Falta un flujo de retiros clínicos/versiones obsoletas antes de uso asistencial.
- SHA-256 comprueba integridad, no autoría profesional ni seguridad clínica. HTTPS, permisos y control editorial siguen siendo necesarios.
- No hay respaldo cloud de notas/PAE/favoritos/archivos ni sincronización multicuenta. Los datos locales pertenecen a la instalación y no se aíslan automáticamente por cuenta; cerrar sesión no borra el cuaderno.
- Cerrar sesión no elimina la cuenta; la baja del servidor se solicita al responsable. Un flujo de eliminación dentro de la app y otros requisitos de publicación pública están pendientes.
- No hubo pruebas de SMTP, Auth o Storage reales ni pruebas físicas de Keystore/instalación. No hay promesa de producción, disponibilidad, residencia de datos o cumplimiento normativo por desplegar el SQL.

## Pruebas reproducibles

```sh
npm ci --prefix backend
npm test --prefix backend
python3 -m unittest discover -s backend/tests -p 'test_*.py' -v
python3 database/verificar_esquema.py
```

`npm test` ejecuta PostgreSQL mediante PGlite, con Auth/Storage mínimos simulados para probar las políticas. No demuestra que un proyecto Supabase real esté configurado ni que el correo funcione. La evidencia Android está separada en `qa/v04/`.
