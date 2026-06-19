import { useNavigate } from 'react-router-dom'
import { Card, Tag, Table, Button } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import {
  ClockCircleOutlined, CalendarOutlined,
  PlayCircleOutlined, CheckCircleOutlined, FileSearchOutlined,
} from '@ant-design/icons'
import dayjs from 'dayjs'
import { PageHeader } from '@/components/common'
import { KpiCard } from '@/components/analytics'
import { useRadiologyDashboard, useRadiologyOrders } from '@/hooks/useRadiology'
import type { RadiologyOrder, RadiologyOrderStatus } from '@/types'

const STATUS_COLOR: Record<RadiologyOrderStatus, string> = {
  PENDING:     'default',
  IN_PROGRESS: 'processing',
  COMPLETED:   'success',
  CANCELLED:   'error',
}

const PRIORITY_COLOR = { ROUTINE: 'default', URGENT: 'orange', STAT: 'red' } as const

export function RadiologyDashboardPage() {
  const navigate = useNavigate()
  const { data: dash, isLoading: dashLoading } = useRadiologyDashboard()
  const { data: pending }                      = useRadiologyOrders('PENDING', 0)
  const { data: inProgress }                   = useRadiologyOrders('IN_PROGRESS', 0)

  const activeOrders = [
    ...(inProgress?.content ?? []),
    ...(pending?.content ?? []),
  ]

  const columns: ColumnsType<RadiologyOrder> = [
    {
      title: 'Order No.',
      dataIndex: 'orderNumber',
      render: (v: string, r: RadiologyOrder) => (
        <Button type="link" style={{ padding: 0 }} onClick={() => navigate(`/radiology/orders/${r.id}`)}>
          {v}
        </Button>
      ),
    },
    { title: 'Patient',   dataIndex: 'patientName' },
    {
      title: 'Studies',
      dataIndex: 'items',
      render: (items: RadiologyOrder['items']) =>
        items.map(i => <Tag key={i.studyCode} style={{ margin: 2 }}>{i.studyCode}</Tag>),
    },
    {
      title: 'Priority', dataIndex: 'priority',
      render: (v: keyof typeof PRIORITY_COLOR) => <Tag color={PRIORITY_COLOR[v]}>{v}</Tag>,
    },
    {
      title: 'Status', dataIndex: 'status',
      render: (v: RadiologyOrderStatus) => <Tag color={STATUS_COLOR[v]}>{v.replace('_', ' ')}</Tag>,
    },
    {
      title: 'Created', dataIndex: 'createdAt',
      render: (v: string) => dayjs(v).format('DD/MM/YY HH:mm'),
    },
  ]

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader
        title="Radiology"
        subtitle="Imaging orders overview and active worklist"
        extra={
          <Button type="primary" icon={<FileSearchOutlined />}
            onClick={() => navigate('/radiology/orders')}>
            All Orders
          </Button>
        }
      />

      <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
        <KpiCard
          title="Pending"
          value={dash?.pendingOrders ?? 0}
          icon={<ClockCircleOutlined />}
          color="warning"
          loading={dashLoading}
        />
        <KpiCard
          title="Scheduled"
          value={dash?.scheduledOrders ?? 0}
          icon={<CalendarOutlined />}
          color="primary"
          loading={dashLoading}
        />
        <KpiCard
          title="In Progress"
          value={dash?.inProgressOrders ?? 0}
          icon={<PlayCircleOutlined />}
          color="cyan"
          loading={dashLoading}
        />
        <KpiCard
          title="Completed"
          value={dash?.completedOrders ?? 0}
          icon={<CheckCircleOutlined />}
          color="success"
          loading={dashLoading}
        />
        <KpiCard
          title="Studies in Catalog"
          value={dash?.totalStudies ?? 0}
          icon={<FileSearchOutlined />}
          color="purple"
          loading={dashLoading}
        />
      </div>

      <Card title="Active Worklist — Pending &amp; In Progress" className="medical-card">
        <Table
          rowKey="id"
          size="small"
          columns={columns}
          dataSource={activeOrders}
          pagination={false}
          locale={{ emptyText: 'No active radiology orders' }}
          onRow={r => ({ onClick: () => navigate(`/radiology/orders/${r.id}`) })}
        />
      </Card>
    </div>
  )
}
