import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';
// import basicSsl from '@vitejs/plugin-basic-ssl';
import tailwindcss from '@tailwindcss/vite';

// https://vitejs.dev/config/
export default defineConfig({
  // plugins: [react(), basicSsl(), tailwindcss()],
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  //
  server: {
    port: 5173,
    proxy: {
      // 代理以 /api 开头的请求
      '/api': {
        target: 'http://13.211.212.24',
        changeOrigin: true,
      },
    },
  },

  css: {
    preprocessorOptions: {
      less: {
        javascriptEnabled: true,
      },
    },
  },
});
