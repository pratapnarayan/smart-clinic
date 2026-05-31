import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios'
import { useAuthStore } from '@/store/authStore'

const BASE_URL = '/api'

export const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

// ── Request interceptor: inject access token ─────────────────────────────────
apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = useAuthStore.getState().accessToken
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// ── Response interceptor: handle 401 → refresh → retry ───────────────────────
let refreshing = false
let waitQueue: Array<(token: string) => void> = []

apiClient.interceptors.response.use(
  (res) => res,
  async (error: AxiosError) => {
    const original = error.config as InternalAxiosRequestConfig & { _retry?: boolean }

    if (error.response?.status !== 401 || original._retry) {
      return Promise.reject(error)
    }

    if (refreshing) {
      // Enqueue request until refresh completes
      return new Promise((resolve) => {
        waitQueue.push((token) => {
          original.headers.Authorization = `Bearer ${token}`
          resolve(apiClient(original))
        })
      })
    }

    original._retry = true
    refreshing = true

    try {
      const refreshToken = useAuthStore.getState().refreshToken
      if (!refreshToken) throw new Error('No refresh token')

      const { data } = await axios.post(`${BASE_URL}/v1/auth/refresh`, null, {
        params: { refreshToken },
      })

      const newToken: string = data.data.accessToken
      useAuthStore.getState().setTokens(newToken, data.data.refreshToken)

      waitQueue.forEach((cb) => cb(newToken))
      waitQueue = []

      original.headers.Authorization = `Bearer ${newToken}`
      return apiClient(original)
    } catch {
      useAuthStore.getState().logout()
      window.location.href = '/login'
      return Promise.reject(error)
    } finally {
      refreshing = false
    }
  }
)
