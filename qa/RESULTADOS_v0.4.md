# Comprobación de iDreyK 360 v0.4 — 2026-11-01

## Ejecutado en esta entrega

| Comprobación | Resultado |
|---|---|
| Compilación Kotlin/pruebas | Correcta |
| Android/JVM + Compose con Robolectric | **106 pruebas, 0 fallos, 0 errores, 0 omitidas** |
| Conversor XLSX, compatibilidad 1/2 | **15 pruebas superadas** |
| Plantilla v3 y publicación | **9 pruebas superadas** |
| PostgreSQL/RLS con PGlite | **17 comprobaciones superadas** |
| Espejo de SQLite local | **5 pruebas superadas** |
| Ensamblado del APK | Correcto |
| Lint | **0 errores, 24 advertencias**: 5 GradleDependency y 19 UnusedResources |
| Firma | Mismo certificado que v0.2/v0.3 |
| APK anteriores | SHA-256 sin cambios |
| Recursos/assets anteriores | 84 archivos comparados byte a byte, sin cambios |

Los XML, logs, metadatos, permisos, firma y resumen JSON se conservan en `qa/v04/`. Los resultados de `qa/RESULTADOS.md` corresponden a v0.3 y no se han borrado ni mezclado con los nuevos.

## Cambios comprobados

- Lectura de formatos 1/2/3; campos nuevos solo en formato 3; exportación 3.
- Los 17 conceptos del PDF, 30 secciones opcionales, grupos de edición y búsqueda por nombre comercial/sinónimos.
- Guardado, exportación y deshacer de campos nuevos sin descartar el contenido anterior.
- Política de animaciones por recursos, accesibilidad, ahorro de energía y estado de primer plano; preferencia persistente probada desde UI.
- UI de nube sin configurar no presenta éxito ni permite enviar solicitudes.
- Contratos del conector con transporte simulado: OTP, datos de sesión, identidad/versión de paquete, integridad, límites y rechazo de borradores.
- AES-GCM: ida/vuelta, aleatoriedad, rechazo de clave distinta y alteración. Esto no equivale a comprobar el hardware Keystore de un teléfono.
- RLS (incluye SQLSTATE esperado en los rechazos): perfiles aislados, lectores sin privilegios editoriales ni acceso a fuentes privadas, publicación visible solo al habilitarla, revisiones inmutables y checksum exigido.

## APK

- Archivo: `Enfermeria_iDreyK_360_v0.4_debug.apk`.
- Paquete: `com.idreyk.enfermeria360.dev`.
- Versión: `0.4.0-dev-debug`, versionCode 4; Android 8.0+/API26, objetivo35.
- Tamaño: **9.962.173 bytes**.
- SHA-256: `8eff28bfabd81db4fbf64a02c623fa7a0f0dfca9226e9fd026baffafa15db5ce`.
- Certificado SHA-256: `d6874c68417eefb1b1b902bf82ba4ce7d8734e47877e12bd1733eb498f0e522b`.
- URL/clave publicable de nube **vacías** en BuildConfig comprobado. No hay claves secretas ni cuenta de administrador de muestra.
- Declara INTERNET para el conector opcional. No solicita contactos, cuentas del sistema, cámara ni almacenamiento general. Se conserva FileProvider restringido.

## Incidencias detectadas y resueltas

- Una prueba usó `Files.readString`, no disponible en su superficie de compilación Android; se sustituyó por lectura Kotlin compatible.
- La suscripción de batería mediante ContextCompat exigía un permiso sintético en Robolectric. Se usa el receptor de plataforma: no exportado en API33+ y sobre una acción protegida del sistema en API26–32; los valores se leen del sistema, no de extras externos.
- La fixture de límite agregado de formato1 pasó accidentalmente de 20 a 30 campos. Ahora usa explícitamente los 20 campos de ese formato, conservando la misma prueba de rechazo al superar 2MB.
- Las pruebas UI se ajustaron a los grupos de edición y al desplazamiento vertical/horizontal real de las nuevas opciones. Se repitió después la suite completa: 106/106.

## No ejecutado / límites importantes

- No se creó, configuró o desplegó un proyecto Supabase real. No hay prueba de correo/SMTP, registro real, renovación contra Auth real ni archivos de Storage reales.
- PGlite ejecuta PostgreSQL, pero simula las tablas básicas de Auth/Storage: no reemplaza la validación del servicio completo.
- No se instaló/actualizó el APK en un teléfono físico ni en un emulador completo operativo. No se midió FPS, consumo o inicio en distintas gamas.
- El selector de rendimiento aplica una heurística; no certifica que todo teléfono con cierta RAM alcance una tasa de cuadros.
- No hubo revisión clínica, incorporación de monografías verificadas o permisos nuevos. Las fichas iniciales permanecen como borradores.
- No hay sincronización automática de datos privados, publicación silenciosa, eliminación de cuenta dentro de la app, paginación ilimitada ni revocación automática de copias offline retiradas.

Instalar como actualización con la firma original, **sin desinstalar ni borrar datos**. No forzar un downgrade a v0.3 después de usar el nuevo formato. Una compilación en otra máquina necesita gestionar la firma; el ZIP no contiene la clave privada.
