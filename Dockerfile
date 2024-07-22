FROM maven:3.8.4-openjdk-8 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package

FROM openjdk:8-jre-slim

WORKDIR /app

COPY --from=build /app/target/helpmeiud-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8084

ENTRYPOINT ["java","-Dspring.profiles.active=local","-jar", "app.jar"]