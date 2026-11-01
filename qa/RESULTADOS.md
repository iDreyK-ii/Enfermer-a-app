# Verificación técnica — Android iDreyK 360 v0.3

Fecha: **1 de noviembre de 2026**. Todas las comprobaciones enumeradas aquí se ejecutaron; no son una lista de pruebas futuras.

## Resultado final

- **77 pruebas nativas/JVM: 77 superadas, 0 fallos, 0 omitidas.**
- **15 pruebas del conversor: 15 superadas.** Esquemas 1/2, protocolos, referencias, límites estructurales y errores de plantilla.
- `assembleDebug` y `lintDebug`: correctos. **0 errores y 25 advertencias de lint.** Se conserva el informe, no se presentan las advertencias como resueltas.
- Firma APK verificada; mismo certificado y paquete que v0.2, versionCode 3. No permiso de Internet ni almacenamiento general.
- CRC del APK comprobado; los 84 recursos/assets/original de diseño versionados en la base permanecen sin cambios.
- APK v0.2 conservado sin modificaciones.

| Suite | Pruebas | Fallos |
|---|---:|---:|
| `CoreTests` | 27 | 0 |
| `InterfaceTests` | 2 | 0 |
| `ModuleInterfaceTests` | 7 | 0 |
| `ModuleRepositoryTests` | 11 | 0 |
| `NursingModuleTests` | 21 | 0 |
| `RepositoryTests` | 9 | 0 |

## Cobertura de la ampliación

- Capurro B: las 1.600 combinaciones de puntajes, constante 204 y representación semanas/días.
- Capurro A: 6.400 combinaciones de seis puntuaciones externas, constante 200 y exclusión de pezón. No valida las maniobras ni la ficha externa.
- Apgar y Silverman: 243 combinaciones cada uno; cero completo, faltantes, puntos no permitidos y descriptores de frecuencia 100 o más.
- Matemáticas de volumen/dilución: equivalencias de masa, UI aisladas, conservación de cantidad en mezcla ideal, valores inválidos, concentración final excesiva y resultado positivo pequeño sin cero falso de formato.
- Catálogo v2: protocolos, conservación de v1, rechazo de protocolos en v1, edición local a borrador, procedencia, deshacer, eliminación y catálogo vacío exportable.
- Cuaderno: formatos y límites, persistencia, importación/mezcla, borrado, deshacer y conservación de notas antiguas.
- SQLite: fixture de esquema v1 actualizado a v2; conserva fichas, notas, favoritos, prácticas, versiones y punto de deshacer. **No equivale a actualización instalada en teléfono.**
- Multimedia: copia privada, nombres internos, FileProvider limitado, renombrado, eliminación sin tocar original, reapertura, archivo vacío/excesivo y ausencia de restos temporales en fallos probados. No certifica apertura en un visor real.
- Tendencia de FC: se alimenta únicamente de registros ficticios guardados para la ficha seleccionada, ordena y limita a veinte; no inventa datos de respaldo.
- Interfaz real Compose en entorno simulado: rutas nuevas, selección/guardado Capurro, reinicio de Apgar al cambiar momento, cálculo y limpieza de resultado, edición farmacológica, plan PAE estructurado, apuntes/protocolos y entrada a multimedia. Se mantienen las dos pruebas anteriores de farmacología y preferencia de tema.

Durante el desarrollo se detectó un fallo del recorrido rápido entre guardado de apunte y protocolo. Se ajustaron los avisos al navegar y la serialización/cierre de E/S; el recorrido focalizado y la suite completa final pasan. No se alteró una prueba para omitir la comprobación de guardado.

## Artefacto

- Archivo: `Enfermeria_iDreyK_360_v0.3_debug.apk`.
- Tamaño: **9,389,862 bytes**.
- SHA-256: `7eb4854d1d6c927423a8ddaca0f1331522927d5b431187b45ca75703dd62bd99`.
- Paquete: `com.idreyk.enfermeria360.dev`.
- Versión: `0.3.0-dev-debug` / código `3`.
- Certificado SHA-256: `d6874c68417eefb1b1b902bf82ba4ce7d8734e47877e12bd1733eb498f0e522b`.
- Android mínimo 26 (8.0), SDK de compilación/objetivo 35.

## Evidencia

`TEST-*.xml`, `pruebas-android.txt`, `conversor-pruebas.txt`, `compilacion-final.txt`, `lint-resultados.xml`, `firma-apk.txt`, `apk-metadatos.txt`, `apk-permisos.txt` y `resultados.json`.

Herramientas: JDK 21, Gradle 8.9, AGP 8.7.3, Kotlin 2.0.21, Compose BOM 2024.12.01, Robolectric 4.14.1. Compilación y ejecución de pruebas separadas para no exceder memoria. El mensaje de empaquetado sin eliminar símbolos de `libandroidx.graphics.path.so` no impidió el ensamblado.

## Lo que NO está demostrado

No se logró anteriormente un emulador Android completo operativo y no se repitió como si hubiera sido exitoso. **No hay instalación física ni aprobación visual en teléfono.** Faltan pruebas reales de actualización, SAF, visores, fabricantes, accesibilidad, rotación, cargas extensas, proceso muerto y poco almacenamiento.

No es validación clínica de instrumentos, fuentes, exploración, población ni equivalencia digital. Braden/Morse son registros externos; Glasgow pediátrica no es cuestionario propio; Capurro A usa puntuaciones externas. No se validaron fármacos ni recetas. La app no es una historia clínica autorizada.

La siguiente prueba manual está en `PRUEBA_EN_TELEFONO.md`; sus casillas no se marcan por haber pasado Robolectric.
