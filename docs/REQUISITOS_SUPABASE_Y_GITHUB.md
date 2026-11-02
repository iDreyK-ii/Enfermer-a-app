# iDreyK 360 — conexión real y sincronización

Fecha de revisión: 2 de noviembre de 2026.

## Avance real de esta fase

**Proyecto ya creado:** `idreyk-360-dev`, referencia `dmxfdvvcvfecqhbbmrlu`, en **Virginia (`us-east-1`)**, con tu autorización y solicitud de plan Free. No se creó en Brasil ni se cambió a un plan de pago.

Panel: https://supabase.com/dashboard/project/dmxfdvvcvfecqhbbmrlu

La migración ya está aplicada: cuatro tablas con RLS, almacenamiento editorial privado y **12 controles SQL + 7 controles HTTPS reales superados**. El catálogo está vacío: no publiqué medicamentos sin revisión ni creé cuentas de prueba.

**Faltan estas acciones para continuar:**

1. **Correo:** en Authentication → Emails / Email Templates, cambiar **Magic Link** y **Confirm signup** para mostrar `{{ .Token }}`. El cuerpo listo está en `backend/supabase/email/otp.html` dentro del código. No desactivar la confirmación de correo. Después se acuerda una prueba con una dirección tuya autorizada. La guía ampliada está en `docs/SUPABASE_REAL_Y_GITHUB.md`.
2. **GitHub:** indicaste que ya tienes repositorio; falta pegar aquí su URL (Code → HTTPS), sin tokens. Con ella revisaremos el destino y la autorización de escritura.
3. **APK conectado:** se dejaron la configuración pública y el comando de compilación preparados. El APK v0.4.1 existente **no fue reemplazado y sigue sin nube incorporada**. No se hicieron pruebas físicas; se mantienen para después de sincronizar.

El conector Supabase no expone la edición de plantillas/SMTP: esos dos cambios se realizan en el panel. Su correo predeterminado se limita a miembros del equipo del proyecto; el envío general necesita SMTP propio. No se ha enviado ningún OTP ni verificado una sesión real.

## Orden de trabajo acordado

1. Crear/elegir el proyecto Supabase y configurar base de datos, permisos, almacenamiento privado y autenticación.
2. Validar técnicamente el servicio y conectar la configuración pública del cliente Android. Registrar qué pruebas reales pasan y cuáles requieren correo o interacción del propietario.
3. Sincronizar el código y el historial con un repositorio GitHub autorizado; comprobar la primera ejecución real de CI.
4. Después realizar las pruebas de instalación, actualización y funcionamiento en teléfonos Android.

La revisión clínica del contenido es una tarea distinta. No bloquea crear infraestructura o versionar código; sí es necesaria antes de presentar el aplicativo como apto para atención real.

## Diagnóstico inicial histórico — proyecto ya creado después

- El conector Supabase está habilitado y responde.
- Se ve la organización **iDreyK**.
- La consulta devolvió **cero proyectos accesibles**. La conexión de la cuenta por sí sola no crea un proyecto ni configura el APK.
- No se ha creado todavía un servidor en esta fase, aplicado SQL ni enviado correos.
- El conector GitHub consultado no está soportado en este entorno. No se ha hecho un push.
- Base actual: Android nativo v0.4.1, rama `desarrollo`, tag `desarrollo-v0.4.1` e historial portátil en el ZIP.

## Para Supabase

### Decisiones confirmadas

