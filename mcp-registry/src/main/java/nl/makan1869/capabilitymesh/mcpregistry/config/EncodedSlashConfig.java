package nl.makan1869.capabilitymesh.mcpregistry.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Server names in this API are reverse-DNS style with a required slash (e.g.
 * "com.example/my-server"), so the path spec requires clients to percent-encode it as
 * "%2F" (e.g. "/v0.1/servers/com.example%2Fmy-server/versions"). Tomcat rejects "%2F" in
 * request paths by default as a security precaution against path-traversal attacks.
 * "passthrough" keeps it literal within the path segment (neither rejecting nor
 * decoding it into an actual "/"), so Spring's normal @PathVariable decoding turns it
 * back into "/" for us, without otherwise splitting the path into extra segments.
 */
@Configuration
public class EncodedSlashConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> encodedSlashCustomizer() {
        return factory -> factory.addConnectorCustomizers(this::allowEncodedSlashPassthrough);
    }

    private void allowEncodedSlashPassthrough(Connector connector) {
        connector.setEncodedSolidusHandling("passthrough");
    }
}
