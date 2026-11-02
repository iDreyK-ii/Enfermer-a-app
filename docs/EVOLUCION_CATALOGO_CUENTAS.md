> **Estado posterior v0.4.1:** pulido de accesibilidad, movimiento, importación protegida y preparación CI realizado. Ver `MEJORAS_v0.4.1.md` y `../qa/RESULTADOS_v0.4.1.md`. El alcance de arquitectura que sigue se mantiene; Supabase y GitHub remoto no están conectados.

# Evolución solicitada: farmacología, cuentas y rendimiento

Fecha de solicitud: 2026-11-01. Estado: selección visual recibida, mejoras integradas y APK v0.4 compilado con comprobaciones locales. La conexión Supabase y las cuentas reales siguen sin configurar. Ver qa/RESULTADOS_v0.4.md.

## Cambio de alcance expresamente solicitado

El usuario ahora pide cuentas y un lugar donde subir información farmacológica que llegue al aplicativo mediante actualizaciones de contenido. Esta petición sí amplía la edición v0.3 offline/sin cuentas; no se deriva de la plantilla genérica anterior. Se mantiene Android nativo y la lectura de la biblioteca ya descargada sin conexión. La lectura local no se bloqueará detrás de un registro salvo nueva decisión expresa.

No sustituir el proyecto por una aplicación web. No borrar medicamentos, favoritos, planes o notas ya guardados. No subir automáticamente notas de pacientes o ejercicios privados.

## Elección técnica para esta evolución

- **Servidor: PostgreSQL administrado mediante Supabase.** Catálogo editorial, revisiones y metadatos de publicación.
- **Cuentas: Supabase Auth.** Las credenciales pertenecen a su sistema de autenticación; no crear una tabla propia de contraseñas en SQLite. Una tabla de perfiles guardará solo datos mínimos de cuenta, vinculados a `auth.users`.
- **Archivos: Supabase Storage.** Documentos fuente/borradores privados; paquetes revisados publicados por un canal separado. No almacenar PDF/binarios como enormes columnas de texto del catálogo.
- **Dispositivo: SQLite existente.** Consultas y contenido descargado; preferencias locales y archivos privados. El esquema documentado en `database/schema.sql` es el esquema realmente ejecutado por v0.3 y v0.4.
- No se ha creado un proyecto Supabase, aceptado una tarifa, elegido región de alojamiento ni recibido URL/clave pública de proyecto. No hay cuentas o sincronización operativas por escribir esta nota.

