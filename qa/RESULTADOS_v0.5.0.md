# QA v0.5.0 — tests clínicos y biblioteca de escalas

Fecha: **2 de noviembre de 2026**. Base anterior: `793aa057efd14e852b0d0b76c47de74da8f1f9fe`.

## Ejecutado localmente antes del commit

`bash scripts/comprobar.sh --rapido`: **88 pruebas Python** (52 guardas, 15 conversor y 21 backend), **5 SQLite** y **17 comprobaciones PGLite/PostgreSQL** superadas. No se llamó ni se modificó el proyecto Supabase real.

`python3 research/escalas/compilar_catalogo.py --comprobar`: correspondencia de fuente editorial/asset. `git diff --check`: sin errores de espacios. Se contrastaron bytes con la revisión anterior para tema/diseño, farmacología, SQLite, lógica anterior de escalas, seed y nube. Carpetas `res`, `design`, `content-tools`, `database`, `backend` y `supabase` sin cambios funcionales. Evidencia: `escalas_v050/preflight.json`.

## Pruebas Android nuevas (necesitan ejecución en entorno Android/JVM)

- **23 tests de modelo/catálogo**: 44 fichas/40 fuentes, integridad de referencias, población, búsquedas, filtros, rutas, bloqueos de falsas declaraciones clínicas y datos inseguros, cero/NV/pendiente, reglas y bandas 4AT.
- **6 tests de interfaz nativa simulada**: filtro neonatal, variante Capurro A, ficha protegida sin calculadora, NRS sin preselección/cero/ausencia de autoinforme/contexto, 4AT no valorable/reinicio/población.
- El test de combinaciones 4AT recorre **36 combinaciones válidas**. No son 36 pruebas clínicas ni una nueva validación psicométrica.

No se ejecutaron localmente tests Android, lint o ensamblado: el entorno local tiene Java 11 y no un SDK/JDK preparado. La CI del **SHA final** ejecutará `scripts/comprobar.sh --todo`. Su resultado y enlace se registran en el informe de entrega y en GitHub; no se declara éxito por tener un workflow o por el resultado histórico de v0.4.1.

## Qué todavía no prueba esta entrega

- No aprobación profesional del contenido, equivalencia digital o rendimiento diagnóstico.
- No prueba física, capturas antes/después de Google, TalkBack real o matriz de dispositivos.
- No uso clínico de instrumentos pendientes ni permisos editoriales aún no obtenidos.
- No correo/sesión autenticada ni compilación conectada de Supabase.
- No equivalencia de la firma de Google/CI con la firma privada del APK previo.

El propietario informa que Google construyó la revisión previa sin cambios. Esa declaración se conserva como **reporte del propietario**, no como inspección independiente del APK o de su exportación.
