import { useState } from 'react'
import type { FormEvent, ReactNode } from 'react'
import { Link, Navigate, useNavigate, useSearchParams } from 'react-router-dom'
import { post, put, get } from '../lib/api'
import { useAuth, safeNext } from './context'
import { FormField } from '../components/UI'
import type { FieldError } from '../components/UI'
import { useToast } from '../components/contexts'
import { refreshSession } from '../lib/api'
function Card({ title, children }: { title: string; children: ReactNode }) {
  return <main className="login-page"><section className="login-card"><header className="login-header"><img src="/favicon.svg" alt="Heritage Baptist Church" width="56" height="56" /><h1>HERITAGE FORGE</h1><hr className="login-divider" /><blockquote>Your word is a lamp to my feet and a light to my path.<cite>Psalm 119:105</cite></blockquote></header>
    <div className="login-body"><h2>{title}</h2>{children}</div></section></main>
}
function message(error: unknown) {
  const typed = error as { status?: number; message?: string }
  return typed.status === 429 ? 'Too many attempts. Please wait fifteen minutes and try again.' : typed.status === 401 ? 'The email, password, or reset link is invalid.' : typed.message ?? 'Could not complete the request. Please try again.'
}
export function LoginPage() {
  const { user, loading, login } = useAuth(), [params] = useSearchParams(), navigate = useNavigate()
  const [email, setEmail] = useState(''), [password, setPassword] = useState(''), [visible, setVisible] = useState(false), [busy, setBusy] = useState(false), [error, setError] = useState('')
  const next = safeNext(params.get('next'))
  if (loading) return <Card title="Loading"><p role="status">Restoring your session...</p></Card>
  if (user) return <Navigate to={next} replace />
  const submit = async (event: FormEvent) => {
    event.preventDefault(); setBusy(true); setError('')
    try { await login(email, password); navigate(next, { replace: true }) } catch (error) { setError(message(error)) } finally { setBusy(false) }
  }
  return <Card title="Staff sign in"><form onSubmit={submit}>{error && <p role="alert" className="alert-danger">{error}</p>}
    <FormField label="Email" type="email" value={email} onChange={setEmail} required />
    <FormField label="Password" type={visible ? 'text' : 'password'} value={password} onChange={setPassword} required />
    <button type="button" className="password-toggle" aria-pressed={visible} onClick={() => setVisible(!visible)}>{visible ? 'Hide password' : 'Show password'}</button>
    <div className="actions"><button className="btn-primary" disabled={busy}>{busy ? 'Signing in...' : 'Sign in'}</button><Link to="/forgot-password">Forgot password?</Link></div></form></Card>
}
export function ForgotPasswordPage() {
  const [email, setEmail] = useState(''), [busy, setBusy] = useState(false), [sent, setSent] = useState(false), [error, setError] = useState('')
  const submit = async (event: FormEvent) => { event.preventDefault(); setBusy(true); setError(''); try { await post('/api/auth/forgot-password', { email }); setSent(true) } catch (error) { setError(message(error)) } finally { setBusy(false) } }
  return <Card title="Reset your password">{sent ? <p role="status">If that email exists, we've sent a link.</p> : <form onSubmit={submit}>{error && <p role="alert">{error}</p>}
    <FormField label="Email" type="email" value={email} onChange={setEmail} required /><button className="btn-primary" disabled={busy}>{busy ? 'Sending...' : 'Send reset link'}</button></form>}<p><Link to="/login">Back to sign in</Link></p></Card>
}
export function ResetPasswordPage() {
  const [params] = useSearchParams(), [password, setPassword] = useState(''), [repeat, setRepeat] = useState(''), [busy, setBusy] = useState(false), [done, setDone] = useState(false), [error, setError] = useState('')
  const token = params.get('token')
  const submit = async (event: FormEvent) => { event.preventDefault(); if (password !== repeat) { setError('The passwords must match.'); return } setBusy(true); setError(''); try { await post('/api/auth/reset-password', { token, password }); setDone(true); window.history.replaceState(null, '', '/reset-password') } catch (error) { setError(message(error)) } finally { setBusy(false) } }
  return <Card title="Choose a new password">{done ? <p role="status">Your password has been changed. You can sign in now.</p> : !token ? <p role="alert">This link is missing a reset token. Request a new link.</p> :
    <form onSubmit={submit}>{error && <p role="alert">{error}</p>}<FormField label="New password" type="password" value={password} onChange={setPassword} required minLength={12} maxLength={72} />
      <FormField label="Confirm password" type="password" value={repeat} onChange={setRepeat} required /><button className="btn-primary" disabled={busy}>{busy ? 'Saving...' : 'Reset password'}</button></form>}<p><Link to="/login">Back to sign in</Link></p></Card>
}
export function AccountPage() {
  const { user } = useAuth(), toast = useToast()
  const [name, setName] = useState(user?.name ?? ''), [email, setEmail] = useState(user?.email ?? ''), [currentPassword, setCurrent] = useState(''), [password, setPassword] = useState(''), [errors, setErrors] = useState<FieldError[]>([]), [busy, setBusy] = useState(false)
  const submit = async (event: FormEvent) => { event.preventDefault(); setBusy(true); setErrors([]); try { await put('/api/auth/me', { name, email, currentPassword, ...(password ? { password } : {}) }); setCurrent(''); setPassword(''); if (password) { toast.success('Password changed. Sign in again.'); await refreshSession().catch(() => {}) } else { const fresh = await get('/api/auth/me'); toast.success(`Saved account for ${fresh.name}`); await refreshSession() } } catch (error) { setErrors((error as { problem?: { errors?: FieldError[] } }).problem?.errors ?? []); toast.error(message(error)) } finally { setBusy(false) } }
  return <><h1>My account</h1><form className="admin-card" onSubmit={submit}><FormField label="Name" name="name" value={name} onChange={setName} errors={errors} required />
    <FormField label="Email" name="email" type="email" value={email} onChange={setEmail} errors={errors} required />
    <FormField label="Current password" name="currentPassword" type="password" value={currentPassword} onChange={setCurrent} errors={errors} required />
    <FormField label="New password (optional)" name="password" type="password" value={password} onChange={setPassword} errors={errors} minLength={12} maxLength={72} />
    <button className="btn-primary" disabled={busy}>{busy ? 'Saving...' : 'Save account'}</button></form></>
}
