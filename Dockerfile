FROM openjdk:21-jdk-slim
COPY build/libs/fitu-0.0.1-SNAPSHOT.jar fitu.jar
ENTRYPOINT ["java", "-jar", "fitu.jar"]