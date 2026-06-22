import { useState } from 'react'
import { Table, Tag, Button, Space, Card, DatePicker, Popconfirm, Tabs, Badge } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { PlusOutlined, CalendarOutlined, CheckCircleOutlined, UserOutlined, RiseOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { PageHeader } from '@/components/common'
import { KpiCard } from '@/components/analytics'
import {
  useAppointments, useUpcomingAppointments, useFrontOfficeDashboard,
  useUpdateAppointment, useCancelAppointment, useCheckIn,
} from '@/hooks/useFrontOffice'
import { useAuthStore } from '@/store/authStore'
import { formatDate } from '@/utils'
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
  SCHEDULED: 'CONFIRMED',
  // CONFIRMED → CHECKED_IN is handled by the dedicated check-in button below
  // CHECKED_IN → COMPLETED is handled by the OPD module
}

// Per-row action buttons — each instance owns its own mutate with the correct ID.
function RowActions({ record }: { record: Appointment }) {
  const { mutate: updateApt, isPending: updating }   = useUpdateAppointment(record.id)
  const { mutate: cancelApt, isPending: cancelling } = useCancelAppointment()
  const { mutate: checkIn,   isPending: checkingIn } = useCheckIn()

  const closed     = record.status === 'CANCELLED' || record.status === 'COMPLETED'
  const canConfirm = record.status === 'SCHEDULED'
  const canCheckIn = record.status === 'CONFIRMED' || record.status === 'SCHEDULED'
  const alreadyIn  = record.status === 'CHECKED_IN'

  return (
    <Space size="small">
      {canConfirm && (
        <Button size="small" type="primary" ghost loading={updating}
          onClick={() => updateApt({ status: 'CONFIRMED' })}>
          Confirm
        </Button>
      )}

      {canCheckIn && !alreadyIn && (
        <Button
          size="small"
          type="primary"
          loading={checkingIn}
          onClick={() => checkIn({ appointmentId: record.id })}
        >
          ✓ Check In
        </Button>
      )}

      {alreadyIn && (
        <Tag color="warning" style={{ margin: 0 }}>Checked In</Tag>
      )}

      {!closed && (
        <Popconfirm title="Cancel this appointment?" onConfirm={() => cancelApt(record.id)}>
          <Button size="small" danger loading={cancelling}>Cancel</Button>
        </Popconfirm>
      )}
    </Space>
  )
}

function buildColumns(withDate: boolean, canEdit: boolean): ColumnsType<Appointment> {
  const cols: ColumnsType<Appointment> = [
    ...(withDate ? [{ title: 'Date', dataIndex: 'appointmentDate', width: 110, render: formatDate } as ColumnsType<Appointment>[number]] : []),
    { title: 'Appointment No.', dataIndex: 'appointmentNumber', width: 160 },
    { title: 'Patient',         dataIndex: 'patientName' },
    { title: 'Mobile',          dataIndex: 'patientMobile',   render: (v?: string) => v ?? '—' },
    { title: 'Doctor',          dataIndex: 'doctorName',      render: (v?: string) => v ?? '—' },
    { title: 'Department',      dataIndex: 'department',      render: (v?: string) => v ?? '—' },
    { title: 'Time Slot',       dataIndex: 'timeSlot',        render: (v?: string) => v ?? '—' },
    { title: 'Type',            dataIndex: 'appointmentType',
      render: (v: string) => <Tag>{v.replace('_', ' ')}</Tag> },
    { title: 'Status',          dataIndex: 'status',
      render: (v: AppointmentStatus) => <Tag color={STATUS_COLOR[v]}>{v.replace('_', ' ')}</Tag> },
  ]
  if (canEdit) cols.push({ title: 'Actions', key: 'actions', render: (_, r) => <RowActions record={r} /> })
  return cols
}

export function AppointmentListPage() {
  const { hasPermission } = useAuthStore()
  const [selectedDate, setSelectedDate] = useState<string>(dayjs().format('YYYY-MM-DD'))
  const [bookOpen, setBookOpen] = useState(false)

  const { data: byDate,   isLoading: loadingDate }     = useAppointments(selectedDate)
  const { data: upcoming, isLoading: loadingUpcoming } = useUpcomingAppointments()
  const { data: dashboard } = useFrontOfficeDashboard()

  const canEdit    = hasPermission('FRONTOFFICE.EDIT')
  const upcomingCols = buildColumns(true,  canEdit)
  const byDateCols   = buildColumns(false, canEdit)

  return (
    <div className="space-y-6 animate-fade-in">
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
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <KpiCard title="Today's Appointments" value={dashboard.todayAppointments.toString()} icon={<CalendarOutlined />} color="primary" />
          <KpiCard title="Confirmed" value={dashboard.confirmedAppointments.toString()} icon={<CheckCircleOutlined />} color="success" />
          <KpiCard title="Checked In" value={dashboard.checkedInAppointments.toString()} icon={<UserOutlined />} color="warning" />
          <KpiCard title="Upcoming (all)" value={(upcoming?.total ?? 0).toString()} icon={<RiseOutlined />} color="cyan" />
        </div>
      )}

      <Tabs
        defaultActiveKey="upcoming"
        items={[
          {
            key: 'upcoming',
            label: (
              <Badge count={upcoming?.total ?? 0} size="small" color="#52c41a" offset={[8, -2]}>
                <span style={{ paddingRight: 6 }}>Upcoming</span>
              </Badge>
            ),
            children: (
              <Card className="medical-card">
                <Table
                  rowKey="id"
                  size="small"
                  columns={upcomingCols}
                  dataSource={upcoming?.content ?? []}
                  loading={loadingUpcoming}
                  pagination={{ pageSize: 20, total: upcoming?.total ?? 0 }}
                />
              </Card>
            ),
          },
          {
            key: 'bydate',
            label: 'By Date',
            children: (
              <Card
                className="medical-card"
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
                  columns={byDateCols}
                  dataSource={byDate?.content ?? []}
                  loading={loadingDate}
                  pagination={{ pageSize: 20, total: byDate?.total ?? 0 }}
                />
              </Card>
            ),
          },
        ]}
      />

      <AppointmentFormModal open={bookOpen} onClose={() => setBookOpen(false)} />
    </div>
  )
}
