import { useEffect, useState } from 'react'
import { RouterProvider } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ConfigProvider, App as AntApp, Spin } from 'antd'
import axios from 'axios'
import { router } from '@/router'
import { useAuthStore } from '@/store/authStore'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30_000,
      retry: 1,
    },
  },
})

/**
 * On every app load, if the user is authenticated, refresh the access token.
 * This ensures the JWT always reflects the latest role permissions even when
 * modules are added after the original login.
 */
function TokenGuard({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, refreshToken, setTokens, logout } = useAuthStore()
  const [ready, setReady] = useState(!isAuthenticated)

  useEffect(() => {
    if (!isAuthenticated || !refreshToken) {
      setReady(true)
      return
    }
    axios
      .post('/api/v1/auth/refresh', null, { params: { refreshToken } })
      .then(({ data }) => {
        setTokens(data.data.accessToken, data.data.refreshToken)
      })
      .catch((err) => {
        // 401 means the refresh token itself is invalid/expired → force re-login
        if (err?.response?.status === 401) {
          logout()
        }
        // Network errors or 5xx: proceed with the existing token
      })
      .finally(() => setReady(true))
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  if (!ready) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <Spin size="large" />
      </div>
    )
  }

  return <>{children}</>
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ConfigProvider
        theme={{
          token: {
            colorPrimary: '#1677ff',
            borderRadius: 6,
            fontFamily:
              "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
          },
        }}
      >
        <AntApp>
          <TokenGuard>
            <RouterProvider router={router} />
          </TokenGuard>
        </AntApp>
      </ConfigProvider>
    </QueryClientProvider>
  )
}
