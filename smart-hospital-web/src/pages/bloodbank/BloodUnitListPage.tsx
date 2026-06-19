import { useState } from 'react'
import {
  Table, Button, Tag, Select, Space, Card, Modal, Form, Input, InputNumber, DatePicker,
} from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { PlusOutlined, CheckOutlined, CloseOutlined, DeleteOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { PageHeader } from '@/components/common'
import { useBloodUnits, useAddBloodUnit, useUpdateUnitStatus } from '@/hooks/useBloodBank'
import { useAuthStore } from '@/store/authStore'
import type { BloodUnit, BloodGroup, ComponentType, UnitStatus, AddBloodUnitPayload } from '@/types'
import {
  BLOOD_GROUP_OPTIONS, COMPONENT_OPTIONS, BLOOD_GROUP_LABELS, COMPONENT_LABELS,
} from '@/types'

const STATUS_COLOR: Record<UnitStatus, string> = {
  PENDING_TESTING: 'processing', AVAILABLE: 'success',
  RESERVED: 'warning', ISSUED: 'default', DISCARDED: 'error', EXPIRED: 'volcano',
}

const TESTING_COLOR = { PENDING: 'default', CLEARED: 'success', REJECTED: 'error' } as const

function ExpiryTag({ date, expired }: { date: string; expired: boolean }) {
  const daysLeft = dayjs(date).diff(dayjs(), 'day')
  if (expired)      return <Tag color="error">Expired</Tag>
  if (daysLeft <= 7) return <Tag color="warning">{date} ({daysLeft}d)</Tag>
  return <span>{date}</span>
}

export function BloodUnitListPage() {
  const { hasPermission } = useAuthStore()
  const [bloodGroup, setBloodGroup]       = useState<BloodGroup | undefined>()
  const [componentType, setComponentType] = useState<ComponentType | undefined>()
  const [status, setStatus]               = useState<UnitStatus | undefined>()
  const [page, setPage]                   = useState(0)
  const [addOpen, setAddOpen]             = useState(false)
  const [form] = Form.useForm<AddBloodUnitPayload>()
  const [confirmUnit, setConfirmUnit]     = useState<{ id: string; action: UnitStatus } | null>(null)

  const params = { bloodGroup, componentType, status, page }
  const { data, isLoading }                 = useBloodUnits(params)
  const { mutate: addUnit, isPending }      = useAddBloodUnit()
  // per-row status update — we pass unitId dynamically
  const { mutate: updateStatus, isPending: updating } = useUpdateUnitStatus(confirmUnit?.id ?? '')

  const canEdit = hasPermission('BLOODBANK.EDIT')

  const columns: ColumnsType<BloodUnit> = [
    { title: 'Unit No.',    dataIndex: 'unitNumber',     width: 140 },
    {
      title: 'Blood Group', dataIndex: 'bloodGroupDisplay', width: 100,
      render: (v: string) => <Tag style={{ fontWeight: 700, fontSize: 14 }}>{v}</Tag>,
    },
    {
      title: 'Component', dataIndex: 'componentType', width: 150,
      render: (v: ComponentType) => COMPONENT_LABELS[v],
    },
    { title: 'Donor',     dataIndex: 'donorName',    render: (v?: string) => v ?? '—' },
    { title: 'Collected', dataIndex: 'collectionDate', width: 110 },
    {
      title: 'Expires', dataIndex: 'expiryDate', width: 150,
      render: (v: string, r: BloodUnit) => <ExpiryTag date={v} expired={r.expired} />,
    },
    {
      title: 'Testing', dataIndex: 'testingStatus', width: 100,
      render: (v: keyof typeof TESTING_COLOR) => <Tag color={TESTING_COLOR[v]}>{v}</Tag>,
    },
    {
      title: 'Status', dataIndex: 'status', width: 130,
      render: (v: UnitStatus) => (
        <Tag color={STATUS_COLOR[v]}>{v.replace('_', ' ')}</Tag>
      ),
    },
    canEdit ? {
      title: 'Actions', key: 'actions', width: 160,
      render: (_: unknown, r: BloodUnit) => (
        <Space size={4}>
          {r.status === 'PENDING_TESTING' && (
            <>
              <Button size="small" type="primary" icon={<CheckOutlined />}
                onClick={() => setConfirmUnit({ id: r.id, action: 'AVAILABLE' })}>
                Clear
              </Button>
              <Button size="small" danger icon={<CloseOutlined />}
                onClick={() => setConfirmUnit({ id: r.id, action: 'DISCARDED' })}>
                Reject
              </Button>
            </>
          )}
          {r.status === 'AVAILABLE' && (
            <Button size="small" danger icon={<DeleteOutlined />}
              onClick={() => setConfirmUnit({ id: r.id, action: 'DISCARDED' })}>
              Discard
            </Button>
          )}
        </Space>
      ),
    } : {},
  ].filter(c => Object.keys(c).length > 0)

  const onAddFinish = (values: AddBloodUnitPayload & { collectionDatePicker?: dayjs.Dayjs; expiryDatePicker?: dayjs.Dayjs }) => {
    const payload: AddBloodUnitPayload = {
      ...values,
      collectionDate: values.collectionDatePicker ? values.collectionDatePicker.format('YYYY-MM-DD') : undefined,
      expiryDate:     values.expiryDatePicker     ? values.expiryDatePicker.format('YYYY-MM-DD')     : undefined,
    }
    addUnit(payload, { onSuccess: () => { form.resetFields(); setAddOpen(false) } })
  }

  const actionLabel = confirmUnit?.action === 'AVAILABLE' ? 'Clear Testing' : 'Discard';
  const actionColor = confirmUnit?.action === 'AVAILABLE' ? 'primary' : 'danger' as const;

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader
        title="Blood Units"
        subtitle="Blood bag inventory — testing and status management"
        extra={
          hasPermission('BLOODBANK.CREATE') && (
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setAddOpen(true)}>
              Add Unit
            </Button>
          )
        }
      />

      <Card className="medical-card"
        title={
          <Space wrap>
            <Select allowClear placeholder="Blood group" style={{ width: 120 }}
              options={BLOOD_GROUP_OPTIONS}
              onChange={v => { setBloodGroup(v); setPage(0) }} />
            <Select allowClear placeholder="Component" style={{ width: 200 }}
              options={COMPONENT_OPTIONS}
              onChange={v => { setComponentType(v); setPage(0) }} />
            <Select allowClear placeholder="Status" style={{ width: 160 }}
              onChange={v => { setStatus(v); setPage(0) }}
              options={(['PENDING_TESTING','AVAILABLE','ISSUED','DISCARDED','EXPIRED'] as UnitStatus[])
                .map(v => ({ value: v, label: v.replace('_', ' ') }))} />
          </Space>
        }
      >
        <Table rowKey="id" size="small" columns={columns}
          dataSource={data?.content ?? []} loading={isLoading}
          pagination={{ current: page + 1, pageSize: 20, total: data?.total ?? 0,
            onChange: p => setPage(p - 1) }} />
      </Card>

      {/* Confirm status change */}
      <Modal
        title={actionLabel}
        open={!!confirmUnit}
        onCancel={() => setConfirmUnit(null)}
        confirmLoading={updating}
        onOk={() => {
          if (!confirmUnit) return
          updateStatus({ status: confirmUnit.action },
            { onSuccess: () => setConfirmUnit(null) })
        }}
        okText={actionLabel}
        okButtonProps={{ danger: confirmUnit?.action !== 'AVAILABLE' }}
      >
        <p>
          {confirmUnit?.action === 'AVAILABLE'
            ? 'Mark this unit as cleared and available for issue?'
            : 'Discard this unit? This action cannot be undone.'}
        </p>
      </Modal>

      {/* Add Unit Modal */}
      <Modal title="Add Blood Unit" open={addOpen} onCancel={() => setAddOpen(false)}
        onOk={() => form.submit()} okText="Add Unit" confirmLoading={isPending} width={540} destroyOnHidden>
        <Form form={form} layout="vertical" onFinish={onAddFinish}>
          <Space style={{ width: '100%' }} size={12}>
            <Form.Item name="bloodGroup" label="Blood Group" style={{ width: 130 }}
              rules={[{ required: true }]}>
              <Select options={BLOOD_GROUP_OPTIONS} />
            </Form.Item>
            <Form.Item name="componentType" label="Component" style={{ flex: 1 }}
              rules={[{ required: true }]}>
              <Select options={COMPONENT_OPTIONS} />
            </Form.Item>
            <Form.Item name="volumeMl" label="Volume (ml)" style={{ width: 110 }} initialValue={450}>
              <InputNumber min={1} style={{ width: '100%' }} />
            </Form.Item>
          </Space>

          <Space style={{ width: '100%' }} size={12}>
            <Form.Item name="collectionDatePicker" label="Collection Date" style={{ flex: 1 }}>
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item name="expiryDatePicker" label="Expiry Date" style={{ flex: 1 }}>
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
          </Space>

          <Space style={{ width: '100%' }} size={12}>
            <Form.Item name="donorName" label="Donor Name" style={{ flex: 1 }}>
              <Input placeholder="Donor full name (optional)" />
            </Form.Item>
          </Space>

          <Form.Item name="notes" label="Notes">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
