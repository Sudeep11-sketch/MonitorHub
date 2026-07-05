# Step 1: Use an official light JDK image to compile the source code
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy Maven files and source directories
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Fix line endings for Linux and build the production jar file
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Step 2: Run using a bare minimum JRE environment to keep things small and safe
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]