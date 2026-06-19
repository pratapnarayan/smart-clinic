import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Card, Descriptions, Tag, Button, Table, Form, Input,
  Select, InputNumber, DatePicker, Modal, Space, Divider, Row, Col,
} from 'antd'
import { ArrowLeftOutlined, PlusOutlined, LogoutOutlined, DollarCircleOutlined, TagOutlined, WalletOutlined, CalendarOutlined } from '@ant-design/icons'
import { KpiCard } from '@/components/analytics'
import dayjs from 'dayjs'
import { PageHeader } from '@/components/common'
import { useIpdAdmission, useDischargePatient, useAddIpdCharge } from '@/hooks/useIpd'
import { useAuthStore } from '@/store/authStore'
import type { IpdCharge, DischargePatientPayload, AddIpdChargePayload, AdmissionStatus } from '@/types'

const STATUS_COLOR: Record<AdmissionStatus, string> = {
  ADMITTED: 'processing', TRANSFERRED: 'warning', DISCHARGED: 'success', DECEASED: 'default',
}

const CHARGE_CATEGORIES = [
  'BED_CHARGE', 'NURSING', 'DOCTOR_VISIT', 'PROCEDURE', 'MEDICINE', 'OTHER',
].map(v => ({ value: v, label: v.replace('_', ' ') }))

