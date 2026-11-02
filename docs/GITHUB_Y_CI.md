# Preparación para GitHub — v0.4.1

**Destino confirmado:** https://github.com/iDreyK-ii/Enfermer-a-app, rama `desarrollo`, público con autorización expresa del propietario. La clave SSH de despliegue y la prueba de escritura fueron comprobadas. Ver `GITHUB_DESTINO.md` para contrastar una subida real; no confundir un dry-run o YAML válido con un push/CI correctos. El conector GitHub de este entorno no está soportado; la operación se realiza mediante Git autorizado.

## Actualización del servidor

Ya existe el proyecto real documentado en `SUPABASE_REAL_Y_GITHUB.md`, con configuración publicable versionada. La calidad/CI sigue **aislada de ese servicio**: fuerza nube vacía y solo ejecuta el comprobador de red con transportes ficticios. `backend/comprobar_supabase.py --remoto` requiere una acción explícita separada. No hay despliegue automático de SQL ni workflow de firma privada.

El destino y el permiso de escritura ya fueron confirmados. Antes de nuevos cambios, revisar siempre las ramas y los hashes actuales del remoto; no sobrescribir trabajo ajeno.

## Qué se añadió

- `.github/workflows/calidad.yml`: pruebas en push a `desarrollo`/`main`, pull requests y ejecución manual.
- `scripts/comprobar.sh`: la misma secuencia para revisión local.
- `scripts/verificar_repositorio.py`: impide incluir determinados archivos privados/generados y detecta algunos patrones evidentes de credenciales. **No es un escáner exhaustivo de secretos ni del historial.**
- `scripts/verificar_apk.py`: inspecciona el APK ensamblado, versión, permisos, integridad y firmante; puede comparar un certificado esperado sin leer la clave privada.
- Acciones oficiales fijadas a commits, no a etiquetas móviles. Referencias verificadas en `CI_ACCIONES_FIJADAS.json`. Permisos `contents: read`, checkout sin credenciales persistentes y sin `pull_request_target`.

La CI usa JDK21, Python3.12, Node22 y SDK35. No cambia Kotlin/Compose/AGP ni necesita un servidor Supabase. Sus pruebas PostgreSQL corren con PGlite local, no contra un servicio real.

## Comprobar en tu equipo

Si partiste del ZIP sin `.git`, recupera primero `historial/desarrollo.bundle` en otra carpeta (ver la sección de recuperación en `docs/GITHUB_Y_CI.md`). La guarda revisa archivos versionados y exige el Git del propio proyecto, no uno de una carpeta superior.

Python 3.10+ y Node compatibles con las dependencias de prueba. Desde la raíz:

```sh
python3 -m pip install -r content-tools/requirements.txt
npm ci --prefix backend --ignore-scripts
bash scripts/comprobar.sh --rapido

# Con JDK/SDK preparados según docs/COMPILAR_Y_PROBAR.md:
bash scripts/comprobar.sh --todo
```

`--rapido` ejecuta guardas del repositorio, sus pruebas, conversor, publicación, espejo SQLite y PostgreSQL/RLS. `--todo` añade compilación Android, pruebas, lint, ensamblado e inspección real del APK. Los logs quedan en `.cache/qa-actual/`, fuera de Git. `PYTHON`, `GRADLE` y `QA_LOG_DIR` permiten elegir ejecutables/ruta de logs; no se interpretan comandos arbitrarios con `eval`.

**La comprobación completa fuerza las dos variables públicas de nube vacías.** Así no se envían correos ni se consultan cuentas reales desde las pruebas. El APK obtenido por este comando es de configuración local. Para un APK conectado, probar primero la base local y después compilar explícitamente con el proyecto público configurado; seguir `backend/README.md` y el plan de integración real.

El workflow instala las versiones de SDK que requiere AGP sobre el SDK del runner. Si las licencias o herramientas no están disponibles, debe resolverse por el responsable; no se salta la comprobación ni se da por aceptada una nueva licencia mediante este documento.

## Firma: una diferencia fundamental

El APK generado en un runner tendrá una **clave debug temporal distinta**. Es solo para pruebas en una instalación separada; **no actualiza necesariamente el APK entregado aquí**. Su artefacto se llama `debug-ci-firma-temporal-…` para evitar esa confusión.

Para las actualizaciones entregadas en este entorno se conserva el certificado:

```text
d6874c68417eefb1b1b902bf82ba4ce7d8734e47877e12bd1733eb498f0e522b
```

En una máquina autorizada que conserve esa firma, comprobarla con:

```sh
export IDREYK_EXPECTED_CERT_SHA256=d6874c68417eefb1b1b902bf82ba4ce7d8734e47877e12bd1733eb498f0e522b
python3 scripts/verificar_apk.py
```

No se subió ni se incluyó la clave privada. Una distribución release requiere plan de firma, recuperación, permisos y política de publicación separados. No introducirla en el workflow de pull requests. No desinstalar ni borrar datos para resolver un conflicto de firma.

## Sincronizar después, sin inventar un repositorio

1. Conservar una copia externa del ZIP y de los respaldos locales del teléfono.
2. Revisar qué material clínico/documental puede alojarse y con qué permisos. No se ha asignado una licencia general al proyecto ni se concede permiso para republicar fuentes de terceros.
3. Crear/elegir un repositorio propio, preferiblemente privado durante esta fase. Revisar costes/límites de Actions antes de activarlo.
4. Si se parte del ZIP sin `.git`, recuperar el historial en **otra carpeta**:

   ```sh
   git clone historial/desarrollo.bundle ../idreyk-restaurado
   cd ../idreyk-restaurado
   git switch desarrollo
   git status
   git remote -v
   ```

5. Un clon desde el bundle puede tener `origin` apuntando al archivo local. Conservarlo con otro nombre si corresponde; no sustituir un remoto existente sin revisarlo. Después añadir como `origin` la URL **real** de GitHub, sin tokens incrustados, usando SSH o un gestor de credenciales autorizado.
6. Subir la rama `desarrollo` y los tags deseados mediante Git, sin force push. No ejecutar este paso hasta que el usuario confirme el destino y el acceso.
7. Comprobar la primera ejecución real de Actions. Solo entonces configurar protección de rama, revisión de PR y el requisito del job `verificar`. Validar el YAML localmente no equivale a que el workflow ya haya corrido en GitHub.

No incluir `.env`, `local.properties`, claves, APK/AAB, bases privadas, cachés o `node_modules` en el repositorio. Los artefactos de pruebas tienen retención limitada; no son un respaldo permanente ni un despliegue de la app.

## Lo que esta CI no certifica

No prueba SMTP/Auth/Storage reales, hardware Keystore, instalación/actualización física, FPS o consumo en dispositivos, validación clínica, licencias o cumplimiento regulatorio. Tampoco publica contenidos ni elimina cuentas. Es una barrera de regresiones del código, no una autorización de uso asistencial.
