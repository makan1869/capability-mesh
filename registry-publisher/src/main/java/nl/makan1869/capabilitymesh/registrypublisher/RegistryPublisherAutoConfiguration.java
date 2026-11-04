package nl.makan1869.capabilitymesh.registrypublisher;

import java.time.Duration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@AutoConfiguration
@EnableConfigurationProperties(RegistryPublisherProperties.class)
@ConditionalOnProperty(prefix = "capability-mesh.registry", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RegistryPublisherAutoConfiguration {

    @Bean
    public RegistryPublisher registryPublisher(RegistryPublisherProperties properties, Environment environment) {
        // Short timeouts: a missing registry must not hold up the server's startup.
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        RestClient restClient = RestClient.builder()
                .baseUrl(properties.url())
                .requestFactory(requestFactory)
                .build();
        return new RegistryPublisher(restClient, properties, environment);
    }
}
