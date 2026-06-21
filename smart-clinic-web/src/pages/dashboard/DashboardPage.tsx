import { Card, Alert, Table, Tag } from 'antd'
import {
  UserOutlined, MedicineBoxOutlined, ShopOutlined, WarningOutlined,
} from '@ant-design/icons'
import { useAuthStore } from '@/store/authStore'
import { useLowStockMedicines, useExpiringBatches } from '@/hooks/usePharmacy'
import { useVisitsByDate } from '@/hooks/useOpdVisits'
import { usePatients } from '@/hooks/usePatients'
import { PageHeader } from '@/components/common/PageHeader'
import { KpiCard } from '@/components/analytics'
import { formatDate, formatCurrency } from '@/utils'
import type { OpdVisit, VisitStatus } from '@/types'
import type { ColumnsType } from 'antd/es/table'
import dayjs from 'dayjs'

const VISIT_STATUS_COLOR: Record<VisitStatus, string> = {
  REGISTERED:  'default',
  IN_PROGRESS: 'processing',
  COMPLETED:   'success',
  CANCELLED:   'error',
}

const recentColumns: ColumnsType<OpdVisit> = [
  { title: 'Visit No.',  dataIndex: 'visitNumber',  width: 130 },
  { title: 'Patient',    dataIndex: 'patientName' },
  { title: 'Doctor',     dataIndex: 'doctorName',   render: (v?: string) => v ?? '—' },
  { title: 'Department', dataIndex: 'department',   render: (v?: string) => v ?? '—' },
  { title: 'Fee',        dataIndex: 'consultationFee', render: formatCurrency },
  {
    title: 'Status', dataIndex: 'visitStatus',
    render: (v: VisitStatus) => <Tag color={VISIT_STATUS_COLOR[v]}>{v.replace('_', ' ')}</Tag>,
  },
]

export function DashboardPage() {
  const { user, hasPermission } = useAuthStore()
  const today = dayjs().format('YYYY-MM-DD')
  // Roles without PHARMACY.VIEW (e.g. Doctor) get a 403 from these endpoints.
  // Previously the queries fired anyway, failed silently, and rendered as a
  // misleading "0 — all clear" instead of just not showing pharmacy data to
  // a role that has no access to it.
  const canViewPharmacy = hasPermission('PHARMACY.VIEW')

  const { data: todayVisits,  isLoading: loadingVisits }  = useVisitsByDate(today)
  const { data: patientsPage, isLoading: loadingPatients } = usePatients(undefined, 0, 1)
  const { data: lowStock,     isLoading: loadingLow }     = useLowStockMedicines(canViewPharmacy)
  const { data: expiring,     isLoading: loadingExpiry }  = useExpiringBatches(30, canViewPharmacy)

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader
        title={`Good ${getGreeting()}${user?.firstName ? `, ${user.firstName}` : ''}!`}
        subtitle={`Today is ${dayjs().format('DD MMM YYYY')}${user?.tenantId ? ` · ${user.tenantId}` : ''}`}
      />

      {/* ── KPI cards ────────────────────────────────────────────────────── */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard
          title="Today's OPD Visits"
          value={(todayVisits?.total ?? 0).toString()}
          icon={<MedicineBoxOutlined />}
          color="primary"
          loading={loadingVisits}
        />
        <KpiCard
          title="Registered Patients"
          value={(patientsPage?.total ?? 0).toString()}
          icon={<UserOutlined />}
          color="success"
          loading={loadingPatients}
        />
        {canViewPharmacy && (
          <KpiCard
            title="Low Stock Medicines"
            value={(lowStock?.length ?? 0).toString()}
            icon={<ShopOutlined />}
            color={lowStock?.length ? 'danger' : 'success'}
            loading={loadingLow}
          />
        )}
        {canViewPharmacy && (
          <KpiCard
            title="Batches Expiring (30d)"
            value={(expiring?.length ?? 0).toString()}
            icon={<WarningOutlined />}
            color={expiring?.length ? 'warning' : 'success'}
            loading={loadingExpiry}
          />
        )}
      </div>

      {/* ── Alerts ───────────────────────────────────────────────────────── */}
      {canViewPharmacy && (
        <div className="space-y-2">
          {lowStock && lowStock.length > 0 && (
            <Alert
              type="error"
              showIcon
              message={`${lowStock.length} medicine(s) at or below reorder level`}
              description={lowStock.slice(0, 5).map((m) => m.name).join(', ')}
            />
          )}
          {expiring && expiring.length > 0 && (
            <Alert
              type="warning"
              showIcon
              message={`${expiring.length} batch(es) expiring within 30 days`}
              description={expiring.slice(0, 5)
                .map((b) => `${b.medicineName} — ${b.batchNumber} (expires ${formatDate(b.expiryDate)})`)
                .join(' · ')}
            />
          )}
          {!loadingLow && !loadingExpiry && !lowStock?.length && !expiring?.length && (
            <Alert type="success" showIcon message="All systems normal — no stock or expiry alerts." />
          )}
        </div>
      )}

      {/* ── Today's OPD Visits table ──────────────────────────────────────── */}
      <Card
        className="medical-card"
        title={`Today's OPD Visits — ${formatDate(today)}`}
        extra={<a href="/opd">View all</a>}
        styles={{ body: { padding: 0 } }}
      >
        <Table
          rowKey="id"
          size="small"
          loading={loadingVisits}
          dataSource={todayVisits?.content ?? []}
          columns={recentColumns}
          pagination={false}
          locale={{ emptyText: 'No OPD visits registered today' }}
          className="[&_.ant-table-thead>tr>th]:bg-neutral-50 [&_.ant-table-thead>tr>th]:font-semibold"
        />
      </Card>
    </div>
  )
}

function getGreeting(): string {
  const hour = new Date().getHours()
  if (hour < 12) return 'morning'
  if (hour < 17) return 'afternoon'
  return 'evening'
}
