# Atlas v2.2 — registro de investigación, selección y límites

Fecha: 2026-11-03. Revisión documental y visual asistida por IA. **No hay profesional que haya aprobado la precisión clínica.** Los JSON conservan `accuracy_approved_by: null` y `clinical_approved: false`.

## Entradas y conservación

Las dos instrucciones originales v2.2 se conservan sin editar en esta carpeta. Complementan las 13 secciones y anexos A/B de la Base Maestra 2.1, cuyo SHA-256 se mantiene en `qa/atlas_v22/baseline_conservado.json`. No se sustituye Kotlin/Compose por una web. No se publica sin revisión y autorización expresa del propietario.

## Selección y lectura de fuentes

Los URL completos, fecha, alcance de lectura y componentes se encuentran en cada JSON de `app/src/main/assets/atlas/`. No se copiaron fotografías de fabricantes al aplicativo.

| Ficha | Referencia utilizada | Segunda referencia y alcance | Decisión / precaución |
|---|---|---|---|
| Jeringa | B. Braun Omnifix Luer Lock Solo; página completa | Folleto B. Braun alojado por Medline, completo (2010). Mismo fabricante, no validación independiente | Tres piezas: vástago, sello separado y cilindro. Collar con rosca interna; sin números/capacidad. Primera candidata rechazada por tablero falso y conector dudoso; se usa la corregida. |
| Aguja | Sterican estándar, catálogo actual B. Braun, página completa | Folleto técnico anterior completo. La antigua página indonesia y otro PDF devolvieron 404; no se cuentan como lecturas completas | Hub, cánula, bisel, punta y protector separados. No declara calibre, longitud ni método de punción. El lumen no tiene punto ni pregunta. Primera candidata tenía tablero falso; se sustituyó antes del recorte. |
| Estetoscopio | Póster anatómico 3M completo | Manual Classic III en stethoscope.eu, dos fragmentos completos; fuente secundaria y específica de ese modelo | Ilustración genérica binaural de doble cabezal, no réplica certificada. No se deduce doble lumen. Cara posterior de campana oculta. Se retiró una mancha/pseudoorificio central del candidato para no enseñar una perforación del diafragma. |
| Catéter | Introcan Safety 3, extracto IFU completo recuperado | Segunda lectura técnica completa pendiente | Modelo alado, cerrado, SIN torre de puerto lateral. Se elimina ese componente de la ficha anterior. Candidato rechazado por tablero opaco y componente metálico interno no sustentado. SIN PNG, puntos ni autoevaluación. |
| Equipo IV | TCBINF001, sección de equipos por gravedad de TrueCare, dos fragmentos completos | JMI: tabla de componentes/materiales completa; Henry Schein: ficha TCBINF001 completa | Un roller y un sitio Y. La antigua URL del fabricante devolvió 404; se localizó la página actual. Filtro interno documentado, pero sin hotspot. El factor de goteo no se deduce del PNG ni se precompleta en cálculos. No se añaden los filtros externos/reguladores de otros TCBINF. |
| Bureta | SMD 129, catálogo completo | Tabla técnica JMI completa: anatomía general, no certificación del modelo | No es Dosifix. Dos pinzas y cámara inferior según configuración seleccionada. Candidato rechazado por cámara/tramo desconectado. SIN PNG ni quiz. |
| Llave | Discofix C, fragmento de catálogo indexado | Lectura integral y segunda fuente técnica pendientes | Se requieren exactamente tres ramas, no cuatro. La imagen generada se rechazó; no se infiere el flujo interno ni posición abierta/cerrada. SIN PNG ni quiz. |
| Aneroid | ADC Prosphyg 760, cinco fragmentos de página completos | IFU oficial español rev. 21, tres fragmentos completos; mismo fabricante, no validación independiente | Dos tubos y vejiga interna; no se mezcla con DuraShock/FlexiPort ni se marca la tela como vejiga. La escala ilustrada no mide presión. El manual seleccionado excluye uso neonatal. Recorrido exacto de tubos y aprobación del modelo requieren revisión profesional. |

