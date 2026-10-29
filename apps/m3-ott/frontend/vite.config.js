import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import fs from 'node:fs'
import path from 'node:path'

// public/media 의 로컬 데모 MP4가 dist(AWS 배포 산출물)에 섞이지 않도록 build 후 제거한다.
// 실제 영상은 S3/CloudFront 경로에서 재생 API가 내려준다.
function stripLocalDemoVideo() {
  return {
    name: 'strip-local-demo-video',
    apply: 'build',
    closeBundle() {
      const dir = path.resolve('dist', 'media')
      if (!fs.existsSync(dir)) return
      for (const f of fs.readdirSync(dir)) if (/\.(mp4|mov|mkv)$/i.test(f)) fs.rmSync(path.join(dir, f))
    },
  }
}

export default defineConfig({
  plugins: [react(), stripLocalDemoVideo()],
  server: {
    port: 5174,
    strictPort: true,
    // 백엔드(기본 8081)로 /api 프록시: 같은 출처로 보이므로 개발 중 CORS·쿠키 설정이 필요 없다.
    proxy: { '/api': { target: process.env.VITE_PROXY_TARGET || 'http://localhost:8081', changeOrigin: true } },
  },
})
