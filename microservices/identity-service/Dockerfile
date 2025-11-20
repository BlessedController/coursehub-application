FROM gradle:8.9-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8081
CMD ["java", "-jar", "app.jar"]