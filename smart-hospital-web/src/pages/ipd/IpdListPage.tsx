import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Table, Tag, Button, Space, Select, Card, Statistic, Row, Col } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { PlusOutlined } from '@ant-design/icons'
import { PageHeader } from '@/components/common'
import { useIpdAdmissions, useIpdDashboard } from '@/hooks/useIpd'
import { useAuthStore } from '@/store/authStore'
import type { IpdAdmission, AdmissionStatus } from '@/types'
import { IpdAdmissionFormModal } from './IpdAdmissionFormModal'

const STATUS_COLOR: Record<AdmissionStatus, string> = {
  ADMITTED:    'processing',
  TRANSFERRED: 'warning',
  DISCHARGED:  'success',
  DECEASED:    'default',
}

export function IpdListPage() {
  const navigate  = useNavigate()
  const { hasPermission } = useAuthStore()
  const [page, setPage]       = useState(0)
  const [status, setStatus]   = useState<AdmissionStatus | undefined>(undefined)
  const [admitOpen, setAdmitOpen] = useState(false)

  const { data, isLoading }   = useIpdAdmissions(status, page)
  const { data: dashboard }   = useIpdDashboard()

  const columns: ColumnsType<IpdAdmission> = [
    {
      title: 'Admission No.',
      dataIndex: 'admissionNumber',
      render: (v: string, r: IpdAdmission) => (
        <Button type="link" onClick={() => navigate(`/ipd/${r.id}`)}>{v}</Button>
      ),
    },
    { title: 'Patient',     dataIndex: 'patientName' },
    { title: 'Doctor',      dataIndex: 'doctorName', render: (v?: string) => v ?? '—' },
    {
      title: 'Admitted On',
      dataIndex: 'admissionDate',
      render: (v: string) => new Date(v).toLocaleDateString('en-IN'),
    },
    {
      title: 'Status',
      dataIndex: 'status',
      render: (v: AdmissionStatus) => <Tag color={STATUS_COLOR[v]}>{v}</Tag>,
    },
    {
      title: 'Net Amount',
      dataIndex: 'netAmount',
      render: (v: number) => `₹${v.toLocaleString('en-IN')}`,
    },
    {
      title: 'Payment',
      dataIndex: 'paymentStatus',
      render: (v: string) => (
        <Tag color={v === 'PAID' ? 'success' : v === 'PARTIAL' ? 'warning' : 'default'}>{v}</Tag>
      ),
    },
  ]

  return (
    <>
      <PageHeader
        title="IPD — Inpatient Department"
        subtitle="Manage admissions, beds and discharges"
        extra={
          hasPermission('IPD.CREATE') && (
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setAdmitOpen(true)}>
              Admit Patient
            </Button>
          )
        }
      />

      {dashboard && (
        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col span={6}>
            <Card><Statistic title="Currently Admitted" value={dashboard.totalAdmitted} valueStyle={{ color: '#1677ff' }} /></Card>
          </Col>
          <Col span={6}>
            <Card><Statistic title="Total Beds" value={dashboard.totalBeds} /></Card>
          </Col>
          <Col span={6}>
            <Card><Statistic title="Available Beds" value={dashboard.availableBeds} valueStyle={{ color: '#52c41a' }} /></Card>
          </Col>
          <Col span={6}>
            <Card><Statistic title="Occupied Beds" value={dashboard.occupiedBeds} valueStyle={{ color: '#faad14' }} /></Card>
          </Col>
        </Row>
      )}

      <Card
        title={
          <Space>
            <span>Admissions</span>
            <Select
              allowClear
              placeholder="Filter by status"
              style={{ width: 180 }}
              onChange={(v) => { setStatus(v as AdmissionStatus | undefined); setPage(0) }}
              options={[
                { value: 'ADMITTED',    label: 'Admitted' },
                { value: 'TRANSFERRED', label: 'Transferred' },
                { value: 'DISCHARGED',  label: 'Discharged' },
                { value: 'DECEASED',    label: 'Deceased' },
              ]}
            />
          </Space>
        }
      >
        <Table
          rowKey="id"
          columns={columns as any}
          dataSource={data?.content ?? []}
          loading={isLoading}
          pagination={{
            current: page + 1,
            pageSize: 20,
            total: data?.total ?? 0,
            onChange: (p) => setPage(p - 1),
          }}
        />
      </Card>

      <IpdAdmissionFormModal open={admitOpen} onClose={() => setAdmitOpen(false)} />
    </>
  )
}
