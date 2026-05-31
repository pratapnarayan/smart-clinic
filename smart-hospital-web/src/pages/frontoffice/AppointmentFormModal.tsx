import { useEffect } from 'react'
import { Modal, Form, Input, Select, DatePicker, Row, Col } from 'antd'
import dayjs from 'dayjs'
import { useBookAppointment } from '@/hooks/useFrontOffice'
import type { BookAppointmentPayload } from '@/types'

const TIME_SLOTS = [
  '08:00-08:30','08:30-09:00','09:00-09:30','09:30-10:00',
  '10:00-10:30','10:30-11:00','11:00-11:30','11:30-12:00',
  '14:00-14:30','14:30-15:00','15:00-15:30','15:30-16:00',
  '16:00-16:30','16:30-17:00',
]

interface Props {
  open: boolean
  onClose: () => void
  patientId?: string
  patientName?: string
}

export function AppointmentFormModal({ open, onClose, patientId }: Props) {
  const [form] = Form.useForm<BookAppointmentPayload & { _date: dayjs.Dayjs }>()
  const { mutate: book, isPending } = useBookAppointment()

  useEffect(() => {
    if (open) {
      form.resetFields()
      form.setFieldsValue({ _date: dayjs() })
      if (patientId) form.setFieldValue('patientId', patientId)
    }
  }, [open, patientId, form])

  const onFinish = (values: BookAppointmentPayload & { _date: dayjs.Dayjs }) => {
    const { _date, ...rest } = values
    book({ ...rest, appointmentDate: _date.format('YYYY-MM-DD') }, { onSuccess: onClose })
  }

  return (
    <Modal
      title="Book Appointment"
      open={open}
      onCancel={onClose}
      onOk={() => form.submit()}
      okText="Book"
      confirmLoading={isPending}
      width={600}
      destroyOnHidden
    >
      <Form form={form} layout="vertical" onFinish={onFinish}>
        <Form.Item name="patientId" label="Patient ID" rules={[{ required: true }]}>
          <Input placeholder="Paste patient UUID" disabled={!!patientId} />
        </Form.Item>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="_date" label="Date" rules={[{ required: true }]}>
              <DatePicker style={{ width: '100%' }} disabledDate={d => d.isBefore(dayjs(), 'day')} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="timeSlot" label="Time Slot">
              <Select placeholder="Select slot" options={TIME_SLOTS.map(s => ({ value: s, label: s }))} />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="department" label="Department">
              <Input placeholder="e.g. General Medicine" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="appointmentType" label="Type" initialValue="CONSULTATION">
              <Select options={[
                { value: 'CONSULTATION', label: 'Consultation' },
                { value: 'FOLLOW_UP',    label: 'Follow-up' },
                { value: 'EMERGENCY',    label: 'Emergency' },
                { value: 'PROCEDURE',    label: 'Procedure' },
              ]} />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="doctorName" label="Doctor Name">
              <Input placeholder="Doctor name" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="doctorId" label="Doctor ID (optional)">
              <Input placeholder="Doctor UUID" />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item name="notes" label="Notes">
          <Input.TextArea rows={2} placeholder="Additional notes" />
        </Form.Item>
      </Form>
    </Modal>
  )
}
