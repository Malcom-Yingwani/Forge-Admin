import { useId, useState } from 'react'
import { renderMarkdown } from './markdown'
export function MarkdownEditor({ label, value, onChange }: { label: string; value: string; onChange: (value: string) => void }) {
  const id = useId(), [mode, setMode] = useState('split')
  return <div className="markdown-editor"><label className="form-label" htmlFor={id}>{label}</label>
    <label>Editor view <select value={mode} onChange={e => setMode(e.target.value)}><option value="split">Side by side</option><option value="edit">Write</option><option value="preview">Preview</option></select></label>
    <div className={mode === 'split' ? 'markdown-split' : ''}>
      {mode !== 'preview' && <textarea className="form-control" id={id} value={value} rows={12} onChange={e => onChange(e.target.value)} />}
      {mode !== 'edit' && <section className="markdown-preview" aria-label={`${label} preview`} dangerouslySetInnerHTML={{ __html: renderMarkdown(value) }} />}
    </div></div>
}
