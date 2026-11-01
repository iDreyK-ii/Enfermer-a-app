# Resultados técnicos de la entrega Android

**1 de noviembre de 2026 · versión 0.2.0-dev-debug**

| Comprobación | Resultado |
|---|---|
| Compilación del APK de desarrollo | Correcta |
| Firma APK | Verificada con apksigner |
| Lógica, aritmética y parser | 27/27 |
| Repositorio SQLite / importación / deshacer | 9/9, con Robolectric |
| Interfaz Compose: farmacología y tema local | 2/2, con Robolectric |
| Total de pruebas de la aplicación | **38/38** |
| Conversor Excel → JSON | **10/10** |
| Lint Android | **0 errores**, 27 advertencias |
| Permiso de internet en APK final | No solicitado |
| Instalar y recorrer APK en teléfono real | **Pendiente** |
| Instalación integral en emulador de sistema | **No completada** |
| Validación clínica / regulatoria | **Pendiente** |

Las advertencias de lint corresponden a dependencias con versiones más recientes y recursos no utilizados; se conservan en `lint-resultados.xml`. No se interpreta la ausencia de errores de lint como una auditoría de seguridad.

## Qué cubren las pruebas

- Las 120 combinaciones numéricas de Glasgow, componentes ausentes y NV sin total.
- Operaciones de práctica, límites de entrada, NaN, infinito y subdesbordamiento numérico.
- Versiones enteras, fechas, tipos de campos, IDs, duplicados, secciones y referencias.
- Rechazo de UTF-8 dañado sin reemplazo silencioso; límites de tamaño.
- Inicialización repetible, notas separadas, favoritos y ejercicios al reabrir el repositorio.
- Previsualización sin escritura; control de versiones; importación y punto de deshacer transaccionales.
- Rechazo del catálogo agregado por cantidad o bytes sin perder el punto anterior de deshacer.
- Exportación reimportable, conservación de atribución y exclusión de notas de práctica.
- Inicio de la actividad, navegación a farmacología y campos de una ficha, y cambio de tema con preferencia local.
- Conversión de plantilla, texto literal, fechas, IDs, referencias enlazadas y rechazo de fórmulas de Excel.

## Alcance real del entorno

Las pruebas de repositorio e interfaz ejecutan código Android mediante **Robolectric / API 28 (Android 9) simulado en JVM**, no mediante un teléfono físico. Las pruebas de interfaz inspeccionan semántica y acciones, no certifican el diseño visual en todas las pantallas.

El entorno disponía de aproximadamente 2 GB de RAM y carecía de `/dev/kvm`. Se intentó un emulador completo Android 10/API 29: con RAM reducida arrancaron servicios, pero no se obtuvo una instalación estable del APK; el intento de instalación informó que el servicio de paquetes no estaba disponible. **Ese intento no se cuenta como una prueba aprobada.**

Quedan pendientes el selector de archivos Android de extremo a extremo, rotación y muerte de proceso, accesibilidad con TalkBack, múltiples densidades y fabricantes, rendimiento, poco almacenamiento, revisión clínica y auditoría de seguridad. Usa `PRUEBA_EN_TELEFONO.md` para registrar esas comprobaciones.

Las 25 pruebas del prototipo HTML corresponden a otra entrega y **no** se suman a estas 38 pruebas. No se promete un funcionamiento al 100 %.

## Identificación del APK

- Archivo: `Enfermeria_iDreyK_360_v0.2_debug.apk`
- Tamaño: 9,664,206 bytes
- SHA-256: `bb624e2059ed20874440ceefc97cb042adfa15fb840557976728a9c87f255076`
- SDK mínimo: 26; objetivo: 35.
- ID de desarrollo: `com.idreyk.enfermeria360.dev`.

Los XML JUnit, metadatos de APK, firma y registros finales se incluyen junto con este informe.
