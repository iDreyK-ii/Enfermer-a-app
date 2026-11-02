-- iDreyK 360: esquema inicial PostgreSQL/Supabase. No SQLite del teléfono.
-- Migración aplicada por el gestor transaccional, sin BEGIN/COMMIT internos.
-- No incluye usuarios, contraseñas, fichas clínicas ni publicaciones de ejemplo.

CREATE SCHEMA IF NOT EXISTS idreyk_private;
REVOKE ALL ON SCHEMA idreyk_private FROM PUBLIC, anon, authenticated;

CREATE TABLE public.profiles (
    user_id uuid PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    display_name text NOT NULL DEFAULT '' CHECK (char_length(display_name) <= 100),
    updated_at timestamptz NOT NULL DEFAULT now()
);
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
REVOKE ALL ON public.profiles FROM PUBLIC, anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.profiles TO authenticated;
GRANT ALL ON public.profiles TO service_role;
CREATE POLICY profile_read_self ON public.profiles FOR SELECT TO authenticated USING (user_id = (SELECT auth.uid()));
CREATE POLICY profile_insert_self ON public.profiles FOR INSERT TO authenticated WITH CHECK (user_id = (SELECT auth.uid()));
CREATE POLICY profile_update_self ON public.profiles FOR UPDATE TO authenticated USING (user_id = (SELECT auth.uid())) WITH CHECK (user_id = (SELECT auth.uid()));
CREATE POLICY profile_delete_self ON public.profiles FOR DELETE TO authenticated USING (user_id = (SELECT auth.uid()));

CREATE TABLE idreyk_private.editors (
    user_id uuid PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    active boolean NOT NULL DEFAULT true
);
ALTER TABLE idreyk_private.editors ENABLE ROW LEVEL SECURITY;
REVOKE ALL ON idreyk_private.editors FROM PUBLIC, anon, authenticated;
GRANT ALL ON idreyk_private.editors TO service_role;

CREATE FUNCTION idreyk_private.is_editor() RETURNS boolean
LANGUAGE sql STABLE SECURITY DEFINER SET search_path = pg_catalog AS $$
    SELECT EXISTS (SELECT 1 FROM idreyk_private.editors WHERE user_id = (SELECT auth.uid()) AND active);
$$;
REVOKE ALL ON FUNCTION idreyk_private.is_editor() FROM PUBLIC, anon, authenticated;
GRANT USAGE ON SCHEMA idreyk_private TO authenticated, service_role;
GRANT EXECUTE ON FUNCTION idreyk_private.is_editor() TO authenticated;

CREATE TABLE public.catalog_drafts (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    slug text NOT NULL CHECK (slug ~ '^[a-z0-9][a-z0-9_-]{0,99}$'),
    kind text NOT NULL CHECK (kind IN ('med','case','safety','protocol')),
    payload jsonb NOT NULL CHECK (jsonb_typeof(payload) = 'object'),
    state text NOT NULL DEFAULT 'borrador' CHECK (state IN ('borrador','en_revision')),
    author_id uuid REFERENCES auth.users(id) ON DELETE SET NULL,
    updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE(kind, slug)
);
ALTER TABLE public.catalog_drafts ENABLE ROW LEVEL SECURITY;
REVOKE ALL ON public.catalog_drafts FROM PUBLIC, anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.catalog_drafts TO authenticated;
GRANT ALL ON public.catalog_drafts TO service_role;
CREATE POLICY editor_drafts ON public.catalog_drafts FOR ALL TO authenticated
USING ((SELECT idreyk_private.is_editor())) WITH CHECK ((SELECT idreyk_private.is_editor()));

CREATE TABLE public.catalog_releases (
    version bigint PRIMARY KEY CHECK (version BETWEEN 1 AND 9999999),
    min_app_version integer NOT NULL DEFAULT 4 CHECK (min_app_version >= 4),
    package_json text NOT NULL CHECK (octet_length(package_json) BETWEEN 2 AND 2000000),
    sha256 text NOT NULL CHECK (sha256 ~ '^[a-f0-9]{64}$'),
    published boolean NOT NULL DEFAULT false,
    published_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    CHECK (sha256 = encode(pg_catalog.sha256(convert_to(package_json, 'UTF8')), 'hex')),
    CHECK (jsonb_typeof(package_json::jsonb) = 'object'),
    CHECK (COALESCE(package_json::jsonb ->> 'id_paquete', '') = 'catalogo-oficial'),
    CHECK (COALESCE((package_json::jsonb ->> 'version_contenido')::bigint, 0) = version),
    CHECK (COALESCE(package_json::jsonb ->> 'version_esquema', '') = '3')
);
ALTER TABLE public.catalog_releases ENABLE ROW LEVEL SECURITY;
REVOKE ALL ON public.catalog_releases FROM PUBLIC, anon, authenticated;
GRANT SELECT ON public.catalog_releases TO anon, authenticated;
GRANT INSERT, UPDATE ON public.catalog_releases TO authenticated;
GRANT ALL ON public.catalog_releases TO service_role;
CREATE POLICY reader_published_releases ON public.catalog_releases FOR SELECT TO anon, authenticated USING (published);
CREATE POLICY editor_read_releases ON public.catalog_releases FOR SELECT TO authenticated USING ((SELECT idreyk_private.is_editor()));
CREATE POLICY editor_insert_releases ON public.catalog_releases FOR INSERT TO authenticated WITH CHECK ((SELECT idreyk_private.is_editor()));
CREATE POLICY editor_publish_releases ON public.catalog_releases FOR UPDATE TO authenticated
USING ((SELECT idreyk_private.is_editor())) WITH CHECK ((SELECT idreyk_private.is_editor()));

