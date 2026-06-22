-- Link OPD visits back to the appointment that triggered them (nullable — walk-ins have none)
ALTER TABLE opd_visits
    ADD COLUMN IF NOT EXISTS appointment_id UUID,
    ADD COLUMN IF NOT EXISTS visit_source   VARCHAR(20) NOT NULL DEFAULT 'WALK_IN'
        CHECK (visit_source IN ('APPOINTMENT', 'WALK_IN'));

CREATE INDEX IF NOT EXISTS idx_opd_appointment_id
    ON opd_visits(appointment_id) WHERE appointment_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_opd_date_status
    ON opd_visits(visit_date, visit_status) WHERE visit_status != 'CANCELLED';
