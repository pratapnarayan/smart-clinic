export interface ItemCategory {
  id: string
  name: string
  description?: string
  active: boolean
}

export interface InventoryItem {
  id: string
  itemCode: string
  name: string
  description?: string
  categoryId: string
  categoryName: string
  unit: string
  reorderLevel: number
  currentStock: number
  lowStock: boolean
  createdAt: string
}

export interface StockReceipt {
  id: string
  receiptNumber: string
  entryDate: string
  itemId: string
  itemName: string
  itemUnit: string
  quantity: number
  unitCost?: number
  totalCost?: number
  supplierName?: string
  grnNumber?: string
  receivedBy?: string
  notes?: string
  createdAt: string
}

export interface StockIssue {
  id: string
  issueNumber: string
  issueDate: string
  itemId: string
  itemName: string
  itemUnit: string
  quantity: number
  issuedTo: string
  issuedBy?: string
  purpose?: string
  notes?: string
  createdAt: string
}

export interface InventoryDashboard {
  totalItems: number
  lowStockCount: number
  todayReceipts: number
  todayIssues: number
  todayReceiptValue: number
  lowStockItems: InventoryItem[]
}

export interface CreateInventoryItemPayload {
  itemCode: string
  name: string
  description?: string
  categoryId: string
  unit: string
  reorderLevel: number
}

export interface RecordReceiptPayload {
  entryDate?: string
  itemId: string
  quantity: number
  unitCost?: number
  supplierName?: string
  grnNumber?: string
  receivedBy?: string
  notes?: string
}

export interface RecordIssuePayload {
  issueDate?: string
  itemId: string
  quantity: number
  issuedTo: string
  issuedBy?: string
  purpose?: string
  notes?: string
}
