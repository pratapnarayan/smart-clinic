-- Inventory module schema
-- inventory_item_categories → inventory_items (master)
-- stock_receipts  (goods received — increments current_stock)
-- stock_issues    (issued to dept  — decrements current_stock)

-- ── Item Categories ───────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS inventory_item_categories (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(300),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(36)
);

-- ── Inventory Items (master) ──────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS inventory_items (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    item_code       VARCHAR(30)  NOT NULL UNIQUE,
    name            VARCHAR(200) NOT NULL,
    description     VARCHAR(500),
    category_id     UUID         NOT NULL REFERENCES inventory_item_categories(id),
    category_name   VARCHAR(100) NOT NULL,
    unit            VARCHAR(30)  NOT NULL,
    reorder_level   INT          NOT NULL DEFAULT 10,
    current_stock   INT          NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_inv_items_category  ON inventory_items(category_id);
CREATE INDEX IF NOT EXISTS idx_inv_items_low_stock ON inventory_items(current_stock, reorder_level);

-- ── Stock Receipts (goods received, stock in) ─────────────────────────────────

CREATE TABLE IF NOT EXISTS stock_receipts (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    receipt_number  VARCHAR(30)   NOT NULL UNIQUE,
    entry_date      DATE          NOT NULL DEFAULT CURRENT_DATE,
    item_id         UUID          NOT NULL REFERENCES inventory_items(id),
    item_name       VARCHAR(200)  NOT NULL,
    item_unit       VARCHAR(30)   NOT NULL,
    quantity        INT           NOT NULL CHECK (quantity > 0),
    unit_cost       NUMERIC(10,2),
    total_cost      NUMERIC(12,2),
    supplier_name   VARCHAR(200),
    grn_number      VARCHAR(100),
    received_by     VARCHAR(200),
    notes           TEXT,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_receipts_item_id ON stock_receipts(item_id);
CREATE INDEX IF NOT EXISTS idx_receipts_date    ON stock_receipts(entry_date DESC);

-- ── Stock Issues (issued to departments, stock out) ───────────────────────────

CREATE TABLE IF NOT EXISTS stock_issues (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    issue_number    VARCHAR(30)   NOT NULL UNIQUE,
    issue_date      DATE          NOT NULL DEFAULT CURRENT_DATE,
    item_id         UUID          NOT NULL REFERENCES inventory_items(id),
    item_name       VARCHAR(200)  NOT NULL,
    item_unit       VARCHAR(30)   NOT NULL,
    quantity        INT           NOT NULL CHECK (quantity > 0),
    issued_to       VARCHAR(200)  NOT NULL,
    issued_by       VARCHAR(200),
    purpose         VARCHAR(500),
    notes           TEXT,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_issues_item_id ON stock_issues(item_id);
CREATE INDEX IF NOT EXISTS idx_issues_date    ON stock_issues(issue_date DESC);
CREATE INDEX IF NOT EXISTS idx_issues_dept    ON stock_issues(issued_to);
