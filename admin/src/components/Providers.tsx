import { useEffect, useMemo, useRef, useState } from 'react'
import type { ReactNode } from 'react'
import { ConfirmContext, ToastContext } from './contexts'
import type { ConfirmOptions, ToastKind } from './contexts'
import { Modal } from './UI'

function Toast({ kind, message, dismiss }: { kind: ToastKind; message: string; dismiss: () => void }) {
  const [leaving, setLeaving] = useState(false)
  useEffect(() => {
    const leave = setTimeout(() => setLeaving(true), 4700)
    const remove = setTimeout(dismiss, 5000)
    return () => { clearTimeout(leave); clearTimeout(remove) }
  }, [dismiss])
  return <div className={`app-toast app-toast--${kind} app-toast--${leaving ? 'leaving' : 'visible'}`}><span>{message}</span><button type="button" aria-label="Dismiss notification" onClick={dismiss}>×</button></div>
}
export function ToastProvider({ children }: { children: ReactNode }) {
  const [stack, setStack] = useState<{ id: number; kind: ToastKind; message: string; dismiss: () => void }[]>([])
  const next = useRef(0)
  const api = useMemo(() => {
    const add = (kind: ToastKind) => (message: string) => {
      const id = ++next.current
      const dismiss = () => setStack(items => items.filter(item => item.id !== id))
      setStack(items => [...items, { id, kind, message, dismiss }])
    }
    return { success: add('success'), error: add('error'), info: add('info') }
  }, [])
  return <ToastContext.Provider value={api}>{children}<div className="app-toast-stack" aria-live="polite" aria-relevant="additions">{stack.map(item => <Toast key={item.id} {...item} />)}</div></ToastContext.Provider>
}
export function ConfirmDialog({ title, message, danger, onAnswer }: ConfirmOptions & { onAnswer: (answer: boolean) => void }) {
  return <Modal title={title} onClose={() => onAnswer(false)}><p>{message}</p><div className="actions"><button type="button" onClick={() => onAnswer(false)}>Cancel</button>
    <button type="button" className={danger ? 'btn-danger' : 'btn-primary'} onClick={() => onAnswer(true)}>Confirm</button></div></Modal>
}
export function ConfirmProvider({ children }: { children: ReactNode }) {
  const [pending, setPending] = useState<ConfirmOptions | null>(null)
  const resolver = useRef<((answer: boolean) => void) | null>(null)
  useEffect(() => () => resolver.current?.(false), [])
  const confirm = useMemo(() => (options: ConfirmOptions) => {
    resolver.current?.(false)
    return new Promise<boolean>(resolve => { resolver.current = resolve; setPending(options) })
  }, [])
  const answer = (value: boolean) => { resolver.current?.(value); resolver.current = null; setPending(null) }
  return <ConfirmContext.Provider value={confirm}>{children}{pending && <ConfirmDialog {...pending} onAnswer={answer} />}</ConfirmContext.Provider>
}
