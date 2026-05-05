FROM eclipse-temurin:21-jdk-alpine
ADD target/Real-Time-Polling-API-0.0.1-SNAPSHOT.jar Real-Time-Polling-API-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","Real-Time-Polling-API-0.0.1-SNAPSHOT.jar"]