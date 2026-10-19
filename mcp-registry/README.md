# mcp-registry

A read-only implementation of the MCP [generic registry API](https://github.com/modelcontextprotocol/registry/blob/main/docs/reference/api/generic-registry-api.md)
(`server.json` discovery), seeded at startup with this mesh's own MCP servers
so it's actually useful to query. Runs on port 8090.

## Scope

This implements only the three read endpoints from the spec — no publish,
update, delete, or status-change endpoints, and none of the official
registry's GitHub/DNS/OIDC auth-challenge endpoints (irrelevant to an
internal mesh registry):

| Method | Path | Notes |
|---|---|---|
| GET | `/v0.1/servers` | `cursor`, `limit`, `search`, `updated_since`, `version`, `include_deleted` query params |
| GET | `/v0.1/servers/{serverName}/versions` | All versions of one server |
| GET | `/v0.1/servers/{serverName}/versions/{version}` | One version; `version=latest` works |

Since there's no publish endpoint, servers are registered by adding a
`server.json` file under `src/main/resources/servers/*.server.json` — see
the five existing ones for the rest of this mesh (`echo-printer`,
`char-printer`, `second-character-printer`, `random-printer`,
`next-char-printer`), each pointing at its module's real `streamable-http`
URL and port.

## The `%2F` in server names

Server names are reverse-DNS style with a required slash (e.g.
`nl.makan1869.capabilitymesh/echo-printer`), so per the spec, clients
must percent-encode it in the URL path as `%2F`. Tomcat rejects `%2F` in
request paths by default (a path-traversal mitigation), so
`config/EncodedSlashConfig.java` sets the connector's
`encodedSolidusHandling` to `passthrough` — this is the one genuinely
tricky part of implementing this API, and it's covered by an integration
test (`ServersControllerIntegrationTest`) that sends real percent-encoded
requests, not just unit tests against the service layer.

## Run it

```bash
mvn -pl mcp-registry spring-boot:run
```

## Try it with curl

```bash
# List servers (paginated)
curl "http://localhost:8090/v0.1/servers?limit=2"

# Get one server's latest version — note the %2F for the slash in the name
curl "http://localhost:8090/v0.1/servers/nl.makan1869.capabilitymesh%2Fecho-printer/versions/latest"

# All versions of a server
curl "http://localhost:8090/v0.1/servers/nl.makan1869.capabilitymesh%2Fchar-printer/versions"

# Search
curl "http://localhost:8090/v0.1/servers?search=random"
```

Unknown servers return `404 {"error": "Server not found"}`.
