
import hashlib
import json
import logging
import os
import subprocess
import sys
import tempfile
import threading
from contextlib import asynccontextmanager
from pathlib import Path, PurePosixPath

import boto3
import requests
from fastapi import FastAPI

logging.basicConfig(level=logging.INFO)
log = logging.getLogger("ott-ai")

BUCKET = os.environ["OTT_MEDIA_BUCKET"]
VIDEO_PREFIX = os.getenv("OTT_VIDEO_PREFIX", "videos").strip("/")
SUBTITLE_PREFIX = os.getenv("OTT_SUBTITLE_PREFIX", "subtitles").strip("/")
THUMBNAIL_PREFIX = os.getenv("OTT_THUMBNAIL_PREFIX", "thumbnails").strip("/")
SPRING_URL = os.environ["OTT_INTERNAL_API_URL"].rstrip("/")
API_TOKEN = os.environ["AI_API_TOKEN"]
SCAN_INTERVAL = int(os.getenv("SCAN_INTERVAL", "60"))

if not API_TOKEN or SCAN_INTERVAL < 10:
    raise RuntimeError("AI 인증 토큰 또는 조회 간격 설정 오류")

s3 = boto3.client("s3")
stop_event = threading.Event()
session = requests.Session()

def headers():
    return {"X-AI-Token": API_TOKEN}

def fingerprint(key, obj):
    identity = (
        f"{key}\n"
        f"{obj.get('VersionId', '')}\n"
        f"{obj.get('ETag', '')}\n"
        f"{obj.get('Size', '')}"
    )
    return hashlib.sha256(identity.encode()).hexdigest()

def completed(key, source_id):
    response = session.get(
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

def valid_subtitle(path):
    if not path.is_file():
        return False
    return " --> " in path.read_text(
        encoding="utf-8"
    )

def process_video(key, source_id):
    log.info("AI 처리 시작: %s", key)

    with tempfile.TemporaryDirectory(dir="/work") as temp:
        temp_path = Path(temp)
        source = temp_path / "source.mp4"
        output = temp_path / "output"

        s3.download_file(BUCKET, key, str(source))

        subprocess.run(
            [
                sys.executable,
                "/app/process_video.py",
                str(source),
                str(output)
            ],
            check=True,
            timeout=3600
        )

        audio = output / "audio"
        metadata = json.loads(
            (audio / "metadata.json").read_text(
                encoding="utf-8"
            )
        )

        relative = PurePosixPath(
            key[len(VIDEO_PREFIX) + 1:]
        )
        stem = relative.with_suffix("").as_posix()
        version = source_id[:16]

        thumbnail_key = (
            f"{THUMBNAIL_PREFIX}/{stem}-{version}.jpg"
        )

        s3.upload_file(
            str(audio / "thumbnail.jpg"),
            BUCKET,
            thumbnail_key,
            ExtraArgs={"ContentType": "image/jpeg"}
        )

        subtitles = []

        for language in ("ko", "en"):
            local = audio / f"{language}.vtt"

            if not valid_subtitle(local):
                continue

            subtitle_key = (
                f"{SUBTITLE_PREFIX}/{stem}-{version}."
                f"{language}.vtt"
            )

            s3.upload_file(
                str(local),
                BUCKET,
                subtitle_key,
                ExtraArgs={
                    "ContentType": "text/vtt; charset=utf-8"
                }
            )

            subtitles.append({
                "language": language,
                "subtitleKey": subtitle_key
            })

        title = relative.stem.replace("_", " ").replace("-", " ")

        summary = metadata.get("summary") or "영상 소개가 준비 중입니다."
        duration = int(metadata["duration_seconds"])

        if duration <= 0:
            raise ValueError("영상 길이가 올바르지 않습니다")

        payload = {
            "videoKey": key,
            "fingerprint": source_id,
            "title": title,
            "thumbnailKey": thumbnail_key,
            "summary": summary,
            "durationSeconds": duration,
            "subtitles": subtitles
        }

        response = session.post(
            f"{SPRING_URL}/internal/ai/complete",
            json=payload,
            headers=headers(),
            timeout=30
        )
        response.raise_for_status()

        log.info("영상·자막 DB 등록 완료: %s", key)

def scan():
    paginator = s3.get_paginator("list_objects_v2")

    for page in paginator.paginate(
        Bucket=BUCKET,
        Prefix=f"{VIDEO_PREFIX}/"
    ):
        for obj in page.get("Contents", []):
            if stop_event.is_set():
                return

            key = obj["Key"]

            if not key.lower().endswith(".mp4"):
                continue

            relative = key[len(VIDEO_PREFIX) + 1:]
            parts = PurePosixPath(relative).parts

            if (
                not relative
                or any(p in ("", ".", "..") for p in parts)
            ):
                continue

            source_id = fingerprint(key, obj)

            try:
                if completed(key, source_id):
                    continue

                process_video(key, source_id)

            except Exception:
                log.exception("AI 처리 실패: %s", key)

def scan_loop():
    while not stop_event.is_set():
        try:
            scan()
        except Exception:
            log.exception("S3 목록 조회 실패")

        stop_event.wait(SCAN_INTERVAL)

@asynccontextmanager
async def lifespan(app: FastAPI):
    stop_event.clear()
    thread = threading.Thread(
        target=scan_loop,
        daemon=True,
        name="ott-ai-scanner"
    )
    thread.start()
    log.info("AI 자동 처리 서비스 시작")

    yield

    stop_event.set()
    thread.join(timeout=5)

app = FastAPI(
    title="OTTer AI Service",
    lifespan=lifespan,
    docs_url=None,
    redoc_url=None,
    openapi_url=None
)

@app.get("/health")
def health():
    return {"status": "ok"}

