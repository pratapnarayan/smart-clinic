import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Table, Tag, Button, Select, Card, Space } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { PlusOutlined, ExperimentOutlined, ClockCircleOutlined, PlayCircleOutlined, CheckCircleOutlined, UnorderedListOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { PageHeader } from '@/components/common'
import { KpiCard } from '@/components/analytics'
import { useLabOrders, usePathologyDashboard } from '@/hooks/usePathology'
import { useAuthStore } from '@/store/authStore'
import type { LabOrder, LabOrderStatus } from '@/types'
import { LabOrderFormModal } from './LabOrderFormModal'

const STATUS_COLOR: Record<LabOrderStatus, string> = {
  PENDING:          'default',
  SAMPLE_COLLECTED: 'processing',
  IN_PROGRESS:      'warning',
  COMPLETED:        'success',
  CANCELLED:        'error',
}

const PRIORITY_COLOR = { ROUTINE: 'default', URGENT: 'orange', STAT: 'red' } as const

export function PathologyListPage() {
  const navigate = useNavigate()
  const { hasPermission } = useAuthStore()
  const [page, setPage]       = useState(0)
  const [status, setStatus]   = useState<LabOrderStatus | undefined>(undefined)
  const [orderOpen, setOrderOpen] = useState(false)

  const { data, isLoading } = useLabOrders(status, page)
  const { data: dash }      = usePathologyDashboard()

  const columns: ColumnsType<LabOrder> = [
    {
      title: 'Order No.',
      dataIndex: 'orderNumber',
      render: (v: string, r: LabOrder) => (
        <Button type="link" onClick={() => navigate(`/pathology/${r.id}`)}>{v}</Button>
      ),
    },
    { title: 'Patient',   dataIndex: 'patientName' },
    { title: 'Tests',     dataIndex: 'items',
      render: (items: LabOrder['items']) =>
        items.map(i => <Tag key={i.testCode} style={{ margin: 2 }}>{i.testCode}</Tag>) },
    {
      title: 'Priority',
      dataIndex: 'priority',
      render: (v: keyof typeof PRIORITY_COLOR) => <Tag color={PRIORITY_COLOR[v]}>{v}</Tag>,
    },
    {
      title: 'Status',
      dataIndex: 'status',
      render: (v: LabOrderStatus) => <Tag color={STATUS_COLOR[v]}>{v.replace(/_/g, ' ')}</Tag>,
    },
    {
      title: 'Source',
      dataIndex: 'sourceType',
      render: (v: string) => <Tag>{v.replace('_', ' ')}</Tag>,
    },
    {
      title: 'Net Amount',
      dataIndex: 'netAmount',
      render: (v: number) => `₹${v.toLocaleString('en-IN')}`,
    },
    {
      title: 'Created',
      dataIndex: 'createdAt',
      render: (v: string) => dayjs(v).format('DD/MM/YY HH:mm'),
    },
  ]

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader
        title="Pathology — Lab Orders"
        subtitle="Manage lab test orders and results"
        extra={
          hasPermission('PATHOLOGY.CREATE') && (
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setOrderOpen(true)}>
              New Order
            </Button>
          )
        }
      />

      {dash && (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4">
          <KpiCard title="Pending" value={dash.pendingOrders.toString()} icon={<ClockCircleOutlined />} color="warning" />
          <KpiCard title="Sample Collected" value={dash.sampleCollected.toString()} icon={<ExperimentOutlined />} color="primary" />
          <KpiCard title="In Progress" value={dash.inProgressOrders.toString()} icon={<PlayCircleOutlined />} color="cyan" />
          <KpiCard title="Completed" value={dash.completedToday.toString()} icon={<CheckCircleOutlined />} color="success" />
          <KpiCard title="Tests in Catalog" value={dash.totalTests.toString()} icon={<UnorderedListOutlined />} color="purple" />
        </div>
      )}

      <Card
        className="medical-card"
        title={
          <Space>
            <ExperimentOutlined />
            <span>Orders</span>
            <Select
              allowClear
              placeholder="Filter by status"
              style={{ width: 200 }}
              onChange={v => { setStatus(v as LabOrderStatus | undefined); setPage(0) }}
              options={[
                { value: 'PENDING',          label: 'Pending' },
                { value: 'SAMPLE_COLLECTED', label: 'Sample Collected' },
                { value: 'IN_PROGRESS',      label: 'In Progress' },
                { value: 'COMPLETED',        label: 'Completed' },
                { value: 'CANCELLED',        label: 'Cancelled' },
              ]}
            />
          </Space>
        }
      >
        <Table
          rowKey="id"
          columns={columns}
          dataSource={data?.content ?? []}
          loading={isLoading}
          pagination={{
            current: page + 1, pageSize: 20, total: data?.total ?? 0,
            onChange: p => setPage(p - 1),
          }}
        />
      </Card>

      <LabOrderFormModal open={orderOpen} onClose={() => setOrderOpen(false)} />
    </div>
  )
}
