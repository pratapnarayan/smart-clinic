import { useEffect } from 'react'
import { Modal, Form, Input, Select, DatePicker, Row, Col } from 'antd'
import dayjs from 'dayjs'
import { useCreateEmployee, useHrDepartments, useDesignations } from '@/hooks/useHr'
import type { CreateEmployeePayload } from '@/types'

interface Props { open: boolean; onClose: () => void }

export function EmployeeFormModal({ open, onClose }: Props) {
  const [form] = Form.useForm<CreateEmployeePayload & { _joinDate: dayjs.Dayjs; _dob: dayjs.Dayjs }>()
  const deptId = Form.useWatch('departmentId', form)

  const { data: depts = [] }        = useHrDepartments()
  const { data: designations = [] } = useDesignations(deptId)
  const { mutate: create, isPending } = useCreateEmployee()

  useEffect(() => {
    if (open) { form.resetFields(); form.setFieldsValue({ _joinDate: dayjs() }) }
  }, [open, form])

  const onFinish = (values: CreateEmployeePayload & { _joinDate: dayjs.Dayjs; _dob?: dayjs.Dayjs }) => {
    const { _joinDate, _dob, ...rest } = values
    create({
      ...rest,
      joinDate: _joinDate.format('YYYY-MM-DD'),
      dateOfBirth: _dob ? _dob.format('YYYY-MM-DD') : undefined,
    }, { onSuccess: onClose })
  }

  return (
    <Modal title="Add Employee" open={open} onCancel={onClose}
      onOk={() => form.submit()} okText="Add" confirmLoading={isPending}
      width={680} destroyOnHidden>
      <Form form={form} layout="vertical" onFinish={onFinish}>
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="firstName" label="First Name" rules={[{ required: true }]}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="lastName" label="Last Name" rules={[{ required: true }]}>
              <Input />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="_joinDate" label="Join Date" rules={[{ required: true }]}>
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="_dob" label="Date of Birth">
              <DatePicker style={{ width: '100%' }} disabledDate={d => d.isAfter(dayjs())} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="gender" label="Gender">
              <Select options={[
                { value: 'MALE', label: 'Male' },
                { value: 'FEMALE', label: 'Female' },
                { value: 'OTHER', label: 'Other' },
              ]} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="employmentType" label="Employment Type" initialValue="FULL_TIME">
              <Select options={[
                { value: 'FULL_TIME',  label: 'Full Time' },
                { value: 'PART_TIME',  label: 'Part Time' },
                { value: 'CONTRACT',   label: 'Contract' },
                { value: 'CONSULTANT', label: 'Consultant' },
              ]} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="mobile" label="Mobile">
              <Input placeholder="10-digit mobile" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="email" label="Email">
              <Input type="email" />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="departmentId" label="Department">
              <Select allowClear placeholder="Select department"
                options={depts.map(d => ({ value: d.id, label: d.name }))}
                onChange={() => form.setFieldValue('designationId', undefined)} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="designationId" label="Designation">
              <Select allowClear placeholder="Select designation" disabled={!deptId}
                options={designations.map(d => ({ value: d.id, label: d.title }))} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="bloodGroup" label="Blood Group">
              <Select allowClear options={['A+','A-','B+','B-','AB+','AB-','O+','O-']
                .map(v => ({ value: v, label: v }))} />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item name="address" label="Address">
          <Input.TextArea rows={2} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
