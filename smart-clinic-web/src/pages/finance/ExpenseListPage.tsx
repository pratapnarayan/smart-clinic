import { useState } from 'react'
import { Table, Button, Tag, Select, DatePicker, Space, Card, Modal, Form, Input, InputNumber } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { PlusOutlined } from '@ant-design/icons'
import dayjs, { type Dayjs } from 'dayjs'
import { PageHeader } from '@/components/common'
import { useExpenseEntries, useCreateExpense, useExpenseCategories } from '@/hooks/useFinance'
import { useAuthStore } from '@/store/authStore'
import type { ExpenseEntry, PaymentMode, CreateExpensePayload } from '@/types'

const PAYMENT_COLOR: Record<PaymentMode, string> = {
  CASH: 'green', CARD: 'blue', UPI: 'purple',
  CHEQUE: 'orange', NEFT: 'cyan', OTHER: 'default',
}

const fmt = (v: number) =>
  `₹${Number(v ?? 0).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`

export function ExpenseListPage() {
  const { hasPermission } = useAuthStore()
  const [range, setRange]         = useState<[Dayjs, Dayjs]>([dayjs().startOf('month'), dayjs()])
  const [categoryId, setCategoryId] = useState<string | undefined>()
  const [page, setPage]           = useState(0)
  const [modalOpen, setModalOpen] = useState(false)
  const [form] = Form.useForm<CreateExpensePayload>()

  const params = {
    from:       range[0].format('YYYY-MM-DD'),
    to:         range[1].format('YYYY-MM-DD'),
    categoryId,
    page,
  }

  const { data, isLoading }                   = useExpenseEntries(params)
  const { data: categories = [] }             = useExpenseCategories()
  const { mutate: createExpense, isPending }  = useCreateExpense()

  const columns: ColumnsType<ExpenseEntry> = [
    { title: 'Entry No.',  dataIndex: 'entryNumber', width: 140 },
    { title: 'Date',       dataIndex: 'entryDate',   width: 110 },
    {
      title: 'Category', dataIndex: 'categoryName', width: 160,
      render: (v: string) => <Tag color="volcano">{v}</Tag>,
    },
    { title: 'Description', dataIndex: 'description', ellipsis: true },
    {
      title: 'Mode', dataIndex: 'paymentMode', width: 90,
      render: (v: PaymentMode) => <Tag color={PAYMENT_COLOR[v]}>{v}</Tag>,
    },
    { title: 'Paid To',    dataIndex: 'paidTo',      render: (v?: string) => v ?? '—' },
    { title: 'Ref No.',    dataIndex: 'referenceNo', render: (v?: string) => v ?? '—', width: 110 },
    { title: 'Approved By', dataIndex: 'approvedBy', render: (v?: string) => v ?? '—' },
    {
      title: 'Amount', dataIndex: 'amount', align: 'right', width: 120,
      render: fmt,
    },
  ]

  const onFinish = (values: CreateExpensePayload) => {
    createExpense(
      { ...values, entryDate: values.entryDate ? dayjs(values.entryDate as unknown as Dayjs).format('YYYY-MM-DD') : undefined },
      { onSuccess: () => { form.resetFields(); setModalOpen(false) } }
    )
  }

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader
        title="Expense Entries"
        subtitle="All operational expenditures"
        extra={
          hasPermission('FINANCE.CREATE') && (
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalOpen(true)}>
              New Expense
            </Button>
          )
        }
      />

      <Card
        className="medical-card"
        title={
          <Space wrap>
            <DatePicker.RangePicker
              value={range}
              onChange={v => { if (v?.[0] && v?.[1]) { setRange([v[0], v[1]]); setPage(0) } }}
            />
            <Select
              allowClear
              placeholder="All categories"
              style={{ width: 200 }}
              onChange={v => { setCategoryId(v as string | undefined); setPage(0) }}
              options={categories.map(c => ({ value: c.id, label: c.name }))}
            />
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

      <Modal
        title="New Expense Entry"
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={() => form.submit()}
        okText="Save"
        confirmLoading={isPending}
        width={560}
        destroyOnHidden
      >
        <Form form={form} layout="vertical" onFinish={onFinish}>
          <Space style={{ width: '100%' }} size={12}>
            <Form.Item name="entryDate" label="Date" style={{ width: 160 }}>
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item name="categoryId" label="Category" style={{ flex: 1 }}
              rules={[{ required: true }]}>
              <Select
                placeholder="Select category"
                options={categories.map(c => ({ value: c.id, label: c.name }))}
              />
            </Form.Item>
          </Space>

          <Space style={{ width: '100%' }} size={12}>
            <Form.Item name="paymentMode" label="Payment Mode" style={{ width: 160 }}
              rules={[{ required: true }]} initialValue="CASH">
              <Select options={['CASH','CARD','UPI','CHEQUE','NEFT','OTHER'].map(v => ({ value: v, label: v }))} />
            </Form.Item>
            <Form.Item name="amount" label="Amount (₹)" style={{ flex: 1 }}
              rules={[{ required: true }]}>
              <InputNumber style={{ width: '100%' }} min={0.01} precision={2} placeholder="0.00" />
            </Form.Item>
          </Space>

          <Form.Item name="description" label="Description" rules={[{ required: true }]}>
            <Input.TextArea rows={2} placeholder="What was this expense for?" />
          </Form.Item>

          <Space style={{ width: '100%' }} size={12}>
            <Form.Item name="paidTo" label="Paid To" style={{ flex: 1 }}>
              <Input placeholder="Vendor / payee name" />
            </Form.Item>
            <Form.Item name="referenceNo" label="Ref / Voucher No." style={{ flex: 1 }}>
              <Input placeholder="Optional reference" />
            </Form.Item>
          </Space>

          <Form.Item name="approvedBy" label="Approved By">
            <Input placeholder="Approving authority" />
          </Form.Item>

          <Form.Item name="notes" label="Notes">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
