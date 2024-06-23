# Use the official OpenJDK image for Java 21
FROM openjdk:21

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY deployment/storage_service-0.0.1-SNAPSHOT.jar /app/my-application.jar

# Command to run the JAR file
CMD ["java", "-jar", "/app/my-application.jar"]