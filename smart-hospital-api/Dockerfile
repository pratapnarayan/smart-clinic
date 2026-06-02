# ── Stage 1: Build ────────────────────────────────────────────────────────────
# maven:3.9-eclipse-temurin-21 is multi-arch (linux/amd64 + linux/arm64).
# Works on both your local machine and the Oracle Cloud ARM VM without changes.
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

# Copy pom.xml first so the dependency layer is cached independently.
# Re-downloaded only when pom.xml changes, not on every source change.
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Non-root user — never run the JVM as root inside a container
RUN addgroup -S smarthospital && adduser -S smarthospital -G smarthospital

# Log directory used by application-docker.yml
RUN mkdir -p /var/log/smarthospital \
 && chown smarthospital:smarthospital /var/log/smarthospital

WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
RUN chown smarthospital:smarthospital app.jar

USER smarthospital

# -XX:+UseContainerSupport  → JVM respects Docker memory limits (not host RAM)
# -Djava.security.egd       → faster SecureRandom startup on Linux containers
ENV JAVA_OPTS="-Xms256m -Xmx768m -XX:+UseG1GC -XX:+UseContainerSupport \
-Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
