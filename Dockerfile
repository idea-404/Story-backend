FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy JAR file
COPY build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Run
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
