-- OPD module schema
-- opd_visits → opd_charges (many per visit)
-- opd_visits → prescriptions (one per visit) → prescription_items (many per prescription)

-- ── OPD Visits ──────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS opd_visits (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    visit_number     VARCHAR(30)  NOT NULL UNIQUE,
    patient_id       UUID         NOT NULL,               -- UUID ref only; no FK across modules
    patient_name     VARCHAR(200) NOT NULL,               -- snapshot; survives patient edits
    visit_date       DATE         NOT NULL DEFAULT CURRENT_DATE,
    department       VARCHAR(100),
    doctor_id        UUID,                                -- UUID ref to users table
    doctor_name      VARCHAR(200),
    symptoms         TEXT,
    diagnosis        TEXT,
    notes            TEXT,
    consultation_fee NUMERIC(10,2) NOT NULL DEFAULT 0,
    total_charges    NUMERIC(10,2) NOT NULL DEFAULT 0,
    discount         NUMERIC(10,2) NOT NULL DEFAULT 0,
    net_amount       NUMERIC(10,2) NOT NULL DEFAULT 0,
    payment_status   VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                     CHECK (payment_status IN ('PENDING','PAID','PARTIAL','WAIVED')),
    visit_status     VARCHAR(20)  NOT NULL DEFAULT 'REGISTERED'
                     CHECK (visit_status IN ('REGISTERED','IN_PROGRESS','COMPLETED','CANCELLED')),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(36),
    updated_by       VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_opd_patient_id   ON opd_visits(patient_id);
CREATE INDEX IF NOT EXISTS idx_opd_visit_date   ON opd_visits(visit_date DESC);
CREATE INDEX IF NOT EXISTS idx_opd_visit_status ON opd_visits(visit_status) WHERE visit_status != 'CANCELLED';
CREATE INDEX IF NOT EXISTS idx_opd_doctor_id    ON opd_visits(doctor_id)    WHERE doctor_id IS NOT NULL;

-- ── OPD Charges ─────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS opd_charges (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    visit_id    UUID          NOT NULL REFERENCES opd_visits(id) ON DELETE CASCADE,
    description VARCHAR(150)  NOT NULL,
    amount      NUMERIC(10,2) NOT NULL,
    category    VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_opd_charges_visit ON opd_charges(visit_id);

-- ── Prescriptions ───────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS prescriptions (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    visit_id      UUID        NOT NULL UNIQUE REFERENCES opd_visits(id) ON DELETE CASCADE,
    advice        TEXT,
    follow_up_days INT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36),
    updated_by    VARCHAR(36)
);

-- ── Prescription Items ──────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS prescription_items (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_id  UUID         NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    medicine_name    VARCHAR(200) NOT NULL,
    dose             VARCHAR(50)  NOT NULL,
    frequency        VARCHAR(80)  NOT NULL,
    duration         VARCHAR(50)  NOT NULL,
    instructions     VARCHAR(200),
    sort_order       INT          NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_rx_items_prescription ON prescription_items(prescription_id);
