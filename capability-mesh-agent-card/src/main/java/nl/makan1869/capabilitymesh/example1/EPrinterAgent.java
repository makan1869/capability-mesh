package nl.makan1869.capabilitymesh.example1;

import nl.makan1869.capabilitymesh.example1.a2a.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * A2A-compliant agent that wraps {@link EPrinter}.
 *
 * Agent Card: GET  /api/agents/printer-e
 * Task send:  POST /api/agents/printer-e  (JSON-RPC 2.0, method "tasks/send")
 */
@RestController
@RequestMapping("/api/agents/printer-e")
public class EPrinterAgent {

    static final AgentCard AGENT_CARD = new AgentCard(
            "EPrinter Agent",
            "An AI-powered agent that uses generative AI to produce and print the letter E.",
            "http://localhost:8080/api/agents/printer-e",
            "1.0.0",
            new AgentCapabilities(false, false),
            List.of(new AgentSkill(
                    "print-e",
                    "Print E",
                    "Uses a generative AI prompt to produce and print the letter E to standard output.",
                    List.of("print", "output", "letter", "ai", "generative"),
                    List.of("print", "print E")
            ))
    );

    private final EPrinter ePrinter;

    public EPrinterAgent(EPrinter ePrinter) {
        this.ePrinter = ePrinter;
    }

    @GetMapping
    public AgentCard agentCard() {
        return AGENT_CARD;
    }

    @PostMapping
    public JsonRpcResponse<TaskResult> handleTask(@RequestBody JsonRpcRequest<TaskSendParams> request) {
        ePrinter.print();

        TaskResult result = new TaskResult(
                request.params().id(),
                TaskStatus.completed(),
                List.of(TaskArtifact.of("output", "E"))
        );
        return new JsonRpcResponse<>("2.0", request.id(), result);
    }
}
