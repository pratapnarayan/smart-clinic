import { create } from 'zustand'

interface UiState {
  sidebarCollapsed: boolean
  activeModule: string
  pageTitle: string

  toggleSidebar: () => void
  setSidebarCollapsed: (v: boolean) => void
  setActiveModule: (module: string) => void
  setPageTitle: (title: string) => void
}

export const useUiStore = create<UiState>()((set) => ({
  sidebarCollapsed: false,
  activeModule: 'dashboard',
  pageTitle: 'Dashboard',

  toggleSidebar: () =>
    set((s) => ({ sidebarCollapsed: !s.sidebarCollapsed })),

  setSidebarCollapsed: (v) => set({ sidebarCollapsed: v }),
  setActiveModule: (activeModule) => set({ activeModule }),
  setPageTitle: (pageTitle) => set({ pageTitle }),
}))
