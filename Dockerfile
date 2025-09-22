# Simple Spring Boot Dockerfile
FROM eclipse-temurin:21-jre-alpine

# Working directory
WORKDIR /app

# Copy the jar file (build it first with: ./gradlew bootJar)
COPY build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
