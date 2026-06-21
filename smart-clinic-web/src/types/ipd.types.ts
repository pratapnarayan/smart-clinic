export type WardType = 'GENERAL' | 'ICU' | 'NICU' | 'MATERNITY' | 'SURGERY' | 'PEDIATRIC' | 'ORTHOPEDIC' | 'PRIVATE'
export type BedType = 'GENERAL' | 'PRIVATE' | 'ICU' | 'SEMI_PRIVATE'
export type BedStatus = 'AVAILABLE' | 'OCCUPIED' | 'MAINTENANCE'
export type AdmissionStatus = 'ADMITTED' | 'TRANSFERRED' | 'DISCHARGED' | 'DECEASED'
export type DischargeCondition = 'STABLE' | 'IMPROVED' | 'CRITICAL' | 'UNCHANGED' | 'DECEASED'
export type IpdPaymentStatus = 'PENDING' | 'PAID' | 'PARTIAL' | 'WAIVED'
export type ChargeCategory = 'BED_CHARGE' | 'NURSING' | 'DOCTOR_VISIT' | 'PROCEDURE' | 'MEDICINE' | 'OTHER'

export interface Ward {
  id: string
  name: string
  wardType: WardType
  totalBeds: number
  active: boolean
}

export interface Bed {
  id: string
  wardId: string
  bedNumber: string
  bedType: BedType
  dailyCharge: number
  status: BedStatus
}

export interface IpdCharge {
  id: string
  category: ChargeCategory
  description: string
  amount: number
  chargeDate: string
  createdAt: string
}

export interface IpdAdmission {
  id: string
  admissionNumber: string
  patientId: string
  patientName: string
  opdVisitId?: string
  admissionDate: string
  wardId: string
  bedId: string
  doctorId?: string
  doctorName?: string
  admissionDiagnosis?: string
  notes?: string
  status: AdmissionStatus
  dischargeDate?: string
  finalDiagnosis?: string
  conditionAtDischarge?: DischargeCondition
  dischargeNotes?: string
  followUpInstructions?: string
  totalCharges: number
  discount: number
  netAmount: number
  paymentStatus: IpdPaymentStatus
  charges: IpdCharge[]
  createdAt: string
}

export interface IpdDashboard {
  totalAdmitted: number
  totalDischarged: number
  totalBeds: number
  availableBeds: number
  occupiedBeds: number
}

// ── Request payloads ─────────────────────────────────────────────────────────

export interface AdmitPatientPayload {
  patientId: string
  opdVisitId?: string
  wardId: string
  bedId: string
  doctorId?: string
  doctorName?: string
  admissionDiagnosis?: string
  notes?: string
}

export interface DischargePatientPayload {
  conditionAtDischarge: DischargeCondition
  finalDiagnosis?: string
  dischargeNotes?: string
  followUpInstructions?: string
}

export interface AddIpdChargePayload {
  category: ChargeCategory
  description: string
  amount: number
  chargeDate?: string
}
