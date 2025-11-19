FROM eclipse-temurin:17-jdk-alpine

ARG JAR_FILE=build/libs/*.jar

WORKDIR /app

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=local", "-Duser.timezone=Asia/Seoul", "-jar", "/app/app.jar"]