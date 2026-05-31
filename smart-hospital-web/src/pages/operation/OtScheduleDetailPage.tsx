import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Card, Descriptions, Tag, Button, Space, Row, Col, Modal, Form,
  Input, Select, Table, InputNumber, DatePicker,
} from 'antd'
import type { ColumnsType } from 'antd/es/table'
import {
  ArrowLeftOutlined, PlayCircleOutlined, CheckCircleOutlined,
  PauseCircleOutlined, CloseCircleOutlined, PlusOutlined, DeleteOutlined,
} from '@ant-design/icons'
import dayjs from 'dayjs'
import { PageHeader } from '@/components/common'
import {
  useOtSchedule, useStartOperation, useCompleteOperation,
  usePostponeOperation, useCancelOperation,
} from '@/hooks/useOperation'
import { useInventoryItems } from '@/hooks/useInventory'
import { useAuthStore } from '@/store/authStore'
import type {
  OtStatus, OtOutcome, PatientCondition, AnesthesiaType,
  CompleteOperationPayload, OtConsumable,
} from '@/types'

const STATUS_COLOR: Record<OtStatus, string> = {
  SCHEDULED: 'blue', IN_PROGRESS: 'orange', COMPLETED: 'success',
  POSTPONED: 'warning', CANCELLED: 'error',
}

const OUTCOME_COLOR: Record<OtOutcome, string> = {
  SUCCESSFUL: 'success', COMPLICATED: 'warning', INCOMPLETE: 'error',
}

interface ConsumableRow { itemId: string; itemName: string; itemUnit: string; quantityUsed: number }

