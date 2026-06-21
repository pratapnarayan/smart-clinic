import { Empty } from 'antd'
import { BarChartOutlined } from '@ant-design/icons'
import { cn } from '@/utils/cn'

interface EmptyChartProps {
  height?: number
  title?: string
  description?: string
  className?: string
}

export function EmptyChart({
  height = 260,
  title = 'No Data Available',
  description = 'There is no data to display for the selected period.',
  className,
}: EmptyChartProps) {
  return (
    <div
      className={cn(
        'flex flex-col items-center justify-center rounded-lg border border-dashed border-neutral-200',
        className
      )}
      style={{ height, background: 'var(--color-neutral-50)' }}
    >
      <Empty
        image={<BarChartOutlined style={{ fontSize: 40, color: 'var(--color-neutral-400)' }} />}
        description={
          <div className="text-center">
            <p className="text-sm font-medium" style={{ color: 'var(--text-secondary)' }}>{title}</p>
            <p className="text-xs mt-1" style={{ color: 'var(--text-tertiary)' }}>{description}</p>
          </div>
        }
      />
    </div>
  )
}
