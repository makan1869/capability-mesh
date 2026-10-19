package nl.makan1869.capabilitymesh.mcpregistry.model;

import java.util.List;

public record ServerList(List<ServerResponse> servers, ServerListMetadata metadata) {}
