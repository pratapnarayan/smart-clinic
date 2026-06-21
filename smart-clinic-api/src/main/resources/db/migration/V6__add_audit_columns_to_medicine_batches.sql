-- Make medicine_batches consistent with every other audited table.
-- created_by / updated_by were omitted in V3; adding them here so the
-- Hibernate entity can safely extend AuditEntity.

ALTER TABLE medicine_batches
    ADD COLUMN IF NOT EXISTS created_by VARCHAR(36),
    ADD COLUMN IF NOT EXISTS updated_by VARCHAR(36);
