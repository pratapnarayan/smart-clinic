-- Add CLINIC_TECHNICIAN to the users.role check constraint (per-tenant migration)
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

ALTER TABLE users
    ADD CONSTRAINT users_role_check
        CHECK (role IN ('SUPER_ADMIN','ADMIN','DOCTOR','NURSE',
                        'PHARMACIST','RECEPTIONIST','ACCOUNTANT',
                        'PATHOLOGIST','PATIENT','CLINIC_TECHNICIAN'));
