# -----------------------------------------------------
# STAGE 1: Build the Application
# -----------------------------------------------------
# UPDATED: Using Java 21 to match your project version
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper and configuration
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Grant execution permission to the wrapper
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the actual source code
COPY src ./src

# Build the JAR
RUN ./mvnw clean package -DskipTests

# -----------------------------------------------------
# STAGE 2: Run the Application
# -----------------------------------------------------
# UPDATED: Using Java 21 JRE for runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a directory for the database
RUN mkdir -p /app/data

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]