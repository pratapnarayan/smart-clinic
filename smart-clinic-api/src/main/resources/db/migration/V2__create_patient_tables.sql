-- Run inside each tenant schema via FlywayMigrationRunner.migrate(schema: "hospital_NNN")
-- The search_path will already be set to the tenant schema when this runs.

CREATE TABLE IF NOT EXISTS patients (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name       VARCHAR(100) NOT NULL,
    last_name        VARCHAR(100) NOT NULL,
    date_of_birth    DATE        NOT NULL,
    gender           VARCHAR(10) NOT NULL CHECK (gender IN ('MALE','FEMALE','OTHER')),
    mobile           VARCHAR(15),
    email            VARCHAR(150),
    address          TEXT,
    blood_group      VARCHAR(10),
    guardian_name    VARCHAR(100),
    guardian_mobile  VARCHAR(15),
    photo_url        VARCHAR(500),
    legacy_id        BIGINT,                   -- cross-reference during parallel migration run
    search_vector    TSVECTOR,
    deleted_at       TIMESTAMPTZ,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(36),
    updated_by       VARCHAR(36)
);

-- Full-text search index (fixes the ONLY_FULL_GROUP_BY 500 crashes from legacy Patient Search)
CREATE INDEX IF NOT EXISTS idx_patients_fts      ON patients USING GIN(search_vector);
CREATE INDEX IF NOT EXISTS idx_patients_mobile   ON patients(mobile) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_patients_legacy   ON patients(legacy_id) WHERE legacy_id IS NOT NULL;

-- Auto-update search_vector on insert/update
CREATE OR REPLACE FUNCTION patients_search_vector_update() RETURNS TRIGGER AS $$
BEGIN
    NEW.search_vector :=
        setweight(to_tsvector('english', COALESCE(NEW.first_name, '')), 'A') ||
        setweight(to_tsvector('english', COALESCE(NEW.last_name, '')),  'A') ||
        setweight(to_tsvector('english', COALESCE(NEW.mobile, '')),     'B') ||
        setweight(to_tsvector('english', COALESCE(NEW.email, '')),      'C');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_patients_search ON patients;
CREATE TRIGGER trg_patients_search
    BEFORE INSERT OR UPDATE ON patients
    FOR EACH ROW EXECUTE FUNCTION patients_search_vector_update();
