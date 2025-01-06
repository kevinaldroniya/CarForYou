FROM amazoncorretto:17

WORKDIR /app

COPY build/libs/CarForYou-0.0.1-SNAPSHOT.jar app.jar

COPY src/main/resources/serviceAccountKey.json /app/serviceAccountKey.json

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080