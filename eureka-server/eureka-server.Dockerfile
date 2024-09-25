FROM openjdk:17-jdk-slim
WORKDIR /app
COPY ./build/libs/eureka-server-*.jar /app/eureka-server.jar
EXPOSE 8761
CMD ["java", "-jar", "/app/eureka-server.jar"]