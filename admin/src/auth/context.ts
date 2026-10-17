import { createContext, useContext } from 'react'
export type User = { id: string; name: string; email: string; role: 'ADMIN' | 'EDITOR' }
export type Auth = { user: User | null; loading: boolean; login: (email: string, password: string) => Promise<void>; logout: () => Promise<void>; hasRole: (...roles: User['role'][]) => boolean }
export const AuthContext = createContext<Auth | null>(null)
export function useAuth() { const context = useContext(AuthContext); if (!context) throw new Error('AuthProvider required'); return context }
export function safeNext(value: string | null) { return value && /^\/(?![/\\])/.test(value) && !/^\/(login|forgot-password|reset-password)(?:[/?]|$)/.test(value) ? value : '/' }
