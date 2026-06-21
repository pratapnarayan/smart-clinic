import { useEffect } from 'react'
import { Navigate } from 'react-router-dom'
import { Card, Form, Input, Button, Typography, Space, Divider } from 'antd'
import { LockOutlined, MailOutlined, BankOutlined } from '@ant-design/icons'
import { useLogin } from '@/hooks/useAuth'
import { useAuthStore } from '@/store/authStore'
import type { LoginPayload } from '@/api/auth.api'

export function LoginPage() {
  const { isAuthenticated } = useAuthStore()
  const { mutate: login, isPending } = useLogin()
  const [form] = Form.useForm<LoginPayload>()

  // Pre-fill dev credentials for convenience
  useEffect(() => {
    if (import.meta.env.DEV) {
      form.setFieldsValue({
        email: 'admin@clinic001.com',
        password: 'Admin@1234',
        tenantId: 'clinic_001',
      })
    }
  }, [form])

  if (isAuthenticated) return <Navigate to="/dashboard" replace />

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      }}
    >
      <Card className="medical-card" style={{ width: 420 }}>
        <Space direction="vertical" size="middle" style={{ width: '100%', textAlign: 'center' }}>
          <Typography.Title level={3} style={{ margin: 0 }}>
            🏥 SmartClinic
          </Typography.Title>
          <Typography.Text type="secondary">
            Sign in to your hospital account
          </Typography.Text>
        </Space>

        <Divider />

        <Form
          form={form}
          layout="vertical"
          onFinish={login}
          size="large"
          requiredMark={false}
        >
          <Form.Item
            name="tenantId"
            label="Hospital ID"
            tooltip="Your hospital schema name, e.g. hospital_001"
          >
            <Input prefix={<BankOutlined />} placeholder="hospital_001 (optional for super-admin)" />
          </Form.Item>

          <Form.Item
            name="email"
            label="Email"
            rules={[
              { required: true, message: 'Email is required' },
              { type: 'email', message: 'Enter a valid email' },
            ]}
          >
            <Input prefix={<MailOutlined />} placeholder="admin@hospital.com" autoComplete="email" />
          </Form.Item>

          <Form.Item
            name="password"
            label="Password"
            rules={[{ required: true, message: 'Password is required' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="••••••••" autoComplete="current-password" />
          </Form.Item>

          <Form.Item style={{ marginBottom: 0 }}>
            <Button
              type="primary"
              htmlType="submit"
              block
              loading={isPending}
            >
              Sign In
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}
