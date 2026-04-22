package nl.makan1869.capabilitymesh.example1;

import nl.makan1869.capabilitymesh.example1.a2a.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * A2A-compliant agent that wraps {@link CPrinter}.
 *
 * Agent Card: GET  /api/agents/printer-c
 * Task send:  POST /api/agents/printer-c  (JSON-RPC 2.0, method "tasks/send")
 */
@RestController
@RequestMapping("/api/agents/printer-c")
public class CPrinterAgent {

    static final AgentCard AGENT_CARD = new AgentCard(
            "CPrinter Agent",
            "An agent that prints the letter C to standard output.",
            "http://localhost:8080/api/agents/printer-c",
            "1.0.0",
            new AgentCapabilities(false, false),
            List.of(new AgentSkill(
                    "print-c",
                    "Print C",
                    "Prints the letter C to standard output.",
                    List.of("print", "output", "letter"),
                    List.of("print", "print C")
            ))
    );

    private final CPrinter cPrinter;

    public CPrinterAgent(CPrinter cPrinter) {
        this.cPrinter = cPrinter;
    }

    @GetMapping
    public AgentCard agentCard() {
        return AGENT_CARD;
    }

    @PostMapping
    public JsonRpcResponse<TaskResult> handleTask(@RequestBody JsonRpcRequest<TaskSendParams> request) {
        cPrinter.print();

        TaskResult result = new TaskResult(
                request.params().id(),
                TaskStatus.completed(),
                List.of(TaskArtifact.of("output", "C"))
        );
        return new JsonRpcResponse<>("2.0", request.id(), result);
    }
}
