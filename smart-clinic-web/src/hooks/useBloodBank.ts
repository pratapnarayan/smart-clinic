import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { message } from 'antd'
import { bloodBankApi } from '@/api'
import type {
  BloodGroup, ComponentType, UnitStatus, RequestStatus,
  RegisterDonorPayload, AddBloodUnitPayload, CreateBloodRequestPayload, IssueBloodPayload,
} from '@/types'

const KEYS = {
  dashboard:  ['bloodbank', 'dashboard'] as const,
  donors:     (params?: object) => ['bloodbank', 'donors', params] as const,
  units:      (params?: object) => ['bloodbank', 'units', params] as const,
  available:  (bg: string, ct: string) => ['bloodbank', 'available', bg, ct] as const,
  requests:   (params?: object) => ['bloodbank', 'requests', params] as const,
  request:    (id: string) => ['bloodbank', 'request', id] as const,
  reqIssues:  (id: string) => ['bloodbank', 'request', id, 'issues'] as const,
  issues:     (params?: object) => ['bloodbank', 'issues', params] as const,
}

export function useBloodBankDashboard() {
  return useQuery({
    queryKey: KEYS.dashboard,
    queryFn:  () => bloodBankApi.getDashboard().then(r => r.data.data),
    refetchInterval: 60_000,
  })
}

export function useBloodDonors(params?: { q?: string; bloodGroup?: BloodGroup; page?: number }) {
  return useQuery({
    queryKey: KEYS.donors(params),
    queryFn:  () => bloodBankApi.listDonors({ ...params, size: 20 }).then(r => r.data.data),
  })
}

export function useRegisterDonor() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: RegisterDonorPayload) =>
      bloodBankApi.registerDonor(payload).then(r => r.data.data),
    onSuccess: (d) => {
      qc.invalidateQueries({ queryKey: ['bloodbank', 'donors'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success(`Donor ${d.donorNumber} registered`)
    },
    onError: () => message.error('Failed to register donor'),
  })
}

export function useBloodUnits(params?: {
  bloodGroup?: BloodGroup; componentType?: ComponentType; status?: UnitStatus; page?: number
}) {
  return useQuery({
    queryKey: KEYS.units(params),
    queryFn:  () => bloodBankApi.listUnits({ ...params, size: 20 }).then(r => r.data.data),
  })
}

export function useAddBloodUnit() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: AddBloodUnitPayload) =>
      bloodBankApi.addUnit(payload).then(r => r.data.data),
    onSuccess: (d) => {
      qc.invalidateQueries({ queryKey: ['bloodbank', 'units'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success(`Unit ${d.unitNumber} added (pending testing)`)
    },
    onError: () => message.error('Failed to add blood unit'),
  })
}

export function useUpdateUnitStatus(unitId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: { status: UnitStatus; notes?: string }) =>
      bloodBankApi.updateUnitStatus(unitId, payload).then(r => r.data.data),
    onSuccess: (d) => {
      qc.invalidateQueries({ queryKey: ['bloodbank', 'units'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success(`Unit ${d.unitNumber} → ${d.status.replace('_', ' ')}`)
    },
    onError: () => message.error('Failed to update unit status'),
  })
}

export function useAvailableUnits(bloodGroup: BloodGroup | undefined, componentType: ComponentType | undefined) {
  return useQuery({
    queryKey: KEYS.available(bloodGroup ?? '', componentType ?? ''),
    queryFn:  () => bloodBankApi.getAvailableUnits(bloodGroup!, componentType!).then(r => r.data.data),
    enabled: !!bloodGroup && !!componentType,
  })
}

export function useBloodRequests(params?: { status?: RequestStatus; page?: number }) {
  return useQuery({
    queryKey: KEYS.requests(params),
    queryFn:  () => bloodBankApi.listRequests({ ...params, size: 20 }).then(r => r.data.data),
  })
}

export function useBloodRequest(id: string) {
  return useQuery({
    queryKey: KEYS.request(id),
    queryFn:  () => bloodBankApi.getRequest(id).then(r => r.data.data),
    enabled: !!id,
  })
}

export function useRequestIssues(requestId: string) {
  return useQuery({
    queryKey: KEYS.reqIssues(requestId),
    queryFn:  () => bloodBankApi.getRequestIssues(requestId).then(r => r.data.data),
    enabled: !!requestId,
  })
}

export function useCreateBloodRequest() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreateBloodRequestPayload) =>
      bloodBankApi.createRequest(payload).then(r => r.data.data),
    onSuccess: (d) => {
      qc.invalidateQueries({ queryKey: ['bloodbank', 'requests'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success(`Request ${d.requestNumber} created`)
    },
    onError: () => message.error('Failed to create request'),
  })
}

export function useCancelRequest(requestId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: () => bloodBankApi.cancelRequest(requestId).then(r => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['bloodbank', 'requests'] })
      qc.invalidateQueries({ queryKey: KEYS.request(requestId) })
      message.success('Request cancelled')
    },
    onError: () => message.error('Failed to cancel request'),
  })
}

export function useIssueBlood() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: IssueBloodPayload) =>
      bloodBankApi.issueBlood(payload).then(r => r.data.data),
    onSuccess: (d) => {
      qc.invalidateQueries({ queryKey: ['bloodbank', 'units'] })
      qc.invalidateQueries({ queryKey: ['bloodbank', 'requests'] })
      qc.invalidateQueries({ queryKey: ['bloodbank', 'issues'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success(`Blood issued — ${d.issueNumber}`)
    },
    onError: () => message.error('Failed to issue blood'),
  })
}
