# echo-printer

A remote MCP server with one tool, `echo`, which takes a single letter
(`a`-`z`, `A`-`Z`) or digit (`0`-`9`) and returns it back unchanged.
Anything else (symbols, multi-character strings, empty input) is rejected
as a tool error. Transport: **Streamable HTTP**
(`spring.ai.mcp.server.protocol: STREAMABLE`), served on its own port so it
can run alongside sibling MCP-server modules.

## Run it

```bash
mvn -pl echo-printer spring-boot:run
```

The MCP endpoint is `POST http://localhost:8081/mcp`.

## Try it with curl

Streamable HTTP is stateful: `initialize` returns an `Mcp-Session-Id` header
that must be echoed back on every following request.

```bash
# 1. Initialize and capture the session id
curl -s -D /tmp/headers.txt -X POST http://localhost:8081/mcp \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2026-07-28","capabilities":{},"clientInfo":{"name":"curl-test","version":"0.0.1"}}}'
SESSION=$(grep -i '^Mcp-Session-Id:' /tmp/headers.txt | awk '{print $2}' | tr -d '\r')

# 2. Acknowledge initialization
curl -s -X POST http://localhost:8081/mcp \
  -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SESSION" \
  -d '{"jsonrpc":"2.0","method":"notifications/initialized"}'

# 3. Call the echo tool
curl -s -X POST http://localhost:8081/mcp \
  -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SESSION" \
  -d '{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"echo","arguments":{"character":"Q"}}}'
# -> data:{"jsonrpc":"2.0","id":3,"result":{"content":[{"type":"text","text":"Q"}],"isError":false}}

# A symbol is rejected
curl -s -X POST http://localhost:8081/mcp \
  -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SESSION" \
  -d '{"jsonrpc":"2.0","id":4,"method":"tools/call","params":{"name":"echo","arguments":{"character":"@"}}}'
# -> data:{"jsonrpc":"2.0","id":4,"result":{"content":[{"type":"text","text":"Input must be a single letter (a-z, A-Z) or digit (0-9)"}],"isError":true}}
```

Or point the [MCP Inspector](https://github.com/modelcontextprotocol/inspector)
at `http://localhost:8081/mcp` with transport type "Streamable HTTP" for an
interactive UI instead.

## Note on tool parameter names

`spring-ai-starter-mcp-server-webmvc` reads `@McpTool`/`@McpToolParam` method
parameter names via reflection, which only works if the module is compiled
with `-parameters`. The root `pom.xml` enables this for every module via
`maven-compiler-plugin` — without it, tool arguments show up as `arg0`,
`arg1`, etc. instead of their real names.
