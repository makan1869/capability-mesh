package nl.makan1869.capabilitymesh.mcpregistry.web;

import nl.makan1869.capabilitymesh.mcpregistry.model.ServerList;
import nl.makan1869.capabilitymesh.mcpregistry.model.ServerResponse;
import nl.makan1869.capabilitymesh.mcpregistry.store.ServerRegistry;
import java.time.Instant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v0.1")
public class ServersController {

    private final ServerRegistry registry;

    public ServersController(ServerRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/servers")
    public ServerList listServers(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String search,
            @RequestParam(value = "updated_since", required = false) Instant updatedSince,
            @RequestParam(required = false) String version,
            @RequestParam(value = "include_deleted", required = false, defaultValue = "false") boolean includeDeleted) {
        return registry.listServers(cursor, limit, search, updatedSince, version, includeDeleted);
    }

    @GetMapping("/servers/{serverName}/versions")
    public ServerList listVersions(
            @PathVariable String serverName,
            @RequestParam(value = "include_deleted", required = false, defaultValue = "false") boolean includeDeleted) {
        return registry
                .listVersions(serverName, includeDeleted)
                .orElseThrow(() -> new ServerNotFoundException("Server not found"));
    }

    @GetMapping("/servers/{serverName}/versions/{version}")
    public ServerResponse getVersion(
            @PathVariable String serverName,
            @PathVariable String version,
            @RequestParam(value = "include_deleted", required = false, defaultValue = "false") boolean includeDeleted) {
        return registry
                .getVersion(serverName, version, includeDeleted)
                .orElseThrow(() -> new ServerNotFoundException("Server not found"));
    }
}