La búsqueda inicial precedió a los candidatos; la lectura integral y el cotejo final se ampliaron después. La cronología no se presenta como ocho revisiones expertas previas. Los documentos del mismo fabricante no se hacen pasar por un contraste independiente. Debe reforzarse esa independencia en la siguiente revisión.

## Selección visual y procesamiento

- El generador alcanzó **10 imágenes en este turno**. No se intentó evadir el límite. No permite completar los 24 recursos en esta entrega.
- Se integran cinco recortes de candidatos con apariencia fotográfica, no fotografías documentales: jeringa, aguja, estetoscopio, equipo IV y aneroide. La aprobación visual final del usuario y la revisión profesional siguen pendientes.
- Son **PNG RGBA con alfa real**, no cuadrados pintados. Se inspeccionaron sobre fondo claro y oscuro. Se corrigieron zonas blancas interiores y las sombras opacas detectadas durante el recorte.
- Los originales son de 1376×768 o 1408×768. Se exportan a **2000 px de lado mayor por ampliación Lanczos**; esto NO inventa resolución óptica ni detalle nativo de 2000 px. Dimensiones, digest y operaciones exactas: `procesamiento_png.json`.
- `preparar_png.py` reproduce los recortes a partir de los cinco originales útiles y de `mascara-tensiometro.png`. La máscara del aneroide se obtuvo con rembg/u2netp; no sintetizó componentes. OpenCV/Pillow realizan máscara, suavizado y tratamiento de bordes.
- Los originales descartados/superados se retiraron para ahorrar espacio, conservando miniaturas inequívocamente etiquetadas en `qa/atlas_v22/rechazados/` y hashes en la evidencia de limpieza. No se empaquetan como imágenes clínicas.
- Los puntos fueron calibrados sobre estos píxeles, normalizados 0–1 y comprobados contra el alfa. Eso verifica ubicación sobre un objeto visible, **no certifica por sí solo que la anatomía sea correcta**.
- Clasificación de partes: `universal` significa común en la familia representada, no en todo dispositivo médico; `modelo` y `opcional` no se universalizan. No hay puntos ficticios para componentes ocultos.

## Integración y continuidad

`ScreensAtlasPng.kt` carga cada archivo local, limita dimensiones y memoria de decodificación y muestra un recurso pendiente si falla. Etiqueta única seleccionada para evitar solapamientos, lista accesible, zoom/reinicio y panel lateral desde 600 dp. La vista inicial limita también su altura en paisaje. No hay etiquetas pintadas dentro de los PNG.

Las cinco autoevaluaciones usan elecciones reales, hasta cinco objetivos visibles por equipo (cuatro en la aguja), versión `atlas-v22.1`, persistencia en `meta learning:` y registro de finalización. No cambian las tablas SQLite ni invalidan las lecturas/casos de `2.1-mvp.1`. Los candidatos pendientes no admiten quiz. Las respuestas se validan por ruta, versión, opciones y estado de finalización.

Se retiran también las manos robóticas de higiene: queda aviso explícito de recurso pendiente, con el cronómetro, pasos, pausa y duraciones originales conservados. No se confunde esta retirada con una animación de manos terminada.

## No completado

- Diecinueve de los 24 PNG pedidos siguen pendientes: tres rechazados y dieciséis no producidos. Inventario completo: `qa/atlas_v22/inventario_24_png.json`. No son los 12 recursos FUTUROS de la sección 5.
- Revisión profesional/modelo exacto, mayor detalle nativo, derechos/versiones de instrumentos, contenido clínico validado y prueba física permanecen abiertos.
- La evidencia de ejecución/QA se registra por separado; una compilación, una tarjeta y un render Robolectric no se llaman prueba de APK instalado.

## Entorno reproducible

Versiones exactas en `entorno_preparacion.json`; la máscara rembg ya está incluida. No se requiere inferencia ni descarga de modelos para repetir `preparar_png.py`. QA final, capturas instaladas y los dos PDF realmente generados están en `qa/atlas_v22/RESULTADOS.md`; no sustituyen aprobación profesional.
