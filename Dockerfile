# 빌드 스테이지
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY .. .
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]