/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  // Prevent Tailwind reset from conflicting with Ant Design base styles
  corePlugins: {
    preflight: false,
  },
  theme: {
    extend: {
      colors: {
        primary: "#1677ff",
        success: "#52c41a",
        warning: "#faad14",
        danger:  "#ff4d4f",
      },
    },
  },
  plugins: [],
}
