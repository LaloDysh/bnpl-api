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
# Install required utilities
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone
# Copy the JAR file
COPY --from=build /app/target/*.jar app.jar
# Create directory for logs
RUN mkdir -p /app/logs
# Add entrypoint script
RUN apk add --no-cache netcat-openbsd curl
# Copy wait-for-it script
COPY wait-for-it.sh /
RUN chmod +x /wait-for-it.sh

# Set environment variables with defaults that can be overridden
ENV SERVER_PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
ENV TZ=UTC
ENV JAVA_OPTS="-Duser.timezone=UTC"

# Expose the configured port
EXPOSE ${SERVER_PORT}
ENTRYPOINT ["/bin/sh", "-c", "/wait-for-it.sh db:5432 -t 60 -- java -Dspring.main.allow-bean-definition-overriding=true -Duser.timezone=UTC ${JAVA_OPTS} -jar app.jar"]