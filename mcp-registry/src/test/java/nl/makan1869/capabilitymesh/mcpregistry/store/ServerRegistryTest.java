package nl.makan1869.capabilitymesh.mcpregistry.store;

import static org.assertj.core.api.Assertions.assertThat;

import nl.makan1869.capabilitymesh.mcpregistry.model.ServerList;
import nl.makan1869.capabilitymesh.mcpregistry.model.ServerResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class ServerRegistryTest {

    private static final String ECHO_PRINTER = "nl.makan1869.capabilitymesh/echo-printer";

    private ServerRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ServerRegistry(new ServerSeedLoader(new ObjectMapper()));
    }

    @Test
    void listsAllFiveSeededServers() {
        ServerList list = registry.listServers(null, null, null, null, null, false);

        assertThat(list.servers()).hasSize(5);
        assertThat(list.metadata().count()).isEqualTo(5);
        assertThat(list.servers()).allMatch(e -> Boolean.TRUE.equals(e.official().isLatest()));
        assertThat(list.servers()).allMatch(e -> "active".equals(e.official().status()));
    }

    @Test
    void searchFiltersByNameSubstringCaseInsensitively() {
        ServerList list = registry.listServers(null, null, "ECHO", null, null, false);

        assertThat(list.servers()).hasSize(1);
        assertThat(list.servers().get(0).server().name()).isEqualTo(ECHO_PRINTER);
    }

    @Test
    void versionLatestFilterKeepsOnlyLatestVersions() {
        ServerList list = registry.listServers(null, null, null, null, "latest", false);

        assertThat(list.servers()).hasSize(5);
    }

    @Test
    void versionExactFilterKeepsOnlyMatchingVersion() {
        ServerList list = registry.listServers(null, null, null, null, "0.1.0", false);

        assertThat(list.servers()).hasSize(5);

        ServerList noMatch = registry.listServers(null, null, null, null, "9.9.9", false);
        assertThat(noMatch.servers()).isEmpty();
    }

    @Test
    void paginationWalksThroughAllEntriesWithoutDuplicatesOrGaps() {
        Set<String> seenNames = new HashSet<>();
        String cursor = null;
        int pages = 0;

        while (true) {
            ServerList page = registry.listServers(cursor, 2, null, null, null, false);
            page.servers().forEach(e -> seenNames.add(e.server().name()));
            pages++;
            if (page.metadata().nextCursor() == null) {
                break;
            }
            cursor = page.metadata().nextCursor();
            assertThat(pages).isLessThan(10); // guard against an infinite loop bug
        }

        assertThat(seenNames).hasSize(5);
        assertThat(pages).isEqualTo(3); // 2 + 2 + 1
    }

    @Test
    void listVersionsReturnsTheSingleSeededVersion() {
        Optional<ServerList> list = registry.listVersions(ECHO_PRINTER, false);

        assertThat(list).isPresent();
        assertThat(list.get().servers()).hasSize(1);
        assertThat(list.get().servers().get(0).server().version()).isEqualTo("0.1.0");
    }

    @Test
    void listVersionsReturnsEmptyOptionalForUnknownServer() {
        assertThat(registry.listVersions("com.example/does-not-exist", false)).isEmpty();
    }

    @Test
    void getVersionSupportsTheLatestKeyword() {
        Optional<ServerResponse> response = registry.getVersion(ECHO_PRINTER, "latest", false);

        assertThat(response).isPresent();
        assertThat(response.get().server().version()).isEqualTo("0.1.0");
    }

    @Test
    void getVersionSupportsAnExactVersionString() {
        Optional<ServerResponse> response = registry.getVersion(ECHO_PRINTER, "0.1.0", false);

        assertThat(response).isPresent();
        assertThat(response.get().server().name()).isEqualTo(ECHO_PRINTER);
    }

    @Test
    void getVersionReturnsEmptyOptionalForUnknownVersion() {
        assertThat(registry.getVersion(ECHO_PRINTER, "9.9.9", false)).isEmpty();
    }

    @Test
    void seededRemoteUrlsPointAtEachModulesOwnPort() {
        List<ServerResponse> servers = registry.listServers(null, null, null, null, null, false).servers();

        assertThat(servers)
                .extracting(e -> e.server().remotes().get(0).toString())
                .anyMatch(s -> s.contains("8081"))
                .anyMatch(s -> s.contains("8082"))
                .anyMatch(s -> s.contains("8083"))
                .anyMatch(s -> s.contains("8084"))
                .anyMatch(s -> s.contains("8085"));
    }
}
