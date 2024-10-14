FROM amazoncorretto:21-alpine

COPY build/libs/recipe-sharing.jar app.jar

ENTRYPOINT [ "sh", "-c", "java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
