-- Pathology module schema
-- lab_test_categories → lab_tests (catalog)
-- lab_orders → lab_order_items (per-test result rows)

-- ── Lab Test Categories ───────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS lab_test_categories (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(300),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(36)
);

-- ── Lab Tests (Catalog) ───────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS lab_tests (
    id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    code             VARCHAR(30)   NOT NULL UNIQUE,
    name             VARCHAR(200)  NOT NULL,
    category_id      UUID          NOT NULL REFERENCES lab_test_categories(id),
    description      VARCHAR(500),
    price            NUMERIC(10,2) NOT NULL DEFAULT 0,
    turnaround_hours INT           NOT NULL DEFAULT 24,
    unit             VARCHAR(50),
    normal_range     VARCHAR(200),
    active           BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_lab_tests_category ON lab_tests(category_id);

-- ── Lab Orders ────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS lab_orders (
    id                   UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number         VARCHAR(30)   NOT NULL UNIQUE,
    patient_id           UUID          NOT NULL,
    patient_name         VARCHAR(200)  NOT NULL,
    patient_mobile       VARCHAR(15),
    referred_by_id       UUID,
    referred_by_name     VARCHAR(200),
    source_type          VARCHAR(20)   NOT NULL DEFAULT 'WALK_IN'
                         CHECK (source_type IN ('OPD','IPD','WALK_IN')),
    source_id            UUID,
    priority             VARCHAR(10)   NOT NULL DEFAULT 'ROUTINE'
                         CHECK (priority IN ('ROUTINE','URGENT','STAT')),
    status               VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
                         CHECK (status IN ('PENDING','SAMPLE_COLLECTED','IN_PROGRESS','COMPLETED','CANCELLED')),
    sample_collected_at  TIMESTAMPTZ,
    total_amount         NUMERIC(10,2) NOT NULL DEFAULT 0,
    discount             NUMERIC(10,2) NOT NULL DEFAULT 0,
    net_amount           NUMERIC(10,2) NOT NULL DEFAULT 0,
    payment_status       VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
                         CHECK (payment_status IN ('PENDING','PAID','PARTIAL','WAIVED')),
    notes                TEXT,
    created_at           TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by           VARCHAR(36),
    updated_by           VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_lab_orders_patient_id ON lab_orders(patient_id);
CREATE INDEX IF NOT EXISTS idx_lab_orders_status     ON lab_orders(status) WHERE status NOT IN ('COMPLETED','CANCELLED');
CREATE INDEX IF NOT EXISTS idx_lab_orders_date       ON lab_orders(created_at DESC);

-- ── Lab Order Items ───────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS lab_order_items (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id            UUID         NOT NULL REFERENCES lab_orders(id) ON DELETE CASCADE,
    test_id             UUID         NOT NULL,
    test_code           VARCHAR(30)  NOT NULL,
    test_name           VARCHAR(200) NOT NULL,
    price               NUMERIC(10,2) NOT NULL DEFAULT 0,
    unit                VARCHAR(50),
    normal_range        VARCHAR(200),
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                        CHECK (status IN ('PENDING','IN_PROGRESS','COMPLETED')),
    result              TEXT,
    result_note         TEXT,
    result_entered_at   TIMESTAMPTZ,
    result_entered_by   VARCHAR(200),
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_lab_items_order_id ON lab_order_items(order_id);
