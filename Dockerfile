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
# Install timezone data and set timezone to UTC
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone
# Copy the JAR file
COPY --from=build /app/target/*.jar app.jar
# Create directory for logs
RUN mkdir -p /app/logs
# Install necessary tools
RUN apk add --no-cache netcat-openbsd curl

# Copy wait-for-it script
COPY wait-for-it.sh /
RUN chmod +x /wait-for-it.sh

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV TZ=UTC
ENV JAVA_OPTS="-Duser.timezone=UTC"

EXPOSE 8080
ENTRYPOINT ["/bin/sh", "-c", "/wait-for-it.sh db:5432 -t 60 -- java -Dspring.main.allow-bean-definition-overriding=true -Duser.timezone=UTC ${JAVA_OPTS} -jar app.jar"]