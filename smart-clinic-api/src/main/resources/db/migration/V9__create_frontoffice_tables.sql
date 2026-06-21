-- Front Office module schema
-- appointments  : scheduled patient-doctor slots
-- opd_tokens    : walk-in daily queue (resets each day)

-- ── Appointments ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS appointments (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    appointment_number VARCHAR(30)  NOT NULL UNIQUE,
    patient_id         UUID         NOT NULL,
    patient_name       VARCHAR(200) NOT NULL,
    patient_mobile     VARCHAR(15),
    doctor_id          UUID,
    doctor_name        VARCHAR(200),
    department         VARCHAR(100),
    appointment_date   DATE         NOT NULL,
    time_slot          VARCHAR(20),                -- e.g. "10:00-10:30"
    appointment_type   VARCHAR(30)  NOT NULL DEFAULT 'CONSULTATION'
                       CHECK (appointment_type IN ('CONSULTATION','FOLLOW_UP','EMERGENCY','PROCEDURE')),
    status             VARCHAR(20)  NOT NULL DEFAULT 'SCHEDULED'
                       CHECK (status IN ('SCHEDULED','CONFIRMED','CHECKED_IN','COMPLETED','CANCELLED','NO_SHOW')),
    notes              TEXT,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by         VARCHAR(36),
    updated_by         VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_apt_patient_id       ON appointments(patient_id);
CREATE INDEX IF NOT EXISTS idx_apt_appointment_date ON appointments(appointment_date DESC);
CREATE INDEX IF NOT EXISTS idx_apt_doctor_id        ON appointments(doctor_id) WHERE doctor_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_apt_status           ON appointments(status) WHERE status NOT IN ('COMPLETED','CANCELLED');

-- ── OPD Tokens ────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS opd_tokens (
    id                   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    token_number         VARCHAR(20)  NOT NULL,
    patient_id           UUID         NOT NULL,
    patient_name         VARCHAR(200) NOT NULL,
    patient_mobile       VARCHAR(15),
    department           VARCHAR(100) NOT NULL,
    doctor_id            UUID,
    doctor_name          VARCHAR(200),
    token_date           DATE         NOT NULL DEFAULT CURRENT_DATE,
    priority             VARCHAR(20)  NOT NULL DEFAULT 'NORMAL'
                         CHECK (priority IN ('NORMAL','URGENT')),
    status               VARCHAR(20)  NOT NULL DEFAULT 'WAITING'
                         CHECK (status IN ('WAITING','IN_PROGRESS','COMPLETED','SKIPPED')),
    linked_appointment_id UUID,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by           VARCHAR(36),
    UNIQUE (token_date, department, token_number)
);

CREATE INDEX IF NOT EXISTS idx_token_date_dept  ON opd_tokens(token_date, department);
CREATE INDEX IF NOT EXISTS idx_token_patient_id ON opd_tokens(patient_id);
CREATE INDEX IF NOT EXISTS idx_token_status     ON opd_tokens(status) WHERE status IN ('WAITING','IN_PROGRESS');
