FROM openjdk:20-jdk-slim

WORKDIR /app

COPY . /app

# Make Gradle wrapper executable
RUN chmod +x ./gradlew

# Build the Kotlin application
RUN ./gradlew build --no-daemon

# Expose the port
EXPOSE 8080

# Start the Kotlin server
CMD ["java", "-jar", "build/libs/MasterChildServer-1.0-SNAPSHOT.jar"]