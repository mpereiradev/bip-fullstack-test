# ── Stage 1: build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace

# Cache dependency layer — re-runs only when pom files change
COPY pom.xml .
COPY ejb/pom.xml ejb/
COPY api/pom.xml api/
RUN mvn -B -N install -q \
 && mvn -B -pl ejb dependency:resolve --fail-never -q 2>/dev/null || true

# Build
COPY ejb/src ejb/src
COPY api/src api/src
RUN mvn -B -N install -q \
 && mvn -B -f ejb/pom.xml install -DskipTests -q \
 && mvn -B -f api/pom.xml package -DskipTests -q

# ── Stage 2: runtime (JRE only — sem JDK) ─────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/api/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
