import { Modal, Form, Input, InputNumber, DatePicker, Row, Col, Button, Select, AutoComplete } from 'antd'
import { useCreateVisit } from '@/hooks/useOpdVisits'
import { usePatients } from '@/hooks/usePatients'
import type { OpdVisitCreateRequest } from '@/types'
import { useState } from 'react'
import dayjs from 'dayjs'

interface Props {
  open: boolean
  onClose: () => void
  preselectedPatientId?: string
}

export function OpdVisitFormModal({ open, onClose, preselectedPatientId }: Props) {
  const [form] = Form.useForm<OpdVisitCreateRequest & { visitDate: dayjs.Dayjs }>()
  const { mutate: create, isPending } = useCreateVisit()
  const [patientQuery, setPatientQuery] = useState('')
  const { data: patients } = usePatients(patientQuery || undefined)

  const patientOptions = patients?.content.map((p) => ({
    value: p.id,
    label: `${p.firstName} ${p.lastName} — ${p.mobile ?? p.id.substring(0, 8)}`,
  })) ?? []

  function handleFinish(values: OpdVisitCreateRequest & { visitDate: dayjs.Dayjs }) {
    create(
      {
        ...values,
        visitDate: values.visitDate?.format('YYYY-MM-DD'),
        consultationFee: values.consultationFee ?? 0,
      },
      { onSuccess: () => { form.resetFields(); onClose() } }
    )
  }

  return (
    <Modal
      title="Register OPD Visit"
      open={open}
      onCancel={onClose}
      footer={null}
      width={640}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        onFinish={handleFinish}
        initialValues={{
          patientId: preselectedPatientId,
          visitDate: dayjs(),
          consultationFee: 300,
        }}
      >
        <Form.Item name="patientId" label="Patient" rules={[{ required: true, message: 'Select a patient' }]}>
          <Select
            showSearch
            filterOption={false}
            onSearch={setPatientQuery}
            options={patientOptions}
            placeholder="Search patient by name or mobile…"
          />
        </Form.Item>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="visitDate" label="Visit Date">
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="department" label="Department">
              <Input placeholder="General Medicine, Gynaecology…" />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item name="doctorName" label="Consulting Doctor">
          <Input placeholder="Dr. Sharma" />
        </Form.Item>

        <Form.Item name="symptoms" label="Presenting Complaints / Symptoms">
          <Input.TextArea rows={2} placeholder="Chief complaints…" />
        </Form.Item>

        <Form.Item name="consultationFee" label="Consultation Fee (₹)" rules={[{ required: true }]}>
          <InputNumber min={0} style={{ width: '100%' }} prefix="₹" />
        </Form.Item>

        <div className="flex justify-end gap-2">
          <Button onClick={onClose}>Cancel</Button>
          <Button type="primary" htmlType="submit" loading={isPending}>
            Register Visit
          </Button>
        </div>
      </Form>
    </Modal>
  )
}
