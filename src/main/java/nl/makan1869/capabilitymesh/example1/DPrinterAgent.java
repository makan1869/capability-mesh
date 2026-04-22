package nl.makan1869.capabilitymesh.example1;

import nl.makan1869.capabilitymesh.example1.a2a.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * A2A-compliant agent that wraps {@link DPrinter}.
 *
 * Agent Card: GET  /api/agents/printer-d
 * Task send:  POST /api/agents/printer-d  (JSON-RPC 2.0, method "tasks/send")
 */
@RestController
@RequestMapping("/api/agents/printer-d")
public class DPrinterAgent {

    static final AgentCard AGENT_CARD = new AgentCard(
            "DPrinter Agent",
            "An agent that prints the letter D to standard output.",
            "http://localhost:8080/api/agents/printer-d",
            "1.0.0",
            new AgentCapabilities(false, false),
            List.of(new AgentSkill(
                    "print-d",
                    "Print D",
                    "Prints the letter D to standard output.",
                    List.of("print", "output", "letter"),
                    List.of("print", "print D")
            ))
    );

    private final DPrinter dPrinter;

    public DPrinterAgent(DPrinter dPrinter) {
        this.dPrinter = dPrinter;
    }

    @GetMapping
    public AgentCard agentCard() {
        return AGENT_CARD;
    }

    @PostMapping
    public JsonRpcResponse<TaskResult> handleTask(@RequestBody JsonRpcRequest<TaskSendParams> request) {
        dPrinter.print();

        TaskResult result = new TaskResult(
                request.params().id(),
                TaskStatus.completed(),
                List.of(TaskArtifact.of("output", "D"))
        );
        return new JsonRpcResponse<>("2.0", request.id(), result);
    }
}
