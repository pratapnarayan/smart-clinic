import { Modal, Form, Input, Select, DatePicker, Row, Col, Button } from 'antd'
import { useCreatePatient, useUpdatePatient } from '@/hooks/usePatients'
import type { Patient, PatientCreateRequest } from '@/types'
import dayjs from 'dayjs'

interface Props {
  open: boolean
  onClose: () => void
  patient?: Patient   // if provided, edit mode
}

export function PatientFormModal({ open, onClose, patient }: Props) {
  const [form] = Form.useForm<PatientCreateRequest>()
  const { mutate: create, isPending: creating } = useCreatePatient()
  const { mutate: update, isPending: updating } = useUpdatePatient(patient?.id ?? '')

  const isEdit = !!patient

  function handleFinish(values: PatientCreateRequest) {
    const payload = {
      ...values,
      dateOfBirth: values.dateOfBirth,
    }
    if (isEdit) {
      update(payload, { onSuccess: onClose })
    } else {
      create(payload, { onSuccess: () => { form.resetFields(); onClose() } })
    }
  }

  return (
    <Modal
      title={isEdit ? 'Edit Patient' : 'Register New Patient'}
      open={open}
      onCancel={onClose}
      footer={null}
      width={680}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        onFinish={handleFinish}
        initialValues={patient ? {
          ...patient,
          dateOfBirth: dayjs(patient.dateOfBirth),
        } : undefined}
      >
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="firstName" label="First Name" rules={[{ required: true }]}>
              <Input placeholder="Raj" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="lastName" label="Last Name" rules={[{ required: true }]}>
              <Input placeholder="Sharma" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="dateOfBirth" label="Date of Birth" rules={[{ required: true }]}>
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="gender" label="Gender" rules={[{ required: true }]}>
              <Select options={[
                { label: 'Male',   value: 'MALE' },
                { label: 'Female', value: 'FEMALE' },
                { label: 'Other',  value: 'OTHER' },
              ]} />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="mobile" label="Mobile">
              <Input placeholder="9876543210" maxLength={15} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="bloodGroup" label="Blood Group">
              <Select allowClear options={['A+','A-','B+','B-','O+','O-','AB+','AB-'].map((v) => ({ label: v, value: v }))} />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item name="address" label="Address">
          <Input.TextArea rows={2} placeholder="House no, Street, City" />
        </Form.Item>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="guardianName" label="Guardian Name">
              <Input placeholder="Parent / Spouse" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="guardianMobile" label="Guardian Mobile">
              <Input placeholder="9876500000" />
            </Form.Item>
          </Col>
        </Row>

        <div className="flex justify-end gap-2">
          <Button onClick={onClose}>Cancel</Button>
          <Button type="primary" htmlType="submit" loading={creating || updating}>
            {isEdit ? 'Save Changes' : 'Register Patient'}
          </Button>
        </div>
      </Form>
    </Modal>
  )
}
