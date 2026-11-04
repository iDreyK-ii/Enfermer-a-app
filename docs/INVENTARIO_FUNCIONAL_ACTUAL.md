> **Actualización local 0.9.0-dev:** 45 fichas/43 fuentes/13 accesos; siete PNG de acompañamiento, doce claves educativas, filtro por tipo y fuentes externas a demanda. Revisión parcial, sin aprobación clínica. Ver `research/escalas_kawaii/REVISION_2026-11-03.md` y `qa/escalas_kawaii/RESULTADOS.md`.

> **Antecedente local 0.8.0-dev:** los 24 dibujos y sus fichas están integrados, con revisión profesional pendiente. Ver `qa/atlas_kawaii/RESULTADOS.md`.

> Histórico 0.7.0, no evidencia automática de 0.8: 252 JVM, 118 Python, 5 SQLite y 17 PGLite; lint 0 errores/26 advertencias. Ocho capturas del APK en emulador API28, 17 renders Robolectric separados y 2/2 pruebas PDF instrumentadas aprobadas. No hardware físico, no nuevo CI/publicación. Detalle: `qa/atlas_v22/RESULTADOS.md`.

> Revisión local no publicada. Matriz histórica del atlas 0.8: [AUDITORIA_ATLAS_KAWAII.md](AUDITORIA_ATLAS_KAWAII.md). No usar el CI del último publicado b463c580 como verificación del borrador.

# Inventario funcional actual — Android 0.9.0-dev local / Base Maestra 2.1 + atlas y escalas kawaii

Este inventario describe la app existente que debe conservarse al importar/compilar. No es una propuesta para generar otra. La matriz de estados, evidencia y bloqueos completos está en [MVP_BASE_MAESTRA_v21.md](MVP_BASE_MAESTRA_v21.md).

## Identidad

Kotlin/Jetpack Compose, módulo `:app`, una Activity, Prototipo B, diseño inicio 2/ficha 1. Namespace `com.idreyk.enfermeria360`, paquete debug `com.idreyk.enfermeria360.dev`, code **10**, nombre `0.9.0-dev` (debug añade `-debug`), minSdk 26, compile/target 35. Especificación editorial **2.1**, no versión Android 2.1.

## Funciones conservadas y ampliadas

