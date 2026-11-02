# Desarrollo continuo del aplicativo iDreyK 360

## Punto actual: desarrollo-v0.4.1

Pulido previo a la futura sincronización: importación con diferencias/aceptación y huella anticolisión, campos congelados durante escrituras, adaptación a pantallas/letra grande, microinteracciones sin forzar movimiento y JSON acotado. SQLite2 y formatos1/2/3 se conservan.

133 pruebas Android; 29 Python (incluidas 5 de guardas); 5 SQLite y 17 PostgreSQL/RLS locales. Lint: 0 errores/24 advertencias. APK versionCode5, misma firma. Evidencia en `qa/v041/` y `qa/RESULTADOS_v0.4.1.md`.

Se prepararon workflow y scripts de CI; no se sincronizó ni ejecutó nada en GitHub. Sigue sin proyecto Supabase real ni validación física/clínica. Ver `docs/GITHUB_Y_CI.md` y `docs/MEJORAS_v0.4.1.md`. La clave privada no se exporta.

## Historial de las etapas anteriores

## Punto de control previo: desarrollo-v0.4

La petición más reciente amplió expresamente el alcance a cuentas y catálogo remoto. Se conserva Android/Compose y SQLite local v2; se añadió Supabase (PostgreSQL/Auth/Storage) como conector opcional, sin proyecto real configurado ni claves incluidas.

- PDF del usuario: 17 conceptos técnicos, lectura de enfermería, 30 secciones opcionales sin descartar las anteriores. Formatos 1/2/3 de entrada y 3 de salida.
- Selección visual conservada: inicio2 y ficha1, corregida tipográficamente. Dossier en `design/propuesta_v0.4/Diseno_y_arquitectura_v0.4.pdf`; no son capturas del APK.
- Rendimiento automático/ligero/completo, respetando accesibilidad, batería, presión de memoria y ciclo de vida.
- Conector de código por correo, sesión cifrada y consulta manual de publicaciones con vista previa/confirmación. No se suben datos privados.
- SQL/RLS, publicador privado, plantilla v3 y guías: `backend/README.md`, `content-tools/GUIA_FARMACOLOGIA_v3.md`, `docs/ORDEN_DE_COMPILACION_v0.4.md`.
- APK v0.4 compilado, misma firma. 106 pruebas Android; 24 de archivos; 17 checks PostgreSQL/RLS y 5 del espejo SQLite. Lint: 0 errores/24 advertencias. Evidencia: `qa/RESULTADOS_v0.4.md`.
- Pendiente: servidor/correo real, privacidad/gestión de cuenta para publicación abierta, prueba física y rendimiento medido, revisión y autorización clínica. No afirmar producción.

El ZIP se actualiza incluyendo el bundle del historial, sin claves ni cachés. Se preservan APK v0.2/v0.3 y recursos originales; el nuevo APK declara INTERNET pero viene sin configuración cloud.

## Registro histórico de v0.3 y arquitectura-v0.3

Las secciones siguientes describen esas etapas; no son el estado cloud/formato de v0.4. El detalle actual está arriba y en README/qa/RESULTADOS_v0.4.md.

## Proyecto y objetivo

La fuente principal sigue siendo `idreyk-android/`: Android nativo, Kotlin/Compose y estética del Prototipo B. Las ampliaciones se incorporan aquí, no en una aplicación paralela. **Se está desarrollando el aplicativo; no preparando una venta ni declarando terminada su validación clínica.**

La limpieza anterior conservó los archivos de programación, los recursos, la biblioteca y el APK v0.2. Las pantallas que seguían indicando «preparado» eran funciones todavía no implementadas, no código perdido por caché. En v0.3 se reemplazaron esos estados por funcionalidades concretas, con límites explícitos.

## Punto de desarrollo entonces: v0.3

