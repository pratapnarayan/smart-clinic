import { Card, Row, Col, Statistic, Table, Tag, Alert } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { TeamOutlined, MedicineBoxOutlined, ExclamationCircleOutlined } from '@ant-design/icons'
import { PageHeader } from '@/components/common'
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
    <>
      <PageHeader title="Blood Bank" subtitle="Inventory availability and today's activity" />

      {criticalGroups.length > 0 && (
        <Alert
          type="error"
          showIcon
          icon={<ExclamationCircleOutlined />}
          message={`Critical shortage: ${criticalGroups.map(g => g.display).join(', ')} — 0 units available`}
          style={{ marginBottom: 16 }}
        />
      )}

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={12} md={4}>
          <Card loading={isLoading}>
            <Statistic title="Total Donors"   value={dash?.totalDonors ?? 0}   prefix={<TeamOutlined />} />
          </Card>
        </Col>
        <Col xs={12} md={4}>
          <Card loading={isLoading}>
            <Statistic title="Active Donors"  value={dash?.activeDonors ?? 0}  valueStyle={{ color: '#52c41a' }} />
          </Card>
        </Col>
        <Col xs={12} md={4}>
          <Card loading={isLoading}>
            <Statistic title="Available Units" value={dash?.totalAvailable ?? 0}
              prefix={<MedicineBoxOutlined />} valueStyle={{ color: '#52c41a' }} />
          </Card>
        </Col>
        <Col xs={12} md={4}>
          <Card loading={isLoading}>
            <Statistic title="Pending Testing" value={dash?.pendingTesting ?? 0}
              valueStyle={{ color: (dash?.pendingTesting ?? 0) > 0 ? '#1677ff' : undefined }} />
          </Card>
        </Col>
        <Col xs={12} md={4}>
          <Card loading={isLoading}>
            <Statistic title="Open Requests"  value={dash?.openRequests ?? 0}
              valueStyle={{ color: (dash?.openRequests ?? 0) > 0 ? '#fa8c16' : undefined }} />
          </Card>
        </Col>
        <Col xs={12} md={4}>
          <Card loading={isLoading}>
            <Statistic title="Today's Issues"  value={dash?.todayIssues ?? 0} />
          </Card>
        </Col>
      </Row>

      <Card title="Blood Stock by Group" loading={isLoading}>
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
    </>
  )
}
