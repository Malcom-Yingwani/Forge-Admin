import { BrowserRouter, Route, Routes } from 'react-router-dom'
import routes from './routes.json'

export default function App() {
  return (
    <BrowserRouter>
      <main>
        <Routes>
          {routes.map(([path, title]) => <Route key={path} path={path} element={<h1>{title}</h1>} />)}
          <Route path="*" element={<h1>404 — Page not found</h1>} />
        </Routes>
      </main>
    </BrowserRouter>
  )
}
// Temporary setup path-filter acceptance probe.
