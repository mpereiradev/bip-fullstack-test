# ── Stage 1: build ────────────────────────────────────────────────────────────
FROM node:24-alpine AS build
WORKDIR /app

# Cache dependency layer
COPY package*.json ./
RUN npm install --prefer-offline

COPY . .
RUN npm run build -- --configuration production

# ── Stage 2: serve com Nginx ───────────────────────────────────────────────────
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/beneficio-web/browser /usr/share/nginx/html
EXPOSE 80
