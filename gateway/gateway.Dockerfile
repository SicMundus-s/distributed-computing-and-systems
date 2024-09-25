FROM openjdk:17-jdk-slim
WORKDIR /app
COPY ./build/libs/gateway-*.jar /app/gateway.jar
EXPOSE 8765
CMD ["java", "-jar", "/app/gateway.jar"]