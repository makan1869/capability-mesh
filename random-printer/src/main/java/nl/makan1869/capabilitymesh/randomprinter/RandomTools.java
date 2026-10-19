package nl.makan1869.capabilitymesh.randomprinter;

import java.util.Random;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class RandomTools {

    /** Every printable, non-space ASCII character a standard keyboard can produce: '!' (0x21) through '~' (0x7E). */
    private static final String PRINTABLE_CHARACTERS = buildPrintableCharacters();

    private static final int RANDOM_PREFIX_LENGTH = 5;

    /** The given character always ends up here: index RANDOM_PREFIX_LENGTH of a RANDOM_PREFIX_LENGTH + 1 length string. */
    private static final int GENERATED_LENGTH = RANDOM_PREFIX_LENGTH + 1;

    private final Random random;

    public RandomTools() {
        this(new Random());
    }

    RandomTools(Random random) {
        this.random = random;
    }

    @McpTool(
            name = "random_character",
            description = "Builds a 6-character string: 5 random printable characters (letters, digits, and "
                    + "symbols) followed by the given character, which always ends up at index 5. Returns the "
                    + "character at a random index from 0 to 5.")
    public String randomCharacter(
            @McpToolParam(description = "The character to place at the end of the generated string", required = true)
                    String character) {
        if (character == null || character.length() != 1) {
            throw new IllegalArgumentException("Input must be a single character");
        }

        StringBuilder generated = new StringBuilder(GENERATED_LENGTH);
        for (int i = 0; i < RANDOM_PREFIX_LENGTH; i++) {
            generated.append(PRINTABLE_CHARACTERS.charAt(random.nextInt(PRINTABLE_CHARACTERS.length())));
        }
        generated.append(character);

        int index = random.nextInt(GENERATED_LENGTH);
        return String.valueOf(generated.charAt(index));
    }

    private static String buildPrintableCharacters() {
        StringBuilder sb = new StringBuilder();
        for (char c = '!'; c <= '~'; c++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
