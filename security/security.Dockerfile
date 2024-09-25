FROM openjdk:17-jdk-slim
WORKDIR /app
COPY ./security/build/libs/security-*.jar /app/security.jar
EXPOSE 8081
CMD ["java", "-jar", "/app/security.jar"]