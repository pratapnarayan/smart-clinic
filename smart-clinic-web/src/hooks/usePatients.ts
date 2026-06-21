import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { message } from 'antd'
import { patientApi } from '@/api'
import type { PatientCreateRequest } from '@/types'
import { getErrorMessage } from '@/utils'

export const PATIENT_KEYS = {
  all:    ['patients']                    as const,
  lists:  () => [...PATIENT_KEYS.all, 'list']   as const,
  // `size` is part of the cache key on purpose. Different screens request
  // different page sizes for the same query/page (e.g. the dashboard asks
  // for size=1 just to read the total count, while the Patients list asks
  // for size=20). Without size in the key, both calls collided under the
  // same cache entry, so navigating from Dashboard to Patients via the
  // sidebar (no full page reload) reused the dashboard's 1-record result
  // until a hard refresh wiped the cache and forced a real refetch.
  list:   (q?: string, page = 0, size = 20) => [...PATIENT_KEYS.lists(), q, page, size] as const,
  detail: (id: string) => [...PATIENT_KEYS.all, id]   as const,
}

export function usePatients(query?: string, page = 0, size = 20) {
  return useQuery({
    queryKey: PATIENT_KEYS.list(query, page, size),
    queryFn: () => patientApi.list({ query, page, size }).then((r) => r.data.data),
    placeholderData: (prev) => prev,
    staleTime: 30_000,
  })
}

export function usePatient(id: string) {
  return useQuery({
    queryKey: PATIENT_KEYS.detail(id),
    queryFn: () => patientApi.get(id).then((r) => r.data.data),
    staleTime: 60_000,
  })
}

export function useCreatePatient() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (body: PatientCreateRequest) =>
      patientApi.create(body).then((r) => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: PATIENT_KEYS.lists() })
      message.success('Patient registered successfully')
    },
    onError: (error) => { message.error(getErrorMessage(error, 'Failed to register patient')) },
  })
}

export function useUpdatePatient(id: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (body: PatientCreateRequest) =>
      patientApi.update(id, body).then((r) => r.data.data),
    onSuccess: (data) => {
      qc.setQueryData(PATIENT_KEYS.detail(id), data)
      qc.invalidateQueries({ queryKey: PATIENT_KEYS.lists() })
      message.success('Patient updated')
    },
    onError: (error) => { message.error(getErrorMessage(error, 'Failed to update patient')) },
  })
}
