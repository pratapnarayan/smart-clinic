import ReactApexChart from 'react-apexcharts'
import { Card } from 'antd'
import { ShoppingOutlined, FileTextOutlined, WarningOutlined, AlertOutlined } from '@ant-design/icons'
import { PageHeader } from '@/components/common'
import { KpiCard, EmptyChart, AnalyticsFilter, ExportToolbar } from '@/components/analytics'
import { chartConfigs, formatCurrency } from '@/theme/chartTheme'
import { usePharmacyAnalytics } from '@/hooks/useAnalytics'
import { withDemoFallback, DEMO_PHARMACY } from '@/hooks/useDemoData'
import type { PharmacyAnalytics } from '@/types'

export function PharmacyAnalyticsPage() {
  const { data: raw, isLoading } = usePharmacyAnalytics()
  const { data, isDemo } = withDemoFallback<PharmacyAnalytics>(raw, DEMO_PHARMACY, isLoading)

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col lg:flex-row lg:items-start lg:justify-between gap-4">
        <PageHeader title="Pharmacy Analytics" subtitle="Medicine sales, stock health and category performance" />
        <ExportToolbar section="pharmacy" isDemoData={isDemo} />
      </div>
      <AnalyticsFilter />

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4">
        <KpiCard title="Total Revenue" value={formatCurrency(data.totalMedicineRevenue)} loading={isLoading} icon={<ShoppingOutlined />} color="success" />
        <KpiCard title="Bills Issued" value={data.totalBillsIssued?.toLocaleString() ?? '0'} loading={isLoading} icon={<FileTextOutlined />} color="primary" />
        <KpiCard title="Low Stock Alerts" value={data.lowStockAlerts?.toString() ?? '0'} loading={isLoading} icon={<WarningOutlined />} color="warning" />
        <KpiCard title="Expiry Alerts" value={data.expiryAlerts?.toString() ?? '0'} loading={isLoading} icon={<AlertOutlined />} color="danger" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <Card title="Revenue Trend" className="medical-card" styles={{ body: { padding: '24px' } }}>
            {data.revenueTrend?.length ? (
              <ReactApexChart type="area" height={260}
                series={[{ name: 'Revenue (₹)', data: data.revenueTrend.map(p => p.value) }]}
                options={{ ...chartConfigs.area('#fa8c16'), xaxis: { categories: data.revenueTrend.map(p => p.label), labels: { rotate: -45, style: { fontSize: '10px' } } }, yaxis: { labels: { formatter: (v: number) => formatCurrency(v) } } }} />
            ) : <EmptyChart height={260} />}
          </Card>
        </div>
        <div>
          <Card title="Stock Health" className="medical-card" styles={{ body: { padding: '24px' } }}>
            {data.stockHealthDistribution?.length ? (
              <ReactApexChart type="donut" height={260}
                series={data.stockHealthDistribution.map(p => p.value)}
                options={{ ...chartConfigs.donut(), labels: data.stockHealthDistribution.map(p => p.name) }} />
            ) : <EmptyChart height={260} />}
          </Card>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card title="Top 10 Medicines by Revenue" className="medical-card" styles={{ body: { padding: '24px' } }}>
          {data.topMedicinesByRevenue?.length ? (
            <ReactApexChart type="bar" height={240}
              series={[{ name: 'Revenue (₹)', data: data.topMedicinesByRevenue.map(p => p.value) }]}
              options={{ ...chartConfigs.horizontalBar('#fa8c16'), xaxis: { categories: data.topMedicinesByRevenue.map(p => p.name) } }} />
          ) : <EmptyChart height={240} />}
        </Card>
        <Card title="Revenue by Category" className="medical-card" styles={{ body: { padding: '24px' } }}>
          {data.revenueByCategory?.length ? (
            <ReactApexChart type="bar" height={240}
              series={[{ name: 'Revenue (₹)', data: data.revenueByCategory.map(p => p.value) }]}
              options={{ ...chartConfigs.bar('#13c2c2'), xaxis: { categories: data.revenueByCategory.map(p => p.name) } }} />
          ) : <EmptyChart height={240} />}
        </Card>
      </div>
    </div>
  )
}
