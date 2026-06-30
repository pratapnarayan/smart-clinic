package com.smartclinic.modules.clinic.seeder;

import com.smartclinic.core.tenant.TenantContext;
import com.smartclinic.core.tenant.TenantMigrationService;
import com.smartclinic.modules.auth.domain.Role;
import com.smartclinic.modules.auth.domain.User;
import com.smartclinic.modules.auth.repository.UserRepository;
import com.smartclinic.modules.hr.domain.Designation;
import com.smartclinic.modules.hr.domain.Employee;
import com.smartclinic.modules.hr.domain.HrDepartment;
import com.smartclinic.modules.hr.repository.DesignationRepository;
import com.smartclinic.modules.hr.repository.EmployeeRepository;
import com.smartclinic.modules.hr.repository.HrDepartmentRepository;
import com.smartclinic.modules.pathology.domain.LabTest;
import com.smartclinic.modules.pathology.domain.LabTestCategory;
import com.smartclinic.modules.pathology.repository.LabTestCategoryRepository;
import com.smartclinic.modules.pathology.repository.LabTestRepository;
import com.smartclinic.modules.patient.domain.Patient;
import com.smartclinic.modules.patient.repository.PatientRepository;
import com.smartclinic.modules.pharmacy.domain.Medicine;
import com.smartclinic.modules.pharmacy.domain.MedicineCategory;
import com.smartclinic.modules.pharmacy.repository.MedicineCategoryRepository;
import com.smartclinic.modules.pharmacy.repository.MedicineRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;

/**
 * Seeds the clinic_001 demo tenant and all data required to test the full
 * SmartClinic workflow on every dev startup.
 *
 * Uses @PostConstruct, which fires during bean initialization — before any ApplicationRunner
 * beans execute. @Order has no effect here; it is intentionally omitted.
 * Spring Boot's Flyway auto-config runs V1 (public.tenants creation) before user beans
 * are initialized, so the INSERT below is safe on first run.
 *
 * Self-provisioning steps (idempotent):
 *   1. INSERT into public.tenants ON CONFLICT DO NOTHING
 *   2. CREATE SCHEMA clinic_001 via raw JDBC with autoCommit=true (DDL cannot be in a transaction)
 *   3. Run Flyway migrations inside clinic_001 via TenantMigrationService.migrateTenantSchema()
 *
 * NOTE: No @Transactional here — same reason as DevDataSeeder.
 * Without @Transactional, each repository.save() opens its own transaction and
 * gets a fresh connection from TenantAwareDataSource, which reads TenantContext at
 * that moment and sets search_path correctly.  With @Transactional the connection
 * would be acquired once (with TenantContext still null) and all saves would land
 * in the public schema.
 */
@Component
@Profile({"dev", "seed"})
public class ClinicDevDataSeeder {

    private static final Logger log = LoggerFactory.getLogger(ClinicDevDataSeeder.class);

    private static final String TENANT_SCHEMA = "clinic_001";
    private static final String TENANT_NAME   = "Demo Clinic";
    private static final String CLINIC_TYPE   = "CLINIC_OPD";

    // ── Demo users ───────────────────────────────────────────────────────────────
    private static final String ADMIN_EMAIL        = "admin@clinic001.com";
    private static final String ADMIN_PASS         = "Admin@1234";

    private static final String DOCTOR_EMAIL       = "doctor@clinic001.com";
    private static final String DOCTOR_PASS        = "Doctor@1234";

    private static final String RECEPT_EMAIL       = "receptionist@clinic001.com";
    private static final String RECEPT_PASS        = "Recept@1234";

    private static final String PATHOLOGIST_EMAIL  = "pathologist@clinic001.com";
    private static final String PATHOLOGIST_PASS   = "Path@1234";

    private static final String TECHNICIAN_EMAIL   = "technician@clinic001.com";
    private static final String TECHNICIAN_PASS    = "Tech@1234";

    // ── Injected dependencies ────────────────────────────────────────────────────

    // DataSource here is the raw HikariCP pool — NOT TenantAwareDataSource.
    private final DataSource                 dataSource;
    private final JdbcTemplate               jdbc;
    private final TenantMigrationService     tenantMigrationService;
    private final UserRepository             userRepository;
    private final PasswordEncoder            passwordEncoder;
    private final EmployeeRepository         employeeRepository;
    private final HrDepartmentRepository     hrDepartmentRepository;
    private final DesignationRepository      designationRepository;
    private final PatientRepository          patientRepository;
    private final LabTestCategoryRepository  labTestCategoryRepository;
    private final LabTestRepository          labTestRepository;
    private final MedicineCategoryRepository medicineCategoryRepository;
    private final MedicineRepository         medicineRepository;

