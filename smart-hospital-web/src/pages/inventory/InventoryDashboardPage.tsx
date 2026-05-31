import { Card, Row, Col, Statistic, Table, Tag, Alert } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import {
  AppstoreOutlined, WarningOutlined, ArrowDownOutlined, ArrowUpOutlined,
} from '@ant-design/icons'
import { PageHeader } from '@/components/common'
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
    <>
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

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={12} md={6}>
          <Card loading={isLoading}>
            <Statistic
              title="Total Items"
              value={dash?.totalItems ?? 0}
              prefix={<AppstoreOutlined />}
            />
          </Card>
        </Col>
        <Col xs={12} md={6}>
          <Card loading={isLoading}>
            <Statistic
              title="Low Stock Items"
              value={dash?.lowStockCount ?? 0}
              valueStyle={{ color: (dash?.lowStockCount ?? 0) > 0 ? '#ff4d4f' : '#52c41a' }}
              prefix={<WarningOutlined />}
            />
          </Card>
        </Col>
        <Col xs={12} md={6}>
          <Card loading={isLoading}>
            <Statistic
              title="Today — Receipts"
              value={dash?.todayReceipts ?? 0}
              valueStyle={{ color: '#52c41a' }}
              prefix={<ArrowDownOutlined />}
              suffix={<span style={{ fontSize: 12, color: '#888', marginLeft: 4 }}>
                ({fmt(dash?.todayReceiptValue ?? 0)})
              </span>}
            />
          </Card>
        </Col>
        <Col xs={12} md={6}>
          <Card loading={isLoading}>
            <Statistic
              title="Today — Issues"
              value={dash?.todayIssues ?? 0}
              valueStyle={{ color: '#1677ff' }}
              prefix={<ArrowUpOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Card title="Items at or Below Reorder Level" loading={isLoading}>
        <Table
          rowKey="id"
          size="small"
          columns={lowStockColumns}
          dataSource={dash?.lowStockItems ?? []}
          pagination={false}
          locale={{ emptyText: 'All items are adequately stocked' }}
        />
      </Card>
    </>
  )
}
