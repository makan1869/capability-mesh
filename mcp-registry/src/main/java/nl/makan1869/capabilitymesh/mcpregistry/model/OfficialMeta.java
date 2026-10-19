package nl.makan1869.capabilitymesh.mcpregistry.model;

import java.time.Instant;

/** Registry-managed metadata, nested under ServerResponse._meta["io.modelcontextprotocol.registry/official"]. */
public record OfficialMeta(
        String status,
        String statusMessage,
        Instant statusChangedAt,
        Instant publishedAt,
        Instant updatedAt,
        Boolean isLatest) {

    public static final String META_KEY = "io.modelcontextprotocol.registry/official";

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_DEPRECATED = "deprecated";
    public static final String STATUS_DELETED = "deleted";
}
