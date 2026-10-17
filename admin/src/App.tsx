import { BrowserRouter, Route, Routes } from 'react-router-dom'
import routes from './routes.json'
import { ToastProvider, ConfirmProvider } from './components/Providers'
import { lazy, Suspense } from 'react'
const ComponentsDemo = import.meta.env.DEV ? lazy(() => import('./components/ComponentsDemo')) : null

export default function App() {
  return (
    <ToastProvider><ConfirmProvider><BrowserRouter>
      <main>
        <Routes>
          {ComponentsDemo && <Route path="/dev/components" element={<Suspense fallback={<p>Loading…</p>}><ComponentsDemo /></Suspense>} /> }
          {routes.map(([path, title]) => <Route key={path} path={path} element={<h1>{title}</h1>} />)}
          <Route path="*" element={<h1>404 - Page not found</h1>} />
        </Routes>
      </main>
    </BrowserRouter></ConfirmProvider></ToastProvider>
  )
}
