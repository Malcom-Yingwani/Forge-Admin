FROM maven:3.9.16-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml ./
COPY src ./src
# Optional BuildKit secrets support the cloud egress proxy without persisting credentials.
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=secret,id=maven_settings \
    --mount=type=secret,id=java_truststore \
    set -eu; \
    if [ -f /run/secrets/java_truststore ]; then export MAVEN_OPTS="-Djavax.net.ssl.trustStore=/run/secrets/java_truststore"; fi; \
    set --; \
    if [ -f /run/secrets/maven_settings ]; then set -- -s /run/secrets/maven_settings; fi; \
    mvn "$@" --batch-mode --no-transfer-progress -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build --chown=10001:10001 /build/target/*-SNAPSHOT.jar /app/app.jar
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75" SERVER_ADDRESS=0.0.0.0
USER 10001:10001
EXPOSE 8081
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
