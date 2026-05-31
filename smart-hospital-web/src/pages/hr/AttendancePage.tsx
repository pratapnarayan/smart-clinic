import { useState } from 'react'
import { Table, Tag, Button, DatePicker, Select, Space, Card, Modal, Form, TimePicker, Input } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { CheckOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { PageHeader } from '@/components/common'
import { useAttendanceByDate, useEmployees, useMarkAttendance } from '@/hooks/useHr'
import type { AttendanceRecord, AttendanceStatus } from '@/types'

const STATUS_COLOR: Record<AttendanceStatus, string> = {
  PRESENT: 'success', ABSENT: 'error', HALF_DAY: 'warning', ON_LEAVE: 'processing', HOLIDAY: 'default',
}

interface MarkForm {
  employeeId: string
  status: AttendanceStatus
  checkIn?: dayjs.Dayjs
  checkOut?: dayjs.Dayjs
  notes?: string
}

export function AttendancePage() {
  const [date, setDate]       = useState(dayjs().format('YYYY-MM-DD'))
  const [markOpen, setMarkOpen] = useState(false)
  const [form] = Form.useForm<MarkForm>()

  const { data: records = [], isLoading } = useAttendanceByDate(date)
  const { data: employeeData }            = useEmployees(undefined, undefined, 0)
  const { mutate: mark, isPending }       = useMarkAttendance()

  const employees = employeeData?.content ?? []

  const columns: ColumnsType<AttendanceRecord> = [
    { title: 'Employee ID',  dataIndex: 'employeeId',     width: 300,
      render: (v: string) => <code style={{ fontSize: 12 }}>{v}</code> },
    { title: 'Date',         dataIndex: 'attendanceDate',
      render: (v: string) => dayjs(v).format('DD MMM YYYY') },
    { title: 'Check In',     dataIndex: 'checkIn',  render: (v?: string) => v ?? '—' },
    { title: 'Check Out',    dataIndex: 'checkOut', render: (v?: string) => v ?? '—' },
    { title: 'Status',       dataIndex: 'status',
      render: (v: AttendanceStatus) => <Tag color={STATUS_COLOR[v]}>{v.replace('_', ' ')}</Tag> },
    { title: 'Notes',        dataIndex: 'notes', render: (v?: string) => v ?? '—' },
  ]

  const onMark = (values: MarkForm) => {
    mark({
      employeeId:     values.employeeId,
      attendanceDate: date,
      status:         values.status,
      checkIn:        values.checkIn?.format('HH:mm:ss'),
      checkOut:       values.checkOut?.format('HH:mm:ss'),
      notes:          values.notes,
    }, { onSuccess: () => { form.resetFields(); setMarkOpen(false) } })
  }

  return (
    <>
      <PageHeader
        title="Attendance"
        subtitle="Daily staff attendance tracking"
        extra={
          <Button type="primary" icon={<CheckOutlined />} onClick={() => setMarkOpen(true)}>
            Mark Attendance
          </Button>
        }
      />

      <Card
        title={
          <Space>
            <span>Records for</span>
            <DatePicker
              defaultValue={dayjs()}
              format="DD MMM YYYY"
              onChange={d => setDate(d ? d.format('YYYY-MM-DD') : dayjs().format('YYYY-MM-DD'))}
            />
            <Tag>{records.length} records</Tag>
          </Space>
        }
      >
        <Table rowKey="id" size="small" columns={columns}
          dataSource={records} loading={isLoading} pagination={false} />
      </Card>

      <Modal title="Mark Attendance" open={markOpen} onCancel={() => setMarkOpen(false)}
        onOk={() => form.submit()} okText="Mark" confirmLoading={isPending} destroyOnHidden>
        <Form form={form} layout="vertical" onFinish={onMark}>
          <Form.Item name="employeeId" label="Employee" rules={[{ required: true }]}>
            <Select showSearch placeholder="Select employee"
              filterOption={(input, opt) =>
                (opt?.label as string ?? '').toLowerCase().includes(input.toLowerCase())}
              options={employees.map(e => ({
                value: e.id,
                label: `${e.employeeCode} — ${e.firstName} ${e.lastName}`,
              }))} />
          </Form.Item>
          <Form.Item name="status" label="Status" rules={[{ required: true }]} initialValue="PRESENT">
            <Select options={['PRESENT','ABSENT','HALF_DAY','ON_LEAVE','HOLIDAY']
              .map(v => ({ value: v, label: v.replace('_', ' ') }))} />
          </Form.Item>
          <Space>
            <Form.Item name="checkIn" label="Check In">
              <TimePicker format="HH:mm" />
            </Form.Item>
            <Form.Item name="checkOut" label="Check Out">
              <TimePicker format="HH:mm" />
            </Form.Item>
          </Space>
          <Form.Item name="notes" label="Notes">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
