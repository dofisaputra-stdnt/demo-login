FROM openjdk:17-slim

# Copy .jar dan properties
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY target/application.properties /application.properties

# Run dengan file properties eksternal
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.config.location=file:/application.properties"]
