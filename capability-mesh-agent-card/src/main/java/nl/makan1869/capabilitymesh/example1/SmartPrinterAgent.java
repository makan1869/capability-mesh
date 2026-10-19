package nl.makan1869.capabilitymesh.example1;

import nl.makan1869.capabilitymesh.example1.a2a.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * A2A-compliant agent that wraps {@link EPrinter}.
 * Agent Card: GET  /api/agents/printer-e
 * Task send:  POST /api/agents/printer-e  (JSON-RPC 2.0, method "tasks/send")
 */
@RestController
@RequestMapping("/api/agents/printer-smart")
public class SmartPrinterAgent {

    static final AgentCard AGENT_CARD = new AgentCard(
            "Smart Printer Agent",
            "An AI-powered agent that uses generative AI to produce and print the letter Requested.",
            "http://localhost:8080/api/agents/printer-smart",
            "1.0.0",
            new AgentCapabilities(false, false),
            List.of(new AgentSkill(
                    "printer-smart",
                    "Smart Print",
                    "Uses a generative AI prompt to produce and print the letter requested to standard output.",
                    List.of("print", "output", "letter", "ai", "generative"),
                    List.of("print", "print character requested to standard output.")
            ))
    );

    private final SmartPrinter smartPrinter;

    public SmartPrinterAgent(SmartPrinter smartPrinter) {
        this.smartPrinter = smartPrinter;
    }

    @GetMapping
    public AgentCard agentCard() {
        return AGENT_CARD;
    }

    @PostMapping
    public JsonRpcResponse<TaskResult> handleTask(@RequestBody JsonRpcRequest<TaskSendParams> request) {
        Character requested = extractRequestedCharacter(request.params());
        smartPrinter.print(requested);

        TaskResult result = new TaskResult(
                request.params().id(),
                TaskStatus.completed(),
                List.of(TaskArtifact.of("output", requested.toString()))
        );
        return new JsonRpcResponse<>("2.0", request.id(), result);
    }

    private Character extractRequestedCharacter(TaskSendParams params) {
        String text = params.message().parts().stream()
                .filter(part -> "text".equals(part.type()))
                .map(TaskPart::text)
                .filter(t -> t != null && !t.isBlank())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No character requested: task message has no text part"));

        return Character.toUpperCase(text.trim().charAt(0));
    }
}
