-- patient_name was in the PharmacyBill entity but missing from V3 schema.
-- Denormalised snapshot so bill history is readable even after patient edits.

ALTER TABLE pharmacy_bills
    ADD COLUMN IF NOT EXISTS patient_name VARCHAR(200);
