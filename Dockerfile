# Use an older base image for testing purposes
FROM eclipse-temurin:11-jre

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY build/libs/task-management-api-*.jar app.jar

# Copy configuration
COPY src/main/resources/config.yml config.yml

# Create logs directory
RUN mkdir -p /app/logs

# Expose port
EXPOSE 8080

# Set JVM options for outdated configurations
ENV JAVA_OPTS="-Xmx1024m -Xms512m -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar server config.yml"]