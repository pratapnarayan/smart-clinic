export type LabOrderSourceType = 'OPD' | 'IPD' | 'WALK_IN'
export type LabOrderPriority   = 'ROUTINE' | 'URGENT' | 'STAT'
export type LabOrderStatus     = 'PENDING' | 'SAMPLE_COLLECTED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED'
export type LabItemStatus      = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED'
export type LabPaymentStatus   = 'PENDING' | 'PAID' | 'PARTIAL' | 'WAIVED'

export interface LabCategory {
  id: string
  name: string
  description?: string
  active: boolean
}

export interface LabTest {
  id: string
  code: string
  name: string
  categoryId: string
  description?: string
  price: number
  turnaroundHours: number
  unit?: string
  normalRange?: string
  active: boolean
}

export interface LabOrderItem {
  id: string
  testId: string
  testCode: string
  testName: string
  price: number
  unit?: string
  normalRange?: string
  status: LabItemStatus
  result?: string
  resultNote?: string
  resultEnteredAt?: string
  resultEnteredBy?: string
}

export interface LabOrder {
  id: string
  orderNumber: string
  patientId: string
  patientName: string
  patientMobile?: string
  referredById?: string
  referredByName?: string
  sourceType: LabOrderSourceType
  sourceId?: string
  priority: LabOrderPriority
  status: LabOrderStatus
  sampleCollectedAt?: string
  totalAmount: number
  discount: number
  netAmount: number
  paymentStatus: LabPaymentStatus
  notes?: string
  items: LabOrderItem[]
  createdAt: string
}

export interface PathologyDashboard {
  pendingOrders: number
  sampleCollected: number
  inProgressOrders: number
  completedToday: number
  totalTests: number
}

export interface CreateLabOrderPayload {
  patientId: string
  referredById?: string
  referredByName?: string
  sourceType?: LabOrderSourceType
  sourceId?: string
  priority?: LabOrderPriority
  notes?: string
  testIds: string[]
}

export interface EnterResultPayload {
  result: string
  resultNote?: string
  enteredBy?: string
}
