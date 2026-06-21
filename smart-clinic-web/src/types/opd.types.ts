export type VisitStatus   = 'REGISTERED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED'
export type PaymentStatus = 'PENDING' | 'PAID' | 'PARTIAL' | 'WAIVED'

export interface OpdCharge {
  id: string
  description: string
  amount: number
  category: string | null
}

export interface PrescriptionItem {
  id: string
  medicineName: string
  dose: string
  frequency: string
  duration: string
  instructions: string | null
}

export interface Prescription {
  id: string
  advice: string | null
  followUpDays: number | null
  items: PrescriptionItem[]
  createdAt: string
}

export interface OpdVisit {
  id: string
  visitNumber: string
  patientId: string
  patientName: string
  visitDate: string
  department: string | null
  doctorId: string | null
  doctorName: string | null
  symptoms: string | null
  diagnosis: string | null
  notes: string | null
  consultationFee: number
  totalCharges: number
  discount: number
  netAmount: number
  paymentStatus: PaymentStatus
  visitStatus: VisitStatus
  charges: OpdCharge[]
  prescription: Prescription | null
  createdAt: string
}

export interface OpdVisitCreateRequest {
  patientId: string
  visitDate?: string
  department?: string
  doctorName?: string
  symptoms?: string
  consultationFee: number
  charges?: { description: string; amount: number; category?: string }[]
}

export interface PrescriptionRequest {
  advice?: string
  followUpDays?: number
  items: { medicineName: string; dose: string; frequency: string; duration: string; instructions?: string }[]
}
