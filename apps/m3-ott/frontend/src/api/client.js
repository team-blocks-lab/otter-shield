// Spring Boot 호출용 공통 fetch. 세션 쿠키 전송을 위해 credentials: "include".
// 개발 중에는 vite.config.js의 /api 프록시(→ localhost:8081)를 쓰므로 CORS가 필요 없다.
// 배포 시 웹과 API 도메인이 다르면 서버 CORS(Allow-Credentials, 정확한 Origin)와
// 쿠키 속성(Secure; SameSite=None; HTTPS)이 필요하다. 현재 백엔드에는 CORS 설정이 없다.
const BASE = (import.meta.env.VITE_API_BASE_URL || "").replace(/\/$/, "");

export class ApiError extends Error {
  constructor(status, message, fieldErrors = {}) {
    super(message);
    this.status = status;
    this.fieldErrors = fieldErrors; // 백엔드 ApiErrorResponse.fieldErrors
  }
}

export async function request(path, { method = "GET", body, signal } = {}) {
  let res;
  try {
    res = await fetch(`${BASE}${path}`, {
      method,
      credentials: "include",
      headers: body ? { "Content-Type": "application/json" } : undefined,
      body: body ? JSON.stringify(body) : undefined,
      signal,
    });
  } catch (e) {
    if (e.name === "AbortError") throw e;
    throw new ApiError(0, "서버에 연결할 수 없습니다. 백엔드(8081) 실행 여부를 확인해 주세요.");
  }
  if (res.status === 204) return null;
  let data = null;
  try { data = await res.json(); } catch { /* 본문 없음 */ }
  if (!res.ok) throw new ApiError(res.status, data?.message || "요청을 처리하지 못했습니다.", data?.fieldErrors || {});
  return data;
}
