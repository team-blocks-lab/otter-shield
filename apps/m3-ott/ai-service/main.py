
import hashlib
import json
import logging
import os
import shutil
import subprocess
import sys
import threading
import time

from contextlib import asynccontextmanager
from pathlib import Path, PurePosixPath

import boto3
import requests
from fastapi import FastAPI


# ==================================================
# 1. 로그 및 환경변수 설정
# ==================================================

logging.basicConfig(level=logging.INFO)
log = logging.getLogger("ott-ai")

BUCKET = os.environ["OTT_MEDIA_BUCKET"]

VIDEO_PREFIX = os.getenv(
    "OTT_VIDEO_PREFIX", "test/videos"
).strip("/")

THUMBNAIL_PREFIX = os.getenv(
    "OTT_THUMBNAIL_PREFIX", "test/thumbnails"
).strip("/")

SUBTITLE_PREFIX = os.getenv(
    "OTT_SUBTITLE_PREFIX", "test/subtitles"
).strip("/")

SPRING_URL = os.environ[
    "OTT_INTERNAL_API_URL"
].rstrip("/")

API_TOKEN = os.environ["AI_API_TOKEN"]

SCAN_INTERVAL = int(
    os.getenv("SCAN_INTERVAL", "60")
)

RETRY_COOLDOWN = 6 * 60 * 60

WORK_ROOT = Path("/work")

if not API_TOKEN:
    raise RuntimeError("AI_API_TOKEN이 비어 있습니다.")

if SCAN_INTERVAL < 10:
    raise RuntimeError(
        "SCAN_INTERVAL은 최소 10초 이상이어야 합니다."
    )

s3 = boto3.client("s3")

stop_event = threading.Event()

retry_after = {}


# ==================================================
# 2. Spring Boot 내부 API 인증
# ==================================================

def headers():
    return {
        "X-AI-Token": API_TOKEN
    }


# ==================================================
# 3. 영상 원본 식별값 생성
# ==================================================

def fingerprint(key, obj):
    identity = (
        f"{key}\n"
        f"{obj.get('VersionId', '')}\n"
        f"{obj.get('ETag', '')}\n"
        f"{obj.get('Size', '')}"
    )

    return hashlib.sha256(
        identity.encode("utf-8")
    ).hexdigest()


# ==================================================
# 4. 기존 처리 완료 여부 확인
# ==================================================

def completed(key, source_id):
    response = requests.get(
        f"{SPRING_URL}/internal/ai/status",
        params={
            "videoKey": key,
            "fingerprint": source_id
        },
        headers=headers(),
        timeout=20
    )

    response.raise_for_status()

    return response.json()["completed"]


# ==================================================
# 5. 유효한 자막 파일인지 확인
# ==================================================

def valid_subtitle(path):
    if not path.is_file():
        return False

    content = path.read_text(
        encoding="utf-8"
    )

    return " --> " in content


# ==================================================
# 6. AI 영상 처리
# ==================================================

