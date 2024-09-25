FROM openjdk:17-jdk-slim
WORKDIR /app
COPY ./simple-english-data/build/libs/simple-english-data-*.jar /app/simple-english-data.jar
EXPOSE 8082
CMD ["java", "-jar", "/app/simple-english-data.jar"]