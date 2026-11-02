# Historial de migraciones reales

La migración `20261102110405_inicial_catalogo_seguro_v1.sql` ya fue aplicada al proyecto `dmxfdvvcvfecqhbbmrlu` y consta en su historial de Supabase.

Es equivalente al esquema previamente probado de `backend/supabase/schema.sql`; no incluye transacción exterior porque la operación de migración la administra. El único cambio de bytes frente al SQL guardado en el servidor es un salto de línea final en el archivo local. Hay una prueba local que contrasta ambos esquemas ignorando comentarios y la transacción exterior.

**No repetir la migración ni el script inicial sobre ese proyecto.** Para cambios posteriores, crear otra migración, revisarla y aplicarla con autorización. No editar la inicial para fingir que se aplicaron cambios nuevos.

Este directorio conserva el historial para futuras herramientas. No se ha configurado Supabase CLI, un flujo de despliegue automático, branching de pago ni una integración GitHub→Supabase. No ejecutar un `db push` a un proyecto distinto sin inspeccionar el destino y su historial.

Estado, permisos y pasos de correo: `../docs/SUPABASE_REAL_Y_GITHUB.md`.
