import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { message } from 'antd'
import { operationApi } from '@/api'
import type { OtStatus, CreateTheatrePayload, ScheduleOperationPayload, CompleteOperationPayload } from '@/types'

const KEYS = {
  dashboard:  ['operation', 'dashboard'] as const,
  theatres:   ['operation', 'theatres'] as const,
  schedules:  (params?: object) => ['operation', 'schedules', params] as const,
  schedule:   (id: string) => ['operation', 'schedule', id] as const,
}

export function useOtDashboard() {
  return useQuery({
    queryKey: KEYS.dashboard,
    queryFn:  () => operationApi.getDashboard().then(r => r.data.data),
    refetchInterval: 30_000,
  })
}

export function useOtTheatres() {
  return useQuery({
    queryKey: KEYS.theatres,
    queryFn:  () => operationApi.listTheatres().then(r => r.data.data),
    staleTime: 5 * 60_000,
  })
}

export function useCreateTheatre() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreateTheatrePayload) =>
      operationApi.createTheatre(payload).then(r => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.theatres })
      message.success('Theatre added')
    },
    onError: () => message.error('Failed to add theatre'),
  })
}

export function useOtSchedules(params?: { date?: string; status?: OtStatus; theatreId?: string; page?: number }) {
  return useQuery({
    queryKey: KEYS.schedules(params),
    queryFn:  () => operationApi.listSchedules({ ...params, size: 20 }).then(r => r.data.data),
  })
}

export function useOtSchedule(id: string) {
  return useQuery({
    queryKey: KEYS.schedule(id),
    queryFn:  () => operationApi.getSchedule(id).then(r => r.data.data),
    enabled: !!id,
  })
}

export function useScheduleOperation() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: ScheduleOperationPayload) =>
      operationApi.scheduleOperation(payload).then(r => r.data.data),
    onSuccess: (d) => {
      qc.invalidateQueries({ queryKey: ['operation', 'schedules'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success(`Operation ${d.scheduleNumber} scheduled`)
    },
    onError: () => message.error('Failed to schedule operation'),
  })
}

export function useStartOperation(scheduleId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: () => operationApi.startOperation(scheduleId).then(r => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.schedule(scheduleId) })
      qc.invalidateQueries({ queryKey: ['operation', 'schedules'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success('Operation started')
    },
    onError: () => message.error('Failed to start operation'),
  })
}

export function useCompleteOperation(scheduleId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CompleteOperationPayload) =>
      operationApi.completeOperation(scheduleId, payload).then(r => r.data.data),
    onSuccess: (d) => {
      qc.invalidateQueries({ queryKey: KEYS.schedule(scheduleId) })
      qc.invalidateQueries({ queryKey: ['operation', 'schedules'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      qc.invalidateQueries({ queryKey: ['inventory', 'items'] })
      message.success(`Operation ${d.scheduleNumber} completed — ${d.outcome}`)
    },
    onError: () => message.error('Failed to complete operation'),
  })
}

export function usePostponeOperation(scheduleId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (notes?: string) =>
      operationApi.postponeOperation(scheduleId, notes).then(r => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.schedule(scheduleId) })
      qc.invalidateQueries({ queryKey: ['operation', 'schedules'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success('Operation postponed')
    },
    onError: () => message.error('Failed to postpone operation'),
  })
}

export function useCancelOperation(scheduleId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (notes?: string) =>
      operationApi.cancelOperation(scheduleId, notes).then(r => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.schedule(scheduleId) })
      qc.invalidateQueries({ queryKey: ['operation', 'schedules'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success('Operation cancelled')
    },
    onError: () => message.error('Failed to cancel operation'),
  })
}
