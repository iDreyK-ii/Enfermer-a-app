# Plan seguro: GitHub → Google AI Studio

Esta guía prepara la transferencia de **la app Android existente**, no autoriza publicar por sí sola. El estado exacto del commit y de las pruebas debe comunicarse al propietario después de cada cambio. No compartir claves privadas, contraseñas ni capturas con datos de pacientes.

## 1. Antes de sincronizar

- Conservar una copia verificable de cualquier cambio realizado en Google AI Studio que todavía no esté en GitHub. No importar encima de trabajo no respaldado.
- Repositorio público autorizado anteriormente: <https://github.com/iDreyK-ii/Enfermer-a-app>, rama `desarrollo`. Al corte de esta preparación, el SHA remoto observado era `b463c580f6e965d971116ef593df0059cb105c86`; consultar nuevamente antes de actualizarlo. Si cambió, detenerse y revisar, sin forzar ni sobrescribir.
- Revisar `git status`, el SHA local completo, los cambios contra el remoto y las guardas/pruebas de la revisión exacta. El informe de `qa/pulido_final/RESULTADOS.md` no valida clínica ni derechos de terceros. La revisión de fuentes y permisos sigue pendiente.
- La autorización para alojar el repositorio públicamente no sustituye una **aprobación expresa de este envío**. Un commit local no publica nada. No subir APK, bases de pacientes, claves, `.env`, `local.properties`, cachés o dependencias generadas.

Comprobaciones de solo lectura desde el Git confiable:

```sh
git status --short
git rev-parse HEAD
git ls-remote https://github.com/iDreyK-ii/Enfermer-a-app.git refs/heads/desarrollo
# Si el SHA remoto está en el Git local, sustituir REMOTO por ese SHA exacto:
git merge-base --is-ancestor REMOTO HEAD
bash scripts/comprobar.sh --rapido
```

Si el SHA remoto no está en el historial local o no es ancestro, no continuar con el envío; pedir revisión. `REMOTO` es un marcador, no un SHA válido. La comprobación rápida no sustituye pruebas Android ni CI.

## 2. Solo tras aprobación expresa

1. Enviar **sin `--force`** el commit revisado a `desarrollo`, con una credencial autorizada para ese repositorio. No enviar claves como archivos o texto.
2. Consultar de nuevo el SHA remoto y comprobar que coincide con el SHA aprobado. Revisar el resultado del workflow del **mismo commit**: <https://github.com/iDreyK-ii/Enfermer-a-app/actions/workflows/calidad.yml>.
3. Comunicar el SHA y el estado real de CI. Una ejecución anterior no valida el commit nuevo. No ejecutar migraciones ni modificar Supabase como parte del envío.

## 3. Cargar en Google AI Studio sin regenerar

1. En la cuenta del propietario, abrir [Google AI Studio](https://aistudio.google.com/apps), entrar a **Build** y elegir **Android**. Conservar los archivos existentes y no solicitar una aplicación nueva a partir de una descripción.
2. Si la cuenta ofrece **Add files (+) → Import from GitHub** en el contexto Android, elegir el repositorio y la rama actualizados. Confirmar el **SHA aprobado**, no solo el nombre de rama. Si la opción no existe o faltan archivos, detenerse y comunicarlo; no sustituir la app por una web.
3. Antes de pedir cambios al agente, exportar o recuperar una copia de los archivos importados para compararla con el Git confiable. `INICIAR_EN_AI_STUDIO.md` y `scripts/verificar_traslado.py` describen la comparación por bytes y modos. Si el ZIP no registra permisos, la herramienta lo indica: no declarar que los permisos originales quedaron verificados.
4. Compilar sin configurar la nube por defecto. Si el importador cambia archivos o dependencias, registrar las diferencias y revisarlas en el repositorio; no aceptar cambios silenciosos ni asumir paridad visual o funcional.
5. Conservar SQLite y Supabase; no activar Firebase ni crear infraestructura sustitutiva sin otra autorización. El entorno Android descrito por Google no ejecuta el backend del repositorio como servidor.

La guía general de Google describe importar desde GitHub; la guía específica de Android afirma que la exportación de un proyecto Android es ZIP y no GitHub. Por tanto, **no se garantiza sincronización bidireccional para este caso**. Verificar las opciones reales de la cuenta: [guía Android](https://ai.google.dev/gemini-api/docs/aistudio-android) y [guía general](https://ai.google.dev/gemini-api/docs/aistudio-build-mode), consultadas el 3 de noviembre de 2026. Aquí no se ha importado ni compilado esta revisión en AI Studio.

## 4. Antes de instalar un APK externo

La firma de Google/CI puede diferir de la del APK local. Comprobar paquete, versionCode y certificado con `scripts/verificar_apk.py`; hacer respaldo de los datos. **No desinstalar ni borrar datos si la firma no coincide.** Compilar, instalar, publicar en Google Play y validar clínicamente son decisiones y comprobaciones distintas.
