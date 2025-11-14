# Use Maven image for building
FROM maven:3.9.4-eclipse-temurin-17-alpine AS build

# Set working directory
WORKDIR /build

# Copy backend files
COPY backend/pom.xml .
COPY backend/src ./src

# Build the application
RUN mvn clean package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy jar from build stage
COPY --from=build /build/target/*.jar app.jar

# Create logs directory
RUN mkdir -p logs

# Expose port
EXPOSE 8080

# Set JVM options for container
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]