import os
import hmac
import subprocess
import sys
from pathlib import Path

from fastapi import FastAPI, BackgroundTasks, Header, HTTPException
from pydantic import BaseModel

app = FastAPI(title="OTTer AI Service")

class VideoRequest(BaseModel):
    video_key: str

@app.get("/health")
def health():
    return {"status": "ok"}

def run_ai(video_key: str):
    subprocess.run(
        [
            sys.executable,
            "/app/process_s3_video.py",
            video_key,
            "--work-root", "/work",
            "--upload"
        ],
        check=True,
        timeout=3600
    )

@app.post("/process", status_code=202)
def process_video(
    request: VideoRequest,
    background_tasks: BackgroundTasks,
    x_ai_token: str = Header(default="")
):
    expected = os.environ["AI_API_TOKEN"]

    if not hmac.compare_digest(x_ai_token, expected):
        raise HTTPException(status_code=401)

    key = request.video_key

    if (
        not key.startswith("test/videos/")
        or not key.endswith(".mp4")
        or ".." in Path(key).parts
    ):
        raise HTTPException(status_code=400)

    background_tasks.add_task(run_ai, key)

    return {
        "status": "accepted",
        "video_key": key
    }
