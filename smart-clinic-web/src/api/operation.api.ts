import { apiClient } from './client'
import type { ApiResponse, PageResponse } from '@/types'
import type {
  OperationTheatre, OtSchedule, OtDashboard, OtStatus,
  CreateTheatrePayload, ScheduleOperationPayload, CompleteOperationPayload,
} from '@/types'

export const operationApi = {
  getDashboard: () =>
    apiClient.get<ApiResponse<OtDashboard>>('/v1/operation/dashboard'),

  // Theatres
  listTheatres: () =>
    apiClient.get<ApiResponse<OperationTheatre[]>>('/v1/operation/theatres'),

  createTheatre: (payload: CreateTheatrePayload) =>
    apiClient.post<ApiResponse<OperationTheatre>>('/v1/operation/theatres', payload),

  // Schedules
  scheduleOperation: (payload: ScheduleOperationPayload) =>
    apiClient.post<ApiResponse<OtSchedule>>('/v1/operation/schedules', payload),

  getSchedule: (id: string) =>
    apiClient.get<ApiResponse<OtSchedule>>(`/v1/operation/schedules/${id}`),

  listSchedules: (params?: { date?: string; status?: OtStatus; theatreId?: string; page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<OtSchedule>>>('/v1/operation/schedules', { params }),

  startOperation: (id: string) =>
    apiClient.post<ApiResponse<OtSchedule>>(`/v1/operation/schedules/${id}/start`),

  completeOperation: (id: string, payload: CompleteOperationPayload) =>
    apiClient.post<ApiResponse<OtSchedule>>(`/v1/operation/schedules/${id}/complete`, payload),

  postponeOperation: (id: string, notes?: string) =>
    apiClient.post<ApiResponse<OtSchedule>>(`/v1/operation/schedules/${id}/postpone`, { notes }),

  cancelOperation: (id: string, notes?: string) =>
    apiClient.delete<ApiResponse<OtSchedule>>(`/v1/operation/schedules/${id}`, { data: { notes } }),
}
