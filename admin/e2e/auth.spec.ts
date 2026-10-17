import { test, expect } from '@playwright/test'
import type { Page } from '@playwright/test'
async function authentication(page: Page, role = 'ADMIN') {
  let signedIn = false
  await page.route('**/api/auth/**', async route => {
    const path = new URL(route.request().url()).pathname
    if (path.endsWith('/logout')) { signedIn = false; await route.fulfill({ status: 204 }); return }
    if (path.endsWith('/login')) {
      const data = route.request().postDataJSON()
      if (data.password !== 'test-password-123') { await route.fulfill({ status: 401, json: { detail: 'Invalid credentials' } }); return }
      signedIn = true
    }
    await route.fulfill(signedIn ? { json: { accessToken: 'browser-test-token', user: { id: 'test-user', name: 'Test Staff', email: 'staff@example.test', role } } } : { status: 401, json: { detail: 'No session' } })
  })
}
test('staff can sign in, restore a session after reload, and log out', async ({ page }) => {
  await authentication(page); await page.goto('/sermons')
  await expect(page.getByRole('heading', { name: 'Staff sign in' })).toBeVisible()
  await page.getByLabel(/^Email/).fill('staff@example.test'); await page.getByLabel(/^Password/).fill('test-password-123')
  await page.getByRole('button', { name: 'Sign in', exact: true }).click()
  await expect(page.getByRole('heading', { name: 'Sermons', exact: true })).toBeVisible()
  await page.reload(); await expect(page.getByRole('heading', { name: 'Sermons', exact: true })).toBeVisible()
  await page.getByRole('link', { name: 'Dashboard' }).click(); await expect(page.getByRole('heading', { name: 'Dashboard', exact: true })).toBeVisible()
  await page.getByRole('button', { name: 'Log out', exact: true }).click(); await expect(page.getByRole('heading', { name: 'Staff sign in' })).toBeVisible()
})
test('editor cannot access administrator screens and credentials stay out of storage', async ({ page }) => {
  await authentication(page, 'EDITOR'); await page.goto('/login'); await page.getByLabel(/^Email/).fill('staff@example.test')
  await page.getByLabel(/^Password/).fill('test-password-123'); await page.getByRole('button', { name: 'Sign in', exact: true }).click()
  await expect(page.getByRole('link', { name: 'Users', exact: true })).toHaveCount(0)
  await page.goto('/users'); await expect(page.getByRole('heading', { name: '403 - Access denied' })).toBeVisible()
  expect(await page.evaluate(() => [localStorage.length, sessionStorage.length])).toEqual([0, 0])
})
test('invalid credentials show an accessible error and unsafe redirects are ignored', async ({ page }) => {
  await authentication(page); await page.goto('/login?next=https://example.test')
  await page.getByLabel(/^Email/).fill('staff@example.test'); await page.getByLabel(/^Password/).fill('wrong')
  await page.getByRole('button', { name: 'Sign in', exact: true }).click(); await expect(page.getByRole('alert')).toContainText('invalid')
  await page.getByLabel(/^Password/).fill('test-password-123'); await page.getByRole('button', { name: 'Sign in', exact: true }).click()
  await expect(page).toHaveURL('http://127.0.0.1:4174/')
})
