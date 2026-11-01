# Lista de aceptación en un teléfono Android

Esta lista está **pendiente de ejecutar en un dispositivo real**. No se deben marcar pasos como aprobados por haber compilado el APK o pasado pruebas JVM.

Registra modelo, versión Android, tamaño de pantalla, idioma, tamaño de fuente del sistema y versión del APK. Utiliza solo datos ficticios.

## Instalación y navegación

- [ ] Instalar el APK sin una versión anterior.
- [ ] Abrirlo en modo avión y aceptar el aviso inicial.
- [ ] Verificar ilustración, textos, tarjetas, barra inferior y zonas seguras de pantalla.
- [ ] Recorrer Inicio, Módulos, Pacientes, Favoritos y Perfil.
- [ ] Probar botón/gesto Atrás, rotación, segundo plano y cierre forzado.
- [ ] Revisar pantallas con fuente de Android ampliada y orientación horizontal.
- [ ] Comprobar TalkBack, orden de foco, contraste y controles táctiles.

## Farmacología y biblioteca

- [ ] Buscar Paracetamol; abrir sus cuatro pestañas. Deben verse campos vacíos con estado de borrador, no pautas clínicas inventadas.
- [ ] Guardarlo como favorito y comprobarlo después de cerrar y abrir la app.
- [ ] Importar `ejemplo_importacion.json` desde Descargas. Revisar el resumen y cancelar primero; no debe cambiar la ficha.
- [ ] Repetir y confirmar. Debe aparecer el texto «IMPORTACIÓN DE PRUEBA CORRECTA» en Descripción general.
- [ ] Deshacer; comprobar que se recupera el contenido anterior.
- [ ] Intentar importar JSON inválido y una versión ya importada. Deben rechazarse sin alterar el catálogo ni su punto de deshacer.
- [ ] Exportar el catálogo mediante el selector Android y abrir el JSON fuera de la app.
- [ ] Probar cancelación del selector, almacenamiento sin espacio, archivo eliminado y proveedor sin permiso.

## Práctica y persistencia

- [ ] Guardar una nota ficticia distinta en Mario y Rosa; comprobar que no se mezclan.
- [ ] Guardar signos, balance y un plan ficticios. Verificar que el gráfico se identifica como decorativo.
- [ ] Cerrar/abrir y comprobar notas, favoritos, historial y ajustes.
- [ ] Cambiar modo oscuro, reducir animaciones y ampliar texto; reiniciar y comprobarlos.
- [ ] Probar cuestionario, tarjetas y avance de tarjetas conservado.

## Escalas y herramientas

- [ ] Glasgow sin selección: sin total.
- [ ] Glasgow E4/V5/M6: total de práctica 15; E1/V1/M1: 3.
- [ ] Cambiar cualquier componente a NV: sin total numérico.
- [ ] EVA: no mostrar resultado hasta mover el control; mantener visible que no es una regla calibrada.
- [ ] Capurro, dosis y dilución: cálculo deshabilitado y motivo visible.
- [ ] Aritmética de IMC, goteo y conversión: entradas inválidas con mensaje; cambiar entradas debe limpiar un resultado previo.
- [ ] ECG: pausar/reanudar; sin lectura real ni clasificación de ritmos.

## Cierre de la beta

- [ ] Revisar errores del sistema y cierres inesperados en cada teléfono.
- [ ] Confirmar que no hay solicitudes de cuenta, red, cámara, micrófono o localización.
- [ ] No asumir seguridad clínica por completar esta lista técnica.
- [ ] Obtener revisión profesional y de privacidad antes de utilizar contenido o datos de pacientes reales.
