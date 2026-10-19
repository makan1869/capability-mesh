package nl.makan1869.capabilitymesh.printer.registry;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Calls mcp-registry's GET /v0.1/servers and turns the response into a flat, usable list. */
@Component
public class RegistryClient {

    private static final Logger log = LoggerFactory.getLogger(RegistryClient.class);

    private final RestClient restClient;

    public RegistryClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public List<DiscoveredServer> listServers(String registryUrl) {
        RegistryServerList response = restClient
                .get()
                .uri(registryUrl + "/v0.1/servers")
                .retrieve()
                .body(RegistryServerList.class);

        List<DiscoveredServer> servers = new ArrayList<>();
        if (response == null || response.servers() == null) {
            return servers;
        }

        for (RegistryServerList.Entry entry : response.servers()) {
            var detail = entry.server();
            String url = firstRemoteUrl(detail);
            if (url == null) {
                log.warn("Skipping '{}': no remote URL in its registry entry", detail.name());
                continue;
            }
            servers.add(new DiscoveredServer(detail.name(), url));
        }
        return servers;
    }

    private static String firstRemoteUrl(RegistryServerList.ServerDetail detail) {
        if (detail.remotes() == null || detail.remotes().isEmpty()) {
            return null;
        }
        return detail.remotes().get(0).url();
    }
}
