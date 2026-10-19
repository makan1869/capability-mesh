package nl.makan1869.capabilitymesh.mcpregistry.store;

import nl.makan1869.capabilitymesh.mcpregistry.model.ServerDetail;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Loads server.json documents from the classpath. This stands in for a real publish
 * endpoint: since this registry is read-only, servers are registered by adding a
 * server.json file under src/main/resources/servers/ rather than via the API.
 */
@Component
public class ServerSeedLoader {

    private static final String LOCATION_PATTERN = "classpath:servers/*.server.json";

    private final ObjectMapper objectMapper;

    public ServerSeedLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<ServerDetail> loadAll() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(LOCATION_PATTERN);
            List<ServerDetail> servers = new java.util.ArrayList<>();
            for (Resource resource : resources) {
                try (InputStream in = resource.getInputStream()) {
                    servers.add(objectMapper.readValue(in, ServerDetail.class));
                }
            }
            return servers;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load seed server.json files", e);
        }
    }
}
