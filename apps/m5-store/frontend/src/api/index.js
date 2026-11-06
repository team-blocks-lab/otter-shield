import { USE_MOCK } from '../config'
import { realApi } from './real'
import { mockApi } from './mock'

// 화면 코드는 이 객체만 사용합니다. 모드 전환은 VITE_USE_MOCK 하나로만 결정됩니다.
export const api = USE_MOCK ? mockApi : realApi
export { ApiError } from './http'
