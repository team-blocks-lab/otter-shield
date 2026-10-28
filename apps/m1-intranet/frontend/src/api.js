const API_BASE = "/api";

// 공통 HTTP 요청과 JSON 응답 처리
async function apiRequest(path, options = {}) {
  const isMultipart = options.body instanceof FormData;

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,

    // 브라우저 로그인 세션을 API 요청에도 전달
    credentials: "include",

    headers: {
      // FormData는 브라우저가 boundary 포함 Content-Type 자동 지정
      ...(options.body && !isMultipart
        ? { "Content-Type": "application/json" }
        : {}),
      ...options.headers,
    },
  });

  const text = await response.text();
  let data = null;

  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      if (response.ok) {
        throw new Error("서버 응답 형식을 확인해 주세요.");
      }
      throw new Error(`요청 실패 (${response.status})`);
    }
  }

  if (!response.ok) {
    const error = new Error(
      data?.message ?? `요청 실패 (${response.status})`
    );
    error.status = response.status;
    throw error;
  }

  return data;
}

// 직원 로그인
export function login(employeeNumber, password) {
  return apiRequest("/auth/login", {
    method: "POST",
    body: JSON.stringify({ employeeNumber, password }),
  });
}

// 현재 로그인 직원 조회
export function getCurrentEmployee() {
  return apiRequest("/auth/me");
}

// 최초 로그인 비밀번호 및 닉네임 설정
export function initialSetup(newPassword, nickname) {
  return apiRequest("/auth/initial-setup", {
    method: "POST",
    body: JSON.stringify({ newPassword, nickname }),
  });
}

// 로그아웃
export function logout() {
  return apiRequest("/auth/logout", { method: "POST" });
}

// 게시글 목록 조회
export function getPosts(page = 0, size = 10) {
  return apiRequest(`/post?page=${page}&size=${size}`);
}

// JSON과 이미지 파일을 multipart/form-data로 함께 등록
export function createPost(post, imageFile = null) {
  const formData = new FormData();

  // @RequestPart("post")의 JSON 형식 유지
  const postData = new Blob(
    [JSON.stringify({ ...post, imageKey: null })],
    { type: "application/json" }
  );
  formData.append("post", postData);

  // @RequestPart("image")의 파일명 및 MIME 유형 전달
  if (imageFile) {
    formData.append("image", imageFile);
  }

  return apiRequest("/post", {
    method: "POST",
    body: formData,
  });
}

// 게시글 수정 (기존 API: JSON 요청)
export function updatePost(postId, post) {
  return apiRequest(`/post/${postId}`, {
    method: "PUT",
    body: JSON.stringify(post),
  });
}

// 게시글 삭제
export function deletePost(postId) {
  return apiRequest(`/post/${postId}`, { method: "DELETE" });
}
