# Lista de pruebas en teléfono — v0.3

**Todas las casillas siguen pendientes de ejecución física.** Las pruebas automatizadas de Robolectric no completan esta lista. Usar solo datos inventados y guardar capturas/modelo/versión Android cuando se realicen.

## Instalación y conservación

- [ ] Instalar v0.2 en un dispositivo de prueba; crear una nota ficticia, favorito, práctica y una ficha importada.
- [ ] Actualizar a v0.3 **sin desinstalar ni borrar datos**; verificar paquete/firma y conservación de todo lo anterior.
- [ ] Confirmar que el antiguo recorrido de cuatro tareas permanece visible como recorrido, no como plan asistencial.
- [ ] Cerrar/abrir y reiniciar teléfono; comprobar contenido, preferencias, cuaderno y medios.
- [ ] Probar instalación limpia en Android 8+ y en una versión Android reciente; documentar modelo, versión y espacio libre.
- [ ] No probar degradación a v0.2 sobre datos v2 como si existiera migración inversa.

## Navegación y presentación

- [ ] Tema claro/oscuro, texto ampliado, accesibilidad/TalkBack y fuentes del sistema grandes.
- [ ] Movimiento reducido y transiciones normales; pantallas pequeñas y tablet.
- [ ] Rotación, teclado, volver físico/gesto y confirmación de cambios no guardados en editores.
- [ ] Navegar inmediatamente después de guardar y guardar otro documento: el aviso anterior no debe interceptar el botón.
- [ ] Suspender/reanudar y recrear proceso con formularios largos; comprobar límites del estado guardado.

## Escalas — solo pruebas técnicas ficticias

- [ ] Glasgow: O/V/M; pendientes o NV sin total; registro externo pediátrico requiere fuente/versión.
- [ ] EVA: posición normalizada; comprobar que no se describe como una línea física calibrada.
- [ ] Capurro B: cinco selecciones de 0 producen matemáticamente 204 días = 29 semanas + 1 día. No usar esta prueba para inferir aplicabilidad clínica.
- [ ] Capurro A: seis campos, sin pezón; constante 200; transcripción externa. Cambiar variante limpia selecciones.
- [ ] Apgar y Silverman: todos 0 → 0/10, todos 2 → 10/10; faltar un componente impide guardar total.
- [ ] Cambiar momento de Apgar reinicia los cinco componentes.
- [ ] Braden/Morse: registro externo identificado; fuera de rango/no entero se rechaza, sin interpretación de riesgo.
- [ ] Historias de práctica muestran tipo, componentes o dato transcrito y fecha.
- [ ] Revisión clínica separada de criterios, fuentes, población, derechos y limitaciones con profesionales competentes.

## Cálculos de práctica

- [ ] Ejercicio sin fármaco: 250 mg respecto de 500 mg en 5 mL → 2,5 mL teóricos; mg/g/mcg explícitos.
- [ ] UI frente a masa, división por cero, negativos, infinitos y entradas inválidas: rechazo sin conservar resultado antiguo.
- [ ] Dilución ideal: C1=20 mg/mL, C2=5 mg/mL, V2=100 mL → V1=25 mL, adicional=75 mL. No reconstitución ni receta.
- [ ] C2>C1: rechazo; concentraciones iguales: volumen adicional cero; unidades incompatibles: rechazo.
- [ ] Editar cualquier valor/unidad elimina el resultado anterior y su botón de guardado.
- [ ] Valores pequeños: conversor/aritmética no muestran un cero por formato fijo cuando el resultado es positivo.

## Edición y respaldo

- [ ] Crear/editar/eliminar medicamento con 20 secciones y fuentes; volver a borrador y conservar procedencia.
- [ ] Crear lista de 20 ítems ficticios, reordenar, marcar, guardar y limpiar; número configurable, no estándar clínico.
- [ ] Caso y protocolo con texto literal, fuentes/fecha y revisión visible; búsquedas y favoritos.
- [ ] Plan PAE parcial/completo, estado documental, actualización y eliminación; no diagnósticos automáticos.
- [ ] Apuntes: título, etiquetas, texto, búsqueda y eliminación.
- [ ] Exportar catálogo por SAF; reimportar esquema 1 y 2; confirmar previamente cantidades nuevas/reemplazadas.
- [ ] Exportar/importar cuaderno por SAF y deshacer último cambio; no intercambiarlo con catálogo.
- [ ] Archivo incorrecto/UTF-8 dañado/duplicados/exceso de tamaño no alteran registros existentes ni deshacer anterior.
- [ ] Verificar que los respaldos no se anuncian como copias completas del dispositivo.

## Multimedia y almacenamiento

- [ ] Seleccionar PDF, JPG/JPEG/PNG/WebP/GIF, MP3/WAV/OGG y MP4/WebM reales mediante SAF.
- [ ] Cancelar selector sin modificar datos; documentos de proveedor local y, si procede, proveedor que requiera descarga por su cuenta.
- [ ] La copia permanece tras quitar acceso al original; no se guarda una dependencia frágil del URI original.
- [ ] Abrir cada formato con visor externo compatible y comprobar permiso temporal de solo lectura.
- [ ] Sin visor instalado: aviso manejado, sin cierre inesperado.
- [ ] Renombrar título; quitar copia y verificar que el original permanece intacto.
- [ ] Archivo vacío, >20 MiB, límite total 100 MiB o disco lleno: error claro, sin restos temporales ni falsa confirmación.
- [ ] Reiniciar después de interrumpir una copia; limpiar archivos temporales/huérfanos sin tocar registros válidos.
- [ ] Restablecer solo después de exportar lo necesario: borra cuaderno y copias multimedia también, según la confirmación.

## Condición para avanzar

No marcar la app como clínicamente lista por superar estas pruebas. El contenido farmacológico, permisos, revisión de instrumentos y seguridad para pacientes reales necesitan procesos independientes. Esta edición no debe contener registros clínicos reales.
