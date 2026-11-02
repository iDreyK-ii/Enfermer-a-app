# Repositorio de trabajo y comprobación de sincronización

Destino autorizado por el propietario el 2 de noviembre de 2026:

- Repositorio: https://github.com/iDreyK-ii/Enfermer-a-app
- Rama de desarrollo: `desarrollo`.
- Visibilidad: **pública, confirmada expresamente por el propietario** para el código y todo el historial, incluidos PDFs y diseños de referencia.
- Comprobación inicial: repositorio vacío, sin ramas ni tags; clave SSH de despliegue identificada por GitHub como perteneciente únicamente a este repositorio; prueba de escritura `--dry-run` aceptada sin enviar archivos.
- Mecanismo: Git/SSH autorizado. El conector GitHub de este entorno no está soportado; no se utiliza para autenticar ni subir archivos.

No se debe confundir una prueba `--dry-run` con una subida. Después del envío se comparan los hashes de la rama y de cada tag con los que devuelve GitHub. El resultado y la ejecución concreta de Actions quedan en el informe de entrega y en el servicio remoto, no se deducen por existir este documento.

## Trabajar desde el repositorio

```sh
git clone https://github.com/iDreyK-ii/Enfermer-a-app.git
cd Enfermer-a-app
git switch desarrollo
git status
```

Para escribir, usar una credencial autorizada en la máquina propia. El permiso SSH de esta sesión no viaja con el código. No añadir contraseñas a la URL ni copiar claves privadas al repositorio. Si un ZIP se recupera desde el bundle, revisar primero sus remotos; `origin` puede apuntar al archivo local.

Conservar el historial. No usar `push --force`, `--mirror` ni borrar ramas para resolver diferencias. Leer `AGENTS.md` antes de modificar la app.

## Comprobar la automatización

Workflow: https://github.com/iDreyK-ii/Enfermer-a-app/actions/workflows/calidad.yml

Validar el estado del run correspondiente al commit concreto, no solo que exista un archivo YAML. Sus pruebas son Android/JVM/Robolectric, Python, SQLite y PGlite; la ejecución en un runner no equivale a una prueba física del teléfono.

La calidad usa URL/clave Supabase vacías. Su APK tiene firma debug temporal: **no es la actualización firmada del APK entregado ni un APK conectado al proyecto real**. La compilación conectada se prepara con `scripts/compilar_con_supabase.py`; ver `SUPABASE_REAL_Y_GITHUB.md` y el plan de firma antes de distribuirla.

## Límites y permisos

- La autorización para alojar este historial públicamente no constituye licencia general ni revisión de derechos de terceros. Se conservan avisos y procedencia existentes; no se inventó una licencia de distribución.
- No se incluyen contraseñas de DB, claves secretas de Supabase, claves privadas SSH o de Android, datos del teléfono, APK, cachés ni dependencias generadas.
- `backend/supabase/deployment.public.json` contiene valores **publicables** del cliente. No son una identidad ni permiso de edición; la seguridad del servidor depende de RLS.
- El correo OTP y la sesión real de Supabase siguen pendientes de comprobación con el propietario. Subir código no configura esas plantillas.
- Las pruebas físicas y la validación clínica siguen siendo tareas separadas. No se cambia ese alcance por publicar en GitHub.
