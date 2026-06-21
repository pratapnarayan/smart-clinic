import { apiClient } from './client'
import type {
  ApiResponse, PageResponse,
  MedicineCategory, Medicine, MedicineBatch, StockSummary, PharmacyBill, BillCreateRequest
} from '@/types'

export const pharmacyApi = {
  // Categories
  listCategories: () =>
    apiClient.get<ApiResponse<MedicineCategory[]>>('/v1/pharmacy/categories'),

  createCategory: (body: { name: string }) =>
    apiClient.post<ApiResponse<MedicineCategory>>('/v1/pharmacy/categories', body),

  // Medicines
  searchMedicines: (params?: { query?: string; categoryId?: string; page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<Medicine>>>('/v1/pharmacy/medicines', { params }),

  getMedicine: (id: string) =>
    apiClient.get<ApiResponse<Medicine>>(`/v1/pharmacy/medicines/${id}`),

  createMedicine: (body: Omit<Medicine, 'id' | 'categoryName' | 'availableStock'> & { categoryId: string }) =>
    apiClient.post<ApiResponse<Medicine>>('/v1/pharmacy/medicines', body),

  getLowStock: () =>
    apiClient.get<ApiResponse<Medicine[]>>('/v1/pharmacy/medicines/low-stock'),

  // Stock / Batches
  getStock: (medicineId: string) =>
    apiClient.get<ApiResponse<StockSummary>>(`/v1/pharmacy/stock/${medicineId}`),

  addBatch: (body: Omit<MedicineBatch, 'id' | 'medicineName' | 'expired' | 'lowStock'>) =>
    apiClient.post<ApiResponse<MedicineBatch>>('/v1/pharmacy/stock/batches', body),

  getExpiring: (days?: number) =>
    apiClient.get<ApiResponse<MedicineBatch[]>>('/v1/pharmacy/stock/expiring', {
      params: { days: days ?? 30 },
    }),

  // Billing
  createBill: (body: BillCreateRequest) =>
    apiClient.post<ApiResponse<PharmacyBill>>('/v1/pharmacy/bills', body),

  getBill: (id: string) =>
    apiClient.get<ApiResponse<PharmacyBill>>(`/v1/pharmacy/bills/${id}`),

  listBillsByPatient: (patientId: string, params?: { page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<PharmacyBill>>>(`/v1/pharmacy/bills/patient/${patientId}`, { params }),
}
