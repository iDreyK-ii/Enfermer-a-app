# QA v0.5.0 — tests clínicos y biblioteca de escalas

Fecha: **2 de noviembre de 2026**. Base anterior: `793aa057efd14e852b0d0b76c47de74da8f1f9fe`.

## Ejecutado localmente antes del commit

`bash scripts/comprobar.sh --rapido`: **88 pruebas Python** (52 guardas, 15 conversor y 21 backend), **5 SQLite** y **17 comprobaciones PGLite/PostgreSQL** superadas. No se llamó ni se modificó el proyecto Supabase real.

`python3 research/escalas/compilar_catalogo.py --comprobar`: correspondencia de fuente editorial/asset. `git diff --check`: sin errores de espacios. Se contrastaron bytes con la revisión anterior para tema/diseño, farmacología, SQLite, lógica anterior de escalas, seed y nube. Carpetas `res`, `design`, `content-tools`, `database`, `backend` y `supabase` sin cambios funcionales. Evidencia: `escalas_v050/preflight.json`.

## Pruebas Android nuevas (necesitan ejecución en entorno Android/JVM)

- **23 tests de modelo/catálogo**: 44 fichas/40 fuentes, integridad de referencias, población, búsquedas, filtros, rutas, bloqueos de falsas declaraciones clínicas y datos inseguros, cero/NV/pendiente, reglas y bandas 4AT.
- **7 tests de interfaz nativa simulada**: filtro neonatal, variante Capurro A, ficha protegida sin calculadora, NRS sin preselección/cero/ausencia de autoinforme/contexto, 4AT no valorable/reinicio/población.
- El test de combinaciones 4AT recorre **36 combinaciones válidas**. No son 36 pruebas clínicas ni una nueva validación psicométrica.

No se ejecutaron localmente tests Android, lint o ensamblado: el entorno local tiene Java 11 y no un SDK/JDK preparado. La CI del **SHA final** ejecutará `scripts/comprobar.sh --todo`. Su resultado y enlace se registran en el informe de entrega y en GitHub; no se declara éxito por tener un workflow o por el resultado histórico de v0.4.1.

## Qué todavía no prueba esta entrega

- No aprobación profesional del contenido, equivalencia digital o rendimiento diagnóstico.
- No prueba física, capturas antes/después de Google, TalkBack real o matriz de dispositivos.
- No uso clínico de instrumentos pendientes ni permisos editoriales aún no obtenidos.
- No correo/sesión autenticada ni compilación conectada de Supabase.
- No equivalencia de la firma de Google/CI con la firma privada del APK previo.

El propietario informa que Google construyó la revisión previa sin cambios. Esa declaración se conserva como **reporte del propietario**, no como inspección independiente del APK o de su exportación.

## Diagnóstico de CI (segunda revisión técnica)

El primer run `41540992515` para `9445652` falló en la etapa de calidad. La API pública devolvió solo el código de salida; el acceso SSH al repositorio no concede acceso administrativo a descargar logs. No se afirma una causa sin evidencia.

Se añadió un diagnóstico acotado de la ejecución actual como anotaciones de GitHub, con redacción de credenciales, escape de comandos, límite de longitud y resumen de XML de tests fallidos. **No aumenta permisos del workflow, no usa secretos y no oculta ni convierte el fallo en éxito.** Siete tests Python comprueban el diagnóstico, incluido conservar el código de salida original del comando.

## Verificación y corrección de las pruebas nuevas

- La suite rápida se repitió después del diagnóstico: **95 Python** (59 guardas, 15 conversor, 21 backend), **5 SQLite** y **17 PGLite**, todos superados.
- El run [41541956946](https://github.com/iDreyK-ii/Enfermer-a-app/actions/runs/41541956946) de `224df5a` hizo visible el resultado nativo: **162 tests ejecutados, dos fallidos**. Compiló las fuentes y tests; no es evidencia de una entrega completa aprobada.
- La búsqueda incluye nombre, área, propósito **y versión**. «Numérica» también coincide legítimamente con la advertencia de versiones de Barthel. Se corrige la expectativa del test y se comprueban tanto la búsqueda amplia con/sin acento como la específica «ESCALA NUMERICA». No se restringe la búsqueda para satisfacer un test incorrecto.
- La prueba de reinicio 4AT tras guardar ahora espera el cierre del aviso transitorio usando los relojes virtuales Compose/Android y verifica que el aviso ya no existe antes del toque real. Se mantienen las aserciones de resultado ausente y guardado deshabilitado, y se añaden las de cuatro respuestas vacías y contador cero. Se incorpora además un test independiente de reinicio **antes** de guardar, sin crear registros.
- No se borran/deshabilitan tests, no se cambian pesos clínicos para aprobarlos y no se modifica el workflow ni sus permisos. La suite nativa contiene **163 tests declarados** tras añadir esta regresión; el resultado efectivo corresponde al run del SHA que se entregue.

**Puerta de entrega:** no pedir a Google que actualice hasta verificar que la CI del SHA final está en verde. `escalas-clinicas-v1` solo se publicará tras esa verificación; su anotación guardará el SHA y URL de la ejecución aprobada. La existencia de este documento no acredita por sí sola ese resultado.

## Tercer control: tests Android correctos y bloqueo de lint

El run [41542944011](https://github.com/iDreyK-ii/Enfermer-a-app/actions/runs/41542944011), SHA `2931439`, superó la fase de tests Android y llegó a ensamblado/lint. Lint bloqueó la entrega con `ProduceStateDoesNotAssignValue` en la carga asíncrona del catálogo (1 error y 25 advertencias en ese run). Se hizo explícito el estado observable con `remember(context)` y `LaunchedEffect(context)`, conservando IO fuera del hilo principal, cancelación y aviso de error. No se desactiva lint ni se crea un baseline para ocultarlo.

Se añade un resumen técnico de CI con recuento de casos XML, fallos, omisiones, lint e identidad del APK inspeccionado. Sale como anotación pública acotada y JSON dentro de las evidencias existentes; no requiere más permisos ni acceso a secretos. Cinco pruebas adicionales comprueban ausencia/incoherencia de evidencia, fallos y exclusión de campos ajenos. El resumen no afirma validación clínica, importación de Google o prueba física.

Repetición local tras este cambio: **100 Python** (64 guardas/diagnóstico, 15 conversor, 21 backend), **5 SQLite y 17 PGLite**, todos superados. Evidencia local: `qa-rapida-final.txt` de la entrega. Android/lint/APK se verifican en el nuevo run.
