# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml from backend
COPY backend/mvnw .
COPY backend/.mvn .mvn
COPY backend/pom.xml .

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code from backend
COPY backend/src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create logs directory
RUN mkdir -p logs

# Expose port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# Run the application
CMD ["java", "-jar", "target/fraud-detection-1.0.0.jar"]