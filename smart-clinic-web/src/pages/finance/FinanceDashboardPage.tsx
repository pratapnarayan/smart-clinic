import { Card, Table, Tag } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { ArrowUpOutlined, ArrowDownOutlined, BarChartOutlined, RiseOutlined, FallOutlined, DollarCircleOutlined } from '@ant-design/icons'
import { PageHeader } from '@/components/common'
import { KpiCard } from '@/components/analytics'
import { useFinanceDashboard } from '@/hooks/useFinance'

const SOURCE_COLOR: Record<string, string> = {
  OPD: 'blue', IPD: 'purple', PHARMACY: 'cyan',
  PATHOLOGY: 'orange', RADIOLOGY: 'geekblue', OTHER: 'default',
}

const fmt = (v: number) =>
  `₹${Number(v ?? 0).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`

export function FinanceDashboardPage() {
  const { data: dash, isLoading } = useFinanceDashboard()

  const incomeColumns: ColumnsType<{ source: string; amount: number }> = [
    {
      title: 'Source',
      dataIndex: 'source',
      render: (v: string) => <Tag color={SOURCE_COLOR[v] ?? 'default'}>{v}</Tag>,
    },
    {
      title: 'Amount',
      dataIndex: 'amount',
      align: 'right',
      render: fmt,
    },
  ]

  const expenseColumns: ColumnsType<{ category: string; amount: number }> = [
    { title: 'Category', dataIndex: 'category' },
    { title: 'Amount', dataIndex: 'amount', align: 'right', render: fmt },
  ]

  const todayNet   = (dash?.todayNet   ?? 0)
  const monthNet   = (dash?.monthNet   ?? 0)

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader title="Finance Overview" subtitle="Today and month-to-date summary" />

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
        <KpiCard title="Today — Income"          value={fmt(dash?.todayIncome ?? 0)}   icon={<ArrowUpOutlined />}       color="success" loading={isLoading} />
        <KpiCard title="Today — Expenses"        value={fmt(dash?.todayExpenses ?? 0)} icon={<ArrowDownOutlined />}     color="danger"  loading={isLoading} />
        <KpiCard title="Today — Net"             value={fmt(todayNet)}                  icon={<BarChartOutlined />}      color={todayNet >= 0 ? 'success' : 'danger'} loading={isLoading} />
        <KpiCard title="Last 30 Days — Income"   value={fmt(dash?.monthIncome ?? 0)}   icon={<RiseOutlined />}          color="primary" loading={isLoading} />
        <KpiCard title="Last 30 Days — Expenses" value={fmt(dash?.monthExpenses ?? 0)} icon={<FallOutlined />}          color="warning" loading={isLoading} />
        <KpiCard title="Last 30 Days — Net"      value={fmt(monthNet)}                  icon={<DollarCircleOutlined />}  color={monthNet >= 0 ? 'primary' : 'danger'} loading={isLoading} />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card title="Income by Source — Last 30 Days" loading={isLoading} className="medical-card">
          <Table
            rowKey="source"
            size="small"
            columns={incomeColumns}
            dataSource={dash?.monthIncomeBySource ?? []}
            pagination={false}
            locale={{ emptyText: 'No income recorded this month' }}
          />
        </Card>
        <Card title="Expenses by Category — Last 30 Days" loading={isLoading} className="medical-card">
          <Table
            rowKey="category"
            size="small"
            columns={expenseColumns}
            dataSource={dash?.monthExpenseByCategory ?? []}
            pagination={false}
            locale={{ emptyText: 'No expenses recorded this month' }}
          />
        </Card>
      </div>
    </div>
  )
}
