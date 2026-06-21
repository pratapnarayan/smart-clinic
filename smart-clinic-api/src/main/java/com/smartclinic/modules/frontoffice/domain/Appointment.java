package com.smartclinic.modules.frontoffice.domain;

import com.smartclinic.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "appointments",
    indexes = {
        @Index(name = "idx_apt_patient_id",       columnList = "patient_id"),
        @Index(name = "idx_apt_appointment_date", columnList = "appointment_date"),
        @Index(name = "idx_apt_doctor_id",        columnList = "doctor_id")
    }
)
public class Appointment extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "appointment_number", nullable = false, unique = true, length = 30)
    private String appointmentNumber;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "patient_name", nullable = false, length = 200)
    private String patientName;

    @Column(name = "patient_mobile", length = 15)
    private String patientMobile;

    @Column(name = "doctor_id")
    private UUID doctorId;

    @Column(name = "doctor_name", length = 200)
    private String doctorName;

    @Column(length = 100)
    private String department;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "time_slot", length = 20)
    private String timeSlot;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", nullable = false, length = 30)
    private AppointmentType appointmentType = AppointmentType.CONSULTATION;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public enum AppointmentType   { CONSULTATION, FOLLOW_UP, EMERGENCY, PROCEDURE }
    public enum AppointmentStatus { SCHEDULED, CONFIRMED, CHECKED_IN, COMPLETED, CANCELLED, NO_SHOW }

    protected Appointment() {}

    public UUID              getId()                { return id; }
    public String            getAppointmentNumber() { return appointmentNumber; }
    public UUID              getPatientId()         { return patientId; }
    public String            getPatientName()       { return patientName; }
    public String            getPatientMobile()     { return patientMobile; }
    public UUID              getDoctorId()          { return doctorId; }
    public String            getDoctorName()        { return doctorName; }
    public String            getDepartment()        { return department; }
    public LocalDate         getAppointmentDate()   { return appointmentDate; }
    public String            getTimeSlot()          { return timeSlot; }
    public AppointmentType   getAppointmentType()   { return appointmentType; }
    public AppointmentStatus getStatus()            { return status; }
    public String            getNotes()             { return notes; }

    public void setDoctorId(UUID v)              { this.doctorId        = v; }
    public void setDoctorName(String v)          { this.doctorName      = v; }
    public void setDepartment(String v)          { this.department      = v; }
    public void setAppointmentDate(LocalDate v)  { this.appointmentDate = v; }
    public void setTimeSlot(String v)            { this.timeSlot        = v; }
    public void setAppointmentType(AppointmentType v){ this.appointmentType = v; }
    public void setStatus(AppointmentStatus v)   { this.status          = v; }
    public void setNotes(String v)               { this.notes           = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final Appointment a = new Appointment();
        public Builder appointmentNumber(String v)      { a.appointmentNumber = v; return this; }
        public Builder patientId(UUID v)                { a.patientId         = v; return this; }
        public Builder patientName(String v)            { a.patientName       = v; return this; }
        public Builder patientMobile(String v)          { a.patientMobile     = v; return this; }
        public Builder doctorId(UUID v)                 { a.doctorId          = v; return this; }
        public Builder doctorName(String v)             { a.doctorName        = v; return this; }
        public Builder department(String v)             { a.department        = v; return this; }
        public Builder appointmentDate(LocalDate v)     { a.appointmentDate   = v; return this; }
        public Builder timeSlot(String v)               { a.timeSlot          = v; return this; }
        public Builder appointmentType(AppointmentType v){ a.appointmentType  = v; return this; }
        public Builder notes(String v)                  { a.notes             = v; return this; }
        public Appointment build()                      { return a; }
    }
}
