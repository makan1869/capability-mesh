package nl.makan1869.capabilitymesh.example1;

import nl.makan1869.capabilitymesh.example1.a2a.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Smart orchestrator that discovers all available printer agents, then asks an
 * LLM to select and order the subset needed to produce the target string.
 *
 * <p>Flow:
 * <ol>
 *   <li>Fetch every agent's {@link AgentCard} to build a capability registry.</li>
 *   <li>Send the registry and the target string to the LLM and ask it to return
 *       an ordered list of agent URLs — using structured output so no parsing is needed.</li>
 *   <li>Invoke only the selected agents, in the LLM-determined order.</li>
 * </ol>
 */
@Component
public class SmartPrinterOrchestrator {

    private static final String TARGET = "ABC";

    private static final List<String> ALL_AGENT_URLS = List.of(
            "http://localhost:8080/api/agents/printer-a",
            "http://localhost:8080/api/agents/printer-b",
            "http://localhost:8080/api/agents/printer-c",
            "http://localhost:8080/api/agents/printer-d",
            "http://localhost:8080/api/agents/printer-e",
            "http://localhost:8080/api/agents/printer-smart"

    );

    private final RestClient restClient = RestClient.create();
    private final ChatClient chatClient;

    public SmartPrinterOrchestrator(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Structured output type: the LLM returns an ordered list of invocations, each
     * naming the agent URL to call and the letter it should be asked to produce.
     */
    record AgentInvocation(String agentUrl, String letter) {}

    record AgentSelection(List<AgentInvocation> invocations) {}

    @Bean
    @Order(2)
    ApplicationRunner smartOrchestrate() {
        return args -> {
            System.out.println("[SmartOrchestrator] Discovering agents...");

            // Step 1: discover all agent cards
            List<AgentCard> allCards = new ArrayList<>();
            for (String url : ALL_AGENT_URLS) {
                AgentCard card = restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(AgentCard.class);
                allCards.add(card);
                System.out.printf("[SmartOrchestrator] Discovered: %s — %s%n", card.name(), card.description());
            }

            // Step 2: ask the LLM to select and order the agents
            String agentSummary = buildAgentSummary(allCards);
            String prompt = """
                    You are an agent orchestrator. Your goal is to produce the string "%s" by selecting
                    agents from the list below and invoking them in the correct order.

                    Most agents are dedicated to one fixed letter and print that letter regardless of
                    what you ask for. The Smart Printer Agent can produce whichever single uppercase
                    letter you tell it to — prefer a dedicated agent when one exists for a letter, and
                    use the Smart Printer Agent only for letters that have no dedicated agent.

                    For each position in the target string, return the agent URL to call and the single
                    uppercase letter you are asking it to produce. No extras, no duplicates unless the
                    target requires them.

                    Available agents:
                    %s
                    """.formatted(TARGET, agentSummary);

            AgentSelection selection = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .entity(AgentSelection.class);

            System.out.printf("[SmartOrchestrator] LLM selected %d invocations: %s%n",
                    selection.invocations().size(), selection.invocations());

            // Step 3: invoke selected agents in LLM-determined order
            StringBuilder result = new StringBuilder();
            for (AgentInvocation invocation : selection.invocations()) {
                String taskId = UUID.randomUUID().toString();
                JsonRpcRequest<TaskSendParams> rpcRequest = JsonRpcRequest.of(
                        taskId,
                        "tasks/send",
                        new TaskSendParams(taskId, TaskMessage.user(invocation.letter()))
                );

                JsonRpcResponse<TaskResult> rpcResponse = restClient.post()
                        .uri(invocation.agentUrl())
                        .body(rpcRequest)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

                rpcResponse.result().artifacts().stream()
                        .flatMap(a -> a.parts().stream())
                        .filter(p -> "text".equals(p.type()))
                        .map(TaskPart::text)
                        .forEach(result::append);
            }

            System.out.println("[SmartOrchestrator] Combined output: " + result);
        };
    }

    private String buildAgentSummary(List<AgentCard> cards) {
        StringBuilder sb = new StringBuilder();
        for (AgentCard card : cards) {
            String skillDesc = card.skills().stream()
                    .map(s -> s.name() + " (" + s.description() + ")")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("none");
            sb.append("- Name: ").append(card.name())
              .append(", URL: ").append(card.url())
              .append(", Skills: ").append(skillDesc)
              .append("\n");
        }
        return sb.toString();
    }
}