- Proyecto creado: `idreyk-360-dev`, dentro de la organización iDreyK.
- Solicitar **plan Free**, sin autorizar cambios a un plan de pago. Si el proveedor exige otra contratación o supera una cuota, detenerse y consultar.
- **Decisión del propietario: crear un proyecto nuevo, pero no en Brasil; preferencia por Perú.** La lista oficial consultada no ofrece una región principal de proyecto en Perú. Se esperó tu autorización de Virginia antes de crear el proyecto.
- Alternativa autorizada después: **Virginia, Estados Unidos (`us-east-1`)**, que no equivale a alojar datos en Perú. El proyecto se creó en esa región, no en Brasil.
- Fuente oficial de regiones y residencia de datos: [1](https://supabase.com/docs/guides/platform/regions). La elección de región por sí sola no acredita cumplimiento normativo.
- Durante esta fase: solo desarrollo y datos de prueba no clínicos; no publicar monografías sin revisión ni subir datos privados del teléfono.

### Configuración y validación técnica

- Aplicar la migración inicial únicamente a un proyecto nuevo o previamente inspeccionado, sin borrar tablas existentes.
- Verificar RLS y permisos: perfiles propios, borradores editoriales privados y lectura pública exclusivamente de versiones publicadas.
- Mantener privado el bucket `editorial-fuentes`; no publicar un archivo por el simple hecho de subirlo.
- Configurar Auth por código de correo. La entrega real del correo requiere comprobar la configuración de envío, las restricciones del servicio y las plantillas OTP.
- Para verificar un acceso real hará falta una dirección de prueba autorizada y la participación de su propietario. No declarar correo recibido o sesión autenticada solo porque una solicitud HTTP haya sido aceptada.
- Incorporar al cliente **solo URL del proyecto y clave publicable**. Nunca clave secreta, `service_role`, contraseña de base de datos ni tokens personales.
- Las notas, planes, favoritos y archivos del teléfono no se sincronizan automáticamente con cuentas. Añadir esa función sería un trabajo separado, con aislamiento por usuario y medidas de privacidad.

## Requisitos para GitHub

1. **Cuenta propia de GitHub** y acceso al destino del proyecto.
2. **Repositorio privado recomendado**, inicialmente vacío: no generar README, licencia ni `.gitignore` si se va a subir el historial existente. No se ha concedido una licencia general para republicar los documentos e imágenes de referencia.
3. **URL exacta del repositorio existente**, por ejemplo una URL HTTPS sin credenciales incrustadas. Compartir la URL es suficiente para identificar el destino; no acredita permiso de escritura.
4. **Git autenticado** en una máquina del propietario o un entorno autorizado. Puede usarse SSH o un gestor de credenciales/cliente oficial. No enviar tokens, contraseñas ni claves privadas por el chat, ni guardarlos en archivos versionados.
5. Permiso para escribir código y el workflow de `.github/workflows/`. Si se usa un token de granularidad fina, limitarlo al repositorio necesario y revisar los permisos de Contents y Workflows requeridos para esa operación.
6. Actions habilitado, con sus límites/cuotas revisados por el responsable. Validar el YAML localmente no equivale a una ejecución de CI en GitHub.

**Alternativa de autorización limitada a un repositorio:** una vez que compartas la URL, podemos preparar una clave SSH de despliegue para este trabajo. Tú añadirías **solo la clave pública** en ese repositorio → Settings → Deploy keys, con permiso de escritura. La clave privada no se comparte por chat ni se sube al código. Esto permite usar Git sin un token pegado en la conversación. No se ha generado ni registrado esa clave todavía.

Subir el código a GitHub no requiere activar el proveedor de inicio de sesión GitHub dentro de Supabase Auth, ni contratar branching de Supabase. Son funciones distintas.

### Recuperar y subir sin perder el historial

Con el ZIP extraído, abrir una terminal en `idreyk-android/`:

```sh
git clone historial/desarrollo.bundle ../idreyk-restaurado
cd ../idreyk-restaurado
git switch desarrollo
git status
git remote -v
```

El clon nuevo puede tener `origin` apuntando al bundle local. Revisarlo antes de renombrarlo o añadir el remoto real. No reemplazar un remoto existente a ciegas. El paso de push se prepara con la URL real y la autenticación del propietario; no usar `--force` ni `--mirror`.

Después de subir, comparar el commit remoto con el local y comprobar la primera ejecución del workflow. La guía técnica ampliada está en `docs/GITHUB_Y_CI.md`, dentro del proyecto.

### Importante para los APK posteriores

- La CI de calidad actual usa configuración de nube vacía para aislar sus pruebas.
- Su APK tiene una firma debug temporal y **no debe darse por compatible como actualización** del APK entregado.
- Para compilar desde GitHub un APK conectado y actualizable se necesitará una tarea de compilación autorizada y un plan de firma. La clave privada nunca debe ir al repositorio ni exponerse a pull requests.
- No desinstalar la app ni borrar sus datos para resolver un conflicto de firma. Conservar respaldos y la firma de desarrollo; una distribución final requiere un plan release separado.

## Qué significa validación clínica

Es la revisión por profesionales competentes del contenido y de los resultados clínicamente relevantes: indicaciones, dosis, contraindicaciones, precauciones de enfermería, fórmulas, unidades, fuentes, vigencia y límites de uso.

No es validar una cuenta de Supabase, recibir un correo o superar tests de programación. Las pruebas técnicas pueden demostrar que un cálculo implementa una fórmula; no que esa fórmula sea apropiada para todos los pacientes.

Se puede avanzar ahora con infraestructura y GitHub. Mientras no exista esa revisión, mantener el material en borrador, claramente educativo, y usar casos ficticios. No se ha realizado ni declarado una certificación clínica o regulatoria.