- Escalas: Capurro B con selección somática y cálculo; A con transcripción de seis componentes y constante independiente; Apgar y Silverman con suma y guardado. Se conservan Glasgow adulto/EVA; se añade Glasgow pediátrica como registro externo. Braden/Morse son registros de puntajes externos, no cuestionarios completos.
- Dosis a volumen y dilución: aritmética con unidades, incompatibilidades dimensionales, validación de números y protección contra resultados antiguos tras editar entradas; sin prescripción o validación farmacológica.
- Editor local de medicamentos (20 secciones), casos, correctos y protocolos. Crear, editar y eliminar; se vuelve a borrador y se conserva procedencia. Deshacer del catálogo.
- PAE/ADPIE: documentos estructurados editables en las fichas ficticias, no el antiguo checklist de cuatro tareas. El checklist viejo permanece conservado y señalado como recorrido.
- Apuntes propios con búsqueda, etiquetas, edición y eliminación; lecturas originales conservadas.
- Protocolos locales con fuentes, estados y catálogo de esquema 2. Lectura de los paquetes de esquema 1 conservada.
- Biblioteca multimedia: copias privadas, renombrado, búsqueda, borrado y apertura con visor externo; límites de tamaño y proveedor de lectura restringido.
- Cuaderno con respaldo/importación confirmada y deshacer independientes del catálogo.
- Gráfico de FC construido desde lecturas ficticias guardadas; ya no es una curva decorativa.
- SQLite v1 → v2 mediante creación de dos tablas nuevas; ninguna tabla antigua se borra al actualizar.
- Mismo paquete y clave de firma de desarrollo, versión de APK incrementada. No desinstalar la versión anterior para actualizar.

El detalle de lo disponible y lo pendiente está en `README.md`. Los resultados exactos están en `qa/RESULTADOS.md` y `qa/resultados.json`.

## Qué conservar

| Elemento | Ubicación | Tratamiento |
|---|---|---|
| Pantallas, lógica, navegación y persistencia | `app/src/main/` | Código principal; conservar y versionar. |
| Fuentes, licencias, ilustración optimizada y biblioteca inicial | `app/src/main/res/`, `app/src/main/assets/` | Parte de la app, no caché. |
| Ilustración original editable | `design/` | Conservar. |
| Pruebas y evidencias | `app/src/test/`, `qa/` | Conservar para regresiones. |
| Referencias documentales | `research/` | Conservar; no equivalen a validación clínica. |
| Conversor, plantillas y esquemas | `content-tools/` | Parte de la aplicación y su flujo editorial. |
| Historial local | `.git/` | Recuperación del código. No es una copia remota. |
| Historial portátil | `historial/desarrollo.bundle` | Se incluye explícitamente en el ZIP de código. |
| APK entregado | Fuera de `app/build/`, en la carpeta compartida | Conserva el instalable, no sustituye al código. |
| Clave de firma | Almacenamiento privado del entorno | Conservar por separado; nunca añadir a Git o al ZIP. |

Cachés y compilaciones (`app/build/`, `.gradle/`, `.kotlin/`, `.cache/`, dependencias descargadas) son regenerables. Robolectric usa `.cache/robolectric-maven/` dentro del proyecto, no `.m2` persistente de gran tamaño. Las herramientas se guardan en `/home/user/.cache/`. No borrar la carpeta del proyecto, sus fuentes o la clave confundiendo recursos con caché.

## Historial y recuperación

La rama es `desarrollo`. El punto inicial real del historial es:

- Commit `038c9e19b84fe198a0fa6d4eceed92b5e37dfd2d`.
- Etiqueta `base-android-v0.2`.
- Se comprobó su recuperación clonando el bundle y comparando las 131 rutas versionadas.

La ampliación v0.3 tiene un nuevo punto de control y etiqueta `desarrollo-v0.3`. **No se inventa historial anterior al punto inicial.** Para revisar:

```sh
git status
git log --oneline --decorate --all
git diff base-android-v0.2..desarrollo-v0.3 --stat
```

Para recuperar el proyecto en otra carpeta:

```sh
git clone historial/desarrollo.bundle idreyk-recuperado
cd idreyk-recuperado
git checkout desarrollo
```

El bundle está ignorado por Git para no incluirse recursivamente dentro de sí mismo. Al actualizar la entrega:

```sh
git bundle create historial/desarrollo.bundle --all
git bundle verify historial/desarrollo.bundle
```

