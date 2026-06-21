import ReactApexChart from 'react-apexcharts'
import { Card } from 'antd'
import { TeamOutlined, UserAddOutlined, SyncOutlined, RiseOutlined } from '@ant-design/icons'
import { PageHeader } from '@/components/common'
import { KpiCard, EmptyChart, AnalyticsFilter, ExportToolbar } from '@/components/analytics'
import { chartConfigs } from '@/theme/chartTheme'
import { usePatientAnalytics } from '@/hooks/useAnalytics'
import { withDemoFallback, DEMO_PATIENTS } from '@/hooks/useDemoData'
import type { PatientAnalytics } from '@/types'

export function PatientAnalyticsPage() {
  const { data: raw, isLoading } = usePatientAnalytics()
  const { data, isDemo } = withDemoFallback<PatientAnalytics>(raw, DEMO_PATIENTS, isLoading)

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col lg:flex-row lg:items-start lg:justify-between gap-4">
        <PageHeader title="Patient Analytics" subtitle="Demographics and patient growth insights" />
        <ExportToolbar section="patients" isDemoData={isDemo} />
      </div>
      <AnalyticsFilter />

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4">
        <KpiCard title="Total Patients" value={data.totalPatients?.toLocaleString() ?? '0'} loading={isLoading} icon={<TeamOutlined />} color="primary" />
        <KpiCard title="New This Period" value={data.newPatientsThisPeriod?.toLocaleString() ?? '0'} loading={isLoading} icon={<UserAddOutlined />} color="success" />
        <KpiCard title="Returning Patients" value={data.returningPatients?.toLocaleString() ?? '0'} loading={isLoading} icon={<SyncOutlined />} color="cyan" />
        <KpiCard title="Retention Rate" value={`${data.retentionRatePct?.toFixed(1) ?? '0'}%`} loading={isLoading} icon={<RiseOutlined />} color="purple" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <Card title="Patient Registration Trend" className="medical-card" styles={{ body: { padding: '24px' } }}>
            {data.registrationTrend?.length ? (
              <ReactApexChart type="area" height={260}
                series={[{ name: 'New Patients', data: data.registrationTrend.map(p => p.value) }]}
                options={{ ...chartConfigs.area('#52c41a'), xaxis: { categories: data.registrationTrend.map(p => p.label) } }} />
            ) : <EmptyChart height={260} />}
          </Card>
        </div>
        <div>
          <Card title="Gender Distribution" className="medical-card" styles={{ body: { padding: '24px' } }}>
            {data.genderDistribution?.length ? (
              <ReactApexChart type="pie" height={260}
                series={data.genderDistribution.map(p => p.value)}
                options={{ ...chartConfigs.donut(), labels: data.genderDistribution.map(p => p.name) }} />
            ) : <EmptyChart height={260} />}
          </Card>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card title="Age Distribution" className="medical-card" styles={{ body: { padding: '24px' } }}>
          {data.ageDistribution?.length ? (
            <ReactApexChart type="bar" height={240}
              series={[{ name: 'Patients', data: data.ageDistribution.map(p => p.value) }]}
              options={{ ...chartConfigs.bar('#13c2c2'), xaxis: { categories: data.ageDistribution.map(p => p.name) } }} />
          ) : <EmptyChart height={240} />}
        </Card>
        <Card title="Blood Group Distribution" className="medical-card" styles={{ body: { padding: '24px' } }}>
          {data.bloodGroupDistribution?.length ? (
            <ReactApexChart type="bar" height={240}
              series={[{ name: 'Patients', data: data.bloodGroupDistribution.map(p => p.value) }]}
              options={{ ...chartConfigs.bar('#ff4d4f'), xaxis: { categories: data.bloodGroupDistribution.map(p => p.name) } }} />
          ) : <EmptyChart height={240} />}
        </Card>
      </div>
    </div>
  )
}
