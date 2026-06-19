import { Card, Skeleton, Typography } from 'antd'
import { ArrowUpOutlined, ArrowDownOutlined, MinusOutlined } from '@ant-design/icons'
import { cn } from '@/utils/cn'

const { Text } = Typography

interface KpiCardProps {
  title: string
  value: string | number
  trend?: number | null
  subtitle?: string
  loading?: boolean
  icon?: React.ReactNode
  color?: 'primary' | 'success' | 'warning' | 'danger' | 'purple' | 'cyan'
  className?: string
  prefix?: string
}

const colorMap = {
  primary: { bg: 'bg-primary-50', text: 'text-primary-600', bar: 'bg-primary-500' },
  success: { bg: 'bg-success-50', text: 'text-success-600', bar: 'bg-success-500' },
  warning: { bg: 'bg-warning-50', text: 'text-warning-600', bar: 'bg-warning-500' },
  danger:  { bg: 'bg-danger-50',  text: 'text-danger-600',  bar: 'bg-danger-500'  },
  purple:  { bg: 'bg-purple-50',  text: 'text-purple-600',  bar: 'bg-purple-500'  },
  cyan:    { bg: 'bg-cyan-50',    text: 'text-cyan-600',    bar: 'bg-cyan-500'    },
}

export function KpiCard({
  title,
  value,
  trend,
  subtitle,
  loading = false,
  icon,
  color = 'primary',
  className,
  prefix = '',
}: KpiCardProps) {
  const colors = colorMap[color]

  if (loading) {
    return (
      <Card
        className={cn('medical-card overflow-hidden', className)}
        styles={{ body: { padding: '1.5rem' } }}
      >
        <Skeleton active paragraph={{ rows: 2 }} />
      </Card>
    )
  }

  const isPositive = trend != null && trend > 0
  const isNegative = trend != null && trend < 0
  const isNeutral  = trend != null && trend === 0

  return (
    <Card
      className={cn(
        'medical-card overflow-hidden relative group cursor-default',
        'hover:shadow-medical-md transition-all duration-300',
        className
      )}
      styles={{ body: { padding: '1.5rem' } }}
    >
      {/* Top accent bar on hover */}
      <div
        className={cn(
          'absolute top-0 left-0 right-0 h-1 rounded-t-lg opacity-0 group-hover:opacity-100 transition-opacity',
          colors.bar
        )}
      />

      <div className="flex items-start justify-between mb-3">
        {/* Icon */}
        <div className={cn('flex items-center justify-center w-10 h-10 rounded-xl', colors.bg)}>
          {icon ? (
            <span className={cn('text-lg', colors.text)}>{icon}</span>
          ) : (
            <div className={cn('w-5 h-5 rounded-full', colors.bar)} />
          )}
        </div>

        {/* Trend badge */}
        {trend != null && (
          <div
            className={cn(
              'flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium',
              isPositive && 'bg-success-50 text-success-600',
              isNegative && 'bg-danger-50 text-danger-600',
              isNeutral  && 'bg-neutral-100 text-neutral-500'
            )}
          >
            {isPositive && <ArrowUpOutlined />}
            {isNegative && <ArrowDownOutlined />}
            {isNeutral  && <MinusOutlined />}
            <span>{Math.abs(trend).toFixed(1)}%</span>
          </div>
        )}
      </div>

      <div className="space-y-1">
        <Text className="text-2xl font-bold block tracking-tight" style={{ color: 'var(--text-primary)' }}>
          {prefix}{value}
        </Text>
        <Text className="text-sm block" style={{ color: 'var(--text-muted)' }}>
          {title}
        </Text>
        {subtitle && (
          <Text className="text-xs block mt-1" style={{ color: 'var(--text-tertiary)' }}>
            {subtitle}
          </Text>
        )}
      </div>
    </Card>
  )
}
