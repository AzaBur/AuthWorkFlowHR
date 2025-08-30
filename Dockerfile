FROM openjdk:21-jdk-slim
LABEL authors="aza"

WORKDIR /app

COPY target/AuthWorkFlowHR-*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]