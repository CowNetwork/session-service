FROM maven:3.8.1-jdk-11

WORKDIR /usr/src/app
COPY . /usr/src/app
RUN mvn clean package

FROM openjdk:11

ENV PORT=5816
ENV POSTGRES_HOST=127.0.0.1:5432
ENV POSTGRES_DB=postgres
ENV POSTGRES_SCHEMA=public
ENV POSTGRES_USERNAME=postgres
ENV POSTGRES_PASSWORD=postgres
ENV USER_SERVICE_HOSTNAME=127.0.0.1
ENV USER_SERVICE_PORT=5816

WORKDIR /app
COPY --from=0 /usr/src/app/target/session-service.jar /app/session-service.jar
CMD ["java", "-jar", "/app/session-service.jar"]