# Inventario funcional que debe conservarse al trasladar

Cliente actual: **Android v0.5.0-dev**, evolución de v0.4.1, no una app nueva. Este inventario reúne la base v0.3 y las ampliaciones v0.4/v0.4.1. Las limitaciones históricas de v0.3 sobre ausencia de INTERNET/cuentas no describen el código actual. El contrato verificable de identidad está en `../CONTINUIDAD.json`.

## Funciones implementadas y límites

| Área que no se puede omitir | Qué se conserva | Límite real |
|---|---|---|
| Inicio, navegación y búsqueda | Prototipo B; selección inicio 2/ficha 1; módulos, búsqueda global, favoritos, perfil e historial. | Comparar el código y recursos, no reconstruir pantallas desde una captura. |
| Diseño y accesibilidad | Paleta, tipografías y licencias; tema oscuro, texto ampliado, adaptación de tarjetas, animaciones y modos Automático/Ligero/Completo. | Reducir movimiento por accesibilidad/memoria/batería; falta medir en teléfonos reales. |
| Farmacología | Buscar, crear, editar y eliminar fichas parciales; 30 secciones opcionales, estructura de 17 conceptos del PDF, sinónimos, fuentes y procedencia. | Seis fichas iniciales en borrador. Vacío no significa seguro; no inventar dosis ni completar clínica con IA. |
| Revisión editorial | Estados de borrador/revisión y procedencia; editar vuelve a borrador y retira la declaración previa de revisión. | Una declaración importada no es certificación profesional. |
| Catálogo y protocolos | Medicamentos, casos, listas de medicación segura y protocolos editables/importables. | No es una biblioteca clínica completa ni un extractor automático de PDF/Word. |
| Importación protegida | Lee JSON 1/2/3 y exporta 3; límites; vista previa, diferencias por campo, aceptación de reemplazos, huella revisada dentro de transacción y deshacer. | Reemplaza registros, no fusiona campos. 2 MB/500 registros agregados/300 por tipo. Las omisiones no borran registros. |
| Medicación segura | Listas de 1–100 ítems, orden, marcas, reinicio, fuentes y guardado del recorrido. | No impone un número universal de correctos ni constituye un protocolo asistencial completo. |
| Biblioteca de tests clínicos | 44 fichas offline/40 fuentes; filtros combinados por población/área, búsqueda y procedencia; acceso a 11 funciones de práctica/registro/simulación. | Inventario inicial; 33 fichas no administran un instrumento. Permisos/versión y revisión clínica pendientes según ficha. |
| Escala numérica y 4AT | NRS 0–10 sin respuesta predeterminada; 4AT con cuatro dominios, no valorable puntuable, estados pendientes, atribución y guardado de práctica. | Adaptaciones de práctica sin validación clínica digital; NRS no es EVA y 4AT no diagnostica. |
| Glasgow adulto/pediátrico | Registro de componentes, estados no valorable/pendiente y suma cuando procede. | Pediátrico requiere ficha externa identificada; sin diagnóstico ni adaptación automática de descriptores adultos. |
| EVA, Capurro A/B, Apgar, Silverman-Andersen | Simulación/registro, fórmulas o sumas y guardado según el instrumento. | Conservar límites, variantes y referencias; no añadir decisiones de gravedad, pronóstico o tratamiento. |
| Braden y Morse | Registro de un total externo con fuente/contexto y comprobación de rango. | No reproduce ni administra cuestionarios completos; no interpreta riesgo. |
| Cálculos | Dosis a volumen, dilución, IMC, goteo y conversores; unidades, límites, validación dimensional y registro de práctica. | Aritmética educativa; no selecciona dosis ni demuestra compatibilidad/estabilidad/reconstitución. |
| PAE / ADPIE | Planes editables, valoración, problema libre, resultados, planificación, ejecución, evaluación y referencias. | No genera diagnósticos ni reproduce NANDA-I/NIC/NOC. |
| Prácticas ficticias | Fichas ficticias, notas, signos, balance y once espacios de Gordon. | No convertirlos en historias clínicas reales ni cargar datos de pacientes. |
| Tendencias | FC a partir de las últimas 20 lecturas ficticias guardadas. | Orden de registro, no intervalos temporales proporcionales; no alertas clínicas. |
| Apuntes y cuaderno | Crear, editar, buscar, etiquetar, borrar; respaldo propio de planes/apuntes, vista previa y deshacer. | Guardado manual; no se garantiza recuperación de formularios sin guardar después de terminar el proceso. |
| Multimedia | Copiar PDF/imagen/audio/video al almacenamiento privado; buscar, renombrar, quitar copias y abrir con visor externo mediante FileProvider. | No reproductor integrado; 20 MiB por archivo, 100 MiB y 200 archivos. Los medios no van en los JSON de respaldo. |
| Aprendizaje | Flashcards, quiz, casos, progreso/historial y ECG ilustrativo. | Sin sensores ni certificación profesional. |
| Persistencia | SQLite `idreyk_local.db` v2, ocho tablas y migración aditiva 1→2; preferencias y archivos privados. | No borrar/recrear tablas ni desinstalar para actualizar. Los datos personales no se sincronizan por crear una cuenta. |
| Cliente remoto opcional | Código Supabase: solicitud/verificación OTP, sesión protegida con Android Keystore, renovación/cierre y consulta manual de publicaciones con comprobación e importación confirmada. | **Preparado, no validado de extremo a extremo.** Plantillas/envío/sesión aplazados; APK entregado todavía sin nube incorporada. |
| Herramientas editoriales | Plantillas XLSX/JSON, conversor, esquemas, publicación controlada, SQL y pruebas SQLite/PGlite. | Conservar también estas carpetas; no desplegar SQL desde el compilador del cliente. |

