import { useParams, useNavigate } from 'react-router-dom'
import {
  Card, Descriptions, Tag, Button, Tabs, Table, Form,
  Input, Select, Spin, Alert, Divider, Space, type TableProps
} from 'antd'
import { PlusOutlined, SaveOutlined, ArrowLeftOutlined } from '@ant-design/icons'
import { useVisit, useUpdateVisit, useSavePrescription } from '@/hooks/useOpdVisits'
import { PageHeader } from '@/components/common/PageHeader'
import { formatDate, formatCurrency } from '@/utils'
import type { PrescriptionRequest, OpdCharge, PrescriptionItem } from '@/types'

export function OpdVisitPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()

  const { data: visit, isLoading, isError } = useVisit(id!)
  const { mutate: updateVisit, isPending: saving } = useUpdateVisit(id!)
  const { mutate: savePrescription, isPending: savingRx } = useSavePrescription(id!)

  const [diagnosisForm] = Form.useForm()
  const [rxForm] = Form.useForm<PrescriptionRequest>()

  if (isLoading) return <Spin size="large" style={{ display: 'block', marginTop: 80 }} />
  if (isError || !visit) return <Alert type="error" message="Visit not found" />

  const chargeColumns: TableProps<OpdCharge>['columns'] = [
    { title: 'Description', dataIndex: 'description' },
    { title: 'Category', dataIndex: 'category', render: (v) => v ?? '—' },
    { title: 'Amount', dataIndex: 'amount', render: formatCurrency, align: 'right' },
  ]

  const rxItemColumns: TableProps<PrescriptionItem>['columns'] = [
    { title: 'Medicine', dataIndex: 'medicineName' },
    { title: 'Dose', dataIndex: 'dose' },
    { title: 'Frequency', dataIndex: 'frequency' },
    { title: 'Duration', dataIndex: 'duration' },
    { title: 'Instructions', dataIndex: 'instructions', render: (v) => v ?? '—' },
  ]

  const statusColors: Record<string, string> = {
    COMPLETED: 'green', IN_PROGRESS: 'blue', REGISTERED: 'orange', CANCELLED: 'red',
  }

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader
        title={visit.visitNumber}
        subtitle={`${visit.patientName} · ${formatDate(visit.visitDate)}`}
        breadcrumbs={[
          { title: 'OPD', href: '/opd' },
          { title: visit.visitNumber },
        ]}
        extra={
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/opd')}>
            Back
          </Button>
        }
      />

      {/* Visit summary */}
      <Card className="medical-card">
        <Descriptions bordered size="small" column={{ xs: 1, sm: 2, lg: 3 }}>
          <Descriptions.Item label="Visit Status">
            <Tag color={statusColors[visit.visitStatus]}>{visit.visitStatus}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Payment">
            <Tag color={visit.paymentStatus === 'PAID' ? 'green' : 'orange'}>
              {visit.paymentStatus}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Department">{visit.department ?? '—'}</Descriptions.Item>
          <Descriptions.Item label="Doctor">{visit.doctorName ?? '—'}</Descriptions.Item>
          <Descriptions.Item label="Symptoms" span={2}>{visit.symptoms ?? '—'}</Descriptions.Item>
          <Descriptions.Item label="Diagnosis" span={2}>{visit.diagnosis ?? '—'}</Descriptions.Item>
        </Descriptions>

        <Divider />

        {/* Billing summary */}
        <div className="flex justify-end">
          <Descriptions size="small" column={1} style={{ width: 280 }}>
            <Descriptions.Item label="Consultation Fee">
              {formatCurrency(visit.consultationFee)}
            </Descriptions.Item>
            <Descriptions.Item label="Other Charges">
              {formatCurrency(visit.totalCharges - visit.consultationFee)}
            </Descriptions.Item>
            <Descriptions.Item label="Discount">
              {formatCurrency(visit.discount)}
            </Descriptions.Item>
            <Descriptions.Item label={<strong>Net Amount</strong>}>
              <strong>{formatCurrency(visit.netAmount)}</strong>
            </Descriptions.Item>
          </Descriptions>
        </div>
      </Card>

      <Tabs defaultActiveKey="diagnosis" items={[
        // ── Diagnosis / Notes ──────────────────────────────────────────────
        {
          key: 'diagnosis',
          label: 'Clinical Notes',
          children: (
            <Card className="medical-card">
              <Form
                form={diagnosisForm}
                layout="vertical"
                initialValues={visit}
                onFinish={(v) => updateVisit(v)}
              >
                <Form.Item name="diagnosis" label="Diagnosis">
                  <Input.TextArea rows={3} placeholder="Primary and secondary diagnoses…" />
                </Form.Item>
                <Form.Item name="notes" label="Doctor Notes">
                  <Input.TextArea rows={3} placeholder="Clinical observations…" />
                </Form.Item>
                <Form.Item name="visitStatus" label="Visit Status">
                  <Select options={[
                    { label: 'Registered',   value: 'REGISTERED' },
                    { label: 'In Progress',  value: 'IN_PROGRESS' },
                    { label: 'Completed',    value: 'COMPLETED' },
                    { label: 'Cancelled',    value: 'CANCELLED' },
                  ]} />
                </Form.Item>
                <Form.Item name="paymentStatus" label="Payment Status">
                  <Select options={[
                    { label: 'Pending', value: 'PENDING' },
                    { label: 'Paid',    value: 'PAID' },
                    { label: 'Partial', value: 'PARTIAL' },
                    { label: 'Waived',  value: 'WAIVED' },
                  ]} />
                </Form.Item>
                <Button type="primary" htmlType="submit" icon={<SaveOutlined />} loading={saving}>
                  Save Notes
                </Button>
              </Form>
            </Card>
          ),
        },

        // ── Charges ────────────────────────────────────────────────────────
        {
          key: 'charges',
          label: `Charges (${visit.charges.length})`,
          children: (
            <Card className="medical-card">
              <Table
                rowKey="id"
                size="small"
                dataSource={visit.charges}
                columns={chargeColumns}
                pagination={false}
                summary={() => (
                  <Table.Summary.Row>
                    <Table.Summary.Cell index={0} colSpan={2}>
                      <strong>Total</strong>
                    </Table.Summary.Cell>
                    <Table.Summary.Cell index={2} align="right">
                      <strong>{formatCurrency(visit.netAmount)}</strong>
                    </Table.Summary.Cell>
                  </Table.Summary.Row>
                )}
              />
            </Card>
          ),
        },

        // ── Prescription ───────────────────────────────────────────────────
        {
          key: 'prescription',
          label: 'Prescription',
          children: (
            <Card className="medical-card">
              {visit.prescription && (
                <>
                  <Alert
                    type="info"
                    showIcon
                    message={`Prescription saved · Follow-up in ${visit.prescription.followUpDays ?? '—'} days`}
                    description={visit.prescription.advice}
                    style={{ marginBottom: 16 }}
                  />
                  <Table
                    rowKey="id"
                    size="small"
                    dataSource={visit.prescription.items}
                    columns={rxItemColumns}
                    pagination={false}
                  />
                  <Divider />
                </>
              )}

              <Form
                form={rxForm}
                layout="vertical"
                initialValues={visit.prescription ?? undefined}
                onFinish={(values) => savePrescription(values)}
              >
                <Form.Item name="advice" label="Advice to Patient">
                  <Input.TextArea rows={2} placeholder="Rest, fluids, diet…" />
                </Form.Item>
                <Form.Item name="followUpDays" label="Follow-up (days)">
                  <Input type="number" style={{ width: 120 }} />
                </Form.Item>

                <Form.List name="items">
                  {(fields, { add, remove }) => (
                    <>
                      {fields.map(({ key, name }) => (
                        <Card key={key} size="small" className="medical-card" style={{ marginBottom: 8 }}
                          extra={<Button danger size="small" onClick={() => remove(name)}>Remove</Button>}
                        >
                          <Form.Item name={[name, 'medicineName']} label="Medicine" rules={[{ required: true }]}>
                            <Input placeholder="Paracetamol 500mg" />
                          </Form.Item>
                          <Space>
                            <Form.Item name={[name, 'dose']} label="Dose" rules={[{ required: true }]}>
                              <Input placeholder="1 tablet" />
                            </Form.Item>
                            <Form.Item name={[name, 'frequency']} label="Frequency" rules={[{ required: true }]}>
                              <Input placeholder="TDS" />
                            </Form.Item>
                            <Form.Item name={[name, 'duration']} label="Duration" rules={[{ required: true }]}>
                              <Input placeholder="5 days" />
                            </Form.Item>
                          </Space>
                          <Form.Item name={[name, 'instructions']} label="Instructions">
                            <Input placeholder="After meals" />
                          </Form.Item>
                        </Card>
                      ))}
                      <Button icon={<PlusOutlined />} onClick={() => add()} style={{ marginBottom: 16 }}>
                        Add Medicine
                      </Button>
                    </>
                  )}
                </Form.List>

                <Button type="primary" htmlType="submit" icon={<SaveOutlined />} loading={savingRx}>
                  Save Prescription
                </Button>
              </Form>
            </Card>
          ),
        },
      ]} />
    </div>
  )
}
