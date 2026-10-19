package nl.makan1869.capabilitymesh.printer.mcp;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import nl.makan1869.capabilitymesh.printer.registry.DiscoveredServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Builds a connected, initialized MCP client for a server discovered from the registry. */
@Component
public class McpClientFactory {

    private static final Logger log = LoggerFactory.getLogger(McpClientFactory.class);

    public McpSyncClient connect(DiscoveredServer server) {
        String baseUri = McpUrls.baseUriOf(server.mcpUrl());
        McpClientTransport transport = HttpClientStreamableHttpTransport.builder(baseUri).build();
        McpSyncClient client = McpClient.sync(transport).build();
        client.initialize();
        log.info("Connected MCP client for '{}' at {}", server.name(), server.mcpUrl());
        return client;
    }
}
