import { useState } from 'react'
import { Table, Tag, Button, Space, Card, DatePicker, Statistic, Row, Col, Popconfirm } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { PageHeader } from '@/components/common'
import {
  useAppointments, useFrontOfficeDashboard,
  useUpdateAppointment, useCancelAppointment,
} from '@/hooks/useFrontOffice'
import { useAuthStore } from '@/store/authStore'
import type { Appointment, AppointmentStatus } from '@/types'
import { AppointmentFormModal } from './AppointmentFormModal'

const STATUS_COLOR: Record<AppointmentStatus, string> = {
  SCHEDULED:  'default',
  CONFIRMED:  'processing',
  CHECKED_IN: 'warning',
  COMPLETED:  'success',
  CANCELLED:  'error',
  NO_SHOW:    'default',
}

const NEXT_STATUS: Partial<Record<AppointmentStatus, AppointmentStatus>> = {
  SCHEDULED:  'CONFIRMED',
  CONFIRMED:  'CHECKED_IN',
  CHECKED_IN: 'COMPLETED',
}

export function AppointmentListPage() {
  const { hasPermission } = useAuthStore()
  const [selectedDate, setSelectedDate] = useState<string>(dayjs().format('YYYY-MM-DD'))
  const [bookOpen, setBookOpen] = useState(false)

  const { data, isLoading } = useAppointments(selectedDate)
  const { data: dashboard } = useFrontOfficeDashboard()
  const { mutate: updateApt }  = useUpdateAppointment('')
  const { mutate: cancelApt }  = useCancelAppointment()

  const canEdit = hasPermission('FRONTOFFICE.EDIT')

  const columns: ColumnsType<Appointment> = [
    { title: 'Appointment No.', dataIndex: 'appointmentNumber', width: 160 },
    { title: 'Patient',         dataIndex: 'patientName' },
    { title: 'Mobile',          dataIndex: 'patientMobile', render: (v?: string) => v ?? '—' },
    { title: 'Doctor',          dataIndex: 'doctorName',   render: (v?: string) => v ?? '—' },
    { title: 'Department',      dataIndex: 'department',   render: (v?: string) => v ?? '—' },
    { title: 'Time Slot',       dataIndex: 'timeSlot',     render: (v?: string) => v ?? '—' },
    { title: 'Type',            dataIndex: 'appointmentType',
      render: (v: string) => <Tag>{v.replace('_', ' ')}</Tag> },
    {
      title: 'Status',
      dataIndex: 'status',
      render: (v: AppointmentStatus) => <Tag color={STATUS_COLOR[v]}>{v.replace('_', ' ')}</Tag>,
    },
    canEdit ? {
      title: 'Actions',
      key: 'actions',
      render: (_: unknown, record: Appointment) => {
        const next = NEXT_STATUS[record.status]
        return (
          <Space size="small">
            {next && (
              <Button size="small" type="primary" ghost
                onClick={() => updateApt({ status: next })}>
                → {next.replace('_', ' ')}
              </Button>
            )}
            {record.status !== 'CANCELLED' && record.status !== 'COMPLETED' && (
              <Popconfirm title="Cancel this appointment?" onConfirm={() => cancelApt(record.id)}>
                <Button size="small" danger>Cancel</Button>
              </Popconfirm>
            )}
          </Space>
        )
      },
    } : {},
  ].filter(c => Object.keys(c).length > 0)

  return (
    <>
      <PageHeader
        title="Front Office — Appointments"
        subtitle="Schedule and manage patient appointments"
        extra={
          hasPermission('FRONTOFFICE.CREATE') && (
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setBookOpen(true)}>
              Book Appointment
            </Button>
          )
        }
      />

      {dashboard && (
        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col span={6}><Card><Statistic title="Today's Appointments" value={dashboard.todayAppointments} /></Card></Col>
          <Col span={6}><Card><Statistic title="Confirmed"            value={dashboard.confirmedAppointments}  valueStyle={{ color: '#1677ff' }} /></Card></Col>
          <Col span={6}><Card><Statistic title="Checked In"           value={dashboard.checkedInAppointments}  valueStyle={{ color: '#faad14' }} /></Card></Col>
          <Col span={6}><Card><Statistic title="Tokens Waiting"       value={dashboard.waitingTokens}          valueStyle={{ color: '#52c41a' }} /></Card></Col>
        </Row>
      )}

      <Card
        title={
          <Space>
            <span>Appointments for</span>
            <DatePicker
              defaultValue={dayjs()}
              format="DD MMM YYYY"
              onChange={(d) => setSelectedDate(d ? d.format('YYYY-MM-DD') : dayjs().format('YYYY-MM-DD'))}
            />
          </Space>
        }
      >
        <Table
          rowKey="id"
          size="small"
          columns={columns}
          dataSource={data?.content ?? []}
          loading={isLoading}
          pagination={{ pageSize: 20, total: data?.total ?? 0 }}
        />
      </Card>

      <AppointmentFormModal open={bookOpen} onClose={() => setBookOpen(false)} />
    </>
  )
}
