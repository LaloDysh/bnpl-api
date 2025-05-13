FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml .

RUN mvn dependency:go-offline -B
COPY src ./src

RUN mvn package -DskipTests


FROM amazoncorretto:17-alpine
WORKDIR /app

RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone

COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p /app/logs

RUN apk add --no-cache netcat-openbsd curl

COPY wait-for-it.sh /
RUN chmod +x /wait-for-it.sh

ENV SERVER_PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
ENV TZ=UTC
ENV JAVA_OPTS="-Duser.timezone=UTC"

EXPOSE ${SERVER_PORT}
ENTRYPOINT ["/bin/sh", "-c", "/wait-for-it.sh db:5432 -t 60 -- java -Dspring.main.allow-bean-definition-overriding=true -Duser.timezone=UTC ${JAVA_OPTS} -jar app.jar"]