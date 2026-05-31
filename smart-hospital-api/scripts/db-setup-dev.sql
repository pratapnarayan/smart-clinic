-- Run this ONCE against your local PostgreSQL as the postgres superuser:
--   psql -U postgres -f scripts/db-setup-dev.sql
--
-- What it does:
--   1. Creates the smarthospital database (if not exists)
--   2. Inside that database, creates the hospital_001 tenant schema
--   3. Flyway will create all tables inside hospital_001 on first app startup

-- Step 1: Create database (must be run outside a transaction)
SELECT 'CREATE DATABASE smarthospital'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'smarthospital')\gexec

-- Step 2: Connect to it and create the tenant schema
\c smarthospital

CREATE SCHEMA IF NOT EXISTS hospital_001;

-- Confirm
SELECT schema_name FROM information_schema.schemata
WHERE schema_name IN ('public', 'hospital_001')
ORDER BY schema_name;
