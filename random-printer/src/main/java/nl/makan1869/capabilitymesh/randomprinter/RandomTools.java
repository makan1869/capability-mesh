package nl.makan1869.capabilitymesh.randomprinter;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class RandomTools {

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @McpTool(name = "random_character", description = "Ignores its input and returns a random letter (A-Z, a-z)")
    public String randomCharacter(
            @McpToolParam(description = "Input string; its content is ignored", required = true) String text) {
        int index = ThreadLocalRandom.current().nextInt(LETTERS.length());
        return String.valueOf(LETTERS.charAt(index));
    }
}
