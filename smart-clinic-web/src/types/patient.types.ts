export type Gender = 'MALE' | 'FEMALE' | 'OTHER'

export interface Patient {
  id: string
  firstName: string
  lastName: string
  dateOfBirth: string        // ISO date string
  gender: Gender
  mobile: string | null
  email: string | null
  address: string | null
  bloodGroup: string | null
  guardianName: string | null
  photoUrl: string | null
  createdAt: string
}

export interface PatientCreateRequest {
  firstName: string
  lastName: string
  dateOfBirth: string
  gender: Gender
  mobile?: string
  email?: string
  address?: string
  bloodGroup?: string
  guardianName?: string
  guardianMobile?: string
}