    public ClinicDevDataSeeder(DataSource                 dataSource,
                               TenantMigrationService     tenantMigrationService,
                               UserRepository             userRepository,
                               PasswordEncoder            passwordEncoder,
                               EmployeeRepository         employeeRepository,
                               HrDepartmentRepository     hrDepartmentRepository,
                               DesignationRepository      designationRepository,
                               PatientRepository          patientRepository,
                               LabTestCategoryRepository  labTestCategoryRepository,
                               LabTestRepository          labTestRepository,
                               MedicineCategoryRepository medicineCategoryRepository,
                               MedicineRepository         medicineRepository) {
        this.dataSource                 = dataSource;
        this.jdbc                       = new JdbcTemplate(dataSource);
        this.tenantMigrationService     = tenantMigrationService;
        this.userRepository             = userRepository;
        this.passwordEncoder            = passwordEncoder;
        this.employeeRepository         = employeeRepository;
        this.hrDepartmentRepository     = hrDepartmentRepository;
        this.designationRepository      = designationRepository;
        this.patientRepository          = patientRepository;
        this.labTestCategoryRepository  = labTestCategoryRepository;
        this.labTestRepository          = labTestRepository;
        this.medicineCategoryRepository = medicineCategoryRepository;
        this.medicineRepository         = medicineRepository;
    }

    @PostConstruct
    public void seed() {
        try {
            provisionTenant();
        } catch (Exception e) {
            log.error("[ClinicSeeder] Tenant provisioning failed — {}", e.getMessage(), e);
            return;
        }

        seedDepartments();
        seedUsers();
        seedPatients();
        seedLabData();
        seedPharmacyData();
    }

    // ── HR departments + designations (clinic_001 schema) ────────────────────
    //
    // Without this, the Department dropdown in HR > Add Employee and in
    // OPD > Register Visit has no options ("No data"), which makes it impossible
    // to assign a specialty (e.g. Gynaecology, Orthopaedics) to any doctor.

    private void seedDepartments() {
        TenantContext.set(TENANT_SCHEMA);
        try {
            if (hrDepartmentRepository.count() > 0) {
                log.info("[ClinicSeeder] Departments already exist — skipping.");
                return;
            }
            record D(String name, String code) {}
            var rows = java.util.List.of(
                    new D("General Medicine", "GM"),
                    new D("Surgery",          "SRG"),
                    new D("Orthopaedics",     "ORT"),
                    new D("Gynaecology",      "GYN"),
                    new D("ENT",              "ENT")
            );
            for (var r : rows) {
                HrDepartment dept = hrDepartmentRepository.save(
                        HrDepartment.builder().name(r.name()).code(r.code()).active(true).build());
                designationRepository.save(Designation.builder()
                        .title("Consultant").departmentId(dept.getId()).active(true).build());
                designationRepository.save(Designation.builder()
                        .title("Senior Resident").departmentId(dept.getId()).active(true).build());
            }
            for (String title : java.util.List.of("Head Nurse", "Staff Nurse", "Pharmacist",
                    "Receptionist", "Accounts Officer", "Lab Technician")) {
                designationRepository.save(Designation.builder().title(title).active(true).build());
            }
            log.info("[ClinicSeeder] {} departments and designations seeded.", rows.size());
        } finally {
            TenantContext.clear();
        }
    }

    // ── Step 1-3: Tenant self-provisioning ───────────────────────────────────────

    private void provisionTenant() throws Exception {
        jdbc.update(
            "INSERT INTO public.tenants (name, schema_name, plan, status, clinic_type) " +
            "VALUES (?, ?, 'BASIC', 'ACTIVE', ?) " +
            "ON CONFLICT (schema_name) DO NOTHING",
            TENANT_NAME, TENANT_SCHEMA, CLINIC_TYPE);
        log.info("[ClinicSeeder] '{}' registered in public.tenants.", TENANT_SCHEMA);

        try (Connection conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            conn.setAutoCommit(true);
            stmt.execute("CREATE SCHEMA IF NOT EXISTS " + TENANT_SCHEMA);
            log.info("[ClinicSeeder] PostgreSQL schema '{}' ensured.", TENANT_SCHEMA);
        }

        tenantMigrationService.migrateTenantSchema(TENANT_SCHEMA);
    }

    // ── Users + Doctor employee record ───────────────────────────────────────────

