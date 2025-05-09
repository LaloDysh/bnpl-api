# Dockerfile
FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies separately to leverage Docker cache
RUN mvn dependency:go-offline -B
COPY src ./src
# Build the application
RUN mvn package -DskipTests

# Create a smaller runtime image
FROM amazoncorretto:17-alpine
WORKDIR /app
# Copy the JAR file
COPY --from=build /app/target/*.jar app.jar
# Create directory for logs
RUN mkdir -p /app/logs
# Install netcat for health check
RUN apk add --no-cache netcat-openbsd
# Add entrypoint script
COPY docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh
# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080
ENTRYPOINT ["/docker-entrypoint.sh"]