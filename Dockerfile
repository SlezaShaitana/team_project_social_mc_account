FROM maven:3.8.6-openjdk-17-slim AS build
WORKDIR /home/app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17.0.2-jdk-slim-buster
LABEL maintainer="tishin"

# Копирование файла без использования wildcard
COPY --from=build /home/app/target/myapp.jar /mc-account.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/mc-account.jar"]