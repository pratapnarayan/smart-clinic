-- Users table lives inside each tenant schema (hospital_001, hospital_002, …)
-- Each hospital has its own isolated user accounts — no cross-tenant logins.

CREATE TABLE IF NOT EXISTS users (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(150) NOT NULL,
    password_hash VARCHAR(60)  NOT NULL,          -- BCrypt hash, always 60 chars
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    tenant_id     VARCHAR(63)  NOT NULL,           -- mirrors the schema name, embedded in JWT
    role          VARCHAR(20)  NOT NULL
                  CHECK (role IN ('SUPER_ADMIN','ADMIN','DOCTOR','NURSE',
                                  'PHARMACIST','RECEPTIONIST','ACCOUNTANT',
                                  'PATHOLOGIST','PATIENT')),
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36),
    updated_by    VARCHAR(36),

    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE INDEX IF NOT EXISTS idx_users_email  ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(active) WHERE active = TRUE;
