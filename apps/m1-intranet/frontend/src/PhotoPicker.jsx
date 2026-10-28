import { useEffect, useRef, useState } from "react";

const MAX_FILE_SIZE = 5 * 1024 * 1024;

export default function PhotoPicker({ file, onChange, disabled = false }) {
  const [previewUrl, setPreviewUrl] = useState(null);
  const [error, setError] = useState("");
  const inputRef = useRef(null);

  // 선택한 이미지 파일의 브라우저 미리보기 URL 생성 및 정리
  useEffect(() => {
    if (!file) {
      setPreviewUrl(null);
      return;
    }

    const url = URL.createObjectURL(file);
    setPreviewUrl(url);

    return () => URL.revokeObjectURL(url);
  }, [file]);

  // JPEG·PNG 형식 및 파일 크기 검사
  function handleFileChange(event) {
    const selected = event.target.files?.[0];

    if (!selected) return;

    if (!["image/jpeg", "image/png"].includes(selected.type)) {
      setError("JPG 또는 PNG 사진만 선택할 수 있습니다.");
      onChange(null);
      event.target.value = "";
      return;
    }

    if (selected.size > MAX_FILE_SIZE) {
      setError("사진은 5MB 이하로 선택해 주세요.");
      onChange(null);
      event.target.value = "";
      return;
    }

    setError("");
    onChange(selected);
  }

  // 선택한 사진 제거 및 파일 입력 초기화
  function handleRemove() {
    onChange(null);
    setError("");

    if (inputRef.current) {
      inputRef.current.value = "";
    }
  }

  return (
    <div className="photo-picker">
      <label htmlFor="post-photo">사진 첨부 (선택)</label>

      <input
        ref={inputRef}
        id="post-photo"
        type="file"
        accept="image/jpeg,image/png"
        onChange={handleFileChange}
        disabled={disabled}
      />

      {error && (
        <p role="alert" className="error">
          {error}
        </p>
      )}

      {previewUrl && (
        <div>
          <img
            src={previewUrl}
            alt="첨부할 사진 미리보기"
            className="post-image"
          />

          <p>{file.name}</p>

          <button type="button" onClick={handleRemove} disabled={disabled}>
            사진 제거
          </button>
        </div>
      )}
    </div>
  );
}
