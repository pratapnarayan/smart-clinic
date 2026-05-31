export type BloodGroup      = 'A_POS' | 'A_NEG' | 'B_POS' | 'B_NEG' | 'AB_POS' | 'AB_NEG' | 'O_POS' | 'O_NEG'
export type ComponentType   = 'WHOLE_BLOOD' | 'PACKED_CELLS' | 'FRESH_FROZEN_PLASMA' | 'PLATELET_CONCENTRATE'
export type UnitStatus      = 'PENDING_TESTING' | 'AVAILABLE' | 'RESERVED' | 'ISSUED' | 'DISCARDED' | 'EXPIRED'
export type TestingStatus   = 'PENDING' | 'CLEARED' | 'REJECTED'
export type RequestStatus   = 'PENDING' | 'PARTIALLY_FULFILLED' | 'FULFILLED' | 'CANCELLED'
export type RequestUrgency  = 'ROUTINE' | 'URGENT' | 'EMERGENCY'
export type DonorGender     = 'MALE' | 'FEMALE' | 'OTHER'

export const BLOOD_GROUP_LABELS: Record<BloodGroup, string> = {
  A_POS: 'A+', A_NEG: 'A-', B_POS: 'B+', B_NEG: 'B-',
  AB_POS: 'AB+', AB_NEG: 'AB-', O_POS: 'O+', O_NEG: 'O-',
}

export const BLOOD_GROUP_OPTIONS = (Object.keys(BLOOD_GROUP_LABELS) as BloodGroup[]).map(k => ({
  value: k, label: BLOOD_GROUP_LABELS[k],
}))

export const COMPONENT_LABELS: Record<ComponentType, string> = {
  WHOLE_BLOOD: 'Whole Blood',
  PACKED_CELLS: 'Packed Cells',
  FRESH_FROZEN_PLASMA: 'Fresh Frozen Plasma',
  PLATELET_CONCENTRATE: 'Platelet Concentrate',
}

export const COMPONENT_OPTIONS = (Object.keys(COMPONENT_LABELS) as ComponentType[]).map(k => ({
  value: k, label: COMPONENT_LABELS[k],
}))

export interface BloodDonor {
  id: string
  donorNumber: string
  firstName: string
  lastName: string
  gender: DonorGender
  dateOfBirth: string
  bloodGroup: BloodGroup
  bloodGroupDisplay: string
  mobile?: string
  email?: string
  lastDonationDate?: string
  totalDonations: number
  active: boolean
  createdAt: string
}

export interface BloodUnit {
  id: string
  unitNumber: string
  bloodGroup: BloodGroup
  bloodGroupDisplay: string
  donorId?: string
  donorName?: string
  componentType: ComponentType
  volumeMl: number
  collectionDate: string
  expiryDate: string
  testingStatus: TestingStatus
  status: UnitStatus
  expired: boolean
  notes?: string
  createdAt: string
}

export interface BloodRequest {
  id: string
  requestNumber: string
  requestDate: string
  patientId?: string
  patientName: string
  requestedBy?: string
  bloodGroup: BloodGroup
  bloodGroupDisplay: string
  componentType: ComponentType
  unitsRequired: number
  unitsIssued: number
  urgency: RequestUrgency
  status: RequestStatus
  requiredBy?: string
  notes?: string
  createdAt: string
}

export interface BloodIssue {
  id: string
  issueNumber: string
  issueDate: string
  requestId: string
  requestNumber: string
  unitId: string
  unitNumber: string
  bloodGroup: string
  componentType: string
  issuedTo: string
  issuedBy?: string
  notes?: string
  createdAt: string
}

export interface BloodGroupStock {
  bloodGroup: string
  display: string
  available: number
  pendingTesting: number
}

export interface BloodBankDashboard {
  totalDonors: number
  activeDonors: number
  totalAvailable: number
  pendingTesting: number
  openRequests: number
  todayIssues: number
  stockByGroup: BloodGroupStock[]
}

export interface RegisterDonorPayload {
  firstName: string
  lastName: string
  gender: DonorGender
  dateOfBirth: string
  bloodGroup: BloodGroup
  mobile?: string
  email?: string
  address?: string
}

export interface AddBloodUnitPayload {
  bloodGroup: BloodGroup
  donorId?: string
  donorName?: string
  componentType: ComponentType
  volumeMl?: number
  collectionDate?: string
  expiryDate?: string
  notes?: string
}

export interface CreateBloodRequestPayload {
  patientId?: string
  patientName: string
  requestedBy?: string
  bloodGroup: BloodGroup
  componentType: ComponentType
  unitsRequired: number
  urgency?: RequestUrgency
  requiredBy?: string
  notes?: string
}

export interface IssueBloodPayload {
  requestId: string
  unitId: string
  issuedBy?: string
  notes?: string
}