## Recursos y organización

En este punto se conservan **85 archivos de recursos empaquetados**: 81 en `app/src/main/res/` y 4 en `app/src/main/assets/`. Incluyen vectores, imagen, seis archivos de fuentes, valores/reglas XML, licencias y el contenido inicial. No son 85 fotografías. Hay 27 archivos Kotlin de producción y 11 archivos de pruebas Android; los recuentos son una foto de esta base, no una prohibición de ampliar el código con cambios aprobados.

Los archivos `Screens*.kt` separan las áreas; `Repository.kt` contiene repositorio, SQLite y ViewModel; `Models.kt`, `Pharmacology.kt`, `ScaleModels.kt`, `DoseMath.kt`, `NotebookModels.kt`, `MediaFiles.kt`, `PracticeTrends.kt`, `Performance.kt`, `ImportReview.kt`, `JsonSafety.kt` y `SessionVault.kt` conservan lógica y validaciones. No sustituirlos por pantallas estáticas.

## Qué significa «todo tal como está»

Se trasladan **todos los archivos versionados del commit aprobado**: cliente, recursos, pruebas, documentación, plantillas, fuentes/referencias autorizadas y backend. GitHub conserva el historial; el ZIP de entrega añade un bundle recuperable. El importador no necesita copiar `.git` ni sus credenciales.

No se incorporan contraseñas, claves administrativas, firma privada, dependencias/cachés, APK anteriores ni bases/datos del teléfono al repositorio. El ZIP de código tampoco respalda automáticamente apuntes, favoritos o medios personales.

Una copia idéntica conserva el software existente, **no convierte pendientes en funciones terminadas**: correo real, sesión/renovación autenticada, APK conectado con firma compatible, prueba física/actualización, accesibilidad/rendimiento en equipos reales, permisos editoriales y validación clínica siguen separados. No se autoriza distribuir esta base como producto clínicamente certificado.


Ampliación de escalas: `ClinicalCatalog.kt`, `ScreensClinicalCatalog.kt`, `research/escalas/` y `docs/ESCALAS_CLINICAS_v0.5.0.md`. No modificar instrumentos para adaptarlos a la estética; conservar versiones, límites, autoría y derechos.
