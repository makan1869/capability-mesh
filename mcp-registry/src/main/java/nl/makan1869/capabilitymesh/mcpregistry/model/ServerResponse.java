package nl.makan1869.capabilitymesh.mcpregistry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record ServerResponse(ServerDetail server, @JsonProperty("_meta") Map<String, Object> meta) {

    public static ServerResponse of(ServerDetail server, OfficialMeta officialMeta) {
        return new ServerResponse(server, Map.of(OfficialMeta.META_KEY, officialMeta));
    }

    public OfficialMeta official() {
        return (OfficialMeta) meta.get(OfficialMeta.META_KEY);
    }
}
