import { mockApi } from "./mock/mockApi";
import { realApi, LOCAL_DEMO_PLAYBACK } from "./realApi";

// 환경변수가 없으면 Mock. 백엔드와 연결하려면 .env 에 VITE_USE_MOCK=false
export const USE_MOCK = String(import.meta.env.VITE_USE_MOCK ?? "true") !== "false";
export const IS_LOCAL_DEMO = USE_MOCK || LOCAL_DEMO_PLAYBACK;
export const api = USE_MOCK ? mockApi : realApi;
export { ApiError } from "./client";
