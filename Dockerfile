# ─────────────────────────────────────────────
# Stage 1: Build & Test
# ─────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-11 AS builder

LABEL maintainer="API Test Automation Team"
LABEL description="REST Assured API Testing with Allure Reports"

WORKDIR /app

# Copy dependency definitions first for Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Run tests and package (skip if you only want to build)
RUN mvn test -B --no-transfer-progress || true

# Generate Allure report
RUN mvn allure:report -B --no-transfer-progress || true

# ─────────────────────────────────────────────
# Stage 2: Report Server (Nginx)
# ─────────────────────────────────────────────
FROM nginx:1.25-alpine AS report-server

LABEL description="Allure Report Server"

# Copy generated Allure report to Nginx html directory
COPY --from=builder /app/target/allure-report /usr/share/nginx/html

# Expose HTTP port
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
