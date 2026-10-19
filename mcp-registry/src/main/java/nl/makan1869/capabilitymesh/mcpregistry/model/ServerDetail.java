package nl.makan1869.capabilitymesh.mcpregistry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/** The server.json document itself (spec schema: ServerDetail). */
public record ServerDetail(
        String name,
        String description,
        String title,
        Repository repository,
        String version,
        String websiteUrl,
        List<Icon> icons,
        @JsonProperty("$schema") String schema,
        List<PackageEntry> packages,
        List<Transport> remotes,
        @JsonProperty("_meta") Map<String, Object> meta) {}
