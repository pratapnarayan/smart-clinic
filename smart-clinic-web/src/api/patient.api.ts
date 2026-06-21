import { apiClient } from './client'
import type { ApiResponse, PageResponse, Patient, PatientCreateRequest } from '@/types'

export const patientApi = {
  list: (params?: { query?: string; page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<Patient>>>('/v1/patients', { params }),

  get: (id: string) =>
    apiClient.get<ApiResponse<Patient>>(`/v1/patients/${id}`),

  create: (body: PatientCreateRequest) =>
    apiClient.post<ApiResponse<Patient>>('/v1/patients', body),

  update: (id: string, body: PatientCreateRequest) =>
    apiClient.put<ApiResponse<Patient>>(`/v1/patients/${id}`, body),

  delete: (id: string) =>
    apiClient.delete(`/v1/patients/${id}`),
}
