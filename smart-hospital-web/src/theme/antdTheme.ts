// src/theme/antdTheme.ts
import type { ThemeConfig } from 'antd'
import { designTokens } from './index'

export const antdTheme: ThemeConfig = {
  token: {
    colorPrimary: designTokens.colors.primary[500],
    colorSuccess: designTokens.colors.success[500],
    colorWarning: designTokens.colors.warning[500],
    colorError: designTokens.colors.danger[500],
    colorInfo: designTokens.colors.primary[500],
    borderRadius: 8,
    borderRadiusSM: 6,
    borderRadiusLG: 12,
    fontFamily: designTokens.typography.fontFamily.sans,
    fontSize: 14,
    colorText: designTokens.colors.text.primary,
    colorTextSecondary: designTokens.colors.text.secondary,
    colorBgContainer: designTokens.colors.background.tertiary,
    colorBgElevated: designTokens.colors.background.tertiary,
    colorBorder: designTokens.colors.neutral[200],
    colorBorderSecondary: designTokens.colors.neutral[100],
    boxShadow: designTokens.shadows.DEFAULT,
    boxShadowSecondary: designTokens.shadows.md,
    controlHeight: 36,
    controlHeightSM: 28,
    controlHeightLG: 44,
  },
  components: {
    Layout: {
      headerBg: designTokens.colors.background.header,
      headerHeight: 64,
      headerPadding: '0 24px',
      siderBg: designTokens.colors.sidebar.bg,
      triggerBg: designTokens.colors.sidebar.hover,
      triggerColor: designTokens.colors.sidebar.text,
    },
    Menu: {
      darkItemBg: designTokens.colors.sidebar.bg,
      darkItemSelectedBg: designTokens.colors.sidebar.active,
      darkItemHoverBg: designTokens.colors.sidebar.hover,
      darkItemColor: designTokens.colors.sidebar.text,
      darkItemSelectedColor: designTokens.colors.sidebar.textActive,
      darkSubMenuItemBg: designTokens.colors.sidebar.bg,
      itemHeight: 44,
      iconSize: 16,
      iconMarginInline: 12,
    },
    Button: {
      borderRadius: 8,
      borderRadiusSM: 6,
      borderRadiusLG: 10,
      controlHeight: 36,
      controlHeightSM: 28,
      controlHeightLG: 44,
      paddingInline: 16,
      paddingInlineSM: 12,
      paddingInlineLG: 24,
    },
    Card: {
      borderRadius: 12,
      borderRadiusLG: 16,
      paddingLG: 24,
    },
    Table: {
      borderRadius: 8,
      headerBg: designTokens.colors.neutral[50],
      headerColor: designTokens.colors.text.primary,
      rowHoverBg: designTokens.colors.primary[50],
      padding: 16,
      paddingSM: 12,
      paddingLG: 20,
    },
    Input: {
      borderRadius: 8,
      paddingInline: 12,
      paddingBlock: 8,
      controlHeight: 36,
    },
    Select: {
      borderRadius: 8,
      controlHeight: 36,
    },
    DatePicker: {
      borderRadius: 8,
      controlHeight: 36,
    },
    Modal: {
      borderRadius: 16,
    },
    Tag: {
      borderRadius: 6,
      defaultBg: designTokens.colors.neutral[100],
    },
    Tooltip: {
      borderRadius: 8,
    },
    Dropdown: {
      borderRadius: 8,
    },
    Pagination: {
      borderRadius: 8,
    },
    Tabs: {
      borderRadius: 8,
    },
    Alert: {
      borderRadius: 8,
    },
    Notification: {
      borderRadius: 12,
    },
    Message: {
      borderRadius: 8,
    },
    Steps: {
      iconSize: 32,
    },
  },
}
