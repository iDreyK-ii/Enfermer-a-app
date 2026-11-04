# Espacio de trabajo: medición y limpieza conservadora

El entorno de esta conversación conserva snapshots de **aproximadamente 128 MiB o 10.000 archivos**, sin garantía exacta. No equivale a la capacidad completa del disco temporal. `build`, `node_modules`, `.cache` y otras carpetas de herramientas están excluidas del snapshot: **no guardar allí un APK, fuente o evidencia que se quiera conservar.** El código, el historial Git y las entregas anteriores sí ocupan espacio persistible.

Medición orientativa, sin borrar nada:

```sh
python3 scripts/medir_espacio.py --root /home/user
# Simular una entrega adicional de 15 MiB antes de crearla:
python3 scripts/medir_espacio.py --root /home/user --reserva-mib 15
# Para un reporte legible por máquina:
python3 scripts/medir_espacio.py --root /home/user --json
```

El script suma tamaños lógicos y cuenta archivos; ignora las carpetas excluidas conocidas y configuraciones Git/credenciales excluidas. Puede diferir del límite real por metadatos, compresión, enlaces o cambios en el servicio. La alerta aparece al llegar al 90 % del límite orientativo o al haber errores de lectura. Revisar también el espacio físico con `df -h`, que mide otra cosa.

## Política

1. Nunca borrar datos personales, claves de firma, respaldos, APK/galerías anteriores ni archivos únicos aportados por el propietario para ganar espacio.
2. Limpiar solo cachés identificadas y regenerables cuando no se estén usando. La carpeta de ejecución `.gradle` del proyecto y `.android/cache` son cachés, no confundirlas con `.git`, `.android/debug.keystore` o el código.
3. Para duplicados, calcular SHA-256 de ambas copias antes de retirar una y dejar un registro que indique cuál se conservó y dónde. No borrar los dos extremos ni romper rutas históricas verificadas.
4. No realizar `git gc` o poda de objetos no referenciados como «limpieza» automática: podrían contener trabajo aún recuperable. No recrear la base SQLite.
5. Antes de generar otro APK o copia grande, medir, estimar su tamaño y asegurar margen. Si no hay espacio suficiente sin afectar archivos importantes, preguntar al propietario qué archivar fuera del espacio en lugar de borrar sin consentimiento.

Limpieza de esta fase: `LIMPIEZA_ESPACIO_iDreyK_2026-11-03.md` en la raíz del espacio. Las herramientas y paquetes eliminados de caché se pueden volver a descargar; los dibujos finales sí deben permanecer en assets versionados.
