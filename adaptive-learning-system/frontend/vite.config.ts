import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'


export default defineConfig({
  plugins: [react()],
  
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./src/test/vitest.setup.ts'],
    include: ['src/**/*.{test,spec}.{ts,tsx}'],
    reporter: ['verbose'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
    },
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks: (id: string) => {
          
          if (id.includes('node_modules/react/') || id.includes('node_modules/react-dom/')) {
            return 'react-vendor';
          }
          
          if (id.includes('node_modules/react-router')) {
            return 'react-router';
          }
          
          if (id.includes('node_modules/@tanstack/react-query')) {
            return 'react-query';
          }
          
          if (id.includes('node_modules/framer-motion')) {
            return 'framer-motion';
          }
          
          if (id.includes('node_modules/lucide-react')) {
            return 'lucide-react';
          }
        },
      },
    },
    chunkSizeWarningLimit: 1000,
  },
})


