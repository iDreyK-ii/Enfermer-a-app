> **Actualización v0.4:** ahora se admite formato 3 y la ficha ampliada. Consulta `GUIA_FARMACOLOGIA_v3.md`. Lo que sigue conserva las instrucciones históricas de los formatos 1/2; las plantillas anteriores siguen siendo legibles por v0.4.

# Contenidos locales — app v0.3

## Dos vías para trabajar

1. **En Android:** Biblioteca local → Crear medicamento/caso/lista/protocolo. También puedes abrir una ficha y editarla. Las ediciones locales siempre quedan como borrador; la procedencia y las fuentes previas se conservan. El último cambio del catálogo puede deshacerse.
2. **En computadora:** completar el XLSX y convertirlo a JSON, o preparar JSON directamente. La app no interpreta Word/PDF ni inventa contenido a partir de un nombre de archivo.

Usa material propio o autorizado, sin datos identificables de pacientes. La validación estructural no comprueba exactitud clínica, licencia o vigencia.

## Esquemas 1 y 2

- **Esquema 1:** medicamentos, casos y listas; se conserva para leer paquetes de v0.2.
- **Esquema 2:** incorpora `protocolos`. v0.3 lee ambos, pero sus exportaciones del catálogo usan esquema 2. **v0.2 no admite esquema 2.**
- Un paquete con protocolos debe declarar `version_esquema: 2`. La app nueva y el conversor rechazan protocolos dentro de esquema 1 para evitar omisiones silenciosas.
- JSON Schema: `esquema_contenido_v1.schema.json` y `esquema_contenido_v2.schema.json`.

La plantilla XLSX actual tiene ocho hojas: Instrucciones, Metadatos, Medicamentos, Casos, Listas, Items, Fuentes y Protocolos. No renombrar encabezados. La hoja Protocolos usa el mismo esquema de campos editoriales que Casos: `id`, `titulo`, `area`, `revision`, `revisor`, `fecha_revision`, `objetivo`, `contenido`.

```sh
python -m pip install -r requirements.txt
python convertir_plantilla.py Plantilla_contenidos_iDreyK_360.xlsx salida.json
python -m unittest -v test_converter
```

Las plantillas antiguas de esquema 1 sin hoja Protocolos siguen funcionando. Para añadir protocolos, cambiar el metadato a 2 y usar la hoja y encabezados de la plantilla actual.

## Metadatos obligatorios y actualización

`version_esquema`, `id_paquete`, `version_contenido`, `titulo`, `autor`, `fecha_actualizacion` (AAAA-MM-DD). La plantilla también pide `licencia` para documentarla; puede estar vacía en un borrador, pero no en una revisión declarada.

IDs de 1–100 caracteres: minúsculas, números, guion y guion bajo; primer carácter alfanumérico. No cambiar un ID para actualizar el mismo registro. Incrementar `version_contenido` (entero 1–9.999.999) si el paquete ya fue importado. El tipo + ID identifica una ficha; los demás registros se conservan. La importación presenta cantidades nuevas/reemplazadas y requiere confirmación.

## Contenido

- **Medicamentos:** nombre, grupo, sinónimos separados por `;`, veinte secciones literales: descripción, mecanismo, presentaciones, vías, indicaciones, contraindicaciones, precauciones, dosificación, reconstitución, dilución, compatibilidad, administración, estabilidad, efectos adversos, interacciones, monitorización, cuidados, poblaciones especiales, educación y alertas. Máximo 16.000 caracteres por sección.
- **Casos / protocolos:** título/área, objetivo (4.000 caracteres), contenido (60.000), fuentes y revisión.
- **Listas:** institución y edición; 1–100 ítems de título/detalle, vinculados en XLSX por `lista_id` y orden. No hay un número universal de correctos impuesto por el software. Título de ítem hasta 180 caracteres, detalle hasta 3.000.
- **Fuentes:** tipo `medicamento`, `caso`, `lista` o `protocolo`; `id_registro` existente; título y URL HTTPS completa sin credenciales. Editor y fecha de consulta opcionales. Hasta 30 fuentes por registro. Documentar también edición/permisos y contexto en los campos apropiados.

No usar fórmulas de Excel. El conversor rechaza fórmulas, IDs duplicados, encabezados cambiados, referencias huérfanas, revisiones incompletas y estructuras fuera de límites. Los textos como marcado se conservan literalmente; la app no ejecuta HTML o JavaScript.

## Revisión y procedencia

`borrador` es el estado inicial. `declarada_por_autor` exige revisor, fecha, fuentes y licencia, pero no significa certificación. Al editar en Android se vuelve a borrador y se retiran revisor/fecha anteriores como declaración activa. La procedencia inicial se conserva en `derivado_de`, además de referencias y los metadatos de la edición local.

No cambiar una etiqueta de estado para hacer pasar una ficha incompleta por verificada. No se incluye NANDA-I/NIC/NOC ni instrumentos restringidos sin aclarar autorización.

## Límites y respaldos

Paquete **y catálogo resultante**: 2.000.000 bytes UTF-8, 500 registros totales y 300 por tipo. Si la combinación de lo guardado y lo importado excede límites, no se modifica el catálogo. Exportar produce JSON legible, no cifrado. Un catálogo vacío puede exportarse como respaldo; un paquete editorial normal debe contener al menos un registro.

El catálogo incluye medicamentos, casos, listas y protocolos. **No incluye** planes, apuntes, multimedia, notas de las fichas ficticias/Gordon/signos, favoritos, historial ni preferencias.

El **Cuaderno de práctica** tiene otro respaldo: `tipo_respaldo: cuaderno_practica`, esquema 1, hasta 200 planes/apuntes y 2 MB. Se importa desde Cuaderno, no desde Catálogo. Los JSON no contienen archivos multimedia: conservar originales.

Los respaldos del catálogo mantienen autoría por ficha, pero importarlos no recupera el control de versiones de los paquetes originales. El deshacer interno sí restaura el estado anterior y su control de versiones. Los dos espacios (catálogo y cuaderno) tienen puntos de deshacer separados; no son una copia completa de la app.

## Ejemplos y mantenimiento

- `ejemplo_importacion.json`: ejemplo de esquema 1, sin información clínica; mantiene compatibilidad.
- `ejemplo_protocolo_v2.json`: ejemplo de esquema 2 sobre el flujo editorial del software, no un procedimiento asistencial.
- `plantilla_paracetamol.json`: estructura en borrador; no una monografía farmacológica.
- `create_schema.py`: regenera documentación de los esquemas 1/2.
- `create_templates.py`: **mantenimiento destructivo de ejemplos/plantillas/seed**; no ejecutar sobre contenido editorial que debas conservar. No se necesita para compilar ni para convertir un XLSX existente. Mantiene el seed básico en esquema 1 y genera plantillas nuevas de esquema 2.

La app funciona offline. Integraciones externas, extracción documental, publicación automática e IA siguen fuera de esta edición.
