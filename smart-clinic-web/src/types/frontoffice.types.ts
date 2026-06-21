export type AppointmentType   = 'CONSULTATION' | 'FOLLOW_UP' | 'EMERGENCY' | 'PROCEDURE'
export type AppointmentStatus = 'SCHEDULED' | 'CONFIRMED' | 'CHECKED_IN' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW'
export type TokenPriority     = 'NORMAL' | 'URGENT'
export type TokenStatus       = 'WAITING' | 'IN_PROGRESS' | 'COMPLETED' | 'SKIPPED'

export interface Appointment {
  id: string
  appointmentNumber: string
  patientId: string
  patientName: string
  patientMobile?: string
  doctorId?: string
  doctorName?: string
  department?: string
  appointmentDate: string
  timeSlot?: string
  appointmentType: AppointmentType
  status: AppointmentStatus
  notes?: string
  createdAt: string
}

export interface OpdToken {
  id: string
  tokenNumber: string
  patientId: string
  patientName: string
  patientMobile?: string
  department: string
  doctorId?: string
  doctorName?: string
  tokenDate: string
  priority: TokenPriority
  status: TokenStatus
  linkedAppointmentId?: string
  createdAt: string
}

export interface FrontOfficeDashboard {
  todayAppointments: number
  confirmedAppointments: number
  checkedInAppointments: number
  todayTokens: number
  waitingTokens: number
  inProgressTokens: number
  completedTokens: number
}

export interface BookAppointmentPayload {
  patientId: string
  doctorId?: string
  doctorName?: string
  department?: string
  appointmentDate: string
  timeSlot?: string
  appointmentType?: AppointmentType
  notes?: string
}

export interface IssueTokenPayload {
  patientId: string
  department: string
  doctorId?: string
  doctorName?: string
  priority?: TokenPriority
  linkedAppointmentId?: string
}
