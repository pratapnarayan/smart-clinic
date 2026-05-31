import { Row, Col, Card, Statistic, Typography, Alert, Spin } from 'antd'
import {
  UserOutlined, MedicineBoxOutlined, ShopOutlined, WarningOutlined,
} from '@ant-design/icons'
import { useAuthStore } from '@/store/authStore'
import { useLowStockMedicines, useExpiringBatches } from '@/hooks/usePharmacy'
import { PageHeader } from '@/components/common/PageHeader'
import { formatDate } from '@/utils'
import dayjs from 'dayjs'

export function DashboardPage() {
  const { user } = useAuthStore()
  const { data: lowStock,  isLoading: loadingLow }    = useLowStockMedicines()
  const { data: expiring,  isLoading: loadingExpiry } = useExpiringBatches(30)

  const today = dayjs().format('DD MMM YYYY')

  return (
    <div>
      <PageHeader
        title={`Good ${getGreeting()}, ${user?.firstName ?? ''}!`}
        subtitle={`Today is ${today} · ${user?.tenantId}`}
      />

      {/* KPI cards */}
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Today's OPD Visits"
              value="—"
              prefix={<MedicineBoxOutlined style={{ color: '#1677ff' }} />}
              valueStyle={{ color: '#1677ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Registered Patients"
              value="—"
              prefix={<UserOutlined style={{ color: '#52c41a' }} />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            {loadingLow
              ? <Spin />
              : <Statistic
                  title="Low Stock Medicines"
                  value={lowStock?.length ?? 0}
                  prefix={<ShopOutlined style={{ color: lowStock?.length ? '#ff4d4f' : '#52c41a' }} />}
                  valueStyle={{ color: lowStock?.length ? '#ff4d4f' : '#52c41a' }}
                />
            }
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            {loadingExpiry
              ? <Spin />
              : <Statistic
                  title="Batches Expiring (30d)"
                  value={expiring?.length ?? 0}
                  prefix={<WarningOutlined style={{ color: expiring?.length ? '#faad14' : '#52c41a' }} />}
                  valueStyle={{ color: expiring?.length ? '#faad14' : '#52c41a' }}
                />
            }
          </Card>
        </Col>
      </Row>

      {/* Alerts */}
      <div className="mt-4 space-y-2">
        {lowStock && lowStock.length > 0 && (
          <Alert
            type="error"
            showIcon
            message={`${lowStock.length} medicine(s) at or below reorder level`}
            description={lowStock.slice(0, 5).map((m) => m.name).join(', ')}
          />
        )}
        {expiring && expiring.length > 0 && (
          <Alert
            type="warning"
            showIcon
            message={`${expiring.length} batch(es) expiring within 30 days`}
            description={expiring.slice(0, 5).map((b) => `${b.medicineName} — ${b.batchNumber} (expires ${formatDate(b.expiryDate)})`).join(' · ')}
          />
        )}
        {(!lowStock?.length && !expiring?.length && !loadingLow && !loadingExpiry) && (
          <Alert type="success" showIcon message="All systems normal — no stock or expiry alerts." />
        )}
      </div>

      {/* Coming soon */}
      <Row gutter={[16, 16]} className="mt-4">
        <Col span={24}>
          <Card title="Recent OPD Visits" extra={<Typography.Link href="/opd">View all</Typography.Link>}>
            <Typography.Text type="secondary">
              OPD visit feed will appear here — navigate to <Typography.Link href="/opd">OPD</Typography.Link> to register a visit.
            </Typography.Text>
          </Card>
        </Col>
      </Row>
    </div>
  )
}

function getGreeting(): string {
  const hour = new Date().getHours()
  if (hour < 12) return 'morning'
  if (hour < 17) return 'afternoon'
  return 'evening'
}
