export type TheatreType      = 'GENERAL' | 'CARDIAC' | 'NEURO' | 'ORTHO' | 'PAEDIATRIC' | 'EMERGENCY' | 'DIAGNOSTIC'
export type OperationType    = 'ELECTIVE' | 'EMERGENCY' | 'DIAGNOSTIC'
export type OtPriority       = 'ROUTINE' | 'URGENT' | 'EMERGENCY'
export type OtStatus         = 'SCHEDULED' | 'IN_PROGRESS' | 'COMPLETED' | 'POSTPONED' | 'CANCELLED'
export type AnesthesiaType   = 'GENERAL' | 'SPINAL' | 'EPIDURAL' | 'LOCAL' | 'REGIONAL'
export type OtOutcome        = 'SUCCESSFUL' | 'COMPLICATED' | 'INCOMPLETE'
export type PatientCondition = 'STABLE' | 'CRITICAL' | 'DECEASED'

export interface OperationTheatre {
  id: string
  theatreNumber: string
  name: string
  type: TheatreType
  active: boolean
}

export interface OtConsumable {
  id: string
  itemId: string
  itemName: string
  itemUnit: string
  quantityUsed: number
}

export interface OtSchedule {
  id: string
  scheduleNumber: string
  admissionId?: string
  patientId?: string
  patientName: string
  theatreId: string
  theatreName: string
  scheduledDate: string
  scheduledStart: string
  estimatedDurationMins: number
  procedureName: string
  operationType: OperationType
  priority: OtPriority
  status: OtStatus
  surgeonId?: string
  surgeonName?: string
  anesthetistId?: string
  anesthetistName?: string
  assistantNames?: string
  preOpDiagnosis?: string
  bloodRequestId?: string
  bloodRequestNumber?: string
  notes?: string
  // Post-op (null until completed)
  actualStart?: string
  actualEnd?: string
  anesthesiaType?: AnesthesiaType
  postOpDiagnosis?: string
  procedureDetails?: string
  complications?: string
  surgeonNotes?: string
  outcome?: OtOutcome
  patientConditionAfter?: PatientCondition
  consumables: OtConsumable[]
  createdAt: string
}

export interface OtTheatreUtilization {
  theatreName: string
  operationsThisMonth: number
}

export interface OtDashboard {
  todayScheduled: number
  todayInProgress: number
  todayCompleted: number
  monthTotal: number
  todaySchedules: OtSchedule[]
  theatreUtilization: OtTheatreUtilization[]
}

export interface CreateTheatrePayload {
  theatreNumber: string
  name: string
  type: TheatreType
}

export interface ScheduleOperationPayload {
  admissionId?: string
  patientId?: string
  patientName?: string
  theatreId: string
  scheduledDate: string
  scheduledStart: string
  estimatedDurationMins: number
  procedureName: string
  operationType?: OperationType
  priority?: OtPriority
  surgeonId?: string
  surgeonName?: string
  anesthetistId?: string
  anesthetistName?: string
  assistantNames?: string
  preOpDiagnosis?: string
  bloodRequestId?: string
  bloodRequestNumber?: string
  notes?: string
}

export interface CompleteOperationPayload {
  actualStart: string
  actualEnd: string
  anesthesiaType?: AnesthesiaType
  postOpDiagnosis?: string
  procedureDetails?: string
  complications?: string
  surgeonNotes?: string
  outcome: OtOutcome
  patientConditionAfter: PatientCondition
  consumables?: { itemId: string; quantityUsed: number }[]
}
