import { apiClient } from './client'
import type { ApiResponse, LoginResponse } from '@/types'

export interface LoginPayload {
  email: string
  password: string
  tenantId?: string
}

export const authApi = {
  login: (payload: LoginPayload) =>
    apiClient.post<ApiResponse<LoginResponse>>('/v1/auth/login', payload),

  refresh: (refreshToken: string) =>
    apiClient.post<ApiResponse<{ accessToken: string; refreshToken: string }>>(
      '/v1/auth/refresh',
      null,
      { params: { refreshToken } }
    ),

  logout: () => apiClient.post('/v1/auth/logout'),

  me: () => apiClient.get('/v1/auth/me'),
}
