
FROM python:3.12-slim

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

WORKDIR /app

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
      ffmpeg ca-certificates && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd -g 10001 ai && \
    useradd -u 10001 -g 10001 \
      --no-create-home ai && \
    mkdir /work && \
    chown 10001:10001 /work

COPY requirements.txt .

RUN pip install --no-cache-dir \
    -r requirements.txt

COPY *.py ./

USER 10001:10001

EXPOSE 8000

CMD ["uvicorn", "main:app", \
     "--host", "0.0.0.0", \
     "--port", "8000", \
     "--workers", "1"]

