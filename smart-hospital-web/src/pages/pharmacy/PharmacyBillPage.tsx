import { useState } from 'react'
import {
  Card, Form, Select, InputNumber, Button, Table, Tag, Divider,
  Alert, Typography, type TableProps
} from 'antd'
import { PlusOutlined, DeleteOutlined, PrinterOutlined } from '@ant-design/icons'
import { useCreateBill } from '@/hooks/usePharmacy'
import { useMedicines, useStockSummary } from '@/hooks/usePharmacy'
import { usePatients } from '@/hooks/usePatients'
import { PageHeader } from '@/components/common/PageHeader'
import { formatCurrency } from '@/utils'
import type { PharmacyBill, BillCreateRequest } from '@/types'

interface BillLineItem {
  batchId: string
  medicineName: string
  batchNumber: string
  quantity: number
  unitPrice: number
}

export function PharmacyBillPage() {
  const [form] = Form.useForm()
  const [items, setItems] = useState<BillLineItem[]>([])
  const [discount, setDiscount] = useState(0)
  const [createdBill, setCreatedBill] = useState<PharmacyBill | null>(null)

  const [patientQuery, setPatientQuery] = useState('')
  const [medQuery, setMedQuery] = useState('')
  const [selectedMedId, setSelectedMedId] = useState<string>()

  const { mutate: createBill, isPending } = useCreateBill()
  const { data: patients } = usePatients(patientQuery || undefined)
  const { data: medicines } = useMedicines(medQuery || undefined)
  const { data: stock } = useStockSummary(selectedMedId ?? '')

  const total = items.reduce((s, i) => s + i.quantity * i.unitPrice, 0)
  const net   = total - discount

  function addItem() {
    const batchId = form.getFieldValue('batchId')
    const qty     = form.getFieldValue('qty') ?? 1
    const batch   = stock?.batches.find((b) => b.id === batchId)
    if (!batch) return

    setItems((prev) => {
      const existing = prev.find((i) => i.batchId === batchId)
      if (existing) {
        return prev.map((i) => i.batchId === batchId ? { ...i, quantity: i.quantity + qty } : i)
      }
      return [...prev, {
        batchId: batch.id,
        medicineName: batch.medicineName,
        batchNumber: batch.batchNumber,
        quantity: qty,
        unitPrice: batch.salePrice,
      }]
    })
    form.resetFields(['batchId', 'qty'])
  }

  function removeItem(batchId: string) {
    setItems((prev) => prev.filter((i) => i.batchId !== batchId))
  }

  function handleSubmit() {
    const patientId = form.getFieldValue('patientId')
    const paymentMode = form.getFieldValue('paymentMode') ?? 'CASH'

    const payload: BillCreateRequest = {
      patientId: patientId || undefined,
      paymentMode,
      discount,
      items: items.map((i) => ({ batchId: i.batchId, quantity: i.quantity })),
    }

    createBill(payload, {
      onSuccess: (data) => {
        setCreatedBill(data)
        setItems([])
        setDiscount(0)
        form.resetFields()
      },
    })
  }

  const lineColumns: TableProps<BillLineItem>['columns'] = [
    { title: 'Medicine', dataIndex: 'medicineName' },
    { title: 'Batch', dataIndex: 'batchNumber' },
    { title: 'Qty', dataIndex: 'quantity', align: 'right' },
    { title: 'Unit Price', dataIndex: 'unitPrice', render: formatCurrency, align: 'right' },
    {
      title: 'Total', key: 'total',
      render: (_, r) => formatCurrency(r.quantity * r.unitPrice),
      align: 'right',
    },
    {
      title: '', key: 'del',
      render: (_, r) => (
        <Button danger size="small" icon={<DeleteOutlined />} onClick={() => removeItem(r.batchId)} />
      ),
    },
  ]

  if (createdBill) {
    return (
      <div>
        <PageHeader title="Bill Created" />
        <Alert
          type="success" showIcon
          message={`Bill ${createdBill.billNumber} created successfully`}
          description={`Net Amount: ${formatCurrency(createdBill.netAmount)} · Payment: ${createdBill.paymentMode}`}
          action={
            <Button icon={<PrinterOutlined />} onClick={() => window.print()}>Print</Button>
          }
          style={{ marginBottom: 16 }}
        />
        <Button onClick={() => setCreatedBill(null)}>Create Another Bill</Button>
      </div>
    )
  }

  return (
    <div>
      <PageHeader
        title="New Pharmacy Bill"
        breadcrumbs={[{ title: 'Dashboard', href: '/dashboard' }, { title: 'Pharmacy Bill' }]}
      />

      <Card title="Bill Details" style={{ marginBottom: 16 }}>
        <Form form={form} layout="vertical">
          <Form.Item name="patientId" label="Patient (optional for OTC sale)">
            <Select
              showSearch allowClear filterOption={false}
              onSearch={setPatientQuery} placeholder="Search patient…"
              options={patients?.content.map((p) => ({
                value: p.id,
                label: `${p.firstName} ${p.lastName} — ${p.mobile ?? ''}`,
              }))}
            />
          </Form.Item>
          <Form.Item name="paymentMode" label="Payment Mode" initialValue="CASH">
            <Select options={[
              { label: 'Cash',    value: 'CASH' },
              { label: 'Card',    value: 'CARD' },
              { label: 'UPI',     value: 'UPI' },
              { label: 'Credit',  value: 'CREDIT' },
            ]} />
          </Form.Item>
        </Form>
      </Card>

      <Card title="Add Medicines" style={{ marginBottom: 16 }}>
        <Form form={form} layout="inline">
          <Form.Item name="medicineId" label="Medicine">
            <Select
              showSearch filterOption={false}
              onSearch={setMedQuery}
              onChange={setSelectedMedId}
              placeholder="Search medicine…"
              style={{ width: 260 }}
              options={medicines?.content.map((m) => ({
                value: m.id,
                label: `${m.name} (${m.availableStock} ${m.unit})`,
              }))}
            />
          </Form.Item>
          <Form.Item name="batchId" label="Batch">
            <Select
              placeholder="Select batch"
              style={{ width: 200 }}
              disabled={!selectedMedId}
              options={stock?.batches
                .filter((b) => !b.expired && b.quantity > 0)
                .map((b) => ({
                  value: b.id,
                  label: `${b.batchNumber} · exp ${b.expiryDate} · ₹${b.salePrice} · ${b.quantity} left`,
                }))}
            />
          </Form.Item>
          <Form.Item name="qty" label="Qty" initialValue={1}>
            <InputNumber min={1} style={{ width: 80 }} />
          </Form.Item>
          <Form.Item label=" ">
            <Button icon={<PlusOutlined />} onClick={addItem} type="dashed">
              Add
            </Button>
          </Form.Item>
        </Form>
      </Card>

      {items.length > 0 && (
        <Card title="Bill Items">
          <Table
            rowKey="batchId"
            size="small"
            dataSource={items}
            columns={lineColumns}
            pagination={false}
            summary={() => (
              <Table.Summary>
                <Table.Summary.Row>
                  <Table.Summary.Cell index={0} colSpan={4} align="right">
                    <Typography.Text type="secondary">Subtotal</Typography.Text>
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={4} align="right">{formatCurrency(total)}</Table.Summary.Cell>
                  <Table.Summary.Cell index={5} />
                </Table.Summary.Row>
                <Table.Summary.Row>
                  <Table.Summary.Cell index={0} colSpan={4} align="right">
                    Discount ₹
                    <InputNumber
                      min={0} max={total} value={discount}
                      onChange={(v) => setDiscount(v ?? 0)}
                      size="small" style={{ width: 80, marginLeft: 8 }}
                    />
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={4} align="right">
                    − {formatCurrency(discount)}
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={5} />
                </Table.Summary.Row>
                <Table.Summary.Row>
                  <Table.Summary.Cell index={0} colSpan={4} align="right">
                    <strong>Net Amount</strong>
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={4} align="right">
                    <strong style={{ color: '#1677ff', fontSize: 16 }}>{formatCurrency(net)}</strong>
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={5} />
                </Table.Summary.Row>
              </Table.Summary>
            )}
          />

          <Divider />

          <div className="flex justify-end">
            <Button
              type="primary" size="large"
              loading={isPending}
              disabled={items.length === 0}
              onClick={handleSubmit}
            >
              Generate Bill
            </Button>
          </div>
        </Card>
      )}
    </div>
  )
}
