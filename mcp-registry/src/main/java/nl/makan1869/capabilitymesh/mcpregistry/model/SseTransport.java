package nl.makan1869.capabilitymesh.mcpregistry.model;

import java.util.List;
import java.util.Map;

public record SseTransport(
        String type, String url, List<KeyValueInput> headers, Map<String, Input> variables)
        implements Transport {}
