# Build context is the repository root (see build.sh), so every module's
# target/*.jar is reachable here. This packages all of them into a single
# runtime image, each running as its own process, all ports exposed.
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY echo-printer/target/echo-printer-*.jar /app/echo-printer.jar
COPY char-printer/target/char-printer-*.jar /app/char-printer.jar
COPY second-character-printer/target/second-character-printer-*.jar /app/second-character-printer.jar
COPY random-printer/target/random-printer-*.jar /app/random-printer.jar
COPY next-char-printer/target/next-char-printer-*.jar /app/next-char-printer.jar
COPY mcp-registry/target/mcp-registry-*.jar /app/mcp-registry.jar

COPY build/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

EXPOSE 8081 8082 8083 8084 8085 8090

ENTRYPOINT ["/app/entrypoint.sh"]
