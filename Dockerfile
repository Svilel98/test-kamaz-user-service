FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/user-service-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=deploy", "app.jar"]