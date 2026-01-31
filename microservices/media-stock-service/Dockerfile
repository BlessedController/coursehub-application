FROM eclipse-temurin:21-jre

RUN apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY build/libs/*.jar app.jar

EXPOSE 8083
CMD ["java", "-jar", "app.jar"]
