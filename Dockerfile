# Use Maven image for building
FROM maven:3.9.4-eclipse-temurin-17-alpine AS build

# Set working directory
WORKDIR /app

# Copy pom.xml from backend
COPY backend/pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code from backend
COPY backend/src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/fraud-detection-1.0.0.jar app.jar

# Create logs directory
RUN mkdir -p logs

# Expose port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# Run the application
CMD ["java", "-jar", "app.jar"]