import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Card, Descriptions, Tag, Button, Table, Modal, Form,
  Input, Space, Row, Col, Statistic, Badge, Select,
} from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { ArrowLeftOutlined, CheckCircleOutlined, CloseCircleOutlined, PlayCircleOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { PageHeader } from '@/components/common'
import {
  useLabOrder, useCollectSample, useStartProcessing, useCancelLabOrder,
  useEnterResult, useUpdateLabPayment,
} from '@/hooks/usePathology'
import { useAuthStore } from '@/store/authStore'
import type { LabOrderItem, LabOrderStatus, LabItemStatus, EnterResultPayload } from '@/types'

const ORDER_STATUS_COLOR: Record<LabOrderStatus, string> = {
  PENDING: 'default', SAMPLE_COLLECTED: 'processing',
  IN_PROGRESS: 'warning', COMPLETED: 'success', CANCELLED: 'error',
}
const ITEM_STATUS_COLOR: Record<LabItemStatus, string> = {
  PENDING: 'default', IN_PROGRESS: 'processing', COMPLETED: 'success',
}

export function LabOrderPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { hasPermission } = useAuthStore()
  const [resultOpen, setResultOpen] = useState(false)
  const [activeItem, setActiveItem] = useState<LabOrderItem | null>(null)
  const [form] = Form.useForm<EnterResultPayload>()

  const { data: order, isLoading }     = useLabOrder(id!)
  const { mutate: collectSample,  isPending: collecting }  = useCollectSample(id!)
  const { mutate: startProcessing, isPending: starting }   = useStartProcessing(id!)
  const { mutate: cancelOrder,     isPending: cancelling } = useCancelLabOrder(id!)
  const { mutate: enterResult,     isPending: saving }     = useEnterResult(id!)
  const { mutate: updatePayment } = useUpdateLabPayment(id!)

  if (isLoading || !order) return <Card loading />

  const canEdit = hasPermission('PATHOLOGY.EDIT')
  const completedCount = order.items.filter(i => i.status === 'COMPLETED').length

  const columns: ColumnsType<LabOrderItem> = [
    { title: 'Code',         dataIndex: 'testCode', width: 100 },
    { title: 'Test',         dataIndex: 'testName' },
    { title: 'Normal Range', dataIndex: 'normalRange', render: (v?: string) => v ?? '—' },
    { title: 'Unit',         dataIndex: 'unit',        render: (v?: string) => v ?? '—' },
    {
      title: 'Result',
      dataIndex: 'result',
      render: (v?: string, r?: LabOrderItem) => v
        ? <span style={{ fontWeight: 600 }}>{v} {r?.unit}</span>
        : <span style={{ color: '#bbb' }}>—</span>,
    },
    {
      title: 'Status',
      dataIndex: 'status',
      render: (v: LabItemStatus) => (
        <Badge status={ITEM_STATUS_COLOR[v] as any} text={v.replace('_', ' ')} />
      ),
    },
    canEdit && order.status !== 'CANCELLED' && order.status !== 'COMPLETED' ? {
      title: 'Action',
      key: 'action',
      render: (_: unknown, r: LabOrderItem) => r.status !== 'COMPLETED' && (
        <Button size="small" icon={<CheckCircleOutlined />}
          onClick={() => { setActiveItem(r); form.resetFields(); setResultOpen(true) }}>
          Enter Result
        </Button>
      ),
    } : {},
  ].filter(c => Object.keys(c).length > 0)

  const onResultSubmit = (values: EnterResultPayload) => {
    if (!activeItem) return
    enterResult({ itemId: activeItem.id, payload: values },
      { onSuccess: () => { form.resetFields(); setResultOpen(false) } })
  }

  return (
    <>
      <PageHeader
        title={`Lab Order — ${order.orderNumber}`}
        subtitle={order.patientName}
        extra={
          <Space>
            <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/pathology')}>Back</Button>
            {canEdit && order.status === 'PENDING' && (
              <Button type="primary" icon={<CheckCircleOutlined />}
                loading={collecting} onClick={() => collectSample()}>
                Collect Sample
              </Button>
            )}
            {canEdit && order.status === 'SAMPLE_COLLECTED' && (
              <Button type="primary" icon={<PlayCircleOutlined />}
                loading={starting} onClick={() => startProcessing()}>
                Start Processing
              </Button>
            )}
            {canEdit && order.status !== 'COMPLETED' && order.status !== 'CANCELLED' && (
              <Button danger icon={<CloseCircleOutlined />}
                loading={cancelling}
                onClick={() => Modal.confirm({
                  title: 'Cancel Lab Order',
                  content: 'This cannot be undone. Cancel this order?',
                  okText: 'Cancel Order', okType: 'danger',
                  onOk: () => cancelOrder(),
                })}>
                Cancel Order
              </Button>
            )}
          </Space>
        }
      />

      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={5}><Card><Statistic title="Tests Ordered"   value={order.items.length} /></Card></Col>
        <Col span={5}><Card><Statistic title="Completed"       value={completedCount}      valueStyle={{ color: '#52c41a' }} /></Card></Col>
        <Col span={5}><Card><Statistic title="Total Amount"    value={`₹${order.totalAmount.toLocaleString('en-IN')}`} /></Card></Col>
        <Col span={5}><Card><Statistic title="Net Amount"      value={`₹${order.netAmount.toLocaleString('en-IN')}`}   valueStyle={{ color: '#1677ff' }} /></Card></Col>
        <Col span={4}>
          <Card>
            <div style={{ fontSize: 12, color: '#888', marginBottom: 4 }}>Payment</div>
            {canEdit ? (
              <Select value={order.paymentStatus} size="small" style={{ width: '100%' }}
                onChange={v => updatePayment(v as any)}
                options={['PENDING','PAID','PARTIAL','WAIVED'].map(v => ({ value: v, label: v }))} />
            ) : (
              <Tag color={order.paymentStatus === 'PAID' ? 'success' : 'default'}>
                {order.paymentStatus}
              </Tag>
            )}
          </Card>
        </Col>
      </Row>

      <Card title="Order Details" style={{ marginBottom: 16 }}>
        <Descriptions bordered column={3} size="small">
          <Descriptions.Item label="Status">
            <Tag color={ORDER_STATUS_COLOR[order.status]}>{order.status.replace(/_/g, ' ')}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Priority">
            <Tag color={order.priority === 'STAT' ? 'red' : order.priority === 'URGENT' ? 'orange' : 'default'}>
              {order.priority}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Source">
            <Tag>{order.sourceType.replace('_', ' ')}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Referred By">{order.referredByName ?? '—'}</Descriptions.Item>
          <Descriptions.Item label="Ordered At">
            {dayjs(order.createdAt).format('DD MMM YYYY, hh:mm A')}
          </Descriptions.Item>
          <Descriptions.Item label="Sample Collected">
            {order.sampleCollectedAt
              ? dayjs(order.sampleCollectedAt).format('DD MMM YYYY, hh:mm A')
              : '—'}
          </Descriptions.Item>
          {order.notes && (
            <Descriptions.Item label="Notes" span={3}>{order.notes}</Descriptions.Item>
          )}
        </Descriptions>
      </Card>

      <Card title={`Test Results (${completedCount}/${order.items.length} complete)`}>
        <Table rowKey="id" size="small" columns={columns}
          dataSource={order.items} pagination={false} />
      </Card>

      <Modal title={`Enter Result — ${activeItem?.testName}`}
        open={resultOpen} onCancel={() => setResultOpen(false)}
        onOk={() => form.submit()} okText="Save Result"
        confirmLoading={saving} destroyOnHidden>
        <Form form={form} layout="vertical" onFinish={onResultSubmit}>
          <Form.Item name="result" label={`Result${activeItem?.unit ? ` (${activeItem.unit})` : ''}`}
            rules={[{ required: true }]}>
            <Input placeholder="Enter result value" />
          </Form.Item>
          {activeItem?.normalRange && (
            <div style={{ color: '#888', marginBottom: 8, fontSize: 12 }}>
              Normal range: {activeItem.normalRange}
            </div>
          )}
          <Form.Item name="resultNote" label="Interpretation / Note">
            <Input.TextArea rows={2} placeholder="Normal / Abnormal / interpretation" />
          </Form.Item>
          <Form.Item name="enteredBy" label="Entered By">
            <Input placeholder="Lab technician name" />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
