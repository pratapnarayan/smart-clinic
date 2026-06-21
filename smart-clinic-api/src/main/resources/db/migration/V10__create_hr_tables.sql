-- HR module schema
-- hr_departments → designations → employees
-- employees → attendance_records (one per employee per day)
-- employees → leave_requests

-- ── HR Departments ───────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS hr_departments (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL UNIQUE,
    code       VARCHAR(20)  NOT NULL UNIQUE,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by VARCHAR(36)
);

-- ── Designations ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS designations (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    title         VARCHAR(100) NOT NULL,
    department_id UUID         REFERENCES hr_departments(id),
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36),
    UNIQUE (title, department_id)
);

CREATE INDEX IF NOT EXISTS idx_desig_dept ON designations(department_id);

-- ── Employees ─────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS employees (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_code   VARCHAR(30)  NOT NULL UNIQUE,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    date_of_birth   DATE,
    gender          VARCHAR(10)  CHECK (gender IN ('MALE','FEMALE','OTHER')),
    mobile          VARCHAR(15),
    email           VARCHAR(150) UNIQUE,
    address         TEXT,
    blood_group     VARCHAR(10),
    department_id   UUID         REFERENCES hr_departments(id),
    designation_id  UUID         REFERENCES designations(id),
    user_id         UUID,                            -- optional link to auth.users
    employment_type VARCHAR(20)  NOT NULL DEFAULT 'FULL_TIME'
                    CHECK (employment_type IN ('FULL_TIME','PART_TIME','CONTRACT','CONSULTANT')),
    join_date       DATE         NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
                    CHECK (status IN ('ACTIVE','ON_LEAVE','SUSPENDED','RESIGNED','TERMINATED')),
    deleted_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_emp_dept        ON employees(department_id) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_emp_status      ON employees(status)        WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_emp_mobile      ON employees(mobile)        WHERE deleted_at IS NULL;

-- ── Attendance Records ────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS attendance_records (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id   UUID         NOT NULL REFERENCES employees(id),
    attendance_date DATE       NOT NULL,
    check_in      TIME,
    check_out     TIME,
    status        VARCHAR(20)  NOT NULL DEFAULT 'PRESENT'
                  CHECK (status IN ('PRESENT','ABSENT','HALF_DAY','ON_LEAVE','HOLIDAY')),
    notes         VARCHAR(300),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36),
    UNIQUE (employee_id, attendance_date)
);

CREATE INDEX IF NOT EXISTS idx_att_date        ON attendance_records(attendance_date DESC);
CREATE INDEX IF NOT EXISTS idx_att_employee_id ON attendance_records(employee_id);

-- ── Leave Requests ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS leave_requests (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    leave_number    VARCHAR(30)  NOT NULL UNIQUE,
    employee_id     UUID         NOT NULL REFERENCES employees(id),
    employee_name   VARCHAR(200) NOT NULL,
    leave_type      VARCHAR(20)  NOT NULL
                    CHECK (leave_type IN ('CASUAL','SICK','EARNED','MATERNITY','PATERNITY','UNPAID')),
    from_date       DATE         NOT NULL,
    to_date         DATE         NOT NULL,
    total_days      INT          NOT NULL,
    reason          TEXT,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                    CHECK (status IN ('PENDING','APPROVED','REJECTED','CANCELLED')),
    approved_by_id  UUID,
    approver_notes  TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_leave_employee_id ON leave_requests(employee_id);
CREATE INDEX IF NOT EXISTS idx_leave_status      ON leave_requests(status) WHERE status = 'PENDING';
CREATE INDEX IF NOT EXISTS idx_leave_dates       ON leave_requests(from_date, to_date);
