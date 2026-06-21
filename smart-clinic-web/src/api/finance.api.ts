import { apiClient } from './client'
import type { ApiResponse, PageResponse } from '@/types'
import type {
  ExpenseCategory, IncomeEntry, ExpenseEntry,
  FinanceDashboard, PeriodSummary,
  CreateIncomePayload, CreateExpensePayload,
  IncomeSourceType,
} from '@/types'

export const financeApi = {
  // ── Dashboard ──────────────────────────────────────────────────────────────
  getDashboard: () =>
    apiClient.get<ApiResponse<FinanceDashboard>>('/v1/finance/dashboard'),

  getSummary: (from?: string, to?: string) =>
    apiClient.get<ApiResponse<PeriodSummary>>('/v1/finance/summary',
      { params: { from, to } }),

  // ── Expense Categories ─────────────────────────────────────────────────────
  listCategories: () =>
    apiClient.get<ApiResponse<ExpenseCategory[]>>('/v1/finance/expense-categories'),

  createCategory: (payload: { name: string; description?: string }) =>
    apiClient.post<ApiResponse<ExpenseCategory>>('/v1/finance/expense-categories', payload),

  // ── Income ─────────────────────────────────────────────────────────────────
  createIncome: (payload: CreateIncomePayload) =>
    apiClient.post<ApiResponse<IncomeEntry>>('/v1/finance/income', payload),

  getIncome: (id: string) =>
    apiClient.get<ApiResponse<IncomeEntry>>(`/v1/finance/income/${id}`),

  listIncome: (params?: { from?: string; to?: string; sourceType?: IncomeSourceType; page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<IncomeEntry>>>('/v1/finance/income', { params }),

  // ── Expenses ───────────────────────────────────────────────────────────────
  createExpense: (payload: CreateExpensePayload) =>
    apiClient.post<ApiResponse<ExpenseEntry>>('/v1/finance/expenses', payload),

  getExpense: (id: string) =>
    apiClient.get<ApiResponse<ExpenseEntry>>(`/v1/finance/expenses/${id}`),

  listExpenses: (params?: { from?: string; to?: string; categoryId?: string; page?: number; size?: number }) =>
    apiClient.get<ApiResponse<PageResponse<ExpenseEntry>>>('/v1/finance/expenses', { params }),
}
