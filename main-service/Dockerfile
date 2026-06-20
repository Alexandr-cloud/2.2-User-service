FROM amazoncorretto:17-alpine

WORKDIR /app

COPY target/main-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]