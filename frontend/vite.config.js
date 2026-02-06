import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react' // Lägg till "/plugin-" här!

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
})