import { useState } from 'react'
import { useParams } from 'react-router-dom'
import { Card, Descriptions, Tag, Button, Tabs, Spin, Alert } from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import { usePatient } from '@/hooks/usePatients'
import { useVisitsByPatient } from '@/hooks/useOpdVisits'
import { PageHeader } from '@/components/common/PageHeader'
import { PatientFormModal } from './PatientFormModal'
import { formatDate, formatDateTime, calcAge, formatCurrency } from '@/utils'
import type { TableProps } from 'antd'
import { Table } from 'antd'
import type { OpdVisit } from '@/types'
import { useNavigate } from 'react-router-dom'

export function PatientDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [editOpen, setEditOpen] = useState(false)

  const { data: patient, isLoading, isError } = usePatient(id!)
  const { data: visitsPage } = useVisitsByPatient(id!)

  if (isLoading) return <Spin size="large" className="flex justify-center mt-20" />
  if (isError || !patient) return <Alert type="error" message="Patient not found" />

  const visitColumns: TableProps<OpdVisit>['columns'] = [
    { title: 'Visit #', dataIndex: 'visitNumber' },
    { title: 'Date', dataIndex: 'visitDate', render: formatDate },
    { title: 'Doctor', dataIndex: 'doctorName', render: (v) => v ?? '—' },
    { title: 'Diagnosis', dataIndex: 'diagnosis', render: (v) => v ?? '—', ellipsis: true },
    { title: 'Amount', dataIndex: 'netAmount', render: formatCurrency },
    {
      title: 'Status', dataIndex: 'visitStatus',
      render: (v: string) => {
        const colors: Record<string, string> = {
          COMPLETED: 'green', IN_PROGRESS: 'blue', REGISTERED: 'orange', CANCELLED: 'red'
        }
        return <Tag color={colors[v] ?? 'default'}>{v}</Tag>
      },
    },
    {
      title: '', key: 'actions',
      render: (_, r) => (
        <Button size="small" onClick={() => navigate(`/opd/${r.id}`)}>View</Button>
      ),
    },
  ]

  return (
    <div>
      <PageHeader
        title={`${patient.firstName} ${patient.lastName}`}
        subtitle={`ID: ${patient.id.substring(0, 8)}… · Registered ${formatDate(patient.createdAt)}`}
        breadcrumbs={[
          { title: 'Patients', href: '/patients' },
          { title: `${patient.firstName} ${patient.lastName}` },
        ]}
        extra={
          <Button icon={<EditOutlined />} onClick={() => setEditOpen(true)}>
            Edit
          </Button>
        }
      />

      <Card className="mb-4">
        <Descriptions column={{ xs: 1, sm: 2, lg: 3 }} bordered size="small">
          <Descriptions.Item label="Gender">
            <Tag color={patient.gender === 'MALE' ? 'blue' : patient.gender === 'FEMALE' ? 'pink' : 'default'}>
              {patient.gender}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Date of Birth">
            {formatDate(patient.dateOfBirth)} ({calcAge(patient.dateOfBirth)})
          </Descriptions.Item>
          <Descriptions.Item label="Blood Group">
            {patient.bloodGroup ? <Tag color="red">{patient.bloodGroup}</Tag> : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Mobile">{patient.mobile ?? '—'}</Descriptions.Item>
          <Descriptions.Item label="Email">{patient.email ?? '—'}</Descriptions.Item>
          <Descriptions.Item label="Address" span={2}>{patient.address ?? '—'}</Descriptions.Item>
          <Descriptions.Item label="Guardian">
            {patient.guardianName ?? '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Registered">{formatDateTime(patient.createdAt)}</Descriptions.Item>
        </Descriptions>
      </Card>

      <Tabs
        defaultActiveKey="opd"
        items={[
          {
            key: 'opd',
            label: 'OPD History',
            children: (
              <Card
                extra={
                  <Button
                    type="primary" size="small" icon={<PlusOutlined />}
                    onClick={() => navigate(`/opd?patientId=${patient.id}`)}
                  >
                    New Visit
                  </Button>
                }
              >
                <Table
                  rowKey="id"
                  size="small"
                  dataSource={visitsPage?.content}
                  columns={visitColumns}
                  pagination={false}
                />
              </Card>
            ),
          },
          {
            key: 'pharmacy',
            label: 'Pharmacy Bills',
            children: (
              <Card>
                <p className="text-gray-400">Pharmacy bill history — coming in Phase 1c wire-up.</p>
              </Card>
            ),
          },
        ]}
      />

      <PatientFormModal
        open={editOpen}
        onClose={() => setEditOpen(false)}
        patient={patient}
      />
    </div>
  )
}
