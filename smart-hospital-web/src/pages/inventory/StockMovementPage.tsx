import { useState } from 'react'
import { Table, Button, Tag, Select, DatePicker, Space, Card, Tabs } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import dayjs, { type Dayjs } from 'dayjs'
import { PageHeader } from '@/components/common'
import { useStockReceipts, useStockIssues, useInventoryItems } from '@/hooks/useInventory'
import type { StockReceipt, StockIssue } from '@/types'

const fmt = (v?: number) =>
  v != null ? `₹${Number(v).toLocaleString('en-IN', { minimumFractionDigits: 2 })}` : '—'

export function StockMovementPage() {
  const [range, setRange] = useState<[Dayjs, Dayjs]>([dayjs().startOf('month'), dayjs()])
  const [itemIdR, setItemIdR] = useState<string | undefined>()
  const [itemIdI, setItemIdI] = useState<string | undefined>()
  const [pageR, setPageR]     = useState(0)
  const [pageI, setPageI]     = useState(0)

  const from = range[0].format('YYYY-MM-DD')
  const to   = range[1].format('YYYY-MM-DD')

  const { data: receipts, isLoading: loadingR } = useStockReceipts({ from, to, itemId: itemIdR, page: pageR })
  const { data: issues,   isLoading: loadingI } = useStockIssues({ from, to, itemId: itemIdI, page: pageI })
  const { data: allItems } = useInventoryItems({ page: 0 })

  const itemOptions = (allItems?.content ?? []).map(i => ({ value: i.id, label: `${i.itemCode} — ${i.name}` }))

  const receiptColumns: ColumnsType<StockReceipt> = [
    { title: 'Receipt No.', dataIndex: 'receiptNumber', width: 140 },
    { title: 'Date',        dataIndex: 'entryDate',     width: 110 },
    { title: 'Item',        dataIndex: 'itemName' },
    { title: 'Qty',         dataIndex: 'quantity', align: 'right', width: 70,
      render: (v: number, r: StockReceipt) => `${v} ${r.itemUnit}` },
    { title: 'Unit Cost',   dataIndex: 'unitCost',  align: 'right', width: 100, render: fmt },
    { title: 'Total Cost',  dataIndex: 'totalCost', align: 'right', width: 110,
      render: (v?: number) => v ? <strong>{fmt(v)}</strong> : '—' },
    { title: 'Supplier',    dataIndex: 'supplierName', render: (v?: string) => v ?? '—' },
    { title: 'GRN No.',     dataIndex: 'grnNumber',    render: (v?: string) => v ?? '—', width: 110 },
    { title: 'Received By', dataIndex: 'receivedBy',   render: (v?: string) => v ?? '—' },
  ]

  const issueColumns: ColumnsType<StockIssue> = [
    { title: 'Issue No.',   dataIndex: 'issueNumber', width: 140 },
    { title: 'Date',        dataIndex: 'issueDate',   width: 110 },
    { title: 'Item',        dataIndex: 'itemName' },
    { title: 'Qty',         dataIndex: 'quantity', align: 'right', width: 70,
      render: (v: number, r: StockIssue) => `${v} ${r.itemUnit}` },
    { title: 'Issued To',   dataIndex: 'issuedTo',
      render: (v: string) => <Tag color="blue">{v}</Tag> },
    { title: 'Issued By',   dataIndex: 'issuedBy',   render: (v?: string) => v ?? '—' },
    { title: 'Purpose',     dataIndex: 'purpose',    render: (v?: string) => v ?? '—', ellipsis: true },
  ]

  const dateFilter = (
    <DatePicker.RangePicker
      value={range}
      onChange={v => { if (v?.[0] && v?.[1]) { setRange([v[0], v[1]]); setPageR(0); setPageI(0) } }}
    />
  )

  return (
    <>
      <PageHeader
        title="Stock Movements"
        subtitle="All goods received and issued to departments"
      />

      <Tabs
        defaultActiveKey="receipts"
        items={[
          {
            key: 'receipts',
            label: `Receipts (${receipts?.total ?? 0})`,
            children: (
              <Card
                title={
                  <Space wrap>
                    {dateFilter}
                    <Select
                      allowClear showSearch filterOption={false}
                      placeholder="Filter by item" style={{ width: 260 }}
                      onChange={v => { setItemIdR(v); setPageR(0) }}
                      options={itemOptions}
                    />
                  </Space>
                }
              >
                <Table
                  rowKey="id" size="small"
                  columns={receiptColumns}
                  dataSource={receipts?.content ?? []}
                  loading={loadingR}
                  pagination={{
                    current: pageR + 1, pageSize: 20, total: receipts?.total ?? 0,
                    onChange: p => setPageR(p - 1),
                  }}
                />
              </Card>
            ),
          },
          {
            key: 'issues',
            label: `Issues (${issues?.total ?? 0})`,
            children: (
              <Card
                title={
                  <Space wrap>
                    {dateFilter}
                    <Select
                      allowClear showSearch filterOption={false}
                      placeholder="Filter by item" style={{ width: 260 }}
                      onChange={v => { setItemIdI(v); setPageI(0) }}
                      options={itemOptions}
                    />
                  </Space>
                }
              >
                <Table
                  rowKey="id" size="small"
                  columns={issueColumns}
                  dataSource={issues?.content ?? []}
                  loading={loadingI}
                  pagination={{
                    current: pageI + 1, pageSize: 20, total: issues?.total ?? 0,
                    onChange: p => setPageI(p - 1),
                  }}
                />
              </Card>
            ),
          },
        ]}
      />
    </>
  )
}
