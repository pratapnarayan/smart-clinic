import { apiClient } from './client'
import type {
  ApiResponse, PageResponse, OpdVisit, OpdVisitCreateRequest, Prescription, PrescriptionRequest
} from '@/types'

export const opdApi = {
  createVisit: (body: OpdVisitCreateRequest) =>
    apiClient.post<ApiResponse<OpdVisit>>('/v1/opd/visits', body),

  getVisit: (id: string) =>
    apiClient.get<ApiResponse<OpdVisit>>(`/v1/opd/visits/${id}`),

  listByDate: (params?: { date?: string; page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<OpdVisit>>>('/v1/opd/visits', { params }),

  listByPatient: (patientId: string, params?: { page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<OpdVisit>>>(`/v1/opd/visits/patient/${patientId}`, { params }),

  updateVisit: (id: string, body: Partial<OpdVisit>) =>
    apiClient.patch<ApiResponse<OpdVisit>>(`/v1/opd/visits/${id}`, body),

  cancelVisit: (id: string) =>
    apiClient.delete(`/v1/opd/visits/${id}`),

  savePrescription: (visitId: string, body: PrescriptionRequest) =>
    apiClient.put<ApiResponse<Prescription>>(`/v1/opd/visits/${visitId}/prescription`, body),

  getPrescription: (visitId: string) =>
    apiClient.get<ApiResponse<Prescription>>(`/v1/opd/visits/${visitId}/prescription`),

  getBill: (visitId: string) =>
    apiClient.get<ApiResponse<OpdVisit>>(`/v1/opd/visits/${visitId}/bill`),

  getTodaysQueue: () =>
    apiClient.get<ApiResponse<OpdVisit[]>>('/v1/opd/visits/queue'),
}
