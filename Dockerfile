# 1단계: 빌드 단계 (Gradle Wrapper를 사용하여 애플리케이션 빌드)
FROM --platform=linux/amd64 gradle:7.5.1-jdk17 AS builder
WORKDIR /home/gradle/project

# 소스 코드 전체를 복사 (gradlew, build.gradle, settings.gradle, src/ 등)
COPY --chown=gradle:gradle . .

# Gradle Wrapper 실행 권한 부여 (실행 권한이 없을 경우를 대비)
RUN chmod +x gradlew

# Gradle을 사용하여 프로젝트 빌드 (JAR 파일은 build/libs 폴더에 생성됨)
RUN ./gradlew clean build --no-daemon

# 2단계: 실행 단계 (빌드 결과물 실행을 위한 환경)
FROM --platform=linux/amd64 openjdk:17-jdk-slim
WORKDIR /app

# 로그 디렉토리 미리 생성
RUN mkdir -p /app/logs

# 빌드 단계에서 생성된 JAR 파일 복사 (파일명이 프로젝트에 따라 달라질 수 있으므로 와일드카드 사용)
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# 컨테이너에서 사용할 포트 (필요 시 변경)
EXPOSE 9000

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
