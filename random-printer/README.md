# random-printer

A remote MCP server with one tool, `random_character`. Given a character, it
builds a 6-character string: 5 random printable keyboard characters (letters,
digits, and symbols like `@#$%^`) followed by the given character, which
always ends up at index 5. It then returns the character at a random index
from 0 to 5 - so most calls return one of the random characters, and roughly
1 in 6 return the character you gave it back.

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

# 3. Call the tool a few times - most calls return a random character,
#    roughly 1 in 6 return "Z" back (whatever character you gave it)
curl -s -X POST http://localhost:8084/mcp \
  -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SESSION" \
  -d '{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"random_character","arguments":{"character":"Z"}}}'
# -> data:{"jsonrpc":"2.0","id":3,"result":{"content":[{"type":"text","text":"<some character>"}],"isError":false}}
```
