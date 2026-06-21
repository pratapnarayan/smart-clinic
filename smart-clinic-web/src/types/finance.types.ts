export type IncomeSourceType = 'OPD' | 'IPD' | 'PHARMACY' | 'PATHOLOGY' | 'RADIOLOGY' | 'OTHER'
export type PaymentMode      = 'CASH' | 'CARD' | 'UPI' | 'CHEQUE' | 'NEFT' | 'OTHER'

export interface ExpenseCategory {
  id: string
  name: string
  description?: string
  active: boolean
}

export interface IncomeEntry {
  id: string
  entryNumber: string
  entryDate: string
  sourceType: IncomeSourceType
  sourceId?: string
  patientName?: string
  amount: number
  description: string
  paymentMode: PaymentMode
  referenceNo?: string
  receivedBy?: string
  notes?: string
  createdAt: string
}

export interface ExpenseEntry {
  id: string
  entryNumber: string
  entryDate: string
  categoryId: string
  categoryName: string
  description: string
  amount: number
  paymentMode: PaymentMode
  referenceNo?: string
  paidTo?: string
  approvedBy?: string
  notes?: string
  createdAt: string
}

export interface FinanceDashboard {
  todayIncome: number
  todayExpenses: number
  todayNet: number
  monthIncome: number
  monthExpenses: number
  monthNet: number
  monthIncomeBySource: { source: string; amount: number }[]
  monthExpenseByCategory: { category: string; amount: number }[]
}

export interface PeriodSummary {
  from: string
  to: string
  totalIncome: number
  totalExpenses: number
  netRevenue: number
}

export interface CreateIncomePayload {
  entryDate?: string
  sourceType: IncomeSourceType
  sourceId?: string
  patientName?: string
  amount: number
  description: string
  paymentMode: PaymentMode
  referenceNo?: string
  receivedBy?: string
  notes?: string
}

export interface CreateExpensePayload {
  entryDate?: string
  categoryId: string
  description: string
  amount: number
  paymentMode: PaymentMode
  referenceNo?: string
  paidTo?: string
  approvedBy?: string
  notes?: string
}
