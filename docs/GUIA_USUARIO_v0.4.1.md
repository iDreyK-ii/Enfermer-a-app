# iDreyK 360 — entrega de desarrollo v0.4.1

## Empieza por el diseño

Abre **Diseno_iDreyK_360_v0.4.pdf**. Contiene las dos pantallas que elegiste, la ficha con textos corregidos y la explicación de sus partes, datos, rendimiento y próximos pasos. Son **maquetas, no capturas del APK**; el diseño se adapta con componentes nativos.

## Archivos de esta entrega

| Archivo | Para qué sirve |
|---|---|
| `Enfermeria_iDreyK_360_v0.4.1_debug.apk` | Instalar/probar Android 8.0 o superior. Edición de desarrollo, no publicación clínica o en tienda. |
| `Enfermeria_iDreyK_360_v0.4.1_debug.apk.sha256` | Comprobar la integridad del APK. |
| `Android_iDreyK_360_codigo.zip` | Proyecto Kotlin/Compose completo, SQL, plantillas, diseño, guías, pruebas e historial Git portátil. |
| `Plantilla_farmacos_iDreyK_v3.xlsx` | Completar progresivamente los medicamentos con la estructura ampliada. |
| `Plantilla_farmaco_iDreyK_v3.json` | Ejemplo equivalente, ficticio y en borrador. |
| `Diseno_iDreyK_360_v0.4.pdf` | Desglose visual y de arquitectura. |

Los APK v0.2/v0.3/v0.4 y la plantilla antigua se conservan. Usa la nueva plantilla v3 para los 17 conceptos del PDF más los campos de enfermería. Las celdas vacías son válidas y se consideran pendientes; no se rellenaron dosis o controles inventados.

## Pulido añadido en v0.4.1

- Antes de importar muestra sustituciones, campos que quedarían vacíos y ediciones locales afectadas. Pide aceptarlas y rechaza una revisión obsoleta si la biblioteca cambió después.
- La bienvenida y las cuadrículas se adaptan mejor a pantalla pequeña y texto grande.
- Respuestas suaves al pulsar tarjetas y seleccionar pestañas, respetando modo ligero y reducción de movimiento.
- Campos bloqueados durante escrituras; se conserva la advertencia al salir sin guardar. No hay autosave garantizado: guarda las fichas parciales con el botón.
- Protección ante JSON excesivamente anidado y vista previa paginada.
- Workflow y scripts preparados para el futuro repositorio. Todavía no se hizo push ni se ejecutó CI en GitHub.

El detalle completo está en `MEJORAS_iDreyK_v0.4.1.md` y dentro del proyecto. El PDF de diseño sigue siendo la propuesta v0.4, no el resultado QA de esta revisión.

## Qué conserva la app

- Farmacología destacada y accesos al resto de módulos.
- Ficha con **Lo esencial / Ficha técnica / Otros / Fuentes**; conserva los campos anteriores y añade los nuevos.
- Búsqueda por nombre genérico, sinónimos y nombres comerciales documentados.
- Editor por grupos, guardado parcial y exportación de formato3; lectura de formatos1/2/3.
- Modos **Automático / Ligero / Completo**, respetando reducción de movimiento, batería, memoria y segundo plano.
- Conector opcional para cuenta por código de correo y consulta de publicaciones, sin reemplazo silencioso.

## Cuentas y lugar de carga: lo preparado y lo pendiente

Se eligió **Supabase**:

- **PostgreSQL:** borradores y catálogo publicado/versionado.
- **Auth:** identidad/correo para registrarse y acceder por código.
- **Storage:** documentos fuente en un espacio editorial privado.
- **SQLite en Android:** consulta local, favoritos, práctica y cuaderno.

**Todavía no hay un proyecto Supabase real creado/configurado para esta app.** El APK entregado no contiene endpoint ni clave pública y muestra «Nube sin configurar». No se han probado correos ni cuentas reales. El lugar inicial para administrar/cargar será el panel privado Supabase Studio; no se ha construido otro portal editorial independiente.

La guía `backend/README.md`, dentro del ZIP, explica cómo crear/configurar el proyecto, aplicar permisos, preparar correos y conectar el cliente. Solo URL y clave publicable van al APK; nunca claves administrativas. La orden de continuidad está en `docs/ORDEN_DE_COMPILACION_v0.4.md`.

Con el servicio activado: completar plantilla → revisar fuentes/licencia → publicar una versión → en Android, **Actualizar biblioteca → Buscar actualización de contenido → revisar y confirmar**. Añadir medicamentos con el formato compatible no requiere recompilar. La consulta es manual; no hay push ni actualización silenciosa. Subir un PDF por sí solo no publica una ficha ni valida su contenido.

Registrar una cuenta **no respalda ni separa automáticamente los datos privados de la instalación por usuario**. Notas, planes, favoritos y medios siguen en el teléfono. Cerrar sesión no borra esos datos ni elimina la cuenta del servidor.

## Instalar sin perder lo anterior

1. Conserva respaldo del catálogo y cuaderno; los medios requieren sus originales por separado.
2. Instala `Enfermeria_iDreyK_360_v0.4.1_debug.apk` como **actualización**, sin desinstalar v0.4. Se verificó que conserva paquete y firma.
3. Si el teléfono informa un conflicto de firma, **no borres datos ni desinstales** para resolverlo. Una compilación de otra máquina puede llevar una firma distinta.
4. No fuerces un downgrade a v0.3: el nuevo formato3 no se puede importar allí.

La instalación/actualización física sigue pendiente de prueba. El ZIP no contiene la clave privada de firma ni datos de tu teléfono. Una edición para tienda requiere firma release, política de privacidad, revisión de requisitos actuales y pruebas adicionales.

## Comprobaciones y límites

**133 pruebas Android**, **29 pruebas Python de contenido/publicación y guardas**, **17 comprobaciones PostgreSQL/RLS locales** y **5 del espejo SQLite**, todas superadas. APK compilado; lint con **0 errores y 24 advertencias**. Evidencias en `qa/RESULTADOS_v0.4.1.md` y `qa/v041/`.

No se ha validado el aplicativo para atención clínica. No se probaron SMTP/Auth/Storage reales, hardware Keystore, instalación física ni FPS en distintas gamas. El PDF aportado define estructura, no un aval DIGEMID. Los medicamentos iniciales permanecen como borradores y se debe trabajar con datos ficticios.

Límites iniciales del catálogo: 2MB, 500 registros agregados y 300 por tipo. Una importación reemplaza los IDs confirmados completos: conservar datos que no deban perderse; no asumir mezcla de campos ni eliminación automática de fichas ausentes.

## Continuar el desarrollo

Extraer el ZIP y entrar en `idreyk-android/`. Leer `README.md`, `AGENTS.md` y `docs/COMPILAR_Y_PROBAR.md` y `docs/GITHUB_Y_CI.md`. No rehacerlo como app web ni borrar la base existente. El proyecto mantiene el mismo Prototipo B y sus módulos anteriores.

Esta entrega permite continuar y exportar el trabajo; **no cierra el desarrollo ni declara operativos los servicios todavía no configurados**.
