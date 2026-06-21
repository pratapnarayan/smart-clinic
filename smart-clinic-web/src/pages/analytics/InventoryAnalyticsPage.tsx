import ReactApexChart from 'react-apexcharts'
import { Card, Table, Tag } from 'antd'
import { WarningOutlined, DollarCircleOutlined, AppstoreOutlined, AlertOutlined } from '@ant-design/icons'
import { PageHeader } from '@/components/common'
import { KpiCard, EmptyChart, AnalyticsFilter, ExportToolbar } from '@/components/analytics'
import { chartConfigs, formatCurrency } from '@/theme/chartTheme'
import { useInventoryAnalytics } from '@/hooks/useAnalytics'
import { withDemoFallback, DEMO_INVENTORY } from '@/hooks/useDemoData'
import type { LowStockEntry, InventoryAnalytics } from '@/types'

export function InventoryAnalyticsPage() {
  const { data: raw, isLoading } = useInventoryAnalytics()
  const { data, isDemo } = withDemoFallback<InventoryAnalytics>(raw, DEMO_INVENTORY, isLoading)

  const lowStockColumns = [
    { title: 'Item', dataIndex: 'itemName', key: 'itemName' },
    { title: 'Category', dataIndex: 'category', key: 'category' },
    { title: 'Current Stock', dataIndex: 'currentStock', key: 'stock', render: (v: number, r: LowStockEntry) => (
      <Tag color={v === 0 ? 'red' : v <= r.reorderLevel / 2 ? 'orange' : 'gold'}>{v}</Tag>
    )},
    { title: 'Reorder Level', dataIndex: 'reorderLevel', key: 'reorder' },
  ]

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col lg:flex-row lg:items-start lg:justify-between gap-4">
        <PageHeader title="Inventory Analytics" subtitle="Stock value, movement and low stock alerts" />
        <ExportToolbar section="inventory" isDemoData={isDemo} />
      </div>
      <AnalyticsFilter />

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4">
        <KpiCard title="Total Stock Value" value={formatCurrency(data.totalStockValue)} loading={isLoading} icon={<DollarCircleOutlined />} color="primary" />
        <KpiCard title="Total Items" value={data.totalItems?.toLocaleString() ?? '0'} loading={isLoading} icon={<AppstoreOutlined />} color="cyan" />
        <KpiCard title="Low Stock Items" value={data.lowStockItems?.toString() ?? '0'} loading={isLoading} icon={<AlertOutlined />} color="warning" />
        <KpiCard title="Out of Stock" value={data.outOfStockItems?.toString() ?? '0'} loading={isLoading} icon={<WarningOutlined />} color="danger" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <Card title="Stock Value Trend" className="medical-card" styles={{ body: { padding: '24px' } }}>
            {data.stockValueTrend?.length ? (
              <ReactApexChart type="area" height={260}
                series={[{ name: 'Stock Value (₹)', data: data.stockValueTrend.map(p => p.value) }]}
                options={{ ...chartConfigs.area('#2f54eb'), xaxis: { categories: data.stockValueTrend.map(p => p.label), labels: { rotate: -45, style: { fontSize: '10px' } } }, yaxis: { labels: { formatter: (v: number) => formatCurrency(v) } } }} />
            ) : <EmptyChart height={260} />}
          </Card>
        </div>
        <div>
          <Card title="Stock by Category" className="medical-card" styles={{ body: { padding: '24px' } }}>
            {data.stockByCategory?.length ? (
              <ReactApexChart type="donut" height={260}
                series={data.stockByCategory.map(p => p.value)}
                options={{ ...chartConfigs.donut(), labels: data.stockByCategory.map(p => p.name) }} />
            ) : <EmptyChart height={260} />}
          </Card>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card title="Fast Moving Items (Top 10)" className="medical-card" styles={{ body: { padding: '24px' } }}>
          {data.fastMovingItems?.length ? (
            <ReactApexChart type="bar" height={240}
              series={[{ name: 'Units Issued', data: data.fastMovingItems.map(p => p.value) }]}
              options={{ ...chartConfigs.horizontalBar('#52c41a'), xaxis: { categories: data.fastMovingItems.map(p => p.name) } }} />
          ) : <EmptyChart height={240} />}
        </Card>
        <Card title="Slow Moving Items" className="medical-card" styles={{ body: { padding: '24px' } }}>
          {data.slowMovingItems?.length ? (
            <ReactApexChart type="bar" height={240}
              series={[{ name: 'Units Issued', data: data.slowMovingItems.map(p => p.value) }]}
              options={{ ...chartConfigs.horizontalBar('#ff4d4f'), xaxis: { categories: data.slowMovingItems.map(p => p.name) } }} />
          ) : <EmptyChart height={240} />}
        </Card>
      </div>

      <Card title={<span><WarningOutlined style={{ color: '#faad14', marginRight: 8 }} />Low Stock Alert List</span>} className="medical-card" styles={{ body: { padding: '24px' } }}>
        <Table
          dataSource={data.lowStockList ?? []}
          columns={lowStockColumns}
          rowKey="itemName"
          size="small"
          pagination={{ pageSize: 10 }}
          loading={isLoading}
          locale={{ emptyText: 'All items are sufficiently stocked' }}
        />
      </Card>
    </div>
  )
}
