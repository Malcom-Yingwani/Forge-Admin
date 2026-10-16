const baseUrl = import.meta.env.VITE_API_URL ?? 'http://localhost:8081'
let accessToken = null
let refreshPromise = null

export function setAccessToken(token) { accessToken = token }

async function refresh(signal) {
  const response = await fetch(`${baseUrl}/api/auth/refresh`, { method: 'POST', credentials: 'include', signal })
  if (!response.ok) { accessToken = null; throw new Error('Session refresh failed') }
  const body = await response.json()
  accessToken = body.accessToken
}

export async function request(path, { timeoutMs = 30000, ...options } = {}) {
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), timeoutMs)
  try {
    const send = () => fetch(`${baseUrl}${path}`, {
      ...options,
      signal: controller.signal,
      credentials: 'include',
      headers: {
        Accept: 'application/json',
        ...(options.body ? { 'Content-Type': 'application/json' } : {}),
        ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
        ...options.headers,
      },
    })
    let response = await send()
    if (response.status === 401) {
      refreshPromise ??= refresh(controller.signal).finally(() => { refreshPromise = null })
      await refreshPromise
      response = await send()
    }
    if (!response.ok) {
      const problem = await response.json().catch(() => ({}))
      throw Object.assign(new Error(problem.detail ?? problem.title ?? response.statusText), { status: response.status, problem })
    }
    return response.status === 204 ? null : await response.json()
  } finally {
    clearTimeout(timer)
  }
}

export const get = (path, options) => request(path, { ...options, method: 'GET' })
export const post = (path, body, options) => request(path, { ...options, method: 'POST', body: JSON.stringify(body) })
