import { Card, Table, Tag, Alert } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import {
  AppstoreOutlined, WarningOutlined, ArrowDownOutlined, ArrowUpOutlined,
} from '@ant-design/icons'
import { PageHeader } from '@/components/common'
import { KpiCard } from '@/components/analytics'
import { useInventoryDashboard } from '@/hooks/useInventory'
import type { InventoryItem } from '@/types'

const fmt = (v: number) =>
  `₹${Number(v ?? 0).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`

const lowStockColumns: ColumnsType<InventoryItem> = [
  { title: 'Code',     dataIndex: 'itemCode',  width: 120 },
  { title: 'Item',     dataIndex: 'name' },
  { title: 'Category', dataIndex: 'categoryName', width: 160 },
  { title: 'Unit',     dataIndex: 'unit', width: 80 },
  {
    title: 'Stock', dataIndex: 'currentStock', width: 100, align: 'right',
    render: (v: number, r: InventoryItem) => (
      <Tag color={v === 0 ? 'red' : 'orange'}>
        {v} {r.unit}
      </Tag>
    ),
  },
  {
    title: 'Reorder @', dataIndex: 'reorderLevel', width: 100, align: 'right',
    render: (v: number, r: InventoryItem) => `${v} ${r.unit}`,
  },
]

export function InventoryDashboardPage() {
  const { data: dash, isLoading } = useInventoryDashboard()

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader title="Inventory Overview" subtitle="Stock status and today's activity" />

      {(dash?.lowStockCount ?? 0) > 0 && (
        <Alert
          type="warning"
          showIcon
          icon={<WarningOutlined />}
          message={`${dash?.lowStockCount} item(s) are at or below reorder level`}
          style={{ marginBottom: 16 }}
        />
      )}

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard title="Total Items"     value={(dash?.totalItems ?? 0).toString()}     icon={<AppstoreOutlined />}   color="primary" loading={isLoading} />
        <KpiCard title="Low Stock Items" value={(dash?.lowStockCount ?? 0).toString()}  icon={<WarningOutlined />}    color={(dash?.lowStockCount ?? 0) > 0 ? 'danger' : 'success'} loading={isLoading} />
        <KpiCard title="Today — Receipts" value={(dash?.todayReceipts ?? 0).toString()} icon={<ArrowDownOutlined />}  color="success" loading={isLoading} subtitle={fmt(dash?.todayReceiptValue ?? 0)} />
        <KpiCard title="Today — Issues"  value={(dash?.todayIssues ?? 0).toString()}    icon={<ArrowUpOutlined />}    color="primary" loading={isLoading} />
      </div>

      <Card title="Items at or Below Reorder Level" loading={isLoading} className="medical-card">
        <Table
          rowKey="id"
          size="small"
          columns={lowStockColumns}
          dataSource={dash?.lowStockItems ?? []}
          pagination={false}
          locale={{ emptyText: 'All items are adequately stocked' }}
        />
      </Card>
    </div>
  )
}
