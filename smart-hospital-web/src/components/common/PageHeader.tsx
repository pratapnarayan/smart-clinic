import { Typography, Breadcrumb, type BreadcrumbProps } from 'antd'
import type { ReactNode } from 'react'

interface Props {
  title: string
  subtitle?: string
  breadcrumbs?: BreadcrumbProps['items']
  extra?: ReactNode
}

export function PageHeader({ title, subtitle, breadcrumbs, extra }: Props) {
  return (
    <div className="mb-6">
      {breadcrumbs && (
        <Breadcrumb items={breadcrumbs} className="mb-2" />
      )}
      <div className="flex items-start justify-between">
        <div>
          <Typography.Title level={4} style={{ margin: 0 }}>{title}</Typography.Title>
          {subtitle && (
            <Typography.Text type="secondary">{subtitle}</Typography.Text>
          )}
        </div>
        {extra && <div>{extra}</div>}
      </div>
    </div>
  )
}
