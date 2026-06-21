export type Role =
  | 'SUPER_ADMIN' | 'ADMIN' | 'DOCTOR' | 'NURSE'
  | 'PHARMACIST' | 'RECEPTIONIST' | 'ACCOUNTANT' | 'PATHOLOGIST' | 'PATIENT'

export interface UserProfile {
  id: string
  email: string
  firstName: string
  lastName: string
  role: Role
  tenantId: string
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
}

export interface LoginResponse {
  tokens: TokenResponse
  user: UserProfile
}
