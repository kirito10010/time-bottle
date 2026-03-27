import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/Usersimg': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/default-avatar.svg': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
