import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { message } from 'antd'
import { financeApi } from '@/api'
import type { CreateIncomePayload, CreateExpensePayload, IncomeSourceType } from '@/types'

const KEYS = {
  dashboard:  ['finance', 'dashboard'] as const,
  summary:    (from?: string, to?: string) => ['finance', 'summary', from, to] as const,
  categories: ['finance', 'categories'] as const,
  income:     (params?: object) => ['finance', 'income', params] as const,
  expenses:   (params?: object) => ['finance', 'expenses', params] as const,
}

export function useFinanceDashboard() {
  return useQuery({
    queryKey: KEYS.dashboard,
    queryFn:  () => financeApi.getDashboard().then(r => r.data.data),
    refetchInterval: 60_000,
  })
}

export function useFinanceSummary(from?: string, to?: string) {
  return useQuery({
    queryKey: KEYS.summary(from, to),
    queryFn:  () => financeApi.getSummary(from, to).then(r => r.data.data),
  })
}

export function useExpenseCategories() {
  return useQuery({
    queryKey: KEYS.categories,
    queryFn:  () => financeApi.listCategories().then(r => r.data.data),
  })
}

export function useCreateExpenseCategory() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: { name: string; description?: string }) =>
      financeApi.createCategory(payload).then(r => r.data.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.categories })
      message.success('Category created')
    },
    onError: () => message.error('Failed to create category'),
  })
}

export function useIncomeEntries(params?: {
  from?: string; to?: string; sourceType?: IncomeSourceType; page?: number
}) {
  return useQuery({
    queryKey: KEYS.income(params),
    queryFn:  () => financeApi.listIncome({ ...params, size: 20 }).then(r => r.data.data),
  })
}

export function useCreateIncome() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreateIncomePayload) =>
      financeApi.createIncome(payload).then(r => r.data.data),
    onSuccess: (data) => {
      qc.invalidateQueries({ queryKey: ['finance', 'income'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success(`Income entry ${data.entryNumber} recorded`)
    },
    onError: () => message.error('Failed to record income entry'),
  })
}

export function useExpenseEntries(params?: {
  from?: string; to?: string; categoryId?: string; page?: number
}) {
  return useQuery({
    queryKey: KEYS.expenses(params),
    queryFn:  () => financeApi.listExpenses({ ...params, size: 20 }).then(r => r.data.data),
  })
}

export function useCreateExpense() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreateExpensePayload) =>
      financeApi.createExpense(payload).then(r => r.data.data),
    onSuccess: (data) => {
      qc.invalidateQueries({ queryKey: ['finance', 'expenses'] })
      qc.invalidateQueries({ queryKey: KEYS.dashboard })
      message.success(`Expense entry ${data.entryNumber} recorded`)
    },
    onError: () => message.error('Failed to record expense entry'),
  })
}
