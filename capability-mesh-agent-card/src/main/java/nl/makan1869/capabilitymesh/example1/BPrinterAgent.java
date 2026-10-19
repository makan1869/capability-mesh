package nl.makan1869.capabilitymesh.example1;

import nl.makan1869.capabilitymesh.example1.a2a.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * A2A-compliant agent that wraps {@link BPrinter}.
 *
 * Agent Card: GET  /api/agents/printer-b
 * Task send:  POST /api/agents/printer-b  (JSON-RPC 2.0, method "tasks/send")
 */
@RestController
@RequestMapping("/api/agents/printer-b")
public class BPrinterAgent {

    static final AgentCard AGENT_CARD = new AgentCard(
            "BPrinter Agent",
            "An agent that prints the letter B to standard output.",
            "http://localhost:8080/api/agents/printer-b",
            "1.0.0",
            new AgentCapabilities(false, false),
            List.of(new AgentSkill(
                    "print-b",
                    "Print B",
                    "Prints the letter B to standard output.",
                    List.of("print", "output", "letter"),
                    List.of("print", "print B")
            ))
    );

    private final BPrinter bPrinter;

    public BPrinterAgent(BPrinter bPrinter) {
        this.bPrinter = bPrinter;
    }

    @GetMapping
    public AgentCard agentCard() {
        return AGENT_CARD;
    }

    @PostMapping
    public JsonRpcResponse<TaskResult> handleTask(@RequestBody JsonRpcRequest<TaskSendParams> request) {
        bPrinter.print();

        TaskResult result = new TaskResult(
                request.params().id(),
                TaskStatus.completed(),
                List.of(TaskArtifact.of("output", "B"))
        );
        return new JsonRpcResponse<>("2.0", request.id(), result);
    }
}
