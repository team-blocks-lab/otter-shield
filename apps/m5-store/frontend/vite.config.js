import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// 개발 서버에서 /api(와 상품 이미지 /images)를 로컬 Spring Boot(8082)로 프록시합니다.
const target = 'http://localhost:8082'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': { target, changeOrigin: true },
      '/images': { target, changeOrigin: true },
    },
  },
})
