// 실제 API 호출 공통부: 상대 경로 /api, 세션 쿠키(same-origin), CSRF 헤더.
// 비밀번호, 세션 값, 토큰은 코드나 localStorage에 저장하지 않습니다.
// CSRF 토큰은 이 모듈의 메모리 변수에만 보관하며 새로고침하면 다시 받습니다.

export class ApiError extends Error {
  constructor(message, { status = 0, fieldErrors = {}, network = false } = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fieldErrors = fieldErrors
    this.network = network
  }
}

const MUTATING = new Set(['POST', 'PUT', 'PATCH', 'DELETE'])
let csrf = null // { headerName, token }

async function parseBody(res) {
  const text = await res.text()
  if (!text) return null
  try {
    return JSON.parse(text)
  } catch {
    return null
  }
}

async function send(path, options) {
  try {
    return await fetch(path, { credentials: 'same-origin', ...options })
  } catch {
    throw new ApiError('서버에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.', { network: true })
  }
}

function toError(res, body) {
  return new ApiError(body?.message || `요청을 처리하지 못했습니다. (${res.status})`, {
    status: res.status,
    fieldErrors: body?.fieldErrors || {},
  })
}

export async function loadCsrf() {
  const res = await send('/api/auth/csrf', { method: 'GET', headers: { Accept: 'application/json' } })
  const body = await parseBody(res)
  if (!res.ok || !body?.headerName || !body?.token) throw toError(res, body)
  csrf = { headerName: body.headerName, token: body.token }
  return csrf
}

export function clearCsrf() {
  csrf = null
}

export async function request(method, path, payload, { retried = false } = {}) {
  const headers = { Accept: 'application/json' }
  const options = { method, headers }
  if (payload !== undefined) {
    headers['Content-Type'] = 'application/json'
    options.body = JSON.stringify(payload)
  }
  if (MUTATING.has(method)) {
    if (!csrf) await loadCsrf()
    headers[csrf.headerName] = csrf.token
  }
  const res = await send(path, options)
  const body = await parseBody(res)
  if (res.ok) return body
  // CSRF 토큰이 만료·교체된 경우 한 번만 새 토큰으로 재시도합니다.
  if (res.status === 403 && MUTATING.has(method) && !retried) {
    await loadCsrf()
    return request(method, path, payload, { retried: true })
  }
  throw toError(res, body)
}
