// 기본값은 실제 API 모드입니다. VITE_USE_MOCK=true 일 때만 모의 데이터를 사용합니다.
export const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'
