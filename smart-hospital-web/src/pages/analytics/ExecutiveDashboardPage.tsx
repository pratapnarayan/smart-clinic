import ReactApexChart from 'react-apexcharts'
import { Card, Typography } from 'antd'
import { PageHeader } from '@/components/common'
import {
  KpiCard, EmptyChart, AnalyticsFilter, ExportToolbar,
} from '@/components/analytics'
import { useExecutiveDashboard } from '@/hooks/useAnalytics'
import { withDemoFallback, DEMO_EXECUTIVE } from '@/hooks/useDemoData'
import { chartConfigs, formatCurrency, formatCompactCurrency } from '@/theme/chartTheme'
import {
  DollarCircleOutlined, TeamOutlined, CalendarOutlined,
  CreditCardOutlined, MedicineBoxOutlined, ExperimentOutlined,
  ShoppingOutlined, AlertOutlined, RiseOutlined, SolutionOutlined,
} from '@ant-design/icons'
import type { ExecutiveDashboard } from '@/types'

const { Text } = Typography

interface KpiConfig {
  title: string
  key: keyof ExecutiveDashboard
  icon: React.ReactNode
  color: 'primary' | 'success' | 'warning' | 'danger' | 'purple' | 'cyan'
  format: (v: number) => string
}

const kpiConfig: KpiConfig[] = [
  { title: "Today's Revenue",       key: 'todayRevenue',          icon: <DollarCircleOutlined />, color: 'primary',  format: formatCurrency },
  { title: 'Month Revenue',         key: 'monthRevenue',          icon: <RiseOutlined />,         color: 'success',  format: formatCurrency },
  { title: 'Total Patients',        key: 'totalPatients',         icon: <TeamOutlined />,         color: 'cyan',     format: (v) => v.toLocaleString('en-IN') },
  { title: "Today's Appointments",  key: 'todayAppointments',     icon: <CalendarOutlined />,     color: 'purple',   format: (v) => v.toString() },
  { title: 'Pending Payments',      key: 'pendingPayments',       icon: <CreditCardOutlined />,   color: 'warning',  format: formatCurrency },
  { title: 'Doctors Available',     key: 'doctorsAvailableToday', icon: <SolutionOutlined />,     color: 'primary',  format: (v) => v.toString() },
  { title: 'Current Admissions',    key: 'currentAdmissions',     icon: <MedicineBoxOutlined />,  color: 'success',  format: (v) => v.toString() },
  { title: 'Lab Tests Today',       key: 'labTestsToday',         icon: <ExperimentOutlined />,   color: 'cyan',     format: (v) => v.toString() },
  { title: 'Medicine Sales',        key: 'medicineSalesToday',    icon: <ShoppingOutlined />,     color: 'purple',   format: formatCurrency },
  { title: 'Inventory Alerts',      key: 'inventoryAlerts',       icon: <AlertOutlined />,        color: 'danger',   format: (v) => v.toString() },
]

