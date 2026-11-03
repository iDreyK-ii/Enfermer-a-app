# Atlas kawaii: procedencia y límites

La elección del propietario sustituyó el requisito anterior de fotorrealismo por **ilustraciones kawaii con caras discretas y movimiento nativo suave**. No cambió la necesidad de reconocer componentes, mantener transparencia, documentar variantes y evitar instrucciones clínicas o aprobaciones inventadas.

## Producción reproducible

- `ilustrar.py` dibuja geometría 2D original, específica para cada dispositivo, con trazos y degradados; **no son fotografías, no son fotos ampliadas ni imágenes producidas por un generador visual de IA**. El asistente generó el código editorial. El SVG es un formato intermedio de reproducción, no un recurso vectorial ejecutado por la app.
- Cada PNG se rasterizó directamente con CairoSVG 2.8.2 a **2000 × 1250 px, RGBA**, sin reescalado de fotografía; el canal alfa es real. Los colores, proporciones y caras son estilizados y no acreditan equivalencia anatómica. Los originales finales se ubican en `app/src/main/assets/atlas/`.
- `crear_atlas.py` recompone imágenes y metadatos individuales. Dependencias solo para reproducción editorial: `python -m pip install -r research/atlas_kawaii/requirements.txt`; después `python research/atlas_kawaii/crear_atlas.py`, `python research/base_maestra_v21/crear_contenido.py` y `python research/base_maestra_v21/compilar_mvp.py`. Esto reemplaza activos locales: revisar el diff antes de conservar cambios.
- Cada JSON incluye nombre técnico, vista, dimensiones, SHA, partes visibles/ocultas, coordenadas normalizadas, función, precaución, fuentes y fecha. `registro_ilustraciones.json` conserva hashes y regiones de caras para la comprobación automática. Las caras **no son componentes ni lecturas**.
- Para la aguja el protector se muestra separado únicamente para identificarlo: no enseña reencapuchado. Las pantallas de los aparatos están vacías y el manómetro no tiene aguja; no hay dosis, presiones, energía, trazados ni lecturas simuladas.
- Cada ficha enlaza al menos dos referencias. Las fuentes primarias y el alcance consultado están señalados; una ficha de distribuidor o un segundo documento del mismo fabricante no son revisión profesional independiente. Algunas fuentes contextualizan el tipo de dispositivo, no certifican la geometría. Las IFU del producto real y su revisión local siguen siendo necesarias.
- Modelos diferenciados: dos frente a tres vías Foley, tres conexiones de llave D300, cánula IV alada sin puerto lateral, una válvula Venturi de un kit con variantes, tubo endotraqueal adulto con ojo de Murphy (la variante pediátrica citada puede no tenerlo), y catéter central triple. El código de colores es decorativo, no identifica calibre ni concentración.
- Partes ocultas, como campana posterior, vejiga del manguito, filtro, lumen de aguja y cánula interna de traqueostomía, no reciben punto visual ni pregunta. El conjunto está estilizado; el cotejo automático de coordenadas/alfa no valida anatomía.

## Compatibilidad y revisión

`referencia_v22_1.json` conserva la descripción y orden previos, no imágenes duplicadas. En las cinco fichas que ya eran funcionales se mantuvieron IDs, visibilidad y orden de opciones/objetivos, por lo que las respuestas `atlas-v22.1` no cambian de significado. Los tres dispositivos previamente rechazados no tenían progreso de quiz; ahora tienen nuevas ilustraciones, no los candidatos descartados.

Revisión visual/documental asistida por IA; **sin aprobación profesional** (`accuracy_approved_by: null`, `clinical_approved: false`). Aún se requiere revisión de una persona cualificada, validación docente/visual, pruebas físicas de accesibilidad/rendimiento y autorización expresa antes de cualquier publicación. Nunca inferir competencia clínica desde el quiz.
