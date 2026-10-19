package nl.makan1869.capabilitymesh.example1;

import nl.makan1869.capabilitymesh.example1.a2a.*;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

/**
 * Orchestrates the three printer agents using the A2A protocol to produce "ABC".
 *
 * <p>For each agent, the orchestrator:
 * <ol>
 *   <li>Discovers the agent by fetching its {@link AgentCard}.</li>
 *   <li>Submits a {@code tasks/send} JSON-RPC 2.0 request.</li>
 *   <li>Collects the artifact text from the response.</li>
 * </ol>
 * The results are concatenated and printed as a single line.
 */
@Component
public class PrinterOrchestrator {

    private static final List<String> AGENT_URLS = List.of(
            "http://localhost:8080/api/agents/printer-a",
            "http://localhost:8080/api/agents/printer-b",
            "http://localhost:8080/api/agents/printer-c"
    );

    private final RestClient restClient = RestClient.create();

//    @Bean
//    @Order(1)
    ApplicationRunner orchestrate() {
        return args -> {
            StringBuilder result = new StringBuilder();

            for (String agentUrl : AGENT_URLS) {
                // 1. Discover agent card
                AgentCard card = restClient.get()
                        .uri(agentUrl)
                        .retrieve()
                        .body(AgentCard.class);

                System.out.printf("[Orchestrator] Discovered agent: %s — %s%n",
                        card.name(), card.description());

                // 2. Build JSON-RPC tasks/send request
                String taskId = UUID.randomUUID().toString();
                JsonRpcRequest<TaskSendParams> rpcRequest = JsonRpcRequest.of(
                        taskId,
                        "tasks/send",
                        new TaskSendParams(taskId, TaskMessage.user("print"))
                );

                // 3. Send task and collect artifact
                JsonRpcResponse<TaskResult> rpcResponse = restClient.post()
                        .uri(agentUrl)
                        .body(rpcRequest)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

                rpcResponse.result().artifacts().stream()
                        .flatMap(a -> a.parts().stream())
                        .filter(p -> "text".equals(p.type()))
                        .map(TaskPart::text)
                        .forEach(result::append);
            }

            System.out.println("[Orchestrator] Combined output: " + result);
        };
    }
}
