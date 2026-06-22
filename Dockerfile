FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY build/libs/eat-ssu.jar app.jar

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "app.jar"]
