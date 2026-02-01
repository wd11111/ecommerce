# Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -pl microservices/config-server -am -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /app
COPY --from=builder /app/microservices/config-server/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
