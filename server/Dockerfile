# syntax=docker/dockerfile:1

FROM eclipse-temurin:18-jdk-jammy

WORKDIR /app

EXPOSE 1471/udp

COPY target/server-1.0-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]