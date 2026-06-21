-- Pharmacy module schema
-- medicine_categories → medicines → medicine_batches → pharmacy_bills → pharmacy_bill_items

CREATE TABLE IF NOT EXISTS medicine_categories (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS medicines (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id   UUID         NOT NULL REFERENCES medicine_categories(id),
    name          VARCHAR(200) NOT NULL,
    generic_name  VARCHAR(200),
    unit          VARCHAR(20)  NOT NULL,   -- e.g. TAB, CAP, ML, MG
    reorder_level INT          NOT NULL DEFAULT 10,
    deleted_at    TIMESTAMPTZ,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36),
    updated_by    VARCHAR(36)
);

CREATE TABLE IF NOT EXISTS medicine_batches (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    medicine_id     UUID          NOT NULL REFERENCES medicines(id),
    batch_number    VARCHAR(50)   NOT NULL,
    expiry_date     DATE          NOT NULL,
    quantity        INT           NOT NULL DEFAULT 0,
    purchase_price  NUMERIC(10,2) NOT NULL,
    sale_price      NUMERIC(10,2) NOT NULL,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- Fixes INNER JOIN data loss from legacy Pharmacy Bill 500 crash:
-- bill survives even if batch is later deleted (LEFT JOIN via optional FK)
CREATE TABLE IF NOT EXISTS pharmacy_bills (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id   UUID          REFERENCES patients(id),   -- nullable: over-the-counter sales
    bill_number  VARCHAR(20)   NOT NULL UNIQUE,
    total_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    discount     NUMERIC(12,2) NOT NULL DEFAULT 0,
    net_amount   NUMERIC(12,2) NOT NULL DEFAULT 0,
    payment_mode VARCHAR(20)   NOT NULL DEFAULT 'CASH',
    status       VARCHAR(20)   NOT NULL DEFAULT 'PAID',
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by   VARCHAR(36)
);

CREATE TABLE IF NOT EXISTS pharmacy_bill_items (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    bill_id      UUID          NOT NULL REFERENCES pharmacy_bills(id),
    batch_id     UUID          REFERENCES medicine_batches(id) ON DELETE SET NULL,  -- historical record preserved
    medicine_name VARCHAR(200) NOT NULL,   -- denormalized so history is never lost even if medicine is deleted
    quantity     INT           NOT NULL,
    unit_price   NUMERIC(10,2) NOT NULL,
    total_price  NUMERIC(12,2) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_pharmacy_bills_patient ON pharmacy_bills(patient_id) WHERE patient_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_medicine_batches_expiry ON medicine_batches(expiry_date);
CREATE INDEX IF NOT EXISTS idx_medicines_category     ON medicines(category_id);
