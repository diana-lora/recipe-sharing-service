FROM amazoncorretto:21-alpine

COPY web-service/build/libs/web-service.jar app.jar

ENTRYPOINT [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
