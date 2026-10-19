package nl.makan1869.capabilitymesh.nextcharprinter;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class NextCharacterTools {

    @McpTool(name = "next_character",
            description = "Given a single letter, returns the next letter of the alphabet, cycling from 'Z' back to 'A' (and 'z' back to 'a')")
    public String nextCharacter(
            @McpToolParam(description = "A single letter (A-Z or a-z)", required = true) String character) {
        if (character == null || character.length() != 1 || !Character.isLetter(character.charAt(0))) {
            throw new IllegalArgumentException("Input must be a single letter (A-Z or a-z)");
        }

        char c = character.charAt(0);
        char base = Character.isUpperCase(c) ? 'A' : 'a';
        char next = (char) (base + (c - base + 1) % 26);
        return String.valueOf(next);
    }
}
