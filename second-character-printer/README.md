# second-character-printer

A remote MCP server with one tool, `second_character`, which takes a string
and returns its second character (index 1). Rejects input shorter than two
characters with a tool error rather than guessing. Transport: **Streamable
HTTP** (`spring.ai.mcp.server.protocol: STREAMABLE`), served on its own port.

## Run it

```bash
mvn -pl second-character-printer spring-boot:run
```

The MCP endpoint is `POST http://localhost:8083/mcp`.

## Try it with curl

Streamable HTTP is stateful: `initialize` returns an `Mcp-Session-Id` header
that must be echoed back on every following request.

```bash
# 1. Initialize and capture the session id
curl -s -D /tmp/headers.txt -X POST http://localhost:8083/mcp \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2026-07-28","capabilities":{},"clientInfo":{"name":"curl-test","version":"0.0.1"}}}'
SESSION=$(grep -i '^Mcp-Session-Id:' /tmp/headers.txt | awk '{print $2}' | tr -d '\r')

# 2. Acknowledge initialization
curl -s -X POST http://localhost:8083/mcp \
  -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SESSION" \
  -d '{"jsonrpc":"2.0","method":"notifications/initialized"}'

# 3. Call the tool
curl -s -X POST http://localhost:8083/mcp \
  -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SESSION" \
  -d '{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"second_character","arguments":{"text":"hello"}}}'
# -> data:{"jsonrpc":"2.0","id":3,"result":{"content":[{"type":"text","text":"e"}],"isError":false}}
```
