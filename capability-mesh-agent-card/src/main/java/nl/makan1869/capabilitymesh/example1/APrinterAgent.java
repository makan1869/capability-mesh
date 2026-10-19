package nl.makan1869.capabilitymesh.example1;

import nl.makan1869.capabilitymesh.example1.a2a.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * A2A-compliant agent that wraps {@link APrinter}.
 *
 * Agent Card: GET  /api/agents/printer-a
 * Task send:  POST /api/agents/printer-a  (JSON-RPC 2.0, method "tasks/send")
 */
@RestController
@RequestMapping("/api/agents/printer-a")
public class APrinterAgent {

    static final AgentCard AGENT_CARD = new AgentCard(
            "APrinter Agent",
            "An agent that prints the letter A to standard output.",
            "http://localhost:8080/api/agents/printer-a",
            "1.0.0",
            new AgentCapabilities(false, false),
            List.of(new AgentSkill(
                    "print-a",
                    "Print A",
                    "Prints the letter A to standard output.",
                    List.of("print", "output", "letter"),
                    List.of("print", "print A")
            ))
    );

    private final APrinter aPrinter;

    public APrinterAgent(APrinter aPrinter) {
        this.aPrinter = aPrinter;
    }

    @GetMapping
    public AgentCard agentCard() {
        return AGENT_CARD;
    }

    @PostMapping
    public JsonRpcResponse<TaskResult> handleTask(@RequestBody JsonRpcRequest<TaskSendParams> request) {
        aPrinter.print();

        TaskResult result = new TaskResult(
                request.params().id(),
                TaskStatus.completed(),
                List.of(TaskArtifact.of("output", "A"))
        );
        return new JsonRpcResponse<>("2.0", request.id(), result);
    }
}
