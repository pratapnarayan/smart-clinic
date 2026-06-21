import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { message } from 'antd'
import { authApi, type LoginPayload } from '@/api'
import { useAuthStore } from '@/store/authStore'

export function useLogin() {
  const { setSession } = useAuthStore()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: (payload: LoginPayload) =>
      authApi.login(payload).then((r) => r.data.data),

    onSuccess: (data) => {
      setSession(data.tokens.accessToken, data.tokens.refreshToken, data.user)
      message.success(`Welcome back, ${data.user.firstName}!`)
      navigate('/dashboard')
    },

    onError: () => {
      message.error('Invalid email, password or tenant ID')
    },
  })
}

export function useLogout() {
  const { logout } = useAuthStore()
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: () => authApi.logout(),
    onSettled: () => {
      logout()
      queryClient.clear()
      navigate('/login')
    },
  })
}