El ZIP se genera desde archivos versionados **más** ese bundle; no desde todas las carpetas de trabajo. Verificar que no contiene cachés, APK/AAB, claves ni `local.properties`. Probar un clon del bundle, no solo su verificación formal. Un historial dentro del mismo entorno no protege frente a pérdida total del entorno: descargar una copia externa del ZIP.

## Datos de la aplicación ≠ código del proyecto

El ZIP y el bundle recuperan programación, recursos y pruebas. **No son copias de los datos de tu teléfono.** Los mecanismos actuales de exportación se separan:

1. Catálogo: medicamentos, casos, listas y protocolos, en esquema 2.
2. Cuaderno: planes PAE y apuntes, en su formato propio.
3. Multimedia: conservar los originales; los JSON no incluyen las copias.

Las notas de fichas ficticias/Gordon/signos de v0.2, favoritos, historial y preferencias no tienen exportación completa en esta edición. Sí se conservan durante la migración local 1 → 2. Desinstalar o borrar datos puede eliminarlos.

## Próximas comprobaciones necesarias

1. Instalar/actualizar el APK en teléfono sin desinstalar v0.2; verificar datos existentes.
2. Comprobar SAF, permisos temporales, apertura de medios con distintas apps, cancelaciones y poco almacenamiento.
3. Revisar accesibilidad, rotación, guardado de estado, textos grandes y cargas extensas en dispositivos reales.
4. Obtener revisión clínica y permisos aplicables a cada escala/contenido; no declarar «completa» una ficha farmacológica vacía.
5. Si se plantea uso con pacientes reales: rediseñar privacidad, cifrado, accesos, auditoría y cumplimiento normativo. Actualmente solo datos ficticios.
6. Integración editorial externa, extracción documental e IA siguen fuera de esta edición offline. No simular una conexión no implementada.

Todo cambio posterior debe traer pruebas, actualización de alcance, nuevo APK compatible cuando corresponda y respaldo reproducible del código.

## Revisión documental de arquitectura — 2026-11-01

Se adaptó la plantilla README/.env/SQL al aplicativo real, sin reorganizar ni reemplazar el código Android. Nuevo punto documental: `arquitectura-v0.3`; las etiquetas funcionales `base-android-v0.2` y `desarrollo-v0.3` no se mueven.

- `README.md`: arquitectura real, estructura, relaciones lógicas y entradas de lectura selectiva.
- `AGENTS.md`: reglas para trabajo incremental con IA sin cargar todo el proyecto ni introducir un servidor por una plantilla genérica.
- `.env.example`: solo rutas opcionales de JDK/SDK/caché. Android/Gradle no lee `.env` automáticamente; no contiene secretos de servidor.
- `database/schema.sql`: espejo de las ocho tablas SQLite de `LocalDatabase`; no sustituye migraciones ni agrega usuarios o claves foráneas inexistentes.
- `database/verificar_esquema.py`: cinco comprobaciones documentales y creación exclusiva de una fixture con el seed original. Sin dependencias Python adicionales.
- `docs/ESTADO_FUNCIONAL_v0.3.md`: README previo conservado íntegro.
- `docs/COMPILAR_Y_PROBAR.md`: comandos completos de herramientas, compilación, pruebas y ADB, con advertencia de firma.
- `qa/ARQUITECTURA.md` y `qa/esquema-pruebas.txt`: resultados nuevos, separados de la evidencia histórica del APK.
- `.gitignore`: exclusión de configuración privada y bases locales, permitiendo `.env.example`.

Se mantuvieron idénticos 114 archivos de fuentes/recursos/pruebas/configuración y los dos APK entregados. No se recompiló Android ni se repitieron las pruebas Robolectric/conversor en esta revisión de documentación. No se implementó autenticación, no se cambió el alcance offline/sin cuentas y no se subió nada a un remoto.

El ZIP de código se actualiza con este punto de control, conservando la historia anterior mediante el bundle. Los hashes, tamaño del ZIP y comprobación de clon de la entrega se registran en `LEEME_Android_iDreyK_360.md`, fuera del proyecto, para no crear referencias circulares al propio commit/archivo comprimido.
