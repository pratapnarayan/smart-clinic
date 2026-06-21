import { apiClient } from './client'
import type { ApiResponse, PageResponse } from '@/types'
import type {
  ItemCategory, InventoryItem, StockReceipt, StockIssue, InventoryDashboard,
  CreateInventoryItemPayload, RecordReceiptPayload, RecordIssuePayload,
} from '@/types'

export const inventoryApi = {
  getDashboard: () =>
    apiClient.get<ApiResponse<InventoryDashboard>>('/v1/inventory/dashboard'),

  // Categories
  listCategories: () =>
    apiClient.get<ApiResponse<ItemCategory[]>>('/v1/inventory/categories'),

  createCategory: (payload: { name: string; description?: string }) =>
    apiClient.post<ApiResponse<ItemCategory>>('/v1/inventory/categories', payload),

  // Items
  listItems: (params?: { q?: string; categoryId?: string; lowStock?: boolean; page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<InventoryItem>>>('/v1/inventory/items', { params }),

  getItem: (id: string) =>
    apiClient.get<ApiResponse<InventoryItem>>(`/v1/inventory/items/${id}`),

  createItem: (payload: CreateInventoryItemPayload) =>
    apiClient.post<ApiResponse<InventoryItem>>('/v1/inventory/items', payload),

  updateItem: (id: string, payload: CreateInventoryItemPayload) =>
    apiClient.patch<ApiResponse<InventoryItem>>(`/v1/inventory/items/${id}`, payload),

  // Receipts
  recordReceipt: (payload: RecordReceiptPayload) =>
    apiClient.post<ApiResponse<StockReceipt>>('/v1/inventory/receipts', payload),

  getReceipt: (id: string) =>
    apiClient.get<ApiResponse<StockReceipt>>(`/v1/inventory/receipts/${id}`),

  listReceipts: (params?: { from?: string; to?: string; itemId?: string; page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<StockReceipt>>>('/v1/inventory/receipts', { params }),

  // Issues
  recordIssue: (payload: RecordIssuePayload) =>
    apiClient.post<ApiResponse<StockIssue>>('/v1/inventory/issues', payload),

  getIssue: (id: string) =>
    apiClient.get<ApiResponse<StockIssue>>(`/v1/inventory/issues/${id}`),

  listIssues: (params?: { from?: string; to?: string; itemId?: string; page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<StockIssue>>>('/v1/inventory/issues', { params }),
}
