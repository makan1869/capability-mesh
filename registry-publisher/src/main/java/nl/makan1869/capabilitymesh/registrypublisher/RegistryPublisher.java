package nl.makan1869.capabilitymesh.registrypublisher;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Once the server is up, registers it with the MCP registry: fetches an anonymous token, then
 * publishes its server.json, or updates it in place if that name and version already exist
 * (e.g. on a restart, or after the port changed).
 *
 * Failures are logged, never thrown: the server keeps running without a registry entry.
 */
public class RegistryPublisher {

    static final String SCHEMA_URL = "https://static.modelcontextprotocol.io/schemas/2025-12-11/server.schema.json";

    private static final Logger log = LoggerFactory.getLogger(RegistryPublisher.class);

    private final RestClient restClient;
    private final RegistryPublisherProperties properties;
    private final Environment environment;

    public RegistryPublisher(RestClient restClient, RegistryPublisherProperties properties, Environment environment) {
        this.restClient = restClient;
        this.properties = properties;
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        Map<String, Object> serverJson = buildServerJson();
        try {
            publish(serverJson);
        } catch (RestClientResponseException e) {
            log.warn("Could not register '{}' with MCP registry {}: HTTP {} {}", serverJson.get("name"),
                    properties.url(), e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (RestClientException | IllegalStateException e) {
            log.warn("Could not register '{}' with MCP registry {}: {}", serverJson.get("name"), properties.url(),
                    e.getMessage());
        }
    }

    void publish(Map<String, Object> serverJson) {
        String name = (String) serverJson.get("name");
        String version = (String) serverJson.get("version");
        String authorization = "Bearer " + fetchToken();

        HttpStatusCode existing = restClient.get()
                .uri("/v0.1/servers/{name}/versions/{version}", name, version)
                .exchange((request, response) -> response.getStatusCode());

        if (existing.value() == HttpStatus.NOT_FOUND.value()) {
            restClient.post()
                    .uri("/v0.1/publish")
                    .header("Authorization", authorization)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(serverJson)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Published '{}' {} to MCP registry {} at {}", name, version, properties.url(), remoteUrl());
        } else if (existing.is2xxSuccessful()) {
            restClient.put()
                    .uri("/v0.1/servers/{name}/versions/{version}", name, version)
                    .header("Authorization", authorization)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(serverJson)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Updated '{}' {} in MCP registry {} at {}", name, version, properties.url(), remoteUrl());
        } else {
            throw new IllegalStateException("unexpected HTTP " + existing.value() + " looking up " + name + "@" + version);
        }
    }

    private String fetchToken() {
        Map<?, ?> response = restClient.post().uri("/v0/auth/none").retrieve().body(Map.class);
        Object token = response == null ? null : response.get("registry_token");
        if (!(token instanceof String value) || value.isBlank()) {
            throw new IllegalStateException("registry returned no token from /v0/auth/none");
        }
        return value;
    }

    Map<String, Object> buildServerJson() {
        Map<String, Object> serverJson = new LinkedHashMap<>();
        serverJson.put("$schema", SCHEMA_URL);
        serverJson.put("name", properties.namespace() + "/" + environment.getRequiredProperty("spring.ai.mcp.server.name"));
        if (properties.description() != null) {
            serverJson.put("description", properties.description());
        }
        if (properties.title() != null) {
            serverJson.put("title", properties.title());
        }
        serverJson.put("version", environment.getRequiredProperty("spring.ai.mcp.server.version"));
        serverJson.put("remotes", List.of(Map.of("type", "streamable-http", "url", remoteUrl())));
        return serverJson;
    }

    private String remoteUrl() {
        // local.server.port is the port actually bound, which differs from server.port when that is 0.
        String port = environment.getRequiredProperty("local.server.port");
        String endpoint = environment.getProperty("spring.ai.mcp.server.streamable-http.mcp-endpoint", "/mcp");
        return "http://" + properties.advertisedHost() + ":" + port + endpoint;
    }
}
