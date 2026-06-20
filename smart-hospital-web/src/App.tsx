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
