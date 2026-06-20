import { useState } from 'react'
import {
  Table, Tag, Button, Select, Card, Space, Modal, Form,
  Input, DatePicker, Popconfirm,
} from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { PageHeader } from '@/components/common'
import { useLeaves, useApplyLeave, useApproveLeave, useRejectLeave } from '@/hooks/useHr'
import { useAuthStore } from '@/store/authStore'
import type { LeaveRequest, LeaveStatus, ApplyLeavePayload } from '@/types'

const STATUS_COLOR: Record<LeaveStatus, string> = {
  PENDING: 'processing', APPROVED: 'success', REJECTED: 'error', CANCELLED: 'default',
}

interface ApplyForm extends Omit<ApplyLeavePayload, 'fromDate' | 'toDate'> {
  _range: [dayjs.Dayjs, dayjs.Dayjs]
}

export function LeaveRequestPage() {
  const { hasPermission } = useAuthStore()
  const [status, setStatus]   = useState<LeaveStatus | undefined>('PENDING')
  const [applyOpen, setApplyOpen] = useState(false)
  const [approveNote, setApproveNote] = useState('')
  const [form] = Form.useForm<ApplyForm>()

  const { data, isLoading }       = useLeaves(status)
  const { mutate: apply, isPending } = useApplyLeave()
  const { mutate: approve }       = useApproveLeave()
  const { mutate: reject }        = useRejectLeave()

  const canManage = hasPermission('HR.MANAGE')

  const columns: ColumnsType<LeaveRequest> = [
    { title: 'Leave No.',    dataIndex: 'leaveNumber', width: 140 },
    { title: 'Employee',     dataIndex: 'employeeName' },
    { title: 'Type',         dataIndex: 'leaveType',
      render: (v: string) => <Tag>{v}</Tag> },
    { title: 'From',         dataIndex: 'fromDate',
      render: (v: string) => dayjs(v).format('DD MMM YYYY') },
    { title: 'To',           dataIndex: 'toDate',
      render: (v: string) => dayjs(v).format('DD MMM YYYY') },
    { title: 'Days',         dataIndex: 'totalDays' },
    { title: 'Status',       dataIndex: 'status',
      render: (v: LeaveStatus) => <Tag color={STATUS_COLOR[v]}>{v}</Tag> },
    canManage ? {
      title: 'Actions',
      key: 'actions',
      render: (_: unknown, r: LeaveRequest) => r.status === 'PENDING' ? (
        <Space size="small">
          <Popconfirm title="Approve this leave?" onConfirm={() => approve({ id: r.id })}>
            <Button size="small" type="primary" ghost>Approve</Button>
          </Popconfirm>
          <Popconfirm title="Reject this leave?" onConfirm={() => reject({ id: r.id })}>
            <Button size="small" danger>Reject</Button>
          </Popconfirm>
        </Space>
      ) : null,
    } : {},
  ].filter(c => Object.keys(c).length > 0)

  const onApply = (values: ApplyForm) => {
    const { _range, ...rest } = values
    apply({
      ...rest,
      fromDate: _range[0].format('YYYY-MM-DD'),
      toDate:   _range[1].format('YYYY-MM-DD'),
    }, { onSuccess: () => { form.resetFields(); setApplyOpen(false) } })
  }

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader
        title="Leave Requests"
        subtitle="Apply and manage staff leave"
        extra={
          hasPermission('HR.CREATE') && (
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setApplyOpen(true)}>
              Apply Leave
            </Button>
          )
        }
      />

      <Card className="medical-card"
        title={
          <Select
            value={status}
            style={{ width: 160 }}
            allowClear
            placeholder="All statuses"
            onChange={v => setStatus(v as LeaveStatus | undefined)}
            options={['PENDING','APPROVED','REJECTED','CANCELLED']
              .map(v => ({ value: v, label: v }))}
          />
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

      <Modal title="Apply for Leave" open={applyOpen} onCancel={() => setApplyOpen(false)}
        onOk={() => form.submit()} okText="Submit" confirmLoading={isPending} destroyOnHidden>
        <Form form={form} layout="vertical" onFinish={onApply}>
          <Form.Item name="employeeId" label="Employee ID" rules={[{ required: true }]}>
            <Input placeholder="Paste employee UUID" />
          </Form.Item>
          <Form.Item name="leaveType" label="Leave Type" rules={[{ required: true }]}>
            <Select options={['CASUAL','SICK','EARNED','MATERNITY','PATERNITY','UNPAID']
              .map(v => ({ value: v, label: v }))} />
          </Form.Item>
          <Form.Item name="_range" label="Date Range" rules={[{ required: true }]}>
            <DatePicker.RangePicker style={{ width: '100%' }}
              disabledDate={d => d.isBefore(dayjs(), 'day')} />
          </Form.Item>
          <Form.Item name="reason" label="Reason">
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
