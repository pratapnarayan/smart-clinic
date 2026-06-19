import ReactApexChart from 'react-apexcharts'
import { Card } from 'antd'
import { CalendarOutlined, CheckCircleOutlined, CloseCircleOutlined, ExclamationCircleOutlined } from '@ant-design/icons'
import { PageHeader } from '@/components/common'
import { KpiCard, EmptyChart, AnalyticsFilter, ExportToolbar } from '@/components/analytics'
import { chartConfigs } from '@/theme/chartTheme'
import { useAppointmentAnalytics } from '@/hooks/useAnalytics'
import { withDemoFallback, DEMO_APPOINTMENTS } from '@/hooks/useDemoData'
import type { AppointmentAnalytics } from '@/types'

export function AppointmentAnalyticsPage() {
  const { data: raw, isLoading } = useAppointmentAnalytics()
  const { data, isDemo } = withDemoFallback<AppointmentAnalytics>(raw, DEMO_APPOINTMENTS, isLoading)

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col lg:flex-row lg:items-start lg:justify-between gap-4">
        <PageHeader title="Appointment Analytics" subtitle="Booking patterns, status distribution and peak hours" />
        <ExportToolbar section="appointments" isDemoData={isDemo} />
      </div>
      <AnalyticsFilter />

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4">
        <KpiCard title="Total Appointments" value={data.totalAppointments?.toLocaleString() ?? '0'} loading={isLoading} icon={<CalendarOutlined />} color="primary" />
        <KpiCard title="Completed" value={data.completed?.toLocaleString() ?? '0'} loading={isLoading} icon={<CheckCircleOutlined />} color="success" />
        <KpiCard title="Cancelled" value={data.cancelled?.toLocaleString() ?? '0'} loading={isLoading} icon={<CloseCircleOutlined />} color="danger" />
        <KpiCard title="No Show" value={data.noShow?.toLocaleString() ?? '0'} loading={isLoading} icon={<ExclamationCircleOutlined />} color="warning" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <Card title="Daily Appointment Trend" className="medical-card" styles={{ body: { padding: '24px' } }}>
            {data.dailyTrend?.length ? (
              <ReactApexChart type="area" height={260}
                series={[{ name: 'Appointments', data: data.dailyTrend.map(p => p.value) }]}
                options={{ ...chartConfigs.area('#1677ff'), xaxis: { categories: data.dailyTrend.map(p => p.label), labels: { rotate: -45, style: { fontSize: '10px' } } } }} />
            ) : <EmptyChart height={260} />}
          </Card>
        </div>
        <div>
          <Card title="Status Distribution" className="medical-card" styles={{ body: { padding: '24px' } }}>
            {data.statusDistribution?.length ? (
              <ReactApexChart type="donut" height={260}
                series={data.statusDistribution.map(p => p.value)}
                options={{ ...chartConfigs.donut(), labels: data.statusDistribution.map(p => p.name) }} />
            ) : <EmptyChart height={260} />}
          </Card>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card title="Appointments by Doctor (Top 10)" className="medical-card" styles={{ body: { padding: '24px' } }}>
          {data.byDoctor?.length ? (
            <ReactApexChart type="bar" height={240}
              series={[{ name: 'Appointments', data: data.byDoctor.map(p => p.value) }]}
              options={{ ...chartConfigs.horizontalBar('#722ed1'), xaxis: { categories: data.byDoctor.map(p => p.name) } }} />
          ) : <EmptyChart height={240} />}
        </Card>
        <Card title="Appointments by Department" className="medical-card" styles={{ body: { padding: '24px' } }}>
          {data.byDepartment?.length ? (
            <ReactApexChart type="bar" height={240}
              series={[{ name: 'Appointments', data: data.byDepartment.map(p => p.value) }]}
              options={{ ...chartConfigs.bar('#13c2c2'), xaxis: { categories: data.byDepartment.map(p => p.name) } }} />
          ) : <EmptyChart height={240} />}
        </Card>
      </div>
    </div>
  )
}
