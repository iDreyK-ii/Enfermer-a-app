# Supabase real y continuidad en GitHub — 2 de noviembre de 2026

## Estado real de esta fase

| Elemento | Resultado |
|---|---|
| Organización | iDreyK |
| Proyecto creado | `idreyk-360-dev` |
| Referencia | `dmxfdvvcvfecqhbbmrlu` |
| Región autorizada | Virginia, EE. UU. — `us-east-1`, no Perú ni Brasil |
| Plan solicitado al crear | `free`; no se solicitó cambiar a un plan de pago. La respuesta no incluyó un desglose de facturación. |
| Servicios | Auth, DB, REST y Storage reportaron `ACTIVE_HEALTHY` |
| Migración registrada | `20261102110405_inicial_catalogo_seguro_v1` |
| Seguridad comprobada | Cuatro tablas con RLS; diez políticas; bucket privado; 12 comprobaciones SQL verdaderas |
| API pública por HTTPS | **7/7 comprobaciones de solo lectura** |
| Contenido/cuentas | Cero publicaciones y cero cuentas creadas por esta operación |
| Correo y sesión real | **Pendientes**: plantillas, remitente autorizado y prueba con el propietario |
| APK entregado v0.4.1 | **No cambió**: sigue con configuración de nube vacía |
| GitHub | Destino y escritura autorizados: `iDreyK-ii/Enfermer-a-app`; ver `GITHUB_DESTINO.md` y el run del commit concreto |

Panel del proyecto: https://supabase.com/dashboard/project/dmxfdvvcvfecqhbbmrlu

Conectar la cuenta de Supabase al agente, crear el servidor y compilar un APK conectado son tres pasos diferentes. Esta fase creó el servidor y comprobó su API; dejó los parámetros públicos y el comando de compilación preparados. No se ha instalado ni probado un APK en teléfono.

## Qué se desplegó y qué no

Se aplicó el mismo esquema que ya tenía pruebas locales, con migración versionada en `supabase/migrations/`. Se inspeccionó el proyecto nuevo antes de aplicarlo: no tenía tablas propias ni migraciones. **No volver a ejecutar `backend/supabase/schema.sql` en este proyecto**, ni repetir la migración para corregir una configuración de correo.

- `profiles`: acceso sujeto a identidad del propietario.
- `catalog_drafts`: borradores editoriales privados.
- `catalog_releases`: lectura pública solo de paquetes publicados; integridad y publicación protegidas por restricciones y trigger.
- `idreyk_private.editors`: pertenencia editorial no editable por usuarios normales.
- Bucket `editorial-fuentes`: privado; 10 MiB por archivo y tipos permitidos explícitos.

No se enviaron medicamentos de demostración como publicaciones revisadas, no se fabricó una revisión clínica y no se asignaron editores ficticios. No se subieron notas, pacientes, PAE, favoritos ni archivos del teléfono. No se activaron branches de pago ni integración automática de despliegue desde GitHub.

## Paso pendiente del propietario: correos con código

El conector permite consultar Auth, pero su operación de actualización no expone los campos de plantillas ni SMTP. No se sustituyó esa limitación por claves administrativas o acceso no autorizado.

1. Abre el panel del proyecto indicado arriba.
2. En **Authentication → Emails / Email Templates**, abre **Magic Link**. Sustituye el cuerpo por el contenido de `backend/supabase/email/otp.html`. Usa, por ejemplo, el asunto `Tu código de iDreyK 360`.
3. Haz lo mismo en **Confirm signup / Confirmación de alta**. Conserva literalmente `{{ .Token }}`: es una variable de Supabase, no un código que tengas que inventar.
4. Guarda ambas plantillas. No actives confirmación automática ni deshabilites la verificación del correo para sortear errores de envío.
5. Para la primera prueba, usa una dirección tuya autorizada por el proyecto. El servicio de correo predeterminado limita destinatarios a miembros del equipo y actualmente documenta dos mensajes por hora. Para otros usuarios se necesita SMTP propio configurado en **Authentication → SMTP**. Fuente oficial: https://supabase.com/docs/guides/auth/auth-smtp
6. Si configuras SMTP, introduce host, puerto, remitente y credenciales **directamente en el panel del proveedor**. No enviarlas por chat ni al repositorio. No se ha contratado un servicio de correo de pago.
7. Después de guardar, se puede verificar nuevamente la configuración y acordar una prueba de envío. **No se ha enviado ningún OTP**; recibir y verificar un código real requiere participación de su propietario.

La configuración observada fue: Email activo, SMS y usuarios anónimos desactivados, confirmación de correo exigida, rotación de refresh tokens activa, OTP de ocho dígitos. Android admite de seis a diez dígitos. Las plantillas aún tenían enlaces, sin `{{ .Token }}`; por ello no se declara finalizado el acceso por código.

