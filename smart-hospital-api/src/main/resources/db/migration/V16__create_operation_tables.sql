-- Operation Theatre module schema
-- ot_theatres   (physical OT room catalog)
-- ot_schedules  (scheduling → in-progress → post-op completion lifecycle)
-- ot_consumables (inventory items consumed per operation)

-- ── OT Theatres ───────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS ot_theatres (
    id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    theatre_number VARCHAR(20)  NOT NULL UNIQUE,
    name           VARCHAR(100) NOT NULL,
    type           VARCHAR(20)  NOT NULL DEFAULT 'GENERAL'
                   CHECK (type IN ('GENERAL','CARDIAC','NEURO','ORTHO','PAEDIATRIC','EMERGENCY','DIAGNOSTIC')),
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by     VARCHAR(36)
);

-- ── OT Schedules (planning + post-op in one entity) ──────────────────────────

CREATE TABLE IF NOT EXISTS ot_schedules (
    id                       UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    schedule_number          VARCHAR(30)  NOT NULL UNIQUE,
    admission_id             UUID,
    patient_id               UUID,
    patient_name             VARCHAR(200) NOT NULL,
    theatre_id               UUID         NOT NULL REFERENCES ot_theatres(id),
    theatre_name             VARCHAR(100) NOT NULL,
    scheduled_date           DATE         NOT NULL,
    scheduled_start          TIMESTAMPTZ  NOT NULL,
    estimated_duration_mins  INT          NOT NULL DEFAULT 60,
    procedure_name           VARCHAR(300) NOT NULL,
    operation_type           VARCHAR(20)  NOT NULL DEFAULT 'ELECTIVE'
                             CHECK (operation_type IN ('ELECTIVE','EMERGENCY','DIAGNOSTIC')),
    priority                 VARCHAR(20)  NOT NULL DEFAULT 'ROUTINE'
                             CHECK (priority IN ('ROUTINE','URGENT','EMERGENCY')),
    status                   VARCHAR(20)  NOT NULL DEFAULT 'SCHEDULED'
                             CHECK (status IN ('SCHEDULED','IN_PROGRESS','COMPLETED','POSTPONED','CANCELLED')),
    surgeon_id               UUID,
    surgeon_name             VARCHAR(200),
    anesthetist_id           UUID,
    anesthetist_name         VARCHAR(200),
    assistant_names          TEXT,
    pre_op_diagnosis         TEXT,
    blood_request_id         UUID,
    blood_request_number     VARCHAR(30),
    notes                    TEXT,
    -- Post-op fields (NULL until operation is completed)
    actual_start             TIMESTAMPTZ,
    actual_end               TIMESTAMPTZ,
    anesthesia_type          VARCHAR(20)  CHECK (anesthesia_type IN ('GENERAL','SPINAL','EPIDURAL','LOCAL','REGIONAL')),
    post_op_diagnosis        TEXT,
    procedure_details        TEXT,
    complications            TEXT,
    surgeon_notes            TEXT,
    outcome                  VARCHAR(20)  CHECK (outcome IN ('SUCCESSFUL','COMPLICATED','INCOMPLETE')),
    patient_condition_after  VARCHAR(20)  CHECK (patient_condition_after IN ('STABLE','CRITICAL','DECEASED')),
    created_at               TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by               VARCHAR(36),
    updated_by               VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_ot_sched_date    ON ot_schedules(scheduled_date DESC);
CREATE INDEX IF NOT EXISTS idx_ot_sched_theatre ON ot_schedules(theatre_id, scheduled_date);
CREATE INDEX IF NOT EXISTS idx_ot_sched_status  ON ot_schedules(status) WHERE status NOT IN ('COMPLETED','CANCELLED','POSTPONED');
CREATE INDEX IF NOT EXISTS idx_ot_sched_patient ON ot_schedules(patient_id);
CREATE INDEX IF NOT EXISTS idx_ot_sched_surgeon ON ot_schedules(surgeon_id);

-- ── OT Consumables (inventory items used, deducted on completion) ─────────────

CREATE TABLE IF NOT EXISTS ot_consumables (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    schedule_id   UUID         NOT NULL REFERENCES ot_schedules(id) ON DELETE CASCADE,
    item_id       UUID         NOT NULL,
    item_name     VARCHAR(200) NOT NULL,
    item_unit     VARCHAR(30)  NOT NULL,
    quantity_used INT          NOT NULL CHECK (quantity_used > 0),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_ot_cons_schedule ON ot_consumables(schedule_id);
