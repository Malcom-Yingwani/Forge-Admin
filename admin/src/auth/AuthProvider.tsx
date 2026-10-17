import { useEffect, useState } from 'react'
import type { ReactNode } from 'react'
import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { post, refreshSession, setSession, subscribeSession } from '../lib/api'
import { AuthContext, useAuth } from './context'
import type { User } from './context'
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null), [loading, setLoading] = useState(true)
  useEffect(() => {
    let active = true
    const unsubscribe = subscribeSession((session: { user: User } | null) => setUser(session?.user ?? null))
    void refreshSession().catch(() => {}).finally(() => { if (active) setLoading(false) })
    return () => { active = false; unsubscribe() }
  }, [])
  const login = async (email: string, password: string) => { const session = await post('/api/auth/login', { email, password }); setSession(session) }
  const logout = async () => { await post('/api/auth/logout'); setSession(null) }
  return <AuthContext.Provider value={{ user, loading, login, logout, hasRole: (...roles) => !!user && roles.includes(user.role) }}>{children}</AuthContext.Provider>
}
export function RequireRole({ roles }: { roles?: User['role'][] }) {
  const { user, loading } = useAuth(), location = useLocation()
  if (loading) return <main><p role="status">Restoring your session...</p></main>
  if (!user) return <Navigate to={`/login?next=${encodeURIComponent(location.pathname + location.search)}`} replace />
  if (roles && !roles.includes(user.role)) return <main><h1>403 - Access denied</h1><p>Your role does not allow access to this screen.</p></main>
  return <Outlet />
}
