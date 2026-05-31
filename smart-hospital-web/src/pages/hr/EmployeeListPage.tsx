import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Table, Tag, Button, Input, Select, Card, Row, Col, Statistic, Space } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { PlusOutlined, SearchOutlined } from '@ant-design/icons'
import { PageHeader } from '@/components/common'
import { useEmployees, useHrDepartments, useHrDashboard } from '@/hooks/useHr'
import { useAuthStore } from '@/store/authStore'
import type { Employee, EmployeeStatus } from '@/types'
import { EmployeeFormModal } from './EmployeeFormModal'

const STATUS_COLOR: Record<EmployeeStatus, string> = {
  ACTIVE: 'success', ON_LEAVE: 'warning', SUSPENDED: 'error',
  RESIGNED: 'default', TERMINATED: 'default',
}

export function EmployeeListPage() {
  const navigate = useNavigate()
  const { hasPermission } = useAuthStore()
  const [page, setPage]         = useState(0)
  const [search, setSearch]     = useState('')
  const [deptId, setDeptId]     = useState<string | undefined>()
  const [addOpen, setAddOpen]   = useState(false)

  const { data, isLoading }  = useEmployees(deptId, search || undefined, page)
  const { data: depts = [] } = useHrDepartments()
  const { data: dash }       = useHrDashboard()

  const deptMap = Object.fromEntries(depts.map(d => [d.id, d.name]))

  const columns: ColumnsType<Employee> = [
    { title: 'Code',       dataIndex: 'employeeCode', width: 130,
      render: (v: string, r: Employee) => <Button type="link" onClick={() => navigate(`/hr/${r.id}`)}>{v}</Button> },
    { title: 'Name',       render: (_: unknown, r: Employee) => `${r.firstName} ${r.lastName}` },
    { title: 'Department', dataIndex: 'departmentId',
      render: (v?: string) => v ? deptMap[v] ?? '—' : '—' },
    { title: 'Type',       dataIndex: 'employmentType',
      render: (v: string) => <Tag>{v.replace('_', ' ')}</Tag> },
    { title: 'Join Date',  dataIndex: 'joinDate',
      render: (v: string) => new Date(v).toLocaleDateString('en-IN') },
    { title: 'Mobile',     dataIndex: 'mobile',  render: (v?: string) => v ?? '—' },
    { title: 'Status',     dataIndex: 'status',
      render: (v: EmployeeStatus) => <Tag color={STATUS_COLOR[v]}>{v.replace('_', ' ')}</Tag> },
  ]

  return (
    <>
      <PageHeader
        title="HR — Employees"
        subtitle="Manage staff, departments and designations"
        extra={
          hasPermission('HR.CREATE') && (
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setAddOpen(true)}>
              Add Employee
            </Button>
          )
        }
      />

      {dash && (
        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col span={4}><Card><Statistic title="Total Staff"   value={dash.totalEmployees} /></Card></Col>
          <Col span={4}><Card><Statistic title="Active"        value={dash.activeEmployees}    valueStyle={{ color: '#52c41a' }} /></Card></Col>
          <Col span={4}><Card><Statistic title="Present Today" value={dash.presentToday}       valueStyle={{ color: '#1677ff' }} /></Card></Col>
          <Col span={4}><Card><Statistic title="Absent Today"  value={dash.absentToday}        valueStyle={{ color: '#ff4d4f' }} /></Card></Col>
          <Col span={4}><Card><Statistic title="On Leave"      value={dash.onLeaveToday}       valueStyle={{ color: '#faad14' }} /></Card></Col>
          <Col span={4}><Card><Statistic title="Leave Pending" value={dash.pendingLeaveRequests} valueStyle={{ color: '#fa8c16' }} /></Card></Col>
        </Row>
      )}

      <Card
        title={
          <Space>
            <Input
              placeholder="Search name / code / mobile"
              prefix={<SearchOutlined />}
              style={{ width: 260 }}
              allowClear
              onChange={e => { setSearch(e.target.value); setPage(0) }}
            />
            <Select
              allowClear
              placeholder="Filter by department"
              style={{ width: 200 }}
              onChange={(v) => { setDeptId(v); setPage(0) }}
              options={depts.map(d => ({ value: d.id, label: d.name }))}
            />
          </Space>
        }
      >
        <Table
          rowKey="id"
          columns={columns}
          dataSource={data?.content ?? []}
          loading={isLoading}
          pagination={{
            current: page + 1, pageSize: 20, total: data?.total ?? 0,
            onChange: (p) => setPage(p - 1),
          }}
        />
      </Card>

      <EmployeeFormModal open={addOpen} onClose={() => setAddOpen(false)} />
    </>
  )
}
