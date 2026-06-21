-- Radiology module schema
-- imaging_modalities → imaging_studies (catalog)
-- radiology_orders   → radiology_order_items (per-study report rows)

-- ── Imaging Modalities ────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS imaging_modalities (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    code        VARCHAR(20)  NOT NULL UNIQUE,
    description VARCHAR(300),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(36)
);

-- ── Imaging Studies (Catalog) ─────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS imaging_studies (
    id                 UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    code               VARCHAR(30)   NOT NULL UNIQUE,
    name               VARCHAR(200)  NOT NULL,
    modality_id        UUID          NOT NULL REFERENCES imaging_modalities(id),
    description        VARCHAR(500),
    price              NUMERIC(10,2) NOT NULL DEFAULT 0,
    prep_instructions  TEXT,
    active             BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by         VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_studies_modality ON imaging_studies(modality_id);

-- ── Radiology Orders ──────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS radiology_orders (
    id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number     VARCHAR(30)   NOT NULL UNIQUE,
    patient_id       UUID          NOT NULL,
    patient_name     VARCHAR(200)  NOT NULL,
    patient_mobile   VARCHAR(15),
    referred_by_id   UUID,
    referred_by_name VARCHAR(200),
    source_type      VARCHAR(20)   NOT NULL DEFAULT 'WALK_IN'
                     CHECK (source_type IN ('OPD','IPD','WALK_IN')),
    source_id        UUID,
    priority         VARCHAR(10)   NOT NULL DEFAULT 'ROUTINE'
                     CHECK (priority IN ('ROUTINE','URGENT','STAT')),
    status           VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
                     CHECK (status IN ('PENDING','SCHEDULED','IN_PROGRESS','COMPLETED','CANCELLED')),
    scheduled_at     TIMESTAMPTZ,
    clinical_history TEXT,
    total_amount     NUMERIC(10,2) NOT NULL DEFAULT 0,
    discount         NUMERIC(10,2) NOT NULL DEFAULT 0,
    net_amount       NUMERIC(10,2) NOT NULL DEFAULT 0,
    payment_status   VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
                     CHECK (payment_status IN ('PENDING','PAID','PARTIAL','WAIVED')),
    notes            TEXT,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(36),
    updated_by       VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_rad_orders_patient_id ON radiology_orders(patient_id);
CREATE INDEX IF NOT EXISTS idx_rad_orders_status     ON radiology_orders(status) WHERE status NOT IN ('COMPLETED','CANCELLED');
CREATE INDEX IF NOT EXISTS idx_rad_orders_scheduled  ON radiology_orders(scheduled_at) WHERE scheduled_at IS NOT NULL;

-- ── Radiology Order Items ─────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS radiology_order_items (
    id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id          UUID         NOT NULL REFERENCES radiology_orders(id) ON DELETE CASCADE,
    study_id          UUID         NOT NULL,
    study_code        VARCHAR(30)  NOT NULL,
    study_name        VARCHAR(200) NOT NULL,
    modality_name     VARCHAR(100) NOT NULL,
    price             NUMERIC(10,2) NOT NULL DEFAULT 0,
    prep_instructions TEXT,
    status            VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                      CHECK (status IN ('PENDING','IN_PROGRESS','REPORTED')),
    findings          TEXT,
    impression        TEXT,
    reported_at       TIMESTAMPTZ,
    reported_by       VARCHAR(200),
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_rad_items_order_id ON radiology_order_items(order_id);
