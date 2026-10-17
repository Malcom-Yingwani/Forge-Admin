const baseUrl = import.meta.env.VITE_API_URL ?? 'http://localhost:8081'
let accessToken = null
let refreshPromise = null
const listeners = new Set()
export function setAccessToken(token) { accessToken = token }
export function setSession(session) { accessToken = session?.accessToken ?? null; listeners.forEach(listener => listener(session)) }
export function subscribeSession(listener) { listeners.add(listener); return () => listeners.delete(listener) }
export function refreshSession() {
  refreshPromise ??= (async () => {
    const controller = new AbortController()
    const timer = setTimeout(() => controller.abort(), 30000)
    try {
      const response = await fetch(`${baseUrl}/api/auth/refresh`, { method: 'POST', credentials: 'include', signal: controller.signal })
      if (!response.ok) throw Object.assign(new Error('Session refresh failed'), { status: response.status })
      const session = await response.json()
      if (typeof session.accessToken !== 'string') throw new Error('Invalid session response')
      setSession(session)
      return session
    } catch (error) { setSession(null); throw error }
    finally { clearTimeout(timer) }
  })().finally(() => { refreshPromise = null })
  return refreshPromise
}
export async function request(path, { timeoutMs = 30000, retryAuth = !/^\/api\/auth\/(login|refresh|logout|forgot-password|reset-password)$/.test(path), signal, ...options } = {}) {
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), timeoutMs)
  const combined = signal ? AbortSignal.any([signal, controller.signal]) : controller.signal
  try {
    const send = () => fetch(`${baseUrl}${path}`, {
      ...options, signal: combined, credentials: 'include',
      headers: { Accept: 'application/json', ...(options.body && !(options.body instanceof FormData) ? { 'Content-Type': 'application/json' } : {}),
        ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}), ...options.headers },
    })
    let response = await send()
    if (response.status === 401 && retryAuth) { await refreshSession(); response = await send() }
    if (!response.ok) {
      if (response.status === 401 && retryAuth) setSession(null)
      const problem = await response.json().catch(() => ({}))
      throw Object.assign(new Error(problem.detail ?? problem.title ?? response.statusText), { status: response.status, problem })
    }
    const text = await response.text()
    return text ? JSON.parse(text) : null
  } finally { clearTimeout(timer) }
}
export const get = (path, options) => request(path, { ...options, method: 'GET' })
export const post = (path, body, options) => request(path, { ...options, method: 'POST', body: body === undefined ? undefined : JSON.stringify(body) })
export const put = (path, body, options) => request(path, { ...options, method: 'PUT', body: JSON.stringify(body) })
