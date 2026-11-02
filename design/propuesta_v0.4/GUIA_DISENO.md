# Diseño elegido y adaptación nativa — v0.4

Se conservó **inicio opción 2** (`01_inicio.png`) y **ficha opción 1** (`02_ficha_farmacologica.png`). El segundo original tenía errores de texto/encuadre; `02_ficha_legible.png` es su corrección tipográfica, no otra alternativa. `preparar_lamina.py` permite regenerarla sin modificar el original. Las imágenes son maquetas creadas con IA y corregidas, **no capturas del APK**.

## Inicio: jerarquía pensada para el uso frecuente

1. Identidad iDreyK 360, saludo y tarjeta de bienvenida del Prototipo B.
2. Búsqueda visible: módulos, medicamentos y recursos; también nombres comerciales/sinónimos cuando estén documentados.
3. Farmacología como tarjeta prioritaria, sin ocultar el resto del aplicativo.
4. Seis accesos: PAE, escalas, cálculos, apuntes, protocolos y multimedia; «Ver todo» conserva los demás módulos.
5. Actualizar biblioteca lleva a la pantalla de cuenta/novedades; sin proyecto muestra un estado explicativo, no un éxito falso.
6. Barra inferior: módulos, fármacos, inicio, favoritos y perfil. Botón central con acento rosa; la cuenta también se alcanza desde perfil.

## Ficha: primero el propósito de enfermería

- Identidad y estado de revisión al comienzo. El medicamento de la maqueta es solo demostración, sin pauta clínica.
- **Lo esencial:** objetivo; antes de administrar; preparación/administración; vigilancia y alertas.
- **Ficha técnica:** los 17 conceptos del PDF con numeración consistente.
- **Otros:** campos previos no cubiertos por esos conceptos. No se borraron las secciones antiguas.
- **Fuentes:** procedencia, licencia y revisión declarada; editar vuelve la ficha a borrador.
- Favorito y edición conservan la gestión local. «Pendiente» no significa seguro, inexistente o no aplicable.

## Identidad y rendimiento

Paleta base: rosa `#D64792`, lavanda `#A271CD`, turquesa `#23AEB3`, fondo claro `#FAF6FF`, tinta `#30253E`. Tipografías locales Outfit (títulos) y Nunito Sans (texto), ya incluidas en el proyecto. Se mantienen modo oscuro y texto ampliado.

Los componentes nativos adaptan tamaños, iconos y separación; **no se promete igualdad píxel a píxel con una imagen generada**. Las pruebas de Compose verifican comportamiento, no equivalencia visual de todos los dispositivos.

- Automático: animación decorativa según RAM/señales del sistema.
- Ligero: elimina movimiento decorativo y reduce sombras; mismas funciones.
- Completo: permite los efectos disponibles, pero sigue respetando accesibilidad, ahorro, memoria y segundo plano.

La reducción de animaciones del usuario o del sistema prevalece. El modo completo no es una garantía de FPS. Falta comprobar fluidez, texto grande, contraste y disposición con equipos físicos de varias gamas antes de cerrar diseño de producción.

## Exportación

`Diseno_y_arquitectura_v0.4.pdf` presenta las pantallas y explica estructura, datos, publicación, rendimiento y estado. `crear_guia_pdf.py` lo regenera con ReportLab y fuentes locales. Los originales elegidos, su corrección y el PDF de estructura aportado por el usuario se incluyen con el código, no como pantallas rasterizadas dentro del APK.

Para regenerar sin red durante el renderizado:

```sh
python3 -m pip install -r design/propuesta_v0.4/requirements.txt
python3 design/propuesta_v0.4/preparar_lamina.py
python3 design/propuesta_v0.4/crear_guia_pdf.py
```
