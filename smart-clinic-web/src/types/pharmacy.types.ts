export interface MedicineCategory {
  id: string
  name: string
}

export interface Medicine {
  id: string
  categoryId: string
  categoryName: string
  name: string
  genericName: string | null
  unit: string
  reorderLevel: number
  availableStock: number
}

export interface MedicineBatch {
  id: string
  medicineId: string
  medicineName: string
  batchNumber: string
  expiryDate: string
  quantity: number
  purchasePrice: number
  salePrice: number
  expired: boolean
  lowStock: boolean
}

export interface StockSummary {
  medicineId: string
  medicineName: string
  genericName: string | null
  unit: string
  totalStock: number
  reorderLevel: number
  lowStock: boolean
  lowestSalePrice: number
  nearestExpiry: string | null
  batches: MedicineBatch[]
}

export interface BillItem {
  id: string
  batchId: string | null
  medicineName: string
  quantity: number
  unitPrice: number
  totalPrice: number
}

export interface PharmacyBill {
  id: string
  billNumber: string
  patientId: string | null
  patientName: string | null
  totalAmount: number
  discount: number
  netAmount: number
  paymentMode: string
  status: string
  items: BillItem[]
  createdAt: string
}

export interface BillCreateRequest {
  patientId?: string
  paymentMode?: string
  discount?: number
  items: { batchId: string; quantity: number }[]
}
