package nl.makan1869.capabilitymesh.mcpregistry.web;

import static org.assertj.core.api.Assertions.assertThat;

import nl.makan1869.capabilitymesh.mcpregistry.model.ServerList;
import nl.makan1869.capabilitymesh.mcpregistry.model.ServerResponse;
import java.net.URI;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServersControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    void listServersReturnsAllSeededServers() {
        ResponseEntity<ServerList> response =
                restTemplate.getForEntity(baseUrl() + "/v0.1/servers", ServerList.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().servers()).hasSize(5);
    }

    /**
     * The server name "nl.makan1869.capabilitymesh/echo-printer" must be sent
     * percent-encoded as "...%2Fecho-printer" in the path. This is the one part of the
     * generic registry API that's genuinely tricky to get working through an embedded
     * Tomcat, so it's worth proving end-to-end rather than only unit-testing the service
     * layer (which never sees an actual encoded HTTP request path).
     */
    @Test
    void getSpecificVersionAcceptsAPercentEncodedSlashInTheServerName() throws Exception {
        URI uri = new URI(baseUrl() + "/v0.1/servers/nl.makan1869.capabilitymesh%2Fecho-printer/versions/latest");

        ResponseEntity<ServerResponse> response = restTemplate.exchange(
                uri, org.springframework.http.HttpMethod.GET, null, ServerResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().server().name()).isEqualTo("nl.makan1869.capabilitymesh/echo-printer");
        assertThat(response.getBody().server().version()).isEqualTo("0.1.0");
    }

    @Test
    void listVersionsAlsoAcceptsThePercentEncodedSlash() throws Exception {
        URI uri = new URI(baseUrl() + "/v0.1/servers/nl.makan1869.capabilitymesh%2Fchar-printer/versions");

        ResponseEntity<ServerList> response =
                restTemplate.exchange(uri, org.springframework.http.HttpMethod.GET, null, ServerList.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().servers()).hasSize(1);
    }

    @Test
    void unknownServerReturns404WithAnErrorBody() throws Exception {
        URI uri = new URI(baseUrl() + "/v0.1/servers/com.example%2Fdoes-not-exist/versions");

        ResponseEntity<Map> response =
                restTemplate.exchange(uri, org.springframework.http.HttpMethod.GET, null, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsKey("error");
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}
