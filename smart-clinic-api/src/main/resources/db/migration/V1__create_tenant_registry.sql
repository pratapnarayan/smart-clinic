-- Shared public schema: tenant registry only
-- All hospital-specific data lives in per-tenant schemas (hospital_001, hospital_002, ...)

CREATE TABLE IF NOT EXISTS public.tenants (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(200) NOT NULL,
    schema_name VARCHAR(63)  NOT NULL UNIQUE,   -- PostgreSQL schema name limit
    plan        VARCHAR(50)  NOT NULL DEFAULT 'BASIC',
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.super_admin_users (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email        VARCHAR(150) NOT NULL UNIQUE,
    password     VARCHAR(60)  NOT NULL,   -- bcrypt hash
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_tenants_schema_name ON public.tenants(schema_name);
CREATE INDEX IF NOT EXISTS idx_tenants_status      ON public.tenants(status);