| Área | Lo disponible | Límites que se deben preservar |
|---|---|---|
| Bienvenida/inicio | Bienvenida, perfil local, hero/imagen elegidos, búsqueda, módulos, tarjetas, favoritos/actividad; accesos nuevos a biblioteca/avance. | No rediseñar ni sustituir por HTML. No actividad/porcentajes inventados. |
| Navegación/tema | Barra nativa, retorno, estados de edición, claro/oscuro. | Probar teclado/recortes/rotación reales; las capturas no lo demuestran. |
| Rendimiento | Política automática/manual, animación seleccionable, respuesta táctil, movimiento reducido prioritario. | No eliminar controles al reducir movimiento; FPS/hardware pendientes. |
| Farmacología anterior | Seis borradores iniciales y ficha con Lo esencial, ficha técnica de 17 conceptos, otros datos/fuentes; 30 campos/estructura editorial preservados. | No convertir datos parciales del PDF en monografías certificadas. |
| Inventario nominal nuevo | 554 maestros editoriales, 737 apariciones trazadas, alias/relaciones/clase, consulta y búsqueda. | Todo IDENTIFICADO; no DCI/stock/registros confirmados. `altilice` no resuelto. |
| Edición de contenido | Editores nativos de medicamentos, casos/listas/protocolos; fuentes, borradores y persistencia. | No publicar como revisado solo por guardarlo o importarlo. |
| Importación/revisión | JSON de catálogo v1/v2/v3, comparación de sustituciones, avisos de campos que se vaciarían, aceptación explícita, prevención de vistas obsoletas, deshacer. | Límites/tipos/IDs/sanitización; no descartar campos desconocidos silenciosamente. |
| Biblioteca Markdown | 20 temas, metadatos/procedencia/fecha/versión, títulos/listas/tablas/fórmulas y enlaces como referencias, filtros área/nivel. | No contenido clínico exhaustivo; revisión profesional pendiente; parser acotado, sin ejecutar HTML/cargar imágenes remotas. |
| Búsqueda offline | Funciones anteriores más texto de temas, etiquetas/áreas, partes de dispositivos, casos, herramientas/registradores e inventario. | Se busca lo empaquetado/local, no se inventa un resultado remoto. |
| Favoritos/historial/avance | Rutas anteriores y nuevas; lectura marcada explícitamente, respuestas persistidas, progreso versionado e historial real. | Abrir no equivale a dominar; versiones incompatibles no se acreditan automáticamente. |
| Biblioteca de escalas | 45 fichas/43 fuentes/13 accesos, filtros por población/área/tipo, versiones, fuentes, derechos y límites; siete ilustraciones y 12 claves. | No 45 calculadoras, no catálogo universal ni certificación clínica. |
| Instrumentos existentes | GCS adulto con componentes/NV, EVA y NRS separadas, 4AT adulto, Apgar educativo, Capurro, Silverman y registros anteriores de alcance explícito. | Mantener población, pendientes, ausencia de autoinforme, variante y reglas de puntuación. No inventar caras o estímulos. |
| Registros externos | Braden original, Morse, Norton original y RASS: total ficticio entero, confirmación adulta, fuente/versión/contexto y rango. | No cuestionarios administrados ni interpretación/riesgo; permisos/validación siguen bloqueados. |
| Cálculos | BigDecimal: dosis-volumen, cantidad/kg, conversión dimensional, mL/h, goteo, duración, dilución aplicable, balance e IMC; historial de ejercicios. | Factor del equipo obligatorio; edad/base explícitas; unidades, límites, fórmula/redondeo; no selecciona tratamientos. |
| Atlas | 24 PNG RGBA/JSON, puntos/lista, zoom/reinicio, movimiento suave adaptable, panel adaptable y autoevaluación persistida. | Los 24 son ilustraciones estilizadas; revisión profesional y aceptación visual/física pendientes. No hay fallback robótico. |
| Bioseguridad/higiene | Cinco momentos, EPP/precauciones, cronómetro lavado/fricción con pausa/reinicio/pasos y modo reducido; manos esquemáticas retiradas y recurso visual pendiente. | Tiempos de procedimiento completo; reparto/dibujo didácticos; quirúrgico depende de producto/protocolo. |
| Listas seguras | Diez ítems institucionales INSN-SB con ámbito y veinte controles propios separados; listas configurables anteriores. | No una norma universal de veinte correctos ni autorización de administración. |
| PAE | Siete campos anteriores y 32 complementarios: datos/fuente, Gordon, ABCDE, razonamiento/NOC/NIC propios, SOAPIE/SBAR. | Sin diagnósticos automáticos ni catálogos/códigos licenciados. Solo fichas ficticias. |
| Apuntes/cuaderno | Crear/editar/buscar/etiquetar/borrar, revisión/deshacer, respaldo lee v1/v2 y exporta v2; Markdown importable/exportable. | Guardado manual; 200 documentos/2 MB; medios no incluidos en respaldo; SAF real pendiente. |
| Impresión/PDF | Adaptador del framework, generador educativo, rangos/cancelación/área imprimible. | Dos pruebas PDF instrumentadas aprobadas en 0.9 final en emulador API28; servicio físico de impresión/SAF pendiente. |
| Prácticas ficticias | Fichas de demostración, notas, signos, balance y Gordon; tendencias de FC de las últimas 20 lecturas. | No pacientes reales, sensores ni alertas clínicas; eje por orden de registro, no tiempo proporcional. |
| Aprendizaje/casos | Flashcards/quiz/casos/ECG ilustrativo anteriores; siete casos nuevos progresivos, 21 preguntas/retroalimentación/rúbricas y puntuación desde respuestas. | Escenarios sintéticos, revisión docente pendiente; no competencia acreditada. |
| Multimedia | Copia privada PDF/imagen/audio/video, búsqueda/renombrado/eliminación y visor externo con FileProvider. | No reproductor integrado; límites anteriores (20 MiB/archivo, 100 MiB/200 archivos). Sin respaldo automático en JSON. |
| Persistencia | SQLite v2/ocho tablas, migración aditiva anterior; cuaderno/notas/prácticas/favoritos/medios; progreso en meta `learning:`. | No recrear ni borrar DB; no sincronización personal automática. |
| Cuentas/contenido remoto | Cliente Supabase anterior: OTP, sesión/renovación/cierre, Keystore y consulta/importación confirmada de publicaciones. | Preparado, no E2E verificado; plantillas/sesión aplazadas, APK local sin nube. Cuentas en Auth, no SQLite. |
| Editorial/servidor | Plantillas, conversor XLSX/JSON, esquemas, publicación controlada, RLS/SQL y pruebas SQLite/PGLite conservados. | No reaplicar migración ni recrear proyecto, no secretos administrativos en cliente. |

## Recursos y estructura

Conservar todas las carpetas versionadas, no solo `app/src/main`. Se añaden `EducationMath`, `MasterCatalog`, `NotebookEducation`, `ScreensMaster`, `ScreensAtlasHygiene` y `ScreensNotebookEducation`, además de rutas/integraciones en archivos existentes. El atlas usa PNG locales y puntos normalizados/etiquetas editables del JSON. No hay rótulos generados dentro de las imágenes ni puntos de componentes ocultos.

`research/base_maestra_v21/` conserva la entrada editorial y generadores; `app/src/main/assets/base_maestra_mvp.json` contiene la selección educativa/nominal empaquetada. No empaquetar el Markdown bruto de la Base como guía clínica. El catálogo de escalas de 45 fichas y sus fuentes permanece en un asset separado.

Los recuentos exactos de archivos de la entrega se obtienen del manifiesto del **commit publicado**. El resultado actual está en `qa/atlas_kawaii/RESULTADOS.md`; una prueba instrumentada compilada no cuenta como ejecutada; no confundir número de archivos/clases con número de pruebas.

## Qué significa trasladar todo

Importar todos los archivos del commit aprobado: código, recursos/licencias, contenido, investigación autorizada, pruebas, backend, plantillas y documentación. El ZIP duplicado fue retirado por limpieza; regenerarlo si corresponde y verificarlo. El repositorio local conserva el historial. No importar credenciales, firma privada, cachés, APK históricos o datos del teléfono. Una copia idéntica preserva el software, **no convierte pendientes en funciones validadas**.

Los módulos realmente futuros están en [10_ROADMAP_Y_MODULOS_FUTUROS.md](10_ROADMAP_Y_MODULOS_FUTUROS.md). Ni la app ni el compilador deben crear pantallas falsas de módulos ausentes para aparentar finalización.