export function IpdAdmissionPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { hasPermission } = useAuthStore()
  const [dischargeOpen, setDischargeOpen] = useState(false)
  const [chargeOpen, setChargeOpen]       = useState(false)
  const [dischargeForm] = Form.useForm<DischargePatientPayload>()
  const [chargeForm]    = Form.useForm<AddIpdChargePayload>()

  const { data: admission, isLoading } = useIpdAdmission(id!)
  const { mutate: discharge, isPending: discharging } = useDischargePatient(id!)
  const { mutate: addCharge, isPending: addingCharge } = useAddIpdCharge(id!)

  if (isLoading || !admission) return <Card loading />

  const canEdit    = hasPermission('IPD.EDIT')
  const isAdmitted = admission.status === 'ADMITTED'

  const chargeColumns = [
    { title: 'Date',        dataIndex: 'chargeDate', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
    { title: 'Category',    dataIndex: 'category',   render: (v: string) => v.replace('_', ' ') },
    { title: 'Description', dataIndex: 'description' },
    { title: 'Amount',      dataIndex: 'amount',      render: (v: number) => `₹${v.toLocaleString('en-IN')}` },
  ]

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader
        title={`Admission — ${admission.admissionNumber}`}
        subtitle={admission.patientName}
        extra={
          <Space>
            <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/ipd')}>Back</Button>
            {canEdit && isAdmitted && (
              <>
                <Button icon={<PlusOutlined />} onClick={() => setChargeOpen(true)}>Add Charge</Button>
                <Button danger icon={<LogoutOutlined />} onClick={() => setDischargeOpen(true)}>
                  Discharge
                </Button>
              </>
            )}
          </Space>
        }
      />

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard title="Total Charges" value={`₹${admission.totalCharges.toLocaleString('en-IN')}`} icon={<DollarCircleOutlined />} color="primary" />
        <KpiCard title="Discount" value={`₹${admission.discount.toLocaleString('en-IN')}`} icon={<TagOutlined />} color="warning" />
        <KpiCard title="Net Amount" value={`₹${admission.netAmount.toLocaleString('en-IN')}`} icon={<WalletOutlined />} color="success" />
        <KpiCard
          title="Days Admitted"
          value={String(Math.max(1, Math.ceil((
            (admission.dischargeDate ? new Date(admission.dischargeDate) : new Date()).getTime()
            - new Date(admission.admissionDate).getTime()
          ) / 86400000)))}
          icon={<CalendarOutlined />}
          color="cyan"
        />
      </div>

      <Card title="Admission Details" className="medical-card">
        <Descriptions bordered column={2} size="small">
          <Descriptions.Item label="Status">
            <Tag color={STATUS_COLOR[admission.status]}>{admission.status}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Payment">
            <Tag color={admission.paymentStatus === 'PAID' ? 'success' : 'warning'}>
              {admission.paymentStatus}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Admitted On">
            {dayjs(admission.admissionDate).format('DD MMM YYYY, hh:mm A')}
          </Descriptions.Item>
          <Descriptions.Item label="Doctor">{admission.doctorName ?? '—'}</Descriptions.Item>
          <Descriptions.Item label="Admission Diagnosis" span={2}>
            {admission.admissionDiagnosis ?? '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Notes" span={2}>{admission.notes ?? '—'}</Descriptions.Item>
          {admission.status === 'DISCHARGED' && (
            <>
              <Descriptions.Item label="Discharged On">
                {admission.dischargeDate ? dayjs(admission.dischargeDate).format('DD MMM YYYY, hh:mm A') : '—'}
              </Descriptions.Item>
              <Descriptions.Item label="Condition">{admission.conditionAtDischarge ?? '—'}</Descriptions.Item>
              <Descriptions.Item label="Final Diagnosis" span={2}>{admission.finalDiagnosis ?? '—'}</Descriptions.Item>
              <Descriptions.Item label="Follow-up" span={2}>{admission.followUpInstructions ?? '—'}</Descriptions.Item>
            </>
          )}
        </Descriptions>
      </Card>

      <Card title={`Charges (${admission.charges.length})`} className="medical-card">
        <Table
          rowKey="id"
          size="small"
          columns={chargeColumns}
          dataSource={admission.charges}
          pagination={false}
          summary={() => (
            <Table.Summary.Row>
              <Table.Summary.Cell index={0} colSpan={3}><strong>Total</strong></Table.Summary.Cell>
              <Table.Summary.Cell index={1}>
                <strong>₹{admission.totalCharges.toLocaleString('en-IN')}</strong>
              </Table.Summary.Cell>
            </Table.Summary.Row>
          )}
        />
      </Card>

      {/* Discharge Modal */}
      <Modal
        title="Discharge Patient"
        open={dischargeOpen}
        onCancel={() => setDischargeOpen(false)}
        onOk={() => dischargeForm.submit()}
        okText="Discharge"
        okButtonProps={{ danger: true }}
        confirmLoading={discharging}
        destroyOnHidden
      >
        <Form form={dischargeForm} layout="vertical"
          onFinish={(v) => discharge(v, { onSuccess: () => setDischargeOpen(false) })}>
          <Form.Item name="conditionAtDischarge" label="Condition at Discharge" rules={[{ required: true }]}>
            <Select options={['STABLE','IMPROVED','CRITICAL','UNCHANGED','DECEASED']
              .map(v => ({ value: v, label: v }))} />
          </Form.Item>
          <Form.Item name="finalDiagnosis" label="Final Diagnosis">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="dischargeNotes" label="Discharge Notes">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="followUpInstructions" label="Follow-up Instructions">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>

      {/* Add Charge Modal */}
      <Modal
        title="Add Charge"
        open={chargeOpen}
        onCancel={() => setChargeOpen(false)}
        onOk={() => chargeForm.submit()}
        okText="Add"
        confirmLoading={addingCharge}
        destroyOnHidden
      >
        <Form form={chargeForm} layout="vertical"
          onFinish={(v) => addCharge(
            { ...v, chargeDate: v.chargeDate ? dayjs(v.chargeDate).format('YYYY-MM-DD') : undefined },
            { onSuccess: () => { chargeForm.resetFields(); setChargeOpen(false) } }
          )}>
          <Form.Item name="category" label="Category" rules={[{ required: true }]}>
            <Select options={CHARGE_CATEGORIES} />
          </Form.Item>
          <Form.Item name="description" label="Description" rules={[{ required: true }]}>
            <Input placeholder="e.g. Room charge, Dressing" />
          </Form.Item>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="amount" label="Amount (₹)" rules={[{ required: true }]}>
                <InputNumber min={0} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="chargeDate" label="Charge Date">
                <DatePicker style={{ width: '100%' }} defaultValue={dayjs()} />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    </div>
  )
}