    private void seedUsers() {
        TenantContext.set(TENANT_SCHEMA);
        try {
            seedUser(ADMIN_EMAIL,       ADMIN_PASS,       Role.ADMIN,             "Clinic", "Admin");
            seedUser(RECEPT_EMAIL,      RECEPT_PASS,      Role.RECEPTIONIST,      "Demo",   "Receptionist");
            seedUser(PATHOLOGIST_EMAIL, PATHOLOGIST_PASS, Role.PATHOLOGIST,       "Demo",   "Pathologist");
            seedUser(TECHNICIAN_EMAIL,  TECHNICIAN_PASS,  Role.CLINIC_TECHNICIAN, "Demo",   "Technician");
            seedUser(DOCTOR_EMAIL,      DOCTOR_PASS,      Role.DOCTOR,            "Anil", "Sharma");
            seedDoctorEmployee();

            log.info("===========================================================");
            log.info("[ClinicSeeder] clinic_001 demo users ready.");
            log.info("  Tenant : {}", TENANT_SCHEMA);
            log.info("  {} / {} (ADMIN)", ADMIN_EMAIL, ADMIN_PASS);
            log.info("  {} / {} (DOCTOR)", DOCTOR_EMAIL, DOCTOR_PASS);
            log.info("  {} / {} (RECEPTIONIST)", RECEPT_EMAIL, RECEPT_PASS);
            log.info("  {} / {} (PATHOLOGIST)", PATHOLOGIST_EMAIL, PATHOLOGIST_PASS);
            log.info("  {} / {} (CLINIC_TECHNICIAN)", TECHNICIAN_EMAIL, TECHNICIAN_PASS);
            log.info("===========================================================");
        } finally {
            TenantContext.clear();
        }
    }

