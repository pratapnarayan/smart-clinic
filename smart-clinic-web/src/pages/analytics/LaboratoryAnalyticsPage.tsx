import ReactApexChart from 'react-apexcharts'
import { Card } from 'antd'
import { ExperimentOutlined, DollarCircleOutlined, FileSearchOutlined } from '@ant-design/icons'
import { PageHeader } from '@/components/common'
import { KpiCard, EmptyChart, AnalyticsFilter, ExportToolbar } from '@/components/analytics'
import { chartConfigs, formatCurrency } from '@/theme/chartTheme'
import { useLaboratoryAnalytics } from '@/hooks/useAnalytics'
import { withDemoFallback, DEMO_LABORATORY } from '@/hooks/useDemoData'
import type { LaboratoryAnalytics } from '@/types'

export function LaboratoryAnalyticsPage() {
  const { data: raw, isLoading } = useLaboratoryAnalytics()
  const { data, isDemo } = withDemoFallback<LaboratoryAnalytics>(raw, DEMO_LABORATORY, isLoading)

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col lg:flex-row lg:items-start lg:justify-between gap-4">
        <PageHeader title="Laboratory Analytics" subtitle="Test volumes, revenue and departmental referrals" />
        <ExportToolbar section="laboratory" isDemoData={isDemo} />
      </div>
      <AnalyticsFilter />

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <KpiCard title="Tests Performed" value={data.totalTestsPerformed?.toLocaleString() ?? '0'} loading={isLoading} icon={<ExperimentOutlined />} color="cyan" />
        <KpiCard title="Total Revenue" value={formatCurrency(data.totalRevenue)} loading={isLoading} icon={<DollarCircleOutlined />} color="success" />
        <KpiCard title="Pending Reports" value={data.pendingReports?.toString() ?? '0'} loading={isLoading} icon={<FileSearchOutlined />} color="warning" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <Card title="Daily Tests Trend" className="medical-card" styles={{ body: { padding: '24px' } }}>
            {data.dailyTestsTrend?.length ? (
              <ReactApexChart type="area" height={260}
                series={[{ name: 'Tests', data: data.dailyTestsTrend.map(p => p.value) }]}
                options={{ ...chartConfigs.area('#722ed1'), xaxis: { categories: data.dailyTestsTrend.map(p => p.label), labels: { rotate: -45, style: { fontSize: '10px' } } } }} />
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
        <Card title="Top 10 Tests by Volume" className="medical-card" styles={{ body: { padding: '24px' } }}>
          {data.topTests?.length ? (
            <ReactApexChart type="bar" height={240}
              series={[{ name: 'Tests', data: data.topTests.map(p => p.value) }]}
              options={{ ...chartConfigs.horizontalBar('#722ed1'), xaxis: { categories: data.topTests.map(p => p.name) } }} />
          ) : <EmptyChart height={240} />}
        </Card>
        <Card title="Referrals by Department" className="medical-card" styles={{ body: { padding: '24px' } }}>
          {data.byDepartmentReferral?.length ? (
            <ReactApexChart type="bar" height={240}
              series={[{ name: 'Referrals', data: data.byDepartmentReferral.map(p => p.value) }]}
              options={{ ...chartConfigs.bar('#13c2c2'), xaxis: { categories: data.byDepartmentReferral.map(p => p.name) } }} />
          ) : <EmptyChart height={240} />}
        </Card>
      </div>
    </div>
  )
}
