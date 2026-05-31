import dayjs from 'dayjs'

/** Formats ISO date string as "12 Jan 1990" */
export function formatDate(iso: string | null | undefined): string {
  if (!iso) return '—'
  return dayjs(iso).format('DD MMM YYYY')
}

/** Formats ISO datetime as "30 May 2026, 10:45 AM" */
export function formatDateTime(iso: string | null | undefined): string {
  if (!iso) return '—'
  return dayjs(iso).format('DD MMM YYYY, hh:mm A')
}

/** Formats a number as Indian rupee currency: ₹1,234.50 */
export function formatCurrency(amount: number | null | undefined): string {
  if (amount == null) return '—'
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 2,
  }).format(amount)
}

/** Calculates age in years from a date-of-birth ISO string */
export function calcAge(dob: string | null | undefined): string {
  if (!dob) return '—'
  return `${dayjs().diff(dayjs(dob), 'year')} yrs`
}

/** Returns initials from first + last name for avatars */
export function initials(firstName: string, lastName: string): string {
  return `${firstName[0] ?? ''}${lastName[0] ?? ''}`.toUpperCase()
}
