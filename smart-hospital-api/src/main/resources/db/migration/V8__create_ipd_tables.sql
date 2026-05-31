-- IPD module schema
-- wards → beds (many per ward)
-- ipd_admissions → ipd_charges (many per admission)

-- ── Wards ────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS wards (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    ward_type   VARCHAR(50)  NOT NULL DEFAULT 'GENERAL'
                CHECK (ward_type IN ('GENERAL','ICU','NICU','MATERNITY','SURGERY','PEDIATRIC','ORTHOPEDIC','PRIVATE')),
    total_beds  INT          NOT NULL DEFAULT 0,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(36)
);

-- ── Beds ─────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS beds (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    ward_id      UUID          NOT NULL REFERENCES wards(id),
    bed_number   VARCHAR(20)   NOT NULL,
    bed_type     VARCHAR(50)   NOT NULL DEFAULT 'GENERAL'
                 CHECK (bed_type IN ('GENERAL','PRIVATE','ICU','SEMI_PRIVATE')),
    daily_charge NUMERIC(10,2) NOT NULL DEFAULT 0,
    status       VARCHAR(20)   NOT NULL DEFAULT 'AVAILABLE'
                 CHECK (status IN ('AVAILABLE','OCCUPIED','MAINTENANCE')),
    UNIQUE (ward_id, bed_number)
);

CREATE INDEX IF NOT EXISTS idx_beds_ward_id       ON beds(ward_id);
CREATE INDEX IF NOT EXISTS idx_beds_status        ON beds(status) WHERE status = 'AVAILABLE';

-- ── IPD Admissions ───────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS ipd_admissions (
    id                     UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    admission_number       VARCHAR(30)   NOT NULL UNIQUE,
    patient_id             UUID          NOT NULL,
    patient_name           VARCHAR(200)  NOT NULL,
    opd_visit_id           UUID,
    admission_date         TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    ward_id                UUID          NOT NULL REFERENCES wards(id),
    bed_id                 UUID          NOT NULL REFERENCES beds(id),
    doctor_id              UUID,
    doctor_name            VARCHAR(200),
    admission_diagnosis    TEXT,
    notes                  TEXT,
    status                 VARCHAR(20)   NOT NULL DEFAULT 'ADMITTED'
                           CHECK (status IN ('ADMITTED','TRANSFERRED','DISCHARGED','DECEASED')),
    discharge_date         TIMESTAMPTZ,
    final_diagnosis        TEXT,
    condition_at_discharge VARCHAR(20)
                           CHECK (condition_at_discharge IN ('STABLE','IMPROVED','CRITICAL','UNCHANGED','DECEASED')),
    discharge_notes        TEXT,
    follow_up_instructions TEXT,
    total_charges          NUMERIC(10,2) NOT NULL DEFAULT 0,
    discount               NUMERIC(10,2) NOT NULL DEFAULT 0,
    net_amount             NUMERIC(10,2) NOT NULL DEFAULT 0,
    payment_status         VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
                           CHECK (payment_status IN ('PENDING','PAID','PARTIAL','WAIVED')),
    created_at             TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by             VARCHAR(36),
    updated_by             VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_ipd_patient_id     ON ipd_admissions(patient_id);
CREATE INDEX IF NOT EXISTS idx_ipd_admission_date ON ipd_admissions(admission_date DESC);
CREATE INDEX IF NOT EXISTS idx_ipd_status         ON ipd_admissions(status) WHERE status = 'ADMITTED';
CREATE INDEX IF NOT EXISTS idx_ipd_bed_id         ON ipd_admissions(bed_id);

-- ── IPD Charges ──────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS ipd_charges (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    admission_id UUID          NOT NULL REFERENCES ipd_admissions(id) ON DELETE CASCADE,
    category     VARCHAR(50)   NOT NULL DEFAULT 'OTHER'
                 CHECK (category IN ('BED_CHARGE','NURSING','DOCTOR_VISIT','PROCEDURE','MEDICINE','OTHER')),
    description  VARCHAR(200)  NOT NULL,
    amount       NUMERIC(10,2) NOT NULL,
    charge_date  DATE          NOT NULL DEFAULT CURRENT_DATE,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by   VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_ipd_charges_admission ON ipd_charges(admission_id);
