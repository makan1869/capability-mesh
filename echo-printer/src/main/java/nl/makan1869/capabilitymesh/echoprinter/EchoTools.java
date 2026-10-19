package nl.makan1869.capabilitymesh.echoprinter;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class EchoTools {

    @McpTool(name = "echo", description = "Echoes back exactly the character it is given")
    public String echo(@McpToolParam(description = "The character to echo back", required = true) String character) {
        return character;
    }
}
