# Verificación documental de arquitectura — 2026-11-01

## Alcance de esta revisión

Adaptación de una plantilla README/.env/SQL al proyecto Android nativo existente. Se documentan Kotlin/Compose, repositorio local y SQLite v2, sin introducir un backend web, MySQL, usuarios, JWT ni permisos de red. La etiqueta documental es `arquitectura-v0.3`; `desarrollo-v0.3` continúa identificando la ampliación del APK.

El README anterior se conserva **byte por byte** en `docs/ESTADO_FUNCIONAL_v0.3.md`. Los comandos de instalación de herramientas, compilación, pruebas e instalación Android están en `docs/COMPILAR_Y_PROBAR.md`. Las reglas de trabajo con IA se concentran en `AGENTS.md`.

## Comprobaciones ejecutadas en esta revisión

Entorno: Python 3.13.16, SQLite 3.46.1 (biblioteca de Python). El verificador requiere Python 3.10+ y solo usa biblioteca estándar.

```sh
python3 -m py_compile database/verificar_esquema.py
python3 database/verificar_esquema.py
```

**5 pruebas superadas, 0 fallos/errores.** Registro completo: `qa/esquema-pruebas.txt`.

1. Las ocho tablas de aplicación, columnas, tipos, nulabilidad, claves, índices, autoincrementos y DDL normalizado del SQL coinciden con las sentencias estáticas del `LocalDatabase` Kotlin. Coincide también la versión 2. Se excluyen tablas internas de SQLite.
2. No aparecen `usuarios`/`registros`, claves foráneas ni relaciones SQL inventadas.
3. El seed original se carga en una base temporal con conservación de campos y procedencia.
4. Un valor de prueba que contiene comillas y texto con apariencia de SQL permanece como dato literal mediante parámetros enlazados.
5. La creación del ejemplo se niega a sobrescribir un archivo existente y conserva sus bytes.

También se ejecutó la opción real `--crear-ejemplo` fuera del test: creó ocho registros del seed en `.cache/`. Una segunda ejecución con la misma ruta devolvió código 2 y dejó el archivo idéntico. La copia no se distribuye y no se instaló en ningún teléfono.

Controles adicionales:

- **114 archivos de fuentes, pruebas, recursos y configuración de Android sin cambios**, comparados por SHA-256 antes/después.
- README anterior igual al guardado en la etiqueta funcional `desarrollo-v0.3`.
- Sintaxis válida de `.env.example` y de cinco bloques de comandos shell documentados; validación de sintaxis, **no ejecución de SDK/Gradle/ADB**.
- `.gitignore` rechaza `.env` y variantes privadas, claves, bases de desarrollo y compilaciones; permite `.env.example`, SQL y fuentes del verificador.
- `git diff --check` sin errores de espacios.

## APK preservados

No se generó ni sustituyó un APK en esta revisión. SHA-256 sin cambios:

- v0.3: `7eb4854d1d6c927423a8ddaca0f1331522927d5b431187b45ca75703dd62bd99`.
- v0.2: `bb624e2059ed20874440ceefc97cb042adfa15fb840557976728a9c87f255076`.

La clave privada de firma no forma parte del repositorio ni del ZIP de código.

## Límites de esta comprobación

- El extractor de DDL es deliberadamente específico para la forma actual de `LocalDatabase`; no interpreta Kotlin ni ejecuta Android. Un cambio de formato o de tablas exige revisar el verificador explícitamente.
- La carga Python es una fixture de documentación, no una reimplementación completa de `ContentParser`, una migración Android ni un respaldo del teléfono. Usa `imported = 0` para mantener la prueba determinista.
- No se repitieron compilación, lint ni las 77 pruebas Android/JVM. Las herramientas SDK/Gradle de la caché anterior no están disponibles en el entorno restaurado. Su evidencia previa continúa en `qa/RESULTADOS.md`.
- Tampoco se presentan las 15 pruebas anteriores del conversor como ejecutadas otra vez. No cambiaron sus fuentes ni plantillas.
- No se implementaron cuentas, registro ni autenticación «lista para producción». Eso cambiaría el alcance offline/sin cuentas y requiere requisitos y diseño de seguridad propios.
- La revisión se realizó sobre los archivos locales. No hubo URL real de repositorio compartida, acceso a un repositorio GitHub ni subida remota.
- No se realizó instalación física, validación clínica ni incorporación de fuentes/licencias nuevas.
