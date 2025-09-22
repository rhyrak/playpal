FROM gradle:jdk21 AS development
WORKDIR /app
COPY build.gradle .
COPY src ./src
COPY .env .
RUN gradle test

FROM gradle:jdk21 AS build
WORKDIR /app
COPY build.gradle .
COPY src ./src
RUN gradle bootJar

FROM openjdk:24-slim AS production
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]