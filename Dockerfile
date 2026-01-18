# ===== BUILD =====
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY src ./src

RUN chmod +x gradlew && sed -i 's/\r$//' gradlew
RUN ./gradlew bootJar --no-daemon

# ===== RUNTIME (DISTROLESS) =====
FROM gcr.io/distroless/java25-debian13
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]