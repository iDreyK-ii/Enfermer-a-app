import { PGlite } from '@electric-sql/pglite';
import fs from 'node:fs/promises';
import assert from 'node:assert/strict';
import crypto from 'node:crypto';

// Real PostgreSQL engine in WebAssembly; mocks only the pre-existing Auth/Storage schemas.
// This is NOT a live Supabase, SMTP or Storage-object integration test.
const db = new PGlite();
let checks = 0;
const alice = '720fe413-8773-4b5a-9607-b812944f0f00';
const bob = 'c3d434af-d0df-4dc4-86dd-3b2fc0796b3e';
const ok = (condition, message) => { assert.ok(condition, message); checks++; console.log('OK', message); };
async function role(name, user = '') {
  assert.ok(['postgres', 'anon', 'authenticated', 'service_role'].includes(name));
  await db.exec('RESET ROLE');
  await db.query("SELECT set_config('request.jwt.claim.sub', $1, false)", [user]);
  if (name !== 'postgres') await db.exec(`SET ROLE ${name}`); // Fixed whitelist, never external input.
}
async function rejected(operation, message, expectedCode = '42501') {
  let failed = false;
  try { await operation(); } catch (error) {
    assert.equal(error.code, expectedCode, `${message}: SQLSTATE inesperado`);
    failed = true;
  }
  ok(failed, message);
}
try {
  await db.exec(`
    CREATE ROLE anon; CREATE ROLE authenticated; CREATE ROLE service_role BYPASSRLS;
    CREATE SCHEMA auth;
    CREATE TABLE auth.users(id uuid PRIMARY KEY);
    CREATE FUNCTION auth.uid() RETURNS uuid LANGUAGE sql STABLE AS $$
      SELECT nullif(current_setting('request.jwt.claim.sub', true), '')::uuid;
    $$;
    GRANT USAGE ON SCHEMA auth TO anon, authenticated, service_role;
    GRANT EXECUTE ON FUNCTION auth.uid() TO anon, authenticated, service_role;
    CREATE SCHEMA storage;
    CREATE TABLE storage.buckets(id text PRIMARY KEY, name text, public boolean, file_size_limit bigint, allowed_mime_types text[]);
    CREATE TABLE storage.objects(id uuid PRIMARY KEY DEFAULT gen_random_uuid(), bucket_id text, name text);
    ALTER TABLE storage.objects ENABLE ROW LEVEL SECURITY;
    GRANT USAGE ON SCHEMA storage TO anon, authenticated, service_role;
    GRANT SELECT, INSERT, UPDATE, DELETE ON storage.objects TO anon, authenticated, service_role;
  `);
  await db.query('INSERT INTO auth.users(id) VALUES ($1),($2)', [alice, bob]);
  await db.exec(await fs.readFile(new URL('../supabase/schema.sql', import.meta.url), 'utf8'));
  ok(true, 'Esquema PostgreSQL completo aplicado sin errores');
  await db.query('INSERT INTO idreyk_private.editors(user_id) VALUES ($1)', [alice]);
  await role('authenticated', alice);
  await db.query('INSERT INTO public.profiles(user_id,display_name) VALUES ($1,$2)', [alice, 'Perfil ficticio QA']);
  await rejected(() => db.query('INSERT INTO public.profiles(user_id,display_name) VALUES ($1,$2)', [bob, 'Ajeno']), 'No se puede crear un perfil ajeno');
  await rejected(() => db.query('UPDATE public.profiles SET user_id=$1 WHERE user_id=$2', [bob, alice]), 'No se puede transferir el perfil a otro usuario');
  await db.query('INSERT INTO public.catalog_drafts(slug,kind,payload,author_id) VALUES ($1,$2,$3,$4)', ['ficha-qa', 'med', '{}', alice]);
  await db.query('INSERT INTO storage.objects(bucket_id,name) VALUES ($1,$2)', ['editorial-fuentes','fuente-qa.pdf']);
  ok((await db.query('SELECT * FROM catalog_drafts')).rows.length === 1, 'Editor puede trabajar con borradores privados');
  const packet = {
    version_esquema: 3, id_paquete: 'catalogo-oficial', version_contenido: 1,
    titulo: 'Fixture de QA, no clínica', autor: 'Prueba de software', fecha_actualizacion: '2026-11-01',
    licencia: 'Datos ficticios; no uso clínico',
    medicamentos: [{ id: 'ficha-qa', nombre: 'Solo ejemplo de formato', secciones: {},
      revision: 'declarada_por_autor', revisor: 'QA, no revisión clínica', fecha_revision: '2026-11-01',
      fuentes: [{titulo: 'Fuente ficticia', url: 'https://example.invalid/qa'}] }],
  };
  const payload = JSON.stringify(packet);
  const digest = crypto.createHash('sha256').update(payload).digest('hex');
  await db.query('INSERT INTO catalog_releases(version,package_json,sha256,published) VALUES ($1,$2,$3,$4)', [1,payload,digest,false]);
  await role('anon');
  ok((await db.query('SELECT * FROM catalog_releases')).rows.length === 0, 'Anónimo no puede leer una revisión aún no publicada');
  await rejected(() => db.query('SELECT * FROM profiles'), 'Anónimo no tiene acceso a perfiles');
  await rejected(() => db.query('INSERT INTO catalog_releases(version,package_json,sha256) VALUES ($1,$2,$3)', [2,payload,digest]), 'Anónimo no puede publicar');
  await role('authenticated', bob);
  ok((await db.query('SELECT * FROM profiles')).rows.length === 0, 'Usuario no ve perfiles ajenos');
  ok((await db.query('SELECT * FROM catalog_drafts')).rows.length === 0, 'Lector no ve borradores editoriales');
  ok((await db.query('SELECT * FROM storage.objects')).rows.length === 0, 'Lector no ve los archivos fuente privados');
  await rejected(() => db.query('INSERT INTO idreyk_private.editors(user_id) VALUES ($1)', [bob]), 'Usuario no puede asignarse permisos de editor');
  await rejected(() => db.query('INSERT INTO catalog_drafts(slug,kind,payload) VALUES ($1,$2,$3)', ['intruso','med','{}']), 'Cuenta lectora no puede cargar borradores');
  await role('authenticated', alice);
  await db.query('UPDATE catalog_releases SET published=true WHERE version=$1', [1]);
  await rejected(() => db.query('UPDATE catalog_releases SET min_app_version=5 WHERE version=$1', [1]), 'Revisión publicada no se reescribe', 'P0001');
  const draft = structuredClone(packet); draft.version_contenido = 2; draft.medicamentos[0].revision = 'borrador';
  const draftRaw = JSON.stringify(draft);
  await rejected(() => db.query('INSERT INTO catalog_releases(version,package_json,sha256,published) VALUES ($1,$2,$3,true)', [2,draftRaw,crypto.createHash('sha256').update(draftRaw).digest('hex')]), 'No se publican registros todavía en borrador', 'P0001');
  await rejected(() => db.query('INSERT INTO catalog_releases(version,package_json,sha256) VALUES ($1,$2,$3)', [2,draftRaw,'0'.repeat(64)]), 'Checksum inválido se rechaza en servidor', '23514');
  await role('anon');
  ok((await db.query('SELECT version,sha256 FROM catalog_releases')).rows.length === 1, 'Publicación revisada queda legible sin exigir cuenta');
  await role('postgres');
  ok((await db.query("SELECT public FROM storage.buckets WHERE id='editorial-fuentes'")).rows[0].public === false, 'El bucket de fuentes permanece privado');
  console.log(`\n${checks}/${checks} comprobaciones PostgreSQL/RLS superadas. Sin servicio Supabase real.`);
} finally { await db.close(); }
