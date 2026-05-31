import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Table, Input, Button, Tag, Space, Tooltip, type TableProps } from 'antd'
import { SearchOutlined, PlusOutlined, EyeOutlined } from '@ant-design/icons'
import { usePatients } from '@/hooks/usePatients'
import { PageHeader } from '@/components/common/PageHeader'
import { formatDate, calcAge } from '@/utils'
import { PatientFormModal } from './PatientFormModal'
import type { Patient } from '@/types'

export function PatientListPage() {
  const navigate = useNavigate()
  const [query, setQuery] = useState('')
  const [page, setPage] = useState(0)
  const [showForm, setShowForm] = useState(false)

  const { data, isLoading } = usePatients(query || undefined, page)

  const columns: TableProps<Patient>['columns'] = [
    {
      title: 'Name', key: 'name',
      render: (_, r) => `${r.firstName} ${r.lastName}`,
    },
    {
      title: 'Gender / Age', key: 'genderage',
      render: (_, r) => (
        <Space>
          <Tag color={r.gender === 'MALE' ? 'blue' : r.gender === 'FEMALE' ? 'pink' : 'default'}>
            {r.gender}
          </Tag>
          {calcAge(r.dateOfBirth)}
        </Space>
      ),
    },
    { title: 'Mobile', dataIndex: 'mobile', render: (v) => v ?? '—' },
    { title: 'Blood Group', dataIndex: 'bloodGroup', render: (v) => v
        ? <Tag color="red">{v}</Tag> : '—' },
    { title: 'Registered', dataIndex: 'createdAt', render: formatDate },
    {
      title: 'Actions', key: 'actions',
      render: (_, r) => (
        <Tooltip title="View / Edit">
          <Button
            icon={<EyeOutlined />}
            size="small"
            onClick={() => navigate(`/patients/${r.id}`)}
          />
        </Tooltip>
      ),
    },
  ]

  return (
    <div>
      <PageHeader
        title="Patients"
        subtitle="Search and manage patient records"
        breadcrumbs={[{ title: 'Dashboard', href: '/dashboard' }, { title: 'Patients' }]}
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={() => setShowForm(true)}>
            New Patient
          </Button>
        }
      />

      <Input.Search
        placeholder="Search by name or mobile…"
        allowClear
        enterButton={<SearchOutlined />}
        style={{ maxWidth: 400, marginBottom: 16 }}
        onSearch={setQuery}
        onChange={(e) => !e.target.value && setQuery('')}
      />

      <Table
        rowKey="id"
        dataSource={data?.content}
        columns={columns}
        loading={isLoading}
        pagination={{
          current: (data?.page ?? 0) + 1,
          pageSize: data?.size ?? 20,
          total: data?.total ?? 0,
          onChange: (p) => setPage(p - 1),
          showTotal: (total) => `${total} patients`,
        }}
        onRow={(r) => ({ onDoubleClick: () => navigate(`/patients/${r.id}`) })}
      />

      <PatientFormModal
        open={showForm}
        onClose={() => setShowForm(false)}
      />
    </div>
  )
}
