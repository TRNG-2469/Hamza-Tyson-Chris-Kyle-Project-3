# STAGE 1: Build the application jar
FROM maven:3.9-eclipse-temurin-21 AS build

# Set the working directory inside the build container
WORKDIR /app

# Copy the pom.xml config first to cache dependency downloads
COPY pom.xml .

# Download dependencies offline (highly cached step)
RUN mvn dependency:go-offline -B

# Copy the actual application source code
COPY src ./src

# Compile and package the executable JAR file
RUN mvn package -DskipTests

# STAGE 2: Lightweight JRE Runtime environment
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy only the compiled JAR file from the builder stage
COPY --from=build /app/target/*.jar app.jar

ENV DB_URL="jdbc:postgresql://postgres:5432/ProjIter2"
ENV DB_USER=postgres
ENV DB_PASS="password"
ENV JWT_SECRET=0d0e4ce6a7127330f235c0983ac87aec7c569b9676da7e9418128af5c06306f1

# Document container port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]