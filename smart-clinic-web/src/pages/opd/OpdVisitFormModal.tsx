import { Modal, Form, Input, InputNumber, DatePicker, Row, Col, Button, Select } from 'antd'
import { useCreateVisit } from '@/hooks/useOpdVisits'
import { usePatients } from '@/hooks/usePatients'
import { useHrDepartments, useEmployees } from '@/hooks/useHr'
import type { OpdVisitCreateRequest } from '@/types'
import { useEffect, useState } from 'react'
import dayjs from 'dayjs'

interface Props {
  open: boolean
  onClose: () => void
  preselectedPatientId?: string
}

export function OpdVisitFormModal({ open, onClose, preselectedPatientId }: Props) {
  const [form] = Form.useForm<OpdVisitCreateRequest & { visitDate: dayjs.Dayjs }>()
  const { mutate: create, isPending } = useCreateVisit()

  // ── Patient search ─────────────────────────────────────────────────────────
  const [patientQuery, setPatientQuery] = useState('')
  const { data: patients } = usePatients(patientQuery || undefined)
  const patientOptions = patients?.content.map((p) => ({
    value: p.id,
    label: `${p.firstName} ${p.lastName} — ${p.mobile ?? p.id.substring(0, 8)}`,
  })) ?? []

  // ── Department list ────────────────────────────────────────────────────────
  const { data: departments } = useHrDepartments()
  const deptOptions = (departments ?? []).map(d => ({ value: d.name, label: d.name, id: d.id }))

  // ── Doctor search — filtered by selected department ────────────────────────
  const [selectedDeptId, setSelectedDeptId] = useState<string | undefined>()
  const [doctorSearch, setDoctorSearch]     = useState('')
  const [debouncedDoctor, setDebouncedDoc]  = useState('')
  useEffect(() => {
    const t = setTimeout(() => setDebouncedDoc(doctorSearch), 300)
    return () => clearTimeout(t)
  }, [doctorSearch])
  const { data: employeePage, isFetching: searchingDoctors } = useEmployees(
    selectedDeptId, debouncedDoctor || undefined, 0
  )
  // Doctor name string is the form value — OpdVisitCreateRequest only has doctorName, no doctorId
  // Strip a pre-existing "Dr." title before re-adding it, so an employee record
  // that already has the title baked into firstName (legacy data) doesn't render
  // as "Dr. Dr. <name>".
  const doctorOptions = (employeePage?.content ?? []).map(e => {
    const cleanFirstName = e.firstName.replace(/^dr\.?\s+/i, '')
    const displayName = `Dr. ${cleanFirstName} ${e.lastName}`
    return { value: displayName, label: displayName }
  })

  // ── Reset on open ──────────────────────────────────────────────────────────
  useEffect(() => {
    if (open) {
      setPatientQuery('')
      setDoctorSearch(''); setDebouncedDoc('')
      setSelectedDeptId(undefined)
    }
  }, [open])

  // ── Escape-key guard ───────────────────────────────────────────────────────
  // Pressing Escape while a Select dropdown (Patient/Department/Doctor) is open
  // was closing BOTH the dropdown and this Modal at once, which raced their two
  // closing animations and left the Modal's DOM in a corrupted, collapsed state
  // until a full page reload. A capture-phase listener lets the dropdown's own
  // Escape handling run first and swallows the keypress before antd Modal's
  // bubble-phase listener sees it, so only the dropdown closes on the first press.
  useEffect(() => {
    if (!open) return
    const onKeyDown = (e: KeyboardEvent) => {
      if (e.key !== 'Escape') return
      const openDropdown = document.querySelector('.ant-select-dropdown:not(.ant-select-dropdown-hidden)')
      if (openDropdown) {
        e.stopPropagation()
      }
    }
    document.addEventListener('keydown', onKeyDown, { capture: true })
    return () => document.removeEventListener('keydown', onKeyDown, { capture: true })
  }, [open])

  // ── Handlers ───────────────────────────────────────────────────────────────
  const onDeptChange = (deptName: string) => {
    const dept = (departments ?? []).find(d => d.name === deptName)
    setSelectedDeptId(dept?.id)
    form.setFieldValue('doctorName', undefined)
    setDoctorSearch(''); setDebouncedDoc('')
  }

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
        {/* ── Patient ─────────────────────────────────────────────────────── */}
        <Form.Item name="patientId" label="Patient" rules={[{ required: true, message: 'Select a patient' }]}>
          <Select
            showSearch
            filterOption={false}
            onSearch={setPatientQuery}
            options={patientOptions}
            placeholder="Search patient by name or mobile…"
            disabled={!!preselectedPatientId}
          />
        </Form.Item>

        {/* ── Visit Date + Department ─────────────────────────────────────── */}
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="visitDate" label="Visit Date">
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="department" label="Department">
              <Select
                showSearch
                allowClear
                placeholder="Select department"
                filterOption={(input, opt) =>
                  (opt?.label as string ?? '').toLowerCase().includes(input.toLowerCase())
                }
                options={deptOptions}
                onChange={onDeptChange}
              />
            </Form.Item>
          </Col>
        </Row>

        {/* ── Consulting Doctor ───────────────────────────────────────────── */}
        <Form.Item name="doctorName" label="Consulting Doctor">
          <Select
            showSearch
            allowClear
            filterOption={false}
            placeholder={selectedDeptId ? 'Search doctor in department…' : 'Select department first, or search all…'}
            onSearch={setDoctorSearch}
            loading={searchingDoctors}
            options={doctorOptions}
            notFoundContent={
              debouncedDoctor
                ? (searchingDoctors ? 'Searching…' : 'No doctors found')
                : (selectedDeptId ? 'No staff in this department' : 'Type to search all staff')
            }
          />
        </Form.Item>

        {/* ── Symptoms ────────────────────────────────────────────────────── */}
        <Form.Item name="symptoms" label="Presenting Complaints / Symptoms">
          <Input.TextArea rows={2} placeholder="Chief complaints…" />
        </Form.Item>

        {/* ── Consultation Fee ─────────────────────────────────────────────── */}
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
