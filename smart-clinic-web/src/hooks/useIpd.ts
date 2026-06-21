import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { message } from 'antd'
import { ipdApi } from '@/api'
import type { AdmitPatientPayload, DischargePatientPayload, AddIpdChargePayload, AdmissionStatus } from '@/types'

const KEYS = {
  wards:      ['ipd', 'wards'] as const,
  beds:       (wardId: string) => ['ipd', 'beds', wardId] as const,
  available:  (wardId: string) => ['ipd', 'beds', wardId, 'available'] as const,
  admissions: (status?: AdmissionStatus) => ['ipd', 'admissions', status ?? 'ALL'] as const,
  admission:  (id: string) => ['ipd', 'admissions', id] as const,
  byPatient:  (patientId: string) => ['ipd', 'admissions', 'patient', patientId] as const,
  dashboard:  ['ipd', 'dashboard'] as const,
}

export function useIpdWards() {
  return useQuery({
    queryKey: KEYS.wards,
    queryFn: () => ipdApi.listWards().then(r => r.data.data),
  })
}

export function useIpdBeds(wardId: string) {
  return useQuery({
    queryKey: KEYS.beds(wardId),
    queryFn: () => ipdApi.listBeds(wardId).then(r => r.data.data),
    enabled: !!wardId,
  })
}

export function useAvailableBeds(wardId: string) {
  return useQuery({
    queryKey: KEYS.available(wardId),
    queryFn: () => ipdApi.listAvailableBeds(wardId).then(r => r.data.data),
    enabled: !!wardId,
  })
}

export function useIpdAdmissions(status?: AdmissionStatus, page = 0) {
  return useQuery({
    queryKey: [...KEYS.admissions(status), page],
    queryFn: () => ipdApi.listAdmissions({ status, page, size: 20 }).then(r => r.data.data),
  })
}

export function useIpdAdmission(id: string) {
  return useQuery({
    queryKey: KEYS.admission(id),
    queryFn: () => ipdApi.getAdmission(id).then(r => r.data.data),
    enabled: !!id,
  })
}

export function usePatientAdmissions(patientId: string) {
  return useQuery({
    queryKey: KEYS.byPatient(patientId),
    queryFn: () => ipdApi.listByPatient(patientId).then(r => r.data.data),
    enabled: !!patientId,
  })
}

export function useIpdDashboard() {
  return useQuery({
    queryKey: KEYS.dashboard,
    queryFn: () => ipdApi.getDashboard().then(r => r.data.data),
    refetchInterval: 60_000,
  })
}

export function useAdmitPatient() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: AdmitPatientPayload) =>
      ipdApi.admitPatient(payload).then(r => r.data.data),
    onSuccess: (data) => {
      qc.invalidateQueries({ queryKey: ['ipd'] })
      message.success(`Patient admitted as ${data.admissionNumber}`)
    },
    onError: () => message.error('Failed to admit patient'),
  })
}

export function useDischargePatient(admissionId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: DischargePatientPayload) =>
      ipdApi.discharge(admissionId, payload).then(r => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['ipd'] })
      message.success('Patient discharged successfully')
    },
    onError: () => message.error('Failed to discharge patient'),
  })
}

export function useAddIpdCharge(admissionId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: AddIpdChargePayload) =>
      ipdApi.addCharge(admissionId, payload).then(r => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.admission(admissionId) })
      message.success('Charge added')
    },
    onError: () => message.error('Failed to add charge'),
  })
}