export function ExecutiveDashboardPage() {
  const { data: raw, isLoading } = useExecutiveDashboard()
  const { data, isDemo } = withDemoFallback<ExecutiveDashboard>(raw, DEMO_EXECUTIVE, isLoading)

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="flex flex-col lg:flex-row lg:items-start lg:justify-between gap-4">
        <PageHeader
          title="Executive Dashboard"
          subtitle="Real-time hospital performance overview and key metrics"
          breadcrumbs={[
            { title: 'Dashboard', href: '/dashboard' },
            { title: 'Analytics' },
            { title: 'Executive' },
          ]}
        />
        <ExportToolbar section="executive" isDemoData={isDemo} />
      </div>

      <AnalyticsFilter />

      {/* KPI Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
        {kpiConfig.map((kpi) => {
          const rawValue = data[kpi.key] as number ?? 0
          const trendKey = `${kpi.key}Trend` as keyof ExecutiveDashboard
          const trend = data[trendKey] as number | undefined ?? null
          return (
            <KpiCard
              key={kpi.title}
              title={kpi.title}
              value={kpi.format(rawValue)}
              trend={trend}
              subtitle="vs previous period"
              loading={isLoading}
              icon={kpi.icon}
              color={kpi.color}
            />
          )
        })}
      </div>

      {/* Charts Row 1: Revenue Trend (2/3) + Revenue by Source (1/3) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <Card
          className="medical-card lg:col-span-2"
          title={
            <div className="flex items-center justify-between">
              <span className="font-semibold" style={{ color: 'var(--text-primary)' }}>Revenue Trend</span>
              <Text type="secondary" className="text-xs">Last 30 Days</Text>
            </div>
          }
          styles={{ body: { padding: '24px' } }}
        >
          {data.revenueTrend?.length ? (
            <ReactApexChart
              type="area"
              height={300}
              series={[{ name: 'Revenue (₹)', data: data.revenueTrend.map((p) => p.value) }]}
              options={{
                ...chartConfigs.area('#1677ff'),
                xaxis: {
                  categories: data.revenueTrend.map((p) => p.label),
                  labels: { rotate: -45, style: { fontSize: '10px' } },
                },
                yaxis: { labels: { formatter: (v: number) => formatCompactCurrency(v) } },
              }}
            />
          ) : (
            <EmptyChart height={300} />
          )}
        </Card>

        <Card
          className="medical-card"
          title={<span className="font-semibold" style={{ color: 'var(--text-primary)' }}>Revenue by Source</span>}
          styles={{ body: { padding: '24px' } }}
        >
          {data.revenueBySource?.length ? (
            <ReactApexChart
              type="donut"
              height={300}
              series={data.revenueBySource.map((p) => p.value)}
              options={{
                ...chartConfigs.donut(),
                labels: data.revenueBySource.map((p) => p.name),
                plotOptions: {
                  pie: {
                    donut: {
                      labels: {
                        show: true,
                        total: {
                          show: true,
                          label: 'Total',
                          formatter: (w: { globals: { seriesTotals: number[] } }) =>
                            formatCurrency(w.globals.seriesTotals.reduce((a, b) => a + b, 0)),
                        },
                      },
                    },
                  },
                },
              }}
            />
          ) : (
            <EmptyChart height={300} />
          )}
        </Card>
      </div>

      {/* Charts Row 2: Patient Growth + Top Doctors */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card
          className="medical-card"
          title={<span className="font-semibold" style={{ color: 'var(--text-primary)' }}>Patient Growth Trend</span>}
          styles={{ body: { padding: '24px' } }}
        >
          {data.patientGrowth?.length ? (
            <ReactApexChart
              type="area"
              height={280}
              series={[{ name: 'New Patients', data: data.patientGrowth.map((p) => p.value) }]}
              options={{
                ...chartConfigs.area('#52c41a'),
                xaxis: { categories: data.patientGrowth.map((p) => p.label) },
              }}
            />
          ) : (
            <EmptyChart height={280} />
          )}
        </Card>

        <Card
          className="medical-card"
          title={<span className="font-semibold" style={{ color: 'var(--text-primary)' }}>Top Performing Doctors</span>}
          styles={{ body: { padding: '24px' } }}
        >
          {data.topDoctors?.length ? (
            <ReactApexChart
              type="bar"
              height={280}
              series={[{ name: 'Revenue (₹)', data: data.topDoctors.map((p) => p.value) }]}
              options={{
                ...chartConfigs.horizontalBar('#722ed1'),
                xaxis: {
                  categories: data.topDoctors.map((p) => p.name),
                  labels: {
                    formatter: (v: string) => {
                      const n = Number(v)
                      return isNaN(n) ? v : formatCompactCurrency(n)
                    },
                  },
                },
              }}
            />
          ) : (
            <EmptyChart height={280} />
          )}
        </Card>
      </div>

      {/* Charts Row 3: Department Performance */}
      <Card
        className="medical-card"
        title={<span className="font-semibold" style={{ color: 'var(--text-primary)' }}>Department Performance</span>}
        styles={{ body: { padding: '24px' } }}
      >
        {data.departmentRevenue?.length ? (
          <ReactApexChart
            type="bar"
            height={280}
            series={[{ name: 'Revenue (₹)', data: data.departmentRevenue.map((p) => p.value) }]}
            options={{
              ...chartConfigs.bar('#13c2c2'),
              xaxis: { categories: data.departmentRevenue.map((p) => p.name) },
              yaxis: { labels: { formatter: (v: number) => formatCompactCurrency(v) } },
            }}
          />
        ) : (
          <EmptyChart height={280} />
        )}
      </Card>
    </div>
  )
}
