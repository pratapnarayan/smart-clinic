import { Skeleton, Card } from 'antd'
import { cn } from '@/utils/cn'

interface PageSkeletonProps {
  kpiCount?: number
  showCharts?: boolean
  chartCount?: number
  className?: string
}

export function PageSkeleton({
  kpiCount = 4,
  showCharts = true,
  chartCount = 2,
  className,
}: PageSkeletonProps) {
  return (
    <div className={cn('space-y-6 animate-fade-in', className)}>
      <div className="space-y-2">
        <Skeleton.Input active size="large" style={{ width: 256 }} />
        <Skeleton.Input active size="small" style={{ width: 384 }} />
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {Array.from({ length: kpiCount }).map((_, i) => (
          <Card key={i} className="medical-card" styles={{ body: { padding: '1.5rem' } }}>
            <Skeleton active paragraph={{ rows: 2 }} />
          </Card>
        ))}
      </div>

      {showCharts && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {Array.from({ length: chartCount }).map((_, i) => (
            <Card key={i} className="medical-card" styles={{ body: { padding: '24px' } }}>
              <Skeleton active paragraph={{ rows: 6 }} />
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
