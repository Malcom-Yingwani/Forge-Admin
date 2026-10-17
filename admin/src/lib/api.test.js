import { afterEach, describe, expect, it, vi } from 'vitest'
import { get, post, setAccessToken } from './api'

afterEach(() => {
  vi.unstubAllGlobals()
  vi.useRealTimers()
  setAccessToken(null)
})

describe('API client scaffold', () => {
  it('parses successful JSON and posts JSON bodies', async () => {
    const fetch = vi.fn().mockResolvedValue(new Response('{"ok":true}', { status: 200 }))
    vi.stubGlobal('fetch', fetch)
    expect(await post('/example', { value: 1 })).toEqual({ ok: true })
    expect(fetch).toHaveBeenCalledWith(expect.stringContaining('/example'), expect.objectContaining({ method: 'POST', body: '{"value":1}' }))
  })

  it('preserves RFC 7807 detail and HTTP status', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response('{"detail":"Not ready"}', { status: 409 })))
    await expect(get('/example')).rejects.toMatchObject({ message: 'Not ready', status: 409 })
  })

  it('aborts requests at the configured timeout', async () => {
    vi.useFakeTimers()
    vi.stubGlobal('fetch', vi.fn((_url, { signal }) => new Promise((_resolve, reject) => {
      signal.addEventListener('abort', () => reject(new DOMException('Aborted', 'AbortError')), { once: true })
    })))
    const result = expect(get('/slow', { timeoutMs: 10 })).rejects.toMatchObject({ name: 'AbortError' })
    await vi.advanceTimersByTimeAsync(10)
    await result
  })

  it('refreshes once after 401 and retries with the in-memory access token', async () => {
    const fetch = vi.fn()
      .mockResolvedValueOnce(new Response('{}', { status: 401 }))
      .mockResolvedValueOnce(new Response('{"accessToken":"test-access-token"}', { status: 200 }))
      .mockResolvedValueOnce(new Response('{"ok":true}', { status: 200 }))
    vi.stubGlobal('fetch', fetch)
    expect(await get('/api/admin/example')).toEqual({ ok: true })
    expect(fetch).toHaveBeenCalledTimes(3)
    expect(fetch.mock.calls[1]).toEqual([expect.stringContaining('/api/auth/refresh'), expect.objectContaining({ method: 'POST', credentials: 'include' })])
    expect(fetch.mock.calls[2][1].headers.Authorization).toBe('Bearer test-access-token')
  })

  it('does not loop when the retried request still returns 401', async () => {
    const fetch = vi.fn()
      .mockResolvedValueOnce(new Response('{}', { status: 401 }))
      .mockResolvedValueOnce(new Response('{"accessToken":"test-access-token"}', { status: 200 }))
      .mockResolvedValueOnce(new Response('{"detail":"Unauthorized"}', { status: 401 }))
    vi.stubGlobal('fetch', fetch)
    await expect(get('/api/admin/example')).rejects.toMatchObject({ status: 401 })
    expect(fetch).toHaveBeenCalledTimes(3)
  })

  it('stops and clears the token when refresh fails', async () => {
    const fetch = vi.fn()
      .mockResolvedValueOnce(new Response('{}', { status: 401 }))
      .mockResolvedValueOnce(new Response('{}', { status: 401 }))
    vi.stubGlobal('fetch', fetch)
    await expect(get('/api/admin/example')).rejects.toThrow('Session refresh failed')
    expect(fetch).toHaveBeenCalledTimes(2)
  })
  it('does not refresh when a login fails and accepts empty 202 responses', async () => {
    const fetch = vi.fn().mockResolvedValueOnce(new Response('{"detail":"Invalid credentials"}', { status: 401 }))
      .mockResolvedValueOnce(new Response(null, { status: 202 }))
    vi.stubGlobal('fetch', fetch)
    await expect(post('/api/auth/login', { email: 'staff@example.test', password: 'wrong' })).rejects.toMatchObject({ status: 401 })
    expect(fetch).toHaveBeenCalledTimes(1)
    expect(await post('/api/auth/forgot-password', { email: 'unknown@example.test' })).toBeNull()
  })

})
