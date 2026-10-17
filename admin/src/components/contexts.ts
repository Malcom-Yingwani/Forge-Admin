import { createContext, useContext } from 'react'
export type ToastKind = 'success' | 'error' | 'info'
export type ConfirmOptions = { title: string; message: string; danger?: boolean }
export const ToastContext = createContext<Record<ToastKind, (message: string) => void> | null>(null)
export const ConfirmContext = createContext<((options: ConfirmOptions) => Promise<boolean>) | null>(null)
export function useToast() { const value = useContext(ToastContext); if (!value) throw new Error('ToastProvider required'); return value }
export function useConfirm() { const value = useContext(ConfirmContext); if (!value) throw new Error('ConfirmProvider required'); return value }
