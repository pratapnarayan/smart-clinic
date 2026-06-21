import { apiClient } from './client'
import type { ApiResponse, PageResponse } from '@/types'
import type {
  BloodDonor, BloodUnit, BloodRequest, BloodIssue, BloodBankDashboard,
  BloodGroup, ComponentType, UnitStatus, RequestStatus,
  RegisterDonorPayload, AddBloodUnitPayload, CreateBloodRequestPayload, IssueBloodPayload,
} from '@/types'

export const bloodBankApi = {
  getDashboard: () =>
    apiClient.get<ApiResponse<BloodBankDashboard>>('/v1/bloodbank/dashboard'),

  // Donors
  registerDonor: (payload: RegisterDonorPayload) =>
    apiClient.post<ApiResponse<BloodDonor>>('/v1/bloodbank/donors', payload),

  getDonor: (id: string) =>
    apiClient.get<ApiResponse<BloodDonor>>(`/v1/bloodbank/donors/${id}`),

  listDonors: (params?: { q?: string; bloodGroup?: BloodGroup; page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<BloodDonor>>>('/v1/bloodbank/donors', { params }),

  // Units
  addUnit: (payload: AddBloodUnitPayload) =>
    apiClient.post<ApiResponse<BloodUnit>>('/v1/bloodbank/units', payload),

  getUnit: (id: string) =>
    apiClient.get<ApiResponse<BloodUnit>>(`/v1/bloodbank/units/${id}`),

  listUnits: (params?: { bloodGroup?: BloodGroup; componentType?: ComponentType; status?: UnitStatus; page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<BloodUnit>>>('/v1/bloodbank/units', { params }),

  updateUnitStatus: (id: string, payload: { status: UnitStatus; notes?: string }) =>
    apiClient.patch<ApiResponse<BloodUnit>>(`/v1/bloodbank/units/${id}/status`, payload),

  getAvailableUnits: (bloodGroup: BloodGroup, componentType: ComponentType) =>
    apiClient.get<ApiResponse<BloodUnit[]>>('/v1/bloodbank/units/available', {
      params: { bloodGroup, componentType },
    }),

  // Requests
  createRequest: (payload: CreateBloodRequestPayload) =>
    apiClient.post<ApiResponse<BloodRequest>>('/v1/bloodbank/requests', payload),

  getRequest: (id: string) =>
    apiClient.get<ApiResponse<BloodRequest>>(`/v1/bloodbank/requests/${id}`),

  listRequests: (params?: { status?: RequestStatus; page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<BloodRequest>>>('/v1/bloodbank/requests', { params }),

  cancelRequest: (id: string) =>
    apiClient.delete<ApiResponse<BloodRequest>>(`/v1/bloodbank/requests/${id}`),

  getRequestIssues: (id: string) =>
    apiClient.get<ApiResponse<BloodIssue[]>>(`/v1/bloodbank/requests/${id}/issues`),

  // Issues
  issueBlood: (payload: IssueBloodPayload) =>
    apiClient.post<ApiResponse<BloodIssue>>('/v1/bloodbank/issues', payload),

  listIssues: (params?: { page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<BloodIssue>>>('/v1/bloodbank/issues', { params }),
}
