FROM eclipse-temurin:21-jre-alpine

COPY target/person-api-*-SNAPSHOT.jar /app.jar

ENTRYPOINT java -jar app.jar
