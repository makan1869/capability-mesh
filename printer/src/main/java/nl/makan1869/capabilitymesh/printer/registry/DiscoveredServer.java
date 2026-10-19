package nl.makan1869.capabilitymesh.printer.registry;

/** One MCP server discovered from the registry, ready to connect to. */
public record DiscoveredServer(String name, String mcpUrl) {}
