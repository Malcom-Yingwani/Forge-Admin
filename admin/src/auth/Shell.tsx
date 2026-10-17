import { useState } from 'react'
import { Link, NavLink, Outlet } from 'react-router-dom'
import { useAuth } from './context'
import { useToast } from '../components/contexts'
import { Modal } from '../components/UI'
const content = [['/sermons','Sermons'],['/series','Series'],['/preachers','Preachers'],['/events','Events'],['/pages','Pages'],['/faqs','FAQs'],['/people','People'],['/ministries','Ministries'],['/growth-groups','Growth Groups'],['/blog','Blog'],['/documents','Documents']]
export function Shell() {
  const { user, logout, hasRole } = useAuth(), toast = useToast(), [open, setOpen] = useState(false), [report, setReport] = useState(false), [busy, setBusy] = useState(false)
  const signOut = async () => { setBusy(true); try { await logout() } catch { toast.error('Could not sign out. Please try again.'); } finally { setBusy(false) } }
  const link = ([path, title]: string[]) => <NavLink key={path} to={path} onClick={() => setOpen(false)}>{title}</NavLink>
  return <><a className="skip-link" href="#main-content">Skip to content</a><header className="navbar"><Link className="brand" to="/"><img src="/favicon.svg" width="36" height="36" alt="" />HERITAGE FORGE</Link>
    <button type="button" className="hamburger" aria-label="Toggle navigation" aria-controls="staff-navigation" aria-expanded={open} onClick={() => setOpen(!open)}><span /><span /><span /></button>
    <div className="staff-actions"><Link to="/account">{user?.name}</Link><button className="nav-report-issue-btn" onClick={() => setReport(true)}>Report issue</button><button className="btn-logout" onClick={signOut} disabled={busy}>Log out</button></div></header>
    <div className="admin-layout"><aside id="staff-navigation" className={`sidebar ${open ? 'sidebar--open' : ''}`}><nav aria-label="Staff navigation">{link(['/','Dashboard'])}<h2>Content</h2>{content.map(link)}<h2>Church</h2>{[['/giving','Giving'],['/inbox','Inbox'],['/settings','Settings']].map(link)}
      {hasRole('ADMIN') && <><h2>Admin</h2>{[['/sync','Sync'],['/users','Users']].map(link)}</>}</nav></aside>
      <main id="main-content" className="admin-content"><Outlet /></main></div>
    {report && <Modal title="Report an issue" onClose={() => setReport(false)}><p>The issue reporting form will be available with the inbox tools.</p><button onClick={() => setReport(false)}>Close</button></Modal>}</>
}
