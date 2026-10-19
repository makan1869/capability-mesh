package nl.makan1869.capabilitymesh.echoprinter;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class EchoTools {

    @McpTool(
            name = "echo",
            description = "Echoes back the character it is given. Only accepts a single letter (a-z, A-Z) or digit (0-9).")
    public String echo(
            @McpToolParam(description = "A single letter (a-z, A-Z) or digit (0-9)", required = true)
                    String character) {
        if (character == null || !character.matches("[a-zA-Z0-9]")) {
            throw new IllegalArgumentException("Input must be a single letter (a-z, A-Z) or digit (0-9)");
        }
        return character;
    }
}
