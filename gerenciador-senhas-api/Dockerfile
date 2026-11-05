# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy project files
COPY . .

# Ensure the Maven wrapper is executable
RUN chmod +x mvnw

# Build the Spring Boot jar (skip tests for faster deploy builds)
RUN ./mvnw -q -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar
COPY --from=build /app/target/*.jar /app/app.jar

# Render injects the PORT environment variable
EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
