import subprocess
import sys
from pathlib import Path


def split_audio(video_path: Path, output_dir: Path) -> None:
    output_dir.mkdir(parents=True, exist_ok=False)

    subprocess.run(
        [
            "ffmpeg", "-hide_banner", "-loglevel", "error",
            "-i", str(video_path),
            "-map", "0:a:0",
            "-ac", "1", "-ar", "16000", "-c:a", "pcm_s16le",
            "-f", "segment", "-segment_time", "60",
            "-segment_list", str(output_dir / "segments.csv"),
            "-segment_list_type", "csv",
            "-reset_timestamps", "1",
            str(output_dir / "part-%03d.wav"),
        ],
        check=True,
    )

    for part in sorted(output_dir.glob("part-*.wav")):
        print(part.name)


if __name__ == "__main__":
    split_audio(Path(sys.argv[1]), Path(sys.argv[2]))