package nl.makan1869.capabilitymesh.printer.registry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Minimal mirror of mcp-registry's GET /v0.1/servers response shape - only the
 * fields this client actually needs (name and remote URL), ignoring the rest
 * (description, version, _meta, etc).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RegistryServerList(List<Entry> servers) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Entry(ServerDetail server) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ServerDetail(String name, List<Remote> remotes) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Remote(String type, String url) {}
}
