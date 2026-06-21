import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { message } from 'antd'
import { inventoryApi } from '@/api'
import type { CreateInventoryItemPayload, RecordReceiptPayload, RecordIssuePayload } from '@/types'

const KEYS = {
  dashboard:  ['inventory', 'dashboard'] as const,
  categories: ['inventory', 'categories'] as const,
  items:      (params?: object) => ['inventory', 'items', params] as const,
  item:       (id: string) => ['inventory', 'item', id] as const,
  receipts:   (params?: object) => ['inventory', 'receipts', params] as const,
  issues:     (params?: object) => ['inventory', 'issues', params] as const,
}

export function useInventoryDashboard() {
  return useQuery({
    queryKey: KEYS.dashboard,
    queryFn:  () => inventoryApi.getDashboard().then(r => r.data.data),
    refetchInterval: 60_000,
  })
}

export function useItemCategories() {
  return useQuery({
    queryKey: KEYS.categories,
    queryFn:  () => inventoryApi.listCategories().then(r => r.data.data),
    staleTime: 5 * 60_000,
  })
}

export function useCreateItemCategory() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: { name: string; description?: string }) =>
      inventoryApi.createCategory(payload).then(r => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.categories })
      message.success('Category created')
    },
    onError: () => message.error('Failed to create category'),
  })
}

export function useInventoryItems(params?: {
  q?: string; categoryId?: string; lowStock?: boolean; page?: number
}) {
  return useQuery({
    queryKey: KEYS.items(params),
    queryFn:  () => inventoryApi.listItems({ ...params, size: 20 }).then(r => r.data.data),
  })
}

export function useCreateInventoryItem() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreateInventoryItemPayload) =>
      inventoryApi.createItem(payload).then(r => r.data.data),
    onSuccess: (data) => {
      qc.invalidateQueries({ queryKey: ['inventory', 'items'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success(`Item "${data.name}" added to catalogue`)
    },
    onError: () => message.error('Failed to create item'),
  })
}

export function useUpdateInventoryItem(id: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreateInventoryItemPayload) =>
      inventoryApi.updateItem(id, payload).then(r => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['inventory', 'items'] })
      qc.invalidateQueries({ queryKey: KEYS.item(id) })
      message.success('Item updated')
    },
    onError: () => message.error('Failed to update item'),
  })
}

export function useStockReceipts(params?: {
  from?: string; to?: string; itemId?: string; page?: number
}) {
  return useQuery({
    queryKey: KEYS.receipts(params),
    queryFn:  () => inventoryApi.listReceipts({ ...params, size: 20 }).then(r => r.data.data),
  })
}

export function useRecordReceipt() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: RecordReceiptPayload) =>
      inventoryApi.recordReceipt(payload).then(r => r.data.data),
    onSuccess: (data) => {
      qc.invalidateQueries({ queryKey: ['inventory', 'items'] })
      qc.invalidateQueries({ queryKey: ['inventory', 'receipts'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success(`Receipt ${data.receiptNumber} recorded`)
    },
    onError: () => message.error('Failed to record receipt'),
  })
}

export function useStockIssues(params?: {
  from?: string; to?: string; itemId?: string; page?: number
}) {
  return useQuery({
    queryKey: KEYS.issues(params),
    queryFn:  () => inventoryApi.listIssues({ ...params, size: 20 }).then(r => r.data.data),
  })
}

export function useRecordIssue() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: RecordIssuePayload) =>
      inventoryApi.recordIssue(payload).then(r => r.data.data),
    onSuccess: (data) => {
      qc.invalidateQueries({ queryKey: ['inventory', 'items'] })
      qc.invalidateQueries({ queryKey: ['inventory', 'issues'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success(`Issue ${data.issueNumber} recorded`)
    },
    onError: () => message.error('Failed to record issue'),
  })
}
