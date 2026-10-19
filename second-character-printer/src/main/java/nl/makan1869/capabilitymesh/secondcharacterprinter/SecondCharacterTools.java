package nl.makan1869.capabilitymesh.secondcharacterprinter;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class SecondCharacterTools {

    @McpTool(name = "second_character", description = "Returns the second character (index 1) of the given string")
    public String secondCharacter(
            @McpToolParam(description = "The string to extract the second character from", required = true) String text) {
        if (text == null || text.length() < 2) {
            throw new IllegalArgumentException("Input must have at least 2 characters");
        }
        return text.substring(1, 2);
    }
}
