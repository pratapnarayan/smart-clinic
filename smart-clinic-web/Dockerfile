# ── Stage 1: Build React app ──────────────────────────────────────────────────
FROM node:22-alpine AS builder
WORKDIR /app

# Copy lockfile first so npm ci layer is cached unless dependencies change
COPY package.json package-lock.json ./
# npm ci fails on Windows-generated lockfiles in Linux containers because
# platform-specific native binaries (@emnapi/core etc.) are missing for musl.
# npm install resolves the correct Linux/musl variants at build time.
RUN npm install --prefer-offline

COPY . .
RUN npm run build

# ── Stage 2: Serve with Nginx ─────────────────────────────────────────────────
# nginx:alpine is multi-arch (linux/amd64 + linux/arm64)
FROM nginx:1.27-alpine AS runtime

# gettext provides envsubst — used to inject API_HOST at container startup
RUN apk add --no-cache gettext

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf.template /etc/nginx/conf.d/default.conf.template

# Defaults for local Docker Compose. Railway overrides via env vars.
ENV API_HOST=api:8080
ENV API_SCHEME=http
ENV PORT=80

EXPOSE 80

# Substitute ${API_HOST}, ${API_SCHEME}, ${PORT} at startup, then launch nginx.
CMD ["/bin/sh", "-c", "envsubst '${API_HOST} ${API_SCHEME} ${PORT}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
