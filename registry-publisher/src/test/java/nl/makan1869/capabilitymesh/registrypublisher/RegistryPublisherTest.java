package nl.makan1869.capabilitymesh.registrypublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class RegistryPublisherTest {

    private static final String REGISTRY = "http://registry.test";
    private static final String VERSION_URL =
            REGISTRY + "/v0.1/servers/nl.makan1869.capabilitymesh%2Fchar-printer/versions/0.1.0";

    private MockRestServiceServer server;
    private RegistryPublisher publisher;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(REGISTRY);
        server = MockRestServiceServer.bindTo(builder).build();

        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.ai.mcp.server.name", "char-printer")
                .withProperty("spring.ai.mcp.server.version", "0.1.0")
                .withProperty("local.server.port", "8082");
        RegistryPublisherProperties properties = new RegistryPublisherProperties(
                true, REGISTRY, "nl.makan1869.capabilitymesh", "Char Printer", "Prints chars.", "localhost");
        publisher = new RegistryPublisher(builder.build(), properties, environment);
    }

    @Test
    void buildsServerJsonFromMcpServerProperties() {
        Map<String, Object> serverJson = publisher.buildServerJson();

        assertThat(serverJson)
                .containsEntry("$schema", RegistryPublisher.SCHEMA_URL)
                .containsEntry("name", "nl.makan1869.capabilitymesh/char-printer")
                .containsEntry("title", "Char Printer")
                .containsEntry("description", "Prints chars.")
                .containsEntry("version", "0.1.0");
        assertThat(serverJson.get("remotes")).asInstanceOf(LIST)
                .containsExactly(Map.of("type", "streamable-http", "url", "http://localhost:8082/mcp"));
    }

    @Test
    void publishesWhenVersionIsNotRegisteredYet() {
        expectToken();
        server.expect(requestTo(VERSION_URL)).andExpect(method(HttpMethod.GET)).andRespond(withResourceNotFound());
        server.expect(requestTo(REGISTRY + "/v0.1/publish"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer tok"))
                .andExpect(jsonPath("$.name").value("nl.makan1869.capabilitymesh/char-printer"))
                .andExpect(jsonPath("$.remotes[0].url").value("http://localhost:8082/mcp"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        publisher.onApplicationReady();

        server.verify();
    }

    @Test
    void updatesInPlaceWhenVersionIsAlreadyRegistered() {
        expectToken();
        server.expect(requestTo(VERSION_URL)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(VERSION_URL))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(header("Authorization", "Bearer tok"))
                .andExpect(jsonPath("$.version").value("0.1.0"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        publisher.onApplicationReady();

        server.verify();
    }

    @Test
    void registryFailureDoesNotPropagate() {
        server.expect(requestTo(REGISTRY + "/v0/auth/none")).andRespond(withServerError());

        publisher.onApplicationReady();

        server.verify();
    }

    private void expectToken() {
        server.expect(requestTo(REGISTRY + "/v0/auth/none"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"registry_token\":\"tok\",\"expires_at\":0}", MediaType.APPLICATION_JSON));
    }
}
