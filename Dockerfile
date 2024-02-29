FROM openjdk:17-alpine as builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=builder /app/target/bike-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]