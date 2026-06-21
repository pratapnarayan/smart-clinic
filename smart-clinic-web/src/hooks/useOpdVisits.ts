import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { message } from 'antd'
import { opdApi } from '@/api'
import type { OpdVisitCreateRequest, PrescriptionRequest } from '@/types'

export const OPD_KEYS = {
  all:          ['opd']                              as const,
  lists:        () => [...OPD_KEYS.all, 'list']      as const,
  byDate:       (date?: string) => [...OPD_KEYS.lists(), 'date', date] as const,
  byPatient:    (pid: string)   => [...OPD_KEYS.lists(), 'patient', pid] as const,
  detail:       (id: string)    => [...OPD_KEYS.all, id]  as const,
  prescription: (visitId: string) => [...OPD_KEYS.all, visitId, 'rx'] as const,
}

export function useVisitsByDate(date?: string, page = 0) {
  return useQuery({
    queryKey: OPD_KEYS.byDate(date),
    queryFn: () => opdApi.listByDate({ date, page, size: 50 }).then((r) => r.data.data),
    staleTime: 30_000,
  })
}

export function useVisitsByPatient(patientId: string) {
  return useQuery({
    queryKey: OPD_KEYS.byPatient(patientId),
    queryFn: () => opdApi.listByPatient(patientId).then((r) => r.data.data),
    staleTime: 30_000,
  })
}

export function useVisit(id: string) {
  return useQuery({
    queryKey: OPD_KEYS.detail(id),
    queryFn: () => opdApi.getVisit(id).then((r) => r.data.data),
    staleTime: 30_000,
  })
}

export function useCreateVisit() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (body: OpdVisitCreateRequest) =>
      opdApi.createVisit(body).then((r) => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: OPD_KEYS.lists() })
      message.success('OPD visit registered')
    },
    onError: () => { message.error('Failed to create visit') },
  })
}

export function useSavePrescription(visitId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (body: PrescriptionRequest) =>
      opdApi.savePrescription(visitId, body).then((r) => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: OPD_KEYS.detail(visitId) })
      message.success('Prescription saved')
    },
    onError: () => { message.error('Failed to save prescription') },
  })
}

export function useUpdateVisit(id: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (body: Parameters<typeof opdApi.updateVisit>[1]) =>
      opdApi.updateVisit(id, body).then((r) => r.data.data),
    onSuccess: (data) => {
      qc.setQueryData(OPD_KEYS.detail(id), data)
      qc.invalidateQueries({ queryKey: OPD_KEYS.lists() })
      message.success('Visit updated')
    },
    onError: () => { message.error('Failed to update visit') },
  })
}
