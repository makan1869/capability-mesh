# random-printer

A remote MCP server with one tool, `random_character`, which takes a string
(its content is ignored) and returns a random letter (`A`-`Z`, `a`-`z`).
Transport: **Streamable HTTP** (`spring.ai.mcp.server.protocol: STREAMABLE`),
served on its own port.

## Run it

```bash
mvn -pl random-printer spring-boot:run
```

The MCP endpoint is `POST http://localhost:8084/mcp`.

## Try it with curl

Streamable HTTP is stateful: `initialize` returns an `Mcp-Session-Id` header
that must be echoed back on every following request.

```bash
# 1. Initialize and capture the session id
curl -s -D /tmp/headers.txt -X POST http://localhost:8084/mcp \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2026-07-28","capabilities":{},"clientInfo":{"name":"curl-test","version":"0.0.1"}}}'
SESSION=$(grep -i '^Mcp-Session-Id:' /tmp/headers.txt | awk '{print $2}' | tr -d '\r')

# 2. Acknowledge initialization
curl -s -X POST http://localhost:8084/mcp \
  -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SESSION" \
  -d '{"jsonrpc":"2.0","method":"notifications/initialized"}'

# 3. Call the tool (input content is ignored)
curl -s -X POST http://localhost:8084/mcp \
  -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SESSION" \
  -d '{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"random_character","arguments":{"text":"anything"}}}'
# -> data:{"jsonrpc":"2.0","id":3,"result":{"content":[{"type":"text","text":"<a random letter>"}],"isError":false}}
```
