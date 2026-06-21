import { apiClient } from './client'
import type { ApiResponse, PageResponse } from '@/types'
import type {
  LabCategory, LabTest, LabOrder, PathologyDashboard,
  CreateLabOrderPayload, EnterResultPayload,
  LabOrderStatus, LabPaymentStatus,
} from '@/types'

export const pathologyApi = {
  // ── Categories ─────────────────────────────────────────────────────────────
  listCategories: () =>
    apiClient.get<ApiResponse<LabCategory[]>>('/v1/pathology/categories'),

  createCategory: (payload: { name: string; description?: string }) =>
    apiClient.post<ApiResponse<LabCategory>>('/v1/pathology/categories', payload),

  // ── Tests ──────────────────────────────────────────────────────────────────
  listTests: (categoryId?: string) =>
    apiClient.get<ApiResponse<LabTest[]>>('/v1/pathology/tests',
      { params: categoryId ? { categoryId } : {} }),

  getTest: (id: string) =>
    apiClient.get<ApiResponse<LabTest>>(`/v1/pathology/tests/${id}`),

  createTest: (payload: {
    code: string; name: string; categoryId: string;
    description?: string; price?: number; turnaroundHours?: number;
    unit?: string; normalRange?: string
  }) => apiClient.post<ApiResponse<LabTest>>('/v1/pathology/tests', payload),

  updateTest: (id: string, payload: Partial<{
    name: string; categoryId: string; description: string;
    price: number; turnaroundHours: number; unit: string; normalRange: string
  }>) => apiClient.patch<ApiResponse<LabTest>>(`/v1/pathology/tests/${id}`, payload),

  // ── Orders ─────────────────────────────────────────────────────────────────
  createOrder: (payload: CreateLabOrderPayload) =>
    apiClient.post<ApiResponse<LabOrder>>('/v1/pathology/orders', payload),

  getOrder: (id: string) =>
    apiClient.get<ApiResponse<LabOrder>>(`/v1/pathology/orders/${id}`),

  listOrders: (params?: { status?: LabOrderStatus; page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<LabOrder>>>('/v1/pathology/orders', { params }),

  listByPatient: (patientId: string, params?: { page?: number }) =>
    apiClient.get<ApiResponse<PageResponse<LabOrder>>>(
      `/v1/pathology/orders/patient/${patientId}`, { params }),

  collectSample: (id: string) =>
    apiClient.post<ApiResponse<LabOrder>>(`/v1/pathology/orders/${id}/collect-sample`),

  startProcessing: (id: string) =>
    apiClient.post<ApiResponse<LabOrder>>(`/v1/pathology/orders/${id}/start`),

  enterResult: (orderId: string, itemId: string, payload: EnterResultPayload) =>
    apiClient.post<ApiResponse<LabOrder>>(
      `/v1/pathology/orders/${orderId}/items/${itemId}/result`, payload),

  cancelOrder: (id: string) =>
    apiClient.delete<ApiResponse<LabOrder>>(`/v1/pathology/orders/${id}`),

  updatePayment: (id: string, status: LabPaymentStatus) =>
    apiClient.patch<ApiResponse<LabOrder>>(`/v1/pathology/orders/${id}/payment`, null,
      { params: { status } }),

  // ── Dashboard ──────────────────────────────────────────────────────────────
  getDashboard: () =>
    apiClient.get<ApiResponse<PathologyDashboard>>('/v1/pathology/dashboard'),
}
