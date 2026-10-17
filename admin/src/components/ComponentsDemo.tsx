import { useEffect, useState } from 'react'
import { AdminCard, DataTable, EmptyState, FormField, ImagePicker, Lightbox, PublishToggle } from './UI'
import { MarkdownEditor } from './MarkdownEditor'
import { useConfirm, useToast } from './contexts'
export default function ComponentsDemo() {
  const [text, setText] = useState('## Welcome\nWrite church content here.'), [published, setPublished] = useState(false), [image, setImage] = useState(''), [lightbox, setLightbox] = useState(false)
  const toast = useToast(), confirm = useConfirm()
  useEffect(() => {
    if (import.meta.env.DEV && import.meta.env.MODE !== "test") void Promise.all([import('@axe-core/react'), import('react'), import('react-dom')]).then(([axe, React, ReactDOM]) => axe.default(React, ReactDOM, 1000))
  }, [])
  return <><h1>Shared components</h1><AdminCard title="Forms"><FormField label="Example title" name="title" value="" onChange={() => {}} errors={[{ field: 'title', message: 'A title is required' }]} />
    <MarkdownEditor label="Body" value={text} onChange={setText} /><ImagePicker value={image} onChange={setImage} /><PublishToggle published={published} onChange={setPublished} /></AdminCard>
    <div className="actions"><button onClick={() => toast.success('Saved successfully')}>Show toast</button>
      <button onClick={async () => { const accepted = await confirm({ title: 'Delete example?', message: 'This demonstrates confirmation.', danger: true }); toast.info(accepted ? 'Confirmed' : 'Cancelled') }}>Confirm example</button>
      <button onClick={() => setLightbox(true)}>Open lightbox</button></div>
    {lightbox && <Lightbox src="/favicon.svg" alt="Heritage tree" onClose={() => setLightbox(false)} />}
    <DataTable columns={[{ key: 'title', label: 'Title', sortable: true }]} rows={[{ id: 'example', title: 'Example sermon' }]} page={0} totalPages={1} onPageChange={() => {}} sort="title,asc" onSortChange={() => {}} />
    <EmptyState text="No sermons yet." /></>
}
