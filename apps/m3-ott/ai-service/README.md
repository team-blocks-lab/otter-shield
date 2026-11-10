# OTT | AI 영상 처리

Gemini로 영어 대사를 받아쓰고 한국어로 번역한다. FFmpeg와 FFprobe로 썸네일과 영상 길이를 추출하고, 영어 자막을 바탕으로 한국어 줄거리를 생성한다.

## 설치

Python 3.12 환경에서 검증. FFmpeg와 FFprobe를 설치하고 터미널에서 실행할 수 있도록 설정한다. 아래 명령은 `apps/m3-ott/ai-service`에서 실행한다.

```bash
python -m pip install -r requirements.txt
```

## 환경변수

실행 환경에 설정하거나 스크립트와 같은 폴더의 `.env`에 입력한다. 실제 키가 담긴 `.env`는 Git에 올리지 않는다.

```dotenv
GEMINI_API_KEY=발급받은_키
GEMINI_MODEL=gemini-3.1-flash-lite
OTT_MEDIA_BUCKET=otter-shield-m3-media
OTT_VIDEO_PREFIX=test/videos
OTT_SUBTITLE_PREFIX=test/subtitles/ai-check
```

S3 연동에는 실행 주체의 원본 읽기(`s3:GetObject`) 권한이 필요하다. 자막까지 올릴 때는 지정한 자막 경로의 쓰기(`s3:PutObject`) 권한도 필요하다.

## 실행

로컬 영상 처리:

```bash
python process_video.py ./sample.mp4 ./video-work/local-sample
```

분할이 끝난 뒤 중단된 경우 같은 명령에 `--resume`을 붙인다. 결과는 작업 폴더의 `audio/`에 저장된다.

S3 원본으로 처리:

```bash
python process_s3_video.py test/videos/elephants-dream.mp4 --work-root ./video-work
```

위 명령은 원본을 내려받아 로컬에서 처리하고 업로드 대상만 출력한다. 같은 명령 끝에 `--upload`를 붙이면 VTT 두 파일을 S3에 올린다. 중단되면 같은 작업 경로로 재실행해 완료한 구간을 재사용한다. 원본 경로의 하위 폴더는 자막 경로에도 유지된다.

영어 대사가 감지되지 않으면 줄거리는 `null`로 저장하고 빈 자막은 업로드하지 않는다. 썸네일과 메타데이터는 로컬에 남는다. DB 자동 감지와 자막 경로 등록은 이 실행 범위에 포함되지 않는다.

[앱 환경 설정](../README.md) · [앱 소개·UI](../../README.md)
