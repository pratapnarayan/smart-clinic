import { apiClient } from './client'
import type { ApiResponse, PageResponse } from '@/types'
import type {
  Ward, Bed, IpdAdmission, IpdDashboard,
  AdmitPatientPayload, DischargePatientPayload, AddIpdChargePayload,
  BedStatus, AdmissionStatus,
} from '@/types'

export const ipdApi = {
  // ── Wards ──────────────────────────────────────────────────────────────────
  listWards: () =>
    apiClient.get<ApiResponse<Ward[]>>('/v1/ipd/wards'),

  createWard: (payload: { name: string; wardType: string; totalBeds: number }) =>
    apiClient.post<ApiResponse<Ward>>('/v1/ipd/wards', payload),

  // ── Beds ───────────────────────────────────────────────────────────────────
  listBeds: (wardId: string) =>
    apiClient.get<ApiResponse<Bed[]>>(`/v1/ipd/wards/${wardId}/beds`),

  listAvailableBeds: (wardId: string) =>
    apiClient.get<ApiResponse<Bed[]>>(`/v1/ipd/wards/${wardId}/beds/available`),

  addBed: (wardId: string, payload: { bedNumber: string; bedType: string; dailyCharge?: number }) =>
    apiClient.post<ApiResponse<Bed>>(`/v1/ipd/wards/${wardId}/beds`, payload),

  updateBedStatus: (bedId: string, status: BedStatus) =>
    apiClient.patch<ApiResponse<Bed>>(`/v1/ipd/beds/${bedId}/status`, null, { params: { status } }),

  // ── Admissions ─────────────────────────────────────────────────────────────
  admitPatient: (payload: AdmitPatientPayload) =>
    apiClient.post<ApiResponse<IpdAdmission>>('/v1/ipd/admissions', payload),

  getAdmission: (id: string) =>
    apiClient.get<ApiResponse<IpdAdmission>>(`/v1/ipd/admissions/${id}`),

  listAdmissions: (params: { status?: AdmissionStatus; page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<IpdAdmission>>>('/v1/ipd/admissions', { params }),

  listByPatient: (patientId: string, params?: { page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<IpdAdmission>>>(`/v1/ipd/admissions/patient/${patientId}`, { params }),

  updateAdmission: (id: string, payload: Partial<AdmitPatientPayload>) =>
    apiClient.patch<ApiResponse<IpdAdmission>>(`/v1/ipd/admissions/${id}`, payload),

  discharge: (id: string, payload: DischargePatientPayload) =>
    apiClient.post<ApiResponse<IpdAdmission>>(`/v1/ipd/admissions/${id}/discharge`, payload),

  addCharge: (admissionId: string, payload: AddIpdChargePayload) =>
    apiClient.post<ApiResponse<IpdAdmission>>(`/v1/ipd/admissions/${admissionId}/charges`, payload),

  // ── Dashboard ──────────────────────────────────────────────────────────────
  getDashboard: () =>
    apiClient.get<ApiResponse<IpdDashboard>>('/v1/ipd/dashboard'),
}