CREATE FUNCTION idreyk_private.guard_release() RETURNS trigger
LANGUAGE plpgsql SET search_path = pg_catalog AS $$
DECLARE
    document jsonb := NEW.package_json::jsonb;
    section text;
    entry jsonb;
    amount integer := 0;
BEGIN
    IF TG_OP = 'UPDATE' AND (NEW.version IS DISTINCT FROM OLD.version OR NEW.package_json IS DISTINCT FROM OLD.package_json OR NEW.sha256 IS DISTINCT FROM OLD.sha256 OR NEW.min_app_version IS DISTINCT FROM OLD.min_app_version) THEN
        RAISE EXCEPTION 'Una revisión es inmutable: publica una versión nueva';
    END IF;
    IF COALESCE(document ->> 'tipo_paquete', 'contenido') <> 'contenido' THEN
        RAISE EXCEPTION 'El canal no admite respaldos privados';
    END IF;
    IF NEW.published THEN
        IF length(btrim(COALESCE(document ->> 'licencia', ''))) = 0 THEN
            RAISE EXCEPTION 'Documenta licencia y procedencia antes de publicar';
        END IF;
        FOREACH section IN ARRAY ARRAY['medicamentos','casos','listas_seguridad','protocolos'] LOOP
            IF document ? section THEN
                IF jsonb_typeof(document -> section) <> 'array' THEN RAISE EXCEPTION 'Colección inválida'; END IF;
                IF jsonb_array_length(document -> section) > 300 THEN RAISE EXCEPTION 'Máximo 300 registros por tipo'; END IF;
                FOR entry IN SELECT * FROM jsonb_array_elements(document -> section) LOOP
                    amount := amount + 1;
                    IF COALESCE(entry ->> 'revision', '') <> 'declarada_por_autor'
                        OR length(btrim(COALESCE(entry ->> 'revisor', ''))) = 0
                        OR COALESCE(entry ->> 'fecha_revision', '') !~ '^[0-9]{4}-[0-9]{2}-[0-9]{2}$'
                        OR jsonb_typeof(entry -> 'fuentes') IS DISTINCT FROM 'array' THEN
                        RAISE EXCEPTION 'Cada ficha publicada requiere revisión declarada y referencias';
                    END IF;
                    IF jsonb_array_length(entry -> 'fuentes') = 0 THEN RAISE EXCEPTION 'Faltan fuentes'; END IF;
                END LOOP;
            END IF;
        END LOOP;
        IF amount NOT BETWEEN 1 AND 500 THEN RAISE EXCEPTION 'La publicación debe contener de 1 a 500 registros'; END IF;
        IF NEW.published_at IS NULL THEN NEW.published_at := now(); END IF;
    END IF;
    RETURN NEW;
END;
$$;
REVOKE ALL ON FUNCTION idreyk_private.guard_release() FROM PUBLIC, anon, authenticated;
CREATE TRIGGER guard_catalog_release BEFORE INSERT OR UPDATE ON public.catalog_releases
FOR EACH ROW EXECUTE FUNCTION idreyk_private.guard_release();

INSERT INTO storage.buckets(id, name, public, file_size_limit, allowed_mime_types)
VALUES ('editorial-fuentes', 'editorial-fuentes', false, 10485760, ARRAY['application/pdf','application/json','image/png','image/jpeg']);
CREATE POLICY editorial_source_files ON storage.objects FOR ALL TO authenticated
USING (bucket_id = 'editorial-fuentes' AND (SELECT idreyk_private.is_editor()))
WITH CHECK (bucket_id = 'editorial-fuentes' AND (SELECT idreyk_private.is_editor()));
