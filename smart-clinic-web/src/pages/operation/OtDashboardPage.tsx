import { useNavigate } from 'react-router-dom'
import { Card, Table, Tag, Button, Progress } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { CalendarOutlined, PlayCircleOutlined, CheckCircleOutlined, BarChartOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { PageHeader } from '@/components/common'
import { KpiCard } from '@/components/analytics'
import { useOtDashboard } from '@/hooks/useOperation'
import type { OtSchedule, OtStatus, OtTheatreUtilization } from '@/types'

const STATUS_COLOR: Record<OtStatus, string> = {
  SCHEDULED: 'blue', IN_PROGRESS: 'orange', COMPLETED: 'success',
  POSTPONED: 'warning', CANCELLED: 'error',
}

const PRIORITY_COLOR = { ROUTINE: 'default', URGENT: 'orange', EMERGENCY: 'red' } as const

export function OtDashboardPage() {
  const navigate = useNavigate()
  const { data: dash, isLoading } = useOtDashboard()

  const totalToday = (dash?.todayScheduled ?? 0) + (dash?.todayInProgress ?? 0) + (dash?.todayCompleted ?? 0)
  const completedPct = totalToday > 0 ? Math.round(((dash?.todayCompleted ?? 0) / totalToday) * 100) : 0

  const scheduleColumns: ColumnsType<OtSchedule> = [
    {
      title: 'Time', dataIndex: 'scheduledStart', width: 80,
      render: (v: string) => dayjs(v).format('HH:mm'),
    },
    { title: 'Theatre', dataIndex: 'theatreName', width: 100 },
    {
      title: 'Patient', dataIndex: 'patientName',
      render: (v: string, r: OtSchedule) => (
        <Button type="link" style={{ padding: 0 }} onClick={() => navigate(`/operation/schedules/${r.id}`)}>
          {v}
        </Button>
      ),
    },
    { title: 'Procedure', dataIndex: 'procedureName', ellipsis: true },
    { title: 'Surgeon',   dataIndex: 'surgeonName', render: (v?: string) => v ?? '—' },
    {
      title: 'Priority', dataIndex: 'priority', width: 90,
      render: (v: keyof typeof PRIORITY_COLOR) => <Tag color={PRIORITY_COLOR[v]}>{v}</Tag>,
    },
    {
      title: 'Status', dataIndex: 'status', width: 120,
      render: (v: OtStatus) => <Tag color={STATUS_COLOR[v]}>{v.replace('_', ' ')}</Tag>,
    },
    {
      title: 'Est.', dataIndex: 'estimatedDurationMins', width: 70, align: 'right',
      render: (v: number) => `${v}m`,
    },
  ]

  const utilisationColumns: ColumnsType<OtTheatreUtilization> = [
    { title: 'Theatre', dataIndex: 'theatreName' },
    {
      title: 'Operations (this month)', dataIndex: 'operationsThisMonth', align: 'right',
      render: (v: number) => <strong>{v}</strong>,
    },
  ]

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader title="Operation Theatre" subtitle="Today's OT schedule and utilization" />

      <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
        <KpiCard
          title="Today — Scheduled"
          value={(dash?.todayScheduled ?? 0).toString()}
          icon={<CalendarOutlined />}
          color="primary"
          loading={isLoading}
        />
        <KpiCard
          title="Today — In Progress"
          value={(dash?.todayInProgress ?? 0).toString()}
          icon={<PlayCircleOutlined />}
          color="warning"
          loading={isLoading}
        />
        <KpiCard
          title="Today — Completed"
          value={(dash?.todayCompleted ?? 0).toString()}
          icon={<CheckCircleOutlined />}
          color="success"
          loading={isLoading}
        />
        <KpiCard
          title="Last 30 Days — Total"
          value={(dash?.monthTotal ?? 0).toString()}
          icon={<BarChartOutlined />}
          color="cyan"
          loading={isLoading}
        />
        <Card className="medical-card" loading={isLoading} styles={{ body: { textAlign: 'center', padding: '16px' } }}>
          <div style={{ fontSize: 12, color: '#888', marginBottom: 8 }}>Today's Progress</div>
          <Progress type="circle" percent={completedPct} size={64} />
        </Card>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-5 gap-6">
        <Card
          className="medical-card xl:col-span-4"
          title={`Today's Schedule (${dayjs().format('DD MMM YYYY')})`}
          loading={isLoading}
        >
          <Table
            rowKey="id"
            size="small"
            columns={scheduleColumns}
            dataSource={dash?.todaySchedules ?? []}
            pagination={false}
            locale={{ emptyText: 'No operations scheduled today' }}
            onRow={r => ({ onClick: () => navigate(`/operation/schedules/${r.id}`) })}
          />
        </Card>
        <Card className="medical-card" title="Theatre Utilization — Last 30 Days" loading={isLoading}>
          <Table
            rowKey="theatreName"
            size="small"
            columns={utilisationColumns}
            dataSource={dash?.theatreUtilization ?? []}
            pagination={false}
            locale={{ emptyText: 'No data yet' }}
          />
        </Card>
      </div>
    </div>
  )
}
