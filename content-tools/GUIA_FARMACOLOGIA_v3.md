# Ficha farmacológica v3 — estructura del PDF del usuario

Requiere **app v0.4+**. El PDF aportado tiene 17 conceptos en sus dos primeras páginas; la tercera es un separador. No incluye dosis ni monografías verificadas. La alusión a DIGEMID en el encabezado no acredita aval, registro o revisión profesional.

## Los 17 conceptos y su ubicación

`nombre` pertenece a la identidad del medicamento. Los demás conceptos se guardan dentro de `secciones` como texto literal, sin ejecutar fórmulas o instrucciones.

| N.º | Concepto del PDF | Clave |
|---|---|---|
| 1 | Nombre genérico | `nombre` |
| 2 | Nombre comercial | `nombres_comerciales` |
| 3 | Presentaciones | `presentaciones` |
| 4 | Mecanismo de acción | `mecanismo_accion` |
| 5 | Indicaciones | `indicaciones` |
| 6 | Almacenamiento | `almacenamiento` |
| 7 | Formas de presentación | `formas_presentacion` |
| 8 | Reconstitución | `reconstitucion` |
| 9 | Dosis | `dosificacion` |
| 10 | Dilución | `dilucion` |
| 11 | Vía de administración | `vias` |
| 12 | Velocidad de administración | `velocidad_administracion` |
| 13 | Contraindicaciones | `contraindicaciones` |
| 14 | Estabilidad de solución | `estabilidad` |
| 15 | Sueros compatibles | `sueros_compatibles` |
| 16 | Incompatibilidades | `incompatibilidades` |
| 17 | Efectos adversos / observaciones / RAM | `reacciones_adversas` |

Se conservan separados presentaciones/formas, almacenamiento/estabilidad y compatibilidad/incompatibilidad. Un registro sanitario, marca, laboratorio o compatibilidad debe respaldarse con la fuente pertinente, no inferirse del título de la plantilla.

## Lo esencial para enfermería

Se añaden `objetivo_terapeutico`, `antes_administrar`, `durante_administracion` y `despues_administracion`. La vista resume estos apartados y muestra los datos de preparación, monitorización y alertas que estén escritos. **No genera controles, umbrales, dosis ni decisiones clínicas.**

También se conservan descripción, precauciones, interacciones, cuidados, poblaciones especiales, educación y demás campos anteriores: **30 secciones opcionales** en total. El editor las agrupa para no presentar todos los campos de entrada a la vez.

- Celda vacía o clave ausente: **pendiente**, nunca «sin riesgo».
- Si realmente no corresponde: escribir «No aplica» y explicar el motivo con respaldo. La app conserva ese texto; no decide aplicabilidad.
- Puede guardarse cualquier ficha parcial como borrador. Una ficha 100 % llena no equivale a revisión clínica.
- Editar una ficha local la devuelve a borrador y conserva su procedencia.

## Archivos listos para completar

- `Plantilla_farmacos_v3.xlsx`: instrucciones, metadatos, mapa de los 17 conceptos y hojas editoriales. No cambiar encabezados ni usar fórmulas; sí dejar celdas vacías.
- `plantilla_farmaco_v3.json`: el mismo ejemplo, sin información clínica inventada.
- `esquema_contenido_v3.schema.json`: contrato de formato.
- `crear_plantilla_v3.py`: generador reproducible que no sobrescribe plantillas anteriores.

```sh
python3 -m pip install -r content-tools/requirements.txt
python3 content-tools/convertir_plantilla.py content-tools/Plantilla_farmacos_v3.xlsx mis-farmacos.json
```

Luego importar en Android → Biblioteca local. Usa IDs estables y aumenta `version_contenido` para actualizar un paquete existente. La importación muestra qué se añade/reemplaza y pide confirmación. Límites actuales: 2 MB, 500 registros agregados y 300 por tipo.

**Al actualizar un ID existente se reemplaza la ficha confirmada; no es un parche que mezcle campos.** No omitas datos anteriores que todavía deban conservarse. Revisa el paquete y la vista previa; conserva un respaldo antes de cambios editoriales importantes.

Para publicar desde el servidor, seguir `backend/README.md`. Solo el canal `catalogo-oficial`, con versión creciente y revisión documentada, alimenta el conector de publicaciones. **No subir directamente los borradores de ejemplo.** La plantilla puede usarse localmente sin cuenta ni servidor.

## Compatibilidad y seguridad

v0.4 lee formatos 1/2/3 y exporta 3. Las plantillas y ejemplos 1/2 se conservan. v0.2/v0.3 rechazan formato 3: no descartar los campos nuevos para forzar compatibilidad. La base SQLite continúa en versión 2 porque almacena el contenido en JSON.

Catálogo, cuaderno y multimedia tienen respaldos distintos. Registrar una cuenta no copia el contenido privado del teléfono al servidor. La publicación requiere proceso editorial y permisos reales, no solo un archivo que pase validación de estructura.
