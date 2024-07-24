FROM maven:3.6.3-openjdk-17-slim AS build
WORKDIR /home/app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM openjdk:17.0.2-jdk-slim-buster
LABEL maintainer="tishin"

ARG JAR_FILE=/home/app/target/*.jar
COPY --from=build ${JAR_FILE} /mc-account.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/mc-account.jar"]
