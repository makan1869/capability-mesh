# next-char-printer

A remote MCP server with one tool, `next_character`, which takes a single
letter and returns the next letter of the alphabet, cycling from `Z` back to
`A` (case is preserved, so `z` cycles back to `a`). Rejects anything that
isn't exactly one letter with a tool error. Transport: **Streamable HTTP**
(`spring.ai.mcp.server.protocol: STREAMABLE`), served on its own port.

## Run it

```bash
mvn -pl next-char-printer spring-boot:run
```

The MCP endpoint is `POST http://localhost:8085/mcp`.

## Try it with curl

Streamable HTTP is stateful: `initialize` returns an `Mcp-Session-Id` header
that must be echoed back on every following request.

```bash
# 1. Initialize and capture the session id
curl -s -D /tmp/headers.txt -X POST http://localhost:8085/mcp \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2026-07-28","capabilities":{},"clientInfo":{"name":"curl-test","version":"0.0.1"}}}'
SESSION=$(grep -i '^Mcp-Session-Id:' /tmp/headers.txt | awk '{print $2}' | tr -d '\r')

# 2. Acknowledge initialization
curl -s -X POST http://localhost:8085/mcp \
  -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SESSION" \
  -d '{"jsonrpc":"2.0","method":"notifications/initialized"}'

# 3. Call the tool
curl -s -X POST http://localhost:8085/mcp \
  -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SESSION" \
  -d '{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"next_character","arguments":{"character":"Z"}}}'
# -> data:{"jsonrpc":"2.0","id":3,"result":{"content":[{"type":"text","text":"A"}],"isError":false}}
```
