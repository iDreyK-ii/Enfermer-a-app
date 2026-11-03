# QA — Android 0.6.0-dev / Base Maestra 2.1

Fecha: 2026-11-03. Resultado del código de esta entrega, no reciclado del hito de escalas.

| Comprobación ejecutada | Resultado |
|---|---|
| Python — guardas, traslado, catálogos, importación, publicación | **111**: 75 + 15 + 21, sin fallos |
| SQLite | **5/5**, esquema local v2/ocho tablas conservado |
| PostgreSQL/PGLite | **17/17**, entorno local, sin servicio real |
| Android/JUnit/Robolectric | **235/235**, **18 suites**, 0 fallos, 0 errores, **0 omisiones** |
| Lint Android | **0 errores, 26 advertencias** |
| Ensamblado APK debug | Correcto; code **7**, `0.6.0-dev-debug`, min 26, target 35 |
| Integridad/metadatos/permisos/firma | Verificados; certificado local conservado comparado explícitamente |
| Capturas de render nativo | **10** PNG, API 35; 390×844, 800×1280, 320×640 px a mdpi |
| Pruebas instrumentadas PDF | **2 compiladas y APK de pruebas ensamblado; no ejecutadas** |

La base anterior pasó 163 pruebas Android/11 suites. Se añaden **72 ejecutadas**: 27 de aritmética, 22 de catálogo/repositorio/cuaderno/Markdown/rangos, 11 de interfaz, 7 de render y 5 de contratos de impresión. Las dos pruebas instrumentadas pendientes **no se suman** al resultado de 235 ni se ocultan como éxitos.

## Evidencia

- `base_maestra_v21/antes.json`: ejecución anterior a los cambios.
- `base_maestra_v21/lectura.json`: lectura y entrada editorial.
- `base_maestra_v21/resultados.json`: resumen final estructurado; el hash del APK corresponde a la compilación de la entrega.
- `base_maestra_v21/junit/`: XML de las 18 suites ejecutadas.
- `base_maestra_v21/lint.xml`: informe de lint.
- `base_maestra_v21/preservacion.json`: archivos anteriores y componentes preservados.
- `base_maestra_v21/capturas/`: PNG/metadatos, dimensiones, método y SHA-256.
- `base_maestra_v21/desglose_visual.html`: galería autocontenida de esos renders, no una recreación HTML de la app.

La comprobación de fuentes del script usa objetos del commit indicado. Un manifiesto generado mientras el árbol estaba modificado no acredita por sí solo las fuentes nuevas; la entrega se compara nuevamente con el commit publicado y con el ZIP. Ver manifiesto y verificación de entrega comunicados con el SHA final.

## Alcance de las pruebas

Se comprueban, entre otras cosas: factor de goteo sin valor supuesto; edad/base explícitas para cantidad/kg; cero/vacío/incompletos; decimales y unidades; invalidación de resultados; 554 maestros/737 apariciones y regeneración reproducible; rangos externos sin cuestionarios; conservación del cuaderno v1/v2; campos SOAPIE/SBAR; UTF-8 y límites Markdown; progreso/respuestas inválidas/versiones/persistencia; lista institucional frente a lista propia; navegación/búsqueda; selección del pistón por toque; zoom; pausa/reinicio de higiene; reanudación de caso; 390/800/320 dp.

Los tests de impresión ejecutados comprueban preparación, cancelación, límite de 100 páginas y área imprimible insuficiente. **No comprueban generación de PDF**. El generador real está cubierto por `EducationalPdfDeviceTests` para dispositivo/emulador, que aquí solo se ha compilado. Tampoco se han ensayado los diálogos del servicio de impresión o proveedores SAF reales.

## Renderizado y límites del entorno

Robolectric 4.14.1, gráficos **NATIVE**, framework **API 35**, layout Compose de producción, rasterización del `View` Android mediante `Canvas(Bitmap)`. No se dibujaron pantallas ficticias en HTML. Movimiento reducido activado para capturas estables; modo claro explícito en teléfono/tablet y oscuro en compacto.

- Sin dispositivo físico ni emulador que ejecute el APK. Las imágenes no prueban instalación, teclados/recortes, GPU, hardware Keystore, TalkBack real ni FPS.
- PixelCopy no recibió el callback de display en este entorno sin pantalla; por eso se usa `View.draw`.
- La rasterización software API 28 recortó una vista de higiene pese a límites geométricos correctos; los renders finales se hicieron en API 35. No se infiere a partir de ello un resultado en hardware Android 9.
- Robolectric 4.14.1 no contiene implementación nativa de `PdfDocument`: la tentativa de render PDF devuelve `document is closed!` antes de crear páginas. No se simula el éxito: las pruebas de generación se separaron a `androidTest`, pendientes.
- El primer intento general terminó por OOM al acumular bibliotecas nativas en `/tmp` (tmpfs) y mostró fallos de selectores/aviso superpuesto. Se corrigieron selectores, se descarta el aviso mediante su acción accesible antes del toque, se usan temporales en disco y un fork secuencial por suite. No se redujeron validaciones clínicas para obtener verde.

## Advertencias, firma e infraestructura

Las **26 advertencias** son siete avisos de versiones de dependencias y 19 recursos sin uso. Además, la consola del compilador muestra avisos conocidos de SDK XML/deprecaciones; no son errores ocultados ni prueba de compatibilidad física.

Certificado público local esperado: `d6874c68417eefb1b1b902bf82ba4ce7d8734e47877e12bd1733eb498f0e522b`. La clave privada no se incluye. En CI la comparación con este certificado se deja vacía intencionadamente: un APK de runner tiene firma temporal y no se presupone instalable encima del APK entregado.

QA fuerza URL y clave de nube vacías. No envía OTP, no usa cuentas reales, no modifica el servidor ni repite migraciones. La infraestructura previamente creada, las plantillas pendientes y la sesión real aplazada no se convierten en una verificación nueva.

No hay certificación clínica/farmacéutica, permisos digitales nuevos, stock acreditado, importación Google comprobada, prueba física ni salida PDF real verificada. La CI del commit final debe consultarse aparte; la evidencia local no se presenta como un run remoto.

## Reproducir

Preparar JDK/SDK/Python/Node según `docs/COMPILAR_Y_PROBAR.md` y ejecutar:

```sh
bash scripts/comprobar.sh --todo
```

Para las dos pruebas pendientes, en un dispositivo/emulador de prueba autorizado y con firmas compatibles:

```sh
./gradlew :app:connectedDebugAndroidTest
```

Ese segundo comando no se ejecutó en esta entrega. Su resultado futuro debe añadirse, con API/dispositivo, sin reetiquetar la compilación previa como una ejecución.
