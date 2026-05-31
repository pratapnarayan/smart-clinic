import { useEffect } from 'react'
import { Modal, Form, Input, Select, Row, Col } from 'antd'
import { useIpdWards, useAvailableBeds, useAdmitPatient } from '@/hooks/useIpd'
import type { AdmitPatientPayload } from '@/types'

interface Props {
  open: boolean
  onClose: () => void
  patientId?: string
  patientName?: string
  opdVisitId?: string
}

export function IpdAdmissionFormModal({ open, onClose, patientId, patientName, opdVisitId }: Props) {
  const [form] = Form.useForm<AdmitPatientPayload & { wardId: string }>()
  const wardId = Form.useWatch('wardId', form)

  const { data: wards = [] }     = useIpdWards()
  const { data: beds  = [] }     = useAvailableBeds(wardId ?? '')
  const { mutate: admit, isPending } = useAdmitPatient()

  useEffect(() => {
    if (open) {
      form.resetFields()
      if (patientId) form.setFieldValue('patientId', patientId)
      if (opdVisitId) form.setFieldValue('opdVisitId', opdVisitId)
    }
  }, [open, patientId, opdVisitId, form])

  // Clear bed selection when ward changes
  useEffect(() => {
    form.setFieldValue('bedId', undefined)
  }, [wardId, form])

  const onFinish = (values: AdmitPatientPayload & { wardId: string }) => {
    admit(values, { onSuccess: onClose })
  }

  return (
    <Modal
      title="Admit Patient"
      open={open}
      onCancel={onClose}
      onOk={() => form.submit()}
      okText="Admit"
      confirmLoading={isPending}
      width={640}
      destroyOnHidden
    >
      <Form form={form} layout="vertical" onFinish={onFinish}>
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="patientId" label="Patient ID" rules={[{ required: true }]}>
              <Input placeholder="Paste patient UUID" disabled={!!patientId} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="opdVisitId" label="OPD Visit ID (optional)">
              <Input placeholder="Paste OPD visit UUID" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="wardId" label="Ward" rules={[{ required: true, message: 'Select a ward' }]}>
              <Select
                placeholder="Select ward"
                options={wards.map(w => ({ value: w.id, label: `${w.name} (${w.wardType})` }))}
              />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="bedId" label="Bed" rules={[{ required: true, message: 'Select a bed' }]}>
              <Select
                placeholder={wardId ? 'Select available bed' : 'Select ward first'}
                disabled={!wardId}
                options={beds.map(b => ({
                  value: b.id,
                  label: `Bed ${b.bedNumber} — ${b.bedType} (₹${b.dailyCharge}/day)`,
                }))}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="doctorName" label="Attending Doctor">
              <Input placeholder="Doctor name" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="doctorId" label="Doctor ID (optional)">
              <Input placeholder="Doctor UUID" />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item name="admissionDiagnosis" label="Admission Diagnosis">
          <Input.TextArea rows={2} placeholder="Presenting complaint / diagnosis" />
        </Form.Item>

        <Form.Item name="notes" label="Notes">
          <Input.TextArea rows={2} placeholder="Additional notes" />
        </Form.Item>
      </Form>
    </Modal>
  )
}
