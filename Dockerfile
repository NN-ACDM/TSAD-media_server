# Use Maven with Java 17 as build environment
FROM maven:3.9-eclipse-temurin-17

# Set working directory early
WORKDIR /app

# Run as root (to allow installing docker CLI if needed)
USER root

# Install Docker CLI (optional — can be removed if unused)
RUN apt-get update && apt-get install -y docker.io && rm -rf /var/lib/apt/lists/*

# Create config directory (if you mount external config, this ensures path exists)
RUN mkdir -p /app/config

# Copy the JAR into image (assuming you've built with mvn before this step)
COPY target/*.jar app.jar

# Run the JAR with external config support
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.additional-location=optional:file:./config/"]
