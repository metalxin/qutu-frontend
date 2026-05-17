import { defineConfig, loadEnv } from "vite";
import uni from "@dcloudio/vite-plugin-uni";

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd());

  return {
    plugins: [uni()],
    server: {
      proxy: {
        "/admin": {
          target: env.VITE_PROXY_TARGET || "http://localhost:9999",
          changeOrigin: true,
          ws: true,
        },
      },
    },
  };
});
