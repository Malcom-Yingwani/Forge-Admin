# Local Development

## Prerequisites

Java 21, Maven 3.9+ (or `./mvnw`), Node 22+, Docker.

## Run

```bash
docker compose up -d                 # postgres:5433, minio:9000/9001, mailpit:8025

cd api
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev     # http://localhost:8081
# Swagger: http://localhost:8081/swagger-ui.html
# The dev profile seeds admin@heritage.local / change-me (local only)

cd admin
cp .env.example .env                 # VITE_API_URL=http://localhost:8081
npm install && npm run dev           # http://localhost:5174
```

## With the Heritage Website

Run [heritage-website](https://github.com/Malcom-Yingwani/heritage-website) as well, with the same `SYNC_SHARED_SECRET`, and set `HERITAGE_SYNC_URL=http://localhost:8080/internal/sync`. Publish a sermon here and it appears on `localhost:5173`. Stop heritage-api, publish again, and watch the **Sync** screen: the event stays pending until Heritage comes back, then delivers.

## Checks before a PR

```bash
cd api && ./mvnw verify
cd admin && npm run lint && npm run build
```
