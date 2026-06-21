-- Finance module schema
-- expense_categories → expense_entries (expenditures)
-- income_entries (revenue tracking)

-- ── Expense Categories ────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS expense_categories (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(300),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(36)
);

-- ── Income Entries ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS income_entries (
    id            UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    entry_number  VARCHAR(30)   NOT NULL UNIQUE,
    entry_date    DATE          NOT NULL DEFAULT CURRENT_DATE,
    source_type   VARCHAR(20)   NOT NULL DEFAULT 'OTHER'
                  CHECK (source_type IN ('OPD','IPD','PHARMACY','PATHOLOGY','RADIOLOGY','OTHER')),
    source_id     UUID,
    patient_name  VARCHAR(200),
    amount        NUMERIC(12,2) NOT NULL CHECK (amount > 0),
    description   TEXT          NOT NULL,
    payment_mode  VARCHAR(20)   NOT NULL DEFAULT 'CASH'
                  CHECK (payment_mode IN ('CASH','CARD','UPI','CHEQUE','NEFT','OTHER')),
    reference_no  VARCHAR(100),
    received_by   VARCHAR(200),
    notes         TEXT,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36),
    updated_by    VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_income_date        ON income_entries(entry_date DESC);
CREATE INDEX IF NOT EXISTS idx_income_source_type ON income_entries(source_type);

-- ── Expense Entries ───────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS expense_entries (
    id            UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    entry_number  VARCHAR(30)   NOT NULL UNIQUE,
    entry_date    DATE          NOT NULL DEFAULT CURRENT_DATE,
    category_id   UUID          NOT NULL REFERENCES expense_categories(id),
    category_name VARCHAR(100)  NOT NULL,
    description   TEXT          NOT NULL,
    amount        NUMERIC(12,2) NOT NULL CHECK (amount > 0),
    payment_mode  VARCHAR(20)   NOT NULL DEFAULT 'CASH'
                  CHECK (payment_mode IN ('CASH','CARD','UPI','CHEQUE','NEFT','OTHER')),
    reference_no  VARCHAR(100),
    paid_to       VARCHAR(200),
    approved_by   VARCHAR(200),
    notes         TEXT,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36),
    updated_by    VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_expense_date     ON expense_entries(entry_date DESC);
CREATE INDEX IF NOT EXISTS idx_expense_category ON expense_entries(category_id);
