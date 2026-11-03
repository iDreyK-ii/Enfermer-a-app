# Auditoría del MVP — Base Maestra 2.1

Fecha: 2026-11-03. Base de código inspeccionada: `cb05f1f0b27d6f1d50ae651df89bd0fab2c95867`.
Respaldo previo: rama `respaldo/base-maestra-v21-cb05f1f`. Sin refactorización previa a la ejecución de las pruebas.

## Evidencia anterior a los cambios

`bash scripts/comprobar.sh --todo`: **163 pruebas Android, 11 suites, sin fallos, errores ni omisiones; lint 0 errores y 24 advertencias; ensamblado e inspección del APK correctos**. 100 pruebas Python, 5 comprobaciones SQLite y 17 PostgreSQL/RLS también ejecutadas. JDK 21, SDK 35, ejecución secuencial. Resultado estructurado: `qa/base_maestra_v21/antes.json`. No se infiere prueba física ni validación clínica.

Los 13 apartados y ambos anexos de la base se leyeron completos. `research/base_maestra_v21/BASE_EDITORIAL_NO_CLINICA.md` conserva el original como entrada editorial, **no como guía clínica publicada ni como asset del APK**. Sus referencias provisionales, ejemplos de medicación y calificación 20/20 no se convierten en aprobación.

## Estado inicial real

HECHO significa función existente dentro de su alcance educativo; no certificación ni exhaustividad.

| Módulo / requisito | Estado inicial | Hallazgo en código y actuación |
|---|---|---|
| Android nativo, Prototipo B, navegación y temas | HECHO | `MainActivity`, `Design`, recursos seleccionados. Conservar. |
| Animación / modo ligero / movimiento reducido | HECHO | `PerformancePolicy`, preferencias persistentes. Reutilizar en todo vector nuevo. |
| Inicio con actividad, favoritos e historial | PARCIAL | Actividad real existente; falta progreso de nuevos contenidos y estado de módulos. |
| Búsqueda global offline | PARCIAL | Busca nombres de módulos, fármacos y 44 fichas; no todo el texto, atlas o casos progresivos. |
| Biblioteca local importable / fichas editables | HECHO | SQLite, importación revisada, deshacer, fuentes, borradores y límites. No sustituir. |
| Biblioteca Markdown con filtros | FALTA | Añadir lector estructurado, metadatos y búsqueda integral. |
| Dosis-volumen y C1V1=C2V2 | PARCIAL | Unidades y bloqueos presentes; Double y falta detalle decimal uniforme. |
| Conversión, goteo e IMC | PARCIAL | Factor de goteo preseleccionado; requiere entrada explícita. |
| Dosis/kg, mL/h y duración | FALTA | Añadir aritmética dimensional sin proponer dosis. |
| Balance y signos ficticios | HECHO | Funciones locales existentes; reforzar validación decimal del balance. |
| GCS adulto, EVA/NRS, Apgar educativo | HECHO | Reutilizar componentes, población, datos incompletos y pruebas. |
| Braden completo | BLOQUEADO-POR-VALIDACIÓN | Solo registro externo; permisos no aportados. No sustituir por formulario inventado. |
| Norton / RASS administrados en app | BLOQUEADO-POR-VALIDACIÓN | RASS tiene ficha documental; Norton aún no tiene entrada propia. Falta cotejo de versión/derechos de uso digital. |
| 4AT, Capurro, Silverman, GCS pediátrico | HECHO | Cálculos o registros de alcance explícito. Preservar aunque el roadmap los denomine futuros. |
| Atlas vectorial táctil de 8 dispositivos | FALTA | Iconos no equivalen a atlas. Añadir partes, selección, zoom y advertencias. |
| Higiene de manos / animación / pausa | FALTA | Añadir temporizador didáctico y modo estático accesible. |
| Bioseguridad / cinco momentos / EPP | PARCIAL | Lista local básica; completar contenido educativo con trazabilidad. |
| Diez correctos INSN-SB | FALTA | Cotejo realizado con GP-028/INSN-SB/UE-V.01, abril 2021, página 6; alcance en página 3. |
| Veinte controles propios | PARCIAL | Infraestructura configurable; separar autoría y ausencia de carácter oficial. |
| PAE, Gordon y cuaderno persistente | PARCIAL | Plan de 7 campos y notas; falta ABCDE y separación subjetivo/objetivo/fuente. |
| SOAPIE / SBAR / exportación educativa | FALTA | Extender cuaderno compatible, sin catálogos licenciados ni pacientes reales. |
| Casos sintéticos progresivos (7) | FALTA | Casos locales editables sí; no existen siete recorridos progresivos. |
| Inventario farmacológico nominal | PARCIAL | Seis borradores del PDF; no están importados los nombres del anexo B. |
| Monografías / validación DIGEMID producto a producto | BLOQUEADO-POR-VALIDACIÓN | No convertir referencias editoriales en validaciones ni publicar pautas. |
| Cuentas / catálogo remoto / OTP | PARCIAL | Cliente y servidor previos conservados; OTP real aplazado, APK QA sin nube. |
| Actualizaciones de contenido | HECHO | Paquetes revisados, importación local y cliente remoto existentes; no afirmar publicación real nueva. |
| Compatibilidad móvil/tablet física | FALTA | Pruebas de framework no sustituyen dispositivos. Se documentará evidencia real disponible. |

## Preservación y apariencia

No se cambia de framework, tema ni identidad. Inicio 2 / ficha 1 continúan como base; se agregan accesos a biblioteca, atlas, bioseguridad y progreso en los módulos existentes. Las fichas nuevas utilizan `Page`, `AppCard`, `ActionRow`, `Badge` y la paleta vigente. No se promete paridad de píxeles con una imagen.

## Cierre

La matriz final, las funciones añadidas y las limitaciones se documentarán en `docs/MVP_BASE_MAESTRA_v21.md`, con pruebas del código final, no con los resultados anteriores.
