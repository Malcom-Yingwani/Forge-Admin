// @vitest-environment jsdom
import { afterEach, describe, expect, it, vi } from 'vitest'
import { act, cleanup, fireEvent, render, screen } from '@testing-library/react'
import '@testing-library/jest-dom/vitest'
import axe from 'axe-core'
import { Modal, FormField } from './UI'
import { ToastProvider, ConfirmProvider } from './Providers'
import { useConfirm, useToast } from './contexts'
import { renderMarkdown } from './markdown'
import ComponentsDemo from './ComponentsDemo'
afterEach(() => { cleanup(); vi.useRealTimers() })
function Actions() {
  const toast = useToast(), confirm = useConfirm()
  return <><button onClick={() => toast.success('Saved')}>Notify</button><button onClick={async () => { const answer = await confirm({ title: 'Delete item?', message: 'Please confirm' }); toast.info(String(answer)) }}>Delete</button></>
}
describe('shared UI', () => {
  it('automatically dismisses notifications after five seconds', () => {
    vi.useFakeTimers(); render(<ToastProvider><ConfirmProvider><Actions /></ConfirmProvider></ToastProvider>)
    fireEvent.click(screen.getByText('Notify')); expect(screen.getByText('Saved')).toBeInTheDocument()
    act(() => vi.advanceTimersByTime(5000)); expect(screen.queryByText('Saved')).not.toBeInTheDocument()
  })
  it('traps focus, closes on Escape, and restores trigger focus', () => {
    const close = vi.fn(), trigger = document.createElement('button'); document.body.append(trigger); trigger.focus()
    const { unmount } = render(<Modal title="Edit" onClose={close}><input aria-label="Title" /><button>Save</button></Modal>)
    const first = screen.getByLabelText('Close dialog'), last = screen.getByText('Save')
    expect(first).toHaveFocus(); last.focus(); fireEvent.keyDown(last, { key: 'Tab' }); expect(first).toHaveFocus()
    fireEvent.keyDown(first, { key: 'Tab', shiftKey: true }); expect(last).toHaveFocus()
    fireEvent.keyDown(last, { key: 'Escape' }); expect(close).toHaveBeenCalledOnce()
    unmount(); expect(trigger).toHaveFocus(); trigger.remove()
  })
  it.each([['Confirm', 'true'], ['Cancel', 'false']])('resolves confirmation with %s', async (button, result) => {
    render(<ToastProvider><ConfirmProvider><Actions /></ConfirmProvider></ToastProvider>)
    fireEvent.click(screen.getByText('Delete')); fireEvent.click(screen.getByText(button)); expect(await screen.findByText(result)).toBeInTheDocument()
  })
  it('connects API field errors and passes accessibility rules', async () => {
    const { container } = render(<FormField label="Title" name="title" value="" onChange={() => {}} errors={[{ field: 'title', message: 'Title is required' }]} />)
    expect(screen.getByLabelText('Title')).toHaveAttribute('aria-invalid', 'true')
    expect(screen.getByLabelText('Title')).toHaveAccessibleDescription('Title is required')
    const result = await axe.run(container, { rules: { 'color-contrast': { enabled: false } } }); expect(result.violations).toEqual([])
  })
  it('renders the component gallery without accessibility violations', async () => {
    const { container } = render(<main><ToastProvider><ConfirmProvider><ComponentsDemo /></ConfirmProvider></ToastProvider></main>)
    const result = await axe.run(container, { rules: { 'color-contrast': { enabled: false } } })
    expect(result.violations).toEqual([])
  })
  it('sanitizes scripts, handlers, and unsafe links from markdown', () => {
    const html = renderMarkdown('<script>alert(1)</script><img src=x onerror=alert(1)>[link](javascript:alert(1))')
    expect(html).not.toMatch(/<script|onerror|href="javascript:/)
  })
})
