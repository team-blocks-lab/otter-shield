// MOCK API. 서버 인증·저장이 아니다. 새로고침 복원은 sessionStorage로 흉내만 낸다.
import { ApiError } from "../client";
import { MOCK_VIDEOS, MOCK_MEMBERS } from "./mockData";

const KEY = "otter.mock.session";
const delay = (ms = 300) => new Promise((r) => setTimeout(r, ms));
const members = [...MOCK_MEMBERS];
const session = () => { try { return JSON.parse(sessionStorage.getItem(KEY)); } catch { return null; } };
const pub = (m) => ({ id: m.id, loginId: m.loginId, name: m.name });
const strip = ({ visibility, createdAt, ...v }) => v;
const LOCAL = { videoUrl: "/media/elephants-dream.mp4", subtitles: [{ lang: "ko", url: "/media/demo-ko.vtt" }, { lang: "en", url: "/media/demo-en.vtt" }], localDemo: true };

export const mockApi = {
  auth: {
    async me() { await delay(120); const s = session(); if (!s) throw new ApiError(401, "로그인이 필요합니다."); return s; },
    async login({ loginId, password }) {
      await delay();
      const m = members.find((x) => x.loginId === loginId && x.password === password);
      if (!m) throw new ApiError(401, "아이디 또는 비밀번호가 올바르지 않습니다.");
      sessionStorage.setItem(KEY, JSON.stringify(pub(m))); return pub(m);
    },
    async signup({ name, loginId, password }) {
      await delay();
      if (members.some((x) => x.loginId === loginId)) throw new ApiError(409, "이미 사용 중인 아이디입니다.");
      members.push({ id: members.length + 1, loginId, name, password }); return { memberId: members.length };
    },
    async logout() { await delay(80); sessionStorage.removeItem(KEY); return null; },
  },
  videos: {
    async list({ page = 0, size = 24 } = {}) {
      await delay();
      const all = MOCK_VIDEOS.filter((v) => v.visibility === "PUBLIC").sort((a, b) => b.createdAt.localeCompare(a.createdAt));
      const totalPages = Math.max(1, Math.ceil(all.length / size));
      return { items: all.slice(page * size, page * size + size).map(strip), page, totalPages, total: all.length, hasNext: page + 1 < totalPages };
    },
    async get(id) {
      await delay(200);
      const v = MOCK_VIDEOS.find((x) => x.id === id);
      if (!v || v.visibility !== "PUBLIC") throw new ApiError(404, "영상을 찾을 수 없습니다.");
      return strip(v);
    },
    async playback(id) {
      await delay(200);
      if (!session()) throw new ApiError(401, "로그인이 필요합니다.");
      const v = MOCK_VIDEOS.find((x) => x.id === id);
      if (!v || v.visibility !== "PUBLIC") throw new ApiError(404, "영상을 찾을 수 없습니다.");
      return LOCAL; // Mock: 모든 항목이 로컬 샘플을 재생한다
    },
  },
};
