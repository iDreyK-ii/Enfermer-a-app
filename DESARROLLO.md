# Desarrollo continuo del aplicativo iDreyK 360

## Objetivo y fuente principal

El objetivo es desarrollar una aplicación Android mantenible, estable y completa para el alcance que se acuerde. **No se está preparando un producto de venta ni se considera terminada la aplicación actual.**

El proyecto principal es esta carpeta, `idreyk-android/`. Las siguientes mejoras deben incorporarse aquí, no quedar en prototipos separados. La referencia visual continúa siendo el Prototipo B.

**Los datos de demostración pueden sustituirse; el código que los muestra y gestiona forma parte del aplicativo.** Que una ficha esté vacía o un paciente sea ficticio no significa que sus pantallas y funciones sean archivos de caché.

## Qué se conserva y qué se puede regenerar

| Elemento | Ubicación | Tratamiento |
|---|---|---|
| Pantallas, navegación, diseño, lógica y persistencia | `app/src/main/` | Código principal; conservar y versionar. |
| Recursos, fuentes, licencias y biblioteca inicial | `app/src/main/res/` y `app/src/main/assets/` | Parte de la aplicación; conservar. |
| Plantillas, esquema e importación de contenidos | `content-tools/` | Parte del proyecto; conservar y versionar. |
| Pruebas, evidencias y referencias documentales | `app/src/test/`, `qa/`, `research/` | Conservar para controlar cambios y límites. |
| Ilustración original editable | `design/` | Recurso de diseño; conservar. |
| Historial local del código | `.git/` | Puntos de recuperación del desarrollo. |
| Copia transportable del historial | `historial/desarrollo.bundle` | Respaldo del historial; no es una dependencia del APK. |
| Bibliotecas descargadas para las pruebas | `.cache/robolectric-maven/` | Caché regenerable; no contiene el código propio del aplicativo. |
| Compilaciones y cachés de herramientas | `app/build/`, `.gradle/`, `.kotlin/` y cachés del entorno | Regenerables; no son la fuente principal. |

Limpiar las carpetas regenerables **no elimina las funciones guardadas en el código**. Las herramientas o dependencias pueden necesitar descargarse otra vez al compilar.

La APK actual se conserva fuera de las carpetas de compilación. Es una versión instalable de trabajo, no un sustituto del código editable. La clave de firma se mantiene aparte y **no se incluye en el ZIP público del código ni en el historial**.

## Estado de lo solicitado

“Implementado” describe software existente; no significa que toda la función esté certificada clínicamente o probada en todos los teléfonos.

| Solicitud | Estado actual | Pendiente |
|---|---|---|
| Android nativo y diseño B | Implementados con Kotlin y Compose. | Revisión visual, accesibilidad y compatibilidad en teléfonos reales. |
| Trabajo local, sin cuentas y biblioteca incluida | Implementados; la app no solicita permiso de internet. | Ampliar pruebas de reinicio, muerte de proceso y errores de almacenamiento. |
| Farmacología detallada | Estructura de 20 campos, búsqueda, filtros, pestañas, favoritos e importación implementadas. | Completar contenido autorizado y revisado; las seis fichas incluidas son borradores. |
| Glasgow | Componentes adultos y cálculo de práctica implementados; NV impide el total. | Validación del uso previsto y revisión profesional de la implementación. |
| EVA | Interfaz digital simulada implementada. | Definir y validar su uso; no es una regla física calibrada de 100 mm. |
| Capurro A/B | Pantallas y referencia documental preparadas. | Criterios, fórmula, límites, revisión clínica y pruebas antes de habilitar puntuación. |
| Otras escalas | Entradas de catálogo preparadas. | Implementación y revisión individual; no se consideran terminadas. |
| Correctos de enfermería | Listas configurables de 1–100 ítems implementadas. | Incorporar el protocolo aplicable y autorizado; no se impone 20 como estándar universal. |
| Archivos de medicamentos y casos | XLSX → JSON, validación, previsualización, confirmación e incorporación local implementadas. | Extracción de Word/PDF y flujo editorial remoto, si se acuerdan después. |
| Casos clínicos | Formato importable y un caso ficticio de organización incluidos. | Contenido clínico autorizado y revisado. |
| PAE/Gordon y pacientes | Funciones de práctica con datos ficticios y guardado local. | No equivalen a una historia clínica real; ese uso requeriría otro trabajo de privacidad y seguridad. |
| Herramientas y aprendizaje | Aritmética de ejemplo, tarjetas, cuestionario y ECG ilustrativo incluidos. | Dosis y dilución siguen deshabilitadas; no hay adquisición ni diagnóstico de ECG. |
| Exportación y recuperación | Catálogo exportable y deshacer una importación implementados. | El respaldo actual no incluye notas, favoritos, ajustes ni historial de ejercicios. |

No se quitarán avisos ni se marcarán funciones como completas solo para que parezcan profesionales. La calidad se conseguirá completando, revisando y probando cada función.

## Historial y respaldo recuperable

Se creó un primer punto de recuperación con el estado Android actual. El historial comienza en esta base; no inventa versiones anteriores que no estaban registradas.

- Rama de desarrollo: `desarrollo`.
- Etiqueta inicial: `base-android-v0.2`.
- El ZIP `Android_iDreyK_360_codigo.zip` contiene el código y una copia del historial en `historial/desarrollo.bundle`.
- No se ha conectado ni publicado el repositorio en un servicio remoto.

Para recuperar el historial desde el ZIP, descomprímelo y ejecuta, indicando una carpeta de destino nueva:

```sh
git clone historial/desarrollo.bundle ../idreyk-recuperado
```

También puedes abrir directamente el código descomprimido en Android Studio sin utilizar Git. El archivo de historial no es necesario para instalar la APK ni para que la aplicación funcione.

**Descarga el ZIP como copia fuera de este espacio de trabajo.** El guardado del entorno tiene límites y no es una garantía de conservación indefinida. El historial local tampoco sustituye una copia externa.

## Cómo continuar sin perder avances

1. Trabajar sobre el proyecto principal y comprobar el estado del historial antes de editar.
2. Integrar cada mejora en la aplicación real, manteniendo separados desarrollo técnico, datos de ejemplo y contenido clínico revisado.
3. Añadir o actualizar las pruebas de la función modificada; registrar resultados reales y comprobaciones pendientes.
4. Guardar un punto de recuperación con una descripción del cambio.
5. Actualizar el archivo transportable del historial y el ZIP del proyecto; nunca incluir cachés, claves privadas ni datos reales de pacientes.
6. Generar una nueva APK cuando cambie la aplicación, conservando la firma adecuada y evitando perder los datos locales existentes.
7. Mantener un único paquete actual de entrega y vigilar el límite del espacio conservado. Si el historial creciera demasiado, respaldarlo fuera antes de reducirlo.

Orden de trabajo acordado: farmacología y flujo de contenidos; Glasgow, EVA y Capurro; listas de seguridad; consolidación y pruebas del resto de módulos. El contenido clínico, sus permisos y los servicios externos siguen siendo una fase posterior, no algo que se complete automáticamente.

## Importante sobre los datos del teléfono

Cerrar la aplicación no borra los registros guardados localmente. **Desinstalarla o borrar su almacenamiento sí puede eliminarlos.** La exportación actual solo respalda el catálogo, no todos los datos del teléfono. Esto es distinto de conservar el código fuente del aplicativo.