    private void seedUser(String email, String rawPassword, Role role,
                          String firstName, String lastName) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            log.info("[ClinicSeeder] {} already exists — skipping.", email);
            return;
        }
        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .firstName(firstName)
                .lastName(lastName)
                .tenantId(TENANT_SCHEMA)
                .role(role)
                .active(true)
                .build();
        userRepository.save(user);
        log.info("[ClinicSeeder] Created {} ({}).", email, role);
    }

    private void seedDoctorEmployee() {
        if (employeeRepository.existsByEmailIgnoreCase(DOCTOR_EMAIL)) {
            log.info("[ClinicSeeder] Doctor employee record already exists — skipping.");
            return;
        }
        // NOTE: firstName intentionally excludes the "Dr." title — the OPD
        // Consulting Doctor picker prepends "Dr." itself when rendering, and
        // storing the title in the name field caused a duplicated "Dr. Dr." prefix.
        Employee.Builder builder = Employee.builder()
                .employeeCode("EMP-2026-001")
                .firstName("Anil")
                .lastName("Sharma")
                .email(DOCTOR_EMAIL)
                .mobile("9000000001")
                .gender(Employee.Gender.MALE)
                .joinDate(LocalDate.of(2026, 1, 1));

        hrDepartmentRepository.findByName("General Medicine").ifPresent(dept -> {
            builder.departmentId(dept.getId());
            designationRepository.findByTitleAndDepartmentId("Consultant", dept.getId())
                    .ifPresent(desig -> builder.designationId(desig.getId()));
        });

        employeeRepository.save(builder.build());
        log.info("[ClinicSeeder] Doctor employee record created.");
    }

    // ── Sample patients ───────────────────────────────────────────────────────────

    private void seedPatients() {
        TenantContext.set(TENANT_SCHEMA);
        try {
            seedPatient("Rahul",   "Sharma",   "9876543210", LocalDate.of(1990, 5, 15),  Patient.Gender.MALE);
            seedPatient("Priya",   "Verma",    "9876543211", LocalDate.of(1985, 8, 22),  Patient.Gender.FEMALE);
            seedPatient("Amit",    "Kumar",    "9876543212", LocalDate.of(2000, 3, 10),  Patient.Gender.MALE);
            seedPatient("Sunita",  "LNU",      "9876543213", LocalDate.of(1975, 11, 30), Patient.Gender.FEMALE);
            seedPatient("Ravi",    "Patel",    "9876543214", LocalDate.of(1965, 7, 4),   Patient.Gender.MALE);
            log.info("[ClinicSeeder] Sample patients seeded.");
        } finally {
            TenantContext.clear();
        }
    }

    private void seedPatient(String firstName, String lastName, String mobile,
                             LocalDate dob, Patient.Gender gender) {
        if (patientRepository.existsByMobile(mobile)) {
            return;
        }
        patientRepository.save(
            Patient.builder()
                .firstName(firstName)
                .lastName(lastName)
                .mobile(mobile)
                .dateOfBirth(dob)
                .gender(gender)
                .build()
        );
    }

    // ── Lab test categories + tests ───────────────────────────────────────────────

    private void seedLabData() {
        TenantContext.set(TENANT_SCHEMA);
        try {
            LabTestCategory blood  = seedCategory("Blood Tests",       "Haematology and biochemistry panels");
            LabTestCategory urine  = seedCategory("Urine Tests",       "Urinalysis and culture panels");
            LabTestCategory thyroid = seedCategory("Thyroid Profile",  "Thyroid function tests");

            // Blood Tests
            seedLabTest("CBC",    "Complete Blood Count",    blood.getId(),  200, 4,  "Report",  "See Report");
            seedLabTest("BSF",    "Blood Sugar Fasting",     blood.getId(),   80, 2,  "mg/dL",   "70–100 mg/dL");
            seedLabTest("BSR",    "Blood Sugar Random",      blood.getId(),   80, 2,  "mg/dL",   "< 140 mg/dL");
            seedLabTest("HBA1C",  "HbA1c (Glycated Hb)",    blood.getId(),  350, 24, "%",        "< 5.7%");
            seedLabTest("LFT",    "Liver Function Test",     blood.getId(),  500, 24, "Report",  "See Report");
            seedLabTest("KFT",    "Kidney Function Test",    blood.getId(),  500, 24, "Report",  "See Report");

            // Urine Tests
            seedLabTest("URE",    "Urine Routine Exam",      urine.getId(),   60, 4,  "Report",  "See Report");
            seedLabTest("UCR",    "Urine Culture & Sensitivity", urine.getId(), 400, 48, "Report", "No Growth");

            // Thyroid
            seedLabTest("TSH",    "TSH (Thyroid Stimulating Hormone)", thyroid.getId(), 300, 24, "mIU/L", "0.4–4.0 mIU/L");
            seedLabTest("T3T4",   "T3, T4, TSH Panel",       thyroid.getId(), 700, 24, "Report",  "See Report");

            log.info("[ClinicSeeder] Lab test categories and tests seeded.");
        } finally {
            TenantContext.clear();
        }
    }

    private LabTestCategory seedCategory(String name, String description) {
        if (labTestCategoryRepository.existsByNameIgnoreCase(name)) {
            return labTestCategoryRepository.findByActiveTrue().stream()
                    .filter(c -> c.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElseThrow();
        }
        return labTestCategoryRepository.save(
            LabTestCategory.builder().name(name).description(description).build()
        );
    }

    private void seedLabTest(String code, String name, java.util.UUID categoryId,
                             int price, int turnaroundHours, String unit, String normalRange) {
        if (labTestRepository.existsByCodeIgnoreCase(code)) {
            return;
        }
        labTestRepository.save(
            LabTest.builder()
                .code(code)
                .name(name)
                .categoryId(categoryId)
                .price(BigDecimal.valueOf(price))
                .turnaroundHours(turnaroundHours)
                .unit(unit)
                .normalRange(normalRange)
                .build()
        );
    }

    // ── Medicine categories + medicines ───────────────────────────────────────────

    private void seedPharmacyData() {
        TenantContext.set(TENANT_SCHEMA);
        try {
            MedicineCategory tablet  = seedMedicineCategory("Tablets");
            MedicineCategory syrup   = seedMedicineCategory("Syrups");
            MedicineCategory injection = seedMedicineCategory("Injections");

            seedMedicine(tablet,    "Paracetamol 500mg",    "Paracetamol",    "TAB", 100);
            seedMedicine(tablet,    "Amoxicillin 500mg",    "Amoxicillin",    "TAB",  50);
            seedMedicine(tablet,    "Metformin 500mg",      "Metformin",      "TAB",  50);
            seedMedicine(tablet,    "Atorvastatin 10mg",    "Atorvastatin",   "TAB",  30);
            seedMedicine(tablet,    "Pantoprazole 40mg",    "Pantoprazole",   "TAB",  50);
            seedMedicine(syrup,     "Cough Syrup 100ml",    "Dextromethorphan","ML",  20);
            seedMedicine(syrup,     "Antacid Suspension",   "Aluminium Hydroxide","ML", 20);
            seedMedicine(injection, "Normal Saline 500ml",  "Sodium Chloride","VIAL", 10);
            seedMedicine(injection, "Dextrose 5% 500ml",    "Dextrose",       "VIAL", 10);

            log.info("[ClinicSeeder] Pharmacy medicines seeded.");
        } finally {
            TenantContext.clear();
        }
    }

    private MedicineCategory seedMedicineCategory(String name) {
        return medicineCategoryRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> medicineCategoryRepository.save(new MedicineCategory(name)));
    }

    private void seedMedicine(MedicineCategory category, String name,
                              String genericName, String unit, int reorderLevel) {
        if (medicineRepository.existsByNameIgnoreCaseAndCategoryId(name, category.getId())) {
            return;
        }
        medicineRepository.save(
            Medicine.builder()
                .category(category)
                .name(name)
                .genericName(genericName)
                .unit(unit)
                .reorderLevel(reorderLevel)
                .build()
        );
    }
}
