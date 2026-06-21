import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Card, Input, Select, Button, Tag, Avatar, Space, Pagination, Empty, Spin } from 'antd'
import { SearchOutlined, UserOutlined, PlusOutlined, CalendarOutlined, TeamOutlined, CheckCircleOutlined, MedicineBoxOutlined } from '@ant-design/icons'
import { PageHeader } from '@/components/common/PageHeader'
import { KpiCard } from '@/components/analytics'
import { useDoctors, useSpecializations, useDoctorDashboard } from '@/hooks/useDoctor'
import { useHrDepartments } from '@/hooks/useHr'
import { useAuthStore } from '@/store/authStore'
import { AppointmentFormModal } from '@/pages/frontoffice/AppointmentFormModal'
import { DoctorProfileModal } from './DoctorProfileModal'
import { formatCurrency } from '@/utils'
import type { DoctorProfile } from '@/types'

export function DoctorDirectoryPage() {
  const navigate = useNavigate()
  const { hasPermission } = useAuthStore()
  const [search, setSearch]         = useState('')
  const [deptId, setDeptId]         = useState<string | undefined>()
  const [specId, setSpecId]         = useState<string | undefined>()
  const [page, setPage]             = useState(0)
  const [apptDoctor, setApptDoctor] = useState<DoctorProfile | null>(null)
  const [addOpen, setAddOpen]       = useState(false)

  const { data, isLoading }         = useDoctors(search || undefined, deptId, specId, page)
  const { data: specs = [] }        = useSpecializations()
  const { data: depts = [] }        = useHrDepartments()
  const { data: dash }              = useDoctorDashboard()

  const deptMap = Object.fromEntries(depts.map(d => [d.id, d.name]))

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader
        title="Doctor Directory"
        subtitle="Find and connect with our specialists"
        extra={
          hasPermission('DOCTOR.CREATE') ? (
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setAddOpen(true)}>
              Add Doctor Profile
            </Button>
          ) : undefined
        }
      />

      {dash && (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <KpiCard title="Total Doctors"   value={(dash.totalDoctors).toString()}   icon={<TeamOutlined />}         color="primary" />
          <KpiCard title="Active"          value={(dash.activeDoctors).toString()}   icon={<CheckCircleOutlined />}  color="success" />
          <KpiCard title="Available Today" value={(dash.availableToday).toString()}  icon={<CalendarOutlined />}     color="cyan" />
          <KpiCard title="Specializations" value={(dash.totalSpecializations).toString()} icon={<MedicineBoxOutlined />} color="purple" />
        </div>
      )}

      <Card
        className="medical-card"
        styles={{ body: { paddingBottom: 0 } }}
        title={
          <Space wrap>
            <Input
              placeholder="Search by name"
              prefix={<SearchOutlined />}
              style={{ width: 220 }}
              allowClear
              onChange={e => { setSearch(e.target.value); setPage(0) }}
            />
            <Select
              allowClear
              placeholder="Department"
              style={{ width: 180 }}
              onChange={(v: string | undefined) => { setDeptId(v); setPage(0) }}
              options={depts.map(d => ({ value: d.id, label: d.name }))}
            />
            <Select
              allowClear
              placeholder="Specialization"
              style={{ width: 180 }}
              onChange={(v: string | undefined) => { setSpecId(v); setPage(0) }}
              options={specs.map(s => ({ value: s.id, label: s.name }))}
            />
          </Space>
        }
      >
        {isLoading ? (
          <div style={{ textAlign: 'center', padding: 48 }}><Spin size="large" /></div>
        ) : !data?.content?.length ? (
          <Empty description="No doctors found" style={{ padding: 48 }} />
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4" style={{ padding: '16px 0' }}>
            {data.content.map(doctor => (
              <Card
                key={doctor.id}
                hoverable
                className="medical-card"
                actions={[
                  <Button type="link" key="view" onClick={() => navigate(`/doctors/${doctor.id}`)}>
                    View Profile
                  </Button>,
                  <Button
                    type="link"
                    key="book"
                    icon={<CalendarOutlined />}
                    onClick={() => setApptDoctor(doctor)}
                  >
                    Book Appointment
                  </Button>,
                ]}
              >
                <Card.Meta
                  avatar={
                    doctor.profilePhoto
                      ? <Avatar size={64} src={doctor.profilePhoto} />
                      : (
                        <Avatar size={64} icon={<UserOutlined />} style={{ background: '#1677ff', fontSize: 20 }}>
                          {doctor.firstName[0]}{doctor.lastName[0]}
                        </Avatar>
                      )
                  }
                  title={`Dr. ${doctor.firstName} ${doctor.lastName}`}
                  description={
                    <Space direction="vertical" size={4} style={{ width: '100%' }}>
                      {doctor.qualifications && (
                        <span style={{ color: '#666', fontSize: 12 }}>{doctor.qualifications}</span>
                      )}
                      {doctor.departmentId && (
                        <span style={{ fontSize: 12, color: '#888' }}>
                          Dept: {deptMap[doctor.departmentId] ?? '—'}
                        </span>
                      )}
                      <div>
                        {doctor.specializations.slice(0, 2).map(s => (
                          <Tag key={s.id} color="blue" style={{ marginBottom: 4 }}>{s.name}</Tag>
                        ))}
                        {doctor.specializations.length > 2 && (
                          <Tag>+{doctor.specializations.length - 2}</Tag>
                        )}
                      </div>
                      {doctor.experienceYears != null && doctor.experienceYears > 0 && (
                        <span style={{ fontSize: 12 }}>{doctor.experienceYears} yrs experience</span>
                      )}
                      {doctor.consultationFee != null && doctor.consultationFee > 0 && (
                        <span style={{ fontSize: 12, color: '#52c41a' }}>
                          Consultation: {formatCurrency(doctor.consultationFee)}
                        </span>
                      )}
                    </Space>
                  }
                />
              </Card>
            ))}
          </div>
        )}

        {data && data.total > 0 && (
          <div className="flex justify-end py-4">
            <Pagination
              current={page + 1}
              pageSize={20}
              total={data.total}
              onChange={p => setPage(p - 1)}
              showSizeChanger={false}
            />
          </div>
        )}
      </Card>

      {apptDoctor && (
        <AppointmentFormModal
          open={!!apptDoctor}
          onClose={() => setApptDoctor(null)}
        />
      )}

      {addOpen && <DoctorProfileModal open={addOpen} onClose={() => setAddOpen(false)} />}
    </div>
  )
}
