import { marked } from 'marked'
import DOMPurify from 'dompurify'
export function renderMarkdown(value: string) {
  return DOMPurify.sanitize(marked.parse(value, { async: false }), { FORBID_TAGS: ['style', 'iframe'], FORBID_ATTR: ['style'] })
}
