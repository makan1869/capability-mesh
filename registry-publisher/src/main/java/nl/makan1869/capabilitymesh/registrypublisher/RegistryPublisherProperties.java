package nl.makan1869.capabilitymesh.registrypublisher;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Where and how this MCP server registers itself on startup.
 *
 * The server's name, version and MCP endpoint are taken from spring.ai.mcp.server.*; the
 * registry name is {@code namespace + "/" + spring.ai.mcp.server.name}. The default namespace
 * is the one an anonymous registry token (POST /v0/auth/none) may publish to.
 */
@ConfigurationProperties(prefix = "capability-mesh.registry")
public record RegistryPublisherProperties(
        @DefaultValue("true") boolean enabled,
        @DefaultValue("http://localhost:8090") String url,
        @DefaultValue("nl.makan1869.capabilitymesh") String namespace,
        String title,
        String description,
        @DefaultValue("localhost") String advertisedHost) {
}
