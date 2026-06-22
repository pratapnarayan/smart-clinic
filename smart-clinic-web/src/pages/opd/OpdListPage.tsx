import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Table, Button, Tag, DatePicker, Space, Card, Tabs, Badge, type TableProps } from 'antd'
import { PlusOutlined, EyeOutlined, ClockCircleOutlined } from '@ant-design/icons'
import dayjs, { type Dayjs } from 'dayjs'
import { useVisitsByDate, useOpdQueue } from '@/hooks/useOpdVisits'
import { PageHeader } from '@/components/common/PageHeader'
import { formatCurrency } from '@/utils'
import { OpdVisitFormModal } from './OpdVisitFormModal'
import type { OpdVisit, VisitStatus, PaymentStatus } from '@/types'
import { useAuthStore } from '@/store/authStore'

const STATUS_COLOR: Record<VisitStatus, string> = {
  REGISTERED:  'orange',
  IN_PROGRESS: 'blue',
  COMPLETED:   'green',
  CANCELLED:   'red',
}

const PAYMENT_COLOR: Record<PaymentStatus, string> = {
  PENDING: 'orange',
  PAID:    'green',
  PARTIAL: 'blue',
  WAIVED:  'purple',
}

const STATUS_LABEL: Record<VisitStatus, string> = {
  REGISTERED:  'Waiting',
  IN_PROGRESS: 'In Consultation',
  COMPLETED:   'Completed',
  CANCELLED:   'Cancelled',
}

export function OpdListPage() {
  const navigate           = useNavigate()
  const { hasPermission }  = useAuthStore()
  const [date, setDate]    = useState<Dayjs>(dayjs())
  const [showForm, setShowForm] = useState(false)

  const { data: queue,   isLoading: queueLoading }   = useOpdQueue()
  const { data: history, isLoading: historyLoading } = useVisitsByDate(date.format('YYYY-MM-DD'))

  const canCreate = hasPermission('OPD.CREATE')

  const queueColumns: TableProps<OpdVisit>['columns'] = [
    {
      title: 'Visit #', dataIndex: 'visitNumber', width: 140,
      render: (v) => <span className="font-mono text-xs">{v}</span>,
    },
    { title: 'Patient',    dataIndex: 'patientName' },
    { title: 'Doctor',     dataIndex: 'doctorName',  render: (v) => v ?? '—' },
    { title: 'Department', dataIndex: 'department',  render: (v) => v ?? '—' },
    {
      title: 'Source', dataIndex: 'visitSource', width: 120,
      render: (v: string) => (
        <Tag color={v === 'APPOINTMENT' ? 'geekblue' : 'cyan'}>
          {v === 'APPOINTMENT' ? 'Appointment' : 'Walk-in'}
        </Tag>
      ),
    },
    {
      title: 'Status', dataIndex: 'visitStatus', width: 150,
      render: (v: VisitStatus) => (
        <Tag color={STATUS_COLOR[v]}>{STATUS_LABEL[v]}</Tag>
      ),
    },
    {
      title: '', key: 'actions',
      render: (_, r) => (
        <Button size="small" icon={<EyeOutlined />} onClick={() => navigate(`/opd/${r.id}`)}>
          Open
        </Button>
      ),
    },
  ]

  const historyColumns: TableProps<OpdVisit>['columns'] = [
    { title: 'Visit #',    dataIndex: 'visitNumber', width: 130 },
    { title: 'Patient',    dataIndex: 'patientName' },
    { title: 'Doctor',     dataIndex: 'doctorName',  render: (v) => v ?? '—' },
    { title: 'Department', dataIndex: 'department',  render: (v) => v ?? '—' },
    {
      title: 'Status', dataIndex: 'visitStatus',
      render: (v: VisitStatus) => <Tag color={STATUS_COLOR[v]}>{STATUS_LABEL[v]}</Tag>,
    },
    {
      title: 'Payment', dataIndex: 'paymentStatus',
      render: (v: PaymentStatus) => <Tag color={PAYMENT_COLOR[v]}>{v}</Tag>,
    },
    {
      title: 'Net Amount', dataIndex: 'netAmount',
      render: formatCurrency, align: 'right' as const,
    },
    {
      title: '', key: 'actions',
      render: (_, r) => (
        <Button size="small" icon={<EyeOutlined />} onClick={() => navigate(`/opd/${r.id}`)}>
          View
        </Button>
      ),
    },
  ]

  const waitingCount   = queue?.filter((v) => v.visitStatus === 'REGISTERED').length  ?? 0
  const inConsultCount = queue?.filter((v) => v.visitStatus === 'IN_PROGRESS').length ?? 0

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader
        title="OPD"
        subtitle="Outpatient queue and visit history"
        breadcrumbs={[{ title: 'Dashboard', href: '/dashboard' }, { title: 'OPD' }]}
        extra={
          canCreate && (
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setShowForm(true)}>
              Walk-in Visit
            </Button>
          )
        }
      />

      <Tabs
        defaultActiveKey="queue"
        items={[
          {
            key: 'queue',
            label: (
              <Space>
                <ClockCircleOutlined />
                <span>Today's Queue</span>
                {waitingCount > 0 && (
                  <Badge count={waitingCount} size="small" color="#faad14" />
                )}
                {inConsultCount > 0 && (
                  <Badge count={inConsultCount} size="small" color="#1677ff" />
                )}
              </Space>
            ),
            children: (
              <Card className="medical-card" styles={{ body: { padding: 0 } }}>
                <div className="px-4 py-2 border-b border-neutral-100 flex gap-4 text-sm text-neutral-500">
                  <span><Tag color="orange">Waiting</Tag> {waitingCount}</span>
                  <span><Tag color="blue">In Consultation</Tag> {inConsultCount}</span>
                  <span className="ml-auto text-xs">Auto-refreshes every 20s</span>
                </div>
                <Table
                  rowKey="id"
                  dataSource={queue ?? []}
                  columns={queueColumns}
                  loading={queueLoading}
                  pagination={false}
                  locale={{ emptyText: 'No patients in queue today' }}
                  className="[&_.ant-table-thead>tr>th]:bg-neutral-50 [&_.ant-table-thead>tr>th]:font-semibold"
                  rowClassName={(r) => r.visitStatus === 'IN_PROGRESS' ? 'bg-blue-50' : ''}
                  onRow={(r) => ({ onDoubleClick: () => navigate(`/opd/${r.id}`) })}
                />
              </Card>
            ),
          },
          {
            key: 'history',
            label: 'Visit History',
            children: (
              <Card
                className="medical-card"
                title={
                  <Space>
                    <span>Visits on</span>
                    <DatePicker
                      value={date}
                      onChange={(d) => d && setDate(d)}
                      format="DD MMM YYYY"
                    />
                  </Space>
                }
                styles={{ body: { padding: 0 } }}
              >
                <Table
                  rowKey="id"
                  dataSource={history?.content ?? []}
                  columns={historyColumns}
                  loading={historyLoading}
                  pagination={false}
                  locale={{ emptyText: `No visits on ${date.format('DD MMM YYYY')}` }}
                  className="[&_.ant-table-thead>tr>th]:bg-neutral-50 [&_.ant-table-thead>tr>th]:font-semibold"
                  onRow={(r) => ({ onDoubleClick: () => navigate(`/opd/${r.id}`) })}
                />
              </Card>
            ),
          },
        ]}
      />

      <OpdVisitFormModal open={showForm} onClose={() => setShowForm(false)} />
    </div>
  )
}
