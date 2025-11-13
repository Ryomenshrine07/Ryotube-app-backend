# Stage 1: build with maven
FROM maven:3.9.0-eclipse-temurin-17 AS builder
WORKDIR /app

# copy pom
COPY pom.xml .

# download dependencies (speeds up rebuilds)
RUN mvn -B dependency:go-offline

# copy source and build jar
COPY src ./src
RUN mvn -B -DskipTests package

# Stage 2: runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy jar produced in builder stage
COPY --from=builder /app/target/*.jar /app/app.jar

# Render will supply PORT. Default 8080 for local.
ENV SERVER_PORT ${PORT:-8080}
ENV JAVA_OPTIONS="-Djava.security.egd=file:/dev/./urandom -XX:+UseContainerSupport"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTIONS -Dserver.port=${SERVER_PORT} -jar /app/app.jar"]
