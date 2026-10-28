import { useEffect, useState } from "react";

export default function PostImage({ postId }) {
  const [imageUrl, setImageUrl] = useState(null);
  const [error, setError] = useState(false);

  useEffect(() => {
    const controller = new AbortController();
    let objectUrl = null;

    // 로그인 세션을 포함한 게시글 첨부 사진 조회
    async function loadImage() {
      try {
        const response = await fetch(`/api/post/${postId}/image`, {
          credentials: "include",
          signal: controller.signal,
        });

        // 첨부 사진이 없는 게시글의 404 응답 처리
        if (response.status === 404) {
          setImageUrl(null);
          return;
        }

        if (!response.ok) {
          throw new Error("사진 조회 실패");
        }

        // 서버 이미지 데이터를 브라우저 표시용 URL로 변환
        const blob = await response.blob();

        if (controller.signal.aborted) return;

        objectUrl = URL.createObjectURL(blob);
        setImageUrl(objectUrl);
        setError(false);
      } catch (err) {
        if (err.name !== "AbortError") {
          setError(true);
        }
      }
    }

    setImageUrl(null);
    setError(false);
    loadImage();

    // 게시글 변경·화면 종료 시 요청 및 임시 URL 정리
    return () => {
      controller.abort();

      if (objectUrl) {
        URL.revokeObjectURL(objectUrl);
      }
    };
  }, [postId]);

  if (error) {
    return <small>첨부 사진을 불러오지 못했습니다.</small>;
  }

  if (!imageUrl) return null;

  return <img src={imageUrl} alt="게시글 첨부 사진" className="post-image" />;
}
