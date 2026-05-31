-- Blood Bank module schema
-- blood_donors  (donor registry)
-- blood_units   (individual blood bags — status lifecycle)
-- blood_requests (request from patient/ward)
-- blood_issues   (unit issued against a request — immutable)

-- ── Blood Donors ──────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS blood_donors (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    donor_number       VARCHAR(30)  NOT NULL UNIQUE,
    first_name         VARCHAR(100) NOT NULL,
    last_name          VARCHAR(100) NOT NULL,
    gender             VARCHAR(10)  NOT NULL CHECK (gender IN ('MALE','FEMALE','OTHER')),
    date_of_birth      DATE         NOT NULL,
    blood_group        VARCHAR(10)  NOT NULL
                       CHECK (blood_group IN ('A_POS','A_NEG','B_POS','B_NEG','AB_POS','AB_NEG','O_POS','O_NEG')),
    mobile             VARCHAR(15),
    email              VARCHAR(150),
    address            TEXT,
    last_donation_date DATE,
    total_donations    INT          NOT NULL DEFAULT 0,
    active             BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by         VARCHAR(36),
    updated_by         VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_donors_blood_group ON blood_donors(blood_group);
CREATE INDEX IF NOT EXISTS idx_donors_mobile      ON blood_donors(mobile);

-- ── Blood Units ───────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS blood_units (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    unit_number     VARCHAR(30) NOT NULL UNIQUE,
    blood_group     VARCHAR(10) NOT NULL
                    CHECK (blood_group IN ('A_POS','A_NEG','B_POS','B_NEG','AB_POS','AB_NEG','O_POS','O_NEG')),
    donor_id        UUID,
    donor_name      VARCHAR(200),
    component_type  VARCHAR(30) NOT NULL DEFAULT 'WHOLE_BLOOD'
                    CHECK (component_type IN ('WHOLE_BLOOD','PACKED_CELLS','FRESH_FROZEN_PLASMA','PLATELET_CONCENTRATE')),
    volume_ml       INT         NOT NULL DEFAULT 450,
    collection_date DATE        NOT NULL DEFAULT CURRENT_DATE,
    expiry_date     DATE        NOT NULL,
    testing_status  VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                    CHECK (testing_status IN ('PENDING','CLEARED','REJECTED')),
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING_TESTING'
                    CHECK (status IN ('PENDING_TESTING','AVAILABLE','RESERVED','ISSUED','DISCARDED','EXPIRED')),
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_units_group_status ON blood_units(blood_group, status);
CREATE INDEX IF NOT EXISTS idx_units_status       ON blood_units(status);
CREATE INDEX IF NOT EXISTS idx_units_expiry       ON blood_units(expiry_date) WHERE status = 'AVAILABLE';

-- ── Blood Requests ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS blood_requests (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    request_number  VARCHAR(30)  NOT NULL UNIQUE,
    request_date    DATE         NOT NULL DEFAULT CURRENT_DATE,
    patient_id      UUID,
    patient_name    VARCHAR(200) NOT NULL,
    requested_by    VARCHAR(200),
    blood_group     VARCHAR(10)  NOT NULL,
    component_type  VARCHAR(30)  NOT NULL DEFAULT 'WHOLE_BLOOD',
    units_required  INT          NOT NULL DEFAULT 1,
    units_issued    INT          NOT NULL DEFAULT 0,
    urgency         VARCHAR(20)  NOT NULL DEFAULT 'ROUTINE'
                    CHECK (urgency IN ('ROUTINE','URGENT','EMERGENCY')),
    status          VARCHAR(30)  NOT NULL DEFAULT 'PENDING'
                    CHECK (status IN ('PENDING','PARTIALLY_FULFILLED','FULFILLED','CANCELLED')),
    required_by     TIMESTAMPTZ,
    notes           TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_blood_req_patient ON blood_requests(patient_id);
CREATE INDEX IF NOT EXISTS idx_blood_req_status  ON blood_requests(status) WHERE status NOT IN ('FULFILLED','CANCELLED');
CREATE INDEX IF NOT EXISTS idx_blood_req_date    ON blood_requests(request_date DESC);

-- ── Blood Issues (immutable once created) ─────────────────────────────────────

CREATE TABLE IF NOT EXISTS blood_issues (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    issue_number    VARCHAR(30)  NOT NULL UNIQUE,
    issue_date      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    request_id      UUID         NOT NULL REFERENCES blood_requests(id),
    request_number  VARCHAR(30)  NOT NULL,
    unit_id         UUID         NOT NULL UNIQUE REFERENCES blood_units(id),
    unit_number     VARCHAR(30)  NOT NULL,
    blood_group     VARCHAR(10)  NOT NULL,
    component_type  VARCHAR(30)  NOT NULL,
    issued_to       VARCHAR(200) NOT NULL,
    issued_by       VARCHAR(200),
    notes           TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_blood_issues_request ON blood_issues(request_id);
CREATE INDEX IF NOT EXISTS idx_blood_issues_date    ON blood_issues(issue_date DESC);
