import { AxiosError } from 'axios'

interface ApiErrorShape {
  code?: string
  message?: string
  details?: Record<string, string>
}

/**
 * Extracts a human-readable message from a failed API call.
 *
 * The backend returns errors as { success: false, error: { code, message, details } },
 * where `details` is a map of field name -> validation message (e.g. for 400s from
 * Jakarta Bean Validation). Falls back to a generic message when the error isn't in
 * the expected shape (network failure, unexpected 5xx, etc.).
 *
 * Without this, callers were showing a single generic toast ("Failed to register
 * patient") with no indication of *why* — e.g. a 400 telling the user exactly which
 * field was invalid was silently discarded.
 */
export function getErrorMessage(error: unknown, fallback = 'Something went wrong'): string {
  if (error instanceof AxiosError) {
    const apiError = error.response?.data?.error as ApiErrorShape | undefined
    if (apiError?.details && Object.keys(apiError.details).length > 0) {
      return Object.values(apiError.details).join('; ')
    }
    if (apiError?.message) {
      return apiError.message
    }
  }
  return fallback
}
