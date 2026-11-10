import argparse
import hashlib
import os
import subprocess
import sys
from pathlib import Path, PurePosixPath

import boto3
from dotenv import load_dotenv

SCRIPT_DIR = Path(__file__).resolve().parent


def main() -> None:
    load_dotenv(SCRIPT_DIR / ".env")

    parser = argparse.ArgumentParser(description="S3 영상의 영어·한국어 자막 처리")
    parser.add_argument("video_key", help="DB에 저장된 원본 영상의 S3 경로")
    parser.add_argument("--work-root", type=Path, required=True, help="작업 파일 저장 위치")
    parser.add_argument("--upload", action="store_true", help="생성한 자막을 S3에 업로드")
    args = parser.parse_args()

    bucket = os.getenv("OTT_MEDIA_BUCKET", "").strip()
    prefix = os.getenv("OTT_SUBTITLE_PREFIX", "").strip().strip("/")
    if not bucket or not prefix:
        raise SystemExit("OTT_MEDIA_BUCKET과 OTT_SUBTITLE_PREFIX를 설정해 주세요.")

    video_prefix = os.getenv("OTT_VIDEO_PREFIX", "test/videos").strip().strip("/")
    prefix_parts = video_prefix.split("/")
    key_parts = args.video_key.split("/")
    if (
        key_parts[:len(prefix_parts)] != prefix_parts
        or len(key_parts) <= len(prefix_parts)
        or any(part in ("", ".", "..") for part in key_parts)
        or not args.video_key.endswith(".mp4")
    ):
        raise SystemExit(f"{video_prefix}/ 아래의 .mp4 경로를 입력해 주세요.")

    # 원본 폴더 구조를 유지해 같은 파일명의 자막 경로 충돌 방지
    relative_video = PurePosixPath(*key_parts[len(prefix_parts):])

    s3 = boto3.client("s3")
    source = s3.head_object(Bucket=bucket, Key=args.video_key)

    # 원본이 바뀌면 별도 작업 폴더 사용
    source_id = f"{bucket}\n{args.video_key}\n{source.get('VersionId')}\n{source['ETag']}"
    job_id = hashlib.sha256(source_id.encode()).hexdigest()[:24]
    job_dir = args.work_root.resolve() / job_id
    job_dir.mkdir(parents=True, exist_ok=True)
    video = job_dir / "source.mp4"
    work_dir = job_dir / "output"

    if not video.is_file() or video.stat().st_size != source["ContentLength"]:
        print(f"원본 다운로드: {args.video_key}", flush=True)
        temp = video.with_suffix(".part")
        s3.download_file(bucket, args.video_key, str(temp))
        temp.replace(video)

    # 기존 오디오와 자막 캐시를 이용한 재실행
    command = [sys.executable, str(SCRIPT_DIR / "process_video.py"), str(video), str(work_dir)]
    if (work_dir / "source-video.json").is_file():
        command.append("--resume")
    subprocess.run(command, check=True)

    for language in ("en", "ko"):
        local_file = work_dir / "audio" / f"{language}.vtt"
        if not any(" --> " in line for line in local_file.read_text(encoding="utf-8").splitlines()):
            print(f"업로드 생략 [{language}]: 생성된 자막 없음")
            continue
        object_key = f"{prefix}/{relative_video.with_suffix(f'.{language}.vtt')}"

        if args.upload:
            s3.upload_file(
                str(local_file),
                bucket,
                object_key,
                ExtraArgs={"ContentType": "text/vtt; charset=utf-8"},
            )
            print(f"업로드 완료 [{language}]: {object_key}")
        else:
            print(f"업로드 예정 [{language}]: {object_key}")

    print(f"로컬 메타데이터: {work_dir / 'audio' / 'metadata.json'}")


if __name__ == "__main__":
    main()
