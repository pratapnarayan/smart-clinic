# SmartClinic — Clinic Management Platform

A full-stack clinic management platform built with **Spring Boot 3** and **React 19**. SmartClinic covers a clinic's outpatient journey — front office appointment booking and OPD check-in, consultation and prescriptions, pharmacy dispensing, pathology orders and home sample collection, right up to visit billing — with a unified analytics layer across all modules.

> **Scope note:** This codebase is multi-tenant and also supports a larger `FULL` (hospital-scale) tenant type with additional modules — Doctor Directory, IPD, Radiology, Finance, Inventory, Blood Bank, and Operation Theatre. This document only covers the `CLINIC_OPD` tenant configuration, which is what this project is actually run as. The other modules exist in the codebase (see `smart-clinic-api/src/main/java/com/smartclinic/modules/`) but are out of scope here.

---

## Table of Contents

- [Who It Is For](#who-it-is-for)
- [Feature Overview](#feature-overview)
  - [Front Office & Appointments](#1-front-office--appointments)
  - [Patient Management](#2-patient-management)
  - [OPD — Outpatient Department](#3-opd--outpatient-department)
  - [Pharmacy](#4-pharmacy)
  - [Pathology](#5-pathology)
  - [HR & Attendance](#6-hr--attendance)
  - [Home Collections & Visit Bills](#7-home-collections--visit-bills)
  - [Reports & Analytics](#8-reports--analytics)
- [Access Control & Roles](#access-control--roles)
- [Multi-Tenancy](#multi-tenancy)
- [Architecture Overview](#architecture-overview)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Database Setup](#database-setup)
  - [Backend Setup](#backend-setup)
  - [Frontend Setup](#frontend-setup)
- [Default Dev Credentials](#default-dev-credentials)
- [API Documentation](#api-documentation)
- [Flyway Migration History](#flyway-migration-history)
- [Environment Variables](#environment-variables)
- [Security Notes](#security-notes)

---

## Who It Is For

SmartClinic is designed for **clinics and polyclinics** that need a single platform to manage their front desk, OPD consultations, pharmacy, and pathology — without the overhead of inpatient wards, operation theatres, or a separate finance/inventory back office. It runs as a multi-tenant SaaS application — each clinic gets its own isolated data schema — with a tenant-aware sidebar that shows only the modules a clinic actually needs.

---

## Feature Overview

### 1. Front Office & Appointments

The front office is the first touchpoint for every patient. SmartClinic provides a complete appointment lifecycle and a live OPD queue that updates automatically.

**Appointment management**
- Book appointments with date, time slot, doctor, department, and appointment type (Consultation / Follow-up / Emergency)
- Auto-generated appointment numbers in the format `APT-YYYY-NNNNN`
- Filter appointments by date, patient, or doctor
- Status flow: `SCHEDULED → CONFIRMED → CHECKED_IN → COMPLETED` (with `CANCELLED` and `NO_SHOW` branches)
- Restrict status updates — closed appointments (COMPLETED / CANCELLED / NO_SHOW) cannot be modified

**OPD Check-In (Appointment → Visit)**
- One-click check-in from the Appointments list — marks the appointment `CHECKED_IN` and creates a linked OPD visit atomically in a single transaction
- Optional symptoms and consultation fee captured at check-in time
- Concurrent check-in protection: pessimistic row-level lock on the appointment + partial unique index on `opd_visits.appointment_id` prevents duplicate visits even under race conditions
- Date guard: only today's appointments can be checked in; future or past appointments are rejected
- Error codes surfaced to the UI: `ALREADY_CHECKED_IN`, `APPOINTMENT_CLOSED`, `WRONG_DATE`

**OPD Token Queue**
- Generate numbered tokens (`T-001`, `T-002`, …) per department and date
- Token status: `WAITING → IN_PROGRESS → COMPLETED / SKIPPED`
- Assign doctor and department per token; optional link to an appointment
- Front office dashboard shows today's totals: appointments, tokens, waiting count, in-progress count, completed count

---

### 2. Patient Management

- **Quick registration** — Name, mobile, gender, date of birth; single-name patients use `LNU` (Last Name Unknown) as the last name
- **Full registration** — Address, blood group, emergency contact, allergies, ID number
- **Full-text search** — PostgreSQL `tsvector` index on name, mobile, and email; search returns instant results as the user types
- **Patient profile** — Demographics, upcoming appointments, recent OPD visits
- **Soft delete** — Records are deactivated, never hard-deleted

---

### 3. OPD — Outpatient Department

Every outpatient consultation is captured as an OPD visit regardless of whether it came from a booked appointment or a walk-in.

**Visit management**
- Visit source tracked: `APPOINTMENT` (created via check-in) or `WALK_IN` (created directly)
- Walk-in visits capture the consulting doctor by employee ID (see [HR & Attendance](#6-hr--attendance)) — doctor name is stored as a denormalised snapshot for display
- Auto-generated visit numbers
- Visit status: `REGISTERED → IN_PROGRESS → COMPLETED / CANCELLED`

**Live OPD Queue (Today's Queue tab)**
- Shows all `REGISTERED` and `IN_PROGRESS` visits for today
- Auto-refreshes every 20 seconds — no manual reload needed
- Visit source badge: `Appointment` (blue) vs `Walk-in` (cyan)
- Waiting and In Consultation counts shown as badges on the tab label

**Visit History tab**
- Date picker to browse any date's visits
- Paginated, sortable table

**Charges & clinical data**
- Consultation fee captured at check-in (appointment visits) or at walk-in creation
- Charges line items can be added per visit
- Prescription module: medicines, dosage, frequency, duration, instructions

---

### 4. Pharmacy

- **Medicine catalogue** — Name, category, formulation, unit, HSN code, GST rate
- **Stock batches** — Batch number, expiry date, purchase price, MRP, quantity; multiple batches per medicine
- **Dispensing bills** — Multi-item bills linked to a patient; each item deducts from the oldest non-expired batch (FEFO)
- **Stock alerts** — Medicines with quantity below reorder level
- **Pharmacy analytics** — Top-selling medicines, dispensing revenue trend, category-wise sales, stock movement

---

### 5. Pathology

- **Test catalogue** — Categories (Haematology, Biochemistry, Microbiology, …) and individual tests with reference ranges and units
- **Lab orders** — Order one or more tests for a patient, linked to an OPD visit
- **Sample collection** — Mark samples collected; record collection time and collector
- **Results entry** — Enter result values per test; mark order as `COMPLETED`
- **Pathology analytics** — Order volume, pending vs. completed ratio, revenue by test category, turnaround time

---

### 6. HR & Attendance

- **Departments and designations** — Organisational hierarchy
- **Employee records** — Personal details, designation, department, joining date, profile photo, salary
- **Consulting doctors are employee records** — a clinic's doctor(s) are managed as HR employees rather than through a separate doctor-directory module, and are selected from the employee list when creating a walk-in OPD visit
- **Attendance** — Mark daily attendance (Present / Absent / Half Day / On Leave)
- **Leave management** — Leave types, leave applications, approval workflow (Pending → Approved / Rejected)
- **Employee directory** — Search by name, department, or designation

---

### 7. Home Collections & Visit Bills

- **Home Collections** — Schedule pathology sample collection at a patient's home address; assign a technician and track collection status
- **Visit Bills** — Consolidated billing for a clinic visit (consultation + tests + medicines)

---

### 8. Reports & Analytics

A dedicated cross-cutting analytics layer aggregates data from clinic modules into interactive dashboards. Every page supports configurable date ranges with quick-select presets (Last 7 days / 30 days / 90 days / Custom).

| Dashboard | What It Shows |
|---|---|
| **Executive Dashboard** | Revenue, total patients, OPD visits |
| **Patient Analytics** | New vs. returning, registration trend, gender and age distribution, blood group breakdown |
| **Appointment Analytics** | Booking trend, cancellation rate, peak-hour heatmap, doctor-wise counts |
| **Pharmacy Analytics** | Top-selling medicines, dispensing revenue, stock movement, category-wise sales |
| **Laboratory Analytics** | Test volume, pending vs. completed ratio, revenue by category, turnaround time |

**Export** — Every dashboard has one-click export:
- **Excel (XLSX)** — Multi-sheet workbook: KPI summary, trend data, breakdown tables; styled with branded headers and formatted cells
- **PDF** — KPI grid, bar chart data, and trend tables via OpenPDF; downloaded through the API client so the JWT Authorization header is sent correctly

**Demo mode** — Append `?demo=true` to any analytics URL to load demo data without a backend connection.

---

## Access Control & Roles

Every JWT carries a `role` claim and a granular `permissions` claim. The backend uses `@PreAuthorize("hasAuthority('MODULE.ACTION')")` at the method level. Roles determine the permission set granted at login.

| Role | Primary Scope |
|------|---------------|
| `SUPER_ADMIN` | Platform management (tenant provisioning) only |
| `ADMIN` | Full access to all modules within their tenant |
| `DOCTOR` | Patient, OPD, Pathology |
| `NURSE` | Patient (read), OPD (read), Pathology (read) |
| `PHARMACIST` | Patient (read), Pharmacy |
| `RECEPTIONIST` | Patient (create/read), Front Office |
| `PATHOLOGIST` | Patient (read), Pathology (full) |
| `CLINIC_TECHNICIAN` | Home Collections |
| `PATIENT` | Own records only |

**Permission format** — `MODULE.ACTION`:

```
PATIENT.VIEW      PATIENT.CREATE     PATIENT.EDIT
OPD.VIEW          OPD.CREATE         OPD.EDIT
FRONTOFFICE.VIEW  FRONTOFFICE.CREATE FRONTOFFICE.EDIT
PHARMACY.VIEW     PHARMACY.CREATE
HR.VIEW           HR.CREATE          HR.EDIT          HR.MANAGE
PATHOLOGY.VIEW    PATHOLOGY.CREATE   PATHOLOGY.EDIT   PATHOLOGY.MANAGE
CLINIC.BILL.VIEW             CLINIC.BILL.CREATE            CLINIC.BILL.MANAGE
CLINIC.HOME_COLLECTION.VIEW  CLINIC.HOME_COLLECTION.CREATE  CLINIC.HOME_COLLECTION.EDIT
REPORTS.VIEW
```

`SUPER_ADMIN` receives a wildcard `*` permission that bypasses all checks.

---

## Multi-Tenancy

SmartClinic uses **schema-per-tenant** isolation on a single PostgreSQL database.

- Each clinic gets its own PostgreSQL schema (e.g. `clinic_001`, `clinic_002`)
- The tenant is resolved from the `tenant_id` claim embedded in the JWT — no extra DB call per request
- A `TenantAwareDataSource` JDBC proxy sets `search_path = <tenantId>` on every connection before executing queries
- Cross-tenant data access is architecturally impossible — queries cannot leak between schemas

Tenant type is stored per tenant (`CLINIC_OPD` for this document's scope) and controls which sidebar modules are shown.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        Browser (SPA)                            │
│              React 19 + TypeScript + Ant Design 5               │
│              Vite dev server  →  http://localhost:3000           │
│              /api  proxied  →  http://localhost:8080            │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP / JSON  (JWT in Authorization header)
┌────────────────────────────▼────────────────────────────────────┐
│                   Spring Boot 3.3 API                            │
│                    http://localhost:8080                          │
│                                                                   │
│  Filter chain:  TenantFilter → JwtAuthFilter → Spring Security   │
│                                                                   │
│  Modules used by CLINIC_OPD tenants:                              │
│  (domain / dto / repository / service / controller each)         │
│  auth · patient · opd · pharmacy · frontoffice · hr · pathology  │
│  setup · analytics · clinic                                      │
└────────────────────────────┬────────────────────────────────────┘
                             │ JDBC  (search_path per tenant)
┌────────────────────────────▼────────────────────────────────────┐
│                  PostgreSQL 16                                    │
│   public schema   →  tenant registry + super-admin users         │
│   clinic_001      →  all clinical & operational tables           │
│   clinic_002      →  same tables, fully isolated data            │
└─────────────────────────────────────────────────────────────────┘
```

**Design principles:**
- Modular monolith with clean domain boundaries — designed for eventual microservices extraction
- No JPA foreign keys across module boundaries — cross-module references use plain UUID columns
- Denormalised name snapshot columns on every entity (patient name, doctor name) — no joins across modules for display data
- Flyway-only schema management — Hibernate `ddl-auto: validate`; the ORM never touches DDL
- All API responses wrapped in `ApiResponse<T>`; paginated responses in `PageResponse<T>`
- Builder pattern on all domain entities; no Lombok

---

## Tech Stack

### Backend (`smart-clinic-api/`)

| Concern | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3.5 |
| Security | Spring Security 6 + jjwt 0.12.6 (HMAC-SHA256) |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL 16 |
| Migrations | Flyway 10 |
| Build | Maven 3 |
| API Docs | SpringDoc OpenAPI 2.6 (Swagger UI) |
| Metrics | Micrometer, exposed via `/actuator/metrics` (`health`, `info`, `metrics` only — Prometheus format not currently enabled) |
| Excel Export | Apache POI 5.2.5 |
| PDF Export | OpenPDF |

### Frontend (`smart-clinic-web/`)

| Concern | Technology |
|---|---|
| Language | TypeScript |
| Framework | React 19 |
| Build Tool | Vite |
| UI Library | Ant Design 5 |
| Charts | ApexCharts 5 + react-apexcharts |
| State | Zustand 4 (with localStorage persistence) |
| Server State | TanStack React Query 5 |
| HTTP Client | Axios (JWT interceptor + auto-refresh) |
| Routing | React Router DOM 6 |
| Styling | Tailwind CSS 3 |

---

## Getting Started

### Prerequisites

| Tool | Minimum Version |
|------|-----------------|
| Java JDK | 21 |
| Maven | 3.9+ |
| PostgreSQL | 16 |
| Node.js | 20 LTS |
| npm | 10+ |

### Database Setup

Run the provided SQL script once as the PostgreSQL superuser, to create the `smartclinic` database:

```bash
psql -U postgres -f smart-clinic-api/scripts/db-setup-dev.sql
```

You do **not** need to create a schema by hand for clinic development — on first startup with the `dev` profile active, `ClinicDevDataSeeder` provisions the `clinic_001` schema itself and runs Flyway migrations into it automatically.

### Backend Setup

1. Open `smart-clinic-api/` as a Maven project in IntelliJ IDEA
2. Edit the Run Configuration for `SmartClinicApplication`
3. Set **Active profiles** to `dev`
4. Click **Run**

No extra configuration is needed for local development — the `dev` profile uses:

| Setting | Dev Default |
|---------|-------------|
| DB URL | `jdbc:postgresql://localhost:5432/smartclinic` |
| DB User | `postgres` |
| DB Password | `postgres` |
| JWT Secret | `dev-secret-key-change-in-prod-min32c` |

On first startup, Flyway runs the `public`-schema migrations and `ClinicDevDataSeeder` creates the `clinic_001` tenant with sample data and default users.

### Frontend Setup

```bash
cd smart-clinic-web
npm install
npm run dev
```

The dev server starts on **http://localhost:3000** and proxies all `/api/**` requests to `http://localhost:8080`.

```bash
npm run build    # Production build → dist/
npm run lint     # ESLint check
npm run preview  # Preview the production build locally
```

---

## Default Dev Credentials

> Created automatically by `ClinicDevDataSeeder` on first startup (`dev` profile only), tenant `clinic_001`.

| Role | Email | Password |
|------|-------|----------|
| Admin | `admin@clinic001.com` | `Admin@1234` |
| Doctor | `doctor@clinic001.com` | `Doctor@1234` |
| Receptionist | `receptionist@clinic001.com` | `Recept@1234` |
| Pathologist | `pathologist@clinic001.com` | `Path@1234` |
| Clinic Technician | `technician@clinic001.com` | `Tech@1234` |

### Super Admin

| Field | Value |
|-------|-------|
| Email | `superadmin@smartclinic.com` |
| Password | `SuperAdmin@1234` |
| Tenant ID | *(leave blank)* |
| Access | Tenant provisioning only |

---

## API Documentation

With the backend running, open **http://localhost:8080/swagger-ui.html**

All endpoints are grouped by module tag. The Swagger UI includes request/response schemas for every endpoint and supports try-it-out with JWT authorization.

Raw OpenAPI spec: **http://localhost:8080/v3/api-docs**

---

## Flyway Migration History

Migrations run against every tenant schema (including `clinic_001`), so tables for modules outside this document's scope (IPD, Radiology, Finance, Inventory, Blood Bank, Operation Theatre, Doctor Directory) do exist in the schema even though the clinic UI doesn't surface them.

| Version | Description |
|---------|-------------|
| V1 | Tenant registry (`public.tenants`, `public.super_admin_users`) |
| V2 | Patient tables with PostgreSQL FTS `tsvector` index |
| V3 | Pharmacy tables (medicines, categories, batches, bills) |
| V4 | Users table |
| V5 | OPD tables (visits, charges, prescriptions, items) |
| V6 | Audit columns on medicine batches |
| V7 | Patient name snapshot on pharmacy bills |
| V8 | IPD tables (wards, beds, admissions, daily charges) |
| V9 | Front Office tables (appointments, OPD tokens) |
| V10 | HR tables (departments, designations, employees, attendance, leave) |
| V11 | Pathology tables (categories, tests, orders, samples, results) |
| V12 | Radiology tables (modalities, studies, orders, reports) |
| V13 | Finance tables (income, expenses, expense categories) |
| V14 | Inventory tables (item categories, items, receipts, issues) |
| V15 | Blood Bank tables (donors, blood units, requests) |
| V16 | Operation Theatre tables (theatres, schedules, post-op, consumables) |
| V17 | Doctor tables (profiles, specializations, availability slots) |
| V18 | Profile photo column on employees |
| V19 | `clinic_type` column on tenants (`FULL` / `CLINIC_OPD`) |
| V20 | Home collection tables |
| V21 | Visit bill tables |
| V22 | `CLINIC_TECHNICIAN` role |
| V23 | `appointment_id` and `visit_source` on OPD visits; partial unique index on `appointment_id` |

---

## Environment Variables

| Variable | Dev Default | Description |
|----------|-------------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/smartclinic` | JDBC connection URL |
| `DB_USER` | `postgres` | Database username |
| `DB_PASSWORD` | `postgres` | Database password |
| `JWT_SECRET` | `dev-secret-key-change-in-prod-min32c` | HMAC signing secret (≥ 32 chars enforced at startup) |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:3000` | Comma-separated allowed CORS origins |

For production, set these via OS environment variables or a secrets manager. Never commit production secrets.

---

## Security Notes

- **JWT secret** — Change `JWT_SECRET` before any non-local deployment. A secret shorter than 32 characters causes a hard startup failure.
- **Token lifetimes** — Access tokens expire in 15 minutes; refresh tokens in 7 days. The frontend auto-refreshes on every app load and on 401 responses via the Axios interceptor.
- **HTTPS** — Always run behind TLS in production. The Vite proxy and CORS config are for `localhost` development only.
- **Tenant isolation** — Every query runs with `search_path = <tenantId>` set at the JDBC level. Cross-tenant access is architecturally prevented.
- **No Hibernate auto-DDL** — `ddl-auto: validate` in all profiles. All schema changes go through Flyway.
- **Export authentication** — Analytics export endpoints require a valid JWT. The frontend uses the Axios API client (blob response type) rather than `window.open` to ensure the Authorization header is sent.
- **Production profile** — `application-prod.yml` is excluded from version control. Create it separately with production credentials.