def process_video(key, source_id):

    log.info("AI 처리 시작: %s", key)

    # 영상 버전별 고정 작업 폴더
    job_dir = WORK_ROOT / source_id
    job_dir.mkdir(
        parents=True,
        exist_ok=True
    )

    source = job_dir / "source.mp4"
    output = job_dir / "output"

    resume_record = (
        output / "source-video.json"
    )

    segments = (
        output / "audio" / "segments.csv"
    )

    # ----------------------------------------------
    # 6-1. 원본 영상 다운로드
    # ----------------------------------------------

    if not source.is_file():

        temp_source = (
            job_dir / "source.mp4.part"
        )

        s3.download_file(
            BUCKET,
            key,
            str(temp_source)
        )

        temp_source.replace(source)

        log.info(
            "원본 다운로드 완료: %s",
            key
        )

    else:

        log.info(
            "기존 원본 영상 재사용: %s",
            key
        )

    # ----------------------------------------------
    # 6-2. Resume 가능 여부 확인
    # ----------------------------------------------

    can_resume = (
        resume_record.is_file()
        and segments.is_file()
    )

    if can_resume:

        log.info(
            "기존 AI 작업 이어서 실행: %s",
            key
        )

    else:

        if output.exists():
            shutil.rmtree(output)

        log.info(
            "AI 신규 작업 실행: %s",
            key
        )

    # ----------------------------------------------
    # 6-3. M5 AI 코드 실행
    # ----------------------------------------------

    command = [
        sys.executable,
        "/app/process_video.py",
        str(source),
        str(output)
    ]

    if can_resume:
        command.append("--resume")

    subprocess.run(
        command,
        check=True,
        timeout=3600
    )

    # ----------------------------------------------
    # 6-4. 생성된 메타데이터 읽기
    # ----------------------------------------------

    audio = output / "audio"

    metadata_path = (
        audio / "metadata.json"
    )

    metadata = json.loads(
        metadata_path.read_text(
            encoding="utf-8"
        )
    )

    relative = PurePosixPath(
        key[len(VIDEO_PREFIX) + 1:]
    )

    stem = relative.with_suffix(
        ""
    ).as_posix()

    version = source_id[:16]

    # ----------------------------------------------
    # 6-5. 썸네일 S3 업로드
    # ----------------------------------------------

    thumbnail_key = (
        f"{THUMBNAIL_PREFIX}/"
        f"{stem}-{version}.jpg"
    )

    s3.upload_file(
        str(audio / "thumbnail.jpg"),
        BUCKET,
        thumbnail_key,
        ExtraArgs={
            "ContentType": "image/jpeg"
        }
    )

    log.info(
        "썸네일 업로드 완료: %s",
        thumbnail_key
    )

    # ----------------------------------------------
    # 6-6. 영어·한국어 자막 S3 업로드
    # ----------------------------------------------

    subtitles = []

    for language in ("ko", "en"):

        local = (
            audio / f"{language}.vtt"
        )

        if not valid_subtitle(local):

            log.info(
                "자막 없음, 업로드 생략: %s",
                language
            )

            continue

        subtitle_key = (
            f"{SUBTITLE_PREFIX}/"
            f"{stem}-{version}."
            f"{language}.vtt"
        )

        s3.upload_file(
            str(local),
            BUCKET,
            subtitle_key,
            ExtraArgs={
                "ContentType":
                    "text/vtt; charset=utf-8"
            }
        )

        subtitles.append({
            "language": language,
            "subtitleKey": subtitle_key
        })

        log.info(
            "자막 업로드 완료: %s",
            subtitle_key
        )

    # ----------------------------------------------
    # 6-7. 영상 정보 구성
    # ----------------------------------------------

    title = (
        relative.stem
        .replace("_", " ")
        .replace("-", " ")
    )[:200]

    summary = (
        metadata.get("summary")
        or "영상 소개가 준비 중입니다."
    )

    duration = int(
        metadata["duration_seconds"]
    )

    if duration <= 0:
        raise ValueError(
            "영상 길이가 올바르지 않습니다."
        )

    # ----------------------------------------------
    # 6-8. Spring Boot에 처리 결과 전달
    # ----------------------------------------------

    payload = {
        "videoKey": key,
        "fingerprint": source_id,
        "title": title,
        "thumbnailKey": thumbnail_key,
        "summary": summary,
        "durationSeconds": duration,
        "subtitles": subtitles
    }

    response = requests.post(
        f"{SPRING_URL}/internal/ai/complete",
        json=payload,
        headers=headers(),
        timeout=30
    )

    response.raise_for_status()

    log.info(
        "영상·자막 DB 등록 완료: %s",
        key
    )

    # ----------------------------------------------
    # 6-9. 전체 작업 성공 시 임시 파일 삭제
    # ----------------------------------------------

    shutil.rmtree(job_dir)

    log.info(
        "AI 작업 완료 및 임시 파일 정리: %s",
        key
    )


# ==================================================
# 7. S3 신규 영상 자동 감지
# ==================================================

def scan():

    paginator = s3.get_paginator(
        "list_objects_v2"
    )

    for page in paginator.paginate(
        Bucket=BUCKET,
        Prefix=f"{VIDEO_PREFIX}/"
    ):

        for obj in page.get(
            "Contents", []
        ):

            if stop_event.is_set():
                return

            key = obj["Key"]

            # MP4만 처리
            if not key.lower().endswith(".mp4"):
                continue

            relative = key[
                len(VIDEO_PREFIX) + 1:
            ]

            parts = PurePosixPath(
                relative
            ).parts

            # 비정상적인 경로 제외
            if (
                not relative
                or any(
                    part in ("", ".", "..")
                    for part in parts
                )
            ):
                continue

            source_id = fingerprint(
                key, obj
            )

            # --------------------------------------
            # 이전 실패 영상 재시도 제한
            # --------------------------------------

            job_key = (key, source_id)

            next_retry = retry_after.get(
                job_key, 0
            )

            if time.monotonic() < next_retry:
                continue

            try:

                # 이미 처리한 영상이면 건너뜀
                if completed(
                    key, source_id
                ):

                    retry_after.pop(
                        job_key, None
                    )

                    continue

                # 신규 또는 미완료 영상 처리
                process_video(
                    key, source_id
                )

                retry_after.pop(
                    job_key, None
                )

            except Exception:

                retry_after[job_key] = (
                    time.monotonic()
                    + RETRY_COOLDOWN
                )

                log.exception(
                    "AI 처리 실패: %s / "
                    "6시간 후 재시도",
                    key
                )


# ==================================================
# 8. S3 주기적 조회
# ==================================================

def scan_loop():

    while not stop_event.is_set():

        try:
            scan()

        except Exception:
            log.exception(
                "S3 목록 조회 실패"
            )

        stop_event.wait(
            SCAN_INTERVAL
        )


# ==================================================
# 9. FastAPI 시작 / 종료
# ==================================================

@asynccontextmanager
async def lifespan(app: FastAPI):

    WORK_ROOT.mkdir(
        parents=True,
        exist_ok=True
    )

    stop_event.clear()

    thread = threading.Thread(
        target=scan_loop,
        daemon=True,
        name="ott-ai-scanner"
    )

    thread.start()

    log.info(
        "AI 자동 처리 서비스 시작"
    )

    yield

    stop_event.set()

    thread.join(timeout=5)

    log.info(
        "AI 자동 처리 서비스 종료"
    )


# ==================================================
# 10. FastAPI 애플리케이션
# ==================================================

app = FastAPI(
    title="OTTer AI Service",
    lifespan=lifespan,
    docs_url=None,
    redoc_url=None,
    openapi_url=None
)


@app.get("/health")
def health():
    return {
        "status": "ok"
    }

