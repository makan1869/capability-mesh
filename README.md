# capability-mesh

A Maven multi-module project. The root `pom.xml` is a shared parent (Java 25,
Spring Boot 4.1.1 BOM, Spring AI 2.0.1 BOM) with no code of its own; each
module under it is an independently runnable Spring Boot app. Most expose a
remote MCP server over the **Streamable HTTP** transport; `mcp-registry` is
a plain REST API that lets clients discover the others.

## Modules

- [`echo-printer`](echo-printer) — exposes a single `echo` tool that returns
  exactly the character it is given. (port 8081)
- [`char-printer`](char-printer) — exposes 26 no-input tools, `a` through
  `z`, that always return the matching uppercase letter. (port 8082)
- [`second-character-printer`](second-character-printer) — exposes a single
  `second_character` tool that returns the second character of a given
  string. (port 8083)
- [`random-printer`](random-printer) — exposes a single `random_character`
  tool that ignores its string input and returns a random letter. (port 8084)
- [`next-char-printer`](next-char-printer) — exposes a single
  `next_character` tool that returns the next letter of the alphabet,
  cycling from `Z` back to `A`. (port 8085)
- [`mcp-registry`](mcp-registry) — read-only implementation of the MCP
  [generic registry API](https://github.com/modelcontextprotocol/registry),
  seeded with the five servers above so they can be discovered via
  `GET /v0.1/servers`. (port 8090)

## Adding another MCP server

1. Create a new module directory and `pom.xml` with `<parent>` pointing at
   `nl.makan1869.capabilitymesh:capability-mesh`.
2. Add it to `<modules>` in the root `pom.xml`.
3. Depend on `org.springframework.ai:spring-ai-starter-mcp-server-webmvc`,
   give it its own `server.port` and `spring.ai.mcp.server.name` in
   `application.yml`, and expose tools with `@McpTool`.

## Running everything

```bash
mvn clean verify
```

builds and tests every module from the root.

## Docker image (all services in one container)

```bash
./build/build.sh
```

builds every module with Maven, then packages all six jars into a single
Docker image (`build/Dockerfile`), each running as its own process
(`build/entrypoint.sh`), with every port (8081-8085, 8090) exposed:

```bash
docker run --rm \
  -p 8081:8081 -p 8082:8082 -p 8083:8083 \
  -p 8084:8084 -p 8085:8085 -p 8090:8090 \
  capability-mesh:latest
```