export function OtScheduleDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { hasPermission } = useAuthStore()
  const [completeOpen, setCompleteOpen] = useState(false)
  const [consumables, setConsumables]   = useState<ConsumableRow[]>([])
  const [selectedItem, setSelectedItem] = useState<string | undefined>()
  const [itemQty, setItemQty]           = useState(1)
  const [form] = Form.useForm()

  const { data: schedule, isLoading } = useOtSchedule(id!)
  const { mutate: start, isPending: starting }       = useStartOperation(id!)
  const { mutate: complete, isPending: completing }  = useCompleteOperation(id!)
  const { mutate: postpone, isPending: postponing }  = usePostponeOperation(id!)
  const { mutate: cancel, isPending: cancelling }    = useCancelOperation(id!)
  const { data: invItems } = useInventoryItems({ page: 0 })

  if (isLoading || !schedule) return <Card loading />

  const canEdit = hasPermission('OPERATION.EDIT')
  const isActive = schedule.status === 'SCHEDULED' || schedule.status === 'IN_PROGRESS'

  const addConsumable = () => {
    const item = invItems?.content.find(i => i.id === selectedItem)
    if (!item) return
    setConsumables(prev => {
      const existing = prev.find(r => r.itemId === item.id)
      if (existing) return prev.map(r => r.itemId === item.id
        ? { ...r, quantityUsed: r.quantityUsed + itemQty } : r)
      return [...prev, { itemId: item.id, itemName: item.name, itemUnit: item.unit, quantityUsed: itemQty }]
    })
    setSelectedItem(undefined); setItemQty(1)
  }

  const removeConsumable = (itemId: string) =>
    setConsumables(prev => prev.filter(r => r.itemId !== itemId))

  const onCompleteFinish = (values: CompleteOperationPayload & {
    actualStartPicker: dayjs.Dayjs; actualEndPicker: dayjs.Dayjs
  }) => {
    const payload: CompleteOperationPayload = {
      actualStart: values.actualStartPicker.toISOString(),
      actualEnd:   values.actualEndPicker.toISOString(),
      anesthesiaType: values.anesthesiaType,
      postOpDiagnosis: values.postOpDiagnosis,
      procedureDetails: values.procedureDetails,
      complications: values.complications,
      surgeonNotes: values.surgeonNotes,
      outcome: values.outcome,
      patientConditionAfter: values.patientConditionAfter,
      consumables: consumables.map(({ itemId, quantityUsed }) => ({ itemId, quantityUsed })),
    }
    complete(payload, { onSuccess: () => { setCompleteOpen(false); setConsumables([]) } })
  }

  const consumableTableCols: ColumnsType<ConsumableRow> = [
    { title: 'Item',      dataIndex: 'itemName' },
    { title: 'Unit',      dataIndex: 'itemUnit', width: 80 },
    { title: 'Qty Used',  dataIndex: 'quantityUsed', width: 90, align: 'right' },
    {
      title: '', key: 'del', width: 50,
      render: (_: unknown, r: ConsumableRow) => (
        <Button danger size="small" icon={<DeleteOutlined />}
          onClick={() => removeConsumable(r.itemId)} />
      ),
    },
  ]

  const recordedCols: ColumnsType<OtConsumable> = [
    { title: 'Item',      dataIndex: 'itemName' },
    { title: 'Unit',      dataIndex: 'itemUnit', width: 80 },
    { title: 'Qty Used',  dataIndex: 'quantityUsed', width: 90, align: 'right' },
  ]

  return (
    <>
      <PageHeader
        title={`Operation — ${schedule.scheduleNumber}`}
        subtitle={schedule.patientName}
        extra={
          <Space>
            <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/operation/schedules')}>
              Back
            </Button>
            {canEdit && schedule.status === 'SCHEDULED' && (
              <Button type="primary" icon={<PlayCircleOutlined />}
                loading={starting} onClick={() => start()}>
                Start Operation
              </Button>
            )}
            {canEdit && schedule.status === 'IN_PROGRESS' && (
              <Button type="primary" icon={<CheckCircleOutlined />}
                style={{ background: '#52c41a', borderColor: '#52c41a' }}
                onClick={() => { setCompleteOpen(true); form.resetFields(); setConsumables([]) }}>
                Complete Operation
              </Button>
            )}
            {canEdit && schedule.status === 'SCHEDULED' && (
              <Button icon={<PauseCircleOutlined />} loading={postponing}
                onClick={() => Modal.confirm({
                  title: 'Postpone Operation',
                  content: 'Mark this operation as postponed?',
                  onOk: () => postpone(),
                })}>
                Postpone
              </Button>
            )}
            {canEdit && isActive && (
              <Button danger icon={<CloseCircleOutlined />} loading={cancelling}
                onClick={() => Modal.confirm({
                  title: 'Cancel Operation',
                  content: 'Cancel this operation? This cannot be undone.',
                  okType: 'danger', okText: 'Cancel Operation',
                  onOk: () => cancel(),
                })}>
                Cancel
              </Button>
            )}
          </Space>
        }
      />

      {/* Schedule Info */}
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col xs={24} md={14}>
          <Card title="Procedure Details">
            <Descriptions bordered size="small" column={2}>
              <Descriptions.Item label="Status" span={2}>
                <Tag color={STATUS_COLOR[schedule.status]}>{schedule.status.replace('_', ' ')}</Tag>
              </Descriptions.Item>
              <Descriptions.Item label="Theatre">{schedule.theatreName}</Descriptions.Item>
              <Descriptions.Item label="Type">
                <Tag color={schedule.operationType === 'EMERGENCY' ? 'red' : 'default'}>
                  {schedule.operationType}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="Scheduled Start">
                {dayjs(schedule.scheduledStart).format('DD MMM YYYY, HH:mm')}
              </Descriptions.Item>
              <Descriptions.Item label="Est. Duration">{schedule.estimatedDurationMins} min</Descriptions.Item>
              <Descriptions.Item label="Procedure" span={2}>
                <strong>{schedule.procedureName}</strong>
              </Descriptions.Item>
              {schedule.preOpDiagnosis && (
                <Descriptions.Item label="Pre-op Diagnosis" span={2}>
                  {schedule.preOpDiagnosis}
                </Descriptions.Item>
              )}
              {schedule.bloodRequestNumber && (
                <Descriptions.Item label="Blood Request" span={2}>
                  <Tag color="red">{schedule.bloodRequestNumber}</Tag>
                </Descriptions.Item>
              )}
              {schedule.notes && (
                <Descriptions.Item label="Notes" span={2}>{schedule.notes}</Descriptions.Item>
              )}
            </Descriptions>
          </Card>
        </Col>
        <Col xs={24} md={10}>
          <Card title="OT Team">
            <Descriptions bordered size="small" column={1}>
              <Descriptions.Item label="Lead Surgeon">
                {schedule.surgeonName ?? <span style={{ color: '#bbb' }}>Not assigned</span>}
              </Descriptions.Item>
              <Descriptions.Item label="Anesthetist">
                {schedule.anesthetistName ?? <span style={{ color: '#bbb' }}>Not assigned</span>}
              </Descriptions.Item>
              <Descriptions.Item label="Assistants">
                {schedule.assistantNames ?? <span style={{ color: '#bbb' }}>None listed</span>}
              </Descriptions.Item>
            </Descriptions>
          </Card>
        </Col>
      </Row>

      {/* Post-op (only when completed) */}
      {schedule.status === 'COMPLETED' && (
        <Row gutter={16}>
          <Col xs={24} md={14}>
            <Card title="Post-op Record" style={{ marginBottom: 16 }}>
              <Descriptions bordered size="small" column={2}>
                <Descriptions.Item label="Outcome">
                  <Tag color={OUTCOME_COLOR[schedule.outcome!]}>{schedule.outcome}</Tag>
                </Descriptions.Item>
                <Descriptions.Item label="Patient Condition">
                  <Tag color={schedule.patientConditionAfter === 'STABLE' ? 'success'
                    : schedule.patientConditionAfter === 'CRITICAL' ? 'error' : 'default'}>
                    {schedule.patientConditionAfter}
                  </Tag>
                </Descriptions.Item>
                <Descriptions.Item label="Actual Start">
                  {schedule.actualStart ? dayjs(schedule.actualStart).format('HH:mm') : '—'}
                </Descriptions.Item>
                <Descriptions.Item label="Actual End">
                  {schedule.actualEnd ? dayjs(schedule.actualEnd).format('HH:mm') : '—'}
                </Descriptions.Item>
                <Descriptions.Item label="Anesthesia">
                  {schedule.anesthesiaType ?? '—'}
                </Descriptions.Item>
                <Descriptions.Item label="Post-op Diagnosis">
                  {schedule.postOpDiagnosis ?? '—'}
                </Descriptions.Item>
                {schedule.procedureDetails && (
                  <Descriptions.Item label="Procedure Details" span={2}>
                    {schedule.procedureDetails}
                  </Descriptions.Item>
                )}
                {schedule.complications && (
                  <Descriptions.Item label="Complications" span={2}>
                    <span style={{ color: '#ff4d4f' }}>{schedule.complications}</span>
                  </Descriptions.Item>
                )}
                {schedule.surgeonNotes && (
                  <Descriptions.Item label="Surgeon Notes" span={2}>
                    {schedule.surgeonNotes}
                  </Descriptions.Item>
                )}
              </Descriptions>
            </Card>
          </Col>
          <Col xs={24} md={10}>
            <Card title={`Consumables Used (${schedule.consumables.length} items)`}>
              <Table rowKey="id" size="small" dataSource={schedule.consumables}
                columns={recordedCols} pagination={false}
                locale={{ emptyText: 'No consumables recorded' }} />
            </Card>
          </Col>
        </Row>
      )}

      {/* Complete Operation Modal */}
      <Modal
        title="Complete Operation"
        open={completeOpen}
        onCancel={() => setCompleteOpen(false)}
        onOk={() => form.submit()}
        okText="Complete & Save"
        confirmLoading={completing}
        width={680}
        destroyOnHidden
      >
        <Form form={form} layout="vertical" onFinish={onCompleteFinish}>
          <Space style={{ width: '100%' }} size={12}>
            <Form.Item name="actualStartPicker" label="Actual Start" style={{ flex: 1 }}
              rules={[{ required: true }]}
              initialValue={dayjs()}>
              <DatePicker showTime format="DD/MM/YYYY HH:mm" style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item name="actualEndPicker" label="Actual End" style={{ flex: 1 }}
              rules={[{ required: true }]}
              initialValue={dayjs()}>
              <DatePicker showTime format="DD/MM/YYYY HH:mm" style={{ width: '100%' }} />
            </Form.Item>
          </Space>

          <Space style={{ width: '100%' }} size={12}>
            <Form.Item name="anesthesiaType" label="Anesthesia" style={{ width: 160 }}>
              <Select allowClear options={['GENERAL','SPINAL','EPIDURAL','LOCAL','REGIONAL'].map(v => ({ value: v, label: v }))} />
            </Form.Item>
            <Form.Item name="outcome" label="Outcome" style={{ width: 160 }} rules={[{ required: true }]}>
              <Select options={[
                { value: 'SUCCESSFUL',  label: '✅ Successful' },
                { value: 'COMPLICATED', label: '⚠️ Complicated' },
                { value: 'INCOMPLETE',  label: '❌ Incomplete' },
              ]} />
            </Form.Item>
            <Form.Item name="patientConditionAfter" label="Patient Condition" style={{ flex: 1 }}
              rules={[{ required: true }]}>
              <Select options={[
                { value: 'STABLE',   label: 'Stable' },
                { value: 'CRITICAL', label: 'Critical' },
                { value: 'DECEASED', label: 'Deceased' },
              ]} />
            </Form.Item>
          </Space>

          <Form.Item name="postOpDiagnosis" label="Post-op Diagnosis">
            <Input />
          </Form.Item>
          <Form.Item name="procedureDetails" label="Procedure Details">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="complications" label="Complications">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="surgeonNotes" label="Surgeon Notes">
            <Input.TextArea rows={2} />
          </Form.Item>

          {/* Consumables */}
          <Card size="small" title="Consumables Used" style={{ marginTop: 8 }}>
            <Space style={{ width: '100%', marginBottom: 8 }} size={8}>
              <Select
                showSearch filterOption={false}
                placeholder="Search inventory item…"
                style={{ width: 280 }}
                value={selectedItem}
                onChange={setSelectedItem}
                options={(invItems?.content ?? []).map(i => ({
                  value: i.id,
                  label: `${i.itemCode} — ${i.name} (${i.currentStock} ${i.unit})`,
                }))}
              />
              <InputNumber min={1} value={itemQty} onChange={v => setItemQty(v ?? 1)} style={{ width: 80 }} />
              <Button icon={<PlusOutlined />} onClick={addConsumable} disabled={!selectedItem}>Add</Button>
            </Space>
            <Table rowKey="itemId" size="small" dataSource={consumables}
              columns={consumableTableCols} pagination={false}
              locale={{ emptyText: 'No consumables added yet' }} />
          </Card>
        </Form>
      </Modal>
    </>
  )
}
