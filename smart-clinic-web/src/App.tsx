import { useEffect, useState } from 'react'
import { RouterProvider } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ConfigProvider, App as AntApp, Spin } from 'antd'
import axios from 'axios'
import { router } from '@/router'
import { useAuthStore } from '@/store/authStore'
import { antdTheme } from '@/theme/antdTheme'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30_000,
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
})

function TokenGuard({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, refreshToken, setTokens, setUser, user, logout } = useAuthStore()
  const [ready, setReady] = useState(!isAuthenticated)

  useEffect(() => {
    if (!isAuthenticated || !refreshToken) {
      setReady(true)
      return
    }
    axios
      .post('/api/v1/auth/refresh', null, { params: { refreshToken } })
      .then(({ data }) => {
        const accessToken = data.data.accessToken
        setTokens(accessToken, data.data.refreshToken)
        // The user object isn't persisted (only tokens are), so on a fresh
        // page load it's null until we rehydrate it here. Without this, the
        // header avatar/name fall back to a generic placeholder after every
        // reload even though the session is still valid.
        if (!user) {
          try {
            const payload = JSON.parse(atob(accessToken.split('.')[1]))
            setUser({
              id: payload.sub,
              email: payload.email,
              firstName: payload.first_name ?? payload.email?.split('@')[0] ?? 'User',
              lastName: payload.last_name ?? '',
              role: payload.roles?.[0],
              tenantId: payload.tenant_id,
            })
          } catch {
            // Malformed token — leave user as-is, TokenGuard's 401 handling covers this.
          }
        }
      })
      .catch((err) => {
        if (err?.response?.status === 401) {
          logout()
        }
      })
      .finally(() => setReady(true))
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  if (!ready) {
    return (
      <div className="flex items-center justify-center h-screen bg-background-primary">
        <div className="text-center">
          <Spin size="large" className="mb-4" />
          <p className="text-sm text-text-muted">Authenticating...</p>
        </div>
      </div>
    )
  }

  return <>{children}</>
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ConfigProvider theme={antdTheme}>
        <AntApp>
          <TokenGuard>
            <RouterProvider router={router} />
          </TokenGuard>
        </AntApp>
      </ConfigProvider>
    </QueryClientProvider>
  )
}
