import { useState } from 'react'
import { Table, Input, Tag, Button, Card, Alert, Tabs, Badge, type TableProps } from 'antd'
import { PlusOutlined, SearchOutlined, WarningOutlined } from '@ant-design/icons'
import { useMedicines, useLowStockMedicines, useExpiringBatches } from '@/hooks/usePharmacy'
import { PageHeader } from '@/components/common/PageHeader'
import { formatDate, formatCurrency } from '@/utils'
import type { Medicine, MedicineBatch } from '@/types'

export function StockPage() {
  const [query, setQuery] = useState('')
  const [page, setPage] = useState(0)

  const { data: medicines, isLoading } = useMedicines(query || undefined, page)
  const { data: lowStock } = useLowStockMedicines()
  const { data: expiring } = useExpiringBatches(30)

  const medicineColumns: TableProps<Medicine>['columns'] = [
    { title: 'Name', dataIndex: 'name' },
    { title: 'Generic', dataIndex: 'genericName', render: (v) => v ?? '—' },
    { title: 'Category', dataIndex: 'categoryName' },
    { title: 'Unit', dataIndex: 'unit' },
    {
      title: 'Stock', dataIndex: 'availableStock',
      render: (v, r) => (
        <Tag color={v <= r.reorderLevel ? 'red' : 'green'}>
          {v <= r.reorderLevel && <WarningOutlined />} {v} {r.unit}
        </Tag>
      ),
    },
    { title: 'Reorder @', dataIndex: 'reorderLevel' },
  ]

  const batchColumns: TableProps<MedicineBatch>['columns'] = [
    { title: 'Medicine', dataIndex: 'medicineName' },
    { title: 'Batch #', dataIndex: 'batchNumber' },
    { title: 'Expiry', dataIndex: 'expiryDate', render: formatDate },
    { title: 'Qty', dataIndex: 'quantity' },
    { title: 'Sale Price', dataIndex: 'salePrice', render: formatCurrency },
    {
      title: 'Status', key: 'status',
      render: (_, r) => r.expired
        ? <Tag color="red">EXPIRED</Tag>
        : r.lowStock ? <Tag color="orange">LOW STOCK</Tag>
        : <Tag color="green">OK</Tag>,
    },
  ]

  return (
    <div>
      <PageHeader
        title="Pharmacy Stock"
        subtitle="Medicine catalogue and batch management"
        breadcrumbs={[{ title: 'Dashboard', href: '/dashboard' }, { title: 'Pharmacy Stock' }]}
        extra={
          <Button type="primary" icon={<PlusOutlined />} disabled>
            Add Medicine
          </Button>
        }
      />

      {lowStock && lowStock.length > 0 && (
        <Alert
          type="error" showIcon className="mb-4"
          message={`${lowStock.length} medicine(s) need restocking`}
        />
      )}
      {expiring && expiring.length > 0 && (
        <Alert
          type="warning" showIcon className="mb-4"
          message={`${expiring.length} batch(es) expiring within 30 days`}
        />
      )}

      <Tabs defaultActiveKey="catalogue" items={[
        {
          key: 'catalogue',
          label: 'Catalogue',
          children: (
            <>
              <Input.Search
                placeholder="Search medicines…"
                allowClear
                enterButton={<SearchOutlined />}
                style={{ maxWidth: 380, marginBottom: 16 }}
                onSearch={setQuery}
                onChange={(e) => !e.target.value && setQuery('')}
              />
              <Table
                rowKey="id"
                dataSource={medicines?.content}
                columns={medicineColumns}
                loading={isLoading}
                pagination={{
                  current: (medicines?.page ?? 0) + 1,
                  pageSize: medicines?.size ?? 20,
                  total: medicines?.total ?? 0,
                  onChange: (p) => setPage(p - 1),
                }}
              />
            </>
          ),
        },
        {
          key: 'expiring',
          label: (
            <Badge count={expiring?.length ?? 0} offset={[8, 0]}>
              Expiring Soon
            </Badge>
          ),
          children: (
            <Table
              rowKey="id"
              size="small"
              dataSource={expiring}
              columns={batchColumns}
              pagination={false}
            />
          ),
        },
        {
          key: 'lowstock',
          label: (
            <Badge count={lowStock?.length ?? 0} offset={[8, 0]}>
              Low Stock
            </Badge>
          ),
          children: (
            <Table
              rowKey="id"
              size="small"
              dataSource={lowStock}
              columns={[
                { title: 'Medicine', dataIndex: 'name' },
                { title: 'Category', dataIndex: 'categoryName' },
                { title: 'Available', dataIndex: 'availableStock' },
                { title: 'Reorder @', dataIndex: 'reorderLevel' },
              ]}
              pagination={false}
            />
          ),
        },
      ]} />
    </div>
  )
}
