# -- Stage 1: Build the Spring Boot JAR --------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper and pom first (layer-cache friendly)
COPY email-writer-sb/email-writer-sb/mvnw       ./mvnw
COPY email-writer-sb/email-writer-sb/.mvn       ./.mvn
COPY email-writer-sb/email-writer-sb/pom.xml    ./pom.xml

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (cached layer unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source and build (skip tests for faster image builds)
COPY email-writer-sb/email-writer-sb/src ./src
RUN ./mvnw package -DskipTests -B

# -- Stage 2: Lightweight runtime image --------------------------------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the fat JAR produced in the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