`site_url` conserva el valor predeterminado `http://localhost:3000`. El flujo previsto es OTP sin enlace; no debe anunciarse un sitio web funcional ni usar los correos con enlaces predeterminados. Antes de habilitar otros flujos con enlaces, configurar una URL real controlada por el propietario y sus redirecciones autorizadas.

La contraseña de base de datos usada al provisionar no está en el repositorio, APK o ZIP. El propietario puede establecer una nueva desde la configuración de la base si necesita usar conexiones directas. El cliente móvil no debe usarla.

## Comprobación remota reproducible

```sh
# Solo valida configuración; no hay red:
python3 backend/comprobar_supabase.py

# Consultas GET explícitas con clave publicable, sin correo, cuentas ni escrituras:
python3 backend/comprobar_supabase.py --remoto
```

Los siete controles son: salud Auth, proveedor email activo, lectura del catálogo, denegación de perfiles, denegación de borradores, esquema privado no expuesto y rechazo de solicitudes sin API key. La respuesta del catálogo fue vacía: no se ha probado descarga/importación de una publicación clínica real.

El comprobador no sigue redirecciones, limita respuestas y no registra claves, tokens, correos ni cuerpos de respuestas. Los errores de permisos se distinguen de una tabla inexistente. La CI de calidad **no ejecuta estas consultas remotas automáticamente**.

Evidencia: `qa/SUPABASE_REAL_2026-11-02.md` y `qa/supabase_real_20261102/`.

## Preparar la compilación conectada, sin reescribir Android

Los únicos valores que puede usar el cliente están en `backend/supabase/deployment.public.json`: endpoint y clave **publicable**, no secreta. La clave no concede identidad o permisos editoriales; RLS es indispensable.

```sh
# Solo comprobar parámetros; no compila ni hace red:
python3 scripts/compilar_con_supabase.py

# Cuando estén preparados JDK17/21, SDK35 y la firma autorizada:
bash scripts/comprobar.sh --todo
python3 scripts/compilar_con_supabase.py --compilar
python3 scripts/verificar_apk.py
```

El script de calidad deja un APK con nube vacía, deliberadamente. El comando siguiente produce el conectado. No confundirlos, ni ejecutar el primero después del segundo esperando conservar su configuración. La firma depende de la máquina; se debe comparar con la huella conservada antes de usarlo como actualización.

**En esta fase no se ejecutó el comando `--compilar`.** Se conservó el APK entregado y todo el código nativo. Las pruebas físicas se harán después de la sincronización, según lo solicitado.

## GitHub: lo que falta y cómo autorizar

El propietario facilitó `https://github.com/iDreyK-ii/Enfermer-a-app` y autorizó el destino público. Se confirmó identidad SSH y escritura limitada a ese repositorio. Ver `GITHUB_DESTINO.md`; los pasos siguientes son orientación para otras máquinas o autorizaciones futuras.

- Revisar si es privado y si ya contiene archivos. Si tiene trabajo, no borrarlo ni forzar su historial.
- El conector GitHub no está soportado aquí. El push requiere Git autenticado en una máquina o entorno autorizado del propietario; la URL por sí sola no da escritura.
- Alternativa de alcance limitado: tras recibir la URL se puede preparar una clave SSH de despliegue. El propietario añade solo la clave pública en Settings → Deploy keys del repositorio, con escritura. La clave privada queda en el entorno autorizado, nunca en chat ni Git. La autorización temporal de esta sesión ya fue registrada y comprobada; la clave privada no se distribuye.
- Usar SSH o un gestor de credenciales/cliente oficial. No pegar tokens, contraseñas ni claves privadas en el chat. Si hay una autorización OAuth, otorgarla en el propio proveedor, con alcance limitado al destino necesario.
- Se necesita acceso para escribir código y el workflow. Revisar permisos de Contents/Workflows si se elige un token de granularidad fina.
- Subir la rama `desarrollo` y tags autorizados, sin `--force` ni `--mirror`. Verificar commit remoto y primera ejecución de Actions.
- No se suben claves, bases locales, cachés, APK ni contraseñas. El historial portátil conserva las etapas anteriores.

El workflow existente prueba el código con nube vacía. Sus APK llevan firma temporal del runner; no deben usarse para sustituir a ciegas la instalación actual. Una compilación conectada y con firma conservada desde GitHub necesita un flujo protegido y el manejo privado de la firma; no se ha transferido esa clave al repositorio.

## Validación clínica: otra comprobación

Es la revisión profesional de indicaciones, dosis, contraindicaciones, cuidados, fórmulas, unidades y fuentes antes de uso asistencial. No equivale a que funcionen Supabase, el correo o la compilación. Se puede avanzar con infraestructura y GitHub mientras se trabaja con contenido de práctica; no declarar validación clínica, aval DIGEMID o aptitud para pacientes reales.
