# char-printer

A remote MCP server with 26 tools — `a` through `z` — each taking no input
and always returning the matching uppercase letter (`a` → `"A"`, ...,
`z` → `"Z"`). Transport: **Streamable HTTP**
(`spring.ai.mcp.server.protocol: STREAMABLE`), served on its own port so it
can run alongside sibling MCP-server modules.

## Run it

```bash
mvn -pl char-printer spring-boot:run
```

The MCP endpoint is `POST http://localhost:8082/mcp`.

## Try it with curl

Streamable HTTP is stateful: `initialize` returns an `Mcp-Session-Id` header
that must be echoed back on every following request.

```bash
# 1. Initialize and capture the session id
curl -s -D /tmp/headers.txt -X POST http://localhost:8082/mcp \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2026-07-28","capabilities":{},"clientInfo":{"name":"curl-test","version":"0.0.1"}}}'
SESSION=$(grep -i '^Mcp-Session-Id:' /tmp/headers.txt | awk '{print $2}' | tr -d '\r')

# 2. Acknowledge initialization
curl -s -X POST http://localhost:8082/mcp \
  -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SESSION" \
  -d '{"jsonrpc":"2.0","method":"notifications/initialized"}'

# 3. Call any of the tools (no arguments needed)
curl -s -X POST http://localhost:8082/mcp \
  -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SESSION" \
  -d '{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"a","arguments":{}}}'
# -> data:{"jsonrpc":"2.0","id":3,"result":{"content":[{"type":"text","text":"A"}],"isError":false}}

curl -s -X POST http://localhost:8082/mcp \
  -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SESSION" \
  -d '{"jsonrpc":"2.0","id":4,"method":"tools/call","params":{"name":"z","arguments":{}}}'
# -> data:{"jsonrpc":"2.0","id":4,"result":{"content":[{"type":"text","text":"Z"}],"isError":false}}
```
