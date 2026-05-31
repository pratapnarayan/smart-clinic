import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Table, Button, Tag, DatePicker, Space, type TableProps } from 'antd'
import { PlusOutlined, EyeOutlined } from '@ant-design/icons'
import dayjs, { type Dayjs } from 'dayjs'
import { useVisitsByDate } from '@/hooks/useOpdVisits'
import { PageHeader } from '@/components/common/PageHeader'
import { formatCurrency } from '@/utils'
import { OpdVisitFormModal } from './OpdVisitFormModal'
import type { OpdVisit, VisitStatus, PaymentStatus } from '@/types'

const STATUS_COLOR: Record<VisitStatus, string> = {
  REGISTERED: 'orange', IN_PROGRESS: 'blue', COMPLETED: 'green', CANCELLED: 'red',
}
const PAYMENT_COLOR: Record<PaymentStatus, string> = {
  PENDING: 'orange', PAID: 'green', PARTIAL: 'blue', WAIVED: 'purple',
}

export function OpdListPage() {
  const navigate = useNavigate()
  const [date, setDate] = useState<Dayjs>(dayjs())
  const [showForm, setShowForm] = useState(false)

  const { data, isLoading } = useVisitsByDate(date.format('YYYY-MM-DD'))

  const columns: TableProps<OpdVisit>['columns'] = [
    { title: 'Visit #', dataIndex: 'visitNumber', width: 130 },
    { title: 'Patient', dataIndex: 'patientName' },
    { title: 'Doctor', dataIndex: 'doctorName', render: (v) => v ?? '—' },
    { title: 'Department', dataIndex: 'department', render: (v) => v ?? '—' },
    {
      title: 'Status', dataIndex: 'visitStatus',
      render: (v: VisitStatus) => <Tag color={STATUS_COLOR[v]}>{v}</Tag>,
    },
    {
      title: 'Payment', dataIndex: 'paymentStatus',
      render: (v: PaymentStatus) => <Tag color={PAYMENT_COLOR[v]}>{v}</Tag>,
    },
    { title: 'Net Amount', dataIndex: 'netAmount', render: formatCurrency, align: 'right' },
    {
      title: '', key: 'actions',
      render: (_, r) => (
        <Button size="small" icon={<EyeOutlined />} onClick={() => navigate(`/opd/${r.id}`)}>
          View
        </Button>
      ),
    },
  ]

  return (
    <div>
      <PageHeader
        title="OPD Visits"
        subtitle={`${data?.total ?? 0} visit(s) on ${date.format('DD MMM YYYY')}`}
        breadcrumbs={[{ title: 'Dashboard', href: '/dashboard' }, { title: 'OPD' }]}
        extra={
          <Space>
            <DatePicker value={date} onChange={(d) => d && setDate(d)} />
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setShowForm(true)}>
              New Visit
            </Button>
          </Space>
        }
      />

      <Table
        rowKey="id"
        dataSource={data?.content}
        columns={columns}
        loading={isLoading}
        pagination={false}
        onRow={(r) => ({ onDoubleClick: () => navigate(`/opd/${r.id}`) })}
      />

      <OpdVisitFormModal open={showForm} onClose={() => setShowForm(false)} />
    </div>
  )
}
