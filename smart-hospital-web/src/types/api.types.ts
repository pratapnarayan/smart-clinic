// Mirrors the ApiResponse<T> envelope from the Spring Boot backend

export interface ApiResponse<T> {
  success: boolean
  data: T
  meta: { timestamp: string }
  error: ApiError | null
}

export interface ApiError {
  code: string
  message: string
  details?: Record<string, string>
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  total: number
  totalPages: number
}
