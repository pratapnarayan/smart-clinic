import { useState } from 'react'
import {
  Table, Button, Tag, Select, Input, Switch, Card, Space, Modal, Form, InputNumber,
} from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { PlusOutlined, WarningOutlined, ArrowDownOutlined, ArrowUpOutlined } from '@ant-design/icons'
import { PageHeader } from '@/components/common'
import {
  useInventoryItems, useItemCategories, useCreateInventoryItem,
  useRecordReceipt, useRecordIssue,
} from '@/hooks/useInventory'
import { useAuthStore } from '@/store/authStore'
import type { InventoryItem, CreateInventoryItemPayload, RecordReceiptPayload, RecordIssuePayload } from '@/types'

export function ItemListPage() {
  const { hasPermission } = useAuthStore()
  const [q, setQ]                     = useState('')
  const [categoryId, setCategoryId]   = useState<string | undefined>()
  const [lowStock, setLowStock]       = useState(false)
  const [page, setPage]               = useState(0)
  const [addOpen, setAddOpen]         = useState(false)
  const [receiptOpen, setReceiptOpen] = useState(false)
  const [issueOpen, setIssueOpen]     = useState(false)
  const [activeItem, setActiveItem]   = useState<InventoryItem | null>(null)

  const [addForm]     = Form.useForm<CreateInventoryItemPayload>()
  const [receiptForm] = Form.useForm<RecordReceiptPayload>()
  const [issueForm]   = Form.useForm<RecordIssuePayload>()

  const params = { q: q || undefined, categoryId, lowStock: lowStock || undefined, page }

  const { data, isLoading }                 = useInventoryItems(params)
  const { data: categories = [] }           = useItemCategories()
  const { mutate: createItem, isPending: creating }   = useCreateInventoryItem()
  const { mutate: recordReceipt, isPending: receiving } = useRecordReceipt()
  const { mutate: recordIssue,   isPending: issuing }   = useRecordIssue()

  const openReceipt = (item: InventoryItem) => {
    setActiveItem(item)
    receiptForm.resetFields()
    receiptForm.setFieldValue('itemId', item.id)
    setReceiptOpen(true)
  }
  const openIssue = (item: InventoryItem) => {
    setActiveItem(item)
    issueForm.resetFields()
    issueForm.setFieldValue('itemId', item.id)
    setIssueOpen(true)
  }

  const columns: ColumnsType<InventoryItem> = [
    { title: 'Code',     dataIndex: 'itemCode', width: 120 },
    { title: 'Name',     dataIndex: 'name' },
    { title: 'Category', dataIndex: 'categoryName', width: 160 },
    { title: 'Unit',     dataIndex: 'unit', width: 70 },
    {
      title: 'Stock', dataIndex: 'currentStock', width: 100, align: 'right',
      render: (v: number, r: InventoryItem) => (
        <Tag color={v === 0 ? 'red' : r.lowStock ? 'orange' : 'green'}>
          {r.lowStock && <WarningOutlined style={{ marginRight: 4 }} />}
          {v} {r.unit}
        </Tag>
      ),
    },
    { title: 'Reorder @', dataIndex: 'reorderLevel', width: 90, align: 'right',
      render: (v: number, r: InventoryItem) => `${v} ${r.unit}` },
    hasPermission('INVENTORY.CREATE') ? {
      title: 'Actions', key: 'actions', width: 180,
      render: (_: unknown, r: InventoryItem) => (
        <Space size={4}>
          <Button size="small" icon={<ArrowDownOutlined />} onClick={() => openReceipt(r)}>
            Receive
          </Button>
          <Button size="small" icon={<ArrowUpOutlined />} onClick={() => openIssue(r)}
            disabled={r.currentStock === 0}>
            Issue
          </Button>
        </Space>
      ),
    } : {},
  ].filter(c => Object.keys(c).length > 0)

  return (
    <>
      <PageHeader
        title="Inventory Items"
        subtitle="Item catalogue with current stock levels"
        extra={
          hasPermission('INVENTORY.CREATE') && (
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setAddOpen(true)}>
              Add Item
            </Button>
          )
        }
      />

      <Card
        title={
          <Space wrap>
            <Input.Search
              placeholder="Search by name or code…"
              allowClear
              style={{ width: 240 }}
              onSearch={v => { setQ(v); setPage(0) }}
              onChange={e => !e.target.value && setQ('')}
            />
            <Select
              allowClear placeholder="All categories" style={{ width: 180 }}
              onChange={v => { setCategoryId(v); setPage(0) }}
              options={categories.map(c => ({ value: c.id, label: c.name }))}
            />
            <Space>
              <Switch checked={lowStock} onChange={v => { setLowStock(v); setPage(0) }} />
              <span>Low stock only</span>
            </Space>
          </Space>
        }
      >
        <Table
          rowKey="id"
          size="small"
          columns={columns}
          dataSource={data?.content ?? []}
          loading={isLoading}
          pagination={{
            current: page + 1, pageSize: 20, total: data?.total ?? 0,
            onChange: p => setPage(p - 1),
          }}
        />
      </Card>

      {/* Add Item Modal */}
      <Modal title="Add Inventory Item" open={addOpen}
        onCancel={() => setAddOpen(false)} onOk={() => addForm.submit()}
        okText="Add Item" confirmLoading={creating} destroyOnHidden width={520}>
        <Form form={addForm} layout="vertical"
          onFinish={v => createItem(v, { onSuccess: () => { addForm.resetFields(); setAddOpen(false) } })}>
          <Space style={{ width: '100%' }} size={12}>
            <Form.Item name="itemCode" label="Item Code" style={{ width: 140 }}
              rules={[{ required: true }]}>
              <Input placeholder="e.g. MED-001" />
            </Form.Item>
            <Form.Item name="name" label="Name" style={{ flex: 1 }} rules={[{ required: true }]}>
              <Input />
            </Form.Item>
          </Space>
          <Space style={{ width: '100%' }} size={12}>
            <Form.Item name="categoryId" label="Category" style={{ flex: 1 }}
              rules={[{ required: true }]}>
              <Select options={categories.map(c => ({ value: c.id, label: c.name }))} />
            </Form.Item>
            <Form.Item name="unit" label="Unit" style={{ width: 120 }}
              rules={[{ required: true }]}>
              <Input placeholder="Piece / Box / Kg" />
            </Form.Item>
            <Form.Item name="reorderLevel" label="Reorder @" style={{ width: 120 }}
              initialValue={10}>
              <InputNumber min={0} style={{ width: '100%' }} />
            </Form.Item>
          </Space>
          <Form.Item name="description" label="Description">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>

      {/* Record Receipt Modal */}
      <Modal title={`Record Receipt — ${activeItem?.name ?? ''}`}
        open={receiptOpen} onCancel={() => setReceiptOpen(false)}
        onOk={() => receiptForm.submit()} okText="Save Receipt"
        confirmLoading={receiving} destroyOnHidden width={500}>
        <Form form={receiptForm} layout="vertical"
          onFinish={v => recordReceipt(v, { onSuccess: () => { receiptForm.resetFields(); setReceiptOpen(false) } })}>
          <Form.Item name="itemId" hidden><Input /></Form.Item>
          <Space style={{ width: '100%' }} size={12}>
            <Form.Item name="quantity" label={`Qty (${activeItem?.unit ?? ''})`} style={{ width: 140 }}
              rules={[{ required: true }]}>
              <InputNumber min={1} style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item name="unitCost" label="Unit Cost (₹)" style={{ width: 140 }}>
              <InputNumber min={0} precision={2} style={{ width: '100%' }} />
            </Form.Item>
          </Space>
          <Space style={{ width: '100%' }} size={12}>
            <Form.Item name="supplierName" label="Supplier" style={{ flex: 1 }}>
              <Input placeholder="Supplier name" />
            </Form.Item>
            <Form.Item name="grnNumber" label="GRN / Invoice No." style={{ flex: 1 }}>
              <Input />
            </Form.Item>
          </Space>
          <Form.Item name="receivedBy" label="Received By">
            <Input />
          </Form.Item>
          <Form.Item name="notes" label="Notes">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>

      {/* Record Issue Modal */}
      <Modal title={`Issue Stock — ${activeItem?.name ?? ''}`}
        open={issueOpen} onCancel={() => setIssueOpen(false)}
        onOk={() => issueForm.submit()} okText="Confirm Issue"
        confirmLoading={issuing} destroyOnHidden width={500}>
        <Form form={issueForm} layout="vertical"
          onFinish={v => recordIssue(v, { onSuccess: () => { issueForm.resetFields(); setIssueOpen(false) } })}>
          <Form.Item name="itemId" hidden><Input /></Form.Item>
          <Space style={{ width: '100%' }} size={12}>
            <Form.Item name="quantity" label={`Qty (${activeItem?.unit ?? ''})`} style={{ width: 140 }}
              rules={[{ required: true }]}
              help={activeItem ? `Available: ${activeItem.currentStock} ${activeItem.unit}` : undefined}>
              <InputNumber min={1} max={activeItem?.currentStock} style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item name="issuedTo" label="Issued To (Dept.)" style={{ flex: 1 }}
              rules={[{ required: true }]}>
              <Input placeholder="Ward / Department name" />
            </Form.Item>
          </Space>
          <Space style={{ width: '100%' }} size={12}>
            <Form.Item name="issuedBy" label="Issued By" style={{ flex: 1 }}>
              <Input />
            </Form.Item>
            <Form.Item name="purpose" label="Purpose" style={{ flex: 1 }}>
              <Input placeholder="Reason for issue" />
            </Form.Item>
          </Space>
          <Form.Item name="notes" label="Notes">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
