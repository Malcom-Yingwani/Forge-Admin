import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { lazy, Suspense } from 'react'
import routes from './routes.json'
import { ToastProvider, ConfirmProvider } from './components/Providers'
import { AuthProvider, RequireRole } from './auth/AuthProvider'
import { LoginPage, ForgotPasswordPage, ResetPasswordPage, AccountPage } from './auth/Pages'
import { Shell } from './auth/Shell'
const ComponentsDemo = import.meta.env.DEV ? lazy(() => import('./components/ComponentsDemo')) : null
export default function App() {
  return <ToastProvider><ConfirmProvider><BrowserRouter><AuthProvider><Routes>
    <Route path="/login" element={<LoginPage />} /><Route path="/forgot-password" element={<ForgotPasswordPage />} /><Route path="/reset-password" element={<ResetPasswordPage />} />
    {ComponentsDemo && <Route path="/dev/components" element={<main><Suspense fallback={<p>Loading...</p>}><ComponentsDemo /></Suspense></main>} />}
    <Route element={<RequireRole />}><Route element={<Shell />}>
      <Route path="/account" element={<AccountPage />} />
      {routes.filter(([path]) => !['/login','/account','/sync','/users'].includes(path)).map(([path,title]) => <Route key={path} path={path} element={<><h1>{title}</h1><p>This screen is ready for the next implementation step.</p></>} />)}
      <Route element={<RequireRole roles={['ADMIN']} />}><Route path="/sync" element={<h1>Sync</h1>} /><Route path="/users" element={<h1>Users</h1>} /></Route>
      <Route path="*" element={<><h1>404 - Page not found</h1><p>Use the navigation to find a staff tool.</p></>} />
    </Route></Route>
  </Routes></AuthProvider></BrowserRouter></ConfirmProvider></ToastProvider>
}
