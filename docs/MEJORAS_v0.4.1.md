# Pulido previo al repositorio — v0.4.1

Se amplía el mismo proyecto Android, no se reemplaza por otra aplicación. Se conservan el diseño elegido, los 84 archivos anteriores de recursos/assets, los datos locales y las plantillas. No se han añadido monografías, dosis o supuestas validaciones clínicas.

## Implementado ahora

### 1. Importaciones más seguras

- Comparación por ficha: altas, sustituciones, campos modificados y campos con contenido que quedarían vacíos/ausentes.
- Señala las fichas provenientes del editor local para advertir que una actualización puede reemplazar trabajo propio.
- Exige una aceptación explícita para sustituir IDs existentes; las altas no requieren aceptar sustituciones inexistentes.
- Huella del paquete y del estado revisado. Dentro de la transacción se verifica que la biblioteca no haya cambiado desde la vista previa. Una revisión antigua no puede pisar una edición posterior.
- Se vuelve a interpretar el texto inmutable del paquete antes de confirmar; modificar accidentalmente un JSONObject de presentación no altera lo que se guarda.
- Se conserva el respaldo/deshacer y no se eliminan registros ausentes del paquete.
- Vista previa en páginas de 12 fichas, no cientos de tarjetas compuestas simultáneamente. Cambiar página vuelve al comienzo de la revisión.

Esto compara estructura y contenido escrito; **no interpreta si una dosis, indicación o control es correcto**. Tampoco mezcla campos automáticamente ni convierte un borrador parcial en ficha validada.

### 2. Pantallas pequeñas y accesibilidad

- La bienvenida deja de tener altura fija: crece con el texto y evita superponerlo con la ilustración.
- El arte decorativo cede espacio en pantallas estrechas o con letra grande.
- Cuadrícula adaptable al ancho y escala de fuente, con tarjetas de altura coherente y descripción de colección para accesibilidad.
- Marca y rótulos de navegación ajustados para texto ampliado; botón de volver de 48dp.
- La pestaña Módulos permanece seleccionada al consultar las fichas ficticias de pacientes.

### 3. Movimiento y respuesta táctil

- Respuesta suave de escala al pulsar tarjetas y transición de color/énfasis al seleccionar pestañas.
- Son efectos finitos y pequeños; no se añadieron nuevos bucles decorativos permanentes.
- Se desactivan los nuevos cambios de escala cuando se reduce el movimiento. Se mantienen las salvaguardas de batería, memoria, accesibilidad y segundo plano.
- La ilustración existente solo anima cuando está visible y la política lo permite.

No se midieron FPS, consumo ni rendimiento en hardware de distintas gamas.

### 4. Edición y archivos

- Se bloquean los campos durante las escrituras pendientes y se retira el foco del teclado; no se deja continuar editando mientras termina el cambio local.
- Se verifica con pruebas la advertencia existente al salir de una ficha sin guardar: permite seguir editando o descartar expresamente.
- JSON excesivamente anidado se rechaza antes del análisis recursivo en catálogo, cuaderno y respuestas de nube. Los corchetes dentro del texto se conservan.
- La pantalla de cuenta aclara que entrar/salir no sube, borra ni separa los datos locales por cuenta.

**Guardar sigue siendo una acción manual.** La protección de salida no es autosave ni recuperación garantizada de un formulario sin guardar después de un cierre forzado o muerte del proceso. Guardar fichas parciales periódicamente y probar restauración/rotación con documentos grandes en equipos reales.

### 5. Preparación para GitHub

Workflow de calidad, script reproducible, guardas básicas de archivos/credenciales y verificador de APK. Acciones fijadas a commits, permisos mínimos y pruebas sin claves de nube ni despliegue. La sintaxis/expresiones del YAML se verificaron con actionlint; **el workflow todavía no se ejecutó en GitHub**.

Ver `docs/GITHUB_Y_CI.md`. La firma de los artefactos de CI será temporal y no debe confundirse con la firma conservada de los APK entregados.

## Compatibilidad

- APK `0.4.1-dev-debug`, versionCode5, mismo paquete y certificado.
- SQLite continúa en esquema2; no hay nueva migración ni borrado de tablas.
- Lee catálogos1/2/3 y exporta3. El canal Supabase de formato3 sigue compatible.
- Siguen vigentes los límites de 2MB, 500 registros agregados y 300 por tipo.
- La plantilla XLSX/JSON v3 no cambia. El PDF de diseño v0.4 permanece como referencia visual, no como evidencia QA actual ni captura del APK.

## Comprobaciones

Resultados y evidencia en `qa/RESULTADOS_v0.4.1.md` y `qa/v041/`. El APK se recompuso después de limpiar únicamente archivos generados de `app/build`, conservando fuentes, datos de entrada y claves.

Siguen pendientes un proyecto Supabase real y sus correos/cuentas/Storage, ejecución remota de CI, instalación física, pruebas de varias gamas, revisión clínica y requisitos de publicación. Esta entrega no se declara de producción.
