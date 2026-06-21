import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { message } from 'antd'
import { pathologyApi } from '@/api'
import type { CreateLabOrderPayload, EnterResultPayload, LabOrderStatus, LabPaymentStatus } from '@/types'

const KEYS = {
  categories:  ['pathology', 'categories'] as const,
  tests:       (catId?: string) => ['pathology', 'tests', catId ?? 'all'] as const,
  orders:      (status?: LabOrderStatus) => ['pathology', 'orders', status ?? 'ALL'] as const,
  order:       (id: string) => ['pathology', 'orders', id] as const,
  byPatient:   (pid: string) => ['pathology', 'orders', 'patient', pid] as const,
  dashboard:   ['pathology', 'dashboard'] as const,
}

export function useLabCategories() {
  return useQuery({
    queryKey: KEYS.categories,
    queryFn: () => pathologyApi.listCategories().then(r => r.data.data),
  })
}

export function useLabTests(categoryId?: string) {
  return useQuery({
    queryKey: KEYS.tests(categoryId),
    queryFn: () => pathologyApi.listTests(categoryId).then(r => r.data.data),
  })
}

export function useLabOrders(status?: LabOrderStatus, page = 0) {
  return useQuery({
    queryKey: [...KEYS.orders(status), page],
    queryFn: () => pathologyApi.listOrders({ status, page, size: 20 }).then(r => r.data.data),
  })
}

export function useLabOrder(id: string) {
  return useQuery({
    queryKey: KEYS.order(id),
    queryFn: () => pathologyApi.getOrder(id).then(r => r.data.data),
    enabled: !!id,
  })
}

export function usePatientLabOrders(patientId: string) {
  return useQuery({
    queryKey: KEYS.byPatient(patientId),
    queryFn: () => pathologyApi.listByPatient(patientId).then(r => r.data.data),
    enabled: !!patientId,
  })
}

export function usePathologyDashboard() {
  return useQuery({
    queryKey: KEYS.dashboard,
    queryFn: () => pathologyApi.getDashboard().then(r => r.data.data),
    refetchInterval: 60_000,
  })
}

export function useCreateLabOrder() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreateLabOrderPayload) =>
      pathologyApi.createOrder(payload).then(r => r.data.data),
    onSuccess: (data) => {
      qc.invalidateQueries({ queryKey: ['pathology', 'orders'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success(`Lab order ${data.orderNumber} created`)
    },
    onError: () => message.error('Failed to create lab order'),
  })
}

export function useCollectSample(orderId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: () => pathologyApi.collectSample(orderId).then(r => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.order(orderId) })
      qc.invalidateQueries({ queryKey: ['pathology', 'orders'] })
      message.success('Sample collected')
    },
    onError: () => message.error('Failed to collect sample'),
  })
}

export function useEnterResult(orderId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ itemId, payload }: { itemId: string; payload: EnterResultPayload }) =>
      pathologyApi.enterResult(orderId, itemId, payload).then(r => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.order(orderId) })
      message.success('Result saved')
    },
    onError: () => message.error('Failed to save result'),
  })
}

export function useStartProcessing(orderId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: () => pathologyApi.startProcessing(orderId).then(r => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.order(orderId) })
      qc.invalidateQueries({ queryKey: ['pathology', 'orders'] })
      message.success('Processing started')
    },
    onError: () => message.error('Failed to start processing'),
  })
}

export function useCancelLabOrder(orderId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: () => pathologyApi.cancelOrder(orderId).then(r => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.order(orderId) })
      qc.invalidateQueries({ queryKey: ['pathology', 'orders'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success('Order cancelled')
    },
    onError: () => message.error('Failed to cancel order'),
  })
}

export function useUpdateLabPayment(orderId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (status: LabPaymentStatus) =>
      pathologyApi.updatePayment(orderId, status).then(r => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.order(orderId) })
      message.success('Payment status updated')
    },
    onError: () => message.error('Failed to update payment'),
  })
}
