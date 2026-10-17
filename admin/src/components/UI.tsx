import { useEffect, useId, useRef, useState } from 'react'
import type { ReactNode } from 'react'
import { createPortal } from 'react-dom'

export function Modal({ title, children, onClose }: { title: string; children: ReactNode; onClose: () => void }) {
  const ref = useRef<HTMLDivElement>(null)
  const titleId = useId()
  const close = useRef(onClose)
  useEffect(() => { close.current = onClose }, [onClose])
  useEffect(() => {
    const trigger = document.activeElement as HTMLElement | null
    const previousOverflow = document.body.style.overflow
    document.body.style.overflow = 'hidden'
    const dialog = ref.current!
    const focusable = () => [...dialog.querySelectorAll<HTMLElement>('button:not(:disabled),input:not(:disabled),select:not(:disabled),textarea:not(:disabled),a[href],[tabindex="0"]')]
    ;(focusable()[0] ?? dialog).focus()
    const keyboard = (event: KeyboardEvent) => {
      if (event.key === 'Escape') { event.preventDefault(); event.stopPropagation(); close.current() }
      if (event.key === 'Tab') {
        const elements = focusable(), first = elements[0], last = elements.at(-1)
        if (!first) { event.preventDefault(); dialog.focus(); return }
        if (event.shiftKey && (document.activeElement === first || document.activeElement === dialog)) { event.preventDefault(); last?.focus() }
        else if (!event.shiftKey && document.activeElement === last) { event.preventDefault(); first.focus() }
      }
    }
    const keepFocus = (event: FocusEvent) => { if (!dialog.contains(event.target as Node)) (focusable()[0] ?? dialog).focus() }
    dialog.addEventListener('keydown', keyboard)
    document.addEventListener('focusin', keepFocus)
    return () => {
      dialog.removeEventListener('keydown', keyboard); document.removeEventListener('focusin', keepFocus)
      document.body.style.overflow = previousOverflow; trigger?.focus()
    }
  }, [])
  return createPortal(<div className="modal-backdrop" onMouseDown={event => { if (event.target === event.currentTarget) onClose() }}>
    <div ref={ref} className="modal" role="dialog" aria-modal="true" aria-labelledby={titleId} tabIndex={-1}>
      <header className="modal-header"><h2 id={titleId}>{title}</h2><button type="button" aria-label="Close dialog" onClick={onClose}>×</button></header>
      <div className="modal-body">{children}</div>
    </div></div>, document.body)
}

export function Lightbox({ src, alt, onClose }: { src: string; alt: string; onClose: () => void }) {
  return <Modal title={alt || 'Image preview'} onClose={onClose}><img className="lightbox-image" src={src} alt={alt} /></Modal>
}
export function ImagePicker({ value, onChange }: { value?: string; onChange: (value: string) => void }) {
  const [preview, setPreview] = useState(false)
  return <div><FormField label="Image asset ID" value={value ?? ''} onChange={onChange} hint="Choose an uploaded media asset. Upload support will be added with media management." />
    {value?.startsWith('https://') && <button type="button" onClick={() => setPreview(true)}>Preview image</button>}
    {preview && value && <Lightbox src={value} alt="Selected image" onClose={() => setPreview(false)} />}</div>
}
export type FieldError = { field: string; message: string }
export function FormField({ label, name, value, onChange, type = 'text', errors = [], hint, required, options, disabled, minLength, maxLength }:
  { label: string; name?: string; value: string | boolean; onChange: (value: string) => void; type?: string; errors?: FieldError[]; hint?: string;
    required?: boolean; options?: { value: string; label: string }[]; disabled?: boolean; minLength?: number; maxLength?: number }) {
  const id = useId(), messages = errors.filter(error => error.field === name)
  const described = [hint ? `${id}-hint` : '', messages.length ? `${id}-error` : ''].filter(Boolean).join(' ') || undefined
  const common = { id, name, required, disabled, 'aria-invalid': messages.length > 0, 'aria-describedby': described, className: 'form-control' }
  return <div className="form-field"><label className="form-label" htmlFor={id}>{label}{required && <span aria-hidden="true"> *</span>}</label>
    {type === 'textarea' ? <textarea {...common} minLength={minLength} maxLength={maxLength} value={String(value)} onChange={e => onChange(e.target.value)} /> :
      type === 'select' ? <select {...common} value={String(value)} onChange={e => onChange(e.target.value)}>{options?.map(option => <option key={option.value} value={option.value}>{option.label}</option>)}</select> :
      type === 'checkbox' ? <input {...common} type="checkbox" checked={Boolean(value)} onChange={e => onChange(String(e.target.checked))} /> :
      <input {...common} type={type} minLength={minLength} maxLength={maxLength} value={String(value)} onChange={e => onChange(e.target.value)} />}
    {hint && <small id={`${id}-hint`}>{hint}</small>}
    {!!messages.length && <div id={`${id}-error`} role="alert">{messages.map(error => <p key={error.message}>{error.message}</p>)}</div>}
  </div>
}
export function AdminCard({ title, children }: { title: string; children: ReactNode }) { return <section className="admin-card"><h2>{title}</h2>{children}</section> }
export function EmptyState({ text, children }: { text: string; children?: ReactNode }) { return <div className="empty-state"><p>{text}</p>{children}</div> }
export function PublishToggle({ published, onChange, disabled }: { published: boolean; onChange: (value: boolean) => void; disabled?: boolean }) {
  return <button type="button" className="publish-toggle" aria-pressed={published} disabled={disabled} onClick={() => onChange(!published)}>{published ? 'Published' : 'Hidden'}</button>
}
export type Column<T> = { key: string; label: string; sortable?: boolean; render?: (row: T) => ReactNode }
export function DataTable<T extends { id: string }>({ columns, rows, page, totalPages, onPageChange, sort, onSortChange, loading, empty }:
  { columns: Column<T>[]; rows: T[]; page: number; totalPages: number; onPageChange: (page: number) => void;
    sort?: string; onSortChange?: (sort: string) => void; loading?: boolean; empty?: ReactNode }) {
  if (loading) return <p role="status">Loading…</p>
  if (!rows.length) return empty ?? <EmptyState text="No results found." />
  return <><div className="table-scroll"><table className="table table-hover"><thead><tr>{columns.map(column =>
    <th key={column.key} aria-sort={column.sortable ? (sort?.split(',')[0] === column.key ? (sort.endsWith(',desc') ? 'descending' : 'ascending') : 'none') : undefined}>
      {column.sortable ? <button type="button" onClick={() => onSortChange?.(`${column.key},${sort === `${column.key},asc` ? 'desc' : 'asc'}`)}>{column.label}</button> : column.label}</th>)}</tr></thead>
    <tbody>{rows.map(row => <tr key={row.id}>{columns.map(column => <td key={column.key}>{column.render ? column.render(row) : String((row as Record<string, unknown>)[column.key] ?? '')}</td>)}</tr>)}</tbody></table></div>
    <nav className="pagination" aria-label="Table pagination"><button type="button" disabled={page <= 0} onClick={() => onPageChange(page - 1)}>Previous</button>
      <span>Page {page + 1} of {Math.max(1, totalPages)}</span><button type="button" disabled={page + 1 >= totalPages} onClick={() => onPageChange(page + 1)}>Next</button></nav></>
}
