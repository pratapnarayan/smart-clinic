import { Navigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'

interface Props {
  children: React.ReactNode
  /** If provided, user must have this permission to access the route */
  permission?: string
  /** If provided, user must have this role */
  role?: string
}

export function PrivateRoute({ children, permission, role }: Props) {
  const { isAuthenticated, hasPermission, hasRole } = useAuthStore()
  const location = useLocation()

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  if (permission && !hasPermission(permission)) {
    return <Navigate to="/403" replace />
  }

  if (role && !hasRole(role)) {
    return <Navigate to="/403" replace />
  }

  return <>{children}</>
}
