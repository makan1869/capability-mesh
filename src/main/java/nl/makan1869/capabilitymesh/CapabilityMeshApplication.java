package nl.makan1869.capabilitymesh;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class CapabilityMeshApplication {

    public static void main(String[] args) {
        SpringApplication.run(CapabilityMeshApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(ChatClient.Builder chatClientBuilder,
                                        @Value("${agent.skills.dirs:Unknown}") List<Resource> agentSkillsDirs) throws IOException {

        return args -> {

        };

    }

}
