package nl.makan1869.capabilitymesh.mcpregistry.model;

import java.util.List;

public record Icon(String src, String mimeType, List<String> sizes, String theme) {}
