import argparse
import json
import subprocess
import sys
from pathlib import Path

SCRIPT_DIR = Path(__file__).resolve().parent


def run_step(script_name: str, *paths: Path) -> None:
    # 현재 가상환경의 Python으로 기존 스크립트 실행
    print(f"\n실행: {script_name}", flush=True)
    subprocess.run(
        [sys.executable, str(SCRIPT_DIR / script_name), *map(str, paths)],
        check=True,
    )


def video_info(video: Path) -> dict:
    # 다른 원본 영상의 작업 결과 재사용 방지
    stat = video.stat()
    return {
        "path": str(video),
        "size": stat.st_size,
        "mtime_ns": stat.st_mtime_ns,
    }


def has_outputs(folder: Path, *names: str) -> bool:
    return all(
        (folder / name).is_file() and (folder / name).stat().st_size > 0
        for name in names
    )


def main() -> None:
    parser = argparse.ArgumentParser(description="영상 자막 및 메타데이터 생성")
    parser.add_argument("video", type=Path, help="원본 영상 경로")
    parser.add_argument("work_dir", type=Path, help="작업 폴더 경로")

    mode = parser.add_mutually_exclusive_group()
    mode.add_argument("--resume", action="store_true", help="기존 작업 이어서 실행")
    mode.add_argument("--split-only", action="store_true", help="오디오 분할까지만 실행")

    args = parser.parse_args()
    video = args.video.resolve()
    work_dir = args.work_dir.resolve()
    audio_dir = work_dir / "audio"
    source_file = work_dir / "source-video.json"

    if not video.is_file():
        raise SystemExit(f"영상 파일을 찾을 수 없습니다: {video}")

    try:
        if args.resume:
            if not source_file.is_file() or not has_outputs(audio_dir, "segments.csv"):
                raise SystemExit(
                    "오디오 분할 완료 기록이 없습니다. 새 작업 폴더에서 시작해 주세요."
                )

            saved_info = json.loads(source_file.read_text(encoding="utf-8"))
            if saved_info != video_info(video):
                raise SystemExit("원본 영상이 이전 실행과 다릅니다. 새 작업 폴더를 사용해 주세요.")

            print(f"기존 오디오 재사용: {audio_dir}", flush=True)
        else:
            if work_dir.exists():
                raise SystemExit(
                    f"이미 있는 작업 폴더입니다: {work_dir}\n"
                    "이어서 실행하려면 --resume을 붙여 주세요."
                )

            work_dir.mkdir(parents=True)
            run_step("split_audio.py", video, audio_dir)

            # 오디오 분할이 성공한 뒤에만 재실행용 기록 저장
            source_file.write_text(
                json.dumps(video_info(video), ensure_ascii=False, indent=2),
                encoding="utf-8",
            )

        if args.split_only:
            print(f"\n오디오 분할 완료: {audio_dir}")
            return

        subtitles_ready = has_outputs(audio_dir, "en.vtt", "ko.vtt")
        if subtitles_ready:
            print("기존 영어·한국어 자막 재사용", flush=True)
        else:
            run_step("generate_subtitles.py", audio_dir)

        if not has_outputs(audio_dir, "en.vtt", "ko.vtt"):
            raise SystemExit("자막 생성이 끝났지만 영어·한국어 VTT를 모두 찾지 못했습니다.")

        if subtitles_ready and has_outputs(audio_dir, "thumbnail.jpg", "metadata.json"):
            print("기존 썸네일·메타데이터 재사용", flush=True)
        else:
            run_step("generate_metadata.py", video, audio_dir)

        if not has_outputs(audio_dir, "thumbnail.jpg", "metadata.json"):
            raise SystemExit("썸네일과 메타데이터 파일을 모두 찾지 못했습니다.")

    except subprocess.CalledProcessError as error:
        script_name = Path(error.cmd[1]).name
        if script_name == "split_audio.py":
            raise SystemExit(f"{script_name} 실패. 오류를 확인한 뒤 새 작업 폴더에서 시작해 주세요.")
        raise SystemExit(f"{script_name} 실패. 오류를 확인한 뒤 같은 경로에 --resume을 붙여 재실행해 주세요.")

    print(f"\n처리 완료: {audio_dir}")


if __name__ == "__main__":
    main()