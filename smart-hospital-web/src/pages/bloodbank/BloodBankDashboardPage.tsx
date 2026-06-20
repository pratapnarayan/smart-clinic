import { Card, Table, Tag, Alert } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import {
  TeamOutlined, HeartOutlined, MedicineBoxOutlined,
  ExperimentOutlined, AlertOutlined, ScheduleOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons'
import { PageHeader } from '@/components/common'
import { KpiCard } from '@/components/analytics'
import { useBloodBankDashboard } from '@/hooks/useBloodBank'
import type { BloodGroupStock } from '@/types'

function stockColor(n: number) {
  if (n === 0) return '#ff4d4f'
  if (n <= 1)  return '#fa8c16'
  if (n <= 3)  return '#fadb14'
  return '#52c41a'
}

const columns: ColumnsType<BloodGroupStock> = [
  {
    title: 'Blood Group', dataIndex: 'display',
    render: (v: string) => (
      <span style={{ fontSize: 18, fontWeight: 700, letterSpacing: 1 }}>{v}</span>
    ),
  },
  {
    title: 'Available', dataIndex: 'available',
    align: 'center',
    render: (v: number) => (
      <Tag color={stockColor(v)} style={{ fontSize: 15, padding: '2px 12px' }}>
        {v} units
      </Tag>
    ),
  },
  {
    title: 'Pending Testing', dataIndex: 'pendingTesting',
    align: 'center',
    render: (v: number) => v > 0
      ? <Tag color="processing">{v} units</Tag>
      : <span style={{ color: '#bbb' }}>—</span>,
  },
  {
    title: 'Status', key: 'status',
    align: 'center',
    render: (_: unknown, r: BloodGroupStock) => {
      if (r.available === 0) return <Tag color="error">Critical — Out of stock</Tag>
      if (r.available <= 1)  return <Tag color="warning">Low stock</Tag>
      return <Tag color="success">Adequate</Tag>
    },
  },
]

export function BloodBankDashboardPage() {
  const { data: dash, isLoading } = useBloodBankDashboard()
  const criticalGroups = (dash?.stockByGroup ?? []).filter(g => g.available === 0)

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader title="Blood Bank" subtitle="Inventory availability and today's activity" />

      {criticalGroups.length > 0 && (
        <Alert
          type="error"
          showIcon
          icon={<ExclamationCircleOutlined />}
          message={`Critical shortage: ${criticalGroups.map(g => g.display).join(', ')} — 0 units available`}
        />
      )}

      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
        <KpiCard
          title="Total Donors"
          value={dash?.totalDonors ?? 0}
          icon={<TeamOutlined />}
          color="primary"
          loading={isLoading}
        />
        <KpiCard
          title="Active Donors"
          value={dash?.activeDonors ?? 0}
          icon={<HeartOutlined />}
          color="success"
          loading={isLoading}
        />
        <KpiCard
          title="Available Units"
          value={dash?.totalAvailable ?? 0}
          icon={<MedicineBoxOutlined />}
          color="cyan"
          loading={isLoading}
        />
        <KpiCard
          title="Pending Testing"
          value={dash?.pendingTesting ?? 0}
          icon={<ExperimentOutlined />}
          color={(dash?.pendingTesting ?? 0) > 0 ? 'warning' : 'primary'}
          loading={isLoading}
        />
        <KpiCard
          title="Open Requests"
          value={dash?.openRequests ?? 0}
          icon={<AlertOutlined />}
          color={(dash?.openRequests ?? 0) > 0 ? 'danger' : 'success'}
          loading={isLoading}
        />
        <KpiCard
          title="Today's Issues"
          value={dash?.todayIssues ?? 0}
          icon={<ScheduleOutlined />}
          color="primary"
          loading={isLoading}
        />
      </div>

      <Card title="Blood Stock by Group" loading={isLoading} className="medical-card">
        <Table
          rowKey="bloodGroup"
          columns={columns}
          dataSource={dash?.stockByGroup ?? []}
          pagination={false}
          size="middle"
          rowClassName={(r: BloodGroupStock) =>
            r.available === 0 ? 'ant-table-row-danger' : ''}
        />
      </Card>
    </div>
  )
}
