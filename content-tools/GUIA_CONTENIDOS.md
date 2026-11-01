# Tu contenido, de la plantilla a la app

## Qué funciona ahora

Puedes preparar un paquete JSON en tu computadora, guardarlo en **Descargas** del teléfono e importarlo desde **Módulos → Biblioteca local**. La app muestra el autor declarado, versión, fecha y cuántos registros se añadirán o reemplazarán. Nada se incorpora hasta que confirmes.

No interpreta el nombre de un archivo. Un documento llamado «Paracetamol» no se transforma por sí solo en una ficha. No consulta internet ni completa dosificaciones, diluciones o cuidados mediante IA.

## Ruta rápida para comprobar la importación

1. Copia `ejemplo_importacion.json` al teléfono.
2. Abre Biblioteca local → Importar paquete JSON.
3. Selecciona el archivo y revisa el resumen.
4. Confirma y busca Paracetamol en Farmacología.
5. Abre Descripción general: verás un texto de prueba, **no instrucciones clínicas**.
6. Vuelve a Biblioteca local → Deshacer última importación. Se recuperará la descripción anterior.

Para importar de nuevo exactamente el mismo paquete después de una importación ya confirmada, aumenta `version_contenido`. Deshacer también revierte ese registro de versión.

## Excel

Abre `Plantilla_contenidos_iDreyK_360.xlsx`. Sus hojas son:

- **Instrucciones**: uso y limitaciones.
- **Metadatos**: identificador y versión del paquete, título, autor, fecha y licencia.
- **Medicamentos**: una fila por ficha, con 20 secciones de texto.
- **Casos**: objetivo y contenido original o autorizado de cada caso ficticio.
- **Listas**: institución, edición y datos de cada lista de seguridad.
- **Items**: filas enlazadas mediante `lista_id`, orden, título y detalle.
- **Fuentes**: referencias enlazadas al tipo y al ID del registro.

No modifiques los encabezados. Usa texto literal, no fórmulas de Excel. Las fechas pueden escribirse como `AAAA-MM-DD`. Separa los sinónimos con punto y coma. No incluyas datos identificables de pacientes.

Instala Python y ejecuta en la carpeta de estas herramientas:

```sh
python -m pip install -r requirements.txt
python convertir_plantilla.py Plantilla_contenidos_iDreyK_360.xlsx mi_biblioteca.json
```

El conversor conserva el contenido de las celdas como texto, sin interpretar información clínica. Verifica relaciones, campos básicos, fórmulas no admitidas y límites de entrada. La validación nativa al importar es la comprobación estructural definitiva. Ni una conversión exitosa ni una importación aceptada certifican exactitud clínica, vigencia, autoría o permisos.

## JSON directo

Puedes editar `plantilla_paracetamol.json` sin usar Excel. El esquema documental está en `esquema_contenido_v1.schema.json`. Reglas principales:

- `version_esquema`: entero literal `1`.
- `id_paquete`: minúsculas, números, guion o guion bajo; máximo 100 caracteres.
- `version_contenido`: entero literal positivo de hasta siete dígitos. Sin comillas ni decimales.
- `titulo`, `autor` y `fecha_actualizacion`: obligatorios; fecha real ISO.
- `licencia`: describe la autorización o las condiciones de uso. No inventes permisos.
- Arrays `medicamentos`, `casos`, `listas_seguridad`: al menos uno debe contener registros.
- Cada registro lleva un ID estable. La clave de reemplazo es **tipo + ID**, no nombre de archivo.
- Los campos clínicos son cadenas de texto. El HTML o JavaScript se muestra como texto y no se ejecuta.
- Fuentes: título y URL HTTPS completa, sin credenciales; hasta 30 por registro.
- Una sección farmacológica admite hasta 16.000 caracteres; un caso, 60.000; un objetivo, 4.000; cada lista, 1–100 ítems.
- El archivo debe ser UTF-8 válido y tener como máximo 2.000.000 bytes.
- Tanto cada paquete como el **catálogo resultante** tienen un máximo de 500 registros totales y 300 por tipo. El catálogo exportado también debe caber en 2 MB.

Los campos de farmacología son: descripción, mecanismo de acción, presentaciones, vías, indicaciones, contraindicaciones, precauciones, dosificación, reconstitución, dilución, compatibilidad, administración, estabilidad, reacciones adversas, interacciones, monitorización, cuidados de enfermería, poblaciones especiales, educación y alertas.

### Estado de revisión

- `borrador`: estado inicial; contenido no validado.
- `declarada_por_autor`: requiere revisor, fecha válida, fuentes y licencia del paquete. La app lo identifica como una **declaración**, no como una aprobación independiente.

La actualización se controla por paquete. Una versión mayor actualiza las claves que incluya, sin borrar otras fichas. Dos paquetes diferentes pueden reemplazar una misma clave; revisa siempre el resumen antes de confirmar.

## Respaldo y deshacer

- **Exportar** crea un catálogo JSON compacto, legible y sin cifrar. Usa `tipo_paquete: respaldo_catalogo` y conserva la procedencia de cada ficha.
- Reimportar un respaldo mezcla sus fichas con la biblioteca, reemplazando claves coincidentes. Se registra como un paquete nuevo, sin reinstalar el historial de versiones de los paquetes originales.
- **Deshacer** revierte una sola importación confirmada y su control de versiones. No borra notas, favoritos ni ejercicios.
- El punto de deshacer se guarda en la misma transacción SQLite que la importación. Un rechazo no sustituye el punto anterior.
- La exportación no es una copia completa de la aplicación. No incluye notas, preferencias, favoritos ni historial de ejercicios.
- **Restablecer** sí elimina biblioteca importada, notas, favoritos y ejercicios, tras pedir confirmación.

## Flujo futuro, todavía no conectado

Una carpeta de documentos podría ser la entrada de un proceso editorial. La extracción de contenido de Word/PDF, el control de derechos, la revisión clínica, la aprobación y la distribución requieren un servicio y un flujo adicionales. No conviene publicar automáticamente instrucciones médicas extraídas o generadas sin revisión. El contrato JSON de esta edición permite preparar esa integración sin conectar cuentas ahora.

## Para mantener las herramientas

`create_schema.py` regenera el esquema documental. `create_templates.py` es un generador de mantenimiento: reemplaza las plantillas y la biblioteca inicial; no lo ejecutes sobre contenido editorial que quieras conservar. No es necesario para compilar la aplicación ni para convertir un XLSX existente. Los textos «COMPLETAR» son marcadores de la plantilla, no permisos ni revisiones válidas.
