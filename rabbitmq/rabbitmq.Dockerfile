FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./rabbitmq/build/libs/rabbitmq-*.jar /app/rabbitmq-service.jar

EXPOSE 8083

CMD ["java", "-jar", "/app/rabbitmq-service.jar"]