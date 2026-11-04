> **Documento histórico v0.5.0:** el catálogo actual 0.9 tiene 45 fichas, 43 fuentes y 13 accesos; véase `research/escalas_kawaii/REVISION_2026-11-03.md`. Las cifras y límites específicos de 0.5 se conservan abajo como antecedente.

# Tests y escalas clínicas — primer bloque v0.5.0

**Decisión del propietario, 2 de noviembre de 2026:** instrumentos clínicos, no cuestionarios de examen; adultos, pediatría y neonatos; sin PDFs descargables por ahora. Farmacología al final. Google construyó la copia anterior según informa el propietario, sin cambios adicionales; no se recibieron fuentes/capturas del resultado para comprobarlo independientemente.

## Qué se ha añadido al código

- **44 fichas documentales, 40 fuentes referenciadas y 9 áreas.** No es un inventario universal ni 44 calculadoras. Cada ficha tiene objetivo, población, versión, método, límites, derechos, fuentes, profundidad de lectura y siguiente paso.
- **11 fichas con acceso a una función existente o nueva**: Glasgow, Glasgow pediátrica, EVA, escala numérica, 4AT, Braden, Morse, Apgar, Silverman-Andersen, Capurro A y Capurro B. El acceso puede ser cálculo de práctica, transcripción externa o simulación, claramente diferenciados.
- Biblioteca offline con búsqueda por texto/acento, filtros combinados de población/área y opción de mostrar solo fichas con práctica/registro. Integración de fichas en búsqueda global. No depende de una cuenta ni de Internet para leer.
- **Escala numérica del dolor** separada de EVA: valores explícitos 0–10, sin preselección; cero válido; ausencia de autoinforme sin puntaje; cambio de contexto invalida respuesta previa; guardado de práctica local.
- **4AT**: adaptación de práctica de cuatro componentes y suma 0–12, con atribución/licencia, confirmación de ejemplo adulto, explicación de cada dominio, estados pendientes, no valorable de AMT4/atención puntuado como 2, bandas de cribado sin diagnóstico, y reinicio. Documentación: [atribución y cambios](../research/escalas/ATRIBUCION_4AT.md).
- Más contexto documental en Glasgow y EVA. EVA conserva su advertencia de línea digital no calibrada y añade reinicio. No se transforma en escala numérica ni se añaden caras.
- Enlace profundo de Capurro A/B que abre la variante correcta; el acceso antiguo sigue abriendo B y permite cambiar a A como antes.

## Ruta dentro del aplicativo

**Inicio → Escalas → Explorar tests y escalas → población/área → ficha.**

Cada ficha expone su estado antes de abrir una función. Los permisos pendientes no se ocultan tras un botón de cálculo ficticio. También permanecen los accesos directos anteriores en Escalas; se añaden Escala numérica del dolor y 4AT.

## Diseño que se conserva

Se reutilizan `AppCard`, `Badge`, `ChoiceField`, `Primary`, `Page`, la paleta y Outfit/Nunito Sans del Prototipo B. No se sustituyen el inicio elegido, la ficha farmacológica, sus imágenes ni el tema. Nuevas pantallas del apartado usan las mismas tarjetas, jerarquía tipográfica y controles adaptables.

- No hay nuevas caras, ilustraciones clínicas, logos ni estímulos de test creados por IA.
- Las áreas usan color e indicación textual; el color **no** significa gravedad.
- Controles numéricos con tamaño mínimo, nombres accesibles y disposición que se adapta al ancho; no se fija la altura del texto.
- Se conservan modo oscuro, texto ampliado y movimiento reducido. No se añaden animaciones obligatorias.
- Igualdad de los recursos del diseño anterior no prueba identidad visual en el dispositivo de Google: se necesitan capturas reales comparables y revisión de accesibilidad.

## Seguridad clínica y editorial