Supabase permite acceso cliente mediante su API y una clave publicable, con políticas RLS y permisos mínimos. Las claves secretas o `service_role` nunca deben incluirse en APK, ZIP público ni configuración de cliente. [1](https://supabase.com/docs/guides/database/secure-data) [2](https://supabase.com/docs/guides/getting-started/api-keys)

## Flujo editorial acordable

1. El editor carga una ficha estructurada y sus referencias en un espacio privado.
2. Se guarda como borrador. Subir un PDF no lo convierte en contenido clínicamente validado ni publica automáticamente lo extraído.
3. Se comprueban estructura, procedencia, unidades, campos pendientes y revisión profesional aplicable.
4. Un rol editorial autorizado publica una revisión y un manifiesto de contenido versionado. Un lector no puede atribuirse el rol de publicador desde su perfil.
5. El aplicativo consulta novedades por HTTPS, descarga con límites, verifica versión/integridad y confirma la escritura local de forma transaccional. Una descarga fallida no sustituye la biblioteca anterior.
6. La ficha nueva aparece en la biblioteca local. **Actualizar contenido no exige reinstalar el APK** si el formato es compatible. Cambiar pantallas, lógica o formato incompatible sí puede exigir otra versión de la app.

No proponer una conexión directa desde Android con usuario/contraseña de PostgreSQL. No usar una carpeta pública como autorización de publicación. Prever borradores, revisiones publicadas inmutables, perfiles mínimos, permisos editoriales y manifiestos/versiones de catálogo. La seguridad debe comprobarse en servidor, no solamente ocultando botones.

## PDF aportado por el usuario

Archivo: `uploads/farmacológia estructura.pdf`. Se revisaron sus tres páginas: los apartados están en las páginas 1–2; la tercera solo contiene un separador. Es una **plantilla de 17 conceptos**, no una monografía con dosis reales o fuentes identificables. La frase introductoria sobre DIGEMID no prueba revisión, registro sanitario ni respaldo institucional.

Conceptos a conservar:

1. Nombre genérico.
2. Nombre comercial.
3. Presentaciones.
4. Mecanismo de acción.
5. Indicaciones.
6. Almacenamiento.
7. Formas de presentación.
8. Reconstitución.
9. Dosis.
10. Dilución.
11. Vía de administración.
12. Velocidad de administración.
13. Contraindicaciones.
14. Estabilidad de solución.
15. Sueros compatibles.
16. Incompatibilidades.
17. Efectos adversos / observaciones / RAM.

No fusionar automáticamente los conceptos 3/7, 6/14 o 15/16: pueden describir información diferente. Conservar también los campos ya implementados que no figuran expresamente en este PDF (interacciones, poblaciones especiales, educación, etc.). No descartar datos anteriores para que coincidan con una plantilla nueva.

### Vista de enfermería adicional solicitada

Presentar una lectura resumida antes de la ficha técnica:

- Para qué se administra / objetivo terapéutico documentado.
- Antes de administrar: verificaciones y controles específicos documentados para el medicamento.
- Preparación y administración: datos revisados que correspondan a presentación/vía.
- Durante y después: vigilancia, alertas y registro pertinente.

Estos apartados se completarán con fuentes y revisión, no con instrucciones clínicas inventadas. Una ficha parcial debe admitirse como borrador. Distinguir **pendiente**, **informado** y **no aplica justificado**: un vacío no implica ausencia de riesgo ni falta de una contraindicación. El porcentaje de campos llenos no mide seguridad clínica.

## Rendimiento y accesibilidad: criterio de desarrollo

- Modo **Automático**, **Ligero** y **Completo**, seleccionable sin perder funciones de enfermería.
- En automático, considerar señales de poca RAM, presión de memoria y ahorro de batería; no deducir capacidad por marca/precio del teléfono. Cualquier umbral adicional será una heurística comprobable, no una garantía de rendimiento.
- Ligero: ilustración estática, sin bucles decorativos, sin transiciones costosas y sombras reducidas. Mantener la misma información, legibilidad y navegación.
- Completo: transiciones breves y animaciones discretas; evitar vídeos/fondos pesados y trabajo continuo en segundo plano.
- El ajuste de reducción de movimiento de accesibilidad prevalece. Revaluar señales al volver a primer plano y detener animación decorativa cuando no se ve la pantalla.
- Mantener modo oscuro, texto ampliado, objetivos táctiles y contraste. No ocultar advertencias o fuentes para ahorrar recursos.
- Para catálogos grandes: consultas/indexación/paginación, detalle bajo demanda y descargas por lotes. La edición v0.3 tiene límites de importación y carga del catálogo: deben migrarse/probarse antes de prometer un catálogo ilimitado.

## Orden de trabajo

1. Revisar la estructura del PDF y el código: realizado.
2. Propuestas mostradas y elegidas: inicio opción 2; ficha opción 1. La ficha tiene una corrección tipográfica legible derivada, conservando el original.
3. Integradas la jerarquía elegida, ficha ampliada y política de rendimiento, manteniendo datos y compatibilidad de lectura 1/2/3.
4. Incluidos contrato/plantilla v3, SQL cloud separado del local, cliente, publicación y guías de compilación/configuración.
5. Configurar un proyecto real y probar autorización, cuentas, cancelaciones, actualización fallida y lectura offline antes de declarar cloud operativo.
6. Pruebas en dispositivos de recursos distintos y revisión clínica siguen siendo necesarias.

Las imágenes de esta fase son **maquetas de diseño**, no capturas de un APK recompilado. No confundir código preparado, comandos documentados y servicios realmente configurados.
