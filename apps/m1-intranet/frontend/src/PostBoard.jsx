import { useEffect, useRef, useState } from "react";
import { getPosts, createPost, updatePost, deletePost } from "./api";
import PostImage from "./PostImage";
import PhotoPicker from "./PhotoPicker";

const EMPTY_FORM = {
  category: "PRAISE",
  title: "",
  content: "",
  anonymous: false,
};

const FILTERS = [
  { id: "ALL", label: "전체" },
  { id: "PRAISE", label: "칭찬" },
  { id: "LUNCH", label: "점심" },
  { id: "MINE", label: "내 글" },
];

function PostAvatar({ post }) {
  const name = post.authorDisplayName || "직원";
  return (
    <span
      className={`post-avatar${post.anonymous ? " post-avatar--anonymous" : ""}`}
      aria-hidden="true"
    >
      {post.anonymous ? "익" : name.charAt(0)}
    </span>
  );
}

function formatDate(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  return date.toLocaleDateString("ko-KR", { month: "long", day: "numeric" });
}

export default function PostBoard({
  nickname,
  filter,
  onFilterChange,
  search,
}) {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [form, setForm] = useState({ ...EMPTY_FORM });
  const [imageFile, setImageFile] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [composerOpen, setComposerOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const composeRef = useRef(null);

  // 서버에서 게시글과 페이지 정보 조회
  async function loadPosts(nextPage = 0) {
    setLoading(true);
    try {
      const feed = await getPosts(nextPage);
      setPosts(feed.posts ?? []);
      setPage(feed.page ?? nextPage);
      setTotalPages(feed.totalPages ?? 0);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  // 게시판 진입 시 첫 페이지 조회
  useEffect(() => {
    loadPosts(0);
  }, []);

  function updateField(name, value) {
    setForm((previous) => ({
      ...previous,
      [name]: value,
      ...(name === "category" && value === "LUNCH" ? { anonymous: false } : {}),
    }));
  }

  function openComposer() {
    setComposerOpen(true);
    setError("");
    setMessage("");
  }

  function resetComposer() {
    setComposerOpen(false);
    setForm({ ...EMPTY_FORM });
    setImageFile(null);
    setEditingId(null);
  }

  // 새 글 등록과 본인 글 수정 요청 구분
  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setMessage("");
    const payload = {
      category: form.category,
      title: form.title.trim(),
      content: form.content.trim(),
      anonymous: form.anonymous,
    };
    if (!payload.title || !payload.content) {
      setError("제목과 내용을 입력해 주세요.");
      return;
    }

    setSaving(true);
    try {
      if (editingId === null) {
        // 게시글 JSON과 선택한 이미지를 하나의 multipart 요청으로 등록
        await createPost(payload, imageFile);
        setMessage("게시글이 등록되었습니다.");
      } else {
        await updatePost(editingId, payload);
        setMessage("게시글이 수정되었습니다.");
      }
      resetComposer();
      await loadPosts(0);
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  }

  // 본인 글을 수정 양식으로 불러오기
  function handleEdit(post) {
    setForm({
      category: post.category,
      title: post.title,
      content: post.content,
      anonymous: post.anonymous,
    });
    setImageFile(null);
    setEditingId(post.postId);
    setComposerOpen(true);
    setError("");
    setMessage("");
    requestAnimationFrame(() =>
      composeRef.current?.scrollIntoView({
        behavior: "smooth",
        block: "start",
      }),
    );
  }

  // 삭제 승인 후 서버와 최신 게시글 목록 동기화
  async function handleDelete(postId) {
    if (!window.confirm("게시글을 삭제하시겠습니까?")) return;
    setSaving(true);
    setError("");
    setMessage("");
    try {
      await deletePost(postId);
      if (editingId === postId) resetComposer();
      setMessage("게시글이 삭제되었습니다.");
      await loadPosts(posts.length === 1 && page > 0 ? page - 1 : page);
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  }

  // 서버 페이지 내 필터·검색만 수행하며 전체 DB 검색으로 오해하지 않도록 표시
  const visiblePosts = posts.filter((post) => {
    if (filter === "PRAISE" && post.category !== "PRAISE") return false;
    if (filter === "LUNCH" && post.category !== "LUNCH") return false;
    if (filter === "MINE" && !post.mine) return false;
    const keyword = search.trim().toLocaleLowerCase();
    return (
      !keyword ||
      `${post.title} ${post.content} ${post.authorDisplayName}`
        .toLocaleLowerCase()
        .includes(keyword)
    );
  });

  return (
    <section className="post-board" aria-label="게시판">
      <div className="composer-card" ref={composeRef}>
        {!composerOpen ? (
          <>
            <div className="composer-quick">
              <span className="user-avatar" aria-hidden="true">
                {(nickname || "직").charAt(0)}
              </span>
              <button
                type="button"
                className="composer-placeholder"
                onClick={openComposer}
              >
                오늘 어떤 이야기를 공유하고 싶나요?
              </button>
            </div>
            <div className="composer-toolbar">
              <button
                type="button"
                className="ghost-action"
                onClick={openComposer}
              >
                <span aria-hidden="true">▧</span> 사진 첨부
              </button>
              <button
                type="button"
                className="ghost-action"
                onClick={() => {
                  openComposer();
                  updateField("category", "PRAISE");
                }}
              >
                <span aria-hidden="true">♡</span> 칭찬하기
              </button>
              <button type="button" className="pill-btn" onClick={openComposer}>
                게시글 작성 <span aria-hidden="true">↗</span>
              </button>
            </div>
          </>
        ) : (
          <form className="compose-form" onSubmit={handleSubmit}>
            <div className="composer-title-row">
              <h2>{editingId === null ? "새 게시글 작성" : "게시글 수정"}</h2>
              <button
                className="icon-btn"
                type="button"
                onClick={resetComposer}
                aria-label="작성 창 닫기"
              >
                ×
              </button>
            </div>
            <label htmlFor="post-category">게시판</label>
            <select
              id="post-category"
              value={form.category}
              onChange={(e) => updateField("category", e.target.value)}
            >
              <option value="PRAISE">칭찬 게시판</option>
              <option value="LUNCH">점심 이야기</option>
            </select>
            <label htmlFor="post-title">제목</label>
            <input
              id="post-title"
              value={form.title}
              onChange={(e) => updateField("title", e.target.value)}
              maxLength={200}
              placeholder="제목을 입력해 주세요"
              required
            />
            <label htmlFor="post-content">내용</label>
            <textarea
              id="post-content"
              value={form.content}
              onChange={(e) => updateField("content", e.target.value)}
              maxLength={10000}
              rows={5}
              placeholder="팀원들과 공유할 이야기를 적어보세요"
              required
            />
            {editingId === null && (
              <div className="photo-input-area">
                <PhotoPicker
                  file={imageFile}
                  onChange={setImageFile}
                  disabled={saving}
                />
                <small>
                  JPG·PNG 사진 1장 첨부 가능 (화면에서 최대 5MB 제한)
                </small>
              </div>
            )}
            {form.category === "PRAISE" && (
              <label className="checkbox-label">
                <input
                  type="checkbox"
                  checked={form.anonymous}
                  onChange={(e) => updateField("anonymous", e.target.checked)}
                />{" "}
                익명으로 작성
              </label>
            )}
            <div className="compose-actions">
              <button
                type="button"
                className="secondary-btn"
                onClick={resetComposer}
                disabled={saving}
              >
                취소
              </button>
              <button type="submit" className="primary-btn" disabled={saving}>
                {editingId === null ? "등록" : "수정 완료"}
              </button>
            </div>
          </form>
        )}
      </div>

      {error && (
        <p role="alert" className="status-error">
          {error}
        </p>
      )}
      {message && (
        <p role="status" className="status-success">
          {message}
        </p>
      )}

      <div className="feed-heading-row">
        <div>
          <p className="eyebrow">LATEST POSTS</p>
          <h2>새로운 소식</h2>
        </div>
        <small className="muted">현재 페이지 기준 필터·검색</small>
      </div>
      <div className="filter-row" role="group" aria-label="게시글 분류">
        {FILTERS.map((item) => (
          <button
            key={item.id}
            type="button"
            className={`filter-chip${filter === item.id ? " selected" : ""}`}
            onClick={() => onFilterChange(item.id)}
            aria-pressed={filter === item.id}
          >
            {item.label}
          </button>
        ))}
      </div>

      {loading ? (
        <p className="empty-message">게시글을 불러오는 중입니다...</p>
      ) : visiblePosts.length === 0 ? (
        <p className="empty-message">
          현재 페이지에서 조건에 맞는 게시글이 없습니다.
        </p>
      ) : (
        <div className="feed-list">
          {visiblePosts.map((post) => (
            <article key={post.postId} className="feed-card">
              <header className="post-header">
                <PostAvatar post={post} />
                <div className="post-author-block">
                  <strong>{post.authorDisplayName}</strong>
                  <small>
                    {post.category === "PRAISE" ? "칭찬 게시판" : "점심 이야기"}
                    {post.createdAt ? ` · ${formatDate(post.createdAt)}` : ""}
                  </small>
                </div>
                {post.mine && <span className="mine-chip">내 글</span>}
              </header>
              {post.category === "PRAISE" && !post.imageUrl && (
                <div className="praise-visual" aria-hidden="true">
                  <span>THANK YOU / PRAISE</span>
                  <strong>함께해 주어서 고마워요 ♡</strong>
                </div>
              )}
              {post.imageUrl ? (
                <img
                  className="post-image"
                  src={post.imageUrl}
                  alt="게시글 첨부 사진"
                />
              ) : (
                <PostImage postId={post.postId} />
              )}
              <div className="post-content">
                <span
                  className={`category-tag ${post.category === "PRAISE" ? "category-praise" : "category-lunch"}`}
                >
                  {post.category === "PRAISE" ? "칭찬" : "점심"}
                </span>
                <h3>{post.title}</h3>
                <p>{post.content}</p>
              </div>
              {post.mine && (
                <div className="post-actions">
                  <button
                    type="button"
                    className="secondary-btn"
                    onClick={() => handleEdit(post)}
                    disabled={saving}
                  >
                    수정
                  </button>
                  <button
                    type="button"
                    className="danger-link"
                    onClick={() => handleDelete(post.postId)}
                    disabled={saving}
                  >
                    삭제
                  </button>
                </div>
              )}
            </article>
          ))}
        </div>
      )}

      <div className="pagination">
        <button
          type="button"
          className="secondary-btn"
          disabled={loading || page === 0}
          onClick={() => loadPosts(page - 1)}
        >
          이전
        </button>
        <span>
          {Math.min(page + 1, Math.max(totalPages, 1))} /{" "}
          {Math.max(totalPages, 1)}
        </span>
        <button
          type="button"
          className="secondary-btn"
          disabled={loading || page + 1 >= totalPages}
          onClick={() => loadPosts(page + 1)}
        >
          다음
        </button>
      </div>
    </section>
  );
}
