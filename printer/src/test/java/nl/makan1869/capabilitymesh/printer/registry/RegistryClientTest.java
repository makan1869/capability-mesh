package nl.makan1869.capabilitymesh.printer.registry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class RegistryClientTest {

    private MockRestServiceServer mockServer;
    private RegistryClient registryClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        registryClient = new RegistryClient(builder);
    }

    @Test
    void parsesServersAndTheirFirstRemoteUrl() {
        mockServer
                .expect(requestTo("http://localhost:8090/v0.1/servers"))
                .andRespond(withSuccess(
                        """
                        {
                          "servers": [
                            {
                              "server": {
                                "name": "nl.makan1869.capabilitymesh/echo-printer",
                                "remotes": [{"type": "streamable-http", "url": "http://localhost:8081/mcp"}]
                              }
                            }
                          ],
                          "metadata": {"count": 1}
                        }
                        """,
                        MediaType.APPLICATION_JSON));

        List<DiscoveredServer> servers = registryClient.listServers("http://localhost:8090");

        assertThat(servers)
                .containsExactly(
                        new DiscoveredServer(
                                "nl.makan1869.capabilitymesh/echo-printer", "http://localhost:8081/mcp"));
    }

    @Test
    void skipsServersWithNoRemotes() {
        mockServer
                .expect(requestTo("http://localhost:8090/v0.1/servers"))
                .andRespond(withSuccess(
                        """
                        {"servers": [{"server": {"name": "com.example/no-remotes", "remotes": []}}]}
                        """,
                        MediaType.APPLICATION_JSON));

        assertThat(registryClient.listServers("http://localhost:8090")).isEmpty();
    }
}