La app sigue siendo de **desarrollo/práctica**, no una herramienta validada para decidir atención. Las fichas se investigaron con profundidad desigual, indicada individualmente: algunas páginas completas, algunos resúmenes/extractos y referencias históricas. No se afirma lectura íntegra de 40 documentos, revisión sistemática ni vigencia normativa exhaustiva.

La reproducción pública de un test no se autoriza por encontrarlo en una búsqueda. FPS-R, Wong-Baker, MoCA, MNA, Braden y Morse, entre otros, tienen condiciones que deben respetarse. Se conserva ficha o registro externo según el caso, no un formulario inventado. NEWS2 no se rediseña para que coincida con los colores de la app; los instrumentos normalizados conservarán su presentación exigida dentro del contenedor visual de la app.

Ninguna escala nueva selecciona medicamentos, dosis, soporte respiratorio o tratamiento. No se reemplaza información no obtenida por cero. Glasgow NV y 4AT no valorable tienen reglas **diferentes**. No se introducen datos identificables de pacientes.

Informe con decisiones, contradicciones, 44 fichas y fuentes: [investigación detallada](../research/escalas/REVISION_2026-11-02.md).

## Compatibilidad

- Mismo Kotlin/Compose, módulo `:app`, actividad, namespace y applicationId.
- **versionCode 6 / versionName 0.5.0-dev**. APK debug: `0.5.0-dev-debug`.
- SQLite `idreyk_local.db` sigue en **v2**, ocho tablas; no migración destructiva ni tablas nuevas. Las prácticas nuevas reutilizan el historial existente.
- JSON farmacológico 1/2/3, exportación 3, catálogo inicial, backend, esquema desplegado y plantillas de correo se conservan. No se despliega SQL ni se modifica Supabase en este bloque.
- No se habilita una cuenta ni una nube de forma implícita; el build de CI sigue con nube vacía.
- Este documento describe fuentes. El APK v0.4.1 entregado antes permanece histórico y no contiene estas pantallas. Una APK externa necesita comprobación de certificado antes de actualizar; no desinstalar para resolverlo.

## Cómo editar y comprobar el catálogo

Fuente editorial única: `research/escalas/catalogo_fuente.json`. Copia empaquetada: `app/src/main/assets/escalas_clinicas_v1.json`.

```sh
python3 research/escalas/compilar_catalogo.py
python3 research/escalas/compilar_catalogo.py --comprobar
bash scripts/comprobar.sh --rapido
# Con SDK y JDK adecuados:
bash scripts/comprobar.sh --todo
```

No marcar `revision_clinica` como aprobada para eludir validación: el parser rechaza esa declaración en esta edición. Una futura habilitación asistencial requiere un proceso explícito, evidencia profesional y cambio revisado, no editar solo una bandera.

Pruebas nuevas: modelo/catálogo Kotlin, interfaz Compose simulada, guardas editoriales Python. Sus resultados reales se comunican en QA/CI del commit final; el mero hecho de existir tests no significa que hayan pasado.

## Continuar apartado por apartado

1. Cotejar este bloque básico y su versión digital con personal de enfermería y las capturas de Google.
2. Dolor pediátrico/neonatal: formularios completos, permisos y población antes de añadir FLACC/r-FLACC/NIPS/PIPP-R.
3. Piel/caídas y funcionalidad: permisos/variantes de Braden, Morse, Barthel, Katz, Lawton, TUG y Tinetti.
4. UCI, delirium y deterioro: fuentes completas, traducciones y validación de RASS, CAM-ICU, CAPD, NEWS2/PEWS, entre otros.
5. Cognición/salud mental: formularios autorizados y rutas de actuación/seguridad, sin sustitutos de IA.
6. Ampliar el resto de apartados del aplicativo por bloques; **farmacología permanece para el final**.

Los puntos 2–6 son trabajo pendiente, no funcionalidades terminadas. El inventario podrá ampliarse; «todos los tests existentes» no es una lista finita acreditada por esta entrega.
