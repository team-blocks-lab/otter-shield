// 실제 Spring Boot 연동 (backend: com.otter.ott). URL과 DTO 필드는 업로드된 Controller/DTO 기준.
//   AuthController  /api/auth/{signup,login,me,logout}
//   VideoController /api/videos, /api/videos/{videoId}
// 재생 API(/api/videos/{id}/playback)와 자막 API는 백엔드에 아직 없다. (아래 playback 참고)
import { request, ApiError } from "./client";

const MEDIA_BASE = (import.meta.env.VITE_MEDIA_BASE_URL || "").replace(/\/$/, "");
export const LOCAL_DEMO_PLAYBACK = String(import.meta.env.VITE_LOCAL_DEMO_PLAYBACK ?? "false") === "true";
const LOCAL_VIDEO = "/media/elephants-dream.mp4";

// thumbnailKey는 S3 키다. CloudFront 주소(VITE_MEDIA_BASE_URL)가 설정된 경우에만 URL로 변환한다.
const mediaUrl = (key) => (!key ? null : /^(https?:)?\//.test(key) ? key : MEDIA_BASE ? `${MEDIA_BASE}/${key}` : null);

const toVideo = (v) => ({
  id: String(v.videoId),
  title: v.title,
  genre: v.genre ?? null,
  durationSec: v.durationSeconds,
  thumbnailUrl: mediaUrl(v.thumbnailKey),
  summary: v.summary ?? "", // 목록 응답에는 없고 상세에만 있다
  actor: v.actor ?? "",
});
const toMember = (m) => ({ id: m.memberId, name: m.name, loginId: m.loginId });

export const realApi = {
  auth: {
    me: async (signal) => toMember(await request("/api/auth/me", { signal })), // 비로그인 401
    login: async ({ loginId, password }) => toMember(await request("/api/auth/login", { method: "POST", body: { loginId, password } })),
    signup: ({ name, loginId, password }) => request("/api/auth/signup", { method: "POST", body: { name, loginId, password } }), // 201 {memberId}
    logout: () => request("/api/auth/logout", { method: "POST" }), // 204
  },
  videos: {
    async list({ page = 0, size = 24 } = {}, signal) {
      const r = await request(`/api/videos?page=${page}&size=${size}`, { signal }); // size 1~50
      return { items: r.videos.map(toVideo), page: r.page, totalPages: r.totalPages, total: r.totalElements, hasNext: r.page + 1 < r.totalPages };
    },
    async get(id, signal) {
      return toVideo(await request(`/api/videos/${encodeURIComponent(id)}`, { signal })); // PRIVATE/없음은 동일하게 404
    },
    // [백엔드 미구현] 계약 제안: GET /api/videos/{id}/playback -> { videoUrl, subtitles:[{lang:"ko"|"en", url}] }
    //  - 로그인 세션 확인(401), PRIVATE 접근 차단, S3/CloudFront 주소 생성은 서버가 한다.
    // 개발 중 VITE_LOCAL_DEMO_PLAYBACK=true 이면 로그인 확인(me)만 서버에 묻고 로컬 샘플 MP4를 재생한다.
    // 이 경로는 서버가 재생 권한을 검증한 것이 아니다. 시연용이며 배포 빌드에서는 false여야 한다.
    async playback(id, signal) {
      if (LOCAL_DEMO_PLAYBACK) {
        await request("/api/auth/me", { signal });
        return { videoUrl: LOCAL_VIDEO, subtitles: [{ lang: "ko", url: "/media/demo-ko.vtt" }, { lang: "en", url: "/media/demo-en.vtt" }], localDemo: true };
      }
      try {
        return await request(`/api/videos/${encodeURIComponent(id)}/playback`, { signal });
      } catch (e) {
        if (e.status === 404 || e.status === 405) throw new ApiError(501, "재생 API가 아직 준비되지 않았습니다.");
        throw e;
      }
    },
  },
};
